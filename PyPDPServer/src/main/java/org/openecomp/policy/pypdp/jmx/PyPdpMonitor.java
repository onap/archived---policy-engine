/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.pypdp.jmx;

import java.util.concurrent.atomic.AtomicLong;

public class PyPdpMonitor implements PyPdpMonitorMBean {
	
	public static PyPdpMonitor singleton = new PyPdpMonitor();
	
	private final AtomicLong configCounter;
	private final AtomicLong eventCounter;
	private final AtomicLong configPolicyNameCounter;	
	
	private PyPdpMonitor() {
		this.configCounter = new AtomicLong();
		this.eventCounter = new AtomicLong();
		this.configPolicyNameCounter = new AtomicLong();
	}
	
	/**
	 * @return the configCounter
	 */
	public AtomicLong getAtomicConfigCounter() {
		return configCounter;
	}
	
	/**
	 * @return the eventCounter
	 */
	public AtomicLong getAtomicEventCounter() {
		return eventCounter;
	}
	
	/**
	 * @return the configPolicyNameCounter
	 */
	public AtomicLong getAtomicConfigPolicyNameCounter() {
		return configPolicyNameCounter;
	}
	/**
	 * @return the configCounter
	 */
	@Override
	public long getConfigCounter() {
		return configCounter.longValue();
	}
	
	/**
	 * @return the eventCounter
	 */
	@Override
	public long getEventCounter() {
		return eventCounter.longValue();
	}
	
	/**
	 * @return the configPolicyNameCounter
	 */
	@Override
	public long getConfigPolicyNameCounter() {
		return configPolicyNameCounter.longValue();
	}
	
	@Override
	public synchronized void resetCounters() {
		this.configCounter.set(0);
		this.eventCounter.set(0);
		this.configPolicyNameCounter.set(0);
	}

}
