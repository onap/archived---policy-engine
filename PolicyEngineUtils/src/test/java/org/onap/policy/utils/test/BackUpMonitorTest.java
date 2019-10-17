/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017,2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.UpdateType;
import org.onap.policy.jpa.BackUpMonitorEntity;
import org.onap.policy.std.NotificationStore;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;
import org.onap.policy.utils.BackUpMonitor;
import org.onap.policy.utils.BackUpMonitor.ResourceNode;
import org.onap.policy.utils.BackUpMonitorException;
import org.onap.policy.utils.PolicyUtils;

public class BackUpMonitorTest {
    @Test(expected = PersistenceException.class)
    public void backUpMonitorTestFail() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
        properties.setProperty("javax.persistence.jdbc.user", "policy_user");
        properties.setProperty("javax.persistence.jdbc.password", "");
        BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test", properties,
                new DummyBackUpHandler());
    }

    @Test
    public void backUpMonitorTestFailNoUser() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
        properties.setProperty("javax.persistence.jdbc.user", "");
        properties.setProperty("javax.persistence.jdbc.password", "password");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test",
                properties, new DummyBackUpHandler());
        assertNull(bum);
    }

    @Test
    public void backUpMonitorTestFailNoUrl() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "");
        properties.setProperty("javax.persistence.jdbc.user", "test");
        properties.setProperty("javax.persistence.jdbc.password", "password");
        properties.setProperty("ping_interval", "500");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test",
                properties, new DummyBackUpHandler());
        assertNull(bum);
    }

    @Test
    public void backUpMonitorTestFailNoDriver() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
        properties.setProperty("javax.persistence.jdbc.user", "test");
        properties.setProperty("javax.persistence.jdbc.password", "password");
        properties.setProperty("ping_interval", "500");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test",
                properties, new DummyBackUpHandler());
        assertNull(bum);
    }

    @Test
    public void backUpMonitorTestFailNoNode() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
        properties.setProperty("javax.persistence.jdbc.user", "test");
        properties.setProperty("javax.persistence.jdbc.password", "password");
        properties.setProperty("ping_interval", "");
        BackUpMonitor bum = BackUpMonitor.getInstance(null, "brms_test", properties, new DummyBackUpHandler());
        assertNull(bum);
    }

    @Test
    public void backUpMonitorTestFailNoResource() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
        properties.setProperty("javax.persistence.jdbc.user", "test");
        properties.setProperty("javax.persistence.jdbc.password", "password");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), null, properties,
                new DummyBackUpHandler());
        assertNull(bum);
    }

    @Test
    public void backUpMonitorTestFailNoProperties() throws Exception {
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test", null,
                new DummyBackUpHandler());
        assertNull(bum);
    }

    @Test
    public void backUpMonitorTestFailNoHandler() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
        properties.setProperty("javax.persistence.jdbc.user", "test");
        properties.setProperty("javax.persistence.jdbc.password", "password");
        properties.setProperty("ping_interval", "500");
        BackUpMonitor bum =
                BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test", properties, null);
        assertNull(bum);
    }

    @Test
    public void backUpMonitorPingError() throws BackUpMonitorException {
        Properties properties = new Properties();
        properties.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:backUpMonitorPingError");
        properties.setProperty("javax.persistence.jdbc.user", "sa");
        properties.setProperty("javax.persistence.jdbc.password", "");
        properties.setProperty("ping_interval", "123a");
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistencePUtest.xml");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test",
                properties, new DummyBackUpHandler());
        assertTrue(bum.getFlag());
    }

    @Test
    public void backUpMonitorMasterTest() throws BackUpMonitorException, InterruptedException, JsonProcessingException {
        Properties properties = new Properties();
        // Master Check. Initial Run.
        properties.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:mem:BackupMonitorMasterTest");
        properties.setProperty("javax.persistence.jdbc.user", "sa");
        properties.setProperty("javax.persistence.jdbc.password", "");
        properties.setProperty("ping_interval", "500");
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistencePUtest.xml");
        BackUpMonitor buMonitor = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test",
                properties, new DummyBackUpHandler());
        createPolicyNotification();
        assertTrue(buMonitor.getFlag());
        // Start a slave check.
        startSlave(properties);
        updatePolicyNotification();
        TimeUnit.MILLISECONDS.sleep(1500);
        assertFalse(buMonitor.getFlag());
        // Get Back to Master test
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(buMonitor.getFlag());
        // No Master check.
        changeAll(properties, "SLAVE");
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(buMonitor.getFlag());
        // No Master check.
        changeAll(properties, "MASTER");
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(buMonitor.getFlag());

    }

    private void updatePolicyNotification() {
        StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.UPDATE);
        loadedPolicy.setVersionNo("2");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        List<StdRemovedPolicy> removedPolicies = new ArrayList<>();
        StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
        removedPolicy.setPolicyName("com.testing");
        removedPolicy.setVersionNo("1");
        notification.setRemovedPolicies(removedPolicies);
        NotificationStore.recordNotification(notification);
    }

    private void createPolicyNotification() {
        StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.UPDATE);
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("1");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        NotificationStore.recordNotification(notification);
    }

    private void changeAll(Properties properties, String flag) {
        EntityManager em =
                Persistence.createEntityManagerFactory("PolicyEngineUtils", properties).createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BackUpMonitorEntity b where b.resourceNodeName = :nn");
        query.setParameter("nn", ResourceNode.BRMS.toString());
        for (Object bmValue : query.getResultList()) {
            BackUpMonitorEntity bmEntity = (BackUpMonitorEntity) bmValue;
            bmEntity.setFlag(flag);
            bmEntity.setTimeStamp(new Date());
        }
        em.flush();
        et.commit();
    }

    private void startSlave(Properties properties) throws JsonProcessingException {
        EntityManager em =
                Persistence.createEntityManagerFactory("PolicyEngineUtils", properties).createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BackUpMonitorEntity b where b.resourceNodeName = :nn");
        query.setParameter("nn", ResourceNode.BRMS.toString());
        List<?> bmList = query.getResultList();
        BackUpMonitorEntity origBm = (BackUpMonitorEntity) bmList.get(0);
        origBm.setFlag("SLAVE");
        origBm.setTimeStamp(new Date());
        BackUpMonitorEntity bmEntity = new BackUpMonitorEntity();
        bmEntity.setResourceNodeName(ResourceNode.BRMS.toString());
        bmEntity.setResourceName("brms_test2");
        bmEntity.setFlag("MASTER");
        bmEntity.setTimeStamp(new Date());
        StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.UPDATE);
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.test");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("1");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        bmEntity.setNotificationRecord(PolicyUtils.objectToJsonString(notification));
        em.persist(bmEntity);
        em.persist(origBm);
        em.flush();
        et.commit();
    }

    @Test(expected = BackUpMonitorException.class)
    public void testException() throws InterruptedException, BackUpMonitorException {
        BackUpMonitor.stop();
        new BackUpMonitorException();
        new BackUpMonitorException(new Exception());
        new BackUpMonitorException("error");
        new BackUpMonitorException("error", new Exception());
        throw new BackUpMonitorException("error", new Exception(), false, false);
    }

    @After
    public void setup() throws InterruptedException {
        BackUpMonitor.stop();
    }

    /**
     * Cleanup.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterClass
    public static void cleanup() throws IOException {
        FileUtils.deleteQuietly(new File("src/test/resources/META-INF/generatedCreate.ddl"));
        FileUtils.deleteQuietly(new File("src/test/resources/META-INF/generatedDrop.ddl"));
    }
}
