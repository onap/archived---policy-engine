/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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
package org.onap.policy.utils;

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class CryptoUtils {
	private static final Logger LOGGER = FlexLogger.getLogger(CryptoUtils.class);
	private static final String CIPHER_TYPE = "AES/CBC/PKCS5Padding";
	private static Key mKey = null;
	private static AlgorithmParameters mAlgParm = null;

	static {
		//the hadcoded key is to be removed in a future iteration
		try {
			String kval = "bmpybWJrbGN4dG9wbGF3Zg==";
			String algp = "BBBpbml0VmVjVGhpc0lzVGhl";

			byte[] kvalb = DatatypeConverter.parseBase64Binary(kval);
			byte[] algb = DatatypeConverter.parseBase64Binary(algp);

			mKey = new SecretKeySpec(kvalb, "AES");

			mAlgParm = AlgorithmParameters.getInstance("AES");
			mAlgParm.init(algb, "ASN.1");

		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private CryptoUtils() {
		// Private Constructor
	}

	/**
	 * Decrypt txt.
	 *
	 * @param encryptedTxt
	 *            text to be decrypted, Base 64 UrlEncoded
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws NoSuchPaddingException
	 *             the no such padding exception
	 * @throws InvalidAlgorithmParameterException
	 *             the invalid algorithm parameter exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 * @throws IllegalBlockSizeException
	 *             the illegal block size exception
	 * @throws BadPaddingException
	 *             the bad padding exception
	 */
	public static byte[] decryptTxt(String encryptedTxt)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
		cipher.init(Cipher.DECRYPT_MODE, mKey, mAlgParm);

		return cipher.doFinal(Base64.getUrlDecoder().decode(encryptedTxt.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Decrypt txt.
	 *
	 * @param encryptedTxt
	 *            text to be decrypted, Base 64 UrlEncoded
	 * @param mKey
	 *            the key as Base 64
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws NoSuchPaddingException
	 *             the no such padding exception
	 * @throws InvalidAlgorithmParameterException
	 *             the invalid algorithm parameter exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 * @throws IllegalBlockSizeException
	 *             the illegal block size exception
	 * @throws BadPaddingException
	 *             the bad padding exception
	 */
	public static byte[] decryptTxt(String encryptedTxt, String base64BinaryKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		byte[] keyValueByte = DatatypeConverter.parseBase64Binary(base64BinaryKey);
		Key paramKey = new SecretKeySpec(keyValueByte, "AES");
		Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
		cipher.init(Cipher.DECRYPT_MODE, paramKey, mAlgParm);

		return cipher.doFinal(Base64.getUrlDecoder().decode(encryptedTxt.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Decrypt txt, no exceptions thrown.
	 *
	 * @param encryptedTxt
	 *            text to be decrypted, Base 64 UrlEncoded
	 * @return the decrypted text, or the original text if it could not be
	 *         decrypted
	 */
	public static byte[] decryptTxtNoEx(String encryptedTxt) {

		try {
			if (encryptedTxt == null || encryptedTxt.isEmpty()) {
				LOGGER.info("decryptTxtNoEx: Input param encryptedTxt is empty");
				return new byte[0];
			}
			return decryptTxt(encryptedTxt);
		} catch (Exception e) {
			try {
				LOGGER.info("decryptTxtNoEx: Exception while decrypting : " + e);
				return (encryptedTxt != null) ? encryptedTxt.getBytes(StandardCharsets.UTF_8) : new byte[0];
			} catch (Exception e1) {
				LOGGER.warn("decryptTxtNoEx: Exception on sending default : " + e1);
				return new byte[0];
			}
		}
	}
	
	/**
	 * Decrypt txt, no exceptions thrown.
	 *
	 * @param encryptedTxt
	 *            text to be decrypted, Base 64 UrlEncoded
	 * @return the decrypted text, or the original text if it could not be
	 *         decrypted
	 */
	public static String decryptTxtNoExStr(String encryptedTxt) {
			return new String(decryptTxtNoEx(encryptedTxt), StandardCharsets.UTF_8);
	}

	/**
	 * Encrypt txt.
	 *
	 * @param plainTxt
	 *            the plain txt
	 * @return the encrypted string
	 * @throws NoSuchPaddingException
	 *             the no such padding exception
	 * @throws InvalidAlgorithmParameterException
	 *             the invalid algorithm parameter exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 * @throws IllegalBlockSizeException
	 *             the illegal block size exception
	 * @throws BadPaddingException
	 *             the bad padding exception
	 */
	public static String encryptTxt(byte[] plainTxt)
			throws NoSuchPaddingException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
		cipher.init(Cipher.ENCRYPT_MODE, mKey, mAlgParm);

		byte[] encryption = cipher.doFinal(plainTxt);
		return new String(Base64.getUrlEncoder().encode(encryption), StandardCharsets.UTF_8);
	}

	/**
	 * Encrypt txt.
	 *
	 * @param plainTxt
	 *            the plain txt to be encrypted
	 * @param base64BinaryKey
	 *            the key as lexical representation of Base64 Binary
	 * @return the encrypted string
	 * @throws NoSuchPaddingException
	 *             the no such padding exception
	 * @throws InvalidAlgorithmParameterException
	 *             the invalid algorithm parameter exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 * @throws IllegalBlockSizeException
	 *             the illegal block size exception
	 * @throws BadPaddingException
	 *             the bad padding exception
	 */
	public static String encryptTxt(byte[] plainTxt, String base64BinaryKey)
			throws NoSuchPaddingException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		byte[] keyValueByte = DatatypeConverter.parseBase64Binary(base64BinaryKey);
		Key paramKey = new SecretKeySpec(keyValueByte, "AES");
		Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
		cipher.init(Cipher.ENCRYPT_MODE, paramKey, mAlgParm);

		byte[] encryption = cipher.doFinal(plainTxt);
		return new String(Base64.getMimeEncoder().encode(encryption), StandardCharsets.UTF_8);
	}

	/**
	 * Encrypt txt, no exceptions thrown
	 *
	 * @param plainTxt
	 *            the plain txt to be encrypted
	 * @return the encrypted String , or the original text if it could not be
	 *         encrypted
	 */
	public static String encryptTxtNoEx(byte[] plainTxt) {

		if (plainTxt == null || plainTxt.length == 0) {
			LOGGER.error("encryptTxtNoEx: Input param plainTxt is not valid");
			return "";
		}

		try {
			return encryptTxt(plainTxt);
		} catch (Exception e) {
			LOGGER.error("encryptTxtNoEx: Exception while decryption : " + e);
			return new String(plainTxt, StandardCharsets.UTF_8);
		}
	}

}