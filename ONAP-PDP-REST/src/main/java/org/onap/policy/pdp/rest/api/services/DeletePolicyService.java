/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import java.io.File;
import java.util.UUID;

import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.springframework.http.HttpStatus;

public class DeletePolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(DeletePolicyService.class.getName());

    private String deleteResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private DeletePolicyParameters deletePolicyParameters = null;
    private String message = null;
    protected String filePrefix = null;
    protected String clientScope = null; 
    private String policyType = null;
    private String policyName = null;
    private String policyScope = null;

    public DeletePolicyService(final DeletePolicyParameters deletePolicyParameters, final String requestID) {
        this.deletePolicyParameters = deletePolicyParameters;
        if (deletePolicyParameters.getRequestID() == null) {
            UUID requestUUID = null;
            if (requestID != null && !requestID.isEmpty()) {
                try {
                    requestUUID = UUID.fromString(requestID);
                } catch (final IllegalArgumentException e) {
                    requestUUID = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUUID.toString(), e);
                }
            } else {
                requestUUID = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUUID.toString());
            }
            this.deletePolicyParameters.setRequestID(requestUUID);
        }
        try {
            run();
            specialCheck();
        } catch (final PolicyException e) {
            LOGGER.error("Unable to process delete policy request for : " + this.deletePolicyParameters);
            deleteResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void specialCheck() {
        if (deleteResult == null) {
            return;
        }
        if (deleteResult.contains("BAD REQUEST") || deleteResult.contains("PE300") || deleteResult.contains("PE200")
                || deleteResult.contains("not exist") || deleteResult.contains("Invalid policyName")) {
            if (deleteResult.contains("groupId")) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.BAD_REQUEST;
            }
        } else if (deleteResult.contains("locked down")) {
            status = HttpStatus.ACCEPTED;
        } else if (deleteResult.contains("not Authorized")) {
            status = HttpStatus.FORBIDDEN;
        } else if (deleteResult.contains("JPAUtils") || deleteResult.contains("database")
                || deleteResult.contains("policy file") || deleteResult.contains("unknown")
                || deleteResult.contains("configuration")) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void run() throws PolicyException {
        // Check Validation.
        if (!getValidation()) {
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Get Result.
        try {
            LOGGER.debug("Processing delete request:  " + deletePolicyParameters.toString());
            status = HttpStatus.OK;
            deleteResult = processResult();
        } catch (final Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    private String processResult() throws PolicyException {
        String response = null;
        String fullPolicyName = null;
        String pdpGroup = deletePolicyParameters.getPdpGroup();
        // PDP Group Check.
        if (pdpGroup == null) {
            pdpGroup = "NA";
        }
        final PAPServices papServices = new PAPServices();
        if (!deletePolicyParameters.getPolicyName().contains("xml")) {

            final String activeVersion = papServices.getActiveVersion(policyScope, filePrefix, policyName, clientScope,
                    deletePolicyParameters.getRequestID());
            LOGGER.debug("The active version of " + policyScope + File.separator + filePrefix + policyName + " is "
                    + activeVersion);
            String id = null;
            if ("pe100".equalsIgnoreCase(activeVersion)) {
                response = XACMLErrorConstants.ERROR_PERMISSIONS
                        + "response code of the URL is 403. PEP is not Authorized for making this Request!! "
                        + "Contact Administrator for this Scope. ";
                LOGGER.error(response);
                return response;
            } else if ("pe300".equalsIgnoreCase(activeVersion)) {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
                        + "This indicates a problem with getting the version from the PAP or the policy does not exist.";
                LOGGER.error(response);
                return response;
            }
            if (!"0".equalsIgnoreCase(activeVersion)) {
                id = policyScope + "." + filePrefix + policyName + "." + activeVersion + ".xml";
                LOGGER.debug("The policyId is " + id);
            } else {
                response = XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "could not retrieve the activeVersion for this policy. could not retrieve the activeVersion for this policy.  "
                        + "This indicates the policy does not exist, please verify the policy exists.";
                LOGGER.error(response);
                return response;
            }

            fullPolicyName = policyScope + "." + filePrefix + policyName + "." + activeVersion + ".xml";

        } else {
            fullPolicyName = policyName;
        }

        if ("PAP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent())) {
            if (deletePolicyParameters.getDeleteCondition() == null
                    || deletePolicyParameters.getDeleteCondition().toString().trim().isEmpty()) {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Delete Condition given.";
                LOGGER.error(message);
                return message;
            }

            final StdPAPPolicy deletePapPolicy =
                    new StdPAPPolicy(fullPolicyName, deletePolicyParameters.getDeleteCondition().toString());
            // send JSON object to PAP
            response = (String) papServices.callPAP(deletePapPolicy,
                    new String[] {"groupId=" + pdpGroup, "apiflag=deletePapApi", "operation=delete"},
                    deletePolicyParameters.getRequestID(), clientScope);
        } else if ("PDP".equalsIgnoreCase(deletePolicyParameters.getPolicyComponent())) {
            if (deletePolicyParameters.getPdpGroup() == null || deletePolicyParameters.getPdpGroup().trim().isEmpty()) {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PDP Group given.";
                LOGGER.error(message);
                return message;
            }
            // send JSON object to PAP
            response =
                    (String) papServices.callPAP(
                            null, new String[] {"policyName=" + fullPolicyName, "groupId=" + pdpGroup,
                                "apiflag=deletePdpApi", "operation=delete"},
                            deletePolicyParameters.getRequestID(), clientScope);
        } else {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "Policy Component does not exist. Please enter either PAP or PDP to delete the policy from a specified Policy Component.";
            LOGGER.error(message);
            response = message;
        }
        return response;
    }

    private boolean getValidation() {
        // While Validating, extract the required values.
        if (deletePolicyParameters.getPolicyName() == null || deletePolicyParameters.getPolicyName().trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            return false;
        }
        if (!deletePolicyParameters.getPolicyName().contains("xml")) {
            if (deletePolicyParameters.getPolicyName() != null
                    && deletePolicyParameters.getPolicyName().contains(".")) {
                policyName = deletePolicyParameters.getPolicyName().substring(
                        deletePolicyParameters.getPolicyName().lastIndexOf('.') + 1,
                        deletePolicyParameters.getPolicyName().length());
                policyScope = deletePolicyParameters.getPolicyName().substring(0,
                        deletePolicyParameters.getPolicyName().lastIndexOf('.'));
                LOGGER.info("Name is " + policyName + "   scope is " + policyScope);
            } else {
                message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
                return false;
            }
        } else {
            policyName = deletePolicyParameters.getPolicyName();
        }
        policyType = deletePolicyParameters.getPolicyType();
        if (policyType == null || policyType.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PolicyType given.";
            return false;
        }
        setClientScope();
        if (clientScope == null || clientScope.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + deletePolicyParameters.getPolicyType()
                    + " is not a valid Policy Type.";
            LOGGER.error(message);
            return false;
        }
        LOGGER.debug("clientScope is " + clientScope);
        LOGGER.debug("filePrefix is " + filePrefix);
        if (deletePolicyParameters.getPolicyComponent() == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Component given.";
            return false;
        }
        return true;
    }
 
 	public void extendedClientScope(String policyType){
 		clientScope = null;
 		message = XACMLErrorConstants.ERROR_DATA_ISSUE + policyType
                 + " is not a valid Policy Type.";
 	}

    public void setClientScope() {
        if ("Firewall".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigFirewall";
            filePrefix = "Config_FW_";
        } else if ("Action".equalsIgnoreCase(policyType)) {
            clientScope = "Action";
            filePrefix = "Action_";
        } else if ("Decision_MS".equalsIgnoreCase(policyType)) {
            clientScope = "Decision_MS";
            filePrefix = "Decision_MS_";
        } else if ("Decision".equalsIgnoreCase(policyType)) {
            clientScope = "Decision";
            filePrefix = "Decision_";
        } else if ("Base".equalsIgnoreCase(policyType)) {
            clientScope = "Config";
            filePrefix = "Config_";
        } else if ("ClosedLoop_Fault".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigClosedLoop";
            filePrefix = "Config_Fault_";
        } else if ("ClosedLoop_PM".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigClosedLoop";
            filePrefix = "Config_PM_";
        } else if ("MicroService".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigMS";
            filePrefix = "Config_MS_";
        } else if ("Optimization".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigOptimization";
            filePrefix = "Config_OOF_";
        } else if ("BRMS_RAW".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigBrmsRaw";
            filePrefix = "Config_BRMS_Raw_";
        } else if ("BRMS_PARAM".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigBrmsParam";
            filePrefix = "Config_BRMS_Param_";
        } else { 
            extendedClientScope(policyType);
        }
    }

    public String getResult() {
        return deleteResult;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
