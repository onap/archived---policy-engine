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

/**
 */
var app = angular.module('abs');
app.controller('exportPolicyController', function ($scope, $window, ExportPolicyService, modalService, $modal, Notification){
    $( "#dialog" ).hide();
     $scope.linkEnable = true;  
    $scope.gridOptions = {
   		 onRegisterApi: function(gridApi) {
   	            $scope.gridPolicyApi = gridApi;
   	        },
   		enableSorting: true,
   		enableFiltering: true,
   		showTreeExpandNoChildren: true,
   		paginationPageSizes: [10, 20, 50, 100],
    	paginationPageSize: 20,
   		columnDefs: [{name: 'name'}, {name: 'version'}, {name: 'dateModified'}]
   };
    
    $scope.files;
    ExportPolicyService.getAutoPushPoliciesData().then(function (data1) {
    	$scope.loading = false;
        $scope.files =  data1.data;
        var data = data1.data;
         
        var id=0;
        var writeoutNode = function(childArray, currentLevel, dataArray){
          childArray.forEach( function(childNode){
          if (childNode.files.length > 0){
              childNode.$$treeLevel = currentLevel;
              id=childNode.categoryId;
             if(childNode.categoryId == childNode.parentCategoryId){
                childNode.parent='';
              }
           }else{
           if((id!=childNode.parentCategoryId) || (childNode.categoryId == childNode.parentCategoryId)){
              if(childNode.categoryId == childNode.parentCategoryId){
                childNode.parent='';
              }
              childNode.$$treeLevel = currentLevel;
            }
          }
            dataArray.push( childNode );
            writeoutNode( childNode.files, currentLevel + 1, dataArray );
          });
          
        };
          $scope.gridOptions.data = [];
          writeoutNode(data, 0, $scope.gridOptions.data);
    }, function (error) {
        console.log("failed");
    });
    $scope.urlLink;
    $scope.download = function(){
    	 var policySelection = $scope.gridPolicyApi.selection.getSelectedRows();
         console.log(policySelection);
        var finalData = {
            "policyDatas": policySelection
        };
        console.log(finalData);
        var uuu = "policy_download/exportPolicy.htm";
        var postData={exportData: finalData};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.data=data.data;
                    var url = '../' + $scope.data;
                    window.location = url;
                    Notification.success("Policies Exported Successfully");
                });
                console.log($scope.data);
            },
            error : function(data){      	
                Notification.error("Error Occured while Exporting Policy.");
            }
        });

    };
    
    $scope.close = function() {
        $modalInstance.close();
    }; 

});
