/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.openecomp.policy.adapter.AddressGroupJson;
import org.openecomp.policy.adapter.AddressJson;
import org.openecomp.policy.adapter.AddressMembers;
import org.openecomp.policy.adapter.DeployNowJson;
import org.openecomp.policy.adapter.PolicyAdapter;
import org.openecomp.policy.adapter.PrefixIPList;
import org.openecomp.policy.adapter.ServiceGroupJson;
import org.openecomp.policy.adapter.ServiceListJson;
import org.openecomp.policy.adapter.ServiceMembers;
import org.openecomp.policy.adapter.ServicesJson;
import org.openecomp.policy.adapter.Term;
import org.openecomp.policy.adapter.TermCollector;
import org.openecomp.policy.admin.PolicyNotificationMail;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.dao.RuleAlgorithmsDao;
import org.openecomp.policy.dao.WatchPolicyNotificationDao;
import org.openecomp.policy.elk.client.PolicyElasticSearchController;
import org.openecomp.policy.rest.dao.AddressGroupDao;
import org.openecomp.policy.rest.dao.FirewallDictionaryListDao;
import org.openecomp.policy.rest.dao.PrefixListDao;
import org.openecomp.policy.rest.dao.SecurityZoneDao;
import org.openecomp.policy.rest.dao.ServiceGroupDao;
import org.openecomp.policy.rest.dao.ServiceListDao;
import org.openecomp.policy.rest.dao.TermListDao;
import org.openecomp.policy.rest.jpa.AddressGroup;
import org.openecomp.policy.rest.jpa.FirewallDictionaryList;
import org.openecomp.policy.rest.jpa.GroupServiceList;
import org.openecomp.policy.rest.jpa.PREFIXLIST;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.RuleAlgorithms;
import org.openecomp.policy.rest.jpa.SecurityZone;
import org.openecomp.policy.rest.jpa.ServiceList;
import org.openecomp.policy.rest.jpa.TermList;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import com.att.research.xacml.api.XACML3;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Joiner;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

@Controller
@RequestMapping("/")
public class CreateFirewallController extends RestrictedBaseController {
	private static Logger logger	= FlexLogger.getLogger(CreateFirewallController.class);

	private static RuleAlgorithmsDao ruleAlgorithmsDao;
	private static PolicyVersionDao policyVersionDao;	
	private static PrefixListDao prefixListDao;
	private static ServiceListDao serviceListDao;
	private static TermListDao termListDao;
	private static ServiceGroupDao serviceGroupDao;
	private static AddressGroupDao	addressGroupDao;
	private static SecurityZoneDao securityZoneDao;
	private static SessionFactory sessionFactory; 	
	private static FirewallDictionaryListDao fwDictionaryListDao;
	private static WatchPolicyNotificationDao policyNotificationDao;

	List<String> expandablePrefixIPList = new ArrayList<String>();
	List<String> expandableServicesList= new ArrayList<String>();
	
	private String parentSecurityZone;
	
	
	public String getParentSecurityZone() {
		return parentSecurityZone;
	}

	public void setParentSecurityZone(String parentSecurityZone) {
		this.parentSecurityZone = parentSecurityZone;
	}



	@Autowired
	private CreateFirewallController(RuleAlgorithmsDao ruleAlgorithmsDao, PolicyVersionDao policyVersionDao, PrefixListDao prefixListDao,
			ServiceListDao serviceListDao, TermListDao termListDao, ServiceGroupDao serviceGroupDao, AddressGroupDao	addressGroupDao, SecurityZoneDao securityZoneDao, SessionFactory sessionFactory
			,FirewallDictionaryListDao fwDictionaryListDao, WatchPolicyNotificationDao policyNotificationDao){
		CreateFirewallController.addressGroupDao = addressGroupDao;
		CreateFirewallController.ruleAlgorithmsDao = ruleAlgorithmsDao;
		CreateFirewallController.policyVersionDao = policyVersionDao;
		CreateFirewallController.prefixListDao = prefixListDao;
		CreateFirewallController.serviceListDao = serviceListDao;
		CreateFirewallController.termListDao = termListDao;
		CreateFirewallController.serviceGroupDao = serviceGroupDao;
		CreateFirewallController.securityZoneDao = securityZoneDao;	
		CreateFirewallController.sessionFactory = sessionFactory;	
		CreateFirewallController.fwDictionaryListDao = fwDictionaryListDao;
		CreateFirewallController.policyNotificationDao = policyNotificationDao;
	}

	public CreateFirewallController(){}
	protected PolicyAdapter policyAdapter = null;
	private List<String> termCollectorList;
	private List<FirewallDictionaryList> parentDictionaryList;
	private String ruleID = "";
	private String jsonBody;
	private ArrayList<Object> attributeList;
	private ArrayList<Object> fwAttributeList;

	public String newPolicyID() {
		return Joiner.on(':').skipNulls().join((PolicyController.getDomain().startsWith("urn") ? null: "urn"),
				PolicyController.getDomain().replaceAll("[/\\\\.]", ":"), "xacml", "policy", "id", UUID.randomUUID());
	}

