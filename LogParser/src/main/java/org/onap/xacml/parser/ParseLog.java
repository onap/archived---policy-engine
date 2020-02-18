/*-
 * ============LICENSE_START=======================================================
 * LogParser
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.im.IntegrityMonitorException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.utils.PeCryptoUtils;
import org.onap.xacml.parser.LogEntryObject.LogType;

/**
 * Parse log files and store the information in a H2 database.
 *
 *
 */
public class ParseLog {

    // only logging last line of each log file processed to the log4j log file defined by property - PARSERLOGPATH
    private static final Logger log4jlogger = Logger.getLogger(ParseLog.class.getName());
    private static final String PROP_AES_KEY = "org.onap.policy.encryption.aes.key";

    // processing logging
    private static org.onap.policy.common.logging.flexlogger.Logger logger =
            FlexLogger.getLogger(ParseLog.class.getName());

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
    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static String jdbcDriver;
    private static int maxLength = 255; // Max length that is allowed in the DB table
    private static String resourceName;
    private static long sleepTimer = 50000;
    static IntegrityMonitor im;
    private static boolean isMissingLogFile;
    // Default:Timer initial delay and the delay between in milliseconds before task is to be execute
    private static final int TIMER_DELAY_TIME = 1000;
    // Default:Timer scheduleAtFixedRate period - time in milliseconds between successive task executions
    private static int checkInterval = 86400000; // run this clean up once a day
    private static String loggingProcess = "Error processing line in ";
    private static int defaultTimeFrame = 5;
    private static String message = " value read in: ";
    private static String lineFormat = "(\\r\\n|\\n)";
    private static String lineRead = "-line-Read:";
    private static String br = "<br />";
    private static String last = "Last-";
    private static String breakLoop = "break the loop.";
    private static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * The main method to start log parsing.
     *
     * @param args the arguments
     * @throws Exception if an error occurs
     */
    public static void main(final String[] args) throws Exception {

        final Properties logProperties = getPropertiesValue("parserlog.properties");

        if (logProperties == null || isMissingLogFile) {
            // missing the path of log file in the properties file, so stop the process
            logger.error("logProperties is null or LOGPATH is missing in parserlog.properties, so stop the process.");
            return;
        }

        // trigger the cleanup systemLogDb timer
        startCleanUp();

        final File fileLog = new File(systemLogFile);

        im = IntegrityMonitor.getInstance(resourceName, logProperties);

        startDebugLogParser(fileLog);
        startErrorLogParser(fileLog);
        startApiRestLogParser(fileLog);

    }

    private static boolean processLine(final Path debugfilePath, final String dataFileName, final int lastNmRead,
            final LogType logType) {
        // log4jlogger must use .info
        try (Stream<String> lines = Files.lines(debugfilePath, Charset.defaultCharset())
                .onClose(() -> log4jlogger.info(last + dataFileName + lineRead + lastNmRead)).skip(lastNmRead)) {
            lines.forEachOrdered(line -> process(line, type, logType));
        } catch (final IOException e) {
            logger.error(loggingProcess + dataFileName, e);
            logger.error(breakLoop);
            return true;
        }
        return false;
    }

    private static void processDebugLogParser(final File debugfile, final Path debugfilePath,
            final String dataFileName) {

        final Runnable runnable = new Runnable() {
            boolean isStop = false;

            @Override
            public void run() {
                while (!isStop) {
                    if (debugfile.isFile()) {
                        isStop = processLine(debugfilePath, dataFileName, debuglastNumberRead, LogType.DEBUG);
                    }
                    try {
                        Thread.sleep(sleepTimer);
                        debugStartFileSize = countLines(debuglogFile);
                    } catch (final Exception e) {
                        logger.error(loggingProcess + dataFileName, e);
                        logger.error(breakLoop);
                        isStop = true;
                    }
                    logger.debug("File Line Count of debug.log: " + debugStartFileSize + message + debuglastNumberRead);
                    if (debugStartFileSize < debuglastNumberRead) {
                        logger.debug("Failed Rolled: set Last number read to 0");
                        debuglastNumberRead = 0;
                    }
                }
            }
        };

        final Thread thread = new Thread(runnable);
        thread.start();
    }

