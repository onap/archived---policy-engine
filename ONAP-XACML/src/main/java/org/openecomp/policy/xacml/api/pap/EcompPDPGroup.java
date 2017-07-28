package org.openecomp.policy.xacml.api.pap;

import java.util.Set;

import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;

public interface EcompPDPGroup extends PDPGroup {

	public Set<EcompPDP> getEcompPdps();
	
	public Set<PDPPolicy> 				getSelectedPolicies();
	
	public String						getOperation();
}
