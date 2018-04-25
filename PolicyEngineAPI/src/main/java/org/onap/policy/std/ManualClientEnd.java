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

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import javax.websocket.ClientEndpoint;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.xacml.api.XACMLErrorConstants;

@ClientEndpoint
public class ManualClientEnd extends WebSocketClient {
    private static CountDownLatch latch;
    private static StdPDPNotification notification = null;
    private static String resultJson = null;
    private static Logger logger = FlexLogger.getLogger(ManualClientEnd.class.getName());
    private static ManualClientEnd client;

    public ManualClientEnd(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("ManualClientEnd disconnected from: " + getURI() + "; Code: " + code + ", reason :  " + reason);
        latch.countDown();
    }

    @Override
    public void onError(Exception ex) {
        logger.error("XACMLErrorConstants.ERROR_PROCESS_FLOW + ManualClientEnd - Error connecting to: " + getURI()
                + ", Exception occured ...\n" + ex);
        latch.countDown();
    }

    @Override
    public void onMessage(String message) {
        logger.info("Manual Notification Recieved Message from : " + getURI() + ", Notification: " + message);
        ManualClientEnd.resultJson = message;
        try {
            ManualClientEnd.notification = NotificationUnMarshal.notificationJSON(message);     
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
        }
        latch.countDown();
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        logger.info("Manual Notification Session Started... " + getURI());
        send("Manual");
    }

    /**
     * Start.
     *
     * @param url the url
     */
    public static void start(String url) {
        latch = new CountDownLatch(1);

        if (url.contains("https")) {
            url = url.replaceAll("https", "wss");
        } else {
            url = url.replaceAll("http", "ws");
        }

        try {
            client = new ManualClientEnd(new URI(url + "notifications"));
            client.connect();
            latch.await();
            client.closeBlocking();
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
        }
    }

    /**
     * Result.
     *
     * @param scheme the scheme
     * @return the PDP notification
     */
    public static PDPNotification result(NotificationScheme scheme) {
        if (resultJson == null || notification == null) {
            logger.info("ManualClientENd - No Result available");
            return null;
        } else {
            if (scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)) {
                boolean removed = false;
                boolean updated = false;
                if (notification.getRemovedPolicies() != null && !notification.getRemovedPolicies().isEmpty()) {
                    removed = true;
                    notification.setNotificationType(NotificationType.REMOVE);
                }
                if (notification.getLoadedPolicies() != null && !notification.getLoadedPolicies().isEmpty()) {
                    updated = true;
                    notification.setNotificationType(NotificationType.UPDATE);
                }
                if (removed && updated) {
                    notification.setNotificationType(NotificationType.BOTH);
                }
                return notification;
            } else if (scheme.equals(NotificationScheme.MANUAL_NOTIFICATIONS)) {
                return MatchStore.checkMatch(notification);
            } else {
                return null;
            }
        }
    }
}
