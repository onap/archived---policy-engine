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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
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

public class ClosedLoopPolicy extends Policy {

    private static final Logger LOGGER = FlexLogger.getLogger(ClosedLoopPolicy.class);

    public ClosedLoopPolicy() {
        super();
    }

    public ClosedLoopPolicy(PolicyRestAdapter policyAdapter){
        this.policyAdapter = policyAdapter;
    }

    //save configuration of the policy based on the policyname
    private void saveConfigurations(String policyName, String jsonBody) {

            if(policyName.endsWith(".xml")){
                policyName = policyName.replace(".xml", "");
            }
        try (PrintWriter out = new PrintWriter(CONFIG_HOME + File.separator+ policyName +".json")){
            String body = jsonBody;
            //Remove the trapMaxAge in Verification Signature
            body = body.replace(",\"trapMaxAge\":null", "");
            this.policyAdapter.setJsonBody(body);
            out.println(body);
        } catch (Exception e) {
            LOGGER.error("Exception Occured while writing Configuration Data"+e);
        }
    }

    //Utility to read json data from the existing file to a string
    static String readFile(String path, Charset encoding) throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);

    }

    //create the configuration file based on the policy name on adding the extension as .json
    private String getConfigFile(String filename) {
        filename = FilenameUtils.removeExtension(filename);
        if (filename.endsWith(".xml")) {
            filename = filename.substring(0, filename.length() - 4);
        }
        filename = filename + ".json";
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

            PolicyType faultPolicy = (PolicyType) policyAdapter.getData();

            faultPolicy.setDescription(policyAdapter.getPolicyDescription());

            faultPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());

            AllOfType allOfOne = new AllOfType();
            String fileName = policyAdapter.getNewFileName();
            String name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
            if ((name == null) || (name.equals(""))) {
                name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            }
            allOfOne.getMatch().add(createMatch("PolicyName", name));
            AllOfType allOf = new AllOfType();
            // Adding the matches to AllOfType element
            // Match for Onap
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

            AnyOfType anyOf = new AnyOfType();
            anyOf.getAllOf().add(allOfOne);
            anyOf.getAllOf().add(allOf);

            TargetType target = new TargetType();
            ((TargetType) target).getAnyOf().add(anyOf);
            // Adding the target to the policy element
            faultPolicy.setTarget((TargetType) target);

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
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "CreateClosedLoopPolicy", "Exception creating ACCESS URI");
            }
            accessAttributeDesignator.setCategory(CATEGORY_ACTION);
            accessAttributeDesignator.setDataType(STRING_DATATYPE);
            accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
            accessMatch.setAttributeDesignator(accessAttributeDesignator);
            accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

            // Creating Config Match in rule Target
            MatchType closedMatch = new MatchType();
            AttributeValueType closedAttributeValue = new AttributeValueType();
            closedAttributeValue.setDataType(STRING_DATATYPE);
            closedAttributeValue.getContent().add("Config");
            closedMatch.setAttributeValue(closedAttributeValue);
            AttributeDesignatorType closedAttributeDesignator = new AttributeDesignatorType();
            URI closedURI = null;
            try {
                closedURI = new URI(RESOURCE_ID);
            } catch (URISyntaxException e) {
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "CreateClosedLoopPolicy", "Exception creating closed URI");
            }
            closedAttributeDesignator.setCategory(CATEGORY_RESOURCE);
            closedAttributeDesignator.setDataType(STRING_DATATYPE);
            closedAttributeDesignator.setAttributeId(new IdentifierImpl(closedURI).stringValue());
            closedMatch.setAttributeDesignator(closedAttributeDesignator);
            closedMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

            allOfInRule.getMatch().add(accessMatch);
            allOfInRule.getMatch().add(closedMatch);

            AnyOfType anyOfInRule = new AnyOfType();
            anyOfInRule.getAllOf().add(allOfInRule);

            TargetType targetInRule = new TargetType();
            targetInRule.getAnyOf().add(anyOfInRule);

            rule.setTarget(targetInRule);
            rule.setAdviceExpressions(getAdviceExpressions(version, policyName));

            faultPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
            policyAdapter.setPolicyData(faultPolicy);

        } else {
            PolicyLogger.error("Unsupported data object." + policyAdapter.getData().getClass().getCanonicalName());
        }

        setPreparedToSave(true);
        return true;
    }

    // Data required for Advice part is setting here.
    private AdviceExpressionsType getAdviceExpressions(int version, String fileName) {
        AdviceExpressionsType advices = new AdviceExpressionsType();
        AdviceExpressionType advice = new AdviceExpressionType();
        advice.setAdviceId("faultID");
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
        String content = CONFIG_URL +"/Config/" + getConfigFile(policyName);
        System.out.println("URL value :" + content);
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
        assignment5.setAttributeId("matching:" + ONAPID);
        assignment5.setCategory(CATEGORY_RESOURCE);
        assignment5.setIssuer("");

        AttributeValueType configNameAttributeValue5 = new AttributeValueType();
        configNameAttributeValue5.setDataType(STRING_DATATYPE);
        configNameAttributeValue5.getContent().add(policyAdapter.getOnapName());
        assignment5.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue5));

        advice.getAttributeAssignmentExpression().add(assignment5);

        //Risk Attributes
        AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
        assignment6.setAttributeId("RiskType");
        assignment6.setCategory(CATEGORY_RESOURCE);
        assignment6.setIssuer("");

        AttributeValueType configNameAttributeValue6 = new AttributeValueType();
        configNameAttributeValue6.setDataType(STRING_DATATYPE);
        configNameAttributeValue6.getContent().add(policyAdapter.getRiskType());
        assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));

        advice.getAttributeAssignmentExpression().add(assignment6);

        AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
        assignment7.setAttributeId("RiskLevel");
        assignment7.setCategory(CATEGORY_RESOURCE);
        assignment7.setIssuer("");

        AttributeValueType configNameAttributeValue7 = new AttributeValueType();
        configNameAttributeValue7.setDataType(STRING_DATATYPE);
        configNameAttributeValue7.getContent().add(policyAdapter.getRiskLevel());
        assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));

        advice.getAttributeAssignmentExpression().add(assignment7);

        AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
        assignment8.setAttributeId("guard");
        assignment8.setCategory(CATEGORY_RESOURCE);
        assignment8.setIssuer("");

        AttributeValueType configNameAttributeValue8 = new AttributeValueType();
        configNameAttributeValue8.setDataType(STRING_DATATYPE);
        configNameAttributeValue8.getContent().add(policyAdapter.getGuard());
        assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

        advice.getAttributeAssignmentExpression().add(assignment8);

        AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
        assignment9.setAttributeId("TTLDate");
        assignment9.setCategory(CATEGORY_RESOURCE);
        assignment9.setIssuer("");

        AttributeValueType configNameAttributeValue9 = new AttributeValueType();
        configNameAttributeValue9.setDataType(STRING_DATATYPE);
        configNameAttributeValue9.getContent().add(policyAdapter.getTtlDate());
        assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

        advice.getAttributeAssignmentExpression().add(assignment9);



        advices.getAdviceExpression().add(advice);
        return advices;
    }

    @Override
    public Object getCorrectPolicyDataObject() {
        return policyAdapter.getPolicyData();
    }


}
