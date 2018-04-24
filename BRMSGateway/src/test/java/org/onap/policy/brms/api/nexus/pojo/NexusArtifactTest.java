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

public class NexusArtifactTest {

    @Test
    public void testNexusArtifact() {
        NexusArtifact artifact = new NexusArtifact();
        
        assertNull(artifact.getGroupId());
        assertNull(artifact.getArtifactId());
        assertNull(artifact.getVersion());
        assertNull(artifact.getHighlightedFragment());
        assertNull(artifact.getLatestRelease());
        assertNull(artifact.getLatestReleaseRepositoryId());
        assertNull(artifact.getLatestSnapshot());
        assertNull(artifact.getLatestSnapshotRepositoryId());
        assertNull(artifact.getArtifactHits());
        
        artifact.setUrlPath("urlPath");
        assertEquals("urlPath", artifact.getUrlPath());
        
        assertNotNull(artifact.toString());
    }
}
