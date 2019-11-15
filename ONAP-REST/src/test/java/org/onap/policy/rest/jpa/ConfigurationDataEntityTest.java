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

public class ConfigurationDataEntityTest {
    @Test
    public void testEquals() {
        // Set up test data
        String value = "testVal";
        ConfigurationDataEntity entity = new ConfigurationDataEntity();
        entity.prePersist();
        ConfigurationDataEntity entity3 = new ConfigurationDataEntity();

        // Test set and get
        entity3.preUpdate();
        entity3.setConfigBody(value);
        assertEquals(value, entity3.getConfigBody());
        entity3.setCreatedBy(value);
        assertEquals(value, entity3.getCreatedBy());
        entity3.setModifiedBy(value);
        assertEquals(value, entity3.getModifiedBy());

        Date date = new Date();
        entity3.setModifiedDate(date);
        assertEquals(date, entity3.getModifiedDate());
        assertEquals(0, entity3.getVersion());
        assertNull(entity3.getCreatedDate());
        entity3.setDeleted(true);
        assertEquals(true, entity3.isDeleted());
        entity3.setDescription(value);
        assertEquals(value, entity3.getDescription());
        entity3.setConfigType(value);
        assertEquals(value, entity3.getConfigType());
        entity3.setConfigurationName(value);
        assertEquals(value, entity3.getConfigurationName());
        assertEquals(0, entity3.getConfigurationDataId());

        // Test method combinations
        assertEquals(false, entity.equals(null));
        assertEquals(true, entity.equals(entity));
        assertEquals(false, entity.equals(value));

        ConfigurationDataEntity entity2 = new ConfigurationDataEntity();
        assertEquals(false, entity.equals(entity2));
        assertThat(entity.hashCode(), is(not(0)));
    }
}
