package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

import me.thomasvt.bankingserver.Bank.BankObject;

class ActionParser {
	JSONObject actionParser(String query) {
		Map<String, String> parameters = new Tools().queryToMap(query);

		String action = parameters.get("action");
		String cardid = parameters.get("user");
		
		if (action.matches("cardCheck"))
			return new CardCheck().cardExists(parameters);
		
		BankObject remotebank = null;
		boolean cardlocal = new UserManagement().cardExists(cardid);
		
		if (!cardlocal)
			remotebank = App.getEx().isRemoteCard(cardid);
		
		if (remotebank != null) {
			// Run if the card is a remote card
			
			ExternalConnect ec = new ExternalConnect();
			
			boolean enabled = ec.cardEnabled(remotebank, cardid);
			
			if (!enabled)
				return new JSONObject().put("error", "card blocked").put("tries", 3);
			
			boolean auth = ec.validateToken(remotebank, parameters.get("pin"), cardid);
			
			if (!auth) {
				int tries = ec.getTries(remotebank, cardid);
				return new JSONObject().put("error", "card blocked").put("tries", tries);
			}
		}
		
		else {
			// Run if the card is a local card
			
			if (!cardlocal)
				return new JSONObject().put("error", "cardid not in database");
			
			int auth = new Tools().authUser(cardid, parameters.get("pin"));
			
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
		}

		/*
		boolean cardExists = new UserManagement().cardExists(cardid);

		//Check if card is blocked
		if (remotecard) {
			if (new ExternalConnect().cardEnabled(remotebank, cardid))
				return new JSONObject().put("error", "card blocked");
		} else {
			if (new UserManagement().cardBlocked(cardid))
				return new JSONObject().put("error", "card blocked");
		}
		 */
		
		if (action.matches("getBalance"))
			return new ActionGetBalance().getBalance(parameters);
		if (action.matches("withdrawMoney"))
			return new ActionWithdrawMoney().withdrawMoney(parameters);

		return new JSONObject().put("error", "invalid action");
	}
}