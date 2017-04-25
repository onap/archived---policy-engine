-- ---------------------------------------------------------------------------------------------------------------
-- This script creates tables in the 1707 COMMON version of the ECOMP SDK application database.
-- Additional DDL scripts may be required for the AT&T version or the OPEN-SOURCE version!
--
-- Note to database admin: set the MySQL system variable called lower_case_table_names to 1
-- It can be set 3 different ways: 
--   command-line options (Cmd-line), 
--   options valid in configuration files (Option file), or 
--   server system variables (System Var). 
--
-- When set to 1 table names are stored in lowercase on disk and comparisons are not case sensitive. 
--
-- MySql/MariaDB Version compatibility information
--
-- bash-4.2$ mysql --version  – cluster version
-- mysql  Ver 15.1 Distrib 10.1.17-MariaDB, for Linux (x86_64) using readline 5.1
--
-- All versions newer or older than these DO NOT necessarily mean they are compatible.
-- ---------------------------------------------------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS=1; 

CREATE DATABASE IF NOT EXISTS ecomp_sdk;

USE ecomp_sdk;

-- ---------- create table SECTION
--
-- NAME: CR_FAVORITE_REPORTS; TYPE: TABLE 
--
create table cr_favorite_reports (
    USER_ID INTEGER NOT NULL,
    REP_ID INTEGER NOT NULL
);

--
-- NAME: CR_FILEHIST_LOG; TYPE: TABLE 
--
create table cr_filehist_log (
    SCHEDULE_ID NUMERIC(11,0) NOT NULL,
    URL CHARACTER VARYING(4000),
    NOTES CHARACTER VARYING(3500),
    RUN_TIME TIMESTAMP
);

--
-- NAME: CR_FOLDER; TYPE: TABLE 
--
create table cr_folder (
    FOLDER_ID INTEGER NOT NULL,
    FOLDER_NAME CHARACTER VARYING(50) NOT NULL,
    DESCR CHARACTER VARYING(500),
    CREATE_ID INTEGER NOT NULL,
    CREATE_DATE TIMESTAMP NOT NULL,
    PARENT_FOLDER_ID INTEGER,
    PUBLIC_YN CHARACTER VARYING(1) DEFAULT 'N' NOT NULL
);

--
-- NAME: CR_FOLDER_ACCESS; TYPE: TABLE 
--
create table cr_folder_access (
    FOLDER_ACCESS_ID NUMERIC(11,0) NOT NULL,
    FOLDER_ID NUMERIC(11,0) NOT NULL,
    ORDER_NO NUMERIC(11,0) NOT NULL,
    ROLE_ID NUMERIC(11,0),
    USER_ID NUMERIC(11,0),
    READ_ONLY_YN CHARACTER VARYING(1) DEFAULT 'N' NOT NULL
);

--
-- NAME: CR_HIST_USER_MAP; TYPE: TABLE 
--
create table cr_hist_user_map (
    HIST_ID INT(11) NOT NULL,
    USER_ID INT(11) NOT NULL
);

--
-- NAME: CR_LU_FILE_TYPE; TYPE: TABLE 
--
create table cr_lu_file_type (
    LOOKUP_ID NUMERIC(2,0) NOT NULL,
    LOOKUP_DESCR CHARACTER VARYING(255) NOT NULL,
    ACTIVE_YN CHARACTER(1) DEFAULT 'Y',
    ERROR_CODE NUMERIC(11,0)
);

--
-- NAME: CR_RAPTOR_ACTION_IMG; TYPE: TABLE 
--
create table cr_raptor_action_img (
    IMAGE_ID CHARACTER VARYING(100) NOT NULL,
    IMAGE_LOC CHARACTER VARYING(400)
);

--
-- NAME: CR_RAPTOR_PDF_IMG; TYPE: TABLE 
--
create table cr_raptor_pdf_img (
    IMAGE_ID CHARACTER VARYING(100) NOT NULL,
    IMAGE_LOC CHARACTER VARYING(400)
);

--
-- NAME: CR_REMOTE_SCHEMA_INFO; TYPE: TABLE 
--
create table cr_remote_schema_info (
    SCHEMA_PREFIX CHARACTER VARYING(5) NOT NULL,
    SCHEMA_DESC CHARACTER VARYING(75) NOT NULL,
    DATASOURCE_TYPE CHARACTER VARYING(100)
);

--
-- NAME: CR_REPORT; TYPE: TABLE 
--
create table cr_report (
    REP_ID NUMERIC(11,0) NOT NULL,
    TITLE CHARACTER VARYING(100) NOT NULL,
    DESCR CHARACTER VARYING(255),
    PUBLIC_YN CHARACTER VARYING(1) DEFAULT 'N' NOT NULL,
    REPORT_XML TEXT,
    CREATE_ID NUMERIC(11,0),
    CREATE_DATE TIMESTAMP default now(),
    MAINT_ID NUMERIC(11,0),
    MAINT_DATE TIMESTAMP DEFAULT NOW(), 
    MENU_ID CHARACTER VARYING(500),
    MENU_APPROVED_YN CHARACTER VARYING(1) DEFAULT 'N' NOT NULL,
    OWNER_ID NUMERIC(11,0),
    FOLDER_ID INTEGER DEFAULT 0,
    DASHBOARD_TYPE_YN CHARACTER VARYING(1) DEFAULT 'N',
    DASHBOARD_YN CHARACTER VARYING(1) DEFAULT 'N'
);

--
-- NAME: CR_REPORT_ACCESS; TYPE: TABLE 
--
create table cr_report_access (
    REP_ID NUMERIC(11,0) NOT NULL,
    ORDER_NO NUMERIC(11,0) NOT NULL,
    ROLE_ID NUMERIC(11,0),
    USER_ID NUMERIC(11,0),
    READ_ONLY_YN CHARACTER VARYING(1) DEFAULT 'N' NOT NULL
);

--
-- NAME: CR_REPORT_DWNLD_LOG; TYPE: TABLE 
--
create table cr_report_dwnld_log (
    USER_ID NUMERIC(11,0) NOT NULL,
    REP_ID INTEGER NOT NULL,
    FILE_NAME CHARACTER VARYING(100) NOT NULL,
    DWNLD_START_TIME TIMESTAMP DEFAULT NOW() NOT NULL,
    RECORD_READY_TIME TIMESTAMP DEFAULT NOW(),
    FILTER_PARAMS CHARACTER VARYING(2000)
);

--
-- NAME: CR_REPORT_EMAIL_SENT_LOG; TYPE: TABLE 
--
create table cr_report_email_sent_log (
    LOG_ID INTEGER NOT NULL,
    SCHEDULE_ID NUMERIC(11,0),
    GEN_KEY CHARACTER VARYING(25) NOT NULL,
    REP_ID NUMERIC(11,0) NOT NULL,
    USER_ID NUMERIC(11,0),
    SENT_DATE TIMESTAMP DEFAULT NOW(),
    ACCESS_FLAG CHARACTER VARYING(1) DEFAULT 'Y' NOT NULL,
    TOUCH_DATE TIMESTAMP DEFAULT NOW()
);

