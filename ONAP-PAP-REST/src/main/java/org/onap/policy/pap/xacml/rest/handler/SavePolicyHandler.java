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

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.pap.xacml.rest.policycontroller.PolicyCreation;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.std.pap.StdPAPPolicy;
import org.xml.sax.SAXException;

import com.att.research.xacml.util.XACMLProperties;

public class SavePolicyHandler {
    private static final Logger logger = FlexLogger.getLogger(SavePolicyHandler.class);
    private HashMap<String, String> ErrorHeaders = null;

    public void doPolicyAPIPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String operation = request.getParameter("operation");
        String policyType = request.getParameter("policyType");
        String apiflag = request.getParameter("apiflag");
        PolicyCreation creation = new PolicyCreation();
        if ( policyType != null ) {
            PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
            if("update".equalsIgnoreCase(operation)){
                policyAdapter.setEditPolicy(true);
            }else{
                policyAdapter.setEditPolicy(false);
            }

            // get the request content into a String
            String json = null;
            // read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
            java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
            scanner.useDelimiter("\\A");
            json =  scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            if(policyAdapter.isEditPolicy()){
                PolicyLogger.info("SavePolicyHandler: JSON request from API to update a policy: " + json);
            } else {
                PolicyLogger.info("SavePolicyHandler: JSON request from API to create a policy: " + json);
            }

            // convert Object sent as JSON into local object
            StdPAPPolicy policy = PolicyUtils.jsonStringToObject(json, StdPAPPolicy.class);
            //Set policyAdapter values including parentPath (Common to all policy types)
            try {
                PolicyLogger.info("SavePolicyHandler: Setting parameter values to PolicyAdapter");
                policyAdapter = setDataToPolicyAdapter(policy, policyType, apiflag);
                
                if(!extendedPolicyOptions(policyAdapter, response)){
                    creation.savePolicy(policyAdapter, response);
                }
                if ("update".equalsIgnoreCase(operation)) {
                    response.addHeader("operation",  "update");
                } else {
                    response.addHeader("operation", "create");
                }
            } catch (Exception e1) {
                logger.error("Could not set data to policy adapter "+e1.getMessage(),e1);
            }
        }
    }

    private PolicyRestAdapter setDataToPolicyAdapter(StdPAPPolicy policy, String policyType, String apiflag) throws ParserConfigurationException, ServletException, SAXException, IOException{
        PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
        policyAdapter.setApiflag(apiflag);
        /*
         * set policy adapter values for Building JSON object containing policy data
         */
        //Common among policy types
        policyAdapter.setPolicyName(policy.getPolicyName());
        policyAdapter.setPolicyDescription(policy.getPolicyDescription());
        policyAdapter.setOnapName(policy.getOnapName()); //Config Base and Decision Policies
        policyAdapter.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");

        policyAdapter.setPolicyType(policyType);
        policyAdapter.setDynamicFieldConfigAttributes(policy.getDynamicFieldConfigAttributes());
        policyAdapter.setEditPolicy(policy.isEditPolicy());
        policyAdapter.setEntityManagerFactory(XACMLPapServlet.getEmf());
        //Config Specific
        policyAdapter.setConfigName(policy.getConfigName());  //Base and Firewall
        policyAdapter.setConfigBodyData(policy.getConfigBodyData()); //Base
        policyAdapter.setConfigType((policy.getConfigType()!=null) ? policy.getConfigType().toUpperCase(): null);  //Base
        policyAdapter.setJsonBody(policy.getJsonBody()); //Firewall, ClosedLoop
        policyAdapter.setConfigPolicyType(policy.getConfigPolicyType());
        policyAdapter.setDraft(policy.isDraft()); //ClosedLoop_Fault
        policyAdapter.setServiceType(policy.getServiceType()); //ClosedLoop_PM
        policyAdapter.setUuid(policy.getUuid()); //Micro Service
        policyAdapter.setLocation(policy.getMsLocation()); //Micro Service
        policyAdapter.setPriority(policy.getPriority()); //Micro Service
        policyAdapter.setPolicyScope(policy.getDomainDir());
        policyAdapter.setRiskType(policy.getRiskType()); //Safe Policy Attributes
        policyAdapter.setRiskLevel(policy.getRiskLevel());//Safe Policy Attributes
        policyAdapter.setGuard(policy.getGuard());//Safe Policy Attributes
        policyAdapter.setTtlDate(policy.getTTLDate());//Safe Policy Attributes
        policyAdapter.setBrmsParamBody(policy.getDrlRuleAndUIParams());
        policyAdapter.setBrmsDependency(policy.getBrmsDependency()); // BRMS Policies.
        policyAdapter.setBrmsController(policy.getBrmsController()); // BRMS Policies.
        //Action Policy Specific
        policyAdapter.setActionAttribute(policy.getActionAttribute());  //comboDictValue
        policyAdapter.setActionPerformer(policy.getActionPerformer());
        policyAdapter.setDynamicRuleAlgorithmLabels(policy.getDynamicRuleAlgorithmLabels());
        policyAdapter.setDynamicRuleAlgorithmCombo(policy.getDynamicRuleAlgorithmCombo());
        policyAdapter.setDynamicRuleAlgorithmField1(policy.getDynamicRuleAlgorithmField1());
        policyAdapter.setDynamicRuleAlgorithmField2(policy.getDynamicRuleAlgorithmField2());
        //Decision Policy Specific
        policyAdapter.setDynamicSettingsMap(policy.getDynamicSettingsMap());
        policyAdapter.setRuleProvider(policy.getProviderComboBox());
        policyAdapter.setDomainDir(policyAdapter.getPolicyScope());
        policyAdapter.setRainydayMap(policy.getTreatments());
        policyAdapter.setRawXacmlPolicy(policy.getRawXacmlPolicy());
        
        return policyAdapter;
    }

    public boolean extendedPolicyOptions(PolicyRestAdapter policyAdapter, HttpServletResponse response){
        return false;
    }

    public void addErrorHeader(String key, String value){
        if(ErrorHeaders==null){
            ErrorHeaders= new HashMap<>();
        }
        ErrorHeaders.put(key, value);
    }

    public static SavePolicyHandler getInstance() {
        try {
            Class<?> savePolicyHandler = Class.forName(XACMLProperties.getProperty("savePolicy.impl.className", SavePolicyHandler.class.getName()));
            SavePolicyHandler instance = (SavePolicyHandler) savePolicyHandler.newInstance();
            return instance;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
