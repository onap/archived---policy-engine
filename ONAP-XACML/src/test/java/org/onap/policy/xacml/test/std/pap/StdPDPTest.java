package org.onap.policy.xacml.test.std.pap;
/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPStatus;

public class StdPDPTest {

	private static Logger logger = FlexLogger.getLogger(StdPDPTest.class);

	private StdPDP stdPDP;

	@Before
	public void setUp(){

		try {
			stdPDP = new StdPDP();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetId() {
		try {
			stdPDP.setId("testId");
			assertTrue(stdPDP.getId() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetName() {
		try {
			stdPDP.setName("abc");
			assertTrue(stdPDP.getName() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetDescription() {
		try {
			stdPDP.setDescription("somee here");
			assertTrue(stdPDP.getDescription() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetStatus() {
		try {
			stdPDP.setStatus(new StdPDPStatus());
			assertTrue(stdPDP.getStatus() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPipConfigs() {
		try {
			assertTrue(stdPDP.getPipConfigs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetJmxPort() {
		try {
			stdPDP.setJmxPort(123);
			assertTrue(stdPDP.getJmxPort() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
