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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

//import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.openecomp.policy.api.NotificationHandler;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.std.NotificationStore;
import org.openecomp.policy.std.StdPDPNotification;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.openecomp.policy.common.logging.flexlogger.*; 

@ClientEndpoint
public class AutoClientEnd {
	private static StdPDPNotification notification = null;
	private static StdPDPNotification oldNotification = null;
	private static ClientManager client = null;
	private static NotificationScheme scheme = null;
	private static NotificationHandler handler = null;
	private static String url = null;
	private static Session session = null;
	private static boolean status = false; 
	private static boolean stop = false;
	private static boolean message = false;
	private static boolean error = false;
	private static Logger logger = FlexLogger.getLogger(AutoClientEnd.class.getName());
	
	public static void setAuto(NotificationScheme scheme,
			NotificationHandler handler) {
		AutoClientEnd.scheme = scheme;
		AutoClientEnd.handler = handler;
	}

	public static void setScheme(NotificationScheme scheme) {
		AutoClientEnd.scheme = scheme;
	}
	
	public static boolean getStatus(){
		return AutoClientEnd.status;
	}

	public static String getURL() {
		return AutoClientEnd.url;
	}

	public static void start(String url) {
		AutoClientEnd.url = url;
		// Stop and Start needs to be done.
		if (scheme != null && handler!=null) {
			if (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS) || scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
				if (AutoClientEnd.client == null) {
					client = ClientManager.createClient();
					if(url.contains("https")){
						url = url.replaceAll("https", "wss");
					}else {
						url = url.replaceAll("http", "ws");
					}
					try {
						logger.info("Starting Auto Notification with the PDP server : " + url);
						client.connectToServer(AutoClientEnd.class, new URI(url	+ "notifications"));
						status = true;
						if(error){
							// The URL's will be in Sync according to design Spec. 
							ManualClientEnd.start(AutoClientEnd.url);
							StdPDPNotification notification = NotificationStore.getDeltaNotification((StdPDPNotification)ManualClientEnd.result(NotificationScheme.MANUAL_ALL_NOTIFICATIONS));
							if(notification.getNotificationType()!=null){
								if(oldNotification!=notification){
									oldNotification= notification;
									AutoClientEnd.notification = notification;
									callHandler();
								}
							}
							error = false;
						}
						//
					} catch (DeploymentException | IOException | URISyntaxException e) {
						logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
						client = null;
						status = false;
						changeURL();
					}
				}
			}
		}
	}
	
	private static void changeURL(){
		// Change the PDP if it is not Up. 
		StdPolicyEngine.rotatePDPList();
		start(StdPolicyEngine.getPDPURL());
	}

	public static void stop() {
		if (client != null) {
			client.shutdown();
			if(session!=null){
				try {
					stop = true;
					logger.info("\n Closing Auto Notification WebSocket Connection.. ");
					session.close();
					session = null;
				} catch (IOException e) {
					//
				}
			}
			client = null;
			status = false;
			stop = false;
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
				try{
					handler.notificationReceived(notification);
				}catch (Exception e){
					logger.error("Error in Clients Handler Object : " + e.getMessage());
				}
			} else if (scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
				PDPNotification newNotification = MatchStore.checkMatch(notification);
				if (newNotification.getNotificationType() != null) {
					handler.notificationReceived(newNotification);
				}
			}
		}
	}

	// WebSockets Code..
	@OnOpen
	public void onOpen(Session session) throws IOException {
		// session.getBasicRemote().sendText("Connected to Client with Session: "
		// + session.getId());
		logger.debug("Auto Notification Session Started... " + session.getId());
		if(AutoClientEnd.session == null){
			AutoClientEnd.session = session;
		}
	}

	@OnError
	public void onError(Session session, Throwable e) {
		// trying to Restart by self.
		logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Session Error.. "+ session.getId() + "\n Error is : " + e );
		// logger.error("Exception Occured"+e);
		stop();
		if (url != null) {
			client = null;
			status = false;
			error= true;
			start(url);
		}
	}

	@OnClose
	public void onClose(Session session) {
		logger.info("Session ended with "+ session.getId());
		if(!stop && !message){
			// This Block of code is executed if there is any Network Failure or if the Notification is Down. 
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Disconnected from Notification Server");
			client = null;
			status = false;
			AutoClientEnd.session=null;
			// Try to connect Back to available PDP.
			error = true;
			start(url);
		}
		AutoClientEnd.message=false;
	}

	@OnMessage
	public void onMessage(String message, Session session) throws JsonParseException, JsonMappingException, IOException {
		AutoClientEnd.message = true;
		logger.debug("Auto Notification Recieved Message " + message + " Session info is : " + session.getId());
		try {
			notification = NotificationUnMarshal.notificationJSON(message);
		} catch (Exception e) {
			logger.error("PE500 " + e);
		}
		if(AutoClientEnd.session == session){
			try{
				NotificationStore.recordNotification(notification);
			}catch(Exception e){
				logger.error(e);
			}
			if(oldNotification!=notification){
				oldNotification= notification;
				callHandler();
			}
		}else{
			session.close();
		}
		AutoClientEnd.message = false;
	}
}
