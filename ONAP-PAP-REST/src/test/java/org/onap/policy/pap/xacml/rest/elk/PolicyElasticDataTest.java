/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.elk;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.Test;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticData;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.adapter.YAMLParams;

public class PolicyElasticDataTest {
    @Test
    public void testSetAndGet() {
        String testKey = "testKey";
        String testVal = "testVal";
        LinkedHashMap<String, String> testMap = new LinkedHashMap<String, String>();
        testMap.put(testKey, testVal);
        ArrayList<Object> testArray = new ArrayList<Object>();
        testArray.add(testVal);
        List<String> testList = new ArrayList<String>();
        testList.add(testVal);
        List<Object> testObjectList = new ArrayList<Object>();
        testObjectList.add(testVal);
        YAMLParams testYaml = new YAMLParams();

        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setConfigPolicyType(testVal);

        PolicyElasticData data = new PolicyElasticData(adapter);
        data.setScope(testVal);
        assertEquals(data.getScope(), testVal);
        data.setPolicyType(testVal);
        assertEquals(data.getPolicyType(), testVal);
        data.setConfigPolicyType(testVal);
        assertEquals(data.getConfigPolicyType(), testVal);
        data.setConfigBodyData(testVal);
        assertEquals(data.getConfigBodyData(), testVal);
        data.setPolicyName(testVal);
        assertEquals(data.getPolicyName(), testVal);
        data.setPolicyDescription(testVal);
        assertEquals(data.getPolicyDescription(), testVal);
        data.setOnapName(testVal);
        assertEquals(data.getOnapName(), testVal);
        data.setConfigName(testVal);
        assertEquals(data.getConfigName(), testVal);
        data.setConfigType(testVal);
        assertEquals(data.getConfigType(), testVal);
        data.setJsonBody(testVal);
        assertEquals(data.getJsonBody(), testVal);
        data.setServiceTypePolicyName(testMap);
        assertEquals(data.getServiceTypePolicyName(), testMap);
        data.setVerticaMetrics(testMap);
        assertEquals(data.getVerticaMetrics(), testMap);
        data.setDescription(testMap);
        assertEquals(data.getDescription(), testMap);
        data.setAttributeFields(testMap);
        assertEquals(data.getAttributeFields(), testMap);
        data.setPolicyScope(testVal);
        assertEquals(data.getPolicyScope(), testVal);
        data.setProviderComboBox(testVal);
        assertEquals(data.getProviderComboBox(), testVal);
        data.setRiskType(testVal);
        assertEquals(data.getRiskType(), testVal);
        data.setRiskLevel(testVal);
        assertEquals(data.getRiskLevel(), testVal);
        data.setGuard(testVal);
        assertEquals(data.getGuard(), testVal);
        data.setTtlDate(testVal);
        assertEquals(data.getTtlDate(), testVal);
        data.setMatching(testMap);
        assertEquals(data.getMatching(), testMap);
        data.setTriggerSignatures(testArray);
        assertEquals(data.getTriggerSignatures(), testArray);
        data.setSymptomSignatures(testArray);
        assertEquals(data.getSymptomSignatures(), testArray);
        data.setLogicalConnector(testVal);
        assertEquals(data.getLogicalConnector(), testVal);
        data.setPolicyStatus(testVal);
        assertEquals(data.getPolicyStatus(), testVal);
        data.setGocServerScope(testVal);
        assertEquals(data.getGocServerScope(), testVal);
        data.setSupressionType(testVal);
        assertEquals(data.getSupressionType(), testVal);
        data.setServiceType(testVal);
        assertEquals(data.getServiceType(),testVal);
        data.setUuid(testVal);
        assertEquals(data.getUuid(), testVal);
        data.setLocation(testVal);
        assertEquals(data.getLocation(), testVal);
        data.setPriority(testVal);
        assertEquals(data.getPriority(), testVal);
        data.setMsLocation(testVal);
        assertEquals(data.getMsLocation(),testVal);
        data.setRuleName(testVal);
        assertEquals(data.getRuleName(), testVal);
        data.setBrmsParamBody(testMap);
        assertEquals(data.getBrmsParamBody(), testMap);
        data.setBrmsController(testVal);
        assertEquals(data.getBrmsController(), testVal);
        data.setBrmsDependency(testList);
        assertEquals(data.getBrmsDependency(), testList);
        data.setRuleData(testMap);
        assertEquals(data.getRuleData(), testMap);
        data.setRuleListData(testMap);
        assertEquals(data.getRuleListData(), testMap);
        data.setDrlRuleAndUIParams(testMap);
        assertEquals(data.getDrlRuleAndUIParams(), testMap);
        data.setClearTimeOut(testVal);
        assertEquals(data.getClearTimeOut(), testVal);
        data.setTrapMaxAge(testVal);
        assertEquals(data.getTrapMaxAge(), testVal);
        data.setVerificationclearTimeOut(testVal);
        assertEquals(data.getVerificationclearTimeOut(), testVal);
        data.setDynamicLayoutMap(testMap);
        assertEquals(data.getDynamicLayoutMap(), testMap);
        data.setFwPolicyType(testVal);
        assertEquals(data.getFwPolicyType(), testVal);
        data.setFwattributes(testArray);
        assertEquals(data.getFwattributes(), testArray);
        data.setParentForChild(testVal);
        assertEquals(data.getParentForChild(), testVal);
        data.setSecurityZone(testVal);
        assertEquals(data.getSecurityZone(), testVal);
        data.setRuleCombiningAlgId(testVal);
        assertEquals(data.getRuleCombiningAlgId(), testVal);
        data.setDynamicFieldConfigAttributes(testMap);
        assertEquals(data.getDynamicFieldConfigAttributes(), testMap);
        data.setDynamicSettingsMap(testMap);
        assertEquals(data.getDynamicSettingsMap(), testMap);
        data.setDropDownMap(testMap);
        assertEquals(data.getDropDownMap(), testMap);
        data.setActionPerformer(testVal);
        assertEquals(data.getActionPerformer(), testVal);
        data.setActionAttribute(testVal);
        assertEquals(data.getActionAttribute(), testVal);
        data.setDynamicRuleAlgorithmLabels(testList);
        assertEquals(data.getDynamicRuleAlgorithmLabels(), testList);
        data.setDynamicRuleAlgorithmCombo(testList);
        assertEquals(data.getDynamicRuleAlgorithmCombo(), testList);
        data.setDynamicRuleAlgorithmField1(testList);
        assertEquals(data.getDynamicRuleAlgorithmField1(), testList);
        data.setDynamicRuleAlgorithmField2(testList);
        assertEquals(data.getDynamicRuleAlgorithmField2(), testList);
        data.setDynamicVariableList(testObjectList);
        assertEquals(data.getDynamicVariableList(), testObjectList);
        data.setDataTypeList(testList);
        assertEquals(data.getDataTypeList(), testList);
        data.setActionAttributeValue(testVal);
        assertEquals(data.getActionAttributeValue(), testVal);
        data.setRuleProvider(testVal);
        assertEquals(data.getRuleProvider(), testVal);
        data.setActionBody(testVal);
        assertEquals(data.getActionBody(), testVal);
        data.setActionDictHeader(testVal);
        assertEquals(data.getActionDictHeader(), testVal);
        data.setActionDictType(testVal);
        assertEquals(data.getActionDictType(), testVal);
        data.setActionDictUrl(testVal);
        assertEquals(data.getActionDictUrl(), testVal);
        data.setActionDictMethod(testVal);
        assertEquals(data.getActionDictMethod(), testVal);
        data.setYamlparams(testYaml);
        assertEquals(data.getYamlparams(), testYaml);
        data.setJsonBodyData(testVal);
        assertEquals(data.getJsonBodyData(), testVal);
    }
}
