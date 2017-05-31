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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.model.PDPGroupContainer;
import org.openecomp.policy.model.PDPPolicyContainer;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.adapter.AutoPushTabAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping({"/"})
public class AutoPushController extends RestrictedBaseController{

	private static final Logger logger = FlexLogger.getLogger(AutoPushController.class);
	
	@Autowired
	CommonClassDao commonClassDao;

	private PDPGroupContainer container;
	protected List<EcompPDPGroup> groups = Collections.synchronizedList(new ArrayList<EcompPDPGroup>());
	
	private PDPPolicyContainer policyContainer;

	private PolicyController policyController;
	public PolicyController getPolicyController() {
		return policyController;
	}

	public void setPolicyController(PolicyController policyController) {
		this.policyController = policyController;
	}

	private List<Object> data;

	public synchronized void refreshGroups() {
		synchronized(this.groups) { 
			this.groups.clear();
			try {
				PolicyController controller = getPolicyControllerInstance();
				this.groups.addAll(controller.getPapEngine().getEcompPDPGroups());
			} catch (PAPException e) {
				String message = "Unable to retrieve Groups from server: " + e;
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
			}

		}
	}

	private PolicyController getPolicyControllerInstance(){
		return policyController != null ? getPolicyController() : new PolicyController();
	}
	
