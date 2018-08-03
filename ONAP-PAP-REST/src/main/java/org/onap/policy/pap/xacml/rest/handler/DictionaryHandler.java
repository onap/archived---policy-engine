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
package org.onap.policy.pap.xacml.rest.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import com.att.research.xacml.util.XACMLProperties;

public interface DictionaryHandler {
    public static final Logger logger = FlexLogger.getLogger(DictionaryHandler.class);
    String DICTIONARY_DEFAULT_CLASS = DictionaryHandlerImpl.class.getName();

    /*
     * Get Instance
     */
    public static DictionaryHandler getInstance(){
        try {
            Class<?> dictionaryHandler = Class.forName(XACMLProperties.getProperty("dictionary.impl.className", DICTIONARY_DEFAULT_CLASS));
            DictionaryHandler instance = (DictionaryHandler) dictionaryHandler.newInstance();
            return instance;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /*
     * Get Equivalent for Dictionary Services.
     */
    public void doDictionaryAPIGet(HttpServletRequest request, HttpServletResponse response);
    /*
     * Put Equivalent for Dictionary Services.
     */
    public void doDictionaryAPIPut(HttpServletRequest request, HttpServletResponse response);

    /**
     * Can be used to extend the services.
     *
     * getflag=true indicates Get Request.
     * getflag=false indicates Put Request.
     * @return
     */
    public String extendedOptions(String dictionaryType, HttpServletRequest request, HttpServletResponse response, boolean getflag);
}
