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

package org.onap.xacml.parser;

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
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.onap.policy.common.im.AdministrativeStateException;
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.im.StandbyStatusException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.xacml.parser.LogEntryObject.LOGTYPE;

/**
 * Parse log files and store the information in a H2 database.
 * 
 *
 */
public class ParseLog {
	
	// only logging last line of each log file processed to the log4j log file defined by property - PARSERLOGPATH
	private static final Logger log4jlogger = Logger.getLogger(ParseLog.class.getName());

	// processing logging 
	private static org.onap.policy.common.logging.flexlogger.Logger logger = FlexLogger.getLogger(ParseLog.class.getName());

	private static String system;
	private static int lastNumberRead = 0;
	private static int debuglastNumberRead = 0;
	private static int errorlastNumberRead = 0;
	private static String type;
	private static long startFileSize;
	private static long debugStartFileSize;
	private static long errorStartFileSize;
	private static String systemLogFile;
	private static String logFile;
	private static String debuglogFile;
	private static String errorlogFile;
	private static String JDBC_URL;
	private static String JDBC_USER;
	private static String JDBC_PASSWORD = "";
	private static String JDBC_DRIVER;
	private static int maxLength = 255;   //Max length that is allowed in the DB table
	private static String resourceName;
	private static long sleepTimer = 50000;
	static  IntegrityMonitor im;
	private static boolean isMissingLogFile;
	//Default:Timer initial delay and the delay between in milliseconds before task is to be execute
	private static int TIMER_DELAY_TIME = 1000;	
	//Default:Timer scheduleAtFixedRate period - time in milliseconds between successive task executions
	private static int CHECK_INTERVAL = 86400000;  // run this clean up once a day

	private static RandomAccessFile randomAccessFile;
	
	private static String loggingProcess = "Error processing line in ";

	private static int TIME_FRAME = 5;
	
	public static void main(String[] args) throws Exception {

		Properties logProperties = getPropertiesValue("parserlog.properties");
		
		if(logProperties == null || isMissingLogFile){
			// missing the path of log file in the properties file, so stop the process
			logger.error("logProperties is null or LOGPATH is missing in parserlog.properties, so stop the process.");
			return;
		}
		
		//trigger the cleanup systemLogDb timer
		startCleanUp();
     
		File fileLog = new File(systemLogFile);

		im = IntegrityMonitor.getInstance(resourceName,logProperties );
		
		startDebugLogParser(fileLog);
		startErrorLogParser(fileLog);
		startAPIRestLogParser(fileLog);	
	
	}	
	
	private static void startDebugLogParser(File fileLog) throws Exception{
		
		if(debuglogFile != null && !debuglogFile.isEmpty()){
			
			// pull the last line number 
			String dataFileName = "debug.log";
			String filesRead = PullLastLineRead(fileLog, dataFileName);  
			if (filesRead!= null){			
				filesRead = filesRead.replaceAll("(\\r\\n|\\n)", "<br />");
				debuglastNumberRead= Integer.parseInt(filesRead.trim());
			}else{
				debuglastNumberRead = 0;
			}	
			
			debugStartFileSize =  countLines(debuglogFile);
			if (debugStartFileSize < debuglastNumberRead ){ 
				logger.error("Filed Rolled: set Last debug number read to 0");
				debuglastNumberRead = 0;
			}
			
			isMissingLogFile = false; 
			Path debugfilePath = Paths.get(debuglogFile);
			File debugfile = new File(debuglogFile);
			debugStartFileSize = debugfile.length();
			
			// start process debug.log file
			Runnable  runnable = new Runnable (){
				boolean isStop = false;				
				public void run(){
					while (!isStop){			             
						if (debugfile.isFile()){
							// log4jlogger must use .info
							Stream<String> lines = null;
							try {
								lines = Files.lines(debugfilePath, Charset.defaultCharset()).onClose(() -> log4jlogger.info("Last-"+dataFileName+"-line-Read:" + debuglastNumberRead)).skip(debuglastNumberRead);
								lines.forEachOrdered(line -> process(line, type, LOGTYPE.DEBUG));
							} catch (IOException e) {
								logger.error(loggingProcess + dataFileName, e);
								logger.error("break the loop.");
								isStop = true;
							}finally{
								if(lines != null){
									lines.close();
								}
							}
						}
						try {
							Thread.sleep(sleepTimer);
							debugStartFileSize =  countLines(debuglogFile);
						} catch (InterruptedException | IOException e) {
							logger.error(loggingProcess + dataFileName, e);
							logger.error("break the loop.");
							isStop = true;
						}						
						logger.debug("File Line Count of debug.log: " + debugStartFileSize + " value read in: " + debuglastNumberRead);
						if (debugStartFileSize < debuglastNumberRead ){
							logger.debug("Failed Rolled: set Last number read to 0");
							debuglastNumberRead = 0;
						}
					}	
				}
			};
			
			Thread thread = new Thread(runnable);
			thread.start();
			
		} 	
	}

