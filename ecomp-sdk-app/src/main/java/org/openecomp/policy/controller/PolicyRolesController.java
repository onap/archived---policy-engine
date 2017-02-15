/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.controller;


import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.dao.PolicyRolesDao;
import org.openecomp.policy.rest.jpa.PolicyRoles;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class PolicyRolesController extends RestrictedBaseController{
	
	@Autowired
	PolicyRolesDao policyRolesDao;
	List<String> scopelist;
	
	@RequestMapping(value={"/get_RolesData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPolicyRolesEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("rolesDatas", mapper.writeValueAsString(policyRolesDao.getUserRoles()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/save_NonSuperRolesData"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView SaveRolesEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		    JsonNode root = mapper.readTree(request.getReader());
		    PolicyRoles adapter = mapper.readValue(root.get("editRoleData").toString(), PolicyRoles.class);
		    policyRolesDao.update(adapter);
		    response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");
		
			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(policyRolesDao.getUserRoles());
			JSONObject j = new JSONObject("{rolesDatas: " + responseString + "}");

			out.write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value={"/get_PolicyRolesScopeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPolicyScopesEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			scopelist = new ArrayList<String>();
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			Path gitPath = PolicyController.getGitPath().toAbsolutePath();
			model.put("scopeDatas", mapper.writeValueAsString(readFileRepository(gitPath)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public List<String> readFileRepository(Path path){
		File root = new File(path.toString());
		for ( File file : root.listFiles()){
			if(file.isDirectory()){
				String fileName = getDomain(file);
				if (!(fileName.contains(".git") || fileName.equals(".DS_Store"))) {
					scopelist.add(fileName);
				}
				readFileRepository(file.toPath());
			}
		}
		return scopelist;
	}
	
	public String getDomain(File file) {
		String filePath = file.getAbsolutePath();
		int startIndex = filePath.indexOf("repository")  + 11;
		return filePath.substring(startIndex, filePath.length());
	}

}
