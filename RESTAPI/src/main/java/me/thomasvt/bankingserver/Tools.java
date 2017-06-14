package me.thomasvt.bankingserver;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class Tools {
	
	private final String ENCRYPTKEY = "gXbB9%kXrg6cxh#y";

	/*
	 * Returns the auth status of the user. 0: Pin OK 1: No user provided 2: No
	 * pin provided 3: No user 4: Pin incorrect
	 */
	int authUser(String cardid, String pin) {
		if (cardid == null)
			return 1; // no user provided
		if (pin == null)
			return 2; // no pin provided

		if (!new UserManagement().cardExists(cardid))
			return 3; // no user

		boolean auth = pinCorrect(cardid, pin);

		if (auth) {
			new UserManagement().resetTries(cardid);
			return 0; // OK
		}
			
		else
			return 4; // pin incorrect
	}

	String getErrorMessage(int code) {
		String message = "";
		if (code == 0)
			message = "OK";
		else if (code == 1)
			message = "No user provided";
		else if (code == 2)
			message = "No pin provided";
		else if (code == 3)
			message = "No user";
		else if (code == 4)
			message = "Pin incorrect";
		else
			message = "no error message";

		return message;
	}

	/*
	 * Returns 'OK' when user is authenticated. Else will return (Json) string
	 * why not.
	 */
	/*
	 * JSONObject authUserOrReturnError(Map<String, String> parameters) { if
	 * (parameters.get("user") == null) return getJsonWithError(7,
	 * "no user provided"); if (parameters.get("pin") == null) return
	 * getJsonWithError(7, "no pin provided");
	 * 
	 * String cardid = parameters.get("user"); String pin =
	 * parameters.get("pin");
	 * 
	 * if (!new UserManagement().cardExists(cardid)) return new
	 * Tools().getJsonWithError(10, "no user");
	 * 
	 * boolean auth = pinCorrect(cardid, pin);
	 * 
	 * if (auth) return null; else return new Tools().getJsonWithError(99,
	 * "pin incorrect"); }
	 */
	public String getSaltedPin(User user, String carduuid, String pin) {
		int i = Integer.parseInt(pin);
		int x = user.getLastname().length();

		int y = ((x * i) + i) * x;

		String toBeHashed = "{g}{x}{y}{z}";
		toBeHashed = toBeHashed.replace("g", user.getFirstname().toUpperCase());
		toBeHashed = toBeHashed.replace("x", y + "");
		toBeHashed = toBeHashed.replace("y", user.getLastname() + user.getFirstname().toUpperCase());
		toBeHashed = toBeHashed.replace("z", y + carduuid + carduuid);

		return new Hashing().hashString(toBeHashed);
	}

	protected boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	protected boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private String getTimeHash() {
		LocalDateTime date = LocalDateTime.now();
		int prefix = date.getMinute() * 9 + date.getDayOfMonth() + 5
				+ date.getDayOfYear() * 3 * date.getMonthValue() * 9 * date.getYear();

		String suffix = "" + date.getMinute() + (date.getYear() * date.getDayOfMonth())
				+ (date.getMinute() * date.getMinute()) + date.getDayOfYear()
				+ (date.getDayOfYear() * date.getDayOfMonth());

		return StringUtils.substring(prefix + suffix, 0, 16); // TODO: 16 long
	}

	public JSONObject getJsonWithError(int i, String error) {
		JSONObject json = new JSONObject();

		JSONObject x = new JSONObject();

		x.put("id", i);
		x.put("message", error);

		json.put("error", x);

		return json;
	}

	public Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}

	/*
	 * Returns true if pin provided is correct user: userid (String) int
	 * encryptedPin (String) encrypted/decrypted pincode (4 long)
	 */
	boolean pinCorrect(String carduuid, String pin) {
		boolean auth = false;
		String pinHash = new UserManagement().getPinHash(carduuid);
		User user = new UserManagement().getUser(carduuid);
		if (pin.length() == 4) {
			String saltedPin = getSaltedPin(user, carduuid, pin);
			if (saltedPin.matches(pinHash))
				auth = true;
		} else {
			String decryptedPin = new Encryption(ENCRYPTKEY).tryDecrypt(pin);
			String saltedPin = getSaltedPin(user, carduuid, decryptedPin);
			if (saltedPin.matches(pinHash))
				auth = true;
		}
		return auth;
	}
	
	String decryptPin(String pin) {
		return new Encryption(ENCRYPTKEY).tryDecrypt(pin);
	}

	boolean matchRequestKey(String providedKey) {
		String keyToCheck = "LOL";
		String postedKey = StringUtils.substring(providedKey, 0, 14);
		Encryption enc = new Encryption(getTimeHash());

		try {
			String givenKey = postedKey;
			String genKey = enc.encrypt(keyToCheck);

			System.out.println("Current key: " + genKey);
			if (genKey.contains(givenKey) && givenKey.length() < genKey.length())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}