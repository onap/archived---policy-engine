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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.adapter.ClosedLoopFaultBody;
import org.openecomp.policy.adapter.ClosedLoopFaultTriggerUISignatures;
import org.openecomp.policy.adapter.ClosedLoopSignatures;
import org.openecomp.policy.adapter.PolicyAdapter;
import org.openecomp.policy.admin.PolicyNotificationMail;
import org.openecomp.policy.admin.RESTfulPAPEngine;
import org.openecomp.policy.dao.PolicyVersionDao;
import org.openecomp.policy.dao.RuleAlgorithmsDao;
import org.openecomp.policy.dao.WatchPolicyNotificationDao;
import org.openecomp.policy.elk.client.PolicyElasticSearchController;
import org.openecomp.policy.rest.dao.VarbindDictionaryDao;
import org.openecomp.policy.rest.jpa.EcompName;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.RuleAlgorithms;
import org.openecomp.policy.rest.jpa.VarbindDictionary;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.att.research.xacml.api.XACML3;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
public class CreateClosedLoopFaultController extends RestrictedBaseController{
 
	private static RuleAlgorithmsDao ruleAlgorithmsDao;
	private static PolicyVersionDao policyVersionDao;
	private static VarbindDictionaryDao varbindDictionaryDao;
	private static WatchPolicyNotificationDao policyNotificationDao;
	
	@Autowired
	private CreateClosedLoopFaultController(RuleAlgorithmsDao ruleAlgorithmsDao, PolicyVersionDao policyVersionDao, VarbindDictionaryDao varbindDictionaryDao,
			WatchPolicyNotificationDao policyNotificationDao){
		CreateClosedLoopFaultController.policyVersionDao = policyVersionDao;
		CreateClosedLoopFaultController.ruleAlgorithmsDao = ruleAlgorithmsDao;
		CreateClosedLoopFaultController.varbindDictionaryDao = varbindDictionaryDao;
		CreateClosedLoopFaultController.policyNotificationDao = policyNotificationDao;
	}
	
	public CreateClosedLoopFaultController(){}
	protected PolicyAdapter policyAdapter = null;
	private String ruleID = "";
	public String newPolicyID() {
		return Joiner.on(':').skipNulls().join((PolicyController.getDomain().startsWith("urn") ? null: "urn"),
				PolicyController.getDomain().replaceAll("[/\\\\.]", ":"), "xacml", "policy", "id", UUID.randomUUID());
	}
	
