/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Bell Canada
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXBElement;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/"})
public class ActionPolicyController extends RestrictedBaseController {
    private static final Logger LOGGER = FlexLogger.getLogger(ActionPolicyController.class);
    private static final String PERFORMER_ATTRIBUTE_ID = "performer";
    private static final String DYNAMIC_RULE_ALGORITHM_FIELD_1 = "dynamicRuleAlgorithmField1";
    private static final String DYNAMIC_RULE_ALGORITHM_FIELD_2 = "dynamicRuleAlgorithmField2";
    private LinkedList<Integer> ruleAlgorithmTracker;
    private Map<String, String> performer = new HashMap<>();
    private List<Object> ruleAlgorithmList;

    public ActionPolicyController() {
        // Default Constructor
    }

    /**
     * prePopulateActionPolicyData.
     *
     * @param policyAdapter PolicyRestAdapter
     */
    public void prePopulateActionPolicyData(PolicyRestAdapter policyAdapter) {
        ruleAlgorithmList = new ArrayList<>();
        performer.put("PDP", "PDPAction");
        performer.put("PEP", "PEPAction");

        if (policyAdapter.getPolicyData() instanceof PolicyType) {
            PolicyType policy = (PolicyType) policyAdapter.getPolicyData();

            // 1. Set policy-name, policy-filename and description to Policy Adapter
            setPolicyAdapterPolicyNameAndDesc(policyAdapter, policy);

            // 2a. Get the target data under policy for Action.
            TargetType target = policy.getTarget();
            if (target == null) {
                return;
            }

            // 2b. Set attributes to Policy Adapter
            setPolicyAdapterAttributes(policyAdapter, target.getAnyOf());

            List<Object> ruleList = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
            // Under rule we have Condition and obligation.
            for (Object o : ruleList) {
                if (!(o instanceof RuleType)) {
                    continue;
                }
                // 3. Set rule-algorithm choices to Policy Adapter
                setPolicyAdapterRuleAlgorithmschoices(policyAdapter, (RuleType) o);

                // 4a. Get the Obligation data under the rule for Form elements.
                ObligationExpressionsType obligations = ((RuleType) o).getObligationExpressions();

                // 4b. Set action attribute-value and action-performer to Policy Adapter
                setPolicyAdapterActionData(policyAdapter, obligations);
            }
        }
    }

    private void setPolicyAdapterActionData(PolicyRestAdapter policyAdapter, ObligationExpressionsType obligations) {
        if (obligations == null) {
            return;
        }
        // Under the obligationExpressions we have obligationExpression.
        // NOTE: getObligationExpression() will never return NULL.
        //
        for (ObligationExpressionType obligation : obligations.getObligationExpression()) {
            policyAdapter.setActionAttributeValue(obligation.getObligationId());
            // Under the obligationExpression we have attributeAssignmentExpression.
            //
            // NOTE: obligation.getAttributeAssignmentExpression() will NEVER be null
            // It will always return a list.
            //
            for (AttributeAssignmentExpressionType attributeAssignmentExpression :
                obligation.getAttributeAssignmentExpression()) {
                //
                //
                //
                String attributeID = attributeAssignmentExpression.getAttributeId();
                AttributeValueType attributeValue =
                        (AttributeValueType) attributeAssignmentExpression.getExpression().getValue();
                if (!attributeID.equals(PERFORMER_ATTRIBUTE_ID)) {
                    continue;
                }
                performer.forEach((key, keyValue) -> {
                    if (keyValue.equals(attributeValue.getContent().get(0))) {
                        policyAdapter.setActionPerformer(key);
                    }
                });
            }
        }
    }

