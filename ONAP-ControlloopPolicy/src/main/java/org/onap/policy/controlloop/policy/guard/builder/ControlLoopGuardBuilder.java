/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.controlloop.policy.guard.builder;

import org.onap.policy.controlloop.policy.builder.BuilderException;
import org.onap.policy.controlloop.policy.builder.Results;
import org.onap.policy.controlloop.policy.guard.Constraint;
import org.onap.policy.controlloop.policy.guard.ControlLoopGuard;
import org.onap.policy.controlloop.policy.guard.Guard;
import org.onap.policy.controlloop.policy.guard.GuardPolicy;
import org.onap.policy.controlloop.policy.guard.builder.impl.ControlLoopGuardBuilderImpl;

public interface ControlLoopGuardBuilder {
	
	/**
	 * Adds one or more guard policies to the Control Loop Guard
	 * 
	 * 
	 * @param policies
	 * @return 
	 * @throws BuilderException
	 */
	public ControlLoopGuardBuilder	addGuardPolicy(GuardPolicy... policies) throws BuilderException;
	
	/**
	 * Removes one or more guard policies from the Control Loop Guard
	 * 
	 * 
	 * @param policies
	 * @return 
	 * @throws BuilderException
	 */
	public ControlLoopGuardBuilder	removeGuardPolicy(GuardPolicy... policies) throws BuilderException;
	
	/**
	 * Removes all guard policies from the Control Loop Guard
	 * 
	 * 
	 * @param 
	 * @return 
	 * @throws BuilderException
	 */
	public ControlLoopGuardBuilder	removeAllGuardPolicies() throws BuilderException;
	
	/**
	 * Adds one or more time limit constraints to the guard policy
	 * 
	 * 
	 * @param id (guard policy id)
	 * @param constraints
	 * @return 
	 * @throws BuilderException
	 */
	public ControlLoopGuardBuilder	addLimitConstraint(String id, Constraint... constraints) throws BuilderException;
	
	/**
	 * Removes one or more time limit constraints from the guard policy
	 * 
	 * 
	 * @param id (guard policy id)
	 * @param constraints
	 * @return 
	 * @throws BuilderException
	 */
	public ControlLoopGuardBuilder	removeLimitConstraint(String id, Constraint... constraints) throws BuilderException;
	
	/**
	 * Removes all time limit constraints from the guard policy
	 * 
	 * 
	 * @param id (guard policy id)
	 * @return 
	 * @throws BuilderException
	 */
	public ControlLoopGuardBuilder	removeAllLimitConstraints(String id) throws BuilderException;
	
	/**
	 *  Simply return a copy of control loop guard
	 *  
	 *  @return ControlLoopGuard
	 */
	public ControlLoopGuard getControlLoopGuard();	
	
	/**
	 * This will compile and build the YAML specification for the Control Loop Guard. Please iterate the Results object for details.
	 * The Results object will contains warnings and errors. If the specification compiled successfully, you will be able to retrieve the
	 * YAML.
	 * 
	 * @return Results
	 */
	public Results	buildSpecification();
	
	/**
	 * The Factory is used to build a ControlLoopGuardBuilder implementation.
	 *
	 */
	public static class Factory {
		
		private Factory(){
			//Do Nothing Private Constructor. 
		}
		/**
		 * @param guard
		 * @return ControlLoopGuardBuilder object
		 * @throws BuilderException
		 */
		public static ControlLoopGuardBuilder	buildControlLoopGuard (Guard guard) throws BuilderException {
			
			return  new ControlLoopGuardBuilderImpl(guard);
			
		}
	}
}
