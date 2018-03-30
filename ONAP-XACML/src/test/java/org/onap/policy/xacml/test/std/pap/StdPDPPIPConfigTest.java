/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;
import org.onap.policy.xacml.std.pap.StdPDPPIPConfig;

public class StdPDPPIPConfigTest {
  @Test
  public void testConfig() {
    // Setup test data
    String id = "testID";
    String value = "testVal";
    Properties props = new Properties();
    props.setProperty(id + ".classname", value);
    Map<String, String> map = new HashMap<String, String>();
    map.put(id, value);

    // Test constructors
    StdPDPPIPConfig config = new StdPDPPIPConfig();
    assertNotNull(config);
    StdPDPPIPConfig config2 = new StdPDPPIPConfig(id);
    assertNotNull(config2);
    StdPDPPIPConfig config3 = new StdPDPPIPConfig(id, value, value);
    assertNotNull(config3);
    StdPDPPIPConfig config4 = new StdPDPPIPConfig(id, props);
    assertNotNull(config4);
    StdPDPPIPConfig config5 = new StdPDPPIPConfig(id, props);
    assertNotNull(config5);

    // Test set and get
    config.setId(value);
    assertEquals(value, config.getId());
    config.setName(value);
    assertEquals(value, config.getName());
    config.setDescription(value);
    assertEquals(value, config.getDescription());
    config.setClassname(value);
    assertEquals(value, config.getClassname());
    config.setValues(map);
    assertEquals(map, config.getConfiguration());
    assertEquals(true, config.isConfigured());
    config.setConfig(map);
    assertEquals(map, config.getConfig());

    // Test equals combinations
    assertEquals(true, config4.equals(config5));
    assertEquals(true, config4.equals(config4));
    assertEquals(false, config4.equals(null));
    assertEquals(false, config4.equals(value));
    config4.setClassname(null);
    config5.setClassname(value);
    assertEquals(false, config4.equals(config5));
    config4.setClassname(id);
    assertEquals(false, config4.equals(config5));
    config4.setClassname(value);
    config5.setConfig(map);
    assertEquals(false, config4.equals(config5));
    config4.setConfig(null);
    assertEquals(false, config4.equals(config5));
    config4.setConfig(map);
    config5.setDescription(value);
    assertEquals(false, config4.equals(config5));
    config4.setDescription(id);
    assertEquals(false, config4.equals(config5));
    config4.setDescription(value);
    config4.setId(null);
    assertEquals(false, config4.equals(config5));
    config4.setId(value);
    assertEquals(false, config4.equals(config5));
    config4.setId(id);
    config5.setName(value);
    assertEquals(false, config4.equals(config5));
    config4.setName(id);
    assertEquals(false, config4.equals(config5));
    assertThat(config.hashCode(), is(not(0)));

    // Test toString
    assertThat(config.toString().length(), is(not(0)));
  }
}
