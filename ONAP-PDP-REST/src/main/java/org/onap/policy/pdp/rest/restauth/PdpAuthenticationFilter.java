/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017,2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pdp.rest.restauth;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class PdpAuthenticationFilter.
 */
@WebFilter("/*")
public class PdpAuthenticationFilter implements Filter {
    private static final String APISTR = "/api/";
    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String ENVIRONMENT_HEADER = "Environment";
    public static final String CLIENTAUTH_HEADER = "ClientAuth";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String environment = httpServletRequest.getHeader(ENVIRONMENT_HEADER);
        String authHeader = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
        String clientAuthHeader = httpServletRequest.getHeader(CLIENTAUTH_HEADER);
        String path = ((HttpServletRequest) request).getRequestURI();
        String resource = path.substring(path.lastIndexOf('/') + 1);

        boolean authenticationStatus =
                AuthenticationService.checkPermissions(clientAuthHeader, authHeader, resource, environment, request);

        if (authenticationStatus) {
            if (check(path)) {
                path = path.substring(path.substring(1).indexOf('/') + 1);
                if (!path.contains(APISTR)) {
                    request.getRequestDispatcher(APISTR + path).forward(request, response);
                } else {
                    request.getRequestDispatcher(path).forward(request, response);
                }

            } else {
                filter.doFilter(request, response);
            }
        } else if (path.contains("swagger") || path.contains("api-docs") || path.contains("configuration")
                || path.contains("count")) {
            path = path.substring(path.substring(1).indexOf('/') + 2);
            request.getRequestDispatcher(APISTR + path).forward(request, response);
        } else if (path.contains("notifications")) {
            filter.doFilter(request, response);
        } else {
            if (response instanceof HttpServletResponse) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private boolean check(String path) {
        return !(path.endsWith("/pdp/") || path.endsWith("/pdp") || path.endsWith("/test"));
    }

    @Override
    public void destroy() {
        // Do nothing.
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing.
    }

}
