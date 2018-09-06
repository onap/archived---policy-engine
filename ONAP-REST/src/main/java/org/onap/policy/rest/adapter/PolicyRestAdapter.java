/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.rest.adapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.onap.policy.rest.jpa.OnapName;

public class PolicyRestAdapter {

    /*
     * 
     * Note : Make Sure if any variables are added in PolicyRestAdapter.java, add them to PolicyElasticData.java file
     * 
     * 
     */

    // Common
    private Object data;
    private String policyName = null;
    private String configBodyData = null;
    private String configType = null;
    private String policyID = null;
    private String policyType = null;
    private String comboPolicyType;
    private String configPolicyType = null;
    private String policyDescription = null;
    private String onapName = null;
    private String configName = null;
    private String ruleID = null;
    private String parentPath;
    private boolean isValidData = false;
    private String adminNotification = null;
    private boolean isEditPolicy = false;
    private boolean isViewPolicy = false;
    private boolean isDraft = false;
    private Object policyData = null;
    private String gitPath;
    private boolean readOnly;
    private String configHome;
    private String configUrl;
    private String finalPolicyPath;
    private String version;
    private String jsonBody;
    private String apiflag;
    private String prevJsonBody;
    private Integer highestVersion;
    private EntityManagerFactory entityManagerFactory = null;
    private Boolean policyExists = false;
    private String oldPolicyFileName = null;
    private String domain = null;
    private String userId;
    private String newFileName;
    private String clWarning = null;
    private String newCLName = null;
    private String existingCLName = null;
    // Used by GUI
    private OnapName onapNameField;
    private Object jsonBodyData;
    private String dirPath;
    private String configBodyPath;
    private List<Object> attributes;
    private List<Object> settings;
    private List<Object> ruleAlgorithmschoices;

    private Map<?, ?> serviceTypePolicyName;

    private Map<?, ?> verticaMetrics;
    private Map<?, ?> description;
    private Map<?, ?> attributeFields;

    // ClosedLoop
    private String clearTimeOut;
    private String trapMaxAge;
    private String verificationclearTimeOut;
    private Map<String, String> dynamicLayoutMap;
    private ClosedLoopFaultTrapDatas trapDatas;
    private ClosedLoopFaultTrapDatas faultDatas;

    // FireWall
    private String fwPolicyType;
    private List<Object> fwattributes;
    private String parentForChild;
    private String securityZone;

    // Action & Decision
    private String ruleCombiningAlgId = null;
    private Map<String, String> dynamicFieldConfigAttributes;
    private Map<String, String> dynamicSettingsMap;
    private Map<String, String> dropDownMap;
    private String actionPerformer = null;
    private String actionAttribute = null;
    private List<String> dynamicRuleAlgorithmLabels;
    private List<String> dynamicRuleAlgorithmCombo;
    private List<String> dynamicRuleAlgorithmField1;
    private List<String> dynamicRuleAlgorithmField2;
    private List<Object> dynamicVariableList;
    private List<String> dataTypeList;
    private String actionAttributeValue;
    private String ruleProvider;
    private String actionBody = null;
    private String actionDictHeader = null;
    private String actionDictType = null;
    private String actionDictUrl = null;
    private String actionDictMethod = null;
    private YAMLParams yamlparams;
    private List<String> blackListEntries;
    private List<String> appendBlackListEntries;
    private String blackListEntryType;
    private String rawXacmlPolicy;

    // Rainy Day Decision
    private RainyDayParams rainyday;
    private Map<String, String> rainydayMap;
    private List<String> errorCodeList;
    private List<String> treatmentList;

    // MicroSerice
    private String serviceType = null;
    private String uuid = null;
    private String location = null;
    private String priority = null;
    private String msLocation = null;
    private Object policyJSON = null;

    // BRMS Policies
    private String ruleName;
    private Map<String, String> brmsParamBody = null;
    private String brmsController = null;
    private List<String> brmsDependency = null;
    private LinkedHashMap<?, ?> ruleData;
    private LinkedHashMap<?, ?> ruleListData;
    private Map<String, String> drlRuleAndUIParams = null;

    // Safe Policy
    private String policyScope;
    private String providerComboBox = null;
    private String riskType;
    private String riskLevel;
    private String guard = null;
    private String ttlDate;
    private Map<String, String> matching;

    private List<Object> triggerSignatures;
    private List<Object> symptomSignatures;
    private String logicalConnector;
    private String policyStatus;
    private String gocServerScope;
    private String supressionType;

