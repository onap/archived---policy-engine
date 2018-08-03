/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.restAuth;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet Filter implementation class PAPAuthenticationFilter
 */
@WebFilter("/*")
public class PAPAuthenticationFilter implements Filter {

    private static final Log logger	= LogFactory.getLog(PAPAuthenticationFilter.class);
    public static final String AUTHENTICATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filter) throws IOException, ServletException {


        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            String authCredentials = null;
            String url = httpServletRequest.getRequestURI();

            logger.info("Request URI: " + url);

            //getting authentication credentials
            authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);

            // Check Authentication credentials
            AuthenticationService authenticationService = new AuthenticationService();
            boolean authenticationStatus = authenticationService.authenticate(authCredentials);

            if (authenticationStatus) {
                //indicates the request comes from Traditional Admin Console or PolicyEngineAPI
                if ("/pap/".equals(url)){
                    logger.info("Request comes from Traditional Admin Console or PolicyEngineAPI");
                    //forward request to the XACMLPAPServlet if authenticated
                    request.getRequestDispatcher("/pap/pap/").forward(request, response);
                }else if (url.startsWith("/pap/onap/") && response instanceof HttpServletResponse){
                    //indicates the request comes from the ONAP Portal onap-sdk-app
                    HttpServletResponse alteredResponse = ((HttpServletResponse)response);
                    addCorsHeader(alteredResponse);
                    logger.info("Request comes from Onap Portal");
                    //Spring dispatcher servlet is at the end of the filter chain at /pap/onap/ path
                    filter.doFilter(request, response);
                }
            } else {
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }

        }
    }

    //method to add CorsHeaders for onap portal rest call
    private void addCorsHeader(HttpServletResponse response) {
        logger.info("Adding Cors Response Headers!!!");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");	
    }

    @Override
    public void destroy() {
        //Empty
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        //Empty
    }
}
