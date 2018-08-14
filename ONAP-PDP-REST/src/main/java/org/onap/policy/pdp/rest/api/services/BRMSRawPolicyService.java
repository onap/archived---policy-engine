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
 * BRMS RAW Policy Implementation.
 *
 * @version 0.1
 */
public class BRMSRawPolicyService {
    private static Logger LOGGER = FlexLogger.getLogger(BRMSRawPolicyService.class.getName());
    private static PAPServices papServices = null;

    private PolicyParameters policyParameters = null;
    private String message = null;
    private String policyName = null;
    private String policyScope = null;
    private String date = null;
    private boolean levelCheck = false;
    private String brmsRawBody = null;

    public BRMSRawPolicyService(String policyName, String policyScope,
                                PolicyParameters policyParameters, String date) {
        this.policyParameters = policyParameters;
        this.policyName = policyName;
        this.policyScope = policyScope;
        this.date = date;
        papServices = new PAPServices();
    }

    public Boolean getValidation() {
        brmsRawBody = policyParameters.getConfigBody();
        if (brmsRawBody == null || brmsRawBody.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + " No Rule Body given";
            return false;
        }
        message = PolicyUtils.brmsRawValidate(brmsRawBody);
        if (message.contains("[ERR")) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Raw rule given is invalid" + message;
            return false;
        }
        levelCheck = PolicyApiUtils.isNumeric(policyParameters.getRiskLevel());
        if (!levelCheck) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
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
        Map<String, String> ruleAttributes = null;
        if (policyParameters.getAttributes() != null) {
            ruleAttributes = policyParameters.getAttributes().get(AttributeType.RULE);
        }
        // Create Policy
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder()
                .configPolicyType("BRMS_Raw")
                .policyName(policyName)
                .description(policyParameters.getPolicyDescription())
                .configName("BRMS_RAW_RULE")
                .editPolicy(updateFlag)
                .domain(policyScope)
                .dynamicFieldConfigAttributes(ruleAttributes)
                .highestVersion(0)
                .onapName("DROOLS")
                .configBodyData(brmsRawBody)
                .riskLevel(policyParameters.getRiskLevel())
                .riskType(policyParameters.getRiskType())
                .guard(String.valueOf(policyParameters.getGuard()))
                .ttlDate(date)
                .brmsController(policyParameters.getControllerName())
                .brmsDependency(policyParameters.getDependencyNames())
                .build());
        // Send JSON to PAP
        response = (String) papServices.callPAP(newPAPPolicy, new String[]{"operation=" + operation, "apiflag=api",
                "policyType=Config"}, policyParameters.getRequestID(), "ConfigBrmsRaw");
        LOGGER.info(response);
        return response;
    }

}
