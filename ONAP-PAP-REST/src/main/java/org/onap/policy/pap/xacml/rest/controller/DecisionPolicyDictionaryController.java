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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.DecisionSettings;
import org.onap.policy.rest.jpa.RainyDayTreatments;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DecisionPolicyDictionaryController {

	private static final Logger LOGGER  = FlexLogger.getLogger(DecisionPolicyDictionaryController.class);
	
	private static CommonClassDao commonClassDao;
	
	@Autowired
	public DecisionPolicyDictionaryController(CommonClassDao commonClassDao){
		DecisionPolicyDictionaryController.commonClassDao = commonClassDao;
	}
	
	public DecisionPolicyDictionaryController(){}
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}
	
	@RequestMapping(value={"/get_SettingsDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getSettingsDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("settingsDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(DecisionSettings.class, "xacmlId")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	
	@RequestMapping(value={"/get_SettingsDictionaryData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getSettingsDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("settingsDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(DecisionSettings.class)));
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
	
	@RequestMapping(value={"/decision_dictionary/save_Settings"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException{
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
			DecisionSettings decisionSettings;
            String userId = null;
            
            if (fromAPI) {
            	decisionSettings = (DecisionSettings)mapper.readValue(root.get("dictionaryFields").toString(), DecisionSettings.class);
            	userId = "API";

            	//check if update operation or create, get id for data to be updated and update attributeData
            	if (request.getParameter("operation").equals("update")) {
            		List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(decisionSettings.getXacmlId(), "xacmlId", DecisionSettings.class);
            		int id = 0;
            		DecisionSettings data = (DecisionSettings) duplicateData.get(0);
            		id = data.getId();
            		if(id==0){
            			isFakeUpdate=true;
            			decisionSettings.setId(1);
            		} else {
            			decisionSettings.setId(id);
            		}
            		decisionSettings.setUserCreatedBy(this.getUserInfo(userId));
            	}
            } else {
            	decisionSettings = (DecisionSettings)mapper.readValue(root.get("settingsDictionaryData").toString(), DecisionSettings.class);
            	userId = root.get("userid").textValue();
            }
			if(decisionSettings.getDatatypeBean().getShortName() != null){
				String datatype = decisionSettings.getDatatypeBean().getShortName();
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
				decisionSettings.setDatatypeBean(a);
			}
			if(decisionSettings.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(decisionSettings.getXacmlId(), "xacmlId", DecisionSettings.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					decisionSettings.setUserCreatedBy(this.getUserInfo(userId));
					decisionSettings.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(decisionSettings);
				}
			}else{
				if(!isFakeUpdate) {
					decisionSettings.setUserModifiedBy(this.getUserInfo(userId));
					decisionSettings.setModifiedDate(new Date());
					commonClassDao.update(decisionSettings); 
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString =  mapper.writeValueAsString(commonClassDao.getData(DecisionSettings.class));
            }
          
            if (fromAPI) {
                if (!"Duplicate".equals(responseString)) {
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
                JSONObject j = new JSONObject("{settingsDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
 
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/settings_dictionary/remove_settings"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DecisionSettings decisionSettings = (DecisionSettings)mapper.readValue(root.get("data").toString(), DecisionSettings.class);
			commonClassDao.delete(decisionSettings);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(DecisionSettings.class));
			JSONObject j = new JSONObject("{settingsDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}
	
	
	
	@RequestMapping(value={"/get_RainyDayDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getRainyDayDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("rainyDayDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(RainyDayTreatments.class, "bbid")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	
	@RequestMapping(value={"/get_RainyDayDictionaryData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getRainyDayDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("rainyDayDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(RainyDayTreatments.class)));
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
	
	@RequestMapping(value={"/decision_dictionary/save_RainyDay"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveRainyDayDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException{
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
			RainyDayTreatments decisionRainyDay;
            TreatmentValues treatmentsData = null;
            if (fromAPI) {
            	decisionRainyDay = (RainyDayTreatments)mapper.readValue(root.get("dictionaryFields").toString(), RainyDayTreatments.class);
            	treatmentsData = (TreatmentValues)mapper.readValue(root.get("dictionaryFields").toString(), TreatmentValues.class);
            	//check if update operation or create, get id for data to be updated and update attributeData
            	if (request.getParameter("operation").equals("update")) {
            		List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(decisionRainyDay.getBbid()+":"+decisionRainyDay.getWorkstep(), "bbid:workstep", RainyDayTreatments.class);
            		int id = 0;
            		RainyDayTreatments data = (RainyDayTreatments) duplicateData.get(0);
            		id = data.getId();
            		if(id==0){
            			isFakeUpdate=true;
            		} else {
            			decisionRainyDay.setId(id);
            		}
            	}
            } else {
            	decisionRainyDay = (RainyDayTreatments)mapper.readValue(root.get("rainyDayDictionaryData").toString(), RainyDayTreatments.class);
            	treatmentsData = (TreatmentValues)mapper.readValue(root.get("rainyDayDictionaryData").toString(), TreatmentValues.class);
            }
            
			String userValue = "";
			int counter = 0;
			if(treatmentsData.getUserDataTypeValues().size() > 0){
				for(Object treatment : treatmentsData.getUserDataTypeValues()){
					if(treatment instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) treatment).get("treatment").toString();
						if(counter>0){
							userValue = userValue + ",";
						}
						userValue = userValue + key ;
						counter ++;
					}
				}
				decisionRainyDay.setTreatments(userValue);
			}
			
			if(decisionRainyDay.getId() == 0){
        		List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(decisionRainyDay.getBbid()+":"+decisionRainyDay.getWorkstep(), "bbid:workstep", RainyDayTreatments.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(decisionRainyDay);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(decisionRainyDay); 
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = "Duplicate";
            }else{
                responseString =  mapper.writeValueAsString(commonClassDao.getData(RainyDayTreatments.class));
            }
          
            if (fromAPI) {
                if (!"Duplicate".equals(responseString)) {
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
                JSONObject j = new JSONObject("{rainyDayDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
 
        }catch (Exception e){
        	LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/decision_dictionary/remove_rainyDay"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeRainyDayDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RainyDayTreatments decisionRainyDay = (RainyDayTreatments)mapper.readValue(root.get("data").toString(), RainyDayTreatments.class);
			commonClassDao.delete(decisionRainyDay);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(RainyDayTreatments.class));
			JSONObject j = new JSONObject("{rainyDayDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}
	
}

class TreatmentValues { 
	private ArrayList<Object> userDataTypeValues = new ArrayList<>();

	public ArrayList<Object> getUserDataTypeValues() {
		return userDataTypeValues;
	}

	public void setUserDataTypeValues(ArrayList<Object> userDataTypeValues) {
		this.userDataTypeValues = userDataTypeValues;
	}
}
