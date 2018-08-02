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

package org.onap.policy.pap.xacml.rest.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.FunctionDefinition;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.api.pap.PAPException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType; 

public class ActionPolicy extends Policy {
    
    /**
     * ActionPolicy Fields
     */
    private static final Logger LOGGER = FlexLogger.getLogger(ActionPolicy.class);
    
    public static final String JSON_CONFIG = "JSON";
    
    public static final String PDP_ACTION = "PDP";
    public static final String PEP_ACTION = "PEP";
    public static final String TYPE_ACTION = "REST";

    public static final String GET_METHOD = "GET";
    public static final String PUT_METHOD = "PUT";
    public static final String POST_METHOD = "POST";

    public static final String PERFORMER_ATTRIBUTEID = "performer";
    public static final String TYPE_ATTRIBUTEID = "type";
    public static final String METHOD_ATTRIBUTEID = "method";
    public static final String HEADERS_ATTRIBUTEID = "headers";
    public static final String URL_ATTRIBUTEID = "url";
    public static final String BODY_ATTRIBUTEID = "body";
    
    List<String> dynamicLabelRuleAlgorithms = new LinkedList<>();
    List<String> dynamicFieldFunctionRuleAlgorithms = new LinkedList<>();
    List<String> dynamicFieldOneRuleAlgorithms = new LinkedList<>();
    List<String> dynamicFieldTwoRuleAlgorithms = new LinkedList<>();
    

    
    private CommonClassDao commonClassDao;
    
    private static boolean isAttribute = false;
    private synchronized static boolean getAttribute () {
        return isAttribute;

    }
    
    public ActionPolicy() {
        super();
    }
    
    public ActionPolicy(PolicyRestAdapter policyAdapter, CommonClassDao commonClassDao){
        this.policyAdapter = policyAdapter;
        this.commonClassDao = commonClassDao;
    }

    @Override
    public Map<String, String> savePolicies() throws PAPException {
        
        Map<String, String> successMap = new HashMap<>();
        if(isPolicyExists()){
            successMap.put("EXISTS", "This Policy already exist on the PAP");
            return successMap;
        }
        
        if(!ActionPolicy.getAttribute()) {
            successMap.put("invalidAttribute", "Action Attrbute was not in the database.");
            return successMap;
        }
        
        if(!isPreparedToSave()){
            //Prep and configure the policy for saving
            prepareToSave();
        }

        // Until here we prepared the data and here calling the method to create xml.
        Path newPolicyPath = null;
        newPolicyPath = Paths.get(policyAdapter.getNewFileName());
        successMap = createPolicy(newPolicyPath,getCorrectPolicyDataObject() );     
        return successMap;      
    }
    
