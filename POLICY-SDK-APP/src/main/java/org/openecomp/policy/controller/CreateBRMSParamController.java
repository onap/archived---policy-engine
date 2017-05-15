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

package org.openecomp.policy.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import org.json.JSONObject;
import org.openecomp.policy.rest.adapter.PolicyRestAdapter;
import org.openecomp.policy.rest.dao.CommonClassDao;
import org.openecomp.policy.rest.jpa.BRMSParamTemplate;
import org.openecomp.policy.rest.jpa.PolicyEntity;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import org.openecomp.policy.xacml.api.XACMLErrorConstants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.openecomp.policy.common.logging.flexlogger.FlexLogger; 
import org.openecomp.policy.common.logging.flexlogger.Logger;

@Controller
@RequestMapping("/")
public class CreateBRMSParamController extends RestrictedBaseController {
	private static final Logger logger = FlexLogger.getLogger(CreateBRMSParamController.class);

	private static CommonClassDao commonClassDao;

	@Autowired
	private CreateBRMSParamController(CommonClassDao commonClassDao){
		CreateBRMSParamController.commonClassDao = commonClassDao;
	}

	public CreateBRMSParamController(){}
	protected PolicyRestAdapter policyAdapter = null;
	private ArrayList<Object> attributeList;

	private HashMap<String, String> dynamicLayoutMap;


	@RequestMapping(value={"/policyController/getBRMSTemplateData.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView getBRMSParamPolicyRuleData(HttpServletRequest request, HttpServletResponse response) throws Exception{
		dynamicLayoutMap = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode root = mapper.readTree(request.getReader());
		String rule = findRule(root.get("policyData").toString().replaceAll("^\"|\"$", ""));
		generateUI(rule);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		String responseString = mapper.writeValueAsString(dynamicLayoutMap);
		JSONObject j = new JSONObject("{policyData: " + responseString + "}");
		out.write(j.toString());
		return null;
	}

	protected String findRule(String ruleTemplate) {
		List<Object> datas = commonClassDao.getData(BRMSParamTemplate.class);
		for (Object data: datas){
			BRMSParamTemplate  bRMSParamTemplate = (BRMSParamTemplate) data;
			if(bRMSParamTemplate.getRuleName().equals(ruleTemplate)){
				return bRMSParamTemplate.getRule();
			}
		}
		return null;
	}

	protected void generateUI(String rule) {
		if(rule!=null){
			try {
				String params = "";
				Boolean flag = false;
				Boolean comment = false;
				String lines[] = rule.split("\n");
				for(String line : lines){
					if (line.isEmpty() || line.startsWith("//")) {
						continue;
					}
					if (line.startsWith("/*")) {
						comment = true;
						continue;
					}
					if (line.contains("//")) {
						line = line.split("\\/\\/")[0];
					}
					if (line.contains("/*")) {
						comment = true;
						if (line.contains("*/")) {
							try {
								comment = false;
								line = line.split("\\/\\*")[0]
										+ line.split("\\*\\/")[1].replace("*/", "");
							} catch (Exception e) {
								line = line.split("\\/\\*")[0];
							}
						} else {
							line = line.split("\\/\\*")[0];
						}
					}
					if (line.contains("*/")) {
						comment = false;
						try {
							line = line.split("\\*\\/")[1].replace("*/", "");
						} catch (Exception e) {
							line = "";
						}
					}
					if (comment) {
						continue;
					}
					if (flag) {
						params = params + line;
					}
					if (line.contains("declare Params")) {
						params = params + line;
						flag = true;
					}
					if (line.contains("end") && flag) {
						break;
					}
				}
				params = params.replace("declare Params", "").replace("end", "")
						.replaceAll("\\s+", "");
				String[] components = params.split(":");
				String caption = "";
				for (int i = 0; i < components.length; i++) {
					String type = "";
					if (i == 0) {
						caption = components[i];
					}
					if(caption.equals("")){
						break;
					}
					String nextComponent = "";
					try {
						nextComponent = components[i + 1];
					} catch (Exception e) {
						nextComponent = components[i];
					}
					if (nextComponent.startsWith("String")) {
						type = "String";
						createField(caption, type);
						caption = nextComponent.replace("String", "");
					} else if (nextComponent.startsWith("int")) {
						type = "int";
						createField(caption, type);
						caption = nextComponent.replace("int", "");
					}
				}
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_SYSTEM_ERROR + e);
			}
		}
	}
	
