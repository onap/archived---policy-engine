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

package org.openecomp.policy.rest.adapter;


import java.util.ArrayList;
import java.util.List;

public class ServiceGroupJson {

	protected String name;
	protected String description;
	protected List<ServiceMembers> members;
	
	// name
	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}
	
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if(this.getClass() != obj.getClass()){
			return false;
		}
		ServiceGroupJson servGroupobj=(ServiceGroupJson) obj;
		if(this.getName().equals(servGroupobj.getName())){
			return true;
		}
		return false;
	}
	
	 public int hashCode() {
	    return Integer.valueOf(name.charAt(0)+(name.charAt(1)));
	 }
	
	// description
	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		this.description = value;
	}
	
	public List<ServiceMembers> getMembers() 
    {
    	if(members==null)
    	{
    		members= new ArrayList<ServiceMembers>();
    	}
        return this.members;
    }

	public void setMembers(List<ServiceMembers> members) 
	{
	        this.members = members;
	}

}
