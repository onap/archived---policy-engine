/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP
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
package org.openecomp.policy.xacml.pdp.std.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 
/**
 * Creates a list of policy ids.
 * 
 * @version $Revision: 1.3 $
 */
public class PolicyList {
	
//	private static Map<String, Integer> policyMap = new HashMap<>();
	private static List<String> policyList = new ArrayList<String>();
	private Logger logger	= FlexLogger.getLogger(this.getClass());
	
	
	public static List<String> getpolicyList(){
		return policyList;
	}

	public static void addPolicyID(String policyID){
		if (!policyList.contains(policyID)){
			policyList.add(policyID);
		}
	//	policyMap.put(policyID, count);
	}
	
	public static void clearPolicyList(){
	//	policyMap.clear();
		if (!policyList.isEmpty()){
			policyList.clear();
		}
	}
}
