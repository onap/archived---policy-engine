/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
 * ================================================================================
 * Copyright (C) 2017-2018, 2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.xacml.pdp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.FactoryFinder;
import com.att.research.xacmlatt.pdp.eval.EvaluationContextFactory;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest(OnapPdpEngineFactory.class)
public class OnapPdpEngineFactoryTest {
    @Test
    public final void testNewEngine() {
        final OnapPdpEngineFactory pdpEngine = new OnapPdpEngineFactory();
        try {
            assertTrue(pdpEngine.newEngine() != null);
        } catch (final Exception e) {
            fail("operation failed, e=" + e);
        }
    }

    @Test
    public final void testNewEngineProperties() {
        final OnapPdpEngineFactory pdpEngine = new OnapPdpEngineFactory();
        final Properties properties = new Properties();
        try {
            assertTrue(pdpEngine.newEngine(properties) != null);
        } catch (final Exception e) {
            fail("operation failed, e=" + e);
        }
    }

    @PrepareForTest({ EvaluationContextFactory.class, FactoryFinder.class })
    @Test(expected = FactoryException.class)
    public void testNegTestEngine() throws FactoryException {
        // Setup test data
        PowerMockito.mockStatic(FactoryFinder.class);
        PowerMockito.when(FactoryFinder.find(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);

        // Negative test factory
        final OnapPdpEngineFactory factory = new OnapPdpEngineFactory();
        factory.newEngine();
        fail("Expecting an exception.");
    }

    @PrepareForTest({ EvaluationContextFactory.class, FactoryFinder.class })
    @Test(expected = FactoryException.class)
    public void testNegTestEngine2() throws FactoryException {
        // Setup test data
        PowerMockito.mockStatic(FactoryFinder.class);
        PowerMockito.when(FactoryFinder.find(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);

        // Negative test factory
        final OnapPdpEngineFactory factory = new OnapPdpEngineFactory();
        final Properties properties = new Properties();
        factory.newEngine(properties);
        fail("Expecting an exception.");
    }
}
