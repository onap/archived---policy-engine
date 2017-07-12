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


package org.openecomp.policy.pap.xacml.rest.policycontroller;


import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.mockito.Mockito;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * The class <code>PolicyCreationTest</code> contains tests
 * for the class {@link <code>PolicyCreation</code>}*
 *
 * All JUnits are designed to run in the local development environment
 * where they have write privileges and can execute time-sensitive
 * tasks.
 */
public class PolicyCreationTest {
	
	private static Logger logger = FlexLogger.getLogger(PolicyCreationTest.class);
	private static CommonClassDao commonClassDao;
	private String jsonString = null;
	private String configBodyString = null;
	private HttpServletRequest request = null;
	
	@Before
	public void setUp() throws Exception {

		logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);
	    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
   
        List<Object> microServiceModelsData = new ArrayList<Object>();
        MicroServiceModels testData = new MicroServiceModels();
        testData.setVersion("1707.4.1.2-Junit");        

        //--- mock the getDataById() call
        when(commonClassDao.getDataById(MicroServiceModels.class, "modelName", "test")).thenReturn(microServiceModelsData);
        PolicyEditorScopes editorScope = new PolicyEditorScopes();
        doNothing().when(commonClassDao).save(editorScope);
        doNothing().when(commonClassDao).update(editorScope);
        
		jsonString = "{\"policyData\": {\"error\": \"\",	\"inprocess\": false,\"model\": {\"name\": \"testingdata\", "
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
                
        logger.info("setUp: exit");
	}

	@Test
	public void testSavePolicy() {
		
		logger.debug("testSavePolicy: enter");
		
		PolicyCreation controller = new PolicyCreation(commonClassDao);

		JsonNode root = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		PolicyRestAdapter policyData = null;
		MockHttpServletResponse response =  new MockHttpServletResponse();
		
		try {
			root = JsonLoader.fromString(jsonString);
			policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);
			policyData.setDomainDir("src/test/resources/client.properties");
			policyData.setJsonBody(configBodyString);
			controller.savePolicy(policyData, response);
			if(response !=null){
			    logger.info("testSavePolicy: response:" + response.getStatus());
			}
			
			assertEquals(200, response.getStatus());
			
		} catch (Exception e) {
			logger.info("testSavePolicy", e);			
		} 

		logger.info("testSavePolicy: exit");
	}

}
