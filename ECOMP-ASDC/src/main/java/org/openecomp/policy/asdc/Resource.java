/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.policy.asdc;

import java.io.Serializable;
import java.util.UUID;

public class Resource implements Serializable {

	private static final long serialVersionUID = -913729158733348027L;
	
	private	UUID	resourceUUID;
	private	UUID	resourceInvariantUUID;
	private String	resourceName;
	private String	resourceVersion;
	private ResourceType	resourceType;
	
	public Resource() {
		//Empty Constructor
	}
	
	public Resource(Resource resource) {
		this.resourceUUID = resource.resourceUUID;
		this.resourceInvariantUUID = resource.resourceInvariantUUID;
		this.resourceName = resource.resourceName;
		this.resourceVersion = resource.resourceVersion;
		this.resourceType = resource.resourceType;
	}
	
	public Resource(UUID uuid) {
		this.resourceUUID = uuid;
	}
	
	public Resource(String name, ResourceType type) {
		this.resourceName = name;
		this.resourceType = type;
	}
	
	public Resource(UUID uuid, UUID invariantUUID, String name, String version, ResourceType type) {
		this.resourceUUID = uuid;
		this.resourceInvariantUUID = invariantUUID;
		this.resourceName = name;
		this.resourceVersion = version;
		this.resourceType = type;
	}
	
	public UUID getResourceUUID() {
		return resourceUUID;
	}

	public void setResourceUUID(UUID resourceUUID) {
		this.resourceUUID = resourceUUID;
	}

	public UUID getResourceInvariantUUID() {
		return resourceInvariantUUID;
	}

	public void setResourceInvariantUUID(UUID resourceInvariantUUID) {
		this.resourceInvariantUUID = resourceInvariantUUID;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceVersion() {
		return resourceVersion;
	}

	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public String toString() {
		return "Resource [resourceUUID=" + resourceUUID + ", resourceInvariantUUID=" + resourceInvariantUUID
				+ ", resourceName=" + resourceName + ", resourceVersion=" + resourceVersion + ", resourceType="
				+ resourceType + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resourceInvariantUUID == null) ? 0 : resourceInvariantUUID.hashCode());
		result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
		result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
		result = prime * result + ((resourceUUID == null) ? 0 : resourceUUID.hashCode());
		result = prime * result + ((resourceVersion == null) ? 0 : resourceVersion.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (resourceInvariantUUID == null) {
			if (other.resourceInvariantUUID != null)
				return false;
		} else if (!resourceInvariantUUID.equals(other.resourceInvariantUUID))
			return false;
		if (resourceName == null) {
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		if (resourceType == null) {
			if (other.resourceType != null)
				return false;
		} else if (!resourceType.equals(other.resourceType))
			return false;
		if (resourceUUID == null) {
			if (other.resourceUUID != null)
				return false;
		} else if (!resourceUUID.equals(other.resourceUUID))
			return false;
		if (resourceVersion == null) {
			if (other.resourceVersion != null)
				return false;
		} else if (!resourceVersion.equals(other.resourceVersion))
			return false;
		return true;
	}
	
}
