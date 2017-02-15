/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.ia;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openecomp.policy.jpa.BackUpMonitorEntity;
import org.openecomp.policy.rest.jpa.ActionBodyEntity;
import org.openecomp.policy.rest.jpa.ActionList;
import org.openecomp.policy.rest.jpa.ActionPolicyDict;
import org.openecomp.policy.rest.jpa.AddressGroup;
import org.openecomp.policy.rest.jpa.Attribute;
import org.openecomp.policy.rest.jpa.AttributeAssignment;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
import org.openecomp.policy.rest.jpa.Category;
import org.openecomp.policy.rest.jpa.ClosedLoopD2Services;
import org.openecomp.policy.rest.jpa.ClosedLoopSite;
import org.openecomp.policy.rest.jpa.ConfigurationDataEntity;
import org.openecomp.policy.rest.jpa.ConstraintType;
import org.openecomp.policy.rest.jpa.ConstraintValue;
import org.openecomp.policy.rest.jpa.DCAEUsers;
import org.openecomp.policy.rest.jpa.DCAEuuid;
import org.openecomp.policy.rest.jpa.DatabaseLockEntity;
import org.openecomp.policy.rest.jpa.Datatype;
import org.openecomp.policy.rest.jpa.DecisionSettings;
import org.openecomp.policy.rest.jpa.DescriptiveScope;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.EnforcingType;
import org.openecomp.policy.rest.jpa.FunctionArgument;
import org.openecomp.policy.rest.jpa.FunctionDefinition;
import org.openecomp.policy.rest.jpa.GlobalRoleSettings;
import org.openecomp.policy.rest.jpa.GroupEntity;
import org.openecomp.policy.rest.jpa.GroupPolicyScopeList;
import org.openecomp.policy.rest.jpa.GroupServiceList;
import org.openecomp.policy.rest.jpa.MicroServiceConfigName;
import org.openecomp.policy.rest.jpa.MicroServiceLocation;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.Obadvice;
import org.openecomp.policy.rest.jpa.ObadviceExpression;
import org.openecomp.policy.rest.jpa.PEPOptions;
import org.openecomp.policy.rest.jpa.PIPConfigParam;
import org.openecomp.policy.rest.jpa.PIPConfiguration;
import org.openecomp.policy.rest.jpa.PIPResolver;
import org.openecomp.policy.rest.jpa.PIPResolverParam;
import org.openecomp.policy.rest.jpa.PIPType;
import org.openecomp.policy.rest.jpa.PREFIXLIST;
import org.openecomp.policy.rest.jpa.PdpEntity;
import org.openecomp.policy.rest.jpa.PolicyAlgorithms;
import org.openecomp.policy.rest.jpa.PolicyDBDaoEntity;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PolicyManagement;
import org.openecomp.policy.rest.jpa.PolicyRoles;
import org.openecomp.policy.rest.jpa.PolicyScopeClosedLoop;
import org.openecomp.policy.rest.jpa.PolicyScopeResource;
import org.openecomp.policy.rest.jpa.PolicyScopeService;
import org.openecomp.policy.rest.jpa.PolicyScopeType;
import org.openecomp.policy.rest.jpa.PolicyScore;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.PortList;
import org.openecomp.policy.rest.jpa.ProtocolList;
import org.openecomp.policy.rest.jpa.RemoteCatalogValues;
import org.openecomp.policy.rest.jpa.RuleAlgorithms;
import org.openecomp.policy.rest.jpa.SecurityZone;
import org.openecomp.policy.rest.jpa.ServiceList;
import org.openecomp.policy.rest.jpa.SystemLogDB;
import org.openecomp.policy.rest.jpa.TermList;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.rest.jpa.VMType;
import org.openecomp.policy.rest.jpa.VNFType;
import org.openecomp.policy.rest.jpa.VSCLAction;
import org.openecomp.policy.rest.jpa.VarbindDictionary;
import org.openecomp.policy.rest.jpa.Zone;

import org.openecomp.policy.common.ia.DbAudit;
import org.openecomp.policy.common.ia.DbDAO;
import org.openecomp.policy.common.ia.IntegrityAuditProperties;
import org.openecomp.policy.common.ia.jpa.IntegrityAuditEntity;
import org.openecomp.policy.common.im.jpa.ForwardProgressEntity;
import org.openecomp.policy.common.im.jpa.ResourceRegistrationEntity;
import org.openecomp.policy.common.im.jpa.StateManagementEntity;

import org.apache.commons.lang3.SerializationUtils;

@Ignore
public class DbAuditCompareEntriesTest {

	private static Log logger = LogFactory.getLog(DbAuditCompareEntriesTest.class);
	private DbDAO dbDAO;
	private String persistenceUnit;
	private Properties properties;
	private String resourceName;
	private String dbDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String siteName;
	private String nodeType;
	
