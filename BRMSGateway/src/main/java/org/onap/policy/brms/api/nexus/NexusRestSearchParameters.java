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

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

/**
 * The Class NexusRestSearchParameters is used to specify the parameters of a search on Nexus.
 * The parameters are used to return the search part of the URL for the Maven search.
 */
public class NexusRestSearchParameters {
    // The REST end point for Nexus Lucene searches
    private static final String NEXUS_LUCENE_SEARCH_PATH = "service/local/lucene/search";

    // REST search query parameter names
    private static final String KEYWORD_QUERY_PARAM        = "q";
    private static final String GROUP_ID_QUERY_PARAM       = "g";
    private static final String ARTIFACT_ID_QUERY_PARAM    = "a";
    private static final String VERSION_QUERY_PARAM        = "v";
    private static final String PACKAGING_TYPE_QUERY_PARAM = "p";
    private static final String CLASSIFIER_QUERY_PARAM     = "c";
    private static final String CLASS_NAME_QUERY_PARAM     = "cn";
    private static final String CHECKSUM_QUERY_PARAM       = "sha1";
    private static final String FROM_QUERY_PARAM           = "from";
    private static final String COUNT_QUERY_PARAM          = "count";
    private static final String REPOSITORY_ID_QUERY_PARAM = "repositoryId";

    private enum SearchType {
        KEYWORD,    // Search using a keyword
        FILTER,     // Search using a group ID, artifact ID, version, packaging type, and/or classifier filter
        CLASS_NAME, // Search for a class name
        CHECKSUM    // Search for artifacts matching a certain checksum
    }

    // The type of search to perform
    private SearchType searchType = null;

    // Search filters
    private String keyword;
    private String groupId;
    private String artifactId;
    private String version;
    private String packagingType;
    private String classifier;
    private String className;
    private String checksum;

    // Repository filter
    private String repositoryId;

    // Scope filters
    private int from = -1;
    private int count = -1;

    /**
     * Specify searching using a keyword.
     * 
     * @param keyword The keyword to search for
     * @throws NexusRestWrapperException on invalid keywords
     */
    public void useKeywordSearch(final String keyword) throws NexusRestWrapperException {
        if (isNullOrBlank(keyword)) {
            throw new NexusRestWrapperException("keyword must be specified for Nexus keyword searches");
        }

        searchType = SearchType.KEYWORD;
        this.keyword = keyword;
    }

    /**
     * Specify searching using a filter.
     * 
     * @param groupId The group ID to filter on
     * @param artifactId The artifact ID to filter on
     * @param version The version to filter on
     * @param packagingType The packaging type to filter on
     * @param classifier The classifier to filter on
     * @throws NexusRestWrapperException on invalid filters
     */
    public void useFilterSearch(final String groupId, final String artifactId, final String version,
            final String packagingType, final String classifier) throws NexusRestWrapperException {
        if (isNullOrBlank(groupId) && isNullOrBlank(artifactId) && isNullOrBlank(version)
                && isNullOrBlank(packagingType) && isNullOrBlank(classifier)) {
            throw new NexusRestWrapperException(
                    "at least one filter parameter must be specified for Nexus keyword searches");
        }

        searchType = SearchType.FILTER;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.packagingType = packagingType;
        this.classifier = classifier;
    }

    /**
     * Specify searching using a class name.
     * 
     * @param className The class name to search for
     * @throws NexusRestWrapperException on invalid className
     */
    public void useClassNameSearch(final String className) throws NexusRestWrapperException {
        if (isNullOrBlank(className)) {
            throw new NexusRestWrapperException("className must be specified for Nexus keyword searches");
        }

        searchType = SearchType.CLASS_NAME;
        this.className = className;
    }

    /**
     * Specify searching using a checksum.
     * 
     * @param checksum The checksum to search for
     * @throws NexusRestWrapperException on invalid checksum
     */
    public void useChecksumSearch(final String checksum) throws NexusRestWrapperException {
        if (isNullOrBlank(checksum)) {
            throw new NexusRestWrapperException("checksum must be specified for Nexus keyword searches");
        }

        searchType = SearchType.CHECKSUM;
        this.checksum = checksum;
    }

    /**
     * Search on a specific repository.
     * 
     * @param repositoryId The repository to search
     * @return this object to allow chaining of methods
     * @throws NexusRestWrapperException on invalid repositoryId
     */
    public NexusRestSearchParameters setRepositoryId(String repositoryId) throws NexusRestWrapperException {
        if (isNullOrBlank(repositoryId)) {
            throw new NexusRestWrapperException("repositoryId must be specified for Nexus keyword searches");
        }

        this.repositoryId = repositoryId;
        return this;
    }

