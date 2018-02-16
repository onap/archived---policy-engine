/*-
 * ============LICENSE_START=======================================================
 * ONAP-XACML
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
package org.onap.policy.xacml.test.std.pap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.policy.xacml.api.pap.OnapPDP;
import org.onap.policy.xacml.api.pap.OnapPDPGroup;
import org.onap.policy.xacml.std.pap.StdEngine;
import org.onap.policy.xacml.std.pap.StdPDPGroup;
import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.api.pap.PDPPolicy;

public class StdEngineTest {

    private Path repository;
    Properties properties = new Properties();
    StdEngine stdEngine = null;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @BeforeClass
    public static void setUpClass() throws IOException{
        new File("target/test/resources/pdps").mkdirs();
        new File("target/test/resources/emptyPapGroupsDefault").mkdirs();
        Files.copy(Paths.get("src/test/resources/pdps/xacml.properties"), Paths.get("target/test/resources/pdps/xacml.properties"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get("src/test/resources/emptyPapGroupsDefault/xacml.properties"), Paths.get("target/test/resources/emptyPapGroupsDefault/xacml.properties"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Before
    public void setUp() throws PAPException, IOException {

        repository = Paths.get("target/test/resources/pdps");
        stdEngine = new StdEngine(repository);
    }

    @Test
    public void testGetDefaultGroup() throws PAPException {
        assertTrue(stdEngine.getDefaultGroup() != null);
    }

    @Test
    public void testGetGroup() throws PAPException {
        assertTrue(stdEngine.getGroup("1") == null);
    }

    @Test
    public void testGetOnapPDPGroups() throws PAPException {
        assertTrue(stdEngine.getOnapPDPGroups() != null);
    }

    @Test
    public void testGetPDP() throws PAPException {
        assertTrue(stdEngine.getPDP("1") == null);
    }

    @Test
    public void testGetPDPGroup() throws PAPException {
        assertTrue(stdEngine.getPDPGroup(null) == null);
    }

    @Test
    public void testNoRepository() throws PAPException, IOException {
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("No repository specified.");
        new StdEngine((Path)null);
    }
    
    @Test
    public void testRepositoryDoesNotExist() throws PAPException, IOException {
        repository = Paths.get("target/test/resources/nonExisting");
        new StdEngine(repository);
        
        assertTrue(Files.exists(Paths.get("target/test/resources/nonExisting/default/xacml.pip.properties")));
        assertTrue(Files.exists(Paths.get("target/test/resources/nonExisting/default/xacml.policy.properties")));
        assertTrue(Files.exists(Paths.get("target/test/resources/nonExisting/xacml.properties")));
        FileUtils.deleteDirectory(repository.toFile());
    }
    
    @Test
    public void testEmptyPapGroupsDefault() throws PAPException, IOException {
        System.setProperty("xacml.pap.groups.default", "");
        repository = Paths.get("target/test/resources/emptyPapGroupsDefault");
        new StdEngine(repository);
        
        assertTrue(Files.exists(Paths.get("target/test/resources/emptyPapGroupsDefault/default/xacml.pip.properties")));
        assertTrue(Files.exists(Paths.get("target/test/resources/emptyPapGroupsDefault/default/xacml.policy.properties")));
        assertTrue(Files.exists(Paths.get("target/test/resources/emptyPapGroupsDefault/xacml.properties")));
    }
    
    @Test
    public void testNewGroupAndRemoveGroup() throws NullPointerException, PAPException, IOException {
        OnapPDPGroup newGroup = createGroup("newGroup", "Description of new group");
        assertNotNull(newGroup);

        stdEngine.removeGroup(stdEngine.getGroup("newGroup"), null);
        assertNull(stdEngine.getGroup("newGroup"));
    }
    
    @Test
    public void testRemoveGroupNull() throws NullPointerException, PAPException, IOException {
        expectedException.expect(NullPointerException.class);
        stdEngine.removeGroup(null, null);
        assertNull(stdEngine.getGroup("newGroup"));
    }
    
    @Test
    public void testRemoveGroupUnknown() throws NullPointerException, PAPException, IOException {
        OnapPDPGroup unknownGroup = new StdPDPGroup("unknownId", null);

        expectedException.expect(PAPException.class);
        expectedException.expectMessage("The group 'unknownId' does not exist");
        stdEngine.removeGroup(unknownGroup, null);
    }
    
    @Test
    public void testRemoveGroupDefault() throws NullPointerException, PAPException, IOException {
        OnapPDPGroup defaultGroup = stdEngine.getDefaultGroup();

        expectedException.expect(PAPException.class);
        expectedException.expectMessage("You cannot delete the default group.");
        stdEngine.removeGroup(defaultGroup, null);
    }
    
    @Test
    public void testSetDefaultGroup() throws NullPointerException, PAPException, IOException {
        OnapPDPGroup newGroup = createGroup("newGroup", "Description of new group");
        assertNotNull(newGroup);
        
        OnapPDPGroup defaultGroup = stdEngine.getDefaultGroup();
        assertEquals("default", defaultGroup.getName());
        stdEngine.setDefaultGroup(newGroup);
        assertEquals(newGroup, stdEngine.getDefaultGroup());
        
        stdEngine.setDefaultGroup(defaultGroup);
        stdEngine.removeGroup(stdEngine.getGroup("newGroup"), null);
    }
    
    @Test
    public void testPdps() throws NullPointerException, PAPException{
        OnapPDPGroup group1 = createGroup("newGroup", "Description of new group");
        assertEquals(0, group1.getPdps().size());
        
        stdEngine.newPDP("newPdp", group1, "newPdpName", "A new pdp", 1);
        assertEquals(1, group1.getPdps().size());
        assertEquals("newPdp", group1.getPdps().iterator().next().getId());
        assertEquals("newPdpName", group1.getPdps().iterator().next().getName());
        assertEquals("A new pdp", group1.getPdps().iterator().next().getDescription());
        
        OnapPDPGroup group2 = createGroup("anotherNewGroup", "Description of new group");
        assertEquals(0, group2.getPdps().size());
        
        stdEngine.movePDP(group1.getOnapPdps().iterator().next(), group2);
        assertEquals(0, group1.getPdps().size());
        assertEquals(1, group2.getPdps().size());
        
        OnapPDP pdp = group2.getOnapPdps().iterator().next();
        pdp.setName("AnUpdatedName");;
        pdp.setDescription("An updated description");
        stdEngine.updatePDP(pdp);
        assertEquals("AnUpdatedName", group2.getPdps().iterator().next().getName());
        assertEquals("An updated description", group2.getPdps().iterator().next().getDescription());
        
        stdEngine.removePDP(group2.getOnapPdps().iterator().next());
        assertEquals(0, group1.getPdps().size());
        assertEquals(0, group2.getPdps().size());
    }
    
    @Test
    public void testNewPdpNullGroup() throws NullPointerException, PAPException{
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("You must specify which group the PDP will belong to.");
        stdEngine.newPDP("newPdp", null, "newPdpName", "A new pdp", 1);
    }
    
    @Test
    public void testNewPdpUnknownGroup() throws NullPointerException, PAPException{
        OnapPDPGroup unknownGroup = new StdPDPGroup("unknownId", null);

        expectedException.expect(PAPException.class);
        expectedException.expectMessage("Unknown group, not in our list.");
        stdEngine.newPDP("newPdp", unknownGroup, "newPdpName", "A new pdp", 1);
    }
    
    @Test
    public void testNewPdpAlreadyExistingPdp() throws NullPointerException, PAPException{
        OnapPDPGroup group1 = createGroup("newGroup", "Description of new group");
        assertEquals(0, group1.getPdps().size());
        
        stdEngine.newPDP("newPdp", group1, "newPdpName", "A new pdp", 1);
        assertEquals(1, group1.getPdps().size());
        
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("A PDP with this ID exists.");
        stdEngine.newPDP("newPdp", group1, "newPdpName", "A new pdp", 1);
    }
    
    @Test
    public void testRemoveGroupWithPdps() throws NullPointerException, PAPException{
        OnapPDPGroup group1 = createGroup("newGroup", "Description of new group");
        
        stdEngine.newPDP("newPdp", group1, "newPdpName", "A new pdp", 1);
        assertEquals(1, group1.getPdps().size());
        
        OnapPDPGroup group2 = createGroup("anotherNewGroup", "Description of new group");
        assertEquals(0, group2.getPdps().size());
        
        stdEngine.removeGroup(group1, group2);
        assertNull(stdEngine.getGroup("newGroup"));
        assertEquals(1, group2.getPdps().size());
    }
    
    @Test
    public void testRemoveGroupWithPdpsNoTarget() throws NullPointerException, PAPException{
        OnapPDPGroup group1 = createGroup("newGroup", "Description of new group");
        
        stdEngine.newPDP("newPdp", group1, "newPdpName", "A new pdp", 1);
        assertEquals(1, group1.getPdps().size());
        
        OnapPDPGroup group2 = createGroup("anotherNewGroup", "Description of new group");
        assertEquals(0, group2.getPdps().size());
        
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Group targeted for deletion has PDPs, you must provide a new group for them.");
        stdEngine.removeGroup(group1, null);
    }
    
    @Test
    public void testUpdateGroupName() throws NullPointerException, PAPException, IOException {
        OnapPDPGroup newGroup = createGroup("newGroup", "Description of new group");
        
        OnapPDPGroup updatedGroup = new StdPDPGroup(newGroup);
        updatedGroup.setName("AnUpdatedName");
        stdEngine.updateGroup(updatedGroup);
        assertNull(stdEngine.getGroup("newGroup"));
        assertNotNull(stdEngine.getGroup("AnUpdatedName"));
        assertEquals("AnUpdatedName", stdEngine.getGroup("AnUpdatedName").getName());
        assertEquals("Description of new group", stdEngine.getGroup("AnUpdatedName").getDescription());
    }
    
    @Test
    public void testUpdateGroupDescription() throws NullPointerException, PAPException, IOException {
        OnapPDPGroup newGroup = createGroup("newGroup", "Description of new group");
                
        OnapPDPGroup updatedGroup = new StdPDPGroup(newGroup.getId(), newGroup.getName(), "An updated description", Paths.get("target/test/resources/pdps/newGroup"));
        updatedGroup.setDescription("An updated description");
        stdEngine.updateGroup(updatedGroup);
        assertEquals("newGroup", stdEngine.getGroup("newGroup").getName());
        assertEquals("An updated description", stdEngine.getGroup("newGroup").getDescription());
    }
    
    @Test
    public void testUpdateGroupNull() throws NullPointerException, PAPException, IOException {
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("Group or id is null");
        stdEngine.updateGroup(null);
    }
    
    @Test
    public void testUpdateGroupIdNull() throws NullPointerException, PAPException, IOException {
        StdPDPGroup group = new StdPDPGroup(null, null);
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("Group or id is null");
        stdEngine.updateGroup(group);
    }
    
    @Test
    public void testUpdateGroupNameNull() throws NullPointerException, PAPException, IOException {
        StdPDPGroup group = new StdPDPGroup("groupId", null);
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("New name for group cannot be null or blank");
        stdEngine.updateGroup(group);
    }
    
    @Test
    public void testUpdateGroupNameEmptyString() throws NullPointerException, PAPException, IOException {
        StdPDPGroup group = new StdPDPGroup("groupId", "", "description", null);
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("New name for group cannot be null or blank");
        stdEngine.updateGroup(group);
    }
    
    @Test
    public void testUpdateGroupUnknown() throws NullPointerException, PAPException, IOException {
        StdPDPGroup group = new StdPDPGroup("groupId", "groupName", "description", null);
        expectedException.expect(PAPException.class);
        expectedException.expectMessage("Update found no existing group with id 'groupId'");
        stdEngine.updateGroup(group);
    }
    
    @Test
    public void testPublishAndRemovePolicy() throws NullPointerException, PAPException, FileNotFoundException{
        OnapPDPGroup newGroup = createGroup("newGroup", "Description of new group");
        InputStream inputStream = new FileInputStream("src/test/resources/pdps/default/com.Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml");
        stdEngine.publishPolicy("com.Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml", "Config_BRMS_Param_BRMSParamvFWDemoPolicy", true, inputStream, newGroup);
        PDPPolicy policy = newGroup.getPolicy("com.Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml");
        assertNotNull(policy);
        
        stdEngine.removePolicy(policy, newGroup);
        assertNull(newGroup.getPolicy("com.Config_BRMS_Param_BRMSParamvFWDemoPolicy.1.xml"));
    }
    
    @Test
    public void testPublishPolicyNull() throws NullPointerException, PAPException, FileNotFoundException{
        expectedException.expect(NullPointerException.class);
        stdEngine.publishPolicy(null, null, true, null, null);
    }
    
    @Test
    public void testPublishPolicyUnknownGroup() throws NullPointerException, PAPException, FileNotFoundException{
        OnapPDPGroup unknownGroup = new StdPDPGroup("unknownId", null);

        expectedException.expect(PAPException.class);
        expectedException.expectMessage("Unknown PDP Group: unknownId");
        stdEngine.publishPolicy(null, null, true, null, unknownGroup);
    }
    
    @Test
    public void testRemovePolicyNull() throws NullPointerException, PAPException, FileNotFoundException{
        expectedException.expect(NullPointerException.class);
        stdEngine.removePolicy(null, null);
    }
    
    @Test
    public void testRemovePolicyUnknownGroup() throws NullPointerException, PAPException, FileNotFoundException{
        OnapPDPGroup unknownGroup = new StdPDPGroup("unknownId", null);

        expectedException.expect(PAPException.class);
        expectedException.expectMessage("Unknown PDP Group: unknownId");
        stdEngine.removePolicy(null, unknownGroup);
    }
    
    private OnapPDPGroup createGroup(final String name, final String description) throws NullPointerException, PAPException{
        ensureGroupDoesntExist(name);
        stdEngine.newGroup(name, description);
        return stdEngine.getGroup(name);
    }
    
    @After
    public void tearDown() throws PAPException{
        ensureGroupDoesntExist("newGroup");
        ensureGroupDoesntExist("anotherNewGroup");
        ensureGroupDoesntExist("AnUpdatedName");
    }
    
    private void ensureGroupDoesntExist(final String groupName) throws PAPException{
        OnapPDPGroup group = stdEngine.getGroup(groupName);
        if (group != null){
            for (OnapPDP pdp: group.getOnapPdps()){
                stdEngine.removePDP(pdp);
            }
            stdEngine.removeGroup(group, null);       
       }
    }
}
