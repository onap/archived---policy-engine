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
package org.onap.policy.pap.xacml.rest.elk;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;

public class PolicyElasticSearchControllerTest {
	
	private PolicyElasticSearchController conroller;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private String jsonString = null;
	
	@Before
	public void setup(){
		conroller = new PolicyElasticSearchController();
		request = Mockito.mock(HttpServletRequest.class);
		response = Mockito.mock(HttpServletResponse.class);
	}
	
	@Test
	public void testSearchDictionary(){
		jsonString = "{\"type\":\"onapName\",\"data\":{\"description\":\"test\",\"onapName\":\"Test\"}}";
		BufferedReader br = new BufferedReader(new StringReader(jsonString));	
		try {
			when(request.getReader()).thenReturn(br);
			conroller.searchDictionary(request, response);
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
	}
}
