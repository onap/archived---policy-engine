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

package org.openecomp.policy.controlloop.policy;

import java.util.LinkedList;

public class ControlLoopPolicy {
	
	public ControlLoop controlLoop;

	public LinkedList<Policy> policies;

	@Override
	public String toString() {
		return "ControlLoopPolicy [controlLoop=" + controlLoop + ", policies=" + policies + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((controlLoop == null) ? 0 : controlLoop.hashCode());
		result = prime * result + ((policies == null) ? 0 : policies.hashCode());
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
		ControlLoopPolicy other = (ControlLoopPolicy) obj;
		if (controlLoop == null) {
			if (other.controlLoop != null)
				return false;
		} else if (!controlLoop.equals(other.controlLoop))
			return false;
		if (policies == null) {
			if (other.policies != null)
				return false;
		} else if (!policies.equals(other.policies))
			return false;
		return true;
	}

}
