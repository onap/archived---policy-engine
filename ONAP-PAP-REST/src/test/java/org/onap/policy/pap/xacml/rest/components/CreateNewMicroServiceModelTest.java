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

package org.onap.policy.pap.xacml.rest.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.pap.xacml.rest.daoimpl.CommonClassDaoImpl;

public class CreateNewMicroServiceModelTest {
    @Test
    public void testEmptyModel() {
        CreateNewMicroServiceModel model =
            new CreateNewMicroServiceModel("file.yml", "model", "desc", "1.0", "id");
        assertNotNull(model);
        assertEquals(1, model.addValuesToNewModel(".yml").size());
    }

    @Test
    public void testCreateConstructor1() {
        CreateNewMicroServiceModel model = new CreateNewMicroServiceModel(null, null, null, null);
        assertNotNull(model);
    }

    @Test
    public void testCreateModel() throws Exception {
        // Mock file retrieval
        File testFile = new File("testFile");
        File[] testList = new File[1];
        testList[0] = testFile;
        File impl = Mockito.mock(File.class);
        when(impl.listFiles()).thenReturn(testList);
        when(impl.isFile()).thenReturn(true);

        // Mock internal dictionary retrieval
        CommonClassDaoImpl daoImpl = Mockito.mock(CommonClassDaoImpl.class);
        when(daoImpl.getDataById(any(), anyString(), anyString()))
            .thenReturn(Collections.emptyList());

        // Test create methods
        String testFileName = "testFile.yml";
        String testVal = "testVal";
        CreateNewMicroServiceModel model =
            new CreateNewMicroServiceModel(testFileName, testVal, testVal, testVal, testVal);
        assertEquals(1, model.addValuesToNewModel(".yml").size());
    }
}
