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

package org.onap.policy.pap.xacml.rest.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.mockrunner.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
public class MetricServiceTest {
	@PrepareForTest({XACMLPapServlet.class})
	@Test
	public void testNegativeGet() {
		// Mock pap servlet
		PowerMockito.mockStatic(XACMLPapServlet.class);
		when(XACMLPapServlet.getPAPEngine()).thenReturn(null);
		when(XACMLPapServlet.getEmf()).thenReturn(null);

		MockHttpServletResponse response = new MockHttpServletResponse();
		MetricService.doGetPolicyMetrics(response);
		assertEquals(response.getStatusCode(), HttpServletResponse.SC_BAD_REQUEST);
	}
}
