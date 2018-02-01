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

import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.api.PolicyType;
import org.w3c.dom.Document;

public class StdStatus extends StdPolicyResponse implements PolicyConfig, PolicyResponse, DecisionResponse{
	private PolicyType policyType;
	private Properties properties;
	private JsonObject jsonObject;
	private PolicyConfigType type;
	private Document document;
	private String other;
	private PolicyConfigStatus policyConfigStatus;
	private String configStatus;
	private PolicyResponseStatus policyResponseStatus; 
	private Map<String,String> actionAdvised;
	private Map<String,String> actionTaken;
	private Map<String,String> requestAttributes;
	private String policyResponseMessage; 
	private String policyName;
	private String policyVersion;
	private Map<String,String> matchingConditions;
	private Map<String,String> responseAttributes;
	private PolicyDecision policyDecision;
	private String details;
	
	public void setStatus(String message, PolicyResponseStatus policyResponseStatus, PolicyConfigStatus policyConfigStatus) {
		this.configStatus = message; 
		this.policyResponseMessage = message;
		this.policyResponseStatus = policyResponseStatus;
		this.policyConfigStatus = policyConfigStatus;
	}
	@Override
	public PolicyResponseStatus getPolicyResponseStatus() {
		return policyResponseStatus;
	}

	@Override
	public Map<String, String> getActionAdvised() {
		return actionAdvised;
	}
	
	@Override
	public Map<String, String> getActionTaken() {
		return actionTaken;
	}

	@Override
	public Map<String, String> getRequestAttributes() {
		return requestAttributes;
	}

	@Override
	public String getPolicyResponseMessage() {
		return policyResponseMessage;
	}

	@Override
	public void setPolicyResponseStatus(PolicyResponseStatus policyResponseStatus) {
		this.policyResponseStatus = policyResponseStatus;
	}
	
	@Override
	public void setActionAdvised(Map<String, String> actionAdvised) {
		this.actionAdvised = actionAdvised;
	}

	@Override
	public void setActionTaken(Map<String, String> actionTaken) {
		this.actionTaken = actionTaken;
	}

	@Override
	public void setRequestAttributes(Map<String, String> requestAttributes) {
		this.requestAttributes = requestAttributes;
	}

	@Override
	public void setPolicyResponseMessage(String policyResponseMessage) {
		this.policyResponseMessage = policyResponseMessage;
	}
	
	@Override
	public void setPolicyResponseStatus(String policyResponseMessage, PolicyResponseStatus policyResponseStatus) {
		this.policyResponseMessage = policyResponseMessage;
		this.policyResponseStatus = policyResponseStatus;
	}
	
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

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public void setConfigStatus(String configStatus) {
		this.configStatus = configStatus;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	public void setPolicyVersion(String policyVersion) {
		this.policyVersion = policyVersion;
	}
	
	public void setMatchingConditions(Map<String,String> matchingConditions){
		this.matchingConditions = matchingConditions;
	}
	
	public void setResposneAttributes(Map<String,String> responseAttributes){
		this.responseAttributes = responseAttributes;
	}
	
	public void setPolicyConfigStatus(PolicyConfigStatus policyConfigStatus) {
		this.policyConfigStatus = policyConfigStatus;
	}
	
	public void setPolicyConfigStatus(String configStatus, PolicyConfigStatus policyConfigStatus) {
		this.policyConfigStatus = policyConfigStatus;
		this.configStatus = configStatus;
	}
	
	@Override
	public String toOther() {
		return other;
	}
	
	public void setOther(String other) {
		this.other = other;
	}
	
	public PolicyDecision getDecision() {
		return policyDecision;
	}
	public void setDecision(PolicyDecision policyDecision) {
		this.policyDecision = policyDecision;
	}
	
	public void setDetails(String details){
		this.details = details;
	}
	
	public String getDetails(){
		return details;
	}
	
    public PolicyConfigType getPolicyType() {
        return type;
    }
    
    public void setPolicyType(PolicyConfigType policyType) {
        this.type = policyType;
    }
}
