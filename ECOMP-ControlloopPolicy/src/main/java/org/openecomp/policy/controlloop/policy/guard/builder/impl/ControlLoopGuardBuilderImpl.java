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
package org.openecomp.policy.controlloop.policy.guard.builder.impl;

import java.util.LinkedList;

import org.openecomp.policy.controlloop.compiler.CompilerException;
import org.openecomp.policy.controlloop.compiler.ControlLoopCompilerCallback;
import org.openecomp.policy.controlloop.guard.compiler.ControlLoopGuardCompiler;
import org.openecomp.policy.controlloop.policy.builder.BuilderException;
import org.openecomp.policy.controlloop.policy.builder.MessageLevel;
import org.openecomp.policy.controlloop.policy.builder.Results;
import org.openecomp.policy.controlloop.policy.builder.impl.MessageImpl;
import org.openecomp.policy.controlloop.policy.builder.impl.ResultsImpl;
import org.openecomp.policy.controlloop.policy.guard.Constraint;
import org.openecomp.policy.controlloop.policy.guard.ControlLoopGuard;
import org.openecomp.policy.controlloop.policy.guard.Guard;
import org.openecomp.policy.controlloop.policy.guard.GuardPolicy;
import org.openecomp.policy.controlloop.policy.guard.builder.ControlLoopGuardBuilder;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class ControlLoopGuardBuilderImpl implements ControlLoopGuardBuilder {

	private ControlLoopGuard CLGuard;
	
	public ControlLoopGuardBuilderImpl(Guard guard) {
		CLGuard = new ControlLoopGuard();
		CLGuard.guard = guard;
	}
	
	@Override
	public ControlLoopGuardBuilder addGuardPolicy(GuardPolicy... policies) throws BuilderException {
		if (policies == null) {
			throw new BuilderException("GuardPolicy must not be null");
		}
		for (GuardPolicy policy : policies) {
			if (!policy.isValid()) {
				throw new BuilderException("Invalid guard policy - some required fields are missing");
			}
			if (CLGuard.guards == null) {
				CLGuard.guards = new LinkedList<GuardPolicy>();
			}
			CLGuard.guards.add(policy);
		}
		return this;
	}

	@Override
	public ControlLoopGuardBuilder removeGuardPolicy(GuardPolicy... policies) throws BuilderException {
		if (policies == null) {
            throw new BuilderException("GuardPolicy must not be null");
        }
        if (CLGuard.guards == null) {
            throw new BuilderException("No existing guard policies to remove");
        }
        for (GuardPolicy policy : policies) {
        	if (!policy.isValid()) {
				throw new BuilderException("Invalid guard policy - some required fields are missing");
			}
            boolean removed = CLGuard.guards.remove(policy);    
            if (!removed) {
                throw new BuilderException("Unknown guard policy: " + policy.name);
            }
        }
        return this;
	}

	@Override
	public ControlLoopGuardBuilder removeAllGuardPolicies() throws BuilderException {
		CLGuard.guards.clear();
        return this;
	}

	@Override
	public ControlLoopGuardBuilder addLimitConstraint(String id, Constraint... constraints) throws BuilderException {
		if (id == null) {
			throw new BuilderException("The id of target guard policy must not be null");
		}
		if (constraints == null) {
			throw new BuilderException("Constraint much not be null");
		}
		boolean exist = false;
		for (GuardPolicy policy: CLGuard.guards) {
			//
			// We could have only one guard policy matching the id
			//
			if (policy.id.equals(id)) {
				exist = true;
				for (Constraint cons: constraints) {
					if (!cons.isValid()) {
						throw new BuilderException("Invalid guard constraint - some required fields are missing");
					}
					if (policy.limit_constraints == null) {
						policy.limit_constraints = new LinkedList<Constraint>();
					}
					policy.limit_constraints.add(cons);
				}
				break;
			}
		}
		if (exist == false) {
			throw new BuilderException("No existing guard policy matching the id: " + id);
		}
		return this;
	}

	@Override
	public ControlLoopGuardBuilder removeLimitConstraint(String id, Constraint... constraints) throws BuilderException {
		if (id == null) {
			throw new BuilderException("The id of target guard policy must not be null");
		}
		if (constraints == null) {
			throw new BuilderException("Constraint much not be null");
		}
		boolean exist = false;
		for (GuardPolicy policy: CLGuard.guards) {
			//
			// We could have only one guard policy matching the id
			//
			if (policy.id.equals(id)) {
				exist = true;
				for (Constraint cons: constraints) {
					if (!cons.isValid()) {
						throw new BuilderException("Invalid guard constraint - some required fields are missing");
					}
					boolean removed = policy.limit_constraints.remove(cons);
					if (!removed) {
						throw new BuilderException("Unknown guard constraint: " + cons);
					}
				}
				break;
			}
		}
		if (exist == false) {
			throw new BuilderException("No existing guard policy matching the id: " + id);
		}
		return this;
	}

	@Override
	public ControlLoopGuardBuilder removeAllLimitConstraints(String id) throws BuilderException {
		if (CLGuard.guards == null || CLGuard.guards.isEmpty()) {
			throw new BuilderException("No guard policies exist");
		} 
		if (id == null) {
			throw new BuilderException("The id of target guard policy must not be null");
		}
		boolean exist = false;
		for (GuardPolicy policy: CLGuard.guards) {
			if (policy.id.equals(id)) {
				exist = true;
				policy.limit_constraints.clear();
			}
		}
		if (exist == false) {
			throw new BuilderException("No existing guard policy matching the id: " + id);
		}
		return this;
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
	public ControlLoopGuard getControlLoopGuard() {
		ControlLoopGuard guard = new ControlLoopGuard(this.CLGuard);
		return guard;
	}	
	
	
	@Override
	public Results buildSpecification() {
		//
		// Dump the specification
		//
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		Yaml yaml = new Yaml(options);
		String dumpedYaml = yaml.dump(CLGuard);
		//
		// This is our callback class for our compiler
		//
		BuilderCompilerCallback callback = new BuilderCompilerCallback();
		//
		// Compile it
		//
		try {
			ControlLoopGuardCompiler.compile(CLGuard, callback);
		} catch (CompilerException e) {
			callback.results.addMessage(new MessageImpl(e.getMessage(), MessageLevel.EXCEPTION));
		}
		//
		// Save the spec
		//
		callback.results.setSpecification(dumpedYaml);
		return callback.results;
	}

}
