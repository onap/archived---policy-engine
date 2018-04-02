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
import java.util.List;

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
import org.onap.policy.rest.jpa.OptimizationModels;
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
 * The class <code>CreateOptimizationControllerTest</code> contains tests
 * for the class {@link <code>CreateOptimizationController</code>}*
 *
 * All JUnits are designed to run in the local development environment
 * where they have write privileges and can execute time-sensitive
 * tasks.
 */
public class CreateOptimizationControllerTest {
	
	private static Logger logger = FlexLogger.getLogger(CreateOptimizationControllerTest.class);
	private static CommonClassDao commonClassDao;
	private String jsonString = null;
	private String configBodyString = null;
	private HttpServletRequest request = null;
	
	@Before
	public void setUp() throws Exception {

		logger.info("setUp: Entering");
        commonClassDao = mock(CommonClassDao.class);
        List<Object> optimizationModelsData = new ArrayList<Object>();
        OptimizationModels testData = new OptimizationModels();
        testData.setVersion("OpenOnap-Junit");        
        optimizationModelsData.add(testData);

        // mock the getDataById() call
        when(commonClassDao.getDataById(OptimizationModels.class, "modelName", "test")).thenReturn(optimizationModelsData);
        
		jsonString = "{\"policyData\": {\"error\": \"\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
				+ " \"subScopename\": \"\",\"path\": [],\"type\": \"dir\",\"size\": 0,\"date\": \"2017-04-12T21:26:57.000Z\", "
				+ " \"version\": \"\",\"createdBy\": \"someone\",	\"modifiedBy\": \"someone\",	\"content\": \"\",\"recursive\": false},"
				+ " \"tempModel\": {\"name\": \"testingdata\",\"subScopename\": \"\"	},"
				+ " \"policy\": {\"policyType\": \"Config\",\"configPolicyType\": \"OOF\",\"policyName\": \"testPolicy\", "
				+ "	\"policyDescription\": \"testing input\", \"onapName\": \"test\",\"guard\": \"False\",\"riskType\": \"Risk12345\",\"riskLevel\": \"2\","
				+ "	\"priority\": \"6\",\"serviceType\": \"DkatPolicyBody\",\"version\": \"1707.41.02\",\"ruleGridData\": [	[\"fileId\"]],\"ttlDate\": null}}, "
				+ "	\"policyJSON\": {\"pmTableName\": \"test\",	\"dmdTopic\": \"1\",\"fileId\": \"56\"} }";

		configBodyString = "{\"service\":\"PolicyEntityTest\",\"policyName\":\"someone\",\"description\":\"test\",\"templateVersion\":\"1607\",\"version\":\"HD\","
				+ "\"priority\":\"2\",\"content\":{\"lastPolled\":\"1\",\"boolen-test\":\"true\",\"created\":\"test\",\"retiredDate\":\"test\",\"scope\":\"TEST_PLACEMENT_VDHV\","
				+ "\"name\":\"test\",\"lastModified\":\"test\",\"state\":\"CREATED\",\"type\":\"CONFIG\",\"intent\":\"test\",\"target\":\"TEST\"}}";

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
		
		CreateOptimizationController controller = new CreateOptimizationController();
		CreateOptimizationController.setCommonClassDao(commonClassDao);
	
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
	 * Run the ModelAndView getOptimizationTemplateData(HttpServletRequest,
	 * HttpServletResponse) method test
	 */
	
	 @Test
	public void testGetOptimizationTemplateData() {
		
		logger.debug("testGetOptimizationTemplateData: enter");
		
		CreateOptimizationController controller = new CreateOptimizationController();
		MockHttpServletResponse response =  new MockHttpServletResponse();
		String modelJson = "{\"policyData\":\"testPolicyBody\"}";
		try {	
			
			CreateOptimizationController.setCommonClassDao(commonClassDao);
	        
	        BufferedReader br = new BufferedReader(new StringReader(modelJson));
	        // mock the getReader() call
	        when(request.getReader()).thenReturn(br); 
	        
	        List<Object> optimizationModelsData = new ArrayList<Object>();
	        OptimizationModels testData = new OptimizationModels();
	        testData.setVersion("1707.4.1.2-Junit");        
	        optimizationModelsData.add(testData);
	        // mock the getDataById() call with the same MS model name 
	        when(commonClassDao.getDataById(OptimizationModels.class, "modelName", "testPolicyBody")).thenReturn(optimizationModelsData);	
	        
			controller.getOptimizationTemplateData(request, response);
			
			assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("optimizationModelData"));
			
			logger.debug("response: "  + response.getContentAsString());
			
		} catch (Exception e) {
			logger.error("testGetOptimizationTemplateData", e);
		}		
	
		logger.debug("testGetOptimizationTemplateData: exit");
	}

	/**
	 * Run the ModelAndView getModelServiceVersionData(HttpServletRequest,
	 * HttpServletResponse) method test
	 */
	
	@Test
	public void testGetModelServiceVersionData() {
		
		logger.debug("testGetModelServiceVersionData: enter");
		
		CreateOptimizationController controller = new CreateOptimizationController();
		MockHttpServletResponse response =  new MockHttpServletResponse();
		String modelJson = "{\"policyData\":\"TestPolicyBody\"}";
		try {
			
			CreateOptimizationController.setCommonClassDao(commonClassDao);
	        
	        BufferedReader br = new BufferedReader(new StringReader(modelJson));
	        // mock the getReader() call
	        when(request.getReader()).thenReturn(br);   
	        
	        List<Object> optimizationModelsData = new ArrayList<Object>();
	        OptimizationModels testData = new OptimizationModels();
	        testData.setVersion("1707.4.1.2-Junit");        
	        optimizationModelsData.add(testData);

	        // mock the getDataById() call with the same MS model name 
	        when(commonClassDao.getDataById(OptimizationModels.class, "modelName", "TestPolicyBody")).thenReturn(optimizationModelsData);
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
	 * Run the void prePopulateDCAEMSPolicyData(PolicyRestAdapter,
	 * PolicyEntity) method test
	 */
	
	@Test
	public void testPrePopulatePolicyData() {
		
		logger.debug("testPrePopulatePolicyData: enter");
		
		CreateOptimizationController controller = new CreateOptimizationController();
	    
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
			
			controller.prePopulatePolicyData(restAdapter, entity);
			
			logger.error("restAdapter.getRiskType() : " + restAdapter.getRiskType());
			logger.error("restAdapter.getRiskLevel() : " + restAdapter.getRiskLevel());
			logger.error("restAdapter.getGuard() : " + restAdapter.getGuard());
			
			assertEquals("True", restAdapter.getGuard());
			assertEquals("3", restAdapter.getRiskLevel());
			assertEquals("test", restAdapter.getRiskType());
			
		} catch (Exception e) {
			logger.error("testPrePopulatePolicyData", e);
			fail("testPrePopulatePolicyData failed due to: " + e);
		} 
		
		logger.debug("testPrePopulatePolicyData: exit");
		
	}
	
	/**
	 * Run the void SetMSModelData(HttpServletRequest, HttpServletResponse)
	 * method test
	 */
	
	@Test
	public void testSetModelData() {		
		
		logger.debug("testSetModelData: enter");
		
		CreateOptimizationController controller = new CreateOptimizationController();
	    HttpServletRequest request = createMock(HttpServletRequest.class);
		MockHttpServletResponse response =  new MockHttpServletResponse();
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
		    controller.setModelData(request, response);

		} catch (Exception e) {
			logger.error("testSetModelData" + e);
			e.printStackTrace();
		}
	    		
	    
		logger.debug("testSetModelData: exit");
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