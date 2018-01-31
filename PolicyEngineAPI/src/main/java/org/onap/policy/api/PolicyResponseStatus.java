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
 * Enumeration of PolicyResponseStatus that can be returned as a part of
 * {@link org.onap.policy.api.PolicyResponse}.
 *
 * @version 0.2
 */
public enum PolicyResponseStatus {
	/**
	 * Indicates there is no action required.
	 */
	NO_ACTION_REQUIRED("no_action"),
	/**
	 * Indicates that an action has been advised.
	 */
	ACTION_ADVISED("action_advised"),
	/**
	 * Indicates that an action has been taken.
	 */
	ACTION_TAKEN("action_taken")
	;

	private String name;
	private PolicyResponseStatus(String name){
		this.name = name;
	}

	/**
	 * Get the <code>PolicyResponseStatus</code> based on <code>String</code> representation of <code>PolicyResponse</code>
	 *
	 * @param responseStatus the <code>String</code> Response Status
	 * @return the <code>PolicyResponseStatus</code> with the name matching <code>ACTION_ADVISED</code> or <code>ACTION_TAKEN</code> or <code>NO_ACTION_REQUIRED</code>
	 */
	public static PolicyResponseStatus getStatus(String responseStatus) {
		if("action_advised".equalsIgnoreCase(responseStatus)) {
			return ACTION_ADVISED;
		}else if("action_taken".equalsIgnoreCase(responseStatus)) {
			return ACTION_TAKEN;
		}else {
			return NO_ACTION_REQUIRED;
		}
	}

	/**
	 * Returns the <code>String</code> name for this <code>PolicyResponseStatus</code>
	 *
	 * @return the <code>String</code> name for this <code>PolicyResponseStatus</code>
	 */
	@Override
	public String toString(){
		return this.name;
	}

	@JsonCreator
    public static PolicyResponseStatus create (String value) {
        for(PolicyResponseStatus type: values()){
            if(type.toString().equals(value) || type.equals(PolicyResponseStatus.valueOf(value))){
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
