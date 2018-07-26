/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

import org.json.JSONObject;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.BusConsumer;
import org.onap.policy.utils.BusPublisher;
import org.onap.policy.xacml.api.XACMLErrorConstants;

public class ManualClientEndDMAAP {
    private static StdPDPNotification notification = null;
    private static String resultJson = null;
    private static Logger logger = FlexLogger.getLogger(ManualClientEndDMAAP.class.getName());
    private static BusConsumer dmaapConsumer = null;
    private static String uniquID = null;
    private static String topic = null;
    private static int RETRY_LIMIT = 4;
    private ManualClientEndDMAAP() {
        // Empty constructor
    }


    public static PDPNotification result(NotificationScheme scheme) {
        if (resultJson == null || notification == null) {
            logger.debug("No Result" );
            return null;
        }
        if(scheme.equals(NotificationScheme.MANUAL_ALL_NOTIFICATIONS)) {
            boolean removed = false;
            boolean updated = false;
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
        }
        return null;
    }

    private static void publishMessage(String pubTopic, String uniqueID, List<String> dmaapList, String aafLogin, String aafPassword) {
        BusPublisher pub = null;
        try {
            pub = new BusPublisher.DmaapPublisherWrapper(dmaapList, topic, aafLogin, aafPassword);
            final JSONObject msg1 = new JSONObject ();
            msg1.put ( "JSON", "DMaaP Update Request UID=" + uniqueID);
            pub.send ( "MyPartitionKey", msg1.toString () );
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to create DMaaP Publisher: ", e);
        }
        if(pub != null){
            pub.close ();
        }
    }

    //NOTE:  should be able to remove this for DMAAP since we will not be creating topics dynamically
    public static void createTopic (String topic, String uniquID, List<String> dmaapList, String aafLogin, String aafPassword){
        ManualClientEndDMAAP.topic = topic;
        publishMessage(topic, uniquID, dmaapList, aafLogin, aafPassword);
    }


    public static void start(List<String> dmaapList, String topic, String aafLogin, String aafPassword, String uniqueID) {

        ManualClientEndDMAAP.uniquID = uniqueID;
        ManualClientEndDMAAP.topic = topic;

        String id = "0";

        try {
            dmaapConsumer = new BusConsumer.DmaapConsumerWrapper(dmaapList, topic, aafLogin, aafPassword, "clientGroup", id, 15*1000, 1000);
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to create DMaaP Consumer: ", e);
        }

        int retries = 1;
        boolean isSuccess = false;
        while (retries < RETRY_LIMIT && !isSuccess) {
            isSuccess = publishMessageAndSetNotification(dmaapList, topic, aafLogin, aafPassword);
            retries++;
        }
    }

    private static boolean publishMessageAndSetNotification(List<String> dmaapList, String topic, String aafLogin, String aafPassword) {
        publishMessage(topic, uniquID, dmaapList, aafLogin, aafPassword);
        try {
            for ( String msg : dmaapConsumer.fetch () ) {
                logger.debug("Manual Notification Recieved Message " + msg + " from DMaaP server : " + dmaapList.toString());
                resultJson = msg;
                if (!msg.contains("DMaaP Update")){
                    notification = NotificationUnMarshal.notificationJSON(msg);
                    return true;
                }
            }
        }catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to fetch messages from DMaaP servers: ", e);
        }
        return false;
    }
}
