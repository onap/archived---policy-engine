/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pdp.rest.api.services;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.XACMLPdpLoader;
import org.onap.policy.pdp.rest.api.models.ConfigNameRequest;
import org.springframework.http.HttpStatus;


public class ListPolicyService {

    private static Logger logger = FlexLogger.getLogger(ListPolicyService.class.getName());

    private Collection<String> results = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;
    private ConfigNameRequest configRequestParameters = null;
    private String requestId = null;


    public ListPolicyService() {
        // Default Constructor
    }

    /**
     * Instantiates a new list policy service.
     *
     * @param configRequestParameters the config request parameters
     */
    public ListPolicyService(ConfigNameRequest configRequestParameters) {
        requestId = UUID.randomUUID().toString();
        this.configRequestParameters = configRequestParameters;

        try {
            run();
        } catch (Exception e) {
            logger.warn("ListPolicy - ERROR for request - " + requestId + ", " + e);
            status = HttpStatus.BAD_REQUEST;
            results = null;
        }

        logger.info("Result for listPolicy - " + configRequestParameters + ", for request - " + requestId
                + " - Response - " + results);
    }

    private void run() throws PAPException, IOException {
        Properties currentProperties = new Properties();
        try (InputStream is = Files.newInputStream(XACMLPdpLoader.getPDPPolicyCache())) {
            currentProperties.load(is);
        }

        Set<String> listOfPolicies = XACMLProperties.getRootPolicyIDs(currentProperties);
        results = filterList(listOfPolicies, configRequestParameters.getPolicyName());
        status = HttpStatus.OK;

    }

    private List<String> filterList(Set<String> list, String regex) {
        return list.stream().filter(s -> s.matches(regex)).collect(Collectors.toList());
    }

    public Collection<String> getResult() {
        return results;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
