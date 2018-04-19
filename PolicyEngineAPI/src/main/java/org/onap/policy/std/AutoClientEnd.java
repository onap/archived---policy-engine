/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants; 

@ClientEndpoint
public class AutoClientEnd extends WebSocketClient {
	private static StdPDPNotification notification = null;
	private static StdPDPNotification oldNotification = null;
	private static AutoClientEnd client = null;
	private static NotificationScheme scheme = null;
	private static NotificationHandler handler = null;
	private static String url = null;
	private static Session session = null;
	private static boolean status = false; 
	private static boolean stop = false;
	private static boolean message = false;
	private static boolean error = false;
	private static Logger logger = FlexLogger.getLogger(AutoClientEnd.class.getName());
	
	private AutoClientEnd(URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		// Not implemented
	}

	@Override
	public void onError(Exception arg0) {
		// Not implemented
	}

	@Override
	public void onMessage(String arg0) {
		// Not implemented
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		// Not implemented
	}

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
		
		if (scheme == null || handler == null ||
			! (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS) ||
					scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS) ) ||
			AutoClientEnd.client != null) {
			return;
		}

		if (url.contains("https")) {
			url = url.replaceAll("https", "wss");
		}
		else {
			url = url.replaceAll("http", "ws");
		}
		
		
		// Stop and Start needs to be done.
		try {
			logger.info("Starting Auto Notification with the PDP server : " + url);
			client = new AutoClientEnd(new URI(url	+ "notifications"));
			status = true;
			if(error){
				// The URL's will be in Sync according to design Spec. 
				ManualClientEnd.start(AutoClientEnd.url);
				StdPDPNotification notification = NotificationStore.getDeltaNotification((StdPDPNotification)ManualClientEnd.result(NotificationScheme.MANUAL_ALL_NOTIFICATIONS));
				if(notification.getNotificationType()!=null&&oldNotification!=notification){
				    oldNotification= notification;
				    AutoClientEnd.notification = notification;
				    callHandler();
				}
				error = false;
			}
			//
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			client = null;
			status = false;
			changeURL();
		}
	}
	
	private static void changeURL(){
		// Change the PDP if it is not Up. 
		StdPolicyEngine.rotatePDPList();
		start(StdPolicyEngine.getPDPURL());
	}

	public static void stop() {
		if (client == null) {
			return;
		}
		client.close();
		if(session!=null){
			try {
				stop = true;
				logger.info("\n Closing Auto Notification WebSocket Connection.. ");
				session.close();
				session = null;
			} catch (IOException e) {
				logger.error("Error closing websocket connection", e);
			}
		}
		client = null;
		status = false;
		stop = false;
	}

	private static void callHandler() {
		if (handler == null || scheme == null) {
			return;
		}
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
			try{
				handler.notificationReceived(notification);
			}catch (Exception e){
				logger.error("Error in Clients Handler Object : ", e);
			}
		} else if (scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
			PDPNotification newNotification = MatchStore.checkMatch(notification);
			if (newNotification.getNotificationType() != null) {
				try{
					handler.notificationReceived(newNotification);
				}catch (Exception e){
					logger.error("Error in Clients Handler Object : ", e);
				}
			}
		}
	}

	// WebSockets Code..
	@OnOpen
	public static void onOpen(Session session){
		logger.debug("Auto Notification Session Started... " + session.getId());
		if(AutoClientEnd.session == null){
			AutoClientEnd.session = session;
		}
	}

	@OnError
	public static void onError(Session session, Throwable e) {
		// trying to Restart by self.
		logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Session Error.. "+ session.getId() + "\n Error is : " + e );
		stop();
		if (url != null) {
			client = null;
			status = false;
			error= true;
			start(url);
		}
	}

	@OnClose
	public static void onClose(Session session) {
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
	public static void onMessage(String message, Session session) throws IOException {
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
