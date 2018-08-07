/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.api;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <code>PolicyParameters</code> defines the Policy Parameters
 *  which are required to Create/Update a Policy. 
 * 
 * @version 0.1
 */
public class PolicyParameters {
    private PolicyClass policyClass;
    private PolicyConfigType policyConfigType;
    private String policyName;
    private String policyDescription;
    private String onapName;
    private String configName;
    private Map<AttributeType, Map<String,String>> attributes;
    private Map<String, String> treatments;
    private String configBody;
    private PolicyType configBodyType;
    private String actionPerformer;
    private String actionAttribute;
    private UUID requestID;
    private List<String> dynamicRuleAlgorithmLabels;
    private List<String> dynamicRuleAlgorithmFunctions;
    private List<String> dynamicRuleAlgorithmField1;
    private List<String> dynamicRuleAlgorithmField2;
    private String priority;
    private RuleProvider ruleProvider;
    private String controllerName;
    private List<String> dependencyNames;
    private Date ttlDate;
    private boolean guard = false;
    private String riskLevel = "5";
    private String riskType = "default";
    private String extendedOption;

    /**
     * Sets Config Policy Parameters.
     *
     * @param policyConfigParams
     */
    public void setConfigPolicyParameters(PolicyConfigParams policyConfigParams){
        this.setPolicyConfigType(policyConfigParams.getPolicyConfigType());
        this.setPolicyName(policyConfigParams.getPolicyName());
        this.setPolicyDescription(policyConfigParams.getPolicyDescription());
        this.setOnapName(policyConfigParams.getOnapName());
        this.setConfigName(policyConfigParams.getConfigName());
        this.setAttributes(policyConfigParams.getAttributes());
        this.setConfigBody(policyConfigParams.getConfigBody());
        this.setConfigBodyType(policyConfigParams.getConfigBodyType());
        this.setRequestID(policyConfigParams.getRequestID());
    }
    
    /**
     * Sets config Firewall Policy Parameters.
     *
     * @param policyName the <code>String</code> format of the Policy Name
     * @param firewallJson the <code>String</code> representation of the Firewall Rules List
     * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
     * A different request ID should be passed for each request.
     */
    public void setConfigFirewallPolicyParameters(String policyName, String firewallJson, UUID requestID){
        this.setPolicyConfigType(PolicyConfigType.Firewall);
        this.setPolicyName(policyName);
        this.setConfigBody(firewallJson);
        this.setConfigBodyType(PolicyType.JSON);
        this.setRequestID(requestID);
    }

    /**
     * Gets the PolicyName of the Policy Parameters.
     *
     * @return policyName the <code>String</code> format of the Policy Name
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Sets the policyName of the Policy Parameters.
     *
     * @param policyName the <code>String</code> format of the Policy Name
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * Gets the policy Description.
     *
     * @return the <code>String</code> format of the Policy Description
     */
    public String getPolicyDescription() {
        return policyDescription;
    }

    /**
     * Sets the policy Description of the Policy Description.
     *
     * @param policyDescription the <code>String</code> format of the Policy Description
     */
    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    /**
     * Gets the ONAP Name value of the Policy Paramters.
     *
     * @return <code>String</code> format of the ONAP Name
     */
    public String getOnapName() {
        return onapName;
    }

    /**
     * Gets the ONAP Name value of the Policy Paramters. 
     * 
     * @return <code>String</code> format of the ONAP Name
     * @deprecated Use {@link #getOnapName()} instead. 
     */
    @Deprecated
    public String getEcompName() {
        return onapName;
    }

