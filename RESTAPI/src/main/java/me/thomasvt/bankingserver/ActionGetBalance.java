package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

class ActionGetBalance {
	JSONObject getBalance(Map<String, String> parameters) {
		JSONObject response = new JSONObject();
		
		String cardid = parameters.get("user");
		
		int balance = (int) (new UserManagement().getBalance(cardid) * 100);
		
		response.put("balance", balance+"");
		return response;
	}
}