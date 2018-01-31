/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.api;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enumeration of PolicyDecision that can be returned as a part of
 * {@link org.onap.policy.api.DecisionResponse} getDecision().
 *
 * @version 0.1
 */
public enum PolicyDecision {
	/**
	 * Indicates that the Decision is to Permit.
	 */
	PERMIT("permit"),
	/**
	 * Indicates that the Decision is to Deny.
	 */
	DENY("deny"),
	/**
	 * Indicates that the Decision process has some issues.
	 */
	ERROR("error")
	;

	private String name;
	private PolicyDecision(String name){
		this.name = name;
	}

	/**
	 * Returns the <code>String</code> name for this <code>PolicyDecision</code>
	 *
	 * @return the <code>String</code> name for this <code>PolicyDecision</code>
	 */
	@Override
	public String toString(){
		return this.name;
	}
	@JsonCreator
    public static PolicyDecision create (String value) {
        for(PolicyDecision type: values()){
            if(type.toString().equals(value) || type.equals(PolicyDecision.valueOf(value))){
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
