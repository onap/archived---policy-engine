/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.elk.client;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.elk.client.ElkConnector.PolicyIndexType;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.Attribute;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.ClosedLoopD2Services;
import org.onap.policy.rest.jpa.ClosedLoopSite;
import org.onap.policy.rest.jpa.DCAEuuid;
import org.onap.policy.rest.jpa.DecisionSettings;
import org.onap.policy.rest.jpa.DescriptiveScope;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.MicroServiceLocation;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.PEPOptions;
import org.onap.policy.rest.jpa.RiskType;
import org.onap.policy.rest.jpa.SafePolicyWarning;
import org.onap.policy.rest.jpa.TermList;
import org.onap.policy.rest.jpa.VNFType;
import org.onap.policy.rest.jpa.VSCLAction;
import org.onap.policy.rest.jpa.VarbindDictionary;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;

import io.searchbox.client.JestResult;

@Controller
@RequestMapping({"/"})
public class PolicyElasticSearchController{

	private static final Logger LOGGER = FlexLogger.getLogger(PolicyElasticSearchController.class);

	enum Mode{
		attribute, onapName, actionPolicy, brmsParam, pepOptions,
		clSite, clService, clVarbind, clVnf, clVSCL, decision,
		fwTerm, msDCAEUUID, msConfigName, msLocation, msModels,
		psGroupPolicy, safeRisk, safePolicyWarning
	}

