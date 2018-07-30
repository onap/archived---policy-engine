/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest;

import com.att.research.xacml.util.XACMLProperties;

/**
 * These are XACML Properties that are relevant to the RESTful API interface for
 * the PDP, PAP and AC interfaces.
 * 
 *
 */
public class XACMLRestProperties extends XACMLProperties {
    /**
     * A unique identifier for the PDP servlet instance. Usually set to the URL
     * it is running as in the J2EE container.
     *
     * Eg. http://localhost:8080/pdp/
     */
    public static final String PROP_PDP_ID = "xacml.rest.pdp.id";
    /**
     * A PDP servlet's configuration directory. Holds the pip and policy
     * configuration data as well as the local policy cache.
     *
     * Eg: /opt/app/xacml/config
     */
    public static final String PROP_PDP_CONFIG = "xacml.rest.pdp.config";
    // Resilience feature-
    public static final String PROP_PDP_WEBAPPS = "xacml.rest.pdp.webapps";
    //Closed Loop JSON table
    public static final String PROP_ADMIN_CLOSEDLOOP = "xacml.rest.admin.closedLoopJSON";
    /**
     * Set this property to true or false if the PDP servlet should register
     * itself upon startup with the PAP servlet.
     */
    public static final String PROP_PDP_REGISTER = "xacml.rest.pdp.register";
    /**
     * Number of seconds the PDP will sleep while retrying registration with the
     * PAP. This value must be greater or equal to 5.
     */
    public static final String PROP_PDP_REGISTER_SLEEP = "xacml.rest.pdp.register.sleep";
    /**
     * Number of retry attempts at registration with the PAP. A value of -1
     * indicates infinite retries.
     */
    public static final String PROP_PDP_REGISTER_RETRIES = "xacml.rest.pdp.register.retries";
    /**
     * Max content length accepted for an incoming POST XML/JSON request.
     * Default is 32767 bytes.
     */
    public static final String PROP_PDP_MAX_CONTENT = "xacml.rest.pdp.maxcontent";
    /**
     * Custom HTTP header used by PDP to send the value of the PROP_PDP_ID
     */
    public static final String PROP_PDP_HTTP_HEADER_ID = "X-XACML-PDP-ID";
    /**
     * Custom HHTP header used by PDP to send its heartbeat value.
     */
    public static final String PROP_PDP_HTTP_HEADER_HB = "X-XACML-PDP-HB";
    /*
     * Custom HTTP header used by PDP to send the value of the
     * X-XACML-PDP-JMX-PORT
     */
    public static final String PROP_PDP_HTTP_HEADER_JMX_PORT = "X-XACML-PDP-JMX-PORT";
    /**
     * The URL of the PAP servlet. Used by PDP servlet's to communicate. Because
     * administrators can set whatever context they want to run the PAP servlet,
     * it isn't easy to determine a return URL for the PAP servlet. This is
     * especially true upon initialization.
     */
    public static final String PROP_PAP_URL = "xacml.rest.pap.url";
    /**
     * A comma divided list of urls pointing to avaiable PAP urls.
     * If one or more fail, the other servers in the list can
     * handle the requests.
     */
    public static final String PROP_PAP_URLS = "xacml.rest.pap.urls";
    public static final String PROP_PAP_FAILED_URLS = "xacml.rest.pap.failedUrls";
    public static final String PROP_PAP_SUCCEEDED_URLS = "xacml.rest.pap.succeededUrls";

    /**
     * Upon startup, have the PAP servlet send latest configuration information
     * to all the PDP nodes it knows about.
     */
    public static final String PROP_PAP_INITIATE_PDP_CONFIG = "xacml.rest.pap.initiate.pdp";
    /**
     * The interval the PAP servlet uses to send heartbeat requests to the PDP
     * nodes.
     */
    public static final String PROP_PAP_HEARTBEAT_INTERVAL = "xacml.rest.pap.heartbeat.interval";
    /**
     * Timeout value used by the PAP servlet when trying to check the heartbeat
     * of a PDP node.
     */
    public static final String PROP_PAP_HEARTBEAT_TIMEOUT = "xacml.rest.pap.heartbeat.timeout";
    /*
     * This is the domain you can setup for your organization, it should be a URI.
     * Eg. com:sample:foo
     */
    public static final String PROP_PAP_DOMAIN = "xacml.rest.pap.domain";

