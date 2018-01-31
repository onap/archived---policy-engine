/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After;
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

import com.fasterxml.jackson.core.JsonProcessingException;


public class testBackUpMonitor {

	@Test (expected = PersistenceException.class)
	public void backUpMonitorTestFail() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
		properties.setProperty("javax.persistence.jdbc.user", "policy_user");
		properties.setProperty("javax.persistence.jdbc.password", "");
		BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
	}

	@Test
	public void backUpMonitorTestFailNoUser() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
		properties.setProperty("javax.persistence.jdbc.user", "");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}

	@Test
	public void backUpMonitorTestFailNoURL() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		properties.setProperty("ping_interval", "500");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}

	@Test
	public void backUpMonitorTestFailNoDriver() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		properties.setProperty("ping_interval", "500");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
		assertNull(bum);
	}

	@Test
	public void backUpMonitorTestFailNoNode() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		properties.setProperty("ping_interval", "");
		BackUpMonitor bum = BackUpMonitor.getInstance(null, "brms_test" , properties, new Handler());
		assertNull(bum);
	}

	@Test
	public void backUpMonitorTestFailNoResource() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), null , properties, new Handler());
		assertNull(bum);
	}

	@Test
	public void backUpMonitorTestFailNoProperties() throws Exception{
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , null, new Handler());
		assertNull(bum);
	}

	@Test
	public void backUpMonitorTestFailNoHandler() throws Exception{
		Properties properties = new Properties();
		properties.setProperty("javax.persistence.jdbc.driver", "org.mariadb.jdbc.Driver");
		properties.setProperty("javax.persistence.jdbc.url", "jdbc:mariadb://localhost:3306/onap_sdk");
		properties.setProperty("javax.persistence.jdbc.user", "test");
		properties.setProperty("javax.persistence.jdbc.password", "password");
		properties.setProperty("ping_interval", "500");
		BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, null);
		assertNull(bum);
	}

	@Test
	public void backUpMonitorPingError() throws BackUpMonitorException {
	    Properties properties = new Properties();
	    properties.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:file:./sql/xacmlTest");
        properties.setProperty("javax.persistence.jdbc.user", "sa");
        properties.setProperty("javax.persistence.jdbc.password", "");
        properties.setProperty("ping_interval", "123a");
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistencePUtest.xml");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
        assertTrue(bum.getFlag());
	}

	@Test
	public void backUpMonitorMasterTest() throws BackUpMonitorException, InterruptedException, JsonProcessingException {
	    Properties properties = new Properties();
	    // Master Check. Initial Run.
	    properties.setProperty("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.setProperty("javax.persistence.jdbc.url", "jdbc:h2:file:./sql/xacmlTest");
        properties.setProperty("javax.persistence.jdbc.user", "sa");
        properties.setProperty("javax.persistence.jdbc.password", "");
        properties.setProperty("ping_interval", "500");
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistencePUtest.xml");
        BackUpMonitor bum = BackUpMonitor.getInstance(BackUpMonitor.ResourceNode.BRMS.toString(), "brms_test" , properties, new Handler());
        createPolicyNotification();
        assertTrue(bum.getFlag());
        // Start a slave check.
        startSlave(properties);
        updatePolicyNotification();
        TimeUnit.MILLISECONDS.sleep(1500);
        assertFalse(bum.getFlag());
        // Get Back to Master test
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(bum.getFlag());
        // No Master check.
        changeALL(properties, "SLAVE");
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(bum.getFlag());
        // No Master check.
        changeALL(properties, "MASTER");
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(bum.getFlag());

	}

	private void updatePolicyNotification() {
	    StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.BOTH);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.UPDATE);
        loadedPolicy.setVersionNo("2");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
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
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.testing");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("1");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        NotificationStore.recordNotification(notification);
    }

    private void changeALL(Properties properties, String flag) {
	    EntityManager em = Persistence.createEntityManagerFactory("PolicyEngineUtils", properties).createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        Query query = em.createQuery("select b from BackUpMonitorEntity b where b.resourceNodeName = :nn");
        query.setParameter("nn", ResourceNode.BRMS.toString());
        for(Object bMValue: query.getResultList()){
            BackUpMonitorEntity bmEntity = (BackUpMonitorEntity) bMValue;
            bmEntity.setFlag(flag);
            bmEntity.setTimeStamp(new Date());
        }
        em.flush();
        et.commit();
    }

    private void startSlave(Properties properties) throws JsonProcessingException {
	    EntityManager em = Persistence.createEntityManagerFactory("PolicyEngineUtils", properties).createEntityManager();
	    EntityTransaction et = em.getTransaction();
	    et.begin();
	    Query query = em.createQuery("select b from BackUpMonitorEntity b where b.resourceNodeName = :nn");
	    query.setParameter("nn", ResourceNode.BRMS.toString());
	    List<?> bMList = query.getResultList();
	    BackUpMonitorEntity origBM = (BackUpMonitorEntity) bMList.get(0);
	    origBM.setFlag("SLAVE");
	    origBM.setTimeStamp(new Date());
	    BackUpMonitorEntity bMEntity = new BackUpMonitorEntity();
        bMEntity.setResourceNodeName(ResourceNode.BRMS.toString());
        bMEntity.setResourceName("brms_test2");
        bMEntity.setFlag("MASTER");
        bMEntity.setTimeStamp(new Date());
        StdPDPNotification notification = new StdPDPNotification();
        notification.setNotificationType(NotificationType.UPDATE);
        List<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
        StdLoadedPolicy loadedPolicy = new StdLoadedPolicy();
        loadedPolicy.setPolicyName("com.test");
        loadedPolicy.setUpdateType(UpdateType.NEW);
        loadedPolicy.setVersionNo("1");
        Map<String, String> matches = new HashMap<>();
        matches.put("test", "test");
        loadedPolicy.setMatches(matches);
        loadedPolicies.add(loadedPolicy);
        notification.setLoadedPolicies(loadedPolicies);
        bMEntity.setNotificationRecord(PolicyUtils.objectToJsonString(notification));
        em.persist(bMEntity);
        em.persist(origBM);
        em.flush();
        et.commit();
    }

    @Test(expected = BackUpMonitorException.class)
    public void testException() throws InterruptedException, BackUpMonitorException{
        BackUpMonitor.stop();
        new BackUpMonitorException();
        new BackUpMonitorException(new Exception());
        new BackUpMonitorException("error");
        new BackUpMonitorException("error", new Exception());
        throw new BackUpMonitorException("error", new Exception(), false, false);
    }

    @After
	public void setup() throws InterruptedException{
	    BackUpMonitor.stop();
	}
}
