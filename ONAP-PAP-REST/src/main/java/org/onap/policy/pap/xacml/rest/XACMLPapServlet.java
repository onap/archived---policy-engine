/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
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
import org.onap.policy.common.logging.OnapLoggingContext;
import org.onap.policy.common.logging.OnapLoggingUtils;
import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pap.xacml.rest.components.HandleIncomingNotifications;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDao;
import org.onap.policy.pap.xacml.rest.components.PolicyDbDaoTransaction;
import org.onap.policy.pap.xacml.rest.handler.APIRequestHandler;
import org.onap.policy.pap.xacml.rest.handler.PushPolicyHandler;
import org.onap.policy.pap.xacml.rest.handler.SavePolicyHandler;
import org.onap.policy.pap.xacml.restAuth.CheckPDP;
import org.onap.policy.rest.XacmlRest;
import org.onap.policy.rest.XacmlRestProperties;
import org.onap.policy.rest.dao.PolicyDbException;
import org.onap.policy.utils.PeCryptoUtils;
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

/**
 * Servlet implementation class XacmlPapServlet.
 */
@WebServlet(
    description = "Implements the XACML PAP RESTful API.",
    urlPatterns = {"/"},
    loadOnStartup = 1,
    initParams = {@WebInitParam(
        name = "XACML_PROPERTIES_NAME",
        value = "xacml.pap.properties",
        description = "The location of the properties file holding configuration information.")})
