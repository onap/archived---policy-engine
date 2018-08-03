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

package org.onap.policy.pap.xacml.rest.jpa;

import static org.junit.Assert.*;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.jpa.ActionBodyEntity;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyDBDaoEntity;
import org.onap.policy.rest.jpa.PolicyEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.util.Date;
import java.util.List;
import org.onap.policy.common.logging.flexlogger.FlexLogger; 
import org.onap.policy.common.logging.flexlogger.Logger;

import java.util.Properties;

public class PolicyEntityTest {

    private static Logger logger = FlexLogger.getLogger(PolicyEntityTest.class);

    @Test
    public void testAllOps(){
        Properties properties = new Properties();
        properties.put(XACMLRestProperties.PROP_PAP_DB_DRIVER,"org.h2.Driver");
        properties.put(XACMLRestProperties.PROP_PAP_DB_URL, "jdbc:h2:file:./sql/xacmlTest");
        properties.put(XACMLRestProperties.PROP_PAP_DB_USER, "sa");
        properties.put(XACMLRestProperties.PROP_PAP_DB_PASSWORD, "");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPapPU", properties);
        EntityManager em = emf.createEntityManager();
        // Start a transaction
        EntityTransaction et = em.getTransaction();

        et.begin();
        //Make sure the DB is clean
        em.createQuery("DELETE FROM PolicyDBDaoEntity").executeUpdate();
        em.createQuery("DELETE FROM PolicyEntity").executeUpdate();
        em.createQuery("DELETE FROM ConfigurationDataEntity").executeUpdate();
        em.createQuery("DELETE FROM ActionBodyEntity").executeUpdate();

        //Create a policy object
        PolicyEntity p1 = new PolicyEntity();
        
        //persist the policy    
        em.persist(p1);

        long policyId1 = p1.getPolicyId();
        
        String policyName1 = p1.getPolicyName();
        
        int version1 = p1.getVersion();
        
        String policyData1 = p1.getPolicyData();
        
        ConfigurationDataEntity configData1 = p1.getConfigurationData();
        String configDataStr1 = (configData1!=null ? "configurationDataId = " + configData1.getConfigurationDataId() : "configurationData is null");
        
        ActionBodyEntity actionBody1 = p1.getActionBodyEntity();
        String actionBodyStr1 = (actionBody1!=null ? "actionBodyId = " + actionBody1.getActionBodyId() : "actionBody is null");
        
        String createdBy1 = p1.getCreatedBy();
        
        Date createdDate1 = p1.getCreatedDate();
        String createdDateStr1 = (createdDate1 != null ? createdDate1.toString() : "createdDate is null");
        
        String description = p1.getDescription();
        
        String modifiedBy1 = p1.getModifiedBy();
        
        Date modifiedDate1 = p1.getModifiedDate();
        String modifiedDateStr1 = (modifiedDate1 != null ? modifiedDate1.toString() : "modifiedDate is null");
        
        
        logger.debug("\n\n********PolicyEntityTest: Local PolicyEntity and Configuration objects before persist*********"
                + "\npolicyId1 = " + policyId1
                + "\npolicyName1 = " + policyName1
                + "\nversion1 = " + version1
                + "\npolicyData1 = " + policyData1
                + "\nconfigDataStr1 = " + configDataStr1
                + "\nactionBodyStr1 = " + actionBodyStr1
                + "\nscope = " + p1.getScope()
                + "\ncreatedBy1 = " + createdBy1
                + "\ncreatedDateStr1 = " + createdDateStr1
                + "\ndescription = " + description
                + "\nmodifiedBy1 = " + modifiedBy1
                + "\nmodifiedDateStr1 = " + modifiedDateStr1
                + "\ndeleted = " + p1.isDeleted());
        
        //Set policyID
        p1.setPolicyName("testPID2");
        
        //Set policyData
        p1.setPolicyData("<policy>PolicyData</policy>");
        
        //We will NOT set the ConfigurationDataEntity or ActionBodyEntity object just to test that it is optional
        
        //set createdBy
        p1.setCreatedBy("kevin");
        
        //createdDate will be set when it is persisted
        
        //set scope
        p1.setScope("mckiou.kevin");
        
        //set description
        p1.setDescription("PolicyEntity Description");
        
        //set modifiedBy
        p1.setModifiedBy("kevin");
        
        //modifiedDate will be set when it is persisted
      
        //Flush to the DB
        em.flush();
        
        //Now lets get some attribute values
  
        policyId1 = p1.getPolicyId();
        
        policyName1 = p1.getPolicyName();
        
        version1 = p1.getVersion();
        
        policyData1 = p1.getPolicyData();
        
        configData1 = p1.getConfigurationData();
        configDataStr1 = (configData1!=null ?  "configurationDataId = " + configData1.getConfigurationDataId() : "configurationData is null");
        
        actionBody1 = p1.getActionBodyEntity();
        actionBodyStr1 = (actionBody1!=null ? "actionBodyId = " + actionBody1.getActionBodyId() : "actionBody is null");
        
        createdBy1 = p1.getCreatedBy();
        
        createdDate1 = p1.getCreatedDate();
        createdDateStr1 = (createdDate1 != null ? createdDate1.toString() : "createdDate is null");
        
        description = p1.getDescription();
        
        modifiedBy1 = p1.getModifiedBy();
        
        modifiedDate1 = p1.getModifiedDate();
        modifiedDateStr1 = (modifiedDate1 != null ? modifiedDate1.toString() : "modifiedDate is null");
        
        logger.debug("\n\n********PolicyEntityTest: Local PolicyEntity and Configuration objects after persist*********"
                + "\npolicyId1 = " + policyId1
                + "\npolicyName1 = " + policyName1
                + "\nversion1 = " + version1
                + "\npolicyData1 = " + policyData1
                + "\nconfigDataStr1 = " + configDataStr1
                + "\nactionBodyStr1 = " + actionBodyStr1
                + "\nscopeId = " + p1.getScope()
                + "\ncreatedBy1 = " + createdBy1
                + "\ncreatedDateStr1 = " + createdDateStr1
                + "\ndescription = " + description
                + "\nmodifiedBy1 = " + modifiedBy1
                + "\nmodifiedDateStr1 = " + modifiedDateStr1
                + "\ndeleted = " + p1.isDeleted());

        //Now lets fully configure the configurationData and actionBody
        
        //Create a ConfigurationDataEntity object and set ID
      ConfigurationDataEntity c1 = new ConfigurationDataEntity();      
      
      ActionBodyEntity a1 = new ActionBodyEntity();
      
        //persist the configuration Data
        em.persist(c1);
        
        c1.setConfigType("OTHER");
        
        c1.setConfigBody("ABC");
        
        c1.setDescription("ConfigurationDataEntity Description");
        
        c1.setCreatedBy("kevin");
        
        //c1.setModifiedBy("kevin");
        
        c1.setDeleted(true);
        
        //persist the action Body
        
        em.persist(a1);
        
        a1.setActionBody("myActionBody");
        
        a1.setActionBodyName("myActionBodyName");
        
        a1.setCreatedBy("kevin");
        
        a1.setModifiedBy("kevin");
        
        a1.setDeleted(false);
        
        
        long configurationDataId = c1.getConfigurationDataId();
        
        int cdVersion = c1.getVersion();
        
        String cdConfigType = c1.getConfigType();
        
        String cdConfigBody = c1.getConfigBody();
        
        String cdCreatedBy = c1.getCreatedBy();
        
        Date cdCreatedDate = c1.getCreatedDate();
        
        String cdDescription = c1.getDescription();
        
        String cdModifiedBy = c1.getModifiedBy();
        
        Date cdModifiedDate = c1.getModifiedDate();
        
        logger.debug("\n\n********PolicyEntityTest: Local Configuration object after setting values *********"
                + "\nconfigurationDataId = " + configurationDataId
                + "\ncdVersion = " + cdVersion
                + "\ncdConfigType = " + cdConfigType
                + "\ncdConfigBody = " + cdConfigBody
                + "\ncdCreatedBy = " + cdCreatedBy
                + "\ncdCreatedDate = " + cdCreatedDate
                + "\ncdDescription = " + cdDescription
                + "\ncdModifiedBy = " + cdModifiedBy
                + "\ncdModifiedDate = " + cdModifiedDate
                + "\ndeleted = " + c1.isDeleted());
        

        
        logger.debug("\n\n********PolicyEntityTest: Local Action Body object after setting values *********"
                + "\nactionBodyId = " + a1.getActionBodyId()
                + "\nactionBodyVersion = " + a1.getVersion()
                + "\nactionBody = " + a1.getActionBody()
                + "\nactionBodyCeatedBy = " + a1.getCreatedBy()
                + "\nactionBodyCreatedDate = " + a1.getCreatedDate()
                + "\nactionBodyModifiedBy = " + a1.getModifiedBy()
                + "\nactionBodyModifiedDate = " + a1.getModifiedDate()
                + "\nactionBodyDeleted = " + a1.isDeleted());

        p1.setScope("mckiou.kevin.kim");        
        
        //flush to the db
        em.flush();
        
        //Perform policy selects
        
        Query query = em.createQuery("Select p from PolicyEntity p where p.policyId=:pid");
        Query queryscope = em.createQuery("Select p from PolicyEntity p where p.scope=:s");
        
        query.setParameter("pid", p1.getPolicyId());
        queryscope.setParameter("s", "mckiou.kevin.kim");
        
        //Just test that we are retrieving the right object
        @SuppressWarnings("rawtypes")
        List psList = queryscope.getResultList();
        PolicyEntity px = null;
        if(!psList.isEmpty()){
            //ignores multiple results
            px = (PolicyEntity) psList.get(0);
        }else{
            fail("\nPolicyEntityTest: No PolicyEntity using scope DB entry found");
        }
        
        //The scope object on the retrieved policy object should be same as the one we used to find it
        assertSame(p1,px);
        
       
        //Because getSingleResult() throws an unchecked exception which is an indication of a 
        //programming error, we are not going to use it.
        @SuppressWarnings("rawtypes")
        List resultList = query.getResultList();
        PolicyEntity p2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            p2 = (PolicyEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest: No PolicyEntity DB entry found");
        }
        
        logger.debug("\n\n********PolicyEntityTest: PolicyEntity object after retrieving from DB BEFORE assigning configurationData*********"
                + "\npolicyId2 = " + p2.getPolicyId()
                + "\npolicyName2 = " + p2.getPolicyName()
                + "\nversion2 = " + p2.getVersion()
                + "\npolicyData2 = " + p2.getPolicyData()
                + "\nconfigurationData2 = " + (p2.getConfigurationData()!=null ? "configurationDataId = " + p2.getConfigurationData().getConfigurationDataId() : "configurationData is null")
                + "\nactionBody2 = " + (p2.getActionBodyEntity()!=null ? "actionBodyId = " + p2.getActionBodyEntity().getActionBodyId() : "actionBody is null")
                + "\nscope2 = " + p2.getScope()
                + "\ncreatedBy2 = " + p2.getCreatedBy()
                + "\ncreatedDate2 = " + p2.getCreatedDate()
                + "\ndescription2 = " + p2.getDescription()
                + "\nmodifiedBy2 = " + p2.getModifiedBy()
                + "\nmodifiedDate2 = " + p2.getModifiedDate()
                + "\ndeleted2 = " + p2.isDeleted());

        //Confirm that the retrieved policy object is the same as the persisted object
        assertSame(p1,p2);
        
        //Perform configurationData selects
        Query query2 = em.createQuery("Select c from ConfigurationDataEntity c where c.configurationDataId=:cid");
        
        query2.setParameter("cid", c1.getConfigurationDataId());
        
        //Get the database version of the Configuration Data
        resultList = query2.getResultList();
        ConfigurationDataEntity c2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            c2 = (ConfigurationDataEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest: No ConfigurationDataEntity DB entry found");
        }
        
        logger.debug("\n\n********PolicyEntityTest: Configuration object after retrieving from DB BEFORE assigning to policy*********"
                + "\nconfigurationDataId2 = " + c2.getConfigurationDataId()
                + "\nversion2 = " + c2.getVersion()
                + "\nconfigType2 = " + c2.getConfigType()
                + "\nconfigBody2 = " + c2.getConfigBody()
                + "\ncreatedBy2 = " + c2.getCreatedBy()
                + "\ncreatedDate2 = " + c2.getCreatedDate()
                + "\ndescription2 = " + c2.getDescription()
                + "\nmodifiedBy2 = " + c2.getModifiedBy()
                + "\nmodifiedDate2 = " + c2.getModifiedDate()
                + "\ndeleted2 = " + c2.isDeleted());
        
        //Confirm the retrieved ConfigurationDataEntity object is the same as the persisted
        assertSame(c1,c2);
        
        //Now assign the configurationData to the policy 
        p1.setConfigurationData(c1);
        
        //Perform actionBody selects
        Query querya2 = em.createQuery("Select a from ActionBodyEntity a where a.actionBodyId=:aid");
        
        querya2.setParameter("aid", a1.getActionBodyId());
        
        //Get the database version of the Action Body
        resultList = querya2.getResultList();
        ActionBodyEntity a2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            a2 = (ActionBodyEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest: No ActionBodyEntity DB entry found");
        }
        
        
        logger.debug("\n\n********PolicyEntityTest: Local Action Body object after retrieving from DB BEFORE assigning to policy *********"
                + "\nactionBodyId2 = " + a2.getActionBodyId()
                + "\nactionBodyVersion2 = " + a2.getVersion()
                + "\nactionBody2 = " + a2.getActionBody()
                + "\nactionBodyCeatedBy2 = " + a2.getCreatedBy()
                + "\nactionBodyCreatedDate2 = " + a2.getCreatedDate()
                + "\nactionBodyModifiedBy2 = " + a2.getModifiedBy()
                + "\nactionBodyModifiedDate2 = " + a2.getModifiedDate()
                + "\nactionBodyDeleted2 = " + a2.isDeleted());

        
        //Confirm the retrieved ActionBodyEntity object is the same as the persisted
        assertSame(a1,a2);
        
        //Now assign the ActionBodyEntity to the policy 
        p1.setActionBodyEntity(a1);

        em.flush();
        
        //Let's retrieve the policy, configurationData and actionBody from the DB and look at them
        //Here is the policy object
        resultList = query.getResultList();
        p2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            p2 = (PolicyEntity) resultList.get(0);
        }else{
            fail("PolicyEntityTest: No PolicyEntity DB entry found");
        }
        
