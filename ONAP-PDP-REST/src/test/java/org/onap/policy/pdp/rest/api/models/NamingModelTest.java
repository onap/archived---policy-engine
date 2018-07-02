/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pdp.rest.api.models;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.pdp.rest.api.operations.DecisionNamingSeqOpTest;
import org.onap.policy.pdp.rest.api.operations.DecisionOperationType;
import org.onap.policy.rest.jpa.NamingSequences;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class NamingModelTest {

    private static String configJson;
    private static NamingModel namingModel = null;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        configJson =
                "{\"naming-properties\":[{\"property-value\":\"    \",\"source-endpoint\":\"vnf-topology-operation-input.vnf-request-information.generic-vnf-name\",\"property-name\":\"VNFNAME \",\"source-system\":\"MD-SAL\"},{\"property-value\":\"   \",\"source-endpoint\":\"select nfc_naming_code from VFC_MODEL where customization_uuid='$vnf-topology-operation-input.vnf-request-information.model-customization-uuid'\",\"property-name\":\"NFC-NAMING-CODE\",\"source-system\":\"TOSCA\"},{\"property-name\":\"SEQUENCE\",\"increment-sequence\":{\"scope\":\"PRECEEDING\",\"start-value\":\"001\",\"length\":\"2\",\"increment\":\"3\"},\"source-system\":\"MD-SAL\"}],\"naming-type\":\"VM\",\"naming-recipe\":\"VNFNAME|NFC-NAMING-CODE|SEQUENCE\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            namingModel = mapper.readValue(configJson, NamingModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // setup the h2 db for seq
        SessionFactory sessionFactory = DecisionNamingSeqOpTest.setupH2DBDaoImpl();

        NamingProperty prop = namingModel.findNamingPropByName("SEQUENCE");

        NamingSequences namingSeqStart = new NamingSequences("VM", prop.getIncrementSequence().getScope(),
                "testVNFtestNFC", null, 1, 99, 2, 1, new Date(), new Date());

        // don't need if you already got a session
        Session session = sessionFactory.openSession();

        // start transaction
        session.beginTransaction();

        // Save to database
        session.save(namingSeqStart);

        // Commit the transaction
        session.getTransaction().commit();

        session.close();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Test method for
     * {@link org.onap.policy.pdp.rest.api.models.NamingModel#NamingModel(java.util.List, java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testNamingModel() {
        NamingModel namModel = new NamingModel(namingModel.getNamingProperties(), namingModel.getNamingType(),
                namingModel.getNamingRecipe(), namingModel.getNfRole(), namingModel.getNameOperation());
        assertNotNull(namModel);
    }

    @Test
    public final void testNamingModelListOfNamingProperty() {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("VNFNAME", "testVNF");
        reqParams.put("NFC-NAMING-CODE", "testNFC");
        for (NamingProperty prop : namingModel.getNamingProperties()) {
            try {
                if (prop.getPropertyName().trim().equals("VNFNAME")) {
                    assertEquals(DecisionOperationType.NOOP, prop.identifyOperation(reqParams, namingModel));
                    assertEquals("testVNF", prop.getResult());
                } else if (prop.getPropertyName().trim().equals("NFC-NAMING-CODE")) {
                    assertEquals(DecisionOperationType.NOOP, prop.identifyOperation(reqParams, namingModel));
                    assertEquals("testNFC", prop.getResult());
                } else if (prop.getPropertyName().trim().equals("SEQUENCE")) {
                    assertEquals(DecisionOperationType.NAMINGSEQGEN, prop.identifyOperation(reqParams, namingModel));
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.models.NamingModel#generateResult()}.
     * 
     * @throws PolicyDecisionException
     */
    @Test
    public final void testGenerateResult() throws PolicyDecisionException {
        for (NamingProperty prop : namingModel.getNamingProperties()) {
            if (prop.getPropertyName().trim().equals("VNFNAME")) {
                prop.setResult("testVNF");
            } else if (prop.getPropertyName().trim().equals("NFC-NAMING-CODE")) {
                prop.setResult("testNFC");
            } else if (prop.getPropertyName().trim().equals("SEQUENCE")) {
                prop.setResult("001");
                String[] propsFromRecipe = namingModel.parseNamingRecipe();
                String seqKey = namingModel.getSeqKey(prop, propsFromRecipe);
                prop.setSeqKey(seqKey);
            }
        }

        namingModel.generateResult();
        assertEquals("testVNFtestNFC001", namingModel.getResult());
    }

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.models.NamingModel#performPostOp(java.lang.String)}.
     */
    @Test
    public final void testPerformPostOp() {
        NamingModel postOpModel = new NamingModel();
        // first n chars
        postOpModel.setNameOperation("   substring( 4 ");
        postOpModel.performPostOp("123456789");
        assertEquals("1234", postOpModel.getResult());
        // last n chars
        postOpModel.setNameOperation("   substring( - 4 ");
        postOpModel.performPostOp("123456789");
        assertEquals("6789", postOpModel.getResult());

        // uppercase
        postOpModel.setNameOperation("   to uppercase ");
        postOpModel.performPostOp("abc");
        assertEquals("ABC", postOpModel.getResult());

        // lower
        postOpModel.setNameOperation("   to lowercase ");
        postOpModel.performPostOp("ABC");
        assertEquals("abc", postOpModel.getResult());
    }

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.models.NamingModel#findNamingPropByName(java.lang.String)}.
     */
    @Test
    public final void testFindNamingPropByName() {
        String prop = "VNFNAME";
        assertNotNull(namingModel.findNamingPropByName(prop));
    }

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.models.NamingModel#findNamingPropByName(java.lang.String)}.
     */
    @Test
    public final void testFindNamingPropByNameValue() {
        NamingModel testModel = new NamingModel();
        List<NamingProperty> namingProperties = new ArrayList<>();
        NamingProperty prop1 = new NamingProperty(null, null, "DELIMITER", null, null);
        namingProperties.add(prop1);
        NamingProperty prop2 = new NamingProperty("", null, "DELIMITER", null, null);
        namingProperties.add(prop2);
        NamingProperty prop3 = new NamingProperty("test", null, "DELIMITER", null, null);
        namingProperties.add(prop3);
        NamingProperty prop4 = new NamingProperty(null, null, "DELIMITER", null, null);
        namingProperties.add(prop4);
        NamingProperty prop5 = new NamingProperty(null, null, "DE", null, null);
        namingProperties.add(prop5);
        testModel.setNamingProperties(namingProperties);
        String prop = "DELIMITER";
        NamingProperty result = testModel.findNamingPropByNameValue(prop);
        assertEquals("test", result.getPropertyValue());
    }

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.models.NamingModel#parseNamingRecipe()}.
     */
    @Test
    public final void testParseNamingRecipe() {
        String[] expected = {"VNFNAME", "NFC-NAMING-CODE", "SEQUENCE"};
        Assert.assertArrayEquals(expected, namingModel.parseNamingRecipe());

        NamingModel whitespaceModel = new NamingModel();
        whitespaceModel.setNamingRecipe("    VNFNAME|NFC-NAMING-CODE  |SEQUENCE ");
        Assert.assertArrayEquals(expected, whitespaceModel.parseNamingRecipe());

    }

    /**
     * Test method for
     * {@link org.onap.policy.pdp.rest.api.models.NamingModel#getSeqKey(org.onap.policy.pdp.rest.api.models.NamingProperty, java.lang.String[])}.
     */
    @Test
    public final void testGetSeqKey() {
        NamingProperty seqProperty = null;
        for (NamingProperty prop : namingModel.getNamingProperties()) {
            if (prop.getPropertyName().trim().equals("VNFNAME")) {
                prop.setResult("testVNF");
            } else if (prop.getPropertyName().trim().equals("NFC-NAMING-CODE")) {
                prop.setResult("testNFC");
            } else if (prop.getPropertyName().trim().equals("SEQUENCE")) {
                seqProperty = prop;
            }
        }
        assertEquals("testVNFtestNFC", namingModel.getSeqKey(seqProperty, namingModel.parseNamingRecipe()));
    }
    
    @Test
    public final void testInvalidSeqKey() {
        NamingProperty seqProperty = null;
        NamingModel invalidModel = new NamingModel();
        invalidModel.setNamingRecipe("VNFNAME|NFC-NAMING-CODE|SEQUENCE");
        List<NamingProperty> namingProperties = new ArrayList<>();
        NamingProperty prop1 = new NamingProperty(null, null, "VNFNAME", null, null);
        namingProperties.add(prop1);
        NamingProperty prop2 = new NamingProperty("", null, "NFC-NAMING-CODE", null, null);
        namingProperties.add(prop2);
        NamingProperty prop3 = new NamingProperty("test", null, "SEQUENCE", null, null);
        namingProperties.add(prop3);
        invalidModel.setNamingProperties(namingProperties);
        
        for (NamingProperty prop : invalidModel.getNamingProperties()) {
            if (prop.getPropertyName().trim().equals("VNFNAME")) {
                prop.setResult("testVNF");
            } else if (prop.getPropertyName().trim().equals("NFC-NAMING-CODE")) {
                prop.setResult("testNFC");
            } else if (prop.getPropertyName().trim().equals("SEQUENCE")) {
                seqProperty = prop;
            }
        }
        IncrementSequence incSeq = new IncrementSequence();
        incSeq.setScope("invalid");
        incSeq.setIncrement("1");
        incSeq.setEndValue("6");
        incSeq.setMax("6");
        incSeq.setLength(5);
        incSeq.setSequenceType("numeric");
        incSeq.setStartValue("1");
        seqProperty.setIncrementSequence(incSeq);
        assertEquals("", invalidModel.getSeqKey(seqProperty, invalidModel.parseNamingRecipe()));
    }

    /**
     * Test method for {@link org.onap.policy.pdp.rest.api.models.NamingModel#toString()}.
     */
    @Test
    public final void testToString() {
        assertNotNull(namingModel.toString());
    }
    
    @Test
    public final void testEquals() {
        String cfgJson =
                "{\"naming-properties\":[{\"property-value\":\"    \",\"source-endpoint\":\"vnf-topology-operation-input.vnf-request-information.generic-vnf-name\",\"property-name\":\"VNFNAME \",\"source-system\":\"MD-SAL\"},{\"property-value\":\"   \",\"source-endpoint\":\"select nfc_naming_code from VFC_MODEL where customization_uuid='$vnf-topology-operation-input.vnf-request-information.model-customization-uuid'\",\"property-name\":\"NFC-NAMING-CODE\",\"source-system\":\"TOSCA\"},{\"property-name\":\"SEQUENCE\",\"increment-sequence\":{\"scope\":\"PRECEEDING\",\"start-value\":\"001\",\"length\":\"2\",\"increment\":\"3\"},\"source-system\":\"MD-SAL\"}],\"naming-type\":\"VM\",\"naming-recipe\":\"VNFNAME|NFC-NAMING-CODE|SEQUENCE\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        NamingModel namingModel1 = null;
        NamingModel namingModel2 = null;
        try {
            namingModel1 = mapper.readValue(cfgJson, NamingModel.class);
            namingModel2 = mapper.readValue(cfgJson, NamingModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(namingModel1.equals(namingModel2));
        assertEquals(namingModel1.hashCode(), namingModel2.hashCode());
    }

}
