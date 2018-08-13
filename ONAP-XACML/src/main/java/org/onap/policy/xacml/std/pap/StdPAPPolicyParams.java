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

import java.util.List;
import java.util.Map;

public class StdPAPPolicyParams {
    private String configPolicyType;
    private String policyName;
    private String description;
    private String onapName;
    private String configName;
    private Map<String, String> dyanamicFieldConfigAttributes;
    private String configType;
    private String configBodyData;
    private Boolean editPolicy;
    private String domain;
    private String riskLevel;
    private String riskType;
    private String guard;
    private String ttlDate;
    private int highestVersion;
    private List<String> dynamicRuleAlgorithmLabels;
    private List<String> dynamicRuleAlgorithmCombo;
    private List<String> dynamicRuleAlgorithmField1;
    private List<String> dynamicRuleAlgorithmField2;
    private String actionPerformer;
    private String actionAttribute;

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

    public Map<String, String> getDyanamicFieldConfigAttributes() {
        return dyanamicFieldConfigAttributes;
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

    public List<String> getDynamicRuleAlgorithmLabels() {
        return dynamicRuleAlgorithmLabels;
    }

    public List<String> getDynamicRuleAlgorithmCombo() {
        return dynamicRuleAlgorithmCombo;
    }

    public List<String> getDynamicRuleAlgorithmField1() {
        return dynamicRuleAlgorithmField1;
    }

    public List<String> getDynamicRuleAlgorithmField2() {
        return dynamicRuleAlgorithmField2;
    }

    public String getActionPerformer() {
        return actionPerformer;
    }

    public String getActionAttribute() {
        return actionAttribute;
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

        public StdPAPPolicyParamsBuilder dyanamicFieldConfigAttributes(Map<String, String> attributes) {
            m.dyanamicFieldConfigAttributes = attributes;
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

        public StdPAPPolicyParamsBuilder dynamicRuleAlgorithmLabels(List<String> dynamicRuleAlgorithmLabels) {
            m.dynamicRuleAlgorithmLabels = dynamicRuleAlgorithmLabels;
            return this;
        }

        public StdPAPPolicyParamsBuilder dynamicRuleAlgorithmCombo(List<String> dynamicRuleAlgorithmCombo) {
            m.dynamicRuleAlgorithmCombo = dynamicRuleAlgorithmCombo;
            return this;
        }

        public StdPAPPolicyParamsBuilder dynamicRuleAlgorithmField1(List<String> dynamicRuleAlgorithmField1) {
            m.dynamicRuleAlgorithmField1 = dynamicRuleAlgorithmField1;
            return this;
        }

        public StdPAPPolicyParamsBuilder dynamicRuleAlgorithmField2(List<String> dynamicRuleAlgorithmField2) {
            m.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
            return this;
        }

        public StdPAPPolicyParamsBuilder actionPerformer(String actionPerformer) {
            m.actionPerformer = actionPerformer;
            return this;
        }

        public StdPAPPolicyParamsBuilder actionAttribute(String actionAttribute) {
            m.actionAttribute = actionAttribute;
            return this;
        }
    }
}
