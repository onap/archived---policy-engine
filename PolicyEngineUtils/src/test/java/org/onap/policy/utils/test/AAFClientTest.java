/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2017, 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.utils.AAFPolicyClient;
import org.onap.policy.utils.AAFPolicyClientImpl;
import org.onap.policy.utils.AAFPolicyException;

public class AAFClientTest {
    AAFPolicyClient afClient;
    String pass = "test";
    String user = "test";

    /**
     * setUp.
     *
     * @throws AAFPolicyException AAFPolicyException
     */
    @Before
    public void setUp() throws AAFPolicyException {
        Properties props = new Properties();
        props.setProperty("ENVIRONMENT", "TEST");
        props.setProperty("aafClient.impl.className", AAFPolicyClientImpl.class.getName());
        afClient = AAFPolicyClient.getInstance(props);
    }

    @Test
    public void invalidClientTest() throws AAFPolicyException {
        assertFalse(afClient.checkPerm(null, null, null, null, null));
        assertFalse(afClient.checkPerm(user, null, null, null, null));
        assertFalse(afClient.checkAuth(user, pass));
        assertFalse(afClient.checkAuth(null, pass));
        assertFalse(afClient.checkAuth(user, null));
        assertFalse(afClient.checkPerm(user, pass, "policy-engine.config", "*", "*"));
        Properties props = new Properties();
        props.setProperty("aafClient.impl.className", AAFPolicyClientImpl.class.getName());
        afClient = AAFPolicyClient.getInstance(props);
        assertFalse(afClient.checkAuth(user, pass));
        props.setProperty("ENVIRONMENT", "PROD");
        props.setProperty("aafClient.impl.className", AAFPolicyClientImpl.class.getName());
        afClient.updateProperties(props);
        assertFalse(afClient.checkAuth("test", pass));
        props.setProperty("aaf_url", "test");
        afClient.updateProperties(props);
        assertFalse(afClient.checkAuth("test", pass));
        props = new Properties();
        props.setProperty("ENVIRONMENT", "123");
        afClient.updateProperties(props);
        assertFalse(afClient.checkAuth("test", pass));
        assertFalse(afClient.checkAuthPerm("test", pass, "decision", "*", "read"));
    }

    @Test(expected = AAFPolicyException.class)
    public void invalidAafInstance() throws AAFPolicyException {
        Properties props = new Properties();
        props.setProperty("aafClient.impl.className", "errorClass");
        afClient = AAFPolicyClient.getInstance(props);
    }

    @Test(expected = AAFPolicyException.class)
    public void testPropNullException() throws AAFPolicyException {
        afClient.updateProperties(null);
    }

    @Test(expected = AAFPolicyException.class)
    public void testPropEmptyException() throws AAFPolicyException {
        afClient.updateProperties(new Properties());
    }

    @Test(expected = AAFPolicyException.class)
    public void testAafException() throws AAFPolicyException {
        new AAFPolicyException();
        new AAFPolicyException("error", new Exception());
        throw new AAFPolicyException("error", new Exception(), false, false);
    }
}
