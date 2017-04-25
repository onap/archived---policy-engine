/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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
package org.openecomp.policy.pap.xacml.rest.handler;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.pap.xacml.rest.XACMLPapServlet;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;

import com.att.research.xacml.util.XACMLProperties;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PushPolicyHandler {
	
	/*
	 * Get Active Version. 
	 */
	public void getActiveVersion(HttpServletRequest request, HttpServletResponse response) {
		EntityManager em = null;
		if(XACMLPapServlet.getEmf()!=null){
			em = (EntityManager) XACMLPapServlet.getEmf().createEntityManager();
		}
		if (em==null){
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Error creating entity manager with persistence unit: " + XACMLPapServlet.getPersistenceUnit());
			return;
		}
		String policyScope = request.getParameter("policyScope");
		String filePrefix = request.getParameter("filePrefix");
		String policyName = request.getParameter("policyName");

		String pvName = policyScope + File.separator + filePrefix + policyName;
		int activeVersion = 0;

		//Get the Active Version to use in the ID
		em.getTransaction().begin();
		Query query = em.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
		query.setParameter("pname", pvName);

		@SuppressWarnings("rawtypes")
		List result = query.getResultList();
		PolicyVersion versionEntity = null;
		if (!result.isEmpty()) {
			versionEntity = (PolicyVersion) result.get(0);
			em.persist(versionEntity);
			activeVersion = versionEntity.getActiveVersion();
			em.getTransaction().commit();
		} else {
			PolicyLogger.debug("No PolicyVersion using policyName found");
		}

		//clean up connection
		em.close();
		if (String.valueOf(activeVersion)!=null || !String.valueOf(activeVersion).equalsIgnoreCase("")) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("version", String.valueOf(activeVersion));								
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);								
		}
	}
	
	/*
	 * Get Git Path 
	 */
	public void getGitPath(HttpServletRequest request, HttpServletResponse response) {
		String policyScope = request.getParameter("policyScope");
		String filePrefix = request.getParameter("filePrefix");
		String policyName = request.getParameter("policyName");
		String activeVersion = request.getParameter("activeVersion");
		
		Path workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE), "admin");
		Path repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY));
		Path gitPath = Paths.get(workspacePath.toString(), repositoryPath.getFileName().toString());
		
		//getting the fullpath of the gitPath and convert to string
		String fullGitPath = gitPath.toAbsolutePath().toString();
		String finalGitPath = null;
		
		//creating the parentPath directory for the Admin Console use
		if(fullGitPath.contains("\\")){
			if(fullGitPath.contains("ECOMP-PAP-REST")){
				finalGitPath = fullGitPath.replace("ECOMP-PAP-REST", "ecomp-sdk-app");
			}else{
				finalGitPath = fullGitPath.replace("ATT-PAP-REST", "ATT-ecomp-sdk-app");
			}
		}else{
			finalGitPath = fullGitPath.replace("pap",  "console");
		}

		finalGitPath += File.separator + policyScope + File.separator + filePrefix + policyName + "." + activeVersion + ".xml";
		File file = new File(finalGitPath);
		URI uri = file.toURI();

		//
		// Extract XACML policy information
		//
		Boolean isValid = false;
		String policyId = null;
		String description = null;
		String	version = null;

		URL url;
		try {
			url = uri.toURL();
			Object rootElement = XACMLPolicyScanner.readPolicy(url.openStream());
			if (rootElement == null ||
					(
							! (rootElement instanceof PolicySetType) &&
							! (rootElement instanceof PolicyType)
							)	) {
				PolicyLogger.warn("No root policy element in URI: " + uri.toString() + " : " + rootElement);
				isValid = false;
			} else {
				if (rootElement instanceof PolicySetType) {
					policyId = ((PolicySetType)rootElement).getPolicySetId();
					description = ((PolicySetType)rootElement).getDescription();
					isValid = true;
					version = ((PolicySetType)rootElement).getVersion();
				} else if (rootElement instanceof PolicyType) {
					policyId = ((PolicyType)rootElement).getPolicyId();
					description = ((PolicyType)rootElement).getDescription();
					version = ((PolicyType)rootElement).getVersion();
					isValid = true;
				} else {
					PolicyLogger.error("Unknown root element: " + rootElement.getClass().getCanonicalName());
				}
			}
		} catch (Exception e) {
			PolicyLogger.error("Exception Occured While Extracting Policy Information");
		} 
		if (!finalGitPath.equalsIgnoreCase("") || policyId!=null || description!=null || version!=null || isValid!=null) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("gitPath", finalGitPath);
			response.addHeader("policyId", policyId);
			response.addHeader("description", description);
			response.addHeader("version", version);
			response.addHeader("isValid", isValid.toString());
		}
	}
	
	/*
	 * Get Selected URI path. 
	 */
	public void getSelectedURI(HttpServletRequest request, HttpServletResponse response) {
		String gitPath = request.getParameter("gitPath");
		File file = new File(gitPath);
		PolicyLogger.debug("The fileItem is : " + file.toString());
		URI selectedURI = file.toURI();
		String uri = selectedURI.toString();
		if (!uri.equalsIgnoreCase("")) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("selectedURI", uri);								
		} else {						
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);								
		}						
	}
	
	public boolean preSafetyCheck(StdPDPPolicy policy, String configHome){
		return true;
	}
	
	public boolean preSafetyCheck(EcompPDPGroup policy, String configHome){
		return true;
	}
	
	public static PushPolicyHandler getInstance() {
		try {
			Class<?> pushPolicyHandler = Class.forName(XACMLProperties.getProperty("pushPolicy.impl.className", PushPolicyHandler.class.getName()));
			PushPolicyHandler instance = (PushPolicyHandler) pushPolicyHandler.newInstance(); 
			return instance;
		} catch (Exception e) {
			PolicyLogger.error(e.getMessage());
		}
		return null;
	}
}
