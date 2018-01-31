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

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.junit.*;
import org.onap.policy.api.EventRequestParameters;

import static org.junit.Assert.*;

/**
 * The class <code>EventRequestParametersTest</code> contains tests for the class <code>{@link EventRequestParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class EventRequestParametersTest {
	/**
	 * Run the EventRequestParameters() constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testEventRequestParameters_1()
		throws Exception {

		EventRequestParameters result = new EventRequestParameters();

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getEventAttributes());
		assertEquals(null, result.getRequestID());
	}

	/**
	 * Run the EventRequestParameters(Map<String,String>,UUID) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testEventRequestParameters_2()
		throws Exception {
		Map<String, String> eventAttributes = new Hashtable<String, String>();
		UUID requestID = UUID.randomUUID();

		EventRequestParameters result = new EventRequestParameters(eventAttributes, requestID);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the Map<String, String> getEventAttributes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetEventAttributes_1()
		throws Exception {
		EventRequestParameters fixture = new EventRequestParameters(new Hashtable<String, String>(), UUID.randomUUID());

		Map<String, String> result = fixture.getEventAttributes();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the UUID getRequestID() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testGetRequestID_1()
		throws Exception {
		EventRequestParameters fixture = new EventRequestParameters(new Hashtable<String, String>(), UUID.fromString("5b15376d-569b-4772-ac75-9362043f6a6c"));

		UUID result = fixture.getRequestID();

		// add additional test code here
		assertNotNull(result);
		assertEquals("5b15376d-569b-4772-ac75-9362043f6a6c", result.toString());
		assertEquals(4, result.version());
		assertEquals(2, result.variant());
		assertEquals(-6019743277723456916L, result.getLeastSignificantBits());
		assertEquals(6563212974706345842L, result.getMostSignificantBits());
	}

	/**
	 * Run the void setEventAttributes(Map<String,String>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetEventAttributes_1()
		throws Exception {
		EventRequestParameters fixture = new EventRequestParameters(new Hashtable<String, String>(), UUID.randomUUID());
		Map<String, String> eventAttributes = new Hashtable<String, String>();

		fixture.setEventAttributes(eventAttributes);

		// add additional test code here
	}

	/**
	 * Run the void setRequestID(UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Test
	public void testSetRequestID_1()
		throws Exception {
		EventRequestParameters fixture = new EventRequestParameters(new Hashtable<String, String>(), UUID.randomUUID());
		UUID requestID = UUID.randomUUID();

		fixture.setRequestID(requestID);

		// add additional test code here
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
		new org.junit.runner.JUnitCore().run(EventRequestParametersTest.class);
	}
}