        logger.debug("\n\n********PolicyEntityTest: PolicyEntity object after retrieving from DB AFTER assigning configurationData*********"
                + "\npolicyId2 = " + p2.getPolicyId()
                + "\npolicyName2 = " + p2.getPolicyName()
                + "\nversion2 = " + p2.getVersion()
                + "\npolicyData2 = " + p2.getPolicyData()
                + "\nconfigurationData2 = " + (p2.getConfigurationData()!=null ? "configurationDataId = " + p2.getConfigurationData().getConfigurationDataId() : "configurationData is null")
                + "\nactionBody2 = " + (p2.getActionBodyEntity()!=null ? "actionBodyId = " + p2.getActionBodyEntity().getActionBodyId() : "actionBody is null")
                + "\nscope2 = " + p2.getScope()
                + "\ncreatedBy2 = " + p2.getCreatedBy()
                + "\ncreatedDate2 = " + p2.getCreatedDate()
                + "\ndescription2 = " + p2.getDescription()
                + "\nmodifiedBy2 = " + p2.getModifiedBy()
                + "\nmodifiedDate2 = " + p2.getModifiedDate()
                + "\ndeleted2 = " + p2.isDeleted());

        //And now the ConfigurationDataEntity object
        resultList = query2.getResultList();
        c2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            c2 = (ConfigurationDataEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest: No ConfigurationDataEntity DB entry found");
        }
        
