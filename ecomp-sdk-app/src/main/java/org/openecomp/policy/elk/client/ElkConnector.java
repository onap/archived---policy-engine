/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.elk.client;


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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.openecomp.policy.elk.converter.ElkRecord;
import org.openecomp.policy.elk.converter.Xacml2Elk;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;

public interface ElkConnector {
	
	public static final String ELK_URL = "http://localhost:9200";
	public static final String ELK_INDEX_POLICY = "policy";
	
	public enum PolicyIndexType {
		config,
		action,
		decision,
		closedloop,
		all,
	}
	
	public enum PolicyType {
		Config,
		Action,
		Decision,
		Config_Fault,
		Config_PM,
		Config_FW,
		Config_MS,
		none,
	}
	
	public enum PolicyBodyType {
		json,
		xml,
		properties,
		txt,
		none,
	}

	public ElkRecord create(PolicyType type, String name, String owner, String scope,
			              File xacmlFile, PolicyBodyType bodyType, String body,
			              File destinationDir) 
		   throws IllegalStateException;

	public boolean testAndUpdate(File xacmlFile) throws IllegalStateException;

	public JestResult policy(String policyId) 
		   throws IllegalStateException, IllegalArgumentException;

	public boolean clone(String origPolicyId, String clonePolicyId)
			throws IllegalStateException;
	
	public boolean delete(File xacmlFile)
			throws IllegalStateException;

	public ArrayList<PolicyLocator> policyLocators(PolicyIndexType type, String text,int connector)
		   throws IllegalStateException, IllegalArgumentException;
	
	public ArrayList<PolicyLocator> policyLocators(PolicyIndexType type, String text, 
			                                       ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s,int connector)
		   throws IllegalStateException, IllegalArgumentException;

	public JestResult search(PolicyIndexType type, String text) 
		   throws IllegalStateException, IllegalArgumentException;
	
	public JestResult search(PolicyIndexType type, String text, 
			                 ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s) 
			   throws IllegalStateException, IllegalArgumentException;
	
	public boolean update(File xacmlFile) throws IllegalStateException;
	
	public ElkConnector singleton = new ElkConnectorImpl();
	
	public static PolicyIndexType toPolicyIndexType(PolicyType type) 
	       throws IllegalArgumentException {
		if (type == null)
			throw new IllegalArgumentException("Unsupported NULL type conversion");
		
		switch(type) {
		case Config:
			return PolicyIndexType.config;
		case Action:
			return PolicyIndexType.action;
		case Decision:
			return PolicyIndexType.decision;
		case Config_Fault:
			return PolicyIndexType.closedloop;
		case Config_PM:
			return PolicyIndexType.closedloop;
		case Config_FW:
			return PolicyIndexType.config;
		case Config_MS:
			return PolicyIndexType.config;
		case none:
			return PolicyIndexType.all;
		default:
			throw new IllegalArgumentException("Unsupported type conversion to index: " + type.name());
		}
	}
	
	public static PolicyIndexType toPolicyIndexType(String policyName) 
		       throws IllegalArgumentException {
			if (policyName == null)
				throw new IllegalArgumentException("Unsupported NULL policy name conversion");
			
			if (policyName.startsWith("Config_Fault")) {
				return PolicyIndexType.closedloop;
			} else if (policyName.startsWith("Config_PM")) {
				return PolicyIndexType.closedloop;
			} else if (policyName.startsWith("Config_FW")) {
				return PolicyIndexType.config;
			} else if (policyName.startsWith("Config_MS")) {
				return PolicyIndexType.config;
			}else if (policyName.startsWith("Action")) {
				return PolicyIndexType.action;
			} else if (policyName.startsWith("Decision")) {
				return PolicyIndexType.decision;
			} else if (policyName.startsWith("Config")) {
				return PolicyIndexType.config;
			} else {
				throw new IllegalArgumentException
							("Unsupported policy name conversion to index: " + 
				             policyName);
			}
	}
	
