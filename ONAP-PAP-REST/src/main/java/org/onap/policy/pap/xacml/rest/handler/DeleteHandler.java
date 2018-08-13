/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.policy.pap.xacml.rest.handler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.pap.xacml.rest.model.RemoveGroupPolicy;
import org.onap.policy.pap.xacml.rest.util.JPAUtils;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPGroup;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;

public class DeleteHandler {

    private OnapPDPGroup newgroup;
    private static Logger logger = FlexLogger.getLogger(DeleteHandler.class);
    public static final String POLICY_IN_PDP = "PolicyInPDP";
    public static final String ERROR = "error";
    public static final String UNKNOWN = "unknown";
    private static final String REGEX = "[0-9a-zA-Z._]*";

    public void doAPIDeleteFromPAP(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        // get the request content into a String
        String json = null;
        java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
        scanner.useDelimiter("\\A");
        json = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        PolicyLogger.info("JSON request from API to Delete Policy from the PAP: " + json);
        // convert Object sent as JSON into local object
        StdPAPPolicy policy = PolicyUtils.jsonStringToObject(json, StdPAPPolicy.class);
        String policyName = policy.getPolicyName();
        boolean policyVersionDeleted = false;
        String removeXMLExtension;
        int currentVersion;
        String removeVersionExtension;
        String splitPolicyName = null;
        String[] split = null;
        String status = ERROR;
        PolicyEntity policyEntity = null;
        JPAUtils jpaUtils = null;

        String papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
        String papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
        String papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
        String papDbPassword = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD);
        Connection con = null;

