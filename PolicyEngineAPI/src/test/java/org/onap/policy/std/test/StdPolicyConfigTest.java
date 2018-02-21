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

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.json.JsonObject;

import org.junit.*;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyType;
import org.onap.policy.std.StdPolicyConfig;

import static org.junit.Assert.*;

import org.w3c.dom.Document;

/**
 * The class <code>StdPolicyConfigTest</code> contains tests for the class
 * <code>{@link StdPolicyConfig}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class StdPolicyConfigTest {
    private static final String POLICY_NAME = "PolicyName";

    private static final String POLICY_NAME_WITH_XML_EXT = POLICY_NAME + ".1.xml";

    private static final String EMPTY_STRING = "";

    /**
     * Run the Map<String, String> getMatchingConditions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetMatchingConditions_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final Map<String, String> result = fixture.getMatchingConditions();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getPolicyConfigMessage() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyConfigMessage_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.getPolicyConfigMessage();

        // add additional test code here
        assertEquals(EMPTY_STRING, result);
    }

    /**
     * Run the PolicyConfigStatus getPolicyConfigStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyConfigStatus_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final PolicyConfigStatus result = fixture.getPolicyConfigStatus();

        // add additional test code here
        assertNotNull(result);
        assertEquals("not_found", result.toString());
        assertEquals("CONFIG_NOT_FOUND", result.name());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the String getPolicyName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyName_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.getPolicyName();

        // add additional test code here
        assertEquals(EMPTY_STRING, result);
    }

    /**
     * Run the String getPolicyName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyName_2() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName((String) null);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.getPolicyName();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the String getPolicyName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyName_3() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.getPolicyName();

        // add additional test code here
        assertEquals(EMPTY_STRING, result);
    }

    /**
     * Run the String getPolicyVersion() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyVersion_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.getPolicyVersion();

        // add additional test code here
        assertEquals(EMPTY_STRING, result);
    }

    /**
     * Run the Map<String, String> getResponseAttributes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetResponseAttributes_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final Map<String, String> result = fixture.getResponseAttributes();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the PolicyType getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetType_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final PolicyType result = fixture.getType();

        // add additional test code here
        assertNotNull(result);
        assertEquals("json", result.toString());
        assertEquals("JSON", result.name());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the void setConfigStatus(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetGetPolicyConfigType_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyType(PolicyConfigType.BRMS_RAW);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        fixture.setConfigStatus(EMPTY_STRING);

        assertEquals(PolicyConfigType.BRMS_RAW, fixture.getPolicyType());

    }

    /**
     * Run the void setConfigStatus(String,PolicyConfigStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetConfigStatus_2() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final String configStatus = EMPTY_STRING;
        final PolicyConfigStatus policyConfigStatus = PolicyConfigStatus.CONFIG_NOT_FOUND;

        fixture.setConfigStatus(configStatus, policyConfigStatus);

        // add additional test code here
    }

    /**
     * Run the void setDocument(Document) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetDocument_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final Document document = null;

        fixture.setDocument(document);

        // add additional test code here
    }

    /**
     * Run the void setJsonObject(JsonObject) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetJsonObject_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final JsonObject jsonObject = null;

        fixture.setJsonObject(jsonObject);

        // add additional test code here
    }

    /**
     * Run the void setMatchingConditions(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetMatchingConditions_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final Map<String, String> matchingConditions = new Hashtable<String, String>();

        fixture.setMatchingConditions(matchingConditions);

        // add additional test code here
    }

    /**
     * Run the void setOther(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetOther_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final String other = EMPTY_STRING;

        fixture.setOther(other);

        // add additional test code here
    }

    /**
     * Run the void setPolicyConfigStatus(PolicyConfigStatus) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyConfigStatus_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final PolicyConfigStatus policyConfigStatus = PolicyConfigStatus.CONFIG_NOT_FOUND;

        fixture.setPolicyConfigStatus(policyConfigStatus);

        assertEquals(policyConfigStatus, fixture.getPolicyConfigStatus());
    }

    @Test
    public void testSetGetPolicyName_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(Collections.emptyMap());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(Collections.emptyMap());
        fixture.setPolicyName(EMPTY_STRING);

        assertEquals(EMPTY_STRING, fixture.getPolicyName());

    }

    @Test
    public void testSetGetPolicyName_2() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(Collections.emptyMap());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(POLICY_NAME_WITH_XML_EXT);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(Collections.emptyMap());

        assertEquals(POLICY_NAME, fixture.getPolicyName());

    }

    /**
     * Run the void setPolicyType(PolicyType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyType_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(Collections.emptyMap());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(Collections.emptyMap());
        final PolicyType policyType = PolicyType.JSON;

        fixture.setPolicyType(policyType);

        assertEquals(policyType, fixture.getType());
    }

    /**
     * Run the void setPolicyVersion(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyVersion_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(Collections.emptyMap());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(Collections.emptyMap());
        fixture.setPolicyVersion(EMPTY_STRING);

        assertEquals(EMPTY_STRING, fixture.getPolicyVersion());

    }

    /**
     * Run the void setProperties(Properties) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetProperties_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(Collections.emptyMap());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(Collections.emptyMap());
        final Properties properties = new Properties();

        fixture.setProperties(properties);

        // add additional test code here
    }

    /**
     * Run the void setResponseAttributes(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetResponseAttributes_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());
        final Map<String, String> responseAttributes = new Hashtable<String, String>();

        fixture.setResponseAttributes(responseAttributes);

        // add additional test code here
    }

    /**
     * Run the JsonObject toJSON() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testToJSON_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final JsonObject result = fixture.toJSON();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the String toOther() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testToOther_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.toOther();

        // add additional test code here
        assertEquals(EMPTY_STRING, result);
    }

    /**
     * Run the Properties toProperties() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testToProperties_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final Properties result = fixture.toProperties();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testToString_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName("test");
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final String result = fixture.toString();

        // add additional test code here
        assertEquals("PolicyConfig [ policyConfigStatus=not_found, policyConfigMessage=, policyName=test]", result);
    }

    /**
     * Run the Document toXML() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testToXML_1() throws Exception {
        final StdPolicyConfig fixture = new StdPolicyConfig();
        fixture.setConfigStatus(EMPTY_STRING, PolicyConfigStatus.CONFIG_NOT_FOUND);
        fixture.setDocument((Document) null);
        fixture.setResponseAttributes(new Hashtable<String, String>());
        fixture.setPolicyVersion(EMPTY_STRING);
        fixture.setOther(EMPTY_STRING);
        fixture.setPolicyType(PolicyType.JSON);
        fixture.setPolicyName(EMPTY_STRING);
        fixture.setProperties(new Properties());
        fixture.setJsonObject((JsonObject) null);
        fixture.setMatchingConditions(new Hashtable<String, String>());

        final Document result = fixture.toXML();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception
     *             if the initialization fails for some reason
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Before
    public void setUp() throws Exception {
        // add additional set up code here
    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception
     *             if the clean-up fails for some reason
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @After
    public void tearDown() throws Exception {
        // Add additional tear down code here
    }

    /**
     * Launch the test.
     *
     * @param args
     *            the command line arguments
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    public static void main(final String[] args) {
        new org.junit.runner.JUnitCore().run(StdPolicyConfigTest.class);
    }
}
