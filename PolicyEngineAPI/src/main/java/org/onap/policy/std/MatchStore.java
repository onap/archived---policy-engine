/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.std;

import java.util.Collection;
import java.util.HashSet;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;

import org.onap.policy.common.logging.flexlogger.*; 

public class MatchStore {
	private static HashSet<Matches> matchStore = new HashSet<>();
	private static Logger logger = FlexLogger.getLogger(MatchStore.class.getName());
	
	private MatchStore() {
		// Empty Constructor
	}
	
	public static HashSet<Matches> getMatchStore() {
		return matchStore;
	}

	public static void storeMatch(Matches newMatch){
		// Initialization..
		if(newMatch!=null){
			if(matchStore.isEmpty()){
				matchStore.add(newMatch);
			}else{
				// Check if it is a new Match
				Boolean match = false;
				for(Matches oldMatch: matchStore){
					// Compare ONAPName
					if(oldMatch.getOnapName().equals(newMatch.getOnapName())){
						// Compare ConfigName if it exists. 
						if(newMatch.getConfigName()!=null && oldMatch.getConfigName()!=null){
							if(oldMatch.getConfigName().equals(newMatch.getConfigName())){
								// Compare the Config Attributes if they exist.
								if(newMatch.getConfigAttributes()!= null && oldMatch.getConfigAttributes()!=null) {
									//Simple thing would be comparing their size. 
									if(newMatch.getConfigAttributes().size()==oldMatch.getConfigAttributes().size()){
										// Now need to compare each of them..
										int count= 0;
										for(String oldkey: oldMatch.getConfigAttributes().keySet()){
											boolean check = false;
											for(String newKey: newMatch.getConfigAttributes().keySet()){
												if(oldkey.equals(newKey)){
													if(oldMatch.getConfigAttributes().get(oldkey).equals(newMatch.getConfigAttributes().get(newKey))){
														check = true;
													}
												}
											}
											if(check){
												count++;
											}else{
												break;
											}
										}
										if(count==oldMatch.getConfigAttributes().size()){
											match = true;
											break;
										}
									}
								}else if(newMatch.getConfigAttributes()== null && oldMatch.getConfigAttributes()==null){
									match = true;
									break;
								}
							}
						}else if(newMatch.getConfigName()==null && oldMatch.getConfigName()==null){
							match = true;
							break;
						}	
					}
					
				}
				// IF not a match then add it to the MatchStore
				if(match==false){
					matchStore.add(newMatch);
				}
			}
		}
	}
	
	//Logic changes for Requested Policies notifications.. 
	public static PDPNotification checkMatch(PDPNotification oldNotification) {
		boolean removed = false;
		boolean updated = false;
		if(oldNotification==null){
			return null;
		}
		StdPDPNotification newNotification = new StdPDPNotification();
		if(matchStore.isEmpty()) {
			logger.debug("No Success Config Calls made yet.. ");
			return null;
		} 
		if(oldNotification.getRemovedPolicies()!=null && !oldNotification.getRemovedPolicies().isEmpty()){
			// send all removed policies to client.
			Collection<StdRemovedPolicy> removedPolicies = new HashSet<>();
			StdRemovedPolicy newRemovedPolicy;
			for(RemovedPolicy removedPolicy: oldNotification.getRemovedPolicies()){
				newRemovedPolicy = new StdRemovedPolicy();
				newRemovedPolicy.setPolicyName(removedPolicy.getPolicyName());
				newRemovedPolicy.setVersionNo(removedPolicy.getVersionNo());
				removedPolicies.add(newRemovedPolicy);
			}
			newNotification.setRemovedPolicies(removedPolicies);
			removed = true;
		}
		if(oldNotification.getLoadedPolicies()!=null && !oldNotification.getLoadedPolicies().isEmpty()){
			Collection<StdLoadedPolicy> updatedPolicies = new HashSet<>();
			StdLoadedPolicy newUpdatedPolicy;
			for(LoadedPolicy updatedPolicy: oldNotification.getLoadedPolicies()){
				// if it is config policies check their matches..
				if(updatedPolicy.getMatches()!=null && !updatedPolicy.getMatches().isEmpty()){
					boolean matched;
					for(Matches match : matchStore){
						matched = false;
						// Again Better way would be comparing sizes first.
						// Matches are different need to check if has configAttributes
						if(match.getConfigAttributes()!=null && !match.getConfigAttributes().isEmpty()){
							// adding onap and config to config-attributes. 
							int compValues = match.getConfigAttributes().size() + 2;
							if(updatedPolicy.getMatches().size()== compValues){
								// Comparing both the values.. 
								boolean matchAttributes = false;
								for(String newKey: updatedPolicy.getMatches().keySet()){
									if("ONAPName".equals(newKey)){
										if(updatedPolicy.getMatches().get(newKey).equals(match.getOnapName())){
											matchAttributes = true;
										}else {
											matchAttributes = false;
											break;
										}
									}else if("ConfigName".equals(newKey)) {
										if(updatedPolicy.getMatches().get(newKey).equals(match.getConfigName())){
											matchAttributes = true;
										}else{
											matchAttributes = false;
											break;
										}
									}else {
										if(match.getConfigAttributes().containsKey(newKey)){
											if(updatedPolicy.getMatches().get(newKey).equals(match.getConfigAttributes().get(newKey))){
												matchAttributes = true;
											}else{
												matchAttributes = false;
												break;
											}
										}else{
											matchAttributes = false;
											break;
										}
									}
								}
								if(matchAttributes){
									// Match..
									matched = true;
								}else{
									break;
								}
							}else {
								break;
							}
						}else if(match.getConfigName()!=null){
							// If there are no config Attributes then check if it has Config Name
							if(updatedPolicy.getMatches().size()== 2){
								if(updatedPolicy.getMatches().get("ONAPName").equals(match.getOnapName())){
									if(updatedPolicy.getMatches().get("ConfigName").equals(match.getConfigName())){
										// Match..
										matched = true;
									}else{
										break;
									}
								}else {
									break;
								}
							}else{
								break;
							}
						}else {
							// If non exist then assuming the ONAP Name to be there. 
							if(updatedPolicy.getMatches().size()== 1){
								if(updatedPolicy.getMatches().get("ONAPName").equals(match.getOnapName())){
									// Match.. 
									matched = true;
								}else {
									break;
								}
							}else{
								break;
							}
						}
						// Add logic to add the policy. 
						if(matched){
							newUpdatedPolicy = new StdLoadedPolicy();
							newUpdatedPolicy.setPolicyName(updatedPolicy.getPolicyName());
							newUpdatedPolicy.setVersionNo(updatedPolicy.getVersionNo());
							newUpdatedPolicy.setMatches(updatedPolicy.getMatches());
							updatedPolicies.add(newUpdatedPolicy);
							updated = true;
						}
					}
					
				}else {
					//send all non config notifications to client.
					newUpdatedPolicy = new StdLoadedPolicy();
					newUpdatedPolicy.setPolicyName(updatedPolicy.getPolicyName());
					newUpdatedPolicy.setVersionNo(updatedPolicy.getVersionNo());
					updatedPolicies.add(newUpdatedPolicy);
					updated = true;
				}
			}
			newNotification.setLoadedPolicies(updatedPolicies);
		}
		// Need to set the type of Update.. 
		if(removed && updated) {
			newNotification.setNotificationType(NotificationType.BOTH);
		}else if(removed){
			newNotification.setNotificationType(NotificationType.REMOVE);
		}else if(updated){
			newNotification.setNotificationType(NotificationType.UPDATE);
		}
		return newNotification;
	}
	
}
