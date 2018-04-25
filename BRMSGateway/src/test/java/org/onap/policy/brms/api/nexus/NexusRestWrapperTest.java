/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 Ericsson Intellectual Property. All rights reserved.
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

package org.onap.policy.brms.api.nexus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.onap.policy.brms.api.nexus.NexusRestSearchParameters;
import org.onap.policy.brms.api.nexus.NexusRestWrapper;
import org.onap.policy.brms.api.nexus.NexusRestWrapperException;
import org.onap.policy.brms.api.nexus.pojo.NexusArtifact;

public class NexusRestWrapperTest {
    @Test
    public void testRestWrapperConstructionErrors() throws NexusRestWrapperException {
        try {
            new NexusRestWrapper(null, null, null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("nexusServerUrl must be specified for the Nexus server", e.getMessage());
        }

        try {
            new NexusRestWrapper("", null, null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("nexusServerUrl must be specified for the Nexus server", e.getMessage());
        }

        try {
            new NexusRestWrapper("   ", null, null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("nexusServerUrl must be specified for the Nexus server", e.getMessage());
        }

        try {
            new NexusRestWrapper("\n\t", null, null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("nexusServerUrl must be specified for the Nexus server", e.getMessage());
        }

        try {
            new NexusRestWrapper("server", "user", null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("if either nexusUser or nexusPassword are specified, both must be specified", e.getMessage());
        }

        try {
            new NexusRestWrapper("server", null, "pass");
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("if either nexusUser or nexusPassword are specified, both must be specified", e.getMessage());
        }

        NexusRestWrapper wrapper = new NexusRestWrapper("http://localhost:99999", "user", "pass");
        assertNotNull(wrapper);

        try {
            wrapper.findArtifact(null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("searchParameters may not be null", e.getMessage());
        }

        try {
            wrapper.findArtifact(new NexusRestSearchParameters());
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("search parameters have not been set", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();

            searchParameters.useFilterSearch("org.onap.policy.engine", null, null, null, null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("search to URI http://localhost:99999/service/local/lucene/search?g=org.onap.policy.engine "
                            + "failed with message: ", e.getMessage().substring(0, 111));
        }

        wrapper.close();
        wrapper = new NexusRestWrapper("http://localhost:57344", "user", "pass");

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();

            searchParameters.useFilterSearch("org.onap.policy.engine", null, null, null, null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("search to URI http://localhost:57344/service/local/lucene/search?g=org.onap.policy.engine "
                            + "failed with message: ", e.getMessage().substring(0, 111));
        }

        wrapper.close();
        wrapper = new NexusRestWrapper("https://nexus.onap.org", "user", "pass");

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useFilterSearch("org.onap.policy.engine", null, "", null, null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("search to URI https://nexus.onap.org/service/local/lucene/search?g=org.onap.policy.engine "
                            + "failed, response was: InboundJaxrsResponse{context=ClientResponse{method=GET, "
                            + "uri=https://nexus.onap.org/service/local/lucene/search?g=org.onap.policy.engine, status=401, reason=Unauthorized}}",
                            e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useFilterSearch(null, null, null, null, null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("at least one filter parameter must be specified for Nexus filter searches", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useKeywordSearch(null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("keyword must be specified for Nexus keyword searches", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useClassNameSearch(null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("className must be specified for Nexus class name searches", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useChecksumSearch(null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("checksum must be specified for Nexus checksum searches", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useKeywordSearch("BRMSGateway").setRepositoryId(null);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("a repositoryId must be specified", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useKeywordSearch("BRMSGateway").setFrom(-1);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("from cannot be less than 0 when from is specified", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useKeywordSearch("BRMSGateway").setCount(0);
            wrapper.findArtifact(searchParameters);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("count cannot be less than 1 when count is specified", e.getMessage());
        }

        try {
            NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
            searchParameters.useKeywordSearch("BRMSGateway");
            searchParameters.getSearchUri(null);
            fail("test shold throw an exception here");
        } catch (NexusRestWrapperException e) {
            assertEquals("nexusServerUrl must be specified for the search URI", e.getMessage());
        }

        wrapper.close();
    }

    @Test
    public void testGetters() throws NexusRestWrapperException {
        NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();
        
        searchParameters.useKeywordSearch("BRMSGateway");
        assertEquals(NexusRestSearchParameters.SearchType.KEYWORD, searchParameters.getSearchType());
        assertEquals(null, searchParameters.getArtifactId());
        assertEquals(null, searchParameters.getChecksum());
        assertEquals(null, searchParameters.getClassName());
        assertEquals(null, searchParameters.getClassifier());
        assertEquals(-1, searchParameters.getCount());
        assertEquals(-1, searchParameters.getFrom());
        assertEquals(null, searchParameters.getGroupId());
        assertEquals("BRMSGateway", searchParameters.getKeyword());
        assertEquals(null, searchParameters.getPackagingType());
        assertEquals(null, searchParameters.getRepositoryId());
        assertEquals(null, searchParameters.getVersion());
        
        searchParameters.useFilterSearch("org.onap.policy.engine", "BRMSGateway",
                        "1.2.3", "jar", "jar-with-dependencies")
            .setFrom(100).setCount(10).setRepositoryId("repository");
        assertEquals(NexusRestSearchParameters.SearchType.FILTER, searchParameters.getSearchType());
        assertEquals("BRMSGateway", searchParameters.getArtifactId());
        assertEquals(null, searchParameters.getChecksum());
        assertEquals(null, searchParameters.getClassName());
        assertEquals("jar-with-dependencies", searchParameters.getClassifier());
        assertEquals(10, searchParameters.getCount());
        assertEquals(100, searchParameters.getFrom());
        assertEquals("org.onap.policy.engine", searchParameters.getGroupId());
        assertEquals(null, searchParameters.getKeyword());
        assertEquals("jar", searchParameters.getPackagingType());
        assertEquals("repository", searchParameters.getRepositoryId());
        assertEquals("1.2.3", searchParameters.getVersion());

        searchParameters.useClassNameSearch("BRMSGateway");
        assertEquals(NexusRestSearchParameters.SearchType.CLASS_NAME, searchParameters.getSearchType());
        assertEquals(null, searchParameters.getArtifactId());
        assertEquals(null, searchParameters.getChecksum());
        assertEquals("BRMSGateway", searchParameters.getClassName());
        assertEquals(null, searchParameters.getClassifier());
        assertEquals(-1, searchParameters.getCount());
        assertEquals(-1, searchParameters.getFrom());
        assertEquals(null, searchParameters.getGroupId());
        assertEquals(null, searchParameters.getKeyword());
        assertEquals(null, searchParameters.getPackagingType());
        assertEquals(null, searchParameters.getRepositoryId());
        assertEquals(null, searchParameters.getVersion());

        searchParameters.useChecksumSearch("987654321");
        assertEquals(NexusRestSearchParameters.SearchType.CHECKSUM, searchParameters.getSearchType());
        assertEquals(null, searchParameters.getArtifactId());
        assertEquals("987654321", searchParameters.getChecksum());
        assertEquals(null, searchParameters.getClassName());
        assertEquals(null, searchParameters.getClassifier());
        assertEquals(-1, searchParameters.getCount());
        assertEquals(-1, searchParameters.getFrom());
        assertEquals(null, searchParameters.getGroupId());
        assertEquals(null, searchParameters.getKeyword());
        assertEquals(null, searchParameters.getPackagingType());
        assertEquals(null, searchParameters.getRepositoryId());
        assertEquals(null, searchParameters.getVersion());
    }

    @Test
    public void testFilterSearch() throws NexusRestWrapperException {
        NexusRestWrapper wrapper = new NexusRestWrapper("https://nexus.onap.org", null, null);

        NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();

        searchParameters.useFilterSearch("org.onap.policy.dorothy", null, null, null, null).setCount(1);
        List<NexusArtifact> foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(0, foundArtifactList.size());

        searchParameters.useFilterSearch("org.onap.policy.engine", null, null, null, null).setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch("org.onap.policy.engine", null, null, null, null).setFrom(2).setCount(2);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(2, foundArtifactList.size());

        searchParameters.useFilterSearch("org.onap.policy.engine", null, null, null, null).setFrom(2).setCount(2);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(2, foundArtifactList.size());

        searchParameters.useFilterSearch(null, "BRMSGateway", null, null, null);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertTrue(foundArtifactList.size() > 2);

        searchParameters.useFilterSearch(null, null, "1.2.3", null, null).setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch("org.onap.policy.engine", null, "1.1.2", null, null).setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch("org.onap.policy.engine", "BRMSGateway", "1.1.2", null, null).setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch(null, "BRMSGateway", "1.1.2", null, null).setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch(null, "BRMSGateway", "1.1.2", "jar", null).setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch(null, "BRMSGateway", "1.1.2", "jar", "jar-with-dependencies").setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        searchParameters.useFilterSearch(null, "BRMSGateway", "1.1.2", "jar", "jar-with-dependencies")
            .setCount(1).setRepositoryId("releases");
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        wrapper.close();
    }

    @Test
    public void testKeywordSearch() throws NexusRestWrapperException {
        NexusRestWrapper wrapper = new NexusRestWrapper("https://nexus.onap.org", null, null);

        NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();

        searchParameters.useKeywordSearch("TheWizardOfOz").setCount(1);
        List<NexusArtifact> foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(0, foundArtifactList.size());

        searchParameters.useKeywordSearch("BRMSGateway").setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        wrapper.close();
    }

    @Test
    public void testClassNameSearch() throws NexusRestWrapperException {
        NexusRestWrapper wrapper = new NexusRestWrapper("https://nexus.onap.org", null, null);

        NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();

        searchParameters.useClassNameSearch("TheWizardOfOz").setCount(1);
        List<NexusArtifact> foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(0, foundArtifactList.size());

        searchParameters.useClassNameSearch("BRMSGateway").setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        wrapper.close();
    }

    @Test
    public void testChecksumSearch() throws NexusRestWrapperException {
        NexusRestWrapper wrapper = new NexusRestWrapper("https://nexus.onap.org", null, null);

        NexusRestSearchParameters searchParameters = new NexusRestSearchParameters();

        searchParameters.useChecksumSearch("99999999999999").setCount(1);
        List<NexusArtifact> foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(0, foundArtifactList.size());

        searchParameters.useChecksumSearch("914acda2ce67de9b45d599109d6ad8357d01b217").setCount(1);
        foundArtifactList = wrapper.findArtifact(searchParameters).getArtifactList();
        assertNotNull(foundArtifactList);
        assertEquals(1, foundArtifactList.size());

        wrapper.close();
    }
}
