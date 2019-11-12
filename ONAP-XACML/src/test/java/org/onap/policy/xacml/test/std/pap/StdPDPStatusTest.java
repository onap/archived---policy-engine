/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2019 Samsung
 * Modifications Copyright (C) 2019 AT&T Intellectual Property.
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

import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus.Status;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.onap.policy.xacml.std.pap.StdPDPPIPConfig;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPStatus;

public class StdPDPStatusTest {

    StdPDPStatus stdPDPStatus = new StdPDPStatus();

    @Test
    public void testSetAndGetStatus() {
        Status status = Status.UP_TO_DATE;
        stdPDPStatus.setStatus(status);
        assertEquals(status, stdPDPStatus.getStatus());
    }

    @Test
    public void testSetAndGetLoadErrors() {
        Set<String> errors = new HashSet<>();
        errors.add("An error");
        stdPDPStatus.setLoadErrors(errors);
        assertEquals(errors, stdPDPStatus.getLoadErrors());
    }

    @Test
    public void testSetAndGetLoadWarnings() {
        Set<String> warnings = new HashSet<>();
        warnings.add("An error");
        stdPDPStatus.setLoadWarnings(warnings);
        assertEquals(warnings, stdPDPStatus.getLoadWarnings());
    }

    @Test
    public void testSetAndGetLoadedPolicies() {
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        stdPDPStatus.setLoadedPolicies(loadedPolicies);
        assertEquals(loadedPolicies, stdPDPStatus.getLoadedPolicies());
    }

    @Test
    public void testSetAndGetLoadedRootPolicies() {
        Set<PDPPolicy> loadedRootPolicies = new HashSet<>();
        loadedRootPolicies.add(new StdPDPPolicy());
        stdPDPStatus.setLoadedRootPolicies(loadedRootPolicies);
        assertEquals(loadedRootPolicies, stdPDPStatus.getLoadedRootPolicies());
    }

    @Test
    public void testSetAndGetFailedPolicies() {
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        stdPDPStatus.setFailedPolicies(failedPolicies);
        assertEquals(failedPolicies, stdPDPStatus.getFailedPolicies());
    }

    @Test
    public void testSetAndGetLoadedPipConfigs() {
        Set<PDPPIPConfig> loadedPipConfigs = new HashSet<>();
        loadedPipConfigs.add(new StdPDPPIPConfig());
        stdPDPStatus.setLoadedPipConfigs(loadedPipConfigs);
        assertEquals(loadedPipConfigs, stdPDPStatus.getLoadedPipConfigs());
    }

    @Test
    public void testSetAndGetFailedPipConfigs() {
        Set<PDPPIPConfig> failedPipConfigs = new HashSet<>();
        failedPipConfigs.add(new StdPDPPIPConfig());
        stdPDPStatus.setFailedPipConfigs(failedPipConfigs);
        assertEquals(failedPipConfigs, stdPDPStatus.getFailedPipConfigs());
    }

    @Test
    public void testPoliciesOK() {
        assertTrue(stdPDPStatus.policiesOK());
        stdPDPStatus.addFailedPolicy(new StdPDPPolicy());
        assertFalse(stdPDPStatus.policiesOK());
    }

    @Test
    public void testPipConfigOK() {
        assertTrue(stdPDPStatus.pipConfigOK());
        stdPDPStatus.addFailedPipConfig(new StdPDPPIPConfig());
        assertFalse(stdPDPStatus.pipConfigOK());
    }

    @Test
    public void testIsOkFailedPolicy() {
        stdPDPStatus.setStatus(Status.UP_TO_DATE);
        assertTrue(stdPDPStatus.isOk());
        stdPDPStatus.addFailedPolicy(new StdPDPPolicy());
        assertFalse(stdPDPStatus.isOk());
    }

    @Test
    public void testIsOkFailedPipConfig() {
        stdPDPStatus.setStatus(Status.UP_TO_DATE);
        assertTrue(stdPDPStatus.isOk());
        stdPDPStatus.addFailedPipConfig(new StdPDPPIPConfig());
        assertFalse(stdPDPStatus.isOk());
    }

    @Test
    public void testIsOkStatusOutOfSync() {
        stdPDPStatus.setStatus(Status.UP_TO_DATE);
        assertTrue(stdPDPStatus.isOk());
        stdPDPStatus.setStatus(Status.OUT_OF_SYNCH);
        assertFalse(stdPDPStatus.isOk());
    }

    @Test
    public void testAddLoadError() {
        stdPDPStatus.addLoadError("An error");
        assertEquals("An error", stdPDPStatus.getLoadErrors().iterator().next());
    }

    @Test
    public void testAddLoadWarning() {
        stdPDPStatus.addLoadWarning("A warning");
        assertEquals("A warning", stdPDPStatus.getLoadWarnings().iterator().next());
    }

    @Test
    public void testAddLoadedPolicy() {
        PDPPolicy policy = new StdPDPPolicy();
        stdPDPStatus.addLoadedPolicy(policy);
        assertEquals(policy, stdPDPStatus.getLoadedPolicies().iterator().next());
    }

