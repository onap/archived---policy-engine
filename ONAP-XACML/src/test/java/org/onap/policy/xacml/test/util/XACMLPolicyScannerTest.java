/*-
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.xacml.util.XACMLPolicyScanner;

import com.att.research.xacml.util.XACMLPolicyScanner.Callback;

public class XACMLPolicyScannerTest {

	private static final Log logger				= LogFactory.getLog(XACMLPolicyScannerTest.class);
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
			logger.error("Exception Occured"+e1);
		}
	}
	
	@Test
	public void xacmlPolicyScannerTest() throws IOException{
		Callback callback = null;
		try{
			XACMLPolicyScanner actionScanner = new XACMLPolicyScanner(actionPolicyPathValue, callback);
			assertTrue(actionScanner.getPolicyObject() != null);
			Object actionObject = actionScanner.scan();
			assertTrue(actionObject != null);
			
			XACMLPolicyScanner scanner = new XACMLPolicyScanner(configPolicyPathValue, callback);
			assertTrue(scanner.getPolicyObject() != null);
			Object object = scanner.scan();
			assertTrue(object != null);
			String id = XACMLPolicyScanner.getID(scanner.getPolicyObject());
			assertTrue(id.equals("urn:com:xacml:policy:id:0b67998b-57e2-4e25-9ea9-f9154bf18df1"));
			String version = XACMLPolicyScanner.getVersion(scanner.getPolicyObject());
			assertTrue(version.equals("1"));
			String versionFromPath = XACMLPolicyScanner.getVersion(configPolicyPathValue);
			assertTrue(versionFromPath.equals("1"));
			List<String> returnValue = XACMLPolicyScanner.getCreatedByModifiedBy(configPolicyPathValue);
			assertTrue(returnValue.get(0).equals("test"));
			String createdBy = XACMLPolicyScanner.getCreatedBy(configPolicyPathValue);
			assertTrue(createdBy.equals("test"));
			String modifiedBy = XACMLPolicyScanner.getModifiedBy(configPolicyPathValue);
			assertTrue(modifiedBy.equals("test"));
		}catch(Exception e){
			fail();
			logger.error("Exception Occured"+e);
		}
	}
}
