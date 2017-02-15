/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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

package org.openecomp.policy.pdp.rest.notifications;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaPublisher;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.util.XACMLProperties;

import org.openecomp.policy.common.logging.flexlogger.*;


/**
 * The NotificationServer sends the Server Notifications to the Clients once there is any Event.
 * WebSockets is being used as a medium for sending Notifications.
 * UEB is being used as a medium for sending Notifications. 
 * 
 * @version 0.1
 *
 **/
@ServerEndpoint(value = "/notifications")
public class NotificationServer {
	private static final Logger logger	= FlexLogger.getLogger(NotificationServer.class);
	private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
	private static String update = null;
	private static  String hosts = null;
	private static URL aURL = null;
	
	@OnOpen
	public void openConnection(Session session) {
		logger.info("Session Connected: " + session.getId());
		queue.add(session);
	}
	
	@OnClose
	public void closeConnection(Session session) {
		queue.remove(session);
	}
	
	@OnError
	public void error(Session session, Throwable t) {
		queue.remove(session);
		logger.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Session Error for : " + session.getId() + " Error: " + t.getMessage());
		
	}
	
	@OnMessage
	public void Message(String message, Session session) {
		
		if(message.equalsIgnoreCase("Manual")) {
			try {
				session.getBasicRemote().sendText(update);
				session.close();
			} catch (IOException e) {
				logger.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in sending the Event Notification: "+ e.getMessage());
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending Message update");
			}	
		}
	}

	public static void sendNotification(String notification, String propNotificationType, String pdpURL){

		logger.debug("Notification set to " + propNotificationType);
		if (propNotificationType.equals("ueb")){
			String topic = null;
			try {
				aURL = new URL(pdpURL);
				topic = aURL.getHost() + aURL.getPort();
			} catch (MalformedURLException e1) {
				pdpURL = pdpURL.replace("/", "");
				topic = pdpURL.replace(":", "");
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in parsing out pdpURL for UEB notfication ");
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e1, "Error in parsing out pdpURL for UEB notfication ");
			}
			hosts = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_UEB_CLUSTER);
			logger.debug("Creating Publisher for host: " + hosts + " with topic: " + topic);
			CambriaPublisher pub = null;
			try {
				pub = CambriaClientFactory.createSimplePublisher (null, hosts, topic );
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				pub.send( "MyPartitionKey", notification );
			} catch (IOException e) {
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending notification update");
			}	
			pub.close();	
		}
		for(Session session: queue) {
			try {
				session.getBasicRemote().sendText(notification);
			} catch (IOException e) {
				logger.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in sending the Event Notification: "+ e.getMessage());
			}
		}
	}
	
	public static void setUpdate(String update) {
		NotificationServer.update = update;
	}
}
