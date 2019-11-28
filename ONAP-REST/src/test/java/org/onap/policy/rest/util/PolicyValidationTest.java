/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.rest.adapter.ClosedLoopFaultTrapDatas;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

public class PolicyValidationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void microServicePolicyTests() throws Exception {
        PolicyParameters policyParameters = new PolicyParameters();

        policyParameters.setPolicyConfigType(PolicyConfigType.MicroService);
        policyParameters.setPolicyName("Test.junitPolicy");
        policyParameters.setPolicyDescription("This is a sample Micro Service policy Create example");
        policyParameters.setOnapName("DCAE");
        policyParameters.setPriority("1");
        String msJsonString = TextFileUtils.getTextFileAsString("src/test/resources/policies/MicroServicePolicy.json");
        policyParameters.setConfigBody(msJsonString);
        policyParameters.setRequestID(UUID.randomUUID());
        SimpleDateFormat dateformat3 = new SimpleDateFormat("dd/MM/yyyy");
        Date date = dateformat3.parse("15/10/2016");
        policyParameters.setTtlDate(date);
        policyParameters.setGuard(true);
        policyParameters.setRiskLevel("5");
        policyParameters.setRiskType("TEST");
        policyParameters.setRequestID(UUID.randomUUID());

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
        PolicyRestAdapter policyData = wrapper.populateRequestParameters(policyParameters);
        PolicyValidation validation = new PolicyValidation();
        String responseString = validation.validatePolicy(policyData).toString();

        assertNotSame("success", responseString);

        new PolicyValidation(null);
        assertNull(PolicyValidation.getCommonClassDao());

