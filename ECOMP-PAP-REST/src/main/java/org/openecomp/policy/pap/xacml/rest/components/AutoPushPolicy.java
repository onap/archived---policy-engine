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

package org.openecomp.policy.pap.xacml.rest.components;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;
import org.openecomp.policy.xacml.api.pap.PAPPolicyEngine;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;

import com.att.research.xacml.api.pap.PDPPolicy;
/**
 * Auto Push Policy based on the property file properties. 
 * 
 * @version 0.1
 */
public class AutoPushPolicy {
	
	private static final Logger LOGGER = FlexLogger.getLogger(AutoPushPolicy.class);
	
	private String filePath = null;
	private Properties properties;
	private Long newModified;
	private Long oldModified;
	private File propFile;
	
	
	/**
	 * Constructor Pass in the property file path. 
	 */
	public AutoPushPolicy(String file){
		filePath = file;
		properties = new Properties();
		propFile = Paths.get(filePath).toFile();
		readFile();
	}
	
	/**
	 * Checks Policy with all the Groups which has set such Property. 
	 * Else returns Empty Set. 
	 * 
	 * @param policyToCreateUpdate
	 * @param papEngine
	 */
	public Set<StdPDPGroup> checkGroupsToPush(String policyToCreateUpdate, PAPPolicyEngine papEngine) {
		Set<StdPDPGroup> changedGroups= new HashSet<>();
		// Check if the file has been modified. then re-load the properties file. 
		newModified = propFile.lastModified();
		try {
			if(newModified!=oldModified){
				// File has been updated.
				readFile();
			}
			// Read the File name as its made.
			String gitPath  = PolicyDBDao.getGitPath();
			String policyId =  policyToCreateUpdate.substring(policyToCreateUpdate.indexOf(gitPath)+gitPath.length()+1);
			String policyName = policyId.substring(policyId.lastIndexOf(File.separator)+1,policyId.lastIndexOf("."));
			policyName = policyName.substring(0,policyName.lastIndexOf("."));
			policyId = policyId.replace("/", ".");
			if(policyId.contains("\\")){
				policyId = policyId.replace("\\", ".");
			}
			LOGGER.info("Policy ID : " + policyId);
			LOGGER.info("Policy Name : " + policyName);
			// Read in Groups 
			for(EcompPDPGroup pdpGroup: papEngine.getEcompPDPGroups()){
				String groupName = pdpGroup.getName();
				Boolean typeFlag = false;
				Boolean scopeFlag = false;
				if(properties.containsKey(groupName + ".policyType")){
					String type= properties.getProperty(groupName + ".policyType").replaceAll(" ","");
					if(type.equals("")){
						type = " ";
					}
					typeFlag = policyName.contains(type);
				}
				if(properties.containsKey(groupName + ".policyScope")){
					String scope = properties.getProperty(groupName + ".policyScope").replaceAll(" ", "");
					if(scope.equals("")){
						scope = " ";
					}
					scopeFlag = policyId.contains(scope);
				}
				if(typeFlag || scopeFlag){
					StdPDPGroup group = addToGroup(policyId,policyName, policyToCreateUpdate, (StdPDPGroup)pdpGroup);
					changedGroups.add(group);
				}
			}
		} catch (Exception e) {
 			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "AutoPushPolicy", "Error while processing the auto push for " + policyToCreateUpdate);
		}
		return changedGroups;
	}
	
	private void readFile(){
		try {
			properties.load(new FileInputStream(propFile));
			oldModified = propFile.lastModified();
		} catch (Exception e) {
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "AutoPushPolicy", "Error while loading in the auto push properties file.");
		}
	}
	
	private StdPDPGroup addToGroup(String policyId, String policyName, String policyToCreateUpdate, StdPDPGroup pdpGroup) throws Exception{
		// Add to group. Send Notification. 
		StdPDPPolicy policy = new StdPDPPolicy(policyId, true, policyName, null);
		//Get the current policies from the Group and Add the new one
        Set<PDPPolicy> currentPoliciesInGroup = pdpGroup.getPolicies();
        Set<PDPPolicy> policies = new HashSet<>();
		policies.add(policy);
        pdpGroup.copyPolicyToFile(policyId, new FileInputStream(Paths.get(policyToCreateUpdate).toFile()));
        //If the selected policy is in the group we must remove it because the name is default
		Iterator<PDPPolicy> policyIterator = policies.iterator();
		while (policyIterator.hasNext()) {
			PDPPolicy selPolicy = policyIterator.next();
			for (PDPPolicy existingPolicy : currentPoliciesInGroup) {
				if (existingPolicy.getId().equals(selPolicy.getId())) {
					pdpGroup.removePolicyFromGroup(existingPolicy);
					LOGGER.debug("Removing policy: " + existingPolicy);
					break;
				}
			}
		}
		if(currentPoliciesInGroup!=null){
            policies.addAll(currentPoliciesInGroup);
        }
		pdpGroup.setPolicies(policies);
		return pdpGroup;
	}
}
