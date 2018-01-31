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

package org.onap.policy.std;

import org.onap.policy.api.MetricsResponse;

public class StdMetricsResponse implements MetricsResponse {
	private String responseMessage;
	private int pdpMetrics;
	private int papMetrics;
	private int metricsTotal;
	private int responseCode;

	@Override
	public String getResponseMessage() {
		return responseMessage;
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public int getPapMetrics() {
		return papMetrics;
	}

	@Override
	public int getPdpMetrics() {
		return pdpMetrics;
	}

	@Override
	public int getMetricsTotal() {
		metricsTotal = papMetrics + pdpMetrics;
		return metricsTotal;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public void setPdpMetrics(int pdpMetrics) {
		this.pdpMetrics = pdpMetrics;
	}

	public void setPapMetrics(int papMetrics) {
		this.papMetrics = papMetrics;
	}

	public void setMetricsTotal(int metricsTotal) {
		this.metricsTotal = metricsTotal;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

}