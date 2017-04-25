-- ---------------------------------------------------------------------------------------------------------------
-- This script populates tables in the 1707 COMMON version of the ECOMP SDK application database.
-- Additional DML scripts may be required for the AT&T version or the OPEN-SOURCE version!
-- ---------------------------------------------------------------------------------------------------------------

SET FOREIGN_KEY_CHECKS=1; 

USE ecomp_sdk;

-- fn_function
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_process','Process List');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_job','Job Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_job_create','Job Create');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_job_designer','Process in Designer view');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_task','Task Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_task_search','Task Search');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_map','Map Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_sample','Sample Pages Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('login','Login');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_home','Home Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_customer','Customer Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_reports','Reports Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_profile','Profile Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_admin','Admin Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_feedback','Feedback Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_help','Help Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_logout','Logout Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_notes','Notes Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_ajax','Ajax Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_customer_create','Customer Create');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_profile_create','Profile Create');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_profile_import','Profile Import');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_tab','Sample Tab Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('view_reports','View Raptor reports');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_itracker_admin','Itracker Admin/Support menu');

-- fn_lu_activity
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('add_role','add_role');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('remove_role','remove_role');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('add_user_role','add_user_role');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('remove_user_role','remove_user_role');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('add_role_function','add_role_function');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('remove_role_function','remove_role_function');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('add_child_role','add_child_role');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('remove_child_role','remove_child_role');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('mobile_login','Mobile Login');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('mobile_logout','Mobile Logout');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('login','Login');
Insert into fn_lu_activity (ACTIVITY_CD,ACTIVITY) values ('logout','Logout');

-- fn_lu_alert_method
Insert into fn_lu_alert_method (ALERT_METHOD_CD,ALERT_METHOD) values ('PHONE','Phone');
Insert into fn_lu_alert_method (ALERT_METHOD_CD,ALERT_METHOD) values ('FAX','Fax');
Insert into fn_lu_alert_method (ALERT_METHOD_CD,ALERT_METHOD) values ('PAGER','Pager');
Insert into fn_lu_alert_method (ALERT_METHOD_CD,ALERT_METHOD) values ('EMAIL','Email');
Insert into fn_lu_alert_method (ALERT_METHOD_CD,ALERT_METHOD) values ('SMS','SMS');

-- fn_lu_menu_set
Insert into fn_lu_menu_set (MENU_SET_CD,MENU_SET_NAME) values ('APP','Application Menu');

-- fn_lu_priority
Insert into fn_lu_priority (PRIORITY_ID,PRIORITY,ACTIVE_YN,SORT_ORDER) values (10,'Low','Y',10);
Insert into fn_lu_priority (PRIORITY_ID,PRIORITY,ACTIVE_YN,SORT_ORDER) values (20,'Normal','Y',20);
Insert into fn_lu_priority (PRIORITY_ID,PRIORITY,ACTIVE_YN,SORT_ORDER) values (30,'High','Y',30);
Insert into fn_lu_priority (PRIORITY_ID,PRIORITY,ACTIVE_YN,SORT_ORDER) values (40,'Urgent','Y',40);
Insert into fn_lu_priority (PRIORITY_ID,PRIORITY,ACTIVE_YN,SORT_ORDER) values (50,'Fatal','Y',50);

-- fn_lu_tab_set
Insert into fn_lu_tab_set (TAB_SET_CD,TAB_SET_NAME) values ('APP','Application Tabs');

-- fn_lu_timezone
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (10,'US/Eastern','US/Eastern');
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (20,'US/Central','US/Central');
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (30,'US/Mountain','US/Mountain');
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (40,'US/Arizona','America/Phoenix');
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (50,'US/Pacific','US/Pacific');
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (60,'US/Alaska','US/Alaska');
Insert into fn_lu_timezone (TIMEZONE_ID,TIMEZONE_NAME,TIMEZONE_VALUE) values (70,'US/Hawaii','US/Hawaii');

-- fn_restricted_url
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('attachment.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('broadcast.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('file_upload.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('job.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('role.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('role_function.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('test.htm','menu_admin');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('async_test.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('chatWindow.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('contact_list.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('customer_dynamic_list.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('event.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('event_list.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('mobile_welcome.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('sample_map.htm','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('template.jsp','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('jbpm_designer.htm','menu_job_create');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('jbpm_drools.htm','menu_job_create');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('process_job.htm','menu_job_create');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('profile.htm','menu_profile_create');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor.htm','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor2.htm','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor_blob_extract.htm','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor_email_attachment.htm','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor_search.htm','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('report_list.htm','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('gauge.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('gmap_controller.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('gmap_frame.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('map.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('map_download.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('map_grid_search.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('sample_animated_map.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('sample_map_2.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('sample_map_3.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('tab2_sub1.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('tab2_sub2_link1.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('tab2_sub2_link2.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('tab2_sub3.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('tab3.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('tab4.htm','menu_tab');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor.htm','view_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('raptor_blob_extract.htm','view_reports');

-- fn_role
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (16,'Standard User','Y',5);
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (1,'System Administrator','Y',1);

-- fn_role_composite
Insert into fn_role_composite (PARENT_ROLE_ID,CHILD_ROLE_ID) values (1,16);

-- fn_role_function
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'login');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_admin');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_ajax');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_customer');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_customer_create');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_feedback');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_help');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_home');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_job');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_job_create');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_logout');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_notes');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_process');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_profile');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_profile_create');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_profile_import');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_reports');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_sample');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_tab');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'login');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_ajax');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_customer');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_customer_create');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_home');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_logout');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_map');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_profile');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_reports');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_tab');

-- fn_tab
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB2_SUB1_S1','Left Tab 1','Sub - Sub Tab 1 Information','tab2_sub1.htm','menu_tab','Y',10,'TAB2_SUB1','APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB1','Tab 1','Tab 1 Information','tab1.htm','menu_tab','Y',10,null,'APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB2','Tab 2','Tab 2 Information','tab2_sub1.htm','menu_tab','Y',20,null,'APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB3','Tab 3','Tab 3 Information','tab3.htm','menu_tab','Y',30,null,'APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB4','Tab 4','Tab 4 Information','tab4.htm','menu_tab','Y',40,null,'APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB2_SUB1','Sub Tab 1','Sub Tab 1 Information','tab2_sub1.htm','menu_tab','Y',10,'TAB2','APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB2_SUB2','Sub Tab 2','Sub Tab 2 Information','tab2_sub2.htm','menu_tab','Y',20,'TAB2','APP');
Insert into fn_tab (TAB_CD,TAB_NAME,TAB_DESCR,ACTION,FUNCTION_CD,ACTIVE_YN,SORT_ORDER,PARENT_TAB_CD,TAB_SET_CD) values ('TAB2_SUB3','Sub Tab 3','Sub Tab 3 Information','tab2_sub3.htm','menu_tab','Y',30,'TAB2','APP');

-- fn_tab_selected
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB1','tab1');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2','tab2_sub1');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2','tab2_sub2');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2','tab2_sub3');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2_SUB1','tab2_sub1');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2_SUB1_S1','tab2_sub1');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2_SUB2','tab2_sub2');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB2_SUB3','tab2_sub3');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB3','tab3');
Insert into fn_tab_selected (SELECTED_TAB_CD,TAB_URI) values ('TAB4','tab4');

-- DEMO_BAR_CHART
Insert into demo_bar_chart (label, value) values ('A', 29.765957771107); 
Insert into demo_bar_chart (label, value) values ('B', 0); 
Insert into demo_bar_chart (label, value) values ('C', 32.807804682612); 
Insert into demo_bar_chart (label, value) values ('D', 196.45946739256); 
Insert into demo_bar_chart (label, value) values ('E', 0.19434030906893); 
Insert into demo_bar_chart (label, value) values ('F', 98.079782601442); 
Insert into demo_bar_chart (label, value) values ('G', 13.925743130903); 
Insert into demo_bar_chart (label, value) values ('H', 5.1387322875705);

-- DEMO_BAR_CHART_INTER
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('6-Mar-13','%e-%b-%y'), 198, 220);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('5-Mar-13','%e-%b-%y'), 198, 220);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('4-Mar-13','%e-%b-%y'), 238, 235);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('3-Mar-13','%e-%b-%y'), 238, 235);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('2-Mar-13','%e-%b-%y'), 256, 275);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('1-Mar-13','%e-%b-%y'), 239, 260);  
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('28-Feb-13','%e-%b-%y'), 247, 255);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('27-Feb-13','%e-%b-%y'), 252, 265);
Insert into demo_bar_chart_inter (spam_date, num_rpt_sources, num_det_sources) values (STR_TO_DATE('26-Feb-13','%e-%b-%y'), 198, 220);

