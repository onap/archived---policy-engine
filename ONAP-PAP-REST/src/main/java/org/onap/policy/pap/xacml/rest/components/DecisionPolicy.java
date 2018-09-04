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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringEscapeUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controlloop.policy.builder.BuilderException;
import org.onap.policy.controlloop.policy.builder.Results;
import org.onap.policy.controlloop.policy.guard.Constraint;
import org.onap.policy.controlloop.policy.guard.ControlLoopGuard;
import org.onap.policy.controlloop.policy.guard.Guard;
import org.onap.policy.controlloop.policy.guard.GuardPolicy;
import org.onap.policy.controlloop.policy.guard.MatchParameters;
import org.onap.policy.controlloop.policy.guard.builder.ControlLoopGuardBuilder;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DecisionSettings;
import org.onap.policy.rest.jpa.FunctionDefinition;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.std.pip.engines.aaf.AAFEngine;
import org.onap.policy.xacml.util.XACMLPolicyScanner;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.std.IdentifierImpl;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;

public class DecisionPolicy extends Policy {

    private static final Logger LOGGER = FlexLogger.getLogger(DecisionPolicy.class);

    public static final String FUNCTION_NOT = "urn:oasis:names:tc:xacml:1.0:function:not";
    private static final String AAFPROVIDER = "AAF";
    public static final String GUARD_YAML = "GUARD_YAML";
    public static final String GUARD_BL_YAML = "GUARD_BL_YAML";
    public static final String RAINY_DAY = "Rainy_Day";
    private static final String XACML_GUARD_TEMPLATE = "Decision_GuardPolicyTemplate.xml";
    private static final String XACML_BLGUARD_TEMPLATE = "Decision_GuardBLPolicyTemplate.xml";

    private static final String ONAPNAME = "ONAPName";
    private static final String POLICY_NAME = "PolicyName";
    private static final String DESCRIPTION = "description";


    List<String> dynamicLabelRuleAlgorithms = new LinkedList<>();
    List<String> dynamicFieldComboRuleAlgorithms = new LinkedList<>();
    List<String> dynamicFieldOneRuleAlgorithms = new LinkedList<>();
    List<String> dynamicFieldTwoRuleAlgorithms = new LinkedList<>();
    List<String> dataTypeList = new LinkedList<>();

    private CommonClassDao commonClassDao;

    public DecisionPolicy() {
        super();
    }

    public DecisionPolicy(PolicyRestAdapter policyAdapter, CommonClassDao commonClassDao) {
        this.policyAdapter = policyAdapter;
        this.commonClassDao = commonClassDao;
    }

    @Override
    public Map<String, String> savePolicies() throws PAPException {

        Map<String, String> successMap = new HashMap<>();
        if (isPolicyExists()) {
            successMap.put("EXISTS", "This Policy already exist on the PAP");
            return successMap;
        }

        if (!isPreparedToSave()) {
            // Prep and configure the policy for saving
            prepareToSave();
        }

        // Until here we prepared the data and here calling the method to create xml.
        Path newPolicyPath = null;
        newPolicyPath = Paths.get(policyAdapter.getNewFileName());

        successMap = createPolicy(newPolicyPath, getCorrectPolicyDataObject());
        return successMap;
    }

