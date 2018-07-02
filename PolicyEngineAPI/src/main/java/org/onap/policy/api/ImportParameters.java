/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineAPI
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.api;

import java.util.UUID;


/**
 * <code>ImportParameters</code> defines the Policy Engine Import Parameters
 *  which are required to import a new Policy Service or Value. 
 * 
 * @version 0.1
 */
public class ImportParameters {
	private String serviceName;
	private String description;
	private UUID requestID;
	private String filePath;
	private String version;
	private IMPORT_TYPE importType;
	private boolean decisionModel = false;
	
	public enum IMPORT_TYPE {
	    MICROSERVICE,
	    BRMSPARAM,
	    OPTIMIZATION
	}

	/**
	 * Sets Import Policy Parameters.
	 * 
	 * @param serviceName the <code>String</code> format of the Service Name
	 * @param description the <code>String</code> format of the i Description
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 * @param filePath the <code>List</code> format of the file paths for the service files
	 * @param importType the {@link IMPORT_TYPE} format of the Policy Service List
	 * @param version the <code>String</code> format of the Policy Import Version
	 * A different request ID should be passed for each request.
	 */
	public void setImportParameters(String serviceName, String description, UUID requestID, String filePath, IMPORT_TYPE importType, String version){
		
		this.setServiceName(serviceName);
		this.setDescription(description);
		this.setRequestID(requestID);
		this.setFilePath(filePath);
		this.setServiceType(importType);	
		this.setVersion(version);
		
	}

	/**
	 * Gets the Policy Service of the Policy Service Import Parameters. 
	 * 
	 * @return serviceName the <code>String</code> format of the Policy Service Name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the serviceName of the Policy Service Parameters.
	 * 
	 * @param serviceName the <code>String</code> format of the Policy Service Name
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the Policy Import Description. 
	 * 
	 * @return description the <code>String</code> format of the Policy Import Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the Description of the new Policy Import Description. 
	 * 
	 * @param description the <code>String</code> format of the Policy Import Description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the requestID of the Policy Parameters. 
	 * 
	 * @return unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public UUID getRequestID() {
		return requestID;
	}
	
	/**
	 * Sets the requestID of the Policy Parameters. 
	 * 
	 * @param requestID unique request ID which will be passed throughout the ONAP components to correlate logging messages.
	 */
	public void setRequestID(UUID requestID) {
		this.requestID = requestID;
	}
	
	/**
	 * Gets the List of File Paths of the new import. 
	 * 
	 * @return filePath the <code>List</code> format of the Policy Import  File
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the policy Import File List of the new Policy Import. 
	 * 
	 * @param filePath the <code>List</code> format of the Policy Import  File
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/** 
	 * Gets the Service Type of the new policy import. 
	 * 
	 * @return ImportType {@link IMPORT_TYPE} format of the Policy Service List
	 */
	public IMPORT_TYPE getServiceType() {
		return importType;
	}
	
	/**
	 * Sets the policy Service Type of the new Policy Service. 
	 * 
	 * @param enumImportType the <code>enumServiceType</code> format of the Policy Service List
	 */
	public void setServiceType(IMPORT_TYPE enumImportType) {
		this.importType = enumImportType;
	}
	
	/**
	 * 
	 * Gets the Import Version of the new policy import. 
	 * 
	 * @return version the <code>String</code> format of the Policy Import Version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the policy Import Version  of the new Policy Import. 
	 * 
	 * @param version the <code>String</code> format of the Policy Import Version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

    /**
     * @return the decisionModel
     */
    public boolean isDecisionModel() {
        return decisionModel;
    }

    /**
     * @param decisionModel the decisionModel to set
     */
    public void setDecisionModel(boolean decisionModel) {
        this.decisionModel = decisionModel;
    }
}
