/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.std.ManualClientEnd;

/**
 * The class <code>ManualClientEndTest</code> contains tests for the class
 * <code>{@link ManualClientEnd}</code>.
 *
 */
public class ManualClientEndTest {
    private static WebSocketServer ws;

    private static int port;
    private static volatile String recvMsg = null;
    private static volatile Exception webEx = null;

    /**
     * Start server.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void startServer() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        ws = new WebSocketServer(new InetSocketAddress(0), 1) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {}

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

            @Override
            public void onMessage(WebSocket conn, String message) {

                // NOTE: must copy to recvMsg BEFORE invoking conn.send()
                recvMsg = message;

                conn.send("{\"removedPolicies\": [],\"loadedPolicies\":"
                        + "[{\"policyName\": \"Test.Config_BRMS_Param_BrmsParamTestPa.1.xml\","
                        + "\"versionNo\": \"1\",\"matches\": {\"ECOMPName\": \"DROOLS\","
                        + "\"ONAPName\": \"DROOLS\",\"ConfigName\": \"BRMS_PARAM_RULE\","
                        + "\"guard\": \"false\",\"TTLDate\": \"NA\",\"RiskLevel\": \"5\","
                        + "\"RiskType\": \"default\"},\"updateType\": \"NEW\"}],"
                        + "\"notificationType\": \"UPDATE\"}");
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                webEx = ex;
                ex.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onStart() {
                latch.countDown();
            }
        };

        ws.setConnectionLostTimeout(0);
        ws.setReuseAddr(true);
        ws.start();

        // ensure port connected (or error) before running the actual test
        latch.await(20, TimeUnit.SECONDS);

        // ensure no error during start-up
        assertNull(webEx);

        port = ws.getPort();
    }

    @Test
    public void testManualClient() throws Exception {
        ManualClientEnd.start("http://localhost:" + port + "/");

        assertNotNull(ManualClientEnd.result(NotificationScheme.MANUAL_ALL_NOTIFICATIONS));
        assertTrue("Manual".equalsIgnoreCase(recvMsg));
    }

    @AfterClass
    public static void successTests() throws InterruptedException, IOException {
        ws.stop(5000);
    }
}
