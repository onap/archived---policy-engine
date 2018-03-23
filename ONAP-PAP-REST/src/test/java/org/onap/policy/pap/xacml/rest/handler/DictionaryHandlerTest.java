/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.handler;

import static org.junit.Assert.assertNull;
import org.junit.Test;

public class DictionaryHandlerTest {
  @Test
  public void negTestHandler() {
    // Set the system property temporarily
    String systemKey = "dictionary.impl.className";
    String oldProperty = System.getProperty(systemKey);
    System.setProperty(systemKey, "foobar");

    // Run negative test on instance
    assertNull(DictionaryHandler.getInstance());

    // Restore the original system property
    if (oldProperty != null) {
      System.setProperty(systemKey, oldProperty);
    } else {
      System.clearProperty(systemKey);
    }
  }
}
