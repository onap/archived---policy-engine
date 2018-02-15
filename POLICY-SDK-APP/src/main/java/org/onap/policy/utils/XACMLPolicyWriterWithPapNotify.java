/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

import org.onap.policy.rest.XACMLRestProperties;

import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.util.XACMLPolicyWriter;
import com.att.research.xacml.util.XACMLProperties;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

import org.onap.policy.common.logging.flexlogger.FlexLogger; 
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * Helper static class that wraps XACMLPolicyWriter
 * 
 *
 */
public class XACMLPolicyWriterWithPapNotify{
	private static final Logger LOGGER = FlexLogger.getLogger(XACMLPolicyWriterWithPapNotify.class);
	
	private XACMLPolicyWriterWithPapNotify() {
		// Add private constructor to hide the implicit public one
	}

	/**
	 * Helper static class that does the work to write a policy set to a file on disk and notify PAP
	 * 
	 *
	 */
	public static Path writePolicyFile(Path filename, PolicySetType policySet) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet)"
					+ "\nfilename = " + filename
					+ "\npolicySet = " + policySet);
		}
		//write to file
		Path path = XACMLPolicyWriter.writePolicyFile(filename, policySet);
		
		if(path!=null){
			//write to DB
			if(notifyPapOfCreateUpdate(filename.toAbsolutePath().toString())){
				return path;
			}else{
				//write to DB failed.  So, delete the file
				try{
					Files.deleteIfExists(path);
				}catch(DirectoryNotEmptyException e){
					//We are trying to delete a directory and it is not empty
					LOGGER.error("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet): Files.deleteIfExists(path)"
							+ "\nDirectoryNotEmptyException for path = " + path
							+ "\nException message = " + e);
				}catch(IOException e) {
				    // File permission problems are caught here.
					LOGGER.error("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet): Files.deleteIfExists(path)"
							+ "\nIOException for path = " + path
							+ "\nException message = " + e);
				}catch(Exception e){
					LOGGER.error("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet): Files.deleteIfExists(path)"
							+ "\nException for path = " + path
							+ "\nException message = " + e);
				}
				return null;
			}

		}else{
			return null;
		}
	}

	/**
	 * Helper static class that does the work to write a policy set to an output stream and notify PAP
	 * 
	 *
	 */
	public static void writePolicyFile(OutputStream os, PolicySetType policySet) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(OutputStream os, PolicySetType policySet)"
					+ "\nos = " + os
					+ "\npolicySet = " + policySet);
		}
		//Only used for writing a byte array output stream for a message.  No file is written
		XACMLPolicyWriter.writePolicyFile(os, policySet);
	}

	/**
	 * Helper static class that does the work to write a policy to a file on disk.
	 * 
	 *
	 */
	public static Path writePolicyFile(Path filename, PolicyType policy) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicyType policy)"
					+ "\nfilename = " + filename
					+ "\npolicy = " + policy);
		}

		//write to file
		Path path = XACMLPolicyWriter.writePolicyFile(filename, policy);
		
		if(path!=null){
			//write to DB			
			if(notifyPapOfCreateUpdate(filename.toAbsolutePath().toString())){
				return path;
			}else{
				//write to DB failed so delete the file
				try{
					Files.deleteIfExists(path);
				}catch(DirectoryNotEmptyException e){
					//We are trying to delete a directory and it is not empty
					LOGGER.error("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet)Files.deleteIfExists(path) :"
							+ "\nDirectoryNotEmptyException for path = " + path
							+ "\nException message = " + e);
				}catch(IOException e) {
				    // File permission problems are caught here.
					LOGGER.error("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet): Files.deleteIfExists(path)"
							+ "\nIOException for path = " + path
							+ "\nException message = " + e);
				}catch(Exception e){
					LOGGER.error("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(Path filename, PolicySetType policySet): Files.deleteIfExists(path)"
							+ "\nException for path = " + path
							+ "\nException message = " + e);
				}
				return null;
			}

		}else{
			return null;
		}
	}


	/**
	 * Helper static class that does the work to write a policy to a file on disk.
	 * 
	 *
	 */
	public static InputStream getXmlAsInputStream(PolicyType policy) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.getXmlAsInputStream(PolicyType policy)"
					+ "\npolicy = " + policy);
		}
		return XACMLPolicyWriter.getXmlAsInputStream(policy);
	}
	/**
	 * Helper static class that does the work to write a policy set to an output stream.
	 * 
	 *
	 */
	public static void writePolicyFile(OutputStream os, PolicyType policy) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.writePolicyFile(OutputStream os, PolicyType policy)"
					+ "\nos = " + os
					+ "\npolicy = " + policy);
		}
		//There are no references to this and if there were, it would most likely be used in an http message
		XACMLPolicyWriter.writePolicyFile(os, policy);
	}

	public static String changeFileNameInXmlWhenRenamePolicy(Path filename) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.changeFileNameInXmlWhenRenamePolicy(Path filename)"
					+ "\nfilename = " + filename);
		}
		return XACMLPolicyWriter.changeFileNameInXmlWhenRenamePolicy(filename);
	}
	
	public static boolean notifyPapOfPolicyRename(String oldPolicyName, String newPolicyName){
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.notifyPapOfCreateUpdate(String policyToCreateUpdate) " 
					+ "\npolicyToCreateUpdate = " + " ");
		}
		Base64.Encoder encoder = Base64.getEncoder();
		String encoding = encoder.encodeToString((XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID)+":"+CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS))).getBytes(StandardCharsets.UTF_8));
		HttpURLConnection connection;
		UUID requestID = UUID.randomUUID();
		URL url;
		try {
			url = new URL(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL)+"?oldPolicyName="+ URLEncoder.encode(oldPolicyName, "UTF-8")+"&newPolicyName="+URLEncoder.encode(newPolicyName,"UTF-8"));
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("\nnotifyPapOfCreateUpdate: URL = " + url);
			}
		} catch (MalformedURLException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nMalformedURLException message = " + e);
			
			return false;
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nUnsupportedEncodingException message = " + e);

			return false;
		}
		//
		// Open up the connection
		//
		try {
			connection = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nurl.openConnection() IOException message = " + e);			
			return false;
		}
		//
		// Setup our method and headers
		//
        try {
			connection.setRequestMethod("PUT");
		} catch (ProtocolException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nconnection.setRequestMethod(PUT) ProtocolException message = " + e);
			connection.disconnect();
			return false;
		}
        connection.setRequestProperty("Authorization", "Basic " + encoding);
		connection.setRequestProperty("Accept", "text/x-java-properties");
        connection.setRequestProperty("Content-Type", "text/x-java-properties");	  	        
        connection.setRequestProperty("requestID", requestID.toString());
        connection.setUseCaches(false);
        //
        // Adding this in. It seems the HttpUrlConnection class does NOT
        // properly forward our headers for POST re-direction. It does so
        // for a GET re-direction.
        //
        // So we need to handle this ourselves.
        //
        connection.setInstanceFollowRedirects(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
        try {
			connection.connect();
		} catch (IOException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nconnection.connect() IOException message = " + e);
			connection.disconnect();
			return false;
		}
        try {
        	int responseCode = connection.getResponseCode();
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
    					+ "\nconnection.getResponseCode() = " + responseCode);
        	}
			if (responseCode == 200) {
				connection.disconnect();
				return true;
			} else {
				connection.disconnect();
				return false;
			}
		} catch (IOException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nconnection.getResponseCode() IOException message = " + e);
			connection.disconnect();
			return false;
		}
	}
	
	public static boolean notifyPapOfDelete(String policyToDelete){
		Base64.Encoder encoder = Base64.getEncoder();
		String encoding = encoder.encodeToString((XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID)+":"+CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS))).getBytes(StandardCharsets.UTF_8));
		HttpURLConnection connection;
		UUID requestID = UUID.randomUUID();
		String papUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
		if(papUrl == null){
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + 
					"PAP url property does not exist");
			return false;
		}
		String urlString = "";
		try{
		urlString = papUrl+"?groupId=0&isDeleteNotify=1&policyToDelete="+ URLEncoder.encode(policyToDelete, "UTF-8");
		} catch(UnsupportedEncodingException e){
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + 
					"Invalid encoding: UTF-8", e);
			return false;
		}
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {			
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + 
					"Error parsing PAP url: "
							+ urlString
							, e);				
			return false;
		}
		//
		// Open up the connection
		//
		try {
			connection = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
					"Error opening HttpURLConnection to: "
							+ url.toString()
							, e);				
			return false;
		}
		//
		// Setup our method and headers
		//
        try {
			connection.setRequestMethod("DELETE");
		} catch (ProtocolException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + 
					"Invalid request method: DELETE", e);
			connection.disconnect();
			return false;
		}
        connection.setRequestProperty("Authorization", "Basic " + encoding);
		connection.setRequestProperty("Accept", "text/x-java-properties");
        connection.setRequestProperty("Content-Type", "text/x-java-properties");	  	        
        connection.setRequestProperty("requestID", requestID.toString());
        connection.setUseCaches(false);
        //
        // Adding this in. It seems the HttpUrlConnection class does NOT
        // properly forward our headers for POST re-direction. It does so
        // for a GET re-direction.
        //
        // So we need to handle this ourselves.
        //
        connection.setInstanceFollowRedirects(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
        try {
			connection.connect();
		} catch (IOException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
					"Error connecting HttpURLConnection to: "
							+ connection.getURL().toString()
							, e);	
			connection.disconnect();
			return false;
		}
        try {
			if (connection.getResponseCode() == 200) {
				connection.disconnect();
				//worked
				return true;
			} else {
				connection.disconnect();
				return false;
			}
		} catch (IOException e) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + 
					"Error getting HttpUrlConnection response code for: "
							+ connection.getURL().toString()
							, e);
			connection.disconnect();
			return false;
		}
	}
	
	public static boolean notifyPapOfCreateUpdate(String policyToCreateUpdate){
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("\nXACMLPolicyWriterWithPapNotify.notifyPapOfCreateUpdate(String policyToCreateUpdate) " 
					+ "\npolicyToCreateUpdate = " + policyToCreateUpdate);
		}
		Base64.Encoder encoder = Base64.getEncoder();
		String encoding = encoder.encodeToString((XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID)+":"+CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS))).getBytes(StandardCharsets.UTF_8));
		HttpURLConnection connection;
		UUID requestID = UUID.randomUUID();
		URL url;
		try {
			url = new URL(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL)+"?policyToCreateUpdate="+ URLEncoder.encode(policyToCreateUpdate, "UTF-8"));
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("\nnotifyPapOfCreateUpdate: URL = " + url);
			}
		} catch (MalformedURLException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nMalformedURLException message = " + e);
			
			return false;
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nUnsupportedEncodingException message = " + e);

			return false;
		}
		//
		// Open up the connection
		//
		try {
			connection = (HttpURLConnection)url.openConnection();
		} catch (IOException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nurl.openConnection() IOException message = " + e);			
			return false;
		}
		//
		// Setup our method and headers
		//
        try {
			connection.setRequestMethod("PUT");
		} catch (ProtocolException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nconnection.setRequestMethod(PUT) ProtocolException message = " + e);
			connection.disconnect();
			return false;
		}
        connection.setRequestProperty("Authorization", "Basic " + encoding);
		connection.setRequestProperty("Accept", "text/x-java-properties");
        connection.setRequestProperty("Content-Type", "text/x-java-properties");	  	        
        connection.setRequestProperty("requestID", requestID.toString());
        connection.setUseCaches(false);
        //
        // Adding this in. It seems the HttpUrlConnection class does NOT
        // properly forward our headers for POST re-direction. It does so
        // for a GET re-direction.
        //
        // So we need to handle this ourselves.
        //
        connection.setInstanceFollowRedirects(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
        try {
			connection.connect();
		} catch (IOException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nconnection.connect() IOException message = " + e);
			connection.disconnect();
			return false;
		}
        try {
        	int responseCode = connection.getResponseCode();
        	if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
    					+ "\nconnection.getResponseCode() = " + responseCode);
        	}
			if (responseCode == 200) {
				connection.disconnect();
				return true;
			} else {
				connection.disconnect();
				return false;
			}
		} catch (IOException e) {
			LOGGER.error("\nnotifyPapOfCreateUpdate(String policyToCreateUpdate)"
					+ "\nconnection.getResponseCode() IOException message = " + e);
			connection.disconnect();
			return false;
		}
	}
}
