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

package org.onap.policy.brms.api;

import org.onap.policy.api.NotificationScheme;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.api.XACMLErrorConstants;

/**
 * BRMSGateway: This application acts as the Gateway interface between the PDP XACML and PDP Drools.
 * The listens for BRMS based policies and pushes them to the specified Policy Repository, from
 * where the PDP Drools reads the Rule Jar.
 * 
 * @version 0.1
 */
public class BrmsGateway {

    private static final Logger logger = FlexLogger.getLogger(BrmsGateway.class);
    private static final String CONFIGFILE = "config.properties";

    private static PolicyEngine policyEngine = null;

    private BrmsGateway() {
        // Default private constructor
    }

    public static void main(final String[] args) throws Exception {
        // The configuration file containing the configuration for the BRMS Gateway
        String configFile = CONFIGFILE;

        // Check if a configuration file has been specified as a parameter 
        if (args.length == 1) {
            configFile = args[0];
        }
        else if (args.length > 1) {
            String errorString = "usage: " + BrmsGateway.class.getCanonicalName() + " [configFile]";
            logger.error(errorString);
            throw new PolicyException(errorString);
        }

        // Initialize Handler.
        logger.info("Initializing BRMS Handler");
        BrmsHandler brmsHandler = null;
        try {
            brmsHandler = new BrmsHandler(configFile);
        } catch (final PolicyException e) {
            String errorString = "Check your property file: " + e.getMessage();
            logger.error(errorString);
            throw new PolicyException(errorString);
        }

        // Set Handler with Auto Notification and initialize policyEngine
        try {
            logger.info("Initializing policyEngine with Auto Notifications");
            policyEngine = new PolicyEngine(CONFIGFILE, NotificationScheme.AUTO_ALL_NOTIFICATIONS, brmsHandler);
        } catch (final Exception e) {
            logger.error(XACMLErrorConstants.ERROR_UNKNOWN + "Error while Initializing Policy Engine " + e.getMessage(),
                            e);
        }

        // Keep Running....
        final Runnable runnable = () -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (final InterruptedException e) {
                    logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Thread Exception " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    public static PolicyEngine getPolicyEngine() {
        return policyEngine;
    }
}
