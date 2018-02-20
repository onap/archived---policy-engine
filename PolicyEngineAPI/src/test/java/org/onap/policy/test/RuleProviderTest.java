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
import org.onap.policy.api.RuleProvider;

public class RuleProviderTest {

    @Test
    public void test() {
        assertEquals(RuleProvider.RAINY_DAY, RuleProvider.create(RuleProvider.RAINY_DAY.name()));
    }

    @Test
    public void testCreate_EnumName_RuleProviderEnum() {
        for (final RuleProvider ruleProvider : RuleProvider.values()) {
            final RuleProvider actualRuleProvider = RuleProvider.create(ruleProvider.name());
            assertEquals(ruleProvider, actualRuleProvider);
            assertEquals(ruleProvider.toString(), actualRuleProvider.toString());
        }
    }

    @Test
    public void testCreate_StringValue_RuleProviderEnum() {
        for (final RuleProvider ruleProvider : RuleProvider.values()) {
            final RuleProvider actualRuleProvider = RuleProvider.create(ruleProvider.toString());
            assertEquals(ruleProvider, actualRuleProvider);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        RuleProvider.create("foobar");
    }

}
