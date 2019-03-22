/*-
 * ============LICENSE_START=======================================================
 * ONAP-REST
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;


public class PeCryptoUtilsTest {
    private final String pass = "policy_user";
    private final String secretKey = "bmpybWJrbGN4dG9wbGF3Zg==";
    private final String encryptedPass = "enc:5ID9PoqWIzBaut+KQcAFBtci9CKDRcCNRHRjdBnXM5U=";
    private static final String PROP_AES_KEY = "org.onap.policy.encryption.aes.key";

    @Before
    public void reset() {
        resetStaticField("cryptoUtils", null);
    }

    private void resetStaticField(String fieldName, String value) {
        Field field;
        if (StringUtils.isBlank(fieldName)) {
            fieldName = "cryptoUtils";
        }
        try {
            field = PeCryptoUtils.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEncrypt() throws GeneralSecurityException {
        assertEquals(pass, PeCryptoUtils.encrypt(pass));
        PeCryptoUtils.initAesKey(secretKey);
        System.out.println("original value : " + pass + "  encrypted value: " + PeCryptoUtils.encrypt(pass));
        assertNotNull(PeCryptoUtils.encrypt(pass));
    }

    @Test
    public void testDecrypt() throws Exception {
        assertEquals(pass, PeCryptoUtils.decrypt(pass));
        System.setProperty(PROP_AES_KEY, secretKey);
        PeCryptoUtils.initAesKey(null);
        System.clearProperty(PROP_AES_KEY);
        assertEquals(pass, PeCryptoUtils.decrypt(encryptedPass));

        resetStaticField("cryptoUtils", null);
        resetStaticField("secretKey", secretKey);
        PeCryptoUtils.initAesKey(" ");
        assertEquals(pass, PeCryptoUtils.decrypt(pass));
    }

}
