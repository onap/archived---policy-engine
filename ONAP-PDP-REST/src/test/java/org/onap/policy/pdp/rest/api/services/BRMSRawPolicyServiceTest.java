/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.services;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.PolicyParameters;

public class BRMSRawPolicyServiceTest {

    private static final String SYSTEM_KEY = "xacml.properties";
    private static String oldProperty;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/test/resources/pass.xacml.pdp.properties"));
        String succeeded = prop.getProperty("xacml.rest.pap.url");
        List<String> paps = Arrays.asList(succeeded.split(","));
        PAPServices.setPaps(paps);
        PAPServices.setJunit(true);
        oldProperty = System.getProperty(SYSTEM_KEY);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        PAPServices.setPaps(null);
        PAPServices.setJunit(false);
        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(SYSTEM_KEY, oldProperty);
        } else {
            System.clearProperty(SYSTEM_KEY);
        }
    }

    @Test
    public void testRaw() throws FileNotFoundException, IOException {

        String testVal = "testVal";
        PolicyParameters testParams = new PolicyParameters();

        // Set the system property temporarily
        System.setProperty(SYSTEM_KEY, "xacml.pdp.properties");

        BRMSRawPolicyService service =
                new BRMSRawPolicyService(testVal, testVal, testParams, testVal);
        assertEquals(false, service.getValidation());
        assertEquals("PE300 - Data Issue:  No Rule Body given", service.getMessage());
    }
}
