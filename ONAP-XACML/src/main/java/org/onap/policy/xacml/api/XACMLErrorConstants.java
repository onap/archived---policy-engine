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
package org.onap.policy.xacml.api;

/**
 * List of Error Classifications
 *  PE100 - Permissions
 *  PE200 - System Error (such as availability, timeout, configuration, etc...)
 *  PE300 - Data Issue( such as request for REST/JSON )
 *  PE400 - Schema validation
 *  PE500 - Process Flow issues
 *  PE900 - Default/Unknown Errors
 *
 *
 */
public class XACMLErrorConstants {
	//Captures all the errors related to Authentication, Authorizations and Permissions in the PolicyEngine Process
	public static final String ERROR_PERMISSIONS = "PE100 - Permissions Error: ";

	//Captures all the errors related to availability, timeout configuration variables, etc... in the PolicyEngine
	public static final String ERROR_SYSTEM_ERROR = "PE200 - System Error: ";

	/*
	 * Captures all the errors related to configuration values from properties files and data from the interfacing System
	 * like REST/JSON values
	*/
	public static final String ERROR_DATA_ISSUE = "PE300 - Data Issue: ";

	//Captures all the errors related to the XML schemas and/or REST/JSON structures
	public static final String ERROR_SCHEMA_INVALID = "PE400 - Schema validation Error: ";

	//Captures all the errors related to the Process, when data from one Process to another Process does not flow
	public static final String ERROR_PROCESS_FLOW = "PE500 - Process Flow Issue: ";

	//Captures all the errors that not related to the list of above error codes
	public static final String ERROR_UNKNOWN = "PE900 - Unknown Error: ";


}
