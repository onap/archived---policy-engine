/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
package org.onap.policy.xacml.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.onap.policy.common.logging.eelf.MessageCodes;
import org.onap.policy.common.logging.eelf.PolicyLogger;

import com.att.research.xacml.api.AttributeAssignment;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttribute;
import com.att.research.xacml.std.StdAttributeAssignment;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdMutableAdvice;
import com.att.research.xacml.std.StdMutableObligation;
import com.att.research.xacml.util.XACMLPolicyScanner.Callback;
import com.att.research.xacml.util.XACMLPolicyScanner.CallbackResult;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;

/**
 * class XACMLPolicyScanner
 * 
 * This class traverses the hierarchy of a XACML 3.0 policy. You can optionally pass a Callback class
 * and override any desired methods to retrieve information from a policy. 
 * 
 *
 */
public class XACMLPolicyScanner {
	
	private static final Log logger				= LogFactory.getLog(XACMLPolicyScanner.class);
	private Object policyObject = null;
	private Callback callback = null;
	
	public XACMLPolicyScanner(Path filename, Callback callback) {
		try (InputStream is = Files.newInputStream(filename)) {
			this.policyObject = XACMLPolicyScanner.readPolicy(is);
		} catch (IOException e) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyScanner", "Failed to read policy");
		}
		this.callback = callback;
	}
	
	public XACMLPolicyScanner(InputStream filename, Callback callback) {
		try (InputStream is = filename) {
			this.policyObject = XACMLPolicyScanner.readPolicy(is);
		} catch (IOException e) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyScanner", "Failed to read policy");
		}
		this.callback = callback;
	}
	
	public XACMLPolicyScanner(PolicySetType policySet, Callback callback) {
		this.policyObject = policySet;
		this.callback = callback;
	}
	
	public XACMLPolicyScanner(PolicySetType policySet) {
		this(policySet, null);
	}
	
	public XACMLPolicyScanner(PolicyType policy, Callback callback) {
		this.policyObject = policy;
		this.callback = callback;
	}
	
	public XACMLPolicyScanner(PolicyType policy) {
		this(policy, null);
	}
	
	/**
	 * Sets the callback interface to be used.
	 * 
	 * @param cb
	 */
	public void setCallback(Callback cb) {
		this.callback = cb;
	}
	
	/**
	 * Saves the given callback object then calls the scan() method.
	 * 
	 * @param cb
	 * @return
	 */
	public Object scan(Callback cb) {
		this.callback = cb;
		return this.scan();
	}
	
	/**
	 * 
	 * This begins the scanning of the contained object.
	 * 
	 * @return - The PolicySet/Policy that was scanned.
	 */
	public Object scan() {
		if (this.policyObject == null) {
			return null;
		}
		if (this.callback != null && this.callback.onBeginScan(this.policyObject) == CallbackResult.STOP) {
			return this.policyObject;
		}
		if (this.policyObject instanceof PolicyType) {
			this.scanPolicy(null, (PolicyType) this.policyObject);
		} else if (this.policyObject instanceof PolicySetType) {
			this.scanPolicySet(null, (PolicySetType) this.policyObject);
		} else {
			PolicyLogger.error(MessageCodes.ERROR_PROCESS_FLOW + "Unknown class type: " + this.policyObject.getClass().getCanonicalName());
		}
		if (this.callback != null) {
			this.callback.onFinishScan(this.policyObject);
		}
		return this.policyObject;
	}
	
	/**
	 * This performs the scan of a PolicySet
	 * 
	 * @param parent - Its parent PolicySet. Can be null if this is the root.
	 * @param policySet - The PolicySet object.
	 * @return CallbackResult - CONTINUE to continue, STOP to terminate scanning.
	 */
	/**
	 * @param parent
	 * @param policySet
	 * @return
	 */
	protected CallbackResult scanPolicySet(PolicySetType parent, PolicySetType policySet) {
		if (logger.isTraceEnabled()) {
			logger.trace("scanning policy set: " + policySet.getPolicySetId() + " " + policySet.getDescription());
		}
		if (this.callback != null && this.callback.onPreVisitPolicySet(parent, policySet) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		//
		// Scan its info
		//
		if (this.scanTarget(policySet, policySet.getTarget()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		if (this.scanObligations(policySet, policySet.getObligationExpressions()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		if (this.scanAdvice(policySet, policySet.getAdviceExpressions()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		//
		// Iterate the policy sets and/or policies
		//
		List<JAXBElement<?>> list = policySet.getPolicySetOrPolicyOrPolicySetIdReference();
		for (JAXBElement<?> element: list) {
			if ("PolicySet".equals(element.getName().getLocalPart()) && 
				this.scanPolicySet(policySet, (PolicySetType)element.getValue()) == CallbackResult.STOP) {
				return CallbackResult.STOP;
			} else if ("Policy".equals(element.getName().getLocalPart()) &&
					   this.scanPolicy(policySet, (PolicyType)element.getValue()) == CallbackResult.STOP) {
				return CallbackResult.STOP;
			} else {
				logger.warn("generating policy sets found unsupported element: " + element.getName().getNamespaceURI());
			}
		}
		if (this.callback != null && this.callback.onPostVisitPolicySet(parent, policySet) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		return CallbackResult.CONTINUE;
	}
	
	/**
	 * 
	 * This performs scanning of the Policy object.
	 * 
	 * @param parent - The parent PolicySet of the policy. This can be null if this is a root Policy.
	 * @param policy - The policy being scanned.
	 * @return CallbackResult - CONTINUE to continue, STOP to terminate scanning.
	 */
	protected CallbackResult scanPolicy(PolicySetType parent, PolicyType policy) {
		if (logger.isTraceEnabled()) {
			logger.trace("scanning policy: " + policy.getPolicyId() + " " + policy.getDescription());
		}
		if (this.callback != null && this.callback.onPreVisitPolicy(parent, policy) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		//
		// Scan its info
		//
		if (this.scanTarget(policy, policy.getTarget()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		if (this.scanVariables(policy, policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		if (this.scanObligations(policy, policy.getObligationExpressions()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		if (this.scanAdvice(policy, policy.getAdviceExpressions()) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		//
		// Iterate the rules
		//
		List<Object> list = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
		for (Object o: list) {
			if (o instanceof RuleType) {
				RuleType rule = (RuleType) o;
				if (logger.isTraceEnabled()) {
					logger.trace("scanning rule: " + rule.getRuleId() + " " + rule.getDescription());
				}
				if (this.callback != null && this.callback.onPreVisitRule(policy, rule) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
				if (this.scanTarget(rule, rule.getTarget()) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
				if (this.scanConditions(rule, rule.getCondition()) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
				if (this.scanObligations(rule, rule.getObligationExpressions()) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
				if (this.scanAdvice(rule, rule.getAdviceExpressions()) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
				if (this.callback != null && this.callback.onPostVisitRule(policy, rule) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
			} else if (o instanceof VariableDefinitionType) {
				if (this.callback != null && this.callback.onVariable(policy, (VariableDefinitionType) o) == CallbackResult.STOP) {
					return CallbackResult.STOP;
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("scanning policy rules found unsupported object:" + o.toString());
				}
			}
		}
		if (this.callback != null && this.callback.onPostVisitPolicy(parent, policy) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		return CallbackResult.CONTINUE;
	}
	
	/**
	 * Scans the given target for attributes. Its sole purpose is to return attributes found.
	 * 
	 * @param parent - The parent PolicySet/Policy/Rule for the target.
	 * @param target - The target.
	 * @return CallbackResult - CONTINUE to continue, STOP to terminate scanning.
	 */
	protected CallbackResult scanTarget(Object parent, TargetType target) {
		if (target == null) {
			return CallbackResult.CONTINUE;
		}
		List<AnyOfType> anyOfList = target.getAnyOf();
		if (anyOfList != null) {
			Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
			while (iterAnyOf.hasNext()) {
				AnyOfType anyOf = iterAnyOf.next();
				List<AllOfType> allOfList = anyOf.getAllOf();
				if (allOfList != null) {
					Iterator<AllOfType> iterAllOf = allOfList.iterator();
					while (iterAllOf.hasNext()) {
						AllOfType allOf = iterAllOf.next();
						List<MatchType> matchList = allOf.getMatch();
						if (matchList != null) {
							Iterator<MatchType> iterMatch = matchList.iterator();
							while (iterMatch.hasNext()) {
								MatchType match = iterMatch.next();
								//
								// Finally down to the actual attribute
								//
								StdAttribute attribute = null;
								AttributeValueType value = match.getAttributeValue();
								if (match.getAttributeDesignator() != null && value != null) {
									AttributeDesignatorType designator = match.getAttributeDesignator();
									//
									// The content may be tricky
									//
									attribute = new StdAttribute(new IdentifierImpl(designator.getCategory()),
																			new IdentifierImpl(designator.getAttributeId()),
																			new StdAttributeValue<List<?>>(new IdentifierImpl(value.getDataType()), value.getContent()),
																			designator.getIssuer(),
																			false);
								} else if (match.getAttributeSelector() != null && value != null) {
									AttributeSelectorType selector = match.getAttributeSelector();
									attribute = new StdAttribute(new IdentifierImpl(selector.getCategory()),
																			new IdentifierImpl(selector.getContextSelectorId()),
																			new StdAttributeValue<List<?>>(new IdentifierImpl(value.getDataType()), value.getContent()),
																			null,
																			false);
								} else {
									logger.warn("NULL designator/selector or value for match.");
								}
								if (attribute != null && this.callback != null && this.callback.onAttribute(parent, target, attribute) == CallbackResult.STOP) {
									return CallbackResult.STOP;
								}
							}
						}
					}
				}
			}
		}
		return CallbackResult.CONTINUE;
	}
	
	/**
	 * Scan the list of obligations.
	 * 
	 * @param parent - The parent PolicySet/Policy/Rule for the obligation.
	 * @param obligationExpressionsType - All the obligation expressions.
	 * @return CallbackResult - CONTINUE to continue, STOP to terminate scanning.
	 */
	protected CallbackResult scanObligations(Object parent, ObligationExpressionsType obligationExpressionsType) {
		if (obligationExpressionsType == null) {
			return CallbackResult.CONTINUE;
		}
		List<ObligationExpressionType> expressions = obligationExpressionsType.getObligationExpression();
		if (expressions == null || expressions.isEmpty()) {
			return CallbackResult.CONTINUE;
		}
		for (ObligationExpressionType expression : expressions) {
			StdMutableObligation ob = new StdMutableObligation(new IdentifierImpl(expression.getObligationId()));
			List<AttributeAssignmentExpressionType> assignments = expression.getAttributeAssignmentExpression();
			if (assignments != null) {
				for (AttributeAssignmentExpressionType assignment : assignments) {
					// category is optional and may be null
					IdentifierImpl categoryId = null;
					if (assignment.getCategory() != null) {
						categoryId = new IdentifierImpl(assignment.getCategory());
					}
					AttributeAssignment attribute = new StdAttributeAssignment(
												categoryId,
												new IdentifierImpl(assignment.getAttributeId()),
												assignment.getIssuer(),
												new StdAttributeValue<Object>(null, null)
												);
					ob.addAttributeAssignment(attribute);
				}
			}
			if (this.callback != null && this.callback.onObligation(parent, expression, ob) == CallbackResult.STOP) {
				return CallbackResult.STOP;
			}
		}
		return CallbackResult.CONTINUE;
	}

	/**
	 * 
	 * Scans the list of advice expressions returning each individually.
	 * 
	 * @param parent - The parent PolicySet/Policy/Rule for the advice.
	 * @param adviceExpressionstype - The list of advice expressions.
	 * @return CallbackResult - CONTINUE to continue, STOP to terminate scanning.
	 */
	protected CallbackResult scanAdvice(Object parent, AdviceExpressionsType adviceExpressionstype) {
		if (adviceExpressionstype == null) {
			return CallbackResult.CONTINUE;
		}
		List<AdviceExpressionType> expressions = adviceExpressionstype.getAdviceExpression();
		if (expressions == null || expressions.isEmpty()) {
			return CallbackResult.CONTINUE;
		}
		for (AdviceExpressionType expression : expressions) {
			StdMutableAdvice ob = new StdMutableAdvice(new IdentifierImpl(expression.getAdviceId()));
			List<AttributeAssignmentExpressionType> assignments = expression.getAttributeAssignmentExpression();
			if (assignments != null) {
				for (AttributeAssignmentExpressionType assignment : assignments) {
					IdentifierImpl categoryId = null;
					if (assignment.getCategory() != null) {
						categoryId = new IdentifierImpl(assignment.getCategory());
					}
					AttributeAssignment attribute = new StdAttributeAssignment(
												categoryId,
												new IdentifierImpl(assignment.getAttributeId()),
												assignment.getIssuer(),
												new StdAttributeValue<Object>(null, null)
												);
					ob.addAttributeAssignment(attribute);
				}
			}
			if (this.callback != null && this.callback.onAdvice(parent, expression, ob) == CallbackResult.STOP) {
				return CallbackResult.STOP;
			}
		}
		return CallbackResult.CONTINUE;
	}
	
	/**
	 * Scans the list of variable definitions.
	 * 
	 * @param policy - Policy object containing the variable definition.
	 * @param list - List of variable definitions.
	 * @return CallbackResult - CONTINUE to continue, STOP to terminate scanning.
	 */
	protected CallbackResult scanVariables(PolicyType policy, List<Object> list) {
		if (list == null) {
			return CallbackResult.CONTINUE;
		}
		for (Object o : list) {
			if (o instanceof VariableDefinitionType && this.callback != null && this.callback.onVariable(policy, (VariableDefinitionType) o) == CallbackResult.STOP) {
				return CallbackResult.STOP;
			}
		}
		
		return CallbackResult.CONTINUE;
	}
	
	/**
	 * Scans the list of conditions.
	 * 
	 * @param rule
	 * @param condition
	 * @return
	 */
	protected CallbackResult scanConditions(RuleType rule, ConditionType condition) {
		if (condition != null && this.callback != null && this.callback.onCondition(rule, condition) == CallbackResult.STOP) {
			return CallbackResult.STOP;
		}
		return CallbackResult.CONTINUE;
	}
	
	/**
	 * Reads the XACML XML policy file in and returns the version contained in the root Policy/PolicySet element.
	 * 
	 * @param policy - The policy file.
	 * @return - The version string from the file (uninterpreted)
	 * @throws IOException 
	 */
	public static String	getVersion(Path policy) throws IOException {
		Object data = null;
		try (InputStream is = Files.newInputStream(policy)) {
			data = XACMLPolicyScanner.readPolicy(is);
		} catch (IOException e) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyScanner", "Failed to read policy");
			throw e;
		}
		if (data == null) {
			logger.warn("Version is null.");
			return null;
		}
		return getVersion(data);
	}
		
	/**
	 * Reads the Policy/PolicySet element object and returns its current version.
	 * 
	 * @param data - Either a PolicySet or Policy XACML type object.
	 * @return - The integer version value. -1 if it doesn't exist or was un-parsable.
	 */
	public static String	getVersion(Object data) {
		String version = null;
		try {
			if (data instanceof PolicySetType) {
				version = ((PolicySetType)data).getVersion();
			} else if (data instanceof PolicyType) {
				version = ((PolicyType)data).getVersion();
			} else {
				if (data != null) {
					PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Expecting a PolicySet/Policy/Rule object. Got: " + data.getClass().getCanonicalName());
				}
				return null;
			}
			if (version != null && version.length() > 0) {
				return version;
			} else {
				logger.warn("No version set in policy");
			}
		} catch (NumberFormatException e) {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE, e, "XACMLPolicyScanner", "Invalid version contained in policy: " + version);
			return null;
		}
		return null;
	}
	
	/**
	 * Returns the Policy or PolicySet ID.
	 * 
	 * @param data - A XACML 3.0 Policy or PolicySet element object.
	 * @return The policy/policyset's policy ID
	 */
	public static String getID(Object data) {
		if (data instanceof PolicySetType) {
			return ((PolicySetType)data).getPolicySetId();
		} else if (data instanceof PolicyType) {
			return ((PolicyType)data).getPolicyId();
		} else {
			PolicyLogger.error(MessageCodes.ERROR_DATA_ISSUE + "Expecting a PolicySet/Policy/Rule object. Got: " + data.getClass().getCanonicalName());
			return null;
		}
	}
	
	public static List<String> getCreatedByModifiedBy(Path policyPath) throws IOException{
		String createdBy = "";
		String modifiedBy= "";
		String cValue = "@CreatedBy:";
		String mValue = "@ModifiedBy:";
		for(String line: Files.readAllLines(policyPath)){
			line = line.replaceAll("\\s+", "");
			if(line.isEmpty()){
				continue;
			}
			if(line.contains("<Description>") && line.contains(cValue) && line.contains(mValue)){
				createdBy = line.substring(line.indexOf(cValue) + cValue.length(), line.lastIndexOf(cValue));
				modifiedBy = line.substring(line.indexOf(mValue) + mValue.length(), line.lastIndexOf(mValue));
				break;
			}
		}
		return Arrays.asList(createdBy, modifiedBy);
	}
	
	//get the Created Name of the User on reading the Xml file
	public static String getCreatedBy(Path policyPath) throws IOException{
		String userId = "";
		String value = "@CreatedBy:";
		for(String line: Files.readAllLines(policyPath)){
			line = line.replaceAll("\\s+", "");
			if(line.isEmpty()){
				continue;
			}
			if(line.contains("<Description>") && line.contains(value)){
				userId = line.substring(line.indexOf(value) + value.length(), line.lastIndexOf(value));
				break;
			}
		}
		return userId;
	}
	
	//get the Modified Name of the User on reading the Xml file
	public static String getModifiedBy(Path policyPath) throws IOException{
		String modifiedBy = "";
		String value = "@ModifiedBy:";
		for(String line: Files.readAllLines(policyPath)){
			line = line.replaceAll("\\s+", "");
			if(line.isEmpty()){
				continue;
			}
			if(line.contains("<Description>") && line.contains(value)){
				modifiedBy = line.substring(line.indexOf(value) + value.length(), line.lastIndexOf(value));
				break;
			}
		}
		return modifiedBy;
	}

	/**
	 * readPolicy - does the work to read in policy data from a file.
	 * 
	 * @param policy - The path to the policy file.
	 * @return - The policy data object. This *should* be either a PolicySet or a Policy.
	 */
	public static Object readPolicy(InputStream is) {
		try {
			//
			// Create a DOM parser
			//
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    dbf.setNamespaceAware(true);
		    DocumentBuilder db = dbf.newDocumentBuilder();
		    //
		    // Parse the policy file
		    //
		    Document doc = db.parse(is);
		    Element e = doc.getDocumentElement();
			//
			// Is it a 3.0 policy?
			//
			if ("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17".equals(e.getNamespaceURI())) {
				//
				// A policyset or policy could be the root
				//
				if (e.getNodeName().endsWith("Policy")) {
					//
					// Now we can create the context for the policy set
					// and unmarshall the policy into a class.
					//
					JAXBContext context = JAXBContext.newInstance(PolicyType.class);
					Unmarshaller um = context.createUnmarshaller();
					JAXBElement<PolicyType> root = um.unmarshal(e, PolicyType.class);
					//
					// Here is our policy set class
					//
					return root.getValue();
				} else if (e.getNodeName().endsWith("PolicySet")) {
					//
					// Now we can create the context for the policy set
					// and unmarshall the policy into a class.
					//
					JAXBContext context = JAXBContext.newInstance(PolicySetType.class);
					Unmarshaller um = context.createUnmarshaller();
					JAXBElement<PolicySetType> root = um.unmarshal(e, PolicySetType.class);
					//
					// Here is our policy set class
					//
					return root.getValue();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Not supported yet: " + e.getNodeName());
					}
				}
			} else {
				logger.warn("unsupported namespace: " + e.getNamespaceURI());
			}
		} catch (Exception e) {
			PolicyLogger.error(MessageCodes.ERROR_SCHEMA_INVALID, e, "XACMLPolicyScanner", "Exception in readPolicy");
		}
		return null;
	}

	/**
	 * @return the policyObject
	 */
	public Object getPolicyObject() {
		return policyObject;
	}
}
