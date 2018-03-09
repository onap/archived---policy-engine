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

import static org.junit.Assert.assertNotNull;
import java.util.Properties;
import org.junit.Test;
import org.onap.policy.pdp.rest.impl.XACMLPdpPIPFinderFactory;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;

public class XACMLPdpPIPFinderFactoryTest {
	@Test
	public void testGets() throws PIPException {
		// Test constructors
		Properties props = new Properties();
		XACMLPdpPIPFinderFactory blankFactory = new XACMLPdpPIPFinderFactory();
		assertNotNull(blankFactory);
		XACMLPdpPIPFinderFactory factory = new XACMLPdpPIPFinderFactory(props);
		assertNotNull(factory);
		
		// Test get
		PIPFinder finder = factory.getFinder();
		assertNotNull(finder);
		finder = blankFactory.getFinder(props);
		assertNotNull(finder);
	}
}
