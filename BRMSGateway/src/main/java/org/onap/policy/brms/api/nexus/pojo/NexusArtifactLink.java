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

/**
 * The Class NexusArtifactHit is a POJO that holds a link to an occurrence of an Artifact in a Maven repository.
 * It is populated from the JSON response to a query on the repository. See:
 * {@linktourl https://repository.sonatype.org/nexus-indexer-lucene-plugin/default/docs/path__lucene_search.html}
 */
public class NexusArtifactLink {
    private String classifier;
    private String extension;
    
    public String getClassifier() {
        return classifier;
    }
    
    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "NexusArtifactLink [classifier=" + classifier + ", extension=" + extension + "]";
    }
}
