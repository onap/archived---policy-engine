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
import java.sql.Clob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the roles database table.
 *
 */
@Entity
@Table(name="policy_manangement")
@NamedQuery(name="PolicyManagement.findAll", query="SELECT r FROM PolicyManagement r")
public class PolicyManagement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)

	@Column(name="id")
	private int id;

	@Column(name="POLICY_NAME", nullable=false, length=45)
	private String policyName;

	@Column(name="scope", nullable=false, length=45)
	private String scope;

	@Column(name="ONAP_NAME", nullable=false, length=45)
	private String onapName;

	@Column(name="CONFIG_NAME", nullable=false, length=45)
	private String configName;

	@Column(name="XML", nullable=false)
	private transient Clob xml;

	@Column(name="CREATE_DATE_TIME", nullable=false)
	private Timestamp createDateTime;


	@Column(name="CREATED_BY", nullable=false, length=45)
	private String createdBy;

	@Column(name="UPDATE_DATE_TIME", nullable=false)
	private Timestamp updateDateTime;

	@Column(name="UPDATED_BY", nullable=false, length=45)
	private String updatedBy;

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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getOnapName() {
		return onapName;
	}

	public void setOnapName(String onapName) {
		this.onapName = onapName;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public Clob getXml() {
		return xml;
	}

	public void setXml(Clob xml) {
		this.xml = xml;
	}

	public Timestamp getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Timestamp updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}



}
