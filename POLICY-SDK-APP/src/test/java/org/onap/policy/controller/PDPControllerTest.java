/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.controller;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.model.Roles;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroupStatus;
import org.springframework.mock.web.MockHttpServletResponse;



public class PDPControllerTest extends Mockito{

	private static Logger logger = FlexLogger.getLogger(PDPControllerTest.class);
	private Set<OnapPDPGroup> groupsData;
	private Set<StdPDPGroup> groups;
	private static List<Object> rolesdata;

	@Before
	public void setUp() throws Exception{
		logger.info("setUp: Entering");
		rolesdata = new ArrayList<>();
		Roles roles = new Roles();
		roles.setLoginId("Test");
		roles.setRole("super-admin");
		Roles roles1 = new Roles();
		roles1.setLoginId("Test");
		roles1.setRole("admin");
		roles1.setScope("['com','Test']");
		rolesdata.add(roles);
		rolesdata.add(roles1);

		groups = new HashSet<>();
		StdPDPGroup group = new StdPDPGroup();
		group.setId("default");
		group.setDefault(true);
		group.setName("default");
		group.setDescription("The default group where new PDP's are put.");
		group.setStatus(new StdPDPGroupStatus());
		groups.add(group);
		groupsData = new HashSet<>();
		for (OnapPDPGroup g : this.groups) {
			groupsData.add(g);
		}
	}

	@Test
	public void testPDPGroupData(){
		HttpServletRequest request = mock(HttpServletRequest.class);
		MockHttpServletResponse response =  new MockHttpServletResponse();
        PolicyController controller = mock(PolicyController.class);
        PDPController pdpController = new PDPController();
        pdpController.setJunit(true);;
        pdpController.setPolicyController(controller);
        pdpController.setGroupsData(groupsData);
        when(controller.getRoles("Test")).thenReturn(rolesdata);
        pdpController.getPDPGroupEntityData(request, response);
        try {
			assertTrue(response.getContentAsString() != null && response.getContentAsString().contains("data"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception Occured"+e);
		}
	}

}
