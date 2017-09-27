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

INSERT INTO `onap_sdk`.`microservicemodels` (`id`, `modelName`, `DESCRIPTION`, `Dependency`, `imported_by`, `attributes`, `ref_attributes`, `sub_attributes`, `version`) VALUES ('3', 'policy_tosca_tca', 'Micro Service Policy Tosca model', '[]', 'API', 'policyVersion=string:defaultValue-:required-true:MANY-false,policyName=string:defaultValue-:required-true:MANY-false,controlLoopSchemaType=string:defaultValue-:required-true:MANY-false,policyScope=string:defaultValue-:required-true:MANY-false,eventName=string:defaultValue-:required-true:MANY-false,', 'threshholds=Threshold:MANY-true,', '{\"Threshold\":{\"severity\":\"string:defaultValue-null:required-true:MANY-false\",\"fieldPath\":\"string:defaultValue-null:required-true:MANY-false\",\"thresholdValue\":\"integer:defaultValue-null:required-true:MANY-false\",\"closedLoopEventStatus\":\"string:defaultValue-null:required-true:MANY-false\",\"version\":\"string:defaultValue-1.0.2:required-true:MANY-false\",\"closedLoopControlName\":\"string:defaultValue-null:required-true:MANY-false\",\"direction\":\"string:defaultValue-null:required-true:MANY-false\"}}', '1.0.0');
