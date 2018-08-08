/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
package org.onap.policy.xacml.test.std.pap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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

        Paths.get("src/test/resources/pdps");
        try {
            stdPAPPolicy = new StdPAPPolicy();
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetActionAttribute() {
        try {
            stdPAPPolicy.setActionAttribute("test");
            assertTrue(stdPAPPolicy.getActionAttribute() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetActionBody() {
        try {
            stdPAPPolicy.setActionBody("actionBody");
            assertTrue(stdPAPPolicy.getActionBody() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetActionDictHeader() {
        try {
            stdPAPPolicy.setActionDictHeader("actionDictHeader");
            assertTrue(stdPAPPolicy.getActionDictHeader() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetActionDictMethod() {
        try {
            stdPAPPolicy.setActionDictMethod("actionDictMethod");
            assertTrue(stdPAPPolicy.getActionDictMethod() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetActionDictType() {
        try {
            stdPAPPolicy.setActionDictType("actionDictType");
            assertTrue(stdPAPPolicy.getActionDictType() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetActionDictUrl() {
        try {
            stdPAPPolicy.setActionDictUrl("actionDictUrl");
            assertTrue(stdPAPPolicy.getActionDictUrl() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetActionPerformer() {
        try {
            stdPAPPolicy.setActionPerformer("actionPerformer");
            assertTrue(stdPAPPolicy.getActionPerformer() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetBrmsController() {
        try {
            stdPAPPolicy.setBrmsController("brmsController");
            assertTrue(stdPAPPolicy.getBrmsController() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetBrmsDependency() {
        try {
            stdPAPPolicy.setBrmsDependency(new ArrayList<>());
            assertTrue(stdPAPPolicy.getBrmsDependency() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetConfigBodyData() {
        try {
            stdPAPPolicy.setConfigBodyData("configBodyData");
            assertTrue(stdPAPPolicy.getConfigBodyData() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }


    @Test
    public void testGetConfigName() {
        try {
            stdPAPPolicy.setConfigName("configName");
            assertTrue(stdPAPPolicy.getConfigName() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetConfigPolicyType() {
        try {
            stdPAPPolicy.setConfigPolicyType("configPolicyType");
            assertTrue(stdPAPPolicy.getConfigPolicyType() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetConfigType() {
        try {
            stdPAPPolicy.setConfigType("configType");
            assertTrue(stdPAPPolicy.getConfigType() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDataTypeList() {
        try {
            stdPAPPolicy.setDataTypeList(new ArrayList<String>());
            assertTrue(stdPAPPolicy.getDataTypeList() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDeleteCondition() {
        try {
            stdPAPPolicy.setDeleteCondition("deleteCondition");
            assertTrue(stdPAPPolicy.getDeleteCondition() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }


    @Test
    public void testGetDrlRuleAndUIParams() {
        try {
            stdPAPPolicy.setDrlRuleAndUIParams(new HashMap<>());
            assertTrue(stdPAPPolicy.getDrlRuleAndUIParams() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDropDownMap() {
        try {
            stdPAPPolicy.setDropDownMap(new HashMap<>());
            assertTrue(stdPAPPolicy.getDropDownMap() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetDynamicFieldConfigAttributes() {
        try {
            assertTrue(stdPAPPolicy.getDynamicFieldConfigAttributes() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDynamicRuleAlgorithmCombo() {
        try {
            stdPAPPolicy.setDynamicRuleAlgorithmCombo(new ArrayList<>());
            assertTrue(stdPAPPolicy.getDynamicRuleAlgorithmCombo() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDynamicRuleAlgorithmField1() {
        try {
            stdPAPPolicy.setDynamicRuleAlgorithmField1(new ArrayList<>());
            assertTrue(stdPAPPolicy.getDynamicRuleAlgorithmField1() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDictionary() {
        try {
            stdPAPPolicy.setDictionary("dictionary");
            assertTrue(stdPAPPolicy.getDictionary() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDictionaryFields() {
        try {
            stdPAPPolicy.setDictionaryFields("dictionaryFields");
            assertTrue(stdPAPPolicy.getDictionaryFields() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetDictionaryType() {
        try {
            stdPAPPolicy.setDictionaryType("dictionaryType");
            assertTrue(stdPAPPolicy.getDictionaryType() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDomainDir() {
        try {
            stdPAPPolicy.setDomainDir("domain");
            assertTrue(stdPAPPolicy.getDomainDir() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testIsDraft() {
        try {
            stdPAPPolicy.setDraft(true);
            assertTrue(stdPAPPolicy.isDraft() == true);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDynamicRuleAlgorithmLabels() {
        try {
            stdPAPPolicy.setDynamicRuleAlgorithmLabels(new ArrayList<>());
            assertTrue(stdPAPPolicy.getDynamicRuleAlgorithmLabels() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetDynamicSettingsMap() {
        try {
            stdPAPPolicy.setDynamicSettingsMap(new HashMap<>());
            assertTrue(stdPAPPolicy.getDynamicSettingsMap() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetDynamicVariableList() {
        try {
            stdPAPPolicy.setDynamicVariableList(new ArrayList<>());
            assertTrue(stdPAPPolicy.getDynamicVariableList() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetGuard() {
        try {
            stdPAPPolicy.setGuard("domain");
            assertTrue(stdPAPPolicy.getGuard() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetHighestVersion() {
        try {
            stdPAPPolicy.setHighestVersion(123);
            assertTrue(stdPAPPolicy.getHighestVersion() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testgGtJsonBody() {
        try {
            stdPAPPolicy.setJsonBody("jsonBoby");
            assertTrue(stdPAPPolicy.getJsonBody() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetLocation() {
        try {
            stdPAPPolicy.setLocation(new URI("test"));
            assertTrue(stdPAPPolicy.getLocation() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetMsLocation() {
        try {
            stdPAPPolicy.setMsLocation("MsLocation");
            assertTrue(stdPAPPolicy.getMsLocation() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testSetOldPolicyFileName() {
        try {
            stdPAPPolicy.setOldPolicyFileName("domain");
            assertTrue(stdPAPPolicy.getOldPolicyFileName() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetOnapName() {
        try {
            stdPAPPolicy.setOnapName("onap");
            assertTrue(stdPAPPolicy.getOnapName() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetPolicyDescription() {
        try {
            stdPAPPolicy.setPolicyDescription("description test");
            assertTrue(stdPAPPolicy.getPolicyDescription() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetPolicyID() {
        try {
            stdPAPPolicy.setPolicyID("test");
            assertTrue(stdPAPPolicy.getPolicyID() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testSetPolicyName() {
        try {
            stdPAPPolicy.setPolicyName("MsLocation");
            assertTrue(stdPAPPolicy.getPolicyName() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testSetPriority() {
        try {
            stdPAPPolicy.setPriority("domain");
            assertTrue(stdPAPPolicy.getPriority() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetProviderComboBox() {
        try {
            stdPAPPolicy.setProviderComboBox("onap");
            assertTrue(stdPAPPolicy.getProviderComboBox() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetRiskLevel() {
        try {
            stdPAPPolicy.setRiskLevel("test");
            assertTrue(stdPAPPolicy.getRiskLevel() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetRiskType() {
        try {
            stdPAPPolicy.setRiskType("test");
            assertTrue(stdPAPPolicy.getRiskType() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testGetRuleID() {
        try {
            stdPAPPolicy.setRuleID("MsLocation");
            assertTrue(stdPAPPolicy.getRuleID() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetServiceType() {
        try {
            stdPAPPolicy.setServiceType("domain");
            assertTrue(stdPAPPolicy.getServiceType() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetTTLDate() {
        try {
            stdPAPPolicy.setTTLDate("09/20/17");
            assertTrue(stdPAPPolicy.getTTLDate() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }


    @Test
    public void testGetUuid() {
        try {
            stdPAPPolicy.setUuid("11212122");
            assertTrue(stdPAPPolicy.getUuid() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testGetVersion() {
        try {
            stdPAPPolicy.setVersion("testv01");
            assertTrue(stdPAPPolicy.getVersion() != null);
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Test
    public void testIsEditPolicy() {
        try {
            stdPAPPolicy.setEditPolicy(true);
            assertTrue(stdPAPPolicy.isEditPolicy() == true);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testToString() {
        try {
            assertTrue(stdPAPPolicy.toString() != null);
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void testConstructorUri() throws URISyntaxException {
        URI location = new URI("testUri");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(location);
        assertEquals(location, stdPAPPolicy.getLocation());
    }

    @Test
    public void testConstructorStringStringStringString() throws URISyntaxException {
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("policyName", "body", "configType", "configTypePolicy");
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("body", stdPAPPolicy.getConfigBodyData());
        assertEquals("configType", stdPAPPolicy.getConfigType());
        assertEquals("configTypePolicy", stdPAPPolicy.getConfigPolicyType());
    }


    @Test
    public void testConstructorStringStringStringStringStringMapStringStringStringStringBooleanStringStringStringStringString()
            throws URISyntaxException {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("aKey", "aValue");

        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy(
                StdPAPPolicyParams.builder()
                        .configPolicyType("configTypePolicy")
                        .policyName("policyName")
                        .description("description")
                        .onapName("onapName")
                        .configName("configName")
                        .attributes(attributes)
                        .configType("configType")
                        .configBodyData("body")
                        .editPolicy(true)
                        .domain("domain")
                        .highestVersion(1)
                        .riskLevel("riskLevel")
                        .riskType("riskType")
                        .guard("guard")
                        .ttlDate("ttlDate").build());
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("policyName", "description", attributes,
                dynamicRuleAlgorithmLabels, dynamicRuleAlgorithmCombo, dynamicRuleAlgorithmField1,
                dynamicRuleAlgorithmField2,
                "actionPerformer", "actionAttribute", true, "domain", 1);
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("policyName", "description", "onapName", "providerComboBox",
                attributes, settings, treatments, dynamicRuleAlgorithmLabels, dynamicRuleAlgorithmCombo,
                dynamicRuleAlgorithmField1, dynamicRuleAlgorithmField2, dropDownMap, dynamicVariableList,
                dataTypeList, true, "domain", 1);
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "onapName",
                "configName", attributes,
                "body", "policyId", "ruleId", "configType", true, "version", "domain", 1, "riskLevel", "riskType",
                "guard", "ttlDate");
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true,
                "domain", "jasonBody", 1, "riskLevel", "riskType", "guard", "ttlDate");
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true,
                "domain", "jasonBody", 1, "eCompName", "riskLevel", "riskType", "guard", "ttlDate");
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true,
                "domain", dyanamicFieldConfigAttributes, 1, "eCompName", "configBodyData", "riskLevel", "riskType",
                "guard", "ttlDate", "brmsController", brmsDependency);
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true,
                "domain", dyanamicFieldConfigAttributes, 1, "eCompName", "configBodyData", drlRuleAndUIParams,
                "riskLevel", "riskType", "guard", "ttlDate", "brmsController", brmsDependency);
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "onapName",
                "jasonBody", true,
                "oldPolicyFileName", "serviceType", true, "domain", 1, "riskLevel", "riskType", "guard", "ttlDate");
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true, "domain", "policyId", "ruleId",
                "version", "jasonBody", 1, "riskLevel", "riskType", "guard", "ttlDate");
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "onapName",
                "configName", "serviceType",
                "uuid", "msLocation", "jasonBody", "priority", "version", true, "domain",
                1, "riskLevel", "riskType", "guard", "ttlDate");
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
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true, "domain", "policyId", "ruleId", "version",
                "jasonBody", 1, "ecompName", "riskLevel", "riskType", "guard", "ttlDate");
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
        Map<String, String> dyanamicFieldConfigAttributes = new HashMap<>();
        dyanamicFieldConfigAttributes.put("aKey", "aValue");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true,
                "domain", "policyId", "ruleId", "version", dyanamicFieldConfigAttributes, 1, "eCompName",
                "configBodyData", "riskLevel", "riskType", "guard", "ttlDate");
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
        Map<String, String> dyanamicFieldConfigAttributes = new HashMap<>();
        dyanamicFieldConfigAttributes.put("aKey", "aValue");
        Map<String, String> drlRuleAndUIParams = new HashMap<>();
        drlRuleAndUIParams.put("aDrlRuleKey", "aDrlRuleValue");
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("configTypePolicy", "policyName", "description", "configName",
                true,
                "domain", "policyId", "ruleId", "version", dyanamicFieldConfigAttributes, 1, "eCompName",
                drlRuleAndUIParams, "riskLevel", "riskType", "guard", "ttlDate");
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
        assertEquals(drlRuleAndUIParams, stdPAPPolicy.getDrlRuleAndUIParams());
        assertEquals("riskLevel", stdPAPPolicy.getRiskLevel());
        assertEquals("riskType", stdPAPPolicy.getRiskType());
        assertEquals("guard", stdPAPPolicy.getGuard());
        assertEquals("ttlDate", stdPAPPolicy.getTTLDate());
    }

    @Test
    public void testConstructorStringString() {
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("policyName", "deleteCondition");
        assertEquals("policyName", stdPAPPolicy.getPolicyName());
        assertEquals("deleteCondition", stdPAPPolicy.getDeleteCondition());
    }

    @Test
    public void testConstructorStringStringString() {
        StdPAPPolicy stdPAPPolicy = new StdPAPPolicy("dictionaryType", "dictionary", "dictionaryFields");
        assertEquals("dictionaryType", stdPAPPolicy.getDictionaryType());
        assertEquals("dictionary", stdPAPPolicy.getDictionary());
        assertEquals("dictionaryFields", stdPAPPolicy.getDictionaryFields());
    }
}