        logger.debug("\n\n********PolicyEntityTest: Configuration object after retrieving from DB AFTER assigning to policy*********"
                + "\nconfigurationDataId2 = " + c2.getConfigurationDataId()
                + "\nversion2 = " + c2.getVersion()
                + "\nconfigType2 = " + c2.getConfigType()
                + "\nconfigBody2 = " + c2.getConfigBody()
                + "\ncreatedBy2 = " + c2.getCreatedBy()
                + "\ncreatedDate2 = " + c2.getCreatedDate()
                + "\ndescription2 = " + c2.getDescription()
                + "\nmodifiedBy = " + c2.getModifiedBy()
                + "\nmodifiedDate = " + c2.getModifiedDate()
                + "\ndeleted2 = " + c2.isDeleted());
        
        
        //Get the database version of the Action Body
        resultList = querya2.getResultList();
        a2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            a2 = (ActionBodyEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest: No ActionBodyEntity DB entry found");
        }
        
        
        logger.debug("\n\n********PolicyEntityTest: Local Action Body object after retrieving from DB AFTER assigning to policy *********"
                + "\nactionBodyId2 = " + a2.getActionBodyId()
                + "\nactionBodyVersion2 = " + a2.getVersion()
                + "\nactionBody2 = " + a2.getActionBody()
                + "\nactionBodyCeatedBy2 = " + a2.getCreatedBy()
                + "\nactionBodyCreatedDate2 = " + a2.getCreatedDate()
                + "\nactionBodyModifiedBy2 = " + a2.getModifiedBy()
                + "\nactionBodyModifiedDate2 = " + a2.getModifiedDate()
                + "\nactionBodyDeleted2 = " + a2.isDeleted());

        
        //****Now lets see if the orphanRemoval=true does anything useful***
        //Remove the configurationData from the policy relationship
        
