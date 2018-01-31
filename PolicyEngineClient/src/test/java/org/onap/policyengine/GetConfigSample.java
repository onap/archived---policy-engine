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

import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyEngine;

public class GetConfigSample {

	public static void main(String[] args) throws Exception {
		PolicyEngine pe = new PolicyEngine("config.properties");
		ConfigRequestParameters configRequestParams = new ConfigRequestParameters();
		configRequestParams.setPolicyName(".*");
		Collection<PolicyConfig> configs = pe.getConfig(configRequestParams);
		for (PolicyConfig config: configs){
			System.out.println(config.getPolicyConfigMessage());
			System.out.println(config.getPolicyConfigStatus());
		}
	}
}
