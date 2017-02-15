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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.policy.adapter.AutoPushTabAdapter;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.model.PDPGroupContainer;
import org.openecomp.policy.model.PDPPolicyContainer;
import org.openecomp.policy.model.Roles;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;

import com.att.research.xacml.api.pap.PAPException;
//import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


@Controller
@RequestMapping({"/"})
public class AutoPushController extends RestrictedBaseController{

	private static final Logger logger = FlexLogger.getLogger(AutoPushController.class);

	private PDPGroupContainer container;
	protected List<EcompPDPGroup> groups = Collections.synchronizedList(new ArrayList<EcompPDPGroup>());
	
	private static PDPPolicyContainer policyContainer;
	Set<PDPPolicy> selectedPolicies;

	@Autowired
	PolicyVersionDao policyVersionDao;

	public synchronized void refreshGroups() {
		synchronized(this.groups) { 
			this.groups.clear();
			try {
				this.groups.addAll(PolicyController.getPapEngine().getEcompPDPGroups());
			} catch (PAPException e) {
				String message = "Unable to retrieve Groups from server: " + e;
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
			}

		}
	}

	@RequestMapping(value={"/get_AutoPushPoliciesContainerData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPolicyGroupContainerData(HttpServletRequest request, HttpServletResponse response){
		try{
			Path gitPath = PolicyController.getGitPath().toAbsolutePath();
			PrintWriter out = response.getWriter();
			JSONObject j = new JSONObject("{data: " + readFileRepository(gitPath,0,0,request).toString() + "}");
			out.write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public JSONArray readFileRepository(Path path, int id, int parentid, HttpServletRequest request){
		Set<String> scopes = null;
		List<String> roles = null;
		String userId = null;
		try {
			userId = UserUtils.getUserIdFromCookie(request);
		} catch (Exception e) {
			logger.error("Exception Occured while reading userid from cookie" +e);
		}
		JSONArray fileJSONArray = new JSONArray();
		File root = new File(path.toString());
		if(parentid==0 ){
			parentid = 1;
		}
		List<Roles> userRoles = PolicyController.getRoles(userId);
		roles = new ArrayList<String>();
		scopes = new HashSet<String>();
		for(Roles userRole: userRoles){
			roles.add(userRole.getRole());
			scopes.add(userRole.getScope());
		}

		for ( File file : root.listFiles()){
			if (!(file.toString().contains(".git") || file.equals(".DS_Store"))) {
				if(file.isDirectory()){
					JSONObject el = new JSONObject();
					String fileName = file.getName().toString();
					el.put("categoryId", id+1);
					el.put("name", fileName);
					el.put("dateModified", lastModified(file));
					el.put("filePath", file.getPath());
					el.put("parentCategoryId", parentid);
					el.put("files",readFileRepository(file.toPath(),id+1, id+1, request));
					if (roles.contains("super-admin") || roles.contains("super-editor")   || roles.contains("super-guest") ) {
						fileJSONArray.put(el);
					}else{
						String filePath =  file.getPath().toString();
						int startIndex = filePath.indexOf("repository") + 11;
						filePath = filePath.substring(startIndex, filePath.length());
						if (scopes.contains(filePath)) {
							fileJSONArray.put(el);
						} 
					}
				}else{
					JSONObject el = new JSONObject();
					String policyName = "";
					String version = "";
					String fileName = file.getName().toString();
					if(fileName.endsWith(".xml")){
						fileName = fileName.substring(0, fileName.lastIndexOf('.'));
						fileName = fileName.substring(0, fileName.lastIndexOf('.'));
						//Query the database
						String parent = file.toString().substring(file.toString().indexOf("repository")+ 11);
						parent = FilenameUtils.removeExtension(parent);
						version = parent.substring(parent.indexOf(".")+1);
						policyName = parent.substring(0, parent.lastIndexOf("."));
						if(policyName.contains("\\")){
							String scope = policyName.substring(0, policyName.lastIndexOf("\\"));
							policyName = scope + "\\" + policyName.substring(policyName.lastIndexOf("\\"));
						}		
					}
					el.put("categoryId", id+1);
					el.put("name", fileName);
					el.put("parentCategoryId", parentid);
					el.put("dateModified", lastModified(file));
					el.put("version", version);
					el.put("filePath", file.getPath());
					el.put("files",new ArrayList());
					String query = "from PolicyVersion where POLICY_NAME ='" +policyName+"' and ACTIVE_VERSION ='"+version+"'";
					Boolean active = PolicyController.getActivePolicy(query);
					if(active){
						if (roles.contains("super-admin") || roles.contains("super-editor")   || roles.contains("super-guest") ) {
							fileJSONArray.put(el);
						}else{
							String filePath =  file.getPath().toString();
							int startIndex = filePath.indexOf("repository") + 11;
							filePath = filePath.substring(startIndex, filePath.length());
							filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
							if (scopes.contains(filePath)) {
								fileJSONArray.put(el);
							} 
						}
					}
				}
			}
		}
		return fileJSONArray;
	}

	public Date lastModified(File file) {
		return new Date(file.lastModified());
	}

	public String getVersion(File file) {
		try {
			return XACMLPolicyScanner.getVersion(Paths.get(file.getAbsolutePath()));
		} catch (IOException e) {
			return "";
		}
	}

	public String getDomain(File file) {
		String filePath = file.getAbsolutePath();
		int startIndex = filePath.indexOf("repository")  + 11;
		return filePath.substring(startIndex, filePath.length());
	}


	@RequestMapping(value={"/auto_Push/PushPolicyToPDP.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView PushPolicyToPDPGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			ArrayList<Object> selectedPDPS = new ArrayList<Object>();
			ArrayList<File> selectedPoliciesInUI = new ArrayList<File>();
			this.groups.addAll(PolicyController.getPapEngine().getEcompPDPGroups());
			ObjectMapper mapper = new ObjectMapper();
			this.container = new PDPGroupContainer(PolicyController.getPapEngine());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			AutoPushTabAdapter adapter = (AutoPushTabAdapter) mapper.readValue(root.get("pushTabData").toString(), AutoPushTabAdapter.class);
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
				Path file =  Paths.get(selected.get("filePath").toString());
				selectedPoliciesInUI.add(file.toFile());
			}

			for (Object pdpDestinationGroupId :  selectedPDPS) {
				Set<PDPPolicy> currentPoliciesInGroup = new HashSet<PDPPolicy>();
				Set<PDPPolicy> selectedPolicies = new HashSet<PDPPolicy>();
				for (File policyId : selectedPoliciesInUI) {
					logger.debug("Handlepolicies..." + pdpDestinationGroupId + policyId);
					//
					// Get the current selection
					File selectedItem = policyId;
					//
					assert (selectedItem != null);
					if (selectedItem.isDirectory()) {
						//AdminNotification.warn("Select only the Policy");
						return null;
					}
					// create the id of the target file
					// Our standard for file naming is:
					// <domain>.<filename>.<version>.xml
					// since the file name usually has a ".xml", we need to strip
					// that
					// before adding the other parts
					String name = selectedItem.getName();
					if(name.endsWith(".xml")){
						name = name.substring(0, name.length() - 4);
						name = name.substring(0, name.lastIndexOf("."));
					}
					String id = name;
					if (id.endsWith(".xml")) {
						id = id.substring(0, id.length() - 4);
						id = id.substring(0, id.lastIndexOf("."));
					}
					// add on the version string
					String version = getVersion(selectedItem);
					id += "." + version;
					// put the .xml on the end
					id += ".xml";
					// track on the domain in front. Do this one level at a time
					// until we
					// reach one of the roots
					String domain = getDomain(selectedItem);
					String mainDomain = domain.substring(0, domain.lastIndexOf(File.separator) );
					logger.info("print the main domain value"+mainDomain);
					String path = mainDomain.replace('\\', '.');
					if(path.contains("/")){
						path = mainDomain.replace('/', '.');
						logger.info("print the path:" +path);
					}
					id = path + "." + id;
					// Default policy to be Root policy; user can change to deferred
					// later
					URI selectedURI = selectedItem.toURI();
					StdPDPPolicy selectedPolicy = null;
					try {
						//
						// Create the policy
						selectedPolicy = new StdPDPPolicy(id, true, name, selectedURI);
					} catch (IOException e) {
						logger.error("Unable to create policy '" + id + "': "+ e.getMessage());
						//AdminNotification.warn("Unable to create policy '" + id + "': " + e.getMessage());
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
							PolicyController.getPapEngine().copyPolicy(selectedPolicy, (StdPDPGroup) pdpDestinationGroupId);
						} catch (PAPException e) {
							e.printStackTrace();
							return null;
						}
						selectedPolicies.add(selectedPolicy);
					}
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

	@RequestMapping(value={"/auto_Push/remove_GroupPolicies.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePDPGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			this.container = new PDPGroupContainer(PolicyController.getPapEngine());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());  
			StdPDPGroup group = (StdPDPGroup)mapper.readValue(root.get("activePdpGroup").toString(), StdPDPGroup.class);
			JsonNode removePolicyData = root.get("data");
			policyContainer = new PDPPolicyContainer(group);
			if(removePolicyData.size() > 0){
				for(int i = 0 ; i < removePolicyData.size(); i++){
					String data = removePolicyData.get(i).toString();
					AutoPushController.policyContainer.removeItem(data);
				}
				Set<PDPPolicy> changedPolicies = new HashSet<PDPPolicy>();
				changedPolicies.addAll((Collection<PDPPolicy>) AutoPushController.policyContainer.getItemIds());
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