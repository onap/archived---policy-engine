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

package org.openecomp.policyEngine;

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

import org.openecomp.policy.api.LoadedPolicy;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.PolicyConfig;
import org.openecomp.policy.api.PolicyConfigException;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyEngine;
import org.openecomp.policy.api.PolicyEngineException;
import org.openecomp.policy.api.PolicyType;
import org.openecomp.policy.api.RemovedPolicy;
import org.w3c.dom.Document;

public class MainClient {
	public static void main(String[] args) {
		PolicyEngine policyEngine;
		try {
			policyEngine = new PolicyEngine("config.properties");
			String eCOMPComponentName = ".*" ;
			String configName = ".*" ;
			Map<String, String> configAttributes = new HashMap<String,String>();
			configAttributes.put("java", "java");
			configAttributes.put("peach", "Tar");
			configAttributes.put("true", "false");
			configAttributes.put("small", "testPass");
			Map<String, String> eventAttributes = new HashMap<String,String>();
			eventAttributes.put("true", "true");
			eventAttributes.put("cpu", "91");
			Map<String, String> decisionAttributes = new HashMap<String,String>();
			decisionAttributes.put("Key", "Value");
			
			// Config Example 
			try {
				Collection<PolicyConfig> policyConfigs = policyEngine.getConfigByPolicyName(".*");//(eCOMPComponentName, configName, configAttributes);
				if(policyConfigs!=null && !policyConfigs.isEmpty()){
					for(PolicyConfig policyConfig: policyConfigs){
						System.out.println("\nConfig Message: "+ policyConfig.getPolicyConfigMessage());
						System.out.println("Config Status: " + policyConfig.getPolicyConfigStatus());
						System.out.println("Policy Name: "+ policyConfig.getPolicyName());
						System.out.println("policy Version: " + policyConfig.getPolicyVersion());
						/*System.out.println("policy Type: " + policyConfig.getType().toString());
						System.out.println("Matching Conditions: " +  policyConfig.getMatchingConditions());
						System.out.println("Body is : ");
						if(policyConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_RETRIEVED)){
							if(policyConfig.getType().equals(PolicyType.OTHER)){
								System.out.println(policyConfig.toOther());
							}else if(policyConfig.getType().equals(PolicyType.JSON)){
								System.out.println(policyConfig.toJSON().toString());
							}else if(policyConfig.getType().equals(PolicyType.PROPERTIES)){
								System.out.println(policyConfig.toProperties().toString());
							}else if(policyConfig.getType().equals(PolicyType.XML)){
								try {
									printDocument(policyConfig.toXML(), System.out);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}*/
					}
				}
			} catch (PolicyConfigException e) {
				e.printStackTrace();
			}
			// Action example
			/*try{
				Collection<PolicyResponse> policyResponses = org.openecomp.policyEngine.sendEvent(eventAttributes);
				if(policyResponses!=null && !policyResponses.isEmpty()){
					for(PolicyResponse policyResponse: policyResponses){
						System.out.println(policyResponse.getPolicyResponseMessage() + " : " + policyResponse.getPolicyResponseStatus());
						System.out.println(policyResponse.getActionAdvised());
						System.out.println(policyResponse.getActionTaken());
						System.out.println(policyResponse.getRequestAttributes());
					}
				}
			}catch (PolicyEventException e){
				e.printStackTrace();
			}*/
			/*// Decision example
			try{
				PolicyDecision policyDecision = org.openecomp.policyEngine.getDecision(eCOMPComponentName, decisionAttributes);
				System.out.println(policyDecision.toString());
			} catch(PolicyDecisionException e){
				e.printStackTrace();
			}*/
			
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
				//
			}
			
		} catch (PolicyEngineException e1) {
			e1.printStackTrace();
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
