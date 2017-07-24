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

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openecomp.policy.api.NotificationType;
import org.openecomp.policy.api.RemovedPolicy;
import org.openecomp.policy.api.UpdateType;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pdp.rest.PapUrlResolver;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacmlatt.pdp.policy.AllOf;
import com.att.research.xacmlatt.pdp.policy.AnyOf;
import com.att.research.xacmlatt.pdp.policy.Match;
import com.att.research.xacmlatt.pdp.policy.PolicyDef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * NotificationController Checks for the Updated and Removed policies. It
 * notifies the Server to send Notifications to the Client.
 * 
 * @version 0.2
 *
 */
public class NotificationController {
	private static final Logger LOGGER = FlexLogger.getLogger(NotificationController.class);
	private static Notification record = new Notification();
	private PDPStatus oldStatus = null;
	private Removed removed = null;
	private Updated updated = null;
	private ManualNotificationUpdateThread registerMaunualNotificationRunnable = null;
	private Thread manualNotificationThread = null;
	private boolean manualThreadStarted = false;
	
	private static String notificationJSON = null;
	private static String propNotificationType = null;
	private static String pdpURL = null;
	private static Boolean notificationFlag = false; 
	
	public void check(PDPStatus newStatus,Map<String, PolicyDef> policyContainer) {
		boolean updated = false;
		boolean removed = false;
		Notification notification = new Notification();
		HashSet<Removed> removedPolicies = new HashSet<>();
		HashSet<Updated> updatedPolicies = new HashSet<>();

		if (oldStatus == null) {
			oldStatus = newStatus;
		}
		// Debugging purpose only.
		LOGGER.debug("old config Status :" + oldStatus.getStatus());
		LOGGER.debug("new config Status :" + newStatus.getStatus());

		// Depending on the above condition taking the Change as an Update.
		if (oldStatus.getStatus().toString() != newStatus.getStatus().toString()) {
			LOGGER.info("There is an Update to the PDP");
			LOGGER.debug(oldStatus.getLoadedPolicies());
			LOGGER.debug(newStatus.getLoadedPolicies());
			// Check if there is an Update/additions in the policy.
			for (PDPPolicy newPolicy : newStatus.getLoadedPolicies()) {
				boolean change = true;
				for (PDPPolicy oldPolicy : oldStatus.getLoadedPolicies()) {
					// Check if there are same policies.
					if (oldPolicy.getId().equals(newPolicy.getId())) {
						// Check if they have same version.
						if (oldPolicy.getVersion().equals(newPolicy.getVersion())) {
							change = false;
						}
					}
				}
				// if there is a change Send the notifications to the Client.
				if (change) {
					sendUpdate(newPolicy, policyContainer);
					updated = true;
					updatedPolicies.add(this.updated);
				}
			}
			// Check if there is any removal of policy.
			for (PDPPolicy oldPolicy : oldStatus.getLoadedPolicies()) {
				boolean change = true;
				for (PDPPolicy newPolicy : newStatus.getLoadedPolicies()) {
					// Check if there are same policies.
					if (oldPolicy.getId().equals(newPolicy.getId())) {
						// Check if they have same version.
						if (oldPolicy.getVersion().equals(newPolicy.getVersion())) {
							change = false;
						}
					}
				}
				// if there is a change Send the notifications to the Client.
				if (change) {
					sendremove(oldPolicy);
					removed = true;
					removedPolicies.add(this.removed);
				}
			}
		}
		// At the end the oldStatus must be updated with the newStatus.
		oldStatus = newStatus;
		// Sending Notification to the Server to pass over to the clients
		if (updated || removed) {
			// Call the Notification Server..
			notification.setRemovedPolicies(removedPolicies);
			notification.setLoadedPolicies(updatedPolicies);
			notification = setUpdateTypes(updated, removed, notification);
			ObjectWriter om = new ObjectMapper().writer();
			try {
				notificationJSON = om.writeValueAsString(notification);
				LOGGER.info(notificationJSON);
				// NotificationServer Method here.
				propNotificationType = XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_TYPE);
				pdpURL = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_ID);
				if (("ueb".equals(propNotificationType)||"dmaap".equals(propNotificationType)) && !manualThreadStarted) {
					LOGGER.debug("Starting  Thread to accept UEB or DMAAP notfications.");
					this.registerMaunualNotificationRunnable = new ManualNotificationUpdateThread();
					this.manualNotificationThread = new Thread(this.registerMaunualNotificationRunnable);
					this.manualNotificationThread.start();
					manualThreadStarted = true;
				}
				String notificationJSON= null;
				notificationFlag = true;
				try{
					notificationJSON= record(notification);
				}catch(Exception e){
					LOGGER.error(e);
				}
				NotificationServer.setUpdate(notificationJSON);
				ManualNotificationUpdateThread.setUpdate(notificationJSON);
			} catch (JsonProcessingException e) {
				LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getMessage());
			}
		}
	}
	
	public static void sendNotification(){
		if(notificationFlag){
			try {
				NotificationServer.sendNotification(notificationJSON, propNotificationType, pdpURL);
			} catch (Exception e) {
				LOGGER.info(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error in sending the Event Notification: "+ e.getMessage());
			}
			notificationFlag = false;
		}
	}
	
	private void sendremove(PDPPolicy oldPolicy) {
		removed = new Removed();
		// Want to know what is removed ?
		LOGGER.info("Policy removed: " + oldPolicy.getId()+ " with version number: " + oldPolicy.getVersion());
		removed.setPolicyName(oldPolicy.getId());
		removed.setVersionNo(oldPolicy.getVersion());
		removeFile(oldPolicy);
	}

	private void sendUpdate(PDPPolicy newPolicy,Map<String, PolicyDef> policyContainer) {
		updated = new Updated();
		// Want to know what is new ?
		LOGGER.info("The new Policy is: " + newPolicy.getId());
		LOGGER.info("The version no. is: " + newPolicy.getVersion());
		updated.setPolicyName(newPolicy.getId());
		updated.setVersionNo(newPolicy.getVersion());
		updated.setUpdateType(UpdateType.NEW);
		// If the policy is of Config type then retrieve its matches.
		if (newPolicy.getName().contains(".Config_")) {
			// Take a Configuration copy to PDP webapps. 
			final String urlStart = "attributeId=URLID,expression";
			final String urlEnd = "}}},{";
			String policy = policyContainer.get(newPolicy.getId()).toString(); 
			if(policy.contains(urlStart)){
				String urlFinePartOne = policy.substring(policy.indexOf(urlStart)+urlStart.length());
				String urlFinePart = urlFinePartOne.substring(0,urlFinePartOne.indexOf(urlEnd));
				String urlString = urlFinePart.substring(urlFinePart.indexOf("value=$URL")+6); 
				callPap(urlString, "Config");
			}
			Iterator<AnyOf> anyOfs = policyContainer.get(newPolicy.getId()).getTarget().getAnyOfs();
			while (anyOfs.hasNext()) {
				AnyOf anyOf = anyOfs.next();
				Iterator<AllOf> allOfs = anyOf.getAllOfs();
				while (allOfs.hasNext()) {
					AllOf allOf = allOfs.next();
					Iterator<Match> matches = allOf.getMatches();
					HashMap<String, String> matchValues = new HashMap<>();
					while (matches.hasNext()) {
						Match match = matches.next();
						LOGGER.info("Attribute Value is: "+ match.getAttributeValue().getValue().toString());
						String[] result = match.getAttributeRetrievalBase().toString().split("attributeId=");
						result[1] = result[1].replaceAll("}", "");
						if (!result[1].equals("urn:oasis:names:tc:xacml:1.0:subject:subject-id")) {
							LOGGER.info("Attribute id is: " + result[1]);
						}
						matchValues.put(result[1], match.getAttributeValue().getValue().toString());
						LOGGER.info("Match is : "+ result[1]+ " , "	+ match.getAttributeValue().getValue().toString());
					}
					updated.setMatches(matchValues);
				}
			}
		}else if(newPolicy.getName().contains(".Action_")){
			// Take Configuration copy to PDP Webapps.
			// Action policies have .json as extension. 
			String urlString = "$URL/Action/" + newPolicy.getId().substring(0, newPolicy.getId().lastIndexOf(".")) + ".json";
			callPap(urlString, "Action");
		}
	}

	// Adding this for Recording the changes to serve Polling requests..
	private static String record(Notification notification) throws Exception {
		// Initialization with updates.
		if (record.getRemovedPolicies() == null	|| record.getLoadedPolicies() == null) {
			record.setRemovedPolicies(notification.getRemovedPolicies());
			record.setLoadedPolicies(notification.getLoadedPolicies());
		} else {
			// Check if there is anything new and update the record..
			if (record.getLoadedPolicies() != null	|| record.getRemovedPolicies() != null) {
				HashSet<Removed> removedPolicies = (HashSet<Removed>) record.getRemovedPolicies();
				HashSet<Updated> updatedPolicies = (HashSet<Updated>) record.getLoadedPolicies();

				// Checking with New updated policies.
				if (notification.getLoadedPolicies() != null && !notification.getLoadedPolicies().isEmpty()) {
					for (Updated newUpdatedPolicy : notification.getLoadedPolicies()) {
						// If it was removed earlier then we need to remove from our record
						Iterator<Removed> oldRemovedPolicy = removedPolicies.iterator();
						while (oldRemovedPolicy.hasNext()) {
							Removed policy = oldRemovedPolicy.next();
							if (newUpdatedPolicy.getPolicyName().equals(policy.getPolicyName())) {
								if (newUpdatedPolicy.getVersionNo().equals(policy.getVersionNo())) {
									oldRemovedPolicy.remove();
								}
							}
						}
						// If it was previously updated need to Overwrite it to the record.
						Iterator<Updated> oldUpdatedPolicy = updatedPolicies.iterator();
						while (oldUpdatedPolicy.hasNext()) {
							Updated policy = oldUpdatedPolicy.next();
							if (newUpdatedPolicy.getPolicyName().equals(policy.getPolicyName())) {
								if (newUpdatedPolicy.getVersionNo().equals(policy.getVersionNo())) {
									oldUpdatedPolicy.remove();
								}
							}
						}
						updatedPolicies.add(newUpdatedPolicy);
					}
				}
				// Checking with New Removed policies.
				if (notification.getRemovedPolicies() != null && !notification.getRemovedPolicies().isEmpty()) {
					for (Removed newRemovedPolicy : notification.getRemovedPolicies()) {
						// If it was previously removed Overwrite it to the record.
						Iterator<Removed> oldRemovedPolicy = removedPolicies.iterator();
						while (oldRemovedPolicy.hasNext()) {
							Removed policy = oldRemovedPolicy.next();
							if (newRemovedPolicy.getPolicyName().equals(policy.getPolicyName())) {
								if (newRemovedPolicy.getVersionNo().equals(policy.getVersionNo())) {
									oldRemovedPolicy.remove();
								}
							}
						}
						// If it was added earlier then we need to remove from our record.
						Iterator<Updated> oldUpdatedPolicy = updatedPolicies.iterator();
						while (oldUpdatedPolicy.hasNext()) {
							Updated policy = oldUpdatedPolicy.next();
							if (newRemovedPolicy.getPolicyName().equals(policy.getPolicyName())) {
								if (newRemovedPolicy.getVersionNo().equals(policy.getVersionNo())) {
									oldUpdatedPolicy.remove();
								}
							}
						}
						removedPolicies.add(newRemovedPolicy);
					}
				}
				record.setRemovedPolicies(removedPolicies);
				record.setLoadedPolicies(updatedPolicies);
			}
		}
		// Send the Result to the caller.
		ObjectWriter om = new ObjectMapper().writer();
		String json = null;
		try {
			json = om.writeValueAsString(record);
		} catch (JsonProcessingException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getMessage());
		}
		LOGGER.info(json);
		return json;
	}
	
	private static Notification setUpdateTypes(boolean updated, boolean removed, Notification notification) {
        if(notification!=null){
            if(updated && removed){
                notification.setNotificationType(NotificationType.BOTH);
                if(notification.getLoadedPolicies()!=null){
                    HashSet<Updated> updatedPolicies = new HashSet<>(); 
                    for(Updated oldUpdatedPolicy: notification.getLoadedPolicies()){
                        Updated updatePolicy = oldUpdatedPolicy;
                        if(notification.getRemovedPolicies()!=null){
                            for(RemovedPolicy removedPolicy: notification.getRemovedPolicies()){
                                String regex = ".(\\d)*.xml";
                                if(removedPolicy.getPolicyName().replaceAll(regex, "").equals(oldUpdatedPolicy.getPolicyName().replaceAll(regex, ""))){
                                    updatePolicy.setUpdateType(UpdateType.UPDATE);
                                    break;
                                }
                            }
                        }
                        updatedPolicies.add(updatePolicy);
                    }
                    notification.setLoadedPolicies(updatedPolicies);
                }
            }else if(updated){
                notification.setNotificationType(NotificationType.UPDATE);
            }else if(removed){
                notification.setNotificationType(NotificationType.REMOVE);
            }
        }
        return notification;
    }
	
	private void removeFile(PDPPolicy oldPolicy) {
		try{
			Path removedPolicyFile = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_CONFIG)+File.separator+oldPolicy.getId());
			Files.deleteIfExists(removedPolicyFile);
			boolean delete=false;
			File dir= null;
			if(oldPolicy.getName().startsWith("Config")){
				delete = true;
				dir = new File(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_WEBAPPS)+File.separator+"Config");
			}else if(oldPolicy.getName().startsWith("Action")){
				delete = true;
				dir = new File(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_WEBAPPS)+File.separator+"Action");
			}
			if(delete){
				FileFilter fileFilter = new WildcardFileFilter(oldPolicy.getId().substring(0, oldPolicy.getId().lastIndexOf("."))+".*");
				File[] configFile = dir.listFiles(fileFilter);
				if(configFile.length==1){
					Files.deleteIfExists(configFile[0].toPath());
				}
			}
		}catch(Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Couldn't remove the policy/config file " + oldPolicy.getName() + e);
		}
	}
	
	private void callPap(String urlString, String type) {
		Path configLocation = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_WEBAPPS)+File.separator+type);
		if(Files.notExists(configLocation)){
			try {
				Files.createDirectories(configLocation);
			} catch (IOException e) {
				LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW +"Failed to create config directory: " + configLocation.toAbsolutePath().toString(), e);
			}
		}
		PapUrlResolver papUrls = PapUrlResolver.getInstance();
		while(papUrls.hasMoreUrls()){
			String papPath = papUrls.getUrl();
			papPath = papPath.substring(0, papPath.lastIndexOf("/pap"));
			String papAddress= urlString.replace("$URL", papPath);
			String fileName = papAddress.substring(papAddress.lastIndexOf("/")+1);
			String fileLocation = configLocation.toString() + File.separator + fileName;
			try {
				URL papURL = new URL(papAddress);
				LOGGER.info("Calling " +papAddress + " for Configuration Copy.");
				URLConnection urlConnection = papURL.openConnection();
				File file= new File(fileLocation);
				try (InputStream is = urlConnection.getInputStream();
						OutputStream os = new FileOutputStream(file)) {
					IOUtils.copy(is, os);
					break;
				}
			} catch (MalformedURLException e) {
				LOGGER.error(e + e.getMessage());
			} catch(FileNotFoundException e){
				LOGGER.error(e + e.getMessage());
			} catch (IOException e) {
				LOGGER.error(e + e.getMessage());
			}
			papUrls.getNext();
		}
	}
}
