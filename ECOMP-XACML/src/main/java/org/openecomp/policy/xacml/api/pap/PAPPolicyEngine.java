/*-
 * ============LICENSE_START=======================================================
 * ECOMP-XACML
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
package org.openecomp.policy.xacml.api.pap;

import java.io.InputStream;
import java.util.Set;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;

public interface PAPPolicyEngine{
	
	public EcompPDPGroup getDefaultGroup() throws PAPException;
	
	public void						SetDefaultGroup(EcompPDPGroup group) throws PAPException;
	
	public void		newPDP(String id, EcompPDPGroup group, String name, String description, int jmxport) throws PAPException, NullPointerException;
	
	public void						newGroup(String name, String description) throws PAPException, NullPointerException;
	
	public EcompPDPGroup getGroup(String id) throws PAPException;
	
	public Set<EcompPDPGroup> getEcompPDPGroups() throws PAPException;
	
	public EcompPDPGroup getPDPGroup(EcompPDP pdp) throws PAPException;
	
	public PDPStatus getStatus(EcompPDP pdp) throws PAPException;
	
	public void movePDP(EcompPDP pdp, EcompPDPGroup newGroup) throws PAPException;
	
	public void updatePDP(EcompPDP pdp) throws PAPException;
	
	public void removePDP(EcompPDP pdp) throws PAPException; 
	
	public EcompPDP getPDP(String pdpId) throws PAPException;
	
	public void						updateGroup(EcompPDPGroup group) throws PAPException;
	
	public void						removeGroup(EcompPDPGroup group, EcompPDPGroup newGroup) throws PAPException, NullPointerException;
	
public void						publishPolicy(String id, String name, boolean isRoot, InputStream policy, EcompPDPGroup group) throws PAPException;
	
	// copy the given policy file into the group's directory, but do not include the policy in the group's policy set
	public void						copyPolicy(PDPPolicy policy, EcompPDPGroup group) throws PAPException;
	
	public void						removePolicy(PDPPolicy policy, EcompPDPGroup group) throws PAPException;
	
}
