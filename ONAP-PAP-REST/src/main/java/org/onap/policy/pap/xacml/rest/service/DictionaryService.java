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
package org.onap.policy.pap.xacml.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.controller.ActionPolicyDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.BRMSDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.ClosedLoopDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DecisionPolicyDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DescriptiveDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DictionaryController;
import org.onap.policy.pap.xacml.rest.controller.EnforcerDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.FirewallDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.MicroServiceDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.PolicyScopeDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.SafePolicyController;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;


@Service("DictionaryService")
public class DictionaryService {
	
	private static final Logger LOGGER	= FlexLogger.getLogger(DictionaryService.class);

	/*
	 * Methods that call the controller method directly to Save and Update dictionary data
	 */
	public String saveOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DictionaryController dictionary = new DictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveOnapDictionary(request, response);
			responseString = result.getViewName();
			
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
		
	}
	
	public String saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DictionaryController dictionary = new DictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveAttributeDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ActionPolicyDictionaryController action = new ActionPolicyDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = action.saveActionPolicyDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		BRMSDictionaryController dictionary = new BRMSDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveBRMSParamDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveVSCLAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveVSCLAction(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveVnfType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveVnfType(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String savePEPOptions(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePEPOptions(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveVarbind(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveVarbind(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveServiceType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveServiceType(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveSiteType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveSiteType(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		
		return responseString;
	}
	
	public String saveSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveSettingsDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DescriptiveDictionaryController dictionary = new DescriptiveDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveDescriptiveDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveEnforcerDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		EnforcerDictionaryController dictionary = new EnforcerDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveEnforcerDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveActionListDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveProtocolListDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveZoneDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveSecurityZoneDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String savePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePrefixListDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveAddressGroupDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveServiceGroupDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveServiceListDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveTermListDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	
	
	
	public String saveMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveMicroServiceLocationDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveMicroServiceConfigNameDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveDCAEUUIDDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveMicroServiceModelsDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveMicroServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveMicroServiceAttributeDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePSServiceDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePSResourceDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePSTypeDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePSClosedLoopDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.savePSGroupScopeDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		SafePolicyController dictionary = new SafePolicyController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveRiskTypeDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	public String saveSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		SafePolicyController dictionary = new SafePolicyController();
		String responseString = null;
		try {
			ModelAndView result = dictionary.saveSafePolicyWarningDictionary(request, response);
			responseString = result.getViewName();
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
		return responseString;
	}
	
	
	/*
	 * Methods that call the controller get methods directly to get dictionary data
	 */
	public void getOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DictionaryController dictionary = new DictionaryController();
		try {
			dictionary.getOnapNameDictionaryEntityData(request, response);					
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}		
	}
	
	public void getAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DictionaryController dictionary = new DictionaryController();
		try {
			dictionary.getAttributeDictionaryEntityData(request, response);
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ActionPolicyDictionaryController action = new ActionPolicyDictionaryController();
		try {
			action.getActionPolicyDictionaryEntityData(request, response);	
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		BRMSDictionaryController dictionary = new BRMSDictionaryController();
		try {
			dictionary.getBRMSParamDictionaryEntityData(request, response);
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
	}
	
	public void getVSCLAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		try {
			dictionary.getVSCLActionDictionaryEntityData(request, response);
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getVnfType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		try {
			dictionary.getVNFTypeDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
	}
	
	public void getPEPOptions(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		try {
			dictionary.getPEPOptionsDictionaryEntityData(request, response);
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getVarbind(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		try {
			dictionary.getVarbindDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
		
	}
	
	public void getServiceType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		try {
			dictionary.getClosedLoopServiceDictionaryEntityData(request, response);
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getSiteType(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
		try {
			dictionary.getClosedLoopSiteDictionaryEntityData(request, response);	
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
		try {
			dictionary.getSettingsDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}		
	}
	
	public void getDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		DescriptiveDictionaryController dictionary = new DescriptiveDictionaryController();
		try {
			dictionary.getDescriptiveDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			
		}
	}
	
	public void getEnforcerDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		EnforcerDictionaryController dictionary = new EnforcerDictionaryController();
		try {
			dictionary.getEnforcerDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getActionListDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getProtocolListDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getZoneDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getSecurityZoneDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getPrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getPrefixListDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getAddressGroupDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getServiceGroupDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getServiceListDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
		try {
			dictionary.getTermListDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	
	public void getMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		try {
			dictionary.getMicroServiceLocationDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		try {
			dictionary.getMicroServiceConfigNameDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		try {
			dictionary.getDCAEUUIDDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		try {
			dictionary.getMicroServiceModelsDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getMicroServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
		try {
			dictionary.getMicroServiceModelsDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getPSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		try {
			dictionary.getPSServiceEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getPSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		try {
			dictionary.getPSResourceEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getPSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		try {
			dictionary.getPSTypeEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getPSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		try {
			dictionary.getPSClosedLoopEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getPSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
		try {
			dictionary.getGroupPolicyScopeEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		SafePolicyController dictionary = new SafePolicyController();
		try {
			dictionary.getOnapNameDictionaryEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
	
	public void getSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		SafePolicyController dictionary = new SafePolicyController();
		try {
			dictionary.getSafePolicyWarningeEntityData(request, response);
		
		} catch (Exception e) {
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			
		}
	}
}