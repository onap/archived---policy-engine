/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
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

package org.onap.policy.pdp.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.onap.policy.api.PolicyParameters;
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
import org.onap.policy.pdp.rest.jmx.PdpRestMonitor;
import org.onap.policy.rest.XACMLRest;
import org.onap.policy.rest.XACMLRestProperties;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.pdp.std.functions.PolicyList;
import org.onap.policy.xacml.std.pap.StdPDPStatus;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pap.PDPStatus.Status;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMResponse;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class XacmlPdpServlet
 * 
 * This is an implementation of the XACML 3.0 RESTful Interface with added features to support simple PAP RESTful API
 * for policy publishing and PIP configuration changes.
 * 
 * If you are running this the first time, then we recommend you look at the xacml.pdp.properties file. This properties
 * file has all the default parameter settings. If you are running the servlet as is, then we recommend setting up
 * you're container to run it on port 8080 with context "/pdp". Wherever the default working directory is set to, a
 * "config" directory will be created that holds the policy and pip cache. This setting is located in the
 * xacml.pdp.properties file.
 * 
 * When you are ready to customize, you can create a separate xacml.pdp.properties on you're local file system and setup
 * the parameters as you wish. Just set the Java VM System variable to point to that file:
 * 
 * -Dxacml.properties=/opt/app/xacml/etc/xacml.pdp.properties
 * 
 * Or if you only want to change one or two properties, simply set the Java VM System variable for that property.
 * 
 * -Dxacml.rest.pdp.register=false
 *
 *
 */
@WebServlet(description = "Implements the XACML PDP RESTful API and client PAP API.", urlPatterns = {"/"},
        loadOnStartup = 1, initParams = {@WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.pdp.properties",
                description = "The location of the PDP xacml.pdp.properties file holding configuration information.")})
public class XACMLPdpServlet extends HttpServlet implements Runnable {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_MAX_CONTENT_LENGTH = "999999999"; // 32767
    private static final String CREATE_UPDATE_POLICY_SERVICE =
            "org.onap.policy.pdp.rest.api.services.CreateUpdatePolicyServiceImpl";
    //
    // Our application debug log
    //
    private static final Log logger = LogFactory.getLog(XACMLPdpServlet.class);
    //
    // This logger is specifically only for Xacml requests and their corresponding response.
    // It's output ideally should be sent to a separate file from the application logger.
    //
    private static final Log requestLogger = LogFactory.getLog("xacml.request");
    //
    // audit logger
    private static final Log auditLogger = LogFactory.getLog("auditLogger");

    public static final PdpRestMonitor monitor = PdpRestMonitor.getSingleton();

    //
    // This thread may getting invoked on startup, to let the PAP know
    // that we are up and running.
    //
    private static transient Thread registerThread = null;
    private static transient XACMLPdpRegisterThread registerRunnable = null;
    //
    // This is our PDP engine pointer. There is a synchronized lock used
    // for access to the pointer. In case we are servicing PEP requests while
    // an update is occurring from the PAP.
    //
    private static PDPEngine pdpEngine = null;
    private static final Object pdpEngineLock = new Object();
    //
    // This is our PDP's status. What policies are loaded (or not) and
    // what PIP configurations are loaded (or not).
    // There is a synchronized lock used for access to the object.
    //
    private static volatile StdPDPStatus status = new StdPDPStatus();
    private static final Object pdpStatusLock = new Object();
    private static Constructor<?> createUpdatePolicyConstructor;

    private static final String ENVIORNMENT_HEADER = "Environment";
    private static String environment = null;

    //
    // Queue of PUT requests
    //
    public static class PutRequest {
        private Properties policyProperties = null;
        private Properties pipConfigProperties = null;

        PutRequest(Properties policies, Properties pips) {
            this.policyProperties = policies;
            this.pipConfigProperties = pips;
        }
    }

    protected static volatile BlockingQueue<PutRequest> queue = null;
    // For notification Delay.
    private static int notificationDelay = 0;

    public static int getNotificationDelay() {
        return XACMLPdpServlet.notificationDelay;
    }

    private static String pdpResourceName;
    private static String[] dependencyNodes = null;

    //
    // This is our configuration thread that attempts to load
    // a new configuration request.
    //
    private static transient Thread configThread = null;
    private static volatile boolean configThreadTerminate = false;
    private transient ONAPLoggingContext baseLoggingContext = null;
    private transient IntegrityMonitor im;

    public IntegrityMonitor getIm() {
        return im;
    }

    public void setIm(IntegrityMonitor im) {
        this.im = im;
    }

