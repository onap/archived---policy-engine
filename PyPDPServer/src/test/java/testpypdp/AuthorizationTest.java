/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package testpypdp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openecomp.policy.pypdp.authorization.AuthenticationFilter;

/*
 * Authentication Filter Testing
 */
public class AuthorizationTest {
	private static final String MASTERCLIENT= "cHl0aG9uOnRlc3Q=";
	/*private static final String CONFIGCLIENT= "Y29uZmlnOmNvbmZpZw==";
	private static final String ACTIONCLIENT= "YWN0aW9uOmFjdGlvbg==";
	private static final String DECIDECLIENT= "ZGVjaWRlOmRlY2lkZQ==";
	private static final String CREATECLIENT= "Y3JlYXRlOmNyZWF0ZQ==";
	private static final String DELETECLIENT= "ZGVsZXRlOmRlbGV0ZQ==";*/
	
	private AuthenticationFilter authenticationFilter = new AuthenticationFilter(); 
	
	@Before
	public void setUp() throws Exception{
		authenticationFilter.init(null);
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
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void testDoFilterNotification() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getRequestURI()).thenReturn("org.openecomp.policy.pypdp.notifications swagger api-docs configuration");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	/*@Test
	public void testDoFilterWrongAuthenticaton() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("error");
	    when(httpServletRequest.getRequestURI()).thenReturn("getConfig");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongClientAuthenticaton() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("getConfig");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn("Error");
	    
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	@Test
	public void testDoFilterWrongClientAuthenticatonCount() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("count");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn("Error");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	/*@Test
	public void testDoFilterWrongGetConfigAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("getConfig");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(ACTIONCLIENT);

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongSendEventAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("sendEvent");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(CONFIGCLIENT);
	    
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongUpdatePolicyAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("updatePolicy");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(ACTIONCLIENT);
	    
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongCreatePolicyAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("createPolicy");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(ACTIONCLIENT);
	    
	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongPushPolicyAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("pushPolicy");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(DELETECLIENT);

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongDeletePolicyAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("deletePolicy");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(DECIDECLIENT);

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	/*@Test
	public void testDoFilterWrongDecidePolicyAuthorization() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("getDecision");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(CREATECLIENT);

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}*/
	
	@Test
	public void testDoFilterAuthorizedError() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("error");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(MASTERCLIENT);

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    // verify if unauthorized
	    verify(httpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void testDoFilterAuthorizedPDPs() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("pdps paps");
	    when(httpServletRequest.getHeader("ClientAuth")).thenReturn(MASTERCLIENT);

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);
	    
	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@Test
	public void testDoFilterDecideAuthorized() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("getDecision");
	    when(httpServletRequest.getHeader(AuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("DEVL");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@Test
	public void testDoFilterDeleteAuthorized() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("deletePolicy");
	    when(httpServletRequest.getHeader(AuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("DEVL");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@Test
	public void testDoFilterEventAuthorized() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("sendEvent");
	    when(httpServletRequest.getHeader(AuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("DEVL");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@Test
	public void testDoFilterCreateAuthorized() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("createPolicy pushPolicy updatePolicy");
	    when(httpServletRequest.getHeader(AuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("DEVL");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@Test
	public void testDoFilterConfigAuthorized() throws IOException, ServletException {
	    // create the objects to be mocked
	    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
	    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
	    FilterChain filterChain = mock(FilterChain.class);
	    // 
	    when(httpServletRequest.getHeader(AuthenticationFilter.AUTHENTICATION_HEADER)).thenReturn("Basic dGVzdHJlc3Q6c2VjVXJl");
	    when(httpServletRequest.getRequestURI()).thenReturn("getConfig");
	    when(httpServletRequest.getHeader(AuthenticationFilter.ENVIRONMENT_HEADER)).thenReturn("DEVL");

	    authenticationFilter.doFilter(httpServletRequest, httpServletResponse,
	            filterChain);

	    verify(filterChain).doFilter(httpServletRequest,httpServletResponse);
	}
	
	@After
	public void tearDown(){
		authenticationFilter.destroy();
	}
}
