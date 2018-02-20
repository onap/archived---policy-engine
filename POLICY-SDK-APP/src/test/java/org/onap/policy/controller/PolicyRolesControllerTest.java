/*-
 * ============LICENSE_START=======================================================
 * POLICY-SDK-APP
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
package org.onap.policy.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.mock.web.MockHttpServletResponse;

public class PolicyRolesControllerTest {
	private static Logger logger = FlexLogger.getLogger(PolicyRolesControllerTest.class);
	private HttpServletRequest request = null;
	private MockHttpServletResponse response = null;
	private PolicyRolesController controller = null;	
	private static CommonClassDao commonClassDao;
	
	@Before
	public void setUp() throws Exception {
		logger.info("setUp: Entering");
		controller = new PolicyRolesController();
		commonClassDao = Mockito.mock(CommonClassDao.class);
		request = mock(HttpServletRequest.class);       
		response =  new MockHttpServletResponse();
		PolicyRolesController.setCommonClassDao(commonClassDao);
		
		HttpSession mockSession = mock(HttpSession.class);
        User user = new User();
		user.setOrgUserId("Test");
		Mockito.when(mockSession.getAttribute(SystemProperties.getProperty("user_attribute_name"))).thenReturn(user);
		Mockito.when(request.getSession(false)).thenReturn(mockSession);
		
		logger.info("setUp: exit");
	}

	@Test
	public void testGetPolicyRolesEntityData() {
	try {
		controller.getPolicyRolesEntityData(request, response);
		assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("rolesDatas"));
	} catch (UnsupportedEncodingException e) {
		logger.error("Exception Occured"+e);
		fail();
	}
}

	@Test
	public void testGetPolicyScopesEntityData() {
		try {
			controller.getPolicyScopesEntityData(request, response);
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("scopeDatas"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
			fail();
		}
	}

	@Test
	public void testSaveRolesEntityData() {
			logger.info("PolicyRolesControllerTest: Entering");
			String jsonString = "{\"editRoleData\": {\"id\": 1,\"loginId\": {\"userLoginId\" : \"test\", \"userName\" : \"test\"},\"role\":\"test\",\"scope\":[\"scope\"]}}";
			try(BufferedReader br = new BufferedReader(new StringReader(jsonString))){
				when(request.getReader()).thenReturn(br); 		    
				controller.SaveRolesEntityData(request, response);
				logger.info("response.getContentAsString(): " + response.getContentAsString());
				assertTrue( response.getContentAsString() != null && response.getContentAsString().contains("rolesDatas"));
			} catch (Exception e) {
			fail("Exception: " + e);
		}
		logger.info("PolicyRolesControllerTest: exit");
	}
}