    public List<Object> getTriggerSignatures() {
        return triggerSignatures;
    }

    public void setTriggerSignatures(List<Object> triggerSignatures) {
        this.triggerSignatures = triggerSignatures;
    }

    public List<Object> getSymptomSignatures() {
        return symptomSignatures;
    }

    public void setSymptomSignatures(List<Object> symptomSignatures) {
        this.symptomSignatures = symptomSignatures;
    }

    public String getLogicalConnector() {
        return logicalConnector;
    }

    public void setLogicalConnector(String logicalConnector) {
        this.logicalConnector = logicalConnector;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getGocServerScope() {
        return gocServerScope;
    }

    public void setGocServerScope(String gocServerScope) {
        this.gocServerScope = gocServerScope;
    }

    public String getSupressionType() {
        return supressionType;
    }

    public void setSupressionType(String supressionType) {
        this.supressionType = supressionType;
    }

    /********************************************************************************/

    public String getComboPolicyType() {
        return comboPolicyType;
    }

    public void setComboPolicyType(String comboPolicyType) {
        this.comboPolicyType = comboPolicyType;
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getOldPolicyFileName() {
        return oldPolicyFileName;
    }

    public void setOldPolicyFileName(String oldPolicyFileName) {
        this.oldPolicyFileName = oldPolicyFileName;
    }

    public String getDomainDir() {
        return domain;
    }

    public void setDomainDir(String domain) {
        this.domain = domain;
    }

    public Integer getHighestVersion() {
        return highestVersion;
    }

    public void setHighestVersion(Integer highestVersion) {
        this.highestVersion = highestVersion;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getConfigBodyData() {
        return configBodyData;
    }

    public void setConfigBodyData(String configBodyData) {
        this.configBodyData = configBodyData;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getPolicyID() {
        return policyID;
    }

    public void setPolicyID(String policyID) {
        this.policyID = policyID;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public String getOnapName() {
        return onapName;
    }

    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getRuleID() {
        return ruleID;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public String getRuleCombiningAlgId() {
        return ruleCombiningAlgId;
    }

    public void setRuleCombiningAlgId(String ruleCombiningAlgId) {
        this.ruleCombiningAlgId = ruleCombiningAlgId;
    }

    public Map<String, String> getDynamicFieldConfigAttributes() {
        return dynamicFieldConfigAttributes;
    }

    public void setDynamicFieldConfigAttributes(Map<String, String> dynamicFieldConfigAttributes) {
        this.dynamicFieldConfigAttributes = dynamicFieldConfigAttributes;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public boolean isEditPolicy() {
        return isEditPolicy;
    }

    public void setEditPolicy(boolean isEditPolicy) {
        this.isEditPolicy = isEditPolicy;
    }

    public boolean isViewPolicy() {
        return isViewPolicy;
    }

    public void setViewPolicy(boolean isViewPolicy) {
        this.isViewPolicy = isViewPolicy;
    }

    public Object getPolicyData() {
        return policyData;
    }

    public void setPolicyData(Object policyData) {
        this.policyData = policyData;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isValidData() {
        return isValidData;
    }

    public void setValidData(boolean isValidData) {
        this.isValidData = isValidData;
    }

    public String getAdminNotification() {
        return adminNotification;
    }

    public void setAdminNotification(String adminNotification) {
        this.adminNotification = adminNotification;
    }

    public String getConfigHome() {
        return configHome;
    }

    public void setConfigHome(String configHome) {
        this.configHome = configHome;
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }

    public String getFinalPolicyPath() {
        return finalPolicyPath;
    }

    public void setFinalPolicyPath(String finalPolicyPath) {
        this.finalPolicyPath = finalPolicyPath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public String getPrevJsonBody() {
        return prevJsonBody;
    }

    public void setPrevJsonBody(String prevJsonBody) {
        this.prevJsonBody = prevJsonBody;
    }

    public String getApiflag() {
        return apiflag;
    }

    public void setApiflag(String apiflag) {
        this.apiflag = apiflag;
    }

    /**
     * @return the actionPerformer
     */
    public String getActionPerformer() {
        return actionPerformer;
    }

    /**
     * @param actionPerformer the actionPerformer to set
     */
    public void setActionPerformer(String actionPerformer) {
        this.actionPerformer = actionPerformer;
    }

    /**
     * @return the actionAttribute
     */
    public String getActionAttribute() {
        return actionAttribute;
    }

    /**
     * @param actionAttribute the actionAttribute to set
     */
    public void setActionAttribute(String actionAttribute) {
        this.actionAttribute = actionAttribute;
    }

    /**
     * @return the dynamicRuleAlgorithmLabels
     */
    public List<String> getDynamicRuleAlgorithmLabels() {
        return dynamicRuleAlgorithmLabels;
    }

    /**
     * @param dynamicRuleAlgorithmLabels the dynamicRuleAlgorithmLabels to set
     */
    public void setDynamicRuleAlgorithmLabels(List<String> dynamicRuleAlgorithmLabels) {
        this.dynamicRuleAlgorithmLabels = dynamicRuleAlgorithmLabels;
    }

    /**
     * @return the dynamicRuleAlgorithmCombo
     */
    public List<String> getDynamicRuleAlgorithmCombo() {
        return dynamicRuleAlgorithmCombo;
    }

    /**
     * @param dynamicRuleAlgorithmCombo the dynamicRuleAlgorithmCombo to set
     */
    public void setDynamicRuleAlgorithmCombo(List<String> dynamicRuleAlgorithmCombo) {
        this.dynamicRuleAlgorithmCombo = dynamicRuleAlgorithmCombo;
    }

    /**
     * @return the dynamicRuleAlgorithmField1
     */
    public List<String> getDynamicRuleAlgorithmField1() {
        return dynamicRuleAlgorithmField1;
    }

    /**
     * @param dynamicRuleAlgorithmField1 the dynamicRuleAlgorithmField1 to set
     */
    public void setDynamicRuleAlgorithmField1(List<String> dynamicRuleAlgorithmField1) {
        this.dynamicRuleAlgorithmField1 = dynamicRuleAlgorithmField1;
    }

    /**
     * @return the dynamicRuleAlgorithmField2
     */
    public List<String> getDynamicRuleAlgorithmField2() {
        return dynamicRuleAlgorithmField2;
    }

    /**
     * @param dynamicRuleAlgorithmField2 the dynamicRuleAlgorithmField2 to set
     */
    public void setDynamicRuleAlgorithmField2(List<String> dynamicRuleAlgorithmField2) {
        this.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
    }

    public Map<String, String> getDropDownMap() {
        return dropDownMap;
    }

    public void setDropDownMap(Map<String, String> dropDownMap) {
        this.dropDownMap = dropDownMap;
    }

    public Map<String, String> getDynamicSettingsMap() {
        return dynamicSettingsMap;
    }

    public void setDynamicSettingsMap(Map<String, String> dynamicSettingsMap) {
        this.dynamicSettingsMap = dynamicSettingsMap;
    }

    public List<Object> getDynamicVariableList() {
        return dynamicVariableList;
    }

    public void setDynamicVariableList(List<Object> dynamicVariableList) {
        this.dynamicVariableList = dynamicVariableList;
    }

    public List<String> getDataTypeList() {
        return dataTypeList;
    }

    public void setDataTypeList(List<String> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean isDraft) {
        this.isDraft = isDraft;
    }

    public String getConfigPolicyType() {
        return configPolicyType;
    }

    public void setConfigPolicyType(String configPolicyType) {
        this.configPolicyType = configPolicyType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Map<String, String> getBrmsParamBody() {
        return brmsParamBody;
    }

    public void setBrmsParamBody(Map<String, String> brmsParamBody) {
        this.brmsParamBody = brmsParamBody;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * @return the policyExists
     */
    public Boolean getPolicyExists() {
        return policyExists;
    }

    /**
     * @param policyExists the policyExists to set
     */
    public void setPolicyExists(Boolean policyExists) {
        this.policyExists = policyExists;
    }

    public String getPolicyScope() {
        return policyScope;
    }

    public void setPolicyScope(String domainDir) {
        this.policyScope = domainDir;
    }

    public String getProviderComboBox() {
        return providerComboBox;
    }

    public void setProviderComboBox(String providerComboBox) {
        this.providerComboBox = providerComboBox;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getTtlDate() {
        return ttlDate;
    }

    public void setTtlDate(String ttlDate) {
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

    public Map<String, String> getMatching() {
        return matching;
    }

    public void setMatching(Map<String, String> matching) {
        this.matching = matching;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public OnapName getOnapNameField() {
        return onapNameField;
    }

    public void setOnapNameField(OnapName onapNameField) {
        this.onapNameField = onapNameField;
    }

    public Object getJsonBodyData() {
        return jsonBodyData;
    }

    public void setJsonBodyData(Object jsonBodyData) {
        this.jsonBodyData = jsonBodyData;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getConfigBodyPath() {
        return configBodyPath;
    }

    public void setConfigBodyPath(String configBodyPath) {
        this.configBodyPath = configBodyPath;
    }

    public List<Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Object> attributes) {
        this.attributes = attributes;
    }

    public List<Object> getSettings() {
        return settings;
    }

    public void setSettings(List<Object> settings) {
        this.settings = settings;
    }

    public List<Object> getRuleAlgorithmschoices() {
        return ruleAlgorithmschoices;
    }

    public void setRuleAlgorithmschoices(List<Object> ruleAlgorithmschoices) {
        this.ruleAlgorithmschoices = ruleAlgorithmschoices;
    }

    public Map<?, ?> getServiceTypePolicyName() {
        return serviceTypePolicyName;
    }

    public void setServiceTypePolicyName(Map<?, ?> serviceTypePolicyName) {
        this.serviceTypePolicyName = serviceTypePolicyName;
    }

    public Map<?, ?> getVerticaMetrics() {
        return verticaMetrics;
    }

    public void setVerticaMetrics(Map<?, ?> verticaMetrics) {
        this.verticaMetrics = verticaMetrics;
    }

    public Map<?, ?> getDescription() {
        return description;
    }

    public void setDescription(LinkedHashMap<?, ?> description) {
        this.description = description;
    }

    public Map<?, ?> getAttributeFields() {
        return attributeFields;
    }

    public void setAttributeFields(LinkedHashMap<?, ?> attributeFields) {
        this.attributeFields = attributeFields;
    }

    public String getClearTimeOut() {
        return clearTimeOut;
    }

    public void setClearTimeOut(String clearTimeOut) {
        this.clearTimeOut = clearTimeOut;
    }

    public String getTrapMaxAge() {
        return trapMaxAge;
    }

    public void setTrapMaxAge(String trapMaxAge) {
        this.trapMaxAge = trapMaxAge;
    }

    public String getVerificationclearTimeOut() {
        return verificationclearTimeOut;
    }

    public void setVerificationclearTimeOut(String verificationclearTimeOut) {
        this.verificationclearTimeOut = verificationclearTimeOut;
    }

    public Map<String, String> getDynamicLayoutMap() {
        return dynamicLayoutMap;
    }

    public void setDynamicLayoutMap(Map<String, String> dynamicLayoutMap) {
        this.dynamicLayoutMap = dynamicLayoutMap;
    }

    public String getFwPolicyType() {
        return fwPolicyType;
    }

    public void setFwPolicyType(String fwPolicyType) {
        this.fwPolicyType = fwPolicyType;
    }

    public List<Object> getFwattributes() {
        return fwattributes;
    }

    public void setFwattributes(List<Object> fwattributes) {
        this.fwattributes = fwattributes;
    }

    public String getParentForChild() {
        return parentForChild;
    }

    public void setParentForChild(String parentForChild) {
        this.parentForChild = parentForChild;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public LinkedHashMap<?, ?> getRuleData() {
        return ruleData;
    }

    public void setRuleData(LinkedHashMap<?, ?> ruleData) {
        this.ruleData = ruleData;
    }

    public LinkedHashMap<?, ?> getRuleListData() {
        return ruleListData;
    }

    public void setRuleListData(LinkedHashMap<?, ?> ruleListData) {
        this.ruleListData = ruleListData;
    }

    public String getSecurityZone() {
        return securityZone;
    }

    public void setSecurityZone(String securityZone) {
        this.securityZone = securityZone;
    }

    public String getActionAttributeValue() {
        return actionAttributeValue;
    }

    public void setActionAttributeValue(String actionAttributeValue) {
        this.actionAttributeValue = actionAttributeValue;
    }

    public String getRuleProvider() {
        return ruleProvider;
    }

    public void setRuleProvider(String ruleProvider) {
        this.ruleProvider = ruleProvider;
    }

    public String getMsLocation() {
        return msLocation;
    }

    public void setMsLocation(String msLocation) {
        this.msLocation = msLocation;
    }

    public Map<String, String> getDrlRuleAndUIParams() {
        return drlRuleAndUIParams;
    }

    public void setDrlRuleAndUIParams(Map<String, String> drlRuleAndUIParams) {
        this.drlRuleAndUIParams = drlRuleAndUIParams;
    }

    public String getActionBody() {
        return actionBody;
    }

    public void setActionBody(String actionBody) {
        this.actionBody = actionBody;
    }

    public String getActionDictHeader() {
        return actionDictHeader;
    }

    public void setActionDictHeader(String actionDictHeader) {
        this.actionDictHeader = actionDictHeader;
    }

    public String getActionDictType() {
        return actionDictType;
    }

    public void setActionDictType(String actionDictType) {
        this.actionDictType = actionDictType;
    }

    public String getActionDictUrl() {
        return actionDictUrl;
    }

    public void setActionDictUrl(String actionDictUrl) {
        this.actionDictUrl = actionDictUrl;
    }

    public String getActionDictMethod() {
        return actionDictMethod;
    }

    public void setActionDictMethod(String actionDictMethod) {
        this.actionDictMethod = actionDictMethod;
    }

    public String getClWarning() {
        return clWarning;
    }

    public void setClWarning(String clWarning) {
        this.clWarning = clWarning;
    }

    public String getNewCLName() {
        return newCLName;
    }

    public void setNewCLName(String newCLName) {
        this.newCLName = newCLName;
    }

    public String getExistingCLName() {
        return existingCLName;
    }

    public void setExistingCLName(String existingCLName) {
        this.existingCLName = existingCLName;
    }

    public YAMLParams getYamlparams() {
        return yamlparams;
    }

    public void setYamlparams(YAMLParams yamlparams) {
        this.yamlparams = yamlparams;
    }

    /**
     * @return the rainyday
     */
    public RainyDayParams getRainyday() {
        return rainyday;
    }

    /**
     * @param rainyday the rainyday to set
     */
    public void setRainyday(RainyDayParams rainyday) {
        this.rainyday = rainyday;
    }

    /**
     * @return the errorCodeList
     */
    public List<String> getErrorCodeList() {
        return errorCodeList;
    }

    /**
     * @param errorCodeList the errorCodeList to set
     */
    public void setErrorCodeList(List<String> errorCodeList) {
        this.errorCodeList = errorCodeList;
    }

    /**
     * @return the treatmentList
     */
    public List<String> getTreatmentList() {
        return treatmentList;
    }

    /**
     * @param treatmentList the treatmentList to set
     */
    public void setTreatmentList(List<String> treatmentList) {
        this.treatmentList = treatmentList;
    }

    /**
     * @return the rainydayMap
     */
    public Map<String, String> getRainydayMap() {
        return rainydayMap;
    }

    /**
     * @param rainydayMap the rainydayMap to set
     */
    public void setRainydayMap(Map<String, String> rainydayMap) {
        this.rainydayMap = rainydayMap;
    }

    /**
     * @return the policyJSON
     */
    public Object getPolicyJSON() {
        return policyJSON;
    }

    /**
     * @param policyJSON the policyJSON to set
     */
    public void setPolicyJSON(Object policyJSON) {
        this.policyJSON = policyJSON;
    }

    public ClosedLoopFaultTrapDatas getTrapDatas() {
        return trapDatas;
    }

    public void setTrapDatas(ClosedLoopFaultTrapDatas trapDatas) {
        this.trapDatas = trapDatas;
    }

    public ClosedLoopFaultTrapDatas getFaultDatas() {
        return faultDatas;
    }

    public void setFaultDatas(ClosedLoopFaultTrapDatas faultDatas) {
        this.faultDatas = faultDatas;
    }

    public List<String> getAppendBlackListEntries() {
        return appendBlackListEntries;
    }

    public void setAppendBlackListEntries(List<String> appendBlackListEntries) {
        this.appendBlackListEntries = appendBlackListEntries;
    }

    public List<String> getBlackListEntries() {
        return blackListEntries;
    }

    public void setBlackListEntries(List<String> blackListEntries) {
        this.blackListEntries = blackListEntries;
    }

    public String getBlackListEntryType() {
        return blackListEntryType;
    }

    public void setBlackListEntryType(String blackListEntryType) {
        this.blackListEntryType = blackListEntryType;
    }
    
    public String getRawXacmlPolicy() {
        return rawXacmlPolicy;
    }

    public void setRawXacmlPolicy(String rawXacmlPolicy) {
        this.rawXacmlPolicy = rawXacmlPolicy;
    }
}
