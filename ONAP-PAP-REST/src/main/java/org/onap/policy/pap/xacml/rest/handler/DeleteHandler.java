/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.pap.xacml.rest.model.RemoveGroupPolicy;
import org.onap.policy.pap.xacml.rest.util.JPAUtils;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteHandler {

    private static CommonClassDao commonClassDao;

    @Autowired
    public DeleteHandler(CommonClassDao commonClassDao) {
        DeleteHandler.commonClassDao = commonClassDao;
    }

    public DeleteHandler() {
        // Default Constructor
    }

    private OnapPDPGroup newgroup;
    private static final Logger LOGGER = FlexLogger.getLogger(DeleteHandler.class);
    private static final String POLICY_IN_PDP = "PolicyInPDP";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String UNKNOWN = "unknown";
    private static final String SUCCESS = "success";
    private static final String OPERATION = "operation";
    private static final String CONFIG = "Config_";
    private static final String REGEX = "[0-9a-zA-Z._]*";
    private static final String DELETE = "delete";
    private static final String ACTION = "Action_";

    /**
     * Do API delete from PAP.
     *
     * @param request the request
     * @param response the response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doApiDeleteFromPap(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        String removeXmlExtension;
        int currentVersion;
        String removeVersionExtension;
        String splitPolicyName = null;
        String[] split = null;
        String status = ERROR;
        PolicyEntity policyEntity = null;
        JPAUtils jpaUtils = null;

        try {
            jpaUtils = JPAUtils.getJPAUtilsInstance();
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "doAPIDeleteFromPAP",
                    " Could not create JPAUtils instance on the PAP");
            response.addHeader(ERROR, "jpautils");
            response.addHeader(OPERATION, DELETE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (jpaUtils.dbLockdownIgnoreErrors()) {
            PolicyLogger.warn("Policies are locked down");
            response.addHeader(OPERATION, DELETE);
            response.addHeader("lockdown", "true");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        String policyEntityQuery = null;
        try {
            if (policyName.endsWith(".xml")) {
                removeXmlExtension = policyName.replace(".xml", "");
                currentVersion =
                        Integer.parseInt(removeXmlExtension.substring(removeXmlExtension.lastIndexOf('.') + 1));
                removeVersionExtension = removeXmlExtension.substring(0, removeXmlExtension.lastIndexOf('.'));
                boolean queryCheck = true;
                if ("All Versions".equalsIgnoreCase(policy.getDeleteCondition())) {
                    if (policyName.contains(CONFIG)) {
                        splitPolicyName = removeVersionExtension.replace(".Config_", ":Config_");
                    } else if (policyName.contains(ACTION)) {
                        splitPolicyName = removeVersionExtension.replace(".Action_", ":Action_");
                    } else if (policyName.contains("Decision_")) {
                        splitPolicyName = removeVersionExtension.replace(".Decision_", ":Decision_");
                    }
                    if (splitPolicyName != null) {
                        split = splitPolicyName.split(":");
                    } else {
                        PolicyLogger.error(MessageCodes.ERROR_UNKNOWN
                                + "Failed to delete the policy. Please, provide the valid policyname.");
                        response.addHeader(ERROR, UNKNOWN);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                    policyEntityQuery =
                            "SELECT p FROM PolicyEntity p WHERE p.policyName LIKE :pName and p.scope=:pScope";
                } else if ("Current Version".equalsIgnoreCase(policy.getDeleteCondition())) {
                    if (policyName.contains(CONFIG)) {
                        splitPolicyName = policyName.replace(".Config_", ":Config_");
                    } else if (policyName.contains(ACTION)) {
                        splitPolicyName = policyName.replace(".Action_", ":Action_");
                    } else if (policyName.contains("Decision_")) {
                        splitPolicyName = policyName.replace(".Decision_", ":Decision_");
                    }
                    split = splitPolicyName.split(":");
                    queryCheck = false;
                    policyEntityQuery = "SELECT p FROM PolicyEntity p WHERE p.policyName=:pName and p.scope=:pScope";
                }
                SimpleBindings params = new SimpleBindings();
                if (queryCheck) {
                    params.put("pName", "%" + split[1] + "%");
                } else {
                    params.put("pName", split[1]);
                }

                params.put("pScope", split[0]);
                List<?> peResult = commonClassDao.getDataByQuery(policyEntityQuery, params);
                if (!peResult.isEmpty()) {
                    String getPolicyVersion = "Select p from PolicyVersion p where p.policyName=:pname";
                    SimpleBindings pvParams = new SimpleBindings();
                    pvParams.put("pname", removeVersionExtension.replace(".", File.separator));
                    List<?> pvResult = commonClassDao.getDataByQuery(getPolicyVersion, pvParams);
                    PolicyVersion polVersion = (PolicyVersion) pvResult.get(0);
                    int newVersion = 0;
                    if ("All Versions".equalsIgnoreCase(policy.getDeleteCondition())) {
                        boolean groupCheck = checkPolicyGroupEntity(peResult);
                        if (!groupCheck) {
                            for (Object peData : peResult) {
                                policyEntity = (PolicyEntity) peData;
                                status = deletePolicyEntityData(policyEntity);
                            }
                        } else {
                            status = POLICY_IN_PDP;
                        }
                        switch (status) {
                            case ERROR:
                                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE
                                        + "Exception Occured while deleting the Entity from Database.");
                                response.addHeader(ERROR, UNKNOWN);
                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return;
                            case POLICY_IN_PDP:
                                PolicyLogger.error(MessageCodes.GENERAL_WARNING
                                        + "Policy can't be deleted, it is active in PDP Groups.");
                                response.addHeader(ERROR, POLICY_IN_PDP);
                                response.setStatus(HttpServletResponse.SC_CONFLICT);
                                return;
                            default:
                                try {
                                    policyVersionDeleted = true;
                                    commonClassDao.delete(polVersion);
                                } catch (Exception e) {
                                    LOGGER.error(e.getMessage(), e);
                                    policyVersionDeleted = false;
                                }
                                break;
                        }
                    } else if ("Current Version".equalsIgnoreCase(policy.getDeleteCondition())) {
                        boolean groupCheck = checkPolicyGroupEntity(peResult);
                        if (!groupCheck) {
                            policyEntity = (PolicyEntity) peResult.get(0);
                            status = deletePolicyEntityData(policyEntity);
                        } else {
                            status = POLICY_IN_PDP;
                        }

                        if (ERROR.equals(status)) {
                            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE
                                    + "Exception Occured while deleting the Entity from Database.");
                            response.addHeader(ERROR, UNKNOWN);
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            return;
                        } else if (POLICY_IN_PDP.equals(status)) {
                            PolicyLogger.error(MessageCodes.GENERAL_WARNING
                                    + "Policy can't be deleted, it is active in PDP Groups.");
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
                                polVersion.setActiveVersion(newVersion);
                                polVersion.setHigherVersion(newVersion);
                                try {
                                    policyVersionDeleted = true;
                                    commonClassDao.save(polVersion);
                                } catch (Exception e) {
                                    LOGGER.error(e.getMessage(), e);
                                    policyVersionDeleted = false;
                                }
                            } else {
                                try {
                                    policyVersionDeleted = true;
                                    commonClassDao.delete(polVersion);
                                } catch (Exception e) {
                                    LOGGER.error(e.getMessage(), e);
                                    policyVersionDeleted = false;
                                }
                            }
                        }
                    }
                } else {
                    PolicyLogger.error(MessageCodes.ERROR_UNKNOWN
                            + "Failed to delete the policy for an unknown reason.  Check the file system and other logs"
                            + " for further information.");
                    response.addHeader(ERROR, UNKNOWN);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
            response.addHeader(ERROR, "deleteDB");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (policyVersionDeleted) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("successMapKey", SUCCESS);
            response.addHeader(OPERATION, DELETE);
        } else {
            PolicyLogger.error(MessageCodes.ERROR_UNKNOWN
                    + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for "
                    + "further information.");
            response.addHeader(ERROR, UNKNOWN);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete policy entity data.
     *
     * @param policyEntity the policy entity
     * @return the string
     */
    public static String deletePolicyEntityData(PolicyEntity policyEntity) {
        PolicyElasticSearchController controller = new PolicyElasticSearchController();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        String policyName = policyEntity.getPolicyName();
        try {
            if (policyName.contains("CONFIG") || policyName.contains("Decision_MS_")) {
                commonClassDao.delete(policyEntity.getConfigurationData());
            } else if (policyName.contains(ACTION)) {
                commonClassDao.delete(policyEntity.getActionBodyEntity());
            }
            String searchPolicyName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
            policyData.setNewFileName(searchPolicyName);
            controller.deleteElk(policyData);
            commonClassDao.delete(policyEntity);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * Check policy group entity.
     *
     * @param peResult the pe result
     * @return true, if successful
     */
    public static boolean checkPolicyGroupEntity(List<?> peResult) {
        String groupEntityquery = "from PolicyGroupEntity where policyid = :policyEntityId";
        for (Object peData : peResult) {
            PolicyEntity policyEntity = (PolicyEntity) peData;
            SimpleBindings geParams = new SimpleBindings();
            geParams.put("policyEntityId", policyEntity.getPolicyId());
            List<Object> groupobject = commonClassDao.getDataByQuery(groupEntityquery, geParams);
            if (!groupobject.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Do API delete from PDP.
     *
     * @param request the request
     * @param response the response
     * @param loggingContext the logging context
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doApiDeleteFromPdp(HttpServletRequest request, HttpServletResponse response,
            OnapLoggingContext loggingContext) throws IOException {

        String groupId = request.getParameter("groupId");

        if (groupId != null && !groupId.matches(REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(ERROR, ERROR);
            response.addHeader(MESSAGE, "Group Id is not valid");
            return;
        }
        String requestId = request.getHeader("X-ECOMP-RequestID");
        String polName = request.getParameter("policyName");
        LOGGER.info("JSON request from API to Delete Policy from the PDP - " + polName + " -RequestId - " + requestId);

        // for PUT operations the group may or may not need to exist before the operation can be
        // done
        OnapPDPGroup group = null;
        try {
            group = XACMLPapServlet.getPAPEngine().getGroup(groupId);
        } catch (PAPException e) {
            LOGGER.error("Exception occured While PUT operation is performing for PDP Group" + " -  RequestId - "
                    + requestId + e);
        }
        if (group == null) {
            String message = "Unknown groupId '" + groupId + "'.";
            LOGGER.error(MessageCodes.ERROR_DATA_ISSUE + " - RequestId -  " + requestId + " - " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            response.addHeader(ERROR, "UnknownGroup");
            response.addHeader(MESSAGE, message);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        loggingContext.setServiceName("API:PAP.deletPolicyFromPDPGroup");
        RemoveGroupPolicy removePolicy = new RemoveGroupPolicy((StdPDPGroup) group);
        removePolicy.prepareToRemove();
        List<String> policyIdList = Arrays.asList(polName.split(","));
        for (String policyName : policyIdList) {
            if (!policyName.endsWith("xml")) {
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid policyName... "
                        + "policyName must be the full name of the file to be deleted including version and extension";
                LOGGER.error(message + "   - RequestId - " + requestId);
                response.addHeader(ERROR, message);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            PDPPolicy policy = group.getPolicy(policyName);
            if (policy == null) {
                String message =
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy does not exist on the PDP - " + policyName;
                LOGGER.error(message + " - RequestId   - " + requestId);
                response.addHeader(ERROR, message);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if (StringUtils.indexOfAny(policy.getId(), "Config_MS_", "BRMS_Param") >= 0) {
                preSafetyCheck(policy);
            }
            LOGGER.info("Preparing to remove policy from group: " + group.getId() + "- RequestId - " + requestId);
            removePolicy.removePolicy(policy);
        }
        OnapPDPGroup updatedGroup = removePolicy.getUpdatedObject();
        String userId = request.getParameter("userId");
        String responseString = deletePolicyFromPdpGroup(updatedGroup, loggingContext, userId);

        switch (responseString) {
            case SUCCESS:
                loggingContext.transactionEnded();
                LOGGER.info("Policy successfully removed from PDP - " + polName + " -   RequestId - " + requestId);
                PolicyLogger.audit("Policy successfully deleted!");
                response.setStatus(HttpServletResponse.SC_OK);
                response.addHeader("successMapKey", SUCCESS);
                response.addHeader(OPERATION, DELETE);
                break;
            case "No Group":
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Group update had bad input.";
                LOGGER.error(message + " - RequestId - " + requestId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(ERROR, "groupUpdate");
                response.addHeader(MESSAGE, message);
                break;
            case "DB Error":
                LOGGER.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database"
                        + " - RequestId - " + requestId);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader(ERROR, "deleteDB");
                break;
            default:
                LOGGER.error(MessageCodes.ERROR_UNKNOWN
                        + " Failed to delete the policy for an unknown reason.  Check the file system and other logs "
                        + "for " + "further information.");
                response.addHeader(ERROR, UNKNOWN);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                break;
        }
    }

    private String deletePolicyFromPdpGroup(OnapPDPGroup group, OnapLoggingContext loggingContext, String userId) {
        PolicyDBDaoTransaction acPutTransaction = XACMLPapServlet.getDbDaoTransaction();
        String response = null;
        loggingContext.setServiceName("API:PAP.DeleteHandler");
        OnapPDPGroup existingGroup = null;
        try {
            existingGroup = XACMLPapServlet.getPAPEngine().getGroup(group.getId());
        } catch (PAPException e1) {
            PolicyLogger.error("Exception occured While Deleting Policy From PDP Group" + e1);
        }
        if (!(group instanceof StdPDPGroup) || existingGroup == null
                || !(group.getId().equals(existingGroup.getId()))) {
            String existingId = null;
            if (existingGroup != null) {
                existingId = existingGroup.getId();
            }
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id=" + existingId
                    + " objectFromJSON=" + group);
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
            acPutTransaction.updateGroup(group, "XACMLPapServlet.doDelete", userId);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet",
                    " Error while updating group in the database: " + "group=" + existingGroup.getId());
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
            response = SUCCESS;
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

    /**
     * Gets the single instance of DeleteHandler.
     *
     * @return single instance of DeleteHandler
     */
    public static DeleteHandler getInstance() {
        try {
            Class<?> deleteHandler = Class
                    .forName(XACMLProperties.getProperty("deletePolicy.impl.className", DeleteHandler.class.getName()));
            return (DeleteHandler) deleteHandler.newInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}
