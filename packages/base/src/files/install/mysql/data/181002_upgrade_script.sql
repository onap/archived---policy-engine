-- ============LICENSE_START=======================================================
-- ONAP Policy Engine
-- ================================================================================
-- Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

alter table fn_function
add type VARCHAR(20) NOT NULL;

alter table fn_function
add action VARCHAR(20) NOT NULL;

ALTER TABLE fn_function
ADD CONSTRAINT function UNIQUE (FUNCTION_CD,TYPE,ACTION);

delete from fn_function where function_cd='1';

update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_process';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_map';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_sample';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_home';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_customer';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_reports';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_admin';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_feedback';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_help';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_logout';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_notes';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_ajax';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_customer_create';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_tab';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_test';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_task';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_task_search';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_profile';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_profile_create';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_profile_import';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_hiveconfig';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_hiveconfig_search';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_hiveconfig_create';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_doclib';
update fn_function set type = 'menu' , action = '*' where function_cd = 'doclib';
update fn_function set type = 'menu' , action = '*' where function_cd = 'doclib_admin';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_concept';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_itracker';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_itracker_admin';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_job';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_job_create';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_job_designer';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_mapreduce';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_mapreduce_create';
update fn_function set type = 'menu' , action = '*' where function_cd = 'menu_mapreduce_search';
update fn_function set type = 'menu' , action = '*' where function_cd = 'view_reports';
update fn_function set type = 'url'  , action = '*' where function_cd = 'login';
update fn_function set type = 'url'  , action = '*' where function_cd = 'quantum_bd';
    
commit;