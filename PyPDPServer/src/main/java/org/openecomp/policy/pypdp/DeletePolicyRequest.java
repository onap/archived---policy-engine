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

import org.openecomp.policy.api.DeletePolicyParameters;
import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.std.StdPolicyEngine;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class DeletePolicyRequest {
	private StdPolicyEngine pe;
	public DeletePolicyRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public String run(DeletePolicyParameters pep, String requestID, String userID, String passcode) {

		String result = null;
		
		// construct a UUID from the request string
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
		
		if (pep.getPolicyName()!= null && !pep.getPolicyName().isEmpty()) {
			if (pep.getPolicyComponent() != null && !pep.getPolicyComponent().isEmpty()) {
						
				try {
							
					result = pe.deletePolicy(pep, userID, passcode).getResponseMessage();

					} catch (PolicyConfigException e) {
						result = e.getMessage();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			} else {
				result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyComponent was null or empty.";
			}
		} else {
			result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyName was null or empty.";
		}
		
		return result;
		
	}
}
