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


import java.util.Map;

public class ClosedLoopPMBody {

    private boolean trinity;
    private boolean vUSP;
    private boolean mcr;
    private boolean gamma;
    private boolean vDNS;
    private String geoLink;
    private String vServices;
    private String ONAPname;

    private String emailAddress;

    private String serviceTypePolicyName;

    private Map<String, String> attributes;
    private String templateVersion;

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
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

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> map) {
        this.attributes = map;
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

    public String getServiceTypePolicyName() {
        return serviceTypePolicyName;
    }

    public void setServiceTypePolicyName(String serviceTypePolicyName) {
        this.serviceTypePolicyName = serviceTypePolicyName;
    }

    public boolean isGamma() {
        return gamma;
    }
    public void setGamma(boolean gamma) {
        this.gamma = gamma;
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

}

