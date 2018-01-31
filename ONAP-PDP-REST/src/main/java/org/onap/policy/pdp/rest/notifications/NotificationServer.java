/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.notifications;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.services.NotificationService;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.utils.BusPublisher;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.nsa.cambria.client.CambriaBatchingPublisher;
import com.att.nsa.cambria.client.CambriaClientBuilders;
import com.att.research.xacml.util.XACMLProperties;


/**
 * The NotificationServer sends the Server Notifications to the Clients once there is any Event.
 * WebSockets is being used as a medium for sending Notifications.
 * UEB is being used as a medium for sending Notifications.
 * DMAAP is being used as a medium for sending Notifications.
 *
 * @version 0.2
 *
 **/
@ServerEndpoint(value = "/notifications")
public class NotificationServer {
	private static final Logger LOGGER	= FlexLogger.getLogger(NotificationServer.class);
	private static Queue<Session> queue = new ConcurrentLinkedQueue<>();
	private static String update = null;

	@OnOpen
	public void openConnection(Session session) {
		LOGGER.info("Session Connected: " + session.getId());
		queue.add(session);
	}

	@OnClose
	public void closeConnection(Session session) {
		queue.remove(session);
	}

	@OnError
	public void error(Session session, Throwable t) {
		queue.remove(session);
		LOGGER.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Session Error for : " + session.getId() + " Error: " + t.getMessage());

	}

	@OnMessage
	public void message(String message, Session session) {

		if(message.equalsIgnoreCase("Manual")) {
			try {
				session.getBasicRemote().sendText(update);
				session.close();
			} catch (IOException e) {
				LOGGER.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in sending the Event Notification: "+ e.getMessage() + e);
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending Message update");
			}
		}
	}

	public static void sendNotification(String notification, String propNotificationType, String pdpURL) throws PolicyEngineException, IOException, InterruptedException {

		LOGGER.debug("Notification set to " + propNotificationType);
		if (propNotificationType.equals("ueb")){

			String topic = null;
			try {
				URL aURL = new URL(pdpURL);
				topic = aURL.getHost() + aURL.getPort();
			} catch (MalformedURLException e1) {
				pdpURL = pdpURL.replace("/", "");
				topic = pdpURL.replace(":", "");
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in parsing out pdpURL for UEB notfication ");
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e1, "Error in parsing out pdpURL for UEB notfication ");
			}
			String hosts = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_SERVERS);
			String apiKey = XACMLProperties.getProperty(XACMLRestProperties.PROP_UEB_API_KEY);
			String apiSecret = XACMLProperties.getProperty(XACMLRestProperties.PROP_UEB_API_SECRET);

			LOGGER.debug("Creating Publisher for host: " + hosts + " with topic: " + topic);
			CambriaBatchingPublisher pub = null;
			try {
				if(hosts==null || topic==null || apiKey==null || apiSecret==null){
					LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
					throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "UEB properties are missing from the property file ");
				}

				hosts = hosts.trim();
				topic = topic.trim();
				apiKey = apiKey.trim();
				apiSecret = apiSecret.trim();
				pub = new CambriaClientBuilders.PublisherBuilder ()
						.usingHosts ( hosts )
						.onTopic ( topic )
						.authenticatedBy ( apiKey, apiSecret )
						.build ()
						;

			} catch (MalformedURLException e1) {
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error creating the UEB publisher" + e1.getMessage());
			} catch (GeneralSecurityException e1) {
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error creating the UEB publisher" + e1.getMessage() +e1);
			}
			if(pub != null){
				try {
					pub.send( "MyPartitionKey", notification );
				} catch (IOException e) {
					LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending notification update" + e.getMessage() + e);
				}
				// close the publisher. The batching publisher does not send events
				// immediately, so you MUST use close to send any remaining messages.
				// You provide the amount of time you're willing to wait for the sends
				// to succeed before giving up. If any messages are unsent after that time,
				// they're returned to your app. You could, for example, persist to disk
				// and try again later.
				final List<?> stuck = pub.close ( 20, TimeUnit.SECONDS );

				if (!stuck.isEmpty()){
					LOGGER.error( stuck.size() + " messages unsent" );
				}else{
					LOGGER.info( "Clean exit; all messages sent: " + notification );
				}
			}
		} else if (propNotificationType.equals("dmaap")) {

			// Setting up the Publisher for DMaaP MR
			String dmaapServers = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_SERVERS);
			String topic = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_TOPIC);
			String aafLogin = XACMLProperties.getProperty("DMAAP_AAF_LOGIN");
			String aafPassword = XACMLProperties.getProperty("DMAAP_AAF_PASSWORD");

			try {
				if(dmaapServers==null || topic==null){
					LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "DMaaP properties are missing from the property file ");
					throw new PolicyEngineException(XACMLErrorConstants.ERROR_DATA_ISSUE + "DMaaP properties are missing from the property file ");
				}

				dmaapServers= dmaapServers.trim();
				topic= topic.trim();
				aafLogin= aafLogin.trim();
				aafPassword= aafPassword.trim();

				List<String> dmaapList = null;
				if(dmaapServers.contains(",")) {
					dmaapList = new ArrayList<>(Arrays.asList(dmaapServers.split("\\s*,\\s*")));
				} else {
					dmaapList = new ArrayList<>();
					dmaapList.add(dmaapServers);
				}

				BusPublisher publisher =
						new BusPublisher.DmaapPublisherWrapper(dmaapList,
								                               topic,
								                               aafLogin,
								                               aafPassword);

				// Sending notification through DMaaP Message Router
				publisher.send( "MyPartitionKey", notification);
				LOGGER.debug("Message Published on DMaaP :" + dmaapList.get(0) + "for Topic: " + topic);
				publisher.close();

			} catch (Exception e) {
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending notification update" + e.getMessage() + e);
			}
		}

		for(Session session: queue) {
			try {
				session.getBasicRemote().sendText(notification);
			} catch (IOException e) {
				LOGGER.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in sending the Event Notification: "+ e.getMessage() + e);
			}
		}
		NotificationService.sendNotification(notification);
	}

	public static void setUpdate(String update) {
		NotificationServer.update = update;
	}

}
