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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.std.pap.StdPDP;

public class UpdatePdpThreadTest {
    @Test
    public void testConstructor1() {
        UpdatePdpThread thread = new UpdatePdpThread(null, null);
        assertNotNull(thread);
    }

    @Test
    public void testConstructor2() {
        OnapLoggingContext loggingContext = new OnapLoggingContext();
        loggingContext.setRequestId("id");
        UpdatePdpThread thread = new UpdatePdpThread(null, loggingContext, null);
        assertNotNull(thread);
    }

    @Test
    public void testRun() {
        OnapPDP pdp = new StdPDP("http://id", 0);
        OnapLoggingContext loggingContext = new OnapLoggingContext();
        loggingContext.setRequestId("id");
        List<Properties> properties = new ArrayList<Properties>();
        UpdatePdpThread thread = new UpdatePdpThread(pdp, loggingContext, properties);
        assertThatCode(thread::run).doesNotThrowAnyException();
    }
}
