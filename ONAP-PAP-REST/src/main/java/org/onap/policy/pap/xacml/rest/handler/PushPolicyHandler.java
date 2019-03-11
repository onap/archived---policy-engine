/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017,2019 AT&T Intellectual Property. All rights reserved.
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

import com.att.research.xacml.util.XACMLProperties;
import java.io.File;
import java.net.URI;
import java.util.List;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PushPolicyHandler {
    private static final Logger logger = FlexLogger.getLogger(PushPolicyHandler.class);

    private static CommonClassDao commonClassDao;

    @Autowired
    public PushPolicyHandler(CommonClassDao commonClassDao) {
        PushPolicyHandler.commonClassDao = commonClassDao;
    }

    public PushPolicyHandler() {
        // Default Constructor
    }

    /*
     * Get Active Version.
     */
    public void getActiveVersion(HttpServletRequest request, HttpServletResponse response) {
        String policyScope = request.getParameter("policyScope").replace(".", File.separator);
        String filePrefix = request.getParameter("filePrefix");
        String policyName = request.getParameter("policyName");

        String pvName = policyScope + File.separator + filePrefix + policyName;
        int activeVersion = 0;

        // Get the Active Version to use in the ID
        String query = "Select p from PolicyVersion p where p.policyName=:pname";
        SimpleBindings params = new SimpleBindings();
        params.put("pname", pvName);

        @SuppressWarnings("rawtypes")
        List result = commonClassDao.getDataByQuery(query, params);
        PolicyVersion versionEntity = null;
        if (!result.isEmpty()) {
            versionEntity = (PolicyVersion) result.get(0);
            commonClassDao.save(versionEntity);
            activeVersion = versionEntity.getActiveVersion();
        } else {
            PolicyLogger.debug("No PolicyVersion using policyName found");
        }

        if (String.valueOf(activeVersion) != null || !String.valueOf(activeVersion).equalsIgnoreCase("")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("version", String.valueOf(activeVersion));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /*
     * Get Selected URI path.
     */
    public void getSelectedURI(HttpServletRequest request, HttpServletResponse response) {
        String gitPath = request.getParameter("gitPath");
        File file = new File(gitPath);
        PolicyLogger.debug("The fileItem is : " + file.toString());
        URI selectedURI = file.toURI();
        String uri = selectedURI.toString();
        if (!uri.equalsIgnoreCase("")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("selectedURI", uri);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public boolean preSafetyCheck(StdPDPPolicy policy, String configHome){
        return true;
    }

    public boolean preSafetyCheck(OnapPDPGroup policy, String configHome){
        return true;
    }

    public static PushPolicyHandler getInstance() {
        try {
            Class<?> pushPolicyHandler = Class.forName(XACMLProperties.getProperty("pushPolicy.impl.className", PushPolicyHandler.class.getName()));
            PushPolicyHandler instance = (PushPolicyHandler) pushPolicyHandler.newInstance();
            return instance;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
