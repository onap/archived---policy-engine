/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.openecomp.policy.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.core.io.impl.ReaderResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.kie.api.io.ResourceType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;

public class PolicyUtils {
	
	public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static String objectToJsonString(Object o) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(o);
	}
	
	public static <T> T jsonStringToObject(String jsonString, Class<T> className) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		T t = mapper.readValue(jsonString, className);
		return t;
	}
	
	public static String decode(String encodedString) throws UnsupportedEncodingException { 
		if(encodedString!=null && !encodedString.isEmpty()){ 
			return new String(Base64.getDecoder().decode(encodedString) ,"UTF-8"); 
		}else{ 
			return null; 
		} 
	}
	
	public static String[] decodeBasicEncoding(String encodedValue) throws Exception{
		if(encodedValue!=null && encodedValue.contains("Basic ")){
			String encodedUserPassword = encodedValue.replaceFirst("Basic"	+ " ", "");
			String usernameAndPassword = null;
			byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
			StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			String username = tokenizer.nextToken();
			String password = tokenizer.nextToken();
			return new String[]{username, password};
		}else{
			return null;
		}
	}
	
	public static String  emptyPolicyValidator(String field){
        String error = "success";
        if (field.equals("") || field.contains(" ") || !field.matches("^[a-zA-Z0-9_]*$")) {
            error = "The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}, _' following set of Combinations";
            return error;
        } else {
            if(CharMatcher.ASCII.matchesAllOf((CharSequence) field)){
                 error = "success";
            }else{
                error = "The Value Contains Non ASCII Characters";
                return error;
            }   
        }
        return error;   
    } 
    
    public static String descriptionValidator(String field) {
        String error = "success";
        if (field.contains("@CreatedBy:") || field.contains("@ModifiedBy:")) {
             error = "The value in the description shouldn't contain @CreatedBy: or @ModifiedBy:";
             return error;
        } else {
            error = "success";
        }
        return error;   
    }
    
    public static String validateEmailAddress(String emailAddressValue) {
        String error = "success";
        List<String> emailList = Arrays.asList(emailAddressValue.toString().split(","));
        for(int i =0 ; i < emailList.size() ; i++){
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(emailList.get(i).trim());
            if(!matcher.matches()){
                error = "Please check the Following Email Address is not Valid ....   " +emailList.get(i).toString();
                return error;
            }else{
                error = "success";
            }
        }
        return error;       
    }
    
    /*
     * Check for "[ERR" to see if there are any errors. 
     */
    public static String brmsRawValidate(String rule){
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();
        verifier.addResourcesToVerify(new ReaderResource(new StringReader(rule)), ResourceType.DRL);
        // Check if there are any Errors in Verification. 
        if(verifier.getErrors().size()!=0){
            String message = "Not a Valid DRL rule"; 
            for(VerifierError error: verifier.getErrors()){
                // Ignore annotations Error Messages
                if(!error.getMessage().contains("'@'")){
                    message = message + "\n" + error.getMessage();
                }
            }
            return message;
        }
        return "";
    }
    
    /**
	 * Given a version string consisting of integers with dots between them, convert it into an array of ints.
	 * 
	 * @param version
	 * @return
	 * @throws NumberFormatException
	 */
	public static int[] versionStringToArray(String version) throws NumberFormatException {
		if (version == null || version.length() == 0) {
			return new int[0];
		}
		String[] stringArray = version.split("\\.");
		int[] resultArray = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			resultArray[i] = Integer.parseInt(stringArray[i]);
		}
		return resultArray;
	}
}