--
-- NAME: CR_REPORT_FILE_HISTORY; TYPE: TABLE 
--
create table cr_report_file_history (
    HIST_ID INT(11) NOT NULL,
    SCHED_USER_ID NUMERIC(11,0) NOT NULL,
    SCHEDULE_ID NUMERIC(11,0) NOT NULL,
    USER_ID NUMERIC(11,0) NOT NULL,
    REP_ID NUMERIC(11,0),
    RUN_DATE TIMESTAMP,
    RECURRENCE CHARACTER VARYING(50),
    FILE_TYPE_ID NUMERIC(2,0),
    FILE_NAME CHARACTER VARYING(80),
    FILE_BLOB BLOB,
    FILE_SIZE NUMERIC(11,0),
    RAPTOR_URL CHARACTER VARYING(4000),
    ERROR_YN CHARACTER(1) DEFAULT 'N',
    ERROR_CODE NUMERIC(11,0),
    DELETED_YN CHARACTER(1) DEFAULT 'N',
    DELETED_BY NUMERIC(38,0)
);

--
-- NAME: CR_REPORT_LOG; TYPE: TABLE 
--
create table cr_report_log (
    REP_ID NUMERIC(11,0) NOT NULL,
    LOG_TIME TIMESTAMP NOT NULL,
    USER_ID NUMERIC(11,0) NOT NULL,
    ACTION CHARACTER VARYING(2000) NOT NULL,
    ACTION_VALUE CHARACTER VARYING(50),
    FORM_FIELDS CHARACTER VARYING(4000)
);

--
-- NAME: CR_REPORT_SCHEDULE; TYPE: TABLE 
--
create table cr_report_schedule (
    SCHEDULE_ID NUMERIC(11,0) NOT NULL,
    SCHED_USER_ID NUMERIC(11,0) NOT NULL,
    REP_ID NUMERIC(11,0) NOT NULL,
    ENABLED_YN CHARACTER VARYING(1) NOT NULL,
    START_DATE TIMESTAMP DEFAULT NOW(),
    END_DATE TIMESTAMP DEFAULT NOW(),
    RUN_DATE TIMESTAMP DEFAULT NOW(),
    RECURRENCE CHARACTER VARYING(50),
    CONDITIONAL_YN CHARACTER VARYING(1) NOT NULL,
    CONDITION_SQL CHARACTER VARYING(4000),
    NOTIFY_TYPE INTEGER DEFAULT 0,
    MAX_ROW INTEGER DEFAULT 1000,
    INITIAL_FORMFIELDS CHARACTER VARYING(3500),
    PROCESSED_FORMFIELDS CHARACTER VARYING(3500),
    FORMFIELDS CHARACTER VARYING(3500),
    CONDITION_LARGE_SQL TEXT,
    ENCRYPT_YN CHARACTER(1) DEFAULT 'N',
    ATTACHMENT_YN CHARACTER(1) DEFAULT 'Y'
);

--
-- NAME: CR_REPORT_SCHEDULE_USERS; TYPE: TABLE 
--
create table cr_report_schedule_users (
    SCHEDULE_ID NUMERIC(11,0) NOT NULL,
    REP_ID NUMERIC(11,0) NOT NULL,
    USER_ID NUMERIC(11,0) NOT NULL,
    ROLE_ID NUMERIC(11,0),
    ORDER_NO NUMERIC(11,0) NOT NULL
);

--
-- NAME: CR_REPORT_TEMPLATE_MAP; TYPE: TABLE 
--
create table cr_report_template_map (
    REPORT_ID INTEGER NOT NULL,
    TEMPLATE_FILE CHARACTER VARYING(200)
);

--
-- NAME: CR_SCHEDULE_ACTIVITY_LOG; TYPE: TABLE 
--
create table cr_schedule_activity_log (
    SCHEDULE_ID NUMERIC(11,0) NOT NULL,
    URL CHARACTER VARYING(4000),
    NOTES CHARACTER VARYING(2000),
    RUN_TIME TIMESTAMP
);

--
-- NAME: CR_TABLE_JOIN; TYPE: TABLE 
--
create table cr_table_join (
    SRC_TABLE_NAME CHARACTER VARYING(30) NOT NULL,
    DEST_TABLE_NAME CHARACTER VARYING(30) NOT NULL,
    JOIN_EXPR CHARACTER VARYING(500) NOT NULL
);

--
-- NAME: CR_TABLE_ROLE; TYPE: TABLE 
--
create table cr_table_role (
    TABLE_NAME CHARACTER VARYING(30) NOT NULL,
    ROLE_ID NUMERIC(11,0) NOT NULL
);

--
-- NAME: CR_TABLE_SOURCE; TYPE: TABLE 
--
create table cr_table_source (
    TABLE_NAME CHARACTER VARYING(30) NOT NULL,
    DISPLAY_NAME CHARACTER VARYING(30) NOT NULL,
    PK_FIELDS CHARACTER VARYING(200),
    WEB_VIEW_ACTION CHARACTER VARYING(50),
    LARGE_DATA_SOURCE_YN CHARACTER VARYING(1) DEFAULT 'N' NOT NULL,
    FILTER_SQL CHARACTER VARYING(4000),
    SOURCE_DB CHARACTER VARYING(50)
);

--
-- NAME: FN_LU_TIMEZONE; TYPE: TABLE 
--
create table fn_lu_timezone (
    TIMEZONE_ID INT(11) NOT NULL,
    TIMEZONE_NAME CHARACTER VARYING(100) NOT NULL,
    TIMEZONE_VALUE CHARACTER VARYING(100) NOT NULL
);

create table fn_user (
    USER_ID INT(11) NOT NULL PRIMARY KEY  AUTO_INCREMENT,
    ORG_ID INT(11),
    MANAGER_ID INT(11),
    FIRST_NAME CHARACTER VARYING(50),
    MIDDLE_NAME CHARACTER VARYING(50),
    LAST_NAME CHARACTER VARYING(50),
    PHONE CHARACTER VARYING(25),
    FAX CHARACTER VARYING(25),
    CELLULAR CHARACTER VARYING(25),
    EMAIL CHARACTER VARYING(50),
    ADDRESS_ID NUMERIC(11,0),
    ALERT_METHOD_CD CHARACTER VARYING(10),
    HRID CHARACTER VARYING(20),
    ORG_USER_ID CHARACTER VARYING(20),
    ORG_CODE CHARACTER VARYING(30),
    LOGIN_ID CHARACTER VARYING(25),
    LOGIN_PWD CHARACTER VARYING(25),
    LAST_LOGIN_DATE TIMESTAMP,
    ACTIVE_YN CHARACTER VARYING(1) DEFAULT 'Y' NOT NULL,
    CREATED_ID INT(11),
    CREATED_DATE TIMESTAMP DEFAULT NOW(),
    MODIFIED_ID INT(11),
    MODIFIED_DATE TIMESTAMP default now(),
    IS_INTERNAL_YN CHARACTER(1) DEFAULT 'N' NOT NULL,
    ADDRESS_LINE_1 CHARACTER VARYING(100),
    ADDRESS_LINE_2 CHARACTER VARYING(100),
    CITY CHARACTER VARYING(50),
    STATE_CD CHARACTER VARYING(3),
    ZIP_CODE CHARACTER VARYING(11),
    COUNTRY_CD CHARACTER VARYING(3),
    LOCATION_CLLI CHARACTER VARYING(8),
    ORG_MANAGER_USERID CHARACTER VARYING(20),
    COMPANY CHARACTER VARYING(100),
    DEPARTMENT_NAME CHARACTER VARYING(100),
    JOB_TITLE CHARACTER VARYING(100),
    TIMEZONE INT(11),
    DEPARTMENT CHARACTER VARYING(25),
    BUSINESS_UNIT CHARACTER VARYING(25),
    BUSINESS_UNIT_NAME CHARACTER VARYING(100),
    COST_CENTER CHARACTER VARYING(25),
    FIN_LOC_CODE CHARACTER VARYING(10),
    SILO_STATUS CHARACTER VARYING(10)
);

