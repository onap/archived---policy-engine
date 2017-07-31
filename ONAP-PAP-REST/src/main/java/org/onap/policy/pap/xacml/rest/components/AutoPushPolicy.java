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

package org.onap.policy.pap.xacml.rest.components;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

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
