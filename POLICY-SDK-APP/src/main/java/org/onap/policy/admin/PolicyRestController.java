/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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
package org.onap.policy.admin;

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
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controller.CreateClosedLoopFaultController;
import org.onap.policy.controller.CreateDcaeMicroServiceController;
import org.onap.policy.controller.CreateFirewallController;
import org.onap.policy.controller.CreateOptimizationController;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.adapter.PolicyRestAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.UserUtils;
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
import org.onap.policy.utils.CryptoUtils;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RestController
@RequestMapping("/")
public class PolicyRestController extends RestrictedBaseController{

    private static final Logger policyLogger = FlexLogger.getLogger(PolicyRestController.class);

    private static final String model = "model";
    private static final String importDictionary = "import_dictionary";

    private static CommonClassDao commonClassDao;

    public PolicyRestController(){
        //default constructor
    }

    @Autowired
    private PolicyRestController(CommonClassDao commonClassDao){
        PolicyRestController.commonClassDao = commonClassDao;
    }

    public static CommonClassDao getCommonClassDao() {
        return commonClassDao;
    }

    public static void setCommonClassDao(CommonClassDao commonClassDao) {
        PolicyRestController.commonClassDao = commonClassDao;
    }



    @RequestMapping(value={"/policycreation/save_policy"}, method={RequestMethod.POST})
    public void policyCreationController(HttpServletRequest request, HttpServletResponse response) {
        String userId = UserUtils.getUserSession(request).getOrgUserId();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try{
            JsonNode root = mapper.readTree(request.getReader());

            policyLogger.info("****************************************Logging UserID while Create/Update Policy**************************************************");
            policyLogger.info("UserId:  " + userId + "Policy Data Object:  "+ root.get(PolicyController.getPolicydata()).get("policy").toString());
            policyLogger.info("***********************************************************************************************************************************");

            PolicyRestAdapter policyData = mapper.readValue(root.get(PolicyController.getPolicydata()).get("policy").toString(), PolicyRestAdapter.class);

            if("file".equals(root.get(PolicyController.getPolicydata()).get(model).get("type").toString().replace("\"", ""))){
                policyData.setEditPolicy(true);
            }
            if(root.get(PolicyController.getPolicydata()).get(model).get("path").size() != 0){
                String dirName = "";
                for(int i = 0; i < root.get(PolicyController.getPolicydata()).get(model).get("path").size(); i++){
                    dirName = dirName.replace("\"", "") + root.get(PolicyController.getPolicydata()).get(model).get("path").get(i).toString().replace("\"", "") + File.separator;
                }
                if(policyData.isEditPolicy()){
                    policyData.setDomainDir(dirName.substring(0, dirName.lastIndexOf(File.separator)));
                }else{
                    policyData.setDomainDir(dirName + root.get(PolicyController.getPolicydata()).get(model).get("name").toString().replace("\"", ""));
                }
            }else{
                String domain = root.get(PolicyController.getPolicydata()).get(model).get("name").toString();
                if(domain.contains("/")){
                    domain = domain.substring(0, domain.lastIndexOf('/')).replace("/", File.separator);
                }
                domain = domain.replace("\"", "");
                policyData.setDomainDir(domain);
            }

            if(policyData.getConfigPolicyType() != null){
                if("ClosedLoop_Fault".equalsIgnoreCase(policyData.getConfigPolicyType())){
                    policyData = new CreateClosedLoopFaultController().setDataToPolicyRestAdapter(policyData, root);
                }else if("Firewall Config".equalsIgnoreCase(policyData.getConfigPolicyType())){
                    policyData = new CreateFirewallController().setDataToPolicyRestAdapter(policyData);
                }else if("Micro Service".equalsIgnoreCase(policyData.getConfigPolicyType())){
                    policyData = new CreateDcaeMicroServiceController().setDataToPolicyRestAdapter(policyData, root);
                }else if("Optimization".equalsIgnoreCase(policyData.getConfigPolicyType())){
                    policyData = new CreateOptimizationController().setDataToPolicyRestAdapter(policyData, root);
                }
            }

            policyData.setUserId(userId);

            String result;
            String body = PolicyUtils.objectToJsonString(policyData);
            String uri = request.getRequestURI();
            ResponseEntity<?> responseEntity = sendToPAP(body, uri, HttpMethod.POST);
            if(responseEntity != null && responseEntity.getBody().equals(HttpServletResponse.SC_CONFLICT)){
                result = "PolicyExists";
            }else if(responseEntity != null){
                result =  responseEntity.getBody().toString();
                String policyName = responseEntity.getHeaders().get("policyName").get(0);
                if(policyData.isEditPolicy() && "success".equalsIgnoreCase(result)){
                    PolicyNotificationMail email = new PolicyNotificationMail();
                    String mode = "EditPolicy";
                    String watchPolicyName = policyName.replace(".xml", "");
                    String version = watchPolicyName.substring(watchPolicyName.lastIndexOf('.')+1);
                    watchPolicyName = watchPolicyName.substring(0, watchPolicyName.lastIndexOf('.')).replace(".", File.separator);
                    String policyVersionName = watchPolicyName.replace(".", File.separator);
                    watchPolicyName = watchPolicyName + "." + version + ".xml";
                    PolicyVersion entityItem = new PolicyVersion();
                    entityItem.setPolicyName(policyVersionName);
                    entityItem.setActiveVersion(Integer.parseInt(version));
                    entityItem.setModifiedBy(userId);
                    email.sendMail(entityItem, watchPolicyName, mode, commonClassDao);
                }
            }else{
                result =  "Response is null from PAP";
            }

            response.setCharacterEncoding(PolicyController.getCharacterencoding());
            response.setContentType(PolicyController.getContenttype());
            request.setCharacterEncoding(PolicyController.getCharacterencoding());

            PrintWriter out = response.getWriter();
            String responseString = mapper.writeValueAsString(result);
            JSONObject j = new JSONObject("{policyData: " + responseString + "}");
            out.write(j.toString());
        }catch(Exception e){
            policyLogger.error("Exception Occured while saving policy" , e);
        }
    }


