package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

import me.thomasvt.bankingserver.Bank.BankObject;

class ActionParser {
	JSONObject actionParser(String query) {
		Map<String, String> parameters = new Tools().queryToMap(query);

		String action = parameters.get("action");
		String cardid = parameters.get("user");
		String pin = parameters.get("pin");
		
		if (action.matches("cardCheck"))
			return new CardCheck().cardExists(parameters);
		
		BankObject remotebank = null;
		boolean cardlocal = new UserManagement().cardExists(cardid);
		
		if (!cardlocal)
			remotebank = App.getEx().isRemoteCard(cardid);
		
		if (pin == null)
			return new Tools().getJsonWithError(99, "No pin provided");
		if (cardid == null)
			return new Tools().getJsonWithError(99, "No user provided");
		
		if (remotebank != null) {
			// Run if the card is a remote card
			
			ExternalConnect ec = new ExternalConnect();
			
			boolean enabled = ec.cardEnabled(remotebank, cardid);
			
			if (!enabled)
				return new JSONObject().put("error", "card blocked").put("tries", 3);
			
			String decryptedpin = new Tools().decryptPin(parameters.get("pin"));
			System.out.println("PIN: " + pin + " / " + decryptedpin);
			
			boolean auth = ec.validateToken(remotebank, decryptedpin, cardid);
			
			if (!auth) {
				int tries = ec.getTries(remotebank, cardid);
				JSONObject response = new JSONObject();
				
				response.put("tries", tries);
				response.put("error", "external card auth went wrong");
				
				return response;
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

		if (remotebank == null) {
			if (action.matches("getBalance"))
				return new ActionGetBalance().getBalance(parameters);
			if (action.matches("withdrawMoney"))
				return new ActionWithdrawMoney().withdrawMoney(parameters);
		}
		
		else {
			String decryptedpin = new Tools().decryptPin(parameters.get("pin"));
			System.out.println("PIN: " + pin + " / " + decryptedpin);
			
			if (action.matches("getBalance"))
				return new ActionGetBalance().getBalance(remotebank, decryptedpin, cardid);
			if (action.matches("withdrawMoney"))
				return new ActionWithdrawMoney().withdrawMoney(remotebank, decryptedpin, cardid);
		}

		return new JSONObject().put("error", "invalid action");
	}
}