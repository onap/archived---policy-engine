/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.admin;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controller.ActionPolicyController;
import org.onap.policy.controller.CreateBRMSParamController;
import org.onap.policy.controller.CreateBRMSRawController;
import org.onap.policy.controller.CreateClosedLoopFaultController;
import org.onap.policy.controller.CreateClosedLoopPMController;
import org.onap.policy.controller.CreateDcaeMicroServiceController;
import org.onap.policy.controller.CreateFirewallController;
import org.onap.policy.controller.CreateOptimizationController;
import org.onap.policy.controller.CreatePolicyController;
import org.onap.policy.controller.DecisionPolicyController;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.jpa.PolicyEntity;

import com.att.research.xacml.util.XACMLProperties;

public class PolicyAdapter {

	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyAdapter.class);

	public void configure(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		if(extendedOptions(policyAdapter, entity)){
			return;
		}
		String policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf('_'));
		String configPolicyName = getConfigPolicyName(policyAdapter);
		policyAdapter.setPolicyType(policyNameValue);

		if (configPolicyName != null) {
			policyAdapter.setConfigPolicyType(configPolicyName);
		}

		if("Action".equalsIgnoreCase(policyAdapter.getPolicyType())){
			new ActionPolicyController().prePopulateActionPolicyData(policyAdapter, entity);
		}
		if("Decision".equalsIgnoreCase(policyAdapter.getPolicyType())){
			new DecisionPolicyController().prePopulateDecisionPolicyData(policyAdapter, entity);
		}
		if("Config".equalsIgnoreCase(policyAdapter.getPolicyType())){
			prePopulatePolicyData(policyAdapter, entity);
		}
	}

	private String getConfigPolicyName(PolicyRestAdapter policyAdapter) {
		String	configPolicyName = null ;
		if(policyAdapter.getPolicyName().startsWith("Config_PM")){
			configPolicyName = "ClosedLoop_PM";
		}else if(policyAdapter.getPolicyName().startsWith("Config_Fault")){
			configPolicyName = "ClosedLoop_Fault";
		}else if(policyAdapter.getPolicyName().startsWith("Config_FW")){
			configPolicyName = "Firewall Config";
		}else if(policyAdapter.getPolicyName().startsWith("Config_BRMS_Raw")){
			configPolicyName = "BRMS_Raw";
		}else if(policyAdapter.getPolicyName().startsWith("Config_BRMS_Param")){
			configPolicyName = "BRMS_Param";
		}else if(policyAdapter.getPolicyName().startsWith("Config_MS")){
			configPolicyName = "Micro Service";
		}else if(policyAdapter.getPolicyName().startsWith("Config_OOF")){
			configPolicyName = "Optimization";
		}else if(policyAdapter.getPolicyName().startsWith("Action") || policyAdapter.getPolicyName().startsWith("Decision") ){
			// No configPolicyName is applicable
		}else{
			configPolicyName = "Base";
		}
		return configPolicyName;
	}

	private void prePopulatePolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		if("Base".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreatePolicyController().prePopulateBaseConfigPolicyData(policyAdapter, entity);
		}
		else if("BRMS_Raw".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateBRMSRawController().prePopulateBRMSRawPolicyData(policyAdapter, entity);
		}
		else if("BRMS_Param".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateBRMSParamController().prePopulateBRMSParamPolicyData(policyAdapter, entity);
		}
		else if("ClosedLoop_Fault".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateClosedLoopFaultController().prePopulateClosedLoopFaultPolicyData(policyAdapter, entity);
		}
		else if("ClosedLoop_PM".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateClosedLoopPMController().prePopulateClosedLoopPMPolicyData(policyAdapter, entity);
		}
		else if("Micro Service".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateDcaeMicroServiceController().prePopulateDCAEMSPolicyData(policyAdapter, entity);
		}
		else if("Optimization".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateOptimizationController().prePopulatePolicyData(policyAdapter, entity);
		}
		else if("Firewall Config".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
			new CreateFirewallController().prePopulateFWPolicyData(policyAdapter, entity);
		}
	}

	private boolean extendedOptions(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		return false;
	}

	public static PolicyAdapter getInstance() {
		try {
			Class<?> policyAdapter = Class.forName(XACMLProperties.getProperty("policyAdapter.impl.className", PolicyAdapter.class.getName()));
			return (PolicyAdapter) policyAdapter.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			LOGGER.error("Exception Occured"+e);
		}
		return null;
	}

}