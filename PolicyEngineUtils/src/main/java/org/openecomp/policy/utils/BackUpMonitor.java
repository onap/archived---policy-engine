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

package org.openecomp.policy.utils;

import java.io.IOException;
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
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.jpa.BackUpMonitorEntity;
import org.openecomp.policy.std.NotificationStore;
import org.openecomp.policy.std.StdPDPNotification;

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
	private static final Logger logger = Logger.getLogger(BackUpMonitor.class.getName());
	private static final int DEFAULT_PING = 60000; // Value is in milliseconds. 
	
	private static BackUpMonitor instance = null;
	private static String resourceName = null;
	private static String resourceNodeName = null;
	private static String notificationRecord = null;
	private static String lastMasterNotification= null;
	private static int pingInterval = DEFAULT_PING;
	private static Boolean masterFlag = false;
	private static Object lock = new Object();
	private static Object notificationLock = new Object();
	private static BackUpHandler handler= null;
	private EntityManager em;
	private EntityManagerFactory emf;
	
	/*
	 * Enumeration for the Resource Node Naming. Add here if required. 
	 */
	public enum ResourceNode{
		BRMS,
		ASTRA
	}
	
	private BackUpMonitor(String resourceNodeName, String resourceName, Properties properties, BackUpHandler handler) throws Exception{
		if(instance == null){
			instance = this;
		}
		BackUpMonitor.resourceNodeName = resourceNodeName;
		BackUpMonitor.resourceName = resourceName;
		BackUpMonitor.handler = handler; 
		// Create Persistence Entity
		properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistencePU.xml");
		emf = Persistence.createEntityManagerFactory("PolicyEngineUtils", properties);
		if(emf==null){
			logger.error("Unable to create Entity Manger Factory ");
			throw new Exception("Unable to create Entity Manger Factory");
		}
		em = emf.createEntityManager();
		
		// Check Database if this is Master or Slave.
		checkDataBase();
		
		// Start thread.
		Thread t = new Thread(new BMonitor());
		t.start();
	}

	/**
	 * Gets the BackUpMonitor Instance if given proper resourceName and properties. Else returns null. 
	 * 
	 * @param resourceNodeName String format of the Resource Node to which the resource Belongs to. 
	 * @param resourceName String format of the ResourceName. This needs to be Unique. 
	 * @param properties Properties format of the properties file. 
	 * @return BackUpMonitor instance. 
	 */
	public static synchronized BackUpMonitor getInstance(String resourceNodeName, String resourceName, Properties properties, BackUpHandler handler) throws Exception {
		if(resourceNodeName==null || resourceNodeName.trim().equals("") ||resourceName==null|| resourceName.trim().equals("") || properties == null || handler==null){
			logger.error("Error while getting Instance. Please check resourceName and/or properties file");
			return null;
		}else if((resourceNodeName.equals(ResourceNode.ASTRA.toString()) || resourceNodeName.equals(ResourceNode.BRMS.toString())) && validate(properties) && instance==null){
			logger.info("Creating Instance of BackUpMonitor");
			instance = new BackUpMonitor(resourceNodeName, resourceName, properties, handler);
		}
		return instance;
	}
	
	// This is to validate given Properties with required values. 
	private static Boolean validate(Properties properties){
		if(properties.getProperty("javax.persistence.jdbc.driver")==null ||properties.getProperty("javax.persistence.jdbc.driver").trim().equals("")){
			logger.error("javax.persistence.jdbc.driver property is empty");
			return false;
		}
		if(properties.getProperty("javax.persistence.jdbc.url")==null || properties.getProperty("javax.persistence.jdbc.url").trim().equals("")){
			logger.error("javax.persistence.jdbc.url property is empty");
			return false;
		}
		if(properties.getProperty("javax.persistence.jdbc.user")==null || properties.getProperty("javax.persistence.jdbc.user").trim().equals("")){
			logger.error("javax.persistence.jdbc.user property is empty");
			return false;
		}
		if(properties.getProperty("javax.persistence.jdbc.password")==null || properties.getProperty("javax.persistence.jdbc.password").trim().equals("")){
			logger.error("javax.persistence.jdbc.password property is empty");
			return false;
		}
		if(properties.getProperty("ping_interval")==null || properties.getProperty("ping_interval").trim().equals("")){
			logger.info("ping_interval property not specified. Taking default value");
		}else{
			try{
				pingInterval = Integer.parseInt(properties.getProperty("ping_interval").trim());
			}catch(NumberFormatException e){
				logger.warn("Ignored invalid proeprty ping_interval. Taking default value.");
				pingInterval = DEFAULT_PING;
			}
		}
		return true;
	}
	
	// Sets the Flag for masterFlag to either True or False. 
	private static void setFlag(Boolean flag){
		synchronized (lock) {
			masterFlag = flag;
		}
	}
	
	/**
	 * Gets the Boolean value of Master(True) or Slave mode (False)
	 * 
	 * @return Boolean flag which if True means that the operation needs to be performed(Master mode) or if false the operation is in slave mode. 
	 */
	public synchronized Boolean getFlag(){
		synchronized (lock) {
			return masterFlag;
		}
	}
	
	// BackUpMonitor Thread 
	private class BMonitor implements Runnable{
		@Override
		public void run() {
			logger.info("Starting BackUpMonitor Thread.. ");
			while(true){
				try {
					TimeUnit.MILLISECONDS.sleep(pingInterval);
					checkDataBase();
				} catch (Exception e) {
					logger.error("Error during Thread execution " + e.getMessage());
				}
			}
		}
	}
	
	// Set Master 
	private static BackUpMonitorEntity setMaster(BackUpMonitorEntity bMEntity){
		bMEntity.setFlag("MASTER");
		setFlag(true);
		return bMEntity;
	}
	
	// Set Slave 
	private static BackUpMonitorEntity setSlave(BackUpMonitorEntity bMEntity){
		bMEntity.setFlag("SLAVE");
		setFlag(false);
		return bMEntity;
	}
	
	// Check Database and set the Flag. 
	private void checkDataBase() throws Exception {
		EntityTransaction et = em.getTransaction();
		notificationRecord = PolicyUtils.objectToJsonString(NotificationStore.getNotificationRecord());
		// Clear Cache. 
		logger.info("Clearing Cache");
		em.getEntityManagerFactory().getCache().evictAll();
		try{
			logger.info("Checking Datatbase for BackUpMonitor.. ");
			et.begin();
			Query query = em.createQuery("select b from BackUpMonitorEntity b where b.resourceNodeName = :nn");
			if(resourceNodeName.equals(ResourceNode.ASTRA.toString())){
				query.setParameter("nn", ResourceNode.ASTRA.toString());
			}else if(resourceNodeName.equals(ResourceNode.BRMS.toString())){
				query.setParameter("nn", ResourceNode.BRMS.toString());
			}
			List<?> bMList = query.getResultList();
			if(bMList.isEmpty()){
				// This is New. create an entry as Master. 
				logger.info("Adding resource " + resourceName + " to Database");
				BackUpMonitorEntity bMEntity = new BackUpMonitorEntity();
				bMEntity.setResoruceNodeName(resourceNodeName);
				bMEntity.setResourceName(resourceName);
				bMEntity = setMaster(bMEntity);
				bMEntity.setTimeStamp(new Date());
				em.persist(bMEntity);
				em.flush();
			}else{
				// Check if resourceName already exists and if there is a Master in Node.
				Boolean masterFlag = false;
				Boolean alreadyMaster = false;
				Date currentTime;
				BackUpMonitorEntity masterEntity = null;
				BackUpMonitorEntity slaveEntity = null;
				long timeDiff = 0;
				for(int i=0; i< bMList.size(); i++){
					BackUpMonitorEntity bMEntity = (BackUpMonitorEntity) bMList.get(i);
					logger.info("Refreshing Entity. ");
					em.refresh(bMEntity);
					if(bMEntity.getResourceName().equals(resourceName)){
						logger.info("Resource Name already Exists. " + resourceName);
						if(bMEntity.getFlag().equalsIgnoreCase("MASTER")){
							// Master mode
							setFlag(true);
							logger.info(resourceName + " is on Master Mode");
							bMEntity.setTimeStamp(new Date());
							bMEntity.setNotificationRecord(notificationRecord);
							em.persist(bMEntity);
							em.flush();
							setLastNotification(null);
							alreadyMaster = true;
							break;
						}else{
							// Slave mode. 
							setFlag(false);
							slaveEntity = bMEntity;
							logger.info(resourceName + " is on Slave Mode");
						}
					}else{
						if(bMEntity.getFlag().equalsIgnoreCase("MASTER")){
							// check if its time stamp is old.
							currentTime = new Date();
							timeDiff = currentTime.getTime()-bMEntity.getTimeStamp().getTime();
							masterEntity = bMEntity;
							masterFlag = true;
						}
					}
				}
				// If there is master and no slave entry then add the slave entry to database. 
				// If we are slave and there is a masterFlag then check timeStamp to find if master is down. 
				if(!alreadyMaster){
					BackUpMonitorEntity bMEntity;
					if(slaveEntity==null){
						bMEntity = new BackUpMonitorEntity();
					}else{
						bMEntity = slaveEntity;
					}
					if(masterFlag && !getFlag()){
						if(timeDiff > pingInterval){
							// This is down or has an issue and we need to become Master while turning the Master to slave. 
							masterEntity = setSlave(masterEntity);
							String lastNotification = null;
							if(masterEntity.getNotificationRecord()!=null){
								lastNotification = calculatePatch(masterEntity.getNotificationRecord());
							}
							setLastNotification(lastNotification);
							em.persist(masterEntity);
							em.flush();
							bMEntity = setMaster(bMEntity);
							logger.info(resourceName + " changed to Master Mode");
						}else{
							bMEntity = setSlave(bMEntity);
							setLastNotification(null);
							logger.info(resourceName + " is on Slave Mode");
						}
					}else{
						// If there is no Master. we need to become Master. 
						bMEntity = setMaster(bMEntity);
						logger.info(resourceName + " is on Master Mode");
						setLastNotification(null);
					}
					bMEntity.setNotificationRecord(notificationRecord);
					bMEntity.setResoruceNodeName(resourceNodeName);
					bMEntity.setResourceName(resourceName);
					bMEntity.setTimeStamp(new Date());
					em.persist(bMEntity);
					em.flush();
				}
			}
			et.commit();
		}catch(Exception e){
			logger.error("failed Database Operation " + e.getMessage());
			if(et.isActive()){
				et.rollback();
			}
			throw new Exception(e);
		}
	}
	
	// Calculate Patch and return String JsonPatch of the notification Delta. 
	private synchronized String calculatePatch(String oldNotificationRecord) {
		try{
			JsonNode notification = JsonLoader.fromString(notificationRecord);
			JsonNode oldNotification = JsonLoader.fromString(oldNotificationRecord);
			JsonNode patchNode = JsonDiff.asJson(oldNotification, notification);
			logger.info("Generated JSON Patch is " + patchNode.toString());
			JsonPatch patch = JsonPatch.fromJson(patchNode);
			try {
				JsonNode patched = patch.apply(oldNotification);
				logger.info("Generated New Notification is : " + patched.toString());
				return patched.toString();
			} catch (JsonPatchException e) {
				logger.error("Error generating Patched "  +e.getMessage());
				return null;
			}
		}catch(IOException e){
			logger.error("Error generating Patched "  +e.getMessage());
			return null;
		}
	}

	/**
	 * Updates Notification in the Database while Performing the health check. 
	 * 
	 * @param notification String format of notification record to store in the Database. 
	 * @throws Exception 
	 */
	public synchronized void updateNotification() throws Exception{
		checkDataBase();
	}
	
	// Take in string notification and send the record delta to Handler. 
	private static void callHandler(String notification){
		if(handler!=null){
			try {
				PDPNotification notificationObject = PolicyUtils.jsonStringToObject(notification, StdPDPNotification.class);
				if(notificationObject.getNotificationType()!=null){
					logger.info("Performing Patched notification ");
					try{
						handler.runOnNotification(notificationObject);
					}catch (Exception e){
						logger.error("Error in Clients Handler Object : " + e.getMessage());
					}
				}
			} catch (IOException e) {
				logger.info("Error while notification Conversion " + e.getMessage());
			}
		}
	}
	
	// Used to set LastMasterNotification Record. 
	private static void setLastNotification(String notification){
		synchronized(notificationLock){
			lastMasterNotification = notification;
			if(lastMasterNotification!=null && !lastMasterNotification.equals("\"notificationType\":null")){
				callHandler(notification);
			}
		}
	}
}
