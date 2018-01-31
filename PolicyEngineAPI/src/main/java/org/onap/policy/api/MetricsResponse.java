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

public interface MetricsResponse {

	/**
	 * Gets the <code>String</code> of the metrics message from <code>MetricsResponse</code>.
	 *
	 * @return the <code>String</code> which consists of the metrics message from <code>MetricsResponse</code>
	 */
	public String getResponseMessage();

	/**
	 * Gets the response code of type <code>Integer</code> which corresponds to the HTTP Response code explaining the response from Policy Engine.
	 *
	 * @return the responseCode in <code>Integer</code> format corresponding to the HTTP response code from Policy Engine.
	 */
	public int getResponseCode();


	/**
	 * Gets the <code>Integer</code> value of the count of policies that reside on the PAP.
	 *
	 * @return the <code>Integer</code> which consists of count of policies that reside on the PAP.
	 */
	public int getPapMetrics();


	/**
	 * Gets the <code>Integer</code> value of the count of policies that reside on the PDP.
	 *
	 * @return the <code>Integer</code> which consists of count of policies that reside on the PDP.
	 */
	public int getPdpMetrics();


	/**
	 * Gets the <code>Integer</code> value of the total count of policies.
	 *
	 * @return the <code>Integer</code> which consists of the total count of policies.
	 */
	public int getMetricsTotal();



}
