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

package org.onap.policy.std.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.std.Matches;

/**
 * The class <code>MatchesTest</code> contains tests for the class
 * <code>{@link Matches}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class MatchesTest {
	private static final String DUMMY_VAL = "SomethingElse";
	private static final String CONFIG_NAME = "CONFIG_NAME";
	private static final String ONAP_NAME = "ONAP_NAME";

	@Test
	public void testMatches_SetterGetterMethods() {
		final Matches objUnderTest = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());

		final Map<String, String> result = objUnderTest.getConfigAttributes();

		assertEquals(ONAP_NAME, objUnderTest.getOnapName());
		assertEquals(CONFIG_NAME, objUnderTest.getConfigName());
		assertEquals(0, result.size());
	}

	@Test
	public void testMatches_EqualsMethod_SameObjectsAndSameHasCode() {
		final Matches firstObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());

		assertEquals(firstObject, secondObject);
		assertEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_differentConfigName_NotEqualsAndDifferentHashCode() {
		final Matches firstObject = getMatches(ONAP_NAME, DUMMY_VAL, Collections.emptyMap());
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());

		assertNotEquals(firstObject, secondObject);
		assertNotEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_differentObjects_NotEquals() {
		final String firstObject = new String();
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());
		Assert.assertFalse(secondObject.equals(firstObject));
	}

	@Test
	public void testMatchesEqualsMethod_nullObject_NotEqualsAndDifferentHashCode() {
		final String firstObject = null;
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());

		assertNotEquals(firstObject, secondObject);
	}

	@Test
	public void testMatchesEqualsMethod_NullConfigName_NotEqualsAndDifferentHashCode() {
		final Matches firstObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());
		final Matches secondObject = getMatches(ONAP_NAME, null, Collections.emptyMap());

		assertNotEquals(firstObject, secondObject);
		assertNotEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_differentOnapName_NotEqualsAndDifferentHashCode() throws Exception {
		final Matches firstObject = getMatches(DUMMY_VAL, CONFIG_NAME, Collections.emptyMap());
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());

		assertNotEquals(firstObject, secondObject);
		assertNotEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_NullOnapName_NotEqualsAndDifferentHashCode() throws Exception {
		final Matches firstObject = getMatches(null, CONFIG_NAME, Collections.emptyMap());
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, Collections.emptyMap());

		assertNotEquals(firstObject, secondObject);
		assertNotEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_BothNullOnapName_Equals() throws Exception {
		final Matches firstObject = getMatches(null, CONFIG_NAME, Collections.emptyMap());
		final Matches secondObject = getMatches(null, CONFIG_NAME, Collections.emptyMap());

		assertEquals(firstObject, secondObject);
		assertEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_BothNullConfigName_Equals() throws Exception {
		final Matches firstObject = getMatches(ONAP_NAME, null, Collections.emptyMap());
		final Matches secondObject = getMatches(ONAP_NAME, null, Collections.emptyMap());

		assertEquals(firstObject, secondObject);
		assertEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_DifferentConfigAttr_NotEqualsAndDifferentHashCode() throws Exception {
		final Map<String, String> firstMap = Collections.emptyMap();
		final Map<String, String> secondMap = new HashMap<>();
		secondMap.put("key", "value");

		final Matches firstObject = getMatches(ONAP_NAME, CONFIG_NAME, firstMap);
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, secondMap);

		assertNotEquals(firstObject, secondObject);
		assertNotEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_NullConfigAttr_NotEqualsAndDifferentHashCode() throws Exception {
		final Map<String, String> secondMap = new HashMap<>();
		secondMap.put("key", "value");

		final Matches firstObject = getMatches(ONAP_NAME, CONFIG_NAME, null);
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, secondMap);

		assertNotEquals(firstObject, secondObject);
		assertNotEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	@Test
	public void testMatchesEqualsMethod_BothNullConfigAttr_Equals() throws Exception {

		final Matches firstObject = getMatches(ONAP_NAME, CONFIG_NAME, null);
		final Matches secondObject = getMatches(ONAP_NAME, CONFIG_NAME, null);

		assertEquals(firstObject, secondObject);
		assertEquals(firstObject.hashCode(), secondObject.hashCode());
	}

	private Matches getMatches(final String onapName, final String configName, final Map<String, String> attributes) {
		final Matches objUnderTest = new Matches();
		objUnderTest.setOnapName(onapName);
		objUnderTest.setConfigName(configName);
		objUnderTest.setConfigAttributes(attributes);
		return objUnderTest;
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args
	 *            the command line arguments
	 *
	 * @generatedBy CodePro at 6/1/16 1:41 PM
	 */
	public static void main(final String[] args) {
		new org.junit.runner.JUnitCore().run(MatchesTest.class);
	}
}
