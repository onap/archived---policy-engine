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

package org.openecomp.policy.components;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.elk.client.ElkConnector;
import org.openecomp.policy.elk.client.ElkConnector.PolicyIndexType;
import org.openecomp.policy.rest.dao.DescriptiveScopeDao;
import org.openecomp.policy.rest.jpa.DescriptiveScope;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.elk.client.Pair;
import org.openecomp.policy.elk.client.PolicyLocator;
import org.springframework.beans.factory.annotation.Autowired;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


public class ElasticSearchComponent {
	private static final Logger logger = FlexLogger.getLogger(ElasticSearchComponent.class);


	private static PolicyVersionDao policyVersionDao;
	private static DescriptiveScopeDao descriptiveScopeDao;

	@Autowired
	public ElasticSearchComponent(PolicyVersionDao policyVersionDao){
		ElasticSearchComponent.policyVersionDao = policyVersionDao;
	}

	@Autowired
	public ElasticSearchComponent(DescriptiveScopeDao descriptiveScopeDao){
		ElasticSearchComponent.descriptiveScopeDao = descriptiveScopeDao;
	}


	private volatile static HashMap<Path, String> filteredPolicies = new HashMap<Path, String>();

	public static final HashMap<String, String> name2jsonPath = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(CLFAULT_UIFIELD_D2_SERVICES_TRINITY, CLFAULT_UIJSON_D2_SERVICES_TRINITY);
			put(CLFAULT_UIFIELD_D2_SERVICES_VUSP, CLFAULT_UIJSON_D2_SERVICES_VUSP);
			put(CLFAULT_UIFIELD_D2_SERVICES_MCR, CLFAULT_UIJSON_D2_SERVICES_MCR);
			put(CLFAULT_UIFIELD_D2_SERVICES_GAMMA, CLFAULT_UIJSON_D2_SERVICES_GAMMA);	
			put(CLFAULT_UIFIELD_D2_SERVICES_VDNS, CLFAULT_UIJSON_D2_SERVICES_VDNS);

			put(CLFAULT_UIFIELD_EMAIL_ADDRESS, CLFAULT_UIJSON_EMAIL_ADDRESS);
			put(CLFAULT_UIFIELD_TRIGGER_SIGNATURE, CLFAULT_UIJSON_TRIGGER_SIGNATURE);
			put(CLFAULT_UIFIELD_VERIFICATION_SIGNATURE, CLFAULT_UIJSON_VERIFICATION_SIGNATURE);
			put(CLFAULT_UIFIELD_CONNECT_ALL_TRAPS, CLFAULT_UIJSON_CONNECT_ALL_TRAPS);	
			put(CLFAULT_UIFIELD_CONNECT_ALL_FAULTS, CLFAULT_UIJSON_CONNECT_ALL_FAULTS);

			put(CLFAULT_UIFIELD_POLICY_STATUS_INACTIVE, CLFAULT_UIJSON_POLICY_STATUS_ACTIVE);
			put(CLFAULT_UIFIELD_POLICY_STATUS_ACTIVE, CLFAULT_UIJSON_POLICY_STATUS_INACTIVE);

