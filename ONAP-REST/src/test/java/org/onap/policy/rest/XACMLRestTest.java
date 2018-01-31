/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest;

/*import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import com.att.research.xacml.util.XACMLProperties;
import com.mockrunner.mock.web.MockServletInputStream;

public class XACMLRestTest extends TestCase{
	private static Log logger	= LogFactory.getLog(XACMLRestTest.class);

	private List<String> headers = new ArrayList<String>();

	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ServletOutputStream mockOutput;
	private ServletInputStream mockInput;
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
    	//pdpServlet = new XACMLPdpServlet();
    
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
	public void testDummy(){
		logger.info("XACMLRestTest - testInit");
		try {
			assertTrue(true);
		} catch (Exception e) {
			fail();

		}

	}
}
*/