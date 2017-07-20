/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.controller;

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
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.adapters.GridData;
import org.onap.policy.pap.xacml.rest.util.JsonMessage;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionList;
import org.onap.policy.rest.jpa.AddressGroup;
import org.onap.policy.rest.jpa.FWTag;
import org.onap.policy.rest.jpa.FWTagPicker;
import org.onap.policy.rest.jpa.FirewallDictionaryList;
import org.onap.policy.rest.jpa.GroupServiceList;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.PortList;
import org.onap.policy.rest.jpa.ProtocolList;
import org.onap.policy.rest.jpa.SecurityZone;
import org.onap.policy.rest.jpa.ServiceList;
import org.onap.policy.rest.jpa.TermList;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.Zone;
import org.onap.policy.xacml.api.XACMLErrorConstants;
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

	private static final Logger LOGGER  = FlexLogger.getLogger(FirewallDictionaryController.class);

	private static CommonClassDao commonClassDao;
	
	@Autowired
	public FirewallDictionaryController(CommonClassDao commonClassDao){
		FirewallDictionaryController.commonClassDao = commonClassDao;
	}
	
	public FirewallDictionaryController(){}	

	public UserInfo getUserInfo(String loginId){
		UserInfo name = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", loginId);
		return name;	
	}


	@RequestMapping(value={"/get_PrefixListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPrefixListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("prefixListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(PrefixList.class, "prefixListName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_PrefixListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPrefixListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("prefixListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PrefixList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_prefixList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PrefixList prefixList;
			if (fromAPI) {
				prefixList = (PrefixList)mapper.readValue(root.get("dictionaryFields").toString(), PrefixList.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(prefixList.getPrefixListName(), "prefixListName", PrefixList.class);
					int id = 0;
					PrefixList data = (PrefixList) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						prefixList.setId(1);
					} else {
						prefixList.setId(id);
					}
				}
			} else {
				prefixList = (PrefixList)mapper.readValue(root.get("prefixListDictionaryData").toString(), PrefixList.class);
			}
			if(prefixList.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(prefixList.getPrefixListName(), "prefixListName", PrefixList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(prefixList);
				}		
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(prefixList); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PrefixList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{prefixListDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_PrefixList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PrefixList prefixList = (PrefixList)mapper.readValue(root.get("data").toString(), PrefixList.class);
			commonClassDao.delete(prefixList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(PrefixList.class));
			JSONObject j = new JSONObject("{prefixListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/validate_prefixList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView validatePrefixListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PrefixList prefixList = (PrefixList)mapper.readValue(root.get("prefixListDictionaryData").toString(), PrefixList.class);
			String responseValidation = "success";
			try{
				CIDR.newCIDR(prefixList.getPrefixListValue());
			}catch(UnknownHostException e){
				responseValidation = "error";
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
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("portListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(PortList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_portName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView savePortListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PortList portList = (PortList)mapper.readValue(root.get("portListDictionaryData").toString(), PortList.class);
			if(portList.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(portList.getPortName(), "portName", PortList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(portList);
				}
			}else{
				commonClassDao.update(portList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(PortList.class));
			}
			JSONObject j = new JSONObject("{portListDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_PortList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removePortListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PortList portList = (PortList)mapper.readValue(root.get("data").toString(), PortList.class);
			commonClassDao.delete(portList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(PortList.class));
			JSONObject j = new JSONObject("{portListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			model.put("protocolListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(ProtocolList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/get_ProtocolListDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getProtocolListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("protocolListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(ProtocolList.class, "protocolName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_protocolList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ProtocolList protocolList;
			if (fromAPI) {
				protocolList = (ProtocolList)mapper.readValue(root.get("dictionaryFields").toString(), ProtocolList.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(protocolList.getProtocolName(), "protocolName", ProtocolList.class);
					int id = 0;
					ProtocolList data = (ProtocolList) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						protocolList.setId(1);
					} else {
						protocolList.setId(id);
					}
				}
			} else {
				protocolList = (ProtocolList)mapper.readValue(root.get("protocolListDictionaryData").toString(), ProtocolList.class);
			}
			if(protocolList.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(protocolList.getProtocolName(), "protocolName", ProtocolList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(protocolList);
				}
			}else{
				if(!isFakeUpdate){
					commonClassDao.update(protocolList);
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(ProtocolList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{protocolListDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_protocol"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeProtocolListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ProtocolList protocolList = (ProtocolList)mapper.readValue(root.get("data").toString(), ProtocolList.class);
			commonClassDao.delete(protocolList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(ProtocolList.class));
			JSONObject j = new JSONObject("{protocolListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("addressGroupDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(AddressGroup.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_AddressGroupData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getAddressGroupDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("addressGroupDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(AddressGroup.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_addressGroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			AddressGroup addressGroup;
			GridData gridData;
			if (fromAPI) {
				addressGroup = (AddressGroup)mapper.readValue(root.get("dictionaryFields").toString(), AddressGroup.class);
				gridData = (GridData)mapper.readValue(root.get("dictionaryFields").toString(), GridData.class);

				if(!addressGroup.getGroupName().startsWith("Group_")){
					String groupName = "Group_"+addressGroup.getGroupName();
					addressGroup.setGroupName(groupName);
				}

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(addressGroup.getGroupName(), "name", AddressGroup.class);
					int id = 0;
					AddressGroup data = (AddressGroup) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						addressGroup.setId(1);
					} else {
						addressGroup.setId(id);
					}            
				}
			} else {
				addressGroup = (AddressGroup)mapper.readValue(root.get("addressGroupDictionaryData").toString(), AddressGroup.class);
				gridData = (GridData)mapper.readValue(root.get("addressGroupDictionaryData").toString(), GridData.class);
				if(!addressGroup.getGroupName().startsWith("Group_")){
					String groupName = "Group_"+addressGroup.getGroupName();
					addressGroup.setGroupName(groupName);
				}
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
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(addressGroup.getGroupName(), "name", AddressGroup.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(addressGroup);
				}
			}else{
				if (!isFakeUpdate) {
					commonClassDao.update(addressGroup); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(AddressGroup.class));
			}
			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{addressGroupDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_AddressGroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeAddressGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			AddressGroup addressGroup = (AddressGroup)mapper.readValue(root.get("data").toString(), AddressGroup.class);
			commonClassDao.delete(addressGroup);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(AddressGroup.class));
			JSONObject j = new JSONObject("{addressGroupDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			List<Object> list = commonClassDao.getData(ActionList.class);
			List<String> dictList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++){
				ActionList dict = (ActionList) list.get(i);
				dictList.add(dict.getActionName());
			}
			model.put("actionListDictionaryDatas", mapper.writeValueAsString(dictList));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_ActionListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getActionListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("actionListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(ActionList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_ActionList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ActionList actionList;
			if (fromAPI) {
				actionList = (ActionList)mapper.readValue(root.get("dictionaryFields").toString(), ActionList.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(actionList.getActionName(), "actionName", ActionList.class);
					int id = 0;
					ActionList data = (ActionList) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						actionList.setId(1);
					} else {
						actionList.setId(id);
					}  
				}
			} else {
				actionList = (ActionList)mapper.readValue(root.get("actionListDictionaryData").toString(), ActionList.class);
			}
			if(actionList.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(actionList.getActionName(), "actionName", ActionList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(actionList);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(actionList);
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(ActionList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{actionListDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_ActionList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeActionListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ActionList actionList = (ActionList)mapper.readValue(root.get("data").toString(), ActionList.class);
			commonClassDao.delete(actionList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(ActionList.class));
			JSONObject j = new JSONObject("{actionListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceGroupDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(GroupServiceList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/get_ServiceGroupDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getServiceGroupDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceGroupDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(GroupServiceList.class, "name")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_serviceGroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupServiceList groupServiceList;
			GridData gridData;
			if (fromAPI) {
				groupServiceList = (GroupServiceList)mapper.readValue(root.get("dictionaryFields").toString(), GroupServiceList.class);
				gridData = (GridData)mapper.readValue(root.get("dictionaryFields").toString(), GridData.class);

				if(!groupServiceList.getGroupName().startsWith("Group_")){
					String groupName = "Group_"+groupServiceList.getGroupName();
					groupServiceList.setGroupName(groupName);
				}
				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(groupServiceList.getGroupName(), "name", GroupServiceList.class);
					int id = 0;
					GroupServiceList data = (GroupServiceList) duplicateData.get(0);
					id = data.getId();

					if(id==0){
						isFakeUpdate=true;
						groupServiceList.setId(1);
					} else {
						groupServiceList.setId(id);
					}   
				}
			} else {
				groupServiceList = (GroupServiceList)mapper.readValue(root.get("serviceGroupDictionaryData").toString(), GroupServiceList.class);
				gridData = (GridData)mapper.readValue(root.get("serviceGroupDictionaryData").toString(), GridData.class);
			}
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
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(groupServiceList.getGroupName(), "name", GroupServiceList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(groupServiceList);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(groupServiceList); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(GroupServiceList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{serviceGroupDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_serviceGroup"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeServiceGroupDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			GroupServiceList groupServiceList = (GroupServiceList)mapper.readValue(root.get("data").toString(), GroupServiceList.class);
			commonClassDao.delete(groupServiceList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(GroupServiceList.class));
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("securityZoneDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(SecurityZone.class, "zoneName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_SecurityZoneData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getSecurityZoneDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("securityZoneDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(SecurityZone.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_securityZone"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SecurityZone securityZone;
			if (fromAPI) {
				securityZone = (SecurityZone)mapper.readValue(root.get("dictionaryFields").toString(), SecurityZone.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(securityZone.getZoneName(), "zoneName", SecurityZone.class);
					int id = 0;
					SecurityZone data = (SecurityZone) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						securityZone.setId(1);
					} else {
						securityZone.setId(id);
					}               
				}
			} else {
				securityZone = (SecurityZone)mapper.readValue(root.get("securityZoneDictionaryData").toString(), SecurityZone.class);
			}
			if(securityZone.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(securityZone.getZoneName(), "zoneName", SecurityZone.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(securityZone);
				}			
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(securityZone); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(SecurityZone.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}   
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{securityZoneDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}

		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_securityZone"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeSecurityZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			SecurityZone securityZone = (SecurityZone)mapper.readValue(root.get("data").toString(), SecurityZone.class);
			commonClassDao.delete(securityZone);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(SecurityZone.class));
			JSONObject j = new JSONObject("{securityZoneDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(ServiceList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/get_ServiceListDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getServiceListDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("serviceListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(ServiceList.class, "serviceName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_serviceList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ServiceList serviceList;
			GridData serviceListGridData;
			if (fromAPI) {
				serviceList = (ServiceList)mapper.readValue(root.get("dictionaryFields").toString(), ServiceList.class);
				serviceListGridData = (GridData)mapper.readValue(root.get("dictionaryFields").toString(), GridData.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(serviceList.getServiceName(), "serviceName", ServiceList.class);
					int id = 0;
					ServiceList data = (ServiceList) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						serviceList.setId(1);
					} else {
						serviceList.setId(id);
					}
				}
			}else{
				serviceList = (ServiceList)mapper.readValue(root.get("serviceListDictionaryData").toString(), ServiceList.class);
				serviceListGridData = (GridData)mapper.readValue(root.get("serviceListDictionaryData").toString(), GridData.class);
			}
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
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(serviceList.getServiceName(), "serviceName", ServiceList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(serviceList);
				}
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(serviceList); 
				}
			} 

			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(ServiceList.class));
			} 
			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{serviceListDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}

		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_serviceList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeServiceListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			ServiceList serviceList = (ServiceList)mapper.readValue(root.get("data").toString(), ServiceList.class);
			commonClassDao.delete(serviceList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(ServiceList.class));
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("zoneDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(Zone.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error("Exception Occured"+e);
		}
	}

	@RequestMapping(value={"/get_ZoneDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getZoneDictionaryEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("zoneDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(Zone.class, "zoneName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_zoneName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Zone zone;
			if (fromAPI) {
				zone = (Zone)mapper.readValue(root.get("dictionaryFields").toString(), Zone.class);

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData = commonClassDao.checkDuplicateEntry(zone.getZoneName(), "zoneName", Zone.class);
					int id = 0;
					Zone data = (Zone) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						zone.setId(1);
					} else {
						zone.setId(id);
					}
				}
			} else {
				zone = (Zone)mapper.readValue(root.get("zoneDictionaryData").toString(), Zone.class);
			}
			if(zone.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(zone.getZoneName(), "zoneName", Zone.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(zone);
				}	
			}else{
				if(!isFakeUpdate) {
					commonClassDao.update(zone); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(Zone.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{zoneDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_zone"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeZoneDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Zone zone = (Zone)mapper.readValue(root.get("data").toString(), Zone.class);
			commonClassDao.delete(zone);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(Zone.class));
			JSONObject j = new JSONObject("{zoneDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			model.put("termListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(TermList.class, "termName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_TermListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTermListDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("termListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(TermList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_termList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean isFakeUpdate = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			TermList termList;
			TermListData termListDatas;
			String userId = null;
			if (fromAPI) {
				termList = (TermList)mapper.readValue(root.get("dictionaryFields").toString(), TermList.class);
				termListDatas = (TermListData)mapper.readValue(root.get("dictionaryFields").toString(), TermListData.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(termList.getTermName(), "termName", TermList.class);
					int id = 0;
					TermList data = (TermList) duplicateData.get(0);
					id = data.getId();
					if(id==0){
						isFakeUpdate=true;
						termList.setId(1);
					} else {
						termList.setId(id);
					}                 
					termList.setUserCreatedBy(this.getUserInfo(userId));
				}
			} else {
				termList = (TermList)mapper.readValue(root.get("termListDictionaryData").toString(), TermList.class);
				termListDatas = (TermListData)mapper.readValue(root.get("termListDictionaryData").toString(), TermListData.class);
				userId = root.get("userid").textValue();
			}
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
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(termList.getTermName(), "termName", TermList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					termList.setUserCreatedBy(this.getUserInfo(userId));
					termList.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(termList);
				}
			}else{
				if(!isFakeUpdate) {
					termList.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.update(termList); 
				}
			} 
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(TermList.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					if(isFakeUpdate){
						responseString = "Exists";
					} else {
						responseString = "Success";
					}
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{termListDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_termList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeTermListDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			TermList termList = (TermList)mapper.readValue(root.get("data").toString(), TermList.class);
			commonClassDao.delete(termList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(TermList.class));
			JSONObject j = new JSONObject("{termListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
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
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwDictListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(FirewallDictionaryList.class, "parentItemName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_FWDictionaryListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getFWDictionaryListEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwDictListDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(FirewallDictionaryList.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.addHeader("successMapKey", "success"); 
			response.addHeader("operation", "getDictionary");
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);                             
			response.addHeader("error", "dictionaryDBQuery");
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_FWDictionaryList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
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
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwDictList.getParentItemName(), "parentItemName", FirewallDictionaryList.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					commonClassDao.save(fwDictList);
				}
			}else{
				commonClassDao.update(fwDictList); 
			} 
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(commonClassDao.getData(FirewallDictionaryList.class));
			}
			JSONObject j = new JSONObject("{fwDictListDictionaryDatas: " + responseString + "}");

			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_FWDictionaryList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeFWDictionaryListy(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			FirewallDictionaryList fwDictList = (FirewallDictionaryList)mapper.readValue(root.get("data").toString(), FirewallDictionaryList.class);
			commonClassDao.delete(fwDictList);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(FirewallDictionaryList.class));
			JSONObject j = new JSONObject("{fwDictListDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}


	@RequestMapping(value={"/get_TagPickerNameByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTagPickerNameEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwTagPickerDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(FWTagPicker.class, "tagPickerName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_TagPickerListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTagPickerDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwTagPickerDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(FWTagPicker.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_fwTagPicker"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveFirewallTagPickerDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());


			FWTagPicker fwTagPicker;
			TagGridValues data;
			String userId = null;
			if (fromAPI) {
				fwTagPicker = (FWTagPicker)mapper.readValue(root.get("fwTagPickerDictionaryData").toString(), FWTagPicker.class);
				data = (TagGridValues)mapper.readValue(root.get("fwTagPickerDictionaryData").toString(), TagGridValues.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwTagPicker.getTagPickerName(), "tagPickerName", FWTagPicker.class);
					int id = 0;
					FWTagPicker dbdata = (FWTagPicker) duplicateData.get(0);
					id = dbdata.getId();
					fwTagPicker.setId(id);
					fwTagPicker.setUserCreatedBy(this.getUserInfo(userId));
				}
			} else {

				fwTagPicker = (FWTagPicker)mapper.readValue(root.get("fwTagPickerDictionaryData").toString(), FWTagPicker.class);
				data = (TagGridValues)mapper.readValue(root.get("fwTagPickerDictionaryData").toString(), TagGridValues.class);
				userId = root.get("userid").textValue();
			}

			String header = "";
			int counter = 0;
			if(data.getTags().size() > 0){
				for(Object attribute : data.getTags()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
						String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
						if(counter>0){
							header = header + "#";
						}
						header = header + key + ":";
						header = header + value;
						counter ++;
					}
				}
			}
			fwTagPicker.setTagValues(header);
			if(fwTagPicker.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwTagPicker.getTagPickerName(), "tagPickerName", FWTagPicker.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					fwTagPicker.setUserCreatedBy(this.getUserInfo(userId));
					fwTagPicker.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(fwTagPicker);
				}
			}else{
				fwTagPicker.setUserModifiedBy(this.getUserInfo(userId));
				commonClassDao.update(fwTagPicker); 
			} 

			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString =  mapper.writeValueAsString(commonClassDao.getData(FWTagPicker.class));
			}

			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					responseString = "Success";
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{fwTagPickerDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_tagPicker"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeFirewallTagPickerDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			FWTagPicker fwTagPicker = (FWTagPicker)mapper.readValue(root.get("data").toString(), FWTagPicker.class);
			commonClassDao.delete(fwTagPicker);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(commonClassDao.getData(FWTagPicker.class));
			JSONObject j = new JSONObject("{fwTagPickerDictionaryDatas: " + responseString + "}");
			out.write(j.toString());
			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}


	@RequestMapping(value={"/get_TagNameByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTagNameEntityDataByName(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwTagDictionaryDatas", mapper.writeValueAsString(commonClassDao.getDataByColumn(FWTag.class, "fwTagName")));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/get_TagListData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getTagDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("fwTagDictionaryDatas", mapper.writeValueAsString(commonClassDao.getData(FWTag.class)));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
	}

	@RequestMapping(value={"/fw_dictionary/save_fwTag"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView saveFirewallTagDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			boolean duplicateflag = false;
			boolean fromAPI = false;
			if (request.getParameter("apiflag")!=null && request.getParameter("apiflag").equalsIgnoreCase("api")) {
				fromAPI = true;
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());


			FWTag fwTag;
			TagGridValues tagGridValues;
			String userId = null;
			if (fromAPI) {
				fwTag = (FWTag)mapper.readValue(root.get("fwTagDictionaryDatas").toString(), FWTag.class);
				tagGridValues = (TagGridValues)mapper.readValue(root.get("fwTagDictionaryDatas").toString(), TagGridValues.class);
				userId = "API";

				//check if update operation or create, get id for data to be updated and update attributeData
				if (request.getParameter("operation").equals("update")) {
					List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwTag.getFwTagName(), "tagName", FWTag.class);
					int id = 0;
					FWTag data = (FWTag) duplicateData.get(0);
					id = data.getId();
					fwTag.setId(id);
					fwTag.setUserCreatedBy(this.getUserInfo(userId));

				}
			} else {
				fwTag = (FWTag)mapper.readValue(root.get("fwTagDictionaryData").toString(), FWTag.class);
				tagGridValues = (TagGridValues)mapper.readValue(root.get("fwTagDictionaryData").toString(), TagGridValues.class);
				userId = root.get("userid").textValue();
			}

			String userValue = "";
			int counter = 0;
			if(tagGridValues.getTags().size() > 0){
				for(Object attribute : tagGridValues.getTags()){
					if(attribute instanceof LinkedHashMap<?, ?>){
						String key = ((LinkedHashMap<?, ?>) attribute).get("tags").toString();
						if(counter>0){
							userValue = userValue + ",";
						}
						userValue = userValue + key ;
						counter ++;
					}
				}
			}
			fwTag.setTagValues(userValue);
			if(fwTag.getId() == 0){
				List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(fwTag.getFwTagName(), "fwTagName", FWTag.class);
				if(!duplicateData.isEmpty()){
					duplicateflag = true;
				}else{
					fwTag.setUserCreatedBy(this.getUserInfo(userId));
					fwTag.setUserModifiedBy(this.getUserInfo(userId));
					commonClassDao.save(fwTag);
				}   
			}else{
				fwTag.setUserModifiedBy(this.getUserInfo(userId));
				commonClassDao.update(fwTag); 
			} 

			String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString =  mapper.writeValueAsString(commonClassDao.getData(FWTag.class));
			}
			if (fromAPI) {
				if (responseString!=null && !responseString.equals("Duplicate")) {
					responseString = "Success";
				}
				ModelAndView result = new ModelAndView();
				result.setViewName(responseString);
				return result;
			} else {

				response.setCharacterEncoding("UTF-8");
				response.setContentType("application / json");
				request.setCharacterEncoding("UTF-8");

				PrintWriter out = response.getWriter();
				JSONObject j = new JSONObject("{fwTagDictionaryDatas: " + responseString + "}");
				out.write(j.toString());
				return null;
			}
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}

	@RequestMapping(value={"/fw_dictionary/remove_tagList"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView removeFirewallTagDictionary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			FWTag fwTag = (FWTag)mapper.readValue(root.get("data").toString(), FWTag.class);
			commonClassDao.delete(fwTag);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(commonClassDao.getData(FWTag.class));
			JSONObject j = new JSONObject("{fwTagDictionaryDatas: " + responseString + "}");
			out.write(j.toString());

			return null;
		}
		catch (Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}
		return null;
	}
}

class TagGridValues{
	private ArrayList<Object> tags;

	public ArrayList<Object> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Object> tags) {
		this.tags = tags;
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
