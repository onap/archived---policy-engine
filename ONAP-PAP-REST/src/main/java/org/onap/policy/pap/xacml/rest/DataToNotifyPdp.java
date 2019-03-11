/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.XACMLProperties;
import com.google.common.base.Joiner;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.GroupEntity;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataToNotifyPdp {

    private static final Logger LOGGER = FlexLogger.getLogger(DataToNotifyPdp.class);
    private static CommonClassDao commonClassDao;
    private Properties policyLocations = null;
    private static Properties pipProperties = null;

    @Autowired
    public DataToNotifyPdp(CommonClassDao commonClassDao) {
        DataToNotifyPdp.commonClassDao = commonClassDao;
    }

    public DataToNotifyPdp() {
        // default constructor.
    }


    private static Properties readPipProperties() throws IOException {
        if (pipProperties == null) {
            try (FileInputStream inputStream = new FileInputStream(Paths.get("pip.properties").toString())) {
                pipProperties = new Properties();
                pipProperties.load(inputStream);
            }
        }
        return pipProperties;
    }

    /**
     * Sets the policy config properties.
     *
     * @param pdp the pdp
     * @param papEngine the pap engine
     * @return the list
     */
    public List<Properties> setPolicyConfigProperties(OnapPDP pdp, PAPPolicyEngine papEngine) {
        OnapPDPGroup group = null;
        try {
            group = papEngine.getPDPGroup(pdp);
        } catch (PAPException e) {
            LOGGER.error("Pdp Id not found in PDP Groups.", e);
        }
        return setPolicyConfigProperties(group);
    }

    /**
     * This method is used to set the policyGroupEntity data to properties. So, we can update the
     * pdp thread with the latest policy info. Hence, considering Database as master instead of File
     * System. To overcome the redundancy issues.
     *
     * @param group Input is OnapPDP Group name.
     */
    public List<Properties> setPolicyConfigProperties(OnapPDPGroup group) {
        boolean groupCheck = false;
        List<Properties> properties = new ArrayList<>();
        policyLocations = new Properties();
        Properties policyProperties = new Properties();
        Properties pipProps = new Properties();
        if (group != null && group.getName() != null) {
            GroupEntity data =
                    (GroupEntity) commonClassDao.getEntityItem(GroupEntity.class, "groupName", group.getName());
            if (data != null) {
                policyProperties = setPolicyProperties(data);
                try {
                    pipProps = readPipProperties();
                } catch (IOException e) {
                    LOGGER.error("Error Occured while reading the pip properties.", e);
                }
                groupCheck = true;
            } else {
                LOGGER.info("Group Name exists, but not exists in DB. So, adding the empty properties list.");
                setEmptyPolicyProperties(policyProperties, pipProps);
            }
        } else {
            LOGGER.info("Group Name is null. So, adding the empty properties list.");
            setEmptyPolicyProperties(policyProperties, pipProps);
        }
        properties.add(policyProperties);
        properties.add(pipProps);
        if (groupCheck) {
            properties.add(policyLocations);
        }
        return properties;
    }

    /**
     * Based on the Group Entity list, write the policyNames to properties.
     *
     * @param data group entity object.
     * @return properties.
     */
    private Properties setPolicyProperties(GroupEntity data) {
        GroupEntity entity = data;
        Properties policyProperties = new Properties() {
            private static final long serialVersionUID = 1L;

            // For Debugging it is helpful for the file to be in a sorted order,
            // any by returning the keys in the natural Alpha order for strings we get close enough.
            // TreeSet is sorted, and this just overrides the normal Properties method to get the
            // keys.
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        List<String> roots = new ArrayList<>();
        for (PolicyEntity policy : entity.getPolicies()) {
            // for all policies need to tell PDP the "name", which is the base name for the file id
            String policyName = policy.getScope() + "." + policy.getPolicyName();
            String policyNameWithNoScope = policy.getPolicyName();
            if (policyName != null) {
                policyProperties.setProperty(policyName + ".name", policy.getScope() + "."
                        + policyNameWithNoScope.substring(0, policyNameWithNoScope.indexOf('.')));
                policyLocations.put(policyName + ".url", XACMLPapServlet.papURL + "?id=" + policyName);
            }
            roots.add(policyName);
        }
        policyProperties.setProperty(XACMLProperties.PROP_ROOTPOLICIES, Joiner.on(',').join(roots));
        policyProperties.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
        return policyProperties;
    }

    /**
     * When Group name is null, then we need to consider group is deleted and notify pdp with empty
     * properties.
     *
     * @param policyProperties policyProperties input.
     * @param pipProps pipProps input.
     */
    private void setEmptyPolicyProperties(Properties policyProperties, Properties pipProps) {
        // create blank properties files
        policyProperties.put(XACMLProperties.PROP_ROOTPOLICIES, "");
        policyProperties.put(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
        pipProps.setProperty(XACMLProperties.PROP_PIP_ENGINES, "");
    }

}
