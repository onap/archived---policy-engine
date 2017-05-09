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

package org.openecomp.policy.controlloop.policy;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class Policy {

	public String id = UUID.randomUUID().toString();
	public String name;
	public String description;
	public String actor;
	public String recipe;
	public Map<String, String> payload;
	public Target target;
	public OperationsAccumulateParams operationsAccumulateParams;
	public Integer retry = 0;
	public Integer timeout = 300;
	public String success = FinalResult.FINAL_SUCCESS.toString();
	public String failure = FinalResult.FINAL_FAILURE.toString();
	public String failure_retries = FinalResult.FINAL_FAILURE_RETRIES.toString();
	public String failure_timeout = FinalResult.FINAL_FAILURE_TIMEOUT.toString();
	public String failure_exception = FinalResult.FINAL_FAILURE_EXCEPTION.toString();
	public String failure_guard = FinalResult.FINAL_FAILURE_GUARD.toString();
	
	
	public Policy() {
		
	}
	
	public Policy(String id) {
		this.id = id;
	}
	
	public Policy(String name, String actor, String recipe, Map<String, String> payload, Target target) {
		this.name = name;
		this.actor = actor;
		this.recipe = recipe;
		this.target = target;
		if (payload != null) {
//			this.payload = new LinkedList<Map<String, String>>();
			this.payload = Collections.unmodifiableMap(payload);
		}
	}
	
	public Policy(String name, String actor, String recipe, Map<String, String> payload, Target target, Integer retries, Integer timeout) {
		this(name, actor, recipe, payload, target);
		this.retry = retries;
		this.timeout = timeout;
	}
	
	public Policy(String id, String name, String description, String actor, Map<String, String> payload, Target target, String recipe, Integer retries, Integer timeout) {
		this(name, actor, recipe, payload, target, retries, timeout);
		this.id = id;
		this.description = description;
	}
	
	public Policy(Policy policy) {
		this.id = policy.id;
		this.name = policy.name;
		this.description = policy.description;
		this.actor = policy.actor;
		this.recipe = policy.recipe;
		if (policy.payload != null) {
//			this.payload = new LinkedList<Map<String, String>>();
//			this.payload.addAll(policy.payload);
			this.payload = Collections.unmodifiableMap(policy.payload);
		}
		this.target = policy.target;
		this.operationsAccumulateParams = policy.operationsAccumulateParams;
		this.retry = policy.retry;
		this.timeout = policy.timeout;
		this.success = policy.success;
		this.failure = policy.failure;
		this.failure_exception = policy.failure_exception;
		this.failure_guard = policy.failure_guard;
		this.failure_retries = policy.failure_retries;
		this.failure_timeout = policy.failure_timeout;
	}

	public boolean isValid() {
		try {
			if (id == null) {
				throw new NullPointerException();
			}
			if (name == null) {
				throw new NullPointerException();
			}
			if (actor == null) {
				throw new NullPointerException();
			}
			if (recipe == null) {
				throw new NullPointerException();
			}
			if (target == null) {
				throw new NullPointerException();
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "Policy [id=" + id + ", name=" + name + ", description=" + description + ", actor=" + actor + ", recipe="
				+ recipe + ", payload=" + payload + ", target=" + target + ", operationsAccumulateParams=" + operationsAccumulateParams + ", retry=" + retry + ", timeout=" + timeout
				+ ", success=" + success + ", failure=" + failure + ", failure_retries=" + failure_retries
				+ ", failure_timeout=" + failure_timeout + ", failure_exception=" + failure_exception + ", failure_guard=" + failure_guard + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actor == null) ? 0 : actor.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((failure == null) ? 0 : failure.hashCode());
		result = prime * result + ((failure_exception == null) ? 0 : failure_exception.hashCode());
		result = prime * result + ((failure_guard == null) ? 0 : failure_guard.hashCode());
		result = prime * result + ((failure_retries == null) ? 0 : failure_retries.hashCode());
		result = prime * result + ((failure_timeout == null) ? 0 : failure_timeout.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
		result = prime * result + ((retry == null) ? 0 : retry.hashCode());
		result = prime * result + ((success == null) ? 0 : success.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((operationsAccumulateParams == null) ? 0 : operationsAccumulateParams.hashCode());
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Policy other = (Policy) obj;
		if (actor != other.actor)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (failure == null) {
			if (other.failure != null)
				return false;
		} else if (!failure.equals(other.failure))
			return false;
		if (failure_exception == null) {
			if (other.failure_exception != null)
				return false;
		} else if (!failure_exception.equals(other.failure_exception))
			return false;
		if (failure_guard == null) {
			if (other.failure_guard != null)
				return false;
		} else if (!failure_guard.equals(other.failure_guard))
			return false;
		if (failure_retries == null) {
			if (other.failure_retries != null)
				return false;
		} else if (!failure_retries.equals(other.failure_retries))
			return false;
		if (failure_timeout == null) {
			if (other.failure_timeout != null)
				return false;
		} else if (!failure_timeout.equals(other.failure_timeout))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		if (recipe == null) {
			if (other.recipe != null)
				return false;
		} else if (!recipe.equals(other.recipe))
			return false;
		if (retry == null) {
			if (other.retry != null)
				return false;
		} else if (!retry.equals(other.retry))
			return false;
		if (success == null) {
			if (other.success != null)
				return false;
		} else if (!success.equals(other.success))
			return false;
		if (operationsAccumulateParams == null) {
			if (other.operationsAccumulateParams != null)
				return false;
		} else if (!operationsAccumulateParams.equals(other.operationsAccumulateParams))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;	
		if (timeout == null) {
			if (other.timeout != null)
				return false;
		} else if (!timeout.equals(other.timeout))
			return false;
		return true;
	}
	
}
