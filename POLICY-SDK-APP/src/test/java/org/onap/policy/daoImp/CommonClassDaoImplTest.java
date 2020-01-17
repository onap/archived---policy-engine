/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017, 2019-2020 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

package org.onap.policy.daoImp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.script.SimpleBindings;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.h2.tools.Server;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.conf.HibernateSession;
import org.onap.policy.controller.PolicyController;
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

public class CommonClassDaoImplTest {

    private static Logger logger = FlexLogger.getLogger(CommonClassDaoImplTest.class);

    SessionFactory sessionFactory;
    Server server;
    CommonClassDaoImpl commonClassDao;

    /**
     * setUp.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
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
            commonClassDao = new CommonClassDaoImpl();
            CommonClassDaoImpl.setSessionfactory(sessionFactory);
            PolicyController.setLogTableLimit("1");
            HibernateSession.setSession(sessionFactory);
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
            HibernateSession.getSession().save(data1);
            HibernateSession.getSession().save(data2);
            // Create TCP server for troubleshooting
            server = Server.createTcpServer("-tcpAllowOthers").start();
            System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:test");

        } catch (Exception e) {
            System.err.println(e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDB() {
        try {
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

            List<?> list = commonClassDao.getData(OnapName.class);
            assertTrue(list.size() == 1);
            logger.debug(list.size());
            logger.debug(list.get(0));
        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUser() {
        try {
            // Add data
            UserInfo userinfo = new UserInfo();
            String loginIdUserName = "Test";
            userinfo.setUserLoginId(loginIdUserName);
            userinfo.setUserName(loginIdUserName);
            commonClassDao.save(userinfo);

            List<?> dataCur = commonClassDao.getDataByQuery("from UserInfo", new SimpleBindings());

            assertEquals(1, dataCur.size());
            UserInfo cur = (UserInfo) dataCur.get(0);
            assertEquals(loginIdUserName, cur.getUserLoginId());
            assertEquals(loginIdUserName, cur.getUserName());

            assertFalse(dataCur.isEmpty());

        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_DashboardController() {
        try {
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

            List<?> dataCur = commonClassDao.getDataByQuery("from PolicyEntity", new SimpleBindings());

            assertTrue(1 == dataCur.size());
            assertTrue(dataCur.get(0) instanceof PolicyEntity);
            assertEquals(name, ((PolicyEntity) dataCur.get(0)).getPolicyName());
            assertEquals(pe, (dataCur.get(0)));

        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_AutoPushController() {
        try {
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
            List<?> dataCur = commonClassDao.getDataByQuery(query, params);

            assertTrue(1 == dataCur.size());
            assertEquals(pv, dataCur.get(0));

        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationMail() {
        try {
            // Add data
            WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
            watch.setLoginIds("Test");
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
            List<?> dataCur = commonClassDao.getDataByQuery(query, params);

            // Assertions
            assertTrue(dataCur.size() == 1);
            assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
            assertEquals(watch, dataCur.get(0));

        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyController() {
        try {
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

            // Current Implementation
            String query = "FROM PolicyEntity where policyName like :splitDBCheckName1 and scope = :splitDBCheckName0";
            SimpleBindings params = new SimpleBindings();
            String[] splitDbCheckName = dbCheckName.split(":");
            params.put("splitDBCheckName1", splitDbCheckName[1] + "%");
            params.put("splitDBCheckName0", splitDbCheckName[0]);
            List<?> dataCur = commonClassDao.getDataByQuery(query, params);

            // Assertions
            assertTrue(dataCur.size() == 1);
            assertTrue(dataCur.get(0) instanceof PolicyEntity);
            assertEquals(pe, dataCur.get(0));

        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationController() {
        try {
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
            List<?> dataCur = commonClassDao.getDataByQuery(query, params);

            // Assertions
            assertTrue(dataCur.size() == 1);
            assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
            assertEquals(watch, dataCur.get(0));

        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    /*
     * Test for SQL Injection Protection
     */

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationController_Injection() {
        try {
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
            List<?> dataCur = commonClassDao.getDataByQuery(query, params);

            // Assertions
            assertTrue(dataCur.size() <= 1);

            if (dataCur.size() >= 1) {
                assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
                assertFalse(watch.equals(dataCur.get(0)));
                assertFalse(watch.equals(dataCur.get(0)));
            }
        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    public void testCommonClassDaoImplMethods() {
        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserLoginId("TestID");
            userInfo.setUserName("Test");
            commonClassDao.save(userInfo);
            List<?> data = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
            assertTrue(data.size() == 1);
            UserInfo userInfoUpdate = (UserInfo) data.get(0);
            userInfoUpdate.setUserName("Test1");
            commonClassDao.update(userInfoUpdate);
            List<String> data1 = commonClassDao.getDataByColumn(UserInfo.class, "userLoginId");
            assertTrue(data1.size() == 1);
            UserInfo data2 =
                    (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId:userName", "TestID:Test1");
            assertTrue("TestID".equals(data2.getUserLoginId()));
            List<?> data3 = commonClassDao.checkDuplicateEntry("TestID:Test1", "userLoginId:userName", UserInfo.class);
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
            List<?> data4 = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, "userLoginId:userName",
                    multipleData);
            assertTrue(data4.size() == 1);
            commonClassDao.delete(data2);
        } catch (Exception e) {
            logger.debug("Exception Occured" + e);
            fail();
        }
    }

    @Test
    public final void testGetLoggingData() {
        SystemLogDbDaoImpl system = new SystemLogDbDaoImpl();
        PolicyController.setjUnit(true);
        try {
            assertTrue(system.getLoggingData() != null);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public final void testGetSystemAlertData() {
        SystemLogDbDaoImpl system = new SystemLogDbDaoImpl();
        PolicyController.setjUnit(true);
        try {
            assertTrue(system.getSystemAlertData() != null);
        } catch (Exception e) {
            fail();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExceptions() throws HibernateException {
        SessionFactory sfMock = Mockito.mock(SessionFactory.class);
        Session mockSession = Mockito.mock(Session.class);
        Criteria crMock = Mockito.mock(Criteria.class);
        Transaction mockTransaction = Mockito.mock(Transaction.class);

        CommonClassDaoImpl.setSessionfactory(sfMock);

        when(sfMock.openSession()).thenReturn(mockSession);
        when(mockSession.createCriteria(OnapName.class)).thenReturn(crMock);

        when(crMock.list()).thenThrow(HibernateException.class);
        when(mockSession.close()).thenThrow(HibernateException.class);

        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        doThrow(HibernateException.class).when(mockTransaction).commit();

        List<?> dataList = commonClassDao.getData(OnapName.class);
        assertNull(dataList);

        List<?> dataByIdList = commonClassDao.getDataById(UserInfo.class, "userLoginId:userName", "TestID:Test");
        assertNull(dataByIdList);

        commonClassDao.save(null);
        commonClassDao.delete(null);
        commonClassDao.update(null);

        List<?> dupEntryList =
                commonClassDao.checkDuplicateEntry("TestID:Test", "userLoginId:userName", UserInfo.class);
        assertNull(dupEntryList);

        List<PolicyRoles> userRoles = commonClassDao.getUserRoles();
        assertNull(userRoles);

        Object entityItem = commonClassDao.getEntityItem(UserInfo.class, "testColName", "testKey");
        assertNull(entityItem);

        commonClassDao.updateQuery("testQueryString");

        List<String> dataByColumn = commonClassDao.getDataByColumn(UserInfo.class, "testColName");
        assertNull(dataByColumn);

        List<?> entityData = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, "", null);
        assertNull(entityData);
    }

    @Test
    public void testCheckExistingGroupListforUpdate() {
        Object retObj = commonClassDao.checkExistingGroupListforUpdate("testString1", "testString2");
        assertNotNull(retObj);
        assertTrue(retObj instanceof List);
        List<?> retList = (List<?>) retObj;
        assertTrue(retList.isEmpty());
    }

    @Test
    public void testEmptyMethods() {
        commonClassDao.deleteAll();
        commonClassDao.updateClAlarms("TestString1", "TestString2");
        commonClassDao.updateClYaml("TestString1", "TestString2");
    }

    @After
    public void deleteDB() {
        sessionFactory.close();
        server.stop();
    }
}
