/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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
import org.onap.policy.api.ConfigNameRequest;
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
import org.onap.policy.pdp.rest.api.services.ListPolicyService;
import org.onap.policy.pdp.rest.api.services.NotificationService;
import org.onap.policy.pdp.rest.api.services.NotificationService.NotificationServiceType;
import org.onap.policy.pdp.rest.api.services.PolicyEngineImportService;
import org.onap.policy.pdp.rest.api.services.PushPolicyService;
import org.onap.policy.pdp.rest.api.services.SendEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * @param requestId the request ID
     * @return the config
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the configuration from the PolicyDecisionPoint(PDP)")
    @PostMapping(value = "/getConfig")
    @ResponseBody
    public ResponseEntity<Collection<PolicyConfig>> getConfig(
            @RequestBody ConfigRequestParameters configRequestParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        Collection<PolicyConfig> policyConfig = null;
        GetConfigService getConfigService = new GetConfigService(configRequestParameters, requestId);
        policyConfig = getConfigService.getResult();
        HttpStatus status = getConfigService.getResponseCode();
        configCounter.incrementAndGet();
        return new ResponseEntity<>(policyConfig, status);
    }

    /**
     * Gets the config by policy name.
     *
     * @param configNameRequest the config name request
     * @param clientEncoding the client encoding
     * @param requestId the request id
     * @return the config by policy name
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the configuration from the PolicyDecisionPoint(PDP) using PolicyName")
    @PostMapping(value = "/getConfigByPolicyName")
    @ResponseBody
    @Deprecated
    public ResponseEntity<Collection<PolicyConfig>> getConfigByPolicyName(
            @RequestBody ConfigNameRequest configNameRequest,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        Collection<PolicyConfig> policyConfig = null;
        logger.info("Operation: getConfigByPolicyName - " + configNameRequest.getPolicyName());
        ConfigRequestParameters configRequestParameters = new ConfigRequestParameters();
        configRequestParameters.setPolicyName(configNameRequest.getPolicyName());

        GetConfigService getConfigService = new GetConfigService(configRequestParameters, requestId);
        policyConfig = getConfigService.getResult();
        HttpStatus status = getConfigService.getResponseCode();
        configNameCounter.incrementAndGet();
        return new ResponseEntity<>(policyConfig, status);
    }


    /**
     * List config.
     *
     * @param configRequestParameters the config request parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the list of configuration policies from the PDP")
    @PostMapping(value = "/listConfig")
    @ResponseBody
    public ResponseEntity<Collection<String>> listConfig(@RequestBody ConfigRequestParameters configRequestParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        Collection<String> results = null;
        logger.info("Operation: listConfig - " + configRequestParameters);
        ListConfigService listConfigService = new ListConfigService(configRequestParameters, requestId);
        results = listConfigService.getResult();
        HttpStatus status = listConfigService.getResponseCode();
        configCounter.incrementAndGet();
        return new ResponseEntity<>(results, status);
    }

    /**
     * List Policy.
     *
     * @param ConfigNameRequest the config request parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the list of policies from the PDP")
    @PostMapping(value = "/listPolicy")
    @ResponseBody
    public ResponseEntity<Collection<String>> listPolicy(@RequestBody ConfigNameRequest configNameRequest,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        Collection<String> results = null;
        logger.info("Operation: listPolicy - " + configNameRequest);
        ListPolicyService listPolicyService = new ListPolicyService(configNameRequest);
        results = listPolicyService.getResult();
        HttpStatus status = listPolicyService.getResponseCode();
        configCounter.incrementAndGet();
        return new ResponseEntity<>(results, status);
    }

    /**
     * Gets the metrics.
     *
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the metrics
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the policy metrics from the PolicyAccessPoint(PAP)")
    @GetMapping(value = "/getMetrics")
    @ResponseBody
    public ResponseEntity<MetricsResponse> getMetrics(
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        MetricsResponse response = null;
        logger.info("Operation: getMetrics");

        GetMetricsService getMetricsService = new GetMetricsService(requestId);
        response = getMetricsService.getResult();
        HttpStatus status = getMetricsService.getResponseCode();
        metricCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Gets the notification.
     *
     * @param notificationTopic the notification topic
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the notification
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Registers DMaaP Topic to recieve notification from Policy Engine")
    @PostMapping(value = "/getNotification")
    @ResponseBody
    public ResponseEntity<String> getNotification(@RequestBody String notificationTopic,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: getNotification for Topic: " + notificationTopic);

        NotificationService notificationService =
                new NotificationService(notificationTopic, requestId, NotificationServiceType.ADD);
        String policyResponse = notificationService.getResult();
        HttpStatus status = notificationService.getResponseCode();
        notificationCounter.incrementAndGet();
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Stop notification.
     *
     * @param notificationTopic the notification topic
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "De-Registers DMaaP Topic to stop recieving notifications from Policy Engine")
    @PostMapping(value = "/stopNotification")
    @ResponseBody
    public ResponseEntity<String> stopNotification(@RequestBody String notificationTopic,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: stopNotification for Topic: " + notificationTopic);

        NotificationService notificationService =
                new NotificationService(notificationTopic, requestId, NotificationServiceType.REMOVE);
        String policyResponse = notificationService.getResult();
        HttpStatus status = notificationService.getResponseCode();

        notificationCounter.incrementAndGet();
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Send heartbeat.
     *
     * @param notificationTopic the notification topic
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(
            value = "Sends Heartbeat to DMaaP Topic Registry to continue recieving notifications from Policy Engine")
    @PostMapping(value = "/sendHeartbeat")
    @ResponseBody
    public ResponseEntity<String> sendHeartbeat(@RequestBody String notificationTopic,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: sendHeartbeat for topic - " + notificationTopic);

        NotificationService notificationService =
                new NotificationService(notificationTopic, requestId, NotificationServiceType.HB);
        String policyResponse = notificationService.getResult();
        HttpStatus status = notificationService.getResponseCode();

        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Send event.
     *
     * @param eventRequestParameters the event request parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Sends the Events specified to the Policy Engine")
    @PostMapping(value = "/sendEvent")
    @ResponseBody
    public ResponseEntity<Collection<PolicyResponse>> sendEvent(
            @RequestBody EventRequestParameters eventRequestParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        Collection<PolicyResponse> policyResponse = null;
        logger.info("Operation: sendEvent with EventAttributes - " + eventRequestParameters.getEventAttributes());

        SendEventService sendEventService = new SendEventService(eventRequestParameters, requestId);
        policyResponse = sendEventService.getResult();
        HttpStatus status = sendEventService.getResponseCode();
        eventCounter.incrementAndGet();
        return new ResponseEntity<>(policyResponse, status);
    }

    /**
     * Gets the decision.
     *
     * @param decisionRequestParameters the decision request parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the decision
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the Decision using specified decision parameters")
    @PostMapping(value = "/getDecision")
    @ResponseBody
    public ResponseEntity<DecisionResponse> getDecision(
            @RequestBody DecisionRequestParameters decisionRequestParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        DecisionResponse decisionResponse = null;

        GetDecisionService getDecisionService = new GetDecisionService(decisionRequestParameters, requestId);
        decisionResponse = getDecisionService.getResult();
        HttpStatus status = getDecisionService.getResponseCode();
        decisionCounter.incrementAndGet();
        return new ResponseEntity<>(decisionResponse, status);
    }

    /**
     * Push policy.
     *
     * @param pushPolicyParameters the push policy parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Pushes the specified policy to the PDP Group.")
    @PutMapping(value = "/pushPolicy")
    @ResponseBody
    public ResponseEntity<String> pushPolicy(@RequestBody PushPolicyParameters pushPolicyParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestAttribute(name = "Mechid") String mechId,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        String response = null;
        logger.info("Operation: pushPolicy - " + pushPolicyParameters + ", Mechid - " + mechId);
        PushPolicyService pushPolicyService = new PushPolicyService(pushPolicyParameters, requestId);
        response = pushPolicyService.getResult();
        HttpStatus status = pushPolicyService.getResponseCode();
        pushCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Delete policy.
     *
     * @param deletePolicyParameters the delete policy parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Deletes the specified policy from the PDP Group or PAP.")
    @DeleteMapping(value = "/deletePolicy")
    @ResponseBody
    public ResponseEntity<String> deletePolicy(@RequestBody DeletePolicyParameters deletePolicyParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestAttribute(name = "Mechid") String mechId,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        String response = null;
        logger.info("Operation: deletePolicy - " + deletePolicyParameters + ", Mechid - " + mechId);
        DeletePolicyService deletePolicyService = new DeletePolicyService(deletePolicyParameters, requestId);
        response = deletePolicyService.getResult();
        HttpStatus status = deletePolicyService.getResponseCode();
        deleteCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates the policy.
     *
     * @param policyParameters the policy parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Policy based on given Policy Parameters.")
    @PutMapping(value = "/createPolicy")
    @ResponseBody
    public ResponseEntity<String> createPolicy(@RequestBody PolicyParameters policyParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestAttribute(name = "Mechid") String mechId,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        logger.info("Operation: createPolicy for " + policyParameters.toString() + ", Mechid : " + mechId);

        CreateUpdatePolicyService createPolicyService;
        try {
            createPolicyService = (CreateUpdatePolicyService) XACMLPdpServlet.getCreateUpdatePolicyConstructor()
                    .newInstance(policyParameters, requestId, false);
            response = createPolicyService.getResult();
            status = createPolicyService.getResponseCode();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            response = "Problem with CreateUpdate Policy Service. ";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        createPolicyCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Update policy.
     *
     * @param policyParameters the policy parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Policy based on given Policy Parameters.")
    @PutMapping(value = "/updatePolicy")
    @ResponseBody
    public ResponseEntity<String> updatePolicy(@RequestBody PolicyParameters policyParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestAttribute(name = "Mechid") String mechId,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        String response = null;
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        logger.info("Operation: updatePolicy for " + policyParameters.toString() + ", MechId - " + mechId);

        CreateUpdatePolicyService updatePolicyService;
        try {
            updatePolicyService = (CreateUpdatePolicyService) XACMLPdpServlet.getCreateUpdatePolicyConstructor()
                    .newInstance(policyParameters, requestId, true);
            response = updatePolicyService.getResult();
            status = updatePolicyService.getResponseCode();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            response = "Problem with CreateUpdate Policy Service. ";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        updatePolicyCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates the dictionary item.
     *
     * @param dictionaryParameters the dictionary parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Dictionary Item for a specific dictionary based on given Parameters.")
    @PutMapping(value = "/createDictionaryItem")
    @ResponseBody
    public ResponseEntity<String> createDictionaryItem(@RequestBody DictionaryParameters dictionaryParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: createDictionaryItem - " + dictionaryParameters);

        CreateUpdateDictionaryService createDictionaryService =
                new CreateUpdateDictionaryService(dictionaryParameters, requestId, false);
        String response = createDictionaryService.getResult();
        HttpStatus status = createDictionaryService.getResponseCode();
        createDictionaryCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Update dictionary item.
     *
     * @param dictionaryParameters the dictionary parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Dictionary Item for a specific dictionary based on given Parameters.")
    @PutMapping(value = "/updateDictionaryItem")
    @ResponseBody
    public ResponseEntity<String> updateDictionaryItem(@RequestBody DictionaryParameters dictionaryParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: updateDictionaryItem - " + dictionaryParameters);

        CreateUpdateDictionaryService updateDictionaryService =
                new CreateUpdateDictionaryService(dictionaryParameters, requestId, true);
        String response = updateDictionaryService.getResult();
        HttpStatus status = updateDictionaryService.getResponseCode();
        updateDictionaryCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Gets the dictionary items.
     *
     * @param dictionaryParameters the dictionary parameters
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the dictionary items
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Gets the dictionary items from the PAP")
    @PostMapping(value = "/getDictionaryItems")
    @ResponseBody
    public ResponseEntity<DictionaryResponse> getDictionaryItems(@RequestBody DictionaryParameters dictionaryParameters,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        DictionaryResponse dictionaryResponse = null;
        logger.info("Operation: getDictionaryItems - " + dictionaryParameters);
        GetDictionaryService getDictionaryService = new GetDictionaryService(dictionaryParameters, requestId);
        dictionaryResponse = getDictionaryService.getResult();
        HttpStatus status = getDictionaryService.getResponseCode();
        getDictionaryCounter.incrementAndGet();
        return new ResponseEntity<>(dictionaryResponse, status);
    }

    /**
     * Policy engine import.
     *
     * @param importParametersJson the import parameters json
     * @param file the file
     * @param clientEncoding the client encoding
     * @param requestId the request ID
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Imports models and templates which represent the service used to create a policy.")
    @PostMapping(value = "/policyEngineImport")
    @ResponseBody
    public ResponseEntity<String> policyEngineImport(@RequestParam("importParametersJson") String importParametersJson,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: policyEngineImport with importParametersJson: " + importParametersJson);
        PolicyEngineImportService policyEngineImportService =
                new PolicyEngineImportService(importParametersJson, file, requestId);
        String response = policyEngineImportService.getResult();
        HttpStatus status = policyEngineImportService.getResponseCode();
        policyEngineImportCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates the config.
     *
     * @param configPolicyAPIRequest the config policy API request
     * @param clientEncoding the client encoding
     * @param requestId the request id
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Config Policy based on given Policy Parameters.")
    @PutMapping(value = "/createConfig")
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> createConfig(@RequestBody ConfigPolicyAPIRequest configPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: createConfig");

        CreateUpdateConfigPolicyService createPolicyService =
                new CreateUpdateConfigPolicyService(configPolicyAPIRequest, requestId, false);
        String response = createPolicyService.getResult();
        HttpStatus status = createPolicyService.getResponseCode();
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Update config.
     *
     * @param configPolicyAPIRequest the config policy API request
     * @param clientEncoding the client encoding
     * @param requestId the request id
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Config Policy based on given Policy Parameters.")
    @PutMapping(value = "/updateConfig")
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> updateConfig(@RequestBody ConfigPolicyAPIRequest configPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: updateConfig");

        CreateUpdateConfigPolicyService updatePolicyService =
                new CreateUpdateConfigPolicyService(configPolicyAPIRequest, requestId, true);
        String response = updatePolicyService.getResult();
        HttpStatus status = updatePolicyService.getResponseCode();
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates the firewall config.
     *
     * @param configFirewallPolicyAPIRequest the config firewall policy API request
     * @param clientEncoding the client encoding
     * @param requestId the request id
     * @return the response entity
     */

    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Creates a Config Firewall Policy")
    @PutMapping(value = "/createFirewallConfig")
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> createFirewallConfig(
            @RequestBody ConfigFirewallPolicyAPIRequest configFirewallPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: createFirewallConfig");

        CreateUpdateFirewallPolicyService createFirewallPolicyService =
                new CreateUpdateFirewallPolicyService(configFirewallPolicyAPIRequest, requestId, false);
        String response = createFirewallPolicyService.getResult();
        HttpStatus status = createFirewallPolicyService.getResponseCode();
        deprecatedCounter.incrementAndGet();
        return new ResponseEntity<>(response, status);
    }

    /**
     * Update firewall config.
     *
     * @param configFirewallPolicyAPIRequest the config firewall policy API request
     * @param clientEncoding the client encoding
     * @param requestId the request id
     * @return the response entity
     */
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = true, paramType = "Header"),
            @ApiImplicitParam(name = "Environment", required = true, paramType = "Header")})
    @ApiOperation(value = "Updates a Config Firewall Policy")
    @PutMapping(value = "/updateFirewallConfig")
    @ResponseBody
    @Deprecated
    public ResponseEntity<String> updateFirewallConfig(
            @RequestBody ConfigFirewallPolicyAPIRequest configFirewallPolicyAPIRequest,
            @RequestHeader(value = "ClientAuth", required = false) String clientEncoding,
            @RequestHeader(value = "X-ECOMP-RequestID", required = false) String requestId) {
        logger.info("Operation: updateFirewallConfig");

        CreateUpdateFirewallPolicyService updateFirewallPolicyService =
                new CreateUpdateFirewallPolicyService(configFirewallPolicyAPIRequest, requestId, true);
        String response = updateFirewallPolicyService.getResult();
        HttpStatus status = updateFirewallPolicyService.getResponseCode();
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
    @GetMapping(value = "/count")
    public String getCount() {
        return "Total Config Calls : " + configCounter + "\n" + "Total Config calls made using Policy File Name: "
                + configNameCounter + "\n" + "Total Event Calls : " + eventCounter + "\nTotal Decision Calls: "
                + decisionCounter + "\nTotal Push policy Calls: " + pushCounter + "\nTotal Delete Policy Calls: "
                + deleteCounter + "\nTotal Create Policy Calls: " + createPolicyCounter
                + "\nTotal Update Policy Calls: " + updatePolicyCounter + "\nTotal Create Dictionary Calls: "
                + createDictionaryCounter + "\nTotal Update Dictionary Calls: " + updateDictionaryCounter
                + "\nTotal Get Dictionary Calls: " + getDictionaryCounter + "\nTotal PolicyEngine Import Calls: "
                + policyEngineImportCounter + "\nTotal Deprecated Policy Calls: " + deprecatedCounter
                + "\nTotal Metrics Calls:" + metricCounter + "\nTotal Notification Calls:" + notificationCounter;
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
