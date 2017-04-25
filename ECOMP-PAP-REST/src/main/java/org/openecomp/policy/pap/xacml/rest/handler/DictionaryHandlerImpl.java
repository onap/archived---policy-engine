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
package org.openecomp.policy.pap.xacml.rest.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.pap.xacml.rest.service.DictionaryService;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;

public class DictionaryHandlerImpl implements DictionaryHandler{
	/*
	 * Get Equivalent for Dictionary Services.
	 */
	public void doDictionaryAPIGet(HttpServletRequest request, HttpServletResponse response) {
		String dictionaryType = request.getParameter("dictionaryType");
		try {
			DictionaryService dictionary = new DictionaryService();
			switch (dictionaryType) {
			case "EcompName":
				dictionary.getEcompDictionary(request, response);
				break;
			case "Attribute":
				dictionary.getAttributeDictionary(request, response);
				break;
			case "Action":
				dictionary.getActionPolicyDictionary(request, response);
				break;
			case "BRMSParamTemplate":
				dictionary.getBRMSParamDictionary(request, response);
				break;
			case "VSCLAction":
				dictionary.getVSCLAction(request, response);
				break;
			case "VNFType":
				dictionary.getVnfType(request, response);
				break;
			case "PEPOptions":
				dictionary.getPEPOptions(request, response);
				break;
			case "Varbind":
				dictionary.getVarbind(request, response);
				break;
			case "Service":
				dictionary.getServiceType(request, response);
				break;
			case "Site":
				dictionary.getSiteType(request, response);
				break;
			case "Settings":
				dictionary.getSettingsDictionary(request, response);
				break;
			case "DescriptiveScope":
				dictionary.getDescriptiveDictionary(request, response);
				break;
			case "Enforcer":
				dictionary.getEnforcerDictionary(request, response);
				break;
			case "ActionList":
				dictionary.getActionListDictionary(request, response);
				break;
			case "ProtocolList":
				dictionary.getProtocolListDictionary(request, response);
				break;
			case "Zone":
				dictionary.getZoneDictionary(request, response);
				break;
			case "SecurityZone":
				dictionary.getSecurityZoneDictionary(request, response);
				break;
			case "PrefixList":
				dictionary.getPrefixListDictionary(request, response);
				break;
			case "AddressGroup":
				dictionary.getAddressGroupDictionary(request, response);
				break;
			case "ServiceGroup":
				dictionary.getServiceGroupDictionary(request, response);
				break;
			case "ServiceList":
				dictionary.getServiceListDictionary(request, response);
				break;
			case "TermList":
			case "RuleList":
			case "FirewallRuleList":
			case "Term":
				dictionary.getTermListDictionary(request, response);
				break;
			case "MicroServiceLocation":
				dictionary.getMicroServiceLocationDictionary(request, response);
				break;
			case "MicroServiceConfigName":
				dictionary.getMicroServiceConfigNameDictionary(request, response);
				break;
			case "DCAEUUID":
				dictionary.getDCAEUUIDDictionary(request, response);
				break;
			case "MicroServiceModels":
				dictionary.getMicroServiceModelsDictionary(request, response);
				break;
			case "PolicyScopeService":
				dictionary.getPSServiceDictionary(request, response);
				break;
			case "PolicyScopeResource":
				dictionary.getPSResourceDictionary(request, response);
				break;
			case "PolicyScopeType":
				dictionary.getPSTypeDictionary(request, response);
				break;
			case "PolicyScopeClosedLoop":
				dictionary.getPSClosedLoopDictionary(request, response);
				break;
			case "GroupPolicyScopeList":
				dictionary.getPSGroupScopeDictionary(request, response);
				break;
			case "RiskType":
				dictionary.getRiskTypeDictionary(request, response);
				break;
			case "SafePolicyWarning":
				dictionary.getSafePolicyWarningDictionary(request, response);
				break;
			case "MicroServiceDictionary":
				dictionary.getMicroServiceDictionary(request, response);
				break;
			default:
				extendedOptions(dictionaryType, request, response, true);
				return;
			}
		} catch (Exception e) {
			String message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Error Querying the Database: " + e.getMessage();
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "DictionaryHandler", " Error Querying the Database.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
			response.addHeader("error", message);
			return;
		}
	}
	
