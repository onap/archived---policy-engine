/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2019-2020 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDaoTransaction;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyDbDaoEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpServletRequest;

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
        ResponseEntity<String> responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        version.setHigherVersion(1);
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setEditPolicy(true);
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setEditPolicy(false);
        version.setHigherVersion(0);
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setEditPolicy(true);
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        version.setHigherVersion(1);
        policyData.setConfigPolicyType("Firewall Config");
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setConfigPolicyType("BRMS_Raw");
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setConfigPolicyType("BRMS_Param");
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setConfigPolicyType("Base");
        Mockito.when(policyData.getRuleData()).thenReturn(new LinkedHashMap<>());

        SessionFactory mockSessionFactory = Mockito.mock(SessionFactory.class);
        Session mockSession = Mockito.mock(Session.class);
        Criteria mockCriteria = Mockito.mock(Criteria.class);
        List<?> policyDbDaoEntityList = new LinkedList<>();

        Mockito.when(mockSessionFactory.openSession()).thenReturn(mockSession);
        Mockito.when(mockSession.createCriteria(PolicyDbDaoEntity.class)).thenReturn(mockCriteria);
        Mockito.when(mockCriteria.list()).thenReturn(policyDbDaoEntityList);
        Whitebox.setInternalState(PolicyDbDao.class, "sessionfactory", mockSessionFactory);

        PolicyDbDao mockPolicyDbDao = Mockito.mock(PolicyDbDao.class);
        PolicyDbDaoTransaction mockTransaction = Mockito.mock(PolicyDbDaoTransaction.class);
        Mockito.when(mockPolicyDbDao.getNewTransaction()).thenReturn(mockTransaction);

        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setConfigPolicyType("ClosedLoop_PM");
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setConfigPolicyType("Micro Service");
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setConfigPolicyType("Optimization");
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setPolicyType("Action");
        List<Object> choices = new ArrayList<>();
        policyData.setRuleAlgorithmschoices(choices);
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();

        policyData.setPolicyType("Decision");
        List<Object> settings = new ArrayList<>();
        policyData.setSettings(settings);
        responseEntity = creation.savePolicy(policyData, response);
        assertThat(responseEntity).isNotNull();
    }
}
