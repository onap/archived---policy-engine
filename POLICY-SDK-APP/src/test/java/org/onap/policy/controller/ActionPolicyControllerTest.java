/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

import static org.assertj.core.api.Assertions.assertThatCode;

import com.att.research.xacml.api.XACML3;
import java.io.IOException;
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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;
import org.junit.Test;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

public class ActionPolicyControllerTest {

    @Test
    public void testBasicConstructor() throws IOException {
        ActionPolicyController controller = new ActionPolicyController();
        final PolicyRestAdapter adapter = new PolicyRestAdapter();
        //
        // Cover the simple if instance branch
        //
        assertThatCode(() -> controller.prePopulateActionPolicyData(adapter)).doesNotThrowAnyException();
    }

    @Test
    public void testNoDescriptionNoTargetType() throws IOException {
        //
        // Do the test - with no description and
        // no TargetType to cover those branches.
        //
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setPolicyData(new PolicyType());
        adapter.setPolicyName("name");
        ActionPolicyController controller = new ActionPolicyController();
        assertThatCode(() -> controller.prePopulateActionPolicyData(adapter)).doesNotThrowAnyException();
    }

    @Test
    public void testWithDescriptionEmptyTargetType() throws IOException {
        //
        // Create a simple PolicyType
        //
        PolicyType policy = new PolicyType();
        policy.setTarget(new TargetType());
        policy.setDescription("i am a description. @CreatedBy: policy designer");
        //
        // Now do the test - description and
        // TargetType but its empty
        //
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setPolicyData(policy);
        adapter.setPolicyName("name");
        ActionPolicyController controller = new ActionPolicyController();
        assertThatCode(() -> controller.prePopulateActionPolicyData(adapter)).doesNotThrowAnyException();
    }


    @Test
    public void testWithTargetTypeWithAnyOf() throws IOException {
        //
        // Create TargetType with empty AnyOf
        //
        AttributeValueType value = new AttributeValueType();
        value.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        value.getContent().add(new String("value"));
        AttributeDesignatorType designator = new AttributeDesignatorType();
        designator.setAttributeId("foo:bar");
        designator.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        designator.setCategory(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION.stringValue());

        MatchType match = new MatchType();
        match.setMatchId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
        match.setAttributeValue(value);
        match.setAttributeDesignator(designator);
        AllOfType allOf = new AllOfType();
        allOf.getMatch().add(match);
        AnyOfType anyOf = new AnyOfType();
        anyOf.getAllOf().add(allOf);
        TargetType target = new TargetType();
        target.getAnyOf().add(anyOf);
        //
        // Create a simple Rule with NO obligations but a Condition
        //
        RuleType rule = new RuleType();
        rule.setRuleId("id:rule");
        rule.setEffect(EffectType.PERMIT);

        AttributeValueType expressionValue = new AttributeValueType();
        expressionValue.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        expressionValue.getContent().add(new String("a string value"));

        designator = new AttributeDesignatorType();
        designator.setAttributeId("foo:bar");
        designator.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        designator.setCategory(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION.stringValue());

        ApplyType applyOneAndOnly = new ApplyType();
        applyOneAndOnly.setDescription("apply this");
        applyOneAndOnly.setFunctionId(XACML3.ID_FUNCTION_STRING_ONE_AND_ONLY.stringValue());
        applyOneAndOnly.getExpression().add(new ObjectFactory().createAttributeDesignator(designator));

        ApplyType applyOneAndOnly2 = new ApplyType();
        applyOneAndOnly2.setDescription("apply this");
        applyOneAndOnly2.setFunctionId(XACML3.ID_FUNCTION_STRING_ONE_AND_ONLY.stringValue());
        applyOneAndOnly2.getExpression().add(new ObjectFactory().createAttributeValue(expressionValue));

        ApplyType apply = new ApplyType();
        apply.setDescription("apply this");
        apply.setFunctionId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
        apply.getExpression().add(new ObjectFactory().createApply(applyOneAndOnly));
        apply.getExpression().add(new ObjectFactory().createApply(applyOneAndOnly2));


        ConditionType condition = new ConditionType();
        condition.setExpression(new ObjectFactory().createApply(apply));
        rule.setCondition(condition);
        //
        // Create a simple Rule WITH obligations
        //
        AttributeValueType val = new AttributeValueType();
        val.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        val.getContent().add(new String("obligation data"));

        AttributeAssignmentExpressionType assignment = new AttributeAssignmentExpressionType();
        assignment.setAttributeId("ob:id:1");
        assignment.setCategory(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue());
        assignment.setExpression(new ObjectFactory().createAttributeValue(val));

        AttributeValueType val2 = new AttributeValueType();
        val2.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        val2.getContent().add(new String("iamperformer"));

        AttributeAssignmentExpressionType assignment2 = new AttributeAssignmentExpressionType();
        assignment2.setAttributeId("performer");
        assignment2.setCategory(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue());
        assignment2.setExpression(new ObjectFactory().createAttributeValue(val2));

        ObligationExpressionType ob = new ObligationExpressionType();
        ob.setFulfillOn(EffectType.PERMIT);
        ob.setObligationId("id:obligation");
        ob.getAttributeAssignmentExpression().add(assignment);
        ob.getAttributeAssignmentExpression().add(assignment2);
        ObligationExpressionsType obs = new ObligationExpressionsType();
        obs.getObligationExpression().add(ob);
        RuleType obligationRule = new RuleType();
        obligationRule.setRuleId("id:rule:obligations");
        obligationRule.setEffect(EffectType.DENY);
        obligationRule.setObligationExpressions(obs);
        //
        // Create a PolicyType
        //
        PolicyType policy = new PolicyType();
        policy.setDescription("i am a description. @CreatedBy: policy designer");
        policy.setTarget(target);
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(obligationRule);
        //
        // Add something the ActionPolicyController will skip over
        // to catch that branch
        //
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(new VariableDefinitionType());
        //
        // Now do the test - description and
        // TargetType but its empty
        //
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setPolicyData(policy);
        adapter.setPolicyName("name");
        ActionPolicyController controller = new ActionPolicyController();
        assertThatCode(() -> controller.prePopulateActionPolicyData(adapter)).doesNotThrowAnyException();
    }
}
