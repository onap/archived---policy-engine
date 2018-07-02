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
package org.onap.policy.pap.xacml.rest.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class MicroServicePolicyTest {
	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConstructor1() {
		thrown.expect(NullPointerException.class);
		MicroServiceConfigPolicy policy = new MicroServiceConfigPolicy();
		policy.getCorrectPolicyDataObject();
		fail("Expected an exception");
	}
	
	@Test
	public void testConstructor2() {
		PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
		MicroServiceConfigPolicy policy = new MicroServiceConfigPolicy(policyAdapter);
		assertNull(policy.getCorrectPolicyDataObject());
	}
	
	@PrepareForTest({MicroServiceConfigPolicy.class})
	@Test
	public void testPrepareToSave() throws Exception {
		// Need to mock internal dictionary retrieval
	    MicroServiceModels model = new MicroServiceModels();
        model.setAnnotation("naming-type=matching-true, nfRole=matching-true, property-name=matching-true");
        List<Object> list = new ArrayList<>();
        list.add(model);
		CommonClassDaoImpl impl = Mockito.mock(CommonClassDaoImpl.class);
		PowerMockito.whenNew(CommonClassDaoImpl.class).withNoArguments().thenReturn(impl);
		when(impl.getDataById(any(), anyString(), anyString())).thenReturn(list);
		
		PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
		MicroServiceConfigPolicy policy = new MicroServiceConfigPolicy(policyAdapter);
		policyAdapter.setHighestVersion(1);
		policyAdapter.setPolicyType("Config");
		policyAdapter.setNewFileName("foo.xml");
		policyAdapter.setJsonBody("{ \"version\": \"1.0\"}");
		policyAdapter.setServiceType("foo");
		policy.prepareToSave();
		assertEquals(policy.isPreparedToSave(), true);
	}
	
	@Test
	public void testCreateConstructor1() {
		CreateNewMicroServiceModel model = new CreateNewMicroServiceModel(null, null, null, null);
		assertNotNull(model);
	}
	
	@PrepareForTest({CreateNewMicroServiceModel.class})
	@Test
	public void testCreateModel() throws Exception {
		// Mock file retrieval
		File testFile = new File("testFile");
		File[] testList = new File[1];
		testList[0] = testFile;
		File impl = Mockito.mock(File.class);
		PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(impl);
		when(impl.listFiles()).thenReturn(testList);
		when(impl.isFile()).thenReturn(true);

		// Mock internal dictionary retrieval
		CommonClassDaoImpl daoImpl = Mockito.mock(CommonClassDaoImpl.class);
		PowerMockito.whenNew(CommonClassDaoImpl.class).withNoArguments().thenReturn(daoImpl);
		when(daoImpl.getDataById(any(), anyString(), anyString())).thenReturn(Collections.emptyList());

		// Test create methods
		String testFileName = "testFile.zip";
		String testVal = "testVal";
		CreateNewMicroServiceModel model = new CreateNewMicroServiceModel(testFileName, testVal, testVal, testVal, testVal, false);
		model.addValuesToNewModel(".xmi");
		model.saveImportService();
	}
}
