/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
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

package org.onap.policyengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class ListConfigPoliciesClient {

	private static final Logger LOGGER	= FlexLogger.getLogger(ListConfigPoliciesClient.class);

	public static void main(String[] args) {
		PolicyEngine policyEngine;

		// List Config Policies Example
		try {
			policyEngine = new PolicyEngine("config.properties");
			ConfigRequestParameters parameters = new ConfigRequestParameters();

			parameters.setPolicyName(".*");
			parameters.setOnapName(".*");
			parameters.setConfigName(".*");

			Map<String, String> configAttributes = new HashMap<>();
			configAttributes.put("java", "java");
			configAttributes.put("peach", "Tar");
			configAttributes.put("true", "false");
			configAttributes.put("small", "testPass");
			parameters.setConfigAttributes(configAttributes);

			parameters.setRequestID(UUID.randomUUID());

			Collection<String> response = policyEngine.listConfig(parameters);
	        if(response!=null && !response.contains("PE300")){
	        	for(String configList : response){
	        		System.out.println(configList.toString()+"\n");
	        	}
	        }else{
	            System.out.println("Error! " +response);
	        }

		} catch (PolicyConfigException e) {
			LOGGER.error("Exception Occured"+e);
		} catch (PolicyEngineException e) {
			LOGGER.error("Exception Occured"+e);
		}
	}

}
