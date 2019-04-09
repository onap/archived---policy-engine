/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import com.att.research.xacml.api.pap.PAPException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.onap.policy.pap.xacml.rest.handler.PushPolicyHandler;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

public class ConsoleAndApiService {

    private static final Logger LOGGER = FlexLogger.getLogger(ConsoleAndApiService.class);
    private static final Logger auditLogger = FlexLogger.getLogger("auditLogger");
    private static final String ADD_GROUP_ERROR = "addGroupError";
    private static final String REGEX = "[0-9a-zA-Z._, ]*";
    private static final String TRANSACTIONFAILED = "Transaction Failed - See Error.log";
    private static final String PAPSERVLETDOACPOST = "XACMLPapServlet.doACPost";
    private static final String ACPOSTCOMMITTRANS = "XACMLPapServlet doACPost commitTransaction";
    private static final String XACMLPAPSERVLET = "XACMLPapServlet";
    private static final String SUCCESS = "Success";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String POLICYID = "policyId";
    private static final String TRANSENDED = "Transaction Ended Successfully";

    /**
     * Requests from the Admin Console for operations not on single specific objects.
     *
     * @param request Servlet request
     * @param response Servlet response
     * @param groupId the group id
     * @param loggingContext the logging context
     * @param papEngine the pap engine
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doAcPost(HttpServletRequest request, HttpServletResponse response, String groupId,
            ONAPLoggingContext loggingContext, PAPPolicyEngine papEngine) throws IOException {
        PolicyDBDaoTransaction doAcPostTransaction = null;
        try {
            String groupName = request.getParameter("groupName");
            String groupDescription = request.getParameter("groupDescription");
            String apiflag = request.getParameter("apiflag");
            String userId = request.getParameter("userId");
            if (groupName != null && groupDescription != null) {
                // Args: group=<groupId> groupName=<name>
                // groupDescription=<description> <= create a new group
                loggingContext.setServiceName("AC:PAP.createGroup");
                String unescapedName = null;
                String unescapedDescription = null;
                try {
                    unescapedName = URLDecoder.decode(groupName, "UTF-8");
                    unescapedDescription = URLDecoder.decode(groupDescription, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e);
                }
                PolicyDBDaoTransaction newGroupTransaction = XACMLPapServlet.getPolicyDbDao().getNewTransaction();
                try {
                    newGroupTransaction.createGroup(PolicyDBDao.createNewPDPGroupId(unescapedName), unescapedName,
                            unescapedDescription, PAPSERVLETDOACPOST);
                    papEngine.newGroup(unescapedName, unescapedDescription);
                    loggingContext.metricStarted();
                    newGroupTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics(ACPOSTCOMMITTRANS);
                } catch (Exception e) {
                    newGroupTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET,
                            " Unable to create new group");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit(TRANSACTIONFAILED);
                    setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Unable to create new group '" + groupId + "'");
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("New Group '" + groupId + "' created");
                }
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                return;
            }
            // for all remaining POST operations the group must exist before the
            // operation can be done
            OnapPDPGroup group = null;
            try {
                group = papEngine.getGroup(groupId);
            } catch (PAPException e) {
                LOGGER.error(e);
            }
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                // for fixing Header Manipulation of Fortify issue
                if (!message.matches(REGEX)) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.addHeader(ERROR, ADD_GROUP_ERROR);
                    response.addHeader(MESSAGE, "GroupId Id is not valid");
                    return;
                }
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                if (apiflag != null) {
                    response.addHeader(ERROR, "unknownGroupId");
                    response.addHeader("operation", "push");
                    response.addHeader(MESSAGE, message);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    setResponseError(response, HttpServletResponse.SC_NOT_FOUND, message);
                }
                return;
            }

            // If the request contains a policyId then we know we are pushing
            // the policy to PDP
            if (request.getParameter(POLICYID) != null) {
                String policyName = request.getParameter(POLICYID);
                List<String> policyIdList = Arrays.asList(policyName.split(","));

                loggingContext.setServiceName("PolicyEngineAPI:PAP.postPolicy");
                LOGGER.info("PushPolicy Request - " + policyName + ", UserId - " + userId);

                StdPDPGroup updatedGroup = null;
                StdPDPPolicy policyForSafetyCheck = new StdPDPPolicy();
                for (String policyId : policyIdList) {
                    PolicyDBDaoTransaction addPolicyToGroupTransaction =
                            XACMLPapServlet.getPolicyDbDao().getNewTransaction();
                    try {
                        // Copying the policy to the file system and updating groups
                        // in database
                        LOGGER.info("PapServlet: calling PolicyDBDao.addPolicyToGroup()");
                        updatedGroup = addPolicyToGroupTransaction.addPolicyToGroup(group.getId(), policyId,
                                PAPSERVLETDOACPOST, userId);
                        loggingContext.metricStarted();
                        addPolicyToGroupTransaction.commitTransaction();
                        loggingContext.metricEnded();
                        PolicyLogger.metrics(ACPOSTCOMMITTRANS);
                        LOGGER.info("PapServlet: addPolicyToGroup() succeeded, transaction was committed");

                        if (policyId.contains("Config_MS_") || policyId.contains("BRMS_Param")) {
                            PushPolicyHandler pushPolicyHandler = PushPolicyHandler.getInstance();
                            policyForSafetyCheck.setId(policyId);
                            if (pushPolicyHandler.preSafetyCheck(policyForSafetyCheck,
                                    XACMLPapServlet.getConfigHome())) {
                                LOGGER.debug("Precheck Successful.");
                            }
                        }

                        // delete temporary policy file from the bin directory
                        Files.deleteIfExists(Paths.get(policyId));

                    } catch (Exception e) {
                        addPolicyToGroupTransaction.rollbackTransaction();
                        String message = "Policy '" + policyName + "' not copied to group '" + groupId + "': " + e;
                        // for fixing Header Manipulation of Fortify issue
                        if (!message.matches(REGEX)) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.addHeader(ERROR, ADD_GROUP_ERROR);
                            response.addHeader(MESSAGE, "Policy Id is not valid");
                            return;
                        }
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " " + message);
                        loggingContext.transactionEnded();
                        PolicyLogger.audit(TRANSACTIONFAILED);
                        if (apiflag != null) {
                            response.addHeader(ERROR, "policyCopyError");
                            response.addHeader(MESSAGE, message);
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        } else {
                            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                        }
                        return;
                    }
                }

                /*
                 * If request comes from the API we need to run the PolicyDBDao updateGroup() to notify other paps of
                 * the change. The GUI does this from the POLICY-SDK-APP code.
                 */

