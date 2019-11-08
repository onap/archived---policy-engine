/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * The Class FWDictionaryJPATest.
 */
public class FwDictionaryJpaTest {

    private static Logger logger = FlexLogger.getLogger(FwDictionaryJpaTest.class);
    private UserInfo userInfo;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");
        logger.info("setUp: exit");
    }

    /**
     * Test action list.
     */
    @Test
    public void testActionList() {
        ActionList data = new ActionList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setActionName("Test");
        assertTrue("Test".equals(data.getActionName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test port list.
     */
    @Test
    public void testPortList() {
        PortList data = new PortList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPortName("Test");
        assertTrue("Test".equals(data.getPortName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test protocol list.
     */
    @Test
    public void testProtocolList() {
        ProtocolList data = new ProtocolList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setProtocolName("Test");
        assertTrue("Test".equals(data.getProtocolName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test security zone.
     */
    @Test
    public void testSecurityZone() {
        SecurityZone data = new SecurityZone();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setZoneName("Test");
        assertTrue("Test".equals(data.getZoneName()));
        data.setZoneValue("Test");
        assertTrue("Test".equals(data.getZoneValue()));
    }

    /**
     * Test zone.
     */
    @Test
    public void testZone() {
        Zone data = new Zone();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setZoneName("Test");
        assertTrue("Test".equals(data.getZoneName()));
        data.setZoneValue("Test");
        assertTrue("Test".equals(data.getZoneValue()));
    }

    /**
     * Test address group.
     */
    @Test
    public void testAddressGroup() {
        AddressGroup data = new AddressGroup();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setServiceList("Test");
        assertTrue("Test".equals(data.getPrefixList()));
        data.setGroupName("Test");
        assertTrue("Test".equals(data.getGroupName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test prefix list.
     */
    @Test
    public void testPrefixList() {
        PrefixList data = new PrefixList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setPrefixListName("Test");
        assertTrue("Test".equals(data.getPrefixListName()));
        data.setPrefixListValue("Test");
        assertTrue("Test".equals(data.getPrefixListValue()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
    }

    /**
     * Test FW dictionary list.
     */
    @Test
    public void testFwDictionaryList() {
        FirewallDictionaryList data = new FirewallDictionaryList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setParentItemName("Test");
        assertTrue("Test".equals(data.getParentItemName()));
        data.setAddressList("Test");
        assertTrue("Test".equals(data.getAddressList()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setServiceList("Test");
        assertTrue("Test".equals(data.getServiceList()));
    }

    /**
     * Test FW tag.
     */
    @Test
    public void testFwTag() {
        FwTag data = new FwTag();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setFwTagName("Test");
        assertTrue("Test".equals(data.getFwTagName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setTagValues("Test");
        assertTrue("Test".equals(data.getTagValues()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }

    /**
     * Test FW tag picker.
     */
    @Test
    public void testFwTagPicker() {
        FwTagPicker data = new FwTagPicker();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setTagPickerName("Test");
        assertTrue("Test".equals(data.getTagPickerName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getDescription()));
        data.setNetworkRole("Test");
        assertTrue("Test".equals(data.getNetworkRole()));
        data.setTagValues("Test");
        assertTrue("Test".equals(data.getTagValues()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }

    /**
     * Test service list.
     */
    @Test
    public void testServiceList() {
        ServiceList data = new ServiceList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setServiceName("Test");
        assertTrue("Test".equals(data.getServiceName()));
        data.setServiceDescription("Test");
        assertTrue("Test".equals(data.getServiceDescription()));
        data.setServiceType("Test");
        assertTrue("Test".equals(data.getServiceType()));
        data.setServiceTransProtocol("Test");
        assertTrue("Test".equals(data.getServiceTransProtocol()));
        data.setServiceAppProtocol("Test");
        assertTrue("Test".equals(data.getServiceAppProtocol()));
        data.setServicePorts("Test");
        assertTrue("Test".equals(data.getServicePorts()));
    }

    /**
     * Test term list.
     */
    @Test
    public void testTermList() {
        TermList data = new TermList();
        data.preUpdate();
        data.prePersist();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setTermName("Test");
        assertTrue("Test".equals(data.getTermName()));
        data.setDescription("Test");
        assertTrue("Test".equals(data.getTermDescription()));
        data.setFromZones("Test");
        assertTrue("Test".equals(data.getFromZone()));
        data.setToZones("Test");
        assertTrue("Test".equals(data.getToZone()));
        data.setSrcIPList("Test");
        assertTrue("Test".equals(data.getSrcIPList()));
        data.setDestIPList("Test");
        assertTrue("Test".equals(data.getDestIPList()));
        data.setProtocolList("Test");
        assertTrue("Test".equals(data.getProtocolList()));
        data.setPortList("Test");
        assertTrue("Test".equals(data.getPortList()));
        data.setSrcPortList("Test");
        assertTrue("Test".equals(data.getSrcPortList()));
        data.setDestPortList("Test");
        assertTrue("Test".equals(data.getDestPortList()));
        data.setAction("Test");
        assertTrue("Test".equals(data.getAction()));
        data.setCreatedDate(new Date());
        assertTrue(data.getCreatedDate() != null);
        data.setModifiedDate(new Date());
        assertTrue(data.getModifiedDate() != null);
        data.setUserCreatedBy(userInfo);
        assertTrue(data.getUserCreatedBy() != null);
        data.setUserModifiedBy(userInfo);
        assertTrue(data.getUserModifiedBy() != null);
    }

    /**
     * Test group service list.
     */
    @Test
    public void testGroupServiceList() {
        GroupServiceList data = new GroupServiceList();
        data.setId(1);
        assertTrue(1 == data.getId());
        data.setGroupName("Test");
        assertTrue("Test".equals(data.getGroupName()));
        data.setServiceList("Test");
        assertTrue("Test".equals(data.getServiceList()));
    }
}
