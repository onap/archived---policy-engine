/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.att.research.xacml.api.XACML3;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyEntity;

public class CreatePolicyControllerTest {

    private PolicyEntity entity;

    /**
     * before - sets up the mocked PolicyEntity.
     */
    @Before
    public void before() {
        //
        // PolicyEntity
        //
        ConfigurationDataEntity dataEntity = mock(ConfigurationDataEntity.class);
        when(dataEntity.getConfigType()).thenReturn("configtype");
        entity = mock(PolicyEntity.class);
        when(entity.getConfigurationData()).thenReturn(dataEntity);
    }

    @Test
    public void testEasyStuff() {
        CreatePolicyController controller = new CreatePolicyController();
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        controller.prePopulateBaseConfigPolicyData(adapter, null);
        //
        // Create a simple PolicyType
        //
        VariableDefinitionType var = new VariableDefinitionType();
        PolicyType policy = new PolicyType();
        policy.setDescription("i am a description. @CreatedBy: policy designer");
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(var);
        adapter.setPolicyData(policy);
        adapter.setPolicyName("name");
        controller.prePopulateBaseConfigPolicyData(adapter, entity);
    }

    @Test
    public void testBadDescription() {
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        //
        // Create a simple PolicyType
        //
        VariableDefinitionType var = new VariableDefinitionType();
        PolicyType policy = new PolicyType();
        policy.setDescription("i am a description");
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(var);
        adapter.setPolicyData(policy);
        adapter.setPolicyName("name");
        CreatePolicyController controller = new CreatePolicyController();
        controller.prePopulateBaseConfigPolicyData(adapter, entity);
    }

    @Test
    public void testExpectedPolicyContents() {
        AllOfType allOf = new AllOfType();
        allOf.getMatch().add(createMatchType("ONAPName", "ONAPName"));
        allOf.getMatch().add(createMatchType("RiskType", "RiskType"));
        allOf.getMatch().add(createMatchType("RiskLevel", "RiskLevel"));
        allOf.getMatch().add(createMatchType("guard", "guard"));
        allOf.getMatch().add(createMatchType("TTLDate", "TTLDate"));
        allOf.getMatch().add(createMatchType("ConfigName", "ConfigName"));
        allOf.getMatch().add(createMatchType("NA", "TTLDate"));
        allOf.getMatch().add(createMatchType("custom", "custom"));
        allOf.getMatch().add(createMatchType("custom", "custom"));
        allOf.getMatch().add(createMatchType("custom", "custom"));

        AnyOfType anyOf = new AnyOfType();
        anyOf.getAllOf().add(allOf);

        TargetType target = new TargetType();
        target.getAnyOf().add(anyOf);

        RuleType rule = new RuleType();
        PolicyType policy = new PolicyType();
        policy.setDescription("i am a description. @CreatedBy: policy designer");
        policy.setTarget(target);
        policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setPolicyData(policy);
        adapter.setPolicyName("name");
        CreatePolicyController controller = new CreatePolicyController();
        controller.prePopulateBaseConfigPolicyData(adapter, entity);
    }

    private MatchType createMatchType(String strValue, String id) {
        AttributeValueType value = new AttributeValueType();
        value.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        value.getContent().add(new String(strValue));
        AttributeDesignatorType designator = new AttributeDesignatorType();
        designator.setAttributeId(id);

        MatchType match = new MatchType();
        match.setAttributeValue(value);
        match.setAttributeDesignator(designator);

        return match;
    }

}
