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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.openecomp.policy.common.im.AdministrativeStateException;
import org.openecomp.policy.common.im.IntegrityMonitor;
import org.openecomp.policy.common.im.StandbyStatusException;
import org.openecomp.xacml.parser.LogEntryObject.LOGTYPE;

/**
 * Parse log files and store the information in a H2 database.
 * 
 *
 */
public class ParseLog {
	
	private static final Logger logger = Logger.getLogger(ParseLog.class.getName());

	private static String system;
	private static int lastNumberRead = 0;
	private static String type;
	private static long startFileSize;
	private static String systemLogFile;
	private static String logFile;
	
	private static String JDBC_URL;
	private static String JDBC_USER;
	private static String JDBC_PASSWORD = "";
	private static String JDBC_DRIVER;
	private static int maxLength = 255;   //Max length that is allowed in the DB table
	private static String resourceName;
	private static long sleepTimer = 50000;
	static IntegrityMonitor im;

	private static RandomAccessFile randomAccessFile;
	
	public static void main(String[] args) throws Exception {

		Properties logProperties = getPropertiesValue("parserlog.properties");
		Path filePath = Paths.get(logFile);
		File file = new File(logFile);
		File fileLog = new File(systemLogFile);
		startFileSize = file.length();
			
		im = IntegrityMonitor.getInstance(resourceName,logProperties );
		
		logger.info("System: " + system );  
		logger.info("System type: " + type );  
		logger.info("Logging File: " + systemLogFile );
		logger.info("log file: " + logFile);
		logger.info("JDBC_URL: " + JDBC_URL);
		logger.info("JDBC_DRIVER: " + JDBC_DRIVER);
		
		String filesRead = PullLastLineRead(fileLog);
		if (filesRead!= null){			
			filesRead = filesRead.replaceAll("(\\r\\n|\\n)", "<br />");
			lastNumberRead= Integer.parseInt(filesRead.trim());
		}else{
			lastNumberRead = 0;
		}
		startFileSize =  countLines(logFile);
		logger.info("File Line Count: " + startFileSize + " value read in: " + lastNumberRead);
		if (startFileSize < lastNumberRead ){
			logger.error("Filed Rolled: set Last number read to 0");
			lastNumberRead = 0;
		}
		Runnable  runnable = new Runnable (){
		public void run(){
			while (true){		
	             
				if (file.isFile()){
					try (Stream<String> lines = Files.lines(filePath, Charset.defaultCharset()).onClose(() -> logger.info("Last line Read: " + lastNumberRead)).skip(lastNumberRead)) {
						
						lines.forEachOrdered(line -> process(line, type));
		
					} catch (IOException e) {
						logger.error("Error processing line in log file: " + e);
					}	
				}
				try {
					Thread.sleep(sleepTimer);
					startFileSize =  countLines(logFile);
				} catch (InterruptedException | IOException e) {
					logger.error("Error: " + e);
				}
				
				logger.info("File Line Count: " + startFileSize + " value read in: " + lastNumberRead);
				if (startFileSize < lastNumberRead ){
					logger.info("Failed Rolled: set Last number read to 0");
					lastNumberRead = 0;
				}
			}	
		}
		};
		
		Thread thread = new Thread(runnable);
		thread.start();

	}			

	public static int countLines(String filename) throws IOException {
	    LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
	    int cnt = 0;
	    while ((reader.readLine()) != null);
	    cnt = reader.getLineNumber(); 
	    reader.close();
	    return cnt;
	}	
	
	public static String PullLastLineRead(File file) throws IOException {
		if(!file.exists()){
			file.createNewFile();
			return null;
		}
		randomAccessFile = new RandomAccessFile(file, "r");
        StringBuilder builder = new StringBuilder();
        long length = file.length();
        length--;
        randomAccessFile.seek(length);
        for(long seek = length; seek >= 0; --seek){
            randomAccessFile.seek(seek);
            char c = (char)randomAccessFile.read();
            builder.append(c);
            if(c == '\n'){
                builder = builder.reverse();
                if (builder.toString().contains("Last line Read:")){
            		String[] parseString = builder.toString().split("Last line Read:");
            		String returnValue = parseString[1].replace("\r", "");
            		return returnValue.trim();
            	}
                builder = null;
                builder = new StringBuilder();
             }

        }
		return null;
	}

