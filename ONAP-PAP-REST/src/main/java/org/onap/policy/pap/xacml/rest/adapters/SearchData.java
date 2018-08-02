/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pap.xacml.rest.adapters;

public class SearchData {
    private String query;
    private String policyType;
    private String descriptiveScope;
    private String closedLooppolicyType;
    private String onapName;
    private String d2Service;
    private String vnfType;
    private String policyStatus;
    private String vproAction;
    private String serviceType;
    private String bindTextSearch;
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public String getPolicyType() {
        return policyType;
    }
    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }
    public String getDescriptiveScope() {
        return descriptiveScope;
    }
    public void setDescriptiveScope(String descriptiveScope) {
        this.descriptiveScope = descriptiveScope;
    }
    public String getClosedLooppolicyType() {
        return closedLooppolicyType;
    }
    public void setClosedLooppolicyType(String closedLooppolicyType) {
        this.closedLooppolicyType = closedLooppolicyType;
    }
    public String getOnapName() {
        return onapName;
    }
    public void setOnapName(String onapName) {
        this.onapName = onapName;
    }
    public String getD2Service() {
        return d2Service;
    }
    public void setD2Service(String d2Service) {
        this.d2Service = d2Service;
    }
    public String getVnfType() {
        return vnfType;
    }
    public void setVnfType(String vnfType) {
        this.vnfType = vnfType;
    }
    public String getPolicyStatus() {
        return policyStatus;
    }
    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }
    public String getVproAction() {
        return vproAction;
    }
    public void setVproAction(String vproAction) {
        this.vproAction = vproAction;
    }
    public String getServiceType() {
        return serviceType;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public String getBindTextSearch() {
        return bindTextSearch;
    }
    public void setBindTextSearch(String bindTextSearch) {
        this.bindTextSearch = bindTextSearch;
    }
}