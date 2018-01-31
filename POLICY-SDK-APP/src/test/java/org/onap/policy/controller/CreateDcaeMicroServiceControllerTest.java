/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.controller;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

/**
 * The class <code>CreateDcaeMicroServiceControllerTest</code> contains tests
 * for the class {@link <code>CreateDcaeMicroServiceController</code>}*
 *
 * All JUnits are designed to run in the local development environment
 * where they have write privileges and can execute time-sensitive
 * tasks.
 *
 *
 *
 */

public class CreateDcaeMicroServiceControllerTest {

	private static Logger logger = FlexLogger.getLogger(CreateDcaeMicroServiceControllerTest.class);
	private static CommonClassDao commonClassDao;
	private String jsonString = null;
	private String configBodyString = null;
	private HttpServletRequest request = null;

	@Before
	public void setUp() throws Exception {

		logger.info("setUp: Entering");
        commonClassDao = mock(CommonClassDao.class);
        List<Object> microServiceModelsData = new ArrayList<Object>();
        MicroServiceModels testData = new MicroServiceModels();
        testData.setVersion("OpenOnap-Junit");
        microServiceModelsData.add(testData);

        // mock the getDataById() call
        when(commonClassDao.getDataById(MicroServiceModels.class, "modelName", "test")).thenReturn(microServiceModelsData);

		jsonString = "{\"policyData\": {\"error\": \"\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
				+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
				+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
				+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
				+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"Micro Service\",\"policyName\": \"may1501\", "
				+ "	\"policyDescription\": \"testing input\", \"onapName\": \"RaviTest\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
				+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
				+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";

		configBodyString = "{\"service\":\"SniroPolicyEntityTest\",\"policyName\":\"someone\",\"description\":\"test\",\"templateVersion\":\"1607\",\"version\":\"HD\","
				+ "\"priority\":\"2\",\"content\":{\"lastPolled\":\"1\",\"boolen-test\":\"true\",\"created\":\"test\",\"retiredDate\":\"test\",\"scope\":\"SNIRO_PLACEMENT_VDHV\","
				+ "\"name\":\"test\",\"lastModified\":\"test\",\"state\":\"CREATED\",\"type\":\"CONFIG\",\"intent\":\"test\",\"target\":\"SNIRO\"}}";

		request = mock(HttpServletRequest.class);
        BufferedReader br = new BufferedReader(new StringReader(jsonString));
        // mock the getReader() call
        when(request.getReader()).thenReturn(br);

        logger.info("setUp: exit");
	}


	/**
	 * Run the PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter,
	 * JsonNode) method test
	 */

	@Test
	public void testSetDataToPolicyRestAdapter() {

		logger.debug("testSetDataToPolicyRestAdapter: enter");

		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
		CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);