	private void createField(String caption, String type) {
		dynamicLayoutMap.put(caption, type);
	}


	@SuppressWarnings("unchecked")
	public void prePopulateBRMSParamPolicyData(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		attributeList = new ArrayList<Object>();
		dynamicLayoutMap = new HashMap<String, String>();
		if (policyAdapter.getPolicyData() instanceof PolicyType) {
			PolicyType policy = (PolicyType) policyAdapter.getPolicyData();
			policyAdapter.setOldPolicyFileName(policyAdapter.getPolicyName());
			// policy name value is the policy name without any prefix and
			// Extensions.
			String policyNameValue = policyAdapter.getPolicyName().substring(policyAdapter.getPolicyName().indexOf("BRMS_Param_") + 11);
			if (logger.isDebugEnabled()) {
				logger.debug("Prepopulating form data for BRMS RAW Policy selected:" + policyAdapter.getPolicyName());
			}
			policyAdapter.setPolicyName(policyNameValue);
			String description = "";
			try{
				description = policy.getDescription().substring(0, policy.getDescription().indexOf("@CreatedBy:"));
			}catch(Exception e){
				description = policy.getDescription();
			}
			policyAdapter.setPolicyDescription(description);
			// Set Attributes. 
			AdviceExpressionsType expressionTypes = ((RuleType)policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().get(0)).getAdviceExpressions();
			for( AdviceExpressionType adviceExpression: expressionTypes.getAdviceExpression()){
				for(AttributeAssignmentExpressionType attributeAssignment: adviceExpression.getAttributeAssignmentExpression()){
					if(attributeAssignment.getAttributeId().startsWith("key:")){
						Map<String, String> attribute = new HashMap<String, String>();
						String key = attributeAssignment.getAttributeId().replace("key:", "");
						attribute.put("key", key);
						JAXBElement<AttributeValueType> attributevalue = (JAXBElement<AttributeValueType>) attributeAssignment.getExpression();
						String value = (String) attributevalue.getValue().getContent().get(0);
						attribute.put("value", value);
						attributeList.add(attribute);
                    }else if(attributeAssignment.getAttributeId().startsWith("dependencies:")){
                        ArrayList<String> dependencies = new ArrayList<String>(Arrays.asList(attributeAssignment.getAttributeId().replace("dependencies:", "").split(",")));
                        if(dependencies.contains("")){
                            dependencies.remove("");
                        }
                        policyAdapter.setBrmsDependency(dependencies);
                    }else if(attributeAssignment.getAttributeId().startsWith("controller:")){
                        policyAdapter.setBrmsController(attributeAssignment.getAttributeId().replace("controller:", ""));
					}
				}
				policyAdapter.setAttributes(attributeList);
			}
			paramUIGenerate(policyAdapter, entity);
			// Get the target data under policy.
			policyAdapter.setDynamicLayoutMap(dynamicLayoutMap);
			if(policyAdapter.getDynamicLayoutMap().size() > 0){
				LinkedHashMap<String,String> drlRule = new LinkedHashMap<String, String>();
				for(Object keyValue: policyAdapter.getDynamicLayoutMap().keySet()){
					drlRule.put(keyValue.toString(), policyAdapter.getDynamicLayoutMap().get(keyValue).toString());
				}
				policyAdapter.setRuleData(drlRule);
			}	
			TargetType target = policy.getTarget();
			if (target != null) {
				// Under target we have AnyOFType
				List<AnyOfType> anyOfList = target.getAnyOf();
				if (anyOfList != null) {
					Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
					while (iterAnyOf.hasNext()) {
						AnyOfType anyOf = iterAnyOf.next();
						// Under AnyOFType we have AllOFType
						List<AllOfType> allOfList = anyOf.getAllOf();
						if (allOfList != null) {
							Iterator<AllOfType> iterAllOf = allOfList.iterator();
							while (iterAllOf.hasNext()) {
								AllOfType allOf = iterAllOf.next();
								// Under AllOFType we have Match
								List<MatchType> matchList = allOf.getMatch();
								if (matchList != null) {
									Iterator<MatchType> iterMatch = matchList.iterator();
									while (iterMatch.hasNext()) {
										MatchType match = iterMatch.next();
										//
										// Under the match we have attribute value and
										// attributeDesignator. So,finally down to the actual attribute.
										//
										AttributeValueType attributeValue = match.getAttributeValue();
										String value = (String) attributeValue.getContent().get(0);
										AttributeDesignatorType designator = match.getAttributeDesignator();
										String attributeId = designator.getAttributeId();

										if (attributeId.equals("RiskType")){
											policyAdapter.setRiskType(value);
										}
										if (attributeId.equals("RiskLevel")){
											policyAdapter.setRiskLevel(value);
										}
										if (attributeId.equals("guard")){
											policyAdapter.setGuard(value);
										}
										if (attributeId.equals("TTLDate") && !value.contains("NA")){
											String newDate = convertDate(value, true);
											policyAdapter.setTtlDate(newDate);
										}
									}
								}
							}
						}
					}
				}
			}
		} 		
	}

