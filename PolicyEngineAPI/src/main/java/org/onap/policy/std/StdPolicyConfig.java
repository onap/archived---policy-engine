/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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


package org.onap.policy.std;

import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyType;
import org.w3c.dom.Document;

/**
 * PolicyConfig Implementation.
 *
 * @version 0.3
 *
 */

public class StdPolicyConfig implements PolicyConfig{
	private PolicyType policyType;
	private Properties properties;
	private JsonObject jsonObject;
	private Document document;
	private String other;
	private PolicyConfigStatus policyConfigStatus;
	private String configStatus;
	private String policyName;
	private String policyVersion;
	private PolicyConfigType type;
	private Map<String,String> matchingConditions;
	private Map<String,String> responseAttributes;

	@Override
	public PolicyType getType() {
		return policyType;
	}

	@Override
	public Properties toProperties() {
		return properties;
	}

	@Override
	public JsonObject toJSON() {
		return jsonObject;
	}

	@Override
	public Document toXML() {
		return document;
	}

	@Override
	public String toOther() {
		return other;
	}

	@Override
	public PolicyConfigStatus getPolicyConfigStatus() {
		return policyConfigStatus;
	}

	@Override
	public String getPolicyConfigMessage() {
		return configStatus;
	}

	@Override
	public String getPolicyName() {
		if(policyName!=null && policyName.contains(".xml")){
			return (policyName.substring(0, policyName.substring(0, policyName.lastIndexOf('.')).lastIndexOf('.')));
		}
		return policyName;
	}

	@Override
	public String getPolicyVersion() {
		return policyVersion;
	}

	@Override
	public Map<String,String> getMatchingConditions(){
		return matchingConditions;
	}

	@Override
	public Map<String,String> getResponseAttributes(){
		return responseAttributes;
	}

	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public void setPolicyVersion(String policyVersion) {
		this.policyVersion = policyVersion;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public void setConfigStatus(String configStatus) {
		this.configStatus = configStatus;
	}

	public void setPolicyConfigStatus(PolicyConfigStatus policyConfigStatus) {
		this.policyConfigStatus = policyConfigStatus;
	}

	public void setConfigStatus(String configStatus, PolicyConfigStatus policyConfigStatus) {
		this.configStatus = configStatus;
		this.policyConfigStatus = policyConfigStatus;
	}

	public void setMatchingConditions(Map<String,String> matchingConditions){
		this.matchingConditions = matchingConditions;
	}

	public void setResponseAttributes(Map<String,String> responseAttributes){
		this.responseAttributes = responseAttributes;
	}


	public void setPolicyType(PolicyConfigType policyType) {
        this.type = policyType;
    }

	@Override
	public PolicyConfigType getPolicyType(){
	    return this.type;
	}

    @Override
	public String toString() {
		return "PolicyConfig [ policyConfigStatus=" + policyConfigStatus + ", policyConfigMessage=" + configStatus + ", policyName=" + policyName +
				""
				+ "]";
	}

}
