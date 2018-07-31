/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.rest.adapter;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TermCollector {
    private String serviceTypeId;
    private String configName;
    private DeployNowJson deploymentOption;
    private String securityZoneId;
    private String vendorServiceId;
    private VendorSpecificData vendorSpecificData= new VendorSpecificData();

    protected Set<Object> serviceGroups;
    protected Set<Object> addressGroups;
    protected List<Term> firewallRuleList;

    protected List<Tags> ruleToTag;

    public List<Tags> getRuleToTag() {
        return ruleToTag;
    }

    public void setRuleToTag(List<Tags> ruleToTag) {
        this.ruleToTag = ruleToTag;
    }

    //SecurityTypeId
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    //ConfigName
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    //DeploymentControl
    public DeployNowJson getDeploymentOption() {
        return deploymentOption;
    }

    public void setDeploymentOption(DeployNowJson deploymentOption) {
        this.deploymentOption = deploymentOption;
    }

    //SecurityZoneId
    public String getSecurityZoneId() {
        return securityZoneId;
    }
    public void setSecurityZoneId(String securityZoneId) {
        this.securityZoneId = securityZoneId;
    }


    //ServiceGroup
    public Set<Object> getServiceGroups() {
        if(serviceGroups==null)
        {
            serviceGroups= new HashSet<>();
        }
        return this.serviceGroups;
    }

    public void setServiceGroups(Set<Object> servListArray) {
        this.serviceGroups = servListArray;
    }

    //AddressGroup
    public Set<Object> getAddressGroups() {
        if(addressGroups==null)
        {
            addressGroups= new HashSet<>();
        }
        return this.addressGroups;
    }

    public void setAddressGroups(Set<Object> addressGroups) {
        this.addressGroups = addressGroups;
    }

    //FirewallRuleList
    public List<Term> getFirewallRuleList() {

        if(firewallRuleList==null)
        {
            firewallRuleList= new ArrayList<>();
        }
        return this.firewallRuleList;
    }

    public void setFirewallRuleList(List<Term> firewallRuleList) {
        this.firewallRuleList = firewallRuleList;
    }

    //vendorServiceId
    public String getVendorServiceId() {
        return vendorServiceId;
    }

    public void setVendorServiceId(String vendorServiceId) {
        this.vendorServiceId = vendorServiceId;
    }

    public VendorSpecificData getVendorSpecificData() {
        return vendorSpecificData;
    }

    public void setVendorSpecificData(VendorSpecificData vendorSpecificData) {
        this.vendorSpecificData = vendorSpecificData;
    }
}
