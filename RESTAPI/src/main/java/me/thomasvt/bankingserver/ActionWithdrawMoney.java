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

	public JSONObject withdrawMoney(String amountToWithdraw, String cardid) {

		if (amountToWithdraw == null)
			return new JSONObject().put("error", "amount not provided");

		if (!new Tools().isDouble(amountToWithdraw))
			return new JSONObject().put("error", "amount not a double");

		double x = Double.parseDouble(amountToWithdraw);

		return withdrawMoney(x, cardid);
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
		
		if (error != null) {
			JSONObject x = new JSONObject();
			
			x.put("success", false);

			error.put("transaction", x);
		}

		new UserManagement().removeMoney(cardid, amountToWithdraw);

		JSONObject json = new JSONObject();

		JSONObject x = new JSONObject();
		
		x.put("success", true);

		json.put("transaction", x);

		return json;
	}

	public JSONObject withdrawMoney(BankObject remotebank, String pin, String cardid) {
		System.out.println("TODO: Withdraw money");
		
		
		
		
		// TODO Auto-generated method stub
		return null;
	}
}