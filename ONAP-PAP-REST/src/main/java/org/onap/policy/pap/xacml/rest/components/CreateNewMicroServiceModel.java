/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.util.MSAttributeObject;
import org.onap.policy.rest.util.MSModelUtils;
import org.onap.policy.rest.util.MSModelUtils.MODEL_TYPE;

import com.google.gson.Gson;

public class CreateNewMicroServiceModel {
	private static final Logger logger = FlexLogger.getLogger(CreateNewMicroServiceModel.class);
	private MicroServiceModels newModel = null;
	private HashMap<String,MSAttributeObject > classMap = new HashMap<>();

	
	MSModelUtils utils = new MSModelUtils(XACMLPapServlet.getMsOnapName(), XACMLPapServlet.getMsPolicyName());

	public CreateNewMicroServiceModel(String fileName, String serviceName, String string, String version) {
		super();
	}

	public CreateNewMicroServiceModel(String importFile, String  modelName, String description, String version, String randomID, boolean decisionModel) {
	
		this.newModel = new MicroServiceModels();
		this.newModel.setVersion(version);
		this.newModel.setModelName(modelName);
		UserInfo userInfo = new UserInfo();
		userInfo.setUserLoginId("API");
		this.newModel.setUserCreatedBy(userInfo);
		this.newModel.setDecisionModel(decisionModel);
		String cleanUpFile = null;
	
	    Map<String, MSAttributeObject> tempMap = new HashMap<>();
	    //Need to delete the file
	    if (importFile.contains(".zip")){
	    	extractFolder(randomID + ".zip");
	        File directory = new File("ExtractDir" + File.separator + randomID);
	        List<File> fileList = listModelFiles(directory.toString());
	        //get all the files from a director
	        for (File file : fileList){
	            if (file.isFile()){
	            	int i = file.getName().lastIndexOf('.');
	            	String type = file.getName().substring(i+1);
	            	
	            	if(type != null && "yml".equalsIgnoreCase(type)){
	            		
	            		processYmlModel(file.toString(), modelName);
	            		
	            	}else{
	            		
				        tempMap = utils.processEpackage(file.getAbsolutePath(), MODEL_TYPE.XMI);
				        classMap.putAll(tempMap);
	            	}
	            }
	        }
	        cleanUpFile = "ExtractDir" + File.separator + randomID + ".zip";
	        try {
				FileUtils.deleteDirectory(new File("ExtractDir" + File.separator + randomID));
				FileUtils.deleteDirectory(new File(randomID));
				File deleteFile = new File(cleanUpFile); 
				FileUtils.forceDelete(deleteFile);
			} catch (IOException e) {
				logger.error("Failed to unzip model file " + randomID, e);
			}
	    }else {	    	
	    	if(importFile.contains(".yml")){
	    		
				processYmlModel("ExtractDir" + File.separator + randomID+".yml", modelName);
			    cleanUpFile = "ExtractDir" + File.separator + randomID+".yml";
	    		
	    	}else{
			    tempMap = utils.processEpackage("ExtractDir" + File.separator + randomID+".xmi", MODEL_TYPE.XMI);
			    classMap.putAll(tempMap);
			    cleanUpFile = "ExtractDir" + File.separator + randomID+".xmi";
	    	}
	    	
		    File deleteFile = new File(cleanUpFile); 
			deleteFile.delete();
	    }
	}
	
