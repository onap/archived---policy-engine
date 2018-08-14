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
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
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
        byte[] fileContent = FileCopyUtils
                .copyToByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("BlackList.xls"));

        MockMultipartFile file = new MockMultipartFile("BlackList.xls",
                Thread.currentThread().getContextClassLoader().getResourceAsStream("BlackList.xls"));

        MockMultipartHttpServletRequest req = new MockMultipartHttpServletRequest();
        req.setMethod("POST");

        String boundary = "JPnJUN6FOo0qLySf-__r_RY1nQE7QOXXJ_nLK1s";
        req.setContentType("multipart/form-data; boundary=" + boundary);

        String start = "--" + boundary + "\r\n Content-Disposition: form-data; name=\"file\"; filename=\""
                + "BlackList.xls" + "\"\r\n" + "Content-type: " + "application/vnd.ms-excel" + "\r\n\r\n";

        String end = "\r\n--" + boundary + "--";
        req.setContent(ArrayUtils.addAll(start.getBytes(), ArrayUtils.addAll(fileContent, end.getBytes())));
        req.addHeader("name", "BlackList.xls");
        req.addFile(file);
        ExportAndImportDecisionBlackListEntries controller = new ExportAndImportDecisionBlackListEntries();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        controller.importBlackListFile(req, resp);
        assertTrue(resp.getContentAsString().contains("data"));

    }
}
