/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.elk;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.searchbox.client.JestResult;
import java.lang.reflect.Method;
import org.junit.Test;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnectorImpl;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

public class ElkConnectorImplTest {

	@Test
	public void isAlphaNumericTest() {
		try {
			Method method = ElkConnectorImpl.class.getDeclaredMethod("isAlphaNumeric", String.class);
			method.setAccessible(true);
			assertTrue((boolean) method.invoke(new ElkConnectorImpl(), "abc123"));
			assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123*"));
			assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123{}"));
			assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123\n"));
			assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123<"));
			assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123:"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void searchTest(){
		JestResult r1=null, r2=null, r3=null, r4=null;

		// Should always work if the above test passes and ELK server is up
		try{
			r1 = new ElkConnectorImpl().search(PolicyIndexType.decision, "abc123");
		} catch (Exception e) {
			// ELK server is down. Don't continue the test
			if(e instanceof IllegalStateException){
				return;
			}
			fail();
		}

		// Should always work
		try{
			r2 = new ElkConnectorImpl().search(PolicyIndexType.decision, "The_quick_brown_fox_jumps_over_the_lazy_dog");
		} catch (Exception e) {
			fail();
		}

		// Should throw exception
		try{
			r3 = new ElkConnectorImpl().search(PolicyIndexType.decision, "abc123{}");
		} catch (Exception e) {
			if(! (e instanceof IllegalArgumentException)){
				fail();
			}
		}
		
		// Should throw exception
		try{
			r4 = new ElkConnectorImpl().search(PolicyIndexType.decision, "The quick brown fox jumps over the lazy dog");
		} catch (Exception e) {
			if(! (e instanceof IllegalArgumentException)){
				fail();
			}
		}

		assertNotNull(r1);
		assertNotNull(r2);
		assertNull(r3);
		assertNull(r4);
	}
	
	@Test
	public void testDelete() {
		ElkConnectorImpl impl = new ElkConnectorImpl();
		PolicyRestAdapter adapter = new PolicyRestAdapter();
		try {
			boolean is = impl.delete(adapter);
		}
		catch (Exception ex) {
		}
	}
	
	@Test
	public void testPut() {
		ElkConnectorImpl impl = new ElkConnectorImpl();
		PolicyRestAdapter adapter = new PolicyRestAdapter();
		try {
			boolean is = impl.put(adapter);
		}
		catch (Exception ex) {
		}
	}
	
	@Test
	public void testUpdate() {
		ElkConnectorImpl impl = new ElkConnectorImpl();
		PolicyRestAdapter adapter = new PolicyRestAdapter();
		try {
			boolean is = impl.update(adapter);
		}
		catch (Exception ex) {
		}
	}
	
	@Test
	public void testSearchWithFilter() {
		ElkConnectorImpl impl = new ElkConnectorImpl();
		PolicyRestAdapter adapter = new PolicyRestAdapter();
		try {
			JestResult result = impl.search(PolicyIndexType.config, "search", null);
		}
		catch (Exception ex) {
		}
	}
}
