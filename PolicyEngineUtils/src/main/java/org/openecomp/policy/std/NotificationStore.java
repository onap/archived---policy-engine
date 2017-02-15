/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.RemovedPolicy;


/*
 * This Should Compare and save the Notifications from the beginning of Time. 
 * If there is something new (missed) We notify the client. 
 * Works for PDP failures. 
 * 
 */
public class NotificationStore {
	private static StdPDPNotification notificationRecord = new StdPDPNotification();
	
	public static StdPDPNotification getDeltaNotification(StdPDPNotification newNotification){
		StdPDPNotification notificationDelta = new StdPDPNotification();
		ArrayList<StdRemovedPolicy> removedDelta = new ArrayList<StdRemovedPolicy>();
		ArrayList<StdLoadedPolicy> updatedDelta = new ArrayList<StdLoadedPolicy>();
		Collection<StdLoadedPolicy> newUpdatedPolicies = new ArrayList<StdLoadedPolicy>();
		Collection<StdRemovedPolicy> newRemovedPolicies = new ArrayList<StdRemovedPolicy>();
		Collection<LoadedPolicy> oldUpdatedLostPolicies = notificationRecord.getLoadedPolicies();
		Collection<RemovedPolicy> oldRemovedPolicies = notificationRecord.getRemovedPolicies();
		Collection<LoadedPolicy> oldUpdatedPolicies = notificationRecord.getLoadedPolicies();
		Boolean update = false;
		Boolean remove = false;
		// if the NotificationRecord is empty
		if(notificationRecord.getRemovedPolicies()==null || notificationRecord.getLoadedPolicies()==null){
			if(newNotification!=null){
				notificationRecord = newNotification;
			}
			return notificationDelta;
		}
		// do the Delta operation. 
		if(newNotification!=null){
			// check for old removed policies.
			if(!newNotification.getRemovedPolicies().isEmpty()){
				for(RemovedPolicy newRemovedPolicy: newNotification.getRemovedPolicies()){
					//Look for policy Not in Remove
					Boolean removed = true;
					for(RemovedPolicy oldRemovedPolicy: notificationRecord.getRemovedPolicies()){
						if(newRemovedPolicy.getPolicyName().equals(oldRemovedPolicy.getPolicyName())){
							if(newRemovedPolicy.getVersionNo().equals(oldRemovedPolicy.getVersionNo())){
								removed = false;
								// Don't want a duplicate. 
								oldRemovedPolicies.remove(oldRemovedPolicy);
							}
						}
					}
					//We need to change our record we have an Update record of this remove.  
					for(LoadedPolicy oldUpdatedPolicy: notificationRecord.getLoadedPolicies()){
						if(newRemovedPolicy.getPolicyName().equals(oldUpdatedPolicy.getPolicyName())){
							if(newRemovedPolicy.getVersionNo().equals(oldUpdatedPolicy.getVersionNo())){
								oldUpdatedPolicies.remove(oldUpdatedPolicy);
								oldUpdatedLostPolicies.remove(oldUpdatedPolicy);
							}
						}
					}
					if(removed){
						remove = true;
						notificationRecord.getRemovedPolicies().add(newRemovedPolicy);
						removedDelta.add((StdRemovedPolicy)newRemovedPolicy);
					}
					// This will be converted to New Later. 
					oldRemovedPolicies.add(newRemovedPolicy);
				}
			}
			// Check for old Updated Policies. 
			if(!newNotification.getLoadedPolicies().isEmpty()){
				for(LoadedPolicy newUpdatedPolicy: newNotification.getLoadedPolicies()){
					// Look for policies which are not in Update
					Boolean updated = true;
					for(LoadedPolicy oldUpdatedPolicy: notificationRecord.getLoadedPolicies()){
						if(newUpdatedPolicy.getPolicyName().equals(oldUpdatedPolicy.getPolicyName())){
							if(newUpdatedPolicy.getVersionNo().equals(oldUpdatedPolicy.getVersionNo())){
								updated = false;
								// Remove the policy from copy. 
								oldUpdatedLostPolicies.remove(oldUpdatedPolicy);
								// Eliminating Duplicate. 
								oldUpdatedPolicies.remove(oldUpdatedPolicy);
							}
						}
					}
					// Change the record if the policy has been Removed earlier. 
					for(RemovedPolicy oldRemovedPolicy: notificationRecord.getRemovedPolicies()){
						if(oldRemovedPolicy.getPolicyName().equals(newUpdatedPolicy.getPolicyName())){
							if(oldRemovedPolicy.getVersionNo().equals(newUpdatedPolicy.getVersionNo())){
								oldRemovedPolicies.remove(oldRemovedPolicy);
							}
						}
					}
					if(updated){
						update = true;
						updatedDelta.add((StdLoadedPolicy)newUpdatedPolicy);
					}
					// This will be converted to new Later
					oldUpdatedPolicies.add(newUpdatedPolicy);
				}
				// Conversion of Update to Remove if that occurred.
				if(!oldUpdatedLostPolicies.isEmpty()){
					for(LoadedPolicy updatedPolicy: oldUpdatedLostPolicies){
						StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
						removedPolicy.setPolicyName(updatedPolicy.getPolicyName());
						removedPolicy.setVersionNo(updatedPolicy.getVersionNo());
						removedDelta.add(removedPolicy);
						remove = true;
					}
				}
			}
			// Update our Record. 
			if(!oldUpdatedPolicies.isEmpty()){
				for(LoadedPolicy updatedPolicy: oldUpdatedPolicies){
					newUpdatedPolicies.add((StdLoadedPolicy)updatedPolicy);
				}
			}
			if(!oldRemovedPolicies.isEmpty()){
				for(RemovedPolicy removedPolicy: oldRemovedPolicies){
					newRemovedPolicies.add((StdRemovedPolicy)removedPolicy);
				}
			}
			notificationRecord.setRemovedPolicies(newRemovedPolicies);
			notificationRecord.setLoadedPolicies(newUpdatedPolicies);
			// Update the notification Result. 
			notificationDelta.setRemovedPolicies(removedDelta);
			notificationDelta.setLoadedPolicies(updatedDelta);
			if(remove&&update){
				notificationDelta.setNotificationType(NotificationType.BOTH);
			}else if(remove){
				notificationDelta.setNotificationType(NotificationType.REMOVE);
			}else if(update){
				notificationDelta.setNotificationType(NotificationType.UPDATE);
			}
		}
		return notificationDelta;
	}
	
