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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.utils.BusConsumer;
import org.onap.policy.utils.BusPublisher;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaPublisher;
import com.att.research.xacml.util.XACMLProperties;

@SuppressWarnings("deprecation")
public class ManualNotificationUpdateThread implements Runnable {

    private static final Logger LOGGER = FlexLogger.getLogger(ManualNotificationUpdateThread.class);

    private String topic = null;
    private CambriaConsumer cConsumer = null;
    private static String clusterList = null;
    private static String update = null;
    private BusConsumer dmaapConsumer = null;
    private List<String> dmaapList = null;
    private static String propNotificationType = null;
    private static String aafLogin = null;
    private static String aafPassword = null;

    public volatile boolean isRunning = false;

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void terminate() {
        this.isRunning = false;
    }

    /**
     *
     * This is our thread that runs on startup if the system is configured to UEB to accept manual update requests
     *
     */
    @Override
    public void run() {
        synchronized (this) {
            this.isRunning = true;
        }

        URL aURL = null;
        String group = UUID.randomUUID().toString();
        String id = "0";
        String returnTopic = null;
        setPropNotification();
        if ("ueb".equals(propNotificationType)) {
            try {
                setCluster();
                String url = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_ID);
                aURL = new URL(url);
                topic = aURL.getHost() + aURL.getPort();
            } catch (NumberFormatException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Unable to get UEB cluster list or pdp url: ", e);
                this.isRunning = false;
            } catch (MalformedURLException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "Error in processing URL to create topic for Notification ", e);
            }
            if (aURL != null) {
                String consumerTopic = aURL.getHost() + aURL.getPort() + "UpdateRequest";
                sendMessage(consumerTopic, "Starting-Topic");
                final LinkedList<String> urlList = new LinkedList<>();
                for (String u : clusterList.split(",")) {
                    urlList.add(u);
                }

                try {
                    cConsumer = CambriaClientFactory.createConsumer(null, urlList, consumerTopic, group, id, 20 * 1000,
                            1000);
                } catch (MalformedURLException | GeneralSecurityException e1) {
                    LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to create UEB Consumer: ", e1);
                }

                while (this.isRunning()) {
                    LOGGER.debug("While loop test _ take out ");
                    try {
                        for (String msg : cConsumer.fetch()) {
                            LOGGER.debug("Manual Notification Recieved Message " + msg + " from UEB cluster : ");
                            returnTopic = processMessage(msg);
                            if (returnTopic != null) {
                                sendMessage(returnTopic, update);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.debug(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in processing UEB message" + e);
                    }
                }
                LOGGER.debug("Stopping UEB Consumer loop will no longer fetch messages from the cluster");
            }
        } else if ("dmaap".equals(propNotificationType)) {
            String dmaapServers = null;
            try {
                dmaapServers = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_SERVERS);
                topic = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_TOPIC);
                setAAFCreds();
            } catch (Exception e) {
                LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Unable to get DMaaP servers list:", e);
                this.isRunning = false;
            }

            if (dmaapServers == null || topic == null) {
                LOGGER.error(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + "DMaaP properties are missing from the property file ");
            }

            dmaapServers = dmaapServers.trim();
            topic = topic.trim();

            String consumerTopic = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_TOPIC).trim();
            sendMessage(consumerTopic, "Starting-Topic");
            dmaapList = new ArrayList<>();
            for (String u : dmaapServers.split(",")) {
                dmaapList.add(u);
            }

            try {
                dmaapConsumer = new BusConsumer.DmaapConsumerWrapper(dmaapList, consumerTopic, aafLogin, aafPassword,
                        group, id, 20 * 1000, 1000);
            } catch (Exception e1) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to create DMaaP Consumer: ", e1);
            }

            while (this.isRunning()) {
                LOGGER.debug("While loop test _ take out ");
                try {
                    for (String msg : dmaapConsumer.fetch()) {
                        LOGGER.debug("Manual Notification Recieved Message " + msg + " from DMaaP server : ");
                        returnTopic = processMessage(msg);
                        if (returnTopic != null) {
                            sendMessage(returnTopic, update);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in processing DMaaP message: ", e);
                }
            }
            LOGGER.debug("Stopping DMaaP Consumer loop will no longer fetch messages from the servers");
        }
    }

    private static void setAAFCreds() {
        aafLogin = XACMLProperties.getProperty("DMAAP_AAF_LOGIN");
        aafPassword = XACMLProperties.getProperty("DMAAP_AAF_PASSWORD");
        if (aafLogin != null) {
            aafLogin = aafLogin.trim();
        }
        if (aafPassword != null) {
            aafPassword = aafPassword.trim();
        }
    }

    private static void setCluster() {
        clusterList = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_SERVERS);
        if (clusterList != null) {
            clusterList = clusterList.trim();
        }
    }

    private static void setPropNotification() {
        propNotificationType = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_TYPE);
    }

    private void sendMessage(String topic, String message) {
        CambriaPublisher pub = null;
        BusPublisher publisher = null;
        try {
            if ("ueb".equals(propNotificationType)) {
                pub = CambriaClientFactory.createSimplePublisher(null, clusterList, topic);
                pub.send("pdpReturnMessage", message);
                LOGGER.debug("Sending Message to UEB topic: " + topic);
                pub.close();

            } else if ("dmaap".equals(propNotificationType)) {
                publisher = new BusPublisher.DmaapPublisherWrapper(dmaapList, topic, aafLogin, aafPassword);
                publisher.send("pdpReturnMessage", message);
                LOGGER.debug("Sending to Message to DMaaP topic: " + topic);
                publisher.close();
            }

        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending notification update: ", e);
        }
        if (pub != null) {
            try {
                pub.send("pdpReturnMessage", message);
                LOGGER.debug("Sending to Message to tpoic" + topic);
                pub.close();
            } catch (IOException e) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error sending notification update" + e);
            }
        }
    }

    private String processMessage(String msg) {
        LOGGER.debug("notification message:  " + msg);
        String[] uID = msg.split("=")[1].split("\"");

        String returnTopic = topic + uID[0];
        if (msg.contains("Starting-Topic")) {
            return null;
        }
        return returnTopic;
    }

    public static void setUpdate(String update) {
        ManualNotificationUpdateThread.update = update;
    }

}
