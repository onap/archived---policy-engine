/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.adapters;

import static org.junit.Assert.*;

import org.junit.Test;

public class SearchDataTest {


    @Test
    public void testSearchData(){
        String data = "Test";
        SearchData searchData = new SearchData();
        searchData.setQuery(data);
        searchData.setPolicyType(data);
        searchData.setOnapName(data);
        searchData.setDescriptiveScope(data);
        searchData.setClosedLooppolicyType(data);
        searchData.setD2Service(data);
        searchData.setVnfType(data);
        searchData.setPolicyStatus(data);
        searchData.setVproAction(data);
        searchData.setServiceType(data);
        searchData.setBindTextSearch(data);
        assertEquals(data, searchData.getQuery());
        assertEquals(data, searchData.getPolicyType());
        assertEquals(data, searchData.getDescriptiveScope());
        assertEquals(data, searchData.getClosedLooppolicyType());
        assertEquals(data, searchData.getOnapName());
        assertEquals(data, searchData.getD2Service());
        assertEquals(data, searchData.getVnfType());
        assertEquals(data, searchData.getPolicyStatus());
        assertEquals(data, searchData.getVproAction());
        assertEquals(data, searchData.getServiceType());
        assertEquals(data, searchData.getBindTextSearch());
    }
}
