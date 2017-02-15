/*-
 * ============LICENSE_START=======================================================
 * ECOMP-REST
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

package org.openecomp.policy.rest.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.json.JSONObject;
import org.openecomp.policy.rest.XACMLRestProperties;

import com.att.research.xacml.util.XACMLProperties;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;


public class MSModelUtitils {
	
	private static final Log logger	= LogFactory.getLog(MSModelUtitils.class);
 
	private HashMap<String,MSAttributeObject > classMap = new HashMap<String,MSAttributeObject>();

	public HashMap<String, MSAttributeObject> processEpackage(String xmiFile){
		EPackage root = getEpackage(xmiFile);
	    TreeIterator<EObject> treeItr = root.eAllContents();
	    String className = null;
	    String returnValue = null;
	    
		//    Pulling out dependency from file
	    while (treeItr.hasNext()) {	    
	    	EObject obj = (EObject) treeItr.next();
	        if (obj instanceof EClassifier) {
	        	EClassifier eClassifier = (EClassifier) obj;
	        	className = eClassifier.getName();
	    		
	    		if (obj instanceof EEnum) {
	    		//	getEEnum();
	    			returnValue = null;
	    		}else if (obj instanceof EClass) {
	        		String temp = getDependencyList(eClassifier, className).toString();
	        		returnValue = StringUtils.replaceEach(temp, new String[]{"[", "]"}, new String[]{"", ""});
	        		getAttributes(className, returnValue, root);
	    		}        		   		
	        }
	    }
	    return classMap;
	}

	private EPackage getEpackage(String xmiFile) {
		ResourceSet resSet = new ResourceSetImpl();
	    Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	    Map<String, Object> m = reg.getExtensionToFactoryMap();
	    m.put("xmi", new XMIResourceFactoryImpl());
	    Resource resource = resSet.getResource(URI.createFileURI(xmiFile), true);
	    try {
			resource.load(Collections.EMPTY_MAP);
		} catch (IOException e) {
			logger.error("Error loading Encore Resource for new Model");
		}
	    
	    EPackage root = (EPackage) resource.getContents().get(0);

		return root;
	}

	private void getEEnum() {
		
	}
	
	public void getAttributes(String className, String dependency, EPackage root) {
		List<String> dpendList = null;
	    if (dependency!=null){
	    	dpendList = new ArrayList<String>(Arrays.asList(dependency.split(",")));
	    }
		MSAttributeObject msAttributeObject = new MSAttributeObject();
		msAttributeObject.setClassName(className);
		HashMap<String, String> returnRefList = getRefAttributeList(root, className);
		HashMap<String, String> returnAttributeList = getAttributeList(root, className);
		HashMap<String, String> returnSubList = getSubAttributeList(root, className);
		msAttributeObject.setAttribute(returnAttributeList);
		msAttributeObject.setRefAttribute(returnRefList);
		msAttributeObject.setSubClass(returnSubList);
		msAttributeObject.setDependency(dpendList.toString());
		this.classMap.put(className, msAttributeObject);	
	}
	
	private HashMap<String, String> getSubAttributeList(EPackage root, String className) {
	    //EPackage root = (EPackage) resource.getContents().get(0);
	    TreeIterator<EObject> treeItr = root.eAllContents();
	    boolean requiredAttribute = false; 
	    HashMap<String, String> subAttribute = new HashMap<String, String>();
	    int rollingCount = 0;
	    int processClass = 0;
	    boolean annotation = false;
		    
		//    Pulling out dependency from file
	    while (treeItr.hasNext() && rollingCount < 2) {	 
	    	
	    	EObject obj = treeItr.next();
	        if (obj instanceof EClassifier) {
	        	requiredAttribute = isRequiredAttribute(obj,  className); 	
	        	if (requiredAttribute){
	        		processClass++;
	        	}
	        	rollingCount = rollingCount+processClass;
			}
			
	        if (requiredAttribute)   {
	        	if (obj instanceof EStructuralFeature) {
	        		EStructuralFeature eStrucClassifier = (EStructuralFeature) obj;
	        		if (eStrucClassifier.getEAnnotations().size() != 0) {
	        			annotation = testAnnotation(eStrucClassifier);
						if (annotation &&  obj instanceof EReference) {
							EClass refType = ((EReference) obj).getEReferenceType();
					//		String array = arrayCheck(((EStructuralFeature) obj).getUpperBound());
							if(!refType.toString().contains("eProxyURI:")){
								subAttribute.put(eStrucClassifier.getName(), refType.getName());						
							}
						}	
	        		}
	        	}
	       }
	    }
		return subAttribute;
	}
	
	public String checkDefultValue(String defultValue) {
		if (defultValue!=null){
			return ":defaultValue-"+ defultValue;
		}
		return ":defaultValue-NA";
		
	}
	
	public String checkRequiredPattern(int upper, int lower) {	
		String pattern = XACMLProperties.getProperty(XACMLRestProperties.PROP_XCORE_REQUIRED_PATTERN);
		
		if (pattern!=null){
			if (upper == Integer.parseInt(pattern.split(",")[1]) && lower==Integer.parseInt(pattern.split(",")[0])){
				return ":required-true";
			}
		}
				
		return ":required-false";
	}
	
	public JSONObject buildJavaObject(HashMap<String, String> map, String attributeType){

		JSONObject returnValue = new JSONObject(map);
		
		return returnValue;
		
	}
	
	public HashMap<String, String> getRefAttributeList(EPackage root, String className){
	    
	    TreeIterator<EObject> treeItr = root.eAllContents();
	    boolean requiredAttribute = false; 
	    HashMap<String, String> refAttribute = new HashMap<String, String>();
	    int rollingCount = 0;
	    int processClass = 0;
	    boolean annotation = false;
		    
		//    Pulling out dependency from file
	    while (treeItr.hasNext()) {	    
	    	EObject obj = treeItr.next();
	        if (obj instanceof EClassifier) {
	        	requiredAttribute = isRequiredAttribute(obj,  className); 	
	        	if (requiredAttribute){
	        		processClass++;
	        	}
	        	rollingCount = rollingCount+processClass;
			}
			
	        if (requiredAttribute)   {
	        	if (obj instanceof EStructuralFeature) {
	        		EStructuralFeature eStrucClassifier = (EStructuralFeature) obj;
	        		if (eStrucClassifier.getEAnnotations().size() != 0) {
	        			annotation = testAnnotation(eStrucClassifier);
						if ( annotation &&  obj instanceof EReference) {
							EClass refType = ((EReference) obj).getEReferenceType();
							if(refType.toString().contains("eProxyURI:")){
								String one = refType.toString().split("eProxyURI:")[1];
								String refValue = StringUtils.replaceEach(one.split("#")[1], new String[]{"//", ")"}, new String[]{"", ""});							
								refAttribute.put(eStrucClassifier.getName(), refValue);							
							} else {
								String array = arrayCheck(((EStructuralFeature) obj).getUpperBound());
								if (array.contains("false")){
									array = "";
								}
								refAttribute.put(eStrucClassifier.getName(), refType.getName() + array);
							}
						}	
	        		}
	        	}
	       }
	    }
		return refAttribute;
	}
	
	private boolean testAnnotation(EStructuralFeature eStrucClassifier) {
		String annotationType = null;
		EAnnotation eAnnotation = null;
		String ecompType = null;
		
		EList<EAnnotation> value = eStrucClassifier.getEAnnotations();
		
		for (int i = 0; i < value.size(); i++){
			annotationType = value.get(i).getSource();
			eAnnotation = eStrucClassifier.getEAnnotations().get(i);
			ecompType = eAnnotation.getDetails().get(0).getValue();
			if (annotationType.contains("http://localhost.com") && ecompType.contains("configuration")){
				return true;
			}
		}

		return false;
	}

	public boolean isRequiredAttribute(EObject obj, String className){
    	EClassifier eClassifier = (EClassifier) obj;
    	String workingClass = eClassifier.getName();
    	workingClass.trim();
		if (workingClass.equalsIgnoreCase(className)){
			return  true;
		}

		return false;
	}

	public HashMap<String, String> getAttributeList(EPackage root, String className){
	    
	    TreeIterator<EObject> treeItr = root.eAllContents();
	    boolean reference = false;
	    boolean requiredAttribute = false; 
	    HashMap<String, String> refAttribute = new HashMap<String, String>();
	    String workingClass = null;
	    boolean annotation = false;
		    
		//    Pulling out dependency from file
	    while (treeItr.hasNext()) {	    
	    	reference = false;
	    	EObject obj = treeItr.next();
	        if (obj instanceof EClassifier) {
	        	requiredAttribute = isRequiredAttribute(obj,  className );
			}
			  
	        if (requiredAttribute){
	        	if (obj instanceof EStructuralFeature) {
	        		EStructuralFeature eStrucClassifier = (EStructuralFeature) obj;
	        		if (eStrucClassifier.getEAnnotations().size() != 0) {
	        			annotation = testAnnotation(eStrucClassifier);
						if (annotation && !(obj instanceof EReference)) {
							String name = eStrucClassifier.getName();
							String defaultValue = checkDefultValue(((EStructuralFeature) obj).getDefaultValueLiteral());
							String eType = eStrucClassifier.getEType().getInstanceClassName();
							String array = arrayCheck(((EStructuralFeature) obj).getUpperBound());
							String required = checkRequiredPattern(((EStructuralFeature) obj).getUpperBound(), ((EStructuralFeature) obj).getLowerBound());
							String attributeValue =  eType + defaultValue + required + array;
							refAttribute.put(name, attributeValue);	
						}
	        		}
	            }
	        }
	    }
		return refAttribute;
		
	}
	
	public String arrayCheck(int upperBound) {
		
		if (upperBound == -1){
			return ":MANY-true";
		}
		
		return ":MANY-false";
	}
	
	public List<String> getDependencyList(EClassifier eClassifier, String className){
		List<String> returnValue = new ArrayList<>();;
		EList<EClass> somelist = ((EClass) eClassifier).getEAllSuperTypes();
		if (somelist.isEmpty()){
			return returnValue;
		}
		for(EClass depend: somelist){
			if (depend.toString().contains("eProxyURI:")){
				String one = depend.toString().split("eProxyURI:")[1];
				String value = StringUtils.replaceEach(one.split("#")[1], new String[]{"//", ")"}, new String[]{"", ""});
				returnValue.add(value);
			}
		}

		return returnValue;
	}
	
	public String createJson(HashMap<String, String> subClassAttributes, HashMap<String, MSAttributeObject> classMap, String className) {
		String json = "";
		JSONObject jsonObj; 
		
		jsonObj = new JSONObject();
		
		Map<String, String> missingValues = new HashMap<String, String>();
		Map<String, String> workingMap = new HashMap<String, String>();
		
		for ( Entry<String, String> map : classMap.get(className).getRefAttribute().entrySet()){
			String value = map.getValue().split(":")[0];
			if (value!=null){
				workingMap =  classMap.get(value).getRefAttribute();
				for ( Entry<String, String> subMab : workingMap.entrySet()){
					String value2 = subMab.getValue().split(":")[0];
					if (!subClassAttributes.containsValue(value2)){
						missingValues.put(subMab.getKey(), subMab.getValue());
						classMap.get(value).addAttribute(subMab.getKey(), subMab.getValue());
					}
				}
				
			}
		}
		
		if (!missingValues.isEmpty()){
			for (Entry<String, String> addValue : missingValues.entrySet()){
				subClassAttributes.put(addValue.getKey(), addValue.getValue().split(":")[0]);
			}
		}
		
		for ( Map.Entry<String, String>  map : subClassAttributes.entrySet()){
			jsonObj.put(map.getValue().split(":")[0], classMap.get(map.getValue().split(":")[0]).getAttribute());	
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Json value: " + jsonObj);
		}
		
		return jsonObj.toString();		
	}
	
	public String createSubAttributes(ArrayList<String> dependency, HashMap<String, MSAttributeObject> classMap, String modelName) {
		
		HashMap <String,  String>  workingMap = new HashMap<String,String>();
		MSAttributeObject tempObject = new MSAttributeObject(); 
		HashMap <String, String> refAttribute =  new HashMap<String,String>();
		 HashMap <String,  String>  workingSubMap = new HashMap<String,String>();
		Map<String, String> tempPefAttribute = null;
		LinkedList linkedList = new LinkedList();
		String addedValue = null;
		
		boolean addingValues = false;
		
		if (dependency!=null){
			if (dependency.size()==0){
				return "{}";
			}	
			dependency.add(modelName);
			for (String element: dependency){
				tempObject = classMap.get(element);
				if (tempObject!=null){
					workingMap.putAll(classMap.get(element).getSubClass());
		            //        workingSubMap = CheckForSecondSubClass(classMap.get(element).getSubClass(), classMap);
		             //       if (workingSubMap!=null){
		            //            workingMap.putAll(workingSubMap);
		           //         }
				}
			}
		}
		
		String returnValue = createJson(workingMap, classMap, modelName);			
		return returnValue;
	}

    private HashMap<String, String> CheckForSecondSubClass(HashMap<String, String> subClass, HashMap<String, MSAttributeObject> mainMap) {
        MSAttributeObject tempObject = new MSAttributeObject(); 
        HashMap<String, String> subClassValue = new HashMap<String,String>();
        
        for (Entry<String, String> classSet : subClass.entrySet()){
            String key = classSet.getKey();
            String value = classSet.getValue();
            tempObject = mainMap.get(value);
            subClassValue = tempObject.getSubClass();
            if (subClassValue!=null){
                return subClassValue;
            }
        }
        return null;
        
    }
    
	public ArrayList<String> getFullDependencyList(ArrayList<String> dependency, HashMap<String,MSAttributeObject > classMap) {
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> workingList = new ArrayList<String>();
		int i = 0;
		MSAttributeObject newDepend = null;
		returnList.addAll(dependency);
		for (String element : dependency ){
			if (classMap.containsKey(element)){
				MSAttributeObject value = classMap.get(element);
				String rawValue = StringUtils.replaceEach(value.getDependency(), new String[]{"[", "]"}, new String[]{"", ""});
				workingList = new ArrayList<String>(Arrays.asList(rawValue.split(",")));	
				for(String depend : workingList){
					if (!returnList.contains(depend) && !depend.isEmpty()){
						returnList.add(depend.trim());
					}
				}
			}
		}
		
		return returnList;
	}
}
