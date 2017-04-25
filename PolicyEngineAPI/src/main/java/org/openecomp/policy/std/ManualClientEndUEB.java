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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;

import org.json.JSONObject;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

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

	private static void publishMessage(String pubTopic, String uniqueID , List<String> uebURLList) {
		
		String UEBlist = uebURLList.toString();
		UEBlist = UEBlist.substring(1,UEBlist.length()-1);
        CambriaPublisher pub = null;
		try {
			pub = CambriaClientFactory.createSimplePublisher(null, UEBlist, pubTopic);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
        final JSONObject msg1 = new JSONObject (); 

        msg1.put ( "JSON", "UEB Update Ruest UID=" + uniqueID);  

        try {
			pub.send ( "MyPartitionKey", msg1.toString () );
		} catch (IOException e) {
			e.printStackTrace();
		} 
        pub.close (); 
		
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
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}		
		int count = 1;
		while (count < 4) {
				publishMessage(topic + "UpdateRequest", uniquID, uebURLList);
				try {
					for ( String msg : CConsumer.fetch () )
					{	
						
						logger.debug("Manual Notification Recieved Message " + msg + " from UEB cluster : " + uebURLList.toString());
						resultJson = msg;
						if (!msg.contains("UEB Update")){
//							System.out.println("Manual Notification Recieved Message " + msg + " from UEB cluster : " + uebURLList.toString());
							notification = NotificationUnMarshal.notificationJSON(msg);
							count = 4;
						}
					}
				}catch (Exception e) {
					
				} 
				count++;
			}		
	}
	
}
