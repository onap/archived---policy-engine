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
package org.onap.policy.pdp.rest.api.services;

import java.util.ArrayList;
import java.util.Collection;

import org.onap.policy.api.ConfigRequestParameters;
import org.onap.policy.api.PolicyConfigStatus;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.PolicyConfig;
import org.springframework.http.HttpStatus;

public class ListConfigService {
    private static Logger LOGGER = FlexLogger.getLogger(ListConfigService.class.getName());

    private Collection<String> results = null;
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public ListConfigService(ConfigRequestParameters configRequestParameters,
            String requestID) {
        GetConfigService getConfigService = new GetConfigService(configRequestParameters,requestID);
        Collection<PolicyConfig> policyConfigs = getConfigService.getResult();
        LOGGER.info("Transferring Config Results to List. ");
        if(policyConfigs!=null){
            results = new ArrayList<String>();
            status = HttpStatus.OK;
            for(PolicyConfig policyConfig : policyConfigs){
                if(policyConfig.getPolicyConfigMessage()!=null && policyConfig.getPolicyConfigMessage().contains("PE300")
                		&& policyConfigs.size()<=1 && policyConfig.getPolicyConfigStatus().equals(PolicyConfigStatus.CONFIG_NOT_FOUND)){
                    results.add(policyConfig.getPolicyConfigMessage());
                    status = HttpStatus.BAD_REQUEST;
                } else {
                    results.add(policyConfig.getPolicyName());
                }
            }
        }
    }

    public Collection<String> getResult() {
        return results;
    }

    public HttpStatus getResponseCode() {
        return status;
    }

}
