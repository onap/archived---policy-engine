/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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


// TODO: Auto-generated Javadoc
/**
 * The Class DictionaryService.
 */
@Service("DictionaryService")
public class DictionaryService {

    /**
     * Save onap dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    /*
     * Methods that call the controller method directly to Save and Update dictionary data
     */
    public String saveOnapDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DictionaryController dictionary = new DictionaryController();
        ModelAndView result = dictionary.saveOnapDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save attribute dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveAttributeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DictionaryController dictionary = new DictionaryController();
        ModelAndView result = dictionary.saveAttributeDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save action policy dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveActionPolicyDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        ActionPolicyDictionaryController action = new ActionPolicyDictionaryController();
        ModelAndView result = action.saveActionPolicyDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save BRMS param dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveBRMSParamDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        BRMSDictionaryController dictionary = new BRMSDictionaryController();
        ModelAndView result = dictionary.saveBRMSParamDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save VSCL action.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveVSCLAction(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        ModelAndView result = dictionary.saveVSCLAction(request, response);
        return result.getViewName();
    }

    /**
     * Save vnf type.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveVnfType(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        ModelAndView result = dictionary.saveVnfType(request, response);
        return result.getViewName();
    }

    /**
     * Save PEP options.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePEPOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        ModelAndView result = dictionary.savePEPOptions(request, response);
        return result.getViewName();
    }

    /**
     * Save varbind.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveVarbind(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        ModelAndView result = dictionary.saveVarbind(request, response);
        return result.getViewName();
    }

    /**
     * Save service type.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveServiceType(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        ModelAndView result = dictionary.saveServiceType(request, response);
        return result.getViewName();
    }

    /**
     * Save site type.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveSiteType(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        ModelAndView result = dictionary.saveSiteType(request, response);
        return result.getViewName();
    }

    /**
     * Save settings dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveSettingsDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
        ModelAndView result = dictionary.saveSettingsDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save rainy day dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveRainyDayDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
        ModelAndView result = dictionary.saveRainyDayDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save descriptive dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveDescriptiveDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        DescriptiveDictionaryController dictionary = new DescriptiveDictionaryController();
        ModelAndView result = dictionary.saveDescriptiveDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save action list dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveActionListDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveActionListDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save protocol list dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveProtocolListDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveProtocolListDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save zone dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveZoneDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save security zone dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveSecurityZoneDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save prefix list dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePrefixListDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.savePrefixListDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save address group dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveAddressGroupDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save service group dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveServiceGroupDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save service list dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveServiceListDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveServiceListDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save term list dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        ModelAndView result = dictionary.saveTermListDictionary(request, response);
        return result.getViewName();
    }



    /**
     * Save micro service location dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveMicroServiceLocationDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        ModelAndView result = dictionary.saveMicroServiceLocationDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save micro service config name dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveMicroServiceConfigNameDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        ModelAndView result = dictionary.saveMicroServiceConfigNameDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save micro service dictionary data.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveMicroServiceDictionaryData(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        ModelAndView result = dictionary.saveDictionaryData(request, response);
        return result.getViewName();
    }

    /**
     * Save DCAEUUID dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveDCAEUUIDDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        ModelAndView result = dictionary.saveDCAEUUIDDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save micro service models dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveMicroServiceModelsDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        ModelAndView result = dictionary.saveMicroServiceModelsDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save micro service dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveMicroServiceDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        ModelAndView result = dictionary.saveMicroServiceAttributeDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save optimization models dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveOptimizationModelsDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        OptimizationDictionaryController dictionary = new OptimizationDictionaryController();
        ModelAndView result = dictionary.saveOptimizationModelsDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save PS service dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePSServiceDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        ModelAndView result = dictionary.savePSServiceDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save PS resource dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePSResourceDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        ModelAndView result = dictionary.savePSResourceDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save PS type dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePSTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        ModelAndView result = dictionary.savePSTypeDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save PS closed loop dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePSClosedLoopDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        ModelAndView result = dictionary.savePSClosedLoopDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save PS group scope dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String savePSGroupScopeDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        ModelAndView result = dictionary.savePSGroupScopeDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save risk type dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveRiskTypeDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {

        SafePolicyController dictionary = new SafePolicyController();
        ModelAndView result = dictionary.saveRiskTypeDictionary(request, response);
        return result.getViewName();
    }

    /**
     * Save safe policy warning dictionary.
     *
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String saveSafePolicyWarningDictionary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        SafePolicyController dictionary = new SafePolicyController();
        ModelAndView result = dictionary.saveSafePolicyWarningDictionary(request, response);
        return result.getViewName();
    }


    /**
     * Gets the onap dictionary.
     *
     * @param response the response
     * @return the onap dictionary
     */
    /*
     * Methods that call the controller get methods directly to get dictionary data
     */
    public void getOnapDictionary(HttpServletResponse response) {
        DictionaryController dictionary = new DictionaryController();
        dictionary.getOnapNameDictionaryEntityData(response);
    }

