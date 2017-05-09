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

package org.openecomp.policy.controlloop.policy.builder.impl;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.openecomp.policy.controlloop.compiler.CompilerException;
import org.openecomp.policy.controlloop.compiler.ControlLoopCompiler;
import org.openecomp.policy.controlloop.compiler.ControlLoopCompilerCallback;
import org.openecomp.policy.controlloop.policy.ControlLoop;
import org.openecomp.policy.controlloop.policy.ControlLoopPolicy;
import org.openecomp.policy.controlloop.policy.FinalResult;
import org.openecomp.policy.controlloop.policy.OperationsAccumulateParams;
import org.openecomp.policy.controlloop.policy.Policy;
import org.openecomp.policy.controlloop.policy.PolicyResult;
import org.openecomp.policy.controlloop.policy.Target;
import org.openecomp.policy.controlloop.policy.builder.BuilderException;
import org.openecomp.policy.controlloop.policy.builder.ControlLoopPolicyBuilder;
import org.openecomp.policy.controlloop.policy.builder.MessageLevel;
import org.openecomp.policy.controlloop.policy.builder.Results;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class ControlLoopPolicyBuilderImpl implements ControlLoopPolicyBuilder {

	private ControlLoopPolicy policy;
	
	public ControlLoopPolicyBuilderImpl(String controlLoopName, Integer timeout) throws BuilderException {
		policy = new ControlLoopPolicy();
		policy.controlLoop = new ControlLoop();
		policy.controlLoop.controlLoopName = controlLoopName;
		policy.controlLoop.timeout = timeout;
	}
	
	@Override
	public ControlLoopPolicyBuilder setAbatement(Boolean abatement) throws BuilderException{
		if (abatement == null) {
			throw new BuilderException("abatement must not be null");
		}
		policy.controlLoop.abatement = abatement;
		return this;
	}
	
	@Override
	public ControlLoopPolicyBuilder	setTimeout(Integer timeout) {
		policy.controlLoop.timeout = timeout;
		return this;
	}
	
	@Override
	public Policy setTriggerPolicy(String name, String description, String actor, Target target, String recipe,
			Map<String, String> payload, Integer retries, Integer timeout) throws BuilderException {
		
		Policy trigger = new Policy(UUID.randomUUID().toString(), name, description, actor, payload, target, recipe, retries, timeout);
		
		policy.controlLoop.trigger_policy = trigger.id;
		
		this.addNewPolicy(trigger);
		//
		// Return a copy of the policy
		//
		return new Policy(trigger);
	}

	@Override
	public Policy setPolicyForPolicyResult(String name, String description, String actor,
			Target target, String recipe, Map<String, String> payload, Integer retries, Integer timeout, String policyID, PolicyResult... results) throws BuilderException {
		//
		// Find the existing policy
		//
		Policy existingPolicy = this.findPolicy(policyID);
		if (existingPolicy == null) {
			throw new BuilderException("Unknown policy " + policyID);
		}
		//
		// Create the new Policy
		//
		Policy newPolicy = new Policy(UUID.randomUUID().toString(), name, description, actor, payload, target, recipe, retries, timeout);
		//
		// Connect the results
		//
		for (PolicyResult result : results) {
			switch (result) {
			case FAILURE:
				existingPolicy.failure = newPolicy.id;
				break;
			case FAILURE_EXCEPTION:
				existingPolicy.failure_exception = newPolicy.id;
				break;
			case FAILURE_RETRIES:
				existingPolicy.failure_retries = newPolicy.id;
				break;
			case FAILURE_TIMEOUT:
				existingPolicy.failure_timeout = newPolicy.id;
				break;
			case FAILURE_GUARD:
				existingPolicy.failure_guard = newPolicy.id;
				break;
			case SUCCESS:
				existingPolicy.success = newPolicy.id;
				break;
			default:
				throw new BuilderException("Invalid PolicyResult " + result);
			}
		}
		//
		// Add it to our list
		//
		this.policy.policies.add(newPolicy);
		//
		// Return a policy to them
		//
		return new Policy(newPolicy);
	}
	
	private class BuilderCompilerCallback implements ControlLoopCompilerCallback {

		public ResultsImpl results = new ResultsImpl();
		
		@Override
		public boolean onWarning(String message) {
			results.addMessage(new MessageImpl(message, MessageLevel.WARNING));
			return false;
		}

		@Override
		public boolean onError(String message) {
			results.addMessage(new MessageImpl(message, MessageLevel.ERROR));
			return false;
		}
	}

	@Override
	public Results	buildSpecification() {
		//
		// Dump the specification
		//
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		Yaml yaml = new Yaml(options);
		String dumpedYaml = yaml.dump(policy);
		//
		// This is our callback class for our compiler
		//
		BuilderCompilerCallback callback = new BuilderCompilerCallback();
		//
		// Compile it
		//
		try {
			ControlLoopCompiler.compile(policy, callback);
		} catch (CompilerException e) {
			callback.results.addMessage(new MessageImpl(e.getMessage(), MessageLevel.EXCEPTION));
		}
		//
		// Save the spec
		//
		callback.results.setSpecification(dumpedYaml);
		return callback.results;
	}

	private void addNewPolicy(Policy policy) {
		if (this.policy.policies == null) {
			this.policy.policies = new LinkedList<Policy>();
		}
		this.policy.policies.add(policy);
	}
	
	private Policy findPolicy(String id) {
		for (Policy policy : this.policy.policies) {
			if (policy.id.equals(id)) {
				return policy;
			}
		}
		return null;
	}

	@Override
	public Integer calculateTimeout() {
		int sum = 0;
        for (Policy policy : this.policy.policies) {
            sum += policy.timeout.intValue();
        }
        return new Integer(sum);
	}

	@Override
	public ControlLoop setTriggerPolicy(String id) throws BuilderException {
		if (id == null) {
            throw new BuilderException("Id must not be null");
        }
	    Policy trigger = this.findPolicy(id);
        if (trigger == null) {
            throw new BuilderException("Unknown policy " + id);
        }
        else {
            this.policy.controlLoop.trigger_policy = id;
        }
        return new ControlLoop(this.policy.controlLoop);
    }

	@Override
	public boolean isOpenLoop() {
        if (this.policy.controlLoop.trigger_policy.equals(FinalResult.FINAL_OPENLOOP.toString())) {
            return true;
        }	
        else {
            return false;
        }
	}

	@Override
	public Policy getTriggerPolicy() throws BuilderException {
	    if (this.policy.controlLoop.trigger_policy.equals(FinalResult.FINAL_OPENLOOP.toString())) {
            return null;
        }
        else {
            Policy trigger = new Policy(this.findPolicy(this.policy.controlLoop.trigger_policy));
            return trigger;
        }
    }

	@Override
	public ControlLoop getControlLoop() {
		ControlLoop loop = new ControlLoop(this.policy.controlLoop);
		return loop;
	}

	@Override
	public Policy setPolicyForPolicyResult(String policyResultID, String policyID, PolicyResult... results)
			throws BuilderException {
		//
        // Find the existing policy
        //
        Policy existingPolicy = this.findPolicy(policyID);
        if (existingPolicy == null) {
            throw new BuilderException(policyID + " does not exist");
        }
        if (this.findPolicy(policyResultID) == null) {
            throw new BuilderException("Operational policy " + policyResultID + " does not exist");
        }
        //
        // Connect the results
        //
        for (PolicyResult result : results) {
            switch (result) {
            case FAILURE:
                existingPolicy.failure = policyResultID;
                break;
            case FAILURE_EXCEPTION:
                existingPolicy.failure_exception = policyResultID;
                break;
            case FAILURE_RETRIES:
            	existingPolicy.failure_retries = policyResultID;
            	break;
            case FAILURE_TIMEOUT:
            	existingPolicy.failure_timeout = policyResultID;
            	break;
            case FAILURE_GUARD:
            	existingPolicy.failure_guard = policyResultID;
            	break;
            case SUCCESS:
            	existingPolicy.success = policyResultID;
            	break;
            default:
            	throw new BuilderException("Invalid PolicyResult " + result);
            }
        }
        return new Policy(this.findPolicy(policyResultID));
	}

	@Override
	public boolean removePolicy(String policyID) throws BuilderException {
		Policy existingPolicy = this.findPolicy(policyID);
        if (existingPolicy == null) {
            throw new BuilderException("Unknown policy " + policyID);
        }
        //
        // Check if the policy to remove is trigger_policy
        //
        if (this.policy.controlLoop.trigger_policy.equals(policyID)) {
            this.policy.controlLoop.trigger_policy = FinalResult.FINAL_OPENLOOP.toString();
        }
        else {
            //
            // Update policies
            //
            for (Policy policy : this.policy.policies) {
                int index = this.policy.policies.indexOf(policy);
                if (policy.success.equals(policyID)) {
                    policy.success = FinalResult.FINAL_SUCCESS.toString();
                }
                if (policy.failure.equals(policyID)) {
                    policy.failure = FinalResult.FINAL_FAILURE.toString();
                }
                if (policy.failure_retries.equals(policyID)) {
                    policy.failure_retries = FinalResult.FINAL_FAILURE_RETRIES.toString();
                }
                if (policy.failure_timeout.equals(policyID)) {
                    policy.failure_timeout = FinalResult.FINAL_FAILURE_TIMEOUT.toString();
                }
                if (policy.failure_exception.equals(policyID)) {
                    policy.failure_exception = FinalResult.FINAL_FAILURE_EXCEPTION.toString();
                }
                if (policy.failure_guard.equals(policyID)) {
                    policy.failure_guard = FinalResult.FINAL_FAILURE_GUARD.toString();
                }
                this.policy.policies.set(index, policy);
            }
        }
        //
        // remove the policy
        //
        boolean removed = this.policy.policies.remove(existingPolicy);
        return removed;
	}

	@Override
	public Policy resetPolicyResults(String policyID) throws BuilderException {
        Policy existingPolicy = this.findPolicy(policyID);
        if (existingPolicy == null) {
            throw new BuilderException("Unknown policy " + policyID);
        }
        //
        // reset policy results
        //
        existingPolicy.success = FinalResult.FINAL_SUCCESS.toString();
        existingPolicy.failure = FinalResult.FINAL_FAILURE.toString();
        existingPolicy.failure_retries = FinalResult.FINAL_FAILURE_RETRIES.toString();
        existingPolicy.failure_timeout = FinalResult.FINAL_FAILURE_TIMEOUT.toString();
        existingPolicy.failure_exception = FinalResult.FINAL_FAILURE_EXCEPTION.toString();
        existingPolicy.failure_guard = FinalResult.FINAL_FAILURE_GUARD.toString();
        return new Policy(existingPolicy);
	}

	@Override
	public ControlLoopPolicyBuilder removeAllPolicies() {
		//
        // Remove all existing operational policies
        //
        this.policy.policies.clear();
        //
        // Revert controlLoop back to an open loop
        //
        this.policy.controlLoop.trigger_policy = FinalResult.FINAL_OPENLOOP.toString();
        return this;
	}
	
	@Override
	public Policy addOperationsAccumulateParams(String policyID, OperationsAccumulateParams operationsAccumulateParams) throws BuilderException {
		Policy existingPolicy = this.findPolicy(policyID);
        if (existingPolicy == null) {
            throw new BuilderException("Unknown policy " + policyID);
        }
        //
        // Add operationsAccumulateParams to existingPolicy
        //
        existingPolicy.operationsAccumulateParams = operationsAccumulateParams;
        return new Policy(existingPolicy);
	}

}
