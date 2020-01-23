/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2020 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 Samsung
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

package org.onap.policy.xacml.test.std.pap;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.std.pap.StdEngine;
import com.att.research.xacml.util.FactoryException;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdEngineFactory;

public class StdEngineFactoryTest {

    private static String systemProperty;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void saveSystemProperty() {
        systemProperty = System.getProperty(StdEngine.PROP_PAP_REPO);
    }

    /**
     * restoreSystemProperty.
     */
    @AfterClass
    public static void restoreSystemProperty() {
        if (systemProperty != null) {
            System.setProperty(StdEngine.PROP_PAP_REPO, systemProperty);
        } else {
            System.clearProperty(StdEngine.PROP_PAP_REPO);
        }
    }

    @Test
    public void testStdEngineFactory() throws FactoryException, PAPException, IOException {
        StdEngineFactory stdFactory = new StdEngineFactory();
        System.setProperty("xacml.pap.pdps", "src/test/resources/pdps");
        assertNotNull(stdFactory.newEngine());
        Properties properties = new Properties();
        properties.setProperty("xacml.pap.pdps", "src/test/resources/pdps");
        assertNotNull(stdFactory.newEngine(properties));

        StdEngineFactory stdFactoryNew = new StdEngineFactory();
        System.setProperty("xacml.pap.pdps", "src/test/resources/pdpstest");
        PAPPolicyEngine engine = stdFactoryNew.newEngine();
        assertNotNull(engine);

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->
            engine.newGroup(null, null)
        );

        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() ->
            engine.movePDP(null, null)
        );

    }

    @Test
    public void testNegativeCase() throws Exception {
        // Setup test data
        Properties props = new Properties();
        String tmpdir = System.getProperty("java.io.tmpdir");
        props.setProperty(StdEngine.PROP_PAP_REPO, tmpdir);

        // Test factory failure cases
        try {
            StdEngineFactory factory = new StdEngineFactory();
            assertNotNull(factory.newEngine());
            assertNotNull(factory.newEngine(props));
        } catch (Exception ex) {
            fail("Not expecting any exceptions: " + ex);
        }

    }

    @Test
    public void testException() throws FactoryException, PAPException, IOException {
        Properties props = new Properties();
        File myFolder = folder.newFolder("idontexist");
        props.setProperty(StdEngine.PROP_PAP_REPO, myFolder.getAbsolutePath());
        StdEngineFactory factory = new StdEngineFactory();
        assertNotNull(factory.newEngine(props));

        //
        //
        //
        myFolder.setReadOnly();
        assertThat(catchThrowable(() -> { throw new PAPException(); }))
        .isInstanceOf(Exception.class);

        //
        //
        //
        File myFile = folder.newFile("iamafile");
        props.setProperty(StdEngine.PROP_PAP_REPO, myFile.getAbsolutePath());
        assertThatExceptionOfType(PAPException.class).isThrownBy(() ->
            factory.newEngine(props)
        );

        //
        //
        //
        props.setProperty(StdEngine.PROP_PAP_REPO, myFolder.getAbsolutePath() + "/badparent/dontexist");
        assertNull(factory.newEngine(props));

        //
        //
        //
        System.setProperty(StdEngine.PROP_PAP_REPO, myFolder.getAbsolutePath() + "/badparent/dontexist");

        assertNull(factory.newEngine());

    }
}
