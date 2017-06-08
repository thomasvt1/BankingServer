package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

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
		
		if (balance < amountToWithdraw)
			return new Tools().getJsonWithError(99, "not enough balance");
		if (amountToWithdraw < 0)
			return new Tools().getJsonWithError(99, "can not add money to account");
		if (amountToWithdraw < 1)
			return new Tools().getJsonWithError(99, "cannot remove too small amount");
		
		new UserManagement().removeMoney(cardid, amountToWithdraw);
		
		int finalmoney = (int) ((balance - amountToWithdraw) * 100);
		
		JSONObject json = new JSONObject();
		
		JSONObject x = new JSONObject();
		
		x.put("id", cardid);
		x.put("balance", finalmoney + "");
		x.put("response", "success");
		
		json.put("card", x);
		
		return json;
	}
}