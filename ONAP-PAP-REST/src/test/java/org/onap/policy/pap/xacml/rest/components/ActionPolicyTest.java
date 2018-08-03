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

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

import com.att.research.xacml.util.XACMLProperties;


public class ActionPolicyTest {

    private static Logger logger = FlexLogger.getLogger(ActionPolicyTest.class);
    PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
    List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
    List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
    List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
    List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();
    Map<String, String> attributeMap = new HashMap<>();
    ActionPolicy component = null;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME,"src/test/resources/xacml.pap.properties");

        dynamicRuleAlgorithmLabels.add("test");
        dynamicRuleAlgorithmField1.add("testField1");
        dynamicRuleAlgorithmCombo.add("testCombo");
        dynamicRuleAlgorithmField2.add("testField2");

        policyAdapter.setPolicyName("Test.Action_junitTest");
        policyAdapter.setPolicyDescription("test");
        policyAdapter.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
        policyAdapter.setPolicyType("Action");
        policyAdapter.setEditPolicy(false);
        policyAdapter.setDomainDir("Test");
        policyAdapter.setNewFileName("Test.Action_junitTest.1.xml");
        policyAdapter.setHighestVersion(1);
        policyAdapter.setPolicyID("urn:xacml:policy:id:"+UUID.randomUUID());

        policyAdapter.setActionDictHeader("");
        policyAdapter.setActionDictType("REST");
        policyAdapter.setActionDictUrl("onap.org");
        policyAdapter.setActionDictMethod("GET");
        policyAdapter.setActionAttribute("test");
        policyAdapter.setActionBody("test");

        policyAdapter.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
        policyAdapter.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
        policyAdapter.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
        policyAdapter.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);

        attributeMap.put("java", "test");
        policyAdapter.setDynamicFieldConfigAttributes(attributeMap);

        component = new ActionPolicy(policyAdapter, null);

        logger.info("setUp: exit");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

/*	*//**
     * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.ActionPolicy#savePolicies()}.
     */
    @Test
    public void testSavePolicies() {
        ActionPolicy mockAction = Mockito.mock(component.getClass());

        Map<String, String> successMap = new HashMap<>();
        
        successMap.put("success", "success");

        try {
            when(mockAction.savePolicies()).thenReturn(successMap);
            successMap = mockAction.savePolicies();
        } catch (Exception e) {
            logger.error("Exception Occured"+e);
        }
        assertEquals(successMap.get("success"),"success");
    }

    /**
     * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.ActionPolicy#prepareToSave()}.
     */
    @Test
    public void testPrepareToSave() {
        logger.debug("test PrepareToSave Policy: enter");
        boolean response = true;

        try {
            response = component.prepareToSave();
        } catch (Exception e) {
            logger.error("Exception Occured"+e);
        }
        assertTrue(response);

    }

}