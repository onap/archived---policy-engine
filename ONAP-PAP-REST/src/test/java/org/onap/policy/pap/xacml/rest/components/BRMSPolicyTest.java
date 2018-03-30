/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.policy.pap.xacml.rest.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.onap.policy.rest.dao.CommonClassDao;

public class BRMSPolicyTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testConstructor1() {
    CreateBRMSRuleTemplate template = new CreateBRMSRuleTemplate();
    assertNotNull(template);
  }

  @Test
  public void testConstructor2() {
    CommonClassDao commonClassDao = null;
    CreateBRMSRuleTemplate template = new CreateBRMSRuleTemplate(commonClassDao);
    assertNotNull(template);
  }

  @Test
  public void testReadFile() throws IOException {
    String goodRule = "declare Params\nparam1 : int\nend\n";
    String badRule = "declare Params\nparam1+ : int\nend\n";
    assertEquals(CreateBRMSRuleTemplate.validateRuleParams(goodRule), true);
    assertEquals(CreateBRMSRuleTemplate.validateRuleParams(badRule), false);
  }

  @Test
  public void testAdd() {
    CommonClassDao dao = Mockito.mock(CommonClassDao.class);
    CreateBRMSRuleTemplate template = new CreateBRMSRuleTemplate(dao);
    String rule = "package foo\n";
    String ruleName = "testName";
    String description = "testDesc";
    String userID = "testID";
    assertEquals(1, template.addRule(rule, ruleName, description, userID).size());
  }
}
