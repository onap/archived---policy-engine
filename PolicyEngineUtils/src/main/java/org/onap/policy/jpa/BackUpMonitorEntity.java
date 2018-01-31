/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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

package org.onap.policy.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "BackUpMonitorEntity")
@NamedQuery(name = "BackUpMonitorEntity.findAll", query = "SELECT b FROM BackUpMonitorEntity b ")
public class BackUpMonitorEntity implements Serializable {

    private static final long serialVersionUID = -9190606334322230630L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "node_name", nullable = false)
    private String resourceNodeName;

    @Column(name = "resource_name", nullable = false, unique = true)
    private String resourceName;

    @Column(name = "flag", nullable = false)
    private String flag;

    @Lob
    @Column(name = "notification_record")
    private String notificationRecord;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_seen")
    private Date timeStamp;

    @PrePersist
    public void prePersist() {
        this.timeStamp = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.timeStamp = new Date();
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public String getResourceNodeName() {
        return this.resourceNodeName;
    }

    public String getFlag() {
        return this.flag;
    }

    public String getNotificationRecord() {
        return this.notificationRecord;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setResourceNodeName(String resourceNodeName) {
        this.resourceNodeName = resourceNodeName;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setNotificationRecord(String notificationRecord) {
        this.notificationRecord = notificationRecord;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