    @Test
    public void testAddRootPolicy() {
        PDPPolicy policy = new StdPDPPolicy();
        stdPDPStatus.addRootPolicy(policy);
        assertEquals(policy, stdPDPStatus.getLoadedRootPolicies().iterator().next());
    }

    @Test
    public void testAddAllLoadedRootPolicy() {
        Set<PDPPolicy> policies = new HashSet<>();
        PDPPolicy policy = new StdPDPPolicy();
        policies.add(policy);
        stdPDPStatus.addAllLoadedRootPolicies(policies);
        assertEquals(policies, stdPDPStatus.getLoadedRootPolicies());
    }

    @Test
    public void testAddLoadedPipConfig() {
        PDPPIPConfig pipConfig = new StdPDPPIPConfig();
        stdPDPStatus.addLoadedPipConfig(pipConfig);
        assertEquals(pipConfig, stdPDPStatus.getLoadedPipConfigs().iterator().next());
    }

    @Test
    public void testSet() {
        final Status status = Status.UP_TO_DATE;
        Set<PDPPIPConfig> failedPipConfigs = new HashSet<>();
        failedPipConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        Set<PDPPIPConfig> loadedPipConfigs = new HashSet<>();
        loadedPipConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        Set<PDPPolicy> loadedRootPolicies = new HashSet<>();
        loadedRootPolicies.add(new StdPDPPolicy());
        Set<String> loadErrors = new HashSet<>();
        loadErrors.add("An error");
        Set<String> loadWarnings = new HashSet<>();
        loadWarnings.add("An error");

        stdPDPStatus.setStatus(status);
        stdPDPStatus.setFailedPipConfigs(failedPipConfigs);
        stdPDPStatus.setFailedPolicies(failedPolicies);
        stdPDPStatus.setLoadedPipConfigs(loadedPipConfigs);
        stdPDPStatus.setLoadedPolicies(loadedPolicies);
        stdPDPStatus.setLoadedRootPolicies(loadedRootPolicies);
        stdPDPStatus.setLoadErrors(loadErrors);
        stdPDPStatus.setLoadWarnings(loadWarnings);

        StdPDPStatus stdPdpStatus1 = new StdPDPStatus();
        stdPdpStatus1.set(stdPDPStatus);

        assertEquals(status, stdPdpStatus1.getStatus());
        assertEquals(loadErrors, stdPdpStatus1.getLoadErrors());
        assertEquals(loadWarnings, stdPdpStatus1.getLoadWarnings());
        assertEquals(loadedPolicies, stdPdpStatus1.getLoadedPolicies());
        assertEquals(loadedRootPolicies, stdPdpStatus1.getLoadedRootPolicies());
        assertEquals(failedPolicies, stdPdpStatus1.getFailedPolicies());
        assertEquals(loadedPipConfigs, stdPdpStatus1.getLoadedPipConfigs());
        assertEquals(failedPipConfigs, stdPdpStatus1.getFailedPipConfigs());

        assertTrue(stdPdpStatus1.toString().startsWith("StdPDPStatus"));
    }

    @Test
    public void testEqualsAndHashCode() {
        final Status status = Status.UP_TO_DATE;
        Set<PDPPIPConfig> failedPipConfigs = new HashSet<>();
        failedPipConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        Set<PDPPIPConfig> loadedPipConfigs = new HashSet<>();
        loadedPipConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        Set<PDPPolicy> loadedRootPolicies = new HashSet<>();
        loadedRootPolicies.add(new StdPDPPolicy());
        Set<String> loadErrors = new HashSet<>();
        loadErrors.add("An error");
        Set<String> loadWarnings = new HashSet<>();
        loadWarnings.add("An error");

        StdPDPStatus stdPdpStatus1 = new StdPDPStatus();

        assertTrue(stdPDPStatus.equals(stdPdpStatus1));

        stdPDPStatus.setStatus(status);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setStatus(status);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setFailedPipConfigs(failedPipConfigs);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setFailedPipConfigs(failedPipConfigs);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setFailedPolicies(failedPolicies);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setFailedPolicies(failedPolicies);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setLoadedPipConfigs(loadedPipConfigs);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setLoadedPipConfigs(loadedPipConfigs);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setLoadedPolicies(loadedPolicies);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setLoadedPolicies(loadedPolicies);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setLoadedRootPolicies(loadedRootPolicies);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setLoadedRootPolicies(loadedRootPolicies);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setLoadErrors(loadErrors);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setLoadErrors(loadErrors);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());

        stdPDPStatus.setLoadWarnings(loadWarnings);
        assertFalse(stdPDPStatus.equals(stdPdpStatus1));
        stdPdpStatus1.setLoadWarnings(loadWarnings);
        assertTrue(stdPDPStatus.equals(stdPdpStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPdpStatus1.hashCode());
    }

    @Test
    public void testEqualsSameObject() {
        assertTrue(stdPDPStatus.equals(stdPDPStatus));
    }

    @Test
    public void testEqualsNull() {
        assertFalse(stdPDPStatus.equals(null));
    }

    @Test
    public void testEqualsInstanceOfDiffClass() {
        assertFalse(stdPDPStatus.equals(""));
    }
}
