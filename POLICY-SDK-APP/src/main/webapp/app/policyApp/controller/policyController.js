/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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
angular.module('abs').requires.push('ui.grid','ui.grid.pagination','ui.grid.selection', 'ui.grid.exporter', 'ui.grid.edit', 'ui.grid.autoResize',
	'ui.grid.resizeColumns','ngRoute', 'pascalprecht.translate', 'ngCookies', 'ui-notification', 'ui.grid.treeView');
app.config(function($routeProvider) {
	$routeProvider
	.when('/Editor', {
		templateUrl:'app/policyApp/policy-models/Editor/templates/policyEditor.html',
		controller : "PolicyManagerController"
	})
	.when('/Dictionary', {
		templateUrl: 'app/policyApp/policy-models/policy_Dictionary.html',
		controller : "dictionaryTabController"
	})
	.when('/Pdp', {
		templateUrl: 'app/policyApp/policy-models/policy_PDPManagement.html',
		controller : "pdpTabController"
	})
	.when('/Push', {
		templateUrl: 'app/policyApp/policy-models/policy_AutoPush.html',
		controller : "policyPushController"
	})
	.when('/Admin', {
		templateUrl: 'app/policyApp/policy-models/policy_AdminTab.html',
		controller : "policyAdminController"
	})
	.when('/Roles', {
		templateUrl: 'app/policyApp/policy-models/policy_Roles.html',
		controller : "policyRolesController"
	})
	.when('/Dashboard', {
		templateUrl: 'app/policyApp/policy-models/policy_DashboardLogging.html',
		controller : "policyDashboardController"
	})
	.when('/Dashboard_Health', {
		templateUrl: 'app/policyApp/policy-models/policy_DashboardHealth.html',
		controller : "policyDashboardHealthController"
	})
	.when('/Dashboard_crud', {
		templateUrl: 'app/policyApp/policy-models/policy_DashboardCRUD.html',
		controller : "policyDashboardCRUDDataController"
	})	
	.when('/policy_SearchFilter', {
		templateUrl: 'app/policyApp/policy-models/policy_SearchFilter.html',
		controller : "PolicySearchController"
	})
	.otherwise({
		templateUrl:'app/policyApp/policy-models/Editor/templates/policyEditor.html',
		controller : "PolicyManagerController"	
	});
	
});