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
angular.module('abs').controller('dcaeMicroServiceController', ['$scope', '$window', '$compile', 'PolicyAppService', 'policyNavigator', 'modalService', '$modal', 'Notification', function ($scope, $window, $compile, PolicyAppService, PolicyNavigator, modalService, $modal, Notification) {
    $("#dialog").hide();
    
    $scope.policyNavigator;
    $scope.isCheck = false;
    $scope.savebutton = true;
    $scope.refreshCheck = false;
    
    $scope.refresh = function(){
    	if($scope.refreshCheck){
    		$scope.policyNavigator.refresh();
    	}
    	$scope.modal('createNewPolicy', true);
    };
    
    $scope.modal = function(id, hide) {
        return $('#' + id).modal(hide ? 'hide' : 'show');
    };
    
	if ($scope.temp.policy.editPolicy != undefined|| $scope.temp.policy.readOnly  != undefined){
		if ($scope.temp.policy.configName == undefined){
			$scope.isCheck = false;
		}else{
			$scope.isCheck = true;
		}
	}else {
		$scope.isCheck = false;
	}
		
	PolicyAppService.getData('getDictionary/get_EcompNameDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.ecompNameDictionaryDatas = JSON.parse($scope.data.ecompNameDictionaryDatas);
		console.log($scope.ecompNameDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('get_DCAEPriorityValues').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.priorityDatas = JSON.parse($scope.data.priorityDatas);
		console.log($scope.priorityDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_GroupPolicyScopeDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.groupPolicyScopeListDatas = JSON.parse($scope.data.groupPolicyScopeListDatas);
		console.log($scope.groupPolicyScopeListDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_MicroServiceConfigNameDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.microServiceCongigNameDictionaryDatas = JSON.parse($scope.data.microServiceCongigNameDictionaryDatas);
		console.log($scope.microServiceCongigNameDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_MicroServiceLocationDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.microServiceLocationDictionaryDatas = JSON.parse($scope.data.microServiceLocationDictionaryDatas);
		console.log($scope.microServiceLocationDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_MicroServiceModelsDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		var inputModelList = JSON.parse($scope.data.microServiceModelsDictionaryDatas);
		var unique = {};
		var uniqueList = [];
		for(var i = 0; i < inputModelList.length; i++){
			if(typeof unique[inputModelList[i]] == "undefined"){
				unique[inputModelList[i]] = "";
				uniqueList.push(inputModelList[i]);
			}
		}
		$scope.microServiceModelsDictionaryDatas = uniqueList;
		console.log($scope.microServiceModelsDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_DCAEUUIDDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.dcaeUUIDDictionaryDatas = JSON.parse($scope.data.dcaeUUIDDictionaryDatas);
		console.log($scope.dcaeUUIDDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_RiskTypeDataByName').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.riskTypeDictionaryDatas = JSON.parse($scope.data.riskTypeDictionaryDatas);
		console.log($scope.riskTypeDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});

	PolicyAppService.getData('getDictionary/get_MicroServiceAttributeData').then(function (data) {
		var j = data;
		$scope.data = JSON.parse(j.data);
		console.log($scope.data);
		$scope.microServiceAttributeDictionaryDatas = JSON.parse($scope.data.microServiceAttributeDictionaryDatas);
		console.log($scope.microServiceAttributeDictionaryDatas);
	}, function (error) {
		console.log("failed");
	});
	    

	 $scope.choices = [];
	 $scope.attributeDatas = [{"attributes" : $scope.choices}];
	 
	 $scope.addNewChoice = function(value) {
		 console.log(value);
		 if(value != undefined){
			if (value.startsWith('div.')){
				value = value.replace('div.','');
			}
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
				var selects = clone.getElementsByTagName("select");
				var inputs = clone.getElementsByTagName("input");
				var removeValues = [];
				for(var i=0; i<inputs.length; i++){
					if ($scope.temp.policy.ruleData!=undefined){
						var checkValue = $scope.temp.policy.ruleData[inputs[i].id];
						if (checkValue!=undefined){
							if($scope.temp.policy.ruleData != null){
								var checkValue = $scope.temp.policy.ruleData[inputs[i].id];
								document.getElementById(inputs[i].id).value = $scope.temp.policy.ruleData[inputs[i].id];
								plainAttributeKeys.push(inputs[i].id);
							}
						} else {
//							removeValues.push(inputs[i].id);
							plainAttributeKeys.push(inputs[i].id);
						}
					}else {
						plainAttributeKeys.push(inputs[i].id);
					}
				}
				
				for(var i=0; i<selects.length; i++){
					if ($scope.temp.policy.ruleData!=undefined){
						var checkValue = $scope.temp.policy.ruleData[selects[i].id];
						if (checkValue!=undefined){
							if($scope.temp.policy.ruleData != null){
								var checkValue = $scope.temp.policy.ruleData[selects[i].id];
								document.getElementById(selects[i].id).value = $scope.temp.policy.ruleData[selects[i].id];
								plainAttributeKeys.push(selects[i].id);
							}
						} else {
							plainAttributeKeys.push(selects[i].id);
						}
					}else {
						plainAttributeKeys.push(selects[i].id);
					}
				}
				
				for (var k=0; k<removeValues.length; k++){
					var elem = document.getElementById(removeValues[k]);
					elem.parentNode.removeChild(elem);
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
	 
	 $scope.pullVersion = function(serviceName) {
		 console.log(serviceName);
		 if(serviceName != undefined){
			 var uuu = "policyController/getModelServiceVersioneData.htm";
			 var postData={policyData: serviceName};
			 $.ajax({
				 type : 'POST',
				 url : uuu,
				 dataType: 'json',
				 contentType: 'application/json',
				 data: JSON.stringify(postData),
				 success : function(data){
					 $scope.$apply(function(){
						 $scope.microServiceModelsDictionaryVersionDatas = data[0].dcaeModelVersionData;
					 });
				 },
				 error : function(data){
					 alert("Error While Retriving the Template Layout Pattren.");
				 }
			 });		 
		 }
	 };
    
    var splitDash = '-';
	var splitEqual = '=';
	var splitComma = ',';
	var splitcolon = ':';
	var splitsemicolon = ";";
	var splitEnum = "],";
	var plainAttributeKeys = [];
	var matching = [];
	var enumKeyList = [];
	var dictionaryList = [];
	var dictionaryNameList = [];
    $scope.addDataToFields = function(serviceName, version){
        if(serviceName != null && version !=null){
        	var service=serviceName+"-v"+version;
        	var myNode = document.getElementById("DynamicTemplate");
        	myNode.innerHTML = '';
            var uuu = "policyController/getDCAEMSTemplateData.htm";
            var postData={policyData: service};
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
                    	$scope.dcaeJsonDate = data[0].jsonValue;
                    	var attributes = $scope.dcaeModelData.attributes;
                    	var refAttributes = $scope.dcaeModelData.ref_attributes;
                    	var subAttributes = 	$scope.dcaeModelData.sub_attributes;
                       	var enumAttributes = $scope.dcaeModelData.enumValues;
                       	var annotation = $scope.dcaeModelData.annotation;
                       	var dictionary = $scope.microServiceAttributeDictionaryDatas;

                       	if (annotation == null || annotation.length<1){
                       		$scope.isCheck = true;
                       	}else {
                       		$scope.isCheck = false;
                       		var annoationList = annotation.split(splitComma);
                       		for (k = 0; k < annoationList.length; k++){
                           		var splitAnnotation = annoationList[k].split(splitEqual);
                           		if (splitAnnotation[1].includes("matching-true")){
                           			matching.push(splitAnnotation[0].trim());
                           		}
                       		}

                       	}

                       	if (dictionary!= null || dictionary>1){
                    		if (dictionary!= null || dictionary.length>1){
                    			for (m=0; m < dictionary.length; m += 1){
                    				var keyCompare = dictionary[m].name;
                    				var valueCompare = dictionary[m].value;
                    				var valueModel = dictionary[m].modelName;
                    				var conpairService = serviceName;
                    				if (valueModel.includes('-v')){
                    					conpairService = service;
                    				}
                    				if(valueModel.localeCompare(conpairService) == 0){
                    					console.log(valueCompare);	
                    					dictionaryList.push(dictionary[m]);
                    					if (!dictionaryNameList.includes(dictionary[m].name)){
                    						dictionaryNameList.push(dictionary[m].name)
                    					}
                    				}
                    			}
                    		}
                       	}

                		$scope.temp.policy.ruleGridData = [];
                		
                		$scope.jsonLayout($scope.dcaeJsonDate);
                		
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
    
    function getDictionary(attribute){
    	var dicName = attribute;
    	if(attribute){
    	   if(attribute.includes(":")){
    		   dicName = attribute.split(":")[0];
    	   }
    	}
	    var dictionaryRegExp = new RegExp(dicName);
	    listemunerateValues = [];
	    if (dictionaryRegExp.test(dictionaryNameList)) {
			for (p=0; p < dictionaryList.length; p += 1) {
				if (dicName == dictionaryList[p].name) {
					listemunerateValues.push(dictionaryList[p].value);
				}
			}
	    }
	    return listemunerateValues;
    }
    
    function getList(attribute) {
	    	var enumName = attribute;
	    	console.log("In getList: attribute => " + attribute);
	    	if(attribute){
	    	   if(attribute.includes(":")){
	    		   enumName = attribute.split(":")[0];
	    	   }
	    	}   	
            var baseEnum = $scope.dcaeModelData.enumValues;
	    	var enumList = baseEnum.split(splitEnum);
	    	var enumAttributes;
	    	var patternTest = new RegExp(enumName);
	    	for (k=0; k < enumList.length; k += 1){
	    		if(patternTest.test(enumList[k]) == true){
	    			enumAttributes = enumList[k].trim();
    		}
    	}

         if(enumAttributes){
	        enumAttributes = enumAttributes.replace("[", "");
	       	enumAttributes = enumAttributes.replace("]", "");
	       	enumAttributes = enumAttributes.replace(/ /g, '');
			var dropListAfterCommaSplit = enumAttributes.split(splitEqual);
			listemunerateValues  = dropListAfterCommaSplit[1].split(splitComma);
			//enumKeyList.push(attribute); 
	        return listemunerateValues;
         }
         
            return [];
	    }
	    
	    function getBooleanList(){
	    	var booleanList = [];
	    	booleanList.push(true);
	    	booleanList.push(false);
	    	return booleanList;
	    }
	    

	    function isArray(arrayTest) {
	        return Object.prototype.toString.call(arrayTest) === '[object Array]';
	    }
	    var lableList = [];
	    
		function deconstructJSON(dataTest, level , name) {
			var array = false;
			var label = level;
			 var stringValue = "java.lang.String";
			 var intValue = "int";
			 var double = "double";
			 var boolean = "boolean";
			 var baseLevel = level;
			 
			if (name.length > 1){
				label = label + name + '.';
			}
			
		    for (key in dataTest) {
		    	array = isArray(dataTest[key]);
		    	console.log(key , dataTest[key]);
		    	if (!!dataTest[key] && typeof(dataTest[key])=="object") {
		    		if (array==false && key!=="0"){
		    			$scope.labelLayout(label, key, array ); 			
		    		}
		    		
		    		if (array == true && key!=0){
		    			lableList.push(key);
		    		}
		    		
		    		if (lableList.length > 0){
		    			array = true;
		    		}
		    		if ( key=="0"){
		    			var newKey = lableList.pop();
		    			$scope.labelLayout(baseLevel, newKey, array );
		    			if (array){
		    				label = baseLevel + newKey + '@0.';
		    			} else {
		    				label = baseLevel + newKey + '.';
		    			}
		    		}
		        	deconstructJSON(dataTest[key] , label, key);
		        } else {
		        	var attirbuteLabel = label;
		        	var defaultValue='';
		        	var isRequired = false;
		        	if (dataTest[key].includes('defaultValue-')){
		        		defaultValue = dataTest[key].split('defaultValue-')[1];
		        	}

		        	if (key==="0"){
		        		array = true;
		        		attributekey = lableList.pop();
		        		attirbuteLabel = baseLevel;
		        	} else {
		        		attributekey = key.split();
		        	}
		        	
		        	if (dataTest[key].includes('required-true')){
		        		isRequired = true;
		        	}
		        	console.log("attirbuteLabel = " + attirbuteLabel);
		        	switch (dataTest[key].split(splitcolon)[0]){
		        		case stringValue:
			        		$scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired);
		        			break;
		        		case intValue: 
		        			$scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired);
		        			break;
		        		case double:
		        			$scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired);
		        			break;
		        		case boolean:
		        			$scope.dropBoxLayout(attirbuteLabel, attributekey, array, dataTest[key], getBooleanList());
		        			break;
		        		default:
		        			if (dataTest[key].includes('dictionary-')){
		        				var list = getDictionary(dataTest[key].split('dictionary-')[1]);
		        			}else{
		        				var list = getList(dataTest[key]);
		        			}
		        			if (list.length===0){
		        				$scope.attributeBox(attributekey, array, attirbuteLabel, defaultValue, isRequired);
		        			}else{
		        				$scope.dropBoxLayout(attirbuteLabel, attributekey, array, dataTest[key], list);
		        			}
		        			break;
		        	}
		        }
		    }
		}  

	    $scope.jsonLayout = function(dataTest){
	    	deconstructJSON(dataTest , "", "");
	    }
	    
	    
	    $scope.attributeBox = function(attibuteKey, attributeManyKey, labelValue, defaultValue, isRequired){
			$scope.temp.policy.ruleGridData.push(attibuteKey);			
		var br = document.createElement("BR");
		
		var label = document.createElement("Label");
		var labeltext = null;
		var requiredLabName = "";
		if (matching.includes(attibuteKey)){
			labeltext = document.createTextNode(attibuteKey + "*!");		
		}else {
			//var labeltext = document.createTextNode(refattributeLabel);
			if(isRequired){
				requiredLabName = attibuteKey + " *";
				labeltext = document.createTextNode(requiredLabName);
			}else{
			    labeltext = document.createTextNode(attibuteKey);	
			}
		}

		
		var divID = labelValue;
		
		if (labelValue.length  < 1){
			divID = "DynamicTemplate";
		}else if (labelValue.endsWith('.')){
			var divID = 'div.'+ labelValue.substring(0, labelValue.length-1);
		}
		
		label.appendChild(labeltext);
		
		var textField = document.createElement("INPUT");
		
		textField.setAttribute("class" , "form-control");
		textField.setAttribute("type" , "text");
		textField.setAttribute("style" , "width:300px;");
		textField.setAttribute("ng-disabled" , "temp.policy.readOnly");
		var checkKey;
		if(attributeManyKey){
			checkKey = labelValue + attibuteKey+'@0';
			textField.setAttribute("id" , ''+labelValue + attibuteKey+'@0'+'');
			var divTag = document.createElement("div");
			divTag.setAttribute("id", "div."+ labelValue +attibuteKey);
			var addButton = document.createElement("BUTTON");
			var buttonaddLabel = document.createTextNode("+");       
			addButton.appendChild(buttonaddLabel); 
			addButton.setAttribute("id", labelValue + attibuteKey);
			addButton.setAttribute("class", "btn btn-default");
			addButton.setAttribute("ng-click" ,  'addNewChoice("'+labelValue + attibuteKey+'");');
			addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			var removeButton = document.createElement("BUTTON");
			var buttonremoveLabel = document.createTextNode("-");       
			removeButton.appendChild(buttonremoveLabel); 
			removeButton.setAttribute("class", "btn btn-default");
			removeButton.setAttribute("ng-click" ,  'removeChoice("'+labelValue + attibuteKey+'");');
			removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			document.getElementById(divID).appendChild(addButton); 
			document.getElementById(divID).appendChild(removeButton); 
			document.getElementById(divID).appendChild(label);  
			document.getElementById(divID).appendChild(textField); 
			document.getElementById(divID).appendChild(br); 
			document.getElementById(divID).appendChild(divTag); 
		}else{
			checkKey = labelValue + attibuteKey;
			textField.setAttribute("id" , ''+labelValue +attibuteKey+'');
			if(requiredLabName.includes("*")){
				textField.setAttribute("required", "true");
			}
			document.getElementById(divID).appendChild(label);  
			document.getElementById(divID).appendChild(textField);  
			document.getElementById(divID).appendChild(br); 

		}
		
		if (defaultValue.length > 0){	
			if(defaultValue.includes(":")){
				defaultValue = defaultValue.split(":")[0];
				if(defaultValue === "NA") {
					defaultValue = "";
				}				
			}
			document.getElementById(checkKey).value = defaultValue;
		}
		
		if($scope.temp.policy.ruleData != null){
			//document.getElementById(checkKey).value = $scope.temp.policy.ruleData[checkKey];
			if (attributeManyKey){
				var newCheckKey = checkKey.replace(attibuteKey + '@0',attibuteKey);
				document.getElementById(newCheckKey +'@0').value = $scope.temp.policy.ruleData[newCheckKey +'@0'];
				var i =1 ;
				while(true){
					var newValue = $scope.temp.policy.ruleData[newCheckKey+'@'+i];
					if(newValue==null){
						break;
					}else{
						$scope.addNewChoice(newCheckKey);
						document.getElementById(newCheckKey+'@'+i).value = newValue;
						i = i+1;
					}
				}		
		
			}else{
				document.getElementById(checkKey).value = $scope.temp.policy.ruleData[checkKey];
			}
		} 
		plainAttributeKeys.push(labelValue + attibuteKey+'*'+attributeManyKey);	
    };
  
    $scope.labelLayout = function(labelValue, lableName, labelManyKey ){
		var label = document.createElement("Label")
		var divID = labelValue;
		if (labelValue.endsWith('.')){
			var workingLabel = labelValue.substring(0, labelValue.length-1);
		}else {
			var workingLabel = labelValue;
		}
		
		if (labelValue.length  < 1){
			divID = "DynamicTemplate";
		} else if (labelValue.endsWith('.')){
			var divID = 'div.'+ labelValue.substring(0, labelValue.length-1);
		}
		
		var labeltext = document.createTextNode(lableName);
	
		label.appendChild(labeltext);

		if(labelManyKey){
			var addButton = document.createElement("BUTTON");
			var buttonLabel = document.createTextNode("+");       
			addButton.appendChild(buttonLabel); 
			addButton.setAttribute("class", "btn btn-default");
			addButton.setAttribute("ng-click" ,  'addNewChoice("'+labelValue + lableName+'");');
			addButton.setAttribute("ng-disabled" , "temp.policy.readOnly");
			var removeButton = document.createElement("BUTTON");
			var buttonremoveLabel = document.createTextNode("-");       
			removeButton.appendChild(buttonremoveLabel); 
			removeButton.setAttribute("class", "btn btn-default");
			removeButton.setAttribute("ng-click" ,  'removeChoice("'+labelValue +lableName+'");');
			removeButton.setAttribute("ng-disabled" , "temp.policy.readOnly"); 
			document.getElementById(divID).appendChild(addButton); 
			document.getElementById(divID).appendChild(removeButton);
			document.getElementById(divID).appendChild(label);
			var id = "div."+labelValue+lableName;
			var divTag = document.createElement("div");
			divTag.setAttribute("id", id); 
			document.getElementById(divID).appendChild(divTag);
			
			var divTag = document.createElement("div");
			divTag.setAttribute("id", id +'@0');
			document.getElementById(id).appendChild(divTag);
		}else{
			var divTag = document.createElement("div");
			divTag.setAttribute("id", "div."+labelValue+lableName);
			document.getElementById(divID).appendChild(label);  
			document.getElementById(divID).appendChild(divTag);
		}
    };

    $scope.dropBoxLayout = function(labelLevel, attributeName, many , refValue, listemunerateValues){
		var br = document.createElement("BR");

		if (labelLevel.length  < 1){
			var divID = "DynamicTemplate";
		} else if (labelLevel.endsWith('.')){
			var divID = 'div.'+ labelLevel.substring(0, labelLevel.length-1);
		}	


	var label = document.createElement("Label")
	if (matching.includes(attributeName)){
		var labeltext = document.createTextNode(attributeName + "*!");
	}else {
		var labeltext = document.createTextNode(attributeName);
	}
	label.appendChild(labeltext);

	var listField = document.createElement("SELECT");
	listField.setAttribute("class" , "form-control");
	listField.setAttribute("style" , "width:300px;");
	listField.setAttribute("ng-disabled" , "temp.policy.readOnly");
	
	for (i=0; i < listemunerateValues.length; i += 1) {
	    option = document.createElement('option');
	    option.setAttribute('value', listemunerateValues[i]);
	    option.appendChild(document.createTextNode(listemunerateValues[i]));
	    listField.appendChild(option);
	}
	listField.setAttribute("id" , ''+ labelLevel + attributeName + '');
	
	enumKeyList.push(attributeName);
	
	document.getElementById(divID).appendChild(label);  
	document.getElementById(divID).appendChild(br);	
			
	if(many == true){
		document.getElementById(divID).appendChild(listField).multiple = true;
		plainAttributeKeys.push(labelLevel + attributeName+'*'+true);
	}else {
		document.getElementById(divID).appendChild(listField).multiple = false;
		plainAttributeKeys.push(labelLevel + attributeName+'*'+false);
	}

	if($scope.temp.policy.ruleData != null){
		if (many == true){
			document.getElementById(labelLevel +attributeName).options[0].selected = false;
			for (i=0; i < listemunerateValues.length; i += 1) {
				var testValue = $scope.temp.policy.ruleData[labelLevel +attributeName+'@' + i];
				if (testValue === undefined){
					testValue = $scope.temp.policy.ruleData[labelLevel +attributeName];
					}
				var location = listemunerateValues.indexOf(testValue);
				if (location!=-1){
					document.getElementById(labelLevel +attributeName).options[location].selected = true;
					}
				}			
			}else {
	                document.getElementById(labelLevel + attributeName).value = $scope.temp.policy.ruleData[labelLevel + attributeName];	
				
			}
		}
    };
    
    function onlyUnique(value, index, self) { 
        return self.indexOf(value) === index;
    };
    
    
    function checkDictionary(value){
    	for (i = 0; i < $scope.microServiceAttributeDictionaryDatas.length; i++) {
    		if ($scope.microServiceAttributeDictionaryDatas[i].name.localeCompare(value)){
    			return true;
    		}
    	}
    	
    }
    $scope.savePolicy = function(policy){
    	if(policy.itemContent != undefined){
    		$scope.refreshCheck = true; 
        	$scope.policyNavigator = policy.itemContent;
        	policy.itemContent = "";
    	}
    	$scope.savebutton = false;
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
                }else if (searchElement.nodeName == 'BUTTON'){
    				searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
    				key = splitPlainAttributeKey[0]+'@0';
    			}
    			if(searchElement != null){
    				var keySplit = key.split(dot);
    				var elumentLocation = keySplit.length;
    				var enumKey = key;
    				if (elumentLocation > 1){
    					enumKey = keySplit[keySplit.length - 1];
    				}
    				var aWhiteSpace = " ";
    				if (enumKeyList.indexOf(enumKey) != -1){
						if (splitPlainAttributeKey[1].indexOf("true") !== -1){
							var multiSlect = [];
							for ( var i = 0; i < searchElement.selectedOptions.length; i++) {
								multiSlect.push(searchElement.selectedOptions[i].value);
								}
							jsonPolicy[key]= multiSlect;
						}else{
							//set a space due to empty value caused JSON format error in PolicyRestAdapter and remove it in back-end.
							if(searchElement.value == ""){
								searchElement.value = aWhiteSpace;
							}
							console.log(" searchElement.value = > " + searchElement.value);
							jsonPolicy[key]= searchElement.value;
						}
    				} else {
        				if(searchElement.value != null){
							//set a default value due to empty value caused JSON format error in PolicyRestAdapter
							if(searchElement.value == ""){
								searchElement.value = aWhiteSpace;
							}
							console.log(" searchElement.value = > " + searchElement.value);
        					jsonPolicy[key]= searchElement.value;
        				}
    				}
    			}
    		}
    	}
        var uuu = "policycreation/save_policy";
        var postData={policyData: policy, policyJSON : jsonPolicy};
        $.ajax({
            type : 'POST',
            url : uuu,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(postData),
            success : function(data){
                $scope.$apply(function(){
                	$scope.data=data.policyData;
                	if($scope.data == 'success'){
                		$scope.temp.policy.readOnly = 'true';
                		$scope.safetyChecker = data.policyData.split("#")[2];
                		if ($scope.safetyChecker!=undefined) {
                			Notification.success($scope.safetyChecker);
                		}
                		$scope.pushStatus=data.policyData.split("&")[1];
                		if($scope.pushStatus=="successPush"){
                			Notification.success("Policy pushed successfully");
                		}
                		Notification.success("Policy Saved Successfully.");	
                	}else if ($scope.data == 'PolicyExists'){
						$scope.savebutton = true;
						Notification.error("Policy Already Exists with Same Name in Scope.");
					}
                });
                console.log($scope.data);
            },
            error : function(data){
            	Notification.error("Error Occured while saving Policy.");
            }
        });
    };
    
    $scope.validatePolicy = function(policy){
    	document.getElementById("validate").innerHTML = "";
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
    			}else if (searchElement.nodeName == 'BUTTON'){
    				searchElement = document.getElementById(splitPlainAttributeKey[0]+'@0');
    				key = splitPlainAttributeKey[0]+'@0';
    			}
    			if(searchElement != null){
    				if (enumKeyList.indexOf(key) != -1){
						if (splitPlainAttributeKey[1].indexOf("true") !== -1){
							var multiSlect = [];
							for ( var i = 0; i < searchElement.selectedOptions.length; i++) {
								multiSlect.push(searchElement.selectedOptions[i].value);
								}
							jsonPolicy[key]= multiSlect;
						}else{
							jsonPolicy[key]= searchElement.value;
						}
    				} else {
        				if(searchElement.value != null){
        					jsonPolicy[key]= searchElement.value;
        					if(searchElement.getAttribute("required")){
        						if(!searchElement.value){
        							return;
        						}
        					}        					
        				}
    				}
    			}
    		}
    	}
        var uuu = "policyController/validate_policy.htm";
        var postData={policyData: policy, policyJSON : jsonPolicy};
 		$.ajax({
 			type : 'POST',
 			url : uuu,
 			dataType: 'json',
 			contentType: 'application/json',
 			data: JSON.stringify(postData),
 			success : function(data){
 				$scope.$apply(function(){
 					$scope.validateData = data.data.replace(/\"/g, "");
					$scope.data=data.data.substring(1,8);
						var size = data.data.length;
						if($scope.data == 'success'){
							Notification.success("Validation Success.");
							$scope.savebutton = false;
							if (size > 18){
								var displayWarning = data.data.substring(19,size  - 1);
								document.getElementById("validate").innerHTML = "Safe Policy Warning Message  :  "+displayWarning;
								document.getElementById("validate").style.color = "white";
								document.getElementById("validate").style.backgroundColor = "skyblue";
							}
 						}else{
 							Notification.error("Validation Failed.");
 							document.getElementById("validate").innerHTML = $scope.validateData;
 							document.getElementById("validate").style.color = "white";
 							document.getElementById("validate").style.backgroundColor = "red";
 							$scope.savebutton = true;
 						}
 						
 				});
 				console.log($scope.data);	
 			},
 			error : function(data){
 				Notification.error("Validation Failed.");
 				$scope.savebutton = true;
 			}
 		});
    };

    function extend(obj, src) {
        for (var key in src) {
            if (src.hasOwnProperty(key)) obj[key] = src[key];
        }
        return obj;
    }
}]);