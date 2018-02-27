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
package org.onap.policy.pdp.rest.jmx;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.ServletContextEvent;

import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class PdpRestMBeanListenerTest {

	private static Logger LOGGER = FlexLogger.getLogger(PdpRestMBeanListenerTest.class);
	
	@Test
	public void test() {

		try {	
			PdpRestMBeanListener listener = new PdpRestMBeanListener();
			ServerContextImpl source = new ServerContextImpl();
			ServletContextEvent event = new ServletContextEvent(source);
			
			listener.contextInitialized(event);
			listener.contextDestroyed(event);
			
			assertTrue(true);
		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
			fail();
			
		}
		
	}

}
