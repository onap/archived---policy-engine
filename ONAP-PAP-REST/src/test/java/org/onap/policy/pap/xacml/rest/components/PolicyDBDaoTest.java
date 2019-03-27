/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.components;

import static org.junit.Assert.fail;
import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.persistence.PersistenceException;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.DataToNotifyPdp;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDao.PolicyDBDaoTestClass;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.policycontroller.PolicyCreation;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.PolicyDBException;
import org.onap.policy.rest.jpa.DatabaseLockEntity;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

public class PolicyDBDaoTest {
    private static Logger logger = FlexLogger.getLogger(PolicyDBDaoTest.class);

    static PolicyDBDaoTestClass d;
    static PolicyDBDao dbd;
    static PolicyDBDao dbd2;
    private static Path repository;
    static StdEngine stdEngine = null;
    static SessionFactory sessionFactory = null;

    @BeforeClass
    public static void init() throws PAPException, IOException {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pap.properties");
        try {
            sessionFactory = setupH2DbDaoImpl("testdbdao");
            dbd = PolicyDBDao.getPolicyDBDaoInstance();
            dbd2 = PolicyDBDao.getPolicyDBDaoInstance();
        } catch (Exception e) {
            Assert.fail();
        }

        d = PolicyDBDao.getPolicyDBDaoTestClass();
        PolicyDBDao.setJunit(true);
        repository = Paths.get("src/test/resources/pdps");
        stdEngine = new StdEngine(repository);
        dbd.setPapEngine(stdEngine);
    }

    @After
    public void cleanUp() {
        try {
            FileUtils.forceDelete(new File("src/test/resources/junitTestCreatedDirectory"));
        } catch (IOException e) {
            // could not delete
        }
    }

    public static SessionFactory setupH2DbDaoImpl(String dbName) {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pap.properties");
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.scanPackages("org.onap.*");

        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");

        sessionBuilder.addProperties(properties);
        SessionFactory sessionFac = sessionBuilder.buildSessionFactory();

        new PolicyDBDao(sessionFac);
        PolicyDbDaoTransactionInstance.setJunit(true);
        new PolicyDbDaoTransactionInstance(sessionFac);
        CommonClassDaoImpl.setSessionfactory(sessionFac);
        new DataToNotifyPdp(new CommonClassDaoImpl());
        PolicyCreation.setCommonClassDao(new CommonClassDaoImpl());

        DatabaseLockEntity lock = null;
        Session session = sessionFac.openSession();
        session.getTransaction().begin();
        lock = (DatabaseLockEntity) session.get(DatabaseLockEntity.class, 1);
        if (lock == null) {
            lock = new DatabaseLockEntity();
            lock.setKey(1);
            session.persist(lock);
            session.flush();
        }
        session.getTransaction().commit();
        session.close();

        UserInfo user = new UserInfo();
        user.setUserLoginId("API");
        user.setUserName("API");
        Session session2 = sessionFac.openSession();
        session2.getTransaction().begin();
        session2.persist(user);

        session2.getTransaction().commit();
        session2.close();

        return sessionFac;
    }


    @Test
    public void testGetConfigFile() {
        PolicyRestAdapter pra = new PolicyRestAdapter();
        pra.setConfigType(ConfigPolicy.JSON_CONFIG);
        String configFile = d.getConfigFile("Config_mypolicy.xml", "org.onap", pra);
        Assert.assertEquals("org.onap.Config_mypolicy.json", configFile);
        // yes, we can do action files too even though they don't have configs
        configFile = d.getConfigFile("Action_mypolicy.xml", "org.onap", pra);
        Assert.assertEquals("org.onap.Action_mypolicy.json", configFile);
    }

    @Test
    public void getPolicyNameAndVersionFromPolicyFileNameTest() throws PolicyDBException {
        String policyName = "com.Decision_testname.1.xml";
        String[] expectedNameAndVersion = new String[2];
        expectedNameAndVersion[0] = "com.Decision_testname";
        expectedNameAndVersion[1] = "1";
        String[] actualNameAndVersion = d.getPolicyNameAndVersionFromPolicyFileName(policyName);
        Assert.assertArrayEquals(expectedNameAndVersion, actualNameAndVersion);
    }

