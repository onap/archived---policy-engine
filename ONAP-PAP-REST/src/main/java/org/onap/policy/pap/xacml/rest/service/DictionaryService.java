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
package org.onap.policy.pap.xacml.rest.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.pap.xacml.rest.controller.ActionPolicyDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.BRMSDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.ClosedLoopDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DecisionPolicyDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DescriptiveDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.DictionaryController;
import org.onap.policy.pap.xacml.rest.controller.FirewallDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.MicroServiceDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.OptimizationDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.PolicyScopeDictionaryController;
import org.onap.policy.pap.xacml.rest.controller.SafePolicyController;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;


@Service("DictionaryService")
public class DictionaryService {

	/*
	 * Methods that call the controller method directly to Save and Update dictionary data
	 */
	public String saveOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		DictionaryController dictionary = new DictionaryController();
			ModelAndView result = dictionary.saveOnapDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		DictionaryController dictionary = new DictionaryController();
			ModelAndView result = dictionary.saveAttributeDictionary(request, response);
		return result.getViewName();
		}
	
	public String saveActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		ActionPolicyDictionaryController action = new ActionPolicyDictionaryController();
			ModelAndView result = action.saveActionPolicyDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		BRMSDictionaryController dictionary = new BRMSDictionaryController();
			ModelAndView result = dictionary.saveBRMSParamDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveVSCLAction(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			ModelAndView result = dictionary.saveVSCLAction(request, response);
		return result.getViewName();
	}
	
	public String saveVnfType(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			ModelAndView result = dictionary.saveVnfType(request, response);
		return result.getViewName();
	}
	
	public String savePEPOptions(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			ModelAndView result = dictionary.savePEPOptions(request, response);
		return result.getViewName();
	}
	
	public String saveVarbind(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			ModelAndView result = dictionary.saveVarbind(request, response);
		return result.getViewName();
	}
	
	public String saveServiceType(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			ModelAndView result = dictionary.saveServiceType(request, response);
		return result.getViewName();
	}
	
	public String saveSiteType(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			ModelAndView result = dictionary.saveSiteType(request, response);
		return result.getViewName();
	}
	
	public String saveSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
			ModelAndView result = dictionary.saveSettingsDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveRainyDayDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
			ModelAndView result = dictionary.saveRainyDayDictionary(request, response);
		return result.getViewName();
	}
	
	public String removeNamingSequence(HttpServletRequest request, HttpServletResponse response) throws IOException{
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
			return dictionary.removeNamingSequence(request, response);
	}
	
	
	public String saveDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		DescriptiveDictionaryController dictionary = new DescriptiveDictionaryController();
			ModelAndView result = dictionary.saveDescriptiveDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveActionListDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveProtocolListDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveZoneDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveSecurityZoneDictionary(request, response);
		return result.getViewName();
	}
	
	public String savePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.savePrefixListDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveAddressGroupDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveServiceGroupDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveServiceListDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			ModelAndView result = dictionary.saveTermListDictionary(request, response);
		return result.getViewName();
	}
	
	
	
	
	public String saveMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			ModelAndView result = dictionary.saveMicroServiceLocationDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			ModelAndView result = dictionary.saveMicroServiceConfigNameDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			ModelAndView result = dictionary.saveDCAEUUIDDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			ModelAndView result = dictionary.saveMicroServiceModelsDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveMicroServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			ModelAndView result = dictionary.saveMicroServiceAttributeDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveOptimizationModelsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		OptimizationDictionaryController dictionary = new OptimizationDictionaryController();
			ModelAndView result = dictionary.saveOptimizationModelsDictionary(request, response);
		return result.getViewName();
	}
	
	public String savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			ModelAndView result = dictionary.savePSServiceDictionary(request, response);
		return result.getViewName();
	}
	
	public String savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			ModelAndView result = dictionary.savePSResourceDictionary(request, response);
		return result.getViewName();
	}
	
	public String savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			ModelAndView result = dictionary.savePSTypeDictionary(request, response);
		return result.getViewName();
	}
	
	public String savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			ModelAndView result = dictionary.savePSClosedLoopDictionary(request, response);
		return result.getViewName();
	}
	
	public String savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			ModelAndView result = dictionary.savePSGroupScopeDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		SafePolicyController dictionary = new SafePolicyController();
			ModelAndView result = dictionary.saveRiskTypeDictionary(request, response);
		return result.getViewName();
	}
	
	public String saveSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException{
		SafePolicyController dictionary = new SafePolicyController();
			ModelAndView result = dictionary.saveSafePolicyWarningDictionary(request, response);
		return result.getViewName();
	}
	
	
	/*
	 * Methods that call the controller get methods directly to get dictionary data
	 */
	public void getOnapDictionary(HttpServletResponse response){
		DictionaryController dictionary = new DictionaryController();
			dictionary.getOnapNameDictionaryEntityData(response);					
	}
	
	public void getAttributeDictionary(HttpServletResponse response){
		DictionaryController dictionary = new DictionaryController();
			dictionary.getAttributeDictionaryEntityData(response);
	}
	
	public void getActionPolicyDictionary(HttpServletResponse response){
		ActionPolicyDictionaryController action = new ActionPolicyDictionaryController();
			action.getActionPolicyDictionaryEntityData(response);	
	}
	
	public void getBRMSParamDictionary(HttpServletResponse response){
		BRMSDictionaryController dictionary = new BRMSDictionaryController();
			dictionary.getBRMSParamDictionaryEntityData(response);
	}
	
	public void getVSCLAction(HttpServletResponse response){
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			dictionary.getVSCLActionDictionaryEntityData(response);
	}
	
	public void getVnfType(HttpServletResponse response){
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			dictionary.getVNFTypeDictionaryEntityData(response);
	}
	
	public void getPEPOptions(HttpServletResponse response){
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			dictionary.getPEPOptionsDictionaryEntityData(response);
	}
	
	public void getVarbind(HttpServletResponse response){
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			dictionary.getVarbindDictionaryEntityData(response);
	}
	
	public void getServiceType(HttpServletResponse response){
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			dictionary.getClosedLoopServiceDictionaryEntityData(response);
	}
	
	public void getSiteType(HttpServletResponse response){
		ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
			dictionary.getClosedLoopSiteDictionaryEntityData(response);	
	}
	
	public void getSettingsDictionary(HttpServletResponse response){
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
			dictionary.getSettingsDictionaryEntityData(response);
	}
	
	public void getRainyDayDictionary(HttpServletResponse response){
		DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
			dictionary.getRainyDayDictionaryEntityData(response);
	}
	
	public void getDescriptiveDictionary(HttpServletResponse response){
		DescriptiveDictionaryController dictionary = new DescriptiveDictionaryController();
			dictionary.getDescriptiveDictionaryEntityData(response);
	}
	
	public void getActionListDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getActionListDictionaryEntityData(response);
	}
	
	public void getProtocolListDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getProtocolListDictionaryEntityData(response);
	}
	
	public void getZoneDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getZoneDictionaryEntityData(response);
	}
	
	public void getSecurityZoneDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getSecurityZoneDictionaryEntityData(response);
	}
	
	public void getPrefixListDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getPrefixListDictionaryEntityData(response);
	}
	
	public void getAddressGroupDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getAddressGroupDictionaryEntityData(response);
	}
	
	public void getServiceGroupDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getServiceGroupDictionaryEntityData(response);
	}
	
	public void getServiceListDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getServiceListDictionaryEntityData(response);
	}
	
	public void getTermListDictionary(HttpServletResponse response){
		FirewallDictionaryController dictionary = new FirewallDictionaryController();
			dictionary.getTermListDictionaryEntityData(response);
	}
	
	
	public void getMicroServiceLocationDictionary(HttpServletResponse response){
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			dictionary.getMicroServiceLocationDictionaryEntityData(response);
	}
	
	public void getMicroServiceConfigNameDictionary(HttpServletResponse response){
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			dictionary.getMicroServiceConfigNameDictionaryEntityData(response);
	}
	
	public void getDCAEUUIDDictionary(HttpServletResponse response){
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			dictionary.getDCAEUUIDDictionaryEntityData(response);
	}
	
	public void getMicroServiceModelsDictionary(HttpServletResponse response){
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			dictionary.getMicroServiceModelsDictionaryEntityData(response);
	}
	
	public void getMicroServiceDictionary(HttpServletResponse response){
		MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
			dictionary.getMicroServiceModelsDictionaryEntityData(response);
	}
	
	public void getOptimizationModelsDictionary(HttpServletResponse response){
		OptimizationDictionaryController dictionary = new OptimizationDictionaryController();
			dictionary.getOptimizationModelsDictionaryEntityData(response);
	}
	
	public void getPSServiceDictionary(HttpServletResponse response){
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			dictionary.getPSServiceEntityData(response);
	}
	
	public void getPSResourceDictionary(HttpServletResponse response){
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			dictionary.getPSResourceEntityData(response);
	}
	
	public void getPSTypeDictionary(HttpServletResponse response){
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			dictionary.getPSTypeEntityData(response);
	}
	
	public void getPSClosedLoopDictionary(HttpServletResponse response){
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			dictionary.getPSClosedLoopEntityData(response);
	}
	
	public void getPSGroupScopeDictionary(HttpServletResponse response){
		PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
			dictionary.getGroupPolicyScopeEntityData(response);
	}
	
	public void getRiskTypeDictionary(HttpServletResponse response){
		SafePolicyController dictionary = new SafePolicyController();
			dictionary.getRiskTypeDictionaryEntityData(response);
	}
	
	public void getSafePolicyWarningDictionary(HttpServletResponse response) {
		SafePolicyController dictionary = new SafePolicyController();
			dictionary.getSafePolicyWarningeEntityData(response);
	}
}
