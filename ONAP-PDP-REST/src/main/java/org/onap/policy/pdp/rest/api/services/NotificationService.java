/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.utils.BusPublisher;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

import com.att.research.xacml.util.XACMLProperties;

public class NotificationService {
    public static final String BACKUPFILE = "topicBackup.txt";
    private static Logger logger = FlexLogger.getLogger(GetDictionaryService.class.getName());
    private static ConcurrentHashMap<String, Date> topicQueue = new ConcurrentHashMap<>();
    private static int interval = 15000;
    private static Thread backUpthread = null;
    private static Object resourceLock = new Object();
    private static List<String> dmaapList = null;
    private static String dmaapServers = null;
    private static String aafLogin = null;
    private static String aafPassword = null;

    private String notificationResponse = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    /**
     * NotificationService Constructor.
     *
     * @param notificationTopic Topic Name in String format.
     * @param requestID Request ID in String format.
     * @param serviceType Needs to be NotificationServiceType based enumeration value.
     */
    public NotificationService(
            String notificationTopic, String requestID, NotificationServiceType serviceType) {
        init();
        if (dmaapServers == null || aafLogin == null || aafPassword == null) {
            notificationResponse =
                    XACMLErrorConstants.ERROR_DATA_ISSUE
                    + "DMaaP properties are missing from the property file";
            return;
        }
        UUID requestUUID = null;
        if (requestID != null && !requestID.isEmpty()) {
            try {
                requestUUID = UUID.fromString(requestID);
            } catch (IllegalArgumentException e) {
                requestUUID = UUID.randomUUID();
                logger.info("Generated Random UUID: " + requestUUID.toString(), e);
            }
        } else {
            requestUUID = UUID.randomUUID();
            logger.info("Generated Random UUID: " + requestUUID.toString());
        }
        try {
            run(notificationTopic, serviceType);
        } catch (PolicyException e) {
            notificationResponse = XACMLErrorConstants.ERROR_DATA_ISSUE + e;
            status = HttpStatus.BAD_REQUEST;
        }
    }