	@RequestMapping(value={"/get_FWParentListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getFWParentListEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			String query= "select distinct parent from fwparent";
			model.put("fwParentListDictionaryDatas", mapper.writeValueAsString(queryToDatabase(query))); 

			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> queryToDatabase(String query) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<String> data = null;
		try {
			SQLQuery sqlquery=session.createSQLQuery(query);
			//Query hbquery = session.createQuery(query);
			data = sqlquery.list();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Qurying Parent Child Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		return data;

	}
	
	@SuppressWarnings("unchecked")
	private void updateToDatabase(String updateQuery) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<String> data = null;
		try {
			SQLQuery sqlquery=session.createSQLQuery(updateQuery);
			 sqlquery.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating FWChildParent Table"+e);	
		}finally{
			try{
				session.close();
			}catch(Exception e1){
				logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
			}
		}
		
	}


	@RequestMapping(value={"/policyController/save_FirewallPolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveFirewallPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		termCollectorList = new ArrayList<String>();
		parentDictionaryList= new ArrayList<FirewallDictionaryList>();
		try {
			String userId = UserUtils.getUserIdFromCookie(request);
			RESTfulPAPEngine engine = (RESTfulPAPEngine) PolicyController.getPapEngine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyAdapter policyData = (PolicyAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyAdapter.class);
			policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			if(root.get("policyData").get("model").get("type").toString().replace("\"", "").equals("file")){
				policyData.isEditPolicy = true;
			}
			this.policyAdapter = policyData;
			if(root.get("policyData").get("model").get("path").size() != 0){
				String dirName = "";
				for(int i = 0; i < root.get("policyData").get("model").get("path").size(); i++){
					dirName = dirName.replace("\"", "") + root.get("policyData").get("model").get("path").get(i).toString().replace("\"", "") + File.separator;
				}
				policyData.setDomainDir(dirName.substring(0, dirName.lastIndexOf(File.separator)));
			}else{
				policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			}

			if (policyData.getTtlDate()==null){
				policyData.setTtlDate("NA");
			}else{
				String dateTTL = policyData.getTtlDate();
				String newDate = convertDate(dateTTL);
				policyData.setTtlDate(newDate);
			}
			
			int version = 0;
			int highestVersion = 0;
			int descriptionVersion = 0;
			//get the highest version of policy from policy version table.
			//getting the sub scope domain where the policy is created or updated
			String dbCheckPolicyName = policyData.getDomainDir() + File.separator + "Config_FW_" + policyData.getPolicyName();
			List<PolicyVersion> policyVersionList = policyVersionDao.getPolicyVersionEntityByName(dbCheckPolicyName);
			if (policyVersionList.size() > 0) {		
				for(int i = 0;  i < policyVersionList.size(); i++) {
					PolicyVersion entityItem = policyVersionList.get(i);
					if(entityItem.getPolicyName().equals(dbCheckPolicyName)){
						highestVersion = entityItem.getHigherVersion();
					}
				}
			}		
			if(highestVersion != 0){
				version = highestVersion;
				descriptionVersion = highestVersion +1;		
			}else{
				version = 1;
				descriptionVersion = 1;
			}

			//set policy adapter values for Building JSON object containing policy data
			String createdBy = "";
			String modifiedBy = userId;
			if(descriptionVersion == 1){
				createdBy = userId;
			}else{
				String policyName = PolicyController.getGitPath().toAbsolutePath().toString() + File.separator + policyData.getDomainDir() + File.separator + policyData.getOldPolicyFileName() + ".xml";
				File policyPath = new File(policyName);
				try {
					createdBy =	XACMLPolicyScanner.getCreatedBy(policyPath.toPath());
				} catch (IOException e) {
					createdBy = "guest";
				}
			}
			
			if(policyData.getFwPolicyType().equalsIgnoreCase("Parent Policy")){
				String comboNames="";
				int i=0;
				for(Object fwattribute : policyData.getFwattributes()){
					if(fwattribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) fwattribute).get("option").toString();
						if(i>0){
							comboNames = comboNames+","+ key;
						}
						else{
							comboNames = key;
						}
						i++;
					}
				}
				policyData.setPolicyDescription(policyData.getPolicyDescription()+ "@CreatedBy:" +createdBy + "@CreatedBy:" + "@ModifiedBy:" +modifiedBy + "@ModifiedBy:"+"@comboList:"+comboNames);
			}
			else{
				policyData.setPolicyDescription(policyData.getPolicyDescription()+ "@CreatedBy:" +createdBy + "@CreatedBy:" + "@ModifiedBy:" +modifiedBy + "@ModifiedBy:");
			}

			//policyData.setPolicyDescription(policyData.getPolicyDescription()+ "@CreatedBy:" +createdBy + "@CreatedBy:" + "@ModifiedBy:" +modifiedBy + "@ModifiedBy:");
			Map<String, String> successMap = new HashMap<String, String>();

			//set the Rule Combining Algorithm Id to be sent to PAP-REST via JSON
			List<RuleAlgorithms> ruleAlgorithmsList = ruleAlgorithmsDao.getRuleAlgorithms();
			for (int i = 0; i < ruleAlgorithmsList.size(); i++) {
				RuleAlgorithms a = ruleAlgorithmsList.get(i);
				if (a.getXacmlId().equals(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue())) {
					policyData.setRuleCombiningAlgId(a.getXacmlId());
					break;
				}
			}

			if(policyData.getAttributes().size() > 0){
				for(Object attribute : policyData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						termCollectorList.add(key);
					}
				}
			}
			if(policyData.getFwattributes()!=null){
				if(policyData.getFwattributes().size() > 0){
					for(Object fwattribute : policyData.getFwattributes()){
						if(fwattribute instanceof LinkedHashMap<?, ?>){
							String key = ((LinkedHashMap<?, ?>) fwattribute).get("option").toString();
							FirewallDictionaryList fwDictValue = fwDictionaryListDao.getFWDictionaryDataById(key);
							parentDictionaryList.add(fwDictValue);
							if(fwDictValue.getAddressList().contains(",")) {
								for(String individualAddressObject:fwDictValue.getAddressList().split(",") ){
									expandablePrefixIPList.add(individualAddressObject);
								}
							}
							else{
								expandablePrefixIPList.add(fwDictValue.getAddressList());
							}

							if(fwDictValue.getServiceList().contains(",")) {
								for(String individualAddressObject:fwDictValue.getServiceList().split(",") ){
									expandableServicesList.add(individualAddressObject);
								}
							}
							else{
								expandableServicesList.add(fwDictValue.getServiceList());
							}
						}
					}
				}
			}

			jsonBody = constructJson();	
			if (jsonBody != null || jsonBody.equalsIgnoreCase("")) {
				policyAdapter.setJsonBody(jsonBody);

			} else {
				policyAdapter.setJsonBody("{}");
			}
			policyData.setJsonBody(jsonBody);


			if (policyData.isEditPolicy()){
				//increment the version and set in policyAdapter
				policyData.setVersion(String.valueOf(version));
				policyData.setHighestVersion(version);
				policyData.setPolicyID(this.newPolicyID());
				policyData.setRuleID(ruleID);
				successMap = engine.updatePolicyRequest(policyData);
			} else {
				//send it for policy creation
				policyData.setVersion(String.valueOf(version));
				policyData.setHighestVersion(version);
				successMap = engine.createPolicyRequest(policyData);

			}

			if (successMap.containsKey("success")) {
				// Add it into our tree
				Path finalPolicyPath = null;
				finalPolicyPath = Paths.get(successMap.get("success"));
				PolicyElasticSearchController controller = new PolicyElasticSearchController();
				controller.updateElk(finalPolicyPath.toString());
				File file = finalPolicyPath.toFile();
				if(file != null){
					String policyName = file.toString();
					String removePath = policyName.substring(policyName.indexOf("repository")+11);
					String removeXml = removePath.replace(".xml", "");
					String removeExtension = removeXml.substring(0, removeXml.indexOf("."));
					List<PolicyVersion> versionList = policyVersionDao.getPolicyVersionEntityByName(removeExtension);
					if (versionList.size() > 0) {		
						for(int i = 0;  i < versionList.size(); i++) {
							PolicyVersion entityItem = versionList.get(i);
							if(entityItem.getPolicyName().equals(removeExtension)){
								version = entityItem.getHigherVersion() +1;
								entityItem.setActiveVersion(version);
								entityItem.setHigherVersion(version);
								entityItem.setModifiedBy(userId);
								policyVersionDao.update(entityItem);
								if(policyData.isEditPolicy){
									PolicyNotificationMail email = new PolicyNotificationMail();
									String mode = "EditPolicy";
									String policyNameForEmail = policyData.getDomainDir() + File.separator + policyData.getOldPolicyFileName() + ".xml";
									email.sendMail(entityItem, policyNameForEmail, mode, policyNotificationDao);
								}
							}
						}
					}else{
						PolicyVersion entityItem = new PolicyVersion();
						entityItem.setActiveVersion(version);
						entityItem.setHigherVersion(version);
						entityItem.setPolicyName(removeExtension);
						entityItem.setCreatedBy(userId);
						entityItem.setModifiedBy(userId);
						policyVersionDao.Save(entityItem);
					}
					removeExtension=removeExtension.replace(File.separator, ".");
					//PC Feature
					if(policyAdapter.getFwPolicyType().equalsIgnoreCase("Parent Policy")){
						//Reads the SecurityZone from the Parent UI and then gets the value from the DB 
						//Stores the Parent Policy Name and securityZone value to the fwparent table. 
						String parentSecurityZoneValue= getParentSecurityZone();
						String parentQuery= "INSERT INTO FWPARENT(PARENT,SECURITYZONE) VALUES ('";
						parentQuery=parentQuery+removeExtension+"','"+ parentSecurityZoneValue +"')";
						updateToDatabase(parentQuery);
					}
					else{
						String updateQuery = "";
						if(policyAdapter.isEditPolicy()){ 
							updateQuery= "UPDATE FWCHILDTOPARENT SET PARENT='"+policyAdapter.getParentForChild()+"' WHERE CHILD='"+removeExtension+"'";
						}
						else{
							updateQuery= "INSERT INTO FWCHILDTOPARENT(CHILD,PARENT) VALUES ('";
							updateQuery =updateQuery+removeExtension+"','"+ policyAdapter.getParentForChild() +"')";
						}
						updateToDatabase(updateQuery);		
					}
				}
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(successMap);
			JSONObject j = new JSONObject("{policyData: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	private String convertDate(String dateTTL) {
		String formateDate = null;
		String[] date  = dateTTL.split("T");
		String[] parts = date[0].split("-");
		
		formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		return formateDate;
	}

	private String constructJson() {
		int ruleCount=1;
		//Maps to assosciate the values read from the TermList dictionary
		Map<Integer, String> srcIP_map =null;
		Map<Integer, String> destIP_map=null;
		Map<Integer, String> srcPort_map =null;
		Map<Integer, String> destPort_map =null;
		Map<Integer, String> action_map=null;
		Map<Integer, String> fromZone_map=null;
		Map<Integer, String> toZone_map=null;

		String ruleDesc=null;
		String ruleFromZone=null;
		String ruleToZone=null;
		String ruleSrcPrefixList=null;
		String ruleDestPrefixList=null;
		String ruleSrcPort=null;
		String ruleDestPort=null;
		String ruleAction=null;

		String json = null;


		List<String> expandableList = new ArrayList<String>();
		TermList jpaTermList;
		SecurityZone jpaSecurityZone;
		List<Term> termList = new ArrayList<Term>();

		ServiceListJson targetSl=null;
		try{

			for (int tl = 0 ; tl< termCollectorList.size(); tl++) {
				expandableList.add(termCollectorList.get(tl));
				Term targetTerm = new Term();
				//targetSl= new ServiceListJson();
				targetTerm.setRuleName(termCollectorList.get(tl));
				List<TermList> termListData = termListDao.getTermListData();
				for (int j =0; j < termListData.size(); j++) {
					jpaTermList = termListData.get(j);
					if (jpaTermList.getTermName().equals(termCollectorList.get(tl))){
						ruleDesc=jpaTermList.getTermDescription();
						if ((ruleDesc!=null)&& (!ruleDesc.isEmpty())){
							targetTerm.setDescription(ruleDesc);
						}	
						ruleFromZone=jpaTermList.getFromZone();	

						if ((ruleFromZone != null) && (!ruleFromZone.isEmpty())){
							fromZone_map = new HashMap<Integer, String>();
							fromZone_map.put(tl, ruleFromZone);
						} 	
						ruleToZone=jpaTermList.getToZone();

						if ((ruleToZone != null) && (!ruleToZone.isEmpty())){
							toZone_map = new HashMap<Integer, String>();
							toZone_map.put(tl, ruleToZone);
						} 
						ruleSrcPrefixList=jpaTermList.getSrcIPList();

						if ((ruleSrcPrefixList != null) && (!ruleSrcPrefixList.isEmpty())){
							srcIP_map = new HashMap<Integer, String>();
							srcIP_map.put(tl, ruleSrcPrefixList);
						} 

						ruleDestPrefixList= jpaTermList.getDestIPList();
						if ((ruleDestPrefixList != null) && (!ruleDestPrefixList.isEmpty())){
							destIP_map = new HashMap<Integer, String>();
							destIP_map.put(tl, ruleDestPrefixList);
						} 

						ruleSrcPort=jpaTermList.getSrcPortList();

						if (ruleSrcPort != null && (!ruleSrcPort.isEmpty())){
							srcPort_map = new HashMap<Integer, String>();
							srcPort_map.put(tl, ruleSrcPort);
						} 

						ruleDestPort= jpaTermList.getDestPortList();

						if (ruleDestPort!= null && (!jpaTermList.getDestPortList().isEmpty())){
							destPort_map = new HashMap<Integer, String>();
							destPort_map.put(tl, ruleDestPort);
						} 

						ruleAction=jpaTermList.getAction();

						if (( ruleAction!= null) && (!ruleAction.isEmpty())){
							action_map = new HashMap<Integer, String>();
							action_map.put(tl, ruleAction);
						} 
					}
				}
				targetTerm.setEnabled(true);
				targetTerm.setLog(true);
				targetTerm.setNegateSource(false);
				targetTerm.setNegateDestination(false);

				if(action_map!=null){
					targetTerm.setAction(action_map.get(tl));
				}

				//FromZone arrays
				if(fromZone_map!=null){
					List<String> fromZone= new ArrayList<String>();
					for(String fromZoneStr:fromZone_map.get(tl).split(",") ){
						fromZone.add(fromZoneStr);
					}
					targetTerm.setFromZones(fromZone);
				}

				//ToZone arrays
				if(toZone_map!=null){
					List<String> toZone= new ArrayList<String>();
					for(String toZoneStr:toZone_map.get(tl).split(",") ){
						toZone.add(toZoneStr);
					}
					targetTerm.setToZones(toZone);
				}

				//Destination Services.
				if(destPort_map!=null){
					Set<ServicesJson> destServicesJsonList= new HashSet<ServicesJson>();
					for(String destServices:destPort_map.get(tl).split(",") ){
						ServicesJson destServicesJson= new ServicesJson();
						destServicesJson.setType("REFERENCE");
						if(destServices.equals("ANY")){
							destServicesJson.setName("any");
							destServicesJsonList.add(destServicesJson);
							break;
						}else{
							if(destServices.startsWith("Group_")){
								destServicesJson.setName(destServices.substring(6,destServices.length()));
							} else{
								destServicesJson.setName(destServices);
							}
							destServicesJsonList.add(destServicesJson);
						}
					}			
					targetTerm.setDestServices(destServicesJsonList);
				}
				//ExpandableServicesList
				if((srcPort_map!=null) && (destPort_map!=null)){
					String servicesCollateString = (srcPort_map.get(tl) + "," + destPort_map.get(tl));
					expandableServicesList.add(servicesCollateString);
				}else if (srcPort_map!=null){
					expandableServicesList.add(srcPort_map.get(tl));
				}else if (destPort_map!=null){
					expandableServicesList.add(destPort_map.get(tl));
				}

				if(srcIP_map!=null){
					//Source List
					List<AddressJson> sourceListArrayJson= new ArrayList<AddressJson>();			
					for(String srcList:srcIP_map.get(tl).split(",") ){
						AddressJson srcListJson= new AddressJson();
						if(srcList.equals("ANY")){
							srcListJson.setType("any");
							sourceListArrayJson.add(srcListJson);
							break;
						}else{
							srcListJson.setType("REFERENCE");
							if(srcList.startsWith("Group_")){
								srcListJson.setValue(srcList.substring(6,srcList.length()));
							}else{
								srcListJson.setValue(srcList);
							}
							sourceListArrayJson.add(srcListJson);
						}
					}
					targetTerm.setSourceList(sourceListArrayJson);
				}
				if(destIP_map!=null){
					//Destination List
					List<AddressJson> destListArrayJson= new ArrayList<AddressJson>();				
					for(String destList:destIP_map.get(tl).split(",")){
						AddressJson destListJson= new AddressJson();
						if(destList.equals("ANY")){
							destListJson.setType("any");
							destListArrayJson.add(destListJson);
							break;
						}else{
							destListJson.setType("REFERENCE");
							if(destList.startsWith("Group_")){
								destListJson.setValue(destList.substring(6,destList.length()));
							}else{
								destListJson.setValue(destList);
							}
							destListArrayJson.add(destListJson);
						}
					}
					targetTerm.setDestinationList(destListArrayJson);	
				}
				//ExpandablePrefixIPList
				if ((srcIP_map!=null) && (destIP_map!=null)) 
				{
					String collateString = (srcIP_map.get(tl) + "," + destIP_map
							.get(tl));
					expandablePrefixIPList.add(collateString);
				}
				else if(srcIP_map!=null){
					expandablePrefixIPList.add(srcIP_map.get(tl));
				}
				else if(destIP_map!=null){
					expandablePrefixIPList.add(destIP_map.get(tl));
				}
				termList.add(targetTerm);
				targetTerm.setPosition("" + (ruleCount++));
			}
			TermCollector tc = new TermCollector();
			List<SecurityZone> securityZoneData = securityZoneDao.getSecurityZoneData();
			for (int j =0 ; j< securityZoneData.size() ; j++){
				jpaSecurityZone = securityZoneData.get(j);
				if (jpaSecurityZone.getZoneName().equals(policyAdapter.getSecurityZone())){
					tc.setSecurityZoneId(jpaSecurityZone.getZoneValue());
					setParentSecurityZone(jpaSecurityZone.getZoneValue());//For storing the securityZone IDs to the DB
					break;
				}
			}

			tc.setServiceTypeId("/v0/firewall/pan");
			tc.setConfigName(policyAdapter.getConfigName());
			
			if(policyAdapter.getFwPolicyType().equalsIgnoreCase("Child Policy")){
				String securityZoneQuery="SELECT SECURITYZONE FROM FWPARENT WHERE PARENT='";
				securityZoneQuery=securityZoneQuery+policyAdapter.getParentForChild()+"'";
				List<String> securityZoneValue= queryToDatabase(securityZoneQuery);
				tc.setPrimaryParentZoneId(securityZoneValue.get(0));
			}
			//Astra is rejecting the packet when it sees a new JSON field, so removing it for now. 
			//tc.setTemplateVersion(XACMLProperties.getProperty(XACMLRestProperties.TemplateVersion_FW));

			DeployNowJson deployNow= new DeployNowJson();
			deployNow.setDeployNow(false);

			tc.setDeploymentOption(deployNow);

			Set<ServiceListJson> servListArray = new HashSet<ServiceListJson>();
			Set<ServiceGroupJson> servGroupArray= new HashSet<ServiceGroupJson>();
			Set<AddressGroupJson> addrGroupArray= new HashSet<AddressGroupJson>();

			ServiceGroupJson targetSg= null;
			AddressGroupJson addressSg=null;
			ServiceListJson targetAny= null;
			ServiceListJson targetAnyTcp=null;
			ServiceListJson targetAnyUdp=null;

			for(String serviceList:expandableServicesList){
				for(String t: serviceList.split(",")){
					if((!t.startsWith("Group_"))){
						if(!t.equals("ANY")){
							ServiceList sl = new ServiceList();
							targetSl= new ServiceListJson();
							sl= mappingServiceList(t);
							targetSl.setName(sl.getServiceName());
							targetSl.setDescription(sl.getServiceDescription());
							targetSl.setTransportProtocol(sl.getServiceTransProtocol());
							targetSl.setType(sl.getServiceType());
							targetSl.setPorts(sl.getServicePorts());
							servListArray.add(targetSl);
						}else{
							//Any for destinationServices.
							//Add names any, any-tcp, any-udp to the serviceGroup object. 
							targetAny= new ServiceListJson();
							targetAny.setName("any");
							targetAny.setType("SERVICE");
							targetAny.setTransportProtocol("any");
							targetAny.setPorts("any");

							servListArray.add(targetAny);

							targetAnyTcp= new ServiceListJson();
							targetAnyTcp.setName("any-tcp");
							targetAnyTcp.setType("SERVICE");
							targetAnyTcp.setTransportProtocol("tcp");
							targetAnyTcp.setPorts("any");

							servListArray.add(targetAnyTcp);

							targetAnyUdp= new ServiceListJson();
							targetAnyUdp.setName("any-udp");
							targetAnyUdp.setType("SERVICE");
							targetAnyUdp.setTransportProtocol("udp");
							targetAnyUdp.setPorts("any");

							servListArray.add(targetAnyUdp);
						}
					}else{//This is a group
						GroupServiceList sg= new GroupServiceList();
						targetSg= new ServiceGroupJson();
						sg= mappingServiceGroup(t);

						String name=sg.getGroupName();
						//Removing the "Group_" prepending string before packing the JSON 
						targetSg.setName(name.substring(6,name.length()));
						List<ServiceMembers> servMembersList= new ArrayList<ServiceMembers>();

						for(String groupString: sg.getServiceList().split(",")){
							ServiceMembers serviceMembers= new ServiceMembers();
							serviceMembers.setType("REFERENCE");
							serviceMembers.setName(groupString);
							servMembersList.add(serviceMembers);
							//Expand the group Name
							ServiceList expandGroupSl = new ServiceList();
							targetSl= new ServiceListJson();
							expandGroupSl= mappingServiceList(groupString);

							targetSl.setName(expandGroupSl.getServiceName());
							targetSl.setDescription(expandGroupSl.getServiceDescription());
							targetSl.setTransportProtocol(expandGroupSl.getServiceTransProtocol());
							targetSl.setType(expandGroupSl.getServiceType());
							targetSl.setPorts(expandGroupSl.getServicePorts());
							servListArray.add(targetSl);
						}

						targetSg.setMembers(servMembersList);
						servGroupArray.add(targetSg);

					}
				}
			}

			Set<PrefixIPList> prefixIPList = new HashSet<PrefixIPList>();
			for(String prefixList:expandablePrefixIPList){
				for(String prefixIP: prefixList.split(",")){
					if((!prefixIP.startsWith("Group_"))){
						if(!prefixIP.equals("ANY")){
							List<AddressMembers> addMembersList= new ArrayList<AddressMembers>();
							List<String> valueDesc= new ArrayList<String>();
							PrefixIPList targetAddressList = new PrefixIPList();
							AddressMembers addressMembers= new AddressMembers();
							targetAddressList.setName(prefixIP);

							valueDesc = mapping(prefixIP);
							targetAddressList.setDescription(valueDesc.get(1));

							addressMembers.setType("SUBNET");
							addressMembers.setValue(valueDesc.get(0));

							addMembersList.add(addressMembers);

							targetAddressList.setMembers(addMembersList);
							prefixIPList.add(targetAddressList);
						}
					}
					else{//This is a group
						AddressGroup ag= new AddressGroup();
						addressSg= new AddressGroupJson();
						ag= mappingAddressGroup(prefixIP);		

						String name=ag.getGroupName();
						//Removing the "Group_" prepending string before packing the JSON 
						addressSg.setName(name.substring(6,name.length()));

						List<AddressMembers> addrMembersList= new ArrayList<AddressMembers>();
						for(String groupString: ag.getPrefixList().split(",")){
							List<String> valueDesc= new ArrayList<String>();
							AddressMembers addressMembers= new AddressMembers();
							valueDesc= mapping (groupString);
							if(valueDesc.size() > 0){
								addressMembers.setValue(valueDesc.get(0));
							}
							addressMembers.setType("SUBNET");
							addrMembersList.add(addressMembers);
							//Expand the group Name
						}
						addressSg.setMembers(addrMembersList);
						addrGroupArray.add(addressSg);
					}


				}
			}

			Set<Object> serviceGroup= new HashSet<Object>();

			for(Object obj1:servGroupArray){
				serviceGroup.add(obj1);
			}

			for(Object obj:servListArray){
				serviceGroup.add(obj);
			}

			Set<Object> addressGroup= new HashSet<Object>();

			for(Object addObj:prefixIPList){
				addressGroup.add(addObj);
			}

			for(Object addObj1:addrGroupArray){
				addressGroup.add(addObj1);
			}

			tc.setServiceGroups(serviceGroup);
			tc.setAddressGroups(addressGroup);
			tc.setFirewallRuleList(termList);


			ObjectWriter om = new ObjectMapper().writer();
			try {
				json = om.writeValueAsString(tc);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	

		}catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	private List<String> mapping(String expandableList) {
		String value = new String();
		String desc =  new String();
		List <String> valueDesc= new ArrayList<String>();
		List<PREFIXLIST> prefixListData = prefixListDao.getPREFIXLISTData();
		for (int i = 0; i< prefixListData.size(); i++) {
			PREFIXLIST prefixList = prefixListData.get(i);
			if (prefixList.getPrefixListName().equals(expandableList)) {
				value = prefixList.getPrefixListValue();
				valueDesc.add(value);
				desc= prefixList.getDescription();
				valueDesc.add(desc);
				break;
			}
		}
		return valueDesc;
	}

	private ServiceList mappingServiceList(String expandableList) {	
		ServiceList serviceList=null;
		List<ServiceList> serviceListData = serviceListDao.getServiceListData();
		for (int i = 0; i< serviceListData.size(); i++) {
			serviceList = serviceListData.get(i);
			if (serviceList.getServiceName().equals(expandableList)) {
				break;
			}
		}
		return serviceList;
	}

	private GroupServiceList mappingServiceGroup(String expandableList) {

		GroupServiceList serviceGroup=null;
		List<GroupServiceList> serviceGroupData = serviceGroupDao.getGroupServiceListData();
		for (int i = 0; i< serviceGroupData.size(); i++) {
			serviceGroup = serviceGroupData.get(i);
			if (serviceGroup.getGroupName().equals(expandableList)) {
				break;
			}
		}
		return serviceGroup;
	}

	private AddressGroup mappingAddressGroup(String expandableList) {

		AddressGroup addressGroup=null;
		List<AddressGroup> addressGroupData = addressGroupDao.getAddressGroupData();
		for (int i = 0; i< addressGroupData.size(); i++) {
			addressGroup = addressGroupData.get(i);
			if (addressGroup.getGroupName().equals(expandableList)) {
				break;
			}
		}
		return addressGroup;
	}

	public void PrePopulateFWPolicyData(PolicyAdapter policyAdapter) {
		attributeList = new ArrayList<Object>();
		fwAttributeList = new ArrayList<Object>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			// policy name value is the policy name without any prefix and Extensions.
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("FW_") +3, policyAdapter.getPolicyName().lastIndexOf("."));
			if (logger.isDebugEnabled()) {
				logger.debug("Prepopulating form data for Config Policy selected:"+ policyAdapter.getPolicyName());
			}
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);

			ObjectMapper mapper = new ObjectMapper();

			TermCollector tc1=null;
			BufferedReader br=null;
			try {
				//Json conversion. 
				String data=null;
				SecurityZone jpaSecurityZone;
				File file = new File(PolicyController.getConfigHome()+ File.separator+ policyAdapter.getDirPath().replace(File.separator, ".")+"."+ policyAdapter.getOldPolicyFileName() +".json");
				//  Get data from this file using a file reader. 
				FileReader fr = new FileReader(file);
				// To store the contents read via File Reader
				br = new BufferedReader(fr);                                                 
				// Read br and store a line in 'data', print data
				data = br.readLine();
				tc1 = (TermCollector)mapper.readValue(data, TermCollector.class);
				List<SecurityZone> securityZoneData = securityZoneDao.getSecurityZoneData();
				for (int i = 0; i < securityZoneData.size() ; i++) {
					jpaSecurityZone = securityZoneData.get(i);
					if (jpaSecurityZone.getZoneValue().equals(tc1.getSecurityZoneId())){
						policyAdapter.setSecurityZone(jpaSecurityZone.getZoneName());
						break;
					}
				}
				if(tc1.getPrimaryParentZoneId()!=null)//Child policy
				{
					policyAdapter.setFwPolicyType("Child Policy");
					
					String pathName=policyAdapter.getParentPath().toString();
					String scope= pathName.substring(pathName.lastIndexOf(File.separator)+1);
					String fullPathName=scope+".Config_FW_"+policyNameValue;
					
					String query= "select parent from FWChildToParent where child='";
					query=query+fullPathName+"'";
					
					List<String> parentName=queryToDatabase(query);
					policyAdapter.setParentForChild(parentName.get(0));
					
				}
				else{//Parent Policy
					policyAdapter.setFwPolicyType("Parent Policy");
					//Retrieving the Dictionary combo list. 
					String desc = policy.getDescription();
					String descripComboList = desc.substring(desc.indexOf("@comboList:")+11,desc.length()) ;
					Map<String, String> parentMap = new HashMap<String, String>();
					for(String value : descripComboList.split(",")){
						parentMap.put("option", value);
						fwAttributeList.add(parentMap);
					}
					policyAdapter.setFwattributes(fwAttributeList);
				}
			}
			catch(Exception e) {
				logger.error("Exception Caused while Retriving the JSON body data" +e);
			}
			finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			for (Term t : tc1.getFirewallRuleList()) {
				Map<String, String> termMap = new HashMap<String, String>();
				termMap.put("option", t.getRuleName());
				attributeList.add(termMap);	
			}
			policyAdapter.setAttributes(attributeList);
			// Get the target data under policy.
			TargetType target = policy.getTarget();
			if (target != null) {
				// Under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					int index = 0;
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AnyOFType we have AllOFType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attribute value and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);
										if (index == 1) {
											policyAdapter.setConfigName(value);
										}
										if (index ==  2){
											policyAdapter.setRiskType(value);
										}
	
										if (index ==  3){
											policyAdapter.setRiskLevel(value);
										}
										
										if (index ==  4){
											policyAdapter.setGuard(value);
										}
										if (index == 5 && !value.contains("NA")){
											String newDate = convertDate(value, true);
											policyAdapter.setTtlDate(newDate);
										}
										index++;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private String convertDate(String dateTTL, boolean portalType) {
		String formateDate = null;
		String[] date;
		String[] parts;
		
		if (portalType){
			parts = dateTTL.split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0] + "T05:00:00.000Z";
		} else {
			date  = dateTTL.split("T");
			parts = date[0].split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		}
		return formateDate;
	}
	
	@RequestMapping(value={"/policyController/ViewFWPolicyRule.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView setFWViewRule(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			termCollectorList = new ArrayList<String>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyAdapter policyData = (PolicyAdapter)mapper.readValue(root.get("policyData").toString(), PolicyAdapter.class);
			if(policyData.getAttributes().size() > 0){
				for(Object attribute : policyData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						termCollectorList.add(key);
					}
				}
			}
			TermList jpaTermList;
			String ruleSrcList=null;
			String ruleDestList=null;
			String ruleSrcPort=null;
			String ruleDestPort=null;
			String ruleAction=null;
			List <String> valueDesc= new ArrayList<String>();
			StringBuffer displayString = new StringBuffer();
			for (String id : termCollectorList) {
				jpaTermList = termListDao.getTermListValueByName(id);
				if (jpaTermList != null){				
					ruleSrcList= ((TermList) jpaTermList).getSrcIPList();	
					if ((ruleSrcList!= null) && (!ruleSrcList.isEmpty()) && !ruleSrcList.equals("null")){
						displayString.append("Source IP List: " + ((TermList) jpaTermList).getSrcIPList());
						displayString.append(" ; \t\n");
						for(String srcList:ruleSrcList.split(",")){	
							if(srcList.startsWith("Group_")){
								AddressGroup ag= new AddressGroup();
								ag= mappingAddressGroup(srcList);
								displayString.append("\n\t"+"Group has  :"+ag.getPrefixList()+"\n");
								for(String groupItems:ag.getPrefixList().split(",")){
									valueDesc=mapping(groupItems);
									displayString.append("\n\t"+"Name: "+groupItems);
									if(!valueDesc.isEmpty()){
										displayString.append("\n\t"+"Description: "+valueDesc.get(1));
										displayString.append("\n\t"+"Value: "+valueDesc.get(0));
									}
									displayString.append("\n");
								}
							}else{
								if(!srcList.equals("ANY")){
									valueDesc=mapping(srcList);
									displayString.append("\n\t"+"Name: "+srcList);
									displayString.append("\n\t"+"Description: "+valueDesc.get(1));
									displayString.append("\n\t"+"Value: "+valueDesc.get(0));
									displayString.append("\n");
								}
							}
						}
						displayString.append("\n");
					} 
					ruleDestList= ((TermList) jpaTermList).getDestIPList();
					if ( ruleDestList!= null && (!ruleDestList.isEmpty())&& !ruleDestList.equals("null")){
						displayString.append("Destination IP List: " + ((TermList) jpaTermList).getDestIPList());
						displayString.append(" ; \t\n");
						for(String destList:ruleDestList.split(",")){	
							if(destList.startsWith("Group_")){
								AddressGroup ag= new AddressGroup();
								ag= mappingAddressGroup(destList);
								displayString.append("\n\t"+"Group has  :"+ag.getPrefixList()+"\n");
								for(String groupItems:ag.getPrefixList().split(",")){
									valueDesc=mapping(groupItems);
									displayString.append("\n\t"+"Name: "+groupItems);
									displayString.append("\n\t"+"Description: "+valueDesc.get(1));
									displayString.append("\n\t"+"Value: "+valueDesc.get(0));
									displayString.append("\n\t");
								}
							}else{
								if(!destList.equals("ANY")){
									valueDesc=mapping(destList);
									displayString.append("\n\t"+"Name: "+destList);
									displayString.append("\n\t"+"Description: "+valueDesc.get(1));
									displayString.append("\n\t"+"Value: "+valueDesc.get(0));
									displayString.append("\n\t");
								}
							}
						}
						displayString.append("\n");
					} 

					ruleSrcPort=((TermList) jpaTermList).getSrcPortList();
					if ( ruleSrcPort!= null && (!ruleSrcPort.isEmpty())&& !ruleSrcPort.equals("null")) {
						displayString.append("\n"+"Source Port List:"
								+ ruleSrcPort);
						displayString.append(" ; \t\n");
					} 

					ruleDestPort= ((TermList) jpaTermList).getDestPortList();
					if (ruleDestPort != null && (!ruleDestPort.isEmpty())&& !ruleDestPort.equals("null")) {
						displayString.append("\n"+"Destination Port List:"
								+ ruleDestPort);
						displayString.append(" ; \t\n");
						for(String destServices:ruleDestPort.split(",")){	
							if(destServices.startsWith("Group_")){
								GroupServiceList sg= new GroupServiceList();
								sg= mappingServiceGroup(destServices);
								displayString.append("\n\t"+"Service Group has  :"+sg.getServiceList()+"\n");
								for(String groupItems:sg.getServiceList().split(",")){
									ServiceList sl= new ServiceList();
									sl= mappingServiceList(groupItems);
									displayString.append("\n\t"+"Name:  "+
											sl.getServiceName());
									displayString.append("\n\t"+"Description:  "+
											sl.getServiceDescription());	
									displayString.append("\n\t"+"Transport-Protocol:  "+
											sl.getServiceTransProtocol());
									displayString.append("\n\t"+"Ports:  "+
											sl.getServicePorts());
									displayString.append("\n");
								}
							}
							else{
								if(!destServices.equals("ANY")){
									ServiceList sl= new ServiceList();
									sl= mappingServiceList(destServices);
									displayString.append("\n\t"+"Name:  "+
											sl.getServiceName());
									displayString.append("\n\t"+"Description:  "+
											sl.getServiceDescription());	
									displayString.append("\n\t"+"Transport-Protocol:  "+
											sl.getServiceTransProtocol());
									displayString.append("\n\t"+"Ports:  "+
											sl.getServicePorts());
									displayString.append("\n");
								}
							}
						}
						displayString.append("\n");
					}

					ruleAction=(jpaTermList).getAction();
					if ( ruleAction!= null && (!ruleAction.isEmpty())) {
						displayString.append("\n"+"Action List:"
								+ ruleAction);
						displayString.append(" ; \t\n");
					} 
				}
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(displayString);
			JSONObject j = new JSONObject("{policyData: " + responseString + "}");
			out.write(j.toString());
			return null;
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		return null;	
	}


}
