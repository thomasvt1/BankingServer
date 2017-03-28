package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

class ActionParser {
	JSONObject actionParser(String query) {
		Map<String, String> parameters = new Tools().queryToMap(query);
		String action = parameters.get("action");
		
		if (action.matches("getJson"))
			return getJson(parameters);
		if (action.matches("getBalance"))
			return new ActionGetBalance().getBalance(parameters);
		if (action.matches("withdrawMoney"))
			return new ActionWithdrawMoney().withdrawMoney(parameters);
		
		return new JSONObject().put("error", "invalid action");
	}
	
	private JSONObject getJson(Map<String, String> parameters) {
		return new JSONObject(parameters);
	}
}