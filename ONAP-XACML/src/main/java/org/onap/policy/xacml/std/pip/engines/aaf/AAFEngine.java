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
package org.onap.policy.xacml.std.pip.engines.aaf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.policy.utils.AAFPolicyClient;
import org.onap.policy.utils.AAFPolicyException;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.api.pip.PIPResponse;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdMutableAttribute;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacml.std.pip.StdMutablePIPResponse;
import com.att.research.xacml.std.pip.StdPIPRequest;
import com.att.research.xacml.std.pip.StdPIPResponse;
import com.att.research.xacml.std.pip.engines.StdConfigurableEngine;
import com.att.research.xacml.util.XACMLProperties;

/**
 * PIP Engine for Implementing {@link com.att.research.xacml.std.pip.engines.ConfigurableEngine} interface to provide
 * attribute retrieval from AAF interface.
 *
 * @version $Revision$
 */
public class AAFEngine extends StdConfigurableEngine {

	public static final String DEFAULT_DESCRIPTION		= "PIP for authenticating aaf attributes using the AAF REST interface";
	public static final String DEFAULT_ISSUER			= "aaf";

	private static final String SUCCESS = "Success";

	public static final String AAF_RESULT= "AAF_RESULT";
	public static final String AAF_RESPONSE= "AAF_RESPONSE";
	//
	public static final Identifier AAF_RESPONSE_ID = new IdentifierImpl(AAF_RESPONSE);
	public static final Identifier AAF_RESULT_ID = new IdentifierImpl(AAF_RESULT);

