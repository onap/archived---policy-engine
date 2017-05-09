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

package org.openecomp.policy.controlloop.guard.compiler;


import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openecomp.policy.controlloop.compiler.CompilerException;
import org.openecomp.policy.controlloop.compiler.ControlLoopCompilerCallback;
import org.openecomp.policy.controlloop.policy.guard.Constraint;
import org.openecomp.policy.controlloop.policy.guard.ControlLoopGuard;
import org.openecomp.policy.controlloop.policy.guard.GuardPolicy;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ControlLoopGuardCompiler {
	
	public static ControlLoopGuard compile(ControlLoopGuard CLGuard, ControlLoopCompilerCallback callback) throws CompilerException {
		//
		// Ensure ControlLoopGuard has at least one guard policies
		//
		validateControlLoopGuard(CLGuard, callback);
		//
		// Ensure each guard policy has at least one constraints and all guard policies are unique
		//
		validateGuardPolicies(CLGuard.guards, callback);
		//
		// Ensure constraints for each guard policy are unique
		//
		validateConstraints(CLGuard.guards, callback);
		
		return CLGuard;
	}
	
	public static ControlLoopGuard	compile(InputStream yamlSpecification, ControlLoopCompilerCallback callback) throws CompilerException {
		Yaml yaml = new Yaml(new Constructor(ControlLoopGuard.class));
		Object obj = yaml.load(yamlSpecification);
		if (obj == null) {
			throw new CompilerException("Could not parse yaml specification.");
		}
		if (! (obj instanceof ControlLoopGuard)) {
			throw new CompilerException("Yaml could not parse specification into required ControlLoopGuard object");
		}
		return ControlLoopGuardCompiler.compile((ControlLoopGuard) obj, callback);
	}
	
	private static void validateControlLoopGuard(ControlLoopGuard CLGuard, ControlLoopCompilerCallback callback) throws CompilerException {
		if (CLGuard == null) {
			if (callback != null) {
				callback.onError("ControlLoop Guard cannot be null");
			}
			throw new CompilerException("ControlLoop Guard cannot be null");
		}
		if (CLGuard.guard == null) {
			if (callback != null) {
				callback.onError("Guard version cannot be null");
			}
		}
		if (CLGuard.guards == null) {
			if (callback != null) {
				callback.onError("ControlLoop Guard should have at least one guard policies");
			}
		} else if (CLGuard.guards.size() < 1) {
			if (callback != null) {
				callback.onError("ControlLoop Guard should have at least one guard policies");
			}
		}
	}
	
	private static void validateGuardPolicies(List<GuardPolicy> policies, ControlLoopCompilerCallback callback) throws CompilerException {
		if (policies == null) {
			if (callback != null) {
				callback.onError("Guard policies should not be null");
			}
			throw new CompilerException("Guard policies should not be null");
		}
		//
		// Ensure all guard policies are unique
		//
		Set<GuardPolicy> newSet = new HashSet<GuardPolicy>(policies);
		if (newSet.size() != policies.size()) {
			if (callback != null) {
				callback.onWarning("There are duplicate guard policies");
			}
		}
		//
		// Ensure each guard policy has at least one constraints
		//
		for (GuardPolicy policy : policies) {
			if (policy.limit_constraints == null || policy.limit_constraints.size() < 1) {
				if (callback != null) {
					callback.onError("Guard policy " + policy.name + " does not have any limit constraint");
				}
				throw new CompilerException("Guard policy " + policy.name + " does not have any limit constraint");
			}
		}
	}
	
	private static void validateConstraints(List<GuardPolicy> policies, ControlLoopCompilerCallback callback) throws CompilerException {
		if (policies == null) {
			if (callback != null) {
				callback.onError("Guard policies should not be null");
			}
			throw new CompilerException("Guard policies should not be null");
		}
		for (GuardPolicy policy : policies) {
			Set<Constraint> newSet = new HashSet<Constraint>(policy.limit_constraints);
			if (newSet.size() != policy.limit_constraints.size()) {
				if (callback != null) {
					callback.onWarning("Guard policy " + policy.name + " has duplicate limit constraints");
				}
			}
		}
	}
	
}
