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


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.dao.GroupEntityDao;
import org.openecomp.policy.dao.PDPEntityDao;
import org.openecomp.policy.model.PDPGroupContainer;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDP;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping({"/"})
public class PDPController extends RestrictedBaseController {
	private static final  Logger logger = FlexLogger.getLogger(PDPController.class);
	
	@Autowired
	GroupEntityDao groupDAO;
	
	@Autowired
	PDPEntityDao pdpDAO;
	
	protected List<EcompPDPGroup> groups = Collections.synchronizedList(new ArrayList<EcompPDPGroup>());
	private PDPGroupContainer container;
	
	public synchronized void refreshGroups() {
		synchronized(this.groups) { 
			this.groups.clear();
			try {
				this.groups.addAll(PolicyController.getPapEngine().getEcompPDPGroups());
			} catch (PAPException e) {
				String message = "Unable to retrieve Groups from server: " + e;
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Pap Engine is Null" + message);
			}
		
		}
	}
	
	@RequestMapping(value={"/get_PDPGroupContainerData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPDPGroupContainerData(HttpServletRequest request, HttpServletResponse response){
		try{
			ObjectMapper mapper = new ObjectMapper();
			refreshGroups();
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while retrieving the PDP Group Container data" + e);
		}
	}
	
	@RequestMapping(value={"/get_PDPGroupData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPDPGroupEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			ObjectMapper mapper = new ObjectMapper();
			refreshGroups();
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while retrieving the PDP Group data" + e);
		}
	}
	
	@RequestMapping(value={"/pdp_Group/save_pdp_group"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView savePDPGroup(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      this.container = new PDPGroupContainer(PolicyController.getPapEngine());
	      StdPDPGroup pdpGroupData =  mapper.readValue(root.get("pdpGroupData").toString().replace("groupName", "name"), StdPDPGroup.class);
	      try {
	    	  if(pdpGroupData.getId() == null){
	    		  this.container.addNewGroup(pdpGroupData.getName(), pdpGroupData.getDescription());
	    	  }else{
	    		  this.container.updateGroup(pdpGroupData);
	    	  }
				
			} catch (Exception e) {
				String message = "Unable to create Group.  Reason:\n" + e.getMessage();
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while creating the PDP Group" + message);
			}
	        
	    
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      refreshGroups();
	      JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
		  JSONObject j = new JSONObject(msg);
	      out.write(j.toString());
	      
	      return null;
	    }
	    catch (Exception e){
	     logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Saving the PDP Group" + e);
	      response.setCharacterEncoding("UTF-8");
	      request.setCharacterEncoding("UTF-8");
	      PrintWriter out = response.getWriter();
	      out.write(e.getMessage());
	    }
	    return null;
	  }
	  
	  @RequestMapping(value={"/pdp_Group/remove_pdp_group"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removePDPGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      this.container = new PDPGroupContainer(PolicyController.getPapEngine()); 
	      StdPDPGroup pdpGroupData =  mapper.readValue(root.get("pdpGroupData").toString(), StdPDPGroup.class);
	      	if(pdpGroupData.getName().equals("Default")) {
				throw new UnsupportedOperationException("You can't remove the Default Group.");
			}else{
				this.container.removeGroup(pdpGroupData, null);
			}
	  
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      
	      refreshGroups();
	      JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
		  JSONObject j = new JSONObject(msg);
	      out.write(j.toString());
	      
	      return null;
	    }
	    catch (Exception e){
	      logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Removing the PDP Group" + e);
	      response.setCharacterEncoding("UTF-8");
	      request.setCharacterEncoding("UTF-8");
	      PrintWriter out = response.getWriter();
	      out.write(e.getMessage());
	    }
	    return null;
	  }
	  
	  @RequestMapping(value={"/pdp_Group/save_pdpTogroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView savePDPToGroup(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      this.container = new PDPGroupContainer(PolicyController.getPapEngine()); 
	      String update = root.get("update").toString();
	      PdpData pdpGroupData = (PdpData)mapper.readValue(root.get("pdpInGroup").toString(), PdpData.class);
	      StdPDPGroup activeGroupData =  mapper.readValue(root.get("activePDP").toString(), StdPDPGroup.class);
	      try {
	    	  
	    	  if(update.contains("false")){
	    		  this.container.addNewPDP(pdpGroupData.getId(), activeGroupData, pdpGroupData.getName(), pdpGroupData.getDescription(), pdpGroupData.getJmxPort());
	    	  }else{
	    		  this.container.updateGroup(activeGroupData);
	    	  }
			} catch (Exception e) {
				String message = "Unable to create Group.  Reason:\n" + e.getMessage();
				 logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Creating Pdp in PDP Group" + message);
			}
	        
	    
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      refreshGroups();
	      JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
		  JSONObject j = new JSONObject(msg);
	      out.write(j.toString());
	      
	      return null;
	    }
	    catch (Exception e){
	      logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Creating Pdp in PDP Group" + e);
	      response.setCharacterEncoding("UTF-8");
	      request.setCharacterEncoding("UTF-8");
	      PrintWriter out = response.getWriter();
	      out.write(e.getMessage());
	    }
	    return null;
	  }
	  
	  @RequestMapping(value={"/pdp_Group/remove_pdpFromGroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removePDPFromGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      this.container = new PDPGroupContainer(PolicyController.getPapEngine()); 
	      StdPDP deletePdp =  mapper.readValue(root.get("data").toString(), StdPDP.class);
	      StdPDPGroup activeGroupData =  mapper.readValue(root.get("activePDP").toString(), StdPDPGroup.class);
	      	
	      this.container.removePDP(deletePdp, activeGroupData);
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      refreshGroups();
	      String responseString = mapper.writeValueAsString(groups);
	      JSONObject j = new JSONObject("{pdpEntityDatas: " + responseString + "}");
	      out.write(j.toString());
	      
	      return null;
	    }
	    catch (Exception e){
	      logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Removing Pdp from PDP Group" + e);
	      response.setCharacterEncoding("UTF-8");
	      request.setCharacterEncoding("UTF-8");
	      PrintWriter out = response.getWriter();
	      out.write(e.getMessage());
	    }
	    return null;
	  }
}

class PdpData{
	String id;
	int jmxPort;
	String name;
	String description;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getJmxPort() {
		return jmxPort;
	}
	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
