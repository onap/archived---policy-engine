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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;

import org.junit.Test;
import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.pdp.rest.api.services.GetDictionaryService;

public class getDictionaryTest {
	
	@Test
	public void dictionaryJsonTest() throws Exception{
		Method formatDictionary = GetDictionaryService.class.getDeclaredMethod("formatDictionaryJson", String.class);
		formatDictionary.setAccessible(true);
		String input="{\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\","
				+ "\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\","
				+ "\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\",\"key\":\"value\"}";
		DictionaryParameters dp = new DictionaryParameters();
		dp.setDictionary("test");
		GetDictionaryService gds = new GetDictionaryService(dp, null);
		String result = (String) formatDictionary.invoke(gds, input);
		assertNull(result);
		//
		dp.setDictionary("OnapName");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Attribute");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Action");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("BRMSParamTemplate");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("VSCLAction");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("VNFType");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("PEPOptions");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Varbind");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Service");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Site");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Settings");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("DescriptiveScope");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Enforcer");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("ActionList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("ProtocolList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Zone");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("SecurityZone");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("PrefixList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("AddressGroup");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("ServiceGroup");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("ServiceList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("TermList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("RuleList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("FirewallRuleList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("Term");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("MicroServiceLocation");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("MicroServiceConfigName");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("DCAEUUID");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("MicroServiceModels");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("PolicyScopeService");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("PolicyScopeResource");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("PolicyScopeType");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("PolicyScopeClosedLoop");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("GroupPolicyScopeList");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("RiskType");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("SafePolicyWarning");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
		//
		dp.setDictionary("MicroServiceDictionary");
		gds = new GetDictionaryService(dp, null);
		result = (String) formatDictionary.invoke(gds, input);
		assertNotNull(result);
	}
}
