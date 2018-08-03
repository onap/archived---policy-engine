/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.pap.xacml.rest.adapters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UpdateObjectDataTest {

    @Test
    public void testClosedLoopFaultTrapDatas(){
        UpdateObjectData updateObject = new UpdateObjectData();
        updateObject.setAction("Rename");
        assertTrue("Rename".equals(updateObject.getAction()));
        updateObject.setNewPolicyName("com.Config_test1.1.json");
        assertTrue("com.Config_test1.1.json".equals(updateObject.getNewPolicyName()));
        updateObject.setOldPolicyName("com.Config_test.1.json");
        assertTrue("com.Config_test.1.json".equals(updateObject.getOldPolicyName()));
    }

}
