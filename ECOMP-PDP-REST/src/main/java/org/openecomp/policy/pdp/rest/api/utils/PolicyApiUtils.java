/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PDP-REST
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
package org.openecomp.policy.pdp.rest.api.utils;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

import com.google.common.base.CharMatcher;

public class PolicyApiUtils {
    private static Logger LOGGER = FlexLogger.getLogger(PolicyApiUtils.class
            .getName());

    public static Boolean validateNONASCIICharactersAndAllowSpaces(
            String jsonString) {
        Boolean isValidForm = false;
        if (jsonString.isEmpty()) {
            LOGGER.error("The Value is empty.");
            return false;
        } else {
            if (CharMatcher.ASCII.matchesAllOf((CharSequence) jsonString)) {
                LOGGER.info("The Value does not contain ASCII Characters");
                isValidForm = true;
            } else {
                LOGGER.error("The Value Contains Non ASCII Characters");
                isValidForm = false;
            }
        }
        return isValidForm;
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    public static JsonObject stringToJsonObject(String value)
            throws JsonException, JsonParsingException, IllegalStateException {
        JsonReader jsonReader = Json.createReader(new StringReader(value));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
    }
}
