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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.test.XACMLPAPTest;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

import com.att.research.xacml.util.XACMLProperties;


public class FirewallConfigPolicyTest {

    private static Logger logger = FlexLogger.getLogger(FirewallConfigPolicyTest.class);
    PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
    Map<String, String> attributeMap = new HashMap<>();
    FirewallConfigPolicy component = null;
    FirewallConfigPolicy mockFWConfig = null;
    private XACMLPAPTest papTest;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setup: enter");
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME,"src/test/resources/xacml.pap.properties");

        policyAdapter.setPolicyName("FWjunitTest");
        policyAdapter.setPolicyDescription("test");
        policyAdapter.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
        policyAdapter.setPolicyType("Config");
        policyAdapter.setConfigPolicyType("Firewall Config");
        policyAdapter.setEditPolicy(false);
        policyAdapter.setDomainDir("Test");
        policyAdapter.setNewFileName("Test.Config_FW_junitTest.1.xml");
        policyAdapter.setHighestVersion(1);
        policyAdapter.setVersion(String.valueOf(1));
        policyAdapter.setPolicyID("urn:xacml:policy:id:"+UUID.randomUUID());
        policyAdapter.setRuleID("");
        policyAdapter.setConfigName("testname");
        policyAdapter.setGuard("True");
        policyAdapter.setRiskLevel("3");
        policyAdapter.setRiskType("RiskTest");
        policyAdapter.setSecurityZone("CraigSecurityZone");
        policyAdapter.setUserId("API");

        attributeMap.put("testJunits", "test");
        policyAdapter.setDynamicFieldConfigAttributes(attributeMap);

        component = new FirewallConfigPolicy(policyAdapter);
        mockFWConfig = Mockito.mock(FirewallConfigPolicy.class);
        papTest = new XACMLPAPTest();
        papTest.setDBDao();
        logger.info("setUp: exit");

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.FirewallConfigPolicy#savePolicies()}.
     */
    @Test
    public void testSavePolicies() {
        Map<String, String> successMap = new HashMap<>();
        successMap.put("success", "success");
        try {
            when(mockFWConfig.savePolicies()).thenReturn(successMap);
            successMap = mockFWConfig.savePolicies();
        } catch (Exception e) {
            logger.error("Exception Occured"+e);
        }
    }

    /**
     * Test method for {@link org.openecomp.policy.pap.xacml.rest.components.FirewallConfigPolicy#prepareToSave()}.
     */
    @Test
    public void testPrepareToSave() {
        logger.debug("test prepareToSave Policy: enter");

        boolean response = false;
        try {
            when(mockFWConfig.prepareToSave()).thenReturn(true);
            response = mockFWConfig.prepareToSave();
        } catch (Exception e) {
            logger.error("Exception Occured"+e);
        }
        assertTrue(response);

    }

    @Test
    public void testUpdateJson() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        FirewallConfigPolicy firewallConfigPolicy = new FirewallConfigPolicy();
        Method method = firewallConfigPolicy.getClass().getDeclaredMethod("updateFirewallDictionaryData", String.class , String.class);
        method.setAccessible(true);
        String jsonBody= "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"TestFwPolicyConfig\",\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"cloudsite:dev1a\",\"serviceGroups\":[{\"name\":\"SSH\",\"description\":\"Sshservice entry in servicelist\",\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"22\"}],\"addressGroups\":[{\"name\":\"test\",\"description\":\"Destination\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/12\"}]},{\"name\":\"TestServers\",\"description\":\"SourceTestServers for firsttesting\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/23\"}]}],\"firewallRuleList\":[{\"position\":\"1\",\"ruleName\":\"FWRuleTestServerToTest\",\"fromZones\":[\"UntrustedZoneTestName\"],\"toZones\":[\"TrustedZoneTestName\"],\"negateSource\":false,\"negateDestination\":false,\"sourceList\":[{\"type\":\"REFERENCE\",\"name\":\"TestServers\"}],\"destinationList\":[{\"type\":\"REFERENCE\",\"name\":\"Test\"}],\"sourceServices\":[],\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"SSH\"}],\"action\":\"accept\",\"description\":\"FWrule for Test source to Test destination\",\"enabled\":true,\"log\":true}]}";
        String prevJsonBody = "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"TestFwPolicy1Config\",\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"cloudsite:dev\",\"vendorServiceId\":\"test\",\"vendorSpecificData\":{\"idMap\":[{\"Id\":\"cloudsite:dev1a\",\"vendorId\":\"deviceGroup:dev\"}]},\"serviceGroups\":[{\"name\":\"SSH\",\"description\":\"Ssh service entry in service list\",\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"22\"}],\"addressGroups\":[{\"name\":\"Test\",\"description\":\"Destination Test\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/12\"}]},{\"name\":\"TestServers\",\"description\":\"Source TestServers for first testing\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/23\"}]}],\"firewallRuleList\":[{\"position\":\"1\",\"ruleName\":\"FWRuleTestServerTot\",\"fromZones\":[\"UntrustedZoneTestName\"],\"toZones\":[\"TrustedZoneTName\"],\"negateSource\":false,\"negateDestination\":false,\"sourceList\":[{\"type\":\"REFERENCE\",\"name\":\"TServers\"}],\"destinationList\":[{\"type\":\"REFERENCE\",\"name\":\"Test\"}],\"sourceServices\":[],\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"SSH\"}],\"action\":\"accept\",\"description\":\"FW rule for HOHO source to CiscoVCE destination\",\"enabled\":true,\"log\":true}]}";
        assertTrue((Boolean) method.invoke(firewallConfigPolicy, jsonBody, prevJsonBody));
    }

    @Test
    public void testInsertJson() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        FirewallConfigPolicy firewallConfigPolicy = new FirewallConfigPolicy();
        Method method = firewallConfigPolicy.getClass().getDeclaredMethod("insertFirewallDicionaryData", String.class);
        method.setAccessible(true);
        String jsonBody= "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"TestFwPolicyConfig\",\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"cloudsite:dev1a\",\"serviceGroups\":[{\"name\":\"SSH\",\"description\":\"Sshservice entry in servicelist\",\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"22\"}],\"addressGroups\":[{\"name\":\"test\",\"description\":\"Destination\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/12\"}]},{\"name\":\"TestServers\",\"description\":\"SourceTestServers for firsttesting\",\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/23\"}]}],\"firewallRuleList\":[{\"position\":\"1\",\"ruleName\":\"FWRuleTestServerToTest\",\"fromZones\":[\"UntrustedZoneTestName\"],\"toZones\":[\"TrustedZoneTestName\"],\"negateSource\":false,\"negateDestination\":false,\"sourceList\":[{\"type\":\"REFERENCE\",\"name\":\"TestServers\"}],\"destinationList\":[{\"type\":\"REFERENCE\",\"name\":\"Test\"}],\"sourceServices\":[],\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"SSH\"}],\"action\":\"accept\",\"description\":\"FWrule for Test source to Test destination\",\"enabled\":true,\"log\":true}]}";
        assertTrue((Boolean) method.invoke(firewallConfigPolicy, jsonBody));
    }

}