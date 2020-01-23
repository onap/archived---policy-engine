/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.pap.xacml.rest.components.ConfigPolicy;
import org.onap.policy.pap.xacml.rest.components.Policy;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDaoTest;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDaoTransaction;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.policycontroller.PolicyCreation;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.PolicyDbException;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

public class ConsoleAndApiServiceTest {
    private static final String TESTGRP5 = "testgrp5";
    private static final String POLICY_ID = "policyId";
    private static final String TESTGRP1 = "testgrp1";
    private static final String TESTGROUP2 = "testgroup2";
    private static final String DEFAULT = "default";
    private static final String PDPS = "pdps";
    private static final String TESTGRP2 = "testgrp2";
    private static final String POLICY_NAME = "com.Config_SampleTest1206.1.xml";
    private static final String PUT = "PUT";
    private static final String POST = "POST";
    private static final String DEVL = "DEVL";
    private static final String TESTGRP4 = "testgrp4";
    private static final String API2 = "api";
    private static final String API = "API";
    private static final String GROUP_DESCRIPTION = "groupDescription";
    private static final String GROUP_NAME = "groupName";
    private static final String PDP_ID = "pdpId";
    private static final String USER_ID = "userId";
    private static final String APIFLAG = "apiflag";
    private static final String ENVIRONMENT_HEADER = "Environment";
    private static final OnapLoggingContext logContext = Mockito.mock(OnapLoggingContext.class);
    private static PolicyDbDao dbd;
    private static StdEngine stdEngine = null;
    private static SessionFactory sessionFactory = null;
    private static List<String> headers = new ArrayList<>();
    private static ConsoleAndApiService consoleAndApi;
    private static MockServletConfig servletConfig;
    private static XACMLPapServlet pap;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Sets the up before class.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pap.properties");
        sessionFactory = PolicyDBDaoTest.setupH2DbDaoImpl("testConsoleApi");
        PolicyDbDao.setJunit(true);
        dbd = PolicyDbDao.getPolicyDbDaoInstance();
        PolicyDbDao.setJunit(true);
        consoleAndApi = new ConsoleAndApiService();

