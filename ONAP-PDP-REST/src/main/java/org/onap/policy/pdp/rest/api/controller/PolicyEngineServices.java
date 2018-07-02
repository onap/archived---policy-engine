/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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
package org.onap.policy.pdp.rest.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.DecisionRequestParameters;
import org.onap.policy.api.DecisionResponse;
import org.onap.policy.api.DeletePolicyParameters;
import org.onap.policy.api.DictionaryParameters;
import org.onap.policy.api.DictionaryResponse;
import org.onap.policy.api.EventRequestParameters;
import org.onap.policy.api.MetricsResponse;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PolicyResponse;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.XACMLPdpServlet;
import org.onap.policy.pdp.rest.api.models.ConfigFirewallPolicyAPIRequest;
import org.onap.policy.pdp.rest.api.models.ConfigNameRequest;
import org.onap.policy.pdp.rest.api.models.ConfigPolicyAPIRequest;
import org.onap.policy.pdp.rest.api.models.PolicyConfig;
import org.onap.policy.pdp.rest.api.services.CreateUpdateConfigPolicyService;
import org.onap.policy.pdp.rest.api.services.CreateUpdateDictionaryService;
import org.onap.policy.pdp.rest.api.services.CreateUpdateFirewallPolicyService;
import org.onap.policy.pdp.rest.api.services.CreateUpdatePolicyService;
import org.onap.policy.pdp.rest.api.services.DeletePolicyService;
import org.onap.policy.pdp.rest.api.services.GetConfigService;
import org.onap.policy.pdp.rest.api.services.GetDecisionService;
import org.onap.policy.pdp.rest.api.services.GetDictionaryService;
import org.onap.policy.pdp.rest.api.services.GetMetricsService;
import org.onap.policy.pdp.rest.api.services.ListConfigService;
import org.onap.policy.pdp.rest.api.services.NotificationService;
import org.onap.policy.pdp.rest.api.services.NotificationService.NotificationServiceType;
import org.onap.policy.pdp.rest.api.services.PolicyEngineImportService;
import org.onap.policy.pdp.rest.api.services.PushPolicyService;
import org.onap.policy.pdp.rest.api.services.SendEventService;
import org.onap.policy.pdp.rest.config.PDPApiAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(value = "Policy Engine Services")
@RequestMapping("/")
public class PolicyEngineServices {
	private static Logger logger = FlexLogger.getLogger(PolicyEngineServices.class.getName());
	private static final String NOTIFICATIONPERM = "notification"; 
	
	private final AtomicLong configCounter = new AtomicLong();
	private final AtomicLong configNameCounter = new AtomicLong();
	private final AtomicLong eventCounter = new AtomicLong();
	private final AtomicLong decisionCounter = new AtomicLong();
	private final AtomicLong pushCounter = new AtomicLong();
	private final AtomicLong deleteCounter = new AtomicLong();
	private final AtomicLong createPolicyCounter = new AtomicLong();
	private final AtomicLong updatePolicyCounter = new AtomicLong();
	private final AtomicLong createDictionaryCounter = new AtomicLong();
	private final AtomicLong updateDictionaryCounter = new AtomicLong();
	private final AtomicLong getDictionaryCounter = new AtomicLong();
	private final AtomicLong policyEngineImportCounter = new AtomicLong();
	private final AtomicLong deprecatedCounter = new AtomicLong();
	private final AtomicLong metricCounter = new AtomicLong();
	private final AtomicLong notificationCounter = new AtomicLong();

