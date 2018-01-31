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

package org.onap.brmsgw.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.onap.policy.api.PolicyException;
import org.onap.policy.brmsInterface.BRMSHandler;

public class BRMSPushTest {

    private final String VALIDFILE = "src/test/resources/config.properties";
    private final String INVALIDFILE = "src/test/resources/failure.properties";

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest() throws PolicyException {
        new BRMSHandler(null);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest1() throws PolicyException {
        new BRMSHandler("src/test/resources/filenotexists.txt");
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest2() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "defaultName";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest3() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryID";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "RESOURCE_NAME";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest4() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryURL";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest5() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryName";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest6() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryURL";
        prop.value = "http://nexus:8081/nexus/content/repositories/releases, http://nexus:8081/nexus/content/repositories/releases";
        prop.remove = false;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "repositoryUsername";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest7() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "repositoryPassword";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest8() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "policyKeyID";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest9() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "sync";
        prop.value = "true";
        prop.remove = false;
        List<PropertyChange> props = new LinkedList<>();
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
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest10() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "groupNames";
        prop.value = "";
        prop.remove = false;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest11() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "default.groupID";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest12() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "default.artifactID";
        prop.remove = true;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test (expected = PolicyException.class)
    public void bRMSHandlerFailTest13() throws PolicyException {
        PropertyChange prop = new PropertyChange();
        prop.key = "NOTIFICATION_TYPE";
        prop.value = "dmaap";
        prop.remove = false;
        List<PropertyChange> props = new LinkedList<>();
        props.add(prop);
        prop = new PropertyChange();
        prop.key = "NOTIFICATION_SERVERS";
        prop.remove = true;
        props.add(prop);
        setFailureProperties(props);
        new BRMSHandler(INVALIDFILE);
    }

    @Test
    public void BRMSHandlerTest() throws PolicyException {
        assertNotNull(new BRMSHandler(VALIDFILE));
    }

    private void setFailureProperties(List<PropertyChange> properties) throws PolicyException {
        Properties validProp = new Properties();
        try {
            validProp.load(new FileInputStream(VALIDFILE));
            for (PropertyChange prop: properties) {
                if(prop.remove) {
                    validProp.remove(prop.key);
                }else {
                    validProp.setProperty(prop.key, prop.value);
                }
            }
            validProp.store(new FileOutputStream(INVALIDFILE), null);
        } catch (IOException e) {
            throw new PolicyException(e);
        }
    }

    class PropertyChange {
        public String key = null;
        public String value = null;
        public Boolean remove = false;
    }
}
