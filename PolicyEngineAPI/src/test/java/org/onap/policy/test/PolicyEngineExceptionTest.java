/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.test;

import org.junit.*;
import org.onap.policy.api.PolicyEngineException;

import static org.junit.Assert.*;

/**
 * The class <code>PolicyEngineExceptionTest</code> contains tests for the class <code>{@link PolicyEngineException}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class PolicyEngineExceptionTest {
	/**
	 * Run the PolicyEngineException() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testPolicyEngineException_1()
		throws Exception {

		PolicyEngineException result = new PolicyEngineException();

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("org.onap.policy.api.PolicyEngineException", result.toString());
		assertEquals(null, result.getLocalizedMessage());
		assertEquals(null, result.getMessage());
	}

	/**
	 * Run the PolicyEngineException(String) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testPolicyEngineException_2()
		throws Exception {
		String message = "";

		PolicyEngineException result = new PolicyEngineException(message);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("org.onap.policy.api.PolicyEngineException: ", result.toString());
		assertEquals("", result.getLocalizedMessage());
		assertEquals("", result.getMessage());
	}

	/**
	 * Run the PolicyEngineException(Throwable) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testPolicyEngineException_3()
		throws Exception {
		Throwable cause = new Throwable();

		PolicyEngineException result = new PolicyEngineException(cause);

		// add additional test code here
		assertNotNull(result);
		assertEquals("org.onap.policy.api.PolicyEngineException: java.lang.Throwable", result.toString());
		assertEquals("java.lang.Throwable", result.getLocalizedMessage());
		assertEquals("java.lang.Throwable", result.getMessage());
	}

	/**
	 * Run the PolicyEngineException(String,Throwable) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testPolicyEngineException_4()
		throws Exception {
		String message = "";
		Throwable cause = new Throwable();

		PolicyEngineException result = new PolicyEngineException(message, cause);

		// add additional test code here
		assertNotNull(result);
		assertEquals("org.onap.policy.api.PolicyEngineException: ", result.toString());
		assertEquals("", result.getLocalizedMessage());
		assertEquals("", result.getMessage());
	}

	/**
	 * Run the PolicyEngineException(String,Throwable,boolean,boolean) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testPolicyEngineException_5()
		throws Exception {
		String message = "";
		Throwable cause = new Throwable();
		boolean enableSuppression = true;
		boolean writableStackTrace = true;

		PolicyEngineException result = new PolicyEngineException(message, cause, enableSuppression, writableStackTrace);

		// add additional test code here
		assertNotNull(result);
		assertEquals("org.onap.policy.api.PolicyEngineException: ", result.toString());
		assertEquals("", result.getLocalizedMessage());
		assertEquals("", result.getMessage());
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(PolicyEngineExceptionTest.class);
	}
}
