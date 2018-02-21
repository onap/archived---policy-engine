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
package org.onap.policy.pap.xacml.rest.controller;

import static org.junit.Assert.assertTrue;
import org.onap.policy.pap.xacml.rest.controller.DictionaryImportController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class DictionaryImportControllerTest {
	
	private static Logger logger = FlexLogger.getLogger(DictionaryImportController.class);

	@Before
	public void setUp() throws Exception {
		logger.info("setUp: Entering");
	}
	
	@Test
	public void testIsValidDictionaryName(){
		DictionaryImportController cotroller = new DictionaryImportController();
		//test invalid name
		assertTrue(!cotroller.isValidDictionaryName("wrong-name"));
		//test valid name
		assertTrue(cotroller.isValidDictionaryName("ActionList"));
	}
	
	@After
	 public void destroy(){

	 }
}
