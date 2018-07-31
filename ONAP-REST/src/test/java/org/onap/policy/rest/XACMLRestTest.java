/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

public class XACMLRestTest extends Mockito{
    private static Log logger	= LogFactory.getLog(XACMLRestTest.class);

    private List<String> headers = new ArrayList<>();

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private ServletOutputStream mockOutput;
    private ServletConfig servletConfig;


    @Before
    public void setUp(){
        httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
        Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(httpServletRequest.getAttributeNames()).thenReturn(Collections.enumeration(headers));

        mockOutput = Mockito.mock(ServletOutputStream.class);

        httpServletResponse = Mockito.mock(MockHttpServletResponse.class);

        try {
            Mockito.when(httpServletResponse.getOutputStream()).thenReturn(mockOutput);
        } catch (IOException e) {
            fail();
        }

        servletConfig = Mockito.mock(MockServletConfig.class);
        Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("xacml.pdp.properties");

        System.setProperty("xacml.properties", "xacml.pdp.properties");
        System.setProperty("xacml.rest.pdp.config", "config_testing");
        System.setProperty("xacml.rest.pep.idfile", "testclient.properties");
        System.setProperty("xacml.rest.pdp.webapps", "/webapps");
        System.setProperty("xacml.rootPolicies", "test_PolicyEngine.xml");
        System.setProperty("xacml.referencedPolicies", "test_PolicyEngine.xml");
        System.setProperty("test_PolicyEngine.xml.file", "config_testing\\test_PolicyEngine.xml");
        System.setProperty("xacml.rest.pdp.register", "false");
    }

    @Test
    public void testXacmlInit(){
        logger.info("XACMLRestTest - testInit");
        try {
            XACMLRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XACMLRest.dumpRequest(httpServletRequest);
            XACMLRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<XACMLRestProperties> constructor = XACMLRestProperties.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}