/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.rest.adapter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FirewallPolicyAdaptersTest {

    @Test
    public void AddressGroupJSON(){
        AddressGroupJson groupJSON = new AddressGroupJson();
        groupJSON.setName("Test");
        assertTrue("Test".equals(groupJSON.getName()));
        groupJSON.setDescription("Test");
        assertTrue("Test".equals(groupJSON.getDescription()));

        AddressMembersJson membersJSON = new AddressMembersJson();
        membersJSON.setType("Test");
        assertTrue("Test".equals(membersJSON.getType()));
        membersJSON.setName("Test");
        assertTrue("Test".equals(membersJSON.getName()));

        groupJSON.setMembers(null);
        assertTrue(groupJSON.getMembers() != null);
    }

    @Test
    public void testAddressMembers(){
        AddressMembers members = new AddressMembers();
        members.setName("Test");
        assertTrue("Test".equals(members.getName()));
        members.setType("Test");
        assertTrue("Test".equals(members.getType()));
        members.setValue("Test");
        assertTrue("Test".equals(members.getValue()));

        PrefixIPList prefixList = new PrefixIPList();
        prefixList.setName("Test");
        assertTrue("Test".equals(prefixList.getName()));
        prefixList.setDescription("Test");
        assertTrue("Test".equals(prefixList.getDescription()));
        prefixList.setMembers(null);
        assertTrue(prefixList.getMembers() != null);
    }

    @Test
    public void testAddressJSON(){
        AddressJson addressJSON = new AddressJson();
        addressJSON.setName("Test");
        assertTrue("Test".equals(addressJSON.getName()));
        addressJSON.setType("Test");
        assertTrue("Test".equals(addressJSON.getType()));
    }


    @Test
    public void testGridData(){
        GridData data = new GridData();
        data.setAttributes(new ArrayList<>());
        data.setAppProtocols(new ArrayList<>());
        data.setTransportProtocols(new ArrayList<>());
        assertTrue(data.getAttributes() != null);
        assertTrue(data.getAppProtocols() != null);
        assertTrue(data.getTransportProtocols() != null);
    }

    @Test
    public void testTermCollector(){

        TermCollector termCollector = new TermCollector();
        termCollector.setServiceTypeId("Test");
        assertTrue("Test".equals(termCollector.getServiceTypeId()));
        termCollector.setConfigName("Test");
        assertTrue("Test".equals(termCollector.getConfigName()));

        DeployNowJson deployNowJson = new DeployNowJson();
        deployNowJson.setDeployNow(true);
        assertTrue(deployNowJson.getDeployNow());

        termCollector.setDeploymentOption(deployNowJson);
        assertTrue(termCollector.getDeploymentOption() != null);
        termCollector.setSecurityZoneId("Test");
        assertTrue("Test".equals(termCollector.getSecurityZoneId()));
        termCollector.setVendorServiceId("Test");
        assertTrue("Test".equals(termCollector.getVendorServiceId()));

        IdMap idMap = new IdMap();
        idMap.setAstraId("Test");
        idMap.setVendorId("Test");
        assertTrue("Test".equals(idMap.getAstraId()));
        assertTrue("Test".equals(idMap.getVendorId()));

        List<IdMap> idMapList = new ArrayList<>();
        idMapList.add(idMap);

        VendorSpecificData vendorData = new VendorSpecificData();
        vendorData.setIdMap(idMapList);
        assertTrue(vendorData.getIdMap() != null);
        termCollector.setVendorSpecificData(vendorData);
        assertTrue(termCollector.getVendorSpecificData()!=null);

        termCollector.setServiceGroups(null);
        assertTrue(termCollector.getServiceGroups()!=null);
        termCollector.setAddressGroups(null);
        assertTrue(termCollector.getAddressGroups()!=null);

        Term term = new Term();
        term.setPosition("Test");
        assertTrue("Test".equals(term.getPosition()));
        term.setRuleName("Test");
        assertTrue("Test".equals(term.getRuleName()));
        term.setFromZones(null);
        assertTrue(term.getFromZones() != null);
        term.setToZones(null);
        assertTrue(term.getToZones() != null);
        term.setNegateSource(true);
        assertTrue(term.getNegateSource());
        term.setNegateDestination(true);
        assertTrue(term.getNegateDestination());
        term.setSourceList(null);
        assertTrue(term.getSourceList() != null);
        term.setDestinationList(null);
        assertTrue(term.getDestinationList() != null);
        term.setSourceServices(null);
        assertTrue(term.getSourceServices() != null);
        term.setDestServices(null);
        assertTrue(term.getDestServices() != null);
        term.setAction("Test");
        assertTrue("Test".equals(term.getAction()));
        term.setDescription("Test");
        assertTrue("Test".equals(term.getDescription()));
        term.setEnabled(true);
        assertTrue(term.getEnabled());
        term.setLog(true);
        assertTrue(term.getLog());

        termCollector.setFirewallRuleList(null);
        assertTrue(termCollector.getFirewallRuleList()!=null);

        Tags tags = new Tags();
        tags.setRuleName("Test");
        assertTrue("Test".equals(tags.getRuleName()));

        TagDefines tagDefines = new TagDefines();
        tagDefines.setKey("Test");
        assertTrue("Test".equals(tagDefines.getKey()));
        tagDefines.setValue("Test");
        assertTrue("Test".equals(tagDefines.getValue()));
        tags.setTags(new ArrayList<>());
        assertTrue(tags.getTags()!=null);
        tags.setTagPickerName("Test");
        assertTrue("Test".equals(tags.getTagPickerName()));
        tags.setNetworkRole("Test");
        assertTrue("Test".equals(tags.getNetworkRole()));
        List<Tags> ruleToTag = new ArrayList<>();
        ruleToTag.add(tags);

        termCollector.setRuleToTag(ruleToTag);
        assertTrue(termCollector.getRuleToTag()!=null);
    }

    @Test
    public void testServiceGroupJson(){
        ServiceGroupJson serviceGroup = new ServiceGroupJson();
        serviceGroup.setName("Test");
        assertTrue("Test".equals(serviceGroup.getName()));
        serviceGroup.setDescription("Test");
        assertTrue("Test".equals(serviceGroup.getDescription()));
        serviceGroup.setMembers(new ArrayList<>());
        assertTrue(serviceGroup.getMembers()!=null);
    }

    @Test
    public void testServiceListJson(){
        ServiceListJson serviceGroup = new ServiceListJson();
        serviceGroup.setName("Test");
        assertTrue("Test".equals(serviceGroup.getName()));
        serviceGroup.setDescription("Test");
        assertTrue("Test".equals(serviceGroup.getDescription()));
        serviceGroup.setType("Test");
        assertTrue("Test".equals(serviceGroup.getType()));
        serviceGroup.setTransportProtocol("Test");
        assertTrue("Test".equals(serviceGroup.getTransportProtocol()));
        serviceGroup.setAppProtocol("Test");
        assertTrue("Test".equals(serviceGroup.getAppProtocol()));
        serviceGroup.setPorts("Test");
        assertTrue("Test".equals(serviceGroup.getPorts()));
    }

    @Test
    public void testServiceMembers(){
        ServiceMembers serviceGroup = new ServiceMembers();
        serviceGroup.setName("Test");
        assertTrue("Test".equals(serviceGroup.getName()));
        serviceGroup.setType("Test");
        assertTrue("Test".equals(serviceGroup.getType()));
    }

    @Test
    public void testServiceJson(){
        ServicesJson serviceGroup = new ServicesJson();
        serviceGroup.setName("Test");
        assertTrue("Test".equals(serviceGroup.getName()));
        serviceGroup.setType("Test");
        assertTrue("Test".equals(serviceGroup.getType()));
    }

}
