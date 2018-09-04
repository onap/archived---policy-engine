/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2018 Samsung Electronics Co., Ltd. All rights reserved.
 * Modifications Copyright (C) 2018 AT&T Intellectual Property.
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

/**
 * Std PAP Policy paramters class.
 */
public class StdPAPPolicyParams {
    private String configPolicyType;
    private String policyName;
    private String description;
    private String onapName;
    private String configName;
    private Map<String, String> dynamicFieldConfigAttributes;
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
    private String providerComboBox;
    private Map<String, String> dynamicSettingsMap;
    private List<Object> dynamicVariableList;
    private List<String> dataTypeList;
    private Map<String, String> dropDownMap;
    private Map<String,String> treatments;
    private String policyID;
    private String ruleID;
    private String version;
    private String jsonBody;
    private String brmsController;
    private List<String> brmsDependency;
    private Map<String,String> drlRuleAndUIParams;
    private boolean draft;
    private String oldPolicyFileName;
    private String serviceType;
    private String uuid;
    private String msLocation;
    private String priority;
    private String deleteCondition;
    private String dictionaryType;
    private String dictionary;
    private String dictionaryFields;
    private String rawXacmlPolicy = null;

    /**

     * Default constructor
     */
    private StdPAPPolicyParams() {
        super();
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getOldPolicyFileName() {
        return oldPolicyFileName;
    }

    public boolean isDraft() {
        return draft;
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

    public Map<String, String> getDynamicFieldConfigAttributes() {
        return dynamicFieldConfigAttributes;
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

    public String getProviderComboBox() {
        return providerComboBox;
    }

    public Map<String,String> getDynamicSettingsMap() {
        return dynamicSettingsMap;
    }

    public List<Object> getDynamicVariableList() {
        return dynamicVariableList;
    }

    public List<String> getDataTypeList() {
        return dataTypeList;
    }

    public Map<String,String> getDropDownMap() {
        return dropDownMap;
    }

    public Map<String,String> getTreatments() {
        return treatments;
    }

    public String getPolicyID() {
        return policyID;
    }

    public String getRuleID() {
        return ruleID;
    }

    public String getVersion() {
        return version;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public String getBrmsController() {
        return brmsController;
    }

    public List<String> getBrmsDependency() {
        return brmsDependency;
    }

    public Map<String,String> getDrlRuleAndUIParams() {
        return drlRuleAndUIParams;
    }

    public String getUuid() {
        return uuid;
    }

    public String getMsLocation() {
        return msLocation;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeleteCondition() {
        return deleteCondition;
    }

    public String getDictionaryType() {
        return dictionaryType;
    }

    public String getDictionary() {
        return dictionary;
    }

    public String getDictionaryFields() {
        return dictionaryFields;
    }

    public String getRawXacmlPolicy() {
        return rawXacmlPolicy;
    }
    /**
     * Builder class for the Policy parameters
     */
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

        public StdPAPPolicyParamsBuilder dynamicFieldConfigAttributes(Map<String, String> attributes) {
            m.dynamicFieldConfigAttributes = attributes;
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

        public StdPAPPolicyParamsBuilder providerComboBox(String providerComboBox) {
            m.providerComboBox = providerComboBox;
            return this;
        }

        public StdPAPPolicyParamsBuilder dynamicVariableList(List<Object> dynamicVariableList) {
            m.dynamicVariableList = dynamicVariableList;
            return this;
        }

        public StdPAPPolicyParamsBuilder dynamicSettingsMap(Map<String, String> dynamicSettingsMap) {
            m.dynamicSettingsMap = dynamicSettingsMap;
            return this;
        }

        public StdPAPPolicyParamsBuilder dataTypeList(List<String> dataTypeList) {
            m.dataTypeList = dataTypeList;
            return this;
        }

        public StdPAPPolicyParamsBuilder dropDownMap(Map<String, String> dropDownMap){
            m.dropDownMap = dropDownMap;
            return this;
        }

        public StdPAPPolicyParamsBuilder treatments(Map<String,String> treatments) {
            m.treatments = treatments;
            return this;
        }

        public StdPAPPolicyParamsBuilder policyID(String policyID) {
            m.policyID = policyID;
            return this;
        }

        public StdPAPPolicyParamsBuilder ruleID(String ruleID) {
            m.ruleID = ruleID;
            return this;
        }

        public StdPAPPolicyParamsBuilder version(String version) {
            m.version = version;
            return this;
        }

        public StdPAPPolicyParamsBuilder jsonBody(String jsonBody) {
            m.jsonBody = jsonBody;
            return this;
        }

        public StdPAPPolicyParamsBuilder brmsController(String controllerName) {
            m.brmsController = controllerName;
            return this;
        }

        public StdPAPPolicyParamsBuilder brmsDependency(List<String> dependencyNames) {
            m.brmsDependency = dependencyNames;
            return this;
        }

        public StdPAPPolicyParamsBuilder drlRuleAndUIParams(Map<String,String> drlRuleAndUIParams) {
            m.drlRuleAndUIParams = drlRuleAndUIParams;
            return this;
        }

        public StdPAPPolicyParamsBuilder draft(boolean b) {
            m.draft = b;
            return this;
        }

        public StdPAPPolicyParamsBuilder oldPolicyFileName(String name) {
            m.oldPolicyFileName = name;
            return this;
        }

        public StdPAPPolicyParamsBuilder serviceType(String s) {
            m.serviceType = s;
            return this;
        }

        public StdPAPPolicyParamsBuilder uuid(String uuid) {
            m.uuid = uuid;
            return this;
        }

        public StdPAPPolicyParamsBuilder msLocation(String msLocation) {
            m.msLocation = msLocation;
            return this;
        }

        public StdPAPPolicyParamsBuilder priority(String priority) {
            m.priority = priority;
            return this;
        }

        public StdPAPPolicyParamsBuilder deleteCondition(String deleteCondition) {
            m.deleteCondition = deleteCondition;
            return this;
        }

        public StdPAPPolicyParamsBuilder dictionaryType(String dictionaryType) {
            m.dictionaryType = dictionaryType;
            return this;
        }

        public StdPAPPolicyParamsBuilder dictionary(String dictionary) {
            m.dictionary = dictionary;
            return this;
        }

        public StdPAPPolicyParamsBuilder dictionaryFields(String dictionaryFields) {
            m.dictionaryFields = dictionaryFields;
            return this;
        }
        
        public StdPAPPolicyParamsBuilder rawXacmlPolicy(String rawXacmlPolicy) {
            m.rawXacmlPolicy = rawXacmlPolicy;
            return this;
        }
    }
}
