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

package org.onap.policy.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(PolicyEngineTest.class);
		for(Failure failure: result.getFailures()) {
			System.out.println("Failed Test: " + failure.toString());
		}
		Result results = null;
		if(result.wasSuccessful()) {
			System.out.println("API Methods are being Tested.. ");
			results = JUnitCore.runClasses(GetConfigByPolicyNameTest.class, GetConfigStringTest.class,GetConfigStringStringTest.class,GetConfigStringStringMapTest.class,SendEventTest.class);
			for(Failure failure: results.getFailures()) {
				System.out.println("Failed Test: " + failure.toString());
			}
			System.out.println("Test Results.. ");
			System.out.println("Stats:  \nRun Time: " + (results.getRunTime()+result.getRunTime()) + "\nTotal Tests:" + results.getRunCount()+ result.getRunCount()
					+ "\nFailures: " + results.getFailureCount()+ result.getFailureCount());
			System.exit(1);
		}
		System.out.println("Test Failed..");
		System.out.println("Stats:  \nRun Time: " + result.getRunTime() + "\nTests:" + result.getRunCount()
				+ "\nFailures: " + result.getFailureCount());
	}
}
