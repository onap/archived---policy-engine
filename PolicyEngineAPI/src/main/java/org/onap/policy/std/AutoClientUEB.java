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

package org.onap.policy.std;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.nsa.cambria.client.CambriaClientBuilders.ConsumerBuilder;
import com.att.nsa.cambria.client.CambriaConsumer;
/**
 * Create a UEB Consumer to receive policy update notification.
 * 
 * 
 *
 */
public class AutoClientUEB implements Runnable  {
	private static StdPDPNotification notification = null;
	private static NotificationScheme scheme = null;
	private static NotificationHandler handler = null;
	private static String topic = null;
	private static String url = null;
	private static boolean status = false; 
	private static Logger logger = FlexLogger.getLogger(AutoClientUEB.class.getName());
	private static String notficatioinType = null;
	private static CambriaConsumer cConsumer = null;
	private static String apiKey = null;
	private static String apiSecret = null;
	private static List<String> uebURLList = null; 
	private volatile boolean running = false;
    

	public AutoClientUEB(String url, List<String> uebURLList, String apiKey, String apiSecret) {
	       AutoClientUEB.url = url;
	       AutoClientUEB.uebURLList = uebURLList;
	       AutoClientUEB.apiKey = apiKey;
	       AutoClientUEB.apiKey = apiKey;
	}

	public static void setAuto(NotificationScheme scheme,
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
		return this.running;
	}
	
	public synchronized void terminate() {
		this.running = false;
	}
	
	@Override
	public void run() {
		synchronized(this) {
			this.running = true;
		}
		String group =  UUID.randomUUID ().toString ();
		String id = "0";
		// Stop and Start needs to be done.
		if (scheme == null || handler == null ||
				! (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS) || scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) ) {
			return;
		}
		URL aURL;
		try {
			aURL = new URL(AutoClientUEB.topic);
			setTopic(aURL.getHost() + aURL.getPort());
		} catch (MalformedURLException e) {
			setTopic(AutoClientUEB.url.replace("[:/]", ""));
		}
		try {
			ConsumerBuilder builder = new CambriaClientBuilders.ConsumerBuilder();
			builder.knownAs(group, id)
			.usingHosts(uebURLList)
			.onTopic(topic)
			.waitAtServer(15*1000)
			.receivingAtMost(1000)
			.authenticatedBy(apiKey, apiSecret);
			setConsumer(builder.build()); 
		} catch (Exception e1) {
			logger.error("Exception Occured" + e1);
		} 
        while (this.isRunning()) {
            try {
                for (String msg : cConsumer.fetch()) {
                    logger.debug("Auto Notification Recieved Message " + msg + " from UEB cluster : "
                            + uebURLList.toString());
                    setNotification(NotificationUnMarshal.notificationJSON(msg));
                    callHandler();
                }
            } catch (Exception e) {
                logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Error in processing UEB message"
                        + e.getMessage(), e);
            }

        }
		logger.debug("Stopping UEB Consumer loop will not logger fetch messages from the cluster");
	}

	private static void setNotification(StdPDPNotification notificationJSON) {
	    notification = notificationJSON;
    }

    private static void setConsumer(CambriaConsumer build) {
	    cConsumer = build;
    }

    private static void setTopic(String topic) {
	    AutoClientUEB.topic = topic;
    }

    private static void callHandler() {
		if (handler != null && scheme != null) {
			if (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS)) {
				boolean removed = false;
				boolean updated = false;
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
