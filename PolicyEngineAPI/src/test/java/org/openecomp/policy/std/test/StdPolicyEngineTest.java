/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.openecomp.policy.std.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.policy.api.AttributeType;
import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.DecisionRequestParameters;
import org.openecomp.policy.api.DecisionResponse;
import org.openecomp.policy.api.DeletePolicyCondition;
import org.openecomp.policy.api.DeletePolicyParameters;
import org.openecomp.policy.api.DictionaryParameters;
import org.openecomp.policy.api.DictionaryType;
import org.openecomp.policy.api.EventRequestParameters;
import org.openecomp.policy.api.ImportParameters;
import org.openecomp.policy.api.NotificationHandler;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.PolicyChangeResponse;
import org.openecomp.policy.api.PolicyClass;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyConfigType;
import org.openecomp.policy.api.PolicyDecision;
import org.openecomp.policy.api.PolicyParameters;
import org.openecomp.policy.api.PolicyResponse;
import org.openecomp.policy.api.PolicyType;
import org.openecomp.policy.api.PushPolicyParameters;
import org.openecomp.policy.api.ImportParameters.IMPORT_TYPE;
import org.openecomp.policy.std.StdDecisionResponse;
import org.openecomp.policy.std.StdPolicyChangeResponse;
import org.openecomp.policy.std.StdPolicyConfig;
import org.openecomp.policy.std.StdPolicyEngine;
import org.openecomp.policy.std.StdPolicyResponse;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

/**
 * The class <code>StdPolicyEngineTest</code> contains tests for the class <code>{@link StdPolicyEngine}</code>.
 *
 * @generatedBy CodePro at 6/3/16 2:03 PM
 * @version $Revision: 1.0 $
 */
public class StdPolicyEngineTest {
	
	private static final Logger logger = FlexLogger.getLogger(StdPolicyEngine.class);

	private StdPolicyEngine fixture = null;
	private StdPolicyEngine mockEngine = null;
	
	PolicyChangeResponse result = null;
	StdPolicyChangeResponse response = new StdPolicyChangeResponse();
	PolicyParameters policyParameters = new PolicyParameters();
	String json = null;
	
	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		fixture = new StdPolicyEngine("Test/config_pass.properties");
		
