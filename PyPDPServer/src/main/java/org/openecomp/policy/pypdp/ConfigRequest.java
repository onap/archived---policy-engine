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

package org.openecomp.policy.pypdp;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.json.JsonObject;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyType;
import org.openecomp.policy.pypdp.model_pojo.PepConfigPolicyNameRequest;
import org.openecomp.policy.pypdp.model_pojo.PyPolicyConfig;
import org.openecomp.policy.std.StdPolicyConfig;
import org.openecomp.policy.std.StdPolicyEngine;
import org.w3c.dom.Document;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.eelf.PolicyLogger;

public class ConfigRequest {
	
	private StdPolicyEngine pe;
	public ConfigRequest(StdPolicyEngine pe){
		this.pe= pe;
	}
	
	public Collection<PyPolicyConfig> run(ConfigRequestParameters pep, String requestID, String userID, String passcode) {
		PolicyLogger.debug("... Request Params : \n"
				+ "configName " + pep.getConfigName() + "\n"
				+ "ecompName"  + pep.getEcompName() + "\n"
				+ "policyName" + pep.getPolicyName() + "\n");
		StdPolicyConfig policyConfig = new StdPolicyConfig();
		Collection<PyPolicyConfig> result = new ArrayList<PyPolicyConfig>();
		// construct a UUID from the request string
		if(pep.getRequestID()==null){
			UUID requestUUID = null;
			if (requestID != null && !requestID.isEmpty()) {
				try {
					requestUUID = UUID.fromString(requestID);
				}
				catch (IllegalArgumentException e) {
					requestUUID = UUID.randomUUID();
					PolicyLogger.info("Generated Random UUID: " + requestUUID.toString());
				}
			}
			pep.setRequestID(requestUUID);
		}
		try {
			PolicyLogger.debug("\n\n calling PEP.. ");
			Collection<PolicyConfig> pConfigs = pe.configRequest(pep, userID, passcode);
			for(PolicyConfig pConfig: pConfigs){
				PyPolicyConfig pyPolicyConfig = checkResponse(pConfig);
				result.add(pyPolicyConfig);
			}
			return result;
		} catch(Exception e){
			policyConfig.setConfigStatus(e.getMessage(), PolicyConfigStatus.CONFIG_NOT_FOUND);
			PyPolicyConfig pyPolicyConfig = checkResponse(policyConfig);
			result.add(pyPolicyConfig);
			return result;
		}
	}
	
	public Collection<PyPolicyConfig> run(PepConfigPolicyNameRequest pep, String requestID, String userID, String passcode) {
		PolicyLogger.debug("... Request Params : \n"
				+ "policyName" + pep.getPolicyName() + "\n");
		StdPolicyConfig policyConfig = new StdPolicyConfig();
		Collection<PyPolicyConfig> result = new ArrayList<PyPolicyConfig>();
		// construct a UUID from the request string
		UUID requestUUID = null;
		if (requestID != null && !requestID.isEmpty()) {
			try {
				requestUUID = UUID.fromString(requestID);
			}
			catch (IllegalArgumentException e) {
				requestUUID = UUID.randomUUID();
				PolicyLogger.info("Generated Random UUID: " + requestUUID.toString());
			}
		}
		if(pep.getPolicyName()!= null && !pep.getPolicyName().isEmpty()) {
			try {
				Collection<PolicyConfig> pConfigs = pe.configPolicyName(pep.getPolicyName(), requestUUID, userID, passcode);
				for(PolicyConfig pConfig: pConfigs){
					PyPolicyConfig pyPolicyConfig = checkResponse(pConfig);
					result.add(pyPolicyConfig);
				}
				return result;
			} catch (PolicyConfigException e) {
				policyConfig.setConfigStatus(e.getMessage(), PolicyConfigStatus.CONFIG_NOT_FOUND);
				PyPolicyConfig pyPolicyConfig = checkResponse(policyConfig);
				result.add(pyPolicyConfig);
				return result;
			}
		}
		else {
			policyConfig.setConfigStatus(XACMLErrorConstants.ERROR_DATA_ISSUE + "PolicyFile Name is empty", PolicyConfigStatus.CONFIG_NOT_FOUND);
			PyPolicyConfig pyPolicyConfig = checkResponse(policyConfig);
			result.add(pyPolicyConfig);
			return result;
		}
	}

	public PyPolicyConfig checkResponse(PolicyConfig pConfig) {
		PyPolicyConfig policyConfig = new PyPolicyConfig();
		policyConfig.setPolicyConfigMessage(pConfig.getPolicyConfigMessage());
		policyConfig.setPolicyConfigStatus(pConfig.getPolicyConfigStatus());
		policyConfig.setType(pConfig.getType());
		policyConfig.setPolicyName(pConfig.getPolicyName());
		policyConfig.setMatchingConditions(pConfig.getMatchingConditions());
		policyConfig.setResponseAttributes(pConfig.getResponseAttributes());
		policyConfig.setPolicyVersion(pConfig.getPolicyVersion());
		if (pConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_RETRIEVED)) {
			PolicyType policyType = policyConfig.getType();
			if(policyType.equals(PolicyType.PROPERTIES)) {
				Properties properties = pConfig.toProperties();
				Map<String, String> propVal = new HashMap<String, String>();
				for(String name: properties.stringPropertyNames()) {
					propVal.put(name, properties.getProperty(name));
				}
				policyConfig.setProperty(propVal);
			} else if(policyType.equals(PolicyType.OTHER)) {
				String other = pConfig.toOther();
				policyConfig.setConfig(other);
			} else if (policyType.equals(PolicyType.JSON)) {
				JsonObject json = pConfig.toJSON();
				policyConfig.setConfig(json.toString());
			} else if (policyType.equals(PolicyType.XML)) {
				Document document = pConfig.toXML();
				DOMSource domSource = new DOMSource(document);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = tf.newTransformer();
					transformer.transform(domSource, result);
					policyConfig.setConfig(writer.toString());
				} catch (TransformerException e) {
					policyConfig.setConfig(null);
					policyConfig.setPolicyConfigMessage(XACMLErrorConstants.ERROR_SCHEMA_INVALID + "XML error in the Configuration. " + e.getMessage());
					policyConfig.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_NOT_FOUND);
				}
			}
		} else {
			policyConfig.setConfig(null);
		}
		return policyConfig;
	}
}
