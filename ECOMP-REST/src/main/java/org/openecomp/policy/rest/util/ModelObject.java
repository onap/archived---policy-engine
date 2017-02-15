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
 

package org.openecomp.policy.rest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ModelObject {
	private String name;
	private String parent;
	private List<String> attibutes = new ArrayList<String>();
	private List<String> arrays = new ArrayList<String>();
	private List<Integer> integers = new ArrayList<Integer>();
	private List<ModelObject> subObjects = new ArrayList<ModelObject>();
	private HashMap<String, LinkedList<ModelObject>> subObjectList = new HashMap<String, LinkedList<ModelObject>>();
	private HashMap<String, TextField> attribute = new HashMap<String, TextField>();
	private Map<String, LinkedList<TextField>> arrayTextList =  new HashMap<String, LinkedList<TextField>>();
	private Map<String,  VerticalLayout> textFieldLayout = new HashMap<String, VerticalLayout>();

	private boolean many = false;
	
	public Map<String, LinkedList<TextField>> getArrayTextList() {
		return arrayTextList;
	}
	public void setArrayTextList(Map<String, LinkedList<TextField>> arrayTextList) {
		this.arrayTextList = arrayTextList;
	}
	public void addArrayTextList(String name, TextField textField ){
		LinkedList<TextField> list = new LinkedList<TextField>();
		if (getArrayTextList().get(name) != null){
			list = getArrayTextList().get(name); 
		}

		list.push(textField);
		this.arrayTextList.put(name, list);
	}
	public void removeLastTextList(String name){
		LinkedList<TextField> list = getArrayTextList().get(name); 
		
		list.pop();
		this.arrayTextList.put(name, list);
	}
	public HashMap<String, TextField> getAttribute() {
		return attribute;
	}
	public void setAttribute(HashMap<String, TextField> attribute) {
		this.attribute = attribute;
	}
    public void addAttribute(String name, TextField textField){
    	this.attribute.put(name, textField);
    }
	public List<String> getAttibutes() {
		return attibutes;
	}
	public void setAttibutes(List<String> attibutes) {
		this.attibutes = attibutes;
	}
	public List<String> getArrays() {
		return arrays;
	}
	public void setArrays(List<String> arrays) {
		this.arrays = arrays;
	}
	public List<Integer> getIntegers() {
		return integers;
	}
	public void setIntegers(List<Integer> integers) {
		this.integers = integers;
	}
	public List<ModelObject> getSubObjects() {
		return subObjects;
	}
	public void setSubObjects(List<ModelObject> subObjects) {
		this.subObjects = subObjects;
	}
	public void addSubObject(ModelObject subObjects ){
		this.subObjects.add(subObjects);
	}
	public void addAttributes(String attibutes){
		this.attibutes.add(attibutes);
	}
	public void addArrays(String arrays){
		this.arrays.add(arrays);
	}
	public void addIntegers(Integer integers){
		this.integers.add(integers);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMany() {
		return many;
	}
	public void setMany(boolean many) {
		this.many = many;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public HashMap<String, LinkedList<ModelObject>> getSubObjectList() {
		return subObjectList;
	}
	public void setSubObjectList(HashMap<String, LinkedList<ModelObject>> subObjectList) {
		this.subObjectList = subObjectList;
	}
	public void addSubObjectList(String name, ModelObject object) {
		LinkedList<ModelObject> list = new LinkedList<ModelObject>();
		if (subObjectList.get(name) != null){
			list = subObjectList.get(name); 
		}

		list.push(object);

		this.subObjectList.put(name, list);
	}
	public Map<String,  VerticalLayout> getTextFieldLayout() {
		return textFieldLayout;
	}
	public void setTextFieldLayout(Map<String,  VerticalLayout> textFieldLayout) {
		this.textFieldLayout = textFieldLayout;
	}
	public void addTextFieldLayout(String name, VerticalLayout vLayout){
		this.textFieldLayout.put(name, vLayout);
	}

}
*/
