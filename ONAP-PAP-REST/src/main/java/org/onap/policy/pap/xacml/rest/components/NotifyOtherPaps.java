/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.pap.xacml.rest.components;

import com.att.research.xacml.util.XACMLProperties;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.jpa.PolicyDBDaoEntity;
import org.onap.policy.utils.PeCryptoUtils;

public class NotifyOtherPaps {

    private static final Logger LOGGER = FlexLogger.getLogger(NotifyOtherPaps.class);
    private List<PolicyDBDaoEntity> failedPaps = null;

    public void notifyOthers(long entityId, String entityType) {
        notifyOthers(entityId, entityType, null);
    }

    /**
     * Notify others.
     *
     * @param entityId the entity id
     * @param entityType the entity type
     * @param newGroupId the new group id
     */
    public void notifyOthers(long entityId, String entityType, String newGroupId) {
        LOGGER.debug("notifyOthers(long entityId, String entityType, long newGroupId) as notifyOthers(" + entityId + ","
                + entityType + "," + newGroupId + ") called");
        failedPaps = new ArrayList<>();

        List<?> otherServers = PolicyDbDao.getPolicyDbDaoInstance().getOtherServers();
        // Notify other paps
        startNotifyThreads(otherServers, entityId, entityType, newGroupId);
        // Retry for failed paps
        if (!failedPaps.isEmpty()) {
            startNotifyThreads(failedPaps, entityId, entityType, newGroupId);
        }
    }

    private void startNotifyThreads(List<?> otherServers, long entityId, String entityType, String newGroupId) {
        LinkedList<Thread> notifyThreads = new LinkedList<>();
        // we're going to run notifications in parallel threads to speed things
        // up
        for (Object obj : otherServers) {
            Thread newNotifyThread = new Thread(new NotifyOtherThread(obj, entityId, entityType, newGroupId));
            newNotifyThread.start();
            notifyThreads.add(newNotifyThread);
        }
        // we want to wait for all notifications to complete or timeout before
        // we unlock the interface and allow more changes
        for (Thread t : notifyThreads) {
            try {
                t.join();
            } catch (Exception e) {
                LOGGER.warn("Could not join a notifcation thread" + e);
            }
        }
    }

    private class NotifyOtherThread implements Runnable {
        public NotifyOtherThread(Object obj, long entityId, String entityType, String newGroupId) {
            this.obj = obj;
            this.entityId = entityId;
            this.entityType = entityType;
            this.newGroupId = newGroupId;
        }

        private Object obj;
        private long entityId;
        private String entityType;
        private String newGroupId;

        @Override
        public void run() {
            PolicyDbDao dao = new PolicyDbDao();
            PolicyDBDaoEntity dbdEntity = (PolicyDBDaoEntity) obj;
            String otherPap = dbdEntity.getPolicyDBDaoUrl();
            String txt;
            try {
                txt = PeCryptoUtils.decrypt(dbdEntity.getPassword());
            } catch (Exception e) {
                LOGGER.debug(e);
                // if we can't decrypt, might as well try it anyway
                txt = dbdEntity.getPassword();
            }

            HttpURLConnection connection = null;
            URL url;
            String papUrl;
            try {
                String[] papUrlUserPass = dao.getPapUrlUserPass();
                if (papUrlUserPass == null) {
                    papUrl = "undefined";
                } else {
                    papUrl = papUrlUserPass[0];
                }
                LOGGER.debug("We are going to try to notify " + otherPap);
                // is this our own url?
                String ourUrl = otherPap;
                try {
                    ourUrl = dao.splitPapUrlUserPass(otherPap)[0];
                } catch (Exception e) {
                    ourUrl = otherPap;
                    LOGGER.debug(e);
                }
                if (otherPap == null) {
                    otherPap = "undefined";
                }
                if (papUrl.equals(ourUrl)) {
                    LOGGER.debug(otherPap + " is our url, skipping notify");
                    return;
                }
                if (newGroupId == null) {
                    url = new URL(otherPap + "?policydbdaourl=" + papUrl + "&entityid=" + entityId + "&entitytype="
                            + entityType);
                } else {
                    url = new URL(otherPap + "?policydbdaourl=" + papUrl + "&entityid=" + entityId + "&entitytype="
                            + entityType + "&extradata=" + newGroupId);
                }
            } catch (MalformedURLException e) {
                LOGGER.warn("Caught MalformedURLException on: new URL()", e);
                return;
            }
            //
            // Open up the connection
            //
            LOGGER.info("PolicyDBDao: NotifyOtherThread: notifying other PAPs of an update");
            LOGGER.info("Connecting with url: " + url);
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (Exception e) {
                LOGGER.warn("Caught exception on: url.openConnection()", e);
                return;
            }
            //
            // Setup our method and headers
            //
            try {
                connection.setRequestMethod("PUT");
            } catch (ProtocolException e) {
                // why would this error ever occur?
                LOGGER.warn("Caught ProtocolException on connection.setRequestMethod(\"PUT\");", e);
                return;
            }

            String username = dbdEntity.getUsername();
            UUID requestId = UUID.randomUUID();

            Base64.Encoder encoder = Base64.getEncoder();
            String encoding = encoder.encodeToString((username + ":" + txt).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("Accept", "text/x-java-properties");
            connection.setRequestProperty("Content-Type", "text/x-java-properties");
            connection.setRequestProperty("requestID", requestId.toString());
            int readTimeout;
            try {
                readTimeout =
                        Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_NOTIFY_TIMEOUT));
            } catch (Exception e) {
                LOGGER.error("xacml.rest.pap.notify.timeoutms property not set, using a default.", e);
                readTimeout = 10000;
            }
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(readTimeout);
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
            } catch (Exception e) {
                LOGGER.warn("Caught exception on: connection.connect()", e);
                return;
            }
            try {
                if (connection.getResponseCode() == 200) {
                    LOGGER.info("PolicyDBDao: NotifyOtherThread received response 200 from pap server on notify");
                } else {
                    LOGGER.warn("PolicyDBDao: NotifyOtherThread connection response code not 200, received: "
                            + connection.getResponseCode());
                    failedPaps.add(dbdEntity);
                }
            } catch (Exception e) {
                LOGGER.warn("Caught Exception on: connection.getResponseCode() ", e);
            }

            connection.disconnect();
        }
    }
}
