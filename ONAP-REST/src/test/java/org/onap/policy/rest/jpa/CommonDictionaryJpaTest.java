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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class CommonDictionaryJpaTest {

    private static Logger logger = FlexLogger.getLogger(CommonDictionaryJpaTest.class);
    private UserInfo userInfo;

    /**
     * Initiations for testing.
     *
     * @throws Exception on test initiation errors
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
    public void testAttribute() {
        Attribute data = new Attribute();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setCategoryBean(new Category());
        assertTrue(data.getCategoryBean() != null);
        data.setConstraintType(new ConstraintType());
        assertTrue(data.getConstraintType() != null);
        data.setConstraintValues(new HashSet<>());
        assertTrue(data.getConstraintValues() != null);
        data.addConstraintValue(new ConstraintValue());
        data.removeConstraintValue(new ConstraintValue());
        data.removeAllConstraintValues();
        data.preUpdate();
        data.prePersist();
        new Attribute("Test");
        new Attribute(new Attribute());
        data.setAttributeValue("Test");
        assertTrue("Test".equals(data.getAttributeValue()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setXacmlId("Test");
        assertTrue("Test".equals(data.getXacmlId()));
        data.setDatatypeBean(new Datatype());
        assertTrue(data.getDatatypeBean() != null);
        data.setIsDesignator(true);
        assertTrue(data.isDesignator());
        data.setIssuer("Test");
        assertTrue("Test".equals(data.getIssuer()));
        data.setMustBePresent(true);
        assertTrue(data.isMustBePresent());
        data.setPriority("Test");
        assertTrue("Test".equals(data.getPriority()));
        data.setSelectorPath("Test");
        assertTrue("Test".equals(data.getSelectorPath()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);

        ConstraintValue constraintValue = new ConstraintValue("Greeting", "Hello");
        data.getConstraintValues().add(constraintValue);
        assertEquals("Hello", data.getConstraintValues().iterator().next().getValue());

        data.removeConstraintValue(constraintValue);
        assertTrue(data.getConstraintValues().isEmpty());

        data.addConstraintValue(constraintValue);
        assertEquals("Hello", data.getConstraintValues().iterator().next().getValue());

        data.setConstraintValues(null);
        assertNull(data.getConstraintValues());

        data.addConstraintValue(constraintValue);
        assertEquals("Hello", data.getConstraintValues().iterator().next().getValue());

        data.removeAllConstraintValues();
        assertTrue(data.getConstraintValues().isEmpty());

        data.setConstraintValues(null);
        assertNull(data.getConstraintValues());

        data.removeAllConstraintValues();
        assertNull(data.getConstraintValues());

        data.addConstraintValue(constraintValue);
        assertEquals("Hello", data.getConstraintValues().iterator().next().getValue());

        data.setConstraintValues(null);
        assertNull(data.getConstraintValues());

        Set<ConstraintValue> constraintValueSet = new LinkedHashSet<>();
        constraintValueSet.add(constraintValue);

        data.setConstraintValues(null);
        assertNull(data.getConstraintValues());

        data.setConstraintValues(constraintValueSet);
        assertEquals("Hello", data.getConstraintValues().iterator().next().getValue());

        Attribute data2 = new Attribute(data);
        assertEquals("Hello", data2.getConstraintValues().iterator().next().getValue());

        data.setIsDesignator(true);
        assertTrue(data.isDesignator());

        data.setIsDesignator(false);
        assertFalse(data.isDesignator());
    }

    @Test
    public void testOnapName() {
        OnapName data = new OnapName();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
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
    public void testRiskType() {
        RiskType data = new RiskType();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setRiskName("Test");
        assertTrue("Test".equals(data.getRiskName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
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
    public void testSafePolicyWarning() {
        SafePolicyWarning data = new SafePolicyWarning();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setMessage("Test");
        assertTrue("Test".equals(data.getMessage()));
        data.setRiskType("Test");
        assertTrue("Test".equals(data.getRiskType()));
    }
}
