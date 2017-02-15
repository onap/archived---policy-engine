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
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.GroupPolicyScopeListDao;
import org.openecomp.policy.rest.dao.PolicyScopeClosedLoopDao;
import org.openecomp.policy.rest.dao.PolicyScopeResourceDao;
import org.openecomp.policy.rest.dao.PolicyScopeServiceDao;
import org.openecomp.policy.rest.dao.PolicyScopeTypeDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
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

	@Autowired
	GroupPolicyScopeListDao groupPolicyScopeListDao;
	
	@Autowired
	PolicyScopeClosedLoopDao  policyScopeClosedLoopDao;
	
	@Autowired
	PolicyScopeResourceDao PolicyScopeResourceDao;
	
	@Autowired
	PolicyScopeTypeDao policyScopeTypeDao;
	
	@Autowired
	PolicyScopeServiceDao policyScopeServiceDao;
	

	@Autowired
	UserInfoDao userInfoDao;
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;	
	}
	
	@RequestMapping(value={"/get_GroupPolicyScopeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getGroupPolicyScopeEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("groupPolicyScopeListDatas", mapper.writeValueAsString(groupPolicyScopeListDao.getGroupPolicyScopeListDataByName()));
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
			model.put("groupPolicyScopeListDatas", mapper.writeValueAsString(groupPolicyScopeListDao.getGroupPolicyScopeListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ps_dictionary/save_psGroupPolicyScope.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupPolicyScopeList ecompData = (GroupPolicyScopeList)mapper.readValue(root.get("groupPolicyScopeListData").toString(), GroupPolicyScopeList.class);
			GroupPolicyScope groupData = null;
			try{
				 groupData = (GroupPolicyScope)mapper.readValue(root.get("groupPolicyScopeListData1").toString(), GroupPolicyScope.class);
			}catch(Exception e){
				groupData = new GroupPolicyScope();
				groupData.setResource(root.get("groupPolicyScopeListData1").get("resource").toString().replace("\"", ""));
				groupData.setClosedloop(root.get("groupPolicyScopeListData1").get("closedloop").toString().replace("\"", ""));
				groupData.setService(root.get("groupPolicyScopeListData1").get("service").toString().replace("\"", ""));
				groupData.setType(root.get("groupPolicyScopeListData1").get("type").toString().replace("\"", ""));
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
			ecompData.setGroupList(list);
			if(!ecompData.getGroupName().startsWith("PolicyScope")){
				String name = "PolicyScope_" + ecompData.getGroupName();
				ecompData.setGroupName(name);
			}
			if(ecompData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(ecompData.getGroupName(), "name", GroupPolicyScopeList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					groupPolicyScopeListDao.Save(ecompData);
				}
			}else{
				groupPolicyScopeListDao.update(ecompData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.groupPolicyScopeListDao.getGroupPolicyScopeListData());
			}
			JSONObject j = new JSONObject("{groupPolicyScopeListDatas: " + responseString + "}");

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

	@RequestMapping(value={"/ps_dictionary/remove_GroupPolicyScope.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupPolicyScopeList ecompData = (GroupPolicyScopeList)mapper.readValue(root.get("data").toString(), GroupPolicyScopeList.class);
			groupPolicyScopeListDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.groupPolicyScopeListDao.getGroupPolicyScopeListData());
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
			model.put("psClosedLoopDictionaryDatas", mapper.writeValueAsString(policyScopeClosedLoopDao.getPolicyScopeClosedLoopDataByName()));
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
			model.put("psClosedLoopDictionaryDatas", mapper.writeValueAsString(policyScopeClosedLoopDao.getPolicyScopeClosedLoopData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ps_dictionary/save_psClosedLoop.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeClosedLoop ecompData = (PolicyScopeClosedLoop)mapper.readValue(root.get("psClosedLoopDictionaryData").toString(), PolicyScopeClosedLoop.class);
			if(ecompData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(ecompData.getName(), "name", PolicyScopeClosedLoop.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					policyScopeClosedLoopDao.Save(ecompData);
				}
			}else{
				policyScopeClosedLoopDao.update(ecompData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.policyScopeClosedLoopDao.getPolicyScopeClosedLoopData());
			}	 
			JSONObject j = new JSONObject("{psClosedLoopDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/ps_dictionary/remove_PSClosedLoop.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeClosedLoop ecompData = (PolicyScopeClosedLoop)mapper.readValue(root.get("data").toString(), PolicyScopeClosedLoop.class);
			policyScopeClosedLoopDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.policyScopeClosedLoopDao.getPolicyScopeClosedLoopData());
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
			model.put("psServiceDictionaryDatas", mapper.writeValueAsString(policyScopeServiceDao.getPolicyScopeServiceDataByName()));
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
			model.put("psServiceDictionaryDatas", mapper.writeValueAsString(policyScopeServiceDao.getPolicyScopeServiceData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ps_dictionary/save_psService.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeService ecompData = (PolicyScopeService)mapper.readValue(root.get("psServiceDictionaryData").toString(), PolicyScopeService.class);
			if(ecompData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(ecompData.getName(), "name", PolicyScopeService.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					policyScopeServiceDao.Save(ecompData);
				}
			}else{
				policyScopeServiceDao.update(ecompData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.policyScopeServiceDao.getPolicyScopeServiceData());
			}	
			JSONObject j = new JSONObject("{psServiceDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/ps_dictionary/remove_PSService.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeService ecompData = (PolicyScopeService)mapper.readValue(root.get("data").toString(), PolicyScopeService.class);
			policyScopeServiceDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.policyScopeServiceDao.getPolicyScopeServiceData());
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
			model.put("psTypeDictionaryDatas", mapper.writeValueAsString(policyScopeTypeDao.getPolicyScopeTypeDataByName()));
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
			model.put("psTypeDictionaryDatas", mapper.writeValueAsString(policyScopeTypeDao.getPolicyScopeTypeData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ps_dictionary/save_psType.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeType ecompData = (PolicyScopeType)mapper.readValue(root.get("psTypeDictionaryData").toString(), PolicyScopeType.class);
			if(ecompData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(ecompData.getName(), "name", PolicyScopeType.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					policyScopeTypeDao.Save(ecompData);
				}
			}else{
				policyScopeTypeDao.update(ecompData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.policyScopeTypeDao.getPolicyScopeTypeData());
			} 
			JSONObject j = new JSONObject("{psTypeDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/ps_dictionary/remove_PSType.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeType ecompData = (PolicyScopeType)mapper.readValue(root.get("data").toString(), PolicyScopeType.class);
			policyScopeTypeDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.policyScopeTypeDao.getPolicyScopeTypeData());
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
			model.put("psResourceDictionaryDatas", mapper.writeValueAsString(PolicyScopeResourceDao.getPolicyScopeResourceDataByName()));
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
			model.put("psResourceDictionaryDatas", mapper.writeValueAsString(PolicyScopeResourceDao.getPolicyScopeResourceData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/ps_dictionary/save_psResource.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeResource ecompData = (PolicyScopeResource)mapper.readValue(root.get("psResourceDictionaryData").toString(), PolicyScopeResource.class);
			if(ecompData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(ecompData.getName(), "name", PolicyScopeResource.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					PolicyScopeResourceDao.Save(ecompData);
				}
			}else{
				PolicyScopeResourceDao.update(ecompData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.PolicyScopeResourceDao.getPolicyScopeResourceData());
			}	  
			JSONObject j = new JSONObject("{psResourceDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/ps_dictionary/remove_PSResource.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyScopeResource ecompData = (PolicyScopeResource)mapper.readValue(root.get("data").toString(), PolicyScopeResource.class);
			PolicyScopeResourceDao.delete(ecompData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.PolicyScopeResourceDao.getPolicyScopeResourceData());
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
