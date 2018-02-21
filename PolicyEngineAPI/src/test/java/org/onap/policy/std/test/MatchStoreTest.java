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

package org.onap.policy.std.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.api.LoadedPolicy;
import org.onap.policy.api.NotificationType;
import org.onap.policy.api.PDPNotification;
import org.onap.policy.std.MatchStore;
import org.onap.policy.std.Matches;
import org.onap.policy.std.StdLoadedPolicy;
import org.onap.policy.std.StdPDPNotification;
import org.onap.policy.std.StdRemovedPolicy;

/**
 * The class <code>MatchStoreTest</code> contains tests for the class
 * <code>{@link MatchStore}</code>.
 *
 * @generatedBy CodePro at 6/1/16 1:41 PM
 * @version $Revision: 1.0 $
 */
public class MatchStoreTest {

    private static final String ATTRIBUTE_DUMMY_VALUE = "Value";

    private static final String ATTRIBUTE_DUMMY_KEY = "Key";

    private static final String POLICY_NAME = "ONAP";

    private static final String POLICY_VERSION = "1.0.0";

    private static final String ONAP_NAME_VAL = "ONAPName";

    private static final String CONFIG_NAME_VAL = "ConfigName";

    private static final String EMPTY_STRING = "";

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception
     *             if the initialization fails for some reason
     */
    @Before
    public void setUp() throws Exception {
        MatchStore.getMatchStore().clear();
    }

    @Test
    public void testCheckMatch_ShouldReturnNullWithNullPDPNotification() throws Exception {
        final PDPNotification oldNotification = null;

        final PDPNotification result = MatchStore.checkMatch(oldNotification);

        assertNull(result);
    }

    @Test
    public void testCheckMatch_ShouldReturnNullIfMatchStoreCacheIsEmpty() throws Exception {
        final PDPNotification oldNotification = new StdPDPNotification();

        final PDPNotification result = MatchStore.checkMatch(oldNotification);
        assertNull(result);
    }

    @Test
    public void testCheckMatch_ShouldNotReturnNullIfMatchStoreCacheIsNotEmpty() throws Exception {
        final Matches newMatch = getMatchesInstance(EMPTY_STRING, EMPTY_STRING);
        MatchStore.storeMatch(newMatch);
        final PDPNotification oldNotification = new StdPDPNotification();

        final PDPNotification result = MatchStore.checkMatch(oldNotification);
        assertNotNull(result);
    }

    @Test
    public void testGetMatchStore_ShouldNotBeNullOnStartUP() throws Exception {
        final Set<Matches> result = MatchStore.getMatchStore();
        assertNotNull(result);
    }

