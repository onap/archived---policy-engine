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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import java.util.Date;
import org.junit.Test;

public class ActionBodyEntityTest {
  @Test
  public void testEntity() {
    // Set up test data
    String value = "testVal";
    Date date = new Date();
    ActionBodyEntity entity = new ActionBodyEntity();
    entity.prePersist();
    ActionBodyEntity entity2 = new ActionBodyEntity();
    ActionBodyEntity entity3 = new ActionBodyEntity();

    // Test set and get
    entity3.preUpdate();
    entity3.setActionBody(value);
    assertEquals(value, entity3.getActionBody());
    entity3.setActionBodyName(value);
    assertEquals(value, entity3.getActionBodyName());
    entity3.setCreatedBy(value);
    assertEquals(value, entity3.getCreatedBy());
    entity3.setModifiedBy(value);
    assertEquals(value, entity3.getModifiedBy());
    entity3.setModifiedDate(date);
    assertEquals(date, entity3.getModifiedDate());
    assertEquals(0, entity3.getVersion());
    assertNull(entity3.getCreatedDate());
    entity3.setDeleted(true);
    assertEquals(true, entity3.isDeleted());
    assertEquals(0, entity3.getActionBodyId());

    // Test equals method combinations
    assertEquals(false, entity.equals(null));
    assertEquals(true, entity.equals(entity));
    assertEquals(false, entity.equals(value));
    assertEquals(false, entity.equals(entity2));
    assertThat(entity.hashCode(), is(not(0)));
  }
}
