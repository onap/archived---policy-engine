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
package org.openecomp.policy.pap.xacml.rest.handler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.policy.common.logging.ECOMPLoggingContext;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;
import org.openecomp.policy.pap.xacml.rest.XACMLPapServlet;
import org.openecomp.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.openecomp.policy.pap.xacml.rest.elk.client.PolicyElasticSearchController;
import org.openecomp.policy.pap.xacml.rest.model.RemoveGroupPolicy;
import org.openecomp.policy.pap.xacml.rest.util.JPAUtils;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPAPPolicy;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;

public class DeleteHandler {

	private EcompPDPGroup newgroup;
	
	private static String papDbDriver = null;
	private static String papDbUrl = null;
	private static String papDbUser = null;
	private static String papDbPassword = null;

	public void doAPIDeleteFromPAP(HttpServletRequest request, HttpServletResponse response, ECOMPLoggingContext loggingContext) throws Exception {
		// get the request content into a String
		String json = null;
		java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
		scanner.useDelimiter("\\A");
		json =  scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		PolicyLogger.info("JSON request from API: " + json);
		// convert Object sent as JSON into local object
		StdPAPPolicy policy = PolicyUtils.jsonStringToObject(json, StdPAPPolicy.class);
		String policyName = policy.getPolicyName();
		Boolean policyVersionDeleted = false;
		String removeXMLExtension;
		int currentVersion;
		String removeVersionExtension;
		String splitPolicyName = null;
		String[] split = null;
		String status = "error";
		PolicyEntity policyEntity = null;
		JPAUtils jpaUtils = null;

		papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
		papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
		papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
		papDbPassword = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD);
		Connection con = null;
		
		try {
			jpaUtils = JPAUtils.getJPAUtilsInstance(XACMLPapServlet.getEmf());
		} catch (Exception e) {
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " Could not create JPAUtils instance on the PAP");
			response.addHeader("error", "jpautils");
			response.addHeader("operation", "delete");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		if (jpaUtils.dbLockdownIgnoreErrors()) {
			PolicyLogger.warn("Policies are locked down");
			response.addHeader("operation", "delete");
			response.addHeader("lockdown", "true");
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		}
		EntityManager em = (EntityManager) XACMLPapServlet.getEmf().createEntityManager();
		Query policyEntityQuery = null;
		try{
			if(policyName.endsWith(".xml")){
				removeXMLExtension = policyName.replace(".xml", "");
				currentVersion = Integer.parseInt(removeXMLExtension.substring(removeXMLExtension.lastIndexOf(".")+1));
				removeVersionExtension = removeXMLExtension.substring(0, removeXMLExtension.lastIndexOf("."));
				boolean queryCheck = true;
				if(policy.getDeleteCondition().equalsIgnoreCase("All Versions")){
					if(policyName.contains("Config_")){
						splitPolicyName = removeVersionExtension.replace(".Config_", ":Config_");
					}else if(policyName.contains("Action_")){
						splitPolicyName = removeVersionExtension.replace(".Action_", ":Action_");
					}else if(policyName.contains("Decision_")){
						splitPolicyName = removeVersionExtension.replace(".Decision_", ":Decision_");
					}
					split = splitPolicyName.split(":");
					policyEntityQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName LIKE :pName and p.scope=:pScope");
				}else if(policy.getDeleteCondition().equalsIgnoreCase("Current Version")) {
					if(policyName.contains("Config_")){
						splitPolicyName = policyName.replace(".Config_", ":Config_");
					}else if(policyName.contains("Action_")){
						splitPolicyName = policyName.replace(".Action_", ":Action_");
					}else if(policyName.contains("Decision_")){
						splitPolicyName = policyName.replace(".Decision_", ":Decision_");
					}
					split = splitPolicyName.split(":");
					queryCheck = false;
					policyEntityQuery = em.createQuery("SELECT p FROM PolicyEntity p WHERE p.policyName=:pName and p.scope=:pScope");
				}
				
				if(queryCheck){
					policyEntityQuery.setParameter("pName", "%"+split[1]+"%");
				}else{
					policyEntityQuery.setParameter("pName", split[1]);
				}
				
				policyEntityQuery.setParameter("pScope", split[0]);
				List<?> peResult = policyEntityQuery.getResultList();
				if(!peResult.isEmpty()){
					Query getPolicyVersion = em.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
					getPolicyVersion.setParameter("pname", removeVersionExtension.replace(".", File.separator));
					List<?> pvResult = getPolicyVersion.getResultList();
					PolicyVersion pVersion = (PolicyVersion) pvResult.get(0);
					int highestVersion = 0; 
					em.getTransaction().begin();
					Class.forName(papDbDriver);
					con = DriverManager.getConnection(papDbUrl,papDbUser,papDbPassword);
					
					if(policy.getDeleteCondition().equalsIgnoreCase("All Versions")){
						boolean groupCheck = checkPolicyGroupEntity(em, con, peResult);
						if(!groupCheck){
							for(Object peData : peResult){
								policyEntity = (PolicyEntity) peData;
								status = deletePolicyEntityData(em, policyEntity);
							}
						}else{
							status = "PolicyInPDP";
						}
						if(status.equals("error")){
							PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Exception Occured while deleting the Entity from Database.");
							response.addHeader("error", "unknown");
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
							return;
						}else if(status.equals("PolicyInPDP")){
							PolicyLogger.error(MessageCodes.GENERAL_WARNING + "Policy can't be deleted, it is active in PDP Groups.");
							response.addHeader("error", "unknown");
							response.setStatus(HttpServletResponse.SC_CONFLICT);
							return;
						}else{
							try{
								policyVersionDeleted = true;
								em.remove(pVersion);
							}catch(Exception e){
								policyVersionDeleted = false;
							}
						}
					}else if(policy.getDeleteCondition().equalsIgnoreCase("Current Version")){
						boolean groupCheck = checkPolicyGroupEntity(em, con, peResult);
						if(!groupCheck){
							policyEntity = (PolicyEntity) peResult.get(0);
							status = deletePolicyEntityData(em, policyEntity);
						}else{
							status = "PolicyInPDP";
						}
						
						if(status.equals("error")){
							PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Exception Occured while deleting the Entity from Database.");
							response.addHeader("error", "unknown");
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
							return;
						}else if(status.equals("PolicyInPDP")){
							PolicyLogger.error(MessageCodes.GENERAL_WARNING + "Policy can't be deleted, it is active in PDP Groups.");
							response.addHeader("error", "unknown");
							response.setStatus(HttpServletResponse.SC_CONFLICT);
							return;
						}else{
							if(currentVersion > 1){
								if(!peResult.isEmpty()){
									for(Object object : peResult){
										policyEntity = (PolicyEntity) object;
										String policyEntityName = policyEntity.getPolicyName().replace(".xml", "");
										int policyEntityVersion = Integer.parseInt(policyEntityName.substring(policyEntityName.lastIndexOf(".")+1));
										if(policyEntityVersion > highestVersion){
											highestVersion = policyEntityVersion;
										}
									}
								}
								pVersion.setActiveVersion(highestVersion);
								pVersion.setHigherVersion(highestVersion);
								try{
									policyVersionDeleted = true;
									em.persist(pVersion);
								}catch(Exception e){
									policyVersionDeleted = false;
								}
							}else{
								try{
									policyVersionDeleted = true;
									em.remove(pVersion);
								}catch(Exception e){
									policyVersionDeleted = false;
								}
							}
						}
					}
				}else{
					PolicyLogger.error(MessageCodes.ERROR_UNKNOWN + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.");
					response.addHeader("error", "unknown");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
					return;
				}
			}
			em.getTransaction().commit();
		}catch(Exception e){
			em.getTransaction().rollback();
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
			response.addHeader("error", "deleteDB");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		} finally {
			em.close();
			con.close();
		}

		if (policyVersionDeleted) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("successMapKey", "success");
			response.addHeader("operation", "delete");
			return;				
		} else {
			PolicyLogger.error(MessageCodes.ERROR_UNKNOWN + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.");
			response.addHeader("error", "unknown");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			return;
		}
	}
	
