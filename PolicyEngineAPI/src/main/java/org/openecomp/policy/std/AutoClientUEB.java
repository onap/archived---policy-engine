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

import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import org.openecomp.policy.api.NotificationHandler;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.std.StdPDPNotification;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaClientBuilders.ConsumerBuilder;
/**
 * Create a UEB Consumer to receive policy update notification.
 * 
 * 
 *
 */
@SuppressWarnings("deprecation")
public class AutoClientUEB implements Runnable  {
	private static StdPDPNotification notification = null;
	private static NotificationScheme scheme = null;
	private static NotificationHandler handler = null;
	private static String topic = null;
	private static String url = null;
	private static boolean status = false; 
	private static Logger logger = FlexLogger.getLogger(AutoClientUEB.class.getName());
	private static String notficatioinType = null;
	private static CambriaConsumer CConsumer = null;
	private static String apiKey = null;
	private static String apiSecret = null;
	private static List<String> uebURLList = null; 
	public volatile boolean isRunning = false;
    

	public AutoClientUEB(String url, List<String> uebURLList, String apiKey, String apiSecret) {
	       AutoClientUEB.url = url;
	       AutoClientUEB.uebURLList = uebURLList;
	       AutoClientUEB.apiKey = apiKey;
	       AutoClientUEB.apiKey = apiKey;
	}

	public void setAuto(NotificationScheme scheme,
			NotificationHandler handler) {
		AutoClientUEB.scheme = scheme;
		AutoClientUEB.handler = handler;
	}

	public static void setScheme(NotificationScheme scheme) {
		AutoClientUEB.scheme = scheme;
	}
	
	public static boolean getStatus(){
		return AutoClientUEB.status;
	}

	public static String getURL() {
		return AutoClientUEB.url;
	}
	
	public static String getNotficationType(){
		return AutoClientUEB.notficatioinType;
	}

	public synchronized boolean isRunning() {
		return this.isRunning;
	}
	
	public synchronized void terminate() {
		this.isRunning = false;
	}
	
	@Override
	public void run() {
		synchronized(this) {
			this.isRunning = true;
		}
		String group =  UUID.randomUUID ().toString ();
		String id = "0";
		//String topic = null;
		// Stop and Start needs to be done.
		if (scheme != null && handler!=null) {
			if (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS) || scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
				URL aURL;
				try {
					aURL = new URL(AutoClientUEB.topic);
					topic = aURL.getHost() + aURL.getPort();
				} catch (MalformedURLException e) {
					topic = AutoClientUEB.url.replace("[:/]", "");
				}
				
				//TODO  create a loop to listen for messages from UEB cluster
					
				try {
					//CConsumer = CambriaClientFactory.createConsumer ( null, uebURLList, topic, group, id, 15*1000, 1000 );
					ConsumerBuilder builder = new CambriaClientBuilders.ConsumerBuilder();
					builder.knownAs(group, id)
					.usingHosts(uebURLList)
					.onTopic(topic)
					.waitAtServer(15*1000)
					.receivingAtMost(1000)
					.authenticatedBy(apiKey, apiSecret);
					
					 CConsumer = builder.build();
					
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (GeneralSecurityException e1) {
					e1.printStackTrace();
				}
				while (this.isRunning() )
				{
					try {
						for ( String msg : CConsumer.fetch () )
						{		
							logger.debug("Auto Notification Recieved Message " + msg + " from UEB cluster : " + uebURLList.toString());
							notification = NotificationUnMarshal.notificationJSON(msg);
							callHandler();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.debug(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Error in processing UEB message" + e.getMessage());
					}

				}
				logger.debug("Stopping UEB Consumer loop will not logger fetch messages from the cluster");
			}
		}
	}

	private static void callHandler() {
		if (handler != null && scheme != null) {
			if (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS)) {
				boolean removed = false, updated = false;
				if (notification.getRemovedPolicies() != null && !notification.getRemovedPolicies().isEmpty()) {
					removed = true;
				}
				if (notification.getLoadedPolicies() != null && !notification.getLoadedPolicies().isEmpty()) {
					updated = true;
				}
				if (removed && updated) {
					notification.setNotificationType(NotificationType.BOTH);
				} else if (removed) {
					notification.setNotificationType(NotificationType.REMOVE);
				} else if (updated) {
					notification.setNotificationType(NotificationType.UPDATE);
				}
				handler.notificationReceived(notification);
			} else if (scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
				PDPNotification newNotification = MatchStore.checkMatch(notification);
				if (newNotification.getNotificationType() != null) {
					handler.notificationReceived(newNotification);
				}
			}
		}
	}

}