    /**
     * Gets the attribute dictionary.
     *
     * @param response the response
     * @return the attribute dictionary
     */
    public void getAttributeDictionary(HttpServletResponse response) {
        DictionaryController dictionary = new DictionaryController();
        dictionary.getAttributeDictionaryEntityData(response);
    }

    /**
     * Gets the action policy dictionary.
     *
     * @param response the response
     * @return the action policy dictionary
     */
    public void getActionPolicyDictionary(HttpServletResponse response) {
        ActionPolicyDictionaryController action = new ActionPolicyDictionaryController();
        action.getActionPolicyDictionaryEntityData(response);
    }

    /**
     * Gets the BRMS param dictionary.
     *
     * @param response the response
     * @return the BRMS param dictionary
     */
    public void getBRMSParamDictionary(HttpServletResponse response) {
        BRMSDictionaryController dictionary = new BRMSDictionaryController();
        dictionary.getBRMSParamDictionaryEntityData(response);
    }

    /**
     * Gets the VSCL action.
     *
     * @param response the response
     * @return the VSCL action
     */
    public void getVSCLAction(HttpServletResponse response) {
        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        dictionary.getVSCLActionDictionaryEntityData(response);
    }

    /**
     * Gets the vnf type.
     *
     * @param response the response
     * @return the vnf type
     */
    public void getVnfType(HttpServletResponse response) {
        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        dictionary.getVNFTypeDictionaryEntityData(response);
    }

    /**
     * Gets the PEP options.
     *
     * @param response the response
     * @return the PEP options
     */
    public void getPEPOptions(HttpServletResponse response) {
        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        dictionary.getPEPOptionsDictionaryEntityData(response);
    }

    /**
     * Gets the varbind.
     *
     * @param response the response
     * @return the varbind
     */
    public void getVarbind(HttpServletResponse response) {
        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        dictionary.getVarbindDictionaryEntityData(response);
    }

    /**
     * Gets the service type.
     *
     * @param response the response
     * @return the service type
     */
    public void getServiceType(HttpServletResponse response) {
        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        dictionary.getClosedLoopServiceDictionaryEntityData(response);
    }

    /**
     * Gets the site type.
     *
     * @param response the response
     * @return the site type
     */
    public void getSiteType(HttpServletResponse response) {
        ClosedLoopDictionaryController dictionary = new ClosedLoopDictionaryController();
        dictionary.getClosedLoopSiteDictionaryEntityData(response);
    }

    /**
     * Gets the settings dictionary.
     *
     * @param response the response
     * @return the settings dictionary
     */
    public void getSettingsDictionary(HttpServletResponse response) {
        DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
        dictionary.getSettingsDictionaryEntityData(response);
    }

    /**
     * Gets the rainy day dictionary.
     *
     * @param response the response
     * @return the rainy day dictionary
     */
    public void getRainyDayDictionary(HttpServletResponse response) {
        DecisionPolicyDictionaryController dictionary = new DecisionPolicyDictionaryController();
        dictionary.getRainyDayDictionaryEntityData(response);
    }

    /**
     * Gets the descriptive dictionary.
     *
     * @param response the response
     * @return the descriptive dictionary
     */
    public void getDescriptiveDictionary(HttpServletResponse response) {
        DescriptiveDictionaryController dictionary = new DescriptiveDictionaryController();
        dictionary.getDescriptiveDictionaryEntityData(response);
    }