    /**
     * Return the search results from this result number.
     * 
     * @param from The number of the first result to return
     * @return this object to allow chaining of methods
     * @throws NexusRestWrapperException on invalid from value
     */
    public NexusRestSearchParameters setFrom(int from) throws NexusRestWrapperException {
        if (from < 0) {
            throw new NexusRestWrapperException("from cannot be less than 0 for Nexus keyword searches");
        }

        this.from = from;
        return this;
    }

    /**
     * Return the specified number of search results.
     * 
     * @param count The number of results to return
     * @return this object to allow chaining of methods
     * @throws NexusRestWrapperException on invalid count value
     */
    public NexusRestSearchParameters setCount(int count) throws NexusRestWrapperException {
        if (count < 1) {
            throw new NexusRestWrapperException("count cannot be less than 1 for Nexus keyword searches");
        }

        this.count = count;
        return this;
    }

    /**
     * Compose the URI for the search to the Nexus server.
     * 
     * @param nexusServerUrl the URL of the server on which to search
     * @return the search URI
     * @throws NexusRestWrapperException on search URL composition exceptions
     */
    public URI getSearchUri(final String nexusServerUrl) throws NexusRestWrapperException {
        if (isNullOrBlank(nexusServerUrl)) {
            throw new NexusRestWrapperException("nexusServerUrl must be specified for Nexus keyword searches");
        }

        // Use a URI builder to build up the search URI
        UriBuilder uriBuilder = UriBuilder
                .fromPath(nexusServerUrl)
                .path(NEXUS_LUCENE_SEARCH_PATH);

        switch (searchType) {
            case KEYWORD:
                getKeywordSearchUri(uriBuilder);
                break;

            case FILTER:
                getFitlerSearchUri(uriBuilder);
                break;

            case CLASS_NAME:
                getClassNameSearchUri(uriBuilder);
                break;

            case CHECKSUM:
                getChecksumSearchUri(uriBuilder);
                break;

            default:
                throw new NexusRestWrapperException("search parameters have not been specified for the NExus search");
        }

        // Add the repository ID query parameter is required
        if (null != repositoryId) {
            uriBuilder.queryParam(REPOSITORY_ID_QUERY_PARAM, repositoryId);
        }

        // Add the from and count values if required
        if (from >= 0) {
            uriBuilder.queryParam(FROM_QUERY_PARAM, from);
        }
        if (count >= 0) {
            uriBuilder.queryParam(COUNT_QUERY_PARAM, count);
        }

        return uriBuilder.build();
    }

    /**
     * Compose the query parameters for a keyword search.
     * @param uriBuilder The builder to add query parameters to
     */
    private void getKeywordSearchUri(UriBuilder uriBuilder) {
        uriBuilder.queryParam(KEYWORD_QUERY_PARAM, keyword);
    }

    /**
     * Compose the query parameters for a filter search.
     * @param uriBuilder The builder to add query parameters to
     */
    private void getFitlerSearchUri(UriBuilder uriBuilder) {
        if (null != groupId) {
            uriBuilder.queryParam(GROUP_ID_QUERY_PARAM, groupId);
        }
        if (null != artifactId) {
            uriBuilder.queryParam(ARTIFACT_ID_QUERY_PARAM, artifactId);
        }
        if (null != version) {
            uriBuilder.queryParam(VERSION_QUERY_PARAM, version);
        }
        if (null != packagingType) {
            uriBuilder.queryParam(PACKAGING_TYPE_QUERY_PARAM, packagingType);
        }
        if (null != classifier) {
            uriBuilder.queryParam(CLASSIFIER_QUERY_PARAM, classifier);
        }
    }

    /**
     * Compose the query parameters for a class name search.
     * @param uriBuilder The builder to add query parameters to
     */
    private void getClassNameSearchUri(UriBuilder uriBuilder) {
        uriBuilder.queryParam(CLASS_NAME_QUERY_PARAM, className);
    }

    /**
     * Compose the query parameters for a checksum search.
     * @param uriBuilder The builder to add query parameters to
     */
    private void getChecksumSearchUri(UriBuilder uriBuilder) {
        uriBuilder.queryParam(CHECKSUM_QUERY_PARAM, checksum);
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getPackagingType() {
        return packagingType;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getClassName() {
        return className;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public int getFrom() {
        return from;
    }

    public int getCount() {
        return count;
    }

    /**
     * Check if a string is null or all white space.
     */
    private boolean isNullOrBlank(final String parameter) {
        return null == parameter || parameter.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "NexusRestSearchParameters [searchType=" + searchType + ", keyword=" + keyword + ", groupId=" + groupId
                + ", artifactId=" + artifactId + ", version=" + version + ", packagingType=" + packagingType
                + ", classifier=" + classifier + ", className=" + className + ", checksum=" + checksum
                + ", repositoryId=" + repositoryId + ", from=" + from + ", count=" + count + "]";
    }
}
