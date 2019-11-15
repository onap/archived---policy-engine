/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.att.research.xacml.api.Identifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class ActionDictionaryJpaTest {

    private static Logger logger = FlexLogger.getLogger(ActionDictionaryJpaTest.class);
    private UserInfo userInfo;

    /**
     * Set up the test.
     *
     * @throws Exception on test errors
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");
        logger.info("setUp: exit");
    }

    @Test
    public void testActionDictionary() {
        ActionPolicyDict data = new ActionPolicyDict();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setAttributeName("Test");
        assertTrue("Test".equals(data.getAttributeName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setBody("Test");
        assertTrue("Test".equals(data.getBody()));
        data.setHeader("Test");
        assertTrue("Test".equals(data.getHeader()));
        data.setMethod("GET");
        assertTrue("GET".equals(data.getMethod()));
        data.setType("JSON");
        assertTrue("JSON".equals(data.getType()));
        data.setUrl("http://test.com");
        assertTrue("http://test.com".equals(data.getUrl()));
        data.prePersist();
        data.preUpdate();
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }

    @Test
    public void testFunctionArgument() {
        FunctionArgument data = new FunctionArgument();
        data.setArgIndex(1);
        assertTrue(1 == data.getArgIndex());
        data.setDatatypeBean(new Datatype());
        assertTrue(data.getDatatypeBean() != null);
        data.setFunctionDefinition(new FunctionDefinition());
        assertTrue(data.getFunctionDefinition() != null);
        data.setId(1);
        assertTrue(1 == data.getId());
        assertFalse(data.isBag());
        assertEquals("FunctionArgument(id=1", data.toString().substring(0, 21));
        data.setIsBag(1);
        assertTrue(data.isBag());
        assertTrue(1 == data.getIsBag());
        new FunctionArgument(data);
    }

    @Test
    public void testFunctionDefinition() {
        FunctionDefinition data = new FunctionDefinition();
        data.setArgLb(1);
        assertTrue(1 == data.getArgLb());
        data.setArgUb(1);
        assertTrue(1 == data.getArgUb());
        data.setDatatypeBean(new Datatype());
        assertTrue(data.getDatatypeBean() != null);
        data.setFunctionArguments(new ArrayList<>());
        assertTrue(data.getFunctionArguments() != null);
        data.setHigherOrderArgLb(1);
        assertTrue(1 == data.getHigherOrderArgLb());
        data.setHigherOrderArgUb(1);
        assertTrue(1 == data.getHigherOrderArgUb());
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setIsBagReturn(1);
        assertTrue(1 == data.getIsBagReturn());
        data.setIsHigherOrder(1);
        assertTrue(1 == data.getIsHigherOrder());
        data.setShortname("Test");
        assertTrue("Test".equals(data.getShortname()));
        data.setXacmlid("Test");
        assertTrue("Test".equals(data.getXacmlid()));
        assertTrue(data.toString().startsWith("FunctionDefinition(id=1"));
        assertTrue(data.isBagReturn());
        data.setIsBagReturn(0);
        assertTrue(data.isHigherOrder());
        assertFalse(data.isBagReturn());
        data.setIsHigherOrder(0);
        assertFalse(data.isHigherOrder());
        FunctionArgument functionArgument = new FunctionArgument();
        functionArgument.setId(12345);
        data.addFunctionArgument(functionArgument);
        assertEquals(12345, data.getFunctionArguments().iterator().next().getId());
        data.removeFunctionArgument(functionArgument);
        assertTrue(data.getFunctionArguments().isEmpty());
    }

    @Test
    public void testCategory() {
        Category data = new Category();
        new Category(null);
        data.setAttributes(new HashSet<>());
        data.addAttribute(new Attribute());
        data.removeAttribute(new Attribute());
        data.isStandard();
        data.isCustom();
        data.getIdentifer();
        data.toString();
        assertTrue(data.getAttributes() != null);
        data.setGrouping("Test");
        assertTrue("Test".equals(data.getGrouping()));
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setShortName("Test");
        assertTrue("Test".equals(data.getShortName()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));

        data.setIsStandard(Category.STANDARD);
        assertEquals(Category.STANDARD, data.getIsStandard());
        assertTrue(data.isStandard());
        assertFalse(data.isCustom());

        data.setIsStandard(Category.CUSTOM);
        assertEquals(Category.CUSTOM, data.getIsStandard());
        assertFalse(data.isStandard());
        assertTrue(data.isCustom());

        Identifier categoryIdentifier = new DummyIdentifier();
        Category data2 = new Category(categoryIdentifier,
                        "urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject", Category.STANDARD);
        assertEquals(DummyIdentifier.class.getName(), data2.getIdentifer().stringValue());

        Category data3 = new Category(categoryIdentifier,
                        "urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject");
        assertEquals(DummyIdentifier.class.getName(), data3.getIdentifer().stringValue());

        assertEquals("subject",
                        Category.extractGrouping("urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject"));
        assertNull(Category.extractGrouping(null));
        assertNull(Category.extractGrouping("some random string"));
        assertNull(Category.extractGrouping(""));
        assertEquals("intermediary-attribute", Category
                        .extractGrouping("urn:oasis:names:tc:xacml:1.0:attribute-category:intermediary-attribute"));
    }

    @Test
    public void testConstraintType() {
        ConstraintType data = new ConstraintType();
        new ConstraintType("Test", "Test");
        ConstraintType.getRangeTypes();
        data.setAttributes(new HashSet<>());
        assertTrue(data.getAttributes() != null);
        data.setConstraintType("Test");
        assertTrue("Test".equals(data.getConstraintType()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setId(1);
        assertTrue(1 == data.getId());
    }

    @Test
    public void testConstraintValue() {
        ConstraintValue data = new ConstraintValue();
        data.clone();
        new ConstraintValue(new ConstraintValue());
        new ConstraintValue("Test", "Test");
        data.setAttribute(new Attribute());
        assertTrue(data.getAttribute() != null);
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setProperty("Test");
        assertTrue("Test".equals(data.getProperty()));
        data.setValue("Test");
        assertTrue("Test".equals(data.getValue()));
    }

    @Test
    public void testObadvice() {
        new Obadvice();
        new Obadvice("Test", "Test");
        new Obadvice(new DummyIdentifier(), "Test");
        Obadvice data = new Obadvice();
        data.clone();
        data.addObadviceExpression(new ObadviceExpression());
        assertNotNull(data.clone());
        data.removeObadviceExpression(new ObadviceExpression());
        data.removeAllExpressions();
        assertEquals(0, data.getObadviceExpressions().size());
        data.setObadviceExpressions(null);
        assertNull(data.getObadviceExpressions());
        data.removeAllExpressions();
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setFulfillOn("Test");
        assertTrue("Test".equals(data.getFulfillOn()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setObadviceExpressions(new HashSet<>());
        assertTrue(data.getObadviceExpressions() != null);
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));

    }

    @Test
    public void testObadviceExpression() {
        ObadviceExpression data = new ObadviceExpression();
        data.clone();
        data.setAttribute(new Attribute());
        assertTrue(data.getAttribute() != null);
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setObadvice(new Obadvice());
        assertTrue(data.getObadvice() != null);
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
    }

    @Test
    public void testRuleAlgorithms() {
        RuleAlgorithms data = new RuleAlgorithms();
        data.isCustom();
        data.isStandard();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setShortName("Test");
        assertTrue("Test".equals(data.getShortName()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));
        data.toString();

        RuleAlgorithms ra0 = new RuleAlgorithms(new DummyIdentifier());
        assertTrue(ra0.isStandard());
        assertFalse(ra0.isCustom());
        RuleAlgorithms ra1 = new RuleAlgorithms(new DummyIdentifier(), 'C');
        assertFalse(ra1.isStandard());
        assertTrue(ra1.isCustom());
    }

    @Test
    public void testAttributeAssignment() {
        AttributeAssignment data = new AttributeAssignment();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setAttributeId(1);
        assertTrue(1 == data.getAttributeId());
        data.setExpression("Test");
        assertTrue("Test".equals(data.getExpression()));
    }

    @Test
    public void testDatatype() {
        Datatype data = new Datatype();
        new Datatype(null);
        new Datatype(1, new Datatype());
        data.setArguments(new HashSet<>());
        assertTrue(data.getArguments() != null);
        data.setAttributes(new HashSet<>());
        assertTrue(data.getAttributes() != null);
        data.setFunctions(new HashSet<>());
        assertTrue(data.getFunctions() != null);
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setShortName("Test");
        assertTrue("Test".equals(data.getShortName()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));
        data.addArgument(new FunctionArgument());
        data.addAttribute(new Attribute());
        data.addFunction(new FunctionDefinition());
        data.removeArgument(new FunctionArgument());
        data.removeAttribute(new Attribute());
        data.removeAttribute(new FunctionDefinition());
        assertTrue(data.getIdentifer() != null);
        assertTrue(data.getIdentiferByShortName() != null);
        data.setIsStandard(Datatype.STANDARD);
        assertTrue(data.isStandard());
        data.setIsStandard(Datatype.CUSTOM);
        assertTrue(data.isCustom());

        data.setIsStandard(Category.STANDARD);
        assertEquals(Category.STANDARD, data.getIsStandard());
        assertTrue(data.isStandard());
        assertFalse(data.isCustom());

        data.setIsStandard(Category.CUSTOM);
        assertEquals(Category.CUSTOM, data.getIsStandard());
        assertFalse(data.isStandard());
        assertTrue(data.isCustom());

        Datatype data2 = new Datatype(new DummyIdentifier(), Datatype.STANDARD);
        assertEquals(DummyIdentifier.class.getName(), data2.getIdentifer().stringValue());
    }

    @Test
    public void testPolicyAlgorithms() {
        PolicyAlgorithms data = new PolicyAlgorithms();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setShortName("Test");
        assertTrue("Test".equals(data.getShortName()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));
        data.setIsStandard(PolicyAlgorithms.STANDARD);
        assertTrue(data.isStandard());
        assertFalse(data.isCustom());
        data.setIsStandard(PolicyAlgorithms.CUSTOM);
        assertFalse(data.isStandard());
        assertTrue(data.isCustom());

        DummyIdentifier identifier = new DummyIdentifier();
        assertNotNull(new PolicyAlgorithms(identifier));
        assertNotNull(new PolicyAlgorithms(identifier, 'C'));
    }
}