    private static void startDebugLogParser(final File fileLog) throws IOException {

        if (debuglogFile != null && !debuglogFile.isEmpty()) {

            // pull the last line number
            final String dataFileName = "debug.log";
            String filesRead = pullLastLineRead(fileLog, dataFileName);
            if (filesRead != null) {
                filesRead = filesRead.replaceAll(lineFormat, br);
                debuglastNumberRead = Integer.parseInt(filesRead.trim());
            } else {
                debuglastNumberRead = 0;
            }

            debugStartFileSize = countLines(debuglogFile);
            if (debugStartFileSize < debuglastNumberRead) {
                logger.error("Filed Rolled: set Last debug number read to 0");
                debuglastNumberRead = 0;
            }

            isMissingLogFile = false;
            final Path debugfilePath = Paths.get(debuglogFile);
            final File debugfile = new File(debuglogFile);
            debugStartFileSize = debugfile.length();

            // start process debug.log file
            processDebugLogParser(debugfile, debugfilePath, dataFileName);

        }
    }

    private static void processErrorLogParser(final File errorfile, final Path errorfilePath,
            final String dataFileName) {
        final Runnable runnable = new Runnable() {
            boolean isStop = false;

            @Override
            public void run() {

                while (!isStop) {
                    if (errorfile.isFile()) {
                        isStop = processLine(errorfilePath, dataFileName, errorlastNumberRead, LogType.ERROR);
                    }
                    try {
                        Thread.sleep(sleepTimer);
                        errorStartFileSize = countLines(errorlogFile);
                    } catch (final Exception e) {
                        logger.error(loggingProcess + dataFileName, e);
                        logger.error(breakLoop);
                        isStop = true;
                    }

                    logger.debug("File Line Count of error.log: " + errorStartFileSize + message + errorlastNumberRead);
                    if (errorStartFileSize < errorlastNumberRead) {
                        logger.debug("Failed Rolled: set Last error number read to 0");
                        errorlastNumberRead = 0;
                    }
                }
            }
        };

        final Thread thread = new Thread(runnable);
        thread.start();
    }

    private static void startErrorLogParser(final File fileLog) throws IOException {

        if (errorlogFile != null && !errorlogFile.isEmpty()) {

            // pull the last line number
            final String dataFileName = "error.log";
            String filesRead = pullLastLineRead(fileLog, dataFileName);
            if (filesRead != null) {
                filesRead = filesRead.replaceAll(lineFormat, br);
                errorlastNumberRead = Integer.parseInt(filesRead.trim());
            } else {
                errorlastNumberRead = 0;
            }

            errorStartFileSize = countLines(errorlogFile);
            if (errorStartFileSize < errorlastNumberRead) {
                logger.error("Filed Rolled: set Last error number read to 0");
                errorlastNumberRead = 0;
            }

            isMissingLogFile = false;
            final Path errorfilePath = Paths.get(errorlogFile);
            final File errorfile = new File(errorlogFile);
            errorStartFileSize = errorfile.length();
            // start process error.log file
            processErrorLogParser(errorfile, errorfilePath, dataFileName);

        }
    }

    private static void processApiRestLog(final File file, final Path filePath, final String dataFileName) {

        final Runnable runnable = new Runnable() {
            boolean isStop = false;

            @Override
            public void run() {
                while (!isStop) {

                    if (file.isFile()) {
                        isStop = processLine(filePath, dataFileName, lastNumberRead, LogType.INFO);
                    }
                    try {
                        Thread.sleep(sleepTimer);
                        startFileSize = countLines(logFile);
                    } catch (final Exception e) {
                        logger.error(loggingProcess + dataFileName, e);
                        logger.error(breakLoop);
                        isStop = true;
                    }

                    logger.debug(
                            "File Line Count of " + dataFileName + ": " + startFileSize + message + lastNumberRead);
                    if (startFileSize < lastNumberRead) {
                        logger.debug("Failed Rolled: set Last number read to 0");
                        lastNumberRead = 0;
                    }
                }
            }
        };

        final Thread thread = new Thread(runnable);
        thread.start();
    }

    private static void startApiRestLogParser(final File fileLog) throws IOException {

        if (logFile != null && !logFile.isEmpty()) {

            // pull the last line number
            final String dataFileName = type.toLowerCase() + "-rest.log";
            String filesRead = pullLastLineRead(fileLog, dataFileName);
            if (filesRead != null) {
                filesRead = filesRead.replaceAll(lineFormat, br);
                lastNumberRead = Integer.parseInt(filesRead.trim());
            } else {
                lastNumberRead = 0;
            }
            startFileSize = countLines(logFile);
            if (startFileSize < lastNumberRead) {
                logger.error("Filed Rolled: set Last number read to 0");
                lastNumberRead = 0;
            }

            isMissingLogFile = false;
            final Path filePath = Paths.get(logFile);
            final File file = new File(logFile);
            startFileSize = file.length();
            // start process pap/pdp-rest.log file
            processApiRestLog(file, filePath, dataFileName);
        }
    }

