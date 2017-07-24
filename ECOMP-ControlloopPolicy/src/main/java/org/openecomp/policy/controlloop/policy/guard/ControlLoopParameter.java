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
package org.openecomp.policy.controlloop.policy.guard;

public class ControlLoopParameter {
	private String controlLoopName;
	private String version;
	
	public String getControlLoopName() {
		return controlLoopName;
	}

	public void setControlLoopName(String controlLoopName) {
		this.controlLoopName = controlLoopName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ControlLoopParameter(String controlLoopName, String version) {
		super();
		this.controlLoopName = controlLoopName;
		this.version = version;
	}

	public ControlLoopParameter(ControlLoopParameter cl) {

		this.controlLoopName = cl.controlLoopName;
		this.version = cl.version;
	}

	@Override
	public String toString() {
		return "ControlLoopParameter [controlLoopName=" + controlLoopName + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((controlLoopName == null) ? 0 : controlLoopName.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		ControlLoopParameter other = (ControlLoopParameter) obj;
		if (controlLoopName == null) {
			if (other.controlLoopName != null)
				return false;
		} else if (!controlLoopName.equals(other.controlLoopName))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
}
