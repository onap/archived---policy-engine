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


public class Term {

    String position;
    protected String ruleName;
    protected List<String> fromZones;
    protected List<String> toZones;
    protected boolean negateSource; //hardcoded
    protected boolean negateDestination; //hardcoded
    protected List<AddressJson> sourceList;
    protected List<AddressJson> destinationList;
    protected List<ServicesJson> sourceServices;
    protected Set<ServicesJson> destServices;
    protected String action;
    protected String description;
    boolean enabled;  //hardcoded
    boolean log;      //hardcoded

    //position
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String value) {
        this.position = value;
    }
    
    //RuleName
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String value) {
        this.ruleName = value;
    }
    
    //From Zone
    public  List<String> getFromZones() {
        if (fromZones==null)
        {
            fromZones= new ArrayList<>();
        }
        return fromZones;
    }
    
    public void setFromZones(List<String> fromZones) {
        this.fromZones = fromZones;
    }
    
    //To Zone
    public  List<String> getToZones() {
        if (toZones==null)
        {
            toZones= new ArrayList<>();
        }
        return toZones;
    }
    
    public void setToZones(List<String> toZones) {
        this.toZones = toZones;
    }
   
    
    //Negate Source
    public boolean getNegateSource() {
        return negateSource;
    }
    
    public void setNegateSource(boolean negateSource) {
        this.negateSource = negateSource;
    }
    
    //Negate Destination
    public boolean getNegateDestination() {
        return negateDestination;
    }
    
    public void setNegateDestination(boolean negateDestination) {
        this.negateDestination = negateDestination;
    }
    
    //SourceList
    public List<AddressJson> getSourceList() 
    {
        if(sourceList==null)
        {
            sourceList= new ArrayList<>();
        }
        return this.sourceList;
    }

    public void setSourceList(List<AddressJson> srcList) {
        this.sourceList = srcList;
    }
    
    //Destination List
    public List<AddressJson> getDestinationList() 
    {
        if(destinationList==null)
        {
            destinationList= new ArrayList<>();
        }
        return this.destinationList;
    }

    public void setDestinationList(List<AddressJson> destList) {
        this.destinationList = destList;
    }
    
    //Source Services
    public List<ServicesJson> getSourceServices() {
        if(sourceServices==null)
        {
            sourceServices= new ArrayList<>();
        }
        return this.sourceServices;
    }

    public void setSourceServices(List<ServicesJson> sourceServices) {
        this.sourceServices = sourceServices;
    }

    //Destination services. 
    public Set<ServicesJson> getDestServices() {
        if(destServices==null)
        {
            destServices= new HashSet<>();
        }
        return this.destServices;
    }

    public void setDestServices(Set<ServicesJson> destServices) {
        this.destServices = destServices;
    }

    //Action
    public String getAction() {
        return action;
    }
    
    public void setAction(String value) {
        this.action = value;
    }
    
    //description
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String desc) {
        this.description = desc;
    }
      
    //enabled
    public boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean value) {
        this.enabled = value;
    }
    
    //Log
    public boolean getLog() {
        return log;
    }
    
    public void setLog(boolean value) {
        this.log = value;
    }

}
