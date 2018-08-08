/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pdp.rest.api.services;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.PolicyException;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;

public class PAPServicesTest {

    PAPServices papServices = null;

    @Before
    public void setUp() throws Exception {
        PAPServices.setJunit(true);
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
        String succeeded = prop.getProperty("xacml.rest.pap.url");
        List<String> paps = Arrays.asList(succeeded.split(","));
        PAPServices.setPaps(paps);
        papServices = new PAPServices();

    }

    @After
    public void tearDown() throws Exception {
        PAPServices.setPaps(null);
        PAPServices.setJunit(false);
    }

    @Test
    public final void testCallPAP() throws PolicyException {
        StdPAPPolicy newPAPPolicy = new StdPAPPolicy();
        String response = null;
        response = (String) papServices.callPAP(newPAPPolicy, new String[] {"operation=create", "apiflag=api", "policyType=Config"}, UUID.randomUUID(), "Config");
        assertEquals("success",response);
    }

    @Test
    public final void testGetActiveVersion() {
            String activeVersion = papServices.getActiveVersion("test", "Config_", "test.testpolicy", "Config", UUID.randomUUID());
            assertNull(activeVersion);
    }

    @Test
    public final void testPushPolicy() throws PolicyException {
        StdPDPPolicy selectedPolicy = papServices.pushPolicy("test", "Config_", "test.testpolicy", "Config", "default", UUID.randomUUID());
        assertNull(selectedPolicy);
    }

}
