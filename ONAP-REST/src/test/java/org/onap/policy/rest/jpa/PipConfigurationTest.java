/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.jpa;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.std.pip.engines.StdConfigurableEngine;
import com.att.research.xacml.util.XACMLProperties;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class PipConfigurationTest {
    @Test
    public void testConfig() throws PIPException {
        String value = "testVal";
        PipConfigParam param = new PipConfigParam();
        param.setParamName(value);
        param.setParamValue(value);
        Set<PipConfigParam> params = new HashSet<PipConfigParam>();
        PipResolver resolver = new PipResolver();
        resolver.setClassname(value);
        resolver.setName(value);
        params.add(param);
        Set<PipResolver> resolvers = new HashSet<PipResolver>();
        resolvers.add(resolver);
        Properties props = new Properties();

        String id = "1";
        props.setProperty(id + ".classname", value);
        props.setProperty(XACMLProperties.PROP_PIP_ENGINES, id);

        // Test constructors
        PipConfiguration config = new PipConfiguration();
        assertNotNull(config);
        config.setPipconfigParams(params);
        config.setPipresolvers(resolvers);
        PipConfiguration config2 = new PipConfiguration(config, value);
        assertNotNull(config2);
        config2.prePersist();
        config2.preUpdate();
        PipConfiguration config3 = new PipConfiguration(id, props);
        assertNotNull(config3);
        PipConfiguration config4 = new PipConfiguration(id, props, value);
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
        config.setReadOnlyFlag(true);
        assertEquals('1', config.getReadOnly());
        config.setReadOnly('0');
        assertEquals('0', config.getReadOnly());
        assertEquals(false, config.isReadOnly());
        config.setRequiresResolvers('t');
        assertEquals('t', config.getRequiresResolvers());
        config.setReadOnlyFlag(false);
        assertEquals('0', config.getReadOnly());

        PipType type = new PipType();
        config.setPiptype(type);
        assertEquals(type, config.getPiptype());
        config.setRequiresResolversFlag(false);
        assertEquals('0', config.getRequiresResolvers());
        config.setCreatedBy(value);
        assertEquals(value, config.getCreatedBy());

        Date date = new Date();
        config.setCreatedDate(date);
        assertEquals(date, config.getCreatedDate());
        config.setModifiedBy(value);
        assertEquals(value, config.getModifiedBy());
        config.setModifiedDate(date);
        assertEquals(date, config.getModifiedDate());
        config.setRequiresResolversFlag(true);
        assertTrue(config.requiresResolvers());
        config.setRequiresResolversFlag(false);
        assertFalse(config.requiresResolvers());
        assertEquals(8, config.getConfiguration(id).size());
        assertEquals(9, config.generateProperties(id).size());

        // Test remove and clear
        assertEquals(param, config.removePipconfigParam(param));
        assertNull(config.removePipconfigParam(null));
        config.clearConfigParams();
        assertEquals(0, config.getPipconfigParams().size());
        config.removePipresolver(resolver);
        assertEquals(0, config.getPipresolvers().size());

        assertEquals(param, config.addPipconfigParam(param));
        config.clearConfigParams();
        assertEquals(0, config.getPipconfigParams().size());
        config.removePipresolver(resolver);
        assertEquals(0, config.getPipresolvers().size());

        // Test import
        assertEquals(1, PipConfiguration.importPipConfigurations(props).size());
        config.readProperties(id, props);
        assertEquals(id, config.getName());

        // Test toString
        String configString = config.toString().replaceAll("@[0-9a-f]*", "");
        assertEquals(322, configString.length());

        assertEquals(0, PipConfiguration.importPipConfigurations(new Properties()).size());

        Properties otherProperties = new Properties();
        otherProperties.put(XACMLProperties.PROP_PIP_ENGINES, "");
        assertEquals(0, PipConfiguration.importPipConfigurations(otherProperties).size());

        otherProperties.put(XACMLProperties.PROP_PIP_ENGINES, "badvalue:evenworse");
        assertEquals(0, PipConfiguration.importPipConfigurations(otherProperties).size());

        // Test readProperties
        props.setProperty(id + "." + StdConfigurableEngine.PROP_ISSUER, "himself");
        props.setProperty(id + ".resolvers", "");
        props.setProperty(id + ".resolver", "");
        props.setProperty(id + ".anotherproperty", "");
        config.readProperties(id, props);
        assertEquals("himself", config.getIssuer());
        assertEquals(49, config.getRequiresResolvers());

        props.setProperty(id + ".resolvers", "aaa");
        assertThatThrownBy(() -> config.readProperties(id, props)).hasMessage("PIP Engine defined without a classname");

        props.setProperty(id + ".resolver.aaa.classname", "somewhere.over.the.Rainbow");
        props.setProperty(id + ".resolver.aaa." + StdConfigurableEngine.PROP_NAME, "Dorothy");
        props.setProperty(id + ".resolver.aaa." + StdConfigurableEngine.PROP_DESCRIPTION, "Oz");
        props.setProperty(id + ".resolver.aaa." + StdConfigurableEngine.PROP_ISSUER, "Wizard");
        props.setProperty(id + ".resolver.aaa.witch", "North");
        config.readProperties(id, props);

        PipResolver pipResolver = config.getPipresolvers().iterator().next();

        assertEquals("somewhere.over.the.Rainbow", pipResolver.getClassname());
        assertEquals("Dorothy", pipResolver.getName());
        assertEquals("Oz", pipResolver.getDescription());
        assertEquals("Wizard", pipResolver.getIssuer());

        // Test getConfiguration
        assertEquals(10, config.getConfiguration(null).size());
        assertEquals(10, config.getConfiguration("kansas").size());
        assertEquals(10, config.getConfiguration("kansas.").size());
        config.setDescription(null);
        assertEquals(9, config.getConfiguration(null).size());
        config.setIssuer(null);
        assertEquals(8, config.getConfiguration(null).size());
        config.setPipresolvers(new LinkedHashSet<PipResolver>());
        assertEquals(3, config.getConfiguration(null).size());

        // Test generateProperties
        assertEquals(4, config.generateProperties(null).size());
        assertEquals(4, config.generateProperties("kansas").size());
        assertEquals(4, config.generateProperties("kansas.").size());
        config.setIssuer("");
        assertEquals(4, config.generateProperties("kansas.").size());
    }
}