    private static void init() {
        if (dmaapServers == null || aafLogin == null || aafPassword == null) {
            dmaapServers = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_SERVERS);
            aafLogin = XACMLProperties.getProperty("DMAAP_AAF_LOGIN");
            aafPassword = XACMLProperties.getProperty("DMAAP_AAF_PASSWORD");
            interval =
                    Integer.parseInt(
                            XACMLProperties.getProperty("CLIENT_INTERVAL", Integer.toString(interval)));
            if (dmaapServers == null || aafLogin == null || aafPassword == null) {
                logger.error(
                        XACMLErrorConstants.ERROR_DATA_ISSUE
                        + "DMaaP properties are missing from the property file ");
                return;
            }
            // Cleanup Values.
            dmaapServers = dmaapServers.trim();
            aafLogin = aafLogin.trim();
            aafPassword = aafPassword.trim();
            // Get servers to List.
            if (dmaapServers.contains(",")) {
                dmaapList = new ArrayList<>(Arrays.asList(dmaapServers.split("\\s*,\\s*")));
            } else {
                dmaapList = new ArrayList<>();
                dmaapList.add(dmaapServers);
            }
            callThread();
        }
    }

    public static void reloadProps() {
        dmaapServers = null;
        aafLogin = null;
        aafPassword = null;
        backUpthread = null;
    }

    private void run(String notificationTopic, NotificationServiceType serviceType)
            throws PolicyException {
        // Check Validation
        if (notificationTopic == null) {
            String message = "Notification Topic is null";
            logger.error(message);
            throw new PolicyException(message);
        }
        notificationTopic = notificationTopic.trim();
        if (notificationTopic.isEmpty()) {
            String message = "Notification Topic is not valid. ";
            logger.error(message);
            throw new PolicyException(message);
        }
        // if already exists give error.Saying already registered.
        // Get Result.
        try {
            status = HttpStatus.OK;
            switch (serviceType) {
                case ADD:
                    addTopic(notificationTopic);
                    notificationResponse =
                            "Success!! Please give permissions to "
                                    + aafLogin
                                    + " that PDP will use to publish on given topic :"
                                    + notificationTopic
                                    + "\n Start calling /sendHeartbeat API at an interval less than "
                                    + Integer.toString(interval)
                                    + "ms";
                    break;
                case REMOVE:
                    removeTopic(notificationTopic);
                    notificationResponse =
                            "Notification Topic :"
                                    + notificationTopic
                                    + " has been removed and PDP will not publish notifications to this Topic.";
                    break;
                case HB:
                    heartBeat(notificationTopic);
                    notificationResponse = "Success!! HeartBeat registered.";
                    break;
            }
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
            status = HttpStatus.BAD_REQUEST;
            throw new PolicyException(e);
        }
    }

    // Used to register Heart beat.
    private void heartBeat(String notificationTopic) throws PolicyException {
        if (!topicQueue.isEmpty() && topicQueue.containsKey(notificationTopic)) {
            topicQueue.put(notificationTopic, new Date());
        } else {
            logger.info("Failed HeartBeat, Topic " + notificationTopic + "is not registered.");
            throw new PolicyException(
                    "Failed HeartBeat, Topic " + notificationTopic + "is not registered.");
        }
    }

    // Used to remove Topic.
    private static void removeTopic(String notificationTopic) throws PolicyException {
        if (topicQueue.containsKey(notificationTopic)) {
            topicQueue.remove(notificationTopic);
            removeTopicFromBackup(notificationTopic);
        } else {
            logger.info("Failed Removal, Topic " + notificationTopic + " is not registered.");
            throw new PolicyException(
                    "Failed Removal, Topic " + notificationTopic + " is not registered.");
        }
    }

    private static void removeTopicFromBackup(String notificationTopic) {
        synchronized (resourceLock) {
            try (Stream<String> lines = Files.lines(Paths.get(BACKUPFILE))) {
                List<String> replaced =
                        lines
                        .map(line -> (line.split("=")[0].equals(notificationTopic) ? "" : line))
                        .collect(Collectors.toList());
                try (PrintWriter pw = new PrintWriter(BACKUPFILE, "UTF-8")) {
                    replaced.forEach(
                            line -> {
                                if (line.trim().isEmpty()) {
                                    return;
                                }
                                pw.println(line);
                            });
                }
                lines.close();
            } catch (IOException e) {
                logger.error(
                        XACMLErrorConstants.ERROR_DATA_ISSUE + " Could not remove/recreate the backup. ", e);
            }
        }
    }

    // Used to add Topic.
    private void addTopic(String notificationTopic) throws PolicyException {
        // validate if topic exists.
        if (!topicQueue.isEmpty() && topicQueue.containsKey(notificationTopic)) {
            topicQueue.put(notificationTopic, new Date());
            logger.info("Topic " + notificationTopic + " is already registered.");
            throw new PolicyException("Topic " + notificationTopic + " is already registered.");
        }
        topicQueue.put(notificationTopic, new Date());
        addTopictoBackUp(notificationTopic);
    }

    private void addTopictoBackUp(String notificationTopic) {
        synchronized (resourceLock) {
            try {
                Files.write(
                        Paths.get(BACKUPFILE),
                        (notificationTopic + "=" + new Date().toString() + "\n").getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + " Could not add to the backup. ", e);
            }
        }
    }

    // Maintains BackUp and Queue Topic.
    private static void callThread() {
        // Create the backup file if it not exists.
        backup();
        if (backUpthread == null) {
            Runnable task =
                    () -> {
                        logger.info("BackUpThread not set. Starting now !");
                        threadTask();
                    };
                    backUpthread = new Thread(task);
                    backUpthread.start();
        }
    }

    private static void backup() {
        synchronized (resourceLock) {
            try {
                File backUpFile = new File(BACKUPFILE);
                if (!backUpFile.exists() && backUpFile.createNewFile()) {
                    logger.info(" BackUp File for topic's has been created !");
                } else {
                    // File Already exists. Process file and load the Memory.
                    Stream<String> stream = Files.lines(Paths.get(BACKUPFILE));
                    Map<String, Date> data =
                            stream
                            .map(line -> line.split(","))
                            .collect(Collectors.toMap(e -> e[0], e -> new Date()));
                    stream.close();
                    data.forEach(
                            (key, value) ->
                            logger.debug("Topic retrieved from backUp : " + key + " with Time : " + value));
                    topicQueue.putAll(data);
                }
            } catch (IOException e) {
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + " Could not process the backup. ", e);
            }
        }
    }

    private static void threadTask() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(interval);
                for (Map.Entry<String, Date> map : topicQueue.entrySet()) {
                    Date currentTime = new Date();
                    long timeDiff = 0;
                    timeDiff = currentTime.getTime() - map.getValue().getTime();
                    if (timeDiff > (interval + 1500)) {
                        removeTopic(map.getKey());
                    }
                }
            } catch (InterruptedException | PolicyException e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error during thread execution ", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public String getResult() {
        return notificationResponse;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

    /**
     * Entry point for sending Notifications from Notification Server.
     *
     * @param notification String JSON format of notification message which needs to be sent.
     */
    public static void sendNotification(String notification) {
        init();
        for (String topic : topicQueue.keySet()) {
            sendDmaapMessage(topic, notification);
        }
    }

    private static void sendDmaapMessage(String topic, String notification) {
        BusPublisher publisher =
                new BusPublisher.DmaapPublisherWrapper(dmaapList, topic, aafLogin, aafPassword);
        // Sending notification through DMaaP Message Router
        logger.info("NotificationService: send DMaaP Message. ");
        publisher.send("MyPartitionKey", notification);
        logger.info("Message Published on DMaaP :" + dmaapList.get(0) + "for Topic: " + topic);
        publisher.close();
    }

    /** Notification service Type Enumeration */
    public enum NotificationServiceType {
        ADD,
        REMOVE,
        HB
    }
}