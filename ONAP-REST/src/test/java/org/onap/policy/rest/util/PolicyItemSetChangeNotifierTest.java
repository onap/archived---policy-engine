/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeEvent;
import org.onap.policy.rest.util.PolicyContainer.ItemSetChangeListener;

public class PolicyItemSetChangeNotifierTest {
  @Test
  public void testNotifier() {
    // Setup test data
    ItemSetChangeListener listener = Mockito.mock(ItemSetChangeListener.class);
    ItemSetChangeEvent event = Mockito.mock(ItemSetChangeEvent.class);

    // Test constructor
    PolicyItemSetChangeNotifier notifier = new PolicyItemSetChangeNotifier();
    assertNotNull(notifier);

    // Test listener methods
    try {
      notifier.addItemSetChangeListener(listener);
      notifier.fireItemSetChange(event);
      notifier.removeItemSetChangeListener(listener);
    } catch (Exception ex) {
      fail("Not expecting any exceptions: " + ex);
    }
  }
}
