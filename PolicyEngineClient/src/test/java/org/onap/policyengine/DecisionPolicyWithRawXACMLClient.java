/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import java.util.UUID;

import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.RuleProvider;

public class DecisionPolicyWithRawXACMLClient {
	/*
	 * 
	 * To Create a Decision Policy Use case. User need provide valid PolicySet or Policy Type xacml.
	 * On Setting below required attributes the Decision Raw policy will be created successfully.
	 * 
	 * */
	static Boolean isEdit = true;
	public static void main(String[] args) {
		try {
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        PolicyParameters policyParameters = new PolicyParameters();
	        // Set Policy Type
	        policyParameters.setPolicyClass(PolicyClass.Decision); //required
	        policyParameters.setPolicyName("com.testRawDecision"); //required
	        policyParameters.setRuleProvider(RuleProvider.RAW);//required
	        policyParameters.setRawXacmlPolicy("");//required - The Raw XACML Policy Set or Policy and escape xml and remove line breaks.
	        policyParameters.setRequestID(UUID.randomUUID());
	        
	        // API method to create Policy or update policy
	        PolicyChangeResponse response = null;
	        if (!isEdit) {
	            response = policyEngine.createPolicy(policyParameters);
	        } else {
	        	response = policyEngine.updatePolicy(policyParameters);
	        }
	        
	        if(response.getResponseCode()==200){
	            System.out.println(response.getResponseMessage());
	            System.out.println("Policy Created Successfully!");
	        }else{
	            System.out.println("Error! " + response.getResponseMessage());
	        }
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
