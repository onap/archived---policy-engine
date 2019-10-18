/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.att.research.xacml.util.XACMLProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PolicyTest {
    @Test
    public void testPolicy() {
        // Setup test data
        System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "xacml.pap.properties");
        PolicyRestAdapter adapter = Mockito.mock(PolicyRestAdapter.class);
        Policy policy = new ConfigPolicy(adapter);
        Path path = Paths.get("src/test/resources");

        // Test set and get
        policy.setFinalPolicyPath(path);
        assertEquals(path, policy.getFinalPolicyPath());

        // Test misc methods
        assertNotNull(policy.createMatch("testKey", "testVal"));
        assertNotNull(policy.createDynamicMatch("testKey", "testVal"));
        assertNotNull(policy.getNextFilename(path, "Config", "SampleTest1206", 1));
        assertNull(policy.getNextLoopFilename(path, "Config", "ClosedLoop_PM", "foo", 1));
        assertNull(policy.getNextLoopFilename(path, "Config", "ClosedLoop_Fault", "foo", 1));
        assertNull(policy.getNextLoopFilename(path, "Config", "Micro Service", "foo", 1));
        assertNull(policy.getNextLoopFilename(path, "Config", "Optimization", "foo", 1));

        // Test create
        Object policyData = null;
        assertEquals(0, policy.createPolicy(null, policyData).size());
        policyData = new PolicyType();
        assertEquals(1, policy.createPolicy(null, policyData).size());

        // Test remaining set and get
        assertNotNull(Policy.getConfigHome());
        assertEquals(true, policy.validateConfigForm());
        policy.setPreparedToSave(true);
        assertEquals(true, policy.isPreparedToSave());
        policy.setPolicyExists(true);
        assertEquals(true, policy.isPolicyExists());
    }
}
