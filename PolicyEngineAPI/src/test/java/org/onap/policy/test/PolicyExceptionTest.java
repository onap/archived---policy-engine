/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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
package org.onap.policy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.api.PolicyException;

public class PolicyExceptionTest {

	@Test
	public void test1() {
		PolicyException result = new PolicyException();		
		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("org.onap.policy.api.PolicyException", result.toString());
		assertEquals(null, result.getLocalizedMessage());
		assertEquals(null, result.getMessage());
	}

	@Test
	public void test2() {
		String message = "message";
		PolicyException result = new PolicyException(message);
		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("org.onap.policy.api.PolicyException: " + message, result.toString());
		assertEquals(message, result.getLocalizedMessage());
		assertEquals(message, result.getMessage());
		
	}

	@Test
	public void test3() {
		Throwable cause = new Throwable();
		PolicyException result = new PolicyException(cause);
		assertNotNull(result);
		assertEquals("org.onap.policy.api.PolicyException: java.lang.Throwable", result.toString());
		assertEquals("java.lang.Throwable", result.getLocalizedMessage());
		assertEquals("java.lang.Throwable", result.getMessage());
	}

	@Test
	public void test4() {
		String message = "";
		Throwable cause = new Throwable();
		PolicyException result = new PolicyException(message, cause);
		assertNotNull(result);
		assertEquals("org.onap.policy.api.PolicyException: ", result.toString());
		assertEquals("", result.getLocalizedMessage());
		assertEquals("", result.getMessage());
	}
	
	@Test
	public void test5()
		throws Exception {
		String message = "";
		Throwable cause = new Throwable();
		boolean enableSuppression = true;
		boolean writableStackTrace = true;

		PolicyException result = new PolicyException(message, cause, enableSuppression, writableStackTrace);

		// add additional test code here
		assertNotNull(result);
		assertEquals("org.onap.policy.api.PolicyException: ", result.toString());
		assertEquals("", result.getLocalizedMessage());
		assertEquals("", result.getMessage());
	}

	
}
