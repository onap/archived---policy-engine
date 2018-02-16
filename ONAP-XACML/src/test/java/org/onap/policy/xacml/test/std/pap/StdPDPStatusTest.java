/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
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
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.onap.policy.xacml.std.pap.StdPDPPIPConfig;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPStatus;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus.Status;

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
    public void testAddLoadedPipConfig(){
        PDPPIPConfig pipConfig = new StdPDPPIPConfig();
        stdPDPStatus.addLoadedPipConfig(pipConfig);
        assertEquals(pipConfig, stdPDPStatus.getLoadedPipConfigs().iterator().next());
    }

    @Test
    public void testSet() {
        Status status = Status.UP_TO_DATE;
        Set<PDPPIPConfig> failedPIPConfigs = new HashSet<>();
        failedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        Set<PDPPIPConfig> loadedPIPConfigs = new HashSet<>();
        loadedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        Set<PDPPolicy> loadedRootPolicies = new HashSet<>();
        loadedRootPolicies.add(new StdPDPPolicy());
        Set<String> loadErrors = new HashSet<>();
        loadErrors.add("An error");
        Set<String> loadWarnings = new HashSet<>();
        loadWarnings.add("An error");

        stdPDPStatus.setStatus(status);
        stdPDPStatus.setFailedPipConfigs(failedPIPConfigs);
        stdPDPStatus.setFailedPolicies(failedPolicies);
        stdPDPStatus.setLoadedPipConfigs(loadedPIPConfigs);
        stdPDPStatus.setLoadedPolicies(loadedPolicies);
        stdPDPStatus.setLoadedRootPolicies(loadedRootPolicies);
        stdPDPStatus.setLoadErrors(loadErrors);
        stdPDPStatus.setLoadWarnings(loadWarnings);

        StdPDPStatus stdPDPStatus1 = new StdPDPStatus();
        stdPDPStatus1.set(stdPDPStatus);

        assertEquals(status, stdPDPStatus1.getStatus());
        assertEquals(loadErrors, stdPDPStatus1.getLoadErrors());
        assertEquals(loadWarnings, stdPDPStatus1.getLoadWarnings());
        assertEquals(loadedPolicies, stdPDPStatus1.getLoadedPolicies());
        assertEquals(loadedRootPolicies, stdPDPStatus1.getLoadedRootPolicies());
        assertEquals(failedPolicies, stdPDPStatus1.getFailedPolicies());
        assertEquals(loadedPIPConfigs, stdPDPStatus1.getLoadedPipConfigs());
        assertEquals(failedPIPConfigs, stdPDPStatus1.getFailedPipConfigs());

        assertEquals("StdPDPStatus [status=UP_TO_DATE, loadErrors=[An error], loadWarnings=[An error], loadedPolicies=[StdPDPPolicy " 
                        + "[id=null, name=null, policyId=null, description=null, version=, isRoot=false, isValid=false, location=null]], "
                        + "loadedRootPolicies=[StdPDPPolicy [id=null, name=null, policyId=null, description=null, version=, isRoot=false, "
                        + "isValid=false, location=null]], failedPolicies=[StdPDPPolicy [id=null, name=null, policyId=null, description=null, "
                        + "version=, isRoot=false, isValid=false, location=null]], loadedPIPConfigs=[StdPDPPIPConfig [id=null, name=null, "
                        + "description=null, classname=null, config={}]], failedPIPConfigs=[StdPDPPIPConfig [id=null, name=null, description=null, classname=null, config={}]]]",
                stdPDPStatus1.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        Status status = Status.UP_TO_DATE;
        Set<PDPPIPConfig> failedPIPConfigs = new HashSet<>();
        failedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> failedPolicies = new HashSet<>();
        failedPolicies.add(new StdPDPPolicy());
        Set<PDPPIPConfig> loadedPIPConfigs = new HashSet<>();
        loadedPIPConfigs.add(new StdPDPPIPConfig());
        Set<PDPPolicy> loadedPolicies = new HashSet<>();
        loadedPolicies.add(new StdPDPPolicy());
        Set<PDPPolicy> loadedRootPolicies = new HashSet<>();
        loadedRootPolicies.add(new StdPDPPolicy());
        Set<String> loadErrors = new HashSet<>();
        loadErrors.add("An error");
        Set<String> loadWarnings = new HashSet<>();
        loadWarnings.add("An error");

        StdPDPStatus stdPDPStatus1 = new StdPDPStatus();

        assertTrue(stdPDPStatus.equals(stdPDPStatus1));

        stdPDPStatus.setStatus(status);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setStatus(status);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setFailedPipConfigs(failedPIPConfigs);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setFailedPipConfigs(failedPIPConfigs);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setFailedPolicies(failedPolicies);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setFailedPolicies(failedPolicies);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setLoadedPipConfigs(loadedPIPConfigs);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setLoadedPipConfigs(loadedPIPConfigs);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setLoadedPolicies(loadedPolicies);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setLoadedPolicies(loadedPolicies);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setLoadedRootPolicies(loadedRootPolicies);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setLoadedRootPolicies(loadedRootPolicies);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setLoadErrors(loadErrors);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setLoadErrors(loadErrors);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());

        stdPDPStatus.setLoadWarnings(loadWarnings);
        assertFalse(stdPDPStatus.equals(stdPDPStatus1));
        stdPDPStatus1.setLoadWarnings(loadWarnings);
        assertTrue(stdPDPStatus.equals(stdPDPStatus1));
        assertEquals(stdPDPStatus.hashCode(), stdPDPStatus1.hashCode());
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
