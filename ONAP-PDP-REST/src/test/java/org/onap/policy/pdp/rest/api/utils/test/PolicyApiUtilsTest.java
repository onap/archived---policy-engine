/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pdp.rest.api.utils.test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.json.JSONObject;
import org.junit.Test;
import org.onap.policy.pdp.rest.api.utils.PolicyApiUtils;

public class PolicyApiUtilsTest {

	@Test
	public void testValidateDirectoryJsonFields() {
		
		String dictonaryAction = "Action";

		
		JsonObject jsonObjAttrGood = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		
		JsonObject jsonObjAttrNull = Json.createObjectBuilder().add("attributeName", "").build();
		JsonObject jsonObjAttrBad = Json.createObjectBuilder().add("attributeName", "succ ess").build();
		JsonObject jsonObjectMissingAttrName = Json.createObjectBuilder().add("foo", "bar").build();
	
		
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGood, dictonaryAction).contains("success"));
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrNull, dictonaryAction).contains("No Attribute Name provided"));
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrBad, dictonaryAction).contains("Invalid Attribute Name"));
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjectMissingAttrName, dictonaryAction).contains("Missing attributeName"));
		
			/* "type" parameter variations. */
		JsonObject jsonObjAttrGoodTemp = jsonObjAttrGood;

			//null/empty type
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("No Type provided"));
		
			//missing type
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing type key in the dictionaryJson parameter"));
		
			//invalid type
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "INVALID")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Invalid Type value"));
		
			/* "method" parameter variations. */
		jsonObjAttrGoodTemp = jsonObjAttrGood;
		
			//null/empty method
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("No Method provided"));

			//missing method
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing method key in the dictionaryJson parameter"));		

			//valid method
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("success"));

		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "PUT")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("success"));
	
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "POST")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("success"));
	
		
			//invalid method
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "INVALID")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Invalid Method value"));
			
		
			/* url parameter variations */
		jsonObjAttrGoodTemp = jsonObjAttrGood;
		
			//null/empty url
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("No URL provided"));

			//missing url
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("body", "foobody")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing url key in the dictionaryJson parameter"));
			
			/* body parameter variations */
		jsonObjAttrGoodTemp = jsonObjAttrGood;
		
			//null body
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("No Body provided"));
		
			//missing body
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.build();
		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing body key in the dictionaryJson parameter"));
				
		
			/*headers parameter variations*/
		JsonObject jsonObjOption;
		JsonObject jsonObjNumber;
		JsonArray jsonArrayHeaders;

			//missing number headers		
		jsonObjOption = Json.createObjectBuilder().add("option","foobar").build();
		//jsonObjNumber = Json.createObjectBuilder().add("number","foobar").build();
		
		jsonArrayHeaders = Json.createArrayBuilder()
				.add(jsonObjOption)
				//.add(jsonObjNumber)
				.build();
		
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.add("headers", jsonArrayHeaders)
				.build();

		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing number key in the headers list of the dictionaryJson parameter."));
		
			//missing options headers		
		jsonObjOption = Json.createObjectBuilder().add("option","foobar").build();
		jsonObjNumber = Json.createObjectBuilder().add("number","foobar").build();
		
		jsonArrayHeaders = Json.createArrayBuilder()
				//.add(jsonObjOption)
				.add(jsonObjNumber)
				.build();
		
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.add("headers", jsonArrayHeaders)
				.build();

		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing option key in the headers list of the dictionaryJson parameter."));

			//null option
		jsonObjOption = Json.createObjectBuilder().add("option","").build();
		jsonObjNumber = Json.createObjectBuilder().add("number","foobar").build();
		
		jsonArrayHeaders = Json.createArrayBuilder()
				.add(jsonObjOption)
				.add(jsonObjNumber)
				.build();
		
		jsonObjAttrGoodTemp = Json.createObjectBuilder().add("attributeName", "success")
				.add("type", "REST")
				.add("method", "GET")
				.add("url", "http://foobar.com")
				.add("body", "foobody")
				.add("headers", jsonArrayHeaders)
				.build();

		assertTrue(PolicyApiUtils.validateDictionaryJsonFields(jsonObjAttrGoodTemp, dictonaryAction).contains("Missing required Option value"));
		
			//null number can't be tested
	
	}
	
	@Test
	public void testStringToJsonObject() {
		String jsonString = "{\"foo\":\"bar\"}";
		JsonObject obj = PolicyApiUtils.stringToJsonObject(jsonString);
		assertTrue(obj.get("foo").toString().equals("\"bar\""));
	}
	
	@Test
	public void testIsNumeric() {
		assertFalse(PolicyApiUtils.isNumeric("notNumeric"));
		assertTrue(PolicyApiUtils.isNumeric("2"));
	}
	
	
}
