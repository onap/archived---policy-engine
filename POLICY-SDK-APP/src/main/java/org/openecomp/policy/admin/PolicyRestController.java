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
package org.openecomp.policy.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;
import org.openecomp.policy.controller.CreateClosedLoopFaultController;
import org.openecomp.policy.controller.CreateDcaeMicroServiceController;
import org.openecomp.policy.controller.CreateFirewallController;
import org.openecomp.policy.controller.PolicyController;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.utils.PolicyUtils;
import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
@RequestMapping("/")
public class PolicyRestController extends RestrictedBaseController{

	private static final Logger LOGGER	= FlexLogger.getLogger(PolicyRestController.class);
	
	private String boundary = null;
	
	@Autowired
	CommonClassDao commonClassDao;

	@RequestMapping(value={"/policycreation/save_policy"}, method={RequestMethod.POST})
	public ModelAndView policyCreationController(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String userId = UserUtils.getUserSession(request).getOrgUserId();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());
		
		PolicyRestAdapter policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);
	
		if(root.get("policyData").get("model").get("type").toString().replace("\"", "").equals("file")){
			policyData.isEditPolicy = true;
		}
		if(root.get("policyData").get("model").get("path").size() != 0){
			String dirName = "";
			for(int i = 0; i < root.get("policyData").get("model").get("path").size(); i++){
				dirName = dirName.replace("\"", "") + root.get("policyData").get("model").get("path").get(i).toString().replace("\"", "") + File.separator;
			}
			if(policyData.isEditPolicy){
				policyData.setDomainDir(dirName.substring(0, dirName.lastIndexOf(File.separator)));
			}else{
				policyData.setDomainDir(dirName + root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			}
		}else{
			String domain = root.get("policyData").get("model").get("name").toString();
			if(domain.contains("/")){
				domain = domain.substring(0, domain.lastIndexOf("/")).replace("/", File.separator);
			}
			domain = domain.replace("\"", "");
			policyData.setDomainDir(domain);
		}
		
		if(policyData.getConfigPolicyType() != null){
			if(policyData.getConfigPolicyType().equalsIgnoreCase("ClosedLoop_Fault")){
				CreateClosedLoopFaultController faultController = new CreateClosedLoopFaultController();
				policyData = faultController.setDataToPolicyRestAdapter(policyData, root);
			}else if(policyData.getConfigPolicyType().equalsIgnoreCase("Firewall Config")){
				CreateFirewallController fwController = new CreateFirewallController();
				policyData = fwController.setDataToPolicyRestAdapter(policyData);
			}else if(policyData.getConfigPolicyType().equalsIgnoreCase("Micro Service")){
				CreateDcaeMicroServiceController msController = new CreateDcaeMicroServiceController();
				policyData = msController.setDataToPolicyRestAdapter(policyData, root);
			}
		}
		
		policyData.setUserId(userId);
		
		String result;
		String body = PolicyUtils.objectToJsonString(policyData);
		String uri = request.getRequestURI();
		ResponseEntity<?> responseEntity = sendToPAP(body, uri, request, HttpMethod.POST);
		if(responseEntity.getBody().equals(HttpServletResponse.SC_CONFLICT)){
			result = "PolicyExists";
		}else{
			result =  responseEntity.getBody().toString();
			String policyName = responseEntity.getHeaders().get("policyName").get(0).toString();
			if(policyData.isEditPolicy){
				if(result.equalsIgnoreCase("success")){
					PolicyNotificationMail email = new PolicyNotificationMail();
					String mode = "EditPolicy";
					String watchPolicyName = policyName.replace(".xml", "");
					String version = watchPolicyName.substring(watchPolicyName.lastIndexOf(".")+1);
					watchPolicyName = watchPolicyName.substring(0, watchPolicyName.lastIndexOf(".")).replace(".", File.separator);
					String policyVersionName = watchPolicyName.replace(".", File.separator);
					watchPolicyName = watchPolicyName + "." + version + ".xml";
					PolicyVersion entityItem = new PolicyVersion();
					entityItem.setPolicyName(policyVersionName);
					entityItem.setActiveVersion(Integer.parseInt(version));
					entityItem.setModifiedBy(userId);
					email.sendMail(entityItem, watchPolicyName, mode, commonClassDao);
				}
			}
		}
		
	
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(result);
		JSONObject j = new JSONObject("{policyData: " + responseString + "}");
		out.write(j.toString());
		return null;

	}
	
	
	private ResponseEntity<?> sendToPAP(String body, String requestURI, HttpServletRequest request, HttpMethod method) throws Exception{
		String papUrl = PolicyController.papUrl;
		String papID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
		String papPass = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS);
		LOGGER.info("User Id is " + papID + "Pass is: " + papPass);

		Base64.Encoder encoder = Base64.getEncoder();
		String encoding = encoder.encodeToString((papID+":"+papPass).getBytes(StandardCharsets.UTF_8));
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + encoding);
		headers.set("Content-Type", "application/json");

		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<?> result = null;
		HttpClientErrorException exception = null;
	
		try{
			result = ((ResponseEntity<?>) restTemplate.exchange(papUrl + requestURI, method, requestEntity, String.class));
		}catch(Exception e){
			LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while connecting to " + papUrl, e);
			exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			if(e.getMessage().equals("409 Conflict")){
				return (ResponseEntity<?>) ResponseEntity.ok(HttpServletResponse.SC_CONFLICT);
			}
		}
		if(exception != null && exception.getStatusCode()!=null){
			if(exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
				String message = XACMLErrorConstants.ERROR_PERMISSIONS +":"+exception.getStatusCode()+":" + "ERROR_AUTH_GET_PERM" ;
				LOGGER.error(message);
				throw new Exception(message, exception);
			}
			if(exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
				String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ":"+exception.getStatusCode()+":" + exception.getResponseBodyAsString();
				LOGGER.error(message);
				throw new Exception(message, exception);
			}
			if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)){
				String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while connecting to " + papUrl + exception;
				LOGGER.error(message);
				throw new Exception(message, exception);
			}
			String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + ":"+exception.getStatusCode()+":" + exception.getResponseBodyAsString();
			LOGGER.error(message);
			throw new Exception(message, exception);
		}
		return result;	
	}
	
	private String callPAP(HttpServletRequest request, HttpServletResponse response, String method, String uri){
		String papUrl = PolicyController.papUrl;
		String papID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
		String papPass = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS);
		LOGGER.info("User Id is " + papID + "Pass is: " + papPass);

		Base64.Encoder encoder = Base64.getEncoder();
		String encoding = encoder.encodeToString((papID+":"+papPass).getBytes(StandardCharsets.UTF_8));
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + encoding);
		headers.set("Content-Type", "application/json");


		HttpURLConnection connection = null;
		List<FileItem> items;
		FileItem item = null;
		File file = null;
		if(uri.contains("import_dictionary")){
			try {
				items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				item = items.get(0);
				file = new File(item.getName());
				String newFile = file.toString();
				uri = uri +"&dictionaryName="+newFile;
			} catch (Exception e2) {
				LOGGER.error("Exception Occured while calling PAP with import dictionary request"+e2);
			}
		}

		try {
			URL url = new URL(papUrl + uri);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			if(!uri.contains("searchPolicy?action=delete&")){
				
				if(!(uri.endsWith("set_BRMSParamData") || uri.contains("import_dictionary"))){
					connection.setRequestProperty("Content-Type","application/json");
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					JsonNode root = null;
					try {
						root = mapper.readTree(request.getReader());
					}catch (Exception e1) {
						LOGGER.error("Exception Occured while calling PAP"+e1);
					}

					ObjectMapper mapper1 = new ObjectMapper();
					mapper1.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

					Object obj = mapper1.treeToValue(root, Object.class);
					String json = mapper1.writeValueAsString(obj);

					Object content =  new ByteArrayInputStream(json.getBytes());

					if (content instanceof InputStream) {
						// send current configuration
						try (OutputStream os = connection.getOutputStream()) {
							int count = IOUtils.copy((InputStream) content, os);
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("copied to output, bytes=" + count);
							}
						}
					}
				}else{
					if(uri.endsWith("set_BRMSParamData")){
						connection.setRequestProperty("Content-Type","application/json");
						try (OutputStream os = connection.getOutputStream()) {
							IOUtils.copy((InputStream) request.getInputStream(), os);
						}
					}else{
						boundary = "===" + System.currentTimeMillis() + "===";
						connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
						try (OutputStream os = connection.getOutputStream()) {
							if(item != null){
								IOUtils.copy((InputStream) item.getInputStream(), os);
							}
						}
					}
				}
			}

			connection.connect();

			int responseCode = connection.getResponseCode();
			if(responseCode == 200){
				// get the response content into a String
				String responseJson = null;
				// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
				java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream());
				scanner.useDelimiter("\\A");
				responseJson =  scanner.hasNext() ? scanner.next() : "";
				scanner.close();
				LOGGER.info("JSON response from PAP: " + responseJson);
				return responseJson;
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured"+e);
		}finally{
			if(file != null){
				if(file.exists()){
					file.delete();
				}
			}
			if (connection != null) {
				try {
					// For some reason trying to get the inputStream from the connection
					// throws an exception rather than returning null when the InputStream does not exist.
					InputStream is = null;
					try {
						is = connection.getInputStream();
					} catch (Exception e1) {
						// ignore this
					}
					if (is != null) {
						is.close();
					}

				} catch (IOException ex) {
					LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to close connection: " + ex, ex);
				}
				connection.disconnect();
			}
		}
		return null;
	}
	
	@RequestMapping(value={"/getDictionary/*"}, method={RequestMethod.GET})
	public void getDictionaryController(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String uri = request.getRequestURI().replace("/getDictionary", "");
		String body = sendToPAP(null, uri, request, HttpMethod.GET).getBody().toString();
		response.getWriter().write(body);
	}
	
	@RequestMapping(value={"/saveDictionary/*/*"}, method={RequestMethod.POST})
	public ModelAndView saveDictionaryController(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String uri = request.getRequestURI().replace("/saveDictionary", "");
		if(uri.contains("import_dictionary")){
			String userId = UserUtils.getUserSession(request).getOrgUserId();
			uri = uri+ "?userId=" +userId;
		}
		String body = callPAP(request, response, "POST", uri.replaceFirst("/", "").trim());
		response.getWriter().write(body);
		return null;
	}
	
	@RequestMapping(value={"/deleteDictionary/*/*"}, method={RequestMethod.POST})
	public ModelAndView deletetDictionaryController(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String uri = request.getRequestURI().replace("/deleteDictionary", "");
		String body = callPAP(request, response, "POST", uri.replaceFirst("/", "").trim());
		response.getWriter().write(body);
		return null;
	}
	
	@RequestMapping(value={"/searchDictionary"}, method={RequestMethod.POST})
	public ModelAndView searchDictionaryController(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Object resultList = null;
		String uri = request.getRequestURI();
		String body = callPAP(request, response, "POST", uri.replaceFirst("/", "").trim());
		if(body.contains("CouldNotConnectException")){
			List<String> data = new ArrayList<>();
			data.add("Elastic Search Server is down");
			resultList = data;
		}else{
			JSONObject json = new JSONObject(body);
			resultList = json.get("policyresult");
		}
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		PrintWriter out = response.getWriter();
		JSONObject j = new JSONObject("{result: " + resultList + "}");
		out.write(j.toString());
		return null;
	}
	
	@RequestMapping(value={"/searchPolicy"}, method={RequestMethod.POST})
	public ModelAndView searchPolicy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Object resultList = null;
		String uri = request.getRequestURI()+"?action=search";
		String body = callPAP(request, response, "POST", uri.replaceFirst("/", "").trim());

		JSONObject json = new JSONObject(body);
		try{
			resultList = json.get("policyresult");
		}catch(Exception e){
			List<String> data = new ArrayList<>();
			data.add("Elastic Search Server is down");
			resultList = data;
		}

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		JSONObject j = new JSONObject("{result: " + resultList + "}");
		out.write(j.toString());
		return null;
	}
	
	public void deleteElasticData(String fileName){
		String uri = "searchPolicy?action=delete&policyName='"+fileName+"'";
		callPAP(null, null, "POST", uri.trim());
	}

}