	@Before
	public void setUp() throws Exception {
		logger.info("setUp: Entering");

		properties = new Properties();
		properties.put(IntegrityAuditProperties.DB_DRIVER, IntegrityAuditProperties.DEFAULT_DB_DRIVER);
		properties.put(IntegrityAuditProperties.DB_URL, "jdbc:h2:file:./sql/xacmlTest");
		properties.put(IntegrityAuditProperties.DB_USER, IntegrityAuditProperties.DEFAULT_DB_USER);
		properties.put(IntegrityAuditProperties.DB_PWD, IntegrityAuditProperties.DEFAULT_DB_PWD);
		properties.put(IntegrityAuditProperties.SITE_NAME, "SiteA");
		properties.put(IntegrityAuditProperties.NODE_TYPE, "pap");

		dbDriver = IntegrityAuditProperties.DEFAULT_DB_DRIVER;
		dbUrl = "jdbc:h2:file:./sql/xacmlTest";
		dbUser = IntegrityAuditProperties.DEFAULT_DB_USER;
		dbPwd = IntegrityAuditProperties.DEFAULT_DB_PWD;
		siteName = "SiteA";
		nodeType = "pap";
		persistenceUnit = "testPapPU";
		resourceName = "siteA.pap1";
		
		//Clean the iaTest DB table for IntegrityAuditEntity entries
		cleanDb(persistenceUnit, properties);
		
		logger.info("setUp: Exiting");
	}

	@After
	public void tearDown() throws Exception {
		logger.info("tearDown: Entering");
		//nothing to do
		logger.info("tearDown: Exiting");
	}
	
	public void cleanDb(String persistenceUnit, Properties properties){
		logger.debug("cleanDb: enter");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, properties);
		
		EntityManager em = emf.createEntityManager();
		// Start a transaction
		EntityTransaction et = em.getTransaction();

		et.begin();

		// Clean up the DB
		em.createQuery("Delete from IntegrityAuditEntity").executeUpdate();

