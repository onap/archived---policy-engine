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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.IdentifierImpl;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "UserInfo")
@NamedQuery(name="UserInfo.findAll", query="SELECT u FROM UserInfo u ")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="loginid", nullable=false, length=45)
    private String userLoginId;

    @Column(name = "name", nullable = false, unique = true)
    private String userName;

    public UserInfo(){
        this.userLoginId = userName;

    }

    public String getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(String loginid) {
        this.userLoginId = loginid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Transient
    @JsonBackReference
    public Identifier getIdentiferByUserId() {
        return new IdentifierImpl(this.userName);
    }

}
