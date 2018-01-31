/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;

import org.onap.policy.rest.XacmlAdminAuthorization;
import org.onap.policy.rest.jpa.Attribute;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.FunctionDefinition;
import org.onap.policy.rest.jpa.GlobalRoleSettings;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class JPAUtils {
	private static final Logger LOGGER	= FlexLogger.getLogger(JPAUtils.class);

	private static EntityManagerFactory emf;
	private static final Object mapAccess = new Object();
	private static Map<Datatype, List<FunctionDefinition>> mapDatatype2Function = null;
	private static Map<String, FunctionDefinition> mapID2Function = null;
	private static JPAUtils currentInstance = null;


	/**
	 * Get an instance of a JPAUtils. It creates one if it does not exist.
	 * Only one instance is allowed to be created per server.
	 * @param emf The EntityFactoryManager to be used for database connections
	 * @return The new instance of JPAUtils or throw exception if the given emf is null.
	 * @throws IllegalStateException if a JPAUtils has already been constructed. Call getJPAUtilsInstance() to get this.
	 */
	public static JPAUtils getJPAUtilsInstance(EntityManagerFactory emf){
		LOGGER.debug("getJPAUtilsInstance(EntityManagerFactory emf) as getJPAUtilsInstance("+emf+") called");
		if(currentInstance == null){
			if(emf != null){
				currentInstance = new JPAUtils(emf);
				return currentInstance;
			}
			throw new IllegalStateException("The EntityManagerFactory is Null");
		}
		return currentInstance;
	}

	private JPAUtils(EntityManagerFactory emf){
		LOGGER.debug("JPAUtils(EntityManagerFactory emf) as JPAUtils("+emf+") called");
		JPAUtils.emf = emf;
	}

	/**
	 * Gets the current instance of JPAUtils.
	 * @return The instance of JPAUtils or throws exception if the given instance is null.
	 * @throws IllegalStateException if a JPAUtils instance is null. Call getJPAUtilsInstance(EntityManagerFactory emf) to get this.
	 */
	public static JPAUtils getJPAUtilsInstance(){
		LOGGER.debug("getJPAUtilsInstance() as getJPAUtilsInstance() called");
		if(currentInstance != null){
			return currentInstance;
		}
		throw new IllegalStateException("The JPAUtils.currentInstance is Null.  Use getJPAUtilsInstance(EntityManagerFactory emf)");
	}

	public static AttributeDesignatorType createDesignator(Attribute attribute) {
		AttributeDesignatorType designator = new AttributeDesignatorType();
		designator.setAttributeId(attribute.getXacmlId());
		if (attribute.getCategoryBean() != null) {
			designator.setCategory(attribute.getCategoryBean().getXacmlId());
		} else {
			LOGGER.warn("No category bean");
		}
		if (attribute.getDatatypeBean() != null) {
			designator.setDataType(attribute.getDatatypeBean().getXacmlId());
		} else {
			LOGGER.warn("No datatype bean");
		}
		designator.setIssuer(attribute.getIssuer());
		designator.setMustBePresent(attribute.isMustBePresent());
		return designator;
	}

	public static AttributeSelectorType	createSelector(Attribute attribute) {
		AttributeSelectorType selector = new AttributeSelectorType();
		selector.setContextSelectorId(attribute.getXacmlId());
		selector.setPath(attribute.getSelectorPath());
		if (attribute.getCategoryBean() != null) {
			selector.setCategory(attribute.getCategoryBean().getXacmlId());
		} else {
			LOGGER.warn("No category bean");
		}
		if (attribute.getDatatypeBean() != null) {
			selector.setDataType(attribute.getDatatypeBean().getXacmlId());
		} else {
			LOGGER.warn("No datatype bean");
		}
		selector.setMustBePresent(attribute.isMustBePresent());
		return selector;
	}

	/**
	 * Builds a map in memory of a functions return datatype to function definition. Useful in limiting the number
	 * of SQL calls to DB especially when we don't expect these to change much.
	 *
	 * @return - A HashMap of Datatype JPA Container ID's to FunctionDefinition objects
	 */
	public Map<Datatype, List<FunctionDefinition>>	getFunctionDatatypeMap() {

		synchronized(mapAccess) {
			if (mapDatatype2Function == null||mapDatatype2Function.isEmpty()) {
				try {
					buildFunctionMaps();
				} catch (ServletException e) {
					LOGGER.error("Exception Occured"+e);
				}
			}
		}
		return mapDatatype2Function;
	}

	public Map<String, FunctionDefinition> getFunctionIDMap() {
		synchronized(mapAccess) {
			if (mapID2Function == null||mapID2Function.isEmpty()) {
				try {
					buildFunctionMaps();
				} catch (ServletException e) {
					LOGGER.error("Exception Occured"+e);
				}
			}
		}
		return mapID2Function;
	}

	private static void buildFunctionMaps() throws ServletException {
		mapDatatype2Function = new HashMap<>();
		mapID2Function = new HashMap<>();

		EntityManager em = emf.createEntityManager();
		Query getFunctionDefinitions = em.createNamedQuery("FunctionDefinition.findAll");
		List<?> functionList = getFunctionDefinitions.getResultList(); 

		for (Object id : functionList) {
			FunctionDefinition value = (FunctionDefinition)id;
			mapID2Function.put(value.getXacmlid(), value);
			if (!mapDatatype2Function.containsKey(value.getDatatypeBean())) {
				mapDatatype2Function.put(value.getDatatypeBean(), new ArrayList<FunctionDefinition>());
			}
			mapDatatype2Function.get(value.getDatatypeBean()).add(value);
		}

		em.close();

	}

	/**
	 * Returns the lockdown value, in case of exception it is assumed that lockdown functionality
	 * is not supported and returns false.
	 *
	 *
	 * @throws ReadOnlyException
	 * @throws ConversionException
	 */
	public boolean dbLockdownIgnoreErrors() {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("ENTER");

		boolean lockdown = false;
		try {
			lockdown = dbLockdown();
		} catch (Exception e) {
			LOGGER.warn("Cannot access DB lockdown value", e);
		}
		return lockdown;
	}

	/**
	 * Returns the lockdown value from the database.
	 *
	 * @throws ReadOnlyException
	 * @throws ConversionException
	 */
	public boolean dbLockdown()
			throws  IllegalAccessException {
		if (LOGGER.isTraceEnabled())
			LOGGER.trace("ENTER");

		EntityManager em = emf.createEntityManager();
		Query globalRoleSettingsJPA = em.createNamedQuery("GlobalRoleSettings.findAll");

		GlobalRoleSettings globalRoleSettings = (GlobalRoleSettings) globalRoleSettingsJPA.getSingleResult();

		if (globalRoleSettings == null) {
			// this should not happen
			String msg = "NO GlobalSetttings for " + XacmlAdminAuthorization.Role.ROLE_SUPERADMIN.toString();
			if (LOGGER.isErrorEnabled())
				LOGGER.error(msg);
			throw new IllegalAccessException(msg);
		}

		if (!globalRoleSettings.getRole().equals(XacmlAdminAuthorization.Role.ROLE_SUPERADMIN.toString())) {
			String msg = "NOT FOUND db data for " + XacmlAdminAuthorization.Role.ROLE_SUPERADMIN.toString();
			if (LOGGER.isErrorEnabled())
				LOGGER.error(msg);
			throw new IllegalAccessException(msg);
		}

		return globalRoleSettings.isLockdown();
	}



}
