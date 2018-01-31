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
 * <code>PolicyChangeResponse</code> defines the Policy Response
 *  which is Contains responseCode corresponding to HTTP response Codes with response Message.
 *
 * @version 0.1
 */
public interface PolicyChangeResponse {

	/**
	 * Policy Change Response Message in <code>String</code> format from the Policy Engine.
	 *
	 * @return the responseMessage in <code>String</code> format related to Response from Policy Engine.
	 */
	public String getResponseMessage();

	/**
	 * Response code of type <code>Integer</code> which corresponds to the HTTP Response code explaining the response from Policy Engine.
	 *
	 * @return the responseCode in <code>Integer</code> format corresponding to the HTTP response code from Policy Engine.
	 */
	public int getResponseCode();
}
