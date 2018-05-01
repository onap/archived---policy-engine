/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.junit.Test;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.util.XACMLProperties;

public class PIPConfigurationTest {
  @Test
  public void testConfig() throws PIPException {
    String value = "testVal";
    String id = "1";
    PIPConfigParam param = new PIPConfigParam();
    param.setParamName(value);
    param.setParamValue(value);
    Set<PIPConfigParam> params = new HashSet<PIPConfigParam>();
    PIPResolver resolver = new PIPResolver();
    resolver.setClassname(value);
    resolver.setName(value);
    params.add(param);
    Set<PIPResolver> resolvers = new HashSet<PIPResolver>();
    resolvers.add(resolver);
    Properties props = new Properties();
    props.setProperty(id + ".classname", value);
    props.setProperty(XACMLProperties.PROP_PIP_ENGINES, id);
    PIPType type = new PIPType();
    Date date = new Date();

    // Test constructors
    PIPConfiguration config = new PIPConfiguration();
    assertNotNull(config);
    config.setPipconfigParams(params);
    config.setPipresolvers(resolvers);
    PIPConfiguration config2 = new PIPConfiguration(config, value);
    assertNotNull(config2);
    config2.prePersist();
    config2.preUpdate();
    PIPConfiguration config3 = new PIPConfiguration(id, props);
    assertNotNull(config3);
    PIPConfiguration config4 = new PIPConfiguration(id, props, value);
    assertNotNull(config4);

    // Test set and get
    config.setId(1);
    assertEquals(1, config.getId());
    config.setDescription(value);
    assertEquals(value, config.getDescription());
    config.setName(id);
    assertEquals(id, config.getName());
    config.setClassname(value);
    assertEquals(value, config.getClassname());
    config.setIssuer(value);
    assertEquals(value, config.getIssuer());
    config.setReadOnly(true);
    assertEquals('1', config.getReadOnly());
    config.setReadOnly('0');
    assertEquals('0', config.getReadOnly());
    assertEquals(false, config.isReadOnly());
    config.setRequiresResolvers('t');
    assertEquals('t', config.getRequiresResolvers());
    config.setReadOnly(false);
    assertEquals('0', config.getReadOnly());
    config.setPiptype(type);
    assertEquals(type, config.getPiptype());
    config.setRequiresResolvers(false);
    assertEquals('0', config.getRequiresResolvers());
    config.setCreatedBy(value);
    assertEquals(value, config.getCreatedBy());
    config.setCreatedDate(date);
    assertEquals(date, config.getCreatedDate());
    config.setModifiedBy(value);
    assertEquals(value, config.getModifiedBy());
    config.setModifiedDate(date);
    assertEquals(date, config.getModifiedDate());
    config.setRequiresResolvers(true);
    assertEquals(true, config.requiresResolvers());
    assertEquals(8, config.getConfiguration(id).size());
    assertEquals(9, config.generateProperties(id).size());

    // Test remove and clear
    assertEquals(param, config.removePipconfigParam(param));
    config.clearConfigParams();
    assertEquals(0, config.getPipconfigParams().size());
    config.removePipresolver(resolver);
    assertEquals(0, config.getPipresolvers().size());

    // Test import
    assertEquals(1, PIPConfiguration.importPIPConfigurations(props).size());
    config.readProperties(id, props);
    assertEquals(id, config.getName());

    // Test toString
    String configString = config.toString().replaceAll("@[0-9a-f]*", "");
    assertEquals(323, configString.length());
  }
}
