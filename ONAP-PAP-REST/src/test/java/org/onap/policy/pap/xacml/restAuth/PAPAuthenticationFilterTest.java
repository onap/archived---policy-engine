/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pap.xacml.restAuth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

public class PAPAuthenticationFilterTest {

    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private String oldProperty;
    private String systemKey = "xacml.properties";
    private FilterChain filter;
    private PAPAuthenticationFilter papFilter;

    @Before
    public void setUp(){
        // Set the system property temporarily
        oldProperty = System.getProperty(systemKey);
        System.setProperty(systemKey, "xacml.pap.properties");

        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filter = Mockito.mock(FilterChain.class);
        papFilter = new PAPAuthenticationFilter();
    }

    @Test
    public void testAuth() throws IOException, ServletException {
        PAPAuthenticationFilter filter = new PAPAuthenticationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = null;

        // Negative test the filter
        filter.doFilter(request, response, filterChain);
        assertEquals(response.getStatusCode(), 401);

        // Test base methods
        try {
            filter.destroy();
            filter.init(null);
        }
        catch (Exception ex) {
            fail("Not expecting any exceptions.");
        }
    }

    @Test
    public void testDoFilter() {
        Mockito.when(request.getRequestURI()).thenReturn("/pap/");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic dGVzdHBhcDphbHBoYTEyMw==");
        callDoFilter();
        Mockito.when(request.getRequestURI()).thenReturn("/pap/onap/");
        callDoFilter();
    }

    public void callDoFilter(){
        try {
            papFilter.doFilter(request, response, filter);
        } catch (Exception e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    public void testOnPassingInvalidParamters(){
        Mockito.when(request.getRequestURI()).thenReturn("/pap/");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Basic dGVzdHBhcDphbHBoYTE789==");
        try {
            papFilter.doFilter(request, response, filter);
            assertEquals(0, response.getStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @After
    public void reset(){
        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(systemKey, oldProperty);
        } else {
            System.clearProperty(systemKey);
        }
    }
}
