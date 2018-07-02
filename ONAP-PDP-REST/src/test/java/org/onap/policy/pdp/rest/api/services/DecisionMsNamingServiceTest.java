/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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
package org.onap.policy.pdp.rest.api.services;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonReader;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.PolicyDecision;
import org.onap.policy.pdp.rest.api.models.NamingModel;
import org.onap.policy.pdp.rest.api.models.NamingProperty;
import org.onap.policy.pdp.rest.api.operations.DecisionNamingSeqOpTest;


public class DecisionMsNamingServiceTest {
    private Map<String, String> decisionAttributes = null;
    private static String inpJson = null;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        inpJson = loadJsonFile("testDecisionMsService.json");

        // setup the h2 db for seq
        SessionFactory sessionFactory = DecisionNamingSeqOpTest.setupH2DBDaoImpl();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        session.getTransaction().commit();

        session.close();
    }

    public static String loadJsonFile(String fileName) {
        InputStream inputStream = DecisionMsNamingServiceTest.class.getResourceAsStream(fileName);
        JsonReader jsonReader = Json.createReader(inputStream);
        String configJson = jsonReader.readObject().toString();
        jsonReader.close();
        return configJson;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.services.DecisionMsNamingService#run()}.
     */
    @Test
    public final void testRun() {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"VOLUME\",\"VOLUME_GROUP_NAME\":\"testVolume001\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        assertEquals("{\"VOLUME\":\"001-volume-01\"}", testObj.getResult().getDetails());
        testObj.run();
        assertEquals("{\"VOLUME\":\"001-volume-02\"}", testObj.getResult().getDetails());

        // 2nd naming model with alpha-numeric sequence
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"VNF\",\"NF_NAMING_CODE\":\"testNfNamingCode\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj2 = new DecisionMsNamingService(null, null, null);
        testObj2.setDecisionAttributes(decisionAttributes);
        testObj2.setRequestId("4567");
        testObj2.setConfigStr(inpJson);
        testObj2.run();
        assertEquals("{\"VNF\":\"complex008testNfNamingCode\"}", testObj2.getResult().getDetails());
        testObj2.run();
        assertEquals("{\"VNF\":\"complex00btestNfNamingCode\"}", testObj2.getResult().getDetails());
        testObj2.run();
        assertEquals("{\"VNF\":\"complex00etestNfNamingCode\"}", testObj2.getResult().getDetails());
        assertEquals(PolicyDecision.PERMIT, testObj2.getResult().getDecision());
    }

    @Test
    public final void testBadInputJson() throws IOException {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"VOLUME\"\"VOLUME_GROUP_NAME\":\"testVolume001\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        assertEquals("PE300 - Data Issue: Error parsing request json. ", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());
    }

    @Test
    public final void testRequestNoParam() throws IOException {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"INTERNAL_NETWORK\",\"VOLUME_GROUP_NAME\":\"testVolume001\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        assertEquals("PE300 - Data Issue: Property - VNF_NAME - unable to resolve value for Property.",
                testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());
    }

    @Test
    public final void testRangeExhausted() throws IOException {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"INTERNAL_NETWORK\",\"VNF_NAME\":\"testVnfName\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        assertEquals("{\"INTERNAL_NETWORK\":\"testVnfName-01IN\"}", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.PERMIT, testObj.getResult().getDecision());
        testObj.run();
        assertEquals("{\"INTERNAL_NETWORK\":\"testVnfName-03IN\"}", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.PERMIT, testObj.getResult().getDecision());
        testObj.run();
        assertEquals("{\"INTERNAL_NETWORK\":\"testVnfName-05IN\"}", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.PERMIT, testObj.getResult().getDecision());
        testObj.run();
        assertEquals("performOperation failed for operation of type - Sequence Number Generation",
                testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());
    }

    @Test
    public final void testNfRoleNotGiven() throws IOException {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"AFFINITY\",\"VNF_NAME\":\"testVnfName\",\"nfRole\":\"\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        assertEquals("{\"AFFINITY\":\"testVnfName-affinity\"}", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.PERMIT, testObj.getResult().getDecision());
    }

    @Test
    public final void testNoMatchNamingType() throws IOException {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"INNAL_NETWORK\",\"VNF_NAME\":\"testVnfName\",\"nfRole\":\"\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        assertEquals("PE300 - Data Issue: NamingType did not match - INNAL_NETWORK", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());

        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"\",\"VNF_NAME\":\"testVnfName\",\"nfRole\":\"\"}");
        testObj.run();
        assertEquals("PE300 - Data Issue: No naming-type provided in Request Decision Attributes.",
                testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());

        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE, "{\"VNF_NAME\":\"testVnfName\",\"nfRole\":\"\"}");
        testObj.run();
        assertEquals("PE300 - Data Issue: No naming-type provided in Request Decision Attributes.",
                testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());
    }

    @Test
    public final void testBadPropConfig() throws IOException {
        String configJson = FileUtils
                .readFileToString(new File(DecisionMsNamingServiceTest.class.getResource("badRecipes.json").getFile()));
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"VNF\",\"NF_NAMING_CODE\":\"testVolume001\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, configJson, "1234");
        testObj.run();
        assertEquals("PE300 - Data Issue: Property : NF_NAMING_CODE from recipe not found in parsed NamingProperties List", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());
    }
    
    @Test
    public final void testBadSeqScopeConfig() throws IOException {
        String configJson = FileUtils
                .readFileToString(new File(DecisionMsNamingServiceTest.class.getResource("badRecipes.json").getFile()));
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"VM\",\"VNF_NAME\":\"testVnf001\",\"NFC_NAMING_CODE\":\"testVolume001\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, configJson, "1234");
        testObj.run();
        assertEquals("PE200 - System Error: Sequence key cannot be empty ", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.DENY, testObj.getResult().getDecision());
    }
    
    @Test
    public final void testScope() throws IOException {
        decisionAttributes = new HashMap<>();
        decisionAttributes.put(PDPServices.DECISION_MS_NAMING_TYPE,
                "{\"naming-type\":\"TEST_SCOPE\",\"VNF_NAME\":\"testVnfName\",\"nfRole\":\"vPE\"}");

        DecisionMsNamingService testObj = new DecisionMsNamingService(decisionAttributes, inpJson, "1234");
        testObj.run();
        NamingModel model = testObj.getSelectedNamingModel();
        NamingProperty seqProp = model.findNamingPropByName("SEQUENCE");
        assertEquals("testVnfName", seqProp.getSeqKey());
        assertEquals("{\"TEST_SCOPE\":\"testVnfName-01INT\"}", testObj.getResult().getDetails());
        assertEquals(PolicyDecision.PERMIT, testObj.getResult().getDecision());
    }


}
