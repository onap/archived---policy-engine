/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.api.PolicyException;

public class BrmsGatewayMainTest {

    @Test
    public void testTooManyArguments() {
        try {
            String[] args = {"aaa", "bbb"};
            BrmsGateway.main(args);
            fail("test should throw an exception");
        }
        catch (PolicyException e) {
            assertEquals("usage: org.onap.policy.brms.api.BrmsGateway [configFile]", e.getMessage());
        }
        catch (Exception e) {
            fail("test should throw a PolicyException");
        }

        try {
            String[] args = {"aaa"};
            BrmsGateway.main(args);
            fail("test should throw an exception");
        }
        catch (PolicyException e) {
            assertEquals("Check your property file: PE300 - Data Issue: "
                            + "Config File doesn't Exist in the specified Path aaa", e.getMessage());
        }
        catch (Exception e) {
            fail("test should throw a PolicyException");
        }
    }

}
