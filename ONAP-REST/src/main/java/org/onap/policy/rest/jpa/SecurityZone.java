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
 */
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name="securityzone")
@NamedQuery(name="SecurityZone.findAll", query="SELECT e FROM SecurityZone e ")
public class SecurityZone implements Serializable {
	private static final long serialVersionUID = 1L;

	private static String domain;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="name", nullable=false)
	@OrderBy("asc")
	private String zoneName;
	
	@Column(name="value")
	private String zoneValue;


	public SecurityZone() {
		// Empty constructor
	}
	public SecurityZone(String string, String userid) {
		this(domain);
		
	}
	public SecurityZone(String domain) {
		// Empty constructor
	}	

	@PrePersist
	public void	prePersist() {
		// Empty function
	}
	@PreUpdate
	public void preUpdate() {
		// Empty function
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getZoneName() {
		return this.zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;

	}
	public String getZoneValue() {
		return this.zoneValue;
	}

	public void setZoneValue(String zoneValue) {
		this.zoneValue = zoneValue;
	}
	
}