--
-- NAME: FN_ROLE; TYPE: TABLE 
--
create table fn_role (
    ROLE_ID INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    ROLE_NAME CHARACTER VARYING(50) NOT NULL,
    ACTIVE_YN CHARACTER VARYING(1) DEFAULT 'Y' NOT NULL,
    PRIORITY NUMERIC(4,0)
);

--
-- NAME: FN_AUDIT_ACTION; TYPE: TABLE 
--
create table fn_audit_action (
    AUDIT_ACTION_ID INTEGER NOT NULL,
    CLASS_NAME CHARACTER VARYING(500) NOT NULL,
    METHOD_NAME CHARACTER VARYING(50) NOT NULL,
    AUDIT_ACTION_CD CHARACTER VARYING(20) NOT NULL,
    AUDIT_ACTION_DESC CHARACTER VARYING(200),
    ACTIVE_YN CHARACTER VARYING(1)
);

--
-- NAME: FN_AUDIT_ACTION_LOG; TYPE: TABLE 
--
create table fn_audit_action_log (
    AUDIT_LOG_ID INTEGER NOT NULL PRIMARY KEY  AUTO_INCREMENT,
    AUDIT_ACTION_CD CHARACTER VARYING(200),
    ACTION_TIME TIMESTAMP,
    USER_ID NUMERIC(11,0),
    CLASS_NAME CHARACTER VARYING(100),
    METHOD_NAME CHARACTER VARYING(50),
    SUCCESS_MSG CHARACTER VARYING(20),
    ERROR_MSG CHARACTER VARYING(500)
);

--
-- NAME: FN_LU_ACTIVITY; TYPE: TABLE 
--
create table fn_lu_activity (
    ACTIVITY_CD CHARACTER VARYING(50) NOT NULL PRIMARY KEY,
    ACTIVITY CHARACTER VARYING(50) NOT NULL
);

--
-- NAME: FN_AUDIT_LOG; TYPE: TABLE 
--
create table fn_audit_log (
    LOG_ID INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    USER_ID INT(11) NOT NULL,
    ACTIVITY_CD CHARACTER VARYING(50) NOT NULL,
    AUDIT_DATE TIMESTAMP DEFAULT NOW() NOT NULL,
    COMMENTS CHARACTER VARYING(1000),
    AFFECTED_RECORD_ID_BK CHARACTER VARYING(500),
    AFFECTED_RECORD_ID CHARACTER VARYING(4000),
    CONSTRAINT FK_FN_AUDIT_REF_209_FN_USER FOREIGN KEY (USER_ID) REFERENCES FN_USER(USER_ID)
);

--
-- NAME: FN_BROADCAST_MESSAGE; TYPE: TABLE 
--
create table fn_broadcast_message (
    MESSAGE_ID INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    MESSAGE_TEXT CHARACTER VARYING(1000) NOT NULL,
    MESSAGE_LOCATION_ID NUMERIC(11,0) NOT NULL,
    BROADCAST_START_DATE TIMESTAMP NOT NULL  DEFAULT NOW(),
    BROADCAST_END_DATE TIMESTAMP NOT NULL DEFAULT NOW(),
    ACTIVE_YN CHARACTER(1) DEFAULT 'Y' NOT NULL,
    SORT_ORDER NUMERIC(4,0) NOT NULL,
    BROADCAST_SITE_CD CHARACTER VARYING(50)
);

--
-- NAME: FN_CHAT_LOGS; TYPE: TABLE 
--
create table fn_chat_logs (
    CHAT_LOG_ID INTEGER NOT NULL,
    CHAT_ROOM_ID INTEGER,
    USER_ID INTEGER,
    MESSAGE CHARACTER VARYING(1000),
    MESSAGE_DATE_TIME TIMESTAMP
);

--
-- NAME: FN_CHAT_ROOM; TYPE: TABLE 
--
create table fn_chat_room (
    CHAT_ROOM_ID INTEGER NOT NULL,
    NAME CHARACTER VARYING(50) NOT NULL,
    DESCRIPTION CHARACTER VARYING(500),
    OWNER_ID INTEGER,
    CREATED_DATE TIMESTAMP DEFAULT NOW(),
    UPDATED_DATE TIMESTAMP DEFAULT NOW()
);

--
-- NAME: FN_CHAT_USERS; TYPE: TABLE 
--
create table fn_chat_users (
    CHAT_ROOM_ID INTEGER,
    USER_ID INTEGER,
    LAST_ACTIVITY_DATE_TIME TIMESTAMP,
    CHAT_STATUS CHARACTER VARYING(20),
    ID INTEGER NOT NULL
);

--
-- NAME: FN_DATASOURCE; TYPE: TABLE 
--
create table fn_datasource (
    ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    NAME CHARACTER VARYING(50),
    DRIVER_NAME CHARACTER VARYING(256),
    SERVER CHARACTER VARYING(256),
    PORT INTEGER,
    USER_NAME CHARACTER VARYING(256),
    PASSWORD CHARACTER VARYING(256),
    URL CHARACTER VARYING(256),
    MIN_POOL_SIZE INTEGER,
    MAX_POOL_SIZE INTEGER,
    ADAPTER_ID INTEGER,
    DS_TYPE CHARACTER VARYING(20)
);

--
-- NAME: FN_FUNCTION; TYPE: TABLE 
--
create table fn_function (
    FUNCTION_CD CHARACTER VARYING(30) NOT NULL PRIMARY KEY,
    FUNCTION_NAME CHARACTER VARYING(50) NOT NULL
);

--
-- NAME: FN_LU_ALERT_METHOD; TYPE: TABLE 
--
create table fn_lu_alert_method (
    ALERT_METHOD_CD CHARACTER VARYING(10) NOT NULL,
    ALERT_METHOD CHARACTER VARYING(50) NOT NULL
);

--
-- NAME: FN_LU_BROADCAST_SITE; TYPE: TABLE 
--
create table fn_lu_broadcast_site (
    BROADCAST_SITE_CD CHARACTER VARYING(50) NOT NULL,
    BROADCAST_SITE_DESCR CHARACTER VARYING(100)
);
--
-- NAME: FN_LU_MENU_SET; TYPE: TABLE 
--
create table fn_lu_menu_set (
    MENU_SET_CD CHARACTER VARYING(10) NOT NULL PRIMARY KEY,
    MENU_SET_NAME CHARACTER VARYING(50) NOT NULL
);

--
-- NAME: FN_LU_PRIORITY; TYPE: TABLE 
--
create table fn_lu_priority (
    PRIORITY_ID NUMERIC(11,0) NOT NULL,
    PRIORITY CHARACTER VARYING(50) NOT NULL,
    ACTIVE_YN CHARACTER(1) NOT NULL,
    SORT_ORDER NUMERIC(5,0)
);

--
-- NAME: FN_LU_ROLE_TYPE; TYPE: TABLE 
--
create table fn_lu_role_type (
    ROLE_TYPE_ID NUMERIC(11,0) NOT NULL,
    ROLE_TYPE CHARACTER VARYING(50) NOT NULL
);
--
-- NAME: FN_LU_TAB_SET; TYPE: TABLE 
--
create table fn_lu_tab_set (
    TAB_SET_CD CHARACTER VARYING(30) NOT NULL,
    TAB_SET_NAME CHARACTER VARYING(50) NOT NULL
);