    /*
     * Local path to where user workspaces exist. The user workspace contains temporary files, the
     * user's clone of the GIT repository, anything specific to the user, etc.
     */
    public static final String PROP_PAP_WORKSPACE = "xacml.rest.pap.workspace";

    /*
     * Local path to  where the GIT repository exists.
     *
     * Eg. /opt/app/xacml/repository
     */
    public static final String PROP_PAP_REPOSITORY = "xacml.rest.pap.repository";

    /*
     * Database driver property
     */
    public static final String PROP_PAP_DB_DRIVER = "javax.persistence.jdbc.driver";

    /*
     * Database url
     */
    public static final String PROP_PAP_DB_URL = "javax.persistence.jdbc.url";

    /*
     * Database user
     */
    public static final String PROP_PAP_DB_USER = "javax.persistence.jdbc.user";

    /*
     * Database password
     */
    public static final String PROP_PAP_DB_PASSWORD = "javax.persistence.jdbc.password";

    /*
     * Time in ms which a Policy DB transaction will wait to get the transaction lock object
     */
    public static final String PROP_PAP_TRANS_WAIT = "xacml.rest.pap.transaction.waitms";

    /*
     * Policy DB transaction timeout in ms after it has obtained the transaction lock object
     */
    public static final String PROP_PAP_TRANS_TIMEOUT = "xacml.rest.pap.transaction.timeoutms";

    /*
     * Policy Audit transaction timeout in ms after it has obtained the transaction lock object
     */
    public static final String PROP_PAP_AUDIT_TIMEOUT = "xacml.rest.pap.audit.timeoutms";

    /*
     * Value determines direction of audit.  Value=true will synch the file system to contents of the DB.
     * Value=false will synch the DB to the contents of the file system.
     */
    public static final String PROP_PAP_AUDIT_FLAG = "xacml.rest.pap.filesystem.audit";

    /*
     * Value for enable/disable of audit functionality
     */
    public static final String PROP_PAP_RUN_AUDIT_FLAG = "xacml.rest.pap.run.audit.flag";

    /*
     * Controls how long the timeout will be when a pap sends a notification to another pap
     */
    public static final String PROP_PAP_NOTIFY_TIMEOUT = "xacml.rest.pap.notify.timeoutms";
    /*
     *  Value for Enable/Disable of AutoPush Flag.
     */
    public static final String PROP_PAP_PUSH_FLAG = "xacml.rest.pap.autopush.flag";

    /*
     *  Properties file for the AutoPush Functionality.
     */
    public static final String PROP_PAP_PUSH_FILE = "xacml.rest.pap.autopush.file";

    /*
     * Local path to where the GIT repository exists.
     *
     * Eg. /opt/app/xacml/repository
     */
    public static final String PROP_ADMIN_REPOSITORY = "xacml.rest.admin.repository";
    /*
     * Local path to where user workspaces exist. The user workspace contains
     * temporary files, the user's clone of the GIT repository, anything
     * specific to the user, etc.
     */
    public static final String PROP_ADMIN_WORKSPACE = "xacml.rest.admin.workspace";
    /*
     * This is the domain you can setup for your organization, it should be a
     * URI.
     *
     * Eg. com:sample:foo
     */
    public static final String PROP_ADMIN_DOMAIN = "xacml.rest.admin.domain";
    /**
     * PROP_ADMIN_USER_NAME is simply a name for the logged in user.
     *
     * AC authentication is out the scope of the web application itself. It is
     * up to the developer to setup authentication as they please in the J2EE
     * container used to run the web application. Whatever authentication
     * mechanism they use, they should then set the attribute into the
     * HttpSession object. The Admin Console will be able to read that value
     * (default to "guest") in.
     *
     * ((HttpServletRequest)
     * request).getSession().setAttribute("xacml.rest.admin.user.name",
     * "Homer");
     *
     */
    public static final String PROP_ADMIN_USER_NAME = "xacml.rest.admin.user.name";
    /**
     *
     * PROP_ADMIN_USER_ID is an id for the logged in user.
     *
     * Eg. hs1234
     *
     * @see #PROP_ADMIN_USER_NAME for more information.
     */
    public static final String PROP_ADMIN_USER_ID = "xacml.rest.admin.user.id";
    /**
     *
     * PROP_ADMIN_USER_EMAIL is a user's email address.
     *
     * @see #PROP_ADMIN_USER_NAME for more information.
     */
    public static final String PROP_ADMIN_USER_EMAIL = "xacml.rest.admin.user.email";
    /**
     * Directory path containing sub-directories where the Subscriber servlet
     * puts files sent through data feeds.
     */
    public static final String PROP_SUBSCRIBER_INCOMING = "xacml.subscriber.incoming";
    /**
     * The specific data feed name for the Subscriber servlet to register for.
     */
    public static final String PROP_SUBSCRIBER_FEED = "xacml.subscriber.feed";
    /**
     * Value for the log time frame that is to be stored in the database any
     * logs after this time frame will be removed.
     */
    public static final String PROP_LOG_TIMEFRAME = "xacml.log.timeframe";
    /**
     * Value for the DB connections used to store the log files.
     */
    public static final String PROP_LOG_DB_DRIVER = "xacml.log.db.driver";
    public static final String PROP_LOG_DB_URL = "xacml.log.db.url";
    public static final String PROP_LOG_DB_USER = "xacml.log.db.user";
    public static final String PROP_LOG_DB_PASSWORD = "xacml.log.db.password";
    /*
     * Value for JMX port for the PDP
     */
    public static final String PROP_PDP_JMX_PORT = "xacml.jmx.port";

