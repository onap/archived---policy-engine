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

import java.util.Map;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPAPPolicyParams;

/**
 * Config Base Policy Implementation.
 *
 * @version 0.1
 */
public class ConfigPolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(ConfigPolicyService.class.getName());
    private PAPServices papServices = null;

    private PolicyParameters policyParameters = null;
    private String message = null;
    private String policyName = null;
    private String policyScope = null;
    private String date = null;
    private String onapName = null;
    private String configName = null;

    public ConfigPolicyService(String policyName, String policyScope,
                               PolicyParameters policyParameters, String date) {
        this.policyParameters = policyParameters;
        this.policyName = policyName;
        this.policyScope = policyScope;
        this.date = date;
        papServices = new PAPServices();
    }

    public Boolean getValidation() {
        if (policyParameters.getConfigBody() == null || policyParameters.getConfigBody().trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Body given.";
            return false;
        }
        if (policyParameters.getConfigBodyType() == null) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Body Type given.";
            return false;
        }
        boolean levelCheck = false;
        levelCheck = PolicyApiUtils.isNumeric(policyParameters.getRiskLevel());
        if (!levelCheck) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
            return false;
        }
        onapName = policyParameters.getOnapName();
        configName = policyParameters.getConfigName();
        if (onapName == null || onapName.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ONAP Name given.";
            return false;
        }
        if (configName == null || configName.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Name given.";
            return false;
        }
        message = PolicyUtils.policySpecialCharValidator(onapName);
        if (!message.contains("success")) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + message;
            return false;
        }
        message = PolicyUtils.policySpecialCharValidator(configName);
        if (!message.contains("success")) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + message;
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
        if (updateFlag) {
            operation = "update";
        } else {
            operation = "create";
        }
        String configType = policyParameters.getConfigBodyType().toString();
        String body = policyParameters.getConfigBody();
        String configBody = null;
        //check body for JSON form and remove single quotes if present
        if ("JSON".equalsIgnoreCase(configType)) {
            if (body.contains("'")) {
                configBody = body.replace("'", "\"");
            } else {
                configBody = body;
            }
        } else {
            configBody = body;
        }
        Map<String, String> configAttributes = null;
        if (policyParameters.getAttributes() != null) {
            configAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
        }
        // create Policy.
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder()
                .configPolicyType("Base")
                .policyName(policyName)
                .description(policyParameters.getPolicyDescription())
                .onapName(onapName)
                .configName(configName)
                .attributes(configAttributes)
                .configType(configType)
                .configBodyData(configBody)
                .editPolicy(updateFlag)
                .domain(policyScope)
                .highestVersion(0)
                .riskLevel(policyParameters.getRiskLevel())
                .riskType(policyParameters.getRiskType())
                .guard(String.valueOf(policyParameters.getGuard()))
                .ttlDate(date)
                .build());
        // Send Json to PAP.
        response = (String) papServices.callPAP(newPAPPolicy, new String[]{"operation=" + operation, "apiflag=api",
                "policyType=Config"}, policyParameters.getRequestID(), "Config");
        LOGGER.info(response);
        return response;
    }

}
