/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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
package org.openecomp.policy.pap.xacml.rest.elk.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;
import org.kohsuke.args4j.Option;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.type.TypeExist;
import io.searchbox.params.Parameters;

public class ElkConnectorImpl implements ElkConnector{

	protected static class CLIOptions {
		@Option(name="-s", usage="search", aliases={"-search", "--search"}, required=false, metaVar="<search text>")
		protected String searchText;

		@Option(name="-e", usage="test and update policy if not exists", aliases={"-exist", "--exists"}, required=false, metaVar="<policy file>")
		protected File testFile;

		@Option(name = "-h", aliases = {"-help", "--help"}, usage = "print this message")
		private boolean help = false;		
	};

	private static final String POLICY_RESULT_FIELDS = "[ \"Policy.PolicyType\", " +
			"\"Policy.PolicyName\", " +
			"\"Policy.Owner\", " +
			"\"Policy.Scope\", " +
			"\"Policy.PolicyId\", " +
			"\"Policy.Version\" ]";

	private static final String SOURCE_RESULT_FIELDS = "\"_source\": " + POLICY_RESULT_FIELDS;

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

	@Override
	public JestResult search(PolicyIndexType type, String text) throws IllegalStateException, IllegalArgumentException {
		if (LOGGER.isTraceEnabled()){
			LOGGER.trace("ENTER: " + text);
		}

		if (text == null || text.isEmpty()) {
			throw new IllegalArgumentException("No search string provided");
		}

		// MatchQueryBuilder mQ = QueryBuilders.matchQuery("_all", text);
		QueryStringQueryBuilder mQ = QueryBuilders.queryStringQuery(text);
		SearchSourceBuilder searchSourceBuilder = 
				new SearchSourceBuilder().query(mQ).
				fetchSource(new String[]{"Policy.PolicyType",
						"Policy.PolicyName",
						"Policy.Owner",
						"Policy.Scope",
						"Policy.PolicyId",
				"Policy.Version"},
						null);
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

	public JestResult searchKey(PolicyIndexType type, String text, 
			ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s,int connector) 
					throws IllegalStateException, IllegalArgumentException {
		if (LOGGER.isTraceEnabled()){
			LOGGER.trace("ENTER: " + text);
		}
		if (filter_s == null || filter_s.size() <= 0) {
			return search(type, text);
		}

		String matches_s = "";

		if(connector==0)// AND CONNECTOR
		{
			matches_s = "{\n" +
					"	 " + SOURCE_RESULT_FIELDS + ",\n" +
					"    \"size\" : "+ ElkConnectorImpl.QUERY_MAXRECORDS + ",\n" +
					"    \"query\": {\n" +
					"        \"bool\" : {\n" +
					"            \"must\" : [";
		}
		else if (connector ==1)//OR CONNECTOR
		{
			matches_s = "{\n" +
					"	 " + SOURCE_RESULT_FIELDS + ",\n" +
					"    \"size\" : "+ ElkConnectorImpl.QUERY_MAXRECORDS + ",\n" +
					"    \"query\": {\n" +
					"        \"bool\" : {\n" +
					"            \"should\" : [";
		}

		for (Pair<ArrayList<String>,ArrayList<String>> p : filter_s) {
			ArrayList<String> name_s = p.left();
			ArrayList<String> value_s = p.right();

			if (name_s == null || name_s.size() <= 0) {
				if (LOGGER.isWarnEnabled()){
					LOGGER.warn("Defaulting to text search: Empty field name array passed in");
				}
				return search(type, text);
			}

			if (LOGGER.isDebugEnabled()) {
				for (String n: name_s) {
					LOGGER.debug("Filter Name: " + n);
				}
			}

			if (value_s == null || value_s.size() <= 0) {
				if (LOGGER.isWarnEnabled()){
					LOGGER.warn("Defaulting to text search: Empty field value array passed in");
				}
				return search(type, text);
			}

			if (LOGGER.isDebugEnabled()) {
				for (String v: value_s) {
					LOGGER.debug("Filter Value: " + v);
				}
			}

			/* common case: # filter names == # filter values */
			if (name_s.size() == value_s.size()) {
				String match = "";
				for (int i=0; i<name_s.size(); i++) {
					if (name_s.get(i).contains("*")) {
						match =
								"{ \"query_string\": { \"fields\": [ \"" +
										name_s.get(i) + "\" ], " +
										"\"query\" : \"" + 
										value_s.get(i) + "\" } },";
					} else {
						match =
								"{ \"match_phrase\": { \"" + 
										name_s.get(i) + "\" : \"" + 
										value_s.get(i) + "\" } },";
					}
					if (LOGGER.isDebugEnabled()){
						LOGGER.debug("Adding Match Line: " + match);
					}
					matches_s = matches_s + "\n                " + match;
				}
			} 
			else if (name_s.size() > value_s.size()  && (value_s.size() == 1)) {
				String match =
						"{ \"multi_match\": { \"query\": \"" + value_s.get(0) + "\", \"type\": \"phrase\", \"fields\": [";
				for (String n:  name_s) {
					match += " \"" + n + "\",";
				}
				match = match.substring(0, match.length()-1); 
				match += " ] } },";//debug
				if (LOGGER.isDebugEnabled()){
					LOGGER.debug("Adding Match Line: " + match);
				}
				matches_s = matches_s + "\n                " + match;
			} else {
				if (LOGGER.isWarnEnabled())
					LOGGER.warn("Defaulting to text search: different number of filter names and values");
				return search(type, text);
			}
		}		

		matches_s = matches_s.substring(0, matches_s.length()-1);  // remove last comma

		matches_s = matches_s  +
				"            ]\n" +
				"        }\n" +
				"    }\n" +
				"}";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(matches_s);
		}

		Builder searchBuilder = new Search.Builder(matches_s).
				addIndex(ELK_INDEX_POLICY);

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

	@Override
	public JestResult search(PolicyIndexType type, String text, 
			ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s) 
					throws IllegalStateException, IllegalArgumentException {
		if (LOGGER.isTraceEnabled()){
			LOGGER.trace("ENTER: " + text);
		}

		if (filter_s == null || filter_s.size() <= 0) {
			return search(type, text);
		}

		String matches_s = "";
		matches_s = "{\n" +
				"	 " + SOURCE_RESULT_FIELDS + ",\n" +
				"    \"size\" : "+ ElkConnectorImpl.QUERY_MAXRECORDS + ",\n" +
				"    \"query\": {\n" +
				"        \"bool\" : {\n" +
				"            \"must\" : [";
		for (Pair<ArrayList<String>,ArrayList<String>> p : filter_s) {
			ArrayList<String> name_s = p.left();
			ArrayList<String> value_s = p.right();

			if (name_s == null || name_s.size() <= 0) {
				if (LOGGER.isWarnEnabled()){
					LOGGER.warn("Defaulting to text search: Empty field name array passed in");
				}	
				return search(type, text);
			}

			if (LOGGER.isDebugEnabled()) {
				for (String n: name_s) {
					LOGGER.debug("Filter Name: " + n);
				}
			}

			if (value_s == null || value_s.size() <= 0) {
				if (LOGGER.isWarnEnabled())
					LOGGER.warn("Defaulting to text search: Empty field value array passed in");
				return search(type, text);
			}

			if (LOGGER.isDebugEnabled()) {
				for (String v: value_s) {
					LOGGER.debug("Filter Value: " + v);
				}
			}

			/* common case: # filter names == # filter values */
			if (name_s.size() == value_s.size()) {
				String match = "";
				for (int i=0; i<name_s.size(); i++) {
					if (name_s.get(i).contains("*")) {
						match =
								"{ \"query_string\": { \"fields\": [ \"" +
										name_s.get(i) + "\" ], " +
										"\"query\" : \"" + 
										value_s.get(i) + "\" } },";
					} else {
						match =
								"{ \"match_phrase\": { \"" + 
										name_s.get(i) + "\" : \"" + 
										value_s.get(i) + "\" } },";
					}
					if (LOGGER.isDebugEnabled()){
						LOGGER.debug("Adding Match Line: " + match);
					}	
					matches_s = matches_s + "\n                " + match;
				}
			} else if (name_s.size() > value_s.size()  && (value_s.size() == 1)) {
				String match =
						"{ \"multi_match\": { \"query\": \"" + value_s.get(0) + "\", \"type\": \"phrase\", \"fields\": [";
				for (String n:  name_s) {
					match += " \"" + n + "\",";
				}
				match = match.substring(0, match.length()-1); 
				match += " ] } },";	
				if (LOGGER.isDebugEnabled()){
					LOGGER.debug("Adding Match Line: " + match);
				}
				matches_s = matches_s + "\n                " + match;
			} else {
				if (LOGGER.isWarnEnabled()){
					LOGGER.warn("Defaulting to text search: different number of filter names and values");
				}	
				return search(type, text);
			}
		}
		if (text != null && !text.isEmpty()) {
			if (LOGGER.isDebugEnabled()){
				LOGGER.debug("Adding Match Line for search text: " + text);
			}

			final JsonObject jsonText = new JsonObject();
			jsonText.addProperty("_all", text);
			String escapedText = jsonText.toString();

			matches_s = matches_s + "\n                " + 
					"{ \"match\": " +
					escapedText + " },";	
		}
		matches_s = matches_s.substring(0, matches_s.length()-1);  // remove last comma
		matches_s = matches_s + "\n" +
				"            ]\n" +
				"        }\n" +
				"    }\n" +
				"}";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(matches_s);
		}

		Builder searchBuilder = new Search.Builder(matches_s).
				addIndex(ELK_INDEX_POLICY);

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

	@Override
	public JestResult policy(String policyId) 
			throws IllegalStateException, IllegalArgumentException {
		if (LOGGER.isTraceEnabled()){
			LOGGER.trace("ENTER: " + policyId);
		}

		if (policyId == null || policyId.isEmpty()) {
			throw new IllegalArgumentException("No policy id string provided");
		}

		Get policyRequest = new Get.Builder(ELK_INDEX_POLICY, policyId).build();

		if (LOGGER.isInfoEnabled()){
			LOGGER.info("ELK Search body request: " + policyRequest.toString());
		}

		JestResult result;
		try {
			result = jestClient.execute(policyRequest);
		} catch (IOException ioe) {
			LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					policyId + ": " + ioe.getMessage(), ioe);
			throw new IllegalStateException(ioe);
		}

		if (result.isSucceeded()) {
			if (LOGGER.isInfoEnabled()){
				LOGGER.info("OK:" + result.getResponseCode() + ":" + policyId + ":" + 
						result.getPathToResult() + ":" + System.lineSeparator() +
						result.getJsonString());
			}

			return result;
		}

		/* Unsuccessful search */
		if (LOGGER.isWarnEnabled())
			LOGGER.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + 
					result.getResponseCode() + ": " + policyId + ":" +
					result.getPathToResult() + ":" +
					result.getErrorMessage());

