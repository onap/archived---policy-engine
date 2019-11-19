/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2019 Nordix Foundation.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.rest.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;
import org.onap.policy.api.RuleProvider;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

@RunWith(MockitoJUnitRunner.class)
public class PolicyValidationRequestWrapperTest {

    @Mock
    HttpServletRequest request;

    @Test
    public void testHttpRequest() throws Exception {
        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        assertNull(wrapper.populateRequestParameters((HttpServletRequest) null));

        BufferedReader decisionPolicyReader = new BufferedReader(new StringReader(
                        TextFileUtils.getTextFileAsString("src/test/resources/policies/DecisionPolicy.json")));

        Mockito.when(request.getReader()).thenReturn(decisionPolicyReader);

        wrapper.populateRequestParameters(request);

        BufferedReader policyJsonTrapFaultReader = new BufferedReader(new StringReader(
                        TextFileUtils.getTextFileAsString("src/test/resources/policies/PolicyJsonTrapFault.json")));

        Mockito.when(request.getReader()).thenReturn(policyJsonTrapFaultReader);

        wrapper.populateRequestParameters(request);

        BufferedReader badJsonReader = new BufferedReader(new StringReader("{"));

        Mockito.when(request.getReader()).thenReturn(badJsonReader);

        wrapper.populateRequestParameters(request);
    }

    @Test
    public void testDefaultParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        parameters.setPolicyConfigType(PolicyConfigType.Firewall);
        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Firewall Config", adapter.getConfigPolicyType());

        parameters.setConfigBody("");
        assertNull(wrapper.populateRequestParameters((parameters)));

        parameters.setConfigBody("{\"name");
        assertNull(wrapper.populateRequestParameters((parameters)));

        parameters.setConfigBodyType(PolicyType.OTHER);
        parameters.setConfigBody(null);
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Firewall Config", adapter.getConfigPolicyType());