	public String deletePolicyEntityData(EntityManager em, PolicyEntity policyEntity) throws SQLException{
		PolicyElasticSearchController controller = new PolicyElasticSearchController();
		PolicyRestAdapter policyData = new PolicyRestAdapter();
		String policyName = policyEntity.getPolicyName();
		try{
			if(policyName.contains("Config_")){
				em.remove(policyEntity.getConfigurationData());
			}else if(policyName.contains("Action_")){
				em.remove(policyEntity.getActionBodyEntity());
			}
			String searchPolicyName = policyEntity.getScope() + "." + policyEntity.getPolicyName();
			policyData.setNewFileName(searchPolicyName);
			controller.deleteElk(policyData);
			em.remove(policyEntity);
		}catch(Exception e){
			return "error";
		}
		return "success";
	}
	
	public boolean checkPolicyGroupEntity(EntityManager em, Connection con, List<?> peResult) throws SQLException{
		for(Object peData : peResult){
			PolicyEntity policyEntity = (PolicyEntity) peData;
			Statement st = null;
			ResultSet rs = null;
			st = con.createStatement();
			rs = st.executeQuery("Select * from PolicyGroupEntity where policyid = '"+policyEntity.getPolicyId()+"'");
			boolean gEntityList = rs.next();
			rs.close();
			if(gEntityList){
				return true;
			}
		}
		return false;
	}

