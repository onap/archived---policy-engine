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
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

//import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.std.StdPDPNotification;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import org.openecomp.policy.common.logging.flexlogger.*; 

@ClientEndpoint
public class ManualClientEnd {
	private static CountDownLatch latch;
	private static StdPDPNotification notification = null;
	private static String resultJson = null;
	private static Logger logger = FlexLogger.getLogger(ManualClientEnd.class.getName());
	
	public static void start(String url) {
		latch = new CountDownLatch(1);
		ClientManager client = ClientManager.createClient();
		if(url.contains("https")){
			url = url.replaceAll("https", "wss");
		}else {
			url = url.replaceAll("http", "ws");
		}
		try {
			client.connectToServer(ManualClientEnd.class, new URI(url+"notifications"));
			latch.await();
		} catch (DeploymentException | URISyntaxException | InterruptedException e) {
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
		} catch (IOException e) {
			logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
		}
	}

	public static PDPNotification result(NotificationScheme scheme) {
		if (resultJson == null || notification == null) {
			logger.debug("No Result" );
			return null;
		} else {
			if(scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)) {
				boolean removed = false, updated = false; 
				if(notification.getRemovedPolicies()!=null && !notification.getRemovedPolicies().isEmpty()){
					removed = true;
				}
				if(notification.getLoadedPolicies()!=null && !notification.getLoadedPolicies().isEmpty()){
					updated = true;
				}
				if(removed && updated) {
					notification.setNotificationType(NotificationType.BOTH);
				}else if(removed){
					notification.setNotificationType(NotificationType.REMOVE);
				}else if(updated){
					notification.setNotificationType(NotificationType.UPDATE);
				}
				return notification;
			}else if(scheme.equals(NotificationScheme.MANUAL_NOTIFICATIONS)) {
				return MatchStore.checkMatch(notification);
			}else {
				return null;
			}
		}
	}
	
	// WebSockets Code.. 
	@OnOpen
	public void onOpen(Session session) throws IOException {
		logger.info("Session Started with : " + session.getId());
		session.getBasicRemote().sendText("Manual");
	}
	
	@OnError
	public void onError(Session session, Throwable e) {
		logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in: "+ session.getId());
		latch.countDown();
	}
	
	@OnClose
	public void onClose(Session session) {
		logger.info("Session ended with "+ session.getId());
		latch.countDown();
	}
	
	@OnMessage
	public void onMessage(String message, Session session){
		logger.debug(" Manual Notification Recieved Message : " + message +" Session info is : "+ session.getId());
		resultJson = message;
		try {
			notification = NotificationUnMarshal.notificationJSON(message);
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			latch.countDown();
		}
		try {
			session.close();
		} catch (IOException e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			latch.countDown();
		} // For Manual Client..
		latch.countDown();
	}
}
