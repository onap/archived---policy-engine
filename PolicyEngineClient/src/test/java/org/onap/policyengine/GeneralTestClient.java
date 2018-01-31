/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineClient
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

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.onap.policyengine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyEngineException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * Class reads from .testCases file and run the test cases available in the file
 * and generates output for each test cases specifing whether is passed or fail
 * and reason why it fails.
 *
 *
 * @version 1.0
 *
 */
public class GeneralTestClient {

	private static final Logger LOGGER	= FlexLogger.getLogger(GeneralTestClient.class);

	static int totalTC = 0, passTC = 0, failTC = 0;

	public static void main(String[] args) {
		Path file;
		/* command line arguments */
		if (args.length != 0) {
			for (int index = 0; index < args.length; index++) {
				// System.out.println(args[index]);
				file = Paths.get(args[index]);
				runTestClientOnConfigFile(file);
			}
		} else {
			/* default file */
			file = Paths.get("input.testCases");
			runTestClientOnConfigFile(file);
		}
		System.out
		.println("###############################################################################################");
		System.out.println("\n\t SUMMARY: TOTAL: " + totalTC + ",\tPASS: "
				+ passTC + ",\tFAIL: " + failTC + "\n");
		System.out
		.println("###############################################################################################");

		System.out.println("Enter a any key to exit");
		try {
			System.in.read();
		} catch (IOException e) {
			//
		}

	}

	/**
	 * This function reads the files passed as arguments and runs the test cases
	 * in the file
	 *
	 * @param file
	 */
	private static void runTestClientOnConfigFile(Path file) {
		String resultReturned, onapComponentName;
		int totalTCforFile = 0, passTCforFile = 0, failTCforFile = 0;
		System.out
		.println("\n###############################################################################################");
		System.out.println("\tRuning test Client on Config file: " + file);
		System.out
		.println("###############################################################################################\n");

		if (Files.notExists(file)) {
			System.out.println("file doesnot exist");
			// throw new
			// PolicyEngineException("File doesn't exist in the specified Path "
			// + file.toString());
		} else if (file.toString().endsWith(".testCases")) {
			try {
				// read the json file
				FileReader reader = new FileReader(file.toString());

				JSONParser jsonParser = new JSONParser();
				JSONArray jsonObjectArray = (JSONArray) jsonParser
						.parse(reader);
				for (Object jsonObject : jsonObjectArray) {
					totalTC++;
					totalTCforFile++;
					ArrayList<String> expectedResult = new ArrayList<>();
					ArrayList<String> resultReceived = new ArrayList<>();
					JSONObject testCase = (JSONObject) jsonObject;
					// get a String from the JSON object
					long id = (long) testCase.get("id");
					String testFor = (String) testCase.get("testFor");
					String testCaseDescription = (String) testCase
							.get("testCaseDescription");
					JSONArray expectedResultJson = (JSONArray) testCase
							.get("expectedResult");
					@SuppressWarnings("rawtypes")
					Iterator i = expectedResultJson.iterator();
					while (i.hasNext()) {
						expectedResult.add((String) i.next());
					}
					String pdp_urlConfigFile = (String) testCase
							.get("PDP_URLConfigFile");
					// System.out.println(pdp_urlConfigFile);
					PolicyEngine policyEngine;
					try {
						policyEngine = new PolicyEngine(pdp_urlConfigFile);

						switch (testFor) {

						case "getConfig":
							onapComponentName = (String) testCase
							.get("ONAPName");
							String configName = (String) testCase
									.get("ConfigName");
							Map<String, String> configAttributes = new HashMap<>();
							configAttributes.put("key", "value");
							JSONArray configAttributesJSON = (JSONArray) testCase
									.get("configAttributes");
							if(configAttributesJSON!=null){
							i = configAttributesJSON.iterator();
							while (i.hasNext()) {
								JSONObject innerObj = (JSONObject) i.next();
								configAttributes.put(
										(String) innerObj.get("key"),
										(String) innerObj.get("value"));

							}
							}else{
								configAttributes = null;
							}
							resultReceived = PolicyEngineTestClient.getConfig(
									policyEngine, onapComponentName,
									configName, configAttributes);
							Collections.sort(expectedResult);
							Collections.sort(resultReceived);
							resultReturned = compareResults(expectedResult,resultReceived);
							if (resultReturned.equals("PASSED")) {
								printResult(id, testFor, testCaseDescription,
										"PASSED");
								passTCforFile++;
								passTC++;
							} else {
								printResult(id, testFor, testCaseDescription,
										"FAILED", resultReturned);
								failTCforFile++;
								failTC++;
							}
							break;

						case "getAction":
							Map<String, String> eventAttributes = new HashMap<>();
							eventAttributes.put("Key", "Value");
							JSONArray eventAttributesJSON = (JSONArray) testCase
									.get("eventAttributes");
							if(eventAttributesJSON != null){
							i = eventAttributesJSON.iterator();
							while (i.hasNext()) {
								JSONObject innerObj = (JSONObject) i.next();
								eventAttributes.put(
										(String) innerObj.get("key"),
										(String) innerObj.get("value"));
							}
							}else{
								eventAttributes=null;
							}
							resultReceived = PolicyEngineTestClient.getAction(
									policyEngine, eventAttributes);
							Collections.sort(expectedResult);
							Collections.sort(resultReceived);
							resultReturned = compareResults(expectedResult,
									resultReceived);
							if (resultReturned.equals("PASSED")) {
								printResult(id, testFor, testCaseDescription,
										"PASSED");
								passTCforFile++;
								passTC++;
							} else {
								printResult(id, testFor, testCaseDescription,
										"FAILED", resultReturned);
								failTCforFile++;
								failTC++;
							}
							break;

						case "getDecision":
							onapComponentName = (String) testCase
							.get("ONAPName");
							Map<String, String> decisionAttributes = new HashMap<>();
							decisionAttributes.put("Key", "Value");
							JSONArray decisionAttributesJSON = (JSONArray) testCase
									.get("decisionAttributes");
							i = decisionAttributesJSON.iterator();
							while (i.hasNext()) {
								JSONObject innerObj = (JSONObject) i.next();
								decisionAttributes.put(
										(String) innerObj.get("key"),
										(String) innerObj.get("value"));

							}

							resultReceived = PolicyEngineTestClient
									.getDecision(policyEngine,
											onapComponentName,
											decisionAttributes);
							Collections.sort(expectedResult);
							Collections.sort(resultReceived);
							resultReturned = compareResults(expectedResult,
									resultReceived);
							if (resultReturned.equals("PASSED")) {
								printResult(id, testFor, testCaseDescription,
										"PASSED");
								passTCforFile++;
								passTC++;
							} else {
								printResult(id, testFor, testCaseDescription,
										"FAILED", resultReturned);
								failTCforFile++;
								failTC++;
							}
							break;

							// case "getManualNotification":
							// PolicyEngineTestClient
							// .getManualNotifications(org.onap.policyEngine);
							// break;
							// case "getAutoNotification":
							// PolicyEngineTestClient
							// .getAutoNotifications(org.onap.policyEngine);
							// break;

						default:
							printResult(id, testFor, testCaseDescription,
									"FAILED", "\tINVAILD TEST CASE.");
							failTCforFile++;
							failTC++;
							break;

						}
					} catch (PolicyEngineException e) {
						printResult(id, testFor, testCaseDescription, "FAILED");
						failTCforFile++;
						failTC++;
						LOGGER.error("Exception Occured"+e);
					} catch (Exception e) {
						printResult(id, testFor, testCaseDescription, "FAILED");
						failTCforFile++;
						failTC++;
						LOGGER.error("Exception Occured"+e);
					}
				}

			} catch (FileNotFoundException ex) {
				LOGGER.error("Exception Occured due to File not found"+ex);
			} catch (IOException ex) {
				LOGGER.error("Exception Occured"+ex);
			} catch (NullPointerException ex) {
				LOGGER.error("Exception Occured due to Null Pointer"+ex);
			} catch (org.json.simple.parser.ParseException e) {
				LOGGER.error("Exception Occured while Parsing"+e);
			}
		}
		System.out.println("\n\n\t Summary for the file: TOTAL: "
				+ totalTCforFile + ",\tPASS: " + passTCforFile + ",\tFAIL: "
				+ failTCforFile + "\n");
	}