			put(CLPM_UIFIELD_ONSET_MESSAGE, CLPM_UIJSON_ONSET_MESSAGE);
			put(CLPM_UIFIELD_POLICY_NAME, CLPM_UIJSON_POLICY_NAME);
			put(CLPM_UIFIELD_ABATEMENT_MESSAGE, CLPM_UIJSON_ABATEMENT_MESSAGE);
			put(CLPM_UIFIELD_GEOLINK, CLPM_UIJSON_GEOLINK);	
		}};

		//For AND and OR logical connector AND=0 and OR=1
		private static int connectorSelected;

		public static final String CLFAULT_UIFIELD_D2_SERVICES_TRINITY = "Hosted Voice (Trinity)";
		public static final String CLFAULT_UIJSON_D2_SERVICES_TRINITY = "trinity";

		public static final String CLFAULT_UIFIELD_D2_SERVICES_VUSP = "vUSP";
		public static final String CLFAULT_UIJSON_D2_SERVICES_VUSP = "vUSP";

		public static final String CLFAULT_UIFIELD_D2_SERVICES_MCR = "MCR";
		public static final String CLFAULT_UIJSON_D2_SERVICES_MCR = "mcr";

		public static final String CLFAULT_UIFIELD_D2_SERVICES_GAMMA = "Gamma";
		public static final String CLFAULT_UIJSON_D2_SERVICES_GAMMA = "gama";

		public static final String CLFAULT_UIFIELD_D2_SERVICES_VDNS = "vDNS";
		public static final String CLFAULT_UIJSON_D2_SERVICES_VDNS = "vDNS";

		public static final String CLFAULT_UIFIELD_EMAIL_ADDRESS = "Email Address";
		public static final String CLFAULT_UIJSON_EMAIL_ADDRESS = "emailAddress";

		public static final String CLFAULT_UIFIELD_TRIGGER_SIGNATURE = "Trigger Signature";
		public static final String CLFAULT_UIJSON_TRIGGER_SIGNATURE = "triggerSignaturesUsedForUI.signatures";

		public static final String CLFAULT_UIFIELD_VERIFICATION_SIGNATURE = "Verification Signature";
		public static final String CLFAULT_UIJSON_VERIFICATION_SIGNATURE = "verificationSignaturesUsedForUI.signatures";

		public static final String CLFAULT_UIFIELD_CONNECT_ALL_TRAPS = "Connect All Traps";
		public static final String CLFAULT_UIJSON_CONNECT_ALL_TRAPS = "triggerSignaturesUsedForUI.connectSignatures";

		public static final String CLFAULT_UIFIELD_CONNECT_ALL_FAULTS = "Connect All Faults";
		public static final String CLFAULT_UIJSON_CONNECT_ALL_FAULTS = "verificationSignaturesUsedForUI.connectSignatures";

		public static final String CLFAULT_UIFIELD_POLICY_STATUS_ACTIVE = "Active";
		public static final String CLFAULT_UIJSON_POLICY_STATUS_ACTIVE = "ACTIVE";

		public static final String CLFAULT_UIFIELD_POLICY_STATUS_INACTIVE = "InActive";
		public static final String CLFAULT_UIJSON_POLICY_STATUS_INACTIVE = "INACTIVE";


		public static final String CLPM_UIFIELD_ONSET_MESSAGE = "Onset Message";
		public static final String CLPM_UIJSON_ONSET_MESSAGE = "attributes.OnsetMessage";

		public static final String CLPM_UIFIELD_POLICY_NAME = "PolicyName";
		public static final String CLPM_UIJSON_POLICY_NAME = "attributes.PolicyName";

		public static final String CLPM_UIFIELD_ABATEMENT_MESSAGE = "Abatement Message";
		public static final String CLPM_UIJSON_ABATEMENT_MESSAGE = "attributes.AbatementMessage";

		public static final String CLPM_UIFIELD_GEOLINK = "Geo Link";	
		public static final String CLPM_UIJSON_GEOLINK = "geoLink";

		public static void search(String value){
			String policyType = "all";//(String) self.searchPolicyType.getValue()

			ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s =  new ArrayList<Pair<ArrayList<String>,ArrayList<String>>>();

			String searchText = "";//self.searchTextBox.getValue()
			if (searchText == null || searchText.isEmpty()) {
				if (policyType == null || policyType.isEmpty() &&
						!policyType.equals(ElkConnector.PolicyIndexType.closedloop.toString())) {
					if (logger.isDebugEnabled()) {
						logger.debug("Clearing search filters, nothing to search and not closed loop.");
					}
					return;						
				}
			} else {
				searchText = searchText.trim();
				//Descriptive Scope. 
				/*
			  When a item is selected in the "descriptiveScope" comboBox, the name of the item 
			  is added to the Search-Text Box with the prefix "Descriptive-Scope"
			  User needs to press the "Search" button to perform the search. 
				 */
				if(searchText.contains("Descriptive-Scope="))
				{ 
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
					for (int i = 0; i < descriptiveScopeDao.getDescriptiveScope().size(); i++) {
						DescriptiveScope dsSearch=descriptiveScopeDao.getDescriptiveScope().get(i);
						if(dsSearch.getScopeName().equals(dsName[1])){
							searchText=dsSearch.getSearch();
							if (logger.isDebugEnabled()) {
								logger.debug("DescriptiveScope Search String is " +searchText );
							}
						}
					}
				}

				if(searchText.contains(":"))
				{
					String connector="&";

					if(searchText.contains("&"))
					{
						connector="&";
						connectorSelected=0;
					}
					else if(searchText.contains("|"))
					{
						connector=Pattern.quote("|");
						connectorSelected=1;
					}
					for (String retval: searchText.split(connector)){

						int index= retval.indexOf(':');
						String filterKey=null;
						String filterValue=null;

						filterKey=retval.substring(0,index);
						filterValue= retval.substring(index+1);

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

				String clPolicyType = "";//(String) self.cbSearchCLPolicyType.getValue()
				if (clPolicyType != null && !clPolicyType.isEmpty()) {							
					ArrayList<String> clPolicyTypeField_s = new ArrayList<String>();
					clPolicyTypeField_s.add("Policy.PolicyType");

					ArrayList<String> clPolicyTypeValue_s = new ArrayList<String>();
					clPolicyTypeValue_s.add(clPolicyType);

					filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clPolicyTypeField_s, clPolicyTypeValue_s));
				}

				String clEcompName = "";//(String) self.cbSearchCLEcompName.getValue()
				if (clEcompName != null && !clEcompName.isEmpty()) {
					clSearchBody(clPolicyType, "ecompname", clEcompName, filter_s);
				}

				String clD2Services = "";//(String) self.cbSearchCLD2Services.getValue()
				if (clD2Services != null && !clD2Services.isEmpty()) {
					switch (clD2Services) {
					case CLFAULT_UIFIELD_D2_SERVICES_TRINITY:
					case CLFAULT_UIFIELD_D2_SERVICES_VUSP:						
					case CLFAULT_UIFIELD_D2_SERVICES_MCR:						
					case CLFAULT_UIFIELD_D2_SERVICES_GAMMA:							
					case CLFAULT_UIFIELD_D2_SERVICES_VDNS:	
						clSearchBody(clPolicyType, name2jsonPath.get(clD2Services), "true", filter_s);
						break;
					default:
						if (logger.isWarnEnabled())
							logger.warn("Unexpected D2 Service: " + clD2Services);
						break;
					}
				}

				String clFaultAction = "";//(String) self.cbSearchCLFaultAction.getValue()
				if (clFaultAction != null && !clFaultAction.isEmpty()) {
					if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_Fault.name())) {
						clSearchFilter(ElkConnector.PolicyType.Config_Fault.name(), "actions", clFaultAction, filter_s);
					}
				}

				String clFaultStatus = "";//(String) self.cbSearchCLFaultStatus.getValue()
				if (clFaultStatus != null && !clFaultStatus.isEmpty()) {
					if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_Fault.name())) {
						clSearchFilter(ElkConnector.PolicyType.Config_Fault.name(), "closedLoopPolicyStatus", clFaultStatus, filter_s);
					}
				}

				String clFaultVnfTypes = "";//(String) self.cbSearchCLFaultVnfTypes.getValue()
				if (clFaultVnfTypes != null && !clFaultVnfTypes.isEmpty()) {
					if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_Fault.name())) {
						clSearchFilter(ElkConnector.PolicyType.Config_Fault.name(), "vnfType", clFaultVnfTypes, filter_s);
					}
				}

				String clPMServiceType = "";//(String) self.cbSearchCLPMServiceType.getValue()
				if (clPMServiceType != null && !clPMServiceType.isEmpty()) {
					if (clPolicyType == null || clPolicyType.equals(ElkConnector.PolicyType.Config_PM.name())) {
						clSearchFilter(ElkConnector.PolicyType.Config_PM.name(), "serviceTypePolicyName", clPMServiceType, filter_s);
					}
				}

				String clSearchBoxFilter = "";//(String) self.cbSearchCLTextFilter.getValue()
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

				return;
			}

			ArrayList<PolicyLocator> locators;
			try {
				locators = ElkConnector.singleton.policyLocators(toPolicyIndexType(policyType), 
						searchText, filter_s,connectorSelected);	
			} catch (Exception ise) {
				/*AdminNotification.warn("Search is unavailable: " + ise.getMessage());*/
				return;
			}

			synchronized(filteredPolicies) {
				if (locators.isEmpty()) {
					if (logger.isInfoEnabled()) {
						logger.info("No match has been found");
					}
					//AdminNotification.warn("No match has been found");
					return;
				}

				// Retrieve active versions

				HashMap<String, Boolean> policyVersion_s = new HashMap<String, Boolean>();
				for(int i = 0; i < policyVersionDao.getPolicyVersionData().size(); i++) {
					PolicyVersion entityVersion = policyVersionDao.getPolicyVersionData().get(i);
					String dbPolicy = entityVersion.getPolicyName() + "." + entityVersion.getActiveVersion();
					policyVersion_s.put(dbPolicy, true);
					if (logger.isDebugEnabled())
						logger.debug("Map addition: DB Policy Name: " + dbPolicy);
				}

				filteredPolicies.clear();
				for (PolicyLocator p: locators) {
					String dbPolicyName = p.scope + "/" + p.policyType + "_" + p.policyName;
					if (policyVersion_s.containsKey(dbPolicyName)) {
						String filterPolicyName = dbPolicyName + ".xml";
						filteredPolicies.put(Paths.get(filterPolicyName), filterPolicyName);							
						if (logger.isInfoEnabled())
							logger.info("Active Version Policy found in search: " + 
									dbPolicyName + " -> " + filterPolicyName);
					} else {
						if (logger.isInfoEnabled()) 
							logger.info("Inactive Version Policy found in search: " + dbPolicyName);						
					}
				}

				if (filteredPolicies.isEmpty()) {
					if (logger.isInfoEnabled()) {
						logger.info("No match has been found for active versions");
					}
					//AdminNotification.warn("No match has been found for active versions");
					return;
				}

				//self.policyContainer.setFilter(self.filteredPolicies);
			}
			/*	self.policyContainer.refresh();*/
		}


		protected static void clSearchBody(String clPolicyType, String bodyField, String bodyValue,
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

		protected static void clSearchFilter(String clType, String clField, String clValue, 
				ArrayList<Pair<ArrayList<String>,ArrayList<String>>> filter_s) {
			if (logger.isDebugEnabled())
				logger.debug("ENTER: " + clType + ":" + clField + ":" + clValue);

			ArrayList<String> clSearchField_s = new ArrayList<String>();
			clSearchField_s.add("Policy.Body." + clType + "_Body." + clField);

			ArrayList<String> clSearchValue_s = new ArrayList<String>();
			clSearchValue_s.add(clValue);

			filter_s.add(new Pair<ArrayList<String>,ArrayList<String>>(clSearchField_s, clSearchValue_s));
		}

		public static ElkConnector.PolicyIndexType toPolicyIndexType(String type) throws IllegalArgumentException {
			if (type == null || type.isEmpty())
				return PolicyIndexType.all;

			return PolicyIndexType.valueOf(type);
		}
}
