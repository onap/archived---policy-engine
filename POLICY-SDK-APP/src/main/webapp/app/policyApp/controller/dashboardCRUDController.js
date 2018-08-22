/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

app.controller('policyDashboardCRUDDataController', function ($scope, PolicyAppService, modalService, $modal){
	console.log("policyDashboardCRUDDataController called");
	
	$('#dashBoardAdvanceSearch').hide();
	
    $scope.papCRUDTableDatasTemp = [];
    
    $scope.dashboardAdsearch = { isDelected: 'both', stage: 'both',  scope: "", ttlDate_after: "", ttlDate_before: ""};

    PolicyAppService.getData('get_DashboardPolicyCRUDData').then(function(data){

		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.papStatusCRUDDatas =JSON.parse($scope.data.papStatusCRUDData);
        if($scope.papStatusCRUDDatas != null){
            for(i = 0; i < $scope.papStatusCRUDDatas.length; i++){
                $scope.papCRUDTableDatasTemp.push($scope.papStatusCRUDDatas[i].map);
            }
            
            $scope.papCRUDTableDatas = $scope.papCRUDTableDatasTemp;
        } 
		
	},function(error){
		console.log("failed");
	});

	$scope.papCRUDStatusDatas = {
		data : 'papCRUDTableDatas',
		enableFiltering: true,
		columnDefs: [{ field: 'id', displayName :'id'},
		 		 	{field: 'scope', displayName :'Scope'},
					{field: 'policyName', displayName :'Policy Name'},
					{field: 'version', displayName :'Version'},
					{field: 'stage', displayName :'Stage'},
					{field: 'createdBy', displayName :'Created By'},
					{field: 'deleted', displayName :'Deleted'},
					{field: 'deleteReasonCode', displayName :'Deleted Reason'},
					{field: 'deletedBy' , displayName :'Deleted By'},
					{field: 'modifiedBy' , displayName :'Modified By'},
					{field: 'createdDate',  displayName :'Created Date'},
					{field: 'modifiedDate',  displayName :'Modified Date'}
		],
        onRegisterApi: function(gridApi){
        	$scope.gridApi = gridApi;
        }
	};
	
    $('#ttlDate_after').datepicker({
    	dateFormat: 'yy-mm-dd',
    	changeMonth: true,
    	changeYear: true,
    	onSelect: function(date) {
    		angular.element($('#ttlDate_after')).triggerHandler('input');
    	}
    });
    
    $('#ttlDate_before').datepicker({
    	dateFormat: 'yy-mm-dd',
    	changeMonth: true,
    	changeYear: true,
    	onSelect: function(date) {
    		angular.element($('#ttlDate_before')).triggerHandler('input');
    	}
    });
    
    $scope.refresh = function(){
    	$scope.modal('advancedSearch', true);
    	$scope.temp.policy = "";
    };
    
    
	$scope.advancedSearch = function(){

		 $('#dashBoardAdvanceSearch').toggle();
		 if($('#advancedSearchArrow').hasClass('arrowdown')){
			 $('#advancedSearchArrow').removeClass("arrowdown");
			 $('#advancedSearchArrow').addClass("arrowup"); 
			 
		 }else{
			 $('#advancedSearchArrow').removeClass("arrowup");
			 $('#advancedSearchArrow').addClass("arrowdown"); 
		 }
	}
	
   
    $scope.startAdvancedSearch = function(data){
    	
		 console.log("startAdvancedSearch called");
		 console.log(data.isDelected);
		 console.log(data.stage);
		 console.log(data.scope);
		 console.log(data.ttlDate_after);
		 console.log(data.ttlDate_before);
		 
		 if(data.scope == null){
			 return;
		 }
		 
       var uuu = "dashboardController/dashboardAdvancedSearch.htm";
        
        var postData={policyData: data};
 		$.ajax({
 			type : 'POST',
 			url : uuu,
 			dataType: 'json',
 			contentType: 'application/json',
 			data: JSON.stringify(postData),
 			success : function(data){
 				 console.log("dashboardAdvancedSearch data returned: " + data);
 				
                 $scope.$apply(function(){  
                	
     				var j = data;
     				$scope.data = JSON.parse(j.data);
     				console.log($scope.data);
     				$scope.papStatusCRUDDatas =JSON.parse($scope.data.policyStatusCRUDData);
     				
     				$scope.papCRUDTableDatasTemp = [];
     		       
     	            for(i = 0; i < $scope.papStatusCRUDDatas.length; i++){
     	                $scope.papCRUDTableDatasTemp.push($scope.papStatusCRUDDatas[i].map);
     	            }
     	            
     	            $scope.papCRUDTableDatas = $scope.papCRUDTableDatasTemp;
     	            
     	           $scope.gridApi.grid.refresh();
               });
 			},
 			error : function(data){
 				console.log("dashboardAdvancedSearch Failed: data returned as " + data);
 			}
 		});
    };

});