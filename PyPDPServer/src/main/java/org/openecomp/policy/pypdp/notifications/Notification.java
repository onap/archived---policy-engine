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

package org.openecomp.policy.pypdp.notifications;

import java.util.Collection;

import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.RemovedPolicy;

public class Notification{
	
	private Collection<RemovedPolicy> removedPolicies = null;
	private Collection<LoadedPolicy> loadedPolicies = null;
	
	public Collection<RemovedPolicy> getRemovedPolicies() {
		return removedPolicies;
	}

	public Collection<LoadedPolicy> getLoadedPolicies() {
		return loadedPolicies;
	}

	
	public void setRemovedPolicies(Collection<RemovedPolicy> removedPolicies){
		this.removedPolicies = removedPolicies;
	}
	
	public void setLoadedPolicies(Collection<LoadedPolicy> loadedPolicies){
		this.loadedPolicies = loadedPolicies;
	}

}
