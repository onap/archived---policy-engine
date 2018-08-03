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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.adapter.RainyDayParams;

import com.att.research.xacml.util.XACMLProperties;

public class DecisionPolicyTest {

    private static Logger logger = FlexLogger.getLogger(DecisionPolicyTest.class);
    PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
    RainyDayParams rainyday = new RainyDayParams();
    Map<String, String> attributeMap = new HashMap<>();
    Map<String, String> treatmentMap = new HashMap<>();
    Map<String, String> settingsMap = new HashMap<>();
    List<String> errorCodeList = new LinkedList<>();
    List<String> treatmentList = new LinkedList<>();
    List<String> dynamicRuleAlgorithmLabels = new LinkedList<>();
    List<String> dynamicRuleAlgorithmCombo = new LinkedList<>();
    List<String> dynamicRuleAlgorithmField1 = new LinkedList<>();
    List<String> dynamicRuleAlgorithmField2 = new LinkedList<>();
    List<Object> dynamicVariableList = new LinkedList<>();
    List<String> dataTypeList = new LinkedList<>();
    DecisionPolicy component = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME,"src/test/resources/xacml.pap.properties");

        policyAdapter.setPolicyName("Test.Decision_junitTest.1.xml");
        policyAdapter.setPolicyDescription("testing");
        policyAdapter.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
        policyAdapter.setPolicyType("Decision");
        policyAdapter.setEditPolicy(false);
        policyAdapter.setDomainDir("Test");
        policyAdapter.setNewFileName("/src/test/resources/Test/client.properties");
        policyAdapter.setHighestVersion(1);
        policyAdapter.setPolicyID("urn:xacml:policy:id:"+UUID.randomUUID());
        policyAdapter.setOnapName("MSO");

        //rainy day attributes
        attributeMap.put("ServiceType", "S");
        attributeMap.put("VNFType", "V");
        attributeMap.put("BB_ID", "testBB");
        attributeMap.put("WorkStep", "1");

        dynamicRuleAlgorithmLabels.add("test1");
        dynamicRuleAlgorithmField1.add("testField1");
        dynamicRuleAlgorithmCombo.add("testruleCombo");
        dynamicRuleAlgorithmField2.add("testField2");

        policyAdapter.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
        policyAdapter.setDynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo);
        policyAdapter.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
        policyAdapter.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
        policyAdapter.setDynamicVariableList(dynamicVariableList);
        policyAdapter.setDynamicSettingsMap(settingsMap);
        policyAdapter.setDataTypeList(dataTypeList);

        policyAdapter.setDynamicFieldConfigAttributes(attributeMap);
        policyAdapter.setRainydayMap(treatmentMap);
        policyAdapter.setRainyday(rainyday);

        component = new DecisionPolicy(policyAdapter, null);

        logger.info("setUp: exit");
    }

    /**
     * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.DecisionPolicy#savePolicies()}.
     */
    @Test
    public void testSavePolicies() {
        DecisionPolicy mockDecision = Mockito.mock(component.getClass());

        Map<String, String> successMap = new HashMap<>();
        successMap.put("success", "success");

        try {
            when(mockDecision.savePolicies()).thenReturn(successMap);
            successMap = mockDecision.savePolicies();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(successMap.get("success"),"success");
    }

    /**
     * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.DecisionPolicy#prepareToSave()}.
     */
    @Test
    public void testPrepareToSaveRainyDay() {
        logger.debug("test PrepareToSave Policy: enter");
        policyAdapter.setRuleProvider("Rainy_Day");
        component = new DecisionPolicy(policyAdapter, null);
        boolean response = false;

        try {
            response = component.prepareToSave();
        } catch (Exception e) {
            logger.error("Exception Occured"+e);
        }
        assertTrue(response);
    }
}