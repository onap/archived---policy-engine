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
package org.onap.policy.pap.xacml.rest.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.pap.xacml.rest.service.DictionaryService;
import org.onap.policy.xacml.api.XACMLErrorConstants;

public class DictionaryHandlerImpl implements DictionaryHandler {
    /*
     * Get Equivalent for Dictionary Services.
     */
    public void doDictionaryAPIGet(HttpServletRequest request, HttpServletResponse response) {
        String dictionaryType = request.getParameter("dictionaryType");
        try {
            DictionaryService dictionary = new DictionaryService();
            switch (dictionaryType) {
                case "OnapName":
                    dictionary.getOnapDictionary(response);
                    break;
                case "Attribute":
                    dictionary.getAttributeDictionary(response);
                    break;
                case "Action":
                    dictionary.getActionPolicyDictionary(response);
                    break;
                case "BRMSParamTemplate":
                    dictionary.getBRMSParamDictionary(response);
                    break;
                case "VSCLAction":
                    dictionary.getVSCLAction(response);
                    break;
                case "VNFType":
                    dictionary.getVnfType(response);
                    break;
                case "PEPOptions":
                    dictionary.getPEPOptions(response);
                    break;
                case "Varbind":
                    dictionary.getVarbind(response);
                    break;
                case "Service":
                    dictionary.getServiceType(response);
                    break;
                case "Site":
                    dictionary.getSiteType(response);
                    break;
                case "Settings":
                    dictionary.getSettingsDictionary(response);
                    break;
                case "RainyDayTreatments":
                    dictionary.getRainyDayDictionary(response);
                    break;
                case "DescriptiveScope":
                    dictionary.getDescriptiveDictionary(response);
                    break;
                case "ActionList":
                    dictionary.getActionListDictionary(response);
                    break;
                case "ProtocolList":
                    dictionary.getProtocolListDictionary(response);
                    break;
                case "Zone":
                    dictionary.getZoneDictionary(response);
                    break;
                case "SecurityZone":
                    dictionary.getSecurityZoneDictionary(response);
                    break;
                case "PrefixList":
                    dictionary.getPrefixListDictionary(response);
                    break;
                case "AddressGroup":
                    dictionary.getAddressGroupDictionary(response);
                    break;
                case "ServiceGroup":
                    dictionary.getServiceGroupDictionary(response);
                    break;
                case "ServiceList":
                    dictionary.getServiceListDictionary(response);
                    break;
                case "TermList":
                case "RuleList":
                case "FirewallRuleList":
                case "Term":
                    dictionary.getTermListDictionary(response);
                    break;
                case "MicroServiceLocation":
                    dictionary.getMicroServiceLocationDictionary(response);
                    break;
                case "MicroServiceConfigName":
                    dictionary.getMicroServiceConfigNameDictionary(response);
                    break;
                case "DCAEUUID":
                    dictionary.getDCAEUUIDDictionary(response);
                    break;
                case "MicroServiceModels":
                    dictionary.getMicroServiceModelsDictionary(response);
                    break;
                case "MicroServiceDictionary":
                    dictionary.getMicroServiceDictionary(response);
                    break;
                case "OptimizationModels":
                    dictionary.getOptimizationModelsDictionary(response);
                    break;
                case "PolicyScopeService":
                    dictionary.getPSServiceDictionary(response);
                    break;
                case "PolicyScopeResource":
                    dictionary.getPSResourceDictionary(response);
                    break;
                case "PolicyScopeType":
                    dictionary.getPSTypeDictionary(response);
                    break;
                case "PolicyScopeClosedLoop":
                    dictionary.getPSClosedLoopDictionary(response);
                    break;
                case "GroupPolicyScopeList":
                    dictionary.getPSGroupScopeDictionary(response);
                    break;
                case "RiskType":
                    dictionary.getRiskTypeDictionary(response);
                    break;
                case "SafePolicyWarning":
                    dictionary.getSafePolicyWarningDictionary(response);
                    break;
                default:
                    extendedOptions(dictionaryType, request, response, true);
            }
        } catch (Exception e) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Error Querying the Database: " + e.getMessage();
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "DictionaryHandler", " Error Querying the Database.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader("error", "dictionaryDBQuery");
            response.addHeader("error", message);
        }
    }

    /**
     * Can be used to extend the services.
     * <p>
     * getflag=true indicates Get Request.
     * getflag=false indicates Put Request.
     *
     * @return
     */
    public String extendedOptions(String dictionaryType, HttpServletRequest request, HttpServletResponse response,
                                  boolean getflag) {
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
                case "OnapName":
                    result = dictionary.saveOnapDictionary(request, response);
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
                case "RainyDayTreatments":
                    result = dictionary.saveRainyDayDictionary(request, response);
                    break;
                case "DescriptiveScope":
                    result = dictionary.saveDescriptiveDictionary(request, response);
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
                case "MicroServiceDictionary":
                    result = dictionary.saveMicroServiceDictionary(request, response);
                    break;
                case "OptimizationModels":
                    result = dictionary.saveOptimizationModelsDictionary(request, response);
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
                default:
                    result = extendedOptions(dictionaryType, request, response, false);
                    if (result == null) {
                        return;
                    } else {
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
                response.addHeader("operation", "updateDictionary");
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
