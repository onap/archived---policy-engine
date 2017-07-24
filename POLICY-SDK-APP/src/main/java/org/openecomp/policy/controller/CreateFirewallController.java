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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.rest.adapter.AddressGroupJson;
import org.openecomp.policy.rest.adapter.AddressJson;
import org.openecomp.policy.rest.adapter.AddressMembers;
import org.openecomp.policy.rest.adapter.DeployNowJson;
import org.openecomp.policy.rest.adapter.IdMap;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.adapter.PrefixIPList;
import org.openecomp.policy.rest.adapter.ServiceGroupJson;
import org.openecomp.policy.rest.adapter.ServiceListJson;
import org.openecomp.policy.rest.adapter.ServiceMembers;
import org.openecomp.policy.rest.adapter.ServicesJson;
import org.openecomp.policy.rest.adapter.TagDefines;
import org.openecomp.policy.rest.adapter.Tags;
import org.openecomp.policy.rest.adapter.Term;
import org.openecomp.policy.rest.adapter.TermCollector;
import org.openecomp.policy.rest.adapter.VendorSpecificData;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.AddressGroup;
import org.openecomp.policy.rest.jpa.FWTagPicker;
import org.openecomp.policy.rest.jpa.GroupServiceList;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.policy.rest.jpa.PrefixList;
import org.openecomp.policy.rest.jpa.SecurityZone;
import org.openecomp.policy.rest.jpa.ServiceList;
import org.openecomp.policy.rest.jpa.TermList;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
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
public class CreateFirewallController extends RestrictedBaseController {
	private static Logger policyLogger	= FlexLogger.getLogger(CreateFirewallController.class);

	@Autowired
	SessionFactory sessionFactory;
	
	private static CommonClassDao commonClassDao;

	private List<String> tagCollectorList;
	private String jsonBody;
	List<String> expandablePrefixIPList = new ArrayList<>();
	List<String> expandableServicesList= new ArrayList<>();
	@Autowired
	private CreateFirewallController(CommonClassDao commonClassDao){
		CreateFirewallController.commonClassDao = commonClassDao;
	}

	public CreateFirewallController(){}
	private List<String> termCollectorList;
	private ArrayList<Object> attributeList;


	public PolicyRestAdapter setDataToPolicyRestAdapter(PolicyRestAdapter policyData){
		
		termCollectorList = new ArrayList<>();
		tagCollectorList = new ArrayList<>();
		if(!policyData.getAttributes().isEmpty()){
			for(Object attribute : policyData.getAttributes()){
				if(attribute instanceof LinkedHashMap<?, ?>){
					String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
					termCollectorList.add(key);
					
					String tag = ((LinkedHashMap<?, ?>) attribute).get("value").toString();
					tagCollectorList.add(tag);
				}
			}
		}
		jsonBody = constructJson(policyData);	
		if (jsonBody != null && !jsonBody.equalsIgnoreCase("")) {
			policyData.setJsonBody(jsonBody);
		} else {
			policyData.setJsonBody("{}");
		}
		policyData.setJsonBody(jsonBody);
		
		return policyData;
	}

	private List<String> mapping(String expandableList) {
		List <String> valueDesc= new ArrayList<>();
		List<Object> prefixListData = commonClassDao.getData(PrefixList.class);
		for (int i = 0; i< prefixListData.size(); i++) {
			PrefixList prefixList = (PrefixList) prefixListData.get(i);
			if (prefixList.getPrefixListName().equals(expandableList)) {
				String value = prefixList.getPrefixListValue();
				valueDesc.add(value);
				String desc= prefixList.getDescription();
				valueDesc.add(desc);
				break;
			}
		}
		return valueDesc;
	}

	private ServiceList mappingServiceList(String expandableList) {	
		ServiceList serviceList=null;
		List<Object> serviceListData = commonClassDao.getData(ServiceList.class);
		for (int i = 0; i< serviceListData.size(); i++) {
			serviceList = (ServiceList) serviceListData.get(i);
			if (serviceList.getServiceName().equals(expandableList)) {
				break;
			}
		}
		return serviceList;
	}

	private GroupServiceList mappingServiceGroup(String expandableList) {

		GroupServiceList serviceGroup=null;
		List<Object> serviceGroupData = commonClassDao.getData(GroupServiceList.class);
		for (int i = 0; i< serviceGroupData.size(); i++) {
			serviceGroup = (GroupServiceList) serviceGroupData.get(i);
			if (serviceGroup.getGroupName().equals(expandableList)) {
				break;
			}
		}
		return serviceGroup;
	}

