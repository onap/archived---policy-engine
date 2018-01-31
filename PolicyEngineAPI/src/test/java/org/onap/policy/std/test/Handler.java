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

package org.onap.policy.std.test;

import java.util.Collection;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class Handler implements NotificationHandler{

	private static final Logger LOGGER	= FlexLogger.getLogger(Handler.class);

	@Override
	public void notificationReceived(PDPNotification notification) {
		System.out.println("Notification Received...");
		System.out.println(notification.getNotificationType());
		if(notification.getNotificationType().equals(NotificationType.REMOVE)){
			System.out.println("Removed Policies: \n");
			for(RemovedPolicy removedPolicy: notification.getRemovedPolicies()){
				System.out.println(removedPolicy.getPolicyName());
				System.out.println(removedPolicy.getVersionNo());
			}
		}else if(notification.getNotificationType().equals(NotificationType.UPDATE)){
			System.out.println("Updated Policies: \n");
			for(LoadedPolicy updatedPolicy: notification.getLoadedPolicies()){
				System.out.println("policyName : " + updatedPolicy.getPolicyName());
				System.out.println("policyVersion :" + updatedPolicy.getVersionNo());
				if(updatedPolicy.getPolicyName().contains(".Config_")){
					System.out.println("Matches: " + updatedPolicy.getMatches());
					System.out.println("UpdateType: "+ updatedPolicy.getUpdateType());
					// Checking the Name is correct or not.
					try {
						PolicyEngine policyEngine = new PolicyEngine("config.properties");
						@SuppressWarnings("deprecation")
						Collection<PolicyConfig> policyConfigs = policyEngine.getConfigByPolicyName(updatedPolicy.getPolicyName());
						for(PolicyConfig policyConfig: policyConfigs){
							if(policyConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_RETRIEVED)){
								System.out.println("Policy Retrieved with this Name notified. ");
							}else{
								System.err.println("\n\n Fail to retrieve policy !!!!\n\n");
							}
							// Also Test this case.
							if(policyConfig.getPolicyName().equals(updatedPolicy.getPolicyName())){
								System.out.println("Policy Name is good. ");
							}else{
								System.err.println("\n\n Fail to check Name \n\n");
							}
						}
					} catch (PolicyEngineException e) {
						LOGGER.error("Exception Occured"+e);
					} catch (PolicyConfigException e) {
						LOGGER.error("Exception Occured"+e);
					}
				}
			}
		}else if(notification.getNotificationType().equals(NotificationType.BOTH)){
			System.out.println("Both updated and Removed Notification: \n");
			System.out.println("Removed Policies: \n");
			for(RemovedPolicy removedPolicy: notification.getRemovedPolicies()){
				System.out.println(removedPolicy.getPolicyName());
				System.out.println(removedPolicy.getVersionNo());
			}
			System.out.println("Updated Policies: \n");
			for(LoadedPolicy updatedPolicy: notification.getLoadedPolicies()){
				System.out.println("policyName : " + updatedPolicy.getPolicyName());
				System.out.println("policyVersion :" + updatedPolicy.getVersionNo());
				System.out.println("Matches: " + updatedPolicy.getMatches());
				System.out.println("UpdateType: "+ updatedPolicy.getUpdateType());
				// Checking the Name is correct or not.
				try {
					PolicyEngine policyEngine = new PolicyEngine("config.properties");
					@SuppressWarnings("deprecation")
					Collection<PolicyConfig> policyConfigs = policyEngine.getConfigByPolicyName(updatedPolicy.getPolicyName());
					for(PolicyConfig policyConfig: policyConfigs){
						if(policyConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_RETRIEVED)){
							System.out.println("Policy Retrieved with this Name notified. ");
						}else{
							System.err.println("\n\n Fail to retrieve policy !!!!\n\n");
						}
						// Also Test this case.
						if(policyConfig.getPolicyName().equals(updatedPolicy.getPolicyName())){
							System.out.println("Policy Name is good. ");
						}else{
							System.err.println("\n\n Fail to check Name \n\n");
						}
					}
				} catch (PolicyEngineException e) {
					LOGGER.error("Exception Occured"+e);
				} catch (PolicyConfigException e) {
					LOGGER.error("Exception Occured"+e);
				}
			}
		}
	}


}
