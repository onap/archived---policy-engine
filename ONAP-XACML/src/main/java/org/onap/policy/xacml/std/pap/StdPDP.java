/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
package org.onap.policy.xacml.std.pap;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.onap.policy.xacml.api.pap.OnapPDP;

import org.onap.policy.common.logging.flexlogger.FlexLogger; 
import org.onap.policy.common.logging.flexlogger.Logger;
import com.att.research.xacml.api.pap.PDPPIPConfig;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;

public class StdPDP extends StdPDPItemSetChangeNotifier implements OnapPDP, Comparable<StdPDP>, Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger	logger	= FlexLogger.getLogger(StdPDP.class);
	
	private String id;
	
	private String name;
	
	private String description;
	
	private Integer jmxport = 0;
	
	private transient PDPStatus status = new StdPDPStatus();
	
	private transient Set<PDPPolicy> policies = new HashSet<>();
	
	private transient Set<PDPPIPConfig> pipConfigs = new HashSet<>();
	
	public StdPDP() {
		//
		// Default constructor
		//
	}
	
	public StdPDP(String id, Integer  jmxport) {
		this(id, null, null, jmxport);
	}
	
	public StdPDP(String id, String name, Integer  jmxport) {
		this(id, name, null, jmxport);
	}
	
	public StdPDP(String id, String name, String description, Integer jmxport) {
		this.id = id;
		this.name = name;
		this.description = description;
		if(jmxport != null){
			this.jmxport = jmxport;
		} 
	}
	
	public StdPDP(String id, Properties properties) {
		this(id, 0);
		
		this.initialize(properties);
	}
	
	public void initialize(Properties properties) {
		for (Object key : properties.keySet()) {
			if (key.toString().startsWith(this.id + ".")) {
				if (logger.isDebugEnabled()) {
					logger.debug("Found: " + key);
				}
				if (key.toString().endsWith(".name")) {
					this.name = properties.getProperty(key.toString());
				} else if (key.toString().endsWith(".description")) {
					this.description = properties.getProperty(key.toString());
				}else if (key.toString().endsWith(".jmxport")) {
					if (properties.getProperty(key.toString()) != null && properties.getProperty(key.toString()).trim().length() > 0){
						logger.debug("initialize before: " + this.jmxport);
						this.jmxport = Integer.valueOf( properties.getProperty(key.toString()));
						logger.debug("initialize after: " + this.jmxport);
					}else{
						this.jmxport = 0;
					}
				}
			}
		}
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id=id;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		this.firePDPChanged(this);
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	@Override
	public void setDescription(String description) {
		this.description = description;
		this.firePDPChanged(this);
	}

	@Override
	public PDPStatus getStatus() {
		return this.status;
	}

	public void setStatus(PDPStatus status) {
		this.status = status;
	}
	
	@Override
	public Set<PDPPolicy> getPolicies() {
		return Collections.unmodifiableSet(this.policies);
	}
	
	public void setPolicies(Set<PDPPolicy> policies) {
		this.policies = policies;
	}

	@Override
	public Set<PDPPIPConfig> getPipConfigs() {
		return Collections.unmodifiableSet(this.pipConfigs);
	}
	
	public void setPipConfigs(Set<PDPPIPConfig> pipConfigs) {
		this.pipConfigs = pipConfigs;
	}
	
	@Override
	public void setJmxPort(Integer jmxport) {
		this.jmxport = jmxport;
	}
	@Override
	public Integer getJmxPort() {
		return this.jmxport;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StdPDP other = (StdPDP) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StdPDP [id=" + id + ", name=" + name + ", description="
				+ description + ", jmxport=" + jmxport + ", status=" + status + ", policies=" + policies
				+ ", pipConfigs=" + pipConfigs + "]";
	}
	
	//
	// Comparable interface
	//
	@Override
	public int compareTo(StdPDP o) {
		if (o == null) {
			return -1;
		}
		if ( ! (o instanceof StdPDP)) {
			return -1;
		}
		if (o.name == null) {
			return -1;
		}
		if (name == null) {
			return 1;
		}
		return name.compareTo(o.name);
	}
	
}
