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

public class CreateNewMicroServiceModel {
	private static final Logger logger = FlexLogger.getLogger(CreateNewMicroServiceModel.class);
	private MicroServiceModels newModel = null;
	private HashMap<String,MSAttributeObject > classMap = new HashMap<>();

	
	MSModelUtils utils = new MSModelUtils(XACMLPapServlet.getMsOnapName(), XACMLPapServlet.getMsPolicyName());

	public CreateNewMicroServiceModel(String fileName, String serviceName, String string, String version) {
		super();
	}

	public CreateNewMicroServiceModel(String importFile, String  modelName, String description, String version, String randomID) {
	
		this.newModel = new MicroServiceModels();
		this.newModel.setVersion(version);
		this.newModel.setModelName(modelName);
		UserInfo userInfo = new UserInfo();
		userInfo.setUserLoginId("API");
		this.newModel.setUserCreatedBy(userInfo);
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
				    tempMap = utils.processEpackage(file.getAbsolutePath(), MODEL_TYPE.XMI);
				    classMap.putAll(tempMap);
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
		    tempMap = utils.processEpackage("ExtractDir" + File.separator + randomID+".xmi", MODEL_TYPE.XMI);
		    classMap.putAll(tempMap);
		    cleanUpFile = "ExtractDir" + File.separator + randomID+".xmi";
		    File deleteFile = new File(cleanUpFile); 
			deleteFile.delete();
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
					try(FileOutputStream fos = new FileOutputStream(destFile)) {
						BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
						while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, currentByte);
						}
						dest.flush();
						dest.close();
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

	public Map<String, String> addValuesToNewModel() {
		
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
		String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
		this.newModel.setDependency(dependTemp);
		if (!this.newModel.getDependency().equals("")){
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
		this.newModel.setAttributes(mainClass.getAttribute().toString().replace("{", "").replace("}", ""));
		this.newModel.setRef_attributes(mainClass.getRefAttribute().toString().replace("{", "").replace("}", ""));
		this.newModel.setEnumValues(mainClass.getEnumType().toString().replace("{", "").replace("}", ""));
        this.newModel.setAnnotation(mainClass.getMatchingSet().toString().replace("{", "").replace("}", ""));
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