        p1.setConfigurationData(null);
        
        p1.setActionBodyEntity(null);
        
        //flush the update to the DB
        em.flush();
        
        //Attempt to retrieve the configuration data object from the db. It should not be there
        //Reusing the previous query
        resultList = query2.getResultList();
        c2 = null;
        if(resultList.isEmpty()){
            logger.debug("\n\n********PolicyEntityTest: orphanRemoval=true******"
                    + "\n Success!! No ConfigurationDataEntity DB entry found");
            
        }else{
            c2 = (ConfigurationDataEntity) resultList.get(0);
            fail("\nPolicyEntityTest: ConfigurationDataEntity DB entry found - and none should exist"
                    + "\nconfigurationDataId = " + c2.getConfigurationDataId());
        }
        
        //Attempt to retrieve the actionBody data object from the db. It should not be there
        //Reusing the previous query
        resultList = querya2.getResultList();
        a2 = null;
        if(resultList.isEmpty()){
            logger.debug("\n\n********PolicyEntityTest: orphanRemoval=true******"
                    + "\n Success!! No ActionBodyEntity DB entry found");
            
        }else{
            a2 = (ActionBodyEntity) resultList.get(0);
            fail("\nPolicyEntityTest: ActionBodyEntity DB entry found - and none should exist"
                    + "\nactionBodyId = " + a2.getActionBodyId());
        }
        
