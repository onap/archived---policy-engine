/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.xacml.test.std.pap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPAPPolicyParams;

public class StdPAPPolicyTest {

    private static Logger logger = FlexLogger.getLogger(StdPAPPolicyTest.class);
    Properties properties = new Properties();
    StdPAPPolicy stdPAPPolicy;

    @Before
    public void setUp() {
        stdPAPPolicy = new StdPAPPolicy();
    }

    @Test
    public void testGetActionAttribute() throws URISyntaxException {
        stdPAPPolicy.setActionAttribute("test");
        assertEquals("test", stdPAPPolicy.getActionAttribute());
        stdPAPPolicy.setActionBody("actionBody");
        assertNotNull(stdPAPPolicy.getActionBody());
        stdPAPPolicy.setActionDictHeader("actionDictHeader");
        assertNotNull(stdPAPPolicy.getActionDictHeader());
        stdPAPPolicy.setActionDictMethod("actionDictMethod");
        assertNotNull(stdPAPPolicy.getActionDictMethod());
        stdPAPPolicy.setActionDictType("actionDictType");
        assertNotNull(stdPAPPolicy.getActionDictType());
        stdPAPPolicy.setActionDictUrl("actionDictUrl");
        assertNotNull(stdPAPPolicy.getActionDictUrl());
        stdPAPPolicy.setActionPerformer("actionPerformer");
        assertNotNull(stdPAPPolicy.getActionPerformer());
        stdPAPPolicy.setBrmsController("brmsController");
        assertNotNull(stdPAPPolicy.getBrmsController());
        stdPAPPolicy.setBrmsDependency(new ArrayList<>());
        assertNotNull(stdPAPPolicy.getBrmsDependency());
        stdPAPPolicy.setConfigBodyData("configBodyData");
        assertNotNull(stdPAPPolicy.getConfigBodyData());
        stdPAPPolicy.setConfigName("configName");
        assertNotNull(stdPAPPolicy.getConfigName());
        stdPAPPolicy.setConfigPolicyType("configPolicyType");
        assertNotNull(stdPAPPolicy.getConfigPolicyType());
        stdPAPPolicy.setConfigType("configType");
        assertNotNull(stdPAPPolicy.getConfigType());
        stdPAPPolicy.setDataTypeList(new ArrayList<String>());
        assertNotNull(stdPAPPolicy.getDataTypeList());
        stdPAPPolicy.setDeleteCondition("deleteCondition");
        assertNotNull(stdPAPPolicy.getDeleteCondition());
        stdPAPPolicy.setDrlRuleAndUIParams(new HashMap<>());
        assertNotNull(stdPAPPolicy.getDrlRuleAndUIParams());
        stdPAPPolicy.setDropDownMap(new HashMap<>());
        assertNotNull(stdPAPPolicy.getDropDownMap() );
        Map<String, String> dynamic = new HashMap<>();
        dynamic.put("foo", "bar");
        stdPAPPolicy.setDynamicFieldConfigAttributes(dynamic);
        assertEquals(dynamic, stdPAPPolicy.getDynamicFieldConfigAttributes());
        stdPAPPolicy.setDynamicRuleAlgorithmCombo(new ArrayList<>());
        assertNotNull(stdPAPPolicy.getDynamicRuleAlgorithmCombo());
        stdPAPPolicy.setDynamicRuleAlgorithmField1(new ArrayList<>());
        assertNotNull(stdPAPPolicy.getDynamicRuleAlgorithmField1());
        stdPAPPolicy.setDynamicRuleAlgorithmField2(new ArrayList<>());
        assertNotNull(stdPAPPolicy.getDynamicRuleAlgorithmField2());
        stdPAPPolicy.setDictionary("dictionary");
        assertNotNull(stdPAPPolicy.getDictionary());
        stdPAPPolicy.setDictionaryFields("dictionaryFields");
        assertNotNull(stdPAPPolicy.getDictionaryFields());
        stdPAPPolicy.setDictionaryType("dictionaryType");
        assertNotNull(stdPAPPolicy.getDictionaryType());
        stdPAPPolicy.setDomainDir("domain");
        assertNotNull(stdPAPPolicy.getDomainDir());
        stdPAPPolicy.setDraft(true);
        assertTrue(stdPAPPolicy.isDraft() == true);
        stdPAPPolicy.setDynamicRuleAlgorithmLabels(new ArrayList<>());
        assertNotNull(stdPAPPolicy.getDynamicRuleAlgorithmLabels());
        stdPAPPolicy.setDynamicSettingsMap(new HashMap<>());
        assertNotNull(stdPAPPolicy.getDynamicSettingsMap());
        stdPAPPolicy.setDynamicVariableList(new ArrayList<>());
        assertNotNull(stdPAPPolicy.getDynamicVariableList());
        stdPAPPolicy.setGuard("domain");
        assertNotNull(stdPAPPolicy.getGuard());
        stdPAPPolicy.setHighestVersion(123);
        assertNotNull(stdPAPPolicy.getHighestVersion());
        stdPAPPolicy.setJsonBody("jsonBoby");
        assertNotNull(stdPAPPolicy.getJsonBody());
        stdPAPPolicy.setLocation(new URI("test"));
        assertNotNull(stdPAPPolicy.getLocation());
        stdPAPPolicy.setMsLocation("MsLocation");
        assertNotNull(stdPAPPolicy.getMsLocation());
        stdPAPPolicy.setOldPolicyFileName("domain");
        assertNotNull(stdPAPPolicy.getOldPolicyFileName());
        stdPAPPolicy.setOnapName("onap");
        assertTrue(stdPAPPolicy.getOnapName() != null);
        stdPAPPolicy.setPolicyDescription("description test");
        assertNotNull(stdPAPPolicy.getPolicyDescription());
        stdPAPPolicy.setPolicyID("test");
        assertNotNull(stdPAPPolicy.getPolicyID());
        stdPAPPolicy.setPolicyName("MsLocation");
        assertNotNull(stdPAPPolicy.getPolicyName());
        stdPAPPolicy.setPriority("domain");
        assertNotNull(stdPAPPolicy.getPriority());
        stdPAPPolicy.setProviderComboBox("onap");
        assertNotNull(stdPAPPolicy.getProviderComboBox());
        stdPAPPolicy.setRiskLevel("test");
        assertNotNull(stdPAPPolicy.getRiskLevel());
        stdPAPPolicy.setRiskType("test");
        assertNotNull(stdPAPPolicy.getRiskType());
        stdPAPPolicy.setRuleID("MsLocation");
        assertNotNull(stdPAPPolicy.getRuleID());
        stdPAPPolicy.setServiceType("domain");
        assertNotNull(stdPAPPolicy.getServiceType());
        stdPAPPolicy.setTTLDate("09/20/17");
        assertNotNull(stdPAPPolicy.getTTLDate());
        stdPAPPolicy.setUuid("11212122");
        assertNotNull(stdPAPPolicy.getUuid());
        stdPAPPolicy.setVersion("testv01");
        assertNotNull(stdPAPPolicy.getVersion());
        stdPAPPolicy.setEditPolicy(true);
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertNotNull(stdPAPPolicy.toString());
        Map<String, String> treatments = new HashMap<>();
        stdPAPPolicy.setTreatments(treatments);
        assertEquals(treatments, stdPAPPolicy.getTreatments());
        stdPAPPolicy.setRawXacmlPolicy("raw");
        assertEquals("raw", stdPAPPolicy.getRawXacmlPolicy());
    }