	private static void startErrorLogParser(File fileLog) throws Exception{
		
		if(errorlogFile != null && !errorlogFile.isEmpty()){
			
			// pull the last line number 
			String dataFileName = "error.log";
			String filesRead = PullLastLineRead(fileLog, dataFileName);  
			if (filesRead!= null){			
				filesRead = filesRead.replaceAll("(\\r\\n|\\n)", "<br />");
				errorlastNumberRead= Integer.parseInt(filesRead.trim());
			}else{
				errorlastNumberRead = 0;
			}	
			
			errorStartFileSize =  countLines(errorlogFile);
			if (errorStartFileSize < errorlastNumberRead ){
				logger.error("Filed Rolled: set Last error number read to 0");
				errorlastNumberRead = 0;
			}
			
			isMissingLogFile = false;			
			Path errorfilePath = Paths.get(errorlogFile);
			File errorfile = new File(errorlogFile);
			errorStartFileSize = errorfile.length();
			// start process error.log file
			Runnable  runnable = new Runnable (){
				boolean isStop = false;
				public void run(){
                    
					while (!isStop){
						if (errorfile.isFile()){
							// log4jlogger must use .info
							Stream<String> lines = null;
							try{
								lines = Files.lines(errorfilePath, Charset.defaultCharset()).onClose(() -> log4jlogger.info("Last-"+dataFileName+"-line-Read:" + errorlastNumberRead)).skip(errorlastNumberRead);
								lines.forEachOrdered(line -> process(line, type, LOGTYPE.ERROR));
							} catch (IOException e) {
								logger.error(loggingProcess + dataFileName, e);
								logger.error("break the loop.");
								isStop = true;
							}finally{
								if(lines != null){
									lines.close();
								}
							}
						}
						try {
							Thread.sleep(sleepTimer);
							errorStartFileSize =  countLines(errorlogFile);
						} catch (InterruptedException | IOException e) {
							logger.error(loggingProcess + dataFileName, e);
							logger.error("break the loop.");
							isStop = true;
						}
						
						logger.debug("File Line Count of error.log: " + errorStartFileSize + " value read in: " + errorlastNumberRead);
						if (errorStartFileSize < errorlastNumberRead ){
							logger.debug("Failed Rolled: set Last error number read to 0");
							errorlastNumberRead = 0;
						}
					}	
				}
			};
			
			Thread thread = new Thread(runnable);
			thread.start();
		}		
	}
	
	private static void startAPIRestLogParser(File fileLog) throws Exception{
		
		if(logFile != null && !logFile.isEmpty()){
			
			// pull the last line number 
			String dataFileName = type.toLowerCase()+"-rest.log";
			String filesRead = PullLastLineRead(fileLog, dataFileName);  
			if (filesRead!= null){			
				filesRead = filesRead.replaceAll("(\\r\\n|\\n)", "<br />");
				lastNumberRead= Integer.parseInt(filesRead.trim());
			}else{
				lastNumberRead = 0;
			}			
			startFileSize =  countLines(logFile);
			if (startFileSize < lastNumberRead ){
				logger.error("Filed Rolled: set Last number read to 0");
				lastNumberRead = 0;
			}
			
			isMissingLogFile = false;
	        Path filePath = Paths.get(logFile);
	        File file = new File(logFile);		
			startFileSize = file.length();
			// start process pap/pdp-rest.log file
			Runnable  runnable = new Runnable () {
				boolean isStop = false;
				public void run(){
					while (!isStop){		
						
						if (file.isFile()){
							// log4jlogger must use .info
							Stream<String> lines = null;;
							try {
								lines = Files.lines(filePath, Charset.defaultCharset()).onClose(() -> log4jlogger.info("Last-"+dataFileName+"-line-Read:" + lastNumberRead)).skip(lastNumberRead);
								lines.forEachOrdered(line -> process(line, type, LOGTYPE.INFO));
							} catch (IOException e) {
								logger.error(loggingProcess + dataFileName, e);
								logger.error("break the loop.");
								isStop = true;
							}finally{
								if(lines != null){
									lines.close();
								}
							}	
						}
						try {
							Thread.sleep(sleepTimer);
							startFileSize =  countLines(logFile);
						} catch (InterruptedException | IOException e) {
							logger.error(loggingProcess + dataFileName, e);
							logger.error("break the loop.");
							isStop = true;
						}
						
						logger.debug("File Line Count of " + dataFileName+": " + startFileSize + " value read in: " + lastNumberRead);
						if (startFileSize < lastNumberRead ){
							logger.debug("Failed Rolled: set Last number read to 0");
							lastNumberRead = 0;
						}
					}	
				}
			};
				
			Thread thread = new Thread(runnable);
			thread.start();
		}		
	}
	
