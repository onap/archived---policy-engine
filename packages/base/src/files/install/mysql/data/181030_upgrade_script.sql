-- ============LICENSE_START=======================================================
-- ONAP Policy Engine
-- ================================================================================
-- Copyright (C) 2018 Intel. All rights reserved.
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

INSERT INTO `onap_sdk`.`optimizationmodels` (`modelname`, `description`, `dependency`, `imported_by`, `attributes`, `ref_attributes`, `sub_attributes`, `version`, `annotation`, `enumValues`, `dataOrderInfo`) VALUES ('hpaPolicy', 'HPA Tests Model', '[]', 'demo', 'identity=string:defaultValue-null:required-true:MANY-false:description-null', 'policyScope=MANY-true,policyType=POLICYTYPE:MANY-false,resources=MANY-true,flavorFeatures=flavorFeatures_properties:MANY-true:description-null', '{"directives_properties":{"attributes":"directives_attributes_properties:required-false:MANY-true:description-null","type":"string:defaultValue-null:required-false:MANY-false:description-null"},"directives_attributes_properties":{"attribute_name":"string:defaultValue-null:required-false:MANY-false:description-null","attribute_value":"string:defaultValue-null:required-false:MANY-false:description-null"},"flavorProperties_properties":{"score":"string:defaultValue-null:required-false:MANY-false:description-null","hpa-feature-attributes":"hpa-feature-attributes_properties:required-true:MANY-true:description-null","directives":"directives_properties:required-true:MANY-true:description-null","hpa-version":"string:defaultValue-null:required-true:MANY-false:description-null","hpa-feature":"string:defaultValue-null:required-true:MANY-false:description-null","mandatory":"string:defaultValue-null:required-true:MANY-false:description-null","architecture":"string:defaultValue-null:required-true:MANY-false:description-null"},"flavorFeatures_properties":{"directives":"directives_properties:required-true:MANY-true:description-null","flavorProperties":"flavorProperties_properties:required-true:MANY-true:description-null","id":"string:defaultValue-null:required-true:MANY-false:description-null","type":"string:defaultValue-null:required-true:MANY-false:description-null"},"hpa-feature-attributes_properties":{"unit":"string:defaultValue-null:required-false:MANY-false:description-null","hpa-attribute-value":"string:defaultValue-null:required-true:MANY-false:description-null","hpa-attribute-key":"string:defaultValue-null:required-true:MANY-false:description-null","operator":"OPERATOR:defaultValue-null:required-false:MANY-false:description-null"}}', '1.0', 'policyScope=matching-true, policyType=matching-true', 'OPERATOR=[<,<equal-sign,>,>equal-sign,equal-sign,!equal-sign,any,all,subset,], POLICYTYPE=[hpa,]', '""');
