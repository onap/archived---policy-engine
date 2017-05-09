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


import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.ActionPolicyDict;
import org.openecomp.policy.rest.jpa.Attribute;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
import org.openecomp.policy.rest.jpa.ClosedLoopD2Services;
import org.openecomp.policy.rest.jpa.ClosedLoopSite;
import org.openecomp.policy.rest.jpa.DCAEuuid;
import org.openecomp.policy.rest.jpa.DecisionSettings;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.EnforcingType;
import org.openecomp.policy.rest.jpa.GroupPolicyScopeList;
import org.openecomp.policy.rest.jpa.MicroServiceLocation;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PEPOptions;
import org.openecomp.policy.rest.jpa.RiskType;
import org.openecomp.policy.rest.jpa.SafePolicyWarning;
import org.openecomp.policy.rest.jpa.TermList;
import org.openecomp.policy.rest.jpa.VNFType;
import org.openecomp.policy.rest.jpa.VSCLAction;
import org.openecomp.policy.rest.jpa.VarbindDictionary;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.searchbox.client.JestResult;

@Controller
@RequestMapping({"/"})
public class PolicyElasticSearchController{

	private static final Logger LOGGER = FlexLogger.getLogger(PolicyElasticSearchController.class);
	private List<JSONObject> policyNames = null;

	enum Mode{
		attribute, ecompName, actionPolicy, brmsParam, pepOptions, clSite, clService, clVarbind, clVnf, clVSCL, decision, enforcer, fwTerm, msDCAEUUID, msConfigName, msLocation, msModels,
		psGroupPolicy, safeRisk, safePolicyWarning
	}

	public static final HashMap<String, String> name2jsonPath = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
	};

	public static CommonClassDao commonClassDao;

	public PolicyElasticSearchController(CommonClassDao commonClassDao) {
		PolicyElasticSearchController.commonClassDao = commonClassDao;
	}

	public PolicyElasticSearchController() {}

	public static void TurnOffCertsCheck() {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		// Install all-trusting trust manager
		SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx
					.getSocketFactory());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			LOGGER.error("SSL Security Error: " + e);
		}

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}

	protected void clSearchBody(String clPolicyType, String bodyField, String bodyValue,
			ArrayList<Pair<ArrayList<String>, ArrayList<String>>> filter_s) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("ENTER: " + clPolicyType + ":" + bodyField + ":" + bodyValue);

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
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("ENTER: " + clType + ":" + clField + ":" + clValue);

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

	public boolean updateElk(PolicyRestAdapter policyData) {
		boolean success = true;
		try {
			success = ElkConnector.singleton.update(policyData);
			if (!success) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("FAILURE to create ELK record created for " + policyData.getNewFileName());
				}
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.warn("SUCCESS creating ELK record created for " + policyData.getNewFileName());
				}									
			}									
		} catch (Exception e) {
			LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": " + e.getMessage(), e);
			success = false;
		}
		return success;
	}

	public boolean deleteElk(PolicyRestAdapter policyData) {
		boolean success = true;
		try {
			success = ElkConnector.singleton.delete(policyData);
			if (!success) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("FAILURE to delete ELK record created for " + policyData.getNewFileName());
				}
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.warn("SUCCESS deleting ELK record created for " + policyData.getNewFileName());
				}									
			}									
		} catch (Exception e) {
			LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + ": " + e.getMessage(), e);
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
			@SuppressWarnings("unused")
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
		JestResult locators;
		Map<String, String> filter_s = new HashMap<String, String>();
		try {
			locators = ElkConnector.singleton.search(toPolicyIndexType(policyType), searchText, filter_s);	
		} catch (Exception ise) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Search is unavailable: " + ise.getMessage());
			value = "$notSuccess%";
			return value;
		}
		policyNames = new ArrayList<JSONObject>();
		System.out.println(locators);
		return value;
	}

	//Search the Elk database
	public String searchElkDatabase(String key, String value){
		String policyType = "";
		String searchText = key+":"+value;
		JestResult locators;
		Map<String, String> filter_s = new HashMap<String, String>();
		LOGGER.debug("Parameter value is"+value);

		String clSearchKey=null;
		clSearchKey=key;

		LOGGER.debug("Filter value is"+clSearchKey);



		String clSearchValue=null;
		clSearchValue=value;

		LOGGER.debug("Search value is"+clSearchValue);

		ArrayList<String> clSearchBoxFilterValue_s = new ArrayList<String>();
		clSearchBoxFilterValue_s.add(clSearchValue);


		try {
			locators = ElkConnector.singleton.search(toPolicyIndexType(policyType), searchText, filter_s);	
			System.out.println(locators);
		} catch (Exception ise) {
			LOGGER.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR+"Search is unavailable: " + ise.getMessage());
			//PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, ise, "AttributeDictionary", " Exception while searching Elk database ");
			LOGGER.debug("Exceptions");
			value = "$notSuccess%";
			return value;
		}
		return value;
	}
	
	public JestResult search(PolicyIndexType type, String text, 
            Map<String, String> searchKeyValue) {
		 return ElkConnector.singleton.search(type, text, searchKeyValue);
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
