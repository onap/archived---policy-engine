/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.api.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyNameType;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.http.HttpStatus;

public class PushPolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(PushPolicyService.class.getName());

    private String pushResult = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private PushPolicyParameters pushPolicyParameters = null;
    private List<PolicyNameType> policyList = new ArrayList<>();
    private String message = null;
    private String pdpGroup = null;
    private String requestId = null;
    protected String filePrefix = null;
    protected String clientScope = null;


    /**
     * Instantiates a new push policy service.
     *
     * @param pushPolicyParameters the push policy parameters
     * @param reqId the request ID
     */
    public PushPolicyService(final PushPolicyParameters pushPolicyParameters, final String reqId) {
        this.pushPolicyParameters = pushPolicyParameters;
        populateRequestId(pushPolicyParameters, reqId);
        LOGGER.info("Operation: pushPolicy - Request - " + pushPolicyParameters + ", RequestId - " + requestId);
        try {
            run();
            specialCheck();
        } catch (PolicyException e) {
            pushResult = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
        LOGGER.info("PushPolicyService - RequestId - " + requestId + " , Response - " + pushResult + ", Status - "
                + status);
    }

    private void specialCheck() {
        if (pushResult.contains("BAD REQUEST") || pushResult.contains("PE300")) {
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private void run() throws PolicyException {
        // Check Validation.
        if (!getValidation()) {
            LOGGER.error(message);
            throw new PolicyException(message);
        }
        // Process Results.
        try {
            status = HttpStatus.OK;
            pushResult = processResult();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    private String processResult() throws PolicyException {
        PAPServices papServices = new PAPServices();
        String response = null;

        try {
            // for each policy to be pushed, we need to call pap to get the active version
            // and construct the full policy name. Then the list of policy names are sent to pap
            // to do the actual push.
            String policyId = constructPolicyNames(papServices);
            response =
                    (String) papServices
                            .callPAP(null,
                                    new String[] {"groupId=" + pdpGroup, "policyId=" + policyId, "apiflag=api",
                                                  "operation=POST", "userId=API"},
                                    pushPolicyParameters.getRequestID(), clientScope);
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e.getMessage());
            throw new PolicyException(e);
        }
        LOGGER.debug("Final API response: " + response);
        return response;
    }

    private String constructPolicyNames(PAPServices papServices) throws PolicyException {
        List<String> pushPolicyList = new ArrayList<>();
        for (PolicyNameType policyData : policyList) {
            LOGGER.info("PushPolicyService - constructPolicyNames - Policy Name is " + policyData.getPolicyName()
                    + "   scope is " + policyData.getPolicyScope());
            // for each policy we need to get the active version to construct policyId
            StdPDPPolicy selectedPolicy = papServices.pushPolicy(policyData.getPolicyScope(),
                    policyData.getFilePrefix(), policyData.getPolicyName(), policyData.getClientScope(),
                    pdpGroup.trim(), pushPolicyParameters.getRequestID());
            if (selectedPolicy != null) {
                pushPolicyList.add(selectedPolicy.getId());
            } else {
                throw new PolicyException(XACMLErrorConstants.ERROR_DATA_ISSUE + "response code of the URL is 404.  "
                        + "This indicates a problem with getting the version "
                        + "from the PAP or the policy does not exist - " + policyData);
            }
        }
        return String.join(",", pushPolicyList);
    }

    private boolean getValidation() {
        // While Validating, extract the required values.
        if (StringUtils.isBlank(pushPolicyParameters.getPolicyName())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Name given.";
            return false;
        }

        if (StringUtils.isBlank(pushPolicyParameters.getPolicyType())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No PolicyType given.";
            return false;
        }

        List<String> policyNames = Arrays.asList(pushPolicyParameters.getPolicyName().split(","));
        List<String> policyTypes = Arrays.asList(pushPolicyParameters.getPolicyType().split(","));

        if (policyTypes.size() != 1 && (policyNames.size() != policyTypes.size())) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid number of PolicyTypes";
            return false;
        }

        if ((policyNames.stream().anyMatch(StringUtils::isBlank))
                || (policyTypes.stream().anyMatch(StringUtils::isBlank))) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid PolicyName or PolicyType";
            return false;
        }

        if (!validatePolicyNameAndScope(policyNames, policyTypes)) {
            LOGGER.warn("PushPolicyService - Failed validation in validatePolicyNameAndScope - " + message);
            return false;
        }

        pdpGroup = pushPolicyParameters.getPdpGroup();
        if (StringUtils.isBlank(pdpGroup)) {
            pdpGroup = "default";
        }

        return true;
    }

    private boolean validatePolicyNameAndScope(List<String> policyNames, List<String> policyTypes) {
        String policyName = null;
        String policyScope = null;
        String polType = null;

        if (policyTypes.size() == 1) {
            polType = policyTypes.get(0).trim();
        }

        try {
            for (int i = 0; i < policyNames.size(); i++) {
                String polName = policyNames.get(i);
                if (policyTypes.size() > 1) {
                    polType = policyTypes.get(i).trim();
                }
                if (polName.contains(".")) {
                    policyName = polName.substring(polName.lastIndexOf('.') + 1, polName.length());
                    policyScope = polName.substring(0, polName.lastIndexOf('.'));
                } else {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Policy Scope given.";
                    return false;
                }
                if (StringUtils.isBlank(policyName) || StringUtils.isBlank(policyScope)) {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Policy Name.";
                    return false;
                }

                clientScope = null;
                filePrefix = null;
                setClientScope(polType);
                if (StringUtils.isBlank(clientScope) || StringUtils.isBlank(filePrefix)) {
                    message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Policy Name.";
                    return false;
                }
                PolicyNameType policyData =
                        new PolicyNameType(policyName.trim(), policyScope.trim(), polType, filePrefix, clientScope);
                policyList.add(policyData);
                LOGGER.info("PushPolicy - RequestId - " + requestId + " , Policy - " + policyData);

            }
        } catch (Exception e) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "validatePolicies - Failed Validation. Please check the input parameters.";
            return false;
        }
        return true;
    }

    protected void extendedClientScope(String policyType) {
        clientScope = null;
        message = XACMLErrorConstants.ERROR_DATA_ISSUE + policyType + " is not a valid Policy Type.";
    }

    /**
     * Sets the client scope.
     */
    private void setClientScope(String policyType) {
        if ("Firewall".equalsIgnoreCase(policyType)) {
            clientScope = "ConfigFirewall";
            filePrefix = "Config_FW_";
        } else if ("Action".equalsIgnoreCase(policyType)) {
            clientScope = "Action";
            filePrefix = "Action_";
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

    private void populateRequestId(PushPolicyParameters pushPolicyParameters, String reqId) {
        if (pushPolicyParameters.getRequestID() == null) {
            UUID requestUuid = null;
            if (!StringUtils.isBlank(reqId)) {
                try {
                    requestUuid = UUID.fromString(reqId);
                } catch (IllegalArgumentException e) {
                    requestUuid = UUID.randomUUID();
                    LOGGER.info("Generated Random UUID: " + requestUuid.toString(), e);
                }
            } else {
                requestUuid = UUID.randomUUID();
                LOGGER.info("Generated Random UUID: " + requestUuid.toString());
            }
            this.pushPolicyParameters.setRequestID(requestUuid);
        }
        this.requestId = pushPolicyParameters.getRequestID().toString();
    }

    public String getResult() {
        return pushResult;
    }

    public HttpStatus getResponseCode() {
        return status;
    }
}
