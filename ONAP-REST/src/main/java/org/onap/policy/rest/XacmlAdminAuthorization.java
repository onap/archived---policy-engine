/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
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

package org.onap.policy.rest;

public class XacmlAdminAuthorization {

    public enum Role {
        ROLE_GUEST("guest"),
        ROLE_ADMIN("admin"),
        ROLE_EDITOR("editor"),
        ROLE_SUPERGUEST("super-guest"),
        ROLE_SUPEREDITOR("super-editor"),
        ROLE_SUPERADMIN("super-admin");

        String userRole;

        Role(String a) {
            this.userRole = a;
        }
        @Override
        public String toString() {
            return this.userRole;
        }
    }
}
