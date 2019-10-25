/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.att.research.xacml.util.XACMLProperties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletInputStream;
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

public class XacmlRestTest extends Mockito {
    private static Log logger = LogFactory.getLog(XacmlRestTest.class);

    private List<String> headers = new ArrayList<>();

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private ServletOutputStream mockOutput;
    private ServletConfig servletConfig;
    private ServletInputStream servletInputStream;

    /**
     * Prepare for the test.
     */
    @Before
    public void setUp() {
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

        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "xacml.pdp.properties");
        System.setProperty("xacml.rest.pdp.config", "config_testing");
        System.setProperty("xacml.rest.pep.idfile", "testclient.properties");
        System.setProperty("xacml.rest.pdp.webapps", "/webapps");
        System.setProperty("xacml.rootPolicies", "test_PolicyEngine.xml");
        System.setProperty("xacml.referencedPolicies", "test_PolicyEngine.xml");
        System.setProperty("test_PolicyEngine.xml.file", "config_testing\\test_PolicyEngine.xml");
        System.setProperty("xacml.rest.pdp.register", "false");

        servletInputStream = Mockito.mock(ServletInputStream.class);
    }

    @Test
    public void testXacmlInit() {
        logger.info("XACMLRestTest - testInit");

        try {
            XacmlRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail("Normal case of initiation of XACML REST failed");
        }

        System.clearProperty(XACMLProperties.XACML_PROPERTIES_NAME);
        try {
            XacmlRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail("Normal case of initiation of XACML REST failed");
        }

        System.clearProperty(XACMLProperties.XACML_PROPERTIES_NAME);
        try {
            Logger.getRootLogger().setLevel(Level.INFO);
            XacmlRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail("Normal case of initiation of XACML REST failed");
        }

        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "xacml.pdp.properties");

        try {
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.xacmlInit(servletConfig);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail("Normal case of initiation of XACML REST failed");
        }

        Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn(null);
        try {
            XacmlRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail("Normal case of initiation of XACML REST failed");
        }

        try {
            Logger.getRootLogger().setLevel(Level.INFO);
            XacmlRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            fail("Normal case of initiation of XACML REST failed");
        }

        Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("xacml.pdp.properties");

        List<String> parNameList = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("Name0");
                add("Name1");
                add("Name2");
                add("XACML_PROPERTIES_NAME");
            }
        };

        Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(parNameList));
        Mockito.when(servletConfig.getInitParameter("Name0")).thenReturn("Value0");
        Mockito.when(servletConfig.getInitParameter("Name1")).thenReturn("Value1");
        Mockito.when(servletConfig.getInitParameter("Name2")).thenReturn("Value2");
        try {
            XacmlRest.xacmlInit(servletConfig);
            Logger.getRootLogger().setLevel(Level.DEBUG);
            XacmlRest.dumpRequest(httpServletRequest);
            XacmlRest.loadXacmlProperties(null, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Normal case of initiation of XACML REST failed");
        }

    }

    @Test
    public void testConstructorIsPrivate()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<XacmlRestProperties> constructor = XacmlRestProperties.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testLoadXacmlProperties() {
        XacmlRest.xacmlInit(servletConfig);
        XacmlRest.loadXacmlProperties(null, null);

        XacmlRest.loadXacmlProperties(new Properties(), new Properties());

        Logger.getRootLogger().setLevel(Level.INFO);
        XacmlRest.loadXacmlProperties(new Properties(), new Properties());
    }

    @Test
    public void testDumpRequest() throws IOException {
        XacmlRest.xacmlInit(servletConfig);

        Logger.getRootLogger().setLevel(Level.INFO);
        XacmlRest.dumpRequest(httpServletRequest);
        Logger.getRootLogger().setLevel(Level.DEBUG);

        Mockito.when(httpServletRequest.getMethod()).thenReturn("GET");
        XacmlRest.dumpRequest(httpServletRequest);
        Mockito.when(httpServletRequest.getParameter("type")).thenReturn("hb");
        XacmlRest.dumpRequest(httpServletRequest);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");

        List<String> headerNameList = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("Name0");
                add("Name1");
            }
        };

        List<String> header0List = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("Name0H0");
                add("Name0H1");
                add("Name0H2");
            }
        };

        List<String> header1List = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("Name1H0");
                add("Name1H1");
                add("Name1H2");
            }
        };

        Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headerNameList));
        Mockito.when(httpServletRequest.getHeaders("Name0")).thenReturn(Collections.enumeration(header0List));
        Mockito.when(httpServletRequest.getHeaders("Name1")).thenReturn(Collections.enumeration(header1List));
        XacmlRest.dumpRequest(httpServletRequest);

        List<String> attributeList = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("Attribute0");
                add("Attribute1");
            }
        };
        Mockito.when(httpServletRequest.getAttributeNames()).thenReturn(Collections.enumeration(attributeList));
        Mockito.when(httpServletRequest.getAttribute("Attribute0")).thenReturn("AttributeValue0");
        Mockito.when(httpServletRequest.getAttribute("Attribute1")).thenReturn("AttributeValue1");
        XacmlRest.dumpRequest(httpServletRequest);

        Mockito.when(httpServletRequest.getInputStream()).thenReturn(servletInputStream);
        XacmlRest.dumpRequest(httpServletRequest);

        Mockito.when(httpServletRequest.getInputStream()).thenThrow(new IOException());
        XacmlRest.dumpRequest(httpServletRequest);

        Mockito.when(httpServletRequest.getMethod()).thenReturn("PUT");
        XacmlRest.dumpRequest(httpServletRequest);

        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        String[] mapValue0 = {"MapValue0"};
        String[] mapValue1 = {"MapValue0"};
        String[] mapValue2 = {};
        parameterMap.put("Key0", mapValue0);
        parameterMap.put("Key1", mapValue1);
        parameterMap.put("Key2", mapValue2);

        Mockito.when(httpServletRequest.getMethod()).thenReturn("DELETE");
        Mockito.when(httpServletRequest.getParameterMap()).thenReturn(parameterMap);
        XacmlRest.dumpRequest(httpServletRequest);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
    }
}
