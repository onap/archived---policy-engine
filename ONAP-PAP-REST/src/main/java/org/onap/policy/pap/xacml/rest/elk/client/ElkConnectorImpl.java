/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.elk.client;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.xacml.api.XACMLErrorConstants;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.type.TypeExist;
import io.searchbox.params.Parameters;

public class ElkConnectorImpl implements ElkConnector{

    private static final Logger LOGGER = FlexLogger.getLogger(ElkConnector.class);

    protected final JestClientFactory jestFactory = new JestClientFactory();
    protected final JestClient jestClient;
    protected static int QUERY_MAXRECORDS = 1000;

    public ElkConnectorImpl() {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("ENTER: -");
        }
        HttpClientConfig jestClientConfig = new HttpClientConfig.Builder(ELK_URL).multiThreaded(true).build();
        jestFactory.setHttpClientConfig(jestClientConfig);
        jestClient = jestFactory.getObject();
    }

    protected boolean isType(PolicyIndexType type) throws IOException {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("ENTER: -");
        }

        try {
            Action<JestResult> typeQuery = new TypeExist.Builder(ELK_INDEX_POLICY).addType(type.toString()).build();
            JestResult result = jestClient.execute(typeQuery);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("JSON:" + result.getJsonString());
                LOGGER.info("ERROR:" + result.getErrorMessage());
                LOGGER.info("PATH:" + result.getPathToResult());
                LOGGER.info(result.getJsonObject());
            }
            return result.isSucceeded();
        } catch (IOException e) {
            LOGGER.warn("Error checking type existance of " + type.toString() + ": " + e.getMessage(), e);
            throw e;
        }
    }

    protected boolean isIndex() throws IOException {
        try {
            Action<JestResult> indexQuery = new IndicesExists.Builder(ELK_INDEX_POLICY).build();

            JestResult result = jestClient.execute(indexQuery);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("JSON:" + result.getJsonString());
                LOGGER.info("ERROR:" + result.getErrorMessage());
                LOGGER.info("PATH:" + result.getPathToResult());
                LOGGER.info(result.getJsonObject());
            }
            return result.isSucceeded();
        } catch (IOException e) {
            LOGGER.warn("Error checking index existance of " + ELK_INDEX_POLICY + ": " + e.getMessage(), e);
            throw e;
        }
    }
    private boolean isAlphaNumeric(String query){
        return query.matches("[a-zA-Z_0-9]+");
    }

    @Override
    public JestResult search(PolicyIndexType type, String text) throws IllegalStateException, IllegalArgumentException {
        if (LOGGER.isTraceEnabled()){
            LOGGER.trace("ENTER: " + text);
        }

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("No search string provided");
        }

        if(!isAlphaNumeric(text)){
            throw new IllegalArgumentException("Search must be alpha numeric");
        }

        QueryStringQueryBuilder mQ = QueryBuilders.queryStringQuery("*"+text+"*");
        SearchSourceBuilder searchSourceBuilder =
                new SearchSourceBuilder().query(mQ);

        Builder searchBuilder = new Search.Builder(searchSourceBuilder.toString()).
                addIndex(ELK_INDEX_POLICY).
                setParameter(Parameters.SIZE, ElkConnectorImpl.QUERY_MAXRECORDS);

        if (type == null || type == PolicyIndexType.all) {
            for (PolicyIndexType pT: PolicyIndexType.values()) {
                if (pT != PolicyIndexType.all) {
                    searchBuilder.addType(pT.toString());
                }
            }
        } else {
            searchBuilder.addType(type.toString());
        }

        Search search = searchBuilder.build();
        JestResult result;
        try {
            result = jestClient.execute(search);
        } catch (IOException ioe) {
            LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" +
                    search + ": " + ioe.getMessage(), ioe);
            throw new IllegalStateException(ioe);
        }

        if (result.isSucceeded()) {
            if (LOGGER.isInfoEnabled()){
                LOGGER.info("OK:" + result.getResponseCode() + ":" + search + ": " +
                        result.getPathToResult() + ":" + System.lineSeparator() +
                        result.getJsonString());
            }
        } else {
            /* Unsuccessful search */
            if (LOGGER.isWarnEnabled()){
                LOGGER.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" +
                        result.getResponseCode() + ": " +
                        search.getURI() + ":" +
                        result.getPathToResult() + ":" +
                        result.getJsonString() + ":" +
                        result.getErrorMessage());
            }

            String errorMessage = result.getErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                String xMessage;
                if (errorMessage.contains("TokenMgrError")) {
                    int indexError = errorMessage.lastIndexOf("TokenMgrError");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("QueryParsingException")) {
                    int indexError = errorMessage.lastIndexOf("QueryParsingException");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("JsonParseException")) {
                    int indexError = errorMessage.lastIndexOf("JsonParseException");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("Parse Failure")) {
                    int indexError = errorMessage.lastIndexOf("Parse Failure");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("SearchParseException")) {
                    int indexError = errorMessage.lastIndexOf("SearchParseException");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else {
                    xMessage = result.getErrorMessage();
                }
                throw new IllegalStateException(xMessage);
            }
        }

        return result;
    }


    @Override
    public JestResult search(PolicyIndexType type, String text,
            Map<String, String> filter_s)
                    throws IllegalStateException, IllegalArgumentException {
        if (LOGGER.isTraceEnabled()){
            LOGGER.trace("ENTER: " + text);
        }

        if (filter_s == null || filter_s.size() == 0) {
            return search(type, text);
        }

        if(!isAlphaNumeric(text)){
            throw new IllegalArgumentException("Search must be alpha numeric");
        }

        String matches_s = "";
        matches_s = "{\n" +
                "    \"size\" : "+ ElkConnectorImpl.QUERY_MAXRECORDS + ",\n" +
                "    \"query\": {\n" +
                "        \"bool\" : {\n" +
                "            \"must\" : [";

        String match_params = "";
        boolean first = true;
        for(Entry<String, String> entry : filter_s.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            if(first){
                match_params = "\"match\" : {\""+key+"\" : \""+value+"\" }},";
                first = false;
            }else{
                match_params = match_params + "{\"match\" : { \""+key+"\" : \""+value+"\" } },";
            }
        }
        if(match_params.endsWith(",")){
            match_params = match_params.substring(0, match_params.length()-2);
        }

        matches_s = matches_s + "{\n" + match_params + "\n}" ;

        boolean query = false;
        String query_String = "";
        if(text != null){
            query = true;
            query_String = "{\n \"query_string\" : {\n \"query\" : \"*"+text+"*\"\n} \n}";
        }

        if(query){
            matches_s = matches_s + "," +  query_String + "]\n}\n}\n}";
        }else{
            matches_s = matches_s + "]\n}\n}\n}";
        }

        Builder searchBuilder = new Search.Builder(matches_s).addIndex(ELK_INDEX_POLICY);

        if (type == null || type == PolicyIndexType.all) {
            for (PolicyIndexType pT: PolicyIndexType.values()) {
                if (pT != PolicyIndexType.all) {
                    searchBuilder.addType(pT.toString());
                }
            }
        } else {
            searchBuilder.addType(type.toString());
        }

        Search search = searchBuilder.build();

        JestResult result;
        try {
            result = jestClient.execute(search);
        } catch (IOException ioe) {
            LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" +
                    search + ": " + ioe.getMessage(), ioe);
            throw new IllegalStateException(ioe);
        }

        if (result.isSucceeded()) {
            if (LOGGER.isInfoEnabled()){
                LOGGER.info("OK:" + result.getResponseCode() + ":" + search + ": " +
                        result.getPathToResult() + ":" + System.lineSeparator() +
                        result.getJsonString());
            }
        } else {
            /* Unsuccessful search */
            if (LOGGER.isWarnEnabled()){
                LOGGER.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" +
                        result.getResponseCode() + ": " +
                        search.getURI() + ":" +
                        result.getPathToResult() + ":" +
                        result.getJsonString() + ":" +
                        result.getErrorMessage());
            }

            String errorMessage = result.getErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                String xMessage = errorMessage;
                if (errorMessage.contains("TokenMgrError")) {
                    int indexError = errorMessage.lastIndexOf("TokenMgrError");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("QueryParsingException")) {
                    int indexError = errorMessage.lastIndexOf("QueryParsingException");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("JsonParseException")) {
                    int indexError = errorMessage.lastIndexOf("JsonParseException");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("Parse Failure")) {
                    int indexError = errorMessage.lastIndexOf("Parse Failure");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else if (errorMessage.contains("SearchParseException")) {
                    int indexError = errorMessage.lastIndexOf("SearchParseException");
                    xMessage = "Invalid Search Expression.  Details: " + errorMessage.substring(indexError);
                } else {
                    xMessage = result.getErrorMessage();
                }
                throw new IllegalStateException(xMessage);
            }
        }

        return result;
    }

    public boolean put(PolicyRestAdapter policyData)
            throws IOException, IllegalStateException {
        if (LOGGER.isTraceEnabled()) LOGGER.trace("ENTER");

        PolicyIndexType indexType;
        try {
            String policyName = policyData.getNewFileName();
            if(policyName.contains("Config_")){
                policyName = policyName.replace(".Config_", ":Config_");
            }else if(policyName.contains("Action_")){
                policyName = policyName.replace(".Action_", ":Action_");
            }else if(policyName.contains("Decision_")){
                policyName = policyName.replace(".Decision_", ":Decision_");
            }

            String[] splitPolicyName = policyName.split(":");
            indexType = ElkConnector.toPolicyIndexType(splitPolicyName[1]);
        } catch (IllegalArgumentException e) {
            LOGGER.error(e);
            throw new IllegalStateException("ELK: Index: " + ELK_INDEX_POLICY + e.getMessage());
        }
        PolicyElasticData elasticData = new PolicyElasticData(policyData);
        JSONObject jsonObj = new JSONObject(elasticData);
        Index elkPut = new Index.Builder(jsonObj.toString()).
                index(ELK_INDEX_POLICY).
                type(indexType.name()).
                id(elasticData.getPolicyName()).
                refresh(true).
                build();

        JestResult result = jestClient.execute(elkPut);

        if (result.isSucceeded()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("ElkConnector: OK: PUT operation of " + "->"  + ": " +
                        "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
                        result.getPathToResult() + "]" + System.lineSeparator() +
                        result.getJsonString());
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("ElkConnector: FAILURE: PUT operation of "+ "->" + ": " +
                        "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
                        result.getPathToResult() + "]" + System.lineSeparator() +
                        result.getJsonString());

        }

        return result.isSucceeded();
    }

    @Override
    public boolean delete(PolicyRestAdapter policyData) throws IllegalStateException  {
        PolicyIndexType indexType = null;
        JestResult result;
        try {
            String policyName = policyData.getNewFileName();
            if(policyName.contains("Config_")){
                policyName = policyName.replace(".Config_", ":Config_");
            }else if(policyName.contains("Action_")){
                policyName = policyName.replace(".Action_", ":Action_");
            }else if(policyName.contains("Decision_")){
                policyName = policyName.replace(".Decision_", ":Decision_");
            }

            String[] splitPolicyName = policyName.split(":");
            indexType = ElkConnector.toPolicyIndexType(splitPolicyName[1]);
            if (!isType(indexType)) {
                throw new IllegalStateException("ELK: Index: " + ELK_INDEX_POLICY +
                        " Type: " + indexType +
                        " is not configured");
            }
            PolicyElasticData elasticData = new PolicyElasticData(policyData);
            Delete deleteRequest = new Delete.Builder(elasticData.getPolicyName()).index(ELK_INDEX_POLICY).
                    type(indexType.name()).build();
            result = jestClient.execute(deleteRequest);
        } catch (IllegalArgumentException | IOException e) {
            LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ": delete:" +
                    indexType +  ": null" + ":" + policyData.getNewFileName() + ": " +
                    e.getMessage(), e);
            throw new IllegalStateException(e);
        }

        if (result.isSucceeded()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("OK: DELETE operation of " + indexType + ":" + policyData.getNewFileName() + ": " +
                        "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
                        result.getPathToResult() + "]" + System.lineSeparator() +
                        result.getJsonString());
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("FAILURE: DELETE operation of " + indexType + ":" + policyData.getNewFileName() + ": " +
                        "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
                        result.getPathToResult() + "]" + System.lineSeparator() +
                        result.getJsonString());
        }

        return result.isSucceeded();
    }

    @Override
    public boolean update(PolicyRestAdapter policyData) throws IllegalStateException  {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("ENTER");
        }
        try {
            boolean success = put(policyData);
            return success;
        } catch (Exception e) {
            LOGGER.warn(XACMLErrorConstants.ERROR_UNKNOWN + ":" + "cannot test and update", e);
            throw new IllegalStateException(e);
        }
    }
}
