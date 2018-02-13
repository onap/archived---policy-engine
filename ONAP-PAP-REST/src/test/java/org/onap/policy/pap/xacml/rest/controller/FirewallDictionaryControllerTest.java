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
import org.onap.policy.rest.adapter.Term;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionList;
import org.onap.policy.rest.jpa.AddressGroup;
import org.onap.policy.rest.jpa.FWTag;
import org.onap.policy.rest.jpa.FWTagPicker;
import org.onap.policy.rest.jpa.FirewallDictionaryList;
import org.onap.policy.rest.jpa.GroupServiceList;
import org.onap.policy.rest.jpa.PortList;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.ProtocolList;
import org.onap.policy.rest.jpa.SecurityZone;
import org.onap.policy.rest.jpa.ServiceList;
import org.onap.policy.rest.jpa.TermList;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.Zone;
import org.springframework.mock.web.MockHttpServletResponse;

public class FirewallDictionaryControllerTest {

	private static Logger logger = FlexLogger.getLogger(FirewallDictionaryControllerTest.class);
	private static CommonClassDao commonClassDao;
	private String jsonString = null;
	private HttpServletRequest request = null;
	private FirewallDictionaryController controller = null;
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
	
		doNothing().when(commonClassDao).delete(new Term());
		doNothing().when(commonClassDao).save(new Term());
		
		controller = new FirewallDictionaryController();
		FirewallDictionaryController.setCommonClassDao(commonClassDao);
		
