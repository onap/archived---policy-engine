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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.MicroServiceModels;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.std.IdentifierImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

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

public class MicroServiceConfigPolicy extends Policy {

    private static final Logger LOGGER = FlexLogger.getLogger(MicroServiceConfigPolicy.class);

    private static Map<String, String> mapAttribute = new HashMap<>();
    private static Map<String, String> mapMatch = new HashMap<>();

    private static synchronized Map<String, String> getMatchMap () {
        return mapMatch;
    }

    private static synchronized void setMatchMap(Map<String, String> mm) {
        mapMatch = mm;
    }

    public MicroServiceConfigPolicy() {
        super();
    }

    public MicroServiceConfigPolicy(PolicyRestAdapter policyAdapter){
        this.policyAdapter = policyAdapter;
    }

    //save configuration of the policy based on the policyname
    private void saveConfigurations(String policyName, String jsonBody) {
            if(policyName.endsWith(".xml")){
                policyName = policyName.replace(".xml", "");
            }
        try (PrintWriter out = new PrintWriter(CONFIG_HOME + File.separator + policyName +".json")){
            out.println(jsonBody);
        } catch (Exception e) {
            LOGGER.error("Exception Occured While writing Configuration data"+e);
        }
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

            //setup values for pulling out matching attributes
            ObjectMapper mapper = new ObjectMapper();
            String matching = null;
            Map<String, String> matchMap = null;
            try {
                JsonNode rootNode = mapper.readTree(policyAdapter.getJsonBody());
                if (policyAdapter.getTtlDate()==null){
                    policyAdapter.setTtlDate("NA");
                }
                if (policyAdapter.getServiceType().contains("-v")){
                    matching = getValueFromDictionary(policyAdapter.getServiceType());
                } else {
                    String jsonVersion  = StringUtils.replaceEach(rootNode.get("version").toString(), new String[]{"\""}, new String[]{""});
                    matching = getValueFromDictionary(policyAdapter.getServiceType() + "-v" + jsonVersion);
                }
                if (matching != null && !matching.isEmpty()){
                    matchMap = Splitter.on(",").withKeyValueSeparator("=").split(matching);
            setMatchMap(matchMap);
                    if(policyAdapter.getJsonBody() != null){
                        pullMatchValue(rootNode);           
                    }
                }
            } catch (IOException e1) {
                throw new PAPException(e1);
            }
            
            // Match for policyName
            allOfOne.getMatch().add(createMatch("PolicyName", name));

            AllOfType allOf = new AllOfType();

            // Adding the matches to AllOfType element Match for Onap
            allOf.getMatch().add(createMatch("ONAPName", policyAdapter.getOnapName()));
            if (matchMap==null || matchMap.isEmpty()){
                // Match for ConfigName
                allOf.getMatch().add(createMatch("ConfigName", policyAdapter.getConfigName()));
                // Match for Service
                allOf.getMatch().add(createDynamicMatch("service", policyAdapter.getServiceType()));
                // Match for uuid
                allOf.getMatch().add(createDynamicMatch("uuid", policyAdapter.getUuid()));
                // Match for location
                allOf.getMatch().add(createDynamicMatch("location", policyAdapter.getLocation()));
            }else {
                for (Entry<String, String> matchValue : matchMap.entrySet()){
                    String value = matchValue.getValue();
                    String key = matchValue.getKey().trim();
                    if (value.contains("matching-true")){
                        if (mapAttribute.containsKey(key)){
                            allOf.getMatch().add(createDynamicMatch(key, mapAttribute.get(key)));
                        }
                    }
                }
            }
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
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "MicroServiceConfigPolicy", "Exception creating ACCESS URI");
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
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "MicroServiceConfigPolicy", "Exception creating Config URI");
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

    private void pullMatchValue(JsonNode rootNode) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
        String newValue = null;
           while (fieldsIterator.hasNext()) {
               Map.Entry<String, JsonNode> field = fieldsIterator.next();
               final String key = field.getKey();
               final JsonNode value = field.getValue();
               if (value.isContainerNode() && !value.isArray()) {
                   pullMatchValue(value); // RECURSIVE CALL
               } else {
                   newValue = StringUtils.replaceEach(value.toString(), new String[]{"[", "]", "\""}, new String[]{"", "", ""});
                   mapAttribute.put(key, newValue);
               }
           }
       
   }

   private String getValueFromDictionary(String service){
       String ruleTemplate=null;
       String modelName = service.split("-v")[0];
       String modelVersion = service.split("-v")[1];
       
       CommonClassDaoImpl dbConnection = new CommonClassDaoImpl();
       List<Object> result = dbConnection.getDataById(MicroServiceModels.class, "modelName:version", modelName+":"+modelVersion);
       if(result != null && !result.isEmpty()){
           MicroServiceModels model = (MicroServiceModels) result.get(0);
           ruleTemplate = model.getAnnotation();
       }
       return ruleTemplate;
   }
   
    // Data required for Advice part is setting here.
    private AdviceExpressionsType getAdviceExpressions(int version, String fileName) {
        AdviceExpressionsType advices = new AdviceExpressionsType();
        AdviceExpressionType advice = new AdviceExpressionType();
        advice.setAdviceId("MSID");
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
        String configName;
        if(policyName.endsWith(".xml")){
            configName = policyName.replace(".xml", "");
        }else{
            configName = policyName;
        }
        String content = CONFIG_URL +"/Config/" + configName + ".json";
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

        AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
        assignment7.setAttributeId("matching:service");
        assignment7.setCategory(CATEGORY_RESOURCE);
        assignment7.setIssuer("");
 
        AttributeValueType configNameAttributeValue7 = new AttributeValueType();
        configNameAttributeValue7.setDataType(STRING_DATATYPE);
        configNameAttributeValue7.getContent().add(policyAdapter.getServiceType());
        assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));
 
        advice.getAttributeAssignmentExpression().add(assignment7);

        Map<String, String> matchMap = getMatchMap();
        if (matchMap==null || matchMap.isEmpty()){
            AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
            assignment6.setAttributeId("matching:" + CONFIGID);
            assignment6.setCategory(CATEGORY_RESOURCE);
            assignment6.setIssuer("");

            AttributeValueType configNameAttributeValue6 = new AttributeValueType();
            configNameAttributeValue6.setDataType(STRING_DATATYPE);
            configNameAttributeValue6.getContent().add(policyAdapter.getConfigName());
            assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));

            advice.getAttributeAssignmentExpression().add(assignment6);


            AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
            assignment8.setAttributeId("matching:uuid");
            assignment8.setCategory(CATEGORY_RESOURCE);
            assignment8.setIssuer("");

            AttributeValueType configNameAttributeValue8 = new AttributeValueType();
            configNameAttributeValue8.setDataType(STRING_DATATYPE);
            configNameAttributeValue8.getContent().add(policyAdapter.getUuid());
            assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

            advice.getAttributeAssignmentExpression().add(assignment8);

            AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
            assignment9.setAttributeId("matching:Location");
            assignment9.setCategory(CATEGORY_RESOURCE);
            assignment9.setIssuer("");

            AttributeValueType configNameAttributeValue9 = new AttributeValueType();
            configNameAttributeValue9.setDataType(STRING_DATATYPE);
            configNameAttributeValue9.getContent().add(policyAdapter.getLocation());
            assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

            advice.getAttributeAssignmentExpression().add(assignment9);
        } else {
            for (Entry<String, String> matchValue : matchMap.entrySet()){
                String value = matchValue.getValue();
                String key = matchValue.getKey().trim();
                if (value.contains("matching-true")){
                    if (mapAttribute.containsKey(key)){
                        AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
                        assignment9.setAttributeId("matching:" + key);
                        assignment9.setCategory(CATEGORY_RESOURCE);
                        assignment9.setIssuer("");
                
                        AttributeValueType configNameAttributeValue9 = new AttributeValueType();
                        configNameAttributeValue9.setDataType(STRING_DATATYPE);
                        configNameAttributeValue9.getContent().add(mapAttribute.get(key));
                        assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));
                
                        advice.getAttributeAssignmentExpression().add(assignment9);
 
                    }
                }
            }
        }
        
        AttributeAssignmentExpressionType assignment10 = new AttributeAssignmentExpressionType();
        assignment10.setAttributeId("Priority");
        assignment10.setCategory(CATEGORY_RESOURCE);
        assignment10.setIssuer("");

        AttributeValueType configNameAttributeValue10 = new AttributeValueType();
        configNameAttributeValue10.setDataType(STRING_DATATYPE);
        configNameAttributeValue10.getContent().add(policyAdapter.getPriority());
        assignment10.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue10));

        advice.getAttributeAssignmentExpression().add(assignment10);

        //Risk Attributes
        AttributeAssignmentExpressionType assignment11 = new AttributeAssignmentExpressionType();
        assignment11.setAttributeId("RiskType");
        assignment11.setCategory(CATEGORY_RESOURCE);
        assignment11.setIssuer("");

        AttributeValueType configNameAttributeValue11 = new AttributeValueType();
        configNameAttributeValue11.setDataType(STRING_DATATYPE);
        configNameAttributeValue11.getContent().add(policyAdapter.getRiskType());
        assignment11.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue11));

        advice.getAttributeAssignmentExpression().add(assignment11);

        AttributeAssignmentExpressionType assignment12 = new AttributeAssignmentExpressionType();
        assignment12.setAttributeId("RiskLevel");
        assignment12.setCategory(CATEGORY_RESOURCE);
        assignment12.setIssuer("");

        AttributeValueType configNameAttributeValue12 = new AttributeValueType();
        configNameAttributeValue12.setDataType(STRING_DATATYPE);
        configNameAttributeValue12.getContent().add(policyAdapter.getRiskLevel());
        assignment12.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue12));

        advice.getAttributeAssignmentExpression().add(assignment12);

        AttributeAssignmentExpressionType assignment13 = new AttributeAssignmentExpressionType();
        assignment13.setAttributeId("guard");
        assignment13.setCategory(CATEGORY_RESOURCE);
        assignment13.setIssuer("");

        AttributeValueType configNameAttributeValue13 = new AttributeValueType();
        configNameAttributeValue13.setDataType(STRING_DATATYPE);
        configNameAttributeValue13.getContent().add(policyAdapter.getGuard());
        assignment13.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue13));

        advice.getAttributeAssignmentExpression().add(assignment13);

        AttributeAssignmentExpressionType assignment14 = new AttributeAssignmentExpressionType();
        assignment14.setAttributeId("TTLDate");
        assignment14.setCategory(CATEGORY_RESOURCE);
        assignment14.setIssuer("");

        AttributeValueType configNameAttributeValue14 = new AttributeValueType();
        configNameAttributeValue14.setDataType(STRING_DATATYPE);
        configNameAttributeValue14.getContent().add(policyAdapter.getTtlDate());
        assignment14.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue14));

        advice.getAttributeAssignmentExpression().add(assignment14);

        advices.getAdviceExpression().add(advice);
        return advices;
    }

    @Override
    public Object getCorrectPolicyDataObject() {
        return policyAdapter.getPolicyData();
    }
}
