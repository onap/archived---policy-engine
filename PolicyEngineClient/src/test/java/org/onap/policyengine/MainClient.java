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

package org.onap.policyengine;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigException;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.w3c.dom.Document;

public class MainClient {

	private static final Logger LOGGER	= FlexLogger.getLogger(MainClient.class);

	public static void main(String[] args) {
		PolicyEngine policyEngine;
		try {
			policyEngine = new PolicyEngine("config.properties");
			Map<String, String> configAttributes = new HashMap<>();
			configAttributes.put("java", "java");
			configAttributes.put("peach", "Tar");
			configAttributes.put("true", "false");
			configAttributes.put("small", "testPass");
			Map<String, String> eventAttributes = new HashMap<>();
			eventAttributes.put("true", "true");
			eventAttributes.put("cpu", "91");
			Map<String, String> decisionAttributes = new HashMap<>();
			decisionAttributes.put("Key", "Value");

			// Config Example
			try {
				@SuppressWarnings("deprecation")
				Collection<PolicyConfig> policyConfigs = policyEngine.getConfigByPolicyName(".*");//(onapComponentName, configName, configAttributes);
				if(policyConfigs!=null && !policyConfigs.isEmpty()){
					for(PolicyConfig policyConfig: policyConfigs){
						System.out.println("\nConfig Message: "+ policyConfig.getPolicyConfigMessage());
						System.out.println("Config Status: " + policyConfig.getPolicyConfigStatus());
						System.out.println("Policy Name: "+ policyConfig.getPolicyName());
						System.out.println("policy Version: " + policyConfig.getPolicyVersion());
					}
				}
			} catch (PolicyConfigException e) {
				LOGGER.error("Exception Occured"+e);
			}

			// Manual Notifications..
			policyEngine.setScheme(NotificationScheme.MANUAL_ALL_NOTIFICATIONS);
			if(policyEngine.getNotification()!=null){
				System.out.println(policyEngine.getNotification().getNotificationType());
				for(LoadedPolicy updated: policyEngine.getNotification().getLoadedPolicies()){
					System.out.println(updated.getPolicyName());
					System.out.println(updated.getVersionNo());
					System.out.println(updated.getMatches());
				}
				for(RemovedPolicy removed: policyEngine.getNotification().getRemovedPolicies()){
					System.out.println(removed.getPolicyName());
					System.out.println(removed.getVersionNo());
				}
			}
			// Auto Notifications..
			Handler handler = new Handler();
			policyEngine.setNotification(NotificationScheme.AUTO_ALL_NOTIFICATIONS, handler);
			//
			System.out.println("Enter a any key to exit");
			try {
				System.in.read();
			} catch (IOException e) {
				System.err.println("Exception Occured"+e);
			}

		} catch (PolicyEngineException e1) {
			System.err.println("Exception Occured"+e1);
		}
	}

	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    transformer.transform(new DOMSource(doc),
	         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
}
