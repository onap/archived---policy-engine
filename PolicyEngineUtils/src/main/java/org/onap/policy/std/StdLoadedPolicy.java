/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.std;

import java.util.Map;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.UpdateType;

public class StdLoadedPolicy implements LoadedPolicy{
	private String policyName = null;
	private String versionNo = null;
	private Map<String,String> matches = null;
	private UpdateType updateType = null;
	
	@Override
	public String getPolicyName() {
		if(policyName!=null && policyName.contains(".xml")){
			return policyName.substring(0, policyName.substring(0, policyName.lastIndexOf('.')).lastIndexOf('.'));
		}
		return this.policyName;
	}
	
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	
	@Override
	public String getVersionNo() {
		return this.versionNo;
	}
	
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	
	@Override
	public Map<String,String> getMatches() {
		return this.matches;
	}
	
	public void setMatches(Map<String,String> matches) {
		this.matches = matches;
	}

	@Override
	public UpdateType getUpdateType() {
		return this.updateType;
	}
	
	public void setUpdateType(UpdateType updateType){
		this.updateType = updateType;
	}
}
