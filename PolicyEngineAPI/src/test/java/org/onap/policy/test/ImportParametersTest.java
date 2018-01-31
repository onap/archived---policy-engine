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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.ImportParameters;

/**
 * The class <code>ImportParametersTest</code> contains tests for the class <code>{@link ImportParameters}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:40 PM
 * @version $Revision: 1.0 $
 */
public class ImportParametersTest {
	/**
	 * Run the String getDescription() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetDescription_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");

		String result = fixture.getDescription();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the String getFilePath() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetFilePath_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");

		String result = fixture.getFilePath();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the UUID getRequestID() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetRequestID_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.fromString("731dca0a-fe99-456c-8ad2-87cff8437b2f"));
		fixture.setDescription("");
		fixture.setServiceName("");

		UUID result = fixture.getRequestID();

		// add additional test code here
		assertNotNull(result);
		assertEquals("731dca0a-fe99-456c-8ad2-87cff8437b2f", result.toString());
		assertEquals(4, result.version());
		assertEquals(2, result.variant());
		assertEquals(-8443537024073106641L, result.getLeastSignificantBits());
		assertEquals(8295008237256263020L, result.getMostSignificantBits());
	}

	/**
	 * Run the String getServiceName() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetServiceName_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");

		String result = fixture.getServiceName();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the ImportParameters.IMPORT_TYPE getServiceType() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetServiceType_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");

		ImportParameters.IMPORT_TYPE result = fixture.getServiceType();

		// add additional test code here
		assertNotNull(result);
		assertEquals("MICROSERVICE", result.name());
		assertEquals("MICROSERVICE", result.toString());
		assertEquals(0, result.ordinal());
	}

	/**
	 * Run the String getVersion() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testGetVersion_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");

		String result = fixture.getVersion();

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the void setDescription(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetDescription_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		String description = "";

		fixture.setDescription(description);

		// add additional test code here
	}

	/**
	 * Run the void setFilePath(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetFilePath_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		String filePath = "";

		fixture.setFilePath(filePath);

		// add additional test code here
	}

	/**
	 * Run the void setImportParameters(String,String,UUID,String,IMPORT_TYPE,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetImportParameters_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		String serviceName = "";
		String description = "";
		UUID requestID = UUID.randomUUID();
		String filePath = "";
		ImportParameters.IMPORT_TYPE importType = ImportParameters.IMPORT_TYPE.MICROSERVICE;
		String version = "";

		fixture.setImportParameters(serviceName, description, requestID, filePath, importType, version);

		// add additional test code here
	}

	/**
	 * Run the void setRequestID(UUID) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetRequestID_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		UUID requestID = UUID.randomUUID();

		fixture.setRequestID(requestID);

		// add additional test code here
	}

	/**
	 * Run the void setServiceName(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetServiceName_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		String serviceName = "";

		fixture.setServiceName(serviceName);

		// add additional test code here
	}

	/**
	 * Run the void setServiceType(IMPORT_TYPE) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetServiceType_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		ImportParameters.IMPORT_TYPE enumImportType = ImportParameters.IMPORT_TYPE.MICROSERVICE;

		fixture.setServiceType(enumImportType);

		// add additional test code here
	}

	/**
	 * Run the void setVersion(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Test
	public void testSetVersion_1()
		throws Exception {
		ImportParameters fixture = new ImportParameters();
		fixture.setFilePath("");
		fixture.setVersion("");
		fixture.setServiceType(ImportParameters.IMPORT_TYPE.MICROSERVICE);
		fixture.setRequestID(UUID.randomUUID());
		fixture.setDescription("");
		fixture.setServiceName("");
		String version = "";

		fixture.setVersion(version);

		// add additional test code here
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 6/1/16 1:40 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ImportParametersTest.class);
	}
}
