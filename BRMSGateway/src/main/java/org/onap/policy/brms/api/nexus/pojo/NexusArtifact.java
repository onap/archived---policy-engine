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
 * The Class NexusArtifact is a POJO that holds information on an Artifact in a Maven repository. It is populated from
 * the JSON response to a query on the repository. See:
 * {@linktourl https://repository.sonatype.org/nexus-indexer-lucene-plugin/default/docs/path__lucene_search.html}
 */
public class NexusArtifact {
    private String groupId;
    private String artifactId;
    private String version;
    private String highlightedFragment;
    private String latestRelease;
    private String latestReleaseRepositoryId;
    private String latestSnapshot;
    private String latestSnapshotRepositoryId;
    private List<NexusArtifactHit> artifactHits;
    
    // Path to the repository, added by wrapper after search is completed
    private String urlPath;

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getHighlightedFragment() {
        return highlightedFragment;
    }

    public String getLatestRelease() {
        return latestRelease;
    }

    public String getLatestReleaseRepositoryId() {
        return latestReleaseRepositoryId;
    }

    public String getLatestSnapshot() {
        return latestSnapshot;
    }

    public String getLatestSnapshotRepositoryId() {
        return latestSnapshotRepositoryId;
    }

    public List<NexusArtifactHit> getArtifactHits() {
        return artifactHits;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(final String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public String toString() {
        return "NexusArtifact [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
                + ", highlightedFragment=" + highlightedFragment + ", latestRelease=" + latestRelease
                + ", latestReleaseRepositoryId=" + latestReleaseRepositoryId + ", latestSnapshot=" + latestSnapshot
                + ", latestSnapshotRepositoryId=" + latestSnapshotRepositoryId + ", artifactHits=" + artifactHits
                + ", urlPath=" + urlPath + "]";
    }
}