		return result;
	}

	protected JsonObject getJsonObject(JsonObject jsonObject, String member) throws IllegalArgumentException {
		if (jsonObject == null) {
			if (LOGGER.isWarnEnabled())
				LOGGER.warn("No JSON object provided to get " + member);

			throw new IllegalArgumentException("No JSON Object provided");
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("ENTER: " + member);
			for (Entry<String, JsonElement> entry: jsonObject.entrySet()) {
				LOGGER.trace("JSONOBJECT: " + entry.getKey() + "->" + entry.getValue());
			}
		}

		if (jsonObject.has(member)) {
			JsonElement element = jsonObject.getAsJsonObject(member);
			if (element.isJsonObject()) {
				return (JsonObject) element;
			}
		}

		throw new IllegalArgumentException(member + " is not a JSON Object");
	}

	protected JsonArray getJsonArray(JsonObject jsonObject, String member) throws IllegalArgumentException {
		if (jsonObject == null) {
			throw new IllegalArgumentException("No JSON Object provided");
		}

		if (jsonObject.has(member)) {
			if (jsonObject.get(member).isJsonArray()) {
				return (JsonArray) jsonObject.get(member);
			}
		}

		throw new IllegalArgumentException(member + " is not a JSON Array");
	}

	protected String getJsonPolicyMember(JsonObject aHit, String member) throws IllegalArgumentException {
		if (aHit == null) {
			throw new IllegalArgumentException("No JSON Object provided");
		}

		JsonObject jSource = getJsonObject(aHit, "_source");
		JsonObject jPolicy = getJsonObject(jSource, "Policy");
		JsonElement jMember = jPolicy.get(member);
		if (jMember == null) {
			throw new IllegalArgumentException(member + " is not a JSON Object");
		}	
		return jMember.getAsString();
	}

	@Override
	public ArrayList<PolicyLocator> policyLocators(PolicyIndexType indexType, String text, int connector) 
			throws IllegalStateException, IllegalArgumentException {
		return policyLocators(indexType, text, new ArrayList<Pair<ArrayList<String>,ArrayList<String>>>(),connector);
	}

	@Override
	public ArrayList<PolicyLocator> policyLocators(PolicyIndexType indexType, 
			String text, 
			ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s, int connector) 
					throws IllegalStateException, IllegalArgumentException {
		final ArrayList<PolicyLocator> policyLocators = new ArrayList<PolicyLocator>();

		JestResult results = searchKey(indexType, text, filter_s,connector);
		if (!results.isSucceeded()) {			
			return policyLocators;
		}

		JsonArray jsonHit_s = null;
		try {
			JsonObject jsonHits = getJsonObject(results.getJsonObject(), "hits");
			jsonHit_s = getJsonArray(jsonHits, "hits");
		} catch (IllegalArgumentException e) {
			LOGGER.warn("SEARCH:" + text + " no valid element provided", e);
			return policyLocators;
		}

		for (JsonElement e : jsonHit_s) {
			JsonObject elkSource = (JsonObject) e;
			try {
				String policyType = getJsonPolicyMember(elkSource,"PolicyType");
				String policyName = getJsonPolicyMember(elkSource,"PolicyName");
				String owner = getJsonPolicyMember(elkSource,"Owner");
				String scope = getJsonPolicyMember(elkSource,"Scope");
				String policyId = getJsonPolicyMember(elkSource,"PolicyId");
				String version = getJsonPolicyMember(elkSource,"Version");	
				PolicyLocator policyLocator = 
						new PolicyLocator(policyType, policyName, owner, 
								scope, policyId, version);
				policyLocators.add(policyLocator);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("SEARCH:" + text + "|FOUND:" + policyLocator);
				}
			} catch (IllegalArgumentException ex) {
				LOGGER.warn("SEARCH:" + text + " missing locator information.", ex);
			}
		}
		return policyLocators;
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
				LOGGER.info("OK: PUT operation of " + "->"  + ": " +
						"success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
						result.getPathToResult() + "]" + System.lineSeparator() +
						result.getJsonString());
		} else {
			if (LOGGER.isWarnEnabled())
				LOGGER.warn("FAILURE: PUT operation of "+ "->" + ": " +
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
					((indexType != null) ? indexType.name() : "null") + ":" + policyData.getNewFileName() + ": " + 
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
