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

package org.onap.policy.brms.api.nexus;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.onap.policy.brms.api.nexus.NexusRestSearchParameters;
import org.onap.policy.brms.api.nexus.NexusRestWrapper;
import org.onap.policy.brms.api.nexus.NexusRestWrapperException;
import org.onap.policy.brms.api.nexus.pojo.NexusArtifact;

public class NexusRestWrapperTest {

    @Test
    public void test() throws NexusRestWrapperException {
        NexusRestWrapper wrapper = new NexusRestWrapper("https://nexus.onap.org");
        
        NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
        searchParameters.useFilterSearch("org.onap.policy.engine", "BRMSGateway", null, null, null);
        
        List<NexusArtifact> foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        
        assertNotNull(foundArtifactList);
        
        for (NexusArtifact artifact: foundArtifactList) {
            System.out.println(artifact.getUrlPath());
        }
        
        wrapper.close();
    }
}
