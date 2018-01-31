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

import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PushPolicyParameters;

public class PushPoliciesToPDP {
	public static void main(String[] args) {
		try {

			PolicyEngine policyEngine = new PolicyEngine("config.properties");
			PushPolicyParameters policyParameters = new PushPolicyParameters();

			//Parameter arguments
			policyParameters.setPolicyName("Mike.testCase1");
			policyParameters.setPolicyType("Base");
			//policyParameters.setPolicyScope("MikeAPItesting");
			policyParameters.setPdpGroup("default");
			policyParameters.setRequestID(null);

            // API method to Push Policy to PDP
            PolicyChangeResponse response = null;
            response = policyEngine.pushPolicy(policyParameters);

            if(response.getResponseCode()==204){
                System.out.println(response.getResponseMessage());
                System.out.println("Policy Pushed Successfully!");
            }else{
                System.out.println("Error! " + response.getResponseMessage());
            }

		} catch (Exception e) {
			System.err.println(e.getMessage());

		}

	}

}
