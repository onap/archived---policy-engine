/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.onap.policy.common.ia.IntegrityAudit;
import org.onap.policy.common.im.AdministrativeStateException;
import org.onap.policy.common.im.ForwardProgressException;
import org.onap.policy.common.im.IntegrityMonitor;
import org.onap.policy.common.im.IntegrityMonitorException;
import org.onap.policy.common.im.IntegrityMonitorProperties;
import org.onap.policy.common.im.StandbyStatusException;
import org.onap.policy.common.logging.ONAPLoggingContext;
import org.onap.policy.common.logging.ONAPLoggingUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.onap.policy.pap.xacml.rest.handler.APIRequestHandler;
import org.onap.policy.pap.xacml.rest.handler.PushPolicyHandler;
import org.onap.policy.pap.xacml.rest.handler.SavePolicyHandler;
import org.onap.policy.pap.xacml.restAuth.CheckPDP;
import org.onap.policy.rest.XACMLRest;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.rest.dao.PolicyDBException;
import org.onap.policy.utils.CryptoUtils;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.ONAPPapEngineFactory;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.api.pap.PAPPolicyEngine;
import org.onap.policy.xacml.std.pap.StdPDP;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPItemSetChangeNotifier.StdItemSetChangeListener;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.policy.xacml.std.pap.StdPDPStatus;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

/**
 * Servlet implementation class XacmlPapServlet
 */
@WebServlet(
        description = "Implements the XACML PAP RESTful API.",
        urlPatterns = { "/" },
        loadOnStartup=1,
        initParams = {
            @WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.pap.properties", description = "The location of the properties file holding configuration information.")
        })
