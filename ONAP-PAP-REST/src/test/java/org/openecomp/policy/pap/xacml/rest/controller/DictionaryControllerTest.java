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

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.Attribute;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;

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
	private String configBodyString = null;
	private HttpServletRequest request = null;
	private DictionaryController controller = null;
	private BufferedReader br = null;

	@Before
	public void setUp() throws Exception {
		logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
	    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
   
        List<Object> microServiceModelsData = new ArrayList<Object>();
        MicroServiceModels testData = new MicroServiceModels();
        testData.setVersion("1707.4.1.2-Junit");        

        //--- mock the getDataByColumn() call
        List<String>  microList = new ArrayList<String>();
        microList.add("123");
        List<Object>  listId = new ArrayList<Object>();
        when(commonClassDao.getDataByColumn(Attribute.class, "xacmlId")).thenReturn(microList);
        PolicyEditorScopes editorScope = new PolicyEditorScopes();
        doNothing().when(commonClassDao).save(editorScope);
        doNothing().when(commonClassDao).update(editorScope);
        
        when(commonClassDao.getData(Attribute.class)).thenReturn(listId);
        
		jsonString = "{\"attributeDictionaryDatas\": {\"error\": \"\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
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

		request = mock(HttpServletRequest.class);        
        BufferedReader br = new BufferedReader(new StringReader(jsonString));
        //--- mock the getReader() call
        when(request.getReader()).thenReturn(br);   
        
        controller = new DictionaryController(commonClassDao);
        
        logger.info("setUp: exit");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAttributeDictionaryEntityDatabyAttributeName() {
		logger.info("testGetAttributeDictionaryEntityDatabyAttributeName: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();

		controller.getAttributeDictionaryEntityDatabyAttributeName(request, response);
		
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

		controller.getAttributeDictionaryEntityData(request, response);
		
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
		    // mock the getReader() call
			jsonString = "{\"attributeDictionaryData\": {\"userDataTypeValues\": [{\"attributeValues\": \"Values1\"}, {\"attributeValues\": \"Values2\"}],	\"datatypeBean\": {\"type\": \"C\"},\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"userDataTypeValues\": [\"user-type\"],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"some\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"}, \"userid\":\"smetest\", \"userDataTypeValues\":[\"type-one\"]}";
			
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
			controller.removeAttributeDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("attributeDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveAttributeDictionary: exit");
	}

	@Test
	public void testGetEcompNameDictionaryByNameEntityData() {
		logger.info("testGetEcompNameDictionaryByNameEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();

		controller.getEcompNameDictionaryByNameEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("ecompNameDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetEcompNameDictionaryByNameEntityData: exit");
	}

	@Test
	public void testGetEcompNameDictionaryEntityData() {
		logger.info("testGetEcompNameDictionaryEntityData: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();

		controller.getEcompNameDictionaryEntityData(request, response);
		
		try {
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("ecompNameDictionaryDatas"));
			logger.info("response.getContentAsString(): " + response.getContentAsString());
		} catch (UnsupportedEncodingException e) {
			fail("Exception: " + e);
		}
		
		logger.info("testGetEcompNameDictionaryEntityData: exit");
	}

	@Test
	public void testSaveEcompDictionary() {
		
		logger.info("testSaveEcompDictionary: Entering");

		MockHttpServletResponse response =  new MockHttpServletResponse();
	    request = mock(HttpServletRequest.class);  
        
		try {
		    // mock the getReader() call
			jsonString = "{\"ecompNameDictionaryData\": {\"userDataTypeValues\": [{\"attributeValues\": \"Values1\"}, {\"attributeValues\": \"Values2\"}],	\"datatypeBean\": {\"type\": \"C\"},\"model\": {\"name\": \"testingdata\", "
					+ " \"subScopename\": \"\",\"userDataTypeValues\": [\"user-type\"],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
					+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
					+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
					+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
					+ "	\"policyDescription\": \"testing input\", \"ecompName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
					+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
					+ "	\"policyJSON\": {\"some\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"}, \"userid\":\"smetest\", \"userDataTypeValues\":[\"type-one\"]}";
			
			BufferedReader br = new BufferedReader(new StringReader(jsonString));
			when(request.getReader()).thenReturn(br); 		    
			controller.saveEcompDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("ecompNameDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testSaveEcompDictionary: exit");		
	}

	@Test
	public void testRemoveEcompDictionary() {
		logger.info("testRemoveEcompDictionary: Entering");

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
			controller.removeEcompDictionary(request, response);
			logger.info("response.getContentAsString(): " + response.getContentAsString());
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("ecompNameDictionaryDatas"));

		} catch (Exception e) {
			fail("Exception: " + e);
		}
		
		logger.info("testRemoveEcompDictionary: exit");
	}

}
