/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

package org.onap.policy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class RolesTest {
    private Roles role = new Roles();

    @Test
    public void testGetId() {
        role.setId(7);
        assertEquals(role.getId(), 7);
    }

    @Test
    public void testGetLoginId() {
        role.setLoginId("testLoginId");
        assertNotNull(role.getLoginId());
        assertEquals(role.getLoginId(), "testLoginId");
    }

    @Test
    public void testGetScope() {
        role.setScope("testScope");
        assertNotNull(role.getScope());
        assertEquals(role.getScope(), "testScope");
    }

    @Test
    public void testGetName() {
        role.setName("testName");
        assertNotNull(role.getName());
        assertEquals(role.getName(), "testName");
    }

    @Test
    public void testGetRole() {
        role.setRole("testRole");
        assertNotNull(role.getRole());
        assertEquals(role.getRole(), "testRole");
    }
}
