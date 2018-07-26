/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Modified Copyright (C) 2018 Samsung Electronics Co., Ltd.
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

package org.onap.policy.xacml.custom;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.FunctionDefinition;
import com.att.research.xacmlatt.pdp.policy.FunctionDefinitionFactory;
import com.att.research.xacmlatt.pdp.std.StdFunctions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.xacml.pdp.std.functions.FunctionDefinitionCustomRegexpMatch;

public class OnapFunctionDefinitionFactory extends FunctionDefinitionFactory {
    private static Logger logger = FlexLogger.getLogger(OnapFunctionDefinitionFactory.class);
    private static Map<Identifier, FunctionDefinition> mapFunctionDefinitions = new HashMap<>();
    private static boolean needMapInit = true;

    public static final Identifier ID_FUNCTION_CUSTOM_REGEXP_MATCH =
            new IdentifierImpl("org.onap.function.regex-match");

    private static final FunctionDefinition FD_CUSTOM_REGEXP_MATCH =
            new FunctionDefinitionCustomRegexpMatch<>(ID_FUNCTION_CUSTOM_REGEXP_MATCH, DataTypes.DT_STRING);

    private static void register(final FunctionDefinition functionDefinition) {
        mapFunctionDefinitions.put(functionDefinition.getId(), functionDefinition);
    }

    private static void initMap() {

        synchronized (mapFunctionDefinitions) {
            if (needMapInit) {
                needMapInit = false;
                final Field[] declaredFields = StdFunctions.class.getDeclaredFields();
                for (final Field field : declaredFields) {
                    registerFunctionDefinition(field);
                }
                //
                // Our custom function
                //
                // register(FunctionDefinitionCustomRegexpMatch);
                register(FD_CUSTOM_REGEXP_MATCH);
            }
        }
    }

    private static void registerFunctionDefinition(Field field) {
        if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith(StdFunctions.FD_PREFIX)
                && FunctionDefinition.class.isAssignableFrom(field.getType())
                && Modifier.isPublic(field.getModifiers())) {
            try {
                register((FunctionDefinition) (field.get(null)));
            } catch (final IllegalAccessException ex) {
                logger.error(ex.getMessage() + ex);
            }
        }
    }

    public OnapFunctionDefinitionFactory() {
        initMap();
    }

    @Override
    public FunctionDefinition getFunctionDefinition(final Identifier functionId) {
        return mapFunctionDefinitions.get(functionId);
    }
}
