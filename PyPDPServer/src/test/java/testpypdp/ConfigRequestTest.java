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

package testpypdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openecomp.policy.pypdp.model_pojo.PyPolicyConfig;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.policy.api.PolicyConfigStatus;
import org.openecomp.policy.api.PolicyType;
import org.openecomp.policy.std.StdPolicyConfig;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.openecomp.policy.pypdp.ConfigRequest;

public class ConfigRequestTest {

	private StdPolicyConfig config;
	private ConfigRequest request;

	@Before
	public void setUp() {
		request = new ConfigRequest(null);
		config = new StdPolicyConfig();
		config.setPolicyConfigStatus(PolicyConfigStatus.CONFIG_RETRIEVED);
	}

	@Test
	public void checkResponsePropertiesTest() {
		config.setPolicyType(PolicyType.PROPERTIES);
		Properties prop = new Properties();
		prop.put("Key", "value");
		config.setProperties(prop);
		PyPolicyConfig pConfig = request.checkResponse(config);
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("Key", "value");
		assertEquals(pConfig.getProperty(), result);
	}

	@Test
	public void checkResponseDocumentTest() throws Exception {
		config.setPolicyType(PolicyType.XML);
		String xmlString = "<test></test>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(
				xmlString)));
		config.setDocument(document);
		PyPolicyConfig pConfig = request.checkResponse(config);
		assertNotNull(pConfig.getConfig());
	}
}
