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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.att.research.xacml.api.pap.PAPException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PushPolicyController {
	private static final Logger LOGGER  = FlexLogger.getLogger(PushPolicyController.class);
	
	private static CommonClassDao commonClassDao;
	
	@Autowired
	public PushPolicyController(CommonClassDao commonClassDao){
		PushPolicyController.commonClassDao = commonClassDao;
	}
	
	public PushPolicyController(){}
	
	@RequestMapping(value="/pushPolicy", method=RequestMethod.POST)
	public void pushPolicy(HttpServletRequest request, HttpServletResponse response){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			JsonNode root = mapper.readTree(request.getInputStream());
			String policyScope = root.get("policyScope").asText();
			String filePrefix = root.get("filePrefix").asText();
			String policyName = root.get("policyName").asText();
			String pdpGroup = root.get("pdpGroup").asText();
			String requestID = request.getHeader("X-ECOMP-RequestID");
			if(requestID==null){
				requestID = UUID.randomUUID().toString();
                LOGGER.info("No request ID provided, sending generated ID: " + requestID.toString());
			}
			LOGGER.info("Push policy Request : " + root.asText());
			String policyVersionName = policyScope.replace(".", File.separator) + File.separator
					+ filePrefix + policyName;
			List<?> policyVersionObject = commonClassDao.getDataById(PolicyVersion.class, "policyName", policyVersionName);
			if(policyVersionObject!=null){
				PolicyVersion policyVersion = (PolicyVersion) policyVersionObject.get(0);
				String policyID = policyVersionName.replace(File.separator, "."); // This is before adding version.
				policyVersionName += "." + policyVersion.getActiveVersion() + ".xml";
				addPolicyToGroup(policyScope, policyID, policyVersionName.replace(File.separator, "."), pdpGroup, response);
			}else{
				String message = "Unknown Policy '" + policyName + "'";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
				response.addHeader("error", "unknownPolicy");
				response.addHeader("operation", "push");
				response.addHeader("message", message);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			//safetyChecker(policyName);
		} catch (NullPointerException | IOException e) {
			LOGGER.error(e);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.addHeader("error", "unknown");
			response.addHeader("operation", "push");
			return;
		}
	}

	private void addPolicyToGroup(String policyScope, String policyID, String policyName, String pdpGroup, HttpServletResponse response) {
		StdPDPGroup selectedPDPGroup = null;
		StdPDPPolicy selectedPolicy = null;
		//Get the current policies from the Group and Add the new one
		//Set<PDPPolicy> currentPoliciesInGroup = null;
		try {
			selectedPDPGroup = (StdPDPGroup) XACMLPapServlet.getPAPEngine().getGroup(pdpGroup);
		} catch (PAPException e1) {
			PolicyLogger.error(e1);
		}
		if(selectedPDPGroup==null){
			String message = "Unknown groupId '" + selectedPDPGroup + "'";
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
			response.addHeader("error", "unknownGroupId");
			response.addHeader("operation", "push");
			response.addHeader("message", message);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		//Get PolicyEntity from DB;
		EntityManager em = XACMLPapServlet.getEmf().createEntityManager();
		Query createPolicyQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:policyName");			
		createPolicyQuery.setParameter("scope", policyScope);
		createPolicyQuery.setParameter("policyName", policyName.substring(policyScope.length()+1));
		List<?> createPolicyQueryList = createPolicyQuery.getResultList();
		PolicyEntity policyEntity = null;
		if(createPolicyQueryList.size()>0){
			policyEntity = (PolicyEntity)createPolicyQueryList.get(0);
		}else{
			PolicyLogger.error("Somehow, more than one policy with the same scope, name, and deleted status were found in the database");
			String message = "Unknown Policy '" + policyName + "'";
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
			response.addHeader("error", "unknownPolicy");
			response.addHeader("operation", "push");
			response.addHeader("message", message);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		File temp = new File(policyName);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write(policyEntity.getPolicyData());
			bw.close();
			URI selectedURI = temp.toURI();
			// Create the policy Object
			selectedPolicy = new StdPDPPolicy(policyName, true, policyID, selectedURI);
		} catch (IOException e) {
			LOGGER.error("Unable to create policy '" + policyName + "': "+ e.getMessage());
		} 
		try {
			new ObjectOutputStream(response.getOutputStream()).writeObject(selectedPolicy);
		} catch (IOException e) {
			LOGGER.error(e);
			response.addHeader("error", "policyCopyError");
			response.addHeader("message", e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		response.addHeader("Content-Type","application/json");
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		response.addHeader("operation", "push");
		response.addHeader("policyId", policyName);
		return;
		// TODO : Check point to push policies within PAP. 
		/*PolicyDBDaoTransaction addPolicyToGroupTransaction = XACMLPapServlet.getDbDaoTransaction();
		try{
			if (selectedPolicy != null) {
				// Add Current policies from container
				currentPoliciesInGroup = selectedPDPGroup.getPolicies();
				// copy policy to PAP
				addPolicyToGroupTransaction.addPolicyToGroup(selectedPDPGroup.getId(), policyName,"XACMLPapServlet.pushPolicyController");
				((StdPDPGroup) selectedPDPGroup).copyPolicyToFile(policyName, policyID, new FileInputStream(temp));
				addPolicyToGroupTransaction.commitTransaction();
			}
		}catch (Exception e) {
			addPolicyToGroupTransaction.rollbackTransaction();
			String message = "Policy '" + policyName + "' not copied to group '" + pdpGroup +"': " + e;
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " " + message);
			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.addHeader("error", "policyCopyError");
			response.addHeader("message", message);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		//If the selected policy is in the group we must remove it because the name is default
		for (PDPPolicy existingPolicy : currentPoliciesInGroup) {
			if (existingPolicy.getId().equals(selectedPolicy.getId())) {
				selectedPDPGroup.removePolicyFromGroup(existingPolicy);
				LOGGER.debug("Removing existing policy: " + existingPolicy);
				break;
			}
		}
		//Update the PDP Group after removing old version of policy
		//Set<PDPPolicy> updatedPoliciesInGroup = selectedPDPGroup.getPolicies();
		//need to remove the policy with default name from group
		for (PDPPolicy updatedPolicy : currentPoliciesInGroup) {
			if (updatedPolicy.getName().equalsIgnoreCase("default")) {
				selectedPDPGroup.removePolicyFromGroup(updatedPolicy);
			}
		}
		Set<PDPPolicy> policies = selectedPDPGroup.getPolicies();
		policies.add(selectedPolicy);
		selectedPDPGroup.setPolicies(policies);
		// Update now. 
		try {
			XACMLPapServlet.getPAPEngine().updateGroup(selectedPDPGroup);
		} catch (PAPException e) {
			// TODO Auto-generated catch block
			logger.error("Exception Occured"+e);
		}
		// policy file copied ok and the Group was updated on the PDP
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		response.addHeader("operation", "push");
		response.addHeader("policyId", policyName);
		response.addHeader("groupId", pdpGroup);
		return;*/
	}
}
