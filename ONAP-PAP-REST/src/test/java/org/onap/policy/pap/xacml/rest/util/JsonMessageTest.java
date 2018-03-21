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

package org.onap.policy.pap.xacml.rest.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JsonMessageTest {
  @Test
  public void testSetAndGet() {
    String data = "testData";
    String data2 = "testData2";
    String data3 = "testData3";

    // Test constructors
    JsonMessage msg = new JsonMessage(data);
    assertEquals(msg.getData(), data);

    JsonMessage msg2 = new JsonMessage(data, data2);
    assertEquals(msg2.getData2(), data2);

    JsonMessage msg3 = new JsonMessage(data, data2, data3);
    assertEquals(msg3.getData3(), data3);

    // Test set and get
    msg.setData(data);
    msg.setData2(data2);
    msg.setData3(data3);
    assertEquals(msg.getData(), data);
    assertEquals(msg.getData2(), data2);
    assertEquals(msg.getData3(), data3);
  }
}
