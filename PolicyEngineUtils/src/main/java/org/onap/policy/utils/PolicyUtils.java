/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.drools.core.io.impl.ReaderResource;
import org.drools.verifier.Verifier;
import org.drools.verifier.VerifierError;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.kie.api.io.ResourceType;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class PolicyUtils {
    private static final Logger LOGGER = FlexLogger.getLogger(PolicyUtils.class);
    public static final String CATCH_EXCEPTION = "PE500: An exception was caught.";  
    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PACKAGE_ERROR = "mismatched input '{' expecting one of the following tokens: '[package";
    public static final String SUCCESS = "success";
    
    private PolicyUtils(){
        // Private Constructor
    }
    
    /**
     * Converts an Object to JSON String 
     * 
     * @param o Object 
     * @return String format of Object JSON. 
     * @throws JsonProcessingException
     */
    public static String objectToJsonString(Object o) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }
    
    /**
     * Converts JSON string into Object
     * 
     * @param jsonString 
     * @param className equivalent Class of the given JSON string 
     * @return T instance of the class given. 
     * @throws IOException
     */
    public static <T> T jsonStringToObject(String jsonString, Class<T> className) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, className);
    }
    
    /**
     * Decode a base64 string 
     * 
     * @param encodedString
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static String decode(String encodedString) throws UnsupportedEncodingException { 
        if(encodedString!=null && !encodedString.isEmpty()){ 
            return new String(Base64.getDecoder().decode(encodedString) ,"UTF-8"); 
        }else{ 
            return null; 
        } 
    }
    
    /**
     * Decodes Basic Authentication 
     * 
     * @param encodedValue
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String[] decodeBasicEncoding(String encodedValue) throws UnsupportedEncodingException {
        if(encodedValue!=null && encodedValue.contains("Basic ")){
            String encodedUserPassword = encodedValue.replaceFirst("Basic"  + " ", "");
            String usernameAndPassword;
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
            StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            String username = tokenizer.nextToken();
            String password = tokenizer.nextToken();
            return new String[]{username, password};
        }else{
            return new String[]{};
        }
    }
    
    /**
     * Validate a field if contains space or unacceptable policy input and return "success" if good. 
     * 
     * @param field
     * @return
     */
    public static String  policySpecialCharValidator(String field){
        String error;
        if ("".equals(field) || field.contains(" ") || !field.matches("^[a-zA-Z0-9_]*$")) {
            error = "The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}, _' following set of Combinations";
            return error; 
        }
        return SUCCESS;   
    } 
    
    /**
     * Validate a field (accepts space) if it contains unacceptable policy input and return "success" if good. 
     * 
     * @param field
     * @return
     */
    public static String  policySpecialCharWithSpaceValidator(String field){
        String error;
        if ("".equals(field) || !field.matches("^[a-zA-Z0-9_ ]*$")) {
            error = "The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}, _' following set of Combinations";
            return error;
        }
        return SUCCESS;   
    } 
    
    /**
     * Validate a field (accepts Dash) if it contains unacceptable policy input and return "success" if good. 
     * 
     * @param field
     * @return
     */
    public static String  policySpecialCharWithDashValidator(String field){
        String error;
        if ("".equals(field) || !field.matches("^[a-zA-Z0-9_-]*$")) {
            error = "The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}, _, -' following set of Combinations";
            return error;
        }
        return SUCCESS;   
    } 
    
    /**
     * Validate the XACML description tag and return "success" if good. 
     * 
     * @param field
     * @return
     */
    public static String descriptionValidator(String field) {
        String error;
        if (field.contains("@CreatedBy:") || field.contains("@ModifiedBy:")) {
             error = "The value in the description shouldn't contain @CreatedBy: or @ModifiedBy:";
             return error;
        } else {
            error = SUCCESS;
        }
        return error;   
    }
    
    /**
     * Validate if string contains non ASCII characters 
     * 
     * @param value
     * @return
     */
    public static boolean containsNonAsciiEmptyChars(String value) {
        return (value == null || value.contains(" ") || "".equals(value.trim()) || !CharMatcher.ascii().matchesAllOf((CharSequence) value)) ? true : false;
    }
    
    /**
     * Validate if given string is an integer. 
     * 
     * @param number
     * @return
     */
    public static Boolean isInteger(String number){
        if(number==null) {
            return false;
        }
        for (char c : number.toCharArray()){
            if (!Character.isDigit(c)) {
            	return false;
            }
        }
        return true;
    }
    
    /**
     * Validate Email Address and return "success" if good. 
     * 
     * @param emailAddressValue
     * @return
     */
    public static String validateEmailAddress(String emailAddressValue) {
        String error = SUCCESS;
        List<String> emailList = Arrays.asList(emailAddressValue.split(","));
        for(int i =0 ; i < emailList.size() ; i++){
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(emailList.get(i).trim());
            if(!matcher.matches()){
                error = "Please check the Following Email Address is not Valid ....   " +emailList.get(i);
                return error;
            }else{
                error = SUCCESS;
            }
        }
        return error;       
    }
    
    /**
     * Validates BRMS rule as per Policy Platform and return string contains "[ERR" if there are any errors.
     * 
     * @param rule
     * @return String error message
     */
    public static String brmsRawValidate(String rule){
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();
        verifier.addResourcesToVerify(new ReaderResource(new StringReader(rule)), ResourceType.DRL);
        // Check if there are any Errors in Verification. 
        if(!verifier.getErrors().isEmpty()){
            boolean ignore = false;
            StringBuilder message = new StringBuilder("Not a Valid DRL rule"); 
            for(VerifierError error: verifier.getErrors()){
                // Ignore annotations Error Messages
                if(!error.getMessage().contains("'@'") && !error.getMessage().contains(PACKAGE_ERROR)){
                    ignore= true;
                    message.append("\n" + error.getMessage());
                }
            }
            // Ignore new package names with '{'
            // More checks for message to check if its a package error.
            if(ignore && !message.toString().contains("Parser returned a null Package")){
                message.append("[ERR 107]");
            }
            return message.toString();
        }
        return "";
    }
    
    /**
     * Validates if the given string is proper JSON format. 
     * 
     * @param data
     * @return
     */
    public static boolean isJSONValid(String data) {
        try{
            JsonParser parser = new JsonParser();
            parser.parse(data);
        }catch(JsonSyntaxException e){
            LOGGER.error("Exception Occurred While Validating"+e);
            return false;
        }
        return true;
    }

    /**
     * Validates if the given string is proper XML format. 
     * 
     * @param data
     * @return
     */
    public static boolean isXMLValid(String data) {
    	if(data == null || data.isEmpty()){
        	return false;
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        
        try {    
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);		
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(new XMLErrorHandler());
            reader.parse(new InputSource(new StringReader(data)));
        } catch (Exception e) {
            LOGGER.error("Exception Occured While Validating"+e);
            return false;
        }
        return true;
    }

    /**
     * Validates if given string is valid Properties format. 
     * 
     * @param prop
     * @return
     */
    public static boolean isPropValid(String prop) {
        Scanner scanner = new Scanner(prop);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.replaceAll("\\s+", "");
            if (line.startsWith("#")) {
                continue;
            } else {
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    if (parts.length < 2) {
                        scanner.close();
                        return false;
                    }
                } else if(!line.trim().isEmpty()){
                    scanner.close();
                    return false;
                }
            }
        }
        scanner.close();
        return true;
    }
    
    /**
     * Given a version string consisting of integers with dots between them, convert it into an array of integers.
     * 
     * @param version
     * @return 
     * @throws NumberFormatException
     */
    public static int[] versionStringToArray(String version){
        if (version == null || version.length() == 0) {
            return new int[0];
        }
        String[] stringArray = version.split("\\.");
        int[] resultArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            resultArray[i] = Integer.parseInt(stringArray[i].trim());
        }
        return resultArray;
    }
}