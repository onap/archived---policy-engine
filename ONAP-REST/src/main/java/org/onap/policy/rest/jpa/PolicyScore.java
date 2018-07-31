/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest.jpa;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.Table;


@Entity
@Table(name="POLICYSCORE")
@NamedQueries({
    @NamedQuery(name="POLICYSCORE.findAll", query="SELECT p FROM PolicyScore p"),
    @NamedQuery(name="POLICYSCORE.deleteAll", query="DELETE FROM PolicyScore WHERE 1=1"),
    @NamedQuery(name="POLICYSCORE.findByPolicyName", query="Select p from PolicyScore p where p.PolicyName=:pname")
})
public class PolicyScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="POLICY_NAME", nullable=false)
    @OrderBy("asc")
    private String PolicyName;

    @Column(name="VERSIONEXTENSION", nullable=false)
    @OrderBy("asc")
    private String VersionExtension;

    @Column(name="POLICY_SCORE", nullable=true)
    private String PolicyScore;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getPolicyName() {
        return PolicyName;
    }
    public void setPolicyName(String policyName) {
        PolicyName = policyName;
    }
    public String getVersionExtension() {
        return VersionExtension;
    }

    public void setVersionExtension(String versionExtension) {
        VersionExtension = versionExtension;
    }
    public String getPolicyScore() {
        return PolicyScore;
    }
    public void setPolicyScore(String policyScore) {
        PolicyScore = policyScore;
    }


}
