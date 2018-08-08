/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
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

package org.onap.policy.xacml.std.pap;

import java.util.Map;

public class StdPAPPolicyParams {
    private final String configPolicyType;
    private final String policyName;
    private final String description;
    private final String onapName;
    private final String configName;
    private final Map<String, String> attributes;
    private final String configType;
    private final String body;
    private final Boolean editPolicy;
    private final String domain;
    private final String riskLevel;
    private final String riskType;
    private final String guard;
    private final String ttlDate;

    public StdPAPPolicyParams(String configPolicyType, String policyName, String description,
                              String onapName, String configName, Map<String, String> attributes,
                              String configType, String body, Boolean editPolicy, String domain,
                              String riskLevel, String riskType, String guard, String ttlDate) {
        this.configPolicyType = configPolicyType;
        this.policyName = policyName;
        this.description = description;
        this.onapName = onapName;
        this.configName = configName;
        this.attributes = attributes;
        this.configType = configType;
        this.body = body;
        this.editPolicy = editPolicy;
        this.domain = domain;
        this.riskLevel = riskLevel;
        this.riskType = riskType;
        this.guard = guard;
        this.ttlDate = ttlDate;
    }

    public String getConfigPolicyType() {
        return configPolicyType;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getDescription() {
        return description;
    }

    public String getOnapName() {
        return onapName;
    }

    public String getConfigName() {
        return configName;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getConfigType() {
        return configType;
    }

    public String getBody() {
        return body;
    }

    public Boolean getEditPolicy() {
        return editPolicy;
    }

    public String getDomain() {
        return domain;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRiskType() {
        return riskType;
    }

    public String getGuard() {
        return guard;
    }

    public String getTtlDate() {
        return ttlDate;
    }
}