	public static int countLines(String filename) throws IOException {
	    LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
	    int cnt = 0;
	    String line= null;
	    while ((line = reader.readLine()) != null) {
	    	logger.debug("Reading the Logs"+line);
	    }
	    cnt = reader.getLineNumber();
	    logger.info("Line number:"+cnt);
	    reader.close();
	    return cnt;
	}	
	
	public static String PullLastLineRead(File file, String dataFileName) throws IOException {
		if(!file.exists()){
			file.createNewFile();
			return null;
		}
		randomAccessFile = new RandomAccessFile(file, "r");
        StringBuilder builder = new StringBuilder();
        long length = file.length();
        logger.debug("dataFileName: " +dataFileName);
        if(length > 0){
	        length--;	        
	        randomAccessFile.seek(length);
	        for(long seek = length; seek >= 0; --seek){
	            randomAccessFile.seek(seek);
	            char c = (char)randomAccessFile.read();
	            builder.append(c);
	            if(c == '\n'){
	                builder = builder.reverse();
	                logger.debug("builder.toString(): " +builder.toString());
	                if (builder.toString().contains("Last-"+dataFileName+"-line-Read:")){
	            		String[] parseString = builder.toString().split("Last-"+dataFileName+"-line-Read:");
	            		String returnValue = parseString[1].replace("\r", "");
	            		return returnValue.trim();
	            	}
	                builder = new StringBuilder();
	             }	
	        }
        }
        
		return null;
	}