	private AddressGroup mappingAddressGroup(String expandableList) {

		AddressGroup addressGroup=null;
		List<Object> addressGroupData = commonClassDao.getData(AddressGroup.class);
		for (int i = 0; i< addressGroupData.size(); i++) {
			addressGroup = (AddressGroup) addressGroupData.get(i);
			if (addressGroup.getGroupName().equals(expandableList)) {
				break;
			}
		}
		return addressGroup;
	}

	public void prePopulateFWPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		attributeList = new ArrayList<>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			Object policyData = policyAdapter.getPolicyData();
			PolicyType policy = (PolicyType) policyData;
			// policy name value is the policy name without any prefix and Extensions.
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("FW_") +3);
			if (policyLogger.isDebugEnabled()) {
				policyLogger.debug("Prepopulating form data for Config Policy selected:"+ policyAdapter.getPolicyName());
			}
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				policyLogger.info("General error", e);
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);

			ObjectMapper mapper = new ObjectMapper();

			TermCollector tc1=null;
			try {
				//Json conversion. 
				String data=null;
				SecurityZone jpaSecurityZone;
				data = entity.getConfigurationData().getConfigBody();
				tc1 = (TermCollector)mapper.readValue(data, TermCollector.class);
				List<Object> securityZoneData = commonClassDao.getData(SecurityZone.class);
				for (int i = 0; i < securityZoneData.size() ; i++) {
					jpaSecurityZone = (SecurityZone) securityZoneData.get(i);
					if (jpaSecurityZone.getZoneValue().equals(tc1.getSecurityZoneId())){
						policyAdapter.setSecurityZone(jpaSecurityZone.getZoneName());
						break;
					}
				}
			}
			catch(Exception e) {
				policyLogger.error("Exception Caused while Retriving the JSON body data" +e);
			}
			
			Map<String, String> termTagMap=null;
			if(tc1 != null){
				for(int i=0;i<tc1.getFirewallRuleList().size();i++){
					termTagMap = new HashMap<String, String>();
					String ruleName= tc1.getFirewallRuleList().get(i).getRuleName();
					String tagPickerName=tc1.getRuleToTag().get(i).getTagPickerName();
					termTagMap.put("key", ruleName);
					termTagMap.put("value", tagPickerName);
					attributeList.add(termTagMap);
				}
			}
			policyAdapter.setAttributes(attributeList);
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
										if (attributeId.equals("ConfigName")) {
											policyAdapter.setConfigName(value);
										}
										if (attributeId.equals("RiskType")){
											policyAdapter.setRiskType(value);
										}
										if (attributeId.equals("RiskLevel")){
											policyAdapter.setRiskLevel(value);
										}
										if (attributeId.equals("guard")){
											policyAdapter.setGuard(value);
										}
										if (attributeId.equals("TTLDate") && !value.contains("NA")){
											String newDate = convertDate(value, true);
											policyAdapter.setTtlDate(newDate);
										}
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
	public void setFWViewRule(HttpServletRequest request, HttpServletResponse response){
		try {
			termCollectorList = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyRestAdapter policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").toString(), PolicyRestAdapter.class);
			if(!policyData.getAttributes().isEmpty()){
				for(Object attribute : policyData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("key").toString();
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
			List <String> valueDesc= new ArrayList<>();
			StringBuffer displayString = new StringBuffer();
			for (String id : termCollectorList) {
				List<Object> tmList = commonClassDao.getDataById(TermList.class, "termName", id);
				jpaTermList = (TermList) tmList.get(0);
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
		} catch (Exception e) {
			policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	private String constructJson(PolicyRestAdapter policyData) {
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


		List<String> expandableList = new ArrayList<>();
		TermList jpaTermList;
		TermCollector tc = new TermCollector();
		SecurityZone jpaSecurityZone;
		List<Term> termList = new ArrayList<>();
		
		Tags tags=null;
		List<Tags>tagsList= new ArrayList<>();
		
		TagDefines tagDefine= new TagDefines();
		List<TagDefines> tagList=null;
		ServiceListJson targetSl=null;
		int i=0;
		try{
			String networkRole="";
			for(String tag:tagCollectorList){
				tags= new Tags();
				List<Object> tagListData = commonClassDao.getData(FWTagPicker.class);
				for(int tagCounter=0; tagCounter<tagListData.size(); tagCounter++){
					FWTagPicker jpaTagPickerList=(FWTagPicker) tagListData.get(tagCounter);
					if (jpaTagPickerList.getTagPickerName().equals(tag) ){
						String tagValues=jpaTagPickerList.getTagValues();
						tagList= new ArrayList<>();
						for(String val:tagValues.split("#")) {
							int index=val.indexOf(":");
							String keyToStore=val.substring(0,index);
							String valueToStore=val.substring(index+1,val.length());
							
							tagDefine= new TagDefines();
							tagDefine.setKey(keyToStore);
							tagDefine.setValue(valueToStore);
							//Add to the collection.
							tagList.add(tagDefine);
							
						}
						networkRole=jpaTagPickerList.getNetworkRole();
						break;
					}
				}	
				tags.setTags(tagList);
				tags.setTagPickerName(tag);
				tags.setRuleName(termCollectorList.get(i));
				tags.setNetworkRole(networkRole);
				tagsList.add(tags);
				i++;
			}
			tc.setRuleToTag(tagsList);

			for (int tl = 0 ; tl< termCollectorList.size(); tl++) {
				expandableList.add(termCollectorList.get(tl));
				Term targetTerm = new Term();
				//targetSl= new ServiceListJson();
				targetTerm.setRuleName(termCollectorList.get(tl));
				List<Object> termListData = commonClassDao.getData(TermList.class);
				for (int j =0; j < termListData.size(); j++) {
					jpaTermList = (TermList) termListData.get(j);
					if (jpaTermList.getTermName().equals(termCollectorList.get(tl))){
						ruleDesc=jpaTermList.getTermDescription();
						if ((ruleDesc!=null)&& (!ruleDesc.isEmpty())){
							targetTerm.setDescription(ruleDesc);
						}	
						ruleFromZone=jpaTermList.getFromZone();	

						if ((ruleFromZone != null) && (!ruleFromZone.isEmpty())){
							fromZone_map = new HashMap<>();
							fromZone_map.put(tl, ruleFromZone);
						} 	
						ruleToZone=jpaTermList.getToZone();

						if ((ruleToZone != null) && (!ruleToZone.isEmpty())){
							toZone_map = new HashMap<>();
							toZone_map.put(tl, ruleToZone);
						} 
						ruleSrcPrefixList=jpaTermList.getSrcIPList();

						if ((ruleSrcPrefixList != null) && (!ruleSrcPrefixList.isEmpty())){
							srcIP_map = new HashMap<>();
							srcIP_map.put(tl, ruleSrcPrefixList);
						} 

						ruleDestPrefixList= jpaTermList.getDestIPList();
						if ((ruleDestPrefixList != null) && (!ruleDestPrefixList.isEmpty())){
							destIP_map = new HashMap<>();
							destIP_map.put(tl, ruleDestPrefixList);
						} 

						ruleSrcPort=jpaTermList.getSrcPortList();

						if (ruleSrcPort != null && (!ruleSrcPort.isEmpty())){
							srcPort_map = new HashMap<>();
							srcPort_map.put(tl, ruleSrcPort);
						} 

						ruleDestPort= jpaTermList.getDestPortList();

						if (ruleDestPort!= null && (!jpaTermList.getDestPortList().isEmpty())){
							destPort_map = new HashMap<>();
							destPort_map.put(tl, ruleDestPort);
						} 

						ruleAction=jpaTermList.getAction();

						if (( ruleAction!= null) && (!ruleAction.isEmpty())){
							action_map = new HashMap<>();
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
					List<String> fromZone= new ArrayList<>();
					for(String fromZoneStr:fromZone_map.get(tl).split(",") ){
						fromZone.add(fromZoneStr);
					}
					targetTerm.setFromZones(fromZone);
				}

				//ToZone arrays
				if(toZone_map!=null){
					List<String> toZone= new ArrayList<>();
					for(String toZoneStr:toZone_map.get(tl).split(",") ){
						toZone.add(toZoneStr);
					}
					targetTerm.setToZones(toZone);
				}

				//Destination Services.
				if(destPort_map!=null){
					Set<ServicesJson> destServicesJsonList= new HashSet<>();
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
					List<AddressJson> sourceListArrayJson= new ArrayList<>();			
					for(String srcList:srcIP_map.get(tl).split(",") ){
						AddressJson srcListJson= new AddressJson();
						if(srcList.equals("ANY")){
							srcListJson.setType("any");
							sourceListArrayJson.add(srcListJson);
							break;
						}else{
							srcListJson.setType("REFERENCE");
							if(srcList.startsWith("Group_")){
								srcListJson.setName(srcList.substring(6,srcList.length()));
							}else{
								srcListJson.setName(srcList);
							}
							sourceListArrayJson.add(srcListJson);
						}
					}
					targetTerm.setSourceList(sourceListArrayJson);
				}
				if(destIP_map!=null){
					//Destination List
					List<AddressJson> destListArrayJson= new ArrayList<>();				
					for(String destList:destIP_map.get(tl).split(",")){
						AddressJson destListJson= new AddressJson();
						if(destList.equals("ANY")){
							destListJson.setType("any");
							destListArrayJson.add(destListJson);
							break;
						}else{
							destListJson.setType("REFERENCE");
							if(destList.startsWith("Group_")){
								destListJson.setName(destList.substring(6,destList.length()));
							}else{
								destListJson.setName(destList);
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
			
			List<Object> securityZoneData = commonClassDao.getData(SecurityZone.class);
			for (int j =0 ; j< securityZoneData.size() ; j++){
				jpaSecurityZone = (SecurityZone) securityZoneData.get(j);
				if (jpaSecurityZone.getZoneName().equals(policyData.getSecurityZone())){
					tc.setSecurityZoneId(jpaSecurityZone.getZoneValue());
					IdMap idMapInstance= new IdMap();
					idMapInstance.setAstraId(jpaSecurityZone.getZoneValue());
					idMapInstance.setVendorId("deviceGroup:dev");
					
					List<IdMap> idMap = new ArrayList<IdMap>();
					idMap.add(idMapInstance);
					
					VendorSpecificData vendorStructure= new VendorSpecificData();
					vendorStructure.setIdMap(idMap);
					tc.setVendorSpecificData(vendorStructure);
					break;
				}
			}

			tc.setServiceTypeId("/v0/firewall/pan");
			tc.setConfigName(policyData.getConfigName());
			tc.setVendorServiceId("vipr");
			
			DeployNowJson deployNow= new DeployNowJson();
			deployNow.setDeployNow(false);

			tc.setDeploymentOption(deployNow);

			Set<ServiceListJson> servListArray = new HashSet<>();
			Set<ServiceGroupJson> servGroupArray= new HashSet<>();
			Set<AddressGroupJson> addrGroupArray= new HashSet<>();

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
						List<ServiceMembers> servMembersList= new ArrayList<>();

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

			Set<PrefixIPList> prefixIPList = new HashSet<>();
			for(String prefixList:expandablePrefixIPList){
				for(String prefixIP: prefixList.split(",")){
					if((!prefixIP.startsWith("Group_"))){
						if(!prefixIP.equals("ANY")){
							List<AddressMembers> addMembersList= new ArrayList<>();
							List<String> valueDesc= new ArrayList<>();
							PrefixIPList targetAddressList = new PrefixIPList();
							AddressMembers addressMembers= new AddressMembers();
							targetAddressList.setName(prefixIP);
							policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "PrefixList value:"+prefixIP);
							valueDesc = mapping(prefixIP);
							if(!valueDesc.isEmpty()){
								policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "PrefixList description:"+valueDesc.get(1));
								targetAddressList.setDescription(valueDesc.get(1));
							}
							

							addressMembers.setType("SUBNET");
							if(!valueDesc.isEmpty()) {
								addressMembers.setValue(valueDesc.get(0));
							}

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

						List<AddressMembers> addrMembersList= new ArrayList<>();
						for(String groupString: ag.getPrefixList().split(",")){
							List<String> valueDesc= new ArrayList<>();
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

			Set<Object> serviceGroup= new HashSet<>();

			for(Object obj1:servGroupArray){
				serviceGroup.add(obj1);
			}

			for(Object obj:servListArray){
				serviceGroup.add(obj);
			}

			Set<Object> addressGroup= new HashSet<>();

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
			} catch (Exception e) {
				policyLogger.error("Exception Occured"+e);
			}	

		}catch (Exception e) {
			policyLogger.error("Exception Occured"+e);
		}

		return json;
	}

}
