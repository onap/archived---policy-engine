/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.api.services;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.api.PolicyNameType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public abstract class PdpApiService {
    private static final Logger LOGGER = FlexLogger.getLogger(PdpApiService.class.getName());
    protected static final String PRINT_REQUESTID = " - RequestId - ";
    protected String requestId = null;
    protected String filePrefix = null;
    protected String clientScope = null;
    protected String message = null;
    protected HttpStatus status = HttpStatus.BAD_REQUEST;

    protected UUID populateRequestId(UUID paramReqId, String reqId) {
        UUID requestUuid = paramReqId;
        if (paramReqId == null) {
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
        }
        this.requestId = requestUuid.toString();
        return requestUuid;
    }

    /**
     * Sets the client scope.
     */
    protected void setClientScope(String policyType) {
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

    protected void extendedClientScope(String policyType) {
        clientScope = null;
        message = XACMLErrorConstants.ERROR_DATA_ISSUE + policyType + " is not a valid Policy Type.";
    }

    protected boolean validatePolicyNameAndScope(List<String> policyNames, List<String> policyTypes,
            List<PolicyNameType> policyList) {
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

                if (polName.contains("xml")) {
                    policyName = polName;
                    policyScope = policyName.substring(0, StringUtils.lastOrdinalIndexOf(policyName, ".", 3));
                } else if (polName.contains(".")) {
                    policyName = polName.substring(polName.lastIndexOf('.') + 1);
                    policyScope = polName.substring(0, polName.lastIndexOf('.'));
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
                PolicyNameType policyData = new PolicyNameType(policyName.trim(), policyScope.trim(), polType,
                        filePrefix, clientScope, null);
                policyList.add(policyData);
                LOGGER.info("RequestId - " + requestId + " , Policy - " + policyData);

            }
        } catch (Exception e) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "validatePolicies - Failed Validation. Please check the input parameters.";
            return false;
        }
        return true;
    }


    /**
     * Gets the response code.
     *
     * @return the response code
     */
    public HttpStatus getResponseCode() {
        return status;
    }

}
