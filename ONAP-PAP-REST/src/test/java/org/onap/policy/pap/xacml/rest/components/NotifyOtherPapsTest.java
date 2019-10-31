/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatCode;

import com.att.research.xacml.util.XACMLProperties;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.rest.jpa.PolicyDBDaoEntity;

public class NotifyOtherPapsTest {
    private static final String systemKey = XACMLProperties.XACML_PROPERTIES_NAME;
    private static final String oldProperty = System.getProperty(systemKey);

    @Before
    public void setup() {
        // Set the system property temporarily
        System.setProperty(systemKey, "xacml.pap.properties");
    }

    @Test
    public void negTestNotify() {
        NotifyOtherPaps notify = new NotifyOtherPaps();
        List<PolicyDBDaoEntity> otherServers = new ArrayList<PolicyDBDaoEntity>();
        PolicyDBDaoEntity dbdaoEntity = new PolicyDBDaoEntity();
        dbdaoEntity.setPolicyDBDaoUrl("http://test");
        otherServers.add(dbdaoEntity);
        long entityId = 0;
        String entityType = "entityType";
        String newGroupId = "newGroupId";
        assertThatCode(() -> notify.startNotifyThreads(otherServers, entityId, entityType, newGroupId))
            .doesNotThrowAnyException();
    }

    @After
    public void cleanup() {
        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(systemKey, oldProperty);
        } else {
            System.clearProperty(systemKey);
        }
    }
}
