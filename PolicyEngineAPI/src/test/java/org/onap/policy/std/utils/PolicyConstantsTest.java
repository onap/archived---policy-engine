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

package org.onap.policy.std.utils;

import static org.junit.Assert.assertTrue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.Test;

public class PolicyConstantsTest {
	@Test
	public void testConstructorIsPrivate1() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
	  Constructor<PolicyCommonConfigConstants> constructor = PolicyCommonConfigConstants.class.getDeclaredConstructor();
	  assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	  constructor.setAccessible(true);
	  constructor.newInstance();
	}

	@Test
	public void testConstructorIsPrivate2() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
	  Constructor<PolicyConfigConstants> constructor = PolicyConfigConstants.class.getDeclaredConstructor();
	  assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	  constructor.setAccessible(true);
	  constructor.newInstance();
	}
}