	@RequestMapping(value={"/get_AutoPushPoliciesContainerData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPolicyGroupContainerData(HttpServletRequest request, HttpServletResponse response){
		try{
			Set<String> scopes = null;
			List<String> roles = null;
			data = new ArrayList<Object>();
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			PolicyController controller = policyController != null ? getPolicyController() : new PolicyController();
			List<Object> userRoles = controller.getRoles(userId);
			roles = new ArrayList<>();
			scopes = new HashSet<>();
			for(Object role: userRoles){
				Roles userRole = (Roles) role;
				roles.add(userRole.getRole());
				if(userRole.getScope() != null){
					if(userRole.getScope().contains(",")){
						String[] multipleScopes = userRole.getScope().split(",");
						for(int i =0; i < multipleScopes.length; i++){
							scopes.add(multipleScopes[i]);
						}
					}else{
						if(!userRole.getScope().equals("")){
							scopes.add(userRole.getScope());
						}
					}		
				}
			}
			if (roles.contains("super-admin") || roles.contains("super-editor")  || roles.contains("super-guest")) {
				data = commonClassDao.getData(PolicyVersion.class);
			}else{
				if(!scopes.isEmpty()){
					for(String scope : scopes){
						String query = "From PolicyVersion where policy_name like '"+scope+"%' and id > 0";
						List<Object> filterdatas = commonClassDao.getDataByQuery(query);
						if(filterdatas != null){
							for(int i =0; i < filterdatas.size(); i++){
								data.add(filterdatas.get(i));
							}	
						}
					}
				}else{
					PolicyVersion emptyPolicyName = new PolicyVersion();
					emptyPolicyName.setPolicyName("Please Contact Policy Super Admin, There are no scopes assigned to you");
					data.add(emptyPolicyName);
				}
			}
			model.put("policydatas", mapper.writeValueAsString(data));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			logger.error("Exception Occured"+e);
		}
	}

	@RequestMapping(value={"/auto_Push/PushPolicyToPDP.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView PushPolicyToPDPGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			ArrayList<Object> selectedPDPS = new ArrayList<>();
			ArrayList<String> selectedPoliciesInUI = new ArrayList<>();
			PolicyController controller = getPolicyControllerInstance();
			this.groups.addAll(controller.getPapEngine().getEcompPDPGroups());
			ObjectMapper mapper = new ObjectMapper();
			this.container = new PDPGroupContainer(controller.getPapEngine());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			AutoPushTabAdapter adapter = mapper.readValue(root.get("pushTabData").toString(), AutoPushTabAdapter.class);
			for (Object pdpGroupId :  adapter.getPdpDatas()) {
				LinkedHashMap<?, ?> selectedPDP = (LinkedHashMap<?, ?>)pdpGroupId;
				for(EcompPDPGroup pdpGroup : this.groups){
					if(pdpGroup.getId().equals(selectedPDP.get("id"))){
						selectedPDPS.add(pdpGroup);
					}
				}
			}

			for (Object policyId :  adapter.getPolicyDatas()) {
				LinkedHashMap<?, ?> selected = (LinkedHashMap<?, ?>)policyId;
				String policyName = selected.get("policyName").toString() + "." + selected.get("activeVersion").toString() + ".xml";
				selectedPoliciesInUI.add(policyName);
			}

			for (Object pdpDestinationGroupId :  selectedPDPS) {
				Set<PDPPolicy> currentPoliciesInGroup = new HashSet<>();
				Set<PDPPolicy> selectedPolicies = new HashSet<>();
				for (String policyId : selectedPoliciesInUI) {
					logger.debug("Handlepolicies..." + pdpDestinationGroupId + policyId);
					
					//
					// Get the current selection
					String selectedItem = policyId;
					//
					assert (selectedItem != null);
					// create the id of the target file
					// Our standard for file naming is:
					// <domain>.<filename>.<version>.xml
					// since the file name usually has a ".xml", we need to strip
					// that
					// before adding the other parts
					String name = selectedItem.replace(File.separator, ".");
					String id = name;
					if (id.endsWith(".xml")) {
						id = id.replace(".xml", "");
						id = id.substring(0, id.lastIndexOf("."));
					}
					
					// Default policy to be Root policy; user can change to deferred
					// later
					
					StdPDPPolicy selectedPolicy = null;
					String dbCheckName = name;
					if(dbCheckName.contains("Config_")){
						dbCheckName = dbCheckName.replace(".Config_", ":Config_");
					}else if(dbCheckName.contains("Action_")){
						dbCheckName = dbCheckName.replace(".Action_", ":Action_");
					}else if(dbCheckName.contains("Decision_")){
						dbCheckName = dbCheckName.replace(".Decision_", ":Decision_");
					}
					String[] split = dbCheckName.split(":");
					String query = "FROM PolicyEntity where policyName = '"+split[1]+"' and scope ='"+split[0]+"'";
					List<Object> queryData = controller.getDataByQuery(query);
					PolicyEntity policyEntity = (PolicyEntity) queryData.get(0);
					File temp = new File(name);
					BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
					bw.write(policyEntity.getPolicyData());
					bw.close();
					URI selectedURI = temp.toURI();
					try {
						//
						// Create the policy
						selectedPolicy = new StdPDPPolicy(name, true, id, selectedURI);
					} catch (IOException e) {
						logger.error("Unable to create policy '" + name + "': "+ e.getMessage());
					}
					StdPDPGroup selectedGroup = (StdPDPGroup) pdpDestinationGroupId;
					if (selectedPolicy != null) {
						// Add Current policies from container
						for (EcompPDPGroup group : container.getGroups()) {
							if (group.getId().equals(selectedGroup.getId())) {
								currentPoliciesInGroup.addAll(group.getPolicies());
							}
						}
						// copy policy to PAP
						try {
							controller.getPapEngine().copyPolicy(selectedPolicy, (StdPDPGroup) pdpDestinationGroupId);
						} catch (PAPException e) {
							logger.error("Exception Occured"+e);
							return null;
						}
						selectedPolicies.add(selectedPolicy);
					}
					temp.delete();
				}
				StdPDPGroup pdpGroup = (StdPDPGroup) pdpDestinationGroupId;
				StdPDPGroup updatedGroupObject = new StdPDPGroup(pdpGroup.getId(), pdpGroup.isDefaultGroup(), pdpGroup.getName(), pdpGroup.getDescription(), pdpGroup.getDirectory());
				updatedGroupObject.setEcompPdps(pdpGroup.getEcompPdps());
				updatedGroupObject.setPipConfigs(pdpGroup.getPipConfigs());
				updatedGroupObject.setStatus(pdpGroup.getStatus());

				// replace the original set of Policies with the set from the
				// container (possibly modified by the user)
				// do not allow multiple copies of same policy
				Iterator<PDPPolicy> policyIterator = currentPoliciesInGroup.iterator();
				logger.debug("policyIterator....." + selectedPolicies);
				while (policyIterator.hasNext()) {
					PDPPolicy existingPolicy = policyIterator.next();
					for (PDPPolicy selPolicy : selectedPolicies) {
						if (selPolicy.getName().equals(existingPolicy.getName())) {
							if (selPolicy.getVersion().equals(existingPolicy.getVersion())) {
								if (selPolicy.getId().equals(existingPolicy.getId())) {
									policyIterator.remove();
									logger.debug("Removing policy: " + selPolicy);
									break;
								}
							} else {
								policyIterator.remove();
								logger.debug("Removing Old Policy version: "+ selPolicy);
								break;
							}
						}
					}
				}

				currentPoliciesInGroup.addAll(selectedPolicies);
				updatedGroupObject.setPolicies(currentPoliciesInGroup);
				this.container.updateGroup(updatedGroupObject);

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
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value={"/auto_Push/remove_GroupPolicies.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePDPGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			PolicyController controller = getPolicyControllerInstance();
			this.container = new PDPGroupContainer(controller.getPapEngine());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());  
			StdPDPGroup group = (StdPDPGroup)mapper.readValue(root.get("activePdpGroup").toString(), StdPDPGroup.class);
			JsonNode removePolicyData = root.get("data");
			policyContainer = new PDPPolicyContainer(group);
			if(removePolicyData.size() > 0){
				for(int i = 0 ; i < removePolicyData.size(); i++){
					String data = removePolicyData.get(i).toString();
					this.policyContainer.removeItem(data);
				}
				Set<PDPPolicy> changedPolicies = new HashSet<>();
				changedPolicies.addAll((Collection<PDPPolicy>) this.policyContainer.getItemIds());
				StdPDPGroup updatedGroupObject = new StdPDPGroup(group.getId(), group.isDefaultGroup(), group.getName(), group.getDescription(),null);
				updatedGroupObject.setPolicies(changedPolicies);
				updatedGroupObject.setEcompPdps(group.getEcompPdps());
				updatedGroupObject.setPipConfigs(group.getPipConfigs());
				updatedGroupObject.setStatus(group.getStatus());
				this.container.updateGroup(updatedGroupObject);
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
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

}