	//
	private static final PIPRequest PIP_REQUEST_UID = new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("AAF_ID"), XACML3.ID_DATATYPE_STRING);
	private static final PIPRequest PIP_REQUEST_PASS =  new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("AAF_PASS"), XACML3.ID_DATATYPE_STRING);
	private static final PIPRequest PIP_REQUEST_TYPE = new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("AAF_TYPE"), XACML3.ID_DATATYPE_STRING);
	private static final PIPRequest PIP_REQUEST_INSTANCE = new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("AAF_INSTANCE"), XACML3.ID_DATATYPE_STRING);
	private static final PIPRequest PIP_REQUEST_ACTION = new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("AAF_ACTION"), XACML3.ID_DATATYPE_STRING);

	private static final List<PIPRequest> mapRequiredAttributes	= new ArrayList<>();
	static{
		mapRequiredAttributes.add(new StdPIPRequest(PIP_REQUEST_UID));
		mapRequiredAttributes.add(new StdPIPRequest(PIP_REQUEST_PASS));
		mapRequiredAttributes.add(new StdPIPRequest(PIP_REQUEST_TYPE));
		mapRequiredAttributes.add(new StdPIPRequest(PIP_REQUEST_INSTANCE));
		mapRequiredAttributes.add(new StdPIPRequest(PIP_REQUEST_ACTION));
	}

	private static final Map<PIPRequest, String> mapSupportedAttributes	= new HashMap<>();
	static{
		mapSupportedAttributes.put(new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, AAF_RESPONSE_ID, XACML3.ID_DATATYPE_STRING), "response");
		mapSupportedAttributes.put(new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, AAF_RESULT_ID, XACML3.ID_DATATYPE_BOOLEAN), "result");
	}

	protected Log logger	= LogFactory.getLog(this.getClass());

	public AAFEngine(){
		//default constructor
	}

	private PIPResponse getAttribute(PIPRequest pipRequest, PIPFinder pipFinder) {
		PIPResponse pipResponse	= null;
		try {
			pipResponse	= pipFinder.getMatchingAttributes(pipRequest, this);
			if (pipResponse.getStatus() != null && !pipResponse.getStatus().isOk()) {
				this.logger.warn("Error retrieving " + pipRequest.getAttributeId().stringValue() + ": " + pipResponse.getStatus().toString());
				pipResponse	= null;
			}
			if (pipResponse != null && pipResponse.getAttributes().isEmpty()) {
				this.logger.warn("No value for " + pipRequest.getAttributeId().stringValue());
				pipResponse	= null;
			}
		} catch (PIPException ex) {
			this.logger.error("PIPException getting subject-id attribute: " + ex.getMessage(), ex);
		}
		return pipResponse;
	}

	private String getValue(PIPResponse pipResponse){
		String result = null;
		Collection<Attribute> listAttributes = pipResponse.getAttributes();
		for(Attribute attribute: listAttributes){
			Iterator<AttributeValue<String>> iterAttributeValues = attribute.findValues(DataTypes.DT_STRING);
			if(iterAttributeValues!=null) {
				while(iterAttributeValues.hasNext()){
					result = iterAttributeValues.next().getValue();
					break;
				}
			}
		}
		return result;
	}

	private synchronized String getResult(PIPFinder pipFinder) {
		PIPResponse pipResponseUID = this.getAttribute(PIP_REQUEST_UID, pipFinder);
		PIPResponse pipResponsePass = this.getAttribute(PIP_REQUEST_PASS, pipFinder);
		PIPResponse pipResponseType = this.getAttribute(PIP_REQUEST_TYPE, pipFinder);
		PIPResponse pipResponseAction = this.getAttribute(PIP_REQUEST_ACTION, pipFinder);
		PIPResponse pipResponseInstance = this.getAttribute(PIP_REQUEST_INSTANCE, pipFinder);
		String response = null;
		// Evaluate AAF if we have all the required values.
		if(pipResponseUID!=null && pipResponsePass!=null && pipResponseType != null && pipResponseAction!= null && pipResponseInstance!=null){
			String userName = getValue(pipResponseUID);
			String pass = getValue(pipResponsePass);

			AAFPolicyClient aafClient = null;
			Properties properties;
			try {
                properties = XACMLProperties.getProperties();
                logger.debug("environment : " + properties.getProperty("ENVIRONMENT"));
            } catch (IOException e1) {
                logger.error("Exception while getting the properties " + e1);
                properties = new Properties();
                properties.setProperty("AAF_LOG_LEVEL", "DEBUG");
            }
			if(userName!=null && pass!=null){
				try {
					aafClient = AAFPolicyClient.getInstance(properties);
				} catch (AAFPolicyException e) {
					logger.error("AAF configuration failed. " + e.getMessage() +e);
				}
				if(aafClient!=null){
					if(aafClient.checkAuth(userName, pass)){
						String type = getValue(pipResponseType);
						String instance = getValue(pipResponseInstance);
						String action = getValue(pipResponseAction);
						if(aafClient.checkPerm(userName, pass, type, instance, action)){
							response = SUCCESS + "Permissions Validated";
						}else{
							response = "No Permissions for "+userName+" to: "+type+", "+instance+", "+action;
						}
					}else{
						response = "Authentication Failed for the given Values";
					}
				}
			}else{
				response = "ID and Password are not given";
			}

		}else{
			response = "Insufficient Values to Evaluate AAF";
		}
		return response;
	}

	private void addStringAttribute(StdMutablePIPResponse stdPIPResponse, Identifier category, Identifier attributeId, String value) {
		if (value != null) {
			AttributeValue<String> attributeValue	= null;
			try {
				attributeValue	= DataTypes.DT_STRING.createAttributeValue(value);
			} catch (Exception ex) {
				this.logger.error("Failed to convert " + value + " to an AttributeValue<String>", ex);
			}
			if (attributeValue != null) {
				stdPIPResponse.addAttribute(new StdMutableAttribute(category, attributeId, attributeValue, this.getIssuer(), false));
			}
		}
	}

	private void addBooleanAttribute(StdMutablePIPResponse stdPIPResponse, Identifier category, Identifier attributeId, boolean value) {
		AttributeValue<Boolean> attributeValue	= null;
		try {
			attributeValue	= DataTypes.DT_BOOLEAN.createAttributeValue(value);
		} catch (Exception ex) {
			this.logger.error("Failed to convert " + value + " to an AttributeValue<Boolean>", ex);
		}
		if (attributeValue != null) {
			stdPIPResponse.addAttribute(new StdMutableAttribute(category, attributeId, attributeValue, this.getIssuer(), false));
		}
	}

	@Override
	public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
		/*
		 * First check to see if the issuer is set and then match it
		 */
		String string;

		if((string = pipRequest.getIssuer()) != null && !string.equals(this.getIssuer())) {
			this.logger.debug("Requested issuer '" + string + "' does not match " + (this.getIssuer() == null ? "null" : "'" + this.getIssuer() + "'"));
			return StdPIPResponse.PIP_RESPONSE_EMPTY;
		}


		/*
		 * Drop the issuer and see if the request matches any of our supported queries
		 */
		PIPRequest pipRequestSupported	= (pipRequest.getIssuer() == null ? pipRequest : new StdPIPRequest(pipRequest.getCategory(), pipRequest.getAttributeId(), pipRequest.getDataTypeId()));
		if (!mapSupportedAttributes.containsKey(pipRequestSupported)) {
			this.logger.debug("Requested attribute '" + pipRequest.toString() + "' is not supported");
			return StdPIPResponse.PIP_RESPONSE_EMPTY;
		}
		StdMutablePIPResponse stdPIPResponse = new StdMutablePIPResponse();
		String response = this.getResult(pipFinder);
		boolean result = false;
		if(response != null && response.contains(SUCCESS)){
			result = true;
		}
		this.addBooleanAttribute(stdPIPResponse, XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, AAF_RESULT_ID, result);
		this.addStringAttribute(stdPIPResponse, XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, AAF_RESPONSE_ID, response);
		return new StdPIPResponse(stdPIPResponse);
	}

	@Override
	public void configure(String id, Properties properties) throws PIPException {
		super.configure(id, properties);
		if (this.getDescription() == null) {
			this.setDescription(DEFAULT_DESCRIPTION);
		}
		if (this.getIssuer() == null) {
			this.setIssuer(DEFAULT_ISSUER);
		}
	}

	@Override
	public Collection<PIPRequest> attributesRequired() {
		List<PIPRequest> attributes = new ArrayList<>();
		for (PIPRequest attribute: mapRequiredAttributes) {
			attributes.add(new StdPIPRequest(attribute));
		}
		return attributes;
	}

	@Override
	public Collection<PIPRequest> attributesProvided() {
		List<PIPRequest> attributes = new ArrayList<>();
		for (PIPRequest attribute : mapSupportedAttributes.keySet()) {
			attributes.add(new StdPIPRequest(attribute));
		}
		return attributes;
	}

}