--
-- NAME: FN_MENU; TYPE: TABLE 
--
create table fn_menu (
    MENU_ID INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
    LABEL CHARACTER VARYING(100),
    PARENT_ID INT(11),
    SORT_ORDER NUMERIC(4,0),
    ACTION CHARACTER VARYING(200),
    FUNCTION_CD CHARACTER VARYING(30),
    ACTIVE_YN CHARACTER VARYING(1) DEFAULT 'Y' NOT NULL,
    SERVLET CHARACTER VARYING(50),
    QUERY_STRING CHARACTER VARYING(200),
    EXTERNAL_URL CHARACTER VARYING(200),
    TARGET CHARACTER VARYING(25),
    MENU_SET_CD CHARACTER VARYING(10) DEFAULT 'APP',
    SEPARATOR_YN CHARACTER(1) DEFAULT 'N',
    IMAGE_SRC CHARACTER VARYING(100),
    CONSTRAINT FK_FN_MENU_REF_196_FN_MENU FOREIGN KEY (PARENT_ID) REFERENCES FN_MENU(MENU_ID),
    CONSTRAINT FK_FN_MENU_MENU_SET_CD FOREIGN KEY (MENU_SET_CD) REFERENCES FN_LU_MENU_SET(MENU_SET_CD),
    CONSTRAINT FK_FN_MENU_REF_223_FN_FUNCT FOREIGN KEY (FUNCTION_CD) REFERENCES FN_FUNCTION(FUNCTION_CD)
);

--
-- NAME: FN_ORG; TYPE: TABLE 
--
create table fn_org (
    ORG_ID INT(11) NOT NULL,
    ORG_NAME CHARACTER VARYING(50) NOT NULL,
    ACCESS_CD CHARACTER VARYING(10)
);

--
-- NAME: FN_RESTRICTED_URL; TYPE: TABLE 
--
create table fn_restricted_url (
    RESTRICTED_URL CHARACTER VARYING(250) NOT NULL,
    FUNCTION_CD CHARACTER VARYING(30) NOT NULL
);

--
-- NAME: FN_ROLE_COMPOSITE; TYPE: TABLE 
--
create table fn_role_composite (
    PARENT_ROLE_ID INT(11) NOT NULL,
    CHILD_ROLE_ID INT(11) NOT NULL,
    CONSTRAINT FK_FN_ROLE_COMPOSITE_CHILD FOREIGN KEY (CHILD_ROLE_ID) REFERENCES FN_ROLE(ROLE_ID),
    CONSTRAINT FK_FN_ROLE_COMPOSITE_PARENT FOREIGN KEY (PARENT_ROLE_ID) REFERENCES FN_ROLE(ROLE_ID)
);

--
-- NAME: FN_ROLE_FUNCTION; TYPE: TABLE 
--
create table fn_role_function (
    ROLE_ID INT(11) NOT NULL,
    FUNCTION_CD CHARACTER VARYING(30) NOT NULL,
    CONSTRAINT FK_FN_ROLE__REF_198_FN_ROLE FOREIGN KEY (ROLE_ID) REFERENCES FN_ROLE(ROLE_ID)
);

--
-- NAME: FN_TAB; TYPE: TABLE 
--
create table fn_tab (
    TAB_CD CHARACTER VARYING(30) NOT NULL,
    TAB_NAME CHARACTER VARYING(50) NOT NULL,
    TAB_DESCR CHARACTER VARYING(100),
    ACTION CHARACTER VARYING(100) NOT NULL,
    FUNCTION_CD CHARACTER VARYING(30) NOT NULL,
    ACTIVE_YN CHARACTER(1) NOT NULL,
    SORT_ORDER NUMERIC(11,0) NOT NULL,
    PARENT_TAB_CD CHARACTER VARYING(30),
    TAB_SET_CD CHARACTER VARYING(30)
);

--
-- NAME: FN_TAB_SELECTED; TYPE: TABLE 
--
create table fn_tab_selected (
    SELECTED_TAB_CD CHARACTER VARYING(30) NOT NULL,
    TAB_URI CHARACTER VARYING(40) NOT NULL
);

--
-- NAME: FN_USER_PSEUDO_ROLE; TYPE: TABLE 
--
create table fn_user_pseudo_role (
    PSEUDO_ROLE_ID INT(11) NOT NULL,
    USER_ID INT(11) NOT NULL
);

--
-- NAME: FN_USER_ROLE; TYPE: TABLE 
--
create table fn_user_role (
    USER_ID INT(10) NOT NULL,
    ROLE_ID INT(10) NOT NULL,
    PRIORITY NUMERIC(4,0),
    APP_ID INT(11) DEFAULT 1,
    CONSTRAINT FK_FN_USER__REF_172_FN_USER FOREIGN KEY (USER_ID) REFERENCES FN_USER(USER_ID),
    CONSTRAINT FK_FN_USER__REF_175_FN_ROLE FOREIGN KEY (ROLE_ID) REFERENCES FN_ROLE(ROLE_ID)
);
--
-- NAME: SCHEMA_INFO; TYPE: TABLE 
--
create table schema_info (
    SCHEMA_ID CHARACTER VARYING(25) NOT NULL,
    SCHEMA_DESC CHARACTER VARYING(75) NOT NULL,
    DATASOURCE_TYPE CHARACTER VARYING(100),
    CONNECTION_URL VARCHAR(200) NOT NULL,
    USER_NAME VARCHAR(45) NOT NULL,
    PASSWORD VARCHAR(45) NULL DEFAULT NULL,
    DRIVER_CLASS VARCHAR(100) NOT NULL,
    MIN_POOL_SIZE INT NOT NULL,
    MAX_POOL_SIZE INT NOT NULL,
    IDLE_CONNECTION_TEST_PERIOD INT NOT NULL

);

-- ----------------------------------------------------------
-- NAME: FN_APP; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_app (
  APP_ID int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  APP_NAME varchar(100) NOT NULL DEFAULT '?',
  APP_IMAGE_URL varchar(256) DEFAULT NULL,
  APP_DESCRIPTION varchar(512) DEFAULT NULL,
  APP_NOTES varchar(4096) DEFAULT NULL,
  APP_URL varchar(256) DEFAULT NULL,
  APP_ALTERNATE_URL varchar(256) DEFAULT NULL,
  APP_REST_ENDPOINT varchar(2000) DEFAULT NULL,
  ML_APP_NAME varchar(50) NOT NULL DEFAULT '?',
  ML_APP_ADMIN_ID varchar(7) NOT NULL DEFAULT '?',
  MOTS_ID int(11) DEFAULT NULL,
  APP_PASSWORD varchar(256) NOT NULL DEFAULT '?',
  OPEN char(1) DEFAULT 'N',
  ENABLED char(1) DEFAULT 'Y',
  THUMBNAIL mediumblob,
  APP_USERNAME varchar(50),
  UEB_KEY VARCHAR(256) DEFAULT NULL,
  UEB_SECRET VARCHAR(256) DEFAULT NULL,
  UEB_TOPIC_NAME VARCHAR(256) DEFAULT NULL
  
);

-- ----------------------------------------------------------
-- NAME: FN_FN_WORKFLOW; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_workflow (
  id mediumint(9) NOT NULL AUTO_INCREMENT,
  name varchar(20) NOT NULL,
  description varchar(500) DEFAULT NULL,
  run_link varchar(300) DEFAULT NULL,
  suspend_link varchar(300) DEFAULT NULL,
  modified_link varchar(300) DEFAULT NULL,
  active_yn varchar(300) DEFAULT NULL,
  created varchar(300) DEFAULT NULL,
  created_by int(11) DEFAULT NULL,
  modified varchar(300) DEFAULT NULL,
  modified_by int(11) DEFAULT NULL,
  workflow_key varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
);

