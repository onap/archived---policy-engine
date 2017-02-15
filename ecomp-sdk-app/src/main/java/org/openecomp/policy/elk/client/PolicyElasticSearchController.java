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


import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.adapter.ClosedLoopPerformanceMetrics;
import org.openecomp.policy.adapter.ClosedLoopPolicy;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.elk.client.ElkConnector.PolicyIndexType;
import org.openecomp.policy.rest.dao.DescriptiveScopeDao;
import org.openecomp.policy.rest.jpa.ActionPolicyDict;
import org.openecomp.policy.rest.jpa.Attribute;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
import org.openecomp.policy.rest.jpa.ClosedLoopD2Services;
import org.openecomp.policy.rest.jpa.ClosedLoopSite;
import org.openecomp.policy.rest.jpa.DCAEuuid;
import org.openecomp.policy.rest.jpa.DecisionSettings;
import org.openecomp.policy.rest.jpa.DescriptiveScope;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.EnforcingType;
import org.openecomp.policy.rest.jpa.GroupPolicyScopeList;
import org.openecomp.policy.rest.jpa.MicroServiceLocation;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PEPOptions;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.RiskType;
import org.openecomp.policy.rest.jpa.SafePolicyWarning;
import org.openecomp.policy.rest.jpa.TermList;
import org.openecomp.policy.rest.jpa.VNFType;
import org.openecomp.policy.rest.jpa.VSCLAction;
import org.openecomp.policy.rest.jpa.VarbindDictionary;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping({"/"})
public class PolicyElasticSearchController extends RestrictedBaseController{

	private static final Logger logger = FlexLogger.getLogger(PolicyElasticSearchController.class);
	private volatile HashMap<Path, String> filteredPolicies = new HashMap<Path, String>();
	private List<JSONObject> policyNames = null;

	enum Mode{
		attribute, ecompName, actionPolicy, brmsParam, pepOptions, clSite, clService, clVarbind, clVnf, clVSCL, decision, enforcer, fwTerm, gocEventAlarm,
		gocTraversal, gocRootCause, gocVnfType, gocServerScope, gocHPEventSource, msDCAEUUID, msConfigName, msLocation, msModels,
		psGroupPolicy, safeRisk, safePolicyWarning
	}
	
