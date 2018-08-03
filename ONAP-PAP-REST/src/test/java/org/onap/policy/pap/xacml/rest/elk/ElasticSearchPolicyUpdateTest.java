/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pap.xacml.rest.elk;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.test.XACMLPAPTest;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.pap.xacml.rest.elk.client.ElasticSearchPolicyUpdate;
import org.onap.policy.rest.jpa.ConfigurationDataEntity;
import org.onap.policy.rest.jpa.PolicyEntity;

public class ElasticSearchPolicyUpdateTest {

    private static Logger logger = FlexLogger.getLogger(ElasticSearchPolicyUpdateTest.class);
    private Object policyContent = "";
    private XACMLPAPTest papTest;

    @Before
    public void setUp() throws IOException, ServletException, SQLException{
        // Set the system property temporarily
        System.setProperty("PROPERTY_FILE", "src/test/resources/policyelk.properties");
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            policyContent = IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml"));
        } catch (Exception e1) {
            logger.error("Exception Occured"+e1);
        }
        papTest = new XACMLPAPTest();
        papTest.setDBDao();
    }

    @Test
    public void testElasticSearchMainFunction() throws SQLException{
        ConfigurationDataEntity configurationEntity = new ConfigurationDataEntity();
        configurationEntity.setConfigBody("Sample Test");
        configurationEntity.setConfigType("OTHER");
        configurationEntity.setConfigurationName("com.Config_SampleTest1206.1.txt");
        configurationEntity.setDescription("test");
        configurationEntity.setModifiedBy("Test");
        configurationEntity.setModifiedDate(new Date());
        
        
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_SampleTest.1.xml");
        entity.setPolicyData(policyContent.toString());
        entity.setScope("com");
        entity.setCreatedBy("Test");
        entity.setDeleted(false);
        entity.setDescription("Test");
        entity.setModifiedBy("Test");
        entity.setConfigurationData(configurationEntity);
        entity.preUpdate();
        CommonClassDaoImpl dao = new CommonClassDaoImpl();
        dao.save(configurationEntity);
        dao.save(entity);
        dao.delete(dao.getEntityItem(PolicyEntity.class, "policyName", "Config_SampleTest.1.xml"));
        ElasticSearchPolicyUpdate.main(null);
        StringBuilder policyDataString = new StringBuilder();
        ElasticSearchPolicyUpdate.constructPolicyData(policyContent, policyDataString);
        assertTrue(policyDataString.toString().contains("onapName"));
    }

    @After
    public void reset(){
        System.clearProperty("PROPERTY_FILE");
    }
}
