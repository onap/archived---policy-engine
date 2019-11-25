/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPStatus;
import com.att.research.xacml.api.pap.PDPStatus.Status;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.springframework.mock.web.MockServletConfig;

public class XACMLPapServletTest {
    String systemKey = "xacml.properties";
    String oldProperty = System.getProperty(systemKey);

    @Before
    public void setup() {
        // Set the system property temporarily
        System.setProperty(systemKey, "xacml.pap.properties");
    }

    @Test
    public void testSetAndGet() {
        String testVal = "testVal";
        XACMLPapServlet servlet = new XACMLPapServlet();

        assertNotNull(servlet);
        assertNotNull(XACMLPapServlet.getConfigHome());
        assertNotNull(XACMLPapServlet.getActionHome());
        assertEquals(XACMLPapServlet.getPersistenceUnit(), "XACML-PAP-REST");

        XACMLPapServlet.setPapDbDriver(testVal);
        assertEquals(XACMLPapServlet.getPapDbDriver(), testVal);
        XACMLPapServlet.setPapDbUrl(testVal);
        assertEquals(XACMLPapServlet.getPapDbUrl(), testVal);
        XACMLPapServlet.setPapDbUser(testVal);
        assertEquals(XACMLPapServlet.getPapDbUser(), testVal);
        XACMLPapServlet.setPapDbPassword(testVal);
        assertEquals(XACMLPapServlet.getPapDbPassword(), testVal);
        XACMLPapServlet.setMsOnapName(testVal);
        assertEquals(XACMLPapServlet.getMsOnapName(), testVal);
        XACMLPapServlet.setMsPolicyName(testVal);
        assertEquals(XACMLPapServlet.getMsPolicyName(), testVal);
    }

    @Ignore
    @Test
    public void testMethods() throws ServletException, IOException, PAPException {
        XACMLPapServlet servlet = new XACMLPapServlet();
        ServletConfig config = new MockServletConfig();
        assertThatThrownBy(() -> servlet.init(config)).isInstanceOf(ServletException.class);

        ObjectMapper mapper = new ObjectMapper();
        HttpServletResponse response = new MockHttpServletResponse();
        assertThatCode(() -> XACMLPapServlet.mapperWriteValue(mapper, response, "hello")).doesNotThrowAnyException();
        assertThatCode(() -> XACMLPapServlet.mapperWriteValue(null, null, null)).doesNotThrowAnyException();

        assertNull(XACMLPapServlet.getPapUrl());

        HttpServletRequest request = new MockHttpServletRequest();
        assertThatThrownBy(() -> servlet.doDelete(request, response)).isInstanceOf(NullPointerException.class);

        StringBuffer urlPath = new StringBuffer("http://foo");
        Properties policies = new Properties();
        servlet.populatePolicyURL(urlPath, policies);
        assertNull(policies.getProperty("foo.url"));

        policies.setProperty(XACMLProperties.PROP_ROOTPOLICIES, "foo");
        policies.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "bar");
        servlet.populatePolicyURL(urlPath, policies);
        assertEquals("http://foo?id=foo", policies.getProperty("foo.url"));

        OnapPDP pdp = new StdPDP();
        Status newStatus = PDPStatus.Status.UP_TO_DATE;
        PAPPolicyEngine newEngine = Mockito.mock(PAPPolicyEngine.class);
        Set<OnapPDPGroup> groups = new HashSet<OnapPDPGroup>();
        OnapPDPGroup group = new StdPDPGroup();
        groups.add(group);
        Mockito.when(newEngine.getOnapPDPGroups()).thenReturn(groups);
        XACMLPapServlet.setPAPEngine(newEngine);
        assertThatCode(() -> servlet.setPDPSummaryStatus(pdp, newStatus)).doesNotThrowAnyException();

        OnapLoggingContext loggingContext = new OnapLoggingContext();
        assertThatCode(() -> servlet.changed(loggingContext)).doesNotThrowAnyException();

        assertNull(servlet.getIa());
        assertEquals("test.properties", XACMLPapServlet.getPDPFile());

        servlet.setBaseLoggingContext(loggingContext);
        assertEquals(servlet.getBaseLoggingContext(), loggingContext);
    }

    @After
    public void after() {
        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(systemKey, oldProperty);
        } else {
            System.clearProperty(systemKey);
        }
    }
}
