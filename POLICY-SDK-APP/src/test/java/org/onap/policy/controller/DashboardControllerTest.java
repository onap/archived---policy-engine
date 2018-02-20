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

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.dao.SystemLogDbDao;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.springframework.mock.web.MockHttpServletResponse;

public class DashboardControllerTest{
	private static Logger logger = FlexLogger.getLogger(DashboardControllerTest.class);
	
	private static CommonClassDao commonClassDao;
	private static SystemLogDbDao systemDAO;
	private static PolicyController ctrl = null;
	private HttpServletRequest request = null;
	private DashboardController controller = null;
	private MockHttpServletResponse response = null;
	private Path repo;
	StdEngine engine = null;
	
	@Before
	public void setUp() throws Exception {
		logger.info("setUp: Entering");
		controller = new DashboardController();
		commonClassDao = Mockito.mock(CommonClassDao.class);
		systemDAO = Mockito.mock(SystemLogDbDao.class); 
		DashboardController.setSystemLogDbDao(systemDAO);
		DashboardController.setCommonClassDao(commonClassDao);
		request = mock(HttpServletRequest.class);       
		response =  new MockHttpServletResponse();
		repo = Paths.get("src/test/resources/pdps");
		engine = new StdEngine(repo);
		ctrl = new PolicyController();
		PolicyController.setPapEngine(engine);
		controller.setPolicyController(ctrl);
		logger.info("setUp: exit");
	}
	
	@Test
	public void testGetData() {
		try {
			controller.getData(request, response);
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("availableLoggingDatas"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
			fail();
		}
	}

	@Test
	public void testGetPAPStatusData() {
		try {
			controller.getPAPStatusData(request, response);
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("papTableDatas"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
			fail();
		}
	}

	@Test
	public void testGetPDPStatusData() {
		try {
			controller.getPDPStatusData(request, response);
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("pdpTableDatas"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
			fail();
		}
	}

	@Test
	public void testGetPolicyActivityData() {
		try {
			controller.getPolicyActivityData(request, response);
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("policyActivityTableDatas"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
			fail();
		}
	}

	@Test
	public void testGetSystemAlertData() {
		try {
			controller.getSystemAlertData(request, response);
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("systemAlertsTableDatas"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
			fail();
		}
	}
}

