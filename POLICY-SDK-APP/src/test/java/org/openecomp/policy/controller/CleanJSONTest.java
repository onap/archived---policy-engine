/*-
 * ============LICENSE_START=======================================================
 * ECOMP Policy Engine
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

package org.openecomp.policy.controller;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger;
import org.openecomp.policy.common.logging.flexlogger.Logger;

public class CleanJSONTest {
	private static Logger logger = FlexLogger.getLogger(CleanJSONTest.class);
	@Test
	public void testRemoveNullJsonObject() {

	    String content = "{\"naming-models\":[{\"naming-properties\":[{\"property-value\":\"\",\"source-endpoint\":\"\",\"property-name\":\"tes\",\"increment-sequence\":{\"scope\":\"CLOUD_REGION_ID\",\"start-value\":\"df\",\"length\":\"\",\"increment\":\"tes\"},\"source-system\":\"SDN-GC\"}],\"naming-type\":\"tes\",\"naming-recipe\":\"tes\"}]}";
	    JsonObject jsonContent = Json.createReader(new StringReader(content)).readObject();
	    
	    logger.info("before: " + jsonContent);
	    JsonObject removed = CreateDcaeMicroServiceController.removeNull(jsonContent);
	    logger.info("after: " + removed);
	    
	    assertTrue(!removed.equals(jsonContent));
	}

}
