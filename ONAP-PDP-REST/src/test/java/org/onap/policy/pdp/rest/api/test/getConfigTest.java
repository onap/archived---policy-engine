/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.pdp.rest.api.models.PolicyConfig;
import org.onap.policy.pdp.rest.api.services.GetConfigService;

public class getConfigTest {
	private static final String TEST = "test";

	@SuppressWarnings("unchecked")
	@Test
	public void filterMethodTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
		GetConfigService getConfigService= new GetConfigService(configRequestParameters, null);
		Method filter = GetConfigService.class.getDeclaredMethod("filterResults", Collection.class,ConfigRequestParameters.class);
		filter.setAccessible(true);
		List<PolicyConfig> policyConfigs = new LinkedList<>();

		List<PolicyConfig> filterResults = (List<PolicyConfig>) filter.invoke(getConfigService, policyConfigs,configRequestParameters);
		assertEquals(PolicyConfigStatus.CONFIG_NOT_FOUND, filterResults.get(0).getPolicyConfigStatus());
		// Check again with some values
		configRequestParameters.setPolicyName(TEST);
		configRequestParameters.setOnapName(TEST);
		configRequestParameters.setConfigName(TEST);
		Map<String,String> configAttributes = new HashMap<>();
		configAttributes.put(TEST, TEST);
		configRequestParameters.setConfigAttributes(configAttributes);
		PolicyConfig pConfig = new PolicyConfig();
		pConfig.setPolicyName(TEST);
		Map<String,String> matching = new HashMap<>();
		matching.put("ONAPName", TEST);
		matching.put("ConfigName", TEST);
		matching.put("TEST", TEST);
		pConfig.setMatchingConditions(matching);
		policyConfigs.add(pConfig);
		filterResults = (List<PolicyConfig>) filter.invoke(getConfigService, policyConfigs,configRequestParameters);
		assertEquals(PolicyConfigStatus.CONFIG_NOT_FOUND, filterResults.get(0).getPolicyConfigStatus());
	}
}
