package org.onap.policy.xacml.test.std.pap;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroupStatus;
import org.onap.policy.xacml.std.pap.StdPDPPIPConfig;

import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPGroupStatus.Status;

public class StdPDPGroupStatusTest {

	private static Logger logger = FlexLogger.getLogger(StdPDPGroupStatus.class);
	
	private StdPDPGroupStatus stdPDPGroupStatus;
	
	@Before
	public void setUp(){
	
		try {
			stdPDPGroupStatus = new StdPDPGroupStatus();
		} catch (Exception e) {
			logger.error(e);
		} 
	}

	@Test
	public void tesGgetStatus() {
		try {
			stdPDPGroupStatus.setStatus(Status.UNKNOWN);
			assertTrue(stdPDPGroupStatus.getStatus() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetFailedPIPConfigs() {
		try {
			stdPDPGroupStatus.setFailedPIPConfigs(new HashSet<PDPPIPConfig>());
			assertTrue(stdPDPGroupStatus.getFailedPIPConfigs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetUnknownPDPs() {
		try {
			stdPDPGroupStatus.setUnknownPDPs(new HashSet<PDP>());
			assertTrue(stdPDPGroupStatus.getUnknownPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetLoadErrors() {
		try {
			stdPDPGroupStatus.setLoadErrors(new HashSet<String>());
			assertTrue(stdPDPGroupStatus.getLoadErrors() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetLoadWarnings() {
		try {
			stdPDPGroupStatus.setLoadWarnings(new HashSet<>());
			assertTrue(stdPDPGroupStatus.getLoadWarnings() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetLoadedPolicies() {
		try {
			stdPDPGroupStatus.setLoadedPolicies(new HashSet<>());
			assertTrue(stdPDPGroupStatus.getLoadedPolicies() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetFailedPolicies() {
		try {
			stdPDPGroupStatus.setFailedPolicies(new HashSet<>());
			assertTrue(stdPDPGroupStatus.getFailedPolicies() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetLoadedPipConfigs() {
		try {
			stdPDPGroupStatus.addLoadedPipConfig(new StdPDPPIPConfig());
			assertTrue(stdPDPGroupStatus.getLoadedPipConfigs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetFailedPipConfigs() {
		try {
			stdPDPGroupStatus.addFailedPipConfig(new StdPDPPIPConfig());
			assertTrue(stdPDPGroupStatus.getFailedPipConfigs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetInSynchPDPs() {
		try {
			stdPDPGroupStatus.addInSynchPDP(new StdPDP());
			assertTrue(stdPDPGroupStatus.getInSynchPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetOutOfSynchPDPs() {
		try {
			stdPDPGroupStatus.addOutOfSynchPDP(new StdPDP());
			assertTrue(stdPDPGroupStatus.getOutOfSynchPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetFailedPDPs() {
		try {
			stdPDPGroupStatus.addFailedPDP(new StdPDP());
			assertTrue(stdPDPGroupStatus.getFailedPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetUpdatingPDPs() {
		try {
			stdPDPGroupStatus.addUpdatingPDP(new StdPDP());
			assertTrue(stdPDPGroupStatus.getUpdatingPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetLastUpdateFailedPDPs() {
		try {
			stdPDPGroupStatus.addLastUpdateFailedPDP(new StdPDP());
			assertTrue(stdPDPGroupStatus.getLastUpdateFailedPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetUnknownStatusPDPs() {
		try {
			stdPDPGroupStatus.addUnknownPDP(new StdPDP());
			assertTrue(stdPDPGroupStatus.getUnknownStatusPDPs() != null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testIsGroupOk() {
		try {
			stdPDPGroupStatus.policiesOK();
			assertTrue(stdPDPGroupStatus.isGroupOk() == false);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
