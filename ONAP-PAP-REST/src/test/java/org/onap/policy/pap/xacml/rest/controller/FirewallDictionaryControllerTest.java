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
package org.onap.policy.pap.xacml.rest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.util.DictionaryUtils;
import org.onap.policy.rest.adapter.Term;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ActionList;
import org.onap.policy.rest.jpa.AddressGroup;
import org.onap.policy.rest.jpa.FWTag;
import org.onap.policy.rest.jpa.FWTagPicker;
import org.onap.policy.rest.jpa.FirewallDictionaryList;
import org.onap.policy.rest.jpa.GroupServiceList;
import org.onap.policy.rest.jpa.PortList;
import org.onap.policy.rest.jpa.PrefixList;
import org.onap.policy.rest.jpa.ProtocolList;
import org.onap.policy.rest.jpa.SecurityZone;
import org.onap.policy.rest.jpa.ServiceList;
import org.onap.policy.rest.jpa.TermList;
import org.onap.policy.rest.jpa.UserInfo;
import org.onap.policy.rest.jpa.Zone;
import org.springframework.mock.web.MockHttpServletResponse;

public class FirewallDictionaryControllerTest {

    private static Logger logger = FlexLogger.getLogger(FirewallDictionaryControllerTest.class);
    private static CommonClassDao commonClassDao;
    private String jsonString = null;
    private HttpServletRequest request = null;
    private FirewallDictionaryController controller = null;
    private MockHttpServletResponse response = null;
    private UserInfo userInfo;
    private List<String> data;

    @Before
    public void setUp() throws Exception {
        logger.info("setUp: Entering");
        commonClassDao = Mockito.mock(CommonClassDao.class);

        data = new ArrayList<>();
        data.add("Test");

        userInfo = new UserInfo();
        userInfo.setUserLoginId("Test");
        userInfo.setUserName("Test");

        doNothing().when(commonClassDao).delete(any(Term.class));
        doNothing().when(commonClassDao).save(any(Term.class));

        controller = new FirewallDictionaryController();
        FirewallDictionaryController.setCommonClassDao(commonClassDao);

        request = Mockito.mock(HttpServletRequest.class);
        response = new MockHttpServletResponse();
        new DictionaryUtils(commonClassDao);
        DictionaryUtils.setDictionaryUtils(new DictionaryUtils());
        mock(DictionaryUtils.class);
        logger.info("setUp: exit");
    }

