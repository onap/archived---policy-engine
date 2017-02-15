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

package org.openecomp.policy.pap.xacml.rest.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.eclipse.emf.common.util.URI;
//import org.eclipse.emf.ecore.EPackage;
//import org.eclipse.emf.ecore.resource.Resource;
//import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
//import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.rest.util.MSAttributeObject;
import org.openecomp.policy.rest.util.MSModelUtitils;

import com.att.research.xacml.util.XACMLProperties;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 

public class CreateNewMicroSerivceModel {
	private static final Logger logger = FlexLogger.getLogger(CreateNewMicroSerivceModel.class);
	private MicroServiceModels newModel = null;
	private HashMap<String,MSAttributeObject > classMap = new HashMap<String,MSAttributeObject>();
	private String directory;
	
	/*
	 * These are the parameters needed for DB access from the PAP
	 */
	private static String papDbDriver = null;
	private static String papDbUrl = null;
	private static String papDbUser = null;
	private static String papDbPassword = null;
	
	MSModelUtitils utils = new MSModelUtitils();

	public CreateNewMicroSerivceModel(String fileName, String serviceName, String string, String version) {
		super();
	}

	public CreateNewMicroSerivceModel(String importFile, String  modelName, String description, String version, String randomID) {
		
		Map<String, String> successMap = new HashMap<String,String>();
		this.newModel = new MicroServiceModels();
		this.newModel.setDescription(description);
		this.newModel.setVersion(version);
		this.newModel.setModelName(modelName);
		UserInfo userInfo = new UserInfo();
		userInfo.setUserLoginId("API");
		this.newModel.setUserCreatedBy(userInfo);
		String cleanUpFile = null;
	
	    HashMap<String, MSAttributeObject> tempMap = new HashMap<String, MSAttributeObject>();
	    //Need to delete the file
	    if (importFile.contains(".zip")){
	    	extractFolder(randomID + ".zip");
	        File directory = new File("ExtractDir" + File.separator + randomID);
	        List<File> fileList = listModelFiles(directory.toString());
	        //get all the files from a directory
	        File[] fList = directory.listFiles();
	        for (File file : fileList){
	            if (file.isFile()){
				    tempMap = utils.processEpackage(file.getAbsolutePath());
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
				logger.error("Failed to unzip model file " + randomID);
			}
	    }else {
		    tempMap = utils.processEpackage("ExtractDir" + File.separator + randomID+".xmi");
		    classMap.putAll(tempMap);
		    cleanUpFile = "ExtractDir" + File.separator + randomID+".xmi";
		    File deleteFile = new File(cleanUpFile); 
			deleteFile.delete();
	    }

	    //	addValuesToNewModel();


	}
	
	private List<File> listModelFiles(String directoryName) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<File>();
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

	private void extractFolder(String zipFile) {
	    int BUFFER = 2048;
	    File file = new File(zipFile);

	    ZipFile zip;
		try {
			zip = new ZipFile("ExtractDir" + File.separator +file);
		    String newPath =  zipFile.substring(0, zipFile.length() - 4);
		    this.directory = "ExtractDir" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    new File(newPath).mkdir();
		    Enumeration zipFileEntries = zip.entries();
	
		    // Process each entry
		    while (zipFileEntries.hasMoreElements())
		    {
		        // grab a zip file entry
		        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
		        String currentEntry = entry.getName();
		        File destFile = new File("ExtractDir" + File.separator + newPath + File.separator + currentEntry);
		        File destinationParent = destFile.getParentFile();
	
		        destinationParent.mkdirs();
	
		        if (!entry.isDirectory())
		        {
		            BufferedInputStream is = new BufferedInputStream(zip
		            .getInputStream(entry));
		            int currentByte;

		            byte data[] = new byte[BUFFER];
	
		            FileOutputStream fos = new FileOutputStream(destFile);
		            BufferedOutputStream dest = new BufferedOutputStream(fos,
		            BUFFER);
	
		            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
		                dest.write(data, 0, currentByte);
		            }
		            dest.flush();
		            dest.close();
		            is.close();
		        }
	
		        if (currentEntry.endsWith(".zip"))
		        {
		            extractFolder(destFile.getAbsolutePath());
		        }
		    }
	    } catch (IOException e) {
			logger.error("Failed to unzip model file " + zipFile);
		}
	}

	public Map<String, String> addValuesToNewModel() {
		
		Map<String, String> successMap = new HashMap<String,String>();
		MSAttributeObject mainClass  = null;
		ArrayList<String> dependency = null;
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
		successMap.put("success", "success");
		return successMap;
		
	}
	
	public Map<String, String> saveImportService(){
		Map<String, String> successMap = new HashMap<String,String>();
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String modelName = this.newModel.getModelName();
		String imported_by = "API";////////////////////////////////////////////
		String version = this.newModel.getVersion();
		String insertQuery = null;
		int ID = 0;
		
		/*
		 * Retrieve the property values for db access from the xacml.pap.properties
		 */
		papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
		papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
		papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
		papDbPassword = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD);
		
		try {
			//Get DB Connection
			Class.forName(papDbDriver);
			con = DriverManager.getConnection(papDbUrl,papDbUser,papDbPassword);
			st = con.createStatement();
			String queryString ="SELECT * FROM MicroServiceModels WHERE modelName='" + modelName + "' AND version='" + version+ "';";
			rs = st.executeQuery(queryString);
		
			if(rs.next()){
				successMap.put("DBError", "EXISTS");
				logger.error("Import new service failed.  Service already exists");
			}else{
				rs = st.executeQuery("SELECT MAX(ID) AS ID FROM MicroServiceModels;");
				if(rs.next()){
					ID = rs.getInt("ID");
					ID++;
				}
	
				insertQuery = "INSERT INTO MicroServiceModels (ID, modelName, Dependency, DESCRIPTION, attributes, ref_attributes, sub_attributes, version, imported_by) "
							+ "VALUES("+ID+",'"+modelName+"','"+ this.newModel.getDependency()+"','"+this.newModel.getDescription()+"','"+this.newModel.getAttributes()+
							"','"+this.newModel.getRef_attributes()+"','"+this.newModel.getSub_attributes()+"','"+version+"','"+imported_by+"')";
				st.executeUpdate(insertQuery);
				successMap.put("success", "success");
			}
			rs.close();
		}catch (ClassNotFoundException e) {
			//TODO:EELF Cleanup - Remove logger
			//logger.error(e.getMessage());
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "saveImportService", "Exception querying MicroServiceModels");
			successMap.put("DBError", "Error Query");
		} catch (SQLException e) {
			//TODO:EELF Cleanup - Remove logger
			//logger.error(e.getMessage());
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "saveImportService", "Exception querying MicroServiceModels");
			successMap.put("DBError", "Error Query");
		} finally {
			try{
				if (con!=null) con.close();
				if (rs!=null) rs.close();
				if (st!=null) st.close();
			} catch (Exception ex){}
		}

		return successMap;
	}
}
