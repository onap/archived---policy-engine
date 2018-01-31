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

package org.onap.policy.std.test;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.junit.*;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.api.PolicyResponseStatus;
import org.onap.policy.api.PolicyType;
import org.onap.policy.std.StdStatus;

import static org.junit.Assert.*;

import org.w3c.dom.Document;

/**
 * The class <code>StdStatusTest</code> contains tests for the class <code>{@link StdStatus}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class StdStatusTest {
	/**
	 * Run the Map<String, String> getActionAdvised() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetActionAdvised_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getActionAdvised();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Map<String, String> getActionTaken() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetActionTaken_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getActionTaken();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Map<String, String> getMatchingConditions() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetMatchingConditions_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getMatchingConditions();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the String getPolicyConfigMessage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyConfigMessage_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyConfigMessage();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the PolicyConfigStatus getPolicyConfigStatus() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyConfigStatus_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		PolicyConfigStatus result = fixture.getPolicyConfigStatus();

		// add additional test code here
		assertNotNull(result);
		assertEquals("not_found", result.toString());
		assertEquals("CONFIG_NOT_FOUND", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the PolicyDecision getPolicyDecision() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyDecision_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		PolicyDecision result = fixture.getDecision();

		// add additional test code here
		assertNotNull(result);
		assertEquals("deny", result.toString());
		assertEquals("DENY", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the String getPolicyName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyName_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getPolicyName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyName_2()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName((String) null);
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the String getPolicyName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyName_3()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getPolicyResponseMessage() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyResponseMessage_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyResponseMessage();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the PolicyResponseStatus getPolicyResponseStatus() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyResponseStatus_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		PolicyResponseStatus result = fixture.getPolicyResponseStatus();

		// add additional test code here
		assertNotNull(result);
		assertEquals("action_advised", result.toString());
		assertEquals("ACTION_ADVISED", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the String getPolicyVersion() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetPolicyVersion_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.getPolicyVersion();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the Map<String, String> getRequestAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetRequestAttributes_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getRequestAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Map<String, String> getResponseAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetResponseAttributes_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Map<String, String> result = fixture.getResponseAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the PolicyType getType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetType_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		PolicyType result = fixture.getType();

		// add additional test code here
		assertNotNull(result);
		assertEquals("json", result.toString());
		assertEquals("JSON", result.name());
		assertEquals(1, result.ordinal());
	}

	/**
	 * Run the void setActionAdvised(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetActionAdvised_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> actionAdvised = new Hashtable<String, String>();

		fixture.setActionAdvised(actionAdvised);

		// add additional test code here
	}

	/**
	 * Run the void setActionTaken(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetActionTaken_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> actionTaken = new Hashtable<String, String>();

		fixture.setActionTaken(actionTaken);

		// add additional test code here
	}

	/**
	 * Run the void setConfigStatus(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetConfigStatus_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String configStatus = "";

		fixture.setConfigStatus(configStatus);

		// add additional test code here
	}

	/**
	 * Run the void setDocument(Document) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetDocument_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Document document = null;

		fixture.setDocument(document);

		// add additional test code here
	}

	/**
	 * Run the void setJsonObject(JsonObject) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetJsonObject_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		JsonObject jsonObject = null;

		fixture.setJsonObject(jsonObject);

		// add additional test code here
	}

	/**
	 * Run the void setMatchingConditions(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetMatchingConditions_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> matchingConditions = new Hashtable<String, String>();

		fixture.setMatchingConditions(matchingConditions);

		// add additional test code here
	}

	/**
	 * Run the void setOther(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetOther_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String other = "";

		fixture.setOther(other);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyConfigStatus(PolicyConfigStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyConfigStatus_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		PolicyConfigStatus policyConfigStatus = PolicyConfigStatus.CONFIG_NOT_FOUND;

		fixture.setPolicyConfigStatus(policyConfigStatus);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyConfigStatus(String,PolicyConfigStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyConfigStatus_2()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String configStatus = "";
		PolicyConfigStatus policyConfigStatus = PolicyConfigStatus.CONFIG_NOT_FOUND;

		fixture.setPolicyConfigStatus(configStatus, policyConfigStatus);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyDecision(PolicyDecision) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyDecision_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		PolicyDecision policyDecision = PolicyDecision.DENY;

		fixture.setDecision(policyDecision);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyName_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String policyName = "";

		fixture.setPolicyName(policyName);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyResponseMessage(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyResponseMessage_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String policyResponseMessage = "";

		fixture.setPolicyResponseMessage(policyResponseMessage);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyResponseStatus(PolicyResponseStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyResponseStatus_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		PolicyResponseStatus policyResponseStatus = PolicyResponseStatus.ACTION_ADVISED;

		fixture.setPolicyResponseStatus(policyResponseStatus);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyResponseStatus(String,PolicyResponseStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyResponseStatus_2()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String policyResponseMessage = "";
		PolicyResponseStatus policyResponseStatus = PolicyResponseStatus.ACTION_ADVISED;

		fixture.setPolicyResponseStatus(policyResponseMessage, policyResponseStatus);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyType(PolicyType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyType_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		PolicyType policyType = PolicyType.JSON;

		fixture.setPolicyType(policyType);

		// add additional test code here
	}

	/**
	 * Run the void setPolicyVersion(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetPolicyVersion_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String policyVersion = "";

		fixture.setPolicyVersion(policyVersion);

		// add additional test code here
	}

	/**
	 * Run the void setProperties(Properties) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetProperties_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Properties properties = new Properties();

		fixture.setProperties(properties);

		// add additional test code here
	}

	/**
	 * Run the void setRequestAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetRequestAttributes_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> requestAttributes = new Hashtable<String, String>();

		fixture.setRequestAttributes(requestAttributes);

		// add additional test code here
	}

	/**
	 * Run the void setResposneAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetResposneAttributes_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		Map<String, String> responseAttributes = new Hashtable<String, String>();

		fixture.setResposneAttributes(responseAttributes);

		// add additional test code here
	}

	/**
	 * Run the void setStatus(String,PolicyResponseStatus,PolicyConfigStatus) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetStatus_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());
		String message = "";
		PolicyResponseStatus policyResponseStatus = PolicyResponseStatus.ACTION_ADVISED;
		PolicyConfigStatus policyConfigStatus = PolicyConfigStatus.CONFIG_NOT_FOUND;

		fixture.setStatus(message, policyResponseStatus, policyConfigStatus);

		// add additional test code here
	}

	/**
	 * Run the JsonObject toJSON() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testToJSON_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		JsonObject result = fixture.toJSON();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Run the String toOther() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testToOther_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		String result = fixture.toOther();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the Properties toProperties() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testToProperties_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Properties result = fixture.toProperties();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the Document toXML() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testToXML_1()
		throws Exception {
		StdStatus fixture = new StdStatus();
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setPolicyName("");
		fixture.setPolicyType(PolicyType.JSON);
		fixture.setResposneAttributes(new Hashtable<String, String>());
		fixture.setMatchingConditions(new Hashtable<String, String>());
		fixture.setStatus("", PolicyResponseStatus.ACTION_ADVISED, PolicyConfigStatus.CONFIG_NOT_FOUND);
		fixture.setJsonObject((JsonObject) null);
		fixture.setDocument((Document) null);
		fixture.setProperties(new Properties());
		fixture.setRequestAttributes(new Hashtable<String, String>());
		fixture.setPolicyVersion("");
		fixture.setActionAdvised(new Hashtable<String, String>());
		fixture.setOther("");
		fixture.setDecision(PolicyDecision.DENY);
		fixture.setDetails("");
		fixture.setActionTaken(new Hashtable<String, String>());

		Document result = fixture.toXML();

		// add additional test code here
		assertEquals(null, result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
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
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(StdStatusTest.class);
	}
}
