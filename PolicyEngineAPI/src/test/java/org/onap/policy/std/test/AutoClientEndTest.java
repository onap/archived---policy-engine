/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.std.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.NotificationHandler;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.std.AutoClientEnd;
import org.springframework.util.SocketUtils;

/**
 * The class <code>AutoClientEndTest</code> contains tests for the class <code>{@link AutoClientEnd}</code>.
 *
 */
public class AutoClientEndTest {
    private static WebSocketServer ws;

    private static int port = SocketUtils.findAvailableTcpPort();
    private static CountDownLatch countServerDownLatch = null;
    private static PDPNotification notification = null;

    /**
     * Start server.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void startServer() throws Exception {
        notification = null;
        ws = new WebSocketServer(new InetSocketAddress(port), 1) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                conn.send("{\"removedPolicies\": [],\"loadedPolicies\": "
                        + "[{\"policyName\": \"Test.Config_BRMS_Param_BrmsParamTestPa.1.xml\","
                        + "\"versionNo\": \"1\",\"matches\": {\"ECOMPName\": \"DROOLS\","
                        + "\"ONAPName\": \"DROOLS\",\"ConfigName\": \"BRMS_PARAM_RULE\","
                        + "\"guard\": \"false\",\"TTLDate\": \"NA\",\"RiskLevel\": \"5\","
                        + "\"RiskType\": \"default\"},\"updateType\": \"NEW\"}],\"notificationType\": \"UPDATE\"}");
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

            @Override
            public void onMessage(WebSocket conn, String message) {}

            @Override
            public void onError(WebSocket conn, Exception ex) {

                ex.printStackTrace();
                fail("There should be no exception!");
            }

            @Override
            public void onStart() {}


        };

        ws.setConnectionLostTimeout(0);
        ws.setReuseAddr(true);
        ws.start();
    }

    @Test
    public void testAutoClient() throws Exception {

        NotificationScheme scheme = NotificationScheme.AUTO_ALL_NOTIFICATIONS;
        NotificationHandler handler = new NotificationHandler() {

            @Override
            public void notificationReceived(PDPNotification notify) {
                notification = notify;
                countServerDownLatch.countDown();

            }
        };

        AutoClientEnd.setAuto(scheme, handler);
        countServerDownLatch = new CountDownLatch(1);

        AutoClientEnd.start("http://localhost:" + port + "/");
        countServerDownLatch.await(45, TimeUnit.SECONDS);

        // simulate a server restart and verify client reconnects
        countServerDownLatch = new CountDownLatch(1);
        ws.stop(30000);
        startServer();
        countServerDownLatch.await(60 + 10, TimeUnit.SECONDS);

        AutoClientEnd.stop();

    }

    @AfterClass
    public static void stopServer() throws InterruptedException, IOException {
        ws.stop(30000);
    }
}
