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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.onap.policy.xacml.util.MetricsUtil.AvgLatency;
import org.onap.policy.xacml.util.MetricsUtil.MaxLatency;
import org.onap.policy.xacml.util.MetricsUtil.MinLatency;

public class PdpRestMonitor implements PdpRestMonitorMBean {
	private static PdpRestMonitor singleton = new PdpRestMonitor();

	private final AtomicLong pdpEvaluationAttempts = new AtomicLong();
	private final AtomicLong pdpEvaluationSuccesses = new AtomicLong();
	private final AtomicLong pdpEvaluationNA = new AtomicLong();
	private final AtomicLong pdpEvaluationPermit = new AtomicLong();
	private final AtomicLong pdpEvaluationDeny = new AtomicLong();
	private final Map<String, Integer> policyCount = new HashMap<>();

	private final MinLatency pdpEngineDecisionMinLatency = new MinLatency();
	private final MaxLatency pdpEngineDecisionMaxLatency = new MaxLatency();
	private final AvgLatency pdpEngineDecisionAvgLatency =  new AvgLatency();

	private volatile long lastDecisionLatency = 0;

	@Override
	public long getPdpEvaluationAttempts() {
		return pdpEvaluationAttempts.longValue();
	}
	@Override
	public long getPdpEvaluationPermit() {
		return pdpEvaluationPermit.longValue();
	}
	@Override
	public long getPdpEvaluationDeny() {
		return pdpEvaluationDeny.longValue();
	}
	@Override
	public long getPdpEvaluationSuccesses() {
		return pdpEvaluationSuccesses.longValue();
	}

	@Override
	public long getpdpEvaluationNA() {
		return pdpEvaluationNA.longValue();
	}
	@Override
	public long getLastDecisionLatency() {
		return lastDecisionLatency;
	}

	/**
	 * @return the pdpEngineDecisionMinLatency
	 */
	@Override
	public long getPdpEngineDecisionMinLatency() {
		return pdpEngineDecisionMinLatency.min();
	}

	/**
	 * @return the pdpEngineDecisionMaxLatency
	 */
	@Override
	public long getPdpEngineDecisionMaxLatency() {
		return pdpEngineDecisionMaxLatency.max();
	}

	/**
	 * @return the pdpEngineDecisionAvgLatency
	 */
	@Override
	public long getPdpEngineDecisionAvgLatency() {
		return pdpEngineDecisionAvgLatency.avg();
	}

	@Override
	public synchronized void resetLatency() {
		this.lastDecisionLatency = 0;
		this.pdpEngineDecisionMinLatency.reset();
		this.pdpEngineDecisionMaxLatency.reset();
		this.pdpEngineDecisionAvgLatency.reset();
	}

	@Override
	public synchronized void resetCounters() {
		this.pdpEvaluationAttempts.set(0);
		this.pdpEvaluationSuccesses.set(0);
		this.pdpEvaluationNA.set(0);
		this.policyCount.clear();
	}

	public void pdpEvaluationAttempts() {
		pdpEvaluationAttempts.incrementAndGet();
	}

	public void pdpEvaluationSuccess() {
		pdpEvaluationSuccesses.incrementAndGet();
	}

	public void pdpEvaluationNA(){
		pdpEvaluationNA.incrementAndGet();
	}
	public void pdpEvaluationPermit(){
		pdpEvaluationPermit.incrementAndGet();
	}
	public void pdpEvaluationDeny(){
		pdpEvaluationDeny.incrementAndGet();
	}

	public synchronized void computeLatency(long latency) {
		this.lastDecisionLatency = latency;
		this.pdpEngineDecisionMinLatency.compute(latency);
		this.pdpEngineDecisionMaxLatency.compute(latency);
		this.pdpEngineDecisionAvgLatency.compute(latency);
	}

	public void policyCountAdd(String policyID, Integer count){
		int countValue = count;
		if (policyCount.containsKey(policyID)){
			countValue = countValue + policyCount.get(policyID);
		}
		policyCount.put(policyID, countValue);
	}
	public Map<String, Integer> getpolicyMap() {
		return policyCount;
	}
	public Integer getpolicyCount(String policyID) {
		if (policyCount.containsKey(policyID)){
			return policyCount.get(policyID);
		}
		return null;
	}

	public static PdpRestMonitor getSingleton() {
		return singleton;
	}
	public static void setSingleton(PdpRestMonitor singleton) {
		PdpRestMonitor.singleton = singleton;
	}

}
