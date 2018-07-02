/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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

package org.onap.policy.xacml.std.pip.engines;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML;
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
 * attribute retrieval from Operation History Table.  
 * 
 * @version $Revision$
 */
public class OperationHistoryEngine extends StdConfigurableEngine{
	public static final String DEFAULT_DESCRIPTION		= "PIP for retrieving Operations History from DB";
	public static final String DEFAULT_ISSUER			= "org:onap:xacml:guard:historydb";
	
	private static final Logger LOGGER= FlexLogger.getLogger(OperationHistoryEngine.class);
	
	private static final PIPRequest PIP_REQUEST_ACTOR	= new StdPIPRequest(
			XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
			new IdentifierImpl("actor"),
			XACML.ID_DATATYPE_STRING);

	private static final PIPRequest PIP_REQUEST_RECIPE	= new StdPIPRequest(
			XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
			new IdentifierImpl("recipe"), 
			XACML.ID_DATATYPE_STRING);

	private static final PIPRequest PIP_REQUEST_TARGET	= new StdPIPRequest(
			XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
			new IdentifierImpl("target"), 
			XACML.ID_DATATYPE_STRING);
	
	public OperationHistoryEngine() {
		super();
	}

	private void addIntegerAttribute(StdMutablePIPResponse stdPIPResponse, Identifier category, Identifier attributeId, int value, PIPRequest pipRequest) {
		AttributeValue<BigInteger> attributeValue	= null;
		try {
			attributeValue	= DataTypes.DT_INTEGER.createAttributeValue(value);
		} catch (Exception ex) {
			LOGGER.error("Failed to convert " + value + " to an AttributeValue<Boolean>", ex);
		}
		if (attributeValue != null) {
			stdPIPResponse.addAttribute(new StdMutableAttribute(category, attributeId, attributeValue, pipRequest.getIssuer()/*this.getIssuer()*/, false));
		}
	}

	@Override
	public Collection<PIPRequest> attributesRequired() {
		return new ArrayList<>();
	}

	@Override
	public Collection<PIPRequest> attributesProvided() {
		return new ArrayList<>();
	}

	@Override
	public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
		LOGGER.debug("Entering FeqLimiter PIP");
		/*
		 * First check to see if the issuer is set and then match it
		 */
		String string;
		if ((string = pipRequest.getIssuer()) == null) {
			LOGGER.debug("FeqLimiter PIP - No issuer in the request!");
			return StdPIPResponse.PIP_RESPONSE_EMPTY;
		}
		else{
			//Notice, we are checking here for the base issuer prefix.
			if (!string.contains(this.getIssuer())) {
				LOGGER.debug("Requested issuer '" + string + "' does not match " + (this.getIssuer() == null ? "null" : "'" + this.getIssuer() + "'"));
				LOGGER.info("FeqLimiter PIP - Issuer "+ string +" does not match with: "+this.getIssuer());
				return StdPIPResponse.PIP_RESPONSE_EMPTY;
			}
		}
		String[] s1 = string.split("tw:");
		String[] s2 = s1[1].split(":");
		int timeWindowVal = Integer.parseInt(s2[0]);// number [of minutes, hours, days...]
		String timeWindowScale = s2[1];//e.g., minute, hour, day, week, month, year
		String actor = getActor(pipFinder).iterator().next();
		String operation = getRecipe(pipFinder).iterator().next();
		String target = getTarget(pipFinder).iterator().next();
		String timeWindow = timeWindowVal + " " + timeWindowScale;
		LOGGER.info("Going to query DB about: "+actor + " " + operation + " " + target + " " + timeWindow);
		int countFromDB = getCountFromDB(actor, operation, target, timeWindowVal, timeWindowScale);
		StdMutablePIPResponse stdPIPResponse	= new StdMutablePIPResponse();
		this.addIntegerAttribute(stdPIPResponse,
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				new IdentifierImpl("count"), 
				countFromDB,
				pipRequest);
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

	private PIPResponse getAttribute(PIPRequest pipRequest, PIPFinder pipFinder) {
		PIPResponse pipResponse	= null;
		try {
			pipResponse	= pipFinder.getMatchingAttributes(pipRequest, this);
			if (pipResponse.getStatus() != null && !pipResponse.getStatus().isOk()) {
				LOGGER.info("Error retrieving " + pipRequest.getAttributeId().stringValue() + ": " + pipResponse.getStatus().toString());
				pipResponse	= null;
			}
			if (pipResponse!=null && pipResponse.getAttributes().isEmpty()) {
				LOGGER.info("No value for " + pipRequest.getAttributeId().stringValue());
				pipResponse	= null;
			}
		} catch (PIPException ex) {
			LOGGER.error("PIPException getting subject-id attribute: " + ex.getMessage(), ex);			
		}
		return pipResponse;
	}


