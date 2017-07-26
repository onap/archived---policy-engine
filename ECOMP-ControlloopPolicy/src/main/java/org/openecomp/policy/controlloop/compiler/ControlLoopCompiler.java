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

package org.openecomp.policy.controlloop.compiler;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.controlloop.policy.ControlLoop;
import org.openecomp.policy.controlloop.policy.ControlLoopPolicy;
import org.openecomp.policy.controlloop.policy.FinalResult;
import org.openecomp.policy.controlloop.policy.Policy;
import org.openecomp.policy.controlloop.policy.PolicyResult;
import org.openecomp.policy.controlloop.policy.TargetType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ControlLoopCompiler implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger LOGGER = FlexLogger.getLogger(ControlLoopCompiler.class.getName());
	
	public static ControlLoopPolicy compile(ControlLoopPolicy policy, ControlLoopCompilerCallback callback) throws CompilerException {
		//
		// Ensure the control loop is sane
		//
		validateControlLoop(policy.getControlLoop(), callback);
		//
		// Validate the policies
		//
		validatePolicies(policy, callback);
		
		return policy;
	}
	
	public static ControlLoopPolicy	compile(InputStream yamlSpecification, ControlLoopCompilerCallback callback) throws CompilerException {
		Yaml yaml = new Yaml(new Constructor(ControlLoopPolicy.class));
		Object obj = yaml.load(yamlSpecification);
		if (obj == null) {
			throw new CompilerException("Could not parse yaml specification.");
		}
		if (! (obj instanceof ControlLoopPolicy)) {
			throw new CompilerException("Yaml could not parse specification into required ControlLoopPolicy object");
		}
		return ControlLoopCompiler.compile((ControlLoopPolicy) obj, callback);
	}
	
	private static void validateControlLoop(ControlLoop controlLoop, ControlLoopCompilerCallback callback) throws CompilerException {
		if (controlLoop == null && callback != null) {
			callback.onError("controlLoop cannot be null");
		}
		if (controlLoop!=null){
			if ((controlLoop.getControlLoopName() == null || controlLoop.getControlLoopName().length() < 1) && callback != null) {
				callback.onError("Missing controlLoopName");
			}
			if ((!controlLoop.getVersion().contentEquals(ControlLoop.getVERSION())) && callback != null) {
				callback.onError("Unsupported version for this compiler");
			}
			if (controlLoop.getTrigger_policy() == null || controlLoop.getTrigger_policy().length() < 1) {
				throw new CompilerException("trigger_policy is not valid");
			}
		}
	}

	private static void validatePolicies(ControlLoopPolicy policy, ControlLoopCompilerCallback callback) throws CompilerException {
		if (policy == null) {
			throw new CompilerException("policy cannot be null");
		}
		//
		// verify controlLoop overall timeout should be no less than the sum of operational policy timeouts
		//
		if (policy.getPolicies() == null) {
            callback.onWarning("controlLoop is an open loop.");   
        }
        else{
            int sum = 0;
		    for (Policy operPolicy : policy.getPolicies()) {
		    	sum += operPolicy.getTimeout().intValue();
		    }
		    if (policy.getControlLoop().getTimeout().intValue() < sum && callback != null) {
		    	callback.onError("controlLoop overall timeout is less than the sum of operational policy timeouts.");
		    }
		    //
		    // For this version we can use a directed multigraph, in the future we may not be able to
		    //
		    DirectedGraph<NodeWrapper, LabeledEdge> graph = new DirectedMultigraph<>(new ClassBasedEdgeFactory<NodeWrapper, LabeledEdge>(LabeledEdge.class));
		    //
		    // Check to see if the trigger Event is for OpenLoop, we do so by
		    // attempting to create a FinalResult object from it. If its a policy id, this should
		    // return null.
		    //
		    FinalResult triggerResult = FinalResult.toResult(policy.getControlLoop().getTrigger_policy());
		    TriggerNodeWrapper triggerNode;
		    //
		    // Did this turn into a FinalResult object?
		    //
		    if (triggerResult != null) {
		    	//
		    	// Ensure they didn't use some other FinalResult code
		    	//
		    	if (triggerResult != FinalResult.FINAL_OPENLOOP) {
		    		throw new CompilerException("Unexpected Final Result for trigger_policy, should only be " + FinalResult.FINAL_OPENLOOP.toString() + " or a valid Policy ID");
		    	}
		    	//
		    	// They really shouldn't have any policies attached.
		    	//
		    	if ((policy.getPolicies() != null || policy.getPolicies().isEmpty())&& callback != null ) {
		    		callback.onWarning("Open Loop policy contains policies. The policies will never be invoked.");
		    	}
		    	return;
		    	//
		    } else {
		    	//
		    	// Ok, not a FinalResult object so let's assume that it is a Policy. Which it should be.
		    	//
		    	triggerNode = new TriggerNodeWrapper(policy.getControlLoop().getControlLoopName());
		    }
		    //
		    // Add in the trigger node
		    //
		    graph.addVertex(triggerNode);
		    //
		    // Add in our Final Result nodes. All paths should end to these nodes.
		    //
		    FinalResultNodeWrapper finalSuccess = new FinalResultNodeWrapper(FinalResult.FINAL_SUCCESS);
		    FinalResultNodeWrapper finalFailure = new FinalResultNodeWrapper(FinalResult.FINAL_FAILURE);
		    FinalResultNodeWrapper finalFailureTimeout = new FinalResultNodeWrapper(FinalResult.FINAL_FAILURE_TIMEOUT);
		    FinalResultNodeWrapper finalFailureRetries = new FinalResultNodeWrapper(FinalResult.FINAL_FAILURE_RETRIES);
		    FinalResultNodeWrapper finalFailureException = new FinalResultNodeWrapper(FinalResult.FINAL_FAILURE_EXCEPTION);
		    FinalResultNodeWrapper finalFailureGuard = new FinalResultNodeWrapper(FinalResult.FINAL_FAILURE_GUARD);
		    graph.addVertex(finalSuccess);
		    graph.addVertex(finalFailure);
		    graph.addVertex(finalFailureTimeout);
		    graph.addVertex(finalFailureRetries);
		    graph.addVertex(finalFailureException);
		    graph.addVertex(finalFailureGuard);
		    //
		    // Work through the policies and add them in as nodes.
		    //
		    Map<Policy, PolicyNodeWrapper> mapNodes = new HashMap<>();
		    for (Policy operPolicy : policy.getPolicies()) {
		    	//
		    	// Is it still ok to add?
		    	//
		    	if (!okToAdd(operPolicy, callback)) {
		    		//
		    		// Do not add it in
		    		//
		    		continue;
		    	}
		    	//
		    	// Create wrapper policy node and save it into our map so we can
		    	// easily retrieve it.
		    	//
		    	PolicyNodeWrapper node = new PolicyNodeWrapper(operPolicy);
		    	mapNodes.put(operPolicy, node);
		    	graph.addVertex(node);
		    	//
		    	// Is this the trigger policy?
		    	//
		    	if (operPolicy.getId().equals(policy.getControlLoop().getTrigger_policy())) {
		    		//
		    		// Yes add an edge from our trigger event node to this policy
		    		//
		    		graph.addEdge(triggerNode, node, new LabeledEdge(triggerNode, node, new TriggerEdgeWrapper("ONSET")));
		    	}
		    }
		    //
		    // last sweep to connect remaining edges for policy results
		    //
		    for (Policy operPolicy : policy.getPolicies()) {
		    	PolicyNodeWrapper node = mapNodes.get(operPolicy);
		    	//
		    	// Just ensure this has something
		    	//
		    	if (node == null) {
		    		continue;
		    	}
	    		if (FinalResult.isResult(operPolicy.getSuccess(), FinalResult.FINAL_SUCCESS)) {
	    			graph.addEdge(node, finalSuccess, new LabeledEdge(node, finalSuccess, new FinalResultEdgeWrapper(FinalResult.FINAL_SUCCESS)));
	    		} else {
	    			PolicyNodeWrapper toNode = findPolicyNode(mapNodes, operPolicy.getSuccess());
	    			if (toNode == null) {
	    				throw new CompilerException("Operation Policy " + operPolicy.getId() + " success is connected to unknown policy " + operPolicy.getSuccess());
	    			} else {
	    			 graph.addEdge(node, toNode, new LabeledEdge(node, toNode, new PolicyResultEdgeWrapper(PolicyResult.SUCCESS)));
	    			}
	    		}
	    		if (FinalResult.isResult(operPolicy.getFailure(), FinalResult.FINAL_FAILURE)) {
	    			graph.addEdge(node, finalFailure, new LabeledEdge(node, finalFailure, new FinalResultEdgeWrapper(FinalResult.FINAL_FAILURE)));
	    		} else {
	    			PolicyNodeWrapper toNode = findPolicyNode(mapNodes, operPolicy.getFailure());
	    			if (toNode == null) {
	    				throw new CompilerException("Operation Policy " + operPolicy.getId() + " failure is connected to unknown policy " + operPolicy.getFailure());
	    			} else {
	    				graph.addEdge(node, toNode, new LabeledEdge(node, toNode, new PolicyResultEdgeWrapper(PolicyResult.FAILURE)));
	    			}
	    		}
	    		if (FinalResult.isResult(operPolicy.getFailure_timeout(), FinalResult.FINAL_FAILURE_TIMEOUT)) {
	    			graph.addEdge(node, finalFailureTimeout, new LabeledEdge(node, finalFailureTimeout, new FinalResultEdgeWrapper(FinalResult.FINAL_FAILURE_TIMEOUT)));
	    		} else {
	    			PolicyNodeWrapper toNode = findPolicyNode(mapNodes, operPolicy.getFailure_timeout());
	    			if (toNode == null) {
	    				throw new CompilerException("Operation Policy " + operPolicy.getId() + " failure_timeout is connected to unknown policy " + operPolicy.getFailure_timeout());
	    			} else {
	    				graph.addEdge(node, toNode, new LabeledEdge(node, toNode, new PolicyResultEdgeWrapper(PolicyResult.FAILURE_TIMEOUT)));
	    			}
	    		}
	    		if (FinalResult.isResult(operPolicy.getFailure_retries(), FinalResult.FINAL_FAILURE_RETRIES)) {
	    			graph.addEdge(node, finalFailureRetries, new LabeledEdge(node, finalFailureRetries, new FinalResultEdgeWrapper(FinalResult.FINAL_FAILURE_RETRIES)));
	    		} else {
	    			PolicyNodeWrapper toNode = findPolicyNode(mapNodes, operPolicy.getFailure_retries());
	    			if (toNode == null) {
	    				throw new CompilerException("Operation Policy " + operPolicy.getId() + " failure_retries is connected to unknown policy " + operPolicy.getFailure_retries());
	    			} else {
	    				graph.addEdge(node, toNode, new LabeledEdge(node, toNode, new PolicyResultEdgeWrapper(PolicyResult.FAILURE_RETRIES)));
	    			}
	    		}
	    		if (FinalResult.isResult(operPolicy.getFailure_exception(), FinalResult.FINAL_FAILURE_EXCEPTION)) {
	    			graph.addEdge(node, finalFailureException, new LabeledEdge(node, finalFailureException, new FinalResultEdgeWrapper(FinalResult.FINAL_FAILURE_EXCEPTION)));
	    		} else {
	    			PolicyNodeWrapper toNode = findPolicyNode(mapNodes, operPolicy.getFailure_exception());
	    			if (toNode == null) {
	    				throw new CompilerException("Operation Policy " + operPolicy.getId() + " failure_exception is connected to unknown policy " + operPolicy.getFailure_exception());
	    			} else {
	    				graph.addEdge(node, toNode, new LabeledEdge(node, toNode, new PolicyResultEdgeWrapper(PolicyResult.FAILURE_EXCEPTION)));
	    			}
	    		}
	    		if (FinalResult.isResult(operPolicy.getFailure_guard(), FinalResult.FINAL_FAILURE_GUARD)) {
	    			graph.addEdge(node, finalFailureGuard, new LabeledEdge(node, finalFailureGuard, new FinalResultEdgeWrapper(FinalResult.FINAL_FAILURE_GUARD)));
	    		} else {
	    			PolicyNodeWrapper toNode = findPolicyNode(mapNodes, operPolicy.getFailure_guard());
	    			if (toNode == null) {
	    				throw new CompilerException("Operation Policy " + operPolicy.getId() + " failure_guard is connected to unknown policy " + operPolicy.getFailure_guard());
	    			} else {
	    				graph.addEdge(node, toNode, new LabeledEdge(node, toNode, new PolicyResultEdgeWrapper(PolicyResult.FAILURE_GUARD)));
	    			}
	    		}
	    	}
		    //
		    // Now validate all the nodes/edges
		    //
		    for (NodeWrapper node : graph.vertexSet()) {
		    	if (node instanceof TriggerNodeWrapper) {
		    		LOGGER.info("Trigger Node " + node.toString());
		    		if (graph.inDegreeOf(node) > 0 ) {
		    			//
		    			// Really should NEVER get here unless someone messed up the code above.
		    			//
		    			throw new CompilerException("No inputs to event trigger");
		    		}
		    		//
		    		// Should always be 1, except in the future we may support multiple events
		    		//
		    		if (graph.outDegreeOf(node) > 1) {
		    			throw new CompilerException("The event trigger should only go to ONE node");
		    		}
		    	} else if (node instanceof FinalResultNodeWrapper) {
		    		LOGGER.info("FinalResult Node " + node.toString());
		    		//
		    		// FinalResult nodes should NEVER have an out edge
		    		//
		    		if (graph.outDegreeOf(node) > 0) {
		    			throw new CompilerException("FinalResult nodes should never have any out edges.");
		    		}
		    	} else if (node instanceof PolicyNodeWrapper) {
		    		LOGGER.info("Policy Node " + node.toString());
		    		//
		    		// All Policy Nodes should have the 5 out degrees defined.
		    		//
		    		if (graph.outDegreeOf(node) != 6) {
		    			throw new CompilerException("Policy node should ALWAYS have 6 out degrees.");
		    		}
		    		//
		    		// All Policy Nodes should have at least 1 in degrees 
		    		// 
		    		if (graph.inDegreeOf(node) == 0 && callback != null) {
		    			callback.onWarning("Policy " + node.getID() + " is not reachable.");
		    		}
		    	}
		    	for (LabeledEdge edge : graph.outgoingEdgesOf(node)){
		    		LOGGER.info(edge.from.getID() + " invokes " + edge.to.getID() + " upon " + edge.edge.getID());
		    	}
		    }
	    }	
	}
	
	private static boolean okToAdd(Policy operPolicy, ControlLoopCompilerCallback callback) {
		//
    	// Check the policy id and make sure its sane
    	//
    	boolean okToAdd = true;
    	if (operPolicy.getId() == null || operPolicy.getId().length() < 1) {
    		if (callback != null) {
    			callback.onError("Operational Policy has an bad ID");
    		}
    		okToAdd = false;
    	}
    	//
    	// Check if they decided to make the ID a result object
    	//
    	if (PolicyResult.toResult(operPolicy.getId()) != null) {
    		if (callback != null) {
    			callback.onError("Policy id is set to a PolicyResult " + operPolicy.getId());
    		}
    		okToAdd = false;
    	}
    	if (FinalResult.toResult(operPolicy.getId()) != null) {
    		if (callback != null) {
    			callback.onError("Policy id is set to a FinalResult " + operPolicy.getId());
    		}
    		okToAdd = false;
    	}
    	//
    	// Check that the actor/recipe/target are valid
    	// 
    	if (operPolicy.getActor() == null) {
    		if (callback != null) {
    			callback.onError("Policy actor is null");
    		}
    		okToAdd = false;
    	}
    	//
    	// Construct a list for all valid actors
    	//
    	ImmutableList<String> actors = ImmutableList.of("APPC", "AOTS", "MSO", "SDNO", "SDNR", "AAI");
    	//
    	if (operPolicy.getActor() != null && (!actors.contains(operPolicy.getActor())) ) {
    		if (callback != null) {
    			callback.onError("Policy actor is invalid");
    		}
    		okToAdd = false;
    	}
    	if (operPolicy.getRecipe() == null) {
    		if (callback != null) {
    			callback.onError("Policy recipe is null");
    		}
    		okToAdd = false;
    	}
    	//
    	// NOTE: We need a way to find the acceptable recipe values (either Enum or a database that has these)
    	// 
    	ImmutableMap<String, List<String>> recipes = new ImmutableMap.Builder<String, List<String>>()
				.put("APPC", ImmutableList.of("Restart", "Rebuild", "Migrate", "ModifyConfig"))
    			.put("AOTS", ImmutableList.of("checkMaintenanceWindow", "checkENodeBTicketHours", "checkEquipmentStatus", "checkEimStatus", "checkEquipmentMaintenance"))
    			.put("MSO", ImmutableList.of("VF Module Create"))
    			.put("SDNO", ImmutableList.of("health-diagnostic-type", "health-diagnostic", "health-diagnostic-history", "health-diagnostic-commands", "health-diagnostic-aes"))
    			.put("SDNR", ImmutableList.of("Restart", "Reboot"))
    			.build();
    	//
    	if (operPolicy.getRecipe() != null && (!recipes.getOrDefault(operPolicy.getActor(), Collections.emptyList()).contains(operPolicy.getRecipe()))) {
    		if (callback != null) {
    			callback.onError("Policy recipe is invalid");
    		}
    		okToAdd = false;
    	}
    	if (operPolicy.getTarget() == null) {
    		if (callback != null) {
    			callback.onError("Policy target is null");
    		}
    		okToAdd = false;
    	}
    	if (operPolicy.getTarget() != null && operPolicy.getTarget().getType() != TargetType.VM && operPolicy.getTarget().getType() != TargetType.VFC && operPolicy.getTarget().getType() != TargetType.PNF) {
    		if (callback != null) {
    			callback.onError("Policy target is invalid");
    		}
    		okToAdd = false;
    	}
    	//
    	// Check that policy results are connected to either default final * or another policy
    	//
    	if (FinalResult.toResult(operPolicy.getSuccess()) != null && operPolicy.getSuccess() != FinalResult.FINAL_SUCCESS.toString()) {
    		if (callback != null) {
    			callback.onError("Policy success is neither another policy nor FINAL_SUCCESS");
    		}
    		okToAdd = false;
    	}
    	if (FinalResult.toResult(operPolicy.getFailure()) != null && operPolicy.getFailure() != FinalResult.FINAL_FAILURE.toString()) {
    		if (callback != null) {
    			callback.onError("Policy failure is neither another policy nor FINAL_FAILURE");
    		}
    		okToAdd = false;
    	}
    	if (FinalResult.toResult(operPolicy.getFailure_retries()) != null && operPolicy.getFailure_retries() != FinalResult.FINAL_FAILURE_RETRIES.toString()) {
    		if (callback != null) {
    			callback.onError("Policy failure retries is neither another policy nor FINAL_FAILURE_RETRIES");
    		}
    		okToAdd = false;
    	}
    	if (FinalResult.toResult(operPolicy.getFailure_timeout()) != null && operPolicy.getFailure_timeout() != FinalResult.FINAL_FAILURE_TIMEOUT.toString()) {
    		if (callback != null) {
    			callback.onError("Policy failure timeout is neither another policy nor FINAL_FAILURE_TIMEOUT");
    		}
    		okToAdd = false;
    	}
    	if (FinalResult.toResult(operPolicy.getFailure_exception()) != null && operPolicy.getFailure_exception() != FinalResult.FINAL_FAILURE_EXCEPTION.toString()) {
    		if (callback != null) {
    			callback.onError("Policy failure exception is neither another policy nor FINAL_FAILURE_EXCEPTION");
    		}
    		okToAdd = false;
    	}
    	if (FinalResult.toResult(operPolicy.getFailure_guard()) != null && operPolicy.getFailure_guard() != FinalResult.FINAL_FAILURE_GUARD.toString()) {
    		if (callback != null) {
    			callback.onError("Policy failure guard is neither another policy nor FINAL_FAILURE_GUARD");
    		}
    		okToAdd = false;
    	}
    	return okToAdd;
	}

	private static PolicyNodeWrapper findPolicyNode(Map<Policy, PolicyNodeWrapper> mapNodes, String id) {
		for (Entry<Policy, PolicyNodeWrapper> entry : mapNodes.entrySet()) {
			if (entry.getKey().getId().equals(id)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	@FunctionalInterface
	private interface NodeWrapper extends Serializable{
		public String	getID();
	}
	
	private static class TriggerNodeWrapper implements NodeWrapper {
		private static final long serialVersionUID = -187644087811478349L;
		private String closedLoopControlName;
		
		public TriggerNodeWrapper(String closedLoopControlName) {
			this.closedLoopControlName = closedLoopControlName;
		}

		@Override
		public String toString() {
			return "TriggerNodeWrapper [closedLoopControlName=" + closedLoopControlName + "]";
		}

		@Override
		public String getID() {
			return closedLoopControlName;
		}
		
	}
		
	private static class FinalResultNodeWrapper implements NodeWrapper {
		private static final long serialVersionUID = 8540008796302474613L;
		private FinalResult result;

		public FinalResultNodeWrapper(FinalResult result) {
			this.result = result;
		}

		@Override
		public String toString() {
			return "FinalResultNodeWrapper [result=" + result + "]";
		}

		@Override
		public String getID() {
			return result.toString();
		}
	}
	
	private static class PolicyNodeWrapper implements NodeWrapper {
		private static final long serialVersionUID = 8170162175653823082L;
		private Policy policy;
		
		public PolicyNodeWrapper(Policy operPolicy) {
			this.policy = operPolicy;
		}

		@Override
		public String toString() {
			return "PolicyNodeWrapper [policy=" + policy + "]";
		}

		@Override
		public String getID() {
			return policy.getId();
		}
	}
	
	@FunctionalInterface
	private interface EdgeWrapper extends Serializable{
		public String getID();
		
	}
	
	private static class TriggerEdgeWrapper implements EdgeWrapper {
		private static final long serialVersionUID = 2678151552623278863L;
		private String trigger;
		
		public TriggerEdgeWrapper(String trigger) {
			this.trigger = trigger;
		}

		@Override
		public String getID() {
			return trigger;
		}

		@Override
		public String toString() {
			return "TriggerEdgeWrapper [trigger=" + trigger + "]";
		}
		
	}
	
	private static class PolicyResultEdgeWrapper implements EdgeWrapper {
		private static final long serialVersionUID = 6078569477021558310L;
		private PolicyResult policyResult;

		public PolicyResultEdgeWrapper(PolicyResult policyResult) {
			super();
			this.policyResult = policyResult;
		}

		@Override
		public String toString() {
			return "PolicyResultEdgeWrapper [policyResult=" + policyResult + "]";
		}

		@Override
		public String getID() {
			return policyResult.toString();
		}
		
		
	}
	
	private static class FinalResultEdgeWrapper implements EdgeWrapper {
		private static final long serialVersionUID = -1486381946896779840L;
		private FinalResult finalResult;
		public FinalResultEdgeWrapper(FinalResult result) {
			this.finalResult = result;
		}

		@Override
		public String toString() {
			return "FinalResultEdgeWrapper [finalResult=" + finalResult + "]";
		}
		
		@Override
		public String getID() {
			return finalResult.toString();
		}
	}
	
	
	private static class LabeledEdge extends DefaultEdge {
		private static final long serialVersionUID = 579384429573385524L;
		
		private NodeWrapper from;
		private NodeWrapper to;
		private EdgeWrapper edge;
		
		public LabeledEdge(NodeWrapper from, NodeWrapper to, EdgeWrapper edge) {
			this.from = from;
			this.to = to;
			this.edge = edge;
		}
		
		@SuppressWarnings("unused")
		public NodeWrapper from() {
			return from;
		}
		
		@SuppressWarnings("unused")
		public NodeWrapper to() {
			return to;
		}
		
		@SuppressWarnings("unused")
		public EdgeWrapper edge() {
			return edge;
		}

		@Override
		public String toString() {
			return "LabeledEdge [from=" + from + ", to=" + to + ", edge=" + edge + "]";
		}
	}

}
