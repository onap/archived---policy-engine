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
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.BRMSParamTemplateDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
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
public class BRMSDictionaryController{

	@Autowired
	BRMSParamTemplateDao brmsParamTemplateDao;
	
	@Autowired
	UserInfoDao userInfoDao;
	
	private String rule;
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;	
	}

	@RequestMapping(value={"/get_BRMSParamDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getBRMSParamDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("brmsParamDictionaryDatas", mapper.writeValueAsString(brmsParamTemplateDao.getBRMSParamDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_BRMSParamData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getBRMSParamDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("brmsParamDictionaryDatas", mapper.writeValueAsString(brmsParamTemplateDao.getBRMSParamTemplateData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/brms_dictionary/set_BRMSParamData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void SetRuleData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		StringWriter writer = new StringWriter();
		IOUtils.copy(request.getInputStream() , writer, StandardCharsets.UTF_8);
		String cleanStreamBoundary =  writer.toString().replaceFirst("------(.*)(?s).*octet-stream", "");
		rule = cleanStreamBoundary.substring(0, cleanStreamBoundary.lastIndexOf("end")+4);
	}
	
	@RequestMapping(value={"/brms_dictionary/save_BRMSParam.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			BRMSParamTemplate bRMSParamTemplateData = (BRMSParamTemplate)mapper.readValue(root.get("brmsParamDictionaryData").toString(), BRMSParamTemplate.class);
			String userId = root.get("loginId").textValue();
			bRMSParamTemplateData.setRule(rule);
			if(bRMSParamTemplateData.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(bRMSParamTemplateData.getRuleName(), "ruleName", BRMSParamTemplate.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					bRMSParamTemplateData.setUserCreatedBy(this.getUserInfo(userId));
					brmsParamTemplateDao.Save(bRMSParamTemplateData);
				}	
			}else{
				brmsParamTemplateDao.update(bRMSParamTemplateData); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.brmsParamTemplateDao.getBRMSParamTemplateData());
			} 
			JSONObject j = new JSONObject("{brmsParamDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/brms_dictionary/remove_brmsParam.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			BRMSParamTemplate bRMSParamTemplateData = (BRMSParamTemplate)mapper.readValue(root.get("data").toString(), BRMSParamTemplate.class);
			brmsParamTemplateDao.delete(bRMSParamTemplateData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.brmsParamTemplateDao.getBRMSParamTemplateData());
			JSONObject j = new JSONObject("{brmsParamDictionaryDatas: " + responseString + "}");
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
