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

package org.onap.policy.pap.xacml.rest.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;

@RunWith(PowerMockRunner.class)
public class DeleteHandlerTest {
    @Test
    public void testGets() {
        DeleteHandler handler = new DeleteHandler();
        assertNotNull(handler);
        assertEquals(handler.preSafetyCheck(null), true);
        assertNull(handler.getDeletedGroup());
    }

    @Test
    public void testGetInstance() {
        DeleteHandler handler = DeleteHandler.getInstance();
        assertNotNull(handler);
    }

    @PrepareForTest({DeleteHandler.class, XACMLPapServlet.class})
    @Test
    public void testDeletes() throws Exception {
        // Mock request
        DeleteHandler handler = new DeleteHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setBodyContent("{\n\"PAPPolicyType\": \"StdPAPPolicy\"\n}\n");

        // Mock servlet
        PAPPolicyEngine engine = Mockito.mock(StdEngine.class);
        PowerMockito.mockStatic(XACMLPapServlet.class);
        when(XACMLPapServlet.getPAPEngine()).thenReturn(engine);
        when(engine.getGroup(any())).thenReturn(null);

        // Mock elastic search
        PolicyElasticSearchController controller = Mockito.mock(PolicyElasticSearchController.class);
        PowerMockito.whenNew(PolicyElasticSearchController.class).withNoArguments().thenReturn(controller);

        // Mock entity manager
        EntityManager em = Mockito.mock(EntityManager.class);

        // Test deletion from PAP
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            handler.doAPIDeleteFromPAP(request, response);
        }
        catch (Exception ex) {
            fail("Not expecting an exception: " + ex);
        }

        // Test deletion from PDP
        ONAPLoggingContext loggingContext = Mockito.mock(ONAPLoggingContext.class);
        try {
            handler.doAPIDeleteFromPDP(request, response, loggingContext);
        }
        catch (Exception ex) {
            fail("Not expecting an exception: " + ex);
        }

        // Test delete entity
        PolicyEntity policyEntity = new PolicyEntity();
        policyEntity.setPolicyName("testVal");
        String result = DeleteHandler.deletePolicyEntityData(em, policyEntity);
        assertEquals(result, "success");

        // Test check entity
        Connection con = null;
        List<?> peResult = Collections.emptyList();
        assertEquals(DeleteHandler.checkPolicyGroupEntity(con, peResult), false);
    }
}