	public static PolicyType toPolicyType(String policyName) 
		       throws IllegalArgumentException {
		if (policyName == null)
			throw new IllegalArgumentException("Unsupported NULL policy name conversion to Policy Type");
		
		if (policyName.startsWith("Config_Fault")) {
			return PolicyType.Config_Fault;
		} else if (policyName.startsWith("Config_PM")) {
			return PolicyType.Config_PM;
		} else if (policyName.startsWith("Config_FW")) {
			return PolicyType.Config_FW;
		} else if (policyName.startsWith("Config_MS")) {
			return PolicyType.Config_MS;
		}else if (policyName.startsWith("Action")) {
			return PolicyType.Action;
		} else if (policyName.startsWith("Decision")) {
			return PolicyType.Decision;
		} else if (policyName.startsWith("Config")) {
			return PolicyType.Config;
		} else {
			throw new IllegalArgumentException
						("Unsupported policy name conversion to index: " + 
			             policyName);
		}
	}
	
	public static void main(String args[]) 
		   throws JAXBException, IOException, CmdLineException, IllegalStateException {
		ElkConnectorImpl.CLIOptions cliOptions = new ElkConnectorImpl.CLIOptions();
		CmdLineParser cliParser= new CmdLineParser(cliOptions);		
		try {
			cliParser.parseArgument(args);
		} catch (CmdLineException e) {
			System.out.println("Usage: ElkConnector");
			cliParser.printUsage(System.out);
			throw e;
		}
		
		if (cliOptions.searchText != null && !cliOptions.searchText.isEmpty()) {
			ArrayList<PolicyLocator> locators = 
					ElkConnector.singleton.policyLocators(PolicyIndexType.all, cliOptions.searchText,0);
			for (PolicyLocator l: locators) {
				System.out.println(l);
			}
		} else if (cliOptions.testFile != null && cliOptions.testFile.canRead()) {
			boolean ok = ElkConnector.singleton.testAndUpdate(cliOptions.testFile);
			System.out.println(cliOptions.testFile.getName() + ":" + ok);
		}
	}
}

class ElkConnectorImpl implements ElkConnector {
	
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
	
	private static final Logger logger = FlexLogger.getLogger(ElkConnector.class);

	protected final JestClientFactory jestFactory = new JestClientFactory();
	protected final JestClient jestClient;	
	protected static int QUERY_MAXRECORDS = 1000;
	
	public ElkConnectorImpl() {
		if (logger.isDebugEnabled()) logger.debug("ENTER: -");
		
		HttpClientConfig jestClientConfig = 
				new HttpClientConfig.Builder(ELK_URL).multiThreaded(true).build();
		jestFactory.setHttpClientConfig(jestClientConfig);
		jestClient = jestFactory.getObject();
	}
	
	protected boolean isType(PolicyIndexType type) throws IOException {
		if (logger.isDebugEnabled()) logger.debug("ENTER: -");
		
		try {
			Action<JestResult> typeQuery = new TypeExist.Builder(ELK_INDEX_POLICY).
					                                     addType(type.toString()).
					                                     build();
			JestResult result = jestClient.execute(typeQuery);
			
			if (logger.isInfoEnabled()) {
				logger.info("JSON:" + result.getJsonString());
				logger.info("ERROR:" + result.getErrorMessage());
				logger.info("PATH:" + result.getPathToResult());
				logger.info(result.getJsonObject());
			}
			return result.isSucceeded();	
		} catch (IOException e) {
			logger.warn("Error checking type existance of " + type.toString() +
					    ": " + e.getMessage(), e);
			throw e;
		}
	}
	
