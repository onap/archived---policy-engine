/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.jpa;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MicroserviceHeaderdeFaultsTest {
  @Test
  public void testHeader() {
    // Set up test data
    String value = "testVal";
    MicroserviceHeaderdeFaults header = new MicroserviceHeaderdeFaults();
    header.prePersist();
    header.preUpdate();

    // Set data
    header.setGuard(value);
    header.setId(1);
    header.setModelName(value);
    header.setOnapName(value);
    header.setPriority(value);
    header.setRiskLevel(value);
    header.setRiskType(value);

    // Test gets
    assertEquals(value, header.getGuard());
    assertEquals(1, header.getId());
    assertEquals(value, header.getModelName());
    assertEquals(value, header.getOnapName());
    assertEquals(value, header.getPriority());
    assertEquals(value, header.getRiskLevel());
    assertEquals(value, header.getRiskType());
  }
}
