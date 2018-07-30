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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Properties;

import org.junit.Test;

import com.att.research.xacml.api.pip.PIPException;

public class PIPJPATest {

    @Test
    public void testPIPConfigParam(){
        PIPConfigParam data = new PIPConfigParam();
        new PIPConfigParam("test");
        new PIPConfigParam(new PIPConfigParam());
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setParamName("Test");
        assertTrue("Test".equals(data.getParamName()));
        data.setParamValue("Test");
        assertTrue("Test".equals(data.getParamValue()));
        data.setParamDefault("Test");
        assertTrue("Test".equals(data.getParamDefault()));
        data.setPipconfiguration(new PIPConfiguration());
        assertTrue(data.getPipconfiguration()!=null);
        data.setRequired(true);
        assertTrue(data.isRequired());
        data.toString();
    }

    @Test
    public void testPIPResolverParam(){
        PIPResolverParam data = new PIPResolverParam();
        new PIPResolverParam("test");
        new PIPResolverParam(new PIPResolverParam());
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setParamName("Test");
        assertTrue("Test".equals(data.getParamName()));
        data.setParamValue("Test");
        assertTrue("Test".equals(data.getParamValue()));
        data.setParamDefault("Test");
        assertTrue("Test".equals(data.getParamDefault()));
        data.setPipresolver(new PIPResolver());
        assertTrue(data.getPipresolver()!=null);
        data.setRequired(true);
        assertTrue(data.isRequired());
        data.toString();
    }

    @Test
    public void testPIPType(){
        PIPType data = new PIPType();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
        data.setPipconfigurations(new HashSet<>());
        assertTrue(data.getPipconfigurations()!=null);
        data.addPipconfiguration(new PIPConfiguration());
        data.removePipconfiguration(new PIPConfiguration());
        data.setType("SQL");
        assertTrue(data.isSQL());
        data.setType("LDAP");
        assertTrue(data.isLDAP());
        data.setType("CSV");
        assertTrue(data.isCSV());
        data.setType("Hyper-CSV");
        assertTrue(data.isHyperCSV());
        data.setType("Custom");
        assertTrue(data.isCustom());
    }

    @Test
    public void testPIPResolver(){
        PIPResolver data = new PIPResolver();
        new PIPResolver(new PIPResolver());
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
        assertTrue(data.getCreatedDate()!=null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!=null);
        data.setPipconfiguration(new PIPConfiguration());
        assertTrue(data.getPipconfiguration()!=null);
        data.setPipresolverParams(new HashSet<>());
        assertTrue(data.getPipresolverParams()!=null);
        data.addPipresolverParam(new PIPResolverParam());
        data.removePipresolverParam(new PIPResolverParam());
        data.clearParams();
        data.getConfiguration("test");
        data.setReadOnly(true);
        assertTrue(data.isReadOnly());
        data.toString();
        Properties properties = new Properties();
        data.generateProperties(properties,"test");
        try {
            data.readProperties("test", properties);
        } catch (PIPException e) {
            fail();
        }
    }

    @Test
    public void testPIPConfiguration(){
        PIPConfiguration data = new PIPConfiguration();
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
        assertTrue(data.getCreatedDate()!=null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate()!=null);
        try {
            data.readProperties("test", data.generateProperties("test"));
        } catch (PIPException e) {
            fail();
        }
        data.setPiptype(new PIPType());
        assertTrue(data.getPiptype()!=null);
        data.setPipresolvers(new HashSet<>());
        assertTrue(data.getPipresolvers()!=null);
        data.addPipresolver(new PIPResolver());
        data.removePipresolver(new PIPResolver());
        data.getConfiguration("test");
        data.setReadOnly(true);
        assertTrue(data.isReadOnly());
        data.setRequiresResolvers(true);
        assertTrue(data.requiresResolvers());
        data.toString();
    }

}
