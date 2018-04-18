/*
 * ============LICENSE_START==================================================
 * ONAP Policy Engine
 * ===========================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ===========================================================================
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
 * ============LICENSE_END====================================================
 */

appDS2.config(function($routeProvider) {
	$routeProvider
	.when('/collaborate_list', {
		templateUrl: 'app/fusion/scripts/DS2-view-models/ds2-admin/collaborate-list.html',
		controller: 'collaborateListControllerDS2'
	})
	.when('/drools/:filename', {
		templateUrl: 'app/fusion/scripts/DS2-view-models/ds2-samplePages/drools.html',
		controller: 'droolsController'
	})
	.when('/droolsList', {
		templateUrl: 'app/fusion/scripts/DS2-view-models/ds2-samplePages/drools-list.html',
		controller: 'droolsListController'
	})
	.otherwise({ 
		templateUrl: 'app/fusion/scripts/DS2-view-models/ds2-samplePages/net_map.html',
		controller : 'netMapController'
	});
});
