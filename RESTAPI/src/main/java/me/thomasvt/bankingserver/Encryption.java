package me.thomasvt.bankingserver;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryption {

	public Encryption(String keyString) {
		this.keyString = keyString;
	}

	private static final String ALGO = "AES";
	private String keyString = null;

	public String tryDecrypt(String encryptedData) {
		try {
			return decrypt(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = new Base64().encodeToString(encVal);
		return encryptedValue;
	}

	public String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new Base64().decode(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private Key generateKey() throws Exception {
		byte[] b = keyString.getBytes();
		Key key = new SecretKeySpec(b, ALGO);
		return key;
	}
}