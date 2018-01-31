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

package org.onap.policy.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.adapter.ClosedLoopFaultBody;
import org.onap.policy.rest.adapter.ClosedLoopFaultTriggerUISignatures;
import org.onap.policy.rest.adapter.ClosedLoopSignatures;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.VarbindDictionary;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

@Controller
@RequestMapping("/")
public class CreateClosedLoopFaultController extends RestrictedBaseController{

	private static final Logger policyLogger	= FlexLogger.getLogger(CreateClosedLoopFaultController.class);

	protected PolicyRestAdapter policyAdapter = null;


	private static CommonClassDao commonclassdao;

	@Autowired
	private CreateClosedLoopFaultController(CommonClassDao commonclassdao){
		CreateClosedLoopFaultController.commonclassdao = commonclassdao;
	}

	public CreateClosedLoopFaultController(){
		// Empty constructor
	}

	public PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter policyData, JsonNode root){
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			TrapDatas trapDatas = mapper.readValue(root.get("trapData").toString(), TrapDatas.class);
			TrapDatas faultDatas = mapper.readValue(root.get("faultData").toString(), TrapDatas.class);
			ClosedLoopGridJSONData policyJsonData = mapper.readValue(root.get("policyData").get("policy").toString(), ClosedLoopGridJSONData.class);
			ClosedLoopFaultBody jsonBody = mapper.readValue(root.get("policyData").get("policy").get("jsonBodyData").toString(), ClosedLoopFaultBody.class);

			ArrayList<Object> trapSignatureDatas = new ArrayList<>();
			if(trapDatas.getTrap1() != null){
				trapSignatureDatas.add(trapDatas);
			}
			ArrayList<Object> faultSignatureDatas = new ArrayList<>();
			if(faultDatas.getTrap1() != null){
				faultSignatureDatas.add(faultDatas);
			}

			StringBuilder resultBody = new StringBuilder();
			if(!policyJsonData.getConnecttriggerSignatures().isEmpty()){
				resultBody.append("(");
				for(int i = policyJsonData.getConnecttriggerSignatures().size()-1; i>=0 ; i--){
					String connectBody = connectTriggerSignature(i, policyJsonData.getConnecttriggerSignatures(), trapSignatureDatas.get(0));
					resultBody.append(connectBody);
				}
				resultBody.append(resultBody + ")");
			}else{
				if(!trapSignatureDatas.isEmpty()){
					resultBody.append(callTrap("nill", trapSignatureDatas.get(0)));
				}
			}
			ClosedLoopSignatures triggerSignatures = new ClosedLoopSignatures();
			triggerSignatures.setSignatures(resultBody.toString());
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
				jsonBody.setTrapMaxAge(Integer.parseInt(policyData.getTrapMaxAge()));
			}

			jsonBody.setTriggerSignatures(triggerSignatures);
			StringBuilder faultBody = new StringBuilder();
			if(!policyJsonData.getConnectVerificationSignatures().isEmpty()){
				faultBody.append("(");
				for(int i = policyJsonData.getConnectVerificationSignatures().size()-1; i>=0 ; i--){
					String connectBody = connectTriggerSignature(i, policyJsonData.getConnectVerificationSignatures(), faultSignatureDatas.get(0));
					faultBody.append(connectBody);
				}
				faultBody.append(")");
			}else{
				if(!faultSignatureDatas.isEmpty()){
					faultBody.append(callTrap("nill", faultSignatureDatas.get(0)));
				}
			}
			ClosedLoopSignatures faultSignatures = new ClosedLoopSignatures();
			faultSignatures.setSignatures(faultBody.toString());
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
				jsonBody.setVerificationTimeWindowUsedForUI(Integer.parseInt(policyData.getVerificationclearTimeOut()));
			}
			jsonBody.setVerificationSignatures(faultSignatures);
			ObjectWriter om = new ObjectMapper().writer();
			String json = om.writeValueAsString(jsonBody);
			policyData.setJsonBody(json);

		}catch(Exception e){
			policyLogger.error("Exception Occured while setting data to Adapter" , e);
		}
		return policyData;
	}


	@SuppressWarnings("unchecked")
	private String connectTriggerSignature(int index, ArrayList<Object> triggerSignatures, Object object) {
		StringBuilder resultBody = new StringBuilder();
		Map<String, String> connectTraps = (Map<String, String>) triggerSignatures.get(index);
		try{
			String notBox = "";
			if(connectTraps.keySet().contains("notBox")){
				notBox = connectTraps.get("notBox");
			}
			resultBody.append("(" + notBox);
		}catch(NullPointerException e){
			policyLogger.info("General error" , e);
			resultBody.append("(");
		}
		String connectTrap1 = connectTraps.get("connectTrap1");
		if(connectTrap1.startsWith("Trap") || connectTrap1.startsWith("Fault")){
			String trapBody = callTrap(connectTrap1, object);
			if(trapBody!=null){
				resultBody.append(trapBody);
			}
		}else if(connectTrap1.startsWith("C")){
			for(int i=0; i<= triggerSignatures.size(); i++){
				Map<String,String> triggerSignature = (Map<String, String>) triggerSignatures.get(i);
				if(triggerSignature.get("id").equals(connectTrap1)){
					resultBody.append("(");
					String connectBody = connectTriggerSignature(i, triggerSignatures, object);
					resultBody.append(connectBody + ")");
				}else{
					i++;
				}
			}
		}
		try{
			String trapCount1 = connectTraps.get("trapCount1");
			resultBody.append(", Time = " + trapCount1 + ")");
		}catch(NullPointerException e){
			policyLogger.info("General error" , e);
		}
		try{
			String operatorBox = connectTraps.get("operatorBox");
			resultBody.append(operatorBox +"(");
		}catch (NullPointerException e){
			policyLogger.info("General error" , e);
		}
		try{
			String connectTrap2 = connectTraps.get("connectTrap2");
			if(connectTrap2.startsWith("Trap") || connectTrap2.startsWith("Fault")){
				String trapBody = callTrap(connectTrap2, object);
				if(trapBody!=null){
					resultBody.append(trapBody);
				}
			}else if(connectTrap2.startsWith("C")){
				for(int i=0; i<= triggerSignatures.size(); i++){
					Map<String,String> triggerSignature = (Map<String, String>) triggerSignatures.get(i);
					if(triggerSignature.get("id").equals(connectTrap2)){
						resultBody.append("(");
						String connectBody = connectTriggerSignature(i, triggerSignatures, object);
						resultBody.append(connectBody + ")");
					}else{
						i++;
					}
				}
			}
		}catch(NullPointerException e){
			policyLogger.info("General error" , e);
		}
		try{
			String trapCount2 = connectTraps.get("trapCount2");
			resultBody.append(", Time = " + trapCount2 + ")");
		}catch(NullPointerException e){
			policyLogger.info("General error" , e);
		}
		return resultBody.toString();
	}


	private String callTrap(String trap, Object object) {
		String signatureBody = "";
		TrapDatas trapDatas = (TrapDatas) object;
		ArrayList<Object> attributeList = new ArrayList<>();
		// Read the Trap
		if(! "nill".equals(trap)){
			try{
				if(trap.startsWith("Trap")){
					if("Trap1".equals(trap)){
						 attributeList = trapDatas.getTrap1();
					}else if("Trap2".equals(trap)){
						attributeList = trapDatas.getTrap2();
					}else if("Trap3".equals(trap)){
						attributeList = trapDatas.getTrap3();
					}else if("Trap4".equals(trap)){
						attributeList = trapDatas.getTrap4();
					}else if("Trap5".equals(trap)){
						attributeList = trapDatas.getTrap5();
					}else if("Trap6".equals(trap)){
						attributeList = trapDatas.getTrap6();
					}
				}else{
					if(trap.startsWith("Fault")){
						if("Fault1".equals(trap)){
							attributeList = trapDatas.getTrap1();
						}else if("Fault2".equals(trap)){
							attributeList = trapDatas.getTrap2();
						}else if("Fault3".equals(trap)){
							attributeList = trapDatas.getTrap3();
						}else if("Fault4".equals(trap)){
							attributeList = trapDatas.getTrap4();
						}else if("Fault5".equals(trap)){
							attributeList = trapDatas.getTrap5();
						}else if("Fault6".equals(trap)){
							attributeList = trapDatas.getTrap6();
						}
					}
				}
			} catch(Exception e){
			    policyLogger.warn("Error during callTrap" , e);
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

	@SuppressWarnings("unchecked")
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
						policyLogger.error("Caused Exception while Encoding Varbind Dictionary Values",e1);
					}
				}
			}else{
				try {
					attrib	= getVarbindOID(attrib);
					attributes = attributes + "("+ URLEncoder.encode(attrib, "UTF-8")+ ")";
				} catch (UnsupportedEncodingException e) {
					policyLogger.error("Caused Exception while Encoding Varbind Dictionary Values",e);
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
						policyLogger.error("Caused Exception while Encoding Varbind Dictionary Values",e1);
					}
				}
			}else{
				try {
					attrib	= getVarbindOID(attrib);
					attributes = attributes + "("+ URLEncoder.encode(attrib, "UTF-8") + ")";
				} catch (UnsupportedEncodingException e) {
					policyLogger.error("Caused Exception while Encoding Varbind Dictionary Values",e);
				}
			}
		}
		return attributes;
	}

	private String getVarbindOID(String attrib) {
		VarbindDictionary varbindId = null;
		try{
			 varbindId = (VarbindDictionary) commonclassdao.getEntityItem(VarbindDictionary.class, "varbindName", attrib);
			 return varbindId.getVarbindOID();
		}catch(Exception e){
		    policyLogger.error("Error during retrieving varbindName " + attrib, e);
			return attrib;
		}
	}


	//connect traps data set to JSON Body as String
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private String getUIConnectTraps(ArrayList<Object> connectTrapSignatures) {
			StringBuilder resultBody = new StringBuilder();
			String connectMainBody = "";
			for(int j = 0; j < connectTrapSignatures.size(); j++){
				Map<String, String> connectTraps = (Map<String, String>)connectTrapSignatures.get(j);
				String connectBody = "";
				Object object = connectTraps;
				if(object instanceof LinkedHashMap<?, ?>){
					String notBox = "";
					String connectTrap1 = "";
					String trapCount1 = "";
					String operatorBox = "";
					String connectTrap2 = "";
					String trapCount2 = "";
					if(((LinkedHashMap) object).keySet().contains("notBox")){
						notBox = ((LinkedHashMap) object).get("notBox").toString();
					}
					if(((LinkedHashMap) object).get("connectTrap1") != null){
						connectTrap1 = ((LinkedHashMap) object).get("connectTrap1").toString();
					}
					if(((LinkedHashMap) object).get("trapCount1") != null){
						trapCount1 = ((LinkedHashMap) object).get("trapCount1").toString();
					}
					if(((LinkedHashMap) object).get("operatorBox") != null){
						operatorBox = ((LinkedHashMap) object).get("operatorBox").toString();
					}
					if(((LinkedHashMap) object).get("connectTrap2") != null){
						connectTrap2 = ((LinkedHashMap) object).get("connectTrap2").toString();
					}
					if(((LinkedHashMap) object).get("trapCount2") != null){
						trapCount2 = ((LinkedHashMap) object).get("trapCount2").toString();
					}
					connectBody = notBox + "@!" + connectTrap1 + "@!" + trapCount1 + "@!" + operatorBox + "@!" + connectTrap2 + "@!" + trapCount2 + "#!?!";
				}
				resultBody.append(connectBody);
			}
			connectMainBody = connectMainBody + resultBody;
			return connectMainBody;
		}



			// get Trigger signature from JSON body
			@SuppressWarnings({ "rawtypes", "unchecked" })
			private String getUITriggerSignature(String trap, Object object2) {
				StringBuilder triggerBody = new StringBuilder();
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
					if(trap.startsWith("Fault")){
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
					StringBuilder signatureBody = new StringBuilder();
					ArrayList<Object> connectTraps = (ArrayList<Object>) attributeList.get(j);
					for(int i =0 ; i < connectTraps.size(); i++){
						String connectBody = "";
						Object object = connectTraps.get(i);
						if(object instanceof LinkedHashMap<?, ?>){
							String notBox = "";
							String trigger1 = "";
							String operatorBox = "";
							String trigger2 = "";
							if(((LinkedHashMap) object).keySet().contains("notBox")){
								notBox = ((LinkedHashMap) object).get("notBox").toString();
							}
							if(((LinkedHashMap) object).get("trigger1") != null){
								trigger1 = ((LinkedHashMap) object).get("trigger1").toString();
							}
							if(((LinkedHashMap) object).get("operatorBox") != null){
								operatorBox = ((LinkedHashMap) object).get("operatorBox").toString();
							}
							if(((LinkedHashMap) object).get("trigger2") != null){
								trigger2 = ((LinkedHashMap) object).get("trigger2").toString();
							}
							connectBody = notBox + "@!" + trigger1 + "@!" + operatorBox + "@!" + trigger2 + "#!";
						}
						signatureBody.append(connectBody);
					}
					triggerBody.append(signatureBody + "?!");
				}

				return triggerBody.toString();
			}

	public  void prePopulateClosedLoopFaultPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("Fault_") +6);
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
			    policyLogger.error("Error during collecting the description tag info for createClosedLoopFault " + policyNameValue , e);
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
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();

										// First match in the target is OnapName, so set that value.
										if ("ONAPName".equals(attributeId)) {
											policyAdapter.setOnapName(value);
											OnapName onapName = new OnapName();
											onapName.setOnapName(value);
											policyAdapter.setOnapNameField(onapName);
										}
										if ("RiskType".equals(attributeId)){
											policyAdapter.setRiskType(value);
										}
										if ("RiskLevel".equals(attributeId)){
											policyAdapter.setRiskLevel(value);
										}
										if ("guard".equals(attributeId)){
											policyAdapter.setGuard(value);
										}
										if ("TTLDate".equals(attributeId) && !value.contains("NA")){
											PolicyController controller = new PolicyController();
											String newDate = controller.convertDate(value);
											policyAdapter.setTtlDate(newDate);
										}
									}
								}
							}
						}
					}
				}
			}
			readClosedLoopJSONFile(policyAdapter, entity);
		}

	}

	private String readClosedLoopJSONFile(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			ClosedLoopFaultBody closedLoopBody = mapper.readValue(entity.getConfigurationData().getConfigBody(), ClosedLoopFaultBody.class);
			if("ACTIVE".equalsIgnoreCase(closedLoopBody.getClosedLoopPolicyStatus())){
				closedLoopBody.setClosedLoopPolicyStatus("Active");
			}else{
				closedLoopBody.setClosedLoopPolicyStatus("InActive");
			}
			policyAdapter.setJsonBodyData(closedLoopBody);
			if(closedLoopBody.getTrapMaxAge() != null){
				policyAdapter.setTrapMaxAge(closedLoopBody.getTrapMaxAge().toString());
			}
			if(closedLoopBody.getTriggerTimeWindowUsedForUI() != null){
				policyAdapter.setClearTimeOut(closedLoopBody.getTriggerTimeWindowUsedForUI().toString());
			}
			if(closedLoopBody.getVerificationTimeWindowUsedForUI() != null){
				policyAdapter.setVerificationclearTimeOut(closedLoopBody.getVerificationTimeWindowUsedForUI().toString());
			}

		} catch (Exception e) {
			policyLogger.error("Exception Occured"+e);
		}

		return null;
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