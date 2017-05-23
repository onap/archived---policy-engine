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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.junit.Test;
import org.openecomp.policy.asdc.Resource;
import org.openecomp.policy.asdc.ResourceType;
import org.openecomp.policy.asdc.Service;
import org.openecomp.policy.controlloop.policy.builder.BuilderException;
import org.openecomp.policy.controlloop.policy.builder.ControlLoopPolicyBuilder;
import org.openecomp.policy.controlloop.policy.builder.Message;
import org.openecomp.policy.controlloop.policy.builder.MessageLevel;
import org.openecomp.policy.controlloop.policy.builder.Results;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;


public class ControlLoopPolicyBuilderTest {
	
	@Test
	public void testControlLoop() {
        try {
			//
			// Create a builder for our policy
			//
			ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(UUID.randomUUID().toString(), 2400);
			//
			// Test add services
			//
			Service vSCP = new Service("vSCP");
			Service vUSP = new Service("vUSP");
			Service vTrinity = new Service("Trinity");
			builder = builder.addService(vSCP, vUSP, vTrinity);
			assertTrue(builder.getControlLoop().getServices().size() == 3);
			//
			// Test remove services
			//
			builder = builder.removeService(vSCP);
			assertTrue(builder.getControlLoop().getServices().size() == 2);
			builder = builder.removeAllServices();
			assertTrue(builder.getControlLoop().getServices().size() == 0);
			//
			// Test add resources
			//
			Resource vCTS = new Resource("vCTS", ResourceType.VF);
			Resource vCOM = new Resource("vCTS", ResourceType.VF);
			Resource vRAR = new Resource("vCTS", ResourceType.VF);
			builder = builder.addResource(vCTS, vCOM, vRAR);
			assertTrue(builder.getControlLoop().getResources().size() == 3);
			//
			// Test remove resources
			//
			builder = builder.removeResource(vCTS);
			assertTrue(builder.getControlLoop().getResources().size() == 2);
			builder = builder.removeAllResources();
			assertTrue(builder.getControlLoop().getResources().size() == 0);
            //
            // Test set abatement
            //
            assertFalse(builder.getControlLoop().getAbatement());
            builder = builder.setAbatement(true);
            assertTrue(builder.getControlLoop().getAbatement());
		} catch (BuilderException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTimeout() {
        try {
            //
            // Create a builder for our policy
            //
			ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(UUID.randomUUID().toString(), 2400);
            //
            // Test setTimeout
            //
            assertTrue(builder.getControlLoop().getTimeout() == 2400);
            builder = builder.setTimeout(800);
            assertTrue(builder.getControlLoop().getTimeout() == 800);
            // 
            // Test calculateTimeout
            //
            Policy trigger = builder.setTriggerPolicy(
                    "Restart the VM",
                    "Upon getting the trigger event, restart the VM",
                    "APPC",
                    new Target(TargetType.VM),
                    "Restart",
                    null,
                    2,
                    300);
            @SuppressWarnings("unused")
			Policy onRestartFailurePolicy = builder.setPolicyForPolicyResult(
                    "Rebuild VM",
                    "If the restart fails, rebuild it",
                    "APPC",
                    new Target(TargetType.VM),
                    "Rebuild",
                    null,
                    1,
                    600,
                    trigger.getId(),
                    PolicyResult.FAILURE,
                    PolicyResult.FAILURE_RETRIES,
                    PolicyResult.FAILURE_TIMEOUT); 
            assertTrue(builder.calculateTimeout().equals(new Integer(300 + 600)));
            //
        } catch (BuilderException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testTriggerPolicyMethods() {
	    try {
            ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(UUID.randomUUID().toString(), 2400);
            //
            // Test isOpenLoop
            //
            assertTrue(builder.isOpenLoop());
            //
            // Test set initial trigger policy
            //
            Policy triggerPolicy1 = builder.setTriggerPolicy( 
                    "Restart the VM",
                    "Upon getting the trigger event, restart the VM",
                    "APPC",
                    new Target(TargetType.VM),
                    "Restart",
                    null,
                    2,
                    300);
            assertTrue(builder.isOpenLoop() == false);
            assertTrue(builder.getControlLoop().getTrigger_policy().equals(triggerPolicy1.getId()));
            //
            // Set trigger policy to a new policy 
            //
            @SuppressWarnings("unused")
			Policy triggerPolicy2 = builder.setTriggerPolicy(
                    "Rebuild the VM",
                    "Upon getting the trigger event, rebuild the VM",
                    "APPC",
                    new Target(TargetType.VM),
                    "Rebuild",
                    null,
                    2,
                    300);
            // 
            // Test set trigger policy to another existing policy
            //
            @SuppressWarnings("unused")
			ControlLoop cl = builder.setTriggerPolicy(triggerPolicy1.getId());
            assertTrue(builder.getControlLoop().getTrigger_policy().equals(triggerPolicy1.getId()));
            //
            // Test get trigger policy
            //
            assertTrue(builder.getTriggerPolicy().equals(triggerPolicy1));
            //
        } catch (BuilderException e) {
            fail(e.getMessage());
        }
    }
	
	@Test
	public void testAddRemovePolicies() {
	    try {
			ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(UUID.randomUUID().toString(), 2400);
            Policy triggerPolicy = builder.setTriggerPolicy(
                    "Restart the VM",
                    "Upon getting the trigger event, restart the VM",
                    "APPC",
                    new Target(TargetType.VM),
                    "Restart",
                    null,
                    2,
                    300);
            //
            // Test create a policy and chain it to the results of trigger policy
            //
            Policy onRestartFailurePolicy1 = builder.setPolicyForPolicyResult(
                    "Rebuild VM",
                    "If the restart fails, rebuild it.",
                    "APPC",
                    new Target(TargetType.VM),
                    "Rebuild",
                    null,
                    1,
                    600,
                    triggerPolicy.getId(),
                    PolicyResult.FAILURE,
                    PolicyResult.FAILURE_RETRIES,
                    PolicyResult.FAILURE_TIMEOUT,
                    PolicyResult.FAILURE_GUARD);
            //
            assertTrue(builder.getTriggerPolicy().getFailure().equals(onRestartFailurePolicy1.getId()));
            assertTrue(builder.getTriggerPolicy().getFailure_retries().equals(onRestartFailurePolicy1.getId()));
            assertTrue(builder.getTriggerPolicy().getFailure_timeout().equals(onRestartFailurePolicy1.getId()));
            assertTrue(builder.getTriggerPolicy().getFailure_guard().equals(onRestartFailurePolicy1.getId()));
            //
            // Test remove policy
            //
            boolean removed = builder.removePolicy(onRestartFailurePolicy1.getId());
            assertTrue(removed);
            assertTrue(builder.getTriggerPolicy().getFailure().equals(FinalResult.FINAL_FAILURE.toString()));
            assertTrue(builder.getTriggerPolicy().getFailure_retries().equals(FinalResult.FINAL_FAILURE_RETRIES.toString()));
            assertTrue(builder.getTriggerPolicy().getFailure_timeout().equals(FinalResult.FINAL_FAILURE_TIMEOUT.toString()));
            assertTrue(builder.getTriggerPolicy().getFailure_guard().equals(FinalResult.FINAL_FAILURE_GUARD.toString()));
            //
            // Create another policy and chain it to the results of trigger policy
            //
            Policy onRestartFailurePolicy2 = builder.setPolicyForPolicyResult( 
                    "Rebuild VM",
                    "If the restart fails, rebuild it.",
                    "APPC",
                    new Target(TargetType.VM),
                    "Rebuild",
                    null,
                    2,
                    600,
                    triggerPolicy.getId(),
                    PolicyResult.FAILURE,
                    PolicyResult.FAILURE_RETRIES,
                    PolicyResult.FAILURE_TIMEOUT);
            //
            // Test reset policy results
            //
            triggerPolicy = builder.resetPolicyResults(triggerPolicy.getId());
            assertTrue(builder.getTriggerPolicy().getFailure().equals(FinalResult.FINAL_FAILURE.toString()));
            assertTrue(builder.getTriggerPolicy().getFailure_retries().equals(FinalResult.FINAL_FAILURE_RETRIES.toString()));
            assertTrue(builder.getTriggerPolicy().getFailure_timeout().equals(FinalResult.FINAL_FAILURE_TIMEOUT.toString()));
            //                                                               
            // Test set the policy results to an existing operational policy
            //
            onRestartFailurePolicy2 = builder.setPolicyForPolicyResult(
                    onRestartFailurePolicy2.getId(), 
                    triggerPolicy.getId(), 
                    PolicyResult.FAILURE,
                    PolicyResult.FAILURE_RETRIES,
                    PolicyResult.FAILURE_TIMEOUT);
            assertTrue(builder.getTriggerPolicy().getFailure().equals(onRestartFailurePolicy2.getId()));
            assertTrue(builder.getTriggerPolicy().getFailure_retries().equals(onRestartFailurePolicy2.getId()));
            assertTrue(builder.getTriggerPolicy().getFailure_timeout().equals(onRestartFailurePolicy2.getId()));
            
            //
            // Test remove all existing operational policies
            //
            builder = builder.removeAllPolicies();
            assertTrue(builder.getControlLoop().getTrigger_policy().equals(FinalResult.FINAL_OPENLOOP.toString()));
            //
        } catch (BuilderException e) {
            fail(e.getMessage());
        }
    }

	@Test
	public void testAddOperationsAccumulateParams() {
		try {
			ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(UUID.randomUUID().toString(), 2400);
            Policy triggerPolicy = builder.setTriggerPolicy(
                    "Restart the eNodeB",
                    "Upon getting the trigger event, restart the eNodeB",
                    "SDNR",
                    new Target(TargetType.PNF),
                    "Restart",
                    null,
                    2,
                    300);
            //
            // Add the operationsAccumulateParams
            //
            triggerPolicy = builder.addOperationsAccumulateParams(triggerPolicy.getId(), new OperationsAccumulateParams("15m", 5));
            assertNotNull(builder.getTriggerPolicy().getOperationsAccumulateParams());
            assertTrue(builder.getTriggerPolicy().getOperationsAccumulateParams().getPeriod().equals("15m"));
            assertTrue(builder.getTriggerPolicy().getOperationsAccumulateParams().getLimit() == 5);
            //
		} catch (BuilderException e) {
            fail(e.getMessage());
        }
	}
	
	
	@Test
	public void testBuildSpecification() {
		try {
			//
			// Create the builder
			//
			ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(UUID.randomUUID().toString(), 800);
			//
			// Set the first invalid trigger policy
			//
			Policy policy1 = builder.setTriggerPolicy(
                    "Restart the VM",
                    "Upon getting the trigger event, restart the VM",
                    null,
                    null,
                    "Instantiate",
                    null,
                    2,
                    300);
			Results results = builder.buildSpecification();
			//
			// Check that ERRORs are in results for invalid policy arguments
			//
			boolean invalid_actor = false;
			boolean invalid_recipe = false;
			boolean invalid_target = false;
			for (Message m : results.getMessages()) {
				if (m.getMessage().equals("Policy actor is null") && m.getLevel() == MessageLevel.ERROR) {
					invalid_actor = true;
				}
				if (m.getMessage().equals("Policy recipe is invalid") && m.getLevel() == MessageLevel.ERROR) {
					invalid_recipe = true;
				}
				if (m.getMessage().equals("Policy target is null") && m.getLevel() == MessageLevel.ERROR) {
					invalid_target = true;
				}
			}
			//
			assertTrue(invalid_actor);
			assertTrue(invalid_recipe);
			assertTrue(invalid_target);
			//
			// Remove the invalid policy
			//
			//@SuppressWarnings("unused")
			boolean removed = builder.removePolicy(policy1.getId());
			assertTrue(removed);
			assertTrue(builder.getTriggerPolicy() == null);
			//
			// Set a valid trigger policy
			//
			policy1 = builder.setTriggerPolicy(
                    "Rebuild VM",
                    "If the restart fails, rebuild it.",
                    "APPC",
                    new Target(TargetType.VM),
                    "Rebuild",
                    null,
                    1,
                    600);
			//
			// Set a second valid trigger policy
			//
			Policy policy2 = builder.setTriggerPolicy(
					"Restart the VM",
                    "Upon getting the trigger event, restart the VM",
                    "APPC",
                    new Target(TargetType.VM),
                    "Restart",
                    null,
                    2,
                    300);
			//
			// Now, we have policy1 unreachable
			//
			results = builder.buildSpecification();
			boolean unreachable = false;
			for (Message m : results.getMessages()) {
				if (m.getMessage().equals("Policy " + policy1.getId() + " is not reachable.") && m.getLevel() == MessageLevel.WARNING) {
					unreachable = true;
					break;
				}
			}
			assertTrue(unreachable);
			//
			// Set policy1 for the failure results of policy2
			//
			policy1 = builder.setPolicyForPolicyResult(
					policy1.getId(), 
					policy2.getId(),
					PolicyResult.FAILURE,
                    PolicyResult.FAILURE_RETRIES,
                    PolicyResult.FAILURE_TIMEOUT);
			results = builder.buildSpecification();
			boolean invalid_timeout = false;
			for (Message m : results.getMessages()) {
				if (m.getMessage().equals("controlLoop overall timeout is less than the sum of operational policy timeouts.") && m.getLevel() == MessageLevel.ERROR) {
					invalid_timeout = true;
					break;
				}
			}
			assertTrue(invalid_timeout);
			//
			// Remove policy2 (revert controlLoop back to open loop) 
			//
			removed = builder.removePolicy(policy2.getId());
			//
			// ControlLoop is open loop now, but it still has policies (policy1)
			//
			results = builder.buildSpecification();
			unreachable = false;
			for (Message m : results.getMessages()) {
				if (m.getMessage().equals("Open Loop policy contains policies. The policies will never be invoked.") && m.getLevel() == MessageLevel.WARNING) {
					unreachable = true;
					break;
				}
			}
			assertTrue(unreachable);
			//
		} catch (BuilderException e) {
            fail(e.getMessage());
        }
	}
	
	
	@Test
	public void test() {
		this.test("src/test/resources/v1.0.0/policy_Test.yaml");
	}
	
	@Test
	public void testEvilYaml() {
		try (InputStream is = new FileInputStream(new File("src/test/resources/v1.0.0/test_evil.yaml"))) {
			//
			// Read the yaml into our Java Object
			//
			Yaml yaml = new Yaml(new Constructor(ControlLoopPolicy.class));
			yaml.load(is);
		} catch (FileNotFoundException e) {
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		} catch (YAMLException e) {
			//
			// Should have this
			//
		}
	}
	
	public void test(String testFile) {
		try (InputStream is = new FileInputStream(new File(testFile))) {
			//
			// Read the yaml into our Java Object
			//
			Yaml yaml = new Yaml(new Constructor(ControlLoopPolicy.class));
			Object obj = yaml.load(is);
			assertNotNull(obj);
			assertTrue(obj instanceof ControlLoopPolicy);
			ControlLoopPolicy policyTobuild = (ControlLoopPolicy) obj;
			//
			// Now we're going to try to use the builder to build this.
			//
			ControlLoopPolicyBuilder builder = ControlLoopPolicyBuilder.Factory.buildControlLoop(
					policyTobuild.getControlLoop().getControlLoopName(),
					policyTobuild.getControlLoop().getTimeout());
			//
			// Add services
			//
			if (policyTobuild.getControlLoop().getServices() != null) {
				builder = builder.addService(policyTobuild.getControlLoop().getServices().toArray(new Service[policyTobuild.getControlLoop().getServices().size()]));
			}
			//
			// Add resources
			//
			if (policyTobuild.getControlLoop().getResources() != null) {
				builder = builder.addResource(policyTobuild.getControlLoop().getResources().toArray(new Resource[policyTobuild.getControlLoop().getResources().size()]));
			}
			//
			// Add the policies and be sure to set the trigger policy
			//
			if (policyTobuild.getPolicies() != null) {
				for (Policy policy : policyTobuild.getPolicies()) {
					if (policy.getId() == policyTobuild.getControlLoop().getTrigger_policy()) {
						builder.setTriggerPolicy(policy.getName(), policy.getDescription(), policy.getActor(), policy.getTarget(), policy.getRecipe(), null, policy.getRetry(), policy.getTimeout());
					}
				}
			}
		
			// Question : how to change policy ID and results by using builder ??
		
			@SuppressWarnings("unused")
			Results results = builder.buildSpecification();
			
		} catch (FileNotFoundException e) {
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		} catch (BuilderException e) {
			fail(e.getLocalizedMessage());
		}
		
	}

}