	public static LogEntryObject pullOutLogValues(String line, String type){
		Date date;
		LogEntryObject logEntry = new LogEntryObject();
		logEntry.setSystemType(type);
		String description = null;
		
		logEntry.setSystem(system);
		
		//Values for PDP/PAP log file
		if(line.contains("||INFO||") || line.contains("||ERROR||")){
			String[] splitString = line.split("[||]");
			String dateString = splitString[0].substring(0, 19);
			logEntry.setDescription(splitString[splitString.length-1]);	

			//parse out date
			date = parseDate(dateString.replace("T", " "), "yyyy-MM-dd HH:mm:ss", false);
			logEntry.setDate(date);
			
			logEntry.setRemote(parseRemoteSystem(line));
			if (line.contains("||INFO||")){
				logEntry.setLogType(LOGTYPE.INFO);
			}else{
				logEntry.setLogType(LOGTYPE.ERROR);
			}		
		}else if (line.contains("INFO") && line.contains(")-")){
			//parse out description
			logEntry.setDescription(line.substring(line.indexOf(")-")+3));

			date = parseDate(line, "yy_MM_dd_HH_mm_ss", true);
			logEntry.setDate(date);
	
			logEntry.setRemote(parseRemoteSystem(line));
			logEntry.setLogType(LOGTYPE.INFO);
		} else if (line.contains("INFO") && line.contains("--- [")){
			//parse out description
			String temp = line.substring(line.indexOf("---")+1);
			String[] split = temp.split(":");

			logEntry.setDescription(split[1]);

			//parse out date
			date = parseDate(line, "yyyy-MM-dd HH:mm:ss", false);
			logEntry.setDate(date);
			
			//remote system
			logEntry.setRemote(parseRemoteSystem(line));
			logEntry.setLogType(LOGTYPE.INFO);
		}else if (line.contains("SEVERE") && line.contains("[main]")){			
			String[] splitString = line.split(" ");
			
			for (int i = 5; i < splitString.length; i++){
				description = description +  " " + splitString[i];
			}

			logEntry.setDescription(description);
			//parse out date
			date = parseDate(line, "dd-MMM-yyyy HH:mm:ss", false);
			logEntry.setDate(date);
			logEntry.setLogType(LOGTYPE.SEVERE);
		} else if (line.contains("WARN") && line.contains(")-")){
			//parse out description

			logEntry.setDescription(line.substring(line.indexOf(")-")+3));

			//parse out date
			date = parseDate(line, "yy_MM_dd_HH_mm_ss", true);
			logEntry.setDate(date);
			
			//remote system
			logEntry.setRemote(parseRemoteSystem(line));
			logEntry.setLogType(LOGTYPE.WARN);
		}else if (line.contains("WARNING") && type =="PyPDP"){
			String[] splitString = line.split(" ");
			for (int i = 5; i < splitString.length; i++){
				description = description +  " " + splitString[i];
			}

			//parse out date
			date = parseDate(line, "dd-MMM-yyyy HH:mm:ss", false);
			logEntry.setDate(date);
			logEntry.setLogType(LOGTYPE.WARN);
		}else if (line.contains("ERROR") && line.contains(")-")){
			//parse out description
			description = line.substring(line.indexOf(")-")+3);

			//parse out date
			date = parseDate(line, "yy_MM_dd_HH_mm_ss", true);
			logEntry.setDate(date);
			//remote system
			logEntry.setRemote(parseRemoteSystem(line));
			logEntry.setLogType(LOGTYPE.ERROR);
		}else {
			return null;
		}
		

		return logEntry;
	}

