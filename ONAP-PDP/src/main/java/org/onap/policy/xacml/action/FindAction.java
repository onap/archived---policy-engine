/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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
package org.onap.policy.xacml.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XACMLRestProperties;

import com.att.research.xacml.api.Advice;
import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeAssignment;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Obligation;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAdvice;
import com.att.research.xacml.std.StdAttributeAssignment;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdMutableResponse;
import com.att.research.xacml.std.StdMutableResult;
import com.att.research.xacml.std.StdObligation;
import com.att.research.xacml.util.XACMLProperties;

@SuppressWarnings("deprecation")
public class FindAction {
	private Logger LOGGER = FlexLogger.getLogger(this.getClass());
	private Boolean changeIt = false;
	private String configURL = null;
	private StdMutableResponse newResponse = new StdMutableResponse();
	private StdMutableResult addResult = new StdMutableResult();

	public StdMutableResponse run(StdMutableResponse stdResponse, Request pepRequest) {
		int count = 0;
		boolean config = false;
		boolean decide = false;
		Collection<RequestAttributes> requestAttributes =  pepRequest.getRequestAttributes();
		for(RequestAttributes requestAttribute : requestAttributes){
			Collection<Attribute> attributes = requestAttribute.getAttributes();
			for(Attribute attribute : attributes){
				if(attribute.getAttributeId().stringValue().equals("urn:oasis:names:tc:xacml:1.0:action:action-id")){
					for(AttributeValue<?> attributeValue : attribute.getValues()){
						if(attributeValue.getValue().toString().equalsIgnoreCase("ACCESS")){
							count++;
						}
						if(attributeValue.getValue().toString().equalsIgnoreCase("DECIDE")){
							decide = true;
						}
					}
				}
				if(attribute.getAttributeId().stringValue().equals("urn:oasis:names:tc:xacml:1.0:resource:resource-id")){
					for(AttributeValue<?> attributeValue : attribute.getValues()){
						if(attributeValue.getValue().toString().equalsIgnoreCase("Config")){
							count++;
						}
					}
				}
			}
		}
		if(count==2){
			config = true;
		}
		if(!config){
			search(stdResponse);
		}
		addResults(stdResponse, config , decide);
		LOGGER.info("Original Result is " + stdResponse.toString());
		LOGGER.info("Generated Result is " + addResult.toString());
		return newResponse;
	}

	private Collection<Obligation> obligations = new ArrayList<>();
	private Map<String, String> matchValues = new HashMap<>();
	private Map<String, String> headers = new HashMap<>();
	private boolean header = false;

