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

import io.swagger.annotations.ApiModel;

import java.util.Map;

import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyType;
import org.onap.policy.models.APIConfigResponse;

@ApiModel
public class PolicyConfig implements APIConfigResponse{
    private String policyConfigMessage;
    private PolicyConfigStatus policyConfigStatus;
    private PolicyType type;
    private String config;
    private String policyName;
    private String policyVersion;
    private Map<String, String> matchingConditions;
    private Map<String, String> responseAttributes;
    private Map<String, String> property;
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
}
