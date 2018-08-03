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
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.springframework.mock.web.MockHttpServletResponse;

public class DictionaryImportControllerTest extends Mockito{

    private static Logger logger = FlexLogger.getLogger(DictionaryImportController.class);

    private static CommonClassDao commonClassDao;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private DictionaryImportController controller = null;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
        doNothing().when(commonClassDao).save(new Object());
        controller = new DictionaryImportController();
        new DictionaryImportController(commonClassDao);
        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
    }

    @Test
    public void testIsValidDictionaryName(){
        DictionaryImportController cotroller = new DictionaryImportController();
        //test invalid name
        assertTrue(!cotroller.isValidDictionaryName("wrong-name"));
        //test valid name
        assertTrue(cotroller.isValidDictionaryName("ActionList"));
    }

    @Test
    public void testImportDictionaryData() throws ServletException, IOException{
        List<String> fileNames = new ArrayList<>();
        fileNames.add("Attribute.csv");
        fileNames.add("ActionPolicyDictionary.csv");
        fileNames.add("OnapName.csv");
        fileNames.add("MSPolicyDictionary.csv");
        fileNames.add("OptimizationPolicyDictionary.csv");
        fileNames.add("ClosedLoopService.csv");
        fileNames.add("ClosedLoopSite.csv");
        fileNames.add("VarbindDictionary.csv");
        fileNames.add("BRMSParamDictionary.csv");
        fileNames.add("BRMSControllerDictionary.csv");
        fileNames.add("BRMSDependencyDictionary.csv");
        fileNames.add("PrefixList.csv");
        fileNames.add("SecurityZone.csv");
        fileNames.add("ServiceList.csv");
        fileNames.add("ServiceGroup.csv");
        fileNames.add("AddressGroup.csv");
        fileNames.add("ProtocolList.csv");
        fileNames.add("TermList.csv");
        fileNames.add("SearchCriteria.csv");
        fileNames.add("VNFType.csv");
        fileNames.add("VSCLAction.csv");
        fileNames.add("PEPOptions.csv");
        fileNames.add("Settings.csv");
        fileNames.add("Zone.csv");
        fileNames.add("ActionList.csv");
        for(int i =0; i < fileNames.size(); i++){
            File file = new File("src/test/resources/dictionaryImport/"+fileNames.get(i));
            try(FileInputStream targetStream = new FileInputStream(file)){
                PushPolicyControllerTest pushController = new PushPolicyControllerTest();
                when(request.getInputStream()).thenReturn(pushController.getInputStream(getBytes(targetStream)));
                when(request.getParameter("userId")).thenReturn("demo");
                when(request.getParameter("dictionaryName")).thenReturn(fileNames.get(i));
                controller.importDictionaryData(request, response);
                assertTrue(HttpServletResponse.SC_OK == response.getStatus());
            } catch (IOException e) {
                fail();
            }
        }
        when(request.getParameter("dictionaryName")).thenReturn("WrongName");
        controller.importDictionaryData(request, response);
        assertTrue(HttpServletResponse.SC_BAD_REQUEST == response.getStatus());

        when(request.getParameter("dictionaryName")).thenReturn("");
        controller.importDictionaryData(request, response);
        assertTrue(HttpServletResponse.SC_BAD_REQUEST == response.getStatus());

        when(request.getInputStream()).thenReturn(null);
        when(request.getParameter("dictionaryName")).thenReturn("Attribute.csv");
        controller.importDictionaryData(request, response);
        assertTrue(HttpServletResponse.SC_INTERNAL_SERVER_ERROR == response.getStatus());
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
}
