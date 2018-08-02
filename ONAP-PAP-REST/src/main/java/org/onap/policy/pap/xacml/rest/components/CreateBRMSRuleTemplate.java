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
package org.onap.policy.pap.xacml.rest.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.BRMSParamTemplate;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.utils.PolicyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateBRMSRuleTemplate {
    private static final Logger LOGGER  = FlexLogger.getLogger(CreateBRMSRuleTemplate.class);
    private static CommonClassDao commonClassDao;

    @Autowired
    public CreateBRMSRuleTemplate(CommonClassDao commonClassDao){
        CreateBRMSRuleTemplate.commonClassDao = commonClassDao;
    }

    public CreateBRMSRuleTemplate() {}

    public Map<String, String> addRule(String rule, String ruleName, String description, String userID) {
        Map<String,String> responseMap = new HashMap<>();
        if(rule!=null && !PolicyUtils.brmsRawValidate(rule).contains("[ERR")){
            List<Object> duplicateData =  commonClassDao.checkDuplicateEntry(ruleName, "ruleName", BRMSParamTemplate.class);
            if(duplicateData!=null && !duplicateData.isEmpty()){
                LOGGER.error("Import new service failed.  Service already exists");
                responseMap.put("DBError", "EXISTS");
                return responseMap;
            }else{
                BRMSParamTemplate brmsParamTemplate = new BRMSParamTemplate();
                brmsParamTemplate.setDescription(description);
                brmsParamTemplate.setRuleName(ruleName);
                brmsParamTemplate.setRule(rule);
                UserInfo userCreatedBy = (UserInfo) commonClassDao.getEntityItem(UserInfo.class, "userLoginId", userID);
                brmsParamTemplate.setUserCreatedBy(userCreatedBy);
                commonClassDao.save(brmsParamTemplate);
                LOGGER.info("Template created with " + ruleName + " by " + userID);
            }
            responseMap.put("success", "success");
        }else{
            LOGGER.debug("Error during validating the rule for creating record for BRMS Param Template");
            responseMap.put("error", "VALIDATION");
        }
        return responseMap;
    }

    public static boolean validateRuleParams(String rule) {
        CreateBrmsParamPolicy policy = new CreateBrmsParamPolicy();
        Map<String, String> paramValues = policy.findType(rule);
        for(String key : paramValues.keySet()) {
            if(!PolicyUtils.SUCCESS.equals(PolicyUtils.policySpecialCharValidator(key))){
                return false;
            }
        }
        return true;
    }
}
