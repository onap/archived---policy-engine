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

package org.onap.policy.pap.xacml.rest.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Attribute;
import org.onap.policy.rest.jpa.Category;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.xacml.api.XACMLErrorConstants;
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
	private static String xacmlId = "xacmlId";
	private static String apiflag = "apiflag";
	private static String operation = "operation";
	private static String dictionaryFields ="dictionaryFields";
	private static String duplicateResponseString = "Duplicate";
	private static String utf8 = "UTF-8";
	private static String applicationJsonContentType = "application / json";
	private static String onapName = "onapName";
	@Autowired
	public DictionaryController(CommonClassDao commonClassDao){
		DictionaryController.commonClassDao = commonClassDao;
	}
	/*
	 * This is an empty constructor
	 */
	public DictionaryController(){}

	public UserInfo getUserInfo(String loginId){
		return (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
	}


	public Category getCategory(){
		List<Object> list = commonClassDao.getData(Category.class);
		for (int i = 0; i < list.size() ; i++) {
			Category value = (Category) list.get(i);
			if (("resource").equals(value.getShortName())) {
				return value;
			}
		}
		return null;
	}

	@RequestMapping(value={"/get_AttributeDatabyAttributeName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAttributeDictionaryEntityDatabyAttributeName(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("attributeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(Attribute.class, xacmlId)));
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
	public void getAttributeDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("attributeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(Attribute.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success");
            response.addHeader(operation, "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/attribute_dictionary/save_attribute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			boolean duplicateflag = false;
            boolean isFakeUpdate = false;
            boolean fromAPI = false;
            if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
                fromAPI = true;
            }
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
            Attribute attributeData = null;
            AttributeValues attributeValueData = null;
            String userId = null;
            if (fromAPI) {
                attributeData = (Attribute)mapper.readValue(root.get(dictionaryFields).toString(), Attribute.class);
                attributeValueData = (AttributeValues)mapper.readValue(root.get(dictionaryFields).toString(), AttributeValues.class);
                userId = "API";

                //check if update operation or create, get id for data to be updated and update attributeData
                if (("update").equals(request.getParameter(operation))) {
                	List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(attributeData.getXacmlId(), xacmlId, Attribute.class);
                	Attribute data = (Attribute) duplicateData.get(0);
                	int id = data.getId();
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
			if(!attributeValueData.getUserDataTypeValues().isEmpty()){
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
				if(("string").equalsIgnoreCase(datatype)){
					a.setId(26);
				}else if(("integer").equalsIgnoreCase(datatype)){
					a.setId(12);
				}else if(("boolean").equalsIgnoreCase(datatype)){
					a.setId(18);
				}else if(("double").equalsIgnoreCase(datatype)){
					a.setId(25);
				}else if(("user").equalsIgnoreCase(datatype)){
					a.setId(29);
				}
				attributeData.setDatatypeBean(a);
			}
			if(attributeData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(attributeData.getXacmlId(), xacmlId, Attribute.class);
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
                responseString = duplicateResponseString;
            } else {
                responseString = mapper.writeValueAsString(commonClassDao.getData(Attribute.class));
            }

            if (fromAPI) {
                if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
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
                response.setCharacterEncoding(utf8);
                response.setContentType(applicationJsonContentType);
                request.setCharacterEncoding(utf8);

                PrintWriter out = response.getWriter();
                JSONObject j = new JSONObject("{attributeDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/attribute_dictionary/remove_attribute"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeAttributeDictionary(HttpServletRequest request, HttpServletResponse response)throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Attribute attributeData = (Attribute)mapper.readValue(root.get("data").toString(), Attribute.class);
			commonClassDao.delete(attributeData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(Attribute.class));
			JSONObject j = new JSONObject("{attributeDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	//OnapName Dictionary
	@RequestMapping(value={"/get_OnapNameDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getOnapNameDictionaryByNameEntityData(HttpServletResponse response){
		LOGGER.info("get_OnapNameDataByName is called");
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("onapNameDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(OnapName.class, onapName)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_OnapNameData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getOnapNameDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("onapNameDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(OnapName.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success");
            response.addHeader(operation, "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/onap_dictionary/save_onapName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			OnapName onapData;
			String userId = null;
			if (fromAPI) {
				onapData = (OnapName)mapper.readValue(root.get(dictionaryFields).toString(), OnapName.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated
				if (("update").equals(request.getParameter(operation))) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getOnapName(), onapName, OnapName.class);
					OnapName data = (OnapName) duplicateData.get(0);
					int id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						onapData.setId(1);
					} else {
						onapData.setId(id);
					}
					onapData.setUserCreatedBy(this.getUserInfo(userId));
				}
			} else {
				onapData = (OnapName)mapper.readValue(root.get("onapNameDictionaryData").toString(), OnapName.class);
				userId = root.get("userid").textValue();
			}
			if(onapData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getOnapName(), onapName, OnapName.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					onapData.setUserCreatedBy(getUserInfo(userId));
					onapData.setUserModifiedBy(getUserInfo(userId));
					commonClassDao.save(onapData);
				}
			}else{
				if(!isFakeUpdate){
					onapData.setUserModifiedBy(this.getUserInfo(userId));
					onapData.setModifiedDate(new Date());
					commonClassDao.update(onapData);
				}
			}
			String responseString = null;
			if(duplicateflag) {
				responseString = duplicateResponseString;
			} else {
				responseString = mapper.writeValueAsString(commonClassDao.getData(OnapName.class));
			}
			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
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
				response.setCharacterEncoding(utf8);
				response.setContentType(applicationJsonContentType);
				request.setCharacterEncoding(utf8);

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{onapNameDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/onap_dictionary/remove_onap"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			OnapName onapData = (OnapName)mapper.readValue(root.get("data").toString(), OnapName.class);
			commonClassDao.delete(onapData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(OnapName.class));
			JSONObject j = new JSONObject("{onapNameDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

}

class AttributeValues{
	private List<Object> userDataTypeValues;

	public List<Object> getUserDataTypeValues() {
		return userDataTypeValues;
	}

	public void setUserDataTypeValues(List<Object> userDataTypeValues) {
		this.userDataTypeValues = userDataTypeValues;
	}
}

