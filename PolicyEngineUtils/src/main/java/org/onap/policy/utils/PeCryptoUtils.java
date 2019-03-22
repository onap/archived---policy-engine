/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
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

import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.utils.security.CryptoUtils;

public class PeCryptoUtils {

    private static Logger logger = FlexLogger.getLogger(PeCryptoUtils.class);
    private static final String PROP_AES_KEY = "org.onap.policy.encryption.aes.key";
    private static CryptoUtils cryptoUtils = null;
    private static String secretKey = System.getenv("AES_ENCRYPTION_KEY");
    private static final Map<String, String> decryptCache = new ConcurrentHashMap<>();
    private static final Map<String, String> encryptCache = new ConcurrentHashMap<>();


    private PeCryptoUtils() {}

    /**
     * Inits the aes key.
     *
     * @param theSecretKey the the secret key
     */
    public static void initAesKey(String theSecretKey) {
        String secKey = theSecretKey;
        if (cryptoUtils == null) {
            if (StringUtils.isBlank(secKey)) {
                secKey = System.getProperty(PROP_AES_KEY);
            }
            if (StringUtils.isBlank(secKey)) {
                secKey = secretKey;
            }
            cryptoUtils = new CryptoUtils(secKey);
        }
    }

    /**
     * Encrypt a value based on the Policy Encryption Key.
     *
     * @param value The plain text string
     * @return The encrypted String
     */
    public static String encrypt(String value) {

        if (cryptoUtils == null || StringUtils.isBlank(value)) {
            return value;
        }

        return encryptCache.computeIfAbsent(value, k -> {
            try {
                return cryptoUtils.encrypt(k);
            } catch (GeneralSecurityException e) {
                logger.error("Could not decrypt value - exception: ", e);
                return value;
            }
        });
    }

    /**
     * Decrypt a value based on the Policy Encryption Key if string begin with 'enc:'.
     *
     * @param value The encrypted string that must be decrypted using the Policy Encryption Key
     * @return The String decrypted if string begin with 'enc:'
     */
    public static String decrypt(String value) {
        if (cryptoUtils == null || StringUtils.isBlank(value)) {
            return value;
        }
        return decryptCache.computeIfAbsent(value, k -> {
            try {
                return cryptoUtils.decrypt(k);
            } catch (GeneralSecurityException e) {
                logger.error("Could not decrypt value - exception: ", e);
                return value;
            }
        });
    }
}
