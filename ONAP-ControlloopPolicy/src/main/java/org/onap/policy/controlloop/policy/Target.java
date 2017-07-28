/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.controlloop.policy;

import java.io.Serializable;

public class Target implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2180988443264988319L;
	 
	private String resourceID;
	private TargetType type;

	public Target() {
		//Does Nothing Empty Constructor
	}
	
	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public TargetType getType() {
		return type;
	}

	public void setType(TargetType type) {
		this.type = type;
	}

	public Target(TargetType type) {
		this.type = type;
	}
	
	public Target(String resourceID) {
		this.resourceID = resourceID;
	}
	
	public Target(TargetType type, String resourceID) {
		this.type = type;
		this.resourceID = resourceID;
	}
	
	public Target(Target target) {
		this.type = target.type;
		this.resourceID = target.resourceID;
	}
	
	@Override
	public String toString() {
		return "Target [type=" + type + ", resourceID=" + resourceID + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((resourceID == null) ? 0 : resourceID.hashCode());
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
		Target other = (Target) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (resourceID == null) {
			if (other.resourceID != null)
				return false;
		} else if (!resourceID.equals(other.resourceID))
			return false;
		return true;
	}
}
