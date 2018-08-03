/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.ia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.ia.DbAudit;
import org.onap.policy.common.ia.DbDAO;
import org.onap.policy.common.ia.IntegrityAuditProperties;
import org.onap.policy.common.ia.jpa.IntegrityAuditEntity;
import org.onap.policy.common.im.jpa.ForwardProgressEntity;
import org.onap.policy.common.im.jpa.ResourceRegistrationEntity;
import org.onap.policy.common.im.jpa.StateManagementEntity;
import org.onap.policy.jpa.BackUpMonitorEntity;

public class DbAuditCompareEntriesTest {

    private static Log logger = LogFactory.getLog(DbAuditCompareEntriesTest.class);
    private DbDAO dbDAO;
    private String persistenceUnit;
    private Properties properties;
    private String resourceName;
    private String dbDriver;
    private String dbUrl;
    private String dbUser;
    private String dbPwd;
    private String siteName;
    private String nodeType;
    private static final String DEFAULT_DB_DRIVER = "org.h2.Driver";
    private static final String DEFAULT_DB_USER = "sa";
    private static final String DEFAULT_DB_PWD = "";

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");

        properties = new Properties();
        properties.put(IntegrityAuditProperties.DB_DRIVER, DbAuditCompareEntriesTest.DEFAULT_DB_DRIVER);
        properties.put(IntegrityAuditProperties.DB_URL, "jdbc:h2:file:./sql/xacmlTest");
        properties.put(IntegrityAuditProperties.DB_USER, DbAuditCompareEntriesTest.DEFAULT_DB_USER);
        properties.put(IntegrityAuditProperties.DB_PWD, DbAuditCompareEntriesTest.DEFAULT_DB_PWD);
        properties.put(IntegrityAuditProperties.SITE_NAME, "SiteA");
        properties.put(IntegrityAuditProperties.NODE_TYPE, "pap");

        dbDriver = DbAuditCompareEntriesTest.DEFAULT_DB_DRIVER;
        dbUrl = "jdbc:h2:file:./sql/xacmlTest";
        dbUser = DbAuditCompareEntriesTest.DEFAULT_DB_USER;
        dbPwd = DbAuditCompareEntriesTest.DEFAULT_DB_PWD;
        siteName = "SiteA";
        nodeType = "pap";
        persistenceUnit = "testPapPU";
        resourceName = "siteA.pap1";

        //Clean the iaTest DB table for IntegrityAuditEntity entries
        cleanDb(persistenceUnit, properties);

