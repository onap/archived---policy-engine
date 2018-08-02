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

package org.onap.policy.pap.xacml.rest.components;


import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.std.IdentifierImpl;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class CreateClosedLoopPerformanceMetrics extends Policy {

    private static final Logger LOGGER	= FlexLogger.getLogger(CreateClosedLoopPerformanceMetrics.class);

    public CreateClosedLoopPerformanceMetrics() {
        super();
    }

    public CreateClosedLoopPerformanceMetrics(PolicyRestAdapter policyAdapter){
        this.policyAdapter = policyAdapter;
    }

    //save configuration of the policy based on the policyname
    private void saveConfigurations(String policyName, final String jsonBody) {

            if(policyName.endsWith(".xml")){
                policyName   = policyName.substring(0, policyName.lastIndexOf(".xml"));
            }
        try (PrintWriter out = new PrintWriter(CONFIG_HOME + File.separator + "."+ policyName +".json")){
            out.println(jsonBody);
            policyAdapter.setJsonBody(jsonBody);
            policyAdapter.setConfigBodyData(jsonBody);
        } catch (Exception e) {
            LOGGER.error("Exception Occured"+e);
        }
    }

    //getting the policy name and setting to configuration on adding .json
    private String getConfigFile(String filename) {
        filename = FilenameUtils.removeExtension(filename);
        if (filename.endsWith(".xml")) {
            filename = filename.substring(0, filename.length() - 4);
        }
        filename = filename +".json";
        return filename;
    }

    @Override
    public Map<String, String> savePolicies() throws PAPException {

        Map<String, String> successMap = new HashMap<>();
        if(isPolicyExists()){
            successMap.put("EXISTS", "This Policy already exist on the PAP");
            return successMap;
        }

        if(!isPreparedToSave()){
            //Prep and configure the policy for saving
            prepareToSave();
        }

        // Until here we prepared the data and here calling the method to create xml.
        Path newPolicyPath = null;
        newPolicyPath = Paths.get(policyAdapter.getNewFileName());

        successMap = createPolicy(newPolicyPath,getCorrectPolicyDataObject());

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
        if (policyAdapter.getPolicyType().equals("Config")) {
            PolicyType policyConfig = new PolicyType();

            policyConfig.setVersion(Integer.toString(version));
            policyConfig.setPolicyId(policyID);
            policyConfig.setTarget(new TargetType());
            policyAdapter.setData(policyConfig);
        }
        policyName = policyAdapter.getNewFileName();
        if (policyAdapter.getData() != null) {
            // Save the Configurations file with the policy name with extention based on selection.
            String jsonBody = policyAdapter.getJsonBody();
            saveConfigurations(policyName, jsonBody);

            // Make sure the filename ends with an extension
            if (policyName.endsWith(".xml") == false) {
                policyName = policyName + ".xml";
            }


            PolicyType configPolicy = (PolicyType) policyAdapter.getData();

            configPolicy.setDescription(policyAdapter.getPolicyDescription());

            configPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());

            AllOfType allOfOne = new AllOfType();
            String fileName = policyAdapter.getNewFileName();
            String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
            if ((name == null) || (name.equals(""))) {
                name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            }
            allOfOne.getMatch().add(createMatch("PolicyName", name));
            AllOfType allOf = new AllOfType();

            // Adding the matches to AllOfType element Match for Onap
            allOf.getMatch().add(createMatch("ONAPName", policyAdapter.getOnapName()));
            // Match for riskType
            allOf.getMatch().add(
                    createDynamicMatch("RiskType", policyAdapter.getRiskType()));
            // Match for riskLevel
            allOf.getMatch().add(
                    createDynamicMatch("RiskLevel", String.valueOf(policyAdapter.getRiskLevel())));
            // Match for riskguard
            allOf.getMatch().add(
                    createDynamicMatch("guard", policyAdapter.getGuard()));
            // Match for ttlDate
            allOf.getMatch().add(
                    createDynamicMatch("TTLDate", policyAdapter.getTtlDate()));
            // Match for ServiceType
            allOf.getMatch().add(createMatch("ServiceType", policyAdapter.getServiceType()));

            AnyOfType anyOf = new AnyOfType();
            anyOf.getAllOf().add(allOfOne);
            anyOf.getAllOf().add(allOf);

            TargetType target = new TargetType();
            ((TargetType) target).getAnyOf().add(anyOf);

            // Adding the target to the policy element
            configPolicy.setTarget((TargetType) target);

            RuleType rule = new RuleType();
            rule.setRuleId(policyAdapter.getRuleID());

            rule.setEffect(EffectType.PERMIT);

            // Create Target in Rule
            AllOfType allOfInRule = new AllOfType();

            // Creating match for ACCESS in rule target
            MatchType accessMatch = new MatchType();
            AttributeValueType accessAttributeValue = new AttributeValueType();
            accessAttributeValue.setDataType(STRING_DATATYPE);
            accessAttributeValue.getContent().add("ACCESS");
            accessMatch.setAttributeValue(accessAttributeValue);
            AttributeDesignatorType accessAttributeDesignator = new AttributeDesignatorType();
            URI accessURI = null;
            try {
                accessURI = new URI(ACTION_ID);
            } catch (URISyntaxException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "CreateClosedLoopPerformanceMetrics", "Exception creating ACCESS URI");
            }
            accessAttributeDesignator.setCategory(CATEGORY_ACTION);
            accessAttributeDesignator.setDataType(STRING_DATATYPE);
            accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
            accessMatch.setAttributeDesignator(accessAttributeDesignator);
            accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

            // Creating Config Match in rule Target
            MatchType configMatch = new MatchType();
            AttributeValueType configAttributeValue = new AttributeValueType();
            configAttributeValue.setDataType(STRING_DATATYPE);
            configAttributeValue.getContent().add("Config");
            configMatch.setAttributeValue(configAttributeValue);
            AttributeDesignatorType configAttributeDesignator = new AttributeDesignatorType();
            URI configURI = null;
            try {
                configURI = new URI(RESOURCE_ID);
            } catch (URISyntaxException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "CreateClosedLoopPerformanceMetrics", "Exception creating Config URI");
            }
            configAttributeDesignator.setCategory(CATEGORY_RESOURCE);
            configAttributeDesignator.setDataType(STRING_DATATYPE);
            configAttributeDesignator.setAttributeId(new IdentifierImpl(configURI).stringValue());
            configMatch.setAttributeDesignator(configAttributeDesignator);
            configMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

            allOfInRule.getMatch().add(accessMatch);
            allOfInRule.getMatch().add(configMatch);

            AnyOfType anyOfInRule = new AnyOfType();
            anyOfInRule.getAllOf().add(allOfInRule);

            TargetType targetInRule = new TargetType();
            targetInRule.getAnyOf().add(anyOfInRule);

            rule.setTarget(targetInRule);
            rule.setAdviceExpressions(getAdviceExpressions(version, policyName));

            configPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
            policyAdapter.setPolicyData(configPolicy);

        } else {
            PolicyLogger.error("Unsupported data object." + policyAdapter.getData().getClass().getCanonicalName());
        }
        setPreparedToSave(true);
        return true;
    }

    // Data required for Advice part is setting here.
    @SuppressWarnings("static-access")
    private AdviceExpressionsType getAdviceExpressions(int version, String fileName) {
        AdviceExpressionsType advices = new AdviceExpressionsType();
        AdviceExpressionType advice = new AdviceExpressionType();
        advice.setAdviceId("PMID");
        advice.setAppliesTo(EffectType.PERMIT);
        // For Configuration
        AttributeAssignmentExpressionType assignment1 = new AttributeAssignmentExpressionType();
        assignment1.setAttributeId("type");
        assignment1.setCategory(CATEGORY_RESOURCE);
        assignment1.setIssuer("");

        AttributeValueType configNameAttributeValue = new AttributeValueType();
        configNameAttributeValue.setDataType(STRING_DATATYPE);
        configNameAttributeValue.getContent().add("Configuration");
        assignment1.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue));

        advice.getAttributeAssignmentExpression().add(assignment1);
        // For Config file Url if configurations are provided.
        AttributeAssignmentExpressionType assignment2 = new AttributeAssignmentExpressionType();
        assignment2.setAttributeId("URLID");
        assignment2.setCategory(CATEGORY_RESOURCE);
        assignment2.setIssuer("");

        AttributeValueType AttributeValue = new AttributeValueType();
        AttributeValue.setDataType(URI_DATATYPE);
        String content = CONFIG_URL +"/Config/"+ getConfigFile(policyName);
        AttributeValue.getContent().add(content);
        assignment2.setExpression(new ObjectFactory().createAttributeValue(AttributeValue));

        advice.getAttributeAssignmentExpression().add(assignment2);
        AttributeAssignmentExpressionType assignment3 = new AttributeAssignmentExpressionType();
        assignment3.setAttributeId("PolicyName");
        assignment3.setCategory(CATEGORY_RESOURCE);
        assignment3.setIssuer("");

        AttributeValueType attributeValue3 = new AttributeValueType();
        attributeValue3.setDataType(STRING_DATATYPE);
        fileName = FilenameUtils.removeExtension(fileName);
        fileName = fileName + ".xml";
        String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
        if ((name == null) || (name.equals(""))) {
            name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        }
        attributeValue3.getContent().add(name);
        assignment3.setExpression(new ObjectFactory().createAttributeValue(attributeValue3));
        advice.getAttributeAssignmentExpression().add(assignment3);

        AttributeAssignmentExpressionType assignment4 = new AttributeAssignmentExpressionType();
        assignment4.setAttributeId("VersionNumber");
        assignment4.setCategory(CATEGORY_RESOURCE);
        assignment4.setIssuer("");

        AttributeValueType configNameAttributeValue4 = new AttributeValueType();
        configNameAttributeValue4.setDataType(STRING_DATATYPE);
        configNameAttributeValue4.getContent().add(Integer.toString(version));
        assignment4.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue4));

        advice.getAttributeAssignmentExpression().add(assignment4);

        AttributeAssignmentExpressionType assignment5 = new AttributeAssignmentExpressionType();
        assignment5.setAttributeId("matching:" + this.ONAPID);
        assignment5.setCategory(CATEGORY_RESOURCE);
        assignment5.setIssuer("");

        AttributeValueType configNameAttributeValue5 = new AttributeValueType();
        configNameAttributeValue5.setDataType(STRING_DATATYPE);
        configNameAttributeValue5.getContent().add(policyAdapter.getOnapName());
        assignment5.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue5));

        advice.getAttributeAssignmentExpression().add(assignment5);

        AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
        assignment6.setAttributeId("matching:" + this.CLOSEDLOOPID);
        assignment6.setCategory(CATEGORY_RESOURCE);
        assignment6.setIssuer("");

        AttributeValueType configNameAttributeValue6 = new AttributeValueType();
        configNameAttributeValue6.setDataType(STRING_DATATYPE);
        configNameAttributeValue6.getContent().add(policyAdapter.getServiceType());
        assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));

        advice.getAttributeAssignmentExpression().add(assignment6);

        //Risk Attributes
        AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
        assignment7.setAttributeId("RiskType");
        assignment7.setCategory(CATEGORY_RESOURCE);
        assignment7.setIssuer("");

        AttributeValueType configNameAttributeValue7 = new AttributeValueType();
        configNameAttributeValue7.setDataType(STRING_DATATYPE);
        configNameAttributeValue7.getContent().add(policyAdapter.getRiskType());
        assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));

        advice.getAttributeAssignmentExpression().add(assignment7);

        AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
        assignment8.setAttributeId("RiskLevel");
        assignment8.setCategory(CATEGORY_RESOURCE);
        assignment8.setIssuer("");

        AttributeValueType configNameAttributeValue8 = new AttributeValueType();
        configNameAttributeValue8.setDataType(STRING_DATATYPE);
        configNameAttributeValue8.getContent().add(policyAdapter.getRiskLevel());
        assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

        advice.getAttributeAssignmentExpression().add(assignment8);

        AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
        assignment9.setAttributeId("guard");
        assignment9.setCategory(CATEGORY_RESOURCE);
        assignment9.setIssuer("");

        AttributeValueType configNameAttributeValue9 = new AttributeValueType();
        configNameAttributeValue9.setDataType(STRING_DATATYPE);
        configNameAttributeValue9.getContent().add(policyAdapter.getGuard());
        assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

        advice.getAttributeAssignmentExpression().add(assignment9);

        AttributeAssignmentExpressionType assignment10 = new AttributeAssignmentExpressionType();
        assignment10.setAttributeId("TTLDate");
        assignment10.setCategory(CATEGORY_RESOURCE);
        assignment10.setIssuer("");

        AttributeValueType configNameAttributeValue10 = new AttributeValueType();
        configNameAttributeValue10.setDataType(STRING_DATATYPE);
        configNameAttributeValue10.getContent().add(policyAdapter.getTtlDate());
        assignment10.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue10));

        advice.getAttributeAssignmentExpression().add(assignment10);

        advices.getAdviceExpression().add(advice);
        return advices;
    }

    @Override
    public Object getCorrectPolicyDataObject() {
        return policyAdapter.getPolicyData();
    }

}
