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

(function(angular) {
    'use strict';

    angular.module('abs').provider('fileManagerConfig', function() {

        var values = { 
            appName: 'Policy Editor',
            defaultLang: 'en',
            listUrl: 'fm/listUrl',
            uploadUrl: 'fm/uploadUrl',
            renameUrl: 'fm/renameUrl',
            copyUrl: 'fm/copyUrl',
            removeUrl: 'fm/removeUrl',
            editUrl: 'fm/editUrl',
            getContentUrl: 'fm/getContentUrl',
            createFolderUrl: 'fm/createFolderUrl',
            downloadFileUrl: 'fm/downloadFileUrl',
            compressUrl: 'fm/compressUrl',
            extractUrl: 'fm/extractUrl',
            permissionsUrl: 'fm/permissionsUrl',
            describePolicyUrl : 'fm/describePolicyUrl',
            viewPolicyUrl : 'fm/viewPolicyUrl',
            addSubScopeUrl : 'fm/addSubScopeUrl',
            switchVersionUrl : 'fm/switchVersionUrl',
            exportUrl : 'fm/exportUrl',

            sidebar: true,
            breadcrumb: true,
            allowedActions: {
                upload: true,
                rename: true,
                copy: true,
                edit: true,
                describePolicy: true,
                createNewPolicy: true,
                viewPolicy: true,
                changePermissions: true,
                compress: true,
                compressChooseName: true,
                extract: true,
                download: true,
                preview: true,
                remove: true,
                addSubScope : true,
                switchVersion : true,
                exportPolicy : true,
                removePolicy : true
            },

            enablePermissionsRecursive: true,
            compressAsync: true,
            extractAsync: true,

            isEditableFilePattern: /\.(txt|html?|aspx?|ini|pl|py|md|css|js|log|htaccess|htpasswd|json|sql|xml|xslt?|sh|rb|as|bat|cmd|coffee|php[3-6]?|java|c|cbl|go|h|scala|vb)$/i,
            isImageFilePattern: /\.(jpe?g|gif|bmp|png|svg|tiff?)$/i,
            isExtractableFilePattern: /\.(gz|tar|rar|g?zip)$/i,
            tplPath: 'app/policyApp/policy-models/Editor/src/templates'
        };

        return {
            $get: function() {
                return values;
            },
            set: function (constants) {
                angular.extend(values, constants);
            }
        };
    
    });
})(angular);
