/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.DictionaryData;
import org.onap.policy.rest.util.MsModelUtils.ModelType;

public class MsModelUtilsTest {
    private static Logger logger = FlexLogger.getLogger(MsModelUtilsTest.class);
    private static CommonClassDao commonClassDao;

    /**
     * Set tests up.
     *
     * @throws Exception on setup failures
     */
    @Before
    public void setUp() throws Exception {
        List<Object> dictionaryData = new ArrayList<Object>();
        DictionaryData testData = new DictionaryData();
        testData.setDictionaryName("dictionaryName");
        testData.setDictionaryDataByName("dictionaryDataByName");
        dictionaryData.add(testData);
        logger.info("setUp: Entering");
        commonClassDao = mock(CommonClassDao.class);
        when(commonClassDao.getDataById(DictionaryData.class, "dictionaryName", "GocVNFType"))
                        .thenReturn(dictionaryData);
    }

    @Test
    public void testMsModelUtils() {
        HashMap<String, MsAttributeObject> classMap = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("DKaTVESPolicy-v1802.xmi").getFile());
        MsModelUtils utils = new MsModelUtils("http://org.onap", "http://org.onap.policy");
        Map<String, MsAttributeObject> tempMap = utils.processEpackage(file.getAbsolutePath().toString(),
                        ModelType.XMI);
        classMap.putAll(tempMap);
        MsAttributeObject mainClass = classMap.get("StandardDeviationThreshold");
        String dependTemp = StringUtils.replaceEach(mainClass.getDependency(), new String[]
            { "[", "]", " " }, new String[]
            { "", "", "" });
        List<String> dependency = new ArrayList<String>(Arrays.asList(dependTemp.split(",")));
        dependency = utils.getFullDependencyList(dependency, classMap);
        String subAttribute = utils.createSubAttributes(dependency, classMap, "StandardDeviationThreshold");
        assertTrue(subAttribute != null);
    }

    /**
     * Run the void stringBetweenDots(String, String) method test.
     */

    @Test
    public void testStringBetweenDots() {

        // expect: uniqueKeys should contain a string value
        MsModelUtils controllerA = new MsModelUtils();
        String str = "testing\\.byCorrectWay\\.OfDATA";
        assertEquals(1, controllerA.stringBetweenDots(str));

        // expect: uniqueKeys should not contain a string value
        str = "testing\byWrongtWay.\\OfDATA";
        MsModelUtils controllerB = new MsModelUtils();
        assertEquals(0, controllerB.stringBetweenDots(str));
    }

    /**
     * Run the load method test.
     */
    @Test
    public void testLoad() {

        boolean isLocalTesting = true;
        MsModelUtils controller = new MsModelUtils();
        String fileName = null;
        Map<String, String> result = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            fileName = new File(classLoader.getResource("TESTMODEL-v1806.yml").getFile()).getAbsolutePath();
        } catch (Exception e1) {
            logger.error("Exception Occured while loading file" + e1);
        }
        if (isLocalTesting) {
            try {
                result = controller.load(fileName);
            } catch (IOException e) {
                logger.error("testLoad", e);
                result = null;
            } catch (ParserException e) {
                logger.error("testLoad", e);
            }

            assertTrue(result != null && !result.isEmpty());
            logger.debug("result : " + result);
        }

        logger.debug("testLoad: exit");
    }

    /**
     * Run the void parseTosca(String) method test.
     */

    @Test
    public void testParseTosca() {

        logger.debug("testParseTosca: enter");
        boolean isLocalTesting = true;
        String fileName = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            fileName = new File(classLoader.getResource("TESTMODEL-v1806.yml").getFile()).getAbsolutePath();
        } catch (Exception e1) {
            logger.error("Exception Occured while loading file" + e1);
        }

        MsModelUtils controller = new MsModelUtils(commonClassDao);
        if (isLocalTesting) {
            try {
                controller.parseTosca(fileName);
            } catch (Exception e) {
                fail("parseTosca caused error: " + e);
            }
        }
        logger.debug("testParseTosca: exit");
    }

}
