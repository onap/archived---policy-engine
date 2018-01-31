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

import java.io.Serializable;
import java.util.Map;

import io.swagger.annotations.ApiModel;

@ApiModel
public class ConfigPolicyAPIRequest implements Serializable{
    private static final long serialVersionUID = -4103391389984557025L;

    private String policyScope = null;
    private String policyName = null;
    private String policyDescription = null;
    private String onapName = null;
    private String configName = null;
    private Map<String,String> configAttributes = null;
    private String configType = null;
    private String body = null;
    private String riskType = "default";
    private String riskLevel = "5";
    private String guard = "false";
    private String ttlDate = null;

    /**
     * @return the policyScope
     */
    public String getPolicyScope() {
        return policyScope;
    }
    /**
     * @return the policyName
     */
    public String getPolicyName() {
        return policyName;
    }
    /**
     * @return the policyDescription
     */
    public String getPolicyDescription() {
        return policyDescription;
    }
    /**
     * @return the onapName
     */
    public String getOnapName() {
        return onapName;
    }
    /**
     * @return the onapName
     */
    @Deprecated
    public String getEcompName() {
        return onapName;
    }
    /**
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }
    /**
     * @return the configAttributes
     */
    public Map<String, String> getConfigAttributes() {
        return configAttributes;
    }
    /**
     * @return the configType
     */
    public String getConfigType() {
        return configType;
    }
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
    /**
     * @param policyScope the policyScope to set
     */
    public void setPolicyScope(String policyScope) {
        this.policyScope = policyScope;
    }
    /**
     * @param policyName the policyName to set
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    /**
     * @param policyDescription the policyDescription to set
     */
    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }
    /**
     * @param onapName the onapName to set
     */
    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }
    /**
     * @param ecompName the onapName to set
     */
    @Deprecated
    public void setEcompName(String ecompName) {
        this.onapName = ecompName;
    }
    /**
     * @param configName the configName to set
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    /**
     * @param configAttributes the configAttributes to set
     */
    public void setConfigAttributes(Map<String, String> configAttributes) {
        this.configAttributes = configAttributes;
    }
    /**
     * @param configType the configType to set
     */
    public void setConfigType(String configType) {
        this.configType = configType;
    }
    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }
    /**
     * @return the guard
     */
    public String getGuard() {
        return guard;
    }
    /**
     * @param guard the guard to set
     */
    public void setGuard(String guard) {
        this.guard = guard;
    }
    /**
     * @return the riskLevel
     */
    public String getRiskLevel() {
        return riskLevel;
    }
    /**
     * @param riskLevel the riskLevel to set
     */
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    /**
     * @return the ttlDate
     */
    public String getTtlDate() {
        return ttlDate;
    }
    /**
     * @param ttlDate the ttlDate to set
     */
    public void setTtlDate(String ttlDate) {
        this.ttlDate = ttlDate;
    }
    /**
     * @return the riskType
     */
    public String getRiskType() {
        return riskType;
    }
    /**
     * @param riskType the riskType to set
     */
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
}
