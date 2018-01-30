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
import org.onap.policy.rest.XacmlAdminAuthorization;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;


@Entity
@Table(name="TERM")
@NamedQuery(name="TermList.findAll", query="SELECT e FROM TermList e")
public class TermList implements Serializable {
	private static final long serialVersionUID = 1L;

	private static String domain;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;
	
	@Column(name="termName", nullable=false)
	@OrderBy("asc")
	private String termName;
	
	@Column(name="description")
	private String termDescription;
	
	@Column(name="fromzone")
	private String fromZone;
	
	@Column(name="tozone")
	private String toZone;
	
	@Column(name="srcIPList")
	private String srcIPList;
	
	@Column(name="destIPList")
	private String destIPList;
	
	@Column(name="protocolList")
	private String protocolList;
	
	@Column(name="portList")
	private String portList;
	
	@Column(name="srcPortList")
	private String srcPortList;
	
	@Column(name="destPortList")
	private String destPortList;
	
	@Column(name="action")
	private String action;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", updatable=false)
	private Date createdDate;

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

	private static Log LOGGER = LogFactory.getLog(TermList.class);
	
	public TermList() {
		// Empty constructor
	}
	
	private static final Log auditLogger = LogFactory
			.getLog("auditLogger");
	
	public TermList(String string, String userid) {
		this(domain);
	}
	public TermList(String domain) {
		this.termName = domain;
	}	

	@PrePersist
	public void	prePersist() {
		Date date = new Date();
		this.createdDate = date;
		this.modifiedDate = date;
		auditLogger.debug("Added New Term Name: "+this.termName+" by "+this.userCreatedBy);
		
	}
	@PreUpdate
	public void preUpdate() {
		this.modifiedDate = new Date();
		try {
			this.userModifiedBy =XacmlAdminAuthorization.getUserId();;
		} catch (Exception e) {
			LOGGER.error("Exception caused While adding Modified by Role"+e);
			PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "TermList", "Exception caused While adding Modified by Role");
		}
		auditLogger.debug("Updated Term Name: "+this.termName+" by "+this.userModifiedBy);
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTermName() {
		return this.termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}
	
	public String getTermDescription() {
		return this.termDescription;
	}

	public void setDescription(String termDescription) {
		this.termDescription = termDescription;
	}
	
	public String getFromZone() {
		return this.fromZone;
	}

	public void setFromZones(String fromZone) {
		this.fromZone = fromZone;
	}
	
	public String getToZone() {
		return this.toZone;
	}

	public void setToZones(String toZone) {
		this.toZone = toZone;
	}
	
	public String getSrcIPList() {
		return this.srcIPList;
	}

	public void setSrcIPList(String srcIPList) {
		this.srcIPList = srcIPList;
	}
	
	public String getDestIPList() {
		return this.destIPList;
	}

	public void setDestIPList(String destIPList) {
		this.destIPList = destIPList;
	}
	
	public String getProtocolList() {
		return this.protocolList;
	}

	public void setProtocolList(String protocolList) {
		this.protocolList = protocolList;
	}
	
	public String getPortList() {
		return this.portList;
	}

	public void setPortList(String portList) {
		this.portList = portList;
	}
	
	public String getSrcPortList() {
		return this.srcPortList;
	}

	public void setSrcPortList(String srcPortList) {
		this.srcPortList = srcPortList;
	}
	
	public String getDestPortList() {
		return this.destPortList;
	}

	public void setDestPortList(String destPortList) {
		this.destPortList = destPortList;
	}
	
	
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