    @Test
    public void testGetPrefixListDictionaryEntityDataByName() {
        test_WithGetDataByColumn(PrefixList.class, "prefixListDictionaryDatas", "prefixListName",
                () -> controller.getPrefixListDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetPrefixListDictionaryEntityData() {
        test_WithGetData(PrefixList.class, "prefixListDictionaryDatas",
                () -> controller.getPrefixListDictionaryEntityData(response));
    }

    @Test
    public void testGetPortListDictionaryEntityData() {
        test_WithGetData(PortList.class, "portListDictionaryDatas",
                () -> controller.getPortListDictionaryEntityData(response));
    }

    @Test
    public void testGetProtocolListDictionaryEntityDataByName() {
        test_WithGetDataByColumn(ProtocolList.class, "protocolListDictionaryDatas", "protocolName",
                () -> controller.getProtocolListDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetProtocolListDictionaryEntityData() {
        test_WithGetData(ProtocolList.class, "protocolListDictionaryDatas",
                () -> controller.getProtocolListDictionaryEntityData(response));
    }

    @Test
    public void testGetAddressGroupDictionaryEntityDataByName() {
        test_WithGetDataByColumn(AddressGroup.class, "addressGroupDictionaryDatas", "name",
                () -> controller.getAddressGroupDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetAddressGroupDictionaryEntityData() {
        test_WithGetData(AddressGroup.class, "addressGroupDictionaryDatas",
                () -> controller.getAddressGroupDictionaryEntityData(response));
    }

    @Test
    public void testGetActionListDictionaryEntityDataByName() {
        test_WithGetDataByColumn(ActionList.class, "actionListDictionaryDatas", "actionName",
                () -> controller.getActionListDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetActionListDictionaryEntityData() {
        test_WithGetData(ActionList.class, "actionListDictionaryDatas",
                () -> controller.getActionListDictionaryEntityData(response));
    }

    @Test
    public void testGetServiceGroupDictionaryEntityDataByName() {
        test_WithGetDataByColumn(GroupServiceList.class, "serviceGroupDictionaryDatas", "name",
                () -> controller.getServiceGroupDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetServiceGroupDictionaryEntityData() {
        test_WithGetData(GroupServiceList.class, "serviceGroupDictionaryDatas",
                () -> controller.getServiceGroupDictionaryEntityData(response));
    }

    @Test
    public void testGetSecurityZoneDictionaryEntityDataByName() {
        test_WithGetDataByColumn(SecurityZone.class, "securityZoneDictionaryDatas", "zoneName",
                () -> controller.getSecurityZoneDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetSecurityZoneDictionaryEntityData() {
        test_WithGetData(SecurityZone.class, "securityZoneDictionaryDatas",
                () -> controller.getSecurityZoneDictionaryEntityData(response));
    }

    @Test
    public void testGetServiceListDictionaryEntityDataByName() {
        test_WithGetDataByColumn(ServiceList.class, "serviceListDictionaryDatas", "serviceName",
                () -> controller.getServiceListDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetServiceListDictionaryEntityData() {
        test_WithGetData(ServiceList.class, "serviceListDictionaryDatas",
                () -> controller.getServiceListDictionaryEntityData(response));
    }

    @Test
    public void testGetZoneDictionaryEntityDataByName() {
        test_WithGetDataByColumn(Zone.class, "zoneDictionaryDatas", "zoneName",
                () -> controller.getZoneDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetZoneDictionaryEntityData() {
        test_WithGetData(Zone.class, "zoneDictionaryDatas", () -> controller.getZoneDictionaryEntityData(response));
    }

    @Test
    public void testGetTermListDictionaryEntityDataByName() {
        test_WithGetDataByColumn(TermList.class, "termListDictionaryDatas", "termName",
                () -> controller.getTermListDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetTermListDictionaryEntityData() {
        test_WithGetData(TermList.class, "termListDictionaryDatas",
                () -> controller.getTermListDictionaryEntityData(response));
    }

    @Test
    public void testGetFWDictListDictionaryEntityDataByName() {
        test_WithGetDataByColumn(FirewallDictionaryList.class, "fwDictListDictionaryDatas", "parentItemName",
                () -> controller.getFWDictListDictionaryEntityDataByName(response));
    }

    @Test
    public void testGetFWDictionaryListEntityData() {
        test_WithGetData(FirewallDictionaryList.class, "fwDictListDictionaryDatas",
                () -> controller.getFWDictionaryListEntityData(response));
    }

    @Test
    public void testGetTagPickerNameEntityDataByName() {
        test_WithGetDataByColumn(FWTagPicker.class, "fwTagPickerDictionaryDatas", "tagPickerName",
                () -> controller.getTagPickerNameEntityDataByName(response));
    }

    @Test
    public void testGetTagPickerDictionaryEntityData() {
        test_WithGetData(FWTagPicker.class, "fwTagPickerDictionaryDatas",
                () -> controller.getTagPickerDictionaryEntityData(response));
    }

    @Test
    public void testGetTagNameEntityDataByName() {
        test_WithGetDataByColumn(FWTag.class, "fwTagDictionaryDatas", "fwTagName",
                () -> controller.getTagNameEntityDataByName(response));
    }

    @Test
    public void testGetTagDictionaryEntityData() {
        test_WithGetData(FWTag.class, "fwTagDictionaryDatas", () -> controller.getTagDictionaryEntityData(response));
    }

    @Test
    public void testSavePrefixListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"prefixListDictionaryData\":{\"description\":\"test\",\"prefixListName\":\"Test\"}}";
        testSave(PrefixList.class, "prefixListDictionaryDatas", "prefixListName",
                () -> controller.savePrefixListDictionary(request, response));
    }

    @Test
    public void testUpdatePrefixListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"prefixListDictionaryData\":{\"id\":1,\"description\":\"test\",\"prefixListName\":\"Test\"}}";
        testUpdate(PrefixList.class, "prefixListDictionaryDatas", "prefixListName",
                () -> controller.savePrefixListDictionary(request, response));
    }

    @Test
    public void testRemovePrefixListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"prefixListName\":\"Test\"}}";
        testRemove(PrefixList.class, "prefixListDictionaryDatas",
                () -> controller.removePrefixListDictionary(request, response));
    }

    @Test
    public void testValidatePrefixListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"prefixListDictionaryData\":{\"id\":1,\"description\":\"test\",\"prefixListName\":\"Test\",\"prefixListValue\":\"10.10.10\"}}";
        testValidate(PrefixList.class, "result", () -> controller.validatePrefixListDictionary(request, response));
    }

    @Test
    public void testSavePortListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"portListDictionaryData\":{\"description\":\"test\",\"portName\":\"Test\"}}";
        testSave(PortList.class, "portListDictionaryDatas", "portName",
                () -> controller.savePortListDictionary(request, response));
    }

    @Test
    public void testUpdatePortListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"portListDictionaryData\":{\"id\":1,\"description\":\"test\",\"portName\":\"Test\"}}";
        testUpdate(PortList.class, "portListDictionaryDatas", "portName",
                () -> controller.savePortListDictionary(request, response));
    }

    @Test
    public void testRemovePortListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"portName\":\"Test\"}}";
        testRemove(PortList.class, "portListDictionaryDatas",
                () -> controller.removePortListDictionary(request, response));
    }

    @Test
    public void testSaveProtocolListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"protocolListDictionaryData\":{\"description\":\"test\",\"protocolName\":\"Test\"}}";
        testSave(ProtocolList.class, "protocolListDictionaryDatas", "protocolName",
                () -> controller.saveProtocolListDictionary(request, response));
    }

    @Test
    public void testUpdateProtocolListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"protocolListDictionaryData\":{\"id\":1,\"description\":\"test\",\"protocolName\":\"Test\"}}";
        testUpdate(ProtocolList.class, "protocolListDictionaryDatas", "protocolName",
                () -> controller.saveProtocolListDictionary(request, response));
    }

    @Test
    public void testRemoveProtocolListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"protocolName\":\"Test\"}}";
        testRemove(ProtocolList.class, "protocolListDictionaryDatas",
                () -> controller.removeProtocolListDictionary(request, response));
    }

    @Test
    public void testSaveAddressGroupDictionary() {
        jsonString = "{\"addressGroupDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:409\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"groupName\":\"Test\"},\"userid\":\"demo\"}";
        testSave(AddressGroup.class, "addressGroupDictionaryDatas", "name", "Group_Test",
                () -> controller.saveAddressGroupDictionary(request, response));
    }

    @Test
    public void testUpdateAddressGroupDictionary() {
        jsonString = "{\"addressGroupDictionaryData\":{\"id\":1, \"attributes\":[{\"$$hashKey\":\"object:409\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"groupName\":\"Test\"},\"userid\":\"demo\"}";
        testUpdate(AddressGroup.class, "addressGroupDictionaryDatas", "name", "Group_Test",
                () -> controller.saveAddressGroupDictionary(request, response));
    }

    @Test
    public void testRemoveAddressGroupDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        testRemove(AddressGroup.class, "addressGroupDictionaryDatas",
                () -> controller.removeAddressGroupDictionary(request, response));
    }

    @Test
    public void testSaveActionListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"actionListDictionaryData\":{\"description\":\"test\",\"actionName\":\"Test\"}}";
        testSave(ActionList.class, "actionListDictionaryDatas", "actionName",
                () -> controller.saveActionListDictionary(request, response));
    }

    @Test
    public void testUpdateActionListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"actionListDictionaryData\":{\"id\":1,\"description\":\"test\",\"actionName\":\"Test\"}}";
        testUpdate(ActionList.class, "actionListDictionaryDatas", "actionName",
                () -> controller.saveActionListDictionary(request, response));
    }

    @Test
    public void testRemoveActionListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"actionName\":\"Test\"}}";
        testRemove(ActionList.class, "actionListDictionaryDatas",
                () -> controller.removeActionListDictionary(request, response));
    }

    @Test
    public void testSaveServiceGroupDictionary() {
        jsonString = "{\"serviceGroupDictionaryData\":{\"attributes\":[{\"$$hashKey\":\"object:657\",\"id\":\"choice1\",\"option\":\"Test\"}],\"groupName\":\"Test\"},\"userid\":\"demo\"}";
        testSave(GroupServiceList.class, "serviceGroupDictionaryDatas", "name", "Group_Test",
                () -> controller.saveServiceGroupDictionary(request, response));
    }

    @Test
    public void testUpdateServiceGroupDictionary() {
        jsonString = "{\"serviceGroupDictionaryData\":{\"id\":1, \"attributes\":[{\"$$hashKey\":\"object:657\",\"id\":\"choice1\",\"option\":\"Test\"}],\"groupName\":\"Test\"},\"userid\":\"demo\"}";
        testUpdate(GroupServiceList.class, "serviceGroupDictionaryDatas", "name", "Group_Test",
                () -> controller.saveServiceGroupDictionary(request, response));
    }

    @Test
    public void testRemoveServiceGroupDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"name\":\"Test\"}}";
        testRemove(GroupServiceList.class, "serviceGroupDictionaryDatas",
                () -> controller.removeServiceGroupDictionary(request, response));
    }

    @Test
    public void testSaveSecurityZoneDictionary() {
        jsonString = "{\"userid\":\"demo\",\"securityZoneDictionaryData\":{\"description\":\"test\",\"zoneName\":\"Test\"}}";
        testSave(SecurityZone.class, "securityZoneDictionaryDatas", "zoneName",
                () -> controller.saveSecurityZoneDictionary(request, response));
    }

    @Test
    public void testUpdateSecurityZoneDictionary() {
        jsonString = "{\"userid\":\"demo\",\"securityZoneDictionaryData\":{\"id\":1,\"description\":\"test\",\"zoneName\":\"Test\"}}";
        testUpdate(SecurityZone.class, "securityZoneDictionaryDatas", "zoneName",
                () -> controller.saveSecurityZoneDictionary(request, response));
    }

    @Test
    public void testRemoveSecurityZoneDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"zoneName\":\"Test\"}}";
        testRemove(SecurityZone.class, "securityZoneDictionaryDatas",
                () -> controller.removeSecurityZoneDictionary(request, response));
    }

    @Test
    public void testSaveServiceListDictionary() {
        jsonString = "{\"serviceListDictionaryData\":{\"appProtocols\":[{\"$$hashKey\":\"object:560\",\"id\":\"choice1\",\"option\":\"Test\"}],\"serviceDescription\":\"test\",\"serviceName\":\"Test\",\"servicePorts\":\"1010\",\"transportProtocols\":[{\"$$hashKey\":\"object:555\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testSave(ServiceList.class, "serviceListDictionaryDatas", "serviceName",
                () -> controller.saveServiceListDictionary(request, response));
    }

    @Test
    public void testUpdateServiceListDictionary() {
        jsonString = "{\"serviceListDictionaryData\":{\"appProtocols\":[{\"$$hashKey\":\"object:560\",\"id\":\"choice1\",\"option\":\"Test\"}],\"serviceDescription\":\"test\",\"id\":1,\"serviceName\":\"Test\",\"servicePorts\":\"1010\",\"transportProtocols\":[{\"$$hashKey\":\"object:555\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testUpdate(ServiceList.class, "serviceListDictionaryDatas", "serviceName",
                () -> controller.saveServiceListDictionary(request, response));
    }

