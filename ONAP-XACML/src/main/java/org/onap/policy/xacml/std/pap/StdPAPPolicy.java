/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.policy.xacml.api.pap.OnapPAPPolicy;

public class StdPAPPolicy implements OnapPAPPolicy, Serializable {
    private static final long serialVersionUID = 5260230629397322000L;

    private String policyName = null;
    private String oldPolicyFileName = null;
    private String policyDescription = null;
    private String onapName = null;
    private String configName = null;
    private Map<String, String> dynamicFieldConfigAttributes = new HashMap<>();
    private Map<String, String> treatments = new HashMap<>();
    private Map<String, String> dropDownMap = new HashMap<>();
    private Map<String, String> dynamicSettingsMap = new HashMap<>();
    private List<String> dynamicRuleAlgorithmLabels;
    private List<String> dynamicRuleAlgorithmCombo;
    private List<String> dynamicRuleAlgorithmField1;
    private List<String> dynamicRuleAlgorithmField2;
    private transient List<Object> dynamicVariableList;
    private List<String> dataTypeList;
    private String configBodyData = null;
    private String policyID = null;
    private String ruleID = null;
    private String brmsController;
    private List<String> brmsDependency;
    private String configType = null;
    private Boolean editPolicy = false;
    private Boolean draft = false;
    private String version = null;
    private String domain = null;
    private String configPolicyType = null;
    private String jsonBody = null;
    private String serviceType = null;
    private Integer highestVersion = null;
    private URI location = null;
    private String actionPerformer = null;
    private String actionAttribute = null;
    private String actionBody = null;
    private String actionDictHeader = null;
    private String actionDictType = null;
    private String actionDictUrl = null;
    private String actionDictMethod = null;
    private String uuid = null;
    private String msLocation = null;
    private String priority = null;
    private transient Map<String, String> drlRuleAndUIParams = null;
    private String deleteCondition = null;
    private String dictionaryType = null;
    private String dictionary = null;
    private String dictionaryFields = null;
    private String providerComboBox = null;
    private String riskType = null;
    private String guard = null;
    private String riskLevel;
    private String ttlDate = null;


    public StdPAPPolicy() {
        //
        // Default empty constructor
        //
    }

    //Constructor for sending location when pushing policies
    public StdPAPPolicy(URI location) {
        this.location = location;
    }

    //Constructor for Validating Config Policies
    public StdPAPPolicy(String policyName, String body, String configType, String configPolicyType) {
        this.policyName = policyName;
        this.configBodyData = body;
        this.configType = configType;
        this.configPolicyType = configPolicyType;
    }

