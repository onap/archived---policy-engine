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
import org.onap.policy.pap.xacml.rest.elk.client.Pair;

public class PairTest {
    @Test
    public void testPair() {
        String testLeft = "left";
        String testRight = "right";

        // Test constructor
        Pair<String, String> pair = new Pair<String, String>(testLeft, testRight);
        assertEquals(pair.left(), testLeft);
        assertEquals(pair.right(), testRight);

        // Test setters
        pair.left(testRight);
        pair.right(testLeft);
        assertEquals(pair.left(), testRight);
        assertEquals(pair.right(), testLeft);
    }
}
