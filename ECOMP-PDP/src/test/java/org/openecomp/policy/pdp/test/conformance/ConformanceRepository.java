/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP
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

package org.openecomp.policy.pdp.test.conformance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.att.research.xacml.util.StringUtils;
import com.att.research.xacml.util.XACMLProperties;
import com.att.research.xacmlatt.pdp.std.StdPolicyFinderFactory;

/**
 * ConformanceRepository represents one or more policies for a single policy test, which will include one or more root policies, and
 * zero or more referenced policies.
 * 
 * @version $Revision$
 */
public class ConformanceRepository {
	private List<File> rootPolicies			= new ArrayList<File>();
	private List<File> referencedPolicies	= new ArrayList<File>();
	
	private void setXACMLProperty(String propertyName, List<File> listFiles) {
		Iterator<File> iterFiles			= listFiles.iterator();
		StringBuilder stringBuilderIdList	= new StringBuilder();
		while (iterFiles.hasNext()) {
			File file	= iterFiles.next();
			if (stringBuilderIdList.length() > 0) {
				stringBuilderIdList.append(',');
			}
			stringBuilderIdList.append(file.getName());
			
			XACMLProperties.setProperty(file.getName() + StdPolicyFinderFactory.PROP_FILE, file.getAbsolutePath());
		}
		XACMLProperties.setProperty(propertyName, stringBuilderIdList.toString());
	}
	
	public ConformanceRepository() {
	}
	
	public void setXACMLProperties() {
		if (this.rootPolicies.size() > 0) {
			this.setXACMLProperty(XACMLProperties.PROP_ROOTPOLICIES, this.rootPolicies);
		}
		if (this.referencedPolicies.size() > 0) {
			this.setXACMLProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, this.referencedPolicies);
		}
	}
	
	private void loadProperty(File fileDir, Properties properties, String propertyName, List<File> listFiles) {
		String fileNameList	= properties.getProperty(propertyName);
		if (fileNameList != null) {
			String[] fileNameArray	= fileNameList.split("[,]",0);
			if (fileNameArray != null && fileNameArray.length > 0) {
				for (String fileName : fileNameArray) {
					File file	= new File(fileDir, fileName);
					if (file.exists() && file.canRead()) {
						listFiles.add(file);
					}
				}
			}
		}
	}
	
	public void load(File fileRepository) throws IOException {
		Properties propertiesRepository	= new Properties();
		try (InputStream is = new FileInputStream(fileRepository)) {
			propertiesRepository.load(is);
		}
		this.loadProperty(fileRepository.getParentFile(), propertiesRepository, XACMLProperties.PROP_ROOTPOLICIES, this.rootPolicies);
		this.loadProperty(fileRepository.getParentFile(), propertiesRepository, XACMLProperties.PROP_REFERENCEDPOLICIES, this.referencedPolicies);
	}
	
	public void addRootPolicy(File filePolicy) {
		this.rootPolicies.add(filePolicy);
	}
	
	public boolean hasRootPolicy() {
		return (this.rootPolicies.size() > 0);
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder	= new StringBuilder("{");
		boolean needComma			= false;
		
		if (this.rootPolicies != null && this.rootPolicies.size() > 0) {
			stringBuilder.append("rootPolicies=");
			stringBuilder.append(StringUtils.toString(this.rootPolicies.iterator()));
			needComma	= true;
		}
		if (this.referencedPolicies != null && this.referencedPolicies.size() > 0) {
			if (needComma) {
				stringBuilder.append(',');
			}
			stringBuilder.append("referencedPolicies=");
			stringBuilder.append(StringUtils.toString(this.referencedPolicies.iterator()));
			needComma	= true;
		}
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
	
}
