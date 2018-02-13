/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.PolicyScopeClosedLoop;
import org.onap.policy.rest.jpa.PolicyScopeResource;
import org.onap.policy.rest.jpa.PolicyScopeService;
import org.onap.policy.rest.jpa.PolicyScopeType;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PolicyScopeDictionaryController {

	private static final Logger LOGGER  = FlexLogger.getLogger(PolicyScopeDictionaryController.class);

	private static CommonClassDao commonClassDao;
	private static String successMapKey = "successMapKey";
	private static String exceptionOccured = "Exception Occured";
	private static String successMessage = "success";
	private static String operation = "operation";
	private static String getDictionary =  "getDictionary";
	private static String errorMsg	= "error";
	private static String dictionaryDBQuery = "dictionaryDBQuery";
	private static String apiflag = "apiflag";
	private static String groupPolicyScopeListData1 = "groupPolicyScopeListData1";
	private static String policyScope= "PolicyScope";
	private static String update = "update";
	private static String duplicateResponseString = "Duplicate";
	private static String successMsg = "Success";
	private static String existsResponseString = "Exists";
	private static String utf8 = "UTF-8";
	private static String applicationJsonContentType = "application / json";
	/*
	 * This is an empty constructor
	 */
	public PolicyScopeDictionaryController(){}	

	@Autowired
	public PolicyScopeDictionaryController(CommonClassDao commonClassDao){
		PolicyScopeDictionaryController.commonClassDao = commonClassDao;
	}
	
	public void setCommonClassDao(CommonClassDao commonClassDao){
		PolicyScopeDictionaryController.commonClassDao = commonClassDao;
	}
	
	public UserInfo getUserInfo(String loginId){
		return (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);	
	}

	@RequestMapping(value={"/get_GroupPolicyScopeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getGroupPolicyScopeEntityDataByName(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("groupPolicyScopeListDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(GroupPolicyScopeList.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(exceptionOccured+e);
		}
	}

	@RequestMapping(value={"/get_GroupPolicyScopeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getGroupPolicyScopeEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("groupPolicyScopeListDatas", mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(successMapKey, successMessage); 
			response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader(errorMsg, dictionaryDBQuery);
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psGroupPolicyScope"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean duplicateGroupFlag = false;
			boolean fromAPI = false;
			if (request.getParameter(apiflag)!=null && ("api").equalsIgnoreCase(request.getParameter(apiflag))) {
				fromAPI = true;
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupPolicyScopeList gpdata = null;
			GroupPolicyScope groupData = null;
			if (fromAPI) {
				gpdata = (GroupPolicyScopeList)mapper.readValue(root.get("dictionaryFields").toString(), GroupPolicyScopeList.class);
				try{
					groupData = (GroupPolicyScope)mapper.readValue(root.get(groupPolicyScopeListData1).toString(), GroupPolicyScope.class);
				}catch(Exception e){
					groupData = new GroupPolicyScope();
					groupData.setResource(root.get("dictionaryFields").get("resource").toString().replace("\"", ""));
					groupData.setClosedloop(root.get("dictionaryFields").get("closedloop").toString().replace("\"", ""));
					groupData.setService(root.get("dictionaryFields").get("service").toString().replace("\"", ""));
					groupData.setType(root.get("dictionaryFields").get("type").toString().replace("\"", ""));
					LOGGER.error(e);
				}

				if(!gpdata.getGroupName().startsWith(policyScope)){
					String name = "PolicyScope_" + gpdata.getGroupName();
					gpdata.setGroupName(name);
				}

				//check if update operation or create, get id for data to be updated and update attributeData
				if ((update).equals(request.getParameter(operation))) {

					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(gpdata.getGroupName(), "name", GroupPolicyScopeList.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						GroupPolicyScopeList data = (GroupPolicyScopeList) duplicateData.get(0);
						id = data.getId();
					}                   

					if(id==0){
						isFakeUpdate=true;
						gpdata.setId(1);
					} else {
						gpdata.setId(id);
					}

				}
			} else {
				gpdata = (GroupPolicyScopeList)mapper.readValue(root.get("groupPolicyScopeListData").toString(), GroupPolicyScopeList.class);

				try{
					groupData = (GroupPolicyScope)mapper.readValue(root.get(groupPolicyScopeListData1).toString(), GroupPolicyScope.class);
				}catch(Exception e){
					LOGGER.error(e);
					groupData = new GroupPolicyScope();
					groupData.setResource(root.get(groupPolicyScopeListData1).get("resource").toString().replace("\"", ""));
					groupData.setClosedloop(root.get(groupPolicyScopeListData1).get("closedloop").toString().replace("\"", ""));
					groupData.setService(root.get(groupPolicyScopeListData1).get("service").toString().replace("\"", ""));
					groupData.setType(root.get(groupPolicyScopeListData1).get("type").toString().replace("\"", ""));
				}
				if(!gpdata.getGroupName().startsWith(policyScope)){
					String name = "PolicyScope_" + gpdata.getGroupName();
					gpdata.setGroupName(name);
				}

			}
			ArrayList<String> valueList = new ArrayList<>();
			String resourceValue = groupData.getResource();
			String typeValue = groupData.getType();
			String serviceValue = groupData.getService();
			String closedLoopValue = groupData.getClosedloop();
			valueList.add("resource=" + resourceValue);
			valueList.add("service=" + serviceValue);
			valueList.add("type=" + typeValue);
			valueList.add("closedLoopControlName="  + closedLoopValue);
			String list = StringUtils.replaceEach(valueList.toString(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
			gpdata.setGroupList(list);
			if(!gpdata.getGroupName().startsWith(policyScope)){
				String name = "PolicyScope_" + gpdata.getGroupName();
				gpdata.setGroupName(name);
			}
			if(gpdata.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(gpdata.getGroupName(), "name", GroupPolicyScopeList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					duplicateData =  commonClassDao.checkDuplicateEntry(gpdata.getGroupList(), "groupList", GroupPolicyScopeList.class);
					if(!duplicateData.isEmpty()){
						duplicateGroupFlag = true;
					}else{
						commonClassDao.save(gpdata);
					}
				}
			}else{
				if(!isFakeUpdate) {
					List<Object> duplicateGroupList =  commonClassDao.checkExistingGroupListforUpdate(gpdata.getGroupList(), gpdata.getGroupName());
					if(!duplicateGroupList.isEmpty()) {
						duplicateGroupFlag = true;
					} else {
						commonClassDao.update(gpdata); 
					}
				} 
			}
			String responseString = "";
			if(duplicateflag){
				responseString = duplicateResponseString;
			}else if(duplicateGroupFlag){
				responseString = "DuplicateGroup";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString) && !("DuplicateGroup").equals(responseString)) {
					if(isFakeUpdate){
						responseString = existsResponseString;
					} else {
						responseString = successMsg;
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
				JSONObject j = new JSONObject("{groupPolicyScopeListDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_GroupPolicyScope"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupPolicyScopeList onapData = (GroupPolicyScopeList)mapper.readValue(root.get("data").toString(), GroupPolicyScopeList.class);
			commonClassDao.delete(onapData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class));
			JSONObject j = new JSONObject("{groupPolicyScopeListDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/get_PSClosedLoopDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSClosedLoopEntityDataByName(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psClosedLoopDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeClosedLoop.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(exceptionOccured+e);
		}
	}

	@RequestMapping(value={"/get_PSClosedLoopData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSClosedLoopEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psClosedLoopDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(successMapKey, successMessage); 
			response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader(errorMsg, dictionaryDBQuery);
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psClosedLoop"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
			PolicyScopeClosedLoop onapData;
			if (fromAPI) {
				onapData = (PolicyScopeClosedLoop)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeClosedLoop.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if ((update).equals(request.getParameter(operation))) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeClosedLoop.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						PolicyScopeClosedLoop data = (PolicyScopeClosedLoop) duplicateData.get(0);
						id = data.getId();
					}

					if(id==0){
						isFakeUpdate=true;
						onapData.setId(1);
					} else {
						onapData.setId(id);
					}   

				}
			} else {
				onapData = (PolicyScopeClosedLoop)mapper.readValue(root.get("psClosedLoopDictionaryData").toString(), PolicyScopeClosedLoop.class);
			}
			if(onapData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeClosedLoop.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(onapData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(onapData); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = duplicateResponseString;
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class));
			}

			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
					if(isFakeUpdate){
						responseString = existsResponseString;
					} else {
						responseString = successMsg;
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
				JSONObject j = new JSONObject("{psClosedLoopDictionaryDatas: " + responseString + "}");

				out.write(j.toString());

				return null;
			}

		}catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSClosedLoop"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeClosedLoop onapData = (PolicyScopeClosedLoop)mapper.readValue(root.get("data").toString(), PolicyScopeClosedLoop.class);
			commonClassDao.delete(onapData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class));
			JSONObject j = new JSONObject("{psClosedLoopDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/get_PSServiceDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSServiceEntityDataByName(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psServiceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeService.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(exceptionOccured+e);
		}
	}

	@RequestMapping(value={"/get_PSServiceData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSServiceEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psServiceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(successMapKey, successMessage); 
			response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader(errorMsg, dictionaryDBQuery);
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psService"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
			PolicyScopeService onapData;
			if (fromAPI) {
				onapData = (PolicyScopeService)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeService.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if ((update).equals(request.getParameter(operation))) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeService.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						PolicyScopeService data = (PolicyScopeService) duplicateData.get(0);
						id = data.getId();
					}
					if(id==0){
						isFakeUpdate=true;
						onapData.setId(1);
					} else {
						onapData.setId(id);
					}
				}
			} else {
				onapData = (PolicyScopeService)mapper.readValue(root.get("psServiceDictionaryData").toString(), PolicyScopeService.class);
			}
			if(onapData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeService.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(onapData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(onapData); 
				}
			} 

			String responseString = "";
			if(duplicateflag){
				responseString = duplicateResponseString;
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class));
			}   

			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
					if(isFakeUpdate){
						responseString = existsResponseString;
					} else {
						responseString = successMsg;
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
				JSONObject j = new JSONObject("{psServiceDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSService"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeService onapData = (PolicyScopeService)mapper.readValue(root.get("data").toString(), PolicyScopeService.class);
			commonClassDao.delete(onapData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class));
			JSONObject j = new JSONObject("{psServiceDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/get_PSTypeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSTypeEntityDataByName(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeType.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(exceptionOccured+e);
		}
	}

	@RequestMapping(value={"/get_PSTypeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSTypeEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(successMapKey, successMessage); 
			response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader(errorMsg, dictionaryDBQuery);
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
			PolicyScopeType onapData;
			if (fromAPI) {
				onapData = (PolicyScopeType)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeType.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if ((update).equals(request.getParameter(operation))) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeType.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						PolicyScopeType data = (PolicyScopeType) duplicateData.get(0);
						id = data.getId();
					}

					if(id==0){
						isFakeUpdate=true;
						onapData.setId(1);
					} else {
						onapData.setId(id);
					}   

				}
			} else {
				onapData = (PolicyScopeType)mapper.readValue(root.get("psTypeDictionaryData").toString(), PolicyScopeType.class);
			}
			if(onapData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeType.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(onapData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(onapData); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = duplicateResponseString;
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class));
			} 

			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
					if(isFakeUpdate){
						responseString = existsResponseString;
					} else {
						responseString = successMsg;
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
				JSONObject j = new JSONObject("{psTypeDictionaryDatas: " + responseString + "}");

				out.write(j.toString());

				return null;
			}

		}catch (Exception e){
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeType onapData = (PolicyScopeType)mapper.readValue(root.get("data").toString(), PolicyScopeType.class);
			commonClassDao.delete(onapData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class));
			JSONObject j = new JSONObject("{psTypeDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/get_PSResourceDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSResourceEntityDataByName(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psResourceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeResource.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(exceptionOccured+e);
		}
	}

	@RequestMapping(value={"/get_PSResourceData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSResourceEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psResourceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", successMessage); 
			response.addHeader(operation, getDictionary);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader(errorMsg, dictionaryDBQuery);
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psResource"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
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
			PolicyScopeResource onapData;
			if (fromAPI) {
				onapData = (PolicyScopeResource)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeResource.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if ((update).equals(request.getParameter(operation))) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeResource.class);
					PolicyScopeResource data = (PolicyScopeResource) duplicateData.get(0);
					int id = data.getId();

					if(id==0){
						isFakeUpdate=true;
						onapData.setId(1);
					} else {
						onapData.setId(id);
					}
				}
			} else {
				onapData = (PolicyScopeResource)mapper.readValue(root.get("psResourceDictionaryData").toString(), PolicyScopeResource.class);
			}
			if(onapData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(onapData.getName(), "name", PolicyScopeResource.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(onapData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(onapData); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = duplicateResponseString;
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class));
			}   

			if (fromAPI) {
				if (responseString!=null && !(duplicateResponseString).equals(responseString)) {
					if(isFakeUpdate){
						responseString = existsResponseString;
					} else {
						responseString = successMsg;
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
				JSONObject j = new JSONObject("{psResourceDictionaryDatas: " + responseString + "}");

				out.write(j.toString());

				return null;
			}

		}catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSResource"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeResource onapData = (PolicyScopeResource)mapper.readValue(root.get("data").toString(), PolicyScopeResource.class);
			commonClassDao.delete(onapData);
			response.setCharacterEncoding(utf8);
			response.setContentType(applicationJsonContentType);
			request.setCharacterEncoding(utf8);

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class));
			JSONObject j = new JSONObject("{psResourceDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e);
			response.setCharacterEncoding(utf8);
			request.setCharacterEncoding(utf8);
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}
}

class GroupPolicyScope{
	String resource;
	String type;
	String service;
	String closedloop;
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getClosedloop() {
		return closedloop;
	}
	public void setClosedloop(String closedloop) {
		this.closedloop = closedloop;
	}

}
