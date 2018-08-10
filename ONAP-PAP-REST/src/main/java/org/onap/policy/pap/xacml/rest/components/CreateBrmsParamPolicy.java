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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.SimpleBindings;

import org.apache.commons.io.FilenameUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.controller.BRMSDictionaryController;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.BRMSParamTemplate;

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

public class CreateBrmsParamPolicy extends Policy {

    private static final Logger LOGGER = FlexLogger.getLogger(CreateBrmsParamPolicy.class);

    public CreateBrmsParamPolicy() {
        super();
    }

    public CreateBrmsParamPolicy(PolicyRestAdapter policyAdapter) {
        this.policyAdapter = policyAdapter;
        this.policyAdapter.setConfigType(policyAdapter.getConfigType());

    }

    public String expandConfigBody(String ruleContents, Map<String, String> brmsParamBody) {

        Map<String, String> copyMap = new HashMap<>();
        copyMap.putAll(brmsParamBody);
        copyMap.put("policyName", policyName.substring(0, policyName.replace(".xml", "").lastIndexOf('.')));
        copyMap.put("policyScope", policyAdapter.getDomainDir());
        copyMap.put("policyVersion", policyAdapter.getHighestVersion().toString());
        copyMap.put("unique", ("p" + policyName + UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", ""));

        //Finding all the keys in the Map data-structure.
        Iterator<String> iterator = copyMap.keySet().iterator();
        Pattern p;
        Matcher m;
        while (iterator.hasNext()) {
            //Converting the first character of the key into a lower case.
            String input = iterator.next();
            String output = Character.toLowerCase(input.charAt(0)) +
                    (input.length() > 1 ? input.substring(1) : "");
            //Searching for a pattern in the String using the key.
            p = Pattern.compile("\\$\\{" + output + "\\}");
            m = p.matcher(ruleContents);
            //Replacing the value with the inputs provided by the user in the editor.
            String finalInput = copyMap.get(input);
            if (finalInput.contains("$")) {
                finalInput = finalInput.replace("$", "\\$");
            }
            ruleContents = m.replaceAll(finalInput);
        }
        return ruleContents;
    }


    // Utility to read json data from the existing file to a string
    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    // Saving the Configurations file at server location for config policy.
    protected void saveConfigurations(String policyName, String ruleBody) {
        if (policyName.endsWith(".xml")) {
            policyName = policyName.substring(0, policyName.lastIndexOf(".xml"));
        }
        try (PrintWriter out = new PrintWriter(CONFIG_HOME + File.separator + policyName + ".txt")) {
            String expandedBody = expandConfigBody(ruleBody, policyAdapter.getBrmsParamBody());
            out.println(expandedBody);
            policyAdapter.setJsonBody(expandedBody);
            policyAdapter.setConfigBodyData(expandedBody);
            out.close();
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "CreateBrmsParamPolicy",
                    "Exception saving configuration file");
        }
    }


    // Here we are adding the extension for the configurations file based on the
    // config type selection for saving.
    private String getConfigFile(String filename) {
        filename = FilenameUtils.removeExtension(filename);
        if (filename.endsWith(".txt")) {
            filename = filename.substring(0, filename.length() - 3);
        }

        filename = filename + ".txt";
        return filename;
    }

    // Validations for Config form
    public boolean validateConfigForm() {

        // Validating mandatory Fields.
        isValidForm = true;
        return isValidForm;

    }

    @Override
    public Map<String, String> savePolicies() throws PAPException {

        Map<String, String> successMap = new HashMap<>();
        if (isPolicyExists()) {
            successMap.put("EXISTS", "This Policy already exist on the PAP");
            return successMap;
        }

        if (!isPreparedToSave()) {
            prepareToSave();
        }
        // Until here we prepared the data and here calling the method to create
        // xml.
        Path newPolicyPath = null;
        newPolicyPath = Paths.get(policyAdapter.getNewFileName());
        successMap = createPolicy(newPolicyPath, getCorrectPolicyDataObject());
        if (successMap == null) {
            successMap = new HashMap<>();
            PolicyLogger.error("Failed to Update the Database Dictionary Tables.");
            successMap.put("error", "DB UPDATE");
        }
        return successMap;
    }

    private String getValueFromDictionary(String templateName) {
        String ruleTemplate = null;
        CommonClassDaoImpl dbConnection = new CommonClassDaoImpl();
        String queryString = "from BRMSParamTemplate where param_template_name= :templateName";
        SimpleBindings params = new SimpleBindings();
        params.put("templateName", templateName);
        List<Object> result = dbConnection.getDataByQuery(queryString, params);
        if (!result.isEmpty()) {
            BRMSParamTemplate template = (BRMSParamTemplate) result.get(0);
            ruleTemplate = template.getRule();
        }
        return ruleTemplate;
    }

    protected Map<String, String> findType(String rule) {
        Map<String, String> mapFieldType = new HashMap<>();
        if (rule != null) {
            try {
                StringBuilder params = new StringBuilder();
                Boolean flag = false;
                Boolean comment = false;
                String lines[] = rule.split("\n");
                for (String line : lines) {
                    if (line.isEmpty() || line.startsWith("//")) {
                        continue;
                    }
                    if (line.startsWith("/*")) {
                        comment = true;
                        continue;
                    }
                    if (line.contains("//") && !(line.contains("http://") || line.contains("https://"))) {
                        line = line.split("\\/\\/")[0];
                    }
                    if (line.contains("/*")) {
                        comment = true;
                        if (line.contains("*/")) {
                            try {
                                comment = false;
                                line = line.split("\\/\\*")[0]
                                        + line.split("\\*\\/")[1].replace("*/", "");
                            } catch (Exception e) {
                                LOGGER.debug(e);
                                line = line.split("\\/\\*")[0];
                            }
                        } else {
                            line = line.split("\\/\\*")[0];
                        }
                    }
                    if (line.contains("*/")) {
                        comment = false;
                        try {
                            line = line.split("\\*\\/")[1].replace("*/", "");
                        } catch (Exception e) {
                            LOGGER.debug(e);
                            line = "";
                        }
                    }
                    if (comment) {
                        continue;
                    }
                    if (flag) {
                        params.append(line);
                    }
                    if (line.contains("declare Params")) {
                        params.append(line);
                        flag = true;
                    }
                    if (line.contains("end") && flag) {
                        break;
                    }
                }
                String param = params.toString().replace("declare Params", "").replace("end", "")
                        .replaceAll("\\s+", "");
                String[] components = param.split(":");
                String caption = "";
                for (int i = 0; i < components.length; i++) {
                    String type = "";
                    if (i == 0) {
                        caption = components[i];
                    }
                    if (caption.equals("")) {
                        break;
                    }
                    String nextComponent = "";
                    try {
                        nextComponent = components[i + 1];
                    } catch (Exception e) {
                        LOGGER.debug(e);
                        nextComponent = components[i];
                    }
                    //If the type is of type String then we add the UI Item and type to the map.
                    if (nextComponent.startsWith("String")) {
                        type = "String";
                        mapFieldType.put(caption, type);
                        caption = nextComponent.replace("String", "");
                    } else if (nextComponent.startsWith("int")) {
                        type = "int";
                        mapFieldType.put(caption, type);
                        caption = nextComponent.replace("int", "");
                    }
                }
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "CreateBrmsParamPolicy",
                        "Exception parsing file in findType");
            }
        }
        return mapFieldType;
    }