        parameters.setPolicyConfigType(PolicyConfigType.Extended);
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("EXTENDED", adapter.getConfigPolicyType());
    }

    @Test
    public void testConfigFirewallParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyConfigType(PolicyConfigType.Firewall);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Firewall Config", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"someParameter\": \"someValue\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals(null, adapter.getSecurityZone());
        assertEquals("ConfigName", adapter.getConfigName());

        parameters.setConfigBody("{\"securityZoneId\": \"SecurityZone\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("SecurityZone", adapter.getSecurityZone());

        parameters.setConfigBody("{\"configName\": \"AnotherConfigName\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("AnotherConfigName", adapter.getConfigName());
    }

    @Test
    public void testConfigMicroserviceParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyConfigType(PolicyConfigType.MicroService);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        assertNull(wrapper.populateRequestParameters((parameters)));

        parameters.setConfigBody("{\"someParameter\": \"someValue\"}");

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Micro Service", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"service\": \"MyService\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("MyService", adapter.getServiceType());

        parameters.setConfigBody("{\"content\": \"{}\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("\"{}\"", adapter.getPolicyJSON().toString());

        parameters.setConfigBody("{\"content\": {hess:}}");
        assertNull(wrapper.populateRequestParameters((parameters)));
    }

    @Test
    public void testConfigOptimizationParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyConfigType(PolicyConfigType.Optimization);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Optimization", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"someParameter\": \"someValue\"}");

        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Optimization", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"service\": \"MyService\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("MyService", adapter.getServiceType());
    }

    @Test
    public void testConfigClosedLoopFaultParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyConfigType(PolicyConfigType.ClosedLoop_Fault);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("ClosedLoop_Fault", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"someParameter\": \"someValue\"}");

        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("ClosedLoop_Fault", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"onapname\": \"MyOnapName\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("MyOnapName", adapter.getOnapName());
    }

    @Test
    public void testConfigClosedLoopPmParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyConfigType(PolicyConfigType.ClosedLoop_PM);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("ClosedLoop_PM", adapter.getConfigPolicyType());

        parameters.setConfigBody("{\"someParameter\": \"someValue\"}");

        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("ClosedLoop_PM", adapter.getConfigPolicyType());

        parameters.setConfigBody(
                        "{\"onapname\": \"MyOnapName\",\"serviceTypePolicyName\":\"Service Type Policy Name\"}");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("MyOnapName", adapter.getOnapName());
    }

    @Test
    public void testConfigBrmsParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        Map<AttributeType, Map<String, String>> attributes = new LinkedHashMap<>();
        parameters.setAttributes(attributes);
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        Map<String, String> templateMap = new LinkedHashMap<>();
        templateMap.put("templateName", "Template Name");
        attributes.put(AttributeType.RULE, templateMap);
        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("BRMS_Param", adapter.getConfigPolicyType());
        assertEquals("Template Name", adapter.getRuleName());

        parameters.setConfigBody("{\"someParameter\": \"someValue\"}");

        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("BRMS_Param", adapter.getConfigPolicyType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDecisionRainyDayParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyClass(PolicyClass.Decision);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        Map<AttributeType, Map<String, String>> attributes = new LinkedHashMap<>();
        parameters.setAttributes(attributes);
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        parameters.setRuleProvider(RuleProvider.RAINY_DAY);
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        Map<String, String> treatments = new LinkedHashMap<>();
        parameters.setTreatments(treatments);

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Rainy_Day", adapter.getRuleProvider());

        treatments.put("ATreatment", "A Treatment Value");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("ATreatment", ((Map<String, String>) adapter.getRainyday().getTreatmentTableChoices().get(0))
                        .get("errorcode"));
        assertEquals("A Treatment Value",
                        ((Map<String, String>) adapter.getRainyday().getTreatmentTableChoices().get(0))
                                        .get("treatment"));

        Map<String, String> matchingMap = new LinkedHashMap<>();
        matchingMap.put("ServiceType", "AServiceType");
        attributes.put(AttributeType.MATCHING, matchingMap);
        parameters.setAttributes(attributes);
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("AServiceType", adapter.getRainyday().getServiceType());
    }

    @Test
    public void testDecisionGuardParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyClass(PolicyClass.Decision);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        Map<AttributeType, Map<String, String>> attributes = new LinkedHashMap<>();
        parameters.setAttributes(attributes);
        assertThatThrownBy(() -> wrapper.populateRequestParameters(parameters))
                        .isInstanceOf(NullPointerException.class);

        parameters.setRuleProvider(RuleProvider.GUARD_BL_YAML);

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("GUARD_BL_YAML", adapter.getRuleProvider());

        parameters.setRuleProvider(RuleProvider.GUARD_MIN_MAX);

        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("GUARD_MIN_MAX", adapter.getRuleProvider());

        parameters.setRuleProvider(RuleProvider.GUARD_YAML);

        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("GUARD_YAML", adapter.getRuleProvider());

        Map<String, String> matchingMap = new LinkedHashMap<>();
        matchingMap.put("actor", "Actor");
        matchingMap.put("recipe", "Recipe");
        matchingMap.put("guardActiveStart", "GuardActiveStart");
        matchingMap.put("guardActiveEnd", "GuardActiveEnd");
        matchingMap.put("limit", "Limit");
        matchingMap.put("timeWindow", "Window");
        matchingMap.put("timeUnits", "Units");

        attributes.put(AttributeType.MATCHING, matchingMap);
        parameters.setAttributes(attributes);
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Actor", adapter.getYamlparams().getActor());
        assertEquals("Recipe", adapter.getYamlparams().getRecipe());
        assertEquals("GuardActiveStart", adapter.getYamlparams().getGuardActiveStart());
        assertEquals("GuardActiveEnd", adapter.getYamlparams().getGuardActiveEnd());
        assertEquals("Limit", adapter.getYamlparams().getLimit());
        assertEquals("Window", adapter.getYamlparams().getTimeWindow());
        assertEquals("Units", adapter.getYamlparams().getTimeUnits());

        parameters.setRuleProvider(RuleProvider.GUARD_MIN_MAX);
        matchingMap.put("min", "TheMin");
        matchingMap.put("max", "TheMax");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("TheMin", adapter.getYamlparams().getMin());
        assertEquals("TheMax", adapter.getYamlparams().getMax());

        parameters.setRuleProvider(RuleProvider.GUARD_BL_YAML);
        adapter = wrapper.populateRequestParameters((parameters));
        assertTrue(adapter.getYamlparams().getBlackList().isEmpty());

        matchingMap.put("blackList", "Bad0,Bad1");
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("Bad0", adapter.getYamlparams().getBlackList().get(0));
        assertEquals("Bad1", adapter.getYamlparams().getBlackList().get(1));

        parameters.setRuleProvider(RuleProvider.AAF);
        adapter = wrapper.populateRequestParameters((parameters));
        assertNull(adapter.getYamlparams());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testActionParameterHandling() throws Exception {
        PolicyParameters parameters = createParametersObject();
        parameters.setPolicyClass(PolicyClass.Action);

        PolicyValidationRequestWrapper wrapper = new PolicyValidationRequestWrapper();

        PolicyRestAdapter adapter = wrapper.populateRequestParameters((parameters));
        assertTrue(adapter.getRuleAlgorithmschoices().isEmpty());

        List<String> dynamicRuleAlgorithmLabels = new ArrayList<>();
        parameters.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
        adapter = wrapper.populateRequestParameters((parameters));
        assertTrue(adapter.getRuleAlgorithmschoices().isEmpty());

        dynamicRuleAlgorithmLabels.add("Label0");
        List<String> dynamicRuleAlgorithmFunctions = new ArrayList<>();
        dynamicRuleAlgorithmFunctions.add("FirstAlgorithmFunction");
        parameters.setDynamicRuleAlgorithmFunctions(dynamicRuleAlgorithmFunctions);
        List<String> dynamicRuleAlgorithmField1 = new ArrayList<>();
        dynamicRuleAlgorithmField1.add("FirstAlgorithmFunctionField1");
        parameters.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);
        List<String> dynamicRuleAlgorithmField2 = new ArrayList<>();
        dynamicRuleAlgorithmField2.add("FirstAlgorithmFunctionField2");
        parameters.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);
        parameters.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals(1, adapter.getRuleAlgorithmschoices().size());
        assertEquals("Label0", ((LinkedHashMap<String, String>) adapter.getRuleAlgorithmschoices().get(0)).get("id"));

        Map<String, String> matchingMap = new LinkedHashMap<>();
        matchingMap.put("AKey", "AValue");

        Map<AttributeType, Map<String, String>> attributes = new LinkedHashMap<>();
        attributes.put(AttributeType.MATCHING, matchingMap);
        parameters.setAttributes(attributes);
        adapter = wrapper.populateRequestParameters((parameters));
        assertEquals("AKey", ((LinkedHashMap<String, String>) adapter.getAttributes().get(0)).get("key"));
    }

    private PolicyParameters createParametersObject() {
        PolicyParameters parameters = new PolicyParameters();

        parameters.setPolicyName("PolicyName");
        parameters.setOnapName("ONAPName");
        parameters.setPriority("SomePriority");
        parameters.setConfigName("ConfigName");
        parameters.setRiskType("RiskType");
        parameters.setRiskLevel("RiskLevel");
        parameters.setGuard(false);
        parameters.setTtlDate(new Date());

        return parameters;
    }
}
