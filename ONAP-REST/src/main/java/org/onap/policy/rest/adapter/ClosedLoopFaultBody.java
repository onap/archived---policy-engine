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



public class ClosedLoopFaultBody {

    private boolean trinity;
    private boolean vUSP;
    private boolean mcr;
    private boolean gamma;
    private boolean vDNS;

    private String vnfType;
    private String vServices;
    private String ONAPname;

    private String closedLoopPolicyStatus;
    private ClosedLoopSignatures triggerSignatures;
    private String actions;
    private int timeInterval;
    private int timeOutvPRO;
    private int timeOutRuby;
    private int retrys;
    private int agingWindow;
    private String geoLink;
    private String emailAddress;
    private ClosedLoopSignatures verificationSignatures;
    private ClosedLoopPolicyConditions conditions;
    private ClosedLoopFaultTriggerUISignatures triggerSignaturesUsedForUI;
    private ClosedLoopFaultTriggerUISignatures verificationSignaturesUsedForUI;
    private int triggerTimeWindowUsedForUI;
    private int verificationTimeWindowUsedForUI;
    private String pepName;
    private String pepAction;
    private String templateVersion;
    private int trapMaxAge;


    public Integer getTrapMaxAge() {
        return trapMaxAge;
    }

    public void setTrapMaxAge(int trapMaxAge) {
        this.trapMaxAge = trapMaxAge;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Integer getTimeOutvPRO() {
        return timeOutvPRO;
    }

    public void setTimeOutvPRO(int timeOutvPRO) {
        this.timeOutvPRO = timeOutvPRO;
    }


    public Integer getTriggerTimeWindowUsedForUI() {
        return triggerTimeWindowUsedForUI;
    }

    public String getPepName() {
        return pepName;
    }

    public void setPepName(String pepName) {
        this.pepName = pepName;
    }

    public String getPepAction() {
        return pepAction;
    }

    public void setPepAction(String pepAction) {
        this.pepAction = pepAction;
    }

    public void setTriggerTimeWindowUsedForUI(int triggerTimeWindowUsedForUI) {
        this.triggerTimeWindowUsedForUI = triggerTimeWindowUsedForUI;
    }

    public Integer getVerificationTimeWindowUsedForUI() {
        return verificationTimeWindowUsedForUI;
    }

    public void setVerificationTimeWindowUsedForUI(
            int verificationTimeWindowUsedForUI) {
        this.verificationTimeWindowUsedForUI = verificationTimeWindowUsedForUI;
    }

    public String getONAPname(){
        return ONAPname;
    }

    public void setONAPname(String ONAPname){
        this.ONAPname = ONAPname;
    }

    public String getvServices() {
        return vServices;
    }
    public void setvServices(String vServices) {
        this.vServices = vServices;
    }

    public ClosedLoopFaultTriggerUISignatures getVerificationSignaturesUsedForUI() {
        return verificationSignaturesUsedForUI;
    }
    public void setVerificationSignaturesUsedForUI(
            ClosedLoopFaultTriggerUISignatures verificationSignaturesUsedForUI) {
        this.verificationSignaturesUsedForUI = verificationSignaturesUsedForUI;
    }
    public ClosedLoopFaultTriggerUISignatures getTriggerSignaturesUsedForUI() {
        return triggerSignaturesUsedForUI;
    }
    public void setTriggerSignaturesUsedForUI(
            ClosedLoopFaultTriggerUISignatures triggerSignaturesUsedForUI) {
        this.triggerSignaturesUsedForUI = triggerSignaturesUsedForUI;
    }
    public ClosedLoopPolicyConditions getConditions() {
        return conditions;
    }
    public void setConditions(ClosedLoopPolicyConditions conditions) {
        this.conditions = conditions;
    }

    public String getVnfType() {
        return vnfType;
    }
    public void setVnfType(String vnfType) {
        this.vnfType = vnfType;
    }

    public Integer getAgingWindow() {
        return agingWindow;
    }
    public void setAgingWindow(int agingWindow) {
        this.agingWindow = agingWindow;
    }

    public String getClosedLoopPolicyStatus() {
        return closedLoopPolicyStatus;
    }
    public void setClosedLoopPolicyStatus(
            String closedLoopPolicyStatus) {
        this.closedLoopPolicyStatus = closedLoopPolicyStatus;
    }
    public ClosedLoopSignatures getTriggerSignatures() {
        return triggerSignatures;
    }
    public void setTriggerSignatures(ClosedLoopSignatures triggerSignatures) {
        this.triggerSignatures = triggerSignatures;
    }
    public String getActions() {
        return actions;
    }
    public void setActions(String actions) {
        this.actions = actions;
    }
    public Integer getTimeInterval() {
        return timeInterval;
    }
    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }
    public Integer getTimeOutRuby() {
        return timeOutRuby;
    }
    public void setTimeOutRuby(int timeOutRuby) {
        this.timeOutRuby = timeOutRuby;
    }
    public Integer getRetrys() {
        return retrys;
    }
    public void setRetrys(int retrys) {
        this.retrys = retrys;
    }
    public String getGeoLink() {
        return geoLink;
    }
    public void setGeoLink(String geoLink) {
        this.geoLink = geoLink;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public ClosedLoopSignatures getVerificationSignatures() {
        return verificationSignatures;
    }
    public void setVerificationSignatures(
            ClosedLoopSignatures verificationSignatures) {
        this.verificationSignatures = verificationSignatures;
    }

    public boolean isvDNS() {
        return vDNS;
    }

    public void setvDNS(boolean vDNS) {
        this.vDNS = vDNS;
    }

    public boolean isTrinity() {
        return trinity;
    }

    public void setTrinity(boolean trinity) {
        this.trinity = trinity;
    }

    public boolean isvUSP() {
        return vUSP;
    }

    public void setvUSP(boolean vUSP) {
        this.vUSP = vUSP;
    }

    public boolean isMcr() {
        return mcr;
    }

    public void setMcr(boolean mcr) {
        this.mcr = mcr;
    }

    public boolean isGamma() {
        return gamma;
    }

    public void setGamma(boolean gamma) {
        this.gamma = gamma;
    }
}

