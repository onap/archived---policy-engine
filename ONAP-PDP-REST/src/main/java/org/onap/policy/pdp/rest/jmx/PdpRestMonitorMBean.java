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

import java.util.Map;

public interface PdpRestMonitorMBean {
	public long getPdpEvaluationAttempts();
	public long getPdpEvaluationSuccesses();
	public long getLastDecisionLatency();
	public long getPdpEngineDecisionMinLatency();
	public long getPdpEngineDecisionMaxLatency();
	public long getPdpEngineDecisionAvgLatency();
	public Integer getpolicyCount(String policyID);

	public void resetLatency();
	public void resetCounters();
	public long getpdpEvaluationNA();
	public long getPdpEvaluationPermit();
	public long getPdpEvaluationDeny();
	public Map<String, Integer> getpolicyMap();
}
