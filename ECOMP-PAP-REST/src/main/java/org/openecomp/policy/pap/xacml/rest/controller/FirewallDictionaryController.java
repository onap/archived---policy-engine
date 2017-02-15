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

package org.openecomp.policy.pap.xacml.rest.controller;

import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.netty.handler.ipfilter.CIDR;
import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.adapters.GridData;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.ActionListDao;
import org.openecomp.policy.rest.dao.AddressGroupDao;
import org.openecomp.policy.rest.dao.FirewallDictionaryListDao;
import org.openecomp.policy.rest.dao.PortListDao;
import org.openecomp.policy.rest.dao.PrefixListDao;
import org.openecomp.policy.rest.dao.ProtocolListDao;
import org.openecomp.policy.rest.dao.SecurityZoneDao;
import org.openecomp.policy.rest.dao.ServiceGroupDao;
import org.openecomp.policy.rest.dao.ServiceListDao;
import org.openecomp.policy.rest.dao.TermListDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.dao.ZoneDao;
import org.openecomp.policy.rest.jpa.ActionList;
import org.openecomp.policy.rest.jpa.AddressGroup;
import org.openecomp.policy.rest.jpa.FirewallDictionaryList;
import org.openecomp.policy.rest.jpa.GroupServiceList;
import org.openecomp.policy.rest.jpa.PREFIXLIST;
import org.openecomp.policy.rest.jpa.PortList;
import org.openecomp.policy.rest.jpa.ProtocolList;
import org.openecomp.policy.rest.jpa.SecurityZone;
import org.openecomp.policy.rest.jpa.ServiceList;
import org.openecomp.policy.rest.jpa.TermList;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.rest.jpa.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class FirewallDictionaryController {

	@Autowired
	PrefixListDao prefixListDao;
	
	@Autowired
	PortListDao portListDao;
	
	@Autowired
	ProtocolListDao protocolListDao;
	
	@Autowired
	AddressGroupDao addressGroupDao;
	
	@Autowired
	ActionListDao actionListDao;
	
	@Autowired
	SecurityZoneDao securityZoneDao;
	
	@Autowired
	ServiceGroupDao serviceGroupDao;
	
	@Autowired
	ServiceListDao serviceListDao;
	
	@Autowired
	TermListDao termListDao;
	
	@Autowired
	ZoneDao zoneDao;
	
	@Autowired
	UserInfoDao userInfoDao;
	
	@Autowired
	FirewallDictionaryListDao fwDictionaryListDao;
	

	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;		
	}

	@RequestMapping(value={"/get_PrefixListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPrefixListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("prefixListDictionaryDatas", mapper.writeValueAsString(prefixListDao.getPrefixListDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_PrefixListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPrefixListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("prefixListDictionaryDatas", mapper.writeValueAsString(prefixListDao.getPREFIXLISTData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_prefixList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PREFIXLIST prefixList = (PREFIXLIST)mapper.readValue(root.get("prefixListDictionaryData").toString(), PREFIXLIST.class);
			if(prefixList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(prefixList.getPrefixListName(), "prefixListName", PREFIXLIST.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					prefixListDao.Save(prefixList);
				}		
			}else{
				prefixListDao.update(prefixList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.prefixListDao.getPREFIXLISTData());
			}
			JSONObject j = new JSONObject("{prefixListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_PrefixList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PREFIXLIST prefixList = (PREFIXLIST)mapper.readValue(root.get("data").toString(), PREFIXLIST.class);
			prefixListDao.delete(prefixList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.prefixListDao.getPREFIXLISTData());
			JSONObject j = new JSONObject("{prefixListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/fw_dictionary/validate_prefixList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView validatePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PREFIXLIST prefixList = (PREFIXLIST)mapper.readValue(root.get("prefixListDictionaryData").toString(), PREFIXLIST.class);
			String responseValidation = "success";
			 try{
				 CIDR.newCIDR(prefixList.getPrefixListValue());
			 }catch(UnknownHostException e){
				 responseValidation = "error";
				 //AdminNotification.warn("IP not according to CIDR notation");
			 }		
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			JSONObject j = new JSONObject("{result: " + responseValidation + "}");
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
	
	@RequestMapping(value={"/get_PortListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPortListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("portListDictionaryDatas", mapper.writeValueAsString(portListDao.getPortListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_portName.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePortListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PortList portList = (PortList)mapper.readValue(root.get("portListDictionaryData").toString(), PortList.class);
			if(portList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(portList.getPortName(), "portName", PortList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					portListDao.Save(portList);
				}
			}else{
				portListDao.update(portList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.portListDao.getPortListData());
			}
			JSONObject j = new JSONObject("{portListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_PortList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePortListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PortList portList = (PortList)mapper.readValue(root.get("data").toString(), PortList.class);
			portListDao.delete(portList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.portListDao.getPortListData());
			JSONObject j = new JSONObject("{portListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_ProtocolListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getProtocolListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("protocolListDictionaryDatas", mapper.writeValueAsString(protocolListDao.getProtocolListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ProtocolListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getProtocolListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("protocolListDictionaryDatas", mapper.writeValueAsString(protocolListDao.getProtocolListDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_protocolList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ProtocolList protocolList = (ProtocolList)mapper.readValue(root.get("protocolListDictionaryData").toString(), ProtocolList.class);
			if(protocolList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(protocolList.getProtocolName(), "protocolName", ProtocolList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					protocolListDao.Save(protocolList);
				}
			}else{
				protocolListDao.update(protocolList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.protocolListDao.getProtocolListData());
			}
			JSONObject j = new JSONObject("{protocolListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_protocol.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ProtocolList protocolList = (ProtocolList)mapper.readValue(root.get("data").toString(), ProtocolList.class);
			protocolListDao.delete(protocolList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.protocolListDao.getProtocolListData());
			JSONObject j = new JSONObject("{protocolListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_AddressGroupDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAddressGroupDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("addressGroupDictionaryDatas", mapper.writeValueAsString(addressGroupDao.getAddressGroupDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_AddressGroupData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAddressGroupDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("addressGroupDictionaryDatas", mapper.writeValueAsString(addressGroupDao.getAddressGroupData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_addressGroup.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			AddressGroup addressGroup = (AddressGroup)mapper.readValue(root.get("addressGroupDictionaryData").toString(), AddressGroup.class);
			GridData gridData = (GridData)mapper.readValue(root.get("addressGroupDictionaryData").toString(), GridData.class);
			if(!addressGroup.getGroupName().startsWith("Group_")){
				String groupName = "Group_"+addressGroup.getGroupName();
				addressGroup.setGroupName(groupName);
			}
			String userValue = "";
			int counter = 0;
			if(gridData.getAttributes().size() > 0){
				for(Object attribute : gridData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						if(counter>0){
							userValue = userValue + ",";
						}
						userValue = userValue + key ;
						counter ++;
					}
				}
			}
			addressGroup.setServiceList(userValue);
			if(addressGroup.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(addressGroup.getGroupName(), "name", AddressGroup.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					addressGroupDao.Save(addressGroup);
				}
			}else{
				addressGroupDao.update(addressGroup); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.addressGroupDao.getAddressGroupData());
			}
			JSONObject j = new JSONObject("{addressGroupDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_AddressGroup.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			AddressGroup addressGroup = (AddressGroup)mapper.readValue(root.get("data").toString(), AddressGroup.class);
			addressGroupDao.delete(addressGroup);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.addressGroupDao.getAddressGroupData());
			JSONObject j = new JSONObject("{addressGroupDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_ActionListDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getActionListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("actionListDictionaryDatas", mapper.writeValueAsString(actionListDao.getActionListDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ActionListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getActionListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("actionListDictionaryDatas", mapper.writeValueAsString(actionListDao.getActionListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_ActionList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ActionList actionList = (ActionList)mapper.readValue(root.get("actionListDictionaryData").toString(), ActionList.class);
			if(actionList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(actionList.getActionName(), "actionName", ActionList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					actionListDao.Save(actionList);
				}
			}else{
				actionListDao.update(actionList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.actionListDao.getActionListData());
			}
			JSONObject j = new JSONObject("{actionListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_ActionList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ActionList actionList = (ActionList)mapper.readValue(root.get("data").toString(), ActionList.class);
			actionListDao.delete(actionList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.actionListDao.getActionListData());
			JSONObject j = new JSONObject("{actionListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_ServiceGroupData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getServiceGroupDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceGroupDictionaryDatas", mapper.writeValueAsString(serviceGroupDao.getGroupServiceListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ServiceGroupDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getServiceGroupDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceGroupDictionaryDatas", mapper.writeValueAsString(serviceGroupDao.getGroupServiceDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_serviceGroup.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupServiceList groupServiceList = (GroupServiceList)mapper.readValue(root.get("serviceGroupDictionaryData").toString(), GroupServiceList.class);
			GridData gridData = (GridData)mapper.readValue(root.get("serviceGroupDictionaryData").toString(), GridData.class);
			if(!groupServiceList.getGroupName().startsWith("Group_")){
				String groupName = "Group_"+groupServiceList.getGroupName();
				groupServiceList.setGroupName(groupName);
			}
			String userValue = "";
			int counter = 0;
			if(gridData.getAttributes().size() > 0){
				for(Object attribute : gridData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						if(counter>0){
							userValue = userValue + ",";
						}
						userValue = userValue + key ;
						counter ++;
					}
				}
			}
			groupServiceList.setServiceList(userValue);
			if(groupServiceList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(groupServiceList.getGroupName(), "name", GroupServiceList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					serviceGroupDao.Save(groupServiceList);
				}
			}else{
				serviceGroupDao.update(groupServiceList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.serviceGroupDao.getGroupServiceListData());
			}
			JSONObject j = new JSONObject("{serviceGroupDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_serviceGroup.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupServiceList groupServiceList = (GroupServiceList)mapper.readValue(root.get("data").toString(), GroupServiceList.class);
			serviceGroupDao.delete(groupServiceList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.serviceGroupDao.getGroupServiceListData());
			JSONObject j = new JSONObject("{serviceGroupDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_SecurityZoneDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getSecurityZoneDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("securityZoneDictionaryDatas", mapper.writeValueAsString(securityZoneDao.getSecurityZoneDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_SecurityZoneData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getSecurityZoneDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("securityZoneDictionaryDatas", mapper.writeValueAsString(securityZoneDao.getSecurityZoneData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_securityZone.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SecurityZone securityZone = (SecurityZone)mapper.readValue(root.get("securityZoneDictionaryData").toString(), SecurityZone.class);
			if(securityZone.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(securityZone.getZoneName(), "zoneName", SecurityZone.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					securityZoneDao.Save(securityZone);
				}			
			}else{
				securityZoneDao.update(securityZone); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.securityZoneDao.getSecurityZoneData());
			}
			JSONObject j = new JSONObject("{securityZoneDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_securityZone.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SecurityZone securityZone = (SecurityZone)mapper.readValue(root.get("data").toString(), SecurityZone.class);
			securityZoneDao.delete(securityZone);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.securityZoneDao.getSecurityZoneData());
			JSONObject j = new JSONObject("{securityZoneDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	
	@RequestMapping(value={"/get_ServiceListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getServiceListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceListDictionaryDatas", mapper.writeValueAsString(serviceListDao.getServiceListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ServiceListDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getServiceListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceListDictionaryDatas", mapper.writeValueAsString(serviceListDao.getServiceListDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_serviceList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ServiceList serviceList = (ServiceList)mapper.readValue(root.get("serviceListDictionaryData").toString(), ServiceList.class);
			GridData serviceListGridData = (GridData)mapper.readValue(root.get("serviceListDictionaryData").toString(), GridData.class);
			String tcpValue = "";
			int counter = 0;
			if(serviceListGridData.getTransportProtocols().size() > 0){
				for(Object attribute : serviceListGridData.getTransportProtocols()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						if(counter>0){
							tcpValue = tcpValue + ",";
						}
						tcpValue = tcpValue + key ;
						counter ++;
					}
				}
			}
			serviceList.setServiceTransProtocol(tcpValue);
			String appValue = "";
			int counter1 = 0;
			if(serviceListGridData.getAppProtocols().size() > 0){
				for(Object attribute : serviceListGridData.getAppProtocols()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						if(counter1>0){
							appValue = appValue + ",";
						}
						appValue = appValue + key ;
						counter1 ++;
					}
				}
			}
			serviceList.setServiceAppProtocol(appValue);
			serviceList.setServiceType("SERVICE");
			if(serviceList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(serviceList.getServiceName(), "serviceName", ServiceList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					serviceListDao.Save(serviceList);
				}
				
			}else{
				serviceListDao.update(serviceList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.serviceListDao.getServiceListData());
			} 
			JSONObject j = new JSONObject("{serviceListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_serviceList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ServiceList serviceList = (ServiceList)mapper.readValue(root.get("data").toString(), ServiceList.class);
			serviceListDao.delete(serviceList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.serviceListDao.getServiceListData());
			JSONObject j = new JSONObject("{serviceListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_ZoneData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getZoneDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("zoneDictionaryDatas", mapper.writeValueAsString(zoneDao.getZoneData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ZoneDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getZoneDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("zoneDictionaryDatas", mapper.writeValueAsString(zoneDao.getZoneDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_zoneName.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Zone zone = (Zone)mapper.readValue(root.get("zoneDictionaryData").toString(), Zone.class);
			if(zone.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(zone.getZoneName(), "zoneName", Zone.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					zoneDao.Save(zone);
				}	
			}else{
				zoneDao.update(zone); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.zoneDao.getZoneData());
			}
			JSONObject j = new JSONObject("{zoneDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_zone.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Zone zone = (Zone)mapper.readValue(root.get("data").toString(), Zone.class);
			zoneDao.delete(zone);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.zoneDao.getZoneData());
			JSONObject j = new JSONObject("{zoneDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	
	@RequestMapping(value={"/get_TermListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTermListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("termListDictionaryDatas", mapper.writeValueAsString(termListDao.getTermListDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_TermListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTermListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("termListDictionaryDatas", mapper.writeValueAsString(termListDao.getTermListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_termList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			TermList termList = (TermList)mapper.readValue(root.get("termListDictionaryData").toString(), TermList.class);
			TermListData termListDatas = (TermListData)mapper.readValue(root.get("termListDictionaryData").toString(), TermListData.class);
			String userId = root.get("loginId").textValue();
			String fromZoneValue = "";
			int counter = 0;
			if(termListDatas.getFromZoneDatas().size() > 0){
				for(Object fromZone : termListDatas.getFromZoneDatas()){
					if(fromZone instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) fromZone).get("option").toString();
						if(counter>0){
							fromZoneValue = fromZoneValue + ",";
						}
						fromZoneValue = fromZoneValue + key ;
						counter ++;
					}
				}
			}
			termList.setFromZones(fromZoneValue);
			
			String toZoneValue = "";
			int toZonecounter = 0;
			if(termListDatas.getToZoneDatas().size() > 0){
				for(Object toZone : termListDatas.getToZoneDatas()){
					if(toZone instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) toZone).get("option").toString();
						if(toZonecounter>0){
							toZoneValue = toZoneValue + ",";
						}
						toZoneValue = toZoneValue + key ;
						toZonecounter ++;
					}
				}
			}
			termList.setToZones(toZoneValue);
			
			String srcListValues = "";
			int srcListcounter = 0;
			if(termListDatas.getSourceListDatas().size() > 0){
				for(Object srcList : termListDatas.getSourceListDatas()){
					if(srcList instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) srcList).get("option").toString();
						if(srcListcounter>0){
							srcListValues = srcListValues + ",";
						}
						srcListValues = srcListValues + key ;
						srcListcounter ++;
					}
				}
			}
			termList.setSrcIPList(srcListValues);
			
			String desListValues = "";
			int destListcounter = 0;
			if(termListDatas.getDestinationListDatas().size() > 0){
				for(Object desList : termListDatas.getDestinationListDatas()){
					if(desList instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) desList).get("option").toString();
						if(destListcounter>0){
							desListValues = desListValues + ",";
						}
						desListValues = desListValues + key ;
						destListcounter ++;
					}
				}
			}
			termList.setDestIPList(desListValues);
			
			String srcSerValue = "";
			int srcSercounter = 0;
			if(termListDatas.getSourceServiceDatas().size() > 0){
				for(Object srcSrc : termListDatas.getSourceServiceDatas()){
					if(srcSrc instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) srcSrc).get("option").toString();
						if(srcSercounter>0){
							srcSerValue = srcSerValue + ",";
						}
						srcSerValue = srcSerValue + key ;
						srcSercounter ++;
					}
				}
			}
			termList.setSrcPortList(srcSerValue);
			
			String desSrcValue = "";
			int desSrccounter = 0;
			if(termListDatas.getDestinationServiceDatas().size() > 0){
				for(Object desSrc : termListDatas.getDestinationServiceDatas()){
					if(desSrc instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) desSrc).get("option").toString();
						if(desSrccounter>0){
							desSrcValue = desSrcValue + ",";
						}
						desSrcValue = desSrcValue + key ;
						desSrccounter ++;
					}
				}
			}
			termList.setDestPortList(desSrcValue);
			
			String actionValue = "";
			int actioncounter = 0;
			if(termListDatas.getActionListDatas().size() > 0){
				for(Object actionList : termListDatas.getActionListDatas()){
					if(actionList instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) actionList).get("option").toString();
						if(actioncounter>0){
							actionValue = actionValue + ",";
						}
						actionValue = actionValue + key ;
						actioncounter ++;
					}
				}
			}
			termList.setAction(actionValue);
			
			if(termList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(termList.getTermName(), "termName", TermList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					termList.setUserCreatedBy(this.getUserInfo(userId));
					termList.setUserModifiedBy(this.getUserInfo(userId));
					termListDao.Save(termList);
				}
			}else{
				termList.setUserModifiedBy(this.getUserInfo(userId));
				termListDao.update(termList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.termListDao.getTermListData());
			}
			JSONObject j = new JSONObject("{termListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_termList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			TermList termList = (TermList)mapper.readValue(root.get("data").toString(), TermList.class);
			termListDao.delete(termList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.termListDao.getTermListData());
			JSONObject j = new JSONObject("{termListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
	//ParentList Dictionary Data
	@RequestMapping(value={"/get_FWDictionaryListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getFWDictListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwDictListDictionaryDatas", mapper.writeValueAsString(fwDictionaryListDao.getFWDictionaryListDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_FWDictionaryListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getFWDictionaryListEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwDictListDictionaryDatas", mapper.writeValueAsString(fwDictionaryListDao.getFWDictionaryListData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/fw_dictionary/save_FWDictionaryList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveFWDictionaryList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			FirewallDictionaryList fwDictList = (FirewallDictionaryList)mapper.readValue(root.get("fwDictListDictionaryData").toString(), FirewallDictionaryList.class);
			GridData gridData = (GridData)mapper.readValue(root.get("fwDictListDictionaryData").toString(), GridData.class);
			String userSLValue = "";
			int slcounter = 0;
			if(gridData.getAttributes().size() > 0){
				for(Object attribute : gridData.getAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						if(slcounter>0){
							userSLValue = userSLValue + ",";
						}
						userSLValue = userSLValue + key ;
						slcounter ++;
					}
				}
			}
			fwDictList.setServiceList(userSLValue);
			String userALValue = "";
			int alcounter = 0;
			if(gridData.getAlAttributes().size() > 0){
				for(Object attribute : gridData.getAlAttributes()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						if(alcounter>0){
							userALValue = userALValue + ",";
						}
						userALValue = userALValue + key ;
						alcounter ++;
					}
				}
			}
			fwDictList.setAddressList(userALValue);
			if(fwDictList.getId() == 0){
				CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
				List<Object> duplicateData =  entry.CheckDuplicateEntry(fwDictList.getParentItemName(), "parentItemName", FirewallDictionaryList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					fwDictionaryListDao.Save(fwDictList);
				}
			}else{
				fwDictionaryListDao.update(fwDictList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.fwDictionaryListDao.getFWDictionaryListData());
			}
			JSONObject j = new JSONObject("{fwDictListDictionaryDatas: " + responseString + "}");

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

	@RequestMapping(value={"/fw_dictionary/remove_FWDictionaryList.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeFWDictionaryListy(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			FirewallDictionaryList fwDictList = (FirewallDictionaryList)mapper.readValue(root.get("data").toString(), FirewallDictionaryList.class);
			fwDictionaryListDao.delete(fwDictList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(this.fwDictionaryListDao.getFWDictionaryListData());
			JSONObject j = new JSONObject("{fwDictListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			System.out.println(e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
}

class AGGridData{
	private ArrayList<Object> attributes;

	public ArrayList<Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Object> attributes) {
		this.attributes = attributes;
	}
}

class TermListData{
	private ArrayList<Object> fromZoneDatas;
	private ArrayList<Object> toZoneDatas;
	private ArrayList<Object> sourceListDatas;
	private ArrayList<Object> destinationListDatas;
	private ArrayList<Object> sourceServiceDatas;
	private ArrayList<Object> destinationServiceDatas;
	private ArrayList<Object> actionListDatas;
	public ArrayList<Object> getFromZoneDatas() {
		return fromZoneDatas;
	}
	public void setFromZoneDatas(ArrayList<Object> fromZoneDatas) {
		this.fromZoneDatas = fromZoneDatas;
	}
	public ArrayList<Object> getToZoneDatas() {
		return toZoneDatas;
	}
	public void setToZoneDatas(ArrayList<Object> toZoneDatas) {
		this.toZoneDatas = toZoneDatas;
	}
	public ArrayList<Object> getSourceListDatas() {
		return sourceListDatas;
	}
	public void setSourceListDatas(ArrayList<Object> sourceListDatas) {
		this.sourceListDatas = sourceListDatas;
	}
	public ArrayList<Object> getDestinationListDatas() {
		return destinationListDatas;
	}
	public void setDestinationListDatas(ArrayList<Object> destinationListDatas) {
		this.destinationListDatas = destinationListDatas;
	}
	public ArrayList<Object> getSourceServiceDatas() {
		return sourceServiceDatas;
	}
	public void setSourceServiceDatas(ArrayList<Object> sourceServiceDatas) {
		this.sourceServiceDatas = sourceServiceDatas;
	}
	public ArrayList<Object> getDestinationServiceDatas() {
		return destinationServiceDatas;
	}
	public void setDestinationServiceDatas(ArrayList<Object> destinationServiceDatas) {
		this.destinationServiceDatas = destinationServiceDatas;
	}
	public ArrayList<Object> getActionListDatas() {
		return actionListDatas;
	}
	public void setActionListDatas(ArrayList<Object> actionListDatas) {
		this.actionListDatas = actionListDatas;
	}
}
