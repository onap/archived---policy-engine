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

package org.onap.policy.pdp.rest.restAuth;

import java.util.Base64;
import java.util.StringTokenizer;

import org.onap.policy.rest.XACMLRestProperties;

import com.att.research.xacml.util.XACMLProperties;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;

public class AuthenticationService {
	private String pdpID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_USERID);
	private String pdpPass = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_PASS);

	public boolean authenticate(String authCredentials) {

		if (null == authCredentials)
			return false;
		// header value format will be "Basic encodedstring" for Basic authentication.
		final String encodedUserPassword = authCredentials.replaceFirst("Basic"	+ " ", "");
		String usernameAndPassword = null;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
		} catch (Exception e) {
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
			return false;
		}
		try {
			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();
			return pdpID.equals(username) && pdpPass.equals(password);
		}catch (Exception e){
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
			return false;
		}
	}

}