    /**
     * Default constructor.
     */
    public XACMLPdpServlet() {
        // Default constructor.
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        String createUpdateResourceName = null;
        String dependencyGroups = null;
        //
        // Initialize
        //
        XACMLRest.xacmlInit(config);
        // Load the Notification Delay.
        setNotificationDelay();
        // Load Queue size. Not sure if we really need to have the queue bounded, we should look further into this
        int queueSize = 50; // Set default Queue Size here.
        queueSize = Integer.parseInt(XACMLProperties.getProperty("REQUEST_BUFFER_SIZE", String.valueOf(queueSize)));
        initQueue(queueSize);
        // Load our engine - this will use the latest configuration
        // that was saved to disk and set our initial status object.
        //
        PDPEngine engine = XACMLPdpLoader.loadEngine(XACMLPdpServlet.status, null, null);
        if (engine != null) {
            synchronized (pdpEngineLock) {
                pdpEngine = engine;
            }
            // Notification will be Sent Here.
            XACMLPdpLoader.sendNotification();
        }
        //
        // Logging stuff....
        //
        baseLoggingContext = new ONAPLoggingContext();
        // fixed data that will be the same in all logging output goes here
        try {
            String hostname = InetAddress.getLocalHost().getCanonicalHostName();
            baseLoggingContext.setServer(hostname);
        } catch (UnknownHostException e) {
            logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get hostname for logging" + e);
        }

        Properties properties;
        try {
            properties = XACMLProperties.getProperties();
        } catch (IOException e) {
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e,
                    "Error loading properties with: XACMLProperties.getProperties()");
            throw new ServletException(e.getMessage(), e.getCause());
        }
        if (properties.getProperty(XACMLRestProperties.PDP_RESOURCE_NAME) == null) {
            XACMLProperties.reloadProperties();
            try {
                properties = XACMLProperties.getProperties();
            } catch (IOException e) {
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e,
                        "Error loading properties with: XACMLProperties.getProperties()");
                throw new ServletException(e.getMessage(), e.getCause());
            }
            PolicyLogger.info("\n Properties Given : \n" + properties.toString());
        }
        setPDPResourceName(properties);
        dependencyGroups = properties.getProperty(IntegrityMonitorProperties.DEPENDENCY_GROUPS);
        if (dependencyGroups == null) {
            PolicyLogger.error(MessageCodes.MISS_PROPERTY_ERROR, IntegrityMonitorProperties.DEPENDENCY_GROUPS,
                    "xacml.pdp");
            throw new ServletException("dependency_groups is null");
        }
        setDependencyNodes(dependencyGroups);


        // CreateUpdatePolicy ResourceName
        createUpdateResourceName =
                properties.getProperty("createUpdatePolicy.impl.className", CREATE_UPDATE_POLICY_SERVICE);
        setCreateUpdatePolicyConstructor(createUpdateResourceName);

        // Create an IntegrityMonitor
        try {
            logger.info("Creating IntegrityMonitor");
            im = IntegrityMonitor.getInstance(pdpResourceName, properties);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "Failed to create IntegrityMonitor" + e);
            throw new ServletException(e);
        }
        startThreads(baseLoggingContext, new Thread(this));
    }

    private static void startThreads(ONAPLoggingContext baseLoggingContext, Thread thread) {
        environment = XACMLProperties.getProperty("ENVIRONMENT", "DEVL");
        //
        // Kick off our thread to register with the PAP servlet.
        //
        if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_REGISTER))) {
            XACMLPdpServlet.registerRunnable = new XACMLPdpRegisterThread(baseLoggingContext);
            XACMLPdpServlet.registerThread = new Thread(XACMLPdpServlet.registerRunnable);
            XACMLPdpServlet.registerThread.start();
        }
        //
        // This is our thread that manages incoming configuration
        // changes.
        //
        XACMLPdpServlet.configThread = thread;
        XACMLPdpServlet.configThread.start();
    }

    private static void setDependencyNodes(String dependencyGroups) {
        // dependency_groups is a semicolon-delimited list of groups, and
        // each group is a comma-separated list of nodes. For our purposes
        // we just need a list of dependencies without regard to grouping,
        // so split the list into nodes separated by either comma or semicolon.
        dependencyNodes = dependencyGroups.split("[;,]");
        for (int i = 0; i < dependencyNodes.length; i++) {
            dependencyNodes[i] = dependencyNodes[i].trim();
        }
    }

    private static void setPDPResourceName(Properties properties) throws ServletException {
        pdpResourceName = properties.getProperty(XACMLRestProperties.PDP_RESOURCE_NAME);
        if (pdpResourceName == null) {
            PolicyLogger.error(MessageCodes.MISS_PROPERTY_ERROR, XACMLRestProperties.PDP_RESOURCE_NAME, "xacml.pdp");
            throw new ServletException("pdpResourceName is null");
        }
    }

    private static void initQueue(int queueSize) {
        queue = new LinkedBlockingQueue<>(queueSize);
    }

    private static void setNotificationDelay() {
        try {
            XACMLPdpServlet.notificationDelay =
                    Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_NOTIFICATION_DELAY));
        } catch (NumberFormatException e) {
            logger.error("Error in notification delay format, Taking the default value.", e);
        }
    }

    /**
     * @see Servlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        logger.info("Destroying....");
        //
        // Make sure the register thread is not running
        //
        if (XACMLPdpServlet.registerRunnable != null) {
            try {
                XACMLPdpServlet.registerRunnable.terminate();
                if (XACMLPdpServlet.registerThread != null) {
                    XACMLPdpServlet.registerThread.interrupt();
                    XACMLPdpServlet.registerThread.join();
                }
            } catch (InterruptedException e) {
                logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
                XACMLPdpServlet.registerThread.interrupt();
            }
        }
        //
        // Make sure the configure thread is not running
        //
        setConfigThreadTerminate(true);
        try {
            XACMLPdpServlet.configThread.interrupt();
            XACMLPdpServlet.configThread.join();
        } catch (InterruptedException e) {
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "");
            XACMLPdpServlet.configThread.interrupt();
        }
        logger.info("Destroyed.");
    }

    private static void setConfigThreadTerminate(boolean value) {
        XACMLPdpServlet.configThreadTerminate = value;
    }

    /**
     * PUT - The PAP engine sends configuration information using HTTP PUT request.
     * 
     * One parameter is expected:
     * 
     * config=[policy|pip|all]
     * 
     * policy - Expect a properties file that contains updated lists of the root and referenced policies that the PDP
     * should be using for PEP requests.
     * 
     * Specifically should AT LEAST contain the following properties: xacml.rootPolicies xacml.referencedPolicies
     * 
     * In addition, any relevant information needed by the PDP to load or retrieve the policies to store in its cache.
     *
     * EXAMPLE: xacml.rootPolicies=PolicyA.1, PolicyB.1
     *
     * PolicyA.1.url=http://localhost:9090/PAP?id=b2d7b86d-d8f1-4adf-ba9d-b68b2a90bee1&version=1
     * PolicyB.1.url=http://localhost:9090/PAP/id=be962404-27f6-41d8-9521-5acb7f0238be&version=1
     * 
     * xacml.referencedPolicies=RefPolicyC.1, RefPolicyD.1
     *
     * RefPolicyC.1.url=http://localhost:9090/PAP?id=foobar&version=1
     * RefPolicyD.1.url=http://localhost:9090/PAP/id=example&version=1
     * 
     * pip - Expect a properties file that contain PIP engine configuration properties.
     * 
     * Specifically should AT LEAST the following property: xacml.pip.engines
     * 
     * In addition, any relevant information needed by the PDP to load and configure the PIPs.
     * 
     * EXAMPLE: xacml.pip.engines=foo,bar
     * 
     * foo.classname=com.foo foo.sample=abc foo.example=xyz ......
     * 
     * bar.classname=com.bar ......
     * 
     * all - Expect ALL new configuration properties for the PDP
     * 
     * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        if ((loggingContext.getRequestID() == null) || "".equals(loggingContext.getRequestID())) {
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPdpSrvlet (doPut) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPdpSrvlet (doPut)");
        }
        loggingContext.metricStarted();
        loggingContext.metricEnded();
        PolicyLogger.metrics("Metric example posted here - 1 of 2");
        loggingContext.metricStarted();
        loggingContext.metricEnded();
        PolicyLogger.metrics("Metric example posted here - 2 of 2");
        //
        // Dump our request out
        //
        if (logger.isDebugEnabled()) {
            XACMLRest.dumpRequest(request);
        }

        try {
            im.startTransaction();
        } catch (IntegrityMonitorException e) {
            String message = e.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message + e);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            return;
        }
        //
        // What is being PUT?
        //
        String cache = request.getParameter("cache");
        //
        // Should be a list of policy and pip configurations in Java properties format
        //
        if (cache != null && request.getContentType().equals("text/x-java-properties")) {
            loggingContext.setServiceName("PDP.putConfig");
            try {
                if (request.getContentLength() > Integer
                        .parseInt(XACMLProperties.getProperty("MAX_CONTENT_LENGTH", DEFAULT_MAX_CONTENT_LENGTH))) {
                    String message = "Content-Length larger than server will accept.";
                    logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
                    loggingContext.transactionEnded();
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, message);
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                    im.endTransaction();
                    return;
                }
                logger.info("XACMLPdpServlet: calling doPutConfig to add properties to the queue");
                this.doPutConfig(cache, request, response, loggingContext);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction ended");

                im.endTransaction();
            } catch (Exception e) {
                logger.error("Exception Occured while getting Max Content lenght" + e);
            }
        } else {
            String message = "Invalid cache: '" + cache + "' or content-type: '" + request.getContentType() + "'";
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + message);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }
    }

    protected void doPutConfig(String config, HttpServletRequest request, HttpServletResponse response,
            ONAPLoggingContext loggingContext) throws IOException {
        try {
            // prevent multiple configuration changes from stacking up
            logger.info("XACMLPdpServlet: checking remainingCapacity of Queue.");
            if (XACMLPdpServlet.queue.remainingCapacity() <= 0) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Queue capacity reached");
                PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, "Queue capacity reached");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                response.sendError(HttpServletResponse.SC_CONFLICT,
                        "Multiple configuration changes waiting processing.");
                return;
            }
            //
            // Read the properties data into an object.
            //
            Properties newProperties = new Properties();
            newProperties.load(request.getInputStream());
            // should have something in the request
            if (newProperties.size() == 0) {
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No properties in PUT");
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No properties in PUT");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "PUT must contain at least one property");
                return;
            }
            //
            // Which set of properties are they sending us? Whatever they send gets
            // put on the queue (if there is room).
            // For audit logging purposes, we consider the transaction done once the
            // the request gets put on the queue.
            //
            if (config.equals("policies")) {
                newProperties = XACMLProperties.getPolicyProperties(newProperties, true);
                if (newProperties.size() == 0) {
                    logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No policy properties in PUT");
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No policy properties in PUT");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "PUT with cache=policies must contain at least one policy property");
                    return;
                }
                logger.info("XACMLPdpServlet: offer policies to queue. No pip properties added.");
                XACMLPdpServlet.queue.offer(new PutRequest(newProperties, null));
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Success");
            } else if (config.equals("pips")) {
                newProperties = XACMLProperties.getPipProperties(newProperties);
                if (newProperties.size() == 0) {
                    logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No pips properties in PUT");
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No pips properties in PUT");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "PUT with cache=pips must contain at least one pip property");
                    return;
                }
                logger.info("XACMLPdpServlet: offer pips to queue. No policy properties added.");
                XACMLPdpServlet.queue.offer(new PutRequest(null, newProperties));
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Success");
            } else if (config.equals("all")) {
                Properties newPolicyProperties = XACMLProperties.getPolicyProperties(newProperties, true);
                if (newPolicyProperties.size() == 0) {
                    logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No policy properties in PUT");
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No policy properties in PUT");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "PUT with cache=all must contain at least one policy property");
                    return;
                }
                Properties newPipProperties = XACMLProperties.getPipProperties(newProperties);
                if (newPipProperties.size() == 0) {
                    logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "No pips properties in PUT");
                    PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "No pips properties in PUT");
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "PUT with cache=all must contain at least one pip property");
                    return;
                }
                logger.info("XACMLPdpServlet: offer policies and pips to queue.");
                XACMLPdpServlet.queue.offer(new PutRequest(newPolicyProperties, newPipProperties));
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Success");

            } else {
                //
                // Invalid value
                //
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid config value: " + config);
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Invalid config value: " + config);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Config must be one of 'policies', 'pips', 'all'");
                return;
            }
        } catch (Exception e) {
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to process new configuration.", e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "Failed to process new configuration");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            return;
        }

    }

    /**
     * Parameters: type=hb|config|Status
     * 
     * 1. HeartBeat Status HeartBeat OK - All Policies are Loaded, All PIPs are Loaded LOADING_IN_PROGRESS - Currently
     * loading a new policy set/pip configuration LAST_UPDATE_FAILED - Need to track the items that failed during last
     * update LOAD_FAILURE - ??? Need to determine what information is sent and how 2. Configuration 3. Status return
     * the StdPDPStatus object in the Response content
     * 
     * 
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")) {
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPdpSrvlet (doGet) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPdpSrvlet (doGet)");
        }
        loggingContext.metricStarted();
        loggingContext.metricEnded();
        PolicyLogger.metrics("Metric example posted here - 1 of 2");
        loggingContext.metricStarted();
        loggingContext.metricEnded();
        PolicyLogger.metrics("Metric example posted here - 2 of 2");

        XACMLRest.dumpRequest(request);

        String pathInfo = request.getRequestURI();
        if (pathInfo != null && "/pdp/test".equals(pathInfo)) {
            // health check from Global Site Selector (iDNS).
            // DO NOT do a im.startTransaction for the test request
            loggingContext.setServiceName("iDNS:PDP.test");
            try {
                im.evaluateSanity();
                // If we make it this far, all is well
                String message = "GET:/pdp/test called and PDP " + pdpResourceName + " is OK";
                PolicyLogger.debug(message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Success");
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            } catch (ForwardProgressException | AdministrativeStateException | StandbyStatusException fpe) {
                // No forward progress is being made
                String message = "GET:/pdp/test called and PDP " + pdpResourceName + " is not making forward progress."
                        + " Exception Message: " + fpe.getMessage();
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message + fpe);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                } catch (Exception e1) {
                    logger.error("Exception occured while sending error in response" + e1);
                }
                return;
            } catch (Exception e) {
                // A subsystem is not making progress or is not responding
                String eMsg = e.getMessage();
                if (eMsg == null) {
                    eMsg = "No Exception Message";
                }
                String message = "GET:/pdp/test called and PDP " + pdpResourceName + " has had a subsystem failure."
                        + " Exception Message: " + eMsg;
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message);
                // Get the specific list of subsystems that failed
                String failedNodeList = null;
                for (String node : dependencyNodes) {
                    if (eMsg.contains(node)) {
                        if (failedNodeList == null) {
                            failedNodeList = node;
                        } else {
                            failedNodeList = failedNodeList.concat("," + node);
                        }
                    }
                }
                if (failedNodeList == null) {
                    failedNodeList = "UnknownSubSystem";
                }
                response.addHeader("X-ONAP-SubsystemFailure", failedNodeList);
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                } catch (Exception e1) {
                    logger.error("Exception occured while sending error in response" + e1);
                }
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log" + e);
                return;
            }
        }

        try {
            im.startTransaction();
        } catch (IntegrityMonitorException e) {
            String message = e.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log" + e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            return;
        }
        //
        // What are they requesting?
        //
        boolean returnHB = false;
        response.setHeader("Cache-Control", "no-cache");
        String type = request.getParameter("type");
        // type might be null, so use equals on string constants
        if ("config".equals(type)) {
            loggingContext.setServiceName("PDP.getConfig");
            response.setContentType("text/x-java-properties");
            try {
                String lists = XACMLProperties.PROP_ROOTPOLICIES + "="
                        + XACMLProperties.getProperty(XACMLProperties.PROP_ROOTPOLICIES, "");
                lists = lists + "\n" + XACMLProperties.PROP_REFERENCEDPOLICIES + "="
                        + XACMLProperties.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "") + "\n";
                try (InputStream listInputStream = new ByteArrayInputStream(lists.getBytes());
                        InputStream pipInputStream = Files.newInputStream(XACMLPdpLoader.getPIPConfig());
                        OutputStream os = response.getOutputStream()) {
                    IOUtils.copy(listInputStream, os);
                    IOUtils.copy(pipInputStream, os);
                }
                loggingContext.transactionEnded();
                auditLogger.info("Success");
                PolicyLogger.audit("Success");
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed to copy property file", e);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "Failed to copy property file");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                try {
                    response.sendError(400, "Failed to copy Property file");
                } catch (Exception e1) {
                    logger.error("Exception occured while sending error in response" + e1);
                }
            }

        } else if ("hb".equals(type)) {
            returnHB = true;
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } else if ("Status".equals(type)) {
            loggingContext.setServiceName("PDP.getStatus");
            // convert response object to JSON and include in the response
            synchronized (pdpStatusLock) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    mapper.writeValue(response.getOutputStream(), status);
                } catch (Exception e1) {
                    logger.error("Exception occured while writing output stream" + e1);
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
            loggingContext.transactionEnded();
            auditLogger.info("Success");
            PolicyLogger.audit("Success");

        } else {
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid type value: " + type);
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Invalid type value: " + type);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "type not 'config' or 'hb'");
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
        }
        if (returnHB) {
            synchronized (pdpStatusLock) {
                response.addHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_HB, status.getStatus().toString());
            }
        }
        loggingContext.transactionEnded();
        PolicyLogger.audit("Transaction Ended");
        im.endTransaction();

    }

    /**
     * POST - We expect XACML requests to be posted by PEP applications. They can be in the form of XML or JSON
     * according to the XACML 3.0 Specifications for both.
     * 
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ONAPLoggingContext loggingContext = ONAPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
        loggingContext.transactionStarted();
        loggingContext.setServiceName("PDP.decide");
        if ((loggingContext.getRequestID() == null) || ("".equals(loggingContext.getRequestID()))) {
            UUID requestID = UUID.randomUUID();
            loggingContext.setRequestID(requestID.toString());
            PolicyLogger.info("requestID not provided in call to XACMLPdpSrvlet (doPost) so we generated one");
        } else {
            PolicyLogger.info("requestID was provided in call to XACMLPdpSrvlet (doPost)");
        }
        loggingContext.metricStarted();
        loggingContext.metricEnded();
        PolicyLogger.metrics("Metric example posted here - 1 of 2");
        loggingContext.metricStarted();
        loggingContext.metricEnded();
        PolicyLogger.metrics("Metric example posted here - 2 of 2");
        monitor.pdpEvaluationAttempts();

        try {
            im.startTransaction();
        } catch (IntegrityMonitorException e) {
            String message = e.toString();
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message + e);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            return;
        }
        //
        // no point in doing any work if we know from the get-go that we cannot do anything with the request
        //
        if (status.getLoadedRootPolicies().isEmpty()) {
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Request from PEP at " + request.getRequestURI()
                    + " for service when PDP has No Root Policies loaded");
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, "Request from PEP at " + request.getRequestURI()
                    + " for service when PDP has No Root Policies loaded");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }

        XACMLRest.dumpRequest(request);
        //
        // Set our no-cache header
        //
        response.setHeader("Cache-Control", "no-cache");
        //
        // They must send a Content-Type
        //
        if (request.getContentType() == null) {
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Must specify a Content-Type");
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, "Must specify a Content-Type");
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no content-type given");
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }
        //
        // Limit the Content-Length to something reasonable
        //
        try {
            if (request.getContentLength() > Integer
                    .parseInt(XACMLProperties.getProperty("MAX_CONTENT_LENGTH", "32767"))) {
                String message = "Content-Length larger than server will accept.";
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                im.endTransaction();
                return;
            }
        } catch (Exception e) {
            logger.error("Exception occured while getting max content length" + e);
        }

        if (request.getContentLength() <= 0) {
            String message = "Content-Length is negative";
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }
        ContentType contentType = null;
        try {
            contentType = ContentType.parse(request.getContentType());
        } catch (Exception e) {
            String message = "Parsing Content-Type: " + request.getContentType() + ", error=" + e.getMessage();
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message, e);
            loggingContext.transactionEnded();
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, message);
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }
        //
        // What exactly did they send us?
        //
        String incomingRequestString = null;
        Request pdpRequest = null;
        if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())
                || contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())
                || contentType.getMimeType().equalsIgnoreCase("application/xacml+xml")) {
            //
            // Read in the string
            //
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            } catch (IOException e) {
                logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error during reading input stream", e);
                return;
            }
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (Exception e) {
                logger.error("Exception Occured while reading line" + e);
            }

            incomingRequestString = buffer.toString();
            logger.info(incomingRequestString);
            //
            // Parse into a request
            //
            try {
                if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())) {
                    pdpRequest = JSONRequest.load(incomingRequestString);
                } else if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())
                        || "application/xacml+xml".equalsIgnoreCase(contentType.getMimeType())) {
                    pdpRequest = DOMRequest.load(incomingRequestString);
                }
            } catch (Exception e) {
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Could not parse request", e);
                PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "Could not parse request");
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                try {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                } catch (Exception e1) {
                    logger.error("Exception occured while sending error in response" + e1);
                }
                im.endTransaction();
                return;
            }
        } else {
            String message = "unsupported content type" + request.getContentType();
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }
        //
        // Did we successfully get and parse a request?
        //
        if (pdpRequest == null || pdpRequest.getRequestAttributes() == null
                || pdpRequest.getRequestAttributes().isEmpty()) {
            String message = "Zero Attributes found in the request";
            logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
            PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            im.endTransaction();
            return;
        }
        //
        // Run it
        //
        try {
            //
            // Authenticating the Request here.
            //
            if (!authorizeRequest(request)) {
                String message =
                        "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
                logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + message);
                PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS, message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
                im.endTransaction();
                return;
            }
            //
            // Get the pointer to the PDP Engine
            //
            PDPEngine myEngine = null;
            synchronized (pdpEngineLock) {
                myEngine = XACMLPdpServlet.pdpEngine;
            }
            if (myEngine == null) {
                String message = "No engine loaded.";
                logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + message);
                PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message);
                loggingContext.transactionEnded();
                PolicyLogger.audit("Transaction Failed - See Error.log");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                im.endTransaction();
                return;
            }
            //
            // Send the request and save the response
            //
            long lTimeStart;
            long lTimeEnd;
            Response pdpResponse = null;

            synchronized (pdpEngineLock) {
                myEngine = XACMLPdpServlet.pdpEngine;
                try {
                    PolicyList.clearPolicyList();
                    lTimeStart = System.currentTimeMillis();
                    pdpResponse = myEngine.decide(pdpRequest);
                    lTimeEnd = System.currentTimeMillis();
                } catch (PDPException e) {
                    String message = "Exception during decide: " + e.getMessage();
                    logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + message + e);
                    PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, message);
                    loggingContext.transactionEnded();
                    PolicyLogger.audit("Transaction Failed - See Error.log");
                    try {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                    } catch (Exception e1) {
                        logger.error("Exception occured while sending error in response" + e1);
                    }
                    im.endTransaction();
                    return;
                }
            }
            monitor.computeLatency(lTimeEnd - lTimeStart);
            requestLogger.info(lTimeStart + "=" + incomingRequestString);
            for (String policy : PolicyList.getpolicyList()) {
                monitor.policyCountAdd(policy, 1);
            }


            logger.info("PolicyID triggered in Request: " + PolicyList.getpolicyList());

            // need to go through the list and find out if the value is unique and then add it other wise
            // monitor.policyCountAdd(PolicyList.getpolicyList(), 1);

            if (logger.isDebugEnabled()) {
                logger.debug("Request time: " + (lTimeEnd - lTimeStart) + "ms");
            }
            //
            // Convert Response to appropriate Content-Type
            //
            if (pdpResponse == null) {
                requestLogger.info(lTimeStart + "=" + "{}");
                try {
                    throw new PDPException("Failed to get response from PDP engine.");
                } catch (Exception e1) {
                    logger.error("Exception occured while throwing Exception" + e1);
                }
            }
            //
            // Set our content-type
            //
            response.setContentType(contentType.getMimeType());
            //
            // Convert the PDP response object to a String to
            // return to our caller as well as dump to our loggers.
            //
            String outgoingResponseString = "";
            try {
                if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())) {
                    //
                    // Get it as a String. This is not very efficient but we need to log our
                    // results for auditing.
                    //
                    outgoingResponseString = JSONResponse.toString(pdpResponse, logger.isDebugEnabled());
                    if (logger.isDebugEnabled()) {
                        logger.debug(outgoingResponseString);
                        //
                        // Get rid of whitespace
                        //
                        outgoingResponseString = JSONResponse.toString(pdpResponse, false);
                    }
                } else if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())
                        || contentType.getMimeType().equalsIgnoreCase("application/xacml+xml")) {
                    //
                    // Get it as a String. This is not very efficient but we need to log our
                    // results for auditing.
                    //
                    outgoingResponseString = DOMResponse.toString(pdpResponse, logger.isDebugEnabled());
                    if (logger.isDebugEnabled()) {
                        logger.debug(outgoingResponseString);
                        //
                        // Get rid of whitespace
                        //
                        outgoingResponseString = DOMResponse.toString(pdpResponse, false);
                    }
                }
                // adding the jmx values for NA, Permit and Deny
                //
                if (outgoingResponseString.contains("NotApplicable")
                        || outgoingResponseString.contains("Decision not a Permit")) {
                    monitor.pdpEvaluationNA();
                }

                if (outgoingResponseString.contains("Permit")
                        && !outgoingResponseString.contains("Decision not a Permit")) {
                    monitor.pdpEvaluationPermit();
                }

                if (outgoingResponseString.contains("Deny")) {
                    monitor.pdpEvaluationDeny();
                }
                //
                // lTimeStart is used as an ID within the requestLogger to match up
                // request's with responses.
                //
                requestLogger.info(lTimeStart + "=" + outgoingResponseString);
                response.getWriter().print(outgoingResponseString);
            } catch (Exception e) {
                logger.error("Exception Occured" + e);
            }
        } catch (Exception e) {
            String message = "Exception executing request: " + e;
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + message, e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, message);
            loggingContext.transactionEnded();
            PolicyLogger.audit("Transaction Failed - See Error.log");
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            } catch (Exception e1) {
                logger.error("Exception occured while sending error in response" + e1);
            }
            return;
        }

        monitor.pdpEvaluationSuccess();
        response.setStatus(HttpServletResponse.SC_OK);

        loggingContext.transactionEnded();
        auditLogger.info("Success");
        PolicyLogger.audit("Success");

    }

    /*
     * Added for Authorizing the PEP Requests for Environment check.
     */
    private boolean authorizeRequest(HttpServletRequest request) {
        // Get the client Credentials from the Request header.
        HttpServletRequest httpServletRequest = request;
        String clientCredentials = httpServletRequest.getHeader(ENVIORNMENT_HEADER);
        if (clientCredentials != null && clientCredentials.equalsIgnoreCase(environment)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        //
        // Keep running until we are told to terminate
        //
        try {
            // variable not used, but constructor has needed side-effects so don't remove:
            while (!XACMLPdpServlet.configThreadTerminate) {
                logger.info("XACMLPdpServlet: Taking requests from the queue");
                PutRequest request = XACMLPdpServlet.queue.take();
                logger.info("XACMLPdpServlet: Taking requests from the queue COMPLETED");
                StdPDPStatus newStatus = new StdPDPStatus();

                PDPEngine newEngine = null;
                synchronized (pdpStatusLock) {
                    XACMLPdpServlet.status.setStatus(Status.UPDATING_CONFIGURATION);

                    logger.info("created new PDPEngine");
                    newEngine =
                            XACMLPdpLoader.loadEngine(newStatus, request.policyProperties, request.pipConfigProperties);
                }
                if (newEngine != null) {
                    logger.info("XACMLPdpServlet: newEngine created, assigning newEngine to the pdpEngine.");
                    synchronized (XACMLPdpServlet.pdpEngineLock) {
                        XACMLPdpServlet.pdpEngine = newEngine;
                        try {
                            logger.info("Saving configuration.");
                            if (request.policyProperties != null) {
                                logger.info("Saving configuration: Policy Properties: " + request.policyProperties);
                                try (OutputStream os = Files.newOutputStream(XACMLPdpLoader.getPDPPolicyCache())) {
                                    request.policyProperties.store(os, "");
                                }
                            }
                            if (request.pipConfigProperties != null) {
                                logger.info("Saving configuration: PIP Properties: " + request.pipConfigProperties);
                                try (OutputStream os = Files.newOutputStream(XACMLPdpLoader.getPIPConfig())) {
                                    request.pipConfigProperties.store(os, "");
                                }
                            }
                            newStatus.setStatus(Status.UP_TO_DATE);
                        } catch (Exception e) {
                            logger.error(
                                    XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to store new properties." + e);
                            PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, "Failed to store new properties");
                            newStatus.setStatus(Status.LOAD_ERRORS);
                            newStatus.addLoadWarning("Unable to save configuration: " + e.getMessage());
                        }
                    }
                } else {
                    newStatus.setStatus(Status.LAST_UPDATE_FAILED);
                }
                synchronized (pdpStatusLock) {
                    XACMLPdpServlet.status.set(newStatus);
                }
                logger.info("New PDP Servlet Status: " + newStatus.getStatus());
                if (Status.UP_TO_DATE.equals(newStatus.getStatus())) {
                    // Notification will be Sent Here.
                    XACMLPdpLoader.sendNotification();
                }
            }
        } catch (InterruptedException e) {
            logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "interrupted" + e);
            PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, "interrupted");
            Thread.currentThread().interrupt();
        }
    }

    public static PDPEngine getPDPEngine() {
        PDPEngine myEngine = null;
        synchronized (pdpEngineLock) {
            myEngine = XACMLPdpServlet.pdpEngine;
        }
        return myEngine;
    }

    public static Constructor<?> getCreateUpdatePolicyConstructor() {
        return createUpdatePolicyConstructor;
    }

    public static Object getPDPEngineLock() {
        return pdpEngineLock;
    }

    private static void setCreateUpdatePolicyConstructor(String createUpdateResourceName) throws ServletException {
        try {
            Class<?> createUpdateclass = Class.forName(createUpdateResourceName);
            createUpdatePolicyConstructor =
                    createUpdateclass.getConstructor(PolicyParameters.class, String.class, boolean.class);
        } catch (Exception e) {
            PolicyLogger.error(MessageCodes.MISS_PROPERTY_ERROR, "createUpdatePolicy.impl.className",
                    "xacml.pdp.init" + e);
            throw new ServletException(
                    "Could not find the Class name : " + createUpdateResourceName + "\n" + e.getMessage());
        }
    }
}
