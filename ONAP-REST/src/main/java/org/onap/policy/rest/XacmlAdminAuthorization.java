/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.annotations.RequestParser;
import com.att.research.xacml.std.annotations.XACMLAction;
import com.att.research.xacml.std.annotations.XACMLRequest;
import com.att.research.xacml.std.annotations.XACMLResource;
import com.att.research.xacml.std.annotations.XACMLSubject;
import com.att.research.xacml.util.FactoryException;



public class XacmlAdminAuthorization {
	private static Log logger	= LogFactory.getLog(XacmlAdminAuthorization.class);

	private static UserInfo userId;
	public static UserInfo getUserId() {
		return userId;
	}

	public static void setUserId(UserInfo userId) {
		XacmlAdminAuthorization.userId = userId;
	}

	public enum AdminAction {
		ACTION_ACCESS("access"),
		ACTION_READ("read"),
		ACTION_WRITE("write"),
		ACTION_ADMIN("admin");

		String action;
		AdminAction(String a) {
			this.action = a;
		}
		@Override
		public String toString() {
			return this.action;
		}
	}

	public enum AdminResource {
		RESOURCE_APPLICATION("application"),
		RESOURCE_POLICY_WORKSPACE("workspace"),
		RESOURCE_POLICY_EDITOR("editor"),
		RESOURCE_DICTIONARIES("dictionaries"),
		RESOURCE_PDP_ADMIN("pdp_admin"),
		RESOURCE_PIP_ADMIN("pip_admin"),
		RESOURCE_SCOPES_SUPERADMIN("manage_scopes");

		String resource;
		AdminResource(String r) {
			this.resource = r;
		}
		@Override
		public String toString() {
			return this.resource;
		}
	}

	public enum Role {
		ROLE_GUEST("guest"),
		ROLE_ADMIN("admin"),
		ROLE_EDITOR("editor"),
		ROLE_SUPERGUEST("super-guest"),
		ROLE_SUPEREDITOR("super-editor"),
		ROLE_SUPERADMIN("super-admin");

		String userRole;

		Role(String a) {
			this.userRole = a;
		}
		@Override
		public String toString() {
			return this.userRole;
		}
	}

	@XACMLRequest(ReturnPolicyIdList=true)
	public class AuthorizationRequest {

		@XACMLSubject(includeInResults=true)
		String	userID;

		@XACMLAction()
		String	action;

		@XACMLResource()
		String	resource;

		public AuthorizationRequest(String userId, String action, String resource) {
			this.userID = userId;
			this.action = action;
			this.resource = resource;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getResource() {
			return resource;
		}

		public void setResource(String resource) {
			this.resource = resource;
		}
	}

	//
	// The PDP Engine
	//
	protected PDPEngine pdpEngine;

	public XacmlAdminAuthorization() {
		PDPEngineFactory pdpEngineFactory	= null;
		try {
			pdpEngineFactory	= PDPEngineFactory.newInstance();
			if (pdpEngineFactory == null) {
				logger.error("Failed to create PDP Engine Factory");
				PolicyLogger.error("Failed to create PDP Engine Factory");
			}
			this.pdpEngine = pdpEngineFactory.newEngine();
		} catch (FactoryException e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Exception create PDP Engine: " + e.getLocalizedMessage());
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XacmlAdminAuthorization", "Exception create PDP Engine");
		}
	}

	public boolean	isAuthorized(String userid, AdminAction action, AdminResource resource) {
		logger.info("authorize: " + userid + " to " + action + " with " + resource);
		if (this.pdpEngine == null) {
			logger.warn("no pdp engine available to authorize");
			return false;
		}
		Request request;
		try {
			request = RequestParser.parseRequest(new AuthorizationRequest(userid, action.toString(), resource.toString()));
		} catch (IllegalArgumentException | IllegalAccessException | DataTypeException e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to create request: " + e.getLocalizedMessage());
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XacmlAdminAuthorization", "Failed to create request");
			return false;
		}
		if (request == null) {
			logger.error("Failed to parse request.");
			PolicyLogger.error("Failed to parse request");
			return false;
		}
		logger.info("Request: " + request);
		//
		// Ask the engine
		//
		try {
			Response response = this.pdpEngine.decide(request);
			if (response == null) {
				logger.error("Null response from PDP decide");
				PolicyLogger.error("Null response from PDP decide");
			}
			//
			// Should only be one result
			//
			if(response != null){
				for (Result result : response.getResults()) {
					Decision decision = result.getDecision();
					logger.info("Decision: " + decision);
					if (decision.equals(Decision.PERMIT)) {
						return true;
					}
				}
			}
		} catch (PDPException e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "PDP Decide failed: " + e.getLocalizedMessage());
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XacmlAdminAuthorization", "PDP Decide failed");
		}
		return false;
	}
}
