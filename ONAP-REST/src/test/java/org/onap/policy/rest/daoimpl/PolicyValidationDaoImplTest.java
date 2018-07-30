/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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
package org.onap.policy.rest.daoimpl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.script.SimpleBindings;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
//import org.onap.policy.conf.HibernateSession;
//import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyRoles;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.SystemLogDB;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.WatchPolicyNotificationTable;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;


public class PolicyValidationDaoImplTest {

    private static Logger logger = FlexLogger.getLogger(PolicyValidationDaoImplTest.class);

    SessionFactory sessionFactory;
    Server server;
    PolicyValidationDaoImpl commonClassDao;

    @Before
    public void setUp() throws Exception{
        try{
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
            //PolicyController.setLogTableLimit("1");
            //HibernateSession.setSession(sessionFactory);
            SystemLogDB data1 = new SystemLogDB();
            data1.setDate(new Date());
            data1.setLogtype("INFO");
            data1.setRemote("Test");
            data1.setSystem("Test");
            data1.setType("Test");
            SystemLogDB data2 = new SystemLogDB();
            data2.setDate(new Date());
            data2.setLogtype("error");
            data2.setRemote("Test");
            data2.setSystem("Test");
            data2.setType("Test");
            //HibernateSession.getSession().save(data1);
            //HibernateSession.getSession().save(data2);

            // Create TCP server for troubleshooting
            server = Server.createTcpServer("-tcpAllowOthers").start();
            System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:test");

        }catch(Exception e){
            System.err.println(e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDB(){
        try{
            // Add data
            UserInfo userinfo = new UserInfo();
            userinfo.setUserLoginId("Test");
            userinfo.setUserName("Test");
            commonClassDao.save(userinfo);
            OnapName onapName = new OnapName();
            onapName.setOnapName("Test");
            onapName.setUserCreatedBy(userinfo);
            onapName.setUserModifiedBy(userinfo);
            onapName.setModifiedDate(new Date());
            commonClassDao.save(onapName);


            List<Object> list = commonClassDao.getData(OnapName.class);
            assertTrue(list.size() == 1);
            logger.debug(list.size());
            logger.debug(list.get(0));
        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUser(){
        try{
            // Add data
            UserInfo userinfo = new UserInfo();
            String loginId_userName = "Test";
            userinfo.setUserLoginId(loginId_userName);
            userinfo.setUserName(loginId_userName);
            commonClassDao.save(userinfo);


            List<Object> dataCur = commonClassDao.getDataByQuery("from UserInfo", new SimpleBindings());

            assertEquals(1, dataCur.size());
            UserInfo cur = (UserInfo) dataCur.get(0);
            assertEquals(loginId_userName, cur.getUserLoginId());
            assertEquals(loginId_userName, cur.getUserName());

            assertFalse(dataCur.isEmpty());

        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_DashboardController(){
        try{
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
            assertTrue( dataCur.get(0) instanceof PolicyEntity);
            assertEquals( name,  ((PolicyEntity)dataCur.get(0)).getPolicyName());
            assertEquals( pe, ((PolicyEntity)dataCur.get(0)));


        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_AutoPushController(){
        try{
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
            assertEquals(pv, (PolicyVersion) dataCur.get(0));

        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationMail(){
        try{
            // Add data
            WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
            String policyFileName = "banana";
            watch.setLoginIds("Test");
            watch.setPolicyName("bananaWatch");
            commonClassDao.save(watch);

            if(policyFileName.contains("/")){
                policyFileName = policyFileName.substring(0, policyFileName.indexOf("/"));
                policyFileName = policyFileName.replace("/", File.separator);
            }
            if(policyFileName.contains("\\")){
                policyFileName = policyFileName.substring(0, policyFileName.indexOf("\\"));
                policyFileName = policyFileName.replace("\\", "\\\\");
            }


            // Current Implementation
            policyFileName += "%";
            String query = "from WatchPolicyNotificationTable where policyName like:policyFileName";
            SimpleBindings params = new SimpleBindings();
            params.put("policyFileName", policyFileName);
            List<Object> dataCur = commonClassDao.getDataByQuery(query, params);

            // Assertions
            assertTrue(dataCur.size() == 1);
            assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
            assertEquals(watch, (WatchPolicyNotificationTable) dataCur.get(0));

        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }


    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyController(){
        try{
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
            String[] splitDBCheckName = dbCheckName.split(":");


            // Current Implementation
            String query =   "FROM PolicyEntity where policyName like :splitDBCheckName1 and scope = :splitDBCheckName0";
            SimpleBindings params = new SimpleBindings();
            params.put("splitDBCheckName1", splitDBCheckName[1] + "%");
            params.put("splitDBCheckName0", splitDBCheckName[0]);
            List<Object> dataCur = commonClassDao.getDataByQuery(query, params);

            // Assertions
            assertTrue(dataCur.size() == 1);
            assertTrue(dataCur.get(0) instanceof PolicyEntity);
            assertEquals(pe, (PolicyEntity) dataCur.get(0));

        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationController(){
        try{
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
            assertEquals(watch, (WatchPolicyNotificationTable) dataCur.get(0) );

        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }


     /* Test for SQL Injection Protection
     */

    @Test
    @Transactional
    @Rollback(true)
    public void getDataByQuery_PolicyNotificationController_Injection(){
        try{
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

            if(dataCur.size() >= 1){
                assertTrue(dataCur.get(0) instanceof WatchPolicyNotificationTable);
                assertFalse(watch.equals((WatchPolicyNotificationTable) dataCur.get(0)));
                assertFalse(watch.equals((WatchPolicyNotificationTable) dataCur.get(0)));
            }
        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @Test
    public void testCommonClassDaoImplMethods(){
        try{
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
            UserInfo data2 = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId:userName", "TestID:Test1");
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
            List<Object> data4 = commonClassDao.getMultipleDataOnAddingConjunction(UserInfo.class, "userLoginId:userName", multipleData);
            assertTrue(data4.size() == 1);
            commonClassDao.delete(data2);
        }catch(Exception e){
            logger.debug("Exception Occured"+e);
            fail();
        }
    }

    @After
    public void deleteDB(){
        sessionFactory.close();
        server.stop();

    }

}