    private void setPolicyAdapterPolicyNameAndDesc(PolicyRestAdapter policyAdapter, PolicyType policy) {
        policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
        String policyNameValue =
                policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf('_') + 1);
        policyAdapter.setPolicyName(policyNameValue);
        String description;
        try {
            description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
        } catch (Exception e) {
            LOGGER.error("Error while collecting the description tag in ActionPolicy " + policyNameValue, e);
            description = policy.getDescription();
        }
        policyAdapter.setPolicyDescription(description);
    }

    private void setPolicyAdapterRuleAlgorithmschoices(PolicyRestAdapter policyAdapter, RuleType ruleType) {
        if (ruleType.getCondition() != null) {
            int index = 0;
            ApplyType actionApply = (ApplyType) ruleType.getCondition().getExpression().getValue();
            ruleAlgorithmTracker = new LinkedList<>();
            // Populating Rule Algorithms starting from compound.
            prePopulateCompoundRuleAlgorithm(index, actionApply);
        }
        policyAdapter.setRuleAlgorithmschoices(ruleAlgorithmList);
    }

    private void setPolicyAdapterAttributes(PolicyRestAdapter policyAdapter, List<AnyOfType> anyOfList) {
        List<Object> attributeList = new ArrayList<>();
        //
        // NOTE: If using xacml3 code and doing a getAnyOf(), the anyOfList will
        // NEVER be null as that code will create it if it is null.
        //
        // Remove the null check as its impossible to cover it.
        //
        // under target we have AnyOFType
        for (AnyOfType anyOf : anyOfList) {
            // Under AntOfType we have AllOfType
            //
            // NOTE: This will NEVER be null as the method call in the
            // previous line getAllOf() will never return a null. It
            // always creates it if its empty.
            //
            List<AllOfType> allOfList = anyOf.getAllOf();
            // Under AllOfType we have Match.
            for (AllOfType allOfType : allOfList) {
                //
                // NOTE: allOfType.getMatch() will NEVER be null as the method
                // call getMatch will always return something. If its
                // not there it will create it.
                //
                //
                // Under the match we have attributeValue and
                // attributeDesignator. So,finally down to the actual attribute.
                //
                // Component attributes are saved under Target here we are fetching them back.
                // One row is default so we are not adding dynamic component at index 0.
                for (MatchType match : allOfType.getMatch()) {
                    AttributeValueType attributeValue = match.getAttributeValue();
                    String value = (String) attributeValue.getContent().get(0);
                    AttributeDesignatorType designator = match.getAttributeDesignator();
                    String attributeId = designator.getAttributeId();
                    Map<String, String> attribute = new HashMap<>();
                    attribute.put("key", attributeId);
                    attribute.put("value", value);
                    attributeList.add(attribute);
                }
                policyAdapter.setAttributes(attributeList);
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
            if (jaxbElement.getValue() instanceof AttributeValueType
                    || jaxbElement.getValue() instanceof AttributeDesignatorType) {
                prePopulateRuleAlgorithms(index, actionApply, jaxbActionTypes);
                ruleAlgorithmTracker.addLast(index);
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
            rule.put(DYNAMIC_RULE_ALGORITHM_FIELD_1, "A" + (ruleAlgorithmTracker.getLast() + 1));
            ruleAlgorithmTracker.removeLast();
            rule.put(DYNAMIC_RULE_ALGORITHM_FIELD_2, "A" + (ruleAlgorithmTracker.getLast() + 1));
            ruleAlgorithmTracker.removeLast();
            ruleAlgorithmTracker.addLast(index);
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
        for (Entry<String, String> entry : dropDownMap.entrySet()) {
            if (entry.getValue().equals(actionApply.getFunctionId())) {
                ruleMap.put("dynamicRuleAlgorithmCombo", entry.getKey());
            }
        }
        // Populate the key and value fields
        // Rule Attribute added as key
        if ((jaxbActionTypes.get(0).getValue()) instanceof ApplyType) {
            // Get from Attribute Designator
            ApplyType innerActionApply = (ApplyType) jaxbActionTypes.get(0).getValue();
            List<JAXBElement<?>> jaxbInnerActionTypes = innerActionApply.getExpression();
            AttributeDesignatorType attributeDesignator =
                    (AttributeDesignatorType) jaxbInnerActionTypes.get(0).getValue();
            ruleMap.put(DYNAMIC_RULE_ALGORITHM_FIELD_1, attributeDesignator.getAttributeId());

            // Get from Attribute Value
            AttributeValueType actionConditionAttributeValue = (AttributeValueType) jaxbActionTypes.get(1).getValue();
            String attributeValue = (String) actionConditionAttributeValue.getContent().get(0);
            ruleMap.put(DYNAMIC_RULE_ALGORITHM_FIELD_2, attributeValue);
        }
        // Rule Attribute added as value
        else if ((jaxbActionTypes.get(0).getValue()) instanceof AttributeValueType) {
            AttributeValueType actionConditionAttributeValue = (AttributeValueType) jaxbActionTypes.get(0).getValue();
            String attributeValue = (String) actionConditionAttributeValue.getContent().get(0);
            ruleMap.put(DYNAMIC_RULE_ALGORITHM_FIELD_2, attributeValue);

            //
            // This is making a BIG assumption here that there exists an innerApply. This IF
            // statement was added to support JUnit code coverage. For lack of any example of what
            // this policy should actually look like.
            //
            if (jaxbActionTypes.size() > 1) {
                ApplyType innerActionApply = (ApplyType) jaxbActionTypes.get(1).getValue();
                List<JAXBElement<?>> jaxbInnerActionTypes = innerActionApply.getExpression();
                if (! jaxbInnerActionTypes.isEmpty()) {
                    AttributeDesignatorType attributeDesignator =
                        (AttributeDesignatorType) jaxbInnerActionTypes.get(0).getValue();
                    ruleMap.put(DYNAMIC_RULE_ALGORITHM_FIELD_1, attributeDesignator.getAttributeId());
                }
            }
        }
        ruleAlgorithmList.add(ruleMap);
    }
}
