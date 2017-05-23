/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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
package org.openecomp.policy.pap.xacml.rest.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.pap.xacml.rest.components.CreateBRMSRuleTemplate;
import org.openecomp.policy.pap.xacml.rest.components.CreateNewMicroSerivceModel;

public class ImportService {

	public void doImportMicroServicePut(HttpServletRequest request, HttpServletResponse response) {
		String importServiceCreation = request.getParameter("importService");;
		String fileName = request.getParameter("fileName");
		String version = request.getParameter("version");
		String serviceName = request.getParameter("serviceName");
		String description = request.getParameter("description");
		Map<String, String> successMap = new HashMap<>();
		switch(importServiceCreation){
		case "BRMSPARAM":
			StringBuilder builder = new StringBuilder();
			int ch;
			try {
				while((ch = request.getInputStream().read()) != -1){
				    builder.append((char)ch);
				}
			} catch (IOException e) {
				PolicyLogger.error("Error in reading in file from API call");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.addHeader("error", "missing");	
				response.addHeader("operation", "import");
				response.addHeader("service", serviceName);
			}
			CreateBRMSRuleTemplate brmsRuleTemplate = new CreateBRMSRuleTemplate();
			successMap = brmsRuleTemplate.addRule(builder.toString(), serviceName, description, "API");
			break;
		case "MICROSERVICE":
			CreateNewMicroSerivceModel newMS = null;
			String randomID = UUID.randomUUID().toString();
			if ( fileName != null) {
				File extracDir = new File("ExtractDir");
				if (!extracDir.exists()){
					extracDir.mkdirs();
				}
				if (fileName.contains(".xmi")){
					// get the request content into a String
					String xmi = null;
					java.util.Scanner scanner;
					try {
						scanner = new java.util.Scanner(request.getInputStream());
						scanner.useDelimiter("\\A");
						xmi =  scanner.hasNext() ? scanner.next() : "";
						scanner.close();
					} catch (IOException e1) {
						PolicyLogger.error("Error in reading in file from API call");
						return;
					}
					PolicyLogger.info("XML request from API for import new Service"); 
					try (Writer writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream("ExtractDir" + File.separator + randomID+".xmi"), "utf-8"))) {
						writer.write(xmi);
					} catch (IOException e) {
						PolicyLogger.error("Error in reading in file from API call");
						return;
					}
				}else{ 
					InputStream inputStream = null;
					FileOutputStream outputStream = null;
					try {	
						inputStream = request.getInputStream();
						outputStream = new FileOutputStream("ExtractDir" + File.separator + randomID+".zip"); 
						byte[] buffer = new byte[4096];
						int bytesRead = -1 ; 
						while ((bytesRead = inputStream.read(buffer)) != -1) { 
							outputStream.write(buffer, 0, bytesRead) ; 
						}
					} catch (IOException e) {
						PolicyLogger.error("Error in reading in Zip File from API call");
						return;
					}finally{
						try {
							if(inputStream != null){
								inputStream.close();
							}
							if(outputStream != null){
								outputStream.close();
							}
						} catch (IOException e) {
							PolicyLogger.error("Exception Occured while closing the input/output stream"+e);
						}
					}
				}
				newMS =  new CreateNewMicroSerivceModel(fileName, serviceName, "API", version, randomID);
				successMap = newMS.addValuesToNewModel();
				if (successMap.containsKey("success")) {
					successMap.clear();
					successMap = newMS.saveImportService();
				}
			}
			break;
		}
		// return a response to the PAP		    
		if (successMap.containsKey("success")) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("successMapKey", "success");								
			response.addHeader("operation", "import");
			response.addHeader("service", serviceName);
		} else if (successMap.containsKey("DBError")) {
			if (successMap.get("DBError").contains("EXISTS")){
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.addHeader("service", serviceName);
				response.addHeader("error", "modelExistsDB");
			}else{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.addHeader("error", "importDB");
			}
			response.addHeader("operation", "import");
			response.addHeader("service", serviceName);
		}else if (successMap.get("error").contains("MISSING")){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error", "missing");	
			response.addHeader("operation", "import");
			response.addHeader("service", serviceName);
		}else if (successMap.get("error").contains("VALIDATION")){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error", "validation");	
			response.addHeader("operation", "import");
			response.addHeader("service", serviceName);
		}
	}

}
