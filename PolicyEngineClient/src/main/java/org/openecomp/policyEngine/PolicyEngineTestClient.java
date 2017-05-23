/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
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

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.openecomp.policyEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.openecomp.policy.api.DecisionResponse;
import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.api.PolicyDecisionException;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyEventException;
import org.openecomp.policy.api.PolicyResponse;
import org.openecomp.policy.api.RemovedPolicy;

/**
 * Class contains static functions which make call to policy engine using API.
 * This class is used by generalTestClient.java
 * 
 * 
 * @version 1.0
 *
 */
public class PolicyEngineTestClient {
	/**
	 * This fuction make API call to policy engine to get config. And returns
	 * policy name, policy version and policy configStatus
	 * 
	 * @param org.openecomp.policyEngine
	 * @param eCOMPComponentName
	 * @param configName
	 * @param configAttributes
	 * @return
	 */

	@SuppressWarnings("deprecation")
	public static ArrayList<String> getConfig(PolicyEngine policyEngine,
			String eCOMPComponentName, String configName,
			Map<String, String> configAttributes) {
		ArrayList<String> resultReceived = new ArrayList<>();
		try {
			UUID requestID = UUID.randomUUID();
			Collection<PolicyConfig> policyConfigs;
			if (configName == null) {
				policyConfigs = policyEngine.getConfig(eCOMPComponentName, requestID);
			} else {
				if (configAttributes == null) {
					policyConfigs = policyEngine.getConfig(eCOMPComponentName,
							configName, requestID);
				} else {
					
					policyConfigs = policyEngine.getConfig(eCOMPComponentName,
							configName, configAttributes, requestID);
				}
			}
			if (policyConfigs != null && !policyConfigs.isEmpty()) {
				for (PolicyConfig policyConfig : policyConfigs) {
					resultReceived.add("Policy Name: "
							+ policyConfig.getPolicyName()
							+ " Policy version: "
							+ policyConfig.getPolicyVersion() + " - "
							+ policyConfig.getPolicyConfigStatus());
				}
			}
		} catch (PolicyConfigException e) {
//			logger.error("Exception Occured"+e);
			resultReceived.add(""+e);
		}
		return resultReceived;
	}

	/**
	 * This functions make API call to policy engine to get decision. And
	 * returns policy Decision
	 * 
	 * @param org.openecomp.policyEngine
	 * @param eCOMPComponentName
	 * @param decisionAttributes
	 * @return
	 */
	public static ArrayList<String> getDecision(PolicyEngine policyEngine,
			String eCOMPComponentName, Map<String, String> decisionAttributes) {
		ArrayList<String> resultReceived = new ArrayList<>();
		// Decision example
		try {
			UUID requestID = UUID.randomUUID();
			@SuppressWarnings("deprecation")
			DecisionResponse policyDecision = policyEngine.getDecision(
					eCOMPComponentName, decisionAttributes, requestID);
			resultReceived.add(policyDecision.getDecision().toString());
		} catch (PolicyDecisionException e) {
//			logger.error("Exception Occured"+e);
			resultReceived.add(""+e);
		}
		return resultReceived;
	}

	/**
	 * This function makes API call to policy engine to get action. And returns
	 * responseMessage and responseStatus
	 * 
	 * @param org.openecomp.policyEngine
	 * @param eventAttributes
	 * @return
	 */
	public static ArrayList<String> getAction(PolicyEngine policyEngine,
			Map<String, String> eventAttributes) {
		ArrayList<String> resultReceived = new ArrayList<>();
		try {
			UUID requestID = UUID.randomUUID();
			@SuppressWarnings("deprecation")
			Collection<PolicyResponse> policyResponses = policyEngine
					.sendEvent(eventAttributes, requestID);
			if (policyResponses != null && !policyResponses.isEmpty()) {
				for (PolicyResponse policyResponse : policyResponses) {
					resultReceived.add(policyResponse
							.getPolicyResponseMessage()
							+ " : "
							+ policyResponse.getPolicyResponseStatus());
				}
			}
		} catch (PolicyEventException e) {
//			logger.error("Exception Occured"+e);
			resultReceived.add(""+e);
		}
		return resultReceived;
	}

	/**
	 * This function makes API call to policy engine to get manual
	 * notifications.
	 * 
	 * @param org.openecomp.policyEngine
	 */

	public static void getManualNotifications(PolicyEngine policyEngine) {
		policyEngine.setScheme(NotificationScheme.MANUAL_ALL_NOTIFICATIONS);
		System.out.println(policyEngine.getNotification().getNotificationType());
		for (LoadedPolicy updated : policyEngine.getNotification().getLoadedPolicies()) {
			System.out.println(updated.getPolicyName());
			System.out.println(updated.getVersionNo());
			System.out.println(updated.getMatches());
		}
		for (RemovedPolicy removed : policyEngine.getNotification()
				.getRemovedPolicies()) {
			System.out.println(removed.getPolicyName());
			System.out.println(removed.getVersionNo());
		}
	}

	/**
	 * This function makes API call to policy engine to get automatic
	 * notifications.
	 * 
	 * @param org.openecomp.policyEngine
	 */
	public static void getAutoNotifications(PolicyEngine policyEngine) {
		Handler handler = new Handler();
		policyEngine.setNotification(NotificationScheme.AUTO_ALL_NOTIFICATIONS,
				handler);
		//
		System.out.println("Enter a any key to exit");
		try {
			System.in.read();
		} catch (IOException e) {
			//
		}
	}

}
