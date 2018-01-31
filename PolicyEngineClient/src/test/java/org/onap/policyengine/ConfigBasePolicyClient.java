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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;

public class ConfigBasePolicyClient{
	static Boolean isEdit = false;
	public static void main(String[] args) {
        try {
            PolicyEngine policyEngine = new PolicyEngine("config.properties");
            PolicyParameters policyParameters = new PolicyParameters();
            // Set Policy Type
            policyParameters.setPolicyConfigType(PolicyConfigType.Base); //required
            policyParameters.setPolicyName("MikeConsole.testDeleteAPI6"); //required
            policyParameters.setPolicyDescription("This is a sample Config Base policy creation example");  //optional
            policyParameters.setOnapName("DCAE");  //required
            policyParameters.setConfigName("testBase"); //required
            policyParameters.setConfigBodyType(PolicyType.OTHER); //required
            policyParameters.setConfigBody("testing");  //required

            //Set the Config Attributes... These are Optional
            Map<String, String> configAttributes = new HashMap<>();
            configAttributes.put("Template", "SampleTemplate");
            configAttributes.put("controller", "default");
            configAttributes.put("SamPoll", "30");
            configAttributes.put("value", "abcd");
            Map<AttributeType, Map<String,String>> attributes = new HashMap<>();
            attributes.put(AttributeType.MATCHING, configAttributes);
            policyParameters.setAttributes(attributes);
            policyParameters.setRequestID(UUID.randomUUID());
            // Set Safe Policy value for Risk Type
			SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
			Date date = dateformat3.parse("15/10/2016");
			policyParameters.setTtlDate(date);
			// Set Safe Policy value for Guard
			policyParameters.setGuard(true);
			// Set Safe Policy value for Risk Level
			policyParameters.setRiskLevel("5");
			// Set Safe Policy value for Risk Type
			policyParameters.setRiskType("PROD");

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
