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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ActionPolicyDictionaryController {

	private static final Logger LOGGER  = FlexLogger.getLogger(ActionPolicyDictionaryController.class);

	private static CommonClassDao commonClassDao;
	
	@Autowired
	public ActionPolicyDictionaryController(CommonClassDao commonClassDao){
		ActionPolicyDictionaryController.commonClassDao = commonClassDao;
	}
	
	public ActionPolicyDictionaryController(){}

	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}

	@RequestMapping(value={"/get_ActionPolicyDictDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getActionEntitybyName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("actionPolicyDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(ActionPolicyDict.class, "attributeName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e.getMessage());
		}
	}

	@RequestMapping(value={"/get_ActionPolicyDictData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getActionPolicyDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("actionPolicyDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(ActionPolicyDict.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/action_dictionary/save_ActionDict"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
			ActionPolicyDict actionPolicyDict = null;
			ActionAdapter adapter = null;
			String userId = null;

			if(fromAPI) {
				actionPolicyDict = (ActionPolicyDict)mapper.readValue(root.get("dictionaryFields").toString(), ActionPolicyDict.class);
				adapter = (ActionAdapter)mapper.readValue(root.get("dictionaryFields").toString(), ActionAdapter.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(actionPolicyDict.getAttributeName(), "attributeName", ActionPolicyDict.class);
					int id = 0;
					ActionPolicyDict data = (ActionPolicyDict) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						actionPolicyDict.setId(1);
					} else {
						actionPolicyDict.setId(id);
					}
					actionPolicyDict.setUserCreatedBy(this.getUserInfo(userId));
				}
			} else {
				actionPolicyDict = (ActionPolicyDict)mapper.readValue(root.get("actionPolicyDictionaryData").toString(), ActionPolicyDict.class);
				adapter = mapper.readValue(root.get("actionPolicyDictionaryData").toString(), ActionAdapter.class);
				userId = root.get("userid").textValue();
			}
			String header = "";
			int counter = 0;
			if(adapter.getHeaders().size() > 0){
				for(Object attribute : adapter.getHeaders()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
						if(counter>0){
							header = header + ":";
						}
						header = header + key + "=";
						header = header + value;
						counter ++;
					}
				}
			}
			actionPolicyDict.setHeader(header);
			if(actionPolicyDict.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(actionPolicyDict.getAttributeName(), "attributeName", ActionPolicyDict.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					actionPolicyDict.setUserCreatedBy(this.getUserInfo(userId));
					actionPolicyDict.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(actionPolicyDict);
				}
			}else{
				if(!isFakeUpdate) {
					actionPolicyDict.setUserModifiedBy(this.getUserInfo(userId));
					actionPolicyDict.setModifiedDate(new Date());
					commonClassDao.update(actionPolicyDict); 
				}
			} 

			String responseString = null;
			if(duplicateflag) {
				responseString = "Duplicate";
			} else {
				responseString = mapper.writeValueAsString(commonClassDao.getData(ActionPolicyDict.class));
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
				JSONObject j = new JSONObject("{actionPolicyDictionaryDatas: " + responseString + "}");
				out.write(j.toString());

				return null;
			}
		}
		catch (Exception e){
			LOGGER.error(e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/action_dictionary/remove_actionPolicyDict"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ActionPolicyDict actionPolicyDict = (ActionPolicyDict)mapper.readValue(root.get("data").toString(), ActionPolicyDict.class);
			commonClassDao.delete(actionPolicyDict);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(ActionPolicyDictionaryController.commonClassDao.getData(ActionPolicyDict.class));
			JSONObject j = new JSONObject("{actionPolicyDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
}

class ActionAdapter{
	private ArrayList<Object> headers;

	public ArrayList<Object> getHeaders() {
		return headers;
	}

	public void setHeaders(ArrayList<Object> headers) {
		this.headers = headers;
	}
}
