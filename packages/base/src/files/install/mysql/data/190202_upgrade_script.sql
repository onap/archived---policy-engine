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
CREATE TABLE policyAuditlog (
    id int auto_increment NOT NULL,
    userName varchar(100) NOT NULL,
    policyName varchar(255) NOT NULL,
    actions varchar(50) NOT NULL,
    dateAndTime datetime NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE microservicemodels
ADD ruleFormation VARCHAR(45) DEFAULT NULL,
ADD COLUMN `decisionModel` tinyint(1) DEFAULT NULL;

CREATE TABLE `onap_sdk`.`dictionaryData` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `dictionaryName` varchar(64) NOT NULL,
 `dictionaryUrl` varchar(64) NOT NULL,
 `dictionaryDataByName` varchar(64) NOT NULL,
 PRIMARY KEY (`id`) 
 );
 
INSERT INTO dictionaryData (dictionaryName, dictionaryUrl, dictionaryDataByName)
VALUES ('GocVNFType', 'getDictionary/get_GocVnfTypeDataByName', 'gocVnfTypeDictionaryDatas'),
('ServerScope','getDictionary/get_ServerScopeDataByName','gocServerScopeDictionaryDatas'),
('TraversalData', 'getDictionary/get_TraversalDataByName', 'gocTraversalDictionaryDatas'); 

INSERT INTO `onap_sdk`.`fn_user_role` (`USER_ID`, `ROLE_ID`, `APP_ID`) VALUES ('1 ', '16', '1');
