/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.brms.api;

import java.util.ArrayList;
import java.util.Collection;
import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.PolicyConfig;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.PolicyException;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.BackUpHandler;
import org.onap.policy.xacml.api.XACMLErrorConstants;

/**
 * BRMSHandler: Notification Handler which listens for PDP Notifications. Take action only for BRMS
 * policies.
 * 
 * @version 0.3
 */
public class BrmsHandler implements BackUpHandler {

    private static final Logger logger = FlexLogger.getLogger(BrmsHandler.class.getName());

    private BrmsPush brmsPush = null;

    public BrmsHandler(final String propertiesFile) throws PolicyException {
        setBrmsPush(new BrmsPush(propertiesFile, this));
    }

    public void setBrmsPush(final BrmsPush brmsPush) {
        this.brmsPush = brmsPush;
    }

    /*
     * This Method is executed upon notification by the Policy Engine API Notification.
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.utils.BackUpHandler#notificationReceived(org.onap.policy.api.PDPNotification)
     */
    @Override
    public void notificationReceived(final PDPNotification notification) {
        logger.info("Notification Recieved");
        logger.info(notification.getNotificationType().toString());
        final Boolean flag = BrmsPush.getBackUpMonitor().getFlag();
        brmsPush.initiate(flag);
        if (flag) {
            logger.info("Master Application performing on Notification ");
            logger.info("Removed Policies: \n");
            for (RemovedPolicy removedPolicy: notification.getRemovedPolicies()) {
                logger.info(removedPolicy.getPolicyName());
                logger.info(removedPolicy.getVersionNo());
            }
            logger.info("Updated Policies: \n");
            for (LoadedPolicy updatedPolicy: notification.getLoadedPolicies()) {
                logger.info("policyName : " + updatedPolicy.getPolicyName());
                logger.info("policyVersion :" + updatedPolicy.getVersionNo());
            }
            runOnNotification(notification);
        } else {
            logger.info("Slave application Skipping Notification.. ");
        }
    }

    /*
     * Executed when a policy is removed from PDP.
     */
    private void removedPolicies(final Collection<RemovedPolicy> removedPolicies) {
        Boolean removed = false;
        logger.info("Removed Policies");
        for (final RemovedPolicy removedPolicy : removedPolicies) {
            logger.info(removedPolicy.getPolicyName());
            logger.info(removedPolicy.getVersionNo());
            if (removedPolicy.getPolicyName().contains("_BRMS_")) {
                try {
                    logger.info("Policy Removed with this policy Name : " + removedPolicy.getPolicyName());
                    brmsPush.removeRule(removedPolicy.getPolicyName());
                    removed = true;
                } catch (final Exception e) {
                    logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Rertriving policy failed " + e.getMessage(),
                            e);
                }
            }
        }
        Boolean failureFlag;
        int index = 0;
        do {
            failureFlag = false;
            if (removed) {
                try {
                    brmsPush.pushRules();
                } catch (final PolicyException e) {
                    // Upon Notification failure
                    failureFlag = true;
                    brmsPush.rotateUrls();
                    logger.error("Failure during Push Operation ", e);
                }
            }
            index++;
        } while (failureFlag && index < brmsPush.urlListSize());
    }

    /*
     * This method is executed if BRMSGW is "MASTER" (non-Javadoc)
     * 
     * @see
     * org.onap.policy.utils.BackUpHandler#runOnNotification(org.onap.policy.api.PDPNotification)
     */
    @Override
    public void runOnNotification(final PDPNotification notification) {
        // reset the BRMSPush data structures
        brmsPush.resetDs();
        if (notification.getNotificationType().equals(NotificationType.REMOVE)) {
            removedPolicies(notification.getRemovedPolicies());
        } else if (notification.getNotificationType().equals(NotificationType.UPDATE)
                || notification.getNotificationType().equals(NotificationType.BOTH)) {
            logger.info("Updated Policies: \n");
            final ArrayList<PolicyConfig> brmsPolicies = addedPolicies(notification);
            Boolean successFlag = false;
            for (int i = 0; !successFlag && i < brmsPush.urlListSize(); i++) {
                if (i != 0) {
                    for (final PolicyConfig policyConfig : brmsPolicies) {
                        logger.info("Policy Retry with this Name notified: " + policyConfig.getPolicyName());
                        brmsPush.addRule(policyConfig.getPolicyName(), policyConfig.toOther(),
                                policyConfig.getResponseAttributes());
                    }
                }
                try {
                    brmsPush.pushRules();
                    successFlag = true;
                } catch (final PolicyException e) {
                    // Upon Notification failure
                    successFlag = false;
                    brmsPush.rotateUrls();
                    logger.error("Failure during Push Operation ", e);
                }
            }
        }
    }

    /*
     * Executed when a policy is added to PDP.
     */
    private ArrayList<PolicyConfig> addedPolicies(final PDPNotification notification) {
        final ArrayList<PolicyConfig> result = new ArrayList<>();
        for (final LoadedPolicy updatedPolicy : notification.getLoadedPolicies()) {
            logger.info("policyName : " + updatedPolicy.getPolicyName());
            logger.info("policyVersion :" + updatedPolicy.getVersionNo());
            logger.info("Matches: " + updatedPolicy.getMatches());
            // Checking the Name is correct or not.
            if (updatedPolicy.getPolicyName().contains("_BRMS_")) {
                try {
                    final PolicyEngine policyEngine = getPolicyEngine();
                    if (policyEngine != null) {
                        final ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
                        configRequestParameters.setPolicyName(updatedPolicy.getPolicyName());
                        final Collection<PolicyConfig> policyConfigs = policyEngine.getConfig(configRequestParameters);
                        for (final PolicyConfig policyConfig : policyConfigs) {
                            if (policyConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_RETRIEVED)) {
                                logger.info(
                                        "Policy Retrieved with this Name notified: " + policyConfig.getPolicyName());
                                result.add(policyConfig);
                                brmsPush.addRule(policyConfig.getPolicyName(), policyConfig.toOther(),
                                        policyConfig.getResponseAttributes());
                            } else {
                                logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR
                                        + "Fail to retrieve policy so rule will not be pushed to PolicyRepo !!!!\n\n");
                            }
                        }
                    }
                } catch (final Exception e) {
                    logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Rertriving policy failed " + e.getMessage(),
                            e);
                }
            }
        }
        return result;
    }

    public PolicyEngine getPolicyEngine() {
        return BrmsGateway.getPolicyEngine();
    }
}
