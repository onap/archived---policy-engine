/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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

package org.onap.policy.xacml.action;

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

@SuppressWarnings("deprecation")
public class FindAction {
    private Logger logger = FlexLogger.getLogger(this.getClass());
    private Boolean changeIt = false;
    private String configUrl = null;
    private StdMutableResponse newResponse = new StdMutableResponse();
    private StdMutableResult addResult = new StdMutableResult();

    /**
     * Generate {@link StdMutableResponse} based on input request.
     *
     * @param stdResponse the response
     * @param pepRequest the request
     * @return {@link StdMutableResponse}
     */
    public StdMutableResponse run(final StdMutableResponse stdResponse, final Request pepRequest) {
        int count = 0;
        boolean config = false;
        boolean decide = false;
        final Collection<RequestAttributes> requestAttributes = pepRequest.getRequestAttributes();
        for (final RequestAttributes requestAttribute : requestAttributes) {
            final Collection<Attribute> attributes = requestAttribute.getAttributes();
            for (final Attribute attribute : attributes) {
                if (attribute.getAttributeId().stringValue().equals("urn:oasis:names:tc:xacml:1.0:action:action-id")) {
                    for (final AttributeValue<?> attributeValue : attribute.getValues()) {
                        if (attributeValue.getValue().toString().equalsIgnoreCase("ACCESS")) {
                            count++;
                        }
                        if (attributeValue.getValue().toString().equalsIgnoreCase("DECIDE")) {
                            decide = true;
                        }
                    }
                }
                if (attribute.getAttributeId().stringValue()
                        .equals("urn:oasis:names:tc:xacml:1.0:resource:resource-id")) {
                    for (final AttributeValue<?> attributeValue : attribute.getValues()) {
                        if (attributeValue.getValue().toString().equalsIgnoreCase("Config")) {
                            count++;
                        }
                    }
                }
            }
        }
        if (count == 2) {
            config = true;
        }
        if (!config) {
            search(stdResponse);
        }
        addResults(stdResponse, config, decide);
        logger.info("Original Result is " + stdResponse.toString());
        logger.info("Generated Result is " + addResult.toString());
        return newResponse;
    }

    private Collection<Obligation> obligations = new ArrayList<>();
    private Map<String, String> matchValues = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private boolean header = false;

