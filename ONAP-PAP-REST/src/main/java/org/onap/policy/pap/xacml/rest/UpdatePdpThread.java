/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPStatus;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.restAuth.CheckPDP;
import org.onap.policy.xacml.api.pap.OnapPDP;

public class UpdatePdpThread implements Runnable {

    private static final Logger LOGGER = FlexLogger.getLogger(UpdatePdpThread.class);
    private OnapLoggingContext baseLoggingContext = new XACMLPapServlet().getBaseLoggingContext();
    private static final Logger auditLogger = FlexLogger.getLogger("auditLogger");
    private static final String XACMLPAPSERVLET = "XACMLPapServlet";
    private static final String MESSAGE = "  message: ";

    private OnapPDP pdp;
    private String requestId;
    private OnapLoggingContext loggingContext;
    private List<Properties> properties;

    public UpdatePdpThread(OnapPDP pdp, List<Properties> properties) {
        this.pdp = pdp;
        this.properties = properties;
    }

    /**
     * Instantiates a new update pdp thread.
     *
     * @param pdp the pdp
     * @param loggingContext the logging context
     * @param properties the properties
     */
    public UpdatePdpThread(OnapPDP pdp, OnapLoggingContext loggingContext, List<Properties> properties) {
        this.pdp = pdp;
        if (loggingContext != null
                && (loggingContext.getRequestId() != null || "".equals(loggingContext.getRequestId()))) {
            this.requestId = loggingContext.getRequestId();
        }
        this.loggingContext = loggingContext;
        this.properties = properties;
    }

    @Override
    public void run() {
        // send the current configuration to one PDP
        HttpURLConnection connection = null;
        // get a new logging context for the thread
        try {
            if (this.loggingContext == null) {
                loggingContext = new OnapLoggingContext(baseLoggingContext);
            }
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    " Failed to send property file to " + pdp.getId());
            // Since this is a server-side error, it probably does not
            // reflect a problem on the client,
            // so do not change the PDP status.
            return;
        }
        try {
            loggingContext.setServiceName("PAP:PDP.putConfig");
            // If a requestId was provided, use it, otherwise generate one;
            // post to loggingContext to be used later when calling PDP
            if (requestId == null || "".equals(requestId)) {
                UUID requestId = UUID.randomUUID();
                loggingContext.setRequestId(requestId.toString());
                PolicyLogger
                        .info("requestID not provided in call to XACMLPapSrvlet (UpdatePDPThread) so we generated one: "
                                + loggingContext.getRequestId());
            } else {
                loggingContext.setRequestId(requestId);
                PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (UpdatePDPThread):  "
                        + loggingContext.getRequestId());
            }
            loggingContext.transactionStarted();
            // the Id of the PDP is its URL
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating url for id '" + pdp.getId() + "'");
            }
            URL url = new URL(pdp.getId() + "?cache=all");
            // Open up the connection
            connection = (HttpURLConnection) url.openConnection();
            // Setup our method and headers
            connection.setRequestMethod("PUT");
            // Authentication
            String encoding = CheckPDP.getEncoding(pdp.getId());
            if (encoding != null) {
                connection.setRequestProperty("Authorization", "Basic " + encoding);
            }
            connection.setRequestProperty("Content-Type", "text/x-java-properties");
            connection.setRequestProperty("X-ECOMP-RequestID", loggingContext.getRequestId());
            connection.setInstanceFollowRedirects(true);
            connection.setDoOutput(true);
            if (!writePropertiesToStream(connection)) {
                return;
            }
            // Do the connect
            loggingContext.metricStarted();
            connection.connect();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet UpdatePDPThread connection connect");
            if (connection.getResponseCode() == 204) {
                LOGGER.info("Success. We are configured correctly - " + pdp.getId());
                loggingContext.transactionEnded();
                auditLogger.info("Success. PDP is configured correctly.");
                PolicyLogger.audit("Transaction Success. PDP is configured correctly.");
                getPapInstance().setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);
            } else if (connection.getResponseCode() == 200) {
                LOGGER.info("Success. PDP needs to update its configuration - " + pdp.getId());
                loggingContext.transactionEnded();
                auditLogger.info("Success. PDP needs to update its configuration.");
                PolicyLogger.audit("Transaction Success. PDP is configured correctly.");
                getPapInstance().setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
            } else {
                LOGGER.warn("Failed: " + connection.getResponseCode() + MESSAGE + connection.getResponseMessage()
                        + pdp.getId());
                loggingContext.transactionEnded();
                auditLogger.warn("Failed: " + connection.getResponseCode() + MESSAGE + connection.getResponseMessage());
                PolicyLogger.audit("Transaction Failed: " + connection.getResponseCode() + MESSAGE
                        + connection.getResponseMessage());
                getPapInstance().setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
            }
        } catch (Exception e) {
            LOGGER.debug(e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    " Unable to sync config with PDP '" + pdp.getId() + "'");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed: Unable to sync config with PDP '" + pdp.getId() + "': " + e);
            LOGGER.info("Transaction Failed: Unable to sync config with PDP '" + pdp.getId() + "': " + e);
            try {
                getPapInstance().setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
            } catch (PAPException e1) {
                LOGGER.debug(e1);
                PolicyLogger
                        .audit("Transaction Failed: Unable to set status of PDP " + pdp.getId() + " to UNKNOWN: " + e);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                        " Unable to set status of PDP '" + pdp.getId() + "' to UNKNOWN");
            }
        } finally {
            // cleanup the connection
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private boolean writePropertiesToStream(HttpURLConnection connection) {
        try (OutputStream os = connection.getOutputStream()) {
            // Policy Properties
            properties.get(0).store(os, "");
            // Pip Properties
            properties.get(1).store(os, "");
            // Policy Locations
            if (properties.size() == 3) {
                properties.get(2).store(os, "");
            }
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    " Failed to send property file to " + pdp.getId());
            // Since this is a server-side error, it probably does not
            // reflect a problem on the client,
            // so do not change the PDP status.
            return false;
        }
        return true;
    }

    private XACMLPapServlet getPapInstance() {
        return new XACMLPapServlet();
    }

}
