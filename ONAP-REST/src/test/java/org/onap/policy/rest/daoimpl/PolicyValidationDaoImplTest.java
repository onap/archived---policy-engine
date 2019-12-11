/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (c) 2019 Nokia
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

package org.onap.policy.rest.daoimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.script.SimpleBindings;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.h2.tools.Server;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyRoles;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.SystemLogDb;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.WatchPolicyNotificationTable;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class PolicyValidationDaoImplTest {

    private static Logger logger = FlexLogger.getLogger(PolicyValidationDaoImplTest.class);

    static SessionFactory sessionFactory;
    static Server server;
    static PolicyValidationDaoImpl commonClassDao;

    /**
     * Set up all unit tests.
     *
     * @throws SQLException on SQL exceptions
     */
    @BeforeClass
    public static void setupAll() throws SQLException {
        System.setProperty("h2.bindAddress", "localhost");

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
        properties.put("hibernate.hbm2ddl.auto", "drop");
        properties.put("hibernate.hbm2ddl.auto", "create");

        sessionBuilder.addProperties(properties);
        sessionFactory = sessionBuilder.buildSessionFactory();

        // Set up dao with SessionFactory
        commonClassDao = new PolicyValidationDaoImpl();
        PolicyValidationDaoImpl.setSessionfactory(sessionFactory);
        // PolicyController.setLogTableLimit("1");
        // HibernateSession.setSession(sessionFactory);
        SystemLogDb data1 = new SystemLogDb();
        data1.setDate(new Date());
        data1.setLogtype("INFO");
        data1.setRemote("Test");
        data1.setSystem("Test");
        data1.setType("Test");
        SystemLogDb data2 = new SystemLogDb();
        data2.setDate(new Date());
        data2.setLogtype("error");
        data2.setRemote("Test");
        data2.setSystem("Test");
        data2.setType("Test");

        // Create TCP server for troubleshooting
        server = Server.createTcpServer("-tcpAllowOthers").start();
        System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:test");
    }

    @AfterClass
    public static void deleteDB() {
        sessionFactory.close();
        server.stop();
    }

    @After
    public void tearDown() {
        truncateAllTables();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDB() {
        // Add data
        UserInfo userinfo = new UserInfo();
        userinfo.setUserLoginId("Test");
        userinfo.setUserName("Test");
        commonClassDao.save(userinfo);
        OnapName onapName = new OnapName();
        onapName.setName("Test");
        onapName.setUserCreatedBy(userinfo);
        onapName.setUserModifiedBy(userinfo);
        onapName.setModifiedDate(new Date());
        commonClassDao.save(onapName);

        List<Object> list = commonClassDao.getData(OnapName.class);
        assertTrue(list.size() == 1);
        logger.debug(list.size());
        logger.debug(list.get(0));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUser() {
        // Add data
        UserInfo userinfo = new UserInfo();
        String loginIdUserName = "Test";
        userinfo.setUserLoginId(loginIdUserName);
        userinfo.setUserName(loginIdUserName);
        commonClassDao.save(userinfo);

        List<Object> dataCur = commonClassDao.getDataByQuery("from UserInfo", new SimpleBindings());

        assertEquals(1, dataCur.size());
        UserInfo cur = (UserInfo) dataCur.get(0);
        assertEquals(loginIdUserName, cur.getUserLoginId());
        assertEquals(loginIdUserName, cur.getUserName());

        assertFalse(dataCur.isEmpty());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_DashboardController() {
        // Add data
        PolicyEntity pe = new PolicyEntity();
        String name = "TestPolicy";
        pe.setPolicyName(name);
        pe.setPolicyData("dummyData");
        pe.prePersist();
        pe.setScope("dummyScope");
        pe.setDescription("descr");
        pe.setDeleted(false);
        pe.setCreatedBy("Test");
        commonClassDao.save(pe);

        List<Object> dataCur = commonClassDao.getDataByQuery("from PolicyEntity", new SimpleBindings());

        assertTrue(1 == dataCur.size());
        assertTrue(dataCur.get(0) instanceof PolicyEntity);
        assertEquals(name, ((PolicyEntity) dataCur.get(0)).getPolicyName());
        assertEquals(pe, (dataCur.get(0)));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_AutoPushController() {
        // Add data
        PolicyVersion pv = new PolicyVersion();
        pv.setActiveVersion(2);
        pv.setPolicyName("myPname");
        pv.prePersist();
        pv.setCreatedBy("Test");
        pv.setModifiedBy("Test");

        PolicyVersion pv2 = new PolicyVersion();
        pv2.setActiveVersion(1);
        pv2.setPolicyName("test");
        pv2.prePersist();
        pv2.setCreatedBy("Test");
        pv2.setModifiedBy("Test");

        commonClassDao.save(pv);
        commonClassDao.save(pv2);

        String scope = "my";
        scope += "%";
        String query = "From PolicyVersion where policy_name like :scope and id > 0";
        SimpleBindings params = new SimpleBindings();
        params.put("scope", scope);
        List<Object> dataCur = commonClassDao.getDataByQuery(query, params);

        assertTrue(1 == dataCur.size());
        assertEquals(pv, dataCur.get(0));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationMail() {
        // Add data
        WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
        watch.setLoginIds("Test");

        // Add data
        UserInfo userinfo = new UserInfo();
        String loginIdUserName = "Test";
        userinfo.setUserLoginId(loginIdUserName);
        userinfo.setUserName(loginIdUserName);
        commonClassDao.save(userinfo);

        List<Object> dataCur = commonClassDao.getDataByQuery("from UserInfo", new SimpleBindings());

        assertEquals(1, dataCur.size());
        UserInfo cur = (UserInfo) dataCur.get(0);
        assertEquals(loginIdUserName, cur.getUserLoginId());
        assertEquals(loginIdUserName, cur.getUserName());

        assertFalse(dataCur.isEmpty());

        watch.setPolicyName("bananaWatch");
        commonClassDao.save(watch);

        String policyFileName = "banana";
        if (policyFileName.contains("/")) {
            policyFileName = policyFileName.substring(0, policyFileName.indexOf("/"));
            policyFileName = policyFileName.replace("/", File.separator);
        }
        if (policyFileName.contains("\\")) {
            policyFileName = policyFileName.substring(0, policyFileName.indexOf("\\"));
            policyFileName = policyFileName.replace("\\", "\\\\");
        }

        // Current Implementation
        policyFileName += "%";
        String query = "from WatchPolicyNotificationTable where policyName like:policyFileName";
        SimpleBindings params = new SimpleBindings();
        params.put("policyFileName", policyFileName);
        dataCur = commonClassDao.getDataByQuery(query, params);

        // Assertions
        assertTrue(dataCur.size() == 1);
        assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
        assertEquals(watch, dataCur.get(0));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyController() {
        // Add data
        PolicyEntity pe = new PolicyEntity();
        String name = "actionDummy";
        pe.setPolicyName(name);
        pe.setPolicyData("dummyData");
        pe.prePersist();
        pe.setScope("dummyScope");
        pe.setDescription("descr");
        pe.setDeleted(false);
        pe.setCreatedBy("Test");
        commonClassDao.save(pe);

        String dbCheckName = "dummyScope:action";
        String[] splitDbCheckName = dbCheckName.split(":");

        // Current Implementation
        String query = "FROM PolicyEntity where policyName like :splitDBCheckName1 and scope = :splitDBCheckName0";
        SimpleBindings params = new SimpleBindings();
        params.put("splitDBCheckName1", splitDbCheckName[1] + "%");
        params.put("splitDBCheckName0", splitDbCheckName[0]);
        List<Object> dataCur = commonClassDao.getDataByQuery(query, params);

        // Assertions
        assertTrue(dataCur.size() == 1);
        assertTrue(dataCur.get(0) instanceof PolicyEntity);
        assertEquals(pe, dataCur.get(0));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationController() {
        // Add data
        WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
        String finalName = "banana"; // Policy File Name
        String userId = "Test";
        watch.setLoginIds(userId);
        watch.setPolicyName(finalName);
        commonClassDao.save(watch);

        // Current Implementation
        String query = "from WatchPolicyNotificationTable where POLICYNAME = :finalName and LOGINIDS = :userId";
        SimpleBindings params = new SimpleBindings();
        params.put("finalName", finalName);
        params.put("userId", userId);
        List<Object> dataCur = commonClassDao.getDataByQuery(query, params);

        // Assertions
        assertTrue(dataCur.size() == 1);
        assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
        assertEquals(watch, dataCur.get(0));

        WatchPolicyNotificationTable table0 = new WatchPolicyNotificationTable();
        WatchPolicyNotificationTable table1 = new WatchPolicyNotificationTable();
        assertEquals(table0, table0);
        assertEquals(table0, table1);
        assertNotEquals(table0, null);
        String helloString = "Hello";
        Object helloObject = helloString;
        assertNotEquals(table0, helloObject);

        table0.setId(1);
        assertNotEquals(table0, table1);
        table1.setId(1);
        assertEquals(table0, table1);

        table0.setPolicyName("GoToOz");
        assertNotEquals(table0, table1);
        table1.setPolicyName("GoToOz");
        assertEquals(table0, table1);
        table1.setPolicyName(null);
        assertNotEquals(table0, table1);
        table0.setPolicyName(null);
        assertEquals(table0, table1);
        table1.setPolicyName("GoToOz");
        assertNotEquals(table0, table1);
        table0.setPolicyName("GoToOz");
        assertEquals(table0, table1);
        assertEquals(table0, table1);
        table1.setPolicyName("InOz");
        assertNotEquals(table0, table1);
        table0.setPolicyName("InOz");
        assertEquals(table0, table1);

        table0.setLoginIds("Wizard");
        assertNotEquals(table0, table1);
        table1.setLoginIds("Wizard");
        assertEquals(table0, table1);
        table1.setLoginIds(null);
        assertNotEquals(table0, table1);
        table0.setLoginIds(null);
        assertEquals(table0, table1);
        table0.setLoginIds("Wizard");
        assertNotEquals(table0, table1);
        table1.setLoginIds("Wizard");
        assertEquals(table0, table1);
        table1.setLoginIds(null);
        table1.setLoginIds("Witch");
        assertNotEquals(table0, table1);
        table0.setLoginIds("Witch");
        assertEquals(table0, table1);

        assertNotNull(table0.hashCode());
        assertNotNull(table1.hashCode());
        assertEquals(table0.hashCode(), table0.hashCode());

        table0.setLoginIds("Witci");
        assertNotEquals(table0, table1);
        assertNotEquals(table0.hashCode(), table1.hashCode());
    }

    /*
     * Test for SQL Injection Protection
     */

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationController_Injection() {
        // Add data
        WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
        String userId = "Test";
        watch.setLoginIds(userId);
        watch.setPolicyName("banana");
        commonClassDao.save(watch);

        WatchPolicyNotificationTable watch2 = new WatchPolicyNotificationTable();
        watch2.setLoginIds(userId);
        watch2.setPolicyName("banana2");
        commonClassDao.save(watch2);

        // SQL Injection attempt
        String finalName = "banana' OR '1'='1";

        // Current Implementation
        String query = "from WatchPolicyNotificationTable where POLICYNAME = :finalName and LOGINIDS = :userId";
        SimpleBindings params = new SimpleBindings();
        params.put("finalName", finalName);
        params.put("userId", userId);
        List<Object> dataCur = commonClassDao.getDataByQuery(query, params);

        // Assertions
        assertTrue(dataCur.size() <= 1);

        if (dataCur.size() >= 1) {
            assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
            assertFalse(watch.equals(dataCur.get(0)));
            assertFalse(watch.equals(dataCur.get(0)));
        }
    }

    @Test
    public void testCommonClassDaoImplMethods() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        List<Object> data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);
        UserInfo userInfoUpdate = (UserInfo) data.get(0);
        userInfoUpdate.setUserName("Test1");
        commonClassDao.update(userInfoUpdate);
        List<String> data1 = commonClassDao.getDataByColumn(UserInfo.class, "userLoginId");
        assertTrue(data1.size() == 1);
        UserInfo data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId:userName",
                        "TestID:Test1");
        assertTrue("TestID".equals(data2.getUserLoginId()));
        List<Object> data3 = commonClassDao.checkDuplicateEntry("TestID:Test1", "userLoginId:userName", UserInfo.class);
        assertTrue(data3.size() == 1);
        PolicyRoles roles = new PolicyRoles();
        roles.setRole("admin");
        roles.setLoginId(userInfo);
        roles.setScope("test");
        commonClassDao.save(roles);
        List<PolicyRoles> roles1 = commonClassDao.getUserRoles();
        assertTrue(roles1.size() == 1);
        List<String> multipleData = new ArrayList<>();
        multipleData.add("TestID:Test1");
        List<Object> data4 = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, "userLoginId:userName",
                        multipleData);
        assertTrue(data4.size() == 1);
        commonClassDao.delete(data2);
    }

    @Test
    public void testGetDataByIdparameters() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        List<Object> data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);
        data = commonClassDao.getDataById(UserInfo.class, null, null);
        assertNull(data);
        data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", null);
        assertNull(data);
        data = commonClassDao.getDataById(UserInfo.class, null, "TestID:Test");
        assertNull(data);
        data = commonClassDao.getDataById(UserInfo.class, "userLoginIduserName", "TestID:Test");
        assertNull(data);
        data = commonClassDao.getDataById(UserInfo.class, "userLoginIduserName", "TestIDTest");
        assertNull(data);
        data = commonClassDao.getDataById(UserInfo.class, "userLoginId   data2.getUserLoginId()" + ":userName",
                        "TestIDTest");
        assertNull(data);
        commonClassDao.delete(data);
    }

    @Test
    public void testGetDataByColumnParameters() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        List<String> data = commonClassDao.getDataByColumn(UserInfo.class, "userLoginId");
        assertTrue(data.size() == 1);
        data = commonClassDao.getDataByColumn(null, null);
        assertNull(data);
        data = commonClassDao.getDataByColumn(UserInfo.class, null);
        assertNull(data);
        data = commonClassDao.getDataByColumn(null, "userLoginId");
        assertNull(data);
        commonClassDao.delete(data);
    }

    @Test
    public void testGetMultipleDataOnAddingConjunctionParameters() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        assertEquals("Test", userInfo.getIdentiferByUserId().getUri().getPath());
        commonClassDao.save(userInfo);
        List<String> multipleData = new ArrayList<>();
        multipleData.add("TestID:Test1");
        List<Object> data = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, "userLoginId:userName",
                        multipleData);
        assertTrue(data.size() == 0);
        data = commonClassDao.getMultipleDataOnAddingConjunction(null, null, null);
        assertNull(data);
        data = commonClassDao.getMultipleDataOnAddingConjunction(null, null, multipleData);
        assertNull(data);
        data = commonClassDao.getMultipleDataOnAddingConjunction(null, "userLoginId:userName", null);
        assertNull(data);
        data = commonClassDao.getMultipleDataOnAddingConjunction(null, "userLoginId:userName", multipleData);
        assertNull(data);
        data = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, null, null);
        assertNull(data);
        data = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, null, multipleData);
        assertNull(data);
        data = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, "userLoginId:userName", null);
        assertNull(data);
        commonClassDao.delete(data);
    }

    @Test
    public void testCheckDuplicateEntryParameters() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        List<Object> data = commonClassDao.checkDuplicateEntry("TestID:Test1", "userLoginId:userName", UserInfo.class);
        assertTrue(data.size() == 0);
        data = commonClassDao.checkDuplicateEntry(null, null, UserInfo.class);
        assertNull(data);
        data = commonClassDao.checkDuplicateEntry("userLoginId:userName", null, UserInfo.class);
        assertNull(data);
        data = commonClassDao.checkDuplicateEntry(null, "TestID:Test", UserInfo.class);
        assertNull(data);
        data = commonClassDao.checkDuplicateEntry("userLoginIduserName", "TestID:Test", UserInfo.class);
        assertNull(data);
        data = commonClassDao.checkDuplicateEntry("userLoginId:userName", "TestID:Test:zooby", UserInfo.class);
        assertNull(data);
        data = commonClassDao.checkDuplicateEntry("userLoginId:userName", "TestID", UserInfo.class);
        assertNull(data);
        commonClassDao.delete(data);
    }

    @Test
    public void testGetEntityItemParameters() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        List<Object> data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);
        UserInfo userInfoUpdate = (UserInfo) data.get(0);
        userInfoUpdate.setUserName("Test1");
        commonClassDao.update(userInfoUpdate);
        List<String> data1 = commonClassDao.getDataByColumn(UserInfo.class, "userLoginId");
        assertTrue(data1.size() == 1);
        UserInfo data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId:userName",
                        "TestID:Test1");
        assertTrue("TestID".equals(data2.getUserLoginId()));
        data2 = (UserInfo) commonClassDao.getEntityItem(null, null, null);
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(null, null, "TestID:Test1");
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(null, "userLoginId:userName", null);
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(null, "userLoginId:userName", "TestID:Test1");
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, null, null);
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, null, "TestID:Test1");
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId:userName", null);
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginIduserName", "TestID:Test1");
        assertNull(data2);
        data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId:userName", "TestIDTest1");
        assertNull(data2);
        commonClassDao.delete(data);
        commonClassDao.delete(data1);
        commonClassDao.delete(data2);
    }

    @Test
    public void testOtherMethods() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        commonClassDao.deleteAll();
        List<Object> data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);

        data = commonClassDao.checkExistingGroupListforUpdate(null, null);
        assertTrue(data.size() == 0);

        commonClassDao.updateClAlarms(null, null);
        commonClassDao.updateClYaml(null, null);
        data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);
        commonClassDao.update(null);
        assertTrue(data.size() == 1);
        commonClassDao.getData(null);
        assertTrue(data.size() == 1);
        commonClassDao.delete(data);
    }

    @Test
    public void testUpdateQuery() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserLoginId("TestID");
        userInfo.setUserName("Test");
        commonClassDao.save(userInfo);
        commonClassDao.updateQuery("SELECT * FROM userLoginId");
        List<Object> data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);

        String query = "DELETE FROM org.onap.policy.rest.jpa.FunctionDefinition";
        commonClassDao.updateQuery(query);
        data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertTrue(data.size() == 1);
        commonClassDao.delete(data);
    }

    @Test
    public void testGetDataByQueryParameters() {
        // Add data
        UserInfo userinfo = new UserInfo();
        String loginIdUserName = "Test";
        userinfo.setUserLoginId(loginIdUserName);
        userinfo.setUserName(loginIdUserName);
        commonClassDao.save(userinfo);

        SimpleBindings bindings = new SimpleBindings();
        bindings.put("usercode", 1L);

        try {
            commonClassDao.getDataByQuery("from UserInfo", bindings);
            fail("test should throw an exception here");
        } catch (Exception exc) {
            assertTrue(exc.getMessage().contains("Parameter usercode does not exist as a named parameter"));
        }
    }

    private void truncateAllTables() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        sessionFactory.getAllClassMetadata().forEach((tableName, x) -> {
            Query query = session.createQuery("DELETE FROM " + tableName);
            query.executeUpdate();
        });
        transaction.commit();
        session.close();
    }
}