	private static void DBClose(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			logger.error("Error closing DB Connection: " + e);
			
		}
	}

	public static void process(String line, String type)  {
		LogEntryObject returnLogValue = null;
		if (im!=null){
			try {
				im.startTransaction();
			} catch (AdministrativeStateException e) {
				logger.error("Error received" + e);
				
			} catch (StandbyStatusException e) {
				logger.error("Error received" + e);
			}
		}
		returnLogValue = pullOutLogValues(line, type);
		lastNumberRead++;
		if (returnLogValue!=null){
			writeDB(returnLogValue);
		}
		if (im!=null){
			im.endTransaction();
		}
	}
	
	private static void writeDB(LogEntryObject returnLogValue) {
		Connection conn = DBConnection(JDBC_DRIVER, JDBC_URL, JDBC_USER,JDBC_PASSWORD);
		DBAccesss(conn, returnLogValue.getSystem(), returnLogValue.getDescription(),  
						returnLogValue.getDate(), returnLogValue.getRemote(), 
						returnLogValue.getSystemType(), returnLogValue.getLogType().toString());
		DBClose(conn);	
	}

	private static Connection DBConnection(String driver, String jdbc, String user, String pass){
        
        try {
        	Class.forName(driver);
			Connection conn = DriverManager.getConnection(jdbc, user, pass);
			return conn;
		} catch ( Exception e) {
			logger.error("Error connecting to DB: " + e);
		}
		return null;
	}
	private static void DBAccesss(Connection conn, String system, String description, Date date, String remote, String type, String logType)  {
		
		String sdate = null;
		
		if (date!=null){
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdate = formatter.format(date);		
		}
		
		//ensure the length of description is less than the maximumm db char length
		if (description.length() > maxLength) {
			description = description.substring(0, maxLength);
		}
		
		try {
			PreparedStatement prep = conn.prepareStatement("insert into SYSTEMLOGDB values (NULL, ?, ?, ?,  ?,  ?, ?);");
			prep.setString(1, system);
			prep.setString(2, description);
			prep.setString(3, remote);
			prep.setString(4, type);
			prep.setString(5, sdate);
			prep.setString(6, logType);

			prep.executeUpdate();
			prep.close();

		} catch (SQLException e1) {
			logger.error("Error trying to excute SQL Statment: " + e1);
		}
	}

	public static Date parseDate(String dateline, String pattern, boolean singleSplit)  {
		
		Date returnDate;
		String[] splitString = dateline.split(" ");
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);	
		if (singleSplit){
			try {
				returnDate = formatter.parse(splitString[0]);
			} catch (ParseException e) {
				logger.error("Unable to parse date for line: " + dateline);
				returnDate = null;
			}
		}else{
			String tmpString = splitString[0] + " " + splitString[1];
			try {
				returnDate = formatter.parse(tmpString);
			} catch (ParseException e) {
				logger.error("Unable to parse date for line: " + dateline);
				returnDate = null;
			}
		}
			
		return returnDate; 
	}

	
	public static String parseRemoteSystem(String line) {
		
		if (line.contains("http") && !(line.contains("www.w3.org"))){
	
			Pattern pattern = Pattern.compile("://(.+?)/");
			Matcher remote = pattern.matcher(line);
			if (remote.find())
			{
				return remote.group(1);
			} 
		}
		return null;
	}
	
	public static Properties getPropertiesValue(String fileName) {
		Properties config = new Properties();
		Path file = Paths.get(fileName);
		if (Files.notExists(file)) {
			logger.info("File doesn't exist in the specified Path "	+ file.toString());
		}else{ 
			if (file.toString().endsWith(".properties")) {
				InputStream in;
				try {
					in = new FileInputStream(file.toFile());
					config.load(in);
							
					resourceName = config.getProperty("RESOURCE_NAME");
					system = config.getProperty("SERVER");
					type = config.getProperty("LOGTYPE");
					systemLogFile = config.getProperty("PARSERLOGPATH");
					logFile = config.getProperty("LOGPATH");
					JDBC_URL = config.getProperty("JDBC_URL").replace("'", "");
					JDBC_USER = config.getProperty("JDBC_USER");
					JDBC_DRIVER =  config.getProperty("JDBC_DRIVER");
					JDBC_PASSWORD = config.getProperty("JDBC_PASSWORD");
					return config;

				} catch (IOException e) {					
					logger.info("Error porcessing Cofnig file will be unable to create Health Check");
				}
				
			}
		}
		return null;
	}	
}
