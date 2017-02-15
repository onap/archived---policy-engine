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

import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.std.StdPolicyConfig;
import org.openecomp.policy.std.StdPolicyEngine;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class ListConfigRequest {
	
	private StdPolicyEngine pe;
	public ListConfigRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public Collection<String> run(ConfigRequestParameters pep, String requestID, String userID, String passcode) {

		StdPolicyConfig policyConfig = new StdPolicyConfig();
		Collection<String> configList = new ArrayList<String>();

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
		pep.setRequestID(requestUUID);
		try {
			PolicyLogger.debug("\n\n calling PEP.. ");
			configList = pe.listConfigRequest(pep, userID, passcode);
			return configList;
		} catch(Exception e){
			policyConfig.setConfigStatus(e.getMessage(), PolicyConfigStatus.CONFIG_NOT_FOUND);
			configList.add(policyConfig.getPolicyConfigStatus().toString());
			return configList;
		}
	}

}