        logger.info("setUp: Exiting");
    }

    @After
    public void tearDown() throws Exception {
        logger.info("tearDown: Entering");
        //nothing to do
        logger.info("tearDown: Exiting");
    }

    public void cleanDb(String persistenceUnit, Properties properties){
        logger.debug("cleanDb: enter");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);

        EntityManager em = emf.createEntityManager();
        // Start a transaction
        EntityTransaction et = em.getTransaction();

        et.begin();

        // Clean up the DB
        em.createQuery("Delete from IntegrityAuditEntity").executeUpdate();

        // commit transaction
        et.commit();
        em.close();
        logger.debug("cleanDb: exit");
    }


    /*
     * Tests that a comparison between hashsets is successful if
     * the entries match
     */
    @Test
    public void runAllTests() throws Exception {
        logger.info("runAllTests: Entering");


        testIntegrityAuditEntity();
        testBackupMonitorEntity();
        testStateManagementEntity();
        testForwardProgressEntity();
        testResourceRegistrationEntity();

        //clean up the IntegrityAuditEntity table
        cleanDb(persistenceUnit, properties);

        logger.info("runAllTests: Exit");
    }


    public void testIntegrityAuditEntity() throws Exception {
        logger.info("testIntegrityAuditEntity: Entering");

        dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
        DbAudit dbAudit = new DbAudit(dbDAO);

        String className = null;
        //There is only one entry IntegrityAuditEntity, but we will check anyway
        Set<String> classNameSet = dbDAO.getPersistenceClassNames();
        for(String c : classNameSet){
            if (c.equals("org.onap.policy.common.ia.jpa.IntegrityAuditEntity")){
                className = c;
            }
        }
        String resourceName1 = resourceName;
        String resourceName2 = resourceName;

        IntegrityAuditEntity entry1 = new IntegrityAuditEntity();
        IntegrityAuditEntity entry2 = new IntegrityAuditEntity();
        Date date = new Date();

        /*
         * Two entries with the same field values
         */
        entry1.setDesignated(false);
        entry1.setJdbcDriver(dbDriver);
        entry1.setJdbcPassword(dbPwd);
        entry1.setJdbcUrl(dbUrl);
        entry1.setJdbcUser(dbUser);
        entry1.setLastUpdated(date);
        entry1.setNodeType(nodeType);
        entry1.setPersistenceUnit(persistenceUnit);
        entry1.setResourceName(resourceName1);
        entry1.setSite(siteName);

        entry2 = SerializationUtils.clone(entry1);

        dbAudit.writeAuditDebugLog(className, resourceName1, resourceName2, entry1, entry2);

        HashMap<Object, Object> myEntries = new HashMap<>();
        HashMap<Object, Object> theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        Set<Object> result = dbAudit.compareEntries(myEntries, theirEntries);

        /*
         * Assert that there are no mismatches returned
         */
        assertTrue(result.isEmpty());

        /*
         * ************************************
         * Now test with a mis-matched entry
         * ************************************
         */

        /*
         * Change the entry2 to different designated value
         */
        entry2.setDesignated(true);

        myEntries = new HashMap<>();
        theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        result = dbAudit.compareEntries(myEntries, theirEntries);

        /*
         * Assert that there was one mismatch
         */
        assertEquals(1, result.size());
        logger.info("testIntegrityAuditEntity: Exit");
    }

    void testBackupMonitorEntity() throws Exception {
        logger.info("testBackupMonitorEntity: Entering");

        dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
        DbAudit dbAudit = new DbAudit(dbDAO);

        BackUpMonitorEntity entry1 = new BackUpMonitorEntity();
        BackUpMonitorEntity entry2 = new BackUpMonitorEntity();

        // Two entries with the same field values


        entry1.setFlag("flag1");
        entry1.setResourceNodeName("node1");
        entry1.setResourceName("resourceName");
        entry1.setTimeStamp(new Date());

        // Clone the first entry
        entry2 = SerializationUtils.clone(entry1);

        HashMap<Object, Object> myEntries = new HashMap<>();
        HashMap<Object, Object> theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        Set<Object> result = dbAudit.compareEntries(myEntries, theirEntries);


        // Assert that there are no mismatches returned

        assertTrue(result.isEmpty());


         /* ************************************
         * Now test with a mis-matched entry
         * ************************************/



        // Change a field on entry2

        entry2.setFlag("flag2");

        myEntries = new HashMap<>();
        theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        result = dbAudit.compareEntries(myEntries, theirEntries);


        //Assert that there was one mismatch

        assertEquals(1, result.size());
        logger.info("testBackupMonitorEntity: Exit");
    }

    void testStateManagementEntity() throws Exception {
        logger.info("testStateManagementEntity: Entering");

        dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
        DbAudit dbAudit = new DbAudit(dbDAO);

        StateManagementEntity entry1 = new StateManagementEntity();
        StateManagementEntity entry2 = new StateManagementEntity();

        // Two entries with the same field values

        entry1.setAdminState("locked");
        entry1.setAvailStatus("null");
        entry1.setModifiedDate(new Date());
        entry1.setOpState("enabled");
        entry1.setResourceName("myResource");
        entry1.setStandbyStatus("coldstandby");

        // Clone the first entry
        entry2 = SerializationUtils.clone(entry1);

        HashMap<Object, Object> myEntries = new HashMap<>();
        HashMap<Object, Object> theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        Set<Object> result = dbAudit.compareEntries(myEntries, theirEntries);


        // Assert that there are no mismatches returned

        assertTrue(result.isEmpty());


         /* ************************************
         * Now test with a mis-matched entry
         * ************************************/



        // Change a field on entry2

        entry2.setAdminState("unlocked");

        myEntries = new HashMap<>();
        theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        result = dbAudit.compareEntries(myEntries, theirEntries);


        //Assert that there was one mismatch

        assertEquals(1, result.size());
        logger.info("testStateManagementEntity: Exit");
    }

    void testForwardProgressEntity() throws Exception {
        logger.info("testForwardProgressEntity: Entering");

        dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
        DbAudit dbAudit = new DbAudit(dbDAO);

        ForwardProgressEntity entry1 = new ForwardProgressEntity();
        ForwardProgressEntity entry2 = new ForwardProgressEntity();

        // Two entries with the same field values

        entry1.setFpcCount(123L);
        entry1.setLastUpdated(new Date());
        entry1.setResourceName("myResource");

        // Clone the first entry
        entry2 = SerializationUtils.clone(entry1);

        HashMap<Object, Object> myEntries = new HashMap<Object, Object>();
        HashMap<Object, Object> theirEntries = new HashMap<Object, Object>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        Set<Object> result = dbAudit.compareEntries(myEntries, theirEntries);


        // Assert that there are no mismatches returned

        assertTrue(result.isEmpty());


         /* ************************************
         * Now test with a mis-matched entry
         * ************************************/

        // Change a field on entry2

        entry2.setFpcCount(321L);

        myEntries = new HashMap<>();
        theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        result = dbAudit.compareEntries(myEntries, theirEntries);


        //Assert that there was one mismatch

        assertEquals(1, result.size());
        logger.info("testForwardProgressEntity: Exit");
    }

    void testResourceRegistrationEntity() throws Exception {
        logger.info("testResourceRegistrationEntity: Entering");

        dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
        DbAudit dbAudit = new DbAudit(dbDAO);

        ResourceRegistrationEntity entry1 = new ResourceRegistrationEntity();
        ResourceRegistrationEntity entry2 = new ResourceRegistrationEntity();

        // Two entries with the same field values

        entry1.setNodeType("pap");
        entry1.setLastUpdated(new Date());
        entry1.setResourceName("myResource");
        entry1.setResourceUrl("http://nowhere.com");
        entry1.setSite("site_1");

        // Clone the first entry
        entry2 = SerializationUtils.clone(entry1);

        HashMap<Object, Object> myEntries = new HashMap<>();
        HashMap<Object, Object> theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        Set<Object> result = dbAudit.compareEntries(myEntries, theirEntries);


        // Assert that there are no mismatches returned

        assertTrue(result.isEmpty());


         /* ************************************
         * Now test with a mis-matched entry
         * ************************************/

        // Change a field on entry2

        entry2.setSite("site_1a");

        myEntries = new HashMap<>();
        theirEntries = new HashMap<>();

        myEntries.put("pdp1", entry1);
        theirEntries.put("pdp1", entry2);

        result = dbAudit.compareEntries(myEntries, theirEntries);


        //Assert that there was one mismatch

        assertEquals(1, result.size());
        logger.info("testResourceRegistrationEntity: Exit");
    }
}