/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

import java.util.List;

public class RainyDayParams {
    private String serviceType;
    private String vnfType;
    private String bbid;
    private String workstep;
    private List<Object> treatmentTableChoices;
    private List<String> errorcode;
    private List<String> treatment;

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }
    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    /**
     * @return the vnfType
     */
    public String getVnfType() {
        return vnfType;
    }
    /**
     * @param vnfType the vnfType to set
     */
    public void setVnfType(String vnfType) {
        this.vnfType = vnfType;
    }
    /**
     * @return the workstep
     */
    public String getWorkstep() {
        return workstep;
    }
    /**
     * @param workstep the workstep to set
     */
    public void setWorkstep(String workstep) {
        this.workstep = workstep;
    }
    /**
     * @return the bbid
     */
    public String getBbid() {
        return bbid;
    }
    /**
     * @param bbid the bbid to set
     */
    public void setBbid(String bbid) {
        this.bbid = bbid;
    }
    /**
     * @return the treatmentTableChoices
     */
    public List<Object> getTreatmentTableChoices() {
        return treatmentTableChoices;
    }
    /**
     * @param treatmentTableChoices the treatmentTableChoices to set
     */
    public void setTreatmentTableChoices(List<Object> treatmentTableChoices) {
        this.treatmentTableChoices = treatmentTableChoices;
    }
    /**
     * @return the errorcode
     */
    public List<String> getErrorcode() {
        return errorcode;
    }
    /**
     * @param errorcode the errorcode to set
     */
    public void setErrorcode(List<String> errorcode) {
        this.errorcode = errorcode;
    }
    /**
     * @return the treatment
     */
    public List<String> getTreatment() {
        return treatment;
    }
    /**
     * @param treatment the treatment to set
     */
    public void setTreatment(List<String> treatment) {
        this.treatment = treatment;
    }

}
