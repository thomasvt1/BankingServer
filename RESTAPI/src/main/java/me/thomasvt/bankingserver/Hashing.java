package me.thomasvt.bankingserver;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;

public class Hashing {

	private Size DEFAULT = Size.S512;

	protected String hashString(String string) {
		return hashString(string, DEFAULT, true);
	}

	protected String hashString(String string, Size s) {
		return hashString(string, s, true);
	}

	protected String hashString(String string, Size s, boolean bouncyencoder) {
		Size size = s == null ? DEFAULT : s;

		DigestSHA3 md = new DigestSHA3(size.getValue());
		String text = string != null ? string : "null";
		try {
			md.update(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			// most unlikely
			md.update(text.getBytes());
		}
		byte[] digest = md.digest();
		return encode(digest, bouncyencoder);
	}

	private String encode(byte[] bytes, boolean bouncyencoder) {
		if (bouncyencoder)
			return Hex.toHexString(bytes);
		else {
			BigInteger bigInt = new BigInteger(1, bytes);
			return bigInt.toString(16);
		}
	}

	protected enum Size {
		S224(224), S256(256), S384(384), S512(512);

		int bits = 0;

		Size(int bits) {
			this.bits = bits;
		}

		public int getValue() {
			return this.bits;
		}
	}
}