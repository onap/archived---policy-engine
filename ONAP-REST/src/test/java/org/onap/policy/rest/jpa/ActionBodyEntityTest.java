/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
        ActionBodyEntity entity = new ActionBodyEntity();
        entity.prePersist();

        // Test set and get
        ActionBodyEntity entity0 = new ActionBodyEntity();
        entity0.preUpdate();
        entity0.setActionBody(value);
        assertEquals(value, entity0.getActionBody());
        entity0.setActionBodyName(value);
        assertEquals(value, entity0.getActionBodyName());
        entity0.setCreatedBy(value);
        assertEquals(value, entity0.getCreatedBy());
        entity0.setModifiedBy(value);
        assertEquals(value, entity0.getModifiedBy());

        Date date = new Date();
        entity0.setModifiedDate(date);
        assertEquals(date, entity0.getModifiedDate());
        assertEquals(0, entity0.getVersion());
        assertNull(entity0.getCreatedDate());
        entity0.setDeleted(true);
        assertEquals(true, entity0.isDeleted());
        assertEquals(0, entity0.getActionBodyId());

        // Test equals method combinations
        assertEquals(false, entity.equals(null));
        assertEquals(true, entity.equals(entity));
        assertEquals(false, entity.equals((Object) value));
        ActionBodyEntity entity1 = new ActionBodyEntity();
        assertEquals(false, entity.equals(entity1));
        assertThat(entity.hashCode(), is(not(0)));
    }
}