        servletConfig = Mockito.mock(MockServletConfig.class);
        System.setProperty("com.sun.management.jmxremote.port", "9993");
        Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME"))
                .thenReturn("src/test/resources/xacml.pap.properties");
        pap = new XACMLPapServlet();
        pap.init(servletConfig);
    }

    @AfterClass
    public static void after() {
        pap.destroy();
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = new MockHttpServletResponse();
        Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(httpServletRequest.getAttributeNames()).thenReturn(Collections.enumeration(headers));
        CommonClassDaoImpl.setSessionfactory(sessionFactory);
        PolicyCreation.setCommonClassDao(new CommonClassDaoImpl());
        File tmpGrpDir = folder.newFolder("src", "test", "resources", "grpTest");
        stdEngine = new StdEngine(tmpGrpDir.toPath());
        dbd.setPapEngine(stdEngine);
    }

    @Test
    public void testGroupCreation() throws IOException {
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(POST);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(GROUP_DESCRIPTION)).thenReturn("test");
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGRP1);
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, TESTGRP1, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

    }

    @Test
    public void testGroupNotExistInDb() throws IOException {
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(POST);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn("testgrpNotExist");
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, "testgrpNotExist", logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_INTERNAL_SERVER_ERROR == httpServletResponse.getStatus());

    }

    @Test
    public void testGroupChange() throws IOException {
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(POST);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(GROUP_DESCRIPTION)).thenReturn("test");
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGRP2);
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, TESTGRP2, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

        Mockito.when(httpServletRequest.getParameter(GROUP_DESCRIPTION)).thenReturn(null);
        Mockito.when(httpServletRequest.getParameter(DEFAULT)).thenReturn(DEFAULT);
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, TESTGRP2, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

    }

    @Test
    public void testPushPolicy() throws Exception {
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(POST);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(GROUP_DESCRIPTION)).thenReturn(null);
        Mockito.when(httpServletRequest.getParameter(POLICY_ID)).thenReturn(POLICY_NAME);
        stdEngine = new StdEngine(Paths.get(PDPS));
        dbd.setPapEngine(stdEngine);
        populatePolicyInDb();

        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, DEFAULT, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

    }

    @Test
    public void testCreatePolicy() throws Exception {
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(PUT);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter("policy")).thenReturn(POLICY_NAME);
        stdEngine = new StdEngine(Paths.get(PDPS));
        dbd.setPapEngine(stdEngine);

        consoleAndApi.doAcPut(httpServletRequest, httpServletResponse, DEFAULT, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

    }

    @Test
    public void testCreateAndMovePdp() throws Exception {
        // create two groups, create a pdp on one group and then move it to another group
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(POST);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(GROUP_DESCRIPTION)).thenReturn("test");
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGRP4);
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, TESTGRP4, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGRP5);
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, TESTGRP5, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

        Mockito.when(httpServletRequest.getParameter(GROUP_DESCRIPTION)).thenReturn(null);
        Mockito.when(httpServletRequest.getParameter(PDP_ID)).thenReturn("http://localhost:4344/pdp/");
        Mockito.when(httpServletRequest.getMethod()).thenReturn(PUT);
        httpServletResponse = new MockHttpServletResponse();
        StdPDP newPdp = new StdPDP("http://localhost:4344/pdp/", "newpdp", "new desc", 9999);
        ObjectWriter ow = new ObjectMapper().writer();
        when(httpServletRequest.getInputStream()).thenReturn(new DelegatingServletInputStream(
                new ByteArrayInputStream(ow.writeValueAsString(newPdp).getBytes(StandardCharsets.UTF_8))));
        consoleAndApi.doAcPut(httpServletRequest, httpServletResponse, TESTGRP5, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = new MockHttpServletResponse();
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn(POST);
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(PDP_ID)).thenReturn("http://localhost:4344/pdp/");
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGRP4);
        consoleAndApi.doAcPost(httpServletRequest, httpServletResponse, TESTGRP4, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        httpServletResponse = new MockHttpServletResponse();
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("DELETE");
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(PDP_ID)).thenReturn("http://localhost:4344/pdp/");
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGRP4);
        consoleAndApi.doAcDelete(httpServletRequest, httpServletResponse, TESTGRP4, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_NO_CONTENT == httpServletResponse.getStatus());

    }

    @Test
    public void testGet() throws Exception {
        stdEngine = new StdEngine(Paths.get("src", "test", "resources", "pdps"));
        dbd.setPapEngine(stdEngine);
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn(DEVL);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("GET");
        Mockito.when(httpServletRequest.getParameter(APIFLAG)).thenReturn(API2);
        Mockito.when(httpServletRequest.getParameter(USER_ID)).thenReturn(API);
        Mockito.when(httpServletRequest.getParameter(PDP_ID)).thenReturn("http://localhost:4344/pdp/");
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn("");
        consoleAndApi.doAcGet(httpServletRequest, httpServletResponse, "", logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_OK == httpServletResponse.getStatus());
        httpServletResponse = new MockHttpServletResponse();
        Mockito.when(httpServletRequest.getParameter(DEFAULT)).thenReturn(DEFAULT);
        consoleAndApi.doAcGet(httpServletRequest, httpServletResponse, "", logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_OK == httpServletResponse.getStatus());
        Mockito.when(httpServletRequest.getParameter(PDP_ID)).thenReturn(null);
        Mockito.when(httpServletRequest.getParameter(DEFAULT)).thenReturn(null);
        consoleAndApi.doAcGet(httpServletRequest, httpServletResponse, "", logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_OK == httpServletResponse.getStatus());
        Mockito.when(httpServletRequest.getParameter("getPDPGroup")).thenReturn(TESTGROUP2);
        Mockito.when(httpServletRequest.getParameter(GROUP_NAME)).thenReturn(TESTGROUP2);
        consoleAndApi.doAcGet(httpServletRequest, httpServletResponse, TESTGROUP2, logContext, stdEngine);
        assertTrue(HttpServletResponse.SC_OK == httpServletResponse.getStatus());
    }

    private static void populatePolicyInDb() throws IOException, PolicyDbException {
        CommonClassDaoImpl.setSessionfactory(sessionFactory);
        PolicyCreation.setCommonClassDao(new CommonClassDaoImpl());
        Policy policyObject = new ConfigPolicy();
        policyObject.policyAdapter = new PolicyRestAdapter();
        policyObject.policyAdapter.setConfigName("testpolicyhandle");
        policyObject.policyAdapter.setPolicyDescription("my description");
        policyObject.policyAdapter.setConfigBodyData("this is my test config file");
        policyObject.policyAdapter.setPolicyName("SampleTest1206");
        policyObject.policyAdapter.setConfigType(ConfigPolicy.OTHER_CONFIG);
        policyObject.policyAdapter.setPolicyType("Config");
        policyObject.policyAdapter.setDomainDir("com");
        policyObject.policyAdapter.setVersion("1");
        policyObject.policyAdapter.setHighestVersion(1);
        PolicyType policyTypeObject = new PolicyType();
        policyObject.policyAdapter.setPolicyData(policyTypeObject);

        PolicyType policyConfig = new PolicyType();
        policyConfig.setVersion("1");
        policyConfig.setPolicyId("");
        policyConfig.setTarget(new TargetType());
        policyObject.policyAdapter.setData(policyConfig);
        policyObject.policyAdapter.setParentPath(IOUtils.toString(
                ConsoleAndApiServiceTest.class.getClassLoader().getResourceAsStream("Config_SampleTest1206.1.xml")));

        PolicyDbDaoTransaction transaction = dbd.getNewTransaction();
        transaction.createPolicy(policyObject, API);
        transaction.commitTransaction();
    }
}