	private void search(StdMutableResponse stdResponse) {
		for (Result result : stdResponse.getResults()) {
			if (!result.getObligations().isEmpty()) {
				System.out.println("Obligation Received");
				// Is there any action that PDP needs to take
				for (Obligation obligation : result.getObligations()) {
					int count = 0, uri = 0, PEP = 0;
					header = false;
					changeIt = false;
					Collection<AttributeAssignment> afterRemoveAssignments = new ArrayList<>();
					Identifier oblId = new IdentifierImpl(obligation.getId().stringValue());
					StdAttributeAssignment attributeURI = null;
					for (AttributeAssignment attribute : obligation.getAttributeAssignments()) {
						matchValues.put(attribute.getAttributeId().stringValue(), attribute.getAttributeValue().getValue().toString());
						if (attribute.getAttributeId().stringValue().equalsIgnoreCase("performer")) {
							if (attribute.getAttributeValue().getValue().toString().equalsIgnoreCase("PEPACTION")) {
								PEP++;
							} else if (attribute.getAttributeValue().getValue().toString().equalsIgnoreCase("PDPACTION")) {
								count++;
							}
						} else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("URL")) {
							uri++;
							if (uri == 1) {
								configURL = attribute.getAttributeValue().getValue().toString();
								attributeURI = new StdAttributeAssignment(attribute);
							}
						} else if (attribute.getAttributeId().stringValue().startsWith("headers")) {
							LOGGER.info("Headers are : "+ attribute.getAttributeValue().getValue().toString());
							header = true;
							headers.put(attribute.getAttributeId().stringValue().replaceFirst("(headers).", ""),
									attribute.getAttributeValue().getValue().toString());
							afterRemoveAssignments.add(attribute);
						} else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("body")) {
							String papPath = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
							papPath= papPath.replace("/pap", "");
							matchValues.put("body",attribute.getAttributeValue().getValue().toString().replace("$URL", papPath));
						}else {
							StdAttributeAssignment attributeObligation = new StdAttributeAssignment(attribute);
							afterRemoveAssignments.add(attributeObligation);
						}
					}
					if (count == 1 && uri == 1 && PEP == 0) {
						// Remove Obligation and add Advice
						changeIt = true;
						takeAction(stdResponse, oblId, afterRemoveAssignments);
					} else if (PEP == 1 && count == 0) {
						// Strip the PEPACTION if available
						if (uri == 1) {
							afterRemoveAssignments.add(attributeURI);
						}
						Obligation afterRemoveObligation = new StdObligation(
								oblId, afterRemoveAssignments);
						obligations.add(afterRemoveObligation);
					} else {
						obligations.add(obligation);
					}
				}
			}
		}
	}

	private void takeAction(StdMutableResponse stdResponse, Identifier advId,
			Collection<AttributeAssignment> afterRemoveAssignments) {
		if (changeIt) {
			LOGGER.info("the URL is :" + configURL);
			// Calling Rest URL..
			callRest();
			// Including the Results in an Advice
			Identifier id = new IdentifierImpl(
					"org.onap.policy:pdp:reply");
			Identifier statId = new IdentifierImpl(
					"org:onap:onap:policy:pdp:reply:status");
			Identifier statCategory = new IdentifierImpl(
					"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject");
			Identifier strId = new IdentifierImpl(
					"http://www.w3.org/2001/XMLSchema#string");
			Identifier resId = new IdentifierImpl(
					"org:onap:onap:policy:pdp:reply:resource");
			Identifier resCategory = new IdentifierImpl(
					"urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
			Identifier urlId = new IdentifierImpl(
					"http://www.w3.org/2001/XMLSchema#anyURI");
			AttributeValue<String> attributeStatusValue = new StdAttributeValue<>(
					strId, status + response);
			AttributeValue<String> attributeResourceValue = new StdAttributeValue<>(
					urlId, configURL);
			StdAttributeAssignment attributeStatus = new StdAttributeAssignment(
					statCategory, statId, "PDP", attributeStatusValue);
			StdAttributeAssignment attributeResouce = new StdAttributeAssignment(
					resCategory, resId, "PDP", attributeResourceValue);
			afterRemoveAssignments.add(attributeStatus);
			afterRemoveAssignments.add(attributeResouce);
			Advice advice = new StdAdvice(id, afterRemoveAssignments);
			addResult.addAdvice(advice);
		}
	}

	private void addResults(StdMutableResponse stdResponse, boolean config, boolean decide) {
		if(decide){
			newResponse = stdResponse;
			return;
		}
		for (Result result : stdResponse.getResults()) {
			if(config){
				addResult.addAdvice(result.getAssociatedAdvice());
			}
			addResult.addAttributeCategories(result.getAttributes());
			addResult.addPolicyIdentifiers(result.getPolicyIdentifiers());
			addResult.addPolicySetIdentifiers(result.getPolicySetIdentifiers());
			addResult.setStatus(result.getStatus());
			addResult.setDecision(result.getDecision());
			if(!config){
				addResult.addObligations(obligations);
			}
		}
		newResponse.add(addResult);
	}

	private int status;
	private String response;
	private DefaultHttpClient httpClient;

	private void callRest() {
		// Finding the Macros in the URL..
		Pattern pattern = Pattern.compile("\\$([a-zA-Z0-9.:]*)");
		Matcher match = pattern.matcher(configURL);
		StringBuffer sb = new StringBuffer();
		while (match.find()) {
			LOGGER.info("Found Macro : " + match.group(1));
			String replaceValue = matchValues.get(match.group(1));
			LOGGER.info("Replacing with :" + replaceValue);
			match.appendReplacement(sb, replaceValue);
		}
		match.appendTail(sb);
		LOGGER.info("URL is : " + sb.toString());
		configURL = sb.toString();
		// Calling the Requested service.
		if (matchValues.get("method").equalsIgnoreCase("GET")) {
			httpClient = new DefaultHttpClient();
			try {
				HttpGet getRequest = new HttpGet(configURL);
				// Adding Headers here
				if (header) {
					for (String key : headers.keySet()) {
						getRequest.addHeader(key, headers.get(key));
					}
				}
				HttpResponse result = httpClient.execute(getRequest);
				status = result.getStatusLine().getStatusCode();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(result.getEntity().getContent())));
				String output = " ";
				String out;
				while ((out = br.readLine()) != null) {
					output = output + out;
				}
				response = output;
			} catch (Exception e) {
				LOGGER.error(e.getMessage()+e);
				response = e.getMessage();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		} else if(matchValues.get("method").equalsIgnoreCase("POST")) {
			httpClient = new DefaultHttpClient();
			try {
				HttpPost postRequest = new HttpPost(configURL);
				// Adding Headers here
				if (header) {
					for (String key : headers.keySet()) {
						postRequest.addHeader(key, headers.get(key));
					}
				}
				// Adding the Body.
				URL configURL = new URL(matchValues.get("body"));
				URLConnection connection = null;
				connection = configURL.openConnection();
				// InputStream in = connection.getInputStrem();
				// LOGGER.info("The Body Content is : " + IOUtils.toString(in));
				JsonReader jsonReader = Json.createReader(connection.getInputStream());
				StringEntity input = new StringEntity(jsonReader.readObject().toString());
				input.setContentType("application/json");
				postRequest.setEntity(input);
				// Executing the Request.
				HttpResponse result = httpClient.execute(postRequest);
				LOGGER.info("Result Headers are : " + result.getAllHeaders());
				status = result.getStatusLine().getStatusCode();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(result.getEntity().getContent())));
				String output = " ";
				String out;
				while ((out = br.readLine()) != null) {
					output = output + out;
				}
				response = output;
			}catch (Exception e) {
				LOGGER.error(e.getMessage() +e);
				response = e.getMessage();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		} else if(matchValues.get("method").equalsIgnoreCase("PUT")) {
			httpClient = new DefaultHttpClient();
			try {
				HttpPut putRequest = new HttpPut(configURL);
				// Adding Headers here
				if (header) {
					for (String key : headers.keySet()) {
						putRequest.addHeader(key, headers.get(key));
					}
				}
				// Adding the Body.
				URL configURL = new URL(matchValues.get("body"));
				URLConnection connection = null;
				connection = configURL.openConnection();
				//InputStream in = connection.getInputStream();
				//LOGGER.info("The Body Content is : " + IOUtils.toString(in));
				JsonReader jsonReader = Json.createReader(connection.getInputStream());
				StringEntity input = new StringEntity(jsonReader.readObject().toString());
				input.setContentType("application/json");
				putRequest.setEntity(input);
				// Executing the Request.
				HttpResponse result = httpClient.execute(putRequest);
				status = result.getStatusLine().getStatusCode();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(result.getEntity().getContent())));
				String output = " ";
				String out;
				while ((out = br.readLine()) != null) {
					output = output + out;
				}
				response = output;
			} catch (Exception e) {
				LOGGER.error(e.getMessage() +e);
				response = e.getMessage();
			}finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}
}