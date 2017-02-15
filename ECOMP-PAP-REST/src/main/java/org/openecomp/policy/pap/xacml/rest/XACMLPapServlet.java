/*-
 * ============LICENSE_START=======================================================
 * ECOMP-PAP-REST
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

package org.openecomp.policy.pap.xacml.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.EntityManagerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.openecomp.policy.pap.xacml.rest.adapters.PolicyRestAdapter;
import org.openecomp.policy.pap.xacml.rest.components.ActionPolicy;
import org.openecomp.policy.pap.xacml.rest.components.AutoPushPolicy;
import org.openecomp.policy.pap.xacml.rest.components.ClosedLoopPolicy;
import org.openecomp.policy.pap.xacml.rest.components.ConfigPolicy;
import org.openecomp.policy.pap.xacml.rest.components.CreateBrmsParamPolicy;
import org.openecomp.policy.pap.xacml.rest.components.CreateBrmsRawPolicy;
import org.openecomp.policy.pap.xacml.rest.components.CreateClosedLoopPerformanceMetrics;
import org.openecomp.policy.pap.xacml.rest.components.CreateNewMicroSerivceModel;
import org.openecomp.policy.pap.xacml.rest.components.DecisionPolicy;
import org.openecomp.policy.pap.xacml.rest.components.FirewallConfigPolicy;
import org.openecomp.policy.pap.xacml.rest.components.MicroServiceConfigPolicy;
import org.openecomp.policy.pap.xacml.rest.components.Policy;
import org.openecomp.policy.pap.xacml.rest.components.PolicyDBDao;
import org.openecomp.policy.pap.xacml.rest.components.PolicyDBDaoTransaction;
import org.openecomp.policy.pap.xacml.rest.model.RemoveGroupPolicy;
import org.openecomp.policy.pap.xacml.rest.util.JPAUtils;
import org.openecomp.policy.pap.xacml.restAuth.CheckPDP;
import org.openecomp.policy.rest.XACMLRest;
import org.openecomp.policy.rest.XACMLRestProperties;
import org.openecomp.policy.rest.jpa.ActionPolicyDict;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
import org.openecomp.policy.rest.jpa.MicroServiceModels;
import org.openecomp.policy.rest.jpa.PolicyEditorScopes;
import org.openecomp.policy.rest.jpa.PolicyScore;
import org.openecomp.policy.rest.jpa.PolicyVersion;
import org.openecomp.policy.rest.jpa.UserInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.persistence.PersistenceException;

import org.openecomp.policy.common.logging.ECOMPLoggingContext;
import org.openecomp.policy.common.logging.ECOMPLoggingUtils;
import org.openecomp.policy.common.logging.eelf.MessageCodes;
import org.openecomp.policy.common.logging.eelf.PolicyLogger;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import org.openecomp.policy.xacml.api.pap.ECOMPPapEngineFactory;
import org.openecomp.policy.xacml.api.pap.EcompPDP;
import org.openecomp.policy.xacml.api.pap.EcompPDPGroup;
import org.openecomp.policy.xacml.api.pap.PAPPolicyEngine;

import com.att.research.xacml.api.pap.PAPException;
//import com.att.research.xacml.api.pap.PDP;
//import com.att.research.xacml.api.pap.PDPGroup;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.att.research.xacml.api.pap.PDPStatus;
import org.openecomp.policy.xacml.std.pap.StdPAPPolicy;
import org.openecomp.policy.xacml.std.pap.StdPDP;
import org.openecomp.policy.xacml.std.pap.StdPDPGroup;
import org.openecomp.policy.xacml.std.pap.StdPDPPolicy;
import org.openecomp.policy.xacml.std.pap.StdPDPStatus;
import org.openecomp.policy.xacml.std.pap.StdPDPItemSetChangeNotifier.StdItemSetChangeListener;
import org.openecomp.policy.xacml.util.XACMLPolicyScanner;

import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.XACMLProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.openecomp.policy.common.im.AdministrativeStateException;
import org.openecomp.policy.common.im.ForwardProgressException;

//IntegrityMontitor
import org.openecomp.policy.common.im.IntegrityMonitor;
import org.openecomp.policy.common.im.IntegrityMonitorProperties;
import org.openecomp.policy.common.im.StandbyStatusException;
//IntegrityAudit
import org.openecomp.policy.common.ia.IntegrityAudit;
import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;


/**
 * Servlet implementation class XacmlPapServlet
 * 
 * 
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
	private static final Logger logger	= FlexLogger.getLogger(XACMLPapServlet.class);

	private static String CONFIG_HOME = getConfigHome();
	private static String ACTION_HOME = getActionHome();

	// audit (transaction) logger
	private static final Logger auditLogger = FlexLogger.getLogger("auditLogger");

	private IntegrityMonitor im;
	private IntegrityAudit ia;

	/*
	 * 
	 * papEngine - This is our engine workhorse that manages the PDP Groups and Nodes.
	 */
	private PAPPolicyEngine papEngine = null;
	/*
	 * This PAP instance's own URL.
	 * 
	 * Need this when creating URLs to send to the PDPs so they can GET the Policy files from this process. 
	 */
	private static String papURL = null;

	/*
	 * These are the parameters needed for DB access from the PAP
	 */
	public static String papDbDriver = null;
	public static String papDbUrl = null;
	public static String papDbUser = null;
	public static String papDbPassword = null;
	private static Integer papTransWait = null;
	private static Integer papTransTimeout = null;
	private static Integer papAuditTimeout = null;
	private static Boolean papAuditFlag = null;
	private static Boolean papFileSystemAudit = null;
	private static Boolean autoPushFlag = false;
	private static String papResourceName = null;
	private static Integer fpMonitorInterval = null; 
	private static Integer failedCounterThreshold = null;
	private static Integer testTransInterval = null;
	private static Integer writeFpcInterval = null;
	private static String papSiteName=null;
	private static String papNodeType=null;	
	private static String papDependencyGroups = null;
	private String storedRequestId = null;
	private static int papIntegrityAuditPeriodSeconds = -1;
	private static String[] papDependencyGroupsFlatArray = null;

	//The entity manager factory for JPA access
	private EntityManagerFactory emf;

	//Persistence Unit for JPA 
	private static final String PERSISTENCE_UNIT = "XACML-PAP-REST";
	private static final String AUDIT_PAP_PERSISTENCE_UNIT = "auditPapPU";


	/*
	 * List of Admin Console URLs.
	 * Used to send notifications when configuration changes.
	 * 
	 * The CopyOnWriteArrayList *should* protect from concurrency errors.
	 * This list is seldom changed but often read, so the costs of this approach make sense.
	 */
	private static final CopyOnWriteArrayList<String> adminConsoleURLStringList = new CopyOnWriteArrayList<String>();

	// Mike M 11/24 Client Headers. 
	private static final String ENVIRONMENT_HEADER = "Environment";
	private static String environment = null;

	/*
	 * This thread may be invoked upon startup to initiate sending PDP policy/pip configuration when
	 * this servlet starts. Its configurable by the admin.
	 */
	private Thread initiateThread = null;

	/*
	// The heartbeat thread.
	 */
	private static Heartbeat heartbeat = null;
	private static Thread heartbeatThread = null;

	private ECOMPLoggingContext baseLoggingContext = null;

	private PolicyDBDao policyDBDao;
	private AutoPushPolicy autoPushPolicy;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XACMLPapServlet() {
		super();
	}
	/*
	 * PDP FIle
	 */
	private static String pdpFile = null;
	public static String getPDPFile(){
		return XACMLPapServlet.pdpFile;
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {


		try {
			//
			// Logging stuff....
			//
			baseLoggingContext = new ECOMPLoggingContext();
			// fixed data that will be the same in all logging output goes here
			try {
				String hostname = InetAddress.getLocalHost().getCanonicalHostName();
				baseLoggingContext.setServer(hostname);
			} catch (UnknownHostException e) {
				logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get hostname for logging");
			}

			//
			// Initialize
			//
			XACMLRest.xacmlInit(config);
			//
			// Load the properties
			//
			XACMLRest.loadXacmlProperties(null, null);

			/*
			 * Retrieve the property values for db access and audits from the xacml.pap.properties
			 */
			//Null string occurs when a property is not present
			try{
				papDbDriver = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_DRIVER);
				if(papDbDriver == null){
					throw new PAPException("papDbDriver is null");
				}
			}
			catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}
			try{
				papDbUrl = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_URL);
				if(papDbUrl == null){
					throw new PAPException("papDbUrl is null");
				}
			} catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}
			try{
				papDbUser = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_USER);
				if(papDbUser == null){
					throw new PAPException("papDbUser is null");
				}
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}
			try{
				papDbPassword = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DB_PASSWORD);
				if(papDbPassword == null){
					throw new PAPException("papDbPassword is null");
				}
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}

			environment = XACMLProperties.getProperty("ENVIRONMENT", "DEVL");

			//Integer will throw an exception of anything is missing or unrecognized
			papTransWait = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_WAIT));
			papTransTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_TRANS_TIMEOUT));
			papAuditTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_AUDIT_TIMEOUT));

			//Boolean will default to false if anything is missing or unrecognized
			papAuditFlag = Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_RUN_AUDIT_FLAG));
			papFileSystemAudit = Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_AUDIT_FLAG));

			//PAP Auto Push 
			autoPushFlag = Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PUSH_FLAG));
			// if Auto push then Load with properties. 
			if(autoPushFlag){
				String file;
				try{
					file = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_PUSH_FILE);
					if(file.endsWith(".properties")){
						autoPushPolicy = new AutoPushPolicy(file);
					}else{
						throw new Exception();
					}
				}catch(Exception e){
					PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Missing property or not a proper property file check for: " + XACMLRestProperties.PROP_PAP_PUSH_FILE );  
					logger.info("Overriding the autoPushFlag to False...");
					autoPushFlag = false;
				}
			}

			try{
				papResourceName = XACMLProperties.getProperty(XACMLRestProperties.PAP_RESOURCE_NAME);
				if(papResourceName == null){
					throw new PAPException("papResourceName is null");
				}
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}

			try{
				papSiteName = XACMLProperties.getProperty(XACMLRestProperties.PAP_SITE_NAME);
				if(papSiteName == null){
					throw new PAPException("papSiteName is null");
				}
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}
			try{
				papNodeType = XACMLProperties.getProperty(XACMLRestProperties.PAP_NODE_TYPE);
				if(papNodeType == null){
					throw new PAPException("papNodeType is null");
				}
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}
			try{
				papDependencyGroups = XACMLProperties.getProperty(XACMLRestProperties.PAP_DEPENDENCY_GROUPS);
				if(papDependencyGroups == null){
					throw new PAPException("papDependencyGroups is null");
				}
				//Now we have flattened the array into a simple comma-separated list
				papDependencyGroupsFlatArray = papDependencyGroups.split("[;,]");

				//clean up the entries
				for (int i = 0 ; i < papDependencyGroupsFlatArray.length ; i ++){
					papDependencyGroupsFlatArray[i] = papDependencyGroupsFlatArray[i].trim();
				}
				try{
					if(XACMLProperties.getProperty(XACMLRestProperties.PAP_INTEGRITY_AUDIT_PERIOD_SECONDS) != null){
						papIntegrityAuditPeriodSeconds = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PAP_INTEGRITY_AUDIT_PERIOD_SECONDS).trim());
					}else{
						//nothing to do.  The parameter is optional
					}
				}catch(Exception e){
					String msg = "integrity_audit_period_seconds ";
					logger.error("\n\nERROR: " + msg + "Bad property entry: " + e.getMessage() + "\n");
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: " + msg +"Bad property entry");
					throw e;
				}
			}catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR: Bad property entry");
				throw e;
			}

			//Integer will throw an exception of anything is missing or unrecognized
			fpMonitorInterval = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.FP_MONITOR_INTERVAL));
			failedCounterThreshold = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.FAILED_COUNTER_THRESHOLD));
			testTransInterval = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.TEST_TRANS_INTERVAL));
			writeFpcInterval = Integer.parseInt(XACMLProperties.getProperty(IntegrityMonitorProperties.WRITE_FPC_INTERVAL));

			logger.debug("\n\n\n**************************************"
					+ "\n**************************************"
					+ "\n"
					+ "\n   papDbDriver = " + papDbDriver
					+ "\n   papDbUrl = " + papDbUrl
					+ "\n   papDbUser = " + papDbUser
					+ "\n   papDbPassword = " + papDbPassword
					+ "\n   papTransWait = " + papTransWait
					+ "\n   papTransTimeout = " + papTransTimeout
					+ "\n   papAuditTimeout = " + papAuditTimeout
					+ "\n   papAuditFlag = " + papAuditFlag
					+ "\n   papFileSystemAudit = " + papFileSystemAudit
					+ "\n	autoPushFlag = " + autoPushFlag
					+ "\n	papResourceName = " + papResourceName
					+ "\n	fpMonitorInterval = " + fpMonitorInterval
					+ "\n	failedCounterThreshold = " + failedCounterThreshold
					+ "\n	testTransInterval = " + testTransInterval
					+ "\n	writeFpcInterval = " + writeFpcInterval
					+ "\n	papSiteName = " + papSiteName
					+ "\n	papNodeType = " + papNodeType
					+ "\n	papDependencyGroupsList = " + papDependencyGroups
					+ "\n   papIntegrityAuditPeriodSeconds = " + papIntegrityAuditPeriodSeconds
					+ "\n\n**************************************"
					+ "\n**************************************");

			//
			// Pull custom persistence settings
			//

			Properties properties;
			try {
				properties = XACMLProperties.getProperties();//XACMLRestProperties.getProperties();
				logger.debug("\n\n\n**************************************"
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
			im = IntegrityMonitor.getInstance(papResourceName,properties);

			// Create an IntegrityAudit
			ia = new IntegrityAudit(papResourceName, AUDIT_PAP_PERSISTENCE_UNIT, properties);
			ia.startAuditThread();

			//
			// Create the entity manager factory
			//
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
			//
			// Did it get created?
			//
			if (emf == null) {
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Error creating entity manager factory with persistence unit: "
						+ PERSISTENCE_UNIT);
				throw new ServletException("Unable to create Entity Manager Factory");
			}
			//
			// we are about to call the PDPs and give them their configuration.
			// To do that we need to have the URL of this PAP so we can construct the Policy file URLs
			//
			XACMLPapServlet.papURL = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
			/*
			 * Create the PolicyDBDao singleton
			 */		
			//Create the policyDBDao
			policyDBDao = PolicyDBDao.getPolicyDBDaoInstance(getEmf());
			boolean performFileToDatabaseAudit = false;
			if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_RUN_AUDIT_FLAG))){
				if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_AUDIT_FLAG))){
					//get an AuditTransaction to lock out all other transactions
					PolicyDBDaoTransaction auditTrans = policyDBDao.getNewAuditTransaction();
					policyDBDao.auditLocalFileSystem();
					//release the transaction lock
					auditTrans.close();
				}else{
					performFileToDatabaseAudit = true;					
				}
			}



			//
			// Load our PAP engine, first create a factory
			//
			ECOMPPapEngineFactory factory = ECOMPPapEngineFactory.newInstance(XACMLProperties.getProperty(XACMLProperties.PROP_PAP_PAPENGINEFACTORY));
			//
			// The factory knows how to go about creating a PAP Engine
			//
			this.papEngine = (PAPPolicyEngine) factory.newEngine();
			PolicyDBDaoTransaction addNewGroup = null;
			try{

				if(((org.openecomp.policy.xacml.std.pap.StdEngine)papEngine).wasDefaultGroupJustAdded){
					addNewGroup = policyDBDao.getNewTransaction();
					EcompPDPGroup group = papEngine.getDefaultGroup();
					addNewGroup.createGroup(group.getId(), group.getName(), group.getDescription(), "automaticallyAdded");
					addNewGroup.commitTransaction();
					addNewGroup = policyDBDao.getNewTransaction();					
					addNewGroup.changeDefaultGroup(group, "automaticallyAdded");
					addNewGroup.commitTransaction();				
				}

			} catch(Exception e){
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " Error creating new default group in the database");
				if(addNewGroup != null){
					addNewGroup.rollbackTransaction();
				}
			}

			policyDBDao.setPapEngine((PAPPolicyEngine) this.papEngine);


			if(performFileToDatabaseAudit){
				//get an AuditTransaction to lock out all other transactions
				PolicyDBDaoTransaction auditTrans = policyDBDao.getNewAuditTransaction();
				policyDBDao.auditLocalDatabase((PAPPolicyEngine) this.papEngine);
				//release the transaction lock
				auditTrans.close();
			}

			//
			// PDPId File location 
			//
			XACMLPapServlet.pdpFile = XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_IDFILE);
			if (XACMLPapServlet.pdpFile == null) {
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + 
						" The PDP Id Authentication File Property is not valid: "
						+ XACMLRestProperties.PROP_PDP_IDFILE);
				throw new PAPException("The PDP Id Authentication File Property :"+ XACMLRestProperties.PROP_PDP_IDFILE+ " is not Valid. ");
			}
			//
			// Sanity check that a URL was defined somewhere, its essential.
			//
			// How to check that its valid? We can validate the form, but since we are in the init() method we
			// are not fully loaded yet so we really couldn't ping ourself to see if the URL will work. One
			// will have to look for errors in the PDP logs to determine if they are failing to initiate a
			// request to this servlet.
			//
			if (XACMLPapServlet.papURL == null) {

				throw new PAPException("The property " + XACMLRestProperties.PROP_PAP_URL + " is not valid: " + XACMLPapServlet.papURL);
			}
			//
			// Configurable - have the PAP servlet initiate sending the latest PDP policy/pip configuration
			// to all its known PDP nodes.
			//
			// Note: parseBoolean will return false if there is no property defined. This is fine for a default.
			//
			if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_INITIATE_PDP_CONFIG))) {
				this.initiateThread = new Thread(this);
				this.initiateThread.start();
			}
			//
			// After startup, the PAP does Heartbeats to each of the PDPs periodically
			//
			XACMLPapServlet.heartbeat = new Heartbeat((PAPPolicyEngine) this.papEngine);
			XACMLPapServlet.heartbeatThread = new Thread(XACMLPapServlet.heartbeat);
			XACMLPapServlet.heartbeatThread.start();
		} catch (FactoryException | PAPException e) {
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to create engine");
			throw new ServletException (XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP not initialized; error: "+e);
		} catch (Exception e) {
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Failed to create engine - unexpected error");
			throw new ServletException (XACMLErrorConstants.ERROR_SYSTEM_ERROR + "PAP not initialized; unexpected error: "+e);		}
	}

	/**
	 * Thread used only during PAP startup to initiate change messages to all known PDPs.
	 * This must be on a separate thread so that any GET requests from the PDPs during this update can be serviced.
	 */
	@Override
	public void run() {
		//
		// send the current configuration to all the PDPs that we know about
		//
		changed();
	}


	/**
	 * @see Servlet#destroy()
	 * 
	 * Depending on how this servlet is run, we may or may not care about cleaning up the resources.
	 * For now we assume that we do care.
	 */
	public void destroy() {
		//
		// Make sure our threads are destroyed
		//
		if (XACMLPapServlet.heartbeatThread != null) {
			//
			// stop the heartbeat
			//
			try {
				if (XACMLPapServlet.heartbeat != null) {
					XACMLPapServlet.heartbeat.terminate();
				}
				XACMLPapServlet.heartbeatThread.interrupt();
				XACMLPapServlet.heartbeatThread.join();
			} catch (InterruptedException e) {
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Error stopping heartbeat");
			}
		}
		if (this.initiateThread != null) {
			try {
				this.initiateThread.interrupt();
				this.initiateThread.join();
			} catch (InterruptedException e) {
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Error stopping thread");
			}
		}
	}

	/**
	 * 
	 * Called by:
	 * 	- PDP nodes to register themselves with the PAP, and
	 * 	- Admin Console to make changes in the PDP Groups.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ECOMPLoggingContext loggingContext = ECOMPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);

		loggingContext.transactionStarted();
		loggingContext.setServiceName("PAP.post"); // we may set a more specific value later
		if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
			UUID requestID = UUID.randomUUID();
			loggingContext.setRequestID(requestID.toString());
			PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doPost) so we generated one");
		} else {
			PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doPost)");
		}
		// dummy metric.log example posted below as proof of concept
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 1 of 2");
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 2 of 2");
		// dummy metric.log example posted above as proof of concept
		PolicyDBDaoTransaction pdpTransaction = null;

		//This im.startTransaction() covers all Post transactions
		try {
			im.startTransaction();
		} catch (AdministrativeStateException ae){
			String message = "POST interface called for PAP " + papResourceName + " but it has an Administrative"
					+ " state of " + im.getStateManager().getAdminState()
					+ "\n Exception Message: " + ae.getMessage();
			logger.info(message);
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			return;
		}catch (StandbyStatusException se) {
			se.printStackTrace();
			String message = "POST interface called for PAP " + papResourceName + " but it has a Standby Status"
					+ " of " + im.getStateManager().getStandbyStatus()
					+ "\n Exception Message: " + se.getMessage();
			logger.info(message);
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			return;
		}

		try {

			XACMLRest.dumpRequest(request);

			// since getParameter reads the content string, explicitly get the content before doing that.
			// Simply getting the inputStream seems to protect it against being consumed by getParameter.
			request.getInputStream();


			String groupId = request.getParameter("groupId");
			String apiflag = request.getParameter("apiflag");

			if (groupId != null) {
				// Is this from the Admin Console or API?
				if(apiflag!=null) {
					if (apiflag.equalsIgnoreCase("api")) {
						// this is from the API so we need to check the client credentials before processing the request
						if(authorizeRequest(request)){
							doACPost(request, response, groupId, loggingContext);
							// Mike B - ended loggingContext transacton & added EELF 'Success' EELF Audit.log message
							loggingContext.transactionEnded();
							PolicyLogger.audit("Transaction Ended Successfully");
							im.endTransaction();
							return;
						} else {
							String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
							PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
							loggingContext.transactionEnded();

							PolicyLogger.audit("Transaction Failed - See Error.log");

							response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
							im.endTransaction();
							return;
						}
					}
				}

				// this is from the Admin Console, so handle separately
				doACPost(request, response, groupId, loggingContext);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Ended Successfully");
				im.endTransaction();
				return;

			}


			//
			//  Request is from a PDP.
			//	It is coming up and asking for its config
			//
			loggingContext.setServiceName("PDP:PAP.register");


			//
			// Get the PDP's ID
			//
			String id = this.getPDPID(request);
			String jmxport = this.getPDPJMX(request);
			//logger.info("doPost from: " + id);
			logger.info("Request(doPost) from PDP coming up: " + id);
			//
			// Get the PDP Object
			//
			EcompPDP pdp = this.papEngine.getPDP(id);
			//
			// Is it known?
			//
			if (pdp == null) {
				logger.info("Unknown PDP: " + id);
				// PDP ID Check is performed Here. 
				if(CheckPDP.validateID(id)){
					pdpTransaction = policyDBDao.getNewTransaction();
					try {
						//this.papEngine.newPDP(id, this.papEngine.getDefaultGroup(), id, "Registered on first startup");
						pdpTransaction.addPdpToGroup(id, this.papEngine.getDefaultGroup().getId(), id, "Registered on first startup", Integer.parseInt(jmxport), "PDP autoregister");
						this.papEngine.newPDP(id, this.papEngine.getDefaultGroup(), id, "Registered on first startup", Integer.parseInt(jmxport));
					} catch (NullPointerException | PAPException | IllegalArgumentException | IllegalStateException | PersistenceException e) {
						pdpTransaction.rollbackTransaction();
						String message = "Failed to create new PDP for id: " + id;
						PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");

						PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " " + message);
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
						im.endTransaction();
						return;
					}
					// get the PDP we just created
					pdp = this.papEngine.getPDP(id);
					if (pdp == null) {
						if(pdpTransaction != null){
							pdpTransaction.rollbackTransaction();
						}
						String message = "Failed to create new PDP for id: " + id;
						PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
						im.endTransaction();
						return;
					}
				} else {
					String message = "PDP is Unauthorized to Connect to PAP: "+ id;
					PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
					loggingContext.transactionEnded();
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "PDP not Authorized to connect to this PAP. Please contact the PAP Admin for registration.");
					PolicyLogger.audit("Transaction Failed - See Error.log");
					im.endTransaction();
					return;
				}
				// get the PDP we just created
				pdp = this.papEngine.getPDP(id);
				if (pdp == null) {
					if(pdpTransaction != null){
						pdpTransaction.rollbackTransaction();
					}
					String message = "Failed to create new PDP for id: " + id;
					PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
					loggingContext.transactionEnded();
					PolicyLogger.audit("Transaction Failed - See Error.log");
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
					im.endTransaction();
					return;
				}
				try{
					pdpTransaction.commitTransaction();
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", "Could not commit transaction to put pdp in the database");
				}
			}

			if (jmxport != null && jmxport != ""){
				((StdPDP) pdp).setJmxPort(Integer.valueOf(jmxport));
			}

			//
			// Get the PDP's Group
			//
			EcompPDPGroup group = this.papEngine.getPDPGroup((EcompPDP) pdp);
			if (group == null) {
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " PDP not associated with any group, even the default");
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "PDP not associated with any group, even the default");
				im.endTransaction();
				return;
			}
			//
			// Determine what group the PDP node is in and get
			// its policy/pip properties.
			//
			Properties policies = group.getPolicyProperties();
			Properties pipconfig = group.getPipConfigProperties();
			//
			// Get the current policy/pip configuration that the PDP has
			//
			Properties pdpProperties = new Properties();
			pdpProperties.load(request.getInputStream());
			logger.info("PDP Current Properties: " + pdpProperties.toString());
			logger.info("Policies: " + (policies != null ? policies.toString() : "null"));
			logger.info("Pip config: " + (pipconfig != null ? pipconfig.toString() : "null"));
			//
			// Validate the node's properties
			//
			boolean isCurrent = this.isPDPCurrent(policies, pipconfig, pdpProperties);
			//
			// Send back current configuration
			//
			if (isCurrent == false) {
				//
				// Tell the PDP we are sending back the current policies/pip config
				//
				logger.info("PDP configuration NOT current.");
				if (policies != null) {
					//
					// Put URL's into the properties in case the PDP needs to
					// retrieve them.
					//
					this.populatePolicyURL(request.getRequestURL(), policies);
					//
					// Copy the properties to the output stream
					//
					policies.store(response.getOutputStream(), "");
				}
				if (pipconfig != null) {
					//
					// Copy the properties to the output stream
					//
					pipconfig.store(response.getOutputStream(), "");
				}
				//
				// We are good - and we are sending them information
				//
				response.setStatus(HttpServletResponse.SC_OK);

				setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
			} else {
				//
				// Tell them they are good
				//
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);

				setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);

			}
			//
			// tell the AC that something changed
			//
			notifyAC();
			loggingContext.transactionEnded();
			auditLogger.info("Success");
			PolicyLogger.audit("Transaction Ended Successfully");
		} catch (PAPException e) {
			if(pdpTransaction != null){
				pdpTransaction.rollbackTransaction();
			}
			logger.debug(XACMLErrorConstants.ERROR_PROCESS_FLOW + "POST exception: " + e, e);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			response.sendError(500, e.getMessage());
			im.endTransaction();
			return;
		}
		//Catch anything that fell through
		loggingContext.transactionEnded();
		PolicyLogger.audit("Transaction Ended");
		im.endTransaction();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ECOMPLoggingContext loggingContext = ECOMPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
		loggingContext.transactionStarted();
		loggingContext.setServiceName("PAP.get"); // we may set a more specific value later
		if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
			UUID requestID = UUID.randomUUID();
			loggingContext.setRequestID(requestID.toString());
			PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doGet) so we generated one");
		} else {
			PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doGet)");
		}
		// dummy metric.log example posted below as proof of concept
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 1 of 2");
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 2 of 2");
		// dummy metric.log example posted above as proof of concept
		try {
			XACMLRest.dumpRequest(request);
			String pathInfo = request.getRequestURI();
			logger.info("path info: " + pathInfo);
			if (pathInfo != null){
				//DO NOT do a im.startTransaction for the test request
				if (pathInfo.equals("/pap/test")) {
					logger.info("Test request received");
					try {
						im.evaluateSanity();
						//If we make it this far, all is well
						String message = "GET:/pap/test called and PAP " + papResourceName + " is OK";
						logger.info(message);
						loggingContext.transactionEnded();
						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.setStatus(HttpServletResponse.SC_OK);
						return;
					}catch (ForwardProgressException fpe){
						//No forward progress is being made
						String message = "GET:/pap/test called and PAP " + papResourceName + " is not making forward progress."
								+ " Exception Message: " + fpe.getMessage();
						logger.info(message);
						PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
						return;
					}catch (AdministrativeStateException ase){
						//Administrative State is locked
						String message = "GET:/pap/test called and PAP " + papResourceName + " Administrative State is LOCKED "
								+ " Exception Message: " + ase.getMessage();
						logger.info(message);
						PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
						return;
					}catch (StandbyStatusException sse){
						//Administrative State is locked
						String message = "GET:/pap/test called and PAP " + papResourceName + " Standby Status is NOT PROVIDING SERVICE "
								+ " Exception Message: " + sse.getMessage();
						logger.info(message);
						PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
						return;
					}catch (Exception e) {
						//A subsystem is not making progress, is locked, standby or is not responding
						String eMsg = e.getMessage();
						if(eMsg == null){
							eMsg = "No Exception Message";
						}
						String message = "GET:/pap/test called and PAP " + papResourceName + " has had a subsystem failure."
								+ " Exception Message: " + eMsg;
						logger.info(message);
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
						response.addHeader("X-ECOMP-SubsystemFailure", ssFailureList);
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
						return;
					}
				}
			}

			//This im.startTransaction() covers all other Get transactions
			try {
				im.startTransaction();
			} catch (AdministrativeStateException ae){
				String message = "GET interface called for PAP " + papResourceName + " but it has an Administrative"
						+ " state of " + im.getStateManager().getAdminState()
						+ "\n Exception Message: " + ae.getMessage();
				logger.info(message);
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
				return;
			}catch (StandbyStatusException se) {
				se.printStackTrace();
				String message = "GET interface called for PAP " + papResourceName + " but it has a Standby Status"
						+ " of " + im.getStateManager().getStandbyStatus()
						+ "\n Exception Message: " + se.getMessage();
				logger.info(message);
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
				return;
			}


			// Request from the API to get the gitPath
			String apiflag = request.getParameter("apiflag");
			if (apiflag!=null) {
				if(authorizeRequest(request)){


					// Request from the API to get the gitPath
					if (apiflag.equalsIgnoreCase("gitPath")) {
						getGitPath(request, response);
						// Mike B - ended loggingContext transacton & added EELF 'Success' EELF Audit.log message
						loggingContext.transactionEnded();
						PolicyLogger.audit("Transaction Ended Successfully");
						im.endTransaction();
						return;
					}

					// Request from the API to get the ActiveVersion from the PolicyVersion table
					if (apiflag.equalsIgnoreCase("version")){
						getActiveVersion(request, response);
						loggingContext.transactionEnded();
						PolicyLogger.audit("Transaction Ended Successfully");
						im.endTransaction();
						return;
					}

					// Request from the API to get the URI from the gitpath
					if (apiflag.equalsIgnoreCase("uri")){
						getSelectedURI(request, response);
						loggingContext.transactionEnded();
						PolicyLogger.audit("Transaction Ended Successfully");
						im.endTransaction();
						return;
					}

				} else {
					String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
					PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
					loggingContext.transactionEnded();

					PolicyLogger.audit("Transaction Failed - See Error.log");
					response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
					im.endTransaction();
					return;
				}

			}


			// Is this from the Admin Console?
			String groupId = request.getParameter("groupId");
			if (groupId != null) {
				// this is from the Admin Console, so handle separately
				doACGet(request, response, groupId, loggingContext);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Ended Successfully");
				im.endTransaction();
				return;
			}

			//
			// Get the PDP's ID
			//
			String id = this.getPDPID(request);
			logger.info("doGet from: " + id);
			//
			// Get the PDP Object
			//
			EcompPDP pdp = this.papEngine.getPDP(id);
			//
			// Is it known?
			//
			if (pdp == null) {
				//
				// Check if request came from localhost
				//
				if (request.getRemoteHost().equals("localhost") ||
						request.getRemoteHost().equals("127.0.0.1") ||
						request.getRemoteHost().equals(request.getLocalAddr())) {
					//
					// Return status information - basically all the groups
					//
					loggingContext.setServiceName("PAP.getGroups");
					Set<EcompPDPGroup> groups = papEngine.getEcompPDPGroups();

					// convert response object to JSON and include in the response
					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(response.getOutputStream(),  groups);
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
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
				im.endTransaction();
				return;
			}

			loggingContext.setServiceName("PAP.getPolicy");

			//
			// Get the PDP's Group
			//
			EcompPDPGroup group = this.papEngine.getPDPGroup((EcompPDP) pdp);
			if (group == null) {
				String message = "No group associated with pdp " + pdp.getId();
				logger.warn(XACMLErrorConstants.ERROR_PERMISSIONS + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
				im.endTransaction();
				return;
			}
			//
			// Which policy do they want?
			//
			String policyId = request.getParameter("id");
			if (policyId == null) {
				String message = "Did not specify an id for the policy";
				logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
				im.endTransaction();
				return;
			}
			PDPPolicy policy = group.getPolicy(policyId);
			if (policy == null) {
				String message = "Unknown policy: " + policyId;
				logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
				im.endTransaction();
				return;
			}
			//
			// Get its stream
			//
			logger.warn("PolicyDebugging: Policy Validity: " + policy.isValid() + "\n "
					+ "Policy Name : " + policy.getName() + "\n Policy URI: " + policy.getLocation().toString() );
			try (InputStream is = policy.getStream(); OutputStream os = response.getOutputStream()) {
				//
				// Send the policy back
				//
				IOUtils.copy(is, os);

				response.setStatus(HttpServletResponse.SC_OK);
				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
			} catch (PAPException e) {
				String message = "Failed to open policy id " + policyId;
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
			}
		}  catch (PAPException e) {
			PolicyLogger.error(MessageCodes.ERROR_UNKNOWN, e, "XACMLPapServlet", " GET exception");
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(500, e.getMessage());
			im.endTransaction();
			return;
		}
		loggingContext.transactionEnded();
		PolicyLogger.audit("Transaction Ended");
		im.endTransaction();
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
	private void updateGroupsFromAPI(HttpServletRequest request, HttpServletResponse response, String groupId, ECOMPLoggingContext loggingContext) throws IOException {
		PolicyDBDaoTransaction acPutTransaction = policyDBDao.getNewTransaction();
		try {


			// for PUT operations the group may or may not need to exist before the operation can be done
			StdPDPGroup group = (StdPDPGroup) papEngine.getGroup(groupId);

			// get the request content into a String
			String json = null;

			// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
			java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
			scanner.useDelimiter("\\A");
			json =  scanner.hasNext() ? scanner.next() : "";
			scanner.close();
			logger.info("JSON request from PolicyEngine API: " + json);

			// convert Object sent as JSON into local object
			ObjectMapper mapper = new ObjectMapper();

			Object objectFromJSON = mapper.readValue(json, StdPDPPolicy.class);

			StdPDPPolicy policy = (StdPDPPolicy) objectFromJSON;

			Set<PDPPolicy> policies = new HashSet<PDPPolicy>();

			if(policy!=null){
				policies.add(policy);
			}

			//Get the current policies from the Group and Add the new one
			Set<PDPPolicy> currentPoliciesInGroup = new HashSet<PDPPolicy>();
			currentPoliciesInGroup = group.getPolicies();

			//If the selected policy is in the group we must remove it because the name is default
			Iterator<PDPPolicy> policyIterator = policies.iterator();
			logger.debug("policyIterator....." + policies);
			while (policyIterator.hasNext()) {
				PDPPolicy selPolicy = policyIterator.next();
				for (PDPPolicy existingPolicy : currentPoliciesInGroup) {
					if (existingPolicy.getId().equals(selPolicy.getId())) {
						group.removePolicyFromGroup(existingPolicy);
						logger.debug("Removing policy: " + existingPolicy);
						break;
					}
				}
			}

			if(currentPoliciesInGroup!=null){
				policies.addAll(currentPoliciesInGroup);
			}
			group.setPolicies(policies);

			// Assume that this is an update of an existing PDP Group
			loggingContext.setServiceName("PolicyEngineAPI:PAP.updateGroup");

			try{
				acPutTransaction.updateGroup(group, "XACMLPapServlet.doACPut");
			} catch(Exception e){
				PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while updating group in the database: "
						+"group="+group.getId());
				throw new PAPException(e.getMessage());	
			}

			papEngine.updateGroup(group);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			response.addHeader("operation", "push");
			response.addHeader("policyId", policy.getId());
			response.addHeader("groupId", groupId);
			if (logger.isDebugEnabled()) {		
				logger.debug("Group '" + group.getId() + "' updated");
			}

			acPutTransaction.commitTransaction();

			notifyAC();

			// Group changed, which might include changing the policies	
			groupChanged(group);
			loggingContext.transactionEnded();
			auditLogger.info("Success");
			PolicyLogger.audit("Transaction Ended Successfully");
			return;
		} catch (PAPException e) {
			acPutTransaction.rollbackTransaction();
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " API PUT exception");
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + "Exception in request to update group from API - See Error.log on on the PAP.";
			response.sendError(500, e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error","addGroupError");
			response.addHeader("message", message);
			return;
		}

	}

	private void getActiveVersion(HttpServletRequest request, HttpServletResponse response) {
		//Setup EntityManager to communicate with the PolicyVersion table of the DB
		EntityManager em = null;
		em = (EntityManager) emf.createEntityManager();

		if (em==null){
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Error creating entity manager with persistence unit: " + PERSISTENCE_UNIT);
			try {
				throw new Exception("Unable to create Entity Manager Factory");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String policyScope = request.getParameter("policyScope");
		String filePrefix = request.getParameter("filePrefix");
		String policyName = request.getParameter("policyName");

		String pvName = policyScope + File.separator + filePrefix + policyName;
		int activeVersion = 0;


		//Get the Active Version to use in the ID
		em.getTransaction().begin();
		Query query = em.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
		query.setParameter("pname", pvName);

		@SuppressWarnings("rawtypes")
		List result = query.getResultList();
		PolicyVersion versionEntity = null;
		if (!result.isEmpty()) {
			versionEntity = (PolicyVersion) result.get(0);
			em.persist(versionEntity);
			activeVersion = versionEntity.getActiveVersion();
			em.getTransaction().commit();
		} else {
			logger.debug("No PolicyVersion using policyName found");
		}

		//clean up connection
		em.close();
		if (String.valueOf(activeVersion)!=null || !String.valueOf(activeVersion).equalsIgnoreCase("")) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("version", String.valueOf(activeVersion));								
		} else {						
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);								
		}	


	}

	private void getSelectedURI(HttpServletRequest request,
			HttpServletResponse response) {

		String gitPath = request.getParameter("gitPath");

		File file = new File(gitPath);

		logger.debug("The fileItem is : " + file.toString());

		URI selectedURI = file.toURI();

		String uri = selectedURI.toString();

		if (!uri.equalsIgnoreCase("")) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("selectedURI", uri);								
		} else {						
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);								
		}						
	}

	/*
	 * getGitPath() method to get the gitPath using data from the JSON string 
	 * when deleting policies using doAPIDelete()
	 */
	private File getPolicyFile(String policyName){

		Path workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE), "admin");
		Path repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY));
		Path gitPath = Paths.get(workspacePath.toString(), repositoryPath.getFileName().toString());

		//getting the fullpath of the gitPath and convert to string
		String fullGitPath = gitPath.toAbsolutePath().toString();
		String finalGitPath = null;

		//creating the parentPath directory for the Admin Console use
		if(fullGitPath.contains("\\")){
			finalGitPath = fullGitPath.replace("ECOMP-PAP-REST", "ecomp-sdk-app");
		}else{
			finalGitPath = fullGitPath.replace("pap",  "console");
		}

		finalGitPath += File.separator + policyName;

		File file = new File(finalGitPath);

		return file;

	}

	/*
	 * getGitPath() method to get the gitPath using data from the http request
	 * and send back in response when pushing policies
	 */
	private void getGitPath(HttpServletRequest request,
			HttpServletResponse response) {

		String policyScope = request.getParameter("policyScope");
		String filePrefix = request.getParameter("filePrefix");
		String policyName = request.getParameter("policyName");
		String activeVersion = request.getParameter("activeVersion");

		Path workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE), "admin");
		Path repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY));
		Path gitPath = Paths.get(workspacePath.toString(), repositoryPath.getFileName().toString());

		//getting the fullpath of the gitPath and convert to string
		String fullGitPath = gitPath.toAbsolutePath().toString();
		String finalGitPath = null;

		//creating the parentPath directory for the Admin Console use
		if(fullGitPath.contains("\\")){
			finalGitPath = fullGitPath.replace("ECOMP-PAP-REST", "ecomp-sdk-app");
		}else{
			finalGitPath = fullGitPath.replace("pap",  "console");
		}

		finalGitPath += File.separator + policyScope + File.separator + filePrefix + policyName + "." + activeVersion + ".xml";
		File file = new File(finalGitPath);
		URI uri = file.toURI();
		
		//
		// Extract XACML policy information
		//
		Boolean isValid = false;
		String policyId = null;
		String description = null;
		String	version = null;

		URL url;
		try {
			url = uri.toURL();
			Object rootElement = XACMLPolicyScanner.readPolicy(url.openStream());
			if (rootElement == null ||
					(
							! (rootElement instanceof PolicySetType) &&
							! (rootElement instanceof PolicyType)
							)	) {
				logger.warn("No root policy element in URI: " + uri.toString() + " : " + rootElement);
				isValid = false;
			} else {
				if (rootElement instanceof PolicySetType) {
					policyId = ((PolicySetType)rootElement).getPolicySetId();
					description = ((PolicySetType)rootElement).getDescription();
					isValid = true;
					version = ((PolicySetType)rootElement).getVersion();
				} else if (rootElement instanceof PolicyType) {
					policyId = ((PolicyType)rootElement).getPolicyId();
					description = ((PolicyType)rootElement).getDescription();
					version = ((PolicyType)rootElement).getVersion();
					isValid = true;
				} else {
					PolicyLogger.error("Unknown root element: " + rootElement.getClass().getCanonicalName());
				}
			}
		} catch (Exception e) {
			logger.error("Exception Occured While Extracting Policy Information");
		} 

		if (!finalGitPath.equalsIgnoreCase("") || policyId!=null || description!=null || version!=null || isValid!=null) {							
			response.setStatus(HttpServletResponse.SC_OK);								
			response.addHeader("gitPath", finalGitPath);
			response.addHeader("policyId", policyId);
			response.addHeader("description", description);
			response.addHeader("version", version);
			response.addHeader("isValid", isValid.toString());
		} else {						
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);								
		}						

	}

	/**
	 * Given a version string consisting of integers with dots between them, convert it into an array of ints.
	 * 
	 * @param version
	 * @return
	 * @throws NumberFormatException
	 */
	public static int[] versionStringToArray(String version) throws NumberFormatException {
		if (version == null || version.length() == 0) {
			return new int[0];
		}
		String[] stringArray = version.split("\\.");
		int[] resultArray = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			resultArray[i] = Integer.parseInt(stringArray[i]);
		}
		return resultArray;
	}

	protected String	getPDPID(HttpServletRequest request) {
		String pdpURL = request.getHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID);
		if (pdpURL == null || pdpURL.isEmpty()) {
			//
			// Should send back its port for identification
			//
			logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP did not send custom header");
			pdpURL = "";
		}
		return  pdpURL;
	}

	protected String getPDPJMX(HttpServletRequest request) {
		String pdpJMMX = request.getHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_JMX_PORT);
		if (pdpJMMX == null || pdpJMMX.isEmpty()) {
			//
			// Should send back its port for identification
			//
			logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "PDP did not send custom header for JMX Port so the value of 0 is assigned");
			return null;
		}
		return pdpJMMX;
	}
	private boolean isPDPCurrent(Properties policies, Properties pipconfig, Properties pdpProperties) {
		String localRootPolicies = policies.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
		String localReferencedPolicies = policies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
		if (localRootPolicies == null || localReferencedPolicies == null) {
			logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + "Missing property on PAP server: RootPolicies="+localRootPolicies+"  ReferencedPolicies="+localReferencedPolicies);
			return false;
		}
		//
		// Compare the policies and pipconfig properties to the pdpProperties
		//
		try {
			//
			// the policy properties includes only xacml.rootPolicies and 
			// xacml.referencedPolicies without any .url entries
			//
			Properties pdpPolicies = XACMLProperties.getPolicyProperties(pdpProperties, false);
			Properties pdpPipConfig = XACMLProperties.getPipProperties(pdpProperties);
			if (localRootPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_ROOTPOLICIES)) &&
					localReferencedPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES)) &&
					pdpPipConfig.equals(pipconfig)) {
				//
				// The PDP is current
				//
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
					logger.info("Policy URL for " + id + ": " + url);
					policies.setProperty(id + ".url", url);
				}
			}
		}
	}


	/**
	 * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ECOMPLoggingContext loggingContext = ECOMPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
		storedRequestId = loggingContext.getRequestID();
		loggingContext.transactionStarted();
		loggingContext.setServiceName("PAP.put"); // we may set a more specific value later
		if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
			UUID requestID = UUID.randomUUID();
			loggingContext.setRequestID(requestID.toString());
			PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doPut) so we generated one");
		} else {
			PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doPut)");
		}
		// dummy metric.log example posted below as proof of concept
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 1 of 2");
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 2 of 2");
		//This im.startTransaction() covers all Put transactions
		try {
			im.startTransaction();
		} catch (AdministrativeStateException ae){
			String message = "PUT interface called for PAP " + papResourceName + " but it has an Administrative"
					+ " state of " + im.getStateManager().getAdminState()
					+ "\n Exception Message: " + ae.getMessage();
			logger.info(message);
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			return;
		}catch (StandbyStatusException se) {
			se.printStackTrace();
			String message = "PUT interface called for PAP " + papResourceName + " but it has a Standby Status"
					+ " of " + im.getStateManager().getStandbyStatus()
					+ "\n Exception Message: " + se.getMessage();
			logger.info(message);
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			return;
		}

		XACMLRest.dumpRequest(request);

		//
		// since getParameter reads the content string, explicitly get the content before doing that.
		// Simply getting the inputStream seems to protect it against being consumed by getParameter.
		//
		request.getInputStream();

		//need to check if request is from the API or Admin console
		String apiflag = request.getParameter("apiflag");

		//This would occur if a PolicyDBDao notification was received
		String policyDBDaoRequestUrl = request.getParameter("policydbdaourl");
		if(policyDBDaoRequestUrl != null){
			String policyDBDaoRequestEntityId = request.getParameter("entityid");
			//String policyDBDaoRequestEntityType = request.getParameter("entitytype");
			String policyDBDaoRequestEntityType = request.getParameter("entitytype");
			String policyDBDaoRequestExtraData = request.getParameter("extradata");
			if(policyDBDaoRequestEntityId == null || policyDBDaoRequestEntityType == null){
				response.sendError(400, "entityid or entitytype not supplied");
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Ended Successfully");
				im.endTransaction();
				return;
			}
			policyDBDao.handleIncomingHttpNotification(policyDBDaoRequestUrl,policyDBDaoRequestEntityId,policyDBDaoRequestEntityType,policyDBDaoRequestExtraData,this);			
			response.setStatus(200);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Ended Successfully");
			im.endTransaction();
			return;
		}

		//This would occur if we received a notification of a policy creation or update
		String policyToCreateUpdate = request.getParameter("policyToCreateUpdate");
		if(policyToCreateUpdate != null){
			if(logger.isDebugEnabled()){
				logger.debug("\nXACMLPapServlet.doPut() - before decoding"
						+ "\npolicyToCreateUpdate = " + policyToCreateUpdate);
			}
			//decode it
			try{
				policyToCreateUpdate = URLDecoder.decode(policyToCreateUpdate, "UTF-8");
				if(logger.isDebugEnabled()){
					logger.debug("\nXACMLPapServlet.doPut() - after decoding"
							+ "\npolicyToCreateUpdate = " + policyToCreateUpdate);
				}
			} catch(UnsupportedEncodingException e){
				PolicyLogger.error("\nXACMLPapServlet.doPut() - Unsupported URL encoding of policyToCreateUpdate (UTF-8)"
						+ "\npolicyToCreateUpdate = " + policyToCreateUpdate);
				response.sendError(500,"policyToCreateUpdate encoding not supported"
						+ "\nfailure with the following exception: " + e);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See error.log");
				im.endTransaction();
				return;
			}

			//send it to PolicyDBDao
			PolicyDBDaoTransaction createUpdateTransaction = policyDBDao.getNewTransaction();
			try{
				createUpdateTransaction.createPolicy(policyToCreateUpdate, "XACMLPapServlet.doPut");
			}catch(Exception e){
				createUpdateTransaction.rollbackTransaction();
				response.sendError(500,"createUpdateTransaction.createPolicy(policyToCreateUpdate, XACMLPapServlet.doPut) "
						+ "\nfailure with the following exception: " + e);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See error.log");
				im.endTransaction();
				return;
			}
			createUpdateTransaction.commitTransaction();
			// Before sending Ok. Lets call AutoPush. 
			if(autoPushFlag){
				Set<StdPDPGroup> changedGroups = autoPushPolicy.checkGroupsToPush(policyToCreateUpdate,  this.papEngine);
				if(!changedGroups.isEmpty()){
					for(StdPDPGroup group: changedGroups){
						try{
							papEngine.updateGroup(group);
							if (logger.isDebugEnabled()) {		
								logger.debug("Group '" + group.getId() + "' updated");
							}
							notifyAC();
							// Group changed, which might include changing the policies	
							groupChanged(group);
						}catch(Exception e){
							PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Failed to Push policy. ");
						}
					}
				}
			}
			response.setStatus(HttpServletResponse.SC_OK);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Ended Successfully");
			im.endTransaction();
			return;
		}

		/*
		 * Request for Micro Service Import
		 */
		String microServiceCreation = request.getParameter("importService");
		if (microServiceCreation != null) {
			if(authorizeRequest(request)){   
				if (microServiceCreation.contains("MICROSERVICE")){
					doImportMicroServicePut(request, response);
					im.endTransaction();
					return;
				}
			} else {
				String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
				logger.error(XACMLErrorConstants.ERROR_PERMISSIONS + message );
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
				return;
			}
		}
		//This would occur if we received a notification of a policy rename from AC
		String oldPolicyName = request.getParameter("oldPolicyName");
		String newPolicyName = request.getParameter("newPolicyName");
		if(oldPolicyName != null && newPolicyName != null){
			if(logger.isDebugEnabled()){
				logger.debug("\nXACMLPapServlet.doPut() - before decoding"
						+ "\npolicyToCreateUpdate = " + " ");
			}
			//decode it
			try{
				oldPolicyName = URLDecoder.decode(oldPolicyName, "UTF-8");
				newPolicyName = URLDecoder.decode(newPolicyName, "UTF-8");
				if(logger.isDebugEnabled()){
					logger.debug("\nXACMLPapServlet.doPut() - after decoding"
							+ "\npolicyToCreateUpdate = " + " ");
				}
			} catch(UnsupportedEncodingException e){
				PolicyLogger.error("\nXACMLPapServlet.doPut() - Unsupported URL encoding of policyToCreateUpdate (UTF-8)"
						+ "\npolicyToCreateUpdate = " + " ");
				response.sendError(500,"policyToCreateUpdate encoding not supported"
						+ "\nfailure with the following exception: " + e);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See error.log");
				im.endTransaction();
				return;
			}
			//send it to PolicyDBDao
			PolicyDBDaoTransaction renameTransaction = policyDBDao.getNewTransaction();
			try{
				renameTransaction.renamePolicy(oldPolicyName,newPolicyName, "XACMLPapServlet.doPut");
			}catch(Exception e){
				renameTransaction.rollbackTransaction();
				response.sendError(500,"createUpdateTransaction.createPolicy(policyToCreateUpdate, XACMLPapServlet.doPut) "
						+ "\nfailure with the following exception: " + e);
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See error.log");
				im.endTransaction();
				return;
			}
			renameTransaction.commitTransaction();
			response.setStatus(HttpServletResponse.SC_OK);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Ended Successfully");
			im.endTransaction();
			return;
		}


		//
		// See if this is Admin Console registering itself with us
		//
		String acURLString = request.getParameter("adminConsoleURL");
		if (acURLString != null) {
			loggingContext.setServiceName("AC:PAP.register");
			//
			// remember this Admin Console for future updates
			//
			if ( ! adminConsoleURLStringList.contains(acURLString)) {
				adminConsoleURLStringList.add(acURLString);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Admin Console registering with URL: " + acURLString);
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
		 * Part of a 2 step process to push policie to the PDP that can now be done 
		 * From both the Admin Console and the PolicyEngine API
		 */
		String groupId = request.getParameter("groupId");
		if (groupId != null) {
			if(apiflag!=null){
				if(apiflag.equalsIgnoreCase("addPolicyToGroup")){
					updateGroupsFromAPI(request, response, groupId, loggingContext);
					loggingContext.transactionEnded();
					PolicyLogger.audit("Transaction Ended Successfully");
					im.endTransaction();
					return;
				}
			}
			//
			// this is from the Admin Console, so handle separately
			//
			doACPut(request, response, groupId, loggingContext);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Ended Successfully");
			im.endTransaction();
			return;
		}

		//
		// Request is for policy validation and creation
		//
		if (apiflag != null && apiflag.equalsIgnoreCase("admin")){
			/*
			 * this request is from the Admin Console
			 */
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Ended Successfully");
			doACPolicyPut(request, response);
			im.endTransaction();
			return;

		} else if (apiflag != null && apiflag.equalsIgnoreCase("api")) {
			/*
			 * this request is from the Policy Creation API
			 */
			// Authenticating the Request here. 
			if(authorizeRequest(request)){
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Ended Successfully");
				doPolicyAPIPut(request, response);
				im.endTransaction();
				return;
			} else {
				String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
				PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
				im.endTransaction();
				return;
			}

		}


		//
		// We do not expect anything from anywhere else.
		// This method is here in case we ever need to support other operations.
		//
		logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Request does not have groupId or apiflag");
		loggingContext.transactionEnded();

		PolicyLogger.audit("Transaction Failed - See Error.log");
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId or apiflag");
		loggingContext.transactionEnded();
		PolicyLogger.audit("Transaction Failed - See error.log");
		im.endTransaction();
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ECOMPLoggingContext loggingContext = ECOMPLoggingUtils.getLoggingContextForRequest(request, baseLoggingContext);
		loggingContext.transactionStarted();
		loggingContext.setServiceName("PAP.delete"); // we may set a more specific value later
		if ((loggingContext.getRequestID() == null) || (loggingContext.getRequestID() == "")){
			UUID requestID = UUID.randomUUID();
			loggingContext.setRequestID(requestID.toString());
			PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (doDelete) so we generated one");
		} else {
			PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (doDelete)");
		}
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 1 of 2");
		loggingContext.metricStarted();
		loggingContext.metricEnded();
		PolicyLogger.metrics("Metric example posted here - 2 of 2");	

		//This im.startTransaction() covers all Delete transactions
		try {
			im.startTransaction();
		} catch (AdministrativeStateException ae){
			String message = "DELETE interface called for PAP " + papResourceName + " but it has an Administrative"
					+ " state of " + im.getStateManager().getAdminState()
					+ "\n Exception Message: " + ae.getMessage();
			logger.info(message);
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			return;
		}catch (StandbyStatusException se) {
			se.printStackTrace();
			String message = "PUT interface called for PAP " + papResourceName + " but it has a Standby Status"
					+ " of " + im.getStateManager().getStandbyStatus()
					+ "\n Exception Message: " + se.getMessage();
			logger.info(message);
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			return;
		}

		XACMLRest.dumpRequest(request);

		String groupId = request.getParameter("groupId");
		String apiflag = request.getParameter("apiflag");

		if (groupId != null) {
			// Is this from the Admin Console or API?
			if(apiflag!=null) {
				if (apiflag.equalsIgnoreCase("deletePapApi")) {
					// this is from the API so we need to check the client credentials before processing the request
					if(authorizeRequest(request)){
						doAPIDeleteFromPAP(request, response, loggingContext);
						return;
					} else {
						String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
						PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
						return;
					}
				} else if (apiflag.equalsIgnoreCase("deletePdpApi")) {
					if(authorizeRequest(request)){
						doAPIDeleteFromPDP(request, response, loggingContext);
						return;
					} else {
						String message = "PEP not Authorized for making this Request!! \n Contact Administrator for this Scope. ";
						PolicyLogger.error(MessageCodes.ERROR_PERMISSIONS + " " + message);
						loggingContext.transactionEnded();

						PolicyLogger.audit("Transaction Failed - See Error.log");
						response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
						return;
					}
				}
			}

			// this is from the Admin Console, so handle separately
			doACDelete(request, response, groupId, loggingContext);
			loggingContext.transactionEnded();
			PolicyLogger.audit("Transaction Ended Successfully");
			im.endTransaction();
			return;

		}
		//
		// We do not expect anything from anywhere else.
		// This method is here in case we ever need to support other operations.
		//
		PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Request does not have groupId");
		loggingContext.transactionEnded();

		PolicyLogger.audit("Transaction Failed - See Error.log");

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId");

		//Catch anything that fell through
		im.endTransaction();

	}
	//
	// Admin Console request handling
	//

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
	private void doACGet(HttpServletRequest request, HttpServletResponse response, String groupId, ECOMPLoggingContext loggingContext) throws ServletException, IOException {
		try {
			String parameterDefault = request.getParameter("default");
			String pdpId = request.getParameter("pdpId");
			String pdpGroup = request.getParameter("getPDPGroup");
			if ("".equals(groupId)) {
				// request IS from AC but does not identify a group by name
				if (parameterDefault != null) {
					// Request is for the Default group (whatever its id)
					loggingContext.setServiceName("AC:PAP.getDefaultGroup");

					EcompPDPGroup group = papEngine.getDefaultGroup();

					// convert response object to JSON and include in the response
					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(response.getOutputStream(),  group);

					if (logger.isDebugEnabled()) {
						logger.debug("GET Default group req from '" + request.getRequestURL() + "'");
					}
					response.setStatus(HttpServletResponse.SC_OK);
					response.setHeader("content-type", "application/json");
					response.getOutputStream().close();
					loggingContext.transactionEnded();
					auditLogger.info("Success");
					PolicyLogger.audit("Transaction Ended Successfully");
					return;

				} else if (pdpId != null) {
					// Request is related to a PDP
					if (pdpGroup == null) {
						// Request is for the PDP itself
						// Request is for the (unspecified) group containing a given PDP
						loggingContext.setServiceName("AC:PAP.getPDP");
						EcompPDP pdp = papEngine.getPDP(pdpId);

						// convert response object to JSON and include in the response
						ObjectMapper mapper = new ObjectMapper();
						mapper.writeValue(response.getOutputStream(),  pdp);

						if (logger.isDebugEnabled()) {
							logger.debug("GET pdp '" + pdpId + "' req from '" + request.getRequestURL() + "'");
						}
						response.setStatus(HttpServletResponse.SC_OK);
						response.setHeader("content-type", "application/json");
						response.getOutputStream().close();
						loggingContext.transactionEnded();
						auditLogger.info("Success");
						PolicyLogger.audit("Transaction Ended Successfully");
						return;

					} else {
						// Request is for the group containing a given PDP
						loggingContext.setServiceName("AC:PAP.getGroupForPDP");
						EcompPDP pdp = papEngine.getPDP(pdpId);
						EcompPDPGroup group = papEngine.getPDPGroup((EcompPDP) pdp);

						// convert response object to JSON and include in the response
						ObjectMapper mapper = new ObjectMapper();
						mapper.writeValue(response.getOutputStream(),  group);

						if (logger.isDebugEnabled()) {
							logger.debug("GET PDP '" + pdpId + "' Group req from '" + request.getRequestURL() + "'");
						}
						response.setStatus(HttpServletResponse.SC_OK);
						response.setHeader("content-type", "application/json");
						response.getOutputStream().close();
						loggingContext.transactionEnded();
						auditLogger.info("Success");
						PolicyLogger.audit("Transaction Ended Successfully");
						return;
					}

				} else {
					// request is for top-level properties about all groups
					loggingContext.setServiceName("AC:PAP.getAllGroups");
					Set<EcompPDPGroup> groups = papEngine.getEcompPDPGroups();

					// convert response object to JSON and include in the response
					ObjectMapper mapper = new ObjectMapper();
					mapper.writeValue(response.getOutputStream(),  groups);

					if (logger.isDebugEnabled()) {
						logger.debug("GET All groups req");
					}
					response.setStatus(HttpServletResponse.SC_OK);
					response.setHeader("content-type", "application/json");
					response.getOutputStream().close();
					loggingContext.transactionEnded();
					auditLogger.info("Success");
					PolicyLogger.audit("Transaction Ended Successfully");
					return;
				}
			}

			// for all other GET operations the group must exist before the operation can be done
			EcompPDPGroup group = papEngine.getGroup(groupId);
			if (group == null) {
				String message = "Unknown groupId '" + groupId + "'";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
				return;
			}


			// Figure out which request this is based on the parameters
			String policyId = request.getParameter("policyId");

			if (policyId != null) {
				// retrieve a policy
				loggingContext.setServiceName("AC:PAP.getPolicy");
				//
				// convert response object to JSON and include in the response
				//
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " GET Policy not implemented");
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");

				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "GET Policy not implemented");

			} else {
				// No other parameters, so return the identified Group
				loggingContext.setServiceName("AC:PAP.getGroup");

				// convert response object to JSON and include in the response
				ObjectMapper mapper = new ObjectMapper();
				mapper.writeValue(response.getOutputStream(),  group);

				if (logger.isDebugEnabled()) {
					logger.debug("GET group '" + group.getId() + "' req from '" + request.getRequestURL() + "'");
				}
				response.setStatus(HttpServletResponse.SC_OK);
				response.setHeader("content-type", "application/json");
				response.getOutputStream().close();
				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;
			}

			//
			// Currently there are no other GET calls from the AC.
			// The AC uses the "GET All Groups" operation to fill its local cache and uses that cache for all other GETs without calling the PAP.
			// Other GETs that could be called:
			//				Specific Group	(groupId=<groupId>)
			//				A Policy		(groupId=<groupId> policyId=<policyId>)
			//				A PDP			(groupId=<groupId> pdpId=<pdpId>)

			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " UNIMPLEMENTED ");
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
		} catch (PAPException e) {
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC Get exception");
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(500, e.getMessage());
			return;
		}

	}

	/**
	 * Requests from the Admin Console for validating and creating policies
	 * 
	 * @param request
	 * @param response
	 * @param groupId
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doACPolicyPut(HttpServletRequest request,
			HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {

		String operation = request.getParameter("operation");
		String policyType = request.getParameter("policyType");
		String apiflag = request.getParameter("apiflag"); 

		if ( policyType != null ) {
			PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
			Policy newPolicy = null;
			// get the request content into a String
			String json = null;
			// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
			java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
			scanner.useDelimiter("\\A");
			json =  scanner.hasNext() ? scanner.next() : "";
			scanner.close();
			logger.info("JSON request from AC: " + json);
			// convert Object sent as JSON into local object
			ObjectMapper mapper = new ObjectMapper();
			Object objectFromJSON = mapper.readValue(json, StdPAPPolicy.class);

			StdPAPPolicy policy = (StdPAPPolicy) objectFromJSON;

			//Set policyAdapter values including parentPath (Common to all policy types)
			//Set values for policy adapter
			try {
				if (operation.equalsIgnoreCase("validate")) {
					policyAdapter.setPolicyName(policy.getPolicyName());
					policyAdapter.setConfigType(policy.getConfigType());
					policyAdapter.setConfigBodyData(policy.getConfigBodyData());
				} else {
					policyAdapter = setDataToPolicyAdapter(policy, policyType, apiflag);
				}
			} catch (Exception e1) {
				logger.error("Exception occured While Setting Values for Policy Adapter"+e1);
			}
			// Calling Component class per policy type
			if (policyType.equalsIgnoreCase("Config")) {
				String configPolicyType = policy.getConfigPolicyType();
				if (configPolicyType != null && configPolicyType.equalsIgnoreCase("Firewall Config")) {
					newPolicy = new FirewallConfigPolicy(policyAdapter);
				} 
				else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("BRMS_Raw")) {
					newPolicy = new CreateBrmsRawPolicy(policyAdapter);
				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("BRMS_Param")) {
					policyAdapter.setBrmsParamBody(policy.getDrlRuleAndUIParams());
					newPolicy = new CreateBrmsParamPolicy(policyAdapter);
				}
				else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("Base")) {
					newPolicy =  new ConfigPolicy(policyAdapter);
				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("ClosedLoop_Fault")) {
					newPolicy = new ClosedLoopPolicy(policyAdapter);
				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("ClosedLoop_PM")) {
					newPolicy = new CreateClosedLoopPerformanceMetrics(policyAdapter);	
				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("DCAE Micro Service")) {	
					newPolicy = new MicroServiceConfigPolicy(policyAdapter);
				}

			} else if (policyType.equalsIgnoreCase("Action")) {
				newPolicy = new ActionPolicy(policyAdapter);
			} else if (policyType.equalsIgnoreCase("Decision")) {
				newPolicy = new DecisionPolicy(policyAdapter);	
			}

			// Validation
			if (operation != null && operation.equalsIgnoreCase("validate")) {

				// validate the body data if applicable and return a response to the PAP-ADMIN	(Config Base only)
				if (newPolicy.validateConfigForm()) {					
					response.setStatus(HttpServletResponse.SC_OK);
					response.addHeader("isValidData", "true");					
				} else {	
					response.setStatus(HttpServletResponse.SC_OK);	
					response.addHeader("isValidData", "false");
				}

			}   

			// Create or Update Policy        
			if (operation != null && (operation.equalsIgnoreCase("create") || operation.equalsIgnoreCase("update"))) {

				// create the policy and return a response to the PAP-ADMIN		        
				PolicyDBDaoTransaction policyDBDaoTransaction = policyDBDao.getNewTransaction();
				try {
					Map<String, String> successMap;
					newPolicy.prepareToSave();
					policyDBDaoTransaction.createPolicy(newPolicy, "doACPolicyPut");
					successMap = newPolicy.savePolicies();
					if (successMap.containsKey("success")) {
						policyDBDaoTransaction.commitTransaction();
						response.setStatus(HttpServletResponse.SC_OK);
						response.addHeader("successMapKey", "success");		    						    				
						response.addHeader("finalPolicyPath", policyAdapter.getFinalPolicyPath());	
					} else {								
						policyDBDaoTransaction.rollbackTransaction();
						response.setStatus(HttpServletResponse.SC_OK);								
					}	
				} catch (Exception e) {	
					policyDBDaoTransaction.rollbackTransaction();
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Could not save policy ");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}		        	
			}

		}

	}

	private void doPolicyAPIPut(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		String operation = request.getParameter("operation");
		String policyType = request.getParameter("policyType");
		String apiflag = request.getParameter("apiflag");

		
		if ( policyType != null ) {
			PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
			Policy newPolicy = null;

			// get the request content into a String
			String json = null;

			// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
			java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
			scanner.useDelimiter("\\A");
			json =  scanner.hasNext() ? scanner.next() : "";
			scanner.close();
			logger.info("JSON request from API: " + json);

			// convert Object sent as JSON into local object
			ObjectMapper mapper = new ObjectMapper();

			Object objectFromJSON = mapper.readValue(json, StdPAPPolicy.class);

			StdPAPPolicy policy = (StdPAPPolicy) objectFromJSON;

			//Set policyAdapter values including parentPath (Common to all policy types)
			try {
				policyAdapter = setDataToPolicyAdapter(policy, policyType, apiflag);
			} catch (Exception e1) {
				logger.error(XACMLErrorConstants.ERROR_UNKNOWN + 
						"Could not set data to policy adapter ",e1);
			}

			// Calling Component class per policy type
			if (policyType.equalsIgnoreCase("Config")) {
				String configPolicyType = policy.getConfigPolicyType();
				if (configPolicyType != null && configPolicyType.equalsIgnoreCase("Firewall Config")) {

					newPolicy = new FirewallConfigPolicy(policyAdapter);

				} 
				else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("BRMS_Raw")) { 

					newPolicy = new CreateBrmsRawPolicy(policyAdapter);

				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("BRMS_Param")) {

					policyAdapter.setBrmsParamBody(policy.getDrlRuleAndUIParams());
					//check for valid actionAttributes
					//Setup EntityManager to communicate with the PolicyVersion table of the DB
					EntityManager em = null;
					em = (EntityManager) emf.createEntityManager();

					Map<String,String> ruleAndUIValue=policyAdapter.getBrmsParamBody();
					String modelName= ruleAndUIValue.get("templateName");
					logger.info("Template name from API is: "+modelName);

					Query getModel = em.createNamedQuery("BRMSParamTemplate.findAll");	
					List<?> modelList = getModel.getResultList(); 	
					Boolean isValidService = false;
					for (Object id : modelList) {
						BRMSParamTemplate value = (BRMSParamTemplate)id;
						logger.info("Template value from dictionary is: "+value);
						if (modelName.equals(value.getRuleName())) {
							isValidService = true;
							break;
						}
					}

					em.close();

					if (isValidService) {
						newPolicy = new CreateBrmsParamPolicy(policyAdapter);
					} else {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Template.  The template name, " 
								+ modelName 
								+ " was not found in the dictionary.");
						response.addHeader("error", "missingTemplate");	
						response.addHeader("modelName", modelName);
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);								
						return;
					}
				}
				else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("Base")) {

					newPolicy =  new ConfigPolicy(policyAdapter);

				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("ClosedLoop_Fault")) {

					newPolicy = new ClosedLoopPolicy(policyAdapter);

				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("ClosedLoop_PM")) {

					newPolicy = new CreateClosedLoopPerformanceMetrics(policyAdapter);

				}else if (configPolicyType != null && configPolicyType.equalsIgnoreCase("DCAE Micro Service")) {

					//check for valid actionAttributes
					//Setup EntityManager to communicate with the PolicyVersion table of the DB
					EntityManager em = null;
					em = (EntityManager) emf.createEntityManager();

					String modelName = policy.getServiceType();
					String modelVersion = policy.getVersion();

					Query getModel = em.createNamedQuery("MicroServiceModels.findAll");	
					List<?> modelList = getModel.getResultList(); 	
					Boolean isValidService = false;
					for (Object id : modelList) {
						MicroServiceModels value = (MicroServiceModels)id;
						if (modelName.equals(value.getModelName()) && modelVersion.equals(value.getVersion())) {
							isValidService = true;
							break;
						}
					}

					em.close();

					if (isValidService) {
						newPolicy = new MicroServiceConfigPolicy(policyAdapter);
					} else {
						logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid Service or Version.  The Service Model, " 
								+ modelName + " of version " + modelVersion 
								+ " was not found in the dictionary.");
						response.addHeader("error", "serviceModelDB");	
						response.addHeader("modelName", modelName);
						response.addHeader("modelVersion", modelVersion);
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);								
						return;
					}

				}

			} else if (policyType.equalsIgnoreCase("Action")) {

				//check for valid actionAttributes
				//Setup EntityManager to communicate with the PolicyVersion table of the DB
				EntityManager em = null;
				em = (EntityManager) emf.createEntityManager();

				String attributeName = policy.getActionAttribute();

				Query getActionAttributes = em.createNamedQuery("ActionPolicyDict.findAll");	
				List<?> actionAttributesList = getActionAttributes.getResultList(); 	
				Boolean isAttribute = false;
				for (Object id : actionAttributesList) {
					ActionPolicyDict value = (ActionPolicyDict)id;
					if (attributeName.equals(value.getAttributeName())) {
						isAttribute = true;
						break;
					}
				}

				em.close();

				if (isAttribute) {
					newPolicy = new ActionPolicy(policyAdapter);
				} else {
					logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Could not fine " + attributeName + " in the ActionPolicyDict table.");
					response.addHeader("error", "actionPolicyDB");	
					response.addHeader("actionAttribute", attributeName);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);								
					return;
				}

			} else if (policyType.equalsIgnoreCase("Decision")) {

				newPolicy = new DecisionPolicy(policyAdapter);

			}

			// Create or Update Policy        
			if (operation != null && (operation.equalsIgnoreCase("create") || operation.equalsIgnoreCase("update"))) {

				// create the policy and return a response to the PAP-ADMIN		        
				if (newPolicy.validateConfigForm()) {		        		
					PolicyDBDaoTransaction policyDBDaoTransaction = policyDBDao.getNewTransaction();
					try {	

						// added check for existing policy when new policy is created to 
						// unique API error for "policy already exists" 
						Boolean isNewPolicy = newPolicy.prepareToSave();
						if(isNewPolicy){
							policyDBDaoTransaction.createPolicy(newPolicy, "doPolicyAPIPut");
						}
						Map<String, String> successMap = newPolicy.savePolicies();							
						if (successMap.containsKey("success")) {
							
							EntityManager apiEm = null;
							apiEm = (EntityManager) emf.createEntityManager();
							//
							// Did it get created?
							//
							if (apiEm == null) {
								PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE +  " Error creating entity manager with persistence unit: " + PERSISTENCE_UNIT);	
								ServletException e = new ServletException("Unable to create Entity Manager Factory");
								e.printStackTrace();
								throw e;
							}
							
							String finalPath = policyAdapter.getFinalPolicyPath();
		    				//
		    				//Check the database entry if a scope is available in PolicyEditorScope table or not.
		    				//If not exists create a new entry.
		    				//
		    				String dirName = finalPath.toString().substring(finalPath.toString().indexOf("repository")+11, finalPath.toString().lastIndexOf(File.separator));
		    				apiEm.getTransaction().begin();
		    				Query query = apiEm.createQuery("Select p from PolicyEditorScopes p where p.scopeName=:sname");
		    				query.setParameter("sname", dirName);
		    				
		    				@SuppressWarnings("rawtypes")
		    				List result = query.getResultList();
		    				if(result.isEmpty()){
		    					PolicyEditorScopes scopeEntity = new PolicyEditorScopes();
		    					scopeEntity.setScopeName(dirName);
		    					UserInfo user = new UserInfo();
		    					user.setUserLoginId("API");
		    					user.setUserName("API");
		    					scopeEntity.setUserCreatedBy(user);
		    					scopeEntity.setUserModifiedBy(user);
		    					try{
		    						apiEm.persist(scopeEntity);
			    					apiEm.getTransaction().commit();
		    					}catch(Exception e){
		    						PolicyLogger.error("Exception Occured while inserting a new Entry to PolicyEditorScopes table"+e);
		    						apiEm.getTransaction().rollback();
		    					}finally{
		    						apiEm.close();
		    					}
		    				}else{
	    						PolicyLogger.info("Scope Already Exists in PolicyEditorScopes table, Hence Closing the Transaction");
	    						apiEm.close();
	    					}
		    				
							policyDBDaoTransaction.commitTransaction();
							response.setStatus(HttpServletResponse.SC_OK);								
							response.addHeader("successMapKey", "success");								
							response.addHeader("policyName", policyAdapter.getPolicyName());

							if (operation.equalsIgnoreCase("update")) {
								response.addHeader("operation",  "update");
							} else {
								response.addHeader("operation", "create");
							}
						} else if (successMap.containsKey("EXISTS")) {
							policyDBDaoTransaction.rollbackTransaction();
							response.setStatus(HttpServletResponse.SC_CONFLICT);
							response.addHeader("error", "policyExists");
							response.addHeader("policyName", policyAdapter.getPolicyName());
						} else if (successMap.containsKey("fwdberror")) {
							policyDBDaoTransaction.rollbackTransaction();
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							response.addHeader("error", "FWDBError");
							response.addHeader("policyName", policyAdapter.getPolicyName());
						}else {						
							policyDBDaoTransaction.rollbackTransaction();
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);								
							response.addHeader("error", "error");							
						}						
					} catch (Exception e) {							
						policyDBDaoTransaction.rollbackTransaction();
						String message = XACMLErrorConstants.ERROR_PROCESS_FLOW + 
								"Could not save policy " + e;
						PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Could not save policy");
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);	
						response.addHeader("error", "savePolicy");
						response.addHeader("message", message);
					}		        	
				}
			}
		}
	}

	private PolicyRestAdapter setDataToPolicyAdapter(StdPAPPolicy policy, String policyType, String apiflag) throws Exception {
		PolicyRestAdapter policyAdapter = new PolicyRestAdapter();
		int highestVersion = 0;

		if (policy.getHighestVersion()!=null) {	
			highestVersion = policy.getHighestVersion();
		}

		EntityManager apiEm = null;
		apiEm = (EntityManager) emf.createEntityManager();

		//
		// Did it get created?
		//
		if (apiEm == null) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + 
					" Error creating entity manager with persistence unit: "
					+ PERSISTENCE_UNIT);	
			throw new ServletException("Unable to create Entity Manager Factory");
		}

		Path workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE), "admin");
		Path repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY));
		Path gitPath = Paths.get(workspacePath.toString(), repositoryPath.getFileName().toString());

		/*
		 * Getting and Setting the parent path for Admin Console use when reading the policy files
		 */
		//domain chosen by the client to store the policy action files 
		String domain = policy.getDomainDir();

		//adding the domain to the gitPath
		Path path;
		String gitPathString = gitPath.toString();

		if (gitPathString.contains("\\")) {
			path = Paths.get(gitPath + "\\" + policy.getDomainDir());
		} else {
			path = Paths.get(gitPath + "/" + policy.getDomainDir());

		}
		logger.debug("path is: " + path.toString());

		//getting the fullpath of the gitPath and convert to string
		String policyDir = path.toAbsolutePath().toString();
		String parentPath = null;

		//creating the parentPath directory for the Admin Console use
		File file;
		if(policyDir.contains("\\"))
		{
			parentPath = policyDir.replace("ECOMP-PAP-REST", "ecomp-sdk-app");
			file = new File(parentPath);
		}
		else
		{
			parentPath = policyDir.replace("pap",  "console");
			file = new File(parentPath);

		}

		//Get the policy file from the git repository
		String filePrefix = null;
		if (policyType.equalsIgnoreCase("Config")) {
			if (policy.getConfigPolicyType().equalsIgnoreCase("Firewall Config")) {
				filePrefix = "Config_FW_";
			}else if (policy.getConfigPolicyType().equalsIgnoreCase("ClosedLoop_Fault")) {
				filePrefix = "Config_Fault_";
			}else if (policy.getConfigPolicyType().equalsIgnoreCase("ClosedLoop_PM")) {
				filePrefix = "Config_PM_";
			}else if (policy.getConfigPolicyType().equalsIgnoreCase("DCAE Micro Service")) {
				filePrefix = "Config_MS_";
			} else if (policy.getConfigPolicyType().equalsIgnoreCase("BRMS_Raw")) {
				filePrefix = "Config_BRMS_Raw_";
			} else if (policy.getConfigPolicyType().equalsIgnoreCase("BRMS_Param")) {
				filePrefix = "Config_BRMS_Param_";
			}
			else {
				filePrefix = "Config_";
			}
		} else if (policyType.equalsIgnoreCase("Action")) {
			filePrefix = "Action_";
		} else if (policyType.equalsIgnoreCase("Decision")) {
			filePrefix = "Decision_";
		}


		String pvName = domain + File.separator + filePrefix + policy.getPolicyName();

		//create the directory if it does not exist
		Boolean fileDir=true;
		if (!file.exists()){
			fileDir = new File(parentPath).mkdirs();
		}

		//set the parent path in the policy adapter
		if (!fileDir){
			logger.debug("Unable to create the policy directory");
		}

		logger.debug("ParentPath is: " + parentPath.toString());
		policyAdapter.setParentPath(parentPath.toString());
		policyAdapter.setApiflag(apiflag);

		if (policy.isEditPolicy()) {

			if(apiflag.equalsIgnoreCase("api")) {

				//Get the Highest Version to Update
				apiEm.getTransaction().begin();
				Query query = apiEm.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
				query.setParameter("pname", pvName);

				@SuppressWarnings("rawtypes")
				List result = query.getResultList();
				PolicyVersion versionEntity = null;
				if (!result.isEmpty()) {
					versionEntity = (PolicyVersion) result.get(0);
					apiEm.persist(versionEntity);
					highestVersion = versionEntity.getHigherVersion();
					int activeVersion = versionEntity.getActiveVersion();

					Calendar calendar = Calendar.getInstance();
					Timestamp modifyDate = new Timestamp(calendar.getTime().getTime());

					//update table with highestVersion
					try{
						versionEntity.setHigherVersion(highestVersion+1);
						versionEntity.setActiveVersion(activeVersion+1);
						versionEntity.setCreatedBy("API");
						versionEntity.setModifiedBy("API");
						versionEntity.setModifiedDate(modifyDate);

						apiEm.getTransaction().commit();

					}catch(Exception e){
						apiEm.getTransaction().rollback();
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
					} finally {
						apiEm.close();
					}
				} else {
					logger.debug("\nNo PolicyVersion using policyName found");
				}		

			}

			File policyFile = null;
			if(policy.getOldPolicyFileName() != null && policy.getOldPolicyFileName().endsWith("Draft.1")) {
				policyFile = new File(parentPath.toString() + File.separator + policy.getOldPolicyFileName() + ".xml");
			} else {
				policyFile = new File(parentPath.toString() + File.separator + filePrefix + policy.getPolicyName() +"."+(highestVersion)+ ".xml");
			}

			if (policyFile.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(policyFile);

				doc.getDocumentElement().normalize();

				String version = doc.getDocumentElement().getAttribute("Version");

				NodeList rList = doc.getElementsByTagName("Rule");
				Node rNode = rList.item(0);
				Element rElement = (Element) rNode;

				String ruleID = null;
				if (rNode!=null){
					ruleID = rElement.getAttribute("RuleId");
				} else {
					ruleID = newRuleID();
				}

				policyAdapter.setPolicyID(newPolicyID());
				policyAdapter.setRuleID(ruleID);
				policyAdapter.setVersion(version);

			} else {
				PolicyLogger.error(MessageCodes.ERROR_UNKNOWN + " The policy file at the path " + policyFile + " does not exist.");
			}

		} else {

			highestVersion = 1;
			if (apiflag.equalsIgnoreCase("api")) {
				Calendar calendar = Calendar.getInstance();
				Timestamp createdDate = new Timestamp(calendar.getTime().getTime());

				apiEm.getTransaction().begin();
				Query query = apiEm.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
				query.setParameter("pname", pvName);

				@SuppressWarnings("rawtypes")
				List result = query.getResultList();

				if (result.isEmpty()) {

					try{
						PolicyVersion versionEntity = new PolicyVersion();
						apiEm.persist(versionEntity);
						versionEntity.setPolicyName(pvName);
						versionEntity.setHigherVersion(highestVersion);
						versionEntity.setActiveVersion(highestVersion);
						versionEntity.setCreatedBy("API");
						versionEntity.setModifiedBy("API");
						versionEntity.setCreatedDate(createdDate);
						versionEntity.setModifiedDate(createdDate);

						apiEm.getTransaction().commit();

					}catch(Exception e){
						apiEm.getTransaction().rollback();
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
					} finally {
						apiEm.close();
					}		
				}
			}

			policyAdapter.setPolicyID(newPolicyID());
			policyAdapter.setRuleID(newRuleID());	

		}

		/*
		 * set policy adapter values for Building JSON object containing policy data
		 */
		//Common among policy types
		policyAdapter.setPolicyName(policy.getPolicyName());
		policyAdapter.setPolicyDescription(policy.getPolicyDescription());
		policyAdapter.setEcompName(policy.getEcompName()); //Config Base and Decision Policies
		policyAdapter.setHighestVersion(highestVersion);
		policyAdapter.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides");
		policyAdapter.setUserGitPath(gitPath.toString());
		policyAdapter.setPolicyType(policyType);
		policyAdapter.setDynamicFieldConfigAttributes(policy.getDynamicFieldConfigAttributes());
		policyAdapter.setEditPolicy(policy.isEditPolicy());
		policyAdapter.setEntityManagerFactory(getEmf());


		//Config Specific
		policyAdapter.setConfigName(policy.getConfigName());  //Base and Firewall
		policyAdapter.setConfigBodyData(policy.getConfigBodyData()); //Base
		policyAdapter.setConfigType(policy.getConfigType());  //Base
		policyAdapter.setJsonBody(policy.getJsonBody()); //Firewall, ClosedLoop, and GoC
		policyAdapter.setConfigPolicyType(policy.getConfigPolicyType());
		policyAdapter.setDraft(policy.isDraft()); //ClosedLoop_Fault
		policyAdapter.setServiceType(policy.getServiceType()); //ClosedLoop_PM
		policyAdapter.setUuid(policy.getUuid()); //Micro Service
		policyAdapter.setLocation(policy.getMsLocation()); //Micro Service
		policyAdapter.setPriority(policy.getPriority()); //Micro Service
		policyAdapter.setPolicyScope(policy.getDomainDir());
		policyAdapter.setRiskType(policy.getRiskType()); //Safe Policy Attributes
		policyAdapter.setRiskLevel(policy.getRiskLevel());//Safe Policy Attributes
		policyAdapter.setGuard(policy.getGuard());//Safe Policy Attributes
		policyAdapter.setTtlDate(policy.getTTLDate());//Safe Policy Attributes

		//Action Policy Specific
		policyAdapter.setActionAttribute(policy.getActionAttribute());  //comboDictValue
		policyAdapter.setActionPerformer(policy.getActionPerformer());
		policyAdapter.setDynamicRuleAlgorithmLabels(policy.getDynamicRuleAlgorithmLabels());
		policyAdapter.setDynamicRuleAlgorithmCombo(policy.getDynamicRuleAlgorithmCombo());
		policyAdapter.setDynamicRuleAlgorithmField1(policy.getDynamicRuleAlgorithmField1());
		policyAdapter.setDynamicRuleAlgorithmField2(policy.getDynamicRuleAlgorithmField2());

		//Decision Policy Specific
		policyAdapter.setDynamicSettingsMap(policy.getDynamicSettingsMap());
		policyAdapter.setProviderComboBox(policy.getProviderComboBox());

		return policyAdapter;
	}

	public String	newPolicyID() {
		return Joiner.on(':').skipNulls().join((XACMLPapServlet.getDomain().startsWith("urn") ? null : "urn"),
				XACMLPapServlet.getDomain().replaceAll("[/\\\\.]", ":"), 
				"xacml", "policy", "id", UUID.randomUUID());
	}

	public String	newRuleID() {
		return Joiner.on(':').skipNulls().join((XACMLPapServlet.getDomain().startsWith("urn") ? null : "urn"),
				XACMLPapServlet.getDomain().replaceAll("[/\\\\.]", ":"), 
				"xacml", "rule", "id", UUID.randomUUID());
	}

	public static String	getDomain() {
		return XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_DOMAIN, "urn");
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
	private void doACPost(HttpServletRequest request, HttpServletResponse response, String groupId, ECOMPLoggingContext loggingContext) throws ServletException, IOException {
		PolicyDBDaoTransaction doACPostTransaction = null;

		try {
			String groupName = request.getParameter("groupName");
			String groupDescription = request.getParameter("groupDescription");
			String apiflag = request.getParameter("apiflag");

			if (groupName != null && groupDescription != null) {
				// Args:	      group=<groupId> groupName=<name> groupDescription=<description>            <= create a new group
				loggingContext.setServiceName("AC:PAP.createGroup");

				String unescapedName = URLDecoder.decode(groupName, "UTF-8");
				String unescapedDescription = URLDecoder.decode(groupDescription, "UTF-8");
				PolicyDBDaoTransaction newGroupTransaction = policyDBDao.getNewTransaction();
				try {					
					newGroupTransaction.createGroup(PolicyDBDao.createNewPDPGroupId(unescapedName), unescapedName, unescapedDescription,"XACMLPapServlet.doACPost");
					papEngine.newGroup(unescapedName, unescapedDescription);
					newGroupTransaction.commitTransaction();
				} catch (Exception e) {
					newGroupTransaction.rollbackTransaction();
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Unable to create new group");
					loggingContext.transactionEnded();

					PolicyLogger.audit("Transaction Failed - See Error.log");
					response.sendError(500, "Unable to create new group '" + groupId + "'");
					return;
				}
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				if (logger.isDebugEnabled()) {
					logger.debug("New Group '" + groupId + "' created");
				}
				// tell the Admin Consoles there is a change
				notifyAC();
				// new group by definition has no PDPs, so no need to notify them of changes
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed - See Error.log");
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;
			}

			// for all remaining POST operations the group must exist before the operation can be done
			EcompPDPGroup group = papEngine.getGroup(groupId);
			if (group == null) {
				String message = "Unknown groupId '" + groupId + "'";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				if (apiflag!=null){
					response.addHeader("error", "unknownGroupId");
					response.addHeader("operation", "push");
					response.addHeader("message", message);
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				} else {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
				}
				return;
			}

			// determine the operation needed based on the parameters in the request
			if (request.getParameter("policyId") != null) {
				//	Args:        group=<groupId> policy=<policyId>		<= copy file
				// copy a policy from the request contents into a file in the group's directory on this machine
				if(apiflag!=null){
					loggingContext.setServiceName("PolicyEngineAPI:PAP.postPolicy");
				} else {
					loggingContext.setServiceName("AC:PAP.postPolicy");
				}

				String policyId = request.getParameter("policyId");
				PolicyDBDaoTransaction addPolicyToGroupTransaction = policyDBDao.getNewTransaction();
				try {
					InputStream is = null;
					if (apiflag != null){
						// get the request content into a String if the request is from API 
						String json = null;
						// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
						java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
						scanner.useDelimiter("\\A");
						json =  scanner.hasNext() ? scanner.next() : "";
						scanner.close();
						logger.info("JSON request from API: " + json);

						// convert Object sent as JSON into local object
						ObjectMapper mapper = new ObjectMapper();

						Object objectFromJSON = mapper.readValue(json, StdPAPPolicy.class);

						StdPAPPolicy policy = (StdPAPPolicy) objectFromJSON;

						is = new FileInputStream(new File(policy.getLocation()));
					} else {
						is = request.getInputStream();

					}

					addPolicyToGroupTransaction.addPolicyToGroup(group.getId(), policyId,"XACMLPapServlet.doACPost");
					((StdPDPGroup) group).copyPolicyToFile(policyId, is);
					addPolicyToGroupTransaction.commitTransaction();

				} catch (Exception e) {
					addPolicyToGroupTransaction.rollbackTransaction();
					String message = "Policy '" + policyId + "' not copied to group '" + groupId +"': " + e;
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " " + message);
					loggingContext.transactionEnded();

					PolicyLogger.audit("Transaction Failed - See Error.log");

					if (apiflag!=null){
						response.addHeader("error", "policyCopyError");
						response.addHeader("message", message);
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					} else {
						response.sendError(500, message);
					}
					return;
				}

				// policy file copied ok and the Group was updated on the PDP
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				response.addHeader("operation", "push");
				response.addHeader("policyId", policyId);
				response.addHeader("groupId", groupId);
				if (logger.isDebugEnabled()) {
					logger.debug("policy '" + policyId + "' copied to directory for group '" + groupId + "'");
				}

				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;

			} else if (request.getParameter("default") != null) {
				// Args:       group=<groupId> default=true               <= make default
				// change the current default group to be the one identified in the request.
				loggingContext.setServiceName("AC:PAP.setDefaultGroup");
				//
				// This is a POST operation rather than a PUT "update group" because of the side-effect that the current default group is also changed.
				// It should never be the case that multiple groups are currently marked as the default, but protect against that anyway.
				PolicyDBDaoTransaction setDefaultGroupTransaction = policyDBDao.getNewTransaction();
				try {
					setDefaultGroupTransaction.changeDefaultGroup(group, "XACMLPapServlet.doACPost");
					papEngine.SetDefaultGroup(group);
					setDefaultGroupTransaction.commitTransaction();
				} catch (Exception e) {
					setDefaultGroupTransaction.rollbackTransaction();
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Unable to set group");
					loggingContext.transactionEnded();

					PolicyLogger.audit("Transaction Failed - See Error.log");
					response.sendError(500, "Unable to set group '" + groupId + "' to default");
					return;
				}

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				if (logger.isDebugEnabled()) {
					logger.debug("Group '" + groupId + "' set to be default");
				}
				// Notify the Admin Consoles that something changed
				// For now the AC cannot handle anything more detailed than the whole set of PDPGroups, so just notify on that
				//TODO - Future: FIGURE OUT WHAT LEVEL TO NOTIFY: 2 groups or entire set - currently notify AC to update whole configuration of all groups
				notifyAC();
				// This does not affect any PDPs in the existing groups, so no need to notify them of this change
				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;

			} else if (request.getParameter("pdpId") != null) {
				doACPostTransaction = policyDBDao.getNewTransaction();
				// Args:       group=<groupId> pdpId=<pdpId>               <= move PDP to group
				loggingContext.setServiceName("AC:PAP.movePDP");

				String pdpId = request.getParameter("pdpId");
				EcompPDP pdp = papEngine.getPDP(pdpId);

				EcompPDPGroup originalGroup = papEngine.getPDPGroup((EcompPDP) pdp);
				try{
					doACPostTransaction.movePdp(pdp, group, "XACMLPapServlet.doACPost");
				}catch(Exception e){	
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", 
							" Error while moving pdp in the database: "
									+"pdp="+pdp.getId()+",to group="+group.getId());
					throw new PAPException(e.getMessage());
				}
				papEngine.movePDP((EcompPDP) pdp, group);

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				if (logger.isDebugEnabled()) {
					logger.debug("PDP '" + pdp.getId() +"' moved to group '" + group.getId() + "' set to be default");
				}

				// update the status of both the original group and the new one
				((StdPDPGroup)originalGroup).resetStatus();
				((StdPDPGroup)group).resetStatus();

				// Notify the Admin Consoles that something changed
				// For now the AC cannot handle anything more detailed than the whole set of PDPGroups, so just notify on that
				notifyAC();
				// Need to notify the PDP that it's config may have changed
				pdpChanged(pdp);
				doACPostTransaction.commitTransaction();
				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;


			}
		} catch (PAPException e) {
			if(doACPostTransaction != null){
				doACPostTransaction.rollbackTransaction();
			}
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC POST exception");
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(500, e.getMessage());
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
	private void doACPut(HttpServletRequest request, HttpServletResponse response, String groupId, ECOMPLoggingContext loggingContext) throws ServletException, IOException {
		PolicyDBDaoTransaction acPutTransaction = policyDBDao.getNewTransaction();
		try {


			// for PUT operations the group may or may not need to exist before the operation can be done
			EcompPDPGroup group = papEngine.getGroup(groupId);

			// determine the operation needed based on the parameters in the request

			// for remaining operations the group must exist before the operation can be done
			if (group == null) {
				String message = "Unknown groupId '" + groupId + "'";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
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
				java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
				scanner.useDelimiter("\\A");
				json =  scanner.hasNext() ? scanner.next() : "";
				scanner.close();
				logger.info("JSON request from AC: " + json);

				// convert Object sent as JSON into local object
				ObjectMapper mapper = new ObjectMapper();

				Object objectFromJSON = mapper.readValue(json, StdPDP.class);

				if (pdpId == null ||
						objectFromJSON == null ||
						! (objectFromJSON instanceof StdPDP) ||
						((StdPDP)objectFromJSON).getId() == null ||
						! ((StdPDP)objectFromJSON).getId().equals(pdpId)) {
					PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " PDP new/update had bad input. pdpId=" + pdpId + " objectFromJSON="+objectFromJSON);
					loggingContext.transactionEnded();

					PolicyLogger.audit("Transaction Failed - See Error.log");
					response.sendError(500, "Bad input, pdpid="+pdpId+" object="+objectFromJSON);
				}
				StdPDP pdp = (StdPDP) objectFromJSON;

				if (papEngine.getPDP(pdpId) == null) {
					// this is a request to create a new PDP object
					try{
						acPutTransaction.addPdpToGroup(pdp.getId(), group.getId(), pdp.getName(), pdp.getDescription(), pdp.getJmxPort(),"XACMLPapServlet.doACPut");
					} catch(Exception e){
						PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while adding pdp to group in the database: "
								+"pdp="+pdp.getId()+",to group="+group.getId());
						throw new PAPException(e.getMessage());
					}
					papEngine.newPDP(pdp.getId(), group, pdp.getName(), pdp.getDescription(), pdp.getJmxPort());
				} else {
					try{
						acPutTransaction.updatePdp(pdp, "XACMLPapServlet.doACPut");
					} catch(Exception e){
						PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while updating pdp in the database: "
								+"pdp="+pdp.getId());
						throw new PAPException(e.getMessage());
					}
					// this is a request to update the pdp
					papEngine.updatePDP(pdp);
				}

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				if (logger.isDebugEnabled()) {
					logger.debug("PDP '" + pdpId + "' created/updated");
				}

				// adjust the group's state including the new PDP
				((StdPDPGroup)group).resetStatus();

				// tell the Admin Consoles there is a change
				notifyAC();
				// this might affect the PDP, so notify it of the change
				pdpChanged(pdp);
				acPutTransaction.commitTransaction();
				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;
			} else if (request.getParameter("pipId") != null) {
				//                group=<groupId> pipId=<pipEngineId> contents=pip properties              <= add a PIP to pip config, or replace it if it already exists (lenient operation) 
				loggingContext.setServiceName("AC:PAP.putPIP");
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
				return;
			} else {
				// Assume that this is an update of an existing PDP Group
				// ARGS:        group=<groupId>         <= Update an Existing Group
				loggingContext.setServiceName("AC:PAP.updateGroup");

				// get the request content into a String
				String json = null;
				// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
				java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
				scanner.useDelimiter("\\A");
				json =  scanner.hasNext() ? scanner.next() : "";
				scanner.close();
				logger.info("JSON request from AC: " + json);

				// convert Object sent as JSON into local object
				ObjectMapper mapper = new ObjectMapper();

				Object objectFromJSON  = mapper.readValue(json, StdPDPGroup.class);

				if (objectFromJSON == null ||
						! (objectFromJSON instanceof StdPDPGroup) ||
						! ((StdPDPGroup)objectFromJSON).getId().equals(group.getId())) {
					PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id=" + group.getId() + " objectFromJSON="+objectFromJSON);
					loggingContext.transactionEnded();

					PolicyLogger.audit("Transaction Failed - See Error.log");
					response.sendError(500, "Bad input, id="+group.getId() +" object="+objectFromJSON);
				}

				// The Path on the PAP side is not carried on the RESTful interface with the AC
				// (because it is local to the PAP)
				// so we need to fill that in before submitting the group for update
				((StdPDPGroup)objectFromJSON).setDirectory(((StdPDPGroup)group).getDirectory());

				try{
					acPutTransaction.updateGroup((StdPDPGroup)objectFromJSON, "XACMLPapServlet.doACPut");
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database: "
							+"group="+group.getId());
					throw new PAPException(e.getMessage());
				}
				papEngine.updateGroup((StdPDPGroup)objectFromJSON);


				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				if (logger.isDebugEnabled()) {
					logger.debug("Group '" + group.getId() + "' updated");
				}
				acPutTransaction.commitTransaction();
				// tell the Admin Consoles there is a change
				notifyAC();
				// Group changed, which might include changing the policies
				groupChanged(group);

				loggingContext.transactionEnded();
				auditLogger.info("Success");
				PolicyLogger.audit("Transaction Ended Successfully");
				return;
			}
		} catch (PAPException e) {
			acPutTransaction.rollbackTransaction();
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " AC PUT exception");
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.sendError(500, e.getMessage());
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
	private void doACDelete(HttpServletRequest request, HttpServletResponse response, String groupId, ECOMPLoggingContext loggingContext) throws ServletException, IOException {

		//This is temporary code to allow deletes to propagate to the database since delete is not implemented
		String isDeleteNotify = request.getParameter("isDeleteNotify");
		if(isDeleteNotify != null){
			String policyToDelete = request.getParameter("policyToDelete");
			try{
				policyToDelete = URLDecoder.decode(policyToDelete,"UTF-8");
			} catch(UnsupportedEncodingException e){
				PolicyLogger.error("Unsupported URL encoding of policyToDelete (UTF-8");
				response.sendError(500,"policyToDelete encoding not supported");
				return;
			}
			PolicyDBDaoTransaction deleteTransaction = policyDBDao.getNewTransaction();
			try{
				deleteTransaction.deletePolicy(policyToDelete);
			} catch(Exception e){
				deleteTransaction.rollbackTransaction();
				response.sendError(500,"deleteTransaction.deleteTransaction(policyToDelete) "
						+ "\nfailure with the following exception: " + e);
				return;
			}
			deleteTransaction.commitTransaction();
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		PolicyDBDaoTransaction removePdpOrGroupTransaction = policyDBDao.getNewTransaction();
		try {
			// for all DELETE operations the group must exist before the operation can be done
			loggingContext.setServiceName("AC:PAP.delete");
			EcompPDPGroup group = papEngine.getGroup(groupId);
			if (group == null) {
				String message = "Unknown groupId '" + groupId + "'";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId +"'");
				return;
			}


			// determine the operation needed based on the parameters in the request
			if (request.getParameter("policy") != null) {
				//        group=<groupId> policy=<policyId>  [delete=<true|false>]       <= delete policy file from group
				loggingContext.setServiceName("AC:PAP.deletePolicy");
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " UNIMPLEMENTED");
				//DATABASE so can policies not be deleted? or doesn't matter maybe as long as this gets called
				loggingContext.transactionEnded();

				PolicyLogger.audit("Transaction Failed - See Error.log");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
				return;
			} else if (request.getParameter("pdpId") != null) {
				// ARGS:        group=<groupId> pdpId=<pdpId>                  <= delete PDP 
				String pdpId = request.getParameter("pdpId");
				EcompPDP pdp = papEngine.getPDP(pdpId);

				try{
					removePdpOrGroupTransaction.removePdpFromGroup(pdp.getId(),"XACMLPapServlet.doACDelete");
				} catch(Exception e){
					throw new PAPException();
				}
				papEngine.removePDP((EcompPDP) pdp);

				// adjust the status of the group, which may have changed when we removed this PDP
				((StdPDPGroup)group).resetStatus();

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				notifyAC();

				// update the PDP and tell it that it has NO Policies (which prevents it from serving PEP Requests)
				pdpChanged(pdp);
				removePdpOrGroupTransaction.commitTransaction();
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
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
				return;
			} else {
				// ARGS:      group=<groupId> movePDPsToGroupId=<movePDPsToGroupId>            <= delete a group and move all its PDPs to the given group
				String moveToGroupId = request.getParameter("movePDPsToGroupId");
				EcompPDPGroup moveToGroup = null;
				if (moveToGroupId != null) {
					moveToGroup = papEngine.getGroup(moveToGroupId);
				}

				// get list of PDPs in the group being deleted so we can notify them that they got changed
				Set<EcompPDP> movedPDPs = new HashSet<EcompPDP>();
				movedPDPs.addAll(group.getEcompPdps());

				// do the move/remove
				try{
					removePdpOrGroupTransaction.deleteGroup(group, moveToGroup,"XACMLPapServlet.doACDelete");
				} catch(Exception e){
					PolicyLogger.error(MessageCodes.ERROR_UNKNOWN, e, "XACMLPapServlet", " Failed to delete PDP Group. Exception");
					e.printStackTrace();
					throw new PAPException(e.getMessage());
				}
				papEngine.removeGroup(group, moveToGroup);

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				notifyAC();
				// notify any PDPs in the removed set that their config may have changed
				for (EcompPDP pdp : movedPDPs) {
					pdpChanged(pdp);
				}
				removePdpOrGroupTransaction.commitTransaction();
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
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Exception in request processing");
			response.sendError(500, e.getMessage());
			return;
		}
	}


	/**
	 * Requests from the API to delete/remove items
	 * 
	 * @param request
	 * @param response
	 * @param groupId
	 * @param loggingContext 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doAPIDeleteFromPAP(HttpServletRequest request, HttpServletResponse response, ECOMPLoggingContext loggingContext) throws ServletException, IOException {

		// get the request content into a String
		String json = null;

		// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
		java.util.Scanner scanner = new java.util.Scanner(request.getInputStream());
		scanner.useDelimiter("\\A");
		json =  scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		logger.info("JSON request from API: " + json);

		// convert Object sent as JSON into local object
		ObjectMapper mapper = new ObjectMapper();

		Object objectFromJSON = mapper.readValue(json, StdPAPPolicy.class);

		StdPAPPolicy policy = (StdPAPPolicy) objectFromJSON;

		String policyName = policy.getPolicyName();
		String fileSeparator = File.separator;
		policyName = policyName.replaceFirst("\\.", "\\"+fileSeparator);

		File file = getPolicyFile(policyName);
		String domain = getParentPathSubScopeDir(file);
		Boolean policyFileDeleted = false;
		Boolean configFileDeleted = false;
		Boolean policyVersionScoreDeleted = false;

		if (policy.getDeleteCondition().equalsIgnoreCase("All Versions")){

			//check for extension in policyName
			String removexmlExtension = null;
			String removeVersion = null;
			if (policyName.contains("xml")) {
				removexmlExtension = file.toString().substring(0, file.toString().lastIndexOf("."));
				removeVersion = removexmlExtension.substring(0, removexmlExtension.lastIndexOf("."));
			} else {
				removeVersion = file.toString();
			}

			File dirXML = new File(file.getParent());
			File[] listofXMLFiles = dirXML.listFiles();

			for (File files : listofXMLFiles) {
				//delete the xml files from the Repository
				if (files.isFile() && files.toString().contains(removeVersion)) {
					JPAUtils jpaUtils = null;
					try {
						jpaUtils = JPAUtils.getJPAUtilsInstance(emf);
					} catch (Exception e) {
						PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " Could not create JPAUtils instance on the PAP");
						e.printStackTrace();
						response.addHeader("error", "jpautils");
						response.addHeader("operation", "delete");
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						return;
					}

					if (jpaUtils.dbLockdownIgnoreErrors()) {
						logger.warn("Policies are locked down");
						response.addHeader("operation", "delete");
						response.addHeader("lockdown", "true");
						response.setStatus(HttpServletResponse.SC_ACCEPTED);
						return;
					}

					//Propagates delete to the database 
					Boolean deletedFromDB = notifyDBofDelete(files.toString());

					if (deletedFromDB) {
						logger.info("Policy deleted from the database.  Continuing with file delete");
					} else {
						PolicyLogger.error("Failed to delete Policy from database. Aborting file delete");
						response.addHeader("error", "deleteDB");
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						return;
					}

					if (files.delete()) {
						if (logger.isDebugEnabled()) {
							logger.debug("Deleted file: " + files.toString());
						}
						policyFileDeleted = true;
					} else {
						logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + 
								"Cannot delete the policy file in specified location: " + files.getAbsolutePath());	
						response.addHeader("error", "deleteFile");
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
						return;
					}

					// Get tomcat home directory for deleting config data
					logger.info("print the path:" +domain);
					String path = domain.replace('\\', '.');
					if(path.contains("/")){
						path = path.replace('/', '.');
						logger.info("print the path:" +path);
					}
					String fileName = FilenameUtils.removeExtension(file.getName());
					String removeVersionInFileName = fileName.substring(0, fileName.lastIndexOf("."));
					String fileLocation = null;

					if(CONFIG_HOME == null){
						CONFIG_HOME = getConfigHome();
					}
					if(ACTION_HOME == null){
						ACTION_HOME = getActionHome();
					}


					if (fileName != null && fileName.contains("Config_")) {
						fileLocation = CONFIG_HOME;
					} else if (fileName != null && fileName.contains("Action_")) {
						fileLocation = ACTION_HOME;
					}

					if (logger.isDebugEnabled()) {
						logger.debug("Attempting to rename file from the location: "+ fileLocation);
					}

					if(!files.toString().contains("Decision_")){
						// Get the file from the saved location
						File dir = new File(fileLocation);
						File[] listOfFiles = dir.listFiles();

						for (File file1 : listOfFiles) {
							if (file1.isFile() && file1.getName().contains( path + removeVersionInFileName)) {
								try {
									if (file1.delete()) {
										if (logger.isDebugEnabled()) {
											logger.debug("Deleted file: " + file1.toString());
										}
										configFileDeleted = true;
									} else {
										logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + 
												"Cannot delete the configuration or action body file in specified location: " + file1.getAbsolutePath());	
										response.addHeader("error", "deleteConfig");
										response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
										return;
									}
								} catch (Exception e) {
									PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " Failed to Delete file");	
								}
							}
							configFileDeleted = true;
						}
					} else {
						configFileDeleted = true;
					}

					//Delete the Policy from Database Policy Version table
					if (policyFileDeleted && configFileDeleted) {
						String removeExtension = domain + removeVersionInFileName;
						EntityManager em = (EntityManager) emf.createEntityManager();

						Query getPolicyVersion = em.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
						Query getPolicyScore = em.createQuery("Select p from PolicyScore p where p.PolicyName=:pname");
						getPolicyVersion.setParameter("pname", removeExtension);
						getPolicyScore.setParameter("pname", removeExtension);

						@SuppressWarnings("rawtypes")
						List pvResult = getPolicyVersion.getResultList();
						@SuppressWarnings("rawtypes")
						List psResult = getPolicyScore.getResultList();


						try{
							em.getTransaction().begin();
							if (!pvResult.isEmpty()) {
								for (Object id : pvResult) {
									PolicyVersion versionEntity = (PolicyVersion)id;	
									em.remove(versionEntity);
								}
							} else {
								logger.debug("No PolicyVersion record found in database.");
							}

							if (!psResult.isEmpty()) {				
								for (Object id : psResult) {
									PolicyScore scoreEntity = (PolicyScore)id;	
									em.remove(scoreEntity);
								}
							} else {
								PolicyLogger.error("No PolicyScore record found in database.");
							}
							em.getTransaction().commit();
							policyVersionScoreDeleted = true;
						}catch(Exception e){
							em.getTransaction().rollback();
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
							response.addHeader("error", "deleteDB");
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							return;
						} finally {
							em.close();
						}
					}
				}
			}
			//If Specific version is requested for delete
		} else if (policy.getDeleteCondition().equalsIgnoreCase("Current Version")) {
			String policyScoreName = domain + file.getName().toString();
			String policyVersionName = policyScoreName.substring(0, policyScoreName.indexOf("."));
			String versionExtension = policyScoreName.substring(policyScoreName.indexOf(".")+1);
			String removexmlExtension = file.toString().substring(0, file.toString().lastIndexOf("."));
			String getVersion = removexmlExtension.substring(removexmlExtension.indexOf(".")+1);
			String removeVersion = removexmlExtension.substring(0, removexmlExtension.lastIndexOf("."));


			JPAUtils jpaUtils = null;
			try {
				jpaUtils = JPAUtils.getJPAUtilsInstance(emf);
			} catch (Exception e) {
				PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " Could not create JPAUtils instance on the PAP");
				e.printStackTrace();
				response.addHeader("error", "jpautils");
				response.addHeader("operation", "delete");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			if (jpaUtils.dbLockdownIgnoreErrors()) {
				logger.warn("Policies are locked down");
				response.addHeader("lockdown", "true");
				response.addHeader("operation", "delete");
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				return;
			}

			//Propagates delete to the database 
			Boolean deletedFromDB = notifyDBofDelete(file.toString());

			if (deletedFromDB) {
				logger.info("Policy deleted from the database.  Continuing with file delete");
			} else {
				PolicyLogger.error("Failed to delete Policy from database. Aborting file delete");
				response.addHeader("error", "deleteDB");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}

			if (file.delete()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Deleted file: " + file.toString());
				}
				policyFileDeleted = true;
			} else {
				logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + 
						"Cannot delete the policy file in specified location: " + file.getAbsolutePath());	
				response.addHeader("error", "deleteFile");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
				return;
			}

			// Get tomcat home directory for deleting config data
			logger.info("print the path:" +domain);
			String path = domain.replace('\\', '.');
			if(path.contains("/")){
				path = path.replace('/', '.');
				logger.info("print the path:" +path);
			}
			String fileName = FilenameUtils.removeExtension(file.getName());
			String removeVersionInFileName = fileName.substring(0, fileName.lastIndexOf("."));
			String fileLocation = null;

			if(CONFIG_HOME == null){
				CONFIG_HOME = getConfigHome();
			}
			if(ACTION_HOME == null){
				ACTION_HOME = getActionHome();
			}


			if (fileName != null && fileName.contains("Config_")) {
				fileLocation = CONFIG_HOME;
			} else if (fileName != null && fileName.contains("Action_")) {
				fileLocation = ACTION_HOME;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Attempting to rename file from the location: "+ fileLocation);
			}

			if(!file.toString().contains("Decision_")){
				// Get the file from the saved location
				File dir = new File(fileLocation);
				File[] listOfFiles = dir.listFiles();

				for (File file1 : listOfFiles) {
					if (file1.isFile() && file1.getName().contains( path + fileName)) {
						try {
							if (file1.delete()) {
								if (logger.isDebugEnabled()) {
									logger.debug("Deleted file: " + file1.toString());
								}
								configFileDeleted = true;
							} else {
								logger.warn(XACMLErrorConstants.ERROR_DATA_ISSUE + 
										"Cannot delete the configuration or action body file in specified location: " + file1.getAbsolutePath());	
								response.addHeader("error", "deleteConfig");
								response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
								return;
							}
						} catch (Exception e) {
							PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " Failed to Delete file");		
						}
					}
					configFileDeleted = true;
				}
			} else {
				configFileDeleted = true;
			}

			//Delete the Policy from Database and set Active Version based on the deleted file.
			int highestVersion = 0;
			if (policyFileDeleted && configFileDeleted) {
				String removeExtension = domain + removeVersionInFileName;
				EntityManager em = (EntityManager) emf.createEntityManager();

				Query getPolicyVersion = em.createQuery("Select p from PolicyVersion p where p.policyName=:pname");
				Query getPolicyScore = em.createQuery("Select p from PolicyScore p where p.PolicyName=:pname");
				getPolicyVersion.setParameter("pname", removeExtension);
				getPolicyScore.setParameter("pname", removeExtension);

				@SuppressWarnings("rawtypes")
				List pvResult = getPolicyVersion.getResultList();
				@SuppressWarnings("rawtypes")
				List psResult = getPolicyScore.getResultList();


				try{
					em.getTransaction().begin();
					if (!pvResult.isEmpty()) {
						PolicyVersion versionEntity = null;
						for (Object id : pvResult) {
							versionEntity = (PolicyVersion)id;
							if(versionEntity.getPolicyName().equals(removeExtension)){
								highestVersion = versionEntity.getHigherVersion();
								em.remove(versionEntity);
							}
						}

						int i = 0;
						int version = Integer.parseInt(getVersion);

						if(version == highestVersion) {
							for(i = highestVersion; i>=1; i--){
								highestVersion = highestVersion - 1;
								String dirXML = removeVersion + "." + highestVersion + ".xml";
								File filenew = new File(dirXML);

								if(filenew.exists()){
									break;
								}

							}
						}

						versionEntity.setPolicyName(removeExtension);
						versionEntity.setHigherVersion(highestVersion);
						versionEntity.setActiveVersion(highestVersion);
						versionEntity.setModifiedBy("API");	

						em.persist(versionEntity);

					} else {
						logger.debug("No PolicyVersion record found in database.");
					}

					if (!psResult.isEmpty()) {				
						for (Object id : psResult) {
							PolicyScore scoreEntity = (PolicyScore)id;	
							if(scoreEntity.getPolicyName().equals(policyVersionName) && scoreEntity.getVersionExtension().equals(versionExtension)){
								em.remove(scoreEntity);
							}
						}
					} else {
						PolicyLogger.error("No PolicyScore record found in database.");
					}
					em.getTransaction().commit();
					policyVersionScoreDeleted = true;
				}catch(Exception e){
					em.getTransaction().rollback();
					PolicyLogger.error(MessageCodes.EXCEPTION_ERROR, e, "XACMLPapServlet", " ERROR");
					response.addHeader("error", "deleteDB");
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				} finally {
					em.close();
				}
			}
		}

		if (policyFileDeleted && configFileDeleted && policyVersionScoreDeleted) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("successMapKey", "success");
			response.addHeader("operation", "delete");
			return;				
		} else {
			PolicyLogger.error(MessageCodes.ERROR_UNKNOWN + "Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.");

			response.addHeader("error", "unknown");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			return;
		}

	}

	private void doImportMicroServicePut(HttpServletRequest request, HttpServletResponse response) {
		String importServiceCreation = request.getParameter("importService");;
		String fileName = request.getParameter("fileName");
		String version = request.getParameter("version");
		String serviceName = request.getParameter("serviceName");
		CreateNewMicroSerivceModel newMS = null;

		String randomID = UUID.randomUUID().toString();

		if ( importServiceCreation != null  || fileName != null) {
			File extracDir = new File("ExtractDir");
			if (!extracDir.exists()){
				extracDir.mkdirs();
			}
			if (fileName.contains(".xmi")){
				// get the request content into a String
				String xmi = null;

				// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
				java.util.Scanner scanner;
				try {
					scanner = new java.util.Scanner(request.getInputStream());
					scanner.useDelimiter("\\A");
					xmi =  scanner.hasNext() ? scanner.next() : "";
					scanner.close();
				} catch (IOException e1) {
					logger.error("Error in reading in file from API call");
					return;
				}

				logger.info("XML request from API for import new Service");

				//Might need to seperate by , for more than one file. 

				try (Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("ExtractDir" + File.separator + randomID+".xmi"), "utf-8"))) {
					writer.write(xmi);
				} catch (IOException e) {
					logger.error("Error in reading in file from API call");
					return;
				}
			}else{ 
				try {	
					InputStream inputStream = request.getInputStream() ; 

					FileOutputStream outputStream = new FileOutputStream("ExtractDir" + File.separator + randomID+".zip"); 
					byte[] buffer = new byte[4096];
					int bytesRead = -1 ; 
					while ((bytesRead = inputStream.read(buffer)) != -1) { 
						outputStream.write(buffer, 0, bytesRead) ; 
					} 

					outputStream.close() ; 
					inputStream.close() ;

				} catch (IOException e) {
					logger.error("Error in reading in Zip File from API call");
					return;
				}
			}

			newMS =  new CreateNewMicroSerivceModel(fileName, serviceName, "API IMPORT", version, randomID);
			Map<String, String> successMap = newMS.addValuesToNewModel();
			if (successMap.containsKey("success")) {
				successMap.clear();
				successMap = newMS.saveImportService();
			}


			// create the policy and return a response to the PAP-ADMIN		    
			if (successMap.containsKey("success")) {							
				response.setStatus(HttpServletResponse.SC_OK);								
				response.addHeader("successMapKey", "success");								
				response.addHeader("operation", "import");
				response.addHeader("service", serviceName);
			} else if (successMap.containsKey("DBError")) {
				if (successMap.get("DBError").contains("EXISTS")){
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.addHeader("service", serviceName);
					response.addHeader("error", "modelExistsDB");
				}else{
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.addHeader("error", "importDB");
				}
				response.addHeader("operation", "import");
				response.addHeader("service", serviceName);
			}else if (successMap.get("error").contains("MISSING")){
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.addHeader("error", "missing");	
				response.addHeader("operation", "import");
				response.addHeader("service", serviceName);
			}
		}
	}

	private void doAPIDeleteFromPDP(HttpServletRequest request, HttpServletResponse response, ECOMPLoggingContext loggingContext) throws ServletException, IOException {

		String policyName = request.getParameter("policyName");
		String groupId = request.getParameter("groupId");
		String responseString = null;

		// for PUT operations the group may or may not need to exist before the operation can be done
		EcompPDPGroup group = null;
		try {
			group = papEngine.getGroup(groupId);
		} catch (PAPException e) {
			logger.error("Exception occured While PUT operation is performing for PDP Group"+e);
		}

		if (group == null) {
			String message = "Unknown groupId '" + groupId + "'";
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " " + message);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");
			response.addHeader("error", "UnknownGroup");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
			return;
		} else {

			loggingContext.setServiceName("API:PAP.deletPolicyFromPDPGroup");

			if (policyName.contains("xml")) {
				logger.debug("The full file name including the extension was provided for policyName.. continue.");
			} else {
				String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Invalid policyName... "
						+ "policyName must be the full name of the file to be deleted including version and extension";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Invalid policyName... "
						+ "policyName must be the full name of the file to be deleted including version and extension");
				response.addHeader("error", "invalidPolicyName");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
				return;
			}
			RemoveGroupPolicy removePolicy = new RemoveGroupPolicy((StdPDPGroup) group);

			PDPPolicy policy =  group.getPolicy(policyName);

			if (policy != null) {
				removePolicy.prepareToRemove(policy);
				EcompPDPGroup updatedGroup = removePolicy.getUpdatedObject();
				responseString = deletePolicyFromPDPGroup(updatedGroup, loggingContext);
			} else {
				String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Policy does not exist on the PDP.";
				PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Policy does not exist on the PDP.");
				response.addHeader("error", "noPolicyExist");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
				return;
			}			
		}

		if (responseString.equals("success")) {
			logger.info("Policy successfully deleted!");
			PolicyLogger.audit("Policy successfully deleted!");
			response.setStatus(HttpServletResponse.SC_OK);
			response.addHeader("successMapKey", "success");
			response.addHeader("operation", "delete");
			return;		
		} else if (responseString.equals("No Group")) {
			String message = XACMLErrorConstants.ERROR_DATA_ISSUE + "Group update had bad input.";
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error", "groupUpdate");
			response.addHeader("message", message);
			return;	
		} else if (responseString.equals("DB Error")) {
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + " Error while updating group in the database");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.addHeader("error", "deleteDB");
			return;
		} else {
			PolicyLogger.error(MessageCodes.ERROR_UNKNOWN + " Failed to delete the policy for an unknown reason.  Check the file system and other logs for further information.");
			response.addHeader("error", "unknown");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			return;
		}

	}

	protected String getParentPathSubScopeDir(File file) {
		String domain1 = null;

		Path workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WORKSPACE), "admin");
		Path repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_REPOSITORY));
		Path gitPath = Paths.get(workspacePath.toString(), repositoryPath.getFileName().toString());

		String policyDir = file.getAbsolutePath();
		int startIndex = policyDir.indexOf(gitPath.toString()) + gitPath.toString().length() + 1;
		policyDir = policyDir.substring(startIndex, policyDir.length());
		if(policyDir.contains("Config_")){
			domain1 = policyDir.substring(0,policyDir.indexOf("Config_"));
		}else if(policyDir.contains("Action_")){
			domain1 = policyDir.substring(0,policyDir.indexOf("Action_"));	
		}else{
			domain1 = policyDir.substring(0,policyDir.indexOf("Decision_"));	
		}
		logger.info("print the main domain value"+policyDir);

		return domain1;
	}

	/*
	 * method to delete the policy from the database and return notification when using API
	 */
	private Boolean notifyDBofDelete (String policyToDelete) {
		//String policyToDelete = request.getParameter("policyToDelete");
		try{
			policyToDelete = URLDecoder.decode(policyToDelete,"UTF-8");
		} catch(UnsupportedEncodingException e){
			PolicyLogger.error("Unsupported URL encoding of policyToDelete (UTF-8)");
			return false;
		}
		PolicyDBDaoTransaction deleteTransaction = policyDBDao.getNewTransaction();
		try{
			deleteTransaction.deletePolicy(policyToDelete);
		} catch(Exception e){
			deleteTransaction.rollbackTransaction();
			return false;
		}
		deleteTransaction.commitTransaction();
		return true;
	}

	private String deletePolicyFromPDPGroup (EcompPDPGroup group, ECOMPLoggingContext loggingContext){
		PolicyDBDaoTransaction acPutTransaction = policyDBDao.getNewTransaction();

		String response = null;
		loggingContext.setServiceName("API:PAP.updateGroup");

		EcompPDPGroup existingGroup = null;
		try {
			existingGroup = papEngine.getGroup(group.getId());
		} catch (PAPException e1) {
			logger.error("Exception occured While Deleting Policy From PDP Group"+e1);
		}

		if (group == null ||
				! (group instanceof StdPDPGroup) ||
				! (group.getId().equals(existingGroup.getId()))) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + " Group update had bad input. id=" + existingGroup.getId() + " objectFromJSON="+group);
			loggingContext.transactionEnded();

			PolicyLogger.audit("Transaction Failed - See Error.log");

			response = "No Group";
			return response;
		}

		// The Path on the PAP side is not carried on the RESTful interface with the AC
		// (because it is local to the PAP)
		// so we need to fill that in before submitting the group for update
		((StdPDPGroup)group).setDirectory(((StdPDPGroup)existingGroup).getDirectory());

		try{
			acPutTransaction.updateGroup(group, "XACMLPapServlet.doAPIDelete");
		} catch(Exception e){
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Error while updating group in the database: "
					+"group="+existingGroup.getId());
			response = "DB Error";
			return response;
		}

		try {
			papEngine.updateGroup(group);
		} catch (PAPException e) {
			logger.error("Exception occured While Updating PDP Groups"+e);
			response = "error in updateGroup method";
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Group '" + group.getId() + "' updated");
		}

		acPutTransaction.commitTransaction();

		// Group changed, which might include changing the policies
		try {
			groupChanged(existingGroup);
		}  catch (Exception e) {
			logger.error("Exception occured in Group Change Method"+e);
			response = "error in groupChanged method";
		}

		if (response==null){
			response = "success";
			PolicyLogger.audit("Policy successfully deleted!");
			PolicyLogger.audit("Transaction Ended Successfully");
		}

		loggingContext.transactionEnded();
		PolicyLogger.audit("Transaction Ended");
		return response;
	}


	//
	// Heartbeat thread - periodically check on PDPs' status
	//

	/**
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
	 * 
	 *
	 */
	private class Heartbeat implements Runnable {
		private PAPPolicyEngine papEngine;
		private Set<EcompPDP> pdps = new HashSet<EcompPDP>();
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
			this.papEngine = papEngine2;
			this.heartbeatInterval = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_INTERVAL, "10000"));
			this.heartbeatTimeout = Integer.parseInt(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_TIMEOUT, "10000"));
		}

		@Override
		public void run() {
			//
			// Set ourselves as running
			//
			synchronized(this) {
				this.isRunning = true;
			}
			HashMap<String, URL> idToURLMap = new HashMap<String, URL>();
			try {
				while (this.isRunning()) {
					// Wait the given time
					Thread.sleep(heartbeatInterval);

					// get the list of PDPs (may have changed since last time)
					pdps.clear();
					synchronized(papEngine) {
						try {
							for (EcompPDPGroup g : papEngine.getEcompPDPGroups()) {
								for (EcompPDP p : g.getEcompPdps()) {
									pdps.add(p);
								}
							}
						} catch (PAPException e) {
							PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", "Heartbeat unable to read PDPs from PAPEngine");
						}
					}
					//
					// Check for shutdown
					//
					if (this.isRunning() == false) {
						logger.info("isRunning is false, getting out of loop.");
						break;
					}

					// try to get the summary status from each PDP
					boolean changeSeen = false;
					for (EcompPDP pdp : pdps) {
						//
						// Check for shutdown
						//
						if (this.isRunning() == false) {
							logger.info("isRunning is false, getting out of loop.");
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

							//
							// Open up the connection
							//
							connection = (HttpURLConnection)pdpURL.openConnection();
							//
							// Setup our method and headers
							//
							connection.setRequestMethod("GET");
							connection.setConnectTimeout(heartbeatTimeout);
							// Added for Authentication
							String encoding = CheckPDP.getEncoding(pdp.getId());
							if(encoding !=null){
								connection.setRequestProperty("Authorization", "Basic " + encoding);
							}
							//
							// Do the connect
							//
							connection.connect();
							if (connection.getResponseCode() == 204) {
								newStatus = connection.getHeaderField(XACMLRestProperties.PROP_PDP_HTTP_HEADER_HB);
								if (logger.isDebugEnabled()) {
									logger.debug("Heartbeat '" + pdp.getId() + "' status='" + newStatus + "'");
								}
							} else {
								// anything else is an unexpected result
								newStatus = PDPStatus.Status.UNKNOWN.toString();
								PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR + " Heartbeat connect response code " + connection.getResponseCode() + ": " + pdp.getId());
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
							connection.disconnect();
						}

						if ( ! pdp.getStatus().getStatus().toString().equals(newStatus)) {
							if (logger.isDebugEnabled()) {
								logger.debug("previous status='" + pdp.getStatus().getStatus()+"'  new Status='" + newStatus + "'");
							}
							try {
								setPDPSummaryStatus(pdp, newStatus);
							} catch (PAPException e) {
								PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", "Unable to set state for PDP '" + pdp.getId());
							}
							changeSeen = true;
						}

					}
					//
					// Check for shutdown
					//
					if (this.isRunning() == false) {
						logger.info("isRunning is false, getting out of loop.");
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
			}
		}
	}


	//
	// HELPER to change Group status when PDP status is changed
	//
	// (Must NOT be called from a method that is synchronized on the papEngine or it may deadlock)
	//

	private void setPDPSummaryStatus(EcompPDP pdp, PDPStatus.Status newStatus) throws PAPException {
		setPDPSummaryStatus(pdp, newStatus.toString());
	}

	private void setPDPSummaryStatus(EcompPDP pdp, String newStatus) throws PAPException {
		synchronized(papEngine) {
			StdPDPStatus status = new StdPDPStatus();
			status.setStatus(PDPStatus.Status.valueOf(newStatus));
			((StdPDP)pdp).setStatus(status);

			// now adjust the group
			StdPDPGroup group = (StdPDPGroup)papEngine.getPDPGroup((EcompPDP) pdp);
			// if the PDP was just deleted it may transiently exist but not be in a group
			if (group != null) {
				group.resetStatus();
			}
		}
	}


	//
	// Callback methods telling this servlet to notify PDPs of changes made by the PAP StdEngine
	//	in the PDP group directories
	//

	@Override
	public void changed() {
		// all PDPs in all groups need to be updated/sync'd
		Set<EcompPDPGroup> groups;
		try {
			groups = papEngine.getEcompPDPGroups();
		} catch (PAPException e) {
			PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " getPDPGroups failed");
			throw new RuntimeException(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to get Groups: " + e);
		}
		for (EcompPDPGroup group : groups) {
			groupChanged(group);
		}
	}

	@Override
	public void groupChanged(EcompPDPGroup group) {
		// all PDPs within one group need to be updated/sync'd
		for (EcompPDP pdp : group.getEcompPdps()) {
			pdpChanged(pdp);
		}
	}

	@Override
	public void pdpChanged(EcompPDP pdp) {
		// kick off a thread to do an event notification for each PDP.
		// This needs to be on a separate thread so that PDPs that do not respond (down, non-existent, etc)
		// do not block the PSP response to the AC, which would freeze the GUI until all PDPs sequentially respond or time-out.
		// begin - Fix to maintain requestId - including storedRequestId in UpdatePDPThread to be used later when calling PDP
		// Thread t = new Thread(new UpdatePDPThread(pdp));
		Thread t = new Thread(new UpdatePDPThread(pdp, storedRequestId));
		// end   - Fix to maintain requestId
		if(CheckPDP.validateID(pdp.getId())){
			t.start();
		}
	}

	private class UpdatePDPThread implements Runnable {
		private EcompPDP pdp;
		// begin - Fix to maintain requestId - define requestId under class to be used later when calling PDP
		private String requestId;
		// end   - Fix to maintain requestId

		// remember which PDP to notify
		public UpdatePDPThread(EcompPDP pdp) {
			this.pdp = pdp;
		}

		// begin - Fix to maintain requestId - clone UpdatePDPThread method with different method signature so to include requestId to be used later when calling PDP
		public UpdatePDPThread(EcompPDP pdp, String storedRequestId) {
			this.pdp = pdp;
			requestId = storedRequestId;
		}
		// end   - Fix to maintain requestId

		public void run() {
			// send the current configuration to one PDP
			HttpURLConnection connection = null;
			// get a new logging context for the thread
			ECOMPLoggingContext loggingContext = new ECOMPLoggingContext(baseLoggingContext);
			try {
				loggingContext.setServiceName("PAP:PDP.putConfig");
				// get a new transaction (request) ID and update the logging context.
				// begin - Fix to maintain requestId - replace unconditioned generation of new requestID so it won't be used later when calling PDP
				// If a requestId was provided, use it, otherwise generate one; post to loggingContext to be used later when calling PDP
				// UUID requestID = UUID.randomUUID();
				// loggingContext.setRequestID(requestID.toString());
				if ((requestId == null) || (requestId == "")) {
					UUID requestID = UUID.randomUUID();
					loggingContext.setRequestID(requestID.toString());
					PolicyLogger.info("requestID not provided in call to XACMLPapSrvlet (UpdatePDPThread) so we generated one:  " + loggingContext.getRequestID());
				} else {
					loggingContext.setRequestID(requestId);
					PolicyLogger.info("requestID was provided in call to XACMLPapSrvlet (UpdatePDPThread):  " + loggingContext.getRequestID());
				}
				// end   - Fix to maintain requestId
				loggingContext.transactionStarted();
				// dummy metric.log example posted below as proof of concept
				loggingContext.metricStarted();
				loggingContext.metricEnded();
				PolicyLogger.metrics("Metric example posted here - 1 of 2");
				loggingContext.metricStarted();
				loggingContext.metricEnded();
				PolicyLogger.metrics("Metric example posted here - 2 of 2");
				// dummy metric.log example posted above as proof of concept

				//
				// the Id of the PDP is its URL
				//
				if (logger.isDebugEnabled()) {
					logger.debug("creating url for id '" + pdp.getId() + "'");
				}
				//TODO - currently always send both policies and pips.  Do we care enough to add code to allow sending just one or the other?
				//TODO		(need to change "cache=", implying getting some input saying which to change)
				URL url = new URL(pdp.getId() + "?cache=all");

				//
				// Open up the connection
				//
				connection = (HttpURLConnection)url.openConnection();
				//
				// Setup our method and headers
				//
				connection.setRequestMethod("PUT");
				// Added for Authentication
				String encoding = CheckPDP.getEncoding(pdp.getId());
				if(encoding !=null){
					connection.setRequestProperty("Authorization", "Basic " + encoding);
				}
				connection.setRequestProperty("Content-Type", "text/x-java-properties");
				// begin - Fix to maintain requestId - post requestID from loggingContext in PDP request header for call to PDP, then reinit storedRequestId to null
				// connection.setRequestProperty("X-ECOMP-RequestID", requestID.toString());
				connection.setRequestProperty("X-ECOMP-RequestID", loggingContext.getRequestID());
				storedRequestId = null;
				// end   - Fix to maintain requestId 
				//
				// Adding this in. It seems the HttpUrlConnection class does NOT
				// properly forward our headers for POST re-direction. It does so
				// for a GET re-direction.
				//
				// So we need to handle this ourselves.
				//
				//TODO - is this needed for a PUT?  seems better to leave in for now?
				//	            connection.setInstanceFollowRedirects(false);
				//
				// PLD - MUST be able to handle re-directs.
				//
				connection.setInstanceFollowRedirects(true);
				connection.setDoOutput(true);
				try (OutputStream os = connection.getOutputStream()) {

					EcompPDPGroup group = papEngine.getPDPGroup((EcompPDP) pdp);
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
				//
				// Do the connect
				//
				connection.connect();
				if (connection.getResponseCode() == 204) {
					logger.info("Success. We are configured correctly.");
					loggingContext.transactionEnded();
					auditLogger.info("Success. PDP is configured correctly.");
					PolicyLogger.audit("Transaction Success. PDP is configured correctly.");
					setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);
				} else if (connection.getResponseCode() == 200) {
					logger.info("Success. PDP needs to update its configuration.");
					loggingContext.transactionEnded();
					auditLogger.info("Success. PDP needs to update its configuration.");
					PolicyLogger.audit("Transaction Success. PDP is configured correctly.");
					setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
				} else {
					logger.warn("Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
					loggingContext.transactionEnded();
					auditLogger.warn("Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
					PolicyLogger.audit("Transaction Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());

					setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
				}
			} catch (Exception e) {
				PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Unable to sync config with PDP '" + pdp.getId() + "'");
				loggingContext.transactionEnded();
				PolicyLogger.audit("Transaction Failed: Unable to sync config with PDP '" + pdp.getId() + "': " + e);
				try {
					setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
				} catch (PAPException e1) {
					PolicyLogger.audit("Transaction Failed: Unable to set status of PDP " + pdp.getId() + " to UNKNOWN: " + e);

					PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Unable to set status of PDP '" + pdp.getId() + "' to UNKNOWN");
				}
			} finally {
				// cleanup the connection
				connection.disconnect();

				// tell the AC to update it's status info
				notifyAC();
			}

		}
	}

	//
	// RESTful Interface from PAP to ACs notifying them of changes
	//

	private void notifyAC() {
		// kick off a thread to do one event notification for all registered ACs
		// This needs to be on a separate thread so that ACs can make calls back to PAP to get the updated Group data
		// as part of processing this message on their end.
		Thread t = new Thread(new NotifyACThread());
		t.start();
	}

	private class NotifyACThread implements Runnable {

		public void run() {
			List<String> disconnectedACs = new ArrayList<String>();

			// There should be no Concurrent exception here because the list is a CopyOnWriteArrayList.
			// The "for each" loop uses the collection's iterator under the covers, so it should be correct.
			for (String acURL : adminConsoleURLStringList) {
				HttpURLConnection connection = null;
				try {

					acURL += "?PAPNotification=true";

					//TODO - Currently we just tell AC that "Something changed" without being specific.  Do we want to tell it which group/pdp changed?
					//TODO - If so, put correct parameters into the Query string here
					acURL += "&objectType=all" + "&action=update";

					if (logger.isDebugEnabled()) {
						logger.debug("creating url for id '" + acURL + "'");
					}
					//TODO - currently always send both policies and pips.  Do we care enough to add code to allow sending just one or the other?
					//TODO		(need to change "cache=", implying getting some input saying which to change)

					URL url = new URL(acURL );

					//
					// Open up the connection
					//
					connection = (HttpURLConnection)url.openConnection();
					//
					// Setup our method and headers
					//
					connection.setRequestMethod("PUT");
					connection.setRequestProperty("Content-Type", "text/x-java-properties");
					//
					// Adding this in. It seems the HttpUrlConnection class does NOT
					// properly forward our headers for POST re-direction. It does so
					// for a GET re-direction.
					//
					// So we need to handle this ourselves.
					//
					//TODO - is this needed for a PUT?  seems better to leave in for now?
					connection.setInstanceFollowRedirects(false);
					//
					// Do not include any data in the PUT because this is just a
					// notification to the AC.
					// The AC will use GETs back to the PAP to get what it needs
					// to fill in the screens.
					//

					//
					// Do the connect
					//
					connection.connect();
					if (connection.getResponseCode() == 204) {
						logger.info("Success. We updated correctly.");
					} else {
						logger.warn(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Failed: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
					}

				} catch (Exception e) {
					//TODO:EELF Cleanup - Remove logger
					//logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + "Unable to sync config AC '" + acURL + "': " + e, e);
					PolicyLogger.error(MessageCodes.ERROR_SYSTEM_ERROR, e, "XACMLPapServlet", " Unable to sync config AC '" + acURL + "'");
					disconnectedACs.add(acURL);
				} finally {
					// cleanup the connection
					connection.disconnect();
				}
			}

			// remove any ACs that are no longer connected
			if (disconnectedACs.size() > 0) {
				adminConsoleURLStringList.removeAll(disconnectedACs);
			}

		}
	}

	/*
	 * Added by Mike M in 1602 release for Authorizing the PEP Requests for Granularity. 
	 */
	private boolean authorizeRequest(HttpServletRequest request) {
		if(request instanceof HttpServletRequest) {

			// Get the client Credentials from the Request header. 
			String clientCredentials = request.getHeader(ENVIRONMENT_HEADER);

			// Check if the Client is Authorized. 
			if(clientCredentials!=null && clientCredentials.equalsIgnoreCase(environment)){
				return true;
			}else{
				return false;
			}
		} else {
			return false;
		}
	}

	public static String getConfigHome(){
		try {
			loadWebapps();
		} catch (Exception e) {
			return null;
		}
		return CONFIG_HOME;
	}

	public static String getActionHome(){
		try {
			loadWebapps();
		} catch (Exception e) {
			return null;
		}
		return ACTION_HOME;
	}

	private static void loadWebapps() throws Exception{
		if(ACTION_HOME == null || CONFIG_HOME == null){
			Path webappsPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_WEBAPPS));
			//Sanity Check
			if (webappsPath == null) {
				PolicyLogger.error("Invalid Webapps Path Location property : " + XACMLRestProperties.PROP_PAP_WEBAPPS);
				throw new Exception("Invalid Webapps Path Location property : " + XACMLRestProperties.PROP_PAP_WEBAPPS);
			}
			Path webappsPathConfig;
			Path webappsPathAction;
			if(webappsPath.toString().contains("\\"))
			{
				webappsPathConfig = Paths.get(webappsPath.toString()+"\\Config");
				webappsPathAction = Paths.get(webappsPath.toString()+"\\Action");
			}
			else
			{
				webappsPathConfig = Paths.get(webappsPath.toString()+"/Config");
				webappsPathAction = Paths.get(webappsPath.toString()+"/Action");
			}
			if (Files.notExists(webappsPathConfig)) 
			{
				try {
					Files.createDirectories(webappsPathConfig);
				} catch (IOException e) {
					PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW, e, "XACMLPapServlet", " Failed to create config directory: "
							+ webappsPathConfig.toAbsolutePath().toString());
				}
			}
			if (Files.notExists(webappsPathAction)) 
			{
				try {
					Files.createDirectories(webappsPathAction);
				} catch (IOException e) {
					logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Failed to create config directory: "
							+ webappsPathAction.toAbsolutePath().toString(), e);
				}
			}
			ACTION_HOME = webappsPathAction.toString();
			CONFIG_HOME = webappsPathConfig.toString();
		}
	}

	/**
	 * @return the emf
	 */
	public EntityManagerFactory getEmf() {
		return emf;
	}
	public IntegrityMonitor getIm() {
		return im;
	}

	public IntegrityAudit getIa() {
		return ia;
	}
}
