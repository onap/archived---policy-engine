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

package org.onap.policy.pap.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.ia.IntegrityAuditProperties;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.controller.ActionPolicyDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.ClosedLoopDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DecisionPolicyDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DescriptiveDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DictionaryController;
import org.onap.policy.pap.xacml.rest.controller.FirewallDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.MicroServiceDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.PolicyScopeDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.SafePolicyController;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.policycontroller.PolicyCreation;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.Category;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPAPPolicyParams;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import com.mockrunner.mock.web.MockServletInputStream;

public class XACMLPAPTest {
    private static final Log logger = LogFactory.getLog(XACMLPAPTest.class);

    private static final String ENVIRONMENT_HEADER = "Environment";
    private List<String> headers = new ArrayList<>();
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private ServletOutputStream mockOutput;
    private ServletConfig servletConfig;
    private XACMLPapServlet pap;
    private SessionFactory sessionFactory;
    private CommonClassDao commonClassDao;

    private static final String DEFAULT_DB_DRIVER = "org.h2.Driver";
    private static final String DEFAULT_DB_USER = "sa";
    private static final String DEFAULT_DB_PWD = "";

    @Before
    public void setUpDB() throws Exception {
        logger.info("setUpDB: Entering");

        Properties properties = new Properties();
        properties.put(IntegrityAuditProperties.DB_DRIVER, XACMLPAPTest.DEFAULT_DB_DRIVER);
        properties.put(IntegrityAuditProperties.DB_URL, "jdbc:h2:file:./sql/xacmlTest");
        properties.put(IntegrityAuditProperties.DB_USER, XACMLPAPTest.DEFAULT_DB_USER);
        properties.put(IntegrityAuditProperties.DB_PWD, XACMLPAPTest.DEFAULT_DB_PWD);
        properties.put(IntegrityAuditProperties.SITE_NAME, "SiteA");
        properties.put(IntegrityAuditProperties.NODE_TYPE, "pap");

        //Clean the iaTest DB table for IntegrityAuditEntity entries
        cleanDb("testPapPU", properties);

        logger.info("setUpDB: Exiting");
    }

    public void cleanDb(String persistenceUnit, Properties properties) {
        logger.debug("cleanDb: enter");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);

        EntityManager em = emf.createEntityManager();
        // Start a transaction
        EntityTransaction et = em.getTransaction();

        et.begin();

        // Clean up the DB
        em.createQuery("Delete from IntegrityAuditEntity").executeUpdate();

