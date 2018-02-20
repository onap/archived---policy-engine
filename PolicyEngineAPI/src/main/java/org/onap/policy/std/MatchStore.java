/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
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

package org.onap.policy.std;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.api.RemovedPolicy;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class MatchStore {
    private static final String CONFIG_NAME = "ConfigName";

    private static final String ONAP_NAME = "ONAPName";

    private static final Set<Matches> MATCH_STORE = new HashSet<>();

    private static final Logger LOGGER = FlexLogger.getLogger(MatchStore.class.getName());

    private MatchStore() {
        // Empty Constructor
    }

    public static Set<Matches> getMatchStore() {
        return MATCH_STORE;
    }

    public static void storeMatch(final Matches newMatch) {
        if (newMatch != null && !MATCH_STORE.contains(newMatch)) {
            MATCH_STORE.add(newMatch);
        }
    }

    // Logic changes for Requested Policies notifications..
    public static PDPNotification checkMatch(final PDPNotification oldNotification) {
        if (oldNotification == null) {
            return null;
        }
        if (MATCH_STORE.isEmpty()) {
            LOGGER.debug("No Success Config Calls made yet.. ");
            return null;
        }
        return getPDPNotification(oldNotification);
    }

    private static PDPNotification getPDPNotification(final PDPNotification oldNotification) {
        boolean removed = false;
        boolean updated = false;
        final StdPDPNotification newNotification = new StdPDPNotification();
        if (isValid(oldNotification.getRemovedPolicies())) {
            // send all removed policies to client.
            newNotification.setRemovedPolicies(getStdRemovedPolicies(oldNotification.getRemovedPolicies()));
            removed = true;
        }
        if (isValid(oldNotification.getLoadedPolicies())) {
            final Collection<StdLoadedPolicy> updatedPolicies = new HashSet<>();
            for (final LoadedPolicy updatedPolicy : oldNotification.getLoadedPolicies()) {
                updated = updateStdLoadedPolicy(updated, updatedPolicies, updatedPolicy);
            }
            newNotification.setLoadedPolicies(updatedPolicies);
        }
        // Need to set the type of Update..
        if (removed && updated) {
            newNotification.setNotificationType(NotificationType.BOTH);
        } else if (removed) {
            newNotification.setNotificationType(NotificationType.REMOVE);
        } else if (updated) {
            newNotification.setNotificationType(NotificationType.UPDATE);
        }
        return newNotification;
    }

    private static boolean updateStdLoadedPolicy(boolean updated, final Collection<StdLoadedPolicy> updatedPolicies,
            final LoadedPolicy updatedPolicy) {
        // if it is config policies check their matches
        if (isValid(updatedPolicy.getMatches())) {
            final Map<String, String> matchesMap = updatedPolicy.getMatches();
            final Matches policyMatches = getMatches(matchesMap);
            for (final Matches match : MATCH_STORE) {
                if (match.equals(policyMatches)) {
                    final StdLoadedPolicy newUpdatedPolicy = new StdLoadedPolicy();
                    newUpdatedPolicy.setPolicyName(updatedPolicy.getPolicyName());
                    newUpdatedPolicy.setVersionNo(updatedPolicy.getVersionNo());
                    newUpdatedPolicy.setMatches(updatedPolicy.getMatches());
                    updatedPolicies.add(newUpdatedPolicy);
                    updated = true;
                } else {
                    break;
                }
            }

        } else {
            // send all non config notifications to client.
            final StdLoadedPolicy newUpdatedPolicy = new StdLoadedPolicy();
            newUpdatedPolicy.setPolicyName(updatedPolicy.getPolicyName());
            newUpdatedPolicy.setVersionNo(updatedPolicy.getVersionNo());
            updatedPolicies.add(newUpdatedPolicy);
            updated = true;
        }
        return updated;
    }

    private static Matches getMatches(final Map<String, String> attributes) {
        final Matches matches = new Matches();
        matches.setOnapName(attributes.get(ONAP_NAME));
        matches.setConfigName(attributes.get(CONFIG_NAME));

        final Map<String, String> configAttributes = new HashMap<>(attributes);
        // remove onap and config to config-attributes.
        configAttributes.remove(ONAP_NAME);
        configAttributes.remove(CONFIG_NAME);

        matches.setConfigAttributes(configAttributes);

        return matches;
    }

    private static boolean isValid(final Map<String, String> map) {
        return map != null && !map.isEmpty();
    }

    private static Collection<StdRemovedPolicy> getStdRemovedPolicies(final Collection<RemovedPolicy> policies) {
        final Set<StdRemovedPolicy> removedPolicies = new HashSet<>();
        for (final RemovedPolicy removedPolicy : policies) {
            final StdRemovedPolicy newRemovedPolicy = new StdRemovedPolicy();
            newRemovedPolicy.setPolicyName(removedPolicy.getPolicyName());
            newRemovedPolicy.setVersionNo(removedPolicy.getVersionNo());
            removedPolicies.add(newRemovedPolicy);
        }
        return removedPolicies;
    }

    private static boolean isValid(final Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

}