                // Get new transaction to perform updateGroup()
                PolicyDBDaoTransaction acPutTransaction = XACMLPapServlet.getPolicyDbDao().getNewTransaction();
                try {
                    // Assume that this is an update of an existing PDP
                    // Group
                    loggingContext.setServiceName("PolicyEngineAPI:PAP.updateGroup");
                    try {
                        acPutTransaction.updateGroup(updatedGroup, "XACMLPapServlet.doACPut", userId);
                    } catch (Exception e) {
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET,
                                " Error occurred when notifying PAPs of a group change: " + e);
                        throw new PAPException(e.getMessage());
                    }

                    LOGGER.info("Calling updatGroup() with new group");
                    papEngine.updateGroup(updatedGroup);

                    LOGGER.info("Group - '" + updatedGroup.getId() + "' updated");

                    // Commit transaction to send notification to other PAPs
                    loggingContext.metricStarted();
                    acPutTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet updateGroupsFromAPI commitTransaction");
                    // Group changed to send notification to PDPs, which
                    // might include changing the policies
                    getPapInstance().groupChanged(updatedGroup, loggingContext);
                    loggingContext.transactionEnded();
                    LOGGER.info(SUCCESS);
                } catch (Exception e) {
                    acPutTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " API PUT exception");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit(TRANSACTIONFAILED);
                    String message = XACMLErrorConstants.ERROR_PROCESS_FLOW
                            + "Exception occurred when updating the group from API.";
                    LOGGER.error(message);
                    setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.addHeader(ERROR, ADD_GROUP_ERROR);
                    response.addHeader(MESSAGE, message);
                    return;
                }
                // policy file copied ok and the Group was updated on the PDP
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                response.addHeader("operation", "push");
                response.addHeader(POLICYID, policyName);
                response.addHeader("groupId", groupId);

                LOGGER.info("policy '" + policyName + "' copied to directory for group '" + groupId + "'");
                loggingContext.transactionEnded();
                auditLogger.info(SUCCESS);
                LOGGER.info(TRANSENDED);

                return;
            } else if (request.getParameter("default") != null) {
                // Args: group=<groupId> default=true <= make default
                // change the current default group to be the one identified in
                // the request.
                loggingContext.setServiceName("AC:PAP.setDefaultGroup");
                // This is a POST operation rather than a PUT "update group"
                // because of the side-effect that the current default group is
                // also changed.
                // It should never be the case that multiple groups are
                // currently marked as the default, but protect against that
                // anyway.
                PolicyDBDaoTransaction setDefaultGroupTransaction =
                        XACMLPapServlet.getPolicyDbDao().getNewTransaction();
                try {
                    setDefaultGroupTransaction.changeDefaultGroup(group, PAPSERVLETDOACPOST);
                    papEngine.setDefaultGroup(group);
                    loggingContext.metricStarted();
                    setDefaultGroupTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics(ACPOSTCOMMITTRANS);
                } catch (Exception e) {
                    setDefaultGroupTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " Unable to set group");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit(TRANSACTIONFAILED);
                    setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Unable to set group '" + groupId + "' to default");
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Group- '" + groupId + "' set to be default");
                }
                auditLogger.info(SUCCESS);
                LOGGER.info(TRANSENDED);
                return;
            } else if (request.getParameter("pdpId") != null) {
                doAcPostTransaction = XACMLPapServlet.getPolicyDbDao().getNewTransaction();
                // Args: group=<groupId> pdpId=<pdpId> <= move PDP to group
                loggingContext.setServiceName("AC:PAP.movePDP");
                String pdpId = request.getParameter("pdpId");
                OnapPDP pdp = papEngine.getPDP(pdpId);
                OnapPDPGroup originalGroup = papEngine.getPDPGroup(pdp);
                try {
                    doAcPostTransaction.movePdp(pdp, group, PAPSERVLETDOACPOST);
                } catch (Exception e) {
                    doAcPostTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET,
                            " Error while moving pdp in the database: " + "pdp=" + pdp.getId() + ",to group="
                                    + group.getId());
                    throw new PAPException(e.getMessage());
                }
                papEngine.movePDP(pdp, group);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(
                            "PDP - '" + pdp.getId() + "' moved to group - '" + group.getId() + "' set to be default");
                }
                // update the status of both the original group and the new one
                ((StdPDPGroup) originalGroup).resetStatus();
                ((StdPDPGroup) group).resetStatus();
                // Need to notify the PDP that it's config may have changed
                getPapInstance().pdpChanged(pdp, loggingContext);
                loggingContext.metricStarted();
                doAcPostTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics(ACPOSTCOMMITTRANS);
                loggingContext.transactionEnded();
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                return;
            }
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " AC POST exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit(TRANSACTIONFAILED);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to GET info about the Groups and PDPs.
     *
     * @param request the request
     * @param response the response
     * @param groupId the group id
     * @param loggingContext the logging context
     * @param papEngine the pap engine
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doAcGet(HttpServletRequest request, HttpServletResponse response, String groupId,
            ONAPLoggingContext loggingContext, PAPPolicyEngine papEngine) throws IOException {
        try {
            String parameterDefault = request.getParameter("default");
            String pdpId = request.getParameter("pdpId");
            String pdpGroup = request.getParameter("getPDPGroup");
            if ("".equals(groupId)) {
                // request IS from AC but does not identify a group by name
                if (parameterDefault != null) {
                    // Request is for the Default group (whatever its id)
                    loggingContext.setServiceName("AC:PAP.getDefaultGroup");
                    OnapPDPGroup group = papEngine.getDefaultGroup();
                    // convert response object to JSON and include in the
                    // response
                    mapperWriteValue(new ObjectMapper(), response, group);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("GET Default group req from '" + request.getRequestURL() + "'");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("content-type", "application/json");
                    try {
                        response.getOutputStream().close();
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                    loggingContext.transactionEnded();
                    auditLogger.info(SUCCESS);
                    PolicyLogger.audit(TRANSENDED);
                    return;
                } else if (pdpId != null) {
                    // Request is related to a PDP
                    if (pdpGroup == null) {
                        // Request is for the (unspecified) group containing a
                        // given PDP
                        loggingContext.setServiceName("AC:PAP.getPDP");
                        OnapPDP pdp = null;
                        try {
                            pdp = papEngine.getPDP(pdpId);
                        } catch (PAPException e) {
                            LOGGER.error(e);
                        }
                        // convert response object to JSON and include in the
                        // response
                        mapperWriteValue(new ObjectMapper(), response, pdp);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("GET pdp '" + pdpId + "' req from '" + request.getRequestURL() + "'");
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setHeader("content-type", "application/json");
                        try {
                            response.getOutputStream().close();
                        } catch (IOException e) {
                            LOGGER.error(e);
                        }
                        loggingContext.transactionEnded();
                        auditLogger.info(SUCCESS);
                        PolicyLogger.audit(TRANSENDED);
                        return;
                    } else {
                        // Request is for the group containing a given PDP
                        loggingContext.setServiceName("AC:PAP.getGroupForPDP");
                        OnapPDPGroup group = null;
                        try {
                            OnapPDP pdp = papEngine.getPDP(pdpId);
                            group = papEngine.getPDPGroup(pdp);
                        } catch (PAPException e) {
                            LOGGER.error(e);
                        }
                        // convert response object to JSON and include in the
                        // response
                        mapperWriteValue(new ObjectMapper(), response, group);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("GET PDP '" + pdpId + "' Group req from '" + request.getRequestURL() + "'");
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setHeader("content-type", "application/json");
                        try {
                            response.getOutputStream().close();
                        } catch (IOException e) {
                            LOGGER.error(e);
                        }
                        loggingContext.transactionEnded();
                        auditLogger.info(SUCCESS);
                        PolicyLogger.audit(TRANSENDED);
                        return;
                    }
                } else {
                    // request is for top-level properties about all groups
                    loggingContext.setServiceName("AC:PAP.getAllGroups");
                    Set<OnapPDPGroup> groups = null;
                    try {
                        groups = papEngine.getOnapPDPGroups();
                    } catch (PAPException e) {
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " AC Get exception");
                        loggingContext.transactionEnded();
                        PolicyLogger.audit(TRANSACTIONFAILED);
                        setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                    // convert response object to JSON and include in the
                    // response
                    mapperWriteValue(new ObjectMapper(), response, groups);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("GET All groups req");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("content-type", "application/json");
                    try {
                        response.getOutputStream().close();
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                    loggingContext.transactionEnded();
                    auditLogger.info(SUCCESS);
                    PolicyLogger.audit(TRANSENDED);
                    return;
                }
            }
            // for all other GET operations the group must exist before the
            // operation can be done
            OnapPDPGroup group = null;
            try {
                group = papEngine.getGroup(groupId);
            } catch (PAPException e) {
                LOGGER.error(e);
            }
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                // for fixing Header Manipulation of Fortify issue
                if (!message.matches(REGEX)) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.addHeader(ERROR, ADD_GROUP_ERROR);
                    response.addHeader(MESSAGE, "Group Id is not valid");
                    return;
                }
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_NOT_FOUND, message);
                return;
            }
            // Figure out which request this is based on the parameters
            String policyId = request.getParameter(POLICYID);
            if (policyId != null) {
                // retrieve a policy
                loggingContext.setServiceName("AC:PAP.getPolicy");
                // convert response object to JSON and include in the response
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " GET Policy not implemented");
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "GET Policy not implemented");
            } else {
                // No other parameters, so return the identified Group
                loggingContext.setServiceName("AC:PAP.getGroup");
                // convert response object to JSON and include in the response
                mapperWriteValue(new ObjectMapper(), response, group);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("GET group '" + group.getId() + "' req from '" + request.getRequestURL() + "'");
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.setHeader("content-type", "application/json");
                try {
                    response.getOutputStream().close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                loggingContext.transactionEnded();
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                return;
            }
            // Currently there are no other GET calls from the AC.
            // The AC uses the "GET All Groups" operation to fill its local
            // cache and uses that cache for all other GETs without calling the
            // PAP.
            // Other GETs that could be called:
            // Specific Group (groupId=<groupId>)
            // A Policy (groupId=<groupId> policyId=<policyId>)
            // A PDP (groupId=<groupId> pdpId=<pdpId>)
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " UNIMPLEMENTED ");
            loggingContext.transactionEnded();
            PolicyLogger.audit(TRANSACTIONFAILED);
            setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " AC Get exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit(TRANSACTIONFAILED);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to create new items or update existing ones.
     *
     * @param request the request
     * @param response the response
     * @param groupId the group id
     * @param loggingContext the logging context
     * @param papEngine the pap engine
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doAcPut(HttpServletRequest request, HttpServletResponse response, String groupId,
            ONAPLoggingContext loggingContext, PAPPolicyEngine papEngine) throws IOException {
        PolicyDBDaoTransaction acPutTransaction = XACMLPapServlet.getPolicyDbDao().getNewTransaction();
        try {
            String userId = request.getParameter("userId");
            // for PUT operations the group may or may not need to exist before
            // the operation can be done
            OnapPDPGroup group = papEngine.getGroup(groupId);
            // determine the operation needed based on the parameters in the
            // request
            // for remaining operations the group must exist before the
            // operation can be done
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_NOT_FOUND, message);
                acPutTransaction.rollbackTransaction();
                return;
            }
            if (request.getParameter("policy") != null) {
                // group=<groupId> policy=<policyId> contents=policy file <=
                // Create new policy file in group dir, or replace it if it
                // already exists (do not touch properties)
                loggingContext.setServiceName("AC:PAP.putPolicy");
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE
                        + " PARTIALLY IMPLEMENTED!!!  ACTUAL CHANGES SHOULD BE MADE BY PAP SERVLET!!! ");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                acPutTransaction.rollbackTransaction();
                return;
            } else if (request.getParameter("pdpId") != null) {
                // ARGS: group=<groupId> pdpId=<pdpId/URL> <= create a new PDP
                // or Update an Existing one
                String pdpId = request.getParameter("pdpId");
                if (papEngine.getPDP(pdpId) == null) {
                    loggingContext.setServiceName("AC:PAP.createPDP");
                } else {
                    loggingContext.setServiceName("AC:PAP.updatePDP");
                }
                // get the request content into a String
                String json = null;
                // read the inputStream into a buffer (trick found online scans
                // entire input looking for end-of-file)
                try {
                    Scanner scanner = new Scanner(request.getInputStream());
                    scanner.useDelimiter("\\A");
                    json = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                LOGGER.info("JSON request from AC: " + json);
                // convert Object sent as JSON into local object
                ObjectMapper mapper = new ObjectMapper();
                Object objectFromJson = null;
                try {
                    objectFromJson = mapper.readValue(json, StdPDP.class);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                if (pdpId == null || objectFromJson == null || !(objectFromJson instanceof StdPDP)
                        || ((StdPDP) objectFromJson).getId() == null
                        || !((StdPDP) objectFromJson).getId().equals(pdpId)) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " PDP new/update had bad input. pdpId=" + pdpId
                            + " objectFromJSON=" + objectFromJson);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit(TRANSACTIONFAILED);
                    setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Bad input pdpid for object:" + objectFromJson);
                }
                StdPDP pdp = (StdPDP) objectFromJson;
                if (pdp != null) {
                    OnapPDP origPdp = null;
                    try {
                        origPdp = papEngine.getPDP(pdpId);
                    } catch (PAPException e) {
                        LOGGER.error(e);
                    }
                    if (origPdp == null) {
                        // this is a request to create a new PDP object
                        try {
                            acPutTransaction.addPdpToGroup(pdp.getId(), group.getId(), pdp.getName(),
                                    pdp.getDescription(), pdp.getJmxPort(), "XACMLPapServlet.doACPut");
                            papEngine.newPDP(pdp.getId(), group, pdp.getName(), pdp.getDescription(), pdp.getJmxPort());
                        } catch (Exception e) {
                            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET,
                                    " Error while adding pdp to group in the database: " + "pdp=" + (pdp.getId())
                                            + ",to group=" + group.getId());
                            throw new PAPException(e.getMessage());
                        }
                    } else {
                        // this is a request to update the pdp
                        try {
                            acPutTransaction.updatePdp(pdp, "XACMLPapServlet.doACPut");
                            papEngine.updatePDP(pdp);
                        } catch (Exception e) {
                            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET,
                                    " Error while updating pdp in the database: " + "pdp=" + pdp.getId());
                            throw new PAPException(e.getMessage());
                        }
                    }
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("PDP '" + pdpId + "' created/updated");
                    }
                    // adjust the group's state including the new PDP
                    ((StdPDPGroup) group).resetStatus();
                    // this might affect the PDP, so notify it of the change
                    getPapInstance().pdpChanged(pdp, loggingContext);
                    loggingContext.metricStarted();
                    acPutTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                    loggingContext.transactionEnded();
                    auditLogger.info(SUCCESS);
                    PolicyLogger.audit(TRANSENDED);
                    return;
                } else {
                    try {
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, XACMLPAPSERVLET,
                                " Error while adding pdp to group in the database: " + "pdp=null" + ",to group="
                                        + group.getId());
                        throw new PAPException("PDP is null");
                    } catch (Exception e) {
                        throw new PAPException("PDP is null" + e.getMessage() + e);
                    }
                }
            } else if (request.getParameter("pipId") != null) {
                // group=<groupId> pipId=<pipEngineId> contents=pip properties
                // <= add a PIP to pip config, or replace it if it already
                // exists (lenient operation)
                loggingContext.setServiceName("AC:PAP.putPIP");
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                acPutTransaction.rollbackTransaction();
                return;
            } else {
                // Assume that this is an update of an existing PDP Group
                // ARGS: group=<groupId> <= Update an Existing Group
                loggingContext.setServiceName("AC:PAP.updateGroup");
                // get the request content into a String
                String json = null;
                // read the inputStream into a buffer (trick found online scans
                // entire input looking for end-of-file)
                try {
                    Scanner scanner = new Scanner(request.getInputStream());
                    scanner.useDelimiter("\\A");
                    json = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                LOGGER.info("JSON request from AC: " + json);
                // convert Object sent as JSON into local object
                ObjectMapper mapper = new ObjectMapper();
                Object objectFromJson = null;
                try {
                    objectFromJson = mapper.readValue(json, StdPDPGroup.class);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
                if (objectFromJson == null || !(objectFromJson instanceof StdPDPGroup)
                        || !((StdPDPGroup) objectFromJson).getId().equals(group.getId())) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id="
                            + group.getId() + " objectFromJSON=" + objectFromJson);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit(TRANSACTIONFAILED);
                    setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Bad input id for object:" + objectFromJson);
                }
                // The Path on the PAP side is not carried on the RESTful
                // interface with the AC
                // (because it is local to the PAP)
                // so we need to fill that in before submitting the group for
                // update
                if (objectFromJson != null) {
                    ((StdPDPGroup) objectFromJson).setDirectory(((StdPDPGroup) group).getDirectory());
                }
                try {
                    if ("delete".equals(((StdPDPGroup) objectFromJson).getOperation())) {
                        acPutTransaction.updateGroup((StdPDPGroup) objectFromJson, "XACMLPapServlet.doDelete", userId);
                    } else {
                        acPutTransaction.updateGroup((StdPDPGroup) objectFromJson, "XACMLPapServlet.doACPut", userId);
                    }
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database: "
                            + "group=" + group.getId());
                    LOGGER.error(e);
                    throw new PAPException(e.getMessage());
                }

                PushPolicyHandler pushPolicyHandler = PushPolicyHandler.getInstance();
                OnapPDPGroup updatedGroup = (StdPDPGroup) objectFromJson;
                if (pushPolicyHandler.preSafetyCheck(updatedGroup, XACMLPapServlet.getConfigHome())) {
                    LOGGER.debug("Precheck Successful.");
                }
                try {
                    papEngine.updateGroup((StdPDPGroup) objectFromJson);
                } catch (PAPException e) {
                    LOGGER.error(e);
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Group '" + group.getId() + "' updated");
                }
                loggingContext.metricStarted();
                acPutTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                // Group changed, which might include changing the policies
                getPapInstance().groupChanged(group, loggingContext);
                loggingContext.transactionEnded();
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                return;
            }
        } catch (PAPException e) {
            LOGGER.debug(e);
            acPutTransaction.rollbackTransaction();
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " AC PUT exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit(TRANSACTIONFAILED);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to delete/remove items.
     *
     * @param request the request
     * @param response the response
     * @param groupId the group id
     * @param loggingContext the logging context
     * @param papEngine the pap engine
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void doAcDelete(HttpServletRequest request, HttpServletResponse response, String groupId,
            ONAPLoggingContext loggingContext, PAPPolicyEngine papEngine) throws IOException {
        PolicyDBDaoTransaction removePdpOrGroupTransaction = XACMLPapServlet.getPolicyDbDao().getNewTransaction();
        try {
            // for all DELETE operations the group must exist before the
            // operation can be done
            loggingContext.setServiceName("AC:PAP.delete");
            OnapPDPGroup group = papEngine.getGroup(groupId);
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId + "'");
                removePdpOrGroupTransaction.rollbackTransaction();
                return;
            }
            // determine the operation needed based on the parameters in the
            // request
            if (request.getParameter("policy") != null) {
                // group=<groupId> policy=<policyId> [delete=<true|false>] <=
                // delete policy file from group
                loggingContext.setServiceName("AC:PAP.deletePolicy");
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                removePdpOrGroupTransaction.rollbackTransaction();
                return;
            } else if (request.getParameter("pdpId") != null) {
                // ARGS: group=<groupId> pdpId=<pdpId> <= delete PDP
                String pdpId = request.getParameter("pdpId");
                OnapPDP pdp = papEngine.getPDP(pdpId);
                removePdpFromGroup(removePdpOrGroupTransaction, pdp, papEngine);
                // adjust the status of the group, which may have changed when
                // we removed this PDP
                ((StdPDPGroup) group).resetStatus();
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                // update the PDP and tell it that it has NO Policies (which
                // prevents it from serving PEP Requests)
                getPapInstance().pdpChanged(pdp, loggingContext);
                loggingContext.metricStarted();
                removePdpOrGroupTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                loggingContext.transactionEnded();
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                return;
            } else if (request.getParameter("pipId") != null) {
                // group=<groupId> pipId=<pipEngineId> <= delete PIP config for
                // given engine
                loggingContext.setServiceName("AC:PAP.deletePIPConfig");
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
                loggingContext.transactionEnded();
                PolicyLogger.audit(TRANSACTIONFAILED);
                setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                removePdpOrGroupTransaction.rollbackTransaction();
                return;
            } else {
                // ARGS: group=<groupId> movePDPsToGroupId=<movePDPsToGroupId>
                // <= delete a group and move all its PDPs to the given group
                String moveToGroupId = request.getParameter("movePDPsToGroupId");
                OnapPDPGroup moveToGroup = null;
                if (moveToGroupId != null) {
                    try {
                        moveToGroup = papEngine.getGroup(moveToGroupId);
                    } catch (PAPException e) {
                        LOGGER.error(e);
                    }
                }
                // get list of PDPs in the group being deleted so we can notify
                // them that they got changed
                Set<OnapPDP> movedPdps = new HashSet<>();
                movedPdps.addAll(group.getOnapPdps());
                // do the move/remove
                deleteGroup(removePdpOrGroupTransaction, group, moveToGroup, papEngine);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                // notify any PDPs in the removed set that their config may have
                // changed
                for (OnapPDP pdp : movedPdps) {
                    getPapInstance().pdpChanged(pdp, loggingContext);
                }
                loggingContext.metricStarted();
                removePdpOrGroupTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                loggingContext.transactionEnded();
                auditLogger.info(SUCCESS);
                PolicyLogger.audit(TRANSENDED);
                return;
            }
        } catch (PAPException e) {
            removePdpOrGroupTransaction.rollbackTransaction();
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET, " AC DELETE exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit(TRANSACTIONFAILED);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    private void deleteGroup(PolicyDBDaoTransaction removePdpOrGroupTransaction, OnapPDPGroup group,
            OnapPDPGroup moveToGroup, PAPPolicyEngine papEngine) throws PAPException {
        try {
            removePdpOrGroupTransaction.deleteGroup(group, moveToGroup, "XACMLPapServlet.doACDelete");
            papEngine.removeGroup(group, moveToGroup);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_UNKNOWN, e, XACMLPAPSERVLET,
                    " Failed to delete PDP Group. Exception");
            throw new PAPException(e.getMessage());
        }
    }

    private void removePdpFromGroup(PolicyDBDaoTransaction removePdpOrGroupTransaction, OnapPDP pdp,
            PAPPolicyEngine papEngine) throws PAPException {
        try {
            removePdpOrGroupTransaction.removePdpFromGroup(pdp.getId(), "XACMLPapServlet.doACDelete");
            papEngine.removePDP(pdp);
        } catch (Exception e) {
            throw new PAPException(e);
        }
    }

    private XACMLPapServlet getPapInstance() {
        return new XACMLPapServlet();
    }

    private static void mapperWriteValue(ObjectMapper mapper, HttpServletResponse response, Object value) {
        try {
            mapper.writeValue(response.getOutputStream(), value);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void setResponseError(HttpServletResponse response, int responseCode, String message) {
        try {
            if (message != null && !message.isEmpty()) {
                response.sendError(responseCode, message);
            }
        } catch (IOException e) {
            LOGGER.error("Error setting Error response Header ", e);
        }
        return;
    }
}
