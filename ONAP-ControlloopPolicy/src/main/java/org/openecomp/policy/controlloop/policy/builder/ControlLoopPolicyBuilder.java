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

package org.openecomp.policy.controlloop.policy.builder;

import java.util.Map;

import org.openecomp.policy.asdc.Resource;
import org.openecomp.policy.asdc.Service;
import org.openecomp.policy.controlloop.policy.ControlLoop;
import org.openecomp.policy.controlloop.policy.OperationsAccumulateParams;
import org.openecomp.policy.controlloop.policy.Policy;
import org.openecomp.policy.controlloop.policy.PolicyResult;
import org.openecomp.policy.controlloop.policy.Target;
import org.openecomp.policy.controlloop.policy.builder.impl.ControlLoopPolicyBuilderImpl;

public interface ControlLoopPolicyBuilder {
	
	/**
	 * Adds one or more services to the ControlLoop
	 * 
	 * 
	 * @param service
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder	addService(Service... services) throws BuilderException;
	
	/**
	 * @param services
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder removeService(Service... services) throws BuilderException;
	
	/**
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder removeAllServices() throws BuilderException;
	
	/**
	 * Adds one or more resources to the ControlLoop
	 * 
	 * 
	 * @param resource
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder	addResource(Resource... resources) throws BuilderException;
	
	/**
	 * @param resources
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder removeResource(Resource... resources) throws BuilderException;
	
	/**
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder removeAllResources() throws BuilderException;
	
	/**
	 *  @param abatement
	 *  @return
	 *  @throws BuilderException
	 */
	public ControlLoopPolicyBuilder setAbatement(Boolean abatement) throws BuilderException;
	
	
	/**
	 * Sets the overall timeout value for the Control Loop. If any operational policies have retries and timeouts,
	 * then this overall timeout value should exceed all those values.
	 * 
	 * @param timeout
	 * @return
	 * @throws BuilderException
	 */
	public ControlLoopPolicyBuilder	setTimeout(Integer timeout) throws BuilderException;
	
	/**
	 * Scans the operational policies and calculate an minimum overall timeout for the Control Loop.
	 * 
	 * 
	 * @return Integer
	 */
	public Integer calculateTimeout();
	
	/**
	 * Sets the initial trigger policy when a DCAE Closed Loop Event arrives in the ECOMP Policy Platform.
	 * 
	 * 
	 * @param name
	 * @param description
	 * @param actor
	 * @param target
	 * @param recipe
	 * @param retries
	 * @param timeout
	 * @return Policy
	 * @throws BuilderException
	 */
	public Policy setTriggerPolicy(String name, String description, String actor, Target target, String recipe, Map<String, String> payload, Integer retries, Integer timeout) throws BuilderException;
	
	/**
	 * 
	 * Changes the trigger policy to point to another existing Policy.
	 * 
	 * 
	 * @param id
	 * @return ControlLoop
	 * @throws BuilderException
	 */
	public ControlLoop	setTriggerPolicy(String id) throws BuilderException;
	
	/**
	 * @return
	 */
	public boolean	isOpenLoop();
	
	/**
	 * @return
	 * @throws BuilderException
	 */
	public Policy	getTriggerPolicy() throws BuilderException;
	
	/**
	 * Simply returns a copy of the ControlLoop information.
	 * 
	 * 
	 * @return ControlLoop
	 */
	public ControlLoop	getControlLoop();
	
	/**
	 * Creates a policy that is chained to the result of another Policy.
	 * 
	 * 
	 * @param name
	 * @param description
	 * @param actor
	 * @param target
	 * @param recipe
	 * @param retries
	 * @param timeout
	 * @param policyID
	 * @param results
	 * @return
	 * @throws BuilderException
	 */
	public Policy setPolicyForPolicyResult(String name, String description, String actor,
			Target target, String recipe, Map<String, String> payload, Integer retries, Integer timeout, String policyID, PolicyResult... results) throws BuilderException;
	
	
	/**
	 * Sets the policy result(s) to an existing Operational Policy.
	 * 
	 * 
	 * @param policyResultID
	 * @param policyID
	 * @param results
	 * @return
	 * @throws BuilderException
	 */
	public Policy setPolicyForPolicyResult(String policyResultID, String policyID, PolicyResult... results) throws BuilderException;
	
