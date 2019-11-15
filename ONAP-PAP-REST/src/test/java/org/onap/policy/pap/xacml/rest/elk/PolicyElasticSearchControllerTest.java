/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.springframework.mock.web.MockHttpServletResponse;

public class PolicyElasticSearchControllerTest {

    private PolicyElasticSearchController controller;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    @Before
    public void setup() {
        controller = new PolicyElasticSearchController();
        request = Mockito.mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
    }

    @Test
    public void testSearchDictionary() throws IOException {
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
        for (int i = 0; i < jsonString.size(); i++) {
            BufferedReader br = new BufferedReader(new StringReader(jsonString.get(i)));
            when(request.getReader()).thenReturn(br);
            assertThatCode(() -> controller.searchDictionary(request, response)).doesNotThrowAnyException();
        }
    }

    @Test
    public void testController() throws IOException {
        CommonClassDao dao = Mockito.mock(CommonClassDao.class);
        PolicyElasticSearchController controller = new PolicyElasticSearchController(dao);
        assertEquals(PolicyIndexType.all, controller.toPolicyIndexType(null));
        assertEquals(PolicyIndexType.config, controller.toPolicyIndexType("config"));

        Map<String, String> searchKeys = new HashMap<String, String>();
        searchKeys.put("key", "value");
        assertThatThrownBy(() -> controller.search(PolicyIndexType.config, "text", searchKeys))
            .isInstanceOf(Exception.class);

        when(request.getParameter("policyName")).thenReturn("policyName");
        when(request.getParameter("action")).thenReturn("search");
        when(request.getReader())
            .thenReturn(new BufferedReader(new StringReader("{\"searchdata\": { \"query\": \"value space\", "
                + "\"policyType\": \"all\", " + "\"closedLooppolicyType\": \"type\", " + "\"onapName\": \"pef\", "
                + "\"vnfType\": \"vnf\", " + "\"policyStatus\": \"active\", " + "\"vproAction\": \"reboot\", "
                + "\"serviceType\": \"type\", " + "\"bindTextSearch\": \"pef\", " + "\"d2Service\": \"vDNS\"} }")));
        controller.searchPolicy(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        PolicyRestAdapter policyData = new PolicyRestAdapter();
        assertFalse(controller.deleteElk(policyData));
    }
}
