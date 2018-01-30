/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.common.logging.flexlogger.FlexLogger; 
import org.onap.policy.common.logging.flexlogger.Logger;


public class ConfigurableRESTUtils  {
	
	protected Logger LOGGER	= FlexLogger.getLogger(this.getClass());

	//
	// How the value is returned from the RESTful server
	//		httpResponseCode means the result is simply the HTTP Response code (e.g. 200, 505, etc.)
	//		other values identify the encoding used for the string in the body of the HTTP response
	//
	public enum REST_RESPONSE_FORMAT {httpResponseCode, json }
	public enum RESQUEST_METHOD {
		  GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
		}
	
	private String ERROR_RECEIVED = "ERROR - Unexpected HTTP response: ";
	
	public ConfigurableRESTUtils() {
		//Default Constructor
	}
	
	
	/**
	 * Call the RESTful API and return a string containing the result.  The string may be either a httpResponseCode or json body
	 * 		
	 * @param fullURI
	 * @param hardCodedHeaders
	 * @param httpResponseCodes
	 * @param responseFormat
	 * @param jsonBody
	 * @param requestMethod
	 * @return String
	 */
	public String sendRESTRequest(String fullURI, Map<String, String> hardCodedHeaderMap, 
			Map<Integer,String> httpResponseCodeMap,
			REST_RESPONSE_FORMAT responseFormat,
			String jsonBody,
			RESQUEST_METHOD requestMethod ){
		
		String responseString = null;
		HttpURLConnection connection = null;
		try {
			
			URL url = new URL(fullURI);

			//
			// Open up the connection
			//
			connection = (HttpURLConnection)url.openConnection();
			//
			// Setup our method and headers
			//
            connection.setRequestMethod(requestMethod.toString());

            connection.setUseCaches(false);
            
            // add hard-coded headers
            for (Entry<String, String> entry : hardCodedHeaderMap.entrySet()) {
            	connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
            
            if (jsonBody != null){
            	connection.setDoInput(true);
            	connection.setDoOutput(true);
    			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
    			out.write(jsonBody);
    			out.flush();
    			out.close();
            } else{
            	connection.connect();
            }
            
            int responseCode = connection.getResponseCode();
            
            // check that the response is one we expected (and get the associated value at the same time)
            responseString = httpResponseCodeMap.get(responseCode);
            if (responseString == null) {
            	// the response was not configured, meaning it is unexpected and therefore an error
            	LOGGER.error("Unexpected HTTP response code '" + responseCode + "' from RESTful Server");
            	return ERROR_RECEIVED +  " code" + responseCode + " from RESTful Server";
            }
            
            // if the response is contained only in the http code we are done.  Otherwise we need to read the body
            if (responseFormat == REST_RESPONSE_FORMAT.httpResponseCode) {
            	return responseString;
            }
            
            // Need to read the body and return that as the responseString.

            responseString = null;
			// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
		    java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream());
		    scanner.useDelimiter("\\A");
		    responseString =  scanner.hasNext() ? scanner.next() : "";
		    scanner.close();
		    LOGGER.debug("RESTful body: " + responseString);
		    return responseString;
		    
		} catch (Exception e) {
			LOGGER.error("HTTP Request/Response from RESTFUL server: " + e);
			responseString =  ERROR_RECEIVED + e;
		} finally {
			// cleanup the connection
				if (connection != null) {
				try {
					// For some reason trying to get the inputStream from the connection
					// throws an exception rather than returning null when the InputStream does not exist.
					InputStream is = null;
					try {
						is = connection.getInputStream();
					} catch (Exception e1) {
						LOGGER.error("Exception Occured"+e1);
					}
					if (is != null) {
						is.close();
					}

				} catch (IOException ex) {
					LOGGER.error("Failed to close connection: " + ex, ex);
				}
				connection.disconnect();
			}
		}
		return responseString;

	}

}
