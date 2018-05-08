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

package org.onap.policy.brms;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.onap.policy.api.PolicyException;
import org.onap.policy.brms.api.BrmsHandler;

public class BrmsPushTest {

    private static final String VALID_FILE = "src/test/resources/config.properties";
    private static final String INVALID_FILE = "src/test/resources/failure.properties";

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest() throws PolicyException {
        new BrmsHandler(null);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest1() throws PolicyException {
        new BrmsHandler("src/test/resources/filenotexists.txt");
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest2() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "defaultName";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest3() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryID";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "RESOURCE_NAME";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest4() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "repositoryURL";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest5() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "repositoryName";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest6() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryURL";
        prop.value = "http://nexus:8081/nexus/content/repositories/releases,"
                        + "http://nexus:8081/nexus/content/repositories/releases";
        prop.remove = false;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "repositoryUsername";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest7() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "repositoryPassword";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest8() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "policyKeyID";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest9() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "sync";
        prop.value = "true";
        prop.remove = false;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "brms.dependency.version";
        prop.remove = true;
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "groupNames";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest10() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "groupNames";
        prop.value = "";
        prop.remove = false;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest11() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "default.groupID";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest12() throws PolicyException {
        final PropertyChange prop = new PropertyChange();
        prop.key = "default.artifactID";
        prop.remove = true;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test(expected = PolicyException.class)
    public void brmsHandlerFailTest13() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "NOTIFICATION_TYPE";
        prop.value = "dmaap";
        prop.remove = false;
        final List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "NOTIFICATION_SERVERS";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BrmsHandler(INVALID_FILE);
    }

    @Test
    public void brmsHandlerTest() throws PolicyException {
        assertNotNull(new BrmsHandler(VALID_FILE));
    }

    private void setFailureProperties(final List<PropertyChange> properties) throws PolicyException {
        final Properties validProp = new Properties();
        try {
            validProp.load(new FileInputStream(VALID_FILE));
            for (final PropertyChange prop : properties) {
                if (prop.remove) {
                    validProp.remove(prop.key);
                } else {
                    validProp.setProperty(prop.key, prop.value);
                }
            }
            validProp.store(new FileOutputStream(INVALID_FILE), null);
        } catch (final IOException e) {
            throw new PolicyException(e);
        }
    }

    class PropertyChange {
        public String key = null;
        public String value = null;
        public Boolean remove = false;
    }
}
