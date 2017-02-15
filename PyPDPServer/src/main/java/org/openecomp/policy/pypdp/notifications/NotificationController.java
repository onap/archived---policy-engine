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

package org.openecomp.policy.pypdp.notifications;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationHandler;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.RemovedPolicy;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationController implements NotificationHandler{
	private static final Log logger	= LogFactory.getLog(NotificationController.class);
	private static Notification record = new Notification();
	//private static CountDownLatch latch;
	
	@Override
	public void notificationReceived(PDPNotification notification) {
		//latch = new CountDownLatch(1);
		if(notification!=null){
			// Take this into our Record holder for polling requests. 
			NotificationServer.setUpdate(record(notification));
			// Send the Update as is for AUTO clients. 
			ObjectWriter ow = new ObjectMapper().writer();
			try{
				String json = ow.writeValueAsString(notification);
				System.out.println("\n Notification: "+json);
				logger.info(json);
				NotificationServer.sendNotification(json);
				//latch.await();
			} catch (JsonProcessingException e) {
				logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e.getMessage());
				// TODO:EELF Cleanup - Remove logger
				PolicyLogger.error(MessageCodes.ERROR_SCHEMA_INVALID, e, "");
			}
			
		}
	}
	
	public static String record(PDPNotification notification) {
		// Initialization with updates. 
		if(record.getRemovedPolicies()== null){
			record.setRemovedPolicies(notification.getRemovedPolicies());
		}
		if(record.getLoadedPolicies()== null){
			record.setLoadedPolicies(notification.getLoadedPolicies());
		}
		// Check if there is anything new and update the record..
		if(record.getLoadedPolicies()!= null || record.getRemovedPolicies()!=null) {
			HashSet<RemovedPolicy> removedPolicies = (HashSet<RemovedPolicy>) record.getRemovedPolicies();
			HashSet<LoadedPolicy> updatedPolicies = (HashSet<LoadedPolicy>) record.getLoadedPolicies();
			// Checking with New updated policies.
			if(notification.getLoadedPolicies()!= null && !notification.getLoadedPolicies().isEmpty()) {
				for( LoadedPolicy newUpdatedPolicy : notification.getLoadedPolicies()) {
					// If it was removed earlier then we need to remove from our record
					Iterator<RemovedPolicy> oldRemovedPolicy = removedPolicies.iterator();
					while(oldRemovedPolicy.hasNext()){
						RemovedPolicy policy = oldRemovedPolicy.next(); 
						if(newUpdatedPolicy.getPolicyName().equals(policy.getPolicyName())) {
							if(newUpdatedPolicy.getVersionNo().equals(policy.getVersionNo())) {
								oldRemovedPolicy.remove();
							}
						}
					}
					// If it was previously updated need to Overwrite it to the record.
					Iterator<LoadedPolicy> oldUpdatedPolicy = updatedPolicies.iterator();
					while(oldUpdatedPolicy.hasNext()){
						LoadedPolicy policy = oldUpdatedPolicy.next();
						if(newUpdatedPolicy.getPolicyName().equals(policy.getPolicyName())) {
							if(newUpdatedPolicy.getVersionNo().equals(policy.getVersionNo())) {
								oldUpdatedPolicy.remove();
							}
						}
					}
					updatedPolicies.add(newUpdatedPolicy);
				}
			}
			// Checking with New Removed policies.
			if(notification.getRemovedPolicies()!= null && !notification.getRemovedPolicies().isEmpty()) {
				for( RemovedPolicy newRemovedPolicy : notification.getRemovedPolicies()) {
					// If it was removed earlier then we need to remove from our record
					Iterator<RemovedPolicy> oldRemovedPolicy = removedPolicies.iterator();
					while(oldRemovedPolicy.hasNext()){
						RemovedPolicy policy = oldRemovedPolicy.next(); 
						if(newRemovedPolicy.getPolicyName().equals(policy.getPolicyName())) {
							if(newRemovedPolicy.getVersionNo().equals(policy.getVersionNo())) {
								oldRemovedPolicy.remove();
							}
						}
					}
					// If it was previously updated need to Overwrite it to the record.
					Iterator<LoadedPolicy> oldUpdatedPolicy = updatedPolicies.iterator();
					while(oldUpdatedPolicy.hasNext()){
						LoadedPolicy policy = oldUpdatedPolicy.next();
						if(newRemovedPolicy.getPolicyName().equals(policy.getPolicyName())) {
							if(newRemovedPolicy.getVersionNo().equals(policy.getVersionNo())) {
								oldUpdatedPolicy.remove();
							}
						}
					}
					removedPolicies.add(newRemovedPolicy);
				}
			}
			record.setRemovedPolicies(removedPolicies);
			record.setLoadedPolicies(updatedPolicies);
		}
		// Send the Result to the caller. 
		ObjectWriter om = new ObjectMapper().writer();
		String json = null;
		try {
			json = om.writeValueAsString(record);
		} catch (JsonProcessingException e) {
			logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + e.getMessage());
			// TODO:EELF Cleanup - Remove logger
			PolicyLogger.error(MessageCodes.ERROR_SCHEMA_INVALID, e, "");
		}
		logger.info(json);
		return json;
	}

}
