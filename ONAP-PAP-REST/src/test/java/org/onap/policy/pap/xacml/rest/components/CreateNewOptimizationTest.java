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

import org.junit.Test;

public class CreateNewOptimizationTest {
    @Test
    public void testModel() {
        CreateNewOptimizationModel model = new CreateNewOptimizationModel();
        assertNotNull(model);
        model = new CreateNewOptimizationModel("file.yml", "model", "desc", "1.0", "id");
        assertNotNull(model);

        assertEquals(1, model.addValuesToNewModel().size());
    }

    @Test(expected = NullPointerException.class)
    public void testSave() {
        CreateNewOptimizationModel model =
            new CreateNewOptimizationModel("file.yml", "model", "desc", "1.0", "id");

        model.saveImportService();
    }
}