    @Test
    public void testConstructorUri() throws URISyntaxException {
        URI location = new URI("testUri");
        StdPAPPolicy stdPapPolicy = new StdPAPPolicy(location);
        assertEquals(location, stdPapPolicy.getLocation());
    }

    @Test
    public void testConstructorStringStringStringString() throws URISyntaxException {
        StdPAPPolicy stdPapPolicy = new StdPAPPolicy("policyName", "body", "configType", "configTypePolicy");
        assertEquals("policyName", stdPapPolicy.getPolicyName());
        assertEquals("body", stdPapPolicy.getConfigBodyData());
        assertEquals("configType", stdPapPolicy.getConfigType());
        assertEquals("configTypePolicy", stdPapPolicy.getConfigPolicyType());
    }

    @Test
    public void testConstructorStringStringStringStringStringMapStringStringStringStringBooleanStringStringStringStringString()
            throws URISyntaxException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("aKey", "aValue");

        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").onapName("onapName").configName("configName")
                .dynamicFieldConfigAttributes(attributes).configType("configType").configBodyData("body")
                .editPolicy(true).domain("domain").highestVersion(1).riskLevel("riskLevel").riskType("riskType")
                .guard("guard").ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("onapName", stdPAPPolicy.getOnapName());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertEquals(attributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals("configType", stdPAPPolicy.getConfigType());
        assertEquals("body", stdPAPPolicy.getConfigBodyData());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringMapStringStringListStringListStringListStringListStringStringStringBooleanStringint() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("aKey", "aValue");
        List<String> dynamicRuleAlgorithmLabels = new ArrayList<>();
        dynamicRuleAlgorithmLabels.add("dynamicRuleAlgorithmLabel");
        List<String> dynamicRuleAlgorithmCombo = new ArrayList<>();
        dynamicRuleAlgorithmCombo.add("dynamicRuleAlgorithmCombo");
        List<String> dynamicRuleAlgorithmField1 = new ArrayList<>();
        dynamicRuleAlgorithmField1.add("dynamicRuleAlgorithmField1");
        List<String> dynamicRuleAlgorithmField2 = new ArrayList<>();
        dynamicRuleAlgorithmField2.add("dynamicRuleAlgorithmField2");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().policyName("policyName")
                .description("description").dynamicFieldConfigAttributes(attributes)
                .dynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels)
                .dynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo)
                .dynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1)
                .dynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2).actionPerformer("actionPerformer")
                .actionAttribute("actionAttribute").editPolicy(true).domain("domain").highestVersion(1).build());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals(attributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals(dynamicRuleAlgorithmLabels, stdPAPPolicy.getDynamicRuleAlgorithmLabels());
        assertEquals(dynamicRuleAlgorithmCombo, stdPAPPolicy.getDynamicRuleAlgorithmCombo());
        assertEquals(dynamicRuleAlgorithmField1, stdPAPPolicy.getDynamicRuleAlgorithmField1());
        assertEquals(dynamicRuleAlgorithmField2, stdPAPPolicy.getDynamicRuleAlgorithmField2());
        assertEquals("actionPerformer", stdPAPPolicy.getActionPerformer());
        assertEquals("actionAttribute", stdPAPPolicy.getActionAttribute());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
    }

    @Test
    public void testConstructorStringStringStringStringMapStringStringMapStringStringMapStringStringListStringListStringListStringListStringMapStringStringListObjectListStringBooleanStringint() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("aKey1", "aValue1");
        Map<String, String> settings = new HashMap<>();
        settings.put("aKey2", "aValue2");
        Map<String, String> treatments = new HashMap<>();
        treatments.put("aKey3", "aValue3");
        Map<String, String> dropDownMap = new HashMap<>();
        dropDownMap.put("aKey4", "aValue4");
        List<String> dynamicRuleAlgorithmLabels = new ArrayList<>();
        dynamicRuleAlgorithmLabels.add("dynamicRuleAlgorithmLabel");
        List<String> dynamicRuleAlgorithmCombo = new ArrayList<>();
        dynamicRuleAlgorithmCombo.add("dynamicRuleAlgorithmCombo");
        List<String> dynamicRuleAlgorithmField1 = new ArrayList<>();
        dynamicRuleAlgorithmField1.add("dynamicRuleAlgorithmField1");
        List<String> dynamicRuleAlgorithmField2 = new ArrayList<>();
        dynamicRuleAlgorithmField2.add("dynamicRuleAlgorithmField2");
        List<Object> dynamicVariableList = new ArrayList<>();
        dynamicVariableList.add("dynamicVariableList");
        List<String> dataTypeList = new ArrayList<>();
        dataTypeList.add("dataTypeList");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().policyName("policyName")
                .description("description").onapName("onapName").providerComboBox("providerComboBox")
                .dynamicFieldConfigAttributes(attributes).dynamicSettingsMap(settings).treatments(treatments)
                .dynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels)
                .dynamicRuleAlgorithmCombo(dynamicRuleAlgorithmCombo)
                .dynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1)
                .dynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2).dropDownMap(dropDownMap)
                .dynamicVariableList(dynamicVariableList).dataTypeList(dataTypeList).editPolicy(true).domain("domain")
                .highestVersion(1).build());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("onapName", stdPAPPolicy.getOnapName());
        assertEquals("providerComboBox", stdPAPPolicy.getProviderComboBox());
        assertEquals(attributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals(settings, stdPAPPolicy.getDynamicSettingsMap());
        assertEquals(treatments, stdPAPPolicy.getTreatments());
        assertEquals(dynamicRuleAlgorithmLabels, stdPAPPolicy.getDynamicRuleAlgorithmLabels());
        assertEquals(dynamicRuleAlgorithmCombo, stdPAPPolicy.getDynamicRuleAlgorithmCombo());
        assertEquals(dynamicRuleAlgorithmField1, stdPAPPolicy.getDynamicRuleAlgorithmField1());
        assertEquals(dynamicRuleAlgorithmField2, stdPAPPolicy.getDynamicRuleAlgorithmField2());
        assertEquals(dropDownMap, stdPAPPolicy.getDropDownMap());
        assertEquals(dynamicVariableList, stdPAPPolicy.getDynamicVariableList());
        assertEquals(dataTypeList, stdPAPPolicy.getDataTypeList());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
    }

    @Test
    public void testConstructorStringStringStringStringStringMapStringStringStringStringStringStringBooleanStringStringintStringStringStringString()
            throws URISyntaxException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("aKey", "aValue");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").onapName("onapName").configName("configName")
                .dynamicFieldConfigAttributes(attributes).configBodyData("body").policyID("policyId").ruleID("ruleId")
                .configType("configType").editPolicy(true).version("version").domain("domain").highestVersion(1)
                .riskLevel("riskLevel").riskType("riskType").guard("guard").ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("onapName", stdPAPPolicy.getOnapName());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertEquals(attributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals("body", stdPAPPolicy.getConfigBodyData());
        assertEquals("policyId", stdPAPPolicy.getPolicyID());
        assertEquals("ruleId", stdPAPPolicy.getRuleID());
        assertEquals("configType", stdPAPPolicy.getConfigType());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("version", stdPAPPolicy.getVersion());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringBooleanStringStringIntegerStringStringStringString()
            throws URISyntaxException {
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").jsonBody("jasonBody").highestVersion(1).riskLevel("riskLevel").riskType("riskType")
                .guard("guard").ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("jasonBody", stdPAPPolicy.getJsonBody());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringBooleanStringStringIntegerStringStringStringStringString()
            throws URISyntaxException {
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").jsonBody("jasonBody").highestVersion(1).onapName("eCompName").riskLevel("riskLevel")
                .riskType("riskType").guard("guard").ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("jasonBody", stdPAPPolicy.getJsonBody());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("eCompName", stdPAPPolicy.getOnapName());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorMapStringStringIntegerStringStringStringStringStringStringStringArrayListString()
            throws URISyntaxException {
        Map<String, String> dyanamicFieldConfigAttributes = new HashMap<>();
        dyanamicFieldConfigAttributes.put("aKey", "aValue");
        ArrayList<String> brmsDependency = new ArrayList<>();
        brmsDependency.add("brmsDependency");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").dynamicFieldConfigAttributes(dyanamicFieldConfigAttributes).highestVersion(1)
                .onapName("eCompName").configBodyData("configBodyData").riskLevel("riskLevel").riskType("riskType")
                .guard("guard").ttlDate("ttlDate").brmsController("brmsController").brmsDependency(brmsDependency)
                .build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals(dyanamicFieldConfigAttributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("eCompName", stdPAPPolicy.getOnapName());
        assertEquals("configBodyData", stdPAPPolicy.getConfigBodyData());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
        assertEquals("brmsController", stdPAPPolicy.getBrmsController());
        assertEquals(brmsDependency, stdPAPPolicy.getBrmsDependency());
    }

    @Test
    public void testConstructorMapStringStringIntegerStringStringMapStringStringStringStringStringStringStringArrayListString()
            throws URISyntaxException {
        Map<String, String> dyanamicFieldConfigAttributes = new HashMap<>();
        dyanamicFieldConfigAttributes.put("aKey", "aValue");
        Map<String, String> drlRuleAndUIParams = new HashMap<>();
        drlRuleAndUIParams.put("aDrlRuleKey", "aDrlRuleValue");
        ArrayList<String> brmsDependency = new ArrayList<>();
        brmsDependency.add("brmsDependency");
        // Creating BRMS Param Policies from the Admin Console
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").dynamicFieldConfigAttributes(dyanamicFieldConfigAttributes).highestVersion(1)
                .onapName("eCompName").configBodyData("configBodyData").drlRuleAndUIParams(drlRuleAndUIParams)
                .riskLevel("riskLevel").riskType("riskType").guard("guard").ttlDate("ttlDate")
                .brmsController("brmsController").brmsDependency(brmsDependency).build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals(dyanamicFieldConfigAttributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("eCompName", stdPAPPolicy.getOnapName());
        assertEquals("configBodyData", stdPAPPolicy.getConfigBodyData());
        assertEquals(drlRuleAndUIParams, stdPAPPolicy.getDrlRuleAndUIParams());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
        assertEquals("brmsController", stdPAPPolicy.getBrmsController());
        assertEquals(brmsDependency, stdPAPPolicy.getBrmsDependency());
    }

    @Test
    public void testConstructorStringStringStringStringStringBooleanStringStringBooleanStringIntegerStringStringStringString()
            throws URISyntaxException {
        // Creating CloseLoop_Fault and Performance Metric Policies
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").onapName("onapName").jsonBody("jasonBody")
                .draft(true).oldPolicyFileName("oldPolicyFileName").serviceType("serviceType").editPolicy(true)
                .domain("domain").highestVersion(1).riskLevel("riskLevel").riskType("riskType").guard("guard")
                .ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("onapName", stdPAPPolicy.getOnapName());
        assertEquals("jasonBody", stdPAPPolicy.getJsonBody());
        assertTrue(stdPAPPolicy.isDraft());
        assertEquals("oldPolicyFileName", stdPAPPolicy.getOldPolicyFileName());
        assertEquals("serviceType", stdPAPPolicy.getServiceType());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringBooleanStringStringStringStringStringIntegerStringStringStringString()
            throws URISyntaxException {
        // test for Updating Config Firewall Policies from the Admin Console
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").policyID("policyId").ruleID("ruleId").version("version").jsonBody("jasonBody")
                .highestVersion(1).riskLevel("riskLevel").riskType("riskType").guard("guard").ttlDate("ttlDate")
                .build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("policyId", stdPAPPolicy.getPolicyID());
        assertEquals("ruleId", stdPAPPolicy.getRuleID());
        assertEquals("version", stdPAPPolicy.getVersion());
        assertEquals("jasonBody", stdPAPPolicy.getJsonBody());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringStringStringStringStringStringStringStringBooleanStringintStringStringStringString()
            throws URISyntaxException {
        // for Micro Service Creating/Updating Policies from the Admin Console
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").onapName("onapName").configName("configName")
                .serviceType("serviceType").uuid("uuid").msLocation("msLocation").jsonBody("jasonBody")
                .priority("priority").version("version").editPolicy(true).domain("domain").highestVersion(1)
                .riskLevel("riskLevel").riskType("riskType").guard("guard").ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("onapName", stdPAPPolicy.getOnapName());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertEquals("serviceType", stdPAPPolicy.getServiceType());
        assertEquals("uuid", stdPAPPolicy.getUuid());
        assertEquals("msLocation", stdPAPPolicy.getMsLocation());
        assertEquals("jasonBody", stdPAPPolicy.getJsonBody());
        assertEquals("priority", stdPAPPolicy.getPriority());
        assertEquals("version", stdPAPPolicy.getVersion());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringBooleanStringStringStringStringStringIntegerStringStringStringStringString()
            throws URISyntaxException {
        // test for Updating Goc Policies from the Admin Console
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").policyID("policyId").ruleID("ruleId").version("version").jsonBody("jasonBody")
                .highestVersion(1).onapName("ecompName").riskLevel("riskLevel").riskType("riskType").guard("guard")
                .ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("policyId", stdPAPPolicy.getPolicyID());
        assertEquals("ruleId", stdPAPPolicy.getRuleID());
        assertEquals("version", stdPAPPolicy.getVersion());
        assertEquals("jasonBody", stdPAPPolicy.getJsonBody());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("ecompName", stdPAPPolicy.getOnapName());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringBooleanStringStringStringStringMapStringStringIntegerStringStringStringStringString()
            throws URISyntaxException {
        // for Updating Brms Policies from the Admin Console
        Map<String, String> dyanamicFieldConfigAttributes = new HashMap<>();
        dyanamicFieldConfigAttributes.put("aKey", "aValue");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").policyID("policyId").ruleID("ruleId").version("version")
                .dynamicFieldConfigAttributes(dyanamicFieldConfigAttributes).highestVersion(1).onapName("eCompName")
                .configBodyData("configBodyData").riskLevel("riskLevel").riskType("riskType").guard("guard")
                .ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("policyId", stdPAPPolicy.getPolicyID());
        assertEquals("ruleId", stdPAPPolicy.getRuleID());
        assertEquals("version", stdPAPPolicy.getVersion());
        assertEquals(dyanamicFieldConfigAttributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("eCompName", stdPAPPolicy.getOnapName());
        assertEquals("configBodyData", stdPAPPolicy.getConfigBodyData());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringStringStringStringBooleanStringStringStringStringMapStringStringIntegerStringMapStringStringStringStringStringString()
            throws URISyntaxException {
        // for Updating Brms Param Policies from the Admin Console
        Map<String, String> drlRuleAndUIParams = new HashMap<>();
        drlRuleAndUIParams.put("aDrlRuleKey", "aDrlRuleValue");
        Map<String, String> dynamicFieldConfigAttributes = new HashMap<>();
        dynamicFieldConfigAttributes.put("foo", "bar");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().configPolicyType("configTypePolicy")
                .policyName("policyName").description("description").configName("configName").editPolicy(true)
                .domain("domain").policyID("policyId").ruleID("ruleId").version("version")
                .dynamicFieldConfigAttributes(dynamicFieldConfigAttributes).highestVersion(1).onapName("eCompName")
                .drlRuleAndUIParams(drlRuleAndUIParams).riskLevel("riskLevel").riskType("riskType").guard("guard")
                .priority("high").ttlDate("ttlDate").build());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("description", stdPAPPolicy.getPolicyDescription());
        assertEquals("configName", stdPAPPolicy.getConfigName());
        assertTrue(stdPAPPolicy.isEditPolicy());
        assertEquals("domain", stdPAPPolicy.getDomainDir());
        assertEquals("policyId", stdPAPPolicy.getPolicyID());
        assertEquals("ruleId", stdPAPPolicy.getRuleID());
        assertEquals("version", stdPAPPolicy.getVersion());
        assertEquals(dynamicFieldConfigAttributes, stdPAPPolicy.getDynamicFieldConfigAttributes());
        assertEquals(Integer.valueOf(1), stdPAPPolicy.getHighestVersion());
        assertEquals("eCompName", stdPAPPolicy.getOnapName());
        assertEquals(drlRuleAndUIParams, stdPAPPolicy.getDrlRuleAndUIParams());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("high", stdPAPPolicy.getPriority());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringString() {
        // for deleting policies from the API
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(
                StdPAPPolicyParams.builder().policyName("policyName").deleteCondition("deleteCondition").build());
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("deleteCondition", stdPAPPolicy.getDeleteCondition());
    }

    @Test
    public void testConstructorStringStringString() {
        // for creating dictionary items from the API>
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder().dictionaryType("dictionaryType")
                .dictionary("dictionary").dictionaryFields("dictionaryFields").build());
        assertEquals("dictionaryType", stdPAPPolicy.getDictionaryType());
        assertEquals("dictionary", stdPAPPolicy.getDictionary());
        assertEquals("dictionaryFields", stdPAPPolicy.getDictionaryFields());
    }
}
