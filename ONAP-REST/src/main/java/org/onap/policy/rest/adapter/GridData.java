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


import java.util.List;

public class GridData {
	private List<Object> attributes;
	private List<Object> transportProtocols;
	private List<Object> appProtocols;

	public List<Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Object> attributes) {
		this.attributes = attributes;
	}

	public List<Object> getAppProtocols() {
		return appProtocols;
	}

	public void setAppProtocols(List<Object> appProtocols) {
		this.appProtocols = appProtocols;
	}

	public List<Object> getTransportProtocols() {
		return transportProtocols;
	}

	public void setTransportProtocols(List<Object> transportProtocols) {
		this.transportProtocols = transportProtocols;
	}
}