	public void doAPIDeleteFromPDP(HttpServletRequest request, HttpServletResponse response, ECOMPLoggingContext loggingContext) throws IOException {
		String policyName = request.getParameter("policyName");
		String groupId = request.getParameter("groupId");
		String responseString = null;
		// for PUT operations the group may or may not need to exist before the operation can be done
		EcompPDPGroup group = null;
		try {
			group = XACMLPapServlet.getPAPEngine().getGroup(groupId);
		} catch (PAPException e) {
			PolicyLogger.error("Exception occured While PUT operation is performing for PDP Group"+e);
		}
		if (group == null) {
			String message = "Unknown groupId '" + groupId + "'";
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.addHeader("error", "UnknownGroup");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
			return;
		} else {
			loggingContext.setServiceName("API:PAP.deletPolicyFromPDPGroup");
			if (policyName.contains("xml")) {
				PolicyLogger.debug("The full file name including the extension was provided for policyName.. continue.");
			} else {
				String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid policyName... "
						+ "policyName must be the full name of the file to be deleted including version and extension";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Invalid policyName... "
						+ "policyName must be the full name of the file to be deleted including version and extension");
				response.addHeader("error", message);
        		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			RemoveGroupPolicy removePolicy = new RemoveGroupPolicy((StdPDPGroup) group);
			PDPPolicy policy =  group.getPolicy(policyName);
			if (policy != null) {
				
				if ((policy.getId().contains("Config_MS_")) || (policy.getId().contains("BRMS_Param"))) {
					if (preSafetyCheck(policy)) {
						PolicyLogger.debug("Precheck Successful.");
					}
				}
				
				removePolicy.prepareToRemove(policy);
				EcompPDPGroup updatedGroup = removePolicy.getUpdatedObject();
				responseString = deletePolicyFromPDPGroup(updatedGroup, loggingContext);
			} else {
				String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy does not exist on the PDP.";
				PolicyLogger.error(message);
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Policy does not exist on the PDP.");
				response.addHeader("error", message);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}			
		}
		if (responseString.equals("success")) {
			PolicyLogger.info("Policy successfully deleted!");
			PolicyLogger.audit("Policy successfully deleted!");
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("successMapKey", "success");
			response.addHeader("operation", "delete");
			return;		
		} else if (responseString.equals("No Group")) {
			String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Group update had bad input.";
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error", "groupUpdate");
			response.addHeader("message", message);
			return;	
		} else if (responseString.equals("DB Error")) {
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error", "deleteDB");
			return;
		} else {
			PolicyLogger.error(MessageCodes.ERROR_UNKNOWN + " Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.");
			response.addHeader("error", "unknown");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			return;
		}
	}

	private String deletePolicyFromPDPGroup (EcompPDPGroup group, ECOMPLoggingContext loggingContext){
		PolicyDBDaoTransaction acPutTransaction = XACMLPapServlet.getDbDaoTransaction();
		String response = null;
		loggingContext.setServiceName("API:PAP.DeleteHandler");
		EcompPDPGroup existingGroup = null;
		try {
			existingGroup = XACMLPapServlet.getPAPEngine().getGroup(group.getId());
		} catch (PAPException e1) {
			PolicyLogger.error("Exception occured While Deleting Policy From PDP Group"+e1);
		}
		if (group == null || ! (group instanceof StdPDPGroup) || ! (group.getId().equals(existingGroup.getId()))) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id=" + existingGroup.getId() + " objectFromJSON="+group);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Failed - See Error.log");
			response = "No Group";
			return response;
		}
		// The Path on the PAP side is not carried on the RESTful interface with the AC
		// (because it is local to the PAP)
		// so we need to fill that in before submitting the group for update
		((StdPDPGroup)group).setDirectory(((StdPDPGroup)existingGroup).getDirectory());
		try{
			acPutTransaction.updateGroup(group, "XACMLPapServlet.doAPIDelete");
		} catch(Exception e){
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while updating group in the database: "
					+"group="+existingGroup.getId());
			response = "DB Error";
			return response;
		}
		try {
			XACMLPapServlet.getPAPEngine().updateGroup(group);
		} catch (PAPException e) {
			PolicyLogger.error("Exception occured While Updating PDP Groups"+e);
			response = "error in updateGroup method";
		}
		PolicyLogger.debug("Group '" + group.getId() + "' updated");
		acPutTransaction.commitTransaction();
		// Group changed, which might include changing the policies
		try {
			newgroup = existingGroup;
		}  catch (Exception e) {
			PolicyLogger.error("Exception occured in Group Change Method"+e);
			response = "error in groupChanged method";
		}
		if (response==null){
			response = "success";
			PolicyLogger.audit("Policy successfully deleted!");
			PolicyLogger.audit("Transaction Ended Successfully");
		}
		loggingContext.transactionEnded();
		PolicyLogger.audit("Transaction Ended");
		return response;
	}
	
	public EcompPDPGroup getDeletedGroup(){
		return newgroup;
	}
	
	public boolean preSafetyCheck(PDPPolicy policy) {
		return true;
	}
	
	public static DeleteHandler getInstance() {
		try {
			Class<?> deleteHandler = Class.forName(XACMLProperties.getProperty("deletePolicy.impl.className", DeleteHandler.class.getName()));
			DeleteHandler instance = (DeleteHandler) deleteHandler.newInstance(); 
			return instance;
		} catch (Exception e) {
			PolicyLogger.error(e.getMessage());
		}
		return null;
	}

}
