/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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

package org.onap.policy.xacml.test.std.pap;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPItemSetChangeNotifier;
import org.onap.policy.xacml.std.pap.StdPDPItemSetChangeNotifier.StdItemSetChangeListener;

public class StdPDPItemSetChangeNotifierTest {
  @Test
  public void testNotifier() {
    StdPDPItemSetChangeNotifier notifier = new StdPDPItemSetChangeNotifier();
    StdItemSetChangeListener listener = Mockito.mock(StdItemSetChangeListener.class);
    notifier.addItemSetChangeListener(listener);
    notifier.removeItemSetChangeListener(listener);
    notifier.fireChanged();
    OnapPDP pdp = Mockito.mock(OnapPDP.class);
    notifier.firePDPChanged(pdp);
    OnapPDPGroup group = Mockito.mock(OnapPDPGroup.class);
    notifier.firePDPGroupChanged(group);
  }
}
