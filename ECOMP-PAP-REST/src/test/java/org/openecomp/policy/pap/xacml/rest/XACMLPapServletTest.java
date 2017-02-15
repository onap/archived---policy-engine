/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.xacml.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.policy.pap.xacml.rest.XACMLPapServlet;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import org.openecomp.policy.common.ia.IntegrityAudit;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger; 

public class XACMLPapServletTest extends TestCase{
	private static Logger logger	= FlexLogger.getLogger(XACMLPapServletTest.class);
	
	private List<String> headers = new ArrayList<String>();
	
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ServletOutputStream mockOutput;
	private ServletConfig servletConfig; 
	private XACMLPapServlet papServlet;

	 
    @Before
   
    public void setUp() throws IOException {
    	httpServletRequest = Mockito.mock(HttpServletRequest.class);
    	Mockito.when(httpServletRequest.getMethod()).thenReturn("POST");
    	Mockito.when(httpServletRequest.getParameter("groupId")).thenReturn(null);
    	Mockito.when(httpServletRequest.getHeaderNames()).thenReturn(Collections.enumeration(headers));
    	Mockito.when(httpServletRequest.getAttributeNames()).thenReturn(Collections.enumeration(headers));
    	
    	
    	mockOutput = Mockito.mock(ServletOutputStream.class);
    	
    	//when(httpServletRequest.getPathInfo()).thenReturn("/lineup/world.xml");
    	//HttpServletResponse httpResponse = new HttpServletResponse();
    	httpServletResponse = Mockito.mock(MockHttpServletResponse.class);
    	
    	Mockito.when(httpServletResponse.getOutputStream()).thenReturn(mockOutput);

    	
    	//when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
    	servletConfig = Mockito.mock(MockServletConfig.class);
    	//Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
    	//servletConfig
    	Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
    	papServlet = new XACMLPapServlet();
    	
    	Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("xacml.pap.test.properties");
    	
		System.setProperty("xacml.PAP.papEngineFactory", "org.openecomp.policy.xacml.std.pap.StdEngineFactory");
		System.setProperty("xacml.pap.pdps", "pdps");
		System.setProperty("xacml.rest.pap.url", "http://localhost:8070/pap/");
		System.setProperty("xacml.rest.pap.initiate.pdp", "false");
		System.setProperty("xacml.rest.pdp.idfile", "testpdp.properties");
		System.setProperty("xacml.rest.pep.idfile", "client.properties");
		System.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
		System.setProperty("javax.persistence.jdbc.url", "jdbc:h2:file:./sql/xacmlTest");
		System.setProperty("javax.persistence.jdbc.user", "sa");
		System.setProperty("javax.persistence.jdbc.password", "");
		System.setProperty("xacml.rest.pap.jmx.url", "service:jmx:rmi:///jndi/rmi://localhost:9990/jmxrmi");
		System.setProperty("xacml.rest.pap.resource.name", "site_1.pap_1");
		System.setProperty("fp_monitor_interval", "30");
		System.setProperty("failed_counter_threshold", "3");
		System.setProperty("test_trans_interval", "10");
		System.setProperty("write_fpc_interval", "5");
		System.setProperty("com.sun.management.jmxremote.port", "9999");
		System.setProperty("dependency_groups", "site_1.logparser_1;site_1.adminconsole_1;site_1.elk_1");
		System.setProperty("site_name", "site_1");
		System.setProperty("node_type", "pap"); 
    }
	
    /*
     * This method initializes and cleans the DB so the XACMLPapServlet will be able to instantiate an
     * IntegrityAudit object which will use the DB.
     */
	public void initializeDb(){
		logger.debug("initializeDb: enter");
    	Properties cleanProperties = new Properties();
    	cleanProperties.put(XACMLRestProperties.PROP_PAP_DB_DRIVER,"org.h2.Driver");
    	cleanProperties.put(XACMLRestProperties.PROP_PAP_DB_URL, "jdbc:h2:file:./sql/xacmlTest");
    	cleanProperties.put(XACMLRestProperties.PROP_PAP_DB_USER, "sa");
    	cleanProperties.put(XACMLRestProperties.PROP_PAP_DB_PASSWORD, "");
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPapPU", cleanProperties);
		
		EntityManager em = emf.createEntityManager();
		// Start a transaction
		EntityTransaction et = em.getTransaction();

		et.begin();

		// Clean up the DB
		em.createQuery("Delete from IntegrityAuditEntity").executeUpdate();

		// commit transaction
		et.commit();
		em.close();
		logger.debug("initializeDb: exit");
	}
	