	/**
	 * Can be used to extend the services.
	 * 
	 * getflag=true indicates Get Request.
	 * getflag=false indicates Put Request.  
	 * @return 
	 */
	public String extendedOptions(String dictionaryType, HttpServletRequest request, HttpServletResponse response, boolean getflag) {
		// Default code
		String message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Invalid Dictionary in Request.";
		PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "DictionaryHandler", " Invalid Dictionary in Request.");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setHeader("error", "dictionary");
		response.addHeader("error", message);
		return null;
	}

	public void doDictionaryAPIPut(HttpServletRequest request, HttpServletResponse response) {
		String result = null;
		String dictionaryType = request.getParameter("dictionaryType");
		String operation = request.getParameter("operation");         
		try {
			DictionaryService dictionary = new DictionaryService();
			switch (dictionaryType) {
			case "EcompName":
				result = dictionary.saveEcompDictionary(request, response);
				break;
			case "Attribute":
				result = dictionary.saveAttributeDictionary(request, response);
				break;
			case "Action":
				result = dictionary.saveActionPolicyDictionary(request, response);
				break;
			case "BRMSParamTemplate":
				result = dictionary.saveBRMSParamDictionary(request, response);
				break;
			case "VSCLAction":
				result = dictionary.saveVSCLAction(request, response);
				break;
			case "VNFType":
				result = dictionary.saveVnfType(request, response);
				break;
			case "PEPOptions":
				result = dictionary.savePEPOptions(request, response);
				break;
			case "Varbind":
				result = dictionary.saveVarbind(request, response);
				break;
			case "Service":
				result = dictionary.saveServiceType(request, response);
				break;
			case "Site":
				result = dictionary.saveSiteType(request, response);
				break;
			case "Settings":
				result = dictionary.saveSettingsDictionary(request, response);
				break;
			case "DescriptiveScope":
				result = dictionary.saveDescriptiveDictionary(request, response);
				break;
			case "Enforcer":
				result = dictionary.saveEnforcerDictionary(request, response);
				break;
			case "ActionList":
				result = dictionary.saveActionListDictionary(request, response);
				break;
			case "ProtocolList":
				result = dictionary.saveProtocolListDictionary(request, response);
				break;
			case "Zone":
				result = dictionary.saveZoneDictionary(request, response);
				break;
			case "SecurityZone":
				result = dictionary.saveSecurityZoneDictionary(request, response);
				break;
			case "PrefixList":
				result = dictionary.savePrefixListDictionary(request, response);
				break;
			case "AddressGroup":
				result = dictionary.saveAddressGroupDictionary(request, response);
				break;
			case "ServiceGroup":
				result = dictionary.saveServiceGroupDictionary(request, response);
				break;
			case "ServiceList":
				result = dictionary.saveServiceListDictionary(request, response);
				break;
			case "TermList":
			case "RuleList":
			case "FirewallRuleList":
			case "Term":
				result = dictionary.saveTermListDictionary(request, response);
				break;
			case "MicroServiceLocation":
				result = dictionary.saveMicroServiceLocationDictionary(request, response);
				break;
			case "MicroServiceConfigName":
				result = dictionary.saveMicroServiceConfigNameDictionary(request, response);
				break;
			case "DCAEUUID":
				result = dictionary.saveDCAEUUIDDictionary(request, response);
				break;
			case "MicroServiceModels":
				result = dictionary.saveMicroServiceModelsDictionary(request, response);
				break;
			case "PolicyScopeService":
				result = dictionary.savePSServiceDictionary(request, response);
				break;
			case "PolicyScopeResource":
				result = dictionary.savePSResourceDictionary(request, response);
				break;
			case "PolicyScopeType":
				result = dictionary.savePSTypeDictionary(request, response);
				break;
			case "PolicyScopeClosedLoop":
				result = dictionary.savePSClosedLoopDictionary(request, response);
				break;
			case "GroupPolicyScopeList":
				result = dictionary.savePSGroupScopeDictionary(request, response);
				break;
			case "RiskType":
				result = dictionary.saveRiskTypeDictionary(request, response);
				break;
			case "SafePolicyWarning":
				result = dictionary.saveSafePolicyWarningDictionary(request, response);
				break;
			case "MicroServiceDictionary":
				result = dictionary.saveMicroServiceDictionary(request, response);
				break;
			default:
				result = extendedOptions(dictionaryType, request, response, false);
				if(result==null){
					return;
				}else{
					break;
				}
			}
		} catch (Exception e) {
			String message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Error Updating the Database: " + e.getMessage();
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPapServlet", " Error Updating the Database.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.addHeader("error", message);
			return;
		}
		if (result.equalsIgnoreCase("Success")) {
			response.setStatus(HttpServletResponse.SC_OK);                              
			response.addHeader("successMapKey", "success");
			if (operation.equalsIgnoreCase("update")) {
				response.addHeader("operation",  "updateDictionary");
			} else {
				response.addHeader("operation", "createDictionary");
			}
		} else if (result.equalsIgnoreCase("Duplicate")) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.addHeader("error", "dictionaryItemExists");
		} else if (result.equalsIgnoreCase("DuplicateGroup")) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.addHeader("error", "duplicateGroup");
		} else {
			String message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Error Updating the Database.";
        	PolicyLogger.error(message);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
			response.addHeader("error", message);
		}
	}
}
