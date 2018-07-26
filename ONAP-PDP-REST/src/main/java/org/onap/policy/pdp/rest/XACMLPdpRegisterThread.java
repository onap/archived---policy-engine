/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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


package org.onap.policy.pdp.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.onap.policy.pdp.rest.XACMLPdpServlet.PutRequest;
import org.onap.policy.rest.XACMLRestProperties;

import org.onap.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.util.XACMLProperties;

import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.*; 

public class XACMLPdpRegisterThread implements Runnable {
    private static final Logger LOGGER	= FlexLogger.getLogger(XACMLPdpRegisterThread.class);
    private static final Logger auditLogger = FlexLogger.getLogger("auditLogger");
    private ONAPLoggingContext baseLoggingContext = null;



    public volatile boolean isRunning = false;

    public XACMLPdpRegisterThread(ONAPLoggingContext baseLoggingContext) {
        this.baseLoggingContext = baseLoggingContext;
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void terminate() {
        this.isRunning = false;
    }

    /**
     *
     * This is our thread that runs on startup to tell the PAP server we are up-and-running.
     *
     */
    @Override
    public void run() {
        synchronized(this) {
            this.isRunning = true;
        }
        // get a new logging context for the thread
        ONAPLoggingContext loggingContext = new ONAPLoggingContext(baseLoggingContext);
        loggingContext.setServiceName("PDP:PAP.register");
        //are we registered with at least one
        boolean registered = false;
        boolean interrupted = false;
        /*
        int seconds;
        try {
            seconds = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_REGISTER_SLEEP));
        } catch (NumberFormatException e) {
            LOGGER.error( XACMLErrorConstants.ERROR_SYSTEM_ERROR +"REGISTER_SLEEP: ", e);
            seconds = 5;
        }
        if (seconds < 5) {
            seconds = 5;
        }
        int retries;
        try {
            retries = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_REGISTER_RETRIES));
        } catch (NumberFormatException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR +" REGISTER_SLEEP: ", e);
            retries = -1;
        }
        */
        PapUrlResolver papUrls = PapUrlResolver.getInstance();
        //while (! registered && ! interrupted && this.isRunning()) {
        String tempRootPoliciesProperty = XACMLProperties.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        String tempReferencedPoliciesProperty = XACMLProperties.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        Properties tempPipConfigProperties = new Properties();
        try(InputStream pipFile = Files.newInputStream(XACMLPdpLoader.getPIPConfig())){
            tempPipConfigProperties.load(pipFile);
        } catch(Exception e){
            LOGGER.error("Failed to open PIP property file", e);
        }
        while(papUrls.hasMoreUrls()){
            String papID = papUrls.getUserId();
            String papPass = papUrls.getPass();
            Base64.Encoder encoder = Base64.getEncoder();
            String encoding = encoder.encodeToString((papID+":"+papPass).getBytes(StandardCharsets.UTF_8));
            HttpURLConnection connection = null;
            try {
                // get a new transaction (request) ID and update the logging context.
                // each time through the outer loop is considered a new transaction.
                // each time through the inner loop (which handles redirects) is a
                // continuation of the same transaction.
                UUID requestID = UUID.randomUUID();
                loggingContext.setRequestID(requestID.toString());
                //PolicyLogger.info("Request Id generated in XACMLPdpRegisterThread under XACML-PDP-REST");
                loggingContext.transactionStarted();
                //
                // Get the list of PAP Servlet URLs from the property file
                //
                //String papUrlList = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
                //String[] papUrls = papUrlList.split(",");
                //PapUrlResolver.setPapUrls(papUrls);
                URL url = new URL(papUrls.getUrl());
                LOGGER.info("Registering with " + url.toString());
                //PolicyLogger.info("new transaction (request) ID and update to logging context in XACMLPdpRegisterThread");
                boolean finished = false;
                while (! finished) {
                    //
                    // Open up the connection
                    //
                    connection = (HttpURLConnection)url.openConnection();
                    //
                    // Setup our method and headers
                    //
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Basic " + encoding);
                    connection.setRequestProperty("Accept", "text/x-java-properties");
                    connection.setRequestProperty("Content-Type", "text/x-java-properties");
                    connection.setRequestProperty(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID, XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_ID));
                    connection.setRequestProperty(XACMLRestProperties.PROP_PDP_HTTP_HEADER_JMX_PORT, XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_JMX_PORT));
                    connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
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
                        //
                        // Send our current policy configuration
                        //
                        String lists = XACMLProperties.PROP_ROOTPOLICIES + "=" + tempRootPoliciesProperty;
                        lists = lists + "\n" + XACMLProperties.PROP_REFERENCEDPOLICIES + "=" + tempReferencedPoliciesProperty + "\n";
                        try (InputStream listsInputStream = new ByteArrayInputStream(lists.getBytes());
                                OutputStream os = connection.getOutputStream()) {
                            IOUtils.copy(listsInputStream, os);

                            //
                            // Send our current PIP configuration
                            //
                            //IOUtils.copy(pipInputStream, os);
                            tempPipConfigProperties.store(os, "");
                        }
                    } catch (Exception e) {
                        LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Failed to send property file", e);
                    }
                    //
                    // Do the connect
                    //
                    connection.connect();
                    if (connection.getResponseCode() == 204) {
                        LOGGER.info("Success. We are configured correctly.");
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Success. We are configured correctly.");
                        papUrls.registered();
                        finished = true;
                        registered = true;
                    } else if (connection.getResponseCode() == 200) {
                        LOGGER.info("Success. We have a new configuration.");
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Success. We have a new configuration.");
                        papUrls.registered();
                        Properties properties = new Properties();
                        properties.load(connection.getInputStream());
                        LOGGER.info("New properties: " + properties.toString());
                        //
                        // Queue it
                        //
                        // The incoming properties does NOT include urls
                        Properties returnedPolicyProperties = XACMLProperties.getPolicyProperties(properties, false);
                        tempRootPoliciesProperty = returnedPolicyProperties.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
                        tempReferencedPoliciesProperty = returnedPolicyProperties.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
                        Properties returnedPipProperties = XACMLProperties.getPipProperties(properties);
                        Properties threadSafeReturnedPipProperties = new Properties();
                        ByteArrayOutputStream threadSafeReturnedPipPropertiesOs = new ByteArrayOutputStream();
                        returnedPipProperties.store(threadSafeReturnedPipPropertiesOs, "");
                        InputStream threadSafeReturnedPipPropertiesIs = new ByteArrayInputStream(threadSafeReturnedPipPropertiesOs.toByteArray());
                        threadSafeReturnedPipProperties.load(threadSafeReturnedPipPropertiesIs);
                        tempPipConfigProperties = threadSafeReturnedPipProperties;

                        PutRequest req = new PutRequest(returnedPolicyProperties,returnedPipProperties);
                        XACMLPdpServlet.queue.offer(req);
                        //
                        // We are now registered
                        //
                        finished = true;
                        registered=true;
                    } else if (connection.getResponseCode() >= 300 && connection.getResponseCode()  <= 399) {
                        //
                        // Re-direction
                        //
                        String newLocation = connection.getHeaderField("Location");
                        if (newLocation == null || newLocation.isEmpty()) {
                            LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Did not receive a valid re-direction location");
                            loggingContext.transactionEnded();
                            auditLogger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR +"Did not receive a valid re-direction location");
                            PolicyLogger.audit("Transaction Failed - See Error.log");
                            finished = true;
                        } else {
                            LOGGER.info("New Location: " + newLocation);
                            url = new URL(newLocation);
                        }
                    } else {
                        LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                        loggingContext.transactionEnded();
                        auditLogger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        finished = true;
                        papUrls.failed();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                papUrls.failed();
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
                            LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to get Input Stream: " + e1);
                        }
                        if (is != null) {
                            is.close();
                        }

                    } catch (IOException ex) {
                        LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to close connection: " + ex, ex);
                    }
                    connection.disconnect();
                }
            }

            papUrls.getNext();
        }
        synchronized(this) {
            this.isRunning = false;
        }
        LOGGER.info("Thread exiting...(registered=" + registered + ", interrupted=" + interrupted + ", isRunning=" + this.isRunning() + ", retries=" + "0" + ")");
    }

}
