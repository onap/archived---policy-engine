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
package org.onap.policy.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AutoPushControllerTest {
	private PolicyController controller = new PolicyController();;
	private AutoPushController apController = new AutoPushController();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testAutoPushSetGet() throws IOException {
		// Get and set tests
		apController.setPolicyController(controller);
		assertEquals(apController.getPolicyController(), controller);
	}
	
	@Test
	public void testNegativeCase1() {
		try {
			apController.getPolicyGroupContainerData(null,  null);
		}
		catch (Exception ex) {
			fail("No exceptions expected, received: " + ex);
		}
	}
		
	@Test
	public void testNegativeCase2() throws IOException {
		thrown.expect(NullPointerException.class);
		apController.pushPolicyToPDPGroup(null, null);
	}

	@Test
	public void testNegativeCase3() throws IOException {
		thrown.expect(NullPointerException.class);
		apController.removePDPGroup(null, null);
	}
}
