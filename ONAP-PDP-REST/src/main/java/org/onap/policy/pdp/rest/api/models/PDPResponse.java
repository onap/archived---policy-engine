/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.models;

import java.util.Map;

import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.api.PolicyType;
import org.onap.policy.models.APIConfigResponse;

public class PDPResponse implements APIConfigResponse, PolicyResponse, DecisionResponse{
    private String policyConfigMessage;
    private PolicyConfigStatus policyConfigStatus;
    private PolicyType type;
    private String config;
    private String policyName;
    private String policyVersion;
    private Map<String, String> matchingConditions;
    private Map<String, String> responseAttributes;
    private Map<String, String> property;
    private PolicyResponseStatus policyResponseStatus;
    private String policyResponseMessage;
    private Map<String,String> actionAdvised;
    private Map<String,String> actionTaken;
    private Map<String,String> requestAttributes;
    private PolicyDecision policyDecision;
    private String details;

    public void setStatus(String message, PolicyResponseStatus policyResponseStatus, PolicyConfigStatus policyConfigStatus) {
        this.policyConfigMessage = message;
        this.policyResponseMessage = message;
        this.policyResponseStatus = policyResponseStatus;
        this.policyConfigStatus = policyConfigStatus;
    }
    public String getConfig() {
        return config;
    }
    public void setConfig(String config) {
        this.config = config;
    }
    public PolicyType getType() {
        return type;
    }
    public void setType(PolicyType type) {
        this.type = type;
    }
    public PolicyConfigStatus getPolicyConfigStatus() {
        return policyConfigStatus;
    }
    public void setPolicyConfigStatus(PolicyConfigStatus policyConfigStatus) {
        this.policyConfigStatus = policyConfigStatus;
    }
    public String getPolicyConfigMessage() {
        return policyConfigMessage;
    }
    public void setPolicyConfigMessage(String policyConfigMessage) {
        this.policyConfigMessage = policyConfigMessage;
    }
    public Map<String, String> getProperty() {
        return property;
    }
    public void setProperty(Map<String, String> property) {
        this.property = property;
    }
    public String getPolicyName(){
        return policyName;
    }
    public void setPolicyName(String policyName){
        this.policyName = policyName;
    }
    public String getPolicyVersion(){
        return policyVersion;
    }
    public void setPolicyVersion(String policyVersion){
        this.policyVersion = policyVersion;
    }
    public Map<String, String> getMatchingConditions(){
        return matchingConditions;
    }
    public void setMatchingConditions(Map<String, String> matchingConditions){
        this.matchingConditions = matchingConditions;
    }
    public void setResponseAttributes(Map<String,String> responseAttributes){
        this.responseAttributes = responseAttributes;
    }
    public Map<String,String> getResponseAttributes(){
        return responseAttributes;
    }
    public void setPolicyResponseStatus(PolicyResponseStatus policyResponseStatus) {
        this.policyResponseStatus = policyResponseStatus;
    }
    @Override
    public PolicyResponseStatus getPolicyResponseStatus() {
        return policyResponseStatus;
    }
    public void setDecision(PolicyDecision policyDecision){
        this.policyDecision = policyDecision;
    }
    @Override
    public PolicyDecision getDecision() {
        return policyDecision;
    }
    public void setDetails(String details){
        this.details = details;
    }
    @Override
    public String getDetails() {
        return details;
    }
    public void setActionAdvised(Map<String, String> actionAdvised) {
        this.actionAdvised = actionAdvised;
    }
    @Override
    public Map<String, String> getActionAdvised() {
        return actionAdvised;
    }
    public void setActionTaken(Map<String, String> actionTaken) {
        this.actionTaken = actionTaken;
    }
    @Override
    public Map<String, String> getActionTaken() {
        return actionTaken;
    }
    public void setRequestAttributes(Map<String, String> requestAttributes) {
        this.requestAttributes = requestAttributes;
    }
    @Override
    public Map<String, String> getRequestAttributes() {
        return requestAttributes;
    }
    public void setPolicyResponseMessage(String policyResponseMessage) {
        this.policyResponseMessage = policyResponseMessage;
    }
    @Override
    public String getPolicyResponseMessage() {
        return policyResponseMessage;
    }
}
