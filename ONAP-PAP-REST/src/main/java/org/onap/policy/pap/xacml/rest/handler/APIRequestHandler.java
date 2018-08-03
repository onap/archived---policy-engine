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
package org.onap.policy.pap.xacml.rest.handler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.pap.xacml.rest.service.ImportService;
import org.onap.policy.pap.xacml.rest.service.MetricService;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;

public class APIRequestHandler {

    private OnapPDPGroup newGroup;

    public void doGet(HttpServletRequest request, HttpServletResponse response, String apiflag) throws IOException{
        // Request from the API to get Dictionary Items
        if ("api".equalsIgnoreCase(apiflag)) {
            DictionaryHandler dictionaryHandler = DictionaryHandler.getInstance();
            dictionaryHandler.doDictionaryAPIGet(request, response);
            return;
        }
        // Request from the API to get the ActiveVersion from the PolicyVersion table
        if ("version".equalsIgnoreCase(apiflag)){
            PushPolicyHandler pushHandler = new PushPolicyHandler();
            pushHandler.getActiveVersion(request, response);
            return;
        }
        // Request from the API to get the URI from the gitpath
        if ("uri".equalsIgnoreCase(apiflag)){
            PushPolicyHandler pushHandler = new PushPolicyHandler();
            pushHandler.getSelectedURI(request, response);
            return;
        }
        if ("getMetrics".equalsIgnoreCase(apiflag)){
            MetricService.doGetPolicyMetrics(response);
            return;
        }
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response, String service) throws IOException {
        if ("MICROSERVICE".equalsIgnoreCase(service) || "BRMSPARAM".equalsIgnoreCase(service) || "OPTIMIZATION".equalsIgnoreCase(service)){
            ImportService importService = new ImportService();
            importService.doImportMicroServicePut(request, response);
            return;
        }
        if ("dictionaryItem".equalsIgnoreCase(service)) {
            DictionaryHandler dictionaryHandler = DictionaryHandler.getInstance();
            dictionaryHandler.doDictionaryAPIPut(request, response);
            return;
        } else {
            SavePolicyHandler savePolicy = SavePolicyHandler.getInstance();
            savePolicy.doPolicyAPIPut(request, response);
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response, ONAPLoggingContext loggingContext, String apiflag) throws IOException, SQLException{
        DeleteHandler deleteHandler = DeleteHandler.getInstance();
        if ("deletePapApi".equalsIgnoreCase(apiflag)) {
            deleteHandler.doAPIDeleteFromPAP(request, response);
            return;
        } else if ("deletePdpApi".equalsIgnoreCase(apiflag)) {
            deleteHandler.doAPIDeleteFromPDP(request, response, loggingContext);
            setNewGroup(deleteHandler.getDeletedGroup());
            return;
        }
    }

    private void setNewGroup(OnapPDPGroup newGroup) {
        this.newGroup = newGroup;
    }

    public OnapPDPGroup getNewGroup() {
        return newGroup;
    }
}
