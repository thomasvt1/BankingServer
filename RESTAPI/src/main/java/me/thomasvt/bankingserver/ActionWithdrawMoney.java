package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

class ActionWithdrawMoney {

	public JSONObject withdrawMoney(Map<String, String> parameters) {
		JSONObject response = new JSONObject();
		
		if (!new Tools().isDouble(parameters.get("amount")))
			return new JSONObject().put("error", "amount not a double");
		
		String cardid = parameters.get("user");
		double balance = new UserManagement().getBalance(cardid);
		double amountToWithdraw = Double.parseDouble(parameters.get("amount"));
		
		if (balance < amountToWithdraw)
			return new JSONObject().put("error", "not enough balance");
		if (amountToWithdraw < 0)
			return new JSONObject().put("error", "can not withdraw negative amount");
		if (amountToWithdraw < 1)
			return new JSONObject().put("error", "can not withdraw too small amount");
		
		new UserManagement().removeMoney(cardid, amountToWithdraw);
		
		response.put("balance", (balance - amountToWithdraw) + "");
		response.put("response", "success");
		
		return response;
	}
}