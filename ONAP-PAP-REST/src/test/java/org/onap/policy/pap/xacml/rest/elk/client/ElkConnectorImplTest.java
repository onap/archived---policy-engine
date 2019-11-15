/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.elk.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.searchbox.client.JestResult;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.onap.policy.rest.adapter.PolicyRestAdapter;

public class ElkConnectorImplTest {

    @Test
    public void isAlphaNumericTest() {
        try {
            Method method = ElkConnectorImpl.class.getDeclaredMethod("isAlphaNumeric", String.class);
            method.setAccessible(true);
            assertTrue((boolean) method.invoke(new ElkConnectorImpl(), "abc123"));
            assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123*"));
            assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123{}"));
            assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123\n"));
            assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123<"));
            assertFalse((boolean) method.invoke(new ElkConnectorImpl(), "abc123:"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void searchTest() {
        JestResult r1 = null, r2 = null, r3 = null, r4 = null;

        // Should always work if the above test passes and ELK server is up
        try {
            r1 = new ElkConnectorImpl().search(PolicyIndexType.decision, "abc123");
        } catch (Exception e) {
            // ELK server is down. Don't continue the test
            if (e instanceof IllegalStateException) {
                return;
            }
            fail();
        }

        // Should always work
        try {
            r2 = new ElkConnectorImpl().search(PolicyIndexType.decision, "The_quick_brown_fox_jumps_over_the_lazy_dog");
        } catch (Exception e) {
            fail();
        }

        // Should throw exception
        try {
            r3 = new ElkConnectorImpl().search(PolicyIndexType.decision, "abc123{}");
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                fail();
            }
        }

        // Should throw exception
        try {
            r4 = new ElkConnectorImpl().search(PolicyIndexType.decision, "The quick brown fox jumps over the lazy dog");
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                fail();
            }
        }

        assertNotNull(r1);
        assertNotNull(r2);
        assertNull(r3);
        assertNull(r4);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDelete() {
        thrown.expect(NullPointerException.class);

        ElkConnectorImpl impl = new ElkConnectorImpl();
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        impl.delete(adapter);
        fail("Expected exception to be thrown");
    }

    @Test
    public void testPut() throws IOException {
        thrown.expect(NullPointerException.class);

        ElkConnectorImpl impl = new ElkConnectorImpl();
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        impl.put(adapter);
        fail("Expected exception to be thrown");
    }

    @Test
    public void testUpdate() {
        thrown.expect(IllegalStateException.class);

        ElkConnectorImpl impl = new ElkConnectorImpl();
        PolicyRestAdapter adapter = new PolicyRestAdapter();
        impl.update(adapter);
        fail("Expected exception to be thrown");
    }

    @Test
    public void testSearchWithFilter() {
        thrown.expect(IllegalStateException.class);

        ElkConnectorImpl impl = new ElkConnectorImpl();
        impl.search(PolicyIndexType.config, "search", null);
        fail("Expected exception to be thrown");
    }

    @Test
    public void testImplNegCases() throws IOException {
        ElkConnectorImpl impl = new ElkConnectorImpl();
        Map<String, String> filter = new HashMap<String, String>();
        assertThatThrownBy(() -> impl.isType(PolicyIndexType.config)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> impl.isIndex()).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> impl.search(null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> impl.search(null, "")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> impl.search(null, ";;;")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> impl.search(null, "foo")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> impl.search(PolicyIndexType.all, "foo")).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> impl.search(null, null, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> impl.search(null, null, filter)).isInstanceOf(IllegalArgumentException.class);
        filter.put("key", "value");
        assertThatThrownBy(() -> impl.search(null, ";;;", filter)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> impl.search(null, "foo", filter)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> impl.search(PolicyIndexType.config, "foo", filter))
            .isInstanceOf(IllegalStateException.class);

        PolicyRestAdapter adapter = new PolicyRestAdapter();
        adapter.setNewFileName("scope.Decision_newFile");
        adapter.setConfigPolicyType("Config");
        assertThatThrownBy(() -> impl.put(adapter)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> impl.delete(adapter)).isInstanceOf(IllegalStateException.class);
    }
}
