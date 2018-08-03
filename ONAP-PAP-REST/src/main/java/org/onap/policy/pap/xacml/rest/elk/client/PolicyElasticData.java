/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.elk.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.adapter.YAMLParams;

public class PolicyElasticData {

    private String scope;
    private String policyType;
    private String configPolicyType;
    private String configBodyData;
    private String policyName;
    private String policyDescription;
    private String onapName;
    private String configName;
    private String configType;
    private String jsonBody;
    private Object jsonBodyData;

    private Map<?, ?> serviceTypePolicyName;
    private Map<?, ?> verticaMetrics;
    private Map<?, ?> description;
    private Map<?, ?> attributeFields;

    //Safe Policy
    private String policyScope;
    private String providerComboBox;
    private String riskType;
    private String riskLevel;
    private String guard;
    private String ttlDate;
    private  Map<String,String> matching;

    private List<Object> triggerSignatures;
    private List<Object> symptomSignatures;
    private String logicalConnector;
    private String policyStatus;
    public String gocServerScope;
    private String supressionType;

    //MicroSerice
    private String serviceType;
    private String uuid;
    private String location;
    private String priority;
    private String msLocation;

    //BRMS Policies
    private String ruleName;
    private Map<String,String> brmsParamBody;
    private String brmsController;
    private List<String> brmsDependency;
    private LinkedHashMap<?, ?>  ruleData;
    private LinkedHashMap<?,?>   ruleListData;
    private Map<String,String> drlRuleAndUIParams;

    //ClosedLoop
    private String clearTimeOut;
    private String trapMaxAge;
    private String verificationclearTimeOut;
    public Map<String , String> dynamicLayoutMap;

    //FireWall
    private String fwPolicyType;
    private List<Object> fwattributes;
    private String parentForChild;
    private String securityZone;

    //Action & Decision
    private String ruleCombiningAlgId;
    private Map<String,String> dynamicFieldConfigAttributes;
    private Map<String,String> dynamicSettingsMap;
    private Map<String,String> dropDownMap;
    private String actionPerformer;
    private String actionAttribute;
    private List<String> dynamicRuleAlgorithmLabels;
    private List<String> dynamicRuleAlgorithmCombo;
    private List<String> dynamicRuleAlgorithmField1;
    private List<String> dynamicRuleAlgorithmField2;
    private List<Object> dynamicVariableList;
    private List<String> dataTypeList;
    private String actionAttributeValue;
    private String ruleProvider;
    private String actionBody;
    private String actionDictHeader;
    private String actionDictType;
    private String actionDictUrl;
    private String actionDictMethod;
    private YAMLParams yamlparams;

