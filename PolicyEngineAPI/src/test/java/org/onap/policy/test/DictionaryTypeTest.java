/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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
package org.onap.policy.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.api.DictionaryType;

public class DictionaryTypeTest {

    @Test
    public void testCreate_EnumName_DictionaryTypeEnum() {
        for (final DictionaryType dictionaryType : DictionaryType.values()) {
            final DictionaryType actualDictionaryType = DictionaryType.create(dictionaryType.name());
            assertEquals(dictionaryType, actualDictionaryType);
            assertEquals(dictionaryType.toString(), actualDictionaryType.toString());
        }
    }

    @Test
    public void testCreate_StringValue_DictionaryTypeEnum() {
        for (final DictionaryType dictionaryType : DictionaryType.values()) {
            final DictionaryType actualDictionaryType = DictionaryType.create(dictionaryType.toString());
            assertEquals(dictionaryType, actualDictionaryType);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test() {
        assertEquals(DictionaryType.Action, DictionaryType.create(DictionaryType.Action.toString()));
        DictionaryType.create("foobar");
    }

}