        //Now lets put the configurationData and actionBody back into the policy object and see what appears
        //in the DB after a flush
        
        //put c1 back into the persistence context since the orphanRemoval removed it.
        em.persist(c1);
        p1.setConfigurationData(c1);
        
        em.persist(a1);
        p1.setActionBodyEntity(a1);
        
        em.flush();
        
        //retrieve the policy object
        resultList = query.getResultList();
        p2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            p2 = (PolicyEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest: No PolicyEntity DB entry found");
        }
        
        //output what we policy object found
        logger.debug("\n\n********PolicyEntityTest: PolicyEntity object after again adding ConfigurationDataEntity and retrieving from DB*********"
                + "\npolicyId2 = " + p2.getPolicyId()
                + "\npolicyName2 = " + p2.getPolicyName()
                + "\nversion2 = " + p2.getVersion()
                + "\npolicyData2 = " + p2.getPolicyData()
                + "\nconfigurationData2 = " + (p2.getConfigurationData()!=null ? "configurationDataId = " + p2.getConfigurationData().getConfigurationDataId() : "configurationData is null")
                + "\nactionBody2 = " + (p2.getActionBodyEntity()!=null ? "actionBodyId = " + p2.getActionBodyEntity().getActionBodyId() : "actionBody is null")
                +  "\nscope2 = " + p2.getScope()
                + "\ncreatedBy2 = " + p2.getCreatedBy()
                + "\ncreatedDate2 = " + p2.getCreatedDate()
                + "\ndescription2 = " + p2.getDescription()
                + "\nmodifiedBy2 = " + p2.getModifiedBy()
                + "\nmodifiedDate2 = " + p2.getModifiedDate()
                + "\ndeleted2 = " + p2.isDeleted());