		//Mocks
		mockEngine = Mockito.mock(StdPolicyEngine.class);
	}
	
    private static JsonObject buildJSON(String jsonString) {
        JsonObject json = null;;
        if (jsonString != null) {
            StringReader in = null;
             
            in = new StringReader(jsonString);
             
            JsonReader jsonReader = Json.createReader(in);
            json = jsonReader.readObject();        
        } 
        
        return json;
    }
    
	//Reads a File and converts into a String. 
	private static String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    try {
	        while( ( line = reader.readLine() ) != null ) {
	            stringBuilder.append( line );
	            stringBuilder.append( ls );
	        }

	        return stringBuilder.toString();
	    } finally {
	        reader.close();
	    }
	}

	/**
	 * Run the StdPolicyEngine(String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testStdPolicyEngine()
		throws Exception {
		String propertyFilePath = "Test/config_pass.properties";

		StdPolicyEngine result = new StdPolicyEngine(propertyFilePath);

		assertNotNull(result);
	}

	/**
	 * Run the StdPolicyEngine(String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testStdPolicyEngine_2()
		throws Exception {
		String propertyFilePath = "http";

		StdPolicyEngine result = new StdPolicyEngine(propertyFilePath);

		assertNull(result);
	}
*/
	/**
	 * Run the StdPolicyEngine(String,NotificationScheme) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testStdPolicyEngine_3()
		throws Exception {
		String propertyFilePath = "Test/config_pass.properties";
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;

		StdPolicyEngine result = new StdPolicyEngine(propertyFilePath, scheme);

		assertNotNull(result);
	}

	/**
	 * Run the StdPolicyEngine(String,NotificationScheme) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testStdPolicyEngine_4()
		throws Exception {
		String propertyFilePath = "http";
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;

		StdPolicyEngine result = new StdPolicyEngine(propertyFilePath, scheme);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.openecomp.policy.std.StdPolicyEngine
		assertNull(result);
	}*/

	/**
	 * Run the StdPolicyEngine(String,NotificationScheme,NotificationHandler) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testStdPolicyEngine_5()
		throws Exception {
		String propertyFilePath = "Test/config_pass.properties";
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
		NotificationHandler handler = new Handler();

		StdPolicyEngine result = new StdPolicyEngine(propertyFilePath, scheme, handler);

		assertNull(result);
	}*/

	/**
	 * Run the StdPolicyEngine(String,NotificationScheme,NotificationHandler) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testStdPolicyEngine_6()
		throws Exception {
		String propertyFilePath = "http";
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
		NotificationHandler handler = new Handler();

		StdPolicyEngine result = new StdPolicyEngine(propertyFilePath, scheme, handler);

		assertNull(result);
	}
*/
	/**
	 * Run the StdPolicyEngine(List<String>,List<String>,List<String>,List<String>,NotificationScheme,NotificationHandler,String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testStdPolicyEngine_8()
		throws Exception {
		List<String> configURL = new LinkedList();
		List<String> configPapURL = new LinkedList();
		List<String> encodingPAP = new LinkedList();
		List<String> encoding = new LinkedList();
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
		NotificationHandler handler = new Handler();
		String clientAuth = "TEST";

		StdPolicyEngine result = new StdPolicyEngine(configURL, configPapURL, encodingPAP, encoding, scheme, handler, clientAuth);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NoClassDefFoundError: Could not initialize class org.openecomp.policy.std.StdPolicyEngine
		assertNull(result);
	}
*/
	/**
	 * Run the Collection<PolicyConfig> config(ConfigRequestParameters) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testConfig()
		throws Exception {
		String configMessage = "Error in Calling the Configuration URL java.lang.Exception: PE500 - Process Flow Issue: Cannot open a connection to the configURL";
		PolicyConfigStatus configStatus = PolicyConfigStatus.CONFIG_NOT_FOUND;
		String policyName = "JunitTest.Config_testing";
		String policyVersion = "1";
				
		ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
		configRequestParameters.setPolicyName(".*");
		Collection<PolicyConfig> result = fixture.config(configRequestParameters);		
		
		//assertEquals(response, result);
		for(PolicyConfig policyConfig: result){
			assertEquals(policyName, policyConfig.getPolicyName());
			assertEquals(policyVersion, policyConfig.getPolicyVersion());
			assertEquals(configStatus, policyConfig.getPolicyConfigStatus());
			assertEquals(configMessage, policyConfig.getPolicyConfigMessage());
		}
	}
	
	
	/**
	 * Run the Collection<String> listConfig(ConfigRequestParameters) method test.
	 *
	 * @throws Exception
	 *
	 */
	@Test
	public void testListConfig()
		throws Exception {
		
		Collection<String> response = new ArrayList<String>();
		response.add("Policy Name: listConfigTest");

		ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
		configRequestParameters.setPolicyName(".*");
		Collection<String> result = fixture.listConfig(configRequestParameters);

		assertEquals(result, response);
	}

	/**
	 * Run the String copyFile(String,String,StdPAPPolicy,String,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testCopyFile()
		throws Exception {
		String policyId = "test.testing";
		String group = "default";
		URI selectedURI = null;
		StdPDPPolicy policy = new StdPDPPolicy("testing", true, "test", selectedURI, true, "test", "testing", "1");	
		StdPAPPolicy location = new StdPAPPolicy(policy.getLocation());
		String clientScope = "Config";
		UUID requestID = UUID.randomUUID();

		String result = fixture.copyFile(policyId, group, location, clientScope, requestID);

		assertNotNull(result);
	}*/

	/**
	 * Run the String copyPolicy(PDPPolicy,String,String,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testCopyPolicy()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);
		URI selectedURI = null;
		
		StdPDPPolicy policy = new StdPDPPolicy("testing", true, "test", selectedURI, true, "test", "testing", "1");
		String group = "default";
		String policyType = "Base";
		UUID requestID = UUID.randomUUID();

		String result = fixture.copyPolicy(policy, group, policyType, requestID);
		
		assertNotNull(result);
	}
*/
	/**
	 * Run the String createConfigFirewallPolicy(String,JsonObject,String,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateConfigFirewallPolicy()
		throws Exception {
		
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyConfigType(PolicyConfigType.Firewall); 

		String json= "{\"serviceTypeId\": \"/v0/firewall/pan\",\"configName\": \"rule1607\",\"deploymentOption\":{\"deployNow\": false},\"securityZoneId\": \"/v0/firewall/pan\",\"serviceGroups\": [{\"name\": \"1607Group\",\"description\": null,\"members\": [{\"type\": \"REFERENCE\",\"name\": \"SList\"},{\"type\": \"REFERENCE\",\"name\": \"Syslog\"}]}, {\"name\": \"Syslog\",\"description\": \"NA\",\"type\": \"SERVICE\",\"transportProtocol\": \"udp\",\"appProtocol\": null,\"ports\": \"514\"}, {\"name\": \"SList\",\"description\": \"Service List\",\"type\": \"SERVICE\",\"transportProtocol\": \"tcp\",\"appProtocol\": null,\"ports\": \"8080\"}],\"addressGroups\": [{\"name\": \"1607Group\",\"description\": null,\"members\": [{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"},{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"}]},{\"name\": \"PL_CCE3\",\"description\": \"CCE Routers\",\"members\":[{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"}]}],\"firewallRuleList\": [{\"position\": \"1\",\"ruleName\": \"1607Rule\",\"fromZones\": [\"Trusted\"],\"toZones\": [\"Untrusted\"],\"negateSource\": false,\"negateDestination\": false,\"sourceList\": [{\"type\": \"REFERENCE\",\"name\": \"PL_CCE3\"}, {\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"destinationList\": [{\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"sourceServices\": [],\"destServices\": [{\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"action\": \"accept\",\"description\": \"Rule for 1607 templates\",\"enabled\": true,\"log\": true}]}";
		policyParameters.setConfigBody(json);
		policyParameters.setPolicyName("test.testing");

		PolicyChangeResponse result = fixture.createPolicy(policyParameters);

		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}

	/**
	 * Run the String createConfigPolicy(String,String,String,String,Map<String,String>,String,String,String,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateConfigPolicy()
		throws Exception {
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyConfigType(PolicyConfigType.Base);
		policyParameters.setPolicyName("test.junittest");
		policyParameters.setPolicyDescription("testing junit");
		policyParameters.setEcompName("test");
		policyParameters.setConfigName("testname");
        Map<String, String> configAttributes = new HashMap<String, String>(); 
        configAttributes.put("Template", "SampleTemplate");
        configAttributes.put("controller", "default"); 
        configAttributes.put("SamPoll", "30");
        configAttributes.put("value", "abcd"); 
		Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);
		policyParameters.setRequestID(UUID.randomUUID());
		policyParameters.setConfigBodyType(PolicyType.OTHER);
		policyParameters.setConfigBody("test");
		
		PolicyChangeResponse result = fixture.createPolicy(policyParameters);

		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}


	/**
	 * Run the String createUpdateActionPolicy(String,String,Map<String,String>,List<String>,List<String>,List<String>,List<String>,String,String,String,Boolean,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateUpdateActionPolicy_Create()
		throws Exception {
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyClass(PolicyClass.Action); 
		policyParameters.setPolicyName("test.junittest");
		policyParameters.setPolicyDescription("testing");
        Map<String, String> configAttributes = new HashMap<String, String>(); 
        configAttributes.put("Template", "UpdateTemplate");
        configAttributes.put("controller", "default"); 
        configAttributes.put("SamPoll", "30");
        configAttributes.put("value", "abcd"); 
        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);
        policyParameters.setActionPerformer("PDP");
        policyParameters.setActionAttribute("test");

		PolicyChangeResponse result = fixture.createPolicy(policyParameters);

		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}
	
	/**
	 * Run the String createUpdateActionPolicy(String,String,Map<String,String>,List<String>,List<String>,List<String>,List<String>,String,String,String,Boolean,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateUpdateActionPolicy_Update()
		throws Exception {
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyClass(PolicyClass.Action); 
		policyParameters.setPolicyName("test.junittest");
		policyParameters.setPolicyDescription("testing");
        Map<String, String> configAttributes = new HashMap<String, String>(); 
        configAttributes.put("Template", "UpdateTemplate");
        configAttributes.put("controller", "default"); 
        configAttributes.put("SamPoll", "30");
        configAttributes.put("value", "abcd"); 
        Map<AttributeType, Map<String,String>> attributes = new HashMap<AttributeType, Map<String,String>>();
        attributes.put(AttributeType.MATCHING, configAttributes);
        policyParameters.setAttributes(attributes);
        policyParameters.setActionPerformer("PDP");
        policyParameters.setActionAttribute("test");

		PolicyChangeResponse result = fixture.updatePolicy(policyParameters);

		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}


	/**
	 * Run the String createUpdateBRMSParamPolicy(String,String,Map<AttributeType,Map<String,String>>,String,String,Boolean,UUID,Map<AttributeType,Map<String,String>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateUpdateBRMSParamPolicy_Create()
		throws Exception {
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyConfigType(PolicyConfigType.BRMS_PARAM); 
		policyParameters.setPolicyName("test.testing");
		policyParameters.setPolicyDescription("testing");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put("templateName", "Sample"); // This sampleTemplate is the Template name from dictionary. 
        ruleAttributes.put("controller", "default"); // Set Rule to a PDP Controller, default is the controller name.
        ruleAttributes.put("SamPoll", "300"); // Template specific key and value set by us. 
        ruleAttributes.put("value", "abcd"); // Template specific key and value set by us. 
        Map<AttributeType, Map<String, String>> attributes = new HashMap<AttributeType, Map<String, String>>();
        attributes.put(AttributeType.RULE, ruleAttributes);
        policyParameters.setAttributes(attributes);
        
		PolicyChangeResponse result = fixture.createPolicy(policyParameters);
		
		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}
	
	/**
	 * Run the String createUpdateBRMSParamPolicy(String,String,Map<AttributeType,Map<String,String>>,String,String,Boolean,UUID,Map<AttributeType,Map<String,String>>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateUpdateBRMSParamPolicy_Update()
		throws Exception {
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyConfigType(PolicyConfigType.BRMS_PARAM); 
		policyParameters.setPolicyName("test.testing");
		policyParameters.setPolicyDescription("testing");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put("templateName", "Sample"); // This sampleTemplate is the Template name from dictionary. 
        ruleAttributes.put("controller", "default"); // Set Rule to a PDP Controller, default is the controller name.
        ruleAttributes.put("SamPoll", "300"); // Template specific key and value set by us. 
        ruleAttributes.put("value", "abcd"); // Template specific key and value set by us. 
        Map<AttributeType, Map<String, String>> attributes = new HashMap<AttributeType, Map<String, String>>();
        attributes.put(AttributeType.RULE, ruleAttributes);
        policyParameters.setAttributes(attributes);
        
		PolicyChangeResponse result = fixture.updatePolicy(policyParameters);
		
		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}

	/**
	 * Run the String createUpdateBRMSRawPolicy(String,String,Map<AttributeType,Map<String,String>>,String,String,Boolean,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testCreateUpdateBRMSRawPolicy_Create()
		throws Exception {
		response.setResponseMessage("success");
		PolicyParameters policyParameters = new PolicyParameters();
		policyParameters.setPolicyConfigType(PolicyConfigType.BRMS_PARAM); 
		policyParameters.setPolicyName("test.testing");
		policyParameters.setPolicyDescription("testing");
        Map<String, String> attrib= new HashMap<String,String>();
        attrib.put("cpu","80");
        attrib.put("memory", "50");
        Map<AttributeType, Map<String, String>> attributes = new HashMap<AttributeType, Map<String, String>>();
        attributes.put(AttributeType.RULE, attrib);
        
        policyParameters.setAttributes(attributes);
        
        File rawBodyFile = null;
		Path file = Paths.get("Test/test.Config_BRMS_Raw_TestBrmsPolicy.1.txt");
		rawBodyFile = file.toFile();
		
		policyParameters.setConfigBody(readFile(rawBodyFile.toString()));		
        
		PolicyChangeResponse result = fixture.updatePolicy(policyParameters);
		
		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}
	
	/** 
	 * Run the PolicyChangeResponse createDictionaryItem(DictionaryParameters) method test.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testCreateDictionaryItem() throws Exception {
		response.setResponseMessage("success");
		DictionaryParameters parameters = new DictionaryParameters();
		
		parameters.setDictionaryType(DictionaryType.Common);
		parameters.setDictionary("Attribute");
		
		Map<String,String> fields = new HashMap<String,String>();
		fields.put("ATTRIBUTEID", "A5:");
		fields.put("DATATYPE", "user");
		fields.put("DESCRIPTION", "testing something");
		fields.put("ATTRIBUTEVALUE", "1,2,A,B");
		fields.put("PRIORITY", "High");
		Map<AttributeType, Map<String,String>> dictionaryFields = new HashMap<AttributeType, Map<String,String>>();
		dictionaryFields.put(AttributeType.DICTIONARY, fields);
		
		parameters.setDictionaryFields(dictionaryFields);

		PolicyChangeResponse result = fixture.createDictionaryItem(parameters);

		assertEquals(response.getResponseMessage(), result.getResponseMessage());
	}
	

	/**
	 * Run the PolicyDecision decide(DecisionRequestParameters) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testDecide()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);
		StdDecisionResponse response = new StdDecisionResponse();
		response.setDecision(PolicyDecision.PERMIT);
		
		DecisionRequestParameters decisionRequestParameters = new DecisionRequestParameters();
		decisionRequestParameters.setECOMPComponentName("testEcompName");
		Map<String,String> decisionAttributes = new HashMap<String,String>();
		decisionAttributes.put("key", "value");
		decisionRequestParameters.setDecisionAttributes(decisionAttributes);
		decisionRequestParameters.setRequestID(UUID.randomUUID());
		
		Mockito.when(mockEngine.decide(decisionRequestParameters)).thenReturn(response);
		DecisionResponse result = mockEngine.decide(decisionRequestParameters);

		assertNotNull(result);
	}

	/**
	 * Run the PolicyChangeResponse deletePolicy(DeletePolicyParameters) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testDeletePolicy()
		throws Exception {
		response.setResponseMessage("success");
		DeletePolicyParameters parameters = new DeletePolicyParameters();
		parameters.setDeleteCondition(DeletePolicyCondition.ALL);
		parameters.setPolicyComponent("PAP");
		parameters.setPolicyName("testing.Config_junittest.1.xml");

		PolicyChangeResponse result = fixture.deletePolicy(parameters);

		assertNotNull(result);
	}

	/**
	 * Run the Collection<PolicyResponse> event(EventRequestParameters) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testEvent()
		throws Exception {
		
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);
		StdPolicyResponse response = new StdPolicyResponse();
		response.setPolicyResponseMessage("tested");
		Collection<PolicyResponse> mockResult = new HashSet<PolicyResponse>();
		mockResult.add(response);
		StdPolicyEngine mockEngine = Mockito.mock(StdPolicyEngine.class);
		
		Map<String,String> eventAttributes = new HashMap<String,String>();
		eventAttributes.put("key", "test");
		EventRequestParameters eventRequestParameters = new EventRequestParameters(eventAttributes, UUID.randomUUID());
		Mockito.when(mockEngine.event(eventRequestParameters)).thenReturn(mockResult);

		Collection<PolicyResponse> result = mockEngine.event(eventRequestParameters);
		
		assertEquals(result, mockResult);
	}

	/**
	 * Run the PDPNotification getNotification() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testGetNotification()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);

		PDPNotification result = fixture.getNotification();

		assertNull(result);
	}*/

	/**
	 * Run the NotificationHandler getNotificationHandler() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testGetNotificationHandler()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);

		NotificationHandler result = fixture.getNotificationHandler();

		assertNull(result);
	}

	/**
	 * Run the String getPAPURL() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testGetPAPURL()
		throws Exception {

		String result = StdPolicyEngine.getPAPURL();

		assertNotNull(result);
	}

	/**
	 * Run the String getPDPURL() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testGetPDPURL()
		throws Exception {

		String result = StdPolicyEngine.getPDPURL();

		assertNotNull(result);
	}

	/**
	 * Run the NotificationScheme getScheme() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testGetScheme()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);

		NotificationScheme result = fixture.getScheme();

		assertNull(result);
	}

	/**
	 * Run the boolean isJSONValid(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testIsJSONValid()
		throws Exception {
		String data = "{\"test\": \"testing\"}";

		boolean result = StdPolicyEngine.isJSONValid(data);

		assertTrue(result);
	}

	/**
	 * Run the void notification(NotificationScheme,NotificationHandler) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testNotification()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
		Handler handler = new Handler();

		fixture.notification(scheme, handler);

	}
*/
	/**
	 * Run the PolicyChangeResponse policyEngineImport(ImportParameters) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testPolicyEngineImport()
		throws Exception {
		response.setResponseMessage("success");
		ImportParameters importParameters = new ImportParameters();
		importParameters.setServiceName("ControllerServiceSampleSdnlServiceInstance");
		importParameters.setVersion("1607-2");
		importParameters.setFilePath("C:\\Workspaces\\models\\TestingModel\\ControllerServiceSampleSdnlServiceInstance-v0.1.0-SNAPSHOT.zip");
		importParameters.setServiceType(IMPORT_TYPE.MICROSERVICE);
		
		PolicyChangeResponse result = fixture.policyEngineImport(importParameters);

		assertNotNull(result);
		
	}

	/**
	 * Run the Collection<PolicyConfig> policyName(String,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testPolicyName()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);
		StdPolicyConfig config = new StdPolicyConfig();
		config.setPolicyName("testing");
		Collection<PolicyConfig> response = new HashSet<PolicyConfig>();
		response.add(config);
		String policyName = "test.testing";
		UUID requestID = UUID.randomUUID();
		
		Mockito.when(mockEngine.policyName(policyName, requestID)).thenReturn(response);
		Collection<PolicyConfig> result = mockEngine.policyName(policyName, requestID);

		assertEquals(result, response);
	}

	/**
	 * Run the PolicyChangeResponse pushPolicy(PushPolicyParameters) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testPushPolicy()
		throws Exception {
		response.setResponseMessage("success");
		
		response.setResponseMessage("success");
		PolicyChangeResponse result = null;
		
		PushPolicyParameters pushPolicyParameters = new PushPolicyParameters();
		pushPolicyParameters.setPolicyName("test.testPolicy");
		pushPolicyParameters.setPdpGroup("default");
		pushPolicyParameters.setPolicyType("Base");
		
		try {
		
			Mockito.when(mockEngine.pushPolicy(pushPolicyParameters)).thenReturn(response);
			result = mockEngine.pushPolicy(pushPolicyParameters);
			
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		assertEquals(result, response);
	}

	/**
	 * Run the void rotateList() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testRotateList()
		throws Exception {

		StdPolicyEngine.rotateList();

	}

	/**
	 * Run the void setScheme(NotificationScheme) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testSetScheme()
		throws Exception {
		//StdPolicyEngine fixture = new StdPolicyEngine("http", NotificationScheme.AUTO_ALL_NOTIFICATIONS, (NotificationHandler) null);
		NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;

		fixture.setScheme(scheme);

	}

	/**
	 * Run the void stopNotification() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
/*	@Test
	public void testStopNotification_1()
		throws Exception {
		Handler handler = new Handler();
	
		Mockito.doNothing().when(mockStdPolicyEngine).notification(NotificationScheme.AUTO_ALL_NOTIFICATIONS, handler);
		StdPolicyEngine fixture = new StdPolicyEngine("Test/config_pass.properties", NotificationScheme.AUTO_ALL_NOTIFICATIONS, handler);
		
		//verify(mockStdPolicyEngine, times(1)).fixture("Test/config_pass.properties", NotificationScheme.AUTO_ALL_NOTIFICATIONS, handler);

		fixture.stopNotification();
	}*/

	/**
	 * Run the String updateConfigFirewallPolicy(String,JsonObject,String,UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@Test
	public void testUpdateConfigFirewallPolicy()
		throws Exception {
		response.setResponseMessage("success");
		String json= "{\"serviceTypeId\": \"/v0/firewall/pan\",\"configName\": \"rule1607\",\"deploymentOption\":{\"deployNow\": false},\"securityZoneId\": \"/v0/firewall/pan\",\"serviceGroups\": [{\"name\": \"1607Group\",\"description\": null,\"members\": [{\"type\": \"REFERENCE\",\"name\": \"SList\"},{\"type\": \"REFERENCE\",\"name\": \"Syslog\"}]}, {\"name\": \"Syslog\",\"description\": \"NA\",\"type\": \"SERVICE\",\"transportProtocol\": \"udp\",\"appProtocol\": null,\"ports\": \"514\"}, {\"name\": \"SList\",\"description\": \"Service List\",\"type\": \"SERVICE\",\"transportProtocol\": \"tcp\",\"appProtocol\": null,\"ports\": \"8080\"}],\"addressGroups\": [{\"name\": \"1607Group\",\"description\": null,\"members\": [{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"},{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"}]},{\"name\": \"PL_CCE3\",\"description\": \"CCE Routers\",\"members\":[{\"type\": \"REFERENCE\",\"name\": \"10.11.12.13/14\"}]}],\"firewallRuleList\": [{\"position\": \"1\",\"ruleName\": \"1607Rule\",\"fromZones\": [\"Trusted\"],\"toZones\": [\"Untrusted\"],\"negateSource\": false,\"negateDestination\": false,\"sourceList\": [{\"type\": \"REFERENCE\",\"name\": \"PL_CCE3\"}, {\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"destinationList\": [{\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"sourceServices\": [],\"destServices\": [{\"type\": \"REFERENCE\",\"name\": \"1607Group\"}],\"action\": \"accept\",\"description\": \"Rule for 1607 templates\",\"enabled\": true,\"log\": true}]}";
		String policyName = "testing";
		JsonObject firewallJson = buildJSON(json);
		String policyScope = "test";
		UUID requestID = UUID.randomUUID();
		String riskLevel  = "";
		String riskType = "";
		String guard = "";
		String date = "";

		String result = fixture.updateConfigFirewallPolicy(policyName, firewallJson, policyScope, requestID,riskLevel,  riskType, guard,  date);

		assertNotNull(result);
	}


	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 6/3/16 2:03 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(StdPolicyEngineTest.class);
	}
}
