/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.rest.adapter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class RainyDayParamsTest {

    @Test
    public void testRainyDayParams(){
        RainyDayParams params = new RainyDayParams();
        params.setServiceType("Test");
        assertTrue("Test".equals(params.getServiceType()));
        params.setVnfType("Test");
        assertTrue("Test".equals(params.getVnfType()));
        params.setBbid("Test");
        assertTrue("Test".equals(params.getBbid()));
        params.setWorkstep("Test");
        assertTrue("Test".equals(params.getWorkstep()));
        params.setTreatmentTableChoices(new ArrayList<>());
        assertTrue(params.getTreatmentTableChoices() != null);
        params.setErrorcode(new ArrayList<>());
        assertTrue(params.getErrorcode() != null);
        params.setTreatment(new ArrayList<>());
        assertTrue(params.getTreatment() != null);
    }
}