	protected boolean isIndex() throws IOException {
		try {
			Action<JestResult> indexQuery = 
					new IndicesExists.Builder(ELK_INDEX_POLICY).build();
			
			JestResult result = jestClient.execute(indexQuery);
			if (logger.isInfoEnabled()) {
				logger.info("JSON:" + result.getJsonString());
				logger.info("ERROR:" + result.getErrorMessage());
				logger.info("PATH:" + result.getPathToResult());
				logger.info(result.getJsonObject());
			}
			return result.isSucceeded();	
		} catch (IOException e) {
			logger.warn("Error checking index existance of " + 
		            ELK_INDEX_POLICY +
				    ": " + e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public JestResult search(PolicyIndexType type, String text) throws IllegalStateException, IllegalArgumentException {
		if (logger.isTraceEnabled())
			logger.trace("ENTER: " + text);
		
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
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
		                search + ": " + ioe.getMessage(), ioe);
			throw new IllegalStateException(ioe);
		}
		
		if (result.isSucceeded()) {
			if (logger.isInfoEnabled())
				logger.info("OK:" + result.getResponseCode() + ":" + search + ": " + 
						    result.getPathToResult() + ":" + System.lineSeparator() +
						    result.getJsonString());
		} else {	
			/* Unsuccessful search */
			if (logger.isWarnEnabled())
				logger.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + 
					            result.getResponseCode() + ": " + 
					            search.getURI() + ":" +
					            result.getPathToResult() + ":" +
					            result.getJsonString() + ":" +
					            result.getErrorMessage());
			
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
				if (logger.isTraceEnabled())
				logger.trace("ENTER: " + text);
				
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
					if (logger.isWarnEnabled())
						logger.warn("Defaulting to text search: Empty field name array passed in");
					return search(type, text);
					}
					
					if (logger.isDebugEnabled()) {
					for (String n: name_s) {
						logger.debug("Filter Name: " + n);
					}
					}
					
					if (value_s == null || value_s.size() <= 0) {
					if (logger.isWarnEnabled())
						logger.warn("Defaulting to text search: Empty field value array passed in");
					return search(type, text);
					}
					
					if (logger.isDebugEnabled()) {
					for (String v: value_s) {
						logger.debug("Filter Value: " + v);
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
							if (logger.isDebugEnabled())
								logger.debug("Adding Match Line: " + match);
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
						//match += " ] } },";	
						match += " ] } },";//debug
						if (logger.isDebugEnabled())
							logger.debug("Adding Match Line: " + match);
						matches_s = matches_s + "\n                " + match;
					} else {
						if (logger.isWarnEnabled())
							logger.warn("Defaulting to text search: different number of filter names and values");
						return search(type, text);
					}
				}		
				
				matches_s = matches_s.substring(0, matches_s.length()-1);  // remove last comma
				
				matches_s = matches_s  +
				   "            ]\n" +
				   "        }\n" +
				   "    }\n" +
				   "}";
				
				if (logger.isDebugEnabled()) {
				logger.debug(matches_s);
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
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				       search + ": " + ioe.getMessage(), ioe);
				throw new IllegalStateException(ioe);
				}
				
