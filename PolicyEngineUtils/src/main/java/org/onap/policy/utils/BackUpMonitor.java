/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.jpa.BackUpMonitorEntity;
import org.onap.policy.std.NotificationStore;
import org.onap.policy.std.StdPDPNotification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.diff.JsonDiff;

/**
 * BackUp Monitor checks Backup Status with the Database and maintains Redundancy for Gateway Applications.
 * 
 */
public class BackUpMonitor {
    private static final Logger LOGGER = Logger.getLogger(BackUpMonitor.class.getName());
    private static final int DEFAULT_PING = 500; // Value is in milliseconds.
    private static final String PING_INTERVAL = "ping_interval";
    private static final String MASTER = "MASTER";
    private static final String SLAVE = "SLAVE";
    
    private static BackUpMonitor instance = null;
    private static String resourceName = null;
    private static String resourceNodeName = null;
    private static String notificationRecord = null;
    private static String lastMasterNotification = null;
    private static int pingInterval = DEFAULT_PING;
    private static Boolean masterFlag = false;
    private static Object lock = new Object();
    private static Object notificationLock = new Object();
    private static BackUpHandler handler = null;
    private static Boolean stopFlag = false;
    private static Thread t = null;
    private EntityManager em;
    private EntityManagerFactory emf;

    /*
     * Enumeration for the Resource Node Naming. Add here if required.
     */
    public enum ResourceNode {
        BRMS, ASTRA
    }

    private BackUpMonitor(String resourceNodeName, String resourceName, Properties properties, BackUpHandler handler)
            throws BackUpMonitorException {
        init(resourceName, resourceNodeName, handler);
        // Create Persistence Entity
        if(!properties.containsKey(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML)){
            properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistencePU.xml");
        }
        emf = Persistence.createEntityManagerFactory("PolicyEngineUtils", properties);
        if (emf == null) {
            LOGGER.error("Unable to create Entity Manger Factory ");
            throw new BackUpMonitorException("Unable to create Entity Manger Factory");
        }
        em = emf.createEntityManager();

        // Check Database if this is Master or Slave.
        checkDataBase();
        // Start thread.
        startThread(new BMonitor());
    }
    
    private static void startThread(BMonitor bMonitor) {
        t = new Thread(bMonitor);
        t.start();
    }

    public static void stop() throws InterruptedException{
        stopFlag = true;
        if(t!=null){
            t.interrupt();
            t.join();
        }
        instance = null;
    }

    private static void init(String resourceName, String resourceNodeName, BackUpHandler handler) {
        BackUpMonitor.resourceNodeName = resourceNodeName;
        BackUpMonitor.resourceName = resourceName;
        BackUpMonitor.handler = handler;
        stopFlag = false;
    }

    /**
     * Gets the BackUpMonitor Instance if given proper resourceName and properties. Else returns null.
     * 
     * @param resourceNodeName
     *            String format of the Resource Node to which the resource Belongs to.
     * @param resourceName
     *            String format of the ResourceName. This needs to be Unique.
     * @param properties
     *            Properties format of the properties file.
     * @return BackUpMonitor instance.
     */
    public static synchronized BackUpMonitor getInstance(String resourceNodeName, String resourceName,
            Properties properties, BackUpHandler handler) throws BackUpMonitorException {
        if (resourceNodeName == null || "".equals(resourceNodeName.trim()) || resourceName == null
                || "".equals(resourceName.trim()) || properties == null || handler == null) {
            LOGGER.error("Error while getting Instance. Please check resourceName and/or properties file");
            return null;
        } else if ((resourceNodeName.equals(ResourceNode.ASTRA.toString())
                || resourceNodeName.equals(ResourceNode.BRMS.toString())) && validate(properties) && instance == null) {
            LOGGER.info("Creating Instance of BackUpMonitor");
            instance = new BackUpMonitor(resourceNodeName, resourceName, properties, handler);
        }
        return instance;
    }

