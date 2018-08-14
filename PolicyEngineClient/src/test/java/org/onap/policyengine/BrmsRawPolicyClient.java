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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class BrmsRawPolicyClient {
	static Boolean isEdit = true;
	
	//Reads a File and converts into a String. 
	private static String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
	        while( ( line = reader.readLine() ) != null ) {
	            stringBuilder.append( line );
	            stringBuilder.append( ls );
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}
	
	
	public static void main(String[] args) {
		try {
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        PolicyParameters policyParameters = new PolicyParameters();
	        Map<String, String> attrib= new HashMap<>();
	        attrib.put("cpu","80");
	        attrib.put("memory", "50");
	        Map<AttributeType, Map<String, String>> attributes = new HashMap<>();
            attributes.put(AttributeType.RULE, attrib);
	        
	        // Set Policy Type
	        policyParameters.setPolicyConfigType(PolicyConfigType.BRMS_RAW); //required
	        policyParameters.setPolicyName("Lakshman.testBRMSRawAPITwo"); //required
	        policyParameters.setPolicyDescription("This is a sample BRMS Raw policy body");  //optional
	        policyParameters.setAttributes(attributes);
	        //policyParameters.setPolicyScope("Lakshman"); //Directory will be created where the Policies are saved... this displays a a subscope on the GUI
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
	        
	        File rawBodyFile = null;
		
			Path file = Paths.get("C:\\Users\\testuser\\Documents\\API\\com.Config_BRMS_Raw_TestBrmsPolicy.1.txt");
			rawBodyFile = file.toFile();
			
			policyParameters.setConfigBody(readFile(rawBodyFile.toString()));		
			policyParameters.setConfigBodyType(PolicyType.OTHER);

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