	public static final HashMap<String, String> name2jsonPath = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_TRINITY, ClosedLoopPolicy.CLFAULT_UIJSON_D2_SERVICES_TRINITY);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_VUSP, ClosedLoopPolicy.CLFAULT_UIJSON_D2_SERVICES_VUSP);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_MCR, ClosedLoopPolicy.CLFAULT_UIJSON_D2_SERVICES_MCR);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_GAMMA, ClosedLoopPolicy.CLFAULT_UIJSON_D2_SERVICES_GAMMA);	
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_VDNS, ClosedLoopPolicy.CLFAULT_UIJSON_D2_SERVICES_VDNS);

			put(ClosedLoopPolicy.CLFAULT_UIFIELD_EMAIL_ADDRESS, ClosedLoopPolicy.CLFAULT_UIJSON_EMAIL_ADDRESS);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_TRIGGER_SIGNATURE, ClosedLoopPolicy.CLFAULT_UIJSON_TRIGGER_SIGNATURE);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_VERIFICATION_SIGNATURE, ClosedLoopPolicy.CLFAULT_UIJSON_VERIFICATION_SIGNATURE);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_CONNECT_ALL_TRAPS, ClosedLoopPolicy.CLFAULT_UIJSON_CONNECT_ALL_TRAPS);	
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_CONNECT_ALL_FAULTS, ClosedLoopPolicy.CLFAULT_UIJSON_CONNECT_ALL_FAULTS);

			put(ClosedLoopPolicy.CLFAULT_UIFIELD_POLICY_STATUS_INACTIVE, ClosedLoopPolicy.CLFAULT_UIJSON_POLICY_STATUS_ACTIVE);
			put(ClosedLoopPolicy.CLFAULT_UIFIELD_POLICY_STATUS_ACTIVE, ClosedLoopPolicy.CLFAULT_UIJSON_POLICY_STATUS_INACTIVE);

			put(ClosedLoopPerformanceMetrics.CLPM_UIFIELD_ONSET_MESSAGE, ClosedLoopPerformanceMetrics.CLPM_UIJSON_ONSET_MESSAGE);
			put(ClosedLoopPerformanceMetrics.CLPM_UIFIELD_POLICY_NAME, ClosedLoopPerformanceMetrics.CLPM_UIJSON_POLICY_NAME);
			put(ClosedLoopPerformanceMetrics.CLPM_UIFIELD_ABATEMENT_MESSAGE, ClosedLoopPerformanceMetrics.CLPM_UIJSON_ABATEMENT_MESSAGE);
			put(ClosedLoopPerformanceMetrics.CLPM_UIFIELD_GEOLINK, ClosedLoopPerformanceMetrics.CLPM_UIJSON_GEOLINK);	
		}};


		//For AND and OR logical connector AND=0 and OR=1
		private int connectorSelected;

		public static DescriptiveScopeDao descriptiveScopeDao;
		public static PolicyVersionDao policyVersionDao;

		@Autowired
		public PolicyElasticSearchController(DescriptiveScopeDao descriptiveScopeDao, PolicyVersionDao policyVersionDao) {
			PolicyElasticSearchController.descriptiveScopeDao = descriptiveScopeDao;
			PolicyElasticSearchController.policyVersionDao = policyVersionDao;

		}

		public PolicyElasticSearchController() {
		}

		@RequestMapping(value={"/searchPolicy"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
		public ModelAndView searchPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
			List<JSONObject> resultList = new ArrayList<JSONObject>();
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JsonNode root = mapper.readTree(request.getReader());
				SearchData searchData = (SearchData)mapper.readValue(root.get("searchdata").toString(), SearchData.class);

				String policyType = searchData.getPolicyType();
				ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s = new ArrayList<Pair<ArrayList<String>,ArrayList<String>>>();

				String searchText = searchData.getQuery();
				if (searchText == null || searchText.isEmpty()) {
					String descriptiveValue = searchData.getDescriptiveScope();
					if(descriptiveValue != null){
						searchText = "Descriptive-Scope="+descriptiveValue;
					}
					if (policyType == null || policyType.isEmpty() &&
							!policyType.equals(ElkConnector.PolicyIndexType.closedloop.toString())) {
						if (logger.isDebugEnabled()) {
							logger.debug("Clearing search filters, nothing to search and not closed loop.");
						}					
					}
				} else {
					searchText = searchText.trim();
					//Descriptive Scope. 
					/*
				  When a item is selected in the "descriptiveScope" comboBox, the name of the item 
				  is added to the Search-Text Box with the prefix "Descriptive-Scope"
				  User needs to press the "Search" button to perform the search. 
					 */
					if(searchText.contains("Descriptive-Scope=")){ 
						if (logger.isDebugEnabled()) {
							logger.debug("Inside the Descriptive Scope");
						}
						/*
					 	First item is always String "Descriptive-Scope" before the "=",
						So taking the second item of "split using ="
						 */
						String[] dsName= searchText.split("=",2);
						/*
					 	Trying to find the search String by traversing different items from the dictionary by Scope-Name
					 	Once when the the "scope-name" is found, we get the search string from dictionary. 
						 */
						if(searchData.getDescriptiveScope() != null){
							DescriptiveScope dsSearch = descriptiveScopeDao.getDescriptiveScopeById(searchData.getDescriptiveScope());
							if(dsSearch.getScopeName().equals(dsName[1])){
								searchText=dsSearch.getSearch();
								if (logger.isDebugEnabled()) {
									logger.debug("DescriptiveScope Search String is " +searchText );
								}
							}
						}

					}
					// '&' turned to "AND" to make it inline with Freeform search. 
					if(searchText.contains(":")){
						String connector="AND";
						if(searchText.contains("AND")){
							connector="AND";
							connectorSelected=0;
						}else if(searchText.contains("OR")){
							connector=Pattern.quote("OR");
							connectorSelected=1;
						}
						for (String retval: searchText.split(connector)){

							int index= retval.indexOf(':');
							String filterKey=null;
							String filterValue=null;

							filterKey=retval.substring(0,index).trim();
							filterValue= retval.substring(index+1).trim();

							logger.debug("Key is "+filterKey+" and value is "+filterValue);
							String clSearchBoxFilter=filterKey;

							ArrayList<String> clSearchBoxFilterField_s = new ArrayList<String>();

							clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_Fault.name() + "_Body." + clSearchBoxFilter);
							clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_PM.name() + "_Body." + clSearchBoxFilter);
							clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_FW.name() + "_Body." + clSearchBoxFilter);
							clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_MS.name() + "_Body." + clSearchBoxFilter);


							ArrayList<String> clSearchBoxFilterValue_s = new ArrayList<String>();
							clSearchBoxFilterValue_s.add(filterValue);

							filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clSearchBoxFilterField_s, clSearchBoxFilterValue_s));
						}
					}
				}

				if (policyType != null && !policyType.isEmpty() && 
						policyType.equals(ElkConnector.PolicyIndexType.closedloop.toString())) {

					/* closed loop policy type */

					String clPolicyType = searchData.getClosedLooppolicyType();
					if (clPolicyType != null && !clPolicyType.isEmpty()) {							
						ArrayList<String> clPolicyTypeField_s = new ArrayList<String>();
						clPolicyTypeField_s.add("Policy.PolicyType");

						ArrayList<String> clPolicyTypeValue_s = new ArrayList<String>();
						clPolicyTypeValue_s.add(clPolicyType);

						filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clPolicyTypeField_s, clPolicyTypeValue_s));
					}

					String clEcompName = searchData.getEcompName();
					if (clEcompName != null && !clEcompName.isEmpty()) {
						clSearchBody(clPolicyType, "ecompname", clEcompName, filter_s);
					}

					String clD2Services = searchData.getD2Service();
					if (clD2Services != null && !clD2Services.isEmpty()) {
						switch (clD2Services) {
						case ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_TRINITY:
						case ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_VUSP:						
						case ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_MCR:						
						case ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_GAMMA:							
						case ClosedLoopPolicy.CLFAULT_UIFIELD_D2_SERVICES_VDNS:	
							clSearchBody(clPolicyType, name2jsonPath.get(clD2Services), "true", filter_s);
							break;
						default:
							if (logger.isWarnEnabled())
								logger.warn("Unexpected D2 Service: " + clD2Services);
							break;
						}
					}

					String clFaultAction = searchData.getVproAction();
					if (clFaultAction != null && !clFaultAction.isEmpty()) {
						if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_Fault.name())) {
							clSearchFilter(ElkConnector.PolicyType.Config_Fault.name(), "actions", clFaultAction, filter_s);
						}
					}

					String clFaultStatus = searchData.getPolicyStatus();
					if (clFaultStatus != null && !clFaultStatus.isEmpty()) {
						if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_Fault.name())) {
							clSearchFilter(ElkConnector.PolicyType.Config_Fault.name(), "closedLoopPolicyStatus", clFaultStatus, filter_s);
						}
					}

					String clFaultVnfTypes = searchData.getVnfType();
					if (clFaultVnfTypes != null && !clFaultVnfTypes.isEmpty()) {
						if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_Fault.name())) {
							clSearchFilter(ElkConnector.PolicyType.Config_Fault.name(), "vnfType", clFaultVnfTypes, filter_s);
						}
					}

					String clPMServiceType = searchData.getServiceType();
					if (clPMServiceType != null && !clPMServiceType.isEmpty()) {
						if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_PM.name())) {
							clSearchFilter(ElkConnector.PolicyType.Config_PM.name(), "serviceTypePolicyName", clPMServiceType, filter_s);
						}
					}

					String clSearchBoxFilter = searchData.getBindTextSearch();
					if (clSearchBoxFilter != null && !clSearchBoxFilter.isEmpty() && 
							searchText != null && !searchText.isEmpty()) {

						if (name2jsonPath.containsKey(clSearchBoxFilter)) {
							clSearchBoxFilter = name2jsonPath.get(clSearchBoxFilter);
						}						

						ArrayList<String> clSearchBoxFilterField_s = new ArrayList<String>();
						if (clPolicyType == null || clPolicyType.isEmpty()) {
							clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_Fault.name() + "_Body." + clSearchBoxFilter);
							clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_PM.name() + "_Body." + clSearchBoxFilter);
						} else {
							clSearchBoxFilterField_s.add("Policy.Body." + clPolicyType + "_Body." + clSearchBoxFilter);
						}

						ArrayList<String> clSearchBoxFilterValue_s = new ArrayList<String>();
						clSearchBoxFilterValue_s.add(searchText);

						filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clSearchBoxFilterField_s, clSearchBoxFilterValue_s));

						// deactivate search all fields in case a searchbox filter is provided
						searchText = "";
					}
				}

				if ((searchText == null || searchText.isEmpty()) && 
						(filter_s == null || filter_s.size() <=0) ) {
					if (logger.isWarnEnabled()) {
						logger.warn("Clearing search filters, closed loop but nothing to search nor filters");
					}
				}

				ArrayList<PolicyLocator> locators = null;
				try {
					locators = ElkConnector.singleton.policyLocators(toPolicyIndexType(policyType), 
							searchText, filter_s,connectorSelected);	
				} catch (Exception ise) {
					logger.warn("Search is unavailable: " + ise.getMessage());
				}

				synchronized(this.filteredPolicies) {
					if (locators.isEmpty()) {
						if (logger.isInfoEnabled()) {
							logger.info("No match has been found");
						}
						logger.warn("No match has been found");
					}

					HashMap<String, Boolean> policyVersion_s = new HashMap<String, Boolean>();
					List<PolicyVersion> policyVersionList = policyVersionDao.getPolicyVersionData();
					for(int i = 0; i < policyVersionList.size(); i++) {
						PolicyVersion entityVersion = policyVersionList.get(i);
						String dbPolicy = entityVersion.getPolicyName() + "." + entityVersion.getActiveVersion();
						policyVersion_s.put(dbPolicy, true);
						if (logger.isDebugEnabled())
							logger.debug("Map addition: DB Policy Name: " + dbPolicy);
					}

					this.filteredPolicies.clear();
					for (PolicyLocator p: locators) {
						String dbPolicyName = p.scope + File.separator  + p.policyType + "_" + p.policyName;
						if (policyVersion_s.containsKey(dbPolicyName)) {
							String filterPolicyName = dbPolicyName + ".xml";
							this.filteredPolicies.put(Paths.get(filterPolicyName), filterPolicyName);
							JSONObject el = new JSONObject();
							el.put("name", dbPolicyName);	
							resultList.add(el);
							if (logger.isInfoEnabled())
								logger.info("Active Version Policy found in search: " + dbPolicyName + " -> " + filterPolicyName);
						} else {
							if (logger.isInfoEnabled()) 
								logger.info("Inactive Version Policy found in search: " + dbPolicyName);						
						}
					}

					if (this.filteredPolicies.isEmpty()) {
						if (logger.isInfoEnabled()) {
							logger.info("No match has been found for active versions");
						}
						JSONObject result = new JSONObject();
						result.put("success", false);
						result.put("error", "No match has been found for active versions");
						resultList.add(result);
						logger.warn("No match has been found for active versions");

					}

					System.out.println(this.filteredPolicies);
				}

				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{result: " + resultList + "}");
				out.write(j.toString());
				return null;
			}catch(Exception e){
				response.setCharacterEncoding("UTF-8");
				request.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(e.getMessage());
			}
			return null;
		}

		protected void clSearchBody(String clPolicyType, String bodyField, String bodyValue,
				ArrayList<Pair<ArrayList<String>, ArrayList<String>>> filter_s) {
			if (logger.isDebugEnabled())
				logger.debug("ENTER: " + clPolicyType + ":" + bodyField + ":" + bodyValue);

			final ArrayList<String> clBodyField_s = new ArrayList<String>();
			final ArrayList<String> clBodyValue_s = new ArrayList<String>();

			if (clPolicyType == null || clPolicyType.isEmpty()) {
				clBodyField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_Fault.name() + "_Body." + bodyField);
				clBodyField_s.add("Policy.Body."+ ElkConnector.PolicyType.Config_PM.name() + "_Body." + bodyField);
				clBodyValue_s.add(bodyValue);
			} else {
				clBodyField_s.add("Policy.Body." + clPolicyType + "_Body." + bodyField);
				clBodyValue_s.add(bodyValue);
			}
			filter_s.add(new Pair<ArrayList<String>, ArrayList<String>>(clBodyField_s, clBodyValue_s));
		}

		protected void clSearchFilter(String clType, String clField, String clValue, 
				ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s) {
			if (logger.isDebugEnabled())
				logger.debug("ENTER: " + clType + ":" + clField + ":" + clValue);

			ArrayList<String> clSearchField_s = new ArrayList<String>();
			clSearchField_s.add("Policy.Body." + clType + "_Body." + clField);

			ArrayList<String> clSearchValue_s = new ArrayList<String>();
			clSearchValue_s.add(clValue);

			filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clSearchField_s, clSearchValue_s));
		}

		public ElkConnector.PolicyIndexType toPolicyIndexType(String type) throws IllegalArgumentException {
			if (type == null || type.isEmpty())
				return PolicyIndexType.all;

			return PolicyIndexType.valueOf(type);
		}

		public boolean updateElk(String xacmlFilePath) {
			boolean success = true;
			try {
				File xacmlPolicy = new File(xacmlFilePath);
				success = ElkConnector.singleton.update(xacmlPolicy);
				if (!success) {
					if (logger.isWarnEnabled()) {
						logger.warn("FAILURE to create ELK record created for " + xacmlPolicy.getPath());
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.warn("SUCCESS creating ELK record created for " + xacmlPolicy.getPath());
					}									
				}									
			} catch (Exception e) {
				logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": " + e.getMessage(), e);
				success = false;
			}
			return success;
		}

		@RequestMapping(value={"/searchDictionary"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
		public ModelAndView searchDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
			try{
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				JsonNode root = mapper.readTree(request.getReader());
				String dictionaryType = root.get("type").textValue();
				Mode mode = Mode.valueOf(dictionaryType);
				String value;
				String msg;
				switch (mode){
				case attribute :
					Attribute attributedata = (Attribute)mapper.readValue(root.get("data").toString(), Attribute.class);
					value = attributedata.getXacmlId();
					msg = searchElkDatabase("pholder",value);
					break;
				case ecompName :
					EcompName ecompName = (EcompName)mapper.readValue(root.get("data").toString(), EcompName.class);
					value = ecompName.getEcompName();
					msg = searchElkDatabase("pholder",value);
					break;
				case actionPolicy :
					ActionPolicyDict actionPolicyDict = (ActionPolicyDict)mapper.readValue(root.get("data").toString(), ActionPolicyDict.class);
					value = actionPolicyDict.getAttributeName();
					msg = searchElkDatabase("pholder",value);
					break;
				case brmsParam :
					BRMSParamTemplate bRMSParamTemplate = (BRMSParamTemplate)mapper.readValue(root.get("data").toString(), BRMSParamTemplate.class);
					value = bRMSParamTemplate.getRuleName();
					msg = searchElkDatabase("BRMSParamTemplate AND " + value);
					break;
				case pepOptions :
					PEPOptions pEPOptions = (PEPOptions)mapper.readValue(root.get("data").toString(), PEPOptions.class);
					value = pEPOptions.getPepName();
					msg = searchElkDatabase("pepName",value);
					break;
				case clSite :
					ClosedLoopSite closedLoopSite = (ClosedLoopSite)mapper.readValue(root.get("data").toString(), ClosedLoopSite.class);
					value = closedLoopSite.getSiteName();
					msg = searchElkDatabase("siteNames",value);
					break;
				case clService :
					ClosedLoopD2Services closedLoopD2Services = (ClosedLoopD2Services)mapper.readValue(root.get("data").toString(), ClosedLoopD2Services.class);
					value = closedLoopD2Services.getServiceName();
					msg = searchElkDatabase("d2Services",value);
					break;
				case clVarbind :
					VarbindDictionary varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("data").toString(), VarbindDictionary.class);
					value = varbindDictionary.getVarbindName();
					msg = searchElkDatabase("triggerSignaturesUsedForUI.signatures",value);
					break;
				case clVnf :
					VNFType vNFType = (VNFType)mapper.readValue(root.get("data").toString(), VNFType.class);
					value = vNFType.getVnftype();
					msg = searchElkDatabase("vnfType",value);
					break;
				case clVSCL :
					VSCLAction vsclAction = (VSCLAction)mapper.readValue(root.get("data").toString(), VSCLAction.class);
					value = vsclAction.getVsclaction();
					msg = searchElkDatabase("actions",value);
					break;
				case decision :
					DecisionSettings decisionSettings = (DecisionSettings)mapper.readValue(root.get("data").toString(), DecisionSettings.class);
					value = decisionSettings.getXacmlId();
					msg = searchElkDatabase("pholder",value);
					break;
				case enforcer :
					EnforcingType enforcingType = (EnforcingType)mapper.readValue(root.get("data").toString(), EnforcingType.class);
					value = enforcingType.getEnforcingType();
					msg = searchElkDatabase("pholder",value);
					break;			
				case fwTerm :
					TermList term = (TermList)mapper.readValue(root.get("data").toString(), TermList.class);
					value = term.getTermName();
					msg = searchElkDatabase("firewallRuleList.ruleName",value);
					break;
				case msDCAEUUID :
					DCAEuuid dcaeUUID = (DCAEuuid)mapper.readValue(root.get("data").toString(), DCAEuuid.class);
					value = dcaeUUID.getName();
					msg = searchElkDatabase("uuid",value);
					break;
				case msLocation :
					MicroServiceLocation mslocation = (MicroServiceLocation)mapper.readValue(root.get("data").toString(), MicroServiceLocation.class);
					value = mslocation.getName();
					msg = searchElkDatabase("location",value);
					break;
				case msModels :
					MicroServiceModels msModels = (MicroServiceModels)mapper.readValue(root.get("data").toString(), MicroServiceModels.class);
					value = msModels.getModelName();
					msg = searchElkDatabase("configName",value);
					break;
				case psGroupPolicy :
					GroupPolicyScopeList groupPoilicy = (GroupPolicyScopeList)mapper.readValue(root.get("data").toString(), GroupPolicyScopeList.class);
					value = groupPoilicy.getGroupName();
					msg = searchElkDatabase("PolicyScope",value);
					break;
				case safeRisk :
					RiskType riskType= (RiskType)mapper.readValue(root.get("data").toString(), RiskType.class);
					value = riskType.getRiskName();
					msg = searchElkDatabase("Risk Type",value);
					break;
				case safePolicyWarning :
					SafePolicyWarning safePolicy = (SafePolicyWarning)mapper.readValue(root.get("data").toString(), SafePolicyWarning.class);
					value = safePolicy.getName();
					msg = searchElkDatabase("Safe Warning",value);
					break;
				default: 		
				}
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{result: " + policyNames + "}");
				out.write(j.toString());
				return null;
			}catch(Exception e){
				response.setCharacterEncoding("UTF-8");
				request.setCharacterEncoding("UTF-8");
				PrintWriter out = response.getWriter();
				out.write(e.getMessage());
			}
			return null;
		}
		
		//Search Elk database
		public String searchElkDatabase(String value){
			String policyType = "";
			String searchText = value;
			ArrayList<PolicyLocator> locators;
			ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s = new ArrayList<Pair<ArrayList<String>,ArrayList<String>>>();
			try {
				locators = ElkConnector.singleton.policyLocators(toPolicyIndexType(policyType), searchText, filter_s,0);	
			} catch (Exception ise) {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Search is unavailable: " + ise.getMessage());
				value = "$notSuccess%";
				return value;
			}
			policyNames = new ArrayList<JSONObject>();
			for (PolicyLocator p: locators) {
				String dbPolicyName = p.scope + "/" + p.policyType + "_" + p.policyName + "." +p.version + ".xml";
				logger.debug(dbPolicyName);
				JSONObject el = new JSONObject();
				el.put("name", dbPolicyName);	
				policyNames.add(el);
			}
			if(!locators.isEmpty()){
				value = "$success%";
				return value;
			}
			return value;
		}
		
		//Search the Elk database
		public String searchElkDatabase(String key, String value){
			String policyType = "";
			String searchText = key+":"+value;
			ArrayList<PolicyLocator> locators;
			ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s = new ArrayList<Pair<ArrayList<String>,ArrayList<String>>>();
			logger.debug("Parameter value is"+value);

			String clSearchKey=null;
			clSearchKey=key;

			logger.debug("Filter value is"+clSearchKey);

			ArrayList<String> clSearchBoxFilterField_s = new ArrayList<String>();

			clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_Fault.name() + "_Body." + clSearchKey);
			clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_PM.name() + "_Body." + clSearchKey);
			clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_FW.name() + "_Body." + clSearchKey);
			clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_MS.name() + "_Body." + clSearchKey);
			//clSearchBoxFilterField_s.add("Policy.Body." + ElkConnector.PolicyType.Config_PM.name() + "_Body." + clSearchKey);

			String clSearchValue=null;
			clSearchValue=value;

			logger.debug("Search value is"+clSearchValue);

			ArrayList<String> clSearchBoxFilterValue_s = new ArrayList<String>();
			clSearchBoxFilterValue_s.add(clSearchValue);

			filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clSearchBoxFilterField_s, clSearchBoxFilterValue_s));

			try {
				locators = ElkConnector.singleton.policyLocators(toPolicyIndexType(policyType), searchText, filter_s,0);	
				logger.debug("No Exceptions");
				for (PolicyLocator l: locators) {
					logger.debug(l.policyName);
				}
				logger.debug("After for");
			} catch (Exception ise) {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Search is unavailable: " + ise.getMessage());
				//PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, ise, "AttributeDictionary", " Exception while searching Elk database ");
				logger.debug("Exceptions");
				value = "$notSuccess%";
				return value;
			}
			policyNames = new ArrayList<JSONObject>();
			for (PolicyLocator p: locators) {
				String dbPolicyName = p.scope + File.separator + p.policyType + "_" + p.policyName + ".xml";
				logger.debug(dbPolicyName);
				JSONObject el = new JSONObject();
				el.put("name", dbPolicyName);	
				policyNames.add(el);
			}
			if(!locators.isEmpty()){
				value = "$success%";
				logger.debug("Success");
				return value;
			}
			return value;
		}

}


