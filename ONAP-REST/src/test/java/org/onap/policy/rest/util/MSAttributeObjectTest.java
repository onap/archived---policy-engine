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
package org.onap.policy.rest.util;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

public class MSAttributeObjectTest {

    @Test
    public void testMSAttributeObject(){
        MSAttributeObject data = new MSAttributeObject();
        data.setClassName("Test");
        assertTrue("Test".equals(data.getClassName()));
        data.setRefAttribute(new HashMap<>());
        assertTrue(data.getRefAttribute()!=null);
        data.setAttribute(new HashMap<>());
        assertTrue(data.getAttribute()!=null);
        data.setEnumType(new HashMap<>());
        assertTrue(data.getEnumType()!=null);
        data.addAttribute("test", "test");
        data.addRefAttribute("test", "test");
        data.addAllAttribute(new HashMap<>());
        data.addAllRefAttribute(new HashMap<>());
        data.setSubClass(new HashMap<>());
        assertTrue(data.getSubClass()!=null);
        data.addAllSubClass(new HashMap<>());
        data.setDependency("Test");
        assertTrue("Test".equals(data.getDependency()));
        data.addSingleEnum("test", "test");
        data.setMatchingSet(new HashMap<>());
        assertTrue(data.getMatchingSet()!=null);
        data.addMatchingSet("test", "test");
        data.addMatchingSet(new HashMap<>());
        data.setPolicyTempalate(true);
        assertTrue(data.isPolicyTempalate());
    }

    @Test
    public void testMSAttributeValue(){
        MSAttributeValue data = new MSAttributeValue();
        data.setName("Test");
        assertTrue("Test".equals(data.getName()));
        data.setType("Test");
        assertTrue("Test".equals(data.getType()));
        data.setRequired(true);
        assertTrue(data.getRequired());
        data.setArrayValue(true);
        assertTrue(data.getArrayValue());
        data.setDefaultValue("Test");
        assertTrue("Test".equals(data.getDefaultValue()));
    }
}