	private void processYmlModel(String fileName, String  modelName){

		try {
			
			
			utils.parseTosca(fileName);
			
			MSAttributeObject msAttributes= new MSAttributeObject();
			msAttributes.setClassName(modelName);
			
			LinkedHashMap<String, String> returnAttributeList =new LinkedHashMap<>();
			returnAttributeList.put(modelName, utils.getAttributeString());
			msAttributes.setAttribute(returnAttributeList);
			
			msAttributes.setSubClass(utils.getRetmap());
			
			msAttributes.setMatchingSet(utils.getMatchableValues());
			
			LinkedHashMap<String, String> returnReferenceList =new LinkedHashMap<>();

			returnReferenceList.put(modelName, utils.getReferenceAttributes());
			msAttributes.setRefAttribute(returnReferenceList);
			
			if(utils.getListConstraints()!=""){
				LinkedHashMap<String, String> enumList =new LinkedHashMap<>();
				String[] listArray=utils.getListConstraints().split("#");
                for(String str:listArray){
                    String[] strArr= str.split("=");
                    if(strArr.length>1){
                        enumList.put(strArr[0], strArr[1]);
                    }
                }
				msAttributes.setEnumType(enumList);
			}
			
			classMap=new LinkedHashMap<>();
			classMap.put(modelName, msAttributes);
			
		} catch (Exception e) {
			logger.error("Failed to process yml model" + e);
		}
	
	}
	