-- DEMO_LINE_CHART  
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-May-12','%e-%b-%y'),582.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Apr-12','%e-%b-%y'),583.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('27-Apr-12','%e-%b-%y'),603);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Apr-12','%e-%b-%y'),607.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Apr-12','%e-%b-%y'),610);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Apr-12','%e-%b-%y'),560.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Apr-12','%e-%b-%y'),571.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Apr-12','%e-%b-%y'),572.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('19-Apr-12','%e-%b-%y'),587.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Apr-12','%e-%b-%y'),608.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Apr-12','%e-%b-%y'),609.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Apr-12','%e-%b-%y'),580.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Apr-12','%e-%b-%y'),605.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Apr-12','%e-%b-%y'),622.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Apr-12','%e-%b-%y'),626.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Apr-12','%e-%b-%y'),628.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Apr-12','%e-%b-%y'),636.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Apr-12','%e-%b-%y'),633.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Apr-12','%e-%b-%y'),624.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Apr-12','%e-%b-%y'),629.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('2-Apr-12','%e-%b-%y'),618.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Mar-12','%e-%b-%y'),599.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Mar-12','%e-%b-%y'),609.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Mar-12','%e-%b-%y'),617.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Mar-12','%e-%b-%y'),614.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Mar-12','%e-%b-%y'),606.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('23-Mar-12','%e-%b-%y'),596.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Mar-12','%e-%b-%y'),599.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Mar-12','%e-%b-%y'),602.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Mar-12','%e-%b-%y'),605.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Mar-12','%e-%b-%y'),601.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Mar-12','%e-%b-%y'),585.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('15-Mar-12','%e-%b-%y'),585.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Mar-12','%e-%b-%y'),589.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Mar-12','%e-%b-%y'),568.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Mar-12','%e-%b-%y'),552);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Mar-12','%e-%b-%y'),545.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Mar-12','%e-%b-%y'),541.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('7-Mar-12','%e-%b-%y'),530.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Mar-12','%e-%b-%y'),530.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Mar-12','%e-%b-%y'),533.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Mar-12','%e-%b-%y'),545.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Mar-12','%e-%b-%y'),544.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Feb-12','%e-%b-%y'),542.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Feb-12','%e-%b-%y'),535.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Feb-12','%e-%b-%y'),525.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Feb-12','%e-%b-%y'),522.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Feb-12','%e-%b-%y'),516.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Feb-12','%e-%b-%y'),513.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Feb-12','%e-%b-%y'),514.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Feb-12','%e-%b-%y'),502.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Feb-12','%e-%b-%y'),502.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Feb-12','%e-%b-%y'),497.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Feb-12','%e-%b-%y'),509.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Feb-12','%e-%b-%y'),502.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Feb-12','%e-%b-%y'),493.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Feb-12','%e-%b-%y'),493.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Feb-12','%e-%b-%y'),476.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Feb-12','%e-%b-%y'),468.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Feb-12','%e-%b-%y'),463.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Feb-12','%e-%b-%y'),459.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Feb-12','%e-%b-%y'),455.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Feb-12','%e-%b-%y'),456.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Jan-12','%e-%b-%y'),456.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Jan-12','%e-%b-%y'),453.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jan-12','%e-%b-%y'),447.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Jan-12','%e-%b-%y'),444.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Jan-12','%e-%b-%y'),446.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Jan-12','%e-%b-%y'),420.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Jan-12','%e-%b-%y'),427.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Jan-12','%e-%b-%y'),420.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Jan-12','%e-%b-%y'),427.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Jan-12','%e-%b-%y'),429.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jan-12','%e-%b-%y'),424.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('13-Jan-12','%e-%b-%y'),419.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Jan-12','%e-%b-%y'),421.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Jan-12','%e-%b-%y'),422.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Jan-12','%e-%b-%y'),423.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Jan-12','%e-%b-%y'),421.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Jan-12','%e-%b-%y'),422.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Jan-12','%e-%b-%y'),418.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Jan-12','%e-%b-%y'),413.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Jan-12','%e-%b-%y'),411.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Dec-11','%e-%b-%y'),405);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Dec-11','%e-%b-%y'),405.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Dec-11','%e-%b-%y'),402.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('27-Dec-11','%e-%b-%y'),406.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Dec-11','%e-%b-%y'),403.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Dec-11','%e-%b-%y'),398.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Dec-11','%e-%b-%y'),396.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Dec-11','%e-%b-%y'),395.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Dec-11','%e-%b-%y'),382.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Dec-11','%e-%b-%y'),381.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Dec-11','%e-%b-%y'),378.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Dec-11','%e-%b-%y'),380.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Dec-11','%e-%b-%y'),388.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Dec-11','%e-%b-%y'),391.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Dec-11','%e-%b-%y'),393.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Dec-11','%e-%b-%y'),390.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Dec-11','%e-%b-%y'),389.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Dec-11','%e-%b-%y'),390.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Dec-11','%e-%b-%y'),393.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Dec-11','%e-%b-%y'),389.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Dec-11','%e-%b-%y'),387.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('30-Nov-11','%e-%b-%y'),382.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Nov-11','%e-%b-%y'),373.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Nov-11','%e-%b-%y'),376.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Nov-11','%e-%b-%y'),363.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Nov-11','%e-%b-%y'),366.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Nov-11','%e-%b-%y'),376.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Nov-11','%e-%b-%y'),369.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Nov-11','%e-%b-%y'),374.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Nov-11','%e-%b-%y'),377.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Nov-11','%e-%b-%y'),384.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Nov-11','%e-%b-%y'),388.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Nov-11','%e-%b-%y'),379.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Nov-11','%e-%b-%y'),384.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Nov-11','%e-%b-%y'),385.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Nov-11','%e-%b-%y'),395.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Nov-11','%e-%b-%y'),406.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Nov-11','%e-%b-%y'),399.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Nov-11','%e-%b-%y'),400.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('3-Nov-11','%e-%b-%y'),403.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Nov-11','%e-%b-%y'),397.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Nov-11','%e-%b-%y'),396.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Oct-11','%e-%b-%y'),404.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Oct-11','%e-%b-%y'),404.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Oct-11','%e-%b-%y'),404.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('26-Oct-11','%e-%b-%y'),400.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Oct-11','%e-%b-%y'),397.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Oct-11','%e-%b-%y'),405.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Oct-11','%e-%b-%y'),392.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Oct-11','%e-%b-%y'),395.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Oct-11','%e-%b-%y'),398.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('18-Oct-11','%e-%b-%y'),422.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Oct-11','%e-%b-%y'),419.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Oct-11','%e-%b-%y'),422);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Oct-11','%e-%b-%y'),408.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Oct-11','%e-%b-%y'),402.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Oct-11','%e-%b-%y'),400.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Oct-11','%e-%b-%y'),388.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Oct-11','%e-%b-%y'),369.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Oct-11','%e-%b-%y'),377.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Oct-11','%e-%b-%y'),378.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Oct-11','%e-%b-%y'),372.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Oct-11','%e-%b-%y'),374.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('30-Sep-11','%e-%b-%y'),381.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Sep-11','%e-%b-%y'),390.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Sep-11','%e-%b-%y'),397.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Sep-11','%e-%b-%y'),399.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Sep-11','%e-%b-%y'),403.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Sep-11','%e-%b-%y'),404.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('22-Sep-11','%e-%b-%y'),401.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Sep-11','%e-%b-%y'),412.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Sep-11','%e-%b-%y'),413.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Sep-11','%e-%b-%y'),411.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Sep-11','%e-%b-%y'),400.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Sep-11','%e-%b-%y'),392.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('14-Sep-11','%e-%b-%y'),389.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Sep-11','%e-%b-%y'),384.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Sep-11','%e-%b-%y'),379.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Sep-11','%e-%b-%y'),377.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Sep-11','%e-%b-%y'),384.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Sep-11','%e-%b-%y'),383.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('6-Sep-11','%e-%b-%y'),379.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Sep-11','%e-%b-%y'),374.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Sep-11','%e-%b-%y'),381.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Aug-11','%e-%b-%y'),384.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Aug-11','%e-%b-%y'),389.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Aug-11','%e-%b-%y'),389.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('26-Aug-11','%e-%b-%y'),383.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Aug-11','%e-%b-%y'),373.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Aug-11','%e-%b-%y'),376.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Aug-11','%e-%b-%y'),373.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Aug-11','%e-%b-%y'),356.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Aug-11','%e-%b-%y'),356.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('18-Aug-11','%e-%b-%y'),366.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Aug-11','%e-%b-%y'),380.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Aug-11','%e-%b-%y'),380.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Aug-11','%e-%b-%y'),383.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Aug-11','%e-%b-%y'),376.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Aug-11','%e-%b-%y'),373.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Aug-11','%e-%b-%y'),363.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Aug-11','%e-%b-%y'),374.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Aug-11','%e-%b-%y'),353.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Aug-11','%e-%b-%y'),373.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Aug-11','%e-%b-%y'),377.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Aug-11','%e-%b-%y'),392.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('2-Aug-11','%e-%b-%y'),388.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Aug-11','%e-%b-%y'),396.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Jul-11','%e-%b-%y'),390.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jul-11','%e-%b-%y'),391.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Jul-11','%e-%b-%y'),392.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Jul-11','%e-%b-%y'),403.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Jul-11','%e-%b-%y'),398.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jul-11','%e-%b-%y'),393.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Jul-11','%e-%b-%y'),387.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Jul-11','%e-%b-%y'),386.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Jul-11','%e-%b-%y'),376.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Jul-11','%e-%b-%y'),373.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('15-Jul-11','%e-%b-%y'),364.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Jul-11','%e-%b-%y'),357.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Jul-11','%e-%b-%y'),358.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Jul-11','%e-%b-%y'),353.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Jul-11','%e-%b-%y'),354);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Jul-11','%e-%b-%y'),359.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('7-Jul-11','%e-%b-%y'),357.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Jul-11','%e-%b-%y'),351.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Jul-11','%e-%b-%y'),349.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jul-11','%e-%b-%y'),343.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Jun-11','%e-%b-%y'),335.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Jun-11','%e-%b-%y'),334.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Jun-11','%e-%b-%y'),335.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jun-11','%e-%b-%y'),332.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Jun-11','%e-%b-%y'),326.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Jun-11','%e-%b-%y'),331.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Jun-11','%e-%b-%y'),322.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Jun-11','%e-%b-%y'),325.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-Jun-11','%e-%b-%y'),315.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jun-11','%e-%b-%y'),320.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Jun-11','%e-%b-%y'),325.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Jun-11','%e-%b-%y'),326.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Jun-11','%e-%b-%y'),332.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Jun-11','%e-%b-%y'),326.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Jun-11','%e-%b-%y'),325.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jun-11','%e-%b-%y'),331.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Jun-11','%e-%b-%y'),332.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Jun-11','%e-%b-%y'),332.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Jun-11','%e-%b-%y'),338.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Jun-11','%e-%b-%y'),343.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('2-Jun-11','%e-%b-%y'),346.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jun-11','%e-%b-%y'),345.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-May-11','%e-%b-%y'),347.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-May-11','%e-%b-%y'),337.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-May-11','%e-%b-%y'),335);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-May-11','%e-%b-%y'),336.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-May-11','%e-%b-%y'),332.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-May-11','%e-%b-%y'),334.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-May-11','%e-%b-%y'),335.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-May-11','%e-%b-%y'),340.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-May-11','%e-%b-%y'),339.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-May-11','%e-%b-%y'),336.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-May-11','%e-%b-%y'),333.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-May-11','%e-%b-%y'),340.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-May-11','%e-%b-%y'),346.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-May-11','%e-%b-%y'),347.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-May-11','%e-%b-%y'),349.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-May-11','%e-%b-%y'),347.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('6-May-11','%e-%b-%y'),346.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-May-11','%e-%b-%y'),346.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-May-11','%e-%b-%y'),349.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-May-11','%e-%b-%y'),348.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-May-11','%e-%b-%y'),346.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Apr-11','%e-%b-%y'),350.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Apr-11','%e-%b-%y'),346.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Apr-11','%e-%b-%y'),350.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Apr-11','%e-%b-%y'),350.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Apr-11','%e-%b-%y'),353.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Apr-11','%e-%b-%y'),350.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Apr-11','%e-%b-%y'),342.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('19-Apr-11','%e-%b-%y'),337.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Apr-11','%e-%b-%y'),331.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Apr-11','%e-%b-%y'),327.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Apr-11','%e-%b-%y'),332.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Apr-11','%e-%b-%y'),336.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Apr-11','%e-%b-%y'),332.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Apr-11','%e-%b-%y'),330.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Apr-11','%e-%b-%y'),335.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Apr-11','%e-%b-%y'),338.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Apr-11','%e-%b-%y'),338.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Apr-11','%e-%b-%y'),338.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Apr-11','%e-%b-%y'),341.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Apr-11','%e-%b-%y'),344.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Mar-11','%e-%b-%y'),348.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Mar-11','%e-%b-%y'),348.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Mar-11','%e-%b-%y'),350.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Mar-11','%e-%b-%y'),350.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Mar-11','%e-%b-%y'),351.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Mar-11','%e-%b-%y'),344.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Mar-11','%e-%b-%y'),339.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Mar-11','%e-%b-%y'),341.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Mar-11','%e-%b-%y'),339.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Mar-11','%e-%b-%y'),330.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Mar-11','%e-%b-%y'),334.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Mar-11','%e-%b-%y'),330.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Mar-11','%e-%b-%y'),345.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Mar-11','%e-%b-%y'),353.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Mar-11','%e-%b-%y'),351.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Mar-11','%e-%b-%y'),346.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Mar-11','%e-%b-%y'),352.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Mar-11','%e-%b-%y'),355.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Mar-11','%e-%b-%y'),355.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Mar-11','%e-%b-%y'),360);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Mar-11','%e-%b-%y'),359.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Mar-11','%e-%b-%y'),352.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Mar-11','%e-%b-%y'),349.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Feb-11','%e-%b-%y'),353.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Feb-11','%e-%b-%y'),348.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Feb-11','%e-%b-%y'),342.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Feb-11','%e-%b-%y'),342.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Feb-11','%e-%b-%y'),338.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Feb-11','%e-%b-%y'),350.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Feb-11','%e-%b-%y'),358.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Feb-11','%e-%b-%y'),363.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Feb-11','%e-%b-%y'),359.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Feb-11','%e-%b-%y'),359.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Feb-11','%e-%b-%y'),356.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Feb-11','%e-%b-%y'),354.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Feb-11','%e-%b-%y'),358.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Feb-11','%e-%b-%y'),355.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Feb-11','%e-%b-%y'),351.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Feb-11','%e-%b-%y'),346.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Feb-11','%e-%b-%y'),343.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Feb-11','%e-%b-%y'),344.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Feb-11','%e-%b-%y'),345.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Jan-11','%e-%b-%y'),339.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Jan-11','%e-%b-%y'),336.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jan-11','%e-%b-%y'),343.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Jan-11','%e-%b-%y'),343.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Jan-11','%e-%b-%y'),341.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Jan-11','%e-%b-%y'),337.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Jan-11','%e-%b-%y'),326.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Jan-11','%e-%b-%y'),332.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Jan-11','%e-%b-%y'),338.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Jan-11','%e-%b-%y'),340.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Jan-11','%e-%b-%y'),348.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('13-Jan-11','%e-%b-%y'),345.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Jan-11','%e-%b-%y'),344.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Jan-11','%e-%b-%y'),341.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Jan-11','%e-%b-%y'),342.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Jan-11','%e-%b-%y'),336.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Jan-11','%e-%b-%y'),333.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Jan-11','%e-%b-%y'),334);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Jan-11','%e-%b-%y'),331.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Jan-11','%e-%b-%y'),329.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Dec-10','%e-%b-%y'),322.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Dec-10','%e-%b-%y'),323.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Dec-10','%e-%b-%y'),325.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Dec-10','%e-%b-%y'),325.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Dec-10','%e-%b-%y'),324.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Dec-10','%e-%b-%y'),323.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Dec-10','%e-%b-%y'),325.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Dec-10','%e-%b-%y'),324.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Dec-10','%e-%b-%y'),322.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Dec-10','%e-%b-%y'),320.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Dec-10','%e-%b-%y'),321.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Dec-10','%e-%b-%y'),320.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Dec-10','%e-%b-%y'),320.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Dec-10','%e-%b-%y'),321.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Dec-10','%e-%b-%y'),320.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Dec-10','%e-%b-%y'),319.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Dec-10','%e-%b-%y'),321.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Dec-10','%e-%b-%y'),318.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Dec-10','%e-%b-%y'),320.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Dec-10','%e-%b-%y'),317.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Dec-10','%e-%b-%y'),318.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Dec-10','%e-%b-%y'),316.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Nov-10','%e-%b-%y'),311.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Nov-10','%e-%b-%y'),316.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Nov-10','%e-%b-%y'),315);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Nov-10','%e-%b-%y'),314.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Nov-10','%e-%b-%y'),308.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('22-Nov-10','%e-%b-%y'),313.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Nov-10','%e-%b-%y'),306.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Nov-10','%e-%b-%y'),308.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Nov-10','%e-%b-%y'),300.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Nov-10','%e-%b-%y'),301.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Nov-10','%e-%b-%y'),307.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-Nov-10','%e-%b-%y'),308.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Nov-10','%e-%b-%y'),316.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Nov-10','%e-%b-%y'),318.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Nov-10','%e-%b-%y'),316.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Nov-10','%e-%b-%y'),318.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Nov-10','%e-%b-%y'),317.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-Nov-10','%e-%b-%y'),318.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Nov-10','%e-%b-%y'),312.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Nov-10','%e-%b-%y'),309.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Nov-10','%e-%b-%y'),304.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Oct-10','%e-%b-%y'),300.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Oct-10','%e-%b-%y'),305.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('27-Oct-10','%e-%b-%y'),307.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Oct-10','%e-%b-%y'),308.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Oct-10','%e-%b-%y'),308.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Oct-10','%e-%b-%y'),307.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Oct-10','%e-%b-%y'),309.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Oct-10','%e-%b-%y'),310.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('19-Oct-10','%e-%b-%y'),309.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Oct-10','%e-%b-%y'),318);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Oct-10','%e-%b-%y'),314.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Oct-10','%e-%b-%y'),302.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Oct-10','%e-%b-%y'),300.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Oct-10','%e-%b-%y'),298.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Oct-10','%e-%b-%y'),295.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Oct-10','%e-%b-%y'),294.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Oct-10','%e-%b-%y'),289.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Oct-10','%e-%b-%y'),289.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Oct-10','%e-%b-%y'),288.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Oct-10','%e-%b-%y'),278.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Oct-10','%e-%b-%y'),282.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Sep-10','%e-%b-%y'),283.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Sep-10','%e-%b-%y'),287.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Sep-10','%e-%b-%y'),286.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Sep-10','%e-%b-%y'),291.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Sep-10','%e-%b-%y'),292.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('23-Sep-10','%e-%b-%y'),288.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Sep-10','%e-%b-%y'),287.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Sep-10','%e-%b-%y'),283.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Sep-10','%e-%b-%y'),283.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Sep-10','%e-%b-%y'),275.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Sep-10','%e-%b-%y'),276.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('15-Sep-10','%e-%b-%y'),270.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Sep-10','%e-%b-%y'),268.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Sep-10','%e-%b-%y'),267.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Sep-10','%e-%b-%y'),263.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Sep-10','%e-%b-%y'),263.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Sep-10','%e-%b-%y'),262.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('7-Sep-10','%e-%b-%y'),257.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Sep-10','%e-%b-%y'),258.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Sep-10','%e-%b-%y'),258.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Sep-10','%e-%b-%y'),252.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Sep-10','%e-%b-%y'),250.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Aug-10','%e-%b-%y'),243.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('30-Aug-10','%e-%b-%y'),242.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Aug-10','%e-%b-%y'),241.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Aug-10','%e-%b-%y'),240.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Aug-10','%e-%b-%y'),242.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Aug-10','%e-%b-%y'),239.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Aug-10','%e-%b-%y'),245.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-Aug-10','%e-%b-%y'),249.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Aug-10','%e-%b-%y'),249.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Aug-10','%e-%b-%y'),253.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Aug-10','%e-%b-%y'),251.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Aug-10','%e-%b-%y'),247.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Aug-10','%e-%b-%y'),249.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-Aug-10','%e-%b-%y'),251.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Aug-10','%e-%b-%y'),250.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Aug-10','%e-%b-%y'),259.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Aug-10','%e-%b-%y'),261.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Aug-10','%e-%b-%y'),260.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Aug-10','%e-%b-%y'),261.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-Aug-10','%e-%b-%y'),262.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Aug-10','%e-%b-%y'),261.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Aug-10','%e-%b-%y'),261.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Jul-10','%e-%b-%y'),257.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Jul-10','%e-%b-%y'),258.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jul-10','%e-%b-%y'),260.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('27-Jul-10','%e-%b-%y'),264.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Jul-10','%e-%b-%y'),259.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Jul-10','%e-%b-%y'),259.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jul-10','%e-%b-%y'),259.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Jul-10','%e-%b-%y'),254.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Jul-10','%e-%b-%y'),251.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('19-Jul-10','%e-%b-%y'),245.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Jul-10','%e-%b-%y'),249.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Jul-10','%e-%b-%y'),251.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Jul-10','%e-%b-%y'),252.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Jul-10','%e-%b-%y'),251.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Jul-10','%e-%b-%y'),257.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Jul-10','%e-%b-%y'),259.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Jul-10','%e-%b-%y'),258.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Jul-10','%e-%b-%y'),258.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Jul-10','%e-%b-%y'),248.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Jul-10','%e-%b-%y'),246.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Jul-10','%e-%b-%y'),246.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Jul-10','%e-%b-%y'),248.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Jun-10','%e-%b-%y'),251.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Jun-10','%e-%b-%y'),256.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jun-10','%e-%b-%y'),268.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Jun-10','%e-%b-%y'),266.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Jun-10','%e-%b-%y'),269);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('23-Jun-10','%e-%b-%y'),270.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jun-10','%e-%b-%y'),273.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Jun-10','%e-%b-%y'),270.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Jun-10','%e-%b-%y'),274.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Jun-10','%e-%b-%y'),271.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Jun-10','%e-%b-%y'),267.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('15-Jun-10','%e-%b-%y'),259.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Jun-10','%e-%b-%y'),254.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Jun-10','%e-%b-%y'),253.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Jun-10','%e-%b-%y'),250.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Jun-10','%e-%b-%y'),243.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Jun-10','%e-%b-%y'),249.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('7-Jun-10','%e-%b-%y'),250.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Jun-10','%e-%b-%y'),255.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Jun-10','%e-%b-%y'),263.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Jun-10','%e-%b-%y'),263.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Jun-10','%e-%b-%y'),260.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-May-10','%e-%b-%y'),256.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-May-10','%e-%b-%y'),256.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-May-10','%e-%b-%y'),253.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-May-10','%e-%b-%y'),244.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-May-10','%e-%b-%y'),245.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-May-10','%e-%b-%y'),246.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-May-10','%e-%b-%y'),242.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-May-10','%e-%b-%y'),237.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-May-10','%e-%b-%y'),248.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-May-10','%e-%b-%y'),252.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-May-10','%e-%b-%y'),254.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-May-10','%e-%b-%y'),253.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-May-10','%e-%b-%y'),258.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-May-10','%e-%b-%y'),262.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-May-10','%e-%b-%y'),256.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-May-10','%e-%b-%y'),253.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-May-10','%e-%b-%y'),235.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-May-10','%e-%b-%y'),246.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-May-10','%e-%b-%y'),255.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-May-10','%e-%b-%y'),258.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-May-10','%e-%b-%y'),266.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Apr-10','%e-%b-%y'),261.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Apr-10','%e-%b-%y'),268.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Apr-10','%e-%b-%y'),261.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Apr-10','%e-%b-%y'),262.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('26-Apr-10','%e-%b-%y'),269.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Apr-10','%e-%b-%y'),270.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Apr-10','%e-%b-%y'),266.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Apr-10','%e-%b-%y'),259.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Apr-10','%e-%b-%y'),244.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Apr-10','%e-%b-%y'),247.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Apr-10','%e-%b-%y'),247.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Apr-10','%e-%b-%y'),248.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Apr-10','%e-%b-%y'),245.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Apr-10','%e-%b-%y'),242.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Apr-10','%e-%b-%y'),242.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Apr-10','%e-%b-%y'),241.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Apr-10','%e-%b-%y'),239.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Apr-10','%e-%b-%y'),240.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Apr-10','%e-%b-%y'),239.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Apr-10','%e-%b-%y'),238.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Apr-10','%e-%b-%y'),235.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Apr-10','%e-%b-%y'),235.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('31-Mar-10','%e-%b-%y'),235);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Mar-10','%e-%b-%y'),235.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Mar-10','%e-%b-%y'),232.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Mar-10','%e-%b-%y'),230.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Mar-10','%e-%b-%y'),226.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Mar-10','%e-%b-%y'),229.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('23-Mar-10','%e-%b-%y'),228.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Mar-10','%e-%b-%y'),224.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Mar-10','%e-%b-%y'),222.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Mar-10','%e-%b-%y'),224.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Mar-10','%e-%b-%y'),224.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Mar-10','%e-%b-%y'),224.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('15-Mar-10','%e-%b-%y'),223.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Mar-10','%e-%b-%y'),226.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Mar-10','%e-%b-%y'),225.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Mar-10','%e-%b-%y'),224.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Mar-10','%e-%b-%y'),223.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Mar-10','%e-%b-%y'),219.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Mar-10','%e-%b-%y'),218.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Mar-10','%e-%b-%y'),210.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Mar-10','%e-%b-%y'),209.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Mar-10','%e-%b-%y'),208.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Mar-10','%e-%b-%y'),208.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Feb-10','%e-%b-%y'),204.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Feb-10','%e-%b-%y'),202);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Feb-10','%e-%b-%y'),200.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Feb-10','%e-%b-%y'),197.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Feb-10','%e-%b-%y'),200.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Feb-10','%e-%b-%y'),201.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Feb-10','%e-%b-%y'),202.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Feb-10','%e-%b-%y'),202.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Feb-10','%e-%b-%y'),203.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Feb-10','%e-%b-%y'),200.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Feb-10','%e-%b-%y'),200.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Feb-10','%e-%b-%y'),198.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Feb-10','%e-%b-%y'),195.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Feb-10','%e-%b-%y'),196.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Feb-10','%e-%b-%y'),194.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Feb-10','%e-%b-%y'),195.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Feb-10','%e-%b-%y'),192.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Feb-10','%e-%b-%y'),199.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Feb-10','%e-%b-%y'),195.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Feb-10','%e-%b-%y'),194.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Jan-10','%e-%b-%y'),192.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Jan-10','%e-%b-%y'),199.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jan-10','%e-%b-%y'),207.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Jan-10','%e-%b-%y'),205.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Jan-10','%e-%b-%y'),203.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('22-Jan-10','%e-%b-%y'),197.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Jan-10','%e-%b-%y'),208.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Jan-10','%e-%b-%y'),211.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Jan-10','%e-%b-%y'),215.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Jan-10','%e-%b-%y'),205.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Jan-10','%e-%b-%y'),205.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('14-Jan-10','%e-%b-%y'),209.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Jan-10','%e-%b-%y'),210.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Jan-10','%e-%b-%y'),207.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Jan-10','%e-%b-%y'),210.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Jan-10','%e-%b-%y'),211.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Jan-10','%e-%b-%y'),210.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('6-Jan-10','%e-%b-%y'),210.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Jan-10','%e-%b-%y'),214.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Jan-10','%e-%b-%y'),214.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jan-10','%e-%b-%y'),210.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Dec-09','%e-%b-%y'),210.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Dec-09','%e-%b-%y'),211.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-Dec-09','%e-%b-%y'),209.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Dec-09','%e-%b-%y'),211.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Dec-09','%e-%b-%y'),209.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Dec-09','%e-%b-%y'),209.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Dec-09','%e-%b-%y'),202.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Dec-09','%e-%b-%y'),200.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Dec-09','%e-%b-%y'),198.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Dec-09','%e-%b-%y'),195.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Dec-09','%e-%b-%y'),191.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Dec-09','%e-%b-%y'),195.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Dec-09','%e-%b-%y'),194.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Dec-09','%e-%b-%y'),196.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Dec-09','%e-%b-%y'),194.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Dec-09','%e-%b-%y'),196.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Dec-09','%e-%b-%y'),197.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Dec-09','%e-%b-%y'),189.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Dec-09','%e-%b-%y'),188.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Dec-09','%e-%b-%y'),193.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('3-Dec-09','%e-%b-%y'),196.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Dec-09','%e-%b-%y'),196.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Dec-09','%e-%b-%y'),196.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Nov-09','%e-%b-%y'),199.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Nov-09','%e-%b-%y'),200.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Nov-09','%e-%b-%y'),204.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Nov-09','%e-%b-%y'),204.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Nov-09','%e-%b-%y'),204.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Nov-09','%e-%b-%y'),205.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Nov-09','%e-%b-%y'),199.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Nov-09','%e-%b-%y'),200.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Nov-09','%e-%b-%y'),205.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Nov-09','%e-%b-%y'),207);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Nov-09','%e-%b-%y'),206.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Nov-09','%e-%b-%y'),204.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Nov-09','%e-%b-%y'),201.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Nov-09','%e-%b-%y'),203.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Nov-09','%e-%b-%y'),202.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Nov-09','%e-%b-%y'),201.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Nov-09','%e-%b-%y'),194.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Nov-09','%e-%b-%y'),194.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Nov-09','%e-%b-%y'),190.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Nov-09','%e-%b-%y'),188.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Nov-09','%e-%b-%y'),189.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('30-Oct-09','%e-%b-%y'),188.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Oct-09','%e-%b-%y'),196.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Oct-09','%e-%b-%y'),192.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Oct-09','%e-%b-%y'),197.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Oct-09','%e-%b-%y'),202.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Oct-09','%e-%b-%y'),203.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('22-Oct-09','%e-%b-%y'),205.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Oct-09','%e-%b-%y'),204.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Oct-09','%e-%b-%y'),198.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Oct-09','%e-%b-%y'),189.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Oct-09','%e-%b-%y'),188.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Oct-09','%e-%b-%y'),190.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('14-Oct-09','%e-%b-%y'),191.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Oct-09','%e-%b-%y'),190.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Oct-09','%e-%b-%y'),190.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Oct-09','%e-%b-%y'),190.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Oct-09','%e-%b-%y'),189.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Oct-09','%e-%b-%y'),190.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('6-Oct-09','%e-%b-%y'),190.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Oct-09','%e-%b-%y'),186.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Oct-09','%e-%b-%y'),184.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Oct-09','%e-%b-%y'),180.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Sep-09','%e-%b-%y'),185.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Sep-09','%e-%b-%y'),185.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Sep-09','%e-%b-%y'),186.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Sep-09','%e-%b-%y'),182.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Sep-09','%e-%b-%y'),183.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Sep-09','%e-%b-%y'),185.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Sep-09','%e-%b-%y'),184.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Sep-09','%e-%b-%y'),184.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('18-Sep-09','%e-%b-%y'),185.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Sep-09','%e-%b-%y'),184.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Sep-09','%e-%b-%y'),181.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Sep-09','%e-%b-%y'),175.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Sep-09','%e-%b-%y'),173.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Sep-09','%e-%b-%y'),172.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Sep-09','%e-%b-%y'),172.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Sep-09','%e-%b-%y'),171.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Sep-09','%e-%b-%y'),172.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Sep-09','%e-%b-%y'),170.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Sep-09','%e-%b-%y'),166.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Sep-09','%e-%b-%y'),165.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Sep-09','%e-%b-%y'),165.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Aug-09','%e-%b-%y'),168.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Aug-09','%e-%b-%y'),170.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Aug-09','%e-%b-%y'),169.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Aug-09','%e-%b-%y'),167.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Aug-09','%e-%b-%y'),169.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Aug-09','%e-%b-%y'),169.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Aug-09','%e-%b-%y'),169.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Aug-09','%e-%b-%y'),166.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Aug-09','%e-%b-%y'),164.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Aug-09','%e-%b-%y'),164);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Aug-09','%e-%b-%y'),159.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('14-Aug-09','%e-%b-%y'),166.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Aug-09','%e-%b-%y'),168.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Aug-09','%e-%b-%y'),165.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Aug-09','%e-%b-%y'),164.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Aug-09','%e-%b-%y'),165.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Aug-09','%e-%b-%y'),163.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Aug-09','%e-%b-%y'),165.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Aug-09','%e-%b-%y'),165.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Aug-09','%e-%b-%y'),166.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Jul-09','%e-%b-%y'),163.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Jul-09','%e-%b-%y'),162.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Jul-09','%e-%b-%y'),160.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Jul-09','%e-%b-%y'),160);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jul-09','%e-%b-%y'),160.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Jul-09','%e-%b-%y'),159.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Jul-09','%e-%b-%y'),157.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Jul-09','%e-%b-%y'),156.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Jul-09','%e-%b-%y'),151.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-Jul-09','%e-%b-%y'),152.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jul-09','%e-%b-%y'),151.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Jul-09','%e-%b-%y'),147.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Jul-09','%e-%b-%y'),146.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Jul-09','%e-%b-%y'),142.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Jul-09','%e-%b-%y'),142.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Jul-09','%e-%b-%y'),138.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jul-09','%e-%b-%y'),136.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Jul-09','%e-%b-%y'),137.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Jul-09','%e-%b-%y'),135.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Jul-09','%e-%b-%y'),138.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Jul-09','%e-%b-%y'),140.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('2-Jul-09','%e-%b-%y'),140.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jul-09','%e-%b-%y'),142.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Jun-09','%e-%b-%y'),142.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Jun-09','%e-%b-%y'),141.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Jun-09','%e-%b-%y'),142.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Jun-09','%e-%b-%y'),139.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Jun-09','%e-%b-%y'),136.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Jun-09','%e-%b-%y'),134.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Jun-09','%e-%b-%y'),137.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Jun-09','%e-%b-%y'),139.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Jun-09','%e-%b-%y'),135.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jun-09','%e-%b-%y'),135.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Jun-09','%e-%b-%y'),136.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Jun-09','%e-%b-%y'),136.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Jun-09','%e-%b-%y'),136.97);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Jun-09','%e-%b-%y'),139.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Jun-09','%e-%b-%y'),140.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jun-09','%e-%b-%y'),142.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Jun-09','%e-%b-%y'),143.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Jun-09','%e-%b-%y'),144.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Jun-09','%e-%b-%y'),143.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Jun-09','%e-%b-%y'),140.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Jun-09','%e-%b-%y'),139.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jun-09','%e-%b-%y'),139.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-May-09','%e-%b-%y'),135.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-May-09','%e-%b-%y'),135.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-May-09','%e-%b-%y'),133.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-May-09','%e-%b-%y'),130.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-May-09','%e-%b-%y'),122.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-May-09','%e-%b-%y'),124.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-May-09','%e-%b-%y'),125.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-May-09','%e-%b-%y'),127.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-May-09','%e-%b-%y'),126.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-May-09','%e-%b-%y'),122.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-May-09','%e-%b-%y'),122.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-May-09','%e-%b-%y'),119.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-May-09','%e-%b-%y'),124.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-May-09','%e-%b-%y'),129.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-May-09','%e-%b-%y'),129.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-May-09','%e-%b-%y'),129.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-May-09','%e-%b-%y'),132.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-May-09','%e-%b-%y'),132.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-May-09','%e-%b-%y'),132.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-May-09','%e-%b-%y'),127.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Apr-09','%e-%b-%y'),125.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Apr-09','%e-%b-%y'),125.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Apr-09','%e-%b-%y'),123.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Apr-09','%e-%b-%y'),124.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Apr-09','%e-%b-%y'),123.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Apr-09','%e-%b-%y'),125.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Apr-09','%e-%b-%y'),121.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Apr-09','%e-%b-%y'),121.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Apr-09','%e-%b-%y'),120.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Apr-09','%e-%b-%y'),123.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Apr-09','%e-%b-%y'),121.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Apr-09','%e-%b-%y'),117.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Apr-09','%e-%b-%y'),118.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Apr-09','%e-%b-%y'),120.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Apr-09','%e-%b-%y'),119.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Apr-09','%e-%b-%y'),119.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Apr-09','%e-%b-%y'),116.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Apr-09','%e-%b-%y'),115);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Apr-09','%e-%b-%y'),118.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Apr-09','%e-%b-%y'),115.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Apr-09','%e-%b-%y'),112.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Apr-09','%e-%b-%y'),108.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('31-Mar-09','%e-%b-%y'),105.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Mar-09','%e-%b-%y'),104.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Mar-09','%e-%b-%y'),106.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Mar-09','%e-%b-%y'),109.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Mar-09','%e-%b-%y'),106.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Mar-09','%e-%b-%y'),106.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('23-Mar-09','%e-%b-%y'),107.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Mar-09','%e-%b-%y'),101.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Mar-09','%e-%b-%y'),101.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Mar-09','%e-%b-%y'),101.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Mar-09','%e-%b-%y'),99.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Mar-09','%e-%b-%y'),95.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('13-Mar-09','%e-%b-%y'),95.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Mar-09','%e-%b-%y'),96.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Mar-09','%e-%b-%y'),92.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Mar-09','%e-%b-%y'),88.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Mar-09','%e-%b-%y'),83.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Mar-09','%e-%b-%y'),85.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Mar-09','%e-%b-%y'),88.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Mar-09','%e-%b-%y'),91.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Mar-09','%e-%b-%y'),88.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Mar-09','%e-%b-%y'),87.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Feb-09','%e-%b-%y'),89.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Feb-09','%e-%b-%y'),89.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Feb-09','%e-%b-%y'),91.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Feb-09','%e-%b-%y'),90.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Feb-09','%e-%b-%y'),86.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Feb-09','%e-%b-%y'),91.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Feb-09','%e-%b-%y'),90.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Feb-09','%e-%b-%y'),94.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Feb-09','%e-%b-%y'),94.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Feb-09','%e-%b-%y'),99.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Feb-09','%e-%b-%y'),99.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Feb-09','%e-%b-%y'),96.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Feb-09','%e-%b-%y'),97.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Feb-09','%e-%b-%y'),102.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('6-Feb-09','%e-%b-%y'),99.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Feb-09','%e-%b-%y'),96.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Feb-09','%e-%b-%y'),93.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Feb-09','%e-%b-%y'),92.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Feb-09','%e-%b-%y'),91.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Jan-09','%e-%b-%y'),90.13);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-Jan-09','%e-%b-%y'),93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jan-09','%e-%b-%y'),94.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Jan-09','%e-%b-%y'),90.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Jan-09','%e-%b-%y'),89.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Jan-09','%e-%b-%y'),88.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jan-09','%e-%b-%y'),88.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Jan-09','%e-%b-%y'),82.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Jan-09','%e-%b-%y'),78.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Jan-09','%e-%b-%y'),82.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Jan-09','%e-%b-%y'),83.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Jan-09','%e-%b-%y'),85.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Jan-09','%e-%b-%y'),87.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-Jan-09','%e-%b-%y'),88.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jan-09','%e-%b-%y'),90.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Jan-09','%e-%b-%y'),92.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Jan-09','%e-%b-%y'),91.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Jan-09','%e-%b-%y'),93.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Jan-09','%e-%b-%y'),94.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('2-Jan-09','%e-%b-%y'),90.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jan-09','%e-%b-%y'),85.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Dec-08','%e-%b-%y'),85.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Dec-08','%e-%b-%y'),86.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Dec-08','%e-%b-%y'),86.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Dec-08','%e-%b-%y'),85.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Dec-08','%e-%b-%y'),85.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Dec-08','%e-%b-%y'),85.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Dec-08','%e-%b-%y'),86.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Dec-08','%e-%b-%y'),85.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Dec-08','%e-%b-%y'),90);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Dec-08','%e-%b-%y'),89.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Dec-08','%e-%b-%y'),89.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Dec-08','%e-%b-%y'),95.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Dec-08','%e-%b-%y'),94.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Dec-08','%e-%b-%y'),98.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Dec-08','%e-%b-%y'),95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Dec-08','%e-%b-%y'),98.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Dec-08','%e-%b-%y'),100.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Dec-08','%e-%b-%y'),99.72);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Dec-08','%e-%b-%y'),94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Dec-08','%e-%b-%y'),91.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Dec-08','%e-%b-%y'),95.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Dec-08','%e-%b-%y'),92.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-Dec-08','%e-%b-%y'),88.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Nov-08','%e-%b-%y'),92.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Nov-08','%e-%b-%y'),95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Nov-08','%e-%b-%y'),95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Nov-08','%e-%b-%y'),90.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Nov-08','%e-%b-%y'),92.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Nov-08','%e-%b-%y'),82.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Nov-08','%e-%b-%y'),80.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Nov-08','%e-%b-%y'),86.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Nov-08','%e-%b-%y'),89.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Nov-08','%e-%b-%y'),88.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Nov-08','%e-%b-%y'),90.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('13-Nov-08','%e-%b-%y'),96.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Nov-08','%e-%b-%y'),90.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Nov-08','%e-%b-%y'),94.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Nov-08','%e-%b-%y'),95.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Nov-08','%e-%b-%y'),98.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Nov-08','%e-%b-%y'),99.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Nov-08','%e-%b-%y'),103.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Nov-08','%e-%b-%y'),110.99);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Nov-08','%e-%b-%y'),106.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Oct-08','%e-%b-%y'),107.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Oct-08','%e-%b-%y'),111.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Oct-08','%e-%b-%y'),104.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Oct-08','%e-%b-%y'),99.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Oct-08','%e-%b-%y'),92.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Oct-08','%e-%b-%y'),96.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Oct-08','%e-%b-%y'),98.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Oct-08','%e-%b-%y'),96.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Oct-08','%e-%b-%y'),91.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-Oct-08','%e-%b-%y'),98.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Oct-08','%e-%b-%y'),97.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Oct-08','%e-%b-%y'),101.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Oct-08','%e-%b-%y'),97.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Oct-08','%e-%b-%y'),104.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Oct-08','%e-%b-%y'),110.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Oct-08','%e-%b-%y'),96.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Oct-08','%e-%b-%y'),88.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Oct-08','%e-%b-%y'),89.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Oct-08','%e-%b-%y'),89.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Oct-08','%e-%b-%y'),98.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Oct-08','%e-%b-%y'),97.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('2-Oct-08','%e-%b-%y'),100.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Oct-08','%e-%b-%y'),109.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Sep-08','%e-%b-%y'),113.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Sep-08','%e-%b-%y'),105.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Sep-08','%e-%b-%y'),128.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Sep-08','%e-%b-%y'),131.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('24-Sep-08','%e-%b-%y'),128.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Sep-08','%e-%b-%y'),126.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Sep-08','%e-%b-%y'),131.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Sep-08','%e-%b-%y'),140.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Sep-08','%e-%b-%y'),134.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Sep-08','%e-%b-%y'),127.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Sep-08','%e-%b-%y'),139.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Sep-08','%e-%b-%y'),140.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Sep-08','%e-%b-%y'),148.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Sep-08','%e-%b-%y'),152.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Sep-08','%e-%b-%y'),151.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Sep-08','%e-%b-%y'),151.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Sep-08','%e-%b-%y'),157.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Sep-08','%e-%b-%y'),160.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Sep-08','%e-%b-%y'),161.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Sep-08','%e-%b-%y'),166.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Sep-08','%e-%b-%y'),166.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Aug-08','%e-%b-%y'),169.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Aug-08','%e-%b-%y'),173.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Aug-08','%e-%b-%y'),174.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Aug-08','%e-%b-%y'),173.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Aug-08','%e-%b-%y'),172.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-Aug-08','%e-%b-%y'),176.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Aug-08','%e-%b-%y'),174.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-Aug-08','%e-%b-%y'),175.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Aug-08','%e-%b-%y'),173.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Aug-08','%e-%b-%y'),175.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Aug-08','%e-%b-%y'),175.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Aug-08','%e-%b-%y'),179.32);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Aug-08','%e-%b-%y'),179.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-Aug-08','%e-%b-%y'),176.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Aug-08','%e-%b-%y'),173.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Aug-08','%e-%b-%y'),169.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Aug-08','%e-%b-%y'),163.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Aug-08','%e-%b-%y'),164.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Aug-08','%e-%b-%y'),160.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-Aug-08','%e-%b-%y'),153.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Aug-08','%e-%b-%y'),156.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Jul-08','%e-%b-%y'),158.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Jul-08','%e-%b-%y'),159.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Jul-08','%e-%b-%y'),157.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jul-08','%e-%b-%y'),154.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Jul-08','%e-%b-%y'),162.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Jul-08','%e-%b-%y'),159.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Jul-08','%e-%b-%y'),166.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jul-08','%e-%b-%y'),162.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Jul-08','%e-%b-%y'),166.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Jul-08','%e-%b-%y'),165.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Jul-08','%e-%b-%y'),171.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Jul-08','%e-%b-%y'),172.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Jul-08','%e-%b-%y'),169.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Jul-08','%e-%b-%y'),173.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Jul-08','%e-%b-%y'),172.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Jul-08','%e-%b-%y'),176.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-Jul-08','%e-%b-%y'),174.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Jul-08','%e-%b-%y'),179.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Jul-08','%e-%b-%y'),175.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Jul-08','%e-%b-%y'),170.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Jul-08','%e-%b-%y'),168.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Jul-08','%e-%b-%y'),174.68);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('30-Jun-08','%e-%b-%y'),167.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jun-08','%e-%b-%y'),170.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Jun-08','%e-%b-%y'),168.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Jun-08','%e-%b-%y'),177.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Jun-08','%e-%b-%y'),173.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Jun-08','%e-%b-%y'),173.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('20-Jun-08','%e-%b-%y'),175.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Jun-08','%e-%b-%y'),180.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Jun-08','%e-%b-%y'),178.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jun-08','%e-%b-%y'),181.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Jun-08','%e-%b-%y'),176.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Jun-08','%e-%b-%y'),172.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-Jun-08','%e-%b-%y'),173.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Jun-08','%e-%b-%y'),180.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Jun-08','%e-%b-%y'),185.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jun-08','%e-%b-%y'),181.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Jun-08','%e-%b-%y'),185.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Jun-08','%e-%b-%y'),189.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-Jun-08','%e-%b-%y'),185.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Jun-08','%e-%b-%y'),185.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Jun-08','%e-%b-%y'),186.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-May-08','%e-%b-%y'),188.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-May-08','%e-%b-%y'),186.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-May-08','%e-%b-%y'),187.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('27-May-08','%e-%b-%y'),186.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-May-08','%e-%b-%y'),181.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('22-May-08','%e-%b-%y'),177.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-May-08','%e-%b-%y'),178.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-May-08','%e-%b-%y'),185.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-May-08','%e-%b-%y'),183.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-May-08','%e-%b-%y'),187.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-May-08','%e-%b-%y'),189.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-May-08','%e-%b-%y'),186.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-May-08','%e-%b-%y'),189.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-May-08','%e-%b-%y'),188.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-May-08','%e-%b-%y'),183.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-May-08','%e-%b-%y'),185.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-May-08','%e-%b-%y'),182.59);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-May-08','%e-%b-%y'),186.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-May-08','%e-%b-%y'),184.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-May-08','%e-%b-%y'),180.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-May-08','%e-%b-%y'),180);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('30-Apr-08','%e-%b-%y'),173.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-Apr-08','%e-%b-%y'),175.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('28-Apr-08','%e-%b-%y'),172.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Apr-08','%e-%b-%y'),169.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Apr-08','%e-%b-%y'),168.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Apr-08','%e-%b-%y'),162.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('22-Apr-08','%e-%b-%y'),160.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Apr-08','%e-%b-%y'),168.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Apr-08','%e-%b-%y'),161.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Apr-08','%e-%b-%y'),154.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Apr-08','%e-%b-%y'),153.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Apr-08','%e-%b-%y'),148.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('14-Apr-08','%e-%b-%y'),147.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Apr-08','%e-%b-%y'),147.14);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Apr-08','%e-%b-%y'),154.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Apr-08','%e-%b-%y'),151.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Apr-08','%e-%b-%y'),152.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Apr-08','%e-%b-%y'),155.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-Apr-08','%e-%b-%y'),153.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Apr-08','%e-%b-%y'),151.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Apr-08','%e-%b-%y'),147.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Apr-08','%e-%b-%y'),149.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Mar-08','%e-%b-%y'),143.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Mar-08','%e-%b-%y'),143.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('27-Mar-08','%e-%b-%y'),140.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Mar-08','%e-%b-%y'),145.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Mar-08','%e-%b-%y'),140.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Mar-08','%e-%b-%y'),139.53);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Mar-08','%e-%b-%y'),133.27);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Mar-08','%e-%b-%y'),129.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('18-Mar-08','%e-%b-%y'),132.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Mar-08','%e-%b-%y'),126.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Mar-08','%e-%b-%y'),126.61);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Mar-08','%e-%b-%y'),127.94);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Mar-08','%e-%b-%y'),126.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Mar-08','%e-%b-%y'),127.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Mar-08','%e-%b-%y'),119.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Mar-08','%e-%b-%y'),122.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Mar-08','%e-%b-%y'),120.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Mar-08','%e-%b-%y'),124.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Mar-08','%e-%b-%y'),124.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Mar-08','%e-%b-%y'),121.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-Feb-08','%e-%b-%y'),125.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Feb-08','%e-%b-%y'),129.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Feb-08','%e-%b-%y'),122.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Feb-08','%e-%b-%y'),119.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Feb-08','%e-%b-%y'),119.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Feb-08','%e-%b-%y'),119.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Feb-08','%e-%b-%y'),121.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Feb-08','%e-%b-%y'),123.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Feb-08','%e-%b-%y'),122.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Feb-08','%e-%b-%y'),124.63);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Feb-08','%e-%b-%y'),127.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Feb-08','%e-%b-%y'),129.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('12-Feb-08','%e-%b-%y'),124.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Feb-08','%e-%b-%y'),129.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Feb-08','%e-%b-%y'),125.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Feb-08','%e-%b-%y'),121.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Feb-08','%e-%b-%y'),122);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Feb-08','%e-%b-%y'),129.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('4-Feb-08','%e-%b-%y'),131.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Feb-08','%e-%b-%y'),133.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Jan-08','%e-%b-%y'),135.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Jan-08','%e-%b-%y'),132.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Jan-08','%e-%b-%y'),131.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jan-08','%e-%b-%y'),130.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Jan-08','%e-%b-%y'),130.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Jan-08','%e-%b-%y'),135.6);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Jan-08','%e-%b-%y'),139.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jan-08','%e-%b-%y'),155.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('18-Jan-08','%e-%b-%y'),161.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jan-08','%e-%b-%y'),160.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('16-Jan-08','%e-%b-%y'),159.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Jan-08','%e-%b-%y'),169.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('14-Jan-08','%e-%b-%y'),178.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Jan-08','%e-%b-%y'),172.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('10-Jan-08','%e-%b-%y'),178.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jan-08','%e-%b-%y'),179.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('8-Jan-08','%e-%b-%y'),171.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Jan-08','%e-%b-%y'),177.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('4-Jan-08','%e-%b-%y'),180.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('3-Jan-08','%e-%b-%y'),194.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Jan-08','%e-%b-%y'),194.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Dec-07','%e-%b-%y'),198.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('28-Dec-07','%e-%b-%y'),199.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Dec-07','%e-%b-%y'),198.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('26-Dec-07','%e-%b-%y'),198.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Dec-07','%e-%b-%y'),198.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Dec-07','%e-%b-%y'),193.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Dec-07','%e-%b-%y'),187.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('19-Dec-07','%e-%b-%y'),183.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Dec-07','%e-%b-%y'),182.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Dec-07','%e-%b-%y'),184.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Dec-07','%e-%b-%y'),190.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Dec-07','%e-%b-%y'),191.83);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Dec-07','%e-%b-%y'),190.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Dec-07','%e-%b-%y'),188.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Dec-07','%e-%b-%y'),194.21);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Dec-07','%e-%b-%y'),194.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Dec-07','%e-%b-%y'),189.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Dec-07','%e-%b-%y'),185.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Dec-07','%e-%b-%y'),179.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('3-Dec-07','%e-%b-%y'),178.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Nov-07','%e-%b-%y'),182.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('29-Nov-07','%e-%b-%y'),184.29);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Nov-07','%e-%b-%y'),180.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Nov-07','%e-%b-%y'),174.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Nov-07','%e-%b-%y'),172.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('23-Nov-07','%e-%b-%y'),171.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('21-Nov-07','%e-%b-%y'),168.46);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Nov-07','%e-%b-%y'),168.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Nov-07','%e-%b-%y'),163.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Nov-07','%e-%b-%y'),166.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('15-Nov-07','%e-%b-%y'),164.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('14-Nov-07','%e-%b-%y'),166.11);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Nov-07','%e-%b-%y'),169.96);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Nov-07','%e-%b-%y'),153.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Nov-07','%e-%b-%y'),165.37);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('8-Nov-07','%e-%b-%y'),175.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('7-Nov-07','%e-%b-%y'),186.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('6-Nov-07','%e-%b-%y'),191.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Nov-07','%e-%b-%y'),186.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('2-Nov-07','%e-%b-%y'),187.87);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('1-Nov-07','%e-%b-%y'),187.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Oct-07','%e-%b-%y'),189.95);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Oct-07','%e-%b-%y'),187);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-Oct-07','%e-%b-%y'),185.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Oct-07','%e-%b-%y'),184.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Oct-07','%e-%b-%y'),182.78);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Oct-07','%e-%b-%y'),185.93);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Oct-07','%e-%b-%y'),186.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Oct-07','%e-%b-%y'),174.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('19-Oct-07','%e-%b-%y'),170.42);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Oct-07','%e-%b-%y'),173.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Oct-07','%e-%b-%y'),172.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Oct-07','%e-%b-%y'),169.58);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Oct-07','%e-%b-%y'),166.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Oct-07','%e-%b-%y'),167.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('11-Oct-07','%e-%b-%y'),162.23);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Oct-07','%e-%b-%y'),166.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Oct-07','%e-%b-%y'),167.86);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Oct-07','%e-%b-%y'),167.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Oct-07','%e-%b-%y'),161.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Oct-07','%e-%b-%y'),156.24);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('3-Oct-07','%e-%b-%y'),157.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Oct-07','%e-%b-%y'),158.45);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Oct-07','%e-%b-%y'),156.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Sep-07','%e-%b-%y'),153.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Sep-07','%e-%b-%y'),154.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Sep-07','%e-%b-%y'),152.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-Sep-07','%e-%b-%y'),153.18);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Sep-07','%e-%b-%y'),148.28);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-Sep-07','%e-%b-%y'),144.15);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Sep-07','%e-%b-%y'),140.31);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Sep-07','%e-%b-%y'),140.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Sep-07','%e-%b-%y'),140.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-Sep-07','%e-%b-%y'),138.41);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Sep-07','%e-%b-%y'),138.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('13-Sep-07','%e-%b-%y'),137.2);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Sep-07','%e-%b-%y'),136.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Sep-07','%e-%b-%y'),135.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Sep-07','%e-%b-%y'),136.71);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('7-Sep-07','%e-%b-%y'),131.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Sep-07','%e-%b-%y'),135.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('5-Sep-07','%e-%b-%y'),136.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Sep-07','%e-%b-%y'),144.16);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('31-Aug-07','%e-%b-%y'),138.48);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Aug-07','%e-%b-%y'),136.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-Aug-07','%e-%b-%y'),134.08);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Aug-07','%e-%b-%y'),126.82);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Aug-07','%e-%b-%y'),132.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Aug-07','%e-%b-%y'),135.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-Aug-07','%e-%b-%y'),131.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Aug-07','%e-%b-%y'),132.51);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Aug-07','%e-%b-%y'),127.57);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Aug-07','%e-%b-%y'),122.22);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('17-Aug-07','%e-%b-%y'),122.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-Aug-07','%e-%b-%y'),117.05);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Aug-07','%e-%b-%y'),119.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Aug-07','%e-%b-%y'),124.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('13-Aug-07','%e-%b-%y'),127.79);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-Aug-07','%e-%b-%y'),125);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('9-Aug-07','%e-%b-%y'),126.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Aug-07','%e-%b-%y'),134.01);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Aug-07','%e-%b-%y'),135.03);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Aug-07','%e-%b-%y'),135.25);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('3-Aug-07','%e-%b-%y'),131.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Aug-07','%e-%b-%y'),136.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Aug-07','%e-%b-%y'),135);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-Jul-07','%e-%b-%y'),131.76);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-Jul-07','%e-%b-%y'),141.43);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('27-Jul-07','%e-%b-%y'),143.85);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('26-Jul-07','%e-%b-%y'),146);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('25-Jul-07','%e-%b-%y'),137.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('24-Jul-07','%e-%b-%y'),134.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('23-Jul-07','%e-%b-%y'),143.7);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('20-Jul-07','%e-%b-%y'),143.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('19-Jul-07','%e-%b-%y'),140);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('18-Jul-07','%e-%b-%y'),138.12);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('17-Jul-07','%e-%b-%y'),138.91);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('16-Jul-07','%e-%b-%y'),138.1);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('13-Jul-07','%e-%b-%y'),137.73);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('12-Jul-07','%e-%b-%y'),134.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('11-Jul-07','%e-%b-%y'),132.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('10-Jul-07','%e-%b-%y'),132.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('9-Jul-07','%e-%b-%y'),130.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('6-Jul-07','%e-%b-%y'),132.3);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('5-Jul-07','%e-%b-%y'),132.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-Jul-07','%e-%b-%y'),127.17);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-Jul-07','%e-%b-%y'),121.26);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('29-Jun-07','%e-%b-%y'),122.04);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('28-Jun-07','%e-%b-%y'),120.56);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Jun-07','%e-%b-%y'),121.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Jun-07','%e-%b-%y'),119.65);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Jun-07','%e-%b-%y'),122.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-Jun-07','%e-%b-%y'),123);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('21-Jun-07','%e-%b-%y'),123.9);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('20-Jun-07','%e-%b-%y'),121.55);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('19-Jun-07','%e-%b-%y'),123.66);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-Jun-07','%e-%b-%y'),125.09);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-Jun-07','%e-%b-%y'),120.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-Jun-07','%e-%b-%y'),118.75);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('13-Jun-07','%e-%b-%y'),117.5);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('12-Jun-07','%e-%b-%y'),120.38);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-Jun-07','%e-%b-%y'),120.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-Jun-07','%e-%b-%y'),124.49);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-Jun-07','%e-%b-%y'),124.07);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('6-Jun-07','%e-%b-%y'),123.64);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('5-Jun-07','%e-%b-%y'),122.67);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-Jun-07','%e-%b-%y'),121.33);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('1-Jun-07','%e-%b-%y'),118.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('31-May-07','%e-%b-%y'),121.19);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('30-May-07','%e-%b-%y'),118.77);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('29-May-07','%e-%b-%y'),114.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('25-May-07','%e-%b-%y'),113.62);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-May-07','%e-%b-%y'),110.69);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('23-May-07','%e-%b-%y'),112.89);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('22-May-07','%e-%b-%y'),113.54);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('21-May-07','%e-%b-%y'),111.98);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('18-May-07','%e-%b-%y'),110.02);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('17-May-07','%e-%b-%y'),109.44);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('16-May-07','%e-%b-%y'),107.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('15-May-07','%e-%b-%y'),107.52);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('14-May-07','%e-%b-%y'),109.36);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('11-May-07','%e-%b-%y'),108.74);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('10-May-07','%e-%b-%y'),107.34);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('9-May-07','%e-%b-%y'),106.88);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('8-May-07','%e-%b-%y'),105.06);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('7-May-07','%e-%b-%y'),103.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('4-May-07','%e-%b-%y'),100.81);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('3-May-07','%e-%b-%y'),100.4);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('2-May-07','%e-%b-%y'),100.39);
Insert into demo_line_chart (series, log_date, data_value) values ('Series3',STR_TO_DATE('1-May-07','%e-%b-%y'),99.47);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('30-Apr-07','%e-%b-%y'),99.8);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('27-Apr-07','%e-%b-%y'),99.92);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('26-Apr-07','%e-%b-%y'),98.84);
Insert into demo_line_chart (series, log_date, data_value) values ('Series2',STR_TO_DATE('25-Apr-07','%e-%b-%y'),95.35);
Insert into demo_line_chart (series, log_date, data_value) values ('Series1',STR_TO_DATE('24-Apr-07','%e-%b-%y'),93.24);