        //now lets see if it put the configurationData c1 back into the table
        resultList = query2.getResultList();
        c2 = null;
        if(!resultList.isEmpty()){
            // ignores multiple results
            c2 = (ConfigurationDataEntity) resultList.get(0);
        }else{
            fail("\nPolicyEntityTest - Check re-entry of configurationData into DB"
                    + "No ConfigurationDataEntity DB entry found");
        }
        
        //output what configurationData object we found
        logger.debug("\n\n********PolicyEntityTest: Configuration object after re-enter into policy object and retrieving from DB *********"
                + "\nconfigurationDataId2 = " + c2.getConfigurationDataId()
                + "\nversion2 = " + c2.getVersion()
                + "\nconfigType2 = " + c2.getConfigType()
                + "\nconfigBody2 = " + c2.getConfigBody()
                + "\ncreatedBy2 = " + c2.getCreatedBy()
                + "\ncreatedDate2 = " + c2.getCreatedDate()
                + "\ndescription2 = " + c2.getDescription()
                + "\nmodifiedBy = " + c2.getModifiedBy()
                + "\nmodifiedDate = " + c2.getModifiedDate()
                + "\ndeleted2 = " + c2.isDeleted());

        //now lets see if it put the actionBody a1 back into the table
        //Get the database version of the Action Body
        resultList = querya2.getResultList();
         a2 = null;
         if(!resultList.isEmpty()){
             // ignores multiple results
             a2 = (ActionBodyEntity) resultList.get(0);
         }else{
            fail("\nPolicyEntityTest - Check re-entry of actionBody into DB"
                    + "No ActionBodyEntity DB entry found");
         }
         
         logger.debug("\n\n********PolicyEntityTest: Local Action Body object after re-enter into policy object and retrieving from DB *********"
                + "\nactionBodyId2 = " + a2.getActionBodyId()
                + "\nactionBodyVersion2 = " + a2.getVersion()
                + "\nactionBody2 = " + a2.getActionBody()
                + "\nactionBodyCeatedBy2 = " + a2.getCreatedBy()
                + "\nactionBodyCreatedDate2 = " + a2.getCreatedDate()
                + "\nactionBodyModifiedBy2 = " + a2.getModifiedBy()
                + "\nactionBodyModifiedDate2 = " + a2.getModifiedDate()
                + "\nactionBodyDeleted2 = " + a2.isDeleted());

        //I want to save all the above in the DB
        try{
            et.commit();
            logger.debug("\n\n***********PolicyEntityTest: et.commit Succeeded********");
        }catch(Exception e){
            logger.debug("\n\n***********PolicyEntityTest: et.commit Failed********"
                    + "\nTRANSACTION ROLLBACK "
                    + "\n   with exception: " + e);
        }

        // Start a new transaction
        EntityTransaction et2 = em.getTransaction();

        et2.begin();
        
        //Let's test if the PolicyEntity uniqueConstraint for policyName and scopeId hold
        PolicyEntity p3 = new PolicyEntity();
        em.persist(p3);


        //first let's assure that you can save with the same name but a different scope
        p3.setPolicyName(p1.getPolicyName());
        p3.setScope("mckiou.kevin.kory");
        em.flush();
        logger.debug("\n\n***********PolicyEntityTest: PolicyEntity Unique test for policyName and scope********"
                + "\nSuccess!  PolicyEntity uniqueness constraint allowed "
                + "\n   policyId1 " + p1.getPolicyId()
                + "\n   policyName1 " + p1.getPolicyName()
                + "\n   scope1 = " + p1.getScope()
                + "\n   policyId3 " + p3.getPolicyId()
                + "\n   policyName3 " + p3.getPolicyName()
                + "\n   scope3 = " + p3.getScope());