	private List<File> listModelFiles(String directoryName) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<>();
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				resultList.add(file);
			} else if (file.isDirectory()) {
				resultList.addAll(listModelFiles(file.getAbsolutePath()));
			}
		}
		return resultList;
	}

	@SuppressWarnings("rawtypes")
	private void extractFolder(String zipFile) {
	    int BUFFER = 2048;
	    File file = new File(zipFile);

	    ZipFile zip = null;
		try {
			zip = new ZipFile("ExtractDir" + File.separator +file);
		    String newPath =  zipFile.substring(0, zipFile.length() - 4);
		    new File(newPath).mkdir();
		    Enumeration zipFileEntries = zip.entries();
	
		    // Process each entry
		    while (zipFileEntries.hasMoreElements()){
		        // grab a zip file entry
		        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
		        String currentEntry = entry.getName();
		        File destFile = new File("ExtractDir" + File.separator + newPath + File.separator + currentEntry);
		        File destinationParent = destFile.getParentFile();
	
		        destinationParent.mkdirs();
	
		        if (!entry.isDirectory()){
		            BufferedInputStream is = new BufferedInputStream(zip
		            .getInputStream(entry));
		            int currentByte;

		            byte data[] = new byte[BUFFER];
					try(FileOutputStream fos = new FileOutputStream(destFile);
						BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {

						while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, currentByte);
						}
						dest.flush();
					}
		            is.close();
		        }
	
		        if (currentEntry.endsWith(".zip")){
		            extractFolder(destFile.getAbsolutePath());
		        }
		    }
	    } catch (IOException e) {
			logger.error("Failed to unzip model file " + zipFile + e);
		}finally{
			if(zip != null){
				try {
					zip.close();
				} catch (Exception e) {
					logger.error("Exception Occured while closing the zip file"+e);
				}
			}
		}
	}

	public Map<String, String> addValuesToNewModel(String type) {
		
		Map<String, String> successMap = new HashMap<>();
		MSAttributeObject mainClass  = null;
		List<String> dependency = null;
		String subAttribute = null;
		
		if (!classMap.containsKey(this.newModel.getModelName())){
			logger.error("Model Provided does not contain the service name provided in request. Unable to import new model");
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "AddValuesToNewModel", "Unable to pull out required values, file missing service name provided in request");
			successMap.put("error", "MISSING");
			return successMap;
		}
		mainClass = classMap.get(this.newModel.getModelName());
		
		
		if(".yml".equalsIgnoreCase(type)){
			
			newModel.setDependency("[]");
			if(mainClass.getSubClass() != null){
			   String value = new Gson().toJson(mainClass.getSubClass());
			   value = value.replace("\"{","{");
               value = value.replace("}\"","}");
               value = value.replace("\\\"","\"");
			   newModel.setSub_attributes(value);
			}
			
			if(mainClass.getAttribute() != null){
				String attributes= mainClass.getAttribute().toString().replace("{", "").replace("}", "");
				int equalsIndexForAttributes= attributes.indexOf("=");
				String atttributesAfterFirstEquals= attributes.substring(equalsIndexForAttributes+1);
				this.newModel.setAttributes(atttributesAfterFirstEquals);
			}
			
			if(mainClass.getRefAttribute() != null){
				String refAttributes= mainClass.getRefAttribute().toString().replace("{", "").replace("}", "");
				int equalsIndex= refAttributes.indexOf("=");
				String refAttributesAfterFirstEquals= refAttributes.substring(equalsIndex+1);
				this.newModel.setRef_attributes(refAttributesAfterFirstEquals);
			}
			
			if(mainClass.getEnumType() != null){
			    this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
			}
			
			if(mainClass.getMatchingSet() != null){
			    this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
			}
			
		}else{
		
			String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
			this.newModel.setDependency(dependTemp);
			if (this.newModel.getDependency() != null && !this.newModel.getDependency().isEmpty()){
				dependency = new ArrayList<String>(Arrays.asList(dependTemp.split(",")));	
				dependency = utils.getFullDependencyList(dependency, classMap);
				if (!dependency.isEmpty()){
					for (String element : dependency){
						MSAttributeObject temp = new MSAttributeObject();
						if (classMap.containsKey(element)){
							temp = classMap.get(element);
							mainClass.addAllRefAttribute(temp.getRefAttribute());
							mainClass.addAllAttribute(temp.getAttribute());
						}
					}
				}		
			}
			subAttribute = utils.createSubAttributes(dependency, classMap, this.newModel.getModelName());
	
			this.newModel.setSub_attributes(subAttribute);
			if(mainClass.getAttribute() != null && !mainClass.getAttribute().isEmpty()){
			    this.newModel.setAttributes(mainClass.getAttribute().toString().replace("{", "").replace("}", ""));
			}
			
			if(mainClass.getRefAttribute() != null && !mainClass.getRefAttribute().isEmpty()){
			   this.newModel.setRef_attributes(mainClass.getRefAttribute().toString().replace("{", "").replace("}", ""));
			}
			
			if(mainClass.getEnumType() != null && !mainClass.getEnumType().isEmpty()){
		        this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
			}
			
			if(mainClass.getMatchingSet() != null && !mainClass.getMatchingSet().isEmpty()){
	            this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
			}
		}
		successMap.put("success", "success");
		return successMap;
		
	}
	
	public Map<String, String> saveImportService(){
		String modelName = this.newModel.getModelName();
		String imported_by = "API";
		String version = this.newModel.getVersion();
		Map<String, String> successMap = new HashMap<>();
		CommonClassDaoImpl dbConnection = new CommonClassDaoImpl();
		List<Object> result = dbConnection.getDataById(MicroServiceModels.class, "modelName:version", modelName+":"+version);
		if(result.isEmpty()){
			MicroServiceModels model = new MicroServiceModels();
			model.setModelName(modelName);
			model.setVersion(version);
			model.setAttributes(this.newModel.getAttributes());
			model.setAnnotation(this.newModel.getAnnotation());
			model.setDependency(this.newModel.getDependency());
			model.setDescription(this.newModel.getDescription());
			model.setEnumValues(this.newModel.getEnumValues());
			model.setRef_attributes(this.newModel.getRef_attributes());
			model.setSub_attributes(this.newModel.getSub_attributes());
			model.setDataOrderInfo(this.newModel.getDataOrderInfo());
			model.setDecisionModel(this.newModel.isDecisionModel());
			UserInfo userInfo = new UserInfo();
			userInfo.setUserLoginId(imported_by);
			userInfo.setUserName(imported_by);
			model.setUserCreatedBy(userInfo);
			dbConnection.save(model);
			successMap.put("success", "success");
		}else{
			successMap.put("DBError", "EXISTS");
			logger.error("Import new service failed.  Service already exists");
		}		
		return successMap;
	}
}