-- DEMO_PIE_CHART
Insert into demo_pie_chart (legend, data_value) values ('One', 5);
Insert into demo_pie_chart (legend, data_value) values ('Two', 2);
Insert into demo_pie_chart (legend, data_value) values ('Three', 9);
Insert into demo_pie_chart (legend, data_value) values ('Four', 7);
Insert into demo_pie_chart (legend, data_value) values ('Five', 4);
Insert into demo_pie_chart (legend, data_value) values ('Six', 3);
Insert into demo_pie_chart (legend, data_value) values ('Seven', .5);
  
  
-- DEMO_SCATTER_CHART
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (4.1, 'Particulate', 122);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (4.3, 'Particulate', 117);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (5.7, 'Particulate', 112);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (5.4, 'Particulate', 114);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (5.9, 'Particulate', 110);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (5.0, 'Particulate', 114);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (3.6, 'Particulate', 128);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (1.9, 'Particulate', 137);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (7.3, 'Particulate', 104);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (6.9, 'Humidity', 119);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (7.9, 'Humidity', 118);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (9.8, 'Humidity', 103);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (4.9, 'Humidity', 137);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (6.8, 'Humidity', 102);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (4.7, 'Humidity', 89);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (2.7, 'Humidity', 98);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (3.7, 'Humidity', 145);
Insert into demo_scatter_chart (rainfall, key_value, measurements) values (7.4, 'Humidity', 118);  

