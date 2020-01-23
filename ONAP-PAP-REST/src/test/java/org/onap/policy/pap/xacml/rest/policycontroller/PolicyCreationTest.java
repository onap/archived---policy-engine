/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.policycontroller;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.mockrunner.mock.web.MockHttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletResponse;

public class PolicyCreationTest {
    @Test
    public void testCreation() {
        CommonClassDao dao = Mockito.mock(CommonClassDao.class);
        PolicyVersion version = new PolicyVersion();
        version.setPolicyName("policyname");
        Mockito.when(dao.getEntityItem(eq(PolicyVersion.class), eq("policyName"), anyString())).thenReturn(version);
        PolicyCreation.setCommonClassDao(dao);
        assertEquals(dao, PolicyCreation.getCommonClassDao());
        PolicyCreation creation = new PolicyCreation(dao);
        assertEquals(dao, PolicyCreation.getCommonClassDao());

        HttpServletRequest req = new MockHttpServletRequest();
        Exception cause = new Exception("oops");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("oops", cause);
        assertEquals(HttpStatus.BAD_REQUEST,
            creation.messageNotReadableExceptionHandler(req, exception).getStatusCode());

        HttpServletResponse response = new MockHttpServletResponse();
        PolicyRestAdapter policyData = new PolicyRestAdapter();
        policyData.setPolicyType("Config");
        policyData.setConfigPolicyType("ClosedLoop_Fault");
        policyData.setDomainDir("domain");
        policyData.setPolicyName("policyname");
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();

        version.setHigherVersion(1);
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();

        policyData.setEditPolicy(true);
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();

        policyData.setEditPolicy(false);
        version.setHigherVersion(0);
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();

        policyData.setEditPolicy(true);
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();

        version.setHigherVersion(1);
        policyData.setConfigPolicyType("Firewall Config");
        assertThatThrownBy(() -> creation.savePolicy(policyData, response))
            .isInstanceOf(IllegalArgumentException.class);

        policyData.setConfigPolicyType("BRMS_Raw");
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();
        policyData.setConfigPolicyType("BRMS_Param");
        assertThatThrownBy(() -> creation.savePolicy(policyData, response))
            .isInstanceOf(IllegalArgumentException.class);
        policyData.setConfigPolicyType("Base");
        Mockito.when(policyData.getRuleData()).thenReturn(any());
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();
        policyData.setConfigPolicyType("ClosedLoop_PM");
        assertThatThrownBy(() -> creation.savePolicy(policyData, response))
            .isInstanceOf(IllegalArgumentException.class);
        policyData.setConfigPolicyType("Micro Service");
        assertThatThrownBy(() -> creation.savePolicy(policyData, response))
            .isInstanceOf(IllegalArgumentException.class);
        policyData.setConfigPolicyType("Optimization");
        assertThatThrownBy(() -> creation.savePolicy(policyData, response))
            .isInstanceOf(IllegalArgumentException.class);

        policyData.setPolicyType("Action");
        List<Object> choices = new ArrayList<Object>();
        policyData.setRuleAlgorithmschoices(choices);
        assertThatCode(() -> creation.savePolicy(policyData, response)).doesNotThrowAnyException();

        policyData.setPolicyType("Decision");
        List<Object> settings = new ArrayList<Object>();
        policyData.setSettings(settings);
        assertThatThrownBy(() -> creation.savePolicy(policyData, response))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
