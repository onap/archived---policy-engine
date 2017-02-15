/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.adapter;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openecomp.policy.rest.jpa.EcompName;

public class PolicyAdapter {
	
	private Object data;
	private String policyName = null;
	private Path parentPath;	
	public boolean isEditPolicy = false;
	private boolean isViewPolicy = false;
	private Object policyData = null;
	private String comboPolicyType;
	private boolean readOnly;
	
	//adding new properties for REST interface call when creating Policies
	private String oldPolicyFileName = null;
	private String configType = null;
	private String policyID = null;
	private String policyType = null;
	private String configPolicyType = null;
	private String policyDescription = null;
	private String ecompName = null;
	private String configName = null;
	private String ruleID = null;
	private String ruleCombiningAlgId = null;
	private Map<String,String> dynamicFieldConfigAttributes;
	private Map<String,String> dropDownMap;
	private Map<String,String> dynamicSettingsMap;
	private Path gitPath;
	private String configBodyData = null;
	private boolean isValidData = false;
	private boolean draft = false;
	private String version = null;
	private String domain = null;
	private String filterName = null;
	private String comboConfigPolicyType;
	private String jsonBody = null;
	private Map<String,String> brmsParamBody=null;
	private Integer highestVersion;
	private String actionPerformer = null;
	private String actionAttribute = null;
	private List<String> dynamicRuleAlgorithmLabels;
	private List<String> dynamicRuleAlgorithmCombo;
	private List<String> dynamicRuleAlgorithmField1;
	private List<String> dynamicRuleAlgorithmField2;
	private List<Object> dynamicVariableList;
	private List<String> dataTypeList;
	private String actionBody = null;
	private String actionDictHeader = null;
	private String actionDictType = null;
	private String actionDictUrl = null;
	private String actionDictMethod = null;
	private String serviceType = null;
	private String uuid = null;
	private String location = null;
	private String priority = null;
	private String actionAttributeValue;
	private String ruleProvider;
	
	private EcompName ecompNameField;
	private Object jsonBodyData;
	private String dirPath;
	private String configBodyPath;
	private ArrayList<Object> attributes;
	private ArrayList<Object> settings;
	private ArrayList<Object> ruleAlgorithmschoices;
	
	private LinkedHashMap<?, ?> serviceTypePolicyName;
	private String ruleName;
	private LinkedHashMap<?, ?>  ruleData;
	private LinkedHashMap<?,?>   ruleListData;
	private String clearTimeOut;
	private String trapMaxAge;
	private String verificationclearTimeOut;
	private String fwPolicyType;
	private ArrayList<Object> fwattributes;
	private String parentForChild; 
	public String getFwPolicyType() {
		return fwPolicyType;
	}
	public ArrayList<Object> getFwattributes() {
		return fwattributes;
	}
	public String getParentForChild() {
		return parentForChild;
	}
	public void setFwPolicyType(String fwPolicyType) {
		this.fwPolicyType = fwPolicyType;
	}
	public void setFwattributes(ArrayList<Object> fwattributes) {
		this.fwattributes = fwattributes;
	}
	public void setParentForChild(String parentForChild) {
		this.parentForChild = parentForChild;
	}
	
	private String riskLevel;
	private String riskType = null;
	private String guard = null;
	private String ttlDate = null;
	
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
	public LinkedHashMap<?, ?> getRuleListData() {
		return ruleListData;
	}
	public void setRuleListData(LinkedHashMap<?, ?> ruleListData) {
		this.ruleListData = ruleListData;
	}
	private ArrayList<Object> triggerSignatures;
	private ArrayList<Object> symptomSignatures;
	private String logicalConnector;
	private String policyStatus;
	public String gocServerScope;
	public Map<String , String> dynamicLayoutMap;
	private String securityZone;
	
	private String policyScope;
	private String supressionType;
	


