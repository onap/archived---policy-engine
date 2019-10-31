/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.dao;

import java.io.IOException;
import org.junit.Test;

public class PolicyDbExceptionTest {
    @Test(expected = PolicyDbException.class)
    public void testException1() throws PolicyDbException {
        throw new PolicyDbException();
    }

    @Test(expected = PolicyDbException.class)
    public void testException2() throws PolicyDbException {
        throw new PolicyDbException("test");
    }

    @Test(expected = PolicyDbException.class)
    public void testException3() throws PolicyDbException {
        Throwable cause = new IOException();
        throw new PolicyDbException(cause);
    }

    @Test(expected = PolicyDbException.class)
    public void testException4() throws PolicyDbException {
        Throwable cause = new IOException();
        throw new PolicyDbException("test", cause);
    }

    @Test(expected = PolicyDbException.class)
    public void testException5() throws PolicyDbException {
        Throwable cause = new IOException();
        throw new PolicyDbException("test", cause, true, true);
    }
}