	private String convertDate(String dateTTL, boolean portalType) {
		String formateDate = null;
		String[] date;
		String[] parts;
		
		if (portalType){
			parts = dateTTL.split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0] + "T05:00:00.000Z";
		} else {
			date  = dateTTL.split("T");
			parts = date[0].split("-");
			formateDate = parts[2] + "-" + parts[1] + "-" + parts[0];
		}
		return formateDate;
	}
	// This method generates the UI from rule configuration
	private void paramUIGenerate(PolicyRestAdapter policyAdapter, PolicyEntity entity) {
		String data = entity.getConfigurationData().getConfigBody();
		if(data != null){
			try {	
				String params = "";
				Boolean flag = false;
				Boolean comment = false;
				for (String line : data.split("\n")) {
					if (line.isEmpty() || line.startsWith("//")) {
						continue;
					}
					if(line.contains("<$%BRMSParamTemplate=")){
						String value = line.substring(line.indexOf("<$%"),line.indexOf("%$>"));
						value = value.replace("<$%BRMSParamTemplate=", "");
						policyAdapter.setRuleName(value);
					}
					if (line.startsWith("/*")) {
						comment = true;
						continue;
					}
					if (line.contains("//")) {
						if(!(line.contains("http://") || line.contains("https://"))){
							line = line.split("\\/\\/")[0];
						}
					}
					if (line.contains("/*")) {
						comment = true;
						if (line.contains("*/")) {
							try {
								comment = false;
								line = line.split("\\/\\*")[0]
										+ line.split("\\*\\/")[1].replace(
												"*/", "");
							} catch (Exception e) {
								line = line.split("\\/\\*")[0];
							}
						} else {
							line = line.split("\\/\\*")[0];
						}
					}
					if (line.contains("*/")) {
						comment = false;
						try {
							line = line.split("\\*\\/")[1]
									.replace("*/", "");
						} catch (Exception e) {
							line = "";
						}
					}
					if (comment) {
						continue;
					}
					if (flag) {
						params = params + line;
					}
					if (line.contains("rule") && line.contains(".Params\"")) {
						params = params + line;
						flag = true;
					}
					if (line.contains("end") && flag) {
						break;
					}
				}
				params = params.substring(params.indexOf(".Params\"")+ 8);
                params = params.replaceAll("\\s+", "").replace("salience1000whenthenParamsparams=newParams();","")
                        .replace("insert(params);end", "")
                        .replace("params.set", "");
				String[] components = params.split(";");
				if(components!= null && components.length > 0){
					for (int i = 0; i < components.length; i++) {
						String value = null;
						String caption = components[i].substring(0,
								components[i].indexOf("("));
						caption = caption.substring(0, 1).toLowerCase() + caption.substring(1);
						if (components[i].contains("(\"")) {
							value = components[i]
									.substring(components[i].indexOf("(\""),
											components[i].indexOf("\")"))
									.replace("(\"", "").replace("\")", "");
						} else {
							value = components[i]
									.substring(components[i].indexOf("("),
											components[i].indexOf(")"))
									.replace("(", "").replace(")", "");
						}
						dynamicLayoutMap.put(caption, value);

					}
				}
			} catch (Exception e) {
				logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + e.getMessage());
			} 
		}
		
	}

	// set View Rule
	@SuppressWarnings("unchecked")
	@RequestMapping(value={"/policyController/ViewBRMSParamPolicyRule.htm"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	public ModelAndView setViewRule(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PolicyRestAdapter policyData = (PolicyRestAdapter)mapper.readValue(root.get("policyData").get("policy").toString(), PolicyRestAdapter.class);
			policyData.setDomainDir(root.get("policyData").get("model").get("name").toString().replace("\"", ""));
			if(root.get("policyData").get("model").get("type").toString().replace("\"", "").equals("file")){
				policyData.isEditPolicy = true;
			}

			String body = "";

			body = "/* Autogenerated Code Please Don't change/remove this comment section. This is for the UI purpose. \n\t " +
					"<$%BRMSParamTemplate=" + policyData.getRuleName() + "%$> \n */ \n";
			body = body + findRule((String) policyData.getRuleName()) + "\n";
			String generatedRule = "rule \""+ policyData.getDomainDir().replace("\\", ".") +".Config_BRMS_Param_" + policyData.getPolicyName()+".Params\" \n\tsalience 1000 \n\twhen\n\tthen\n\t\tParams params = new Params();";

			if(policyData.getRuleData().size() > 0){ 
				for(Object keyValue: policyData.getRuleData().keySet()){ 
					String key = keyValue.toString().substring(0, 1).toUpperCase() + keyValue.toString().substring(1); 
					if (keyValue.equals("String")) { 
						generatedRule = generatedRule + "\n\t\tparams.set" 
								+ key + "(\"" 
								+ policyData.getRuleData().get(keyValue).toString() + "\");"; 
					} else { 
						generatedRule = generatedRule + "\n\t\tparams.set" 
								+ key + "(" 
								+ policyData.getRuleData().get(keyValue).toString() + ");"; 
					} 
				} 
			}
			generatedRule = generatedRule
					+ "\n\t\tinsert(params);\nend";
			logger.info("New rule generated with :" + generatedRule);
			body = body + generatedRule;
			// Expand the body. 
			Map<String,String> copyMap=new HashMap<>();
			copyMap.putAll((Map<? extends String, ? extends String>) policyData.getRuleData());
			copyMap.put("policyName", policyData.getDomainDir().replace("\\", ".") +".Config_BRMS_Param_" + policyData.getPolicyName());
			copyMap.put("policyScope", policyData.getDomainDir().replace("\\", "."));
			copyMap.put("policyVersion", "1");
			//Finding all the keys in the Map data-structure.
			Set<String> keySet= copyMap.keySet();
			Iterator<String> iterator = keySet.iterator(); 
			Pattern p;
			Matcher m;
			while(iterator.hasNext()) {
				//Converting the first character of the key into a lower case. 
				String input= iterator.next();
				String output  = Character.toLowerCase(input.charAt(0)) +
						(input.length() > 1 ? input.substring(1) : "");
				//Searching for a pattern in the String using the key. 
				p=Pattern.compile("\\$\\{"+output+"\\}");   
				m=p.matcher(body);
				//Replacing the value with the inputs provided by the user in the editor. 
				body=m.replaceAll(copyMap.get(input));
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(body);
			JSONObject j = new JSONObject("{policyData: " + responseString + "}");
			out.write(j.toString());
			return null;
		} catch (Exception e) {
			logger.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + e);
		}
		return null;	
	}
}
