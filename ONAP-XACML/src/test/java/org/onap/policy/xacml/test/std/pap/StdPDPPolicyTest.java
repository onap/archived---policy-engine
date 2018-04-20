/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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

package org.onap.policy.xacml.test.std.pap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import org.junit.Test;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import com.att.research.xacml.api.pap.PAPException;


public class StdPDPPolicyTest {
  @Test
  public void testPolicy() throws URISyntaxException, IOException, PAPException {
    // Set up test data
    String value = "testVal";
    URI uri = new URI("http://localhost/");
    int[] array = {1, 1};

    // Test constructors
    StdPDPPolicy policy = new StdPDPPolicy(value, true);
    assertNotNull(policy);
    StdPDPPolicy policy2 = new StdPDPPolicy(value, true, value);
    assertNotNull(policy2);
    StdPDPPolicy policy4 = new StdPDPPolicy();
    assertNotNull(policy4);
    StdPDPPolicy policy5 = new StdPDPPolicy(value, true, value, uri, false, value, value, "1");
    assertNotNull(policy5);
    StdPDPPolicy policy6 = new StdPDPPolicy(value, true, value, uri, false);
    assertNotNull(policy6);
    StdPDPPolicy policy8 = new StdPDPPolicy(value, true, value, uri, false);
    assertNotNull(policy8);

    // Test set and get
    policy.setId(value);
    assertEquals(value, policy.getId());
    policy.setName(value);
    assertEquals(value, policy.getName());
    policy.setPolicyId(value);
    assertEquals(value, policy.getPolicyId());
    policy.setDescription(value);
    assertEquals(value, policy.getDescription());
    policy.setVersion("1");
    assertEquals("1", policy.getVersion());
    assertEquals(1, policy.getVersionInts()[0]);
    assertEquals(true, policy.isRoot());
    policy.setValid(true);
    assertEquals(true, policy.isValid());
    assertEquals(uri, policy5.getLocation());
    policy.setRoot(false);
    assertEquals(false, policy.isRoot());
    policy.setLocation(uri);
    assertEquals(uri, policy.getLocation());

    // Test equals combinations
    assertThat(policy.hashCode(), is(not(0)));
    assertEquals(true, policy.equals(policy));
    assertEquals(false, policy.equals(null));
    assertEquals(false, policy.equals(value));
    assertEquals(true, policy6.equals(policy8));
    policy6.setId("1");
    assertEquals(false, policy6.equals(policy8));
    policy6.setId(null);
    assertEquals(false, policy6.equals(policy8));
    policy8.setId(null);
    assertEquals(true, policy6.equals(policy8));
    policy8.setPolicyId(value);
    assertEquals(false, policy6.equals(policy8));
    policy6.setPolicyId("1");
    policy8.setPolicyId("2");
    assertEquals(false, policy6.equals(policy8));
    policy8.setPolicyId("1");
    policy6.setVersion("1");
    policy8.setVersion("2");
    assertEquals(false, policy6.equals(policy8));

    // Test toString
    assertThat(policy.toString().length(), is(not(0)));
    assertEquals("1.1", StdPDPPolicy.versionArrayToString(array));
    assertEquals("", StdPDPPolicy.versionArrayToString(null));
    assertEquals(0, StdPDPPolicy.versionStringToArray(null).length);
  }

  @Test(expected = ConnectException.class)
  public void negTestStream() throws URISyntaxException, IOException, PAPException {
    // Set up test data
    String value = "testVal";
    URI uri = new URI("http://localhost:54287");
    StdPDPPolicy policy = new StdPDPPolicy(value, true, value, uri, false, value, value, "1");

    // Negative test stream
    policy.getStream();
  }

  @Test(expected = ConnectException.class)
  public void negTestConstructor1() throws URISyntaxException, IOException {
    // Set up test data
    String value = "testVal";
    URI uri = new URI("http://localhost:54287");

    // Test constructor
    StdPDPPolicy policy = new StdPDPPolicy(value, true, value, uri);
    assertNotNull(policy);
  }

  @Test(expected = ConnectException.class)
  public void negTestConstructor2() throws URISyntaxException, IOException {
    // Set up test data
    String value = "testVal";
    URI uri = new URI("http://localhost:54287");
    Properties props = new Properties();

    // Test constructor
    StdPDPPolicy policy = new StdPDPPolicy(value, true, uri, props);
    assertNotNull(policy);
  }
}
