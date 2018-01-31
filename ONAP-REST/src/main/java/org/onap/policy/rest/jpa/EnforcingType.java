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

import javax.persistence.*;

/**
 * Entity implementation class for Entity: EnforcingType
 *
 */
@Entity
@Table(name="EnforcingType")
@NamedQuery(name="EnforcingType.findAll", query="SELECT e FROM EnforcingType e ")
public class EnforcingType implements Serializable {


	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	@Column(name="enforcingType", nullable=false, unique=true)
	@OrderBy("asc")
	private String enforcingType;
	@Column(name="script", nullable=false, length=255)
	private String script;
	@Column(name="connectionQuery", nullable=false, length=255)
	private String connectionQuery;
	@Column(name="valueQuery", nullable=false, length=255)
	private String valueQuery;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEnforcingType() {
		return enforcingType;
	}

	public void setEnforcingType(String enforcingType) {
		this.enforcingType = enforcingType;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getConnectionQuery() {
		return connectionQuery;
	}

	public void setConnectionQuery(String connectionQuery) {
		this.connectionQuery = connectionQuery;
	}

	public String getValueQuery() {
		return valueQuery;
	}

	public void setValueQuery(String valueQuery) {
		this.valueQuery = valueQuery;
	}

	public EnforcingType() {
		super();
	}

}
