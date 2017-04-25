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


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.policy.dao.SystemLogDbDao;
import org.openecomp.policy.model.PDPGroupContainer;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.EcompPDP;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDP;
import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping({"/"})
public class DashboardController  extends RestrictedBaseController{
	private static final Logger logger = FlexLogger.getLogger(DashboardController.class);
	@Autowired
	SystemLogDbDao systemDAO;
	
	@Autowired
	CommonClassDao commonClassDao;
	
	private int pdpCount;
	private PDPGroupContainer pdpConatiner;
	private ArrayList<Object> pdpStatusData;
	private ArrayList<Object> papStatusData;
	private ArrayList<Object> policyActivityData;
	

	
	@RequestMapping(value={"/get_DashboardLoggingData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("availableLoggingDatas", mapper.writeValueAsString(systemDAO.getLoggingData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_DashboardSystemAlertData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getSystemAlertData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			model.put("systemAlertsTableDatas", mapper.writeValueAsString(systemDAO.getSystemAlertData()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_DashboardPAPStatusData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPAPStatusData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			addPAPToTable();
			model.put("papTableDatas", mapper.writeValueAsString(papStatusData));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_DashboardPDPStatusData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPDPStatusData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			this.pdpConatiner = new PDPGroupContainer(PolicyController.getPapEngine());
			addPDPToTable();
			model.put("pdpTableDatas", mapper.writeValueAsString(pdpStatusData));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value={"/get_DashboardPolicyActivityData"}, method={org.springframework.web.bind.annotation.RequestMethod.GET} , produces=MediaType.APPLICATION_JSON_VALUE)
	public void getPolicyActivityData(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String, Object> model = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			this.pdpConatiner = new PDPGroupContainer(PolicyController.getPapEngine());
			addPolicyToTable();
			model.put("policyActivityTableDatas", mapper.writeValueAsString(policyActivityData));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Add the PAP information to the PAP Table
	 */
	public void addPAPToTable(){
		papStatusData = new ArrayList<Object>();
		String papStatus = null;
		try {
			Set<EcompPDPGroup> groups = PolicyController.getPapEngine().getEcompPDPGroups();
			if (groups == null) {
				papStatus = "UNKNOWN";
				throw new PAPException("PAP not running");		
			}else {
				papStatus = "IS_OK";
			}
		} catch (PAPException | NullPointerException e1) {
			papStatus = "CANNOT_CONNECT";
			logger.error("Error getting PAP status, PAP not responding to requests");
		}
		String papURL = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
		JSONObject object = new JSONObject();
		object.put("system", papURL);
		object.put("status", papStatus);
		List<Object> data = commonClassDao.getDataByQuery("from PolicyEntity");
		object.put("noOfPolicy", data.size());
		object.put("noOfConnectedTrap", pdpCount);
		papStatusData.add(0, object);
	}
	
	/**
	 * Add PDP Information to the PDP Table
	 *  
	 */
	public void addPDPToTable(){	
		pdpCount = 0;
		pdpStatusData = new ArrayList<Object>();
		long naCount;
		long denyCount = 0;
		long permitCount = 0;
		for (PDPGroup group : this.pdpConatiner.getGroups()){	
			for (PDP pdp : group.getPdps()){
				naCount = -1;
				if (pdp.getStatus().getStatus().toString() == "UP_TO_DATE" && ((EcompPDP) pdp).getJmxPort() != 0){
					String pdpIpAddress = parseIPSystem(pdp.getId());
					int port = ((EcompPDP) pdp).getJmxPort();
					if (port != 0)
					logger.debug("Getting JMX Response Counts from " + pdpIpAddress + " at JMX port " + port);
					naCount = getRequestCounts(pdpIpAddress, port, "pdpEvaluationNA");
					permitCount = getRequestCounts(pdpIpAddress, port, "PdpEvaluationPermit");
					denyCount = getRequestCounts(pdpIpAddress, port, "PdpEvaluationDeny");
				}
				if (naCount == -1){
					JSONObject object = new JSONObject();
					object.put("id", pdp.getId());
					object.put("name", pdp.getName());
					object.put("groupname", group.getName());
					object.put("status", pdp.getStatus().getStatus().toString());
					object.put("description", pdp.getDescription());
					object.put("permitCount", "NA");
					object.put("denyCount", "NA");
					object.put("naCount", "NA");
					pdpStatusData.add(object);
				}else{	
					JSONObject object = new JSONObject();
					object.put("id", pdp.getId());
					object.put("name", pdp.getName());
					object.put("groupname", group.getName());
					object.put("status", pdp.getStatus().getStatus().toString());
					object.put("description", pdp.getDescription());
					object.put("permitCount", permitCount);
					object.put("denyCount", denyCount);
					object.put("naCount", naCount);
					pdpStatusData.add(object);
				}
				pdpCount++;
			}
		}
	}
	
	private static String parseIPSystem(String line) {	
		Pattern pattern = Pattern.compile("://(.+?):");
		Matcher ip = pattern.matcher(line);
		if (ip.find())
		{
			return ip.group(1);
		} 
		return null;
	}
	
	/*
	 * Contact JMX Connector Sever and return the value of the given jmxAttribute
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private long getRequestCounts(String host, int port, String jmxAttribute) {
		
		logger.debug("Create an RMI connector client and connect it to the JMX connector server");
		HashMap map = new HashMap();
		map = null;
		JMXConnector jmxConnection;
		try {
			jmxConnection = JMXConnectorFactory.newJMXConnector(createConnectionURL(host, port), map);
			jmxConnection.connect();
			Object o = jmxConnection.getMBeanServerConnection().getAttribute(new ObjectName("PdpRest:type=PdpRestMonitor"), jmxAttribute);
			jmxConnection.close();
			logger.debug("pdpEvaluationNA value retreived: " + o);
			return (long) o;
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException for JMX connection");
		} catch (IOException e) {
			logger.error("Error in reteriving" + jmxAttribute + " from JMX connection");
		} catch (AttributeNotFoundException e) {		
			logger.error("AttributeNotFoundException  " + jmxAttribute +  " for JMX connection");
		} catch (InstanceNotFoundException e) {
			logger.error("InstanceNotFoundException " + host + " for JMX connection");
		} catch (MalformedObjectNameException e) {
			logger.error("MalformedObjectNameException for JMX connection");
		} catch (MBeanException e) {
			logger.error("MBeanException for JMX connection");
			e.printStackTrace();
		} catch (ReflectionException e) {
			logger.error("ReflectionException for JMX connection");
		}
		
		return -1;
	}
	
	private static JMXServiceURL createConnectionURL(String host, int port) throws MalformedURLException{
	    return new JMXServiceURL("rmi", "", 0, "/jndi/rmi://" + host + ":" + port + "/jmxrmi");
	}
	
	
	/*
	 * Add the information to the Policy Table
	 */
	private void addPolicyToTable() {
		policyActivityData = new ArrayList<Object>();
		int i = 1;
		String policyID = null;
		int policyFireCount = 0;
		Map<String, String> policyMap = new HashMap<>();
		Object policyList = null;
		//get list of policy 
		
		for (PDPGroup group : this.pdpConatiner.getGroups()){	
			for (PDPPolicy policy : group.getPolicies()){
				try{
					policyMap.put(policy.getPolicyId().replace(" ", ""), policy.getId());
				}catch(Exception e){
					logger.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID+policy.getName() +e);
				}
			}
			
			for (PDP pdp : group.getPdps()){		
					// Add rows to the Policy Table
				policyList = null;
				if (pdp.getStatus().getStatus().toString() == "UP_TO_DATE" && ((EcompPDP) pdp).getJmxPort() != 0){
					String pdpIpAddress = parseIPSystem(pdp.getId());
					policyList = getPolicy(pdpIpAddress, ((EcompPDP) pdp).getJmxPort(), "policyCount");
				}
				if (policyList != null && policyList.toString().length() > 3){
						String[]  splitPolicy = policyList.toString().split(",");
						for (String policyKeyValue : splitPolicy){	
							policyID = urnPolicyID(policyKeyValue);	
							policyFireCount = countPolicyID(policyKeyValue);	
							if (policyID != null ){
								if (policyMap.containsKey(policyID)){
									JSONObject object = new JSONObject();
									object.put("policyId", policyMap.get(policyID));
									object.put("fireCount", policyFireCount);
									object.put("system", pdp.getId());
									policyActivityData.add(i, object);	
									i++;
								}
							}
						}
				}else {
					if (policyList != null){
						JSONObject object = new JSONObject();
						object.put("policyId", "Unable to retrieve policy information");
						object.put("fireCount", "NA");
						object.put("system", pdp.getId());
						policyActivityData.add(i, object);
						i++;
					}else{
						JSONObject object = new JSONObject();
						object.put("policyId", "Unable to access PDP JMX Server");
						object.put("fireCount", "NA");
						object.put("system", pdp.getId());
						policyActivityData.add(i, object);
						i++;
					}
				}							
	
			}
		}
	}
	
	/*
	 * Contact JMX Connector Sever and return the list of {policy id , count}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getPolicy(String host, int port, String jmxAttribute){
		logger.debug("Create an RMI connector client and connect it to the JMX connector server for Policy: " + host);
		HashMap map = new HashMap();
		map = null;
		JMXConnector jmxConnection;
		try {
			jmxConnection = JMXConnectorFactory.newJMXConnector(createConnectionURL(host, port), map);
			jmxConnection.connect();
			Object o = jmxConnection.getMBeanServerConnection().getAttribute(new ObjectName("PdpRest:type=PdpRestMonitor"), "policyMap");
			jmxConnection.close();
			logger.debug("policyMap value retreived: " + o);
			return  o;
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException for JMX connection");
		} catch (IOException e) {
			logger.error("AttributeNotFoundException for policyMap" );
		} catch (AttributeNotFoundException e) {		
			logger.error("AttributeNotFoundException for JMX connection");
		} catch (InstanceNotFoundException e) {
			logger.error("InstanceNotFoundException " + host + " for JMX connection");
		} catch (MalformedObjectNameException e) {
			logger.error("MalformedObjectNameException for JMX connection");
		} catch (MBeanException e) {
			logger.error("MBeanException for JMX connection");
			e.printStackTrace();
		} catch (ReflectionException e) {
			logger.error("ReflectionException for JMX connection");
		}
		
		return null;
	
	}
	
	private static String urnPolicyID(String line){
		String[]  splitLine = line.toString().split("=");	
		String removeSpaces = splitLine[0].replaceAll("\\s+", "");
		return removeSpaces.replace("{", "");
	}
	
	private static Integer countPolicyID(String line){
		String[]  splitLine = line.toString().split("=");
		String sCount = splitLine[1].replace("}", "");
		int intCount = Integer.parseInt(sCount);
		return intCount;
	}
	
}
