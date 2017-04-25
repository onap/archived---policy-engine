package org.openecomp.policy.admin;

import org.openecomp.policy.controller.ActionPolicyController;
import org.openecomp.policy.controller.CreateBRMSParamController;
import org.openecomp.policy.controller.CreateBRMSRawController;
import org.openecomp.policy.controller.CreateClosedLoopFaultController;
import org.openecomp.policy.controller.CreateClosedLoopPMController;
import org.openecomp.policy.controller.CreateDcaeMicroServiceController;
import org.openecomp.policy.controller.CreateFirewallController;
import org.openecomp.policy.controller.CreatePolicyController;
import org.openecomp.policy.controller.DecisionPolicyController;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.jpa.PolicyEntity;

import com.att.research.xacml.util.XACMLProperties;

public class PolicyAdapter {

	public void configure(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		String	policyNameValue = null ;
		String	configPolicyName = null ;
		if(extendedOptions(policyAdapter, entity)){
			return;
		}
		if(policyAdapter.getPolicyName().startsWith("Config_PM")){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "ClosedLoop_PM";
		}else if(policyAdapter.getPolicyName().startsWith("Config_Fault")){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "ClosedLoop_Fault";
		}else if(policyAdapter.getPolicyName().startsWith("Config_FW")){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "Firewall Config";
		}else if(policyAdapter.getPolicyName().startsWith("Config_BRMS_Raw")){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "BRMS_Raw";
		}else if(policyAdapter.getPolicyName().startsWith("Config_BRMS_Param")){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "BRMS_Param";
		}else if(policyAdapter.getPolicyName().startsWith("Config_MS")){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "Micro Service";
		}else if(policyAdapter.getPolicyName().startsWith("Action") || policyAdapter.getPolicyName().startsWith("Decision") ){
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
		}else{
			policyNameValue = policyAdapter.getPolicyName().substring(0, policyAdapter.getPolicyName().indexOf("_"));
			configPolicyName = "Base";
		}
		if (policyNameValue != null) {
			policyAdapter.setPolicyType(policyNameValue);
		}
		if (configPolicyName != null) {
			policyAdapter.setConfigPolicyType(configPolicyName);
		}

		if("Action".equalsIgnoreCase(policyAdapter.getPolicyType())){
			ActionPolicyController actionController = new ActionPolicyController();
			actionController.prePopulateActionPolicyData(policyAdapter, entity);
		}
		if("Decision".equalsIgnoreCase(policyAdapter.getPolicyType())){
			DecisionPolicyController decisionController = new DecisionPolicyController();
			decisionController.prePopulateDecisionPolicyData(policyAdapter, entity);
		}
		if("Config".equalsIgnoreCase(policyAdapter.getPolicyType())){
			if("Base".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreatePolicyController baseController = new CreatePolicyController();
				baseController.prePopulateBaseConfigPolicyData(policyAdapter, entity);
			}
			else if("BRMS_Raw".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreateBRMSRawController brmsController = new CreateBRMSRawController();
				brmsController.prePopulateBRMSRawPolicyData(policyAdapter, entity);
			}
			else if("BRMS_Param".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreateBRMSParamController paramController = new CreateBRMSParamController();
				paramController.prePopulateBRMSParamPolicyData(policyAdapter, entity);
			}
			else if("ClosedLoop_Fault".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreateClosedLoopFaultController newFaultTemplate =  new CreateClosedLoopFaultController();
				newFaultTemplate.prePopulateClosedLoopFaultPolicyData(policyAdapter, entity);
			}
			else if("ClosedLoop_PM".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreateClosedLoopPMController pmController = new CreateClosedLoopPMController();
				pmController.prePopulateClosedLoopPMPolicyData(policyAdapter, entity);
			}
			else if("Micro Service".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreateDcaeMicroServiceController msController = new CreateDcaeMicroServiceController();
				msController.prePopulateDCAEMSPolicyData(policyAdapter, entity);
			}
			else if("Firewall Config".equalsIgnoreCase(policyAdapter.getConfigPolicyType())){
				CreateFirewallController firewallController = new CreateFirewallController();
				firewallController.prePopulateFWPolicyData(policyAdapter, entity);
			}
		}
	}
	
	public boolean extendedOptions(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		return false;
	}

	public static PolicyAdapter getInstance() {
		try {
			Class<?> policyAdapter = Class.forName(XACMLProperties.getProperty("policyAdapter.impl.className", PolicyAdapter.class.getName()));
			return (PolicyAdapter) policyAdapter.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

}
