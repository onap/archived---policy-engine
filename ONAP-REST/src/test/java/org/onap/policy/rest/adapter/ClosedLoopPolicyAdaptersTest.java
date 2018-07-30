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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class ClosedLoopPolicyAdaptersTest {

    @Test
    public void testClosedLoopFaultTrapDatas(){
        List<Object> trap = new ArrayList<>();
        trap.add("Test");
        ClosedLoopFaultTrapDatas closedLoopTrapData = new ClosedLoopFaultTrapDatas();
        closedLoopTrapData.setTrap1(trap);
        assertTrue("Test".equals(closedLoopTrapData.getTrap1().get(0)));
        closedLoopTrapData.setTrap2(trap);
        assertTrue("Test".equals(closedLoopTrapData.getTrap2().get(0)));
        closedLoopTrapData.setTrap3(trap);
        assertTrue("Test".equals(closedLoopTrapData.getTrap3().get(0)));
        closedLoopTrapData.setTrap4(trap);
        assertTrue("Test".equals(closedLoopTrapData.getTrap4().get(0)));
        closedLoopTrapData.setTrap5(trap);
        assertTrue("Test".equals(closedLoopTrapData.getTrap5().get(0)));
        closedLoopTrapData.setTrap6(trap);
        assertTrue("Test".equals(closedLoopTrapData.getTrap6().get(0)));

    }

    @Test
    public void testClosedLoopFaultBody(){
        ClosedLoopFaultBody faultBody = new ClosedLoopFaultBody();
        faultBody.setTrinity(true);
        assertTrue(faultBody.isTrinity());
        faultBody.setvUSP(true);
        assertTrue(faultBody.isvUSP());
        faultBody.setMcr(true);
        assertTrue(faultBody.isMcr());
        faultBody.setGamma(true);
        assertTrue(faultBody.isGamma());
        faultBody.setvDNS(true);
        assertTrue(faultBody.isvDNS());
        faultBody.setVnfType("Test");
        assertTrue("Test".equals(faultBody.getVnfType()));
        faultBody.setvServices("Test");
        assertTrue("Test".equals(faultBody.getvServices()));
        faultBody.setONAPname("Test");
        assertTrue("Test".equals(faultBody.getONAPname()));
        faultBody.setClosedLoopPolicyStatus("Active");
        assertTrue("Active".equals(faultBody.getClosedLoopPolicyStatus()));

        ClosedLoopSignatures triggerSignatures = new ClosedLoopSignatures();
        triggerSignatures.setSignatures("Test");
        triggerSignatures.setTimeWindow(1);
        triggerSignatures.setTrapMaxAge(2);
        assertTrue("Test".equals(triggerSignatures.getSignatures()));
        assertTrue(1 == triggerSignatures.getTimeWindow());
        assertTrue(2 == triggerSignatures.getTrapMaxAge());

        faultBody.setTriggerSignatures(triggerSignatures);
        assertTrue(faultBody.getTriggerSignatures()!=null);
        faultBody.setActions("Test");
        assertTrue("Test".equals(faultBody.getActions()));
        faultBody.setTimeInterval(1);
        assertTrue(1 == faultBody.getTimeInterval());
        faultBody.setTimeOutvPRO(2);
        assertTrue(2 == faultBody.getTimeOutvPRO());
        faultBody.setTimeOutRuby(3);
        assertTrue(3 == faultBody.getTimeOutRuby());
        faultBody.setRetrys(4);
        assertTrue(4 == faultBody.getRetrys());
        faultBody.setAgingWindow(5);
        assertTrue(5 == faultBody.getAgingWindow());
        faultBody.setGeoLink("Test");
        assertTrue("Test".equals(faultBody.getGeoLink()));
        faultBody.setEmailAddress("test@test.com");
        assertTrue("test@test.com".equals(faultBody.getEmailAddress()));
        faultBody.setVerificationSignatures(triggerSignatures);
        assertTrue(faultBody.getVerificationSignatures()!=null);

        faultBody.setConditions(ClosedLoopPolicyConditions.SEND);
        assertTrue(ClosedLoopPolicyConditions.SEND.equals(faultBody.getConditions()));

        faultBody.setConditions(ClosedLoopPolicyConditions.NOTSEND);
        assertTrue(ClosedLoopPolicyConditions.NOTSEND.equals(faultBody.getConditions()));

        ClosedLoopFaultTriggerUISignatures uiSignatures = new ClosedLoopFaultTriggerUISignatures();
        uiSignatures.setConnectSignatures("Test");
        uiSignatures.setSignatures("Test");
        assertTrue("Test".equals(uiSignatures.getConnectSignatures()));
        assertTrue("Test".equals(uiSignatures.getSignatures()));

        faultBody.setTriggerSignaturesUsedForUI(uiSignatures);
        assertTrue(faultBody.getTriggerSignaturesUsedForUI()!=null);
        faultBody.setVerificationSignaturesUsedForUI(uiSignatures);
        assertTrue(faultBody.getVerificationSignaturesUsedForUI()!=null);
        faultBody.setTriggerTimeWindowUsedForUI(1);
        assertTrue(1 == faultBody.getTriggerTimeWindowUsedForUI());
        faultBody.setVerificationTimeWindowUsedForUI(2);
        assertTrue(2 == faultBody.getVerificationTimeWindowUsedForUI());
        faultBody.setPepName("Test");
        assertTrue("Test".equals(faultBody.getPepName()));
        faultBody.setPepAction("Test");
        assertTrue("Test".equals(faultBody.getPepAction()));
        faultBody.setTemplateVersion("1802");
        assertTrue("1802".equals(faultBody.getTemplateVersion()));
        faultBody.setTrapMaxAge(3);
        assertTrue(3 == faultBody.getTrapMaxAge());
    }

    @Test
    public void testClosedLoopPMBody(){
        ClosedLoopPMBody pmBody = new ClosedLoopPMBody();
        pmBody.setTrinity(true);
        assertTrue(pmBody.isTrinity());
        pmBody.setvUSP(true);
        assertTrue(pmBody.isvUSP());
        pmBody.setMcr(true);
        assertTrue(pmBody.isMcr());
        pmBody.setGamma(true);
        assertTrue(pmBody.isGamma());
        pmBody.setvDNS(true);
        assertTrue(pmBody.isvDNS());
        pmBody.setvServices("Test");
        assertTrue("Test".equals(pmBody.getvServices()));
        pmBody.setONAPname("Test");
        assertTrue("Test".equals(pmBody.getONAPname()));
        pmBody.setEmailAddress("test@test.com");
        assertTrue("test@test.com".equals(pmBody.getEmailAddress()));
        pmBody.setServiceTypePolicyName("Test");
        assertTrue("Test".equals(pmBody.getServiceTypePolicyName()));
        pmBody.setTemplateVersion("1802");
        assertTrue("1802".equals(pmBody.getTemplateVersion()));
        pmBody.setAttributes(new HashMap<String, String>());
        assertTrue(pmBody.getAttributes() != null);
        pmBody.setGeoLink("Test");
        assertTrue("Test".equals(pmBody.getGeoLink()));
    }

    @Test
    public void testClosedLoopPolicyStatus() {
        assertEquals(ClosedLoopPolicyStatus.ACTIVE.toString(), "active");
        assertEquals(ClosedLoopPolicyStatus.INACTIVE.toString(), "inactive");
    }
}
