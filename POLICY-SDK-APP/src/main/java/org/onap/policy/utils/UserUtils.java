/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onap.policy.model.Roles;

public final class UserUtils {
	
	private UserUtils () {
		// Empty Constructor
	}
	
	public static class Pair<T, U> {
		public final T t;
		public final U u;
		
		public Pair (T t, U u) {
			this.t = t;
			this.u = u;
		}
	}
	
	public static Pair<Set<String>, List<String>> checkRoleAndScope(List<Object> userRoles) {
		Set<String> scopes;
		List<String> roles;
		//Check if the Role and Scope Size are Null get the values from db. 
		roles = new ArrayList<>();
		scopes = new HashSet<>();
		for(Object role: userRoles){
			Roles userRole = (Roles) role;
			roles.add(userRole.getRole());
			if(userRole.getScope() != null){
				if(userRole.getScope().contains(",")){
					String[] multipleScopes = userRole.getScope().split(",");
					for(int i =0; i < multipleScopes.length; i++){
						scopes.add(multipleScopes[i]);
					}
				}else{
					scopes.add(userRole.getScope());
				}		
			}
		}
		return new Pair<>(scopes, roles);
	}

}
