/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
 /*
  * 
  * 
  * */
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionList;
import org.onap.policy.rest.jpa.ActionPolicyDict;
import org.onap.policy.rest.jpa.AddressGroup;
import org.onap.policy.rest.jpa.Attribute;
import org.onap.policy.rest.jpa.BRMSController;
import org.onap.policy.rest.jpa.BRMSDependency;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.Category;
import org.onap.policy.rest.jpa.ClosedLoopD2Services;
import org.onap.policy.rest.jpa.ClosedLoopSite;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.DecisionSettings;
import org.onap.policy.rest.jpa.DescriptiveScope;
import org.onap.policy.rest.jpa.GroupServiceList;
import org.onap.policy.rest.jpa.MicroServiceModels;
import org.onap.policy.rest.jpa.OnapName;
import org.onap.policy.rest.jpa.PEPOptions;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.ProtocolList;
import org.onap.policy.rest.jpa.SecurityZone;
import org.onap.policy.rest.jpa.ServiceList;
import org.onap.policy.rest.jpa.TermList;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.VNFType;
import org.onap.policy.rest.jpa.VSCLAction;
import org.onap.policy.rest.jpa.VarbindDictionary;
import org.onap.policy.rest.jpa.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.bytecode.opencsv.CSVReader;


@Controller
public class DictionaryImportController {
	private static final Logger LOGGER  = FlexLogger.getLogger(DictionaryImportController.class);
	
	private String newFile;

	private static CommonClassDao commonClassDao;
	private static final String DESCRIPTION= "description";
	
	@Autowired
	public DictionaryImportController(CommonClassDao commonClassDao){
		DictionaryImportController.commonClassDao = commonClassDao;
	}
	
	public DictionaryImportController(){}	


