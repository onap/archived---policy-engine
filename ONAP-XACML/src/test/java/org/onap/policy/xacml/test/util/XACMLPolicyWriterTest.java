/*
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.xacml.test.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.xacml.util.XACMLPolicyWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class XACMLPolicyWriterTest {
  private static final Log logger = LogFactory.getLog(XACMLPolicyWriterTest.class);
  private static Path configPolicyPathValue;
  private static Path actionPolicyPathValue;

  @Before
  public void setUp() {
    File templateFile;
    ClassLoader classLoader = getClass().getClassLoader();
    try {
      templateFile = new File(classLoader.getResource("Config_SampleTest1206.1.xml").getFile());
      configPolicyPathValue = templateFile.toPath();
      templateFile = new File(classLoader.getResource("Action_TestActionPolicy.1.xml").getFile());
      actionPolicyPathValue = templateFile.toPath();
    } catch (Exception e1) {
      logger.error("Exception Occured" + e1);
    }
  }

  @SuppressWarnings("static-access")
  @Test
  public void xacmlPolicyWriterTest() throws IOException {
    XACMLPolicyWriter writer = new XACMLPolicyWriter();
    String configResponseValue = writer.changeFileNameInXmlWhenRenamePolicy(configPolicyPathValue);
    assertTrue(configResponseValue.equals("txt"));
    String actionResponseValue = writer.changeFileNameInXmlWhenRenamePolicy(actionPolicyPathValue);
    assertTrue(actionResponseValue.equals("json"));
  }

  @Test
  public void testWrites() throws Exception {
    // Set up test data
    PolicyType policyType = new PolicyType();
    OutputStream os = new ByteArrayOutputStream();
    File tmpfile = File.createTempFile("foo", null);
    Path filename = tmpfile.toPath();
    PolicySetType policySet = new PolicySetType();
    
    // delete tmp file before running the test
    tmpfile.delete();

    // ensure its deleted after writePolicyFile() re-creates it
    tmpfile.deleteOnExit();

    // Test write combinations
    assertNotNull(XACMLPolicyWriter.writePolicyFile(filename, policySet));
    assertNotNull(XACMLPolicyWriter.writePolicyFile(filename, policyType));
    assertNotNull(XACMLPolicyWriter.getXmlAsInputStream(policyType));
    XACMLPolicyWriter.writePolicyFile(os, policySet);
    assertNotNull(os.toString());
    XACMLPolicyWriter.writePolicyFile(os, policyType);
    assertNotNull(os.toString());
  }
}
