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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaPublisher; 


@SuppressWarnings("deprecation")
public class ManualClientEndUEB {
    private static StdPDPNotification notification = null;
    private static String resultJson = null;
    private static Logger logger = FlexLogger.getLogger(ManualClientEndUEB.class.getName());
    private static CambriaConsumer CConsumer = null;
    @SuppressWarnings("unused")
    private static List<String> uebURLList = null;
    @SuppressWarnings("unused")
    private static boolean messageNotReceived = false;
    @SuppressWarnings("unused")
    private static String url = null;
    private static String uniquID = null;
    private static String topic = null;

    private ManualClientEndUEB() {
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

    private static void publishMessage(String pubTopic, String uniqueID , List<String> uebURLList) {

        String UEBlist = uebURLList.toString();
        UEBlist = UEBlist.substring(1,UEBlist.length()-1);
        CambriaPublisher pub = null;
        try {
            pub = CambriaClientFactory.createSimplePublisher(null, UEBlist, pubTopic);
        } catch (Exception e1) {
            logger.error("Exception Occured"+e1);
        }
        final JSONObject msg1 = new JSONObject (); 

        msg1.put ( "JSON", "UEB Update Ruest UID=" + uniqueID);  
        if(pub != null){
             try {
                pub.send ( "MyPartitionKey", msg1.toString () );
                pub.close ();
            } catch (IOException e) {
                logger.error("Exception Occured"+e);
            }
        }	
    }

    public static void createTopic (String url, String uniquID, List<String> uebURLList){
        URL aURL;
        try {
            aURL = new URL(url);
            topic = aURL.getHost() + aURL.getPort();
        } catch (MalformedURLException e) {
            topic = url.replace("[:/]", "");
        }

        publishMessage(topic+ uniquID , uniquID, uebURLList);

    }
    public static void start(String url, List<String> uebURLList,
            String uniqueID) {
        ManualClientEndUEB.uebURLList  = uebURLList;
        ManualClientEndUEB.url = url;
        ManualClientEndUEB.uniquID = uniqueID;
        URL aURL;
        try {
            aURL = new URL(url);
            ManualClientEndUEB.topic = aURL.getHost() + aURL.getPort();
        } catch (MalformedURLException e) {
            ManualClientEndUEB.topic = url.replace("[:/]", "");
        }
        String id = "0";
        try {
            CConsumer = CambriaClientFactory.createConsumer ( null, uebURLList, topic + uniquID, "clientGroup", id, 15*1000, 1000 );
        } catch (Exception e1) {
            logger.error("Exception Occured"+e1);
        }
        int count = 1;
        while (count < 4) {
            count = publishMessageAndSetNotification(uebURLList, count);
        }
    }

    private static int publishMessageAndSetNotification(List<String> uebURLList, int count) {
        publishMessage(topic + "UpdateRequest", uniquID, uebURLList);
        try {
            for ( String msg : CConsumer.fetch () ) {
                logger.debug("Manual Notification Recieved Message " + msg + " from UEB cluster : " + uebURLList.toString());
                resultJson = msg;
                if (!msg.contains("UEB Update")){
                    notification = NotificationUnMarshal.notificationJSON(msg);
                    count = 4;
                }
            }
        }catch (Exception e) {
            logger.error("Error in Manual CLient UEB notification ", e);
        }
        count++;
        return count;
    }

}