    @Test
    public void getNameScopeAndVersionFromPdpPolicyTest() {
        String fileName = "com.Decision_testname.1.xml";
        String[] expectedArray = new String[3];
        expectedArray[0] = "Decision_testname.1.xml";
        expectedArray[2] = "1";
        expectedArray[1] = "com";

        String[] returnArray = d.getNameScopeAndVersionFromPdpPolicy(fileName);
        Assert.assertArrayEquals(expectedArray, returnArray);
    }

    @Test
    public void getPdpPolicyNameTest() {
        String name = "Decision_testname.1.json";
        String scope = "com";
        String expectedFinalname = "com.Decision_testname.1.xml";

        String finalname = d.getPdpPolicyName(name, scope);
        Assert.assertEquals(expectedFinalname, finalname);
    }

    @Test
    public void getPolicySubFileTest() {
        String name = "Config_testname.1.json";
        String subFileType = "Config";
        HandleIncomingNotifications handle = new HandleIncomingNotifications(sessionFactory);

        Path path = handle.getPolicySubFile(name, subFileType);
        Assert.assertNull(path);
    }

    @Test
    public void createFromPolicyObject() {
        Policy policyObject = new ConfigPolicy();
        policyObject.policyAdapter = new PolicyRestAdapter();
        policyObject.policyAdapter.setConfigName("testpolicy1");
        policyObject.policyAdapter.setPolicyDescription("my description");
        policyObject.policyAdapter.setConfigBodyData("this is my test config file");
        policyObject.policyAdapter.setPolicyName("SampleTest1206");
        policyObject.policyAdapter.setConfigType(ConfigPolicy.OTHER_CONFIG);
        policyObject.policyAdapter.setPolicyType("Config");
        policyObject.policyAdapter.setDomainDir("com");
        policyObject.policyAdapter.setVersion("1");
        policyObject.policyAdapter.setHighestVersion(1);
        PolicyType policyTypeObject = new PolicyType();
        policyObject.policyAdapter.setPolicyData(policyTypeObject);
        ClassLoader classLoader = getClass().getClassLoader();
        PolicyType policyConfig = new PolicyType();
        policyConfig.setVersion("1");
        policyConfig.setPolicyId("");
        policyConfig.setTarget(new TargetType());
        policyObject.policyAdapter.setData(policyConfig);
        try {
            policyObject.policyAdapter
                    .setParentPath(IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml")));
        } catch (Exception e2) {
            fail();
        }

        PolicyDBDaoTransaction transaction = dbd.getNewTransaction();
        try {
            transaction.createPolicy(policyObject, "testuser1");
            transaction.commitTransaction();
        } catch (Exception e) {
            transaction.rollbackTransaction();
            Assert.fail();
        }

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Query policyQuery =
                session.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName=:name AND p.scope=:scope");

        policyQuery.setParameter("name", "Config_SampleTest1206.1.xml");
        policyQuery.setParameter("scope", "com");

        List<?> policyQueryList = policyQuery.list();
        PolicyEntity result = null;
        try {
            result = (PolicyEntity) policyQueryList.get(0);
        } catch (Exception e) {
            logger.error("Exception Occured " + e);
            Assert.fail();
        }
        session.getTransaction().commit();
        session.close();

        String expectedData;
        try {
            expectedData = IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml"));
        } catch (IOException e1) {
            expectedData = "";
        }
        Assert.assertEquals(expectedData, result.getPolicyData());
        result = null;

        Assert.assertFalse(transaction.isTransactionOpen());
    }

    @Test
    public void groupTransactions() {
        PolicyDBDaoTransaction group = dbd.getNewTransaction();
        String groupName = "test group 1";
        try {
            group.createGroup(PolicyDBDao.createNewPDPGroupId(groupName), groupName, "this is a test group",
                    "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        Query getGroup =
                session.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup.setParameter("groupId", PolicyDBDao.createNewPDPGroupId(groupName));
        getGroup.setParameter("deleted", false);
        List<?> groups = getGroup.list();
        GroupEntity groupEntity = (GroupEntity) groups.get(0);
        Assert.assertEquals(groupName, groupEntity.getgroupName());
        Assert.assertEquals("this is a test group", groupEntity.getDescription());
        session.getTransaction().commit();
        session.close();

        group = dbd.getNewTransaction();
        try {
            OnapPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId(groupName), Paths.get("/"));
            group.deleteGroup(groupToDelete, null, "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }
        Session session2 = sessionFactory.openSession();
        session2.getTransaction().begin();
        Query getGroup2 =
                session2.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup2.setParameter("groupId", PolicyDBDao.createNewPDPGroupId(groupName));
        getGroup2.setParameter("deleted", false);
        List<?> groups2 = getGroup2.list();
        groups2 = getGroup2.list();
        if (groups2.size() != 0) {
            System.out.println("Group size: " + groups2.size());
            Assert.fail();
        }
        session2.getTransaction().commit();
        session2.close();


        // add a pdp to a group
        group = dbd.getNewTransaction();
        try {
            group.createGroup(PolicyDBDao.createNewPDPGroupId(groupName), groupName, "test group", "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        group = dbd.getNewTransaction();
        try {
            group.addPdpToGroup("http://localhost:4344/pdp/", PolicyDBDao.createNewPDPGroupId(groupName), "primary",
                    "the main pdp", 3232, "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        Session session3 = sessionFactory.openSession();
        session3.getTransaction().begin();
        Query getPdp = session3.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
        getPdp.setParameter("pdpId", "http://localhost:4344/pdp/");
        getPdp.setParameter("deleted", false);
        List<?> pdps = getPdp.list();
        if (pdps.size() != 1) {
            System.out.println("Group size: " + pdps.size());
            Assert.fail();
        }
        PdpEntity pdp = (PdpEntity) pdps.get(0);
        Assert.assertEquals(groupName, pdp.getGroup().getgroupName());
        Assert.assertEquals(pdp.getPdpName(), "primary");
        session3.getTransaction().commit();
        session3.close();


        group = dbd.getNewTransaction();
        try {
            group.removePdpFromGroup("http://localhost:4344/pdp/", "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        Session session4 = sessionFactory.openSession();
        session4.getTransaction().begin();
        Query getPdp2 = session4.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
        getPdp2.setParameter("pdpId", "http://localhost:4344/pdp/");
        getPdp2.setParameter("deleted", false);
        List<?> pdps2 = getPdp2.list();
        if (pdps2.size() != 0) {
            System.out.println("Group size: " + pdps2.size());
            Assert.fail();
        }

        session4.getTransaction().commit();
        session4.close();

        // add some pdps to groups
        group = dbd.getNewTransaction();
        try {
            group.createGroup(PolicyDBDao.createNewPDPGroupId("testgroup1"), "testgroup1", "test group", "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }
        group = dbd.getNewTransaction();
        try {
            group.createGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"), "testgroup2", "test group", "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        group = dbd.getNewTransaction();
        try {
            group.addPdpToGroup("http://localhost:4344/pdp/", PolicyDBDao.createNewPDPGroupId("testgroup1"), "primary",
                    "the main pdp", 3232, "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }
        group = dbd.getNewTransaction();
        try {
            group.addPdpToGroup("http://localhost:4345/pdp/", PolicyDBDao.createNewPDPGroupId("testgroup1"),
                    "secondary", "the second pdp", 3233, "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        Session session5 = sessionFactory.openSession();
        session5.getTransaction().begin();
        Query getPdp3 = session5.createQuery("SELECT p FROM PdpEntity p WHERE p.deleted=:deleted");
        getPdp3.setParameter("deleted", false);
        List<?> pdps3 = getPdp3.list();
        for (Object obj : pdps3) {
            Assert.assertEquals("testgroup1", ((PdpEntity) obj).getGroup().getgroupName());
        }

        session5.getTransaction().commit();
        session5.close();


        group = dbd.getNewTransaction();
        try {
            OnapPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup1"), Paths.get("/"));
            OnapPDPGroup groupToMoveTo = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"), Paths.get("/"));
            group.deleteGroup(groupToDelete, groupToMoveTo, "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        Session session6 = sessionFactory.openSession();
        session6.getTransaction().begin();
        Query getGroup3 =
                session6.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup3.setParameter("groupId", "testgroup1");
        getGroup3.setParameter("deleted", false);
        List<?> groups3 = getGroup3.list();
        if (groups3.size() != 0) {
            System.out.println("Group size: " + groups3.size());
            Assert.fail();
        }

        session6.getTransaction().commit();
        session6.close();

        Session session7 = sessionFactory.openSession();
        session7.getTransaction().begin();
        Query getPdp4 = session7.createQuery("SELECT p FROM PdpEntity p WHERE p.deleted=:deleted");
        getPdp4.setParameter("deleted", false);
        List<?> pdps4 = getPdp4.list();
        for (Object obj : pdps4) {
            Assert.assertEquals("testgroup2", ((PdpEntity) obj).getGroup().getgroupName());
        }

        session7.getTransaction().commit();
        session7.close();


        group = dbd.getNewTransaction();
        try {
            OnapPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"), Paths.get("/"));
            OnapPDPGroup groupToMoveTo = null;
            group.deleteGroup(groupToDelete, groupToMoveTo, "testuser");
            group.commitTransaction();
            Assert.fail();
        } catch (PolicyDBException pe) {
            // good, can't delete group with pdps
            group.rollbackTransaction();
        } catch (Exception e) {
            group.rollbackTransaction();
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        // update group
        OnapPDPGroup pdpGroup =
                new StdPDPGroup("testgroup2", false, "newtestgroup2", "this is my new description", Paths.get("/"));
        group = dbd.getNewTransaction();
        try {
            group.updateGroup(pdpGroup, "testuser", "testuser");
            group.commitTransaction();
        } catch (Exception e) {
            logger.error("Exception Occured" + e);
            group.rollbackTransaction();
            Assert.fail();
        }

        Session session8 = sessionFactory.openSession();
        session8.getTransaction().begin();
        Query getGroup4 =
                session8.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup4.setParameter("groupId", "newtestgroup2");
        getGroup4.setParameter("deleted", false);
        List<?> groups4 = getGroup4.list();
        if (groups4.size() != 1) {
            System.out.println("Group size: " + groups4.size());
            Assert.fail();
        }

        session8.getTransaction().commit();
        session8.close();

        Session session9 = sessionFactory.openSession();
        session9.getTransaction().begin();
        Query getGroup5 =
                session9.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup5.setParameter("groupId", "testgroup2");
        getGroup5.setParameter("deleted", false);
        List<?> groups5 = getGroup5.list();
        if (groups5.size() != 0) {
            System.out.println("Group size: " + groups5.size());
            Assert.fail();
        }

        session9.getTransaction().commit();
        session9.close();
    }

    @Ignore
    @Test
    public void threadingStabilityTest() {
        if (logger.isDebugEnabled()) {
            logger.debug("\n\n****************************" + "threadingStabilityTest() entry"
                    + "******************************\n\n");
        }

        PolicyDBDaoTransaction t = dbd.getNewTransaction();
        Assert.assertTrue(t.isTransactionOpen());
        try {
            // Add 1000 ms to the timeout just to be sure it actually times out
            int sleepTime =
                    Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT)) + 1000;
            if (logger.isDebugEnabled()) {
                Date date = new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   sleepTime =  " + sleepTime
                        + "\n   TimeStamp = " + date.getTime() + "\n\n");
            }
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            logger.error("Exception Occured" + e);
        }
        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug(
                    "\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   Assert.assertFalse(t.isTransactionOpen() = "
                            + t.isTransactionOpen() + ")" + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        Assert.assertFalse(t.isTransactionOpen());


        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   a = dbd.getNewTransaction() "
                    + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        PolicyDBDaoTransaction a = dbd.getNewTransaction();
        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug(
                    "\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   Assert.assertTrue(a.isTransactionOpen() = "
                            + a.isTransactionOpen() + ")" + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        Assert.assertTrue(a.isTransactionOpen());

        try {
            // Add 1000 ms to the timeout just to be sure it actually times out
            int sleepTime =
                    Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT)) + 1000;
            if (logger.isDebugEnabled()) {
                Date date = new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   sleepTime =  " + sleepTime
                        + "\n   TimeStamp = " + date.getTime() + "\n\n");
            }
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            logger.error("Exception Occured" + e);
        }
        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   b = dbd.getNewTransaction() "
                    + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        PolicyDBDaoTransaction b = dbd.getNewTransaction();
        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug(
                    "\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   Assert.assertFalse(a.isTransactionOpen() = "
                            + a.isTransactionOpen() + ")" + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        Assert.assertFalse(a.isTransactionOpen());
        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug(
                    "\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   Assert.assertTrue(b.isTransactionOpen() = "
                            + b.isTransactionOpen() + ")" + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        Assert.assertTrue(b.isTransactionOpen());
        b.close();



        // Now let's test the transaction wait time timeout. Shorten the wait time to 1000 ms
        System.setProperty(XACMLRestProperties.PROP_PAP_TRANS_WAIT, "1000");
        // And let's lengthen the transaction timeout to 5000 ms
        System.setProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT, "5000");
        // get a transacton
        PolicyDBDaoTransaction t1 = dbd.getNewTransaction();
        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug(
                    "\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   Assert.assertTrue(t1.isTransactionOpen() = "
                            + t1.isTransactionOpen() + ")" + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
        Assert.assertTrue(t1.isTransactionOpen());
        // while it is open, get another from a different DB Dao so it will not collide on the synchronized
        // code segment
        // but will collide at the DB. Remember that the wait time is only 1000 ms
        try {
            // Now the 2nd transaction has a wait timeout in 1000 ms
            PolicyDBDaoTransaction t2 = dbd2.getNewTransaction();
            /*
             * Give it plenty of time to time out the second transaction It will actually hang right here until
             * it either gets the lock from the DB or the request for the DB lock times out. The timers are very
             * sloppy so, I have given this plenty of leeway.
             */

            if (logger.isDebugEnabled()) {
                Date date = new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() " + "\n   Thread.sleep(3000)"
                        + "\n   TimeStamp = " + date.getTime() + "\n\n");
            }
            Thread.sleep(3000);
            if (logger.isDebugEnabled()) {
                Date date = new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   Assert.assertTrue(t1.isTransactionOpen() = " + t1.isTransactionOpen() + ")"
                        + "\n   Assert.assertFalse(t2.isTransactionOpen() = " + t2.isTransactionOpen() + ")"
                        + "\n   TimeStamp = " + date.getTime() + "\n\n");
            }
            // Assert.assertTrue(t1.isTransactionOpen());
            // Assert.assertFalse(t2.isTransactionOpen());

            Assert.fail("\n\nTransaction timeout of 1000 ms exceeded without a PersistenceException\n\n");
        } catch (PersistenceException e) {
            // success
            if (logger.isDebugEnabled()) {
                Date date = new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   SUCCESS! Transaction Wait Timeout worked!" + "\n   Caught PersistenceException = " + e
                        + "\n   TimeStamp = " + date.getTime() + "\n\n");
            }
        } catch (Exception e) {
            // failure due to some other reason
            if (logger.isDebugEnabled()) {
                Date date = new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() FAILURE" + "\n   Caught Exception = " + e
                        + "\n   TimeStamp = " + date.getTime() + "\n\n");
            }
            logger.error("Exception Occured" + e);
            Assert.fail();
        }

        if (logger.isDebugEnabled()) {
            Date date = new java.util.Date();
            logger.debug("\n\nthreadingStabilityTest() exit" + "\n   TimeStamp = " + date.getTime() + "\n\n");
        }
    }

}