    private void search(final StdMutableResponse stdResponse) {
        for (final Result result : stdResponse.getResults()) {
            if (!result.getObligations().isEmpty()) {
                System.out.println("Obligation Received");
                // Is there any action that PDP needs to take
                for (final Obligation obligation : result.getObligations()) {
                    int count = 0;
                    int uri = 0;
                    int pep = 0;
                    header = false;
                    changeIt = false;
                    final Collection<AttributeAssignment> afterRemoveAssignments = new ArrayList<>();
                    final Identifier oblId = new IdentifierImpl(obligation.getId().stringValue());
                    StdAttributeAssignment attributeUri = null;
                    for (final AttributeAssignment attribute : obligation.getAttributeAssignments()) {
                        matchValues.put(attribute.getAttributeId().stringValue(),
                                attribute.getAttributeValue().getValue().toString());
                        if (attribute.getAttributeId().stringValue().equalsIgnoreCase("performer")) {
                            if (attribute.getAttributeValue().getValue().toString().equalsIgnoreCase("PEPACTION")) {
                                pep++;
                            } else if (attribute.getAttributeValue().getValue().toString()
                                    .equalsIgnoreCase("PDPACTION")) {
                                count++;
                            }
                        } else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("URL")) {
                            uri++;
                            if (uri == 1) {
                                configUrl = attribute.getAttributeValue().getValue().toString();
                                attributeUri = new StdAttributeAssignment(attribute);
                            }
                        } else if (attribute.getAttributeId().stringValue().startsWith("headers")) {
                            logger.info("Headers are : " + attribute.getAttributeValue().getValue().toString());
                            header = true;
                            headers.put(attribute.getAttributeId().stringValue().replaceFirst("(headers).", ""),
                                    attribute.getAttributeValue().getValue().toString());
                            afterRemoveAssignments.add(attribute);
                        } else if (attribute.getAttributeId().stringValue().equalsIgnoreCase("body")) {
                            String papPath = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
                            papPath = papPath.replace("/pap", "");
                            matchValues.put("body",
                                    attribute.getAttributeValue().getValue().toString().replace("$URL", papPath));
                        } else {
                            final StdAttributeAssignment attributeObligation = new StdAttributeAssignment(attribute);
                            afterRemoveAssignments.add(attributeObligation);
                        }
                    }
                    if (count == 1 && uri == 1 && pep == 0) {
                        // Remove Obligation and add Advice
                        changeIt = true;
                        takeAction(stdResponse, oblId, afterRemoveAssignments);
                    } else if (pep == 1 && count == 0) {
                        // Strip the PEPACTION if available
                        if (uri == 1) {
                            afterRemoveAssignments.add(attributeUri);
                        }
                        final Obligation afterRemoveObligation = new StdObligation(oblId, afterRemoveAssignments);
                        obligations.add(afterRemoveObligation);
                    } else {
                        obligations.add(obligation);
                    }
                }
            }
        }
    }

    private void takeAction(final StdMutableResponse stdResponse, final Identifier advId,
            final Collection<AttributeAssignment> afterRemoveAssignments) {
        if (changeIt) {
            logger.info("the URL is :" + configUrl);
            // Calling Rest URL..
            callRest();
            // Including the Results in an Advice
            final Identifier id = new IdentifierImpl("org.onap.policy:pdp:reply");
            final Identifier statId = new IdentifierImpl("org:onap:onap:policy:pdp:reply:status");
            final Identifier statCategory =
                    new IdentifierImpl("urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject");
            final Identifier strId = new IdentifierImpl("http://www.w3.org/2001/XMLSchema#string");
            final Identifier resId = new IdentifierImpl("org:onap:onap:policy:pdp:reply:resource");
            final Identifier resCategory =
                    new IdentifierImpl("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
            final Identifier urlId = new IdentifierImpl("http://www.w3.org/2001/XMLSchema#anyURI");
            final AttributeValue<String> attributeStatusValue = new StdAttributeValue<>(strId, status + response);
            final AttributeValue<String> attributeResourceValue = new StdAttributeValue<>(urlId, configUrl);
            final StdAttributeAssignment attributeStatus =
                    new StdAttributeAssignment(statCategory, statId, "PDP", attributeStatusValue);
            final StdAttributeAssignment attributeResouce =
                    new StdAttributeAssignment(resCategory, resId, "PDP", attributeResourceValue);
            afterRemoveAssignments.add(attributeStatus);
            afterRemoveAssignments.add(attributeResouce);
            final Advice advice = new StdAdvice(id, afterRemoveAssignments);
            addResult.addAdvice(advice);
        }
    }

    private void addResults(final StdMutableResponse stdResponse, final boolean config, final boolean decide) {
        if (decide) {
            newResponse = stdResponse;
            return;
        }
        for (final Result result : stdResponse.getResults()) {
            if (config) {
                addResult.addAdvice(result.getAssociatedAdvice());
            }
            addResult.addAttributeCategories(result.getAttributes());
            addResult.addPolicyIdentifiers(result.getPolicyIdentifiers());
            addResult.addPolicySetIdentifiers(result.getPolicySetIdentifiers());
            addResult.setStatus(result.getStatus());
            addResult.setDecision(result.getDecision());
            if (!config) {
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
        final Pattern pattern = Pattern.compile("\\$([a-zA-Z0-9.:]*)");
        final Matcher match = pattern.matcher(configUrl);
        final StringBuffer sb = new StringBuffer();
        JsonReader jsonReader = null;
        while (match.find()) {
            logger.info("Found Macro : " + match.group(1));
            final String replaceValue = matchValues.get(match.group(1));
            logger.info("Replacing with :" + replaceValue);
            match.appendReplacement(sb, replaceValue);
        }
        match.appendTail(sb);
        logger.info("URL is : " + sb.toString());
        configUrl = sb.toString();
        // Calling the Requested service.
        if (matchValues.get("method").equalsIgnoreCase("GET")) {
            httpClient = new DefaultHttpClient();
            try {
                final HttpGet getRequest = new HttpGet(configUrl);
                // Adding Headers here
                if (header) {
                    for (final String key : headers.keySet()) {
                        getRequest.addHeader(key, headers.get(key));
                    }
                }
                final HttpResponse result = httpClient.execute(getRequest);
                status = result.getStatusLine().getStatusCode();
                final BufferedReader br = new BufferedReader(new InputStreamReader((result.getEntity().getContent())));
                String output = " ";
                String out;
                while ((out = br.readLine()) != null) {
                    output = output + out;
                }
                response = output;
            } catch (final Exception e) {
                logger.error(e.getMessage() + e);
                response = e.getMessage();
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
        } else if (matchValues.get("method").equalsIgnoreCase("POST")) {
            httpClient = new DefaultHttpClient();
            try {
                final HttpPost postRequest = new HttpPost(configUrl);
                // Adding Headers here
                if (header) {
                    for (final String key : headers.keySet()) {
                        postRequest.addHeader(key, headers.get(key));
                    }
                }
                // Adding the Body.
                final URL configUrl = new URL(matchValues.get("body"));
                URLConnection connection = null;
                connection = configUrl.openConnection();
                // InputStream in = connection.getInputStrem();
                // LOGGER.info("The Body Content is : " + IOUtils.toString(in));
                jsonReader = Json.createReader(connection.getInputStream());
                final StringEntity input = new StringEntity(jsonReader.readObject().toString());
                input.setContentType("application/json");
                postRequest.setEntity(input);
                // Executing the Request.
                final HttpResponse result = httpClient.execute(postRequest);
                logger.info("Result Headers are : " + result.getAllHeaders());
                status = result.getStatusLine().getStatusCode();
                final BufferedReader br = new BufferedReader(new InputStreamReader((result.getEntity().getContent())));
                String output = " ";
                String out;
                while ((out = br.readLine()) != null) {
                    output = output + out;
                }
                response = output;
            } catch (final Exception e) {
                logger.error(e.getMessage() + e);
                response = e.getMessage();
            } finally {
                if (jsonReader != null) {
                    try {
                        jsonReader.close();
                    } catch (final Exception e) {
                        logger.error("Exception Occured while closing the JsonReader" + e);
                    }
                }
                httpClient.getConnectionManager().shutdown();
            }
        } else if (matchValues.get("method").equalsIgnoreCase("PUT")) {
            httpClient = new DefaultHttpClient();
            try {
                final HttpPut putRequest = new HttpPut(configUrl);
                // Adding Headers here
                if (header) {
                    for (final String key : headers.keySet()) {
                        putRequest.addHeader(key, headers.get(key));
                    }
                }
                // Adding the Body.
                final URL configUrl = new URL(matchValues.get("body"));
                URLConnection connection = null;
                connection = configUrl.openConnection();
                // InputStream in = connection.getInputStream();
                // LOGGER.info("The Body Content is : " + IOUtils.toString(in));
                jsonReader = Json.createReader(connection.getInputStream());
                final StringEntity input = new StringEntity(jsonReader.readObject().toString());
                input.setContentType("application/json");
                putRequest.setEntity(input);
                // Executing the Request.
                final HttpResponse result = httpClient.execute(putRequest);
                status = result.getStatusLine().getStatusCode();
                final BufferedReader br = new BufferedReader(new InputStreamReader((result.getEntity().getContent())));
                String output = " ";
                String out;
                while ((out = br.readLine()) != null) {
                    output = output + out;
                }
                response = output;
            } catch (final Exception e) {
                logger.error(e.getMessage() + e);
                response = e.getMessage();
            } finally {
                if (jsonReader != null) {
                    try {
                        jsonReader.close();
                    } catch (final Exception e) {
                        logger.error("Exception Occured while closing the JsonReader" + e);
                    }
                }
                httpClient.getConnectionManager().shutdown();
            }
        }
    }
}
