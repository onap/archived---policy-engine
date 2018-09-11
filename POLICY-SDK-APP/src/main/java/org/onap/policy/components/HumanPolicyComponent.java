/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

package org.onap.policy.components;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttribute;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.util.XACMLPolicyScanner.CallbackResult;
import com.att.research.xacml.util.XACMLPolicyScanner.SimpleCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.controller.PolicyController;
import org.onap.policy.rest.jpa.FunctionDefinition;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.util.XACMLPolicyScanner;



public class HumanPolicyComponent {

    private static final Logger LOGGER = FlexLogger.getLogger(HumanPolicyComponent.class);

    // Constants Used in XML Creation
    public static final String CATEGORY_RECIPIENT_SUBJECT =
            "urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject";
    public static final String CATEGORY_RESOURCE = "urn:oasis:names:tc:xacml:3.0:attribute-category:resource";
    public static final String CATEGORY_ACTION = "urn:oasis:names:tc:xacml:3.0:attribute-category:action";
    public static final String CATEGORY_ACCESS_SUBJECT = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
    public static final String ACTION_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";
    public static final String SUBJECT_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
    public static final String RESOURCE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
    public static final String FUNTION_INTEGER_ONE_AND_ONLY =
            "urn:oasis:names:tc:xacml:1.0:function:integer-one-and-only";
    public static final String FUNCTION_STRING_ONE_AND_ONLY =
            "urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";
    public static final String FUNCTION_STRING_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-equal";
    public static final String FUNCTION_STRING_REGEX_MATCH = "org.onap.function.regex-match";
    public static final String FUNCTION_STRING_EQUAL_IGNORE =
            "urn:oasis:names:tc:xacml:3.0:function:string-equal-ignore-case";
    public static final String INTEGER_DATATYPE = "http://www.w3.org/2001/XMLSchema#integer";
    public static final String BOOLEAN_DATATYPE = "http://www.w3.org/2001/XMLSchema#boolean";
    public static final String STRING_DATATYPE = "http://www.w3.org/2001/XMLSchema#string";
    public static final String URI_DATATYPE = "http://www.w3.org/2001/XMLSchema#anyURI";
    public static final String RULE_VARIABLE = "var:";
    public static final String EMPTY_STRING = "";
    private static final String ENTER = "ENTER";


    private static HtmlProcessor htmlProcessor;

    private static File policyFile;

    private HumanPolicyComponent() {
        // Default Constructor
    }

    public static JSONObject DescribePolicy(final File policyFile) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        HumanPolicyComponent.policyFile = policyFile;
        return humanPolicyLayout();

    }

    private static JSONObject humanPolicyLayout() {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        try {
            String html = processPolicy();
            JSONObject result = new JSONObject();
            result.put("html", html);
            return result;

        } catch (IllegalArgumentException e) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "cannot build html area por policy", e);
        }
        return null;
    }

    private static String processPolicy() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(ENTER);
        }
        try (FileInputStream pIS = new FileInputStream(policyFile)) {
            Object policy = XACMLPolicyScanner.readPolicy(pIS);
            if (policy == null)
                throw new IllegalArgumentException("Policy File " + policyFile.getName() + " cannot be unmarshalled");

            HumanPolicyComponent.htmlProcessor = new HtmlProcessor(HumanPolicyComponent.policyFile, policy);

            Path policyPath = Paths.get(policyFile.getAbsolutePath());
            XACMLPolicyScanner xacmlScanner = new XACMLPolicyScanner(policyPath, htmlProcessor);
            xacmlScanner.scan();
            String html = htmlProcessor.html();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug(policyPath + System.lineSeparator() + html);

            return html;

        } catch (Exception e) {
            String msg = "Exception reading policy: " + policyFile.getAbsolutePath() + ": " + e.getMessage();
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + msg, e);
            throw new IllegalArgumentException(msg);
        }
    }

}


class HtmlProcessor extends SimpleCallback {

