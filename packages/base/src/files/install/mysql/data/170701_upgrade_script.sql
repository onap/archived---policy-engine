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

INSERT INTO policyeditorscopes (`id`, `scopename`, `created_date`, `created_by`, `modified_date`, `modified_by`) VALUES ('1', 'com', '2017-06-01 11:45:36', 'demo', '2017-06-01 11:45:36', 'demo');

alter table integrityauditentity modify jdbcUrl varchar(200) not null;

alter table `onap_sdk`.`microservicemodels`
add column `enumValues` longtext null default null after `version`,
add column `annotation` longtext null after `enumValues`;

drop table if exists FwTag;
CREATE TABLE FwTag(
Id int NOT NULL AUTO_INCREMENT,
tagName VARCHAR(45) NOT NULL,
description VARCHAR(1024),
tagValues VARCHAR(1024) NOT NULL,
CREATED_DATE TIMESTAMP NOT NULL default current_timestamp,
CREATED_BY VARCHAR(45) NOT NULL,
MODIFIED_DATE TIMESTAMP NOT NULL,
MODIFIED_BY VARCHAR(45) NOT NULL,
PRIMARY KEY(ID)
);

drop table if exists FwTagPicker;
CREATE TABLE FwTagPicker(
ID INT NOT NULL AUTO_INCREMENT,
tagPickerName VARCHAR(45) NOT NULL,
DESCRIPTION VARCHAR(1024),
tags VARCHAR(1024) NOT NULL,
networkRole varchar(64),
CREATED_DATE TIMESTAMP NOT NULL default current_timestamp,
CREATED_BY VARCHAR(45) NOT NULL,
MODIFIED_DATE TIMESTAMP NOT NULL,
MODIFIED_BY VARCHAR(45) NOT NULL,
PRIMARY KEY(ID)
);

drop table if exists brmsdependency;
CREATE TABLE brmsdependency (
id int not null auto_increment,
dependency_name varchar(1024) not null,
description varchar(1024),
created_by varchar(45) not null,
created_date timestamp not null default current_timestamp,
modified_by varchar(45),
modified_date timestamp,
dependency longtext not null,
primary key(id)
);

drop table if exists brmscontroller;
CREATE TABLE brmscontroller (
id int not null auto_increment,
controller_name varchar(1024) not null,
description varchar(1024),
created_by varchar(45) not null,
created_date timestamp not null default current_timestamp,
modified_by varchar(45),
modified_date timestamp,
controller longtext not null,
primary key(id)
);

drop table if exists microserviceattribute;
CREATE TABLE microserviceattribute(
ID INT NOT NULL AUTO_INCREMENT,
name VARCHAR(255) NOT NULL,
value VARCHAR(1024),
modelName VARCHAR(1024) NOT NULL,
PRIMARY KEY(ID)
);
