package org.openecomp.policy.std;

import java.util.List;

import org.json.JSONObject;
import org.openecomp.policy.api.NotificationScheme;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.PDPNotification;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.utils.BusConsumer;
import org.openecomp.policy.utils.BusPublisher;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;

public class ManualClientEndDMAAP {
	private static StdPDPNotification notification = null;
	private static String resultJson = null;
	private static Logger logger = FlexLogger.getLogger(ManualClientEndDMAAP.class.getName());
	private static BusConsumer dmaapConsumer = null;
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
        pub.close (); 
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
		
		int count = 1;
		while (count < 4) {
				publishMessage(topic, uniquID, dmaapList, aafLogin, aafPassword);
				try {
					for ( String msg : dmaapConsumer.fetch () )
					{	
						logger.debug("Manual Notification Recieved Message " + msg + " from DMaaP server : " + dmaapList.toString());
						resultJson = msg;
						if (!msg.contains("DMaaP Update")){
							notification = NotificationUnMarshal.notificationJSON(msg);
							count = 4;
						}
					}
				}catch (Exception e) {
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to fetch messages from DMaaP servers: ", e);
				} 
				count++;
			}		
	}
}