    private static final Logger LOGGER = FlexLogger.getLogger(HtmlProcessor.class);

    private static final String ENTER = "ENTER";
    private static Map<String, String> function2human;
    static {
        function2human = new HashMap<>();
        function2human.put(HumanPolicyComponent.FUNCTION_STRING_EQUAL, "equal");
        function2human.put(HumanPolicyComponent.FUNCTION_STRING_EQUAL_IGNORE, "equal");
        function2human.put(HumanPolicyComponent.FUNCTION_STRING_ONE_AND_ONLY, "one-and-only");
        function2human.put(HumanPolicyComponent.FUNCTION_STRING_REGEX_MATCH, "matching regular expression");
        function2human.put(HumanPolicyComponent.FUNTION_INTEGER_ONE_AND_ONLY, "one-and-only");
    }

    private static Map<String, String> combiningAlgo2human;
    static {
        combiningAlgo2human = new HashMap<>();
        combiningAlgo2human.put("deny-overrides", "to deny if any $placeholder$ below evaluates to <i>deny</i>");
        combiningAlgo2human.put("permit-overrides", "to permit if any $placeholder$ below evaluates to <i>permit</i>");

        combiningAlgo2human.put("ordered-deny-overrides",
                "to deny if any $placeholder$ below evaluates to <i>deny</i>");
        combiningAlgo2human.put("ordered-permit-overrides",
                "to permit if any $placeholder$ below evaluates to <i>permit</i>");
        combiningAlgo2human.put("deny-unless-permit",
                "to permit if any $placeholder$ below evaluates to <i>deny</i> and not <i>indeterminate</i>");

        combiningAlgo2human.put("permit-unless-deny",
                "to deny if any $placeholder$ below evaluates to is <i>permit</i> and not <i>indeterminate</i>");
        combiningAlgo2human.put("first-applicable",
                "to honour the result of the first successfully evaluated $placeholder$ in order");
        combiningAlgo2human.put("only-one-applicable",
                "to honour the result of the first successfully evaluated $placeholder$ in order");
    }

    private Map<String, AttributeIdentifiers> attributeIdentifiersMap = new HashMap<>();

    private final StringWriter stringWriter = new StringWriter();
    private final PrintWriter htmlOut = new PrintWriter(stringWriter);
    private final String policyName;
    private final Object rootPolicyObject;