	/**
	 * Removes an Operational Policy. Be mindful that if any other Operational Policies have results that point to this policy, any
	 * policies that have results pointing to this policy will have their result reset to the appropriate default FINAL_* result.
	 * 
	 * 
	 * @param policyID
	 * @return
	 * @throws BuilderException
	 */
	public boolean removePolicy(String policyID) throws BuilderException;
	
	/**
	 * Resets a policy's results to defualt FINAL_* codes.
	 * 
	 * 
	 * @return Policy
	 * @throws BuilderException - Policy does not exist
	 */
	public Policy	resetPolicyResults(String policyID) throws BuilderException;
	
	/**
	 * Removes all existing Operational Policies and reverts back to an Open Loop.
	 * 
	 * @return
	 */
	public ControlLoopPolicyBuilder	removeAllPolicies();
		
	/**
	 * Adds an operationsAccumulateParams to an existing operational policy
	 * 
	 * @return Policy
	 * @throws BuilderException - Policy does not exist
	 */
	public Policy addOperationsAccumulateParams(String policyID, OperationsAccumulateParams operationsAccumulateParams) throws BuilderException;
	
	/**
	 * This will compile and build the YAML specification for the Control Loop Policy. Please iterate the Results object for details.
	 * The Results object will contains warnings and errors. If the specification compiled successfully, you will be able to retrieve the
	 * YAML.
	 * 
	 * @return Results
	 */
	public Results	buildSpecification();
	
	/**
	 * The Factory is used to build a ControlLoopPolicyBuilder implementation.
	 * 
	 * @author pameladragosh
	 *
	 */
	public static class Factory {
		private Factory(){
			// Private Constructor.
		}
		
		/**
		 * Builds a basic Control Loop with an overall timeout. Use this method if you wish to create an OpenLoop, or if you 
		 * want to interactively build a Closed Loop.
		 * 
		 * @param controlLoopName - Per Closed Loop AID v1.0, unique string for the closed loop.
		 * @param timeout - Overall timeout for the Closed Loop to execute.
		 * @return ControlLoopPolicyBuilder object
		 * @throws BuilderException
		 */
		public static ControlLoopPolicyBuilder	buildControlLoop (String controlLoopName, Integer timeout) throws BuilderException {
			return new ControlLoopPolicyBuilderImpl(controlLoopName, timeout);
		}
		
		/**
		 * Build a Control Loop for a resource and services associated with the resource.
		 * 
		 * @param controlLoopName - Per Closed Loop AID v1.0, unique string for the closed loop.
		 * @param timeout - Overall timeout for the Closed Loop to execute.
		 * @param resource - Resource this closed loop is for. Should come from ASDC, but if not available use resourceName to distinguish.
		 * @param services - Zero or more services associated with this resource. Should come from ASDC, but if not available use serviceName to distinguish.
		 * @return ControlLoopPolicyBuilder object
		 * @throws BuilderException
		 */
		public static ControlLoopPolicyBuilder	buildControlLoop (String controlLoopName, Integer timeout, Resource resource, Service... services) throws BuilderException {
			
			ControlLoopPolicyBuilder builder = new ControlLoopPolicyBuilderImpl(controlLoopName, timeout, resource, services);
			
			return builder;
		}
		
		/**
		 * @param controlLoopName
		 * @param timeout
		 * @param service
		 * @param resources
		 * @return
		 * @throws BuilderException
		 */
		public static ControlLoopPolicyBuilder	buildControlLoop (String controlLoopName, Integer timeout, Service service, Resource... resources) throws BuilderException {
			
			ControlLoopPolicyBuilder builder = new ControlLoopPolicyBuilderImpl(controlLoopName, timeout, service, resources);
			
			return builder;
		}
	}

}
