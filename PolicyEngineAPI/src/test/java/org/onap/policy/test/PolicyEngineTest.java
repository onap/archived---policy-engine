/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class PolicyEngineTest {

	private static final Logger logger = FlexLogger.getLogger(PolicyEngineTest.class);
	private PolicyEngine policyEngine = null;
	private String filePath = null;

	@Test
	public void testPolicyEngineForFail() {
		filePath = null;
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyEngine);
		// Test even for this case.
		filePath = "NotNull";
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyEngine);
	}

	@Test
	public void testPolicyEngineforPropertyFileError() {
		filePath = "Test/config_error.property";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyEngine);
	}

	@Test
	public void testPolicyEngineforPDPURLError() {
		String filePath = "Test/config_fail.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNull(policyEngine);
	}

	@Test
	public void testPolicyEngineForPass() {
		String filePath = "Test/config_pass.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNotNull(policyEngine);
	}

	@Test
	public void testPolicyEngineForUEBPass() {
		String filePath = "Test/config_UEB_pass.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNotNull(policyEngine);
	}


	@Test
	public void testPolicyEngineForUEBBadType() {
		String filePath = "Test/config_UEB_bad_type.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNotNull(policyEngine);
	}

	@Test
	public void testPolicyEngineForUEBBadServerType() {
		String filePath = "Test/config_UEB_badservers.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNotNull(policyEngine);
	}

	@Test
	public void testPolicyEngineNotficationAutoUEB() {
		String filePath = "Test/config_UEB_pass.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
			policyEngine.setScheme(NotificationScheme.AUTO_ALL_NOTIFICATIONS);
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNotNull(policyEngine);
	}

	@Test
	public void testPolicyEngineNotficationAuto() {
		String filePath = "Test/config_pass.properties";
		isFileAvailable(filePath);
		try {
			policyEngine = new PolicyEngine(filePath);
			policyEngine.setScheme(NotificationScheme.AUTO_ALL_NOTIFICATIONS);
			//policyEngine.getNotification();
		} catch (PolicyEngineException e) {
			logger.warn(e.getMessage());
		}
		assertNotNull(policyEngine);
	}

	public void isFileAvailable(String filePath) {
		Path file = Paths.get(filePath);
		if (Files.notExists(file)) {
			logger.error("File Doesn't Exist "+ file.toString());
			fail("File: " +filePath + " Not found");
		}
	}
}