    /**
     * Constructor for
     *  Create Config Policies from API and Admin Console
     *  Updating Config Policies from the API
     *  Create/Update Action Policies from API
     *  Create/Update Decision Policies
     * @param stdPAPPolicyParams Input params object
     */
    public StdPAPPolicy(StdPAPPolicyParams stdPAPPolicyParams) {
        this.configPolicyType=stdPAPPolicyParams.getConfigPolicyType();
        this.policyName = stdPAPPolicyParams.getPolicyName();
        this.policyDescription = stdPAPPolicyParams.getDescription();
        this.onapName = stdPAPPolicyParams.getOnapName();
        this.providerComboBox = stdPAPPolicyParams.getProviderComboBox();
        this.configName = stdPAPPolicyParams.getConfigName();
        this.dynamicFieldConfigAttributes = stdPAPPolicyParams.getDynamicFieldConfigAttributes();
        this.dynamicSettingsMap = stdPAPPolicyParams.getDynamicSettingsMap();
        this.configType = stdPAPPolicyParams.getConfigType();
        this.configBodyData = stdPAPPolicyParams.getConfigBodyData();
        this.editPolicy = stdPAPPolicyParams.getEditPolicy();
        this.domain = stdPAPPolicyParams.getDomain();
        this.highestVersion = stdPAPPolicyParams.getHighestVersion();
        this.riskLevel = stdPAPPolicyParams.getRiskLevel();
        this.riskType = stdPAPPolicyParams.getRiskType();
        this.guard = stdPAPPolicyParams.getGuard();
        this.ttlDate = stdPAPPolicyParams.getTtlDate();
        this.dynamicRuleAlgorithmLabels = stdPAPPolicyParams.getDynamicRuleAlgorithmLabels();
        this.dynamicRuleAlgorithmCombo = stdPAPPolicyParams.getDynamicRuleAlgorithmCombo();
        this.dynamicRuleAlgorithmField1 = stdPAPPolicyParams.getDynamicRuleAlgorithmField1();
        this.dynamicRuleAlgorithmField2 = stdPAPPolicyParams.getDynamicRuleAlgorithmField2();
        this.dynamicVariableList = stdPAPPolicyParams.getDynamicVariableList();
        this.dataTypeList = stdPAPPolicyParams.getDataTypeList();
        this.dropDownMap = stdPAPPolicyParams.getDropDownMap();
        this.actionPerformer = stdPAPPolicyParams.getActionPerformer();
        this.actionAttribute = stdPAPPolicyParams.getActionAttribute();
        this.treatments = stdPAPPolicyParams.getTreatments();
        this.policyID = stdPAPPolicyParams.getPolicyID();
        this.ruleID = stdPAPPolicyParams.getRuleID();
        this.version = stdPAPPolicyParams.getVersion();
        this.jsonBody = stdPAPPolicyParams.getJsonBody();
        this.brmsController = stdPAPPolicyParams.getBrmsController();
        this.brmsDependency = stdPAPPolicyParams.getBrmsDependency();
        this.drlRuleAndUIParams = stdPAPPolicyParams.getDrlRuleAndUIParams();
        this.serviceType = stdPAPPolicyParams.getServiceType();
        this.oldPolicyFileName = stdPAPPolicyParams.getOldPolicyFileName();
        this.draft = stdPAPPolicyParams.isDraft();
        this.uuid = stdPAPPolicyParams.getUuid();
        this.msLocation = stdPAPPolicyParams.getMsLocation();
        this.priority = stdPAPPolicyParams.getPriority();
        this.deleteCondition = stdPAPPolicyParams.getDeleteCondition();
        this.dictionaryType = stdPAPPolicyParams.getDictionaryType();
        this.dictionary = stdPAPPolicyParams.getDictionary();
        this.dictionaryFields = stdPAPPolicyParams.getDictionaryFields();

    }

    @Override
    public String getPolicyName() {
        return policyName;
    }

    @Override
    public String getPolicyDescription() {
        return policyDescription;
    }

