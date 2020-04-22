/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights reserved.
 * Modifications Copyright (C) 2019 Bell Canada
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

package org.onap.policy.controller;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.model.PDPGroupContainer;
import org.onap.policy.model.Roles;
import org.onap.policy.rest.adapter.AutoPushTabAdapter;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.PolicyEntity;
import org.onap.policy.rest.jpa.PolicyVersion;
import org.onap.policy.rest.util.PdpPolicyContainer;
import org.onap.policy.utils.PolicyUtils;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import org.onap.policy.xacml.std.pap.StdPDPPolicy;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({"/"})
public class AutoPushController extends RestrictedBaseController {

    private static final Logger logger = FlexLogger.getLogger(AutoPushController.class);

    @Autowired
    CommonClassDao commonClassDao;

    private PDPGroupContainer container;
    private PdpPolicyContainer policyContainer;
    private PolicyController policyController;
    protected List<OnapPDPGroup> groups = Collections.synchronizedList(new ArrayList<>());

    public PolicyController getPolicyController() {
        return policyController;
    }

    public void setPolicyController(PolicyController policyController) {
        this.policyController = policyController;
    }

    /**
     * refreshGroups.
     */
    public synchronized void refreshGroups() {
        synchronized (this.groups) {
            this.groups.clear();
            try {
                PolicyController controller = getPolicyControllerInstance();
                this.groups.addAll(controller.getPapEngine().getOnapPDPGroups());
            } catch (PAPException e) {
                String message = "Unable to retrieve Groups from server: " + e;
                logger.error(XACMLErrorConstants.ERROR_DATA_ISSUE + message);
            }

        }
    }

    private PolicyController getPolicyControllerInstance() {
        return policyController != null ? getPolicyController() : new PolicyController();
    }

    private Set<String> addAllScopes(Roles userRole, Set<String> scopes) {
        if (userRole.getScope() != null) {
            scopes.addAll(Stream.of(userRole.getScope().split(",")).collect(Collectors.toSet()));
        }
        return scopes;
    }

    /**
     * getPolicyGroupContainerData.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(
            value = {"/get_AutoPushPoliciesContainerData"},
            method = {RequestMethod.GET},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void getPolicyGroupContainerData(HttpServletRequest request, HttpServletResponse response) {
        try {
            Set<String> scopes = new HashSet<>();
            List<String> roles = new ArrayList<>();
            List<Object> data = new ArrayList<>();
            Map<String, Object> model = new HashMap<>();

            String userId = UserUtils.getUserSession(request).getOrgUserId();

            PolicyController controller = policyController != null ? getPolicyController() : new PolicyController();
            List<Object> userRoles = controller.getRoles(userId);
            for (Object role : userRoles) {
                Roles userRole = (Roles) role;
                roles.add(userRole.getRole());
                addAllScopes(userRole, scopes);
            }

            if (roles.contains("super-admin") || roles.contains("super-editor") || roles.contains("super-guest")) {
                data = commonClassDao.getData(PolicyVersion.class);
            } else {
                if (!scopes.isEmpty()) {
                    for (String scope : scopes) {
                        scope += "%";
                        String query = "From PolicyVersion where policy_name like :scope and id > 0";
                        SimpleBindings params = new SimpleBindings();
                        params.put("scope", scope);
                        List<Object> filterdatas = commonClassDao.getDataByQuery(query, params);
                        if (filterdatas != null) {
                            data.addAll(filterdatas);
                        }
                    }
                } else {
                    PolicyVersion emptyPolicyName = new PolicyVersion();
                    emptyPolicyName
                            .setPolicyName("Please Contact Policy Super Admin, There are no scopes assigned to you");
                    data.add(emptyPolicyName);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            model.put("policydatas", mapper.writeValueAsString(data));
            JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
            response.getWriter().write(new JSONObject(msg).toString());
        } catch (Exception e) {
            logger.error("Exception Occurred" + e);
        }
    }

    /**
     * pushPolicyToPDPGroup.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     * @throws IOException IOException
     */
    @RequestMapping(value = {"/auto_Push/PushPolicyToPDP.htm"}, method = {RequestMethod.POST})
    public ModelAndView pushPolicyToPDPGroup(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            //
            //
            //
            ArrayList<Object> selectedPdps = new ArrayList<>();
            ArrayList<String> selectedPoliciesInUI = new ArrayList<>();
            PolicyController controller = getPolicyControllerInstance();
            this.groups.addAll(controller.getPapEngine().getOnapPDPGroups());
            ObjectMapper mapper = new ObjectMapper();
            this.container = new PDPGroupContainer(controller.getPapEngine());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());

