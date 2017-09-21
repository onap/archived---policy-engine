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

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroupStatus;

import com.att.research.xacml.api.pap.PDPGroupStatus.Status;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;

public class StdPDPGroupTest {


	private static Logger logger = FlexLogger.getLogger(StdPDPGroupTest.class);
	
	private StdPDPGroup stdPDPGroup;
	private Path repository;
	private StdPDP testPdp = new StdPDP();
	private StdPDP testPdp1 = new StdPDP();

	HashSet<PDPPolicy> temp = new HashSet<PDPPolicy>();

	
	@Before
	public void setUp(){
	
		try {
			stdPDPGroup = new StdPDPGroup();
			repository = Paths.get("src/test/resources/pdps");
			testPdp1.setId("100");
			temp.add((PDPPolicy) testPdp1);
			stdPDPGroup.setPolicies(temp);
			
		} catch (Exception e) {
			logger.error(e);
		} 
	}


	@Test
	public void testGetStatus() {
		try {
			stdPDPGroup.setStatus(new StdPDPGroupStatus(Status.UNKNOWN));
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
			stdPDPGroup.setSelectedPolicies(new HashSet<PDPPolicy>());
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
			stdPDPGroup.setPolicies(new HashSet<PDPPolicy>());
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
	public void testRemovePolicyFromGroup() {
		try {
			stdPDPGroup.setId("testId");
			assertTrue(stdPDPGroup.removePolicyFromGroup((PDPPolicy) testPdp1) == true);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testRemovePolicy() {
		try {
			assertTrue(stdPDPGroup.removePolicy((PDPPolicy) testPdp1) == false);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPipConfigs() {
		try {
			stdPDPGroup.setPipConfigs(new HashSet<PDPPIPConfig>());
			assertTrue(stdPDPGroup.getPipConfigs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetPipConfig() {
		try {
			Set<PDPPIPConfig> pipConfigs = new HashSet<PDPPIPConfig>();			
			StdPDP testPdp = new StdPDP();
			testPdp.setId("20");
			pipConfigs.add((PDPPIPConfig) testPdp);			
			stdPDPGroup.setId("testId");
			assertTrue(stdPDPGroup.getPipConfig("20") != null);
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
