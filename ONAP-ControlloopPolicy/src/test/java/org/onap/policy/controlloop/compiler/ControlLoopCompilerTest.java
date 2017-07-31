/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.controlloop.compiler;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ControlLoopCompilerTest {

	@Test 
	public void testTest() {
		try {
			this.test("src/test/resources/v1.0.0/test.yaml");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test 
	public void testBad1() {
		try {
			this.test("src/test/resources/v1.0.0/bad_trigger_1.yaml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test 
	public void testBad2() {
		try {
			this.test("src/test/resources/v1.0.0/bad_trigger_2.yaml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testBad() {
		try {
			this.test("src/test/resources/v1.0.0/bad_policies_1.yaml");
		} catch (Exception e) {
		}
	}
	
	public void test(String testFile) throws Exception {
		try (InputStream is = new FileInputStream(new File(testFile))) {
			ControlLoopCompiler.compile(is, null);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			throw e;
		}
	}

}
