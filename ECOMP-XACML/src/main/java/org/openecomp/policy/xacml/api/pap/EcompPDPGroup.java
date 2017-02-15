package org.openecomp.policy.xacml.api.pap;

import java.util.Set;

import com.att.research.xacml.api.pap.PDPGroup;

public interface EcompPDPGroup extends PDPGroup {

	public Set<EcompPDP> getEcompPdps();
	
}
