/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.ia.DbDAO;
import org.onap.policy.common.ia.IntegrityAuditProperties;
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.im.IntegrityMonitorException;
import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPStatus;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;


import com.att.research.xacml.util.XACMLProperties;
import com.mockrunner.mock.web.MockServletInputStream;

import junit.framework.TestCase;

public class XACMLPdpServletTest extends TestCase{
	private static Logger LOGGER	= FlexLogger.getLogger(XACMLPdpServletTest.class);
	
	private List<String> headers = new ArrayList<>();
	
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ServletOutputStream mockOutput;
	private ServletInputStream mockInput;
	private ServletConfig servletConfig; 
	private XACMLPdpServlet pdpServlet;
	private IntegrityMonitor im;
	
	private DbDAO dbDAO;
	private String persistenceUnit;
	private Properties properties;
	private String resourceName;
	private String dbDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String siteName;
	private String nodeType;
	private static final String DEFAULT_DB_DRIVER = "org.h2.Driver";
	private static final String DEFAULT_DB_USER = "sa";
	private static final String DEFAULT_DB_PWD = "";

	private StdPDPStatus status;
	private StdPDPPolicy foobarPolicy;

    @Before
    public void setUp(){
    	status = new StdPDPStatus();
    	foobarPolicy = new StdPDPPolicy();
		foobarPolicy.setId("foobar");
		foobarPolicy.setVersion("123");	
		foobarPolicy.setName("nothing");
		status.addLoadedPolicy(foobarPolicy);


    	properties = new Properties();
		properties.put(IntegrityAuditProperties.DB_DRIVER, XACMLPdpServletTest.DEFAULT_DB_DRIVER);
		properties.put(IntegrityAuditProperties.DB_URL, "jdbc:h2:file:./sql/xacmlTest");
		properties.put(IntegrityAuditProperties.DB_USER, XACMLPdpServletTest.DEFAULT_DB_USER);
		properties.put(IntegrityAuditProperties.DB_PWD, XACMLPdpServletTest.DEFAULT_DB_PWD);
		properties.put(IntegrityAuditProperties.SITE_NAME, "SiteA");
		properties.put(IntegrityAuditProperties.NODE_TYPE, "pap");
		//properties.put("com.sun.management.jmxremote.port", "9999");
		dbDriver = XACMLPdpServletTest.DEFAULT_DB_DRIVER;
		dbUrl = "jdbc:h2:file:./sql/xacmlTest";
		dbUser = XACMLPdpServletTest.DEFAULT_DB_USER;
		dbPwd = XACMLPdpServletTest.DEFAULT_DB_PWD;
		siteName = "SiteA";
		nodeType = "pdp";
		persistenceUnit = "testPdpPU";
		resourceName = "siteA.pdp1";
		
		System.setProperty("com.sun.management.jmxremote.port", "9999");
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);
		
		EntityManager em = emf.createEntityManager();
		// Start a transaction
		EntityTransaction et = em.getTransaction();
		
