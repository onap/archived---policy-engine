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
 * MicroServices Policy implementation. 
 * 
 * @version 0.1
 */
public class MicroServicesPolicyService implements PolicyService {
	private static final Logger LOGGER = FlexLogger.getLogger(MicroServicesPolicyService.class.getName());
	
	private PAPServices papServices = null;
	private PolicyParameters policyParameters = null;
	private String message = null;
	private String policyName = null;
	private String policyScope = null;
	private String date = null;
	private String onapName = null;
	private JsonObject microServiceAttributes = null;
	
	public MicroServicesPolicyService(String policyName, String policyScope, PolicyParameters policyParameters, String date) {
		this.policyParameters = policyParameters;
		this.policyName = policyName;
		this.policyScope = policyScope;
		this.date = date;
		papServices = new PAPServices();
	}

	public Boolean getValidation() {
		if(policyParameters.getConfigBody()==null){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " No Micro Service or Attributes Config Body Present";
			return false;
		}
		try{
			microServiceAttributes = PolicyApiUtils.stringToJsonObject(policyParameters.getConfigBody());
		} catch(JsonException| IllegalStateException e){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE+ " improper JSON object : " + policyParameters.getConfigBody();
			LOGGER.error("Error while parsing JSON body for MicroService Policy creation. ", e);
			return false;
		}
		onapName = policyParameters.getOnapName();
		if (onapName==null||onapName.trim().isEmpty()){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Onap Name given.";
			return false;
		}
		boolean levelCheck = false;
		levelCheck = PolicyApiUtils.isNumeric(policyParameters.getRiskLevel());
		if (!levelCheck){
			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Incorrect Risk Level given.";
			return false;
		}
		return true;
	}

	public String getMessage() {
		return message;
	}
	
	public String getResult(boolean updateFlag) throws PolicyException{
	    String response = null;
        String operation = null;
        if (updateFlag){
            operation = "update";
        } else {
            operation = "create";
        }
        // get values and attributes from the JsonObject
        String uuid = null;
        String msLocation = null;
        String configName = null;
        String microService = null;
        String policyDescription=null;
        String priority=null;
        String version=null;
        
        if (microServiceAttributes.get("service")!=null){
        	microService = microServiceAttributes.get("service").toString().replace("\"", "");
        }
        if (microServiceAttributes.get("uuid")!=null){
            uuid = microServiceAttributes.get("uuid").toString().replace("\"", "");
        }
        if (microServiceAttributes.get("location")!=null){
            msLocation = microServiceAttributes.get("location").toString().replace("\"", "");
        }
        if (microServiceAttributes.get("configName")!=null){
            configName = microServiceAttributes.get("configName").toString().replace("\"", "");
        }
        if(microServiceAttributes.containsKey("description")){
        	policyDescription = microServiceAttributes.get("description").toString().replace("\"", "");
        }
        if(microServiceAttributes.containsKey("priority")){
        	priority = microServiceAttributes.get("priority").toString().replace("\"", "");
        }
        if(microServiceAttributes.containsKey("version")){
        	version = microServiceAttributes.get("version").toString().replace("\"", "");
        }
        // Create Policy. 
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Micro Service", policyName, policyDescription, onapName, 
                configName, microService, uuid, msLocation, microServiceAttributes.toString(), priority, 
                version, updateFlag, policyScope, 0, policyParameters.getRiskLevel(),
                policyParameters.getRiskType(), String.valueOf(policyParameters.getGuard()), date); 
        // Send JSON Object to PAP. 
        response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation="+operation, "apiflag=api", "policyType=Config"}, policyParameters.getRequestID(), "ConfigMS");
        LOGGER.info("Policy MS created Response: " + response);
        return response;
	}
}
