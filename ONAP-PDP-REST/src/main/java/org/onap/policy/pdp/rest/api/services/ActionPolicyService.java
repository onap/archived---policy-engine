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
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;

/**
 * Action Policy Implementation. 
 * 
 * @version 0.1
 */
public class ActionPolicyService {
    private static final Logger LOGGER = FlexLogger.getLogger(ActionPolicyService.class.getName());
    private PAPServices papServices = null;

    private PolicyParameters policyParameters = null;
    private String message = null;
    private String policyName = null;
    private String policyScope = null;
    private Map<String,String> componentAttributes = null;
    private String actionAttribute = null;
    private String actionPerformer = null;

    public ActionPolicyService(String policyScope, String policyName,
            PolicyParameters policyParameters) {
        this.policyParameters = policyParameters;
        this.policyName = policyName;
        this.policyScope = policyScope;
        papServices = new PAPServices();
    }

    public Boolean getValidation() {
        if(policyParameters.getAttributes()==null || policyParameters.getAttributes().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Component Attributes given.";
            return false;
        }
        componentAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
        if (componentAttributes==null||componentAttributes.isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Component Attributes given.";
            return false;
        }
        actionAttribute = policyParameters.getActionAttribute();
        if (actionAttribute==null||actionAttribute.trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Action Attribute given.";
            return false;
        }
        actionPerformer = policyParameters.getActionPerformer();
        if (actionPerformer==null||actionPerformer.trim().isEmpty()){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Action Performer given.";
            return false;
        }
        if(!"PEP".equalsIgnoreCase(actionPerformer)&& !"PDP".equalsIgnoreCase(actionPerformer)){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Action Performer given.";
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
        if (updateFlag){
            operation = "update";
        } else {
            operation = "create";
        }
        // Create Policy
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy(policyName, policyParameters.getPolicyDescription(),
                componentAttributes, policyParameters.getDynamicRuleAlgorithmLabels(), policyParameters.getDynamicRuleAlgorithmFunctions(),
                policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmField2(), actionPerformer, actionAttribute, updateFlag, policyScope, 0);
        // send Json to PAP
        response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Action"}, policyParameters.getRequestID(), "Action");
        LOGGER.info(response);
        return response;
    }
}