-- DEMO_SCATTER_PLOT 

-- SET DEFINE OFF;
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -46.5901128883449, -464.477370615131);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -85.0293361247543, -362.252178232471);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 70.9700275365898, 402.214363675566);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -50.1110580054506, -310.108907443154);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 85.043005750476, 813.481841353449);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -14.2356123424179, -134.200903707809);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -66.7014933188071, -445.754374526706);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -79.941582021797, -694.089097548454);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -64.4665101305822, -431.660620986243);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 13.718818366452, 100.010719918027);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 35.457456199233, 223.254643848734);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 55.2326402548387, 268.940835852805);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -32.9989160276248, -237.280626944034);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -0.464911506111831, -2.65656324666862);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -59.0205101710777, -498.895652307826);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 28.0939970575828, 117.200615553207);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 31.7305239061572, 186.662624012256);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 50.9035126419798, 440.937283203403);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 58.3875046571053, 547.879249694999);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 76.7846997917459, 525.020578968308);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 1.73485745801611, 7.28149474936192);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 15.304841061276, 71.2374666595537);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 75.1342455000693, 381.145932349436);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -58.2087417684623, -573.630956069476);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 76.7977837302114, 624.733726327778);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 56.3295585433654, 309.697529902676);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -9.27601440680639, -49.5126219388194);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 98.3002030040236, 765.653589829535);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -25.0987502451517, -174.651201240269);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 39.9215299020147, 337.889176256456);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 77.9225832868337, 356.183903852096);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 75.4311841137638, 14.5258766665983);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 58.9445375968278, 376.359576288564);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', 46.9521897141796, 3.5679984193934);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -49.0378307695689, -230.816092788509);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -96.2461776340861, -863.765255159092);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -96.3388912796447, -538.147283544646);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -21.4684477767032, -200.140077054848);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -30.3532837083366, -226.462637188158);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample0', -17.5055590488884, -118.709622452841);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 56.6394671790491, 385.48951169801);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 70.4622912302344, 356.986529538635);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 58.2647422222769, 489.418744916999);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -92.16093253903, -425.576081634713);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -2.64574970943097, -26.190027661226);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -16.7568654181289, -117.460886096034);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 68.215377945908, 606.917788617984);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 53.7387814434413, 367.53491797949);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -51.8861573715238, -289.998186955562);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -15.9721784074351, -75.335027134323);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 63.2540648905791, 602.546517566905);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -53.376167960458, -470.921238684285);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 40.8307443439851, 276.112653117961);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -13.0540977188468, -127.648158921993);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -43.7358336047599, -435.080470107322);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -74.5673321340732, -617.960236798371);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -6.47907144443936, -55.654651151187);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 68.1915507628225, 352.320728639801);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -23.5393521654339, -137.714557244391);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 28.3621412621467, 141.103859877604);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -99.5615230664525, -974.857161307048);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -77.7132553058204, -736.182131225006);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 95.9321864873013, 478.286112499176);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 97.7451855292708, 940.301427763062);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -10.2483179758141, -70.1145330070458);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -24.0750124187893, -113.523998470537);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -5.5721118558967, -43.8516395203455);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -55.5378338160537, -368.506951528332);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 52.6669516338013, 38.2926120131942);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -74.1981412067658, -436.990411988621);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -8.70511941690364, -71.3277811558721);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 63.2921735621378, 534.825008407329);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 97.9066635843841, 678.994971737474);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -83.1613916743288, -494.53303650568);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 23.4730547863992, 223.031148353333);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 40.6105099506845, 199.937366405274);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 35.4473225526307, 331.61786915261);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 27.2050975460142, 146.277993239147);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', -1.54684302938646, -12.6706471561247);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample1', 45.8386162291745, 401.780882699918);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 43.4241956158593, 228.71488367607);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -23.4517134254585, -165.958577325218);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -98.8954664030229, -829.964553125469);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 48.0527046113198, 451.527720751234);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -77.8912947988124, -416.867729852279);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 23.8325471824168, 206.907438743452);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 83.2787398847467, 814.01250022556);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 49.1572992549647, 451.03037365466);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -11.1351768833872, -57.3863334655361);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 71.1980242104626, 572.745863967841);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 15.0952976022392, 103.30274980367);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 75.0300005037414, 406.581640027236);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -82.0092720309019, -690.340287049552);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 40.2433497232209, 363.579616486762);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 26.8157962678174, 262.150124949525);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -83.9563210001448, -432.739081022174);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -20.6480437627346, -161.330015497217);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 37.1388896882226, 161.352404658606);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 20.2126667486174, 168.833789818416);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 58.3723632769494, 293.206814023827);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -20.2876832456236, -88.0090685884954);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 72.9768050433371, 691.684023528398);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -21.6612128833675, -130.834158714088);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 97.4870524045038, 806.47904449193);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 60.6688063197852, 255.749289305775);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -44.235772358471, -336.262226570567);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -47.39573087854, -321.133647936626);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -24.7522484346097, -204.548308435727);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -42.903238078129, -239.651563752902);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 20.4656734934697, 172.700213789797);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 56.0665747085147, 365.360390019834);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 31.9979219049038, 237.490140339893);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -29.9884426739069, -203.821484170813);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 13.5011085362703, 79.4784314297668);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 41.8402945507358, 297.04934398378);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -84.9323678979223, -345.2331996232);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', -42.2469964847455, -361.468816319656);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 98.1057699772752, 445.181262282444);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 95.5327901766563, 522.663100406047);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample2', 35.0916611161316, 246.796980313209);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 99.3987950082867, 401.97428571655);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -3.0419413965969, -14.5325761725203);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -79.388026451666, -701.817589967372);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -88.451242397524, -668.370526000304);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 34.3131838963851, 254.418322223563);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -61.518208630511, -347.521623572776);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 45.6124480237487, 356.33565541369);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -1.0716036518037, -7.80544934354423);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 31.7371714687412, 283.925868763573);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -76.771029786315, -626.268489584739);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -67.913690110843, -614.736930677921);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 34.9311671860034, 171.384205820777);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 94.3264454603021, 914.267819214392);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 76.7493996267558, 705.649611960615);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -87.795202856922, -749.505178721718);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 2.10914716736019, 15.4036733330536);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -50.456669557937, -318.410608422062);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -94.646644883092, -734.660992935541);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 34.9170862075359, 347.583881438806);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 44.3524585090071, 294.615219199443);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -67.191016143335, -609.956472872497);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -78.826443879164, -369.129912603377);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -50.427554400015, -418.144241602024);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -70.371769526721, -307.02193189609);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 16.3220947890044, 97.0997346831135);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -47.067245718878, -398.27032236792);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 5.91206661301702, 24.7239863780181);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -99.621681801868, -843.593457399484);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 87.4503492670535, 695.345037859433);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -91.176921118057, -683.305064255346);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -0.6632900001386, -5.34157539224209);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 2.98188785882178, 12.1181973600389);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 30.6008700087597, 205.922863867274);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 7.90348761612496, 65.5271597329641);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 40.3565229854156, 268.058138389501);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 37.6892733312091, 247.519083233639);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 16.4759733864001, 107.72661087278);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -53.578492311122, -369.768816039059);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', 77.1292326560541, 765.262108306778);
Insert into demo_scatter_plot
   (SERIES, VALUEX, VALUEY)
 Values
   ('Sample3', -79.566811593352, -677.545127214159);

