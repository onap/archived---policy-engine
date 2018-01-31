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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;

public class ConfigFirewallPolicyClient {
	static Boolean isEdit = false;
	public static void main(String[] args) {
		try{
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        PolicyParameters policyParameters = new PolicyParameters();
	        // Set Policy Type
	        policyParameters.setPolicyConfigType(PolicyConfigType.Firewall); //required
	        policyParameters.setPolicyName("MikeAPItesting.testConfigFirewallPolicy1607_1"); //required
	        //policyParameters.setPolicyScope("MikeAPItesting"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI
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
			File jsonFile = null;
			String jsonRuleList = null;
			Path file = Paths.get("C:\\policyAPI\\firewallRulesJSON\\Config_FW_1607Rule.json");
			jsonFile = file.toFile();

			//buildJSON(jsonFile, jsonRuleList);
			policyParameters.setConfigBody(buildJSON(jsonFile, jsonRuleList).toString());
			policyParameters.setConfigBodyType(PolicyType.JSON);
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

	private static JsonObject buildJSON(File jsonInput, String jsonString) throws FileNotFoundException {
		JsonObject json = null;
		JsonReader jsonReader = null;
		if (jsonString != null && jsonInput == null) {
			StringReader in = null;
			in = new StringReader(jsonString);
			jsonReader = Json.createReader(in);
			json = jsonReader.readObject();
			in.close();
		} else {
			InputStream in = null;
			in = new FileInputStream(jsonInput);
			jsonReader = Json.createReader(in);
			json = jsonReader.readObject();
			try {
				in.close();
			} catch (IOException e) {
				System.err.println("Exception Occured while closing input stream"+e);
			}
		}
		jsonReader.close();
		return json;
	}

}