-- ----------------------------------------------------------
-- NAME: FN_SCHEDULE_WORKFLOWS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_schedule_workflows (
  id_schedule_workflows bigint(25) PRIMARY KEY NOT NULL AUTO_INCREMENT, 
  workflow_server_url varchar(45) DEFAULT NULL,
  workflow_key varchar(45) NOT NULL,
  workflow_arguments varchar(45) DEFAULT NULL,
  startDateTimeCron varchar(45) DEFAULT NULL,
  endDateTime TIMESTAMP DEFAULT NOW(),
  start_date_time TIMESTAMP DEFAULT NOW(),
  recurrence varchar(45) DEFAULT NULL
  );

--  For demo reporting application add : demo_bar_chart, demo_bar_chart_inter, demo_line_chart, demo_pie_chart and demo_util_chart
-- 	 									demo_scatter_chart, demo_scatter_plot
-- ----------------------------------------------------------
-- NAME: DEMO_BAR_CHART; TYPE: TABLE
-- ----------------------------------------------------------
create table demo_bar_chart (
    label varchar(20),
    value numeric(25,15) 
  );

-- ----------------------------------------------------------
-- NAME: DEMO_BAR_CHART_INTER; TYPE: TABLE
-- ----------------------------------------------------------
create table demo_bar_chart_inter (
    spam_date date,
    num_rpt_sources numeric(10,0),
    num_det_sources numeric(10,0)
  );

-- ----------------------------------------------------------
-- NAME: DEMO_LINE_CHART; TYPE: TABLE
-- ----------------------------------------------------------
create table demo_line_chart (
    series varchar(20),
    log_date date,
    data_value numeric(10,5) 
  );
  
-- ----------------------------------------------------------
-- NAME: DEMO_PIE_CHART; TYPE: TABLE
-- ----------------------------------------------------------
create table demo_pie_chart (
    legend varchar(20),
    data_value numeric(10,5)  
  );  
 
-- ----------------------------------------------------------
-- NAME: DEMO_UTIL_CHART; TYPE: TABLE
-- ----------------------------------------------------------
create table demo_util_chart (
    traffic_date date,
    util_perc numeric(10,5) 
  );  

-- ----------------------------------------------------------
-- NAME: DEMO_SCATTER_CHART; TYPE: TABLE
-- ----------------------------------------------------------  
create table demo_scatter_chart (
  rainfall numeric(10,2),
  key_value varchar(20),
  measurements numeric(10,2)
); 
  
-- ----------------------------------------------------------
-- NAME: DEMO_SCATTER_PLOT; TYPE: TABLE
-- ----------------------------------------------------------
create table demo_scatter_plot
(
  SERIES  VARCHAR(20),
  VALUEX  numeric(25,15),
  VALUEY  numeric(25,15)
);

-- ----------------------------------------------------------
-- NAME: FN_QZ_JOB_DETAILS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_job_details (
SCHED_NAME VARCHAR(120) NOT NULL,
JOB_NAME VARCHAR(200) NOT NULL,
JOB_GROUP VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
JOB_CLASS_NAME VARCHAR(250) NOT NULL,
IS_DURABLE VARCHAR(1) NOT NULL,
IS_NONCONCURRENT VARCHAR(1) NOT NULL,
IS_UPDATE_DATA VARCHAR(1) NOT NULL,
REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);
  
-- ----------------------------------------------------------
-- NAME: FN_QZ_TRIGGERS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_triggers (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
JOB_NAME VARCHAR(200) NOT NULL,
JOB_GROUP VARCHAR(200) NOT NULL,
DESCRIPTION VARCHAR(250) NULL,
NEXT_FIRE_TIME BIGINT(13) NULL,
PREV_FIRE_TIME BIGINT(13) NULL,
PRIORITY INTEGER NULL,
TRIGGER_STATE VARCHAR(16) NOT NULL,
TRIGGER_TYPE VARCHAR(8) NOT NULL,
START_TIME BIGINT(13) NOT NULL,
END_TIME BIGINT(13) NULL,
CALENDAR_NAME VARCHAR(200) NULL,
MISFIRE_INSTR SMALLINT(2) NULL,
JOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
REFERENCES FN_QZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);
  
