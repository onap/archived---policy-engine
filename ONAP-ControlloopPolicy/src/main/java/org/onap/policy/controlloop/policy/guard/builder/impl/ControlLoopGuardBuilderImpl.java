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
package org.onap.policy.controlloop.policy.guard.builder.impl;

import java.util.LinkedList;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controlloop.compiler.CompilerException;
import org.onap.policy.controlloop.compiler.ControlLoopCompilerCallback;
import org.onap.policy.controlloop.guard.compiler.ControlLoopGuardCompiler;
import org.onap.policy.controlloop.policy.builder.BuilderException;
import org.onap.policy.controlloop.policy.builder.MessageLevel;
import org.onap.policy.controlloop.policy.builder.Results;
import org.onap.policy.controlloop.policy.builder.impl.MessageImpl;
import org.onap.policy.controlloop.policy.builder.impl.ResultsImpl;
import org.onap.policy.controlloop.policy.guard.Constraint;
import org.onap.policy.controlloop.policy.guard.ControlLoopGuard;
import org.onap.policy.controlloop.policy.guard.Guard;
import org.onap.policy.controlloop.policy.guard.GuardPolicy;
import org.onap.policy.controlloop.policy.guard.builder.ControlLoopGuardBuilder;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public class ControlLoopGuardBuilderImpl implements ControlLoopGuardBuilder {
	private static Logger logger = FlexLogger.getLogger(ControlLoopGuardBuilderImpl.class.getName());
	private ControlLoopGuard cLGuard;
	
	public ControlLoopGuardBuilderImpl(Guard guard) {
		cLGuard = new ControlLoopGuard();
		cLGuard.setGuard(guard);
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
			if (cLGuard.getGuards() == null) {
				cLGuard.setGuards(new LinkedList<>());
			}
			cLGuard.getGuards().add(policy);
		}
		return this;
	}

	@Override
	public ControlLoopGuardBuilder removeGuardPolicy(GuardPolicy... policies) throws BuilderException {
		if (policies == null) {
            throw new BuilderException("GuardPolicy must not be null");
        }
        if (cLGuard.getGuards() == null) {
            throw new BuilderException("No existing guard policies to remove");
        }
        for (GuardPolicy policy : policies) {
        	if (!policy.isValid()) {
				throw new BuilderException("Invalid guard policy - some required fields are missing");
			}
            boolean removed = cLGuard.getGuards().remove(policy);
            if (!removed) {
                throw new BuilderException("Unknown guard policy: " + policy.getName());
            }
        }
        return this;
	}

	@Override
	public ControlLoopGuardBuilder removeAllGuardPolicies() throws BuilderException {
		cLGuard.getGuards().clear();
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
		if (!addLimitConstraints(id,constraints)) {
			throw new BuilderException("No existing guard policy matching the id: " + id);
		}
		return this;
	}

	private boolean addLimitConstraints(String id, Constraint... constraints) throws BuilderException {
		boolean exist = false;
		for (GuardPolicy policy: cLGuard.getGuards()) {
			//
			// We could have only one guard policy matching the id
			//
			if (policy.getId().equals(id)) {
				exist = true;
				for (Constraint cons: constraints) {
					if (!cons.isValid()) {
						throw new BuilderException("Invalid guard constraint - some required fields are missing");
					}
					if (policy.getLimit_constraints() == null) {
						policy.setLimit_constraints(new LinkedList<>());
					}
					policy.getLimit_constraints().add(cons);
				}
				break;
			}
		}
		return exist;
	}

	@Override
	public ControlLoopGuardBuilder removeLimitConstraint(String id, Constraint... constraints) throws BuilderException {
		if (id == null) {
			throw new BuilderException("The id of target guard policy must not be null");
		}
		if (constraints == null) {
			throw new BuilderException("Constraint much not be null");
		}
		if (!removeConstraints(id, constraints)) {
			throw new BuilderException("No existing guard policy matching the id: " + id);
		}
		return this;
	}

	private boolean removeConstraints(String id, Constraint... constraints) throws BuilderException {
		boolean exist = false;
		for (GuardPolicy policy: cLGuard.getGuards()) {
			//
			// We could have only one guard policy matching the id
			//
			if (policy.getId().equals(id)) {
				exist = true;
				for (Constraint cons: constraints) {
					if (!cons.isValid()) {
						throw new BuilderException("Invalid guard constraint - some required fields are missing");
					}
					boolean removed = policy.getLimit_constraints().remove(cons);
					if (!removed) {
						throw new BuilderException("Unknown guard constraint: " + cons);
					}
				}
				break;
			}
		}
		return exist;
	}

	@Override
	public ControlLoopGuardBuilder removeAllLimitConstraints(String id) throws BuilderException {
		if (cLGuard.getGuards() == null || cLGuard.getGuards().isEmpty()) {
			throw new BuilderException("No guard policies exist");
		} 
		if (id == null) {
			throw new BuilderException("The id of target guard policy must not be null");
		}
		boolean exist = false;
		for (GuardPolicy policy: cLGuard.getGuards()) {
			if (policy.getId().equals(id)) {
				exist = true;
				policy.getLimit_constraints().clear();
			}
		}
		if (!exist) {
			throw new BuilderException("No existing guard policy matching the id: " + id);
		}
		return this;
	}

	
	private class BuilderCompilerCallback implements ControlLoopCompilerCallback {

		private ResultsImpl results = new ResultsImpl();
		
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
		return new ControlLoopGuard(this.cLGuard);
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
		String dumpedYaml = yaml.dump(cLGuard);
		//
		// This is our callback class for our compiler
		//
		BuilderCompilerCallback callback = new BuilderCompilerCallback();
		//
		// Compile it
		//
		try {
			ControlLoopGuardCompiler.compile(cLGuard, callback);
		} catch (CompilerException e) {
			logger.error(e.getMessage() + e);
			callback.results.addMessage(new MessageImpl(e.getMessage(), MessageLevel.EXCEPTION));
		}
		//
		// Save the spec
		//
		callback.results.setSpecification(dumpedYaml);
		return callback.results;
	}

}
