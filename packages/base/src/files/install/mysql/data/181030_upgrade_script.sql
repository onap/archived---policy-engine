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

INSERT INTO `onap_sdk`.`optimizationmodels` (`modelname`, `description`, `dependency`, `imported_by`, `attributes`, `ref_attributes`, `sub_attributes`, `version`, `annotation`, `enumValues`, `dataOrderInfo`) VALUES ('hpaPolicy', 'HPA Tests Model', '[]', 'demo', 'identity=string:defaultValue-null:required-true:MANY-false,resources=string:defaultValue-null:required-true:MANY-false', 'policyScope=MANY-true,policyType=POLICYTYPE1:MANY-false,flavorFeatures=flavorFeatures-properties:MANY-true', '{\"flavorProperties-properties\":{\"hpa-feature-attributes\":\"hpa-feature-attributes-properties:required-false:MANY-true\",\"mandatory\":\"string:defaultValue-null:required-true:MANY-false\",\"hpa-feature\":\"string:defaultValue-null:required-true:MANY-false\",\"hpa-version\":\"string:defaultValue-null:required-true:MANY-false\",\"architecture\":\"string:defaultValue-null:required-true:MANY-false\",\"directives\":\"directives-properties:required-false:MANY-true\"},\"directives-properties\":{\"type\":\"string:defaultValue-flavor_directives:required-false:MANY-false\",\"attributes\":\"directives-attributes-properties:MANY-false\"},\"directives-attributes-properties\":{\"attribute_name":\"MANY-false\",\"attribute_value\":\"MANY-false\"},\"flavorFeatures-properties\":{\"flavorProperties\":\"flavorProperties-properties:required-false:MANY-true\",\"id\":\"string:defaultValue-null:required-true:MANY-false\",\"type\":\"string:defaultValue-tosca.nodes.nfv.Vdu.Compute:required-true:MANY-false\"},\"hpa-feature-attributes-properties\":{\"unit\":\"string:defaultValue-null:required-false:MANY-false\",\"hpa-attribute-key\":\"string:defaultValue-null:required-false:MANY-false\",\"hpa-attribute-value\":\"string:defaultValue-null:required-true:MANY-false\",\"operator\":\"OPERATOR:required-false:MANY-false\"}}\n', '1.0', 'policyScope=matching-true', 'OPERATOR=[<,<equal-sign,>,>equal-sign,equal-sign,!equal-sign,any,all,subset,], POLICYTYPE1=[hpaPolicy]', '\"[resources,identity,policyScope,policyType,flavorFeatures,id,type,flavorProperties,hpa-feature,mandatory,architecture,hpa-version,directives,type,attributes,attribute_name,attribute_value,hpa-feature-attributes,hpa-attribute-key,operator,unit,hpa-attribute-value]\"');
