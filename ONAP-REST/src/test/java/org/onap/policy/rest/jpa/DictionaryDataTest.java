/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.rest.jpa;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DictionaryDataTest {

    @Test
    public void test() {
        DictionaryData dictData;
        dictData = new DictionaryData();
        String Value = "testData1";

        // Set Data
        dictData.setId(1);;
        dictData.setDictionaryDataByName(Value);
        dictData.setDictionaryName(Value);
        dictData.setDictionaryUrl(Value);

        // Test gets
        assertEquals(1, dictData.getId());
        assertEquals(Value, dictData.getDictionaryDataByName());
        assertEquals(Value, dictData.getDictionaryName());
        assertEquals(Value, dictData.getDictionaryUrl());
    }

}
