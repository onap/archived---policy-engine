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

package org.onap.policy.api;

import java.util.Map;
import java.util.UUID;

/**
 * <code>EventRequestParameters</code> defines the Event Policy Request Parameters
 *  which retrieve(s) the response from PDP if the request parameters match with any Action Policy.  
 * 
 * @version 0.1
 */
public class EventRequestParameters {
	private Map<String,String> eventAttributes;
	private UUID requestID;
	
	/**
	 * Constructor with no Parameters
	 */
	public EventRequestParameters(){
		// Empty constructor
	}
	
	/**
	 * Constructor with Parameters
	 * 
	 * @param eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that contains the event ID and values.
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public EventRequestParameters(Map<String,String> eventAttributes, UUID requestID){
		this.eventAttributes = eventAttributes;
		this.requestID = requestID;
	}
	
	/**
	 * Gets the eventAttributes of Event Request Parameters. 
	 * 
	 * @return eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that contains the event ID and values.
	 */
	public Map<String,String> getEventAttributes() {
		return eventAttributes;
	}
	
	/**
	 * Sets the eventAttributes that contain the eventID and values to the Event Request Parameters. 
	 * 
	 * @param eventAttributes the <code>Map</code> of <code>String,String</code> format of the eventAttributes that must contain the event ID and values.
	 */
	public void setEventAttributes(Map<String,String> eventAttributes) {
		this.eventAttributes = eventAttributes;
	}
	
	/**
	 * Gets the ReqestID of Event Request Parameters which will be passed around ONAP requests. 
	 * 
	 * @return requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public UUID getRequestID() {
		return requestID;
	}
	
	/**
	 * Sets the ReqestID of Event Request Parameters which will be passed around ONAP requests.
	 * 
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public void setRequestID(UUID requestID) {
		this.requestID = requestID;
	}
	
} 
