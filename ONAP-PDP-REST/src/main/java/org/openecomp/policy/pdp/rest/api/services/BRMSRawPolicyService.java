/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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
package org.openecomp.policy.pdp.rest.api.services;

import java.util.Map;

import org.openecomp.policy.api.AttributeType;
import org.openecomp.policy.api.PolicyException;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pdp.rest.api.utils.PolicyApiUtils;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.std.pap.StdPAPPolicy;

/**
 * BRMS RAW Policy Implementation. 
 * 
 * @version 0.1
 */
public class BRMSRawPolicyService{
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
		if(brmsRawBody==null || brmsRawBody.trim().isEmpty()){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Rule Body given";
			return false;
		}
		message = PolicyUtils.brmsRawValidate(brmsRawBody);
		if(message.contains("[ERR")){
		    message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Raw rule given is invalid" +message;
		    return false;
		}
		levelCheck = PolicyApiUtils.isNumeric(policyParameters.getRiskLevel());
		if(!levelCheck){
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
		if (updateFlag){
			operation = "update";
		} else {
			operation = "create";
		}
		Map<String,String> ruleAttributes = null;
		if(policyParameters.getAttributes()!=null){
			ruleAttributes = policyParameters.getAttributes().get(AttributeType.RULE);
		}
		// Create Policy 
		StdPAPPolicy newPAPPolicy = new StdPAPPolicy("BRMS_Raw",policyName,policyParameters.getPolicyDescription(),
				"BRMS_RAW_RULE",updateFlag,policyScope, ruleAttributes, 0, "DROOLS", 
				brmsRawBody, policyParameters.getRiskLevel(),
				policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date,  policyParameters.getControllerName(), policyParameters.getDependencyNames());
		// Send JSON to PAP 
		response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, policyParameters.getRequestID(), "ConfigBrmsRaw");
		LOGGER.info(response);
		return response;
	}

}