    public HtmlProcessor(File policyFile, Object policyObject) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        if (policyFile == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + "Null Policy File");
            throw new IllegalArgumentException("Null Policy File");
        }

        if (!policyFile.exists() || !policyFile.canRead()) {
            String msg = "Can't access " + policyFile.getAbsolutePath();
            LOGGER.error(XACMLErrorConstants.ERROR_PERMISSIONS + msg);
            throw new IllegalArgumentException(msg);
        }

        if (policyObject == null
                || (!(policyObject instanceof PolicySetType) && !(policyObject instanceof PolicyType))) {
            String msg = "Invalid unmarshalled object: " + policyObject;
            LOGGER.error(XACMLErrorConstants.ERROR_SCHEMA_INVALID + msg);
            throw new IllegalArgumentException(msg);
        }

        this.policyName = FilenameUtils.removeExtension(policyFile.getName());
        this.rootPolicyObject = policyObject;

        String version = "-";
        if (policyObject instanceof PolicyType) {
            PolicyType policy = (PolicyType) policyObject;
            version = policy.getVersion();
            htmlOut.println("<h1>Policy:   " + policyName + "  (version " + version + ") </h1>");

        } else {
            PolicySetType policySet = (PolicySetType) policyObject;
            version = policySet.getVersion();
            htmlOut.println("<h1>Policy Set:   " + policyName + "  (v" + version + ") </h1>");
        }

        htmlOut.println("<h3><b>Location: </b>" + policyFile.getPath() + "</h3>");
        htmlOut.println("<hr>");

        if (rootPolicyObject instanceof PolicySetType) {
            if (policyName.startsWith("Config_")) {
                htmlOut.println("<p>This is a <b>config</b> policy set.</p>");
            } else if (policyName.startsWith("Action_")) {
                htmlOut.println("<p>This is an <b>action</b> policy set.</p>");
            }
            htmlOut.println("<dl>");
        } else {
            if (policyName.startsWith("Config_")) {
                htmlOut.println("<p>This is a <b>config</b> policy.</p>");
            } else if (policyName.startsWith("Action_")) {
                htmlOut.println("<p>This is an <b>action</b> policy.</p>");
            }
            htmlOut.println("<ol>");
        }
    }

    /**
     * @return the attributeIdentifiersMap
     */
    public Map<String, AttributeIdentifiers> getAttributeIdentifiersMap() {
        return attributeIdentifiersMap;
    }

    @Override
    public void onFinishScan(Object root) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        if (rootPolicyObject instanceof PolicySetType) {
            htmlOut.println("</dl>");
        } else {
            htmlOut.println("</ol>");
        }

        htmlOut.println("<hr>");

        htmlOut.println("<h3>Attribute Table:</h3>");

        htmlOut.println("<table border=\"3\" style=\"width:100%\">");
        htmlOut.println("<tr>");
        htmlOut.print("<th>Category</th>");
        htmlOut.print("<th>Type</th>");
        htmlOut.print("<th>Identifier</th>");
        htmlOut.println("</tr>");
        for (Map.Entry<String, AttributeIdentifiers> entry : this.attributeIdentifiersMap.entrySet()) {
            AttributeIdentifiers value = entry.getValue();
            htmlOut.println("<tr>");
            htmlOut.print("<td><a name=\"" + entry.getKey() + "\"></a>" + value.category + "</td>");
            htmlOut.print("<td>" + value.getType() + "</td>");
            htmlOut.print("<td>" + value.id + "</td>");
            htmlOut.println("</tr>");
        }
        htmlOut.println("</table>");

        htmlOut.println("<p></p>");

        // Not necessary for the user, uncomment if desired at some point
        // writeRawXACML()

        super.onFinishScan(root);
    }

    @Override
    public CallbackResult onPreVisitPolicySet(PolicySetType parent, PolicySetType policySet) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId() + " Version: " + policySet.getVersion());

        if (parent != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId() + "Parent PolicySet: " + parent.getPolicySetId()
                    + " Version: " + parent.getVersion());

        String description = policySet.getDescription();
        if (description != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId() + " Description: " + policySet.getDescription());

        if (parent == null) // root
            policySet(policySet, "dl");
        else
            policySet(policySet, "li");

        if (!policySet.getPolicySetOrPolicyOrPolicySetIdReference().isEmpty())
            htmlOut.println("<ol>");

        return super.onPreVisitPolicySet(parent, policySet);
    }

    @Override
    public CallbackResult onPostVisitPolicySet(PolicySetType parent, PolicySetType policySet) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId() + " Version: " + policySet.getVersion());

        if (parent != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId() + "Parent PolicySet: " + parent.getPolicySetId()
                    + " Version: " + parent.getVersion());

        String description = policySet.getDescription();
        if (description != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId() + " Description: " + policySet.getDescription());

        if (!policySet.getPolicySetOrPolicyOrPolicySetIdReference().isEmpty())
            htmlOut.println("</ol>");

        htmlOut.println("<p></p>");

        return super.onPostVisitPolicySet(parent, policySet);
    }

    public void policySet(PolicySetType policySet, String htmlListElement) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policySet.getPolicySetId());

        String combiningAlgorithm = "-";
        String id = "-";
        String version = "-";


        if (policySet.getPolicyCombiningAlgId() != null)
            combiningAlgorithm = extractLastIdentifier(policySet.getPolicyCombiningAlgId(), ":");

        if (policySet.getPolicySetId() != null)
            id = extractLastIdentifier(policySet.getPolicySetId(), ":");

        if (policySet.getVersion() != null)
            version = policySet.getVersion();


        htmlOut.println("<" + htmlListElement + "><b>Policy Set ID</b>: <i>" + id + "</i>  (v" + version + ") " + "</"
                + htmlListElement + ">");

        if (policySet.getTarget() == null || policySet.getTarget().getAnyOf() == null
                || policySet.getTarget().getAnyOf().isEmpty()) {
            htmlOut.println("<p>This policy set applies to all requests.</p>");
        } else {
            htmlOut.print("<p>");
            htmlOut.print("This policy set applies to requests with attributes ");

            List<AnyOfType> anyOf_s = policySet.getTarget().getAnyOf();
            target(anyOf_s);
            htmlOut.println(".</p>");
        }

        if (policySet.getPolicySetOrPolicyOrPolicySetIdReference() != null
                && !policySet.getPolicySetOrPolicyOrPolicySetIdReference().isEmpty()) {
            String algoDesc = combiningAlgo2human.get(combiningAlgorithm);
            if (algoDesc != null) {
                algoDesc = algoDesc.replace("$placeholder$", "policy") + " (" + "<i>" + combiningAlgorithm + "</i>)";
            } else {
                algoDesc = combiningAlgorithm;
            }

            htmlOut.println("<p>The result is " + algoDesc + ": </p>");
        }
    }

    @Override
    public CallbackResult onPreVisitPolicy(PolicySetType parent, PolicyType policy) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policy.getPolicyId() + " Version: " + policy.getVersion());

        if (parent != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policy.getPolicyId() + "Parent PolicySet: " + parent.getPolicySetId()
                    + " Version: " + parent.getVersion());

        String description = policy.getDescription();
        if (description != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policy.getPolicyId() + " Description: " + policy.getDescription());

        policy(policy);

        if (!policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().isEmpty())
            htmlOut.println("<ol type=\"i\">");

        return super.onPreVisitPolicy(parent, policy);
    }

    @Override
    public CallbackResult onPostVisitPolicy(PolicySetType parent, PolicyType policy) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policy.getPolicyId() + " Version: " + policy.getVersion());

        if (parent != null && LOGGER.isTraceEnabled())
            LOGGER.trace("PolicySet: " + policy.getPolicyId() + "Parent PolicySet: " + parent.getPolicySetId()
                    + " Version: " + parent.getVersion());

        if (!policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().isEmpty())
            htmlOut.println("</ol>");

        htmlOut.println("<p></p>");
        return super.onPostVisitPolicy(parent, policy);
    }

    public void policy(PolicyType policy) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Policy: " + policy.getPolicyId());

        String combiningAlgorithm = "-";
        String id = "-";
        String version = "-";


        if (policy.getRuleCombiningAlgId() != null)
            combiningAlgorithm = extractLastIdentifier(policy.getRuleCombiningAlgId(), ":");

        if (policy.getPolicyId() != null)
            id = extractLastIdentifier(policy.getPolicyId(), ":");

        if (policy.getVersion() != null)
            version = policy.getVersion();

        htmlOut.println("<li><b>Policy ID</b>: <i>" + id + "</i>  (v" + version + ") " + "</li>");

        if (policy.getTarget() == null || policy.getTarget().getAnyOf() == null
                || policy.getTarget().getAnyOf().isEmpty()) {
            htmlOut.println("<p>This policy applies to all requests.</p>");
        } else {
            htmlOut.print("<p>");
            htmlOut.print("This policy applies to requests with attributes ");

            List<AnyOfType> anyOf_s = policy.getTarget().getAnyOf();
            target(anyOf_s);
            htmlOut.println(".</p>");
        }

        if (policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition() != null
                && !policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().isEmpty()) {
            String algoDesc = combiningAlgo2human.get(combiningAlgorithm);
            if (algoDesc != null) {
                algoDesc = algoDesc.replace("$placeholder$", "rule") + " (<i>" + combiningAlgorithm + "</i>)";
            } else {
                algoDesc = combiningAlgorithm;
            }
            htmlOut.println("<p>The result is " + algoDesc + ": </p>");
        }
    }


    @Override
    public CallbackResult onPreVisitRule(PolicyType parent, RuleType rule) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Rule: " + rule.getRuleId());

        if (parent != null && LOGGER.isTraceEnabled())
            LOGGER.trace("Parent Policy: " + parent.getPolicyId() + " Version: " + parent.getVersion());

        String description = rule.getDescription();
        if (description != null && LOGGER.isTraceEnabled()) {
            LOGGER.trace("Rule: " + rule.getRuleId() + " Description: " + rule.getDescription());
        }

        rule(rule);

        return super.onPreVisitRule(parent, rule);
    }

    @Override
    public CallbackResult onPostVisitRule(PolicyType parent, RuleType rule) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Rule: " + rule.getRuleId());

        if (parent != null && LOGGER.isTraceEnabled())
            LOGGER.trace("Parent Policy: " + parent.getPolicyId() + " Version: " + parent.getVersion());

        return super.onPostVisitRule(parent, rule);
    }

    public void rule(RuleType rule) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace("Rule: " + rule.getRuleId());

        String id = "-";

        if (rule.getRuleId() != null)
            id = extractLastIdentifier(rule.getRuleId(), ":");

        htmlOut.println("<li><b>Rule ID</b>: <i>" + id + "</i></li>");

        htmlOut.println("<dl>");

        htmlOut.print("<p>");
        htmlOut.print(rule.getEffect().value());

        if (rule.getTarget() == null || rule.getTarget().getAnyOf() == null || rule.getTarget().getAnyOf().isEmpty()) {
            htmlOut.print(" for all requests");
        } else {
            List<AnyOfType> anyOf_s = rule.getTarget().getAnyOf();
            htmlOut.print(" for requests with attributes ");
            target(anyOf_s);
        }

        if (rule.getCondition() != null) {
            htmlOut.print(" when ");
            htmlOut.println(this.stringifyCondition(rule.getCondition()) + " ");
        } else {
            htmlOut.print(" with no conditions ");
        }

        if (rule.getAdviceExpressions() != null) {
            advice(rule.getAdviceExpressions());
            if (rule.getObligationExpressions() != null)
                htmlOut.println(" and ");
        }

        if (rule.getObligationExpressions() != null) {
            obligation(rule.getObligationExpressions());
        }

        htmlOut.println("</p>");
    }

    private void advice(AdviceExpressionsType adviceExpressions) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        List<AdviceExpressionType> ae = adviceExpressions.getAdviceExpression();
        for (AdviceExpressionType expression : ae) {
            htmlOut.println(" with <b>advice</b> (<i>" + expression.getAdviceId() + "</i>) on <i>"
                    + expression.getAppliesTo().value() + "</i>:");
            htmlOut.println("<ol type=\"a\">");
            List<AttributeAssignmentExpressionType> assignments = expression.getAttributeAssignmentExpression();
            if (assignments != null) {
                processAttributeAssignments(assignments);
            }
            htmlOut.println("</ol>");
        }
    }

    private void obligation(ObligationExpressionsType obligationExpressions) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        List<ObligationExpressionType> oe = obligationExpressions.getObligationExpression();
        for (ObligationExpressionType expression : oe) {
            htmlOut.println(" with <b>obligations</b> (<i>" + expression.getObligationId()
                    + "</i>) to be fullfilled on <i>" + expression.getFulfillOn().value() + "</i>:");
            htmlOut.println("<ol type=\"a\">");
            List<AttributeAssignmentExpressionType> assignments = expression.getAttributeAssignmentExpression();
            if (assignments != null) {
                processAttributeAssignments(assignments);
            }
            htmlOut.println("</ol>");
        }
    }

    /**
     * @param assignments
     */
    private void processAttributeAssignments(List<AttributeAssignmentExpressionType> assignments) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        for (AttributeAssignmentExpressionType assignment : assignments) {
            String succintIdentifier = extractLastIdentifier(assignment.getCategory(), ":") + ":"
                    + extractLastIdentifier(assignment.getAttributeId(), ":");
            AttributeIdentifiers attributeIdentifiers = null;
            if (!this.attributeIdentifiersMap.containsKey(succintIdentifier)) {
                // Note Attribute Assignments do not have an Attribute Type, assume string
                // but note this case is unlikely since attributeMap should have been populated
                // during parsing of target and conditions, and not in this case for Advice and
                // Obligations.
                attributeIdentifiers =
                        new AttributeIdentifiers(assignment.getCategory(), "NA", assignment.getAttributeId());
                this.attributeIdentifiersMap.put(succintIdentifier, attributeIdentifiers);
            }

            htmlOut.print("<li><i><a href=\"#" + succintIdentifier + "\">" + succintIdentifier + "</a></i> is ");
            // AttributeValueType
            JAXBElement<?> jaxbExp = assignment.getExpression();
            Object assignmentObject = jaxbExp.getValue();
            if (assignmentObject instanceof AttributeValueType) {
                AttributeValueType avt = (AttributeValueType) assignmentObject;
                if (attributeIdentifiers != null) {
                    attributeIdentifiers.setType(avt.getDataType());
                }
                int numContent = avt.getContent().size();
                int countContent = 0;
                for (Object c : avt.getContent()) {
                    countContent++;
                    htmlOut.print("<i>" + c + "</i>");
                    if (countContent < numContent)
                        htmlOut.print(" or ");
                }
                htmlOut.println("</li>");
            } else if (assignmentObject instanceof AttributeDesignatorType
                    || assignmentObject instanceof AttributeSelectorType || assignmentObject instanceof ApplyType) {
                htmlOut.println("NA");
            } else {
                htmlOut.println("Unexpected");
            }
        }
    }

    /**
     * 
     * @param anyOfList
     */
    public void target(List<AnyOfType> anyOfList) {
        if (LOGGER.isTraceEnabled())
            LOGGER.trace(ENTER);

        if (anyOfList != null) {
            Iterator<AnyOfType> iterAnyOf = anyOfList.iterator();
            StringBuilder targetInHuman = new StringBuilder();
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
                            if (matchList.size() > 1)
                                targetInHuman.append("(");
                            while (iterMatch.hasNext()) {
                                MatchType match = iterMatch.next();
                                //
                                // Finally down to the actual attribute
                                //
                                StdAttribute attribute = null;
                                AttributeValueType value = match.getAttributeValue();
                                String attributeDataType;
                                if (match.getAttributeDesignator() != null && value != null) {
                                    AttributeDesignatorType designator = match.getAttributeDesignator();
                                    attribute = new StdAttribute(new IdentifierImpl(designator.getCategory()),
                                            new IdentifierImpl(designator.getAttributeId()),
                                            new StdAttributeValue<List<?>>(new IdentifierImpl(value.getDataType()),
                                                    value.getContent()),
                                            designator.getIssuer(), false);
                                    attributeDataType = designator.getDataType();
                                } else if (match.getAttributeSelector() != null && value != null) {
                                    AttributeSelectorType selector = match.getAttributeSelector();
                                    attribute = new StdAttribute(new IdentifierImpl(selector.getCategory()),
                                            new IdentifierImpl(selector.getContextSelectorId()),
                                            new StdAttributeValue<List<?>>(new IdentifierImpl(value.getDataType()),
                                                    value.getContent()),
                                            null, false);
                                    attributeDataType = selector.getDataType();
                                } else {
                                    LOGGER.warn("NULL designator/selector or value for match.");
                                    attributeDataType = "NA";
                                }

                                String functionName = getHumanFunction(match.getMatchId());
                                if (attribute != null) {
                                    String succintIdentifier = extractLastIdentifier(
                                            attribute.getCategory().stringValue(), ":") + ":"
                                            + extractLastIdentifier(attribute.getAttributeId().stringValue(), ":");
                                    AttributeIdentifiers ai =
                                            new AttributeIdentifiers(attribute.getCategory().stringValue(),
                                                    attributeDataType, attribute.getAttributeId().stringValue());
                                    this.attributeIdentifiersMap.put(succintIdentifier, ai);

                                    targetInHuman.append("<i><a href=\"#" + succintIdentifier + "\">"
                                            + succintIdentifier + "</a></i> " + functionName + " ");

                                    int numAttributes = attribute.getValues().size();
                                    int count = 0;
                                    for (AttributeValue<?> v : attribute.getValues()) {
                                        count++;
                                        if (v.getValue() instanceof Collection<?>) {
                                            Collection<?> value_s = (Collection<?>) v.getValue();
                                            int numValues = value_s.size();
                                            int countValues = 0;
                                            for (Object o : value_s) {
                                                countValues++;
                                                targetInHuman.append(" <I>" + o + "</I>");
                                                if (countValues < numValues) {
                                                    targetInHuman.append(", or");
                                                }
                                            }
                                        } else {
                                            targetInHuman.append(" <I>" + v.getValue() + "</I>");
                                            if (count < numAttributes) {
                                                targetInHuman.append(", or ");
                                            }
                                        }
                                    }
                                }

                                if (iterMatch.hasNext()) {
                                    targetInHuman.append(" and ");
                                }
                            } // end iterMatch
                            if (matchList.size() > 1) {
                                targetInHuman.append(")");
                            }
                        }
                        if (iterAllOf.hasNext()) {
                            targetInHuman.append(" or ");
                        }
                    } // end iterAllOf
                }
                if (iterAnyOf.hasNext()) {
                    targetInHuman = new StringBuilder();
                    targetInHuman.append("(" + targetInHuman + ")" + " or ");
                } else {
                    if (anyOfList.size() > 1) {
                        targetInHuman.append(")");
                    }
                }
            } // end iterAnyOf
            htmlOut.println(targetInHuman);
        }
    }

    private String getHumanFunction(String matchId) {
        if (HtmlProcessor.function2human.containsKey(matchId)) {
            return HtmlProcessor.function2human.get(matchId);
        }

        FunctionDefinition function = PolicyController.getFunctionIdMap().get(matchId);
        String functionName = function.getShortname();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(functionName + ": #args[" + function.getArgLb() + "," + function.getArgUb() + "]");
        }

        return extractLastIdentifier(removePrimitives(functionName), ":");
    }

    public String html() {
        this.htmlOut.flush();
        return this.stringWriter.toString();
    }

    private String extractLastIdentifier(String in, String separator) {
        int lastIndex = in.lastIndexOf(separator);
        if (lastIndex < 0) {
            return in;
        } else {
            return in.substring(lastIndex + 1);
        }
    }

    private String removePrimitives(String in) {
        String newIn = in;
        newIn = newIn.replace("string-", "");
        newIn = newIn.replace("integer-", "");
        newIn = newIn.replace("double-", "");
        newIn = newIn.replace("boolean-", "");
        return newIn;
    }

    private String stringifyCondition(ConditionType condition) {
        if (condition.getExpression() == null) {
            return "";
        }

        return stringifyExpression(condition.getExpression().getValue());
    }

    private String stringifyExpression(Object expression) {
        if (expression instanceof ApplyType) {
            ApplyType apply = (ApplyType) expression;
            FunctionDefinition function = PolicyController.getFunctionIdMap().get(apply.getFunctionId());
            String functionName = function.getShortname();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(functionName + ": #args[" + function.getArgLb() + "," + function.getArgUb() + "]");
            }

            if (functionName.contains("one-and-only")) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("one-and-only found: " + functionName);
                }

                List<JAXBElement<?>> exps = apply.getExpression();
                if (exps == null || exps.isEmpty()) {
                    return "";
                } else {
                    StringBuilder forResult = new StringBuilder();
                    for (JAXBElement<?> e : exps) {
                        Object v = e.getValue();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("one-and-only children: " + v);
                        }
                        if (v != null) {
                            forResult.append(stringifyExpression(v));
                        }
                    }
                    return forResult.toString();
                }
            }

            final int numExpr = (apply.getExpression() == null) ? -1 : apply.getExpression().size();
            if (numExpr <= 0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(functionName + " 0 expressions: " + numExpr);
                }
                return "";
            } else if (numExpr == 1) {
                // eg: not
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(functionName + " 1 expression: " + numExpr);
                }
                StringBuilder applySubresult = new StringBuilder();
                for (JAXBElement<?> e : apply.getExpression()) {
                    Object v = e.getValue();
                    if (v != null) {
                        applySubresult.append(this.stringifyExpression(e.getValue()));
                    }
                }
                return " " + removePrimitives(functionName) + " (" + applySubresult.toString() + ")";
            } else {
                // > 1 arguments
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(functionName + " > 1 expressions: " + numExpr);
                }
                StringBuilder applySubresult = new StringBuilder();
                int exprCount = 0;
                for (JAXBElement<?> e : apply.getExpression()) {
                    exprCount++;
                    Object ev = e.getValue();
                    if (ev != null) {
                        if (ev instanceof ApplyType) {
                            if (((ApplyType) ev).getFunctionId().contains("one-and-only")) {
                                applySubresult.append(this.stringifyExpression(e.getValue()));
                            } else {
                                applySubresult.append("(" + this.stringifyExpression(e.getValue()) + ")");
                            }
                        } else {
                            applySubresult.append(this.stringifyExpression(e.getValue()));
                        }

                        if (exprCount < numExpr) {
                            applySubresult.append(" " + removePrimitives(functionName) + " ");
                        }
                    }
                }
                return applySubresult.toString();
            }
        }
        if (expression instanceof AttributeDesignatorType) {
            AttributeDesignatorType adt = (AttributeDesignatorType) expression;

            String succintIdentifier = extractLastIdentifier(adt.getCategory(), ":") + ":"
                    + extractLastIdentifier(adt.getAttributeId(), ":");
            AttributeIdentifiers ai =
                    new AttributeIdentifiers(adt.getCategory(), adt.getDataType(), adt.getAttributeId());
            this.attributeIdentifiersMap.put(succintIdentifier, ai);

            return "<a href=\"#" + succintIdentifier + "\">" + succintIdentifier + "</a>";
        }
        if (expression instanceof AttributeSelectorType) {
            AttributeSelectorType ast = (AttributeSelectorType) expression;

            String attrName = ast.getPath();
            if (attrName == null || (attrName.length() == 0)) {
                return "";
            }

            String textSelector = "/text()";
            if (attrName.endsWith(textSelector)) {
                attrName = attrName.substring(0, attrName.length() - textSelector.length());
            }

            attrName = extractLastIdentifier(attrName, "/");
            attrName = extractLastIdentifier(attrName, ":");
            return " " + attrName;
        }
        if (expression instanceof AttributeValueType) {
            AttributeValueType avt = (AttributeValueType) expression;
            List<Object> content = avt.getContent();
            StringBuilder value_s = new StringBuilder();
            for (Object o : content) {
                value_s.append(" " + o.toString());
            }
            return " " + value_s.toString();
        }
        if (expression instanceof VariableReferenceType) {
            //
            // Really unknown - the variable may or may not have been defined
            //
            return " VARIABLEREF-NOT-HANDLED";
        } else {
            throw new IllegalArgumentException("Unexpected input expression");
        }
    }
}


class AttributeIdentifiers {
    public final String category;
    private String type;
    public final String id;

    public AttributeIdentifiers(String category, String type, String id) {
        this.category = category;
        this.setType(type);
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
