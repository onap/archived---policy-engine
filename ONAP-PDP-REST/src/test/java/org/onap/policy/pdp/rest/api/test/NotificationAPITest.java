/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.pdp.rest.api.services.NotificationService;
import org.onap.policy.pdp.rest.api.services.NotificationService.NotificationServiceType;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

import com.att.research.xacml.util.XACMLProperties;

public class NotificationAPITest {

	@Before
	public void setup() throws IOException{
		// Fix properties for next test cases.
		XACMLProperties.reloadProperties();
		System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/pass.xacml.pdp.properties");
		XACMLProperties.getProperties();
	}

	@Test
	public void testPropertyFailure() throws IOException{
		// Change properties and fail.
		XACMLProperties.reloadProperties();
		System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/fail.xacml.pdp.properties");
		XACMLProperties.getProperties();
		NotificationService notificationService = new NotificationService(null,null,null);
		assertEquals(HttpStatus.BAD_REQUEST, notificationService.getResponseCode());
		setup();
	}

	@Test
	public void testFailureTopicName(){
		NotificationService notificationService = new NotificationService(null,null,null);
		assertEquals(HttpStatus.BAD_REQUEST, notificationService.getResponseCode());
		assertEquals(XACMLErrorConstants.ERROR_DATA_ISSUE + "org.onap.policy.api.PolicyException: Notification Topic is null", notificationService.getResult());
		notificationService = new NotificationService(" ",null,null);
		assertEquals(HttpStatus.BAD_REQUEST, notificationService.getResponseCode());
		assertEquals(XACMLErrorConstants.ERROR_DATA_ISSUE + "org.onap.policy.api.PolicyException: Notification Topic is not valid. ", notificationService.getResult());
	}

	@Test
	public void testFailureServiceType(){
		NotificationService notificationService = new NotificationService("test",null,null);
		assertEquals(HttpStatus.BAD_REQUEST, notificationService.getResponseCode());
	}

	@Test
	public void threadTest() throws InterruptedException{
		NotificationService notificationSerivce = new NotificationService("test",null,NotificationServiceType.ADD);
		assertEquals(HttpStatus.OK, notificationSerivce.getResponseCode());
		// Wait for thread to remove the Topic Entry.
		await().atMost(Integer.toUnsignedLong(2500),TimeUnit.MILLISECONDS).until(()-> {
			// Trying to remove again should fail
			NotificationService nService = new NotificationService("test",null,NotificationServiceType.REMOVE);
			return HttpStatus.BAD_REQUEST.equals(nService.getResponseCode());
		});
		// Coverage Tests, Call Notification Service.
		NotificationService.sendNotification("test");
	}
}
