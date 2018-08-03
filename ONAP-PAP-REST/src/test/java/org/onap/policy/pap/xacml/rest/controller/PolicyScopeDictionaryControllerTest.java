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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.PolicyScopeClosedLoop;
import org.onap.policy.rest.jpa.PolicyScopeResource;
import org.onap.policy.rest.jpa.PolicyScopeService;
import org.onap.policy.rest.jpa.PolicyScopeType;
import org.onap.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;

public class PolicyScopeDictionaryControllerTest {
    private static Logger logger = FlexLogger.getLogger(PolicyScopeDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private PolicyScopeDictionaryController controller = null;
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

        doNothing().when(commonClassDao).delete(new GroupPolicyScopeList());
        doNothing().when(commonClassDao).save(new GroupPolicyScopeList());

        controller = new PolicyScopeDictionaryController();
        controller.setCommonClassDao(commonClassDao);

        request = Mockito.mock(HttpServletRequest.class);
        response =  new MockHttpServletResponse();
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @Test
    public void testGetGroupPolicyScopeEntityDataByName(){
        when(commonClassDao.getDataByColumn(GroupPolicyScopeList.class, "name")).thenReturn(data);
        controller.getGroupPolicyScopeEntityDataByName(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("groupPolicyScopeListDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetGroupPolicyScopeEntityData(){
        when(commonClassDao.getData(GroupPolicyScopeList.class)).thenReturn(new ArrayList<>());
        controller.getGroupPolicyScopeEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("groupPolicyScopeListDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSClosedLoopEntityDataByName(){
        when(commonClassDao.getDataByColumn(PolicyScopeClosedLoop.class, "name")).thenReturn(data);
        controller.getPSClosedLoopEntityDataByName(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psClosedLoopDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSClosedLoopEntityData(){
        when(commonClassDao.getData(PolicyScopeClosedLoop.class)).thenReturn(new ArrayList<>());
        controller.getPSClosedLoopEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psClosedLoopDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSServiceEntityDataByName(){
        when(commonClassDao.getDataByColumn(PolicyScopeService.class, "name")).thenReturn(data);
        controller.getPSServiceEntityDataByName(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psServiceDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSServiceEntityData(){
        when(commonClassDao.getData(PolicyScopeService.class)).thenReturn(new ArrayList<>());
        controller.getPSServiceEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psServiceDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSTypeEntityDataByName(){
        when(commonClassDao.getDataByColumn(PolicyScopeType.class, "name")).thenReturn(data);
        controller.getPSTypeEntityDataByName(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psTypeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSTypeEntityData(){
        when(commonClassDao.getData(PolicyScopeType.class)).thenReturn(new ArrayList<>());
        controller.getPSTypeEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psTypeDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSResourceEntityDataByName(){
        when(commonClassDao.getDataByColumn(PolicyScopeResource.class, "name")).thenReturn(data);
        controller.getPSResourceEntityDataByName(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psResourceDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testGetPSResourceEntityData(){
        when(commonClassDao.getData(PolicyScopeResource.class)).thenReturn(new ArrayList<>());
        controller.getPSResourceEntityData(response);
        try {
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("psResourceDictionaryDatas"));
        } catch (Exception e) {
            fail();
            logger.error(e.getMessage(),e);
        }
    }

    @Test
    public void testSavePSGroupScopeDictionary(){
        jsonString = "{\"groupPolicyScopeListData\":{\"description\":\"test\",\"groupName\":\"Test\"},\"groupPolicyScopeListData1\":{\"closedloop\":\"SampleClosedLoop\",\"resource\":\"SampleResource\",\"service\":\"SampleService\",\"type\":\"SampleType\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSGroupScopeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("groupPolicyScopeListDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdatePSGroupScopeDictionary(){
        jsonString = "{\"groupPolicyScopeListData\":{\"id\":1,\"description\":\"test\",\"groupName\":\"Test\"},\"groupPolicyScopeListData1\":{\"closedloop\":\"SampleClosedLoop\",\"resource\":\"SampleResource\",\"service\":\"SampleService\",\"type\":\"SampleType\"},\"userid\":\"demo\"}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSGroupScopeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("groupPolicyScopeListDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemovePSGroupScopeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removePSGroupScopeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("groupPolicyScopeListDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSavePSClosedLoopDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psClosedLoopDictionaryData\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSClosedLoopDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psClosedLoopDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdatePSClosedLoopDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psClosedLoopDictionaryData\":{\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSClosedLoopDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psClosedLoopDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemovePSClosedLoopDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removePSClosedLoopDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psClosedLoopDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSavePSServiceDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psServiceDictionaryData\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSServiceDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psServiceDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdatePSServiceDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psServiceDictionaryData\":{\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSServiceDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psServiceDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemovePSServiceDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removePSServiceDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psServiceDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSavePSTypeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psTypeDictionaryData\":{\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSTypeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psTypeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdatePSTypeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psTypeDictionaryData\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSTypeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psTypeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemovePSTypeDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removePSTypeDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psTypeDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testSavePSResourceDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psResourceDictionaryData\":{\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSResourceDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psResourceDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testUpdatePSResourceDictionary(){
        jsonString = "{\"userid\":\"demo\",\"psResourceDictionaryData\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.savePSResourceDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psResourceDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }

    @Test
    public void testRemovePSResourceDictionary(){
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
            when(request.getReader()).thenReturn(br);
            controller.removePSResourceDictionary(request, response);
            assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("psResourceDictionaryDatas"));
        }catch(Exception e){
            logger.error("Exception"+ e);
        }
    }
}