class SearchData{
	private String query;
	private String policyType;
	private String descriptiveScope;
	private String closedLooppolicyType;
	private String ecompName;
	private String d2Service;
	private String vnfType;
	private String policyStatus;
	private String vproAction;
	private String serviceType;
	private String bindTextSearch;
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getPolicyType() {
		return policyType;
	}
	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}
	public String getDescriptiveScope() {
		return descriptiveScope;
	}
	public void setDescriptiveScope(String descriptiveScope) {
		this.descriptiveScope = descriptiveScope;
	}
	public String getClosedLooppolicyType() {
		return closedLooppolicyType;
	}
	public void setClosedLooppolicyType(String closedLooppolicyType) {
		this.closedLooppolicyType = closedLooppolicyType;
	}
	public String getEcompName() {
		return ecompName;
	}
	public void setEcompName(String ecompName) {
		this.ecompName = ecompName;
	}
	public String getD2Service() {
		return d2Service;
	}
	public void setD2Service(String d2Service) {
		this.d2Service = d2Service;
	}
	public String getVnfType() {
		return vnfType;
	}
	public void setVnfType(String vnfType) {
		this.vnfType = vnfType;
	}
	public String getPolicyStatus() {
		return policyStatus;
	}
	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}
	public String getVproAction() {
		return vproAction;
	}
	public void setVproAction(String vproAction) {
		this.vproAction = vproAction;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getBindTextSearch() {
		return bindTextSearch;
	}
	public void setBindTextSearch(String bindTextSearch) {
		this.bindTextSearch = bindTextSearch;
	}
}