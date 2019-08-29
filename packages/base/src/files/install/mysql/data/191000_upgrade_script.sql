-- ============LICENSE_START=======================================================
-- ONAP Policy Engine
-- ================================================================================
-- Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
use onap_sdk;

SET FOREIGN_KEY_CHECKS=0;

-- Changes for epsdk 2.5.0 version
update fn_menu set action = 'welcome' where action = 'welcome.htm';

-- Changes for epsdk 2.6.0 version upgrade
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('report/wizard/add_formfield_tab_data/*','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('report/wizard/save_formfield_tab_data/*','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('report/wizard/retrieve_form_tab_wise_data/*/delete','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('report/wizard/list_child_report_col/*','menu_reports');
INSERT INTO fn_restricted_url (restricted_url, function_cd) VALUES ('report/wizard/list_child_report_ff/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('serviceModels','serviceModels ');
INSERT INTO fn_restricted_url VALUES('serviceModels','menu_admin');
INSERT INTO fn_restricted_url VALUES('report/wizard/list_columns','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/list_formfields','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/retrieve_data/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/retrieve_col_tab_wise_data/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('welcome','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/security/addReportUser','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/security/addReportRole','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/security/*','menu_reports');
INSERT INTO fn_restricted_url VALUES('report/wizard/get_report_log/*','menu_reports');

