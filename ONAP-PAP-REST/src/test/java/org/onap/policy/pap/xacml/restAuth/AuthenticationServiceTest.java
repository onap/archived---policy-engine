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

package org.onap.policy.pap.xacml.restAuth;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.junit.Test;
import org.onap.policy.pap.xacml.restAuth.AuthenticationService;

public class AuthenticationServiceTest {
    private final String testCred = "testpap:alpha123";
    private final String testCredEncoded = new String(Base64.getEncoder().encode(testCred.getBytes()));
    private final String basicCred = "Basic " + testCredEncoded;

    @Test
    public void testAuth() throws UnsupportedEncodingException {
        String systemKey = "xacml.properties";

        // Set the system property temporarily
        String oldProperty = System.getProperty(systemKey);
        System.setProperty(systemKey, "xacml.pap.properties");

        AuthenticationService service = new AuthenticationService();
        assertEquals(service.authenticate(basicCred), true);

        // Restore the original system property
        if (oldProperty != null) {
            System.setProperty(systemKey, oldProperty);
        } else {
            System.clearProperty(systemKey);
        }
    }
}
