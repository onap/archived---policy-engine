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

package org.openecomp.policy.pap.xacml.rest.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.components.PolicyDBDao.PolicyDBDaoTestClass;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.jpa.ActionBodyEntity;
import org.openecomp.policy.rest.jpa.GroupEntity;
import org.openecomp.policy.rest.jpa.PdpEntity;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.util.Webapps;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;
import org.openecomp.policy.xacml.util.XACMLPolicyWriter;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

@Ignore //only run locally as timing sometimes causes failures on Jenkins
public class PolicyDBDaoTest {

	private static Logger logger = FlexLogger.getLogger(PolicyDBDaoTest.class);
	
	PolicyDBDaoTestClass d;
	PolicyDBDao dbd;
	PolicyDBDao dbd2;
	EntityManagerFactory emf;
	@Before
	public void init(){
		System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME,"xacml.pap.properties");
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
			//logger.error("Exception Occured"+e);
			Assert.fail();
		}

		d = PolicyDBDao.getPolicyDBDaoTestClass();
	}
	
	@After
	public void cleanUp(){
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
	public void getScopeAndNameAndTypeTest(){

		String s = d.getGitPath();
		String pathIwantToUse;
		if(s.contains("/")){
			pathIwantToUse = "/root/users/" + s + "/org/openecomp/Config_mypolicy.xml";
		} else {
			pathIwantToUse = "C:\\root\\users\\" + s + "\\org\\openecomp\\Config_mypolicy.xml";
		}
		String[] snt = d.getScopeAndNameAndType(pathIwantToUse);
		Assert.assertEquals("Scope was parsed wrong","org.openecomp", snt[0]);
		Assert.assertEquals("Policy name was parsed wrong","Config_mypolicy.xml", snt[1]);
		Assert.assertEquals("Policy type was parsed wrong","Config", snt[2]);
	}
	@Test
	public void computeScopeTest(){
		Assert.assertEquals("com",d.computeScope("C:\\Users\\testuser\\admin\\repo\\com\\", "C:\\Users\\testuser\\admin\\repo"));
		Assert.assertEquals("org.openecomp.policy",d.computeScope("/Users/testuser/admin/repo/org/openecomp/policy", "/Users/testuser/admin/repo"));
	}
	@Test
	public void getConfigFileTest(){
		PolicyRestAdapter pra = new PolicyRestAdapter();
		pra.setConfigType(ConfigPolicy.JSON_CONFIG);
		String configFile = d.getConfigFile("Config_mypolicy.xml", "org.openecomp", pra);
		Assert.assertEquals("org.openecomp.Config_mypolicy.json", configFile);
		//yes, we can do action files too even though they don't have configs
		configFile = d.getConfigFile("Action_mypolicy.xml", "org.openecomp", pra);
		Assert.assertEquals("org.openecomp.Action_mypolicy.json", configFile);
	}
	
	@Test
	public void transactionTests(){
		
		
//		try{
//			transac.commitTransaction();
//			Assert.fail();
//		} catch(IllegalStateException e){
//			//worked
//		} catch(Exception e2){
//			Assert.fail();
//		}
		String filePath = null;
		String xmlFile = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<Policy xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" PolicyId=\"urn:com:xacml:policy:id:eaa4bb64-59cf-4517-bb44-b2eeabd50b11\" Version=\"1\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides\">\n    <Description></Description>\n    <Target>\n        <AnyOf>\n            <AllOf>\n                <Match MatchId=\"org.openecomp.labs.ecomp.function.regex-match\">\n                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#integer\">99</AttributeValue>\n                    <AttributeDesignator Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" AttributeId=\"cpu\" DataType=\"http://www.w3.org/2001/XMLSchema#integer\" MustBePresent=\"false\"/>\n                </Match>\n            </AllOf>\n        </AnyOf>\n    </Target>\n    <Rule RuleId=\"urn:com:xacml:rule:id:3350bf37-43d0-4a94-a317-febec81150d8\" Effect=\"Permit\">\n        <Target/>\n        <ObligationExpressions>\n            <ObligationExpression ObligationId=\"test\" FulfillOn=\"Permit\">\n                <AttributeAssignmentExpression AttributeId=\"performer\" Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject\">\n                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">PDPAction</AttributeValue>\n                </AttributeAssignmentExpression>\n                <AttributeAssignmentExpression AttributeId=\"type\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">REST</AttributeValue>\n                </AttributeAssignmentExpression>\n                <AttributeAssignmentExpression AttributeId=\"url\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#anyURI\">http://localhost:8056/pcd</AttributeValue>\n                </AttributeAssignmentExpression>\n                <AttributeAssignmentExpression AttributeId=\"method\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">GET</AttributeValue>\n                </AttributeAssignmentExpression>\n                <AttributeAssignmentExpression AttributeId=\"body\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n                    <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#anyURI\">$URLaction/com.Action_patbaction7.json</AttributeValue>\n                </AttributeAssignmentExpression>\n            </ObligationExpression>\n        </ObligationExpressions>\n    </Rule>\n</Policy>\n";
		String jsonFile = "{\"actionAttribute\":\"Memory\"}";
		
		try{
			//policy file
			InputStream in = new ByteArrayInputStream(xmlFile.getBytes());
			String workspaceDir = "src/test/resources/junitTestCreatedDirectory/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE)+"/admin/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY);
			FileUtils.forceMkdir(new File(workspaceDir+"/com/att"));
			File outFile = new File(workspaceDir+"/org/openecomp/Action_mypol.xml");
			OutputStream out = new FileOutputStream(outFile);
			IOUtils.copy(in, out);
			filePath = outFile.getAbsolutePath();	
			out.close();
			
			//action body file
			InputStream actionIn = new ByteArrayInputStream(jsonFile.getBytes());
			String webappDir = "src/test/resources/junitTestCreatedDirectory/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE);
			XACMLProperties.setProperty(XACMLRestProperties.PROP_PAP_WEBAPPS, webappDir);
			String actionDir = Webapps.getActionHome();
			FileUtils.forceMkdir(new File(actionDir));
			File actionOutFile = new File(actionDir+"/org.openecomp.Action_mypol.json");
			OutputStream actionOut = new FileOutputStream(actionOutFile);
			IOUtils.copy(actionIn, actionOut);
			actionOut.close();
			
		}catch(Exception e){
			//could not run test
		}
		PolicyDBDaoTransaction transac = dbd.getNewTransaction();
		if(filePath != null){
			try{
				transac.createPolicy(filePath, "tester");
				transac.commitTransaction();
			} catch(Exception e){
				Assert.fail();
			}
			EntityManager getData = emf.createEntityManager();
			Query getDataQuery = getData.createQuery("SELECT p FROM PolicyEntity p WHERE p.scope=:scope AND p.policyName=:name");
			getDataQuery.setParameter("scope", "org.openecomp");
			getDataQuery.setParameter("name","Action_mypol.xml");
			PolicyEntity result = null;
			try{
				result = (PolicyEntity)getDataQuery.getSingleResult();
			} catch(Exception e){
				logger.error("Exception Occured"+e);
				Assert.fail();
			}
			Assert.assertEquals(xmlFile, result.getPolicyData());
			getData.close();
			result = null;
			xmlFile = null;
			try{
				transac = dbd.getNewTransaction();
				transac.deletePolicy(filePath);
			} catch(Exception e){
				logger.error("Exception Occured"+e);
				Assert.fail();
			}
			Assert.assertTrue(transac.isTransactionOpen());
			try{				
				transac.deletePolicy(filePath);
				Assert.fail();
			} catch(IllegalStateException e){
				//pass
			} catch(Exception e){
				Assert.fail();
			}
			transac.commitTransaction();
			//Assert.assertFalse(transac.isTransactionOpen());
			try{
				transac = dbd.getNewTransaction();
				transac.deletePolicy(filePath);
			} catch(Exception e){
				logger.error("Exception Occured"+e);
				Assert.fail();
			}
			transac.commitTransaction();
			//Assert.assertFalse(transac.isTransactionOpen());
			String workspaceDir = "src/test/resources/junitTestCreatedDirectory/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE)+"/admin/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY);
			PolicyDBDaoTransaction willFail = dbd.getNewTransaction();
			File fakeFile = new File("directorythatdoesnotexist/"+workspaceDir);
			try{
			willFail.createPolicy(fakeFile.getAbsolutePath(), "user1");
			Assert.fail();
			} catch(IllegalArgumentException e){
				if(!e.getMessage().equals("The file path could not be parsed")){
					Assert.fail();
				}
			}			
			willFail.close();
			
			fakeFile = new File("directorythatdoesnotexist/"+workspaceDir+"/Action_mypol2.xml");
			willFail = dbd.getNewTransaction();
			try{
			willFail.createPolicy(fakeFile.getAbsolutePath(), "user1");
			Assert.fail();
			} catch(IllegalArgumentException e){
				if(!e.getMessage().equals("The file path could not be parsed")){
					Assert.fail();
				}
			}
			willFail.close();
			
			fakeFile = new File("directorythatdoesnotexist/"+workspaceDir+"com/att/Action_mypol2.xml");
			willFail = dbd.getNewTransaction();
			try{
			willFail.createPolicy(fakeFile.getAbsolutePath(), "user1");
			Assert.fail();
			} catch(IllegalArgumentException e){
				if(!e.getMessage().equals("The file path does not exist")){
					Assert.fail();
				}
			}
			willFail.close();
			
			emf = Persistence.createEntityManagerFactory("testPU");
			EntityManager aem = emf.createEntityManager();
			Query actionQuery = aem.createQuery("SELECT a FROM ActionBodyEntity a WHERE a.actionBodyName=:actionBodyName");
			actionQuery.setParameter("actionBodyName", "org.openecomp.Action_mypol.json");
			List<?> actionQueryList = actionQuery.getResultList();
			if(actionQueryList.size() < 1){
				Assert.fail("ActionBodyEntity not found with actionBodyName=: org.openecomp.Action_mypol.json"  );
			} else if(actionQueryList.size() > 1){
				//something went wrong
				Assert.fail("Somehow, more than one ActionBodyEntity with the actionBodyName = org.openecomp.Action_mypol.json");
			} else {
				ActionBodyEntity abe = (ActionBodyEntity)actionQueryList.get(0);
				logger.debug("\n\nPolicyDBDaoTest.transactionTests() Assert.assertEquals"
						+ "\n   abe.getActionBody() = " + abe.getActionBody()
						+ "\n   jsonFile = " + jsonFile
						+ "\n\n");
				Assert.assertEquals(abe.getActionBody(),jsonFile);				
			}
		}
	}
	
	@Test
	public void createFromPolicyObject(){
		String workspaceDir = "src/test/resources/junitTestCreatedDirectory/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE)+"/admin/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY);
		File parentPath = new File(workspaceDir+"/com/att");
		File scope = new File(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE)+"/admin/"+XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY));
		Policy policyObject = new ConfigPolicy();
		policyObject.policyAdapter = new PolicyRestAdapter();
		policyObject.policyAdapter.setConfigName("testpolicy1");
		policyObject.policyAdapter.setParentPath(parentPath.getAbsolutePath());
		policyObject.policyAdapter.setUserGitPath(scope.getPath());
		policyObject.policyAdapter.setPolicyDescription("my description");
		policyObject.policyAdapter.setConfigBodyData("this is my test config file");
		policyObject.policyAdapter.setPolicyName("testpolicy1");
		policyObject.policyAdapter.setConfigType(ConfigPolicy.OTHER_CONFIG);
		policyObject.policyAdapter.setPolicyType("Config");
		PolicyType policyTypeObject = new PolicyType();
		policyObject.policyAdapter.setPolicyData(policyTypeObject);
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
		getDataQuery.setParameter("scope", "org.openecomp");
		getDataQuery.setParameter("name","Config_testpolicy1.xml");
		PolicyEntity result = null;
		try{
		result = (PolicyEntity)getDataQuery.getSingleResult();
		} catch(Exception e){
			logger.error("Exception Occured"+e);
			Assert.fail();
		}
		String expectedData;
		try {
			expectedData = IOUtils.toString(XACMLPolicyWriter.getXmlAsInputStream(policyTypeObject));
		} catch (IOException e1) {
			expectedData = "";
		}
		Assert.assertEquals(expectedData, result.getPolicyData());
		getData.close();
		result = null;
		File policyFile = new File(workspaceDir+"/org/openecomp/Config_testpolicy1.xml");
		try{
			transaction = dbd.getNewTransaction();
			transaction.deletePolicy(policyFile.getAbsolutePath());
		} catch(Exception e){
			logger.error("Exception Occured"+e);
			Assert.fail();
		}
		Assert.assertTrue(transaction.isTransactionOpen());
		try{				
			transaction.deletePolicy(policyFile.getAbsolutePath());
			Assert.fail();
		} catch(IllegalStateException e){
			//pass
		} catch(Exception e){
			Assert.fail();
		}
		transaction.commitTransaction();
		Assert.assertFalse(transaction.isTransactionOpen());
		try{
			transaction = dbd.getNewTransaction();
			transaction.deletePolicy(policyFile.getAbsolutePath());
		} catch(Exception e){
			logger.error("Exception Occured"+e);
			Assert.fail();
		}
		//Assert.assertFalse(transaction.isTransactionOpen());
		transaction.commitTransaction();
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
			EcompPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId(groupName),Paths.get("/"));			
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
			EcompPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup1"),Paths.get("/"));
			EcompPDPGroup groupToMoveTo = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"),Paths.get("/"));	
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
			EcompPDPGroup groupToDelete = new StdPDPGroup(PolicyDBDao.createNewPDPGroupId("testgroup2"),Paths.get("/"));
			EcompPDPGroup groupToMoveTo = null;	
			group.deleteGroup(groupToDelete, groupToMoveTo,"testuser");
			group.commitTransaction();
			Assert.fail();
		} catch(PAPException pe){
			//good, can't delete group with pdps
			group.rollbackTransaction();
		} catch(Exception e){
			group.rollbackTransaction();
			logger.error("Exception Occured"+e);
			Assert.fail();
		}
		
		
		//add policy to group
		
		//update group
		EcompPDPGroup pdpGroup = new StdPDPGroup("testgroup2", false, "newtestgroup2", "this is my new description", Paths.get("/"));
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
		//update pdp

		//set group as default

		//move pdp to new group


	}
	
	@Test
	public void encryptionTest(){
		try {
			String encr = d.encryptPassword("testpassword");
			System.out.println("original password: "+"testpassword");
			System.out.println("Encrypted password: "+encr);
			String decr = d.decryptPassword(encr);
			System.out.println("Decrypted password: "+decr);
			Assert.assertEquals("testpassword", decr);
		} catch (Exception e) {
			logger.error("Exception Occured"+e);
			Assert.fail();
		}
		
	}
	@Test
	public void getDescriptionFromXacmlTest(){
		String myTestDesc = "hello this is a test";
		String desc = d.getDescriptionFromXacml("<Description>"+myTestDesc+"</Description>");
		Assert.assertEquals(myTestDesc, desc);
	}

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
