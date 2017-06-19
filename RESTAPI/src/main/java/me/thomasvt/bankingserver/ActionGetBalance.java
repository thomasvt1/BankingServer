package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

import me.thomasvt.bankingserver.Bank.BankObject;

class ActionGetBalance {

	JSONObject getBalance(Map<String, String> parameters) {

		return getBalance(parameters.get("user"));

		/*
		 * JSONObject response = new JSONObject();
		 * 
		 * String cardid = parameters.get("user");
		 * 
		 * int balance = (int) (new UserManagement().getBalance(cardid) * 100);
		 * 
		 * response.put("balance", balance+""); return response;
		 */
	}

	JSONObject getBalance(String cardid) {
		JSONObject response = new JSONObject();

		response.put("balance", getIntBalance(cardid) + "");
		return response;
	}

	int getIntBalance(String cardid) {
		return (int) (new UserManagement().getBalance(cardid) * 100);
	}

	JSONObject getBalance(BankObject remotebank, String pin, String carduuid) {
		int balance = new ExternalConnect().getBalance(remotebank, pin, carduuid);
		
		JSONObject response = new JSONObject();

		response.put("balance", balance);
		return response;
	}
}