    //This is the method for preparing the policy for saving.  We have broken it out
    //separately because the fully configured policy is used for multiple things
    @Override
    public boolean prepareToSave() throws PAPException{

        if(isPreparedToSave()){
            //we have already done this
            return true;
        }
        
        int version = 0;
        String policyID = policyAdapter.getPolicyID();
        version = policyAdapter.getHighestVersion();
        
        // Create the Instance for pojo, PolicyType object is used in marshalling.
        if (policyAdapter.getPolicyType().equals("Action")) {
            PolicyType policyConfig = new PolicyType();

            policyConfig.setVersion(Integer.toString(version));
            policyConfig.setPolicyId(policyID);
            policyConfig.setTarget(new TargetType());
            policyAdapter.setData(policyConfig);
        }
        
        policyName = policyAdapter.getNewFileName();
        
        if (policyAdapter.getData() != null) {
            // Action body is optional so checking value provided or not
            String comboDictValue = policyAdapter.getActionAttribute();
            String actionBody = policyAdapter.getActionBody();
            setAttribute(false);

            //if actionBody is null or empty then we know the ActionAttribute in the request does not exist in the dictionary
            if(!(actionBody==null || "".equals(actionBody))){   
                saveActionBody(policyName, actionBody);
                setAttribute(true);
            } else {
                if(!getAttribute()){
                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Could not find " + comboDictValue + " in the ActionPolicyDict table.");
                    return false;
                }
            }
            
            PolicyType actionPolicy = (PolicyType) policyAdapter.getData();
            actionPolicy.setDescription(policyAdapter.getPolicyDescription());
            actionPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());

            AllOfType allOf = new AllOfType();
            
            Map<String, String> dynamicFieldComponentAttributes = policyAdapter.getDynamicFieldConfigAttributes();
            
            // If there is any dynamic field attributes create the matches here
            for (String keyField : dynamicFieldComponentAttributes.keySet()) {
                String key = keyField;
                String value = dynamicFieldComponentAttributes.get(key);
                MatchType dynamicMatch = createDynamicMatch(key, value);
                allOf.getMatch().add(dynamicMatch);
            }

            AnyOfType anyOf = new AnyOfType();
            anyOf.getAllOf().add(allOf);

            TargetType target = new TargetType();
            target.getAnyOf().add(anyOf);
            
            // Adding the target to the policy element
            actionPolicy.setTarget(target);
            
            RuleType rule = new RuleType();
            rule.setRuleId(policyAdapter.getRuleID());

            rule.setEffect(EffectType.PERMIT);
            rule.setTarget(new TargetType());
            
            dynamicLabelRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmLabels();
            dynamicFieldFunctionRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmCombo();
            dynamicFieldOneRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmField1();
            dynamicFieldTwoRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmField2();
                        
            // Rule attributes are optional and dynamic so check and add them to condition.
            if (dynamicLabelRuleAlgorithms != null && !dynamicLabelRuleAlgorithms.isEmpty()) {
                boolean isCompound = false;
                ConditionType condition = new ConditionType();
                int index = dynamicFieldOneRuleAlgorithms.size() - 1;

                for (String labelAttr : dynamicLabelRuleAlgorithms) {
                    // if the rule algorithm as a label means it is a compound
                    if (dynamicFieldOneRuleAlgorithms.get(index).equals(labelAttr)) {
                        ApplyType actionApply = new ApplyType();

                        String selectedFunction = dynamicFieldFunctionRuleAlgorithms.get(index).toString();
                        String value1 = dynamicFieldOneRuleAlgorithms.get(index);
                        String value2 = dynamicFieldTwoRuleAlgorithms.get(index);
                        actionApply.setFunctionId(getFunctionDefinitionId(selectedFunction));
                        actionApply.getExpression().add(new ObjectFactory().createApply(getInnerActionApply(value1)));
                        actionApply.getExpression().add(new ObjectFactory().createApply(getInnerActionApply(value2)));
                        condition.setExpression(new ObjectFactory().createApply(actionApply));
                        isCompound = true;
                    }
                }
                // if rule algorithm not a compound
                if (!isCompound) {
                    condition.setExpression(new ObjectFactory().createApply(getInnerActionApply(dynamicLabelRuleAlgorithms.get(index).toString())));
                }
                rule.setCondition(condition);
            }
            // set the obligations to rule
            rule.setObligationExpressions(getObligationExpressions());
            actionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
            policyAdapter.setPolicyData(actionPolicy);
        }  else {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Unsupported data object." + policyAdapter.getData().getClass().getCanonicalName());
        }   

