/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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
package org.openecomp.policy.admin;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.controller.PolicyController;
import org.openecomp.policy.model.Roles;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.UserInfo;

public class PolicyManagerServletTest extends Mockito{
	
	private static Logger logger = FlexLogger.getLogger(PolicyManagerServletTest.class);

	private static List<Object> rolesdata;
	private static List<Object> policyData;
	private static List<Object> policyEditorScopes;
	private static List<Object> policyVersion;
	
	@Before
	public void setUp() throws Exception{
		logger.info("setUp: Entering");
		UserInfo userinfo = new UserInfo();
		userinfo.setUserLoginId("Test");
		userinfo.setUserName("Test");
		//Roles Data
        rolesdata = new ArrayList<>();
        Roles roles = new Roles();
        roles.setLoginId("Test");
        roles.setRole("super-admin");
        Roles roles1 = new Roles();
        roles1.setLoginId("Test");
        roles1.setRole("admin");
        roles1.setScope("['com','Test']");
        rolesdata.add(roles);
        rolesdata.add(roles1);
        
        //PolicyEntity Data
        policyData = new ArrayList<>();
        String policyContent = "";
        try {
			ClassLoader classLoader = getClass().getClassLoader();
			policyContent = IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml"));
		} catch (Exception e1) {
			logger.error("Exception Occured"+e1);
		}
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_SampleTest.1.xml");
        entity.setPolicyData(policyContent);
        entity.setScope("com");
        policyData.add(entity);
        
        //PolicyEditorScopes data
        policyEditorScopes = new ArrayList<>();
        PolicyEditorScopes scopes = new PolicyEditorScopes();
        scopes.setScopeName("com");
        scopes.setUserCreatedBy(userinfo);
        scopes.setUserModifiedBy(userinfo);
        PolicyEditorScopes scopes1 = new PolicyEditorScopes();
        scopes1.setScopeName("com\\Test");
        scopes1.setUserCreatedBy(userinfo);
        scopes1.setUserModifiedBy(userinfo);
        policyEditorScopes.add(scopes);
        policyEditorScopes.add(scopes1);
        
        //PolicyVersion data
        policyVersion = new ArrayList<>();
        PolicyVersion policy = new PolicyVersion();
        policy.setPolicyName("com\\Config_SampleTest1206");
        policy.setActiveVersion(1);
        policy.setHigherVersion(1);
        policy.setCreatedBy("Test");
        policy.setModifiedBy("Test");
        policyVersion.add(policy);
	}
	
	@Test
	public void testDescribePolicy(){
		PolicyManagerServlet servlet = new PolicyManagerServlet();
		HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class);  
        PolicyController controller = mock(PolicyController.class);
        
        BufferedReader reader = new BufferedReader(new StringReader("{params: { mode: 'DESCRIBEPOLICYFILE', path: 'com.Config_SampleTest1206.1.xml'}}"));
        try {
			when(request.getReader()).thenReturn(reader);
			when(controller.getDataByQuery("FROM PolicyEntity where policyName = 'Config_SampleTest1206.1.xml' and scope ='com'")).thenReturn(policyData);
			servlet.setPolicyController(controller);
			servlet.doPost(request, response);
		} catch (Exception e1) {
			logger.error("Exception Occured"+e1);
		}
	}
	
	
	@Test
	public void testPolicyScopeList(){
		PolicyManagerServlet servlet = new PolicyManagerServlet();
		HttpServletRequest request = mock(HttpServletRequest.class);       
        HttpServletResponse response = mock(HttpServletResponse.class); 
        PolicyController controller = mock(PolicyController.class);
        List<String> list = new ArrayList<>();
        list.add("{params: { mode: 'LIST', path: '/', onlyFolders: false}}");
        list.add("{params: { mode: 'LIST', path: '/com', onlyFolders: false}}");
        for(int i =0; i < list.size(); i++){
        	BufferedReader reader = new BufferedReader(new StringReader(list.get(i)));
            try {
    			when(request.getReader()).thenReturn(reader);
    			when(controller.getRoles("Test")).thenReturn(rolesdata);
    			when(controller.getDataByQuery("from PolicyEditorScopes")).thenReturn(policyEditorScopes);
    			when(controller.getDataByQuery("from PolicyEditorScopes where SCOPENAME like 'com%'")).thenReturn(policyEditorScopes);
    			when(controller.getDataByQuery("from PolicyVersion where POLICY_NAME like 'com%'")).thenReturn(policyVersion);
    			servlet.setPolicyController(controller);
    			servlet.setTestUserId("Test");
    			servlet.doPost(request, response);
    		} catch (Exception e1) {
    			logger.error("Exception Occured"+e1);
    		}
        }
	}
	

}
