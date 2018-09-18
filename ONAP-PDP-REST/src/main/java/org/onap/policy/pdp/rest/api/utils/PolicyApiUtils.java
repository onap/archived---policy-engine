/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.utils;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.google.common.base.CharMatcher;

public class PolicyApiUtils {
    private static Logger LOGGER = FlexLogger.getLogger(PolicyApiUtils.class
            .getName());
    private static final String SUCCESS = "success";

		
    public static Boolean validateNONASCIICharactersAndAllowSpaces(
            String jsonString) {
        Boolean isValidForm = false;
        if (jsonString.isEmpty()) {
            LOGGER.error("The Value is empty.");
            return false;
        } else {
            if (CharMatcher.ascii().matchesAllOf((CharSequence) jsonString)) {
                LOGGER.info("The Value does not contain ASCII Characters");
                isValidForm = true;
            } else {
                LOGGER.error("The Value Contains Non ASCII Characters");
                isValidForm = false;
            }
        }
        return isValidForm;
    }
    
    public static String formatResponse(StringBuilder responseString){
    	
    	LOGGER.info("Formatting response message from Policy Validator");
    	String response = responseString.toString().replace("<br>", " | ");		
		response = response.replaceAll("(<b>|<\\/b>|<br>|<i>|<\\/i>|@#)", "");
				
    	return response;
    }
    
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    public static JsonObject stringToJsonObject(String value)
            throws JsonException, JsonParsingException, IllegalStateException {
        JsonReader jsonReader = Json.createReader(new StringReader(value));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
    }
    
    public static String validateDictionaryJsonFields(JsonObject json, String dictionary) {
    	
    	LOGGER.info("Validating DictionaryJsonField values");
    	String message;
    	
    	if("Action".equals(dictionary.trim())){
        	if(json.containsKey("attributeName")){
        		if(json.getString("attributeName")==null || json.getString("attributeName").trim().isEmpty()){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Attribute Name provided.";
        			return message;
        		}
        		if(!SUCCESS.equals(PolicyUtils.policySpecialCharValidator(json.getString("attributeName").trim()))){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Attribute Name value.";
        			return message;
        		}
        	}else{
        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing attributeName key in the dictionaryJson parameter.";
        		return message;
        	}
        	if(json.containsKey("type")){
        		if(json.getString("type")==null || json.getString("type").trim().isEmpty()){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Type provided.";
        			return message;
        		}
        		if(!"REST".equals(json.getString("type").trim())){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Type value.";
        			return message;
        		}
        	}else{
        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing type key in the dictionaryJson parameter.";
        		return message;
        	}
        	if(json.containsKey("method")){
        		if(json.getString("method")==null || json.getString("method").trim().isEmpty()){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Method provided.";
        			return message;
        		}
        		if("GET".equals(json.getString("method").trim()) 
        				|| "PUT".equals(json.getString("method").trim()) 
        				|| "POST".equals(json.getString("method").trim())){
        			
        			//Successful Validation
        			
        		}else{
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Method value.";
    				return message;	
        		}
        	}else{
        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing method key in the dictionaryJson parameter.";
        		return message;
        	}
        	if(json.containsKey("url")){
        		if(json.getString("url")==null || json.getString("url").trim().isEmpty()){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No URL provided.";
        			return message;
        		}
        	}else{
        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing url key in the dictionaryJson parameter.";
        		return message;
        	}
        	if(json.containsKey("body")){
        		if(json.getString("body")==null || json.getString("body").trim().isEmpty()){
        			message = XACMLErrorConstants.ERROR_DATA_ISSUE + "No Body provided.";
        			return message;
        		}
        	}else{
        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing body key in the dictionaryJson parameter.";
        		return message;
        	}
        	if(json.containsKey("headers")){
        		JsonArray array = json.getJsonArray("headers");
        		
        		for (int i = 0;i<array.size(); i++) { 
    				JsonObject jsonObj = array.getJsonObject(i);
    				if(jsonObj.containsKey("option")){
        				if(jsonObj.getString("option")==null || jsonObj.getString("option").trim().isEmpty()){
        					message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing required Option value";
        	    			return message;
        				}
    				}else{
    	        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing option key in the headers list of the dictionaryJson parameter.";
    	        		return message;
    				}

    				if(jsonObj.containsKey("number")){
        				if(jsonObj.getString("number")==null || jsonObj.getString("number").trim().isEmpty()){
        					message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing required Number value";
        	    			return message;
        				}
    				}else{
    	        		message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing number key in the headers list of the dictionaryJson parameter.";
    	        		return message;
    				}
        		}
        	}

    	}

    	return SUCCESS;
    }
}
