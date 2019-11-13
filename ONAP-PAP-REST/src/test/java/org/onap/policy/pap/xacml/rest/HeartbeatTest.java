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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.att.research.xacml.api.pap.PAPException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;

public class HeartbeatTest {
    @Test
    public void testHeartbeat() throws IOException, PAPException {
        Heartbeat hb = new Heartbeat(null);

        assertThatThrownBy(hb::run).isInstanceOf(NullPointerException.class);
        assertTrue(hb.isHeartBeatRunning());
        hb.terminate();
        assertFalse(hb.isHeartBeatRunning());
    }

    @Test
    public void testGetPdps() throws PAPException, IOException {
        Set<OnapPDPGroup> pdpGroups = new HashSet<OnapPDPGroup>();
        StdPDPGroup pdpGroup = new StdPDPGroup();
        OnapPDP pdp = new StdPDP();
        pdpGroup.addPDP(pdp);
        pdpGroups.add(pdpGroup);
        PAPPolicyEngine pap = Mockito.mock(PAPPolicyEngine.class);
        Mockito.when(pap.getOnapPDPGroups()).thenReturn(pdpGroups);
        Heartbeat hb = new Heartbeat(pap);
        hb.getPdpsFromGroup();
        assertFalse(hb.isHeartBeatRunning());

        assertThatCode(hb::notifyEachPdp).doesNotThrowAnyException();
        assertThatThrownBy(hb::run).isInstanceOf(Exception.class);
        assertThatThrownBy(hb::notifyEachPdp).isInstanceOf(Exception.class);
    }

    @Test
    public void testOpen() throws MalformedURLException {
        Heartbeat hb = new Heartbeat(null);
        OnapPDP pdp = new StdPDP();

        assertThatCode(() -> {
            URL url = new URL("http://onap.org");
            hb.openPdpConnection(url, pdp);
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            URL url = new URL("http://1.2.3.4");
            hb.openPdpConnection(url, pdp);
        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            URL url = new URL("http://fakesite.fakenews");
            hb.openPdpConnection(url, pdp);
        }).doesNotThrowAnyException();
    }
}
