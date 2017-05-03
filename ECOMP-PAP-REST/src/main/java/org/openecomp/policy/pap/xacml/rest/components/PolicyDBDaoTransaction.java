/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.xacml.rest.components;

import java.util.List;

import javax.persistence.PersistenceException;

import org.openecomp.policy.rest.jpa.GroupEntity;
import org.openecomp.policy.rest.jpa.PdpEntity;
import org.openecomp.policy.xacml.api.pap.EcompPDP;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;

import com.att.research.xacml.api.pap.PAPException;

public interface PolicyDBDaoTransaction {

	/**
	 * Commits (makes permanent) the current transaction. Also, notifies other PolicyDBDao instances on other PAP servers of the update.
	 * @throws IllegalStateException if the PolicyDBDao transaction has not been used or has been committed already.
	 * @throws PersistenceException if the commit fails for some reason
	 */
	public void commitTransaction();
	
	/**
	 * Create or update a policy
	 * @param policy A Policy object representing the policy to store or update
	 * @param username A string of the username you want to be stored for doing this operation
	 * @throws IllegalStateException If a transaction is open that has not yet been committed
	 * @throws PersistenceException If a database error occurs
	 * @throws IllegalArgumentException If the Policy's PolicyRestAdapter contains incorrect data.
	 */
	public void createPolicy(Policy policy, String username) throws IllegalStateException, PersistenceException, IllegalArgumentException;
	
	/**
	 * Create or update a policy
	 * @param filePath The file path of the policy xml file
	 * @param username A string of the username you want to be stored for doing this operation
	 * @throws IllegalStateException If a transaction is open that has not yet been committed
	 * @throws PersistenceException If a database error occurs
	 * @throws IllegalArgumentException If the file path is incorrect, or if it refers to a Config policy
	 */
	public void createPolicy(String filePath, String username) throws IllegalStateException, PersistenceException, IllegalArgumentException;
	
	/**
	 * Check if the PolicyDBDaoTransaction is currently open
	 * @return False if the PolicyDBDao transaction has not been used or has been committed already, true if it is open.
	 */
	public boolean isTransactionOpen();
	
	
	
	/**
	 * Delete an existing policy
	 * @param policyToDelete The file path of the policy to delete
	 * @throws IllegalArgumentException If the file path given can not be parsed
	 * @throws IllegalStateException If a transaction is open that has not yet been committed
	 * @throws PersistenceException If a database error occurs
	 */
	public void deletePolicy(String policyToDelete) throws IllegalStateException, PersistenceException, IllegalArgumentException;
	
	/**
	 * Rollback (undo) the current transaction.
	 */
	public void rollbackTransaction();
	
	/**
	 * Close the PolicyDBDaoTransaction without rolling back or doing anything. Just used to close the EntityManager
	 */
	public void close();
	
	
	/**
	 * Create a new PDP group in the database
	 * @param groupID The ID to name the new group (use PolicyDBDao.createNewPDPGroupId)
	 * @param groupName The name to use for the new group
	 * @param groupDescription Description of the new group (optional)
	 * @param username Username of the user performing the operation
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 */
	public void createGroup(String groupID, String groupName, String groupDescription, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	/**
	 * Updates a group in the database with a new name of description
	 * @param group The group with updated information. The id must match an existing group, but the name and description can be changed.
	 * @param username Username of the user performing the operation
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs or if the group can not be found
	 */
	public void updateGroup(EcompPDPGroup group, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	/**
	 * Updates a PDP in the database with new information
	 * @param pdp The PDP to update
	 * @param username Username of the user performing the operation
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs or if the pdp can not be found
	 */
	public void updatePdp(EcompPDP pdp, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	/**
	 * Change the default group in the database to the group provided.
	 * @param group The new group which should be set as default in the database
	 * @param username Username of the user performing the operation
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 */
	public void changeDefaultGroup(EcompPDPGroup group, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	/**
	 * Moves a PDP to a new group.
	 * @param pdp The PDP which is to be moved to a new group
	 * @param group The new group which the PDP should be added to
	 * @param username Username of the user performing the operation
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 */
	public void movePdp(EcompPDP pdp, EcompPDPGroup group, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	/**
	 * Add a new PDP to an existing group
	 * @param pdpID The ID to name the new PDP
	 * @param groupID The ID of the existing group to add the PDP to
	 * @param pdpName The name to use for the new PDP
	 * @param pdpDescription Description of the new PDP (optional)
	 * @param pdpJmxPort
	 * @param username Username of the user performing the operation
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 */
	public void addPdpToGroup(String pdpID, String groupID, String pdpName, String pdpDescription, int pdpJmxPort, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	/**
	 * Add an existing policy to an existing group
	 * @param group The ID of the existing group to add the policy to
	 * @param policyID The ID of an existing policy
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 */
	public void addPolicyToGroup(String group, String policyID, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	
	/**
	 * Delete an existing PDP group
	 * @param group A PDPGroup object representing the group to delete
	 * @param moveToGroup A PDPGroup object representing another existing group which PDPs in the group being deleted should be moved to
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 * @throws PAPException If an error relating to how groups are handled occurs
	 */
	public void deleteGroup(EcompPDPGroup group, EcompPDPGroup moveToGroup, String username)throws IllegalArgumentException, IllegalStateException, PersistenceException, PAPException;
	
	/**
	 * Removes an existing PDP from its group and deletes it.
	 * @param pdpID The ID of the existing PDP which should be deleted
	 * @throws IllegalArgumentException If non-optional parameters are null or empty strings
	 * @throws IllegalStateException If a transaction is already open
	 * @throws PersistenceException If a database error occurs
	 */
	public void removePdpFromGroup(String pdpID, String username) throws IllegalArgumentException, IllegalStateException, PersistenceException;
	
	public GroupEntity getGroup(long groupKey);
	public GroupEntity getGroup(String groupId);
	public List<?> getPdpsInGroup(long groupKey);
	public PdpEntity getPdp(long pdpKey);

	void renamePolicy(String oldPath, String newPath,String username);

	void clonePolicy(String oldPolicyPath, String newPolicyPath, String username);
		
}