        //Assert that the policyIds are NOT the same to show that the automatic sequencing is working
        assert(p1.getPolicyId() != p3.getPolicyId());

        try{
            //Now set the scope the same to verify the uniqueness constraint will be enforced
            p3.setScope(p1.getScope());

            em.flush();
            logger.debug("\n\n***********PolicyEntityTest: PolicyEntity Unique test for policyName and scope********"
                    + "\nFailed! PolicyEntity Uniqueness constraint FAILED and DID allow "
                    + "\n   policyId1 " + p1.getPolicyId()
                    + "\n   policyName1 " + p1.getPolicyName()
                    + "\n   scope1 = " + p1.getScope()
                    + "\n   policyId3 " + p3.getPolicyId()
                    + "\n   policyName3 " + p3.getPolicyName()
                    + "\n   scope3 = " + p3.getScope());;
        }
        catch(Exception e){
            //Success
            logger.debug("\n\n***********PolicyEntityTest: PolicyEntity Unique test for policyName and scope********"
                    + "\nSuccess!  PolicyEntity Uniqueness constraint SUCCEEDED and did NOT allow "
                    + "\n   policyId1 " + p1.getPolicyId()
                    + "\n   policyName1 " + p1.getPolicyName()
                    + "\n   scope1 = " + p1.getScope()
                    + "\n   policyId3 " + p3.getPolicyId()
                    + "\n   policyName3 " + p3.getPolicyName()
                    + "\n   scope3 = " + p3.getScope()
                    + "\n   with excpetion: " + e);
        }


        try{
            et2.commit();
            logger.debug("\n\n***********PolicyEntityTest: et2.commit Succeeded********");
        }catch(Exception e){
            logger.debug("\n\n***********PolicyEntityTest: et2.commit Failed********"
                    + "\nTRANSACTION ROLLBACK "
                    + "\n   with exception: " + e);
        }
        
        //****************Test the PolicyDBDaoEntity************************
        
        //Create a transaction
        EntityTransaction et3 = em.getTransaction();

        et3.begin();
        
        //create one 
        PolicyDBDaoEntity pe1 = new PolicyDBDaoEntity();
        em.persist(pe1);
        
        pe1.setDescription("This is pe1");
        
        pe1.setPolicyDBDaoUrl("http://123.45.2.456:2345");
        
        //push it to the DB
        em.flush();
        
        //create another
        PolicyDBDaoEntity pe2 = new PolicyDBDaoEntity();
        em.persist(pe2);
        
        pe2.setDescription("This is pe2");
        
        pe2.setPolicyDBDaoUrl("http://789.01.2.345:2345");
        
        //Print them to the log before flushing
        logger.debug("\n\n***********PolicyEntityTest: PolicyDBDaoEntity objects before flush********"
                + "\n   policyDBDaoUrl-1 = " + pe1.getPolicyDBDaoUrl()
                + "\n   description-1 = " + pe1.getDescription()
                + "\n   createdDate-1 = " + pe1.getCreatedDate()
                + "\n   modifiedDate-1 " + pe1.getModifiedDate()
                + "\n*****************************************"
                + "\n   policyDBDaoUrl-2 = " + pe2.getPolicyDBDaoUrl()
                + "\n   description-2 = " + pe2.getDescription()
                + "\n   createdDate-2 = " + pe2.getCreatedDate()
                + "\n   modifiedDate-2 " + pe2.getModifiedDate()
                );
        
        //push it to the DB
        em.flush();
        
        //Now let's retrieve them from the DB using the named query
        
        resultList = em.createNamedQuery("PolicyDBDaoEntity.findAll").getResultList();

        PolicyDBDaoEntity pex = null;
        PolicyDBDaoEntity pey = null;

