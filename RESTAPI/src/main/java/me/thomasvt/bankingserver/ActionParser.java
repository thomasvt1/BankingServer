package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

class ActionParser {
	JSONObject actionParser(String query) {
		Map<String, String> parameters = new Tools().queryToMap(query);

		String action = parameters.get("action");
		String cardid = parameters.get("user");

		if (action.matches("cardCheck"))
			return new CardCheck().cardExists(parameters);

		boolean cardExists = new UserManagement().cardExists(cardid);

		if (!cardExists)
			return new JSONObject().put("error", "cardid not in database");

		int auth = new Tools().authUser(cardid, parameters.get("pin"));

		if (new UserManagement().cardBlocked(cardid))
			return new JSONObject().put("error", "card blocked");

		if (auth != 0) {
			JSONObject response = new JSONObject();

			new UserManagement().increaseTries(cardid);

			int tries = new UserManagement().getTries(cardid);

			if (tries == 3) {
				return new JSONObject().put("error", "card blocked").put("tries", 3);
			}

			response.put("tries", tries);
			response.put("error", new Tools().getErrorMessage(auth));

			return response;
		}

		new UserManagement().resetTries(cardid);

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