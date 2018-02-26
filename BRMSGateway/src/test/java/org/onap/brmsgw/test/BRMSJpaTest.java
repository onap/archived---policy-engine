/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.brmsgw.test;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.api.PEDependency;
import org.onap.policy.brmsInterface.jpa.BRMSGroupInfo;
import org.onap.policy.brmsInterface.jpa.BRMSPolicyInfo;
import org.onap.policy.brmsInterface.jpa.DependencyInfo;

public class BRMSJpaTest {
	@Test
	public void testJpa() {
		String testVal = "testVal";
		BRMSGroupInfo groupInfo = new BRMSGroupInfo();
		
		// Test group info
		groupInfo.setControllerName(testVal);
		assertEquals(groupInfo.getControllerName(), testVal);
		groupInfo.setGroupId(testVal);
		assertEquals(groupInfo.getGroupId(), testVal);
		groupInfo.setArtifactId(testVal);
		assertEquals(groupInfo.getArtifactId(), testVal);
		groupInfo.setVersion(testVal);
		assertEquals(groupInfo.getVersion(), testVal);
		
		// Test policy info
		BRMSPolicyInfo policyInfo = new BRMSPolicyInfo();
		policyInfo.setPolicyName(testVal);
		assertEquals(policyInfo.getPolicyName(), testVal);
		policyInfo.setControllerName(groupInfo);
		assertEquals(policyInfo.getControllerName(), groupInfo);
	}
	
	@Test
	public void testDependencyInfo() {
		String testKey = "testKey";
		PEDependency dependency = new PEDependency();
		List<PEDependency> list = new ArrayList<PEDependency>();
		list.add(dependency);
		Map<String, List<PEDependency>> map = new HashMap<String, List<PEDependency>>();
		map.put(testKey, list);
		DependencyInfo info = new DependencyInfo();
		
		info.setDependencies(map);
		assertEquals(info.getDependencies(), map);
	}
}
