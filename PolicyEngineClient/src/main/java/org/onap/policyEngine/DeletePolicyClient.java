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

package org.onap.policyEngine;

import org.onap.policy.api.DeletePolicyCondition;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.common.logging.flexlogger.Logger;

public class DeletePolicyClient {

	public static void main(String[] args) {
		try {

			PolicyEngine policyEngine = new PolicyEngine("config.properties");
			DeletePolicyParameters policyParameters = new DeletePolicyParameters();			
						
			//Parameter arguments
			policyParameters.setPolicyName("MikeConsole.Config_testDeleteAPI6.1.xml");
			policyParameters.setPolicyComponent("PDP");
			policyParameters.setPdpGroup("default");
			policyParameters.setDeleteCondition(DeletePolicyCondition.ALL);
			policyParameters.setRequestID(null);
			
            // API method to Push Policy to PDP
            PolicyChangeResponse response = null;
            response = policyEngine.deletePolicy(policyParameters);

            if(response.getResponseCode()==200){
                System.out.println(response.getResponseMessage());
                System.out.println("Policy Deleted Successfully!");
            }else{
                System.out.println("Error! " + response.getResponseMessage());
            }

		} catch (Exception e) {
			Logger l = null;
			l.error(e);
			System.err.println(e.getMessage());		
			
		}
		
	}
	

}
