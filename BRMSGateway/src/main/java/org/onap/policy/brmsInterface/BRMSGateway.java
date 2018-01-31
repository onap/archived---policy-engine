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

package org.onap.policy.brmsInterface;

import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;

/**
 * BRMSGateway: This application acts as the Gateway interface between the PDP XACML and PDP Drools. The listens for
 * BRMS based policies and pushes them to the specified Policy Repository, from where the PDP Drools reads the Rule Jar.
 *
 * @version 0.1
 */
class BRMSGateway {

    private static final Logger logger = FlexLogger.getLogger(BRMSGateway.class);
    private static final String CONFIGFILE = "config.properties";

    private static PolicyEngine policyEngine = null;

    private BRMSGateway() {
    	// Default private constructor
    }

    public static void main(String[] args) throws Exception {
        // Initialize Handler.
        logger.info("Initializing BRMS Handler");
        BRMSHandler bRMSHandler = null;
        try {
            bRMSHandler = new BRMSHandler(CONFIGFILE);
        } catch (PolicyException e) {
            logger.error("Check your property file: " + e.getMessage(), e);
            System.exit(1);
        }

        // Set Handler with Auto Notification and initialize policyEngine
        try {
            logger.info("Initializing policyEngine with Auto Notifications");
            policyEngine = new PolicyEngine(CONFIGFILE, NotificationScheme.AUTO_ALL_NOTIFICATIONS, bRMSHandler);
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_UNKNOWN + "Error while Initializing Policy Engine " + e.getMessage(),
                    e);
        }

        // Keep Running....
        Runnable runnable = () -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Thread Exception " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static PolicyEngine getPolicyEngine() {
        return policyEngine;
    }
}