            String userId = UserUtils.getUserSession(request).getOrgUserId();
            logger.info(
                    "**********************Logging UserID while Pushing  Policy to PDP Group***********************");
            logger.info("UserId:  " + userId + "Push Policy Data:  " + root.get("pushTabData").toString());
            logger.info(
                    "**********************************************************************************************");

            AutoPushTabAdapter adapter = mapper.readValue(root.get("pushTabData").toString(), AutoPushTabAdapter.class);
            for (Object pdpGroupId : adapter.getPdpDatas()) {
                LinkedHashMap<?, ?> selectedPdp = (LinkedHashMap<?, ?>) pdpGroupId;
                for (OnapPDPGroup pdpGroup : this.groups) {
                    if (pdpGroup.getId().equals(selectedPdp.get("id"))) {
                        selectedPdps.add(pdpGroup);
                    }
                }
            }

            for (Object policyId : adapter.getPolicyDatas()) {
                LinkedHashMap<?, ?> selected = (LinkedHashMap<?, ?>) policyId;
                String policyName =
                        selected.get("policyName").toString() + "." + selected.get("activeVersion").toString() + ".xml";
                selectedPoliciesInUI.add(policyName);
            }

            for (Object pdpDestinationGroupId : selectedPdps) {
                Set<PDPPolicy> currentPoliciesInGroup = new HashSet<>();
                Set<PDPPolicy> selectedPolicies = new HashSet<>();
                for (String policyId : selectedPoliciesInUI) {
                    logger.debug("Handlepolicies..." + pdpDestinationGroupId + policyId);

                    //
                    // Get the current selection
                    //
                    assert policyId != null;
                    // create the id of the target file
                    // Our standard for file naming is:
                    // <domain>.<filename>.<version>.xml
                    // since the file name usually has a ".xml", we need to strip
                    // that
                    // before adding the other parts
                    String name = policyId.replace(File.separator, ".");
                    String id = name;
                    if (id.endsWith(".xml")) {
                        id = id.replace(".xml", "");
                        id = id.substring(0, id.lastIndexOf('.'));
                    }

                    // Default policy to be Root policy; user can change to deferred
                    // later

                    StdPDPPolicy selectedPolicy = null;
                    String dbCheckName = name;
                    if (dbCheckName.contains("Config_")) {
                        dbCheckName = dbCheckName.replace(".Config_", ":Config_");
                    } else if (dbCheckName.contains("Action_")) {
                        dbCheckName = dbCheckName.replace(".Action_", ":Action_");
                    } else if (dbCheckName.contains("Decision_")) {
                        dbCheckName = dbCheckName.replace(".Decision_", ":Decision_");
                    }
                    String[] split = dbCheckName.split(":");
                    String query = "FROM PolicyEntity where policyName = :split_1 and scope = :split_0";
                    SimpleBindings policyParams = new SimpleBindings();
                    policyParams.put("split_1", split[1]);
                    policyParams.put("split_0", split[0]);
                    List<Object> queryData = controller.getDataByQuery(query, policyParams);
                    PolicyEntity policyEntity = (PolicyEntity) queryData.get(0);
                    File temp = new File(name);
                    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
                    bw.write(policyEntity.getPolicyData());
                    bw.close();
                    try {
                        // Create the policy
                        selectedPolicy = new StdPDPPolicy(name, true, id, temp.toURI());
                    } catch (IOException e) {
                        logger.error("Unable to create policy '" + name + "': " + e.getMessage(), e);
                    }
                    StdPDPGroup selectedGroup = (StdPDPGroup) pdpDestinationGroupId;
                    if (selectedPolicy != null) {
                        // Add Current policies from container
                        for (OnapPDPGroup group : container.getGroups()) {
                            if (group.getId().equals(selectedGroup.getId())) {
                                currentPoliciesInGroup.addAll(group.getPolicies());
                            }
                        }
                        // copy policy to PAP
                        try {
                            controller.getPapEngine().copyPolicy(selectedPolicy, (StdPDPGroup) pdpDestinationGroupId, userId);
                        } catch (PAPException e) {
                            logger.error("Exception Occured" + e);
                            return null;
                        }
                        selectedPolicies.add(selectedPolicy);
                    }
                    temp.delete();
                }
                StdPDPGroup pdpGroup = (StdPDPGroup) pdpDestinationGroupId;
                StdPDPGroup updatedGroupObject = new StdPDPGroup(pdpGroup.getId(), pdpGroup.isDefaultGroup(),
                        pdpGroup.getName(), pdpGroup.getDescription(), pdpGroup.getDirectory());
                updatedGroupObject.setOnapPdps(pdpGroup.getOnapPdps());
                updatedGroupObject.setPipConfigs(pdpGroup.getPipConfigs());
                updatedGroupObject.setStatus(pdpGroup.getStatus());
                updatedGroupObject.setOperation("push");

                // replace the original set of Policies with the set from the
                // container (possibly modified by the user)
                // do not allow multiple copies of same policy
                Iterator<PDPPolicy> policyIterator = currentPoliciesInGroup.iterator();
                logger.debug("policyIterator....." + selectedPolicies);
                while (policyIterator.hasNext()) {
                    PDPPolicy existingPolicy = policyIterator.next();
                    for (PDPPolicy selPolicy : selectedPolicies) {
                        if (selPolicy.getName().equals(existingPolicy.getName())) {
                            if (selPolicy.getVersion().equals(existingPolicy.getVersion())) {
                                if (selPolicy.getId().equals(existingPolicy.getId())) {
                                    policyIterator.remove();
                                    logger.debug("Removing policy: " + selPolicy);
                                    break;
                                }
                            } else {
                                policyIterator.remove();
                                logger.debug("Removing Old Policy version: " + selPolicy);
                                break;
                            }
                        }
                    }
                }

                currentPoliciesInGroup.addAll(selectedPolicies);
                updatedGroupObject.setPolicies(currentPoliciesInGroup);
                this.container.updateGroup(updatedGroupObject, userId);
                response.setContentType(PolicyUtils.APPLICATION_JSON);
                refreshGroups();
                response.getWriter().write(new JSONObject(
                        new JsonMessage(mapper.writeValueAsString(groups))).toString());
            }
        } catch (Exception e) {
            logger.error(e);
            response.getWriter().write(PolicyUtils.CATCH_EXCEPTION);
        }
        return null;
    }

    /**
     * removePDPGroup.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = {"/auto_Push/remove_GroupPolicies.htm"}, method = {RequestMethod.POST})
    public ModelAndView removePDPGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            request.setCharacterEncoding(PolicyUtils.CHARACTER_ENCODING);
            //
            //
            //
            PolicyController controller = getPolicyControllerInstance();
            this.container = new PDPGroupContainer(controller.getPapEngine());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode root = mapper.readTree(request.getReader());
            final StdPDPGroup group = mapper.readValue(root.get("activePdpGroup").toString(), StdPDPGroup.class);
            final JsonNode removePolicyData = root.get("data");

            String userId = UserUtils.getUserSession(request).getOrgUserId();
            logger.info(
                    "**********************Logging UserID while Removing Policy from PDP Group*********************");
            logger.info("UserId:  " + userId + "PDP Group Data:  " + root.get("activePdpGroup").toString()
                    + "Remove Policy Data: " + root.get("data"));
            logger.info(
                    "**********************************************************************************************");

            policyContainer = new PdpPolicyContainer(group);
            if (removePolicyData.size() > 0) {
                IntStream.range(0, removePolicyData.size()).mapToObj(i -> removePolicyData.get(i).toString())
                        .forEach(polData -> this.policyContainer.removeItem(polData));
                Set<PDPPolicy> changedPolicies =
                        new HashSet<>((Collection<PDPPolicy>) this.policyContainer.getItemIds());
                StdPDPGroup updatedGroupObject = new StdPDPGroup(group.getId(), group.isDefaultGroup(), group.getName(),
                        group.getDescription(), null);
                updatedGroupObject.setPolicies(changedPolicies);
                updatedGroupObject.setOnapPdps(group.getOnapPdps());
                updatedGroupObject.setPipConfigs(group.getPipConfigs());
                updatedGroupObject.setStatus(group.getStatus());
                updatedGroupObject.setOperation("delete");
                this.container.updateGroup(updatedGroupObject, userId);
            }

            response.setContentType(PolicyUtils.APPLICATION_JSON);

            refreshGroups();
            response.getWriter().write(new JSONObject(new JsonMessage(mapper.writeValueAsString(groups))).toString());
        } catch (Exception e) {
            logger.error(e);
            response.getWriter().write(PolicyUtils.CATCH_EXCEPTION);
        }
        return null;
    }
}
