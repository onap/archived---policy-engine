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

package org.onap.policy.brmsInterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.api.PolicyException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.onap.policy.utils.BackUpHandler;
import org.onap.policy.utils.BackUpMonitor;
import org.onap.policy.utils.BackUpMonitorException;
import org.apache.maven.model.Dependency;
import javax.persistence.Query;

@RunWith(PowerMockRunner.class)
public class BRMSPushTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@PrepareForTest({Persistence.class, BackUpMonitor.class})
	@Test
	public void testPush() throws BackUpMonitorException, PolicyException {
		// Mock emf, persistence, and query
		EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
		EntityManager em = Mockito.mock(EntityManager.class);
		Mockito.when(emf.createEntityManager()).thenReturn(em);
		PowerMockito.mockStatic(Persistence.class);
		PowerMockito.when(Persistence.createEntityManagerFactory(Mockito.any(), Mockito.any())).thenReturn(emf);
		EntityTransaction et = Mockito.mock(EntityTransaction.class);
		Mockito.when(em.getTransaction()).thenReturn(et);		
		Query query = Mockito.mock(Query.class);
		Mockito.when(em.createQuery(Mockito.anyString())).thenReturn(query);		

		// Mock backup monitor
		PowerMockito.mockStatic(BackUpMonitor.class);
		BackUpMonitor monitor = Mockito.mock(BackUpMonitor.class);
		PowerMockito.when(BackUpMonitor.getInstance(any(), any(), any(), any())).thenReturn(monitor);

		// Test constructor
		String propFile = "config.properties";
		BackUpHandler handler = Mockito.mock(BackUpHandler.class);
		BRMSPush push = new BRMSPush(propFile, handler);
		assertNotNull(push);
		
		String name = "testName";
		try {
			// Test initiate
			push.initiate(true);
			
			// Test reset
			push.resetDS();
		
			// Test add
			String rule = "testRule";
			Map<String, String> responseAttributes = new HashMap<String, String>();
			responseAttributes.put("$controller:", "{\n\"testKey\": \"testVal\"\n}\n");
			responseAttributes.put("$dependency$", "[a,b]");
			push.addRule(name, rule, responseAttributes);
		}
		catch (Exception ex) {
			fail("Not expecting an exception: " + ex);
		}
				
		try {
			// Test remove
			push.removeRule(name);
		}
		catch (Exception ex) {
			fail("Not expecting an exception: " + ex);

		}
		
		// Test misc methods
		String controllerName = "testController";
		List<Dependency> deps = push.defaultDependencies(controllerName);
		assertEquals(deps.size(), 7);
		assertNotNull(BRMSPush.getBackUpMonitor());
		assertEquals(push.urlListSize(), 1);

		try {
			push.rotateURLs();
		}
		catch (Exception ex) {
			fail("Not expecting an exception: " + ex);
		}

		// Test push
		thrown.expect(PolicyException.class);
		push.pushRules();
	}
}
