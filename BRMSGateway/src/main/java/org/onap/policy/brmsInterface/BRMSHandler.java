/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.brmsInterface;

import java.util.ArrayList;
import java.util.Collection;

import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.BackUpHandler;
import org.onap.policy.xacml.api.XACMLErrorConstants;

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
	
	/*
	 * This Method is executed upon notification by the Policy Engine API Notification. 
	 * (non-Javadoc)
	 * @see org.onap.policy.utils.BackUpHandler#notificationReceived(org.onap.policy.api.PDPNotification)
	 */
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
	
	/*
	 * Executed when a policy is removed from PDP.  
	 */
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
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Rertriving policy failed " + e.getMessage(), e);
				}
			}
		}
		Boolean failureFlag;
		int i = 0;
		do{
			failureFlag = false;
			if(removed){
				try{
					bRMSPush.pushRules();
				}catch(PolicyException e){
					//Upon Notification failure 
					failureFlag = true;
					bRMSPush.rotateURLs();
					logger.error("Failure during Push Operation " , e);
				}
			}
			i++;
		}while(failureFlag && i< bRMSPush.urlListSize());
	}
	
	/*
	 * This method is executed if BRMSGW is "MASTER" 
	 * (non-Javadoc)
	 * @see org.onap.policy.utils.BackUpHandler#runOnNotification(org.onap.policy.api.PDPNotification)
	 */
	@Override
	public void runOnNotification(PDPNotification notification){
		// reset the BRMSPush data structures
		bRMSPush.resetDS();
		if(notification.getNotificationType().equals(NotificationType.REMOVE)){
			removedPolicies(notification.getRemovedPolicies());
		}else if(notification.getNotificationType().equals(NotificationType.UPDATE)|| notification.getNotificationType().equals(NotificationType.BOTH)){
			logger.info("Updated Policies: \n");
			ArrayList<PolicyConfig> brmsPolicies = addedPolicies(notification);
			Boolean successFlag = false;
			for(int i=0; !successFlag && i< bRMSPush.urlListSize(); i++){
				if(i!=0){
					for(PolicyConfig policyConfig: brmsPolicies){
						logger.info("Policy Retry with this Name notified: " + policyConfig.getPolicyName());
						bRMSPush.addRule(policyConfig.getPolicyName(),policyConfig.toOther(),policyConfig.getResponseAttributes());
					}
				}
				try{
					bRMSPush.pushRules();
					successFlag = true;
				}catch(PolicyException e){
					//Upon Notification failure 
					successFlag = false;
					bRMSPush.rotateURLs();
					logger.error("Failure during Push Operation " , e);
				}
			}
		}
	}

	/*
	 * Executed when a policy is added to PDP.  
	 */
	private ArrayList<PolicyConfig> addedPolicies(PDPNotification notification) {
		ArrayList<PolicyConfig> result = new ArrayList<>();
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
								result.add(policyConfig);
								bRMSPush.addRule(policyConfig.getPolicyName(),policyConfig.toOther(),policyConfig.getResponseAttributes());
							}else{
								logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Fail to retrieve policy so rule will not be pushed to PolicyRepo !!!!\n\n");
							}
						}
					}
				}catch(Exception e){
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+"Rertriving policy failed " + e.getMessage(), e);
				}
			}
		}
		return result;
	}

	public PolicyEngine getPolicyEngine() {
		return BRMSGateway.getPolicyEngine();
	}
}
