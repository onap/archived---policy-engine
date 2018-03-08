/*-
 * ================================================================================
 * ONAP Portal SDK
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property
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
 * ================================================================================
 */

package org.onap.portalapp.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

public class SecurityXssFilterTest {
	@Test
	public void testGets() throws ServletException, IOException {
		SecurityXssFilter filter = new SecurityXssFilter();
		assertNotNull(filter);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("POST");
		request.setBodyContent("testBody");
		request.setupAddParameter("testKey", "testVal");
		request.setAttribute("testKey", "testVal");
		request.setRequestURI("testVal");
		request.setRequestURL("testVal");
		MockHttpServletResponse response = new MockHttpServletResponse();
		FilterChain filterChain = Mockito.mock(FilterChain.class);
		filter.doFilterInternal(request, response, filterChain);
		assertEquals(response.getStatusCode(), HttpServletResponse.SC_OK);
	}
}