	@RequestMapping(value={"/dictionary/import_dictionary"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public void ImportDictionaryData(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String userId = request.getParameter("userId");
		String dictionaryName = request.getParameter("dictionaryName");
		
		if(dictionaryName == null || dictionaryName.isEmpty()){
			LOGGER.error("dictionaryName is null/empty");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Error");
			return;
		}
		
		// fix Fortify Path Manipulation issue
		if(!isValidDictionaryName(dictionaryName)){
			LOGGER.error("dictionaryName is invalid");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Error");
			return;			
		}
				
		boolean dictionaryImportExists = false;
		try{
			File file = new File(dictionaryName);
			OutputStream outputStream = new FileOutputStream(file);
			IOUtils.copy(request.getInputStream(), outputStream);
			outputStream.close();
			this.newFile = file.toString();
			CSVReader csvReader = new CSVReader(new FileReader(this.newFile));
			List<String[]> dictSheet = csvReader.readAll();
			if(dictionaryName.startsWith("Attribute")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					Attribute attribute = new Attribute("");
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("xacml_id").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Attribute ID").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setXacmlId(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("priority").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setPriority(rows[j]);
						}
						if(("datatype").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Data Type").equalsIgnoreCase(dictSheet.get(0)[j])){
							Datatype dataType = new Datatype();
							if(("string").equalsIgnoreCase(rows[j])){
								dataType.setId(26);
							}else if(("integer").equalsIgnoreCase(rows[j])){
								dataType.setId(12);
							}else if(("double").equalsIgnoreCase(rows[j])){
								dataType.setId(25);
							}else if(("boolean").equalsIgnoreCase(rows[j])){
								dataType.setId(18);
							}else if(("user").equalsIgnoreCase(rows[j])){
								dataType.setId(29);
							}
							attribute.setDatatypeBean(dataType);
							Category category = new Category();
							category.setId(5);
							attribute.setCategoryBean(category);
						}
						if(("attribute_value").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Attribute Value").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setAttributeValue(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ActionPolicyDictionary")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					ActionPolicyDict attribute = new ActionPolicyDict();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("attribute_name").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Attribute Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setAttributeName(rows[j]);
						}
						if(("body").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setBody(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("headers").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setHeader(rows[j]);
						}
						if(("method").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setMethod(rows[j]);
						}
						if(("type").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setType(rows[j]);
						}
						if(("url").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setUrl(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("OnapName")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					OnapName attribute = new OnapName();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("onap_name").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Onap Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setOnapName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}

			if(dictionaryName.startsWith("MSPolicyDictionary")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					MicroServiceModels attribute = new MicroServiceModels();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("modelName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Micro Service Model").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setModelName(rows[j]);
						}
						if(("version").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Model Version").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setVersion(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("dependency").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDependency(rows[j]);
						}
						if(("attributes").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setAttributes(rows[j]);
						}
						if(("enumValues").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setEnumValues(rows[j]);
						}
						if(("Ref Attributes").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setRef_attributes(rows[j]);
						}
						if(("Sub Attributes").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setSub_attributes(rows[j]);
						}
					}

					commonClassDao.save(attribute);
				}
			}		

			if(dictionaryName.startsWith("VNFType")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					VNFType attribute = new VNFType();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("vnf_type").equalsIgnoreCase(dictSheet.get(0)[j]) || ("VNF Type").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setVnftype(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("VSCLAction")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					VSCLAction attribute = new VSCLAction();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("vscl_action").equalsIgnoreCase(dictSheet.get(0)[j]) || ("VSCL Action").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setVsclaction(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ClosedLoopService")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					ClosedLoopD2Services attribute = new ClosedLoopD2Services();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("serviceName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Service Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ClosedLoopSite")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					ClosedLoopSite attribute = new ClosedLoopSite();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("siteName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Site Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setSiteName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("PEPOptions")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					PEPOptions attribute = new PEPOptions();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("PEP_NAME").equalsIgnoreCase(dictSheet.get(0)[j]) || ("PEP Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setPepName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("Actions").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setActions(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("VarbindDictionary")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					VarbindDictionary attribute = new VarbindDictionary();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("varbind_Name").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Varbind Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setVarbindName(rows[j]);
						}
						if(("varbind_Description").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Varbind Description").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setVarbindDescription(rows[j]);
						}
						if(("varbind_oid").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Varbind OID").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setVarbindOID(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("BRMSParamDictionary")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					BRMSParamTemplate attribute = new BRMSParamTemplate();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("param_template_name").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Rule Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setRuleName(rows[j]);
						}
						if(("Description").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("rule").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setRule(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("BRMSControllerDictionary")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					BRMSController attribute = new BRMSController();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("controllerName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Controller Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setControllerName(rows[j]);
						}
						if(("DESCRIPTION").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("controller").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setController(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("BRMSDependencyDictionary")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					BRMSDependency attribute = new BRMSDependency();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("dependencyName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Dependency Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDependencyName(rows[j]);
						}
						if(("DESCRIPTION").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("dependency").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDependency(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("Settings")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					DecisionSettings attribute = new DecisionSettings();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("xacml_id").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Settings ID").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setXacmlId(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("priority").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setPriority(rows[j]);
						}
						if(("datatype").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Data Type").equalsIgnoreCase(dictSheet.get(0)[j])){
							Datatype dataType = new Datatype();
							if(("string").equalsIgnoreCase(rows[j])){
								dataType.setId(26);
							}else if(("integer").equalsIgnoreCase(rows[j])){
								dataType.setId(12);
							}else if(("double").equalsIgnoreCase(rows[j])){
								dataType.setId(25);
							}else if(("boolean").equalsIgnoreCase(rows[j])){
								dataType.setId(18);
							}else if(("user").equalsIgnoreCase(rows[j])){
								dataType.setId(29);
							}
							attribute.setDatatypeBean(dataType);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("PrefixList")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					PrefixList attribute = new PrefixList();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("prefixListName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("PrefixList Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setPrefixListName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setPrefixListValue(rows[j]);
						}
						if(("prefixListValue").equalsIgnoreCase(dictSheet.get(0)[j]) || ("PrefixList Value").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("SecurityZone")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					SecurityZone attribute = new SecurityZone();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("zoneName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Zone Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setZoneName(rows[j]);
						}
						if(("zoneValue").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Zone Value").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setZoneValue(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("Zone")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					Zone attribute = new Zone();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("zoneName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Zone Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setZoneName(rows[j]);
						}
						if(("zoneValue").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Zone Value").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setZoneValue(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ServiceList")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					ServiceList attribute = new ServiceList();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("serviceName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Service Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceName(rows[j]);
						}
						if(("serviceDesc").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Description").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceDescription(rows[j]);
						}
						if(("serviceType").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Service Type").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceType(rows[j]);
						}
						if(("serviceTrasProtocol").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Transport Protocol").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceTransProtocol(rows[j]);
						}
						if(("serviceAppProtocol").equalsIgnoreCase(dictSheet.get(0)[j])  || ("APP Protocol").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceAppProtocol(rows[j]);
						}
						if(("servicePorts").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Ports").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServicePorts(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ServiceGroup")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					GroupServiceList attribute = new GroupServiceList();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("name").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Group Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setGroupName(rows[j]);
						}
						if(("serviceList").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Service List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceList(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("AddressGroup")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					AddressGroup attribute = new AddressGroup();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("name").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Group Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setGroupName(rows[j]);
						}
						if(("serviceList").equalsIgnoreCase(dictSheet.get(0)[j])  || ("Prefix List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setServiceList(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ProtocolList")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					ProtocolList attribute = new ProtocolList();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("protocolName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Protocol Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setProtocolName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("ActionList")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					ActionList attribute = new ActionList();
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("actionName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Action Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setActionName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("TermList")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					TermList attribute = new TermList();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("termName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Term-Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setTermName(rows[j]);
						}
						if(("Term-Description").equalsIgnoreCase(dictSheet.get(0)[j]) || ("termDescription").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("fromZone").equalsIgnoreCase(dictSheet.get(0)[j])  || ("From Zone").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setFromZones(rows[j]);
						}
						if(("toZone").equalsIgnoreCase(dictSheet.get(0)[j]) || ("To Zone").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setToZones(rows[j]);
						}
						if(("srcIPList").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Source-IP-List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setSrcIPList(rows[j]);
						}
						if(("destIPList").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Destination-IP-List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDestIPList(rows[j]);
						}
						if(("srcPortList").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Source-Port-List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setSrcPortList(rows[j]);
						}
						if(("destPortList").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Destination-Port-List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDestPortList(rows[j]);
						}
						if(("action").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Action List").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setAction(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			if(dictionaryName.startsWith("SearchCriteria")){
				dictionaryImportExists = true;
				for(int i = 1; i< dictSheet.size(); i++){
					DescriptiveScope attribute = new DescriptiveScope();
					UserInfo userinfo = new UserInfo();
					userinfo.setUserLoginId(userId);
					attribute.setUserCreatedBy(userinfo);
					attribute.setUserModifiedBy(userinfo);
					String[] rows = dictSheet.get(i);
					for (int j=0 ; j<rows.length; j++ ){
						if(("descriptiveScopeName").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Descriptive Scope Name").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setScopeName(rows[j]);
						}
						if((DESCRIPTION).equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setDescription(rows[j]);
						}
						if(("search").equalsIgnoreCase(dictSheet.get(0)[j]) || ("Search Criteria").equalsIgnoreCase(dictSheet.get(0)[j])){
							attribute.setSearch(rows[j]);
						}
					}
					commonClassDao.save(attribute);
				}
			}
			csvReader.close();
			if(file.exists()){
				file.delete();
			}
			if(!dictionaryImportExists){
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write("Dictionary Import failed. Hence the following dictionary doen't support import function  : "+ dictionaryName);
			}else{
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().write("Success");
			}
		}catch(Exception e){
			LOGGER.error("Exception Occured while importing dictionary"+e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("Error");
		}
	}
	
	private boolean isValidDictionaryName(String dictionaryName){
		
		if(dictionaryName.startsWith("Attribute")){
			return true;
		}
		if(dictionaryName.startsWith("ActionPolicyDictionary")){
			return true;
		}
		if(dictionaryName.startsWith("OnapName")){
			return true;
		}
		if(dictionaryName.startsWith("MSPolicyDictionary")){
			return true;
		}
		if(dictionaryName.startsWith("VNFType")){
			return true;
		}
		if(dictionaryName.startsWith("VSCLAction")){
			return true;
		}
		if(dictionaryName.startsWith("ClosedLoopService")){
			return true;
		}
		if(dictionaryName.startsWith("ClosedLoopSite")){
			return true;
		}
		if(dictionaryName.startsWith("PEPOptions")){
			return true;
		}
		if(dictionaryName.startsWith("VarbindDictionary")){
			return true;
		}
		if(dictionaryName.startsWith("BRMSParamDictionary")){
			return true;
		}
		if(dictionaryName.startsWith("BRMSControllerDictionary")){
			return true;
		}
		if(dictionaryName.startsWith("BRMSDependencyDictionary")){
			return true;
		}
		if(dictionaryName.startsWith("Settings")){
			return true;
		}
		if(dictionaryName.startsWith("PrefixList")){
			return true;
		}
		if(dictionaryName.startsWith("SecurityZone")){
			return true;
		}
		if(dictionaryName.startsWith("Zone")){
			return true;
		}
		if(dictionaryName.startsWith("ServiceList")){
			return true;
		}
		if(dictionaryName.startsWith("ServiceGroup")){
			return true;
		}
		if(dictionaryName.startsWith("AddressGroup")){
			return true;
		}
		if(dictionaryName.startsWith("ProtocolList")){
			return true;
		}
		if(dictionaryName.startsWith("ActionList")){
			return true;
		}
		if(dictionaryName.startsWith("TermList")){
			return true;
		}
		if(dictionaryName.startsWith("SearchCriteria")){
			return true;
		}
		return false;
	}
}