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

package org.openecomp.policy.pap.xacml.rest.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.DCAEUUIDDao;
import org.openecomp.policy.rest.dao.MicroServiceConfigNameDao;
import org.openecomp.policy.rest.dao.MicroServiceLocationDao;
import org.openecomp.policy.rest.dao.MicroServiceModelsDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.DCAEuuid;
import org.openecomp.policy.rest.jpa.MicroServiceConfigName;
import org.openecomp.policy.rest.jpa.MicroServiceLocation;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.rest.util.MSAttributeObject;
import org.openecomp.policy.rest.util.MSModelUtitils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MicroServiceDictionaryController {
	private static final Log logger	= LogFactory.getLog(MicroServiceDictionaryController.class);

	@Autowired
	MicroServiceConfigNameDao microServiceConfigNameDao;
	
	@Autowired
	MicroServiceLocationDao microServiceLocationDao;
	
	@Autowired
	MicroServiceModelsDao microServiceModelsDao;
	
	@Autowired
	DCAEUUIDDao dcaeUUIDDao;
	
	@Autowired
	UserInfoDao userInfoDao;
	

	private String newFile;
	private String directory;
	private List<String> dirDependencyList = new ArrayList<String>();
	private HashMap<String,MSAttributeObject > classMap = new HashMap<String,MSAttributeObject>();
	MSModelUtitils utils = new MSModelUtitils();
	private MicroServiceModels newModel;
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;	
	}
	
	@RequestMapping(value={"/get_DCAEUUIDDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDCAEUUIDDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("dcaeUUIDDictionaryDatas", mapper.writeValueAsString(dcaeUUIDDao.getDCAEuuidDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value={"/get_DCAEUUIDData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDCAEUUIDDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("dcaeUUIDDictionaryDatas", mapper.writeValueAsString(dcaeUUIDDao.getDCAEuuidData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ms_dictionary/save_dcaeUUID.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DCAEuuid dCAEuuid = (DCAEuuid)mapper.readValue(root.get("dcaeUUIDDictionaryData").toString(), DCAEuuid.class);
			if(dCAEuuid.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(dCAEuuid.getName(), "name", DCAEuuid.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					dcaeUUIDDao.Save(dCAEuuid);
				}
			}else{
				dcaeUUIDDao.update(dCAEuuid); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.dcaeUUIDDao.getDCAEuuidData());
			} 
			JSONObject j = new JSONObject("{dcaeUUIDDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_dcaeuuid.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DCAEuuid dCAEuuid = (DCAEuuid)mapper.readValue(root.get("data").toString(), DCAEuuid.class);
			dcaeUUIDDao.delete(dCAEuuid);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.dcaeUUIDDao.getDCAEuuidData());
			JSONObject j = new JSONObject("{dcaeUUIDDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	
	@RequestMapping(value={"/get_MicroServiceConfigNameDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceConfigNameByNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceCongigNameDictionaryDatas", mapper.writeValueAsString(microServiceConfigNameDao.getMSConfigDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	@RequestMapping(value={"/get_MicroServiceConfigNameData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceConfigNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceCongigNameDictionaryDatas", mapper.writeValueAsString(microServiceConfigNameDao.getMicroServiceConfigNameData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ms_dictionary/save_configName.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceConfigName microServiceConfigName = (MicroServiceConfigName)mapper.readValue(root.get("microServiceCongigNameDictionaryData").toString(), MicroServiceConfigName.class);
			if(microServiceConfigName.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(microServiceConfigName.getName(), "name", MicroServiceConfigName.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					microServiceConfigNameDao.Save(microServiceConfigName);
				}
			}else{
				microServiceConfigNameDao.update(microServiceConfigName); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.microServiceConfigNameDao.getMicroServiceConfigNameData());
			}
			JSONObject j = new JSONObject("{microServiceCongigNameDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_msConfigName.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceConfigName microServiceConfigName = (MicroServiceConfigName)mapper.readValue(root.get("data").toString(), MicroServiceConfigName.class);
			microServiceConfigNameDao.delete(microServiceConfigName);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.microServiceConfigNameDao.getMicroServiceConfigNameData());
			JSONObject j = new JSONObject("{microServiceCongigNameDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_MicroServiceLocationDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceLocationByNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceLocationDictionaryDatas", mapper.writeValueAsString(microServiceLocationDao.getMSLocationDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_MicroServiceLocationData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceLocationDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceLocationDictionaryDatas", mapper.writeValueAsString(microServiceLocationDao.getMicroServiceLocationData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ms_dictionary/save_location.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceLocation microServiceLocation = (MicroServiceLocation)mapper.readValue(root.get("microServiceLocationDictionaryData").toString(), MicroServiceLocation.class);
			if(microServiceLocation.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(microServiceLocation.getName(), "name", MicroServiceLocation.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					microServiceLocationDao.Save(microServiceLocation);
				}
			}else{
				microServiceLocationDao.update(microServiceLocation); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.microServiceLocationDao.getMicroServiceLocationData());
			}
			JSONObject j = new JSONObject("{microServiceLocationDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_msLocation.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceLocation microServiceLocation = (MicroServiceLocation)mapper.readValue(root.get("data").toString(), MicroServiceLocation.class);
			microServiceLocationDao.delete(microServiceLocation);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.microServiceLocationDao.getMicroServiceLocationData());
			JSONObject j = new JSONObject("{microServiceLocationDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_MicroServiceModelsDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceModelsDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceModelsDictionaryDatas", mapper.writeValueAsString(microServiceModelsDao.getMSModelsDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_MicroServiceModelsData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getMicroServiceModelsDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("microServiceModelsDictionaryDatas", mapper.writeValueAsString(microServiceModelsDao.getMicroServiceModelsData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ms_dictionary/save_model.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceModels microServiceModels = (MicroServiceModels)mapper.readValue(root.get("microServiceModelsDictionaryData").toString(), MicroServiceModels.class);
			String userId = root.get("loginId").textValue();
			microServiceModels.setAttributes(this.newModel.getAttributes());
			microServiceModels.setRef_attributes(this.newModel.getRef_attributes());
			microServiceModels.setDependency(this.newModel.getDependency());
			microServiceModels.setModelName(this.newModel.getModelName());
			microServiceModels.setSub_attributes(this.newModel.getSub_attributes());
			microServiceModels.setVersion(this.newModel.getVersion());
			if(microServiceModels.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(microServiceModels.getModelName(), "modelName", MicroServiceModels.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					microServiceModels.setUserCreatedBy(this.getUserInfo(userId));
					microServiceModelsDao.Save(microServiceModels);
				}
			}else{
				microServiceModelsDao.update(microServiceModels); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.microServiceModelsDao.getMicroServiceModelsData());
			} 
			JSONObject j = new JSONObject("{microServiceModelsDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ms_dictionary/remove_msModel.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			MicroServiceModels microServiceModels = (MicroServiceModels)mapper.readValue(root.get("data").toString(), MicroServiceModels.class);
			microServiceModelsDao.delete(microServiceModels);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.microServiceModelsDao.getMicroServiceModelsData());
			JSONObject j = new JSONObject("{microServiceModelsDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/ms_dictionary/set_MSModelData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void SetRuleData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
		for (FileItem item : items) {
			if(item.getName().endsWith(".zip")){
				this.newModel = new MicroServiceModels();
				try{
					File file = new File(item.getName());
					OutputStream outputStream = new FileOutputStream(file);
					IOUtils.copy(item.getInputStream(), outputStream);
					outputStream.close();
					this.newFile = file.toString();
					this.newModel.setModelName(this.newFile.toString().split("-v")[0]);
					if (this.newFile.toString().contains("-v")){
						this.newModel.setVersion(this.newFile.toString().split("-v")[1].replace(".zip", ""));
					}
				}catch(Exception e){
					logger.error("Upload error : " + e);
				}
			}
		}
		extractFolder(this.newFile);
		List<File> fileList = listModelFiles(this.directory);
		
		File folder = new File(this.directory);
		File[] test = folder.listFiles();
		
		//Process Main Model file first
		String ignoreFile = null;
		for (File file : test) {
			if(!file.isDirectory() && file.getName().endsWith(".xmi")){
            	retreiveDependency(file.toString(), true);
            	ignoreFile = file.toString();
            }	
		}
		
		for(File tempFile: fileList){
			if (!tempFile.toString().contains(ignoreFile)){
				retreiveDependency(tempFile.toString(), false);
			}
		}
		
		addValuesToNewModel();
		
		File deleteFile = new File(this.newFile);
		deleteFile.delete();
	}
	
	private void addValuesToNewModel() {
		//Loop  through the classmap and pull out the required info for the new file.
		MSAttributeObject mainClass  = null;
		ArrayList<String> dependency = null;
		String subAttribute = null;
		
		mainClass = classMap.get(this.newModel.getModelName());
		
		if (mainClass !=null){
			String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
			dependency = new ArrayList<String>(Arrays.asList(dependTemp.split(",")));	
			dependency = getFullDependencyList(dependency);
			for (String element : dependency){
				MSAttributeObject temp = new MSAttributeObject();
				temp = classMap.get(element);
				if (temp!=null){
					mainClass.addAllRefAttribute(temp.getRefAttribute());
					mainClass.addAllAttribute(temp.getAttribute());
				}
			}
			subAttribute = utils.createSubAttributes(dependency, classMap, this.newModel.getModelName());
		}else{
			subAttribute = "{}";
			this.newModel.setDependency("");
		}

		if (mainClass.getDependency()==null){
			mainClass.setDependency("");
		}

		this.newModel.setDependency(mainClass.getDependency());
		this.newModel.setSub_attributes(subAttribute.toString());
		this.newModel.setAttributes(mainClass.getAttribute().toString().replace("{", "").replace("}", ""));
		this.newModel.setRef_attributes(mainClass.getRefAttribute().toString().replace("{", "").replace("}", ""));
	
	} 
	
	private ArrayList<String> getFullDependencyList(ArrayList<String> dependency) {
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> workingList = new ArrayList<String>();
		returnList.addAll(dependency);
		for (String element : dependency ){
			if (classMap.containsKey(element)){
				MSAttributeObject value = classMap.get(element);			
				String rawValue = StringUtils.replaceEach(value.getDependency(), new String[]{"[", "]"}, new String[]{"", ""});
				workingList = new ArrayList<String>(Arrays.asList(rawValue.split(",")));	
				for(String depend : workingList){
					if (!returnList.contains(depend) && !depend.isEmpty()){
						returnList.add(depend.trim());
						//getFullDepedency(workingList)
					}
				}
			}
		}
		
		return returnList;
	}

	
	/*
	 * Unzip file and store in the model directory for processing
	 */
	@SuppressWarnings("rawtypes")
	private void extractFolder(String zipFile )  {
	    int BUFFER = 2048;
	    File file = new File(zipFile);

	    ZipFile zip;
		try {
			zip = new ZipFile(file);
		    String newPath =  "model" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    this.directory = "model" + File.separator + zipFile.substring(0, zipFile.length() - 4);
		    new File(newPath).mkdir();
		    Enumeration zipFileEntries = zip.entries();
	
		    // Process each entry
		    while (zipFileEntries.hasMoreElements()){
		        // grab a zip file entry
		        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
		        String currentEntry = entry.getName();
		        File destFile = new File("model" + File.separator + currentEntry);
		        File destinationParent = destFile.getParentFile();
		        
		        destinationParent.mkdirs();
	
		        if (!entry.isDirectory()){
		            BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
		            int currentByte;
		            byte data[] = new byte[BUFFER];
		            FileOutputStream fos = new FileOutputStream(destFile);
		            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		            while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
		                dest.write(data, 0, currentByte);
		            }
		            dest.flush();
		            dest.close();
		            is.close();
		        }
	
		        if (currentEntry.endsWith(".zip")){
		            extractFolder(destFile.getAbsolutePath());
		        }
		    }
	    } catch (IOException e) {
			logger.error("Failed to unzip model file " + zipFile);
		}
	}
	
	private void retreiveDependency(String workingFile, Boolean modelClass) {
		
		MSModelUtitils utils = new MSModelUtitils();
	    HashMap<String, MSAttributeObject> tempMap = new HashMap<String, MSAttributeObject>();
	    
	    tempMap = utils.processEpackage(workingFile);
	    
	    classMap.putAll(tempMap);
	    System.out.println(tempMap);
	    
	    return;   	}
		
	private List<File> listModelFiles(String directoryName) {
		File directory = new File(directoryName);
		List<File> resultList = new ArrayList<File>();
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				resultList.add(file);
			} else if (file.isDirectory()) {
				dirDependencyList.add(file.getName());
				resultList.addAll(listModelFiles(file.getAbsolutePath()));
			}
		}
		return resultList;
	}
}
