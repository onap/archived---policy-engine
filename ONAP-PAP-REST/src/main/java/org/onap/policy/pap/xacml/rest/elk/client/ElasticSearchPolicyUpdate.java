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
package org.onap.policy.pap.xacml.rest.elk.client;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.utils.CryptoUtils;
import org.onap.policy.xacml.util.XACMLPolicyScanner;

import com.google.gson.Gson;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;



/**
 * This code will deals with parsing the XACML content on reading from 
 * database(PolicyEntity, ConfigurationDataEntity and ActionBodyEntity tables)
 * and convert the data into json to do bulk operation on putting to elastic search database.
 * Which is used to support Elastic Search in Policy Application GUI to search policies.
 * 
 * 
 * 
 * properties should be configured in policyelk.properties
 *
 */
public class ElasticSearchPolicyUpdate {
	
	private static final Logger LOGGER = FlexLogger.getLogger(ElasticSearchPolicyUpdate.class);
	protected final static JestClientFactory jestFactory = new JestClientFactory();
	
	public static void main(String[] args) {
		
		String elkURL = null;
		String databseUrl = null;
		String userName = null;
		String txt = null;
		String databaseDriver = null; 
		
		String propertyFile = System.getProperty("PROPERTY_FILE");
		Properties config = new Properties();
		Path file = Paths.get(propertyFile);
		if(!file.toFile().exists()){
			LOGGER.error("Config File doesn't Exist in the specified Path " + file.toString());
		}else{
			if(file.toString().endsWith(".properties")){
				try {
					InputStream in = new FileInputStream(file.toFile());
					config.load(in);
					elkURL = config.getProperty("policy.elk.url");
					databseUrl = config.getProperty("policy.database.url");
					userName = config.getProperty("policy.database.username");
					txt = CryptoUtils.decryptTxtNoExStr(config.getProperty("policy.database.password"));
					databaseDriver = config.getProperty("policy.database.driver");
					if(elkURL == null || databseUrl == null || userName == null || txt == null || databaseDriver == null){
						LOGGER.error("please check the elk configuration");
					}
				} catch (Exception e) {
					LOGGER.error("Config File doesn't Exist in the specified Path " + file.toString(),e);
				} 
			}
		}

		Builder bulk = null;
		
		HttpClientConfig httpClientConfig = new HttpClientConfig.Builder(elkURL).multiThreaded(true).build();
		jestFactory.setHttpClientConfig(httpClientConfig);
	    JestHttpClient client = (JestHttpClient) jestFactory.getObject();
	    
		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		
		List<Index> listIndex = new ArrayList<>();
		
		try {
			Class.forName(databaseDriver);
			conn = DriverManager.getConnection(databseUrl, userName, txt);
			stmt = conn.createStatement();
			
			String policyEntityQuery = "Select * from PolicyEntity";
			result = stmt.executeQuery(policyEntityQuery);
			
			while(result.next()){
				StringBuilder policyDataString = new StringBuilder("{");
				String scope = result.getString("scope");
				String policyName = result.getString("policyName");
				if(policyName != null){
					policyDataString.append("\"policyName\":\""+scope+"."+policyName+"\",");
				}
				String description = result.getString("description");
				if(description != null){
					policyDataString.append("\"policyDescription\":\""+description+"\",");
				}
				Object policyData = result.getString("policydata");
				
				if(scope != null){
					policyDataString.append("\"scope\":\""+scope+"\",");
				}
				String actionbodyid = result.getString("actionbodyid");
				String configurationdataid = result.getString("configurationdataid");
				
				
				String policyWithScopeName = scope + "." + policyName;
				String _type = null;
				
				if(policyWithScopeName.contains(".Config_")){
					policyDataString.append("\"policyType\":\"Config\",");
					if(policyWithScopeName.contains(".Config_Fault_")){
						_type = "closedloop";
						policyDataString.append("\"configPolicyType\":\"ClosedLoop_Fault\",");
					}else if(policyWithScopeName.contains(".Config_PM_")){
						_type = "closedloop";
						policyDataString.append("\"configPolicyType\":\"ClosedLoop_PM\",");
					}else{
						_type = "config";
						policyDataString.append("\"configPolicyType\":\"Base\",");
					}
				}else if(policyWithScopeName.contains(".Action_")){
					_type = "action";
					policyDataString.append("\"policyType\":\"Action\",");
				}else if(policyWithScopeName.contains(".Decision_MS_")){
					_type = "decision_ms";
					policyDataString.append("\"policyType\":\"Decision\",");
				}
				else if(policyWithScopeName.contains(".Decision_")){
					_type = "decision";
					policyDataString.append("\"policyType\":\"Decision\",");
				}
				
				if(!"decision".equals(_type)){
					if(configurationdataid != null){
					    updateConfigData(conn, configurationdataid, policyDataString);
					}
					if(actionbodyid != null){
					    updateActionData(conn, actionbodyid, policyDataString);
					}	
				}
				
				String _id = policyWithScopeName;
				
				String dataString = constructPolicyData(policyData, policyDataString);
				dataString = dataString.substring(0, dataString.length()-1);
				dataString = dataString.trim().replace(System.getProperty("line.separator"), "") + "}";
				dataString = dataString.replace("null", "\"\"");
				dataString = dataString.replaceAll("\n", "");
				
				try{
					Gson gson = new Gson();
					gson.fromJson(dataString, Object.class);
				}catch(Exception e){
					LOGGER.error(e);
					continue;
				}
				
				if("config".equals(_type)){
					listIndex.add(new Index.Builder(dataString).index("policy").type("config").id(_id).build());
				}else if("closedloop".equals(_type)){
					listIndex.add(new Index.Builder(dataString).index("policy").type("closedloop").id(_id).build());
				}else if("action".equals(_type)){
					listIndex.add(new Index.Builder(dataString).index("policy").type("action").id(_id).build());
				}else if("decision".equals(_type) || "decision_ms".equals(_type)){
					listIndex.add(new Index.Builder(dataString).index("policy").type("decision").id(_id).build());
				}
			}
			
			result.close();
			bulk = new Bulk.Builder();
			for(int i =0; i < listIndex.size(); i++){
				bulk.addAction(listIndex.get(i));
			}
			BulkResult searchResult = client.execute(bulk.build());
			if(searchResult.isSucceeded()){
				LOGGER.debug("Success");
			}else{
				LOGGER.error("Failure");
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured while performing database Operation for Elastic Search Policy Upgrade"+e);
		}finally{
		        if(result != null){
		            try {
		                result.close();
		            } catch (Exception e) {
		                LOGGER.error("Exception Occured while closing the resultset"+e);
		            }
		        }
		        if(stmt != null){
		            try {
		                stmt.close();
		            } catch (Exception e) {
		                LOGGER.error("Exception Occured while closing the statement"+e);
		            }
		        }
			if(conn != null){
				try {
					conn.close();
				} catch (Exception e) {
					LOGGER.error("Exception Occured while closing the connection"+e);
				}
			}
		}
	}
	
	public static String constructPolicyData(Object policyContent, StringBuilder policyDataString){
		InputStream stream = new ByteArrayInputStream(policyContent.toString().getBytes(StandardCharsets.UTF_8));
		Object policyData = XACMLPolicyScanner.readPolicy(stream);
		if(policyData instanceof PolicyType){
			PolicyType policy = (PolicyType) policyData;
			TargetType target = policy.getTarget();
			if (target != null) {
				// Under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AnyOFType we have AllOFType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attribute value and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();
										// First match in the target is OnapName, so set that value.
										if ("ONAPName".equals(attributeId)) {
											policyDataString.append("\"onapName\":\""+value+"\",");
										}
										if ("RiskType".equals(attributeId)){
											policyDataString.append("\"riskType\":\""+value+"\",");
										}
										if ("RiskLevel".equals(attributeId)){
											policyDataString.append("\"riskLevel\":\""+value+"\",");
										}
										if ("guard".equals(attributeId)){
											policyDataString.append("\"guard\":\""+value+"\",");
										}
										if ("ConfigName".equals(attributeId)){
											policyDataString.append("\"configName\":\""+value+"\",");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return policyDataString.toString();
	}
	
	private static void updateConfigData(Connection conn, String configurationdataid, StringBuilder policyDataString) throws Exception {

	        PreparedStatement pstmt = null;
	        ResultSet configResult = null;
	        try {
	            String configEntityQuery = "Select * from ConfigurationDataEntity where configurationDataId = ?";
	            pstmt = null;
	            pstmt = conn.prepareStatement(configEntityQuery);
	            pstmt.setString(1, configurationdataid);
	            configResult = pstmt.executeQuery();
	            while(configResult.next()){
	                String configBody = configResult.getString("configbody");
	                String configType = configResult.getString("configtype");
	                if(configBody!=null){
	                    configBody = configBody.replace("null", "\"\"");
	                    configBody= configBody.replace("\"", "\\\"");
	                    policyDataString.append("\"jsonBodyData\":\""+configBody+"\",\"configType\":\""+configType+"\",");
	                }
	            }
	        } catch(Exception e) {
	            LOGGER.error("Exception Occured while updating configData"+e);
	            throw(e);
	        } finally {
	            if(configResult != null){
	                try {
	                    configResult.close();
	                } catch (Exception e) {
	                    LOGGER.error("Exception Occured while closing the ResultSet"+e);
	                }
	            }
	            if(pstmt != null){
	                try {
	                    pstmt.close();
	                } catch (Exception e) {
	                    LOGGER.error("Exception Occured while closing the PreparedStatement"+e);
	                }
	            }
	       }
	}
	
	private static void updateActionData(Connection conn, String actionbodyid, StringBuilder policyDataString) throws Exception {

            PreparedStatement pstmt = null;
            ResultSet actionResult = null;
            try {
                String actionEntityQuery = "Select * from ActionBodyEntity where actionBodyId = ?";
                pstmt = conn.prepareStatement(actionEntityQuery);
                pstmt.setString(1, actionbodyid);
                actionResult = pstmt.executeQuery();
                while(actionResult.next()){
                    String actionBody = actionResult.getString("actionbody");
                    actionBody = actionBody.replace("null", "\"\"");
                    actionBody = actionBody.replace("\"", "\\\"");
                    policyDataString.append("\"jsonBodyData\":\""+actionBody+"\",");
                }
            } catch(Exception e) {
                LOGGER.error("Exception Occured while updating actionData"+e);
                throw(e);
            } finally {
                if(actionResult != null){
                    try {
                        actionResult.close();
                    } catch (Exception e) {
                        LOGGER.error("Exception Occured while closing the ResultSet"+e);
                    }
                }
                if(pstmt != null){
                    try {
                        pstmt.close();
                    } catch (Exception e) {
                        LOGGER.error("Exception Occured while closing the PreparedStatement"+e);
                    }
                }
           }
	}
}
