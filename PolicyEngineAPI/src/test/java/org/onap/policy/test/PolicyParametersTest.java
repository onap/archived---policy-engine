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

package org.onap.policy.test;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.AttributeType;
import org.onap.policy.api.PolicyClass;
import org.onap.policy.api.PolicyConfigParams;
import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyType;


/**
 * The class <code>PolicyParametersTest</code> contains tests for the class <code>{@link PolicyParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class PolicyParametersTest {

    @Test
    public void testEverythingElse() {
        PolicyParameters params = new PolicyParameters();
        params.setRuleProvider(null);
        assertNull(params.getRuleProvider());
        params.setGuard(false);
        assertFalse(params.getGuard());
        params.setRiskLevel("level");
        assertEquals("level", params.getRiskLevel());
        params.setRiskType("type");
        assertEquals("type", params.getRiskType());
        params.setTtlDate(new Date());
        assertNotNull(params.getTtlDate());
        params.setControllerName(null);
        assertNull(params.getControllerName());
        params.setDependencyNames(Collections.emptyList());
        params.setExtendedOption(null);
        assertNull(params.getExtendedOption());
        params.setTreatments(Collections.emptyMap());
        assertTrue(params.getTreatments().size() == 0);
        assertNotNull(params.toString());
    }

    /**
     * Run the String getActionAttribute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetActionAttribute_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");
        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getActionAttribute();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getActionPerformer() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetActionPerformer_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");
        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getActionPerformer();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the Map<AttributeType, Map<String, String>> getAttributes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetAttributes_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        Map<AttributeType, Map<String, String>> result = fixture.getAttributes();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getConfigBody() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetConfigBody_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getConfigBody();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the PolicyType getConfigBodyType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetConfigBodyType_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        PolicyType result = fixture.getConfigBodyType();

        // add additional test code here
        assertNotNull(result);
        assertEquals("json", result.toString());
        assertEquals("JSON", result.name());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the String getConfigName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetConfigName_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getConfigName();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the List<String> getDynamicRuleAlgorithmField1() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetDynamicRuleAlgorithmField1_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        List<String> result = fixture.getDynamicRuleAlgorithmField1();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<String> getDynamicRuleAlgorithmField2() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetDynamicRuleAlgorithmField2_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        List<String> result = fixture.getDynamicRuleAlgorithmField2();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<String> getDynamicRuleAlgorithmFunctions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetDynamicRuleAlgorithmFunctions_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        List<String> result = fixture.getDynamicRuleAlgorithmFunctions();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<String> getDynamicRuleAlgorithmLabels() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetDynamicRuleAlgorithmLabels_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        List<String> result = fixture.getDynamicRuleAlgorithmLabels();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getOnapName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetOnapName_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getOnapName();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the PolicyClass getPolicyClass() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyClass_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        PolicyClass result = fixture.getPolicyClass();

        // add additional test code here
        assertNotNull(result);
        assertEquals("Action", result.toString());
        assertEquals("Action", result.name());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the PolicyConfigType getPolicyConfigType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyConfigType_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        PolicyConfigType result = (PolicyConfigType) fixture.getPolicyConfigType();

        // add additional test code here
        assertNotNull(result);
        assertEquals("BRMS_Param", result.toString());
        assertEquals("BRMS_PARAM", result.name());
        assertEquals(5, result.ordinal());
    }

    /**
     * Run the String getPolicyDescription() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyDescription_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getPolicyDescription();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getPolicyName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPolicyName_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getPolicyName();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getPriority() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetPriority_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        String result = fixture.getPriority();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the UUID getRequestID() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testGetRequestID_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.fromString("878d319c-2799-4684-b480-99f40e1042b2"));
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");

        UUID result = fixture.getRequestID();

        // add additional test code here
        assertNotNull(result);
        assertEquals("878d319c-2799-4684-b480-99f40e1042b2", result.toString());
        assertEquals(4, result.version());
        assertEquals(2, result.variant());
        assertEquals(-5440179076376542542L, result.getLeastSignificantBits());
        assertEquals(-8679226360124062076L, result.getMostSignificantBits());
    }

    /**
     * Run the void setActionAttribute(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetActionAttribute_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String actionAttribute = "";

        fixture.setActionAttribute(actionAttribute);

        // add additional test code here
    }

    /**
     * Run the void setActionPerformer(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetActionPerformer_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String actionPerformer = "";

        fixture.setActionPerformer(actionPerformer);

        // add additional test code here
    }

    /**
     * Run the void setAttributes(Map<AttributeType,Map<String,String>>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetAttributes_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        Map<AttributeType, Map<String, String>> attributes = new Hashtable<AttributeType, Map<String, String>>();

        fixture.setAttributes(attributes);

        // add additional test code here
    }

    /**
     * Run the void setConfigBody(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetConfigBody_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String configBody = "";

        fixture.setConfigBody(configBody);

        // add additional test code here
    }

    /**
     * Run the void setConfigBodyType(PolicyType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetConfigBodyType_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        PolicyType configBodyType = PolicyType.JSON;

        fixture.setConfigBodyType(configBodyType);

        // add additional test code here
    }

    /**
     * Run the void setConfigFirewallPolicyParameters(String,String,UUID) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetConfigFirewallPolicyParameters_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String policyName = "";
        String firewallJson = "";
        UUID requestID = UUID.randomUUID();

        fixture.setConfigFirewallPolicyParameters(policyName, firewallJson, requestID);

        // add additional test code here
    }

    /**
     * Run the void setConfigName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetConfigName_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String configName = "";

        fixture.setConfigName(configName);

        // add additional test code here
    }

    /**
     * Run the void setConfigPolicyParameters(PolicyConfigType,String,String,String,String,Map<AttributeType,Map<String,String>>,PolicyType,String,UUID) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetConfigPolicyParameters_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        PolicyConfigType policyConfigType = PolicyConfigType.BRMS_PARAM;
        String policyName = "";
        String policyDescription = "";
        String onapName = "";
        String configName = "";
        Map<AttributeType, Map<String, String>> attributes = new Hashtable<AttributeType, Map<String, String>>();
        PolicyType configBodyType = PolicyType.JSON;
        String configBody = "";
        UUID requestID = UUID.randomUUID();

        fixture.setConfigPolicyParameters(PolicyConfigParams.builder()
                .policyConfigType(policyConfigType)
                .policyName(policyName)
                .policyDescription(policyDescription)
                .onapName(onapName)
                .configName(configName)
                .attributes(attributes)
                .configBodyType(configBodyType)
                .configBody(configBody)
                .requestID(requestID)
                .build());

        // add additional test code here
    }

    /**
     * Run the void setDynamicRuleAlgorithmField1(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetDynamicRuleAlgorithmField1_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        List<String> dynamicRuleAlgorithmField1 = new LinkedList<String>();

        fixture.setDynamicRuleAlgorithmField1(dynamicRuleAlgorithmField1);

        // add additional test code here
    }

    /**
     * Run the void setDynamicRuleAlgorithmField2(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetDynamicRuleAlgorithmField2_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        List<String> dynamicRuleAlgorithmField2 = new LinkedList<String>();

        fixture.setDynamicRuleAlgorithmField2(dynamicRuleAlgorithmField2);

        // add additional test code here
    }

    /**
     * Run the void setDynamicRuleAlgorithmFunctions(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetDynamicRuleAlgorithmFunctions_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        List<String> dynamicRuleAlgorithmFunctions = new LinkedList<String>();

        fixture.setDynamicRuleAlgorithmFunctions(dynamicRuleAlgorithmFunctions);

        // add additional test code here
    }

    /**
     * Run the void setDynamicRuleAlgorithmLabels(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetDynamicRuleAlgorithmLabels_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        List<String> dynamicRuleAlgorithmLabels = new LinkedList<String>();

        fixture.setDynamicRuleAlgorithmLabels(dynamicRuleAlgorithmLabels);

        // add additional test code here
    }

    /**
     * Run the void setOnapName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetOnapName_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String onapName = "";

        fixture.setOnapName(onapName);

        // add additional test code here
    }

    /**
     * Run the void setPolicyClass(PolicyClass) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyClass_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        PolicyClass policyClass = PolicyClass.Action;

        fixture.setPolicyClass(policyClass);

        // add additional test code here
    }

    /**
     * Run the void setPolicyConfigType(PolicyConfigType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyConfigType_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        PolicyConfigType policyConfigType = PolicyConfigType.BRMS_PARAM;

        fixture.setPolicyConfigType(policyConfigType);

        // add additional test code here
    }

    /**
     * Run the void setPolicyDescription(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyDescription_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String policyDescription = "";

        fixture.setPolicyDescription(policyDescription);

        // add additional test code here
    }

    /**
     * Run the void setPolicyName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPolicyName_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String policyName = "";

        fixture.setPolicyName(policyName);

        // add additional test code here
    }

    /**
     * Run the void setPriority(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetPriority_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        String priority = "";

        fixture.setPriority(priority);

        // add additional test code here
    }

    /**
     * Run the void setRequestID(UUID) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    @Test
    public void testSetRequestID_1()
        throws Exception {
        PolicyParameters fixture = new PolicyParameters();
        fixture.setRequestID(UUID.randomUUID());
        fixture.setActionAttribute("");
        fixture.setAttributes(new Hashtable<AttributeType, Map<String, String>>());
        fixture.setDynamicRuleAlgorithmLabels(new LinkedList<String>());
        fixture.setPolicyDescription("");

        fixture.setPolicyConfigType(PolicyConfigType.BRMS_PARAM);
        fixture.setDynamicRuleAlgorithmField2(new LinkedList<String>());
        fixture.setPolicyName("");
        fixture.setConfigName("");
        fixture.setDynamicRuleAlgorithmFunctions(new LinkedList<String>());
        fixture.setPolicyClass(PolicyClass.Action);
        fixture.setOnapName("");
        fixture.setConfigBodyType(PolicyType.JSON);
        fixture.setDynamicRuleAlgorithmField1(new LinkedList<String>());
        fixture.setPriority("");
        fixture.setActionPerformer("");
        fixture.setConfigBody("");
        UUID requestID = UUID.randomUUID();

        fixture.setRequestID(requestID);

        // add additional test code here
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception
     *         if the initialization fails for some reason
     *
     * @generatedBy CodePro at 6/1/16 1:41 PM
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
     * @generatedBy CodePro at 6/1/16 1:41 PM
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
     * @generatedBy CodePro at 6/1/16 1:41 PM
     */
    public static void main(String[] args) {
        new org.junit.runner.JUnitCore().run(PolicyParametersTest.class);
    }
}