		IntegrityMonitor im = null;
		try {
			im = IntegrityMonitor.getInstance(resourceName, properties);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//cleanDb(persistenceUnit, properties);
		
    	httpServletRequest = Mockito.mock(HttpServletRequest.class);
    	Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
    	Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers));
    	Mockito.when(httpServletRequest.getAttributeNames()).thenReturn(Collections.enumeration(headers));
    	Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/pdp/test");
    	
    	mockOutput = Mockito.mock(ServletOutputStream.class);
    	
    	httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
    	
    	try {
			Mockito.when(httpServletResponse.getOutputStream()).thenReturn(mockOutput);
		} catch (IOException e) {
			fail();
		}

    	servletConfig = Mockito.mock(MockServletConfig.class);
    	//servletConfig
    	Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
    	pdpServlet = new XACMLPdpServlet();
    	pdpServlet.setIm(im);
    	
    	Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("src/test/resources/xacml.pdp.properties");
    	
		System.setProperty("xacml.properties", "src/test/resources/xacml.pdp.properties");
		System.setProperty("xacml.rest.pdp.config", "src/test/resources/config_testing");
		System.setProperty("xacml.rest.pdp.webapps", "src/test/resources/webapps");
		/*System.setProperty("xacml.rootPolicies", "test_PolicyEngine.xml");
		System.setProperty("xacml.referencedPolicies", "test_PolicyEngine.xml");
		System.setProperty("test_PolicyEngine.xml.file", "config_testing\\test_PolicyEngine.xml");
		*/
		System.setProperty("xacml.rest.pdp.register", "false");
		System.setProperty("com.sun.management.jmxremote.port", "9999");
		
		im = Mockito.mock(IntegrityMonitor.class);
		// Need PowerMockito for mocking static method getInstance(...)
		PowerMockito.mockStatic(IntegrityMonitor.class);
		try {
			// when IntegrityMonitor.getInstance is called, return the mock object
			PowerMockito.when(IntegrityMonitor.getInstance(Mockito.anyString(), Mockito.any(Properties.class))).thenReturn(im);
		} catch (Exception e1) {
			LOGGER.error("Exception Occured"+e1);
		}
		
		try {
			Mockito.doNothing().when(im).startTransaction();
		} catch (IntegrityMonitorException e) {
			fail();
		}
		Mockito.doNothing().when(im).endTransaction();
    }

	@Test
    public void testInit(){
		LOGGER.info("XACMLPdpServletTest - testInit");
		try {	
			pdpServlet.init(servletConfig);

			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
			
		}

	}

	@Test
	public void testUebNotification() {
		LOGGER.info("XACMLPdpServletTest - testUebNotification");
		try {	

			XACMLProperties.reloadProperties();
			System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pdp.ueb.properties");
			XACMLProperties.getProperties();

		   pdpServlet.init(servletConfig);

			status.setStatus(com.att.research.xacml.api.pap.PDPStatus.Status.UPDATING_CONFIGURATION);
			
			XACMLPdpLoader.validatePolicies(properties, status);
			XACMLPdpLoader.sendNotification();
			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
			
		}
		
	}

	@Test
	public void testDmaapNotification() {
		LOGGER.info("XACMLPdpServletTest - testDmaapNotification");
		try {	
			
			XACMLProperties.reloadProperties();
			System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pdp.dmaap.properties");
			XACMLProperties.getProperties();
		
			pdpServlet.init(servletConfig);

			status.setStatus(com.att.research.xacml.api.pap.PDPStatus.Status.UPDATING_CONFIGURATION);
			
			XACMLPdpLoader.validatePolicies(properties, status);
			XACMLPdpLoader.sendNotification();
			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
			
		}
		
	}
	

	@Test
	public void testXACMLPdpRegisterThread() {
		LOGGER.info("XACMLPdpServletTest - testXACMLPdpRegisterThread");
		try {
			ONAPLoggingContext baseLoggingContext = new ONAPLoggingContext();
			baseLoggingContext.setServer("localhost");
			XACMLPdpRegisterThread regThread = new XACMLPdpRegisterThread(baseLoggingContext);
			regThread.run();
			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}
	
	@Test
	public void testDoGetNoTypeError(){
		LOGGER.info("XACMLPdpServletTest - testDoGetNoTypeError");
		try{
			
			pdpServlet.init(servletConfig);
			pdpServlet.doGet(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
			assertTrue(true);
		}catch(Exception e){
			System.out.println("Unexpected exception in testDoGetNoTypeError");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}
   
	@Test
	public void testDoGetConfigType(){
		LOGGER.info("XACMLPdpServletTest - testDoGetConfigType");
		Mockito.when(httpServletRequest.getParameter("type")).thenReturn("config");	

		try{	
			pdpServlet.init(servletConfig);
			pdpServlet.doGet(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoGetConfigType");
			LOGGER.error("Exception Occured"+e);
			fail();
		}

	}
    
	@Test
	public void testDoGetTypeHb(){
		LOGGER.info("XACMLPdpServletTest - testDoGetTypeHb");
		try{
			Mockito.when(httpServletRequest.getParameter("type")).thenReturn("hb");
			pdpServlet.init(servletConfig);
			pdpServlet.doGet(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
			assertTrue(true);
		}catch(Exception e){
			System.out.println("Unexpected exception in testDoGetTypeHb");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}
    
	@Test
	public void testDoGetTypeStatus(){
		LOGGER.info("XACMLPdpServletTest - testDoGetTypeStatus");
		try{
			Mockito.when(httpServletRequest.getParameter("type")).thenReturn("Status");
			pdpServlet.init(servletConfig);
			pdpServlet.doGet(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
			assertTrue(true);
		}catch(Exception e){
			System.out.println("Unexpected exception in testDoGetTypeStatus");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	
    
	@Test
	public void testDoPost(){
		LOGGER.info("XACMLPdpServletTest - testDoPost");
		try{
			pdpServlet.init(servletConfig);
			pdpServlet.doPost(httpServletRequest, httpServletResponse);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPost");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}

	@Test
	public void testDoPostToLong(){
		LOGGER.info("XACMLPdpServletTest - testDoPostToLong");
		try{
			Mockito.when(httpServletRequest.getContentType()).thenReturn("stuff");
			Mockito.when(httpServletRequest.getContentLength()).thenReturn(32768);
			
			pdpServlet.init(servletConfig);
			pdpServlet.doPost(httpServletRequest, httpServletResponse);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPostToLong");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	

	@Test
	public void testDoPostContentLengthNegative(){
		LOGGER.info("XACMLPdpServletTest - testDoPostToLong");
		try{
			Mockito.when(httpServletRequest.getContentType()).thenReturn("stuff");
			Mockito.when(httpServletRequest.getContentLength()).thenReturn(-1);
			
			pdpServlet.init(servletConfig);
			pdpServlet.doPost(httpServletRequest, httpServletResponse);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPostContentLengthNegative");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	

	@Test
	public void testDoPostContentTypeNonValid(){
		LOGGER.info("XACMLPdpServletTest - testDoPostToLong");
		try{
			Mockito.when(httpServletRequest.getContentType()).thenReturn(";");
			Mockito.when(httpServletRequest.getContentLength()).thenReturn(30768);
			
			pdpServlet.init(servletConfig);
			pdpServlet.doPost(httpServletRequest, httpServletResponse);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPostContentTypeNonValid");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	
    
	@Test
	public void testDoPostContentTypeConfigurationError(){
		LOGGER.info("XACMLPdpServletTest - testDoPostToLong");
		try{
			Mockito.when(httpServletRequest.getContentType()).thenReturn("stuff");
			Mockito.when(httpServletRequest.getContentLength()).thenReturn(30768);
			
			pdpServlet.init(servletConfig);
			pdpServlet.doPost(httpServletRequest, httpServletResponse);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPostContentTypeConfigurationError");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	
    
	@Test
	public void testDoPutCacheEmpty(){
		LOGGER.info("XACMLPdpServletTest - testDoPutCacheEmpty");
		mockInput = Mockito.mock(ServletInputStream.class);
		
		try{
			Mockito.when(httpServletRequest.getParameter("cache")).thenReturn("cache");
			Mockito.when(httpServletRequest.getContentType()).thenReturn("text/x-java-properties");
			Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);
			pdpServlet.init(servletConfig);
			pdpServlet.doPut(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "PUT must contain at least one property");
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPutCacheEmpty");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}
    
	@Test
	public void testDoPutConfigPolicies(){
		LOGGER.info("XACMLPdpServletTest - testDoPutConfigPolicies");
		byte[] b = new byte[20];
		new Random().nextBytes(b);
		
		mockInput = new MockServletInputStream(b);
	
		try{
			Mockito.when(httpServletRequest.getParameter("cache")).thenReturn("policies");
			Mockito.when(httpServletRequest.getContentType()).thenReturn("text/x-java-properties");
			Mockito.when(httpServletRequest.getInputStream()).thenReturn(mockInput);
			pdpServlet.init(servletConfig);
			pdpServlet.doPut(httpServletRequest, httpServletResponse);
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPutConfigPolicies");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	
	
	public void testDoPutToLong(){
		LOGGER.info("XACMLPdpServletTest - testDoPutToLong");
		try{
			Mockito.when(httpServletRequest.getParameter("cache")).thenReturn("policies");
			
			Mockito.when(httpServletRequest.getContentType()).thenReturn("text/x-java-properties");
			Mockito.when(httpServletRequest.getContentLength()).thenReturn(1000000000);
			
			pdpServlet.init(servletConfig);
			pdpServlet.doPut(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Content-Length larger than server will accept.");
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPutToLong");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}	
	
	@Test
	public void testDoPutInvalidContentType(){
		LOGGER.info("XACMLPdpServletTest - testDoPutToLong");
		try{
			Mockito.when(httpServletRequest.getParameter("cache")).thenReturn("policies");
			
			Mockito.when(httpServletRequest.getContentType()).thenReturn("text/json");
			Mockito.when(httpServletRequest.getContentLength()).thenReturn(32768);
			
			pdpServlet.init(servletConfig);
			pdpServlet.doPut(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid cache: 'policies' or content-type: 'text/json'");
			assertTrue(true);
		}catch (Exception e){
			System.out.println("Unexpected exception in testDoPutInvalidContentType");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}		

	@Test
	public void testDestroy(){
		LOGGER.info("XACMLPdpServletTest - testDestroy");
		
		try{
			pdpServlet.init(servletConfig);
			pdpServlet.destroy();
		}catch(Exception e){
			System.out.println("Unexpected exception in testDestroy");
			LOGGER.error("Exception Occured"+e);
			fail();
		}
	}
}