        // commit transaction
        et.commit();
        em.close();
        logger.debug("cleanDb: exit");
    }

    @Before
    public void setUp() throws ServletException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(httpServletRequest.getAttributeNames()).thenReturn(Collections.enumeration(headers));

        servletConfig = Mockito.mock(MockServletConfig.class);
        System.setProperty("com.sun.management.jmxremote.port", "9993");
        Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME"))
                .thenReturn("src/test/resources/xacml.pap.properties");
        pap = new XACMLPapServlet();
        pap.init(servletConfig);
        commonClassDao = Mockito.mock(CommonClassDao.class);
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        Mockito.mock(DictionaryUtils.class);
    }

    @Test
    public void testFirwallCreatePolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        String json =
                "{\"serviceTypeId\":\"/v0/firewall/pan\",\"configName\":\"TestFwPolicyConfig\"," +
                        "\"deploymentOption\":{\"deployNow\":false},\"securityZoneId\":\"cloudsite:dev1a\"," +
                        "\"serviceGroups\":[{\"name\":\"SSH\",\"description\":\"Sshservice entry in servicelist\"," +
                        "\"type\":\"SERVICE\",\"transportProtocol\":\"tcp\",\"appProtocol\":null,\"ports\":\"22\"}]," +
                        "\"addressGroups\":[{\"name\":\"test\",\"description\":\"Destination\"," +
                        "\"members\":[{\"type\":\"SUBNET\",\"value\":\"127.0.0.1/12\"}]},{\"name\":\"TestServers\"," +
                        "\"description\":\"SourceTestServers for firsttesting\",\"members\":[{\"type\":\"SUBNET\"," +
                        "\"value\":\"127.0.0.1/23\"}]}],\"firewallRuleList\":[{\"position\":\"1\"," +
                        "\"ruleName\":\"FWRuleTestServerToTest\",\"fromZones\":[\"UntrustedZoneTestName\"]," +
                        "\"toZones\":[\"TrustedZoneTestName\"],\"negateSource\":false,\"negateDestination\":false," +
                        "\"sourceList\":[{\"type\":\"REFERENCE\",\"name\":\"TestServers\"}]," +
                        "\"destinationList\":[{\"type\":\"REFERENCE\",\"name\":\"Test\"}],\"sourceServices\":[]," +
                        "\"destServices\":[{\"type\":\"REFERENCE\",\"name\":\"SSH\"}],\"action\":\"accept\"," +
                        "\"description\":\"FWrule for Test source to Test destination\",\"enabled\":true," +
                        "\"log\":true}]}";
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Config");
        StdPAPPolicy newPAPPolicy =
                new StdPAPPolicy("Firewall Config", "test", "testDescription", "Test", false, "test", json, 0,
                        "5", "default", "false", "");
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Config_FW_test.1.xml");
    }

    @Test
    public void testBRMSCreatePolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Config");
        Map<String, String> matchingAttributes = new HashMap<>();
        Map<String, String> ruleAttributes = new HashMap<>();
        ruleAttributes.put("templateName", "testPolicy");
        ruleAttributes.put("samPoll", "5");
        ruleAttributes.put("value", "test");
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("BRMS_Param", "test", "testing",
                "BRMS_PARAM_RULE", false, "test",
                matchingAttributes, 0, "DROOLS",
                null, ruleAttributes, "5",
                "default", "false", "", null, null);
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        setPolicyCreation();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Config_BRMS_Param_test.1.xml");
    }

    @Test
    public void testBRMSRawCreatePolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Config");
        Map<String, String> ruleAttributes = new HashMap<>();
        ruleAttributes.put("value", "test");
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("BRMS_Raw", "test", "testig description",
                "BRMS_RAW_RULE", false, "test", ruleAttributes, 0, "DROOLS",
                "test", "4",
                "default", "false", null, null, null);
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        setPolicyCreation();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Config_BRMS_Raw_test.1.xml");
    }

    @Test
    public void testClosedLoopPMCreatePolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Config");
        String json = "{\"test\":\"java\"}";
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("ClosedLoop_PM", "test", "testing", "onap",
                json, false, null, "Registration Failure(Trinity)", false, "test", 0, null,
                "default", "true", "");
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        setPolicyCreation();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Config_PM_test.1.xml");
    }

    @Test
    public void testDecisonAAFPolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Decision");
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("test", "test rule", "ONAP", "AAF", null, null, null,
                null, null, null, null, null, null, null, false, "test", 0);
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Decision_test.1.xml");
    }

    @Test
    public void testDecisonGuardPolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Decision");
        Map<String, String> matchingAttributes = new HashMap<>();
        matchingAttributes.put("actor", "test");
        matchingAttributes.put("recipe", "restart");
        matchingAttributes.put("targets", "test,test1");
        matchingAttributes.put("clname", "");
        matchingAttributes.put("limit", "1");
        matchingAttributes.put("timeWindow", "15");
        matchingAttributes.put("timeUnits", "minute");
        matchingAttributes.put("guardActiveStart", "05:00");
        matchingAttributes.put("guardActiveEnd", "10:00");
        StdPAPPolicy newPAPPolicy =
                new StdPAPPolicy("testGuard", "test rule", "PDPD", "GUARD_YAML", matchingAttributes, null, null,
                        null, null, null, null, null, null, null, false, "test", 0);
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Decision_testGuard.1.xml");
    }

    @Test
    public void testDecisonBLGuardPolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Decision");
        Map<String, String> matchingAttributes = new HashMap<>();
        matchingAttributes.put("actor", "test");
        matchingAttributes.put("recipe", "restart");
        matchingAttributes.put("clname", "test");
        matchingAttributes.put("guardActiveStart", "05:00");
        matchingAttributes.put("guardActiveEnd", "10:00");
        matchingAttributes.put("blackList", "bl1,bl2");
        StdPAPPolicy newPAPPolicy =
                new StdPAPPolicy("testblGuard", "test rule", "PDPD", "GUARD_BL_YAML", matchingAttributes, null, null,
                        null, null, null, null, null, null, null, false, "test", 0);
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Decision_testblGuard.1.xml");
    }

    @Test
    public void testConfigPolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Config");
        Map<String, String> configAttributes = new HashMap<>();
        configAttributes.put("value", "test");
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy(StdPAPPolicyParams.builder()
                .configPolicyType("Base")
                .policyName("test")
                .description("test rule")
                .onapName("TEST")
                .configName("config")
                .attributes(configAttributes)
                .configType("OTHER")
                .body("test body")
                .editPolicy(false)
                .domain("test")
                .highestVersion(0)
                .riskLevel("5")
                .riskType("default")
                .guard("false")
                .ttlDate(null).build());
        MockServletInputStream mockInput =
                new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);

        // set DBDao
        setDBDao();
        pap.service(httpServletRequest, httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Config_test.1.xml");
    }

    private void setPolicyCreation() {
        CommonClassDao commonClassDao = Mockito.mock(CommonClassDao.class);
        PolicyCreation.setCommonClassDao(commonClassDao);
        PolicyEditorScopes editorScope = new PolicyEditorScopes();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("API");
        userInfo.setUserLoginId("API");
        editorScope.setScopeName("test");
        editorScope.setUserCreatedBy(userInfo);
        editorScope.setUserModifiedBy(userInfo);
        Mockito.when(commonClassDao.getEntityItem(PolicyEditorScopes.class, "scopeName", "test"))
                .thenReturn(editorScope);
        BRMSParamTemplate template = new BRMSParamTemplate();
        template.setRuleName("testPolicy");
        template.setUserCreatedBy(userInfo);
        String rule = "package com.sample;\n"
                + "import com.sample.DroolsTest.Message;\n"
                + "declare Params\n"
                + "samPoll : int\n"
                + "value : String\n"
                + "end\n"
                + "///This Rule will be generated by the UI.\n"
                + "rule \"${policyName}.Create parameters structure\"\n"
                + "salience 1000  \n"
                + "when\n"
                + "then\n"
                + "Params params = new Params();\n"
                + "params.setSamPoll(76);\n"
                + "params.setValue(\"test\");\n"
                + "insertLogical(params);\n"
                + "end\n"
                + "rule \"Rule 1: Check parameter structure access from when/then\"\n"
                + "when\n"
                + "$param: Params()\n"
                + "Params($param.samPoll > 50)\n"
                + "then\n"
                + "System.out.println(\"Firing rule 1\");\n"
                + "System.out.println($param);\n"
                + "end\n";
        template.setRule(rule);
        Mockito.when(commonClassDao.getEntityItem(BRMSParamTemplate.class, "ruleName", "testPolicy"))
                .thenReturn(template);

    }

    @Test
    public void testClosedLoopCreateDictionary() throws IOException, SQLException, ServletException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        // Check VSCLAction. 
        String json = "{\"dictionaryFields\": {\"vsclaction\": \"testRestAPI\",\"description\": \"testing create\"}}";
        dictionaryTestSetup(false, "VSCLAction", json);
        // set DBDao
        ClosedLoopDictionaryController.setCommonClassDao(new CommonClassDaoImpl());
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check VNFType
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\": {\"vnftype\": \"testrestAPI1\",\"description\": \"testing create\"}}";
        dictionaryTestSetup(false, "VNFType", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check PEPOptions
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"pepName\":\"testRestAPI\",\"description\":\"testing create\"," +
                        "\"attributes\":[{\"option\":\"test1\",\"number\":\"test\"},{\"option\":\"test2\"," +
                        "\"number\":\"test\"}]}}";
        dictionaryTestSetup(false, "PEPOptions", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check Varbind
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"varbindName\":\"testRestAPI\",\"varbindDescription\":\"testing\"," +
                        "\"varbindOID\":\"test\"}}";
        dictionaryTestSetup(false, "Varbind", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check Service
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"serviceName\":\"testRestAPI\",\"description\":\"testing\"}}";
        dictionaryTestSetup(false, "Service", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check Site
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"siteName\":\"testRestAPI\",\"description\":\"testing\"}}";
        dictionaryTestSetup(false, "Site", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testFirewallCreateDictionary() throws IOException, SQLException, ServletException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        // Check SecurityZone. 
        String json = "{\"dictionaryFields\":{\"zoneName\":\"testRestAPI\",\"zoneValue\":\"testing\"}}";
        dictionaryTestSetup(false, "SecurityZone", json);
        // set DBDao
        FirewallDictionaryController.setCommonClassDao(new CommonClassDaoImpl());
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check Action List
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"actionName\":\"testRestAPI\",\"description\":\"test\"}}";
        dictionaryTestSetup(false, "ActionList", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check Protocol List. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"protocolName\":\"testRestAPI\",\"description\":\"test\"}}";
        dictionaryTestSetup(false, "ProtocolList", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check Zone. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"zoneName\":\"testRestAPI\",\"zoneValue\":\"test\"}}";
        dictionaryTestSetup(false, "Zone", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check PrefixList. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"prefixListName\":\"testRestAPI\",\"prefixListValue\":\"127.0.0.1\"," +
                        "\"description\":\"testing\"}}";
        dictionaryTestSetup(false, "PrefixList", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check AddressGroup. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"groupName\":\"testRestAPIgroup\",\"description\":\"testing\"," +
                        "\"attributes\":[{\"option\":\"testRestAPI\"}, {\"option\":\"testRestAPI\"}]}}";
        dictionaryTestSetup(false, "AddressGroup", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check ServiceGroup. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"groupName\":\"testRestAPIServiceGroup\"," +
                        "\"attributes\":[{\"option\":\"testRestAPIservice\"}]}}";
        dictionaryTestSetup(false, "ServiceGroup", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check ServiceList. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"serviceName\":\"testRestAPIservice\",\"serviceDescription\":\"test\"," +
                        "\"servicePorts\":\"8888\",\"transportProtocols\":[{\"option\":\"testRestAPI\"}," +
                        "{\"option\":\"testRestAPI1\"}],\"appProtocols\":[{\"option\":\"testRestAPI\"}," +
                        "{\"option\":\"testRestAPI1\"}]}}";
        dictionaryTestSetup(false, "ServiceList", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        //
        // Check TermList. 
        //
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"termName\":\"testRestAPIRule\",\"termDescription\":\"testing\"," +
                        "\"fromZoneDatas\":[{\"option\":\"testRestAPI\"}]," +
                        "\"toZoneDatas\":[{\"option\":\"testRestAPI1\"}]," +
                        "\"sourceListDatas\":[{\"option\":\"Group_testportal\"}]," +
                        "\"destinationListDatas\":[{\"option\":\"testRestAPI\"}]," +
                        "\"sourceServiceDatas\":[{\"option\":\"testRestAPIservice\"}," +
                        "{\"option\":\"testRestAPIservice1\"}]," +
                        "\"destinationServiceDatas\":[{\"option\":\"testRestAPIservice1\"}," +
                        "{\"option\":\"testportalservice2\"}],\"actionListDatas\":[{\"option\":\"testRestAPI\"}]}}";
        dictionaryTestSetup(false, "TermList", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify 
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testCommonCreateDictionary() throws IOException, SQLException, ServletException {
        new DictionaryController(commonClassDao);
        new ActionPolicyDictionaryController(commonClassDao);
        new SafePolicyController(commonClassDao);
        new DescriptiveDictionaryController(commonClassDao);
        List<Object> object = new ArrayList<>();
        object.add(new Category());
        when(commonClassDao.getDataById(Category.class, "shortName", "resource")).thenReturn(object);
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        String json =
                "{\"dictionaryFields\": {\"onapName\": \"testMMRestAPI1\",\"description\": \"testing update response " +
                        "message\"}}";
        dictionaryTestSetup(false, "OnapName", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\": {\"xacmlId\": \"testMMRestAPI1\",\"datatypeBean\": {\"shortName\": " +
                        "\"string\"}, \"description\": \"testing update\",\"priority\": \"High\"," +
                        "\"userDataTypeValues\": [{\"attributeValues\": \"testAttr\"}, {\"attributeValues\": " +
                        "\"testAttr2\"}, {\"attributeValues\": \"testAttr3\"}]}}";
        dictionaryTestSetup(false, "Attribute", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);


        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"attributeName\":\"TestMMrestAPI1\",\"type\":\"REST\",\"url\":\"testsomeurl" +
                        ".com\",\"method\":\"GET\",\"description\":\"test create\",\"body\":\"Testing Create\"," +
                        "\"headers\":[{\"option\":\"test1\",\"number\":\"test\"},{\"option\":\"test2\"," +
                        "\"number\":\"test\"}]}}";
        dictionaryTestSetup(false, "Action", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"scopeName\":\"testMMRestAPI1\",\"description\":\"test\"," +
                        "\"attributes\":[{\"option\":\"test1\",\"number\":\"test\"},{\"option\":\"test2\"," +
                        "\"number\":\"test\"}]}}";
        dictionaryTestSetup(false, "DescriptiveScope", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"riskName\":\"testMMrestAPI1\",\"description\":\"test\"}}";
        dictionaryTestSetup(false, "RiskType", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"name\":\"testMMrestAPI1\",\"message\":\"test\"," +
                        "\"riskType\":\"testMMrestAPI1\"}}";
        dictionaryTestSetup(false, "SafePolicyWarning", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testDecisionCreateDictionary() throws IOException, SQLException, ServletException {
        new DecisionPolicyDictionaryController(commonClassDao);
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        String json =
                "{\"dictionaryFields\":{\"xacmlId\":\"testMMRestAPI1\",\"datatypeBean\":{\"shortName\":\"string\"}," +
                        "\"description\":\"test\",\"priority\":\"High\"}}";
        dictionaryTestSetup(false, "Settings", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"bbid\":\"BB1\",\"workstep\":\"1\",\"treatments\":\"Manual Handling,Abort," +
                        "Retry\"}}";
        dictionaryTestSetup(false, "RainyDayTreatments", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testMSCreateDictionary() throws IOException, SQLException, ServletException {
        new MicroServiceDictionaryController(commonClassDao);
        new PolicyScopeDictionaryController(commonClassDao);
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        String json = "{\"dictionaryFields\":{\"name\":\"testMMrestAPI1\",\"descriptionValue\":\"test\"}}";
        dictionaryTestSetup(false, "MicroServiceLocation", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"name\":\"testMMrestAPI1\",\"descriptionValue\":\"test\"}}";
        dictionaryTestSetup(false, "MicroServiceConfigName", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"name\":\"testMMrestAPI1\",\"description\":\"test\"}}";
        dictionaryTestSetup(false, "DCAEUUID", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"name\":\"testApiANY\",\"descriptionValue\":\"default test\"}}";
        dictionaryTestSetup(false, "PolicyScopeService", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"name\":\"testApiANY\",\"descriptionValue\":\"default test\"}}";
        dictionaryTestSetup(false, "PolicyScopeResource", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"name\":\"testApiANY\",\"descriptionValue\":\"default test\"}}";
        dictionaryTestSetup(false, "PolicyScopeType", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json = "{\"dictionaryFields\":{\"name\":\"testApiANY\",\"descriptionValue\":\"default test\"}}";
        dictionaryTestSetup(false, "PolicyScopeClosedLoop", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
        json =
                "{\"dictionaryFields\":{\"groupName\":\"testMMrestAPI1\",\"description\":\"testing\"}," +
                        "\"groupPolicyScopeListData1\":{\"resource\":\"ANY\",\"type\":\"ANY\",\"service\":\"ANY\"," +
                        "\"closedloop\":\"ANY\"}}";
        dictionaryTestSetup(false, "GroupPolicyScopeList", json);
        // send Request to PAP
        pap.service(httpServletRequest, httpServletResponse);
        // Verify
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    private void dictionaryTestSetup(Boolean updateFlag, String dictionaryType, String json)
            throws IOException, SQLException {
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getHeader("ClientScope")).thenReturn("dictionaryItem");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        if (updateFlag) {
            Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("update");
        } else {
            Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        }
        Mockito.when(httpServletRequest.getParameter("dictionaryType")).thenReturn(dictionaryType);
        MockServletInputStream mockInput = new MockServletInputStream(json.getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);
        Mockito.when(httpServletRequest.getReader()).thenReturn(new BufferedReader(new InputStreamReader(mockInput)));
        // set DBDao
        setDBDao();
    }

    public void setDBDao() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        // In-memory DB for testing
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.scanPackages("org.onap.*", "com.*");

        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "drop");
        properties.put("hibernate.hbm2ddl.auto", "create");

        sessionBuilder.addProperties(properties);
        sessionFactory = sessionBuilder.buildSessionFactory();

        // Set up dao with SessionFactory
        CommonClassDaoImpl.setSessionfactory(sessionFactory);
        PolicyCreation.setCommonClassDao(new CommonClassDaoImpl());
    }

    @Test
    public void getDictionary() throws ServletException, IOException {
        String[] dictionarys = new String[]{"Attribute", "OnapName", "Action", "BRMSParamTemplate", "VSCLAction"
                , "VNFType", "PEPOptions", "Varbind", "Service", "Site", "Settings", "RainyDayTreatments",
                "DescriptiveScope", "ActionList", "ProtocolList", "Zone", "SecurityZone",
                "PrefixList", "AddressGroup", "ServiceGroup", "ServiceList", "TermList",
                "MicroServiceLocation", "MicroServiceConfigName", "DCAEUUID", "MicroServiceModels",
                "PolicyScopeService", "PolicyScopeResource", "PolicyScopeType", "PolicyScopeClosedLoop",
                "GroupPolicyScopeList", "RiskType", "SafePolicyWarning", "MicroServiceDictionary"};
        for (String dictionary : dictionarys) {
            httpServletRequest = Mockito.mock(HttpServletRequest.class);
            httpServletResponse = new MockHttpServletResponse();
            Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
            Mockito.when(httpServletRequest.getMethod()).thenReturn("GET");
            Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
            Mockito.when(httpServletRequest.getParameter("dictionaryType")).thenReturn(dictionary);
            pap.service(httpServletRequest, httpServletResponse);
            assertTrue(HttpServletResponse.SC_OK == httpServletResponse.getStatus());
        }
    }

    @Test
    public void testDummy() throws ServletException, IOException {

        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
        mockOutput = Mockito.mock(ServletOutputStream.class);

        try {
            Mockito.when(httpServletResponse.getOutputStream()).thenReturn(mockOutput);
        } catch (IOException e) {
            fail();
        }

        try {
            pap.service(httpServletRequest, httpServletResponse);
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @After
    public void destroy() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        pap.destroy();
    }
}
