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
import javax.json.JsonException;
import javax.json.JsonObject;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.RuleProvider;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;

/**
 * Decision Policy Implementation.
 * 
 * @version 0.1
 */
public class DecisionPolicyService implements PolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(DecisionPolicyService.class.getName());
    private static PAPServices papServices = null;

    private String message = null;
    private String policyScope = null;
    private String policyName = null;
    private PolicyParameters policyParameters = null;
    private String onapName = null;

    /**
     * Instantiates a new decision policy service.
     *
     * @param policyScope the policy scope
     * @param policyName the policy name
     * @param policyParameters the policy parameters
     */
    public DecisionPolicyService(String policyScope, String policyName, PolicyParameters policyParameters) {
        this.policyScope = policyScope;
        this.policyName = policyName;
        this.policyParameters = policyParameters;
        papServices = new PAPServices();
    }

    /**
     * Gets the validation.
     *
     * @return the validation
     */
    public Boolean getValidation() {
        onapName = policyParameters.getOnapName();
        if (onapName == null || onapName.trim().isEmpty()) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ONAP Name given.";
            return false;
        }
        return true;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Gets the result.
     *
     * @param updateFlag the update flag
     * @return the result
     * @throws PolicyException the policy exception
     */
    public String getResult(boolean updateFlag) throws PolicyException {
        String operation = null;
        if (updateFlag) {
            operation = "update";
        } else {
            operation = "create";
        }
        RuleProvider ruleProvider = policyParameters.getRuleProvider();
        if (ruleProvider == null) {
            ruleProvider = RuleProvider.CUSTOM;
        }
        Map<String, String> matchingAttributes = null;
        Map<String, String> settingsAttributes = null;

        // Get the MATCHING and/or SETTINGS attributes
        if (policyParameters.getAttributes() != null
                && policyParameters.getAttributes().containsKey(AttributeType.MATCHING)
                && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)) {
            matchingAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
            settingsAttributes = policyParameters.getAttributes().get(AttributeType.SETTINGS);
        } else if (policyParameters.getAttributes() != null
                && !policyParameters.getAttributes().containsKey(AttributeType.MATCHING)
                && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)) {
            settingsAttributes = policyParameters.getAttributes().get(AttributeType.SETTINGS);
        } else if (policyParameters.getAttributes() != null
                && policyParameters.getAttributes().containsKey(AttributeType.MATCHING)
                && !policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)) {
            matchingAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
        }

        // Create StdPAPPolicy object used to send policy data to PAP-REST.
        StdPAPPolicy newPapPolicy = new StdPAPPolicy(policyName, policyParameters.getPolicyDescription(), onapName,
                ruleProvider.toString(), matchingAttributes, settingsAttributes, policyParameters.getTreatments(),
                policyParameters.getDynamicRuleAlgorithmLabels(), policyParameters.getDynamicRuleAlgorithmFunctions(),
                policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmField2(),
                null, null, null, updateFlag, policyScope, 0);
        // if it is decision MS, populate few more fields including config body
        if (RuleProvider.MICROSERVICE_MODEL.toString().equalsIgnoreCase(ruleProvider.toString())) {
            populateDecisionMsPolicy(newPapPolicy);
        }

        // Send JSON to PAP.
        String response = (String) papServices.callPAP(newPapPolicy,
                new String[] {"operation=" + operation, "apiflag=api", "policyType=Decision"},
                policyParameters.getRequestID(), "Decision");
        LOGGER.info(message);
        return response;
    }

    private void populateDecisionMsPolicy(StdPAPPolicy newPapPolicy) throws PolicyException {
        JsonObject microServiceAttributes = null;
        try {
            microServiceAttributes = PolicyApiUtils.stringToJsonObject(policyParameters.getConfigBody());
        } catch (JsonException | IllegalStateException e) {
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + " improper JSON object : "
                    + policyParameters.getConfigBody();
            LOGGER.error("Error while parsing JSON body for Decision MicroService Policy creation. ", e);
            throw new PolicyException(message);
        }

        if (microServiceAttributes.get("service") != null) {
            newPapPolicy.setServiceType(microServiceAttributes.get("service").toString().replace("\"", ""));
        }
        if (microServiceAttributes.containsKey("description")) {
            newPapPolicy.setPolicyDescription(microServiceAttributes.get("description").toString().replace("\"", ""));
        }

        if (microServiceAttributes.containsKey("version")) {
            newPapPolicy.setVersion(microServiceAttributes.get("version").toString().replace("\"", ""));
        }

        newPapPolicy.setConfigBodyData(policyParameters.getConfigBody());
        newPapPolicy.setJsonBody(policyParameters.getConfigBody());
    }

}
