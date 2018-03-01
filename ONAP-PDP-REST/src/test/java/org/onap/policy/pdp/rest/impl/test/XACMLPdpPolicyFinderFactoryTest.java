/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.impl.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.onap.policy.pdp.rest.impl.XACMLPdpPolicyFinderFactory;

import com.att.research.xacml.util.XACMLProperties;

public class XACMLPdpPolicyFinderFactoryTest {
	private static Log LOGGER = LogFactory.getLog(XACMLPdpPolicyFinderFactoryTest.class);
	
	@Test
	public void testDefaultConstructor() throws IOException {
		
		LOGGER.info("XACMLPdpPolicyFinderFactoryTest - testDefaultConstructor");
		try {	
			XACMLProperties.reloadProperties();
			System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pdp.properties");
			XACMLProperties.getProperties();
			
			XACMLPdpPolicyFinderFactory finderFactory = new XACMLPdpPolicyFinderFactory();
			
			finderFactory.getPolicyFinder();
			
			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
			
		}
	}
	@Test
	public void testPropertiesConstructor () {
		
		LOGGER.info("XACMLPdpPolicyFinderFactoryTest - testPropertiesConstructor");
		try {	
			Properties properties = new Properties();
			FileInputStream input = new FileInputStream("src/test/resources/xacml.pdp.properties");
			properties.load(input);
			
			XACMLPdpPolicyFinderFactory finderFactory = new XACMLPdpPolicyFinderFactory(properties);
			
			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
			
		}
		
	}
}
