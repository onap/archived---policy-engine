/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
package org.onap.policy.xacml.util;

public class MetricsUtil {

	public static class AvgLatency {
		private long cumLatency = 0;
		private long count = 0;

		public void compute(long latency) {
			cumLatency += latency;
			count++;
		}

		public long avg() {
			if (count == 0)
				return 0;

			return (cumLatency / count);
		}

		public void reset() {
			cumLatency = 0;
			count = 0;
		}
	}

	public static class MinLatency {
		private long min = Long.MAX_VALUE;

		public synchronized void compute(long ts) {
			if (ts < min)
				min = ts;
		}

		public long min() {
			return min;
		}

		public void reset() {
			min = Long.MAX_VALUE;
		}
	}

	public static class MaxLatency {
		private long max = Long.MIN_VALUE;

		public synchronized void compute(long ts) {
			if (ts > max)
				max = ts;
		}

		public long max() {
			return max;
		}

		public void reset() {
			max = Long.MIN_VALUE;
		}
	}

}
