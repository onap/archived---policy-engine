/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.pypdp;

import java.io.StringReader;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.pypdp.model_pojo.PepConfigFirewallPolicyRequest;
import org.openecomp.policy.std.StdPolicyEngine;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class ConfigFirewallPolicyRequest {

	private StdPolicyEngine pe;
	public ConfigFirewallPolicyRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public String run(PepConfigFirewallPolicyRequest pep, String requestID, String operation, String userID, String passcode) {

		String result = null;
		
		// construct a UUID from the request string
		UUID requestUUID = null;
		if (requestID != null && !requestID.isEmpty()) {
			try {
				requestUUID = UUID.fromString(requestID);
			}
			catch (IllegalArgumentException e) {
				requestUUID = UUID.randomUUID();
				PolicyLogger.info("Generated Random UUID: " + requestUUID.toString());
			}
		}
		
		if (pep.getPolicyName()!= null && !pep.getPolicyName().isEmpty()) {
			if (pep.getFirewallJson() != null && !pep.getFirewallJson().isEmpty()) {
				if (pep.getPolicyScope() != null && !pep.getPolicyScope().isEmpty()) {
					try {
						
						JsonObject json = stringToJson(pep.getFirewallJson());
						
						if(!json.toString().contains("errorMessage")){
							if (operation.equalsIgnoreCase("create")) {
								result = pe.createConfigFirewallPolicy(pep.getPolicyName(), json, pep.getPolicyScope(), requestUUID, userID, passcode,
										pep.getRiskLevel(), pep.getRiskType(), pep.getGuard(), pep.getTtlDate());
							} else {
								result = pe.updateConfigFirewallPolicy(pep.getPolicyName(), json, pep.getPolicyScope(), requestUUID, userID, passcode,
										pep.getRiskLevel(), pep.getRiskType(), pep.getGuard(), pep.getTtlDate());
							}
						} else {
							result = XACMLErrorConstants.ERROR_SCHEMA_INVALID + "BAD REQUEST:  Invalid Json for firewallJson: " + pep.getFirewallJson();
						}
					} catch (PolicyConfigException e) {
						result = e.getMessage();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyScope was null or empty.";
				}
			} else {
				result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  firewallJson was null or empty.";
			}
		} else {
			result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyName was null or empty.";
		}
		
		return result;
		
	}
	
	private JsonObject stringToJson(String jsonString) {
		
		JsonObject json = null;
		if (jsonString != null) {
			
			try {
				
				//Read jsonBody to JsonObject
				StringReader in = null;
				
				in = new StringReader(jsonString);
				
				JsonReader jsonReader = Json.createReader(in);
				json = jsonReader.readObject();
				
			} catch (Exception e) {
				String jsonError = "{\"errorMessage\": \"" +  e.getMessage() + "\"}";
				StringReader error = null;
				error = new StringReader(jsonError);
				JsonReader jsonReader = Json.createReader(error);
				JsonObject badJson = jsonReader.readObject();
				return badJson;
			}

		}
		
		return json;
	}
	
}