    // This is to validate given Properties with required values.
    private static Boolean validate(Properties properties) {
        if (properties.getProperty("javax.persistence.jdbc.driver") == null
                || "".equals(properties.getProperty("javax.persistence.jdbc.driver").trim())) {
            LOGGER.error("javax.persistence.jdbc.driver property is empty");
            return false;
        }
        if (properties.getProperty("javax.persistence.jdbc.url") == null
                || "".equals(properties.getProperty("javax.persistence.jdbc.url").trim())) {
            LOGGER.error("javax.persistence.jdbc.url property is empty");
            return false;
        }
        if (properties.getProperty("javax.persistence.jdbc.user") == null
                || "".equals(properties.getProperty("javax.persistence.jdbc.user").trim())) {
            LOGGER.error("javax.persistence.jdbc.user property is empty");
            return false;
        }
        if (properties.getProperty(PING_INTERVAL) == null
                || "".equals(properties.getProperty(PING_INTERVAL).trim())) {
            LOGGER.info("ping_interval property not specified. Taking default value");
        } else {
            try {
                pingInterval = Integer.parseInt(properties.getProperty(PING_INTERVAL).trim());
            } catch (NumberFormatException e) {
                LOGGER.warn("Ignored invalid proeprty ping_interval. Taking default value: " + pingInterval);
                pingInterval = DEFAULT_PING;
            }
        }
        return true;
    }

    // Sets the Flag for masterFlag to either True or False.
    private static void setFlag(Boolean flag) {
        synchronized (lock) {
            masterFlag = flag;
        }
    }

    /**
     * Gets the Boolean value of Master(True) or Slave mode (False)
     * 
     * @return Boolean flag which if True means that the operation needs to be performed(Master mode) or if false the
     *         operation is in slave mode.
     */
    public Boolean getFlag() {
        synchronized (lock) {
            return masterFlag;
        }
    }

    // BackUpMonitor Thread
    private class BMonitor implements Runnable {
        @Override
        public void run() {
            LOGGER.info("Starting BackUpMonitor Thread.. ");
            while (!stopFlag) {
                try {
                    TimeUnit.MILLISECONDS.sleep(pingInterval);
                    checkDataBase();
                } catch (Exception e) {
                    LOGGER.error("Error during Thread execution " + e.getMessage(), e);
                }
            }
        }
    }

    // Set Master
    private static BackUpMonitorEntity setMaster(BackUpMonitorEntity bMEntity) {
        bMEntity.setFlag(MASTER);
        setFlag(true);
        return bMEntity;
    }

    // Set Slave
    private static BackUpMonitorEntity setSlave(BackUpMonitorEntity bMEntity) {
        bMEntity.setFlag(SLAVE);
        setFlag(false);
        return bMEntity;
    }