-- DEMO_UTIL_CHART
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-17','%Y-%m-%d'),53.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-10','%Y-%m-%d'),62.95747);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-21','%Y-%m-%d'),48.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-22','%Y-%m-%d'),51.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-11','%Y-%m-%d'),56.13373);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-23','%Y-%m-%d'),53.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-12','%Y-%m-%d'),57.05287);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-24','%Y-%m-%d'),51.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-13','%Y-%m-%d'),55.78947);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-25','%Y-%m-%d'),54.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-14','%Y-%m-%d'),63.34907);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-26','%Y-%m-%d'),50.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-15','%Y-%m-%d'),52.21327);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-27','%Y-%m-%d'),48.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-16','%Y-%m-%d'),51.32080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-28','%Y-%m-%d'),50.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-17','%Y-%m-%d'),58.35720);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-29','%Y-%m-%d'),51.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-18','%Y-%m-%d'),57.62293);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-04-30','%Y-%m-%d'),67.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-19','%Y-%m-%d'),55.25000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-01','%Y-%m-%d'),59.89393);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-20','%Y-%m-%d'),58.79573);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-02','%Y-%m-%d'),61.20753);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-21','%Y-%m-%d'),54.09720);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-03','%Y-%m-%d'),58.98340);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-22','%Y-%m-%d'),59.95813);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-04','%Y-%m-%d'),59.55873);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-23','%Y-%m-%d'),62.03067);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-05','%Y-%m-%d'),58.73680);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-24','%Y-%m-%d'),61.97620);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-06','%Y-%m-%d'),59.89967);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-25','%Y-%m-%d'),58.00207);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-07','%Y-%m-%d'),60.67973);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-26','%Y-%m-%d'),59.95440);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-08','%Y-%m-%d'),60.85913);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-27','%Y-%m-%d'),55.43747);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-09','%Y-%m-%d'),60.62460);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-28','%Y-%m-%d'),52.53933);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-10','%Y-%m-%d'),59.51887);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-29','%Y-%m-%d'),57.46260);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-11','%Y-%m-%d'),61.57187);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-30','%Y-%m-%d'),60.04787);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-12','%Y-%m-%d'),60.70000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-31','%Y-%m-%d'),58.79480);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-13','%Y-%m-%d'),69.85133);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-01','%Y-%m-%d'),54.40107);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-14','%Y-%m-%d'),68.99620);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-02','%Y-%m-%d'),62.26007);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-15','%Y-%m-%d'),67.64080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-03','%Y-%m-%d'),60.72360);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-16','%Y-%m-%d'),59.71433);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-04','%Y-%m-%d'),60.95847);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-17','%Y-%m-%d'),59.99667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-05','%Y-%m-%d'),59.45920);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-18','%Y-%m-%d'),63.27207);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-06','%Y-%m-%d'),60.58620);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-19','%Y-%m-%d'),60.32080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-07','%Y-%m-%d'),61.94207);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-20','%Y-%m-%d'),57.32907);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-08','%Y-%m-%d'),59.03327);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-21','%Y-%m-%d'),59.76933);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-09','%Y-%m-%d'),62.83087);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-22','%Y-%m-%d'),59.12453);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-10','%Y-%m-%d'),59.36840);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-23','%Y-%m-%d'),57.10167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-11','%Y-%m-%d'),56.11480);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-24','%Y-%m-%d'),58.45820);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-12','%Y-%m-%d'),62.23393);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-25','%Y-%m-%d'),59.45440);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-13','%Y-%m-%d'),59.72313);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-26','%Y-%m-%d'),60.12807);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-14','%Y-%m-%d'),53.37093);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-27','%Y-%m-%d'),59.11760);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-15','%Y-%m-%d'),52.99233);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-28','%Y-%m-%d'),57.32020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-16','%Y-%m-%d'),55.99080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-29','%Y-%m-%d'),59.80360);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-17','%Y-%m-%d'),53.93853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-30','%Y-%m-%d'),66.73280);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-18','%Y-%m-%d'),55.99313);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-05-31','%Y-%m-%d'),58.78673);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-19','%Y-%m-%d'),68.23393);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-01','%Y-%m-%d'),58.82773);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-20','%Y-%m-%d'),61.86213);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-02','%Y-%m-%d'),63.12100);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-21','%Y-%m-%d'),61.20307);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-03','%Y-%m-%d'),59.70467);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-22','%Y-%m-%d'),61.05900);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-04','%Y-%m-%d'),58.85173);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-23','%Y-%m-%d'),58.41040);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-05','%Y-%m-%d'),61.21880);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-24','%Y-%m-%d'),59.15967);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-06','%Y-%m-%d'),58.99920);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-25','%Y-%m-%d'),56.42153);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-07','%Y-%m-%d'),59.94693);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-26','%Y-%m-%d'),60.46580);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-08','%Y-%m-%d'),66.27293);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-27','%Y-%m-%d'),57.44333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-09','%Y-%m-%d'),61.46773);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-28','%Y-%m-%d'),56.88887);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-10','%Y-%m-%d'),59.70467);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-29','%Y-%m-%d'),61.85773);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-11','%Y-%m-%d'),60.16000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-09-30','%Y-%m-%d'),61.96400);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-12','%Y-%m-%d'),61.20300);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-01','%Y-%m-%d'),65.88833);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-13','%Y-%m-%d'),60.95673);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-02','%Y-%m-%d'),62.67920);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-14','%Y-%m-%d'),60.70207);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-03','%Y-%m-%d'),63.52047);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-15','%Y-%m-%d'),61.02520);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-04','%Y-%m-%d'),58.60280);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-16','%Y-%m-%d'),60.33953);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-05','%Y-%m-%d'),63.74487);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-17','%Y-%m-%d'),61.20300);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-06','%Y-%m-%d'),59.94880);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-18','%Y-%m-%d'),63.12100);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-07','%Y-%m-%d'),59.44380);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-23','%Y-%m-%d'),61.09153);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-08','%Y-%m-%d'),59.16320);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-24','%Y-%m-%d'),61.28867);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-09','%Y-%m-%d'),60.84593);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-25','%Y-%m-%d'),60.95673);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-10','%Y-%m-%d'),58.84113);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-26','%Y-%m-%d'),60.61100);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-11','%Y-%m-%d'),62.59827);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-27','%Y-%m-%d'),61.22913);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-12','%Y-%m-%d'),60.94660);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-28','%Y-%m-%d'),58.88507);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-13','%Y-%m-%d'),59.37593);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-29','%Y-%m-%d'),59.73693);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-14','%Y-%m-%d'),67.21840);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-06-30','%Y-%m-%d'),62.45307);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-15','%Y-%m-%d'),68.56020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-01','%Y-%m-%d'),61.30167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-16','%Y-%m-%d'),57.56493);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-02','%Y-%m-%d'),62.92727);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-17','%Y-%m-%d'),57.02280);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-03','%Y-%m-%d'),60.05887);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-18','%Y-%m-%d'),56.20947);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-06','%Y-%m-%d'),61.20100);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-19','%Y-%m-%d'),55.69353);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-07','%Y-%m-%d'),60.66120);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-20','%Y-%m-%d'),57.17640);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-08','%Y-%m-%d'),59.78180);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-21','%Y-%m-%d'),57.50867);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-09','%Y-%m-%d'),58.74653);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-22','%Y-%m-%d'),61.67860);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-10','%Y-%m-%d'),59.77893);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-23','%Y-%m-%d'),57.34867);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-11','%Y-%m-%d'),67.34500);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-24','%Y-%m-%d'),61.68080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-12','%Y-%m-%d'),57.07293);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-25','%Y-%m-%d'),55.55793);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-13','%Y-%m-%d'),57.37567);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-26','%Y-%m-%d'),55.81013);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-14','%Y-%m-%d'),63.97820);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-27','%Y-%m-%d'),59.85540);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-15','%Y-%m-%d'),56.06647);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-28','%Y-%m-%d'),61.05073);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-16','%Y-%m-%d'),53.66347);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-29','%Y-%m-%d'),59.81253);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-23','%Y-%m-%d'),56.50813);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-30','%Y-%m-%d'),61.02047);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-24','%Y-%m-%d'),53.19667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-10-31','%Y-%m-%d'),60.60413);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-25','%Y-%m-%d'),51.57133);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-01','%Y-%m-%d'),57.43067);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-26','%Y-%m-%d'),45.98160);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-02','%Y-%m-%d'),58.63027);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-27','%Y-%m-%d'),49.21113);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-03','%Y-%m-%d'),59.08127);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-28','%Y-%m-%d'),49.67213);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-04','%Y-%m-%d'),59.37373);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-29','%Y-%m-%d'),52.94053);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-05','%Y-%m-%d'),58.13413);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-30','%Y-%m-%d'),57.55727);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-06','%Y-%m-%d'),57.18893);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-07-31','%Y-%m-%d'),61.76900);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-07','%Y-%m-%d'),56.72853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-01','%Y-%m-%d'),56.51953);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-08','%Y-%m-%d'),56.47340);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-02','%Y-%m-%d'),61.04853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-09','%Y-%m-%d'),62.02333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-03','%Y-%m-%d'),70.06067);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-10','%Y-%m-%d'),61.21787);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-04','%Y-%m-%d'),60.97787);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-11','%Y-%m-%d'),62.01087);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-05','%Y-%m-%d'),59.25967);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-12','%Y-%m-%d'),62.67573);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-06','%Y-%m-%d'),56.12287);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-13','%Y-%m-%d'),59.23993);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-07','%Y-%m-%d'),63.99913);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-14','%Y-%m-%d'),67.34973);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-08','%Y-%m-%d'),58.71127);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-15','%Y-%m-%d'),60.93753);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-08-09','%Y-%m-%d'),64.01913);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-16','%Y-%m-%d'),54.52607);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-17','%Y-%m-%d'),57.81127);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-18','%Y-%m-%d'),63.53027);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-19','%Y-%m-%d'),58.13000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-20','%Y-%m-%d'),58.46827);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-21','%Y-%m-%d'),65.27807);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-22','%Y-%m-%d'),53.74513);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-23','%Y-%m-%d'),60.99107);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-24','%Y-%m-%d'),60.45427);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-25','%Y-%m-%d'),56.16847);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-26','%Y-%m-%d'),59.04040);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-27','%Y-%m-%d'),54.62040);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-28','%Y-%m-%d'),56.34687);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-29','%Y-%m-%d'),54.81560);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-11-30','%Y-%m-%d'),60.22753);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-01','%Y-%m-%d'),59.07307);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-02','%Y-%m-%d'),59.73553);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-03','%Y-%m-%d'),68.69447);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-04','%Y-%m-%d'),68.91767);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-05','%Y-%m-%d'),67.86460);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-06','%Y-%m-%d'),64.43120);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-01','%Y-%m-%d'),61.22507);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-07','%Y-%m-%d'),60.67793);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-02','%Y-%m-%d'),63.27533);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-08','%Y-%m-%d'),62.47060);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-03','%Y-%m-%d'),69.88087);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-09','%Y-%m-%d'),58.26053);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-04','%Y-%m-%d'),66.84920);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-10','%Y-%m-%d'),61.03340);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-05','%Y-%m-%d'),61.57367);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-11','%Y-%m-%d'),57.32620);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-06','%Y-%m-%d'),60.52293);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-12','%Y-%m-%d'),63.10353);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-07','%Y-%m-%d'),62.21027);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-13','%Y-%m-%d'),61.73167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-08','%Y-%m-%d'),63.20380);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-14','%Y-%m-%d'),65.31080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-09','%Y-%m-%d'),62.72427);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-15','%Y-%m-%d'),64.67620);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-10','%Y-%m-%d'),61.59373);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-16','%Y-%m-%d'),62.87287);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-11','%Y-%m-%d'),61.21280);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-17','%Y-%m-%d'),60.14680);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-12','%Y-%m-%d'),60.79787);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-18','%Y-%m-%d'),63.01007);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-13','%Y-%m-%d'),60.00080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-19','%Y-%m-%d'),57.82680);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-14','%Y-%m-%d'),60.82333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-20','%Y-%m-%d'),62.59173);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-15','%Y-%m-%d'),59.62020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-21','%Y-%m-%d'),61.65607);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-16','%Y-%m-%d'),60.27420);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-23','%Y-%m-%d'),61.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-17','%Y-%m-%d'),60.06200);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-24','%Y-%m-%d'),59.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-18','%Y-%m-%d'),60.88900);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-25','%Y-%m-%d'),50.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-19','%Y-%m-%d'),59.92547);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-26','%Y-%m-%d'),60.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-20','%Y-%m-%d'),59.99853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-27','%Y-%m-%d'),62.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-21','%Y-%m-%d'),58.65873);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-28','%Y-%m-%d'),61.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-22','%Y-%m-%d'),60.61000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-29','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-23','%Y-%m-%d'),59.92280);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-30','%Y-%m-%d'),61.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-24','%Y-%m-%d'),59.52427);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2014-12-31','%Y-%m-%d'),62.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-25','%Y-%m-%d'),59.44887);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-01','%Y-%m-%d'),60.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-26','%Y-%m-%d'),60.23540);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-02','%Y-%m-%d'),62.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-27','%Y-%m-%d'),61.18333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-03','%Y-%m-%d'),62.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-28','%Y-%m-%d'),60.88133);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-04','%Y-%m-%d'),60.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-29','%Y-%m-%d'),61.74160);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-06','%Y-%m-%d'),62.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-30','%Y-%m-%d'),60.25647);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-08','%Y-%m-%d'),61.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-03-31','%Y-%m-%d'),60.41220);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-09','%Y-%m-%d'),63.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-01','%Y-%m-%d'),59.21053);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-10','%Y-%m-%d'),61.82227);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-02','%Y-%m-%d'),55.04713);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-11','%Y-%m-%d'),61.89553);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-03','%Y-%m-%d'),56.08473);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-12','%Y-%m-%d'),60.90127);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-04','%Y-%m-%d'),64.51107);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-13','%Y-%m-%d'),62.23660);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-05','%Y-%m-%d'),56.75193);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-14','%Y-%m-%d'),61.76947);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-06','%Y-%m-%d'),61.64240);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-15','%Y-%m-%d'),63.08853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-07','%Y-%m-%d'),61.33653);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-16','%Y-%m-%d'),60.12627);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-08','%Y-%m-%d'),69.28867);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-17','%Y-%m-%d'),60.60020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-09','%Y-%m-%d'),60.87507);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-18','%Y-%m-%d'),60.43440);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-10','%Y-%m-%d'),68.08707);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-19','%Y-%m-%d'),65.02820);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-11','%Y-%m-%d'),60.57680);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-20','%Y-%m-%d'),62.95593);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-12','%Y-%m-%d'),61.52467);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-21','%Y-%m-%d'),61.23967);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-13','%Y-%m-%d'),58.06567);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-22','%Y-%m-%d'),62.61853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-14','%Y-%m-%d'),59.80807);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-23','%Y-%m-%d'),63.64227);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-15','%Y-%m-%d'),79.90007);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-24','%Y-%m-%d'),63.42147);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-16','%Y-%m-%d'),54.75020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-25','%Y-%m-%d'),60.82687);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-17','%Y-%m-%d'),65.69500);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-26','%Y-%m-%d'),60.66260);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-18','%Y-%m-%d'),68.56247);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-27','%Y-%m-%d'),60.12767);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-19','%Y-%m-%d'),72.67153);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-28','%Y-%m-%d'),60.51513);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-20','%Y-%m-%d'),71.30720);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-29','%Y-%m-%d'),61.71520);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-21','%Y-%m-%d'),57.75233);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-30','%Y-%m-%d'),60.89553);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-22','%Y-%m-%d'),59.04200);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-01-31','%Y-%m-%d'),63.50540);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-23','%Y-%m-%d'),62.30153);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-01','%Y-%m-%d'),62.37533);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-24','%Y-%m-%d'),67.68287);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-02','%Y-%m-%d'),60.31400);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-25','%Y-%m-%d'),69.21800);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-03','%Y-%m-%d'),63.43920);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-26','%Y-%m-%d'),69.75993);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-04','%Y-%m-%d'),61.20487);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-27','%Y-%m-%d'),64.64113);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-05','%Y-%m-%d'),62.11167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-28','%Y-%m-%d'),60.10053);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-06','%Y-%m-%d'),59.96140);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-29','%Y-%m-%d'),58.67653);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-07','%Y-%m-%d'),62.60727);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-04-30','%Y-%m-%d'),58.29180);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-08','%Y-%m-%d'),61.95493);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-01','%Y-%m-%d'),60.56173);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-09','%Y-%m-%d'),58.89653);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-02','%Y-%m-%d'),57.15840);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-10','%Y-%m-%d'),66.20167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-03','%Y-%m-%d'),54.49167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-11','%Y-%m-%d'),64.76873);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-04','%Y-%m-%d'),61.54087);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-12','%Y-%m-%d'),69.90680);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-05','%Y-%m-%d'),63.86073);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-13','%Y-%m-%d'),68.49253);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-06','%Y-%m-%d'),64.13460);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-14','%Y-%m-%d'),63.19360);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-07','%Y-%m-%d'),65.30087);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-15','%Y-%m-%d'),63.35453);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-08','%Y-%m-%d'),64.46353);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-16','%Y-%m-%d'),59.78020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-09','%Y-%m-%d'),62.81193);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-17','%Y-%m-%d'),60.70760);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-10','%Y-%m-%d'),56.14480);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-18','%Y-%m-%d'),58.05167);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-11','%Y-%m-%d'),61.47853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-19','%Y-%m-%d'),57.12700);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-12','%Y-%m-%d'),63.39287);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-20','%Y-%m-%d'),59.49013);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-13','%Y-%m-%d'),64.30640);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-21','%Y-%m-%d'),59.18607);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-14','%Y-%m-%d'),64.29447);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-22','%Y-%m-%d'),60.94680);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-15','%Y-%m-%d'),65.63307);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-23','%Y-%m-%d'),59.85807);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-16','%Y-%m-%d'),62.32887);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-24','%Y-%m-%d'),60.06767);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-17','%Y-%m-%d'),56.52853);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-25','%Y-%m-%d'),61.60267);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-18','%Y-%m-%d'),70.26520);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-26','%Y-%m-%d'),60.94220);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-19','%Y-%m-%d'),64.38267);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-27','%Y-%m-%d'),59.99040);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-20','%Y-%m-%d'),63.01447);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-02-28','%Y-%m-%d'),63.05567);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-21','%Y-%m-%d'),61.89200);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-22','%Y-%m-%d'),62.06920);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-23','%Y-%m-%d'),68.49253);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-24','%Y-%m-%d'),69.98867);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-25','%Y-%m-%d'),60.26940);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-26','%Y-%m-%d'),62.91493);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-09','%Y-%m-%d'),62.28433);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-27','%Y-%m-%d'),62.36827);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-10','%Y-%m-%d'),64.38787);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-11','%Y-%m-%d'),61.17093);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-28','%Y-%m-%d'),60.33887);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-12','%Y-%m-%d'),57.17713);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-29','%Y-%m-%d'),83.89220);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-13','%Y-%m-%d'),57.69653);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-30','%Y-%m-%d'),60.96747);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-14','%Y-%m-%d'),55.97980);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-05-31','%Y-%m-%d'),57.80627);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-15','%Y-%m-%d'),62.08940);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-01','%Y-%m-%d'),61.60173);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-16','%Y-%m-%d'),83.81047);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-02','%Y-%m-%d'),63.23627);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-17','%Y-%m-%d'),61.32540);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-03','%Y-%m-%d'),58.39987);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-18','%Y-%m-%d'),61.08900);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-04','%Y-%m-%d'),69.51947);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-19','%Y-%m-%d'),67.26267);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-05','%Y-%m-%d'),69.67787);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-20','%Y-%m-%d'),58.71120);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-06','%Y-%m-%d'),62.40513);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-21','%Y-%m-%d'),55.77320);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-07','%Y-%m-%d'),61.01893);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-22','%Y-%m-%d'),59.99420);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-08','%Y-%m-%d'),61.34813);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-23','%Y-%m-%d'),60.83867);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-24','%Y-%m-%d'),59.57020);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-25','%Y-%m-%d'),63.20393);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-17','%Y-%m-%d'),67.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-26','%Y-%m-%d'),58.30480);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-18','%Y-%m-%d'),64.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-27','%Y-%m-%d'),58.79667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-19','%Y-%m-%d'),60.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-28','%Y-%m-%d'),54.04967);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-20','%Y-%m-%d'),57.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-29','%Y-%m-%d'),57.88313);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-21','%Y-%m-%d'),58.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-06-30','%Y-%m-%d'),56.94940);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-22','%Y-%m-%d'),59.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-01','%Y-%m-%d'),65.01080);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-23','%Y-%m-%d'),59.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-02','%Y-%m-%d'),64.97013);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-24','%Y-%m-%d'),59.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-03','%Y-%m-%d'),65.06647);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-25','%Y-%m-%d'),59.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-04','%Y-%m-%d'),64.08287);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-26','%Y-%m-%d'),76.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-05','%Y-%m-%d'),65.40367);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-27','%Y-%m-%d'),59.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-06','%Y-%m-%d'),72.61373);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-28','%Y-%m-%d'),59.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-07','%Y-%m-%d'),72.53120);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-29','%Y-%m-%d'),58.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-08','%Y-%m-%d'),72.54133);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-26','%Y-%m-%d'),58.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-30','%Y-%m-%d'),54.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-01','%Y-%m-%d'),58.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-02','%Y-%m-%d'),59.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-03','%Y-%m-%d'),58.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-04','%Y-%m-%d'),60.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-05','%Y-%m-%d'),101.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-06','%Y-%m-%d'),58.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-07','%Y-%m-%d'),59.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-08','%Y-%m-%d'),56.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-09','%Y-%m-%d'),57.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-10','%Y-%m-%d'),65.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-11','%Y-%m-%d'),56.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-12','%Y-%m-%d'),54.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-13','%Y-%m-%d'),68.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-14','%Y-%m-%d'),69.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-15','%Y-%m-%d'),66.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-16','%Y-%m-%d'),58.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-17','%Y-%m-%d'),60.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-18','%Y-%m-%d'),58.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-19','%Y-%m-%d'),55.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-20','%Y-%m-%d'),55.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-21','%Y-%m-%d'),57.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-22','%Y-%m-%d'),57.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-23','%Y-%m-%d'),58.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-24','%Y-%m-%d'),57.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-25','%Y-%m-%d'),58.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-26','%Y-%m-%d'),61.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-27','%Y-%m-%d'),57.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-28','%Y-%m-%d'),58.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-29','%Y-%m-%d'),59.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-30','%Y-%m-%d'),55.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-10-31','%Y-%m-%d'),53.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-01','%Y-%m-%d'),56.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-02','%Y-%m-%d'),57.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-03','%Y-%m-%d'),55.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-04','%Y-%m-%d'),57.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-05','%Y-%m-%d'),65.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-06','%Y-%m-%d'),55.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-07','%Y-%m-%d'),57.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-08','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-09','%Y-%m-%d'),60.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-10','%Y-%m-%d'),58.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-11','%Y-%m-%d'),58.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-12','%Y-%m-%d'),57.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-13','%Y-%m-%d'),57.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-14','%Y-%m-%d'),55.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-15','%Y-%m-%d'),55.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-16','%Y-%m-%d'),58.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-17','%Y-%m-%d'),58.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-18','%Y-%m-%d'),64.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-19','%Y-%m-%d'),59.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-20','%Y-%m-%d'),57.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-21','%Y-%m-%d'),57.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-22','%Y-%m-%d'),59.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-23','%Y-%m-%d'),56.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-24','%Y-%m-%d'),58.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-25','%Y-%m-%d'),57.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-26','%Y-%m-%d'),57.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-27','%Y-%m-%d'),53.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-28','%Y-%m-%d'),58.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-29','%Y-%m-%d'),56.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-11-30','%Y-%m-%d'),58.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-01','%Y-%m-%d'),60.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-02','%Y-%m-%d'),59.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-03','%Y-%m-%d'),58.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-04','%Y-%m-%d'),77.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-05','%Y-%m-%d'),56.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-06','%Y-%m-%d'),55.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-07','%Y-%m-%d'),57.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-08','%Y-%m-%d'),57.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-09','%Y-%m-%d'),71.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-10','%Y-%m-%d'),62.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-11','%Y-%m-%d'),59.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-12','%Y-%m-%d'),59.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-13','%Y-%m-%d'),62.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-14','%Y-%m-%d'),59.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-15','%Y-%m-%d'),59.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-16','%Y-%m-%d'),62.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-17','%Y-%m-%d'),66.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-18','%Y-%m-%d'),59.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-19','%Y-%m-%d'),60.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-20','%Y-%m-%d'),57.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-21','%Y-%m-%d'),60.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-22','%Y-%m-%d'),59.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-23','%Y-%m-%d'),61.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-24','%Y-%m-%d'),57.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-25','%Y-%m-%d'),52.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-09','%Y-%m-%d'),70.77980);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-10','%Y-%m-%d'),70.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-11','%Y-%m-%d'),60.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-12','%Y-%m-%d'),58.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-13','%Y-%m-%d'),58.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-14','%Y-%m-%d'),58.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-15','%Y-%m-%d'),68.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-16','%Y-%m-%d'),84.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-17','%Y-%m-%d'),61.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-18','%Y-%m-%d'),64.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-19','%Y-%m-%d'),59.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-20','%Y-%m-%d'),77.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-21','%Y-%m-%d'),60.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-22','%Y-%m-%d'),65.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-23','%Y-%m-%d'),68.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-24','%Y-%m-%d'),58.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-25','%Y-%m-%d'),56.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-26','%Y-%m-%d'),58.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-27','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-28','%Y-%m-%d'),59.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-29','%Y-%m-%d'),58.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-30','%Y-%m-%d'),59.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-27','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-07-31','%Y-%m-%d'),57.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-28','%Y-%m-%d'),305.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-01','%Y-%m-%d'),59.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-29','%Y-%m-%d'),62.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-02','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-30','%Y-%m-%d'),60.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-03','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-12-31','%Y-%m-%d'),60.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-04','%Y-%m-%d'),83.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-01','%Y-%m-%d'),55.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-05','%Y-%m-%d'),59.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-02','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-06','%Y-%m-%d'),58.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-03','%Y-%m-%d'),61.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-07','%Y-%m-%d'),59.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-04','%Y-%m-%d'),62.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-08','%Y-%m-%d'),57.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-05','%Y-%m-%d'),61.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-09','%Y-%m-%d'),58.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-06','%Y-%m-%d'),61.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-10','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-07','%Y-%m-%d'),62.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-11','%Y-%m-%d'),58.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-08','%Y-%m-%d'),100.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-12','%Y-%m-%d'),57.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-09','%Y-%m-%d'),62.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-13','%Y-%m-%d'),61.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-10','%Y-%m-%d'),66.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-14','%Y-%m-%d'),58.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-11','%Y-%m-%d'),61.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-15','%Y-%m-%d'),59.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-12','%Y-%m-%d'),60.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-16','%Y-%m-%d'),61.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-13','%Y-%m-%d'),61.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-17','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-14','%Y-%m-%d'),60.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-18','%Y-%m-%d'),59.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-15','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-19','%Y-%m-%d'),59.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-16','%Y-%m-%d'),61.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-20','%Y-%m-%d'),60.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-17','%Y-%m-%d'),70.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-21','%Y-%m-%d'),63.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-18','%Y-%m-%d'),61.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-22','%Y-%m-%d'),58.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-19','%Y-%m-%d'),61.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-23','%Y-%m-%d'),59.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-20','%Y-%m-%d'),61.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-24','%Y-%m-%d'),59.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-21','%Y-%m-%d'),61.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-25','%Y-%m-%d'),63.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-22','%Y-%m-%d'),60.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-26','%Y-%m-%d'),61.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-23','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-27','%Y-%m-%d'),60.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-24','%Y-%m-%d'),60.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-28','%Y-%m-%d'),57.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-25','%Y-%m-%d'),61.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-29','%Y-%m-%d'),59.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-26','%Y-%m-%d'),61.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-30','%Y-%m-%d'),57.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-27','%Y-%m-%d'),61.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-08-31','%Y-%m-%d'),59.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-28','%Y-%m-%d'),61.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-01','%Y-%m-%d'),58.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-29','%Y-%m-%d'),61.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-02','%Y-%m-%d'),61.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-30','%Y-%m-%d'),61.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-03','%Y-%m-%d'),59.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-01-31','%Y-%m-%d'),59.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-04','%Y-%m-%d'),59.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-01','%Y-%m-%d'),60.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-05','%Y-%m-%d'),59.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-02','%Y-%m-%d'),60.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-06','%Y-%m-%d'),61.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-03','%Y-%m-%d'),60.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-07','%Y-%m-%d'),59.20000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-04','%Y-%m-%d'),61.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-08','%Y-%m-%d'),61.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-05','%Y-%m-%d'),60.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-09','%Y-%m-%d'),61.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-06','%Y-%m-%d'),60.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-10','%Y-%m-%d'),60.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-07','%Y-%m-%d'),60.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-11','%Y-%m-%d'),58.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-08','%Y-%m-%d'),61.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-12','%Y-%m-%d'),58.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-09','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-13','%Y-%m-%d'),58.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-10','%Y-%m-%d'),60.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-14','%Y-%m-%d'),60.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-11','%Y-%m-%d'),60.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-15','%Y-%m-%d'),62.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-12','%Y-%m-%d'),59.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2015-09-16','%Y-%m-%d'),65.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-13','%Y-%m-%d'),61.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-14','%Y-%m-%d'),69.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-15','%Y-%m-%d'),59.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-16','%Y-%m-%d'),59.80000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-17','%Y-%m-%d'),60.46667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-18','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-19','%Y-%m-%d'),62.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-20','%Y-%m-%d'),60.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-21','%Y-%m-%d'),59.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-22','%Y-%m-%d'),61.40000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-23','%Y-%m-%d'),60.93333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-24','%Y-%m-%d'),64.13333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-25','%Y-%m-%d'),61.53333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-26','%Y-%m-%d'),59.06667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-27','%Y-%m-%d'),61.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-28','%Y-%m-%d'),60.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-02-29','%Y-%m-%d'),61.00000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-01','%Y-%m-%d'),61.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-02','%Y-%m-%d'),61.66667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-03','%Y-%m-%d'),60.73333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-04','%Y-%m-%d'),61.26667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-05','%Y-%m-%d'),61.60000);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-06','%Y-%m-%d'),61.33333);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-07','%Y-%m-%d'),61.86667);
INSERT INTO demo_util_chart (TRAFFIC_DATE,UTIL_PERC ) VALUES (str_to_date('2016-03-08','%Y-%m-%d'),62.00000);

commit;
