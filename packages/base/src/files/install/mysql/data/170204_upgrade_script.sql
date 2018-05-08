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
set foreign_key_checks=0;
drop table if exists brmsgroup_info;
CREATE TABLE brmsgroup_info
(
controllerName VARCHAR(255) NOT NULL,
groupId VARCHAR(255) NOT NULL,
artifactId VARCHAR(255) NOT NULL,
version VARCHAR(45),
PRIMARY KEY (controllerName)
);
drop table if exists brmsgroup_policy;
CREATE TABLE brmsgroup_policy
(
policyName VARCHAR(255) NOT NULL,
controllerName VARCHAR(255) NOT NULL references brmsgroup_info(controllerName),
PRIMARY KEY(policyname)
);

drop table if exists operationshistory10; 

create table operationshistory10(
CLNAME varchar(255) not null, 
requestID varchar(100),
actor varchar(50) not null,
operation varchar(50) not null,
target varchar(50) not null,
starttime timestamp not null,
outcome varchar(50) not null,
message varchar(255) ,
subrequestId varchar(100),
endtime timestamp not null default current_timestamp
);

Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (5002,'Policy Super Admin','Y',10);
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (5003,'Policy Super Editor','Y',10);
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (5004,'Policy Super Guest','Y',10);
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (5005,'Policy Admin','Y',10);
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (5006,'Policy Editor','Y',10);
Insert into fn_role (ROLE_ID,ROLE_NAME,ACTIVE_YN,PRIORITY) values (5007,'Policy Guest','Y',10);


Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_admin');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_roles');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_pdp');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_dictionary');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5002,'policy_push');

Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5003,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5003,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5003,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5003,'policy_pdp');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5003,'policy_push');

Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5004,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5004,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5004,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5004,'policy_pdp');

Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5005,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5005,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5005,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5005,'policy_pdp');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5005,'policy_dictionary');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5005,'policy_push');

Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5006,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5006,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5006,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5006,'policy_pdp');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5006,'policy_push');

Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5007,'menu_policy');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5007,'policy_dashboard');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5007,'policy_editor');
Insert into fn_role_function (ROLE_ID,FUNCTION_CD) values (5007,'policy_pdp');

set foreign_key_checks=0;

commit;
