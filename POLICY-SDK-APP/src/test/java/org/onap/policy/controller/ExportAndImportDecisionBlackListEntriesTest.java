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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ExportAndImportDecisionBlackListEntriesTest {

    private HttpServletRequest request;
    private MockHttpServletResponse response;
    String jsonString;
    
    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);       
        response =  new MockHttpServletResponse();
    }
    
    @Test
    public void testExportBlackList() throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        jsonString = IOUtils.toString(classLoader.getResourceAsStream("DecisionPolicyData.txt"));
        try(BufferedReader reader = new BufferedReader(new StringReader(jsonString))){
            Mockito.when(request.getReader()).thenReturn(reader);
            ExportAndImportDecisionBlackListEntries controller = new ExportAndImportDecisionBlackListEntries();
            controller.exportBlackList(request, response);
            assertTrue("".equals(response.getContentAsString()));
        }catch(Exception e){
            fail("Not expecting Exception while Exporting BlackListEntries.");
        }
    }
    
    @Test
    public void testImportBlackList() throws Exception{
        MockHttpServletRequest request =  new MockHttpServletRequest();
        ExportAndImportDecisionBlackListEntries controller = new ExportAndImportDecisionBlackListEntries();
        File file = new File("src/test/resources/BlackList.xls");
        try(FileInputStream targetStream = new FileInputStream(file)){
            ServletInputStream inputStream = getInputStream(getBytes(targetStream));
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            Mockito.when(request.getInputStream()).thenReturn(inputStream);
            String boundary = "===" + System.currentTimeMillis() + "===";
            request.addHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
            request.addHeader("name", "BlackList.xls");
            controller.importBlackListFile(request, response);
            assertTrue(response.getContentAsString().contains("data"));
        }catch(Exception e){
            fail("Not expecting Exception while importing BlackListEntries.");
        }
    }
    
    public static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1)
            bos.write(buf, 0, len);
        buf = bos.toByteArray();
        return buf;
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
}
