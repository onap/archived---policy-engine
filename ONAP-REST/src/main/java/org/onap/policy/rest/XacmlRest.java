/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

import com.att.research.xacml.util.XACMLProperties;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;

/**
 * This static class is used by both the PDP and PAP servlet's. It contains some common static functions and objects
 * used by both the servlet's.
 *
 *
 */
public class XacmlRest {
    private static final Log logger = LogFactory.getLog(XacmlRest.class);
    private static Properties restProperties = new Properties();

    private XacmlRest() {
        // Empty constructor
    }

    /**
     * This must be called during servlet initialization. It sets up the xacml.?.properties file as a system property.
     * If the System property is already set, then it does not do anything. This allows the developer to specify their
     * own xacml.properties file to be used. They can 1) modify the default properties that comes with the project, or
     * 2) change the WebInitParam annotation, or 3) specify an alternative path in the web.xml, or 4) set the Java
     * System property to point to their xacml.properties file.
     *
     * <p>The recommended way of overriding the default xacml.properties file is using a Java System property:
     * -Dxacml.properties=/opt/app/xacml/etc/xacml.admin.properties
     *
     * <p>This way one does not change any actual code or files in the project and can leave the defaults alone.
     *
     * @param config - The servlet config file passed from the javax servlet init() function
     */
    public static void xacmlInit(ServletConfig config) {
        //
        // Get the XACML Properties File parameter first
        //
        String propFile = config.getInitParameter("XACML_PROPERTIES_NAME");
        if (propFile != null) {
            //
            // Look for system override
            //
            String xacmlPropertiesName = System.getProperty(XACMLProperties.XACML_PROPERTIES_NAME);
            logger.info("\n\n" + xacmlPropertiesName + "\n" + XACMLProperties.XACML_PROPERTIES_NAME);
            if (xacmlPropertiesName == null) {
                //
                // Set it to our servlet default
                //
                if (logger.isDebugEnabled()) {
                    logger.debug("Using Servlet Config Property for XACML_PROPERTIES_NAME:" + propFile);
                }
                System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, propFile);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Using System Property for XACML_PROPERTIES_NAME:" + xacmlPropertiesName);
                }
            }
        }
        //
        // Setup the remaining properties
        //
        Enumeration<String> params = config.getInitParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            if (!"XACML_PROPERTIES_NAME".equals(param)) {
                String value = config.getInitParameter(param);
                PolicyLogger.info(param + "=" + config.getInitParameter(param));
                restProperties.setProperty(param, value);
            }
        }
    }

    /**
     * Reset's the XACMLProperties internal properties object so we start in a fresh environment. Then adds back in our
     * Servlet init properties that were passed in the javax Servlet init() call.
     *
     * <p>This function is primarily used when a new configuration is passed in and the PDP servlet needs to load a new
     * PDP engine instance.
     *
     * @param pipProperties - PIP configuration properties
     * @param policyProperties - Policy configuration properties
     */
    public static void loadXacmlProperties(Properties policyProperties, Properties pipProperties) {
        try {
            //
            // Start fresh
            //
            XACMLProperties.reloadProperties();
            //
            // Now load our init properties
            //
            XACMLProperties.getProperties().putAll(XacmlRest.restProperties);
            //
            // Load our policy properties
            //
            if (policyProperties != null) {
                XACMLProperties.getProperties().putAll(policyProperties);
            }
            //
            // Load our pip config properties
            //
            if (pipProperties != null) {
                XACMLProperties.getProperties().putAll(pipProperties);
            }
        } catch (IOException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e,
                    "Failed to put init properties into Xacml properties");
        }
        //
        // Dump them
        //
        if (logger.isDebugEnabled()) {
            try {
                logger.debug(XACMLProperties.getProperties().toString());
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "Cannot dump properties");
            }
        }
    }

    /**
     * Helper routine to dump the HTTP servlet request being serviced. Primarily for debugging.
     *
     * @param request - Servlet request (from a POST/GET/PUT/etc.)
     */
    public static void dumpRequest(HttpServletRequest request) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        // special-case for receiving heartbeat - don't need to repeatedly output all of the information in multiple
        // lines
        if ("GET".equals(request.getMethod()) && "hb".equals(request.getParameter("type"))) {
            PolicyLogger.debug("GET type=hb : heartbeat received");
            return;
        }

        dumpRequestHeadersAttributesContextPath(request);

        dumpRequestBody(request);
    }

    /**
     * Dump the headers, attributes, and context path of the request.
     *
     * @param request the request to dump
     */
    private static void dumpRequestHeadersAttributesContextPath(HttpServletRequest request) {
        logger.debug(request.getMethod() + ":" + request.getRemoteAddr() + " " + request.getRemoteHost() + " "
                + request.getRemotePort());
        logger.debug(request.getLocalAddr() + " " + request.getLocalName() + " " + request.getLocalPort());
        Enumeration<String> en = request.getHeaderNames();
        logger.debug("Headers:");
        while (en.hasMoreElements()) {
            String element = en.nextElement();
            Enumeration<String> values = request.getHeaders(element);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                logger.debug(element + ":" + value);
            }
        }
        logger.debug("Attributes:");
        en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String element = en.nextElement();
            logger.debug(element + ":" + request.getAttribute(element));
        }
        logger.debug("ContextPath: " + request.getContextPath());
    }


    /**
     * Dump the body of the request.
     *
     * @param request the request to act on
     */
    private static void dumpRequestBody(HttpServletRequest request) {
        if ("PUT".equals(request.getMethod()) || "POST".equals(request.getMethod())) {
            // POST and PUT are allowed to have parameters in the content, but in our usage the parameters are always in
            // the Query string.
            // More importantly, there are cases where the POST and PUT content is NOT parameters (e.g. it might contain
            // a Policy file).
            // Unfortunately the request.getParameterMap method reads the content to see if there are any parameters,
            // and once the content is read it cannot be read again.
            // Thus for PUT and POST we must avoid reading the content here so that the main code can read it.
            logger.debug("Query String:" + request.getQueryString());
            try {
                if (request.getInputStream() == null) {
                    logger.debug("Content: No content inputStream");
                } else {
                    logger.debug("Content available: " + request.getInputStream().available());
                }
            } catch (Exception e) {
                logger.debug("Content: inputStream exception: " + e.getMessage() + ";  (May not be relevant)" + e);
            }
        } else {
            logger.debug("Parameters:");
            Map<String, String[]> params = request.getParameterMap();
            Set<String> keys = params.keySet();
            for (String key : keys) {
                String[] values = params.get(key);
                logger.debug(key + "(" + values.length + "): " + (values.length > 0 ? values[0] : ""));
            }
        }
        logger.debug("Request URL:" + request.getRequestURL());
    }
}
