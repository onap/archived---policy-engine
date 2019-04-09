/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
import com.att.research.xacml.util.XACMLProperties;
import java.nio.file.Path;
import java.nio.file.Paths;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.xacml.std.pap.StdEngine;

public class HandleIncomingNotificationsTest {
    private static PolicyDBDao dbd;
    private static Path repository;
    private static StdEngine stdEngine = null;
    private static SessionFactory sessionFactory = null;
    private static HandleIncomingNotifications handleIncomingNotifications;
    private static GroupEntity groupEntity;

    /**
     * Sets the up before class.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pap.properties");
        try {
            sessionFactory = PolicyDBDaoTest.setupH2DbDaoImpl("testHandleIncoming");
            handleIncomingNotifications = new HandleIncomingNotifications(sessionFactory);
        } catch (Exception e) {
            Assert.fail();
        }

        PolicyDBDao.setJunit(true);
        dbd = PolicyDBDao.getPolicyDBDaoInstance();
        PolicyDBDao.setJunit(true);
        repository = Paths.get("src/test/resources/pdps");
        stdEngine = new StdEngine(repository);
        dbd.setPapEngine(stdEngine);
        populateDb();
    }


    @Test
    public void testHandleIncomingHttpNotification() {
        handleIncomingNotifications.handleIncomingHttpNotification(null, "1", "group", null, null);
        handleIncomingNotifications.handleIncomingHttpNotification(null, "1", "group", null, null);
        handleIncomingNotifications.handleIncomingHttpNotification(null, "1", "policy", null, null);
        handleIncomingNotifications.handleIncomingHttpNotification(null, "1", "pdp", null, null);
        populatePdpEntityDb("2", groupEntity);
        handleIncomingNotifications.handleIncomingHttpNotification(null, "2", "pdp", null, null);
    }

    /**
     * Populate db.
     */
    public static void populateDb() {
        groupEntity = new GroupEntity();
        groupEntity.setCreatedBy("API");
        groupEntity.setDefaultGroup(false);
        groupEntity.setDeleted(false);
        groupEntity.setDescription("a test group");
        groupEntity.setGroupId("1");
        groupEntity.setGroupName("1");
        groupEntity.prePersist();
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.persist(groupEntity);
        session.getTransaction().commit();
        session.close();
        populatePdpEntityDb("1", groupEntity);
        populatePolicyInDb();
    }

    private static void populatePdpEntityDb(String pdpId, GroupEntity groupEntity) {
        PdpEntity pdpEntity = new PdpEntity();
        pdpEntity.setCreatedBy("API");
        pdpEntity.setDeleted(false);
        pdpEntity.setDescription("test pdp");
        pdpEntity.setGroup(groupEntity);
        pdpEntity.setJmxPort(9993);
        pdpEntity.setModifiedBy("API");
        pdpEntity.setPdpId(pdpId);
        pdpEntity.setPdpName("grouptest");
        pdpEntity.prePersist();

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.persist(pdpEntity);
        session.getTransaction().commit();
        session.close();
    }

    private static void populatePolicyInDb() {
        Policy policyObject = new ConfigPolicy();
        policyObject.policyAdapter = new PolicyRestAdapter();
        policyObject.policyAdapter.setConfigName("testpolicyhandle");
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
        ClassLoader classLoader = HandleIncomingNotificationsTest.class.getClassLoader();
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
    }
}