-- ----------------------------------------------------------
-- NAME: FN_QZ_SIMPLE_TRIGGERS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_simple_triggers (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
REPEAT_COUNT BIGINT(7) NOT NULL,
REPEAT_INTERVAL BIGINT(12) NOT NULL,
TIMES_TRIGGERED BIGINT(10) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES FN_QZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
  
-- ----------------------------------------------------------
-- NAME: FN_QZ_CRON_TRIGGERS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_cron_triggers (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
CRON_EXPRESSION VARCHAR(120) NOT NULL,
TIME_ZONE_ID VARCHAR(80),
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES FN_QZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
  
-- ----------------------------------------------------------
-- NAME: FN_QZ_SIMPROP_TRIGGERS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_simprop_triggers
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 BIGINT NULL,
    LONG_PROP_2 BIGINT NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR(1) NULL,
    BOOL_PROP_2 VARCHAR(1) NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES FN_QZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
  
-- ----------------------------------------------------------
-- NAME: FN_QZ_BLOB_TRIGGERS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_blob_triggers (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
BLOB_DATA BLOB NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
REFERENCES FN_QZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
  
-- ----------------------------------------------------------
-- NAME: FN_QZ_CALENDARS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_calendars (
SCHED_NAME VARCHAR(120) NOT NULL,
CALENDAR_NAME VARCHAR(200) NOT NULL,
CALENDAR BLOB NOT NULL,
PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

-- ----------------------------------------------------------
-- NAME: FN_QZ_PAUSED_TRIGGER_GRPS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_paused_trigger_grps (
SCHED_NAME VARCHAR(120) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

-- ----------------------------------------------------------
-- NAME: FN_QZ_FIRED_TRIGGERS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_fired_triggers (
SCHED_NAME VARCHAR(120) NOT NULL,
ENTRY_ID VARCHAR(95) NOT NULL,
TRIGGER_NAME VARCHAR(200) NOT NULL,
TRIGGER_GROUP VARCHAR(200) NOT NULL,
INSTANCE_NAME VARCHAR(200) NOT NULL,
FIRED_TIME BIGINT(13) NOT NULL,
SCHED_TIME BIGINT(13) NOT NULL,
PRIORITY INTEGER NOT NULL,
STATE VARCHAR(16) NOT NULL,
JOB_NAME VARCHAR(200) NULL,
JOB_GROUP VARCHAR(200) NULL,
IS_NONCONCURRENT VARCHAR(1) NULL,
REQUESTS_RECOVERY VARCHAR(1) NULL,
PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

-- ----------------------------------------------------------
-- NAME: FN_QZ_SCHEDULER_STATE; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_scheduler_state (
SCHED_NAME VARCHAR(120) NOT NULL,
INSTANCE_NAME VARCHAR(200) NOT NULL,
LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
CHECKIN_INTERVAL BIGINT(13) NOT NULL,
PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

-- ----------------------------------------------------------
-- NAME: FN_QZ_LOCKS; TYPE: TABLE
-- ----------------------------------------------------------
create table fn_qz_locks (
SCHED_NAME VARCHAR(120) NOT NULL,
LOCK_NAME VARCHAR(40) NOT NULL,
PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

--
-- name: rcloudinvocation; type: table
--
create table rcloudinvocation (
    id varchar(128) not null primary key,
    created timestamp not null,
    userinfo varchar(2048) not null,
    notebookid varchar(128) not null,
    parameters varchar(2048) default null,
    tokenreaddate timestamp null
);

--
-- name: rcloudnotebook; type: table
--
create table rcloudnotebook (
    notebookname varchar(128) not null primary key,
    notebookid varchar(128) not null
);

--
-- Name: fn_lu_message_location; Type: TABLE
--

CREATE TABLE fn_lu_message_location (
    message_location_id numeric(11,0) NOT NULL,
    message_location_descr character varying(30) NOT NULL
);

-- ------------------ CREATE VIEW SECTION
--
-- NAME: V_URL_ACCESS; TYPE: VIEW
--
CREATE VIEW v_url_access AS
 SELECT DISTINCT M.ACTION AS URL,
    M.FUNCTION_CD
   FROM FN_MENU M
  WHERE (M.ACTION IS NOT NULL)
UNION
 SELECT DISTINCT T.ACTION AS URL,
    T.FUNCTION_CD
   FROM FN_TAB T
  WHERE (T.ACTION IS NOT NULL)
UNION
 SELECT R.RESTRICTED_URL AS URL,
    R.FUNCTION_CD
   FROM FN_RESTRICTED_URL R;
   
-- ------------------ ALTER TABLE ADD CONSTRAINT PRIMARY KEY SECTION
--
-- NAME: CR_FAVORITE_REPORTS_USER_IDREP_ID; TYPE: CONSTRAINT 
--
alter table cr_favorite_reports 
    add constraint cr_favorite_reports_user_idrep_id primary key (user_id, rep_id);
--
-- NAME: CR_FOLDER_FOLDER_ID; TYPE: CONSTRAINT 
--
alter table cr_folder
    add constraint cr_folder_folder_id primary key (folder_id);
--
-- NAME: CR_FOLDER_ACCESS_FOLDER_ACCESS_ID; TYPE: CONSTRAINT 
--
alter table cr_folder_access
    add constraint cr_folder_access_folder_access_id primary key (folder_access_id);
--
-- NAME: CR_HIST_USER_MAP_HIST_IDUSER_ID; TYPE: CONSTRAINT 
--
alter table cr_hist_user_map
    add constraint cr_hist_user_map_hist_iduser_id primary key (hist_id, user_id);
--
-- NAME: CR_LU_FILE_TYPE_LOOKUP_ID; TYPE: CONSTRAINT 
--
alter table cr_lu_file_type
    add constraint cr_lu_file_type_lookup_id primary key (lookup_id);
--
-- NAME: CR_RAPTOR_ACTION_IMG_IMAGE_ID; TYPE: CONSTRAINT 
--
alter table cr_raptor_action_img
    add constraint cr_raptor_action_img_image_id primary key (image_id);
--
-- NAME: CR_RAPTOR_PDF_IMG_IMAGE_ID; TYPE: CONSTRAINT 
--
alter table cr_raptor_pdf_img
    add constraint cr_raptor_pdf_img_image_id primary key (image_id);
--
-- NAME: CR_REMOTE_SCHEMA_INFO_SCHEMA_PREFIX; TYPE: CONSTRAINT 
--
alter table cr_remote_schema_info
    add constraint cr_remote_schema_info_schema_prefix primary key (schema_prefix);
--
-- NAME: CR_REPORT_REP_ID; TYPE: CONSTRAINT 
--
alter table cr_report
    add constraint cr_report_rep_id primary key (rep_id);
--
-- NAME: CR_REPORT_ACCESS_REP_IDORDER_NO; TYPE: CONSTRAINT 
--
alter table cr_report_access
    add constraint cr_report_access_rep_idorder_no primary key (rep_id, order_no);
--
-- NAME: CR_REPORT_EMAIL_SENT_LOG_LOG_ID; TYPE: CONSTRAINT 
--
alter table cr_report_email_sent_log
    add constraint cr_report_email_sent_log_log_id primary key (log_id);
--
-- NAME: CR_REPORT_FILE_HISTORY_HIST_ID; TYPE: CONSTRAINT 
--
alter table cr_report_file_history
    add constraint cr_report_file_history_hist_id primary key (hist_id);
--
-- NAME: CR_REPORT_SCHEDULE_SCHEDULE_ID; TYPE: CONSTRAINT 
--
alter table cr_report_schedule
    add constraint cr_report_schedule_schedule_id primary key (schedule_id);
--
-- NAME: CR_REPORT_SCHEDULE_USERS_SCHEDULE_IDREP_IDUSER_IDORDER_NO; TYPE: CONSTRAINT 
--
alter table cr_report_schedule_users
    add constraint cr_report_schedule_users_schedule_idrep_iduser_idorder_no primary key (schedule_id, rep_id, user_id, order_no);
--
-- NAME: CR_REPORT_TEMPLATE_MAP_REPORT_ID; TYPE: CONSTRAINT 
--
alter table cr_report_template_map
    add constraint cr_report_template_map_report_id primary key (report_id);
--
-- NAME: CR_TABLE_ROLE_TABLE_NAMEROLE_ID; TYPE: CONSTRAINT 
--
alter table cr_table_role
    add constraint cr_table_role_table_namerole_id primary key (table_name, role_id);
--
-- NAME: CR_TABLE_SOURCE_TABLE_NAME; TYPE: CONSTRAINT 
--
alter table cr_table_source
    add constraint cr_table_source_table_name primary key (table_name);
--
-- NAME: FN_AUDIT_ACTION_AUDIT_ACTION_ID; TYPE: CONSTRAINT 
--
alter table fn_audit_action
    add constraint fn_audit_action_audit_action_id primary key (audit_action_id);  
--
-- NAME: FN_CHAT_LOGS_CHAT_LOG_ID; TYPE: CONSTRAINT 
--
alter table fn_chat_logs
    add constraint fn_chat_logs_chat_log_id primary key (chat_log_id);
--
-- NAME: FN_CHAT_ROOM_CHAT_ROOM_ID; TYPE: CONSTRAINT 
--
alter table fn_chat_room
    add constraint fn_chat_room_chat_room_id primary key (chat_room_id);
--
-- NAME: FN_CHAT_USERS_ID; TYPE: CONSTRAINT 
--
alter table fn_chat_users  
    add constraint fn_chat_users_id primary key (id);
--
-- NAME: FN_LU_ALERT_METHOD_ALERT_METHOD_CD; TYPE: CONSTRAINT 
--
alter table fn_lu_alert_method
    add constraint fn_lu_alert_method_alert_method_cd primary key (alert_method_cd);
--
-- NAME: FN_LU_BROADCAST_SITE_BROADCAST_SITE_CD; TYPE: CONSTRAINT 
--
alter table fn_lu_broadcast_site
    add constraint fn_lu_broadcast_site_broadcast_site_cd primary key (broadcast_site_cd);
--
-- NAME: FN_LU_PRIORITY_PRIORITY_ID; TYPE: CONSTRAINT 
--
alter table fn_lu_priority
    add constraint fn_lu_priority_priority_id primary key (priority_id);
--
-- NAME: FN_LU_ROLE_TYPE_ROLE_TYPE_ID; TYPE: CONSTRAINT 
--
alter table fn_lu_role_type
    add constraint fn_lu_role_type_role_type_id primary key (role_type_id);
--
-- NAME: FN_LU_TAB_SET_TAB_SET_CD; TYPE: CONSTRAINT 
--
alter table fn_lu_tab_set
    add constraint fn_lu_tab_set_tab_set_cd primary key (tab_set_cd);
--
-- NAME: FN_LU_TIMEZONE_TIMEZONE_ID; TYPE: CONSTRAINT 
--
alter table fn_lu_timezone
    add constraint fn_lu_timezone_timezone_id primary key (timezone_id);
--
-- NAME: FN_ORG_ORG_ID; TYPE: CONSTRAINT 
--
alter table fn_org
    add constraint fn_org_org_id primary key (org_id);
--
-- NAME: FN_RESTRICTED_URL_RESTRICTED_URLFUNCTION_CD; TYPE: CONSTRAINT 
--
alter table fn_restricted_url
    add constraint fn_restricted_url_restricted_urlfunction_cd primary key (restricted_url, function_cd);
--
-- NAME: FN_ROLE_COMPOSITE_PARENT_ROLE_IDCHILD_ROLE_ID; TYPE: CONSTRAINT 
--
alter table fn_role_composite
    add constraint fn_role_composite_parent_role_idchild_role_id primary key (parent_role_id, child_role_id);
--
-- NAME: FN_ROLE_FUNCTION_ROLE_IDFUNCTION_CD; TYPE: CONSTRAINT 
--
alter table fn_role_function
    add constraint fn_role_function_role_idfunction_cd primary key (role_id, function_cd);
--
-- NAME: FN_TAB_TAB_CD; TYPE: CONSTRAINT 
--
alter table fn_tab
    add constraint fn_tab_tab_cd primary key (tab_cd);
--
-- NAME: FN_TAB_SELECTED_SELECTED_TAB_CDTAB_URI; TYPE: CONSTRAINT 
--
alter table fn_tab_selected
    add constraint fn_tab_selected_selected_tab_cdtab_uri primary key (selected_tab_cd, tab_uri);
--
-- NAME: FN_USER_PSEUDO_ROLE_PSEUDO_ROLE_IDUSER_ID; TYPE: CONSTRAINT 
--
alter table fn_user_pseudo_role
    add constraint fn_user_pseudo_role_pseudo_role_iduser_id primary key (pseudo_role_id, user_id);
--
-- NAME: FN_USER_ROLE_USER_IDROLE_ID; TYPE: CONSTRAINT 
--
alter table fn_user_role
    add constraint fn_user_role_user_idrole_id primary key (user_id, role_id, app_id);
--
-- Name: fn_lu_message_location_MESSAGE_LOCATION_ID; Type: CONSTRAINT
--

ALTER TABLE fn_lu_message_location
    ADD CONSTRAINT fn_lu_message_location_MESSAGE_LOCATION_ID PRIMARY KEY (message_location_id);

-- ------------------ CREATE INDEX SECTION
--
-- NAME: CR_REPORT_CREATE_IDPUBLIC_YNTITLE; TYPE: INDEX 
--
create index cr_report_create_idpublic_yntitle using btree on cr_report (create_id, public_yn, title);
--
-- NAME: CR_TABLE_JOIN_DEST_TABLE_NAME; TYPE: INDEX 
--
create index cr_table_join_dest_table_name using btree on cr_table_join (dest_table_name);
--
-- NAME: CR_TABLE_JOIN_SRC_TABLE_NAME; TYPE: INDEX 
--
create index cr_table_join_src_table_name using btree on cr_table_join (src_table_name);
--
-- NAME: FN_AUDIT_LOG_ACTIVITY_CD; TYPE: INDEX 
--
create index fn_audit_log_activity_cd using btree on fn_audit_log (activity_cd);
--
-- NAME: FN_AUDIT_LOG_USER_ID; TYPE: INDEX 
--
create index fn_audit_log_user_id using btree on fn_audit_log (user_id);
--
-- NAME: FN_MENU_FUNCTION_CD; TYPE: INDEX 
--
create index fn_menu_function_cd using btree on fn_menu (function_cd);
--
-- NAME: FN_ORG_ACCESS_CD; TYPE: INDEX 
--
create index fn_org_access_cd using btree on fn_org (access_cd);
--
-- NAME: FN_ROLE_FUNCTION_FUNCTION_CD; TYPE: INDEX 
--
create index fn_role_function_function_cd using btree on fn_role_function (function_cd);
--
-- NAME: FN_ROLE_FUNCTION_ROLE_ID; TYPE: INDEX 
--
create index fn_role_function_role_id using btree on fn_role_function (role_id);
--
-- NAME: FN_USER_ADDRESS_ID; TYPE: INDEX 
--
create index fn_user_address_id using btree on fn_user (address_id); 
--
-- NAME: FN_USER_ALERT_METHOD_CD; TYPE: INDEX 
--
create index fn_user_alert_method_cd using btree on fn_user (alert_method_cd); 
--
-- NAME: FN_USER_HRID; TYPE: INDEX 
--
create unique index fn_user_hrid using btree on fn_user (hrid); 
--
-- NAME: FN_USER_LOGIN_ID; TYPE: INDEX 
--
create unique index fn_user_login_id using btree on fn_user (login_id); 
--
-- NAME: FN_USER_ORG_ID; TYPE: INDEX 
--
create index fn_user_org_id using btree on fn_user (org_id); 
--
-- NAME: FN_USER_ROLE_ROLE_ID; TYPE: INDEX 
--
create index fn_user_role_role_id using btree on fn_user_role (role_id);
--
-- NAME: FN_USER_ROLE_USER_ID; TYPE: INDEX 
--
create index fn_user_role_user_id using btree on fn_user_role (user_id);
--
-- NAME: FK_FN_USER__REF_178_FN_APP_idx; TYPE: INDEX 
--
create index fk_fn_user__ref_178_fn_app_IDX on fn_user_role (app_id);

-- ----------------------------------------------------------
-- NAME: QUARTZ TYPE: INDEXES
-- ----------------------------------------------------------
create index idx_fn_qz_j_req_recovery on fn_qz_job_details(sched_name,requests_recovery);
create index idx_fn_qz_j_grp on fn_qz_job_details(sched_name,job_group);
create index idx_fn_qz_t_j on fn_qz_triggers(sched_name,job_name,job_group);
create index idx_fn_qz_t_jg on fn_qz_triggers(sched_name,job_group);
create index idx_fn_qz_t_c on fn_qz_triggers(sched_name,calendar_name);
create index idx_fn_qz_t_g on fn_qz_triggers(sched_name,trigger_group);
create index idx_fn_qz_t_state on fn_qz_triggers(sched_name,trigger_state);
create index idx_fn_qz_t_n_state on fn_qz_triggers(sched_name,trigger_name,trigger_group,trigger_state);
create index idx_fn_qz_t_n_g_state on fn_qz_triggers(sched_name,trigger_group,trigger_state);
create index idx_fn_qz_t_next_fire_time on fn_qz_triggers(sched_name,next_fire_time);
create index idx_fn_qz_t_nft_st on fn_qz_triggers(sched_name,trigger_state,next_fire_time);
create index idx_fn_qz_t_nft_misfire on fn_qz_triggers(sched_name,misfire_instr,next_fire_time);
create index idx_fn_qz_t_nft_st_misfire on fn_qz_triggers(sched_name,misfire_instr,next_fire_time,trigger_state);
create index idx_fn_qz_t_nft_st_misfire_grp on fn_qz_triggers(sched_name,misfire_instr,next_fire_time,trigger_group,trigger_state);
create index idx_fn_qz_ft_trig_inst_name on fn_qz_fired_triggers(sched_name,instance_name);
create index idx_fn_qz_ft_inst_job_req_rcvry on fn_qz_fired_triggers(sched_name,instance_name,requests_recovery);
create index idx_fn_qz_ft_j_g on fn_qz_fired_triggers(sched_name,job_name,job_group);
create index idx_fn_qz_ft_jg on fn_qz_fired_triggers(sched_name,job_group);
create index idx_fn_qz_ft_t_g on fn_qz_fired_triggers(sched_name,trigger_name,trigger_group);
create index idx_fn_qz_ft_tg on fn_qz_fired_triggers(sched_name,trigger_group);

-- ------------------ ALTER TABLE ADD CONSTRAINT FOREIGN KEY SECTION
--
-- NAME: FK_FN_AUDIT_REF_205_FN_LU_AC; TYPE: CONSTRAINT 
--
alter table fn_audit_log
	add constraint fk_fn_audit_ref_205_fn_lu_ac foreign key (activity_cd) references fn_lu_activity(activity_cd);
--
-- NAME: FK_FN_ROLE__REF_201_FN_FUNCT; TYPE: CONSTRAINT 
--    
alter table fn_role_function
	add constraint fk_fn_role__ref_201_fn_funct foreign key (function_cd) references fn_function(function_cd);
--
-- NAME: FK_FN_USER__REF_178_FN_APP; TYPE: FK CONSTRAINT
--
alter table fn_user_role
	add constraint fk_fn_user__ref_178_fn_app foreign key (app_id) references fn_app(app_id);
--
-- NAME: FK_CR_REPOR_REF_14707_CR_REPOR; TYPE: FK CONSTRAINT
--
alter table cr_report_schedule
    add constraint fk_cr_repor_ref_14707_cr_repor foreign key (rep_id) references cr_report(rep_id);
--
-- NAME: FK_CR_REPOR_REF_14716_CR_REPOR; TYPE: FK CONSTRAINT
--
alter table cr_report_schedule_users
    add constraint fk_cr_repor_ref_14716_cr_repor foreign key (schedule_id) references cr_report_schedule(schedule_id);
--
-- NAME: FK_CR_REPOR_REF_17645_CR_REPOR; TYPE: FK CONSTRAINT
--
alter table cr_report_log
    add constraint fk_cr_repor_ref_17645_cr_repor foreign key (rep_id) references cr_report(rep_id);
--
-- NAME: FK_CR_REPOR_REF_8550_CR_REPOR; TYPE: FK CONSTRAINT
--
alter table cr_report_access
    add constraint fk_cr_repor_ref_8550_cr_repor foreign key (rep_id) references cr_report(rep_id);
--
-- NAME: FK_CR_REPORT_REP_ID; TYPE: FK CONSTRAINT
--
alter table cr_report_email_sent_log
    add constraint fk_cr_report_rep_id foreign key (rep_id) references cr_report(rep_id);
--
-- NAME: FK_CR_TABLE_REF_311_CR_TAB; TYPE: FK CONSTRAINT
--
alter table cr_table_join
    add constraint fk_cr_table_ref_311_cr_tab foreign key (src_table_name) references cr_table_source(table_name);
--
-- NAME: FK_CR_TABLE_REF_315_CR_TAB; TYPE: FK CONSTRAINT
--
alter table cr_table_join
    add constraint fk_cr_table_ref_315_cr_tab foreign key (dest_table_name) references cr_table_source(table_name);
--
-- NAME: FK_CR_TABLE_REF_32384_CR_TABLE; TYPE: FK CONSTRAINT
--
alter table cr_table_role
    add constraint fk_cr_table_ref_32384_cr_table foreign key (table_name) references cr_table_source(table_name);
--
-- NAME: FK_FN_TAB_FUNCTION_CD; TYPE: FK CONSTRAINT
--
alter table fn_tab
    add constraint fk_fn_tab_function_cd foreign key (function_cd) references fn_function(function_cd);
--
-- NAME: FK_FN_TAB_SELECTED_TAB_CD; TYPE: FK CONSTRAINT
--
alter table fn_tab_selected
    add constraint fk_fn_tab_selected_tab_cd foreign key (selected_tab_cd) references fn_tab(tab_cd);
--
-- NAME: FK_FN_TAB_SET_CD; TYPE: FK CONSTRAINT
--
alter table fn_tab
    add constraint fk_fn_tab_set_cd foreign key (tab_set_cd) references fn_lu_tab_set(tab_set_cd);
--
-- NAME: FK_FN_USER_REF_110_FN_ORG; TYPE: FK CONSTRAINT
-- 
alter table fn_user
    add constraint fk_fn_user_ref_110_fn_org foreign key (org_id) references fn_org(org_id); 
--
-- NAME: FK_FN_USER_REF_123_FN_LU_AL; TYPE: FK CONSTRAINT
--
alter table fn_user
    add constraint fk_fn_user_ref_123_fn_lu_al foreign key (alert_method_cd) references fn_lu_alert_method(alert_method_cd); 
--
-- NAME: FK_FN_USER_REF_197_FN_USER; TYPE: FK CONSTRAINT
--
alter table fn_user  
    add constraint fk_fn_user_ref_197_fn_user foreign key (manager_id) references fn_user(user_id); 
--
-- NAME: FK_FN_USER_REF_198_FN_USER; TYPE: FK CONSTRAINT
--
alter table fn_user  
    add constraint fk_fn_user_ref_198_fn_user foreign key (created_id) references fn_user(user_id); 
--
-- NAME: FK_FN_USER_REF_199_FN_USER; TYPE: FK CONSTRAINT
--
alter table fn_user  
    add constraint fk_fn_user_ref_199_fn_user foreign key (modified_id) references fn_user(user_id);    
--
-- NAME: FK_PARENT_KEY_CR_FOLDER; TYPE: FK CONSTRAINT
--
alter table cr_folder
    add constraint fk_parent_key_cr_folder foreign key (parent_folder_id) references cr_folder(folder_id);
--
-- NAME: FK_PSEUDO_ROLE_PSEUDO_ROLE_ID; TYPE: FK CONSTRAINT
--
alter table fn_user_pseudo_role 
    add constraint fk_pseudo_role_pseudo_role_id foreign key (pseudo_role_id) references fn_role(role_id);
--
-- NAME: FK_PSEUDO_ROLE_USER_ID; TYPE: FK CONSTRAINT
--
alter table fn_user_pseudo_role 
    add constraint fk_pseudo_role_user_id foreign key (user_id) references fn_user(user_id);
--
-- NAME: FK_RESTRICTED_URL_FUNCTION_CD; TYPE: FK CONSTRAINT
--
alter table fn_restricted_url
    add constraint fk_restricted_url_function_cd foreign key (function_cd) references fn_function(function_cd);
--
-- NAME: FK_TIMEZONE; TYPE: FK CONSTRAINT
--
alter table fn_user
    add constraint fk_timezone foreign key (timezone) references fn_lu_timezone(timezone_id); 
--
-- NAME: SYS_C0014614; TYPE: FK CONSTRAINT
--
alter table cr_report_file_history
    add constraint sys_c0014614 foreign key (file_type_id) references cr_lu_file_type(lookup_id);
--
-- NAME: SYS_C0014615; TYPE: FK CONSTRAINT
--
alter table cr_report_file_history
    add constraint sys_c0014615 foreign key (rep_id) references cr_report(rep_id);
--
-- NAME: SYS_C0014616; TYPE: FK CONSTRAINT
--
alter table cr_hist_user_map  
    add constraint sys_c0014616 foreign key (hist_id) references cr_report_file_history(hist_id);
--
-- NAME: SYS_C0014617; TYPE: FK CONSTRAINT
--
alter table cr_hist_user_map  
    add constraint sys_c0014617 foreign key (user_id) references fn_user(user_id);
    
commit;
