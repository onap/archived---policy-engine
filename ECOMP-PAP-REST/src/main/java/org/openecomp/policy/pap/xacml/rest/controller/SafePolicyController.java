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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.CategoryDao;
import org.openecomp.policy.rest.dao.RiskTypeDao;
import org.openecomp.policy.rest.dao.SafePolicyWarningDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.Category;
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

	private static final Log logger = LogFactory.getLog(SafePolicyController.class);

	@Autowired
	SafePolicyWarningDao safePolicyWarningDao;

	@Autowired
	RiskTypeDao riskTypeDao;

	@Autowired
	UserInfoDao userInfoDao;

	@Autowired
	CategoryDao categoryDao;
	

	public Category getCategory() {
		for (int i = 0; i < categoryDao.getCategoryListData().size(); i++) {
			Category value = categoryDao.getCategoryListData().get(i);
			if (value.getShortName().equals("resource")) {
				return value;
			}
		}
		return null;
	}

	public UserInfo getUserInfo(String loginId) {
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;
	}

	// EcompName Dictionary
	@RequestMapping(value = { "/get_RiskTypeDataByName" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getRiskTypeDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response) {
		logger.info("get_RiskTypeDataByName is called");
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("riskTypeDictionaryDatas", mapper.writeValueAsString(riskTypeDao.getRiskTypeDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "/get_RiskTypeData" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getEcompNameDictionaryEntityData(HttpServletRequest request, HttpServletResponse response) {
		logger.info("get_RiskTypeData is called");
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("riskTypeDictionaryDatas", mapper.writeValueAsString(riskTypeDao.getRiskName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR While callinge DAO: " + e.getMessage());
		}
	}

	@RequestMapping(value = { "/sp_dictionary/save_riskType.htm" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView saveRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			boolean duplicateflag = false;
			System.out.println("SafePolicyController:  saveRiskTypeDictionary() is called");
			logger.debug("SafePolicyController:  saveRiskTypeDictionary() is called");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RiskType riskTypeData = (RiskType) mapper.readValue(root.get("riskTypeDictionaryData").toString(),
					RiskType.class);
			String userId = root.get("loginId").textValue();
			System.out.println("the userId from the ecomp portal is: " + userId);
			if (riskTypeData.getId() == 0) {
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(riskTypeData.getRiskName(), "name", RiskType.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					riskTypeData.setUserCreatedBy(getUserInfo(userId));
					riskTypeData.setUserModifiedBy(getUserInfo(userId));
					System.out.println(
							"SafePolicyController:  got the user info now about to call Save() method on riskTypedao");
					riskTypeDao.Save(riskTypeData);
				}
			} else {
				riskTypeData.setUserModifiedBy(this.getUserInfo(userId));
				riskTypeDao.update(riskTypeData);
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.riskTypeDao.getRiskName());
			}
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

	@RequestMapping(value = { "/sp_dictionary/remove_riskType.htm" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView removeEcompDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RiskType ecompData = (RiskType) mapper.readValue(root.get("data").toString(), RiskType.class);
			riskTypeDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.riskTypeDao.getRiskName());
			JSONObject j = new JSONObject("{riskTypeDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value = { "/get_SafePolicyWarningDataByName" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getSafePolicyWarningEntityDataByName(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("safePolicyWarningDatas",
					mapper.writeValueAsString(safePolicyWarningDao.getSafePolicyWarningDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "/get_SafePolicyWarningData" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
	public void getSafePolicyWarningeEntityData(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("safePolicyWarningDatas",
					mapper.writeValueAsString(safePolicyWarningDao.getSafePolicyWarningData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "/sp_dictionary/save_safePolicyWarning.htm" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView saveSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SafePolicyWarning safePolicyWarning = (SafePolicyWarning) mapper
					.readValue(root.get("safePolicyWarningData").toString(), SafePolicyWarning.class);

			if (safePolicyWarning.getId() == 0) {
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(safePolicyWarning.getName(), "name", SafePolicyWarning.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					safePolicyWarningDao.Save(safePolicyWarning);
				}
			} else {
				safePolicyWarningDao.update(safePolicyWarning);
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.safePolicyWarningDao.getSafePolicyWarningData());
			}
			JSONObject j = new JSONObject("{safePolicyWarningDatas: " + responseString + "}");

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

	@RequestMapping(value = { "/sp_dictionary/remove_SafePolicyWarning.htm" }, method = {
			org.springframework.web.bind.annotation.RequestMethod.POST })
	public ModelAndView removeSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SafePolicyWarning safePolicyWarningData = (SafePolicyWarning) mapper.readValue(root.get("data").toString(),
					SafePolicyWarning.class);
			safePolicyWarningDao.delete(safePolicyWarningData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.safePolicyWarningDao.getSafePolicyWarningData());
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
