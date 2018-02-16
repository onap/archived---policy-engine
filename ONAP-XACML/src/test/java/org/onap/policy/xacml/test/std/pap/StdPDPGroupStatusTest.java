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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroupStatus;
import org.onap.policy.xacml.std.pap.StdPDPPIPConfig;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroupStatus.Status;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;

public class StdPDPGroupStatusTest {

    private static Logger logger = FlexLogger.getLogger(StdPDPGroupStatus.class);

    private StdPDPGroupStatus stdPDPGroupStatus;

    @Before
    public void setUp() {

        try {
            stdPDPGroupStatus = new StdPDPGroupStatus();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Test
    public void tesGgetStatus() {
        try {
            assertTrue(stdPDPGroupStatus.getStatus() != null);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Test
    public void testGetFailedPIPConfigs() {
        try {
            assertTrue(stdPDPGroupStatus.getFailedPIPConfigs() != null);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Test
    public void testGetUnknownPDPs() {
        try {
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

    @Test
    public void testPoliciesOK() {
        assertTrue(stdPDPGroupStatus.policiesOK());
        stdPDPGroupStatus.addFailedPolicy(new StdPDPPolicy());
        assertFalse(stdPDPGroupStatus.policiesOK());
    }

    @Test
    public void testPipConfigOK() {
        assertTrue(stdPDPGroupStatus.pipConfigOK());
        stdPDPGroupStatus.addFailedPipConfig(new StdPDPPIPConfig());
        assertFalse(stdPDPGroupStatus.pipConfigOK());
    }
    
    @Test
    public void testPdpsOKOutOfSyncPdp() {
        assertTrue(stdPDPGroupStatus.pdpsOK());
        stdPDPGroupStatus.addOutOfSynchPDP(new StdPDP());
        assertFalse(stdPDPGroupStatus.pdpsOK());
    }
    
    @Test
    public void testPdpsOKFailedPdp() {
        assertTrue(stdPDPGroupStatus.pdpsOK());
        stdPDPGroupStatus.addFailedPDP(new StdPDP());
        assertFalse(stdPDPGroupStatus.pdpsOK());
    }
    
    @Test
    public void testPdpsOKLastUpdateFailedPdp() {
        assertTrue(stdPDPGroupStatus.pdpsOK());
        stdPDPGroupStatus.addLastUpdateFailedPDP(new StdPDP());
        assertFalse(stdPDPGroupStatus.pdpsOK());
    }
    
    @Test
    public void testPdpsOKUnknownPdp() {
        assertTrue(stdPDPGroupStatus.pdpsOK());
        stdPDPGroupStatus.addUnknownPDP(new StdPDP());
        assertFalse(stdPDPGroupStatus.pdpsOK());
    }
    
    @Test
    public void testIsGroupOkFailedPolicy() {
        stdPDPGroupStatus.setStatus(Status.OK);
        assertTrue(stdPDPGroupStatus.isGroupOk());
        stdPDPGroupStatus.addFailedPolicy(new StdPDPPolicy());
        assertFalse(stdPDPGroupStatus.isGroupOk());
    }
    
    @Test
    public void testIsGroupOkFailedPipConfig() {
        stdPDPGroupStatus.setStatus(Status.OK);
        assertTrue(stdPDPGroupStatus.isGroupOk());
        stdPDPGroupStatus.addFailedPipConfig(new StdPDPPIPConfig());
        assertFalse(stdPDPGroupStatus.isGroupOk());
    }
    
    @Test
    public void testIsGroupOkFailedPdp() {
        stdPDPGroupStatus.setStatus(Status.OK);
        assertTrue(stdPDPGroupStatus.isGroupOk());
        stdPDPGroupStatus.addLastUpdateFailedPDP(new StdPDP());
        assertFalse(stdPDPGroupStatus.isGroupOk());
    }  
    
    @Test
    public void testIsGroupOkLoadErrors() {
        stdPDPGroupStatus.setStatus(Status.OK);
        assertTrue(stdPDPGroupStatus.isGroupOk());
        stdPDPGroupStatus.addLoadError("A load error");
        assertFalse(stdPDPGroupStatus.isGroupOk());
    }  
    
    @Test
    public void testIsGroupOkStatusOutOfSynch() {
        stdPDPGroupStatus.setStatus(Status.OK);
        assertTrue(stdPDPGroupStatus.isGroupOk());
        stdPDPGroupStatus.setStatus(Status.OUT_OF_SYNCH);
        assertFalse(stdPDPGroupStatus.isGroupOk());
    }  

    @Test
    public void testConstructor() {
        Status status = Status.OK;
        Set<PDP> failedPDPs = new HashSet<>();
        failedPDPs.add(new StdPDP());
        Set<PDPPIPConfig> failedPIPConfigs = new HashSet<>();
        failedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        Set<PDP> inSynchPDPs = new HashSet<>();
        inSynchPDPs.add(new StdPDP());
        Set<PDP> lastUpdateFailedPDPs = new HashSet<>();
        lastUpdateFailedPDPs.add(new StdPDP());
        Set<PDPPIPConfig> loadedPIPConfigs = new HashSet<>();
        loadedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        Set<String> loadErrors = new HashSet<>();
        loadErrors.add("An error");
        Set<String> loadWarnings = new HashSet<>();
        loadWarnings.add("An error");
        Set<PDP> outOfSynchPDPs = new HashSet<>();
        outOfSynchPDPs.add(new StdPDP());
        Set<PDP> unknownPDPs = new HashSet<>();
        unknownPDPs.add(new StdPDP());
        Set<PDP> updatingPDPs = new HashSet<>();
        updatingPDPs.add(new StdPDP());


        stdPDPGroupStatus.setStatus(status);
        stdPDPGroupStatus.setFailedPDPs(failedPDPs);
        stdPDPGroupStatus.setFailedPIPConfigs(failedPIPConfigs);
        stdPDPGroupStatus.setFailedPolicies(failedPolicies);
        stdPDPGroupStatus.setInSynchPDPs(inSynchPDPs);
        stdPDPGroupStatus.setLastUpdateFailedPDPs(lastUpdateFailedPDPs);
        stdPDPGroupStatus.setLoadedPIPConfigs(loadedPIPConfigs);
        stdPDPGroupStatus.setLoadedPolicies(loadedPolicies);
        stdPDPGroupStatus.setLoadErrors(loadErrors);
        stdPDPGroupStatus.setLoadWarnings(loadWarnings);
        stdPDPGroupStatus.setOutOfSynchPDPs(outOfSynchPDPs);
        stdPDPGroupStatus.setUnknownPDPs(unknownPDPs);
        stdPDPGroupStatus.setUpdatingPDPs(updatingPDPs);

        StdPDPGroupStatus stdPDPGroupStatus2 = new StdPDPGroupStatus(stdPDPGroupStatus);

        assertEquals(status, stdPDPGroupStatus2.getStatus());
        assertEquals(failedPDPs, stdPDPGroupStatus2.getFailedPDPs());
        assertEquals(failedPIPConfigs, stdPDPGroupStatus2.getFailedPIPConfigs());
        assertEquals(failedPolicies, stdPDPGroupStatus2.getFailedPolicies());
        assertEquals(inSynchPDPs, stdPDPGroupStatus2.getInSynchPDPs());
        assertEquals(lastUpdateFailedPDPs, stdPDPGroupStatus2.getLastUpdateFailedPDPs());
        assertEquals(loadedPIPConfigs, stdPDPGroupStatus2.getLoadedPIPConfigs());
        assertEquals(loadedPolicies, stdPDPGroupStatus2.getLoadedPolicies());
        assertEquals(loadErrors, stdPDPGroupStatus2.getLoadErrors());
        assertEquals(loadWarnings, stdPDPGroupStatus2.getLoadWarnings());
        assertEquals(outOfSynchPDPs, stdPDPGroupStatus2.getOutOfSynchPDPs());
        assertEquals(unknownPDPs, stdPDPGroupStatus2.getUnknownPDPs());
        assertEquals(updatingPDPs, stdPDPGroupStatus2.getUpdatingPDPs());
    }

    @Test
    public void testEqualsAndHashCode() {
        Status status = Status.OK;
        Set<PDP> failedPDPs = new HashSet<>();
        failedPDPs.add(new StdPDP());
        Set<PDPPIPConfig> failedPIPConfigs = new HashSet<>();
        failedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        Set<PDP> inSynchPDPs = new HashSet<>();
        inSynchPDPs.add(new StdPDP());
        Set<PDP> lastUpdateFailedPDPs = new HashSet<>();
        lastUpdateFailedPDPs.add(new StdPDP());
        Set<PDPPIPConfig> loadedPIPConfigs = new HashSet<>();
        loadedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        Set<String> loadErrors = new HashSet<>();
        loadErrors.add("An error");
        Set<String> loadWarnings = new HashSet<>();
        loadWarnings.add("An error");
        Set<PDP> outOfSynchPDPs = new HashSet<>();
        outOfSynchPDPs.add(new StdPDP());
        Set<PDP> unknownPDPs = new HashSet<>();
        unknownPDPs.add(new StdPDP());
        Set<PDP> updatingPDPs = new HashSet<>();
        updatingPDPs.add(new StdPDP());

        StdPDPGroupStatus stdPDPGroupStatus1 = new StdPDPGroupStatus();

        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));

        stdPDPGroupStatus.setStatus(status);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setStatus(status);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setFailedPDPs(failedPDPs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setFailedPDPs(failedPDPs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setFailedPIPConfigs(failedPIPConfigs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setFailedPIPConfigs(failedPIPConfigs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setFailedPolicies(failedPolicies);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setFailedPolicies(failedPolicies);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setInSynchPDPs(inSynchPDPs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setInSynchPDPs(inSynchPDPs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setLastUpdateFailedPDPs(lastUpdateFailedPDPs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setLastUpdateFailedPDPs(lastUpdateFailedPDPs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setLoadedPIPConfigs(loadedPIPConfigs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setLoadedPIPConfigs(loadedPIPConfigs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setLoadedPolicies(loadedPolicies);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setLoadedPolicies(loadedPolicies);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setLoadErrors(loadErrors);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setLoadErrors(loadErrors);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setLoadWarnings(loadWarnings);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setLoadWarnings(loadWarnings);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setOutOfSynchPDPs(outOfSynchPDPs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setOutOfSynchPDPs(outOfSynchPDPs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setUnknownPDPs(unknownPDPs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setUnknownPDPs(unknownPDPs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());

        stdPDPGroupStatus.setUpdatingPDPs(updatingPDPs);
        assertFalse(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        stdPDPGroupStatus1.setUpdatingPDPs(updatingPDPs);
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus1));
        assertEquals(stdPDPGroupStatus.hashCode(), stdPDPGroupStatus1.hashCode());
    }

    @Test
    public void testEqualsSameObject() {
        assertTrue(stdPDPGroupStatus.equals(stdPDPGroupStatus));
    }

    @Test
    public void testEqualsNull() {
        assertFalse(stdPDPGroupStatus.equals(null));
    }

    @Test
    public void testEqualsInstanceOfDiffClass() {
        assertFalse(stdPDPGroupStatus.equals(""));
    }

}
