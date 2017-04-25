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
package org.openecomp.policy.brmsInterface.jpa;

import java.util.List;
import java.util.Map;

import org.openecomp.policy.api.PEDependency;

public class DependencyInfo {
	private Map<String, List<PEDependency>> dependencies;

	public Map<String, List<PEDependency>> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Map<String, List<PEDependency>> dependencies) {
		this.dependencies = dependencies;
	}
	
}
