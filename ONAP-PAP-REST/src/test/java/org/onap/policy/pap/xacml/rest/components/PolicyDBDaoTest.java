/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDao.PolicyDBDaoTestClass;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.PolicyDBException;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.util.XACMLPolicyWriter;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class PolicyDBDaoTest extends Mockito{

    private static Logger logger = FlexLogger.getLogger(PolicyDBDaoTest.class);

    PolicyDBDaoTestClass d;
    PolicyDBDao dbd;
    PolicyDBDao dbd2;
    EntityManagerFactory emf;
    private Path repository;
    StdEngine stdEngine = null;

    @Before
    public void init() throws PAPException, IOException{
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME,"src/test/resources/xacml.pap.properties");
        emf = Persistence.createEntityManagerFactory("testPapPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try{
            em.createQuery("DELETE FROM PolicyDBDaoEntity").executeUpdate();
            em.createQuery("DELETE FROM PolicyEntity").executeUpdate();
            em.createQuery("DELETE FROM ConfigurationDataEntity").executeUpdate();
            em.createQuery("DELETE FROM ActionBodyEntity").executeUpdate();
            em.createQuery("DELETE FROM PdpEntity").executeUpdate();
            em.createQuery("DELETE FROM GroupEntity").executeUpdate();

            em.getTransaction().commit();
        } catch(Exception e){
            logger.error("Exception Occured"+e);
            em.getTransaction().rollback();
        }
        em.close();
        try {
            dbd = PolicyDBDao.getPolicyDBDaoInstance(emf);
            dbd2 = PolicyDBDao.getPolicyDBDaoInstance(emf);
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
    public void cleanUp(){
        PolicyDBDao.setJunit(false);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try{
        em.createQuery("DELETE FROM PolicyDBDaoEntity").executeUpdate();
        em.createQuery("DELETE FROM PolicyEntity").executeUpdate();
        em.createQuery("DELETE FROM ConfigurationDataEntity").executeUpdate();
        em.createQuery("DELETE FROM ActionBodyEntity").executeUpdate();
        em.createQuery("DELETE FROM PdpEntity").executeUpdate();
        em.createQuery("DELETE FROM GroupEntity").executeUpdate();

        em.getTransaction().commit();
        } catch(Exception e){
            em.getTransaction().rollback();
        }
        em.close();
        try {
            FileUtils.forceDelete(new File("src/test/resources/junitTestCreatedDirectory"));
        } catch (IOException e) {
            //could not delete
        }
    }

    @Test
    public void getConfigFileTest(){
        PolicyRestAdapter pra = new PolicyRestAdapter();
        pra.setConfigType(ConfigPolicy.JSON_CONFIG);
        String configFile = d.getConfigFile("Config_mypolicy.xml", "org.onap", pra);
        Assert.assertEquals("org.onap.Config_mypolicy.json", configFile);
        //yes, we can do action files too even though they don't have configs
        configFile = d.getConfigFile("Action_mypolicy.xml", "org.onap", pra);
        Assert.assertEquals("org.onap.Action_mypolicy.json", configFile);
    }

    @Test
    public void createFromPolicyObject(){
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
        PolicyType policyTypeObject = new PolicyType();
        policyObject.policyAdapter.setPolicyData(policyTypeObject);
        ClassLoader classLoader = getClass().getClassLoader();
        PolicyType policyConfig = new PolicyType();
        policyConfig.setVersion(Integer.toString(1));
        policyConfig.setPolicyId("");
        policyConfig.setTarget(new TargetType());
        policyObject.policyAdapter.setData(policyConfig);
        mock(XACMLPolicyWriter.class);
        try {
            policyObject.policyAdapter.setParentPath(IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml")));
        } catch (Exception e2) {
            fail();
        }

        PolicyDBDaoTransaction transaction = dbd.getNewTransaction();
        try{
            transaction.createPolicy(policyObject, "testuser1");
            transaction.commitTransaction();
        } catch(Exception e){
            transaction.rollbackTransaction();
            Assert.fail();
        }

        EntityManager getData = emf.createEntityManager();
        Query getDataQuery = getData.createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:name");
        getDataQuery.setParameter("scope", "com");
        getDataQuery.setParameter("name","Config_SampleTest1206.1.xml");
        PolicyEntity result = null;
        try{
            result = (PolicyEntity)getDataQuery.getSingleResult();
        } catch(Exception e){
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        String expectedData;
        try {
            expectedData = IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml"));
        } catch (IOException e1) {
            expectedData = "";
        }
        Assert.assertEquals(expectedData, result.getPolicyData());
        getData.close();
        result = null;

        transaction.commitTransaction();
        Assert.assertFalse(transaction.isTransactionOpen());
    }

    @Test
    public void groupTransactions(){
        PolicyDBDaoTransaction group = dbd.getNewTransaction();
        String groupName = "test group 1";
        try{
            group.createGroup(PolicyDBDao.createNewPDPGroupId(groupName), groupName, "this is a test group","testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        EntityManager em = emf.createEntityManager();
        Query getGroup = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup.setParameter("groupId", PolicyDBDao.createNewPDPGroupId(groupName));
        getGroup.setParameter("deleted", false);
        List<?> groups = getGroup.getResultList();
        if(groups.size() != 1){
            Assert.fail();
        }
        GroupEntity groupEntity = (GroupEntity)groups.get(0);
        em.close();
        Assert.assertEquals(groupName, groupEntity.getgroupName());
        Assert.assertEquals("this is a test group", groupEntity.getDescription());
        group = dbd.getNewTransaction();
        try{
            OnapPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId(groupName),Paths.get("/"));
            group.deleteGroup(groupToDelete, null,"testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        em = emf.createEntityManager();
        getGroup = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup.setParameter("groupId", PolicyDBDao.createNewPDPGroupId(groupName));
        getGroup.setParameter("deleted", false);
        groups = getGroup.getResultList();
        if(groups.size() != 0){
            System.out.println("Group size: "+groups.size());
            Assert.fail();
        }
        em.close();
        //add a pdp to a group
        group = dbd.getNewTransaction();
        try{
            group.createGroup(PolicyDBDao.createNewPDPGroupId(groupName), groupName, "test group", "testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        group = dbd.getNewTransaction();
        try{
            group.addPdpToGroup("http://localhost:4344/pdp/", PolicyDBDao.createNewPDPGroupId(groupName), "primary", "the main pdp", 3232, "testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        em = emf.createEntityManager();
        Query getPdp = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
        getPdp.setParameter("pdpId", "http://localhost:4344/pdp/");
        getPdp.setParameter("deleted", false);
        List<?> pdps = getPdp.getResultList();
        if(pdps.size() != 1){
            System.out.println("Group size: "+pdps.size());
            Assert.fail();
        }
        PdpEntity pdp = (PdpEntity)pdps.get(0);
        Assert.assertEquals(groupName, pdp.getGroup().getgroupName());
        Assert.assertEquals(pdp.getPdpName(), "primary");
        em.close();
        group = dbd.getNewTransaction();
        try{
            group.removePdpFromGroup("http://localhost:4344/pdp/","testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        em = emf.createEntityManager();
        getPdp = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
        getPdp.setParameter("pdpId", "http://localhost:4344/pdp/");
        getPdp.setParameter("deleted", false);
        pdps = getPdp.getResultList();
        if(pdps.size() != 0){
            System.out.println("Group size: "+pdps.size());
            Assert.fail();
        }
        em.close();

        //add some pdps to groups
        group = dbd.getNewTransaction();
        try{
            group.createGroup(PolicyDBDao.createNewPDPGroupId("testgroup1"), "testgroup1", "test group", "testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        group = dbd.getNewTransaction();
        try{
            group.createGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"), "testgroup2", "test group", "testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }

        group = dbd.getNewTransaction();
        try{
            group.addPdpToGroup("http://localhost:4344/pdp/", PolicyDBDao.createNewPDPGroupId("testgroup1"), "primary", "the main pdp", 3232, "testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        group = dbd.getNewTransaction();
        try{
            group.addPdpToGroup("http://localhost:4345/pdp/", PolicyDBDao.createNewPDPGroupId("testgroup1"), "secondary", "the second pdp", 3233, "testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        em = emf.createEntityManager();
        getPdp = em.createQuery("SELECT p FROM PdpEntity p WHERE p.deleted=:deleted");
        getPdp.setParameter("deleted", false);
        pdps = getPdp.getResultList();
        for(Object o : pdps){
            Assert.assertEquals("testgroup1",((PdpEntity)o).getGroup().getgroupName());
        }
        em.close();

        group = dbd.getNewTransaction();
        try{
            OnapPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup1"),Paths.get("/"));
            OnapPDPGroup groupToMoveTo = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"),Paths.get("/"));
            group.deleteGroup(groupToDelete, groupToMoveTo,"testuser");
            group.commitTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }
        em = emf.createEntityManager();
        getGroup = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup.setParameter("groupId", "testgroup1");
        getGroup.setParameter("deleted", false);
        groups = getGroup.getResultList();
        if(groups.size() != 0){
            System.out.println("Group size: "+groups.size());
            Assert.fail();
        }
        em.close();

        em = emf.createEntityManager();
        getPdp = em.createQuery("SELECT p FROM PdpEntity p WHERE p.deleted=:deleted");
        getPdp.setParameter("deleted", false);
        pdps = getPdp.getResultList();
        for(Object o : pdps){
            Assert.assertEquals("testgroup2",((PdpEntity)o).getGroup().getgroupName());
        }
        em.close();

        group = dbd.getNewTransaction();
        try{
            OnapPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"),Paths.get("/"));
            OnapPDPGroup groupToMoveTo = null;
            group.deleteGroup(groupToDelete, groupToMoveTo,"testuser");
            group.commitTransaction();
            Assert.fail();
        } catch(PolicyDBException pe){
            //good, can't delete group with pdps
            group.rollbackTransaction();
        } catch(Exception e){
            group.rollbackTransaction();
            logger.error("Exception Occured"+e);
            Assert.fail();
        }

        //update group
        OnapPDPGroup pdpGroup = new StdPDPGroup("testgroup2", false, "newtestgroup2", "this is my new description", Paths.get("/"));
        group = dbd.getNewTransaction();
        try{
            group.updateGroup(pdpGroup, "testuser");
            group.commitTransaction();
        }catch (Exception e){
            logger.error("Exception Occured"+e);
            group.rollbackTransaction();
            Assert.fail();
        }
        em = emf.createEntityManager();
        getGroup = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup.setParameter("groupId", "newtestgroup2");
        getGroup.setParameter("deleted", false);
        groups = getGroup.getResultList();
        if(groups.size() != 1){
            System.out.println("Group size: "+groups.size());
            Assert.fail();
        }
        em.close();
        em = emf.createEntityManager();
        getGroup = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
        getGroup.setParameter("groupId", "testgroup2");
        getGroup.setParameter("deleted", false);
        groups = getGroup.getResultList();
        if(groups.size() != 0){
            System.out.println("Group size: "+groups.size());
            Assert.fail();
        }
        em.close();
    }

    @Ignore
    @Test
    public void threadingStabilityTest(){
        if(logger.isDebugEnabled()){
            logger.debug("\n\n****************************"
                    + "threadingStabilityTest() entry"
                    + "******************************\n\n");
        }

        PolicyDBDaoTransaction t = dbd.getNewTransaction();
        Assert.assertTrue(t.isTransactionOpen());
        try {
            //Add 1000 ms to the timeout just to be sure it actually times out
            int sleepTime = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT)) + 1000;
            if(logger.isDebugEnabled()){
                Date date= new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   sleepTime =  " + sleepTime
                        + "\n   TimeStamp = " + date.getTime()
                        + "\n\n");
            }
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            logger.error("Exception Occured"+e);
        }
        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   Assert.assertFalse(t.isTransactionOpen() = " + t.isTransactionOpen() + ")"
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        Assert.assertFalse(t.isTransactionOpen());


        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   a = dbd.getNewTransaction() "
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        PolicyDBDaoTransaction a = dbd.getNewTransaction();
        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   Assert.assertTrue(a.isTransactionOpen() = " + a.isTransactionOpen() + ")"
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        Assert.assertTrue(a.isTransactionOpen());

        try {
            //Add 1000 ms to the timeout just to be sure it actually times out
            int sleepTime = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT)) + 1000;
            if(logger.isDebugEnabled()){
                Date date= new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   sleepTime =  " + sleepTime
                        + "\n   TimeStamp = " + date.getTime()
                        + "\n\n");
            }
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            logger.error("Exception Occured"+e);
        }
        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   b = dbd.getNewTransaction() "
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        PolicyDBDaoTransaction b = dbd.getNewTransaction();
        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   Assert.assertFalse(a.isTransactionOpen() = " + a.isTransactionOpen() + ")"
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        Assert.assertFalse(a.isTransactionOpen());
        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   Assert.assertTrue(b.isTransactionOpen() = " + b.isTransactionOpen() + ")"
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        Assert.assertTrue(b.isTransactionOpen());
        b.close();



        //Now let's test the transaction wait time timeout. Shorten the wait time to 1000 ms
        System.setProperty(XACMLRestProperties.PROP_PAP_TRANS_WAIT,"1000");
        //And let's lengthen the transaction timeout to 5000 ms
        System.setProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT,"5000");
        //get a transacton
        PolicyDBDaoTransaction t1 = dbd.getNewTransaction();
        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                    + "\n   Assert.assertTrue(t1.isTransactionOpen() = " + t1.isTransactionOpen() + ")"
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
        Assert.assertTrue(t1.isTransactionOpen());
        //while it is open, get another from a different DB Dao so it will not collide on the synchronized code segment
        //but will collide at the DB. Remember that the wait time is only 1000 ms
        try {
            //Now the 2nd transaction has a wait timeout in 1000 ms
            PolicyDBDaoTransaction t2 = dbd2.getNewTransaction();
            /*
             * Give it plenty of time to time out the second transaction
             * It will actually hang right here until it either gets the lock from the DB or the
             * request for the DB lock times out. The timers are very sloppy so, I have given
             * this plenty of leeway.
             */

            if(logger.isDebugEnabled()){
                Date date= new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   Thread.sleep(3000)"
                        + "\n   TimeStamp = " + date.getTime()
                        + "\n\n");
            }
            Thread.sleep(3000);
            if(logger.isDebugEnabled()){
                Date date= new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   Assert.assertTrue(t1.isTransactionOpen() = " + t1.isTransactionOpen() + ")"
                        + "\n   Assert.assertFalse(t2.isTransactionOpen() = " + t2.isTransactionOpen() + ")"
                        + "\n   TimeStamp = " + date.getTime()
                        + "\n\n");
            }
            //Assert.assertTrue(t1.isTransactionOpen());
            //Assert.assertFalse(t2.isTransactionOpen());

            Assert.fail("\n\nTransaction timeout of 1000 ms exceeded without a PersistenceException\n\n");
        } catch (PersistenceException e) {
            //success
            if(logger.isDebugEnabled()){
                Date date= new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() "
                        + "\n   SUCCESS! Transaction Wait Timeout worked!"
                        + "\n   Caught PersistenceException = " + e
                        + "\n   TimeStamp = " + date.getTime()
                        + "\n\n");
            }
        } catch (Exception e) {
            // failure due to some other reason
            if(logger.isDebugEnabled()){
                Date date= new java.util.Date();
                logger.debug("\n\nPolicyDBDaoTest.threadingStabilityTest() FAILURE"
                        + "\n   Caught Exception = " + e
                        + "\n   TimeStamp = " + date.getTime()
                        + "\n\n");
            }
            logger.error("Exception Occured"+e);
            Assert.fail();
        }

        if(logger.isDebugEnabled()){
            Date date= new java.util.Date();
            logger.debug("\n\nthreadingStabilityTest() exit"
                    + "\n   TimeStamp = " + date.getTime()
                    + "\n\n");
        }
    }

}