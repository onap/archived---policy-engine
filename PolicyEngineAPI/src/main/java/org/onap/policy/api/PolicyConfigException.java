/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.api;

/**
 * PolicyConfigException extends <code>Exception</code> to implement exceptions thrown by {@link org.onap.policy.api.PolicyEngine}
 *
 * @version 0.1
 */
public class PolicyConfigException extends Exception{
	private static final long serialVersionUID = -188355220060684215L;

	public PolicyConfigException() {
	}

	public PolicyConfigException(String message) {
		super(message);
	}

	public PolicyConfigException(Throwable cause){
		super(cause);
	}

	public PolicyConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public PolicyConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