    @Override
    public String getOnapName() {
        return onapName;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public Map<String, String> getDynamicFieldConfigAttributes() {
        return dynamicFieldConfigAttributes;
    }

    @Override
    public String getConfigBodyData() {
        return configBodyData;
    }

    @Override
    public String getPolicyID() {
        return policyID;
    }

    @Override
    public String getRuleID() {
        return ruleID;
    }

    @Override
    public String getConfigType() {
        return configType;
    }

    @Override
    public Boolean isEditPolicy() {
        return editPolicy;
    }

    @Override
    public Boolean isDraft() {
        return draft;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDomainDir() {
        return domain;
    }

    @Override
    public String getConfigPolicyType() {
        return configPolicyType;
    }

    @Override
    public String getJsonBody() {
        return jsonBody;
    }

    @Override
    public Integer getHighestVersion() {
        return highestVersion;
    }

    @Override
    public URI getLocation() {
        return location;
    }

    @Override
    public List<String> getDynamicRuleAlgorithmLabels() {
        return dynamicRuleAlgorithmLabels;
    }

    @Override
    public List<String> getDynamicRuleAlgorithmCombo() {
        return dynamicRuleAlgorithmCombo;
    }

    @Override
    public List<String> getDynamicRuleAlgorithmField1() {
        return dynamicRuleAlgorithmField1;
    }

    @Override
    public List<String> getDynamicRuleAlgorithmField2() {
        return dynamicRuleAlgorithmField2;
    }

    @Override
    public String getActionPerformer() {
        return actionPerformer;
    }

    @Override
    public String getActionAttribute() {
        return actionAttribute;
    }

    @Override
    public String getActionBody() {
        return actionBody;
    }

    @Override
    public Map<String, String> getDropDownMap() {
        return dropDownMap;
    }

    @Override
    public String getActionDictHeader() {
        return actionDictHeader;
    }

    @Override
    public String getActionDictType() {
        return actionDictType;
    }

    @Override
    public String getActionDictUrl() {
        return actionDictUrl;
    }

    @Override
    public String getActionDictMethod() {
        return actionDictMethod;
    }

    @Override
    public Map<String, String> getDynamicSettingsMap() {
        return dynamicSettingsMap;
    }

    @Override
    public List<Object> getDynamicVariableList() {
        return dynamicVariableList;
    }

    @Override
    public List<String> getDataTypeList() {
        return dataTypeList;
    }

    @Override
    public String getOldPolicyFileName() {
        return oldPolicyFileName;
    }

    @Override
    public String getServiceType() {
        return serviceType;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public String getMsLocation() {
        return msLocation;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Override
    public String getDeleteCondition() {
        return deleteCondition;
    }

    @Override
    public String getDictionaryType() {
        return dictionaryType;
    }

    @Override
    public String getDictionary() {
        return dictionary;
    }

    @Override
    public String getTTLDate() {
        return ttlDate;
    }

    @Override
    public String getDictionaryFields() {
        return dictionaryFields;
    }

    @Override
    public String getRiskType() {
        return riskType;
    }

    @Override
    public String getRiskLevel() {
        return riskLevel;
    }

    @Override
    public String getGuard() {
        return guard;
    }

    @Override
    public Map<String, String> getTreatments() {
        return treatments;
    }

    @Override
    public String toString() {
        return "StdPAPPolicy [policyName=" + policyName + ", policyDescription=" + policyDescription + ", onapName="
                + onapName + ", configName=" + configName + ", dynamicFieldConfigAttributes=" +
                dynamicFieldConfigAttributes + ", configBodyData=" + configBodyData
                + ", policyID=" + policyID + ", ruleID=" + ruleID + ", configType=" + configType + ", editPolicy=" +
                ", version=" + ", domain=" + domain
                + ", configPolicyType=" + configPolicyType + ", jsonBody=" + jsonBody + ", highestVersion=" +
                highestVersion + ", location=" + location
                + ",dynamicRuleAlgorithmLabels=" + dynamicRuleAlgorithmLabels + ",dynamicRuleAlgorithmCombo=" +
                dynamicRuleAlgorithmCombo
                + ",dynamicRuleAlgorithmField1=" + dynamicRuleAlgorithmField1 + ",dynamicRuleAlgorithmField2=" +
                dynamicRuleAlgorithmField2
                + ",actionPerformer=" + actionPerformer + ",actionAttribute=" + actionAttribute + ",actionBody=" +
                actionBody + ",dropDownMap=" + dropDownMap
                + ",actionDictHeader=" + actionDictHeader + ",actionDictType=" + actionDictType + ",actionDictUrl=" +
                actionDictUrl
                + ",actionDictMethod=" + actionDictMethod + ",dynamicSettingsMap=" + dynamicSettingsMap + "," +
                "dynamicVariableList=" + dynamicVariableList + ",providerComboBox=" + providerComboBox
                + ",dataTypeList=" + dataTypeList + ",draft=" + ",oldPolicyFileName=" + oldPolicyFileName + "," +
                "serviceType=" + serviceType
                + ",uuid=" + uuid + ",msLocation=" + msLocation + ",priority=" + priority + ",deleteCondition=" +
                deleteCondition + ",dictionaryType=" + dictionaryType
                + ",dictionary=" + dictionary + ",dictionaryFields=" + dictionaryFields + ",uuid=" + uuid + "," +
                "msLocation=" + msLocation + ",priority="
                + priority + ",deleteCondition=" + deleteCondition + ",riskType=" + riskType + ",riskLevel=" +
                riskLevel + ",guard=" + guard + ",ttlDate=" + ttlDate
                + ",treatments=" + treatments + "]";
    }

    // Methods needed for JSON Deserialization
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setDynamicFieldConfigAttributes(
            Map<String, String> dynamicFieldConfigAttributes) {
        this.dynamicFieldConfigAttributes = dynamicFieldConfigAttributes;
    }

    public void setConfigBodyData(String configBodyData) {
        this.configBodyData = configBodyData;
    }

    public void setPolicyID(String policyID) {
        this.policyID = policyID;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public void setEditPolicy(Boolean editPolicy) {
        this.editPolicy = editPolicy;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDomainDir(String domain) {
        this.domain = domain;
    }

    public void setConfigPolicyType(String configPolicyType) {
        this.configPolicyType = configPolicyType;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public void setHighestVersion(Integer highestVersion) {
        this.highestVersion = highestVersion;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

    public void setDynamicRuleAlgorithmLabels(
            List<String> dynamicRuleAlgorithmLabels) {
        this.dynamicRuleAlgorithmLabels = dynamicRuleAlgorithmLabels;
    }

    public void setDynamicRuleAlgorithmCombo(List<String> dynamicRuleAlgorithmCombo) {
        this.dynamicRuleAlgorithmCombo = dynamicRuleAlgorithmCombo;
    }

    public void setDynamicRuleAlgorithmField1(
            List<String> dynamicRuleAlgorithmField1) {
        this.dynamicRuleAlgorithmField1 = dynamicRuleAlgorithmField1;
    }

    public void setDynamicRuleAlgorithmField2(
            List<String> dynamicRuleAlgorithmField2) {
        this.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
    }

    public void setActionPerformer(String actionPerformer) {
        this.actionPerformer = actionPerformer;
    }

    public void setActionAttribute(String actionAttribute) {
        this.actionAttribute = actionAttribute;
    }

    public void setActionBody(String actionBody) {
        this.actionBody = actionBody;
    }

    public void setDropDownMap(Map<String, String> dropDownMap) {
        this.dropDownMap = dropDownMap;
    }

    public void setActionDictHeader(String actionDictHeader) {
        this.actionDictHeader = actionDictHeader;
    }

    public void setActionDictType(String actionDictType) {
        this.actionDictType = actionDictType;
    }

    public void setActionDictUrl(String actionDictUrl) {
        this.actionDictUrl = actionDictUrl;
    }

    public void setActionDictMethod(String actionDictMethod) {
        this.actionDictMethod = actionDictMethod;
    }

    public void setDynamicSettingsMap(Map<String, String> dynamicSettingsMap) {
        this.dynamicSettingsMap = dynamicSettingsMap;
    }

    public void setDynamicVariableList(List<Object> dynamicVariableList) {
        this.dynamicVariableList = dynamicVariableList;
    }

    public void setDataTypeList(List<String> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public void setOldPolicyFileName(String oldPolicyFileName) {
        this.oldPolicyFileName = oldPolicyFileName;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Map<String, String> getDrlRuleAndUIParams() {
        return drlRuleAndUIParams;
    }

    public void setDrlRuleAndUIParams(Map<String, String> drlRuleAndUIParams) {
        this.drlRuleAndUIParams = drlRuleAndUIParams;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setMsLocation(String msLocation) {
        this.msLocation = msLocation;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDeleteCondition(String deleteCondition) {
        this.deleteCondition = deleteCondition;
    }

    public void setDictionaryType(String dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public void setDictionaryFields(String dictionaryFields) {
        this.dictionaryFields = dictionaryFields;
    }

    public String getProviderComboBox() {
        return providerComboBox;
    }

    public void setProviderComboBox(String providerComboBox) {
        this.providerComboBox = providerComboBox;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public void setTTLDate(String ttlDate) {
        this.ttlDate = ttlDate;
    }

    public String getBrmsController() {
        return brmsController;
    }

    public void setBrmsController(String brmsController) {
        this.brmsController = brmsController;
    }

    public List<String> getBrmsDependency() {
        return brmsDependency;
    }

    public void setBrmsDependency(List<String> brmsDependency) {
        this.brmsDependency = brmsDependency;
    }

    public void setTreatments(Map<String, String> treatments) {
        this.treatments = treatments;
    }
}
