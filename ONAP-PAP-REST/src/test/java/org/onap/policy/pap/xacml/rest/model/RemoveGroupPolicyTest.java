/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.model;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import com.att.research.xacml.api.pap.PDPPolicy;

public class RemoveGroupPolicyTest {
    @Test
    public void testRemove() {
        // Test constructor
        StdPDPGroup group = new StdPDPGroup();
        RemoveGroupPolicy remove = new RemoveGroupPolicy(group);
        assertEquals(remove.isRemoved(), false);
        assertEquals(remove.getUpdatedObject(), null);

        // Test remove
        PDPPolicy policy = new StdPDPPolicy();
        remove.prepareToRemove(policy);
        remove.doSave();
        assertEquals(remove.isRemoved(), true);
    }
}
