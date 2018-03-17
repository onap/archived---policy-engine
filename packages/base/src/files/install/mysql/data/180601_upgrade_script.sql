/*-
* ============LICENSE_START=======================================================
* ONAP Policy Engine
* ================================================================================
* Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
use onap_sdk;

drop table if exists `optimizationmodels`;
create table `onap_sdk`.`optimizationmodels` (
`id` int(11) not null auto_increment,
`modelname` varchar(767) not null,
`description` varchar(1024) default null,
`dependency` varchar(1024) default null,
`imported_by` varchar(45) not null,
`attributes` longtext,
`ref_attributes` longtext,
`sub_attributes` longtext,
`version` varchar(45) default null,
`annotation` longtext,
`enumValues` longtext, 
primary key (`id`),
unique key `optimizationmodels_uniq` (`modelname`,`version`)
);