    /**
     * Sets the ONAP Name field of the Policy Parameters.
     *
     * @param onapName the <code>String</code> format of the ONAP Name
     */
    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }

    /**
     * Sets the ONAP Name field of the Policy Parameters. 
     * 
     * @param ecompName the <code>String</code> format of the ONAP Name
     * @deprecated use {@link #setOnapName(String)} instead. 
     */
    @Deprecated
    public void setEcompName(String ecompName) {
        this.onapName = ecompName;
    }
    

    /**
     * Gets the Config Name value of the Policy Parameters.
     *
     * @return <code>String</code> format of the Config Name
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * Sets the Config Name field of the Policy Parameters.
     *
     * @param configName the <code>String</code> format of the Config Name
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * Gets the Attributes of the policy Parameters.
     *
     * @return <code>List</code> the <code>Map</code> Attributes that must contain the AttributeType and Map of key,value pairs corresponding to it.
     */
    public Map<AttributeType, Map<String, String>> getAttributes() {
        return attributes;
    }

    /**
     * Sets the Attributes of the Policy Parameters.
     *
     * @param attributes the <code>Map</code> Attributes that must contain the AttributeType and Map of key,value pairs corresponding to it.
     */
    public void setAttributes(Map<AttributeType, Map<String, String>> attributes) {
        this.attributes = attributes;
    }

    /**
     * Gets the Policy Config Type value the Policy parameters.
     *
     * @return {@link org.onap.policy.api.PolicyConfigType} Enum of the Config Type
     */
    public PolicyConfigType getPolicyConfigType() {
        return policyConfigType;
    }

    /**
     * Sets the Policy Config Type field of the policy Parameters.
     *
     * @param policyConfigType the {@link org.onap.policy.api.PolicyConfigType} Enum format of the Config Type
     */
    public void setPolicyConfigType(PolicyConfigType policyConfigType) {
        if(policyConfigType!=null){
            setPolicyClass(PolicyClass.Config);
        }
        this.policyConfigType = policyConfigType;
    }

    /**
     * Gets the configBody value of the Policy Parameters.
     *
     * @return the <code>String</code> format of the Policy Body
     */
    public String getConfigBody() {
        return configBody;
    }

    /**
     * Sets the configBody field of the Policy Parameters.
     *
     * @param configBody the <code>String</code> format of the Policy Body
     */
    public void setConfigBody(String configBody) {
        this.configBody = configBody;
    }

    /**
     * Gets the config Body Type value of the Policy Parameters.
     *
     * @return the <code>PolicyType</code> representation of the configBodyType
     */
    public PolicyType getConfigBodyType() {
        return configBodyType;
    }

    /**
     * Sets the configBodyType field of the Policy Parameters.
     *
     * @param configBodyType the <code>PolicyType</code> representation of the config BodyType
     */
    public void setConfigBodyType(PolicyType configBodyType) {
        this.configBodyType = configBodyType;
    }

    /**
     * Gets the requestID of the Policy Parameters.
     *
     * @return unique request ID which will be passed throughout the ONAP components to correlate logging messages.
     */
    public UUID getRequestID() {
        return requestID;
    }

    /**
     * Sets the requestID of the Policy Parameters.
     *
     * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
     */
    public void setRequestID(UUID requestID) {
        this.requestID = requestID;
    }

    /**
     * Gets the Policy Class of the Policy Parameters.
     *
     * @return {@link org.onap.policy.api.PolicyClass} of the Policy Parameters.
     */
    public PolicyClass getPolicyClass() {
        return policyClass;
    }

    /**
     * Sets the Policy Class of the Policy Parameters.
     *
     * @param policyClass the Enum {@link org.onap.policy.api.PolicyClass} to set Policy Class Type of Policy parameters.
     */
    public void setPolicyClass(PolicyClass policyClass) {
        this.policyClass = policyClass;
    }

    /**
     * Gets the Action Performer value of the Policy Parameters for Action Policies.
     *
     * @return the <code>String</code> value of the Action Performer for Action Policies
     */
    public String getActionPerformer() {
        return actionPerformer;
    }

    /**
     * Sets the Action Performer value of the Policy Parameters for Action Policies.
     *
     * @param actionPerformer the <code>String</code> format of the Action Performer
     */
    public void setActionPerformer(String actionPerformer) {
        this.actionPerformer = actionPerformer;
    }

    /**
     * Gets the Action Attribute value of the Policy Parameters for Action Policies.
     *
     * @return the <code>String</code> value of the Action Attribute for Action Policies
     */
    public String getActionAttribute() {
        return actionAttribute;
    }

    /**
     * Sets the Action Attribute value of the Policy Parameters for Action Policies.
     *
     * @param actionAttribute the <code>String</code> format of the Action Attribute
     */
    public void setActionAttribute(String actionAttribute) {
        this.actionAttribute = actionAttribute;
    }

    /**
     * Gets the Dynamic Rule Algorithm Label of the policy Parameters. Used in conjunction with the Label, Field1,
     * Function, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @return <code>List</code> the Dynamic Rule Algorithm Label that must contain the Labels in order
     */
    public List<String> getDynamicRuleAlgorithmLabels() {
        return dynamicRuleAlgorithmLabels;
    }

    /**
     * Sets the Dynamic Rule Algorithm Labels used in conjunction with the Label, Field1,
     * Function, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @param dynamicRuleAlgorithmLabels the <code>List</code> dynamicRuleAlgoritmLabels in order
     */
    public void setDynamicRuleAlgorithmLabels(
            List<String> dynamicRuleAlgorithmLabels) {
        this.dynamicRuleAlgorithmLabels = dynamicRuleAlgorithmLabels;
    }

    /**
     * Gets the Dynamic Rule Algorithm Function of the policy Parameters. Used in conjunction with the Label, Field1,
     * FunctionDef, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @return <code>List</code> the Dynamic Rule Algorithm Functions that must contain the values in order
     */
    public List<String> getDynamicRuleAlgorithmFunctions() {
        return dynamicRuleAlgorithmFunctions;
    }

    /**
     * Sets the Dynamic Rule Algorithm Functions used in conjunction with the Label, Field1,
     * Function, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @param dynamicRuleAlgorithmFunctions the <code>List</code> dynamicRuleAlgorithmFunctions in order
     */
    public void setDynamicRuleAlgorithmFunctions(List<String> dynamicRuleAlgorithmFunctions) {
        this.dynamicRuleAlgorithmFunctions = dynamicRuleAlgorithmFunctions;
    }

    /**
     * Gets the Dynamic Rule Algorithm Field1 of the policy Parameters. Used in conjunction with the Label, Field1,
     * Function, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @return <code>List</code> the Dynamic Rule Algorithm Field1 that must contain the Field1 values in order
     */
    public List<String> getDynamicRuleAlgorithmField1() {
        return dynamicRuleAlgorithmField1;
    }

    /**
     * Sets the Dynamic Rule Algorithm Field1 used in conjunction with the Label, Field1,
     * Function, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @param dynamicRuleAlgorithmField1 the <code>List</code> dynamicRuleAlgorithmField1 in order
     */
    public void setDynamicRuleAlgorithmField1(
            List<String> dynamicRuleAlgorithmField1) {
        this.dynamicRuleAlgorithmField1 = dynamicRuleAlgorithmField1;
    }

    /**
     * Gets the Dynamic Rule Algorithm Field2 of the policy Parameters. Used in conjunction with the Label, Field1,
     * Operator, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @return <code>List</code> the Dynamic Rule Algorithm Field2 that must contain the Field2 values in order
     */
    public List<String> getDynamicRuleAlgorithmField2() {
        return dynamicRuleAlgorithmField2;
    }

    /**
     * Sets the Dynamic Rule Algorithm Field2 used in conjunction with the Label, Field1,
     * Function, and Field2 to complete the complex and simple Rule Algorithms
     *
     * @param dynamicRuleAlgorithmField2 the <code>List</code> dynamicRuleAlgorithmField2 in order
     */
    public void setDynamicRuleAlgorithmField2(
            List<String> dynamicRuleAlgorithmField2) {
        this.dynamicRuleAlgorithmField2 = dynamicRuleAlgorithmField2;
    }

    /**
     * Gets the Priority of the Policy Parameters.
     *
     * @return priority the <code>String</code> format of the Micro Services priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the Priority of the Policy Parameters.
     *
     * @param priority the <code>String</code> format of the Micro Services priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public RuleProvider getRuleProvider() {
        return ruleProvider;
    }

    public void setRuleProvider(RuleProvider ruleProvider) {
        this.ruleProvider = ruleProvider;
    }
    /**
     * Sets the Guard field of the Policy Parameters.
     *
     * @param guard the <code>Boolean</code> format of the guard value
     */
    public void setGuard(boolean guard){
        this.guard = guard;
    }

    /**
     * Gets the  guard value of the Policy Parameters for Action Policies.
     *
     * @return the <code>boolean</code> value of the Guard for Config Policies
     */
    public boolean getGuard(){
        return guard;
    }

    /**
     * Sets the riskType field of the Policy Parameters.
     *
     * @param riskType the <code>String</code> format of the riskType value
     */
    public void setRiskType(String riskType){
        this.riskType = riskType;
    }

    /**
     * Gets the riskType value of the Policy Parameters for Config Policies.
     *
     * @return the <code>String</code> value of the riskType for Config Policies
     */
    public String getRiskType(){
        return riskType;
    }

    /**
     * Sets the riskLevel field of the Policy Parameters.
     *
     * @param riskLevel the <code>String</code> format of the riskType value
     */
    public void setRiskLevel(String riskLevel){
        this.riskLevel = riskLevel;
    }

    /**
     * Gets the riskLevel value of the Policy Parameters for Config Policies.
     *
     * @return the <code>String</code> value of the riskLevel for Config Policies
     */
    public String getRiskLevel(){
        return riskLevel;
    }

    /**
     * Sets the TTLDate field of the Policy Parameters.
     *
     * @param ttlDate the <code>Date</code> format of the TTLDate value
     */
    public void setTtlDate(Date ttlDate){
        this.ttlDate = ttlDate;
    }

    /**
     * Gets the TTLDate value of the Policy Parameters for Config Policies.
     *
     * @return the <code>Date</code> value of the TTLDate for Config Policies
     */
    public Date getTtlDate(){
        return ttlDate;
    }

    /**
     * Gets the Controller Name for your policy.
     *
     * @return String format of the controller Name.
     */
    public String getControllerName() {
        return controllerName;
    }

    /**
     * Sets Controller Name for your policy.
     *
     * @param controllerName to identify the controller information for your policy.
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
    
    /**
     * Gets Dependency Names for your policy. 
     * 
     * @return ArrayList of String(s) format of dependency names.
     */
    public List<String> getDependencyNames() {
        return dependencyNames;
    }

    /**
     * Sets Dependency that your policy is dependent on. 
     * 
     * @param dependencyNames ArrayList of String(s). 
     */
    public void setDependencyNames(List<String> dependencyNames) {
        this.dependencyNames = dependencyNames;
    }

    public String getExtendedOption() {
        return extendedOption;
    }

    public void setExtendedOption(String extendedOption) {
        this.extendedOption = extendedOption;
    }

    /**
     * Gets Allowed Treatments Map for Rainy Day Decision Policy
     * 
     * @return Map of String format for treatments per errorcode
     */
    public Map<String, String> getTreatments() {
        return treatments;
    }

    /**
     * Sets Allowed Treatments Map for Rainy Day Decision Policy
     *
     * @param treatments Map that contains the treatment per errorcode
     */
    public void setTreatments(Map<String, String> treatments) {
        this.treatments = treatments;
    }

    @Override
    public String toString() {
        return "PolicyParameters [ policyName=" + policyName + ", policyDescription=" + policyDescription + ", onapName="+ onapName
                + ", configName=" + configName + ", attributes=" + attributes + ", configBody=" + configBody
                + ",dynamicRuleAlgorithmLabels=" + dynamicRuleAlgorithmLabels + ",dynamicRuleAlgorithmFunctions=" + dynamicRuleAlgorithmFunctions
                + ",dynamicRuleAlgorithmField1=" + dynamicRuleAlgorithmField1 + ",dynamicRuleAlgorithmField2=" + dynamicRuleAlgorithmField2
                + ", actionPerformer=" + actionPerformer + ", actionAttribute=" + actionAttribute + ", priority=" + priority
                + ", ruleProvider= " + ruleProvider + ", riskLevel= " + riskLevel + ", riskType= " + riskType + ", extendedOption= " + extendedOption
                + ", treatments= " + treatments + "]";
    }
}
