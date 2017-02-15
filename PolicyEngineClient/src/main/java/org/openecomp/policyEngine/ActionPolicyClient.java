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

package org.openecomp.policyEngine;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openecomp.policy.api.AttributeType;
import org.openecomp.policy.api.PolicyChangeResponse;
import org.openecomp.policy.api.PolicyClass;
import org.openecomp.policy.api.PolicyConfigType;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.api.PolicyType;

public class ActionPolicyClient {
	static Boolean isEdit = true;
	public static void main(String[] args) {
		try {
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        PolicyParameters policyParameters = new PolicyParameters();
	        // Set Policy Type
	        policyParameters.setPolicyClass(PolicyClass.Action); //required
	        policyParameters.setPolicyName("MikeAPItesting.testActionAPI5"); //required
	        policyParameters.setPolicyDescription("This is a sample Action policy update example with no Action Body");  //optional
	        //policyParameters.setPolicyScope("MikeAPItesting"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI
	        
	        //Set the Component Attributes... These are Optional
	        Map<String, String> configAttributes = new HashMap<String, String>(); 
	        configAttributes.put("Template", "UpdateTemplate");
	        configAttributes.put("controller", "default"); 
	        configAttributes.put("SamPoll", "30");
	        configAttributes.put("value", "abcd"); 
	        
	        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
	        attributes.put(AttributeType.MATCHING, configAttributes);
	        policyParameters.setAttributes(attributes);

	        //Set the Rule Algorithm
	        // Map<String, Map<String, Map<String, String>>> translated to Map<Dynamic Label, Map<Field1,Map<Combo Operator, Field2>>>
	        Map<String, Map<String,Map<String,String>>> ruleAlgorithm = new HashMap<String, Map<String,Map<String,String>>>();
	        
			List<String> dynamicRuleAlgorithmLabels = new LinkedList<String>();
			List<String> dynamicRuleAlgorithmFunctions = new LinkedList<String>();
			List<String> dynamicRuleAlgorithmField1 = new LinkedList<String>();
			List<String> dynamicRuleAlgorithmField2 = new LinkedList<String>();
			
			//Example of a complex Rule algorithm
			/* label 	field1     	function				field2
			 * *****************************************************
			 * A1   	cobal   	integer-equal   			90
			 * A2   	cap     	string-contains     		ca
			 * A3		cobal		integer-equal				90	
			 * A4		A2			and							A3
			 * A5		Config		integer-greater-than		45
			 * A6		A4	`		or							A5
			 * A7		A1			and							A6
			 */
			dynamicRuleAlgorithmLabels = Arrays.asList("A1","A2","A3","A4","A5","A6","A7");
			dynamicRuleAlgorithmField1 = Arrays.asList("cobal","cap","cobal","A2","Config","A4","A1");
			dynamicRuleAlgorithmFunctions = Arrays.asList("integer-equal","string-contains","integer-equal","and","integer-greater-than","or","and");
			dynamicRuleAlgorithmField2 = Arrays.asList("90","ca","90","A3","45","A5","A6");
			        
			policyParameters.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
			policyParameters.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
			policyParameters.setDynamicRuleAlgorithmFunctions(dynamicRuleAlgorithmFunctions);
			policyParameters.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
			
	        policyParameters.setActionPerformer("PEP");
	        policyParameters.setActionAttribute("mikeTest2");
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


