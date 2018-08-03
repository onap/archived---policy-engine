/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
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

package org.onap.policy.pap.xacml.rest.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.onap.policy.rest.XacmlAdminAuthorization;
import org.onap.policy.rest.jpa.GlobalRoleSettings;

import org.onap.policy.common.logging.flexlogger.FlexLogger; 
import org.onap.policy.common.logging.flexlogger.Logger;

public class JPAUtils {
    private static final Logger LOGGER	= FlexLogger.getLogger(JPAUtils.class);

    private static EntityManagerFactory emf;
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
