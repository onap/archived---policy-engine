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

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;

import java.io.File;
import java.io.IOException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.PolicyDBException;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.xacml.std.pap.StdEngine;

public class HandleIncomingNotificationsTest {
    private static final String API = "API";
    private static final String TWO = "2";
    private static final String PDP = "pdp";
    private static final String GROUP = "group";
    private static final String ONE = "1";
    private static PolicyDBDao dbd;
    private static StdEngine stdEngine = null;
    private static SessionFactory sessionFactory = null;
    private static HandleIncomingNotifications handleIncomingNotifications;
    private static GroupEntity groupEntity;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Sets the up before class.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pap.properties");
        sessionFactory = PolicyDBDaoTest.setupH2DbDaoImpl("testHandleIncoming");
        handleIncomingNotifications = new HandleIncomingNotifications(sessionFactory);
        PolicyDBDao.setJunit(true);
        dbd = PolicyDBDao.getPolicyDBDaoInstance();
        PolicyDBDao.setJunit(true);
        populateDb();
    }

    /**
     * Setup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws PAPException the PAP exception
     */
    @Before
    public void setup() throws IOException, PAPException {
        File tmpGrpDir = folder.newFolder("src", "test", "resources", "pdps", "grpTest");
        stdEngine = new StdEngine(tmpGrpDir.toPath());
        dbd.setPapEngine(stdEngine);
    }

    @Test
    public void testHandleIncomingHttpNotification() {
        handleIncomingNotifications.handleIncomingHttpNotification(null, ONE, GROUP, null, null);
        handleIncomingNotifications.handleIncomingHttpNotification(null, ONE, GROUP, null, null);
        handleIncomingNotifications.handleIncomingHttpNotification(null, ONE, "policy", null, null);
        handleIncomingNotifications.handleIncomingHttpNotification(null, ONE, PDP, null, null);
        populatePdpEntityDb(TWO, groupEntity);
        handleIncomingNotifications.handleIncomingHttpNotification(null, TWO, PDP, null, null);
    }

    private static void populateDb() throws PolicyDBException, IOException {
        groupEntity = new GroupEntity();
        groupEntity.setCreatedBy(API);
        groupEntity.setDefaultGroup(false);
        groupEntity.setDeleted(false);
        groupEntity.setDescription("a test group");
        groupEntity.setGroupId(ONE);
        groupEntity.setGroupName(ONE);
        groupEntity.prePersist();
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.persist(groupEntity);
        session.getTransaction().commit();
        session.close();
        populatePdpEntityDb(ONE, groupEntity);
        populatePolicyInDb();
    }

    private static void populatePdpEntityDb(String pdpId, GroupEntity groupEntity) {
        PdpEntity pdpEntity = new PdpEntity();
        pdpEntity.setCreatedBy(API);
        pdpEntity.setDeleted(false);
        pdpEntity.setDescription("test pdp");
        pdpEntity.setGroup(groupEntity);
        pdpEntity.setJmxPort(9993);
        pdpEntity.setModifiedBy(API);
        pdpEntity.setPdpId(pdpId);
        pdpEntity.setPdpName("grouptest");
        pdpEntity.prePersist();

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.persist(pdpEntity);
        session.getTransaction().commit();
        session.close();
    }

    private static void populatePolicyInDb() throws PolicyDBException, IOException {
        Policy policyObject = new ConfigPolicy();
        policyObject.policyAdapter = new PolicyRestAdapter();
        policyObject.policyAdapter.setConfigName("testpolicyhandle");
        policyObject.policyAdapter.setPolicyDescription("my description");
        policyObject.policyAdapter.setConfigBodyData("this is my test config file");
        policyObject.policyAdapter.setPolicyName("SampleTest1206");
        policyObject.policyAdapter.setConfigType(ConfigPolicy.OTHER_CONFIG);
        policyObject.policyAdapter.setPolicyType("Config");
        policyObject.policyAdapter.setDomainDir("com");
        policyObject.policyAdapter.setVersion(ONE);
        policyObject.policyAdapter.setHighestVersion(1);
        PolicyType policyTypeObject = new PolicyType();
        policyObject.policyAdapter.setPolicyData(policyTypeObject);

        PolicyType policyConfig = new PolicyType();
        policyConfig.setVersion(ONE);
        policyConfig.setPolicyId("");
        policyConfig.setTarget(new TargetType());
        policyObject.policyAdapter.setData(policyConfig);
        ClassLoader classLoader = HandleIncomingNotificationsTest.class.getClassLoader();
        policyObject.policyAdapter
                .setParentPath(IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml")));

        PolicyDBDaoTransaction transaction = dbd.getNewTransaction();
        transaction.createPolicy(policyObject, "testuser1");
        transaction.commitTransaction();

    }
}
