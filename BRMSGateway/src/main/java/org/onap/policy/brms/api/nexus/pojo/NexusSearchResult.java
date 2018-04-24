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

import java.util.List;

/**
 * The Class NexusSearchResult holds the result of a search in Nexus. It is populated directly from the JSON returned by
 * the Nexus search.
 */
public class NexusSearchResult {
    private int totalCount;
    private int from;
    private int count;
    private boolean tooManyResults;
    private boolean collapsed;
    private List<NexusRepository> repoDetails;
    private List<NexusArtifact> data;
    
    public int getTotalCount() {
        return totalCount;
    }

    public int getFrom() {
        return from;
    }

    public int getCount() {
        return count;
    }

    public boolean isTooManyResults() {
        return tooManyResults;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public List<NexusRepository> getRepoDetailsList() {
        return repoDetails;
    }

    public List<NexusArtifact> getArtifactList() {
        return data;
    }

    @Override
    public String toString() {
        return "NexusSearchResult [totalCount=" + totalCount + ", from=" + from + ", count=" + count
                + ", tooManyResults=" + tooManyResults + ", collapsed=" + collapsed + ", repoDetailsList="
                + repoDetails + ", artifactList=" + data + "]";
    }
}