		// commit transaction
		et.commit();
		em.close();
		logger.debug("cleanDb: exit");
	}
	

	/*
	 * Tests that a comparison between hashsets is successful if
	 * the entries match
	 */
	//@Ignore
	@Test
	public void runAllTests() throws Exception {
		logger.info("runAllTests: Entering");
		
		
		testIntegrityAuditEntity();
		testBackupMonitorEntity();
		testStateManagementEntity();
		testForwardProgressEntity();
		testResourceRegistrationEntity();
		
		//clean up the IntegrityAuditEntity table
		cleanDb(persistenceUnit, properties);
		
		logger.info("runAllTests: Exit");
	}


	public void testIntegrityAuditEntity() throws Exception {
		logger.info("testIntegrityAuditEntity: Entering");
		
		dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
		DbAudit dbAudit = new DbAudit(dbDAO);
		
		String className = null;
		//There is only one entry IntegrityAuditEntity, but we will check anyway
		HashSet<String> classNameSet = dbDAO.getPersistenceClassNames();
		for(String c : classNameSet){
			if (c.equals("org.openecomp.policy.common.ia.IntegrityAuditEntity")){
				className = c;
			}
		}
		String resourceName1 = resourceName;
		String resourceName2 = resourceName;
		
		IntegrityAuditEntity entry1 = new IntegrityAuditEntity();
		IntegrityAuditEntity entry2 = new IntegrityAuditEntity();
		Date date = new Date();
		
		/*
		 * Two entries with the same field values
		 */
		entry1.setDesignated(false);
		entry1.setJdbcDriver(dbDriver);
		entry1.setJdbcPassword(dbPwd);
		entry1.setJdbcUrl(dbUrl);
		entry1.setJdbcUser(dbUser);
		entry1.setLastUpdated(date);
		entry1.setNodeType(nodeType);
		entry1.setPersistenceUnit(persistenceUnit);
		entry1.setResourceName(resourceName1);
		entry1.setSite(siteName);
		
		entry2 = SerializationUtils.clone(entry1);
		
		dbAudit.writeAuditDebugLog(className, resourceName1, resourceName2, entry1, entry2);
		
		HashMap<Object, Object> myEntries = new HashMap<Object, Object>();
		HashMap<Object, Object> theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
				
		HashSet<Object> result = dbAudit.compareEntries(myEntries, theirEntries);
		
		/*
		 * Assert that there are no mismatches returned
		 */
		assertTrue(result.isEmpty());
		
		/*
		 * ************************************
		 * Now test with a mis-matched entry
		 * ************************************
		 */
		
		/*
		 * Change the entry2 to different designated value
		 */
		entry2.setDesignated(true);
				
		myEntries = new HashMap<Object, Object>();
		theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
		
		result = dbAudit.compareEntries(myEntries, theirEntries);
		
		/*
		 * Assert that there was one mismatch
		 */
		assertEquals(1, result.size());
		logger.info("testIntegrityAuditEntity: Exit");
	}
	
	void testBackupMonitorEntity() throws Exception {
		logger.info("testBackupMonitorEntity: Entering");
		
		dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
		DbAudit dbAudit = new DbAudit(dbDAO);
		
		BackUpMonitorEntity entry1 = new BackUpMonitorEntity();
		BackUpMonitorEntity entry2 = new BackUpMonitorEntity();
		
		// Two entries with the same field values
		 
		
		entry1.setFlag("flag1");
		entry1.setResoruceNodeName("node1");
		entry1.setResourceName("resourceName");
		entry1.setTimeStamp(new Date());
		
		// Clone the first entry
		entry2 = SerializationUtils.clone(entry1);
		
		HashMap<Object, Object> myEntries = new HashMap<Object, Object>();
		HashMap<Object, Object> theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
				
		HashSet<Object> result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		// Assert that there are no mismatches returned
		 
		assertTrue(result.isEmpty());
		
		
		 /* ************************************
		 * Now test with a mis-matched entry
		 * ************************************/
		 
		
		
		// Change a field on entry2
		 
		entry2.setFlag("flag2");
				
		myEntries = new HashMap<Object, Object>();
		theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
		
		result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		//Assert that there was one mismatch
		 
		assertEquals(1, result.size());
		logger.info("testBackupMonitorEntity: Exit");
	}

	void testStateManagementEntity() throws Exception {
		logger.info("testStateManagementEntity: Entering");
		
		dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
		DbAudit dbAudit = new DbAudit(dbDAO);
		
		StateManagementEntity entry1 = new StateManagementEntity();
		StateManagementEntity entry2 = new StateManagementEntity();
		
		// Two entries with the same field values
		
		entry1.setAdminState("locked");
		entry1.setAvailStatus("null");
		entry1.setModifiedDate(new Date());
		entry1.setOpState("enabled");
		entry1.setResourceName("myResource");
		entry1.setStandbyStatus("coldstandby");
		
		// Clone the first entry
		entry2 = SerializationUtils.clone(entry1);
		
		HashMap<Object, Object> myEntries = new HashMap<Object, Object>();
		HashMap<Object, Object> theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
				
		HashSet<Object> result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		// Assert that there are no mismatches returned
		 
		assertTrue(result.isEmpty());
		
		
		 /* ************************************
		 * Now test with a mis-matched entry
		 * ************************************/
		 
		
		
		// Change a field on entry2
		 
		entry2.setAdminState("unlocked");
				
		myEntries = new HashMap<Object, Object>();
		theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
		
		result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		//Assert that there was one mismatch
		 
		assertEquals(1, result.size());
		logger.info("testStateManagementEntity: Exit");
	}
	
	void testForwardProgressEntity() throws Exception {
		logger.info("testForwardProgressEntity: Entering");
		
		dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
		DbAudit dbAudit = new DbAudit(dbDAO);
		
		ForwardProgressEntity entry1 = new ForwardProgressEntity();
		ForwardProgressEntity entry2 = new ForwardProgressEntity();

		// Two entries with the same field values
		
		entry1.setFpcCount(123L);
		entry1.setLastUpdated(new Date());
		entry1.setResourceName("myResource");
		
		// Clone the first entry
		entry2 = SerializationUtils.clone(entry1);
		
		HashMap<Object, Object> myEntries = new HashMap<Object, Object>();
		HashMap<Object, Object> theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
				
		HashSet<Object> result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		// Assert that there are no mismatches returned
		 
		assertTrue(result.isEmpty());
		
		
		 /* ************************************
		 * Now test with a mis-matched entry
		 * ************************************/
		
		// Change a field on entry2
		 
		entry2.setFpcCount(321L);
				
		myEntries = new HashMap<Object, Object>();
		theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
		
		result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		//Assert that there was one mismatch
		 
		assertEquals(1, result.size());
		logger.info("testForwardProgressEntity: Exit");
	}

	void testResourceRegistrationEntity() throws Exception {
		logger.info("testResourceRegistrationEntity: Entering");
		
		dbDAO = new DbDAO(resourceName, persistenceUnit, properties);
		DbAudit dbAudit = new DbAudit(dbDAO);
		
		ResourceRegistrationEntity entry1 = new ResourceRegistrationEntity();
		ResourceRegistrationEntity entry2 = new ResourceRegistrationEntity();
		
		// Two entries with the same field values
		
		entry1.setNodeType("pap");
		entry1.setLastUpdated(new Date());
		entry1.setResourceName("myResource");
		entry1.setResourceUrl("http://nowhere.com");
		entry1.setSite("site_1");
		
		// Clone the first entry
		entry2 = SerializationUtils.clone(entry1);
		
		HashMap<Object, Object> myEntries = new HashMap<Object, Object>();
		HashMap<Object, Object> theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
				
		HashSet<Object> result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		// Assert that there are no mismatches returned
		 
		assertTrue(result.isEmpty());
		
		
		 /* ************************************
		 * Now test with a mis-matched entry
		 * ************************************/
		
		// Change a field on entry2
		 
		entry2.setSite("site_1a");
				
		myEntries = new HashMap<Object, Object>();
		theirEntries = new HashMap<Object, Object>();
		
		myEntries.put("pdp1", entry1);
		theirEntries.put("pdp1", entry2);
		
		result = dbAudit.compareEntries(myEntries, theirEntries);
		
		
		//Assert that there was one mismatch
		 
		assertEquals(1, result.size());
		logger.info("testResourceRegistrationEntity: Exit");
	}
}
