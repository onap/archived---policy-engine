/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
package org.onap.policy.xacml.test.std.pap;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.std.pap.StdEngine;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.std.pap.StdPDP;

public class StdEngineTest {

	private static Logger logger = FlexLogger.getLogger(StdEngineTest.class);
	private Path repository;
	Properties properties = new Properties();
	StdEngine stdEngine = null;

	@Before
	public void setUp(){

		repository = Paths.get("src/test/resources/pdps");
		try {
			stdEngine = new StdEngine(repository);
		} catch (PAPException e) {
			logger.info(e);
		} catch (IOException e) {
			logger.info(e);
		}
	}

	@Test
	public void testGetDefaultGroup(){
		try {
			assertTrue(stdEngine.getDefaultGroup() != null);
		} catch (PAPException e) {
			logger.info(e);
		}
	}
	@Test
	public void testGetGroup(){
		try {
			assertTrue(stdEngine.getGroup("1") == null);
		} catch (PAPException e) {
			logger.info(e);
		}

	}

	@Test
	public void testGetOnapPDPGroups(){
		try {
			assertTrue(stdEngine.getOnapPDPGroups() != null);
		} catch (PAPException e) {
			logger.info(e);
		}
	}
	@Test
	public void testGetPDP(){
		try {
			assertTrue(stdEngine.getPDP("1") == null);
		} catch (PAPException e) {
			logger.info(e);
		}
	}
	@Test
	public void testGetPDPGroup(){
		try {
			assertTrue(stdEngine.getPDPGroup(null) == null);
		} catch (PAPException e) {
			logger.info(e);
		}
	}
}
