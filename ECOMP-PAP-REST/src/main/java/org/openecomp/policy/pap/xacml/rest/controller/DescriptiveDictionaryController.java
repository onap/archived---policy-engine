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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.adapters.GridData;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.DescriptiveScopeDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.DescriptiveScope;
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
public class DescriptiveDictionaryController {

	@Autowired
	DescriptiveScopeDao descriptiveScopeDao;
	
	@Autowired
	UserInfoDao userInfoDao;
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;	
	}

	@RequestMapping(value={"/get_DescriptiveScopeByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDescriptiveDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("descriptiveScopeDictionaryDatas", mapper.writeValueAsString(descriptiveScopeDao.getDescriptiveScopeDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_DescriptiveScope"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getDescriptiveDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("descriptiveScopeDictionaryDatas", mapper.writeValueAsString(descriptiveScopeDao.getDescriptiveScope()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/descriptive_dictionary/save_descriptive.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DescriptiveScope descriptiveScope = (DescriptiveScope)mapper.readValue(root.get("descriptiveScopeDictionaryData").toString(), DescriptiveScope.class);
			GridData data = (GridData)mapper.readValue(root.get("descriptiveScopeDictionaryData").toString(), GridData.class);
		    String userId = root.get("loginId").textValue();
			String header = "";
			int counter = 0;
			if(data.getAttributes().size() > 0){
				for(Object attribute : data.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
						if(counter>0){
							header = header + "AND";
						}
						header = header + key + ":";
						header = header + value;
						counter ++;
					}
				}
			}
			descriptiveScope.setSearch(header);
			if(descriptiveScope.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(descriptiveScope.getScopeName(), "descriptiveScopeName", DescriptiveScope.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					descriptiveScope.setUserCreatedBy(this.getUserInfo(userId));
					descriptiveScope.setUserModifiedBy(this.getUserInfo(userId));
					descriptiveScopeDao.Save(descriptiveScope);
				}
			}else{
				descriptiveScope.setUserModifiedBy(this.getUserInfo(userId));
				descriptiveScopeDao.update(descriptiveScope); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString =  mapper.writeValueAsString(this.descriptiveScopeDao.getDescriptiveScope());
			}
			JSONObject j = new JSONObject("{descriptiveScopeDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/descriptive_dictionary/remove_descriptiveScope.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			DescriptiveScope descriptiveScope = (DescriptiveScope)mapper.readValue(root.get("data").toString(), DescriptiveScope.class);
			descriptiveScopeDao.delete(descriptiveScope);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.descriptiveScopeDao.getDescriptiveScope());
			JSONObject j = new JSONObject("{descriptiveScopeDictionaryDatas: " + responseString + "}");
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