        try {
            jpaUtils = JPAUtils.getJPAUtilsInstance(XACMLPapServlet.getEmf());
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet",
                    " Could not create JPAUtils instance on the PAP");
            response.addHeader(ERROR, "jpautils");
            response.addHeader("operation", "delete");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (jpaUtils.dbLockdownIgnoreErrors()) {
            PolicyLogger.warn("Policies are locked down");
            response.addHeader("operation", "delete");
            response.addHeader("lockdown", "true");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        EntityManager em = XACMLPapServlet.getEmf().createEntityManager();
        Query policyEntityQuery = null;
        try {
            if (policyName.endsWith(".xml")) {
                removeXMLExtension = policyName.replace(".xml", "");
                currentVersion =
                        Integer.parseInt(removeXMLExtension.substring(removeXMLExtension.lastIndexOf('.') + 1));
                removeVersionExtension = removeXMLExtension.substring(0, removeXMLExtension.lastIndexOf('.'));
                boolean queryCheck = true;
                if (policy.getDeleteCondition().equalsIgnoreCase("All Versions")) {
                    if (policyName.contains("Config_")) {
                        splitPolicyName = removeVersionExtension.replace(".Config_", ":Config_");
                    } else if (policyName.contains("Action_")) {
                        splitPolicyName = removeVersionExtension.replace(".Action_", ":Action_");
                    } else if (policyName.contains("Decision_")) {
                        splitPolicyName = removeVersionExtension.replace(".Decision_", ":Decision_");
                    }
                    if (splitPolicyName != null) {
                        split = splitPolicyName.split(":");
                    } else {
                        PolicyLogger.error(MessageCodes.ERROR_UNKNOWN +
                                "Failed to delete the policy. Please, provide the valid policyname.");
                        response.addHeader(ERROR, UNKNOWN);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                    policyEntityQuery = em.createQuery(
                            "SELECT p FROM PolicyEntity p WHERE p.policyName LIKE :pName and p.scope=:pScope");
                } else if (policy.getDeleteCondition().equalsIgnoreCase("Current Version")) {
                    if (policyName.contains("Config_")) {
                        splitPolicyName = policyName.replace(".Config_", ":Config_");
                    } else if (policyName.contains("Action_")) {
                        splitPolicyName = policyName.replace(".Action_", ":Action_");
                    } else if (policyName.contains("Decision_")) {
                        splitPolicyName = policyName.replace(".Decision_", ":Decision_");
                    }
                    split = splitPolicyName.split(":");
                    queryCheck = false;
                    policyEntityQuery = em.createQuery(
                            "SELECT p FROM PolicyEntity p WHERE p.policyName=:pName and p.scope=:pScope");
                }

                if (queryCheck) {
                    policyEntityQuery.setParameter("pName", "%" + split[1] + "%");
                } else {
                    policyEntityQuery.setParameter("pName", split[1]);
                }

                policyEntityQuery.setParameter("pScope", split[0]);
                List<?> peResult = policyEntityQuery.getResultList();
                if (!peResult.isEmpty()) {
                    Query getPolicyVersion = em.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
                    getPolicyVersion.setParameter("pname", removeVersionExtension.replace(".", File.separator));
                    List<?> pvResult = getPolicyVersion.getResultList();
                    PolicyVersion pVersion = (PolicyVersion) pvResult.get(0);
                    int newVersion = 0;
                    em.getTransaction().begin();
                    Class.forName(papDbDriver);
                    con = DriverManager.getConnection(papDbUrl, papDbUser, papDbPassword);

                    if (policy.getDeleteCondition().equalsIgnoreCase("All Versions")) {
                        boolean groupCheck = checkPolicyGroupEntity(con, peResult);
                        if (!groupCheck) {
                            for (Object peData : peResult) {
                                policyEntity = (PolicyEntity) peData;
                                status = deletePolicyEntityData(em, policyEntity);
                            }
                        } else {
                            status = POLICY_IN_PDP;
                        }
                        switch (status) {
                            case ERROR:
                                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE +
                                        "Exception Occured while deleting the Entity from Database.");
                                response.addHeader(ERROR, UNKNOWN);
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return;
                            case POLICY_IN_PDP:
                                PolicyLogger.error(MessageCodes.GENERAL_WARNING +
                                        "Policy can't be deleted, it is active in PDP Groups.");
                                response.addHeader(ERROR, POLICY_IN_PDP);
                                response.setStatus(HttpServletResponse.SC_CONFLICT);
                                return;
                            default:
                                try {
                                    policyVersionDeleted = true;
                                    em.remove(pVersion);
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                    policyVersionDeleted = false;
                                }
                                break;
                        }
                    } else if (policy.getDeleteCondition().equalsIgnoreCase("Current Version")) {
                        boolean groupCheck = checkPolicyGroupEntity(con, peResult);
                        if (!groupCheck) {
                            policyEntity = (PolicyEntity) peResult.get(0);
                            status = deletePolicyEntityData(em, policyEntity);
                        } else {
                            status = POLICY_IN_PDP;
                        }

                        if (ERROR.equals(status)) {
                            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE +
                                    "Exception Occured while deleting the Entity from Database.");
                            response.addHeader(ERROR, UNKNOWN);
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        } else if (POLICY_IN_PDP.equals(status)) {
                            PolicyLogger.error(MessageCodes.GENERAL_WARNING +
                                    "Policy can't be deleted, it is active in PDP Groups.");
                            response.addHeader(ERROR, POLICY_IN_PDP);
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            return;
                        } else {
                            if (currentVersion > 1) {
                                if (!peResult.isEmpty()) {
                                    for (Object object : peResult) {
                                        policyEntity = (PolicyEntity) object;
                                        String policyEntityName = policyEntity.getPolicyName().replace(".xml", "");
                                        int policyEntityVersion = Integer.parseInt(
                                                policyEntityName.substring(policyEntityName.lastIndexOf('.') + 1));
                                        if (policyEntityVersion > newVersion) {
                                            newVersion = policyEntityVersion - 1;
                                        }
                                    }
                                }
                                pVersion.setActiveVersion(newVersion);
                                pVersion.setHigherVersion(newVersion);
                                try {
                                    policyVersionDeleted = true;
                                    em.persist(pVersion);
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                    policyVersionDeleted = false;
                                }
                            } else {
                                try {
                                    policyVersionDeleted = true;
                                    em.remove(pVersion);
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                    policyVersionDeleted = false;
                                }
                            }
                        }
                    }
                } else {
                    PolicyLogger.error(MessageCodes.ERROR_UNKNOWN +
                            "Failed to delete the policy for an unknown reason.  Check the file system and other logs" +
                            " for further information.");
                    response.addHeader(ERROR, UNKNOWN);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
            response.addHeader(ERROR, "deleteDB");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } finally {
            em.close();
            if (con != null) {
                con.close();
            }
        }

        if (policyVersionDeleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("successMapKey", "success");
            response.addHeader("operation", "delete");
        } else {
            PolicyLogger.error(MessageCodes.ERROR_UNKNOWN +
                    "Failed to delete the policy for an unknown reason.  Check the file system and other logs for " +
                    "further information.");
            response.addHeader(ERROR, UNKNOWN);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static String deletePolicyEntityData(EntityManager em, PolicyEntity policyEntity) {
        PolicyElasticSearchController controller = new PolicyElasticSearchController();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        String policyName = policyEntity.getPolicyName();
        try {
            if (policyName.contains("Config_")) {
                em.remove(policyEntity.getConfigurationData());
            } else if (policyName.contains("Action_")) {
                em.remove(policyEntity.getActionBodyEntity());
            }
            String searchPolicyName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
            policyData.setNewFileName(searchPolicyName);
            controller.deleteElk(policyData);
            em.remove(policyEntity);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ERROR;
        }
        return "success";
    }

    public static boolean checkPolicyGroupEntity(Connection con, List<?> peResult) throws SQLException {
        for (Object peData : peResult) {
            PolicyEntity policyEntity = (PolicyEntity) peData;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "Select * from PolicyGroupEntity where policyid = '" + policyEntity.getPolicyId() + "'")) {
                boolean gEntityList = rs.next();
                if (gEntityList) {
                    return true;
                }
            }
        }
        return false;
    }

    public void doAPIDeleteFromPDP(HttpServletRequest request, HttpServletResponse response,
                                   ONAPLoggingContext loggingContext) throws IOException {

        String policyName = request.getParameter("policyName");
        String groupId = request.getParameter("groupId");
        String responseString = null;

        if (groupId != null && !groupId.matches(REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("error", ERROR);
            response.addHeader("message", "Group Id is not valid");
            return;
        }

        PolicyLogger.info("JSON request from API to Delete Policy from the PDP: " + policyName);

        // for PUT operations the group may or may not need to exist before the operation can be done
        OnapPDPGroup group = null;
        try {
            group = XACMLPapServlet.getPAPEngine().getGroup(groupId);
        } catch (PAPException e) {
            PolicyLogger.error("Exception occured While PUT operation is performing for PDP Group" + e);
        }
        if (group == null) {
            String message = "Unknown groupId '" + groupId + "'.";
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            response.addHeader(ERROR, "UnknownGroup");
            response.addHeader("message", message);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } else {
            loggingContext.setServiceName("API:PAP.deletPolicyFromPDPGroup");
            if (policyName.contains("xml")) {
                PolicyLogger
                        .debug("The full file name including the extension was provided for policyName.. continue.");
            } else {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid policyName... "
                        + "policyName must be the full name of the file to be deleted including version and extension";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Invalid policyName... "
                        + "policyName must be the full name of the file to be deleted including version and extension");
                response.addHeader(ERROR, message);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            RemoveGroupPolicy removePolicy = new RemoveGroupPolicy((StdPDPGroup) group);
            PDPPolicy policy = group.getPolicy(policyName);
            if (policy != null) {

                if ((policy.getId().contains("Config_MS_")) || (policy.getId().contains("BRMS_Param"))) {
                    if (preSafetyCheck(policy)) {
                        PolicyLogger.debug("Precheck Successful.");
                    }
                }
                PolicyLogger.info("Preparing to remove policy from group: " + group.getId());
                removePolicy.prepareToRemove(policy);
                OnapPDPGroup updatedGroup = removePolicy.getUpdatedObject();
                responseString = deletePolicyFromPDPGroup(updatedGroup, loggingContext);
            } else {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy does not exist on the PDP.";
                PolicyLogger.error(message);
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Policy does not exist on the PDP.");
                response.addHeader(ERROR, message);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        switch (responseString) {
            case "success":
                loggingContext.transactionEnded();
                PolicyLogger.info("Policy successfully deleted!");
                PolicyLogger.audit("Policy successfully deleted!");
                response.setStatus(HttpServletResponse.SC_OK);
                response.addHeader("successMapKey", "success");
                response.addHeader("operation", "delete");
                break;
            case "No Group":
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Group update had bad input.";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(ERROR, "groupUpdate");
                response.addHeader("message", message);
                break;
            case "DB Error":
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(ERROR, "deleteDB");
                break;
            default:
                PolicyLogger.error(MessageCodes.ERROR_UNKNOWN +
                        " Failed to delete the policy for an unknown reason.  Check the file system and other logs " +
                        "for " +
                        "further information.");
                response.addHeader(ERROR, UNKNOWN);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                break;
        }
    }

    private String deletePolicyFromPDPGroup(OnapPDPGroup group, ONAPLoggingContext loggingContext) {
        PolicyDBDaoTransaction acPutTransaction = XACMLPapServlet.getDbDaoTransaction();
        String response = null;
        loggingContext.setServiceName("API:PAP.DeleteHandler");
        OnapPDPGroup existingGroup = null;
        try {
            existingGroup = XACMLPapServlet.getPAPEngine().getGroup(group.getId());
        } catch (PAPException e1) {
            PolicyLogger.error("Exception occured While Deleting Policy From PDP Group" + e1);
        }
        if (!(group instanceof StdPDPGroup) || existingGroup == null ||
                !(group.getId().equals(existingGroup.getId()))) {
            String existingID = null;
            if (existingGroup != null) {
                existingID = existingGroup.getId();
            }
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id=" + existingID +
                    " objectFromJSON=" + group);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            response = "No Group";
            return response;
        }
        // The Path on the PAP side is not carried on the RESTful interface with the AC
        // (because it is local to the PAP)
        // so we need to fill that in before submitting the group for update
        ((StdPDPGroup) group).setDirectory(((StdPDPGroup) existingGroup).getDirectory());
        try {
            acPutTransaction.updateGroup(group, "XACMLPapServlet.doDelete");
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet",
                    " Error while updating group in the database: "
                            + "group=" + existingGroup.getId());
            response = "DB Error";
            return response;
        }
        try {
            XACMLPapServlet.getPAPEngine().updateGroup(group);
        } catch (PAPException e) {
            PolicyLogger.error("Exception occured While Updating PDP Groups" + e);
            response = "error in updateGroup method";
        }
        PolicyLogger.debug("Group '" + group.getId() + "' updated");
        acPutTransaction.commitTransaction();
        // Group changed, which might include changing the policies
        try {
            newgroup = existingGroup;
        } catch (Exception e) {
            PolicyLogger.error("Exception occured in Group Change Method" + e);
            response = "error in groupChanged method";
        }
        if (response == null) {
            response = "success";
            loggingContext.transactionEnded();
            PolicyLogger.audit("Policy successfully deleted!");
        }
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Ended");
        return response;
    }

    public OnapPDPGroup getDeletedGroup() {
        return newgroup;
    }

    public boolean preSafetyCheck(PDPPolicy policy) {
        return true;
    }

    public static DeleteHandler getInstance() {
        try {
            Class<?> deleteHandler = Class.forName(
                    XACMLProperties.getProperty("deletePolicy.impl.className", DeleteHandler.class.getName()));
            return (DeleteHandler) deleteHandler.newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
