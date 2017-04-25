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
(function(window, angular, $) {
    'use strict';
    angular.module('abs').requires.push('ngRoute', 'modalServices', 'pascalprecht.translate', 'ngCookies', 'ngSanitize', 'ui-notification',
            'ui.grid','ui.grid.pagination','ui.grid.selection', 'ui.grid.exporter', 'ui.grid.edit', 'ui.grid.autoResize','b2b.att',
          	'ui.grid.resizeColumns', 'ui.grid.treeView');
    var app = appDS2;
    
    app.config(['policyManagerConfigProvider', function (config) {
    	var defaults = config.$get();
    	config.set({
    		appName: 'Policy Editor',
    		cache: false,
    		allowedActions: angular.extend(defaults.allowedActions, {
    			remove: true
    		})
    	});
    }]);
    /**
     * jQuery inits
     */
    $(window.document).on('shown.bs.modal', '.modal', function() {
        window.setTimeout(function() {
            $('[autofocus]', this).focus();
        }.bind(this), 100);
    });

    $(window.document).on('click', function() {
        $('#context-menu').hide();
    });

    $(window.document).on('contextmenu', '.main-navigation .table-files td:first-child, .iconset a.thumbnail', function(e) {
        $('#context-menu').hide().css({
            left: e.pageX,
            top: e.pageY
        }).show();
        e.preventDefault();
    });

})(window, angular, jQuery);
