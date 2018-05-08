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

use onap_sdk;

alter table configurationdataentity modify configBody mediumtext;

insert into fn_restricted_url values('admin','menu_admin');
insert into fn_restricted_url values('get_role','menu_admin');
insert into fn_restricted_url values('get_role_functions','menu_admin');
insert into fn_restricted_url values('role_list/*','menu_admin');
insert into fn_restricted_url values('role_function_list/*','menu_admin');
insert into fn_restricted_url values('addRole','menu_admin');
insert into fn_restricted_url values('addRoleFunction','menu_admin');
insert into fn_restricted_url values('removeRole','menu_admin');
insert into fn_restricted_url values('removeRoleFunction','menu_admin');
insert into fn_restricted_url values('profile/*','menu_admin');

insert into fn_restricted_url values('welcome.htm','menu_home');
insert into fn_restricted_url values('policy','menu_home');
insert into fn_restricted_url values('policy','menu_policy');
insert into fn_restricted_url values('get_RolesData','menu_home');
insert into fn_restricted_url values('get_LockDownData','menu_home');
insert into fn_restricted_url values('adminTabController/*','menu_home');
insert into fn_restricted_url values('get_AutoPushPoliciesContainerData','menu_home');
insert into fn_restricted_url values('auto_Push/*','menu_home');
insert into fn_restricted_url values('get_PDPGroupData','menu_home');
insert into fn_restricted_url values('pdp_Group/*','menu_home');
insert into fn_restricted_url values('policy_download/*','menu_home');
insert into fn_restricted_url values('watchPolicy','menu_home');
insert into fn_restricted_url values('save_NonSuperRolesData','menu_home');
insert into fn_restricted_url values('get_PolicyRolesScopeData','menu_home');
insert into fn_restricted_url values('policyController/*','menu_home');
insert into fn_restricted_url values('get_FunctionDefinitionDataByName','menu_home');
insert into fn_restricted_url values('get_DashboardLoggingData','menu_home');
insert into fn_restricted_url values('get_DashboardSystemAlertData','menu_home');
insert into fn_restricted_url values('get_DashboardPDPStatusData','menu_home');
insert into fn_restricted_url values('get_DashboardPolicyActivityData','menu_home');
insert into fn_restricted_url values('get_DCAEPriorityValues','menu_home');
insert into fn_restricted_url values('ms_dictionary/*','menu_home');
insert into fn_restricted_url values('policycreation/*','menu_home');
insert into fn_restricted_url values('getDictionary/*','menu_home');
insert into fn_restricted_url values('saveDictionary/*/*','menu_home');
insert into fn_restricted_url values('deleteDictionary/*/*','menu_home');
insert into fn_restricted_url values('searchDictionary','menu_home');
insert into fn_restricted_url values('searchPolicy','menu_home');
insert into fn_restricted_url values('get_PolicyUserInfo','menu_home');