    /*
     * Value for refresh rate
     */
    public static final String PROP_REFRESH_RATE = "xacml.refresh.rate";

    // added for Security between Policy Components.
    // 6/26
    /*
     * PROP_PAP_USERID is the PAP Unique User ID
     */
    public static final String PROP_PAP_USERID = "xacml.rest.pap.userid";
    /*
     * PROP_PAP_PASS is the PAP password
     */
    public static final String PROP_PAP_PASS = "xacml.rest.pap.password";
    /*
     * PROP_PAP_PASS is the PAP password
     */
    public static final String PROP_CONFIG_URL = "xacml.rest.config.url";
    /*
     * PROP_PDP_USERID is the PDP Unique User ID
     */
    public static final String PROP_PDP_USERID = "xacml.rest.pdp.userid";
    /*
     * PROP_PDP_PASS is the PDP password
     */
    public static final String PROP_PDP_PASS = "xacml.rest.pdp.password";
    /*
     * PROP_PDP_IDFILE is the PDP Authentication File
     */
    public static final String PROP_PDP_IDFILE = "xacml.rest.pdp.idfile";
    /*
     * PROP_PEP_IDFILE is the Client Authentication File
     */
    public static final String PROP_PEP_IDFILE = "xacml.rest.pep.idfile";
    /*
     * webapps Location of the PAP-REST server
     */
    public static final String PROP_PAP_WEBAPPS= "xacml.rest.config.webapps";
    /*
     * Value for Notification Option
     */
    public static final String PROP_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    /*
     * Value for Notification DMaaP servers
     */
    public static final String PROP_NOTIFICATION_SERVERS = "NOTIFICATION_SERVERS";
    /*
     * Value for Notification Delay
     */
    public static final String PROP_NOTIFICATION_DELAY= "NOTIFICATION_DELAY";
    /*
     * Value for Notification Topic
     */
    public static final String PROP_NOTIFICATION_TOPIC= "NOTIFICATION_TOPIC";
    /*
     * Value for Notification Topic
     */
    public static final String PROP_UEB_API_KEY= "UEB_API_KEY";
    /*
     * Value for Notification Topic
     */
    public static final String PROP_UEB_API_SECRET= "UEB_API_SECRET";
    /*
     * Closedloop Fault Policy Template Version
     */
    public static final String TemplateVersion_Fault= "xacml.rest.closedLoopFault";
    /*
     * Closedloop PM Policy Template Version
     */
    public static final String TemplateVersion_PM= "xacml.rest.closedLoopPM";
    /*
     * Value for model properties file
     */
    public static final String PROP_ADMIN_MICROSERVICE = "xacml.rest.admin.microServiceModel";
    /*
     * MicroService Policy Template Version
     */
    public static final String TemplateVersion_MS= "xacml.rest.microServices";
    /*
     * Optimization Policy Template Version
     */
    public static final String TemplateVersion_OOF= "xacml.rest.optimization";
    /*
     * Firewall Policy Template Version
     */
    public static final String TemplateVersion_FW= "xacml.rest.firewallPolicy";
    /*
     *  Size of SelectList for Users in MS
     *
    */
    public static final String PROP_USER_SELECTLIST_WINDOW_SIZE= "xacml.user.column.count";
    /*
     * Audit function in pap admin to Update userinfo table to syncronize with Roles table
     */
    public static final String PROP_ROLES_USERINFO_AUDIT= "xacml.audit.userInfo";
    /*
     * test Environment LoginId
     */
    public static final String PROP_TEST_ENVIRONMENT_LOGINID= "xacml.testEnvironment.loginId";
    /*
     *  Size of of the page length for sqlcontainer
     *
    */
    public static final String PROP_SQLCONTAINER_PAGE_LENGTH= "xacml.sqlcontainer.page.length";
    /*
     *  add values used to connect to restful api
     *
    */
    public static final String PROP_RESTFUL_INTERFACE= "xacm.restful.interface.file";
    /*
     *  add pattern to identify what values are designed as required
     *
    */
    public static final String PROP_XCORE_REQUIRED_PATTERN= "xacm.xcor.required.pattern";
    /*
     *  Time before a cache value is evicted
     *
    */
    public static final String PROP_CACHE_LIVE_TIME= "xacm.cache.live.time";
    /*
     *  Highest value allowed in priority
     *
    */
    public static final String PROP_PRIORITY_COUNT= "xacml.max.priority.count";
    /*
     * The name of the PAP.  Must be unique across the system
     */
    public static final String PAP_RESOURCE_NAME="xacml.rest.pap.resource.name";
    /*
     * The name of the site in which the PAP resides
     */
    public static final String PAP_SITE_NAME="site_name";
    /*
     * The node type of the PAP - really a no-op since it's value is pap
     */
    public static final String PAP_NODE_TYPE="node_type";
    /*
     * A list of the groups of resources/nodes on which the PAP is dependent. The members of a
     * group are comma-separated and the groups are separated with semicolons.
     */
    public static final String PAP_DEPENDENCY_GROUPS="dependency_groups";
    /*
     * The (optional) period of time in seconds between executions of the integrity audit.
     * Value < 0 : Audit does not run (default value if property is not present = -1)
     * Value = 0 : Audit runs continuously
     * Value > 0 : The period of time in seconds between execution of the audit on a particular node
     */
    public static final String PAP_INTEGRITY_AUDIT_PERIOD_SECONDS = "integrity_audit_period_seconds";
    /*
     * The name of the Admin.  Must be unique across the system
     */
    public static final String ADMIN_RESOURCE_NAME="xacml.rest.admin.resource.name";
    /*
     * The name of the PDP.  Must be unique across the system
     */
    public static final String PDP_RESOURCE_NAME="xacml.rest.pdp.resource.name";
    /*
     * Audit function in pap admin to Update userinfo table to syncronize with Roles table
     */
    public static final String PROP_AUTOMATIC_POLICYPUSH= "xacml.automatic.push";
    /*
     * Add Limit for Onap Portal Dashboard tab data
     */
    public static final String PROP_ONAP_LOGLIMIT = "xacml.onap.dashboard.logTableLimit";
    public static final String PROP_ONAP_SYSTEMALERTLIMIT = "xacml.onap.dashboard.systemAlertTableLimit";
    /*
     * Diff of the policies for the Firewall Feature.
     */
    public static final String PROP_FW_GETURL = "FW_GETURL";
    public static final String PROP_FW_AUTHOURL = "FW_AUTHOURL";
    public static final String PROP_FW_PROXY = "FW_PROXY";
    public static final String PROP_FW_PORT = "FW_PORT";

    /*
     * The number of Risk Levels allowed
     */
    public static final String ADMIN_RISK_LEVEL_COUNT="xacml.risk.level.count";
    /*
     * The maxium Level displayed on the UI for Micro Services
     */
    public static final String PROP_MODEL_LEVEL = "xacml.model.level";

    /*
     * Value for Incoming Notification tries
     *
     * */
    public static final String PROP_PAP_INCOMINGNOTIFICATION_TRIES = "xacml.rest.pap.incomingnotification.tries";


    // Static class, hide constructor
    private XACMLRestProperties() {
        super();
    }
}