	@RequestMapping(value={"/policyController/save_Faultpolicy.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveFaultPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			String userId = UserUtils.getUserIdFromCookie(request);
			RESTfulPAPEngine engine = (RESTfulPAPEngine) PolicyController.getPapEngine();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyAdapter policyData = (PolicyAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyAdapter.class);	
			TrapDatas trapDatas = mapper.readValue(root.get("trapData").toString(), TrapDatas.class);
			TrapDatas faultDatas = mapper.readValue(root.get("faultData").toString(), TrapDatas.class);
			ClosedLoopGridJSONData policyJsonData = mapper.readValue(root.get("policyData").get("policy").toString(), ClosedLoopGridJSONData.class);
			ClosedLoopFaultBody jsonBody = mapper.readValue(root.get("policyData").get("policy").get("jsonBodyData").toString(), ClosedLoopFaultBody.class);
			policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			if(root.get("policyData").get("model").get("type").toString().replace("\"", "").equals("file")){
				policyData.isEditPolicy = true;
			}
			
			if (policyData.getTtlDate()==null){
				policyData.setTtlDate("NA");
			}else{
				String dateTTL = policyData.getTtlDate();
				String newDate = convertDate(dateTTL);
				policyData.setTtlDate(newDate);
			}
			
			if(root.get("policyData").get("model").get("path").size() != 0){
				String dirName = "";
				for(int i = 0; i < root.get("policyData").get("model").get("path").size(); i++){
					dirName = dirName.replace("\"", "") + root.get("policyData").get("model").get("path").get(i).toString().replace("\"", "") + File.separator;
				}
				policyData.setDomainDir(dirName.substring(0, dirName.lastIndexOf(File.separator)));
			}else{
				policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			}
			ArrayList<Object> trapSignatureDatas = new ArrayList<Object>();
			if(trapDatas.getTrap1() != null){
				trapSignatureDatas.add(trapDatas);
			}
			ArrayList<Object> faultSignatureDatas = new ArrayList<Object>();
			if(faultDatas.getTrap1() != null){
				faultSignatureDatas.add(faultDatas);
			}
			
			String resultBody = "";
			if(!policyJsonData.getConnecttriggerSignatures().isEmpty()){
				resultBody = resultBody + "(";
				for(int i = policyJsonData.getConnecttriggerSignatures().size()-1; i>=0 ; i--){
					String connectBody = connectTriggerSignature(i, policyJsonData.getConnecttriggerSignatures(), trapSignatureDatas.get(0));
					resultBody = resultBody  + connectBody;
				}
				resultBody = resultBody + ")";
			}else{
				if(!trapSignatureDatas.isEmpty()){
					resultBody = callTrap("nill", trapSignatureDatas.get(0));
				}
			}
			ClosedLoopSignatures triggerSignatures = new ClosedLoopSignatures();
			triggerSignatures.setSignatures(resultBody);
			if(policyData.getClearTimeOut() != null){
				triggerSignatures.setTimeWindow(Integer.parseInt(policyData.getClearTimeOut()));	
				triggerSignatures.setTrapMaxAge(Integer.parseInt(policyData.getTrapMaxAge()));
				ClosedLoopFaultTriggerUISignatures uiTriggerSignatures = new ClosedLoopFaultTriggerUISignatures();
				if(!trapSignatureDatas.isEmpty()){
					uiTriggerSignatures.setSignatures(getUITriggerSignature("Trap", trapSignatureDatas.get(0)));
					if(!policyJsonData.getConnecttriggerSignatures().isEmpty()){
						uiTriggerSignatures.setConnectSignatures(getUIConnectTraps(policyJsonData.getConnecttriggerSignatures()));
					}				
				}			
				jsonBody.setTriggerSignaturesUsedForUI(uiTriggerSignatures);
				jsonBody.setTriggerTimeWindowUsedForUI(Integer.parseInt(policyData.getClearTimeOut()));
				jsonBody.setTrapMaxAgeUsedForUI(Integer.parseInt(policyData.getTrapMaxAge()));
			}
			
			jsonBody.setTriggerSignatures(triggerSignatures);
			String faultBody = "";
			if(!policyJsonData.getConnectVerificationSignatures().isEmpty()){
				faultBody = faultBody + "(";
				for(int i = policyJsonData.getConnectVerificationSignatures().size()-1; i>=0 ; i--){
					String connectBody = connectTriggerSignature(i, policyJsonData.getConnectVerificationSignatures(), faultSignatureDatas.get(0));
					faultBody = faultBody  + connectBody;
				}
				faultBody = faultBody + ")";
			}else{
				if(!faultSignatureDatas.isEmpty()){
					faultBody = callTrap("nill", faultSignatureDatas.get(0));
				}
			}
			ClosedLoopSignatures faultSignatures = new ClosedLoopSignatures();
			faultSignatures.setSignatures(faultBody);
			if(policyData.getVerificationclearTimeOut() != null){
				faultSignatures.setTimeWindow(Integer.parseInt(policyData.getVerificationclearTimeOut()));
				ClosedLoopFaultTriggerUISignatures uifaultSignatures = new ClosedLoopFaultTriggerUISignatures();
				if(!faultSignatureDatas.isEmpty()){
					uifaultSignatures.setSignatures(getUITriggerSignature("Fault", faultSignatureDatas.get(0)));
					if(!policyJsonData.getConnectVerificationSignatures().isEmpty()){
						uifaultSignatures.setConnectSignatures(getUIConnectTraps(policyJsonData.getConnectVerificationSignatures()));
					}		
				}
				
				jsonBody.setVerificationSignaturesUsedForUI(uifaultSignatures);
				jsonBody.setVerfificationTimeWindowUsedForUI(Integer.parseInt(policyData.getVerificationclearTimeOut()));
			}		
			jsonBody.setVerificationSignatures(faultSignatures);
			ObjectWriter om = new ObjectMapper().writer();
			String json = om.writeValueAsString(jsonBody);
			policyData.setJsonBody(json);
			
			int version = 0;
			int highestVersion = 0;
			int descriptionVersion = 0;
			//get the highest version of policy from policy version table.
			//getting the sub scope domain where the policy is created or updated
			String dbCheckPolicyName = policyData.getDomainDir() + File.separator + "Config_Fault_" + policyData.getPolicyName();
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
			
			policyData.setPolicyDescription(policyData.getPolicyDescription()+ "@CreatedBy:" +createdBy + "@CreatedBy:" + "@ModifiedBy:" +modifiedBy + "@ModifiedBy:");
			Map<String, String> successMap = new HashMap<String, String>();
			//set the Rule Combining Algorithm Id to be sent to PAP-REST via JSON
			List<RuleAlgorithms> ruleAlgorithsList = ruleAlgorithmsDao.getRuleAlgorithms();
			for (int i = 0; i < ruleAlgorithsList.size(); i++) {
				RuleAlgorithms a = ruleAlgorithsList.get(i);
				if (a.getXacmlId().equals(XACML3.ID_RULE_PERMIT_OVERRIDES.stringValue())) {
					policyData.setRuleCombiningAlgId(a.getXacmlId());
					break;
				}
			}
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
					List<PolicyVersion> versionlist = policyVersionDao.getPolicyVersionEntityByName(removeExtension);
					if (versionlist.size() > 0) {		
						for(int i = 0;  i < versionlist.size(); i++) {
						PolicyVersion entityItem = versionlist.get(i);
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
	
	//connect traps data set to JSON Body as String
	@SuppressWarnings("rawtypes")
	private String getUIConnectTraps(ArrayList<Object> connectTrapSignatures) {
		String resultBody = "";
		String connectMainBody = "";
		for(int j = 0; j < connectTrapSignatures.size(); j++){
			Map<String, String> connectTraps = (Map<String, String>)connectTrapSignatures.get(j);
			String connectBody = "";
			Object object = connectTraps;
			if(object instanceof LinkedHashMap<?, ?>){
				String notBox = "";
				if(((LinkedHashMap) object).keySet().contains("notBox")){
					notBox = ((LinkedHashMap) object).get("notBox").toString();
				}
				String connectTrap1 = ((LinkedHashMap) object).get("connectTrap1").toString();
				String trapCount1 = ((LinkedHashMap) object).get("trapCount1").toString();
				String operatorBox = ((LinkedHashMap) object).get("operatorBox").toString();
				String connectTrap2 = ((LinkedHashMap) object).get("connectTrap2").toString();
				String trapCount2 = ((LinkedHashMap) object).get("trapCount2").toString();
				connectBody = notBox + "@!" + connectTrap1 + "@!" + trapCount1 + "@!" + operatorBox + "@!" + connectTrap2 + "@!" + trapCount2 + "#!?!"; 
			}
			resultBody = resultBody + connectBody;
		}
		connectMainBody = connectMainBody + resultBody;
		return connectMainBody;
	}

		

		// get Trigger signature from JSON body
		private String getUITriggerSignature(String trap, Object object2) {
			String triggerBody = "";
			TrapDatas trapDatas = (TrapDatas) object2;
			ArrayList<Object> attributeList = new ArrayList<>();
			// Read the Trap 
			if(trap.startsWith("Trap")){
				if(trapDatas.getTrap1()!= null){
					attributeList.add(trapDatas.getTrap1());
				}
				if(trapDatas.getTrap2()!= null){
					attributeList.add(trapDatas.getTrap2());
				}
				if(trapDatas.getTrap3()!= null){
					attributeList.add(trapDatas.getTrap3());
				}
				if(trapDatas.getTrap4()!= null){
					attributeList.add(trapDatas.getTrap4());
				}
				if(trapDatas.getTrap5()!= null){
					attributeList.add(trapDatas.getTrap5());
				}
				if(trapDatas.getTrap6()!= null){
					attributeList.add(trapDatas.getTrap6());
				}
			}else{
				if(trap.equals("Fault")){
					if(trapDatas.getTrap1()!= null){
						attributeList.add(trapDatas.getTrap1());
					}
					if(trapDatas.getTrap2()!= null){
						attributeList.add(trapDatas.getTrap2());
					}
					if(trapDatas.getTrap3()!= null){
						attributeList.add(trapDatas.getTrap3());
					}
					if(trapDatas.getTrap4()!= null){
						attributeList.add(trapDatas.getTrap4());
					}
					if(trapDatas.getTrap5()!= null){
						attributeList.add(trapDatas.getTrap5());
					}
					if(trapDatas.getTrap6()!= null){
						attributeList.add(trapDatas.getTrap6());
					}				
				}	
			}

			for(int j = 0; j < attributeList.size(); j++){
				String signatureBody = "";
				ArrayList<Object> connectTraps = (ArrayList<Object>) attributeList.get(j);
				for(int i =0 ; i < connectTraps.size(); i++){
					String connectBody = "";
					Object object = connectTraps.get(i);
					if(object instanceof LinkedHashMap<?, ?>){
						String notBox = "";
						if(((LinkedHashMap) object).keySet().contains("notBox")){
							notBox = ((LinkedHashMap) object).get("notBox").toString();
						}
						String trigger1 = ((LinkedHashMap) object).get("trigger1").toString();
						String operatorBox = ((LinkedHashMap) object).get("operatorBox").toString();
						String trigger2 = ((LinkedHashMap) object).get("trigger2").toString();
						connectBody = notBox + "@!" + trigger1 + "@!" + operatorBox + "@!" + trigger2 + "#!"; 
					}
					signatureBody = signatureBody + connectBody;
				}
				triggerBody = triggerBody + signatureBody + "?!";
			}
			
			return triggerBody;		
		}

		private String convertDate(String dateTTL) {
			String formateDate = null;
			String[] date  = dateTTL.split("T");
			String[] parts = date[0].split("-");
			
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
			return formateDate;
		}
		
	private String callTrap(String trap, Object object) {
		String signatureBody = "";
		TrapDatas trapDatas = (TrapDatas) object;
		ArrayList<Object> attributeList = new ArrayList<>();
		// Read the Trap 
		if(!trap.equals("nill")){
			try{
				if(trap.startsWith("Trap")){
					if(trap.equals("Trap1")){
						 attributeList = trapDatas.getTrap1();
					}else if(trap.equals("Trap2")){
						attributeList = trapDatas.getTrap2();
					}else if(trap.equals("Trap3")){
						attributeList = trapDatas.getTrap3();
					}else if(trap.equals("Trap4")){
						attributeList = trapDatas.getTrap4();
					}else if(trap.equals("Trap5")){
						attributeList = trapDatas.getTrap5();
					}else if(trap.equals("Trap6")){
						attributeList = trapDatas.getTrap6();
					}
				}else{
					if(trap.equals("Fault")){
						if(trap.equals("Fault1")){
							attributeList = trapDatas.getTrap1();
						}else if(trap.equals("Fault2")){
							attributeList = trapDatas.getTrap2();
						}else if(trap.equals("Fault3")){
							attributeList = trapDatas.getTrap3();
						}else if(trap.equals("Fault4")){
							attributeList = trapDatas.getTrap4();
						}else if(trap.equals("Fault5")){
							attributeList = trapDatas.getTrap5();
						}else if(trap.equals("Fault6")){
							attributeList = trapDatas.getTrap6();
						}	
					}
				}
			} catch(Exception e){
				return "(" + trap + ")";
			}
		}else{
			if(trapDatas.getTrap1()!=null){
				attributeList = trapDatas.getTrap1();
			}else{
				return "";
			}
		}
		signatureBody = signatureBody + "(" + readAttributes(attributeList, attributeList.size()-1) + ")";
		return signatureBody;
	}

	private String readAttributes(ArrayList<Object> object, int index) {
		String attributes = "";
		Map<String, String> trapSignatures = (Map<String, String>) object.get(index);
		// Read the Elements. 
		Object notBox = "";
		if(trapSignatures.keySet().contains("notBox")){
			notBox = trapSignatures.get("notBox");
		}
		if(notBox!=null){
			attributes = attributes + notBox.toString();
		}
		Object trapName1 = trapSignatures.get("trigger1");
		if(trapName1!=null){
			String attrib = trapName1.toString();
			if(attrib.startsWith("A")){
				try{
					int iy = Integer.parseInt(attrib.substring(1))-1;
					attributes = attributes + "(" + readAttributes(object, iy) + ")";
				}catch(NumberFormatException e){
					try {
						attrib	= getVarbindOID(attrib);
						attributes = attributes + "("+ URLEncoder.encode(attrib, "UTF-8")+ ")";
					} catch (UnsupportedEncodingException e1) {
						//logger.error("Caused Exception while Encoding Varbind Dictionary Values"+e1);
					}
				}
			}else{
				try {
					attrib	= getVarbindOID(attrib);
					attributes = attributes + "("+ URLEncoder.encode(attrib, "UTF-8")+ ")";
				} catch (UnsupportedEncodingException e) {
					//logger.error("Caused Exception while Encoding Varbind Dictionary Values"+e);
				}
			}
		}else{
			return "";
		}
		Object comboBox = trapSignatures.get("operatorBox");
		if(comboBox!=null){
			attributes = attributes + comboBox.toString();
		}else{
			return attributes;
		}
		Object trapName2 = trapSignatures.get("trigger2"); 
		if(trapName2!=null){
			String attrib = trapName2.toString();
			if(attrib.startsWith("A")){
				try{
					int iy = Integer.parseInt(attrib.substring(1))-1;
					attributes = attributes + "(" + readAttributes(object, iy) + ")";
				}catch(NumberFormatException e){
					try {
						attrib	= getVarbindOID(attrib);
						attributes = attributes + "("+ URLEncoder.encode(attrib, "UTF-8") + ")";
					} catch (UnsupportedEncodingException e1) {
						//logger.error("Caused Exception while Encoding Varbind Dictionary Values"+e1);
					}
				}
			}else{
				try {
					attrib	= getVarbindOID(attrib);
					attributes = attributes + "("+ URLEncoder.encode(attrib, "UTF-8") + ")";
				} catch (UnsupportedEncodingException e) {
					//logger.error("Caused Exception while Encoding Varbind Dictionary Values"+e);
				}
			}
		}
		return attributes;
	}

	private String getVarbindOID(String attrib) {
		VarbindDictionary varbindId = varbindDictionaryDao.getVarbindEntityByName(attrib).get(0);
		return varbindId.getVarbindOID();
	}

	private String connectTriggerSignature(int index, ArrayList<Object> triggerSignatures, Object object) {
		String resultBody = "";
		Map<String, String> connectTraps = (Map<String, String>) triggerSignatures.get(index);
		try{
			String notBox = "";
			if(connectTraps.keySet().contains("notBox")){
				notBox = connectTraps.get("notBox");
			}
			resultBody = resultBody + "(" + notBox;
		}catch(NullPointerException e){
			resultBody = resultBody + "(";
		}
		String connectTrap1 = connectTraps.get("connectTrap1");
		if(connectTrap1.startsWith("Trap") || connectTrap1.startsWith("Fault")){
			String trapBody = callTrap(connectTrap1, object);
			if(trapBody!=null){
				resultBody = resultBody + trapBody;
			}
		}else if(connectTrap1.startsWith("C")){
			for(int i=0; i<= triggerSignatures.size(); i++){
				Map<String,String> triggerSignature = (Map<String, String>) triggerSignatures.get(i);
				if(triggerSignature.get("id").equals(connectTrap1)){
					resultBody = resultBody + "(";
					String connectBody = connectTriggerSignature(i, triggerSignatures, object);
					resultBody = resultBody + connectBody + ")";
				}else{
					i++;
				}
			}
		}
		try{
			String trapCount1 = connectTraps.get("trapCount1");
			resultBody = resultBody + ", Time = " + trapCount1 + ")";
		}catch(NullPointerException e){
		}
		try{
			String operatorBox = connectTraps.get("operatorBox");
			resultBody = resultBody + operatorBox +"(";
		}catch (NullPointerException e){
		}
		try{
			String connectTrap2 = connectTraps.get("connectTrap2");
			if(connectTrap2.startsWith("Trap") || connectTrap2.startsWith("Fault")){
				String trapBody = callTrap(connectTrap2, object);
				if(trapBody!=null){
					resultBody = resultBody + trapBody;
				}
			}else if(connectTrap2.startsWith("C")){
				for(int i=0; i<= triggerSignatures.size(); i++){
					Map<String,String> triggerSignature = (Map<String, String>) triggerSignatures.get(i);
					if(triggerSignature.get("id").equals(connectTrap2)){
						resultBody = resultBody + "(";
						String connectBody = connectTriggerSignature(i, triggerSignatures, object);
						resultBody = resultBody + connectBody + ")";
					}else{
						i++;
					}
				}
			}
		}catch(NullPointerException e){
		}
		try{
			String trapCount2 = connectTraps.get("trapCount2");
			resultBody = resultBody + ", Time = " + trapCount2 + ")";
		}catch(NullPointerException e){
		}
		return resultBody;
	}

	public  void PrePopulateClosedLoopFaultPolicyData(PolicyAdapter policyAdapter) {
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("Fault_") +6 , policyAdapter.getPolicyName().lastIndexOf("."));
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			// Get the target data under policy.
			TargetType target = policy.getTarget();
			if (target != null) {
				// Under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AnyOFType we have AllOFType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							int index = 0;
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attributevalue and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);

										// First match in the target is EcompName, so set that value.
										if (index == 1) {
											policyAdapter.setEcompName(value);
											EcompName ecompName = new EcompName();
											ecompName.setEcompName(value);
											policyAdapter.setEcompNameField(ecompName);
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
			String jsonBodyName = policyAdapter.getDirPath().replace(File.separator, ".")+ "." + policyAdapter.getOldPolicyFileName() + ".";
			policyAdapter.setConfigBodyPath(jsonBodyName);
			readClosedLoopJSONFile(policyAdapter);
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
	
	private String readClosedLoopJSONFile(PolicyAdapter policyAdapter) {
		String fileLocation = null;
		String fileName = policyAdapter.getConfigBodyPath();
		if (fileName != null ) {
			fileLocation = PolicyController.getConfigHome();
		}		
		if (fileLocation == null) {
			return fileLocation;
		}
		File dir = new File(fileLocation);
		File[] listOfFiles = dir.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(fileName)) {
				FileInputStream inputStream = null;
				String location = file.toString();
				try {
					inputStream = new FileInputStream(location);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if (location.endsWith("json")) {
					JsonReader jsonReader = null;
					jsonReader = Json.createReader(inputStream);
					ObjectMapper mapper = new ObjectMapper();
					try {
						ClosedLoopFaultBody closedLoopBody = mapper.readValue(jsonReader.read().toString(), ClosedLoopFaultBody.class);
						if(closedLoopBody.getClosedLoopPolicyStatus().equalsIgnoreCase("ACTIVE")){
							closedLoopBody.setClosedLoopPolicyStatus("Active");
						}else{
							closedLoopBody.setClosedLoopPolicyStatus("InActive");
						}
						policyAdapter.setJsonBodyData(closedLoopBody);
						if(closedLoopBody.getTrapMaxAgeUsedForUI() != null){
							policyAdapter.setTrapMaxAge(closedLoopBody.getTrapMaxAgeUsedForUI().toString());
						}
						if(closedLoopBody.getTriggerTimeWindowUsedForUI() != null){
							policyAdapter.setClearTimeOut(closedLoopBody.getTriggerTimeWindowUsedForUI().toString());
						}
						if(closedLoopBody.getVerfificationTimeWindowUsedForUI() != null){
							policyAdapter.setVerificationclearTimeOut(closedLoopBody.getVerfificationTimeWindowUsedForUI().toString());
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					jsonReader.close();
				}
			}
		}
		return null;	
	}

}

class ClosedLoopGridJSONData{

	private String clearTimeOut;
	private String trapMaxAge;
	private String verificationclearTimeOut;
	private ArrayList<Object> connecttriggerSignatures;
	private ArrayList<Object> connectVerificationSignatures;

	public String getClearTimeOut() {
		return clearTimeOut;
	}
	public void setClearTimeOut(String clearTimeOut) {
		this.clearTimeOut = clearTimeOut;
	}
	public String getTrapMaxAge() {
		return trapMaxAge;
	}
	public void setTrapMaxAge(String trapMaxAge) {
		this.trapMaxAge = trapMaxAge;
	}
	public String getVerificationclearTimeOut() {
		return verificationclearTimeOut;
	}
	public void setVerificationclearTimeOut(String verificationclearTimeOut) {
		this.verificationclearTimeOut = verificationclearTimeOut;
	}
	

	public ArrayList<Object> getConnecttriggerSignatures() {
		return connecttriggerSignatures;
	}
	public void setConnecttriggerSignatures(ArrayList<Object> connecttriggerSignatures) {
		this.connecttriggerSignatures = connecttriggerSignatures;
	}
	public ArrayList<Object> getConnectVerificationSignatures() {
		return connectVerificationSignatures;
	}
	public void setConnectVerificationSignatures(ArrayList<Object> connectVerificationSignatures) {
		this.connectVerificationSignatures = connectVerificationSignatures;
	}
}

class TrapDatas{
	private ArrayList<Object> trap1;
	private ArrayList<Object> trap2;
	private ArrayList<Object> trap3;
	private ArrayList<Object> trap4;
	private ArrayList<Object> trap5;
	private ArrayList<Object> trap6;
	public ArrayList<Object> getTrap1() {
		return trap1;
	}
	public void setTrap1(ArrayList<Object> trap1) {
		this.trap1 = trap1;
	}
	public ArrayList<Object> getTrap2() {
		return trap2;
	}
	public void setTrap2(ArrayList<Object> trap2) {
		this.trap2 = trap2;
	}
	public ArrayList<Object> getTrap3() {
		return trap3;
	}
	public void setTrap3(ArrayList<Object> trap3) {
		this.trap3 = trap3;
	}
	public ArrayList<Object> getTrap4() {
		return trap4;
	}
	public void setTrap4(ArrayList<Object> trap4) {
		this.trap4 = trap4;
	}
	public ArrayList<Object> getTrap5() {
		return trap5;
	}
	public void setTrap5(ArrayList<Object> trap5) {
		this.trap5 = trap5;
	}
	public ArrayList<Object> getTrap6() {
		return trap6;
	}
	public void setTrap6(ArrayList<Object> trap6) {
		this.trap6 = trap6;
	}
}