	public static LogEntryObject pullOutLogValues(String line, String type){
		Date date;
		LogEntryObject logEntry = new LogEntryObject();
		String description = "";
		logEntry.setSystemType(type);
		logEntry.setSystem(system);
		String INFO_1 = "||INFO||";
		String INFO_2 = "INFO:";
		String INFO_3 = "INFO";
		String ERROR_1 = "||ERROR||";
		String ERROR_2 = "ERROR:";

		if(line.contains(INFO_1) || line.contains(ERROR_1) || line.contains(INFO_2) || line.contains(ERROR_2)){
			String[] splitString = null;
			if(line.contains(INFO_1) || line.contains(ERROR_1)){
				splitString = line.split("[||]");
			}else if(line.contains(INFO_2)){
				splitString = line.split(INFO_2);
			}else{
				splitString = line.split(ERROR_2);
			}
			String dateString = splitString[0].substring(0, 19);
			logEntry.setDescription(splitString[splitString.length-1]);	

			//parse out date
			date = parseDate(dateString.replace("T", " "), "yyyy-MM-dd HH:mm:ss", false);
			logEntry.setDate(date);
			
			logEntry.setRemote(parseRemoteSystem(line));
			if (line.contains(INFO_2) || line.contains(INFO_1)){
				logEntry.setLogType(LOGTYPE.INFO);
			}else{
				logEntry.setLogType(LOGTYPE.ERROR);
			}
           // from PDP/PAP rest log file below
		}else if (line.contains(INFO_3) && line.contains(")-")){ 
			//parse out description
			logEntry.setDescription(line.substring(line.indexOf(")-")+3));

			date = parseDate(line, "yy_MM_dd_HH_mm_ss", true);
			logEntry.setDate(date);
	
			logEntry.setRemote(parseRemoteSystem(line));
			logEntry.setLogType(LOGTYPE.INFO);
		} else if (line.contains(INFO_3) && line.contains("--- [")){
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

	public static void process(String line, String type, LOGTYPE logFile)  {
		
		LogEntryObject returnLogValue = null;
		if (im!=null){
			try {
				im.startTransaction();
			} catch (AdministrativeStateException e) {
				logger.error("Error received" + e);				
			} catch (StandbyStatusException ex) {
				logger.error("Error received" + ex);
			}
		}
		returnLogValue = pullOutLogValues(line, type);
		
		if(logFile.equals(LOGTYPE.DEBUG)){
		   debuglastNumberRead++;
		}else if(logFile.equals(LOGTYPE.ERROR)){
		   errorlastNumberRead++;
		}else if(logFile.equals(LOGTYPE.INFO)){
		   lastNumberRead++;
		}
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
			logger.debug("DBAccesss : sdate : " + sdate);
		}else{
			logger.debug("DBAccesss : sdate is null");
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
	
	public static String[] getPaths(String logPath){	
		
		if(logPath != null && !logPath.isEmpty()){
			if(logPath.contains(";")){
		       return logPath.split(";");
			}else{
				 String[] oneFile = new String[1];
				 oneFile[0] = logPath;
				 return oneFile;
			}
		}
		
	    return null;	
	}
	
	public static Properties getPropertiesValue(String fileName) {
		Properties config = new Properties();
		Path file = Paths.get(fileName);
		if (Files.notExists(file)) {
			logger.debug("File doesn't exist in the specified Path "	+ file.toString());
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
					String logFiles = config.getProperty("LOGPATH");
					String cleanupInterval= config.getProperty("CHECK_INTERVAL");
					String timeFrame = config.getProperty("TIME_FRAME");
					
					if(cleanupInterval != null && !cleanupInterval.isEmpty()){
						try{
							int intCheckInterval = Integer.parseInt(cleanupInterval);
							if(intCheckInterval > 300000) {//must be longer than 5 minutes
								CHECK_INTERVAL = intCheckInterval;
							}
						}catch(Exception e){
						   logger.error("Error getPropertiesValue on CHECK_INTERVAL", e);
						   logger.debug("Error getPropertiesValue on CHECK_INTERVAL, so use its default value:" + CHECK_INTERVAL + " milliseconds");
						}
					}else{
						 logger.debug("No value defined for CHECK_INTERVAL in parserlog.properties, so use its default value:" + CHECK_INTERVAL + " milliseconds");
					}
					
					if(timeFrame != null && !timeFrame.isEmpty()){
						try{
							int intTimeFrame = Integer.parseInt(timeFrame);
							if(intTimeFrame > 0){
								TIME_FRAME = intTimeFrame;
							}
						}catch(Exception e){
						   logger.error("Error getPropertiesValue on TIME_FRAME", e);
						   logger.debug("Error getPropertiesValue on TIME_FRAME, so use its default value:" + TIME_FRAME + " days");
						}
					}else{
						 logger.debug("No value defined for TIME_FRAME in parserlog.properties, so use its default value:" + TIME_FRAME + " days");
					}
					
					if(logFiles == null || logFiles.isEmpty()){
						isMissingLogFile = true;
						return null;
					}
					
					String[] splitString = getPaths(logFiles);
					
					if(splitString != null){
                        for(int i=0;  i < splitString.length; i++){
                        	
                        	if(splitString[i].contains("debug")){
        						// get path of debug.log file
        						debuglogFile = splitString[i];
        						if(debuglogFile != null && !debuglogFile.isEmpty()){
        							debuglogFile = debuglogFile.trim();
        						}
                        	}else if(splitString[i].contains("error")){
        						// get path of error.log file
        						errorlogFile = splitString[i];
        						if(errorlogFile != null && !errorlogFile.isEmpty()){
        							errorlogFile = errorlogFile.trim();
        						}
                        	}else {
        						// get path of default file
                        		logFile = splitString[i];
        						if(logFile != null && !logFile.isEmpty()){
        							logFile = logFile.trim();
        						}
                        	}
                        }
					}else{	
						
						debuglogFile = null;
						errorlogFile = null;
						logFile = null;
					}
					
					JDBC_URL = config.getProperty("JDBC_URL").replace("'", "");
					JDBC_USER = config.getProperty("JDBC_USER");
					JDBC_DRIVER =  config.getProperty("JDBC_DRIVER");
					JDBC_PASSWORD = config.getProperty("JDBC_PASSWORD");
					return config;

				} catch (IOException e) {					
					logger.error("Error porcessing Config file will be unable to create Health Check" + e);
				}
				
			}
		}
		return null;
	}
	
	public static Connection getDbConnection(){
		return DBConnection(JDBC_DRIVER, JDBC_URL, JDBC_USER,JDBC_PASSWORD);
	}
	private static void startCleanUp(){
		Connection conn = DBConnection(JDBC_DRIVER, JDBC_URL, JDBC_USER,JDBC_PASSWORD);
		CleanUpSystemLogDB cleanUp = new CleanUpSystemLogDB(conn, TIME_FRAME); 
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(cleanUp, TIMER_DELAY_TIME, CHECK_INTERVAL);
		logger.info("startCleanUp begins! : " + new Date());
	}
}