    @Test
    public void testStoreMatch_ShouldRetrunEmptyLoadedRemovedPolicesIfNotMatchFoundInMatchStore() throws Exception {
        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL);

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification oldNotification = new StdPDPNotification();
        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertTrue(actualPDPNotification.getLoadedPolicies().isEmpty());
        assertTrue(actualPDPNotification.getRemovedPolicies().isEmpty());
        assertNull(actualPDPNotification.getNotificationType());
    }

    @Test
    public void testStoreMatch_NotificationTypeRemoved_IfRemovedPolicyExistInOldNotification() throws Exception {
        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL);
        final StdRemovedPolicy removedPolicy = getRemovedPolicy(POLICY_VERSION, POLICY_NAME);
        final PDPNotification oldNotification = getPDPNotification(Arrays.asList(removedPolicy),
                Collections.emptySet());

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.REMOVE, actualPDPNotification.getNotificationType());
        assertFalse(actualPDPNotification.getRemovedPolicies().isEmpty());

    }

    @Test
    public void testStoreMatch_NoticficationTypeUpdate_IfStdLoadPolicyExistsWithEmptyMatches() throws Exception {
        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL);
        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, Collections.emptyMap());
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.UPDATE, actualPDPNotification.getNotificationType());
        assertFalse(actualPDPNotification.getLoadedPolicies().isEmpty());
    }

    @Test
    public void testStoreMatch_NoticficationTypeUpdate_IfStdLoadPolicyExistsWithMatches() throws Exception {
        final Map<String, String> attribute = getAttributesMap();

        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, attribute);
        final Map<String, String> matches = getPolicyMatches(ONAP_NAME_VAL, CONFIG_NAME_VAL);
        matches.putAll(attribute);

        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, matches);
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.UPDATE, actualPDPNotification.getNotificationType());

        final Collection<LoadedPolicy> actualLoadPolicies = actualPDPNotification.getLoadedPolicies();
        assertFalse(actualLoadPolicies.isEmpty());

        final LoadedPolicy loadedPolicy = actualLoadPolicies.iterator().next();
        assertEquals(stdLoadedPolicy.getPolicyName(), loadedPolicy.getPolicyName());
        assertEquals(stdLoadedPolicy.getVersionNo(), loadedPolicy.getVersionNo());
    }

    @Test
    public void testStoreMatch_NoticficationTypeUpdate_IfStdLoadPolicyExistsWithNullMatches() throws Exception {
        final Map<String, String> attribute = getAttributesMap();

        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, attribute);

        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, null);
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.UPDATE, actualPDPNotification.getNotificationType());

        final Collection<LoadedPolicy> actualLoadPolicies = actualPDPNotification.getLoadedPolicies();
        assertFalse(actualLoadPolicies.isEmpty());

        final LoadedPolicy loadedPolicy = actualLoadPolicies.iterator().next();
        assertEquals(stdLoadedPolicy.getPolicyName(), loadedPolicy.getPolicyName());
        assertEquals(stdLoadedPolicy.getVersionNo(), loadedPolicy.getVersionNo());
    }

    @Test
    public void testStoreMatch_NoticficationTypeNull_IfStdLoadPolicyExistsWithMatchesWithOutMatchingConfigAttribute()
            throws Exception {
        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, getAttributesMap());
        final Map<String, String> matches = getPolicyMatches(ONAP_NAME_VAL, CONFIG_NAME_VAL);

        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, matches);
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertNull(actualPDPNotification.getNotificationType());

        final Collection<LoadedPolicy> actualLoadPolicies = actualPDPNotification.getLoadedPolicies();
        assertTrue(actualLoadPolicies.isEmpty());

    }

    @Test
    public void testStoreMatch_NoticficationTypeUpdate_IfMatchStoreContainMatchesWithNullConStdLoadPolicyExistsWithMatches()
            throws Exception {

        final Matches newMatch = getMatchesInstance(null, ONAP_NAME_VAL, Collections.emptyMap());
        final Map<String, String> matches = new HashMap<>();
        matches.put(ONAP_NAME_VAL, ONAP_NAME_VAL);

        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, matches);
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.UPDATE, actualPDPNotification.getNotificationType());

        final Collection<LoadedPolicy> actualLoadPolicies = actualPDPNotification.getLoadedPolicies();
        assertFalse(actualLoadPolicies.isEmpty());

    }

    @Test
    public void testStoreMatch_NoticficationTypeUpdate_IfMatchStoreContainMatchingMatches() throws Exception {

        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, Collections.emptyMap());
        final Map<String, String> matches = getPolicyMatches(ONAP_NAME_VAL, CONFIG_NAME_VAL);

        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, matches);
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.UPDATE, actualPDPNotification.getNotificationType());

        final Collection<LoadedPolicy> actualLoadPolicies = actualPDPNotification.getLoadedPolicies();
        assertFalse(actualLoadPolicies.isEmpty());

        final LoadedPolicy loadedPolicy = actualLoadPolicies.iterator().next();
        assertEquals(stdLoadedPolicy.getPolicyName(), loadedPolicy.getPolicyName());
        assertEquals(stdLoadedPolicy.getVersionNo(), loadedPolicy.getVersionNo());
    }

    @Test
    public void testStoreMatch_NoticficationTypeUpdate_IfMatchStoreContainMatchingMatches2() throws Exception {

        final Matches firstObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, Collections.emptyMap());
        final Map<String, String> firstPolicyObj = getPolicyMatches(ONAP_NAME_VAL, CONFIG_NAME_VAL);
        final Map<String, String> attributesMap = getAttributesMap();

        final Matches secondObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, attributesMap);
        final Map<String, String> secondPolicyObj = getPolicyMatches(ONAP_NAME_VAL, CONFIG_NAME_VAL);
        secondPolicyObj.putAll(attributesMap);

        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, firstPolicyObj);
        final StdLoadedPolicy secondStdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, secondPolicyObj);
        final PDPNotification oldNotification = getPDPNotification(Collections.emptySet(),
                Arrays.asList(stdLoadedPolicy, secondStdLoadedPolicy));

        MatchStore.storeMatch(firstObj);
        MatchStore.storeMatch(secondObj);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.UPDATE, actualPDPNotification.getNotificationType());

        final Collection<LoadedPolicy> actualLoadPolicies = actualPDPNotification.getLoadedPolicies();
        assertFalse(actualLoadPolicies.isEmpty());
        assertEquals(1, actualLoadPolicies.size());
    }

    @Test
    public void testStoreMatch_NoticficationTypeBoth_IfOldNotificationContainRemovedAndLoadedPolicies()
            throws Exception {

        final Matches newMatch = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, Collections.emptyMap());
        final Map<String, String> matches = getPolicyMatches(ONAP_NAME_VAL, CONFIG_NAME_VAL);

        final StdRemovedPolicy removedPolicy = getRemovedPolicy(POLICY_VERSION, POLICY_NAME);
        final StdLoadedPolicy stdLoadedPolicy = getStdLoadedPolicy(POLICY_VERSION, POLICY_NAME, matches);
        final PDPNotification oldNotification = getPDPNotification(Arrays.asList(removedPolicy),
                Arrays.asList(stdLoadedPolicy));

        MatchStore.storeMatch(newMatch);

        assertFalse(MatchStore.getMatchStore().isEmpty());

        final PDPNotification actualPDPNotification = MatchStore.checkMatch(oldNotification);

        assertEquals(NotificationType.BOTH, actualPDPNotification.getNotificationType());
        assertFalse(actualPDPNotification.getRemovedPolicies().isEmpty());
        assertFalse(actualPDPNotification.getLoadedPolicies().isEmpty());
    }

    private Map<String, String> getPolicyMatches(final String onapName, final String configName) {
        final Map<String, String> matches = new HashMap<>();
        matches.put(ONAP_NAME_VAL, onapName);
        matches.put(CONFIG_NAME_VAL, configName);
        return matches;
    }

    @Test
    public void testStoreMatch_MatchesObjectShouldbeAddOnceToMatchStoreAndNoDuplication() throws Exception {
        final String[] configNames = new String[] { CONFIG_NAME_VAL, CONFIG_NAME_VAL, "ConfigName1", CONFIG_NAME_VAL,
                "ConfigName1", null };
        final String[] onapNames = new String[] { ONAP_NAME_VAL, ONAP_NAME_VAL, "ONAPName1", "ONAPName1", ONAP_NAME_VAL,
                ONAP_NAME_VAL };

        for (int i = 0; i < configNames.length; i++) {

            final Matches matches = getMatchesInstance(configNames[i], onapNames[i], getAttributesMap());
            MatchStore.storeMatch(matches);
            MatchStore.storeMatch(matches);
        }

        assertEquals(configNames.length - 1, MatchStore.getMatchStore().size());

    }

    @Test
    public void testStoreMatch_MatchesObjectShouldBeAddedToMatchStore_ConfigAttrValuesAreDifferentThenExistingOne()
            throws Exception {
        final Matches firstObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, getAttributesMap());
        final Matches secondObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, Collections.emptyMap());

        MatchStore.storeMatch(firstObj);
        MatchStore.storeMatch(secondObj);
        MatchStore.storeMatch(firstObj);

        assertEquals(2, MatchStore.getMatchStore().size());

    }

    @Test
    public void testStoreMatch_MatchesObjectShouldBeAddedToMatchStore_ConfigAttrValuesNull() throws Exception {
        final Matches firstObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, null);
        final Matches secondObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, null);

        MatchStore.storeMatch(firstObj);
        MatchStore.storeMatch(secondObj);
        MatchStore.storeMatch(firstObj);

        assertEquals(1, MatchStore.getMatchStore().size());

    }

    @Test
    public void testStoreMatch_MatchesObjectShouldBeAddedToMatchStore_OnapNameIsDifferentThenExistingOne()
            throws Exception {
        final Matches firstObj = getMatchesInstance(CONFIG_NAME_VAL, ONAP_NAME_VAL, getAttributesMap());
        final Matches secondObj = getMatchesInstance(CONFIG_NAME_VAL, "ONAPName1", getAttributesMap());

        MatchStore.storeMatch(firstObj);
        MatchStore.storeMatch(secondObj);
        MatchStore.storeMatch(firstObj);

        assertEquals(2, MatchStore.getMatchStore().size());

    }

    private Map<String, String> getAttributesMap() {
        final Map<String, String> attribute = new HashMap<>();
        attribute.put(ATTRIBUTE_DUMMY_KEY, ATTRIBUTE_DUMMY_VALUE);
        return attribute;
    }

    private Matches getMatchesInstance(final String configName, final String onapName) {
        return getMatchesInstance(configName, onapName, null);
    }

    private Matches getMatchesInstance(final String configName, final String onapName,
            final Map<String, String> configAttributes) {
        final Matches matches = new Matches();
        matches.setConfigName(configName);
        matches.setOnapName(onapName);
        matches.setConfigAttributes(configAttributes);
        return matches;
    }

    private StdRemovedPolicy getRemovedPolicy(final String version, final String policyName) {
        return new StdRemovedPolicy() {

            @Override
            public String getVersionNo() {
                return version;
            }

            @Override
            public String getPolicyName() {
                return policyName;
            }
        };
    }

    private PDPNotification getPDPNotification(final Collection<StdRemovedPolicy> removedPolicies,
            final Collection<StdLoadedPolicy> loadedPolicies) {
        final StdPDPNotification oldNotification = new StdPDPNotification();
        oldNotification.setLoadedPolicies(loadedPolicies);
        oldNotification.setRemovedPolicies(removedPolicies);
        return oldNotification;
    }

    private StdLoadedPolicy getStdLoadedPolicy(final String version, final String policyName,
            final Map<String, String> matches) {
        return new StdLoadedPolicy() {

            @Override
            public String getVersionNo() {
                return version;
            }

            @Override
            public String getPolicyName() {
                return policyName;
            }

            @Override
            public Map<String, String> getMatches() {
                return matches;
            }
        };
    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception
     *             if the clean-up fails for some reason
     */
    @After
    public void tearDown() throws Exception {
        // Add additional tear down code here
    }

    /**
     * Launch the test.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(final String[] args) {
        new org.junit.runner.JUnitCore().run(MatchStoreTest.class);
    }
}