		request = Mockito.mock(HttpServletRequest.class);
		response =  new MockHttpServletResponse();  
		logger.info("setUp: exit");
	}
	
	@Test
	public void testGetPrefixListDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(PrefixList.class, "prefixListName")).thenReturn(data);
		controller.getPrefixListDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("prefixListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetPrefixListDictionaryEntityData(){
		when(commonClassDao.getData(PrefixList.class)).thenReturn(new ArrayList<>());
		controller.getPrefixListDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("prefixListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetPortListDictionaryEntityData(){
		when(commonClassDao.getData(PortList.class)).thenReturn(new ArrayList<>());
		controller.getPortListDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("portListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetProtocolListDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(ProtocolList.class, "protocolName")).thenReturn(data);
		controller.getProtocolListDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("protocolListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetProtocolListDictionaryEntityData(){
		when(commonClassDao.getData(ProtocolList.class)).thenReturn(new ArrayList<>());
		controller.getProtocolListDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("protocolListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetAddressGroupDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(AddressGroup.class, "name")).thenReturn(data);
		controller.getAddressGroupDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("addressGroupDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetAddressGroupDictionaryEntityData(){
		when(commonClassDao.getData(AddressGroup.class)).thenReturn(new ArrayList<>());
		controller.getAddressGroupDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("addressGroupDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetActionListDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(ActionList.class, "actionName")).thenReturn(data);
		controller.getActionListDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("actionListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetActionListDictionaryEntityData(){
		when(commonClassDao.getData(ActionList.class)).thenReturn(new ArrayList<>());
		controller.getActionListDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("actionListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetServiceGroupDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(GroupServiceList.class, "name")).thenReturn(data);
		controller.getServiceGroupDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("serviceGroupDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetServiceGroupDictionaryEntityData(){
		when(commonClassDao.getData(GroupServiceList.class)).thenReturn(new ArrayList<>());
		controller.getServiceGroupDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("serviceGroupDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetSecurityZoneDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(SecurityZone.class, "zoneName")).thenReturn(data);
		controller.getSecurityZoneDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("securityZoneDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetSecurityZoneDictionaryEntityData(){
		when(commonClassDao.getData(SecurityZone.class)).thenReturn(new ArrayList<>());
		controller.getSecurityZoneDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("securityZoneDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetServiceListDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(ServiceList.class, "serviceName")).thenReturn(data);
		controller.getServiceListDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("serviceListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetServiceListDictionaryEntityData(){
		when(commonClassDao.getData(ServiceList.class)).thenReturn(new ArrayList<>());
		controller.getServiceListDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("serviceListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetZoneDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(Zone.class, "zoneName")).thenReturn(data);
		controller.getZoneDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("zoneDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetZoneDictionaryEntityData(){
		when(commonClassDao.getData(Zone.class)).thenReturn(new ArrayList<>());
		controller.getZoneDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("zoneDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetTermListDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(TermList.class, "termName")).thenReturn(data);
		controller.getTermListDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("termListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetTermListDictionaryEntityData(){
		when(commonClassDao.getData(TermList.class)).thenReturn(new ArrayList<>());
		controller.getTermListDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("termListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetFWDictListDictionaryEntityDataByName(){
		when(commonClassDao.getDataByColumn(FirewallDictionaryList.class, "parentItemName")).thenReturn(data);
		controller.getFWDictListDictionaryEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("fwDictListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetFWDictionaryListEntityData(){
		when(commonClassDao.getData(FirewallDictionaryList.class)).thenReturn(new ArrayList<>());
		controller.getFWDictionaryListEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("fwDictListDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetTagPickerNameEntityDataByName(){
		when(commonClassDao.getDataByColumn(FWTagPicker.class, "tagPickerName")).thenReturn(data);
		controller.getTagPickerNameEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("fwTagPickerDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetTagPickerDictionaryEntityData(){
		when(commonClassDao.getData(FWTagPicker.class)).thenReturn(new ArrayList<>());
		controller.getTagPickerDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("fwTagPickerDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetTagNameEntityDataByName(){
		when(commonClassDao.getDataByColumn(FWTag.class, "fwTagName")).thenReturn(data);
		controller.getTagNameEntityDataByName(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("fwTagDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testGetTagDictionaryEntityData(){
		when(commonClassDao.getData(FWTag.class)).thenReturn(new ArrayList<>());
		controller.getTagDictionaryEntityData(response);
		try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("fwTagDictionaryDatas"));
		} catch (Exception e) {
			fail();
			logger.error(e.getMessage(),e);
		}
	}
	
	@Test
	public void testSavePrefixListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"prefixListDictionaryData\":{\"description\":\"test\",\"prefixListName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.savePrefixListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("prefixListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdatePrefixListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"prefixListDictionaryData\":{\"id\":1,\"description\":\"test\",\"prefixListName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.savePrefixListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("prefixListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemovePrefixListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"prefixListName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removePrefixListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("prefixListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testValidatePrefixListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"prefixListDictionaryData\":{\"id\":1,\"description\":\"test\",\"prefixListName\":\"Test\",\"prefixListValue\":\"10.10.10\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.validatePrefixListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("result"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSavePortListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"portListDictionaryData\":{\"description\":\"test\",\"portName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.savePortListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("portListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdatePortListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"portListDictionaryData\":{\"id\":1,\"description\":\"test\",\"portName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.savePortListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("portListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemovePortListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"portName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removePortListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("portListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveProtocolListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"protocolListDictionaryData\":{\"description\":\"test\",\"protocolName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveProtocolListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("protocolListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateProtocolListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"protocolListDictionaryData\":{\"id\":1,\"description\":\"test\",\"protocolName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveProtocolListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("protocolListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveProtocolListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"protocolName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeProtocolListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("protocolListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveAddressGroupDictionary(){
		jsonString = "{\"addressGroupDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:409\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"groupName\":\"Test\"},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveAddressGroupDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("addressGroupDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateAddressGroupDictionary(){
		jsonString = "{\"addressGroupDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:409\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"groupName\":\"Test\"},\"userid\":\"demo\",\"id\":1}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveAddressGroupDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("addressGroupDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveAddressGroupDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeAddressGroupDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("addressGroupDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveActionListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"actionListDictionaryData\":{\"description\":\"test\",\"actionName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveActionListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateActionListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"actionListDictionaryData\":{\"id\":1,\"description\":\"test\",\"actionName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveActionListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveActionListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"actionName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeActionListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("actionListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveServiceGroupDictionary(){
		jsonString = "{\"serviceGroupDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:657\",\"id\":\"choice1\",\"option\":\"Test\"}],\"groupName\":\"Test\"},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveServiceGroupDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("serviceGroupDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateServiceGroupDictionary(){
		jsonString = "{\"serviceGroupDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:657\",\"id\":\"choice1\",\"option\":\"Test\"}],\"groupName\":\"Test\"},\"userid\":\"demo\",\"id\":1}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveServiceGroupDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("serviceGroupDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveServiceGroupDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeServiceGroupDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("serviceGroupDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveSecurityZoneDictionary(){
		jsonString = "{\"userid\":\"demo\",\"securityZoneDictionaryData\":{\"description\":\"test\",\"zoneName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveSecurityZoneDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("securityZoneDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateSecurityZoneDictionary(){
		jsonString = "{\"userid\":\"demo\",\"securityZoneDictionaryData\":{\"id\":1,\"description\":\"test\",\"zoneName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveSecurityZoneDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("securityZoneDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveSecurityZoneDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"zoneName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeSecurityZoneDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("securityZoneDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveServiceListDictionary(){
		jsonString = "{\"serviceListDictionaryData\":{\"appProtocols\":[{\"$$hashKey\":\"object:560\",\"id\":\"choice1\",\"option\":\"Test\"}],\"serviceDescription\":\"test\",\"serviceName\":\"Test\",\"servicePorts\":\"1010\",\"transportProtocols\":[{\"$$hashKey\":\"object:555\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveServiceListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("serviceListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateServiceListDictionary(){
		jsonString = "{\"serviceListDictionaryData\":{\"appProtocols\":[{\"$$hashKey\":\"object:560\",\"id\":\"choice1\",\"option\":\"Test\"}],\"serviceDescription\":\"test\",\"id\":1,\"serviceName\":\"Test\",\"servicePorts\":\"1010\",\"transportProtocols\":[{\"$$hashKey\":\"object:555\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveServiceListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("serviceListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveServiceListDictionary(){
		jsonString = "{\"data\":{\"appProtocols\":[{\"$$hashKey\":\"object:560\",\"id\":\"choice1\",\"option\":\"Test\"}],\"serviceDescription\":\"test\",\"id\":1,\"serviceName\":\"Test\",\"servicePorts\":\"1010\",\"transportProtocols\":[{\"$$hashKey\":\"object:555\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeServiceListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("serviceListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveZoneDictionary(){
		jsonString = "{\"userid\":\"demo\",\"zoneDictionaryData\":{\"id\":1,\"zoneValue\":\"test\",\"zoneName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveZoneDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("zoneDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateZoneDictionary(){
		jsonString = "{\"userid\":\"demo\",\"zoneDictionaryData\":{\"id\":1,\"zoneValue\":\"test\",\"zoneName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveZoneDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("zoneDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveZoneDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"zoneValue\":\"test\",\"zoneName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeZoneDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("zoneDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveTermListDictionary(){
		jsonString = "{\"termListDictionaryData\":{\"actionListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationServiceDatas\":[{\"$$hashKey\":\"object:1230\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"fromZoneDatas\":[{\"$$hashKey\":\"object:1245\",\"id\":\"choice1\",\"option\":\"Test\"}],\"sourceListDatas\":[{\"$$hashKey\":\"object:1215\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"sourceServiceDatas\":[{\"$$hashKey\":\"object:1225\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"termDescription\":\"test\",\"termName\":\"Test\",\"toZoneDatas\":[{\"$$hashKey\":\"object:1240\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveTermListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("termListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateTermListDictionary(){
		jsonString = "{\"termListDictionaryData\":{\"id\":1,\"actionListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationServiceDatas\":[{\"$$hashKey\":\"object:1230\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"fromZoneDatas\":[{\"$$hashKey\":\"object:1245\",\"id\":\"choice1\",\"option\":\"Test\"}],\"sourceListDatas\":[{\"$$hashKey\":\"object:1215\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"sourceServiceDatas\":[{\"$$hashKey\":\"object:1225\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"termDescription\":\"test\",\"termName\":\"Test\",\"toZoneDatas\":[{\"$$hashKey\":\"object:1240\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveTermListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("termListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveTermListDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"termDescription\":\"test\",\"termName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeTermListDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("termListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveFWDictionaryList(){
		jsonString = "{\"fwDictListDictionaryData\":{\"alAttributes\":[{\"$$hashKey\":\"object:1379\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"attributes\":[{\"$$hashKey\":\"object:1374\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"parentItemName\":\"Test\"},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveFWDictionaryList(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwDictListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateFWDictionaryList(){
		jsonString = "{\"fwDictListDictionaryData\":{\"id\":1,\"alAttributes\":[{\"$$hashKey\":\"object:1379\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"attributes\":[{\"$$hashKey\":\"object:1374\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"parentItemName\":\"Test\"},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveFWDictionaryList(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwDictListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveFWDictionaryList(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"parentItemName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeFWDictionaryList(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwDictListDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveFirewallTagPickerDictionary(){
		jsonString = "{\"fwTagPickerDictionaryData\":{\"description\":\"test\",\"networkRole\":\"test\",\"tagPickerName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1855\",\"id\":\"choice1\",\"number\":\"test\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveFirewallTagPickerDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwTagPickerDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateFirewallTagPickerDictionary(){
		jsonString = "{\"fwTagPickerDictionaryData\":{\"id\":1,\"description\":\"test\",\"networkRole\":\"test\",\"tagPickerName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1855\",\"id\":\"choice1\",\"number\":\"test\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveFirewallTagPickerDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwTagPickerDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveFirewallTagPickerDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"tagPickerName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeFirewallTagPickerDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwTagPickerDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testSaveFirewallTagDictionary(){
		jsonString = "{\"fwTagDictionaryData\":{\"description\":\"test\",\"fwTagName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1690\",\"id\":\"choice1\",\"tags\":\"test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveFirewallTagDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwTagDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testUpdateFirewallTagDictionary(){
		jsonString = "{\"fwTagDictionaryData\":{\"id\":1,\"description\":\"test\",\"fwTagName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1690\",\"id\":\"choice1\",\"tags\":\"test\"}]},\"userid\":\"demo\"}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.saveFirewallTagDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwTagDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
	
	@Test
	public void testRemoveFirewallTagDictionary(){
		jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"fwTagName\":\"Test\"}}";
		try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
			when(request.getReader()).thenReturn(br);
			controller.removeFirewallTagDictionary(request, response);
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("fwTagDictionaryDatas"));
		}catch(Exception e){
			logger.error("Exception"+ e);
		} 
	}
}
