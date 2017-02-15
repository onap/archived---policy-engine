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
angular.module('abs').controller('dcaeMicroServiceController', function ($scope, $window, $compile, PapUrlService, PolicyDictionaryService, modalService, $modal, Notification) {
    $("#dialog").hide();

	$scope.temp.policy.ttlDate = new Date($scope.temp.policy.ttlDate);
	var papUrl;
	PapUrlService.getPapUrl().then(function(data) {
		var config = data;
		papUrl = config.PAP_URL;
		console.log(papUrl);
		
	    PolicyDictionaryService.getEcompDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.ecompNameDictionaryDatas = JSON.parse($scope.data.ecompNameDictionaryDatas);
	        console.log($scope.ecompNameDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyDictionaryService.getPriorityValueData().then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.priorityDatas = JSON.parse($scope.data.priorityDatas);
	        console.log($scope.priorityDatas);
	    }, function (error) {
	        console.log("failed");
	    });

	    PolicyDictionaryService.getPolicyScopeData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.groupPolicyScopeListDatas = JSON.parse($scope.data.groupPolicyScopeListDatas);
	        console.log($scope.groupPolicyScopeListDatas);
	    }, function (error) {
	        console.log("failed");
	    });

	    PolicyDictionaryService.getMSConfigDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.microServiceCongigNameDictionaryDatas = JSON.parse($scope.data.microServiceCongigNameDictionaryDatas);
	        console.log($scope.microServiceCongigNameDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });

	    PolicyDictionaryService.getMSLocationDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.microServiceLocationDictionaryDatas = JSON.parse($scope.data.microServiceLocationDictionaryDatas);
	        console.log($scope.microServiceLocationDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });

	    PolicyDictionaryService.getMSServiceModelsDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.microServiceModelsDictionaryDatas = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
	        console.log($scope.microServiceModelsDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });

	    PolicyDictionaryService.getDCAEuuidDataByName(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.dcaeUUIDDictionaryDatas = JSON.parse($scope.data.dcaeUUIDDictionaryDatas);
	        console.log($scope.dcaeUUIDDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyDictionaryService.getRiskTypeDictionaryData(papUrl).then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
	        console.log($scope.riskTypeDictionaryDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyDictionaryService.getRiskLevelValueData().then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.riskLevelDatas = JSON.parse($scope.data.riskLevelDatas);
	        console.log($scope.riskLevelDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	    
	    PolicyDictionaryService.getGuardValueData().then(function (data) {
	        var j = data;
	        $scope.data = JSON.parse(j.data);
	        console.log($scope.data);
	        $scope.guardDatas = JSON.parse($scope.data.guardDatas);
	        console.log($scope.guardDatas);
	    }, function (error) {
	        console.log("failed");
	    });
	});

	 $scope.choices = [];
	 $scope.attributeDatas = [{"attributes" : $scope.choices}];
	 
	 $scope.addNewChoice = function(value) {
		 console.log(value);
		 if(value != undefined){
			var parentElement = document.getElementById("div."+value);
			var div = document.getElementById(value+"@0");
			if(div != null){
				var clone = div.cloneNode(true); 
				var addElement = parentElement.childElementCount + 1;
				clone.id = ''+value+'@'+addElement;
				clone.value = '';
				document.getElementById("div."+value).appendChild(clone);
				plainAttributeKeys.push(''+value+'@'+addElement);
			}else{
				div = document.getElementById("div."+value+"@0");
				var childElement = parentElement.firstElementChild;
				var countParent = parentElement.childElementCount;
				var childElementString = childElement.innerHTML;
				var find = value+"@0";
				var re = new RegExp(find, 'g');
				childElementString = childElementString.replace(re,value+'@' + countParent);
				var clone = childElement.cloneNode(true);
				for (var ii = 0; ii < parentElement.childNodes.length; ii++){
		            var childId = parentElement.childNodes[ii].id;
		            if(ii = parentElement.childNodes.length){
		            	var childnewId = childId.slice(0, -1);
		            	var count = childId.slice(-1);
		            }
		        }
				var countvalue = parseInt(count) + 1;
				clone.id = childnewId+countvalue;
				clone.value = '';
				clone.innerHTML=childElementString;
				document.getElementById("div."+value).appendChild(clone);
				var inputs = clone.getElementsByTagName("input");
				for(var i=0; i<inputs.length; i++){
					if($scope.temp.policy.ruleData != null){
						document.getElementById(inputs[i].id).value = $scope.temp.policy.ruleData[inputs[i].id];
					}
					plainAttributeKeys.push(inputs[i].id);
				}
				var ele = angular.element(document.getElementById("div."+value));
	    		$compile(ele.contents())($scope);
	            $scope.$apply();
			}
		 }
	 };
	 
	 $scope.removeChoice = function(value) {
		 console.log(value);
		 if(value != undefined){
			 document.getElementById("div."+value).removeChild(document.getElementById("div."+value).lastChild);	 
		 }
	 };
	    
    var splitDash = '-';
	var splitEqual = '=';
	var splitComma = ',';
	var splitcolon = ':';
	var plainAttributeKeys = [];
    $scope.addDataToFields = function(serviceName){
        if(serviceName != null){
        	var myNode = document.getElementById("DynamicTemplate");
        	myNode.innerHTML = '';
            var uuu = "policyController/getDCAEMSTemplateData.htm";
            var postData={policyData: serviceName};
            $.ajax({
                type : 'POST',
                url : uuu,
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify(postData),
                success : function(data){
                    $scope.$apply(function(){
                    	$scope.addNewChoice();
                    	var plainAttributeKeys = [];
                    	$scope.dcaeModelData = data[0].dcaeModelData;
                    	var attributes = $scope.dcaeModelData.attributes;
                    	var refAttributes = $scope.dcaeModelData.ref_attributes;
                    	var subAttributes = 	$scope.dcaeModelData.sub_attributes;
            
                		$scope.temp.policy.ruleGridData = [];
                    	if(attributes != null && attributes.length>0){	
                    		if(attributes.indexOf(splitComma) >= 0){
                    			var attributesAfterCommaSplit = attributes.split(splitComma);
                    			for(i = 0; i < attributesAfterCommaSplit.length; i++){
                    				if(attributesAfterCommaSplit[i].indexOf(splitcolon) > 0){
                    					var splitAttribute = attributesAfterCommaSplit[i].split(splitcolon);
                    					$scope.attributeLayout(splitAttribute);
                    				}
                    			}
                    		}else{
                    			//Implementation for single Attribute
                    			var attributesAfterCommaSplit = attributes;
                    			var splitAttribute = attributesAfterCommaSplit.split(splitcolon);
                                	$scope.attributeLayout(splitAttribute);
   
                    		}	
                    	}
                    if(refAttributes != null){
                    	if(refAttributes.indexOf(splitComma) >= 0){
                			var refattributesAfterCommaSplit = refAttributes.split(splitComma);
                			for(j = 0; j < refattributesAfterCommaSplit.length; j++){
                				if(refattributesAfterCommaSplit[j].indexOf(splitcolon) > 0){
                					var splitRefAttribute = refattributesAfterCommaSplit[j].split(splitcolon);
                					$scope.refAttributeLayout(splitRefAttribute, subAttributes);
                				}
                			}
                    	}else{
                    		//Implementation for single RefAttribute
                    		if(refAttributes != ""){
                    			var refattributesAfterCommaSplit = refAttributes;
                    			var splitRefAttribute = refattributesAfterCommaSplit.split(splitcolon);
                    			$scope.refAttributeLayout(splitRefAttribute, subAttributes);	
                    		}	
                    	}
                    }	
                    });
                    if($scope.temp.policy.editPolicy || $scope.temp.policy.readOnly){
                    	var checkData = [];
                    	var data = [];
                    	if($scope.temp.policy.ruleData != null){
                    		var propNames = Object.getOwnPropertyNames($scope.temp.policy.ruleData);
                    		propNames.forEach(function(name) {
                    			data.push(name);
                    		});
                    		for(a = 0; a < data.length; a++){
                    			var splitPlainAttributeKey1 = data[a].split(".");
                    			if(splitPlainAttributeKey1[0].indexOf("@") != -1){
                    				var key = splitPlainAttributeKey1[0];
                        			checkData.push(key);
                    			}
                    		}
                    		var unique = checkData.filter(onlyUnique);
                    		for(i =0; i < unique.length; i++){
                    			if(unique[i].indexOf("@0") == -1){
                    				var finalkey = unique[i].split("@")[0];
                    				$scope.addNewChoice(finalkey);
                    			}	
                    		}
                    	}
                    }
                    var ele = angular.element(document.getElementById("DynamicTemplate"));
            		$compile(ele.contents())($scope);
                    $scope.$apply();
                },
                error : function(data){
                    alert("Error While Retriving the Template Layout Pattren.");
                }
            });
        }
    };

    function onlyUnique(value, index, self) { 
        return self.indexOf(value) === index;
    };
    
    $scope.attributeLayout = function(splitAttribute){
		var attributeValue = splitAttribute[0];
		var splitAttributeValue = attributeValue.split(splitEqual);
		var attibuteKey = splitAttributeValue[0].trim();
		var attributeType = splitAttributeValue[1];
		var attributedefaultValue = splitAttribute[1];
		var attributeRequiredValue = splitAttribute[2];
		var attributeManyValue = splitAttribute[3];
		var splitAttributeMany = attributeManyValue.split(splitDash);
		var attributeManyKey = splitAttributeMany[1];
		$scope.temp.policy.ruleGridData.push(attibuteKey);
		var br = document.createElement("BR");
		var label = document.createElement("Label");
		var labeltext = document.createTextNode(attibuteKey);
		label.appendChild(labeltext); 
		var element = document.getElementById("DynamicTemplate");
		var textField = document.createElement("INPUT");
		textField.setAttribute("class" , "form-control");
		textField.setAttribute("type" , "text");
		textField.setAttribute("style" , "width:300px;");
		textField.setAttribute("ng-disabled" , "temp.policy.readOnly");
		var checkKey;
		if(attributeManyKey == 'true'){
			checkKey = attibuteKey+'@0';
			textField.setAttribute("id" , ''+attibuteKey+'@0'+'');
			var divTag = document.createElement("div");
			divTag.setAttribute("id", "div."+attibuteKey);
			var addButton = document.createElement("BUTTON");
			var buttonaddLabel = document.createTextNode("+");       
			addButton.appendChild(buttonaddLabel); 
			addButton.setAttribute("id", attibuteKey);
			addButton.setAttribute("class", "btn btn-default");
			addButton.setAttribute("ng-click" ,  'addNewChoice("'+attibuteKey+'");');
			addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			var removeButton = document.createElement("BUTTON");
			var buttonremoveLabel = document.createTextNode("-");       
			removeButton.appendChild(buttonremoveLabel); 
			removeButton.setAttribute("class", "btn btn-default");
			removeButton.setAttribute("ng-click" ,  'removeChoice("'+attibuteKey+'");');
			removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			document.getElementById("DynamicTemplate").appendChild(addButton); 
			document.getElementById("DynamicTemplate").appendChild(removeButton); 
			document.getElementById("DynamicTemplate").appendChild(label);  
			document.getElementById("DynamicTemplate").appendChild(textField); 
			document.getElementById("DynamicTemplate").appendChild(divTag); 
			document.getElementById("DynamicTemplate").appendChild(br); 
		}else{
			checkKey = attibuteKey;
			textField.setAttribute("id" , ''+attibuteKey+'');
			document.getElementById("DynamicTemplate").appendChild(label);   
			document.getElementById("DynamicTemplate").appendChild(textField);  
			document.getElementById("DynamicTemplate").appendChild(br);
		}
		if($scope.temp.policy.ruleData != null){
			document.getElementById(checkKey).value = $scope.temp.policy.ruleData[checkKey];
		}
		plainAttributeKeys.push(attibuteKey+'*'+attributeManyKey);
    };
    
    $scope.firstlayer = null;
    $scope.secondlayer = null;
    $scope.refAttributeLayout = function(splitRefAttribute, subAttributes){
    	var attibuteKey = splitRefAttribute[0];
		var splitRefAttributeValue = attibuteKey.split(splitEqual);
		var refattributeLabel = splitRefAttributeValue[0].trim();
		var refattributeValue = splitRefAttributeValue[1].trim();
		
		var refattributeManyValue = splitRefAttribute[1];
		var splitRefAttributeMany = refattributeManyValue.split(splitDash);
		var refattributeManyKey = splitRefAttributeMany[1];
		var subAttributeJSON = JSON.parse(subAttributes);
		var resultValue = subAttributeJSON[refattributeValue];
		var br = document.createElement("BR");
		var label = document.createElement("Label");
		var labeltext = document.createTextNode(refattributeLabel);
		label.appendChild(labeltext);
		   
		if(refattributeManyKey == 'true'){
 
			var divTag = document.createElement("div");
			divTag.setAttribute("id", "div."+refattributeLabel);
			var addButton = document.createElement("BUTTON");
			var buttonLabel = document.createTextNode("+");       
			addButton.appendChild(buttonLabel); 
			addButton.setAttribute("id", refattributeLabel);
			addButton.setAttribute("class", "btn btn-default");
			addButton.setAttribute("ng-click" ,  'addNewChoice("'+refattributeLabel+'");');
			addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			var removeButton = document.createElement("BUTTON");
			var buttonremoveLabel = document.createTextNode("-");       
			removeButton.appendChild(buttonremoveLabel); 
			removeButton.setAttribute("class", "btn btn-default");
			removeButton.setAttribute("ng-click" ,  'removeChoice("'+refattributeLabel+'");');
			removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly"); 
			document.getElementById("DynamicTemplate").appendChild(addButton); 
			document.getElementById("DynamicTemplate").appendChild(removeButton);
			document.getElementById("DynamicTemplate").appendChild(label);
			document.getElementById("DynamicTemplate").appendChild(br); 
			document.getElementById("DynamicTemplate").appendChild(divTag);
			var divTag = document.createElement("div");
			divTag.setAttribute("id", "div."+refattributeLabel+"@0");
			document.getElementById("div."+refattributeLabel).appendChild(divTag);
			$scope.firstlayer = refattributeLabel+"@0";
			$scope.subAttributeLayout(resultValue, subAttributes, refattributeLabel);
			document.getElementById("DynamicTemplate").appendChild(br); 
		}else{
			document.getElementById("DynamicTemplate").appendChild(label);  
			document.getElementById("DynamicTemplate").appendChild(br);  
			$scope.firstlayer = refattributeLabel;
			$scope.subAttributeLayout(resultValue, subAttributes, refattributeLabel);
		}
    };
    
    $scope.subAttributeLayout = function(resultValue, subAttributes, refattributeLabel){
    	for(var key in resultValue){
    		key = key.trim();
    		if(key == 'logicalConnector'){
    			$scope.secondlayer = null;
    		}
			var splitSubAttribute = resultValue[key].split(splitcolon);
			var br = document.createElement("BR");
			var label = document.createElement("Label");
			var labeltext = document.createTextNode(key);
			label.appendChild(labeltext);
			var textField = document.createElement("INPUT");
			textField.setAttribute("class" , "form-control");
			textField.setAttribute("type" , "text");
			textField.setAttribute("style" , "width:300px;");
			textField.setAttribute("ng-disabled" , "temp.policy.readOnly");
	
			if(splitSubAttribute.length >= 3){
				var subattributeType = splitSubAttribute[0];
				var subattributedefaultValue = splitSubAttribute[1];
				var subattributeRequiredValue = splitSubAttribute[2];
				var subattributeManyValue = splitSubAttribute[3];
				var splitSubAttributeMany = subattributeManyValue.split(splitDash);
				var subattributeManyKey = splitSubAttributeMany[1];
				if(subattributeManyKey == 'true'){	
					if($scope.firstlayer == null && $scope.secondlayer == null){
						textField.setAttribute("id" , ''+key+'@0'+'');
					}
					if($scope.firstlayer != null && $scope.secondlayer == null){
						textField.setAttribute("id" , ''+$scope.firstlayer+'.'+key+'@0'+'');
					}
					if($scope.firstlayer != null && $scope.secondlayer != null){
						textField.setAttribute("id" , ''+$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0'+'');
					}	
					var divTag = document.createElement("div");
					var addButton = document.createElement("BUTTON");
					var buttonLabel = document.createTextNode("+");       
					addButton.appendChild(buttonLabel); 
					addButton.setAttribute("class", "btn btn-default");
					addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
					var removeButton = document.createElement("BUTTON");
					var buttonremoveLabel = document.createTextNode("-");       
					removeButton.appendChild(buttonremoveLabel); 
					removeButton.setAttribute("class", "btn btn-default");
					if($scope.secondlayer == null){
						addButton.setAttribute("ng-click" ,  'addNewChoice("'+$scope.firstlayer+'.'+key+'");');
						removeButton.setAttribute("ng-click" ,  'removeChoice("'+$scope.firstlayer+'.'+key+'");');
						divTag.setAttribute("id", "div."+$scope.firstlayer+'.'+key);
					}else{
						addButton.setAttribute("ng-click" ,  'addNewChoice("'+$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'");');
						removeButton.setAttribute("ng-click" ,  'removeChoice("'+$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'");');
						divTag.setAttribute("id", "div."+$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key);
					}	
					
					removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
					if(!refattributeLabel.startsWith('div.')){
						refattributeLabel = 'div.'+refattributeLabel+"@0";
					}
					document.getElementById(refattributeLabel).appendChild(addButton); 
					document.getElementById(refattributeLabel).appendChild(removeButton); 
					document.getElementById(refattributeLabel).appendChild(label);
					document.getElementById(refattributeLabel).appendChild(textField);  
					document.getElementById(refattributeLabel).appendChild(divTag);
					document.getElementById(refattributeLabel).appendChild(br);
				}else{
					if($scope.firstlayer == null && $scope.secondlayer == null){
						textField.setAttribute("id" , ''+key+'');
					}
					if($scope.firstlayer != null && $scope.secondlayer == null){
						textField.setAttribute("id" , ''+$scope.firstlayer+'.'+key+'');
					}
					if($scope.firstlayer != null && $scope.secondlayer != null){
						textField.setAttribute("id" , ''+$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'');
					}
					if(!refattributeLabel.startsWith('div.')){
						refattributeLabel = 'div.'+refattributeLabel+"@0";
					}
					document.getElementById(refattributeLabel).appendChild(label); 
					document.getElementById(refattributeLabel).appendChild(textField); 
					document.getElementById(refattributeLabel).appendChild(br); 
				}
				if($scope.secondlayer == null){
					plainAttributeKeys.push($scope.firstlayer+'.'+key+'*'+subattributeManyKey);
					if($scope.temp.policy.ruleData != null){
						if(document.getElementById($scope.firstlayer+'.'+key) != null){
							document.getElementById($scope.firstlayer+'.'+key).value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+key];
						}else{
							document.getElementById($scope.firstlayer+'.'+key+'@0').value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+key+'@0'];
							var i =1 ;
							while(true){
								var newValue = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+key+'@'+i];
								if(newValue==null){
									break;
								}else{
									$scope.addNewChoice($scope.firstlayer+'.'+key);
									document.getElementById($scope.firstlayer+'.'+key+'@'+i).value = newValue;
									i = i+1;
								}
							}
						}
						
					}
				}else{
					plainAttributeKeys.push($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'*'+subattributeManyKey);
					if($scope.temp.policy.ruleData != null){
						if(document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key) != null){
							document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key).value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key];
						}else{
							document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0').value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0'];
							var i =1 ;
							while(true){
								var newValue = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@'+i];
								if(newValue==null){
									break;
								}else{
									$scope.addNewChoice($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key);
									document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@'+i).value = newValue;
									i = i+1;
								}
							}
						}
						
					}
				}
			}else{
				//Add Recursive Function
				var saveResultValue = resultValue;
				var subAttributeJSON = JSON.parse(subAttributes);
				var recursiveKey = splitSubAttribute[0].trim();
				var resultValue = subAttributeJSON[recursiveKey];
				var recursiveMany = splitSubAttribute[1];
				var br = document.createElement("BR");
				var label = document.createElement("Label");
				var labeltext = document.createTextNode(key);
				label.appendChild(labeltext);

				if(recursiveMany == 'MANY-true'){
					var addButton = document.createElement("BUTTON");
					var buttonLabel = document.createTextNode("+");
					addButton.appendChild(buttonLabel); 
					addButton.setAttribute("class", "btn btn-default");
					addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
					var removeButton = document.createElement("BUTTON");
					var buttonremoveLabel = document.createTextNode("-");       
					removeButton.appendChild(buttonremoveLabel); 
					removeButton.setAttribute("class", "btn btn-default");
					addButton.setAttribute("ng-click" ,  'addNewChoice("'+$scope.firstlayer+'.'+key+'");');
					removeButton.setAttribute("ng-click" ,  'removeChoice("'+$scope.firstlayer+'.'+key+'");');
					removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
					var idf = "div."+$scope.firstlayer;
					document.getElementById(idf).appendChild(addButton); 
					document.getElementById(idf).appendChild(removeButton);
					document.getElementById(idf).appendChild(label);
					document.getElementById(idf).appendChild(br); 
					var id = "div."+$scope.firstlayer+'.'+key;
					var divTag = document.createElement("div");
					divTag.setAttribute("id",id); 
					document.getElementById("div."+$scope.firstlayer).appendChild(divTag);
					$scope.secondlayer = key;
					var idc = "div."+$scope.firstlayer+key+'@0';
					var divTag = document.createElement("div");
					divTag.setAttribute("id", idc);
					document.getElementById(id).appendChild(divTag);
					$scope.subAttributeLayout(resultValue, subAttributes, idc);
					document.getElementById(id).appendChild(br); 
				}else{
					var id = "div."+$scope.firstlayer+'.'+key;
					var divTag = document.createElement("div");
					divTag.setAttribute("id",id); 
					document.getElementById("div."+$scope.firstlayer).appendChild(divTag);
					document.getElementById(id).appendChild(br); 
					document.getElementById(id).appendChild(label); 
					document.getElementById(id).appendChild(br);
					$scope.secondlayer = key;
					$scope.subAttributeLayout(resultValue, subAttributes, id);
				}
				resultValue = saveResultValue;
				//$scope.secondlayer == null;
				if($scope.secondlayer == null){
					plainAttributeKeys.push($scope.firstlayer+'.'+key+'*'+recursiveMany);
					if($scope.temp.policy.ruleData != null){
						if(document.getElementById($scope.firstlayer+'.'+key) != null){
							document.getElementById($scope.firstlayer+'.'+key).value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+key];
						}else{
							document.getElementById($scope.firstlayer+'.'+key+'@0').value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+key+'@0'];
						}
					}
				}else{
					plainAttributeKeys.push($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'*'+recursiveMany);
					if($scope.temp.policy.ruleData != null){
						if(document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key) != null){
							document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key).value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key];
						}else{
							if(document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0') != null){
								document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0').value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0'];
							}else if(document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key) != null){
								document.getElementById($scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key).value = $scope.temp.policy.ruleData[$scope.firstlayer+'.'+$scope.secondlayer+'@0.'+key+'@0'];
							}						
						}					
					}
				}	
			}
		}
    };
    
    $scope.savePolicy = function(policy){
    	var splitAt = '*';
    	var dot ='.';
    	var jsonPolicy = {};
    	if(plainAttributeKeys != null){
    		for(a = 0; a < plainAttributeKeys.length; a++){
    			var splitPlainAttributeKey = plainAttributeKeys[a].split(splitAt);
    			console.log(splitPlainAttributeKey[1]);	
    			var searchElement = document.getElementById(splitPlainAttributeKey[0]);
    			var key = splitPlainAttributeKey[0];
    			if(searchElement == null){
    				searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
    				key = splitPlainAttributeKey[0]+'@0';
    			}
    			if(searchElement != null){
    				if(searchElement.value != null){
    					jsonPolicy[key]= searchElement.value;
    				}
    			}
    		}
    	}
        var uuu = "policyController/save_DCAEMSPolicy.htm";
        var postData={policyData: policy, policyJSON : jsonPolicy};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                    $scope.data=data.data;
                	$scope.temp.policy.readOnly = 'true';
                    Notification.success("Policy Saved Successfully.");
                });
                console.log($scope.data);
                $modalInstance.close();

            },
            error : function(data){
            	Notification.error("Error Occured while saving Policy.");
            }
        });
    };
    
    $scope.validatePolicy = function(policy){
        var uuu = "policyController/validate_policy.htm";
 		var postData={policyData: policy};
 		$.ajax({
 			type : 'POST',
 			url : uuu,
 			dataType: 'json',
 			contentType: 'application/json',
 			data: JSON.stringify(postData),
 			success : function(data){
 				$scope.$apply(function(){
					$scope.data=data.data.substring(1,8);
						var size = data.data.length;
						if($scope.data == 'success'){
							Notification.success("Validation Success.");
							if (size > 18){
								var displayWarning = data.data.substring(19,size);
								window.alert(displayWarning);
							}
 						}else{
 							Notification.error("Validation Failed.");
 						}
 						
 				});
 				console.log($scope.data);	
 			},
 			error : function(data){
 				Notification.error("Validation Failed.");
 			}
 		});
    };

    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
});