    /**
     * Gets the config.
     *
     * @param configRequestParameters the config request parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the config
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the configuration from the PolicyDecisionPoint(PDP)")
    @RequestMapping(value = "/getConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Collection<PolicyConfig>> getConfig(
            @RequestBody ConfigRequestParameters configRequestParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        Collection<PolicyConfig> policyConfig = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: getConfig - " + configRequestParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "getConfig", logUserInfo)) {
            GetConfigService getConfigService = new GetConfigService(configRequestParameters, requestID);
            policyConfig = getConfigService.getResult();
            status = getConfigService.getResponseCode();
        }
        configCounter.incrementAndGet();
        return new ResponseEntity<>(policyConfig, status);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the configuration from the PolicyDecisionPoint(PDP) using PolicyName")
    @RequestMapping(value = "/getConfigByPolicyName", method = RequestMethod.POST)
    @ResponseBody
    @Deprecated
    public ResponseEntity<Collection<PolicyConfig>> getConfigByPolicyName(
            @RequestBody ConfigNameRequest configNameRequest,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        Collection<PolicyConfig> policyConfig = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: getConfigByPolicyName";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "getConfigByPolicyName", logUserInfo)) {
            ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
            configRequestParameters.setPolicyName(configNameRequest.getPolicyName());
            GetConfigService getConfigService = new GetConfigService(configRequestParameters, requestID);
            policyConfig = getConfigService.getResult();
            status = getConfigService.getResponseCode();
        }
        configNameCounter.incrementAndGet();
        return new ResponseEntity<>(policyConfig, status);
    }

    /**
     * List config.
     *
     * @param configRequestParameters the config request parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the list of configuration policies from the PDP")
    @RequestMapping(value = "/listConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Collection<String>> listConfig(@RequestBody ConfigRequestParameters configRequestParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        Collection<String> results = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: listConfig - " + configRequestParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "listConfig", logUserInfo)) {
            ListConfigService listConfigService = new ListConfigService(configRequestParameters, requestID);
            results = listConfigService.getResult();
            status = listConfigService.getResponseCode();
        }
        configCounter.incrementAndGet();
        return new ResponseEntity<>(results, status);
    }

    /**
     * Gets the metrics.
     *
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the metrics
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the policy metrics from the PolicyAccessPoint(PAP)")
    @RequestMapping(value = "/getMetrics", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<MetricsResponse> getMetrics(
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        MetricsResponse response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: getMetrics";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "getMetrics", logUserInfo)) {
            GetMetricsService getMetricsService = new GetMetricsService(requestID);
            response = getMetricsService.getResult();
            status = getMetricsService.getResponseCode();
        }
        metricCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Gets the notification.
     *
     * @param notificationTopic the notification topic
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the notification
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Registers DMaaP Topic to recieve notification from Policy Engine")
    @RequestMapping(value = "/getNotification", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> getNotification(@RequestBody String notificationTopic,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String policyResponse = "Error Unauthorized to use Notification Service.";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: getNotification for Topic: " + notificationTopic;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, NOTIFICATIONPERM, logUserInfo)) {
            NotificationService notificationService =
                    new NotificationService(notificationTopic, requestID, NotificationServiceType.ADD);
            policyResponse = notificationService.getResult();
            status = notificationService.getResponseCode();
        }
        notificationCounter.incrementAndGet();
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Stop notification.
     *
     * @param notificationTopic the notification topic
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "De-Registers DMaaP Topic to stop recieving notifications from Policy Engine")
    @RequestMapping(value = "/stopNotification", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> stopNotification(@RequestBody String notificationTopic,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String policyResponse = "Error Unauthorized to use Notification Service.";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: stopNotification for Topic: " + notificationTopic;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, NOTIFICATIONPERM, logUserInfo)) {
            NotificationService notificationService =
                    new NotificationService(notificationTopic, requestID, NotificationServiceType.REMOVE);
            policyResponse = notificationService.getResult();
            status = notificationService.getResponseCode();
        }
        notificationCounter.incrementAndGet();
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Send heartbeat.
     *
     * @param notificationTopic the notification topic
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(
            value = "Sends Heartbeat to DMaaP Topic Registry to continue recieving notifications from Policy Engine")
    @RequestMapping(value = "/sendHeartbeat", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sendHeartbeat(@RequestBody String notificationTopic,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String policyResponse = "Error Unauthorized to use Heartbeat Service.";
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: sendHeartbeat";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, NOTIFICATIONPERM, logUserInfo)) {
            NotificationService notificationService =
                    new NotificationService(notificationTopic, requestID, NotificationServiceType.HB);
            policyResponse = notificationService.getResult();
            status = notificationService.getResponseCode();
        }
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Send event.
     *
     * @param eventRequestParameters the event request parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Sends the Events specified to the Policy Engine")
    @RequestMapping(value = "/sendEvent", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Collection<PolicyResponse>> sendEvent(
            @RequestBody EventRequestParameters eventRequestParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        Collection<PolicyResponse> policyResponse = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo =
                "Operation: sendEvent with EventAttributes - " + eventRequestParameters.getEventAttributes();
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "sendEvent", logUserInfo)) {
            SendEventService sendEventService = new SendEventService(eventRequestParameters, requestID);
            policyResponse = sendEventService.getResult();
            status = sendEventService.getResponseCode();
        }
        eventCounter.incrementAndGet();
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Gets the decision.
     *
     * @param decisionRequestParameters the decision request parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the decision
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the Decision using specified decision parameters")
    @RequestMapping(value = "/getDecision", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<DecisionResponse> getDecision(
            @RequestBody DecisionRequestParameters decisionRequestParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        DecisionResponse decisionResponse = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: getDecision - " + decisionRequestParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "getDecision", logUserInfo)) {
            GetDecisionService getDecisionService = new GetDecisionService(decisionRequestParameters, requestID);
            decisionResponse = getDecisionService.getResult();
            status = getDecisionService.getResponseCode();
        }
        decisionCounter.incrementAndGet();
        return new ResponseEntity<>(decisionResponse, status);
    }

    /**
     * Push policy.
     *
     * @param pushPolicyParameters the push policy parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Pushes the specified policy to the PDP Group.")
    @RequestMapping(value = "/pushPolicy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> pushPolicy(@RequestBody PushPolicyParameters pushPolicyParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: pushPolicy - " + pushPolicyParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "pushPolicy", logUserInfo)) {
            PushPolicyService pushPolicyService;
            
                pushPolicyService = new PushPolicyService(pushPolicyParameters, requestID);
                response = pushPolicyService.getResult();
                status = pushPolicyService.getResponseCode();
            

        }
        pushCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Delete policy.
     *
     * @param deletePolicyParameters the delete policy parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Deletes the specified policy from the PDP Group or PAP.")
    @RequestMapping(value = "/deletePolicy", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deletePolicy(@RequestBody DeletePolicyParameters deletePolicyParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: deletePolicy - " + deletePolicyParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "deletePolicy", logUserInfo)) {

               DeletePolicyService deletePolicyService =  new DeletePolicyService(deletePolicyParameters, requestID);
                response = deletePolicyService.getResult();
                status = deletePolicyService.getResponseCode();
            
        }
        deleteCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates the policy.
     *
     * @param policyParameters the policy parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Policy based on given Policy Parameters.")
    @RequestMapping(value = "/createPolicy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> createPolicy(@RequestBody PolicyParameters policyParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: createPolicy for " + policyParameters.toString();
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "createPolicy", logUserInfo)) {
            CreateUpdatePolicyService createPolicyService;
            try {
                createPolicyService = (CreateUpdatePolicyService) XACMLPdpServlet.getCreateUpdatePolicyConstructor()
                        .newInstance(policyParameters, requestID, false);
                response = createPolicyService.getResult();
                status = createPolicyService.getResponseCode();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
                response = "Problem with CreateUpdate Policy Service. ";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        createPolicyCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Update policy.
     *
     * @param policyParameters the policy parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Policy based on given Policy Parameters.")
    @RequestMapping(value = "/updatePolicy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updatePolicy(@RequestBody PolicyParameters policyParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: updatePolicy for " + policyParameters.toString();
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "updatePolicy", logUserInfo)) {
            CreateUpdatePolicyService updatePolicyService;
            try {
                updatePolicyService = (CreateUpdatePolicyService) XACMLPdpServlet.getCreateUpdatePolicyConstructor()
                        .newInstance(policyParameters, requestID, true);
                response = updatePolicyService.getResult();
                status = updatePolicyService.getResponseCode();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
                response = "Problem with CreateUpdate Policy Service. ";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        updatePolicyCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates the dictionary item.
     *
     * @param dictionaryParameters the dictionary parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Dictionary Item for a specific dictionary based on given Parameters.")
    @RequestMapping(value = "/createDictionaryItem", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> createDictionaryItem(@RequestBody DictionaryParameters dictionaryParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: createDictionaryItem - " + dictionaryParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "createDictionary", logUserInfo)) {
            CreateUpdateDictionaryService createDictionaryService =
                    new CreateUpdateDictionaryService(dictionaryParameters, requestID, false);
            response = createDictionaryService.getResult();
            status = createDictionaryService.getResponseCode();
        }
        createDictionaryCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Update dictionary item.
     *
     * @param dictionaryParameters the dictionary parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Dictionary Item for a specific dictionary based on given Parameters.")
    @RequestMapping(value = "/updateDictionaryItem", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateDictionaryItem(@RequestBody DictionaryParameters dictionaryParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: updateDictionaryItem - " + dictionaryParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "updateDictionary", logUserInfo)) {
            CreateUpdateDictionaryService updateDictionaryService =
                    new CreateUpdateDictionaryService(dictionaryParameters, requestID, true);
            response = updateDictionaryService.getResult();
            status = updateDictionaryService.getResponseCode();
        }
        updateDictionaryCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Gets the dictionary items.
     *
     * @param dictionaryParameters the dictionary parameters
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the dictionary items
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the dictionary items from the PAP")
    @RequestMapping(value = "/getDictionaryItems", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<DictionaryResponse> getDictionaryItems(@RequestBody DictionaryParameters dictionaryParameters,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        DictionaryResponse dictionaryResponse = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: getDictionaryItems - " + dictionaryParameters;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "getDictionary", logUserInfo)) {
            GetDictionaryService getDictionaryService = new GetDictionaryService(dictionaryParameters, requestID);
                dictionaryResponse = getDictionaryService.getResult();
                status = getDictionaryService.getResponseCode();
           
        }
        getDictionaryCounter.incrementAndGet();
        return new ResponseEntity<>(dictionaryResponse, status);

    }

    /**
     * Policy engine import.
     *
     * @param importParametersJson the import parameters json
     * @param file the file
     * @param clientEncoding the client encoding
     * @param requestID the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(
            value = "Imports Policy based on the parameters which represent the service used to create a policy Service.")
    @RequestMapping(value = "/policyEngineImport", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> policyEngineImport(@RequestParam("importParametersJson") String importParametersJson,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: policyEngineImport with importParametersJson: " + importParametersJson;
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "policyEngineImport", logUserInfo)) {
            PolicyEngineImportService policyEngineImportService =
                    new PolicyEngineImportService(importParametersJson, file, requestID);
            response = policyEngineImportService.getResult();
            status = policyEngineImportService.getResponseCode();
        }
        policyEngineImportCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Config Policy based on given Policy Parameters.")
    @RequestMapping(value = "/createConfig", method = RequestMethod.PUT)
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> createConfig(@RequestBody ConfigPolicyAPIRequest configPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: createConfig";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "createPolicy", logUserInfo)) {
            CreateUpdateConfigPolicyService createPolicyService =
                    new CreateUpdateConfigPolicyService(configPolicyAPIRequest, requestID, false);
            response = createPolicyService.getResult();
            status = createPolicyService.getResponseCode();
        }
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Config Policy based on given Policy Parameters.")
    @RequestMapping(value = "/updateConfig", method = RequestMethod.PUT)
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> updateConfig(@RequestBody ConfigPolicyAPIRequest configPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: updateConfig";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "updatePolicy", logUserInfo)) {
            CreateUpdateConfigPolicyService updatePolicyService =
                    new CreateUpdateConfigPolicyService(configPolicyAPIRequest, requestID, true);
            response = updatePolicyService.getResult();
            status = updatePolicyService.getResponseCode();
        }
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Config Firewall Policy")
    @RequestMapping(value = "/createFirewallConfig", method = RequestMethod.PUT)
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> createFirewallConfig(
            @RequestBody ConfigFirewallPolicyAPIRequest configFirewallPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: createFirewallConfig";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "createPolicy", logUserInfo)) {
            CreateUpdateFirewallPolicyService createFirewallPolicyService =
                    new CreateUpdateFirewallPolicyService(configFirewallPolicyAPIRequest, requestID, false);
            response = createFirewallPolicyService.getResult();
            status = createFirewallPolicyService.getResponseCode();
        }
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Config Firewall Policy")
    @RequestMapping(value = "/updateFirewallConfig", method = RequestMethod.PUT)
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> updateFirewallConfig(
            @RequestBody ConfigFirewallPolicyAPIRequest configFirewallPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = true) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestID) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String logUserInfo = "Operation: updateFirewallConfig";
        // Check Permissions.
        if (PDPApiAuth.checkPermissions(clientEncoding, requestID, "updatePolicy", logUserInfo)) {
            CreateUpdateFirewallPolicyService updateFirewallPolicyService =
                    new CreateUpdateFirewallPolicyService(configFirewallPolicyAPIRequest, requestID, true);
            response = updateFirewallPolicyService.getResult();
            status = updateFirewallPolicyService.getResponseCode();
        }
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

	

        /**
        * Gets the count.
        *
        * @return the count
        */
        @ApiOperation(value = "Gets the API Services usage Information")
	@ApiIgnore
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public String getCount() {
		return "Total Config Calls : " + configCounter + "\n"
				+ "Total Config calls made using Policy File Name: "
				+ configNameCounter + "\n" + "Total Event Calls : "
				+ eventCounter + "\nTotal Decision Calls: " + decisionCounter
				+ "\nTotal Push policy Calls: " + pushCounter
				+ "\nTotal Delete Policy Calls: " + deleteCounter
				+ "\nTotal Create Policy Calls: " + createPolicyCounter
				+ "\nTotal Update Policy Calls: " + updatePolicyCounter
				+ "\nTotal Create Dictionary Calls: " + createDictionaryCounter
				+ "\nTotal Update Dictionary Calls: " + updateDictionaryCounter
				+ "\nTotal Get Dictionary Calls: " + getDictionaryCounter
				+ "\nTotal PolicyEngine Import Calls: "
				+ policyEngineImportCounter
				+ "\nTotal Deprecated Policy Calls: " + deprecatedCounter
				+ "\nTotal Metrics Calls:" + metricCounter
				+ "\nTotal Notification Calls:" + notificationCounter;
	}

    /**
     * Message not readable exception handler.
     *
     * @param req the req
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<String> messageNotReadableExceptionHandler(HttpServletRequest req,
            HttpMessageNotReadableException exception) {
        logger.error("Request not readable: {}", exception);
        StringBuilder message = new StringBuilder();
        message.append(exception.getMessage());
        if (exception.getCause() != null) {
            message.append(" Reason Caused: " + exception.getCause().getMessage());
        }
        return new ResponseEntity<>(message.toString(), HttpStatus.BAD_REQUEST);
    }
}
