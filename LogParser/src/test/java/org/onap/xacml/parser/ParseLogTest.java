/*-
 * ============LICENSE_START=======================================================
 * LogParser
 * ================================================================================
 * Copyright (C) 2017-2018, 2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.xacml.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.policy.common.im.AdministrativeStateException;
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.im.IntegrityMonitorException;
import org.onap.policy.common.im.StandbyStatusException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.xacml.parser.LogEntryObject.LogType;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "jdk.internal.reflect.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest({ ParseLogTest.class, IntegrityMonitor.class })
public class ParseLogTest {

    private static Logger logger = FlexLogger.getLogger(ParseLogTest.class);
    private Properties config = new Properties();
    private String configFile;
    private String configFileDebug;
    private String configFileError;
    private String testFile1;
    private String testFile2;
    private IntegrityMonitor im;

    /**
     * Setup for the test case execution.
     *
     * @throws Exception if any error occurs
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty("com.sun.management.jmxremote.port", "9998");
        im = Mockito.mock(IntegrityMonitor.class);
        final String regex = "^\\/[a-zA-Z]\\:\\/";

        try {
            Mockito.doNothing().when(im).startTransaction();
        } catch (StandbyStatusException | AdministrativeStateException e) {
            fail();
        }
        Mockito.doNothing().when(im).endTransaction();
        final ClassLoader classLoader = getClass().getClassLoader();
        configFile = classLoader.getResource("test_config.properties").getFile();
        configFileDebug = classLoader.getResource("test_config_debug.properties").getFile();
        configFileError = classLoader.getResource("test_config_error.properties").getFile();
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(configFile);
        final Matcher matcherDebug = pattern.matcher(configFileDebug);
        final Matcher matcherError = pattern.matcher(configFileError);

        if (matcher.find()) {
            configFile = configFile.substring(1);
        }
        if (matcherDebug.find()) {
            configFileDebug = configFileDebug.substring(1);
        }
        if (matcherError.find()) {
            configFileError = configFileError.substring(1);
        }
        testFile1 = classLoader.getResource("LineTest.txt").getFile();
        testFile2 = classLoader.getResource("LineTest2.txt").getFile();

    }

    /**
     * Cleaning off after test case execution.
     */
    @After
    public void tearDown() {

        logger.debug("tearDown: enter");

        final File file = new File("nonExistFile.txt");
        file.delete();
        logger.debug("tearDown: exit");
    }

    @Test
    public void testCountLines() throws IOException {

        logger.debug("testCountLines: enter");

        final int returnValue = ParseLog.countLines(testFile1);
        logger.debug("testCountLines: returnValue: " + returnValue);
        assertEquals(12, returnValue);

        logger.debug("testCountLines: exit");
    }

    @Test
    public void testParseRemoteSystem() {

        logger.debug("testParseRemoteSystem: enter");

        final String line =
                "||org.onap.policy.pap.xacml.rest.XACMLPapServlet$Heartbeat.run(XACMLPapServlet.java:2801)||Heartbeat 'https://localhost:8081/pdp/' status='UP_TO_DATE'";
        final String returnValue = ParseLog.parseRemoteSystem(line);
        logger.debug("testParseRemoteSystem: returnValue: " + returnValue);
        assertEquals("localhost:8081", returnValue);

        logger.debug("testParseRemoteSystem: exit");
    }

    @Test
    public void testGetPropertiesValue() {

        logger.debug("testGetPropertiesValue: enter");

        config = new Properties();
        config.put("RESOURCE_NAME", "logparser_pap01");
        config.put("JDBC_DRIVER", "org.mariadb.jdbc.Driver");
        config.put("JDBC_URL", "jdbc:mariadb://localhost:3306/");
        config.put("JDBC_USER", "root");
        config.put("JDBC_PASSWORD", "password");
        config.put("JMX_URL", "service:jmx:rmi:///jndi/rmi://localhost:9998/jmxrmi");
        config.put("SERVER", "password");
        config.put("JDBC_PASSWORD", "https://localhost:9091/pap/");
        config.put("LOGTYPE", "PAP");
        config.put("LOGPATH", "C:\\Workspaces\\HealthCheck\\pap-rest.log");
        config.put("PARSERLOGPATH", "IntegrityMonitor.log");

        final Properties returnConfig = ParseLog.getPropertiesValue(configFile);
        logger.debug("testGetPropertiesValue: returnConfig: " + returnConfig);
        assertEquals(config.get("RESOURCE_NAME"), returnConfig.get("RESOURCE_NAME"));

        logger.debug("testGetPropertiesValue: exit");
    }

    @Test
    public void testGetPropertiesValue_1() {

        logger.debug("testGetPropertiesValue: enter");

        config = new Properties();
        config.put("RESOURCE_NAME", "logparser_pap01");
        config.put("JDBC_DRIVER", "org.mariadb.jdbc.Driver");
        config.put("JDBC_URL", "jdbc:mariadb://localhost:3306/");
        config.put("JDBC_USER", "root");
        config.put("JDBC_PASSWORD", "password");
        config.put("JMX_URL", "service:jmx:rmi:///jndi/rmi://localhost:9998/jmxrmi");
        config.put("SERVER", "password");
        config.put("JDBC_PASSWORD", "https://localhost:9091/pap/");
        config.put("LOGTYPE", "PAP");
        config.put("LOGPATH", "C:\\Workspaces\\HealthCheck\\debug\\pap-rest.log");
        config.put("PARSERLOGPATH", "IntegrityMonitor.log");

        final Properties returnConfig = ParseLog.getPropertiesValue(configFileDebug);
        logger.debug("testGetPropertiesValue: returnConfig: " + returnConfig);
        assertEquals(config.get("RESOURCE_NAME"), returnConfig.get("RESOURCE_NAME"));

        logger.debug("testGetPropertiesValue_1: exit");
    }

    @Test
    public void testGetPropertiesValue_2() {

        logger.debug("testGetPropertiesValue: enter");

        config = new Properties();
        config.put("RESOURCE_NAME", "logparser_pap01");
        config.put("JDBC_DRIVER", "org.mariadb.jdbc.Driver");
        config.put("JDBC_URL", "jdbc:mariadb://localhost:3306/");
        config.put("JDBC_USER", "root");
        config.put("JDBC_PASSWORD", "password");
        config.put("JMX_URL", "service:jmx:rmi:///jndi/rmi://localhost:9998/jmxrmi");
        config.put("SERVER", "password");
        config.put("JDBC_PASSWORD", "https://localhost:9091/pap/");
        config.put("LOGTYPE", "PAP");
        config.put("LOGPATH", "C:\\Workspaces\\HealthCheck\\error\\pap-rest.log");
        config.put("PARSERLOGPATH", "IntegrityMonitor.log");

        final Properties returnConfig = ParseLog.getPropertiesValue(configFileError);
        logger.debug("testGetPropertiesValue: returnConfig: " + returnConfig);
        assertEquals(config.get("RESOURCE_NAME"), returnConfig.get("RESOURCE_NAME"));

        logger.debug("testGetPropertiesValue_2: exit");
    }

    @Test
    public void testGetPropertiesFail() {

        logger.debug("testGetPropertiesFail: enter");

        final Properties returnValue = ParseLog.getPropertiesValue("nonExistFile");
        logger.debug("testGetPropertiesFail: returnValue: " + returnValue);
        assertEquals(null, returnValue);

        logger.debug("testGetPropertiesFail: exit");
    }

    @Test
    public void testParseDate() {

        logger.debug("testParseDate: enter");

        String line = "2016-02-23 08:07:30";
        final Date returnValue = ParseLog.parseDate(line, "yyyy-MM-dd HH:mm:ss", false);
        logger.debug("testParseDate: returnValue: " + returnValue);
        line = returnValue.toString().substring(0, returnValue.toString().lastIndexOf(":30") + 3);
        assertEquals("Tue Feb 23 08:07:30", line);

        logger.debug("testParseDate: exit");
    }

    @Test
    public void testPullLastLineRead() {

        logger.debug("testPullLastLineRead: enter");
        final File file = new File(testFile1);
        String returnValue = null;
        try {
            returnValue = ParseLog.pullLastLineRead(file, "pap-rest.log");
            logger.debug("testPullLastLineRead: returnValue for pap-rest.log: " + returnValue);
        } catch (final IOException e) {
            fail();
        }
        assertEquals("52", returnValue);

        try {
            returnValue = ParseLog.pullLastLineRead(file, "debug.log");
            logger.debug("testPullLastLineRead: returnValue for debug.log: " + returnValue);
        } catch (final IOException e) {
            fail();
        }
        assertEquals("17", returnValue);

        try {
            returnValue = ParseLog.pullLastLineRead(file, "error.log");
            logger.debug("testPullLastLineRead: returnValue for error.log: " + returnValue);
        } catch (final IOException e) {
            fail();
        }
        assertEquals("22", returnValue);

        logger.debug("testPullLastLineRead: exit");
    }

    @Test
    public void testPullLastLineReadNoFile() {

        logger.debug("testPullLastLineReadNoFile: enter");

        final File file = new File("nonExistFile.txt");
        try {
            assertEquals(null, ParseLog.pullLastLineRead(file, "pap-rest"));
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullLastLineReadNoFile: exit");
    }

    @Test
    public void testPullLastLineReadFail() {

        logger.debug("testPullLastLineReadFail: enter");

        final File file = new File(testFile2);
        try {
            assertEquals(null, ParseLog.pullLastLineRead(file, "pap-rest"));
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullLastLineReadFail: exit");
    }

    @Test
    public void testPullOutLogValues() {

        logger.debug("testPullOutLogValues: enter");
        // ERROR_VALUE
        // Open the file
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            final String strLine = br.readLine();
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "ERROR");
            assertEquals("ERROR_VALUE", retrunObject.getDescription());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValues: exit");
    }

    @Test
    public void testGetPaths() {

        logger.debug("testGetPaths: enter");

        try {
            // valid test
            String logPaths = "C:\\pap-log\\pap-rest.log;C:\\pap-log\\debug.log;C:\\pap-log\\error.log";
            String[] retrunObject = ParseLog.getPaths(logPaths);
            assertEquals(3, retrunObject.length);

            // valid test
            logPaths = "C:\\pap-log\\pap-rest.log";
            retrunObject = ParseLog.getPaths(logPaths);
            assertEquals(1, retrunObject.length);

            // invalid test
            logPaths = "";
            retrunObject = ParseLog.getPaths(logPaths);
            assertTrue(retrunObject == null);

        } catch (final Exception e) {
            fail();
        }

        logger.debug("testGetPaths: exit");
    }

    @Test
    public void testPullOutLogValuesSecond() {

        logger.debug("testPullOutLogValuesSecond: enter");
        // ERROR_VALUE
        // Open the file
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            strLine = br.readLine();
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "INFO");
            assertEquals(LogType.INFO, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesSecond: exit");
    }

    @Test
    public void testPullOutLogValuesThird() {

        logger.debug("testPullOutLogValuesThird: enter");
        // ERROR_VALUE
        // Open the file
        FileInputStream fstream;
        try {
            final int number = 3;
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            for (int i = 0; i < number; i++) {
                strLine = br.readLine();
            }
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PAP");
            assertEquals(LogType.INFO, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesThird: exit");
    }

    @Test
    public void testPullOutLogValuesFourth() {

        logger.debug("testPullOutLogValuesFourth: enter");
        // Open the file
        FileInputStream fstream;
        try {
            final int number = 4;
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            for (int i = 0; i < number; i++) {
                strLine = br.readLine();
            }
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PAP");
            assertEquals(LogType.INFO, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesFourth: exit");
    }

    @Test
    public void testPullOutLogValuesFith() {

        logger.debug("testPullOutLogValuesFith: enter");
        // Open the file
        FileInputStream fstream;
        try {
            final int number = 5;
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            for (int i = 0; i < number; i++) {
                strLine = br.readLine();
            }
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PyPDP");
            assertEquals(LogType.WARN, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesFith: exit");
    }

    @Test
    public void testPullOutLogValuesSixth() {

        logger.debug("testPullOutLogValuesSixth: enter");
        // Open the file
        FileInputStream fstream;
        try {
            final int number = 6;
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            for (int i = 0; i < number; i++) {
                strLine = br.readLine();
            }
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PyPDP");
            assertEquals(LogType.SEVERE, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesSixth: exit");
    }

    @Test
    public void testPullOutLogValuesSeven() {

        logger.debug("testPullOutLogValuesSeven: enter");
        // Open the file
        FileInputStream fstream;
        try {
            final int number = 7;
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            for (int i = 0; i < number; i++) {
                strLine = br.readLine();
            }
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "Console");
            assertEquals(LogType.ERROR, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesSeven: exit");
    }

    @Test
    public void testPullOutLogValuesEight() {

        logger.debug("testPullOutLogValuesEight: enter");
        // Open the file
        FileInputStream fstream;
        try {
            final int number = 8;
            fstream = new FileInputStream(testFile1);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = br.readLine();
            for (int i = 0; i < number; i++) {
                strLine = br.readLine();
            }
            final LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "pap");
            assertEquals(LogType.WARN, retrunObject.getLogType());
            br.close();
        } catch (final IOException e) {
            fail();
        }

        logger.debug("testPullOutLogValuesEight: exit");
    }

    @Test
    public void testPullOutLogValuesNull() {

        logger.debug("testPullOutLogValuesNull: enter");
        // Open the file
        final LogEntryObject retrunObject = ParseLog.pullOutLogValues("", "Console");
        assertEquals(null, retrunObject);

        logger.debug("testPullOutLogValuesNull: exit");
    }

    @Test
    public void testLogEntryObject() {

        logger.debug("testLogEntryObject: enter");

        final Date date = new Date();

        // Open the file
        final LogEntryObject logObject = new LogEntryObject();
        logObject.setSystem("vm02");
        logObject.setSystemType("pap");
        logObject.setDate(date);
        logObject.setRemote("remote");

        assertEquals("vm02", logObject.getSystem());
        assertEquals("pap", logObject.getSystemType());
        assertEquals(date, logObject.getDate());
        assertEquals("remote", logObject.getRemote());

        logger.debug("testLogEntryObject: exit");
    }

    @Test
    public void testProcess() {

        logger.debug("testProcess: enter");

        final String line = "2015-04-01 09:13:44.947" + "  DEBUG 17482 --- [nio-8480-exec-7] "
                + "c.a.l.onap.policy.std.StdPolicyConfig" + "   : config Retrieved ";

        im = Mockito.mock(IntegrityMonitor.class);
        try {
            Mockito.doNothing().when(im).startTransaction();
        } catch (final IntegrityMonitorException e) {
            fail();
        }
        Mockito.doNothing().when(im).endTransaction();
        ParseLog.process(line, "pap", LogType.INFO);

        logger.debug("testProcess: exit");
    }

    @Test
    public void testMain() {
        try {
            ParseLog.main(new String[] {});
        } catch (final Exception e) {
            logger.debug("exception occured while executing the test: exit");
        }
    }

    @Test
    public void testMainDebug() {
        try {
            final Properties returnConfig = ParseLog.getPropertiesValue(configFileDebug);
            PowerMockito.mockStatic(IntegrityMonitor.class);
            Mockito.when(IntegrityMonitor.getInstance("test", returnConfig)).thenReturn(im);
            ParseLog.main(new String[] {});
            Thread.sleep(30000);
        } catch (final Exception e) {
            logger.debug("exception occured while executing the test: exit");
        }
    }

    @Test
    public void testMainError() {
        try {
            final Properties returnConfig = ParseLog.getPropertiesValue(configFileError);
            PowerMockito.mockStatic(IntegrityMonitor.class);
            Mockito.when(IntegrityMonitor.getInstance("test", returnConfig)).thenReturn(im);
            ParseLog.main(new String[] {});
            Thread.sleep(30000);
        } catch (final Exception e) {
            logger.debug("exception occured while executing the test: exit");
        }
    }
}
