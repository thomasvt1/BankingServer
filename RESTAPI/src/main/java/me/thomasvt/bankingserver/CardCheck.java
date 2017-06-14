package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

import me.thomasvt.bankingserver.Bank.BankObject;

public class CardCheck {
	
	JSONObject cardExists(Map<String, String> parameters) {
		JSONObject response = new JSONObject();

		String cardid = parameters.get("user");
		
		if (cardid == null) {
			response.put("exists", false);
			return response;
		}
		
		boolean exists = false;
		int tries = 0;
		
		if (new UserManagement().cardExists(cardid)) {
			//The card does exist locally
			exists = true;
			tries = new UserManagement().getTries(cardid);
		}
		
		else {
			//The card doesn't exist locally
			BankObject bank = App.getEx().isRemoteCard(cardid);
			if (bank != null) {
				//The card does exist in the Maaslandje network
				
				exists = true;
				tries = new ExternalConnect().getTries(bank, cardid);
			}
		}

		response.put("exists", exists);
		
		if (exists)
			response.put("tries", tries);
		return response;
	}
}