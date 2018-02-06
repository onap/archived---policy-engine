/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.xacml.test.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.policy.xacml.util.MetricsUtil;

public class MetricsUtilTest {
	
	@Test
	public void metricsUtilTest(){
		MetricsUtil.AvgLatency avgLatency = new MetricsUtil.AvgLatency();
		avgLatency.compute(0);
		assertTrue(avgLatency.avg() == 0);
		avgLatency.compute(2);
		assertTrue(avgLatency.avg() == 1);
		avgLatency.reset();
		assertTrue(avgLatency.avg() == 0);
		
		MetricsUtil.MaxLatency maxLatency = new MetricsUtil.MaxLatency();
		maxLatency.compute(2);
		assertTrue(maxLatency.max() == 2);
		maxLatency.reset();
		assertTrue(maxLatency.max() < 0);
		
		MetricsUtil.MinLatency minLatency = new MetricsUtil.MinLatency();
		minLatency.compute(2);
		assertTrue(minLatency.min() == 2);
		minLatency.reset();
		assertTrue(minLatency.min() > 0);
	}

}
