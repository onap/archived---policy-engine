/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.rest.dao;

import java.io.IOException;
import org.junit.Test;

public class PolicyDBExceptionTest {
  @Test(expected = PolicyDBException.class)
  public void testException1() throws PolicyDBException {
    throw new PolicyDBException();
  }

  @Test(expected = PolicyDBException.class)
  public void testException2() throws PolicyDBException {
    throw new PolicyDBException("test");
  }

  @Test(expected = PolicyDBException.class)
  public void testException3() throws PolicyDBException {
    Throwable cause = new IOException();
    throw new PolicyDBException(cause);
  }

  @Test(expected = PolicyDBException.class)
  public void testException4() throws PolicyDBException {
    Throwable cause = new IOException();
    throw new PolicyDBException("test", cause);
  }

  @Test(expected = PolicyDBException.class)
  public void testException5() throws PolicyDBException {
    Throwable cause = new IOException();
    throw new PolicyDBException("test", cause, true, true);
  }
}
