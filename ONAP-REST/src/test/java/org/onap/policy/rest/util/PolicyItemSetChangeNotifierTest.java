/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeEvent;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeListener;

public class PolicyItemSetChangeNotifierTest {
    @Test
    public void testNotifier() {

        // Test constructor
        PolicyItemSetChangeNotifier notifier = new PolicyItemSetChangeNotifier();
        assertNotNull(notifier);

        assertEquals(null, notifier.getItemSetChangeListeners());

        ItemSetChangeListener listener1 = Mockito.mock(ItemSetChangeListener.class);
        notifier.addItemSetChangeListener(listener1);
        assertEquals(1, notifier.getItemSetChangeListeners().size());
        ItemSetChangeListener listener2 = Mockito.mock(ItemSetChangeListener.class);
        notifier.addItemSetChangeListener(listener2);
        assertEquals(2, notifier.getItemSetChangeListeners().size());

        notifier.removeItemSetChangeListener(listener1);
        assertEquals(1, notifier.getItemSetChangeListeners().size());
        notifier.addItemSetChangeListener(listener1);
        assertEquals(2, notifier.getItemSetChangeListeners().size());

        ItemSetChangeEvent event = Mockito.mock(ItemSetChangeEvent.class);
        notifier.fireItemSetChange(event);

        notifier.removeItemSetChangeListener(listener1);
        assertEquals(1, notifier.getItemSetChangeListeners().size());
        notifier.removeItemSetChangeListener(listener2);
        assertEquals(0, notifier.getItemSetChangeListeners().size());
        notifier.removeItemSetChangeListener(listener2);
        assertEquals(0, notifier.getItemSetChangeListeners().size());

        notifier.setItemSetChangeListeners(null);
        notifier.removeItemSetChangeListener(listener2);
        assertEquals(null, notifier.getItemSetChangeListeners());

        assertThatCode(() -> notifier.fireItemSetChange(event)).doesNotThrowAnyException();

        notifier.setContainer(new DummyPolicyContainer());
        assertThatCode(() -> notifier.fireItemSetChange()).doesNotThrowAnyException();
    }
}
