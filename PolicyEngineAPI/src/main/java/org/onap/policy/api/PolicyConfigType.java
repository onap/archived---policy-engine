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
 * Enumeration of the Policy Config Types that is used as a part of
 * {@link org.onap.policy.api.PolicyParameters}.
 *
 * @version 0.1
 */
public enum PolicyConfigType{
	/**
	 * Indicates Base Config Policy.
	 */
	Base("Base"),
	/**
	 * Indicates ClosedLoop Fault Policy.
	 */
	ClosedLoop_Fault("Fault"),
	/**
	 * Indicates ClosedLoop Performance Metrics Policy.
	 */
	ClosedLoop_PM("PM"),
	/**
	 * Indicates Firewall Config Policy.
	 */
	Firewall("FW"),
	/**
	 * Indicates BRMS based raw DRL Rule Policy.
	 */
	BRMS_RAW("BRMS_Raw"),
	/**
	 * Indicates BRMS based Param DRL policy.
	 */
	BRMS_PARAM("BRMS_Param"),
	/**
	 * Indicates DCAE Micro Service based Policy.
	 */
	MicroService("MS"),
	/**
	 * Indicates Custom Extended Policy type.
	 */
	Extended("EXTENDED")
	;

	private String name;

	private PolicyConfigType(String name){
		this.name = name;
	}

	/**
	 * Returns the <code>String</code> format of Type for this <code>PolicyClass</code>
	 * @return the <code>String</code> of the Type for this <code>PolicyClass</code>
	 */
	@Override
	public String toString() {
		return name;
	}

	@JsonCreator
    public static PolicyConfigType create (String value) {
        for(PolicyConfigType type: values()){
            if(type.toString().equals(value) || type.equals(PolicyConfigType.valueOf(value))){
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
