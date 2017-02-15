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

package org.openecomp.policy.pypdp.model_pojo;

import java.io.Serializable;

public class PepPushPolicyRequest implements Serializable {
	
	private static final long serialVersionUID = 2638006651985508836L;
	
	private String policyScope = null;
	private String policyName = null;
	private String policyType = null;
	private String pdpGroup = null;
	
	public String getPolicyScope() {
		return policyScope;
	}
	
	public String getPolicyName() {
		return policyName;
	}

	public String getPolicyType() {
		return policyType;
	}

	public String getPdpGroup() {
		return pdpGroup;
	}

	public void setPolicyScope(String policyScope) {
		this.policyScope = policyScope;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public void setPdpGroup(String pdpGroup) {
		this.pdpGroup = pdpGroup;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

}
