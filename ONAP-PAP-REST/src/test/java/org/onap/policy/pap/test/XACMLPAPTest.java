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

package org.onap.policy.pap.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.policycontroller.PolicyCreation;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import com.mockrunner.mock.web.MockServletInputStream;


public class XACMLPAPTest {

    private static final String ENVIRONMENT_HEADER = "Environment";
    private List<String> headers = new ArrayList<>();
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private ServletOutputStream mockOutput;
    private ServletConfig servletConfig;
    private XACMLPapServlet pap;
    private SessionFactory sessionFactory;

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
    }
    
    @Test
    public void testFirwallCreatePolicy() throws IOException, ServletException, SQLException {
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        String json = "";
        Mockito.when(httpServletRequest.getHeader(ENVIRONMENT_HEADER)).thenReturn("DEVL");
        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        Mockito.when(httpServletRequest.getParameter("apiflag")).thenReturn("api");
        Mockito.when(httpServletRequest.getParameter("operation")).thenReturn("create");
        Mockito.when(httpServletRequest.getParameter("policyType")).thenReturn("Config");
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy("Firewall Config", "test", "testDescription", "Test", false, "test", json, 0, 
                "5","default", "false", "");
        MockServletInputStream mockInput = new MockServletInputStream(PolicyUtils.objectToJsonString(newPAPPolicy).getBytes());
        Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);
        
        // set DBDao
        setDBDao();
        pap.service(httpServletRequest, httpServletResponse);
        
        Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(httpServletResponse).addHeader("successMapKey", "success");
        Mockito.verify(httpServletResponse).addHeader("policyName", "test.Config_FW_test.1.xml");
        
    }
    
    

    private void setDBDao() throws SQLException {
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
    public void destroy(){
        if(sessionFactory!=null){
            sessionFactory.close();
        }
        pap.destroy();
    }
}
