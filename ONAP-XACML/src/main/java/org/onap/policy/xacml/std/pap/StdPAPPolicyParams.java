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
    private String configPolicyType;
    private String policyName;
    private String description;
    private String onapName;
    private String configName;
    private Map<String, String> attributes;
    private String configType;
    private String configBodyData;
    private Boolean editPolicy;
    private String domain;
    private String riskLevel;
    private String riskType;
    private String guard;
    private String ttlDate;
    private int highestVersion;

    private StdPAPPolicyParams() {
        super();
    }

    public int getHighestVersion() {
        return highestVersion;
    }

    public static StdPAPPolicyParamsBuilder builder() {
        return new StdPAPPolicyParamsBuilder();
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

    public String getConfigBodyData() {
        return configBodyData;
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

    public static class StdPAPPolicyParamsBuilder {
        StdPAPPolicyParams m = new StdPAPPolicyParams();

        public StdPAPPolicyParams build() {
            return m;
        }

        public StdPAPPolicyParamsBuilder configPolicyType(String configPolicyType) {
            m.configPolicyType = configPolicyType;
            return this;
        }


        public StdPAPPolicyParamsBuilder policyName(String policyName) {
            m.policyName = policyName;
            return this;
        }

        public StdPAPPolicyParamsBuilder description(String description) {
            m.description = description;
            return this;
        }

        public StdPAPPolicyParamsBuilder onapName(String onapName) {
            m.onapName = onapName;
            return this;
        }

        public StdPAPPolicyParamsBuilder configName(String configName) {
            m.configName = configName;
            return this;
        }

        public StdPAPPolicyParamsBuilder attributes(Map<String, String> attributes) {
            m.attributes = attributes;
            return this;
        }

        public StdPAPPolicyParamsBuilder configType(String configType) {
            m.configType = configType;
            return this;
        }

        public StdPAPPolicyParamsBuilder configBodyData(String body) {
            m.configBodyData = body;
            return this;
        }

        public StdPAPPolicyParamsBuilder editPolicy(boolean editPolicy) {
            m.editPolicy = editPolicy;
            return this;
        }

        public StdPAPPolicyParamsBuilder domain(String domain) {
            m.domain = domain;
            return this;
        }

        public StdPAPPolicyParamsBuilder riskLevel(String riskLevel) {
            m.riskLevel = riskLevel;
            return this;
        }

        public StdPAPPolicyParamsBuilder riskType(String riskType) {
            m.riskType = riskType;
            return this;
        }

        public StdPAPPolicyParamsBuilder guard(String guard) {
            m.guard = guard;
            return this;
        }

        public StdPAPPolicyParamsBuilder ttlDate(String ttlDate) {
            m.ttlDate = ttlDate;
            return this;
        }

        public StdPAPPolicyParamsBuilder highestVersion(int highVer) {
            m.highestVersion = highVer;
            return this;
        }
    }
}
