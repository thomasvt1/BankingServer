package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

import me.thomasvt.bankingserver.Bank.BankObject;

class ActionWithdrawMoney {

	public JSONObject withdrawMoney(Map<String, String> parameters) {
		String cardid = parameters.get("user");

		if (!new Tools().isDouble(parameters.get("amount")))
			return new JSONObject().put("error", "amount not a double");

		double amountToWithdraw = Double.parseDouble(parameters.get("amount"));

		return withdrawMoney(amountToWithdraw, cardid);
	}

	public JSONObject withdrawMoney(double amountToWithdraw, String cardid) {
		double balance = new UserManagement().getBalance(cardid);
		
		JSONObject error = null;

		if (balance < amountToWithdraw)
			error = new Tools().getJsonWithError(21, "not enough balance");
		else if (amountToWithdraw < 0)
			error = new Tools().getJsonWithError(99, "can not add money to account");
		else if (amountToWithdraw < 1)
			error = new Tools().getJsonWithError(99, "cannot remove too small amount");
		
		System.out.println(error);
		
		if (error != null) {
			JSONObject x = new JSONObject();
			
			x.put("success", false);

			error.put("transaction", x);
			return error;
		}

		new UserManagement().removeMoney(cardid, amountToWithdraw);

		JSONObject json = new JSONObject();
		JSONObject x = new JSONObject();
		
		x.put("success", true);

		json.put("transaction", x);

		return json;
	}

	public JSONObject withdrawMoney(BankObject remotebank, String pin, String cardid, double amount) {
		int balance = new ExternalConnect().getBalance(remotebank, pin, cardid);
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("balance: " + balance);
		
		JSONObject error = null;
		
		if (balance < amount)
			error = new Tools().getJsonWithError(21, "not enough balance");
		else if (amount < 0)
			error = new Tools().getJsonWithError(99, "can not add money to account");
		else if (amount < 1)
			error = new Tools().getJsonWithError(99, "cannot remove too small amount");
		
		if (error != null)
			return getFailedTransaction(error);
		
		int amountwithdraw = (int) amount *  -100;
		
		System.out.println("amount: " + amountwithdraw);
		
		
		boolean withdraw = new ExternalConnect().withdrawMoney(remotebank, cardid, pin, amountwithdraw);
		
		if (!withdraw) {
			error = new Tools().getJsonWithError(99, "API connect");
			return getFailedTransaction(error);
		}
		
		JSONObject json = new JSONObject();
		JSONObject x = new JSONObject();
		
		x.put("success", true);

		json.put("transaction", x);

		return json;
	}
	
	JSONObject getFailedTransaction(JSONObject error) {
		JSONObject x = new JSONObject();
		
		x.put("success", false);

		error.put("transaction", x);
		return error;
	}
}