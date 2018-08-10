/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;

public class ExportAndImportDecisionBlackListEntriesTest {

    private HttpServletRequest request;
    private MockHttpServletResponse response;
    String jsonString;

    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
    }

    @Test
    public void testExportBlackList() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        jsonString = IOUtils.toString(classLoader.getResourceAsStream("DecisionPolicyData.txt"));
        try (BufferedReader reader = new BufferedReader(new StringReader(jsonString))) {
            Mockito.when(request.getReader()).thenReturn(reader);
            ExportAndImportDecisionBlackListEntries controller = new ExportAndImportDecisionBlackListEntries();
            controller.exportBlackList(request, response);
            assertTrue("".equals(response.getContentAsString()));
        } catch (Exception e) {
            fail("Not expecting Exception while Exporting BlackListEntries.");
        }
    }

    @Test
    public void testImportBlackList() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader("name", "BlackList.xls");
        ExportAndImportDecisionBlackListEntries controller = new ExportAndImportDecisionBlackListEntries();
        try {
            createMultipartFormDataRequest(request);
            controller.importBlackListFile(request, response);
            assertTrue(response.getContentAsString().contains("data"));
        } catch (Exception e) {
            fail("Not expecting Exception while importing BlackListEntries.");
        }
    }

    /**
     * Create Multi part Request for import test case.
     * 
     * @param request mock request to set the BlackList.xls 
     * @throws IOException throws error if anything fails while writing request.
     * 
     */
    public void createMultipartFormDataRequest(MockHttpServletRequest request) throws IOException {
        String resourceName = "BlackList.xls";
        String partName = "BlackList.xls";
        // Load resource being uploaded
        byte[] fileContent = FileCopyUtils
                .copyToByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("BlackList.xls"));
        // Create part & entity from resource
        Part[] parts = new Part[] {new FilePart(partName, new ByteArrayPartSource(resourceName, fileContent))};
        MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, new PostMethod().getParams());
        // Serialize request body
        ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
        multipartRequestEntity.writeRequest(requestContent);
        // Set request body to HTTP servlet request
        request.setContent(requestContent.toByteArray());
        // Set content type to HTTP servlet request (important, includes Mime boundary string)
        request.setContentType(multipartRequestEntity.getContentType());
    }

}
