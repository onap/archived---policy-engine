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

package org.openecomp.policy.std;

import java.util.Collection;
import java.util.HashSet;

import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.RemovedPolicy;
import org.openecomp.policy.std.StdLoadedPolicy;
import org.openecomp.policy.std.StdPDPNotification;
import org.openecomp.policy.std.StdRemovedPolicy;

import org.openecomp.policy.common.logging.flexlogger.*; 

public class MatchStore {
	private static HashSet<Matches> matchStore = new HashSet<Matches>();
	private static Logger logger = FlexLogger.getLogger(MatchStore.class.getName());
	
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
					// Compare ECOMPName
					if(oldMatch.getEcompName().equals(newMatch.getEcompName())){
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
	
	//TODO Logic changes for Requested Policies notifications.. 
	public static PDPNotification checkMatch(PDPNotification oldNotification) {
		boolean removed = false, updated = false;
		if(oldNotification==null){
			return null;
		}
		StdPDPNotification newNotification = new StdPDPNotification();
		if(matchStore.isEmpty()) {
			logger.debug("No Success Config Calls made yet.. ");
			System.out.println("No success Config calls made yet. ");
			return null;
		} 
		if(oldNotification.getRemovedPolicies()!=null && !oldNotification.getRemovedPolicies().isEmpty()){
			// send all removed policies to client.
			Collection<StdRemovedPolicy> removedPolicies = new HashSet<StdRemovedPolicy>();
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
			Collection<StdLoadedPolicy> updatedPolicies = new HashSet<StdLoadedPolicy>();
			StdLoadedPolicy newUpdatedPolicy;
			for(LoadedPolicy updatedPolicy: oldNotification.getLoadedPolicies()){
				// if it is config policies check their matches..
				if(updatedPolicy.getMatches()!=null && !updatedPolicy.getMatches().isEmpty()){
					boolean matched = false;
					for(Matches match : matchStore){
						matched = false;
						// Again Better way would be comparing sizes first.
						// Matches are different need to check if has configAttributes
						if(match.getConfigAttributes()!=null && !match.getConfigAttributes().isEmpty()){
							// adding ecomp and config to config-attributes. 
							int compValues = match.getConfigAttributes().size() + 2;
							if(updatedPolicy.getMatches().size()== compValues){
								// Comparing both the values.. 
								boolean matchAttributes = false;
								for(String newKey: updatedPolicy.getMatches().keySet()){
									if(newKey.equals("ECOMPName")){
										if(updatedPolicy.getMatches().get(newKey).equals(match.getEcompName())){
											matchAttributes = true;
										}else {
											matchAttributes = false;
											break;
										}
									}else if(newKey.equals("ConfigName")) {
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
								if(updatedPolicy.getMatches().get("ECOMPName").equals(match.getEcompName())){
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
							// If non exist then assuming the ECOMP Name to be there. 
							if(updatedPolicy.getMatches().size()== 1){
								if(updatedPolicy.getMatches().get("ECOMPName").equals(match.getEcompName())){
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