	public static void recordNotification(StdPDPNotification notification){
		if(notification!=null){
			if(notificationRecord.getRemovedPolicies()==null || notificationRecord.getLoadedPolicies()==null){
				notificationRecord = notification;
			}else{
				// Check if there is anything new and update the record. 
				if(notificationRecord.getLoadedPolicies()!=null || notificationRecord.getRemovedPolicies()!=null){
					HashSet<StdRemovedPolicy> removedPolicies = new HashSet<StdRemovedPolicy>();
					for(RemovedPolicy rPolicy: notificationRecord.getRemovedPolicies()){
						StdRemovedPolicy sRPolicy = new StdRemovedPolicy();
						sRPolicy.setPolicyName(rPolicy.getPolicyName());
						sRPolicy.setVersionNo(rPolicy.getVersionNo());
						removedPolicies.add(sRPolicy);
					}
					HashSet<StdLoadedPolicy> updatedPolicies = new HashSet<StdLoadedPolicy>();
					for(LoadedPolicy uPolicy: notificationRecord.getLoadedPolicies()){
						StdLoadedPolicy sUPolicy = new StdLoadedPolicy();
						sUPolicy.setMatches(uPolicy.getMatches());
						sUPolicy.setPolicyName(uPolicy.getPolicyName());
						sUPolicy.setVersionNo(uPolicy.getVersionNo());
						updatedPolicies.add(sUPolicy);
					}
					
					// Checking with the new updated policies.
					if(notification.getLoadedPolicies()!=null && !notification.getLoadedPolicies().isEmpty()){
						for(LoadedPolicy newUpdatedPolicy: notification.getLoadedPolicies()){
							// If it was removed earlier then we need to remove from our record
							Iterator<StdRemovedPolicy> oldRemovedPolicy = removedPolicies.iterator();
							while(oldRemovedPolicy.hasNext()){
								RemovedPolicy policy = oldRemovedPolicy.next(); 
								if(newUpdatedPolicy.getPolicyName().equals(policy.getPolicyName())) {
									if(newUpdatedPolicy.getVersionNo().equals(policy.getVersionNo())) {
										oldRemovedPolicy.remove();
									}
								}
							}
							// If it was previously updated need to Overwrite it to the record. 
							Iterator<StdLoadedPolicy> oldUpdatedPolicy = updatedPolicies.iterator();
							while(oldUpdatedPolicy.hasNext()){
								LoadedPolicy policy = oldUpdatedPolicy.next();
								if(newUpdatedPolicy.getPolicyName().equals(policy.getPolicyName())) {
									if(newUpdatedPolicy.getVersionNo().equals(policy.getVersionNo())) {
										oldUpdatedPolicy.remove();
									}
								}
							}
							StdLoadedPolicy sUPolicy = new StdLoadedPolicy();
							sUPolicy.setMatches(newUpdatedPolicy.getMatches());
							sUPolicy.setPolicyName(newUpdatedPolicy.getPolicyName());
							sUPolicy.setVersionNo(newUpdatedPolicy.getVersionNo());
							updatedPolicies.add(sUPolicy);
						}
					}
					// Checking with New Removed Policies.
					if(notification.getRemovedPolicies()!=null && !notification.getRemovedPolicies().isEmpty()){
						for(RemovedPolicy newRemovedPolicy : notification.getRemovedPolicies()){
							// If it was previously removed Overwrite it to the record. 
							Iterator<StdRemovedPolicy> oldRemovedPolicy = removedPolicies.iterator();
							while(oldRemovedPolicy.hasNext()){
								RemovedPolicy policy = oldRemovedPolicy.next(); 
								if(newRemovedPolicy.getPolicyName().equals(policy.getPolicyName())) {
									if(newRemovedPolicy.getVersionNo().equals(policy.getVersionNo())) {
										oldRemovedPolicy.remove();
									}
								}
							}
							// If it was added earlier then we need to remove from our record. 
							Iterator<StdLoadedPolicy> oldUpdatedPolicy = updatedPolicies.iterator();
							while(oldUpdatedPolicy.hasNext()){
								LoadedPolicy policy = oldUpdatedPolicy.next();
								if(newRemovedPolicy.getPolicyName().equals(policy.getPolicyName())) {
									if(newRemovedPolicy.getVersionNo().equals(policy.getVersionNo())) {
										oldUpdatedPolicy.remove();
									}
								}
							}
							StdRemovedPolicy sRPolicy = new StdRemovedPolicy();
							sRPolicy.setPolicyName(newRemovedPolicy.getPolicyName());
							sRPolicy.setVersionNo(newRemovedPolicy.getVersionNo());
							removedPolicies.add(sRPolicy);
						}
					}
					notificationRecord.setRemovedPolicies(removedPolicies);
					notificationRecord.setLoadedPolicies(updatedPolicies);
				}
				if(!notificationRecord.getLoadedPolicies().isEmpty() && !notificationRecord.getRemovedPolicies().isEmpty()){
					notificationRecord.setNotificationType(NotificationType.BOTH);
				}else if(!notificationRecord.getLoadedPolicies().isEmpty()){
					notificationRecord.setNotificationType(NotificationType.UPDATE);
				}else if(!notificationRecord.getRemovedPolicies().isEmpty()){
					notificationRecord.setNotificationType(NotificationType.REMOVE);
				}
			}
		}
	}
	
	// This should return the current Notification Record. 
	public static PDPNotification getNotificationRecord(){
		return notificationRecord;
	}
}
