/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.onap.policy.pdp.rest.restauth.AuthenticationService;

public class AuthenticationServiceTest {
    private final String testCred = "python:test";
    private final String testCredEncoded = new String(Base64.getEncoder().encode(testCred.getBytes()));
    private final String basicCred = "Basic " + testCredEncoded;

    @Test
    public void testAuth() throws UnsupportedEncodingException {
        String systemKey = "xacml.properties";

        // Set the system property temporarily
        String oldProperty = System.getProperty(systemKey);
        System.setProperty(systemKey, "xacml.pdp.properties");
        ServletRequest request = new ServletRequest() {

            @Override
            public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {}

            @Override
            public void setAttribute(String arg0, Object arg1) {}

            @Override
            public void removeAttribute(String arg0) {}

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String arg0) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return null;
            }

            @Override
            public String getRealPath(String arg0) {
                return null;
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String[] getParameterValues(String arg0) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getParameter(String arg0) {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public Object getAttribute(String arg0) {
                return null;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }
        };

        assertTrue(AuthenticationService.checkPermissions(null, basicCred, "getConfig", "DEVL", request));

        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(systemKey, oldProperty);
        } else {
            System.clearProperty(systemKey);
        }
    }
}
