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

package org.onap.policy.rest.jpa;
/*
 * 
 * 
 * */

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "WatchPolicyNotificationTable")
@NamedQuery(name="WatchPolicyNotificationTable.findAll", query="SELECT e FROM WatchPolicyNotificationTable e ")
public class WatchPolicyNotificationTable implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="policyName", nullable=false, unique=true)
    @OrderBy("asc")
    private String policyName;

    @Column(name="loginIds", nullable=false, unique=true)
    @OrderBy("asc")
    private String loginIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getLoginIds() {
        return loginIds;
    }

    public void setLoginIds(String loginIds) {
        this.loginIds = loginIds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, policyName, loginIds);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof WatchPolicyNotificationTable)){
            return false;
        }

        return id == ((WatchPolicyNotificationTable)obj).id &&
        policyName.equals(((WatchPolicyNotificationTable)obj).policyName) &&
        loginIds.equals(((WatchPolicyNotificationTable)obj).loginIds);
    }


}
