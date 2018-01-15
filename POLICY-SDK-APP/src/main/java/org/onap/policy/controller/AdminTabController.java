/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.GlobalRoleSettings;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping({"/"})
public class AdminTabController extends RestrictedBaseController{

	private static final Logger LOGGER	= FlexLogger.getLogger(AdminTabController.class);
	
	private static CommonClassDao commonClassDao;
	
        public AdminTabController() {
		//default constructor
	}

	@Autowired
	private AdminTabController(CommonClassDao commonClassDao){
		AdminTabController.commonClassDao = commonClassDao;
	}

        public static CommonClassDao getCommonClassDao() {
		return commonClassDao;
	}

	public static void setCommonClassDao(CommonClassDao commonClassDao) {
		AdminTabController.commonClassDao = commonClassDao;
	}
	
	@RequestMapping(value={"/get_LockDownData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAdminTabEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("lockdowndata", mapper.writeValueAsString(commonClassDao.getData(GlobalRoleSettings.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error("Exception Occured"+e);
		}
	}
	
	@RequestMapping(value={"/adminTabController/save_LockDownValue.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveAdminTabLockdownValue(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			LOGGER.info("****************************************Logging UserID for Application Lockdown Function*****************************************");
			LOGGER.info("UserId:  " + userId);
			LOGGER.info("*********************************************************************************************************************************");
			JsonNode root = mapper.readTree(request.getReader());
			GlobalRoleSettings globalRole = mapper.readValue(root.get("lockdowndata").toString(), GlobalRoleSettings.class);
			globalRole.setRole("super-admin");
			commonClassDao.update(globalRole);
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(GlobalRoleSettings.class));
			JSONObject j = new JSONObject("{descriptiveScopeDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error("Exception Occured"+e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
}
