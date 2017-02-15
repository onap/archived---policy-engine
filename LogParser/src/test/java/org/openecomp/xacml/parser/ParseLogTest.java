/*-
 * ============LICENSE_START=======================================================
 * LogParser
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

package org.openecomp.xacml.parser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.openecomp.policy.common.im.AdministrativeStateException;
import org.openecomp.policy.common.im.IntegrityMonitor;
import org.openecomp.policy.common.im.StandbyStatusException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.xacml.parser.LogEntryObject;
import org.openecomp.xacml.parser.ParseLog;
import org.openecomp.xacml.parser.LogEntryObject.LOGTYPE;


public class ParseLogTest {

	private ParseLog logParser = new ParseLog();
	private Properties config = new Properties();
	private String configFile = "test_config.properties";
	private static Properties myProp;
	private static Properties systemProps;
	private static String resourceName;
	private IntegrityMonitor im;

	
	@Before
	public void setUp() throws Exception {
		System.setProperty("com.sun.management.jmxremote.port", "9998");
		im = Mockito.mock(IntegrityMonitor.class);
		
		// Need PowerMockito for mocking static method getInstance(...)
	//	PowerMockito.mockStatic(IntegrityMonitor.class);
		
		try {
			Mockito.doNothing().when(im).startTransaction();
		} catch (StandbyStatusException | AdministrativeStateException e) {
			fail();
		}
		Mockito.doNothing().when(im).endTransaction();

	}

	@After
	public void tearDown() {
		File file = new File("nonExistFile.txt");
		file.delete();
//		systemProps.remove("com.sun.management.jmxremote.port");
	}

	@Test
	public void testMain() {
		try {	
			
			logParser.main(null);
		} catch (Exception e) {
			//fail();
		}
	}

	@Test
	public void testCountLines() throws IOException {
		String fileName = "LineTest.txt";
		int returnValue = logParser.countLines(fileName);
		
		assertEquals(9, returnValue);
	}
	
	@Test
	public void testParseRemoteSystem() {
		String line = "||org.openecomp.policy.pap.xacml.rest.XACMLPapServlet$Heartbeat.run(XACMLPapServlet.java:2801)||Heartbeat 'https://localhost:8081/pdp/' status='UP_TO_DATE'";
		String returnValue = ParseLog.parseRemoteSystem(line);
		assertEquals("localhost:8081", returnValue);
	}

	@Test
	public void testGetPropertiesValue() {
		config = new Properties();
		config.put("RESOURCE_NAME", "logparser_pap01");
		config.put("JDBC_DRIVER" ,"com.mysql.jdbc.Driver");
		config.put("JDBC_URL", "jdbc:mysql://localhost:3306/");
		config.put("JDBC_USER", "root");
		config.put("JDBC_PASSWORD", "password");
		config.put("JMX_URL", "service:jmx:rmi:///jndi/rmi://localhost:9998/jmxrmi");
		config.put("SERVER", "password");
		config.put("JDBC_PASSWORD", "https://localhost:9091/pap/");
		config.put("LOGTYPE", "PAP");
		config.put("LOGPATH", "C:\\Workspaces\\HealthCheck\\pap-rest.log");
		config.put("PARSERLOGPATH", "IntegrityMonitor.log");
		
		Properties returnConfig = logParser.getPropertiesValue(configFile);

		
		assertEquals(config.get("RESOURCE_NAME"), returnConfig.get("RESOURCE_NAME"));	
	}
	
	@Test
	public void testGetPropertiesFail() {	
		Properties returnValue = ParseLog.getPropertiesValue("nonExistFile");
		
		assertEquals(null, returnValue);	
	}

	@Test
	public  void  testParseDate(){
		String line = "2016-02-23 08:07:30";
		Date returnValue = ParseLog.parseDate(line, "yyyy-MM-dd HH:mm:ss", false);
		
		assertEquals("Tue Feb 23 08:07:30 CST 2016", returnValue.toString());
	}
	
	@Test
	public  void  testParseDateFail(){
		String line = "2016-02-23 08:07:30";
		Date returnValue = ParseLog.parseDate(line, "yyyy-MM-dd HH:mm:ss", true);
		
		assertEquals(null, returnValue);
	}
	
	@Test
	public void testPullLastLineRead(){
		
		File file = new File("LineTest.txt");
		String returnValue = null;
		try {
			returnValue = ParseLog.PullLastLineRead(file).trim();
		} catch (IOException e) {
			fail();
		}		
		assertEquals("12", returnValue);

	}
	
	@Test
	public void testPullLastLineReadNoFile(){
		
		File file = new File("nonExistFile.txt");
		try {
			assertEquals(null, ParseLog.PullLastLineRead(file));
		} catch (IOException e) {
			fail();
		}
	}
	@Test
	public void testPullLastLineReadFail(){
		
		File file = new File("LineTest2.txt");
		try {
			assertEquals(null, ParseLog.PullLastLineRead(file));
		} catch (IOException e) {
			fail();
		}
	}

	@Test
	public void testPullOutLogValues(){
		//ERROR_VALUE
		// Open the file
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "ERROR");
			assertEquals("ERROR_VALUE", retrunObject.getDescription());
			br.close();
		} catch (IOException e) {
			fail();
		}	
//		assert(true);
	}
	@Test
	public void testPullOutLogValuesSecond(){
		//ERROR_VALUE
		// Open the file
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			strLine = br.readLine();
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "INFO");
			assertEquals(LOGTYPE.INFO, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}
	
	@Test
	public void testPullOutLogValuesThird(){
		//ERROR_VALUE
		// Open the file
		FileInputStream fstream;
		try {
			int number = 3;
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			for (int i =0; i < number; i++){
				strLine = br.readLine();
			}
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PAP");
			assertEquals(LOGTYPE.INFO, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}

	@Test
	public void testPullOutLogValuesFourth(){
		// Open the file
		FileInputStream fstream;
		try {
			int number = 4;
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			for (int i =0; i < number; i++){
				strLine = br.readLine();
			}
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PAP");
			assertEquals(LOGTYPE.INFO, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}
	
	@Test
	public void testPullOutLogValuesFith(){
		// Open the file
		FileInputStream fstream;
		try {
			int number = 5;
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			for (int i =0; i < number; i++){
				strLine = br.readLine();
			}
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PyPDP");
			assertEquals(LOGTYPE.WARN, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}
	
	@Test
	public void testPullOutLogValuesSixth(){
		// Open the file
		FileInputStream fstream;
		try {
			int number = 6;
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			for (int i =0; i < number; i++){
				strLine = br.readLine();
			}
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "PyPDP");
			assertEquals(LOGTYPE.SEVERE, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}

	@Test
	public void testPullOutLogValuesSeven(){
		// Open the file
		FileInputStream fstream;
		try {
			int number = 7;
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			for (int i =0; i < number; i++){
				strLine = br.readLine();
			}
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "Console");
			assertEquals(LOGTYPE.ERROR, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}
	
	@Test
	public void testPullOutLogValuesEight(){
		// Open the file
		FileInputStream fstream;
		try {
			int number = 8;
			fstream = new FileInputStream("LineTest.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine = br.readLine();
			for (int i =0; i < number; i++){
				strLine = br.readLine();
			}
			LogEntryObject retrunObject = ParseLog.pullOutLogValues(strLine, "pap");
			assertEquals(LOGTYPE.WARN, retrunObject.getLogType());
			br.close();
		} catch (IOException e) {
			fail();
		}	
	}
	
	@Test
	public void testPullOutLogValuesNull(){
		// Open the file
		FileInputStream fstream;
		LogEntryObject retrunObject = ParseLog.pullOutLogValues("", "Console");
		assertEquals(null, retrunObject);
	}
	
	@Test
	public void testLogEntryObject(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
	 
		// Open the file
		LogEntryObject logObject = new LogEntryObject();
		logObject.setSystem("vm02");
		logObject.setSystemType("pap");
		logObject.setDate(date);
		logObject.setRemote("remote");

		assertEquals("vm02", logObject.getSystem());
		assertEquals("pap", logObject.getSystemType());
		assertEquals(date, logObject.getDate());
		assertEquals("remote", logObject.getRemote());
	}

	@Test
	public void testProcess(){
		String line = "2015-04-01 09:13:44.947  DEBUG 17482 --- [nio-8480-exec-7] c.a.l.ecomp.policy.std.StdPolicyConfig   : config Retrieved ";
	
		im = Mockito.mock(IntegrityMonitor.class);
		try {
			Mockito.doNothing().when(im).startTransaction();
		} catch (StandbyStatusException | AdministrativeStateException e) {
			fail();
		}
		Mockito.doNothing().when(im).endTransaction();
		ParseLog.process(line, "pap");
	}
}
