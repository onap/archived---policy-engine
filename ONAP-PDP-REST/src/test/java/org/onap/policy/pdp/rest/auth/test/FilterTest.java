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

package org.onap.policy.pdp.rest.auth.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.pdp.rest.restAuth.PDPAuthenticationFilter;

import com.att.research.xacml.util.XACMLProperties;
import com.mockrunner.mock.web.MockRequestDispatcher;

public class FilterTest {
	
	private PDPAuthenticationFilter authenticationFilter = new PDPAuthenticationFilter(); 
	private final String VALIDHEADERVALUE = "Basic dGVzdHBkcDphbHBoYTQ1Ng==";
	
	@Before
	public void setUp() throws Exception{
		authenticationFilter.init(null);
		XACMLProperties.reloadProperties();
		System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/pass.xacml.pdp.properties");
		XACMLProperties.getProperties();
	}
	
	@Test
	public void testDoFilterError() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("error");
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	@Test
	public void testDoFilterNotification() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getRequestURI()).thenReturn("notifications");
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@Test
	public void testDoFilterSwagger() throws Exception{
		// create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/swagger");
	    when(httpServletRequest.getRequestDispatcher("/api/swagger")).thenReturn(new MockRequestDispatcher());
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    verify(httpServletRequest).getRequestDispatcher("/api/swagger");
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/api-docs/"); 
	    when(httpServletRequest.getRequestDispatcher("/api/api-docs/")).thenReturn(new MockRequestDispatcher());
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    verify(httpServletRequest).getRequestDispatcher("/api/api-docs/");
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/configuration");
	    when(httpServletRequest.getRequestDispatcher("/api/configuration")).thenReturn(new MockRequestDispatcher());
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    verify(httpServletRequest).getRequestDispatcher("/api/configuration");
	}
	
	@Test
	public void newRequestAuthFailTest() throws Exception{
		// create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/api/getConfig");
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("error");
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	@Test
	public void tokenFailureTest() throws Exception{
		// create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/api/getConfig");
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic test123");
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	@Test
	public void oldRequestAuthPassTest() throws Exception{
		// create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // New request no environment header check 
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/api/getConfig");
	    when(httpServletRequest.getRequestDispatcher("/api/getConfig")).thenReturn(new MockRequestDispatcher());
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn(VALIDHEADERVALUE);
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if authorized
	    verify(httpServletRequest).getRequestDispatcher("/api/getConfig");
	    //
	    // Old Requests Checks
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/getConfig");
	    when(httpServletRequest.getRequestDispatcher("/api//getConfig")).thenReturn(new MockRequestDispatcher());
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn(VALIDHEADERVALUE);
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if authorized
	    verify(httpServletRequest).getRequestDispatcher("/api//getConfig");
	}
	
	@Test
	public void newRequestAuthPassTest() throws Exception{
		// create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
		//
	    // Requests with Valid Environment Header values. 
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/getConfig");
	    when(httpServletRequest.getRequestDispatcher("/api//getConfig")).thenReturn(new MockRequestDispatcher());
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("DEVL");
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn(VALIDHEADERVALUE);
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if authorized
	    verify(httpServletRequest).getRequestDispatcher("/api//getConfig");
	    // New request no environment header check 
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/api/getConfig");
	    when(httpServletRequest.getRequestDispatcher("/api/getConfig")).thenReturn(new MockRequestDispatcher());
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn(VALIDHEADERVALUE);
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if authorized
	    verify(httpServletRequest).getRequestDispatcher("/api/getConfig");
	    //
	    //
	    // Requests with InValid Environment Header
	    //
	    when(httpServletRequest.getRequestURI()).thenReturn("/pdp/getConfig");
	    when(httpServletRequest.getRequestDispatcher("/api//getConfig")).thenReturn(new MockRequestDispatcher());
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("TEST");
	    when(httpServletRequest.getHeader(PDPAuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn(VALIDHEADERVALUE);
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
