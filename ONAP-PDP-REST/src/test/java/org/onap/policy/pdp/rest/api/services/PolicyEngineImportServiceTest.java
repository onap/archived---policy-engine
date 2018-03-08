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
package org.onap.policy.pdp.rest.api.services;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class PolicyEngineImportServiceTest {
	@Test
	public final void negativeTestService1() {
		// Negative test constructor
		String json = "testJson";
		MultipartFile file = null;
		String id = "testID";
		PolicyEngineImportService service = new PolicyEngineImportService(json, file, id);
		
		// Test gets
		assertNotNull(service.getResult());
		assertEquals(service.getResponseCode().value(), 400);
	}

	@Test
	public final void negativeTestService2() {
		// Negative test constructor
		String json = "{\n\"serviceName\": \"testVal\"\n}\n";
		byte[] content = "bar".getBytes();
		MultipartFile file = new MockMultipartFile("foo", content);
		String id = "testID";
		PolicyEngineImportService service = new PolicyEngineImportService(json, file, id);
		
		// Test gets
		assertNotNull(service.getResult());
		assertEquals(service.getResponseCode().value(), 400);
	}
}
