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
package org.onap.policy.admin;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.when;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.jpa.WatchPolicyNotificationTable;

public class PolicyNotificationMailTest {

	private PolicyVersion version;
	private String policyName = "com/Config_Test";
	private CommonClassDao commonClassDao;
	private List<Object> data = null;

	@Before
	public void setUp() throws Exception {
		PolicyController.setjUnit(true);
		PolicyController.setSmtpApplicationName("Test");
		PolicyController.setSmtpEmailExtension("test.com");
		PolicyController.setSmtpHost("test");
		PolicyController.setSmtpPort("23");
		PolicyController.setSmtpPassword("test");
		PolicyController.setSmtpUsername("test");

		version = new PolicyVersion();
		version.setPolicyName("com/Config_Test");
		version.setModifiedBy("xyz");

		WatchPolicyNotificationTable watch = new WatchPolicyNotificationTable();
		watch.setPolicyName("com/Config_Test");
		data = new ArrayList<>();
		data.add(watch);

		commonClassDao = mock(CommonClassDao.class);
        PolicyController.setCommonClassDao(commonClassDao);
        when(commonClassDao.getDataByQuery("from WatchPolicyNotificationTable where policyName like:policyFileName", null)).thenReturn(data);
	}

	@Test
	public final void testJavaMailSenderImpl() {
		PolicyNotificationMail notificationMail = new PolicyNotificationMail();
		try{
			assertTrue(notificationMail.javaMailSenderImpl() != null);
		}catch(Exception e){
			fail();
		}
	}

	@Test
	public final void testSendMail() {
		PolicyNotificationMail notificationMail = new PolicyNotificationMail();
		try{
			notificationMail.sendMail(version, policyName, "EditPolicy", commonClassDao);
			notificationMail.sendMail(version, policyName, "Rename", commonClassDao);
			notificationMail.sendMail(version, policyName, "DeleteAll", commonClassDao);
			notificationMail.sendMail(version, policyName, "DeleteOne", commonClassDao);
			notificationMail.sendMail(version, policyName, "DeleteScope", commonClassDao);
			notificationMail.sendMail(version, policyName, "SwitchVersion", commonClassDao);
			notificationMail.sendMail(version, policyName, "Move", commonClassDao);
		}catch(Exception e){
			fail();
		}
	}

}
