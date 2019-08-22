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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.api.PolicyException;
import org.onap.policy.brms.api.BrmsGateway.Factory;
import org.powermock.reflect.Whitebox;

public class BrmsGatewayTest {
    private static final String FACTORY_FIELD = "factory";

    private static Factory saveFactory;

    private Thread thread;

    @BeforeClass
    public static void setUpBeforeClass() {
        saveFactory = Whitebox.getInternalState(BrmsGateway.class, FACTORY_FIELD);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        Whitebox.setInternalState(BrmsGateway.class, FACTORY_FIELD, saveFactory);
    }

    /**
     * Installs a factory.
     */
    @Before
    public void setUp() {
        thread = null;

        Factory factory = new Factory() {
            @Override
            public BrmsHandler makeBrmsHandler(String configFile) throws PolicyException {
                // Mock handler
                return Mockito.mock(BrmsHandler.class);
            }

            @Override
            public Thread makeThread(Runnable runnable) {
                thread = super.makeThread(runnable);
                return thread;
            }

        };

        Whitebox.setInternalState(BrmsGateway.class, FACTORY_FIELD, factory);
    }

    /**
     * Interrupts the thread, if there is one.
     */
    @After
    public void tearDown() throws InterruptedException {
        if (thread != null) {
            thread.interrupt();
            thread.join(5000L);
            assertFalse(thread.isAlive());
        }
    }

    @Test
    public void testFactory() throws InterruptedException {
        assertNotNull(saveFactory);
        assertNotNull(saveFactory.makeThread(() -> { }));
    }

    @Test
    public void testGet() {
        assertNull(BrmsGateway.getPolicyEngine());
    }

    @Test
    public void testMain() throws Exception {
        try {
            final String[] args = new String[0];
            BrmsGateway.main(args);
        } catch (final Exception ex) {
            fail("Not expected an exception: " + ex);
        }
    }
}