    public PolicyElasticData(PolicyRestAdapter policyData) {
        this.scope = policyData.getDomainDir();
        this.policyType = policyData.getPolicyType();
        this.configPolicyType = policyData.getConfigPolicyType();
        this.configBodyData = policyData.getConfigBodyData();
        this.policyName  = policyData.getNewFileName();
        this.policyDescription  = policyData.getPolicyDescription();
        this.onapName = policyData.getOnapName();
        this.configName = policyData.getConfigName();
        this.configType = policyData.getConfigType();
        this.jsonBody = policyData.getJsonBody();
        if(configPolicyType.startsWith("ClosedLoop")){
            this.jsonBodyData = jsonBody;
        }else{
            this.jsonBodyData = policyData.getJsonBodyData();
        }

        this.serviceTypePolicyName = policyData.getServiceTypePolicyName();
        this.verticaMetrics = policyData.getVerticaMetrics();
        this.description = policyData.getDescription();
        this.attributeFields = policyData.getAttributeFields();

        //Safe Policy
        this.policyScope = policyData.getPolicyScope();
        this.providerComboBox = policyData.getProviderComboBox();
        this.riskType = policyData.getRiskType();
        this.riskLevel = policyData.getRiskLevel();
        this.guard  = policyData.getGuard();
        this.ttlDate = policyData.getTtlDate();
        this.matching = policyData.getMatching();

        this.triggerSignatures = policyData.getTriggerSignatures();
        this.symptomSignatures = policyData.getSymptomSignatures();
        this.logicalConnector = policyData.getLogicalConnector();
        this.policyStatus = policyData.getPolicyStatus();
        this.gocServerScope = policyData.getGocServerScope();
        this.supressionType = policyData.getSupressionType();

        //MicroSerice
        this.serviceType = policyData.getServiceType();
        this.uuid = policyData.getUuid();
        this.location = policyData.getLocation();
        this.priority = policyData.getPriority();
        this.msLocation = policyData.getMsLocation();

        //BRMS Policies
        this.ruleName = policyData.getRuleName();
        this.brmsParamBody = policyData.getBrmsParamBody();
        this.brmsController = policyData.getBrmsController();
        this.brmsDependency = policyData.getBrmsDependency();
        this.ruleData = policyData.getRuleData();
        this.ruleListData = policyData.getRuleListData();
        this.drlRuleAndUIParams = policyData.getDrlRuleAndUIParams();

        //ClosedLoop
        this.clearTimeOut = policyData.getClearTimeOut();
        this.trapMaxAge = policyData.getTrapMaxAge();
        this.verificationclearTimeOut = policyData.getVerificationclearTimeOut();
        this.dynamicLayoutMap = policyData.getDynamicLayoutMap();

        //FireWall
        this.fwPolicyType = policyData.getFwPolicyType();
        this.fwattributes = policyData.getFwattributes();
        this.parentForChild = policyData.getParentForChild();
        this.securityZone = policyData.getSecurityZone();

        //Action & Decision
        this.ruleCombiningAlgId = policyData.getRuleCombiningAlgId();
        this.dynamicFieldConfigAttributes = policyData.getDynamicFieldConfigAttributes();
        this.dynamicSettingsMap = policyData.getDynamicSettingsMap();
        this.dropDownMap = policyData.getDropDownMap();
        this.actionPerformer = policyData.getActionPerformer();
        this.actionAttribute = policyData.getActionAttribute();
        this.dynamicRuleAlgorithmLabels = policyData.getDynamicRuleAlgorithmLabels();
        this.dynamicRuleAlgorithmCombo = policyData.getDynamicRuleAlgorithmCombo();
        this.dynamicRuleAlgorithmField1 = policyData.getDynamicRuleAlgorithmField1();
        this.dynamicRuleAlgorithmField2 = policyData.getDynamicRuleAlgorithmField2();
        this.dynamicVariableList = policyData.getDynamicVariableList();
        this.dataTypeList = policyData.getDataTypeList();
        this.actionAttributeValue = policyData.getActionAttributeValue();
        this.ruleProvider = policyData.getRuleProvider();
        this.actionBody = policyData.getActionBody();
        this.actionDictHeader = policyData.getActionDictHeader();
        this.actionDictType = policyData.getActionDictType();
        this.actionDictUrl = policyData.getActionDictUrl();
        this.actionDictMethod = policyData.getActionDictMethod();
        this.yamlparams = policyData.getYamlparams();
    }

    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getPolicyType() {
        return policyType;
    }
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
    public String getConfigPolicyType() {
        return configPolicyType;
    }
    public void setConfigPolicyType(String configPolicyType) {
        this.configPolicyType = configPolicyType;
    }
    public String getConfigBodyData() {
        return configBodyData;
    }

