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
package org.onap.policy.pap.xacml.rest.elk;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;

public class PolicyElasticSearchControllerTest {

    private PolicyElasticSearchController conroller;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    @Before
    public void setup(){
        conroller = new PolicyElasticSearchController();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
    }

    @Test
    public void testSearchDictionary(){
        List<String> jsonString = new ArrayList<>();
        jsonString.add("{\"type\":\"attribute\",\"data\":{\"xacmlId\":\"Test\"}}");
        jsonString.add("{\"type\":\"onapName\",\"data\":{\"onapName\":\"Test\"}}");
        jsonString.add("{\"type\":\"actionPolicy\",\"data\":{\"attributeName\":\"Test\"}}");
        jsonString.add("{\"type\":\"brmsParam\",\"data\":{\"ruleName\":\"Test\"}}");
        jsonString.add("{\"type\":\"pepOptions\",\"data\":{\"pepName\":\"Test\"}}");
        jsonString.add("{\"type\":\"clSite\",\"data\":{\"siteName\":\"Test\"}}");
        jsonString.add("{\"type\":\"clService\",\"data\":{\"serviceName\":\"Test\"}}");
        jsonString.add("{\"type\":\"clVarbind\",\"data\":{\"varbindName\":\"Test\"}}");
        jsonString.add("{\"type\":\"clVnf\",\"data\":{\"vnftype\":\"Test\"}}");
        jsonString.add("{\"type\":\"clVSCL\",\"data\":{\"vsclaction\":\"Test\"}}");
        jsonString.add("{\"type\":\"decision\",\"data\":{\"xacmlId\":\"Test\"}}");
        jsonString.add("{\"type\":\"fwTerm\",\"data\":{\"termName\":\"Test\"}}");
        jsonString.add("{\"type\":\"msDCAEUUID\",\"data\":{\"name\":\"Test\"}}");
        jsonString.add("{\"type\":\"msLocation\",\"data\":{\"name\":\"Test\"}}");
        jsonString.add("{\"type\":\"msModels\",\"data\":{\"modelName\":\"Test\"}}");
        jsonString.add("{\"type\":\"psGroupPolicy\",\"data\":{\"name\":\"Test\"}}");
        jsonString.add("{\"type\":\"safeRisk\",\"data\":{\"name\":\"Test\"}}");
        jsonString.add("{\"type\":\"safePolicyWarning\",\"data\":{\"name\":\"Test\"}}");
        for(int i = 0; i < jsonString.size(); i++){
            try(BufferedReader br = new BufferedReader(new StringReader(jsonString.get(i)))) {
                when(request.getReader()).thenReturn(br);
                conroller.searchDictionary(request, response);
            } catch (Exception e) {
                assertEquals(NullPointerException.class, e.getClass());
            }
        }
    }
}
