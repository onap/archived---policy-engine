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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.NotificationScheme;
import org.onap.policy.std.ManualClientEnd;
import org.springframework.util.SocketUtils;

/**
 * The class <code>ManualClientEndTest</code> contains tests for the class <code>{@link ManualClientEnd}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class ManualClientEndTest {
    private static WebSocketServer ws;

    private static int port = 18080;
    private static CountDownLatch countServerDownLatch = null;
    private static String recvMsg = null;

    /**
     * Start server.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void startServer() throws Exception {
        port = SocketUtils.findAvailableTcpPort();
        ws = new WebSocketServer(new InetSocketAddress(port), 16) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {

            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                countServerDownLatch.countDown();
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                recvMsg = message;
                conn.send("{\"removedPolicies\": [],\"loadedPolicies\":"
                        + "[{\"policyName\": \"Test.Config_BRMS_Param_BrmsParamTestPa.1.xml\","
                        + "\"versionNo\": \"1\",\"matches\": {\"ECOMPName\": \"DROOLS\","
                        + "\"ONAPName\": \"DROOLS\",\"ConfigName\": \"BRMS_PARAM_RULE\","
                        + "\"guard\": \"false\",\"TTLDate\": \"NA\",\"RiskLevel\": \"5\","
                        + "\"RiskType\": \"default\"},\"updateType\": \"NEW\"}],\"notificationType\": \"UPDATE\"}");
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {

                ex.printStackTrace();
                fail("There should be no exception!");
            }

            @Override
            public void onStart() {}


        };

        ws.setConnectionLostTimeout(30);
        ws.start();
    }

    @Test
    public void testAutoClient() throws Exception {
        countServerDownLatch = new CountDownLatch(1);

        ManualClientEnd.start("http://localhost:" + port + "/");
        countServerDownLatch.await();

        assertNotNull(ManualClientEnd.result(NotificationScheme.MANUAL_ALL_NOTIFICATIONS));
        assertTrue("Manual".equalsIgnoreCase(recvMsg));
    }

    @AfterClass
    public static void successTests() throws InterruptedException, IOException {
        ws.stop();
    }
}
