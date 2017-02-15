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
import org.openecomp.policy.pypdp.model_pojo.PepPushPolicyRequest;
import org.openecomp.policy.std.StdPolicyEngine;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class PushPolicyRequest {
	private StdPolicyEngine pe;
	public PushPolicyRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public String run(PepPushPolicyRequest pep, String requestID, String userID, String passcode) {

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
		}else{
			requestUUID = UUID.randomUUID();
			PolicyLogger.error("No Request UUID Given, hence generating one random ID: " + requestUUID.toString());
		}
		String policyName = pep.getPolicyName();
		String policyScope = pep.getPolicyScope();
		if(policyName==null || policyName.isEmpty()){
			return XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyName was null or empty.";
		}
		if(policyScope== null || policyScope.isEmpty()){
			try{
				policyName = pep.getPolicyName().substring(pep.getPolicyName().lastIndexOf(".")+1, pep.getPolicyName().length());
				policyScope = pep.getPolicyName().substring(0, pep.getPolicyName().lastIndexOf("."));
			} catch (Exception e){
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "BAD REQUEST:  policyScope was null or empty.");
				return XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyScope was null or empty.";
			}
		}
		PolicyLogger.info("policyName: "  + policyName + " policyScope is : " + policyScope);
		if (pep.getPolicyType() != null && !pep.getPolicyType().isEmpty()) {
			if (pep.getPdpGroup() != null && !pep.getPdpGroup().isEmpty()) {
				try {
					result = pe.pushPolicy(policyScope ,policyName , pep.getPolicyType(), pep.getPdpGroup(), requestUUID, userID, passcode);
				} catch (PolicyConfigException e) {
					result = e.getMessage();
				} catch (Exception e) {
					result = e.getMessage();
				}
			} else {
				result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyGroup was null or empty.";
			}
		} else {
			result = XACMLErrorConstants.ERROR_DATA_ISSUE + "BAD REQUEST:  policyType was null or empty.";
		}
		return result;
	}
}
