/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;
/**
 *
 *
 */

@WebListener
public class PdpRestMBeanListener implements ServletContextListener {
	private static final String JMX_OBJECT_NAME = "PdpRest:type=PdpRestMonitor";
	private static final Logger LOGGER	= FlexLogger.getLogger(PdpRestMBeanListener.class);

	private ObjectName objectName;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
        if (LOGGER.isInfoEnabled())
        	LOGGER.info("Registering.");

        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            objectName = new ObjectName(JMX_OBJECT_NAME);
            server.registerMBean(PdpRestMonitor.getSingleton(), objectName);
            LOGGER.info("MBean registered: " + objectName);
        } catch (Exception e) {

            LOGGER.warn(e.getMessage(), e);

            LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to Register " +e.getMessage(), e);

        }
	}
	// mark
	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Unregistering");
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            objectName = new ObjectName(JMX_OBJECT_NAME);
            server.unregisterMBean(objectName);
            if (LOGGER.isInfoEnabled())
            	LOGGER.info("MBean unregistered: " + objectName);
        } catch (Exception e) {

            LOGGER.warn(e.getMessage(), e);

            LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to Destroy Context" +e.getMessage(), e);

        }
	}

}

