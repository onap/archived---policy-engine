/*-
 * ============LICENSE_START=======================================================
 * ECOMP-XACML
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

package org.openecomp.policy.xacml.test.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.RequestReference;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONStructureException;
/**
 * Test JSON Request convert to object - Conformance tests
 * 
 * TO RUN - use jUnit
 * In Eclipse select this file or the enclosing directory, right-click and select Run As/JUnit Test
 * 
 * NOTE:
 * The "correct" way to verify that each JSON string gets translated into our internal Objects correctly is to look explicitly at each of the child objects
 * and verify that they are correct.  This would involve a lot of coding to get child of child of child and individually verify each property of each element.
 * To simplify testing we assume that request.toString() correctly includes a complete text representation of every sub-component of the Request object
 * and we compare the resulting String to our expected String.
 * This has two possible sources of error:
 * 	- toString might not include some sub-component, and
 * 	- the initial verification of the resulting string is done by hand and may have been incorrect.
 * 
 *
 */
public class RequestConformanceTest {
	
	// where to find the conformance test XML files
	private final String CONFORMANCE_DIRECTORY_PATH = "testsets/conformance/xacml3.0-ct-v.0.4";
	
	// The request object output from each test conversion from JSON string
	Request request;


	
	
	
	// test just one of each top-level element.
	// For simple elements also test for incorrect type
	@Test
	public void testConformanceRequests() {
		
		List<File> filesInDirectory = null;
		
		File conformanceDirectory = null;
		
		File currentFile = null;
		
		try {
			conformanceDirectory = new File(CONFORMANCE_DIRECTORY_PATH);
			filesInDirectory = getRequestsInDirectory(conformanceDirectory);
		} catch (Exception e) {
			fail("Unable to set up Conformance tests for dir '" + conformanceDirectory.getAbsolutePath()+"' e="+ e);
		}
		
		// run through each XML file
		//	- load the file from XML into an internal Request object
		//	- generate the JSON representation of that Request object
		//	- load that JSON representation into a new Request object
		//	- compare the 2 Request objects
		Request xmlRequest = null;
		Request jsonRequest = null;
		try {
			for (File f : filesInDirectory) {
				currentFile = f;

//// This is a simple way to select just one file for debugging - comment out when not being used
//if ( ! f.getName().equals("IIA023Request.xml")) {   continue;  }

// during debugging it is helpful to know what file it is starting to work on
//				System.out.println("starting file="+currentFile.getName());
				
				try {
					// load XML into a Request object
					xmlRequest = DOMRequest.load(f);
					xmlRequest.getStatus();
				} catch (Exception e) {
					// if XML does not load, just note it and continue with next file
					System.out.println("XML file did not load: '" + f.getName() + "  e=" + e);
					continue;
				}
				
//System.out.println(JSONRequest.toString(xmlRequest, false));

				// generate JSON from the Request
				String jsonString = JSONRequest.toString(xmlRequest, false);		
				
				// load JSON into a Request
				jsonRequest = JSONRequest.load(jsonString);
				
				// compare the two Request objects
				
				// check simple things first
				assertEquals("File '" + currentFile.getName() + "' CombinedDecision", xmlRequest.getCombinedDecision(), jsonRequest.getCombinedDecision());
				assertEquals("File '" + currentFile.getName() + "' getReturnPolicyIdList", xmlRequest.getReturnPolicyIdList(), jsonRequest.getReturnPolicyIdList());
				assertEquals("File '" + currentFile.getName() + "' requestDefaults", xmlRequest.getRequestDefaults(), jsonRequest.getRequestDefaults());

				// multiRequests (guaranteed to not be null)
				// We do NOT care about ordering, so compare the two collections inefficiently
				Collection<RequestReference> xmlCollection = xmlRequest.getMultiRequests();
				Collection<RequestReference> jsonCollection = jsonRequest.getMultiRequests();
				String errorMessage = null;
				if (jsonCollection.size() != xmlCollection.size()) {
					errorMessage = "File '" + currentFile.getName() + "' MultiRequests not same size.  ";
				} else if (! jsonCollection.containsAll(xmlCollection)) {
					errorMessage = "File '" + currentFile.getName() + "' MultiRequests have different contents.  ";
				}
				if (errorMessage != null) {
					String xmlContents = "";
					String jsonContents = "";
					Iterator<RequestReference> rrIt = xmlCollection.iterator();
					while (rrIt.hasNext()) {
						xmlContents += "\n   " + rrIt.next().toString(); 
					}
					rrIt = jsonCollection.iterator();
					while (rrIt.hasNext()) { 
						jsonContents += "\n  " + rrIt.next().toString(); 
					}
					fail(errorMessage + "\nXML(" + xmlCollection.size() + ")='" + xmlContents + 
							"'  \nJSON(" + jsonCollection.size() + ")='" + jsonContents +
							"'" +
							"\njson='" + jsonString + "'");
				}
				
				// attributes (guaranteed to not be null)
				// We do NOT care about ordering, so compare the two collections inefficiently
				Collection<RequestAttributes> xmlAttrCollection = xmlRequest.getRequestAttributes();
				Collection<RequestAttributes> jsonAttrCollection = jsonRequest.getRequestAttributes();
				errorMessage = null;
				if (jsonAttrCollection.size() != xmlAttrCollection.size()) {
					errorMessage = "File '" + currentFile.getName() + "' RequestAttributes not same size.  ";
				} else if (! jsonAttrCollection.containsAll(xmlAttrCollection)) {
					String attrName = "";
					Iterator<RequestAttributes> rait = xmlAttrCollection.iterator();
					while (rait.hasNext()) {
						RequestAttributes ra = rait.next();
						if (jsonAttrCollection.contains(ra) == false) {
							attrName = ra.toString();
						}
					}
					errorMessage = "File '" + currentFile.getName() + "' RequestAttributes have different contents.  JSON is missing attr=" + attrName;
				}
				if (errorMessage != null) {
					String xmlContents = "";
					String jsonContents = "";
					Iterator<RequestAttributes> rrIt = xmlAttrCollection.iterator();
					while (rrIt.hasNext()) {
						RequestAttributes ras = rrIt.next();
						xmlContents += "\n   " + ras.toString();
						if (ras.getContentRoot() != null) {
							StringWriter writer = new StringWriter();
							Transformer transformer = null;
							try {
								transformer = TransformerFactory.newInstance().newTransformer();
								transformer.transform(new DOMSource(ras.getContentRoot()), new StreamResult(writer));
							} catch (Exception e) {
								throw new JSONStructureException("Unable to Content node to string; e="+e);
							}

							xmlContents += "\n        Content: " + writer.toString();
						}
					}
					rrIt = jsonAttrCollection.iterator();
					while (rrIt.hasNext()) { 
						RequestAttributes ras = rrIt.next();
						jsonContents += "\n   " + ras.toString();	
						if (ras.getContentRoot() != null) {
							StringWriter writer = new StringWriter();
							Transformer transformer = null;
							try {
								transformer = TransformerFactory.newInstance().newTransformer();
								transformer.transform(new DOMSource(ras.getContentRoot()), new StreamResult(writer));
							} catch (Exception e) {
								throw new JSONStructureException("Unable to Content node to string; e="+e);
							}

							jsonContents += "\n        Content: " + writer.toString();
						}
					}
					fail(errorMessage + "\nXML(" + xmlAttrCollection.size() + ")='" + xmlContents + 
							"'  \nJSON(" + jsonAttrCollection.size() + ")='" + jsonContents +
							"\njson='" + jsonString + "'");
				}
				

			}			

		} catch (Exception e) {
			fail ("Failed test with '" + currentFile.getName() + "', e=" + e);
		}

		
	}
	
	//
	// HELPER to get list of all Request files in the given directory
	//
	
	private List<File> getRequestsInDirectory(File directory) {
		List<File> fileList = new ArrayList<File>();
		
		File[] fileArray = directory.listFiles();
		for (File f : fileArray) {
			if (f.isDirectory()) {
				List<File> subDirList = getRequestsInDirectory(f);
				fileList.addAll(subDirList);
			}
			if (f.getName().endsWith("Request.xml")) {
				fileList.add(f);
			}
		}
		return fileList;
		
	}
	
}