    // This is the method for preparing the policy for saving. We have broken it out
    // separately because the fully configured policy is used for multiple things
    @Override
    public boolean prepareToSave() throws PAPException {

        if (isPreparedToSave()) {
            // we have already done this
            return true;
        }

        int version = 0;
        String policyID = policyAdapter.getPolicyID();
        version = policyAdapter.getHighestVersion();

        if ("Raw".equals(policyAdapter.getRuleProvider())) {
            Object policy;
            if ("API".equalsIgnoreCase(policyAdapter.getApiflag())) {
                policy = XACMLPolicyScanner.readPolicy(new ByteArrayInputStream(StringEscapeUtils
                        .unescapeXml(policyAdapter.getRawXacmlPolicy()).getBytes(StandardCharsets.UTF_8)));
            } else {
                policy = XACMLPolicyScanner.readPolicy(
                        new ByteArrayInputStream(policyAdapter.getRawXacmlPolicy().getBytes(StandardCharsets.UTF_8)));
            }
            String policyRawDesc;
            if (policy instanceof PolicySetType) {
                policyRawDesc =
                        ((PolicySetType) policy).getDescription() + "@#RuleProvider@#Decision_Raw@#RuleProvider@#";
                ((PolicySetType) policy).setDescription(policyRawDesc);
            } else {
                policyRawDesc = ((PolicyType) policy).getDescription() + "@#RuleProvider@#Decision_Raw@#RuleProvider@#";
                ((PolicyType) policy).setDescription(policyRawDesc);
            }
            policyAdapter.setPolicyData(policy);
            policyAdapter.setData(policy);
            setPreparedToSave(true);
            return true;
        }
        // Create the Instance for pojo, PolicyType object is used in marshalling.
        if ("Decision".equals(policyAdapter.getPolicyType())) {
            PolicyType policyConfig = new PolicyType();

            policyConfig.setVersion(Integer.toString(version));
            policyConfig.setPolicyId(policyID);
            policyConfig.setTarget(new TargetType());
            policyAdapter.setData(policyConfig);
        }
        policyName = policyAdapter.getNewFileName();

        if (policyAdapter.getRuleProvider().equals(GUARD_YAML)
                || policyAdapter.getRuleProvider().equals(GUARD_BL_YAML)) {
            Map<String, String> yamlParams = new HashMap<>();
            String blackListEntryType = policyAdapter.getBlackListEntryType() != null
                    ? policyAdapter.getBlackListEntryType() : "Use Manual Entry";
            String description = policyAdapter.getPolicyDescription() != null ? policyAdapter.getPolicyDescription()
                    : "YAML Guard Policy";
            yamlParams.put(DESCRIPTION, description + "@blEntry@" + blackListEntryType + "@blEntry@");
            String fileName = policyAdapter.getNewFileName();
            String name = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length());
            if ((name == null) || ("".equals(name))) {
                name = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());
            }
            yamlParams.put(POLICY_NAME, name);
            yamlParams.put(ONAPNAME, policyAdapter.getOnapName());
            Map<String, String> params = policyAdapter.getDynamicFieldConfigAttributes();
            yamlParams.putAll(params);
            // Call YAML to XACML
            try {
                PolicyType decisionPolicy = getGuardPolicy(yamlParams, policyAdapter.getRuleProvider());
                decisionPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());
                decisionPolicy.setVersion(Integer.toString(version));
                policyAdapter.setPolicyData(decisionPolicy);
                policyAdapter.setData(decisionPolicy);
            } catch (BuilderException e) {
                LOGGER.error(e);
                throw new PAPException(e);
            }
        } else if (policyAdapter.getData() != null) {
            PolicyType decisionPolicy = (PolicyType) policyAdapter.getData();

            decisionPolicy.setDescription(policyAdapter.getPolicyDescription());

            decisionPolicy.setRuleCombiningAlgId(policyAdapter.getRuleCombiningAlgId());
            AllOfType allOfOne = new AllOfType();
            String fileName = policyAdapter.getNewFileName();
            String name = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length());
            if ((name == null) || ("".equals(name))) {
                name = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());
            }
            allOfOne.getMatch().add(createMatch(POLICY_NAME, name));

            AllOfType allOf = new AllOfType();

            // Match for Onap
            allOf.getMatch().add(createMatch(ONAPNAME, (policyAdapter.getOnapName())));

            Map<String, String> dynamicFieldComponentAttributes = policyAdapter.getDynamicFieldConfigAttributes();
            if (policyAdapter.getRuleProvider() != null && policyAdapter.getRuleProvider().equals(AAFPROVIDER)) {
                dynamicFieldComponentAttributes = new HashMap<>();
            }

            // If there is any dynamic field attributes create the matches here
            for (String keyField : dynamicFieldComponentAttributes.keySet()) {
                String key = keyField;
                String value = dynamicFieldComponentAttributes.get(key);
                MatchType dynamicMatch = createDynamicMatch(key, value);
                allOf.getMatch().add(dynamicMatch);
            }

            AnyOfType anyOf = new AnyOfType();
            anyOf.getAllOf().add(allOfOne);
            anyOf.getAllOf().add(allOf);

            TargetType target = new TargetType();
            target.getAnyOf().add(anyOf);
            decisionPolicy.setTarget(target);

            Map<String, String> dynamicFieldDecisionSettings = policyAdapter.getDynamicSettingsMap();
            if (policyAdapter.getRuleProvider() != null && (policyAdapter.getRuleProvider().equals(AAFPROVIDER)
                    || policyAdapter.getRuleProvider().equals(RAINY_DAY))) {
                dynamicFieldDecisionSettings = new HashMap<>();
            }

            // settings are dynamic so check how many rows are added and add all
            for (String keyField : dynamicFieldDecisionSettings.keySet()) {
                String key = keyField;
                String value = dynamicFieldDecisionSettings.get(key);
                String dataType = getDataType(key);
                VariableDefinitionType dynamicVariable = createDynamicVariable(key, value, dataType);
                decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(dynamicVariable);
            }

            Map<String, String> dynamicFieldTreatmentAttributes = policyAdapter.getRainydayMap();
            if (policyAdapter.getRuleProvider().equals(RAINY_DAY)) {
                for (String keyField : dynamicFieldTreatmentAttributes.keySet()) {
                    String errorcode = keyField;
                    String treatment = dynamicFieldTreatmentAttributes.get(errorcode);
                    createRainydayRule(decisionPolicy, errorcode, treatment, true);
                }
            } else {
                createRule(decisionPolicy, true);
                createRule(decisionPolicy, false);
            }

        }
        setPreparedToSave(true);
        return true;
    }

    public PolicyType getGuardPolicy(Map<String, String> yamlParams, String ruleProvider) throws BuilderException {
        try {
            ControlLoopGuardBuilder builder = ControlLoopGuardBuilder.Factory.buildControlLoopGuard(new Guard());
            MatchParameters matchParameters = new MatchParameters(yamlParams.get("actor"), yamlParams.get("recipe"));
            matchParameters.setControlLoopName(yamlParams.get("clname"));
            if (yamlParams.containsKey("targets")) {
                String targetString = yamlParams.get("targets");
                List<String> targets = null;
                if (targetString != null && !targetString.isEmpty()) {
                    if (targetString.contains(",")) {
                        targets = Arrays.asList(targetString.split(","));
                    } else {
                        targets = new ArrayList<>();
                        targets.add(targetString);
                    }
                }
                matchParameters.setTargets(targets);
            }
            GuardPolicy policy1 = new GuardPolicy(
                    (policyAdapter.getUuid() != null ? policyAdapter.getUuid() : UUID.randomUUID().toString()),
                    yamlParams.get(POLICY_NAME), yamlParams.get(DESCRIPTION), matchParameters);
            builder = builder.addGuardPolicy(policy1);
            Map<String, String> activeTimeRange = new HashMap<>();
            activeTimeRange.put("start", yamlParams.get("guardActiveStart"));
            activeTimeRange.put("end", yamlParams.get("guardActiveEnd"));
            String blackListString = yamlParams.get("blackList");
            List<String> blackList = null;
            if (blackListString != null && !blackListString.trim().isEmpty()) {
                if (blackListString.contains(",")) {
                    blackList = Arrays.asList(blackListString.split(","));
                } else {
                    blackList = new ArrayList<>();
                    blackList.add(blackListString);
                }
            }
            if (yamlParams.containsKey("appendBlackList")) {
                String appendBlackListString = yamlParams.get("appendBlackList");
                List<String> appendBlackList = null;
                if (appendBlackListString != null && !appendBlackListString.trim().isEmpty()) {
                    appendBlackList = Arrays.asList(appendBlackListString.split(","));
                    for (int i = 0; i < appendBlackList.size(); i++) {
                        blackList.remove(appendBlackList.get(i));
                    }
                }
            }
            File templateFile;
            Path xacmlTemplatePath;
            ClassLoader classLoader = getClass().getClassLoader();
            Constraint cons = new Constraint();
            switch (ruleProvider) {
                case GUARD_BL_YAML:
                    templateFile = new File(classLoader.getResource(XACML_BLGUARD_TEMPLATE).getFile());
                    xacmlTemplatePath = templateFile.toPath();
                    cons.setActive_time_range(activeTimeRange);
                    if (blackList == null || blackList.isEmpty()) {
                        throw new BuilderException("blackList is required");
                    }
                    cons.setBlacklist(blackList);
                    break;
                default:
                    templateFile = new File(classLoader.getResource(XACML_GUARD_TEMPLATE).getFile());
                    xacmlTemplatePath = templateFile.toPath();
                    Map<String, String> timeWindow = new HashMap<>();
                    if (!PolicyUtils.isInteger(yamlParams.get("timeWindow"))) {
                        throw new BuilderException("time window is not in Integer format.");
                    }
                    String timeUnits = yamlParams.get("timeUnits");
                    if (timeUnits == null
                            || !("minute".equalsIgnoreCase(timeUnits) || "hour".equalsIgnoreCase(timeUnits)
                                    || "day".equalsIgnoreCase(timeUnits) || "week".equalsIgnoreCase(timeUnits)
                                    || "month".equalsIgnoreCase(timeUnits) || "year".equalsIgnoreCase(timeUnits))) {
                        throw new BuilderException("time Units is not in proper format.");
                    }
                    timeWindow.put("value", yamlParams.get("timeWindow"));
                    timeWindow.put("units", yamlParams.get("timeUnits"));
                    cons = new Constraint(Integer.parseInt(yamlParams.get("limit")), timeWindow, activeTimeRange);
                    break;
            }
            builder = builder.addLimitConstraint(policy1.getId(), cons);
            // Build the specification
            Results results = builder.buildSpecification();
            // YAML TO XACML
            ControlLoopGuard yamlGuardObject = SafePolicyBuilder.loadYamlGuard(results.getSpecification());
            String xacmlTemplateContent;
            try {
                xacmlTemplateContent = new String(Files.readAllBytes(xacmlTemplatePath));
                HashMap<String, String> yamlSpecs = new HashMap<>();
                yamlSpecs.put(POLICY_NAME, yamlParams.get(POLICY_NAME));
                yamlSpecs.put(DESCRIPTION, yamlParams.get(DESCRIPTION));
                yamlSpecs.put(ONAPNAME, yamlParams.get(ONAPNAME));
                yamlSpecs.put("actor", yamlGuardObject.getGuards().getFirst().getMatch_parameters().getActor());
                yamlSpecs.put("recipe", yamlGuardObject.getGuards().getFirst().getMatch_parameters().getRecipe());
                yamlSpecs.put("clname",
                        yamlGuardObject.getGuards().getFirst().getMatch_parameters().getControlLoopName());
                if (yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst()
                        .getFreq_limit_per_target() != null) {
                    yamlSpecs.put("limit", yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst()
                            .getFreq_limit_per_target().toString());
                }
                if (yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst().getTime_window() != null) {
                    yamlSpecs.put("twValue", yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst()
                            .getTime_window().get("value"));
                    yamlSpecs.put("twUnits", yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst()
                            .getTime_window().get("units"));
                }
                yamlSpecs.put("guardActiveStart", yamlGuardObject.getGuards().getFirst().getLimit_constraints()
                        .getFirst().getActive_time_range().get("start"));
                yamlSpecs.put("guardActiveEnd", yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst()
                        .getActive_time_range().get("end"));
                String xacmlPolicyContent = SafePolicyBuilder.generateXacmlGuard(xacmlTemplateContent, yamlSpecs,
                        yamlGuardObject.getGuards().getFirst().getLimit_constraints().getFirst().getBlacklist(),
                        yamlGuardObject.getGuards().getFirst().getMatch_parameters().getTargets());
                // Convert the Policy into Stream input to Policy Adapter.
                Object policy = XACMLPolicyScanner
                        .readPolicy(new ByteArrayInputStream(xacmlPolicyContent.getBytes(StandardCharsets.UTF_8)));
                return (PolicyType) policy;
            } catch (IOException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error while creating the policy " + e.getMessage(),
                        e);
            }
        } catch (BuilderException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error while creating the policy " + e.getMessage(), e);
            throw e;
        }
        return null;
    }

    private DecisionSettings findDecisionSettingsBySettingId(String settingId) {
        DecisionSettings decisionSetting = null;

        EntityManager em = XACMLPapServlet.getEmf().createEntityManager();
        Query getDecisionSettings = em.createNamedQuery("DecisionSettings.findAll");
        List<?> decisionSettingsList = getDecisionSettings.getResultList();

        for (Object id : decisionSettingsList) {
            decisionSetting = (DecisionSettings) id;
            if (decisionSetting.getXacmlId().equals(settingId)) {
                break;
            }
        }
        return decisionSetting;
    }

    private void createRule(PolicyType decisionPolicy, boolean permitRule) {
        RuleType rule = new RuleType();

        rule.setRuleId(policyAdapter.getRuleID());

        if (permitRule) {
            rule.setEffect(EffectType.PERMIT);
        } else {
            rule.setEffect(EffectType.DENY);
        }
        rule.setTarget(new TargetType());

        // Create Target in Rule
        AllOfType allOfInRule = new AllOfType();

        // Creating match for ACCESS in rule target
        MatchType accessMatch = new MatchType();
        AttributeValueType accessAttributeValue = new AttributeValueType();
        accessAttributeValue.setDataType(STRING_DATATYPE);
        accessAttributeValue.getContent().add("DECIDE");
        accessMatch.setAttributeValue(accessAttributeValue);
        AttributeDesignatorType accessAttributeDesignator = new AttributeDesignatorType();
        URI accessURI = null;
        try {
            accessURI = new URI(ACTION_ID);
        } catch (URISyntaxException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "DecisionPolicy", "Exception creating ACCESS URI");
        }
        accessAttributeDesignator.setCategory(CATEGORY_ACTION);
        accessAttributeDesignator.setDataType(STRING_DATATYPE);
        accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
        accessMatch.setAttributeDesignator(accessAttributeDesignator);
        accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

        dynamicLabelRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmLabels();
        dynamicFieldComboRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmCombo();
        dynamicFieldOneRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmField1();
        dynamicFieldTwoRuleAlgorithms = policyAdapter.getDynamicRuleAlgorithmField2();

        if (policyAdapter.getRuleProvider() != null && policyAdapter.getRuleProvider().equals(AAFPROVIDER)) {
            // Values for AAF Provider are here for XML Creation.
            ConditionType condition = new ConditionType();
            ApplyType decisionApply = new ApplyType();

            AttributeValueType value1 = new AttributeValueType();
            value1.setDataType(BOOLEAN_DATATYPE);
            value1.getContent().add("true");

            AttributeDesignatorType value2 = new AttributeDesignatorType();
            value2.setAttributeId(AAFEngine.AAF_RESULT);
            value2.setCategory(CATEGORY_RESOURCE);
            value2.setDataType(BOOLEAN_DATATYPE);
            value2.setMustBePresent(false);

            ApplyType innerDecisionApply = new ApplyType();
            innerDecisionApply.setFunctionId(FUNCTION_BOOLEAN_ONE_AND_ONLY);
            innerDecisionApply.getExpression().add(new ObjectFactory().createAttributeDesignator(value2));

            decisionApply.setFunctionId(XACML3.ID_FUNCTION_BOOLEAN_EQUAL.stringValue());
            decisionApply.getExpression().add(new ObjectFactory().createAttributeValue(value1));
            decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply));
            condition.setExpression(new ObjectFactory().createApply(decisionApply));
            if (!permitRule) {
                ApplyType notOuterApply = new ApplyType();
                notOuterApply.setFunctionId(FUNCTION_NOT);
                notOuterApply.getExpression().add(condition.getExpression());
                condition.setExpression(new ObjectFactory().createApply(notOuterApply));
            }
            rule.setCondition(condition);
            allOfInRule.getMatch().add(accessMatch);

            AnyOfType anyOfInRule = new AnyOfType();
            anyOfInRule.getAllOf().add(allOfInRule);

            TargetType targetInRule = new TargetType();
            targetInRule.getAnyOf().add(anyOfInRule);

            rule.setTarget(targetInRule);
            if (!permitRule) {
                AdviceExpressionsType adviceExpressions = new AdviceExpressionsType();
                AdviceExpressionType adviceExpression = new AdviceExpressionType();
                adviceExpression.setAdviceId(AAFPROVIDER);
                adviceExpression.setAppliesTo(EffectType.DENY);
                AttributeAssignmentExpressionType assignment = new AttributeAssignmentExpressionType();
                assignment.setAttributeId("aaf.response");
                assignment.setCategory(CATEGORY_RESOURCE);
                AttributeDesignatorType value = new AttributeDesignatorType();
                value.setAttributeId(AAFEngine.AAF_RESPONSE);
                value.setCategory(CATEGORY_RESOURCE);
                value.setDataType(STRING_DATATYPE);
                value.setMustBePresent(false);
                assignment.setExpression(new ObjectFactory().createAttributeDesignator(value));
                adviceExpression.getAttributeAssignmentExpression().add(assignment);
                adviceExpressions.getAdviceExpression().add(adviceExpression);
                rule.setAdviceExpressions(adviceExpressions);
            }
            decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
            policyAdapter.setPolicyData(decisionPolicy);

        } else if (dynamicLabelRuleAlgorithms != null && !dynamicLabelRuleAlgorithms.isEmpty()) {
            boolean isCompound = false;
            ConditionType condition = new ConditionType();
            int index = dynamicFieldOneRuleAlgorithms.size() - 1;

            for (String labelAttr : dynamicLabelRuleAlgorithms) {
                // if the rule algorithm as a label means it is a compound
                if (dynamicFieldOneRuleAlgorithms.get(index).equals(labelAttr)) {
                    ApplyType decisionApply = new ApplyType();

                    String selectedFunction = dynamicFieldComboRuleAlgorithms.get(index);
                    String value1 = dynamicFieldOneRuleAlgorithms.get(index);
                    String value2 = dynamicFieldTwoRuleAlgorithms.get(index);
                    decisionApply.setFunctionId(getFunctionDefinitionId(selectedFunction));
                    decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value1)));
                    decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value2)));
                    condition.setExpression(new ObjectFactory().createApply(decisionApply));
                    isCompound = true;
                }

                // if rule algorithm not a compound
                if (!isCompound) {
                    condition.setExpression(new ObjectFactory()
                            .createApply(getInnerDecisionApply(dynamicLabelRuleAlgorithms.get(index))));
                }
            }
            if (!permitRule) {
                ApplyType notOuterApply = new ApplyType();
                notOuterApply.setFunctionId(FUNCTION_NOT);
                notOuterApply.getExpression().add(condition.getExpression());
                condition.setExpression(new ObjectFactory().createApply(notOuterApply));
            }
            rule.setCondition(condition);
            allOfInRule.getMatch().add(accessMatch);

            AnyOfType anyOfInRule = new AnyOfType();
            anyOfInRule.getAllOf().add(allOfInRule);

            TargetType targetInRule = new TargetType();
            targetInRule.getAnyOf().add(anyOfInRule);

            rule.setTarget(targetInRule);

            decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
            policyAdapter.setPolicyData(decisionPolicy);

        } else {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Unsupported data object."
                    + policyAdapter.getData().getClass().getCanonicalName());
        }

    }

    private void createRainydayRule(PolicyType decisionPolicy, String errorcode, String treatment, boolean permitRule) {
        RuleType rule = new RuleType();

        rule.setRuleId(UUID.randomUUID().toString());

        if (permitRule) {
            rule.setEffect(EffectType.PERMIT);
        } else {
            rule.setEffect(EffectType.DENY);
        }
        rule.setTarget(new TargetType());

        // Create Target in Rule
        AllOfType allOfInRule = new AllOfType();

        // Creating match for DECIDE in rule target
        MatchType accessMatch = new MatchType();
        AttributeValueType accessAttributeValue = new AttributeValueType();
        accessAttributeValue.setDataType(STRING_DATATYPE);
        accessAttributeValue.getContent().add("DECIDE");
        accessMatch.setAttributeValue(accessAttributeValue);
        AttributeDesignatorType accessAttributeDesignator = new AttributeDesignatorType();
        URI accessURI = null;
        try {
            accessURI = new URI(ACTION_ID);
        } catch (URISyntaxException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "DecisionPolicy", "Exception creating ACCESS URI");
        }
        accessAttributeDesignator.setCategory(CATEGORY_ACTION);
        accessAttributeDesignator.setDataType(STRING_DATATYPE);
        accessAttributeDesignator.setAttributeId(new IdentifierImpl(accessURI).stringValue());
        accessMatch.setAttributeDesignator(accessAttributeDesignator);
        accessMatch.setMatchId(FUNCTION_STRING_EQUAL_IGNORE);

        allOfInRule.getMatch().add(accessMatch);

        // Creating match for ErrorCode in rule target
        MatchType errorcodeMatch = new MatchType();
        AttributeValueType errorcodeAttributeValue = new AttributeValueType();
        errorcodeAttributeValue.setDataType(STRING_DATATYPE);
        errorcodeAttributeValue.getContent().add(errorcode);
        errorcodeMatch.setAttributeValue(errorcodeAttributeValue);
        AttributeDesignatorType errorcodeAttributeDesignator = new AttributeDesignatorType();
        errorcodeAttributeDesignator.setCategory(CATEGORY_RESOURCE);
        errorcodeAttributeDesignator.setDataType(STRING_DATATYPE);
        errorcodeAttributeDesignator.setAttributeId("ErrorCode");
        errorcodeMatch.setAttributeDesignator(errorcodeAttributeDesignator);
        errorcodeMatch.setMatchId(FUNCTION_STRING_REGEXP_MATCH);

        allOfInRule.getMatch().add(errorcodeMatch);

        AnyOfType anyOfInRule = new AnyOfType();
        anyOfInRule.getAllOf().add(allOfInRule);

        TargetType targetInRule = new TargetType();
        targetInRule.getAnyOf().add(anyOfInRule);

        rule.setTarget(targetInRule);

        AdviceExpressionsType adviceExpressions = new AdviceExpressionsType();
        AdviceExpressionType adviceExpression = new AdviceExpressionType();
        adviceExpression.setAdviceId(RAINY_DAY);
        adviceExpression.setAppliesTo(EffectType.PERMIT);

        AttributeAssignmentExpressionType assignment = new AttributeAssignmentExpressionType();
        assignment.setAttributeId("treatment");
        assignment.setCategory(CATEGORY_RESOURCE);

        AttributeValueType treatmentAttributeValue = new AttributeValueType();
        treatmentAttributeValue.setDataType(STRING_DATATYPE);
        treatmentAttributeValue.getContent().add(treatment);
        assignment.setExpression(new ObjectFactory().createAttributeValue(treatmentAttributeValue));

        adviceExpression.getAttributeAssignmentExpression().add(assignment);
        adviceExpressions.getAdviceExpression().add(adviceExpression);
        rule.setAdviceExpressions(adviceExpressions);
        decisionPolicy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
        policyAdapter.setPolicyData(decisionPolicy);

    }

    // if compound setting the inner apply here
    protected ApplyType getInnerDecisionApply(String value1Label) {
        ApplyType decisionApply = new ApplyType();
        int index = 0;
        // check the index for the label.
        for (String labelAttr : dynamicLabelRuleAlgorithms) {
            if (labelAttr.equals(value1Label)) {
                String value1 = dynamicFieldOneRuleAlgorithms.get(index);
                populateDataTypeList(value1);

                // check if the row contains label again
                for (String labelValue : dynamicLabelRuleAlgorithms) {
                    if (labelValue.equals(value1)) {
                        return getCompoundDecisionApply(index);
                    }
                }

                // Getting the values from the form.
                String functionKey = dynamicFieldComboRuleAlgorithms.get(index);
                String value2 = dynamicFieldTwoRuleAlgorithms.get(index);
                decisionApply.setFunctionId(getFunctionDefinitionId(functionKey));
                // if two text field are rule attributes.
                if ((value1.contains(RULE_VARIABLE)) && (value2.contains(RULE_VARIABLE))) {
                    ApplyType innerDecisionApply1 = new ApplyType();
                    ApplyType innerDecisionApply2 = new ApplyType();
                    AttributeDesignatorType attributeDesignator1 = new AttributeDesignatorType();
                    AttributeDesignatorType attributeDesignator2 = new AttributeDesignatorType();
                    // If selected function is Integer function set integer functionID
                    if (functionKey.toLowerCase().contains("integer")) {
                        innerDecisionApply1.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
                        innerDecisionApply2.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
                        attributeDesignator1.setDataType(INTEGER_DATATYPE);
                        attributeDesignator2.setDataType(INTEGER_DATATYPE);
                    } else {
                        // If selected function is not a Integer function set String functionID
                        innerDecisionApply1.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
                        innerDecisionApply2.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
                        attributeDesignator1.setDataType(STRING_DATATYPE);
                        attributeDesignator2.setDataType(STRING_DATATYPE);
                    }
                    attributeDesignator1.setCategory(CATEGORY_RESOURCE);
                    attributeDesignator2.setCategory(CATEGORY_RESOURCE);
                    // Here set actual field values
                    attributeDesignator1
                            .setAttributeId(value1.contains("resource:") ? value1.substring(9) : value1.substring(8));
                    attributeDesignator2
                            .setAttributeId(value1.contains("resource:") ? value1.substring(9) : value1.substring(8));
                    innerDecisionApply1.getExpression()
                            .add(new ObjectFactory().createAttributeDesignator(attributeDesignator1));
                    innerDecisionApply2.getExpression()
                            .add(new ObjectFactory().createAttributeDesignator(attributeDesignator2));
                    decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply1));
                    decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply2));
                } else {
                    // if either of one text field is rule attribute.
                    if (!value1.startsWith("S_")) {
                        ApplyType innerDecisionApply = new ApplyType();
                        AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
                        AttributeValueType decisionConditionAttributeValue = new AttributeValueType();

                        if (functionKey.toLowerCase().contains("integer")) {
                            innerDecisionApply.setFunctionId(FUNTION_INTEGER_ONE_AND_ONLY);
                            decisionConditionAttributeValue.setDataType(INTEGER_DATATYPE);
                            attributeDesignator.setDataType(INTEGER_DATATYPE);
                        } else {
                            innerDecisionApply.setFunctionId(FUNCTION_STRING_ONE_AND_ONLY);
                            decisionConditionAttributeValue.setDataType(STRING_DATATYPE);
                            attributeDesignator.setDataType(STRING_DATATYPE);
                        }

                        String attributeId = null;
                        String attributeValue = null;

                        // Find which textField has rule attribute and set it as
                        // attributeId and the other as attributeValue.
                        attributeId = value1;
                        attributeValue = value2;

                        if (attributeId != null) {
                            attributeDesignator.setCategory(CATEGORY_RESOURCE);
                            attributeDesignator.setAttributeId(attributeId);
                        }
                        decisionConditionAttributeValue.getContent().add(attributeValue);
                        innerDecisionApply.getExpression()
                                .add(new ObjectFactory().createAttributeDesignator(attributeDesignator));
                        decisionApply.getExpression()
                                .add(new ObjectFactory().createAttributeValue(decisionConditionAttributeValue));
                        decisionApply.getExpression().add(new ObjectFactory().createApply(innerDecisionApply));
                    } else {
                        value1 = value1.substring(2, value1.length());
                        VariableReferenceType variableReferenceType = new VariableReferenceType();
                        variableReferenceType.setVariableId(value1);

                        String dataType = dataTypeList.get(index);

                        AttributeValueType decisionConditionAttributeValue = new AttributeValueType();
                        decisionConditionAttributeValue.setDataType(dataType);
                        decisionConditionAttributeValue.getContent().add(value2);
                        decisionApply.getExpression()
                                .add(new ObjectFactory().createVariableReference(variableReferenceType));
                        decisionApply.getExpression()
                                .add(new ObjectFactory().createAttributeValue(decisionConditionAttributeValue));
                    }
                }
            }
            index++;
        }
        return decisionApply;
    }

    // if the rule algorithm is multiple compound one setting the apply
    protected ApplyType getCompoundDecisionApply(int index) {
        ApplyType decisionApply = new ApplyType();
        String selectedFunction = dynamicFieldComboRuleAlgorithms.get(index);
        String value1 = dynamicFieldOneRuleAlgorithms.get(index);
        String value2 = dynamicFieldTwoRuleAlgorithms.get(index);
        decisionApply.setFunctionId(getFunctionDefinitionId(selectedFunction));
        decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value1)));
        decisionApply.getExpression().add(new ObjectFactory().createApply(getInnerDecisionApply(value2)));
        return decisionApply;
    }

    private VariableDefinitionType createDynamicVariable(String key, String value, String dataType) {
        VariableDefinitionType dynamicVariable = new VariableDefinitionType();
        AttributeValueType dynamicAttributeValue = new AttributeValueType();

        dynamicAttributeValue.setDataType(dataType);
        dynamicAttributeValue.getContent().add(value);

        dynamicVariable.setVariableId(key);
        dynamicVariable.setExpression(new ObjectFactory().createAttributeValue(dynamicAttributeValue));

        return dynamicVariable;

    }

    private void populateDataTypeList(String value1) {
        String dataType = null;
        if (value1.contains("S_")) {
            value1 = value1.substring(2, value1.length());
            DecisionSettings decisionSettings = findDecisionSettingsBySettingId(value1.substring(2, value1.length()));
            if (decisionSettings != null && "string".equals(decisionSettings.getDatatypeBean().getShortName())) {
                dataType = STRING_DATATYPE;
            } else if (decisionSettings != null
                    && "boolean".equals(decisionSettings.getDatatypeBean().getShortName())) {
                dataType = BOOLEAN_DATATYPE;
            } else {
                dataType = INTEGER_DATATYPE;
            }
        } else {
            dataType = "OTHER";
        }

        dataTypeList.add(dataType);
    }


    private String getDataType(String key) {

        DecisionSettings decisionSettings = findDecisionSettingsBySettingId(key);
        String dataType = null;

        if (decisionSettings != null && "string".equals(decisionSettings.getDatatypeBean().getShortName())) {
            dataType = STRING_DATATYPE;
        } else if (decisionSettings != null && "boolean".equals(decisionSettings.getDatatypeBean().getShortName())) {
            dataType = BOOLEAN_DATATYPE;
        } else {
            dataType = INTEGER_DATATYPE;
        }

        return dataType;
    }

    @Override
    public Object getCorrectPolicyDataObject() {
        return policyAdapter.getData();
    }

    public String getFunctionDefinitionId(String key) {
        FunctionDefinition object =
                (FunctionDefinition) commonClassDao.getDataById(FunctionDefinition.class, "shortname", key);
        if (object != null) {
            return object.getXacmlid();
        }
        return null;
    }

}
