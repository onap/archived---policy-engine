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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DescriptiveScope;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;

public class DescriptiveDictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(DescriptiveDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private DescriptiveDictionaryController controller = null;
    private MockHttpServletResponse response = null;
    private UserInfo userInfo;
    private List<String>  data;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);

        data = new ArrayList<>();
        data.add("Test");

        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");

        doNothing().when(commonClassDao).delete(new DescriptiveScope());
        doNothing().when(commonClassDao).save(new DescriptiveScope());

        controller = new DescriptiveDictionaryController();
        controller.setCommonClassDao(commonClassDao);

        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    public List<Object> testDescriptiveScope(){
        List<Object>  objectData = new ArrayList<>();

        DescriptiveScope data = new DescriptiveScope();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setScopeName("Test");
        assertTrue("Test".equals(data.getScopeName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setSearch("Test");
        assertTrue("Test".equals(data.getSearch()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate()!= null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!= null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy()!= null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy()!= null);
        objectData.add(data);

        return objectData;
    }

    @Test
    public void testGetDescriptiveDictionaryByNameEntityData(){
        when(commonClassDao.getDataByColumn(DescriptiveScope.class, "descriptiveScopeName")).thenReturn(data);
        controller.getDescriptiveDictionaryByNameEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("descriptiveScopeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetDescriptiveDictionaryEntityData(){
        when(commonClassDao.getData(DescriptiveScope.class)).thenReturn(testDescriptiveScope());
        controller.getDescriptiveDictionaryEntityData(response);
        try {
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("descriptiveScopeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testSaveDescriptiveDictionary(){
        jsonString = "{\"descriptiveScopeDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:257\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"description\":\"test\",\"descriptiveScopeName\":\"Test\",\"search\":\"Test\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveDescriptiveDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("descriptiveScopeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdateDescriptiveDictionary(){
        jsonString = "{\"descriptiveScopeDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:257\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"description\":\"test\",\"descriptiveScopeName\":\"Test\",\"id\":1,\"search\":\"Test\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.saveDescriptiveDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("descriptiveScopeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemoveDescriptiveDictionary(){
        jsonString = "{\"data\":{\"attributes\":[{\"$$hashKey\":\"object:257\",\"id\":\"choice1\",\"number\":\"12\",\"option\":\"test\"}],\"description\":\"test\",\"descriptiveScopeName\":\"Test\",\"id\":1,\"search\":\"Test\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removeDescriptiveDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("descriptiveScopeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }
}