    private ResponseEntity<?> sendToPAP(String body, String requestURI, HttpMethod method){
        String papUrl = PolicyController.getPapUrl();
        String papID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
        String papPass = CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS));

        Base64.Encoder encoder = Base64.getEncoder();
        String encoding = encoder.encodeToString((papID+":"+papPass).getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoding);
        headers.set("Content-Type", PolicyController.getContenttype());

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<?> result = null;
        HttpClientErrorException exception = null;
        String uri = requestURI;
        if(uri.startsWith("/")){
            uri = uri.substring(uri.indexOf('/')+1);
        }
        uri = "onap" + uri.substring(uri.indexOf('/'));
        try{
            result = restTemplate.exchange(papUrl + uri, method, requestEntity, String.class);
        }catch(Exception e){
            policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while connecting to " + papUrl, e);
            exception = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            if("409 Conflict".equals(e.getMessage())){
                return ResponseEntity.ok(HttpServletResponse.SC_CONFLICT);
            }
        }
        if(exception != null && exception.getStatusCode()!=null){
            if(exception.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
                String message = XACMLErrorConstants.ERROR_PERMISSIONS +":"+exception.getStatusCode()+":" + "ERROR_AUTH_GET_PERM" ;
                policyLogger.error(message);
            }
            if(exception.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                String message = XACMLErrorConstants.ERROR_DATA_ISSUE + ":"+exception.getStatusCode()+":" + exception.getResponseBodyAsString();
                policyLogger.error(message);
            }
            if(exception.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while connecting to " + papUrl + exception;
                policyLogger.error(message);
            }
            String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + ":"+exception.getStatusCode()+":" + exception.getResponseBodyAsString();
            policyLogger.error(message);
        }
        return result;
    }

    private String callPAP(HttpServletRequest request , String method, String uriValue){
        String uri = uriValue;
        String boundary = null;
        String papUrl = PolicyController.getPapUrl();
        String papID = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_USERID);
        String papPass = CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PASS));

        Base64.Encoder encoder = Base64.getEncoder();
        String encoding = encoder.encodeToString((papID+":"+papPass).getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoding);
        headers.set("Content-Type", PolicyController.getContenttype());


        HttpURLConnection connection = null;
        List<FileItem> items;
        FileItem item = null;
        File file = null;
        if(uri.contains(importDictionary)){
            try {
                items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                item = items.get(0);
                file = new File(item.getName());
                String newFile = file.toString();
                uri = uri +"&dictionaryName="+newFile;
            } catch (Exception e2) {
                policyLogger.error("Exception Occured while calling PAP with import dictionary request"+e2);
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

                if(!(uri.endsWith("set_BRMSParamData") || uri.contains(importDictionary))){
                    connection.setRequestProperty("Content-Type",PolicyController.getContenttype());
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JsonNode root = getJsonNode(request, mapper);

                    ObjectMapper mapper1 = new ObjectMapper();
                    mapper1.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

                    Object obj = mapper1.treeToValue(root, Object.class);
                    String json = mapper1.writeValueAsString(obj);

                    Object content =  new ByteArrayInputStream(json.getBytes());

                    if (content instanceof InputStream) {
                        // send current configuration
                        try (OutputStream os = connection.getOutputStream()) {
                            int count = IOUtils.copy((InputStream) content, os);
                            if (policyLogger.isDebugEnabled()) {
                                policyLogger.debug("copied to output, bytes=" + count);
                            }
                        }
                    }
                }else{
                    if(uri.endsWith("set_BRMSParamData")){
                        connection.setRequestProperty("Content-Type",PolicyController.getContenttype());
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
            return doConnect(connection);
        } catch (Exception e) {
            policyLogger.error("Exception Occured"+e);
        }finally{
            if(file != null && file.exists() && file.delete()){
                policyLogger.info("File Deleted Successfully");
            }
            if (connection != null) {
                try {
                    // For some reason trying to get the inputStream from the connection
                    // throws an exception rather than returning null when the InputStream does not exist.
                    InputStream is = connection.getInputStream();
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ex) {
                    policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to close connection: " + ex, ex);
                }
                connection.disconnect();
            }
        }
        return null;
    }

    private JsonNode getJsonNode(HttpServletRequest request, ObjectMapper mapper) {
        JsonNode root = null;
        try {
            root = mapper.readTree(request.getReader());
        }catch (Exception e1) {
            policyLogger.error("Exception Occured while calling PAP"+e1);
        }
        return root;
    }

    private String doConnect(final HttpURLConnection connection) throws IOException{
        connection.connect();
        int responseCode = connection.getResponseCode();
        if(responseCode == 200){
            // get the response content into a String
            String responseJson = null;
            // read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
            try(java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream())) {
                scanner.useDelimiter("\\A");
                responseJson = scanner.hasNext() ? scanner.next() : "";
            } catch (Exception e){
                //Reason for rethrowing the exception is if any exception occurs during reading of inputsteam
                //then the exception handling is done by the outer block without returning the response immediately
                //Also finally block is existing only in outer block and not here so all exception handling is
                //done in only one place
                policyLogger.error("Exception Occured"+e);
                throw e;
            }

            policyLogger.info("JSON response from PAP: " + responseJson);
            return responseJson;
        }
        return null;
    }

    @RequestMapping(value={"/getDictionary/*"}, method={RequestMethod.GET})
    public void getDictionaryController(HttpServletRequest request, HttpServletResponse response){
        String uri = request.getRequestURI().replace("/getDictionary", "");
        String body;
        ResponseEntity<?> responseEntity = sendToPAP(null, uri, HttpMethod.GET);
        if(responseEntity != null){
            body = responseEntity.getBody().toString();
        }else{
            body = "";
        }
        try {
            response.getWriter().write(body);
        } catch (IOException e) {
            policyLogger.error("Exception occured while getting Dictionary entries", e);
        }
    }

    @RequestMapping(value={"/saveDictionary/*/*"}, method={RequestMethod.POST})
    public void saveDictionaryController(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String userId = "";
        String uri = request.getRequestURI().replace("/saveDictionary", "");
        if(uri.startsWith("/")){
            uri = uri.substring(uri.indexOf('/')+1);
        }
        uri = "/onap" + uri.substring(uri.indexOf('/'));
        if(uri.contains(importDictionary)){
            userId = UserUtils.getUserSession(request).getOrgUserId();
            uri = uri+ "?userId=" +userId;
        }

        policyLogger.info("****************************************Logging UserID while Saving Dictionary*****************************************************");
        policyLogger.info("UserId:  " + userId);
        policyLogger.info("***********************************************************************************************************************************");

        String body = callPAP(request, "POST", uri.replaceFirst("/", "").trim());
        if(body != null && !body.isEmpty()){
            response.getWriter().write(body);
        }else{
            response.getWriter().write("Failed");
        }
    }

    @RequestMapping(value={"/deleteDictionary/*/*"}, method={RequestMethod.POST})
    public void deletetDictionaryController(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI().replace("/deleteDictionary", "");
        if(uri.startsWith("/")){
            uri = uri.substring(uri.indexOf('/')+1);
        }
        uri = "/onap" + uri.substring(uri.indexOf('/'));

        String userId = UserUtils.getUserSession(request).getOrgUserId();
        policyLogger.info("****************************************Logging UserID while Deleting Dictionary*****************************************************");
        policyLogger.info("UserId:  " + userId);
        policyLogger.info("*************************************************************************************************************************************");

        String body = callPAP(request, "POST", uri.replaceFirst("/", "").trim());
        if(body != null && !body.isEmpty()){
            response.getWriter().write(body);
        }else{
            response.getWriter().write("Failed");
        }
    }

    @RequestMapping(value={"/searchDictionary"}, method={RequestMethod.POST})
    public ModelAndView searchDictionaryController(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object resultList;
        String uri = request.getRequestURI();
        if(uri.startsWith("/")){
            uri = uri.substring(uri.indexOf('/')+1);
        }
        uri = "/onap" + uri.substring(uri.indexOf('/'));
        try{
            String body = callPAP(request, "POST", uri.replaceFirst("/", "").trim());
            if(body.contains("CouldNotConnectException")){
                List<String> data = new ArrayList<>();
                data.add("Elastic Search Server is down");
                resultList = data;
            }else{
                JSONObject json = new JSONObject(body);
                resultList = json.get("policyresult");
            }
        }catch(Exception e){
            policyLogger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Exception Occured while querying Elastic Search: " + e);
            List<String> data = new ArrayList<>();
            data.add("Elastic Search Server is down");
            resultList = data;
        }

        response.setCharacterEncoding(PolicyController.getCharacterencoding());
        response.setContentType(PolicyController.getContenttype());
        PrintWriter out = response.getWriter();
        JSONObject j = new JSONObject("{result: " + resultList + "}");
        out.write(j.toString());
        return null;
    }

    @RequestMapping(value={"/searchPolicy"}, method={RequestMethod.POST})
    public ModelAndView searchPolicy(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Object resultList;
        String uri = request.getRequestURI()+"?action=search";
        if(uri.startsWith("/")){
            uri = uri.substring(uri.indexOf('/')+1);
        }
        uri = "/onap" + uri.substring(uri.indexOf('/'));
        String body = callPAP(request, "POST", uri.replaceFirst("/", "").trim());

        JSONObject json = new JSONObject(body);
        try{
            resultList = json.get("policyresult");
        }catch(Exception e){
            List<String> data = new ArrayList<>();
            resultList = json.get("data");
            data.add("Exception");
            data.add(resultList.toString());
            resultList = data;
            policyLogger.error("Exception Occured while searching for Policy in Elastic Database" +e);
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
        callPAP(null, "POST", uri.trim());
    }

    public String notifyOtherPAPSToUpdateConfigurations(String mode, String newName, String oldName){
        String uri = "onap/notifyOtherPAPs?action="+mode+"&newPolicyName="+newName+"&oldPolicyName="+oldName+"";
        return callPAP(null, "POST", uri.trim());
    }

}
