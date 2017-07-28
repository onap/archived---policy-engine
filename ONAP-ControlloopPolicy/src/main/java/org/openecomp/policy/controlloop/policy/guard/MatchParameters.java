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
package org.openecomp.policy.controlloop.policy.guard;

import java.util.LinkedList;
import java.util.List;

public class MatchParameters {
	private String controlLoopName;
	private String actor;
	private String recipe;
	private List<String> targets;

	public MatchParameters() {
		// Do Nothing Empty Constructor.
	}	
	
	public String getControlLoopName() {
		return controlLoopName;
	}

	public void setControlLoopName(String controlLoopName) {
		this.controlLoopName = controlLoopName;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getRecipe() {
		return recipe;
	}

	public void setRecipe(String recipe) {
		this.recipe = recipe;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void setTargets(List<String> targets) {
		this.targets = targets;
	}

	public MatchParameters(String actor, String recipe) {
		this.actor = actor;
		this.recipe = recipe;
	}

	public MatchParameters(String actor, String recipe, List<String> targets) {
		this(actor, recipe);
		if (targets != null) {
			this.targets = new LinkedList<>(targets);
		}
	}

	public MatchParameters(String controlLoopName, String actor, String recipe, List<String> targets) {
		this(actor, recipe, targets);
		this.controlLoopName = controlLoopName;
	}

	public MatchParameters(MatchParameters matchParameters) {

		this.controlLoopName = matchParameters.controlLoopName;
		this.actor = matchParameters.actor;
		this.recipe = matchParameters.recipe;
		if (matchParameters.targets != null) {
			this.targets = new LinkedList<>(matchParameters.targets);
		}
	}

	@Override
	public String toString() {
		return "MatchParameters [controlLoopName=" + controlLoopName + ", actor=" + actor + ", recipe=" + recipe
				+ ", targets=" + targets + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actor == null) ? 0 : actor.hashCode());
		result = prime * result + ((controlLoopName == null) ? 0 : controlLoopName.hashCode());
		result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
		result = prime * result + ((targets == null) ? 0 : targets.hashCode());
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
		MatchParameters other = (MatchParameters) obj;
		if (actor == null) {
			if (other.actor != null)
				return false;
		} else if (!actor.equals(other.actor))
			return false;
		if (controlLoopName == null) {
			if (other.controlLoopName != null)
				return false;
		} else if (!controlLoopName.equals(other.controlLoopName))
			return false;
		if (recipe == null) {
			if (other.recipe != null)
				return false;
		} else if (!recipe.equals(other.recipe))
			return false;
		if (targets == null) {
			if (other.targets != null)
				return false;
		} else if (!targets.equals(other.targets))
			return false;
		return true;
	}
}