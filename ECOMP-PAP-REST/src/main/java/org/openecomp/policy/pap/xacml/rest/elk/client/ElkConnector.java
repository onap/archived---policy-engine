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
package org.openecomp.policy.pap.xacml.rest.elk.client;


import java.util.ArrayList;

import org.openecomp.policy.rest.adapter.PolicyRestAdapter;

import io.searchbox.client.JestResult;

public interface ElkConnector {
	
	public static final String ELK_URL = "http://localhost:9200";
	public static final String ELK_INDEX_POLICY = "policy";
	
	public enum PolicyIndexType {
		config,
		action,
		decision,
		closedloop,
		all,
	}
	
	public enum PolicyType {
		Config,
		Action,
		Decision,
		Config_Fault,
		Config_PM,
		Config_FW,
		Config_MS,
		none,
	}
	
	public enum PolicyBodyType {
		json,
		xml,
		properties,
		txt,
		none,
	}

	public JestResult policy(String policyId) 
		   throws IllegalStateException, IllegalArgumentException;
	
	public boolean delete(PolicyRestAdapter policyData)
			throws IllegalStateException;

	public ArrayList<PolicyLocator> policyLocators(PolicyIndexType type, String text,int connector)
		   throws IllegalStateException, IllegalArgumentException;
	
	public ArrayList<PolicyLocator> policyLocators(PolicyIndexType type, String text, 
			                                       ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s,int connector)
		   throws IllegalStateException, IllegalArgumentException;

	public JestResult search(PolicyIndexType type, String text) 
		   throws IllegalStateException, IllegalArgumentException;
	
	public JestResult search(PolicyIndexType type, String text, 
			                 ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s) 
			   throws IllegalStateException, IllegalArgumentException;
	
	public boolean update(PolicyRestAdapter policyData) throws IllegalStateException;
	
	public ElkConnector singleton = new ElkConnectorImpl();
	
	public static PolicyIndexType toPolicyIndexType(PolicyType type) 
	       throws IllegalArgumentException {
		if (type == null)
			throw new IllegalArgumentException("Unsupported NULL type conversion");
		
		switch(type) {
		case Config:
			return PolicyIndexType.config;
		case Action:
			return PolicyIndexType.action;
		case Decision:
			return PolicyIndexType.decision;
		case Config_Fault:
			return PolicyIndexType.closedloop;
		case Config_PM:
			return PolicyIndexType.closedloop;
		case Config_FW:
			return PolicyIndexType.config;
		case Config_MS:
			return PolicyIndexType.config;
		case none:
			return PolicyIndexType.all;
		default:
			throw new IllegalArgumentException("Unsupported type conversion to index: " + type.name());
		}
	}
	
	public static PolicyIndexType toPolicyIndexType(String policyName) 
		       throws IllegalArgumentException {
			if (policyName == null)
				throw new IllegalArgumentException("Unsupported NULL policy name conversion");
			
			if (policyName.startsWith("Config_Fault")) {
				return PolicyIndexType.closedloop;
			} else if (policyName.startsWith("Config_PM")) {
				return PolicyIndexType.closedloop;
			} else if (policyName.startsWith("Config_FW")) {
				return PolicyIndexType.config;
			} else if (policyName.startsWith("Config_MS")) {
				return PolicyIndexType.config;
			}else if (policyName.startsWith("Action")) {
				return PolicyIndexType.action;
			} else if (policyName.startsWith("Decision")) {
				return PolicyIndexType.decision;
			} else if (policyName.startsWith("Config")) {
				return PolicyIndexType.config;
			} else {
				throw new IllegalArgumentException
							("Unsupported policy name conversion to index: " + 
				             policyName);
			}
	}
	
	public static PolicyType toPolicyType(String policyName) 
		       throws IllegalArgumentException {
		if (policyName == null)
			throw new IllegalArgumentException("Unsupported NULL policy name conversion to Policy Type");
		
		if (policyName.startsWith("Config_Fault")) {
			return PolicyType.Config_Fault;
		} else if (policyName.startsWith("Config_PM")) {
			return PolicyType.Config_PM;
		} else if (policyName.startsWith("Config_FW")) {
			return PolicyType.Config_FW;
		} else if (policyName.startsWith("Config_MS")) {
			return PolicyType.Config_MS;
		}else if (policyName.startsWith("Action")) {
			return PolicyType.Action;
		} else if (policyName.startsWith("Decision")) {
			return PolicyType.Decision;
		} else if (policyName.startsWith("Config")) {
			return PolicyType.Config;
		} else {
			throw new IllegalArgumentException
						("Unsupported policy name conversion to index: " + 
			             policyName);
		}
	}
	
}
