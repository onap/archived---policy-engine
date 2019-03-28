/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "DictionaryData")
@NamedQuery(name = "DictionaryData.findAll", query = "SELECT v FROM DictionaryData v ")
public class DictionaryData {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "dictionaryName", nullable = false, unique = true)
    private String dictionaryName;

    @Column(name = "dictionaryUrl", nullable = false, length = 2048)
    private String dictionaryUrl;

    @Column(name = "dictionaryDataByName", nullable = false, length = 1024)
    private String dictionaryDataByName;

    public String getDictionaryUrl() {
        return dictionaryUrl;
    }

    public void setDictionaryUrl(String dictionaryUrl) {
        this.dictionaryUrl = dictionaryUrl;
    }

    public String getDictionaryDataByName() {
        return dictionaryDataByName;
    }

    public void setDictionaryDataByName(String dictionaryDataByName) {
        this.dictionaryDataByName = dictionaryDataByName;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