        if(!resultList.isEmpty()){
            if (resultList.size() != 2){
                fail("\nPolicyEntityTest: Number of PolicyDBDaoEntity entries = " + resultList.size() + " instead of 2");
            }
            for(Object policyDBDaoEntity: resultList){
                PolicyDBDaoEntity pdbdao = (PolicyDBDaoEntity)policyDBDaoEntity;
                if(pdbdao.getPolicyDBDaoUrl().equals("http://123.45.2.456:2345")){
                    pex = pdbdao;
                }else if(pdbdao.getPolicyDBDaoUrl().equals("http://789.01.2.345:2345")){
                    pey = pdbdao;
                }
            }

            //Print them to the log before flushing
            logger.debug("\n\n***********PolicyEntityTest: PolicyDBDaoEntity objects retrieved from DB********"
                    + "\n   policyDBDaoUrl-x = " + pex.getPolicyDBDaoUrl()
                    + "\n   description-x = " + pex.getDescription()
                    + "\n   createdDate-x = " + pex.getCreatedDate()
                    + "\n   modifiedDate-x " + pex.getModifiedDate()
                    + "\n*****************************************"
                    + "\n   policyDBDaoUrl-y = " + pey.getPolicyDBDaoUrl()
                    + "\n   description-y = " + pey.getDescription()
                    + "\n   createdDate-y = " + pey.getCreatedDate()
                    + "\n   modifiedDate-y " + pey.getModifiedDate()
                    );
            //Verify the retrieved objects are the same as the ones we stored in the DB
            if(pex.getPolicyDBDaoUrl().equals("http://123.45.2.456:2345")){
                assertSame(pe1,pex);
                assertSame(pe2,pey);
            }else{
                assertSame(pe2,pex);
                assertSame(pe1,pey);
            }
            
        }else{
            fail("\nPolicyEntityTest: No PolicyDBDaoEntity DB entry found");
        }
       
        //Now let's see if we can do an update on the PolicyDBDaoEntity which we retrieved.
        //em.persist(pex);
        pex.setDescription("This is pex");
        em.flush();
        
        //retrieve it
        Query createPolicyQuery = em.createQuery("SELECT p FROM PolicyDBDaoEntity p WHERE p.description=:desc");
        resultList = createPolicyQuery.setParameter("desc", "This is pex").getResultList();

        PolicyDBDaoEntity pez = null;
        
        if(!resultList.isEmpty()){
            if (resultList.size() != 1){
                fail("\nPolicyEntityTest: Update Test - Number of PolicyDBDaoEntity entries = " + resultList.size() + " instead of 1");
            }
            pez = (PolicyDBDaoEntity) resultList.get(0);

            //Print them to the log before flushing
            logger.debug("\n\n***********PolicyEntityTest: Update Test - PolicyDBDaoEntity objects retrieved from DB********"
                    + "\n   policyDBDaoUrl-x = " + pex.getPolicyDBDaoUrl()
                    + "\n   description-x = " + pex.getDescription()
                    + "\n   createdDate-x = " + pex.getCreatedDate()
                    + "\n   modifiedDate-x " + pex.getModifiedDate()
                    + "\n*****************************************"
                    + "\n   policyDBDaoUrl-z = " + pez.getPolicyDBDaoUrl()
                    + "\n   description-z = " + pez.getDescription()
                    + "\n   createdDate-z = " + pez.getCreatedDate()
                    + "\n   modifiedDate-z " + pez.getModifiedDate()
                    );
            //Verify the retrieved objects are the same as the ones we stored in the DB
            assertSame(pex,pez);
        }else{
            fail("\nPolicyEntityTest: Update Test - No PolicyDBDaoEntity DB updated entry found");
        }
        
        //Clean up the DB
        em.createQuery("DELETE FROM PolicyDBDaoEntity").executeUpdate();
        em.createQuery("DELETE FROM PolicyEntity").executeUpdate();
        em.createQuery("DELETE FROM ConfigurationDataEntity").executeUpdate();
        em.createQuery("DELETE FROM ActionBodyEntity").executeUpdate();
        
        //Wrap up the transaction
        try{
            et3.commit();
            logger.debug("\n\n***********PolicyEntityTest: et3.commit Succeeded********");
        }catch(Exception e){
            logger.debug("\n\n***********PolicyEntityTest: et3.commit Failed********"
                    + "\nTRANSACTION ROLLBACK "
                    + "\n   with exception: " + e);
        }
         
        
        //Tidy up
        em.close();
    }
    
}
