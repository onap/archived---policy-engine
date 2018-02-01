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

import java.util.List;
import java.util.UUID;

import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.BusConsumer;
import org.onap.policy.utils.BusConsumer.DmaapConsumerWrapper;
import org.onap.policy.xacml.api.XACMLErrorConstants;

public class AutoClientDMAAP implements Runnable {
    private static StdPDPNotification notification = null;
    private static NotificationScheme scheme = null;
    private static NotificationHandler handler = null;
    private static String topic = null;
    private static boolean status = false;
    private static Logger logger = FlexLogger.getLogger(AutoClientDMAAP.class.getName());
    private static String notficatioinType = null;
    private static BusConsumer dmaapConsumer = null;
    private static List<String> dmaapList = null;
    private static String aafLogin = null;
    private static String aafPassword = null;
    private volatile boolean running = false;

    public AutoClientDMAAP(List<String> dmaapList, String topic, String aafLogin, String aafPassword) {
        AutoClientDMAAP.topic = topic;
        AutoClientDMAAP.dmaapList = dmaapList;
        AutoClientDMAAP.aafLogin = aafLogin;
        AutoClientDMAAP.aafPassword = aafPassword;
    }

    public static void setAuto(NotificationScheme scheme, NotificationHandler handler) {
        AutoClientDMAAP.scheme = scheme;
        AutoClientDMAAP.handler = handler;
    }

    public static void setScheme(NotificationScheme scheme) {
        AutoClientDMAAP.scheme = scheme;
    }

    public static boolean getStatus() {
        return AutoClientDMAAP.status;
    }

    public static String getTopic() {
        return AutoClientDMAAP.topic;
    }

    public static String getNotficationType() {
        return AutoClientDMAAP.notficatioinType;
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void terminate() {
        this.running = false;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.running = true;
        }
        String group = UUID.randomUUID().toString();
        String id = "0";
        
        if (scheme == null || handler == null || 
        	! (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS) ||
        		scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) ) {
        	logger.info("no stop/start required");
        	return;
        }

        // create a loop to listen for messages from DMaaP server
        try {
            setDmaapCosumer(new BusConsumer.DmaapConsumerWrapper(dmaapList, topic, aafLogin, aafPassword, group,
                    id, 15 * 1000, 1000));
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to create DMaaP Consumer: ", e);
        }

        while (this.isRunning()) {
            try {
                for (String msg : dmaapConsumer.fetch()) {
                    logger.debug("Auto Notification Recieved Message " + msg + " from DMAAP server : "
                            + dmaapList.toString());
                    setNotification(NotificationUnMarshal.notificationJSON(msg));
                    callHandler();
                }
            } catch (Exception e) {
                logger.debug("Error in processing DMAAP message", e);
            }

        }
        logger.debug("Stopping DMAAP Consumer loop will no longer fetch messages from the servers");
    }

    private static void setNotification(StdPDPNotification notificationJSON) {
        notification = notificationJSON;
    }

    private static void setDmaapCosumer(DmaapConsumerWrapper dmaapConsumerWrapper) {
        dmaapConsumer = dmaapConsumerWrapper;
    }

    private static void callHandler() {
    	
    	if (handler == null || scheme == null) {
    		logger.info("handler does not need to do anything");
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
	        handler.notificationReceived(notification);
	    } else if (scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
	        PDPNotification newNotification = MatchStore.checkMatch(notification);
	        if (newNotification.getNotificationType() != null) {
	            handler.notificationReceived(newNotification);
	        }
	    }
    }

}
