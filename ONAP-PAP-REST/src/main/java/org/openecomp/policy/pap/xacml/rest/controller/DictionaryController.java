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
import java.util.Date;
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
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.Attribute;
import org.openecomp.policy.rest.jpa.Category;
import org.openecomp.policy.rest.jpa.Datatype;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
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
	
	private static final Log LOGGER	= LogFactory.getLog(DictionaryController.class);

	private static CommonClassDao commonClassDao;
	
	@Autowired
	public DictionaryController(CommonClassDao commonClassDao){
		DictionaryController.commonClassDao = commonClassDao;
	}
	
	public DictionaryController(){}
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}
	
	
	public Category getCategory(){
		List<Object> list = commonClassDao.getData(Category.class);
		for (int i = 0; i < list.size() ; i++) {
			Category value = (Category) list.get(i);
			if (value.getShortName().equals("resource")) {
				return value;
			}
		}
		return null;	
	}

	@RequestMapping(value={"/get_AttributeDatabyAttributeName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAttributeDictionaryEntityDatabyAttributeName(HttpServletRequest request, HttpServletResponse response){
		try{
			System.out.println();
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("attributeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(Attribute.class, "xacmlId")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}
	
	//Attribute Dictionary
	@RequestMapping(value="/get_AttributeData", method= RequestMethod.GET , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAttributeDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			System.out.println();
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("attributeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(Attribute.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
		}
	}
	
	@RequestMapping(value={"/attribute_dictionary/save_attribute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
            boolean isFakeUpdate = false;
            boolean fromAPI = false;
            if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
            Attribute attributeData = null;
            AttributeValues attributeValueData = null;
            String userId = null;
            if (fromAPI) {
                //JsonNode json = root.get("dictionaryFields");
                attributeData = (Attribute)mapper.readValue(root.get("dictionaryFields").toString(), Attribute.class);
                attributeValueData = (AttributeValues)mapper.readValue(root.get("dictionaryFields").toString(), AttributeValues.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(attributeData.getXacmlId(), "xacmlId", Attribute.class);
                	int id = 0;
                	Attribute data = (Attribute) duplicateData.get(0);
                	id = data.getId();
                	if(id==0){
                		isFakeUpdate=true;
                		attributeData.setId(1);
                	} else {
                		attributeData.setId(id);
                	}
                	attributeData.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	attributeData = (Attribute)mapper.readValue(root.get("attributeDictionaryData").toString(), Attribute.class);
            	attributeValueData = (AttributeValues)mapper.readValue(root.get("attributeDictionaryData").toString(), AttributeValues.class);
            	userId = root.get("userid").textValue();
            }
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
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(attributeData.getXacmlId(), "xacmlId", Attribute.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					attributeData.setCategoryBean(this.getCategory());
					attributeData.setUserCreatedBy(this.getUserInfo(userId));
					attributeData.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(attributeData);
				}
			}else{
				if(!isFakeUpdate) {
					attributeData.setUserModifiedBy(this.getUserInfo(userId));
					attributeData.setModifiedDate(new Date());
					commonClassDao.update(attributeData); 
				}
			} 
            String responseString = null;
            if(duplicateflag) {
                responseString = "Duplicate";
            } else {
                responseString = mapper.writeValueAsString(commonClassDao.getData(Attribute.class));
            }
            
            if (fromAPI) {
                if (responseString!=null && !responseString.equals("Duplicate")) {
                    if(isFakeUpdate) {
                        responseString = "Exists";
                    } else {
                        responseString = "Success";
                    }
                }
                ModelAndView result = new ModelAndView();
                result.setViewName(responseString);
                return result;
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application / json");
                request.setCharacterEncoding("UTF-8");
 
                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{attributeDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/attribute_dictionary/remove_attribute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Attribute attributeData = (Attribute)mapper.readValue(root.get("data").toString(), Attribute.class);
			commonClassDao.delete(attributeData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(Attribute.class));
			JSONObject j = new JSONObject("{attributeDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
		LOGGER.info("get_EcompNameDataByName is called");
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("ecompNameDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(EcompName.class, "ecompName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}
	
	@RequestMapping(value={"/get_EcompNameData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getEcompNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("ecompNameDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(EcompName.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/ecomp_dictionary/save_ecompName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveEcompDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			EcompName ecompData;
			String userId = null;
			if (fromAPI) {
				ecompData = (EcompName)mapper.readValue(root.get("dictionaryFields").toString(), EcompName.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getEcompName(), "ecompName", EcompName.class);
					int id = 0;
					EcompName data = (EcompName) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						ecompData.setId(1);
					} else {
						ecompData.setId(id);
					}
					ecompData.setUserCreatedBy(this.getUserInfo(userId));
				}
			} else {
				ecompData = (EcompName)mapper.readValue(root.get("ecompNameDictionaryData").toString(), EcompName.class);
				userId = root.get("userid").textValue();
			}
			if(ecompData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getEcompName(), "ecompName", EcompName.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					ecompData.setUserCreatedBy(getUserInfo(userId));
					ecompData.setUserModifiedBy(getUserInfo(userId));
					commonClassDao.save(ecompData);
				}
			}else{
				if(!isFakeUpdate){
					ecompData.setUserModifiedBy(this.getUserInfo(userId));
					ecompData.setModifiedDate(new Date());
					commonClassDao.update(ecompData);
				}
			} 
			String responseString = null;
			if(duplicateflag) {
				responseString = "Duplicate";
			} else {
				responseString = mapper.writeValueAsString(commonClassDao.getData(EcompName.class));
			}
			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}

				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{ecompNameDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ecomp_dictionary/remove_ecomp"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeEcompDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			EcompName ecompData = (EcompName)mapper.readValue(root.get("data").toString(), EcompName.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(EcompName.class));
			JSONObject j = new JSONObject("{ecompNameDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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

