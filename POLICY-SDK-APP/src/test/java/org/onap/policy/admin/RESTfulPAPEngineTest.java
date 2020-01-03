/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018-2020 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertNull;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import javax.servlet.http.HttpServletResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;

public class RESTfulPAPEngineTest {

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

    /**
     * BeforeClass does some simple code coverage and sets up the
     * XACML properties.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        //
        // Test constructor with bad URL
        //
        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            new RESTfulPAPEngine(null));

        XACMLProperties.reloadProperties();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        XACMLProperties.reloadProperties();
    }

    private void setupConnection(int responseCode, String location) throws Exception {
        // Mock connection
        HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);

        Mockito.when(connection.getResponseCode()).thenReturn(responseCode);
        Mockito.when(connection.getHeaderField("Location")).thenReturn(location);

        InputStream mockInputStream = Mockito.mock(InputStream.class);
        Mockito.when(connection.getInputStream()).thenReturn(mockInputStream);

        // Set the system property temporarily
        String systemKey = "xacml.properties";
        final String oldProperty = System.getProperty(systemKey);
        System.setProperty(systemKey, "src/test/resources/xacml.admin.properties");

        // Test constructor
        String urlName = "localhost:1234";
        engine = new RESTfulPAPEngine(urlName) {
            @Override
            protected URLConnection makeConnection(String fullUrl) throws IOException {
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
    public void testAllTheExceptions() throws Exception {
        setupConnection(HttpServletResponse.SC_NO_CONTENT, "localhost:5678");

        engine.setDefaultGroup(group);
        assertNull(engine.getDefaultGroup());
        engine.newGroup(name, description);
        engine.removeGroup(group, newGroup);
        assertNull(engine.getPDPGroup(pdp));
        assertNull(engine.getPDPGroup(id));
        assertNull(engine.getPDP(id));
        assertNull(engine.getStatus(pdp));

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->
            engine.getOnapPDPGroups()
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.updateGroup(group)
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.updateGroup(group, "testUserName")
        );

        assertNull(engine.getGroup(name));
        engine.movePDP(pdp, newGroup);

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.newPDP(id, newGroup, name, description, jmxport)
        );

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->
            engine.updatePDP(pdp)
        );

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->
            engine.removePDP(pdp)
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.validatePolicyRequest(new PolicyRestAdapter(), policyType)
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.publishPolicy(id, name, false, policy, newGroup)
        );

        engine.copyFile(id, newGroup, policy);
        PDPPolicy pdpPolicy = Mockito.mock(PDPPolicy.class);
        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.copyPolicy(pdpPolicy, newGroup)
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.removePolicy(null, group)
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            engine.copyPolicy(null, null)
        );

        //
        // Change the mockito to take a different path
        //
        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            setupConnection(HttpServletResponse.SC_FOUND, "localhost:5678")
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            setupConnection(200, "localhost:5678")
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            setupConnection(350, "localhost:5678")
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            setupConnection(500, "localhost:5678")
        );

        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            setupConnection(350, null)
        );
    }

}
