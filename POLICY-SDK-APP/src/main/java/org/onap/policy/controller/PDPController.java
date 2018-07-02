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


import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.admin.RESTfulPAPEngine;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.model.PDPGroupContainer;
import org.onap.policy.utils.UserUtils.Pair;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping({"/"})
public class PDPController extends RestrictedBaseController {
	private static final  Logger policyLogger = FlexLogger.getLogger(PDPController.class);

	protected List<OnapPDPGroup> groups = Collections.synchronizedList(new ArrayList<OnapPDPGroup>());
	private PDPGroupContainer container;

	private static String SUPERADMIN = "super-admin";
	private static String SUPEREDITOR = "super-editor";
	private static String SUPERGUEST = "super-guest";

	private Set<OnapPDPGroup> groupsData;

	private boolean junit = false;

	private PolicyController policyController;
	public PolicyController getPolicyController() {
		return policyController;
	}

	public void setPolicyController(PolicyController policyController) {
		this.policyController = policyController;
	}

	public synchronized void refreshGroups(HttpServletRequest request) {
		synchronized(this.groups) { 
			this.groups.clear();
			try {
				PolicyController controller = getPolicyControllerInstance();
				Set<PDPPolicy> filteredPolicies = new HashSet<>();
				Set<String> scopes;
				List<String> roles;
				String userId =  isJunit()  ? "Test" : UserUtils.getUserSession(request).getOrgUserId();
				List<Object> userRoles = controller.getRoles(userId);
				Pair<Set<String>, List<String>> pair = org.onap.policy.utils.UserUtils.checkRoleAndScope(userRoles);
				roles = pair.u;
				scopes = pair.t;
				
				if(!junit&& controller.getPapEngine()==null){
				    setPAPEngine(request);
				}
				if (roles.contains(SUPERADMIN) || roles.contains(SUPEREDITOR) || roles.contains(SUPERGUEST) ) {
					if(!junit){
						this.groups.addAll(controller.getPapEngine().getOnapPDPGroups());
					}else{
						this.groups.addAll(this.getGroupsData());
					}	
				}else{
					if(!userRoles.isEmpty() && !scopes.isEmpty()){
						this.groups.addAll(controller.getPapEngine().getOnapPDPGroups());
						List<OnapPDPGroup> tempGroups = new ArrayList<>();
						if(!groups.isEmpty()){
							Iterator<OnapPDPGroup> pdpGroup = groups.iterator();
							while(pdpGroup.hasNext()){
								OnapPDPGroup group = pdpGroup.next();
								Set<PDPPolicy> policies = group.getPolicies();
								for(PDPPolicy policy : policies){
									for(String scope : scopes){
										scope = scope.replace(File.separator, ".");
										String policyName = policy.getId();
										if(policyName.contains(".Config_")){
											policyName = policyName.substring(0, policyName.lastIndexOf(".Config_"));
										}else if(policyName.contains(".Action_")){
											policyName = policyName.substring(0, policyName.lastIndexOf(".Action_"));
										}else if(policyName.contains(".Decision_MS_")){
											policyName = policyName.substring(0, policyName.lastIndexOf(".Decision_MS_"));
										}else if(policyName.contains(".Decision_")){
											policyName = policyName.substring(0, policyName.lastIndexOf(".Decision_"));
										}
										if(policyName.startsWith(scope)){
											filteredPolicies.add(policy);
										}
									}
								}
								pdpGroup.remove();
								StdPDPGroup newGroup = (StdPDPGroup) group;
								newGroup.setPolicies(filteredPolicies);
								tempGroups.add(newGroup);
							}	
							groups.clear();
							groups = tempGroups;	
						}
					}
				}
			} catch (PAPException e) {
				String message = "Unable to retrieve Groups from server: " + e;
				policyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Pap Engine is Null" + message);
			}
		}
	}

	private void setPAPEngine(HttpServletRequest request) {
	    String myRequestURL = request.getRequestURL().toString();
        try {
            //
            // Set the URL for the RESTful PAP Engine
            //
            PolicyController.setPapEngine((PAPPolicyEngine) new RESTfulPAPEngine(myRequestURL));
        }catch(Exception e){
            policyLogger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Exception Occured while loading PAP",e);
        }
    }

    @RequestMapping(value={"/get_PDPGroupData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPDPGroupEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			ObjectMapper mapper = new ObjectMapper();
			refreshGroups(request);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while retrieving the PDP Group data" + e);
		}
	}

