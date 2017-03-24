package me.thomasvt.bankingserver;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class Tools {
	
	/*
	 * Returns 'OK' when user is authenticated. Else will return (Json) string why not.
	 */
	String authUserOrReturnError(Map<String, String> parameters) {
		if (parameters.get("user") == null)
			return getJsonWithError("no user provided");
		if (parameters.get("pin") == null)
			return getJsonWithError("no pin provided");
		
		String cardid = parameters.get("user");
		String pin = parameters.get("pin");
		
		if (!new UserManagement().accountExists(cardid))
			return new Tools().getJsonWithError("no user");

		boolean auth = pinCorrect(cardid, pin);

		if (auth)
			return "OK";
		else
			return new Tools().getJsonWithError("pin incorrect");
	}

	private String getSaltedPin(String cardid, String pin) {
		return new Hashing().hashString(pin);
	}

	protected boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s);
	    } catch(Exception e) { 
	        return false;
	    }
	    return true;
	}
	
	protected boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(Exception e) { 
	        return false;
	    }
	    return true;
	}
	
	private String getTimeHash() {
		LocalDateTime date = LocalDateTime.now();
		int prefix = date.getMinute() * 9 + date.getDayOfMonth() + 5 + date.getDayOfYear() * 3 * date.getMonthValue() * 9 * date.getYear();
		
		String suffix = "" + date.getMinute() + (date.getYear() * date.getDayOfMonth()) + (date.getMinute() * date.getMinute()) + date.getDayOfYear() + (date.getDayOfYear() * date.getDayOfMonth());
		
		return StringUtils.substring(prefix + suffix, 0, 16);
	}
	
	public String getJsonWithError(String error) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("error", error);
		return new JSONObject(response).toString();
	}
	
	public Map<String, String> queryToMap(String query){
	    Map<String, String> result = new HashMap<String, String>();
	    for (String param : query.split("&")) {
	        String pair[] = param.split("=");
	        if (pair.length>1) {
	            result.put(pair[0], pair[1]);
	        }else{
	            result.put(pair[0], "");
	        }
	    }
	    return result;
	}
	
	/*
	 * Returns true if pin provided is correct
	 * user: userid (String) int
	 * encryptedPin (String) encrypted/decrypted pincode (4 long)
	 */
	boolean pinCorrect(String cardid, String pin) {
		String pinHash = new UserManagement().getPinHash(cardid);
		boolean auth = false;
		if (pin.length() == 4) {
			String saltedPin = getSaltedPin(cardid, pin);
			if (saltedPin.matches(pinHash))
				auth = true;
		} else {
			String decryptedPin = new Encryption("gXbB9%kXrg6cxh#y").tryDecrypt(pin);
			String saltedPin = getSaltedPin(cardid, decryptedPin);
			if (saltedPin.matches(pinHash))
				auth = true;
		}
        return auth;
    }
	
	boolean matchRequestKey(String providedKey) {
		String keyToCheck = "LOL";
		String postedKey = StringUtils.substring(providedKey, 0, 14);
		Encryption enc = new Encryption(getTimeHash());
		
		try {
			String givenKey = postedKey;
			String genKey = enc.encrypt(keyToCheck);
			
			System.out.println(getTimeHash());
			
			System.out.println("Current key: " + genKey);
			if (genKey.contains(givenKey) && givenKey.length() < genKey.length())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}