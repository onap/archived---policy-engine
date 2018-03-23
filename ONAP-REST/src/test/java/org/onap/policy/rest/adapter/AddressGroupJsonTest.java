/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.rest.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class AddressGroupJsonTest {
  @Test
  public void testJson() {
    // Setup test data
    String value = "testVal";
    String value2 = "testVal2";

    // Test constructors
    AddressGroupJson json = new AddressGroupJson();
    json.setName(value);
    AddressGroupJson json2 = new AddressGroupJson();
    json2.setName(value);
    AddressGroupJson json3 = new AddressGroupJson();
    json3.setName(value2);

    // Test equals and hash functions
    assertTrue(json.equals(json2));
    assertFalse(json.equals(json3));
    assertFalse(json.equals(null));
    assertFalse(json.equals(value));
    assertEquals(217, json.hashCode());
  }
}
