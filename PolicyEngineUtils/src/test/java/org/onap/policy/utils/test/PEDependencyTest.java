/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Exclusion;
import org.junit.Test;
import org.onap.policy.api.PEDependency;

public class PEDependencyTest {

    @Test
    public void pojoTests(){
        PEDependency pe = new PEDependency();
        pe.setArtifactId("test");
        pe.setGroupId("test");
        pe.setVersion("1");
        pe.setType("jar");
        pe.setScope("test");
        pe.setClassifier("pom");
        List<Exclusion> exclusions = new ArrayList<>();
        Exclusion e = new Exclusion();
        e.setArtifactId("ex1");
        e.setGroupId("eG");
        exclusions.add(e);
        pe.setExclusions(exclusions);
        pe.getDependency();
        assertEquals(exclusions, pe.getExclusions());
        assertEquals("pom", pe.getClassifier());
        assertEquals("test", pe.getScope());
        assertEquals("jar", pe.getType());
        assertEquals("1", pe.getVersion());
        assertEquals("test", pe.getGroupId());
        assertEquals("test", pe.getArtifactId());
    }

}