    public void setConfigBodyData(String configBodyData) {
        this.configBodyData = configBodyData;
    }
    public String getPolicyName() {
        return policyName;
    }
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
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
    public String getConfigType() {
        return configType;
    }
    public void setConfigType(String configType) {
        this.configType = configType;
    }
    public String getJsonBody() {
        return jsonBody;
    }
    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }
    public Map<?, ?> getServiceTypePolicyName() {
        return serviceTypePolicyName;
    }

    public void setServiceTypePolicyName(LinkedHashMap<?, ?> serviceTypePolicyName) {
        this.serviceTypePolicyName = serviceTypePolicyName;
    }

    public Map<?, ?> getVerticaMetrics() {
        return verticaMetrics;
    }

    public void setVerticaMetrics(LinkedHashMap<?, ?> verticaMetrics) {
        this.verticaMetrics = verticaMetrics;
    }

    public Map<?, ?> getDescription() {
        return description;
    }

    public void setDescription(Map<?, ?> description) {
        this.description = description;
    }

    public Map<?, ?> getAttributeFields() {
        return attributeFields;
    }

    public void setAttributeFields(LinkedHashMap<?, ?> attributeFields) {
        this.attributeFields = attributeFields;
    }
    public String getPolicyScope() {
        return policyScope;
    }
    public void setPolicyScope(String policyScope) {
        this.policyScope = policyScope;
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
    public Map<String, String> getMatching() {
        return matching;
    }
    public void setMatching(Map<String, String> matching) {
        this.matching = matching;
    }
    public List<Object> getTriggerSignatures() {
        return triggerSignatures;
    }
    public void setTriggerSignatures(ArrayList<Object> triggerSignatures) {
        this.triggerSignatures = triggerSignatures;
    }
    public List<Object> getSymptomSignatures() {
        return symptomSignatures;
    }
    public void setSymptomSignatures(ArrayList<Object> symptomSignatures) {
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
    public String getMsLocation() {
        return msLocation;
    }
    public void setMsLocation(String msLocation) {
        this.msLocation = msLocation;
    }
    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public Map<String, String> getBrmsParamBody() {
        return brmsParamBody;
    }
    public void setBrmsParamBody(Map<String, String> brmsParamBody) {
        this.brmsParamBody = brmsParamBody;
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
    public Map<?, ?> getRuleData() {
        return ruleData;
    }
    public void setRuleData(LinkedHashMap<?, ?> ruleData) {
        this.ruleData = ruleData;
    }
    public Map<?, ?> getRuleListData() {
        return ruleListData;
    }
    public void setRuleListData(LinkedHashMap<?, ?> ruleListData) {
        this.ruleListData = ruleListData;
    }
    public Map<String, String> getDrlRuleAndUIParams() {
        return drlRuleAndUIParams;
    }
    public void setDrlRuleAndUIParams(Map<String, String> drlRuleAndUIParams) {
        this.drlRuleAndUIParams = drlRuleAndUIParams;
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
    public void setFwattributes(ArrayList<Object> fwattributes) {
        this.fwattributes = fwattributes;
    }
    public String getParentForChild() {
        return parentForChild;
    }
    public void setParentForChild(String parentForChild) {
        this.parentForChild = parentForChild;
    }
    public String getSecurityZone() {
        return securityZone;
    }
    public void setSecurityZone(String securityZone) {
        this.securityZone = securityZone;
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
    public Map<String, String> getDynamicSettingsMap() {
        return dynamicSettingsMap;
    }
    public void setDynamicSettingsMap(Map<String, String> dynamicSettingsMap) {
        this.dynamicSettingsMap = dynamicSettingsMap;
    }
    public Map<String, String> getDropDownMap() {
        return dropDownMap;
    }
    public void setDropDownMap(Map<String, String> dropDownMap) {
        this.dropDownMap = dropDownMap;
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
    public void setDynamicRuleAlgorithmLabels(List<String> dynamicRuleAlgorithmLabels) {
        this.dynamicRuleAlgorithmLabels = dynamicRuleAlgorithmLabels;
    }
    public List<String> getDynamicRuleAlgorithmCombo() {
        return dynamicRuleAlgorithmCombo;
    }
    public void setDynamicRuleAlgorithmCombo(List<String> dynamicRuleAlgorithmCombo) {
        this.dynamicRuleAlgorithmCombo = dynamicRuleAlgorithmCombo;
    }
    public List<String> getDynamicRuleAlgorithmField1() {
        return dynamicRuleAlgorithmField1;
    }
    public void setDynamicRuleAlgorithmField1(List<String> dynamicRuleAlgorithmField1) {
        this.dynamicRuleAlgorithmField1 = dynamicRuleAlgorithmField1;
    }
    public List<String> getDynamicRuleAlgorithmField2() {
        return dynamicRuleAlgorithmField2;
    }
    public void setDynamicRuleAlgorithmField2(List<String> dynamicRuleAlgorithmField2) {
        this.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
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
    public YAMLParams getYamlparams() {
        return yamlparams;
    }

    public void setYamlparams(YAMLParams yamlparams) {
        this.yamlparams = yamlparams;
    }

    public Object getJsonBodyData() {
        return jsonBodyData;
    }

    public void setJsonBodyData(Object jsonBodyData) {
        this.jsonBodyData = jsonBodyData;
    }
}
