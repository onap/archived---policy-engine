/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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
package org.onap.policy.xacml.pdp.std.functions;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PolicyListTest {

	@Before
	public void setUp() throws Exception {
		PolicyList.addPolicyID("Test");
	}

	@Test
	public final void testGetpolicyList() {
		try{
			assertTrue(PolicyList.getpolicyList().size() ==1);
		}catch(Exception e){
			fail("operation failed, e="+e);
		}
	}

	@Test
	public final void testAddPolicyID() {
		try{
			PolicyList.addPolicyID("Test");
			assertTrue(PolicyList.getpolicyList().size() == 1);
		}catch(Exception e){
			fail("operation failed, e="+e);
		}
	}

	@Test
	public final void testClearPolicyList() {
		try{
			PolicyList.clearPolicyList();
			assertTrue(PolicyList.getpolicyList().size() == 0);
		}catch(Exception e){
			fail("operation failed, e="+e);
		}
	}

}
