/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;

public class XACMLPapServletTest {
	@Test
	public void testSetAndGet() {
		String testVal = "testVal";
		XACMLPapServlet servlet = new XACMLPapServlet();
		
		System.setProperty("xacml.properties", "xacml.pap.properties");
		assertNotNull(XACMLPapServlet.getConfigHome());
		assertNotNull(XACMLPapServlet.getActionHome());
		assertEquals(XACMLPapServlet.getPersistenceUnit(), "XACML-PAP-REST");

		assertNull(XACMLPapServlet.getEmf());
		assertNull(servlet.getIa());
		assertNull(XACMLPapServlet.getPDPFile());
		assertNull(XACMLPapServlet.getPAPEngine());
		
		XACMLPapServlet.setPapDbDriver(testVal);
		assertEquals(XACMLPapServlet.getPapDbDriver(), testVal);
		XACMLPapServlet.setPapDbUrl(testVal);
		assertEquals(XACMLPapServlet.getPapDbUrl(), testVal);
		XACMLPapServlet.setPapDbUser(testVal);
		assertEquals(XACMLPapServlet.getPapDbUser(), testVal);
		XACMLPapServlet.setPapDbPassword(testVal);
		assertEquals(XACMLPapServlet.getPapDbPassword(), testVal);
		XACMLPapServlet.setMsOnapName(testVal);
		assertEquals(XACMLPapServlet.getMsOnapName(), testVal);
		XACMLPapServlet.setMsPolicyName(testVal);
		assertEquals(XACMLPapServlet.getMsPolicyName(), testVal);
	}
}
