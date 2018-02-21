/*-
 * ============LICENSE_START=======================================================
 * APIDictionaryResponse
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

package org.onap.policy.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.onap.policy.models.APIDictionaryResponse;

public class APIDictionaryResponseTest {

    private static final String JSON_STRING = "JSON_STRING";

    private static final String DICTIONARY_DATA = "DICTIONARY_DATA";

    @Test
    public final void testSetAndGet() {
        final APIDictionaryResponse objUnderTest = new APIDictionaryResponse();
        objUnderTest.setDictionaryData(DICTIONARY_DATA);
        objUnderTest.setDictionaryJson(JSON_STRING);
        objUnderTest.setResponseCode(Status.BAD_GATEWAY.getStatusCode());
        objUnderTest.setResponseMessage(Status.BAD_GATEWAY.toString());

        assertEquals(DICTIONARY_DATA, objUnderTest.getDictionaryData());
        assertEquals(JSON_STRING, objUnderTest.getDictionaryJson());
        assertEquals(Status.BAD_GATEWAY.getStatusCode(), objUnderTest.getResponseCode());
        assertEquals(Status.BAD_GATEWAY.toString(), objUnderTest.getResponseMessage());

    }
}