public class XACMLPapServlet extends HttpServlet implements StdItemSetChangeListener, Runnable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER	= FlexLogger.getLogger(XACMLPapServlet.class);
    // audit (transaction) LOGGER
    private static final Logger auditLogger = FlexLogger.getLogger("auditLogger");
    //Persistence Unit for JPA
    private static final String PERSISTENCE_UNIT = "XACML-PAP-REST";
    private static final String AUDIT_PAP_PERSISTENCE_UNIT = "auditPapPU";
    // Client Headers.
    private static final String ENVIRONMENT_HEADER = "Environment";
    private static final String ADD_GROUP_ERROR = "addGroupError";
    private static final String PERSISTENCE_JDBC_PWD = "javax.persistence.jdbc.password";

    private static final String REGEX = "[0-9a-zA-Z._ ]*";

    /*
     * List of Admin Console URLs.
     * Used to send notifications when configuration changes.
     *
     * The CopyOnWriteArrayList *should* protect from concurrency errors.
     * This list is seldom changed but often read, so the costs of this approach make sense.
     */
    private static final CopyOnWriteArrayList<String> adminConsoleURLStringList = new CopyOnWriteArrayList<>();

    private static String configHome;
    private static String actionHome;
    /*
     * This PAP instance's own URL.
     * Need this when creating URLs to send to the PDPs so they can GET the Policy files from this process.
     */
    private static String papURL = null;
    // The heartbeat thread.
    private static Heartbeat heartbeat = null;
    private static Thread heartbeatThread = null;
    //The entity manager factory for JPA access
    private static EntityManagerFactory emf;
    private static PolicyDBDao policyDBDao;
    /*
     * papEngine - This is our engine workhorse that manages the PDP Groups and Nodes.
     */
    private static PAPPolicyEngine papEngine = null;
    /*
     * These are the parameters needed for DB access from the PAP
     */
    private static int papIntegrityAuditPeriodSeconds = -1;
    private static String papDbDriver = null;
    private static String papDbUrl = null;
    private static String papDbUser = null;
    private static String papDbPassword = null;
    private static String papResourceName = null;
    private static String[] papDependencyGroupsFlatArray = null;
    private static String environment = null;
    private static String pdpFile = null;

    private transient IntegrityMonitor im;
    private transient IntegrityAudit ia;

    //MicroService Model Properties
    private static String msOnapName;
    private static String msPolicyName;
    /*
     * This thread may be invoked upon startup to initiate sending PDP policy/pip configuration when
     * this servlet starts. Its configurable by the admin.
     */
    private static transient Thread initiateThread = null;
    private transient ONAPLoggingContext baseLoggingContext = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public XACMLPapServlet() {
        super();
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        try {
            // Logging
            baseLoggingContext = new ONAPLoggingContext();
            // fixed data that will be the same in all logging output goes here
            try {
                String hostname = InetAddress.getLocalHost().getCanonicalHostName();
                baseLoggingContext.setServer(hostname);
            } catch (UnknownHostException e) {
                LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get hostname for logging", e);
            }
            // Initialize
            XACMLRest.xacmlInit(config);
            // Load the properties
            XACMLRest.loadXacmlProperties(null, null);
            /*
             * Retrieve the property values
             */
            setCommonProperties();
            String papSiteName = XACMLProperties.getProperty(XACMLRestProperties.PAP_SITE_NAME);
            if(papSiteName == null){
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papSiteName property entry");
                throw new PAPException("papSiteName is null");
            }
            String papNodeType = XACMLProperties.getProperty(XACMLRestProperties.PAP_NODE_TYPE);
            if(papNodeType == null){
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papNodeType property entry");
                throw new PAPException("papNodeType is null");
            }
            //Integer will throw an exception of anything is missing or unrecognized
            int papTransWait = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_WAIT));
            int papTransTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT));
            int papAuditTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_AUDIT_TIMEOUT));
            //Boolean will default to false if anything is missing or unrecognized
            boolean papAuditFlag = Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_RUN_AUDIT_FLAG));
            boolean papFileSystemAudit = Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_AUDIT_FLAG));
            String papDependencyGroups = XACMLProperties.getProperty(XACMLRestProperties.PAP_DEPENDENCY_GROUPS);
            if(papDependencyGroups == null){
                throw new PAPException("papDependencyGroups is null");
            }
            setPAPDependencyGroups(papDependencyGroups);
            //Integer will throw an exception of anything is missing or unrecognized
            int fpMonitorInterval = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.FP_MONITOR_INTERVAL));
            int failedCounterThreshold = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.FAILED_COUNTER_THRESHOLD));
            int testTransInterval = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.TEST_TRANS_INTERVAL));
            int writeFpcInterval = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.WRITE_FPC_INTERVAL));
            LOGGER.debug("\n\n\n**************************************"
                    + "\n*************************************"
                    + "\n"
                    + "\n   papDbDriver = " + papDbDriver
                    + "\n   papDbUrl = " + papDbUrl
                    + "\n   papDbUser = " + papDbUser
                    + "\n   papTransWait = " + papTransWait
                    + "\n   papTransTimeout = " + papTransTimeout
                    + "\n   papAuditTimeout = " + papAuditTimeout
                    + "\n   papAuditFlag = " + papAuditFlag
                    + "\n   papFileSystemAudit = " + papFileSystemAudit
                    + "\n	papResourceName = " + papResourceName
                    + "\n	fpMonitorInterval = " + fpMonitorInterval
                    + "\n	failedCounterThreshold = " + failedCounterThreshold
                    + "\n	testTransInterval = " + testTransInterval
                    + "\n	writeFpcInterval = " + writeFpcInterval
                    + "\n	papSiteName = " + papSiteName
                    + "\n	papNodeType = " + papNodeType
                    + "\n	papDependencyGroupsList = " + papDependencyGroups
                    + "\n   papIntegrityAuditPeriodSeconds = " + papIntegrityAuditPeriodSeconds
                    + "\n\n*************************************"
                    + "\n**************************************");
            // Pull custom persistence settings
            Properties properties;
            try {
                properties = XACMLProperties.getProperties();
                LOGGER.debug("\n\n\n**************************************"
                        + "\n**************************************"
                        + "\n\n"
                        + "properties = " + properties
                        + "\n\n**************************************");
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPapServlet", " Error loading properties with: "
                        + "XACMLProperties.getProperties()");
                throw new ServletException(e.getMessage(), e.getCause());
            }
            // Create an IntegrityMonitor
            if(properties.getProperty(PERSISTENCE_JDBC_PWD) != null ){
                properties.setProperty(PERSISTENCE_JDBC_PWD, CryptoUtils.decryptTxtNoExStr(properties.getProperty(PERSISTENCE_JDBC_PWD, "")));
            }
            im = IntegrityMonitor.getInstance(papResourceName,properties);
            // Create an IntegrityAudit
            ia = new IntegrityAudit(papResourceName, AUDIT_PAP_PERSISTENCE_UNIT, properties);
            ia.startAuditThread();
            // Create the entity manager factory
            setEMF(properties);
            // we are about to call the PDPs and give them their configuration.
            // To do that we need to have the URL of this PAP so we can construct the Policy file URLs
            setPAPURL(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL));
            //Create the policyDBDao
            setPolicyDBDao();
            // Load our PAP engine, first create a factory
            ONAPPapEngineFactory factory = ONAPPapEngineFactory.newInstance(XACMLProperties.getProperty(XACMLProperties.PROP_PAP_PAPENGINEFACTORY));
            // The factory knows how to go about creating a PAP Engine
            setPAPEngine((PAPPolicyEngine) factory.newEngine());
            PolicyDBDaoTransaction addNewGroup = null;
            if (((org.onap.policy.xacml.std.pap.StdEngine) papEngine).wasDefaultGroupJustAdded) {
                try {
                    addNewGroup = policyDBDao.getNewTransaction();
                    OnapPDPGroup group = papEngine.getDefaultGroup();
                    addNewGroup.createGroup(group.getId(), group.getName(), group.getDescription(),
                            "automaticallyAdded");
                    addNewGroup.commitTransaction();
                    addNewGroup = policyDBDao.getNewTransaction();
                    addNewGroup.changeDefaultGroup(group, "automaticallyAdded");
                    addNewGroup.commitTransaction();
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet",
                            " Error creating new default group in the database");
                    if (addNewGroup != null) {
                        addNewGroup.rollbackTransaction();
                    }
                }
            }
            policyDBDao.setPapEngine((PAPPolicyEngine) XACMLPapServlet.papEngine);
            if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_RUN_AUDIT_FLAG))){
                /*
                 * Auditing the local File System groups to be in sync with the Database
                 */

                //get an AuditTransaction to lock out all other transactions
                PolicyDBDaoTransaction auditTrans = policyDBDao.getNewAuditTransaction();

                LOGGER.info("PapServlet: calling auditLocalFileSystem for PDP group audit");
                LOGGER.info("PapServlet: old group is " + papEngine.getDefaultGroup().toString());
                //get the current filesystem group and update from the database if needed
                StdPDPGroup group = (StdPDPGroup) papEngine.getDefaultGroup();
                StdPDPGroup updatedGroup = policyDBDao.auditLocalFileSystem(group);
                if(updatedGroup!=null) {
                    papEngine.updateGroup(updatedGroup);
                }    
                LOGGER.info("PapServlet:  updated group is " + papEngine.getDefaultGroup().toString());
                
                //release the transaction lock
                auditTrans.close();
            }

            // Sanity check for URL.
            if (XACMLPapServlet.papURL == null) {
                throw new PAPException("The property " + XACMLRestProperties.PROP_PAP_URL + " is not valid: " + XACMLPapServlet.papURL);
            }
            // Configurable - have the PAP servlet initiate sending the latest PDP policy/pip configuration
            // to all its known PDP nodes.
            if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_INITIATE_PDP_CONFIG))) {
                startInitiateThreadService(new Thread(this));
            }
            // After startup, the PAP does Heartbeat's to each of the PDPs periodically
            startHeartBeatService(new Heartbeat((PAPPolicyEngine) XACMLPapServlet.papEngine));

        } catch (FactoryException | PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to create engine");
            throw new ServletException (XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP not initialized; error: "+e);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to create engine - unexpected error");
            throw new ServletException (XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP not initialized; unexpected error: "+e);
        }
    }

    private static void startInitiateThreadService(Thread thread) {
        initiateThread = thread;
        initiateThread.start();
    }

    private static void mapperWriteValue(ObjectMapper mapper,  HttpServletResponse response, Object value) {
        try {
            mapper.writeValue(response.getOutputStream(), value);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private static void startHeartBeatService(Heartbeat heartbeat) {
        XACMLPapServlet.heartbeat = heartbeat;
        XACMLPapServlet.heartbeatThread = new Thread(XACMLPapServlet.heartbeat);
        XACMLPapServlet.heartbeatThread.start();
    }

    private static void setPolicyDBDao() throws ServletException {
        try {
            policyDBDao = PolicyDBDao.getPolicyDBDaoInstance(getEmf());
        } catch (Exception e) {
            throw new ServletException("Unable to Create Policy DBDao Instance",e);
        }
    }

    private static void setEMF(Properties properties) throws ServletException {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
        if (emf == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Error creating entity manager factory with persistence unit: "
                    + PERSISTENCE_UNIT);
            throw new ServletException("Unable to create Entity Manager Factory");
        }
    }

    private static void setPAPURL(String papURL) {
        XACMLPapServlet.papURL = papURL;
    }

    private static void setPAPEngine(PAPPolicyEngine newEngine) {
        XACMLPapServlet.papEngine = newEngine;
    }

    private static void setPAPDependencyGroups(String papDependencyGroups) throws PAPException {
        try{
            //Now we have flattened the array into a simple comma-separated list
            papDependencyGroupsFlatArray = papDependencyGroups.split("[;,]");
            //clean up the entries
            for (int i = 0 ; i < papDependencyGroupsFlatArray.length ; i ++){
                papDependencyGroupsFlatArray[i] = papDependencyGroupsFlatArray[i].trim();
            }
            try{
                if(XACMLProperties.getProperty(XACMLRestProperties.PAP_INTEGRITY_AUDIT_PERIOD_SECONDS) != null){
                    papIntegrityAuditPeriodSeconds = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PAP_INTEGRITY_AUDIT_PERIOD_SECONDS).trim());
                }
            }catch(Exception e){
                String msg = "integrity_audit_period_seconds ";
                LOGGER.error("\n\nERROR: " + msg + "Bad property entry: " + e.getMessage() + "\n");
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: " + msg +"Bad property entry");
                throw e;
            }
        }catch(Exception e){
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
            throw new PAPException(e);
        }
    }

    private static void setCommonProperties() throws PAPException {
        setConfigHome();
        setActionHome();
        papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
        if(papDbDriver == null){
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papDbDriver property entry");
            throw new PAPException("papDbDriver is null");
        }
        setPapDbDriver(papDbDriver);
        papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
        if(papDbUrl == null){
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papDbUrl property entry");
            throw new PAPException("papDbUrl is null");
        }
        setPapDbUrl(papDbUrl);
        papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
        if(papDbUser == null){
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papDbUser property entry");
            throw new PAPException("papDbUser is null");
        }
        setPapDbUser(papDbUser);
        papDbPassword = CryptoUtils.decryptTxtNoExStr(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD, ""));
        if(papDbPassword == null){
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papDbPassword property entry");
            throw new PAPException("papDbPassword is null");
        }
        setPapDbPassword(papDbPassword);
        papResourceName = XACMLProperties.getProperty(XACMLRestProperties.PAP_RESOURCE_NAME);
        if(papResourceName == null){
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE,"XACMLPapServlet", " ERROR: Bad papResourceName property entry");
            throw new PAPException("papResourceName is null");
        }
        environment = XACMLProperties.getProperty("ENVIRONMENT", "DEVL");
        //Micro Service Properties
        msOnapName=XACMLProperties.getProperty("xacml.policy.msOnapName");
        setMsOnapName(msOnapName);
        msPolicyName=XACMLProperties.getProperty("xacml.policy.msPolicyName");
        setMsPolicyName(msPolicyName);
        // PDPId File location 
        XACMLPapServlet.pdpFile = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_IDFILE);
        if (XACMLPapServlet.pdpFile == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " The PDP Id Authentication File Property is not valid: "
                + XACMLRestProperties.PROP_PDP_IDFILE);
            throw new PAPException("The PDP Id Authentication File Property :"+ XACMLRestProperties.PROP_PDP_IDFILE+ " is not Valid. ");
        }
    }

    /**
     * Thread used only during PAP startup to initiate change messages to all known PDPs.
     * This must be on a separate thread so that any GET requests from the PDPs during this update can be serviced.
     */
    @Override
    public void run() {
        // send the current configuration to all the PDPs that we know about
        changed();
    }

    /**
     * @see Servlet#destroy()
     *
     * Depending on how this servlet is run, we may or may not care about cleaning up the resources.
     * For now we assume that we do care.
     */
    @Override
    public void destroy() {
        // Make sure our threads are destroyed
        if (XACMLPapServlet.heartbeatThread != null) {
            // stop the heartbeat
            try {
                if (XACMLPapServlet.heartbeat != null) {
                    XACMLPapServlet.heartbeat.terminate();
                }
                XACMLPapServlet.heartbeatThread.interrupt();
                XACMLPapServlet.heartbeatThread.join();
            } catch (InterruptedException e) {
                XACMLPapServlet.heartbeatThread.interrupt();
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Error stopping heartbeat");
            }
        }
        if (initiateThread != null) {
            try {
                initiateThread.interrupt();
                initiateThread.join();
            } catch (InterruptedException e) {
                initiateThread.interrupt();
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Error stopping thread");
            }
        }
    }

    /**
     * Called by:
     * 	- PDP nodes to register themselves with the PAP, and
     * 	- Admin Console to make changes in the PDP Groups.
     *
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        loggingContext.setServiceName("PAP.post");
        if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doPost) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doPost)");
        }
        PolicyDBDaoTransaction pdpTransaction = null;
        loggingContext.metricStarted();
        try {
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
        } catch (AdministrativeStateException ae){
            String message = "POST interface called for PAP " + papResourceName + " but it has an Administrative"
                    + " state of " + im.getStateManager().getAdminState()
                    + "\n Exception Message: " +  PolicyUtils.CATCH_EXCEPTION;
            LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message, ae);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }catch (StandbyStatusException se) {
            String message = "POST interface called for PAP " + papResourceName + " but it has a Standby Status"
                    + " of " + im.getStateManager().getStandbyStatus()
                    + "\n Exception Message: " + se.getMessage();
            LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message, se);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        } catch (IntegrityMonitorException e) {
            String message = "POST interface called for PAP " + papResourceName + " but an exception occurred"
                    + "\n Exception Message: " + e.getMessage();
            LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message, e);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
        try {
            loggingContext.metricStarted();
            XACMLRest.dumpRequest(request);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost dumpRequest");
            // since getParameter reads the content string, explicitly get the content before doing that.
            // Simply getting the inputStream seems to protect it against being consumed by getParameter.
            request.getInputStream();
            String groupId = request.getParameter("groupId");
            String apiflag = request.getParameter("apiflag");
            if(groupId != null) {
                // Is this from the Admin Console or API?
                if(apiflag!=null && apiflag.equalsIgnoreCase("api")) {
                    // this is from the API so we need to check the client credentials before processing the request
                    if(!authorizeRequest(request)){
                        String message = "PEP not Authorized for making this Request!!";
                        PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        setResponseError(response,HttpServletResponse.SC_FORBIDDEN, message);
                        im.endTransaction();
                        return;
                    }
                }
                loggingContext.metricStarted();
                doACPost(request, response, groupId, loggingContext);
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doPost doACPost");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            }
            // Request is from a PDP asking for its config.
            loggingContext.setServiceName("PDP:PAP.register");
            // Get the PDP's ID
            String id = this.getPDPID(request);
            String jmxport = this.getPDPJMX(request);
            LOGGER.info("Request(doPost) from PDP coming up: " + id);
            // Get the PDP Object
            OnapPDP pdp = XACMLPapServlet.papEngine.getPDP(id);
            // Is it known?
            if (pdp == null) {
                LOGGER.info("Unknown PDP: " + id);
                // Check PDP ID
                if(CheckPDP.validateID(id)){
                    pdpTransaction = policyDBDao.getNewTransaction();
                    try {
                        pdpTransaction.addPdpToGroup(id, XACMLPapServlet.papEngine.getDefaultGroup().getId(), id, "Registered on first startup", Integer.parseInt(jmxport), "PDP autoregister");
                        XACMLPapServlet.papEngine.newPDP(id, XACMLPapServlet.papEngine.getDefaultGroup(), id, "Registered on first startup", Integer.parseInt(jmxport));
                    } catch (NullPointerException | PAPException | IllegalArgumentException | IllegalStateException | PersistenceException | PolicyDBException e) {
                        pdpTransaction.rollbackTransaction();
                        String message = "Failed to create new PDP for id: " + id;
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " " + message);
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        im.endTransaction();
                        return;
                    }
                    // get the PDP we just created
                    try{
                        pdp = XACMLPapServlet.papEngine.getPDP(id);
                    }catch(PAPException e){
                        LOGGER.error(e);
                    }
                    if (pdp == null) {
                        if(pdpTransaction != null){
                            pdpTransaction.rollbackTransaction();
                        }
                        String message = "Failed to create new PDP for id: " + id;
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " " + message);
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                        im.endTransaction();
                        return;
                    }
                } else {
                    String message = "PDP is Unauthorized to Connect to PAP: "+ id;
                    loggingContext.transactionEnded();
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, "PDP not Authorized to connect to this PAP. Please contact the PAP Admin for registration.");
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    im.endTransaction();
                    return;
                }
                try{
                    loggingContext.metricStarted();
                    pdpTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doPost commitTransaction");
                } catch(Exception e){
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", "Could not commit transaction to put pdp in the database");
                }
            }
            if (jmxport != null && jmxport != ""){
                try{
                    ((StdPDP) pdp).setJmxPort(Integer.valueOf(jmxport));
                }catch(NumberFormatException e){
                    LOGGER.error(e);
                }
            }
            // Get the PDP's Group
            OnapPDPGroup group =null;
            try{
                group= XACMLPapServlet.papEngine.getPDPGroup((OnapPDP) pdp);
            }catch(PAPException e){
                LOGGER.error(e);
            }
            if (group == null) {
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " PDP not associated with any group, even the default");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, "PDP not associated with any group, even the default");
                im.endTransaction();
                return;
            }
            // Determine what group the PDP node is in and get
            // its policy/pip properties.
            Properties policies = group.getPolicyProperties();
            Properties pipconfig = group.getPipConfigProperties();
            // Get the current policy/pip configuration that the PDP has
            Properties pdpProperties = new Properties();
            try{
                pdpProperties.load(request.getInputStream());
            }catch(IOException e){
                LOGGER.error(e);
            }
            LOGGER.info("PDP Current Properties: " + pdpProperties.toString());
            LOGGER.info("Policies: " + (policies != null ? policies.toString() : "null"));
            LOGGER.info("Pip config: " + (pipconfig != null ? pipconfig.toString() : "null"));
            // Validate the node's properties
            boolean isCurrent = this.isPDPCurrent(policies, pipconfig, pdpProperties);
            // Send back current configuration
            if (isCurrent == false) {
                // Tell the PDP we are sending back the current policies/pip config
                LOGGER.info("PDP configuration NOT current.");
                if (policies != null) {
                    // Put URL's into the properties in case the PDP needs to
                    // retrieve them.
                    this.populatePolicyURL(request.getRequestURL(), policies);
                    // Copy the properties to the output stream
                    try{
                        policies.store(response.getOutputStream(), "");
                    }catch(IOException e){
                        LOGGER.error(e);
                    }
                }
                if (pipconfig != null) {
                    // Copy the properties to the output stream
                    try{
                        pipconfig.store(response.getOutputStream(), "");
                    }catch(IOException e){
                        LOGGER.error(e);
                    }
                }
                // We are good - and we are sending them information
                response.setStatus(HttpServletResponse.SC_OK);
                try{
                    setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
                }catch(PAPException e){
                    LOGGER.error(e);
                }
            } else {
                // Tell them they are good
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                try{
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);
                }catch(PAPException e){
                    LOGGER.error(e);
                }
            }
            // tell the AC that something changed
            loggingContext.metricStarted();
            notifyAC();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost notifyAC");
            loggingContext.transactionEnded();
            auditLogger.info("Success");
            PolicyLogger.audit("Transaction Ended Successfully");
        } catch (PAPException | IOException | NumberFormatException e) {
            if(pdpTransaction != null){
                pdpTransaction.rollbackTransaction();
            }
            LOGGER.debug(XACMLErrorConstants.ERROR_PROCESS_FLOW + "POST exception: " + e, e);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            im.endTransaction();
            return;
        }
        //Catch anything that fell through
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Ended");
        im.endTransaction();
    }

    private void setResponseError(HttpServletResponse response,int responseCode, String message) {
        try {
            if(message != null && !message.isEmpty()){
                   response.sendError(responseCode, message);
            }
        } catch (IOException e) {
            LOGGER.error("Error setting Error response Header ", e);
        }
        return;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        loggingContext.setServiceName("PAP.get");
        if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doGet) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doGet)");
        }
            loggingContext.metricStarted();
            XACMLRest.dumpRequest(request);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doGet dumpRequest");
            String pathInfo = request.getRequestURI();
            LOGGER.info("path info: " + pathInfo);
            if (pathInfo != null){
                //DO NOT do a im.startTransaction for the test request
                if (pathInfo.equals("/pap/test")) {
                    try {
                        testService(loggingContext, response);
                    } catch (IOException e) {
                        LOGGER.debug(e);
                    }
                    return;
                }
            }
            //This im.startTransaction() covers all other Get transactions
            try {
                loggingContext.metricStarted();
                im.startTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doGet im startTransaction");
            } catch (AdministrativeStateException ae){
                String message = "GET interface called for PAP " + papResourceName + " but it has an Administrative"
                        + " state of " + im.getStateManager().getAdminState()
                        + "\n Exception Message: " + ae.getMessage();
                LOGGER.info(message, ae);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                return;
            }catch (StandbyStatusException se) {
                String message = "GET interface called for PAP " + papResourceName + " but it has a Standby Status"
                        + " of " + im.getStateManager().getStandbyStatus()
                        + "\n Exception Message: " + se.getMessage();
                LOGGER.info(message, se);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                return;
            } catch (IntegrityMonitorException e) {
                String message = "GET interface called for PAP " + papResourceName + " but an exception occurred"
                        + "\n Exception Message: " + e.getMessage();
                LOGGER.info(message, e);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                return;
            }
            // Request from the API to get the gitPath
            String apiflag = request.getParameter("apiflag");
            if (apiflag!=null) {
                if(authorizeRequest(request)){
                    APIRequestHandler apiRequestHandler = new APIRequestHandler();
                    try{
                        loggingContext.metricStarted();
                        apiRequestHandler.doGet(request,response, apiflag);
                        loggingContext.metricEnded();
                        PolicyLogger.metrics("XACMLPapServlet doGet apiRequestHandler doGet");
                    }catch(IOException e){
                        LOGGER.error(e);
                    }
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Ended Successfully");
                    im.endTransaction();
                    return;
                } else {
                    String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
                    im.endTransaction();
                    return;
                }
            }
            // Is this from the Admin Console?
            String groupId = request.getParameter("groupId");
            if (groupId != null) {
                // this is from the Admin Console, so handle separately
                try{
                    loggingContext.metricStarted();
                    doACGet(request, response, groupId, loggingContext);
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doGet doACGet");
                } catch(IOException e){
                    LOGGER.error(e);
                }
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            }
            // Get the PDP's ID
            String id = this.getPDPID(request);
            LOGGER.info("doGet from: " + id);
            // Get the PDP Object
            OnapPDP pdp = null;
            try{
                pdp = XACMLPapServlet.papEngine.getPDP(id);
            }catch(PAPException e){
                LOGGER.error(e);
            }
            // Is it known?
            if (pdp == null) {
                // Check if request came from localhost
                if (request.getRemoteHost().equals("localhost") ||
                        request.getRemoteHost().equals(request.getLocalAddr())) {
                    // Return status information - basically all the groups
                    loggingContext.setServiceName("PAP.getGroups");
                    Set<OnapPDPGroup> groups = null;
                    try {
                        groups = papEngine.getOnapPDPGroups();
                    } catch(PAPException e) {
                        LOGGER.debug(e);
                        PolicyLogger.error(MessageCodes.ERROR_UNKNOWN, e, "XACMLPapServlet", " GET exception");
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        im.endTransaction();
                        return;
                    }
                    // convert response object to JSON and include in the response
                    mapperWriteValue(new ObjectMapper(), response,  groups);
                    response.setHeader("content-type", "application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Ended Successfully");
                    im.endTransaction();
                    return;
                }
                String message = "Unknown PDP: " + id + " from " + request.getRemoteHost() + " us: " + request.getLocalAddr();
                PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
                im.endTransaction();
                return;
            }
            loggingContext.setServiceName("PAP.getPolicy");
            // Get the PDP's Group
            OnapPDPGroup group = null;
            try {
                group = XACMLPapServlet.papEngine.getPDPGroup((OnapPDP) pdp);
            } catch (PAPException e) {
                LOGGER.error(e);
            }
            if (group == null) {
                String message = "No group associated with pdp " + pdp.getId();
                LOGGER.warn(XACMLErrorConstants.ERROR_PERMISSIONS + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED, message);
                im.endTransaction();
                return;
            }
            // Which policy do they want?
            String policyId = request.getParameter("id");
            if (policyId == null) {
                String message = "Did not specify an id for the policy";
                LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_NOT_FOUND, message);
                im.endTransaction();
                return;
            }
            PDPPolicy policy = group.getPolicy(policyId);
            if (policy == null) {
                String message = "Unknown policy: " + policyId;
                LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_NOT_FOUND, message);
                im.endTransaction();
                return;
            }
            try{
                LOGGER.warn("PolicyDebugging: Policy Validity: " + policy.isValid() + "\n "
                        + "Policy Name : " + policy.getName() + "\n Policy URI: " + policy.getLocation().toString());
            } catch (PAPException| IOException e){
                LOGGER.error(e);
            }
            try (InputStream is = new FileInputStream(((StdPDPGroup)group).getDirectory().toString()+File.separator+policyId); OutputStream os = response.getOutputStream()) {
                // Send the policy back
                IOUtils.copy(is, os);
                response.setStatus(HttpServletResponse.SC_OK);
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
            } catch (IOException e) {
                String message = "Failed to open policy id " + policyId;
                LOGGER.debug(e);
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_NOT_FOUND, message);
            }
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Ended");
        im.endTransaction();
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        loggingContext.setServiceName("PAP.put");
        if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doPut) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doPut)");
        }
        try {
            loggingContext.metricStarted();
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPut im startTransaction");
        } catch (IntegrityMonitorException e) {
            String message = "PUT interface called for PAP " + papResourceName;
            if (e instanceof AdministrativeStateException) {
                message += " but it has an Administrative state of "
                    + im.getStateManager().getAdminState();
            } else if (e instanceof StandbyStatusException) {
                message += " but it has a Standby Status of "
                    + im.getStateManager().getStandbyStatus();
            } else {
                message += " but an exception occurred";

            }
            message += "\n Exception Message: " + e.getMessage();

            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }

        loggingContext.metricStarted();
        XACMLRest.dumpRequest(request);
        loggingContext.metricEnded();
        PolicyLogger.metrics("XACMLPapServlet doPut dumpRequest");
        //need to check if request is from the API or Admin console
        String apiflag = request.getParameter("apiflag");
        //This would occur if a PolicyDBDao notification was received
        String policyDBDaoRequestUrl = request.getParameter("policydbdaourl");
        if(policyDBDaoRequestUrl != null){
            LOGGER.info("XACMLPapServlet: PolicyDBDao Notification received." );
            String policyDBDaoRequestEntityId = request.getParameter("entityid");
            String policyDBDaoRequestEntityType = request.getParameter("entitytype");
            String policyDBDaoRequestExtraData = request.getParameter("extradata");
            if(policyDBDaoRequestEntityId == null || policyDBDaoRequestEntityType == null){
                setResponseError(response,400, "entityid or entitytype not supplied");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            }
            loggingContext.metricStarted();
            LOGGER.info("XACMLPapServlet: Calling PolicyDBDao to handlIncomingHttpNotification");
            policyDBDao.handleIncomingHttpNotification(policyDBDaoRequestUrl,policyDBDaoRequestEntityId,policyDBDaoRequestEntityType,policyDBDaoRequestExtraData,this);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPut handle incoming http notification");
            response.setStatus(200);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Ended Successfully");
            im.endTransaction();
            return;
        }
        /*
         * Request for ImportService
         */
        String importService = request.getParameter("importService");
        if (importService != null) {
            if(authorizeRequest(request)){
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try{
                    loggingContext.metricStarted();
                    apiRequestHandler.doPut(request, response, importService);
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doPut apiRequestHandler doPut");
                }catch(IOException e){
                    LOGGER.error(e);
                }
                im.endTransaction();
                return;
            } else {
                String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS + message );
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_FORBIDDEN, message);
                return;
            }
        }
        //
        // See if this is Admin Console registering itself with us
        //
        String acURLString = request.getParameter("adminConsoleURL");
        if (acURLString != null) {
            loggingContext.setServiceName("AC:PAP.register");
            // remember this Admin Console for future updates
            if ( ! adminConsoleURLStringList.contains(acURLString)) {
                adminConsoleURLStringList.add(acURLString);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Admin Console registering with URL: " + acURLString);
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            loggingContext.transactionEnded();
            auditLogger.info("Success");
            PolicyLogger.audit("Transaction Ended Successfully");
            im.endTransaction();
            return;
        }
        /*
         * This is to update the PDP Group with the policy/policies being pushed
         * Part of a 2 step process to push policies to the PDP that can now be done
         * From both the Admin Console and the PolicyEngine API
         */
        String groupId = request.getParameter("groupId");
        if (groupId != null) {
            if(apiflag!=null){
                if(!authorizeRequest(request)){
                    String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response,HttpServletResponse.SC_FORBIDDEN, message);
                    return;
                }
                if(apiflag.equalsIgnoreCase("addPolicyToGroup")){
                    try{
                        updateGroupsFromAPI(request, response, groupId, loggingContext);
                    }catch(IOException e){
                        LOGGER.error(e);
                    }
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Ended Successfully");
                    im.endTransaction();
                    return;
                }
            }
            // this is from the Admin Console, so handle separately
            try {
                loggingContext.metricEnded();
                doACPut(request, response, groupId, loggingContext);
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet goPut doACPut");
            } catch (IOException e) {
                LOGGER.error(e);
            }
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Ended Successfully");
            im.endTransaction();
            return;
        }
        //
        // Request is for policy validation and creation
        //
        if (apiflag != null && apiflag.equalsIgnoreCase("admin")){
            // this request is from the Admin Console
            SavePolicyHandler savePolicyHandler = SavePolicyHandler.getInstance();
            try{
                loggingContext.metricStarted();
                savePolicyHandler.doPolicyAPIPut(request, response);
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet goPut savePolicyHandler");
            } catch (IOException e) {
                LOGGER.error(e);
            }
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Ended Successfully");
            im.endTransaction();
            return;
        } else if (apiflag != null && "api".equalsIgnoreCase(apiflag)) {
            // this request is from the Policy Creation API
            if(authorizeRequest(request)){
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try{
                    loggingContext.metricStarted();
                    apiRequestHandler.doPut(request, response, request.getHeader("ClientScope"));
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet goPut apiRequestHandler doPut");
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            } else {
                String message = "PEP not Authorized for making this Request!!";
                PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_FORBIDDEN, message);
                im.endTransaction();
                return;
            }
        }
        // We do not expect anything from anywhere else.
        // This method is here in case we ever need to support other operations.
        LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Request does not have groupId or apiflag");
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Failed - See Error.log");
        setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId or apiflag");
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Failed - See error.log");
        im.endTransaction();
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        loggingContext.setServiceName("PAP.delete");
        if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doDelete) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doDelete)");
        }
        try {
            loggingContext.metricStarted();
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doDelete im startTransaction");
        } catch (AdministrativeStateException ae){
            String message = "DELETE interface called for PAP " + papResourceName + " but it has an Administrative"
                    + " state of " + im.getStateManager().getAdminState()
                    + "\n Exception Message: " + ae.getMessage();
            LOGGER.info(message, ae);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }catch (StandbyStatusException se) {
            String message = "PUT interface called for PAP " + papResourceName + " but it has a Standby Status"
                    + " of " + im.getStateManager().getStandbyStatus()
                    + "\n Exception Message: " + se.getMessage();
            LOGGER.info(message, se);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        } catch (IntegrityMonitorException e) {
            String message = "PUT interface called for PAP " + papResourceName + " but an exception occurred"
                    + "\n Exception Message: " + e.getMessage();
            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
        loggingContext.metricStarted();
        XACMLRest.dumpRequest(request);
        loggingContext.metricEnded();
        PolicyLogger.metrics("XACMLPapServlet doDelete dumpRequest");
        String groupId = request.getParameter("groupId");
        String apiflag = request.getParameter("apiflag");
        if (groupId != null) {
            // Is this from the Admin Console or API?
            if(apiflag!=null) {
                if(!authorizeRequest(request)){
                    String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response,HttpServletResponse.SC_FORBIDDEN, message);
                    return;
                }
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try {
                    loggingContext.metricStarted();
                    apiRequestHandler.doDelete(request, response, loggingContext, apiflag);
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doDelete apiRequestHandler doDelete");
                } catch (Exception e) {
                    LOGGER.error("Exception Occured"+e);
                }
                if(apiRequestHandler.getNewGroup()!=null){
                    groupChanged(apiRequestHandler.getNewGroup(), loggingContext);
                }
                return;
            }
            // this is from the Admin Console, so handle separately
            try{
                loggingContext.metricStarted();
                doACDelete(request, response, groupId, loggingContext);
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doDelete doACDelete");
            } catch (IOException e) {
                LOGGER.error(e);
            }
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Ended Successfully");
            im.endTransaction();
            return;
        }
        //Catch anything that fell through
        PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Request does not have groupId");
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Failed - See Error.log");
        setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId");
        im.endTransaction();
    }

    private boolean isPDPCurrent(Properties policies, Properties pipconfig, Properties pdpProperties) {
        String localRootPolicies = policies.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        String localReferencedPolicies = policies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        if (localRootPolicies == null || localReferencedPolicies == null) {
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing property on PAP server: RootPolicies="+localRootPolicies+"  ReferencedPolicies="+localReferencedPolicies);
            return false;
        }
        // Compare the policies and pipconfig properties to the pdpProperties
        try {
            // the policy properties includes only xacml.rootPolicies and
            // xacml.referencedPolicies without any .url entries
            Properties pdpPolicies = XACMLProperties.getPolicyProperties(pdpProperties, false);
            Properties pdpPipConfig = XACMLProperties.getPipProperties(pdpProperties);
            if (localRootPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_ROOTPOLICIES)) &&
                    localReferencedPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES)) &&
                    pdpPipConfig.equals(pipconfig)) {
                // The PDP is current
                return true;
            }
        } catch (Exception e) {
            // we get here if the PDP did not include either xacml.rootPolicies or xacml.pip.engines,
            // or if there are policies that do not have a corresponding ".url" property.
            // Either of these cases means that the PDP is not up-to-date, so just drop-through to return false.
            PolicyLogger.error(MessageCodes.ERROR_SCHEMA_INVALID, e, "XACMLPapServlet", " PDP Error");
        }
        return false;
    }

    private void populatePolicyURL(StringBuffer urlPath, Properties policies) {
        String lists[] = new String[2];
        lists[0] = policies.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        lists[1] = policies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        for (String list : lists) {
            if (list != null && list.isEmpty() == false) {
                for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(list)) {
                    String url = urlPath + "?id=" + id;
                    LOGGER.info("Policy URL for " + id + ": " + url);
                    policies.setProperty(id + ".url", url);
                }
            }
        }
    }

    protected String getPDPID(HttpServletRequest request) {
        String pdpURL = request.getHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID);
        if (pdpURL == null || pdpURL.isEmpty()) {
            // Should send back its port for identification
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP did not send custom header");
            pdpURL = "";
        }
        return pdpURL;
    }

    protected String getPDPJMX(HttpServletRequest request) {
        String pdpJMMX = request.getHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_JMX_PORT);
        if (pdpJMMX == null || pdpJMMX.isEmpty()) {
            // Should send back its port for identification
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP did not send custom header for JMX Port so the value of 0 is assigned");
            return null;
        }
        return pdpJMMX;
    }

    /**
     * Requests from the PolicyEngine API to update the PDP Group with pushed policy
     *
     * @param request
     * @param response
     * @param groupId
     * @param loggingContext
     * @throws ServletException
     * @throws IOException
     */
    public void updateGroupsFromAPI(HttpServletRequest request, HttpServletResponse response, String groupId, ONAPLoggingContext loggingContext) throws IOException {
        PolicyDBDaoTransaction acPutTransaction = policyDBDao.getNewTransaction();
        PolicyLogger.audit("PolicyDBDaoTransaction started for updateGroupsFromAPI");
        try {
            // for PUT operations the group may or may not need to exist before the operation can be done
            StdPDPGroup group = (StdPDPGroup) papEngine.getGroup(groupId);

            // get the request input stream content into a String
            String json = null;
            java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
            scanner.useDelimiter("\\A");
            json =  scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            PolicyLogger.info("pushPolicy request from API: " + json);

            // convert Object sent as JSON into local object
            StdPDPPolicy policy = PolicyUtils.jsonStringToObject(json, StdPDPPolicy.class);

            //Get the current policies from the Group and Add the new one
            Set<PDPPolicy> currentPoliciesInGroup = new HashSet<>();
            currentPoliciesInGroup = group.getPolicies();
            //If the selected policy is in the group we must remove the old version of it
            LOGGER.info("Removing old version of the policy");
            for(PDPPolicy existingPolicy : currentPoliciesInGroup) {
                if (existingPolicy.getName().equals(policy.getName()) && !existingPolicy.getId().equals(policy.getId())){
                    group.removePolicy(existingPolicy);
                    LOGGER.info("Removing policy: " + existingPolicy);
                    break;
                }
            }

            // Assume that this is an update of an existing PDP Group
            loggingContext.setServiceName("PolicyEngineAPI:PAP.updateGroup");
            try{
                acPutTransaction.updateGroup(group, "XACMLPapServlet.doACPut");
            } catch(Exception e){
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while updating group in the database: "
                        +"group="+group.getId());
                throw new PAPException(e.getMessage());
            }

            LOGGER.info("Calling updatGroup() with new group");
            papEngine.updateGroup(group);
            String policyId = "empty";
            if(policy !=null && policy.getId() != null){
                policyId = policy.getId();
            }
            if(!policyId.matches(REGEX) ){
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader("error",ADD_GROUP_ERROR);
                response.addHeader("message", "Policy Id is not valid");
                return;
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.addHeader("operation", "push");
            response.addHeader("policyId", policyId);
            response.addHeader("groupId", groupId);

            LOGGER.info("Group '" + group.getId() + "' updated");

            loggingContext.metricStarted();
            acPutTransaction.commitTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet updateGroupsFromAPI commitTransaction");
            loggingContext.metricStarted();
            notifyAC();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet updateGroupsFromAPI notifyAC");

            // Group changed, which might include changing the policies
            groupChanged(group, loggingContext);
            loggingContext.transactionEnded();
            LOGGER.info("Success");

            if (policy != null && ((policy.getId().contains("Config_MS_")) || (policy.getId().contains("BRMS_Param")))) {
                PushPolicyHandler pushPolicyHandler = PushPolicyHandler.getInstance();
                if (pushPolicyHandler.preSafetyCheck(policy, configHome)) {
                    LOGGER.debug("Precheck Successful.");
                }
            }

            PolicyLogger.audit("Transaction Ended Successfully");
            return;
        } catch (PAPException e) {
            acPutTransaction.rollbackTransaction();
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " API PUT exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + "Exception in request to update group from API - See Error.log on on the PAP.";
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.addHeader("error",ADD_GROUP_ERROR);
            response.addHeader("message", message);
            return;
        }
    }

    /**
     * Requests from the Admin Console for operations not on single specific objects
     *
     * @param request
     * @param response
     * @param groupId
     * @param loggingContext
     * @throws ServletException
     * @throws IOException
     */
    private void doACPost(HttpServletRequest request, HttpServletResponse response, String groupId, ONAPLoggingContext loggingContext) throws ServletException, IOException {
        PolicyDBDaoTransaction doACPostTransaction = null;
        try {
            String groupName = request.getParameter("groupName");
            String groupDescription = request.getParameter("groupDescription");
            String apiflag = request.getParameter("apiflag");
            if (groupName != null && groupDescription != null) {
                // Args:	      group=<groupId> groupName=<name> groupDescription=<description>            <= create a new group
                loggingContext.setServiceName("AC:PAP.createGroup");
                String unescapedName = null;
                String unescapedDescription = null;
                try{
                    unescapedName = URLDecoder.decode(groupName, "UTF-8");
                    unescapedDescription = URLDecoder.decode(groupDescription, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e);
                }
                PolicyDBDaoTransaction newGroupTransaction = policyDBDao.getNewTransaction();
                try {
                    newGroupTransaction.createGroup(PolicyDBDao.createNewPDPGroupId(unescapedName), unescapedName, unescapedDescription,"XACMLPapServlet.doACPost");
                    papEngine.newGroup(unescapedName, unescapedDescription);
                    loggingContext.metricStarted();
                    newGroupTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doACPost commitTransaction");
                } catch (Exception e) {
                    newGroupTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Unable to create new group");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to create new group '" + groupId + "'");
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("New Group '" + groupId + "' created");
                }
                // tell the Admin Consoles there is a change
                loggingContext.metricStarted();
                notifyAC();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPost notifyAC");
                // new group by definition has no PDPs, so no need to notify them of changes
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            }
            // for all remaining POST operations the group must exist before the operation can be done
            OnapPDPGroup group = null;
            try{
                group = papEngine.getGroup(groupId);
            } catch (PAPException e){
                LOGGER.error(e);
            }
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                //for fixing Header Manipulation of Fortify issue
                if(!message.matches(REGEX)){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.addHeader("error",ADD_GROUP_ERROR);
                    response.addHeader("message", "GroupId Id is not valid");
                    return;
                }
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                if (apiflag!=null){
                    response.addHeader("error", "unknownGroupId");
                    response.addHeader("operation", "push");
                    response.addHeader("message", message);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    setResponseError(response,HttpServletResponse.SC_NOT_FOUND, message);
                }
                return;
            }

            // If the request contains a policyId then we know we are pushing the policy to PDP
            if (request.getParameter("policyId") != null) {

                if(apiflag!=null){
                    loggingContext.setServiceName("PolicyEngineAPI:PAP.postPolicy");
                    LOGGER.info("PushPolicy Request From The API");
                } else {
                    loggingContext.setServiceName("AC:PAP.postPolicy");
                    LOGGER.info("PushPolicy Request From The AC");
                }

                String policyId = request.getParameter("policyId");
                PolicyDBDaoTransaction addPolicyToGroupTransaction = policyDBDao.getNewTransaction();
                StdPDPGroup updatedGroup = null;
                try {
                    //Copying the policy to the file system and updating groups in database
                    LOGGER.info("PapServlet: calling PolicyDBDao.addPolicyToGroup()");
                    updatedGroup = addPolicyToGroupTransaction.addPolicyToGroup(group.getId(), policyId,"XACMLPapServlet.doACPost");
                    loggingContext.metricStarted();
                    addPolicyToGroupTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doACPost commitTransaction");
                    LOGGER.info("PapServlet: addPolicyToGroup() succeeded, transaction was committed");

                } catch (Exception e) {
                    addPolicyToGroupTransaction.rollbackTransaction();
                    String message = "Policy '" + policyId + "' not copied to group '" + groupId +"': " + e;
                    //for fixing Header Manipulation of Fortify issue
                    if(!message.matches(REGEX)){
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.addHeader("error",ADD_GROUP_ERROR);
                        response.addHeader("message", "Policy Id is not valid");
                        return;
                    }
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " " + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    if (apiflag!=null){
                        response.addHeader("error", "policyCopyError");
                        response.addHeader("message", message);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } else {
                        setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                    }
                    return;
                }

                if(apiflag != null){
                    /*
                     * If request comes from the API we need to run the PolicyDBDao updateGroup() to notify other paps of the change.
                     * The GUI does this from the POLICY-SDK-APP code.
                     */

                    // Get new transaction to perform updateGroup()
                    PolicyDBDaoTransaction acPutTransaction = policyDBDao.getNewTransaction();
                    try {
                        // get the request content into a String and read the inputStream into a buffer
                        java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
                        scanner.useDelimiter("\\A");
                        String json =  scanner.hasNext() ? scanner.next() : "";
                        scanner.close();

                        // convert Object sent as JSON into local object
                        ObjectMapper mapper = new ObjectMapper();
                        Object objectFromJSON = mapper.readValue(json, StdPDPPolicy.class);
                        StdPDPPolicy policy = (StdPDPPolicy) objectFromJSON;

                        LOGGER.info("Request JSON Payload: " + json);

                        // Assume that this is an update of an existing PDP Group
                        loggingContext.setServiceName("PolicyEngineAPI:PAP.updateGroup");
                        try{
                            acPutTransaction.updateGroup(updatedGroup, "XACMLPapServlet.doACPut");
                        } catch(Exception e){
                            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error occurred when notifying PAPs of a group change: "
                                    + e);
                            throw new PAPException(e.getMessage());
                        }

                        LOGGER.info("Calling updatGroup() with new group");
                        papEngine.updateGroup(updatedGroup);

                        LOGGER.info("Group '" + updatedGroup.getId() + "' updated");

                        // Commit transaction to send notification to other PAPs
                        loggingContext.metricStarted();
                        acPutTransaction.commitTransaction();
                        loggingContext.metricEnded();
                        PolicyLogger.metrics("XACMLPapServlet updateGroupsFromAPI commitTransaction");
                        loggingContext.metricStarted();

                        notifyAC();
                        loggingContext.metricEnded();
                        PolicyLogger.metrics("XACMLPapServlet updateGroupsFromAPI notifyAC");

                        // Group changed to send notification to PDPs, which might include changing the policies
                        groupChanged(updatedGroup,loggingContext);
                        loggingContext.transactionEnded();
                        LOGGER.info("Success");

                        if (policy != null && ((policy.getName().contains("Config_MS_")) || (policy.getId().contains("BRMS_Param")))) {
                            PushPolicyHandler pushPolicyHandler = PushPolicyHandler.getInstance();
                            if (pushPolicyHandler.preSafetyCheck(policy, configHome)) {
                                LOGGER.debug("Precheck Successful.");
                            }
                        }

                        //delete temporary policy file from the bin directory
                        Files.deleteIfExists(Paths.get(policy.getId()));

                    } catch (Exception e) {
                        acPutTransaction.rollbackTransaction();
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " API PUT exception");
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + "Exception occurred when updating the group from API.";
                        LOGGER.error(message);
                        setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.addHeader("error",ADD_GROUP_ERROR);
                        response.addHeader("message", message);
                        return;
                    }
                }

                // policy file copied ok and the Group was updated on the PDP
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                response.addHeader("operation", "push");
                response.addHeader("policyId", policyId);
                response.addHeader("groupId", groupId);

                LOGGER.info("policy '" + policyId + "' copied to directory for group '" + groupId + "'");
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                LOGGER.info("Transaction Ended Successfully");

                return;
            } else if (request.getParameter("default") != null) {
                // Args:       group=<groupId> default=true               <= make default
                // change the current default group to be the one identified in the request.
                loggingContext.setServiceName("AC:PAP.setDefaultGroup");
                // This is a POST operation rather than a PUT "update group" because of the side-effect that the current default group is also changed.
                // It should never be the case that multiple groups are currently marked as the default, but protect against that anyway.
                PolicyDBDaoTransaction setDefaultGroupTransaction = policyDBDao.getNewTransaction();
                try {
                    setDefaultGroupTransaction.changeDefaultGroup(group, "XACMLPapServlet.doACPost");
                    papEngine.setDefaultGroup(group);
                    loggingContext.metricStarted();
                    setDefaultGroupTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doACPost commitTransaction");
                } catch (Exception e) {
                    setDefaultGroupTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Unable to set group");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to set group '" + groupId + "' to default");
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Group '" + groupId + "' set to be default");
                }
                // Notify the Admin Consoles that something changed
                // For now the AC cannot handle anything more detailed than the whole set of PDPGroups, so just notify on that
                //TODO - Future: FIGURE OUT WHAT LEVEL TO NOTIFY: 2 groups or entire set - currently notify AC to update whole configuration of all groups
                loggingContext.metricStarted();
                notifyAC();
                // This does not affect any PDPs in the existing groups, so no need to notify them of this change
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPost notifyAC");
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                LOGGER.info("Transaction Ended Successfully");
                return;
            } else if (request.getParameter("pdpId") != null) {
                doACPostTransaction = policyDBDao.getNewTransaction();
                // Args:       group=<groupId> pdpId=<pdpId>               <= move PDP to group
                loggingContext.setServiceName("AC:PAP.movePDP");
                String pdpId = request.getParameter("pdpId");
                OnapPDP pdp = papEngine.getPDP(pdpId);
                OnapPDPGroup originalGroup = papEngine.getPDPGroup((OnapPDP) pdp);
                try{
                    doACPostTransaction.movePdp(pdp, group, "XACMLPapServlet.doACPost");
                }catch(Exception e){
                    doACPostTransaction.rollbackTransaction();
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet",
                            " Error while moving pdp in the database: "
                                    +"pdp="+pdp.getId()+",to group="+group.getId());
                    throw new PAPException(e.getMessage());
                }
                papEngine.movePDP((OnapPDP) pdp, group);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("PDP '" + pdp.getId() +"' moved to group '" + group.getId() + "' set to be default");
                }
                // update the status of both the original group and the new one
                ((StdPDPGroup)originalGroup).resetStatus();
                ((StdPDPGroup)group).resetStatus();
                // Notify the Admin Consoles that something changed
                // For now the AC cannot handle anything more detailed than the whole set of PDPGroups, so just notify on that
                loggingContext.metricStarted();
                notifyAC();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPost notifyAC");
                // Need to notify the PDP that it's config may have changed
                pdpChanged(pdp, loggingContext);
                loggingContext.metricStarted();
                doACPostTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPost commitTransaction");
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            }
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC POST exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to GET info about the Groups and PDPs
     *
     * @param request
     * @param response
     * @param groupId
     * @param loggingContext
     * @throws ServletException
     * @throws IOException
     */
    private void doACGet(HttpServletRequest request, HttpServletResponse response, String groupId, ONAPLoggingContext loggingContext) throws IOException {
        try {
            String parameterDefault = request.getParameter("default");
            String pdpId = request.getParameter("pdpId");
            String pdpGroup = request.getParameter("getPDPGroup");
            if ("".equals(groupId)) {
                // request IS from AC but does not identify a group by name
                if (parameterDefault != null) {
                    // Request is for the Default group (whatever its id)
                    loggingContext.setServiceName("AC:PAP.getDefaultGroup");
                    OnapPDPGroup group = papEngine.getDefaultGroup();
                    // convert response object to JSON and include in the response
                    mapperWriteValue(new ObjectMapper(), response,  group);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("GET Default group req from '" + request.getRequestURL() + "'");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("content-type", "application/json");
                    try{
                        response.getOutputStream().close();
                    } catch (IOException e){
                        LOGGER.error(e);
                    }
                    loggingContext.transactionEnded();
                    auditLogger.info("Success");
                    PolicyLogger.audit("Transaction Ended Successfully");
                    return;
                } else if (pdpId != null) {
                    // Request is related to a PDP
                    if (pdpGroup == null) {
                        // Request is for the (unspecified) group containing a given PDP
                        loggingContext.setServiceName("AC:PAP.getPDP");
                        OnapPDP pdp = null;
                        try{
                            pdp = papEngine.getPDP(pdpId);
                        }catch(PAPException e){
                            LOGGER.error(e);
                        }
                        // convert response object to JSON and include in the response
                        mapperWriteValue(new ObjectMapper(), response,  pdp);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("GET pdp '" + pdpId + "' req from '" + request.getRequestURL() + "'");
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setHeader("content-type", "application/json");
                        try{
                            response.getOutputStream().close();
                        } catch (IOException e){
                            LOGGER.error(e);
                        }
                        loggingContext.transactionEnded();
                        auditLogger.info("Success");
                        PolicyLogger.audit("Transaction Ended Successfully");
                        return;
                    } else {
                        // Request is for the group containing a given PDP
                        loggingContext.setServiceName("AC:PAP.getGroupForPDP");
                        OnapPDPGroup group =null;
                        try{
                            OnapPDP pdp = papEngine.getPDP(pdpId);
                            group = papEngine.getPDPGroup((OnapPDP) pdp);
                        }catch(PAPException e){
                            LOGGER.error(e);
                        }
                        // convert response object to JSON and include in the response
                        mapperWriteValue(new ObjectMapper(), response,  group);
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("GET PDP '" + pdpId + "' Group req from '" + request.getRequestURL() + "'");
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setHeader("content-type", "application/json");
                        try{
                            response.getOutputStream().close();
                        } catch (IOException e){
                            LOGGER.error(e);
                        }
                        loggingContext.transactionEnded();
                        auditLogger.info("Success");
                        PolicyLogger.audit("Transaction Ended Successfully");
                        return;
                    }
                } else {
                    // request is for top-level properties about all groups
                    loggingContext.setServiceName("AC:PAP.getAllGroups");
                    Set<OnapPDPGroup> groups = null;
                    try {
                        groups = papEngine.getOnapPDPGroups();
                    } catch(PAPException e) {
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC Get exception");
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                    // convert response object to JSON and include in the response
                    mapperWriteValue(new ObjectMapper(), response,  groups);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("GET All groups req");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("content-type", "application/json");
                    try{
                        response.getOutputStream().close();
                    } catch (IOException e){
                        LOGGER.error(e);
                    }
                    loggingContext.transactionEnded();
                    auditLogger.info("Success");
                    PolicyLogger.audit("Transaction Ended Successfully");
                    return;
                }
            }
            // for all other GET operations the group must exist before the operation can be done
            OnapPDPGroup group = null;
            try{
                group = papEngine.getGroup(groupId);
            } catch(PAPException e){
                LOGGER.error(e);
            }
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                //for fixing Header Manipulation of Fortify issue
                if(!message.matches(REGEX)){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.addHeader("error",ADD_GROUP_ERROR);
                    response.addHeader("message", "Group Id is not valid");
                    return;
                }
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_NOT_FOUND, message);
                return;
            }
            // Figure out which request this is based on the parameters
            String policyId = request.getParameter("policyId");
            if (policyId != null) {
                // retrieve a policy
                loggingContext.setServiceName("AC:PAP.getPolicy");
                // convert response object to JSON and include in the response
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " GET Policy not implemented");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "GET Policy not implemented");
            } else {
                // No other parameters, so return the identified Group
                loggingContext.setServiceName("AC:PAP.getGroup");
                // convert response object to JSON and include in the response
                mapperWriteValue(new ObjectMapper(), response,  group);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("GET group '" + group.getId() + "' req from '" + request.getRequestURL() + "'");
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.setHeader("content-type", "application/json");
                try{
                    response.getOutputStream().close();
                } catch (IOException e){
                    LOGGER.error(e);
                }
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            }
            // Currently there are no other GET calls from the AC.
            // The AC uses the "GET All Groups" operation to fill its local cache and uses that cache for all other GETs without calling the PAP.
            // Other GETs that could be called:
            //				Specific Group	(groupId=<groupId>)
            //				A Policy		(groupId=<groupId> policyId=<policyId>)
            //				A PDP			(groupId=<groupId> pdpId=<pdpId>)
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " UNIMPLEMENTED ");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC Get exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to create new items or update existing ones
     *
     * @param request
     * @param response
     * @param groupId
     * @param loggingContext
     * @throws ServletException
     * @throws IOException
     */
    private void doACPut(HttpServletRequest request, HttpServletResponse response, String groupId, ONAPLoggingContext loggingContext) throws IOException {
        PolicyDBDaoTransaction acPutTransaction = policyDBDao.getNewTransaction();
        try {
            // for PUT operations the group may or may not need to exist before the operation can be done
            OnapPDPGroup group = papEngine.getGroup(groupId);
            // determine the operation needed based on the parameters in the request
            // for remaining operations the group must exist before the operation can be done
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_NOT_FOUND, message);
                return;
            }
            if (request.getParameter("policy") != null) {
                //        group=<groupId> policy=<policyId> contents=policy file               <= Create new policy file in group dir, or replace it if it already exists (do not touch properties)
                loggingContext.setServiceName("AC:PAP.putPolicy");
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " PARTIALLY IMPLEMENTED!!!  ACTUAL CHANGES SHOULD BE MADE BY PAP SERVLET!!! ");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            } else if (request.getParameter("pdpId") != null) {
                // ARGS:        group=<groupId> pdpId=<pdpId/URL>          <= create a new PDP or Update an Existing one
                String pdpId = request.getParameter("pdpId");
                if (papEngine.getPDP(pdpId) == null) {
                    loggingContext.setServiceName("AC:PAP.createPDP");
                } else {
                    loggingContext.setServiceName("AC:PAP.updatePDP");
                }
                // get the request content into a String
                String json = null;
                // read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
                try{
                    Scanner scanner = new Scanner(request.getInputStream());
                    scanner.useDelimiter("\\A");
                    json =  scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                }catch(IOException e){
                    LOGGER.error(e);
                }
                LOGGER.info("JSON request from AC: " + json);
                // convert Object sent as JSON into local object
                ObjectMapper mapper = new ObjectMapper();
                Object objectFromJSON = null;
                try {
                    objectFromJSON = mapper.readValue(json, StdPDP.class);
                } catch(Exception e) {
                    LOGGER.error(e);
                }
                if (pdpId == null ||
                        objectFromJSON == null ||
                        ! (objectFromJSON instanceof StdPDP) ||
                        ((StdPDP)objectFromJSON).getId() == null ||
                        ! ((StdPDP)objectFromJSON).getId().equals(pdpId)) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " PDP new/update had bad input. pdpId=" + pdpId + " objectFromJSON="+objectFromJSON);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Bad input pdpid for object:"+objectFromJSON);
                }
                StdPDP pdp = (StdPDP) objectFromJSON;
                if(pdp != null){
                    OnapPDP oPDP = null;
                    try{
                        oPDP = papEngine.getPDP(pdpId);
                    }catch (PAPException e){
                        LOGGER.error(e);
                    }
                    if (oPDP == null) {
                        // this is a request to create a new PDP object
                        try{
                            acPutTransaction.addPdpToGroup(pdp.getId(), group.getId(), pdp.getName(),
                                    pdp.getDescription(), pdp.getJmxPort(),"XACMLPapServlet.doACPut");
                        } catch(Exception e){
                            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while adding pdp to group in the database: "
                                    +"pdp="+ (pdp.getId()) +",to group="+group.getId());
                            throw new PAPException(e.getMessage());
                        }
                        try{
                            papEngine.newPDP(pdp.getId(), group, pdp.getName(), pdp.getDescription(), pdp.getJmxPort());
                        }catch(PAPException e){
                            LOGGER.error(e);
                        }
                    } else {
                        try{
                            acPutTransaction.updatePdp(pdp, "XACMLPapServlet.doACPut");
                        } catch(Exception e){
                            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while updating pdp in the database: "
                                    +"pdp="+ pdp.getId());
                            throw new PAPException(e.getMessage());
                        }
                        // this is a request to update the pdp
                        try{
                            papEngine.updatePDP(pdp);
                        }catch(PAPException e){
                            LOGGER.error(e);
                        }
                    }
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("PDP '" + pdpId + "' created/updated");
                    }
                    // adjust the group's state including the new PDP
                    ((StdPDPGroup)group).resetStatus();
                    // tell the Admin Consoles there is a change
                    loggingContext.metricStarted();
                    notifyAC();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doACPut notifyAC");
                    // this might affect the PDP, so notify it of the change
                    pdpChanged(pdp, loggingContext);
                    loggingContext.metricStarted();
                    acPutTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                    loggingContext.transactionEnded();
                    auditLogger.info("Success");
                    PolicyLogger.audit("Transaction Ended Successfully");
                    return;
                }else{
                    try{
                        PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, "XACMLPapServlet", " Error while adding pdp to group in the database: "
                                +"pdp=null" + ",to group="+group.getId());
                        throw new PAPException("PDP is null");
                    } catch(Exception e){
                        throw new PAPException("PDP is null" + e.getMessage() +e);
                    }
                }
            } else if (request.getParameter("pipId") != null) {
                //                group=<groupId> pipId=<pipEngineId> contents=pip properties              <= add a PIP to pip config, or replace it if it already exists (lenient operation)
                loggingContext.setServiceName("AC:PAP.putPIP");
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                return;
            } else {
                // Assume that this is an update of an existing PDP Group
                // ARGS:        group=<groupId>         <= Update an Existing Group
                loggingContext.setServiceName("AC:PAP.updateGroup");
                // get the request content into a String
                String json = null;
                // read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
                try{
                    Scanner scanner = new Scanner(request.getInputStream());
                    scanner.useDelimiter("\\A");
                    json =  scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                }catch(IOException e){
                    LOGGER.error(e);
                }
                LOGGER.info("JSON request from AC: " + json);
                // convert Object sent as JSON into local object
                ObjectMapper mapper = new ObjectMapper();
                Object objectFromJSON = null;
                try {
                    objectFromJSON  = mapper.readValue(json, StdPDPGroup.class);
                } catch(Exception e) {
                    LOGGER.error(e);
                }
                if (objectFromJSON == null || ! (objectFromJSON instanceof StdPDPGroup) ||
                        ! ((StdPDPGroup)objectFromJSON).getId().equals(group.getId())) {
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id=" + group.getId() + " objectFromJSON="+objectFromJSON);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Bad input id for object:"+objectFromJSON);
                }
                // The Path on the PAP side is not carried on the RESTful interface with the AC
                // (because it is local to the PAP)
                // so we need to fill that in before submitting the group for update
                if(objectFromJSON != null){
                    ((StdPDPGroup)objectFromJSON).setDirectory(((StdPDPGroup)group).getDirectory());
                }
                try{
                    if("delete".equals(((StdPDPGroup)objectFromJSON).getOperation())){
                        acPutTransaction.updateGroup((StdPDPGroup)objectFromJSON, "XACMLPapServlet.doDelete");
                    } else {
                        acPutTransaction.updateGroup((StdPDPGroup)objectFromJSON, "XACMLPapServlet.doACPut");
                    }
                } catch(Exception e){
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database: "
                            +"group="+group.getId());
                    LOGGER.error(e);
                    throw new PAPException(e.getMessage());
                }

                PushPolicyHandler pushPolicyHandler = PushPolicyHandler.getInstance();
                OnapPDPGroup updatedGroup = (StdPDPGroup)objectFromJSON;
                if (pushPolicyHandler.preSafetyCheck(updatedGroup, configHome)) {
                    LOGGER.debug("Precheck Successful.");
                }
                try{
                    papEngine.updateGroup((StdPDPGroup)objectFromJSON);
                }catch(PAPException e){
                    LOGGER.error(e);
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Group '" + group.getId() + "' updated");
                }
                loggingContext.metricStarted();
                acPutTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                // tell the Admin Consoles there is a change
                loggingContext.metricStarted();
                notifyAC();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut notifyAC");
                // Group changed, which might include changing the policies
                groupChanged(group, loggingContext);
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            }
        } catch (PAPException e) {
            LOGGER.debug(e);
            acPutTransaction.rollbackTransaction();
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC PUT exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to delete/remove items
     *
     * @param request
     * @param response
     * @param groupId
     * @param loggingContext
     * @throws ServletException
     * @throws IOException
     */
    private void doACDelete(HttpServletRequest request, HttpServletResponse response, String groupId, ONAPLoggingContext loggingContext) throws IOException {
        PolicyDBDaoTransaction removePdpOrGroupTransaction = policyDBDao.getNewTransaction();
        try {
            // for all DELETE operations the group must exist before the operation can be done
            loggingContext.setServiceName("AC:PAP.delete");
            OnapPDPGroup group = papEngine.getGroup(groupId);
            if (group == null) {
                String message = "Unknown groupId '" + groupId + "'";
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId +"'");
                return;
            }
            // determine the operation needed based on the parameters in the request
            if (request.getParameter("policy") != null) {
                //        group=<groupId> policy=<policyId>  [delete=<true|false>]       <= delete policy file from group
                loggingContext.setServiceName("AC:PAP.deletePolicy");
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                return;
            } else if (request.getParameter("pdpId") != null) {
                // ARGS:        group=<groupId> pdpId=<pdpId>                  <= delete PDP
                String pdpId = request.getParameter("pdpId");
                OnapPDP pdp = papEngine.getPDP(pdpId);
                try{
                    removePdpOrGroupTransaction.removePdpFromGroup(pdp.getId(),"XACMLPapServlet.doACDelete");
                } catch(Exception e){
                    throw new PAPException(e);
                }
                try{
                    papEngine.removePDP((OnapPDP) pdp);
                }catch(PAPException e){
                    LOGGER.error(e);
                }
                // adjust the status of the group, which may have changed when we removed this PDP
                ((StdPDPGroup)group).resetStatus();
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                loggingContext.metricStarted();
                notifyAC();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut notifyAC");
                // update the PDP and tell it that it has NO Policies (which prevents it from serving PEP Requests)
                pdpChanged(pdp, loggingContext);
                loggingContext.metricStarted();
                removePdpOrGroupTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            } else if (request.getParameter("pipId") != null) {
                //        group=<groupId> pipId=<pipEngineId> <= delete PIP config for given engine
                loggingContext.setServiceName("AC:PAP.deletePIPConfig");
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response,HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                return;
            } else {
                // ARGS:      group=<groupId> movePDPsToGroupId=<movePDPsToGroupId>            <= delete a group and move all its PDPs to the given group
                String moveToGroupId = request.getParameter("movePDPsToGroupId");
                OnapPDPGroup moveToGroup = null;
                if (moveToGroupId != null) {
                    try{
                        moveToGroup = papEngine.getGroup(moveToGroupId);
                    }catch(PAPException e){
                        LOGGER.error(e);
                    }
                }
                // get list of PDPs in the group being deleted so we can notify them that they got changed
                Set<OnapPDP> movedPDPs = new HashSet<>();
                movedPDPs.addAll(group.getOnapPdps());
                // do the move/remove
                try{
                    removePdpOrGroupTransaction.deleteGroup(group, moveToGroup,"XACMLPapServlet.doACDelete");
                } catch(Exception e){
                    PolicyLogger.error(MessageCodes.ERROR_UNKNOWN, e, "XACMLPapServlet", " Failed to delete PDP Group. Exception");
                    throw new PAPException(e.getMessage());
                }
                try{
                    papEngine.removeGroup(group, moveToGroup);
                }catch(PAPException e){
                    LOGGER.error(e);
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                loggingContext.metricStarted();
                notifyAC();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut notifyAC");
                // notify any PDPs in the removed set that their config may have changed
                for (OnapPDP pdp : movedPDPs) {
                    pdpChanged(pdp, loggingContext);
                }
                loggingContext.metricStarted();
                removePdpOrGroupTransaction.commitTransaction();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doACPut commitTransaction");
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Transaction Ended Successfully");
                return;
            }
        } catch (PAPException e) {
            removePdpOrGroupTransaction.rollbackTransaction();
            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC DELETE exception");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Heartbeat thread - periodically check on PDPs' status
     *
     * Heartbeat with all known PDPs.
     *
     * Implementation note:
     *
     * The PDPs are contacted Sequentially, not in Parallel.
     *
     * If we did this in parallel using multiple threads we would simultaneously use
     * 		- 1 thread and
     * 		- 1 connection
     * for EACH PDP.
     * This could become a resource problem since we already use multiple threads and connections for updating the PDPs
     * when user changes occur.
     * Using separate threads can also make it tricky dealing with timeouts on PDPs that are non-responsive.
     *
     * The Sequential operation does a heartbeat request to each PDP one at a time.
     * This has the flaw that any PDPs that do not respond will hold up the entire heartbeat sequence until they timeout.
     * If there are a lot of non-responsive PDPs and the timeout is large-ish (the default is 20 seconds)
     * it could take a long time to cycle through all of the PDPs.
     * That means that this may not notice a PDP being down in a predictable time.
     */
    private class Heartbeat implements Runnable {
        private PAPPolicyEngine papEngine;
        private Set<OnapPDP> pdps = new HashSet<>();
        private int heartbeatInterval;
        private int heartbeatTimeout;

        public volatile boolean isRunning = false;

        public synchronized boolean isRunning() {
            return this.isRunning;
        }

        public synchronized void terminate() {
            this.isRunning = false;
        }

        public Heartbeat(PAPPolicyEngine papEngine2) {
            papEngine = papEngine2;
            this.heartbeatInterval = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_INTERVAL, "10000"));
            this.heartbeatTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_TIMEOUT, "10000"));
        }

        @Override
        public void run() {
            // Set ourselves as running
            synchronized(this) {
                this.isRunning = true;
            }
            HashMap<String, URL> idToURLMap = new HashMap<>();
            try {
                while (this.isRunning()) {
                    // Wait the given time
                    Thread.sleep(heartbeatInterval);
                    // get the list of PDPs (may have changed since last time)
                    pdps.clear();
                    synchronized(papEngine) {
                        try {
                            for (OnapPDPGroup g : papEngine.getOnapPDPGroups()) {
                                for (OnapPDP p : g.getOnapPdps()) {
                                    pdps.add(p);
                                }
                            }
                        } catch (PAPException e) {
                            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", "Heartbeat unable to read PDPs from PAPEngine");
                        }
                    }
                    // Check for shutdown
                    if (this.isRunning() == false) {
                        LOGGER.info("isRunning is false, getting out of loop.");
                        break;
                    }
                    // try to get the summary status from each PDP
                    boolean changeSeen = false;
                    for (OnapPDP pdp : pdps) {
                        // Check for shutdown
                        if (this.isRunning() == false) {
                            LOGGER.info("isRunning is false, getting out of loop.");
                            break;
                        }
                        // the id of the PDP is its url (though we add a query parameter)
                        URL pdpURL = idToURLMap.get(pdp.getId());
                        if (pdpURL == null) {
                            // haven't seen this PDP before
                            String fullURLString = null;
                            try {
                                // Check PDP ID
                                if(CheckPDP.validateID(pdp.getId())){
                                    fullURLString = pdp.getId() + "?type=hb";
                                    pdpURL = new URL(fullURLString);
                                    idToURLMap.put(pdp.getId(), pdpURL);
                                }
                            } catch (MalformedURLException e) {
                                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPapServlet", " PDP id '" + fullURLString + "' is not a valid URL");
                                continue;
                            }
                        }
                        // Do a GET with type HeartBeat
                        String newStatus = "";
                        HttpURLConnection connection = null;
                        try {
                            // Open up the connection
                            if(pdpURL != null){
                                connection = (HttpURLConnection)pdpURL.openConnection();
                                // Setup our method and headers
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(heartbeatTimeout);
                                // Authentication
                                String encoding = CheckPDP.getEncoding(pdp.getId());
                                if(encoding !=null){
                                    connection.setRequestProperty("Authorization", "Basic " + encoding);
                                }
                                // Do the connect
                                connection.connect();
                                if (connection.getResponseCode() == 204) {
                                    newStatus = connection.getHeaderField(XACMLRestProperties.PROP_PDP_HTTP_HEADER_HB);
                                    if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("Heartbeat '" + pdp.getId() + "' status='" + newStatus + "'");
                                    }
                                } else {
                                    // anything else is an unexpected result
                                    newStatus = PDPStatus.Status.UNKNOWN.toString();
                                    PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " Heartbeat connect response code " + connection.getResponseCode() + ": " + pdp.getId());
                                }
                            }
                        } catch (UnknownHostException e) {
                            newStatus = PDPStatus.Status.NO_SUCH_HOST.toString();
                            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Heartbeat '" + pdp.getId() + "' NO_SUCH_HOST");
                        } catch (SocketTimeoutException e) {
                            newStatus = PDPStatus.Status.CANNOT_CONNECT.toString();
                            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Heartbeat '" + pdp.getId() + "' connection timeout");
                        } catch (ConnectException e) {
                            newStatus = PDPStatus.Status.CANNOT_CONNECT.toString();
                            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Heartbeat '" + pdp.getId() + "' cannot connect");
                        } catch (Exception e) {
                            newStatus = PDPStatus.Status.UNKNOWN.toString();
                            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", "Heartbeat '" + pdp.getId() + "' connect exception");
                        } finally {
                            // cleanup the connection
                            if(connection != null)
                                connection.disconnect();
                        }
                        if ( ! pdp.getStatus().getStatus().toString().equals(newStatus)) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("previous status='" + pdp.getStatus().getStatus()+"'  new Status='" + newStatus + "'");
                            }
                            try {
                                setPDPSummaryStatus(pdp, newStatus);
                            } catch (PAPException e) {
                                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", "Unable to set state for PDP '" + pdp.getId());
                            }
                            changeSeen = true;
                        }
                    }
                    // Check for shutdown
                    if (this.isRunning() == false) {
                        LOGGER.info("isRunning is false, getting out of loop.");
                        break;
                    }
                    // if any of the PDPs changed state, tell the ACs to update
                    if (changeSeen) {
                        notifyAC();
                    }
                }
            } catch (InterruptedException e) {
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " Heartbeat interrupted.  Shutting down");
                this.terminate();
                Thread.currentThread().interrupt();
            }
        }
    }

    /*
     * HELPER to change Group status when PDP status is changed
     * (Must NOT be called from a method that is synchronized on the papEngine or it may deadlock)
     */
    private void setPDPSummaryStatus(OnapPDP pdp, PDPStatus.Status newStatus) throws PAPException {
        setPDPSummaryStatus(pdp, newStatus.toString());
    }

    private void setPDPSummaryStatus(OnapPDP pdp, String newStatus) throws PAPException {
        synchronized(papEngine) {
            StdPDPStatus status = new StdPDPStatus();
            status.setStatus(PDPStatus.Status.valueOf(newStatus));
            ((StdPDP)pdp).setStatus(status);
            // now adjust the group
            StdPDPGroup group = (StdPDPGroup)papEngine.getPDPGroup((OnapPDP) pdp);
            // if the PDP was just deleted it may transiently exist but not be in a group
            if (group != null) {
                group.resetStatus();
            }
        }
    }

    /*
     * Callback methods telling this servlet to notify PDPs of changes made by the PAP StdEngine
     * in the PDP group directories
     */
    @Override
    public void changed() {
        // all PDPs in all groups need to be updated/sync'd
        Set<OnapPDPGroup> groups;
        try {
            groups = papEngine.getOnapPDPGroups();
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " getPDPGroups failed");
            throw new IllegalAccessError(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get Groups: " + e);
        }
        for (OnapPDPGroup group : groups) {
            groupChanged(group);
        }
    }

    public void changed(ONAPLoggingContext loggingContext) {
        // all PDPs in all groups need to be updated/sync'd
        Set<OnapPDPGroup> groups;
        try {
            groups = papEngine.getOnapPDPGroups();
        } catch (PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " getPDPGroups failed");
            throw new IllegalAccessError(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get Groups: " + e);
        }
        for (OnapPDPGroup group : groups) {
            groupChanged(group, loggingContext);
        }
    }

    @Override
    public void groupChanged(OnapPDPGroup group) {
        // all PDPs within one group need to be updated/sync'd
        for (OnapPDP pdp : group.getOnapPdps()) {
            pdpChanged(pdp);
        }
    }

    public void groupChanged(OnapPDPGroup group, ONAPLoggingContext loggingContext) {
        // all PDPs within one group need to be updated/sync'd
        for (OnapPDP pdp : group.getOnapPdps()) {
            pdpChanged(pdp, loggingContext);
        }
    }

    @Override
     public void pdpChanged(OnapPDP pdp) {
        // kick off a thread to do an event notification for each PDP.
        // This needs to be on a separate thread so that PDPs that do not respond (down, non-existent, etc)
        // do not block the PSP response to the AC, which would freeze the GUI until all PDPs sequentially respond or time-out.
        Thread t = new Thread(new UpdatePDPThread(pdp));
        if(CheckPDP.validateID(pdp.getId())){
            t.start();
        }
    }

     public void pdpChanged(OnapPDP pdp, ONAPLoggingContext loggingContext) {
        // kick off a thread to do an event notification for each PDP.
        // This needs to be on a separate thread so that PDPs that do not respond (down, non-existent, etc)
        // do not block the PSP response to the AC, which would freeze the GUI until all PDPs sequentially respond or time-out.
        Thread t = new Thread(new UpdatePDPThread(pdp, loggingContext));
        if(CheckPDP.validateID(pdp.getId())){
            t.start();
        }
    }

    private class UpdatePDPThread implements Runnable {
        private OnapPDP pdp;
        private String requestId;
        private ONAPLoggingContext loggingContext;

        public UpdatePDPThread(OnapPDP pdp) {
            this.pdp = pdp;
        }

        public UpdatePDPThread(OnapPDP pdp, ONAPLoggingContext loggingContext) {
            this.pdp = pdp;
            if ((loggingContext != null) && (loggingContext.getRequestID() != null || loggingContext.getRequestID() == "")) {
                    this.requestId = loggingContext.getRequestID();
            }
            this.loggingContext = loggingContext;
        }

        public void run() {
            // send the current configuration to one PDP
            HttpURLConnection connection = null;
            // get a new logging context for the thread
            try {
                if (this.loggingContext == null) {
                     loggingContext = new ONAPLoggingContext(baseLoggingContext);
                }
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to send property file to " + pdp.getId());
                // Since this is a server-side error, it probably does not reflect a problem on the client,
                // so do not change the PDP status.
                return;
            }
            try {
                loggingContext.setServiceName("PAP:PDP.putConfig");
                // If a requestId was provided, use it, otherwise generate one; post to loggingContext to be used later when calling PDP
                if ((requestId == null) || (requestId == "")) {
                    UUID requestID = UUID.randomUUID();
                    loggingContext.setRequestID(requestID.toString());
                    PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (UpdatePDPThread) so we generated one:  " + loggingContext.getRequestID());
                } else {
                    loggingContext.setRequestID(requestId);
                    PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (UpdatePDPThread):  " + loggingContext.getRequestID());
                }
                loggingContext.transactionStarted();
                // the Id of the PDP is its URL
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("creating url for id '" + pdp.getId() + "'");
                }
                //TODO - currently always send both policies and pips.  Do we care enough to add code to allow sending just one or the other?
                //TODO		(need to change "cache=", implying getting some input saying which to change)
                URL url = new URL(pdp.getId() + "?cache=all");
                // Open up the connection
                connection = (HttpURLConnection)url.openConnection();
                // Setup our method and headers
                connection.setRequestMethod("PUT");
                // Authentication
                String encoding = CheckPDP.getEncoding(pdp.getId());
                if(encoding !=null){
                    connection.setRequestProperty("Authorization", "Basic " + encoding);
                }
                connection.setRequestProperty("Content-Type", "text/x-java-properties");
                connection.setRequestProperty("X-ECOMP-RequestID", loggingContext.getRequestID());
                connection.setInstanceFollowRedirects(true);
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    OnapPDPGroup group = papEngine.getPDPGroup((OnapPDP) pdp);
                    // if the PDP was just deleted, there is no group, but we want to send an update anyway
                    if (group == null) {
                        // create blank properties files
                        Properties policyProperties = new Properties();
                        policyProperties.put(XACMLProperties.PROP_ROOTPOLICIES, "");
                        policyProperties.put(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
                        policyProperties.store(os, "");
                        Properties pipProps = new Properties();
                        pipProps.setProperty(XACMLProperties.PROP_PIP_ENGINES, "");
                        pipProps.store(os, "");
                    } else {
                        // send properties from the current group
                        group.getPolicyProperties().store(os, "");
                        Properties policyLocations = new Properties();
                        for (PDPPolicy policy : group.getPolicies()) {
                            policyLocations.put(policy.getId() + ".url", XACMLPapServlet.papURL + "?id=" + policy.getId());
                        }
                        policyLocations.store(os, "");
                        group.getPipConfigProperties().store(os, "");
                    }
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to send property file to " + pdp.getId());
                    // Since this is a server-side error, it probably does not reflect a problem on the client,
                    // so do not change the PDP status.
                    return;
                }
                // Do the connect
                loggingContext.metricStarted();
                connection.connect();
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet UpdatePDPThread connection connect");
                if (connection.getResponseCode() == 204) {
                    LOGGER.info("Success. We are configured correctly.");
                    loggingContext.transactionEnded();
                    auditLogger.info("Success. PDP is configured correctly.");
                    PolicyLogger.audit("Transaction Success. PDP is configured correctly.");
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);
                } else if (connection.getResponseCode() == 200) {
                    LOGGER.info("Success. PDP needs to update its configuration.");
                    loggingContext.transactionEnded();
                    auditLogger.info("Success. PDP needs to update its configuration.");
                    PolicyLogger.audit("Transaction Success. PDP is configured correctly.");
                    setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
                } else {
                    LOGGER.warn("Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                    loggingContext.transactionEnded();
                    auditLogger.warn("Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                    PolicyLogger.audit("Transaction Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
                }
            } catch (Exception e) {
                LOGGER.debug(e);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Unable to sync config with PDP '" + pdp.getId() + "'");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed: Unable to sync config with PDP '" + pdp.getId() + "': " + e);
                try {
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
                } catch (PAPException e1) {
                    LOGGER.debug(e1);
                    PolicyLogger.audit("Transaction Failed: Unable to set status of PDP " + pdp.getId() + " to UNKNOWN: " + e);
                    PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Unable to set status of PDP '" + pdp.getId() + "' to UNKNOWN");
                }
            } finally {
                // cleanup the connection
                if(connection != null){
                    connection.disconnect();
                }
                // tell the AC to update it's status info
                notifyAC();
            }
        }
    }

    /*
     * RESTful Interface from PAP to ACs notifying them of changes
     */
    private void notifyAC() {
        // kick off a thread to do one event notification for all registered ACs
        // This needs to be on a separate thread so that ACs can make calls back to PAP to get the updated Group data
        // as part of processing this message on their end.
        Thread t = new Thread(new NotifyACThread());
        t.start();
    }

    private class NotifyACThread implements Runnable {
        public void run() {
            List<String> disconnectedACs = new ArrayList<>();
            // There should be no Concurrent exception here because the list is a CopyOnWriteArrayList.
            // The "for each" loop uses the collection's iterator under the covers, so it should be correct.
            for (String acURL : adminConsoleURLStringList) {
                HttpURLConnection connection = null;
                try {
                    acURL += "?PAPNotification=true";
                    //TODO - Currently we just tell AC that "Something changed" without being specific.  Do we want to tell it which group/pdp changed?
                    //TODO - If so, put correct parameters into the Query string here
                    acURL += "&objectType=all" + "&action=update";
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("creating url for id '" + acURL + "'");
                    }
                    //TODO - currently always send both policies and pips.  Do we care enough to add code to allow sending just one or the other?
                    //TODO		(need to change "cache=", implying getting some input saying which to change)
                    URL url = new URL(acURL );
                    // Open up the connection
                    connection = (HttpURLConnection)url.openConnection();
                    // Setup our method and headers
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "text/x-java-properties");
                    // Adding this in. It seems the HttpUrlConnection class does NOT
                    // properly forward our headers for POST re-direction. It does so
                    // for a GET re-direction.
                    // So we need to handle this ourselves.
                    //TODO - is this needed for a PUT?  seems better to leave in for now?
                    connection.setInstanceFollowRedirects(false);
                    // Do not include any data in the PUT because this is just a
                    // notification to the AC.
                    // The AC will use GETs back to the PAP to get what it needs
                    // to fill in the screens.
                    // Do the connect
                    connection.connect();
                    if (connection.getResponseCode() == 204) {
                        LOGGER.info("Success. We updated correctly.");
                    } else {
                        LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
                    }

                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Unable to sync config AC '" + acURL + "'");
                    disconnectedACs.add(acURL);
                } finally {
                    // cleanup the connection
                    if(connection != null)
                        connection.disconnect();
                }
            }
            // remove any ACs that are no longer connected
            if (!disconnectedACs.isEmpty()) {
                adminConsoleURLStringList.removeAll(disconnectedACs);
            }
        }
    }

    private void testService(ONAPLoggingContext loggingContext, HttpServletResponse response) throws IOException{
        LOGGER.info("Test request received");
        try {
            im.evaluateSanity();
            //If we make it this far, all is well
            String message = "GET:/pap/test called and PAP " + papResourceName + " is OK";
            LOGGER.info(message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }catch (ForwardProgressException | AdministrativeStateException | StandbyStatusException e){
            String submsg;
            if (e instanceof ForwardProgressException) {
                submsg = " is not making forward progress.";
            } else if (e instanceof AdministrativeStateException) {
                submsg = " Administrative State is LOCKED.";
            } else {
                submsg = " Standby Status is NOT PROVIDING SERVICE.";
            }

            String message = "GET:/pap/test called and PAP " + papResourceName + submsg
                    + " Exception Message: " + e.getMessage();
            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }catch (Exception e) {
            //A subsystem is not making progress, is locked, standby or is not responding
            String eMsg = e.getMessage();
            if(eMsg == null){
                eMsg = "No Exception Message";
            }
            String message = "GET:/pap/test called and PAP " + papResourceName + " has had a subsystem failure."
                    + " Exception Message: " + eMsg;
            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            //Get the specific list of subsystems that failed
            String ssFailureList = null;
            for(String failedSS : papDependencyGroupsFlatArray){
                if(eMsg.contains(failedSS)){
                    if(ssFailureList == null){
                        ssFailureList = failedSS;
                    }else{
                        ssFailureList = ssFailureList.concat(","+failedSS);
                    }
                }
            }
            if(ssFailureList == null){
                ssFailureList = "UnknownSubSystem";
            }
            response.addHeader("X-ONAP-SubsystemFailure", ssFailureList);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
    }

    /*
     * Authorizing the PEP Requests.
     */
    private boolean authorizeRequest(HttpServletRequest request) {
        String clientCredentials = request.getHeader(ENVIRONMENT_HEADER);
        // Check if the Client is Authorized.
        if(clientCredentials!=null && clientCredentials.equalsIgnoreCase(environment)){
            return true;
        }else{
            return false;
        }
    }

    private static void loadWebapps() throws PAPException{
        if(actionHome == null || configHome == null){
            Path webappsPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS));
            //Sanity Check
            if (webappsPath == null) {
                PolicyLogger.error("Invalid Webapps Path Location property : " + XACMLRestProperties.PROP_PAP_WEBAPPS);
                throw new PAPException("Invalid Webapps Path Location property : " + XACMLRestProperties.PROP_PAP_WEBAPPS);
            }
            Path webappsPathConfig = Paths.get(webappsPath.toString()+File.separator+"Config");
            Path webappsPathAction = Paths.get(webappsPath.toString()+File.separator+"Action");
            if (Files.notExists(webappsPathConfig)) {
                try {
                    Files.createDirectories(webappsPathConfig);
                } catch (IOException e) {
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", "Failed to create config directory: "
                            + webappsPathConfig.toAbsolutePath().toString());
                }
            }
            if (Files.notExists(webappsPathAction)) {
                try {
                    Files.createDirectories(webappsPathAction);
                } catch (IOException e) {
                    LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to create action directory: "
                            + webappsPathAction.toAbsolutePath().toString(), e);
                }
            }
            actionHome = webappsPathAction.toString();
            configHome = webappsPathConfig.toString();
        }
    }

    public static String getConfigHome(){
        try {
            loadWebapps();
        } catch (PAPException e) {
            LOGGER.debug(e);
            return null;
        }
        return configHome;
    }

    private static void setConfigHome(){
        configHome = getConfigHome();
    }

    public static String getActionHome(){
        try {
            loadWebapps();
        } catch (PAPException e) {
            LOGGER.debug(e);
            return null;
        }
        return actionHome;
    }

    private static void setActionHome(){
        actionHome = getActionHome();
    }

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public IntegrityAudit getIa() {
        return ia;
    }

    public static String getPDPFile(){
        return XACMLPapServlet.pdpFile;
    }

    public static String getPersistenceUnit(){
        return PERSISTENCE_UNIT;
    }

    public static PAPPolicyEngine getPAPEngine(){
        return papEngine;
    }

    public static PolicyDBDaoTransaction getDbDaoTransaction(){
        return policyDBDao.getNewTransaction();
    }
    public static String getPapDbDriver() {
        return papDbDriver;
    }

    public static void setPapDbDriver(String papDbDriver) {
        XACMLPapServlet.papDbDriver = papDbDriver;
    }

    public static String getPapDbUrl() {
        return papDbUrl;
    }

    public static void setPapDbUrl(String papDbUrl) {
        XACMLPapServlet.papDbUrl = papDbUrl;
    }

    public static String getPapDbUser() {
        return papDbUser;
    }

    public static void setPapDbUser(String papDbUser) {
        XACMLPapServlet.papDbUser = papDbUser;
    }

    public static String getPapDbPassword() {
        return papDbPassword;
    }

    public static void setPapDbPassword(String papDbPassword) {
        XACMLPapServlet.papDbPassword = papDbPassword;
    }

    public static String getMsOnapName() {
        return msOnapName;
    }

    public static void setMsOnapName(String msOnapName) {
        XACMLPapServlet.msOnapName = msOnapName;
    }

    public static String getMsPolicyName() {
        return msPolicyName;
    }

    public static void setMsPolicyName(String msPolicyName) {
        XACMLPapServlet.msPolicyName = msPolicyName;
    }
}