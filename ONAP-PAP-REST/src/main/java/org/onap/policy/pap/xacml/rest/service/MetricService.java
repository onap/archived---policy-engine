/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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
package org.onap.policy.pap.xacml.rest.service;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.pap.xacml.rest.XACMLPapServlet;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;

import com.att.research.xacml.api.pap.PDPPolicy;

public class MetricService {
    private static String errorMsg	= "error";
    /*
     * This is a private constructor
     * */
    private MetricService(){

    }
    public static void doGetPolicyMetrics(HttpServletResponse response) {
        Set<OnapPDPGroup> groups = new HashSet<>();
        try {
            //get the count of policies on the PDP
            if(XACMLPapServlet.getPAPEngine()!=null){
                groups = XACMLPapServlet.getPAPEngine().getOnapPDPGroups();
            }
            int pdpCount = 0;
            for (OnapPDPGroup group : groups) {
                Set<PDPPolicy> policies = group.getPolicies();
                pdpCount += policies.size();
            }
            //get the count of policies on the PAP
            EntityManager em = null;
            if(XACMLPapServlet.getEmf()!=null){
                em = XACMLPapServlet.getEmf().createEntityManager();
            }
            if (em==null){
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Error creating entity manager with persistence unit: " + XACMLPapServlet.getPersistenceUnit());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.addHeader(errorMsg, "Error creating entity manager with persistence unit");
                return;
            }
            int papCount = ((Number) em.createNamedQuery("PolicyVersion.findAllCount").getSingleResult()).intValue();
            em.close();
            int totalCount = pdpCount + papCount;
            //create json string for API response
            JSONObject json = new JSONObject();
            json.put("papCount", papCount);
            json.put("pdpCount", pdpCount);
            json.put("totalCount", totalCount);
            if (pdpCount>0 && papCount>0 && totalCount>0) {
                PolicyLogger.info("Metrics have been found on the Policy Engine for the number of policies on the PAP and PDP.");
                response.setStatus(HttpServletResponse.SC_OK);
                response.addHeader("successMapKey", "success");
                response.addHeader("operation", "getMetrics");
                response.addHeader("metrics", json.toString() );
                return;
            }else{
                String message = "The policy count on the PAP and PDP is 0.  Please check the database and file system to correct this error.";
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.addHeader(errorMsg, message);
                return;
            }
        } catch (Exception e) {
            String message = XACMLErrorConstants.ERROR_DATA_ISSUE + " Error Querying the Database: " + e.getMessage();
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPapServlet", " Error Querying the Database.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.addHeader(errorMsg, message);
            return;
        }
    }

}