    // This is the method for preparing the policy for saving. We have broken it
    // out
    // separately because the fully configured policy is used for multiple
    // things
    @Override
    public boolean prepareToSave() throws PAPException {

        if (isPreparedToSave()) {
            // we have already done this
            return true;
        }

        int version = 0;
        String policyID = policyAdapter.getPolicyID();
        version = policyAdapter.getHighestVersion();

        // Create the Instance for pojo, PolicyType object is used in
        // marshalling.
        if (policyAdapter.getPolicyType().equals("Config")) {
            PolicyType policyConfig = new PolicyType();

            policyConfig.setVersion(Integer.toString(version));
            policyConfig.setPolicyId(policyID);
            policyConfig.setTarget(new TargetType());
            policyAdapter.setData(policyConfig);
        }

        policyName = policyAdapter.getNewFileName();

        if (policyAdapter.getData() != null) {
            Map<String, String> ruleAndUIValue = policyAdapter.getBrmsParamBody();
            String templateValue = ruleAndUIValue.get("templateName");
            String valueFromDictionary = getValueFromDictionary(templateValue);

            StringBuilder body = new StringBuilder();

            try {
                body.append(
                        "/* Autogenerated Code Please Don't change/remove this comment section. This is for the UI " +
                                "purpose. \n\t " +
                                "<$%BRMSParamTemplate=" + templateValue + "%$> \n");
                body.append("<%$Values=");
                for (Map.Entry<String, String> entry : ruleAndUIValue.entrySet()) {
                    String uiKey = entry.getKey();
                    if (!"templateName".equals(uiKey)) {
                        body.append(uiKey + ":-:" + entry.getValue() + ":|:");
                    }
                }
                body.append("$%> \n*/ \n");
                body.append(valueFromDictionary + "\n");
            } catch (Exception e) {
                PolicyLogger
                        .error(MessageCodes.ERROR_PROCESS_FLOW, e, "CreateBrmsParamPolicy", "Exception saving policy");
            }

            saveConfigurations(policyName, body.toString());

            // Make sure the filename ends with an extension
            if (!policyName.endsWith(".xml")) {
                policyName = policyName + ".xml";
            }

            PolicyType configPolicy = (PolicyType) policyAdapter.getData();

            configPolicy.setDescription(policyAdapter.getPolicyDescription());

            configPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());

            AllOfType allOfOne = new AllOfType();

            String fileName = policyAdapter.getNewFileName();
            String name = fileName.substring(fileName.lastIndexOf("\\") + 1);
            if ((name == null) || (name.equals(""))) {
                name = fileName.substring(fileName.lastIndexOf("/") + 1
                );
            }
            allOfOne.getMatch().add(createMatch("PolicyName", name));


            AllOfType allOf = new AllOfType();

            // Match for ONAPName
            allOf.getMatch().add(createMatch("ONAPName", policyAdapter.getOnapName()));
            allOf.getMatch().add(createMatch("ConfigName", policyAdapter.getConfigName()));
            // Match for riskType
            allOf.getMatch().add(createDynamicMatch("RiskType", policyAdapter.getRiskType()));
            // Match for riskLevel
            allOf.getMatch().add(createDynamicMatch("RiskLevel", String.valueOf(policyAdapter.getRiskLevel())));
            // Match for riskguard
            allOf.getMatch().add(createDynamicMatch("guard", policyAdapter.getGuard()));
            // Match for ttlDate
            allOf.getMatch().add(createDynamicMatch("TTLDate", policyAdapter.getTtlDate()));
            AnyOfType anyOf = new AnyOfType();
            anyOf.getAllOf().add(allOfOne);
            anyOf.getAllOf().add(allOf);

            TargetType target = new TargetType();
            target.getAnyOf().add(anyOf);

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
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "CreateBrmsParamPolicy",
                        "Exception creating ACCESS URI");
            }
            accessAttributeDesignator.setCategory(CATEGORY_ACTION);
            accessAttributeDesignator.setDataType(STRING_DATATYPE);
            accessAttributeDesignator.setAttributeId(new IdentifierImpl(
                    accessURI).stringValue());
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
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "CreateBrmsParamPolicy",
                        "Exception creating Config URI");
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

            configPolicy
                    .getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
                    .add(rule);
            policyAdapter.setPolicyData(configPolicy);

        } else {
            PolicyLogger.error("Unsupported data object."
                    + policyAdapter.getData().getClass().getCanonicalName());
        }
        setPreparedToSave(true);
        return true;
    }

    // Data required for Advice part is setting here.
    private AdviceExpressionsType getAdviceExpressions(int version,
                                                       String fileName) {

        //Policy Config ID Assignment
        AdviceExpressionsType advices = new AdviceExpressionsType();
        AdviceExpressionType advice = new AdviceExpressionType();
        advice.setAdviceId("BRMSPARAMID");
        advice.setAppliesTo(EffectType.PERMIT);
        // For Configuration
        AttributeAssignmentExpressionType assignment1 = new AttributeAssignmentExpressionType();
        assignment1.setAttributeId("type");
        assignment1.setCategory(CATEGORY_RESOURCE);
        assignment1.setIssuer("");
        AttributeValueType configNameAttributeValue = new AttributeValueType();
        configNameAttributeValue.setDataType(STRING_DATATYPE);
        configNameAttributeValue.getContent().add("Configuration");
        assignment1.setExpression(new ObjectFactory()
                .createAttributeValue(configNameAttributeValue));
        advice.getAttributeAssignmentExpression().add(assignment1);

        // For Config file Url if configurations are provided.
        // URL ID Assignment
        AttributeAssignmentExpressionType assignment2 = new AttributeAssignmentExpressionType();
        assignment2.setAttributeId("URLID");
        assignment2.setCategory(CATEGORY_RESOURCE);
        assignment2.setIssuer("");
        AttributeValueType attributeValue = new AttributeValueType();
        attributeValue.setDataType(URI_DATATYPE);

        String content = CONFIG_URL + "/Config/" + getConfigFile(policyName);

        attributeValue.getContent().add(content);
        assignment2.setExpression(new ObjectFactory()
                .createAttributeValue(attributeValue));
        advice.getAttributeAssignmentExpression().add(assignment2);

        // Policy Name Assignment
        AttributeAssignmentExpressionType assignment3 = new AttributeAssignmentExpressionType();
        assignment3.setAttributeId("PolicyName");
        assignment3.setCategory(CATEGORY_RESOURCE);
        assignment3.setIssuer("");
        AttributeValueType attributeValue3 = new AttributeValueType();
        attributeValue3.setDataType(STRING_DATATYPE);
        fileName = FilenameUtils.removeExtension(fileName);
        fileName = fileName + ".xml";
        String name = fileName.substring(fileName.lastIndexOf("\\") + 1
        );
        if ((name == null) || (name.equals(""))) {
            name = fileName.substring(fileName.lastIndexOf("/") + 1
            );
        }
        attributeValue3.getContent().add(name);
        assignment3.setExpression(new ObjectFactory()
                .createAttributeValue(attributeValue3));
        advice.getAttributeAssignmentExpression().add(assignment3);

        // Version Number Assignment
        AttributeAssignmentExpressionType assignment4 = new AttributeAssignmentExpressionType();
        assignment4.setAttributeId("VersionNumber");
        assignment4.setCategory(CATEGORY_RESOURCE);
        assignment4.setIssuer("");
        AttributeValueType configNameAttributeValue4 = new AttributeValueType();
        configNameAttributeValue4.setDataType(STRING_DATATYPE);
        configNameAttributeValue4.getContent().add(Integer.toString(version));
        assignment4.setExpression(new ObjectFactory()
                .createAttributeValue(configNameAttributeValue4));
        advice.getAttributeAssignmentExpression().add(assignment4);

        // Onap Name Assignment
        AttributeAssignmentExpressionType assignment5 = new AttributeAssignmentExpressionType();
        assignment5.setAttributeId("matching:" + ONAPID);
        assignment5.setCategory(CATEGORY_RESOURCE);
        assignment5.setIssuer("");
        AttributeValueType configNameAttributeValue5 = new AttributeValueType();
        configNameAttributeValue5.setDataType(STRING_DATATYPE);
        configNameAttributeValue5.getContent().add(policyAdapter.getOnapName());
        assignment5.setExpression(new ObjectFactory()
                .createAttributeValue(configNameAttributeValue5));
        advice.getAttributeAssignmentExpression().add(assignment5);


        //Config Name Assignment
        AttributeAssignmentExpressionType assignment6 = new AttributeAssignmentExpressionType();
        assignment6.setAttributeId("matching:" + CONFIGID);
        assignment6.setCategory(CATEGORY_RESOURCE);
        assignment6.setIssuer("");
        AttributeValueType configNameAttributeValue6 = new AttributeValueType();
        configNameAttributeValue6.setDataType(STRING_DATATYPE);
        configNameAttributeValue6.getContent().add(policyAdapter.getConfigName());
        assignment6.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue6));
        advice.getAttributeAssignmentExpression().add(assignment6);
        // Adding Controller Information. 
        if (policyAdapter.getBrmsController() != null) {
            BRMSDictionaryController brmsDicitonaryController = new BRMSDictionaryController();
            advice.getAttributeAssignmentExpression().add(
                    createResponseAttributes("controller:" + policyAdapter.getBrmsController(),
                            brmsDicitonaryController.getControllerDataByID(policyAdapter.getBrmsController())
                                    .getController()));
        }

        // Adding Dependencies. 
        if (policyAdapter.getBrmsDependency() != null) {
            BRMSDictionaryController brmsDicitonaryController = new BRMSDictionaryController();
            ArrayList<String> dependencies = new ArrayList<>();
            StringBuilder key = new StringBuilder();
            for (String dependencyName : policyAdapter.getBrmsDependency()) {
                dependencies.add(brmsDicitonaryController.getDependencyDataByID(dependencyName).getDependency());
                key.append(dependencyName + ",");
            }
            advice.getAttributeAssignmentExpression().add(
                    createResponseAttributes("dependencies:" + key.toString(), dependencies.toString()));
        }

        // Dynamic Field Config Attributes. 
        Map<String, String> dynamicFieldConfigAttributes = policyAdapter.getDynamicFieldConfigAttributes();
        for (Entry<String, String> map : dynamicFieldConfigAttributes.entrySet()) {
            advice.getAttributeAssignmentExpression()
                    .add(createResponseAttributes("key:" + map.getKey(), map.getValue()));
        }

        //Risk Attributes
        AttributeAssignmentExpressionType assignment8 = new AttributeAssignmentExpressionType();
        assignment8.setAttributeId("RiskType");
        assignment8.setCategory(CATEGORY_RESOURCE);
        assignment8.setIssuer("");

        AttributeValueType configNameAttributeValue8 = new AttributeValueType();
        configNameAttributeValue8.setDataType(STRING_DATATYPE);
        configNameAttributeValue8.getContent().add(policyAdapter.getRiskType());
        assignment8.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue8));

        advice.getAttributeAssignmentExpression().add(assignment8);

        AttributeAssignmentExpressionType assignment9 = new AttributeAssignmentExpressionType();
        assignment9.setAttributeId("RiskLevel");
        assignment9.setCategory(CATEGORY_RESOURCE);
        assignment9.setIssuer("");

        AttributeValueType configNameAttributeValue9 = new AttributeValueType();
        configNameAttributeValue9.setDataType(STRING_DATATYPE);
        configNameAttributeValue9.getContent().add(policyAdapter.getRiskLevel());
        assignment9.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue9));

        advice.getAttributeAssignmentExpression().add(assignment9);

        AttributeAssignmentExpressionType assignment10 = new AttributeAssignmentExpressionType();
        assignment10.setAttributeId("guard");
        assignment10.setCategory(CATEGORY_RESOURCE);
        assignment10.setIssuer("");

        AttributeValueType configNameAttributeValue10 = new AttributeValueType();
        configNameAttributeValue10.setDataType(STRING_DATATYPE);
        configNameAttributeValue10.getContent().add(policyAdapter.getGuard());
        assignment10.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue10));

        advice.getAttributeAssignmentExpression().add(assignment10);

        AttributeAssignmentExpressionType assignment11 = new AttributeAssignmentExpressionType();
        assignment11.setAttributeId("TTLDate");
        assignment11.setCategory(CATEGORY_RESOURCE);
        assignment11.setIssuer("");

        AttributeValueType configNameAttributeValue11 = new AttributeValueType();
        configNameAttributeValue11.setDataType(STRING_DATATYPE);
        configNameAttributeValue11.getContent().add(policyAdapter.getTtlDate());
        assignment11.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue11));

        advice.getAttributeAssignmentExpression().add(assignment11);

        advices.getAdviceExpression().add(advice);
        return advices;
    }

    @Override
    public Object getCorrectPolicyDataObject() {
        return policyAdapter.getData();
    }

    private AttributeAssignmentExpressionType createResponseAttributes(String key, String value) {
        AttributeAssignmentExpressionType assignment7 = new AttributeAssignmentExpressionType();
        assignment7.setAttributeId(key);
        assignment7.setCategory(CATEGORY_RESOURCE);
        assignment7.setIssuer("");
        AttributeValueType configNameAttributeValue7 = new AttributeValueType();
        configNameAttributeValue7.setDataType(STRING_DATATYPE);
        configNameAttributeValue7.getContent().add(value);
        assignment7.setExpression(new ObjectFactory().createAttributeValue(configNameAttributeValue7));
        return assignment7;
    }
}
