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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyParameters;

public class DecisionPolicyClient {
	static Boolean isEdit = true;
	public static void main(String[] args) {
		try {
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        PolicyParameters policyParameters = new PolicyParameters();
	        // Set Policy Type
	        policyParameters.setPolicyClass(PolicyClass.Decision); //required
	        policyParameters.setPolicyName("MikeAPItests.testDecisionAPI"); //required
	        policyParameters.setOnapName("java"); //required
	        policyParameters.setPolicyDescription("This is a sample Decision policy UPDATE example with Settings");  //optional
	        //policyParameters.setPolicyScope("MikeAPItests"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI

	        //Set the Component Attributes... These are Optional
	        Map<String, String> configAttributes = new HashMap<>();
	        configAttributes.put("Template", "UpdateTemplate");
	        configAttributes.put("controller", "default");
	        configAttributes.put("SamPoll", "30");
	        configAttributes.put("value", "abcd");

	        Map<AttributeType, Map<String,String>> attributes = new HashMap<>();
	        attributes.put(AttributeType.MATCHING, configAttributes);

	        //Set the settings... These are Optional
	        Map<String, String> settingsMap = new HashMap<>();
	        settingsMap.put("server", "5");

	        attributes.put(AttributeType.SETTINGS, settingsMap);
	        policyParameters.setAttributes(attributes);


			List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
			List<String> dynamicRuleAlgorithmFunctions = new LinkedList<>();
			List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
			List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();

			//Example of a complex Rule algorithm using the settings in the Field1
			/* label 	field1     	function				field2
			 * *****************************************************
			 * A1   	S_server   	integer-equal   			90
			 * A2   	cap     	string-contains     		ca
			 * A3		cobal		integer-equal				90
			 * A4		A2			and							A3
			 * A5		Config		integer-greater-than		45
			 * A6		A4	`		or							A5
			 * A7		A1			and							A6
			 */
			dynamicRuleAlgorithmLabels = Arrays.asList("A1","A2","A3","A4","A5","A6","A7");
			dynamicRuleAlgorithmField1 = Arrays.asList("S_server","cap","cobal","A2","Config","A4","A1");
			dynamicRuleAlgorithmFunctions = Arrays.asList("integer-equal","string-contains","integer-equal","and","integer-greater-than","or","and");
			dynamicRuleAlgorithmField2 = Arrays.asList("90","ca","90","A3","45","A5","A6");

			policyParameters.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
			policyParameters.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
			policyParameters.setDynamicRuleAlgorithmFunctions(dynamicRuleAlgorithmFunctions);
			policyParameters.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);

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
