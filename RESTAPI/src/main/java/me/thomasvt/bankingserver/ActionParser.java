package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

class ActionParser {
	JSONObject actionParser(String query) {
		Map<String, String> parameters = new Tools().queryToMap(query);
		String action = parameters.get("action");
		
		int auth = new Tools().authUser(parameters);
		
		if (auth != 0) {
			JSONObject response = new JSONObject();
			
			response.put("tries", 2);
			
			return response.put("error", new Tools().getErrorMessage(auth));
			//return new JSONObject().put("error", new Tools().getErrorMessage(auth));
		}
		
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