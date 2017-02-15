/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.pypdp.notifications;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;


@ServerEndpoint(value = "/org.openecomp.policy.pypdp.notifications")
public class NotificationServer {
	private static final Logger logger	= FlexLogger.getLogger(NotificationServer.class);
	private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
	private static String update = null;
	
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
		logger.info(XACMLErrorConstants.ERROR_SYSTEM_ERROR+ "Session Error for : " + session.getId() + " Error: " + t.getMessage());
		
	}
	
	@OnMessage
	public void Message(String message, Session session) {
		if(message.equalsIgnoreCase("Manual")) {
			try {
				session.getBasicRemote().sendText(update);
			} catch (IOException e) {
				logger.info(XACMLErrorConstants.ERROR_SYSTEM_ERROR+ "Error in sending the Event Notification: "+ e.getMessage());
			}
		}
	}
	
	public static void sendNotification(String notification){
		for(Session session: queue) {
			try {
				session.getBasicRemote().sendText(notification);
			} catch (IOException e) {
				logger.info(XACMLErrorConstants.ERROR_SYSTEM_ERROR+ "Error in sending the Event Notification: "+ e.getMessage());
			}
		}
	}
	
	public static void setUpdate(String update) {
		NotificationServer.update = update;
	}
}