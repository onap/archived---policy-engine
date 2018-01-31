/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
package org.onap.policy.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.Datatype;
import org.onap.policy.rest.jpa.FunctionDefinition;
import org.onap.policy.rest.jpa.PolicyEntity;

public class PolicyControllerTest {

	private static Logger logger = FlexLogger.getLogger(PolicyControllerTest.class);
	private static CommonClassDao commonClassDao;

	@Before
	public void setUp() throws Exception{
		logger.info("setUp: Entering");
        commonClassDao = mock(CommonClassDao.class);
        PolicyController.setCommonClassDao(commonClassDao);
        List<Object> data = new ArrayList<>();
        String policyData = "";
        try {
			ClassLoader classLoader = getClass().getClassLoader();
			policyData = IOUtils.toString(classLoader.getResourceAsStream("Config_SampleTest1206.1.xml"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        PolicyEntity entity = new PolicyEntity();
        entity.setPolicyName("Config_SampleTest.1.xml");
        entity.setPolicyData(policyData);
        entity.setScope("com");
        data.add(entity);

        when(commonClassDao.getDataByQuery("FROM PolicyEntity where policyName = 'Config_SampleTest1206.1.xml' and scope ='com'", new SimpleBindings())).thenReturn(data);

        FunctionDefinition fnDefinition = new FunctionDefinition();
        fnDefinition.setXacmlid("Test");
        fnDefinition.setShortname("Test");
        Datatype dataType = new Datatype();
        dataType.setXacmlId("Test");
        fnDefinition.setDatatypeBean(dataType);
        List<Object> fnObjects = new ArrayList<>();
        fnObjects.add(fnDefinition);
        when(commonClassDao.getData(FunctionDefinition.class)).thenReturn(fnObjects);
	}

	@Test
	public void testInit(){
		PolicyController controller = new PolicyController();
		PolicyController.setjUnit(true);
		controller.init();
		try{
			assertTrue(PolicyController.dropDownMap.size() > 0);
		}catch(Exception e){
			logger.error("Exception Occured"+e);
			fail();
		}
	}
}
