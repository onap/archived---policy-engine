-- ============LICENSE_START=======================================================
-- ONAP Policy Engine
-- ================================================================================
-- Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
-- ================================================================================
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ============LICENSE_END=========================================================

-- ---------------------------------------------------------------------------------------------------------------
-- This is for the 1610 Version of SDK database called onap_sdk for Open Source

-- Note to : Database Admin,  set the MySQL system variable called lower_case_table_names

--		It can be set 3 different ways:
--			command-line options (Cmd-line),
--			options valid in configuration files (Option file), or
--			server system variables (System Var).

-- It needs to be set to 1, then table names are stored in lowercase on disk and comparisons are not case sensitive.

--                          MySql/MariaDB Version compatibility information
-- $ mysql --version
-- mysql  Ver 15.1 Distrib 5.5.35-MariaDB, for Linux (x86_64) using readline 5.1

-- bash-4.2$ mysql --version  – cluster version
-- mysql  Ver 15.1 Distrib 10.1.17-MariaDB, for Linux (x86_64) using readline 5.1

-- All versions newer or older than these DO NOT necessarily mean they are compatible.
-- ------------------------------------------------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `onap_sdk`;

USE `onap_sdk`;
set foreign_key_checks=0;

--
-- Increasing the Database variable timeouts
--

set wait_timeout=2147483;   
set interactive_timeout=2147483;

--
-- Table structure for table `actionbodyentity`
--

drop table if exists `actionbodyentity`;
create table `actionbodyentity` (
`actionbodyid` bigint(20) not null auto_increment,
`actionbody` text,
`actionbodyname` varchar(255) not null,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`deleted` tinyint(1) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`version` int(11) default null,
primary key (`actionbodyid`)
);

--
-- Table structure for table `actionlist`
--

drop table if exists `actionlist`;
create table `actionlist` (
`id` int(11) not null auto_increment,
`actionname` TEXT not null,
`description` TEXT default null,
primary key (`id`)
);

--
-- Table structure for table `actionpolicydict`
--

drop table if exists `actionpolicydict`;
create table `actionpolicydict` (
`id` int(11) not null auto_increment,
`type` varchar(45) not null,
`url` TEXT not null,
`method` varchar(45) not null,
`headers` varchar(1024) not null,
`body` TEXT not null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
`attribute_name` varchar(255) not null,
`description` TEXT default null,
primary key (`id`),
unique key `actionpolicydict_unique` (`attribute_name`)
);

--
-- Table structure for table `addressgroup`
--

drop table if exists `addressgroup`;
create table `addressgroup` (
`id` int(11) not null auto_increment,
`name` TEXT default null,
`description` TEXT default null,
`prefixlist` TEXT default null,
primary key (`id`)
);

--
-- Table structure for table `attribute`
--

drop table if exists `attribute`;
create table `attribute` (
`id` int(11) not null auto_increment,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`description` varchar(2048) default null,
`is_designator` char(1) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`selector_path` varchar(2048) default null,
`xacml_id` varchar(500) not null,
`category` int(11) default null,
`constraint_type` int(11) default null,
`datatype` int(11) default null,
`priority` varchar(45) default null,
`attribute_value` varchar(255) default null,
primary key (`id`)
);

--
-- Table structure for table `attributeassignment`
--

drop table if exists `attributeassignment`;
create table `attributeassignment` (
`id` int(11) not null,
`attribute_id` int(11) default null,
`expression` varchar(5000) not null,
`obadvice_id` int(11) default null,
primary key (`id`),
key `fk_attributeassignment_obadvice_id` (`obadvice_id`),
constraint `fk_attributeassignment_obadvice_id` foreign key (`obadvice_id`) references `obadvice` (`id`)
);

--
-- Table structure for table `backupmonitorentity`
--

drop table if exists `backupmonitorentity`;
create table `backupmonitorentity` (
`id` int(11) not null auto_increment,
`node_name` varchar(45) not null,
`resource_name` varchar(75) not null,
`flag` varchar(45) not null,
`notification_record` longtext,
`last_seen` timestamp not null default current_timestamp on update current_timestamp,
primary key (`id`)
);

--
-- Table structure for table `brmsparamtemplate`
--

