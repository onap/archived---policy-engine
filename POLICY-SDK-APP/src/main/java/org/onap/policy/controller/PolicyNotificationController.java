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


/*
 *
 * */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.WatchPolicyNotificationTable;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Controller
@RequestMapping({"/"})
public class PolicyNotificationController extends RestrictedBaseController {
    private static Logger logger = FlexLogger.getLogger(PolicyNotificationController.class);

	@Autowired
	CommonClassDao commonClassDao;

	@RequestMapping(value={"/watchPolicy"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView watchPolicy(HttpServletRequest request, HttpServletResponse response) throws IOException{
		StringBuilder path = new StringBuilder();
		String responseValue = "";
		try {
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			logger.info("userid info: " + userId);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			String name = root.get("watchData").get("name").toString();
			JsonNode pathList = root.get("watchData").get("path");
			String finalName;
			if(pathList.isArray()){
				ArrayNode arrayNode = (ArrayNode) pathList;
				for (int i = 0; i < arrayNode.size(); i++) {
					JsonNode individualElement = arrayNode.get(i);
					if(i == 0){
						path.append(individualElement.toString().replace("\"", "").trim());
					}else{
						path.append(File.separator + individualElement.toString().replace("\"", "").trim());
					}
				}
			}

			if(pathList.size() > 0){
				finalName = path + File.separator + name.replace("\"", "").trim();
			}else{
				finalName = name.replace("\"", "").trim();
			}
			if(finalName.contains("\\")){
				finalName = finalName.replace("\\", "\\\\");
			}
			String query = "from WatchPolicyNotificationTable where POLICYNAME = :finalName and LOGINIDS = :userId";
			SimpleBindings params = new SimpleBindings();
			params.put("finalName", finalName);
			params.put("userId", userId);
			List<Object> watchList = commonClassDao.getDataByQuery(query, params);
			if(watchList.isEmpty()){
				if(finalName.contains("\\\\")){
					finalName = finalName.replace("\\\\", File.separator);
				}
				WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
				watch.setPolicyName(finalName);
				watch.setLoginIds(userId);
				commonClassDao.save(watch);
				responseValue = "You have Subscribed Successfully";
			}else{
				commonClassDao.delete(watchList.get(0));
				responseValue = "You have UnSubscribed Successfully";
			}

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(responseValue);
			JSONObject j = new JSONObject("{watchData: " + responseString + "}");
			out.write(j.toString());
			return null;
		}catch(Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			logger.error("Error druing watchPolicy function " + e);
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
}
