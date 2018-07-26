/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
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

package org.onap.policy.xacml.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdMutableAdvice;
import com.att.research.xacml.std.StdMutableAttributeAssignment;
import com.att.research.xacml.std.StdMutableMissingAttributeDetail;
import com.att.research.xacml.std.StdMutableObligation;
import com.att.research.xacml.std.StdMutableResponse;
import com.att.research.xacml.std.StdMutableResult;
import com.att.research.xacml.std.StdMutableStatus;
import com.att.research.xacml.std.StdMutableStatusDetail;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.util.XACMLProperties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.impl.IndexedHttpServletServerFactory;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.xacml.custom.OnapFunctionDefinitionFactory;

public class FindActionTest {

    String xpathexamplefromspec = "{ " + "\"Request\" : { " + "\"Resource\" : { " + "\"Attribute\": [ " + "{ "
            + "\"Id\" : \"urn:oasis:names:tc:xacml:3.0:content-selector\", " + "\"DataType\" : \"xpathExpression\", "
            + "\"Value\" : { " + "\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", "
            + "\"Namespaces\" : [{ " + "\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " + "}, "
            + "{ " + "\"Prefix\" : \"md\", " + "\"Namespace\" : \"urn:example:med:schemas:record\" " + "} " + "], "
            + "\"XPath\" : \"md:record/md:patient/md:patientDoB\" " + "} " + "} " + "] " + "} " + "} " + "} ";

    String jsonResponse;
    Request request;
    private static final int MOCK_SERVER_PORT = 6670;

    /**
     * Setup server before test class.
     */
    @BeforeClass
    public static void setUpServer() {
        try {
            final HttpServletServer testServer = IndexedHttpServletServerFactory.getInstance().build("dmaapSim",
                    "localhost", MOCK_SERVER_PORT, "/", false, true);
            testServer.addServletClass("/*", DummyRest.class.getName());
            testServer.waitedStart(2000);
            if (!NetworkUtil.isTcpPortOpen("localhost", testServer.getPort(), 5, 10000L)) {
                throw new IllegalStateException("cannot connect to port " + testServer.getPort());
            }
        } catch (final Exception e) {
            fail(e.getMessage());
        }

    }

    @AfterClass
    public static void tearDownSimulator() {
        IndexedHttpServletServerFactory.getInstance().destroy();
    }

    /**
     * Setup before test case execution.
     *
     * @throws Exception if any error occurs
     */
    @Before
    public void setUp() throws Exception {
        new OnapFunctionDefinitionFactory();
        request = JSONRequest.load(xpathexamplefromspec);

        try {
            XACMLProperties.reloadProperties();
            System.setProperty(XACMLProperties.XACML_PROPERTIES_NAME, "src/test/resources/xacml.pdp.properties");
            XACMLProperties.getProperties();

            assertTrue(true);
        } catch (final Exception e) {
            fail();

        }
    }


    @Test
    public final void testRun() {
        final FindAction action = new FindAction();
        // fully-loaded multiple response
        final StdMutableResponse response = new StdMutableResponse();
        // create a Status object
        StdMutableStatus status = new StdMutableStatus(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
        status.setStatusMessage("some status message");
        final StdMutableStatusDetail statusDetailIn = new StdMutableStatusDetail();
        final StdMutableMissingAttributeDetail mad = new StdMutableMissingAttributeDetail();
        mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "PEPACTION"));
        mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_INTEGER.getId(), "PDPACTION"));
        mad.setAttributeId(XACML3.ID_ACTION_PURPOSE);
        mad.setCategory(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
        mad.setDataTypeId(XACML3.ID_DATATYPE_STRING);
        mad.setIssuer("an Issuer");
        statusDetailIn.addMissingAttributeDetail(mad);
        status.setStatusDetail(statusDetailIn);
        // create a single result object
        StdMutableResult result = new StdMutableResult(status);
        // set the decision
        result.setDecision(Decision.INDETERMINATE);
        // put the Result into the Response
        response.add(result);
        // create a new Result with a different Decision
        status = new StdMutableStatus(StdStatusCode.STATUS_CODE_OK);
        result = new StdMutableResult(status);
        result.setDecision(Decision.DENY);

        StdMutableObligation obligation = new StdMutableObligation();
        obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
        obligation.addAttributeAssignment(
                new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, XACML3.ID_SUBJECT,
                        "obligation-issuer1", new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Test")));
        result.addObligation(obligation);

        final StdMutableAdvice advice = new StdMutableAdvice();
        advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
        advice.addAttributeAssignment(
                new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, XACML3.ID_SUBJECT,
                        "advice-issuer1", new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Test")));
        response.add(result);

        // The logic below exercises the callRest and takeAction methods in FindAction
        // GET request
        status = new StdMutableStatus(StdStatusCode.STATUS_CODE_OK);
        result = new StdMutableResult(status);
        result.setDecision(Decision.PERMIT);

        obligation = new StdMutableObligation();
        obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("performer"), "obligation-issuer",
                new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "PDPACTION")));

        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("URL"), "obligation-issuer",
                new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "http://localhost:" + MOCK_SERVER_PORT)));
        obligation.addAttributeAssignment(
                new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("method"),
                        "obligation-issuer", new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "GET")));
        obligation.addAttributeAssignment(
                new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("headers"),
                        "obligation-issuer", new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "foobar")));


        result.addObligation(obligation);
        response.add(result);

        // POST request
        status = new StdMutableStatus(StdStatusCode.STATUS_CODE_OK);
        result = new StdMutableResult(status);
        result.setDecision(Decision.PERMIT);

        obligation = new StdMutableObligation();
        obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("performer"), "obligation-issuer",
                new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "PDPACTION")));

        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("URL"), "obligation-issuer",
                new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "http://localhost:" + MOCK_SERVER_PORT)));
        obligation.addAttributeAssignment(
                new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("method"),
                        "obligation-issuer", new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "POST")));
        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("body"), "obligation-issuer", new StdAttributeValue<String>(
                        DataTypes.DT_STRING.getId(), "http://localhost:" + MOCK_SERVER_PORT + "/foobar")));

        result.addObligation(obligation);
        response.add(result);

        // PUT request
        status = new StdMutableStatus(StdStatusCode.STATUS_CODE_OK);
        result = new StdMutableResult(status);
        result.setDecision(Decision.PERMIT);

        obligation = new StdMutableObligation();
        obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("performer"), "obligation-issuer",
                new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "PDPACTION")));

        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("URL"), "obligation-issuer",
                new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "http://localhost:" + MOCK_SERVER_PORT)));
        obligation.addAttributeAssignment(
                new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, new IdentifierImpl("method"),
                        "obligation-issuer", new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "PUT")));
        obligation.addAttributeAssignment(new StdMutableAttributeAssignment(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                new IdentifierImpl("body"), "obligation-issuer", new StdAttributeValue<String>(
                        DataTypes.DT_STRING.getId(), "http://localhost:" + MOCK_SERVER_PORT + "/foobar")));

        result.addObligation(obligation);
        response.add(result);

        try {
            assertTrue(action.run(response, request) != null);
        } catch (final Exception e) {
            fail("operation failed, e=" + e);
        }
    }

}
