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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.pap.xacml.rest.adapters.GridData;
import org.openecomp.policy.pap.xacml.rest.util.JsonMessage;
import org.openecomp.policy.rest.dao.PEPOptionsDao;
import org.openecomp.policy.rest.dao.ServiceDictionaryDao;
import org.openecomp.policy.rest.dao.SiteDictionaryDao;
import org.openecomp.policy.rest.dao.UserInfoDao;
import org.openecomp.policy.rest.dao.VNFTypeDao;
import org.openecomp.policy.rest.dao.VSCLActionDao;
import org.openecomp.policy.rest.dao.VarbindDictionaryDao;
import org.openecomp.policy.rest.jpa.ClosedLoopD2Services;
import org.openecomp.policy.rest.jpa.ClosedLoopSite;
import org.openecomp.policy.rest.jpa.PEPOptions;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.openecomp.policy.rest.jpa.VNFType;
import org.openecomp.policy.rest.jpa.VSCLAction;
import org.openecomp.policy.rest.jpa.VarbindDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ClosedLoopDictionaryController{

	@Autowired
	VSCLActionDao vsclActionDao;
	
	@Autowired
	VNFTypeDao vnfTypeDao;
	
	@Autowired
	PEPOptionsDao pepOptionsDao;
	
	@Autowired
	VarbindDictionaryDao varbindDao;
	
	@Autowired
	ServiceDictionaryDao closedLoopServiceDao;
	
	@Autowired
	SiteDictionaryDao closedLoopSiteDao;
	
	@Autowired
	UserInfoDao userInfoDao;
	
	public UserInfo getUserInfo(String loginId){
		UserInfo name = userInfoDao.getUserInfoByLoginId(loginId);
		return name;	
	}
	
	
	@RequestMapping(value={"/get_VSCLActionDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVSCLActionDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vsclActionDictionaryDatas", mapper.writeValueAsString(vsclActionDao.getVsclActionDataByName()));
			org.openecomp.policy.pap.xacml.rest.util.JsonMessage msg = new org.openecomp.policy.pap.xacml.rest.util.JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value={"/get_VSCLActionData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVSCLActionDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vsclActionDictionaryDatas", mapper.writeValueAsString(vsclActionDao.getVSCLActionData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_VNFTypeDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVNFTypeDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vnfTypeDictionaryDatas", mapper.writeValueAsString(vnfTypeDao.getVNFTypeDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
		
	@RequestMapping(value={"/get_VNFTypeData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVNFTypeDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("vnfTypeDictionaryDatas", mapper.writeValueAsString(vnfTypeDao.getVNFTypeData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_PEPOptionsDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPEPOptionsDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("pepOptionsDictionaryDatas", mapper.writeValueAsString(pepOptionsDao.getPEPOptionsDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_PEPOptionsData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPEPOptionsDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("pepOptionsDictionaryDatas", mapper.writeValueAsString(pepOptionsDao.getPEPOptionsData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_VarbindDictionaryDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVarbindDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("varbindDictionaryDatas", mapper.writeValueAsString(varbindDao.getVarbindDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_VarbindDictionaryData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getVarbindDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("varbindDictionaryDatas", mapper.writeValueAsString(varbindDao.getVarbindDictionaryData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ClosedLoopServicesDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopServiceDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopServiceDictionaryDatas", mapper.writeValueAsString(closedLoopServiceDao.getCLServiceDictDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ClosedLoopServicesData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopServiceDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopServiceDictionaryDatas", mapper.writeValueAsString(closedLoopServiceDao.getClosedLoopD2ServicesData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ClosedLoopSiteDataByName"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopSiteDictionaryByNameEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopSiteDictionaryDatas", mapper.writeValueAsString(closedLoopSiteDao.getCLSiteDataByName()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_ClosedLoopSiteData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getClosedLoopSiteDictionaryEntityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("closedLoopSiteDictionaryDatas", mapper.writeValueAsString(closedLoopSiteDao.getClosedLoopSiteData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/cl_dictionary/save_vsclAction.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView saveVSCLAction(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	    	boolean duplicateflag = false;
		      ObjectMapper mapper = new ObjectMapper();
		      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		      JsonNode root = mapper.readTree(request.getReader());
		      VSCLAction vSCLAction = (VSCLAction)mapper.readValue(root.get("vsclActionDictionaryData").toString(), VSCLAction.class);
		      String userId = root.get("loginId").textValue();
		      if(vSCLAction.getId() == 0){
		    	  CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
		    	  List<Object> duplicateData =  entry.CheckDuplicateEntry(vSCLAction.getVsclaction(), "vsclaction", VSCLAction.class);
		    	  if(!duplicateData.isEmpty()){
		    		  duplicateflag = true;
		    	  }else{
		    		  vSCLAction.setUserCreatedBy(this.getUserInfo(userId));
		    		  vSCLAction.setUserModifiedBy(this.getUserInfo(userId));
		    		  vsclActionDao.Save(vSCLAction);
		    	  }
		      }else{
		    	  vSCLAction.setUserModifiedBy(this.getUserInfo(userId));
		    	  vsclActionDao.update(vSCLAction); 
		      } 
		      response.setCharacterEncoding("UTF-8");
		      response.setContentType("application / json");
		      request.setCharacterEncoding("UTF-8");
		      
		      PrintWriter out = response.getWriter();
		      String responseString = "";
			  if(duplicateflag){
				responseString = "Duplicate";
			  }else{
				responseString = mapper.writeValueAsString(this.vsclActionDao.getVSCLActionData());
			  }	  
		      JSONObject j = new JSONObject("{vsclActionDictionaryDatas: " + responseString + "}");
		      
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
	
	@RequestMapping(value={"/cl_dictionary/remove_VsclAction.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removeVSCLAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      VSCLAction vSCLAction = (VSCLAction)mapper.readValue(root.get("data").toString(), VSCLAction.class);
	      vsclActionDao.delete(vSCLAction);
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      
	      String responseString = mapper.writeValueAsString(this.vsclActionDao.getVSCLActionData());
	      JSONObject j = new JSONObject("{vsclActionDictionaryDatas: " + responseString + "}");
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
	
	@RequestMapping(value={"/cl_dictionary/save_vnfType.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView saveVnfType(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	    	boolean duplicateflag = false;
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      VNFType vNFType = (VNFType)mapper.readValue(root.get("vnfTypeDictionaryData").toString(), VNFType.class);
		  String userId = root.get("loginId").textValue();
	      if(vNFType.getId() == 0){
	    	  CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
	    	  List<Object> duplicateData =  entry.CheckDuplicateEntry(vNFType.getVnftype(), "vnftype", VNFType.class);
	    	  if(!duplicateData.isEmpty()){
	    	  	duplicateflag = true;
	    	  }else{
	    		  vNFType.setUserCreatedBy(this.getUserInfo(userId));
		    	  vNFType.setUserModifiedBy(this.getUserInfo(userId));
		    	  vnfTypeDao.Save(vNFType);
	    	  }  	 
	      }else{
	    	  vNFType.setUserModifiedBy(this.getUserInfo(userId));
	    	  vnfTypeDao.update(vNFType); 
	      } 
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      String responseString = "";
			if(duplicateflag){
				responseString = "Duplicate";
			}else{
				responseString = mapper.writeValueAsString(this.vnfTypeDao.getVNFTypeData());
			} 
	      JSONObject j = new JSONObject("{vnfTypeDictionaryDatas: " + responseString + "}");
	      
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
	
	@RequestMapping(value={"/cl_dictionary/remove_vnfType.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removeVnfType(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
		      ObjectMapper mapper = new ObjectMapper();
		      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		      JsonNode root = mapper.readTree(request.getReader());
		      VNFType vNFType = (VNFType)mapper.readValue(root.get("data").toString(), VNFType.class);
		      vnfTypeDao.delete(vNFType);
		      response.setCharacterEncoding("UTF-8");
		      response.setContentType("application / json");
		      request.setCharacterEncoding("UTF-8");
		      
		      PrintWriter out = response.getWriter();
		      
		      String responseString = mapper.writeValueAsString(this.vnfTypeDao.getVNFTypeData());
		      JSONObject j = new JSONObject("{vnfTypeDictionaryDatas: " + responseString + "}");
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
	
	@RequestMapping(value={"/cl_dictionary/save_pepOptions.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView savePEPOptions(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	    	boolean duplicateflag = false;
		      ObjectMapper mapper = new ObjectMapper();
		      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		      JsonNode root = mapper.readTree(request.getReader());
		      PEPOptions pEPOptions = (PEPOptions)mapper.readValue(root.get("pepOptionsDictionaryData").toString(), PEPOptions.class);
		      GridData gridData = (GridData)mapper.readValue(root.get("pepOptionsDictionaryData").toString(), GridData.class);
		      String userId = root.get("loginId").textValue();
		      String actions = "";
				int counter = 0;
				if(gridData.getAttributes().size() > 0){
					for(Object attribute : gridData.getAttributes()){
						if(attribute instanceof LinkedHashMap<?, ?>){
							String key = ((LinkedHashMap<?, ?>) attribute).get("option").toString();
							String value = ((LinkedHashMap<?, ?>) attribute).get("number").toString();
							if(counter>0){
								actions = actions + ":#@";
							}
							actions = actions + key + "=#@";
							actions = actions + value;
							counter ++;
						}
					}
				}
				pEPOptions.setActions(actions);
		      if(pEPOptions.getId() == 0){
		    	  CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
		    	  List<Object> duplicateData =  entry.CheckDuplicateEntry(pEPOptions.getPepName(), "pepName", PEPOptions.class);
		    	  if(!duplicateData.isEmpty()){
		    	  	duplicateflag = true;
		    	  }else{
		    		  pEPOptions.setUserCreatedBy(this.getUserInfo(userId));
			    	  pEPOptions.setUserModifiedBy(this.getUserInfo(userId));
			    	  pepOptionsDao.Save(pEPOptions);
		    	  }   	 
		      }else{
		    	  pEPOptions.setUserModifiedBy(this.getUserInfo(userId));
		    	  pepOptionsDao.update(pEPOptions); 
		      } 
		      response.setCharacterEncoding("UTF-8");
		      response.setContentType("application / json");
		      request.setCharacterEncoding("UTF-8");
		      
		      PrintWriter out = response.getWriter();
		      String responseString = "";
		      if(duplicateflag){
		      	responseString = "Duplicate";
		      }else{
		    	  responseString = mapper.writeValueAsString(this.pepOptionsDao.getPEPOptionsData());
		      } 
		      JSONObject j = new JSONObject("{pepOptionsDictionaryDatas: " + responseString + "}");
		      
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
	
	@RequestMapping(value={"/cl_dictionary/remove_pepOptions.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removePEPOptions(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      PEPOptions pEPOptions = (PEPOptions)mapper.readValue(root.get("data").toString(), PEPOptions.class);
	      pepOptionsDao.delete(pEPOptions);
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      
	      String responseString = mapper.writeValueAsString(this.pepOptionsDao.getPEPOptionsData());
	      JSONObject j = new JSONObject("{pepOptionsDictionaryDatas: " + responseString + "}");
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
	
	@RequestMapping(value={"/cl_dictionary/save_service.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView saveServiceType(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	    	boolean duplicateflag = false;
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      ClosedLoopD2Services serviceData = (ClosedLoopD2Services)mapper.readValue(root.get("closedLoopServiceDictionaryData").toString(), ClosedLoopD2Services.class);
	      String userId = root.get("loginId").textValue();
	      if(serviceData.getId() == 0){
	    	  CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
	    	  List<Object> duplicateData =  entry.CheckDuplicateEntry(serviceData.getServiceName(), "serviceName", ClosedLoopD2Services.class);
	    	  if(!duplicateData.isEmpty()){
	    	  	duplicateflag = true;
	    	  }else{
	    		  serviceData.setUserCreatedBy(this.getUserInfo(userId));
		    	  serviceData.setUserModifiedBy(this.getUserInfo(userId));
		    	  closedLoopServiceDao.Save(serviceData);
	    	  }
	      }else{
	    	  serviceData.setUserModifiedBy(this.getUserInfo(userId));
	    	  closedLoopServiceDao.update(serviceData); 
	      } 
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      String responseString = "";
	      if(duplicateflag){
	      	responseString = "Duplicate";
	      }else{
	    	  responseString = mapper.writeValueAsString(this.closedLoopServiceDao.getClosedLoopD2ServicesData());
	      } 
	      JSONObject j = new JSONObject("{closedLoopServiceDictionaryDatas: " + responseString + "}");
	      
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
	
	@RequestMapping(value={"/cl_dictionary/remove_Service.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removeServiceType(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      ClosedLoopD2Services closedLoopD2Services = (ClosedLoopD2Services)mapper.readValue(root.get("data").toString(), ClosedLoopD2Services.class);
	      closedLoopServiceDao.delete(closedLoopD2Services);
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      
	      String responseString = mapper.writeValueAsString(this.closedLoopServiceDao.getClosedLoopD2ServicesData());
	      JSONObject j = new JSONObject("{closedLoopServiceDictionaryDatas: " + responseString + "}");
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
	
	@RequestMapping(value={"/cl_dictionary/save_siteName.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView saveSiteType(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	    	boolean duplicateflag = false;
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      ClosedLoopSite siteData = (ClosedLoopSite)mapper.readValue(root.get("closedLoopSiteDictionaryData").toString(), ClosedLoopSite.class);
	      String userId = root.get("loginId").textValue();
	      if(siteData.getId() == 0){
	    	  CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
	    	  List<Object> duplicateData =  entry.CheckDuplicateEntry(siteData.getSiteName(), "siteName", ClosedLoopSite.class);
	    	  if(!duplicateData.isEmpty()){
	    	  	duplicateflag = true;
	    	  }else{
	    		  siteData.setUserCreatedBy(this.getUserInfo(userId));
		    	  siteData.setUserModifiedBy(this.getUserInfo(userId));
		    	  closedLoopSiteDao.Save(siteData);
	    	  }
	      }else{
	    	  siteData.setUserModifiedBy(this.getUserInfo(userId));
	    	  closedLoopSiteDao.update(siteData); 
	      } 
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      String responseString = "";
	      if(duplicateflag){
	      	responseString = "Duplicate";
	      }else{
	      	responseString = mapper.writeValueAsString(this.closedLoopSiteDao.getClosedLoopSiteData());
	      }	
	      JSONObject j = new JSONObject("{closedLoopSiteDictionaryDatas: " + responseString + "}");
	      
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
	
	@RequestMapping(value={"/cl_dictionary/remove_site.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removeSiteType(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      ClosedLoopSite closedLoopSite = (ClosedLoopSite)mapper.readValue(root.get("data").toString(), ClosedLoopSite.class);
	      closedLoopSiteDao.delete(closedLoopSite);
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      
	      String responseString = mapper.writeValueAsString(this.closedLoopSiteDao.getClosedLoopSiteData());
	      JSONObject j = new JSONObject("{closedLoopSiteDictionaryDatas: " + responseString + "}");
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
	
	@RequestMapping(value={"/cl_dictionary/save_varbind.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView saveVarbind(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    try {
	    	boolean duplicateflag = false;
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      VarbindDictionary varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("varbindDictionaryData").toString(), VarbindDictionary.class);
	      String userId = root.get("loginId").textValue();
	      if(varbindDictionary.getId() == 0){
	    	  CheckDictionaryDuplicateEntries entry = new CheckDictionaryDuplicateEntries();
	    	  List<Object> duplicateData =  entry.CheckDuplicateEntry(varbindDictionary.getVarbindName(), "varbindName", VarbindDictionary.class);
	    	  if(!duplicateData.isEmpty()){
	    	  	duplicateflag = true;
	    	  }else{
	    		  varbindDictionary.setUserCreatedBy(this.getUserInfo(userId));
		    	  varbindDictionary.setUserModifiedBy(this.getUserInfo(userId));
		    	  varbindDao.Save(varbindDictionary);
	    	  }	  
	      }else{
	    	  varbindDictionary.setUserModifiedBy(this.getUserInfo(userId));
	    	  varbindDao.update(varbindDictionary); 
	      } 
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      String responseString = "";
	      if(duplicateflag){
	      	responseString = "Duplicate";
	      }else{
	      	responseString = mapper.writeValueAsString(this.varbindDao.getVarbindDictionaryData());
	      }
	      JSONObject j = new JSONObject("{varbindDictionaryDatas: " + responseString + "}");
	      
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
	
	@RequestMapping(value={"/cl_dictionary/remove_varbindDict.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  public ModelAndView removeVarbind(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	      JsonNode root = mapper.readTree(request.getReader());
	      VarbindDictionary varbindDictionary = (VarbindDictionary)mapper.readValue(root.get("data").toString(), VarbindDictionary.class);
	      varbindDao.delete(varbindDictionary);
	      response.setCharacterEncoding("UTF-8");
	      response.setContentType("application / json");
	      request.setCharacterEncoding("UTF-8");
	      
	      PrintWriter out = response.getWriter();
	      
	      String responseString = mapper.writeValueAsString(this.varbindDao.getVarbindDictionaryData());
	      JSONObject j = new JSONObject("{varbindDictionaryDatas: " + responseString + "}");
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

