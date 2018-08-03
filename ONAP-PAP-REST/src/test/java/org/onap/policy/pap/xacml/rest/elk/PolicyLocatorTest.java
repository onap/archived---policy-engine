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

package org.onap.policy.pap.xacml.rest.elk;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyLocator;

public class PolicyLocatorTest {
    @Test
    public void testLocator() {
        String policyType = "type";
        String policyName = "name";
        String owner = "owner";
        String scope = "scope";
        String policyId = "id";
        String version = "1.0";
        String testString = "[owner|scope|type|name|id|v1.0|]";

        PolicyLocator locator = new PolicyLocator(policyType, policyName, owner,
            scope, policyId, version);
        String locatorString = locator.toString();
        assertEquals(locatorString, testString);
    }
}
