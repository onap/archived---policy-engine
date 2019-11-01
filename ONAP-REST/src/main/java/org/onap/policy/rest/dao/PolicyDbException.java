/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.dao;

/**
 * PolicyDBException extends <code>Exception</code> to implement exceptions thrown by Policy Database transactions.
 *
 * @version 0.1
 */
public class PolicyDbException extends Exception {
    private static final long serialVersionUID = -6162444281003852781L;

    public PolicyDbException() {
        // Empty constructor
    }

    public PolicyDbException(String message) {
        super(message);
    }

    public PolicyDbException(Throwable cause) {
        super(cause);
    }

    public PolicyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolicyDbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
