/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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

package org.openecomp.policy.pdp.rest.notifications;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.UUID;

import org.openecomp.policy.rest.XACMLRestProperties;

import com.att.nsa.cambria.client.CambriaClientFactory;
import com.att.nsa.cambria.client.CambriaConsumer;
import com.att.nsa.cambria.client.CambriaPublisher;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.util.XACMLProperties;

import org.openecomp.policy.common.logging.flexlogger.*;

public class ManualNotificationUpdateThread implements Runnable {
	private static final Logger logger	= FlexLogger.getLogger(ManualNotificationUpdateThread.class);
//	private static List<String> uebURLList = null;
	private static String topic = null;
	private static CambriaConsumer CConsumer = null;
//	private static Collection<String>  clusterList = null;
	private static String clusterList = null;
//	private Collection<String> urlList  = null;
	private static String update = null;
	
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
		synchronized(this) {
			this.isRunning = true;
		}
		URL aURL = null;
		String group =  UUID.randomUUID ().toString ();
		String id = "0";
		String returnTopic = null;
		try {
			ManualNotificationUpdateThread.clusterList = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_UEB_CLUSTER);
			String url = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_ID);
			aURL = new URL(url);
			topic = aURL.getHost() + aURL.getPort();
		} catch (NumberFormatException e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Unable to get UEB cluster list or pdp url: ", e);
			this.isRunning = false;
		} catch (MalformedURLException e) {
			logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in processing URL to create topic for Notification ", e);
		}
		String consumerTopic = aURL.getHost() + aURL.getPort() + "UpdateRequest";
		SendMessage(consumerTopic, "Starting-Topic");
		final LinkedList<String> urlList = new LinkedList<String> ();
		for ( String u : clusterList.split ( "," ) ){
			urlList.add ( u );
		}
		
		try {
			CConsumer = CambriaClientFactory.createConsumer ( null, urlList, consumerTopic , group, id, 20*1000, 1000 );
		} catch (MalformedURLException | GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		while (this.isRunning()) {
			logger.debug("While loop test _ take out ");
			try {
				for ( String msg : CConsumer.fetch () ){		
					logger.debug("Manual Notification Recieved Message " + msg + " from UEB cluster : ");
					returnTopic = processMessage(msg);
					if(returnTopic != null){
						SendMessage(returnTopic, update);
					}
				}
			} catch (IOException e) {
				logger.debug(XACMLErrorConstants.ERROR_DATA_ISSUE + "Error in processing UEB message");
			}
		}
		logger.debug("Stopping UEB Consuer loop will not logger fetch messages from the cluser");

		}

	private void SendMessage( String topic, String message) {
		CambriaPublisher pub = null;
		try {
			pub = CambriaClientFactory.createSimplePublisher (null, clusterList, topic );
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			pub.send( "pdpReturnMessage", message );
			logger.debug("Sending to Message to tpoic" + topic);
		} catch (IOException e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW+ "Error sending notification update");
		}	
		pub.close();		
	}

	private String processMessage(String msg) {
		logger.debug("notification message:  " + msg);
		String[] UID = msg.split("=")[1].split("\"");
		String returnTopic = topic + UID[0];
		if(msg.contains("Starting-Topic")){
			return null;
		}
		return returnTopic;
	}
	public static void setUpdate(String update) {
		ManualNotificationUpdateThread.update = update;
	}
	
}
