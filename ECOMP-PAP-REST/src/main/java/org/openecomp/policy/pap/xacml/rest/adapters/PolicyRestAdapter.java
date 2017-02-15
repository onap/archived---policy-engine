/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.xacml.rest.adapters;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

//import org.openecomp.policy.pap.xacml.rest.model.GitRepositoryContainer;

public class PolicyRestAdapter {
	
	private Object data;
	private String policyName = null;
	private String configBodyData = null;
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
	private Map<String,String> dynamicSettingsMap;
	private Map<String,String> dropDownMap;
	private String actionPerformer = null;
	private String actionAttribute = null;
	private List<String> dynamicRuleAlgorithmLabels;
	private List<String> dynamicRuleAlgorithmCombo;
	private List<String> dynamicRuleAlgorithmField1;
	private List<String> dynamicRuleAlgorithmField2;
	private List<Object> dynamicVariableList;
	private List<String> dataTypeList;
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
//	private String actionDictHeader = null;
//	private String actionDictType = null;
//	private String actionDictUrl = null;
//	private String actionDictMethod = null;
	private String serviceType = null;
	private String uuid = null;
	private String location = null;
    private String priority = null;
	private Map<String,String> brmsParamBody=null;
	private EntityManagerFactory entityManagerFactory = null;
	private Boolean policyExists = false;
	private String policyScope;
	private String providerComboBox = null;
	private String riskType;
	private String guard;
	private String riskLevel;
	private String ttlDate;
	

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
	public Map<String,String> getDynamicFieldConfigAttributes() {
		return dynamicFieldConfigAttributes;
	}
	public void setDynamicFieldConfigAttributes(
			Map<String,String> dynamicFieldConfigAttributes) {
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
	public String getUserGitPath() {
		return gitPath;
	}
	public void setUserGitPath(String gitPath) {
		this.gitPath = gitPath;
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
	public void setDynamicRuleAlgorithmLabels(
			List<String> dynamicRuleAlgorithmLabels) {
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
	public void setDynamicRuleAlgorithmField1(
			List<String> dynamicRuleAlgorithmField1) {
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
	public void setDynamicRuleAlgorithmField2(
			List<String> dynamicRuleAlgorithmField2) {
		this.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
	}
	public Map<String,String> getDropDownMap() {
		return dropDownMap;
	}
	public void setDropDownMap(Map<String,String> dropDownMap) {
		this.dropDownMap = dropDownMap;
	}
/*	public String getActionDictHeader() {
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
	}*/
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
		this. policyScope=domainDir;
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
}
