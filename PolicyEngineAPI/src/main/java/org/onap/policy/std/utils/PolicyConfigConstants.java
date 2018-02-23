/*-
 * ============LICENSE_START=======================================================
 * PolicyConfigConstants
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.std.utils;

public final class PolicyConfigConstants {

    public static final String HTTP = "http";
    public static final String REGEX = "\\s*;\\s*";
    public static final String PDP_VALUE_REGEX = "\\s*,\\s*";
    public static final String COMMA = ",";
    public static final String SEMICOLLON = ";";

    public static final String PDP_URL_PROP_NAME = "PDP_URL";
    public static final String CLIENT_KEY_PROP_NAME = "CLIENT_KEY";
    public static final String CLIENT_ID_PROP_NAME = "CLIENT_ID";
    public static final String UEB_API_SECRET_PROP_NAME = "UEB_API_SECRET";
    public static final String UEB_API_KEY_PROP_NAME = "UEB_API_KEY";
    public static final String NOTIFICATION_TOPIC_PROP_NAME = "NOTIFICATION_TOPIC";
    public static final String NOTIFICATION_SERVERS_PROP_NAME = "NOTIFICATION_SERVERS";
    public static final String NOTIFICATION_TYPE_PROP_NAME = "NOTIFICATION_TYPE";
    public static final String ENVIRONMENT_PROP_NAME = "ENVIRONMENT";
    public static final String JUNIT_PROP_NAME = "JUNIT";

    public static final String UEB = "ueb";

    public static final String ERROR_AUTH_GET_PERM =
            "You are not allowed to Make this Request. Please contact PolicyAdmin to give access to: ";
    public static final String DEFAULT_NOTIFICATION = "websocket";
    public static final String ERROR_DATA_ISSUE = "Invalid Data is given.";
    public static final String DMAAP = "dmaap";
    public static final String ERROR_INVALID_PDPS = "Unable to get valid Response from  PDP(s) ";
    public static final String ERROR_WHILE_CONNECTING = "Error while connecting to ";

    public static final String TEST_POLICY_NAME = "Policy Name: listConfigTest";

    public static final String SEND_EVENT_RESOURCE_NAME = "sendEvent";
    public static final String GET_CONFIG_RESOURCE_NAME = "getConfig";
    public static final String GET_DECISION_RESOURCE_NAME = "getDecision";
    public static final String GET_METRICS_RESOURCE_NAME = "getMetrics";
    public static final String PUSH_POLICY_RESOURCE_NAME = "pushPolicy";
    public static final String DELETE_POLICY_RESOURCE_NAME = "deletePolicy";
    public static final String GET_DICTIONARY_ITEMS_RESOURCE_NAME = "getDictionaryItems";
    public static final String UPDATE_DICTIONARY_ITEM_RESOURCE_NAME = "updateDictionaryItem";
    public static final String CREATE_DICTIONARY_ITEM_RESOURCE_NAME = "createDictionaryItem";
    public static final String POLICY_ENGINE_IMPORT_RESOURCE_NAME = "policyEngineImport";
    public static final String UPDATE_POLICY_RESOURCE_NAME = "updatePolicy";
    public static final String CREATE_POLICY_RESOURCE_NAME = "createPolicy";


    public static final String PE300 = "PE300";
    
    public static final String BAD_REQUEST_STATUS_CODE = "400";
    public static final String UNAUTHORIZED_STATUS_CODE = "401";
    public static final String DATE_FORMAT = "dd-MM-yyyy";

    private PolicyConfigConstants() {}
}