    // Check Database and set the Flag.
    private void checkDataBase() throws BackUpMonitorException {
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            setNotificationRecord();
            // Clear Cache.
            LOGGER.info("Clearing Cache");
            em.getEntityManagerFactory().getCache().evictAll();
            LOGGER.info("Checking Datatbase for BackUpMonitor.. ");
            et.begin();
            Query query = em.createQuery("select b from BackUpMonitorEntity b where b.resourceNodeName = :nn");
            if (resourceNodeName.equals(ResourceNode.ASTRA.toString())) {
                query.setParameter("nn", ResourceNode.ASTRA.toString());
            } else if (resourceNodeName.equals(ResourceNode.BRMS.toString())) {
                query.setParameter("nn", ResourceNode.BRMS.toString());
            }
            List<?> bMList = query.getResultList();
            if (bMList.isEmpty()) {
                // This is New. create an entry as Master.
                LOGGER.info("Adding resource " + resourceName + " to Database");
                BackUpMonitorEntity bMEntity = new BackUpMonitorEntity();
                bMEntity.setResourceNodeName(resourceNodeName);
                bMEntity.setResourceName(resourceName);
                bMEntity = setMaster(bMEntity);
                bMEntity.setTimeStamp(new Date());
                em.persist(bMEntity);
                em.flush();
            } else {
                checkOtherMaster(bMList);
            }
            et.commit();
        } catch (Exception e) {
            LOGGER.error("failed Database Operation " + e.getMessage(), e);
            if (et!=null && et.isActive()) {
                et.rollback();
            }
            throw new BackUpMonitorException(e);
        }
    }

    private void checkOtherMaster(List<?> bMList) {
        // Check for other Master(s)
        ArrayList<BackUpMonitorEntity> masterEntities = new ArrayList<>();
        // Check for self.
        BackUpMonitorEntity selfEntity = null;
        // Check backup monitor entities.
        for (int i = 0; i < bMList.size(); i++) {
            BackUpMonitorEntity bMEntity = (BackUpMonitorEntity) bMList.get(i);
            LOGGER.info("Refreshing Entity. ");
            em.refresh(bMEntity);
            if (bMEntity.getFlag().equalsIgnoreCase(MASTER)) {
                masterEntities.add(bMEntity);
            }
            if (bMEntity.getResourceName().equals(resourceName)) {
                selfEntity = bMEntity;
            }
        }
        if (selfEntity != null) {
            LOGGER.info("Resource Name already Exists: " + resourceName);
            if (selfEntity.getFlag().equalsIgnoreCase(MASTER)) {
                // Already Master Mode.
                setFlag(true);
                LOGGER.info(resourceName + " is on Master Mode");
                selfEntity.setTimeStamp(new Date());
                selfEntity.setNotificationRecord(notificationRecord);
                em.persist(selfEntity);
                em.flush();
                setLastNotification(null);
                if (!masterEntities.contains(selfEntity)) {
                    masterEntities.add(selfEntity);
                }
            } else {
                // Already Slave Mode.
                setFlag(false);
                selfEntity.setTimeStamp(new Date());
                selfEntity.setNotificationRecord(notificationRecord);
                em.persist(selfEntity);
                em.flush();
                LOGGER.info(resourceName + " is on Slave Mode");
            }
        } else {
            // Resource name is null -> No resource with same name.
            selfEntity = new BackUpMonitorEntity();
            selfEntity.setResourceNodeName(resourceNodeName);
            selfEntity.setResourceName(resourceName);
            selfEntity.setTimeStamp(new Date());
            selfEntity = setSlave(selfEntity);
            setLastNotification(null);
            LOGGER.info("Creating: " + resourceName + " on Slave Mode");
            em.persist(selfEntity);
            em.flush();
        }
        // Correct the database if any errors and perform monitor checks.
        if (masterEntities.size() != 1 || !getFlag()) {
            // We are either not master or there are more masters or no masters.
            if (masterEntities.isEmpty()) {
                // No Masters is a problem Convert ourselves to Master.
                selfEntity = setMaster(selfEntity);
                selfEntity.setTimeStamp(new Date());
                selfEntity.setNotificationRecord(notificationRecord);
                LOGGER.info(resourceName + " changed to Master Mode - No Masters available.");
                em.persist(selfEntity);
                em.flush();
            } else {
                if (masterEntities.size() > 1) {
                    masterEntities = multipleMasterEntity(masterEntities);
                }
                if (masterEntities.size() == 1) {
                    singleMasterEntity(masterEntities, selfEntity);
                } else {
                    LOGGER.error(
                            "Backup Monitor Issue, Masters out of sync, This will be fixed in next interval.");
                }
            }
        }
    }

    private void singleMasterEntity(ArrayList<BackUpMonitorEntity> masterEntities, BackUpMonitorEntity selfEntity) {
        // Correct Size, Check if Master is Latest, if not Change Master to Slave and Slave to
        // Master.
        BackUpMonitorEntity masterEntity = masterEntities.get(0);
        if (!masterEntity.getResourceName().equals(selfEntity.getResourceName())) {
            Date currentTime = new Date();
            long timeDiff;
            timeDiff = currentTime.getTime() - masterEntity.getTimeStamp().getTime();
            if (timeDiff > (pingInterval + 1500)) {
                // This is down or has an issue and we need to become Master while turning the
                // Master to slave.
                masterEntity.setFlag(SLAVE);
                String lastNotification = null;
                if (masterEntity.getNotificationRecord() != null) {
                    lastNotification = calculatePatch(masterEntity.getNotificationRecord());
                }
                setLastNotification(lastNotification);
                em.persist(masterEntity);
                em.flush();
                // Lets Become Master.
                selfEntity = setMaster(selfEntity);
                LOGGER.info("Changing " + resourceName + " from slave to Master Mode");
                selfEntity.setTimeStamp(new Date());
                selfEntity.setNotificationRecord(notificationRecord);
                em.persist(selfEntity);
                em.flush();
            }
        }
    }

    private ArrayList<BackUpMonitorEntity> multipleMasterEntity(ArrayList<BackUpMonitorEntity> masterEntities) {
        // More Masters is a problem, Fix the issue by looking for the latest one and make others
        // Slave.
        BackUpMonitorEntity masterEntity = null;
        for (BackUpMonitorEntity currentEntity : masterEntities) {
            if (currentEntity.getFlag().equalsIgnoreCase(MASTER)) {
                if (masterEntity == null) {
                    masterEntity = currentEntity;
                } else if (currentEntity.getTimeStamp().getTime() > masterEntity.getTimeStamp()
                        .getTime()) {
                    // False Master, Update master to slave and take currentMaster as Master.
                    masterEntity.setFlag(SLAVE);
                    masterEntity.setTimeStamp(new Date());
                    em.persist(masterEntity);
                    em.flush();
                    masterEntity = currentEntity;
                } else {
                    currentEntity.setFlag(SLAVE);
                    currentEntity.setTimeStamp(new Date());
                    em.persist(currentEntity);
                    em.flush();
                }
            }
        }
        masterEntities = new ArrayList<>();
        masterEntities.add(masterEntity);
        return masterEntities;
    }

    private static void setNotificationRecord() throws BackUpMonitorException {
        try {
            notificationRecord = PolicyUtils.objectToJsonString(NotificationStore.getNotificationRecord());
        } catch (JsonProcessingException e1) {
            LOGGER.error("Error retrieving notification record failed. ", e1);
            throw new BackUpMonitorException(e1);
        }
    }

    // Calculate Patch and return String JsonPatch of the notification Delta.
    private synchronized String calculatePatch(String oldNotificationRecord) {
        try {
            JsonNode notification = JsonLoader.fromString(notificationRecord);
            JsonNode oldNotification = JsonLoader.fromString(oldNotificationRecord);
            JsonNode patchNode = JsonDiff.asJson(oldNotification, notification);
            LOGGER.info("Generated JSON Patch is " + patchNode.toString());
            JsonPatch patch = JsonPatch.fromJson(patchNode);
            return generatePatchNotification(patch, oldNotification);
        } catch (IOException e) {
            LOGGER.error("Error generating Patched " + e.getMessage(), e);
            return null;
        }
    }

    private String generatePatchNotification(JsonPatch patch, JsonNode oldNotification) {
        try {
            JsonNode patched = patch.apply(oldNotification);
            LOGGER.info("Generated New Notification is : " + patched.toString());
            return patched.toString();
        } catch (JsonPatchException e) {
            LOGGER.error("Error generating Patched " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Updates Notification in the Database while Performing the health check.
     * 
     * @param notification
     *            String format of notification record to store in the Database.
     * @throws Exception
     */
    public synchronized void updateNotification() throws BackUpMonitorException {
        checkDataBase();
    }

    // Take in string notification and send the record delta to Handler.
    private static void callHandler(String notification) {
        if (handler != null) {
            try {
                PDPNotification notificationObject = PolicyUtils.jsonStringToObject(notification,
                        StdPDPNotification.class);
                if (notificationObject.getNotificationType() != null) {
                    LOGGER.info("Performing Patched notification ");
                    performPatchNotification(notificationObject);

                }
            } catch (IOException e) {
                LOGGER.info("Error while notification Conversion " + e.getMessage(), e);
            }
        }
    }

    private static void performPatchNotification(PDPNotification notificationObject) {
        try {
            handler.runOnNotification(notificationObject);
            notificationRecord = lastMasterNotification;
        } catch (Exception e) {
            LOGGER.error("Error in Clients Handler Object : " + e.getMessage(), e);
        }

    }

    // Used to set LastMasterNotification Record.
    private static void setLastNotification(String notification) {
        synchronized (notificationLock) {
            lastMasterNotification = notification;
            if (lastMasterNotification != null && !"\"notificationType\":null".equals(lastMasterNotification)) {
                if (lastMasterNotification.equals(notificationRecord)) {
                    return;
                }
                callHandler(notification);
            }
        }
    }
}
