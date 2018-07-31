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



public class ServiceListJson {

    protected String name;
    protected String description;
    protected String type;
    protected String transportProtocol;
    protected String appProtocol;
    protected String ports;
    // name
    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this.getClass() != obj.getClass()){
            return false;
        }
        ServiceListJson servobj=(ServiceListJson) obj;
        if(this.getName().equals(servobj.getName())){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
         if(name!=null){
            return Integer.valueOf(name.charAt(0)+(name.charAt(1)));
         }else{
             return 0;
         }
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // type
    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    // transportProtocol
    public String getTransportProtocol() {
        return transportProtocol;
    }

    public void setTransportProtocol(String value) {
        this.transportProtocol = value;
    }

    // appProtocol
    public String getAppProtocol() {
        return appProtocol;
    }

    public void setAppProtocol(String value) {
        this.appProtocol = value;
    }

    // ports
    public String getPorts() {
        return ports;
    }

    public void setPorts(String value) {
        this.ports = value;
    }



}
