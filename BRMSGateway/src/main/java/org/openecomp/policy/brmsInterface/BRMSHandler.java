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

package org.openecomp.policy.brmsInterface;

import java.util.Collection;

import org.openecomp.policy.api.ConfigRequestParameters;
import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyException;
import org.openecomp.policy.api.RemovedPolicy;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.utils.BackUpHandler;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
//import org.apache.log4j.Logger;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;

/**
 * BRMSHandler: Notification Handler which listens for PDP Notifications. 
 * Take action only for BRMS policies. 
 * 
 * @version 0.3
 */
public class BRMSHandler implements BackUpHandler{
	
	private static final Logger logger = FlexLogger.getLogger(BRMSHandler.class.getName());
	
	private BRMSPush bRMSPush = null;
	
	public BRMSHandler(String propertiesFile) throws PolicyException{
		setBRMSPush(new BRMSPush(propertiesFile, this));
	}

	public void setBRMSPush(BRMSPush brmsPush) {
		this.bRMSPush = brmsPush;
	}

	@Override
	public void notificationReceived(PDPNotification notification) {
		logger.info("Notification Recieved");
		logger.info(notification.getNotificationType().toString());
		Boolean flag = BRMSPush.getBackUpMonitor().getFlag();
		bRMSPush.initiate(flag);
		if(flag){
			logger.info("Master Application performing on Notification ");
			runOnNotification(notification);
		}else{
			logger.info("Slave application Skipping Notification.. ");
		}
	}
	
	private void removedPolicies(Collection<RemovedPolicy> removedPolicies){
		Boolean removed = false;
		logger.info("Removed Policies");
		for(RemovedPolicy removedPolicy: removedPolicies){
			logger.info(removedPolicy.getPolicyName());
			logger.info(removedPolicy.getVersionNo());
			if(removedPolicy.getPolicyName().contains("_BRMS_")){
				try{
					logger.info("Policy Removed with this policy Name : " + removedPolicy.getPolicyName());
					bRMSPush.removeRule(removedPolicy.getPolicyName());
					removed = true;
				}catch(Exception e){
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Rertriving policy failed " + e.getMessage());
				}
			}
		}
		if(removed){
			bRMSPush.pushRules();
		}
	}
	
	public void runOnNotification(PDPNotification notification){
		if(notification.getNotificationType().equals(NotificationType.REMOVE)){
			removedPolicies(notification.getRemovedPolicies());
		}else if(notification.getNotificationType().equals(NotificationType.UPDATE)|| notification.getNotificationType().equals(NotificationType.BOTH)){
			logger.info("Updated Policies: \n");
			for(LoadedPolicy updatedPolicy: notification.getLoadedPolicies()){
				logger.info("policyName : " + updatedPolicy.getPolicyName());
				logger.info("policyVersion :" + updatedPolicy.getVersionNo());
				logger.info("Matches: " + updatedPolicy.getMatches());
				// Checking the Name is correct or not. 
				if(updatedPolicy.getPolicyName().contains("_BRMS_")){
					try{
						PolicyEngine policyEngine = getPolicyEngine();
						if(policyEngine!=null){
							ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
							configRequestParameters.setPolicyName(updatedPolicy.getPolicyName());
							Collection<PolicyConfig> policyConfigs = policyEngine.getConfig(configRequestParameters);
							for(PolicyConfig policyConfig: policyConfigs){
								if(policyConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_RETRIEVED)){
									logger.info("Policy Retrieved with this Name notified: " + policyConfig.getPolicyName());
								    bRMSPush.addRule(policyConfig.getPolicyName(),policyConfig.toOther(),policyConfig.getResponseAttributes());
								}else{
									logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Fail to retrieve policy so rule will not be pushed to PolicyRepo !!!!\n\n");
								}
							}
						}
					}catch(Exception e){
						logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Rertriving policy failed " + e.getMessage());
					}
				}
			}
			bRMSPush.pushRules();
		}
	}

	public PolicyEngine getPolicyEngine() {
		return BRMSGateway.getPolicyEngine();
	}
}
