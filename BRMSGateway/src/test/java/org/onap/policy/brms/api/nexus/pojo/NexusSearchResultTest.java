/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 Ericsson Intellectual Property. All rights reserved.
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

package org.onap.policy.brms.api.nexus.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class NexusSearchResultTest {

    @Test
    public void testNexusSearchResult() {
        NexusSearchResult result = new NexusSearchResult();
        
        assertNull(result.getArtifactList());
        assertEquals(0, result.getCount());
        assertEquals(0, result.getFrom());
        assertNull(result.getRepoDetailsList());
        assertEquals(0, result.getTotalCount());
        assertEquals(false, result.isCollapsed());
        assertEquals(false, result.isTooManyResults());
        
        assertNotNull(result.toString());
    }
}
