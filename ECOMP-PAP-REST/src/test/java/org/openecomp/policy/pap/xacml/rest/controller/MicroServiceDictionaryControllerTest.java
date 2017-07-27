/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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


package org.openecomp.policy.pap.xacml.rest.controller;

import static org.junit.Assert.assertEquals;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.DCAEuuid;
import org.openecomp.policy.rest.jpa.MicroServiceLocation;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * The class <code>MicroServiceDictionaryControllerTest</code> contains tests
 * for the class {@link <code>MicroServiceDictionaryController</code>}*
 *
 * All JUnits are designed to run in the local development environment
 * where they have write privileges and can execute time-sensitive
 * tasks.
 */

public class MicroServiceDictionaryControllerTest {
	
	private static Logger logger = FlexLogger.getLogger(MicroServiceDictionaryControllerTest.class);
	private static CommonClassDao commonClassDao;
	private String jsonString = null;
	private String configBodyString = null;
	private HttpServletRequest request = null;
	private MicroServiceDictionaryController controller = null;
	 BufferedReader br = null;

	@Before
	public void setUp() throws Exception {
		logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("testUserId");
        userInfo.setUserName("John");
        when(commonClassDao.getEntityItem(UserInfo.class, "userLoginId", "testing")).thenReturn(userInfo);
        
        List<String>  listIds = new ArrayList<String>();
        listIds.add("Jack");
        when(commonClassDao.getDataByColumn(DCAEuuid.class, "name")).thenReturn(listIds);
        
        List<String>  microList = new ArrayList<String>();
        microList.add("MC-Model");
        when(commonClassDao.getDataByColumn(MicroServiceLocation.class, "name")).thenReturn(microList);
        
        List<Object>  listId = new ArrayList<Object>();
        listId.add("smith");
        when(commonClassDao.getData(DCAEuuid.class)).thenReturn(listId);
        MicroServiceModels microServiceModels = new MicroServiceModels();
        
        doNothing().when(commonClassDao).delete(microServiceModels);
		
		MicroServiceDictionaryController.setCommonClassDao(commonClassDao);	
		
		controller = new MicroServiceDictionaryController();
       
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        
		jsonString = "{\"microServiceModelsDictionaryData\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
				+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
				+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
				+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
				+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
				+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
				+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
				+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";

		configBodyString = "{\"service\":\"SniroPolicyEntityTest\",\"policyName\":\"someone\",\"description\":\"test\",\"templateVersion\":\"1607\",\"version\":\"HD\","
				+ "\"priority\":\"2\",\"content\":{\"lastPolled\":\"1\",\"boolen-test\":\"true\",\"created\":\"test\",\"retiredDate\":\"test\",\"scope\":\"SNIRO_PLACEMENT_VDHV\","
				+ "\"name\":\"test\",\"lastModified\":\"test\",\"state\":\"CREATED\",\"type\":\"CONFIG\",\"intent\":\"test\",\"target\":\"SNIRO\"}}";
    
        br = new BufferedReader(new StringReader(jsonString));
        //--- mock the getReader() call
        when(request.getReader()).thenReturn(br);   
                
        logger.info("setUp: exit");
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGetUserInfo() {
		
		logger.info("testGetUserInfo: Entering");		

		UserInfo userInfo = controller.getUserInfo("testing");
		logger.info("userInfo.getUserName() : " + userInfo.getUserName());
		
		assertEquals("John", userInfo.getUserName());
	
		logger.info("testGetUserInfo: exit");		
	}

	@Test
	public void testGetDCAEUUIDDictionaryByNameEntityData() {
		
		logger.info("testGetDCAEUUIDDictionaryByNameEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();

		controller.getDCAEUUIDDictionaryByNameEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("dcaeUUIDDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetDCAEUUIDDictionaryByNameEntityData: exit");
	}

	@Test
	public void testGetDCAEUUIDDictionaryEntityData() {
		
		logger.info("testGetDCAEUUIDDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getDCAEUUIDDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("dcaeUUIDDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetDCAEUUIDDictionaryEntityData: exit");
	}

	@Test
	public void testSaveDCAEUUIDDictionary() {
		logger.info("testSaveDCAEUUIDDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"dcaeUUIDDictionaryData\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.saveDCAEUUIDDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("dcaeUUIDDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testSaveDCAEUUIDDictionary: exit");
	}

	@Test
	public void testRemoveDCAEUUIDDictionary() {
		logger.info("testRemoveDCAEUUIDDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.removeMicroServiceConfigNameDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceCongigNameDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveDCAEUUIDDictionary: exit");
	}

	@Test
	public void testGetMicroServiceConfigNameByNameDictionaryEntityData() {
		logger.info("testGetMicroServiceConfigNameByNameDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceConfigNameByNameDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceCongigNameDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceConfigNameByNameDictionaryEntityData: exit");
	}

	@Test
	public void testGetMicroServiceConfigNameDictionaryEntityData() {
		logger.info("testGetMicroServiceConfigNameByNameDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceConfigNameDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceCongigNameDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceConfigNameDictionaryEntityData: exit");
	}

	@Test
	public void testSaveMicroServiceConfigNameDictionary() {
		logger.info("testSaveMicroServiceConfigNameDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"microServiceCongigNameDictionaryData\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.saveMicroServiceConfigNameDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceCongigNameDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testSaveMicroServiceConfigNameDictionary: exit");
	}

	@Test
	public void testRemoveMicroServiceConfigNameDictionary() {
		logger.info("testRemoveMicroServiceConfigNameDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.removeMicroServiceConfigNameDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceCongigNameDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveMicroServiceConfigNameDictionary: exit");
	}

	@Test
	public void testGetMicroServiceLocationByNameDictionaryEntityData() {
		
		logger.info("testGetMicroServiceLocationByNameDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceLocationByNameDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceLocationDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceLocationByNameDictionaryEntityData: exit");
	}

	@Test
	public void testGetMicroServiceLocationDictionaryEntityData() {
		logger.info("testGetMicroServiceLocationDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceLocationDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceLocationDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceLocationDictionaryEntityData: exit");
	}

	@Test
	public void testSaveMicroServiceLocationDictionary() {
		logger.info("testSaveMicroServiceLocationDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"microServiceLocationDictionaryData\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.saveMicroServiceLocationDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceLocationDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testSaveMicroServiceLocationDictionary: exit");
	}

	@Test
	public void testRemoveMicroServiceLocationDictionary() {
		logger.info("testRemoveMicroServiceLocationDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));

			when(request.getReader()).thenReturn(br); 		    
			controller.removeMicroServiceLocationDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceLocationDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveMicroServiceLocationDictionary: exit");
	}

	@Test
	public void testGetMicroServiceAttributeByNameDictionaryEntityData() {
		logger.info("testGetMicroServiceAttributeByNameDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceAttributeByNameDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceAttributeDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceAttributeByNameDictionaryEntityData: exit");
	}

	@Test
	public void testGetMicroServiceAttributeDictionaryEntityData() {
		logger.info("testGetMicroServiceAttributeDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceAttributeDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceAttributeDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceAttributeDictionaryEntityData: exit");
	}

	@Test
	public void testSaveMicroServiceAttributeDictionary() {
		logger.info("testSaveMicroServiceAttributeDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"modelAttributeDictionaryData\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.saveMicroServiceAttributeDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceAttributeDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testSaveMicroServiceAttributeDictionary: exit");
	}

	@Test
	public void testRemoveMicroServiceAttributeDictionary() {
		logger.info("testRemoveMicroServiceAttributeDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.removeMicroServiceAttributeDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceAttributeDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveMicroServiceAttributeDictionary: exit");
	}

	@Test
	public void testGetMicroServiceModelsDictionaryByNameEntityData() {
		logger.info("testGetMicroServiceModelsDictionaryByNameEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		controller.getMicroServiceModelsDictionaryByNameEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceModelsDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceModelsDictionaryByNameEntityData: exit");
	}

	@Test
	public void testGetMicroServiceModelsDictionaryByVersionEntityData() {
		logger.info("testGetMicroServiceModelsDictionaryByVersionEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		String msModelJson = "{\"microServiceModelsDictionaryData\":[\"modelName\"]}";
	        
	    BufferedReader br = new BufferedReader(new StringReader(msModelJson));
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			when(request.getReader()).thenReturn(br); 		    
			controller.getMicroServiceModelsDictionaryByVersionEntityData(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("No model name given"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceModelsDictionaryByVersionEntityData: exit");
	}

	@Test
	public void testGetMicroServiceModelsDictionaryEntityData() {
		logger.info("testGetMicroServiceModelsDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		String msModelJson = "{\"microServiceModelsDictionaryData\":[\"modelName\"]}";
	        
	    BufferedReader br = new BufferedReader(new StringReader(msModelJson));
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			when(request.getReader()).thenReturn(br); 		    
			controller.getMicroServiceModelsDictionaryEntityData(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceModelsDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceModelsDictionaryEntityData: exit");
	}

	@Test
	public void testGetMicroServiceModelsDictionaryEntityDataServiceVersion() {
		logger.info("testGetMicroServiceModelsDictionaryEntityDataServiceVersion: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		String msModelJson = "{\"microServiceModelsDictionaryData\":[\"modelName\"]}";
	        
	    BufferedReader br = new BufferedReader(new StringReader(msModelJson));
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			when(request.getReader()).thenReturn(br); 		    
			controller.getMicroServiceModelsDictionaryEntityDataServiceVersion(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceModelsDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceModelsDictionaryEntityDataServiceVersion: exit");
	}

	@Test
	public void testGetMicroServiceModelsDictionaryClassEntityData() {
		logger.info("testGetMicroServiceModelsDictionaryClassEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
		String msModelJson = "{\"microServiceModelsDictionaryData\":[\"modelName\"]}";
	        
	    BufferedReader br = new BufferedReader(new StringReader(msModelJson));
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			when(request.getReader()).thenReturn(br); 		    
			controller.getMicroServiceModelsDictionaryClassEntityData(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceModelsDictionaryClassDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetMicroServiceModelsDictionaryClassEntityData: exit");
	}

	@Test
	public void testSaveMicroServiceModelsDictionary() {
		logger.info("testSaveMicroServiceModelsDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			when(request.getReader()).thenReturn(br); 		    
			controller.saveMicroServiceModelsDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceModelsDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testSaveMicroServiceModelsDictionary: exit");
	}

	@Test
	public void testRemoveMicroServiceModelsDictionary() {
		logger.info("testRemoveMicroServiceModelsDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);   
	
		try {
		    // mock the getReader() call
			jsonString = "{\"data\": {\"modelName\": \"test\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";
			
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.removeMicroServiceModelsDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("microServiceModelsDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveMicroServiceModelsDictionary: exit");
	}

}