    /**
     * Gets the action list dictionary.
     *
     * @param response the response
     * @return the action list dictionary
     */
    public void getActionListDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getActionListDictionaryEntityData(response);
    }

    /**
     * Gets the protocol list dictionary.
     *
     * @param response the response
     * @return the protocol list dictionary
     */
    public void getProtocolListDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getProtocolListDictionaryEntityData(response);
    }

    /**
     * Gets the zone dictionary.
     *
     * @param response the response
     * @return the zone dictionary
     */
    public void getZoneDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getZoneDictionaryEntityData(response);
    }

    /**
     * Gets the security zone dictionary.
     *
     * @param response the response
     * @return the security zone dictionary
     */
    public void getSecurityZoneDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getSecurityZoneDictionaryEntityData(response);
    }

    /**
     * Gets the prefix list dictionary.
     *
     * @param response the response
     * @return the prefix list dictionary
     */
    public void getPrefixListDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getPrefixListDictionaryEntityData(response);
    }

    /**
     * Gets the address group dictionary.
     *
     * @param response the response
     * @return the address group dictionary
     */
    public void getAddressGroupDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getAddressGroupDictionaryEntityData(response);
    }

    /**
     * Gets the service group dictionary.
     *
     * @param response the response
     * @return the service group dictionary
     */
    public void getServiceGroupDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getServiceGroupDictionaryEntityData(response);
    }

    /**
     * Gets the service list dictionary.
     *
     * @param response the response
     * @return the service list dictionary
     */
    public void getServiceListDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getServiceListDictionaryEntityData(response);
    }

    /**
     * Gets the term list dictionary.
     *
     * @param response the response
     * @return the term list dictionary
     */
    public void getTermListDictionary(HttpServletResponse response) {
        FirewallDictionaryController dictionary = new FirewallDictionaryController();
        dictionary.getTermListDictionaryEntityData(response);
    }


    /**
     * Gets the micro service location dictionary.
     *
     * @param response the response
     * @return the micro service location dictionary
     */
    public void getMicroServiceLocationDictionary(HttpServletResponse response) {
        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        dictionary.getMicroServiceLocationDictionaryEntityData(response);
    }

    /**
     * Gets the micro service config name dictionary.
     *
     * @param response the response
     * @return the micro service config name dictionary
     */
    public void getMicroServiceConfigNameDictionary(HttpServletResponse response) {
        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        dictionary.getMicroServiceConfigNameDictionaryEntityData(response);
    }

    /**
     * Gets the micro service dictionary data.
     *
     * @param response the response
     * @return the micro service dictionary data
     */
    public void getMicroServiceDictionaryData(HttpServletResponse response) {
        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        dictionary.getMicroServiceDictNameDictionaryEntityData(response);
    }

    /**
     * Gets the DCAEUUID dictionary.
     *
     * @param response the response
     * @return the DCAEUUID dictionary
     */
    public void getDCAEUUIDDictionary(HttpServletResponse response) {
        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        dictionary.getDCAEUUIDDictionaryEntityData(response);
    }

    /**
     * Gets the micro service models dictionary.
     *
     * @param response the response
     * @return the micro service models dictionary
     */
    public void getMicroServiceModelsDictionary(HttpServletResponse response) {
        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        dictionary.getMicroServiceModelsDictionaryEntityData(response);
    }

    /**
     * Gets the micro service dictionary.
     *
     * @param response the response
     * @return the micro service dictionary
     */
    public void getMicroServiceDictionary(HttpServletResponse response) {
        MicroServiceDictionaryController dictionary = new MicroServiceDictionaryController();
        dictionary.getMicroServiceModelsDictionaryEntityData(response);
    }

    /**
     * Gets the optimization models dictionary.
     *
     * @param response the response
     * @return the optimization models dictionary
     */
    public void getOptimizationModelsDictionary(HttpServletResponse response) {
        OptimizationDictionaryController dictionary = new OptimizationDictionaryController();
        dictionary.getOptimizationModelsDictionaryEntityData(response);
    }

    /**
     * Gets the PS service dictionary.
     *
     * @param response the response
     * @return the PS service dictionary
     */
    public void getPSServiceDictionary(HttpServletResponse response) {
        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        dictionary.getPSServiceEntityData(response);
    }

    /**
     * Gets the PS resource dictionary.
     *
     * @param response the response
     * @return the PS resource dictionary
     */
    public void getPSResourceDictionary(HttpServletResponse response) {
        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        dictionary.getPSResourceEntityData(response);
    }

    /**
     * Gets the PS type dictionary.
     *
     * @param response the response
     * @return the PS type dictionary
     */
    public void getPSTypeDictionary(HttpServletResponse response) {
        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        dictionary.getPSTypeEntityData(response);
    }

    /**
     * Gets the PS closed loop dictionary.
     *
     * @param response the response
     * @return the PS closed loop dictionary
     */
    public void getPSClosedLoopDictionary(HttpServletResponse response) {
        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        dictionary.getPSClosedLoopEntityData(response);
    }

    /**
     * Gets the PS group scope dictionary.
     *
     * @param response the response
     * @return the PS group scope dictionary
     */
    public void getPSGroupScopeDictionary(HttpServletResponse response) {
        PolicyScopeDictionaryController dictionary = new PolicyScopeDictionaryController();
        dictionary.getGroupPolicyScopeEntityData(response);
    }

    /**
     * Gets the risk type dictionary.
     *
     * @param response the response
     * @return the risk type dictionary
     */
    public void getRiskTypeDictionary(HttpServletResponse response) {
        SafePolicyController dictionary = new SafePolicyController();
        dictionary.getRiskTypeDictionaryEntityData(response);
    }

    /**
     * Gets the safe policy warning dictionary.
     *
     * @param response the response
     * @return the safe policy warning dictionary
     */
    public void getSafePolicyWarningDictionary(HttpServletResponse response) {
        SafePolicyController dictionary = new SafePolicyController();
        dictionary.getSafePolicyWarningeEntityData(response);
    }
}