				if (result.isSucceeded()) {
					if (logger.isInfoEnabled())
					logger.info("OK:" + result.getResponseCode() + ":" + search + ": " + 
							    result.getPathToResult() + ":" + System.lineSeparator() +
							    result.getJsonString());
				} else {	
					/* Unsuccessful search */
					if (logger.isWarnEnabled())
					logger.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + 
					       result.getResponseCode() + ": " + 
					       search.getURI() + ":" +
					       result.getPathToResult() + ":" +
					       result.getJsonString() + ":" +
					       result.getErrorMessage());
					
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
		if (logger.isTraceEnabled())
			logger.trace("ENTER: " + text);
		
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
				if (logger.isWarnEnabled())
					logger.warn("Defaulting to text search: Empty field name array passed in");
				return search(type, text);
			}
			
			if (logger.isDebugEnabled()) {
				for (String n: name_s) {
					logger.debug("Filter Name: " + n);
				}
			}
			
			if (value_s == null || value_s.size() <= 0) {
				if (logger.isWarnEnabled())
					logger.warn("Defaulting to text search: Empty field value array passed in");
				return search(type, text);
			}
			
			if (logger.isDebugEnabled()) {
				for (String v: value_s) {
					logger.debug("Filter Value: " + v);
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
					if (logger.isDebugEnabled())
						logger.debug("Adding Match Line: " + match);
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
				if (logger.isDebugEnabled())
					logger.debug("Adding Match Line: " + match);
				matches_s = matches_s + "\n                " + match;
			} else {
				if (logger.isWarnEnabled())
					logger.warn("Defaulting to text search: different number of filter names and values");
				return search(type, text);
			}
		}
		if (text != null && !text.isEmpty()) {
			if (logger.isDebugEnabled())
				logger.debug("Adding Match Line for search text: " + text);
			
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
		
		if (logger.isDebugEnabled()) {
			logger.debug(matches_s);
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
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
		                search + ": " + ioe.getMessage(), ioe);
			throw new IllegalStateException(ioe);
		}
		
		if (result.isSucceeded()) {
			if (logger.isInfoEnabled())
				logger.info("OK:" + result.getResponseCode() + ":" + search + ": " + 
						    result.getPathToResult() + ":" + System.lineSeparator() +
						    result.getJsonString());
		} else {	
			/* Unsuccessful search */
			if (logger.isWarnEnabled())
				logger.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + 
			            result.getResponseCode() + ": " + 
			            search.getURI() + ":" +
			            result.getPathToResult() + ":" +
			            result.getJsonString() + ":" +
			            result.getErrorMessage());
			
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
		if (logger.isTraceEnabled())
			logger.trace("ENTER: " + policyId);
		
		if (policyId == null || policyId.isEmpty()) {
			throw new IllegalArgumentException("No policy id string provided");
		}
		
		Get policyRequest = new Get.Builder(ELK_INDEX_POLICY, policyId).build();
		
		if (logger.isInfoEnabled())
			logger.info("ELK Search body request: " + policyRequest.toString());
		
		JestResult result;
		try {
			result = jestClient.execute(policyRequest);
		} catch (IOException ioe) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					policyId + ": " + ioe.getMessage(), ioe);
			throw new IllegalStateException(ioe);
		}
		
		if (result.isSucceeded()) {
			if (logger.isInfoEnabled())
				logger.info("OK:" + result.getResponseCode() + ":" + policyId + ":" + 
						    result.getPathToResult() + ":" + System.lineSeparator() +
						    result.getJsonString());

			return result;
		}
		
		/* Unsuccessful search */
		if (logger.isWarnEnabled())
			logger.warn(XACMLErrorConstants.ERROR_PROCESS_FLOW + ":" + 
				            result.getResponseCode() + ": " + policyId + ":" +
				            result.getPathToResult() + ":" +
				            result.getErrorMessage());
		
		return result;
	}
	
	protected JsonObject getJsonObject(JsonObject jsonObject, String member) throws IllegalArgumentException {
		if (jsonObject == null) {
			if (logger.isWarnEnabled())
				logger.warn("No JSON object provided to get " + member);

			throw new IllegalArgumentException("No JSON Object provided");
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("ENTER: " + member);
			for (Entry<String, JsonElement> entry: jsonObject.entrySet()) {
				logger.trace("JSONOBJECT: " + entry.getKey() + "->" + entry.getValue());
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
			logger.warn("SEARCH:" + text + " no valid element provided", e);
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
				if (logger.isInfoEnabled()) {
					logger.info("SEARCH:" + text + "|FOUND:" + policyLocator);
				}
			} catch (IllegalArgumentException ex) {
				logger.warn("SEARCH:" + text + " missing locator information.", ex);
			}
		}
		return policyLocators;
	}
	
	public boolean put(String record, PolicyType type, String id) 
	       throws IOException, IllegalStateException {
		if (logger.isTraceEnabled()) logger.trace("ENTER");
		
		PolicyIndexType indexType;
		try {
			indexType = ElkConnector.toPolicyIndexType(type);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("ELK: Index: " + ELK_INDEX_POLICY +
                    " Type: " + type + " :" + e.getMessage());			
		}
		
		if (indexType == PolicyIndexType.all) {
			throw new IllegalStateException("ELK: Index: " + ELK_INDEX_POLICY +
                    " Bad Type: " + type.toString());			
		}
		
		if (!isType(indexType)) {
			throw new IllegalStateException("ELK: Index: " + ELK_INDEX_POLICY +
					                        " Type: " + type.toString() + 
			                                " is not configured");
		}
		
		Index elkPut = new Index.Builder(record).
		          index(ELK_INDEX_POLICY).
		          type(indexType.name()).
		          id(id).
		          build();
		
		JestResult result = jestClient.execute(elkPut);
		
		if (result.isSucceeded()) {
			if (logger.isInfoEnabled())
				logger.info("OK: PUT operation of " + type.name() + "->" + indexType + ":" + id + ": " +
						    "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
						    result.getPathToResult() + "]" + System.lineSeparator() +
						    result.getJsonString());
		} else {
			if (logger.isWarnEnabled())
				logger.warn("FAILURE: PUT operation of " + type.name() + "->" + indexType + ":" + id + ": " +
						    "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
						    result.getPathToResult() + "]" + System.lineSeparator() +
						    result.getJsonString());			
			
		}
		
		return result.isSucceeded();
	}
	
	@Override
	public boolean clone(String origPolicyId, String clonePolicyId) 
	        throws IllegalStateException  {
		if (logger.isTraceEnabled()) logger.trace("ENTER");
		
		String methodLog = "[" + 
                "original-policy-id:" + origPolicyId + "|" +
                "cloned-policy-id:" + clonePolicyId + "]";
		
		if (logger.isDebugEnabled())
			logger.debug(methodLog);
		
		if (origPolicyId == null || clonePolicyId == null || 
		    origPolicyId.isEmpty() || clonePolicyId.isEmpty()) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				    "Internal Error: original and cloned policy ids are identical: " + 
				    origPolicyId + "->" + clonePolicyId + " :" +
					methodLog);
			throw new IllegalStateException(": " + "original and cloned policy ids are identical.");
		}
		
		// GET original record
		JestResult result = this.policy(origPolicyId);
		if (!result.isSucceeded()) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				        "Internal Error: not found policy id: " + 
				        origPolicyId + " :" +
					    methodLog);
			throw new IllegalStateException(": " + "policy id: " + origPolicyId + " not found");
		}
		
		try {
			String policyId = getJsonPolicyMember(result.getJsonObject(),"PolicyId");
			String policyType = getJsonPolicyMember(result.getJsonObject(),"PolicyType");
			if (policyType == null || policyType.isEmpty()) {
				throw new IllegalStateException(": " + origPolicyId + 
						                        " invalid policy type: " + policyType);
			}
			PolicyType policyTypeEnum = PolicyType.valueOf(policyType);
			String newPolicyId = policyId.replace(origPolicyId, clonePolicyId);
			
			JsonObject jsonSource = getJsonObject(result.getJsonObject(), "_source");
			JsonObject jsonPolicy = getJsonObject(jsonSource, "Policy");
			jsonPolicy.addProperty("PolicyId", newPolicyId);
			String sourcePolicy = new Gson().toJson(jsonPolicy);
			return put(sourcePolicy, policyTypeEnum, clonePolicyId);
		} catch (IllegalArgumentException e) {
			logger.warn("POLICY-SEARCH:" + origPolicyId + " not properly found", e);
			throw new IllegalStateException(": " + origPolicyId + " not found in ELK");
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					    "cannot create searchable record for " + methodLog +
					    ".  Reason: " + e.getMessage(), e);
			throw new IllegalStateException(": Communication Problem with ELK server");
		}
	}
	
	@Override
	public boolean delete(File xacmlFile) throws IllegalStateException  {
		if (logger.isDebugEnabled())
			logger.debug("ENTER: " + "[xacml-file:" + 
						((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "]");
		
		if (xacmlFile == null || !xacmlFile.canRead()) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				    "Internal Error: invalid arguments provided: " + 
					((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "]");
			throw new IllegalStateException(": " + "Invalid arguments to convert to ELK format.");
		}
		
		String policyId = "";
		PolicyIndexType indexType = null;
		JestResult result;
		try {
			indexType = ElkConnector.toPolicyIndexType(xacmlFile.getName());
			if (!isType(indexType)) {
				throw new IllegalStateException("ELK: Index: " + ELK_INDEX_POLICY +
						                        " Type: " + indexType + 
				                                " is not configured");
			}
			Xacml2Elk searchablePolicy = new Xacml2Elk(xacmlFile, true);
			policyId = searchablePolicy.getPolicy().getValue().getPolicyId();
			policyId = policyId.substring(policyId.lastIndexOf(":")+1);
			Delete deleteRequest = 
					new Delete.Builder(policyId).index(ELK_INDEX_POLICY).
					           type(indexType.name()).build();
			result = jestClient.execute(deleteRequest);
		} catch (IllegalArgumentException | IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ": delete:" + 
					    ((indexType != null) ? indexType.name() : "null") + ":" + policyId + ": " + 
					    e.getMessage(), e);
			throw new IllegalStateException(e);
		}
		
		if (result.isSucceeded()) {
			if (logger.isInfoEnabled())
				logger.info("OK: DELETE operation of " + indexType + ":" + policyId + ": " +
						    "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
						    result.getPathToResult() + "]" + System.lineSeparator() +
						    result.getJsonString());
		} else {
			if (logger.isWarnEnabled())
				logger.warn("FAILURE: DELETE operation of " + indexType + ":" + policyId + ": " +
						    "success=" + result.isSucceeded() + "[" + result.getResponseCode() + ":" +
						    result.getPathToResult() + "]" + System.lineSeparator() +
						    result.getJsonString());	
		}
		
		return result.isSucceeded();
	}
	
	@Override
	public ElkRecord create(PolicyType policyType, 
			              String name, 
			              String owner, 
			              String scope, 
			              File xacmlFile, 
			              PolicyBodyType bodyType,
			              String body,
			              File destinationDir) 
	        throws IllegalStateException  {
		if (logger.isTraceEnabled()) logger.trace("ENTER");
		
		String methodLog = "[" + 
                "type:" + policyType.name() + "|" +
                "owner:" + owner + "|" +	
                "scope:" + scope + "|" +
                "xacml-file:" + ((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "|" +	
                "body-type:" + bodyType.name() + "|" +
                "body:" + body + "|" +
                "destination-dir:" + ((destinationDir != null) ? destinationDir.getPath() : "null")+ "]";
		
		if (logger.isDebugEnabled())
			logger.debug(methodLog);
		
		if (policyType == null || name == null || owner == null || scope == null ||
			xacmlFile == null) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				    "Internal Error: invalid arguments provided for " + methodLog);
			throw new IllegalStateException(": " + "Invalid arguments to convert to ELK format.");
		}
		
		try {
			Xacml2Elk searchablePolicy =
					new Xacml2Elk(policyType.name(), 
								  name, 
								  owner, 
							      scope, 
							      xacmlFile, 
							      bodyType,
							      body,
							      destinationDir);
			ElkRecord elkRecord = searchablePolicy.record();
			put(elkRecord.record, policyType, elkRecord.policyId);	
			return elkRecord;
		} catch (JAXBException | JsonProcessingException | IllegalArgumentException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					    "cannot create searchable record for " + methodLog +
					    ".  Reason: " + e.getMessage(), e);
			throw new IllegalStateException(": " + "Error encountered converting to ELK format.");
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					    "cannot create searchable record for " + methodLog +
					    ".  Reason: " + e.getMessage(), e);
			throw new IllegalStateException(": " + "Communication Problem with ELK server.");
		}
	}
	
	@Override
	public boolean update(File xacmlFile) throws IllegalStateException  {		
		if (logger.isDebugEnabled())
			logger.debug("ENTER: " + "[xacml-file:" + 
						((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "]");
		
		if (xacmlFile == null || !xacmlFile.canRead()) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				    "Internal Error: invalid arguments provided: " + 
					((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "]");
			throw new IllegalStateException(": " + "Invalid arguments to convert to ELK format.");
		}
		
		Xacml2Elk searchablePolicy = new Xacml2Elk(xacmlFile, false);
		return update(xacmlFile, searchablePolicy);
	}
	
	protected boolean update(File xacmlFile, Xacml2Elk searchablePolicy) throws IllegalStateException  {		
		if (logger.isDebugEnabled())
			logger.debug("ENTER");

		try {
			ElkRecord elkRecord = searchablePolicy.record();
			boolean success = put(elkRecord.record, ElkConnector.toPolicyType(xacmlFile.getName()), elkRecord.policyId);	
			return success;
		} catch (JAXBException | JsonProcessingException | IllegalArgumentException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					    "cannot create searchable record for " + xacmlFile.getAbsolutePath() +
					    ".  Reason: " + e.getMessage(), e);
			throw new IllegalStateException(": " + "Error encountered converting to ELK format for " +
					                        xacmlFile.getAbsolutePath());
		} catch (IOException e) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
					    "cannot create ELK searchable record for " + xacmlFile.getAbsolutePath() +
					    ".  Reason: " + e.getMessage(), e);
			throw new IllegalStateException(": " + "Communication Problem with ELK server.");
		} catch (IllegalStateException e) {
			/* unexpected */
			throw e;			
		} catch (Exception e) {
			logger.warn(XACMLErrorConstants.ERROR_UNKNOWN + ":" + "cannot test and update", e);
			throw new IllegalStateException(e);			
		}
	}
	
	@Override
	public boolean testAndUpdate(File xacmlFile) throws IllegalStateException  {		
		if (logger.isDebugEnabled())
			logger.debug("ENTER: " + "[xacml-file:" + 
						((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "]");
		
		if (xacmlFile == null || !xacmlFile.canRead()) {
			logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + ":" + 
				    "Internal Error: invalid arguments provided: " + 
					((xacmlFile != null) ? xacmlFile.getPath() : "null")+ "]");
			throw new IllegalStateException(": " + "Invalid arguments to convert to ELK format.");
		}
		
		try {
			Xacml2Elk searchablePolicy = new Xacml2Elk(xacmlFile, true);
			String policyId = searchablePolicy.getPolicy().getValue().getPolicyId();
			policyId = policyId.substring(policyId.lastIndexOf(":")+1);
			JestResult result = this.policy(policyId);
			if (result.isSucceeded()) {
				logger.info("Policy exists: " + policyId);
			
				/* validation tests */
				
				String policyType = getJsonPolicyMember(result.getJsonObject(), "PolicyType");
				String scope = getJsonPolicyMember(result.getJsonObject(), "Scope");
				String policyName = getJsonPolicyMember(result.getJsonObject(), "PolicyName");
				if (policyType == null || policyType.isEmpty() || 
					scope == null || scope.isEmpty() || 
				    policyName == null || policyName.isEmpty()) {
					logger.warn("Policy metadata not found.  Updating record ..");
					update(xacmlFile, searchablePolicy);
					return false;
				} 
				
				if (!xacmlFile.getName().startsWith(policyType)) {
					logger.warn(xacmlFile.getName() + " does not match Policy Type: " + 
				                policyType);
					update(xacmlFile, searchablePolicy);
					return false;
				}				
				
				java.nio.file.Path xacmlElkPath = Paths.get(scope, policyType + "_" + policyName + ".xml");
				java.nio.file.Path xacmlPath = xacmlFile.toPath();
				
				if (logger.isDebugEnabled()) {
					logger.debug(xacmlElkPath + " in " +  xacmlElkPath + "? ");
				}
				
				if (!xacmlPath.endsWith(xacmlElkPath)) {
					logger.warn(xacmlPath + " does not match ELK inferred path: " + 
							    xacmlElkPath);
					update(xacmlFile, searchablePolicy);
					return false;
				}
				
				if (logger.isInfoEnabled()) {
					logger.warn("OK: " + xacmlPath + " matches ELK inferred path: " + 
							    xacmlElkPath);
				}
				return true;
			} else {
				logger.info("Policy ID not found.  Adding to database: " + policyId);
				update(xacmlFile, searchablePolicy);
				return false;
			}
		} catch (Exception e) {
			logger.warn(XACMLErrorConstants.ERROR_UNKNOWN + ":" + "cannot test and update", e);
			throw new IllegalStateException(e);
		}
	}
}