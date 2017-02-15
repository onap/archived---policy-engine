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

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.openecomp.policy.api.EventRequestParameters;
import org.openecomp.policy.api.PolicyResponse;
import org.openecomp.policy.api.PolicyResponseStatus;
import org.openecomp.policy.std.StdPolicyEngine;
import org.openecomp.policy.std.StdPolicyResponse;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class EventRequest {
	
	private StdPolicyEngine pe;
	public EventRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public Collection<PolicyResponse> run(EventRequestParameters pep, String requestID, String userID, String passcode){
		StdPolicyResponse policyResponse = new StdPolicyResponse();
		Collection<PolicyResponse> result = new ArrayList<PolicyResponse>();
		// construct a UUID from the request string
		if(pep.getRequestID()==null){ 
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
			pep.setRequestID(requestUUID);
		}
		try {
			Collection<PolicyResponse> pResponses = pe.event(pep.getEventAttributes(), pep.getRequestID(), userID, passcode);
			for(PolicyResponse pResponse: pResponses){
				pResponse = checkResponse(pResponse);
				result.add(pResponse);
			}
			return result;
		} catch(Exception e){
			policyResponse.setPolicyResponseStatus(e.getMessage(), PolicyResponseStatus.NO_ACTION_REQUIRED);
			policyResponse = checkResponse(policyResponse);
			result.add(policyResponse);
			return result;
		}
	}
	
	private StdPolicyResponse checkResponse(PolicyResponse pResponse) {
		StdPolicyResponse policyResponse= new StdPolicyResponse();
		policyResponse.setActionAdvised(pResponse.getActionAdvised());
		policyResponse.setActionTaken(pResponse.getActionTaken());
		policyResponse.setPolicyResponseMessage(pResponse.getPolicyResponseMessage());
		policyResponse.setPolicyResponseStatus(pResponse.getPolicyResponseStatus());
		policyResponse.setRequestAttributes(pResponse.getRequestAttributes());
		return policyResponse;
	}
}