    @Test
	public void testInit() throws Exception{
    	System.setProperty("integrity_audit_period_seconds", "0");
    	initializeDb();
		try {	
			papServlet.init(servletConfig);
			IntegrityAudit ia = papServlet.getIa();
			if(ia.isThreadInitialized()){
				assertTrue(true);
			}else{
				fail();
			}
			ia.stopAuditThread();
			// Allow time for the thread to stop
			Thread.sleep(1000);
			if(!ia.isThreadInitialized()){
				assertTrue(true);
			}else{
				fail();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail();
		}
	}
	
/*	public void testDoGetPapTest(){
		try{
			Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/pap/test");
			papServlet.init(servletConfig);
			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();
			papServlet.doGet(httpServletRequest, httpServletResponse);		
			logger.info(httpServletResponse.getStatus());

			//Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
		}catch (Exception e){
			logger.info("testDoGetPapTest failed with message: " + e.getMessage());
			fail();
		}
		assertTrue(true);
	}*/

/*	
 * Need to figure a way to get it to match any message string
 * public void testDoGetPapTestFpcFailure(){
		try{
			Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/pap/test");
			Mockito.when(httpServletRequest.getHeader("THIS-IS-A-TEST")).thenReturn("FPC");
			papServlet.init(servletConfig);
			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();
			papServlet.doGet(httpServletRequest, httpServletResponse);		
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Mockito.anyString());
		}catch (Exception e){
			logger.info("testDoGetPapTestFpcFailure failed with message: " + e.getMessage());
			fail();
		}
		assertTrue(true);
	}*/
	
	public void testDoGetLocal(){
		try{
			Mockito.when(httpServletRequest.getRemoteHost()).thenReturn("localhost");
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();
			papServlet.doGet(httpServletRequest, httpServletResponse);		
			
			logger.info(httpServletResponse.getStatus());
			Mockito.verify(httpServletResponse).setHeader("content-type", "application/json");
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
		}catch (Exception e){
			fail();
		}

		assertTrue(true);
	}
	
	public void testDoGetNonLocal(){
		//return non-local host remote address, which is invalid
		Mockito.when(httpServletRequest.getRemoteHost()).thenReturn("0.0.0.0");	
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doGet(httpServletRequest, httpServletResponse);		
			logger.info(httpServletResponse.getStatus());	
			String message = "Unknown PDP:  from 0.0.0.0 us: null";
			
			Mockito.verify(httpServletResponse).sendError(401, message);
			
		}catch (Exception e){
			fail();
		}
	}
	
	public void testDoGetWithGroup() throws Exception{
		Mockito.when(httpServletRequest.getParameter("groupId")).thenReturn("default");
		//Mockito.when(httpServletRequest.getHeader("X-XACML-PDP-ID")).thenReturn("default");
		papServlet.init(servletConfig);
		IntegrityAudit ia = papServlet.getIa();
		ia.stopAuditThread();		
		papServlet.doGet(httpServletRequest, httpServletResponse);
		Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
	}
	
	public void testDoPostWithGroup(){
		Mockito.when(httpServletRequest.getParameter("groupId")).thenReturn("default");
		Mockito.when(httpServletRequest.getParameter("policyId")).thenReturn("default");
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doPost(httpServletRequest, httpServletResponse);
			//Mockito.verify(httpServletResponse).sendError(500, "Policy 'default' not copied to group 'default': java.lang.NullPointerException");
			//Mockito.verify(httpServletResponse).sendError(500, "Policy 'default' not copied to group 'default': javax.persistence.PersistenceException: Group policy is being added to does not exist with id default");
			
		}catch (Exception e){
			fail();
		}
	}
	//why is this test trying to send no pdp id and expecting a 200 response?
	/*
	public void testDoPost(){
		final ByteArrayOutputStream os = new ByteArrayOutputStream ();
		ByteArrayOutputStream multiPartResponse = new ByteArrayOutputStream();
		Mockito.when(httpServletRequest.getHeader("X-XACML-PDP-JMX-PORT")).thenReturn("0");
		
		try{
			multiPartResponse.writeTo(os);
			final ByteArrayInputStream is = new ByteArrayInputStream (os.toByteArray ());
			Mockito.when(httpServletRequest.getInputStream()).thenReturn(new ServletInputStream() {
		        @Override
		        public int read() throws IOException {
		            return is.read();
		        }
		    });
			
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doPost(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
		}catch (Exception e){
			fail();
		}
	}	
	*/
		
	public void testDoPostPDPId(){
		String groupId = "newPDP";
		Mockito.when(httpServletRequest.getParameter("groupId")).thenReturn(groupId);		
		Mockito.when(httpServletRequest.getHeader("X-XACML-PDP-ID")).thenReturn(groupId);
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doPut(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId +"'");
		}catch(Exception e){
			fail();
		}
	}
	
	public void testDoPutInvalidAdminConsoleURL(){
		Mockito.when(httpServletRequest.getParameter("adminConsoleURL")).thenReturn("wwww.adminConsole.com");
		//204
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doPut(httpServletRequest,  httpServletResponse);
			Mockito.verify(httpServletResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
		}catch (Exception e){
			fail();
		}
	}
	
	public void testDoPutWithGroupIdAndUnimplimentedPipId(){
		Mockito.when(httpServletRequest.getParameter("groupId")).thenReturn("default");
		Mockito.when(httpServletRequest.getParameter("pipId")).thenReturn("default");
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doPut(httpServletRequest,  httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
		}catch (Exception e){
			fail();
		}
	}	
	
	public void testDoDeleteNoGroup(){
		Mockito.when(httpServletRequest.getParameter("groupdId")).thenReturn(null);
		
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doDelete(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId");
		}catch (Exception e){
			fail();			
		}
	}
	
	public void testDoDeleteWithDefaultGroup(){
		Mockito.when(httpServletRequest.getParameter("groupId")).thenReturn("default");
		
		try{
			papServlet.init(servletConfig);
  			IntegrityAudit ia = papServlet.getIa();
  			ia.stopAuditThread();			
			papServlet.doDelete(httpServletRequest, httpServletResponse);
			Mockito.verify(httpServletResponse).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"You cannot delete the default group.");
		}catch(Exception e){
			fail();
		}
	}
}
