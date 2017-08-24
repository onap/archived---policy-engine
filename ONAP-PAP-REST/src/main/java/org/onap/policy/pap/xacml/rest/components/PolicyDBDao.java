/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.DatabaseLockEntity;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PdpEntity;
import org.onap.policy.rest.jpa.PolicyDBDaoEntity;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.util.Webapps;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.policy.xacml.util.XACMLPolicyWriter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PolicyDBDao {
	private static final Logger logger	= FlexLogger.getLogger(PolicyDBDao.class);		
	private List<?> otherServers;
	private EntityManagerFactory emf;
	private static PolicyDBDao currentInstance = null;
	private PAPPolicyEngine papEngine;

	public static final String JSON_CONFIG = "JSON";
	public static final String XML_CONFIG = "XML";
	public static final String PROPERTIES_CONFIG = "PROPERTIES";
	public static final String OTHER_CONFIG = "OTHER";
	public static final String AUDIT_USER = "audit";

	/**
	 * Get an instance of a PolicyDBDao. It creates one if it does not exist.
	 * Only one instance is allowed to be created per server.
	 * @param emf The EntityFactoryManager to be used for database connections
	 * @return The new instance of PolicyDBDao or throw exception if the given emf is null.
	 * @throws IllegalStateException if a PolicyDBDao has already been constructed. Call getPolicyDBDaoInstance() to get this.
	 */
	public static PolicyDBDao getPolicyDBDaoInstance(EntityManagerFactory emf) throws Exception{
		logger.debug("getPolicyDBDaoInstance(EntityManagerFactory emf) as getPolicyDBDaoInstance("+emf+") called");
		if(currentInstance == null){
			if(emf != null){
				currentInstance = new PolicyDBDao(emf);
				return currentInstance;
			}
			throw new IllegalStateException("The EntityManagerFactory is Null");
		}
		return currentInstance;
	}

	/**
	 * Gets the current instance of PolicyDBDao. 
	 * @return The instance of PolicyDBDao or throws exception if the given instance is null.
	 * @throws IllegalStateException if a PolicyDBDao instance is null. Call createPolicyDBDaoInstance(EntityManagerFactory emf) to get this.
	 */
	public static PolicyDBDao getPolicyDBDaoInstance() throws Exception{
		logger.debug("getPolicyDBDaoInstance() as getPolicyDBDaoInstance() called");
		if(currentInstance != null){
			return currentInstance;
		}
		throw new IllegalStateException("The PolicyDBDao.currentInstance is Null.  Use getPolicyDBDao(EntityManagerFactory emf)");
	}
	public void setPapEngine(PAPPolicyEngine papEngine2){
		this.papEngine = (PAPPolicyEngine) papEngine2;
	}
	private PolicyDBDao(EntityManagerFactory emf){
		logger.debug("PolicyDBDao(EntityManagerFactory emf) as PolicyDBDao("+emf+") called");
		this.emf = emf;

		//not needed in this release
		if(!register()){
			PolicyLogger.error("This server's PolicyDBDao instance could not be registered and may not reveive updates");
		}

		otherServers = getRemotePolicyDBDaoList();
		if(logger.isDebugEnabled()){
			logger.debug("Number of remote PolicyDBDao instances: "+otherServers.size());
		}
		if(otherServers.isEmpty()){
			logger.warn("List of PolicyDBDao servers is empty or could not be retrieved");
		}
	}

	//not static because we are going to be using the instance's emf
	//waitTime in ms to wait for lock, or -1 to wait forever (no)
	private void startTransactionSynced(EntityManager entityMgr,int waitTime){
		logger.debug("\n\nstartTransactionSynced(EntityManager entityMgr,int waitTime) as "
				+ "\n   startTransactionSynced("+entityMgr+","+waitTime+") called\n\n");
		DatabaseLockEntity lock = null;		

		entityMgr.setProperty("javax.persistence.query.timeout", waitTime);
		entityMgr.getTransaction().begin();

		if(logger.isDebugEnabled()){
			Map<String,Object> properties = entityMgr.getProperties();
			logger.debug("\n\nstartTransactionSynced():"
					+ "\n   entityManager.getProperties() = " + properties 
					+ "\n\n");
		}
		try{
			if(logger.isDebugEnabled()){
				logger.debug("\n\nstartTransactionSynced():"
						+ "\n   ATTEMPT to get the DB lock"
						+ "\n\n");
			}
			lock = entityMgr.find(DatabaseLockEntity.class, 1, LockModeType.PESSIMISTIC_WRITE);
			if(logger.isDebugEnabled()){
				logger.debug("\n\nstartTransactionSynced():"
						+ "\n   GOT the DB lock"
						+ "\n\n");
			}
		} catch(Exception e){
			System.out.println("Could not get lock entity");
			logger.error("Exception Occured"+e);
		}
		if(lock == null){
			throw new IllegalStateException("The lock row does not exist in the table. Please create a primary key with value = 1.");	
		}

	}
	/**
	 * Gets the list of other registered PolicyDBDaos from the database
	 * @return List (type PolicyDBDaoEntity) of other PolicyDBDaos
	 */
	private List<?> getRemotePolicyDBDaoList(){
		logger.debug("getRemotePolicyDBDaoList() as getRemotePolicyDBDaoList() called");
		List<?> policyDBDaoEntityList = new LinkedList<>();
		EntityManager em = emf.createEntityManager();
		startTransactionSynced(em, 1000);
		try{						
			Query getPolicyDBDaoEntityQuery = em.createNamedQuery("PolicyDBDaoEntity.findAll");			
			policyDBDaoEntityList = getPolicyDBDaoEntityQuery.getResultList();

		} catch(Exception e){
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Exception querying for other registered PolicyDBDaos");
			logger.warn("List of remote PolicyDBDaos will be empty", e);
		}
		try{
			em.getTransaction().commit();
		} catch(Exception e){
			try{
				em.getTransaction().rollback();
			} catch(Exception e2){
				logger.debug("List of remote PolicyDBDaos will be empty", e2);
			}
		}
		em.close();
		return policyDBDaoEntityList;
	}

	public PolicyDBDaoTransaction getNewTransaction(){
		logger.debug("getNewTransaction() as getNewTransaction() called");
		return (PolicyDBDaoTransaction)(new PolicyDBDaoTransactionInstance());
	}

	/*
	 * Because the normal transactions are not used in audits, we can use the same transaction
	 * mechanism to get a transaction and obtain the emlock and the DB lock.  We just need to
	 * provide different transaction timeout values in ms because the audit will run longer
	 * than normal transactions.
	 */
	public PolicyDBDaoTransaction getNewAuditTransaction(){
		logger.debug("getNewAuditTransaction() as getNewAuditTransaction() called");
		//Use the standard transaction wait time in ms
		int auditWaitMs = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_WAIT));
		//Use the (extended) audit timeout time in ms
		int auditTimeoutMs = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_AUDIT_TIMEOUT)); 
		return (PolicyDBDaoTransaction)(new PolicyDBDaoTransactionInstance(auditTimeoutMs, auditWaitMs));
	}


	/**
	 * Checks if two strings are equal. Null strings ARE allowed.
	 * @param one A String or null to compare
	 * @param two A String or null to compare
	 */
	private static boolean stringEquals(String one, String two){
		logger.debug("stringEquals(String one, String two) as stringEquals("+one+", "+two+") called");
		if(one == null && two == null){
			return true;
		}
		if(one == null || two == null){
			return false;
		}
		return one.equals(two);
	}

	/**
	 * Computes the scope in dotted format based on an absolute path and a path that divides the scope.
	 * @param fullPath An absolute path including scope folders and other folders(does not have to be absolute, must just contain scope and other folders before)
	 * @param pathToExclude The path that acts as a division between the scope and the other folders
	 * @return The scope in dotted format (org.onap)
	 */
	private static String computeScope(String fullPath, String pathToExclude){
		logger.debug("computeScope(String fullPath, String pathToExclude) as computeScope("+fullPath+", "+pathToExclude+") called");
		int excludeIndex = fullPath.indexOf(pathToExclude);
		String scopePath = fullPath.substring(excludeIndex+pathToExclude.length());
		String scope = scopePath.replace('\\', '.');
		scope = scope.replace('/', '.');
		if(scope.charAt(0) == '.'){
			scope = scope.substring(1);
		}
		if(scope.charAt(scope.length()-1) == '.'){
			scope = scope.substring(0, scope.length()-1);
		}
		return scope;
	}

	/**
	 * Returns the url of this local pap server, removing the username and password, if they are present
	 * @return The url of this local pap server
	 */
	private String[] getPapUrlUserPass(){
		logger.debug("getPapUrl() as getPapUrl() called");
		String url = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
		if(url == null){
			return null;
		}
		return splitPapUrlUserPass(url);


	}
	private String[] splitPapUrlUserPass(String url){
		String[] urlUserPass = new String[3];
		String[] commaSplit = url.split(",");
		urlUserPass[0] = commaSplit[0];
		if(commaSplit.length > 2){
			urlUserPass[1] = commaSplit[1];
			urlUserPass[2] = commaSplit[2];
		}
		if(urlUserPass[1] == null || urlUserPass[1].equals("")){
			String usernamePropertyValue = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
			if(usernamePropertyValue != null){
				urlUserPass[1] = usernamePropertyValue;
			}
		}
		if(urlUserPass[2] == null || urlUserPass[2].equals("")){
			String passwordPropertyValue = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS);
			if(passwordPropertyValue != null){
				urlUserPass[2] = passwordPropertyValue;
			}
		}
		//if there is no comma, for some reason there is no username and password, so don't try to cut them off
		return urlUserPass;
	}

	private static String encryptPassword(String password) throws Exception{
		Cipher cipher = Cipher.getInstance("AES");		
		cipher.init(Cipher.ENCRYPT_MODE, aesKey());
		byte[] encryption = cipher.doFinal(password.getBytes("UTF-8"));
		System.out.println(encryption);
		return new String(Base64.getMimeEncoder().encode(encryption),"UTF-8");
	}

	private static String decryptPassword(String encryptedPassword) throws Exception{
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, aesKey());
		byte[] password = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword.getBytes("UTF-8")));
		return new String(password,"UTF-8");
	}
	private static Key aesKey(){
		byte[] aesValue = (new String("njrmbklcxtoplawf")).getBytes();
		return new SecretKeySpec(aesValue,"AES");
	}
	/**
	 * Register the PolicyDBDao instance in the PolicyDBDaoEntity table
	 * @return Boolean, were we able to register?
	 */
	private boolean register(){
		logger.debug("register() as register() called");
		String[] url = getPapUrlUserPass();
		EntityManager em = emf.createEntityManager();
		try{
			startTransactionSynced(em, 1000);
		} catch(IllegalStateException e){
			logger.debug ("\nPolicyDBDao.register() caught an IllegalStateException: \n" +e + "\n");
			DatabaseLockEntity lock;
			lock = em.find(DatabaseLockEntity.class, 1);
			if(lock==null){				
				lock = new DatabaseLockEntity();
				em.persist(lock);
				lock.setKey(1);
				try{
					em.flush();
					em.getTransaction().commit();
					em.close();					
				} catch(Exception e2){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, "PolicyDBDao", "COULD NOT CREATE DATABASELOCK ROW.  WILL TRY ONE MORE TIME");
				}
				em = null;
				em = emf.createEntityManager();
				try{
					startTransactionSynced(em, 1000);
				} catch(Exception e3){
					String msg = "DATABASE LOCKING NOT WORKING. CONCURRENCY CONTROL NOT WORKING";
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e3, "PolicyDBDao", msg);
					throw new IllegalStateException("msg" + "\n" + e3);
				}
			}
		}
		logger.debug("\nPolicyDBDao.register. Database locking and concurrency control is initialized\n");
		PolicyDBDaoEntity foundPolicyDBDaoEntity = em.find(PolicyDBDaoEntity.class, url[0]);
		Query getPolicyDBDaoEntityQuery = em.createQuery("SELECT e FROM PolicyDBDaoEntity e WHERE e.policyDBDaoUrl=:url");
		getPolicyDBDaoEntityQuery.setParameter("url", url[0]);
		if(foundPolicyDBDaoEntity == null){
			PolicyDBDaoEntity newPolicyDBDaoEntity = new PolicyDBDaoEntity();
			em.persist(newPolicyDBDaoEntity);
			newPolicyDBDaoEntity.setPolicyDBDaoUrl(url[0]);
			newPolicyDBDaoEntity.setDescription("PAP server at "+url[0]);
			newPolicyDBDaoEntity.setUsername(url[1]);
			try{
				newPolicyDBDaoEntity.setPassword(encryptPassword(url[2]));
			} catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Could not encrypt PAP password");
			}
			try{
				em.getTransaction().commit();
			} catch(Exception e){
				try{
					em.getTransaction().rollback();
				} catch(Exception e2){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, "PolicyDBDao", "Could not add new PolicyDBDao to the database");
				}
			}
		} else {
			//just want to update in order to change modified date
			String encryptedPassword = null;
			try{
				encryptedPassword = encryptPassword(url[2]);
			} catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Could not encrypt PAP password");
			}
			if(url[1] != null && !stringEquals(url[1], foundPolicyDBDaoEntity.getUsername())){
				foundPolicyDBDaoEntity.setUsername(url[1]);
			}
			if(encryptedPassword != null && !stringEquals(encryptedPassword, foundPolicyDBDaoEntity.getPassword())){
				foundPolicyDBDaoEntity.setPassword(encryptedPassword);
			}
			foundPolicyDBDaoEntity.preUpdate();
			try{
				em.getTransaction().commit();
			} catch(Exception e){
				try{
					em.getTransaction().rollback();
				} catch(Exception e2){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, "PolicyDBDao", "Could not update PolicyDBDao in the database");
				}
			}
		}
		em.close();
		logger.debug("\nPolicyDBDao.register(). Success!!\n");
		return true;
	}
	public void notifyOthers(long entityId,String entityType){
		notifyOthers(entityId,entityType,null);
	}
	public void notifyOthers(long entityId, String entityType, String newGroupId){
		logger.debug("notifyOthers(long entityId, String entityType, long newGroupId) as notifyOthers("+entityId+","+entityType+","+newGroupId+") called");		
		LinkedList<Thread> notifyThreads = new LinkedList<>();

		//we're going to run notifications in parallel threads to speed things up
		for(Object obj : otherServers){

			Thread newNotifyThread = new Thread(new NotifyOtherThread(obj, entityId, entityType, newGroupId));

			newNotifyThread.start();

			notifyThreads.add(newNotifyThread);

		}
		//we want to wait for all notifications to complete or timeout before we unlock the interface and allow more changes
		for(Thread t : notifyThreads){
			try {
				t.join();
			} catch (Exception e) {
				logger.warn("Could not join a notifcation thread" + e);
			}
		}


	}

	private class NotifyOtherThread implements Runnable {
		public NotifyOtherThread(Object obj, long entityId, String entityType, String newGroupId){
			this.obj = obj;
			this.entityId = entityId;
			this.entityType = entityType;
			this.newGroupId = newGroupId;
		}
		private Object obj;
		private long entityId;
		private String entityType;
		private String newGroupId;
		@Override
		public void run(){
			//naming of 'o' is for backwards compatibility with the rest of the function
			PolicyDBDaoEntity dbdEntity = (PolicyDBDaoEntity)obj;
			String o = dbdEntity.getPolicyDBDaoUrl();
			String username = dbdEntity.getUsername();
			String password;
			try{
				password = decryptPassword(dbdEntity.getPassword());
			} catch(Exception e){
				//if we can't decrypt, might as well try it anyway
				password = dbdEntity.getPassword();
			}
			Base64.Encoder encoder = Base64.getEncoder();			
			String encoding = encoder.encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));
			HttpURLConnection connection = null;
			UUID requestID = UUID.randomUUID();
			URL url;
			try {
				String papUrl = getPapUrlUserPass()[0];
				if(papUrl == null){
					papUrl = "undefined";
				}
				logger.debug("We are going to try to notify "+o);
				//is this our own url?
				String ourUrl = o;
				try{
					ourUrl = splitPapUrlUserPass((String)o)[0];
				}catch(Exception e){
					ourUrl = o;
					logger.debug(e);
				}
				if(o == null){
					o = "undefined";
				}
				if(papUrl.equals(ourUrl)){
					logger.debug(((String)o)+" is our url, skipping notify");
					return;
				}
				if(newGroupId == null){
					url = new URL(((String)o)+"?policydbdaourl="+papUrl+"&entityid="+entityId+"&entitytype="+entityType);
				} else {
					url = new URL(((String)o)+"?policydbdaourl="+papUrl+"&entityid="+entityId+"&entitytype="+entityType+"&extradata="+newGroupId);
				}
			} catch (MalformedURLException e) {
				logger.warn("Caught MalformedURLException on: new URL()", e);
				return;
			}
			//
			// Open up the connection
			//
			logger.debug("Connecting with url: "+url);
			try {
				connection = (HttpURLConnection)url.openConnection();
			} catch (Exception e) {
				logger.warn("Caught exception on: url.openConnection()",e);
				return;
			}
			//
			// Setup our method and headers
			//
			try {
				connection.setRequestMethod("PUT");
			} catch (ProtocolException e) {
				//why would this error ever occur?
				logger.warn("Caught ProtocolException on connection.setRequestMethod(\"PUT\");",e);			
				return;
			}
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			connection.setRequestProperty("Accept", "text/x-java-properties");
			connection.setRequestProperty("Content-Type", "text/x-java-properties");	  	        	        
			connection.setRequestProperty("requestID", requestID.toString());
			int readTimeout;
			try{
				readTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_NOTIFY_TIMEOUT));

			} catch(Exception e){
				logger.error("xacml.rest.pap.notify.timeoutms property not set, using a default.");
				readTimeout = 10000;
			}
			connection.setReadTimeout(readTimeout);
			connection.setConnectTimeout(readTimeout);
			connection.setUseCaches(false);
			//
			// Adding this in. It seems the HttpUrlConnection class does NOT
			// properly forward our headers for POST re-direction. It does so
			// for a GET re-direction.
			//
			// So we need to handle this ourselves.
			//
			connection.setInstanceFollowRedirects(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			try {
				connection.connect();
			} catch (Exception e) {
				logger.warn("Caught exception on: connection.connect()",e);
				return;
			}
			try {
				if (connection.getResponseCode() == 200) {
					logger.info("Received response 200 from pap server on notify");
					//notified = true;
				} else {
					logger.warn("connection response code not 200, received: "+connection.getResponseCode());
				}
			} catch (Exception e) {
				logger.warn("Caught Exception on: connection.getResponseCode() ", e);
			}


			connection.disconnect();
		}
	}

	private static String evaluateXPath(String expression, String xml) {
		InputSource source = new InputSource(new StringReader(xml));

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		String description = "";
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(source);

			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();


			description = xpath.evaluate(expression, document);		
		}catch(Exception e){
			logger.error("Exception Occured while evaluating path"+e);
		}

		return description;
	}

	private static String getDescriptionFromXacml(String xacmlData){
		String openTag = "<Description>";
		String closeTag = "</Description>";
		int descIndex = xacmlData.indexOf(openTag);
		int endDescIndex = xacmlData.indexOf(closeTag);
		String desc = xacmlData.substring(descIndex+openTag.length(),endDescIndex);
		return desc;
	}
	
	private final String POLICY_NOTIFICATION = "policy";
	private final String PDP_NOTIFICATION = "pdp";
	private final String GROUP_NOTIFICATION = "group";
	public void handleIncomingHttpNotification(String url, String entityId, String entityType, String extraData, XACMLPapServlet xacmlPapServlet){
		logger.info("DBDao url: " + url + " has reported an update on "+entityType+" entity "+entityId);		
		PolicyDBDaoTransaction transaction = this.getNewTransaction();
		//although its named retries, this is the total number of tries
		int retries;
		try{
			retries = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_INCOMINGNOTIFICATION_TRIES));

		} catch(Exception e){
			logger.error("xacml.rest.pap.incomingnotification.tries property not set, using a default of 3."+e);
			retries = 3;
		}
		//if someone sets it to some dumb value, we need to make sure it will try at least once
		if(retries < 1){
			retries = 1;
		}
		int pauseBetweenRetries = 1000;
		switch(entityType){	

		case POLICY_NOTIFICATION:
			for(int i=0; i<retries;i++){
				try{
					handleIncomingPolicyChange(url, entityId,extraData);
					break;
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught exception on handleIncomingPolicyChange("+url+", "+entityId+", "+extraData+")");
				}
				try{
					Thread.sleep(pauseBetweenRetries);
				}catch(InterruptedException ie){
					break;
				}
			}
			break;
		case PDP_NOTIFICATION:
			for(int i=0; i<retries;i++){
				try{
					handleIncomingPdpChange(url, entityId, transaction);
					break;
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught exception on handleIncomingPdpChange("+url+", "+entityId+", "+transaction+")");
				}
				try{
					Thread.sleep(pauseBetweenRetries);
				}catch(InterruptedException ie){
					break;
				}
			}
			break;
		case GROUP_NOTIFICATION:
			for(int i=0; i<retries;i++){
				try{
					handleIncomingGroupChange(url, entityId, extraData, transaction, xacmlPapServlet);
					break;
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught exception on handleIncomingGroupChange("+url+", "+entityId+", "+extraData+", "+transaction+", "+xacmlPapServlet+")");
				}
				try{
					Thread.sleep(pauseBetweenRetries);
				}catch(InterruptedException ie){
					break;
				}
			}
			break;		
		}
		//no changes should be being made in this function, we still need to close
		transaction.rollbackTransaction();
	}
	private void handleIncomingGroupChange(String url, String groupId, String extraData,PolicyDBDaoTransaction transaction,XACMLPapServlet xacmlPapServlet) throws PAPException{
		GroupEntity groupRecord = null;
		long groupIdLong = -1;
		try{
			groupIdLong = Long.parseLong(groupId);
		} catch(NumberFormatException e){
			throw new IllegalArgumentException("groupId "+groupId+" cannot be parsed into a long");
		}
		try{
			groupRecord = transaction.getGroup(groupIdLong);
		} catch(Exception e){
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get pdp group record with transaction.getGroup("+groupIdLong+");");
			throw new PAPException("Could not get local group "+groupIdLong);
		}
		if(groupRecord == null){
			throw new PersistenceException("The group record returned is null");
		}
		//compare to local fs
		//does group folder exist
		OnapPDPGroup localGroup = null;
		try {
			localGroup = papEngine.getGroup(groupRecord.getGroupId());
		} catch (Exception e) {
			logger.warn("Caught PAPException trying to get local pdp group with papEngine.getGroup("+groupId+");",e);
		}
		if(localGroup == null && extraData != null){
			//here we can try to load an old group id from the extraData
			try{
				localGroup = papEngine.getGroup(extraData);
			}catch(Exception e){
				logger.warn("Caught PAPException trying to get local pdp group with papEngine.getGroup("+extraData+");",e);
			}
		}
		if(localGroup != null && groupRecord.isDeleted()){
			OnapPDPGroup newLocalGroup = null;
			if(extraData != null){
				try {
					newLocalGroup = papEngine.getGroup(extraData);
				} catch (PAPException e) {
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to get new pdp group with papEngine.getGroup("+extraData+");");
				}
			}
			try {
				papEngine.removeGroup(localGroup, newLocalGroup);
			} catch (NullPointerException | PAPException e) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to get remove pdp group with papEngine.removeGroup("+localGroup+", "+newLocalGroup+");");
				throw new PAPException("Could not remove group "+groupId);
			}
		}
		else if(localGroup == null){
			//creating a new group
			try {
				papEngine.newGroup(groupRecord.getgroupName(), groupRecord.getDescription());
			} catch (NullPointerException | PAPException e) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to create pdp group with papEngine.newGroup(groupRecord.getgroupName(), groupRecord.getDescription());");
				throw new PAPException("Could not create group "+groupRecord);
			}
			try {
				localGroup = papEngine.getGroup(groupRecord.getGroupId());
			} catch (PAPException e1) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, "PolicyDBDao", "Caught PAPException trying to get pdp group we just created with papEngine.getGroup(groupRecord.getGroupId());\nAny PDPs or policies in the new group may not have been added");
				return;
			}
			//add possible pdps to group
			List<?> pdpsInGroup = transaction.getPdpsInGroup(Long.parseLong(groupRecord.getGroupId()));
			for(Object pdpO : pdpsInGroup){
				PdpEntity pdp = (PdpEntity)pdpO;
				try {
					papEngine.newPDP(pdp.getPdpId(), localGroup, pdp.getPdpName(), pdp.getDescription(), pdp.getJmxPort());
				} catch (NullPointerException | PAPException e) {
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to get create pdp with papEngine.newPDP(pdp.getPdpId(), localGroup, pdp.getPdpName(), pdp.getDescription(), pdp.getJmxPort());");
					throw new PAPException("Could not create pdp "+pdp);
				}
			}
			//add possible policies to group (filesystem only, apparently)
		} else {
			if(!(localGroup instanceof StdPDPGroup)){
				throw new PAPException("group is not a StdPDPGroup");
			}
			//clone the object
			//because it will be comparing the new group to its own version
			StdPDPGroup localGroupClone = new StdPDPGroup(localGroup.getId(),localGroup.isDefaultGroup(),localGroup.getName(),localGroup.getDescription(),((StdPDPGroup)localGroup).getDirectory());
			localGroupClone.setOnapPdps(localGroup.getOnapPdps());
			localGroupClone.setPipConfigs(localGroup.getPipConfigs());
			localGroupClone.setStatus(localGroup.getStatus());			
			//we are updating a group or adding a policy or changing default
			//set default if it should be
			if(!localGroupClone.isDefaultGroup() && groupRecord.isDefaultGroup()){
				try {
					papEngine.SetDefaultGroup(localGroup);
					return;
				} catch (PAPException e) {
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to set default group with papEngine.SetDefaultGroup("+localGroupClone+");");
					throw new PAPException("Could not set default group to "+localGroupClone);
				}				
			}		
			boolean needToUpdate = false;
			if(updateGroupPoliciesInFileSystem(localGroupClone,localGroup, groupRecord, transaction)){
				needToUpdate = true;
			}
			if(!stringEquals(localGroupClone.getId(),groupRecord.getGroupId()) || !stringEquals(localGroupClone.getName(),groupRecord.getgroupName())){
				//changing ids
				//we do not want to change the id, the papEngine will do this for us, it needs to know the old id
				localGroupClone.setName(groupRecord.getgroupName());
				needToUpdate = true;
			}
			if(!stringEquals(localGroupClone.getDescription(),groupRecord.getDescription())){
				localGroupClone.setDescription(groupRecord.getDescription());
				needToUpdate = true;
			}
			if(needToUpdate){
				try {

					papEngine.updateGroup(localGroupClone);
				} catch (PAPException e) {
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to update group with papEngine.updateGroup("+localGroupClone+");");
					throw new PAPException("Could not update group "+localGroupClone);
				}
			}				

		}
		//call command that corresponds to the change that was made
	}
	//this will also handle removes, since incoming pdpGroup has no policies internally, we are just going to add them all in from the db
	private boolean updateGroupPoliciesInFileSystem(OnapPDPGroup pdpGroup,OnapPDPGroup oldPdpGroup, GroupEntity groupRecord, PolicyDBDaoTransaction transaction) throws PAPException{
		if(!(pdpGroup instanceof StdPDPGroup)){
			throw new PAPException("group is not a StdPDPGroup");
		}
		StdPDPGroup group = (StdPDPGroup)pdpGroup;
		//this must always be true since we don't explicitly know when a delete is occuring
		boolean didUpdate = true;
		HashMap<String,PDPPolicy> currentPolicySet = new HashMap<String,PDPPolicy>(oldPdpGroup.getPolicies().size());
		HashSet<PDPPolicy> newPolicySet = new HashSet<>();
		for(PDPPolicy pdpPolicy : oldPdpGroup.getPolicies()){
			currentPolicySet.put(pdpPolicy.getId(), pdpPolicy);
		}
		for(PolicyEntity policy : groupRecord.getPolicies()){
			String pdpPolicyName = getPdpPolicyName(policy.getPolicyName(), policy.getScope());
			if(group.getPolicy(pdpPolicyName) == null){
				didUpdate = true;
				if(currentPolicySet.containsKey(pdpPolicyName)){
					newPolicySet.add(currentPolicySet.get(pdpPolicyName));
				} else{
					InputStream policyStream = new ByteArrayInputStream(policy.getPolicyData().getBytes());
					group.copyPolicyToFile(pdpPolicyName,policyStream);
					((StdPDPPolicy)(group.getPolicy(pdpPolicyName))).setName(removeExtensionAndVersionFromPolicyName(policy.getPolicyName()));
					try {
						policyStream.close();
					} catch (IOException e) {
						didUpdate = false;
						PolicyLogger.error(e.getMessage() +e);
					}
				}
			}
		}
		if(didUpdate){
			newPolicySet.addAll(group.getPolicies());
			group.setPolicies(newPolicySet);
		}
		return didUpdate;

	}
	private String removeExtensionAndVersionFromPolicyName(String originalPolicyName){
        return getPolicyNameAndVersionFromPolicyFileName(originalPolicyName)[0];
    }

    /**
     * Splits apart the policy name and version from a policy file path
     * @param originalPolicyName: a policy file name ex: Config_policy.2.xml
     * @return An array [0]: The policy name, [1]: the policy version, as a string
     */
    private String[] getPolicyNameAndVersionFromPolicyFileName(String originalPolicyName){
        String policyName = originalPolicyName;
        String[] nameAndVersion = new String[2];
        try{
            policyName = removeFileExtension(policyName);
            nameAndVersion[0] = policyName.substring(0,policyName.lastIndexOf('.'));
            if(isNullOrEmpty(nameAndVersion[0])){
                throw new Exception();
            }
        } catch(Exception e){
            nameAndVersion[0] = originalPolicyName;         
            logger.debug(e);
        }
        try{
            nameAndVersion[1] = policyName.substring(policyName.lastIndexOf('.')+1);
            if(isNullOrEmpty(nameAndVersion[1])){
                throw new Exception();
            }
        } catch(Exception e){
            nameAndVersion[1] = "1";
            logger.debug(e);
        }
        return nameAndVersion;
    }
    
	private void handleIncomingPdpChange(String url, String pdpId, PolicyDBDaoTransaction transaction) throws PAPException{
		//get pdp
		long pdpIdLong = -1;
		try{
			pdpIdLong = Long.parseLong(pdpId);
		}catch(NumberFormatException e){
			throw new IllegalArgumentException("pdpId "+pdpId+" cannot be parsed into a long");
		}
		PdpEntity pdpRecord = null;
		try{
			pdpRecord = transaction.getPdp(pdpIdLong);
		}catch(Exception e){
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get pdp record with transaction.getPdp("+pdpIdLong+");");
			throw new PAPException("Could not get local pdp "+pdpIdLong);
		}
		if(pdpRecord == null){
			throw new PersistenceException("The pdpRecord returned is null");
		}
		PDP localPdp = null;
		try {
			localPdp = papEngine.getPDP(pdpRecord.getPdpId());
		} catch (PAPException e) {
			logger.warn("Caught PAPException trying to get local pdp  with papEngine.getPDP("+pdpId+");",e);
		}
		if(localPdp != null && pdpRecord.isDeleted()){
			try {
				papEngine.removePDP((OnapPDP) localPdp);
			} catch (PAPException e) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to get remove pdp with papEngine.removePDP("+localPdp+");");
				throw new PAPException("Could not remove pdp "+pdpId);
			}
		}
		else if(localPdp == null){
			//add new pdp
			//get group
			OnapPDPGroup localGroup = null;
			try {
				localGroup = papEngine.getGroup(pdpRecord.getGroup().getGroupId());
			} catch (PAPException e1) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, "PolicyDBDao", "Caught PAPException trying to get local group to add pdp to with papEngine.getGroup(pdpRecord.getGroup().getGroupId());");
				throw new PAPException("Could not get local group");
			}			
			try {
				papEngine.newPDP(pdpRecord.getPdpId(), localGroup, pdpRecord.getPdpName(), pdpRecord.getDescription(), pdpRecord.getJmxPort());
			} catch (NullPointerException | PAPException e) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to create pdp with papEngine.newPDP("+pdpRecord.getPdpId()+", "+localGroup+", "+pdpRecord.getPdpName()+", "+pdpRecord.getDescription()+", "+pdpRecord.getJmxPort()+");");
				throw new PAPException("Could not create pdp "+pdpRecord);
			}
		} else {
			boolean needToUpdate = false;
			if(!stringEquals(localPdp.getId(),pdpRecord.getPdpId()) || !stringEquals(localPdp.getName(),pdpRecord.getPdpName())){
				//again, we don't want to change the id, the papEngine will do this
				localPdp.setName(pdpRecord.getPdpName());
				needToUpdate = true;
			}
			if(!stringEquals(localPdp.getDescription(),pdpRecord.getDescription())){
				localPdp.setDescription(pdpRecord.getDescription());
				needToUpdate = true;
			}
			String localPdpGroupId = null;
			try{
				localPdpGroupId = papEngine.getPDPGroup((OnapPDP) localPdp).getId();
			} catch(PAPException e){
				//could be null or something, just warn at this point
				logger.warn("Caught PAPException trying to get id of local group that pdp is in with localPdpGroupId = papEngine.getPDPGroup(localPdp).getId();",e);
			}
			if(!stringEquals(localPdpGroupId,pdpRecord.getGroup().getGroupId())){
				OnapPDPGroup newPdpGroup = null;
				try{
					newPdpGroup = papEngine.getGroup(pdpRecord.getGroup().getGroupId());
				}catch(PAPException e){
					//ok, now we have an issue. Time to stop things
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to get id of local group to move pdp to with papEngine.getGroup(pdpRecord.getGroup().getGroupId());");
					throw new PAPException("Could not get local group");
				}
				try{
					papEngine.movePDP((OnapPDP) localPdp, newPdpGroup);
				}catch(PAPException e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to move pdp with papEngine.movePDP(localPdp, newPdpGroup);");
					throw new PAPException("Could not move pdp "+localPdp);
				}
			}
			if(((PdpEntity) localPdp).getJmxPort() != pdpRecord.getJmxPort()){
				((PdpEntity) localPdp).setJmxPort(pdpRecord.getJmxPort());
				needToUpdate = true;
			}
			if(needToUpdate){
				try {
					papEngine.updatePDP((OnapPDP) localPdp);
				} catch (PAPException e) {
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PAPException trying to update pdp with papEngine.updatePdp("+localPdp+");");
					throw new PAPException("Could not update pdp "+localPdp);
				}
			}
		}
		//compare to local situation
		//call command to update
	}
	private void handleIncomingPolicyChange(String url, String policyId,String oldPathString){
		String policyName = null;
		EntityManager em = emf.createEntityManager();
		Query getPolicyEntityQuery = em.createNamedQuery("PolicyEntity.FindById");
		getPolicyEntityQuery.setParameter("id", Long.valueOf(policyId));

		@SuppressWarnings("unchecked")
		List<PolicyEntity> policies = getPolicyEntityQuery.getResultList();
		PolicyEntity policy = null;
		if (!policies.isEmpty()){
			policy = policies.get(0);
		}
		String action = "unknown action";
		try {
			if(policy != null){
				policyName = policy.getPolicyName();
				logger.debug("Deleting Policy: " + policy.getPolicyName());
				action = "delete";
				Path subFile = null;

				if (policy.getConfigurationData()!= null){
					subFile = getPolicySubFile(policy.getConfigurationData().getConfigurationName(), "Config");
				}else if(policy.getActionBodyEntity()!= null){
					subFile = getPolicySubFile(policy.getActionBodyEntity().getActionBodyName(), "Action");
				}

				if(subFile != null){
					Files.deleteIfExists(subFile);
				}
				if (policy.getConfigurationData()!= null){
					writePolicySubFile(policy, "Config");
				}else if(policy.getActionBodyEntity()!= null){
					writePolicySubFile(policy, "Action");
				}
			}
		} catch (IOException e1) {
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, "PolicyDBDao", "Error occurred while performing [" + action + "] of Policy File: " + policyName);
		}	
	}

	private String getPdpPolicyName(String name, String scope){
		String finalName = "";
		finalName += scope;
		finalName += ".";
		finalName += removeFileExtension(name);
		finalName += ".xml";
		return finalName;
	}
	private String removeFileExtension(String fileName){
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	private Path getPolicySubFile(String filename, String subFileType){
		logger.debug("getPolicySubFile(" + filename + ", " + subFileType + ")");
		Path filePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS).toString(), subFileType);
		File file = null;

		filename = FilenameUtils.removeExtension(filename);

		for(File tmpFile : filePath.toFile().listFiles()){
			if (FilenameUtils.removeExtension(tmpFile.getName()).equals(filename)){
				file = tmpFile;
			}
		}

		Path finalPath = null;
		if (file!= null){
			finalPath = Paths.get(file.getAbsolutePath());
		}

		logger.debug("end of getPolicySubFile: " + finalPath);
		return finalPath;	
	}

	private boolean writePolicySubFile(PolicyEntity policy, String policyType){
		logger.info("writePolicySubFile with policyName[" + policy.getPolicyName() + "] and policyType[" + policyType + "]");
		String type = null;
		String subTypeName = null;
		String subTypeBody = null;
		if (policyType.equalsIgnoreCase("config")){
			type = "Config";
			subTypeName = FilenameUtils.removeExtension(policy.getConfigurationData().getConfigurationName());
			subTypeBody = policy.getConfigurationData().getConfigBody();

			String configType = policy.getConfigurationData().getConfigType();


			if (configType != null) {
				if (configType.equals(JSON_CONFIG)) {
					subTypeName = subTypeName + ".json";
				}
				if (configType.equals(XML_CONFIG)) {
					subTypeName = subTypeName + ".xml";
				}
				if (configType.equals(PROPERTIES_CONFIG)) {
					subTypeName = subTypeName + ".properties";
				}
				if (configType.equals(OTHER_CONFIG)) {
					subTypeName = subTypeName + ".txt";
				}
			}

		}else if (policyType.equalsIgnoreCase("action")){
			type = "Action";
			subTypeName = policy.getActionBodyEntity().getActionBodyName();
			subTypeBody = policy.getActionBodyEntity().getActionBody();


		}
		Path filePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS).toString(), type);

		if(subTypeBody == null){
			subTypeBody = "";
		}
		boolean success = false;
		try {
			Files.deleteIfExists(Paths.get(filePath.toString(), subTypeName));
			File file = Paths.get(filePath.toString(),subTypeName).toFile();
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file, false); // false to overwrite
			fileWriter.write(subTypeBody);
			fileWriter.close();
			success = true;

		} catch (Exception e) {
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Exception occured while creating Configuration File for Policy : " + policy.getPolicyName());
		}					

		return success;

	}

	public void auditLocalDatabase(PAPPolicyEngine papEngine2){
		logger.debug("PolicyDBDao.auditLocalDatabase() is called");
		try{
			deleteAllGroupTables();
			auditGroups(papEngine2);
		} catch(Exception e){
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "auditLocalDatabase() error");
			logger.error("Exception Occured"+e);
		}
	}

	public void deleteAllGroupTables(){
		logger.debug("PolicyDBDao.deleteAllGroupTables() called");
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		Query deletePdpEntityEntityTableUpdate = em.createNamedQuery("PdpEntity.deleteAll");
		deletePdpEntityEntityTableUpdate.executeUpdate();

		Query deleteGroupEntityTableUpdate = em.createNamedQuery("GroupEntity.deleteAll");
		deleteGroupEntityTableUpdate.executeUpdate();

		em.getTransaction().commit();
		em.close();
	}

	@SuppressWarnings("unchecked")
	public void auditGroups(PAPPolicyEngine papEngine2){
		logger.debug("PolicyDBDao.auditGroups() called");

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		final String AUDIT_STR = "Audit";
		try{

			Set<OnapPDPGroup> groups = papEngine2.getOnapPDPGroups();

			for (OnapPDPGroup grp : groups){
				try{
					GroupEntity groupEntity = new GroupEntity();
					em.persist(groupEntity);
					groupEntity.setGroupName(grp.getName());
					groupEntity.setDescription(grp.getDescription());
					groupEntity.setDefaultGroup(grp.isDefaultGroup());
					groupEntity.setCreatedBy(AUDIT_STR);
					groupEntity.setGroupId(createNewPDPGroupId(grp.getId()));
					groupEntity.setModifiedBy(AUDIT_STR);
					Set<OnapPDP> pdps =  grp.getOnapPdps();				

					for(OnapPDP pdp : pdps){
						PdpEntity pdpEntity = new PdpEntity();
						em.persist(pdpEntity);
						pdpEntity.setGroup(groupEntity);
						pdpEntity.setJmxPort(pdp.getJmxPort());
						pdpEntity.setPdpId(pdp.getId());
						pdpEntity.setPdpName(pdp.getName());
						pdpEntity.setModifiedBy(AUDIT_STR);
						pdpEntity.setCreatedBy(AUDIT_STR);

					}

					Set<PDPPolicy> policies = grp.getPolicies();

					for(PDPPolicy policy : policies){
						try{
							String[] stringArray = getNameScopeAndVersionFromPdpPolicy(policy.getId());
							List<PolicyEntity> policyEntityList;
							Query getPolicyEntitiesQuery = em.createNamedQuery("PolicyEntity.findByNameAndScope");
							getPolicyEntitiesQuery.setParameter("name", stringArray[0]);
							getPolicyEntitiesQuery.setParameter("scope", stringArray[1]);

							policyEntityList = getPolicyEntitiesQuery.getResultList();
							PolicyEntity policyEntity = null;
							if(!policyEntityList.isEmpty()){
								policyEntity = policyEntityList.get(0);
							}
							if(policyEntity != null){
								groupEntity.addPolicyToGroup(policyEntity);
							}
						}catch(Exception e2){
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, "PolicyDBDao", "Exception auditGroups inner catch");
						}
					}
				}catch(Exception e1){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, "PolicyDBDao", "Exception auditGroups middle catch");
				}
			}
		}catch(Exception e){
			em.getTransaction().rollback();
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Exception auditGroups outer catch");
			em.close();
			return;
		}

		em.getTransaction().commit();
		em.close();

	}

	private String getConfigFile(String filename, PolicyRestAdapter policy){
		if(policy == null){
			return getConfigFile(filename, (String)null);
		}
		return getConfigFile(filename, policy.getConfigType());
	}
	//copied from ConfigPolicy.java and modified
	// Here we are adding the extension for the configurations file based on the
	// config type selection for saving.
	private String getConfigFile(String filename, String configType) {
		logger.debug("getConfigFile(String filename, String scope, String configType) as getConfigFile("+filename+", "+configType+") called");
		filename = FilenameUtils.removeExtension(filename);
		String id = configType;

		if (id != null) {
			if (id.equals(ConfigPolicy.JSON_CONFIG) || id.contains("Firewall")) {
				filename = filename + ".json";
			}
			if (id.equals(ConfigPolicy.XML_CONFIG)) {
				filename = filename + ".xml";
			}
			if (id.equals(ConfigPolicy.PROPERTIES_CONFIG)) {
				filename = filename + ".properties";
			}
			if (id.equals(ConfigPolicy.OTHER_CONFIG)) {
				filename = filename + ".txt";
			}
		}
		return filename;
	}
	
	private String[] getNameScopeAndVersionFromPdpPolicy(String fileName){
		String[] splitByDots = fileName.split("\\.");
		if(splitByDots.length < 3){
			//should we throw something
			return null;
		}
		String policyName = splitByDots[splitByDots.length-3];
		String version = splitByDots[splitByDots.length-2];
		//policy names now include version
		String scope = "";
		for(int i=0;i<splitByDots.length-3;i++){
			scope += ".".concat(splitByDots[i]);
		}
		//remove the first dot
		if(scope.length() > 0){
			scope = scope.substring(1);
		}
		String[] returnArray = new String[3];
		returnArray[0] = policyName + "." + version + ".xml";
		returnArray[2] = version;
		returnArray[1] = scope;
		return returnArray;
	}

	//copied from StdEngine.java
	public static String createNewPDPGroupId(String name) {
		String id = name;
		// replace "bad" characters with sequences that will be ok for file names and properties keys.
		id = id.replace(" ", "_sp_");
		id = id.replace("\t", "_tab_");
		id = id.replace("\\", "_bksl_");
		id = id.replace("/", "_sl_");
		id = id.replace(":", "_col_");
		id = id.replace("*", "_ast_");
		id = id.replace("?", "_q_");
		id = id.replace("\"", "_quo_");
		id = id.replace("<", "_lt_");
		id = id.replace(">", "_gt_");
		id = id.replace("|", "_bar_");
		id = id.replace("=", "_eq_");
		id = id.replace(",", "_com_");
		id = id.replace(";", "_scom_");

		return id;
	}

	/**
	 * Checks if any of the given strings are empty or null
	 * @param strings One or more Strings (or nulls) to check if they are null or empty
	 * @return true if one or more of the given strings are empty or null
	 */
	private static boolean isNullOrEmpty(String... strings){
		for(String s : strings){
			if(!(s instanceof String)){
				return true;
			}
			if(s.equals("")){
				return true;
			}
		}
		return false;
	}

	
	private class PolicyDBDaoTransactionInstance implements PolicyDBDaoTransaction {
		private EntityManager em;
		private final Object emLock = new Object();
		long policyId;
		long groupId;
		long pdpId;
		String newGroupId;
		private boolean operationRun = false;
		private final Thread transactionTimer;

		private PolicyDBDaoTransactionInstance(){
			//call the constructor with arguments
			this(Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT)),
					Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_WAIT)));
		}
		//timeout is how long the transaction can sit before rolling back
		//wait time is how long to wait for the transaction to start before throwing an exception
		private PolicyDBDaoTransactionInstance(int transactionTimeout, int transactionWaitTime){
			if(logger.isDebugEnabled()){
				logger.debug("\n\nPolicyDBDaoTransactionInstance() as PolicyDBDaoTransactionInstance() called:"
						+ "\n   transactionTimeout = " + transactionTimeout
						+ "\n   transactionWaitTime = " + transactionWaitTime + "\n\n");
			}
			this.em = emf.createEntityManager();
			policyId = -1;
			groupId = -1;
			pdpId = -1;
			newGroupId = null;
			synchronized(emLock){
				try{
					startTransactionSynced(this.em,transactionWaitTime);
				} catch(Exception e){
					throw new PersistenceException("Could not lock transaction within "+transactionWaitTime+" milliseconds");
				}
			}
			class TransactionTimer implements Runnable {

				private int sleepTime;
				public TransactionTimer(int timeout){
					this.sleepTime = timeout;
				}
				@Override
				public void run() {
					if(logger.isDebugEnabled()){
						Date date= new java.util.Date();
						logger.debug("\n\nTransactionTimer.run() - SLEEPING: "
								+ "\n   sleepTime (ms) = " + sleepTime 
								+ "\n   TimeStamp = " + date.getTime()
								+ "\n\n");
					}
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						//probably, the transaction was completed, the last thing we want to do is roll back
						if(logger.isDebugEnabled()){
							Date date= new java.util.Date();
							logger.debug("\n\nTransactionTimer.run() - WAKE Interrupt: "
									+ "\n   TimeStamp = " + date.getTime()
									+ "\n\n");
						}
						return;
					}
					if(logger.isDebugEnabled()){
						Date date= new java.util.Date();
						logger.debug("\n\nTransactionTimer.run() - WAKE Timeout: "
								+ "\n   TimeStamp = " + date.getTime()
								+ "\n\n");
					}
					rollbackTransaction();					
				}

			}

			transactionTimer = new Thread(new TransactionTimer(transactionTimeout),"transactionTimerThread");
			transactionTimer.start();


		}

		private void checkBeforeOperationRun(){
			checkBeforeOperationRun(false);
		}
		private void checkBeforeOperationRun(boolean justCheckOpen){
			if(!isTransactionOpen()){
				PolicyLogger.error("There is no transaction currently open");
				throw new IllegalStateException("There is no transaction currently open");
			}
			if(operationRun && !justCheckOpen){
				PolicyLogger.error("An operation has already been performed and the current transaction should be committed");
				throw new IllegalStateException("An operation has already been performed and the current transaction should be committed");
			}
			operationRun = true;
		}
		@Override
		public void commitTransaction() {
			synchronized(emLock){
				logger.debug("commitTransaction() as commitTransaction() called");
				if(!isTransactionOpen()){
					logger.warn("There is no open transaction to commit");
					try{
						em.close();
					} catch(Exception e){
						logger.error("Exception Occured"+e);
					}
					return;
				}
				try{
					em.getTransaction().commit();
				} catch(RollbackException e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught RollbackException on em.getTransaction().commit()");
					throw new PersistenceException("The commit failed. Message:\n"+e.getMessage());
				}
				em.close();
				// need to revisit
				if(policyId >= 0){
					if(newGroupId != null){
						try{
							notifyOthers(policyId,POLICY_NOTIFICATION,newGroupId);
						} catch(Exception e){
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on notifyOthers("+policyId+","+POLICY_NOTIFICATION+","+newGroupId+")");
						}
					} else {
						try{
							notifyOthers(policyId,POLICY_NOTIFICATION);
						} catch(Exception e){
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on notifyOthers("+policyId+","+POLICY_NOTIFICATION+")");
						}
					}
				}
				if(groupId >= 0){
					//we don't want commit to fail just because this does
					if(newGroupId != null){
						try{
							notifyOthers(groupId,GROUP_NOTIFICATION,newGroupId);
						} catch(Exception e){
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on notifyOthers("+groupId+","+GROUP_NOTIFICATION+","+newGroupId+")");
						}
					} else {
						try{
							notifyOthers(groupId,GROUP_NOTIFICATION);
						} catch(Exception e){
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on notifyOthers("+groupId+","+GROUP_NOTIFICATION+")");
						}
					}
				}
				if(pdpId >= 0){
					//we don't want commit to fail just because this does
					try{
						notifyOthers(pdpId,PDP_NOTIFICATION);
					} catch(Exception e){
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on notifyOthers("+pdpId+","+PDP_NOTIFICATION+")");
					}
				}
			}
			if(transactionTimer instanceof Thread){
				transactionTimer.interrupt();
			}
		}

		@Override
		public void rollbackTransaction() {
			logger.debug("rollbackTransaction() as rollbackTransaction() called");
			synchronized(emLock){
				if(isTransactionOpen()){	

					try{
						em.getTransaction().rollback();					
					} catch(Exception e){
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Could not rollback transaction");
					}
					try{
						em.close();
					}catch(Exception e){
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Could not close EntityManager");
					}

				} else {
					try{
						em.close();
					}catch(Exception e){
						logger.warn("Could not close already closed transaction");
					}
				}

			}
			if(transactionTimer instanceof Thread){
				transactionTimer.interrupt();
			}


		}

		private void createPolicy(PolicyRestAdapter policy, String username, String policyScope, String policyName, String policyDataString) {
			logger.debug("createPolicy(PolicyRestAdapter policy, String username, String policyScope, String policyName, String policyDataString) as createPolicy("+policy+", "+username+", "+policyScope+", "+policyName+", "+policyDataString+") called");
			synchronized(emLock){
				checkBeforeOperationRun();
				String configName = policyName;
				if(policyName.contains("Config_")){
					policyName = policyName.replace(".Config_", ":Config_");
				}else if(policyName.contains("Action_")){
					policyName = policyName.replace(".Action_", ":Action_");
				}else if(policyName.contains("Decision_")){
					policyName = policyName.replace(".Decision_", ":Decision_");
				}
				policyName = policyName.split(":")[1];
				Query createPolicyQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:policyName");			
				createPolicyQuery.setParameter("scope", policyScope);
				createPolicyQuery.setParameter("policyName", policyName);
				List<?> createPolicyQueryList = createPolicyQuery.getResultList();
				PolicyEntity newPolicyEntity;
				boolean update;
				if(createPolicyQueryList.size() < 1){
					newPolicyEntity = new PolicyEntity();
					update = false;
				} else if(createPolicyQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one policy with the same scope, name, and deleted status were found in the database");
					throw new PersistenceException("Somehow, more than one policy with the same scope, name, and deleted status were found in the database");
				} else {
					newPolicyEntity = (PolicyEntity)createPolicyQueryList.get(0);
					update = true;
				}			

				ActionBodyEntity newActionBodyEntity = null;
				if(policy.getPolicyType().equals("Action")){
					boolean abupdate = false;
					if(newPolicyEntity.getActionBodyEntity() == null){
						newActionBodyEntity = new ActionBodyEntity();
					}else{
						newActionBodyEntity = em.find(ActionBodyEntity.class, newPolicyEntity.getActionBodyEntity().getActionBodyId());
						abupdate = true;
					}

					if(newActionBodyEntity != null){
						if(!abupdate){
							em.persist(newActionBodyEntity);
						}
						//build the file path
						//trim the .xml off the end
						String policyNameClean = FilenameUtils.removeExtension(configName);
						String actionBodyName =  policyNameClean + ".json";
						Path actionBodyPath = Paths.get(Webapps.getActionHome(), actionBodyName);
						if(logger.isDebugEnabled()){
							logger.debug("\nPolicyDBDao.createPolicy"
									+ "\n   actionBodyPath = " + actionBodyPath);
						}
						//get the action body
						String actionBodyString = null;
						String actionBodyPathStr = null;
						InputStream fileContentStream = null;

						if (Files.exists(actionBodyPath)) {
							try {
								actionBodyPathStr = (actionBodyPath != null ? actionBodyPath.toString() : null);
								fileContentStream = new FileInputStream(actionBodyPathStr);
								actionBodyString = IOUtils.toString(fileContentStream);
								if(logger.isDebugEnabled()){
									logger.debug("\nPolicyDBDao.createPolicy"
											+ "\n   actionBodyPathStr = " + actionBodyPathStr
											+ "\n   actionBodyString = " + actionBodyString);
								}
							} catch (FileNotFoundException e) {
								PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught FileNotFoundException on new actionBodyPathStr FileInputStream("+actionBodyPathStr+")");
								throw new IllegalArgumentException("The actionBodyPathStr file path " + actionBodyPathStr + " does not exist" 
										+ "\nEXCEPTION: " + e);
							} catch(IOException e2){
								PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, "PolicyDBDao", "Caught IOException on actionBodyPath newIOUtils.toString("+fileContentStream+")");
								throw new IllegalArgumentException("The actionBodyPath file path cannot be read" + fileContentStream 
										+ "\nEXCEPTION: " + e2);
							} finally {
								IOUtils.closeQuietly(fileContentStream);
							}

							if(actionBodyString == null){
								throw new IllegalArgumentException("The file path (" + actionBodyPathStr + ") cannot be read");
							}
						} else {
							actionBodyString = "{}";
						}

						newActionBodyEntity.setActionBody(actionBodyString);
						newActionBodyEntity.setActionBodyName(actionBodyName);
						newActionBodyEntity.setModifiedBy("PolicyDBDao.createPolicy()");
						newActionBodyEntity.setDeleted(false);
						if(!abupdate){
							newActionBodyEntity.setCreatedBy("PolicyDBDao.createPolicy()");
						}
						if(logger.isDebugEnabled()){
							logger.debug("\nPolicyDBDao.createPolicy"
									+ "\n   newActionBodyEntity.getActionBody() = " + newActionBodyEntity.getActionBody()
									+ "\n   newActionBodyEntity.getActionBodyName() = " + newActionBodyEntity.getActionBodyName()
									+ "\n   newActionBodyEntity.getModifiedBy() = " + newActionBodyEntity.getModifiedBy()
									+ "\n   newActionBodyEntity.getCreatedBy() = " + newActionBodyEntity.getCreatedBy()
									+ "\n   newActionBodyEntity.isDeleted() = " + newActionBodyEntity.isDeleted()
									+ "\n   FLUSHING to DB");
						}
						//push the actionBodyEntity to the DB
						em.flush();
					}else{
						//newActionBodyEntity == null
						//We have a actionBody in the policy but we found no actionBody in the DB
						String msg = "\n\nPolicyDBDao.createPolicy - Incoming Action policy had an "
								+ "actionBody, but it could not be found in the DB for update."
								+ "\n  policyScope = " + policyScope
								+ "\n  policyName = " + policyName + "\n\n";
						PolicyLogger.error("PolicyDBDao.createPolicy - Incoming Action policy had an actionBody, but it could not be found in the DB for update: policyName = " + policyName);
						throw new IllegalArgumentException(msg);
					}
				}

				ConfigurationDataEntity newConfigurationDataEntity;
				if(policy.getPolicyType().equals("Config")){
					boolean configUpdate;
					if(newPolicyEntity.getConfigurationData() == null){
						newConfigurationDataEntity = new ConfigurationDataEntity();
						configUpdate = false;
					} else {
						newConfigurationDataEntity = em.find(ConfigurationDataEntity.class, newPolicyEntity.getConfigurationData().getConfigurationDataId());
						configUpdate = true;
					}

					if(newConfigurationDataEntity != null){
						if(!configUpdate){
							em.persist(newConfigurationDataEntity);
						}
						if(!stringEquals(newConfigurationDataEntity.getConfigurationName(),getConfigFile(configName,policy))){
							newConfigurationDataEntity.setConfigurationName(getConfigFile(configName,policy));
						}
						if(newConfigurationDataEntity.getConfigType() == null || !newConfigurationDataEntity.getConfigType().equals(policy.getConfigType())){
							newConfigurationDataEntity.setConfigType(policy.getConfigType());
						}
						if(!configUpdate){
							newConfigurationDataEntity.setCreatedBy(username);
						}
						if(newConfigurationDataEntity.getModifiedBy() == null || !newConfigurationDataEntity.getModifiedBy().equals(username)){
							newConfigurationDataEntity.setModifiedBy(username);
						}					
						if(newConfigurationDataEntity.getDescription() == null || !newConfigurationDataEntity.getDescription().equals("")){
							newConfigurationDataEntity.setDescription("");
						}
						if(newConfigurationDataEntity.getConfigBody() == null || newConfigurationDataEntity.getConfigBody().isEmpty() ||
								(!newConfigurationDataEntity.getConfigBody().equals(policy.getConfigBodyData()))){
							//hopefully one of these won't be null
							if(policy.getConfigBodyData() == null || policy.getConfigBodyData().isEmpty()){
								newConfigurationDataEntity.setConfigBody(policy.getJsonBody());
							}else{
								newConfigurationDataEntity.setConfigBody(policy.getConfigBodyData());
							}
						}
						if(newConfigurationDataEntity.isDeleted() == true){
							newConfigurationDataEntity.setDeleted(false);
						}

						em.flush();
					}else{
						//We have a configurationData body in the policy but we found no configurationData body in the DB
						String msg = "\n\nPolicyDBDao.createPolicy - Incoming Config policy had a "
								+ "configurationData body, but it could not be found in the DB for update."
								+ "\n  policyScope = " + policyScope
								+ "\n  policyName = " + policyName + "\n\n";
						PolicyLogger.error("PolicyDBDao.createPolicy - Incoming Config policy had a configurationData body, but it could not be found in the DB for update: policyName = " + policyName);
						throw new IllegalArgumentException(msg);
					}

				} else {
					newConfigurationDataEntity = null;
				}
				if(!update){
					em.persist(newPolicyEntity);
				}

				policyId = newPolicyEntity.getPolicyId();

				if(!stringEquals(newPolicyEntity.getPolicyName(),policyName)){
					newPolicyEntity.setPolicyName(policyName);
				}
				if(!stringEquals(newPolicyEntity.getCreatedBy(),username)){
					newPolicyEntity.setCreatedBy(username);
				}
				if(!stringEquals(newPolicyEntity.getDescription(),policy.getPolicyDescription())){
					newPolicyEntity.setDescription(policy.getPolicyDescription());
				}
				if(!stringEquals(newPolicyEntity.getModifiedBy(),username)){
					newPolicyEntity.setModifiedBy(username);
				}
				if(!stringEquals(newPolicyEntity.getPolicyData(),policyDataString)){
					newPolicyEntity.setPolicyData(policyDataString);
				}
				if(!stringEquals(newPolicyEntity.getScope(),policyScope)){
					newPolicyEntity.setScope(policyScope);
				}
				if(newPolicyEntity.isDeleted() == true){
					newPolicyEntity.setDeleted(false);
				}
				newPolicyEntity.setConfigurationData(newConfigurationDataEntity);
				newPolicyEntity.setActionBodyEntity(newActionBodyEntity);


				em.flush();
				this.policyId = newPolicyEntity.getPolicyId();
			}

			return;
		}

		@SuppressWarnings("unused")
		public PolicyEntity getPolicy(int policyID){
			return getPolicy(policyID,null,null);
		}
		public PolicyEntity getPolicy(String policyName,String scope){
			return getPolicy(-1,policyName,scope);
		}
		private PolicyEntity getPolicy(int policyID, String policyName,String scope){
			logger.debug("getPolicy(int policyId, String policyName) as getPolicy("+policyID+","+policyName+") called");
			if(policyID < 0 && isNullOrEmpty(policyName,scope)){
				throw new IllegalArgumentException("policyID must be at least 0 or policyName must be not null or blank");
			}

			synchronized(emLock){
				checkBeforeOperationRun(true);
				//check if group exists
				String policyId;
				Query policyQuery;
				if(!isNullOrEmpty(policyName,scope)){
					policyId = policyName;
					policyQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName=:name AND p.scope=:scope");
					policyQuery.setParameter("name", policyId);
					policyQuery.setParameter("scope", scope);
				} else{
					policyId = String.valueOf(policyID);
					policyQuery = em.createNamedQuery("PolicyEntity.FindById");
					policyQuery.setParameter("id", policyId);
				}
				List<?> policyQueryList;
				try{
					policyQueryList = policyQuery.getResultList();
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get policy with policyQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get policy "+policyId);
				}
				if(policyQueryList.size() < 1){
					PolicyLogger.error("Policy does not exist with id "+policyId);
					throw new PersistenceException("Group policy is being added to does not exist with id "+policyId);
				} else if(policyQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one policy with the id "+policyId+" were found in the database");
					throw new PersistenceException("Somehow, more than one policy with the id "+policyId+" were found in the database");
				}
				return (PolicyEntity)policyQueryList.get(0);
			}
		}

		@Override
		public void renamePolicy(String oldPath, String newPath,String username){
/*			String[] oldPolicy = getScopeAndNameAndType(oldPath);
			String[] newPolicy = getScopeAndNameAndType(newPath);
			if(oldPolicy == null || newPolicy == null){
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW+"Could not parse one or more of the path names: "
						+oldPath+", "+newPath);
				throw new IllegalArgumentException("Could not parse one or more of the path names");
			}
			synchronized (emLock) {
				checkBeforeOperationRun();

				PolicyEntity existingPolicy;
				boolean existingPolicyDeleted = false;
				List<?> groups = null;
				try{
					existingPolicy = getPolicy(newPolicy[1],newPolicy[0]);
				} catch(Exception e){
					existingPolicy = null;
				}
				if(existingPolicy != null && !existingPolicy.isDeleted()){
					logger.error("The policy named "+existingPolicy.getPolicyName()+" already exists, cannot rename policy: "+newPolicy);
					throw new IllegalArgumentException("The policy named "+existingPolicy.getPolicyName()+" already exists, cannot rename policy: "+newPolicy);
				} else if(existingPolicy != null && existingPolicy.isDeleted()){
					try{
						Query getGroups = em.createQuery("SELECT g FROM GroupEntity g JOIN g.policies p WHERE p.policyId=:pid");

						getGroups.setParameter("pid", existingPolicy.getPolicyId());
						groups = getGroups.getResultList();
					}catch(Exception e){
						groups = new LinkedList<>();
					}
					for(Object o : groups){
						GroupEntity group = (GroupEntity)o;
						group.removePolicyFromGroup(existingPolicy);
					}
					try{
						em.flush();
					}catch(Exception e){
						logger.error("Error while removing the policy from groups: "+existingPolicy.getPolicyName());
					}
					try{
						em.remove(existingPolicy);
						em.flush();
					}catch(Exception e){
						logger.error("Could not remove the existing deleted policy: "+existingPolicy.getPolicyName());
					}
					existingPolicyDeleted = true;
					//create the new policy
					//for each of the groups, add the new policy
				}

				PolicyEntity policyToRename;
				try{
					policyToRename = getPolicy(oldPolicy[1],oldPolicy[0]);
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "PolicyDBDao", "Could not get policy record to rename: "
							+oldPolicy[1]);
					throw new PersistenceException("Could not get policy record to rename");
				}
				String policyDataString = null;
				InputStream fileContentStream = null;
				String policyFilePath = Paths.get(oldPath).toAbsolutePath().toString();
				//I want to try the old path first, then if it doesn't work, try the new path
				for(int i=0;i<2;i++){
					try {
						fileContentStream = new FileInputStream(policyFilePath);
						policyDataString = IOUtils.toString(fileContentStream);
					} catch (FileNotFoundException e) {
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught FileNotFoundException on new FileInputStream("+policyFilePath+")");
						//if we can't find the oldPath, we'll try the new path
						if(i == 0){
							policyFilePath = Paths.get(newPath).toAbsolutePath().toString();
							continue;
						}
						throw new IllegalArgumentException("The file path does not exist");
					} catch(IOException e2){
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e2, "PolicyDBDao", "Caught IOException on newIOUtils.toString("+fileContentStream+")");
						throw new IllegalArgumentException("The file path cannot be read");
					} finally {
						IOUtils.closeQuietly(fileContentStream);
					}
					if(policyDataString == null){
						throw new IllegalArgumentException("The file path cannot be read");
					}
					//escape the loop
					i=2;
				}
				policyToRename.setPolicyName(newPolicy[1]);
				policyToRename.setPolicyData(policyDataString);
				policyToRename.setScope(newPolicy[0]);
				policyToRename.setModifiedBy(username);
				if(policyToRename.getConfigurationData() != null){
					String configType = policyToRename.getConfigurationData().getConfigType();
					policyToRename.getConfigurationData().setConfigurationName(getConfigFile(newPolicy[1], configType));
					policyToRename.getConfigurationData().setModifiedBy(username);
				}
				if(policyToRename.getActionBodyEntity() != null){
					String newActionName = newPolicy[0]+"."+removeFileExtension(newPolicy[1])+".json";
					policyToRename.getActionBodyEntity().setActionBodyName(newActionName);
					policyToRename.getActionBodyEntity().setModifiedBy(username);
				}
				if(existingPolicyDeleted){
					for(Object o : groups){

						GroupEntity group = (GroupEntity)o;
						group.addPolicyToGroup(policyToRename);
					}
				}
				em.flush();
				this.policyId = policyToRename.getPolicyId();
				this.newGroupId = oldPath;
			}*/
		}

		@Override
		public GroupEntity getGroup(long groupKey){
			logger.debug("getGroup(int groupKey) as getGroup("+groupKey+") called");
			if(groupKey < 0){
				throw new IllegalArgumentException("groupKey must be at least 0");
			}
			synchronized(emLock){
				checkBeforeOperationRun(true);
				//check if group exists
				Query groupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupKey=:groupKey");
				groupQuery.setParameter("groupKey", groupKey);			
				List<?> groupQueryList;
				try{
					groupQueryList = groupQuery.getResultList();
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get group with groupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get group "+groupKey);
				}
				if(groupQueryList.size() < 1){
					PolicyLogger.error("Group does not exist with groupKey "+groupKey);
					throw new PersistenceException("Group does not exist with groupKey "+groupKey);
				} else if(groupQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one group with the groupKey "+groupKey+" were found in the database");
					throw new PersistenceException("Somehow, more than one group with the groupKey "+groupKey+" were found in the database");
				}
				return (GroupEntity)groupQueryList.get(0);
			}
		}

		@Override
		public GroupEntity getGroup(String groupId){
			logger.debug("getGroup(String groupId) as getGroup("+groupId+") called");
			if(isNullOrEmpty(groupId)){
				throw new IllegalArgumentException("groupId must not be null or empty");
			}
			synchronized(emLock){
				checkBeforeOperationRun(true);
				//check if group exists
				Query groupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId");
				groupQuery.setParameter("groupId", groupId);			
				List<?> groupQueryList;
				try{
					groupQueryList = groupQuery.getResultList();
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get group with groupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get group "+groupId);
				}
				if(groupQueryList.size() < 1){
					PolicyLogger.error("Group does not exist with id "+groupId);
					throw new PersistenceException("Group does not exist with id "+groupId);
				} else if(groupQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one group with the id "+groupId+" were found in the database");
					throw new PersistenceException("Somehow, more than one group with the id "+groupId+" were found in the database");
				}
				return (GroupEntity)groupQueryList.get(0);
			}
		}
		@Override
		public List<?> getPdpsInGroup(long groupKey){
			logger.debug("getPdpsInGroup(int groupKey) as getPdpsInGroup("+groupKey+") called");
			if(groupKey < 0){
				throw new IllegalArgumentException("groupId must not be < 0");
			}			
			synchronized(emLock){
				checkBeforeOperationRun(true);
				Query pdpsQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.groupEntity=:group");
				pdpsQuery.setParameter("group", getGroup(groupKey));
				return pdpsQuery.getResultList();
			}
		}
		@Override
		public PdpEntity getPdp(long pdpKey){
			logger.debug("getPdp(int pdpKey) as getPdp("+pdpKey+") called");
			if(pdpKey < 0){
				throw new IllegalArgumentException("pdpKey must be at least 0");
			}
			synchronized(emLock){
				checkBeforeOperationRun(true);
				//check if group exists
				Query pdpQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpKey=:pdpKey");
				pdpQuery.setParameter("pdpKey", pdpKey);			
				List<?> pdpQueryList;
				try{
					pdpQueryList = pdpQuery.getResultList();
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get pdp with pdpQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get pdp "+pdpKey);
				}
				if(pdpQueryList.size() < 1){
					PolicyLogger.error("Pdp does not exist with pdpKey "+pdpKey);
					throw new PersistenceException("Pdp does not exist with pdpKey "+pdpKey);
				} else if(pdpQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one pdp with the pdpKey "+pdpKey+" were found in the database");
					throw new PersistenceException("Somehow, more than one pdp with the pdpKey "+pdpKey+" were found in the database");
				}
				return (PdpEntity)pdpQueryList.get(0);
			}
		}
		
		public void deletePolicy(String policyToDeletes){
			/*synchronized(emLock){
				checkBeforeOperationRun();
				logger.debug("deletePolicy(String policyToDeletes) as deletePolicy("+policyToDeletes+") called");
				String[] scopeNameAndType = getScopeAndNameAndType(policyToDeletes);
				if(scopeNameAndType == null){
					throw new IllegalArgumentException("Could not parse file path");
				}
				String realScope = scopeNameAndType[0];
				String realName = scopeNameAndType[1];
				Query deletePolicyQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:policyName AND p.deleted=:deleted");			
				deletePolicyQuery.setParameter("scope",realScope);
				deletePolicyQuery.setParameter("policyName", realName);
				deletePolicyQuery.setParameter("deleted", false);
				List<?> deletePolicyQueryList = deletePolicyQuery.getResultList();
				if(deletePolicyQueryList.size() < 1){
					logger.warn("The policy being deleted could not be found.");
					return;
				} else if(deletePolicyQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one policy with the same scope, name, and deleted status were found in the database");
					throw new PersistenceException("Somehow, more than one policy with the same scope, name, and deleted status were found in the database");
				} else {
					PolicyEntity policyToDelete = (PolicyEntity)deletePolicyQueryList.get(0);
					policyToDelete.setDeleted(true);
					if(policyToDelete.getConfigurationData() != null){
						ConfigurationDataEntity cde = em.find(ConfigurationDataEntity.class,policyToDelete.getConfigurationData().getConfigurationDataId());					
						if(cde != null){
							cde.setDeleted(true);
						}
					}
					if(policyToDelete.getActionBodyEntity() != null){
						ActionBodyEntity abe = em.find(ActionBodyEntity.class,policyToDelete.getActionBodyEntity().getActionBodyId());					
						if(abe != null){
							abe.setDeleted(true);
						}
					}

					em.flush();
					this.policyId = policyToDelete.getPolicyId();

				}
			}
*/
		}


		@Override
		public boolean isTransactionOpen() {
			logger.debug("isTransactionOpen() as isTransactionOpen() called");
			synchronized(emLock){
				return em.isOpen() && em.getTransaction().isActive();	
			}
		}


		@Override
		public void clonePolicy(String oldPolicyPath, String newPolicyPath, String username){
			/*String[] oldPolicyData = getScopeAndNameAndType(oldPolicyPath);
			String[] newPolicyData = getScopeAndNameAndType(newPolicyPath);
			if(oldPolicyData == null || newPolicyData == null){
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW+"Could not parse one or more of the path names: "
						+oldPolicyPath+", "+newPolicyPath);
				throw new IllegalArgumentException("Could not parse the oldPolicyPath or newPolicyPath");
			}
			PolicyEntity oldPolicy;
			try{
				oldPolicy = getPolicy(oldPolicyData[1],oldPolicyData[0]);
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "PolicyDBDao", "Could not get policy record to clone: "
						+oldPolicyData[1]);
				throw new PersistenceException("Could not get policy record to clone");
			}
			ConfigurationDataEntity clonedConfig = null;
			if(oldPolicy.getConfigurationData() != null){
				clonedConfig = new ConfigurationDataEntity();
				em.persist(clonedConfig);
				clonedConfig.setConfigBody(oldPolicy.getConfigurationData().getConfigBody());
				clonedConfig.setConfigType(oldPolicy.getConfigurationData().getConfigType());
				clonedConfig.setCreatedBy(username);
				clonedConfig.setConfigurationName(getConfigFile(newPolicyData[1], oldPolicy.getConfigurationData().getConfigType()));
				clonedConfig.setDescription(oldPolicy.getConfigurationData().getDescription());
				clonedConfig.setModifiedBy(username);
				em.flush();
			}
			ActionBodyEntity clonedAction = null;
			if(oldPolicy.getActionBodyEntity() != null){
				clonedAction = new ActionBodyEntity();
				em.persist(clonedAction);
				clonedAction.setActionBody(oldPolicy.getActionBodyEntity().getActionBody());
				clonedAction.setActionBodyName(newPolicyData[0]+"."+newPolicyData[1]+".json");
				clonedAction.setCreatedBy(username);
				clonedAction.setModifiedBy(username);
				em.flush();
			}			

*/
		}

		private String processConfigPath(String configPath){
			String webappsPath = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS);
			if(webappsPath == null){
				logger.error("Webapps property does not exist");
				throw new IllegalArgumentException("Webapps property does not exist");
			}
			configPath = configPath.replace("$URL", webappsPath);
			//make sure the correct slashes are in
			try{
				configPath = Paths.get(configPath).toString();
			} catch(InvalidPathException e){
				logger.error("Invalid config path: "+configPath);
				throw new IllegalArgumentException("Invalid config path: "+configPath);
			}
			return configPath;
		}
		private String readConfigFile(String configPath){
			String configDataString = null;
			InputStream configContentStream = null;
			try {
				configContentStream = new FileInputStream(configPath);
				configDataString = IOUtils.toString(configContentStream);
			} catch (FileNotFoundException e) {
				logger.error("Caught FileNotFoundException on new FileInputStream("+configPath+")",e);
				throw new IllegalArgumentException("The config file path does not exist");
			} catch(IOException e2){
				logger.error("Caught IOException on newIOUtils.toString("+configContentStream+")",e2);
				throw new IllegalArgumentException("The config file path cannot be read");
			} finally {
				IOUtils.closeQuietly(configContentStream);
			}
			if(configDataString == null){
				throw new IllegalArgumentException("The config file path cannot be read");
			}
			return configDataString;
		}

		@Override
		public void createPolicy(Policy policy, String username){
			InputStream policyXmlStream = null;
			try{
				logger.debug("createPolicy(PolicyRestAdapter policy, String username) as createPolicy("+policy+","+username+") called");
				String policyScope = policy.policyAdapter.getDomainDir().replace(File.separator, ".");
				//Does not need to be XACMLPolicyWriterWithPapNotify since it is already in the PAP
				//and this transaction is intercepted up stream.
				String policyDataString;
				try {
					policyXmlStream = XACMLPolicyWriter.getXmlAsInputStream((PolicyType)policy.getCorrectPolicyDataObject());
					policyDataString = IOUtils.toString(policyXmlStream);
				} catch (IOException e) {
					policyDataString = "could not read";
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught IOException on IOUtils.toString("+policyXmlStream+")");
					throw new IllegalArgumentException("Cannot parse the policy xml from the PolicyRestAdapter.");
				}
				IOUtils.closeQuietly(policyXmlStream);
				String configPath = "";
				if (policy.policyAdapter.getPolicyType().equalsIgnoreCase("Config")) {
					configPath = evaluateXPath("/Policy/Rule/AdviceExpressions/AdviceExpression[contains(@AdviceId,'ID')]/AttributeAssignmentExpression[@AttributeId='URLID']/AttributeValue/text()", policyDataString);
				} else if (policy.policyAdapter.getPolicyType().equalsIgnoreCase("Action")) {
					configPath = evaluateXPath("/Policy/Rule/ObligationExpressions/ObligationExpression[contains(@ObligationId, " +policy.policyAdapter.getActionAttribute()+ ")]/AttributeAssignmentExpression[@AttributeId='body']/AttributeValue/text()", policyDataString);
				}

				String prefix = null;
				if (policy.policyAdapter.getPolicyType().equalsIgnoreCase("Config")) {

					prefix = configPath.substring(configPath.indexOf(policyScope+".")+policyScope.concat(".").length(), configPath.indexOf(policy.policyAdapter.getPolicyName()));
					if(isNullOrEmpty(policy.policyAdapter.getConfigBodyData())){
						String configData = "";
						try{
							String newConfigPath = configPath;
							try{
								newConfigPath = processConfigPath(newConfigPath);							
							}catch(Exception e2){
								logger.error("Could not process config path: "+newConfigPath,e2);
							}
							configData = readConfigFile(newConfigPath);
						}catch(Exception e){
							logger.error("Could not read config body data for "+configPath,e);
						}
						policy.policyAdapter.setConfigBodyData(configData);
					}
				} else if (policy.policyAdapter.getPolicyType().equalsIgnoreCase("Action")) {
					prefix = "Action_";
				} else if (policy.policyAdapter.getPolicyType().equalsIgnoreCase("Decision")) {
					prefix = "Decision_";
				}

				if(!(policy.policyAdapter.getData() instanceof PolicyType)){
					PolicyLogger.error("The data field is not an instance of PolicyType");
					throw new IllegalArgumentException("The data field is not an instance of PolicyType");
				}
				String finalName = policyScope + "." + prefix+policy.policyAdapter.getPolicyName()+"."+((PolicyType)policy.policyAdapter.getData()).getVersion()+".xml";
				if(policy.policyAdapter.getConfigType() == null || policy.policyAdapter.getConfigType().equals("")){
					//get the config file extension
					String ext = "";
					if (configPath != null) {
						if (!configPath.equalsIgnoreCase("")) {
							ext = configPath.substring(configPath.lastIndexOf('.'), configPath.length());;
						}
					}

					if(ext.contains("txt")){
						policy.policyAdapter.setConfigType(OTHER_CONFIG);
					} else if(ext.contains("json")){
						policy.policyAdapter.setConfigType(JSON_CONFIG);
					} else if(ext.contains("xml")){
						policy.policyAdapter.setConfigType(XML_CONFIG);
					} else if(ext.contains("properties")){
						policy.policyAdapter.setConfigType(PROPERTIES_CONFIG);
					} else {
						if (policy.policyAdapter.getPolicyType().equalsIgnoreCase("Action")){
							policy.policyAdapter.setConfigType(JSON_CONFIG);
						}
					}
				}

				createPolicy(policy.policyAdapter, username, policyScope,finalName,policyDataString);
			}finally{
				if(policyXmlStream != null){
					try {
						policyXmlStream.close();
					} catch (IOException e) {
						logger.error("Exception Occured while closing input stream"+e);
					}
				}
			}
		}

		@Override
		public void close(){
			synchronized(emLock){
				if(em.isOpen()){
					if(em.getTransaction().isActive()){
						em.getTransaction().rollback();
					}
					em.close();
				}
				if(transactionTimer instanceof Thread){
					transactionTimer.interrupt();
				}
			}
		}



		@Override
		public void createGroup(String groupId, String groupName, String groupDescription, String username) {
			logger.debug("deletePolicy(String policyToDeletes) as createGroup("+groupId+", "+groupName+", "+groupDescription+") called");
			if(isNullOrEmpty(groupId, groupName, username)){
				throw new IllegalArgumentException("groupId, groupName, and username must not be null or empty");
			}
			if(!(groupDescription instanceof String)){
				groupDescription = "";
			}

			synchronized(emLock){
				checkBeforeOperationRun();
				Query checkGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				checkGroupQuery.setParameter("groupId", groupId);
				checkGroupQuery.setParameter("deleted", false);
				List<?> checkGroupQueryList;
				try{
					checkGroupQueryList = checkGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on checkGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check for existing group");
				}
				if(checkGroupQueryList.size() > 0){
					PolicyLogger.error("The group being added already exists with id "+groupId);
					throw new PersistenceException("The group being added already exists with id "+groupId);
				}
				GroupEntity newGroup = new GroupEntity();
				em.persist(newGroup);
				newGroup.setCreatedBy(username);
				newGroup.setModifiedBy(username);
				newGroup.setGroupName(groupName);
				newGroup.setGroupId(groupId);
				newGroup.setDescription(groupDescription);

				em.flush();
				this.groupId = newGroup.getGroupKey();
			}
		}

		@Override
		public void updateGroup(OnapPDPGroup group, String username){
			logger.debug("updateGroup(PDPGroup group) as updateGroup("+group+","+username+") called");
			if(group == null){
				throw new IllegalArgumentException("PDPGroup group must not be null");
			}
			if(isNullOrEmpty(group.getId(), username)){
				throw new IllegalArgumentException("group.getId() and username must not be null or empty");
			}

			synchronized(emLock){
				checkBeforeOperationRun();
				Query getGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				getGroupQuery.setParameter("groupId", group.getId());
				getGroupQuery.setParameter("deleted", false);
				List<?> getGroupQueryList;
				try{
					getGroupQueryList = getGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on getGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get group "+group.getId()+" for editing");
				}
				if(getGroupQueryList.size() < 1){
					PolicyLogger.error("The group cannot be found to update with id "+group.getId());
					throw new PersistenceException("The group cannot be found to update with id "+group.getId());
				} else if(getGroupQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one group with the same id "+group.getId()+" and deleted status were found in the database");
					throw new PersistenceException("Somehow, more than one group with the same id "+group.getId()+" and deleted status were found in the database");
				}
				GroupEntity groupToUpdate = (GroupEntity)getGroupQueryList.get(0);
				if(!stringEquals(groupToUpdate.getModifiedBy(), username)){
					groupToUpdate.setModifiedBy(username);
				}
				if(group.getDescription() != null && !stringEquals(group.getDescription(),groupToUpdate.getDescription())){
					groupToUpdate.setDescription(group.getDescription());
				}
				//let's find out what policies have been deleted
				StdPDPGroup oldGroup = null;
				try {
					oldGroup = (StdPDPGroup) papEngine.getGroup(group.getId());
				} catch (PAPException e1) {
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e1, "PolicyDBDao", "We cannot get the group from the papEngine to delete policies");
				}
				if(oldGroup == null){
					PolicyLogger.error("We cannot get the group from the papEngine to delete policies");
				} else {

					Set<String> newPolicySet = new HashSet<>(group.getPolicies().size());
					//a multiple of n runtime is faster than n^2, so I am using a hashset to do the comparison
					for(PDPPolicy pol: group.getPolicies()){
						newPolicySet.add(pol.getId());
					}
					for(PDPPolicy pol : oldGroup.getPolicies()){
						//should be fast since getPolicies uses a HashSet in StdPDPGroup
						if(!newPolicySet.contains(pol.getId())){
							String[] scopeAndName = getNameScopeAndVersionFromPdpPolicy(pol.getId());
							PolicyEntity policyToDelete;
							try{
								policyToDelete = getPolicy(scopeAndName[0],scopeAndName[1]);
							}catch(Exception e){
								PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Could not get policy to remove: "+pol.getId());
								throw new PersistenceException("Could not get policy to remove: "+pol.getId());
							}
							groupToUpdate.getPolicies().remove(policyToDelete);

						}
					}
				}
				if(group.getName() != null && !stringEquals(group.getName(),groupToUpdate.getgroupName())){
					//we need to check if the new id exists in the database
					String newGroupId = createNewPDPGroupId(group.getName());
					Query checkGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
					checkGroupQuery.setParameter("groupId", newGroupId);
					checkGroupQuery.setParameter("deleted", false);
					List<?> checkGroupQueryList;
					try{
						checkGroupQueryList = checkGroupQuery.getResultList();
					} catch(Exception e){
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on checkGroupQuery.getResultList()");
						throw new PersistenceException("Query failed trying to check for existing group");
					}
					if(checkGroupQueryList.size() != 0){
						PolicyLogger.error("The new group name already exists, group id "+newGroupId);
						throw new PersistenceException("The new group name already exists, group id "+newGroupId);
					}
					groupToUpdate.setGroupId(newGroupId);
					groupToUpdate.setGroupName(group.getName());
					this.newGroupId = group.getId();
				}

				em.flush();
				this.groupId = groupToUpdate.getGroupKey();
			}
		}

		@Override
		public void addPdpToGroup(String pdpID, String groupID, String pdpName, String pdpDescription, int pdpJmxPort, String username) {
			logger.debug("addPdpToGroup(String pdpID, String groupID, String pdpName, String pdpDescription, int pdpJmxPort, String username) as addPdpToGroup("+pdpID+", "+groupID+", "+pdpName+", "+pdpDescription+", "+pdpJmxPort+", "+username+") called");
			if(isNullOrEmpty(pdpID, groupID,pdpName,username)){
				throw new IllegalArgumentException("pdpID, groupID, pdpName, and username must not be null or empty");
			}
			if(!(pdpDescription instanceof String)){
				pdpDescription = "";
			}
			synchronized(emLock){
				checkBeforeOperationRun();
				Query checkGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				checkGroupQuery.setParameter("groupId", groupID);
				checkGroupQuery.setParameter("deleted", false);
				List<?> checkGroupQueryList;
				try{
					checkGroupQueryList = checkGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check for existing group on checkGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check for existing group");
				}
				if(checkGroupQueryList.size() != 1){
					PolicyLogger.error("The group does not exist");
					throw new PersistenceException("The group does not exist");
				}
				Query checkDuplicateQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
				checkDuplicateQuery.setParameter("pdpId", pdpID);
				checkDuplicateQuery.setParameter("deleted", false);
				List<?> checkDuplicateList;
				try{
					checkDuplicateList = checkDuplicateQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check for duplicate PDP "+pdpID+" on checkDuplicateQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check for duplicate PDP "+pdpID);
				}
				PdpEntity newPdp;
				if(checkDuplicateList.size() > 0){
					logger.warn("PDP already exists with id "+pdpID);				
					newPdp = (PdpEntity)checkDuplicateList.get(0);
				} else {
					newPdp = new PdpEntity();
					em.persist(newPdp);
				}			

				newPdp.setCreatedBy(username);
				newPdp.setDeleted(false);
				newPdp.setDescription(pdpDescription);
				newPdp.setGroup((GroupEntity)checkGroupQueryList.get(0));
				newPdp.setJmxPort(pdpJmxPort);
				newPdp.setModifiedBy(username);
				newPdp.setPdpId(pdpID);
				newPdp.setPdpName(pdpName);

				em.flush();
				this.pdpId = newPdp.getPdpKey();

			}
		}


		@Override
		public void updatePdp(OnapPDP pdp, String username){
			logger.debug("updatePdp(PDP pdp, String username) as updatePdp("+pdp+","+username+") called");
			if(pdp == null){
				throw new IllegalArgumentException("PDP pdp must not be null");
			}
			if(isNullOrEmpty(pdp.getId(),username)){
				throw new IllegalArgumentException("pdp.getId() and username must not be null or empty");
			}

			synchronized(emLock){
				checkBeforeOperationRun();
				Query getPdpQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
				getPdpQuery.setParameter("pdpId", pdp.getId());
				getPdpQuery.setParameter("deleted", false);
				List<?> getPdpQueryList;
				try{
					getPdpQueryList = getPdpQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on getPdpQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get PDP "+pdp.getId());
				}
				if(getPdpQueryList.size() < 1){
					PolicyLogger.error("The pdp cannot be found to update with id "+pdp.getId());
					throw new PersistenceException("The pdp cannot be found to update with id "+pdp.getId());
				} else if(getPdpQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one pdp with the same id "+pdp.getId()+" and deleted status were found in the database");
					throw new PersistenceException("Somehow, more than one pdp with the same id "+pdp.getId()+" and deleted status were found in the database");
				}
				PdpEntity pdpToUpdate = (PdpEntity)getPdpQueryList.get(0);
				if(!stringEquals(pdpToUpdate.getModifiedBy(), username)){
					pdpToUpdate.setModifiedBy(username);
				}
				if(pdp.getDescription() != null && !stringEquals(pdp.getDescription(),pdpToUpdate.getDescription())){
					pdpToUpdate.setDescription(pdp.getDescription());
				}
				if(pdp.getName() != null && !stringEquals(pdp.getName(),pdpToUpdate.getPdpName())){
					pdpToUpdate.setPdpName(pdp.getName());
				}
				if(pdp.getJmxPort() != null && !pdp.getJmxPort().equals(pdpToUpdate.getJmxPort())){
					pdpToUpdate.setJmxPort(pdp.getJmxPort());
				}

				em.flush();
				this.pdpId = pdpToUpdate.getPdpKey();
			}
		}

		@Override
		public void movePdp(OnapPDP pdp, OnapPDPGroup group, String username){
			logger.debug("movePdp(PDP pdp, PDPGroup group, String username) as movePdp("+pdp+","+group+","+username+") called");
			if(pdp == null || group == null){
				throw new IllegalArgumentException("PDP pdp and PDPGroup group must not be null");
			}
			if(isNullOrEmpty(username,pdp.getId(),group.getId())){
				throw new IllegalArgumentException("pdp.getId(), group.getId(), and username must not be null or empty");
			}

			synchronized(emLock){
				checkBeforeOperationRun();
				//check if pdp exists
				Query getPdpQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
				getPdpQuery.setParameter("pdpId", pdp.getId());
				getPdpQuery.setParameter("deleted", false);
				List<?> getPdpQueryList;
				try{
					getPdpQueryList = getPdpQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on getPdpQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get pdp to move with id "+pdp.getId());
				}
				if(getPdpQueryList.size() < 1){
					PolicyLogger.error("The pdp cannot be found to move with id "+pdp.getId());
					throw new PersistenceException("The pdp cannot be found to move with id "+pdp.getId());
				} else if(getPdpQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one pdp with the same id "+pdp.getId()+" and deleted status were found in the database");
					throw new PersistenceException("Somehow, more than one pdp with the same id "+pdp.getId()+" and deleted status were found in the database");
				}

				//check if new group exists
				Query checkGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				checkGroupQuery.setParameter("groupId", group.getId());
				checkGroupQuery.setParameter("deleted", false);
				List<?> checkGroupQueryList;
				try{
					checkGroupQueryList = checkGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get group on checkGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get new group "+group.getId());
				}
				if(checkGroupQueryList.size() != 1){
					PolicyLogger.error("The group "+group.getId()+" does not exist");
					throw new PersistenceException("The group "+group.getId()+" does not exist");
				}
				GroupEntity groupToMoveInto = (GroupEntity)checkGroupQueryList.get(0);
				PdpEntity pdpToUpdate = (PdpEntity)getPdpQueryList.get(0);
				pdpToUpdate.setGroup(groupToMoveInto);
				if(!stringEquals(pdpToUpdate.getModifiedBy(), username)){
					pdpToUpdate.setModifiedBy(username);
				}

				em.flush();
				this.pdpId = pdpToUpdate.getPdpKey();
			}
		}

		@Override
		public void changeDefaultGroup(OnapPDPGroup group, String username){
			logger.debug("changeDefaultGroup(PDPGroup group, String username) as changeDefaultGroup("+group+","+username+") called");
			if(group == null){
				throw new IllegalArgumentException("PDPGroup group must not be null");
			}
			if(isNullOrEmpty(group.getId(),username)){
				throw new IllegalArgumentException("group.getId() and username must not be null or empty");
			}

			synchronized(emLock){
				checkBeforeOperationRun();
				Query getGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				getGroupQuery.setParameter("groupId", group.getId());
				getGroupQuery.setParameter("deleted", false);
				List<?> getGroupQueryList;
				try{
					getGroupQueryList = getGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on getGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get group "+group.getId());
				}
				if(getGroupQueryList.size() < 1){
					PolicyLogger.error("The group cannot be found to set default with id "+group.getId());				
					throw new PersistenceException("The group cannot be found to set default with id "+group.getId());
				} else if(getGroupQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one group with the same id "+group.getId()+" and deleted status were found in the database");
					throw new PersistenceException("Somehow, more than one group with the same id "+group.getId()+" and deleted status were found in the database");
				}
				GroupEntity newDefaultGroup = (GroupEntity)getGroupQueryList.get(0);
				newDefaultGroup.setDefaultGroup(true);
				if(!stringEquals(newDefaultGroup.getModifiedBy(), username)){
					newDefaultGroup.setModifiedBy(username);
				}

				em.flush();
				this.groupId = newDefaultGroup.getGroupKey();
				Query setAllGroupsNotDefault = em.createQuery("UPDATE GroupEntity g SET g.defaultGroup=:defaultGroup WHERE g.deleted=:deleted AND g.groupKey<>:groupKey");
				//not going to set modified by for all groups
				setAllGroupsNotDefault.setParameter("defaultGroup", false);
				setAllGroupsNotDefault.setParameter("deleted", false);
				setAllGroupsNotDefault.setParameter("groupKey", newDefaultGroup.getGroupKey());
				try{
					logger.info("set " + setAllGroupsNotDefault.executeUpdate() + " groups as not default");
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception on setAllGroupsNotDefault.executeUpdate()");
					throw new PersistenceException("Could not set all other groups default to false");
				}

				em.flush();
			}
		}


		@Override
		public void deleteGroup(OnapPDPGroup group, OnapPDPGroup moveToGroup, String username) throws PAPException {
			logger.debug("deleteGroup(PDPGroup group, PDPGroup moveToGroup, String username) as deleteGroup("+group+", "+moveToGroup+","+username+") called");
			if(group == null){
				throw new IllegalArgumentException("PDPGroup group cannot be null");
			}
			if(isNullOrEmpty(username,group.getId())){
				throw new IllegalArgumentException("group.getId() and and username must not be null or empty");
			}

			if(group.isDefaultGroup()){
				PolicyLogger.error("The default group "+group.getId()+" was attempted to be deleted. It cannot be.");
				throw new PAPException("You cannot delete the default group.");
			}
			synchronized(emLock){
				checkBeforeOperationRun();
				Query deleteGroupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				deleteGroupQuery.setParameter("groupId", group.getId());
				deleteGroupQuery.setParameter("deleted", false);
				List<?> deleteGroupQueryList;
				try{
					deleteGroupQueryList = deleteGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check if group exists deleteGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check if group exists");
				}
				if(deleteGroupQueryList.size() < 1){
					logger.warn("The group could not be found with id " + group.getId());
					return;
				} else if(deleteGroupQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one group with the id "+group.getId()+" were found in the database that are not deleted");
					throw new PersistenceException("Somehow, more than one group with the id "+group.getId()+" were found in the database that are not deleted");
				}				

				Query pdpsInGroupQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.groupEntity=:group and p.deleted=:deleted");
				pdpsInGroupQuery.setParameter("group", ((GroupEntity)deleteGroupQueryList.get(0)));
				pdpsInGroupQuery.setParameter("deleted", false);
				List<?> pdpsInGroupList;
				try{
					pdpsInGroupList = pdpsInGroupQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to get PDPs in group on pdpsInGroupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to get PDPs in group");
				}
				if(pdpsInGroupList.size() > 0){
					if(moveToGroup != null){
						Query checkMoveToGroupQuery = em.createQuery("SELECT o FROM GroupEntity o WHERE o.groupId=:groupId AND o.deleted=:deleted");
						checkMoveToGroupQuery.setParameter("groupId", moveToGroup.getId());
						checkMoveToGroupQuery.setParameter("deleted", false);
						List<?> checkMoveToGroupList;
						try{
							checkMoveToGroupList = checkMoveToGroupQuery.getResultList();
						} catch(Exception e){
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check if group exists checkMoveToGroupQuery.getResultList()");
							throw new PersistenceException("Query failed trying to check if group exists");
						}
						if(checkMoveToGroupList.size() < 1){
							PolicyLogger.error("The group could not be found with id " + moveToGroup.getId());
							throw new PersistenceException("The group could not be found with id " + moveToGroup.getId());
						} else if(checkMoveToGroupList.size() > 1){
							PolicyLogger.error("Somehow, more than one group with the id "+moveToGroup.getId()+" were found in the database that are not deleted");
							throw new PersistenceException("Somehow, more than one group with the id "+moveToGroup.getId()+" were found in the database that are not deleted");
						} else {
							GroupEntity newGroup = (GroupEntity)checkMoveToGroupList.get(0);
							for(Object pdpObject : pdpsInGroupList){
								PdpEntity pdp = (PdpEntity)pdpObject;
								pdp.setGroup(newGroup);
								if(!stringEquals(pdp.getModifiedBy(),username)){
									pdp.setModifiedBy(username);
								}
								try{

									em.flush();
									this.newGroupId = newGroup.getGroupId();
								} catch(PersistenceException e){
									PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught PersistenceException trying to set pdp group to null on em.flush()");
									throw new PersistenceException("Query failed trying to set pdp group to ");
								}
							}
						}
					} else {
						PolicyLogger.error("Group "+group.getId()+" is trying to be delted with PDPs. No group was provided to move them to");
						throw new PAPException("Group has PDPs. Must provide a group for them to move to");
					}
				} 

				//delete group here
				GroupEntity groupToDelete = (GroupEntity)deleteGroupQueryList.get(0);
				groupToDelete.setDeleted(true);
				if(!stringEquals(groupToDelete.getModifiedBy(), username)){
					groupToDelete.setModifiedBy(username);
				}
				em.flush();
				this.groupId = groupToDelete.getGroupKey();
			}
		}

		@Override
		public void addPolicyToGroup(String groupID, String policyID, String username) {
			logger.debug("addPolicyToGroup(String groupID, String policyID, String username) as addPolicyToGroup("+groupID+", "+policyID+","+username+") called");
			if(isNullOrEmpty(groupID, policyID, username)){
				throw new IllegalArgumentException("groupID, policyID, and username must not be null or empty");
			}
			synchronized(emLock){
				checkBeforeOperationRun();
				//check if group exists
				Query groupQuery = em.createQuery("SELECT g FROM GroupEntity g WHERE g.groupId=:groupId AND g.deleted=:deleted");
				groupQuery.setParameter("groupId", groupID);
				groupQuery.setParameter("deleted", false);
				List<?> groupQueryList;
				try{
					groupQueryList = groupQuery.getResultList();
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check if group exists groupQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check if group "+groupID+" exists");
				}
				if(groupQueryList.size() < 1){
					PolicyLogger.error("Group policy is being added to does not exist with id "+groupID);
					throw new PersistenceException("Group policy is being added to does not exist with id "+groupID);
				} else if(groupQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one group with the id "+groupID+" were found in the database that are not deleted");
					throw new PersistenceException("Somehow, more than one group with the id "+groupID+" were found in the database that are not deleted");
				}
				//we need to convert the form of the policy id that is used groups into the form that is used 
				//for the database. (com.Config_mypol.1.xml) to (Config_mypol.xml)
				String[] policyNameScopeAndVersion = getNameScopeAndVersionFromPdpPolicy(policyID);			
				Query policyQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName=:policyName AND p.scope=:scope AND p.deleted=:deleted");
				policyQuery.setParameter("policyName", policyNameScopeAndVersion[0]);
				policyQuery.setParameter("scope", policyNameScopeAndVersion[1]);			
				policyQuery.setParameter("deleted", false);
				List<?> policyQueryList;
				try{
					policyQueryList = policyQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check if policy exists policyQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check if policy "+policyNameScopeAndVersion[0]+" exists");
				}
				if(policyQueryList.size() < 1){
					PolicyLogger.error("Policy being added to the group does not exist with policy id "+policyNameScopeAndVersion[0]);
					throw new PersistenceException("Policy being added to the group does not exist with policy id "+policyNameScopeAndVersion[0]);				
				} else if(policyQueryList.size() > 1){
					PolicyLogger.error("Somehow, more than one policy with the id "+policyNameScopeAndVersion[0]+" were found in the database that are not deleted");
					throw new PersistenceException("Somehow, more than one group with the id "+policyNameScopeAndVersion[0]+" were found in the database that are not deleted");
				}
				GroupEntity group = (GroupEntity)groupQueryList.get(0);
				PolicyEntity policy = (PolicyEntity)policyQueryList.get(0);
	            Iterator<PolicyEntity> policyIt = group.getPolicies().iterator();
	            String policyName = getPolicyNameAndVersionFromPolicyFileName(policy.getPolicyName())[0];
	            try{
	            while(policyIt.hasNext()){
	                PolicyEntity pol = policyIt.next();
	                if(getPolicyNameAndVersionFromPolicyFileName(pol.getPolicyName())[0].equals(policyName)){
	                    policyIt.remove();
	                }
	            }
	            }catch(Exception e){
	                PolicyLogger.error("Could not delete old versions for policy "+policy.getPolicyName()+", ID: "+policy.getPolicyId());
	            }
				group.addPolicyToGroup(policy);
				em.flush();
			}
		}

		//this means delete pdp not just remove from group
		@Override
		public void removePdpFromGroup(String pdpID, String username) {
			logger.debug("removePdpFromGroup(String pdpID, String username) as removePdpFromGroup("+pdpID+","+username+") called");
			if(isNullOrEmpty(pdpID,username)){
				throw new IllegalArgumentException("pdpID and username must not be null or empty");
			}
			synchronized(emLock){
				checkBeforeOperationRun();
				Query pdpQuery = em.createQuery("SELECT p FROM PdpEntity p WHERE p.pdpId=:pdpId AND p.deleted=:deleted");
				pdpQuery.setParameter("pdpId", pdpID);
				pdpQuery.setParameter("deleted", false);
				List<?> pdpList;
				try{
					pdpList = pdpQuery.getResultList();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "PolicyDBDao", "Caught Exception trying to check if pdp exists  pdpQuery.getResultList()");
					throw new PersistenceException("Query failed trying to check if pdp "+pdpID+" exists");
				}
				if(pdpList.size() > 1){
					PolicyLogger.error("Somehow, more than one pdp with the id "+pdpID+" were found in the database that are not deleted");
					throw new PersistenceException("Somehow, more than one pdp with the id "+pdpID+" were found in the database that are not deleted");
				} else if(pdpList.size() < 1){
					PolicyLogger.error("Pdp being removed does not exist with id "+pdpID);
					return;
				}
				PdpEntity pdp = (PdpEntity)pdpList.get(0);
				pdp.setGroup(null);
				if(!stringEquals(pdp.getModifiedBy(),username)){
					pdp.setModifiedBy(username);
				}
				pdp.setDeleted(true);

				em.flush();
				this.pdpId = pdp.getPdpKey();
			}
		}
	}

	private PolicyDBDao(){

	}
	
	public static PolicyDBDaoTestClass getPolicyDBDaoTestClass(){
		return new PolicyDBDao().new PolicyDBDaoTestClass();
	}
	
	final class PolicyDBDaoTestClass {
		String getConfigFile(String filename, String scope, PolicyRestAdapter policy){
			return scope + "." + PolicyDBDao.this.getConfigFile(filename, policy);
		}
		String computeScope(String fullPath, String pathToExclude){
			return PolicyDBDao.computeScope(fullPath, pathToExclude);
		}
		String encryptPassword(String password) throws Exception{
			return PolicyDBDao.encryptPassword(password);
		}
		String decryptPassword(String password) throws Exception{
			return PolicyDBDao.decryptPassword(password);
		}
		String getDescriptionFromXacml(String xacmlData){
			return PolicyDBDao.getDescriptionFromXacml(xacmlData);
		}
        String[] getPolicyNameAndVersionFromPolicyFileName(String originalPolicyName){
            return PolicyDBDao.this.getPolicyNameAndVersionFromPolicyFileName(originalPolicyName);
        }
	}

}
