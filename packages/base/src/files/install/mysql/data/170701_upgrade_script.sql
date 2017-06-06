/*-
* ============LICENSE_START=======================================================
* ECOMP Policy Engine
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
use ecomp_sdk;
ALTER TABLE fwtagpicker add networkRole varchar(64);

INSERT INTO policyeditorscopes (`id`, `scopename`, `created_date`, `created_by`, `modified_date`, `modified_by`) VALUES ('1', 'com', '2017-06-01 11:45:36', 'demo', '2017-06-01 11:45:36', 'demo');
