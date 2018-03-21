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

package org.onap.policy.utils.test;

import java.io.IOException;
import org.junit.Test;
import org.onap.policy.utils.BackUpMonitorException;

public class BackUpMonitorExceptionTest {
  @Test(expected = BackUpMonitorException.class)
  public void testException1() throws BackUpMonitorException {
    throw new BackUpMonitorException();
  }

  @Test(expected = BackUpMonitorException.class)
  public void testException2() throws BackUpMonitorException {
    throw new BackUpMonitorException("test");
  }

  @Test(expected = BackUpMonitorException.class)
  public void testException3() throws BackUpMonitorException {
    Throwable cause = new IOException();
    throw new BackUpMonitorException(cause);
  }

  @Test(expected = BackUpMonitorException.class)
  public void testException4() throws BackUpMonitorException {
    Throwable cause = new IOException();
    throw new BackUpMonitorException("test", cause);
  }

  @Test(expected = BackUpMonitorException.class)
  public void testException5() throws BackUpMonitorException {
    Throwable cause = new IOException();
    throw new BackUpMonitorException("test", cause, true, true);
  }
}
