/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.UpdateType;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.utils.XMLErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PolicyUtilsTest {
	
	private static final String ERROR = "The Value in Required Field will allow only '{0-9}, {a-z}, {A-Z}, _' following set of Combinations";
	private static final String SUCCESS = "success";
	
	@Test
	public void testJsonConversions() throws Exception{
		StdPDPNotification notification = new StdPDPNotification();
		notification.setNotificationType(NotificationType.BOTH);
		Collection<StdRemovedPolicy> removedPolicies = new ArrayList<>();
		Collection<StdLoadedPolicy> loadedPolicies = new ArrayList<>();
		StdRemovedPolicy removedPolicy = new StdRemovedPolicy();
		StdLoadedPolicy updatedPolicy = new StdLoadedPolicy();
		removedPolicy.setPolicyName("Test");
		removedPolicy.setVersionNo("1");
		removedPolicies.add(removedPolicy);
		updatedPolicy.setPolicyName("Testing");
		updatedPolicy.setVersionNo("1");
		updatedPolicy.setUpdateType(UpdateType.NEW);
		Map<String, String> matches = new HashMap<>();
		matches.put("key", "value");
		updatedPolicy.setMatches(matches);
		loadedPolicies.add(updatedPolicy);
		notification.setRemovedPolicies(removedPolicies);
		notification.setLoadedPolicies(loadedPolicies);
		
		String json = PolicyUtils.objectToJsonString(notification);
		PDPNotification getBackObject = PolicyUtils.jsonStringToObject(json, StdPDPNotification.class);
		assertEquals(0,getBackObject.getNotificationType().compareTo(notification.getNotificationType()));
		
	}
	
	private String encodedValue(String input){
		return new String(Base64.getEncoder().encode(input.getBytes()));
	}
	
	@Test
	public void testDecode() throws Exception{
		String value = "test";
		assertEquals(value, PolicyUtils.decode(encodedValue(value)));
		assertNull(PolicyUtils.decode(null));
		assertNull(PolicyUtils.decode(""));
	}
	
	@Test
	public void testBasicEncoding() throws Exception{
		String userName = "test";
		String key = "pass";
		String[] decodedValue = PolicyUtils.decodeBasicEncoding("Basic "+encodedValue(userName+":"+key));
		assertEquals(userName,decodedValue[0]);
		assertEquals(key,decodedValue[1]);
		assertEquals(0, PolicyUtils.decodeBasicEncoding(encodedValue(userName+":"+key)).length);
		assertEquals(0, PolicyUtils.decodeBasicEncoding(null).length);
	}
	
	@Test
	public void testSpecialCharValidator(){
		assertEquals(ERROR, PolicyUtils.policySpecialCharValidator("$TEST_"));
		assertEquals(ERROR, PolicyUtils.policySpecialCharValidator("$TEST _"));
		assertEquals(ERROR, PolicyUtils.policySpecialCharValidator(""));
		assertEquals(ERROR, PolicyUtils.policySpecialCharValidator("TæST"));
		assertEquals(SUCCESS, PolicyUtils.policySpecialCharValidator("TEST"));
	}
	
	@Test
	public void testSpecialCharWithSpaceValidator(){
		assertEquals(ERROR, PolicyUtils.policySpecialCharWithSpaceValidator(""));
		assertEquals(ERROR, PolicyUtils.policySpecialCharWithSpaceValidator("$TEST _"));
		assertEquals(SUCCESS, PolicyUtils.policySpecialCharWithSpaceValidator("TE ST"));
	}
	
	@Test
	public void testDescription() {
		assertEquals(SUCCESS, PolicyUtils.descriptionValidator("Test"));
		assertNotEquals(SUCCESS, PolicyUtils.descriptionValidator("@ModifiedBy:TesterB"));
		assertNotEquals(SUCCESS, PolicyUtils.descriptionValidator("@CreatedBy:TesterA"));
	}
	
	@Test
	public void testNonAscii(){
		assertTrue(PolicyUtils.containsNonAsciiEmptyChars(null));
		assertTrue(PolicyUtils.containsNonAsciiEmptyChars(""));
		assertTrue(PolicyUtils.containsNonAsciiEmptyChars("T æST"));
		assertTrue(PolicyUtils.containsNonAsciiEmptyChars("TæST"));
		assertFalse(PolicyUtils.containsNonAsciiEmptyChars("TEST"));
	}
	
	@Test
	public void testInteger(){
	    assertFalse(PolicyUtils.isInteger(null));
		assertTrue(PolicyUtils.isInteger("123"));
		assertFalse(PolicyUtils.isInteger("1a23"));
	}
	
	@Test
	public void testEmailAddress(){
		assertEquals(SUCCESS, PolicyUtils.validateEmailAddress("test@onap.org"));
		assertNotEquals(SUCCESS, PolicyUtils.validateEmailAddress("test@onap"));
	}
	
	@Test
	public void testBRMSValidate(){
	    String rule = "package com.sample;\n"
	            + "import com.sample.DroolsTest.Message;\n"
	            + "declare Params\n"
	            + "samPoll : int\n"
	            + "value : String\n"
	            + "end\n"
	            + "///This Rule will be generated by the UI.\n"
	            + "rule \"Create parameters structure\"\n"
	            + "salience 1000  \n"
	            + "when\n"
	            + "then\n"
	            + "Params params = new Params();\n"
	            + "params.setSamPoll(76);\n"
	            + "params.setValue(\"test\");\n"
	            + "insertLogical(params);\n"
	            + "end\n"
	            + "rule \"Rule 1: Check parameter structure access from when/then\"\n"
	            + "when\n"
	            + "$param: Params()\n"
	            + "Params($param.samPoll > 50)\n"
	            + "then\n"
	            + "System.out.println(\"Firing rule 1\");\n"
	            + "System.out.println($param);\n"
	            + "end\n";
	    assertEquals(PolicyUtils.brmsRawValidate(rule),"");
	    assertTrue(PolicyUtils.brmsRawValidate("error").contains("[ERR"));
        assertFalse(PolicyUtils.brmsRawValidate("package com.att.ecomp.policy.controlloop.p_${unique};").contains("[ERR"));
	}
	
	@Test
	public void testiIsJsonValid(){
	    assertTrue(PolicyUtils.isJSONValid("{\"test\":\"test\"}"));
	    String value = "{\"test\":\"test\", \"t1\": {\"test\": 12 , \"t2\":\"34\"},\"t2\":[{\"test\":\"val\"}]}";
	    assertTrue(PolicyUtils.isJSONValid(value));
	    assertFalse(PolicyUtils.isJSONValid("{\"test\":\"test"));
	}
	
	@Test
	public void testIsXMLValid() throws SAXException{
	    XMLErrorHandler error = new XMLErrorHandler();
        error.error(new SAXParseException(null, null));
        error.warning(new SAXParseException(null, null));
	    assertTrue(PolicyUtils.isXMLValid("<test>123</test>"));
	    assertFalse(PolicyUtils.isXMLValid("<test>123</test"));
	}
	
	@Test
	public void testIsPropValid(){
        assertTrue(PolicyUtils.isPropValid("test=123\n\tval=123"));
        assertFalse(PolicyUtils.isPropValid("test"));
        assertFalse(PolicyUtils.isPropValid("test="));
        assertTrue(PolicyUtils.isPropValid("#test\n\nval=123"));
	}
	
	@Test
	public void testVersionStringToArray(){
	    assertTrue(PolicyUtils.versionStringToArray(null).length==0);
	    assertTrue(PolicyUtils.versionStringToArray("").length==0);
	    assertTrue(PolicyUtils.versionStringToArray("1.2.3").length==3);
	}
}