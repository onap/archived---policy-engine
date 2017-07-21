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
 * Config Base Policy Implementation. 
 * 
 * @version 0.1
 */
public class ConfigPolicyService {
	private static Logger LOGGER = FlexLogger.getLogger(ConfigPolicyService.class.getName());
	private static PAPServices papServices = null;
	
	private PolicyParameters policyParameters = null;
	private String message = null;
	private String policyName = null;
	private String policyScope = null;
	private String date = null;
	private boolean levelCheck = false;
	private String ecompName = null;
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
		if(policyParameters.getConfigBody()==null || policyParameters.getConfigBody().trim().isEmpty()){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body given.";
			return false;
		}
		if(policyParameters.getConfigBodyType()==null){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE+ "No Config Body Type given.";
			return false;
		}
		levelCheck = PolicyApiUtils.isNumeric(policyParameters.getRiskLevel());
		if (!levelCheck){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
			return false;
		}
		ecompName = policyParameters.getEcompName();
		configName = policyParameters.getConfigName();
		if(ecompName==null || ecompName.trim().isEmpty()){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No ECOMP Name given.";
			return false;
		}
		if(configName==null || configName.trim().isEmpty()){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Config Name given.";
			return false;
		}
		message = PolicyUtils.emptyPolicyValidator(ecompName);
        if(!message.contains("success")){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ message;
            return false;
        }
        message = PolicyUtils.emptyPolicyValidator(configName);
        if(!message.contains("success")){
            message = XACMLErrorConstants.ERROR_DATA_ISSUE+ message;
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
		String configType = policyParameters.getConfigBodyType().toString();
		String body = policyParameters.getConfigBody();
		String configBody = null;
		//check body for JSON form and remove single quotes if present
		if (configType.equalsIgnoreCase("JSON")) {
			if (body.contains("'")) {
				configBody = body.replace("'", "\"");
			} else {
				configBody = body;
			}
		} else {
			configBody = body;
		}
		Map<String,String> configAttributes = null;
		if(policyParameters.getAttributes()!=null){
			configAttributes = policyParameters.getAttributes().get(AttributeType.MATCHING);
		}
		// create Policy. 
		StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Base", policyName, policyParameters.getPolicyDescription(), ecompName, configName, configAttributes, configType, 
				configBody, updateFlag, policyScope,0, policyParameters.getRiskLevel(),policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date);
		// Send Json to PAP. 
		response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, policyParameters.getRequestID(), "Config");
		LOGGER.info(response);
		return response;
	}

}
