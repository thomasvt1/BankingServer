package me.thomasvt.bankingserver;

import java.util.HashMap;
import java.util.Map;

class ActionGetBalance {
	String getBalance(Map<String, String> parameters) {
		Map<String, String> response = new HashMap<String, String>();
		
		String auth = new Tools().authUserOrReturnError(parameters);
		if (!auth.matches("OK"))
			return auth;
		
		String cardid = parameters.get("user");
		
		int balance = (int) (new UserManagement().getBalance(cardid) * 100);
		
		response.put("balance", balance+"");
		return response.toString();
	}
}