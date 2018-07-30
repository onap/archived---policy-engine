/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.rest.adapter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;
import org.onap.policy.rest.jpa.OnapName;

public class PolicyRestAdapterTest {

    @Test
    public void testPolicyExportAdapter(){
        PolicyExportAdapter adapter = new PolicyExportAdapter();
        adapter.setPolicyDatas(new ArrayList<>());
        assertTrue(adapter.getPolicyDatas() != null);
    }

    @Test
    public void testPolicyRestAdapter(){
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setData(new Object());
        assertTrue(adapter.getData() != null);
        adapter.setPolicyName("com.Config_test.1.xml");
        assertTrue("com.Config_test.1.xml".equals(adapter.getPolicyName()));
        adapter.setConfigBodyData("Test");
        assertTrue("Test".equals(adapter.getConfigBodyData()));
        adapter.setConfigType("Config_PM");
        assertTrue("Config_PM".equals(adapter.getConfigType()));
        adapter.setPolicyID("Test");
        assertTrue("Test".equals(adapter.getPolicyID()));
        adapter.setPolicyType("Config");
        assertTrue("Config".equals(adapter.getPolicyType()));
        adapter.setComboPolicyType("Test");
        assertTrue("Test".equals(adapter.getComboPolicyType()));
        adapter.setConfigPolicyType("Test");
        assertTrue("Test".equals(adapter.getConfigPolicyType()));
        adapter.setPolicyDescription("Test");
        assertTrue("Test".equals(adapter.getPolicyDescription()));
        adapter.setOnapName("Test");
        assertTrue("Test".equals(adapter.getOnapName()));
        adapter.setConfigName("Test");
        assertTrue("Test".equals(adapter.getConfigName()));
        adapter.setRuleID("Test");
        assertTrue("Test".equals(adapter.getRuleID()));
        adapter.setParentPath("config/webapps");
        assertTrue("config/webapps".equals(adapter.getParentPath()));
        adapter.setValidData(true);
        assertTrue(adapter.isValidData());
        adapter.setAdminNotification("TestNotification");
        assertTrue("TestNotification".equals(adapter.getAdminNotification()));
        adapter.setEditPolicy(true);
        assertTrue(adapter.isEditPolicy());
        adapter.setViewPolicy(true);
        assertTrue(adapter.isViewPolicy());
        adapter.setDraft(true);
        assertTrue(adapter.isDraft());
        adapter.setPolicyData("Test Policy");
        assertTrue("Test Policy".equals(adapter.getPolicyData()));
        adapter.setGitPath("config/webapps");
        assertTrue("config/webapps".equals(adapter.getGitPath()));
        adapter.setReadOnly(true);
        assertTrue(adapter.isReadOnly());
        adapter.setConfigName("Test");
        assertTrue("Test".equals(adapter.getConfigName()));
        adapter.setConfigUrl("Test");
        assertTrue("Test".equals(adapter.getConfigUrl()));
        adapter.setFinalPolicyPath("config/webapps");
        assertTrue("config/webapps".equals(adapter.getFinalPolicyPath()));
        adapter.setVersion("1");
        assertTrue("1".equals(adapter.getVersion()));
        adapter.setJsonBody("Test");
        assertTrue("Test".equals(adapter.getJsonBody()));
        adapter.setApiflag("API");
        assertTrue("API".equals(adapter.getApiflag()));
        adapter.setPrevJsonBody("Test");
        assertTrue("Test".equals(adapter.getPrevJsonBody()));
        adapter.setHighestVersion(1);
        assertTrue(1 == adapter.getHighestVersion());
        adapter.setPolicyExists(true);
        assertTrue(adapter.getPolicyExists());
        adapter.setOldPolicyFileName("Config_oldtest.1.xml");
        assertTrue("Config_oldtest.1.xml".equals(adapter.getOldPolicyFileName()));
        adapter.setDomainDir("Test");
        assertTrue("Test".equals(adapter.getDomainDir()));
        adapter.setUserId("Test");
        assertTrue("Test".equals(adapter.getUserId()));
        adapter.setNewFileName("Test");
        assertTrue("Test".equals(adapter.getNewFileName()));
        adapter.setClWarning("Test");
        assertTrue("Test".equals(adapter.getClWarning()));
        adapter.setNewCLName("Test");
        assertTrue("Test".equals(adapter.getNewCLName()));
        adapter.setExistingCLName("Test");
        assertTrue("Test".equals(adapter.getExistingCLName()));
        adapter.setOnapNameField(new OnapName());
        assertTrue(adapter.getOnapNameField()!=null);
        adapter.setJsonBodyData(new Object());
        assertTrue(adapter.getJsonBodyData()!=null);
        adapter.setDirPath("Test");
        assertTrue("Test".equals(adapter.getDirPath()));
        adapter.setConfigBodyPath("Test");
        assertTrue("Test".equals(adapter.getConfigBodyPath()));
        adapter.setAttributes(new ArrayList<>());
        assertTrue(adapter.getAttributes()!=null);
        adapter.setSettings(new ArrayList<>());
        assertTrue(adapter.getSettings()!=null);
        adapter.setRuleAlgorithmschoices(new ArrayList<>());
        assertTrue(adapter.getRuleAlgorithmschoices()!=null);
        adapter.setServiceTypePolicyName(new HashMap<>());
        assertTrue(adapter.getServiceTypePolicyName()!=null);
        adapter.setVerticaMetrics(new HashMap<>());
        assertTrue(adapter.getVerticaMetrics()!=null);
        adapter.setDescription(new LinkedHashMap<>());
        assertTrue(adapter.getVerticaMetrics()!=null);
        adapter.setAttributeFields(new LinkedHashMap<>());
        assertTrue(adapter.getAttributeFields()!=null);
        adapter.setClearTimeOut("Test");
        assertTrue("Test".equals(adapter.getClearTimeOut()));
        adapter.setTrapMaxAge("Test");
        assertTrue("Test".equals(adapter.getTrapMaxAge()));
        adapter.setVerificationclearTimeOut("Test");
        assertTrue("Test".equals(adapter.getVerificationclearTimeOut()));
        adapter.setDynamicLayoutMap(new HashMap<>());
        assertTrue(adapter.getDynamicLayoutMap()!=null);
        adapter.setTrapDatas(new ClosedLoopFaultTrapDatas());
        assertTrue(adapter.getTrapDatas()!=null);
        adapter.setFaultDatas(new ClosedLoopFaultTrapDatas());
        assertTrue(adapter.getFaultDatas()!=null);
        adapter.setFwPolicyType("Test");
        assertTrue("Test".equals(adapter.getFwPolicyType()));
        adapter.setFwattributes(new ArrayList<>());
        assertTrue(adapter.getFwattributes()!=null);
        adapter.setParentForChild("Test");
        assertTrue("Test".equals(adapter.getParentForChild()));
        adapter.setSecurityZone("Test");
        assertTrue("Test".equals(adapter.getSecurityZone()));
        adapter.setRuleCombiningAlgId("Test");
        assertTrue("Test".equals(adapter.getRuleCombiningAlgId()));
        adapter.setDynamicFieldConfigAttributes(new HashMap<>());
        assertTrue(adapter.getDynamicFieldConfigAttributes()!=null);
        adapter.setDynamicSettingsMap(new HashMap<>());
        assertTrue(adapter.getDynamicSettingsMap()!=null);
        adapter.setDropDownMap(new HashMap<>());
        assertTrue(adapter.getDropDownMap()!=null);
        adapter.setActionPerformer("Test");
        assertTrue("Test".equals(adapter.getActionPerformer()));
        adapter.setActionAttribute("Test");
        assertTrue("Test".equals(adapter.getActionAttribute()));
        adapter.setDynamicRuleAlgorithmLabels(new ArrayList<>());
        assertTrue(adapter.getDynamicRuleAlgorithmLabels()!=null);
        adapter.setDynamicRuleAlgorithmCombo(new ArrayList<>());
        assertTrue(adapter.getDynamicRuleAlgorithmCombo()!=null);
        adapter.setDynamicRuleAlgorithmField1(new ArrayList<>());
        assertTrue(adapter.getDynamicRuleAlgorithmField1()!=null);
        adapter.setDynamicRuleAlgorithmField2(new ArrayList<>());
        assertTrue(adapter.getDynamicRuleAlgorithmField2()!=null);
        adapter.setDynamicVariableList(new ArrayList<>());
        assertTrue(adapter.getDynamicVariableList()!=null);
        adapter.setDataTypeList(new ArrayList<>());
        assertTrue(adapter.getDataTypeList()!=null);
        adapter.setActionAttributeValue("Test");
        assertTrue("Test".equals(adapter.getActionAttributeValue()));
        adapter.setRuleProvider("Test");
        assertTrue("Test".equals(adapter.getRuleProvider()));
        adapter.setActionBody("Test");
        assertTrue("Test".equals(adapter.getActionBody()));
        adapter.setActionDictHeader("Test");
        assertTrue("Test".equals(adapter.getActionDictHeader()));
        adapter.setActionDictType("Test");
        assertTrue("Test".equals(adapter.getActionDictType()));
        adapter.setActionDictUrl("Test");
        assertTrue("Test".equals(adapter.getActionDictUrl()));
        adapter.setActionDictMethod("Test");
        assertTrue("Test".equals(adapter.getActionDictMethod()));
        adapter.setYamlparams(new YAMLParams());
        assertTrue(adapter.getYamlparams()!=null);
        adapter.setRainyday(new RainyDayParams());
        assertTrue(adapter.getRainyday()!=null);
        adapter.setRainydayMap(new HashMap<>());
        assertTrue(adapter.getRainydayMap()!=null);
        adapter.setErrorCodeList(new ArrayList<>());
        assertTrue(adapter.getErrorCodeList()!=null);
        adapter.setTreatmentList(new ArrayList<>());
        assertTrue(adapter.getTreatmentList()!=null);
        adapter.setServiceType("Test");
        assertTrue("Test".equals(adapter.getServiceType()));
        adapter.setUuid("Test");
        assertTrue("Test".equals(adapter.getUuid()));
        adapter.setLocation("Test");
        assertTrue("Test".equals(adapter.getLocation()));
        adapter.setPriority("Test");
        assertTrue("Test".equals(adapter.getPriority()));
        adapter.setMsLocation("Test");
        assertTrue("Test".equals(adapter.getMsLocation()));
        adapter.setPolicyJSON("Test");
        assertTrue("Test".equals(adapter.getPolicyJSON()));
        adapter.setRuleName("Test");
        assertTrue("Test".equals(adapter.getRuleName()));
        adapter.setBrmsParamBody(new HashMap<>());
        assertTrue(adapter.getBrmsParamBody()!=null);
        adapter.setBrmsController("Test");
        assertTrue("Test".equals(adapter.getBrmsController()));
        adapter.setBrmsDependency(new ArrayList<>());
        assertTrue(adapter.getBrmsDependency()!=null);
        adapter.setRuleData(new LinkedHashMap<>());
        assertTrue(adapter.getRuleData()!=null);
        adapter.setRuleListData(new LinkedHashMap<>());
        assertTrue(adapter.getRuleListData()!=null);
        adapter.setDrlRuleAndUIParams(new LinkedHashMap<>());
        assertTrue(adapter.getDrlRuleAndUIParams()!=null);
        adapter.setPolicyScope("Test");
        assertTrue("Test".equals(adapter.getPolicyScope()));
        adapter.setProviderComboBox("Test");
        assertTrue("Test".equals(adapter.getProviderComboBox()));
        adapter.setRiskType("Test");
        assertTrue("Test".equals(adapter.getRiskType()));
        adapter.setRiskLevel("Test");
        assertTrue("Test".equals(adapter.getRiskLevel()));
        adapter.setGuard("Test");
        assertTrue("Test".equals(adapter.getGuard()));
        adapter.setTtlDate("Test");
        assertTrue("Test".equals(adapter.getTtlDate()));
        adapter.setMatching(new LinkedHashMap<>());
        assertTrue(adapter.getMatching()!=null);
        adapter.setTriggerSignatures(new ArrayList<>());
        assertTrue(adapter.getTriggerSignatures()!=null);
        adapter.setSymptomSignatures(new ArrayList<>());
        assertTrue(adapter.getSymptomSignatures()!=null);
        adapter.setLogicalConnector("Test");
        assertTrue("Test".equals(adapter.getLogicalConnector()));
        adapter.setPolicyStatus("Test");
        assertTrue("Test".equals(adapter.getPolicyStatus()));
        adapter.setGocServerScope("Test");
        assertTrue("Test".equals(adapter.getGocServerScope()));
        adapter.setSupressionType("Test");
        assertTrue("Test".equals(adapter.getSupressionType()));
    }
}
