/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2018-2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

public class ImportServiceTest {
    @Test
    public void testNegativeCases() {
        ImportService service = new ImportService();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        service.doImportMicroServicePut(request, response);
        assertEquals("missing", response.getHeader("error"));
    }

    @Test
    public void testImportBRMS() {
        ImportService service = new ImportService();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("serviceName", "serviceName");
        request.setupAddParameter("importService", "BRMSPARAM");
        request.setBodyContent("foo");
        HttpServletResponse response = new MockHttpServletResponse();
        assertThatCode(() -> service.doImportMicroServicePut(request, response)).doesNotThrowAnyException();
    }

    @Test
    public void testImportMS() {
        ImportService service = new ImportService();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("serviceName", "serviceName");
        request.setupAddParameter("importService", "MICROSERVICE");
        request.setupAddParameter("fileName", "fileName");
        request.setBodyContent("foo");
        HttpServletResponse response = new MockHttpServletResponse();
        assertThatThrownBy(() -> service.doImportMicroServicePut(request, response)).isInstanceOf(Exception.class);
    }

    @Test
    public void testImportOpt() {
        ImportService service = new ImportService();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("serviceName", "serviceName");
        request.setupAddParameter("importService", "OPTIMIZATION");
        request.setupAddParameter("fileName", "fileName");
        request.setBodyContent("foo");
        HttpServletResponse response = new MockHttpServletResponse();
        assertThatThrownBy(() -> service.doImportMicroServicePut(request, response)).isInstanceOf(Exception.class);
    }

    @AfterClass
    public static void tearDown(){
        try {
            FileUtils.deleteDirectory(new File("ExtractDir"));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
