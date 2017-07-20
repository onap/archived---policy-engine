package org.onap.policy.xacml.api.pap;

import java.util.Set;

import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;

public interface OnapPDPGroup extends PDPGroup {

	public Set<OnapPDP> getOnapPdps();
	
	public Set<PDPPolicy> 				getSelectedPolicies();
	
	public String						getOperation();
}