	protected static final HashMap<String, String> name2jsonPath = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
	};

	private static CommonClassDao commonClassDao;

	@Autowired
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




	public ElkConnector.PolicyIndexType toPolicyIndexType(String type) throws IllegalArgumentException {
		if (type == null || type.isEmpty()){
			return PolicyIndexType.all;
		}
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


	@RequestMapping(value="/searchPolicy", method= RequestMethod.POST)
	public void searchPolicy(HttpServletRequest request, HttpServletResponse response) {
		try{
			boolean result = false;
			boolean policyResult = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			PolicyRestAdapter policyData = new PolicyRestAdapter();
			PolicyElasticSearchController controller = new PolicyElasticSearchController();
			Map<String, String> searchKeyValue = new HashMap<>();
			List<String> policyList = new ArrayList<>();
			if(request.getParameter("policyName") != null){
				String policyName = request.getParameter("policyName");
				policyData.setNewFileName(policyName);
				if("delete".equalsIgnoreCase(request.getParameter("action"))){
					result = controller.deleteElk(policyData);
				}else{
					result = controller.updateElk(policyData);
				}
			}
			if("search".equalsIgnoreCase(request.getParameter("action"))){
				try {
					JsonNode root = mapper.readTree(request.getReader());
					SearchData searchData = (SearchData)mapper.readValue(root.get("searchdata").toString(), SearchData.class);

					String policyType = searchData.getPolicyType();

					String searchText = searchData.getQuery();
					String descriptivevalue = searchData.getDescriptiveScope();
					if(descriptivevalue != null){
						DescriptiveScope dsSearch = (DescriptiveScope) commonClassDao.getEntityItem(DescriptiveScope.class, "descriptiveScopeName", descriptivevalue);
						if(dsSearch != null){
							String[] descriptiveList =  dsSearch.getSearch().split("AND");
							for(String keyValue : descriptiveList){
								String[] entry = keyValue.split(":");
								if(searchData.getPolicyType() != null && "closedLoop".equals(searchData.getPolicyType())){
									searchKeyValue.put("jsonBodyData", "*" +entry[1] +"*");
								}else{
									searchText = entry[1];
								}
							}
						}
					}

					if(searchData.getClosedLooppolicyType() != null){
						String closedLoopType;
						if("Config_Fault".equalsIgnoreCase(searchData.getClosedLooppolicyType())){
							closedLoopType  = "ClosedLoop_Fault";
						}else{
							closedLoopType  = "ClosedLoop_PM";
						}
						searchKeyValue.put("configPolicyType", closedLoopType);
					}
					if(searchData.getOnapName() != null){
						searchKeyValue.put("onapName", searchData.getOnapName());
					}
					if(searchData.getD2Service() != null){
						String d2Service = searchData.getD2Service().trim();
						if(d2Service.equalsIgnoreCase("Hosted Voice (Trinity)")){
							d2Service = "trinity";
						}else if(d2Service.equalsIgnoreCase("vUSP")){
							d2Service = "vUSP";
						}else if(d2Service.equalsIgnoreCase("MCR")){
							d2Service = "mcr";
						}else if(d2Service.equalsIgnoreCase("Gamma")){
							d2Service = "gamma";
						}else if(d2Service.equalsIgnoreCase("vDNS")){
							d2Service = "vDNS";
						}
						searchKeyValue.put("jsonBodyData."+d2Service+"", "true");
					}
					if(searchData.getVnfType() != null){
						searchKeyValue.put("jsonBodyData", "*"+searchData.getVnfType()+"*");
					}
					if(searchData.getPolicyStatus() != null){
						searchKeyValue.put("jsonBodyData", "*"+searchData.getPolicyStatus()+"*");
					}
					if(searchData.getVproAction() != null){
						searchKeyValue.put("jsonBodyData", "*"+searchData.getVproAction()+"*");
					}
					if(searchData.getServiceType() != null){
						searchKeyValue.put("serviceType", searchData.getServiceType());
					}
					if(searchData.getBindTextSearch() != null){
						searchKeyValue.put(searchData.getBindTextSearch(), searchText);
						searchText = null;
					}
					PolicyIndexType type = null;
					if(policyType != null){
						if(policyType.equalsIgnoreCase("action")){
							type = ElkConnector.PolicyIndexType.action;
						}else if(policyType.equalsIgnoreCase("decision")){
							type = ElkConnector.PolicyIndexType.decision;
						}else if(policyType.equalsIgnoreCase("config")){
							type = ElkConnector.PolicyIndexType.config;
						}else if(policyType.equalsIgnoreCase("closedloop")){
							type = ElkConnector.PolicyIndexType.closedloop;
						}else{
							type = ElkConnector.PolicyIndexType.all;
						}
					}else{
						type = ElkConnector.PolicyIndexType.all;
					}
					JestResult policyResultList = controller.search(type, searchText, searchKeyValue);
					if(policyResultList.isSucceeded()){
						result = true;
						policyResult = true;
						JsonArray resultObject = policyResultList.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
						for(int i =0; i < resultObject.size(); i++){
							String policyName = resultObject.get(i).getAsJsonObject().get("_id").toString();
							policyList.add(policyName);
						}
					}else{
						LOGGER.error("Exception Occured While Searching for Data in Elastic Search Server, Check the Logs");
					}
				}catch(Exception e){
					LOGGER.error("Exception Occured While Searching for Data in Elastic Search Server" + e);
				}
			}
			String message="";
			if(result){
				message = "Elastic Server Transaction is success";
			}else{
				message = "Elastic Server Transaction is failed, please check the logs";
			}
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(message));
			JSONObject j = new JSONObject(msg);
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("success", "success");
			if(policyResult){
				JSONObject k = new JSONObject("{policyresult: " + policyList + "}");
				response.getWriter().write(k.toString());
			}else{
				response.getWriter().write(j.toString());
			}
		}catch(Exception e){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.addHeader("error", "Exception Occured While Performing Elastic Transaction");
			LOGGER.error("Exception Occured While Performing Elastic Transaction"+e.getMessage(),e);
		}
	}

	@RequestMapping(value={"/searchDictionary"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView searchDictionary(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		try{
			PolicyIndexType config = PolicyIndexType.config;
			PolicyIndexType closedloop = PolicyIndexType.closedloop;
			PolicyIndexType action = PolicyIndexType.action;
			PolicyIndexType decision = PolicyIndexType.decision;
			PolicyIndexType all = PolicyIndexType.all;

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			String dictionaryType = root.get("type").textValue();
			Mode mode = Mode.valueOf(dictionaryType);
			String value;
			List<String> policyList = new ArrayList<>();
			switch (mode){
			case attribute :
				Attribute attributedata = (Attribute)mapper.readValue(root.get("data").toString(), Attribute.class);
				value = attributedata.getXacmlId();
				policyList = searchElkDatabase(all, "pholder",value);
				break;
			case onapName :
				OnapName onapName = (OnapName)mapper.readValue(root.get("data").toString(), OnapName.class);
				value = onapName.getOnapName();
				policyList = searchElkDatabase(all, "onapName",value);
				break;
			case actionPolicy :
				ActionPolicyDict actionPolicyDict = (ActionPolicyDict)mapper.readValue(root.get("data").toString(), ActionPolicyDict.class);
				value = actionPolicyDict.getAttributeName();
				policyList = searchElkDatabase(action, "actionAttributeValue",value);
				break;
			case brmsParam :
				BRMSParamTemplate bRMSParamTemplate = (BRMSParamTemplate)mapper.readValue(root.get("data").toString(), BRMSParamTemplate.class);
				value = bRMSParamTemplate.getRuleName();
				policyList = searchElkDatabase(config, "ruleName",value);
				break;
			case pepOptions :
				PEPOptions pEPOptions = (PEPOptions)mapper.readValue(root.get("data").toString(), PEPOptions.class);
				value = pEPOptions.getPepName();
				policyList = searchElkDatabase(closedloop,"jsonBodyData.pepName",value);
				break;
			case clSite :
				ClosedLoopSite closedLoopSite = (ClosedLoopSite)mapper.readValue(root.get("data").toString(), ClosedLoopSite.class);
				value = closedLoopSite.getSiteName();
				policyList = searchElkDatabase(closedloop,"siteNames",value);
				break;
			case clService :
				ClosedLoopD2Services closedLoopD2Services = (ClosedLoopD2Services)mapper.readValue(root.get("data").toString(), ClosedLoopD2Services.class);
				value = closedLoopD2Services.getServiceName();
				policyList = searchElkDatabase(closedloop, "pholder",value);
				break;
			case clVarbind :
				VarbindDictionary varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("data").toString(), VarbindDictionary.class);
				value = varbindDictionary.getVarbindName();
				policyList = searchElkDatabase(closedloop, "jsonBodyData","*"+value+"*");
				break;
			case clVnf :
				VNFType vNFType = (VNFType)mapper.readValue(root.get("data").toString(), VNFType.class);
				value = vNFType.getVnftype();
				policyList = searchElkDatabase(closedloop, "jsonBodyData","*"+value+"*");
				break;
			case clVSCL :
				VSCLAction vsclAction = (VSCLAction)mapper.readValue(root.get("data").toString(), VSCLAction.class);
				value = vsclAction.getVsclaction();
				policyList = searchElkDatabase(closedloop, "jsonBodyData","*"+value+"*");
				break;
			case decision :
				DecisionSettings decisionSettings = (DecisionSettings)mapper.readValue(root.get("data").toString(), DecisionSettings.class);
				value = decisionSettings.getXacmlId();
				policyList = searchElkDatabase(decision,"pholder",value);
				break;
			case fwTerm :
				TermList term = (TermList)mapper.readValue(root.get("data").toString(), TermList.class);
				value = term.getTermName();
				policyList = searchElkDatabase(config, "pholder",value);
				break;
			case msDCAEUUID :
				DCAEuuid dcaeUUID = (DCAEuuid)mapper.readValue(root.get("data").toString(), DCAEuuid.class);
				value = dcaeUUID.getName();
				policyList = searchElkDatabase(config, "uuid",value);
				break;
			case msLocation :
				MicroServiceLocation mslocation = (MicroServiceLocation)mapper.readValue(root.get("data").toString(), MicroServiceLocation.class);
				value = mslocation.getName();
				policyList = searchElkDatabase(config, "location",value);
				break;
			case msModels :
				MicroServiceModels msModels = (MicroServiceModels)mapper.readValue(root.get("data").toString(), MicroServiceModels.class);
				value = msModels.getModelName();
				policyList = searchElkDatabase(config, "serviceType",value);
				break;
			case psGroupPolicy :
				GroupPolicyScopeList groupPoilicy = (GroupPolicyScopeList)mapper.readValue(root.get("data").toString(), GroupPolicyScopeList.class);
				value = groupPoilicy.getGroupName();
				policyList = searchElkDatabase(config, "pholder",value);
				break;
			case safeRisk :
				RiskType riskType= (RiskType)mapper.readValue(root.get("data").toString(), RiskType.class);
				value = riskType.getRiskName();
				policyList = searchElkDatabase(config, "riskType",value);
				break;
			case safePolicyWarning :
				SafePolicyWarning safePolicy = (SafePolicyWarning)mapper.readValue(root.get("data").toString(), SafePolicyWarning.class);
				value = safePolicy.getName();
				policyList = searchElkDatabase(config, "pholder",value);
				break;
			default: 
			}

			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("success", "success");
			JSONObject k = new JSONObject("{policyresult: " + policyList + "}");
			response.getWriter().write(k.toString());
		}catch(Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			LOGGER.error(e);
		}
		return null;
	}

	//Search the Elk database
	public List<String> searchElkDatabase(PolicyIndexType type, String key, String value){
		PolicyElasticSearchController controller = new PolicyElasticSearchController();
		Map<String, String> searchKeyValue = new HashMap<>();
		if(!"pholder".equals(key)){
			searchKeyValue.put(key, value);
		}

		List<String> policyList = new ArrayList<>();
		JestResult policyResultList = controller.search(type, value, searchKeyValue);
		if(policyResultList.isSucceeded()){
			JsonArray resultObject = policyResultList.getJsonObject().get("hits").getAsJsonObject().get("hits").getAsJsonArray();
			for(int i =0; i < resultObject.size(); i++){
				String policyName = resultObject.get(i).getAsJsonObject().get("_id").toString();
				policyList.add(policyName);
			}
		}else{
			LOGGER.error("Exception Occured While Searching for Data in Elastic Search Server, Check the Logs");
		}
		return policyList;
	}

	public JestResult search(PolicyIndexType type, String text, Map<String, String> searchKeyValue) {
		 return ElkConnector.singleton.search(type, text, searchKeyValue);
	}

}

class SearchData{
	private String query;
	private String policyType;
	private String descriptiveScope;
	private String closedLooppolicyType;
	private String onapName;
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
	public String getOnapName() {
		return onapName;
	}
	public void setOnapName(String onapName) {
		this.onapName = onapName;
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
