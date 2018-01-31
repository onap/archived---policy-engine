/*-
 * ============LICENSE_START=======================================================
 * PolicyEngineUtils
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
package org.onap.policy.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;
import org.onap.policy.utils.CryptoUtils;

public class CryptoUtilsTest {

	@Test
	public final void testDecryptTxt() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		String decryptedTxt = new String(
				CryptoUtils.decryptTxt("g0uHKXCLyzJ6wSbpphNGsA=="),
				StandardCharsets.UTF_8);
		assertEquals("mypass", decryptedTxt);
	}

	@Test
	public final void testDecryptTxtWithKey() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		String decryptedTxt = new String(CryptoUtils.decryptTxt(
				"g0uHKXCLyzJ6wSbpphNGsA==", "bmpybWJrbGN4dG9wbGF3Zg=="),
				StandardCharsets.UTF_8);
		assertEquals("mypass", decryptedTxt);
	}

	@Test
	public final void testDecryptTxtNoEx() {
		String decryptedTxt = new String(
				CryptoUtils.decryptTxtNoEx("g0uHKXCLyzJ6wSbpphNGsA=="),
				StandardCharsets.UTF_8);
		assertEquals("mypass", decryptedTxt);

	}

	@Test
	public final void testDecryptTxtNoExStr() {
		assertEquals("mypass",
				CryptoUtils.decryptTxtNoExStr("g0uHKXCLyzJ6wSbpphNGsA=="));
		assertEquals("admin123",
				CryptoUtils.decryptTxtNoExStr("1uzoHuC0Ol4R7YXkPOLr-w=="));
		assertEquals("policy_user",
				CryptoUtils.decryptTxtNoExStr("Za2Xy8XOo9wn8V1EetPgoQ=="));
		assertEquals("password",
				CryptoUtils.decryptTxtNoExStr("WX_dzpHkOqKjbDz2LGnykA=="));
		assertEquals("alpha123",
				CryptoUtils.decryptTxtNoExStr("C-q5acixlXApArbP1whNAQ=="));
		assertEquals("PolicyR0ck$",
				CryptoUtils.decryptTxtNoExStr("H3q5h8tkcMvPCeUhBOYR7A=="));
		assertEquals("smil3yfc",
				CryptoUtils.decryptTxtNoExStr("wXwqcPmdC3JVfgkyOspu8w=="));
		assertEquals("alpha456",
				CryptoUtils.decryptTxtNoExStr("ZphAyQ7pPvX_BmLTmNByjA=="));
		assertEquals("smtp_password",
				CryptoUtils.decryptTxtNoExStr("kdNV89upHJmApdT0dHdxWg=="));
	}

	@Test
	public final void testDecryptTxtNoExInvalidINput() {
		assertArrayEquals(new byte[0], CryptoUtils.decryptTxtNoEx(null));
		assertArrayEquals(new byte[0], CryptoUtils.decryptTxtNoEx(""));
		// ensure backward compatibility
		assertEquals("bogus", new String(CryptoUtils.decryptTxtNoEx("bogus"),
				StandardCharsets.UTF_8));
		assertEquals("admin123", CryptoUtils.decryptTxtNoExStr("admin123"));
		assertEquals("policy_user",
				CryptoUtils.decryptTxtNoExStr("policy_user"));
		assertEquals("password", CryptoUtils.decryptTxtNoExStr("password"));
		assertEquals("alpha123", CryptoUtils.decryptTxtNoExStr("alpha123"));
		assertEquals("PolicyR0ck$",
				CryptoUtils.decryptTxtNoExStr("PolicyR0ck$"));
		assertEquals("smil3yfc", CryptoUtils.decryptTxtNoExStr("smil3yfc"));
		assertEquals("alpha456", CryptoUtils.decryptTxtNoExStr("alpha456"));
		assertEquals("smtp_password",
				CryptoUtils.decryptTxtNoExStr("smtp_password"));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testDecryptTxtInvalidInput() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		CryptoUtils.decryptTxt("bogus");
	}

	@Test
	public final void testEncryptTxt() throws InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, IllegalBlockSizeException,
			BadPaddingException {
		String txtStr = "mypass";
		byte[] txt = txtStr.getBytes(StandardCharsets.UTF_8);
		assertEquals("g0uHKXCLyzJ6wSbpphNGsA==", CryptoUtils.encryptTxt(txt));
	}

	@Test
	public final void testEncryptTxtWithKey() throws InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, IllegalBlockSizeException,
			BadPaddingException {
		String txtStr = "mypass";
		byte[] txt = txtStr.getBytes(StandardCharsets.UTF_8);
		assertEquals("g0uHKXCLyzJ6wSbpphNGsA==",
				CryptoUtils.encryptTxt(txt, "bmpybWJrbGN4dG9wbGF3Zg=="));
	}

	@Test
	public final void testEncryptTxtNoEx() {
		String txtStr = "mypass";
		byte[] txt = txtStr.getBytes(StandardCharsets.UTF_8);
		assertEquals("g0uHKXCLyzJ6wSbpphNGsA==",
				CryptoUtils.encryptTxtNoEx(txt));
	}

	@Test
	public final void testEncryptTxtNoExInvalidInput() {
		String txtStr = "";
		byte[] txt = txtStr.getBytes(StandardCharsets.UTF_8);
		assertEquals("", CryptoUtils.encryptTxtNoEx(txt));
		assertEquals("", CryptoUtils.encryptTxtNoEx(null));
	}

	@Test(expected = InvalidKeyException.class)
	public final void testEncryptTxtWithKeyInvalid()
			throws InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			IllegalBlockSizeException, BadPaddingException {
		String txtStr = "mypass";
		byte[] txt = txtStr.getBytes(StandardCharsets.UTF_8);
		CryptoUtils.encryptTxt(txt, "mykey");
	}

}