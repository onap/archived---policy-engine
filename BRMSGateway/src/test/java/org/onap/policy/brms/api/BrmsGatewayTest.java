/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.brms.api;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class BrmsGatewayTest {
    @Test
    public void testGet() {
        assertNull(BrmsGateway.getPolicyEngine());
    }

    @PrepareForTest({Thread.class, BrmsGateway.class})
    @Test
    public void testMain() throws Exception {
        // Mock Thread
        PowerMockito.spy(Thread.class);
        PowerMockito.doNothing().when(Thread.class);
        Thread.sleep(1000);

        // Mock handler
        final BrmsHandler handler = Mockito.mock(BrmsHandler.class);
        PowerMockito.whenNew(BrmsHandler.class).withArguments(any()).thenReturn(handler);

        // Run app
        try {
            final String[] args = new String[0];
            BrmsGateway.main(args);
        } catch (final Exception ex) {
            fail("Not expected an exception: " + ex);
        }
    }
}
