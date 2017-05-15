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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.RiskType;
import org.openecomp.policy.rest.jpa.SafePolicyWarning;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SafePolicyController {

	private static final Logger LOGGER  = FlexLogger.getLogger(SafePolicyController.class);
	
	private static CommonClassDao commonClassDao;
	
	@Autowired
	public SafePolicyController(CommonClassDao commonClassDao){
		SafePolicyController.commonClassDao = commonClassDao;
	}
	
	public SafePolicyController(){}	
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}
	
	private static String DUPLICATE = "Duplicate";

	@RequestMapping(value = { "/get_RiskTypeDataByName" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getRiskTypeDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("riskTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(RiskType.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
		}
	}

	@RequestMapping(value = { "/get_RiskTypeData" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getEcompNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("riskTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(RiskType.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		} catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value = { "/sp_dictionary/save_riskType" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView saveRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
            RiskType riskTypeData;
            String userId = null;
            if (fromAPI) {
                riskTypeData = (RiskType) mapper.readValue(root.get("dictionaryFields").toString(),
                        RiskType.class);
                userId = "API";
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if ("update".equalsIgnoreCase(request.getParameter("operation"))){
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(riskTypeData.getRiskName(), "name", RiskType.class);
                    int id = 0;
                    RiskType data = (RiskType) duplicateData.get(0);
                    id = data.getId();
                    
                    if(id==0){
                        isFakeUpdate=true;
                        riskTypeData.setId(1);
                    } else {
                        riskTypeData.setId(id);
                    }
                    
                    riskTypeData.setUserCreatedBy(this.getUserInfo(userId));
                }
            } else {
            	riskTypeData = (RiskType) mapper.readValue(root.get("riskTypeDictionaryData").toString(), RiskType.class);
            	userId = root.get("userid").textValue();
            }
			 
			if (riskTypeData.getId() == 0) {
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(riskTypeData.getRiskName(), "name", RiskType.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					riskTypeData.setUserCreatedBy(getUserInfo(userId));
					riskTypeData.setUserModifiedBy(getUserInfo(userId));
					commonClassDao.save(riskTypeData);
				}
			} else {
				if (!isFakeUpdate) {
					riskTypeData.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.update(riskTypeData);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = DUPLICATE;
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(RiskType.class));
            }
            
            if (fromAPI) {
                if (responseString!=null && !responseString.equals(DUPLICATE)) {
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
                JSONObject j = new JSONObject("{riskTypeDictionaryDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
        }catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value = { "/sp_dictionary/remove_riskType" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView removeEcompDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RiskType ecompData = (RiskType) mapper.readValue(root.get("data").toString(), RiskType.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(RiskType.class));
			JSONObject j = new JSONObject("{riskTypeDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value = { "/get_SafePolicyWarningDataByName" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getSafePolicyWarningEntityDataByName(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("safePolicyWarningDatas",
					mapper.writeValueAsString(commonClassDao.getDataByColumn(SafePolicyWarning.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
		}
	}

	@RequestMapping(value = { "/get_SafePolicyWarningData" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getSafePolicyWarningeEntityData(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("safePolicyWarningDatas",
					mapper.writeValueAsString(commonClassDao.getData(SafePolicyWarning.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
            response.addHeader("successMapKey", "success"); 
            response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		} catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
            response.addHeader("error", "dictionaryDBQuery");
            LOGGER.error(e.getMessage());
		}
	}

	@RequestMapping(value = { "/sp_dictionary/save_safePolicyWarning" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView saveSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
			SafePolicyWarning safePolicyWarning;
            if (fromAPI) {
                safePolicyWarning = (SafePolicyWarning) mapper
                        .readValue(root.get("dictionaryFields").toString(), SafePolicyWarning.class);
                
                //check if update operation or create, get id for data to be updated and update attributeData
                if (request.getParameter("operation").equals("update")) {
                    List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(safePolicyWarning.getName(), "name", SafePolicyWarning.class);
                    int id = 0;
                    SafePolicyWarning data = (SafePolicyWarning) duplicateData.get(0);
                    id = data.getId();
                    
                    if(id==0){
                        isFakeUpdate=true;
                        safePolicyWarning.setId(1);
                    } else {
                        safePolicyWarning.setId(id);
                    } 
                }
            } else {
            	safePolicyWarning = (SafePolicyWarning) mapper.readValue(root.get("safePolicyWarningData").toString(), SafePolicyWarning.class);
            }

			if (safePolicyWarning.getId() == 0) {
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(safePolicyWarning.getName(), "name", SafePolicyWarning.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(safePolicyWarning);
				}
			} else {
				if(!isFakeUpdate) {
					commonClassDao.update(safePolicyWarning);
				}
			}
            String responseString = "";
            if(duplicateflag){
                responseString = DUPLICATE;
            }else{
                responseString = mapper.writeValueAsString(commonClassDao.getData(SafePolicyWarning.class));
            }
            
            if (fromAPI) {
                if (responseString!=null && !responseString.equals(DUPLICATE)) {
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
                JSONObject j = new JSONObject("{safePolicyWarningDatas: " + responseString + "}");
                out.write(j.toString());
                return null;
            }
 
        }catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value = { "/sp_dictionary/remove_SafePolicyWarning" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView removeSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SafePolicyWarning safePolicyWarningData = (SafePolicyWarning) mapper.readValue(root.get("data").toString(),
					SafePolicyWarning.class);
			commonClassDao.delete(safePolicyWarningData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(SafePolicyWarning.class));
			JSONObject j = new JSONObject("{groupPolicyScopeListDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

}