	@RequestMapping(value={"/pdp_Group/save_pdp_group"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void savePDPGroup(HttpServletRequest request, HttpServletResponse response){
		try {
			ObjectMapper mapper = new ObjectMapper();
			PolicyController controller = getPolicyControllerInstance();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			this.container = new PDPGroupContainer(controller.getPapEngine());
			
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			policyLogger.info("****************************************Logging UserID for Save PDP Group Function*****************************************");
			policyLogger.info("UserId:  " + userId + "PDP Group Data:  "+ root.get("pdpGroupData").toString());
			policyLogger.info("***************************************************************************************************************************");
			
			StdPDPGroup pdpGroupData =  mapper.readValue(root.get("pdpGroupData").toString().replace("groupName", "name"), StdPDPGroup.class);
			try {
				if(pdpGroupData.getId() == null){
					this.container.addNewGroup(pdpGroupData.getName(), pdpGroupData.getDescription());
				}else{
					this.container.updateGroup(pdpGroupData);
				}

			} catch (Exception e) {
				String message = "Unable to create Group.  Reason:\n" + e.getMessage();
				policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while creating the PDP Group" + message + e);
			}


			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			refreshGroups(request);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			out.write(j.toString());
		}
		catch (Exception e){
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Saving the PDP Group" + e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = null;
			try {
				request.setCharacterEncoding("UTF-8");
				out = response.getWriter();
				out.write(e.getMessage());
			} catch (Exception e1) {
				policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Saving the PDP Group" + e1);
			}
		}
	}

	@RequestMapping(value={"/pdp_Group/remove_pdp_group"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void removePDPGroup(HttpServletRequest request, HttpServletResponse response){
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyController controller = getPolicyControllerInstance();
			this.container = new PDPGroupContainer(controller.getPapEngine()); 
			
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			policyLogger.info("****************************************Logging UserID for Remove PDP Group Function*****************************************");
			policyLogger.info("UserId:  " + userId + "PDP Group Data:  "+ root.get("pdpGroupData").toString());
			policyLogger.info("*****************************************************************************************************************************");
			
			StdPDPGroup pdpGroupData =  mapper.readValue(root.get("pdpGroupData").toString(), StdPDPGroup.class);
			if("Default".equals(pdpGroupData.getName())) {
				throw new UnsupportedOperationException("You can't remove the Default Group.");
			}else{
				this.container.removeGroup(pdpGroupData, null);
			}

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			refreshGroups(request);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			out.write(j.toString());
		}
		catch (Exception e){
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Removing the PDP Group" + e);
			PrintWriter out;
			try {
				response.setCharacterEncoding("UTF-8");
				request.setCharacterEncoding("UTF-8");
				out = response.getWriter();
				out.write(e.getMessage());
			} catch (Exception e1) {
				policyLogger.error("Exception Occured"+ e1);
			}
		}
	}

	@RequestMapping(value={"/pdp_Group/save_pdpTogroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void savePDPToGroup(HttpServletRequest request, HttpServletResponse response){
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyController controller = getPolicyControllerInstance();
			this.container = new PDPGroupContainer(controller.getPapEngine()); 
			String update = root.get("update").toString();
			PdpData pdpGroupData = mapper.readValue(root.get("pdpInGroup").toString(), PdpData.class);
			StdPDPGroup activeGroupData =  mapper.readValue(root.get("activePDP").toString(), StdPDPGroup.class);
			
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			policyLogger.info("****************************************Logging UserID while Saving  pdp in  PDP Group*****************************************");
			policyLogger.info("UserId:  " + userId + "PDP Group Data:  "+ root.get("pdpInGroup").toString() + "Active Group Data: "+ root.get("activePDP").toString());
			policyLogger.info("*******************************************************************************************************************************");
			
			try {

				if(update.contains("false")){
					this.container.addNewPDP(pdpGroupData.getId(), activeGroupData, pdpGroupData.getName(), pdpGroupData.getDescription(), pdpGroupData.getJmxPort());
				}else{
					this.container.updateGroup(activeGroupData);
				}
			} catch (Exception e) {
				String message = "Unable to create Group.  Reason:\n" + e.getMessage();
				policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Creating Pdp in PDP Group" + message + e);
			}


			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			refreshGroups(request);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			out.write(j.toString());
		}
		catch (Exception e){
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Creating Pdp in PDP Group" + e);
			PrintWriter out;
			try {
				response.setCharacterEncoding("UTF-8");
				request.setCharacterEncoding("UTF-8");
				out = response.getWriter();
				out.write(e.getMessage());
			} catch (Exception e1) {
				policyLogger.error("Exception Occured"+ e1);
			}
		}
	}

	@RequestMapping(value={"/pdp_Group/remove_pdpFromGroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void removePDPFromGroup(HttpServletRequest request, HttpServletResponse response){
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyController controller = getPolicyControllerInstance();
			this.container = new PDPGroupContainer(controller.getPapEngine()); 
			StdPDP deletePdp =  mapper.readValue(root.get("data").toString(), StdPDP.class);
			StdPDPGroup activeGroupData =  mapper.readValue(root.get("activePDP").toString(), StdPDPGroup.class);

			String userId = UserUtils.getUserSession(request).getOrgUserId();
			policyLogger.info("****************************************Logging UserID while Removing  pdp from  PDP Group*****************************************");
			policyLogger.info("UserId:  " + userId + "Delete PDP Group Data:  "+ root.get("data").toString() + "Active Group Data: "+ root.get("activePDP").toString());
			policyLogger.info("***********************************************************************************************************************************");
			
			this.container.removePDP(deletePdp, activeGroupData);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			refreshGroups(request);
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(groups));
			JSONObject j = new JSONObject(msg);
			out.write(j.toString());
		}
		catch (Exception e){
			policyLogger.error(XACMLErrorConstants.ERROR_DATA_ISSUE+"Error Occured while Removing Pdp from PDP Group" + e);
			PrintWriter out;
			try {
				response.setCharacterEncoding("UTF-8");
				request.setCharacterEncoding("UTF-8");
				out = response.getWriter();
				out.write(e.getMessage());
			} catch (Exception e1) {
				policyLogger.error("Exception Occured"+ e1);
			}
		}
	}

	private PolicyController getPolicyControllerInstance(){
		return policyController != null ? getPolicyController() : new PolicyController();
	}

	public boolean isJunit() {
		return junit;
	}

	public void setJunit(boolean junit) {
		this.junit = junit;
	}

	public Set<OnapPDPGroup> getGroupsData() {
		return groupsData;
	}

	public void setGroupsData(Set<OnapPDPGroup> groupsData) {
		this.groupsData = groupsData;
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