    @Test
    public void testRemoveServiceListDictionary() {
        jsonString = "{\"data\":{\"appProtocols\":[{\"$$hashKey\":\"object:560\",\"id\":\"choice1\",\"option\":\"Test\"}],\"serviceDescription\":\"test\",\"id\":1,\"serviceName\":\"Test\",\"servicePorts\":\"1010\",\"transportProtocols\":[{\"$$hashKey\":\"object:555\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testRemove(ServiceList.class, "serviceListDictionaryDatas",
                () -> controller.removeServiceListDictionary(request, response));
    }

    @Test
    public void testSaveZoneDictionary() {
        jsonString = "{\"userid\":\"demo\",\"zoneDictionaryData\":{\"zoneValue\":\"test\",\"zoneName\":\"Test\"}}";
        testSave(Zone.class, "zoneDictionaryDatas", "zoneName", () -> controller.saveZoneDictionary(request, response));
    }

    @Test
    public void testUpdateZoneDictionary() {
        jsonString = "{\"userid\":\"demo\",\"zoneDictionaryData\":{\"id\":1,\"zoneValue\":\"test\",\"zoneName\":\"Test\"}}";
        testUpdate(Zone.class, "zoneDictionaryDatas", "zoneName",
                () -> controller.saveZoneDictionary(request, response));
    }

    @Test
    public void testRemoveZoneDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"zoneValue\":\"test\",\"zoneName\":\"Test\"}}";
        testRemove(Zone.class, "zoneDictionaryDatas", () -> controller.removeZoneDictionary(request, response));
    }

    @Test
    public void testSaveTermListDictionary() {
        jsonString = "{\"termListDictionaryData\":{\"actionListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationServiceDatas\":[{\"$$hashKey\":\"object:1230\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"fromZoneDatas\":[{\"$$hashKey\":\"object:1245\",\"id\":\"choice1\",\"option\":\"Test\"}],\"sourceListDatas\":[{\"$$hashKey\":\"object:1215\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"sourceServiceDatas\":[{\"$$hashKey\":\"object:1225\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"termDescription\":\"test\",\"termName\":\"Test\",\"toZoneDatas\":[{\"$$hashKey\":\"object:1240\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testSave(TermList.class, "termListDictionaryDatas", "termName",
                () -> controller.saveTermListDictionary(request, response));
    }

    @Test
    public void testUpdateTermListDictionary() {
        jsonString = "{\"termListDictionaryData\":{\"id\":1,\"actionListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationListDatas\":[{\"$$hashKey\":\"object:1220\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"destinationServiceDatas\":[{\"$$hashKey\":\"object:1230\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"fromZoneDatas\":[{\"$$hashKey\":\"object:1245\",\"id\":\"choice1\",\"option\":\"Test\"}],\"sourceListDatas\":[{\"$$hashKey\":\"object:1215\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"sourceServiceDatas\":[{\"$$hashKey\":\"object:1225\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"termDescription\":\"test\",\"termName\":\"Test\",\"toZoneDatas\":[{\"$$hashKey\":\"object:1240\",\"id\":\"choice1\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testUpdate(TermList.class, "termListDictionaryDatas", "termName",
                () -> controller.saveTermListDictionary(request, response));
    }

    @Test
    public void testRemoveTermListDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"termDescription\":\"test\",\"termName\":\"Test\"}}";
        testRemove(TermList.class, "termListDictionaryDatas",
                () -> controller.removeTermListDictionary(request, response));
    }

    @Test
    public void testSaveFWDictionaryList() {
        jsonString = "{\"fwDictListDictionaryData\":{\"alAttributes\":[{\"$$hashKey\":\"object:1379\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"attributes\":[{\"$$hashKey\":\"object:1374\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"parentItemName\":\"Test\"},\"userid\":\"demo\"}";
        testSave(FirewallDictionaryList.class, "fwDictListDictionaryDatas", "parentItemName",
                () -> controller.saveFWDictionaryList(request, response));
    }

    @Test
    public void testUpdateFWDictionaryList() {
        jsonString = "{\"fwDictListDictionaryData\":{\"id\":1,\"alAttributes\":[{\"$$hashKey\":\"object:1379\",\"id\":\"choice1\",\"option\":\"Group_Test\"}],\"attributes\":[{\"$$hashKey\":\"object:1374\",\"id\":\"choice1\",\"option\":\"Test\"}],\"description\":\"test\",\"parentItemName\":\"Test\"},\"userid\":\"demo\"}";
        testUpdate(FirewallDictionaryList.class, "fwDictListDictionaryDatas", "parentItemName",
                () -> controller.saveFWDictionaryList(request, response));
    }

    @Test
    public void testRemoveFWDictionaryList() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"parentItemName\":\"Test\"}}";
        testRemove(FirewallDictionaryList.class, "fwDictListDictionaryDatas",
                () -> controller.removeFWDictionaryList(request, response));
    }

    @Test
    public void testSaveFirewallTagPickerDictionary() {
        jsonString = "{\"fwTagPickerDictionaryData\":{\"description\":\"test\",\"networkRole\":\"test\",\"tagPickerName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1855\",\"id\":\"choice1\",\"number\":\"test\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testSave(FWTagPicker.class, "fwTagPickerDictionaryDatas", "tagPickerName",
                () -> controller.saveFirewallTagPickerDictionary(request, response));
    }

    @Test
    public void testUpdateFirewallTagPickerDictionary() {
        jsonString = "{\"fwTagPickerDictionaryData\":{\"id\":1,\"description\":\"test\",\"networkRole\":\"test\",\"tagPickerName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1855\",\"id\":\"choice1\",\"number\":\"test\",\"option\":\"Test\"}]},\"userid\":\"demo\"}";
        testUpdate(FWTagPicker.class, "fwTagPickerDictionaryDatas", "tagPickerName",
                () -> controller.saveFirewallTagPickerDictionary(request, response));
    }

    @Test
    public void testRemoveFirewallTagPickerDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"tagPickerName\":\"Test\"}}";
        testRemove(FWTagPicker.class, "fwTagPickerDictionaryDatas",
                () -> controller.removeFirewallTagPickerDictionary(request, response));
    }

    @Test
    public void testSaveFirewallTagDictionary() {
        jsonString = "{\"fwTagDictionaryData\":{\"description\":\"test\",\"fwTagName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1690\",\"id\":\"choice1\",\"tags\":\"test\"}]},\"userid\":\"demo\"}";
        testSave(FWTag.class, "fwTagDictionaryDatas", "fwTagName",
                () -> controller.saveFirewallTagDictionary(request, response));
    }

    @Test
    public void testUpdateFirewallTagDictionary() {
        jsonString = "{\"fwTagDictionaryData\":{\"id\":1,\"description\":\"test\",\"fwTagName\":\"Test\",\"tags\":[{\"$$hashKey\":\"object:1690\",\"id\":\"choice1\",\"tags\":\"test\"}]},\"userid\":\"demo\"}";
        testUpdate(FWTag.class, "fwTagDictionaryDatas", "fwTagName",
                () -> controller.saveFirewallTagDictionary(request, response));
    }

    @Test
    public void testRemoveFirewallTagDictionary() {
        jsonString = "{\"userid\":\"demo\",\"data\":{\"id\":1,\"description\":\"test\",\"fwTagName\":\"Test\"}}";
        testRemove(FWTag.class, "fwTagDictionaryDatas",
                () -> controller.removeFirewallTagDictionary(request, response));
    }

    /**
     * Tests a "get" function that uses commonClassDao.getDataByColumn().
     *
     * @param clazz
     * @param contentData
     * @param contentName
     * @param func
     */
    private void test_WithGetDataByColumn(Class<?> clazz, String contentData, String contentName, VoidFunc func) {
        when(commonClassDao.getDataByColumn(clazz, contentName)).thenReturn(data);
        try {
            func.apply();
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains(contentData));
            verify(commonClassDao).getDataByColumn(clazz, contentName);
        } catch (Exception e) {
            fail("get " + clazz.getName() + e);
        }
    }

    /**
     * Tests a "get" function that uses commonClassDao.getData().
     *
     * @param clazz
     * @param contentData
     * @param contentName
     * @param func
     */
    private void test_WithGetData(Class<?> clazz, String contentData, VoidFunc func) {
        when(commonClassDao.getData(clazz)).thenReturn(new ArrayList<>());
        try {
            func.apply();
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains(contentData));
            verify(commonClassDao).getData(clazz);
        } catch (Exception e) {
            fail("get " + clazz.getName() + e);
        }
    }

    /**
     * Tests a function that uses commonClassDao.save().
     *
     * @param clazz
     * @param contentData
     * @param contentName
     * @param func
     */
    private void testSave(Class<?> clazz, String contentData, String contentName, VoidFunc func) {
        testSave(clazz, contentData, contentName, "Test", func);
    }

    /**
     * Tests a function that uses commonClassDao.save().
     *
     * @param clazz
     * @param contentData
     * @param contentName
     * @param testName
     * @param func
     */
    private void testSave(Class<?> clazz, String contentData, String contentName, String testName, VoidFunc func) {
        try (BufferedReader br = new BufferedReader(new StringReader(jsonString))) {
            when(request.getReader()).thenReturn(br);
            func.apply();
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains(contentData));
            verify(commonClassDao).checkDuplicateEntry(testName, contentName, clazz);
            verify(commonClassDao).save(any());
            verify(commonClassDao, never()).update(any());
            verify(commonClassDao).getData(clazz);

        } catch (IOException e) {
            fail("save " + clazz.getName() + e);
        }
    }

    /**
     * Tests a function that uses commonClassDao.update().
     *
     * @param clazz
     * @param contentData
     * @param contentName
     * @param func
     */
    private void testUpdate(Class<?> clazz, String contentData, String contentName, VoidFunc func) {
        testUpdate(clazz, contentData, contentName, "Test", func);
    }

    /**
     * Tests a function that uses commonClassDao.update().
     *
     * @param clazz
     * @param contentData
     * @param contentName
     * @param testName
     * @param func
     */
    private void testUpdate(Class<?> clazz, String contentData, String contentName, String testName, VoidFunc func) {
        try (BufferedReader br = new BufferedReader(new StringReader(jsonString))) {
            when(request.getReader()).thenReturn(br);
            func.apply();
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains(contentData));
            verify(commonClassDao).checkDuplicateEntry(testName, contentName, clazz);
            verify(commonClassDao, never()).save(any());
            verify(commonClassDao).update(any());
            verify(commonClassDao).getData(clazz);

        } catch (IOException e) {
            fail("update " + clazz.getName() + e);
        }
    }

    /**
     * Tests a function that uses commonClassDao.delete() and
     * commonClassDao.getData().
     *
     * @param clazz
     * @param contentData
     * @param func
     */
    private void testRemove(Class<?> clazz, String contentData, VoidFunc func) {
        try (BufferedReader br = new BufferedReader(new StringReader(jsonString))) {
            when(request.getReader()).thenReturn(br);
            func.apply();
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains(contentData));
            verify(commonClassDao).delete(any());
            verify(commonClassDao).getData(clazz);

        } catch (IOException e) {
            fail("remove " + clazz.getName() + e);
        }
    }

    /**
     * Tests a "validate" function.
     *
     * @param clazz
     * @param contentData
     * @param func
     */
    private void testValidate(Class<?> clazz, String contentData, VoidFunc func) {
        try (BufferedReader br = new BufferedReader(new StringReader(jsonString))) {
            when(request.getReader()).thenReturn(br);
            func.apply();
            assertTrue(response.getContentAsString() != null && response.getContentAsString().contains(contentData));

        } catch (IOException e) {
            fail("save " + clazz.getName() + e);
        }
    }

    @FunctionalInterface
    private static interface VoidFunc {
        public void apply() throws IOException;
    }
}
