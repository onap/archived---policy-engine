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

import java.io.Serializable;

@ApiModel
public class ConfigFirewallPolicyAPIRequest implements Serializable{
    private static final long serialVersionUID = -7460640390070215401L;

    private String policyName = null;
    private String policyScope = null;
    private String firewallJson = null;
    private String riskType = "default";
    private String riskLevel = "5";
    private String guard = "false";
    private String ttlDate = null;

    public String getPolicyName() {
        return policyName;
    }
    public String getPolicyScope() {
        return policyScope;
    }
    public String getFirewallJson() {
        return firewallJson;
    }
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    public void setPolicyScope(String policyScope) {
        this.policyScope = policyScope;
    }
    public void setFirewallJson(String firewallJson) {
        this.firewallJson = firewallJson;
    }
    public String getRiskType() {
        return riskType;
    }
    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }
    public String getRiskLevel() {
        return riskLevel;
    }
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    public String getGuard() {
        return guard;
    }
    public void setGuard(String guard) {
        this.guard = guard;
    }
    public String getTtlDate() {
        return ttlDate;
    }
    public void setTtlDate(String ttlDate) {
        this.ttlDate = ttlDate;
    }
}