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
import org.onap.policy.api.RuleProvider;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;

/**
 * Decision Policy Implementation 
 * 
 * @version 0.1 
 */
public class DecisionPolicyService{
	private static Logger LOGGER = FlexLogger.getLogger(DecisionPolicyService.class.getName());
	private static PAPServices papServices = null;
	
	private String message = null;
	private String policyScope = null;
	private String policyName = null;
	private PolicyParameters policyParameters = null;
	private String onapName = null;
	
	public DecisionPolicyService(String policyScope, String policyName,
			PolicyParameters policyParameters) {
		this.policyScope = policyScope;
		this.policyName = policyName;
		this.policyParameters = policyParameters;
		papServices = new PAPServices();
	}

	public Boolean getValidation() {
	    onapName = policyParameters.getOnapName();
		if (onapName==null||onapName.trim().isEmpty()){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ONAP Name given.";
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
		RuleProvider ruleProvider = policyParameters.getRuleProvider();
		if (ruleProvider==null) {
			ruleProvider = RuleProvider.CUSTOM ;
		}
		Map<String,String> matchingAttributes = null;
		Map<String,String> settingsAttributes = null;
		if (policyParameters.getAttributes()!=null && policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)) {
			matchingAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
			settingsAttributes = policyParameters.getAttributes().get(AttributeType.SETTINGS);
		}else if(policyParameters.getAttributes()!=null && !policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)){
			settingsAttributes = policyParameters.getAttributes().get(AttributeType.SETTINGS);
		}else if(policyParameters.getAttributes()!=null && policyParameters.getAttributes().containsKey(AttributeType.MATCHING) && !policyParameters.getAttributes().containsKey(AttributeType.SETTINGS)){
			matchingAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
		}
		// Create Policy. 
		StdPAPPolicy newPAPPolicy = new StdPAPPolicy(policyName, policyParameters.getPolicyDescription(), onapName, ruleProvider.toString(), matchingAttributes, settingsAttributes, policyParameters.getDynamicRuleAlgorithmLabels(), 
				policyParameters.getDynamicRuleAlgorithmFunctions(), policyParameters.getDynamicRuleAlgorithmField1(), policyParameters.getDynamicRuleAlgorithmField2(), null, null, null, updateFlag, policyScope, 0);
		// Send JSON to PAP. 
		response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Decision"}, policyParameters.getRequestID(), "Decision");
		LOGGER.info(message);
		return response;
	}

}
