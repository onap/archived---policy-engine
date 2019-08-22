/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;
import javax.servlet.http.HttpServletResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;

public class RESTfulPAPEngineTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RESTfulPAPEngine engine = null;
    private String name = "testName";
    private String id = "testID";
    private String description = "testDescription";
    private String policyType = "testType";
    private String policyContent = "testContent";
    private int jmxport = 0;
    OnapPDPGroup group = Mockito.mock(OnapPDPGroup.class);
    OnapPDPGroup newGroup = Mockito.mock(OnapPDPGroup.class);
    OnapPDP pdp = Mockito.mock(OnapPDP.class);
    InputStream policy;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        XACMLProperties.reloadProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        XACMLProperties.reloadProperties();
    }

    @Before
    public void runConstructor() throws Exception {
        // Mock connection
        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
        Mockito.when(connection.getResponseCode()).thenReturn(HttpServletResponse.SC_NO_CONTENT);

        // Set the system property temporarily
        String systemKey = "xacml.properties";
        String oldProperty = System.getProperty(systemKey);
        System.setProperty(systemKey, "src/test/resources/xacml.admin.properties");

        // Test constructor
        String urlName = "localhost:1234";
        engine = new RESTfulPAPEngine(urlName) {
            @Override
            protected URLConnection makeConnection(String fullURL) throws MalformedURLException, IOException {
                return connection;
            }
        };

        // Initialize policy
        policy = new ByteArrayInputStream(policyContent.getBytes("UTF-8"));

        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(systemKey, oldProperty);
        } else {
            System.clearProperty(systemKey);
        }
    }

    @Test
    public void testGroups() throws Exception {
        engine.setDefaultGroup(group);
        assertEquals(engine.getDefaultGroup(), null);
        engine.newGroup(name, description);
        engine.removeGroup(group, newGroup);
        assertEquals(engine.getPDPGroup(pdp), null);
        assertEquals(engine.getPDPGroup(id), null);
        assertEquals(engine.getPDP(id), null);
        assertEquals(engine.getStatus(pdp), null);

        thrown.expect(NullPointerException.class);
        engine.getOnapPDPGroups();
        fail("Expecting an exception.");
    }

    @Test
    public void testUpdateGroup() throws PAPException {
        thrown.expect(PAPException.class);
        engine.updateGroup(group);
        fail("Expecting an exception.");
    }

    @Test
    public void testPDP() throws PAPException {
        assertEquals(engine.getGroup(name), null);
        engine.movePDP(pdp, newGroup);

        thrown.expect(PAPException.class);
        engine.newPDP(id, newGroup, name, description, jmxport);
        fail("Expecting an exception.");
    }

    @Test
    public void testUpdatePDP() throws PAPException {
        thrown.expect(NullPointerException.class);
        engine.updatePDP(pdp);
        fail("Expecting an exception.");
    }

    @Test
    public void testRemovePDP() throws PAPException {
        thrown.expect(NullPointerException.class);
        engine.removePDP(pdp);
        fail("Expecting an exception.");
    }

    @Test
    public void testValidatePolicy() throws PAPException {
        PolicyRestAdapter policyAdapter = new PolicyRestAdapter();

        thrown.expect(PAPException.class);
        engine.validatePolicyRequest(policyAdapter, policyType);
        fail("Expecting an exception.");
    }

    @Test
    public void testPublishPolicy() throws PAPException {
        thrown.expect(PAPException.class);
        engine.publishPolicy(id, name, false, policy, newGroup);
        fail("Expecting an exception.");
    }

    @Test
    public void testCopy() throws PAPException {
        engine.copyFile(id, newGroup, policy);
        PDPPolicy pdpPolicy = Mockito.mock(PDPPolicy.class);

        thrown.expect(PAPException.class);
        engine.copyPolicy(pdpPolicy, newGroup);
        fail("Expecting an exception.");
    }
}