	/**
	 * This function prints the reason if test fails.
	 *
	 * @param id
	 * @param testFor
	 * @param testCaseDescription
	 * @param passFail
	 * @param resultReturned
	 */
	private static void printResult(long id, String testFor,
			String testCaseDescription, String passFail, String resultReturned) {
		// TODO Auto-generated method stub
		printResult(id, testFor, testCaseDescription, passFail);
		System.out.println(resultReturned);

	}

	/**
	 * This function prints in output in required format.
	 *
	 * @param id
	 * @param testFor
	 * @param testCaseDescription
	 * @param result
	 */
	private static void printResult(long id, String testFor,
			String testCaseDescription, String result) {
		System.out.println(result + " - Test Case " + id + " - Test type: "
				+ testFor + " - " + testCaseDescription);
	}

	/**
	 * This function compares the required expected output and received output
	 * and returns PASS if expected output and received output matches
	 *
	 * @param expectedResult
	 * @param resultReceived
	 * @return
	 */
	private static String compareResults(ArrayList<String> expectedResult,
			ArrayList<String> resultReceived) {
		// TODO Auto-generated method stub
		String returnString = "";
		int index;
//		System.out.println(expectedResult.size());
//		System.out.println(resultReceived.size());
		for (index = 0; index < expectedResult.size()
				|| index < resultReceived.size(); index++) {
			if (index < expectedResult.size() && index < resultReceived.size()) {
				if (!expectedResult.get(index)
						.equals(resultReceived.get(index))) {
					//returnString = "FAILED";
					returnString += "\tExpected Output: "
							+ expectedResult.get(index)
							+ ",\n\tOutput Received: "
							+ resultReceived.get(index)+"\n";
//
					//System.out.println(resultReceived.get(index));
				}

			} else {
				if (index >= expectedResult.size()) {
					returnString += "\tExpected Size of output: "
							+ expectedResult.size()
							+ ", Size of output received: "
							+ resultReceived.size()
							+ "\n\tExpected Output: none,\n\tOutput Received: "
							+ resultReceived.get(index)+"\n";

				} else {
					if (index >= resultReceived.size()) {
						returnString += "\tExpected Size of output: "
								+ expectedResult.size()
								+ ", Size of output received: "
								+ resultReceived.size()
								+ "\n\tExpected Output: "
								+ expectedResult.get(index)
								+ ",\n\tOutput Received: none\n";

					}
				}
			}

		}
		if(index==expectedResult.size() && index==resultReceived.size() && returnString.equals("")){
			returnString="PASSED";
		}
		return returnString;

	}
}
