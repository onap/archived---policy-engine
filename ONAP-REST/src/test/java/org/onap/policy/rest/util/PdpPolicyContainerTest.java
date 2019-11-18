/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPPolicy;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.rest.util.PdpPolicyContainer.PdpPolicyItem;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

public class PdpPolicyContainerTest {
    StdPDPGroup group;
    PdpPolicyContainer container;
    StdPDPPolicy policy;

    /**
     * Set test up.
     */
    @Before
    public void setUp() {
        group = new StdPDPGroup();
        group.setDefault(true);
        group.setDefaultGroup(true);
        group.setDescription("Test");
        group.setId("Test");
        group.setName("Test");
        group.setOnapPdps(new HashSet<>());
        group.setOperation("Test");
        group.setPipConfigs(new HashSet<>());
        policy = new StdPDPPolicy();
        policy.setName("Config_test.1.xml");
        policy.setId("Config_test");
        policy.setVersion("1.0");
        policy.setDescription("testDescription");

        HashSet<PDPPolicy> policies = new HashSet<>();
        policies.add(policy);
        group.setPolicies(policies);
        group.setSelectedPolicies(new HashSet<>());
        container = new PdpPolicyContainer(group);
    }

    @Test
    public void testPdpPolicyContainer() {
        container.nextItemId(policy);
        container.prevItemId(policy);
        container.firstItemId();
        container.lastItemId();
        container.isFirstId(policy);
        container.isLastId(policy);
        container.addItemAfter(policy);
        container.getContainerPropertyIds();
        container.getItemIds();
        container.getType("Id");
        assertTrue(String.class.equals(String.class));
        container.getType("Name");
        assertTrue(String.class.equals(String.class));
        container.getType("Version");
        assertTrue(String.class.equals(String.class));
        container.getType("Description");
        assertTrue(String.class.equals(String.class));
        container.getType("Root");
        assertTrue(Boolean.class.equals(Boolean.class));
        assertTrue(container.size() == 1);
        container.containsId(policy);
        container.removeItem(policy);
        container.addContainerProperty(null, null, null);
        container.removeContainerProperty(policy);
        container.removeAllItems();
        container.addItemAt(0);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor() {
        // Test PDP based constructor
        PDP pdp = new StdPDP();
        PdpPolicyContainer container1 = new PdpPolicyContainer(pdp);
        assertNotNull(container1);

        // Test set based constructor
        Set<PDPPolicy> set = new HashSet<PDPPolicy>();
        PdpPolicyContainer container2 = new PdpPolicyContainer(set);
        assertNotNull(container2);

        // Test object based constructor
        PdpPolicyContainer container3 = new PdpPolicyContainer("testObject");
        assertNotNull(container3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddItem() {
        container.addItem();
    }

    @Test
    public void testGetters() {
        assertNull(container.nextItemId("testItem"));
        assertNull(container.prevItemId("testItem"));
        assertNotNull(container.firstItemId());
        assertNotNull(container.lastItemId());
        assertEquals(container.indexOfId("testItem"), -1);
        assertNotNull(container.getIdByIndex(0));
        assertNotNull(container.getItemIds(0, 1));
    }

    @Test
    public void testPdpPolicyItem() {
        PdpPolicyItem item = container.new PdpPolicyItem(policy);
        assertEquals("Config_test", item.getId());
        assertEquals("Config_test.1.xml", item.getName());
        assertEquals("1.0", item.getVersion());
        assertEquals("testDescription", item.getDescription());
        item.setRoot(true);
        assertEquals(true, item.getRoot());
    }
}
