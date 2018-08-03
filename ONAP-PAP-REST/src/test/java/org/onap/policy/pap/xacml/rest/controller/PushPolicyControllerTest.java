/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ReadListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.springframework.mock.web.MockServletConfig;

import com.att.research.xacml.api.pap.PAPException;


public class PushPolicyControllerTest {

    private static Logger logger = FlexLogger.getLogger(PushPolicyControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private PushPolicyController controller = null;
    private HttpServletResponse response = null;
    private List<String> headers = new ArrayList<>();
    private ServletConfig servletConfig;
    private XACMLPapServlet pap;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(request.getAttributeNames()).thenReturn(Collections.enumeration(headers));

        servletConfig = Mockito.mock(MockServletConfig.class);
        System.setProperty("com.sun.management.jmxremote.port", "9993");
        Mockito.when(servletConfig.getInitParameterNames()).thenReturn(Collections.enumeration(headers));
        Mockito.when(servletConfig.getInitParameter("XACML_PROPERTIES_NAME")).thenReturn("src/test/resources/xacml.pap.properties");
       
        commonClassDao = Mockito.mock(CommonClassDao.class);
        controller = new PushPolicyController();
        controller.setCommonClassDao(commonClassDao);
        logger.info("setUp: exit");
    }

    @Test
    public void testPushPolicy() throws ServletException, PAPException{
        PolicyVersion versionData = new PolicyVersion();
        versionData.setPolicyName("com"+File.separator+"Config_Test");
        versionData.setActiveVersion(1);
        versionData.setHigherVersion(1);
        List<Object> data = new ArrayList<>();
        data.add(versionData);
        when(commonClassDao.getDataById(PolicyVersion.class, "policyName", "com"+File.separator+"Config_Test")).thenReturn(data);
        pap = new XACMLPapServlet();
        pap.init(servletConfig);
        callPushPolicy();
        when(commonClassDao.getDataById(PolicyVersion.class, "policyName", "com"+File.separator+"Config_Test")).thenReturn(null);
        callPushPolicy();
    }

    public void callPushPolicy(){
        jsonString = "{\"policyScope\":\"com\",\"filePrefix\":\"Config_\",\"policyName\":\"Test\",\"pdpGroup\":\"default\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            char[] charBuffer = new char[8 * 1024];
            StringBuilder builder = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = br.read(charBuffer, 0, charBuffer.length)) != -1) {
                builder.append(charBuffer, 0, numCharsRead);
            }
            when(request.getInputStream()).thenReturn(getInputStream(builder.toString().getBytes(StandardCharsets.UTF_8)));
            controller.pushPolicy(request, response);
            assertTrue(response != null);
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    public ServletInputStream getInputStream(byte[] body) throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body); 
        ServletInputStream servletInputStream = new ServletInputStream() { 
            public int read() throws IOException { 
                return byteArrayInputStream.read(); 
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        }; 
        return servletInputStream; 
    } 

     @After
     public void destroy(){
         if(pap!=null)
             pap.destroy();
     }
}
