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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;

public class StdPDPGroupTest {


	private static Logger logger = FlexLogger.getLogger(StdPDPGroupTest.class);
	
	private StdPDPGroup stdPDPGroup;
	private Path repository;
	private StdPDP testPdp = new StdPDP();
	private StdPDP testPdp1 = new StdPDP();
	
	@Before
	public void setUp(){
	
		try {
			stdPDPGroup = new StdPDPGroup();
			repository = Paths.get("src/test/resources/pdps");
			testPdp1.setId("100");
			
		} catch (Exception e) {
			logger.error(e);
		} 
	}


	@Test
	public void testGetStatus() {
		try {
			assertTrue(stdPDPGroup.getStatus() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetId() {
		try {
			stdPDPGroup.setId("testId");
			assertTrue(stdPDPGroup.getId() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testIsDefaultGroup() {
		try {
			stdPDPGroup.setDefaultGroup(true);
			assertTrue(stdPDPGroup.isDefaultGroup() == true);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetName() {
		try {
			stdPDPGroup.setName("testing");
			assertTrue(stdPDPGroup.getName() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetDescription() {
		try {
			stdPDPGroup.setDescription("description");
			assertTrue(stdPDPGroup.getDescription() != null);
		} catch (Exception e) {
			logger.error(e);
		}	
	}

	@Test
	public void testGetDirectory() {
		try {
			stdPDPGroup.setDirectory(repository);
			assertTrue(stdPDPGroup.getDirectory() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetSelectedPolicies() {
		try {
			assertTrue(stdPDPGroup.getSelectedPolicies() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetOperation() {
		try {
			stdPDPGroup.setOperation("test");
			assertTrue(stdPDPGroup.getOperation() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPdps() {
		try {
			stdPDPGroup.setOnapPdps(new HashSet<>());
			assertTrue(stdPDPGroup.getPdps() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetOnapPdps() {
		try {
			stdPDPGroup.setOnapPdps(new HashSet<>());
			assertTrue(stdPDPGroup.getOnapPdps() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testAddPDP() {
		try {
			assertTrue(stdPDPGroup.addPDP(testPdp) == true);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testRemovePDP() {
		try {
			assertTrue(stdPDPGroup.removePDP(testPdp) == false);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPolicies() {
		try {
			stdPDPGroup.setPolicies(new HashSet());
			assertTrue(stdPDPGroup.getPolicies() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPolicy() {
		try {
			assertTrue(stdPDPGroup.getPolicy("wrongId") == null);
		} catch (Exception e) {
			logger.error(e);
		}
	}


	@Test
	public void testGetPipConfigs() {
		try {
			stdPDPGroup.setPipConfigs(new HashSet());
			assertTrue(stdPDPGroup.getPipConfigs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPipConfig() {
		try {
			Set pipConfigs = new HashSet();			
			StdPDP testPdp = new StdPDP();
			testPdp.setId("20");
			pipConfigs.add(testPdp);			
			stdPDPGroup.setId("testId");
			assertTrue(stdPDPGroup.getPipConfig("222") == null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPipConfigProperties() {
		try {
			assertTrue(stdPDPGroup.getPipConfigProperties() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
