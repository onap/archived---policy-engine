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
package org.onap.policy.pdp.rest.impl;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;

import org.onap.policy.xacml.api.XACMLErrorConstants;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPFinderFactory;
import com.att.research.xacml.std.pip.finders.ConfigurableEngineFinder;
import com.att.research.xacml.util.XACMLProperties;

public class XACMLPdpPIPFinderFactory extends PIPFinderFactory {
	private ConfigurableEngineFinder pipFinder;
	
	private static Log LOGGER	= LogFactory.getLog(XACMLPdpPIPFinderFactory.class);
	
	public XACMLPdpPIPFinderFactory() {
	}

	public XACMLPdpPIPFinderFactory(Properties properties) {
	}

	@Override
	public PIPFinder getFinder() throws PIPException {
			synchronized(this) {
				if (pipFinder == null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Creating default configurable engine finder");
					}
					pipFinder = new ConfigurableEngineFinder();
					Properties xacmlProperties	= null;
					try {
						xacmlProperties	= XACMLProperties.getProperties();
					} catch (Exception ex) {
						LOGGER.error( XACMLErrorConstants.ERROR_SYSTEM_ERROR+ "Exception getting XACML properties: " + ex.getMessage(), ex);
						PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, ex, "Exception getting XACML properties");
						return null;
					}
					if (xacmlProperties != null) {
						((ConfigurableEngineFinder)pipFinder).configure(xacmlProperties);
					}
				}
			}
		return pipFinder;
	}

	@Override
	public PIPFinder getFinder(Properties properties) throws PIPException {
		synchronized(this) {
			if (pipFinder == null) {
				if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Creating configurable engine finder using: " + properties);
				}
				pipFinder = new ConfigurableEngineFinder();
				((ConfigurableEngineFinder)pipFinder).configure(properties);
			}
		}
		return this.pipFinder;
	}
}
