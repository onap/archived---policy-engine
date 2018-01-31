/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.admin;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class CheckPDPTest {

	@Test
	public final void testCheckPDP() {
		try {
			System.setProperty("xacml.rest.pdp.idfile", new File(".").getCanonicalPath() + File.separator + "src"+ File.separator + "test" + File.separator + "resources" + File.separator + "test.properties");
			CheckPDP.validateID("http://localhost:8082/pdp/");
			assertTrue(CheckPDP.getPdpMap().containsKey("http://localhost:8082/pdp/"));
			assertTrue(CheckPDP.getEncoding("http://localhost:8082/pdp/").equals("dGVzdHBkcDphbHBoYTQ1Ng=="));
		} catch (Exception e) {
			fail("Error occured in CheckPDP test");
		}
	}

}
