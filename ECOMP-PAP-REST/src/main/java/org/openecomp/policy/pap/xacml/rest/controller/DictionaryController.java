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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.AttributeDao;
import org.openecomp.policy.rest.dao.CategoryDao;
import org.openecomp.policy.rest.dao.EcompNameDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.Attribute;
import org.openecomp.policy.rest.jpa.Category;
import org.openecomp.policy.rest.jpa.Datatype;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class DictionaryController {
	
	private static final Log logger	= LogFactory.getLog(DictionaryController.class);

	@Autowired
	AttributeDao attributeDao;

	@Autowired
	EcompNameDao ecompNameDao;
	
	@Autowired
	UserInfoDao userInfoDao;
	
	@Autowired
	CategoryDao categoryDao;
	
	
	public Category getCategory(){
		for (int i = 0; i < categoryDao.getCategoryListData().size() ; i++) {
			Category value = categoryDao.getCategoryListData().get(i);
			if (value.getShortName().equals("resource")) {
				return value;
			}
		}
		return null;	
	}
	
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;	
	}

	@RequestMapping(value={"/get_AttributeDatabyAttributeName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAttributeDictionaryEntityDatabyAttributeName(HttpServletRequest request, HttpServletResponse response){
		try{
			System.out.println();
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("attributeDictionaryDatas", mapper.writeValueAsString(attributeDao.getAttributeData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//Attribute Dictionary
	@RequestMapping(value="/get_AttributeData", method= RequestMethod.GET , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAttributeDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			System.out.println();
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("attributeDictionaryDatas", mapper.writeValueAsString(attributeDao.getData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/attribute_dictionary/save_attribute.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Attribute attributeData = (Attribute)mapper.readValue(root.get("attributeDictionaryData").toString(), Attribute.class);
			AttributeValues attributeValueData = (AttributeValues)mapper.readValue(root.get("attributeDictionaryData").toString(), AttributeValues.class);
			String userId = root.get("loginId").textValue();
			String userValue = "";
			int counter = 0;
			if(attributeValueData.getUserDataTypeValues().size() > 0){
				for(Object attribute : attributeValueData.getUserDataTypeValues()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("attributeValues").toString();
						if(counter>0){
							userValue = userValue + ",";
						}
						userValue = userValue + key ;
						counter ++;
					}
				}
			}
			attributeData.setAttributeValue(userValue);
			if(attributeData.getDatatypeBean().getShortName() != null){
				String datatype = attributeData.getDatatypeBean().getShortName();
				Datatype a = new Datatype();
				if(datatype.equalsIgnoreCase("string")){
					a.setId(26);	
				}else if(datatype.equalsIgnoreCase("integer")){
					a.setId(12);	
				}else if(datatype.equalsIgnoreCase("boolean")){
					a.setId(18);	
				}else if(datatype.equalsIgnoreCase("double")){
					a.setId(25);	
				}else if(datatype.equalsIgnoreCase("user")){
					a.setId(29);	
				}
				attributeData.setDatatypeBean(a);
			}
			if(attributeData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(attributeData.getXacmlId(), "xacmlId", Attribute.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					attributeData.setCategoryBean(this.getCategory());
					attributeData.setUserCreatedBy(this.getUserInfo(userId));
					attributeData.setUserModifiedBy(this.getUserInfo(userId));
					attributeDao.Save(attributeData);
				}
			}else{
				attributeData.setUserModifiedBy(this.getUserInfo(userId));
				attributeDao.update(attributeData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.attributeDao.getData());
			}
			JSONObject j = new JSONObject("{attributeDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/attribute_dictionary/remove_attribute.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Attribute attributeData = (Attribute)mapper.readValue(root.get("data").toString(), Attribute.class);
			attributeDao.delete(attributeData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.attributeDao.getData());
			JSONObject j = new JSONObject("{attributeDictionaryDatas: " + responseString + "}");
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
	
	//EcompName Dictionary
	@RequestMapping(value={"/get_EcompNameDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getEcompNameDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		logger.info("get_EcompNameDataByName is called");
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("ecompNameDictionaryDatas", mapper.writeValueAsString(ecompNameDao.getEcompNameDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_EcompNameData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getEcompNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		logger.info("get_EcompNameData is called");
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("ecompNameDictionaryDatas", mapper.writeValueAsString(ecompNameDao.getEcompName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
			logger.error("ERROR While callinge DAO: " + e.getMessage());
		}
	}

	@RequestMapping(value={"/ecomp_dictionary/save_ecompName.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveEcompDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			System.out.println("DictionaryController:  saveEcompDictionary() is called");
			logger.debug("DictionaryController:  saveEcompDictionary() is called");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			EcompName ecompData = (EcompName)mapper.readValue(root.get("ecompNameDictionaryData").toString(), EcompName.class);
			String userId = root.get("loginId").textValue();
			System.out.println("the userId from the ecomp portal is: " + userId);
			if(ecompData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(ecompData.getEcompName(), "ecompName", EcompName.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					ecompData.setUserCreatedBy(getUserInfo(userId));
					ecompData.setUserModifiedBy(getUserInfo(userId));
					System.out.println("DictionaryController:  got the user info now about to call Save() method on ecompNamedao");
					ecompNameDao.Save(ecompData);
				}
			}else{
				ecompData.setUserModifiedBy(this.getUserInfo(userId));
				ecompNameDao.update(ecompData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.ecompNameDao.getEcompName());
			}
			JSONObject j = new JSONObject("{ecompNameDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/ecomp_dictionary/remove_ecomp.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeEcompDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			EcompName ecompData = (EcompName)mapper.readValue(root.get("data").toString(), EcompName.class);
			ecompNameDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.ecompNameDao.getEcompName());
			JSONObject j = new JSONObject("{ecompNameDictionaryDatas: " + responseString + "}");
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

}

class AttributeValues{
	private ArrayList<Object> userDataTypeValues;

	public ArrayList<Object> getUserDataTypeValues() {
		return userDataTypeValues;
	}

	public void setUserDataTypeValues(ArrayList<Object> userDataTypeValues) {
		this.userDataTypeValues = userDataTypeValues;
	}
}

