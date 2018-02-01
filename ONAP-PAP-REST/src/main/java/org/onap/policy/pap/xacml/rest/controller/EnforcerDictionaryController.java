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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.EnforcingType;
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
public class EnforcerDictionaryController {

	private static final Logger LOGGER	= FlexLogger.getLogger(EnforcerDictionaryController.class);
	
	@Autowired
	CommonClassDao commonClassDao;

	@RequestMapping(value={"/get_EnforcerTypeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getEnforcerDictionaryEntityData(HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			List<Object> list = commonClassDao.getData(EnforcingType.class);
			List<String> dictList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++){
				EnforcingType dict = (EnforcingType) list.get(i);
				dictList.add(dict.getEnforcingType());
			}
			model.put("enforcerDictionaryDatas", mapper.writeValueAsString(dictList));
			org.onap.policy.pap.xacml.rest.util.JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error("Exception Occured"+e);
		}
	}
	
	@RequestMapping(value={"/enforcer_dictionary/save_enforcerType"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveEnforcerDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException{
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			EnforcingType enforcingType = (EnforcingType)mapper.readValue(root.get("enforcerDictionaryData").toString(), EnforcingType.class);
			if(enforcingType.getId() == 0){
				commonClassDao.save(enforcingType);
			}else{
				commonClassDao.update(enforcingType); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(EnforcingType.class));
			JSONObject j = new JSONObject("{enforcerDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
			LOGGER.error(e);
		}
		return null;
	}

	@RequestMapping(value={"/enforcer_dictionary/remove_enforcer"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeEnforcerDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			EnforcingType enforcingType = (EnforcingType)mapper.readValue(root.get("data").toString(), EnforcingType.class);
			commonClassDao.delete(enforcingType);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(EnforcingType.class));
			JSONObject j = new JSONObject("{enforcerDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(PolicyUtils.CATCH_EXCEPTION);
		}
		return null;
	}
}
