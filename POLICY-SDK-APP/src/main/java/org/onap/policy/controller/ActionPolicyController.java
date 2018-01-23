/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

@Controller
@RequestMapping({ "/" })
public class ActionPolicyController extends RestrictedBaseController {
    private static final Logger LOGGER = FlexLogger.getLogger(ActionPolicyController.class);

    public ActionPolicyController() {
        // Default Constructor
    }

    private ArrayList<Object> attributeList;
    protected LinkedList<Integer> ruleAlgoirthmTracker;
    public static final String PERFORMER_ATTRIBUTEID = "performer";
    protected Map<String, String> performer = new HashMap<>();
    private ArrayList<Object> ruleAlgorithmList;

    public void prePopulateActionPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
        attributeList = new ArrayList<>();
        ruleAlgorithmList = new ArrayList<>();
        performer.put("PDP", "PDPAction");
        performer.put("PEP", "PEPAction");

        if (policyAdapter.getPolicyData() instanceof PolicyType) {
            Object policyData = policyAdapter.getPolicyData();
            PolicyType policy = (PolicyType) policyData;
            policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
            String policyNameValue = policyAdapter.getPolicyName()
                    .substring(policyAdapter.getPolicyName().indexOf('_') + 1);
            policyAdapter.setPolicyName(policyNameValue);
            String description = "";
            try {
                description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
            } catch (Exception e) {
                LOGGER.error("Error while collecting the desciption tag in ActionPolicy " + policyNameValue, e);
                description = policy.getDescription();
            }
            policyAdapter.setPolicyDescription(description);
            // Get the target data under policy for Action.
            TargetType target = policy.getTarget();
            if (target != null) {
                // under target we have AnyOFType
                List<AnyOfType> anyOfList = target.getAnyOf();
                if (anyOfList != null) {
                    Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
                    while (iterAnyOf.hasNext()) {
                        AnyOfType anyOf = iterAnyOf.next();
                        // Under AntOfType we have AllOfType
                        List<AllOfType> allOfList = anyOf.getAllOf();
                        if (allOfList != null) {
                            Iterator<AllOfType> iterAllOf = allOfList.iterator();
                            while (iterAllOf.hasNext()) {
                                AllOfType allOf = iterAllOf.next();
                                // Under AllOfType we have Mathch.
                                List<MatchType> matchList = allOf.getMatch();
                                if (matchList != null) {
                                    Iterator<MatchType> iterMatch = matchList.iterator();
                                    while (iterMatch.hasNext()) {
                                        MatchType match = iterMatch.next();
                                        //
                                        // Under the match we have attributevalue and
                                        // attributeDesignator. So,finally down to the actual attribute.
                                        //
                                        AttributeValueType attributeValue = match.getAttributeValue();
                                        String value = (String) attributeValue.getContent().get(0);
                                        AttributeDesignatorType designator = match.getAttributeDesignator();
                                        String attributeId = designator.getAttributeId();
                                        // Component attributes are saved under Target here we are fetching them back.
                                        // One row is default so we are not adding dynamic component at index 0.
                                        Map<String, String> attribute = new HashMap<>();
                                        attribute.put("key", attributeId);
                                        attribute.put("value", value);
                                        attributeList.add(attribute);
                                    }
                                }
                                policyAdapter.setAttributes(attributeList);
                            }
                        }
                    }
                }

                List<Object> ruleList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
                // Under rule we have Condition and obligation.
                for (Object o : ruleList) {
                    if (o instanceof RuleType) {
                        ConditionType condition = ((RuleType) o).getCondition();
                        ObligationExpressionsType obligations = ((RuleType) o).getObligationExpressions();
                        if (condition != null) {
                            int index = 0;
                            ApplyType actionApply = (ApplyType) condition.getExpression().getValue();
                            ruleAlgoirthmTracker = new LinkedList<>();
                            // Populating Rule Algorithms starting from compound.
                            prePopulateCompoundRuleAlgorithm(index, actionApply);
                        }
                        policyAdapter.setRuleAlgorithmschoices(ruleAlgorithmList);
                        // get the Obligation data under the rule for Form elements.
                        if (obligations != null) {
                            // Under the obligationExpressions we have obligationExpression.
                            List<ObligationExpressionType> obligationList = obligations.getObligationExpression();
                            if (obligationList != null) {
                                Iterator<ObligationExpressionType> iterObligation = obligationList.iterator();
                                while (iterObligation.hasNext()) {
                                    ObligationExpressionType obligation = iterObligation.next();
                                    policyAdapter.setActionAttributeValue(obligation.getObligationId());
                                    // Under the obligationExpression we have attributeAssignmentExpression.
                                    List<AttributeAssignmentExpressionType> attributeAssignmentExpressionList = obligation
                                            .getAttributeAssignmentExpression();
                                    if (attributeAssignmentExpressionList != null) {
                                        Iterator<AttributeAssignmentExpressionType> iterAttributeAssignmentExpression = attributeAssignmentExpressionList
                                                .iterator();
                                        while (iterAttributeAssignmentExpression.hasNext()) {
                                            AttributeAssignmentExpressionType attributeAssignmentExpression = iterAttributeAssignmentExpression
                                                    .next();
                                            String attributeID = attributeAssignmentExpression.getAttributeId();
                                            AttributeValueType attributeValue = (AttributeValueType) attributeAssignmentExpression
                                                    .getExpression().getValue();
                                            if (attributeID.equals(PERFORMER_ATTRIBUTEID)) {
                                                for (String key : performer.keySet()) {
                                                    String keyValue = performer.get(key);
                                                    if (keyValue.equals(attributeValue.getContent().get(0))) {
                                                        policyAdapter.setActionPerformer(key);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int prePopulateCompoundRuleAlgorithm(int index, ApplyType actionApply) {
        boolean isCompoundRule = true;
        List<JAXBElement<?>> jaxbActionTypes = actionApply.getExpression();
        for (JAXBElement<?> jaxbElement : jaxbActionTypes) {
            // If There is Attribute Value under Action Type that means we came to the final child
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Prepopulating rule algoirthm: " + index);
            }
            // Check to see if Attribute Value exists, if yes then it is not a compound rule
            if (jaxbElement.getValue() instanceof AttributeValueType) {
                prePopulateRuleAlgorithms(index, actionApply, jaxbActionTypes);
                ruleAlgoirthmTracker.addLast(index);
                isCompoundRule = false;
                index++;
            }
        }
        if (isCompoundRule) {
            // As it's compound rule, Get the Apply types
            for (JAXBElement<?> jaxbElement : jaxbActionTypes) {
                ApplyType innerActionApply = (ApplyType) jaxbElement.getValue();
                index = prePopulateCompoundRuleAlgorithm(index, innerActionApply);
            }
            // Populate combo box
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Prepopulating Compound rule algorithm: " + index);
            }
            Map<String, String> rule = new HashMap<>();
            for (String key : PolicyController.getDropDownMap().keySet()) {
                String keyValue = PolicyController.getDropDownMap().get(key);
                if (keyValue.equals(actionApply.getFunctionId())) {
                    rule.put("dynamicRuleAlgorithmCombo", key);
                }
            }
            rule.put("id", "A" + (index + 1));
            // Populate Key and values for Compound Rule
            rule.put("dynamicRuleAlgorithmField1", "A" + (ruleAlgoirthmTracker.getLast() + 1));
            ruleAlgoirthmTracker.removeLast();
            rule.put("dynamicRuleAlgorithmField2", "A" + (ruleAlgoirthmTracker.getLast() + 1));
            ruleAlgoirthmTracker.removeLast();
            ruleAlgoirthmTracker.addLast(index);
            ruleAlgorithmList.add(rule);
            index++;
        }
        return index;
    }

    private void prePopulateRuleAlgorithms(int index, ApplyType actionApply, List<JAXBElement<?>> jaxbActionTypes) {
        Map<String, String> ruleMap = new HashMap<>();
        ruleMap.put("id", "A" + (index + 1));
        // Populate combo box
        Map<String, String> dropDownMap = PolicyController.getDropDownMap();
        for (String key : dropDownMap.keySet()) {
            String keyValue = dropDownMap.get(key);
            if (keyValue.equals(actionApply.getFunctionId())) {
                ruleMap.put("dynamicRuleAlgorithmCombo", key);
            }
        }
        // Populate the key and value fields
        // Rule Attribute added as key
        if ((jaxbActionTypes.get(0).getValue()) instanceof ApplyType) {
            // Get from Attribute Designator
            ApplyType innerActionApply = (ApplyType) jaxbActionTypes.get(0).getValue();
            List<JAXBElement<?>> jaxbInnerActionTypes = innerActionApply.getExpression();
            AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) jaxbInnerActionTypes.get(0)
                    .getValue();
            ruleMap.put("dynamicRuleAlgorithmField1", attributeDesignator.getAttributeId());

            // Get from Attribute Value
            AttributeValueType actionConditionAttributeValue = (AttributeValueType) jaxbActionTypes.get(1).getValue();
            String attributeValue = (String) actionConditionAttributeValue.getContent().get(0);
            ruleMap.put("dynamicRuleAlgorithmField2", attributeValue);
        }
        // Rule Attribute added as value
        else if ((jaxbActionTypes.get(0).getValue()) instanceof AttributeValueType) {
            AttributeValueType actionConditionAttributeValue = (AttributeValueType) jaxbActionTypes.get(0).getValue();
            String attributeValue = (String) actionConditionAttributeValue.getContent().get(0);
            ruleMap.put("dynamicRuleAlgorithmField2", attributeValue);

            ApplyType innerActionApply = (ApplyType) jaxbActionTypes.get(1).getValue();
            List<JAXBElement<?>> jaxbInnerActionTypes = innerActionApply.getExpression();
            AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) jaxbInnerActionTypes.get(0)
                    .getValue();
            ruleMap.put("dynamicRuleAlgorithmField1", attributeDesignator.getAttributeId());
        }
        ruleAlgorithmList.add(ruleMap);
    }

}
