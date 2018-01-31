/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.notifications;

import java.util.HashMap;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.UpdateType;

/**
 * Updated is the POJO which consists of any new or Updated Policy information.
 * It must hold the Policy Name, version Number, Matches.
 *
 * @version 0.2
 *
 */
public class Updated implements LoadedPolicy{
	private String policyName = null;
	private String versionNo = null;
	private HashMap<String,String> matches = null;
	private UpdateType updateType = null;

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public HashMap<String,String> getMatches() {
		return matches;
	}

	public void setMatches(HashMap<String,String> matches) {
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
