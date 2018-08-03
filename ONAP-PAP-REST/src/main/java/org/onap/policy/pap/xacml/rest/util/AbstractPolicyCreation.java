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
package org.onap.policy.pap.xacml.rest.util;


import java.util.UUID;

import org.onap.policy.rest.XACMLRestProperties;
import org.springframework.stereotype.Component;

import com.att.research.xacml.util.XACMLProperties;
import com.google.common.base.Joiner;

@Component
public abstract class AbstractPolicyCreation {

    public static String getDomain() {
        return XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_DOMAIN, "urn");
    }

    public String newPolicyID() {
        return Joiner.on(':').skipNulls().join((getDomain().startsWith("urn") ? null: "urn"),
                getDomain().replaceAll("[/\\\\.]", ":"), "xacml", "policy", "id", UUID.randomUUID());
    }

    public String convertDate(String dateTTL) {
        String formateDate = null;
        if(dateTTL.contains("/")){
            formateDate = dateTTL.replace("/", "-");
        }else {
            formateDate = dateTTL;
        }
        return formateDate;
    }



    public void updatePolicyCreationToDatabase(){
        // Add it into our tree
/*		Path finalPolicyPath = null;
        finalPolicyPath = Paths.get(successMap.get("success"));
        PolicyElasticSearchController controller = new PolicyElasticSearchController();
        controller.updateElk(finalPolicyPath.toString());
        File file = finalPolicyPath.toFile();
        if(file != null){
            String policyName = file.toString();
            String removePath = policyName.substring(policyName.indexOf("repository")+11);
            String removeXml = removePath.replace(".xml", "");
            String removeExtension = removeXml.substring(0, removeXml.indexOf("."));
            List<Object> policyVersionList = commonClassDao.getDataById(PolicyVersion.class, "policyName", removeExtension);
            if (policyVersionList.size() > 0) {
                for(int i = 0;  i < policyVersionList.size(); i++) {
                PolicyVersion entityItem = (PolicyVersion) policyVersionList.get(i);
                    if(entityItem.getPolicyName().equals(removeExtension)){
                        version = entityItem.getHigherVersion() +1;
                        entityItem.setActiveVersion(version);
                        entityItem.setHigherVersion(version);
                        entityItem.setModifiedBy(userId);
                        commonClassDao.update(entityItem);
                        if(policyData.isEditPolicy){
                            PolicyNotificationMail email = new PolicyNotificationMail();
                            String mode = "EditPolicy";
                            String policyNameForEmail = policyData.getDomainDir() + File.separator + policyData.getOldPolicyFileName() + ".xml";
                            email.sendMail(entityItem, policyNameForEmail, mode, commonClassDao);
                        }
                    }
                }
            }else{
                PolicyVersion entityItem = new PolicyVersion();
                entityItem.setActiveVersion(version);
                entityItem.setHigherVersion(version);
                entityItem.setPolicyName(removeExtension);
                entityItem.setCreatedBy(userId);
                entityItem.setModifiedBy(userId);
                commonClassDao.save(entityItem);
            }
        }*/
    }


}
