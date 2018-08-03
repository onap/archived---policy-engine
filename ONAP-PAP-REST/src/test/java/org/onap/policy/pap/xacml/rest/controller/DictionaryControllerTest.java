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


package org.onap.policy.pap.xacml.rest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Attribute;
import org.onap.policy.rest.jpa.Category;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.PolicyEditorScopes;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * The class <code>DictionaryControllerTest</code> contains tests
 * for the class {@link <code>DictionaryController</code>}*
 *
 * All JUnits are designed to run in the local development environment
 * where they have write privileges and can execute time-sensitive
 * tasks.
 */
public class DictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(DictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private DictionaryController controller = null;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);

        MicroServiceModels testData = new MicroServiceModels();
        testData.setVersion("1707.4.1.2-Junit");        

        //--- mock the getDataByColumn() call
        List<String>  microList = new ArrayList<String>();
        microList.add("123");
        List<Object>  listId = new ArrayList<Object>();
        when(commonClassDao.getDataByColumn(Attribute.class, "xacmlId")).thenReturn(microList);
        List<Object> object = new ArrayList<>();
        object.add(new Category());
        when(commonClassDao.getDataById(Category.class, "shortName", "resource")).thenReturn(object);
        PolicyEditorScopes editorScope = new PolicyEditorScopes();
        doNothing().when(commonClassDao).save(editorScope);
        doNothing().when(commonClassDao).update(editorScope);
        
        when(commonClassDao.getData(Attribute.class)).thenReturn(listId);
        
        request = mock(HttpServletRequest.class);
        controller = new DictionaryController(commonClassDao);
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @Test
    public void testGetAttributeDictionaryEntityDatabyAttributeName() {
        logger.info("testGetAttributeDictionaryEntityDatabyAttributeName: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getAttributeDictionaryEntityDatabyAttributeName(response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("attributeDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }

        logger.info("testGetAttributeDictionaryEntityDatabyAttributeName: exit");
    }

    @Test
    public void testGetAttributeDictionaryEntityData() {
        logger.info("testGetAttributeDictionaryEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getAttributeDictionaryEntityData(response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("attributeDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }

        logger.info("testGetAttributeDictionaryEntityData: exit");
    }

    @Test
    public void testSaveAttributeDictionary() {
        logger.info("testSaveAttributeDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        
        try {
            jsonString = "{\"attributeDictionaryData\":{\"datatypeBean\":{\"shortName\":\"string\"},\"description\":\"Qwerty\",\"priority\":\"High\",\"userDataTypeValues\":[{\"$$hashKey\":\"object:641\",\"attributeValues\":\"test\",\"id\":\"choice1\"},{\"$$hashKey\":\"object:646\",\"attributeValues\":\"test\",\"id\":\"choice2\"}],\"xacmlId\":\"Qwerty\"},\"userid\":\"demo\"}";
            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.saveAttributeDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("attributeDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testSaveAttributeDictionary: exit");
    }

    @Test
    public void testRemoveAttributeDictionary() {
        logger.info("testRemoveAttributeDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);

        try {
            jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"xacmlId\":\"Test\"}}";
                BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.removeAttributeDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("attributeDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testRemoveAttributeDictionary: exit");
    }

    @Test
    public void testGetOnapNameDictionaryByNameEntityData() {
        logger.info("testGetOnapNameDictionaryByNameEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getOnapNameDictionaryByNameEntityData(response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("onapNameDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }

        logger.info("testGetOnapNameDictionaryByNameEntityData: exit");
    }

    @Test
    public void testGetOnapNameDictionaryEntityData() {
        logger.info("testGetOnapNameDictionaryEntityData: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();

        controller.getOnapNameDictionaryEntityData(response);

        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("onapNameDictionaryDatas"));
            logger.info("response.getContentAsString(): " + response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail("Exception: " + e);
        }

        logger.info("testGetOnapNameDictionaryEntityData: exit");
    }

    @Test
    public void testSaveOnapDictionary() {

        logger.info("testSaveOnapDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);
        
        try {
            jsonString = "{\"userid\":\"demo\",\"onapNameDictionaryData\":{\"description\":\"test\",\"onapName\":\"Test\"}}";
            BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.saveOnapDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("onapNameDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testSaveOnapDictionary: exit");
    }

    @Test
    public void testRemoveOnapDictionary() {
        logger.info("testRemoveOnapDictionary: Entering");

        MockHttpServletResponse response =  new MockHttpServletResponse();
        request = mock(HttpServletRequest.class);

        try {
            jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"onapName\":\"Test\"}}";
                BufferedReader br = new BufferedReader(new StringReader(jsonString));
            when(request.getReader()).thenReturn(br);
            controller.removeOnapDictionary(request, response);
            logger.info("response.getContentAsString(): " + response.getContentAsString());
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("onapNameDictionaryDatas"));

        } catch (Exception e) {
            fail("Exception: " + e);
        }

        logger.info("testRemoveOnapDictionary: exit");
    }

}
