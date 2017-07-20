/*-
* ============LICENSE_START=======================================================
* ONAP Policy Engine
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
use onap_sdk;
drop table if exists `onap_sdk`.`rainydaytreatments`;

CREATE TABLE `onap_sdk`.`rainydaytreatments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bbid` varchar(250) DEFAULT NULL,
  `workstep` varchar(250) DEFAULT NULL,
  `treatments` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
);