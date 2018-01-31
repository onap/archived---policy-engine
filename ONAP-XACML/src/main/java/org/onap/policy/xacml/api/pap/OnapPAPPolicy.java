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
package org.onap.policy.xacml.api.pap;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.onap.policy.xacml.std.pap.StdPAPPolicy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/*
 * The following allows us to use Jackson to convert sub-types of this type into JSON and back to objects.
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "PAPPolicyType")
@JsonSubTypes({
	    @Type(value = StdPAPPolicy.class, name = "StdPAPPolicy") })
public interface OnapPAPPolicy {

	public String getPolicyName();
	public String getOldPolicyFileName();
	public String getPolicyDescription();
	public String getOnapName();
	public String getConfigName();
	public Map<String, String> getDynamicFieldConfigAttributes();
	public Map<String, String> getTreatments();
	public Map<String, String> getDynamicSettingsMap();
	public List<String> getDynamicRuleAlgorithmLabels();
	public List<String> getDynamicRuleAlgorithmCombo();
	public List<String> getDynamicRuleAlgorithmField1();
	public List<String> getDynamicRuleAlgorithmField2();
	public List<Object> getDynamicVariableList();
	public List<String> getDataTypeList();
	public String getConfigBodyData();
	public String getPolicyID();
	public String getRuleID();
	public String getConfigType();
	public Boolean isEditPolicy();
	public Boolean isDraft();
	public String getVersion();
	public String getDomainDir();
	public String getConfigPolicyType();
	public String getJsonBody();
	public Integer getHighestVersion();
	public URI getLocation();
	public String getActionPerformer();
	public String getActionAttribute();
	public String getActionBody();
	public Map<String, String> getDropDownMap();
	public String getActionDictHeader();
	public String getActionDictType();
	public String getActionDictUrl();
	public String getActionDictMethod();
	public String getServiceType();
	public String getUuid();
	public String getMsLocation();
    public String getPriority();
    public String getDeleteCondition();
    public String getDictionaryType();
    public String getDictionary();
    public String getDictionaryFields();
	public String getRiskLevel();
	public String getGuard();
	public String getRiskType();
	public String getTTLDate();
}
