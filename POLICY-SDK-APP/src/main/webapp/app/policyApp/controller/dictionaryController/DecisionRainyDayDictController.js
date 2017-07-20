/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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
app.controller('editRainyDayDictController' , function ($scope, $modalInstance, message, UserInfoServiceDS2, Notification){

	if(message.rainyDayDictionaryData==null)
        $scope.label='Add Allowed Treatments',
        $scope.treatments = [];
    else{
       	$scope.treatments = [];
        $scope.label='Edit Treatment'
        $scope.disableCd=true;
        var headers = message.rainyDayDictionaryData.treatments;
        var splitEqual = ',';
        if(headers != null && headers != ""){
        	if (headers.indexOf(splitEqual) >= 0) {
        		var splitValue = headers.split(splitEqual);
        		for(i = 0; i < splitValue.length; i++){
        			var key  = splitValue[i];
        			$scope.treatments.push({'treatment': key});
        		}
        	}else{
            	 var key  = headers;
                 $scope.treatments.push({'treatment': key});
            }
        }
    }
    
	
	/*getting user info from session*/
	var userid = null;
	UserInfoServiceDS2.getFunctionalMenuStaticDetailSession()
	  	.then(function (response) {	  		
	  		userid = response.userid;	  	
	 });
    
    $scope.editRainyDayTreatment = message.rainyDayDictionaryData;
    $scope.saveDecisionTreatments = function(rainyDayDictionaryData) {
		var finalData = extend(rainyDayDictionaryData, $scope.treatmentDatas[0]);
		var uuu = "saveDictionary/decision_dictionary/save_RainyDay";
		var postData={rainyDayDictionaryData: rainyDayDictionaryData, userid: userid};
		$.ajax({
			type : 'POST',
			url : uuu,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(postData),
			success : function(data){
				$scope.$apply(function(){
					$scope.rainyDayDictionaryDatas=data.rainyDayDictionaryDatas;});
				if($scope.rainyDayDictionaryDatas == "Duplicate"){
					Notification.error("Allowed Treatments Dictionary exists with Same Building Block and Workstep.")
				}else{      
					console.log($scope.rainyDayDictionaryDatas);
					$modalInstance.close({rainyDayDictionaryDatas:$scope.rainyDayDictionaryDatas});
				}
			},
			error : function(data){
				alert("Error while saving.");
			}
		});
    };
    
    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
    
    $scope.treatmentDatas = [{"userDataTypeValues" : $scope.treatments}];
    $scope.addNewTreatment = function() {
      $scope.treatments.push({});
    };    
    $scope.removeTreatment = function() {
      var lastItem = $scope.treatments.length-1;
      $scope.treatments.splice(lastItem);
    };
    

    $scope.close = function() {
        $modalInstance.close();
    };
});