		JsonNode root = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		PolicyRestAdapter policyData = null;
		try {
			root = JsonLoader.fromString(jsonString);
			policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);
		} catch (Exception e) {
			logger.error("testSetDataToPolicyRestAdapter", e);
		}

		PolicyRestAdapter result = controller.setDataToPolicyRestAdapter(policyData,	root);
		assertTrue(result != null && result.getJsonBody() != null && !result.getJsonBody().isEmpty());

		logger.debug("result.getJsonBody() : " + result.getJsonBody());
		logger.debug("testSetDataToPolicyRestAdapter: exit");
	}

	/**
	 * Run the void stringBetweenDots(String, String) method test
	 */

	 @Test
	public void testStringBetweenDots() {

		logger.debug("testStringBetweenDots: enter");

		//expect: uniqueKeys should contain a string value
		CreateDcaeMicroServiceController controllerA = new CreateDcaeMicroServiceController();
		String str = "testing\\.byCorrectWay\\.OfDATA";
		assertEquals(1, controllerA.stringBetweenDots(str));

		//expect: uniqueKeys should not contain a string value
		str = "testing\byWrongtWay.\\OfDATA";
		CreateDcaeMicroServiceController controllerB = new CreateDcaeMicroServiceController();
	    assertEquals(0, controllerB.stringBetweenDots(str));

		logger.debug("testStringBetweenDots: exit");
	}

	/**
	 * Run the Map<String,String> load(String) method test
	 */

	@Test
	public void testLoad() {

		logger.debug("testLoad: enter");

		boolean isLocalTesting = true;
		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
		String fileName = null;
		Map<String,String> result = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			fileName = new File(classLoader.getResource("policy_tosca_tca_v1707.yml").getFile()).getAbsolutePath();
		} catch (Exception e1) {
			logger.error("Exception Occured while loading file"+e1);
		}
		if(isLocalTesting){
			try {
				result = controller.load(fileName);
			} catch (IOException e) {
				logger.error("testLoad", e);
				result = null;
			}

			assertTrue(result != null && !result.isEmpty());
			logger.debug("result : " + result);
		}

		logger.debug("testLoad: exit");
	}

	/**
	 * Run the void parseTosca(String) method test
	 */

	@Test
	public void testParseTosca() {

		logger.debug("testParseTosca: enter");
		boolean isLocalTesting = true;
		String fileName = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			fileName = new File(classLoader.getResource("policy_tosca_tca_v1707.yml").getFile()).getAbsolutePath();
		} catch (Exception e1) {
			logger.error("Exception Occured while loading file"+e1);
		}

		CreateDcaeMicroServiceController contoller = new CreateDcaeMicroServiceController();
        if(isLocalTesting){
			try {
			    contoller.parseTosca(fileName);
			}catch (Exception e) {
				fail("parseTosca caused error: " + e);
			}
        }
		logger.debug("testParseTosca: exit");
	}

	/**
	 * Run the ModelAndView getDCAEMSTemplateData(HttpServletRequest,
	 * HttpServletResponse) method test
	 */

	 @Test
	public void testGetDCAEMSTemplateData() {

		logger.debug("testGetDCAEMSTemplateData: enter");

		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
		MockHttpServletResponse response =  new MockHttpServletResponse();
		String msModelJson = "{\"policyData\":\"DkatPolicyBody\"}";
		try {

	        CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);

	        BufferedReader br = new BufferedReader(new StringReader(msModelJson));
	        // mock the getReader() call
	        when(request.getReader()).thenReturn(br);

	        List<Object> microServiceModelsData = new ArrayList<Object>();
	        MicroServiceModels testData = new MicroServiceModels();
	        testData.setVersion("1707.4.1.2-Junit");
	        microServiceModelsData.add(testData);
	        // mock the getDataById() call with the same MS model name
	        when(commonClassDao.getDataById(MicroServiceModels.class, "modelName", "DkatPolicyBody")).thenReturn(microServiceModelsData);

			controller.getDCAEMSTemplateData(request, response);

			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("dcaeModelData"));

			logger.debug("response: "  + response.getContentAsString());

		} catch (Exception e) {
			logger.error("testGetDCAEMSTemplateData", e);
		}

		logger.debug("testGetDCAEMSTemplateData: exit");
	}

	/**
	 * Run the ModelAndView getModelServiceVersionData(HttpServletRequest,
	 * HttpServletResponse) method test
	 */

	@Test
	public void testGetModelServiceVersionData() {

		logger.debug("testGetModelServiceVersionData: enter");

		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
		MockHttpServletResponse response =  new MockHttpServletResponse();
		String msModelJson = "{\"policyData\":\"DkatPolicyBody\"}";
		try {

	        CreateDcaeMicroServiceController.setCommonClassDao(commonClassDao);

	        BufferedReader br = new BufferedReader(new StringReader(msModelJson));
	        // mock the getReader() call
	        when(request.getReader()).thenReturn(br);

	        List<Object> microServiceModelsData = new ArrayList<Object>();
	        MicroServiceModels testData = new MicroServiceModels();
	        testData.setVersion("1707.4.1.2-Junit");
	        microServiceModelsData.add(testData);

	        // mock the getDataById() call with the same MS model name
	        when(commonClassDao.getDataById(MicroServiceModels.class, "modelName", "DkatPolicyBody")).thenReturn(microServiceModelsData);
			controller.getModelServiceVersionData(request, response);

			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("1707.4.1.2-Junit"));

			logger.debug("response: "  + response.getContentAsString());

		} catch (Exception e) {
			logger.error("testGetModelServiceVersionData", e);
			fail("testGetModelServiceVersionData failed due to: " + e);
		}

		logger.debug("testGetModelServiceVersionData: exit");
	}

	/**
	 * Run the void getDCAEPriorityValuesData(HttpServletRequest,
	 * HttpServletResponse) method test
	 */

	@Test
	public void testGetDCAEPriorityValuesData() {

		logger.debug("testGetDCAEPriorityValuesData: enter");

		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
        try{
		   controller.getDCAEPriorityValuesData(request, response);
		   assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("priorityDatas"));
		   logger.debug("response: "  + response.getContentAsString());
        } catch (Exception e) {
			logger.error("testGetDCAEPriorityValuesData", e);
			fail("testGetDCAEPriorityValuesData failed due to: " + e);
		}

		logger.debug("testGetDCAEPriorityValuesData: exit");
	}

	/**
	 * Run the void prePopulateDCAEMSPolicyData(PolicyRestAdapter,
	 * PolicyEntity) method test
	 */

	@Test
	public void testPrePopulateDCAEMSPolicyData() {

		logger.debug("testPrePopulateDCAEMSPolicyData: enter");

	    CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();

	    // populate an entity object for testing
		PolicyEntity entity = new PolicyEntity();
		ConfigurationDataEntity configData = new ConfigurationDataEntity();
		configData.setConfigBody(configBodyString);
		entity.setConfigurationData(configData);

		JsonNode root = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		PolicyRestAdapter restAdapter = null;

		try {
			root = JsonLoader.fromString(jsonString);
			restAdapter = (PolicyRestAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);
			PolicyType policyType = new PolicyType();
			TargetType target = new TargetType();

			// create guard attribute
			AnyOfType anyOfType = new AnyOfType();
			AllOfType alltype = new AllOfType();
			MatchType matchType = new MatchType();
			// set value
			AttributeValueType attributeValue1 = new AttributeValueType();
			attributeValue1.getContent().add("True");
			matchType.setAttributeValue(attributeValue1);
            // set Id
			AttributeDesignatorType designator = new AttributeDesignatorType();
			designator.setAttributeId("guard");
			matchType.setAttributeDesignator(designator);
			alltype.getMatch().add(matchType);

			// add a dummy MatchType object since while (matchList.size()>1 ...)
			MatchType matchDummy = new MatchType();
			// set value
			AttributeValueType dummyValue = new AttributeValueType();
			dummyValue.getContent().add("dummy");
			matchDummy.setAttributeValue(dummyValue);
            // set Id
			AttributeDesignatorType designatorDummy = new AttributeDesignatorType();
			designatorDummy.setAttributeId("dummyId");
			matchDummy.setAttributeDesignator(designatorDummy);

			alltype.getMatch().add(matchDummy);
			anyOfType.getAllOf().add(alltype);

			target.getAnyOf().add(anyOfType);

			// create RiskType attribute
			AnyOfType anyRiskType = new AnyOfType();
			AllOfType allRiskType = new AllOfType();
			MatchType matchRiskType = new MatchType();
			// set value
			AttributeValueType riskTypeValue = new AttributeValueType();
			riskTypeValue.getContent().add("test");
			matchRiskType.setAttributeValue(riskTypeValue);
            // set Id
			AttributeDesignatorType designatorRiskType = new AttributeDesignatorType();
			designatorRiskType.setAttributeId("RiskType");
			matchRiskType.setAttributeDesignator(designatorRiskType);
			allRiskType.getMatch().add(matchRiskType);

			// add a dummy MatchType object since while (matchList.size()>1 ...)
			MatchType matchDummy1 = new MatchType();
			// set value
			AttributeValueType dummy1Value = new AttributeValueType();
			dummy1Value.getContent().add("dummy");
			matchDummy1.setAttributeValue(dummy1Value);
            // set Id
			AttributeDesignatorType designatorDummy1 = new AttributeDesignatorType();
			designatorDummy1.setAttributeId("dummyId");
			matchDummy1.setAttributeDesignator(designatorDummy1);

			allRiskType.getMatch().add(matchDummy1);

			anyRiskType.getAllOf().add(allRiskType);

			target.getAnyOf().add(anyRiskType);

			// create RiskLevel attribute
			AnyOfType anyRiskLevel = new AnyOfType();
			AllOfType allRiskLevel = new AllOfType();
			MatchType matchRiskLevel = new MatchType();
			// set value
			AttributeValueType riskLevel = new AttributeValueType();
			riskLevel.getContent().add("3");
			matchRiskLevel.setAttributeValue(riskLevel);
            // set Id
			AttributeDesignatorType designatorRiskLevel = new AttributeDesignatorType();
			designatorRiskLevel.setAttributeId("RiskLevel");
			matchRiskLevel.setAttributeDesignator(designatorRiskLevel);
			allRiskLevel.getMatch().add(matchRiskLevel);

			// add a dummy MatchType object since while (matchList.size()>1 ...)
			MatchType matchDummy2 = new MatchType();
			// set value
			AttributeValueType dummy2Value = new AttributeValueType();
			dummy2Value.getContent().add("dummy");
			matchDummy2.setAttributeValue(dummy2Value);
            // set Id
			AttributeDesignatorType designatorDummy2 = new AttributeDesignatorType();
			designatorDummy2.setAttributeId("dummyId");
			matchDummy2.setAttributeDesignator(designatorDummy2);

			allRiskLevel.getMatch().add(matchDummy2);

			anyRiskLevel.getAllOf().add(allRiskLevel);
			target.getAnyOf().add(anyRiskLevel);

			policyType.setTarget(target);

			restAdapter.setPolicyData(policyType);

			controller.prePopulateDCAEMSPolicyData(restAdapter, entity);

			logger.error("restAdapter.getRiskType() : " + restAdapter.getRiskType());
			logger.error("restAdapter.getRiskLevel() : " + restAdapter.getRiskLevel());
			logger.error("restAdapter.getGuard() : " + restAdapter.getGuard());

			assertEquals("True", restAdapter.getGuard());
			assertEquals("3", restAdapter.getRiskLevel());
			assertEquals("test", restAdapter.getRiskType());

		} catch (Exception e) {
			logger.error("testPrePopulateDCAEMSPolicyData", e);
			fail("testPrePopulateDCAEMSPolicyData failed due to: " + e);
		}

		logger.debug("testPrePopulateDCAEMSPolicyData: exit");

	}

	/**
	 * Run the Map<String,String> convert(String, String) method test
	 */

	@Test
	public void testConvert(){
		logger.debug("testConvert: enter");

	    String str = "k1=v1,k2=v2,k3=v3";
		String split = ",";
		Map<String,String> result = CreateDcaeMicroServiceController.convert(str, split);
		assertTrue(result != null && result.size() == 3);

		logger.debug("testConvert: exit");
	}

	/**
	 * Run the Map<String,String> convertMap(Map<String,String>,
	 * Map<String,String>) method test
	 */

	@Test
	public void testConvertMap(){
		logger.debug("testConvertMap: enter");

		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();
		Map<String,String> attributesMap = new HashMap<String, String>();
		Map<String,String> attributesRefMap = new HashMap<String, String>();
		Map<String,String> attributesListRefMap  = controller.getAttributesListRefMap();
		Map<String, LinkedList<String>> arrayTextList = controller.getArrayTextList();
		LinkedList<String> list = new LinkedList<String>();

		attributesMap.put("keyOne", "valueOne");
		attributesMap.put("keyTwo", "valueTwo");
		attributesMap.put("keyThree", "valueThree");

		attributesRefMap.put("key4", "value4");
		attributesRefMap.put("key5", "value5");
		attributesRefMap.put("key6", "value6");

		attributesListRefMap.put("key7", "value7");

		list.add("l1");
		list.add("l2");
		arrayTextList.put("key8", list);

		Map<String,String> result = controller.convertMap(attributesMap, attributesRefMap);

		assertTrue(result != null && result.size() == 8);

		assertTrue(arrayTextList.get("key8").toString().contains("[l1, l2]"));

		logger.debug("testConvertMap: exit");
	}

	/**
	 * Run the void SetMSModelData(HttpServletRequest, HttpServletResponse)
	 * method test
	 */

	//@Ignore
	@Test
	public void testSetMSModelData() {

		logger.debug("testSetMSModelData: enter");

		CreateDcaeMicroServiceController controller = new CreateDcaeMicroServiceController();

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    HttpServletRequest request = createMock(HttpServletRequest.class);
	    expect(request.getContentType()).andReturn("multipart/form-data; boundary=----WebKitFormBoundaryWcRUaIbC8kXgjr3p");
	    expect(request.getMethod()).andReturn("post");
	    expect(request.getHeader("Content-length")).andReturn("7809");

	    expect(request.getContentLength()).andReturn(7809);

	    try {
	    	// value of fileName needs to be matched to your local directory
	    	String fileName = "";
	    	try {
				ClassLoader classLoader = getClass().getClassLoader();
				fileName = new File(classLoader.getResource("schedulerPolicies-v1707.xmi").getFile()).getAbsolutePath();
			} catch (Exception e1) {
				logger.error("Exception Occured while loading file"+e1);
			}
			expect(request.getInputStream()).andReturn(new MockServletInputStream(fileName));
		    expect(request.getCharacterEncoding()).andReturn("UTF-8");
		    expect(request.getContentLength()).andReturn(1024);
		    replay(request);

		} catch (Exception e) {
			logger.error("testSetMSModelData" + e);
			e.printStackTrace();
		}

		//assertTrue(false);

		logger.debug("testSetMSModelData: exit");
	}

	/**
	 *
	 * @ Get File Stream
	 *
	 */
	private class MockServletInputStream extends ServletInputStream {

		InputStream fis = null;
		public MockServletInputStream(String fileName) {
			try {
				fis = new FileInputStream(fileName);
			} catch (Exception genExe) {
				genExe.printStackTrace();
			}
		}
		@Override
		public int read() throws IOException {
			if(fis.available() > 0) {
				return fis.read();
			}
			return 0;
		}

		@Override
		public int read(byte[] bytes, int len, int size) throws IOException {
			if(fis.available() > 0) {
				int length = fis.read(bytes, len, size);
				return length;
			}
			return -1;
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
		public void setReadListener(ReadListener arg0) {

		}
	}

}