	public String getSupressionType() {
		return supressionType;
	}
	public void setSupressionType(String supressionType) {
		this.supressionType = supressionType;
	}
	public Map<String, String> getDynamicLayoutMap() {
		return dynamicLayoutMap;
	}
	public void setDynamicLayoutMap(Map<String, String> dynamicLayoutMap) {
		this.dynamicLayoutMap = dynamicLayoutMap;
	}
	public String getGocServerScope() {
		return gocServerScope;
	}
	public void setGocServerScope(String gocServerScope) {
		this.gocServerScope = gocServerScope;
	}
	public Object getJsonBodyData() {
		return jsonBodyData;
	}
	public void setJsonBodyData(Object jsonBodyData) {
		this.jsonBodyData = jsonBodyData;
	}
	public EcompName getEcompNameField() {
		return ecompNameField;
	}
	public void setEcompNameField(EcompName ecompNameField) {
		this.ecompNameField = ecompNameField;
	}
	public Integer getHighestVersion() {
		return highestVersion;
	}
	public void setHighestVersion(Integer highestVersion) {
		this.highestVersion = highestVersion;
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
	public String getEcompName() {
		return ecompName;
	}
	public void setEcompName(String ecompName) {
		this.ecompName = ecompName;
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
	public void setDynamicFieldConfigAttributes(
			Map<String, String> dynamicFieldConfigAttributes) {
		this.dynamicFieldConfigAttributes = dynamicFieldConfigAttributes;
	}
	public Path getGitPath() {
		return gitPath;
	}
	public void setGitPath(Path gitPath) {
		this.gitPath = gitPath;
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
	public Path getParentPath() {
		return parentPath;
	}
	public void setParentPath(Path parentPath) {
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
	public String getComboPolicyType() {
		return comboPolicyType;
	}
	public void setComboPolicyType(String comboPolicyType) {
		this.comboPolicyType = comboPolicyType;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public String getConfigBodyData() {
		return configBodyData;
	}
	public void setConfigBodyData(String configBodyData) {
		this.configBodyData = configBodyData;
	}
	public boolean isValidData() {
		return isValidData;
	}
	public void setValidData(boolean isValidData) {
		this.isValidData = isValidData;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDomainDir() {
		return domain;
	}
	public void setDomainDir(java.lang.String domain) {
		this.domain = domain;
		
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getComboConfigPolicyType() {
		return comboConfigPolicyType;
	}
	public void setComboConfigPolicyType(String comboConfigPolicyType) {
		this.comboConfigPolicyType = comboConfigPolicyType;
	}
	public Map<String, String> getBRMSParamBody() {
		return brmsParamBody;
	}
	public void setBRMSParamBody(Map<String, String> brmsParamBody) {
		this.brmsParamBody = brmsParamBody;
	} 
	public String getJsonBody() {
		return jsonBody;
	}
	public void setJsonBody(String jsonBody) {
		this.jsonBody = jsonBody;
	}
	public String getConfigPolicyType() {
		return configPolicyType;
	}
	public void setConfigPolicyType(String configPolicyType) {
		this.configPolicyType = configPolicyType;
	}
	public String getActionPerformer() {
		return actionPerformer;
	}
	public void setActionPerformer(String actionPerformer) {
		this.actionPerformer = actionPerformer;
	}
	public String getActionAttribute() {
		return actionAttribute;
	}
	public void setActionAttribute(String actionAttribute) {
		this.actionAttribute = actionAttribute;
	}
	public List<String> getDynamicRuleAlgorithmLabels() {
		return dynamicRuleAlgorithmLabels;
	}
	public void setDynamicRuleAlgorithmLabels(
			List<String> dynamicRuleAlgorithmLabels) {
		this.dynamicRuleAlgorithmLabels = dynamicRuleAlgorithmLabels;
	}
	public List<String> getDynamicRuleAlgorithmCombo() {
		return dynamicRuleAlgorithmCombo;
	}
	public void setDynamicRuleAlgorithmCombo(
			List<String> dynamicRuleAlgorithmCombo) {
		this.dynamicRuleAlgorithmCombo = dynamicRuleAlgorithmCombo;
	}
	public List<String> getDynamicRuleAlgorithmField1() {
		return dynamicRuleAlgorithmField1;
	}
	public void setDynamicRuleAlgorithmField1(
			List<String> dynamicRuleAlgorithmField1) {
		this.dynamicRuleAlgorithmField1 = dynamicRuleAlgorithmField1;
	}
	public List<String> getDynamicRuleAlgorithmField2() {
		return dynamicRuleAlgorithmField2;
	}
	public void setDynamicRuleAlgorithmField2(
			List<String> dynamicRuleAlgorithmField2) {
		this.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
	}
	public String getActionBody() {
		return actionBody;
	}
	public void setActionBody(String actionBody) {
		this.actionBody = actionBody;
	}
	public Map<String,String> getDropDownMap() {
		return dropDownMap;
	}
	public void setDropDownMap(Map<String,String> dropDownMap) {
		this.dropDownMap = dropDownMap;
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
	public boolean isDraft() {
		return draft;
	}
	public void setDraft(boolean draft) {
		this.draft = draft;
	}
	public Map<String,String> getDynamicSettingsMap() {
		return dynamicSettingsMap;
	}
	public void setDynamicSettingsMap(Map<String,String> dynamicSettingsMap) {
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
	public String getOldPolicyFileName() {
		return oldPolicyFileName;
	}
	public void setOldPolicyFileName(String oldPolicyFileName) {
		this.oldPolicyFileName = oldPolicyFileName;
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
	public ArrayList<Object> getAttributes() {
		return attributes;
	}
	
	@SuppressWarnings("unchecked")
	public void setAttributes(Object attributes) {
		this.attributes =   (ArrayList<Object>) attributes;
	}
	public LinkedHashMap<?, ?> getServiceTypePolicyName() {
		return serviceTypePolicyName;
	}
	public void setServiceTypePolicyName(LinkedHashMap<?, ?> serviceTypePolicyName) {
		this.serviceTypePolicyName = (LinkedHashMap<?, ?>) serviceTypePolicyName;
	}
	public ArrayList<Object> getSettings() {
		return settings;
	}
	public void setSettings(ArrayList<Object> settings) {
		this.settings = settings;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public LinkedHashMap<?, ?>  getRuleData() {
		return ruleData;
	}
	public void setRuleData(LinkedHashMap<?, ?>  ruleData) {
		this.ruleData = ruleData;
	}
	public ArrayList<Object> getTriggerSignatures() {
		return triggerSignatures;
	}
	public void setTriggerSignatures(ArrayList<Object> triggerSignatures) {
		this.triggerSignatures = triggerSignatures;
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
	public String getActionAttributeValue() {
		return actionAttributeValue;
	}
	public void setActionAttributeValue(String actionAttributeValue) {
		this.actionAttributeValue = actionAttributeValue;
	}
	public ArrayList<Object> getRuleAlgorithmschoices() {
		return ruleAlgorithmschoices;
	}
	public void setRuleAlgorithmschoices(ArrayList<Object> ruleAlgorithmschoices) {
		this.ruleAlgorithmschoices = ruleAlgorithmschoices;
	}
	public String getSecurityZone() {
		return securityZone;
	}
	public void setSecurityZone(String securityZone) {
		this.securityZone = securityZone;
	}
	public ArrayList<Object> getSymptomSignatures() {
		return symptomSignatures;
	}
	public void setSymptomSignatures(ArrayList<Object> symptomSignatures) {
		this.symptomSignatures = symptomSignatures;
	}
	public String getPolicyScope() {
		return policyScope;
	}
	public void setPolicyScope(String policyScope) {
		this.policyScope = policyScope;
	}
	public String getRuleProvider() {
		return ruleProvider;
	}
	public void setRuleProvider(String ruleProvider) {
		this.ruleProvider = ruleProvider;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
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
	public String getTtlDate() {
		return ttlDate;
	}
	public void setTtlDate(String ttlDate) {
		this.ttlDate = ttlDate;
	}
}