    /**
     * Count the number of lines in input file.
     *
     * @param filename the filename
     * @return the lines count
     */
    public static int countLines(final String filename) {
        int cnt = 0;
        try (FileReader freader = new FileReader(filename); LineNumberReader reader = new LineNumberReader(freader)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                logger.debug("Reading the Logs" + line);
            }
            cnt = reader.getLineNumber();
            logger.info("Line number:" + cnt);
            reader.close();
            freader.close();

        } catch (final Exception e) {
            logger.error(e);
        }

        return cnt;
    }

    /**
     * Get the last line read from the input file if exists.
     *
     * @param file the file
     * @param dataFileName the data file name
     * @return last line read or null
     * @throws IOException if any error occurs
     */
    public static String pullLastLineRead(final File file, final String dataFileName) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            return null;
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");) {
            StringBuilder builder = new StringBuilder();
            long length = file.length();
            logger.debug("dataFileName: " + dataFileName);
            if (length == 0) {
                return null;
            }

            length--;
            randomAccessFile.seek(length);
            for (long seek = length; seek >= 0; --seek) {
                randomAccessFile.seek(seek);
                final char c = (char) randomAccessFile.read();
                builder.append(c);
                if (c == '\n') {
                    builder = builder.reverse();
                    logger.debug("builder.toString(): " + builder.toString());
                    if (builder.toString().contains(last + dataFileName + lineRead)) {
                        final String[] parseString = builder.toString().split(last + dataFileName + lineRead);
                        final String returnValue = parseString[1].replace("\r", "");
                        return returnValue.trim();
                    }
                    builder = new StringBuilder();
                }
            }

        }
        return null;
    }

    private static LogEntryObject getDebugOutLogValue(final String line, final String type) {

        Date date;
        final LogEntryObject logEntry = new LogEntryObject();
        logEntry.setSystemType(type);
        logEntry.setSystem(system);
        final String info1 = "||INFO||";
        final String info2 = "INFO:";
        final String error1 = "||ERROR||";
        final String error2 = "ERROR:";

        if (line.contains(info1) || line.contains(error1) || line.contains(info2) || line.contains(error2)) {
            String[] splitString = null;
            if (line.contains(info1) || line.contains(error1)) {
                splitString = line.split("[||]");
            } else if (line.contains(info2)) {
                splitString = line.split(info2);
            } else {
                splitString = line.split(error2);
            }
            final String dateString = splitString[0].substring(0, 19);
            logEntry.setDescription(splitString[splitString.length - 1]);

            // parse out date
            date = parseDate(dateString.replace("T", " "), dateFormat, false);
            logEntry.setDate(date);

            logEntry.setRemote(parseRemoteSystem(line));
            if (line.contains(info2) || line.contains(info1)) {
                logEntry.setLogType(LogType.INFO);
            } else {
                logEntry.setLogType(LogType.ERROR);
            }

            return logEntry;
        }

        return null;
    }

    private static LogEntryObject getRestApiOutLogValue(final String line, final String type) {
        Date date;
        final LogEntryObject logEntry = new LogEntryObject();
        logEntry.setSystemType(type);
        logEntry.setSystem(system);
        final String info3 = "INFO";

        // from PDP/PAP rest log file below
        if (line.contains(info3) && line.contains(")-")) {
            // parse out description
            logEntry.setDescription(line.substring(line.indexOf(")-") + 3));

            date = parseDate(line, dateFormat, true);
            logEntry.setDate(date);

            logEntry.setRemote(parseRemoteSystem(line));
            logEntry.setLogType(LogType.INFO);

            return logEntry;
        }

        return null;
    }

    private static LogEntryObject getInfoOutLogValue(final String line, final String type) {
        Date date;
        final LogEntryObject logEntry = new LogEntryObject();
        logEntry.setSystemType(type);
        logEntry.setSystem(system);
        final String info3 = "INFO";

        if (line.contains(info3) && line.contains("--- [")) {
            // parse out description
            final String temp = line.substring(line.indexOf("---") + 1);
            final String[] split = temp.split(":");

            logEntry.setDescription(split[1]);

            // parse out date
            date = parseDate(line, dateFormat, false);
            logEntry.setDate(date);

            // remote system
            logEntry.setRemote(parseRemoteSystem(line));
            logEntry.setLogType(LogType.INFO);

            return logEntry;
        }

        return null;

    }

    private static LogEntryObject getSevereOutLogValue(final String line, final String type) {
        Date date;
        final LogEntryObject logEntry = new LogEntryObject();
        logEntry.setSystemType(type);
        logEntry.setSystem(system);
        if (line.contains("SEVERE") && line.contains("[main]")) {
            final String[] splitString = line.split(" ");
            final StringBuilder description = new StringBuilder();
            for (int i = 5; i < splitString.length; i++) {
                description.append(" " + splitString[i]);
            }

            logEntry.setDescription(description.toString());
            // parse out date
            date = parseDate(line, dateFormat, false);
            logEntry.setDate(date);
            logEntry.setLogType(LogType.SEVERE);

            return logEntry;
        }

        if (line.contains("ERROR") && line.contains(")-")) {
            // parse out description
            final StringBuilder description = new StringBuilder();
            description.append(line.substring(line.indexOf(")-") + 3));
            // parse out date
            date = parseDate(line, dateFormat, true);
            logEntry.setDate(date);
            logEntry.setDescription(description.toString());
            // remote system
            logEntry.setRemote(parseRemoteSystem(line));
            logEntry.setLogType(LogType.ERROR);

            return logEntry;
        }

        return null;
    }

    private static LogEntryObject getWarnOutLogValue(final String line, final String type) {
        Date date;
        final LogEntryObject logEntry = new LogEntryObject();
        logEntry.setSystemType(type);
        logEntry.setSystem(system);
        if (line.contains("WARN") && line.contains(")-")) {
            // parse out description

            logEntry.setDescription(line.substring(line.indexOf(")-") + 3));

            // parse out date
            date = parseDate(line, dateFormat, true);
            logEntry.setDate(date);

            // remote system
            logEntry.setRemote(parseRemoteSystem(line));
            logEntry.setLogType(LogType.WARN);

            return logEntry;
        }

        if (line.contains("WARNING") && type == "PyPDP") {
            final String[] splitString = line.split(" ");
            final StringBuilder description = new StringBuilder();

            for (int i = 5; i < splitString.length; i++) {
                description.append(" " + splitString[i]);
            }

            // parse out date
            date = parseDate(line, dateFormat, false);
            logEntry.setDate(date);
            logEntry.setLogType(LogType.WARN);
            logEntry.setDescription(description.toString());
            return logEntry;
        }

        return null;

    }

    /**
     * Get log values based on provided line and type.
     *
     * @param line the line
     * @param type the type
     * @return {@link LogEntryObject}
     */
    public static LogEntryObject pullOutLogValues(final String line, final String type) {

        LogEntryObject logEntry = getDebugOutLogValue(line, type);

        if (logEntry == null) {
            logEntry = getRestApiOutLogValue(line, type);
        }
        if (logEntry == null) {
            logEntry = getInfoOutLogValue(line, type);
        }
        if (logEntry == null) {
            logEntry = getSevereOutLogValue(line, type);
        }
        if (logEntry == null) {
            logEntry = getWarnOutLogValue(line, type);
        }

        return logEntry;
    }

    private static void dbClose(final Connection conn) {
        try {
            conn.close();
        } catch (final SQLException e) {
            logger.error("Error closing DB Connection: " + e);

        }
    }

    /**
     * Process the provided line, type and log file.
     *
     * @param line the line
     * @param type the type
     * @param logFile the log type
     */
    public static void process(final String line, final String type, final LogType logFile) {

        LogEntryObject returnLogValue = null;
        if (im != null) {
            try {
                im.startTransaction();
            } catch (final IntegrityMonitorException e) {
                logger.error("Error received" + e);
            }
        }
        returnLogValue = pullOutLogValues(line, type);

        if (logFile.equals(LogType.DEBUG)) {
            debuglastNumberRead++;
        } else if (logFile.equals(LogType.ERROR)) {
            errorlastNumberRead++;
        } else if (logFile.equals(LogType.INFO)) {
            lastNumberRead++;
        }
        if (returnLogValue != null) {
            writeDb(returnLogValue);
        }
        if (im != null) {
            im.endTransaction();
        }
    }

    private static void writeDb(final LogEntryObject returnLogValue) {

        final Connection conn = dbConnection(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
        dbAccesss(conn, returnLogValue.getSystem(), returnLogValue.getDescription(), returnLogValue.getDate(),
                returnLogValue.getRemote(), returnLogValue.getSystemType(), returnLogValue.getLogType().toString());
        dbClose(conn);
    }

    private static Connection dbConnection(final String driver, final String jdbc, final String user,
            final String pass) {

        try {
            Class.forName(driver);
            return DriverManager.getConnection(jdbc, user, pass);
        } catch (final Exception e) {
            logger.error("Error connecting to DB: " + e);
        }
        return null;
    }

    private static void dbAccesss(final Connection conn, final String system, String description, final Date date,
            final String remote, final String type, final String logType) {

        String sdate = null;
        if (date != null) {
            final Format formatter = new SimpleDateFormat(dateFormat);
            sdate = formatter.format(date);
            logger.debug("DBAccesss : sdate : " + sdate);
        } else {
            logger.debug("DBAccesss : sdate is null");
        }

        // ensure the length of description is less than the maximumm db char length
        if (description.length() > maxLength) {
            description = description.substring(0, maxLength);
        }

        try (PreparedStatement prep =
                conn.prepareStatement("insert into SYSTEMLOGDB values (NULL, ?, ?, ?,  ?,  ?, ?);");) {

            prep.setString(1, system);
            prep.setString(2, description);
            prep.setString(3, remote);
            prep.setString(4, type);
            prep.setString(5, sdate);
            prep.setString(6, logType);

            prep.executeUpdate();
            prep.close();

        } catch (final SQLException e1) {
            logger.error("Error trying to excute SQL Statment: " + e1);
        }
    }

    /**
     * Parse the given date string based on the provided pattern with/without split based on input boolean value.
     *
     * @param dateline the date string
     * @param pattern the provided pattern
     * @param singleSplit the single split boolean value
     * @return the parsed date or null
     */
    public static Date parseDate(final String dateline, final String pattern, final boolean singleSplit) {

        Date returnDate;
        final String[] splitString = dateline.split(" ");
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        if (singleSplit) {
            try {
                returnDate = formatter.parse(dateline);
            } catch (final ParseException e) {
                logger.error("Unable to parse date for line: " + dateline);
                returnDate = null;
            }
        } else {
            final String tmpString = splitString[0] + " " + splitString[1];
            try {
                returnDate = formatter.parse(tmpString);
            } catch (final ParseException e) {
                logger.error("Unable to parse date for line: " + dateline);
                returnDate = null;
            }
        }

        return returnDate;
    }


    /**
     * Get remote system host from given string if exists.
     *
     * @param line the input string
     * @return system host or null
     */
    public static String parseRemoteSystem(final String line) {

        if (line.contains("http") && !(line.contains("www.w3.org"))) {

            final Pattern pattern = Pattern.compile("://(.+?)/");
            final Matcher remote = pattern.matcher(line);
            if (remote.find()) {
                return remote.group(1);
            }
        }
        return null;
    }

    /**
     * Get the log paths provided in input string.
     *
     * @param logPath the string of log paths
     * @return log paths
     */
    public static String[] getPaths(final String logPath) {
        String[] oneFile = null;
        if (logPath != null && !logPath.isEmpty()) {
            if (logPath.contains(";")) {
                return logPath.split(";");
            } else {
                oneFile = new String[1];
                oneFile[0] = logPath;
            }
        }

        return oneFile;
    }

    private static void setCleanUpProperties(final String cleanupInterval, final String timeFrame) {
        if (cleanupInterval != null && !cleanupInterval.isEmpty()) {
            final int intCheckInterval = Integer.parseInt(cleanupInterval);
            if (intCheckInterval > 300000) { // must be longer than 5 minutes
                checkInterval = intCheckInterval;
            }
        } else {
            logger.debug("No value defined for CHECK_INTERVAL in parserlog.properties, so use its default value:"
                    + checkInterval + " milliseconds");
        }

        if (timeFrame != null && !timeFrame.trim().isEmpty()) {
            int intTimeFrame = defaultTimeFrame;
            try {
                intTimeFrame = Integer.parseInt(timeFrame);
            } catch (final NumberFormatException e) {
                logger.debug("Improper value defined for TIME_FRAME in parserlog.properties, so use its default value:"
                        + defaultTimeFrame + " days");
            }
            if (intTimeFrame > 0) {
                defaultTimeFrame = intTimeFrame;
            }
        } else {
            logger.debug("No value defined for TIME_FRAME in parserlog.properties, so use its default value:"
                    + defaultTimeFrame + " days");
        }
    }

    private static void setDebuglogFile(final String fileName) {
        debuglogFile = fileName;
        if (debuglogFile != null && !debuglogFile.isEmpty()) {
            debuglogFile = debuglogFile.trim();
        } else {
            debuglogFile = null;
        }
    }

    private static void setErrorlogFile(final String fileName) {
        errorlogFile = fileName;
        if (errorlogFile != null && !errorlogFile.isEmpty()) {
            errorlogFile = errorlogFile.trim();
        } else {
            errorlogFile = null;
        }
    }

    private static void setLogFileProperties(final String[] splitString) {
        if (splitString == null) {
            return;
        }

        for (int i = 0; i < splitString.length; i++) {

            if (splitString[i].contains("debug")) {
                // get path of debug.log file
                setDebuglogFile(splitString[i]);
            } else if (splitString[i].contains("error")) {
                // get path of error.log file
                setErrorlogFile(splitString[i]);
            } else {
                // get path of default file
                logFile = splitString[i];
                if (logFile != null && !logFile.isEmpty()) {
                    logFile = logFile.trim();
                } else {
                    logFile = null;
                }
            }
        }
    }

    /**
     * Get all the properties from file with given file name.
     *
     * @param fileName the filename
     * @return properties from file or null
     */
    public static Properties getPropertiesValue(final String fileName) {
        final Properties config = new Properties();
        final Path file = Paths.get(fileName);
        //ensure file exists and it is properties file
        if (!(file.toFile().exists() && file.toString().endsWith(".properties"))) {
            logger.debug("File doesn't exist in the specified Path Or it is not a properties file" + file.toString());
            return null;
        }

        try (InputStream in = new FileInputStream(file.toFile())) {
            config.load(in);

            resourceName = config.getProperty("RESOURCE_NAME");
            system = config.getProperty("SERVER");
            type = config.getProperty("LOGTYPE");
            systemLogFile = config.getProperty("PARSERLOGPATH");
            final String logFiles = config.getProperty("LOGPATH");
            final String cleanupInterval = config.getProperty("CHECK_INTERVAL");
            final String timeFrame = config.getProperty("TIME_FRAME");

            setCleanUpProperties(cleanupInterval, timeFrame);

            if (logFiles == null || logFiles.isEmpty()) {
                isMissingLogFile = true;
                return null;
            }

            final String[] splitString = getPaths(logFiles);

            setLogFileProperties(splitString);

            jdbcUrl = config.getProperty("JDBC_URL").replace("'", "");
            jdbcUser = config.getProperty(getValue("JDBC_USER"));
            jdbcDriver = config.getProperty("JDBC_DRIVER");

            PeCryptoUtils.initAesKey(config.getProperty(PROP_AES_KEY));
            jdbcPassword = PeCryptoUtils.decrypt(config.getProperty(getValue("JDBC_PASSWORD")));
            config.setProperty("javax.persistence.jdbc.password", jdbcPassword);
            return config;

        } catch (final IOException e) {
            logger.error("Error porcessing Config file will be unable to create Health Check" + e);
        } catch (final Exception e) {
            logger.error("Error getPropertiesValue on TIME_FRAME", e);
            logger.debug("Error getPropertiesValue on TIME_FRAME, so use its default value:" + defaultTimeFrame
                    + " days");
        }
        return null;
    }

    private static String getValue(final String value) {
        if (value != null && value.matches("[$][{].*[}]$")) {
            return System.getenv(value.substring(2, value.length() - 1));
        }
        return value;
    }

    public static Connection getDbConnection() {
        return dbConnection(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
    }

    private static void startCleanUp() {
        final Connection conn = dbConnection(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
        final CleanUpSystemLogDb cleanUp = new CleanUpSystemLogDb(conn, defaultTimeFrame);
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(cleanUp, TIMER_DELAY_TIME, checkInterval);
        logger.info("startCleanUp begins! : " + new Date());
    }
}
