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

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import junit.framework.TestCase;

public class GetConfigByPolicyNameTest extends TestCase {
	private PolicyEngine policyEngine = null;
	private String policyName = null;
	private Collection<PolicyConfig> policyConfig = null;
	private static final Logger logger = FlexLogger.getLogger(GetConfigByPolicyNameTest.class);
	@Before
	public void setUp() {
		try {
			policyEngine = new PolicyEngine("Test/config_pass.properties");
		} catch (PolicyEngineException e) {
			logger.error(e.getMessage());
			fail("PolicyEngine Instantiation Error" + e);
		}
		logger.info("Loaded.. PolicyEngine");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetConfigPolicyNameNotValid(){
		policyName = null;
		try{
			policyConfig = policyEngine.getConfigByPolicyName(policyName);
		} catch (PolicyConfigException e){
			logger.warn(e.getMessage());
		}
		assertNull(policyConfig);
	}
}