	private Set<String> getActor(PIPFinder pipFinder) {
		/*
		 * Get the AT&T UID from either the subject id or the attuid property
		 */
		PIPResponse pipResponseATTUID	= this.getAttribute(PIP_REQUEST_ACTOR, pipFinder);
		if (pipResponseATTUID == null) {
			return new HashSet<>();
		}
		/*
		 * Iterate over all of the returned results and do the LDAP requests
		 */
		Collection<Attribute> listATTUIDs	= pipResponseATTUID.getAttributes();
		Set<String> setATTUIDs			= new HashSet<>();
		for (Attribute attributeATTUID: listATTUIDs) {
			Iterator<AttributeValue<String>> iterAttributeValues	= attributeATTUID.findValues(DataTypes.DT_STRING);
			if (iterAttributeValues != null) {
				while (iterAttributeValues.hasNext()) {
					String attuid	= iterAttributeValues.next().getValue();
					if (attuid != null) {
						setATTUIDs.add(attuid);
					}
				}
			}
		}
		return setATTUIDs;
	}

	private Set<String> getRecipe(PIPFinder pipFinder) {
		/*
		 * Get the AT&T UID from either the subject id or the attuid property
		 */
		PIPResponse pipResponseATTUID	= this.getAttribute(PIP_REQUEST_RECIPE, pipFinder);
		if (pipResponseATTUID == null) {
			return new HashSet<>();
		}
		/*
		 * Iterate over all of the returned results and do the LDAP requests
		 */
		Collection<Attribute> listATTUIDs	= pipResponseATTUID.getAttributes();
		Set<String> setATTUIDs			= new HashSet<>();
		for (Attribute attributeATTUID: listATTUIDs) {
			Iterator<AttributeValue<String>> iterAttributeValues	= attributeATTUID.findValues(DataTypes.DT_STRING);
			if (iterAttributeValues != null) {
				while (iterAttributeValues.hasNext()) {
					String attuid	= iterAttributeValues.next().getValue();
					if (attuid != null) {
						setATTUIDs.add(attuid);
					}
				}
			}
		}
		return setATTUIDs;
	}


	private Set<String> getTarget(PIPFinder pipFinder) {
		/*
		 * Get the AT&T UID from either the subject id or the attuid property
		 */
		PIPResponse pipResponseATTUID	= this.getAttribute(PIP_REQUEST_TARGET, pipFinder);
		if (pipResponseATTUID == null) {
			return new HashSet<>();
		}
		/*
		 * Iterate over all of the returned results and do the LDAP requests
		 */
		Collection<Attribute> listATTUIDs	= pipResponseATTUID.getAttributes();
		Set<String> setATTUIDs			= new HashSet<>();
		for (Attribute attributeATTUID: listATTUIDs) {
			Iterator<AttributeValue<String>> iterAttributeValues	= attributeATTUID.findValues(DataTypes.DT_STRING);
			if (iterAttributeValues != null) {
				while (iterAttributeValues.hasNext()) {
					String attuid	= iterAttributeValues.next().getValue();
					if (attuid != null) {
						setATTUIDs.add(attuid);
					}
				}
			}
		}
		return setATTUIDs;
	}

	private static int getCountFromDB(String actor, String operation, String target, int timeWindow, String timeUnits){
		EntityManager em;
		try{
			Properties properties = XACMLProperties.getProperties();
			properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/operationHistoryPU.xml");
			em = Persistence.createEntityManagerFactory("OperationsHistoryPU",properties).createEntityManager();
		}catch(Exception e){
			LOGGER.error("Test thread got Exception " + e.getLocalizedMessage() + " Can't connect to Operations History DB.", e);
			return -1;
		}
		// Preventing SQL injection
		if(!validTimeUnits(timeUnits)){
			LOGGER.error("given PIP timeUnits is not valid. " + timeUnits);
			em.close();
			return -1;
		}
		String sql = "select count(*) as count from operationshistory10 where outcome<>'Failure_Guard' and actor=?"
				+ " and operation=?"
				+ " and target=?"
				+ " and endtime between date_sub(now(),interval ? "+timeUnits+") and now()";
		Query nq = em.createNativeQuery(sql);
		nq.setParameter(1, actor);
		nq.setParameter(2, operation);
		nq.setParameter(3, target);
		nq.setParameter(4, timeWindow);
		int ret = ((Number)nq.getSingleResult()).intValue();
		LOGGER.info("###########************** History count: " + ret);
		em.close();
		return ret;	
	}
	
	// Validating Time Units to prevent SQL Injection. 
	private static boolean validTimeUnits(String timeUnits) {
		return ("minute".equalsIgnoreCase(timeUnits) || "hour".equalsIgnoreCase(timeUnits) || "day".equalsIgnoreCase(timeUnits) 
			|| "week".equalsIgnoreCase(timeUnits) || "month".equalsIgnoreCase(timeUnits)|| "year".equalsIgnoreCase(timeUnits))?
				true: false;
	}
}
