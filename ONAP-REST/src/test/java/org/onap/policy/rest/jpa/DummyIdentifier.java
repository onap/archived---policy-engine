/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.rest.jpa;

import com.att.research.xacml.api.Identifier;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Dummy category identifier class.
 */
public class DummyIdentifier implements Identifier {

    @Override
    public String stringValue() {
        return this.getClass().getName();
    }

    @Override
    public URI getUri() {
        try {
            return new URI(this.getClass().getName());
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