        setPreparedToSave(true);
        return true;
    }
    
    private static synchronized void setAttribute(boolean b) {
        isAttribute = b;
    }

    // Saving the json Configurations file if exists at server location for action policy.
    private void saveActionBody(String policyName, String actionBodyData) {
            if(policyName.endsWith(".xml")){
                policyName = policyName.replace(".xml", "");
            }
            File file = new File(ACTION_HOME+ File.separator + policyName + ".json");
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
            bw.write(actionBodyData);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Action Body is succesfully saved at " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.error("Exception Occured"+e);
        }
    }
    
    // Data required for obligation part is setting here.
    private ObligationExpressionsType getObligationExpressions() {
        ObligationExpressionsType obligations = new ObligationExpressionsType();

        ObligationExpressionType obligation = new ObligationExpressionType();
        String comboDictValue = policyAdapter.getActionAttribute();
        obligation.setObligationId(comboDictValue);
        obligation.setFulfillOn(EffectType.PERMIT);

        // Add Action Assignment:
        AttributeAssignmentExpressionType assignment1 = new AttributeAssignmentExpressionType();
        assignment1.setAttributeId(PERFORMER_ATTRIBUTEID);
        assignment1.setCategory(CATEGORY_RECIPIENT_SUBJECT);

        AttributeValueType actionNameAttributeValue = new AttributeValueType();
        actionNameAttributeValue.setDataType(STRING_DATATYPE);
        actionNameAttributeValue.getContent().add(performer.get(policyAdapter.getActionPerformer()));

        assignment1.setExpression(new ObjectFactory().createAttributeValue(actionNameAttributeValue));
        obligation.getAttributeAssignmentExpression().add(assignment1);

        // Add Type Assignment:
        AttributeAssignmentExpressionType assignmentType = new AttributeAssignmentExpressionType();
        assignmentType.setAttributeId(TYPE_ATTRIBUTEID);
        assignmentType.setCategory(CATEGORY_RESOURCE);

        AttributeValueType typeAttributeValue = new AttributeValueType();
        typeAttributeValue.setDataType(STRING_DATATYPE);
        String actionDictType = policyAdapter.getActionDictType();
        typeAttributeValue.getContent().add(actionDictType);

        assignmentType.setExpression(new ObjectFactory().createAttributeValue(typeAttributeValue));
        obligation.getAttributeAssignmentExpression().add(assignmentType);

        // Add Rest_URL Assignment:
        AttributeAssignmentExpressionType assignmentURL = new AttributeAssignmentExpressionType();
        assignmentURL.setAttributeId(URL_ATTRIBUTEID);
        assignmentURL.setCategory(CATEGORY_RESOURCE);

        AttributeValueType actionURLAttributeValue = new AttributeValueType();
        actionURLAttributeValue.setDataType(URI_DATATYPE);
        String actionDictUrl = policyAdapter.getActionDictUrl();
        actionURLAttributeValue.getContent().add(actionDictUrl);

        assignmentURL.setExpression(new ObjectFactory().createAttributeValue(actionURLAttributeValue));
        obligation.getAttributeAssignmentExpression().add(assignmentURL);

        // Add Method Assignment:
        AttributeAssignmentExpressionType assignmentMethod = new AttributeAssignmentExpressionType();
        assignmentMethod.setAttributeId(METHOD_ATTRIBUTEID);
        assignmentMethod.setCategory(CATEGORY_RESOURCE);

        AttributeValueType methodAttributeValue = new AttributeValueType();
        methodAttributeValue.setDataType(STRING_DATATYPE);
        String actionDictMethod = policyAdapter.getActionDictMethod();
        methodAttributeValue.getContent().add(actionDictMethod);

        assignmentMethod.setExpression(new ObjectFactory().createAttributeValue(methodAttributeValue));
        obligation.getAttributeAssignmentExpression().add(assignmentMethod);

        // Add JSON_URL Assignment:
        String actionBody = policyAdapter.getActionBody();      
        if (actionBody != null) {
            AttributeAssignmentExpressionType assignmentJsonURL = new AttributeAssignmentExpressionType();
            assignmentJsonURL.setAttributeId(BODY_ATTRIBUTEID);
            assignmentJsonURL.setCategory(CATEGORY_RESOURCE);

            AttributeValueType jsonURLAttributeValue = new AttributeValueType();
            jsonURLAttributeValue.setDataType(URI_DATATYPE);
            jsonURLAttributeValue.getContent().add(CONFIG_URL + "/Action/"  + policyName + ".json");

            assignmentJsonURL.setExpression(new ObjectFactory().createAttributeValue(jsonURLAttributeValue));
            obligation.getAttributeAssignmentExpression().add(assignmentJsonURL);
        }

        String headerVal = policyAdapter.getActionDictHeader();
        if(headerVal != null && !headerVal.trim().isEmpty()){
            // parse it on : to get number of headers
            String[] result = headerVal.split(":");
            for (String eachString : result){
                // parse each value on =
                String[] textFieldVals = eachString.split("=");
                obligation.getAttributeAssignmentExpression().add(addDynamicHeaders(textFieldVals[0], textFieldVals[1]));
            }
        }
            
        obligations.getObligationExpression().add(obligation);
        return obligations;
    }

    
    // if compound setting the inner apply here
    protected ApplyType getInnerActionApply(String value1Label) {
        ApplyType actionApply = new ApplyType();
        int index = 0;
        // check the index for the label.
        for (String labelAttr : dynamicLabelRuleAlgorithms) {
            if (labelAttr.equals(value1Label)) {
                String value1 = dynamicFieldOneRuleAlgorithms.get(index).toString();
                // check if the row contains label again
                for (String labelValue : dynamicLabelRuleAlgorithms) {
                    if (labelValue.equals(value1)) {
                        return getCompoundApply(index);
                    }
                }

                // Getting the values from the form.
                String functionKey = dynamicFieldFunctionRuleAlgorithms.get(index);
                String value2 = dynamicFieldTwoRuleAlgorithms.get(index);
                actionApply.setFunctionId(getFunctionDefinitionId(functionKey));
                // if two text field are rule attributes.
                if ((value1.contains(RULE_VARIABLE)) && (value2.contains(RULE_VARIABLE))) {
                    ApplyType innerActionApply1 = new ApplyType();
                    ApplyType innerActionApply2 = new ApplyType();
                    AttributeDesignatorType attributeDesignator1 = new AttributeDesignatorType();
                    AttributeDesignatorType attributeDesignator2 = new AttributeDesignatorType();
                    // If selected function is Integer function set integer functionID
                    if (functionKey.toLowerCase().contains("integer")) {
                        innerActionApply1.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
                        innerActionApply2.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
                        attributeDesignator1.setDataType(INTEGER_DATATYPE);
                        attributeDesignator2.setDataType(INTEGER_DATATYPE);
                    } else {
                        // If selected function is not a Integer function
                        // set String functionID
                        innerActionApply1.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
                        innerActionApply2.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
                        attributeDesignator1.setDataType(STRING_DATATYPE);
                        attributeDesignator2.setDataType(STRING_DATATYPE);
                    }
                    attributeDesignator1.setCategory(CATEGORY_RESOURCE);
                    attributeDesignator2.setCategory(CATEGORY_RESOURCE);

                    // Here set actual field values
                    attributeDesignator1.setAttributeId(value1.contains("resource:") ? value1.substring(9): value1.substring(8));
                    attributeDesignator2.setAttributeId(value1.contains("resource:") ? value1.substring(9): value1.substring(8));

                    innerActionApply1.getExpression().add(new ObjectFactory().createAttributeDesignator(attributeDesignator1));
                    innerActionApply2.getExpression().add(new ObjectFactory().createAttributeDesignator(attributeDesignator2));

                    actionApply.getExpression().add(new ObjectFactory().createApply(innerActionApply1));
                    actionApply.getExpression().add(new ObjectFactory().createApply(innerActionApply2));

                } else {// if either of one text field is rule attribute.
                    ApplyType innerActionApply = new ApplyType();
                    AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
                    AttributeValueType actionConditionAttributeValue = new AttributeValueType();

                    if (functionKey.toLowerCase().contains("integer")) {
                        innerActionApply.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
                        actionConditionAttributeValue.setDataType(INTEGER_DATATYPE);
                        attributeDesignator.setDataType(INTEGER_DATATYPE);
                    } else {
                        innerActionApply.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
                        actionConditionAttributeValue.setDataType(STRING_DATATYPE);
                        attributeDesignator.setDataType(STRING_DATATYPE);
                    }

                    String attributeId = null;
                    String attributeValue = null;

                    // Find which textField has rule attribute and set it as
                    attributeId = value1;
                    attributeValue = value2;

                    if (attributeId != null) {
                        attributeDesignator.setCategory(CATEGORY_RESOURCE);
                        attributeDesignator.setAttributeId(attributeId);
                    }
                    actionConditionAttributeValue.getContent().add(attributeValue);
                    innerActionApply.getExpression().add(new ObjectFactory().createAttributeDesignator(attributeDesignator));
                    // Decide the order of element based the values.
                    if (attributeId.equals(value1)) {
                        actionApply.getExpression().add(new ObjectFactory().createApply(innerActionApply));
                        actionApply.getExpression().add(new ObjectFactory().createAttributeValue(actionConditionAttributeValue));
                    } else {
                        actionApply.getExpression().add(new ObjectFactory().createAttributeValue(actionConditionAttributeValue));
                        actionApply.getExpression().add(new ObjectFactory().createApply(innerActionApply));
                    }
                }
            }
            index++;
        }
        return actionApply;
    }

    // if the rule algorithm is multiple compound one setting the apply
    protected ApplyType getCompoundApply(int index) {
        ApplyType actionApply = new ApplyType();
        String selectedFunction = dynamicFieldFunctionRuleAlgorithms.get(index);
        String value1 = dynamicFieldOneRuleAlgorithms.get(index);
        String value2 = dynamicFieldTwoRuleAlgorithms.get(index);
        actionApply.setFunctionId(getFunctionDefinitionId(selectedFunction));
        actionApply.getExpression().add(new ObjectFactory().createApply(getInnerActionApply(value1)));
        actionApply.getExpression().add(new ObjectFactory().createApply(getInnerActionApply(value2)));
        return actionApply;
    }
        
    // Adding the dynamic headers if any
    private AttributeAssignmentExpressionType addDynamicHeaders(String header, String value) {
        AttributeAssignmentExpressionType assignmentHeaders = new AttributeAssignmentExpressionType();
        assignmentHeaders.setAttributeId("headers:" + header);
        assignmentHeaders.setCategory(CATEGORY_RESOURCE);

        AttributeValueType headersAttributeValue = new AttributeValueType();
        headersAttributeValue.setDataType(STRING_DATATYPE);
        headersAttributeValue.getContent().add(value);

        assignmentHeaders.setExpression(new ObjectFactory().createAttributeValue(headersAttributeValue));
        return assignmentHeaders;
    }

    @Override
    public Object getCorrectPolicyDataObject() {
        return policyAdapter.getPolicyData();
    }
    
    public String getFunctionDefinitionId(String key){
        FunctionDefinition object = (FunctionDefinition) commonClassDao.getDataById(FunctionDefinition.class, "short_name", key);
        if(object != null){
            return object.getXacmlid();
        }
        return null;
    }

}