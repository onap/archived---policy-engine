/*-
 * ============LICENSE_START=======================================================
 * ECOMP-REST
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

package org.openecomp.policy.rest.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.policy.rest.XacmlAdminAuthorization;

import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;


@Entity
@Table(name="VSCLAction")
@NamedQuery(name="VSCLAction.findAll", query="SELECT v FROM VSCLAction v ")
public class VSCLAction implements Serializable  {
	private static final long serialVersionUID = 1L;

	private static String domain;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="vscl_action", nullable=false, unique=true)
	@OrderBy("asc")
	private String vsclaction;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", updatable=false)
	private Date createdDate;

	@Column(name="description", nullable=true, length=2048)
	private String description;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date", nullable=false)
	private Date modifiedDate;

	@ManyToOne(optional = false)
	@JoinColumn(name="created_by")
	private UserInfo userCreatedBy;

	@ManyToOne(optional = false)
	@JoinColumn(name="modified_by")
	private UserInfo userModifiedBy;
	
	public UserInfo getUserCreatedBy() {
		return userCreatedBy;
	}

	public void setUserCreatedBy(UserInfo userCreatedBy) {
		this.userCreatedBy = userCreatedBy;
	}

	public UserInfo getUserModifiedBy() {
		return userModifiedBy;
	}

	public void setUserModifiedBy(UserInfo userModifiedBy) {
		this.userModifiedBy = userModifiedBy;
	}

	private static Log LOGGER = LogFactory.getLog(VSCLAction.class);
	
	public VSCLAction() {
		
	}
	
	public VSCLAction(String string, String userid) {
		this(domain);
	}
	
	public VSCLAction(String domain) {
		this.vsclaction = domain;
	}	

	@PrePersist
	public void	prePersist() {
		Date date = new Date();
		this.createdDate = date;
		this.modifiedDate = date;
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedDate = new Date();
		try {
			this.userModifiedBy =XacmlAdminAuthorization.getUserId();;
		} catch (Exception e) {
			LOGGER.error("Exception caused While adding Modified by Role"+e);
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "VSCLAction", "Exception caused While adding Modified by Role");
		}
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getVsclaction() {
		return vsclaction;
	}

	public void setVsclaction(String vsclaction) {
		this.vsclaction = vsclaction;
	}
	

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