public class XACMLPapServlet extends HttpServlet implements StdItemSetChangeListener, Runnable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = FlexLogger.getLogger(XACMLPapServlet.class);
    // audit (transaction) LOGGER
    private static final Logger auditLogger = FlexLogger.getLogger("auditLogger");
    // Persistence Unit for JPA
    private static final String PERSISTENCE_UNIT = "XACML-PAP-REST";
    private static final String AUDIT_PAP_PERSISTENCE_UNIT = "auditPapPU";
    // Client Headers.
    private static final String ENVIRONMENT_HEADER = "Environment";
    private static final String ADD_GROUP_ERROR = "addGroupError";
    private static final String PERSISTENCE_JDBC_PWD = "javax.persistence.jdbc.password";

    private static final String REGEX = "[0-9a-zA-Z._ ]*";

    /*
     * List of Admin Console URLs. Used to send notifications when configuration changes.
     *
     * The CopyOnWriteArrayList *should* protect from concurrency errors. This list is seldom
     * changed but often read, so the costs of this approach make sense.
     */
    private static final CopyOnWriteArrayList<String> adminConsoleURLStringList = new CopyOnWriteArrayList<>();

    private static String configHome;
    private static String actionHome;
    /*
     * This PAP instance's own URL. Need this when creating URLs to send to the PDPs so they can GET
     * the Policy files from this process.
     */
    private static String papUrl = null;
    // The heartbeat thread.
    private static Heartbeat heartbeat = null;
    private static Thread heartbeatThread = null;
    private static PolicyDbDao policyDbDao;
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
    private static String papDbPd = null;
    private static String papResourceName = null;
    private static String[] papDependencyGroupsFlatArray = null;
    private static String environment = null;
    private static String pdpFile = null;

    private transient IntegrityMonitor im;
    private transient IntegrityAudit ia;

    // MicroService Model Properties
    private static String msOnapName;
    private static String msPolicyName;
    /*
     * This thread may be invoked upon startup to initiate sending PDP policy/pip configuration when
     * this servlet starts. Its configurable by the admin.
     */
    private static transient Thread initiateThread = null;
    private transient OnapLoggingContext baseLoggingContext = null;
    private static final String GROUPID = "groupId";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public XACMLPapServlet() {
        super();
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            // Logging
            baseLoggingContext = new OnapLoggingContext();
            // fixed data that will be the same in all logging output goes here
            try {
                String hostname = InetAddress.getLocalHost().getCanonicalHostName();
                baseLoggingContext.setServer(hostname);
            } catch (UnknownHostException e) {
                LOGGER.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get hostname for logging", e);
            }

            // Initialize
            XacmlRest.xacmlInit(config);
            // Load the properties
            XacmlRest.loadXacmlProperties(null, null);
            /*
             * Retrieve the property values
             */
            setCommonProperties();
            String papSiteName = XACMLProperties.getProperty(XacmlRestProperties.PAP_SITE_NAME);
            if (papSiteName == null) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet",
                    " ERROR: Bad papSiteName property entry");
                throw new PAPException("papSiteName is null");
            }
            String papNodeType = XACMLProperties.getProperty(XacmlRestProperties.PAP_NODE_TYPE);
            if (papNodeType == null) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet",
                    " ERROR: Bad papNodeType property entry");
                throw new PAPException("papNodeType is null");
            }
            // Integer will throw an exception of anything is missing or
            // unrecognized
            int papTransWait = Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_TRANS_WAIT));
            int papTransTimeout =
                Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_TRANS_TIMEOUT));
            int papAuditTimeout =
                Integer.parseInt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_AUDIT_TIMEOUT));
            // Boolean will default to false if anything is missing or
            // unrecognized
            boolean papAuditFlag =
                Boolean.parseBoolean(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_RUN_AUDIT_FLAG));
            boolean papFileSystemAudit =
                Boolean.parseBoolean(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_AUDIT_FLAG));
            String papDependencyGroups = XACMLProperties.getProperty(XacmlRestProperties.PAP_DEPENDENCY_GROUPS);
            if (papDependencyGroups == null) {
                throw new PAPException("papDependencyGroups is null");
            }
            setPAPDependencyGroups(papDependencyGroups);
            // Integer will throw an exception of anything is missing or
            // unrecognized
            int fpMonitorInterval =
                Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.FP_MONITOR_INTERVAL));
            int failedCounterThreshold =
                Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.FAILED_COUNTER_THRESHOLD));
            int testTransInterval =
                Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.TEST_TRANS_INTERVAL));
            int writeFpcInterval =
                Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.WRITE_FPC_INTERVAL));
            LOGGER.debug("\n\n\n**************************************" + "\n*************************************"
                + "\n" + "\n   papDbDriver = " + papDbDriver + "\n   papDbUrl = " + papDbUrl + "\n   papDbUser = "
                + papDbUser + "\n   papTransWait = " + papTransWait + "\n   papTransTimeout = " + papTransTimeout
                + "\n   papAuditTimeout = " + papAuditTimeout + "\n   papAuditFlag = " + papAuditFlag
                + "\n   papFileSystemAudit = " + papFileSystemAudit + "\n	papResourceName = " + papResourceName
                + "\n	fpMonitorInterval = " + fpMonitorInterval + "\n	failedCounterThreshold = "
                + failedCounterThreshold + "\n	testTransInterval = " + testTransInterval + "\n	writeFpcInterval = "
                + writeFpcInterval + "\n	papSiteName = " + papSiteName + "\n	papNodeType = " + papNodeType
                + "\n	papDependencyGroupsList = " + papDependencyGroups + "\n   papIntegrityAuditPeriodSeconds = "
                + papIntegrityAuditPeriodSeconds + "\n\n*************************************"
                + "\n**************************************");
            // Pull custom persistence settings
            Properties properties;
            try {
                properties = XACMLProperties.getProperties();
                LOGGER.debug("\n\n\n**************************************" + "\n**************************************"
                    + "\n\n" + "properties = " + properties + "\n\n**************************************");
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPapServlet",
                    " Error loading properties with: " + "XACMLProperties.getProperties()");
                throw new ServletException(e.getMessage(), e.getCause());
            }
            // Create an IntegrityMonitor
            if (properties.getProperty(PERSISTENCE_JDBC_PWD) != null) {
                properties.setProperty(PERSISTENCE_JDBC_PWD,
                    PeCryptoUtils.decrypt(properties.getProperty(PERSISTENCE_JDBC_PWD, "")));
            }
            im = IntegrityMonitor.getInstance(papResourceName, properties);
            // Create an IntegrityAudit
            ia = new IntegrityAudit(papResourceName, AUDIT_PAP_PERSISTENCE_UNIT, properties);
            ia.startAuditThread();
            // we are about to call the PDPs and give them their configuration.
            // To do that we need to have the URL of this PAP so we can
            // construct the Policy file URLs
            setPapUrl(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_URL));
            // Create the policyDBDao
            setPolicyDbDao();
            // Load our PAP engine, first create a factory
            ONAPPapEngineFactory factory = ONAPPapEngineFactory
                .newInstance(XACMLProperties.getProperty(XACMLProperties.PROP_PAP_PAPENGINEFACTORY));
            // The factory knows how to go about creating a PAP Engine
            setPAPEngine(factory.newEngine());
            if (((org.onap.policy.xacml.std.pap.StdEngine) papEngine).wasDefaultGroupJustAdded) {
                createDefaultGroupOnInit();
            }
            policyDbDao.setPapEngine(XACMLPapServlet.papEngine);
            if (Boolean.parseBoolean(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_RUN_AUDIT_FLAG))) {
                /*
                 * Auditing the local File System groups to be in sync with the Database
                 */

                // get an AuditTransaction to lock out all other transactions
                PolicyDbDaoTransaction auditTrans = policyDbDao.getNewAuditTransaction();

                LOGGER.info("PapServlet: calling auditLocalFileSystem for PDP group audit");
                LOGGER.info("PapServlet: old group is " + papEngine.getDefaultGroup().toString());
                // get the current filesystem group and update from the database
                // if needed
                StdPDPGroup group = (StdPDPGroup) papEngine.getDefaultGroup();
                StdPDPGroup updatedGroup = policyDbDao.auditLocalFileSystem(group);
                if (updatedGroup != null) {
                    papEngine.updateGroup(updatedGroup);
                }
                LOGGER.info("PapServlet:  updated group is " + papEngine.getDefaultGroup().toString());

                // sync up the config data from DB to file system
                LOGGER.info("PapServlet:  Sync config data from DB to file system");
                policyDbDao.synchronizeConfigDataInFileSystem();

                // release the transaction lock
                auditTrans.close();
            }

            // Configurable - have the PAP servlet initiate sending the latest
            // PDP policy/pip configuration
            // to all its known PDP nodes.
            if (Boolean.parseBoolean(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_INITIATE_PDP_CONFIG))) {
                startInitiateThreadService(new Thread(this));
            }
            // After startup, the PAP does Heartbeat's to each of the PDPs
            // periodically
            startHeartBeatService(new Heartbeat(XACMLPapServlet.papEngine));
        } catch (FactoryException | PAPException e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to create engine");
            throw new ServletException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP not initialized; error: " + e);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet",
                " Failed to create engine - unexpected error");
            throw new ServletException(
                XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP not initialized; unexpected error: " + e);
        }

    }

    private void createDefaultGroupOnInit() {
        PolicyDbDaoTransaction addNewGroup = null;
        try {
            addNewGroup = policyDbDao.getNewTransaction();
            OnapPDPGroup group = papEngine.getDefaultGroup();
            addNewGroup.createGroup(group.getId(), group.getName(), group.getDescription(), "automaticallyAdded");
            addNewGroup.commitTransaction();
            addNewGroup = policyDbDao.getNewTransaction();
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

    private static void startInitiateThreadService(Thread thread) {
        initiateThread = thread;
        initiateThread.start();
    }

    @VisibleForTesting
    protected static void mapperWriteValue(ObjectMapper mapper, HttpServletResponse response, Object value) {
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

    private static void setPolicyDbDao() throws ServletException {
        try {
            policyDbDao = PolicyDbDao.getPolicyDbDaoInstance();
        } catch (Exception e) {
            throw new ServletException("Unable to Create Policy DBDao Instance", e);
        }
    }

    public static PolicyDbDao getPolicyDbDao() {
        return policyDbDao;
    }

    private static void setPapUrl(String papUrl) {
        XACMLPapServlet.papUrl = papUrl;
    }

    public static String getPapUrl() {
        return papUrl;
    }

    @VisibleForTesting
    protected static void setPAPEngine(PAPPolicyEngine newEngine) {
        XACMLPapServlet.papEngine = newEngine;
    }

    private static void setPAPDependencyGroups(String papDependencyGroups) throws PAPException {
        try {
            // Now we have flattened the array into a simple comma-separated list
            papDependencyGroupsFlatArray = papDependencyGroups.split("[;,]");
            // clean up the entries
            for (int i = 0; i < papDependencyGroupsFlatArray.length; i++) {
                papDependencyGroupsFlatArray[i] = papDependencyGroupsFlatArray[i].trim();
            }
            try {
                if (XACMLProperties.getProperty(XacmlRestProperties.PAP_INTEGRITY_AUDIT_PERIOD_SECONDS) != null) {
                    papIntegrityAuditPeriodSeconds = Integer.parseInt(
                        XACMLProperties.getProperty(XacmlRestProperties.PAP_INTEGRITY_AUDIT_PERIOD_SECONDS).trim());
                }
            } catch (Exception e) {
                String msg = "integrity_audit_period_seconds ";
                LOGGER.error("\n\nERROR: " + msg + "Bad property entry: " + e.getMessage() + "\n");
                PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet",
                    " ERROR: " + msg + "Bad property entry");
                throw e;
            }
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
            throw new PAPException(e);
        }
    }

    private static void setCommonProperties() throws PAPException {
        setConfigHome();
        setActionHome();
        papDbDriver = XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_DB_DRIVER);
        if (papDbDriver == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet",
                " ERROR: Bad papDbDriver property entry");
            throw new PAPException("papDbDriver is null");
        }
        setPapDbDriver(papDbDriver);
        papDbUrl = XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_DB_URL);
        if (papDbUrl == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet", " ERROR: Bad papDbUrl property entry");
            throw new PAPException("papDbUrl is null");
        }
        setPapDbUrl(papDbUrl);
        papDbUser = XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_DB_USER);
        if (papDbUser == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet",
                " ERROR: Bad papDbUser property entry");
            throw new PAPException("papDbUser is null");
        }
        setPapDbUser(papDbUser);
        PeCryptoUtils.initAesKey(XACMLProperties.getProperty(XacmlRestProperties.PROP_AES_KEY));
        papDbPd = PeCryptoUtils.decrypt(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_DB_PASSWORD));
        if (papDbPd == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet",
                " ERROR: Bad papDbPassword property entry");
            throw new PAPException("papDbPassword is null");
        }
        setPapDbPassword(papDbPd);
        papResourceName = XACMLProperties.getProperty(XacmlRestProperties.PAP_RESOURCE_NAME);
        if (papResourceName == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "XACMLPapServlet",
                " ERROR: Bad papResourceName property entry");
            throw new PAPException("papResourceName is null");
        }
        environment = XACMLProperties.getProperty("ENVIRONMENT", "DEVL");
        // Micro Service Properties
        msOnapName = XACMLProperties.getProperty("xacml.policy.msOnapName");
        setMsOnapName(msOnapName);
        msPolicyName = XACMLProperties.getProperty("xacml.policy.msPolicyName");
        setMsPolicyName(msPolicyName);
        // PDPId File location
        XACMLPapServlet.pdpFile = XACMLProperties.getProperty(XacmlRestProperties.PROP_PDP_IDFILE);
        if (XACMLPapServlet.pdpFile == null) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " The PDP Id Authentication File Property is not valid: "
                + XacmlRestProperties.PROP_PDP_IDFILE);
            throw new PAPException(
                "The PDP Id Authentication File Property :" + XacmlRestProperties.PROP_PDP_IDFILE + " is not Valid. ");
        }
    }

    /**
     * Thread used only during PAP startup to initiate change messages to all known PDPs. This must
     * be on a separate thread so that any GET requests from the PDPs during this update can be
     * serviced.
     */
    @Override
    public void run() {
        // send the current configuration to all the PDPs that we know about
        changed();
    }

    /**
     * @see Servlet#destroy()
     *      <p>
     *      Depending on how this servlet is run, we may or may not care about cleaning up the
     *      resources. For now we assume that we do care.
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
        // reset pap url
        setPapUrl(null);
    }

    private ConsoleAndApiService getAcServiceInstance() {
        return new ConsoleAndApiService();
    }

    /**
     * Called by: - PDP nodes to register themselves with the PAP, and - Admin Console to make
     * changes in the PDP Groups.
     *
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        OnapLoggingContext loggingContext = OnapLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        setLoggingContext(loggingContext, "doPost", "PAP.post");
        PolicyDbDaoTransaction pdpTransaction = null;
        try {
            loggingContext.metricStarted();
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
        } catch (AdministrativeStateException ae) {
            String message =
                "POST interface called for PAP " + papResourceName + " but it has an Administrative state of "
                    + im.getStateManager().getAdminState() + "\n Exception Message: " + PolicyUtils.CATCH_EXCEPTION;
            LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message, ae);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        } catch (IntegrityMonitorException ime) {
            String message =
                "POST interface called for PAP " + papResourceName + " but it has an Administrative state of "
                    + im.getStateManager().getAdminState() + " and a Standby Status of "
                    + im.getStateManager().getStandbyStatus() + "\n Exception Message: " + ime.getMessage();
            LOGGER.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message, ime);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost im startTransaction");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
        try {
            loggingContext.metricStarted();
            XacmlRest.dumpRequest(request);
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPost dumpRequest");
            // since getParameter reads the content string, explicitly get the
            // content before doing that.
            // Simply getting the inputStream seems to protect it against being
            // consumed by getParameter.
            request.getInputStream();
            String groupId = request.getParameter(GROUPID);
            String apiflag = request.getParameter("apiflag");
            if (groupId != null) {
                // Is this from the Admin Console or API?
                if (apiflag != null && "api".equalsIgnoreCase(apiflag)) {
                    // this is from the API so we need to check the client
                    // credentials before processing the request
                    if (!authorizeRequest(request)) {
                        String message = "PEP not Authorized for making this Request!!";
                        PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                        loggingContext.transactionEnded();
                        PolicyLogger.audit("Transaction Failed - See Error.log");
                        setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
                        im.endTransaction();
                        return;
                    }
                }
                loggingContext.metricStarted();
                getAcServiceInstance().doAcPost(request, response, groupId, loggingContext, papEngine);
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
                if (CheckPDP.validateID(id)) {
                    pdpTransaction = policyDbDao.getNewTransaction();
                    try {
                        pdpTransaction.addPdpToGroup(id, XACMLPapServlet.papEngine.getDefaultGroup().getId(), id,
                            "Registered on first startup", Integer.parseInt(jmxport), "PDP autoregister");
                        XACMLPapServlet.papEngine.newPDP(id, XACMLPapServlet.papEngine.getDefaultGroup(), id,
                            "Registered on first startup", Integer.parseInt(jmxport));
                    } catch (NullPointerException | PAPException | IllegalArgumentException | IllegalStateException
                        | PersistenceException | PolicyDbException e) {
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
                    try {
                        pdp = XACMLPapServlet.papEngine.getPDP(id);
                    } catch (PAPException e) {
                        LOGGER.error(e);
                    }
                    if (pdp == null) {
                        if (pdpTransaction != null) {
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
                    String message = "PDP is Unauthorized to Connect to PAP: " + id;
                    loggingContext.transactionEnded();
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "PDP not Authorized to connect to this PAP. Please contact the PAP Admin for registration.");
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    im.endTransaction();
                    return;
                }
                try {
                    loggingContext.metricStarted();
                    pdpTransaction.commitTransaction();
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doPost commitTransaction");
                } catch (Exception e) {
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet",
                        "Could not commit transaction to put pdp in the database");
                }
            }
            if (jmxport != null && !"".equals(jmxport)) {
                try {
                    ((StdPDP) pdp).setJmxPort(Integer.valueOf(jmxport));
                } catch (NumberFormatException e) {
                    LOGGER.error(e);
                }
            }
            // Get the PDP's Group
            OnapPDPGroup group = null;
            try {
                group = XACMLPapServlet.papEngine.getPDPGroup(pdp);
            } catch (PAPException e) {
                LOGGER.error(e);
            }
            if (group == null) {
                PolicyLogger
                    .error(MessageCodes.ERROR_PROCESS_FLOW + " PDP not associated with any group, even the default");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "PDP not associated with any group, even the default");
                im.endTransaction();
                return;
            }
            // Determine what group the PDP node is in and get
            // its policy/pip properties.
            Properties policies = group.getPolicyProperties();
            Properties pipconfig = group.getPipConfigProperties();
            // Get the current policy/pip configuration that the PDP has
            Properties pdpProperties = new Properties();
            try {
                pdpProperties.load(request.getInputStream());
            } catch (IOException e) {
                LOGGER.error(e);
            }
            LOGGER.info("PDP Current Properties: " + pdpProperties.toString());
            LOGGER.info("Policies: " + (policies != null ? policies.toString() : "null"));
            LOGGER.info("Pip config: " + (pipconfig != null ? pipconfig.toString() : "null"));
            // Validate the node's properties
            boolean isCurrent = this.isPDPCurrent(policies, pipconfig, pdpProperties);
            // Send back current configuration
            if (!isCurrent) {
                // Tell the PDP we are sending back the current policies/pip
                // config
                LOGGER.info("PDP configuration NOT current.");
                if (policies != null) {
                    // Put URL's into the properties in case the PDP needs to
                    // retrieve them.
                    this.populatePolicyURL(request.getRequestURL(), policies);
                    // Copy the properties to the output stream
                    try {
                        policies.store(response.getOutputStream(), "");
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                }
                if (pipconfig != null) {
                    // Copy the properties to the output stream
                    try {
                        pipconfig.store(response.getOutputStream(), "");
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                }
                // We are good - and we are sending them information
                response.setStatus(HttpServletResponse.SC_OK);
                try {
                    setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
                } catch (PAPException e) {
                    LOGGER.error(e);
                }
            } else {
                // Tell them they are good
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                try {
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);
                } catch (PAPException e) {
                    LOGGER.error(e);
                }
            }
            auditLogger.info("Success");
            PolicyLogger.audit("Transaction Ended Successfully");
        } catch (PAPException | IOException | NumberFormatException e) {
            if (pdpTransaction != null) {
                pdpTransaction.rollbackTransaction();
            }
            LOGGER.debug(XACMLErrorConstants.ERROR_PROCESS_FLOW + "POST exception: " + e, e);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            im.endTransaction();
            return;
        }
        // Catch anything that fell through
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Ended");
        im.endTransaction();
    }

    private void setResponseError(HttpServletResponse response, int responseCode, String message) {
        try {
            if (message != null && !message.isEmpty()) {
                response.sendError(responseCode, message);
            }
        } catch (IOException e) {
            LOGGER.error("Error setting Error response Header ", e);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        OnapLoggingContext loggingContext = OnapLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        setLoggingContext(loggingContext, "doGet", "PAP.get");
        loggingContext.metricStarted();
        XacmlRest.dumpRequest(request);
        loggingContext.metricEnded();
        PolicyLogger.metrics("XACMLPapServlet doGet dumpRequest");
        String pathInfo = request.getRequestURI();
        LOGGER.info("path info: " + pathInfo);
        if (pathInfo != null && "/pap/test".equals(pathInfo)) {
            // DO NOT do a im.startTransaction for the test request
            try {
                testService(loggingContext, response);
            } catch (IOException e) {
                LOGGER.debug(e);
            }
            return;
        }
        // This im.startTransaction() covers all other Get transactions
        try {
            loggingContext.metricStarted();
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doGet im startTransaction");
        } catch (IntegrityMonitorException ime) {
            String message =
                "GET interface called for PAP " + papResourceName + " but it has an Administrative state of "
                    + im.getStateManager().getAdminState() + " and a Standby Status of "
                    + im.getStateManager().getStandbyStatus() + "\n Exception Message: " + ime.getMessage();
            LOGGER.info(message, ime);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
        // Request from the API to get the gitPath
        String apiflag = request.getParameter("apiflag");
        if (apiflag != null) {
            if (authorizeRequest(request)) {
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try {
                    loggingContext.metricStarted();
                    apiRequestHandler.doGet(request, response, apiflag);
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doGet apiRequestHandler doGet");
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            } else {
                String message =
                    "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
                im.endTransaction();
                return;
            }
        }
        // Is this from the Admin Console?
        String groupId = request.getParameter(GROUPID);
        if (groupId != null) {
            // this is from the Admin Console, so handle separately
            try {
                loggingContext.metricStarted();
                getAcServiceInstance().doAcGet(request, response, groupId, loggingContext, papEngine);
                loggingContext.metricEnded();
                PolicyLogger.metrics("XACMLPapServlet doGet doACGet");
            } catch (IOException e) {
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
        try {
            pdp = XACMLPapServlet.papEngine.getPDP(id);
        } catch (PAPException e) {
            LOGGER.error(e);
        }
        // Is it known?
        if (pdp == null) {
            // Check if request came from localhost
            if ("localhost".equals(request.getRemoteHost()) || request.getRemoteHost().equals(request.getLocalAddr())) {
                // Return status information - basically all the groups
                loggingContext.setServiceName("PAP.getGroups");
                Set<OnapPDPGroup> groups = null;
                try {
                    groups = papEngine.getOnapPDPGroups();
                } catch (PAPException e) {
                    LOGGER.debug(e);
                    PolicyLogger.error(MessageCodes.ERROR_UNKNOWN, e, "XACMLPapServlet", " GET exception");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    im.endTransaction();
                    return;
                }
                // convert response object to JSON and include in the response
                mapperWriteValue(new ObjectMapper(), response, groups);
                response.setHeader("content-type", "application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            }
            String message =
                "Unknown PDP: " + id + " from " + request.getRemoteHost() + " us: " + request.getLocalAddr();
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
            group = XACMLPapServlet.papEngine.getPDPGroup(pdp);
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
            setResponseError(response, HttpServletResponse.SC_NOT_FOUND, message);
            im.endTransaction();
            return;
        }
        PDPPolicy policy = group.getPolicy(policyId);
        if (policy == null) {
            String message = "Unknown policy: " + policyId;
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_NOT_FOUND, message);
            im.endTransaction();
            return;
        }
        try {
            LOGGER.warn("PolicyDebugging: Policy Validity: " + policy.isValid() + "\n " + "Policy Name : "
                + policy.getName() + "\n Policy URI: " + policy.getLocation().toString());
        } catch (PAPException | IOException e) {
            LOGGER.error(e);
        }
        try (
            InputStream is =
                new FileInputStream(((StdPDPGroup) group).getDirectory().toString() + File.separator + policyId);
            OutputStream os = response.getOutputStream()) {
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
            setResponseError(response, HttpServletResponse.SC_NOT_FOUND, message);
        }
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Ended");
        im.endTransaction();
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        OnapLoggingContext loggingContext = OnapLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        setLoggingContext(loggingContext, "doPut", "PAP.put");
        try {
            loggingContext.metricStarted();
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doPut im startTransaction");
        } catch (IntegrityMonitorException e) {
            String message =
                "PUT interface called for PAP " + papResourceName + " but it has an Administrative state of "
                    + im.getStateManager().getAdminState() + " and a Standby Status of "
                    + im.getStateManager().getStandbyStatus() + "\n Exception Message: " + e.getMessage();
            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }

        loggingContext.metricStarted();
        // need to check if request is from the API or Admin console
        String apiflag = request.getParameter("apiflag");
        // For Debug purposes
        if (!"api".equals(apiflag) && PolicyLogger.isDebugEnabled()) {
            XacmlRest.dumpRequest(request);
            PolicyLogger.metrics("XACMLPapServlet doPut dumpRequest");
        }
        loggingContext.metricEnded();

        // This would occur if a PolicyDBDao notification was received
        String policyDBDaoRequestUrl = request.getParameter("policydbdaourl");
        if (policyDBDaoRequestUrl != null) {
            LOGGER.info("XACMLPapServlet: PolicyDBDao Notification received.");
            String policyDBDaoRequestEntityId = request.getParameter("entityid");
            String policyDBDaoRequestEntityType = request.getParameter("entitytype");
            String policyDBDaoRequestExtraData = request.getParameter("extradata");
            if (policyDBDaoRequestEntityId == null || policyDBDaoRequestEntityType == null) {
                setResponseError(response, 400, "entityid or entitytype not supplied");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Ended Successfully");
                im.endTransaction();
                return;
            }
            loggingContext.metricStarted();
            LOGGER.info("XACMLPapServlet: Calling PolicyDBDao to handlIncomingHttpNotification");
            HandleIncomingNotifications handleIncomingNotifications = new HandleIncomingNotifications();
            handleIncomingNotifications.handleIncomingHttpNotification(policyDBDaoRequestUrl,
                policyDBDaoRequestEntityId, policyDBDaoRequestEntityType, policyDBDaoRequestExtraData, this);
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
            if (authorizeRequest(request)) {
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try {
                    loggingContext.metricStarted();
                    apiRequestHandler.doPut(request, response, importService);
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doPut apiRequestHandler doPut");
                } catch (IOException e) {
                    LOGGER.error(e);
                }
                im.endTransaction();
                return;
            } else {
                String message =
                    "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS + message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
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
            if (!adminConsoleURLStringList.contains(acURLString)) {
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
         * This is to update the PDP Group with the policy/policies being pushed Part of a 2 step
         * process to push policies to the PDP that can now be done From both the Admin Console and
         * the PolicyEngine API
         */
        String groupId = request.getParameter(GROUPID);
        if (groupId != null) {
            if (apiflag != null) {
                if (!authorizeRequest(request)) {
                    String message =
                        "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
                    return;
                }
                if (apiflag.equalsIgnoreCase("addPolicyToGroup")) {
                    try {
                        updateGroupsFromAPI(request, response, groupId, loggingContext);
                    } catch (IOException e) {
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
                getAcServiceInstance().doAcPut(request, response, groupId, loggingContext, papEngine);
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
        if (apiflag != null && apiflag.equalsIgnoreCase("admin")) {
            // this request is from the Admin Console
            SavePolicyHandler savePolicyHandler = SavePolicyHandler.getInstance();
            try {
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
            if (authorizeRequest(request)) {
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try {
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
                setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
                im.endTransaction();
                return;
            }
        }
        // We do not expect anything from anywhere else.
        // This method is here in case we ever need to support other operations.
        LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Request does not have groupId or apiflag");
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Failed - See Error.log");
        setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId or apiflag");
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Failed - See error.log");
        im.endTransaction();
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        OnapLoggingContext loggingContext = OnapLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        setLoggingContext(loggingContext, "doDelete", "PAP.delete");
        try {
            loggingContext.metricStarted();
            im.startTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet doDelete im startTransaction");
        } catch (IntegrityMonitorException ime) {
            String message =
                "DELETE interface called for PAP " + papResourceName + " but it has an Administrative state of "
                    + im.getStateManager().getAdminState() + " and a Standby Status of "
                    + im.getStateManager().getStandbyStatus() + "\n Exception Message: " + ime.getMessage();
            LOGGER.info(message, ime);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
        loggingContext.metricStarted();
        XacmlRest.dumpRequest(request);
        loggingContext.metricEnded();
        PolicyLogger.metrics("XACMLPapServlet doDelete dumpRequest");
        String groupId = request.getParameter(GROUPID);
        String apiflag = request.getParameter("apiflag");
        if (groupId != null) {
            // Is this from the Admin Console or API?
            if (apiflag != null) {
                if (!authorizeRequest(request)) {
                    String message =
                        "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                    PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    setResponseError(response, HttpServletResponse.SC_FORBIDDEN, message);
                    return;
                }
                APIRequestHandler apiRequestHandler = new APIRequestHandler();
                try {
                    loggingContext.metricStarted();
                    apiRequestHandler.doDelete(request, response, loggingContext, apiflag);
                    loggingContext.metricEnded();
                    PolicyLogger.metrics("XACMLPapServlet doDelete apiRequestHandler doDelete");
                } catch (Exception e) {
                    LOGGER.error("Exception Occured" + e);
                }
                if (apiRequestHandler.getNewGroup() != null) {
                    groupChanged(apiRequestHandler.getNewGroup(), loggingContext);
                }
                return;
            }
            // this is from the Admin Console, so handle separately
            try {
                loggingContext.metricStarted();
                getAcServiceInstance().doAcDelete(request, response, groupId, loggingContext, papEngine);
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
        // Catch anything that fell through
        PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Request does not have groupId");
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Failed - See Error.log");
        setResponseError(response, HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId");
        im.endTransaction();
    }

    private boolean isPDPCurrent(Properties policies, Properties pipconfig, Properties pdpProperties) {
        String localRootPolicies = policies.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        String localReferencedPolicies = policies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        if (localRootPolicies == null || localReferencedPolicies == null) {
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing property on PAP server: RootPolicies="
                + localRootPolicies + "  ReferencedPolicies=" + localReferencedPolicies);
            return false;
        }
        // Compare the policies and pipconfig properties to the pdpProperties
        try {
            // the policy properties includes only xacml.rootPolicies and
            // xacml.referencedPolicies without any .url entries
            Properties pdpPolicies = XACMLProperties.getPolicyProperties(pdpProperties, false);
            Properties pdpPipConfig = XACMLProperties.getPipProperties(pdpProperties);
            if (localRootPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_ROOTPOLICIES))
                && localReferencedPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES))
                && pdpPipConfig.equals(pipconfig)) {
                // The PDP is current
                return true;
            }
        } catch (Exception e) {
            // we get here if the PDP did not include either xacml.rootPolicies
            // or xacml.pip.engines,
            // or if there are policies that do not have a corresponding ".url"
            // property.
            // Either of these cases means that the PDP is not up-to-date, so
            // just drop-through to return false.
            PolicyLogger.error(MessageCodes.ERROR_SCHEMA_INVALID, e, "XACMLPapServlet", " PDP Error");
        }
        return false;
    }

    @VisibleForTesting
    protected void populatePolicyURL(StringBuffer urlPath, Properties policies) {
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
        String pdpURL = request.getHeader(XacmlRestProperties.PROP_PDP_HTTP_HEADER_ID);
        if (pdpURL == null || pdpURL.isEmpty()) {
            // Should send back its port for identification
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP did not send custom header");
            pdpURL = "";
        }
        return pdpURL;
    }

    protected String getPDPJMX(HttpServletRequest request) {
        String pdpJMMX = request.getHeader(XacmlRestProperties.PROP_PDP_HTTP_HEADER_JMX_PORT);
        if (pdpJMMX == null || pdpJMMX.isEmpty()) {
            // Should send back its port for identification
            LOGGER.warn(XACMLErrorConstants.ERROR_DATA_ISSUE
                + "PDP did not send custom header for JMX Port so the value of 0 is assigned");
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
    public void updateGroupsFromAPI(HttpServletRequest request, HttpServletResponse response, String groupId,
        OnapLoggingContext loggingContext) throws IOException {
        PolicyDbDaoTransaction acPutTransaction = policyDbDao.getNewTransaction();
        PolicyLogger.audit("PolicyDBDaoTransaction started for updateGroupsFromAPI");
        try {
            String userId = request.getParameter("userId");
            // for PUT operations the group may or may not need to exist before
            // the operation can be done
            StdPDPGroup group = (StdPDPGroup) papEngine.getGroup(groupId);

            // get the request input stream content into a String
            String json = null;
            java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
            scanner.useDelimiter("\\A");
            json = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            PolicyLogger.info("pushPolicy request from API: " + json);

            // convert Object sent as JSON into local object
            StdPDPPolicy policy = PolicyUtils.jsonStringToObject(json, StdPDPPolicy.class);

            // Get the current policies from the Group and Add the new one
            // If the selected policy is in the group we must remove the old
            // version of it
            LOGGER.info("Removing old version of the policy");
            for (PDPPolicy existingPolicy : group.getPolicies()) {
                if (existingPolicy.getName().equals(policy.getName())
                    && !existingPolicy.getId().equals(policy.getId())) {
                    group.removePolicy(existingPolicy);
                    LOGGER.info("Removing policy: " + existingPolicy);
                    break;
                }
            }

            // Assume that this is an update of an existing PDP Group
            loggingContext.setServiceName("PolicyEngineAPI:PAP.updateGroup");
            try {
                acPutTransaction.updateGroup(group, "XACMLPapServlet.doACPut", userId);
            } catch (Exception e) {
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet",
                    " Error while updating group in the database: " + "group=" + group.getId());
                throw new PAPException(e.getMessage());
            }

            LOGGER.info("Calling updatGroup() with new group");
            papEngine.updateGroup(group);
            String policyId = "empty";
            if (policy != null && policy.getId() != null) {
                policyId = policy.getId();
            }
            if (!policyId.matches(REGEX)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.addHeader("error", ADD_GROUP_ERROR);
                response.addHeader("message", "Policy Id is not valid");
                return;
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.addHeader("operation", "push");
            response.addHeader("policyId", policyId);
            response.addHeader(GROUPID, groupId);

            LOGGER.info("Group '" + group.getId() + "' updated");

            loggingContext.metricStarted();
            acPutTransaction.commitTransaction();
            loggingContext.metricEnded();
            PolicyLogger.metrics("XACMLPapServlet updateGroupsFromAPI commitTransaction");

            // Group changed, which might include changing the policies
            groupChanged(group, loggingContext);
            loggingContext.transactionEnded();
            LOGGER.info("Success");

            if (policy != null
                && ((policy.getId().contains("Config_MS_")) || (policy.getId().contains("BRMS_Param")))) {
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
            String message = XACMLErrorConstants.ERROR_PROCESS_FLOW
                + "Exception in request to update group from API - See Error.log on on the PAP.";
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.addHeader("error", ADD_GROUP_ERROR);
            response.addHeader("message", message);
            return;
        }
    }

    /*
     * HELPER to change Group status when PDP status is changed (Must NOT be called from a method
     * that is synchronized on the papEngine or it may deadlock)
     */
    public void setPDPSummaryStatus(OnapPDP pdp, PDPStatus.Status newStatus) throws PAPException {
        setPDPSummaryStatus(pdp, newStatus.toString());
    }

    public void setPDPSummaryStatus(OnapPDP pdp, String newStatus) throws PAPException {
        synchronized (papEngine) {
            StdPDPStatus status = new StdPDPStatus();
            status.setStatus(PDPStatus.Status.valueOf(newStatus));
            ((StdPDP) pdp).setStatus(status);
            // now adjust the group
            StdPDPGroup group = (StdPDPGroup) papEngine.getPDPGroup(pdp);
            // if the PDP was just deleted it may transiently exist but not be
            // in a group
            if (group != null) {
                group.resetStatus();
            }
        }
    }

    /*
     * Callback methods telling this servlet to notify PDPs of changes made by the PAP StdEngine in
     * the PDP group directories
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

    public void changed(OnapLoggingContext loggingContext) {
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
            pdpChanged(pdp, getPdpDataByGroup(group));
        }
    }

    public void groupChanged(OnapPDPGroup group, OnapLoggingContext loggingContext) {
        // all PDPs within one group need to be updated/sync'd
        for (OnapPDP pdp : group.getOnapPdps()) {
            pdpChanged(pdp, loggingContext, getPdpDataByGroup(group));
        }
    }

    @Override
    public void pdpChanged(OnapPDP pdp) {
        // kick off a thread to do an event notification for each PDP.
        // This needs to be on a separate thread so that PDPs that do not
        // respond (down, non-existent, etc)
        // do not block the PSP response to the AC, which would freeze the GUI
        // until all PDPs sequentially respond or time-out.
        Thread t = new Thread(new UpdatePdpThread(pdp, getPdpDataByPdpId(pdp)));
        if (CheckPDP.validateID(pdp.getId())) {
            t.start();
        }
    }

    public void pdpChanged(OnapPDP pdp, OnapLoggingContext loggingContext) {
        // kick off a thread to do an event notification for each PDP.
        // This needs to be on a separate thread so that PDPs that do not
        // respond (down, non-existent, etc)
        // do not block the PSP response to the AC, which would freeze the GUI
        // until all PDPs sequentially respond or time-out.
        Thread t = new Thread(new UpdatePdpThread(pdp, loggingContext, getPdpDataByPdpId(pdp)));
        if (CheckPDP.validateID(pdp.getId())) {
            t.start();
        }
    }

    private void pdpChanged(OnapPDP pdp, List<Properties> pdpDataByGroup) {
        Thread t = new Thread(new UpdatePdpThread(pdp, pdpDataByGroup));
        if (CheckPDP.validateID(pdp.getId())) {
            t.start();
        }
    }

    private void pdpChanged(OnapPDP pdp, OnapLoggingContext loggingContext, List<Properties> pdpDataByGroup) {
        Thread t = new Thread(new UpdatePdpThread(pdp, loggingContext, pdpDataByGroup));
        if (CheckPDP.validateID(pdp.getId())) {
            t.start();
        }
    }

    private List<Properties> getPdpDataByGroup(OnapPDPGroup group) {
        DataToNotifyPdp dataToNotify = new DataToNotifyPdp();
        return dataToNotify.setPolicyConfigProperties(group);
    }

    private List<Properties> getPdpDataByPdpId(OnapPDP pdp) {
        DataToNotifyPdp dataToNotify = new DataToNotifyPdp();
        return dataToNotify.setPolicyConfigProperties(pdp, papEngine);
    }

    private void testService(OnapLoggingContext loggingContext, HttpServletResponse response) throws IOException {
        LOGGER.info("Test request received");
        try {
            im.evaluateSanity();
            // If we make it this far, all is well
            String message = "GET:/pap/test called and PAP " + papResourceName + " is OK";
            LOGGER.info(message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        } catch (ForwardProgressException | AdministrativeStateException | StandbyStatusException e) {
            String submsg;
            if (e instanceof ForwardProgressException) {
                submsg = " is not making forward progress.";
            } else if (e instanceof AdministrativeStateException) {
                submsg = " Administrative State is LOCKED.";
            } else {
                submsg = " Standby Status is NOT PROVIDING SERVICE.";
            }

            String message =
                "GET:/pap/test called and PAP " + papResourceName + submsg + " Exception Message: " + e.getMessage();
            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        } catch (Exception e) {
            // A subsystem is not making progress, is locked, standby or is not
            // responding
            String eMsg = e.getMessage();
            if (eMsg == null) {
                eMsg = "No Exception Message";
            }
            String message = "GET:/pap/test called and PAP " + papResourceName + " has had a subsystem failure."
                + " Exception Message: " + eMsg;
            LOGGER.info(message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            // Get the specific list of subsystems that failed
            String ssFailureList = null;
            for (String failedSS : papDependencyGroupsFlatArray) {
                if (eMsg.contains(failedSS)) {
                    if (ssFailureList == null) {
                        ssFailureList = failedSS;
                    } else {
                        ssFailureList = ssFailureList.concat("," + failedSS);
                    }
                }
            }
            if (ssFailureList == null) {
                ssFailureList = "UnknownSubSystem";
            }
            response.addHeader("X-ONAP-SubsystemFailure", ssFailureList);
            setResponseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
    }

    private void setLoggingContext(OnapLoggingContext loggingContext, String methodType, String serviceName) {
        loggingContext.transactionStarted();
        loggingContext.setServiceName(serviceName);
        if (loggingContext.getRequestId() == null || "".equals(loggingContext.getRequestId())) {
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestId(requestID.toString());
            PolicyLogger
                .info("requestID not provided in call to XACMLPapServlet ('" + methodType + "') so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPapServlet ('" + methodType + "')");
        }
    }

    /*
     * Authorizing the PEP Requests.
     */
    private boolean authorizeRequest(HttpServletRequest request) {
        String clientCredentials = request.getHeader(ENVIRONMENT_HEADER);
        // Check if the Client is Authorized.
        return clientCredentials != null && clientCredentials.equalsIgnoreCase(environment);
    }

    private static void loadWebapps() throws PAPException {
        if (actionHome == null || configHome == null) {
            Path webappsPath = Paths.get(XACMLProperties.getProperty(XacmlRestProperties.PROP_PAP_WEBAPPS));
            // Sanity Check
            if (webappsPath == null) {
                PolicyLogger.error("Invalid Webapps Path Location property : " + XacmlRestProperties.PROP_PAP_WEBAPPS);
                throw new PAPException(
                    "Invalid Webapps Path Location property : " + XacmlRestProperties.PROP_PAP_WEBAPPS);
            }
            Path webappsPathConfig = Paths.get(webappsPath.toString() + File.separator + "Config");
            Path webappsPathAction = Paths.get(webappsPath.toString() + File.separator + "Action");
            if (Files.notExists(webappsPathConfig)) {
                try {
                    Files.createDirectories(webappsPathConfig);
                } catch (IOException e) {
                    PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet",
                        "Failed to create config directory: " + webappsPathConfig.toAbsolutePath().toString());
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

    public static String getConfigHome() {
        try {
            loadWebapps();
        } catch (PAPException e) {
            LOGGER.debug(e);
            return null;
        }
        return configHome;
    }

    private static void setConfigHome() {
        configHome = getConfigHome();
    }

    public static String getActionHome() {
        try {
            loadWebapps();
        } catch (PAPException e) {
            LOGGER.debug(e);
            return null;
        }
        return actionHome;
    }

    private static void setActionHome() {
        actionHome = getActionHome();
    }

    public IntegrityAudit getIa() {
        return ia;
    }

    public static String getPDPFile() {
        return XACMLPapServlet.pdpFile;
    }

    public static String getPersistenceUnit() {
        return PERSISTENCE_UNIT;
    }

    public static PAPPolicyEngine getPAPEngine() {
        return papEngine;
    }

    public static PolicyDbDaoTransaction getDbDaoTransaction() {
        return policyDbDao.getNewTransaction();
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
        return papDbPd;
    }

    public static void setPapDbPassword(String papDbPassword) {
        XACMLPapServlet.papDbPd = papDbPassword;
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

    public OnapLoggingContext getBaseLoggingContext() {
        return baseLoggingContext;
    }

    public void setBaseLoggingContext(OnapLoggingContext baseLoggingContext) {
        this.baseLoggingContext = baseLoggingContext;
    }
}
