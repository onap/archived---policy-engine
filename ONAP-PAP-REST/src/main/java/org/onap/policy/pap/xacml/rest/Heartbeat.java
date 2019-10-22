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
import com.att.research.xacml.util.XACMLProperties;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.restAuth.CheckPDP;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;

/**
 * Heartbeat thread - periodically check on PDPs' status.
 * Heartbeat with all known PDPs.
 * Implementation note:
 * The PDPs are contacted Sequentially, not in Parallel.
 * If we did this in parallel using multiple threads we would simultaneously use - 1 thread and - 1
 * connection for EACH PDP. This could become a resource problem since we already use multiple
 * threads and connections for updating the PDPs when user changes occur. Using separate threads can
 * also make it tricky dealing with timeouts on PDPs that are non-responsive.
 * The Sequential operation does a heartbeat request to each PDP one at a time. This has the flaw
 * that any PDPs that do not respond will hold up the entire heartbeat sequence until they timeout.
 * If there are a lot of non-responsive PDPs and the timeout is large-ish (the default is 20
 * seconds) it could take a long time to cycle through all of the PDPs. That means that this may not
 * notice a PDP being down in a predictable time.
 */
public class Heartbeat implements Runnable {

    private static final Logger LOGGER = FlexLogger.getLogger(Heartbeat.class);

    private PAPPolicyEngine papEngine;
    private Set<OnapPDP> pdps = new HashSet<>();
    private int heartbeatInterval;
    private int heartbeatTimeout;
    private static final String HEARTBEATSTRING = " Heartbeat '";
    private static final String XACMLPAPSERVLET = "XacmlPapServlet";
    private static final String ISRUNNINGFALSE = "isRunning is false, getting out of loop.";
    private volatile boolean isRunning = false;

    public synchronized boolean isHeartBeatRunning() {
        return this.isRunning;
    }

    public synchronized void terminate() {
        this.isRunning = false;
    }

    /**
     * Instantiates a new heartbeat.
     *
     * @param papEngine2 the pap engine 2
     */
    public Heartbeat(PAPPolicyEngine papEngine2) {
        papEngine = papEngine2;
        this.heartbeatInterval =
                Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_INTERVAL, "10000"));
        this.heartbeatTimeout =
                Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_TIMEOUT, "10000"));
    }

    @Override
    public void run() {
        // Set ourselves as running
        synchronized (this) {
            this.isRunning = true;
        }
        try {
            while (this.isHeartBeatRunning()) {
                // Wait the given time
                Thread.sleep(heartbeatInterval);
                // get the list of PDPs (may have changed since last time)
                pdps.clear();
                synchronized (papEngine) {
                    getPdpsFromGroup();
                }
                // Check for shutdown
                if (!this.isHeartBeatRunning()) {
                    LOGGER.info(ISRUNNINGFALSE);
                    break;
                }
                notifyEachPdp();
                // Check for shutdown
                if (!this.isHeartBeatRunning()) {
                    LOGGER.info(ISRUNNINGFALSE);
                    break;
                }
            }
        } catch (InterruptedException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " Heartbeat interrupted.  Shutting down");
            this.terminate();
            Thread.currentThread().interrupt();
        }
    }

    private void getPdpsFromGroup() {
        try {
            for (OnapPDPGroup g : papEngine.getOnapPDPGroups()) {
                for (OnapPDP p : g.getOnapPdps()) {
                    pdps.add(p);
                }
            }
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    "Heartbeat unable to read PDPs from PAPEngine");
        }
    }

    private void notifyEachPdp() {
        HashMap<String, URL> idToUrlMap = new HashMap<>();
        for (OnapPDP pdp : pdps) {
            // Check for shutdown
            if (!this.isHeartBeatRunning()) {
                LOGGER.info(ISRUNNINGFALSE);
                break;
            }
            // the id of the PDP is its url (though we add a query
            // parameter)
            URL pdpUrl = idToUrlMap.get(pdp.getId());
            if (pdpUrl == null) {
                // haven't seen this PDP before
                String fullUrlString = null;
                try {
                    // Check PDP ID
                    if (CheckPDP.validateID(pdp.getId())) {
                        fullUrlString = pdp.getId() + "?type=hb";
                        pdpUrl = new URL(fullUrlString);
                        idToUrlMap.put(pdp.getId(), pdpUrl);
                    }
                } catch (MalformedURLException e) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, XACMLPAPSERVLET,
                            " PDP id '" + fullUrlString + "' is not a valid URL");
                }
            }
            updatePdpStatus(pdp, openPdpConnection(pdpUrl, pdp));
        }
    }

    private String openPdpConnection(URL pdpUrl, OnapPDP pdp) {
        // Do a GET with type HeartBeat
        String newStatus = "";
        HttpURLConnection connection = null;
        try {
            // Open up the connection
            if (pdpUrl != null) {
                connection = (HttpURLConnection) pdpUrl.openConnection();
                // Setup our method and headers
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(heartbeatTimeout);
                // Authentication
                String encoding = CheckPDP.getEncoding(pdp.getId());
                if (encoding != null) {
                    connection.setRequestProperty("Authorization", "Basic " + encoding);
                }
                // Do the connect
                connection.connect();
                if (connection.getResponseCode() == 204) {
                    newStatus = connection.getHeaderField(XACMLRestProperties.PROP_PDP_HTTP_HEADER_HB);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Heartbeat '" + pdp.getId() + "' status='" + newStatus + "'");
                    }
                } else {
                    // anything else is an unexpected result
                    newStatus = PDPStatus.Status.UNKNOWN.toString();
                    PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " Heartbeat connect response code "
                            + connection.getResponseCode() + ": " + pdp.getId());
                }
            }
        } catch (UnknownHostException e) {
            newStatus = PDPStatus.Status.NO_SUCH_HOST.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    HEARTBEATSTRING + pdp.getId() + "' NO_SUCH_HOST");
        } catch (SocketTimeoutException e) {
            newStatus = PDPStatus.Status.CANNOT_CONNECT.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    HEARTBEATSTRING + pdp.getId() + "' connection timeout");
        } catch (ConnectException e) {
            newStatus = PDPStatus.Status.CANNOT_CONNECT.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    HEARTBEATSTRING + pdp.getId() + "' cannot connect");
        } catch (Exception e) {
            newStatus = PDPStatus.Status.UNKNOWN.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, XACMLPAPSERVLET,
                    HEARTBEATSTRING + pdp.getId() + "' connect exception");
        } finally {
            // cleanup the connection
            if (connection != null)
                connection.disconnect();
        }
        return newStatus;
    }

    private void updatePdpStatus(OnapPDP pdp, String newStatus) {
        if (!pdp.getStatus().getStatus().toString().equals(newStatus)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("previous status='" + pdp.getStatus().getStatus() + "'  new Status='" + newStatus + "'");
            }
            try {
                getPAPInstance().setPDPSummaryStatus(pdp, newStatus);
            } catch (PAPException e) {
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, XACMLPAPSERVLET,
                        "Unable to set state for PDP '" + pdp.getId());
            }
        }
    }

    private XACMLPapServlet getPAPInstance() {
        return new XACMLPapServlet();
    }

}