drop table if exists `brmsparamtemplate`;
create table `brmsparamtemplate` (
`id` int(11) not null auto_increment,
`param_template_name` varchar(255) not null,
`description` varchar(2048) default null,
`rule` longtext not null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `category`
--

drop table if exists `category`;
create table `category` (
`id` int(11) not null,
`grouping` varchar(64) not null,
`is_standard` char(1) not null,
`short_name` varchar(64) not null,
`xacml_id` varchar(255) not null,
primary key (`id`),
unique key `xacml_id` (`xacml_id`)
);

--
-- Table structure for table `closedloopd2services`
--

drop table if exists `closedloopd2services`;
create table `closedloopd2services` (
`id` int(11) not null auto_increment,
`service_name` varchar(1024) not null,
`description` varchar(1024) default null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `closedloopsite`
--

drop table if exists `closedloopsite`;
create table `closedloopsite` (
`id` int(11) not null auto_increment,
`site_name` varchar(1024) not null,
`description` varchar(1024) default null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `configurationdataentity`
--

drop table if exists `configurationdataentity`;
create table `configurationdataentity` (
`configurationdataid` bigint(20) not null auto_increment,
`configbody` text,
`configtype` varchar(255) not null,
`configurationname` varchar(255) not null,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`deleted` tinyint(1) not null,
`description` varchar(2048) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`version` int(11) default null,
primary key (`configurationdataid`)
);

--
-- Table structure for table `constrainttype`
--

drop table if exists `constrainttype`;
create table `constrainttype` (
`id` int(11) not null,
`constraint_type` varchar(64) not null,
`description` varchar(255) not null,
primary key (`id`)
);

--
-- Table structure for table `constraintvalues`
--

drop table if exists `constraintvalues`;
create table `constraintvalues` (
`id` int(11) not null,
`property` varchar(1000) default null,
`value` varchar(1000) default null,
`attribute_id` int(11) default null,
primary key (`id`),
key `fk_constraintvalues_attribute_id` (`attribute_id`),
constraint `fk_constraintvalues_attribute_id` foreign key (`attribute_id`) references `attribute` (`id`)
);

--
-- Table structure for table `cr_favorite_reports`
--

drop table if exists `cr_favorite_reports`;
create table `cr_favorite_reports` (
`user_id` int(11) not null,
`rep_id` int(11) not null,
primary key (`user_id`,`rep_id`)
);

--
-- Table structure for table `cr_filehist_log`
--

drop table if exists `cr_filehist_log`;
create table `cr_filehist_log` (
`schedule_id` decimal(11,0) not null,
`url` varchar(4000) default null,
`notes` varchar(3500) default null,
`run_time` timestamp not null default current_timestamp on update current_timestamp
);

--
-- Table structure for table `cr_folder`
--

drop table if exists `cr_folder`;
create table `cr_folder` (
`folder_id` int(11) not null,
`folder_name` varchar(50) not null,
`descr` varchar(500) default null,
`create_id` int(11) not null,
`create_date` timestamp not null default current_timestamp on update current_timestamp,
`parent_folder_id` int(11) default null,
`public_yn` varchar(1) not null default 'n',
primary key (`folder_id`),
key `fk_parent_key_cr_folder` (`parent_folder_id`),
constraint `fk_parent_key_cr_folder` foreign key (`parent_folder_id`) references `cr_folder` (`folder_id`)
);

--
-- Table structure for table `cr_folder_access`
--

drop table if exists `cr_folder_access`;
create table `cr_folder_access` (
`folder_access_id` decimal(11,0) not null,
`folder_id` decimal(11,0) not null,
`order_no` decimal(11,0) not null,
`role_id` decimal(11,0) default null,
`user_id` decimal(11,0) default null,
`read_only_yn` varchar(1) not null default 'n',
primary key (`folder_access_id`)
);

--
-- Table structure for table `cr_hist_user_map`
--

drop table if exists `cr_hist_user_map`;
create table `cr_hist_user_map` (
`hist_id` int(11) not null,
`user_id` int(11) not null,
primary key (`hist_id`,`user_id`),
key `sys_c0014617` (`user_id`),
constraint `sys_c0014616` foreign key (`hist_id`) references `cr_report_file_history` (`hist_id`),
constraint `sys_c0014617` foreign key (`user_id`) references `fn_user` (`user_id`)
);

--
-- Table structure for table `cr_lu_file_type`
--

drop table if exists `cr_lu_file_type`;
create table `cr_lu_file_type` (
`lookup_id` decimal(2,0) not null,
`lookup_descr` varchar(255) not null,
`active_yn` char(1) default 'y',
`error_code` decimal(11,0) default null,
primary key (`lookup_id`)
);

--
-- Table structure for table `cr_raptor_action_img`
--

drop table if exists `cr_raptor_action_img`;
create table `cr_raptor_action_img` (
`image_id` varchar(100) not null,
`image_loc` varchar(400) default null,
primary key (`image_id`)
);

--
-- Table structure for table `cr_raptor_pdf_img`
--

drop table if exists `cr_raptor_pdf_img`;
create table `cr_raptor_pdf_img` (
`image_id` varchar(100) not null,
`image_loc` varchar(400) default null,
primary key (`image_id`)
);

--
-- Table structure for table `cr_remote_schema_info`
--

drop table if exists `cr_remote_schema_info`;
create table `cr_remote_schema_info` (
`schema_prefix` varchar(5) not null,
`schema_desc` varchar(75) not null,
`datasource_type` varchar(100) default null,
primary key (`schema_prefix`)
);

--
-- Table structure for table `cr_report`
--

drop table if exists `cr_report`;
create table `cr_report` (
`rep_id` decimal(11,0) not null,
`title` varchar(100) not null,
`descr` varchar(255) default null,
`public_yn` varchar(1) not null default 'n',
`report_xml` text,
`create_id` decimal(11,0) default null,
`create_date` timestamp not null default current_timestamp,
`maint_id` decimal(11,0) default null,
`maint_date` timestamp not null default current_timestamp,
`menu_id` varchar(500) default null,
`menu_approved_yn` varchar(1) not null default 'n',
`owner_id` decimal(11,0) default null,
`folder_id` int(11) default '0',
`dashboard_type_yn` varchar(1) default 'n',
`dashboard_yn` varchar(1) default 'n',
primary key (`rep_id`),
key `cr_report_create_idpublic_yntitle` (`create_id`,`public_yn`,`title`) using btree
);

--
-- Table structure for table `cr_report_access`
--

drop table if exists `cr_report_access`;
create table `cr_report_access` (
`rep_id` decimal(11,0) not null,
`order_no` decimal(11,0) not null,
`role_id` decimal(11,0) default null,
`user_id` decimal(11,0) default null,
`read_only_yn` varchar(1) not null default 'n',
primary key (`rep_id`,`order_no`),
constraint `fk_cr_repor_ref_8550_cr_repor` foreign key (`rep_id`) references `cr_report` (`rep_id`)
);

--
-- Table structure for table `cr_report_dwnld_log`
--

drop table if exists `cr_report_dwnld_log`;
create table `cr_report_dwnld_log` (
`user_id` decimal(11,0) not null,
`rep_id` int(11) not null,
`file_name` varchar(100) not null,
`dwnld_start_time` timestamp not null default current_timestamp,
`record_ready_time` timestamp not null default current_timestamp,
`filter_params` varchar(2000) default null
);

--
-- Table structure for table `cr_report_email_sent_log`
--

drop table if exists `cr_report_email_sent_log`;
create table `cr_report_email_sent_log` (
`log_id` int(11) not null,
`schedule_id` decimal(11,0) default null,
`gen_key` varchar(25) not null,
`rep_id` decimal(11,0) not null,
`user_id` decimal(11,0) default null,
`sent_date` timestamp not null default current_timestamp,
`access_flag` varchar(1) not null default 'y',
`touch_date` timestamp not null default current_timestamp,
primary key (`log_id`),
key `fk_cr_report_rep_id` (`rep_id`),
constraint `fk_cr_report_rep_id` foreign key (`rep_id`) references `cr_report` (`rep_id`)
);

--
-- Table structure for table `cr_report_file_history`
--

drop table if exists `cr_report_file_history`;
create table `cr_report_file_history` (
`hist_id` int(11) not null,
`sched_user_id` decimal(11,0) not null,
`schedule_id` decimal(11,0) not null,
`user_id` decimal(11,0) not null,
`rep_id` decimal(11,0) default null,
`run_date` timestamp not null default current_timestamp on update current_timestamp,
`recurrence` varchar(50) default null,
`file_type_id` decimal(2,0) default null,
`file_name` varchar(80) default null,
`file_blob` blob,
`file_size` decimal(11,0) default null,
`raptor_url` varchar(4000) default null,
`error_yn` char(1) default 'n',
`error_code` decimal(11,0) default null,
`deleted_yn` char(1) default 'n',
`deleted_by` decimal(38,0) default null,
primary key (`hist_id`),
key `sys_c0014614` (`file_type_id`),
key `sys_c0014615` (`rep_id`),
constraint `sys_c0014614` foreign key (`file_type_id`) references `cr_lu_file_type` (`lookup_id`),
constraint `sys_c0014615` foreign key (`rep_id`) references `cr_report` (`rep_id`)
);

--
-- Table structure for table `cr_report_log`
--

drop table if exists `cr_report_log`;
create table `cr_report_log` (
`rep_id` decimal(11,0) not null,
`log_time` timestamp not null default current_timestamp on update current_timestamp,
`user_id` decimal(11,0) not null,
`action` varchar(2000) not null,
`action_value` varchar(50) default null,
`form_fields` varchar(4000) default null,
key `fk_cr_repor_ref_17645_cr_repor` (`rep_id`),
constraint `fk_cr_repor_ref_17645_cr_repor` foreign key (`rep_id`) references `cr_report` (`rep_id`)
);

--
-- Table structure for table `cr_report_schedule`
--

drop table if exists `cr_report_schedule`;
create table `cr_report_schedule` (
`schedule_id` decimal(11,0) not null,
`sched_user_id` decimal(11,0) not null,
`rep_id` decimal(11,0) not null,
`enabled_yn` varchar(1) not null,
`start_date` timestamp not null default current_timestamp,
`end_date` timestamp not null default current_timestamp,
`run_date` timestamp not null default current_timestamp,
`recurrence` varchar(50) default null,
`conditional_yn` varchar(1) not null,
`condition_sql` varchar(4000) default null,
`notify_type` int(11) default '0',
`max_row` int(11) default '1000',
`initial_formfields` varchar(3500) default null,
`processed_formfields` varchar(3500) default null,
`formfields` varchar(3500) default null,
`condition_large_sql` text,
`encrypt_yn` char(1) default 'n',
`attachment_yn` char(1) default 'y',
primary key (`schedule_id`),
key `fk_cr_repor_ref_14707_cr_repor` (`rep_id`),
constraint `fk_cr_repor_ref_14707_cr_repor` foreign key (`rep_id`) references `cr_report` (`rep_id`)
);

--
-- Table structure for table `cr_report_schedule_users`
--

drop table if exists `cr_report_schedule_users`;
create table `cr_report_schedule_users` (
`schedule_id` decimal(11,0) not null,
`rep_id` decimal(11,0) not null,
`user_id` decimal(11,0) not null,
`role_id` decimal(11,0) default null,
`order_no` decimal(11,0) not null,
primary key (`schedule_id`,`rep_id`,`user_id`,`order_no`),
constraint `fk_cr_repor_ref_14716_cr_repor` foreign key (`schedule_id`) references `cr_report_schedule` (`schedule_id`)
);

--
-- Table structure for table `cr_report_template_map`
--

drop table if exists `cr_report_template_map`;
create table `cr_report_template_map` (
`report_id` int(11) not null,
`template_file` varchar(200) default null,
primary key (`report_id`)
);

--
-- Table structure for table `cr_schedule_activity_log`
--

drop table if exists `cr_schedule_activity_log`;
create table `cr_schedule_activity_log` (
`schedule_id` decimal(11,0) not null,
`url` varchar(4000) default null,
`notes` varchar(2000) default null,
`run_time` timestamp not null default current_timestamp on update current_timestamp
);

--
-- Table structure for table `cr_table_join`
--

drop table if exists `cr_table_join`;
create table `cr_table_join` (
`src_table_name` varchar(30) not null,
`dest_table_name` varchar(30) not null,
`join_expr` varchar(500) not null,
key `cr_table_join_dest_table_name` (`dest_table_name`) using btree,
key `cr_table_join_src_table_name` (`src_table_name`) using btree,
constraint `fk_cr_table_ref_311_cr_tab` foreign key (`src_table_name`) references `cr_table_source` (`table_name`),
constraint `fk_cr_table_ref_315_cr_tab` foreign key (`dest_table_name`) references `cr_table_source` (`table_name`)
);

--
-- Table structure for table `cr_table_role`
--

drop table if exists `cr_table_role`;
create table `cr_table_role` (
`table_name` varchar(30) not null,
`role_id` decimal(11,0) not null,
primary key (`table_name`,`role_id`),
constraint `fk_cr_table_ref_32384_cr_table` foreign key (`table_name`) references `cr_table_source` (`table_name`)
);

--
-- Table structure for table `cr_table_source`
--

drop table if exists `cr_table_source`;
create table `cr_table_source` (
`table_name` varchar(30) not null,
`display_name` varchar(30) not null,
`pk_fields` varchar(200) default null,
`web_view_action` varchar(50) default null,
`large_data_source_yn` varchar(1) not null default 'n',
`filter_sql` varchar(4000) default null,
`source_db` varchar(50) default null,
primary key (`table_name`)
);

--
-- Table structure for table `databaselockentity`
--

drop table if exists `databaselockentity`;
create table `databaselockentity` (
`lock_key` int(11) not null,
primary key (`lock_key`)
);

--
-- Table structure for table `datatype`
--

drop table if exists `datatype`;
create table `datatype` (
`id` int(11) not null,
`is_standard` char(1) not null,
`short_name` varchar(64) not null,
`xacml_id` varchar(255) not null,
primary key (`id`),
unique key `xacml_id` (`xacml_id`)
);

--
-- Table structure for table `dcaeusers`
--

drop table if exists `dcaeusers`;
create table `dcaeusers` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `dcaeuuid`
--

drop table if exists `dcaeuuid`;
create table `dcaeuuid` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `decisionsettings`
--

drop table if exists `decisionsettings`;
create table `decisionsettings` (
`id` int(11) not null auto_increment,
`xacml_id` varchar(1024) not null,
`description` varchar(1024) default null,
`datatype` varchar(45) not null,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default current_timestamp on update current_timestamp,
`modified_by` varchar(45) not null,
`priority` varchar(45) default null,
`created_date` timestamp not null default current_timestamp,
primary key (`id`)
);

--
-- Table structure for table `demo_bar_chart`
--

drop table if exists `demo_bar_chart`;
create table `demo_bar_chart` (
`label` varchar(20) default null,
`value` decimal(25,15) default null
);

--
-- Table structure for table `demo_bar_chart_inter`
--

drop table if exists `demo_bar_chart_inter`;
create table `demo_bar_chart_inter` (
`spam_date` date default null,
`num_rpt_sources` decimal(10,0) default null,
`num_det_sources` decimal(10,0) default null
);

--
-- Table structure for table `demo_line_chart`
--

drop table if exists `demo_line_chart`;
create table `demo_line_chart` (
`series` varchar(20) default null,
`log_date` date default null,
`data_value` decimal(10,5) default null
);

--
-- Table structure for table `demo_pie_chart`
--

drop table if exists `demo_pie_chart`;
create table `demo_pie_chart` (
`legend` varchar(20) default null,
`data_value` decimal(10,5) default null
);

--
-- Table structure for table `demo_scatter_chart`
--

drop table if exists `demo_scatter_chart`;
create table `demo_scatter_chart` (
`rainfall` decimal(10,2) default null,
`key_value` varchar(20) default null,
`measurements` decimal(10,2) default null
);

--
-- Table structure for table `demo_scatter_plot`
--

drop table if exists `demo_scatter_plot`;
create table `demo_scatter_plot` (
`series` varchar(20) default null,
`valuex` decimal(25,15) default null,
`valuey` decimal(25,15) default null
);

--
-- Table structure for table `demo_util_chart`
--

drop table if exists `demo_util_chart`;
create table `demo_util_chart` (
`traffic_date` date default null,
`util_perc` decimal(10,5) default null
);

--
-- Table structure for table `descriptivescope`
--

drop table if exists `descriptivescope`;
create table `descriptivescope` (
`id` int(11) not null auto_increment,
`scopename` varchar(1024) not null,
`description` varchar(1024) default null,
`search` varchar(1024) not null,
`created_date` timestamp not null default current_timestamp on update current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `onapname`
--

drop table if exists `onapname`;
create table `onapname` (
`id` int(11) not null auto_increment,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`description` varchar(2048) default null,
`onap_name` varchar(767) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
primary key (`id`),
unique key `onap_name` (`onap_name`)
);

--
-- Table structure for table `enforcingtype`
--

drop table if exists `enforcingtype`;
create table `enforcingtype` (
`id` int(11) not null auto_increment,
`enforcingtype` varchar(1024) not null,
`script` varchar(5000) not null,
`connectionquery` varchar(1000) not null,
`valuequery` varchar(1000) not null,
primary key (`id`)
);

--
-- Table structure for table `fn_app`
--

drop table if exists `fn_app`;
create table `fn_app` (
`app_id` int(11) not null auto_increment,
`app_name` varchar(100) not null default '?',
`app_image_url` varchar(256) default null,
`app_description` varchar(512) default null,
`app_notes` varchar(4096) default null,
`app_url` varchar(256) default null,
`app_alternate_url` varchar(256) default null,
`app_rest_endpoint` varchar(2000) default null,
`ml_app_name` varchar(50) not null default '?',
`ml_app_admin_id` varchar(7) not null default '?',
`mots_id` int(11) default null,
`app_password` varchar(256) not null default '?',
`open` char(1) default 'n',
`enabled` char(1) default 'y',
`thumbnail` mediumblob,
`app_username` varchar(50) default null,
`ueb_key` varchar(256) default null,
`ueb_secret` varchar(256) default null,
`ueb_topic_name` varchar(256) default null,
primary key (`app_id`)
);

--
-- Table structure for table `fn_audit_action`
--

drop table if exists `fn_audit_action`;
create table `fn_audit_action` (
`audit_action_id` int(11) not null,
`class_name` varchar(500) not null,
`method_name` varchar(50) not null,
`audit_action_cd` varchar(20) not null,
`audit_action_desc` varchar(200) default null,
`active_yn` varchar(1) default null,
primary key (`audit_action_id`)
);

--
-- Table structure for table `fn_audit_action_log`
--

drop table if exists `fn_audit_action_log`;
create table `fn_audit_action_log` (
`audit_log_id` int(11) not null auto_increment,
`audit_action_cd` varchar(200) default null,
`action_time` timestamp not null default current_timestamp on update current_timestamp,
`user_id` decimal(11,0) default null,
`class_name` varchar(100) default null,
`method_name` varchar(50) default null,
`success_msg` varchar(20) default null,
`error_msg` varchar(500) default null,
primary key (`audit_log_id`)
);

--
-- Table structure for table `fn_audit_log`
--

drop table if exists `fn_audit_log`;
create table `fn_audit_log` (
`log_id` int(11) not null auto_increment,
`user_id` int(11) not null,
`activity_cd` varchar(50) not null,
`audit_date` timestamp not null default current_timestamp,
`comments` varchar(1000) default null,
`affected_record_id_bk` varchar(500) default null,
`affected_record_id` varchar(4000) default null,
primary key (`log_id`),
key `fn_audit_log_activity_cd` (`activity_cd`) using btree,
key `fn_audit_log_user_id` (`user_id`) using btree,
constraint `fk_fn_audit_ref_209_fn_user` foreign key (`user_id`) references `fn_user` (`user_id`),
constraint `fk_fn_audit_ref_205_fn_lu_ac` foreign key (`activity_cd`) references `fn_lu_activity` (`activity_cd`)
);

--
-- Table structure for table `fn_broadcast_message`
--

drop table if exists `fn_broadcast_message`;
create table `fn_broadcast_message` (
`message_id` int(11) not null auto_increment,
`message_text` varchar(1000) not null,
`message_location_id` decimal(11,0) not null,
`broadcast_start_date` timestamp not null default current_timestamp,
`broadcast_end_date` timestamp not null default current_timestamp,
`active_yn` char(1) not null default 'y',
`sort_order` decimal(4,0) not null,
`broadcast_site_cd` varchar(50) default null,
primary key (`message_id`)
);

--
-- Table structure for table `fn_chat_logs`
--

drop table if exists `fn_chat_logs`;
create table `fn_chat_logs` (
`chat_log_id` int(11) not null,
`chat_room_id` int(11) default null,
`user_id` int(11) default null,
`message` varchar(1000) default null,
`message_date_time` timestamp not null default current_timestamp on update current_timestamp,
primary key (`chat_log_id`)
);

--
-- Table structure for table `fn_chat_room`
--

drop table if exists `fn_chat_room`;
create table `fn_chat_room` (
`chat_room_id` int(11) not null,
`name` varchar(50) not null,
`description` varchar(500) default null,
`owner_id` int(11) default null,
`created_date` timestamp not null default current_timestamp,
`updated_date` timestamp not null default current_timestamp,
primary key (`chat_room_id`)
);

--
-- Table structure for table `fn_chat_users`
--

drop table if exists `fn_chat_users`;
create table `fn_chat_users` (
`chat_room_id` int(11) default null,
`user_id` int(11) default null,
`last_activity_date_time` timestamp not null default current_timestamp on update current_timestamp,
`chat_status` varchar(20) default null,
`id` int(11) not null,
primary key (`id`)
);

--
-- Table structure for table `fn_datasource`
--

drop table if exists `fn_datasource`;
create table `fn_datasource` (
`id` int(11) not null auto_increment,
`name` varchar(50) default null,
`driver_name` varchar(256) default null,
`server` varchar(256) default null,
`port` int(11) default null,
`user_name` varchar(256) default null,
`password` varchar(256) default null,
`url` varchar(256) default null,
`min_pool_size` int(11) default null,
`max_pool_size` int(11) default null,
`adapter_id` int(11) default null,
`ds_type` varchar(20) default null,
primary key (`id`)
);

--
-- Table structure for table `fn_function`
--

drop table if exists `fn_function`;
create table `fn_function` (
`function_cd` varchar(30) not null,
`function_name` varchar(50) not null,
primary key (`function_cd`)
);

--
-- Table structure for table `fn_lu_activity`
--

drop table if exists `fn_lu_activity`;
create table `fn_lu_activity` (
`activity_cd` varchar(50) not null,
`activity` varchar(50) not null,
primary key (`activity_cd`)
);

--
-- Table structure for table `fn_lu_alert_method`
--

drop table if exists `fn_lu_alert_method`;
create table `fn_lu_alert_method` (
`alert_method_cd` varchar(10) not null,
`alert_method` varchar(50) not null,
primary key (`alert_method_cd`)
);

--
-- Table structure for table `fn_lu_broadcast_site`
--

drop table if exists `fn_lu_broadcast_site`;
create table `fn_lu_broadcast_site` (
`broadcast_site_cd` varchar(50) not null,
`broadcast_site_descr` varchar(100) default null,
primary key (`broadcast_site_cd`)
);

--
-- Table structure for table `fn_lu_menu_set`
--

drop table if exists `fn_lu_menu_set`;
create table `fn_lu_menu_set` (
`menu_set_cd` varchar(10) not null,
`menu_set_name` varchar(50) not null,
primary key (`menu_set_cd`)
);

--
-- Table structure for table `fn_lu_message_location`
--

drop table if exists `fn_lu_message_location`;
create table `fn_lu_message_location` (
`message_location_id` decimal(11,0) not null,
`message_location_descr` varchar(30) not null,
primary key (`message_location_id`)
);

--
-- Table structure for table `fn_lu_priority`
--

drop table if exists `fn_lu_priority`;
create table `fn_lu_priority` (
`priority_id` decimal(11,0) not null,
`priority` varchar(50) not null,
`active_yn` char(1) not null,
`sort_order` decimal(5,0) default null,
primary key (`priority_id`)
);

--
-- Table structure for table `fn_lu_role_type`
--

drop table if exists `fn_lu_role_type`;
create table `fn_lu_role_type` (
`role_type_id` decimal(11,0) not null,
`role_type` varchar(50) not null,
primary key (`role_type_id`)
);

--
-- Table structure for table `fn_lu_tab_set`
--

drop table if exists `fn_lu_tab_set`;
create table `fn_lu_tab_set` (
`tab_set_cd` varchar(30) not null,
`tab_set_name` varchar(50) not null,
primary key (`tab_set_cd`)
);

--
-- Table structure for table `fn_lu_timezone`
--

drop table if exists `fn_lu_timezone`;
create table `fn_lu_timezone` (
`timezone_id` int(11) not null,
`timezone_name` varchar(100) not null,
`timezone_value` varchar(100) not null,
primary key (`timezone_id`)
);

--
-- Table structure for table `fn_menu`
--

drop table if exists `fn_menu`;
create table `fn_menu` (
`menu_id` int(11) not null auto_increment,
`label` varchar(100) default null,
`parent_id` int(11) default null,
`sort_order` decimal(4,0) default null,
`action` varchar(200) default null,
`function_cd` varchar(30) default null,
`active_yn` varchar(1) not null default 'y',
`servlet` varchar(50) default null,
`query_string` varchar(200) default null,
`external_url` varchar(200) default null,
`target` varchar(25) default null,
`menu_set_cd` varchar(10) default 'app',
`separator_yn` char(1) default 'n',
`image_src` varchar(100) default null,
primary key (`menu_id`),
key `fk_fn_menu_ref_196_fn_menu` (`parent_id`),
key `fk_fn_menu_menu_set_cd` (`menu_set_cd`),
key `fn_menu_function_cd` (`function_cd`) using btree,
constraint `fk_fn_menu_menu_set_cd` foreign key (`menu_set_cd`) references `fn_lu_menu_set` (`menu_set_cd`),
constraint `fk_fn_menu_ref_196_fn_menu` foreign key (`parent_id`) references `fn_menu` (`menu_id`),
constraint `fk_fn_menu_ref_223_fn_funct` foreign key (`function_cd`) references `fn_function` (`function_cd`)
);

--
-- Table structure for table `fn_org`
--

drop table if exists `fn_org`;
create table `fn_org` (
`org_id` int(11) not null,
`org_name` varchar(50) not null,
`access_cd` varchar(10) default null,
primary key (`org_id`),
key `fn_org_access_cd` (`access_cd`) using btree
);

--
-- Table structure for table `fn_qz_blob_triggers`
--

drop table if exists `fn_qz_blob_triggers`;
create table `fn_qz_blob_triggers` (
`sched_name` varchar(120) not null,
`trigger_name` varchar(200) not null,
`trigger_group` varchar(200) not null,
`blob_data` blob,
primary key (`sched_name`,`trigger_name`,`trigger_group`),
key `sched_name` (`sched_name`,`trigger_name`,`trigger_group`),
constraint `fn_qz_blob_triggers_ibfk_1` foreign key (`sched_name`, `trigger_name`, `trigger_group`) references `fn_qz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
);

--
-- Table structure for table `fn_qz_calendars`
--

drop table if exists `fn_qz_calendars`;
create table `fn_qz_calendars` (
`sched_name` varchar(120) not null,
`calendar_name` varchar(200) not null,
`calendar` blob not null,
primary key (`sched_name`,`calendar_name`)
);

--
-- Table structure for table `fn_qz_cron_triggers`
--

drop table if exists `fn_qz_cron_triggers`;
create table `fn_qz_cron_triggers` (
`sched_name` varchar(120) not null,
`trigger_name` varchar(200) not null,
`trigger_group` varchar(200) not null,
`cron_expression` varchar(120) not null,
`time_zone_id` varchar(80) default null,
primary key (`sched_name`,`trigger_name`,`trigger_group`),
constraint `fn_qz_cron_triggers_ibfk_1` foreign key (`sched_name`, `trigger_name`, `trigger_group`) references `fn_qz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
);

--
-- Table structure for table `fn_qz_fired_triggers`
--

drop table if exists `fn_qz_fired_triggers`;
create table `fn_qz_fired_triggers` (
`sched_name` varchar(120) not null,
`entry_id` varchar(95) not null,
`trigger_name` varchar(200) not null,
`trigger_group` varchar(200) not null,
`instance_name` varchar(200) not null,
`fired_time` bigint(13) not null,
`sched_time` bigint(13) not null,
`priority` int(11) not null,
`state` varchar(16) not null,
`job_name` varchar(200) default null,
`job_group` varchar(200) default null,
`is_nonconcurrent` varchar(1) default null,
`requests_recovery` varchar(1) default null,
primary key (`sched_name`,`entry_id`),
key `idx_fn_qz_ft_trig_inst_name` (`sched_name`,`instance_name`),
key `idx_fn_qz_ft_inst_job_req_rcvry` (`sched_name`,`instance_name`,`requests_recovery`),
key `idx_fn_qz_ft_j_g` (`sched_name`,`job_name`,`job_group`),
key `idx_fn_qz_ft_jg` (`sched_name`,`job_group`),
key `idx_fn_qz_ft_t_g` (`sched_name`,`trigger_name`,`trigger_group`),
key `idx_fn_qz_ft_tg` (`sched_name`,`trigger_group`)
);

--
-- Table structure for table `fn_qz_job_details`
--

drop table if exists `fn_qz_job_details`;
create table `fn_qz_job_details` (
`sched_name` varchar(120) not null,
`job_name` varchar(200) not null,
`job_group` varchar(200) not null,
`description` varchar(250) default null,
`job_class_name` varchar(250) not null,
`is_durable` varchar(1) not null,
`is_nonconcurrent` varchar(1) not null,
`is_update_data` varchar(1) not null,
`requests_recovery` varchar(1) not null,
`job_data` blob,
primary key (`sched_name`,`job_name`,`job_group`),
key `idx_fn_qz_j_req_recovery` (`sched_name`,`requests_recovery`),
key `idx_fn_qz_j_grp` (`sched_name`,`job_group`)
);

--
-- Table structure for table `fn_qz_locks`
--

drop table if exists `fn_qz_locks`;
create table `fn_qz_locks` (
`sched_name` varchar(120) not null,
`lock_name` varchar(40) not null,
primary key (`sched_name`,`lock_name`)
);

--
-- Table structure for table `fn_qz_paused_trigger_grps`
--

drop table if exists `fn_qz_paused_trigger_grps`;
create table `fn_qz_paused_trigger_grps` (
`sched_name` varchar(120) not null,
`trigger_group` varchar(200) not null,
primary key (`sched_name`,`trigger_group`)
);

--
-- Table structure for table `fn_qz_scheduler_state`
--

drop table if exists `fn_qz_scheduler_state`;
create table `fn_qz_scheduler_state` (
`sched_name` varchar(120) not null,
`instance_name` varchar(200) not null,
`last_checkin_time` bigint(13) not null,
`checkin_interval` bigint(13) not null,
primary key (`sched_name`,`instance_name`)
);

--
-- Table structure for table `fn_qz_simple_triggers`
--

drop table if exists `fn_qz_simple_triggers`;
create table `fn_qz_simple_triggers` (
`sched_name` varchar(120) not null,
`trigger_name` varchar(200) not null,
`trigger_group` varchar(200) not null,
`repeat_count` bigint(7) not null,
`repeat_interval` bigint(12) not null,
`times_triggered` bigint(10) not null,
primary key (`sched_name`,`trigger_name`,`trigger_group`),
constraint `fn_qz_simple_triggers_ibfk_1` foreign key (`sched_name`, `trigger_name`, `trigger_group`) references `fn_qz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
);

--
-- Table structure for table `fn_qz_simprop_triggers`
--

drop table if exists `fn_qz_simprop_triggers`;
create table `fn_qz_simprop_triggers` (
`sched_name` varchar(120) not null,
`trigger_name` varchar(200) not null,
`trigger_group` varchar(200) not null,
`str_prop_1` varchar(512) default null,
`str_prop_2` varchar(512) default null,
`str_prop_3` varchar(512) default null,
`int_prop_1` int(11) default null,
`int_prop_2` int(11) default null,
`long_prop_1` bigint(20) default null,
`long_prop_2` bigint(20) default null,
`dec_prop_1` decimal(13,4) default null,
`dec_prop_2` decimal(13,4) default null,
`bool_prop_1` varchar(1) default null,
`bool_prop_2` varchar(1) default null,
primary key (`sched_name`,`trigger_name`,`trigger_group`),
constraint `fn_qz_simprop_triggers_ibfk_1` foreign key (`sched_name`, `trigger_name`, `trigger_group`) references `fn_qz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
);

--
-- Table structure for table `fn_qz_triggers`
--

drop table if exists `fn_qz_triggers`;
create table `fn_qz_triggers` (
`sched_name` varchar(120) not null,
`trigger_name` varchar(200) not null,
`trigger_group` varchar(200) not null,
`job_name` varchar(200) not null,
`job_group` varchar(200) not null,
`description` varchar(250) default null,
`next_fire_time` bigint(13) default null,
`prev_fire_time` bigint(13) default null,
`priority` int(11) default null,
`trigger_state` varchar(16) not null,
`trigger_type` varchar(8) not null,
`start_time` bigint(13) not null,
`end_time` bigint(13) default null,
`calendar_name` varchar(200) default null,
`misfire_instr` smallint(2) default null,
`job_data` blob,
primary key (`sched_name`,`trigger_name`,`trigger_group`),
key `idx_fn_qz_t_j` (`sched_name`,`job_name`,`job_group`),
key `idx_fn_qz_t_jg` (`sched_name`,`job_group`),
key `idx_fn_qz_t_c` (`sched_name`,`calendar_name`),
key `idx_fn_qz_t_g` (`sched_name`,`trigger_group`),
key `idx_fn_qz_t_state` (`sched_name`,`trigger_state`),
key `idx_fn_qz_t_n_state` (`sched_name`,`trigger_name`,`trigger_group`,`trigger_state`),
key `idx_fn_qz_t_n_g_state` (`sched_name`,`trigger_group`,`trigger_state`),
key `idx_fn_qz_t_next_fire_time` (`sched_name`,`next_fire_time`),
key `idx_fn_qz_t_nft_st` (`sched_name`,`trigger_state`,`next_fire_time`),
key `idx_fn_qz_t_nft_misfire` (`sched_name`,`misfire_instr`,`next_fire_time`),
key `idx_fn_qz_t_nft_st_misfire` (`sched_name`,`misfire_instr`,`next_fire_time`,`trigger_state`),
key `idx_fn_qz_t_nft_st_misfire_grp` (`sched_name`,`misfire_instr`,`next_fire_time`,`trigger_group`,`trigger_state`),
constraint `fn_qz_triggers_ibfk_1` foreign key (`sched_name`, `job_name`, `job_group`) references `fn_qz_job_details` (`sched_name`, `job_name`, `job_group`)
);

--
-- Table structure for table `fn_restricted_url`
--

drop table if exists `fn_restricted_url`;
create table `fn_restricted_url` (
`restricted_url` varchar(250) not null,
`function_cd` varchar(30) not null,
primary key (`restricted_url`,`function_cd`),
key `fk_restricted_url_function_cd` (`function_cd`),
constraint `fk_restricted_url_function_cd` foreign key (`function_cd`) references `fn_function` (`function_cd`)
);

--
-- Table structure for table `fn_role`
--

drop table if exists `fn_role`;
create table `fn_role` (
`role_id` int(11) not null auto_increment,
`role_name` varchar(50) not null,
`active_yn` varchar(1) not null default 'y',
`priority` decimal(4,0) default null,
primary key (`role_id`)
);

--
-- Table structure for table `fn_role_composite`
--

drop table if exists `fn_role_composite`;
create table `fn_role_composite` (
`parent_role_id` int(11) not null,
`child_role_id` int(11) not null,
primary key (`parent_role_id`,`child_role_id`),
key `fk_fn_role_composite_child` (`child_role_id`),
constraint `fk_fn_role_composite_child` foreign key (`child_role_id`) references `fn_role` (`role_id`),
constraint `fk_fn_role_composite_parent` foreign key (`parent_role_id`) references `fn_role` (`role_id`)
);

--
-- Table structure for table `fn_role_function`
--

drop table if exists `fn_role_function`;
create table `fn_role_function` (
`role_id` int(11) not null,
`function_cd` varchar(30) not null,
primary key (`role_id`,`function_cd`),
key `fn_role_function_function_cd` (`function_cd`) using btree,
key `fn_role_function_role_id` (`role_id`) using btree,
constraint `fk_fn_role__ref_198_fn_role` foreign key (`role_id`) references `fn_role` (`role_id`),
constraint `fk_fn_role__ref_201_fn_funct` foreign key (`function_cd`) references `fn_function` (`function_cd`)
);

--
-- Table structure for table `fn_schedule_workflows`
--

drop table if exists `fn_schedule_workflows`;
create table `fn_schedule_workflows` (
`id_schedule_workflows` bigint(25) not null auto_increment,
`workflow_server_url` varchar(45) default null,
`workflow_key` varchar(45) not null,
`workflow_arguments` varchar(45) default null,
`startdatetimecron` varchar(45) default null,
`enddatetime` timestamp not null default current_timestamp,
`start_date_time` timestamp not null default current_timestamp,
`recurrence` varchar(45) default null,
primary key (`id_schedule_workflows`)
);

--
-- Table structure for table `fn_tab`
--

drop table if exists `fn_tab`;
create table `fn_tab` (
`tab_cd` varchar(30) not null,
`tab_name` varchar(50) not null,
`tab_descr` varchar(100) default null,
`action` varchar(100) not null,
`function_cd` varchar(30) not null,
`active_yn` char(1) not null,
`sort_order` decimal(11,0) not null,
`parent_tab_cd` varchar(30) default null,
`tab_set_cd` varchar(30) default null,
primary key (`tab_cd`),
key `fk_fn_tab_function_cd` (`function_cd`),
key `fk_fn_tab_set_cd` (`tab_set_cd`),
constraint `fk_fn_tab_function_cd` foreign key (`function_cd`) references `fn_function` (`function_cd`),
constraint `fk_fn_tab_set_cd` foreign key (`tab_set_cd`) references `fn_lu_tab_set` (`tab_set_cd`)
);

--
-- Table structure for table `fn_tab_selected`
--

drop table if exists `fn_tab_selected`;
create table `fn_tab_selected` (
`selected_tab_cd` varchar(30) not null,
`tab_uri` varchar(40) not null,
primary key (`selected_tab_cd`,`tab_uri`),
constraint `fk_fn_tab_selected_tab_cd` foreign key (`selected_tab_cd`) references `fn_tab` (`tab_cd`)
);

--
-- Table structure for table `fn_user`
--

drop table if exists `fn_user`;
create table `fn_user` (
`user_id` int(11) not null auto_increment,
`org_id` int(11) default null,
`manager_id` int(11) default null,
`first_name` varchar(25) default null,
`middle_name` varchar(25) default null,
`last_name` varchar(25) default null,
`phone` varchar(25) default null,
`fax` varchar(25) default null,
`cellular` varchar(25) default null,
`email` varchar(50) default null,
`address_id` decimal(11,0) default null,
`alert_method_cd` varchar(10) default null,
`hrid` varchar(20) default null,
`org_user_id` varchar(20) default null,
`org_code` varchar(30) default null,
`login_id` varchar(25) default null,
`login_pwd` varchar(25) default null,
`last_login_date` timestamp not null default current_timestamp on update current_timestamp,
`active_yn` varchar(1) not null default 'y',
`created_id` int(11) default null,
`created_date` timestamp not null default current_timestamp,
`modified_id` int(11) default null,
`modified_date` timestamp not null default current_timestamp,
`is_internal_yn` char(1) not null default 'n',
`address_line_1` varchar(100) default null,
`address_line_2` varchar(100) default null,
`city` varchar(50) default null,
`state_cd` varchar(3) default null,
`zip_code` varchar(11) default null,
`country_cd` varchar(3) default null,
`location_clli` varchar(8) default null,
`org_manager_userid` varchar(20) default null,
`company` varchar(100) default null,
`department_name` varchar(100) default null,
`job_title` varchar(100) default null,
`timezone` int(11) default null,
`department` varchar(25) default null,
`business_unit` varchar(25) default null,
`business_unit_name` varchar(100) default null,
`cost_center` varchar(25) default null,
`fin_loc_code` varchar(10) default null,
`silo_status` varchar(10) default null,
primary key (`user_id`),
unique key `fn_user_hrid` (`hrid`) using btree,
unique key `fn_user_login_id` (`login_id`) using btree,
key `fn_user_address_id` (`address_id`) using btree,
key `fn_user_alert_method_cd` (`alert_method_cd`) using btree,
key `fn_user_org_id` (`org_id`) using btree,
key `fk_fn_user_ref_197_fn_user` (`manager_id`),
key `fk_fn_user_ref_198_fn_user` (`created_id`),
key `fk_fn_user_ref_199_fn_user` (`modified_id`),
key `fk_timezone` (`timezone`),
constraint `fk_fn_user_ref_110_fn_org` foreign key (`org_id`) references `fn_org` (`org_id`),
constraint `fk_fn_user_ref_123_fn_lu_al` foreign key (`alert_method_cd`) references `fn_lu_alert_method` (`alert_method_cd`),
constraint `fk_fn_user_ref_197_fn_user` foreign key (`manager_id`) references `fn_user` (`user_id`),
constraint `fk_fn_user_ref_198_fn_user` foreign key (`created_id`) references `fn_user` (`user_id`),
constraint `fk_fn_user_ref_199_fn_user` foreign key (`modified_id`) references `fn_user` (`user_id`),
constraint `fk_timezone` foreign key (`timezone`) references `fn_lu_timezone` (`timezone_id`)
);

--
-- Table structure for table `fn_user_pseudo_role`
--

drop table if exists `fn_user_pseudo_role`;
create table `fn_user_pseudo_role` (
`pseudo_role_id` int(11) not null,
`user_id` int(11) not null,
primary key (`pseudo_role_id`,`user_id`),
key `fk_pseudo_role_user_id` (`user_id`),
constraint `fk_pseudo_role_pseudo_role_id` foreign key (`pseudo_role_id`) references `fn_role` (`role_id`),
constraint `fk_pseudo_role_user_id` foreign key (`user_id`) references `fn_user` (`user_id`)
);

--
-- Table structure for table `fn_user_role`
--

drop table if exists `fn_user_role`;
create table `fn_user_role` (
`user_id` int(10) not null,
`role_id` int(10) not null,
`priority` decimal(4,0) default null,
`app_id` int(11) not null default '1',
primary key (`user_id`,`role_id`,`app_id`),
key `fn_user_role_role_id` (`role_id`) using btree,
key `fn_user_role_user_id` (`user_id`) using btree,
key `fk_fn_user__ref_178_fn_app_idx` (`app_id`),
constraint `fk_fn_user__ref_172_fn_user` foreign key (`user_id`) references `fn_user` (`user_id`),
constraint `fk_fn_user__ref_175_fn_role` foreign key (`role_id`) references `fn_role` (`role_id`),
constraint `fk_fn_user__ref_178_fn_app` foreign key (`app_id`) references `fn_app` (`app_id`)
);

--
-- Table structure for table `fn_workflow`
--

drop table if exists `fn_workflow`;
create table `fn_workflow` (
`id` mediumint(9) not null auto_increment,
`name` varchar(20) not null,
`description` varchar(500) default null,
`run_link` varchar(300) default null,
`suspend_link` varchar(300) default null,
`modified_link` varchar(300) default null,
`active_yn` varchar(300) default null,
`created` varchar(300) default null,
`created_by` int(11) default null,
`modified` varchar(300) default null,
`modified_by` int(11) default null,
`workflow_key` varchar(50) default null,
primary key (`id`),
unique key `name` (`name`)
);

--
-- Table structure for table `forwardprogressentity`
--

drop table if exists `forwardprogressentity`;
create table `forwardprogressentity` (
`forwardprogressid` bigint(20) not null auto_increment,
`resourcename` varchar(100) not null,
`fpc_count` mediumtext not null,
`created_date` timestamp not null default current_timestamp,
`last_updated` timestamp not null default '0000-00-00 00:00:00',
primary key (`forwardprogressid`),
unique key `resource_key` (`resourcename`)
);

--
-- Temporary table structure for view `function_flattener`
--

drop table if exists `function_flattener`;
drop view  if exists `function_flattener`;
create table `function_flattener` (
`id` tinyint not null,
`shortname` tinyint not null,
`return_datatype` tinyint not null,
`is_bag_return` tinyint not null,
`is_higher_order` tinyint not null,
`arg_lb` tinyint not null,
`arg_ub` tinyint not null,
`arg1_isbag` tinyint not null,
`arg1_datatype` tinyint not null,
`arg2_isbag` tinyint not null,
`arg2_datatype` tinyint not null,
`arg3_isbag` tinyint not null,
`arg3_datatype` tinyint not null
);

--
-- Table structure for table `functionarguments`
--

drop table if exists `functionarguments`;
create table `functionarguments` (
`id` int(11) not null,
`arg_index` int(11) not null,
`is_bag` int(11) not null,
`datatype_id` int(11) default null,
`function_id` int(11) default null,
primary key (`id`),
key `fk_functionarguments_function_id` (`function_id`),
key `fk_functionarguments_datatype_id` (`datatype_id`),
constraint `fk_functionarguments_datatype_id` foreign key (`datatype_id`) references `datatype` (`id`),
constraint `fk_functionarguments_function_id` foreign key (`function_id`) references `functiondefinition` (`id`)
);

--
-- Table structure for table `functiondefinition`
--

drop table if exists `functiondefinition`;
create table `functiondefinition` (
`id` int(11) not null,
`arg_lb` int(11) not null,
`arg_ub` int(11) not null,
`ho_arg_lb` int(11) default null,
`ho_arg_ub` int(11) default null,
`ho_primitive` char(1) default null,
`is_bag_return` int(11) not null,
`is_higher_order` int(11) not null,
`short_name` varchar(64) not null,
`xacml_id` varchar(255) not null,
`return_datatype` int(11) default null,
primary key (`id`),
key `fk_functiondefinition_return_datatype` (`return_datatype`),
constraint `fk_functiondefinition_return_datatype` foreign key (`return_datatype`) references `datatype` (`id`)
);

--
-- Table structure for table `fwchildtoparent`
--

drop table if exists `fwchildtoparent`;
create table `fwchildtoparent` (
`id` int(11) not null auto_increment,
`child` varchar(1024) default null,
`parent` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `fwparent`
--

drop table if exists `fwparent`;
create table `fwparent` (
`id` int(11) not null auto_increment,
`parent` varchar(1024) default null,
`securityzone` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `fwparenttochild`
--

drop table if exists `fwparenttochild`;
create table `fwparenttochild` (
`id` int(11) not null auto_increment,
`parent` varchar(1024) default null,
`child` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `globalrolesettings`
--

drop table if exists `globalrolesettings`;
create table `globalrolesettings` (
`role` varchar(45) not null,
`lockdown` tinyint(1) default null,
primary key (`role`)
);





--
-- Table structure for table `groupentity`
--

drop table if exists `groupentity`;
create table `groupentity` (
`groupkey` bigint(20) not null auto_increment,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`defaultgroup` tinyint(1) not null,
`deleted` tinyint(1) not null,
`description` varchar(2048) not null,
`groupid` varchar(100) not null,
`groupname` varchar(1024) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`version` int(11) default null,
primary key (`groupkey`)
);

--
-- Table structure for table `grouppolicyscopelist`
--

drop table if exists `grouppolicyscopelist`;
create table `grouppolicyscopelist` (
`id` int(11) not null auto_increment,
`name` varchar(767) not null,
`grouplist` varchar(767) default null,
`description` varchar(1024) default null,
primary key (`id`),
unique key `name_uniq` (`name`),
unique key `grouplist_unique` (`grouplist`)
);

--
-- Table structure for table `groupservicelist`
--

drop table if exists `groupservicelist`;
create table `groupservicelist` (
`id` int(11) not null auto_increment,
`name` varchar(1024) default null,
`servicelist` varchar(1024) default null,
primary key (`id`),
key `idx_groupservicelist` (`id`)
);

--
-- Temporary table structure for view `higherorder_bag_functions`
--

drop table if exists `higherorder_bag_functions`;
drop view  if exists `higherorder_bag_functions`;
create table `higherorder_bag_functions` (
`id` tinyint not null,
`shortname` tinyint not null,
`return_datatype` tinyint not null,
`is_bag_return` tinyint not null,
`is_higher_order` tinyint not null,
`arg_lb` tinyint not null,
`arg_ub` tinyint not null,
`arg1_isbag` tinyint not null,
`arg1_datatype` tinyint not null,
`arg2_isbag` tinyint not null,
`arg2_datatype` tinyint not null,
`arg3_isbag` tinyint not null,
`arg3_datatype` tinyint not null
);

--
-- Table structure for table `hpeventsource`
--

drop table if exists `hpeventsource`;
create table `hpeventsource` (
`id` int(11) not null auto_increment,
`eventsourcename` varchar(1024) not null,
`description` varchar(1024) default null,
`created_date` timestamp not null default current_timestamp on update current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `integrityauditentity`
--

drop table if exists `integrityauditentity`;
create table `integrityauditentity` (
`id` int(11) not null auto_increment,
`persistenceunit` varchar(100) not null,
`site` varchar(100) default null,
`nodetype` varchar(100) default null,
`resourcename` varchar(100) not null,
`designated` tinyint(1) default '0',
`jdbcdriver` varchar(100) not null,
`jdbcurl` varchar(100) not null,
`jdbcuser` varchar(30) not null,
`jdbcpassword` varchar(30) not null,
`createddate` timestamp not null default current_timestamp,
`lastupdated` timestamp not null default '0000-00-00 00:00:00',
primary key (`id`),
unique key `resourcename_uniq` (`resourcename`)
);

--
-- Temporary table structure for view `match_functions`
--

drop table if exists `match_functions`;
drop view  if exists `match_functions`;
create table `match_functions` (
`id` tinyint not null,
`shortname` tinyint not null,
`onap_sdkid` tinyint not null,
`return_datatype` tinyint not null,
`is_bag_return` tinyint not null,
`arg_lb` tinyint not null,
`arg_ub` tinyint not null,
`arg1_isbag` tinyint not null,
`arg1_datatype` tinyint not null,
`arg2_isbag` tinyint not null,
`arg2_datatype` tinyint not null
);

--
-- Table structure for table `microserviceconfigname`
--

drop table if exists `microserviceconfigname`;
create table `microserviceconfigname` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `microservicelocation`
--

drop table if exists `microservicelocation`;
create table `microservicelocation` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `microservicemodels`
--

drop table if exists `microservicemodels`;
create table `microservicemodels` (
`id` int(11) not null auto_increment,
`modelname` varchar(767) not null,
`description` varchar(1024) default null,
`dependency` varchar(1024) default null,
`imported_by` varchar(45) not null,
`attributes` longtext,
`ref_attributes` longtext,
`sub_attributes` longtext,
`version` varchar(45) default null,
primary key (`id`),
unique key `microservicemodels_uniq` (`modelname`,`version`)
);

--
-- Table structure for table `obadvice`
--

drop table if exists `obadvice`;
create table `obadvice` (
`id` int(11) not null,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`description` varchar(2048) default null,
`fulfill_on` varchar(32) default null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`type` varchar(100) not null,
`xacml_id` varchar(255) not null,
primary key (`id`)
);

--
-- Table structure for table `obadviceexpressions`
--

drop table if exists `obadviceexpressions`;
create table `obadviceexpressions` (
`id` int(11) not null,
`type` varchar(100) not null,
`obadvice_id` int(11) default null,
`attribute_id` int(11) default null,
primary key (`id`),
key `fk_obadviceexpressions_obadvice_id` (`obadvice_id`),
key `fk_obadviceexpressions_attribute_id` (`attribute_id`),
constraint `fk_obadviceexpressions_attribute_id` foreign key (`attribute_id`) references `attribute` (`id`),
constraint `fk_obadviceexpressions_obadvice_id` foreign key (`obadvice_id`) references `obadvice` (`id`)
);

--
-- Table structure for table `parentdictionaryitems`
--

drop table if exists `parentdictionaryitems`;
create table `parentdictionaryitems` (
`id` int(11) not null auto_increment,
`parentitemname` varchar(1024) default null,
`description` varchar(1024) default null,
`addresslist` varchar(1024) not null,
`servicelist` varchar(1024) not null,
primary key (`id`)
);

--
-- Table structure for table `pdpentity`
--

drop table if exists `pdpentity`;
create table `pdpentity` (
`pdpkey` bigint(20) not null auto_increment,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`deleted` tinyint(1) not null,
`description` varchar(2048) not null,
`jmxport` int(11) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`pdpid` varchar(255) not null,
`pdpname` varchar(255) not null,
`groupkey` bigint(20) default null,
primary key (`pdpkey`),
key `fk_pdpentity_groupkey` (`groupkey`),
constraint `fk_pdpentity_groupkey` foreign key (`groupkey`) references `groupentity` (`groupkey`)
);

--
-- Table structure for table `pepoptions`
--

drop table if exists `pepoptions`;
create table `pepoptions` (
`id` int(11) not null auto_increment,
`pep_name` varchar(1024) not null,
`description` varchar(1024) default null,
`actions` varchar(1024) not null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `pipconfigparams`
--

drop table if exists `pipconfigparams`;
create table `pipconfigparams` (
`id` int(11) not null,
`param_default` varchar(2048) default null,
`param_name` varchar(1024) not null,
`param_value` varchar(2048) not null,
`required` char(1) not null,
`pip_id` int(11) default null,
primary key (`id`),
key `fk_pipconfigparams_pip_id` (`pip_id`),
constraint `fk_pipconfigparams_pip_id` foreign key (`pip_id`) references `pipconfiguration` (`id`)
);

--
-- Table structure for table `pipconfiguration`
--

drop table if exists `pipconfiguration`;
create table `pipconfiguration` (
`id` int(11) not null,
`classname` varchar(2048) not null,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp on update current_timestamp,
`description` varchar(2048) default null,
`issuer` varchar(1024) default null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`name` varchar(255) not null,
`read_only` char(1) not null,
`requires_resolver` char(1) not null,
`type` int(11) default null,
primary key (`id`),
key `fk_pipconfiguration_type` (`type`),
constraint `fk_pipconfiguration_type` foreign key (`type`) references `piptype` (`id`)
);

--
-- Table structure for table `pipresolver`
--

drop table if exists `pipresolver`;
create table `pipresolver` (
`id` int(11) not null,
`classname` varchar(2048) not null,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp on update current_timestamp,
`description` varchar(2048) default null,
`issuer` varchar(1024) default null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`name` varchar(255) not null,
`read_only` char(1) not null,
`pip_id` int(11) default null,
primary key (`id`),
key `fk_pipresolver_pip_id` (`pip_id`),
constraint `fk_pipresolver_pip_id` foreign key (`pip_id`) references `pipconfiguration` (`id`)
);

--
-- Table structure for table `pipresolverparams`
--

drop table if exists `pipresolverparams`;
create table `pipresolverparams` (
`id` int(11) not null,
`param_default` varchar(2048) default null,
`param_name` varchar(1024) not null,
`param_value` varchar(2048) not null,
`required` char(1) not null,
`id_resolver` int(11) default null,
primary key (`id`),
key `fk_pipresolverparams_id_resolver` (`id_resolver`),
constraint `fk_pipresolverparams_id_resolver` foreign key (`id_resolver`) references `pipresolver` (`id`)
);

--
-- Table structure for table `piptype`
--

drop table if exists `piptype`;
create table `piptype` (
`id` int(11) not null,
`type` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `policy_management`
--

drop table if exists `policy_management`;
create table `policy_management` (
`id` int(11) not null,
`config_name` varchar(1024) not null,
`create_date_time` timestamp not null default current_timestamp on update current_timestamp,
`creted_by` varchar(45) not null,
`onap_name` varchar(1024) not null,
`policy_name` varchar(1024) not null,
`scope` varchar(1024) not null,
`update_date_time` timestamp not null default '0000-00-00 00:00:00',
`updated_by` varchar(45) not null,
`xml` text not null,
primary key (`id`)
);

--
-- Table structure for table `policy_manangement`
--

drop table if exists `policy_manangement`;
create table `policy_manangement` (
`id` int(11) not null,
`config_name` varchar(1024) not null,
`create_date_time` timestamp not null default current_timestamp on update current_timestamp,
`created_by` varchar(45) not null,
`onap_name` varchar(1024) not null,
`policy_name` varchar(1024) not null,
`scope` varchar(1024) not null,
`update_date_time` timestamp not null default '0000-00-00 00:00:00',
`updated_by` varchar(45) not null,
`xml` text not null,
primary key (`id`)
);

--
-- Table structure for table `policyalgorithms`
--

drop table if exists `policyalgorithms`;
create table `policyalgorithms` (
`id` int(11) not null,
`is_standard` char(1) not null,
`short_name` varchar(64) not null,
`xacml_id` varchar(255) not null,
primary key (`id`)
);

--
-- Table structure for table `policydbdaoentity`
--

drop table if exists `policydbdaoentity`;
create table `policydbdaoentity` (
`policydbdaourl` varchar(500) not null,
`created_date` timestamp not null default current_timestamp,
`description` varchar(2048) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`password` varchar(100) default null,
`username` varchar(100) default null,
primary key (`policydbdaourl`)
);

--
-- Table structure for table `policyeditorscopes`
--

drop table if exists `policyeditorscopes`;
create table `policyeditorscopes` (
`id` int(45) not null auto_increment,
`scopename` varchar(1024) not null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `policyentity`
--

drop table if exists `policyentity`;
create table `policyentity` (
`policyid` bigint(20) not null auto_increment,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`deleted` tinyint(1) not null,
`description` varchar(2048) not null,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`policydata` text,
`policyname` varchar(255) not null,
`policyversion` int(11) default null,
`scope` varchar(255) not null,
`version` int(11) default null,
`actionbodyid` bigint(20) default null,
`configurationdataid` bigint(20) default null,
primary key (`policyid`),
unique key `unq_policyentity_0` (`policyname`,`scope`),
key `scope` (`scope`),
key `policyname` (`policyname`),
key `fk_policyentity_configurationdataid` (`configurationdataid`),
key `fk_policyentity_actionbodyid` (`actionbodyid`),
constraint `fk_policyentity_actionbodyid` foreign key (`actionbodyid`) references `actionbodyentity` (`actionbodyid`),
constraint `fk_policyentity_configurationdataid` foreign key (`configurationdataid`) references `configurationdataentity` (`configurationdataid`)
);

--
-- Table structure for table `policygroupentity`
--

drop table if exists `policygroupentity`;
create table `policygroupentity` (
`groupkey` bigint(20) not null auto_increment,
`policyid` bigint(20) not null,
primary key (`groupkey`,`policyid`),
key `fk_policygroupentity_policyid` (`policyid`),
constraint `fk_policygroupentity_groupkey` foreign key (`groupkey`) references `groupentity` (`groupkey`),
constraint `fk_policygroupentity_policyid` foreign key (`policyid`) references `policyentity` (`policyid`)
);

--
-- Table structure for table `policyscopeclosedloop`
--

drop table if exists `policyscopeclosedloop`;
create table `policyscopeclosedloop` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `policyscoperesource`
--

drop table if exists `policyscoperesource`;
create table `policyscoperesource` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `policyscopeservice`
--

drop table if exists `policyscopeservice`;
create table `policyscopeservice` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `policyscopetype`
--

drop table if exists `policyscopetype`;
create table `policyscopetype` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `policyscore`
--

drop table if exists `policyscore`;
create table `policyscore` (
`id` int(11) not null,
`policy_name` varchar(1024) not null,
`versionextension` varchar(45) default null,
`policy_score` varchar(45) default null,
primary key (`id`)
);

--
-- Table structure for table `policyversion`
--

drop table if exists `policyversion`;
create table `policyversion` (
`id` int(11) not null auto_increment,
`policy_name` varchar(1024) not null,
`active_version` int(11) default null,
`highest_version` int(11) default null,
`created_date` timestamp not null default current_timestamp on update current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `portlist`
--

drop table if exists `portlist`;
create table `portlist` (
`id` int(11) not null auto_increment,
`portname` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `prefixlist`
--

drop table if exists `prefixlist`;
create table `prefixlist` (
`id` int(11) not null auto_increment,
`pl_name` varchar(1024) not null,
`pl_value` varchar(1024) default null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `protocollist`
--

drop table if exists `protocollist`;
create table `protocollist` (
`id` int(11) not null auto_increment,
`protocolname` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `rcloudinvocation`
--

drop table if exists `rcloudinvocation`;
create table `rcloudinvocation` (
`id` varchar(128) not null,
`created` timestamp not null default current_timestamp on update current_timestamp,
`userinfo` varchar(2048) not null,
`notebookid` varchar(128) not null,
`parameters` varchar(2048) default null,
`tokenreaddate` timestamp null default null,
primary key (`id`)
);

--
-- Table structure for table `rcloudnotebook`
--

drop table if exists `rcloudnotebook`;
create table `rcloudnotebook` (
`notebookname` varchar(128) not null,
`notebookid` varchar(128) not null,
primary key (`notebookname`)
);

--
-- Table structure for table `remotecatalogvalues`
--

drop table if exists `remotecatalogvalues`;
create table `remotecatalogvalues` (
`id` int(11) not null,
`name` varchar(75) not null,
`value` longtext,
primary key (`name`)
);

--
-- Table structure for table `resourceregistrationentity`
--

drop table if exists `resourceregistrationentity`;
create table `resourceregistrationentity` (
`resourceregistrationid` bigint(20) not null auto_increment,
`resourcename` varchar(100) not null,
`resourceurl` varchar(255) not null,
`site` varchar(50) default null,
`nodetype` varchar(50) default null,
`created_date` timestamp not null default current_timestamp,
`last_updated` timestamp not null default '0000-00-00 00:00:00',
primary key (`resourceregistrationid`),
unique key `resource` (`resourcename`),
unique key `id_resource_url` (`resourceurl`)
);

--
-- Table structure for table `risktype`
--

drop table if exists `risktype`;
create table `risktype` (
`id` int(11) not null auto_increment,
`created_by` varchar(255) default null,
`created_date` timestamp not null default current_timestamp on update current_timestamp,
`description` varchar(2048) default null,
`name` varchar(767) default null,
`modified_by` varchar(255) default null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
primary key (`id`),
unique key `name` (`name`)
);

--
-- Table structure for table `roles`
--

drop table if exists `roles`;
create table `roles` (
`id` int(11) not null auto_increment,
`loginid` varchar(1024) not null,
`role` varchar(1024) not null,
`scope` varchar(1024) default null,
`name` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `rulealgorithms`
--

drop table if exists `rulealgorithms`;
create table `rulealgorithms` (
`id` int(11) not null,
`is_standard` char(1) not null,
`short_name` varchar(64) not null,
`xacml_id` varchar(255) not null,
primary key (`id`),
unique key `xacml_id` (`xacml_id`)
);

--
-- Table structure for table `safepolicywarning`
--

drop table if exists `safepolicywarning`;
create table `safepolicywarning` (
`id` int(11) not null auto_increment,
`message` varchar(2048) default null,
`risktype` varchar(2048) default null,
`name` varchar(767) not null,
primary key (`id`),
unique key `name` (`name`)
);

--
-- Table structure for table `schema_info`
--

drop table if exists `schema_info`;
create table `schema_info` (
`schema_id` varchar(25) not null,
`schema_desc` varchar(75) not null,
`datasource_type` varchar(100) default null,
`connection_url` varchar(200) not null,
`user_name` varchar(45) not null,
`password` varchar(45) default null,
`driver_class` varchar(100) not null,
`min_pool_size` int(11) not null,
`max_pool_size` int(11) not null,
`idle_connection_test_period` int(11) not null
);

--
-- Table structure for table `scope`
--

drop table if exists `scope`;
create table `scope` (
`scopeid` bigint(20) not null auto_increment,
`parentscope` bigint(20) default null,
`scopename` varchar(1024) not null,
primary key (`scopeid`),
key `parentscope` (`parentscope`)
);

--
-- Table structure for table `scopes`
--

drop table if exists `scopes`;
create table `scopes` (
`id` int(11) not null auto_increment,
`scope` varchar(1024) not null,
`parent_scope` int(11) default null,
primary key (`id`)
);

--
-- Table structure for table `securityzone`
--

drop table if exists `securityzone`;
create table `securityzone` (
`id` int(11) not null auto_increment,
`name` varchar(1024) default null,
`value` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `sequence`
--

drop table if exists `sequence`;
create table `sequence` (
`seq_name` varchar(50) not null,
`seq_count` decimal(38,0) default null,
primary key (`seq_name`)
);

--
-- Table structure for table `servicegroup`
--

drop table if exists `servicegroup`;
create table `servicegroup` (
`id` int(11) not null auto_increment,
`name` varchar(1024) default null,
`type` varchar(1024) default null,
`transportprotocol` varchar(1024) default null,
`appprotocol` varchar(1024) default null,
`ports` varchar(1024) default null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `statemanagemententity`
--

drop table if exists `statemanagemententity`;
create table `statemanagemententity` (
`id` int(11) not null auto_increment,
`resourcename` varchar(100) not null,
`adminstate` varchar(20) not null,
`opstate` varchar(20) not null,
`availstatus` varchar(20) default null,
`standbystatus` varchar(20) default null,
`created_date` timestamp not null default current_timestamp,
`modifieddate` timestamp not null default '0000-00-00 00:00:00',
primary key (`id`),
unique key `resource` (`resourcename`)
);

--
-- Table structure for table `term`
--

drop table if exists `term`;
create table `term` (
`id` int(11) not null auto_increment,
`termname` varchar(1024) not null,
`fromzone` varchar(1024) default null,
`tozone` varchar(1024) default null,
`srciplist` varchar(1024) default null,
`destiplist` varchar(1024) default null,
`protocollist` varchar(1024) default null,
`portlist` varchar(1024) default null,
`srcportlist` varchar(1024) default null,
`destportlist` varchar(1024) default null,
`action` varchar(1024) default null,
`description` varchar(1024) default null,
`created_by` varchar(100) default null,
`created_date` timestamp not null default current_timestamp,
`modified_by` varchar(100) default null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
primary key (`id`)
);

--
-- Table structure for table `userinfo`
--

drop table if exists `userinfo`;
create table `userinfo` (
`loginid` varchar(767) not null,
`name` varchar(1024) not null,
primary key (`loginid`)
);


--
-- Temporary table structure for view `v_url_access`
--

drop table if exists `v_url_access`;
drop view  if exists `v_url_access`;
create table `v_url_access` (
`url` tinyint not null,
`function_cd` tinyint not null
);

--
-- Table structure for table `varbinddictionary`
--

drop table if exists `varbinddictionary`;
create table `varbinddictionary` (
`id` int(11) not null auto_increment,
`created_by` varchar(255) not null,
`created_date` timestamp not null default current_timestamp,
`modified_by` varchar(255) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`varbind_description` varchar(2048) default null,
`varbind_name` varchar(767) not null,
`varbind_oid` varchar(1024) not null,
primary key (`id`),
unique key `varbind_name` (`varbind_name`)
);

--
-- Table structure for table `vmtype`
--

drop table if exists `vmtype`;
create table `vmtype` (
`id` int(11) not null auto_increment,
`name` varchar(1024) not null,
`description` varchar(1024) default null,
primary key (`id`)
);

--
-- Table structure for table `vnftype`
--

drop table if exists `vnftype`;
create table `vnftype` (
`id` int(11) not null auto_increment,
`vnf_type` varchar(1024) not null,
`description` varchar(1024) default null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `vsclaction`
--

drop table if exists `vsclaction`;
create table `vsclaction` (
`id` int(11) not null auto_increment,
`vscl_action` varchar(767) not null,
`description` varchar(1024) default null,
`created_date` timestamp not null default current_timestamp,
`created_by` varchar(45) not null,
`modified_date` timestamp not null default '0000-00-00 00:00:00',
`modified_by` varchar(45) not null,
primary key (`id`),
unique key `vsclaction_vscl_action_unique` (`vscl_action`)
);

--
-- Table structure for table `watchpolicynotificationtable`
--

drop table if exists `watchpolicynotificationtable`;
create table `watchpolicynotificationtable` (
`id` int(45) not null auto_increment,
`policyname` text not null,
`loginids` varchar(45) not null,
primary key (`id`)
);

--
-- Table structure for table `zone`
--

drop table if exists `zone`;
create table `zone` (
`id` int(11) not null auto_increment,
`zonename` varchar(1024) not null,
`zonevalue` varchar(1024) default null,
primary key (`id`)
);

--
-- Current Database: `onap_sdk`
--

use `onap_sdk`;

--
-- Final view structure for view `function_flattener`
--

drop table if exists `function_flattener`;
drop view if exists `function_flattener`;
create view `function_flattener` as select `d`.`id` as `id`,`d`.`short_name` as `shortname`,`d`.`return_datatype` as `return_datatype`,`d`.`is_bag_return` as `is_bag_return`,`d`.`is_higher_order` as `is_higher_order`,`d`.`arg_lb` as `arg_lb`,`d`.`arg_ub` as `arg_ub`,`a1`.`is_bag` as `arg1_isbag`,`a1`.`datatype_id` as `arg1_datatype`,`a2`.`is_bag` as `arg2_isbag`,`a2`.`datatype_id` as `arg2_datatype`,`a3`.`is_bag` as `arg3_isbag`,`a3`.`datatype_id` as `arg3_datatype` from (((`functiondefinition` `d` left join `functionarguments` `a1` on(((`a1`.`function_id` = `d`.`id`) and (`a1`.`arg_index` = 1)))) left join `functionarguments` `a2` on(((`a2`.`function_id` = `d`.`id`) and (`a2`.`arg_index` = 2)))) left join `functionarguments` `a3` on(((`a3`.`function_id` = `d`.`id`) and (`a3`.`arg_index` = 3)))) order by `d`.`id`;

--
-- Final view structure for view `higherorder_bag_functions`
--

drop table if exists `higherorder_bag_functions`;
drop view if exists `higherorder_bag_functions`;
create view `higherorder_bag_functions` as select `function_flattener`.`id` as `id`,`function_flattener`.`shortname` as `shortname`,`function_flattener`.`return_datatype` as `return_datatype`,`function_flattener`.`is_bag_return` as `is_bag_return`,`function_flattener`.`is_higher_order` as `is_higher_order`,`function_flattener`.`arg_lb` as `arg_lb`,`function_flattener`.`arg_ub` as `arg_ub`,`function_flattener`.`arg1_isbag` as `arg1_isbag`,`function_flattener`.`arg1_datatype` as `arg1_datatype`,`function_flattener`.`arg2_isbag` as `arg2_isbag`,`function_flattener`.`arg2_datatype` as `arg2_datatype`,`function_flattener`.`arg3_isbag` as `arg3_isbag`,`function_flattener`.`arg3_datatype` as `arg3_datatype` from `function_flattener` where ((`function_flattener`.`is_higher_order` = 1) and (`function_flattener`.`is_bag_return` = 1) and (`function_flattener`.`return_datatype` = 18) and (`function_flattener`.`arg_lb` = 2) and (`function_flattener`.`arg_ub` = 2) and (`function_flattener`.`arg1_isbag` = 1) and ((`function_flattener`.`arg2_isbag` = 1) or isnull(`function_flattener`.`arg2_isbag`)));

--
-- Final view structure for view `match_functions`
--

drop table if exists `match_functions`;
drop view if exists `match_functions`;
create view `match_functions` as select `d`.`id` as `id`,`d`.`short_name` as `shortname`,`d`.`xacml_id` as `onap_sdkid`,`d`.`return_datatype` as `return_datatype`,`d`.`is_bag_return` as `is_bag_return`,`d`.`arg_lb` as `arg_lb`,`d`.`arg_ub` as `arg_ub`,`a1`.`is_bag` as `arg1_isbag`,`a1`.`datatype_id` as `arg1_datatype`,`a2`.`is_bag` as `arg2_isbag`,`a2`.`datatype_id` as `arg2_datatype` from ((`functiondefinition` `d` left join `functionarguments` `a1` on(((`a1`.`function_id` = `d`.`id`) and (`a1`.`arg_index` = 1)))) left join `functionarguments` `a2` on(((`a2`.`function_id` = `d`.`id`) and (`a2`.`arg_index` = 2)))) where ((`d`.`arg_lb` = 2) and (`d`.`arg_ub` = 2) and (`d`.`return_datatype` = 18) and (`a1`.`is_bag` = 0)) order by `d`.`short_name`;

--
-- Final view structure for view `v_url_access`
--

drop table if exists `v_url_access`;
drop view if exists `v_url_access`;
create view `v_url_access` as select distinct `m`.`action` as `url`,`m`.`function_cd` as `function_cd` from `fn_menu` `m` where (`m`.`action` is not null) union select distinct `t`.`action` as `url`,`t`.`function_cd` as `function_cd` from `fn_tab` `t` where (`t`.`action` is not null) union select `r`.`restricted_url` as `url`,`r`.`function_cd` as `function_cd` from `fn_restricted_url` `r`;

CREATE DATABASE IF NOT EXISTS `log`;

USE `log`;

--
-- Table structure for table `systemlogdb`
--

drop table if exists `systemlogdb`;
create table `systemlogdb` (
`id` int(11) not null auto_increment,
`system` varchar(255) not null,
`description` varchar(2048) default null,
`remote` varchar(255) not null,
`type` varchar(10) not null,
`date` timestamp not null default current_timestamp on update current_timestamp,
`logtype` varchar(255) not null,
primary key (`id`)
);

-- 
-- This is for the default data for 1610 Version of SDK database for Open Source called onap_sdk
--

USE onap_sdk;

-- fn_function
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_process','Process List');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('1','test role function');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_job','Job Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_job_create','Job Create');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_job_designer','Process in Designer view');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_task','Task Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_task_search','Task Search');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_map','Map Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_sample','Sample Pages Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_test','Test Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('login','Login');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_home','Home Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_customer','Customer Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_reports','Reports Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_profile','Profile Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_admin','Admin Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_feedback','Feedback Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_help','Help Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_logout','Logout Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_doclib','Document Library Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('doclib','Document Library');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('doclib_admin','Document Library Admin');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_notes','Notes Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_ajax','Ajax Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_customer_create','Customer Create');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_profile_create','Profile Create');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_profile_import','Profile Import');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_tab','Sample Tab Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_concept','CoNCEPT');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_itracker','iTracker Menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('view_reports','View Raptor reports');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_itracker_admin','Itracker Admin/Support menu');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('menu_policy','Policy');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_admin','Policy Admin');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_dashboard','Policy Dashboard');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_dictionary','Policy Dictionary');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_editor','Policy Editor');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_push','Policy Push');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_roles','Policy Roles');
Insert into fn_function (FUNCTION_CD,FUNCTION_NAME) values ('policy_pdp','Policy PDP');

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

-- fn_menu
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (1, 'Root', NULL, 10, NULL, 'menu_home', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', NULL); --  we need even though it's inactive
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (5000, 'Sample Pages', 1, 30, 'sample.htm', 'menu_sample', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', 'ion-android-apps');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (2, 'Home', 1, 10, 'welcome.htm', 'menu_home', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', 'ion-home');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (8, 'Reports', 1, 40, 'report.htm', 'menu_reports', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', 'ion-ios-paper');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (9, 'Profile', 1, 90, 'userProfile', 'menu_profile', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', 'ion-person');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (10, 'Admin', 1, 110, 'role_list.htm', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', 'ion-gear-a');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (13, 'Application Logout', 1, 130, 'app_logout.htm', 'menu_logout', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', 'ion-android-exit');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (84, 'All Reports', 8, 50, 'report', 'menu_reports', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/reports.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) values (87, 'Create Reports', 8, 120, 'report_wizard.htm?r_action=report.create', 'menu_reports', 'N', NULL, 'r_action=report.create', NULL, NULL, 'APP', 'N', NULL);
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) values (88, 'Sample Dashboard', 8, 130, 'report_sample', 'menu_reports', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', NULL);
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (92, 'Import User', 9, 30, 'userProfile#/post_search', 'menu_profile_import', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', NULL); 
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (94, 'Self', 9, 40, 'userProfile#/self_profile', 'menu_profile', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/profile.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (101, 'Roles', 10, 20, 'admin', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/users.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (102, 'Role Functions', 10, 30, 'admin#/role_function_list', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', NULL);
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (103, 'Broadcast Messages', 10, 50, 'admin#/broadcast_list', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/bubble.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (105, 'Cache Admin', 10, 40, 'admin#/jcs_admin', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/cache.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (108, 'Usage', 10, 80, 'admin#/usage_list', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/users.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (121, 'Collaboration', 5000, 100, 'collaborate_list.htm', 'menu_sample', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/bubble.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (930, 'Search', 9, 15, 'userProfile', 'menu_admin', 'N', NULL, NULL, NULL, NULL, 'APP', 'N', '/static/fusion/images/search_profile.png');
INSERT INTO fn_menu (MENU_ID, LABEL, PARENT_ID, SORT_ORDER, ACTION, FUNCTION_CD, ACTIVE_YN, SERVLET, QUERY_STRING, EXTERNAL_URL, TARGET, MENU_SET_CD, SEPARATOR_YN, IMAGE_SRC) VALUES (150038,'Notebook',5000,135,'notebook.htm','menu_sample','N',NULL,NULL,NULL,NULL,'APP','N',NULL);
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150004', 'Policy', '1', '1000', 'menu_policy', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150005', 'Editor', '150004', '1000', 'policy#/Editor', 'policy_editor', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150006', 'Dictionary', '150004', '1000', 'policy#/Dictionary', 'policy_dictionary', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150007', 'PDP', '150004', '1000', 'policy#/Pdp', 'policy_pdp', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150008', 'Push', '150004', '1000', 'policy#/Push', 'policy_push', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150009', 'Roles', '150004', '1000', 'policy#/Roles', 'policy_roles', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150010', 'Admin', '150004', '1000', 'policy#/Admin', 'policy_admin', 'Y', 'APP', 'N');
INSERT INTO `onap_sdk`.`fn_menu` (`menu_id`, `label`, `parent_id`, `sort_order`, `action`, `function_cd`, `active_yn`, `menu_set_cd`, `separator_yn`) VALUES ('150011', 'Dashboard', '150004', '1000', 'policy#/Dashboard', 'policy_dashboard', 'Y', 'APP', 'N');

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
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('zkau','menu_home');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('itracker_assign.htm','menu_itracker');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('itracker_byassignee.htm','menu_itracker');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('itracker_create.htm','menu_itracker');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('itracker_update.htm','menu_itracker');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('manage_license.htm','menu_itracker');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('support_ticket.htm','menu_itracker');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('jbpm_designer.htm','menu_job_create'); -- check
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('jbpm_drools.htm','menu_job_create'); -- check
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
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'doclib');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'doclib_admin');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'login');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_admin');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_ajax');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_customer');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_customer_create');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_feedback');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_help');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_home');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_itracker');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_itracker_admin');
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
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (1,'menu_test');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'login');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_ajax');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_customer');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_customer_create');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_home');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_itracker');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_logout');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_map');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_profile');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_reports');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_tab');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_admin');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_roles');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_pdp');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_dictionary');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (16,'policy_push');


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

-- fn_user
Insert into fn_user (USER_ID,ORG_ID,MANAGER_ID,FIRST_NAME,MIDDLE_NAME,LAST_NAME,PHONE,FAX,CELLULAR,EMAIL,ADDRESS_ID,ALERT_METHOD_CD,HRID,ORG_USER_ID,ORG_CODE,LOGIN_ID,LOGIN_PWD,LAST_LOGIN_DATE,ACTIVE_YN,CREATED_ID,CREATED_DATE,MODIFIED_ID,MODIFIED_DATE,IS_INTERNAL_YN,ADDRESS_LINE_1,ADDRESS_LINE_2,CITY,STATE_CD,ZIP_CODE,COUNTRY_CD,LOCATION_CLLI,ORG_MANAGER_USERID,COMPANY,DEPARTMENT_NAME,JOB_TITLE,TIMEZONE,DEPARTMENT,BUSINESS_UNIT,BUSINESS_UNIT_NAME,COST_CENTER,FIN_LOC_CODE,SILO_STATUS) values (1,null,null,'Demo',null,'User',null,null,null,null,null,null,null,'demo',null,'demo','demo',str_to_date('24-OCT-16','%d-%M-%Y'),'Y',null,str_to_date('17-OCT-16','%d-%M-%Y'),1,str_to_date('24-OCT-16','%d-%M-%Y'),'N',null,null,null,'NJ',null,'US',null,null,null,null,null,10,null,null,null,null,null,null);

-- cr_raptor_action_img
Insert into cr_raptor_action_img (IMAGE_ID, IMAGE_LOC) Values ('DELETE', '/static/fusion/raptor/img/deleteicon.gif');
Insert into cr_raptor_action_img (IMAGE_ID, IMAGE_LOC) Values ('CALENDAR', '/static/fusion/raptor/img/Calendar-16x16.png');

-- fn_app
Insert into fn_app (APP_ID,APP_NAME,APP_IMAGE_URL,APP_DESCRIPTION,APP_NOTES,APP_URL,APP_ALTERNATE_URL,APP_REST_ENDPOINT,ML_APP_NAME,ML_APP_ADMIN_ID,MOTS_ID,APP_PASSWORD,OPEN,ENABLED,THUMBNAIL,APP_USERNAME,UEB_KEY,UEB_SECRET,UEB_TOPIC_NAME) VALUES (1,'Default',null,'Some Default Description','Some Default Note',null,null,null,'ECPP','?','1','okYTaDrhzibcbGVq5mjkVQ==','N','N',null,'Default',null,null,'ONAP-PORTAL-INBOX');

-- fn_user_role
Insert into fn_user_role (USER_ID,ROLE_ID,PRIORITY,APP_ID) values (1,1,null,1);

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

INSERT INTO ConstraintType (id, constraint_type, description) VALUES (1,'Range','Set a range of min and/or max integer/double values the attribute can be set to during policy creation.');
INSERT INTO ConstraintType (id, constraint_type, description) VALUES (2,'Regular Expression','Define a regular expression the attribute must match against during policy creation.');
INSERT INTO ConstraintType (id, constraint_type, description) VALUES (3,'Enumeration','Enumerate a set of values that the attribute may be set to during policy creation.');

INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (4,'subject','S','urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject','intermediary-subject');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (5,'resource','S','urn:oasis:names:tc:xacml:3.0:attribute-category:resource','resource');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (6,'subject','S','urn:oasis:names:tc:xacml:1.0:subject-category:codebase','codebase');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (7,'action','S','urn:oasis:names:tc:xacml:3.0:attribute-category:action','action');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (8,'subject','S','urn:oasis:names:tc:xacml:1.0:subject-category:access-subject','access-subject');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (9,'environment','S','urn:oasis:names:tc:xacml:3.0:attribute-category:environment','environment');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (10,'subject','S','urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine','requesting-machine');
INSERT INTO Category (id, grouping, is_standard, xacml_id, short_name) VALUES (11,'subject','S','urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject','recipient-subject');

INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (12,'S','integer','http://www.w3.org/2001/XMLSchema#integer');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (13,'S','base64Binary','http://www.w3.org/2001/XMLSchema#base64Binary');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (14,'S','x500Name','urn:oasis:names:tc:xacml:1.0:data-type:x500Name');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (15,'S','dayTimeDuration','http://www.w3.org/2001/XMLSchema#dayTimeDuration');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (16,'S','time','http://www.w3.org/2001/XMLSchema#time');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (17,'S','dnsName','urn:oasis:names:tc:xacml:2.0:data-type:dnsName');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (18,'S','boolean','http://www.w3.org/2001/XMLSchema#boolean');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (19,'S','dateTime','http://www.w3.org/2001/XMLSchema#dateTime');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (20,'S','rfc822Name','urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (21,'S','date','http://www.w3.org/2001/XMLSchema#date');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (22,'S','ipAddress','urn:oasis:names:tc:xacml:2.0:data-type:ipAddress');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (23,'S','yearMonthDuration','http://www.w3.org/2001/XMLSchema#yearMonthDuration');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (24,'S','hexBinary','http://www.w3.org/2001/XMLSchema#hexBinary');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (25,'S','double','http://www.w3.org/2001/XMLSchema#double');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (26,'S','string','http://www.w3.org/2001/XMLSchema#string');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (27,'S','anyURI','http://www.w3.org/2001/XMLSchema#anyURI');
INSERT INTO Datatype (id, is_standard, short_name, xacml_id) VALUES (28,'S','xpathExpression','urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression');

INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (29,'S','ordered-deny-overrides','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-deny-overrides');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (30,'S','on-permit-apply-second','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:on-permit-apply-second');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (31,'S','deny-overrides','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (32,'S','permit-unless-deny','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (33,'S','deny-unless-permit','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (34,'S','permit-overrides','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (35,'S','only-one-applicable','urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:only-one-applicable');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (36,'S','first-applicable','urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable');
INSERT INTO PolicyAlgorithms (id, is_standard, short_name, xacml_id) VALUES (37,'S','ordered-permit-overrides','urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-permit-overrides');

INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (38,'S','permit-unless-deny','urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (39,'S','permit-overrides','urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (40,'S','deny-overrides','urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (41,'S','ordered-permit-overrides','urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (42,'S','deny-unless-permit','urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (43,'S','ordered-deny-overrides','urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (44,'S','only-one-applicable','urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:only-one-applicable');
INSERT INTO RuleAlgorithms (id, is_standard, short_name, xacml_id) VALUES (45,'S','first-applicable','urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable');

INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (1,'integer-greater-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:integer-greater-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (2,'integer-subtract','urn:oasis:names:tc:xacml:1.0:function:integer-subtract',12,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (3,'double-to-integer','urn:oasis:names:tc:xacml:1.0:function:double-to-integer',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (4,'integer-one-and-only','urn:oasis:names:tc:xacml:1.0:function:integer-one-and-only',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (5,'double-one-and-only','urn:oasis:names:tc:xacml:1.0:function:double-one-and-only',25,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (6,'string-equal','urn:oasis:names:tc:xacml:1.0:function:string-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (7,'string-equal-ignore-case','urn:oasis:names:tc:xacml:3.0:function:string-equal-ignore-case',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (8,'string-starts-with','urn:oasis:names:tc:xacml:3.0:function:string-starts-with',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (9,'string-ends-with','urn:oasis:names:tc:xacml:3.0:function: string-ends-with',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (10,'string-concatenate','urn:oasis:names:tc:xacml:2.0:function:string-concatenate',26,0,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (11,'boolean-from-string','urn:oasis:names:tc:xacml:3.0:function:boolean-from-string',18,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (12,'string-from-boolean','urn:oasis:names:tc:xacml:3.0:function:string-from-boolean',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (13,'integer-from-string','urn:oasis:names:tc:xacml:3.0:function:integer-from-string',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (14,'string-from-integer','urn:oasis:names:tc:xacml:3.0:function:string-from-integer',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (15,'double-from-string','urn:oasis:names:tc:xacml:3.0:function:double-from-string',25,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (16,'string-from-double','urn:oasis:names:tc:xacml:3.0:function:string-from-double',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (17,'time-from-string','urn:oasis:names:tc:xacml:3.0:function:time-from-string',16,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (18,'string-from-time','urn:oasis:names:tc:xacml:3.0:function:string-from-time',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (19,'date-from-string','urn:oasis:names:tc:xacml:3.0:function:date-from-string',21,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (20,'string-from-date','urn:oasis:names:tc:xacml:3.0:function:string-from-date',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (21,'dateTime-from-string','urn:oasis:names:tc:xacml:3.0:function:dateTime-from-string',19,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (22,'string-from-dateTime','urn:oasis:names:tc:xacml:3.0:function:string-from-dateTime',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (23,'anyURI-from-string','urn:oasis:names:tc:xacml:3.0:function:anyURI-from-string',27,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (24,'string-from-anyURI','urn:oasis:names:tc:xacml:3.0:function:string-from-anyURI',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (25,'dayTimeDuration-from-string','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-from-string',15,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (26,'string-from-dayTimeDuration','urn:oasis:names:tc:xacml:3.0:function:string-from-dayTimeDuration',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (27,'yearMonthDuration-from-string','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-from-string',23,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (28,'string-from-yearMonthDuration','urn:oasis:names:tc:xacml:3.0:function:string-from-yearMonthDuration',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (29,'x500Name-from-string','urn:oasis:names:tc:xacml:3.0:function:x500Name-from-string',14,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (30,'string-from-x500Name','urn:oasis:names:tc:xacml:3.0:function:string-from-x500Name',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (31,'rfc822Name-from-string','urn:oasis:names:tc:xacml:3.0:function:rfc822Name-from-string',20,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (32,'string-from-rfc822Name','urn:oasis:names:tc:xacml:3.0:function:string-from-rfc822Name',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (33,'ipAddress-from-string','urn:oasis:names:tc:xacml:3.0:function:ipAddress-from-string',22,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (34,'string-from-ipAddress','urn:oasis:names:tc:xacml:3.0:function:string-from-ipAddress',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (35,'dnsName-from-string','urn:oasis:names:tc:xacml:3.0:function:dnsName-from-string',17,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (36,'string-from-dnsName','urn:oasis:names:tc:xacml:3.0:function:string-from-dnsName',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (37,'anyURI-starts-with','urn:oasis:names:tc:xacml:3.0:function:anyURI-starts-with',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (38,'anyURI-ends-with','urn:oasis:names:tc:xacml:3.0:function:anyURI-ends-with',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (39,'string-contains','urn:oasis:names:tc:xacml:3.0:function:string-contains',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (40,'anyURI-contains','urn:oasis:names:tc:xacml:3.0:function:anyURI-contains',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (41,'string-substring','urn:oasis:names:tc:xacml:3.0:function:string-substring',26,0,0,3,3,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (42,'anyURI-substring','urn:oasis:names:tc:xacml:3.0:function:anyURI-substring',26,0,0,3,3,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (43,'boolean-equal','urn:oasis:names:tc:xacml:1.0:function:boolean-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (44,'integer-equal','urn:oasis:names:tc:xacml:1.0:function:integer-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (45,'double-equal','urn:oasis:names:tc:xacml:1.0:function:double-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (46,'date-equal','urn:oasis:names:tc:xacml:1.0:function:date-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (47,'time-equal','urn:oasis:names:tc:xacml:1.0:function:time-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (48,'dateTime-equal','urn:oasis:names:tc:xacml:1.0:function:dateTime-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (49,'dayTimeDuration-equal','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (50,'yearMonthDuration-equal','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (51,'anyURI-equal','urn:oasis:names:tc:xacml:1.0:function:anyURI-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (52,'x500Name-equal','urn:oasis:names:tc:xacml:1.0:function:x500Name-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (53,'rfc822Name-equal','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (54,'hexBinary-equal','urn:oasis:names:tc:xacml:1.0:function:hexBinary-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (55,'base64Binary-equal','urn:oasis:names:tc:xacml:1.0:function:base64Binary-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (56,'integer-add','urn:oasis:names:tc:xacml:1.0:function:integer-add',12,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (57,'double-add','urn:oasis:names:tc:xacml:1.0:function:double-add',25,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (58,'double-subtract','urn:oasis:names:tc:xacml:1.0:function:double-subtract',25,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (59,'integer-multiply','urn:oasis:names:tc:xacml:1.0:function:integer-multiply',12,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (60,'double-multiply','urn:oasis:names:tc:xacml:1.0:function:double-multiply',25,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (61,'integer-divide','urn:oasis:names:tc:xacml:1.0:function:integer-divide',12,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (62,'double-divide','urn:oasis:names:tc:xacml:1.0:function:double-divide',25,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (63,'integer-mod','urn:oasis:names:tc:xacml:1.0:function:integer-mod',12,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (64,'integer-abs','urn:oasis:names:tc:xacml:1.0:function:integer-abs',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (65,'double-abs','urn:oasis:names:tc:xacml:1.0:function:double-abs',25,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (66,'round','urn:oasis:names:tc:xacml:1.0:function:round',25,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (67,'floor','urn:oasis:names:tc:xacml:1.0:function:floor',25,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (68,'string-normalize-space','urn:oasis:names:tc:xacml:1.0:function:string-normalize-space',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (69,'string-normalize-to-lower-case','urn:oasis:names:tc:xacml:1.0:function:string-normalize-to-lower-case',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (70,'integer-to-double','urn:oasis:names:tc:xacml:1.0:function:integer-to-double',25,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (71,'integer-greater-than','urn:oasis:names:tc:xacml:1.0:function:integer-greater-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (72,'integer-less-than','urn:oasis:names:tc:xacml:1.0:function:integer-less-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (73,'integer-less-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:integer-less-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (74,'double-greater-than','urn:oasis:names:tc:xacml:1.0:function:double-greater-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (75,'double-greater-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:double-greater-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (76,'double-less-than','urn:oasis:names:tc:xacml:1.0:function:double-less-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (77,'double-less-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:double-less-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (78,'dateTime-add-dayTimeDuration','urn:oasis:names:tc:xacml:3.0:function:dateTime-add-dayTimeDuration',19,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (79,'dateTime-add-yearMonthDuration','urn:oasis:names:tc:xacml:3.0:function:dateTime-add-yearMonthDuration',19,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (80,'dateTime-subtract-dayTimeDuration','urn:oasis:names:tc:xacml:3.0:function:dateTime-subtract-dayTimeDuration',19,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (81,'dateTime-subtract-yearMonthDuration','urn:oasis:names:tc:xacml:3.0:function:dateTime-subtract-yearMonthDuration',19,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (82,'date-add-yearMonthDuration','urn:oasis:names:tc:xacml:3.0:function:date-add-yearMonthDuration',21,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (83,'date-subtract-yearMonthDuration','urn:oasis:names:tc:xacml:3.0:function:date-subtract-yearMonthDuration',21,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (84,'string-greater-than','urn:oasis:names:tc:xacml:1.0:function:string-greater-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (85,'string-greater-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (86,'string-less-than','urn:oasis:names:tc:xacml:1.0:function:string-less-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (87,'string-less-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (88,'time-greater-than','urn:oasis:names:tc:xacml:1.0:function:time-greater-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (89,'time-greater-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (90,'time-less-than','urn:oasis:names:tc:xacml:1.0:function:time-less-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (91,'time-less-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (92,'time-in-range','urn:oasis:names:tc:xacml:2.0:function:time-in-range',18,0,0,3,3,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (93,'dateTime-greater-than','urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (94,'dateTime-greater-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (95,'dateTime-less-than','urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (96,'dateTime-less-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (97,'date-greater-than','urn:oasis:names:tc:xacml:1.0:function:date-greater-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (98,'date-greater-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (99,'date-less-than','urn:oasis:names:tc:xacml:1.0:function:date-less-than',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (100,'date-less-than-or-equal','urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (101,'string-one-and-only','urn:oasis:names:tc:xacml:1.0:function:string-one-and-only',26,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (102,'string-bag-size','urn:oasis:names:tc:xacml:1.0:function:string-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (103,'string-is-in','urn:oasis:names:tc:xacml:1.0:function:string-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (104,'string-bag','urn:oasis:names:tc:xacml:1.0:function:string-bag',26,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (105,'integer-bag-size','urn:oasis:names:tc:xacml:1.0:function:integer-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (106,'integer-is-in','urn:oasis:names:tc:xacml:1.0:function:integer-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (107,'integer-bag','urn:oasis:names:tc:xacml:1.0:function:integer-bag',12,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (108,'double-bag-size','urn:oasis:names:tc:xacml:1.0:function:double-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (109,'double-is-in','urn:oasis:names:tc:xacml:1.0:function:double-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (110,'double-bag','urn:oasis:names:tc:xacml:1.0:function:double-bag',25,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (111,'boolean-one-and-only','urn:oasis:names:tc:xacml:1.0:function:boolean-one-and-only',18,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (112,'boolean-bag-size','urn:oasis:names:tc:xacml:1.0:function:boolean-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (113,'boolean-is-in','urn:oasis:names:tc:xacml:1.0:function:boolean-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (114,'boolean-bag','urn:oasis:names:tc:xacml:1.0:function:boolean-bag',18,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (115,'time-one-and-only','urn:oasis:names:tc:xacml:1.0:function:time-one-and-only',16,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (116,'time-bag-size','urn:oasis:names:tc:xacml:1.0:function:time-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (117,'time-is-in','urn:oasis:names:tc:xacml:1.0:function:time-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (118,'time-bag','urn:oasis:names:tc:xacml:1.0:function:time-bag',16,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (119,'date-one-and-only','urn:oasis:names:tc:xacml:1.0:function:date-one-and-only',21,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (120,'date-bag-size','urn:oasis:names:tc:xacml:1.0:function:date-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (121,'date-is-in','urn:oasis:names:tc:xacml:1.0:function:date-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (122,'date-bag','urn:oasis:names:tc:xacml:1.0:function:date-bag',21,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (123,'dateTime-one-and-only','urn:oasis:names:tc:xacml:1.0:function:dateTime-one-and-only',19,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (124,'dateTime-bag-size','urn:oasis:names:tc:xacml:1.0:function:dateTime-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (125,'dateTime-is-in','urn:oasis:names:tc:xacml:1.0:function:dateTime-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (126,'dateTime-bag','urn:oasis:names:tc:xacml:1.0:function:dateTime-bag',19,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (127,'anyURI-one-and-only','urn:oasis:names:tc:xacml:1.0:function:anyURI-one-and-only',27,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (128,'anyURI-bag-size','urn:oasis:names:tc:xacml:1.0:function:anyURI-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (129,'anyURI-is-in','urn:oasis:names:tc:xacml:1.0:function:anyURI-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (130,'anyURI-bag','urn:oasis:names:tc:xacml:1.0:function:anyURI-bag',27,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (131,'hexBinary-one-and-only','urn:oasis:names:tc:xacml:1.0:function:hexBinary-one-and-only',24,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (132,'hexBinary-bag-size','urn:oasis:names:tc:xacml:1.0:function:hexBinary-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (133,'hexBinary-is-in','urn:oasis:names:tc:xacml:1.0:function:hexBinary-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (134,'hexBinary-bag','urn:oasis:names:tc:xacml:1.0:function:hexBinary-bag',24,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (135,'base64Binary-one-and-only','urn:oasis:names:tc:xacml:1.0:function:base64Binary-one-and-only',13,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (136,'base64Binary-bag-size','urn:oasis:names:tc:xacml:1.0:function:base64Binary-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (137,'base64Binary-is-in','urn:oasis:names:tc:xacml:1.0:function:base64Binary-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (138,'base64Binary-bag','urn:oasis:names:tc:xacml:1.0:function:base64Binary-bag',13,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (139,'dayTimeDuration-one-and-only','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-one-and-only',15,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (140,'dayTimeDuration-bag-size','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (141,'dayTimeDuration-is-in','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (142,'dayTimeDuration-bag','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-bag',15,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (143,'yearMonthDuration-one-and-only','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-one-and-only',23,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (144,'yearMonthDuration-bag-size','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (145,'yearMonthDuration-is-in','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (146,'yearMonthDuration-bag','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-bag',23,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (147,'x500Name-one-and-only','urn:oasis:names:tc:xacml:1.0:function:x500Name-one-and-only',14,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (148,'x500Name-bag-size','urn:oasis:names:tc:xacml:1.0:function:x500Name-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (149,'x500Name-is-in','urn:oasis:names:tc:xacml:1.0:function:x500Name-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (150,'x500Name-bag','urn:oasis:names:tc:xacml:1.0:function:x500Name-bag',14,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (151,'rfc822Name-one-and-only','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-one-and-only',20,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (152,'rfc822Name-bag-size','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (153,'rfc822Name-is-in','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (154,'rfc822Name-bag','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-bag',20,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (155,'ipAddress-one-and-only','urn:oasis:names:tc:xacml:2.0:function:ipAddress-one-and-only',22,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (156,'ipAddress-bag-size','urn:oasis:names:tc:xacml:2.0:function:ipAddress-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (157,'ipAddress-is-in','urn:oasis:names:tc:xacml:2.0:function:ipAddress-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (158,'ipAddress-bag','urn:oasis:names:tc:xacml:2.0:function:ipAddress-bag',22,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (159,'dnsName-one-and-only','urn:oasis:names:tc:xacml:2.0:function:dnsName-one-and-only',17,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (160,'dnsName-bag-size','urn:oasis:names:tc:xacml:2.0:function:dnsName-bag-size',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (161,'dnsName-is-in','urn:oasis:names:tc:xacml:2.0:function:dnsName-is-in',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (162,'dnsName-bag','urn:oasis:names:tc:xacml:2.0:function:dnsName-bag',17,1,0,1,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (163,'string-regexp-match','urn:oasis:names:tc:xacml:1.0:function:string-regexp-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (164,'anyURI-regexp-match','urn:oasis:names:tc:xacml:2.0:function:anyURI-regexp-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (165,'ipAddress-regexp-match','urn:oasis:names:tc:xacml:2.0:function:ipAddress-regexp-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (166,'dnsName-regexp-match','urn:oasis:names:tc:xacml:2.0:function:dnsName-regexp-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (167,'rfc822Name-regexp-match','urn:oasis:names:tc:xacml:2.0:function:rfc822Name-regexp-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (168,'x500Name-regexp-match','urn:oasis:names:tc:xacml:2.0:function:x500Name-regexp-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (169,'string-intersection','urn:oasis:names:tc:xacml:1.0:function:string-intersection',26,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (170,'string-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (171,'string-union','urn:oasis:names:tc:xacml:1.0:function:string-union',26,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (172,'string-subset','urn:oasis:names:tc:xacml:1.0:function:string-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (173,'string-set-equals','urn:oasis:names:tc:xacml:1.0:function:string-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (174,'integer-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:integer-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (175,'integer-union','urn:oasis:names:tc:xacml:1.0:function:integer-union',12,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (176,'integer-subset','urn:oasis:names:tc:xacml:1.0:function:integer-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (177,'integer-set-equals','urn:oasis:names:tc:xacml:1.0:function:integer-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (178,'double-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:double-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (179,'double-union','urn:oasis:names:tc:xacml:1.0:function:double-union',25,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (180,'double-subset','urn:oasis:names:tc:xacml:1.0:function:double-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (181,'double-set-equals','urn:oasis:names:tc:xacml:1.0:function:double-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (182,'boolean-intersection','urn:oasis:names:tc:xacml:1.0:function:boolean-intersection',18,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (183,'boolean-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:boolean-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (184,'boolean-union','urn:oasis:names:tc:xacml:1.0:function:boolean-union',18,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (185,'boolean-subset','urn:oasis:names:tc:xacml:1.0:function:boolean-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (186,'boolean-set-equals','urn:oasis:names:tc:xacml:1.0:function:boolean-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (187,'time-intersection','urn:oasis:names:tc:xacml:1.0:function:time-intersection',16,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (188,'time-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:time-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (189,'time-union','urn:oasis:names:tc:xacml:1.0:function:time-union',16,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (190,'time-subset','urn:oasis:names:tc:xacml:1.0:function:time-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (191,'time-set-equals','urn:oasis:names:tc:xacml:1.0:function:time-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (192,'date-intersection','urn:oasis:names:tc:xacml:1.0:function:date-intersection',21,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (193,'date-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:date-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (194,'date-union','urn:oasis:names:tc:xacml:1.0:function:date-union',21,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (195,'date-subset','urn:oasis:names:tc:xacml:1.0:function:date-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (196,'date-set-equals','urn:oasis:names:tc:xacml:1.0:function:date-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (197,'dateTime-intersection','urn:oasis:names:tc:xacml:1.0:function:dateTime-intersection',19,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (198,'dateTime-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:dateTime-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (199,'dateTime-union','urn:oasis:names:tc:xacml:1.0:function:dateTime-union',19,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (200,'dateTime-subset','urn:oasis:names:tc:xacml:1.0:function:dateTime-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (201,'dateTime-set-equals','urn:oasis:names:tc:xacml:1.0:function:dateTime-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (202,'anyURI-intersection','urn:oasis:names:tc:xacml:1.0:function:anyURI-intersection',27,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (203,'anyURI-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:anyURI-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (204,'anyURI-union','urn:oasis:names:tc:xacml:1.0:function:anyURI-union',27,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (205,'anyURI-subset','urn:oasis:names:tc:xacml:1.0:function:anyURI-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (206,'anyURI-set-equals','urn:oasis:names:tc:xacml:1.0:function:anyURI-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (207,'hexBinary-intersection','urn:oasis:names:tc:xacml:1.0:function:hexBinary-intersection',24,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (208,'hexBinary-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:hexBinary-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (209,'hexBinary-union','urn:oasis:names:tc:xacml:1.0:function:hexBinary-union',24,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (210,'hexBinary-subset','urn:oasis:names:tc:xacml:1.0:function:hexBinary-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (211,'hexBinary-set-equals','urn:oasis:names:tc:xacml:1.0:function:hexBinary-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (212,'base64Binary-intersection','urn:oasis:names:tc:xacml:1.0:function:base64Binary-intersection',13,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (213,'base64Binary-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:base64Binary-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (214,'base64Binary-union','urn:oasis:names:tc:xacml:1.0:function:base64Binary-union',13,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (215,'base64Binary-subset','urn:oasis:names:tc:xacml:1.0:function:base64Binary-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (216,'base64Binary-set-equals','urn:oasis:names:tc:xacml:1.0:function:base64Binary-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (217,'dayTimeDuration-intersection','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-intersection',15,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (218,'dayTimeDuration-at-least-one-member-of','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (219,'dayTimeDuration-union','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-union',15,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (220,'dayTimeDuration-subset','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (221,'dayTimeDuration-set-equals','urn:oasis:names:tc:xacml:3.0:function:dayTimeDuration-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (222,'yearMonthDuration-intersection','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-intersection',23,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (223,'yearMonthDuration-at-least-one-member-of','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (224,'yearMonthDuration-union','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-union',23,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (225,'yearMonthDuration-subset','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (226,'yearMonthDuration-set-equals','urn:oasis:names:tc:xacml:3.0:function:yearMonthDuration-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (227,'x500Name-intersection','urn:oasis:names:tc:xacml:1.0:function:x500Name-intersection',14,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (228,'x500Name-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:x500Name-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (229,'x500Name-union','urn:oasis:names:tc:xacml:1.0:function:x500Name-union',14,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (230,'x500Name-subset','urn:oasis:names:tc:xacml:1.0:function:x500Name-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (231,'x500Name-set-equals','urn:oasis:names:tc:xacml:1.0:function:x500Name-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (232,'rfc822Name-intersection','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-intersection',20,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (233,'rfc822Name-at-least-one-member-of','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-at-least-one-member-of',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (234,'rfc822Name-union','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-union',20,1,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (235,'rfc822Name-subset','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-subset',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (236,'rfc822Name-set-equals','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-set-equals',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (237,'x500Name-match','urn:oasis:names:tc:xacml:1.0:function:x500Name-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (238,'rfc822Name-match','urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (239,'integer-intersection','urn:oasis:names:tc:xacml:1.0:function:integer-intersection',12,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (240,'double-intersection','urn:oasis:names:tc:xacml:1.0:function:double-intersection',25,1,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (241,'or','urn:oasis:names:tc:xacml:1.0:function:or',18,0,0,0,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (242,'and','urn:oasis:names:tc:xacml:1.0:function:and',18,0,0,0,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (243,'n-of','urn:oasis:names:tc:xacml:1.0:function:n-of',18,0,0,2,-1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (244,'not','urn:oasis:names:tc:xacml:1.0:function:not',18,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (245,'any-of','urn:oasis:names:tc:xacml:3.0:function:any-of',18,0,1,2,-1,1,-1,1);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (246,'all-of','urn:oasis:names:tc:xacml:3.0:function:all-of',18,0,1,2,-1,1,-1,1);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (247,'any-of-any','urn:oasis:names:tc:xacml:3.0:function:any-of-any',18,0,1,2,-1,1,-1,0);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (248,'all-of-any','urn:oasis:names:tc:xacml:1.0:function:all-of-any',18,0,1,3,3,2,2,1);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (249,'any-of-all','urn:oasis:names:tc:xacml:1.0:function:any-of-all',18,0,1,3,3,2,2,1);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (250,'all-of-all','urn:oasis:names:tc:xacml:1.0:function:all-of-all',18,0,1,3,3,2,2,1);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (251,'map','urn:oasis:names:tc:xacml:3.0:function:map',NULL,1,1,2,-1,1,-1,1);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (252,'xpath-node-count','urn:oasis:names:tc:xacml:3.0:function:xpath-node-count',12,0,0,1,1,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (253,'xpath-node-equal','urn:oasis:names:tc:xacml:3.0:function:xpath-node-equal',18,0,0,2,2,NULL,NULL,NULL);
INSERT INTO FunctionDefinition (id, short_name, xacml_id, return_datatype, is_bag_return, is_higher_order, arg_lb, arg_ub, ho_arg_lb, ho_arg_ub, ho_primitive) VALUES (254,'xpath-node-match','urn:oasis:names:tc:xacml:3.0:function:xpath-node-match',18,0,0,2,2,NULL,NULL,NULL);

INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (1,0,1,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (2,0,1,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (3,0,2,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (4,0,2,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (5,0,3,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (6,1,4,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (7,1,5,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (8,0,6,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (9,0,6,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (10,0,7,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (11,0,7,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (12,0,8,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (13,0,8,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (14,0,9,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (15,0,9,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (16,0,10,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (17,0,11,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (18,0,12,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (19,0,13,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (20,0,14,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (21,0,15,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (22,0,16,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (23,0,17,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (24,0,18,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (25,0,19,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (26,0,20,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (27,0,21,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (28,0,22,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (29,0,23,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (30,0,24,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (31,0,25,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (32,0,26,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (33,0,27,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (34,0,28,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (35,0,29,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (36,0,30,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (37,0,31,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (38,0,32,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (39,0,33,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (40,0,34,1,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (41,0,35,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (42,0,36,1,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (43,0,37,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (44,0,37,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (45,0,38,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (46,0,38,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (47,0,39,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (48,0,39,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (49,0,40,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (50,0,40,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (51,0,41,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (52,0,41,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (53,0,41,3,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (54,0,42,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (55,0,42,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (56,0,42,3,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (57,0,43,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (58,0,43,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (59,0,44,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (60,0,44,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (61,0,45,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (62,0,45,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (63,0,46,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (64,0,46,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (65,0,47,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (66,0,47,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (67,0,48,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (68,0,48,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (69,0,49,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (70,0,49,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (71,0,50,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (72,0,50,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (73,0,51,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (74,0,51,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (75,0,52,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (76,0,52,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (77,0,53,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (78,0,53,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (79,0,54,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (80,0,54,2,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (81,0,55,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (82,0,55,2,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (83,0,56,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (84,0,56,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (85,0,57,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (86,0,57,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (89,0,58,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (90,0,58,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (91,0,59,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (92,0,59,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (93,0,60,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (94,0,60,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (95,0,61,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (96,0,61,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (97,0,62,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (98,0,62,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (99,0,63,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (100,0,63,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (101,0,64,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (102,0,65,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (103,0,66,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (104,0,67,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (125,0,68,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (126,0,69,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (128,0,70,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (129,0,71,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (130,0,71,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (131,0,72,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (132,0,72,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (133,0,73,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (134,0,73,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (135,0,74,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (136,0,74,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (137,0,75,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (138,0,75,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (139,0,76,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (140,0,76,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (141,0,77,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (142,0,77,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (143,0,78,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (144,0,78,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (145,0,79,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (146,0,79,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (147,0,80,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (148,0,80,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (149,0,81,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (150,0,81,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (151,0,82,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (152,0,82,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (153,0,83,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (154,0,83,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (155,0,84,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (156,0,84,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (157,0,85,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (158,0,85,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (159,0,86,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (160,0,86,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (161,0,87,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (162,0,87,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (163,0,88,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (164,0,88,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (165,0,89,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (166,0,89,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (167,0,90,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (168,0,90,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (169,0,91,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (170,0,91,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (171,0,92,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (172,0,92,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (173,0,93,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (174,0,93,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (175,0,94,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (176,0,94,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (177,0,95,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (178,0,95,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (179,0,96,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (180,0,96,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (181,0,97,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (182,0,97,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (183,0,98,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (184,0,98,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (185,0,99,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (186,0,99,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (187,0,100,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (188,0,100,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (189,1,101,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (190,1,102,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (191,0,103,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (192,1,103,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (193,0,104,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (194,1,105,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (195,0,106,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (196,1,106,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (197,0,107,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (198,1,108,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (199,0,109,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (200,1,109,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (201,0,110,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (202,1,111,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (203,1,112,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (204,0,113,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (205,1,113,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (206,0,114,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (207,1,115,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (208,1,116,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (209,0,117,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (210,1,117,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (211,0,118,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (212,1,119,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (213,1,120,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (214,0,121,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (215,1,121,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (216,0,122,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (217,1,123,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (218,1,124,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (219,0,125,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (220,1,125,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (221,0,126,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (222,1,127,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (223,1,128,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (224,0,129,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (225,1,129,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (226,0,130,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (227,1,131,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (228,1,132,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (229,0,133,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (230,1,133,2,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (231,0,134,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (232,1,135,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (233,1,136,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (234,0,137,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (235,1,137,2,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (236,0,138,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (237,1,139,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (238,1,140,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (239,0,141,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (240,1,141,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (241,0,142,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (242,1,143,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (243,1,144,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (244,1,145,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (245,0,145,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (246,0,146,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (247,1,147,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (248,1,148,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (249,0,149,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (250,1,149,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (251,0,150,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (252,1,151,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (253,1,152,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (254,0,153,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (255,1,153,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (256,0,154,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (257,1,155,1,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (258,1,156,1,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (259,0,157,1,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (260,1,157,2,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (261,0,158,1,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (262,1,159,1,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (263,1,160,1,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (264,0,161,1,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (265,1,161,2,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (266,0,162,1,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (267,0,163,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (268,0,163,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (269,0,164,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (270,0,164,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (271,0,165,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (272,0,165,2,22);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (273,0,166,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (274,0,166,2,17);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (275,0,167,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (276,0,167,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (277,0,168,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (278,0,168,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (279,1,169,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (280,1,169,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (281,1,170,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (282,1,170,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (283,1,171,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (284,1,172,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (285,1,172,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (286,1,173,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (287,1,173,2,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (288,1,174,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (289,1,174,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (290,1,175,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (292,1,176,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (293,1,176,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (294,1,177,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (295,1,177,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (296,1,178,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (297,1,178,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (298,1,179,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (299,1,180,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (300,1,180,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (301,1,181,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (302,1,181,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (303,1,182,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (304,1,182,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (305,1,183,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (306,1,183,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (307,1,184,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (308,1,185,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (309,1,185,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (310,1,186,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (311,1,186,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (312,1,187,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (313,1,187,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (314,1,188,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (315,1,188,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (316,1,189,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (317,1,190,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (318,1,190,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (319,1,191,1,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (320,1,191,2,16);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (321,1,192,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (322,1,192,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (323,1,193,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (324,1,193,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (325,1,194,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (326,1,195,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (327,1,195,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (328,1,196,1,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (329,1,196,2,21);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (330,1,197,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (331,1,197,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (332,1,198,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (333,1,198,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (334,1,199,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (335,1,200,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (336,1,200,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (337,1,201,1,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (338,1,201,2,19);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (339,1,202,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (340,1,202,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (341,1,203,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (342,1,203,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (343,1,204,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (344,1,205,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (345,1,205,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (346,1,206,1,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (347,1,206,2,27);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (348,1,207,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (349,1,207,2,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (350,1,208,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (351,1,208,2,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (352,1,209,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (353,1,210,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (354,1,210,2,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (355,1,211,1,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (356,1,211,2,24);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (357,1,212,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (358,1,212,2,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (359,1,213,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (360,1,213,2,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (361,1,214,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (362,1,215,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (363,1,215,2,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (364,1,216,1,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (365,1,216,2,13);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (366,1,217,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (367,1,217,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (368,1,218,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (369,1,218,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (370,1,219,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (371,1,220,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (372,1,220,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (373,1,221,1,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (374,1,221,2,15);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (375,1,222,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (376,1,222,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (377,1,223,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (378,1,223,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (380,1,224,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (381,1,225,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (382,1,225,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (383,1,226,2,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (384,1,226,1,23);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (385,1,227,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (386,1,227,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (387,1,228,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (388,1,228,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (389,1,229,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (390,1,230,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (391,1,230,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (392,1,231,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (393,1,231,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (394,1,232,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (395,1,232,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (396,1,233,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (397,1,233,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (398,1,234,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (399,1,235,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (400,1,235,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (401,1,236,1,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (402,1,236,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (403,0,237,1,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (404,0,237,2,14);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (405,0,238,1,26);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (406,0,238,2,20);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (407,1,239,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (408,1,239,2,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (409,1,240,1,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (410,1,240,2,25);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (411,0,241,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (412,0,242,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (413,0,243,1,12);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (414,0,243,2,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (415,0,244,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (416,0,245,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (417,0,245,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (418,1,245,3,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (419,0,246,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (420,0,246,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (421,1,246,3,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (422,0,247,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (423,1,247,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (424,1,247,3,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (433,0,248,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (434,1,248,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (435,1,248,3,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (436,0,249,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (437,1,249,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (438,1,249,3,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (439,0,250,1,18);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (440,1,250,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (441,1,250,3,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (442,0,251,1,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (447,1,251,2,NULL);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (448,0,252,1,28);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (449,0,253,1,28);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (450,0,253,2,28);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (451,0,254,1,28);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (452,0,254,2,28);
INSERT INTO FunctionArguments (id, is_bag, function_id, arg_index, datatype_id) VALUES (453,0,92,3,16);

INSERT INTO PIPType VALUES (500,'SQL'), (501,'LDAP'), (502,'CSV'), (503,'Hyper-CSV'), (504,'Custom');

INSERT INTO GlobalRoleSettings (role, lockdown) values ('super-admin', '0');

INSERT INTO `onap_sdk`.`roles` (`id`, `loginid`, `role`) VALUES ('2', 'API', 'super-admin');
INSERT INTO `onap_sdk`.`roles` (`id`, `loginid`, `role`) VALUES ('3', 'demo', 'super-admin');

INSERT INTO `onap_sdk`.`USERINFO` (`loginid`, `NAME`) VALUES ('API', 'API');
INSERT INTO `onap_sdk`.`USERINFO` (`loginid`, `NAME`) VALUES ('demo', 'Demo');

INSERT INTO `onap_sdk`.`dcaeuuid` (`ID`, `name`, `description`) VALUES ('2', '/services/cdap-tca-hi-lo/instances/demo/configuration/metricsPerFunctionalRole/vLoadBalancer', 'vLoadBalancer');
INSERT INTO `onap_sdk`.`dcaeuuid` (`ID`, `name`, `description`) VALUES ('3', '/services/cdap-tca-hi-lo/instances/demo/configuration/metricsPerFunctionalRole/vFirewall', 'vFirewall');

INSERT INTO `onap_sdk`.`microservicemodels` (`id`, `modelname`, `description`, `dependency`, `imported_by`, `attributes`, `ref_attributes`, `sub_attributes`, `version`) VALUES ('1', 'TcaMetrics', 'TCA Metrics test', '[NamedEntity,  DcaePolicyEntity]', 'API', 'functionalRole=java.lang.String:defaultValue-NA:required-false:MANY-false, name=java.lang.String:defaultValue-NA:required-false:MANY-false', 'thresholds=TcaThreshold:MANY-true', '{\"TcaThreshold\":{\"severity\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"fieldPath\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"thresholdValue\":\"java.lang.Integer:defaultValue-NA:required-false:MANY-false\",\"closedLoopControlName\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"version\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"direction\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\"}}', '1.0.0.5');
INSERT INTO `onap_sdk`.`microservicemodels` (`id`, `modelname`, `description`, `dependency`, `imported_by`, `ref_attributes`, `sub_attributes`, `version`) VALUES ('2', 'StringMatchingConfiguration', 'StringMatchingConfiguration', '[CdapServiceInstanceConfiguration]', 'API', 'serviceConfigurations=StringMatchingServiceConfiguration:MANY-true', '{\"StringMatchingServiceConfiguration\":{\"ageLimit\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"timeWindow\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"createClosedLoopEventId\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"aaiSendFields\":\"java.lang.String:defaultValue-NA:required-false:MANY-true\",\"stringSet\":\"java.lang.String:defaultValue-NA:required-false:MANY-true\",\"outputEventName\":\"java.lang.String:defaultValue-NA:required-false:MANY-false\",\"aaiMatchingFields\":\"java.lang.String:defaultValue-NA:required-false:MANY-true\"}}', '1.0.0.3');

INSERT INTO `onapname` VALUES (1,'demo','2016-12-28 14:23:31','DCAE','DCAE','demo','2016-12-28 14:23:31');
INSERT INTO `onapname` VALUES (2,'demo','2016-12-28 14:23:31','test','SampleDemo','demo','2016-12-28 14:23:31');

INSERT INTO `safepolicywarning` VALUES (1,'demo','SampleRiskType','SampleSafePolicyWarning');

INSERT INTO `policyscopeclosedloop` VALUES (1,'SampleClosedLoop','demo');

INSERT INTO `policyscoperesource` VALUES (1,'SampleResource','demo');

INSERT INTO `policyscopeservice` VALUES (1,'SampleService','demo');

INSERT INTO `policyscopetype` VALUES (1,'SampleType','demo');

INSERT INTO `grouppolicyscopelist` VALUES (1,'PolicyScope_SampleGroupPolicyScope','resource=SampleResource,service=SampleService,type=SampleType,closedLoopControlName=SampleClosedLoop','demo');

INSERT INTO `microserviceconfigname` VALUES (1,'SampleConfigName','demo');

INSERT INTO `microservicelocation` VALUES (1,'SampleServiceLocation','demo');

INSERT INTO `risktype` VALUES (1,'demo','2016-12-29 20:20:48','demo','SampleRiskType','demo','2016-12-29 20:20:48');

insert into sequence (seq_name, seq_count) values ('SEQ_GEN', 3050); 

set foreign_key_checks=1; 
commit; 
