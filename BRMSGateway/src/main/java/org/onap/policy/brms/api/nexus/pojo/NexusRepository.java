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

import com.google.gson.annotations.SerializedName;

/**
 * The Class NexusRepository is a POJO that holds information on a repository in Maven.
 * It is populated directly from the JSON returned from a search in Maven.
 */
public class NexusRepository {
    private String repositoryContentClass;
    private String repositoryId;
    private String repositoryKind;
    private String repositoryName;
    private String repositoryPolicy;
    @SerializedName("repositoryURL")
    private String repositoryUrl;

    public String getRepositoryContentClass() {
        return repositoryContentClass;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getRepositoryKind() {
        return repositoryKind;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getRepositoryPolicy() {
        return repositoryPolicy;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    @Override
    public String toString() {
        return "NexusRepository [repositoryContentClass=" + repositoryContentClass + ", repositoryId=" + repositoryId
                + ", repositoryKind=" + repositoryKind + ", repositoryName=" + repositoryName + ", repositoryPolicy="
                + repositoryPolicy + ", repositoryUrl=" + repositoryUrl + "]";
    }
}