        policyData.setConfigPolicyType("ClosedLoop_Fault");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).doesNotContain("success");

        policyData.setConfigPolicyType("ClosedLoop_PM");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).doesNotContain("success");

        policyData.setConfigPolicyType("Enforcer Config");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).doesNotContain("success");

        policyData.setConfigPolicyType("Optimization");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).doesNotContain("success");

        policyData.setConfigPolicyType("Strange");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).doesNotContain("success");
    }

    @Test
    public final void testEmailValidation() {
        PolicyValidation validation = new PolicyValidation();
        String result = validation.emailValidation("testemail@test.com", "SUCCESS");
        assertEquals("success", result);
    }

    @Test
    public void testPolicyHeadingValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();

        String responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("PolicyName Should not be empty");

        policyData.setPolicyName("%%%~~~%%%");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}");

        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("@CreatedBy:");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString)
                        .contains("The value in the description shouldn't contain @CreatedBy: or @ModifiedBy:");
        policyData.setPolicyDescription("@CreatedBy:");

        policyData.setPolicyDescription("A Legal Description");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString)
                        .doesNotContain("The value in the description shouldn't contain @CreatedBy: or @ModifiedBy:");
    }

    @Test
    public void testPolicyAttributeValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("NOTAPI");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        List<Object> attributes = new ArrayList<>();
        policyData.setAttributes(attributes);
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        attributes.add(new String("hello"));
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);
        attributes.clear();

        Map<String, String> mapAttribute = new LinkedHashMap<>();
        attributes.add(mapAttribute);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Component Attributes</b>:<i> has one missing Component Attribute value");

        mapAttribute.put("key", "value");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("<b>Component Attributes</b>:<i> has one missing Component Attribute value</i><br>",
                        responseString);

        mapAttribute.put("key", "");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("<b>Attributes or Component Attributes</b>:<i>null : value has spaces or invalid characters</i>"
                        + "<br><b>Component Attributes</b>:<i> has one missing Component Attribute value</i><br>",
                        responseString);
        mapAttribute.clear();

        responseString = validation.validatePolicy(policyData).toString();
        mapAttribute.put("hello", "aaa");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("<b>Component Attributes</b>:<i> has one missing Component Attribute key</i><br>"
                        + "<b>Component Attributes</b>:<i> has one missing Component Attribute value</i><br>",
                        responseString);

        policyData.setPolicyType("Config");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("<b>RiskType</b>: Risk Type Should not be Empty</i><br>"
                        + "<b>RiskLevel</b>: Risk Level Should not be Empty</i><br>"
                        + "<b>Guard</b>: Guard Value Should not be Empty</i><br>", responseString);

        policyData.setConfigPolicyType("Base");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("has one missing Attribute key");

        policyData.setConfigPolicyType("BRMS_Param");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("has one missing Attribute key");

        policyData.setConfigPolicyType("BRMS_Raw");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("has one missing Attribute key");

        policyData.setConfigPolicyType(null);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Risk Level Should not be Empty");

        mapAttribute.clear();
        mapAttribute.put("value", "thevalue");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Risk Level Should not be Empty");

        mapAttribute.put("value", "$$$%%%%");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Risk Level Should not be Empty");

        policyData.setConfigPolicyType("Base");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("value has spaces or invalid characters");

        policyData.setConfigPolicyType("BRMS_Param");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("value has spaces or invalid characters");

        policyData.setConfigPolicyType("BRMS_Raw");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("value has spaces or invalid characters");

        policyData.setConfigPolicyType(null);
        policyData.setPolicyType(null);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("value has spaces or invalid characters");
    }

    @Test
    public void testPolicySettingsValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("NOTAPI");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        List<Object> settings = new ArrayList<>();
        policyData.setSettings(settings);
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        settings.add("hello");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);
        settings.clear();

        Map<String, String> mapSetting = new LinkedHashMap<>();
        settings.add(mapSetting);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Settings Attributes</b>:<i> has one missing Attribute key");

        mapSetting.put("key", "value");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("<b>Settings Attributes</b>:<i> has one missing Attribute Value</i><br>", responseString);

        mapSetting.put("key", "");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("<b>Settings Attributes</b>:<i> has one missing Attribute Value</i><br>", responseString);
        mapSetting.clear();

        mapSetting.put("value", "thevalue");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("has one missing Attribute key");

        mapSetting.put("value", "$$$%%%");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("value has spaces or invalid characters");
    }

    @Test
    public void testPolicyRuleAlgorithmsValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("NOTAPI");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        List<Object> ruleAlgorithmschoices = new ArrayList<>();
        policyData.setRuleAlgorithmschoices(ruleAlgorithmschoices);
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        ruleAlgorithmschoices.add("hello");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);
        ruleAlgorithmschoices.clear();

        Map<String, String> mapChoice = new LinkedHashMap<>();
        ruleAlgorithmschoices.add(mapChoice);
        assertNull(validation.validatePolicy(policyData));

        mapChoice.clear();
        mapChoice.put("id", "TheID");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Field 1 value is not selected");

        mapChoice.put("dynamicRuleAlgorithmField1", "Field1");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Field 2 value is not selected");

        mapChoice.put("dynamicRuleAlgorithmCombo", "Combo");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Field 3 value is empty");

        mapChoice.put("dynamicRuleAlgorithmField2", "Field2");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        mapChoice.put("dynamicRuleAlgorithmField2", "%%%$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Field 3 value has special characters");
    }

    @Test
    public void testPolicyConfigBaseValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setPolicyType("Config");
        policyData.setConfigPolicyType("Base");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Onap Name Should not be empty");

        policyData.setOnapName("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("<b>OnapName</b>:<i>The Value in Required Field");

        policyData.setOnapName("AValidOnapName");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Risk Level Should not be Empty");

        policyData.setRiskType("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("<b>RiskType</b>:<i>The Value in Required Field");

        policyData.setRiskType("AValidRiskType");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Risk Level Should not be Empty");

        policyData.setRiskLevel("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("<b>RiskLevel</b>:<i>The Value in Required Field");

        policyData.setRiskLevel("AValidRiskLevel");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Guard Value Should not be Empty");

        policyData.setGuard("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("<b>Guard</b>:<i>The Value in Required Field");

        policyData.setGuard("AValidGuard");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Name Should not be Empty");

        policyData.setConfigName("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ConfigName:The Value in Required Field");

        policyData.setConfigName("AValidConfigName");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Type Should not be Empty");

        policyData.setConfigType("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ConfigType:The Value in Required Field");

        policyData.setConfigType("AValidConfigType");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Body Should not be Empty");

        policyData.setConfigBodyData("");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Body Should not be Empty");

        policyData.setConfigBodyData("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setConfigType(null);
        policyData.setConfigBodyData("ValidConfigBodyData");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Type Should not be Empty");

        policyData.setConfigType("JSON");
        policyData.setConfigBodyData("{");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Body: JSON Content is not valid");

        policyData.setConfigBodyData("ValidConfigBodyData");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setConfigType("XML");
        policyData.setConfigBodyData("{");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Body: XML Content data is not valid");

        policyData.setConfigBodyData("<tag>value</tag>");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setConfigType("PROPERTIES");
        policyData.setConfigBodyData("{");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Body: Property data is not valid");

        policyData.setConfigBodyData("propertyName=PropertyValue");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);
    }

    @Test
    public void testPolicyConfigFirewallValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        // Invalid values tested in config base test
        policyData.setOnapName("AValidOnapName");
        policyData.setRiskType("AValidRiskType");
        policyData.setRiskLevel("AValidRiskLevel");
        policyData.setGuard("AValidGuard");
        assertEquals("success", responseString);

        policyData.setPolicyType("Config");
        policyData.setConfigPolicyType("Firewall Config");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Name is required");

        policyData.setConfigName("");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Config Name is required");

        policyData.setConfigName("%%%$$$$");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("<b>ConfigName</b>:<i>The Value in Required Field");

        policyData.setConfigName("AValidConfigName");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Security Zone is required");

        policyData.setSecurityZone("");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Security Zone is required");

        policyData.setSecurityZone("AValidSeurityZone");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);
    }

    @Test
    public void testPolicyConfigBRMSValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        // Invalid values tested in config base test
        policyData.setOnapName("AValidOnapName");
        policyData.setRiskType("AValidRiskType");
        policyData.setRiskLevel("AValidRiskLevel");
        policyData.setGuard("AValidGuard");
        assertEquals("success", responseString);

        policyData.setPolicyType("Config");
        policyData.setConfigPolicyType("BRMS_Param");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("BRMS Template is required");

        policyData.setRuleName("");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("BRMS Template is required");

        policyData.setRuleName("AValidRuleName");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);
        policyData.setRuleName(null);

        policyData.setConfigPolicyType("BRMS_Raw");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Raw Rule is required");

        policyData.setConfigBodyData("");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Raw Rule is required");

        policyData.setConfigBodyData("InvalidConfigBodyData");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("expecting one of the following tokens");

        policyData.setConfigBodyData("import org.onap.policy.test.DummyTestSomething;");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);
    }

    @Test
    public void testPolicyConfigCloseLoopPmValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        // Invalid values tested in config base test
        policyData.setOnapName("AValidOnapName");
        policyData.setRiskType("AValidRiskType");
        policyData.setRiskLevel("AValidRiskLevel");
        policyData.setGuard("AValidGuard");
        assertEquals("success", responseString);

        policyData.setPolicyType("Config");
        policyData.setConfigPolicyType("ClosedLoop_PM");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ServiceType PolicyName is required");

        Map<String, String> serviceTypePolicyName = null;
        policyData.setServiceTypePolicyName(serviceTypePolicyName);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ServiceType PolicyName is required");

        serviceTypePolicyName = new LinkedHashMap<>();
        policyData.setServiceTypePolicyName(serviceTypePolicyName);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ServiceType PolicyName is required");

        serviceTypePolicyName.put("AKey", "AValue");
        policyData.setServiceTypePolicyName(serviceTypePolicyName);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ServiceType PolicyName is required");
        serviceTypePolicyName.clear();

        serviceTypePolicyName.put("serviceTypePolicyName", "");
        policyData.setServiceTypePolicyName(serviceTypePolicyName);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("ServiceType PolicyName is required");

        serviceTypePolicyName.put("serviceTypePolicyName", "AValidserviceTypePolicyName");
        policyData.setServiceTypePolicyName(serviceTypePolicyName);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("");
        assertNull(validation.validatePolicy(policyData));

        policyData.setJsonBody("InvalidConfigBodyData");
        assertNull(validation.validatePolicy(policyData));

        policyData.setJsonBody("{}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"gamma\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"gamma\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"mcr\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"mcr\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"trinity\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"vDNS\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"vDNS\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"vUSP\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"vUSP\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": \"\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Email Address is not Valid");

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": \"%%%$$$\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Email Address is not Valid");

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": \"dorothy@emealdcity.oz\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"geoLink\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"geoLink\": \"\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"geoLink\": \"$$$%%%\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("The Value in Required Field");

        policyData.setJsonBody("{\"trinity\": true, \"geoLink\": \"AValidGeoLink\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"attributes\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"attributes\": {}}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"attributes\": {\"an0\":\"av0\"}}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);

        policyData.setJsonBody("{\"trinity\": true, \"attributes\": {\"an0\":\"$$$%%%\"}}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("value has spaces or invalid characters");

        policyData.setJsonBody("{\"trinity\": true, \"attributes\": {\"Message\":\"$$$%%%\"}}");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);
    }

    @Test
    public void testPolicyConfigCloseLoopFaultValidation() throws IOException {
        PolicyValidation validation = new PolicyValidation();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyName("ALegalPolicyName");
        policyData.setPolicyDescription("A Valid Description");

        String responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        policyData.setApiflag("API");
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success", responseString);

        // Invalid values tested in config base test
        policyData.setOnapName("AValidOnapName");
        policyData.setRiskType("AValidRiskType");
        policyData.setRiskLevel("AValidRiskLevel");
        policyData.setGuard("AValidGuard");
        assertEquals("success", responseString);

        policyData.setPolicyType("Config");
        policyData.setConfigPolicyType("ClosedLoop_Fault");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("");
        assertNull(validation.validatePolicy(policyData));

        policyData.setJsonBody("InvalidConfigBodyData");
        assertNull(validation.validatePolicy(policyData));

        policyData.setJsonBody("{}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("There were no conditions provided in configBody json");

        policyData.setJsonBody("{\"conditions\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("There were no conditions provided in configBody json");

        policyData.setJsonBody("{\"conditions\": \"\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select At least one Condition");

        policyData.setJsonBody("{\"conditions\": \"AValidCondition\"}");
        assertNull(validation.validatePolicy(policyData));

        policyData.setApiflag("NOAPI");

        policyData.setJsonBody("");
        assertNull(validation.validatePolicy(policyData));

        policyData.setJsonBody("InvalidConfigBodyData");
        assertNull(validation.validatePolicy(policyData));

        ClosedLoopFaultTrapDatas trapDatas = new ClosedLoopFaultTrapDatas();
        policyData.setTrapDatas(trapDatas);

        ClosedLoopFaultTrapDatas faultDatas = new ClosedLoopFaultTrapDatas();
        policyData.setFaultDatas(faultDatas);

        policyData.setJsonBody("{}");
        assertThat(responseString).contains("Select At least one Condition");

        List<Object> trap1 = new ArrayList<>();
        trapDatas.setTrap1(trap1);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Trigger Clear TimeOut is required");

        policyData.setClearTimeOut("AValidClearTimeout");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Trap Max Age is required");

        policyData.setTrapMaxAge("AValidTrapMaxAge");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        trapDatas.setTrap1(null);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        faultDatas.setTrap1(null);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        faultDatas.setTrap1(trap1);
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Fault Clear TimeOut is required when");

        policyData.setVerificationclearTimeOut("AValidVerificationClearTimeout");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"gamma\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"gamma\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"mcr\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"mcr\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"trinity\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"vDNS\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"vDNS\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"vUSP\": false}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select at least one D2");

        policyData.setJsonBody("{\"vUSP\": true}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": \"\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": \"%%%$$$\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Email Address is not Valid");

        policyData.setJsonBody("{\"trinity\": true, \"emailAddress\": \"dorothy@emealdcity.oz\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": true, \"actions\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": true, \"actions\": \"\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("vPRO Actions is required");

        policyData.setJsonBody("{\"trinity\": true, \"actions\": \"$$$%%%\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Policy Status is required");
        assertThat(responseString).contains("Vnf Type is required");

        policyData.setJsonBody("{\"trinity\": true, \"actions\": \"ValidActions\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Policy Status is required");

        policyData.setJsonBody("{\"trinity\": true, \"actions\": \"ValidActions\", \"closedLoopPolicyStatus\": null}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Policy Status is required");

        policyData.setJsonBody("{\"trinity\": true, \"actions\": \"ValidActions\", \"closedLoopPolicyStatus\": \"\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Policy Status is required");

        policyData.setJsonBody(
                        "{\"trinity\": true, \"actions\": \"ValidActions\", \"closedLoopPolicyStatus\": \"$$$%%%\"}");
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select At least one Condition");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select At least one Condition");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": null"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Select At least one Condition");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Aging Window is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"geoLink\": null"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Aging Window is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"geoLink\": \"\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Aging Window is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"geoLink\": \"%%%$$$\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("GeoLink</b>:<i>The Value in Required Field");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"geoLink\": \"AValidGeoLink\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Aging Window is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Time Interval is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Number of Retries is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1,"
                        + "\"retrys\": -1"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("APP-C Timeout is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1,"
                        + "\"retrys\": -1,"
                        + "\"timeOutvPRO\": -1"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("TimeOutRuby is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1,"
                        + "\"retrys\": -1,"
                        + "\"timeOutvPRO\": -1,"
                        + "\"timeOutRuby\": -1"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Vnf Type is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1,"
                        + "\"retrys\": -1,"
                        + "\"timeOutvPRO\": -1,"
                        + "\"timeOutRuby\": -1,"
                        + "\"vnfType\": null"
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Vnf Type is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1,"
                        + "\"retrys\": -1,"
                        + "\"timeOutvPRO\": -1,"
                        + "\"timeOutRuby\": -1,"
                        + "\"vnfType\": \"\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertThat(responseString).contains("Vnf Type is required");

        // @formatter:off
        policyData.setJsonBody("{"
                        + "\"trinity\": true,"
                        + "\"actions\": \"ValidActions\","
                        + "\"closedLoopPolicyStatus\": \"AValidStatus\","
                        + "\"conditions\": \"SEND\","
                        + "\"agingWindow\": -1,"
                        + "\"timeInterval\": -1,"
                        + "\"retrys\": -1,"
                        + "\"timeOutvPRO\": -1,"
                        + "\"timeOutRuby\": -1,"
                        + "\"vnfType\": \"AValid VNF Type\""
                        + "}");
        // @formatter:on
        responseString = validation.validatePolicy(policyData).toString();
        assertEquals("success@#", responseString);
    }
}
