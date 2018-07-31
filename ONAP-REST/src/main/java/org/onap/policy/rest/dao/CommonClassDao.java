/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.dao;

import java.util.List;

import org.onap.policy.rest.jpa.PolicyRoles;
import javax.script.SimpleBindings;

public interface CommonClassDao {

    //Common methods
    List<Object> getData(@SuppressWarnings("rawtypes") Class className);
    List<Object> getDataById(@SuppressWarnings("rawtypes") Class className, String columnName, String key);
    List<String> getDataByColumn(@SuppressWarnings("rawtypes") Class className, String columnName);
    List<Object> checkDuplicateEntry(String value, String columnName,  @SuppressWarnings("rawtypes") Class className);
    Object getEntityItem(@SuppressWarnings("rawtypes") Class className, String columnName, String key);
    List<Object>  getDataByQuery(String query, SimpleBindings params);
    List<Object>  getMultipleDataOnAddingConjunction(@SuppressWarnings("rawtypes") Class className, String columnName, List<String> data);
    void save(Object entity);
    void delete(Object entity);
    void update(Object entity);
    void updateQuery(String query);

    //Group Policy Scope
    List<Object> checkExistingGroupListforUpdate(String groupListValue, String groupNameValue);


    //Roles
    List<PolicyRoles> getUserRoles();


    //ClosedLoops
    void updateClAlarms(String clName, String alarms);
    void updateClYaml(String clName, String yaml);
    void deleteAll();



}
