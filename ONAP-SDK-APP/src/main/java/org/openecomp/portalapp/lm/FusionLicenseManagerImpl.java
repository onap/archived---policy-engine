/*-
 * ================================================================================
 * ONAP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
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
 * ================================================================================
 */
package org.openecomp.portalapp.lm;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.openecomp.portalsdk.core.lm.FusionLicenseManager;
import org.springframework.stereotype.Component;

/*
 *  Please note that this class is not being used; its a dummy stub to have a qualifying bean for the interface.
 */

@Component
public class FusionLicenseManagerImpl implements FusionLicenseManager {

	@Override
	public void initKeyStoreParam() {
		
	}
	
	@Override
	public void initCipherParam() {
		
	}

	@Override	
	public void initLicenseParam() {
		
	}

	@Override
	public void doInitWork() {
		
	}

	@Override
	public int installLicense() {
		return 0;
	}

	@Override
	public synchronized int verifyLicense(ServletContext context) {
		return 0;
	}

	@Override
	public void generateLicense(Map<String, String> clientInfoMap, List<String> ipAddressList) throws Exception {
		
	}
	
	@Override
	public String nvl(String s) {
		return null;
	}

	@Override
	public Date getExpiredDate() {
		return null;
	}

	@Override	
	public void setExpiredDate(Date expiredDate) {
		
	}

}
