/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

import javax.json.JsonException;
import javax.json.JsonObject;

import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;

/**
 * Closed Loop Fault Policy Implementation.
 *
 * @version 0.1
 */
public class ClosedLoopFaultPolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(ClosedLoopFaultPolicyService.class.getName());
    private PAPServices papServices = null;

    private PolicyParameters policyParameters = null;
    private String message = null;
    private String policyName = null;
    private String policyScope = null;
    private String date = null;
    private JsonObject configBody = null;

    public ClosedLoopFaultPolicyService(String policyName, String policyScope,
                                        PolicyParameters policyParameters, String date) {
        this.policyParameters = policyParameters;
        this.policyName = policyName;
        this.policyScope = policyScope;
        this.date = date;
        papServices = new PAPServices();
    }

    public Boolean getValidation() {
        if (policyParameters.getConfigBody() == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + " No Config Body Present";
            return false;
        }
        if (!PolicyApiUtils.validateNONASCIICharactersAndAllowSpaces(policyParameters.getConfigBody())) {
            message =
                    XACMLErrorConstants.ERROR_DATA_ISSUE + " improper JSON object : " +
                            policyParameters.getConfigBody();
            return false;
        }
        try {
            configBody = PolicyApiUtils.stringToJsonObject(policyParameters.getConfigBody());
        } catch (JsonException | IllegalStateException e) {
            message =
                    XACMLErrorConstants.ERROR_DATA_ISSUE + " improper JSON object : " +
                            policyParameters.getConfigBody();
            LOGGER.error("Json Parse Exception.", e);
            return false;
        }
        return true;
    }

    public String getMessage() {
        return message;
    }

    public String getResult(boolean updateFlag) throws PolicyException {
        String response = null;
        String operation = null;
        String oldPolicyName = null;
        if (updateFlag) {
            operation = "update";
            if (policyName.endsWith("_Draft")) {
                oldPolicyName = policyName + "_Draft.1";
            }
        } else {
            operation = "create";
        }
        // get values and attributes from the JsonObject
        if (!configBody.containsKey("onapname")) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Onap Name given.";
            LOGGER.error(message);
            return message;
        }
        String onapName = configBody.get("onapname").toString().trim().replace("\"", "");
        if (onapName == null || onapName.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Onap Name given.";
            LOGGER.error(message);
            return message;
        }
        boolean levelCheck = PolicyApiUtils.isNumeric(policyParameters.getRiskLevel());
        if (!levelCheck) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
            LOGGER.error(message);
            return message;
        }
        String jsonBody = configBody.toString();
        // Create Policy.
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("ClosedLoop_Fault", policyName,
                policyParameters.getPolicyDescription(), onapName,
                jsonBody, false, oldPolicyName, null, updateFlag, policyScope, 0, policyParameters.getRiskLevel(),
                policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date);
        //send JSON object to PAP
        response = (String) papServices.callPAP(newPAPPolicy, new String[]{"operation=" + operation, "apiflag=api",
                "policyType=Config"}, policyParameters.getRequestID(), "ConfigClosedLoop");
        return response;
    }

}
