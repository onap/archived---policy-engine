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

package org.onap.policy.pap.xacml.rest.elk;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyBodyType;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyType;

public class ElkConnectorTest {
    @Test
    public void testConnector() {
        PolicyType type = PolicyType.Config;
        PolicyBodyType body = PolicyBodyType.json;
        assertNotNull(type);
        assertNotNull(body);

        assertEquals(PolicyIndexType.closedloop, ElkConnector.toPolicyIndexType("Config_Fault"));
        assertEquals(PolicyIndexType.closedloop, ElkConnector.toPolicyIndexType("Config_PM"));
        assertEquals(PolicyIndexType.config, ElkConnector.toPolicyIndexType("Config_FW"));
        assertEquals(PolicyIndexType.config, ElkConnector.toPolicyIndexType("Config_MS"));
        assertEquals(PolicyIndexType.config, ElkConnector.toPolicyIndexType("Config_OOF"));
        assertEquals(PolicyIndexType.action, ElkConnector.toPolicyIndexType("Action"));
        assertEquals(PolicyIndexType.decision, ElkConnector.toPolicyIndexType("Decision"));
        assertEquals(PolicyIndexType.config, ElkConnector.toPolicyIndexType("Config"));

        assertThatIllegalArgumentException().isThrownBy(() -> ElkConnector.toPolicyIndexType(null));

        assertThatIllegalArgumentException().isThrownBy(() -> ElkConnector.toPolicyIndexType("Foo"));
    }
}
