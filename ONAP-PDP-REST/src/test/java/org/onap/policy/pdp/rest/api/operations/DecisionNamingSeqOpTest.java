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
package org.onap.policy.pdp.rest.api.operations;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.PolicyDecisionException;
import org.onap.policy.pdp.rest.api.models.IncrementSequence;
import org.onap.policy.pdp.rest.api.models.NamingProperty;
import org.onap.policy.rest.daoimpl.PolicyValidationDaoImpl;
import org.onap.policy.rest.jpa.NamingSequences;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

public class DecisionNamingSeqOpTest {
    private static NamingProperty prop = null;
    private static SessionFactory sessionFactory = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        prop = new NamingProperty();
        IncrementSequence seq =
                new IncrementSequence("PRECEDING", "100", "130", 3, "5", IncrementSequence.SEQUENCE_TYPE_NUMERIC);
        prop.setIncrementSequence(seq);
        prop.setPropertyName("SEQUENCE");
        prop.setSeqKey("test123");

        sessionFactory = setupH2DBDaoImpl();

        // don't need if you already got a session
        Session session = sessionFactory.openSession();

        // start transaction
        session.beginTransaction();

        long start = Long.parseLong(seq.getStartValue(), 10);
        long end = Long.parseLong(seq.getMax(), 10);
        long step = Long.parseLong(seq.getIncrement(), 10);

        NamingSequences namingSeqStart = new NamingSequences("VM", prop.getIncrementSequence().getScope(),
                prop.getSeqKey(), null, start, end, step, start, new Date(), new Date());

        // Save to database
        session.save(namingSeqStart);

        // Commit the transaction
        session.getTransaction().commit();

        session.close();

    }

    public static SessionFactory setupH2DBDaoImpl() {
        // set persistence
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        // In-memory DB for testing
        dataSource.setUrl("jdbc:h2:mem:test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.scanPackages("org.onap.*", "com.*");

        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");

        sessionBuilder.addProperties(properties);
        SessionFactory sessionFact = sessionBuilder.buildSessionFactory();

        // Set up dao with SessionFactory
        PolicyValidationDaoImpl.setSessionfactory(sessionFact);

        return sessionFact;
    }

    @Test
    public final void testPerformOperation() {
        DecisionNamingSeqOp seqOp = new DecisionNamingSeqOp(DecisionOperationType.NAMINGSEQGEN);
        Map<String, Object> opData = new HashMap<>();
        opData.put(DecisionNamingSeqOp.NAMING_PROP, prop);
        opData.put(DecisionNamingSeqOp.SEQ_KEY, prop.getSeqKey());
        opData.put(DecisionNamingSeqOp.NAMING_TYPE, "VM");
        try {
            seqOp.executeOperation(opData);
        } catch (PolicyDecisionException e) {
            e.printStackTrace();
        }
        assertEquals("105", seqOp.getResult());
        assertEquals(DecisionOperationType.NAMINGSEQGEN, seqOp.getOperationType());
        assertEquals("VM", seqOp.getNamingType());
        assertEquals(prop.getSeqKey(), seqOp.getSeqKey());
    }

    @Test
    public final void testStartNewRange() {
        NamingProperty prop2 = new NamingProperty();
        IncrementSequence seq = new IncrementSequence(IncrementSequence.SEQUENCE_SCOPE_PRECEDING, "40a", "430", 3, "5",
                IncrementSequence.SEQUENCE_TYPE_ALPHA);
        prop2.setIncrementSequence(seq);
        prop2.setPropertyName("SEQUENCE");
        prop2.setSeqKey("test456");

        DecisionNamingSeqOp seqOp = new DecisionNamingSeqOp(DecisionOperationType.NAMINGSEQGEN);
        seqOp.setNamingProp(prop2);
        seqOp.setNamingType("VM");
        seqOp.setOperationType(DecisionOperationType.NAMINGSEQGEN);
        seqOp.setSeqKey("test456");
        seqOp.setSeqType(IncrementSequence.SEQUENCE_TYPE_ALPHA);
        Map<String, Object> opData = new HashMap<>();
        opData.put(DecisionNamingSeqOp.NAMING_PROP, prop2);
        opData.put(DecisionNamingSeqOp.SEQ_KEY, prop2.getSeqKey());
        opData.put(DecisionNamingSeqOp.NAMING_TYPE, "VM");
        try {
            seqOp.executeOperation(opData);
        } catch (PolicyDecisionException e) {
            e.printStackTrace();
        }
        assertEquals("40a", seqOp.getResult());

    }

    @Test(expected = PolicyDecisionException.class)
    public final void testInvalidOperation() throws PolicyDecisionException {
        DecisionNamingSeqOp seqOp = new DecisionNamingSeqOp(DecisionOperationType.NAMINGSEQGEN);
        Map<String, Object> opData = new HashMap<>();
        opData.put(DecisionNamingSeqOp.NAMING_PROP, prop);
        opData.put(DecisionNamingSeqOp.SEQ_KEY, prop.getSeqKey());
        seqOp.executeOperation(opData);
    }
    
    @Test(expected = PolicyDecisionException.class)
    public final void testInvalidInputOperation() throws PolicyDecisionException {
        DecisionNamingSeqOp seqOp = new DecisionNamingSeqOp(DecisionOperationType.NAMINGSEQGEN);
        seqOp.executeOperation(null);
    }
    
    @Test
    public final void testDecisionOperationType() throws PolicyDecisionException {
       assertEquals(DecisionOperationType.NOOP, DecisionOperationType.create("NOOP"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        sessionFactory = null;
    }
}

