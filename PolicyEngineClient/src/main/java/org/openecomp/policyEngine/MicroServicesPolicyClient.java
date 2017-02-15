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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.openecomp.policy.api.PolicyChangeResponse;
import org.openecomp.policy.api.PolicyConfigType;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.api.PolicyType;
public class MicroServicesPolicyClient {
     
//For updating a Micro Services policy set the "isEdit" flag to true.   
//For creating a Micro Services policy set the "isEdit" flag to false.  
static Boolean isEdit = false;
     
//Builds JSONObject from File  
private static JsonObject buildJSON(File jsonInput, String jsonString) throws FileNotFoundException {
    JsonObject json = null;;
         
    if (jsonString != null && jsonInput == null) {
        StringReader in = null;
            in = new StringReader(jsonString);
        JsonReader jsonReader = Json.createReader(in);
            json = jsonReader.readObject();
    }
    else {
        InputStream in = null;
        in = new FileInputStream(jsonInput);
        JsonReader jsonReader = Json.createReader(in);
        json = jsonReader.readObject();
    }
         
    return json;
}
public static void main(String[] args) {
		try {
			PolicyEngine policyEngine = new PolicyEngine("config.properties");
			PolicyParameters policyParameters = new PolicyParameters();
			// Set Policy Type 
			policyParameters.setPolicyConfigType(PolicyConfigType.MicroService);
			policyParameters.setPolicyName("Katrina.configuration_dcae_microservice_stringmatcher");
			//policyParameters.setPolicyDescription("This is a sample Micro Service policy Create example");
			policyParameters.setEcompName("DCAE");
			//policyParameters.setConfigName("Collector");
			//policyParameters.setPriority("1");
			//policyParameters.setPolicyScope("service=vSCP;resource=F5;type=configuration;closedLoopControlName=vSCP_F5_Firewall_d925ed73-8231-4d02-9545-db4e113213abab322");
			
			// Set up Micro Services Attributes 
			File jsonFile = null;
			String MSjsonString= null;
			if (MSjsonString == null) {
				Path file = Paths.get("C:\\policyAPI\\MicroServicesJSON\\testStringMatching.json");
				jsonFile = file.toFile();
			}
			policyParameters.setConfigBody(buildJSON(jsonFile, MSjsonString).toString());		
			policyParameters.setConfigBodyType(PolicyType.JSON);

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
			
			// API method to create or update Policy. 
 	        PolicyChangeResponse response = null;
	        if (!isEdit) {
	            response = policyEngine.createPolicy(policyParameters);
	        } 
	        else {	
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
