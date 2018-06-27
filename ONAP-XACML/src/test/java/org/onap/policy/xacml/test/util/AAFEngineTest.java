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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Properties;
import org.junit.Test;
import org.onap.policy.xacml.std.pip.engines.aaf.AAFEngine;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.api.pip.PIPResponse;
import com.att.research.xacml.std.pip.StdPIPFinderFactory;
import com.att.research.xacml.std.pip.StdPIPRequest;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacml.api.XACML3;

public class AAFEngineTest {
	@Test
	public void aafEngineTest(){
		String testId = "testId";
		System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "xacml.properties");
		AAFEngine aafEngine = new AAFEngine();
		assertTrue(AAFEngine.DEFAULT_DESCRIPTION.equals("PIP for authenticating aaf attributes using the AAF REST interface"));
		assertTrue(AAFEngine.DEFAULT_ISSUER.equals("aaf"));
		
		Properties props = new Properties();
		try {
			aafEngine.configure(testId, props);
			assertEquals(aafEngine.getName(), testId);
			assertEquals(aafEngine.getDescription(), AAFEngine.DEFAULT_DESCRIPTION);
			assertEquals(aafEngine.getIssuer(), AAFEngine.DEFAULT_ISSUER);
			assertEquals(aafEngine.attributesProvided().size(), 2);
			assertEquals(aafEngine.attributesRequired().size(), 5);
			
			PIPRequest pipRequest = new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, AAFEngine.AAF_RESPONSE_ID, XACML3.ID_DATATYPE_STRING);
			StdPIPFinderFactory pipFactory = new StdPIPFinderFactory();
			PIPFinder pipFinder = pipFactory.getFinder();
			assertEquals(pipFinder.getPIPEngines().size(), 0);
			PIPResponse pipResponse = aafEngine.getAttributes(pipRequest, pipFinder);
			assertEquals(pipResponse.getStatus().isOk(), true);
		}
		catch (Exception ex) {
			fail("Not expecting any exceptions");
		}
	}
}
