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
package org.onap.policy.pdp.rest.api.services;

import java.util.UUID;

import javax.json.JsonException;

import org.json.JSONObject;
import org.onap.policy.api.MetricsRequestParameters;
import org.onap.policy.api.MetricsResponse;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.std.StdMetricsResponse;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.http.HttpStatus;

public class GetMetricsService {
	private static final Logger LOGGER = FlexLogger
			.getLogger(GetMetricsService.class.getName());

	private MetricsResponse response = null;
	private HttpStatus status = HttpStatus.BAD_REQUEST;
	private MetricsRequestParameters metricsParameters = null;

	public GetMetricsService(String requestID) {
		UUID requestUUID = null;
		if (requestID != null && !requestID.isEmpty()) {
			try {
				requestUUID = UUID.fromString(requestID);
			} catch (IllegalArgumentException e) {
				requestUUID = UUID.randomUUID();
				LOGGER.info("Generated Random UUID: " + requestUUID.toString(), e);
			}
		} else {
			requestUUID = UUID.randomUUID();
			LOGGER.info("Generated Random UUID: " + requestUUID.toString());
		}
		metricsParameters = new MetricsRequestParameters();
		this.metricsParameters.setRequestID(requestUUID);

		try {
			run();
			specialCheck();
		} catch (PolicyException e) {
			StdMetricsResponse metricsResponse = new StdMetricsResponse();
			metricsResponse
					.setResponseMessage(XACMLErrorConstants.ERROR_DATA_ISSUE
							+ e);
			this.response = metricsResponse;
			status = HttpStatus.BAD_REQUEST;
		}
	}

	private void specialCheck() {
		if (response != null && (response.getResponseMessage() != null
					&& response.getResponseMessage().contains("PE300"))) {
				status = HttpStatus.BAD_REQUEST;
		}
	}

	private void run() throws PolicyException {
		// Get Result.
		try {
			status = HttpStatus.OK;
			response = processResult();
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e);
			status = HttpStatus.BAD_REQUEST;
			throw new PolicyException(e);
		}
	}

	private MetricsResponse processResult() throws PolicyException {
		StdMetricsResponse metricsResponse = new StdMetricsResponse();
		PAPServices papServices = new PAPServices();
		String result = (String) papServices.callPAP(null, new String[] {
				"operation=get", "apiflag=getMetrics" },
				metricsParameters.getRequestID(), "metrics");

		JSONObject json = null;
		String message = null;
		if (result != null) {
			if (result.length() > 81 && result.contains("{")) {
				try {
					String responseMessage = result.substring(0, 82);
					String jsonString = result.substring(result.indexOf('{'),
							result.length());
					json = new JSONObject(jsonString);

					int papCount = (int) json.get("papCount");
					int pdpCount = (int) json.get("pdpCount");

					metricsResponse.setResponseCode(papServices
							.getResponseCode());
					metricsResponse.setResponseMessage(responseMessage);
					metricsResponse.setPapMetrics(papCount);
					metricsResponse.setPdpMetrics(pdpCount);

				} catch (JsonException | IllegalStateException e) {
					String jsonString = null;
					if(json != null){
						jsonString = json.toString();
					}
					message = XACMLErrorConstants.ERROR_DATA_ISSUE
							+ " improper JSON object : " + jsonString;
					LOGGER.error(message + e);
					metricsResponse.setResponseMessage(message);
					metricsResponse.setResponseCode(400);
					return metricsResponse;
				}
			} else {
				message = result;
				metricsResponse.setResponseCode(400);
				metricsResponse.setResponseMessage(message);
				return metricsResponse;
			}

		} else {
			message = XACMLErrorConstants.ERROR_SYSTEM_ERROR
					+ "There was an issue with connecting to the PAP, "
					+ "review the logs for further debugging.";
			metricsResponse.setResponseCode(500);
			metricsResponse.setResponseMessage(message);
			return metricsResponse;
		}

		return metricsResponse;
	}

	public MetricsResponse getResult() {
		return response;
	}

	public HttpStatus getResponseCode() {
		return status;
	}

}
