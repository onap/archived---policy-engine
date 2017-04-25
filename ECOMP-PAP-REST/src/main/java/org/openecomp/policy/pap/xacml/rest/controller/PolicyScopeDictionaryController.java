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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.GroupPolicyScopeList;
import org.openecomp.policy.rest.jpa.PolicyScopeClosedLoop;
import org.openecomp.policy.rest.jpa.PolicyScopeResource;
import org.openecomp.policy.rest.jpa.PolicyScopeService;
import org.openecomp.policy.rest.jpa.PolicyScopeType;
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
public class PolicyScopeDictionaryController {

	private static final Logger LOGGER  = FlexLogger.getLogger(PolicyScopeDictionaryController.class);

	private static CommonClassDao commonClassDao;
	
	@Autowired
	public PolicyScopeDictionaryController(CommonClassDao commonClassDao){
		PolicyScopeDictionaryController.commonClassDao = commonClassDao;
	}
	
	public PolicyScopeDictionaryController(){}	

	private static String SUCCESSMAPKEY = "successMapKey";

	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}

	@RequestMapping(value={"/get_GroupPolicyScopeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getGroupPolicyScopeEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("groupPolicyScopeListDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(GroupPolicyScopeList.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value={"/get_GroupPolicyScopeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getGroupPolicyScopeEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("groupPolicyScopeListDatas", mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(SUCCESSMAPKEY, "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psGroupPolicyScope"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean duplicateGroupFlag = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			//GroupPolicyScopeList gpdata = (GroupPolicyScopeList)mapper.readValue(root.get("groupPolicyScopeListData").toString(), GroupPolicyScopeList.class);
			GroupPolicyScopeList gpdata = null;
			GroupPolicyScope groupData = null;
			if (fromAPI) {
				gpdata = (GroupPolicyScopeList)mapper.readValue(root.get("dictionaryFields").toString(), GroupPolicyScopeList.class);
				try{
					groupData = (GroupPolicyScope)mapper.readValue(root.get("groupPolicyScopeListData1").toString(), GroupPolicyScope.class);
				}catch(Exception e){
					groupData = new GroupPolicyScope();
					groupData.setResource(root.get("dictionaryFields").get("resource").toString().replace("\"", ""));
					groupData.setClosedloop(root.get("dictionaryFields").get("closedloop").toString().replace("\"", ""));
					groupData.setService(root.get("dictionaryFields").get("service").toString().replace("\"", ""));
					groupData.setType(root.get("dictionaryFields").get("type").toString().replace("\"", ""));
				}

				if(!gpdata.getGroupName().startsWith("PolicyScope")){
					String name = "PolicyScope_" + gpdata.getGroupName();
					gpdata.setGroupName(name);
				}

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {

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
					groupData = (GroupPolicyScope)mapper.readValue(root.get("groupPolicyScopeListData1").toString(), GroupPolicyScope.class);
				}catch(Exception e){
					groupData = new GroupPolicyScope();
					groupData.setResource(root.get("groupPolicyScopeListData1").get("resource").toString().replace("\"", ""));
					groupData.setClosedloop(root.get("groupPolicyScopeListData1").get("closedloop").toString().replace("\"", ""));
					groupData.setService(root.get("groupPolicyScopeListData1").get("service").toString().replace("\"", ""));
					groupData.setType(root.get("groupPolicyScopeListData1").get("type").toString().replace("\"", ""));
				}
				if(!gpdata.getGroupName().startsWith("PolicyScope")){
					String name = "PolicyScope_" + gpdata.getGroupName();
					gpdata.setGroupName(name);
				}

			}
			ArrayList<String> valueList = new ArrayList<String>();
			String list = null;
			String resourceValue = groupData.getResource();
			String typeValue = groupData.getType();
			String serviceValue = groupData.getService();
			String closedLoopValue = groupData.getClosedloop();
			valueList.add("resource=" + resourceValue);
			valueList.add("service=" + serviceValue);
			valueList.add("type=" + typeValue);
			valueList.add("closedLoopControlName="  + closedLoopValue);
			list = StringUtils.replaceEach(valueList.toString(), new String[]{"[", "]", " "}, new String[]{"", "", ""});
			gpdata.setGroupList(list);
			if(!gpdata.getGroupName().startsWith("PolicyScope")){
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
				responseString = "Duplicate";
			}else if(duplicateGroupFlag){
				responseString = "DuplicateGroup";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate") && !responseString.equals("DuplicateGroup")) {
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
				JSONObject j = new JSONObject("{groupPolicyScopeListDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_GroupPolicyScope"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupPolicyScopeList ecompData = (GroupPolicyScopeList)mapper.readValue(root.get("data").toString(), GroupPolicyScopeList.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(GroupPolicyScopeList.class));
			JSONObject j = new JSONObject("{groupPolicyScopeListDatas: " + responseString + "}");
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

	@RequestMapping(value={"/get_PSClosedLoopDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSClosedLoopEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psClosedLoopDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeClosedLoop.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value={"/get_PSClosedLoopData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSClosedLoopEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psClosedLoopDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(SUCCESSMAPKEY, "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psClosedLoop"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			PolicyScopeClosedLoop ecompData;
			if (fromAPI) {
				ecompData = (PolicyScopeClosedLoop)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeClosedLoop.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeClosedLoop.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						PolicyScopeClosedLoop data = (PolicyScopeClosedLoop) duplicateData.get(0);
						id = data.getId();
					}

					if(id==0){
						isFakeUpdate=true;
						ecompData.setId(1);
					} else {
						ecompData.setId(id);
					}   

				}
			} else {
				ecompData = (PolicyScopeClosedLoop)mapper.readValue(root.get("psClosedLoopDictionaryData").toString(), PolicyScopeClosedLoop.class);
			}
			if(ecompData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeClosedLoop.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(ecompData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(ecompData); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class));
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
				JSONObject j = new JSONObject("{psClosedLoopDictionaryDatas: " + responseString + "}");

				out.write(j.toString());

				return null;
			}

		}catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSClosedLoop"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeClosedLoop ecompData = (PolicyScopeClosedLoop)mapper.readValue(root.get("data").toString(), PolicyScopeClosedLoop.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeClosedLoop.class));
			JSONObject j = new JSONObject("{psClosedLoopDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/get_PSServiceDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSServiceEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psServiceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeService.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value={"/get_PSServiceData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSServiceEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psServiceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(SUCCESSMAPKEY, "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psService"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			PolicyScopeService ecompData;
			if (fromAPI) {
				ecompData = (PolicyScopeService)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeService.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeService.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						PolicyScopeService data = (PolicyScopeService) duplicateData.get(0);
						id = data.getId();
					}
					if(id==0){
						isFakeUpdate=true;
						ecompData.setId(1);
					} else {
						ecompData.setId(id);
					}
				}
			} else {
				ecompData = (PolicyScopeService)mapper.readValue(root.get("psServiceDictionaryData").toString(), PolicyScopeService.class);
			}
			if(ecompData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeService.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(ecompData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(ecompData); 
				}
			} 

			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class));
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
				JSONObject j = new JSONObject("{psServiceDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSService"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeService ecompData = (PolicyScopeService)mapper.readValue(root.get("data").toString(), PolicyScopeService.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeService.class));
			JSONObject j = new JSONObject("{psServiceDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/get_PSTypeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSTypeEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeType.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value={"/get_PSTypeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSTypeEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psTypeDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader(SUCCESSMAPKEY, "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			PolicyScopeType ecompData;
			if (fromAPI) {
				ecompData = (PolicyScopeType)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeType.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeType.class);
					int id = 0;
					for (int i =0; i< duplicateData.size(); i++){
						PolicyScopeType data = (PolicyScopeType) duplicateData.get(0);
						id = data.getId();
					}

					if(id==0){
						isFakeUpdate=true;
						ecompData.setId(1);
					} else {
						ecompData.setId(id);
					}   

				}
			} else {
				ecompData = (PolicyScopeType)mapper.readValue(root.get("psTypeDictionaryData").toString(), PolicyScopeType.class);
			}
			if(ecompData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeType.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(ecompData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(ecompData); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class));
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
				JSONObject j = new JSONObject("{psTypeDictionaryDatas: " + responseString + "}");

				out.write(j.toString());

				return null;
			}

		}catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeType ecompData = (PolicyScopeType)mapper.readValue(root.get("data").toString(), PolicyScopeType.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeType.class));
			JSONObject j = new JSONObject("{psTypeDictionaryDatas: " + responseString + "}");
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

	@RequestMapping(value={"/get_PSResourceDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSResourceEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psResourceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PolicyScopeResource.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@RequestMapping(value={"/get_PSResourceData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPSResourceEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("psResourceDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
			LOGGER.error(e);
		}
	}

	@RequestMapping(value={"/ps_dictionary/save_psResource"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			PolicyScopeResource ecompData;
			if (fromAPI) {
				ecompData = (PolicyScopeResource)mapper.readValue(root.get("dictionaryFields").toString(), PolicyScopeResource.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeResource.class);
					int id = 0;
					PolicyScopeResource data = (PolicyScopeResource) duplicateData.get(0);
					id = data.getId();

					if(id==0){
						isFakeUpdate=true;
						ecompData.setId(1);
					} else {
						ecompData.setId(id);
					}
				}
			} else {
				ecompData = (PolicyScopeResource)mapper.readValue(root.get("psResourceDictionaryData").toString(), PolicyScopeResource.class);
			}
			if(ecompData.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ecompData.getName(), "name", PolicyScopeResource.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(ecompData);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(ecompData); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class));
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
				JSONObject j = new JSONObject("{psResourceDictionaryDatas: " + responseString + "}");

				out.write(j.toString());

				return null;
			}

		}catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/ps_dictionary/remove_PSResource"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeResource ecompData = (PolicyScopeResource)mapper.readValue(root.get("data").toString(), PolicyScopeResource.class);
			commonClassDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(PolicyScopeResource.class));
			JSONObject j = new JSONObject("{psResourceDictionaryDatas: " + responseString + "}");
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
