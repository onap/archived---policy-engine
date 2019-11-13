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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.att.research.xacml.api.pip.PIPException;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

public class PipJpaTest {

    @Test
    public void testPipConfigParam() {
        PipConfigParam data = new PipConfigParam();
        new PipConfigParam("test");
        new PipConfigParam(new PipConfigParam());
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setParamName("Test");
        assertTrue("Test".equals(data.getParamName()));
        data.setParamValue("Test");
        assertTrue("Test".equals(data.getParamValue()));
        data.setParamDefault("Test");
        assertTrue("Test".equals(data.getParamDefault()));
        data.setPipconfiguration(new PipConfiguration());
        assertTrue(data.getPipconfiguration() != null);
        data.setRequiredFlag(true);
        assertTrue(data.isRequired());
        data.setRequiredFlag(false);
        assertFalse(data.isRequired());
        data.toString();
    }

    @Test
    public void testPipResolverParam() {
        PipResolverParam data = new PipResolverParam();
        new PipResolverParam("test");
        new PipResolverParam(new PipResolverParam());
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setParamName("Test");
        assertTrue("Test".equals(data.getParamName()));
        data.setParamValue("Test");
        assertTrue("Test".equals(data.getParamValue()));
        data.setParamDefault("Test");
        assertTrue("Test".equals(data.getParamDefault()));
        data.setPipresolver(new PipResolver());
        assertTrue(data.getPipresolver() != null);
        data.setRequired(true);
        assertTrue(data.isRequired());
        data.setRequired(false);
        assertFalse(data.isRequired());
        data.toString();
    }

    @Test
    public void testPipType() {
        PipType data = new PipType();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
        data.setPipconfigurations(new HashSet<>());
        assertTrue(data.getPipconfigurations() != null);
        data.addPipconfiguration(new PipConfiguration());
        data.removePipconfiguration(new PipConfiguration());
        data.setType("SQL");
        assertTrue(data.isSql());
        data.setType("LDAP");
        assertTrue(data.isLdap());
        data.setType("CSV");
        assertTrue(data.isCsv());
        data.setType("Hyper-CSV");
        assertTrue(data.isHyperCsv());
        data.setType("Custom");
        assertTrue(data.isCustom());
    }

    @Test
    public void testPipResolver() {
        PipResolver data = new PipResolver();
        new PipResolver(new PipResolver());
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setIssuer("Test");
        assertTrue("Test".equals(data.getIssuer()));
        data.setClassname("Test");
        assertTrue("Test".equals(data.getClassname()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setPipconfiguration(new PipConfiguration());
        assertTrue(data.getPipconfiguration() != null);
        data.setPipresolverParams(new HashSet<>());
        assertTrue(data.getPipresolverParams() != null);
        data.addPipresolverParam(new PipResolverParam());
        data.removePipresolverParam(new PipResolverParam());
        assertNull(data.removePipresolverParam(null));
        data.clearParams();
        data.getConfiguration("test");
        assertNotNull(data.getConfiguration("test."));
        data.setReadOnly(true);
        assertTrue(data.isReadOnly());
        data.setReadOnly(false);
        assertFalse(data.isReadOnly());
        data.toString();
        Properties properties = new Properties();
        data.generateProperties(properties, "test");
        try {
            data.readProperties("test", properties);
        } catch (PIPException e) {
            fail();
        }
        data.setIssuer("");
        assertEquals(4, data.getConfiguration("test").size());
        data.generateProperties(properties, "test.");
        assertEquals(4, data.getConfiguration("test").size());

        Set<PipResolverParam> pipresolverParams = new LinkedHashSet<>();
        PipResolverParam prp = new PipResolverParam();
        prp.setParamName("Dorothy");
        prp.setParamValue("Gale");
        pipresolverParams.add(prp);
        data.setPipresolverParams(pipresolverParams);
        data.generateProperties(properties, "test");
        assertEquals(5, data.getConfiguration("test").size());
        assertEquals(5, new PipResolver(data).getConfiguration("test").size());
    }

    @Test
    public void testPipConfiguration() {
        PipConfiguration data = new PipConfiguration();
        data.prePersist();
        data.preUpdate();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setIssuer("Test");
        assertTrue("Test".equals(data.getIssuer()));
        data.setClassname("Test");
        assertTrue("Test".equals(data.getClassname()));
        data.setCreatedBy("Test");
        assertTrue("Test".equals(data.getCreatedBy()));
        data.setModifiedBy("Test");
        assertTrue("Test".equals(data.getModifiedBy()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        try {
            data.readProperties("test", data.generateProperties("test"));
        } catch (PIPException e) {
            fail();
        }
        data.setPiptype(new PipType());
        assertTrue(data.getPiptype() != null);
        data.setPipresolvers(new HashSet<>());
        assertTrue(data.getPipresolvers() != null);
        data.addPipresolver(new PipResolver());
        data.removePipresolver(new PipResolver());
        data.getConfiguration("test");
        data.setReadOnlyFlag(true);
        assertTrue(data.isReadOnly());
        data.setRequiresResolversFlag(true);
        assertTrue(data.requiresResolvers());
        data.toString();
    }

}
