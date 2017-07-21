/*-
 * ============LICENSE_START=======================================================
 * ECOMP-XACML
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
package org.openecomp.policy.xacml.std.pap;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import java.io.IOException;
import java.util.Properties;

import org.openecomp.policy.xacml.api.pap.ECOMPPapEngineFactory;
import org.openecomp.policy.xacml.api.pap.PAPPolicyEngine;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.FactoryException;

public class StdEngineFactory extends ECOMPPapEngineFactory {
	
	@Override
	public PAPPolicyEngine newEngine() throws FactoryException, PAPException {
		try {
			return (PAPPolicyEngine) new StdEngine();
		} catch (IOException e) {
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "StdEngineFactory", "Failed to create engine");
			return null;
		}
	}

	@Override
	public PAPPolicyEngine newEngine(Properties properties) throws FactoryException,
			PAPException {
		try {
			return (PAPPolicyEngine) new StdEngine(properties);
		} catch (IOException e) {
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "StdEngineFactory", "Failed to create engine");
			return null;
		}
	}
	
}
