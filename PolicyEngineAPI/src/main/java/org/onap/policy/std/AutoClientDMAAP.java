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
	public volatile boolean isRunning = false;
    

	public AutoClientDMAAP(List<String> dmaapList, String topic, String aafLogin, String aafPassword) {
	       AutoClientDMAAP.topic = topic;
	       AutoClientDMAAP.dmaapList = dmaapList;
	       AutoClientDMAAP.aafLogin = aafLogin;
	       AutoClientDMAAP.aafPassword = aafPassword;
	}

	public static void setAuto(NotificationScheme scheme,
			NotificationHandler handler) {
		AutoClientDMAAP.scheme = scheme;
		AutoClientDMAAP.handler = handler;
	}

	public static void setScheme(NotificationScheme scheme) {
		AutoClientDMAAP.scheme = scheme;
	}
	
	public static boolean getStatus(){
		return AutoClientDMAAP.status;
	}

	public static String getTopic() {
		return AutoClientDMAAP.topic;
	}
	
	public static String getNotficationType(){
		return AutoClientDMAAP.notficatioinType;
	}

	public synchronized boolean isRunning() {
		return this.isRunning;
	}
	
	public synchronized void terminate() {
		this.isRunning = false;
	}
	
	@Override
	public void run() {
		synchronized(this) {
			this.isRunning = true;
		}
		String group =  UUID.randomUUID ().toString ();
		String id = "0";

		// Stop and Start needs to be done.
		if (scheme != null && handler!=null) {
			if (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS) || scheme.equals(NotificationScheme.AUTO_NOTIFICATIONS)) {
					
				// create a loop to listen for messages from DMaaP server
				try {
					dmaapConsumer = new BusConsumer.DmaapConsumerWrapper(dmaapList, topic, aafLogin, aafPassword, group, id, 15*1000, 1000 );
				} catch (Exception e) {
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Unable to create DMaaP Consumer: ", e);
				} 
				
				while (this.isRunning() )
				{
					try {
						for ( String msg : dmaapConsumer.fetch () )
						{		
							logger.debug("Auto Notification Recieved Message " + msg + " from DMAAP server : " + dmaapList.toString());
							notification = NotificationUnMarshal.notificationJSON(msg);
							callHandler();
						}
					} catch (Exception e) {
						logger.debug("Error in processing DMAAP message");
						logger.error(e);
					}

				}
				logger.debug("Stopping DMAAP Consumer loop will no longer fetch messages from the servers");
			}
		}
	}

	private static void callHandler() {
		if (handler != null && scheme != null) {
			if (scheme.equals(NotificationScheme.AUTO_ALL_NOTIFICATIONS)) {
				boolean removed = false, updated = false;
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

}
