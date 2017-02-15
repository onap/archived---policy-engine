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

import java.util.UUID;

import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.pypdp.model_pojo.PepConfigPolicyRequest;
import org.openecomp.policy.std.StdPolicyEngine;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class PolicyCreateUpdateRequest {
	private StdPolicyEngine pe;
	public PolicyCreateUpdateRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public String run(PolicyParameters pep, String requestID, String operation, String userID, String passcode) {
		String result = null;
		if(pep.getRequestID()==null){
			if (requestID != null && !requestID.isEmpty()) {
				try {
					pep.setRequestID(UUID.fromString(requestID));
				}
				catch (IllegalArgumentException e) {
					pep.setRequestID(UUID.randomUUID());
					PolicyLogger.info("Generated Random UUID: " + pep.getRequestID().toString());
				}
			}
		}
		// check if this is create
		try{
			if (operation.equalsIgnoreCase("create")) {
				result = pe.createPolicy(pep, userID, passcode ).getResponseMessage();
			}else{
				// this is Update policy. 
				result = pe.updatePolicy(pep, userID, passcode ).getResponseMessage();
			}
		}catch(Exception e){
			result = e.getMessage();
		}
		return result;
	}
	
	public String run(PepConfigPolicyRequest pep, String requestID, String operation, String userID, String passcode) {

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
			if (pep.getEcompName() != null && !pep.getEcompName().isEmpty()) {
				if (pep.getConfigName() != null && !pep.getConfigName().isEmpty()){
					if (pep.getPolicyScope() != null && !pep.getPolicyScope().isEmpty()) {
						try {
							
							if (operation.equalsIgnoreCase("create")) {
								
								result = pe.createConfigPolicy(pep.getPolicyName(), pep.getPolicyDescription(), pep.getEcompName(), 
										pep.getConfigName(), pep.getConfigAttributes(), pep.getConfigType(), pep.getBody(), 
									pep.getPolicyScope(), requestUUID, userID, passcode, pep.getRiskLevel(), pep.getRiskType(), pep.getGuard(), pep.getTtlDate());	
							} else {
								result = pe.updateConfigPolicy(pep.getPolicyName(), pep.getPolicyDescription(), pep.getEcompName(), 
										pep.getConfigName(), pep.getConfigAttributes(), pep.getConfigType(), pep.getBody(), 
									pep.getPolicyScope(), requestUUID, userID, passcode, pep.getRiskLevel(), pep.getRiskType(), pep.getGuard(), pep.getTtlDate());	
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
					result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  configName was null or empty.";
				}
			} else {
				result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  ecompName was null or empty.";
			}
		} else {
			result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyName was null or empty.";
		}
		
		return result;
	}
}
